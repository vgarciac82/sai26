package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.syc.crud.dsmngr.DataSourceManager;

import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.reportes.core.ReporteBalanzaManager;

public class ReportBalanzaBusinessLogic extends DataSourceManager  {
	
	public ReportBalanzaBusinessLogic(String jniName) {
		super.init(jniName);
	}
	
	public void generaExcel( HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas ) throws Exception {
	Connection conn = null;
	String file = null;
	try {
		conn = getConnection();

		String ejercicio = req.getParameter("aEjercicioFiscal");
		String cc = req.getParameter( "cCentroContable" );
		String mes = req.getParameter( "mesInicio" );
		String tipoReporte = req.getParameter( "tipoReporte" );
		
		file = ReporteBalanzaManager.generaReporteBalanzaManager( conn, ejercicio,  cc, mes, tipoReporte,  plantillas);

		File f = new File (file);
		resp.setContentType( "application/octet-stream" );
		resp.addHeader( "Content-Disposition", "inline; filename=\"" + f.getName() + "\"; " );

		ServletOutputStream out = resp.getOutputStream();

		Util.doDownload( out, file, file, "" );
		
		conn.commit();
		out.flush();
		out.close();

	} finally {
		CloseObject.closeObject( conn, false );
		if ( file != null ) {
			File f = new File( file );
			if ( !f.delete() )
				f.deleteOnExit();
		}
	}
}

}
