<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuario.getLogin();
	String roles="";
	
	Map<String,Role> rol =usuario.getRoles();
	
	Boolean OnSession=false;
	 if (request.getParameter("cEjercicio") != null) { 
		session.setAttribute(GestionInterface.ATT_ContratoEjercicioPasivo,request.getParameter("cEjercicio").toString());
		System.out.println(request.getParameter("cEjercicio").toString());
		session.setAttribute(GestionInterface.ATT_ContratoUnidadEjecPasivo, request.getParameter("cIdUnidadEjecutora").toString());
		session.setAttribute(GestionInterface.ATT_ContratoPasivo, request.getParameter("cIdContrato").toString());
		session.setAttribute(GestionInterface.ATT_ContratoPasivoDefinitivo, request.getParameter("cIdContratoDefinitivo").toString());
		session.setAttribute(GestionInterface.ATT_tipoCambioPasivo, request.getParameter("cIdTipoCambio").toString());
		session.setAttribute(GestionInterface. ATT_tipoContratoPasivo, request.getParameter("cIdTipoContrato").toString());
		//session.setAttribute(GestionInterface. ATT_SubpartidaPasivo, request.getParameter("cIdSubpartida").toString());
		
	
		
	}
	 if ((String)session.getAttribute(GestionInterface.ATT_ContratoEjercicioPasivo) != null)
		OnSession = false;
	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Pasivos Contratos</title>
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
				Map pestanas=ebl.getPestana(roles,"PasivoContrato");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
				}
		%> 	 
	
			$(".tabs").tabs(); 
			$("#nuevoPasivoContrato").click(function() {
				window.location = "Pasivos.jsp?tab=" + 0;
			});
			$("#consultaPasivoContrato").click(function() {
				window.location = "Pasivos.jsp?tab=" + 1;	
			});
			$("#presupuestoPasivoContrato").click(function() {
				window.location = "Pasivos.jsp?tab=" + 2;
			});
			$("#precompromisoPasivoContrato").click(function() { 
				window.location = "Pasivos.jsp?tab=" + 3; 
			});
			
			
		
			if ($("#session").val() == "false") {
				$( "#presupuestoPasivo" ).attr("disabled", true);
				$( "#precompromisoPasivo" ).attr("disabled", true);
				
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
  	<form> 
		<div id="container" class="container">	
			<br/>
			<br/>
			<h1>Pasivos Contratos <label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<br/>
			<div class="tabs" id="tabs" name="tabs" >
				<ul>
					<li><a id="nuevoPasivoContrato" href="#tabs-0" >Nueva</a></li> 
					<li><a id="consultaPasivoContrato" href="#tabs-1" >Consulta</a></li>	
					<li><a id="presupuestoPasivoContrato" href="#tabs-2" >Presupuesto</a></li> 
					<li><a id="precompromisoPasivoContrato" href="#tabs-3" >Pre-compromiso</a></li>	
				
				</ul> 
				<% if (request.getParameter("tab")!= null){ %>
					<div id="tabs-0" align="center">
					 	<% if (request.getParameter("tab").equals("0")) {
					 		OnSession=false; %>
							<jsp:include page="NuevoPasivo.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 0);
							</script>
						<% } %> 
					</div>
					<div id="tabs-1" align="center" >
						 <% if (request.getParameter("tab").equals("1")) { %>
							<jsp:include page="ConsultaPasivo.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 1);
							</script>
						<% }  %>						 
					</div>
					<div id="tabs-2" align="center" >
						 <% if (request.getParameter("tab").equals("2")) { %>
							<jsp:include page="presupuestoPasivo.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 2);
							</script>
						<% }  %>						 
					</div>
					<div id="tabs-3" align="center" >
						 <% if (request.getParameter("tab").equals("3")) { %>
							<jsp:include page="precompromisoPasivo.jsp" />
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
