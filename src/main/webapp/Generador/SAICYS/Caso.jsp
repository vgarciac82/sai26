<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%




String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuario.getLogin();
    String roles="";
    String cIdTipo="";
    String cEjercicio="";
    String nIdUnidadEjecutora="";
    String nIdTipoConsolidado="";
    String nIdConsecutivo="";
    Map rol =usuario.getRoles();
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}

	String idCaso = (String)request.getParameter("idCaso");

	String sesion = "";

	if (request.getParameter("cEjercicio") != null) { // && !request.getAttribute("cEjercicio").equals("")) {
		session.setAttribute(GestionInterface.ATT_ConEjercicio, request.getParameter("cEjercicio").toString());
		session.setAttribute(GestionInterface.ATT_ConTipoConsolidado, request.getParameter("cIdTipoConsolidado").toString());
		session.setAttribute(GestionInterface.ATT_ConUnidadEjec, request.getParameter("cIdUnidadEjecutora").toString());
		session.setAttribute(GestionInterface.ATT_ConConsecutivo, request.getParameter("nIdConsecutivo").toString());
	}
	if ((String)session.getAttribute(GestionInterface.ATT_ConEjercicio) != null){
	 // && !session.getAttribute(GestionInterface.ATT_ReqEjercicio).equals(""))
		sesion = "1";
		cIdTipo = (String)session.getAttribute(GestionInterface.ATT_ConTipoConsolidado);
		cEjercicio =(String)session.getAttribute(GestionInterface.ATT_ConEjercicio);
		nIdUnidadEjecutora= (String)session.getAttribute(GestionInterface.ATT_ConUnidadEjec);
		nIdTipoConsolidado=(String)session.getAttribute(GestionInterface.ATT_ConTipoConsolidado);
		nIdConsecutivo=(String)session.getAttribute(GestionInterface.ATT_ConConsecutivo);
		System.out.print(nIdUnidadEjecutora);
	}
		
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
    
    <title>Caso</title>
    
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
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map pestanas=ebl.getPestana(roles,"Consolidado");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
				}
				%>
				
			
			$(".tabs").tabs();
			//$("#tabs-3").click();
			
			$("#DatosCaso").click(function() {
				window.location = "Caso.jsp?idCaso=<%=idCaso%>&tab=0";
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
			<h1>Ampliaci&oacute;n de Vigencia<label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<br/>
			<div class="tabs" id="tabs" name="tabs" >
				<ul>
					<li><a id="DatosCaso" href="#tabs-0">Datos del Precompromiso</a></li>
					
				</ul>
				<div id="tabs-0" align="center">
					<% if (request.getParameter("tab").equals("0")) { %>
						<jsp:include page="DatosCaso.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 0);
						</script>
					<% } %>
				</div>
				
		</div>
		<input type="hidden" name="session" id="session" value="<%= sesion %>" />
	    <input type="hidden" name="nIdEstado" id="nIdEstado"/>
	    <input type="hidden" name="cEjercicio" id="cEjercicio"/>
	    <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora"/>
	    <input type="hidden" name="cIdTipoConsolidado" id="cIdTipoConsolidado" />
	    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" />
	    <input type="hidden" name="idCaso" id="idCaso" value="<%=idCaso %>" />
	    </div> 
	    
	    
	</form> 
  </body>
</html>
