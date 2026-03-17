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
	}
		
	if ((String)session.getAttribute(GestionInterface.ATT_ConEjercicio) == null) {
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
    
    <title>Consolidado</title>
    
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
  	<script type="text/javascript" src="../js/Consolidado.js"></script>
	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
  	<script type="text/javascript" src="../../js/jquery.blockUI-2.4.2.js"></script>
  	<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.all.js"></script>
	
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
					$("#<%=e.getValue()%>").attr("disabled", true);
					<%
				}
			
			%>
			$(".tabs").tabs();

			$("#NuevoAutomaticoConsolidado").click(function() {
				window.location = "Consolidado.jsp?tab=" + 0;
			});
			$("#NuevoConsolidado").click(function() {
				window.location = "Consolidado.jsp?tab=" + 1;
			});
			$("#ConsultaConsolidado").click(function() {
				window.location = "Consolidado.jsp?tab=" + 2;
			});
			$("#CaratulaConsolidado").click(function() {
				window.location = "Consolidado.jsp?tab=" + 3;
			});
			
			$("#PreseleccionConsolidado").click(function() {
				window.location = "Consolidado.jsp?tab=" + 4;
			});
			 
			$("#PartidasConsolidado").click(function() {
				window.location = "Consolidado.jsp?tab=" + 5;
			}); 

			$("#presupuestoConsolidado").click(function() {
				window.location = "Consolidado.jsp?tab=" + 6;
			}); 
			$("#preCompromisoConsolidado").click(function() {
				window.location = "Consolidado.jsp?tab=" + 7;
			}); 
			
			$("#ampliacionPrecompromiso").click(function() {
				window.location = "Consolidado.jsp?tab=" + 8;
			}); 
			
			$("#vigenciaRequisicionesConsolidadas").click(function() {
				window.location = "Consolidado.jsp?tab=" +9;
			}); 
			
			$("#cIdUnidadEjecutora").val("<%=nIdUnidadEjecutora%>");
			$("#cEjercicio").val("<%=cEjercicio%>");
			$("#cIdTipoConsolidado").val("<%=nIdTipoConsolidado%>");
			$("#nIdConsecutivo").val("<%=nIdConsecutivo%>");
			 
			if ($("#session").val() == "") {
				$( "#CaratulaConsolidado" ).attr("disabled", true);
				$( "#PreseleccionConsolidado" ).attr("disabled", true);
				$( "#PartidasConsolidado" ).attr("disabled", true);
				$( "#presupuestoConsolidado" ).attr("disabled", true);
				$( "#preCompromisoConsolidado" ).attr("disabled", true);
				$( "#ampliacionPrecompromiso" ).attr("disabled", true);
				$( "#vigenciaRequisicionesConsolidadas" ).attr("disabled", true);
			}
			$(window).bind('resize', function (){
				resizeDt();
			});
			showAndHideTabs();
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
	</script>
  </head>
  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" onkeydown="return checkShortcut()">
  	<form> 
		<div id="container" class="container" style="width: 90%;">
			<br/>
			<br/>
			<h1>Consolidado<label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<br/>
			<div class="tabs" id="tabs" >
				<ul>
					<li><a id="NuevoAutomaticoConsolidado" href="#tabs-0">Nuevo Autom&aacute;tico</a></li>
					<li><a id="NuevoConsolidado" href="#tabs-1">Nuevo</a></li>
					<li><a id="ConsultaConsolidado" href="#tabs-2">Consulta</a></li>
					<li><a id="CaratulaConsolidado" href="#tabs-3">Car&aacute;tula</a></li>
					<li><a id="PreseleccionConsolidado" href="#tabs-4">PreSelecci&oacute;n</a></li>
					<li><a id="PartidasConsolidado" href="#tabs-5">Partidas</a></li>
					<li><a id="presupuestoConsolidado" href="#tabs-6">Presupuesto</a></li>
					<li><a id="preCompromisoConsolidado" href="#tabs-7">PreCompromiso</a></li>
					<li><a id="ampliacionPrecompromiso" href="#tabs-8">Ampliación vigencia</a></li>
					<li><a id="vigenciaRequisicionesConsolidadas" href="#tabs-9">Ampliación vigencia de Requisiciones</a></li>
					
				</ul>
				<div id="tabs-0" align="center">
					<% if (request.getParameter("tab").equals("0")) { 
					sesion=""; %>
						<jsp:include page="NuevoAutomaticoConsolidado.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 0);
						</script>
					<% } %>
				</div>
				<div id="tabs-1" align="center" width="800">
					<% if (request.getParameter("tab").equals("1")) {
					sesion=""; %>
						<jsp:include page="NuevoConsolidado.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 1);
						</script>
					<% } %>
				</div>
				<div id="tabs-2" align="center">
					<% if (request.getParameter("tab").equals("2")) {
					sesion=""; %>
						<jsp:include page="ConsultaConsolidado.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 2);
						</script>
					<% } %>
				</div>
				<div id="tabs-3" align="center">
					<% if (request.getParameter("tab").equals("3") && sesion=="1") { %>
						<jsp:include page="CaratulaConsolidado.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 3);
						</script>
					<% } %>
				</div>
				<div id="tabs-4" align="center">
					<% if (request.getParameter("tab").equals("4")  && sesion=="1") { %>
						<jsp:include page="PreseleccionConsolidado.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 4);
						</script>
					<% } %>
				</div>
				<div id="tabs-5" align="center">
					<% if (request.getParameter("tab").equals("5")  && sesion=="1") { %>
						<jsp:include page="PartidasConsolidado.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 5);
						</script>
					<% } %>
				</div>
						
				<div id="tabs-6" align="center">
					<% if (request.getParameter("tab").equals("6")  && sesion=="1") { %>
						<jsp:include page="presupuestoConsolidado.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 6);
						</script>
					<% } %>
				</div>
				
				<div id="tabs-7" align="center">
					<% if (request.getParameter("tab").equals("7")  && sesion=="1") { %>
						<jsp:include page="preCompromisoConsolidado.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 7);
						</script>
					<% } %>
				</div>
				
				<div id="tabs-8" align="center">
					<% if (request.getParameter("tab").equals("8")  && sesion=="1") { %>
						<jsp:include page="AmpliacionVigenciaConsolidado.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 8);
						</script>
					<% } %>
				</div>
				
				<div id="tabs-9" align="center">
					<% if (request.getParameter("tab").equals("9")  && sesion=="1") { %>
						<jsp:include page="AmpliacionVigenciaRequisiciones.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 9);
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
	    <input type="hidden" name="tbs" id="tbs" value = "0">
	    </div> 
	</form> 
  </body>
</html>
