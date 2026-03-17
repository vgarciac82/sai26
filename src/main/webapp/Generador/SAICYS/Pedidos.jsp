<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null){
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	String roles="";
	Map<String, Role> rol =usuario.getRoles();
	if(session.getAttribute(GestionInterface.ATT_PedidoAbierto) == null)
		session.setAttribute(GestionInterface.ATT_PedidoAbierto,0);
	if(request.getParameter("lPedidoAbierto") != null)
		session.setAttribute(GestionInterface.ATT_PedidoAbierto,Integer.parseInt(request.getParameter("lPedidoAbierto").toString()));
	
	if(session.getAttribute(GestionInterface.ATT_EstadoPedido) == null)
		session.setAttribute(GestionInterface.ATT_EstadoPedido,0);
	if(request.getParameter("nIdEstadoPedido") != null){
		session.setAttribute(GestionInterface.ATT_EstadoPedido,Integer.parseInt(request.getParameter("nIdEstadoPedido").toString()));
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
	if (request.getParameter("cEjercicio") != null) { 
		session.setAttribute(GestionInterface.ATT_PedidoEjercicio, request.getParameter("cEjercicio").toString());
		session.setAttribute(GestionInterface.ATT_PedidoTipoPedido, request.getParameter("cIdTipoPedido").toString());
		session.setAttribute(GestionInterface.ATT_PedidoUnidadEjec, request.getParameter("cIdUnidadEjecutora").toString());
		session.setAttribute(GestionInterface.ATT_PedidoConsecutivo, request.getParameter("nIdConsecutivo").toString());
	}
	if ((String)session.getAttribute(GestionInterface.ATT_PedidoEjercicio) != null)
		OnSession = true;
	if (request.getParameter("cPedidoDefinitivo") != null) { 
		session.setAttribute(GestionInterface.ATT_pDefinitivo, request.getParameter("cPedidoDefinitivo").toString());
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Pedidos</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" /> 
		<style type="text/css" title="currentStyle"> 
			@import "../../css/custom-theme/jquery-ui-1.8.16.custom.css";
	 		@import "../css/demo_table_jui.css"; 
			@import "../css/demo_page.css"; 
			@import "../css/demo_table.css"; 
			@import "../../css/interfaz.css";
		</style>
	
		<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/crud.js"></script>
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	  	<script type="text/javascript" src="../js/Pedido.js"></script>
		<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	  	<script type="text/javascript" src="../../js/jquery.blockUI-2.4.2.js"></script>
	  	<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.all.js"></script>	
	<script type="text/javascript" charset="utf-8">
		var nIdEstado=1;
		var nIdEstado=1
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
				Map pestanas=ebl.getPestana(roles,"Pedidos");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
				}
				pestanas=ebl.getPestana(roles,"PedidosContratos");
				it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
				}
		%> 	
			$(".tabs").tabs();
			$("#consultaPedido").click(function() {
				window.location = "Pedidos.jsp?tab=" + 0;
			});
			$("#caratulaPedido").click(function() {
				window.location = "Pedidos.jsp?tab=" + 1;
			});
			$("#partidasPedido").click(function() {
				window.location = "Pedidos.jsp?tab=" + 2;
			});
			$("#firmantesPedido").click(function() {
				window.location = "Pedidos.jsp?tab=" + 3;
			});		
			$("#presupuestoPedido").click(function() {
				window.location = "Pedidos.jsp?tab=" + 4;
			});	
			$("#preCompromisoPedido").click(function() {
				window.location = "Pedidos.jsp?tab=" + 5;
			});
			$("#pagosPedido").click(function() {
				window.location = "Pedidos.jsp?tab=" + 6;
			}); 
			$("#imprimirPedido").click(function() {
				//$("#form1").submit(); 
				//window.location="../../gstnmngr/gestion?cmd=2&id_tc=7&pb_crear='Iniciar Tramite'&sacel=2";
				window.location = "Pedidos.jsp?tab=" + 7;
			});
			$("#clausulasPedido").click(function() {
				window.location = "Pedidos.jsp?tab=" + 8;
			});
			$("#nuevoPedidoCap1000").click(function() {
				window.location = "Pedidos.jsp?tab=" + 9;
			});
			$("#nuevoPedidoFONDEN").click(function() {
				window.location = "Pedidos.jsp?tab=" + 10;
			});
			$("#ampliacionesPedido").click(function() {
				window.location = "Pedidos.jsp?tab=" + 11;
			}); 
			
			$("#plurianualidad").click(function() {
				window.location = "Pedidos.jsp?tab=" +12; 
						
			});
			
			$("#pasivo").click(function() {
				window.location = "Pedidos.jsp?tab=" + 13; 
			});
			
			$("#responsablesAlmacen").click(function() {
				window.location = "Pedidos.jsp?tab=" + 14; 
			});
			
			$("#reportePedido").click(function() {
				window.location = "Pedidos.jsp?tab=" + 15; 
			});
			
			$("#propuestaConjunta").click(function() {
				window.location = "Pedidos.jsp?tab=" + 16; 
			});
			$("#anticiposRetencion").click(function() {
				window.location = "Pedidos.jsp?tab=" + 17; 
			});
			$("#reclasificaTipoAdj").click(function() {
				window.location = "Pedidos.jsp?tab=" + 18; 
			});
			$( "#clausulasPedido").attr("disabled", true);
			if ($("#session").val() == "false") {
			    $( "#caratulaPedido" ).attr("disabled", true);
				$( "#partidasPedido" ).attr("disabled", true);
				$( "#firmantesPedido" ).attr("disabled", true);
				$( "#presupuestoPedido" ).attr("disabled", true);
				$( "#preCompromisoPedido" ).attr("disabled", true);
				$( "#pagosPedido" ).attr("disabled", true);
				$( "#imprimirPedido" ).attr("disabled", true);
				$( "#clausulasPedido").attr("disabled", true);
				$( "#ampliacionesPedido").attr("disabled", true);
				$( "#plurianualidad").attr("disabled", true);
				$( "#pasivo").attr("disabled", true);		
				$( "#responsablesAlmacen").attr("disabled", true);
				$( "#reportePedido").attr("disabled", true);	
				$( "#propuestaConjunta").attr("disabled", true);	
				$( "#anticiposRetencion").attr("disabled", true);
				$( "#reclasificaTipoAdj").attr("disabled", true);	
			}
			nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoPedido)%>;			
			$(window).bind('resize', function (){
				resizeDt();
			});
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
		function compare_dates(fecha, fecha2){
			var xFecha = fecha.split("/");
			var yFecha = fecha2.split("/");
			var xMonth;
			var xDay;
			var xYear;
			var yMonth;
			var yDay;
			var yYear;
		
			xMonth = xFecha[1];
			yMonth = yFecha[1];
			//verifica en que posision viene en anio en fecha1
			if(xFecha[0].toString>2){
				xDay = xFecha[2];
				xYear = xFecha[0];
			}
			else{
				xDay = xFecha[0];
				xYear = xFecha[2];
			}
			
			//verifica en que posision viene en anio en fecha2
			if(yFecha[0].toString>2){
				yDay = yFecha[2];
				yYear = yFecha[0];
			}
			else{
				yDay = yFecha[0];
				yYear = yFecha[2];
			}
			
		  	if (xYear> yYear){
		      return(true);
		  	}
		  	else{
		    	if (xYear == yYear){ 
		      		if (xMonth> yMonth){
		          		return(true);
		     		}
		      		else{ 
		        		if (xMonth == yMonth){
		          			if (xDay >= yDay)
		            			return(true);
		          			else
		            			return(false);
		        		}
		        		else
		          			return(false);
		      		}
		    	}
		    	else
		      		return(false);
		  	}
		}
		
	</script>
</head>  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" onkeydown="return checkShortcut()">
  	<form action="../../gstnmngr/gestion?cmd=2&id_tc=7&pb_crear='Iniciar Tramite'" method="post" id="form1"> 
		<div id="container" class="container" style="width: 98%;">	
			<br/>
			<br/>
			<h1>Pedidos<label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<br/>
			<div class="tabs" id="tabs">
				<ul>
					<li><a id="nuevoPedidoFONDEN" href="#tabs-10" >Nuevo-OTROS</a></li>
					<li><a id="nuevoPedidoCap1000" href="#tabs-9" >Nuevo-CAP1000</a></li>
					<li><a id="consultaPedido" href="#tabs-0" >Consulta</a></li>
					<li><a id="caratulaPedido" href="#tabs-1" >Car&aacute;tula</a></li>
					<li><a id="partidasPedido" href="#tabs-2" >Partidas</a></li>      
					<li><a id="firmantesPedido" href="#tabs-3" >Firmantes</a></li>
					<li><a id="presupuestoPedido" href="#tabs-4" >Presupuesto</a></li>
					<li><a id="preCompromisoPedido" href="#tabs-5" >Pre-Compromiso</a></li>
					<li><a id="pagosPedido" href="#tabs-6" >Pagos</a></li>
					<li><a id="imprimirPedido" href="#tabs-7" >Imprimir</a></li>
					<li><a id="clausulasPedido" href="#tabs-8" >Clausulas</a></li>
					<%if (Integer.parseInt(session.getAttribute(GestionInterface.ATT_PedidoAbierto).toString()) == 0){
						%>
						<li><a id="ampliacionesPedido" href="#tabs-11" style="display: none">Ampliaciones</a></li>
						<%
					}
					else{
						%>
						<li><a id="ampliacionesPedido" href="#tabs-11" >Ampliaciones</a></li>
						<%
					}%>
					<li><a id="plurianualidad" href="#tabs-12" >Plurianualidad</a></li>
					<li><a id="pasivo" href="#tabs-13" >Pasivo</a></li>
					<li><a id="responsablesAlmacen" href="#tabs-14" >Responsables Almacen</a></li>
				 	<li><a id="reportePedido" href="#tabs-15" style="display: none" >Reporte Pedido</a></li>
					<li><a id="propuestaConjunta" href="#tabs-16" >Pedido Propuesta Conjunta</a></li>
					<li><a id="anticiposRetencion" href="#tabs-17" >Anticipos y Retenciones</a></li>
				  	<li><a id="reclasificaTipoAdj" href="#tabs-18" >Reclasifica Tipo Adj</a></li>
				
				</ul>
				<% if (request.getParameter("tab")!= null){ %>
					<div id="tabs-10" align="center" >
						 <% if (request.getParameter("tab").equals("10")) {
						 OnSession=false; %>
						 <jsp:include page="PedidoNuevoFONDEN.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 0);
							</script>
						<% }  %>
					</div>
					<div id="tabs-9" align="center" >
						 <% if (request.getParameter("tab").equals("9")) {
						 OnSession=false; %>
						 <jsp:include page="PedidoNuevoCap1000.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 1);
							</script>
						<% }  %>
					</div>
					<div id="tabs-0" align="center">
					 	<% if (request.getParameter("tab").equals("0")) {
					 		OnSession=false; %>
							<jsp:include page="consultaPedido.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 2);
							</script>
						<% } %> 
					</div>
					<div id="tabs-1" align="center" >
						 <% if (request.getParameter("tab").equals("1") && OnSession) { %>
							<jsp:include page="caratulaPedido.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 3);
							</script>
						<% }  %>						 
					</div>
					<div id="tabs-2" align="center" >
						 <% if (request.getParameter("tab").equals("2") && OnSession) { %>
							<jsp:include page="partidasPedido.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 4);
							</script>
						<% }  %>						 
					</div>
					<div id="tabs-3" align="center" >
						 <% if (request.getParameter("tab").equals("3") && OnSession) { %>
							<jsp:include page="firmantesPedido.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 5);
							</script>
						<% }  %>						 
					</div>
					<div id="tabs-4" align="center" >
						 <% if (request.getParameter("tab").equals("4") && OnSession) { %>
							<jsp:include page="presupuestoPedido.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 6);
							</script>
						<% }  %>						 
					</div>
					<div id="tabs-5" align="center" >
						 <% if (request.getParameter("tab").equals("5") && OnSession) { %>
							<jsp:include page="preCompromisoPedido.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 7);
							</script>
						<% }  %>						 
					</div>
					<div id="tabs-6" align="center" >
						 <% if (request.getParameter("tab").equals("6") && OnSession) { %>
							<jsp:include page="pagosPedido.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 8);
							</script>
						<% }  %>						 
					</div>
					
					<div id="tabs-7" align="center" >
						 <% if (request.getParameter("tab").equals("7") && OnSession) {  %>
						 <jsp:include page="imprimirPedido.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 9);
							</script>
						 
						<% }  %>						 
					</div>
					<div id="tabs-8" align="center" >
						 <% if (request.getParameter("tab").equals("8") && OnSession) {  %>
						 <jsp:include page="NuevoClausulado.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 10);
							</script>
						 
						<% }  %>						 
					</div>
					<div id="tabs-11" align="center" >
						 <% if (request.getParameter("tab").equals("11") && OnSession) {  %>
						 <jsp:include page="AmpliacionesPedido.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 11);
							</script>
						 
						<% }  %>						 
					</div>
				<% } %> 
				
				
				<div id="tabs-12" align="center" >
						 <% if (request.getParameter("tab").equals("12") && OnSession) {  %>
						 <jsp:include page="PlurianualidadPedido.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 12);
							</script>
						 
						<% }  %>						 
					</div>
					
					<div id="tabs-13" align="center" >
						 <% if (request.getParameter("tab").equals("13") && OnSession) {  %>
						 <jsp:include page="PasivoPedido.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 13);
							</script>
						 
						<% }  %>						 
					</div>
				
					<div id="tabs-14" align="center" >
						 <% if (request.getParameter("tab").equals("14") && OnSession) {  %>
						 <jsp:include page="ResponsablesAlmacen.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 14);
							</script>
						 
						<% }  %>						 
					</div>
				
					<div id="tabs-15" align="center" >
						 <% if (request.getParameter("tab").equals("15") && OnSession) {  %>
						 <jsp:include page="ReportePedido.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 15);
							</script>
						 
						<% }  %>						 
					</div>
					<div id="tabs-16" align="center" >
						 <% if (request.getParameter("tab").equals("16") && OnSession) {  %>
						 <jsp:include page="PedidoPropuestaConjunta.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 16);
							</script>
						 
						<% }  %>						 
					</div>
					<div id="tabs-17" align="center" >
						 <% if (request.getParameter("tab").equals("17") && OnSession) {  %>
						 <jsp:include page="AnticiposRetenciones.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 17);
							</script>
						 
						<% }  %>						 
					</div>
					<div id="tabs-18" align="center" >
						 <% if (request.getParameter("tab").equals("18") && OnSession) {  %>
						 <jsp:include page="ReclasificaTipoAdjudicacion.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 18);
							</script>
						 
						<% }  %>						 
					</div>
			</div>
	    </div>
	   	<input type="hidden" name="session" id="session" value="<%= OnSession %>" />
		<input type="hidden" name="lPedidoAbiertoCOntrato" id="lPedidoAbiertoCOntrato" />
		<input type="hidden" name="tbs" id="tbs" value = "0">
	</form>
  </body>
</html>
