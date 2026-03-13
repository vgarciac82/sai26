package com.syc.contable.core;

import java.sql.Connection;

import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;

public class CatalogoURFIELBusinessLogic extends DataSourceManager {

	public CatalogoURFIELBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public boolean permitePagoSinFiel(String ur) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			return CatalogoURFIELManager.permitePagoSinFiel(conn, ur);
		} finally {
			CloseObject.closeObject(conn);
		}
	}
}
