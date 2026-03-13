package com.syc.contable;

import java.sql.Connection;

import com.syc.contable.core.PasivoDiferidoOperAjenaManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class PasivoDiferidoOperAjenaBussinessLogic extends DataSourceManager {

	public PasivoDiferidoOperAjenaBussinessLogic(String jniName) {

		super.init(jniName);
	}
	
	public String buscaSolicitudesJSON(String caNoContrarrecibo, String tipoRetencion) throws Exception {

		Connection conn = null;
		String mensaje = null;
		
		try {
			conn = getConnection();
			mensaje = PasivoDiferidoOperAjenaManager.buscaSolicitudes(conn, caNoContrarrecibo, tipoRetencion);						
		} finally {
			CloseObject.closeObject(conn);
		}
	
		return mensaje;
		
	}

}
