package com.syc.contable.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.syc.contable.ReintegrosAnexo1BusinessLogic;
import com.syc.contable.ReintegrosBusinessLogic;
import com.syc.contable.ReintegrosMilBusinessLogic;
import com.syc.contable.core.CompromisoManager;
import com.syc.contable.core.ReintegroDetalle;
import com.syc.contable.core.ReintegroEncabezado;
import com.syc.contable.core.ReintegroEncabezadoMil;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionFileReceiverServlet;
import com.syc.gestion.servlet.GestionInterface;

public class LayoutReintegrosSicopServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
    	private static final long serialVersionUID = 1L;
	public LayoutReintegrosSicopServlet() {
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
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/*
		 * response.setContentType("text/html"); PrintWriter out =
		 * response.getWriter(); out.println(
		 * "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		 * out.println("<HTML>");
		 * out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		 * out.println("  <BODY>"); out.print("    This is ");
		 * out.print(this.getClass()); out.println(", using the GET method");
		 * out.println("  </BODY>"); out.println("</HTML>"); out.flush();
		 * out.close();
		 */
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	private static Logger log = Logger.getLogger(GestionFileReceiverServlet.class);

	private String jniName = null;
	private String tempDir = null;

	public void init(ServletConfig config) throws ServletException {

		super.init(config);

		try {
			InitialContext ic = new InitialContext();
			jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");

			if (jniName == null) {
				jniName = "jdbc/gestion";
				log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
			} else
				log.info("dataSourceRefName=" + jniName);
		} catch (NamingException exc) {
			jniName = "jdbc/gestion";
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
		}

		tempDir = config.getInitParameter("tempDir");
		if (tempDir == null) {
			tempDir = config.getServletContext().getRealPath("/") + File.separator + "upload" + File.separator;

			File fDir = new File(tempDir);
			if (!fDir.exists())
				if (!fDir.mkdirs())
					throw new ServletException("No se pudo crear el directorio " + tempDir);
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String tipoLayout = request.getParameter("tipoLayout");
			
			if("1".equals(tipoLayout)) //Layout para reintegros normales
				creaSicop(request, response);
			if("2".equals(tipoLayout)) //Layout para reintegros capitulo mil
				creaSicopMil(request, response);
			if("3".equals(tipoLayout)) //Layout para reintegros de Anexo 1 (Disponible Radicado-Disponible Neto)
				creaSicopAnexo1(request, response);
			if("4".equals(tipoLayout)) //Layout para decremento de compromiso en SICOP de RG
				layoutCompromisos(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void creaSicop(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException{
		//AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
		HttpSession session = request.getSession(false);

		Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
		Usuario usuario = (Usuario) session
				.getAttribute(GestionInterface.ATT_USER);
		if (usuario == null) {
			response.sendRedirect("../index.jsp");
			return;
		}
		
		final int folio = Integer.parseInt(c.getFolio().split("-")[2]);
		
		ReintegrosBusinessLogic reintegroBL = new ReintegrosBusinessLogic(GestionInterface.ATT_CONEXION);

		ReintegroEncabezado re = reintegroBL.getReintegroEncabezado(folio);
		ArrayList<ReintegroDetalle> reintegrosDetalle2 = new ArrayList<ReintegroDetalle>();
				
		try {
			reintegrosDetalle2 = reintegroBL.getReintegroLayout(folio);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
	
		String file_name = c.getFolio();
		response.setContentType("application/vnd.ms-excel");
		response.addHeader("Content-Disposition","inline; filename=\"" + file_name + ".csv\";");

		// Para escribir en el archivo la fecha de tipo dd/mm/yyyy
		Date date = new java.util.Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fecha = sdf.format(date);
		
		String archivo = "";
		
		try {
		    archivo="H";//A
		    archivo+=",";
		    archivo+= re.getfAplicacion()==null?"": sdf.format( new java.util.Date(re.getfAplicacion().replaceAll("-", "/")) );//B
		    archivo+=",";
		    archivo+= re.getfSolicitud()==null?"": sdf.format( new java.util.Date(re.getfSolicitud().replaceAll("-", "/")) );//C
		    archivo+=","+re.getcRamo().trim();//D
		    archivo+=","+re.getcRamo().trim();//E
		    archivo+=","+re.getcRamo().trim();//F
		    archivo+=","+re.getcUnidadResponsableContable().trim();//G
		    archivo+=","+re.getcUnidadResponsableContable().trim();//H
		    archivo+=","+re.getcUnidadResponsableContable().trim();//I
		    archivo+=",";
		    archivo+= re.getfSolicitud()==null?"": sdf.format( new java.util.Date(re.getfSolicitud().replaceAll("-", "/")) );//J
		    archivo+=","+re.getMovimiento();//K
		    archivo+=","+("");//L
		    archivo+=","+("N/A".equals(re.getTipoAviso())?"":re.getTipoAviso());//M
		    archivo+=","+("N/A".equals(re.getCausaAviso())?"":re.getCausaAviso());//N
		    archivo+=","+("");//O
		    archivo+=","+("");//P
		    archivo+=","+re.getObservaciones().replace(",", "");//Q
		    archivo+=","+re.getConcepto().replace(",", "");//R
		    archivo+=",";//S
		    archivo+=",";//T
		    archivo+=",";//U
		    archivo+=",";//V
		    archivo+=","+file_name.replace(" ", "");//W
		    archivo+=","+file_name.replace(" ", "");//X
		    archivo+=",";//Y	
		} catch (Exception jxlex) {
			jxlex.printStackTrace();
		}
		
		String celdaA="";
		String celdaB="";
		if(re.getTipoAviso()==1){
			celdaA=",\r\n72";
			celdaB="604_AVR_3";
		}else{
			celdaA=",\r\n69";
			celdaB="601_AVR_3";
		}
			
		try {
		    for (int i = 0; i < reintegrosDetalle2.size(); i++) {
				//ReintegroDetalle reintegroDetalle = new ReintegroDetalle();
				ReintegroDetalle reintegroDetalleB = new ReintegroDetalle();
				//reintegroDetalle = reintegrosDetalle.get(i);
				reintegroDetalleB = reintegrosDetalle2.get(i);
				String[] claveEp = reintegroDetalleB.getEP().split("\\.");
				archivo+=celdaA;//A
				archivo+=","+celdaB;//B
				archivo+=","+reintegroDetalleB.getClcSicop();//C
				archivo+=","+reintegroDetalleB.getSecCLC();//D
				archivo+=","+claveEp[1];//E
				archivo+=","+claveEp[2];//F
				archivo+=","+claveEp[0];//G
				archivo+=","+claveEp[3];//H
				archivo+=","+claveEp[4];//I
				archivo+=","+claveEp[5];//J				
				archivo+=","+claveEp[6];//K
				archivo+=","+claveEp[7];//L
				archivo+=","+claveEp[8];//M
				archivo+=","+claveEp[9].substring(0,1).trim();//N
				archivo+=","+claveEp[9].substring(1,2).trim();//O
				archivo+=","+claveEp[9].substring(2,3).trim();//P
				archivo+=","+claveEp[9].substring(3,5).trim();//Q
				archivo+=","+claveEp[10];//R
				archivo+=","+claveEp[11];//S
				archivo+=","+claveEp[12];//T
				archivo+=","+claveEp[13].trim();//U
				archivo+=",0000000000";//+claveEp[14];//V
				archivo+=",00";//+reintegroDetalleB.getnCompromiso();//X ESTE REALMENTE ES LA cUNIDADNORMATIVA
				archivo+=",000";//W
				archivo+=",000";//X
				archivo+=",00000";//Y
				archivo+=",00000";//Z
				archivo+=",0000000000";//AA
				archivo+=","+reintegroDetalleB.getnPartida();;//AB
				archivo+=","+reintegroDetalleB.getMes();//AC
				archivo+=","+reintegroDetalleB.getCxp();//AD
				archivo+=","+reintegroDetalleB.getBeneficiario();//AE
				archivo+=","+reintegroDetalleB.getNres();//AF
				archivo+=","+reintegroDetalleB.getSolOli();//AG
				archivo+=","+reintegroDetalleB.getTpag();//AH
				archivo+=","+reintegroDetalleB.getTipoDeCon();//AI
				archivo+=","+reintegroDetalleB.getTipoCon();//AJ				
				archivo+=",0.00";//AK
				archivo+=",0.00";//AL
				archivo+=",0.00";//AM
				archivo+=",0.00";//AN
				archivo+=",";//AO
				archivo+=","+reintegroDetalleB.getSuficiencia();//AP
		    }
			bw.write(archivo);
			bw.flush();   
			bw.close(); 
		}catch(Exception ex){			
		    ex.printStackTrace();
		}
	}
	
	public void creaSicopMil(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException{
		//AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
		HttpSession session = request.getSession(false);

		Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
		Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
		if (usuario == null) {
			response.sendRedirect("../index.jsp");
			return;
		}
		
		final int folio = Integer.parseInt(c.getFolio().split("-")[2]);
		
		ReintegrosMilBusinessLogic reintegroBL = new ReintegrosMilBusinessLogic(GestionInterface.ATT_CONEXION);

		ReintegroEncabezadoMil re = reintegroBL.getReintegroEncabezadoNuevo(folio);
		ArrayList<ReintegroDetalle> reintegrosDetalle2 = new ArrayList<ReintegroDetalle>();
				
		try {
			reintegrosDetalle2 = reintegroBL.getReintegroLayout(folio);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
	
		String file_name = c.getFolio();
		response.setContentType("application/vnd.ms-excel");
		response.addHeader("Content-Disposition","inline; filename=\"" + file_name + ".csv\";");

		// Para escribir en el archivo la fecha de tipo dd/mm/yyyy
		Date date = new java.util.Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fecha = sdf.format(date);
		
		String archivo = "";
		
		try {
		    archivo="H";//A
		    archivo+=",";
		    archivo+= re.getfAplicacion()==null?"": sdf.format( new java.util.Date(re.getfAplicacion().replaceAll("-", "/")) );
		    archivo+=",";
		    archivo+= re.getfSolicitud()==null?"": sdf.format( new java.util.Date(re.getfSolicitud().replaceAll("-", "/")) );//C
		    archivo+=","+re.getcRamo().trim();//D
		    archivo+=","+re.getcRamo().trim();//E
		    archivo+=","+re.getcRamo().trim();//F
		    archivo+=","+re.getcUnidadResponsableContable().trim();//G
		    archivo+=","+re.getcUnidadResponsableContable().trim();//H
		    archivo+=","+re.getcUnidadResponsableContable().trim();//I
		    archivo+=",";
		    archivo+= re.getfSolicitud()==null?"": sdf.format( new java.util.Date(re.getfSolicitud().replaceAll("-", "/")) );//C
		    archivo+=","+re.getMovimiento();//K
		    archivo+=","+("");//L
		    archivo+=","+("N/A".equals(re.getTipoAviso())?"":re.getTipoAviso());//M
		    archivo+=","+("N/A".equals(re.getCausaAviso())?"":re.getCausaAviso());//N
		    archivo+=","+("");//O
		    archivo+=","+("");//M
		    archivo+=","+re.getObservaciones().replace(",", "");//Q
		    archivo+=","+re.getConcepto().replace(",", "");//R
		    archivo+=",";//S
		    archivo+=",";//T
		    //archivo+=",";
		    archivo+=",";//re.getfAcreditacion()==null?"": sdf.format( new java.util.Date(re.getfAcreditacion().replaceAll("-", "/")) );//C
		    archivo+=",";//V
		    archivo+=","+file_name.replace(" ", "");//W
		    archivo+=","+file_name.replace(" ", "");//X
		    archivo+=",";//Y
		} catch (Exception jxlex) {
			jxlex.printStackTrace();
		}
		
		String celdaA="";
		String celdaB="";
		if("1".equals(re.getTipoAviso())){
			celdaA=",\r\n72";
			celdaB="604_AVR_3";
		}else{
			celdaA=",\r\n69";
			celdaB="601_AVR_3";
		}
			
		try {
		    for (int i = 0; i < reintegrosDetalle2.size(); i++) {
				//ReintegroDetalle reintegroDetalle = new ReintegroDetalle();
				ReintegroDetalle reintegroDetalleB = new ReintegroDetalle();
				//reintegroDetalle = reintegrosDetalle.get(i);
				reintegroDetalleB = reintegrosDetalle2.get(i);
				String[] claveEp = reintegroDetalleB.getEP().split("\\.");
				archivo+=celdaA;//A
				archivo+=","+celdaB;//B
				archivo+=","+reintegroDetalleB.getClcSicop();//C
				archivo+=","+reintegroDetalleB.getSecCLC();//D
				archivo+=","+claveEp[1];//E
				archivo+=","+claveEp[2];//F
				archivo+=","+claveEp[0];//G
				archivo+=","+claveEp[3];//H
				archivo+=","+claveEp[4];//I
				archivo+=","+claveEp[5];//J
				archivo+=","+claveEp[6];//K
				archivo+=","+claveEp[7];//L
				archivo+=","+claveEp[8];//M
				archivo+=","+claveEp[9].substring(0,1).trim();//N
				archivo+=","+claveEp[9].substring(1,2).trim();//O
				archivo+=","+claveEp[9].substring(2,3).trim();//P
				archivo+=","+claveEp[9].substring(3,5).trim();//Q
				archivo+=","+claveEp[10];//R
				archivo+=","+claveEp[11];//S
				archivo+=","+claveEp[12];//T
				//archivo+=",0";//
				archivo+=",00000000000"; //+claveEp[13].trim();//U
				archivo+=",0000000000";//+claveEp[14];//V
				archivo+=",00";//+reintegroDetalleB.getnCompromiso();//W ESTE REALMENTE ES LA cUNIDADNORMATIVA
				archivo+=",000";//X
				archivo+=",000";//Y
				archivo+=",00000";//Z
				archivo+=",00000";//AA
				archivo+=",0000000000";//AB
				archivo+=","+reintegroDetalleB.getnPartida();;//AC
				archivo+=","+reintegroDetalleB.getMes();//AD
				archivo+=","+reintegroDetalleB.getCxp();//AE ******REVISAR
				archivo+=","+reintegroDetalleB.getBeneficiario();//AF
				archivo+=","+reintegroDetalleB.getNres();//AG *****REVISAR
				archivo+=","+reintegroDetalleB.getSolOli();//AH
				archivo+=","+reintegroDetalleB.getTpag();//AI
				archivo+=","+reintegroDetalleB.getTipoDeCon();//AJ
				archivo+=","+reintegroDetalleB.getTipoCon();//AK
				//archivo+=","+reintegroDetalle.getTipoDeCon();//AK
				//archivo+=","+reintegroDetalle.getMvto();//AL
				//archivo+=","+reintegroDetalleB.getIsr();//AM
				//archivo+=","+reintegroDetalleB.getIva();//AN
				//archivo+=","+reintegroDetalleB.getMillar();//AO
				archivo+=",0.00";//AL
				archivo+=",0.00";//AM
				archivo+=",0.00";//AN
				archivo+=",0.00";//AO
				//archivo+=","+re.getcUnidadResponsable();//AP
				//archivo+=","+c.getFolio().replace(" ", "");//AP
				archivo+=",";//AP
				archivo+=","+reintegroDetalleB.getSuficiencia();//AQ
		    }
			bw.write(archivo);
			bw.flush();   
			bw.close(); 
		}catch(Exception ex){			
		    ex.printStackTrace();
		}
	}
	
	public void creaSicopAnexo1(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException{
		HttpSession session = request.getSession(false);

		Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
		Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
		if (usuario == null) {
			response.sendRedirect("../index.jsp");
			return;
		}
		
		final int folio = Integer.parseInt(c.getFolio().split("-")[2]);
		
		ReintegrosAnexo1BusinessLogic reintegroBL = new ReintegrosAnexo1BusinessLogic(GestionInterface.ATT_CONEXION);

		ReintegroEncabezadoMil re = reintegroBL.getReintegroEncabezadoNuevo(folio);
		ArrayList<ReintegroDetalle> reintegrosDetalle2 = new ArrayList<ReintegroDetalle>();
				
		try {
			reintegrosDetalle2 = reintegroBL.getReintegroLayout(folio);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
	
		String file_name = c.getFolio();
		response.setContentType("application/vnd.ms-excel");
		response.addHeader("Content-Disposition","inline; filename=\"" + file_name + ".csv\";");

		// Para escribir en el archivo la fecha de tipo dd/mm/yyyy
		Date date = new java.util.Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fecha = sdf.format(date);
		
		String archivo = "";
		
		try {
		    archivo="H";//A
		    archivo+=",";
		    archivo+= re.getfSolicitud()==null?"": sdf.format( new java.util.Date(re.getfSolicitud().replaceAll("-", "/")) );
		    archivo+=",";
		    archivo+= re.getfSolicitud()==null?"": sdf.format( new java.util.Date(re.getfSolicitud().replaceAll("-", "/")) );//C
		    archivo+=","+re.getcRamo().trim();//D
		    archivo+=","+re.getcRamo().trim();//E
		    archivo+=","+re.getcRamo().trim();//F
		    archivo+=","+re.getcUnidadResponsableContable().trim();//G
		    archivo+=","+re.getcUnidadResponsableContable().trim();//H
		    archivo+=","+re.getcUnidadResponsableContable().trim();//I
		    archivo+=",";
		    archivo+= re.getfSolicitud()==null?"": sdf.format( new java.util.Date(re.getfSolicitud().replaceAll("-", "/")) );//C
		    archivo+=","+re.getMovimiento();//K
		    archivo+=","+("");//L
		    archivo+=","+("N/A".equals(re.getTipoAviso())?"":re.getTipoAviso());//M
		    archivo+=","+("N/A".equals(re.getCausaAviso())?"":re.getCausaAviso());//N
		    archivo+=","+("");//O
		    archivo+=","+("");//M
		    archivo+=","+re.getObservaciones().replace(",", "");//Q
		    archivo+=","+re.getConcepto().replace(",", "");//R
		    archivo+=",";//S
		    archivo+=",";//T
		    //archivo+=",";
		    archivo+=",";//re.getfAcreditacion()==null?"": sdf.format( new java.util.Date(re.getfAcreditacion().replaceAll("-", "/")) );//C
		    archivo+=",";//V
		    archivo+=","+file_name.replace(" ", "");//W
		    archivo+=","+file_name.replace(" ", "");//X
		    archivo+=",";//Y
		} catch (Exception jxlex) {
			jxlex.printStackTrace();
		}
		
		String celdaA="";
		String celdaB="";
		if("1".equals(re.getTipoAviso())){
			celdaA=",\r\n72";
			celdaB="604_AVR_3";
		}else{
			celdaA=",\r\n69";
			celdaB="601_AVR_3";
		}
			
		try {
		    for (int i = 0; i < reintegrosDetalle2.size(); i++) {
				//ReintegroDetalle reintegroDetalle = new ReintegroDetalle();
				ReintegroDetalle reintegroDetalleB = new ReintegroDetalle();
				//reintegroDetalle = reintegrosDetalle.get(i);
				reintegroDetalleB = reintegrosDetalle2.get(i);
				String[] claveEp = reintegroDetalleB.getEP().split("\\.");
				
				archivo+=celdaA;//A
				archivo+=","+celdaB;//B
				archivo+=","+reintegroDetalleB.getClcSicop();//C
				archivo+=","+reintegroDetalleB.getSecCLC();//D
				archivo+=","+claveEp[1];//E
				archivo+=","+claveEp[2];//F
				archivo+=","+claveEp[0];//G
				archivo+=","+claveEp[3];//H
				archivo+=","+claveEp[4];//I
				archivo+=","+claveEp[5];//J
				archivo+=","+claveEp[6];//K
				archivo+=","+claveEp[7];//L
				archivo+=","+claveEp[8];//M
				archivo+=","+claveEp[9].substring(0,1).trim();//N
				archivo+=","+claveEp[9].substring(1,2).trim();//O
				archivo+=","+claveEp[9].substring(2,3).trim();//P
				archivo+=","+claveEp[9].substring(3,5).trim();//Q
				archivo+=","+claveEp[10];//R
				archivo+=","+claveEp[11];//S
				archivo+=","+claveEp[12];//T
				//archivo+=",0";//
				archivo+=","+claveEp[13].trim();//U
				archivo+=",0000000000";//+claveEp[14];//V
				archivo+=",00";//+reintegroDetalleB.getnCompromiso();//W ESTE REALMENTE ES LA cUNIDADNORMATIVA
				archivo+=",000";//X
				archivo+=",000";//Y
				archivo+=",00000";//Z
				archivo+=",00000";//AA
				archivo+=",0000000000";//AB
				archivo+=","+reintegroDetalleB.getnPartida();;//AC
				archivo+=","+reintegroDetalleB.getMes();//AD
				archivo+=","+reintegroDetalleB.getCxp();//AE ******REVISAR
				archivo+=","+reintegroDetalleB.getBeneficiario();//AF
				archivo+=","+reintegroDetalleB.getNres();//AG *****REVISAR
				archivo+=","+reintegroDetalleB.getSolOli();//AH
				archivo+=","+reintegroDetalleB.getTpag();//AI
				archivo+=","+reintegroDetalleB.getTipoDeCon();//AJ
				archivo+=","+reintegroDetalleB.getTipoCon();//AK
				//archivo+=","+reintegroDetalle.getTipoDeCon();//AK
				//archivo+=","+reintegroDetalle.getMvto();//AL
				//archivo+=","+reintegroDetalleB.getIsr();//AM
				//archivo+=","+reintegroDetalleB.getIva();//AN
				//archivo+=","+reintegroDetalleB.getMillar();//AO
				archivo+=",0.00";//AL
				archivo+=",0.00";//AM
				archivo+=",0.00";//AN
				archivo+=",0.00";//AO
				//archivo+=","+re.getcUnidadResponsable();//AP
				//archivo+=","+c.getFolio().replace(" ", "");//AP
				archivo+=",";//AP
				archivo+=","+reintegroDetalleB.getSuficiencia();//AQ
		    }
			bw.write(archivo);
			bw.flush();   
			bw.close(); 
		}catch(Exception ex){			
		    ex.printStackTrace();
		}
	}
	
	public void layoutCompromisos(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
		HttpSession session = request.getSession(false);

		Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
		Usuario usuario = (Usuario) session
				.getAttribute(GestionInterface.ATT_USER);
		if (usuario == null) {
			response.sendRedirect("../index.jsp");
			return;
		}
		
		final int folio = Integer.parseInt(c.getFolio().split("-")[2]);
		
		ReintegrosBusinessLogic reintegroBL = new ReintegrosBusinessLogic(GestionInterface.ATT_CONEXION);

		String decrementoEncabezado = "";
		String decrementoDetalle = "";
				
		try {
			decrementoEncabezado = reintegroBL.getDecrementoEncabezado(folio,c.getFolio());
			decrementoDetalle = reintegroBL.getDecrementoDetalle(folio,c.getFolio());
			reintegroBL.actualizaEnvioSICOPCompromiso( c.getFolio() );
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
	
		String file_name = "LayoutDecrementoCompromiso" + c.getFolio();
		response.setContentType("application/vnd.ms-excel");
		response.addHeader("Content-Disposition","inline; filename=\"" + file_name + ".csv\";");
		
		String archivo = "";
		
		try {
			archivo=decrementoEncabezado;					;
		    archivo+=decrementoDetalle;
		    		    
			bw.write(archivo);
			bw.flush();   
			bw.close(); 
		}catch(Exception ex){			
		    ex.printStackTrace();
		}
	}

}
	
