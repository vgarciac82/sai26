package com.syc.contable.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.axtel.egresos.exceptions.EgresoException;
import com.syc.cfdi.db.CloseObject;
import com.syc.ejercido.pagado.core.EgresoCalendario;
import com.syc.gestion.util.Util;

public class SaldoManager {

	private static final Logger log = Logger.getLogger(SaldoManager.class);
	private static final BigDecimal TOLERANCIA_MINIMA = new BigDecimal(1.00);

	/**
	 * Calendariza en base al presupuesto las retenciones. Como retenciones
	 * entendemos deducciones al pago, que seran pagados a TESOFE o a la SHCP,
	 * sean estas retenciones o penas convencionales.
	 * 
	 * @param calendarioPago Montos calendarizados del pago
	 * @param montoRetencion Mont de la retencion a calendarizar
	 * @return Listado con el saldo que se tomara por mes.
	 * @throws Exception si no se alcanza a cubrir el monto de la retencion con
	 * todos los montos.
	 */
	public static List<SaldoMensual> calendarioSaldoRetencion(List<EgresoCalendario> calendarioPago,
			BigDecimal montoRetencion) throws EgresoException {

		List<SaldoMensual> saldos = new ArrayList<SaldoMensual>();

		for (EgresoCalendario mesCalendario : calendarioPago)

			/*
			 * Si se cubre el monto con el saldo en el mes y queda saldo se toma
			 * todo y termina el ciclo
			 */
			if (mesCalendario.getImporteBruto().compareTo(montoRetencion) > 0) {
				log.debug("El saldo en el mes permite calendarizar todo el importe");
				BigDecimal restante = mesCalendario.getImporteBruto().subtract(montoRetencion);
				mesCalendario.setImporteBruto(restante);
				SaldoMensual saldoMensual = new SaldoMensual(mesCalendario.getEp(), mesCalendario.getMesPresupuesto(),
						montoRetencion, mesCalendario.getIdTipoConcepto(), mesCalendario.getIdTipoMovimiento());
				log.info("Se genero saldo mensual " + saldoMensual + "");
				saldos.add(saldoMensual);
				montoRetencion = new BigDecimal(0.00d);
				break;
			} else {
				/*
				 * Si no se cubre el monto con el saldo en el mes se toma el
				 * saldo del mes menos la tolerancia y se actualizan saldos.
				 */

				BigDecimal montoEjercerMes = mesCalendario.getImporteBruto().subtract(TOLERANCIA_MINIMA).setScale( 2, RoundingMode.HALF_UP );
				BigDecimal saldoMes = mesCalendario.getImporteBruto().subtract(montoEjercerMes);

				saldos.add(new SaldoMensual(mesCalendario.getEp(), mesCalendario.getMesPresupuesto(), montoEjercerMes,
						mesCalendario.getIdTipoConcepto(), mesCalendario.getIdTipoMovimiento()));
				montoRetencion = montoRetencion.subtract(montoEjercerMes);
				mesCalendario.setImporteBruto(saldoMes);

			}

		/*
		 * Al salir del ciclo el montoRetencion deberia ser 0, en caso contrario
		 * no alcanzo el monto calendarizado.
		 */
		if (montoRetencion.compareTo(Util.ZERO) != 0)
			throw new EgresoException("No se encontro saldo suficiente para calendarizar. Faltante: "
					+ Util.formatNumber(montoRetencion));

		return saldos;
	}

	public static List<SaldoMensual> generaCalendarioVacio(String cuenta) {
		List<SaldoMensual> calendario = new ArrayList<SaldoMensual>();
		for (int i = 0; i < 12; i++)
			calendario.add(i, new SaldoMensual(cuenta, new BigDecimal(0.0d), i + 1));
		return calendario;
	}

	public static Map<Integer, SaldoMensual> getSaldoCompromiso(Connection conn, Map<Integer, SaldoMensual> saldos,
		String numeroContrato, String tipoPago, int folioPago, String ep) throws Exception {
		String aliasTablaEnc = tipoPago + "Encabezado";
		String aliasTablaDet = tipoPago + "Detalle";
		String tablaEnc = "t" + tipoPago + "Encabezado";
		String tablaDet = "t" + tipoPago + "Detalle";
		String ctipopago = tipoPago;
		
		if ("PAGOFEDERALIZADO".equals(tipoPago)) {
			ctipopago = "ContratoObra";
		}
		
		String idContratoColumn = "cFolio" + ctipopago;
		
		if( "PAGODIRECTO".equalsIgnoreCase( tipoPago ) )
			idContratoColumn = "cIdContrato";
				

		StringBuilder query = new StringBuilder();
		query.append("SELECT saldos.idContrato, ");
		query.append("       saldos.ep, ");
		query.append("       saldos.mes, ");
		query.append("       Sum(saldos.saldo) AS saldo ");
		query.append("FROM   (SELECT " + aliasTablaEnc + "." + idContratoColumn + " AS idContrato, ");
		query.append("               " + aliasTablaDet + "." + "EP, ");
		query.append("               Cast(" + aliasTablaDet + "." + "cMes AS INT)    AS mes, ");
		query.append("               -Sum(" + aliasTablaDet + "." + "mImporteMasIva) saldo ");
		query.append("        FROM   " + tablaEnc + " " + aliasTablaEnc + " ");
		query.append("               INNER JOIN " + tablaDet + " " + aliasTablaDet + " ");
		query.append("                       ON " + aliasTablaEnc + "." + "nFolio" + tipoPago + " = ");
		query.append("                          " + aliasTablaDet + "." + "nFolio" + tipoPago + " ");
		query.append("        WHERE  Isnull(" + aliasTablaEnc + "." + "cDocumentoHaplicado, 'S') = 'S' ");
		query.append("               AND " + aliasTablaEnc + "." + idContratoColumn + " = ? ");
		query.append("               AND " + aliasTablaDet + "." + "EP = ?");
		query.append("        GROUP  BY " + aliasTablaEnc + "." + idContratoColumn + ", ");
		query.append("                  " + aliasTablaDet + "." + "EP, ");
		query.append("                  " + aliasTablaDet + "." + "cMes ");
		query.append("        UNION ALL ");
		query.append("        SELECT compromisoEncabezado.cIdContrato    AS idContrato, ");
		query.append("               compromisoDetalle.EP, ");
		query.append("               Cast(compromisoDetalle.cMes AS INT) AS mes, ");
		query.append("               Sum(compromisoDetalle.mImporte)     saldo ");
		query.append("        FROM   tcompromisoencabezado compromisoEncabezado ");
		query.append("               INNER JOIN tcompromisodetalle compromisoDetalle ");
		query.append("                       ON compromisoEncabezado.nFolioCompromiso = ");
		query.append("                          compromisoDetalle.nFolioCompromiso ");
		query.append("        WHERE  compromisoEncabezado.cIdContrato = ? ");
		query.append("               AND compromisoDetalle.EP = ?");
		query.append("                AND compromisoEncabezado.cDocumentoHaplicado = 'S'");
		query.append("        GROUP  BY compromisoEncabezado.cIdContrato, ");
		query.append("                  compromisoDetalle.EP, ");
		query.append("                  compromisoDetalle.cMes ");
		query.append("        UNION ALL ");
		query.append("        SELECT " + aliasTablaEnc + "." + idContratoColumn + "  AS idContrato, ");
		query.append("               reintDet.EP, ");
		query.append("               Cast(reintDet.cMes AS INT) AS mes, ");
		query.append("               reintDet.mImporte  AS saldo");
		query.append("        FROM   treintegroautencabezado reintEnc ");
		query.append("               INNER JOIN treintegroautdetalle reintDet ");
		query.append("                       ON reintEnc.nFolioReintegroaut = ");
		query.append("                          reintDet.nFolioReintegroaut ");
		query.append("               INNER JOIN " + tablaEnc + " " + aliasTablaEnc + " ");
		query.append("                       ON " + aliasTablaEnc + "." + "caNoContrarrecibo = reintDet.cxp ");
		query.append("        WHERE  reintEnc.cDocumentoHaplicado = 'S' ");
		query.append("               AND " + aliasTablaEnc + "." + idContratoColumn + " = ? ");
		query.append("               AND reintDet.EP = ?");
		query.append("        UNION ALL ");
		query.append("SELECT " + aliasTablaEnc + "." + idContratoColumn + " AS idContrato, ");
		query.append("		   d.ep, ");
		query.append("		   d.cmes AS mes, ");
		query.append("		   -SUM(d.mimporte) AS saldo ");
		query.append("	FROM   tdisminuciondevencabezado e WITH(nolock) ");
		query.append("		   INNER JOIN tdisminuciondevdetalle d WITH(nolock) ");
		query.append("				   ON e.nfoliodisminuciondev = d.nfoliodisminuciondev ");
		query.append("         INNER JOIN " + tablaEnc + " " + aliasTablaEnc + " ");
		query.append("				   ON e.canocontrarreciboref = " + aliasTablaEnc + "." + "caNoContrarrecibo " );
		query.append("	WHERE " + aliasTablaEnc + "." + idContratoColumn + " = ? ");
		query.append("        AND d.ep = ? ");
		query.append("	GROUP BY " +  aliasTablaEnc + "." + idContratoColumn + ", d.ep, d.cmes ");
		query.append(")       AS saldos ");
		query.append("GROUP  BY saldos.idContrato, ");
		query.append("          saldos.ep, ");
		query.append("          saldos.mes ");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			log.trace("Query Generado: " + query.toString());

			ps = conn.prepareStatement(query.toString());
			ps.setString(1, numeroContrato);
			ps.setString(2, ep);
			ps.setString(3, numeroContrato);
			ps.setString(4, ep);
			ps.setString(5, numeroContrato);
			ps.setString(6, ep);
			ps.setString(7, numeroContrato);
			ps.setString(8, ep);
			
			rs = ps.executeQuery();

			while (rs.next()) {
				int mes = rs.getInt("mes");
				saldos.get(mes).setMontoSaldo(rs.getBigDecimal("saldo"));
			}
			return saldos;
		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}
	}

	public static Map<Integer, SaldoMensual> getSaldoDisponible(Connection conn, Map<Integer, SaldoMensual> saldos,
			String tipoPago, int folioPago, String ep) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append("SELECT	cSubCuenta AS ep,");
		query.append("     	CAST( SUBSTRING(nCuenta,7,5) AS INT) AS mes  ,");
		query.append("     	mSaldoArrastre AS saldo");
		query.append("  FROM	tSaldos saldos WITH(NOLOCK)");
		query.append(" WHERE	nCuenta LIKE '82106-%'");
		query.append("   AND	cSubCuenta = ?");
		query.append("   AND	mSaldoArrastre > 0");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			log.trace("Query Generado: " + query.toString());

			ps = conn.prepareStatement(query.toString());
			ps.setString(1, ep);

			rs = ps.executeQuery();

			while (rs.next()) {
				int mes = rs.getInt("mes");
				saldos.get(mes).setMontoSaldo(rs.getBigDecimal("saldo"));
			}

			return saldos;

		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}
	}

	public static Map<Integer, SaldoMensual> iniciaSaldos(int cuenta) {
		Map<Integer, SaldoMensual> saldosMap = new LinkedHashMap<Integer, SaldoMensual>();
		for (int i = 1; i <= 12; i++) {
			saldosMap.put(new Integer(i), new SaldoMensual(String.valueOf(cuenta), new BigDecimal(0.00f), i));
		}
		return saldosMap;
	}

	public static List<SaldoMensual> obtenSaldoMensual(Connection conn, String cuenta, String ep) throws Exception {

		List<SaldoMensual> calendario = generaCalendarioVacio(cuenta);
		String query = "SELECT nCuenta, ";
		query += "       cSubCuenta, ";
		query += "       mSaldoArrastre ";
		query += "FROM   tsaldos ";
		query += "WHERE  nCuenta LIKE ?";
		query += "'%' ";
		query += "       AND cSubCuenta = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, cuenta);
			ps.setString(2, ep);

			rs = ps.executeQuery();

			while (rs.next()) {
				String cuentaSaldo = rs.getString("nCuenta").substring(0, 5);
				int mes = Integer.parseInt(rs.getString("nCuenta").substring(6, 11));
				BigDecimal montoMes = rs.getBigDecimal("mSaldoArrastre");
				log.debug("Cuenta: " + cuentaSaldo + " Mes: " + mes + " Monto: " + Util.formatNumber(montoMes));
				calendario.set(mes - 1, new SaldoMensual(cuentaSaldo, montoMes, mes));
			}

		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}

		return calendario;

	}

}
