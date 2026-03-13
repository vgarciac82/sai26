package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReportesINAIManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ReportesINAIBusinessLogic extends DataSourceManager {
	private static Logger log = Logger.getLogger(ReportesINAIBusinessLogic.class);
	public ReportesINAIBusinessLogic(String jniName) {
		super.init(jniName);
	}
	
	public void generaReportesINAI(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas, int tipoReporte,ServletContext context) throws Exception {
		Connection conn = null;
		String file = null;
		ReportesINAIManager manager=new ReportesINAIManager();
		ServletOutputStream out=null;
		String mimetype = null;
		File f=null;
		try {
			conn = getConnection();
			switch (tipoReporte){
				case 1://Contratistas y Proveedores
					file=manager.generaReporteProveedores(conn, plantillas);
					
					break;
				case 2://Adjudicacion Directa
					file=manager.generaReporteADJ(conn, plantillas);
					break;
				case 3://
					file=manager.generaReporteLicitacionesInvitaciones(conn, plantillas);
					break;
				case 4://
					file=manager.generaReporteV2(conn, plantillas,tipoReporte);
					break;
				case 5://
					file=manager.generaReporteAnexo3(conn, plantillas);
					break;
				case 6://
					file=manager.generaReporteFormato7(conn, plantillas,tipoReporte);
					break;
				case 7://Matriz de contratos Backup
					file=manager.generaReporteV2(conn, plantillas,tipoReporte);
					break;
				case 8://
					file=manager.generaReporteFormato14(conn, plantillas,tipoReporte);
					break;
				case 9://
					file=manager.generaReporteFormato7(conn, plantillas,tipoReporte);
					break;
				case 10://
					file=manager.generaReporteFormato14(conn, plantillas,tipoReporte);
					break;
				default:
					log.warn("Tipo de reporte desconocido");
					return;
			}
			f = new File(file);
			mimetype = context.getMimeType(f.getName());
			resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
			
			out = resp.getOutputStream();

			Util.doDownload(out, file, file, "");
			
			conn.commit();
			out.flush();
		}catch (SQLException e) {
			if(conn!=null) {
				conn.rollback();
			}
			throw (e);
		} finally {
			CloseObject.closeObject(conn, false);
			if (file != null) {
				if (null!=f&&!f.delete())
					f.deleteOnExit();
			}
			if(out!=null){
				out.close();
			}
			manager=null;
			out=null;
		}

	}
}
