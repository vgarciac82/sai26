package com.syc.gestion.servlet;

import javax.servlet.http.HttpServlet;
/*
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
*/
public class GestionReportesPDF extends HttpServlet implements GestionInterface {
	/*private DataSource ds = null;

	public static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(GestionServlet.class);

	private String jniName = null;
	private Usuario u;
	
	public void init() {
		Context initContext;
		jniName = "jdbc/gestion";
		try {
			initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup(jniName);
		} catch (NamingException ne) {
			try {
				initContext = new InitialContext();
				Context envContext = (Context) initContext
						.lookup("java:comp/env");
				ds = (DataSource) envContext.lookup(jniName);
			} catch (NamingException nexc) {
				try {
					initContext = new InitialContext();
					ds = (DataSource) initContext.lookup(jniName);
				} catch (NamingException exc) {
					ne.printStackTrace();
					exc.printStackTrace();
					throw new RuntimeException("No se encontro la fuente '"
							+ jniName + "'");
				}
			}
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		HttpSession session = req.getSession(false);
		if (session == null) {
			log.warn("No hay sesion");
			resp.sendRedirect("../index.jsp");	
			return;
		}
		
	
		u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null) {
			log.warn("No hay Usuario en sesion");
			session.invalidate();
			resp.sendRedirect("../index.jsp");
			return;
		}

		String titulo_reporte = req.getParameter("titulo_reporte");
		String tipo_reporte = req.getParameter("tipo_reporte");
		String area_persona = req.getParameter("area_persona");
		
		if(titulo_reporte==null || area_persona==null || tipo_reporte==null
				|| "".equals(titulo_reporte) || "".equals(area_persona) || "".equals(tipo_reporte) ){
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parametros (titulo_reporte,area_persona,tipo_reporte)");
			return;
		}
		
		Connection conn = null;
		try {

			conn = ds.getConnection();
			
			//primero vemos si hay datos
			String sSelect = "select LABEL as nombre, VALOR as valor FROM VCG_REPORTES "
				+ " WHERE TIPO = ? "
				+ " AND AREA_PERSONA = ?";
			PreparedStatement prep = conn.prepareStatement(sSelect);
			prep.setString(1, tipo_reporte);
			prep.setString(2, area_persona);
			ResultSet rs = prep.executeQuery();
			if(rs==null || !rs.next()){
				resp.sendRedirect("admin/reportespdf.jsp?noDatos=true");
			}
			else{

				System.out.println("Generando Reportes...");
				
				String fileName = (""+ new java.util.Date().getTime());
				fileName = fileName.substring(fileName.length()-6)+".pdf";
				fileName = getServletContext().getRealPath("/upload")+System.getProperty("file.separator")+fileName;
			
				
				Map params = new HashMap();
				
				
				
				params.put("REPORT_TITULO", titulo_reporte);
				params.put("REPORT_AP", area_persona);
				params.put("REPORT_TIPO",tipo_reporte);
				
			
				
				JasperPrint jasperprint = JasperFillManager
				.fillReport(
						getServletContext().getRealPath("/Reportes")+System.getProperty("file.separator")+"ReportesTelecomm.jasper",
						params, conn);
	
				JasperExportManager.exportReportToPdfFile(jasperprint,fileName);
				
				doDownload(resp,fileName,fileName);
				
				new File(fileName).delete();
			}
		} catch (JRException exc) {
			exc.printStackTrace();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		finally{
			try{
				if(conn!=null)
					conn.close();
			}
			catch(Exception exc){}
		}
	}
	
	private void doDownload(HttpServletResponse resp, String filename, String original_filename) throws IOException {
		File f = new File(filename);
		int length = 0;
		
		ServletContext context = getServletConfig().getServletContext();
		String mimetype = context.getMimeType(original_filename);
		
		resp.setContentLength((int) f.length());
		resp.setHeader("Content-Disposition", "inline; filename=\"" + original_filename + "\";");
		resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
		resp.setHeader ("Pragma", "public");
		resp.setHeader("Cache-control", "must-revalidate");

		ServletOutputStream op = resp.getOutputStream();
		
		byte[] bbuf = new byte[4 * 1024]; // 4K buffer
		DataInputStream in = new DataInputStream(new FileInputStream(f));

		while ((in != null) && ((length = in.read(bbuf)) != -1)) {
			op.write(bbuf, 0, length);
		}
		in.close();
		op.flush();
		op.close();
	}*/
}
