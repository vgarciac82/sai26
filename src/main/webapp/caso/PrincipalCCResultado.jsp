<%@page language="java" import="java.util.*"  contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@ page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.core.UsuarioPropiedades"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%!private Logger log = Logger.getLogger(getClass());
	String headerParameterHtml = "";
	private String jniName = null;

	public void jspInit() {
		try {
			InitialContext ic = new InitialContext();
			jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");

			if (jniName == null) {
				jniName = "jdbc/gestion";
				log
						.info("Environment Entry \"dataSourceRefName\" nula usando default \""
								+ jniName + "\"");
			} else
				log.info("dataSourceRefName=" + jniName);
		} catch (NamingException exc) {
			jniName = "jdbc/gestion";
			log
					.info("Environment Entry \"dataSourceRefName\" no definida usando default \""
							+ jniName + "\"");
		}
	}

	public String getValor(String data) {
		return (data == null ? "" : data);
	}%>
<%
	Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	Map propiedades = u.getPropiedades();
	UsuarioPropiedades up = (UsuarioPropiedades)propiedades.get("CCENTROCONTABLE");
	String cCentroContable = u.getPropiedad("CCENTROCONTABLE").getValor(); 
	String CentroContable = getValor(request.getParameter("CentroContable"));
	up.setValor(CentroContable);
	cCentroContable = u.getPropiedad("CCENTROCONTABLE").getValor(); 
	u.setPropiedad("CCENTROCONTABLE",up) ;
	JSONObject resJson = new JSONObject();
	resJson.put("status","OK");
	out.println(resJson);
%>
