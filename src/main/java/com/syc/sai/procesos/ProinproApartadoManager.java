package com.syc.sai.procesos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.contable.AccountingEngine;
import com.syc.contable.AccountingEngineException;

public class ProinproApartadoManager {

	private static final Logger log = Logger
			.getLogger(ProinproApartadoManager.class);

	public static void revisaTiempoLimite(Connection conn, long maximoDias)
			throws AccountingEngineException {

		String sqlSelect = "update tAutoCancelaBitacora set fEjecucion = GETDATE() where tipoOperacion = 'PROINPRO' ";
			sqlSelect += " SELECT	nFolioDocOli "
				+ "  FROM	tDocProinproEncabezado p "
				+ " WHERE	CONVERT( DATE, fAplicacion, 103) < CONVERT( date,  DATEADD(DD,-"
				+ maximoDias + ",GETDATE()), 103) "
				+ "   AND	cDocumentoHaplicado = 'S' "
				+ "   AND	cDocumentoEstatus = 'A' ";
		String sqlUpdate = "UPDATE tDocProinproEncabezado SET cDocumentoEstatus = 'V' WHERE nFolioDocOli = ?";

		PreparedStatement psSelect = null, psUpdate = null;
		ResultSet rs = null;
		List<Integer> r = null;
		AccountingEngine accEng = new AccountingEngine();
		accEng.setValidaInsuficienciaDeSaldo(false);

		try {
			psSelect = conn.prepareStatement(sqlSelect);
			psUpdate = conn.prepareStatement(sqlUpdate);

			rs = psSelect.executeQuery();

			while (rs.next()) {
				if (r == null)
					r = new ArrayList<Integer>();

				r.add(rs.getInt("nFolioDocOli"));
			}

			if (r != null && r.size() > 0) {
				log.info("Se cancelaran " + r.size()
						+ " documentos con mas de " + maximoDias
						+ " dias de antiguedad");
				for (Iterator<Integer> i = r.iterator(); i.hasNext();) {
					int nFolioOLI = i.next();
					log.info("Cancelando folio: " + nFolioOLI);
					accEng.cancelAccountingApplication(conn, "PROINPRO",
							String.valueOf(nFolioOLI),
							"tdocproinproencabezado", "tdocproinprodetalle",
							"nFolioDocOli");
					psUpdate.setInt(1, nFolioOLI);
					psUpdate.executeUpdate();
				}
			} else
				log.info("No existen documentos a cancelar");
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
