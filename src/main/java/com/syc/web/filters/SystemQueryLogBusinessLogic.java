package com.syc.web.filters;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class SystemQueryLogBusinessLogic extends DataSourceManager {

	private static final Logger	log	= Logger.getLogger(SystemQueryLogBusinessLogic.class);

	public SystemQueryLogBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public synchronized boolean insertLog(HttpServletRequest request) {
		boolean correcto = false;
		Connection conn = null;

		try {
			conn = getConnection();
			SystemQueryLog sql = SystemQueryLog.instanceFromRequest(request);
			SystemQueryLogManager.insertLog(conn, sql);
			conn.commit();
			correcto = true;
		} catch (Exception e) {
			log.error(e,e);
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn("Problemas en rollback: " + e2);
				}
		} finally {
			CloseObject.closeObject(conn);
		}
		return correcto;
	}

}
