<%@page pageEncoding="iso-8859-1" contentType="text/html; charset=iso-8859-1" language="java"%>

<%@page import="org.apache.log4j.Logger"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.reportes.ReporteBussinesLogic" %>
<%@page import="com.syc.gestion.reportes.core.ReporteConf" %>
<%!
	private Logger log = Logger.getLogger(getClass());

	private String jniName = null;

	public void jspInit() {
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

%>

<%
	ReporteConf in_rc = new ReporteConf();
	String idreporte = request.getParameter("id");
	String idTipoReporte= request.getParameter("id2");
	
	if(idreporte == null){
		log.warn("Parámetros incompletos: id ");
		session.invalidate();
		response.sendRedirect("../index.jsp");
		return;
	}
	in_rc.setId(Integer.parseInt(idreporte,10));

	Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (u == null) {
		log.warn("No hay Usuario en sesion");
		session.invalidate();
		response.sendRedirect("../index.jsp");
		return;
	}

	ReporteBussinesLogic rbl = new ReporteBussinesLogic(jniName);
	ReporteConf rc = rbl.getConfiguracion(u.getLogin(), in_rc);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
 	<script type="text/javascript">

 		function OnInit()
 		{
			if (<%=in_rc.getId() %> == 2)
			{
			    document.getElementById("fsReporte").rows = "360px,*"
			}
			else if (<%=in_rc.getId() %> == 9)
			{
			    document.getElementById("fsReporte").rows = "350px,*"
			}
			else if ( <%=in_rc.getId() %> == 12 || <%=in_rc.getId() %> == 10)
			{
			    document.getElementById("fsReporte").rows = "1024px,*"
			}
			else if ( <%=in_rc.getId() %> == 15 )
			{
			    document.getElementById("fsReporte").rows = "280px,*"
			}
			else if (<%=in_rc.getId() %> == 14 || <%=in_rc.getId() %> == 16 )
			{
			    document.getElementById("fsReporte").rows = "100%,*"
			}
		}
	</script>
  </head>
   <frameset id="fsReporte" rows="230px,*" onload="OnInit();">
    <%if(idTipoReporte!=null){ %>  
	<frame name = "formulario" SCROLLING="auto"  src="<%=rc.getPlantillaFormulario()+"?id="+in_rc.getId()+"&id2="+idTipoReporte%>"/>
  	<frame name = "resultado"  SCROLLING="auto"  src=<%=rc.getPlantillaResultado()+"?id="+in_rc.getId()+"&id2="+idTipoReporte%>/>
     <%}else{ %>
    <frame name = "formulario" SCROLLING="auto"  src="<%=rc.getPlantillaFormulario()+"?id="+in_rc.getId()%>"/>
  	<frame name = "resultado"  SCROLLING="auto"  src=<%=rc.getPlantillaResultado()+"?id="+in_rc.getId()%>/>
    <%} %>
  </frameset>

</html>
