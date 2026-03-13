package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.reportes.core.ReimpresionFormatosManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ReimpresionFormatosBusinessLogic extends DataSourceManager {

	public ReimpresionFormatosBusinessLogic(String jniName) {	
		super.init(jniName);
	}
	
	Logger log = Logger.getLogger(ReportesGreenMexBusinessLogic.class);
		
	public void reimpresionPagos(HttpServletRequest req, HttpServletResponse resp, String tipoReporte, String ruta, String tipo) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();

			Map<String, Object> parms = new LinkedHashMap<String, Object>();

			String cxp = req.getParameter("cxp");			
			
			if ( tipo.equals( "RG" ) ) {
				parms.put("whereFolio", " and CR.canocontrarrecibo ='" + cxp + "'");
			} else if ( tipo.equals( "FE" ) || tipo.equals( "DV" )  || tipo.equals( "RGOC" ) || tipo.equals( "OB" ) || tipo.equals( "DR" ) || tipo.equals( "OA" ) || tipo.equals( "PN" )) {
				parms.put("whereFolio", " CR.canocontrarrecibo ='" + cxp + "'");
			}else if ( tipo.equals( "IC" ) ) {
				parms.put("whereFolio", cxp );
				parms.put("SUBREPORT_DIR", ruta + File.separator);
			}
			ReimpresionFormatosManager.ReimpresionPagos(conn, resp, tipoReporte, ruta, parms);				

		} finally {
			CloseObject.closeObject(conn, false);
		}
	}
		
}
