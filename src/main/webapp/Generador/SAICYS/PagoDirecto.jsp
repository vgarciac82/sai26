<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuario.getLogin();
	String roles = "";
	Map<String, Role> rol = usuario.getRoles();
	Boolean OnSession=false;
	
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	if (Integer.parseInt(request.getParameter("tab").toString()) == 1 || Integer.parseInt(request.getParameter("tab").toString()) == 0) {
	  	session.setAttribute(GestionInterface.ATT_PagoDirectoEjercicio, null);
		session.setAttribute(GestionInterface.ATT_PagoDirectoFolio, null);
		session.setAttribute(GestionInterface.ATT_PagoDirectoEstado, null);
		session.setAttribute(GestionInterface.ATT_PagoDirectoUE, null);
	}
	
	if (request.getParameter("cIdDocumento") != null) {
		OnSession = true;
		session.setAttribute(GestionInterface.ATT_PagoDirectoEjercicio, request.getParameter("cEjercicio").toString());
		session.setAttribute(GestionInterface.ATT_PagoDirectoFolio, request.getParameter("cIdDocumento").toString());
		session.setAttribute(GestionInterface.ATT_PagoDirectoEstado, request.getParameter("nIdEstado").toString());
		session.setAttribute(GestionInterface.ATT_PagoDirectoUE, request.getParameter("cIdUnidadEjecutora").toString());
	}
	if ((String)session.getAttribute(GestionInterface.ATT_PagoDirectoFolio) != null)
		OnSession = true;
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title></title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="Consolidado">
	<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />
	<style type="text/css" title="currentStyle">
			@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../css/demo_table_jui.css";
			@import "../css/demo_page.css";
			@import "../css/interfaz.css";
	</style>
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.core"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
			<%
				int tabla=0;
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
				Map pestanas=ebl.getPestana(roles,"PagosDirectos");
				Iterator it = pestanas.entrySet().iterator();
				
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
				}
			%>
			
			var sesion = "<%=OnSession%>";
			$(".tabs").tabs();
			$("#nuevoPagoDirecto").click(function() {
				$("#caratulaPagoDirecto").attr("disabled",true);
				$("#facturaPagoDirecto").attr("disabled",true);				
				$("#partidasPagoDirecto").attr("disabled",true);
				$("#firmantesPagoDirecto").attr("disabled",true);
				$("#apartadoPagoDirecto").attr("disabled",true);
				$("#pagosPagoDirecto").attr("disabled",true);
				$("#retencionesPagoDirecto").attr("disabled",true);
				window.location = "PagoDirecto.jsp?tab=" + 0;
			});
			$("#consultaPagoDirecto").click(function() {
				$("#caratulaPagoDirecto").attr("disabled",true);
				$("#facturaPagoDirecto").attr("disabled",true);
				$("#partidasPagoDirecto").attr("disabled",true);
				$("#firmantesPagoDirecto").attr("disabled",true);
				$("#apartadoPagoDirecto").attr("disabled",true);
				$("#pagosPagoDirecto").attr("disabled",true);
				$("#retencionesPagoDirecto").attr("disabled",true);
				window.location = "PagoDirecto.jsp?tab=" + 1;
			});
			$("#caratulaPagoDirecto").click(function() {
				window.location = "PagoDirecto.jsp?tab=" + 2;
			});
			$("#facturaPagoDirecto").click(function() {
				window.location = "PagoDirecto.jsp?tab=" + 3;
			});
			$("#retencionesPagoDirecto").click(function() {
				window.location = "PagoDirecto.jsp?tab=" + 4;
			});
			
			$("#partidasPagoDirecto").click(function() {
				window.location = "PagoDirecto.jsp?tab=" + 5;
			});
			$("#firmantesPagoDirecto").click(function() {
				window.location = "PagoDirecto.jsp?tab=" + 6;
			});
			$("#apartadoPagoDirecto").click(function() {
				window.location = "PagoDirecto.jsp?tab=" + 7;
			});
			$("#pagosPagoDirecto").click(function() {
				window.location = "PagoDirecto.jsp?tab=" + 8;
			});
			
			
			if (sesion == "false") {
				$("#caratulaPagoDirecto").attr("disabled",true);
				$("#facturaPagoDirecto").attr("disabled",true);
				$("#partidasPagoDirecto").attr("disabled",true);
				$("#firmantesPagoDirecto").attr("disabled",true);
				$("#apartadoPagoDirecto").attr("disabled",true);
				$("#pagosPagoDirecto").attr("disabled",true);
				$("#retencionesPagoDirecto").attr("disabled",true);				
			}
		});
		
		function checkShortcut()
		{
			if(event.keyCode==27){
				return false;
			}
			if((event.srcElement.tagName.toUpperCase() != 'INPUT'
				|| document.getElementById(event.srcElement.id).style.readonly )						
				&& (event.keyCode==8 || event.keyCode==13)){					
				return false;
			}
		}
	</script>
</head>  
<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form action="../../gstnmngr/gestion?cmd=2&id_tc=7&pb_crear='Iniciar Tramite'" method="post" id="form1"> 
		<div id="container" class="container">
			<br/>
			<br/>
			<h1>Pagos Directos<label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<br/>
			<div class="tabs" id="tabs" name="tabs" >
				<ul>
					<li><a id="nuevoPagoDirecto" href="#tabs-0" >Nuevo</a></li>
					<li><a id="consultaPagoDirecto" href="#tabs-1" >Consulta</a></li>
					<li><a id="caratulaPagoDirecto" href="#tabs-2" >Car&aacute;tula</a></li>
					<li><a id="facturaPagoDirecto" href="#tabs-3" >Facturas</a></li>
					<li><a id="retencionesPagoDirecto" href="#tabs-4" >Retenciones</a></li>
					<li><a id="partidasPagoDirecto" href="#tabs-5" >Partidas</a></li>
					<li><a id="firmantesPagoDirecto" href="#tabs-6">Firmantes</a></li>
					<li><a id="apartadoPagoDirecto" href="#tabs-7">Apartado</a></li>
					<li><a id="pagosPagoDirecto" href="#tabs-8">Pagos</a></li>
				</ul>
				<% if (request.getParameter("tab")!= null){ %>
					<div id="tabs-0" align="center" >
						 <% if (request.getParameter("tab").equals("0")) {
						 OnSession=false; %>
						 <jsp:include page="NuevoPagoDirecto.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 0);
							</script>
						<% }  %>
					</div>
					<div id="tabs-1" align="center" >
						 <% if (request.getParameter("tab").equals("1")) {
						 OnSession=false; %>
						 <jsp:include page="ConsultaPagoDirecto.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 1);
							</script>
						<% }  %>
					</div>
					<div id="tabs-2" align="center">
					 	<% if (request.getParameter("tab").equals("2")) {
					 		OnSession=false; %>
							<jsp:include page="CaratulaPagoDirecto.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 2);
							</script>
						<% } %> 
					</div>
					<div id="tabs-3" align="center" >
						 <% if (request.getParameter("tab").equals("3") && OnSession) { %>
							<jsp:include page="FacturasPagoDirecto.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 3);
							</script>
						<% }  %>						 
					</div>
					<div id="tabs-4" align="center">
					 	<% if (request.getParameter("tab").equals("4")) {
					 		OnSession=false; %>
							<jsp:include page="RetencionesPagoDirecto.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 4);
							</script>
						<% } %> 
					</div>
					<div id="tabs-5" align="center">
					 	<% if (request.getParameter("tab").equals("5")) {
					 		OnSession=false; %>
							<jsp:include page="PartidasPagoDirecto.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 5);
							</script>
						<% } %> 
					</div>
					<div id="tabs-6" align="center" >
						 <% if (request.getParameter("tab").equals("6") && OnSession) { %>
							<jsp:include page="FirmantesPagoDirecto.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 6);
							</script>
						<% }  %>						 
					</div>
					<div id="tabs-7" align="center" >
						 <% if (request.getParameter("tab").equals("7") && OnSession) { %>
							<jsp:include page="ApartadoPagoDirecto.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 7);
							</script>
						<% }  %>						 
					</div>
					<div id="tabs-8" align="center" >
						 <% if (request.getParameter("tab").equals("8") && OnSession) { %>
							<jsp:include page="PagosPagoDirecto.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 8);
							</script>
						<% }  %>						 
					</div>
					
					
				<% } %>
			</div>
	    </div>
	    <br/>
		<br/>
		<input type="hidden" name="lPedidoAbiertoCOntrato" id="lPedidoAbiertoCOntrato" />
	</form>
</body>
</html>
