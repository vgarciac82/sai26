package com.syc.obrapublica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.contable.AccountingEngine;
import com.syc.contable.AccountingEngineException;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.CasoOperacionManager;
import com.syc.utils.ModuleProperties;

public class AutoCancelacionApartadoManager {

	private static final Logger	log					= Logger.getLogger(AutoCancelacionApartadoManager.class);
	private static final String	ENCABEZADO_CORREO	= "<b>Atenci&oacute;n</b><br><br>El sistema ha detectado los siguientes contratos con recursos apartados autorizados que han rebasado la fecha l&iacute;mite de tolerancia. <br><br>Se hace este hecho de su conocimiento para que tome las acciones pertinentes. <br><br>Listado de contratos:";
	private static final String	FIRMA_CORREO		= "<br><br>Atentamente Sistema de Administracion Integral SAI. M&oacute;dulo de Obra P&uacute;blica";

	public static String revisaTiempoLimite(Connection conn) throws AccountingEngineException {
		ModuleProperties moduleProperties = null;
		StringBuffer msg = new StringBuffer();
		try {
			moduleProperties = new ModuleProperties("mop");
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		}
		String sqlSelect = " SELECT " + "p.foliosai, p.nFolioOPAHeader, " + "CASE " + " WHEN p.ccvecontrato IS NULL " + "	   OR p.ccvecontrato = '' THEN 'No Capturado' " + " ELSE p.ccvecontrato " + "END                                                       AS ccvecontrato " + ", "
			+ "CONVERT(VARCHAR, p.faplicacion, 103)                      AS " + "faplicacion, " + "'$' " + "+ CONVERT( VARCHAR, ( CONVERT( MONEY, p.nmontoconiva) ) ) AS nmontoconiva " + ", " + "p.u_login, " + "p.diasapartado" + " FROM   vobra_publica_apartado_pendiente p "
			+ " WHERE  CONVERT(DATE, faplicacion, 103) < CONVERT(DATE, Dateadd(dd, ?, Getdate()), 103  )";
		String maximoDiasStr = moduleProperties.getProperty("max.dias.apartado");
		if (maximoDiasStr == null || "".equals(maximoDiasStr))
			throw new AccountingEngineException("No se definio la propiedad \"mop.max.dias.apartado\"");

		int maximoDias = Integer.parseInt(maximoDiasStr);

		PreparedStatement psSelect = null;
		ResultSet rs = null;
		List<String> r = null;
		List<String> rFolioSAI = null;
		AccountingEngine accEng = new AccountingEngine();
		accEng.setValidaInsuficienciaDeSaldo(false);

		try {
			psSelect = conn.prepareStatement(sqlSelect);
			psSelect.setInt(1, -1 * maximoDias);
			rs = psSelect.executeQuery();

			while (rs.next()) {
				if (r == null) {
					r = new ArrayList<String>();
					rFolioSAI = new ArrayList<String>();
				}
				msg.append("<br><br><b>Contrato:</b>" + rs.getString("ccvecontrato"));
				msg.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Folio SAI:</b>&nbsp;" + rs.getString("foliosai"));
				msg.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Monto Apartado:</b>&nbsp;" + rs.getString("nmontoconiva"));
				msg.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Autorizacion apartado:</b>&nbsp;" + rs.getString("faplicacion"));
				msg.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Usuario Responsable:</b>&nbsp;" + rs.getString("u_login"));
				msg.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Dias Apartado:</b>&nbsp;" + rs.getString("diasapartado"));
				r.add(rs.getString("nFolioOPAHeader"));
				rFolioSAI.add(rs.getString("foliosai"));
			}

			if (r != null && r.size() > 0) {
				msg.append("<br><br>Se cancelaron <b>" + r.size() + "</b> documentos con mas de <b>" + maximoDias + "</b> dias de antiguedad");
				int j = 0;
				for (Iterator<String> i = r.iterator(); i.hasNext();) {
					String sFolioCancel = i.next();
					log.info("Cancelando folio: " + sFolioCancel);

					accEng.cancelAccountingApplication(conn, "APARTADO_OPC", sFolioCancel, "tObraPublicaApartadoEncabezado", "tObraPublicaApartadoDetalle", "nFolioOPAHeader");

					Caso c = new Caso();
					c.setFolio(rFolioSAI.get(j));

					c = CasoManager.select(conn, c);
					CasoOperacion co = ((CasoOperacion) c.getCasoOperacion().get(0));
					co.setIdOperacion(3);
					co.setResponsable("CONSULTA_OBRA");
					CasoOperacionManager.update(conn, co);
					j++;
				}
			} else
				log.info("No existen documentos a cancelar");
			return msg.toString().equals("") ? "" : ENCABEZADO_CORREO + msg.toString() + FIRMA_CORREO;
		} catch (Exception e) {
			throw new AccountingEngineException(e);
		} finally {
			if (psSelect != null)
				try {
					psSelect.close();
				} catch (Exception e) {
					log.warn("No se pudo cerrar el statement " + e, e);
				}
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					log.warn("No se pudo cerrar el statement " + e, e);
				}
			psSelect = null;
			rs = null;
		}
	}

}
