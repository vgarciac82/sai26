package com.syc.contable.servlet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.syc.contable.RectificacionPresupuestariaBusinessLogic;
import com.syc.contable.RectificacionPresupuestariaMilBusinessLogic;
import com.syc.contable.core.RectificacionEncabezado;
import com.syc.contable.core.ReintegroDetalle;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;

public class LayoutRectificacionesSicopServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public LayoutRectificacionesSicopServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
	    try {
			creaSicop(request, response);
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

	public void creaSicop(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
		HttpSession session = request.getSession(false);

		Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
		Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
		if (usuario == null) {
			response.sendRedirect("../index.jsp");
			return;
		}
		
		final int folio = Integer.parseInt(c.getFolio().split("-")[2]);
		
		RectificacionPresupuestariaBusinessLogic rectificacionBL = new RectificacionPresupuestariaBusinessLogic(GestionInterface.ATT_CONEXION);
		RectificacionPresupuestariaMilBusinessLogic rectificacionMilBL = new RectificacionPresupuestariaMilBusinessLogic(GestionInterface.ATT_CONEXION);
		RectificacionEncabezado re=new RectificacionEncabezado();
		StringBuilder detalles = new StringBuilder();
		
		if (request.getParameter("mil") != null && request.getParameter("mil").equals("1")) {
			re = rectificacionMilBL.getRectificacionEncabezadoSicop(folio);
			detalles = rectificacionMilBL.getRectificacionDetalleSicop(folio);
		}else{
			re = rectificacionBL.getRectificacionEncabezadoSicop(folio);
			detalles = rectificacionBL.getRectificacionDetalleSicop(folio);
		}
		ReintegroDetalle reintegro = new ReintegroDetalle();
		ArrayList<ReintegroDetalle> reintegrosDetalle = new ArrayList<ReintegroDetalle>();;
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
	
		String file_name = c.getFolio();
		response.setContentType("application/vnd.ms-excel");
		response.addHeader("Content-Disposition","inline; filename=\"" + file_name + ".csv\";");

		// Para escribir en el archivo la fecha de tipo dd/mm/yyyy
		Date date = new java.util.Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fecha = sdf.format(date);		
		StringBuilder archivo = new StringBuilder();
		
		try {
		    archivo.append(re.getPosicion1());
		    archivo.append(",");
		    archivo.append(re.getfAplicacion()==null?"": sdf.format( new java.util.Date(re.getfAplicacion().replaceAll("-", "/")) ));
		    archivo.append(",");
		    archivo.append(re.getfExp()==null?"": sdf.format( new java.util.Date(re.getfExp().replaceAll("-", "/")) ));
		    archivo.append(","+re.getcRamo().trim());
		    archivo.append(","+re.getcRamo().trim());
		    archivo.append(","+re.getcRamo().trim());
		    archivo.append(",RHQ");
		    archivo.append(",RHQ");
		    archivo.append(",RHQ");
		    archivo.append(","+re.getcTipoMovto());
		    archivo.append(","+re.getnOrigenPPTO());
		    archivo.append(","+re.getcConceptoRectificacion());
		    archivo.append(","+re.getCtr_int());
		    archivo.append(","+c.getFolio().trim().replace(" ", ""));
		    archivo.append(","+c.getFolio().trim().replace(" ", ""));
		    archivo.append(",8");
		} catch (Exception jxlex) {
			jxlex.printStackTrace();
		}
								
		archivo.append(detalles.toString().replace(" ", ""));
		try{
			bw.write(archivo.toString());
			bw.flush();   
			bw.close(); 
		}catch(Exception ex){			
		    ex.printStackTrace();
		}
	}
}
