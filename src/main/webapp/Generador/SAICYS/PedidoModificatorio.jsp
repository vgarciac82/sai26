<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuario.getLogin();
	String role="";
	Map rol =usuario.getRoles();
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	if (session.getAttribute(GestionInterface.ATT_ReqEjercicio) != null ) {
	    session.setAttribute(GestionInterface.ATT_ReqEjercicio, null);
		session.setAttribute(GestionInterface.ATT_ReqTipoSolicitud, null);
		session.setAttribute(GestionInterface.ATT_ReqUnidadEjec, null);
		session.setAttribute(GestionInterface.ATT_ReqConsecutivo, null);
	}
	if (session.getAttribute(GestionInterface.ATT_ContratoEjercicio) != null) { 
		session.setAttribute(GestionInterface.ATT_ContratoEjercicio, null);
		session.setAttribute(GestionInterface.ATT_ContratoTipoContrato, null);
		session.setAttribute(GestionInterface.ATT_ContratoUnidadEjec, null);
		session.setAttribute(GestionInterface.ATT_ContratoConsecutivo,null);
	}
	Boolean OnSession=false;
	if (request.getParameter("pDefinitivo") != null) { 
		session.setAttribute(GestionInterface.ATT_PedidoModificatorioDefinitivo, request.getParameter("pDefinitivo").toString());
		session.setAttribute(GestionInterface.ATT_PedidoModificatorioConsecutivo, request.getParameter("mod").toString());
		session.setAttribute(GestionInterface.ATT_PedidoModificatorioTipoArchivo, request.getParameter("tipoArchivo").toString());
		session.setAttribute(GestionInterface.ATT_PedidoModificatorioEjercicio, request.getParameter("pEjercicio").toString());
		session.setAttribute(GestionInterface.ATT_PedidoModificatorioId, request.getParameter("pId").toString());
	}
	
	if ((String)session.getAttribute(GestionInterface.ATT_PedidoModificatorioDefinitivo) != null){
		OnSession = true;
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Pedidos</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
	</style>
	<style>			// estilos del dialogo
			div#dialog-form fieldset { padding:0; border:0; margin-top:25px; }
			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
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
	<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
		<%
			int tabla=0;
			NegativaPestana NegPestana = new NegativaPestana();
			NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
			Iterator it1 = rol.entrySet().iterator();
			while (it1.hasNext()) {
				Map.Entry r = (Map.Entry)it1.next();
				role=(String)r.getKey();
			}
			Map pestanas = ebl.getPestana(role,"PedidoModificatorio");
			Iterator it = pestanas.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry e = (Map.Entry)it.next();%>
				$( "#<%=e.getValue()%>" ).attr("disabled", true);
				
		<% } %>
		 	
			$(".tabs").tabs();
			$("#nuevoPedidoMod").click(function() {
				window.location = "PedidoModificatorio.jsp?tab=" + 0;
			});
			$("#consultaPedidoMod").click(function() {
				window.location = "PedidoModificatorio.jsp?tab=" + 1;
			});
			$("#caratulaPedidoMod").click(function() {
				window.location = "PedidoModificatorio.jsp?tab=" + 2;
			});
			$("#observacionesPedidoMod").click(function() {
				window.location = "PedidoModificatorio.jsp?tab=" + 3;
			});
			$("#presupuestoPedidoMod").click(function() {
				window.location = "PedidoModificatorio.jsp?tab=" + 4;
			});
			$("#precompromisoPedidoMod").click(function() {
				window.location = "PedidoModificatorio.jsp?tab=" + 5;
			});
			$("#pagosPedidoMod").click(function() {
				window.location = "PedidoModificatorio.jsp?tab=" + 6;
			});
			$("#reduccionPedidoMod").click(function() {
				window.location = "PedidoModificatorio.jsp?tab=" + 7;
			});
			
			if ($("#session").val() == "false") {
			    $( "#caratulaPedidoMod" ).attr("disabled", true);
			    $( "#observacionesPedidoMod" ).attr("disabled", true);
			    $( "#presupuestoPedidoMod" ).attr("disabled", true);
			    $( "#precompromisoPedidoMod" ).attr("disabled", true);
			    $( "#pagosPedidoMod" ).attr("disabled", true);
			    $( "#reduccionPedidoMod" ).attr("disabled", true);
			}
			
		});
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
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" onkeydown="return checkShortcut()">
  	<form action="../../gstnmngr/gestion?cmd=2&id_tc=7&pb_crear='Iniciar Tramite'" method="post" id="form1"> 
		<div id="container" class="container">	
			<br/>
			<br/>
			<h1>Pedidos Modificados<label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<br/>
			<div class="tabs" id="tabs" name="tabs" >
				<ul> 
					<li><a id="nuevoPedidoMod" href="#tabs-0" >Nuevo</a></li>
					<li><a id="consultaPedidoMod" href="#tabs-1" >Consulta</a></li> 
					<li><a id="caratulaPedidoMod" href="#tabs-2" >Car&aacute;tula</a></li>
					<li><a id="observacionesPedidoMod" href="#tabs-3" >Observaciones</a></li>
					<li><a id="presupuestoPedidoMod" href="#tabs-4" >Presupuesto</a></li>
					<li><a id="precompromisoPedidoMod" href="#tabs-5" >PreCompromiso</a></li>
					<li><a id="pagosPedidoMod" href="#tabs-6" >Pagos</a></li>
					<li><a id="reduccionPedidoMod" href="#tabs-7" >Reducci&oacute;n</a></li>
				</ul>
				<% if (request.getParameter("tab")!= null){ %>
					<div id="tabs-0" align="center">
					 	<% if (request.getParameter("tab").equals("0")) {
					 		OnSession=false; %>
							<jsp:include page="PedidoModificatorioNuevo.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 0);
							</script>
						<% } %> 
					</div>
					<div id="tabs-1" align="center">
					 	<% if (request.getParameter("tab").equals("1")) {
					 		OnSession=false; %>
							<jsp:include page="PedidoModificatorioConsulta.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 1);
							</script>
						<% } %> 
					</div>
					<div id="tabs-2" align="center" >
						 <% if (request.getParameter("tab").equals("2") && OnSession) { %>
							<jsp:include page="PedidoModificatorioCaratula.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 2);
							</script>
						<% }  %>
					</div>
					<div id="tabs-3" align="center" >
						 <% if (request.getParameter("tab").equals("3") && OnSession) { %>
							<jsp:include page="PedidoModificatorioObservaciones.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 3);
							</script>
						<% }  %>
					</div>
					<div id="tabs-4" align="center" >
						 <% if (request.getParameter("tab").equals("4") && OnSession) { %>
							<jsp:include page="PedidoModificatorioPresupuesto.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 4);
							</script>
						<% }  %>
					</div>
					<div id="tabs-5" align="center" >
						 <% if (request.getParameter("tab").equals("5") && OnSession) { %>
							<jsp:include page="PedidoModificatorioPreCompromiso.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 5);
							</script>
						<% }  %>
					</div>
					<div id="tabs-6" align="center" >
						 <% if (request.getParameter("tab").equals("6") && OnSession) { %>
							<jsp:include page="PedidoModificatorioPagos.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 6);
							</script>
						<% }  %>
					</div>
					<div id="tabs-7" align="center" >
						 <% if (request.getParameter("tab").equals("7") && OnSession) { %>
							<jsp:include page="ReduccionPedidoModificatorio.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 7);
							</script>
						<% }  %>
					</div>
				<% } %> 
			</div>
	    </div>
	   <input type="hidden" name="session" id="session" value="<%= OnSession %>" />
		
	</form>
  </body>
</html>
