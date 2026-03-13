package com.syc.admin.servlet;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.syc.admin.AdminBusinessLogic;
import com.syc.admin.AdminException;
import com.syc.admin.core.Usuario;

public class Dispatcher extends javax.servlet.http.HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(Dispatcher.class);

	private String jniName = null;

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
	}

	
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String origen = request.getParameter("origen");
		String evento = request.getParameter("evento");
		System.out.println("Dispatcher : origin = " + origen + "\tevent = " + evento);
		String destination = "UsersDAO"; 
		// FlowDao.getDestination(origin, event);
		System.out.println("Dispatcher : destination = '" + destination + "'");
		destination = "UsuarioManager";
		if(destination!=null) {
			//forward(destination, request, response);
				
				Usuario u = new Usuario();
				
				u.setLogin(request.getParameter("usr"));
				u.setPassword(request.getParameter("pwd"));
				u.setNombre(request.getParameter("nom"));
				u.setDescripcion(request.getParameter("desc"));
				
				try{
				
					AdminBusinessLogic abl = new AdminBusinessLogic(jniName);
					abl.servicio(evento, u);
					
				} catch(AdminException ae){
					throw new ServletException(ae);
				}
				
				//resp.sendRedirect("../caso/exec-container.jsp");
			//} catch (GestionException exc) {
			//	log.error("Iniciando Caso", exc);
			//	throw new ServletException(exc);
			//}
		} else {
			//forward("projectWa", request, response);
		}
		
		//response.setContentType("text/xml");
	    //PrintWriter out = response.getWriter();
	    
	    //out.println("<?xml version=\"1.0\"?>");
	    //out.println("<greeting language=\"en_US\">");
	    //out.println("  Hello, World!");
	    //out.println("</greeting>");
	    
	    StringBuffer xmlResp = getRespuesta("status");
	    
	    response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		response.getWriter().write(xmlResp.toString());
		
		System.out.println(xmlResp.toString());

	}
	
	private StringBuffer getRespuesta(String resp) {
		StringBuffer xmlOut = new StringBuffer();

		xmlOut.append("<respuesta>");
		xmlOut.append("<estado valor='");
		xmlOut.append(resp);
		xmlOut.append("'/></respuesta>");

		return xmlOut;
	}

	
	public void forward(String location, HttpServletRequest request, HttpServletResponse response){
		try {
			System.out.println("forwarding to " + location);
			(getServletContext().getRequestDispatcher("/"+location)).forward(request, response);
		} catch(ServletException se){
			System.out.println(se);
		} catch(IOException ioe){
			System.out.println(ioe);
		}
	}
}

/*
class FlowDao {
	static String getDestination(String origin, String event){
		if(origin == null || event == null)
			return null;
		String destination = null;
		Connection c=null;
		Statement s=null;
		ResultSet rs=null;
		try {
			InitialContext ic = new InitialContext();
			DataSource ds = (DataSource)ic.lookup("jdbc/BugDs");
			c = ds.getConnection();
			s = c.createStatement();
			rs = s.executeQuery("select * from FLOW where ORIGIN='"+origin+"' and EVENT='"+event+"'");
			if(rs == null) return null;
			if(rs.next())
				destination = rs.getString("DESTINATION");
		} catch(NamingException ne){
			System.out.println(ne);
		} catch(SQLException sqle){
			System.out.println(sqle);
		} finally {
			try {
				if(c!=null) c.close();
				if(s!=null) s.close();
				if(rs!=null) rs.close();
			} catch(SQLException sqle){
				System.out.println(sqle);
			}
		}
		return destination.trim();
	}
}
*/