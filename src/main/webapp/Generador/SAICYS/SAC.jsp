<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Boolean OnSession=false;
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	String roles="";
	Map rol =usuario.getRoles();
	if ((String)session.getAttribute(GestionInterface.ATT_ContratoEjercicio) != null)
		OnSession = true;
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>My JSP 'SAC.jsp' starting page</title>
    
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
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
  	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../js/funciones.js"></script>
	<script type="text/javascript" src="../js/funcionesSAC.js"></script>
	
	
	
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
				Map pestanas=ebl.getPestana(roles,"SAC");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);<%
				}
				
			%>
			$(".tabs").tabs();						
			$("#consultaSAC").click(function() {
				window.location = "SAC.jsp?tab=" + 0;
			});
			$("#nuevoSAC").click(function() {
				window.location = "SAC.jsp?tab=" + 1;
			});			
			$("#procedimientoSAC").click(function() {
				window.location = "SAC.jsp?tab=" + 2;
			});
			$("#contratoSAC").click(function() {
				window.location = "SAC.jsp?tab=" + 3;
			});			
			$("#convenioSAC").click(function() {
				window.location = "SAC.jsp?tab=" + 4;
			});
			$(window).bind('resize', function (){
				resizeDt();
			});
		});
		function checkShortcut(){			
			if(event.keyCode==27){  //escape
				return false;
			}
			if(((event.srcElement.tagName.toUpperCase() != 'INPUT' && event.srcElement.tagName.toUpperCase() != 'TEXTAREA')
				|| document.getElementById(event.srcElement.id).style.readonly )	//backspace
				&& (event.keyCode==8 || event.keyCode==13)){					
				return false;
			}
		}
		function getUrlParameter(param) {
			param = param.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
			var r1 = "[\\?&]"+param+"=([^&#]*)";
			var r2 = new RegExp(r1);
			var r3 = r2.exec(window.location.href);
			if (r3 == null)
				return "";
			else
				return r3[1];
		}
	</script>

  </head>
  
  <body id="dt_example" onkeydown="return checkShortcut()" style="width: 95%">
    <form > 
		<div id="container" class="container" style="width: 90%">	
			<br/>
			<br/>
			<h1>Base de datos SAC<label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<br/>
			<div class="tabs" id="tabs" >
				<ul>
					<li><a id="consultaSAC" href="#tabs-0" >Consulta</a></li>
					<li><a id="nuevoSAC" href="#tabs-1" >Nuevo</a></li>
					<li><a id="procedimientoSAC" href="#tabs-2" >Procedimiento</a></li>
					<li><a id="contratoSAC" href="#tabs-3" >Contrato</a></li>
					<li><a id="convenioSAC" href="#tabs-4" >Convenio</a></li>
				</ul>
				<% 
				String tab=request.getParameter("tab").toString(); 
				System.out.println("tab:"+tab);
				if (request.getParameter("tab")!= null){ %>
					<div id="tabs-0" align="center">
					 	<% if (request.getParameter("tab").equals("0")) {
					 		OnSession=false; %>
							<jsp:include page="ConsultaProcedimientoSAC.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 0);
							</script>
						<% } %> 
					</div>
					<div id="tabs-1" align="center">
					 	<% if (request.getParameter("tab").equals("1")) {
					 		OnSession=false; %>
							<jsp:include page="NuevoProcedimientoSAC.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 1);
							</script>
						<% } %> 
					</div>
					<div id="tabs-2" align="center">
					 	<% if (request.getParameter("tab").equals("2")) {
					 		OnSession=false; %>
							<jsp:include page="ProcedimientoSAC.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 2);
							</script>
						<% } %> 
					</div>
					<div id="tabs-3" align="center">
					 	<% if (request.getParameter("tab").equals("3")) {
					 		OnSession=false; %>
							<jsp:include page="ContratoSAC.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 3);
							</script>
						<% } %> 
					</div>
					<div id="tabs-4" align="center">
					 	<% if (request.getParameter("tab").equals("4")) {
					 		OnSession=false; %>
							<jsp:include page="ConvenioSAC.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 4);
							</script>
						<% } %> 
					</div>
				<% } %> 
			</div>
		</div>
		<input type="hidden" name="tbs" id="tbs" value = "0">
	</form>
	
  </body>
</html>
