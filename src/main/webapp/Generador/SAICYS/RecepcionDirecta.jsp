<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	String roles="";
	Map rol =usuario.getRoles();
	
	String cIdTipo = "";
	
	if (session.getAttribute(GestionInterface.ATT_ContratoEjercicio) != null) {
		session.setAttribute(GestionInterface.ATT_ContratoEjercicio,null);
		session.setAttribute(GestionInterface.ATT_ContratoTipoContrato,null);
		session.setAttribute(GestionInterface.ATT_ContratoUnidadEjec,null);
		session.setAttribute(GestionInterface.ATT_ContratoConsecutivo,null);
	}

	if (request.getParameter(GestionInterface.ATT_PedidoEjercicio) != null) {
		session.setAttribute(GestionInterface.ATT_PedidoEjercicio,null);
		session.setAttribute(GestionInterface.ATT_PedidoTipoPedido,null);
		session.setAttribute(GestionInterface.ATT_PedidoUnidadEjec,null);
		session.setAttribute(GestionInterface.ATT_PedidoConsecutivo,null);
	}
	
	if (GestionInterface.ATT_ConEjercicio != null) {
		session.setAttribute(GestionInterface.ATT_ConEjercicio, null);
		session.setAttribute(GestionInterface.ATT_ConTipoConsolidado, null);
		session.setAttribute(GestionInterface.ATT_ConUnidadEjec, null);
		session.setAttribute(GestionInterface.ATT_ConConsecutivo, null);
	}
	
	session.setAttribute(GestionInterface.ATT_CASE, null);
	//Esto se ejecuta cuando se selecciona una Req
	if (request.getParameter("cEjercicio") != null) { // && !request.getAttribute("cEjercicio").equals("")) {
		session.setAttribute(GestionInterface.ATT_ReqEjercicio, request.getParameter("cEjercicio").toString());
		session.setAttribute(GestionInterface.ATT_ReqTipoSolicitud, request.getParameter("cIdTipoSolicitud").toString());
		session.setAttribute(GestionInterface.ATT_ReqUnidadEjec, request.getParameter("cIdUnidadEjecutora").toString());
		session.setAttribute(GestionInterface.ATT_ReqConsecutivo, request.getParameter("nIdConsecutivo").toString());
		
		cIdTipo = request.getParameter("cIdTipoSolicitud").toString();
	}
	String sesion = "";
	if ((String)session.getAttribute(GestionInterface.ATT_ReqEjercicio) != null){ // && !session.getAttribute(GestionInterface.ATT_ReqEjercicio).equals(""))
		sesion = "1";
		cIdTipo = (String)session.getAttribute(GestionInterface.ATT_ReqTipoSolicitud);
	}
		
	if (request.getParameter("ses") != null) {
		session.setAttribute(GestionInterface.ATT_ReqEjercicio, null);
		session.setAttribute(GestionInterface.ATT_ReqTipoSolicitud, null);
		session.setAttribute(GestionInterface.ATT_ReqUnidadEjec, null);
		session.setAttribute(GestionInterface.ATT_ReqConsecutivo, null);
		sesion = "";
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    
    <title>Recepcion Directa</title>
    
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
			div#users-contain { width: 100%; margin: 20px 0; }
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
				Map pestanas=ebl.getPestana(roles,"Requisiciones");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
				}
				%> 
			$(".tabs").tabs();
			//$("#tabs-3").click();
			
			$("#NuevaRecepcionDirecta").click(function() {
				window.location = "RecepcionDirecta.jsp?tab=" + 0;
			});
			$("#ConsultaRecepcionDirecta").click(function() {
				window.location = "RecepcionDirecta.jsp?tab=" + 1;
			});
			$("#LineasRecepcionDirecta").click(function() {
				window.location = "RecepcionDirecta.jsp?tab=" + 2;
			});
			
			
			if ($("#session").val() == "") {
				$( "#LineasRecepcionDirecta" ).attr("disabled", true);
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
		function enviarPago(){

			queryFormPost({
				queryName: "autorizacionRecepcionDirecta",
				async: false,
				callback: function() {
					alert("La Recepcion fue autorizada puede realizar el pago de la recepcion: "+$("#cIdSolicitu").val());
					$("#cAccion").val("APRUEBA_RECEPCION_DIRECTA");
					$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
					$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
					window.location = "RecepcionDirecta.jsp?tab=1";
				}});
			
		}
						
		function checkShortcut()
		{			
			if(event.keyCode==27){  //escape
				return false;
			}
			if(((event.srcElement.tagName.toUpperCase() != 'INPUT' && event.srcElement.tagName.toUpperCase() != 'TEXTAREA')
				|| document.getElementById(event.srcElement.id).style.readonly )	//backspace
				&& (event.keyCode==8 || event.keyCode==13)){					
				return false;
			}
		}
	</script>
  </head>
  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" onkeydown="return checkShortcut()">
  	
		<div id="container" class="container" style="width: 90%;">	
			<br/>
			<br/>
			<h1>Recepcion Directa<label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<br/>
			<div class="tabs" id="tabs" name="tabs">
				<ul>
					<li><a id="NuevaRecepcionDirecta" href="#tabs-0" >Nuevo</a></li>
					<li><a id="ConsultaRecepcionDirecta" href="#tabs-1" >Consulta</a></li>
					<li><a id="LineasRecepcionDirecta" href="#tabs-2" >Car&aacute;tula</a></li>
				</ul>
				<div id="tabs-0" align="center">
					<% if (request.getParameter("tab").equals("0")) { %>
						<jsp:include page="NuevaRecepcionDirecta.jsp" />
						<script type="text/javascript" charset="utf-8">
								$( "#LineasRecepcionDirecta" ).attr("disabled", true);
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 0);
						</script>
					<% } %>
				</div>
				<div id="tabs-1" align="center" width="800">
					<% if (request.getParameter("tab").equals("1")) { %>
						<jsp:include page="ConsultaRecepcionDirecta.jsp" />
						<script type="text/javascript" charset="utf-8">
								$( "#LineasRecepcionDirecta" ).attr("disabled", true);
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 1);
						</script>
					<% } %>
				</div>
				<div id="tabs-2" align="center">
					<% if (request.getParameter("tab").equals("2")) { %>
						<jsp:include page="LineasRecepcionDirecta.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 2);
						</script>
					<% } %>
				</div>				
			</div>
	    </div>
	    <input type="hidden" name="session" id="session" value="<%= sesion %>" />
  </body>
</html>
