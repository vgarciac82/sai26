package com.syc.contable.servlet;

import java.sql.Connection;
import java.util.List;

import com.syc.cfdi.utils.CloseObject;
import com.syc.contable.adecuaciones.core.Fap01;
import com.syc.contable.adecuaciones.core.IadeManager;
import com.syc.crud.dsmngr.DataSourceManager;

public class IADEBusinessLogic extends DataSourceManager {

	/**
	 * 
	 * @param jniName
	 */
	public IADEBusinessLogic(String jniName) {
		super.init(jniName);
	}

	/**
	 * 
	 * @param nFolioIADE
	 * @return
	 * @throws Exception
	 */
	public List<Fap01> cargaFAP01(int nFolioIADE) throws Exception {
		Connection conn = null;
		List<Fap01> r = null;
		try {
			conn = getConnection();
			r = IadeManager.cargaFAP01(conn, nFolioIADE);
			return r;
		} finally {
			CloseObject.closeObject(conn, false);
		}
	}
}
