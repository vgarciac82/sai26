<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuario.getLogin();
	String roles="";
	
	Map rol =usuario.getRoles();
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	
	
	
	Boolean OnSession=false;
	 if (request.getParameter("cEjercicio") != null) { 
		session.setAttribute(GestionInterface.ATT_PedidoEjercicioPasivo,request.getParameter("cEjercicio").toString());
		System.out.println(request.getParameter("cEjercicio").toString());
		session.setAttribute(GestionInterface.ATT_PedidoUnidadEjecPasivo, request.getParameter("cIdUnidadEjecutora").toString());
		session.setAttribute(GestionInterface.ATT_PedidoPasivo, request.getParameter("cIdPedido").toString());
		session.setAttribute(GestionInterface.ATT_PedidoPasivoDefinitivo, request.getParameter("cIdPedidoDefinitivo").toString());
		session.setAttribute(GestionInterface.ATT_tipoCambioPasivoPedido, request.getParameter("cIdTipoCambio").toString());
		session.setAttribute(GestionInterface. ATT_tipoContratoPasivoPedido, request.getParameter("cIdTipoPedido").toString());
		//session.setAttribute(GestionInterface. ATT_SubpartidaPasivo, request.getParameter("cIdSubpartida").toString());
		
		
		
	}
	 if ((String)session.getAttribute(GestionInterface.ATT_PedidoEjercicioPasivo) != null)
		OnSession = false;
	
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
				Map pestanas=ebl.getPestana(roles,"PasivoPedidos");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
				}
		%> 	
		
			$(".tabs").tabs();
			$("#nuevoPasivoPedido").click(function() {
				window.location = "PasivosPedido.jsp?tab=" + 0;
			});
			$("#consultaPasivoPedido").click(function() {
				window.location = "PasivosPedido.jsp?tab=" + 1;	
			});
			$("#presupuestoPasivoPedido").click(function() {
				window.location = "PasivosPedido.jsp?tab=" + 2;
			});
			$("#precompromisoPasivoPedido").click(function() {
				window.location = "PasivosPedido.jsp?tab=" + 3;
			});
			
			
			/*
			if ($("#session").val() == "false") {
				$( "#presupuestoPasivo" ).attr("disabled", true);
				$( "#precompromisoPasivo" ).attr("disabled", true);
				
			}
			*/
			
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
  	<form> 
		<div id="container" class="container">	
			<br/>
			<br/>
			<h1>Pasivos Pedido <label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<br/>
			<div class="tabs" id="tabs" name="tabs" >
				<ul>
					<li><a id="nuevoPasivoPedido" href="#tabs-0" >Nueva</a></li> 
					<li><a id="consultaPasivoPedido" href="#tabs-1" >Consulta</a></li>	
					<li><a id="presupuestoPasivoPedido" href="#tabs-2" >Presupuesto</a></li> 
					<li><a id="precompromisoPasivoPedido" href="#tabs-3" >Pre-compromiso</a></li>	
				
				</ul> 
				<% if (request.getParameter("tab")!= null){ %>
					<div id="tabs-0" align="center">
					 	<% if (request.getParameter("tab").equals("0")) {
					 		OnSession=false; %>
							<jsp:include page="NuevoPasivoPedido.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 0);
							</script>
						<% } %> 
					</div>
					<div id="tabs-1" align="center" >
						 <% if (request.getParameter("tab").equals("1")) { %>
							<jsp:include page="ConsultaPasivoPedido.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 1);
							</script>
						<% }  %>						 
					</div>
					<div id="tabs-2" align="center" >
						 <% if (request.getParameter("tab").equals("2")) { %>
							<jsp:include page="presupuestoPasivoPedido.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 2);
							</script>
						<% }  %>						 
					</div>
					<div id="tabs-3" align="center" >
						 <% if (request.getParameter("tab").equals("3")) { %>
							<jsp:include page="precompromisoPasivoPedido.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 3);
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
