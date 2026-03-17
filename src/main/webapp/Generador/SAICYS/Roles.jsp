<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	
	/*if (session.getAttribute(GestionInterface.ATT_ContratoEjercicio) != null) {
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
	}*/
	
	String sesion = "";
	
	if ((String)session.getAttribute(GestionInterface.ATT_ReqEjercicio) != null) // && !session.getAttribute(GestionInterface.ATT_ReqEjercicio).equals(""))
		sesion = "1";
		
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
    
    <title>Roles</title>
    
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
			$(".tabs").tabs();
			//$("#tabs-3").click();
			
			$("#aTab0").click(function() {
				window.location = "Roles.jsp?tab=" + 0;
			});
			$("#aTab1").click(function() {
				window.location = "Roles.jsp?tab=" + 1;
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
			if(event.keyCode==27){  //escape
				return false;
			}
			if(((event.srcElement.tagName.toUpperCase() != 'INPUT' && event.srcElement.tagName.toUpperCase() != 'TEXTAREA')
				|| document.getElementById(event.srcElement.id).style.readonly )	//backspace
				&& (event.keyCode==8 || event.keyCode==13)){					
				return false; //onkeydown="return checkShortcut()"
			}
		}
	</script>
  </head>
  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" >
  	<form> 
		<div id="container" class="container">	
			<br/>
			<br/>
			<h1>Roles<label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<br/>
			<div class="tabs" id="tabs" name="tabs">
				<ul>
					<li><a id="aTab0" href="#tabs-0" >Nuevo</a></li>
					<li><a id="aTab1" href="#tabs-1" >Control de acceso</a></li>
					
					
				</ul>
				<div id="tabs-0" align="center">
					<% if (request.getParameter("tab").equals("0")) { %>
						<jsp:include page="NuevoRol.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 0);
						</script>
					<% } %>
				</div>
				<div id="tabs-1" align="center" width="800">
					<% if (request.getParameter("tab").equals("1")) { %>
						<jsp:include page="controlAcceso.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 1);
						</script>
					<% } %>
				</div>
			</div>
	    </div>
	    <input type="hidden" name="session" id="session" value="<%= sesion %>" />
	</form>
  </body>
</html>
