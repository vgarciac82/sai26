<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	String roles="";
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	Map rol =usuario.getRoles();

 %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   
    
    <title>Integra requisiciones para layout</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<style type="text/css" title="currentStyle">
			@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../css/demo_table_jui.css";
			@import "../css/demo_page.css";
			@import "../../css/interfaz.css";
	</style>
	<link href="../../css/interfaz.css" rel="stylesheet" type="text/css" />

	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="../js/funciones.js"></script>
	<script type="text/javascript" src="../js/FuncionesIntegraRequis.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
  	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
  	
  	<script type="text/javascript" charset="utf-8">
  		$(document).ready(function() {
  			<%
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map pestanas=ebl.getPestana(roles,"RecepcionMaterial");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();
					System.out.println(e.getValue());
					%>
					
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
				}
				%>
	  		$(".tabs").tabs();
	  		
	  		$("#NuevaIntegracion").click(function() {
					window.location = "IntegraRequis.jsp?tab=" + 0;
			});
			$("#ConsultaIntegracion").click(function() {
					window.location = "IntegraRequis.jsp?tab=" + 1;
			});
			$("#PreseleccionaRequis").click(function() {
					
			});
			$(window).bind('resize', function (){
				resizeDt();
			});
		});
  	</script>

  </head>
  
  <body id="dt_example">
  <form action="">
  	<input type="hidden" name="tbs" id="tbs" value = "0">
     <div id="container" class="container" style="width: 98%;">
     	<br/>
			<h1>Integra Requisiciones de tienda digital para layout<label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<br/>
			<div class="tabs" id="tabs">
				<ul>
					<li><a id="NuevaIntegracion" href="#tabs-0" >Nuevo</a></li>
					<li><a id="ConsultaIntegracion" href="#tabs-1" >Consulta</a></li>
					<li><a id="PreseleccionaRequis" href="#tabs-2" >Preselecci&oacute;n</a></li>
					
				</ul>
				<div id="tabs-0" align="center">
					<% if (request.getParameter("tab").equals("0")) { %>
						<jsp:include page="IntegraRequiNueva.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 0);
						</script>
					<% } %>
				</div>
				<div id="tabs-1" align="center" >
					<% if (request.getParameter("tab").equals("1")) { %>
						<jsp:include page="IntegraRequiConsulta.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 1);
						</script>
					<% } %>
				</div>
				<div id="tabs-2" align="center">
					<% if (request.getParameter("tab").equals("2")) { %>
						<jsp:include page="IntegraRequiPreseleccion.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 2);
						</script>
					<% } %>
				</div>
			</div>
     </div>
     </form>
  </body>
</html>
