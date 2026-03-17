<%@page pageEncoding="iso-8859-1" contentType="text/html; charset=iso-8859-1" language="java"%>

<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>

<%!
	private Logger log = LoggerFactory.getLogger(getClass());
%>

<%
	String param_in = "mg=" + request.getParameter("mg") + "&q=" + request.getParameter("q");
	if(param_in.length() <= 6){
		log.warn("Parámetros incompletos: mg= ó q= ");
		session.invalidate();
		response.sendRedirect("../index.jsp");
		return;
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Seguimiento resultado</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  <frameset rows="50%,50%">
 	 <frame name = "formulario" MARGINHEIGHT="0"  SCROLLING="yes" src="seguimiento.jsp?<%=param_in%>">
  	 <frame name = "resultado"  MARGINHEIGHT="0" SCROLLING="no"  src="blank.htm">
  </frameset>
</html>
