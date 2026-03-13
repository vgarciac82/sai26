package com.syc.ejercido.pagado;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

import com.syc.cfdi.utils.CloseObject;

public class LogCancelaDevengadoManager {

	private static Logger	log	= Logger.getLogger(LogCancelaDevengadoManager.class);

	public static int registraLog(Connection conn, String tipoPago, int folioPago, String uLogin) throws Exception {

		log.info(String.format("Registrando cancelacion de devengado en bitacora. Pago[%S] Folio[%d] Cancelo[%S]", tipoPago, folioPago, uLogin));

		String query = "INSERT INTO tLogDevengadoCancelado( cTipoPago, nFolioPago, uLogin, fCancelacion ) VALUES(?,?,?,GETDATE() ) ";
		PreparedStatement psInsert = null;
		int afectados = 0;

		try {

			psInsert = conn.prepareStatement(query);
			psInsert.setString(1, tipoPago);
			psInsert.setInt(2, folioPago);
			psInsert.setString(3, uLogin);

			afectados = psInsert.executeUpdate();
			log.info(String.format("Registro terminado exitosamente, se afecto %d registro", afectados));

			return afectados;

		} finally {
			CloseObject.closeObject(psInsert, false);

		}
	}
	
	public static int BitacoraLog(Connection conn, String tipoPago, int folioPago, String uLogin) throws Exception {

		log.info(String.format("Registrando cancelacion de documentos en bitacora. Pago[%S] Folio[%d] Cancelo[%S]", tipoPago, folioPago, uLogin));

		String query = "INSERT INTO tLogCancelaDocumento( cTipoPago, nFolioPago, uLogin, fCancelacion ) VALUES(?,?,?,GETDATE() ) ";
		PreparedStatement psInsert = null;
		int afectados = 0;

		try {
			psInsert = conn.prepareStatement(query);
			psInsert.setString(1, tipoPago);
			psInsert.setInt(2, folioPago);
			psInsert.setString(3, uLogin);

			afectados = psInsert.executeUpdate();
			log.info(String.format("Registro terminado exitosamente, se afecto %d registro", afectados));

			return afectados;

		} finally {
			CloseObject.closeObject(psInsert, false);

		}
	}
}
