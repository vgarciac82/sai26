package com.syc.ejercido.pagado;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.syc.cfdi.utils.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;

public class CLCAttachmentLogBussinesLogic extends DataSourceManager {

	private static final Logger	log	= Logger.getLogger(CLCAttachmentLogBussinesLogic.class);

	public CLCAttachmentLogBussinesLogic(String jniName) {
		super.init(jniName);
	}

	public int insertaLog(String nombreArchivo, int clc, String caNoContraRecibo, int folioPago, String tipoPago, char estatusPago, String logOperacion) throws Exception {
		Connection conn = null;
		int insertados = 0;
		try {
			
			conn = getConnection();
			CLCAttachmentLogManager.insertLog(conn, nombreArchivo, clc, caNoContraRecibo, folioPago, tipoPago, estatusPago, logOperacion);
			conn.commit();

		} catch (Exception e) {
			log.error(e, e);
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn("problemas realizando Rollback " + e2, e2);
				}
		} finally {
			CloseObject.closeObject(conn, false);
		}
		return insertados;
	}
}
