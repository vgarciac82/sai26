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
	
	/////////////////////////////////////////////
	if (session.getAttribute(GestionInterface.ATT_ReqEjercicio) != null ) {
		session.setAttribute(GestionInterface.ATT_ReqEjercicio, null);
		session.setAttribute(GestionInterface.ATT_ReqTipoSolicitud, null);
		session.setAttribute(GestionInterface.ATT_ReqUnidadEjec, null);
		session.setAttribute(GestionInterface.ATT_ReqConsecutivo, null);
	}
	
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
	/////////////////////////////////////////////
	
	String sesion = "";
	if (request.getParameter("cEjercicio") != null) { // && !request.getAttribute("cEjercicio").equals("")) {
		session.setAttribute(GestionInterface.ATT_ConEjercicio, request.getParameter("cEjercicio").toString());
		session.setAttribute(GestionInterface.ATT_ConTipoConsolidado, request.getParameter("cIdTipoConsolidado").toString());
		session.setAttribute(GestionInterface.ATT_ConUnidadEjec, request.getParameter("cIdUnidadEjecutora").toString());
		session.setAttribute(GestionInterface.ATT_ConConsecutivo, request.getParameter("nIdConsecutivo").toString());
	}
	if ((String)session.getAttribute(GestionInterface.ATT_ConEjercicio) != null) // && !session.getAttribute(GestionInterface.ATT_ReqEjercicio).equals(""))
		sesion = "1";
		
	if (request.getParameter("ses") != null) {
		session.setAttribute(GestionInterface.ATT_ConEjercicio, null);
		session.setAttribute(GestionInterface.ATT_ConTipoConsolidado, null);
		session.setAttribute(GestionInterface.ATT_ConUnidadEjec, null);
		session.setAttribute(GestionInterface.ATT_ConConsecutivo, null);
		sesion = "";
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    
    <title>Clausulas</title>
    
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
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
				}
				Map pestanas=ebl.getPestana(role,"Consolidado");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
				}
				%>
				
			$(".tabs").tabs();
			//$("#tabs-3").click();
			
			$("#Clausulas").click(function() {
				window.location = "Consolidado.jsp?tab=" + 0;
			});
			
			
			
		});
		
	</script>
  </head>
  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form> 
		<div id="container" class="container">	
			<br/>
			<br/>
			<h1>Clausulas</h1>
			<br/>
			<div class="tabs" id="tabs" name="tabs" >
				<ul>
					<li><a id="NuevoClausulado" href="#tabs-0">Nuevo</a></li>
				</ul>
				<div id="tabs-0" align="center">
					<% if (request.getParameter("tab").equals("0")) { %><!--
						 <jsp:include page="prueba.jsp" />
						--><script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 0);
						</script>
					<% } %>
				</div>
				
			
			</div>
	    </div>
	    <input type="hidden" name="session" id="session" value="<%= sesion %>" />
	</form>
  </body>
</html>
