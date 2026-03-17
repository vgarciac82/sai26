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
	String sesion = "";
	session.setAttribute(GestionInterface.ATT_CASE, null);
	//Esto se ejecuta cuando se selecciona una Req
	if (request.getParameter("cEjercicio") != null) { // && !request.getAttribute("cEjercicio").equals("")) {
		System.out.println("cEjercicio: "+request.getParameter("cEjercicio") );
		session.setAttribute(GestionInterface.ATT_ReqEjercicio, request.getParameter("cEjercicio").toString());
		session.setAttribute(GestionInterface.ATT_ReqTipoSolicitud, request.getParameter("cIdTipoSolicitud").toString());
		session.setAttribute(GestionInterface.ATT_ReqUnidadEjec, request.getParameter("cIdUnidadEjecutora").toString());
		session.setAttribute(GestionInterface.ATT_ReqConsecutivo, request.getParameter("nIdConsecutivo").toString());
		
		cIdTipo = request.getParameter("cIdTipoSolicitud").toString();
		sesion = "1";
	}
	if ((String)session.getAttribute(GestionInterface.ATT_ReqEjercicio) != null){ // && !session.getAttribute(GestionInterface.ATT_ReqEjercicio).equals(""))
		sesion = "1";
		cIdTipo = (String)session.getAttribute(GestionInterface.ATT_ReqTipoSolicitud);
	}else{
		session.setAttribute(GestionInterface.ATT_ReqEjercicio, null);
		session.setAttribute(GestionInterface.ATT_ReqTipoSolicitud, null);
		session.setAttribute(GestionInterface.ATT_ReqUnidadEjec, null);
		session.setAttribute(GestionInterface.ATT_ReqConsecutivo, null);
		sesion = "";
	}
	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>Requisiciones</title>
    
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
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script> 
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script> 
	<script type="text/javascript" src="../../js/utils/syctools.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>
	<script type="text/javascript" src="../js/Requisiciones.js"></script>
	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>

	<script type="text/javascript" charset="utf-8">
		var globalApartado = false;
		var cIdTipo="<%=cIdTipo%>";
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
			
			$("#NuevoRequisiciones").click(function() {
				$("#tbs").val(0);
				if(!globalApartado) window.location = "Requisiciones.jsp?tab=" + 0;
			});
			$("#ConsultaRequisiciones").click(function() {
				$("#tbs").val(1);
				if(!globalApartado) window.location = "Requisiciones.jsp?tab=" + 1;
			});
			$("#CaratulaRequisiciones").click(function() {
				$("#tbs").val(2);
				if(!globalApartado) window.location = "Requisiciones.jsp?tab=" + 2;
			});
			$("#LineasRequisiciones").click(function() {
				$("#tbs").val(3);
				if(!globalApartado) window.location = "Requisiciones.jsp?tab=" + 3;
			});
			$("#FirmantesRequisiciones").click(function() {
				$("#tbs").val(4);
				if(!globalApartado) window.location = "Requisiciones.jsp?tab=" + 4;
			});
			$("#PresupuestoRequisiciones").click(function() {
				$("#tbs").val(5);
				if(!globalApartado) window.location = "Requisiciones.jsp?tab=" + 5;
			});
			$("#ApartadoRequisiciones").click(function() {
				$("#tbs").val(6);
				if(!globalApartado) window.location = "Requisiciones.jsp?tab=" + 6;
			});
			$(window).bind('resize', function (){
				resizeDt();
			});
			var apartables = [ "RC", "RM", "RS","RT" ]; //Agregar está linea para activar el apartado

			if ($.inArray("<%=cIdTipo%>", apartables) == -1){
				$( "#PresupuestoRequisiciones" ).hide();
				$( "#ApartadoRequisiciones" ).hide();
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
		
		function checkShortcut(){			
			if(event.keyCode==27){  //escape
				return false;
			}
			if(((event.srcElement.tagName.toUpperCase() != 'INPUT' && event.srcElement.tagName.toUpperCase() != 'TEXTAREA'))	
				&& (event.keyCode==8 || event.keyCode==13)){					
				return false;
			}
		}
		
		function rechazaProceso() {
			swal("Requisición Rechazada.",{icon:"warning",button: "Cerrar"});
			$( "#dlgQuestionnaire" ).dialog("close");
		}
	</script>
  </head>
  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" onkeydown="return checkShortcut()">
  	
		<div id="container" class="container" style="width: 100%;">	
			<br/>
			<br/>
			<h1>Requisiciones<label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<br/>
			<div class="tabs" id="tabs" name="tabs">
				<ul>
					<li><a id="NuevoRequisiciones" href="#tabs-0" >Nuevo</a></li>
					<li><a id="ConsultaRequisiciones" href="#tabs-1" >Consulta</a></li>
					<li><a id="CaratulaRequisiciones" href="#tabs-2" >Car&aacute;tula</a></li>
					<li><a id="LineasRequisiciones" href="#tabs-3" >L&iacute;neas</a></li>
					<li><a id="FirmantesRequisiciones" href="#tabs-4" >Firmantes</a></li>
					<li><a id="PresupuestoRequisiciones" href="#tabs-5" >Presupuesto</a></li>
					<li><a id="ApartadoRequisiciones" href="#tabs-6" >Apartado</a></li>
				</ul>
				<div id="tabs-0" align="center">
					<% if (request.getParameter("tab").equals("0")) { %>
						<jsp:include page="NuevaRequisicion.jsp" />
						<script type="text/javascript" charset="utf-8">
								$( "#CaratulaRequisiciones" ).attr("disabled", true);
								$( "#LineasRequisiciones" ).attr("disabled", true);
								$( "#FirmantesRequisiciones" ).attr("disabled", true);
								$( "#PresupuestoRequisiciones" ).hide();
								$( "#ApartadoRequisiciones" ).hide();
								$("#tbs").val(0);
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 0);
						</script>
					<% } %>
				</div>
				<div id="tabs-1" align="center">
					<% if (request.getParameter("tab").equals("1")) { %>
						<jsp:include page="ConsultaRequisiciones.jsp" />
						<script type="text/javascript" charset="utf-8">
							$("#tbs").val(1);	
							$( "#CaratulaRequisiciones" ).attr("disabled", true);
							$( "#LineasRequisiciones" ).attr("disabled", true);
							$( "#FirmantesRequisiciones" ).attr("disabled", true);
							$( "#PresupuestoRequisiciones" ).hide();
							$( "#ApartadoRequisiciones" ).hide();
							var $tabs = $(".tabs").tabs();
							$tabs.tabs('select', 1);
						</script>
					<% } %>
				</div>
				<div id="tabs-2" align="center">
					<% if (request.getParameter("tab").equals("2")) { %>
						<jsp:include page="EdicionCaratula.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 2);
								$("#tbs").val(2);
						</script>
					<% } %>
				</div>
				<div id="tabs-3" align="center">
					<% if (request.getParameter("tab").equals("3")) { %>
						<jsp:include page="LineasSolicitud.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 3);
								$("#tbs").val(3);
						</script>
					<% } %>
				</div>
				<div id="tabs-4" align="center">
					<% if (request.getParameter("tab").equals("4")) { %>
						<jsp:include page="FirmantesRequisicion.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 4);
								$("#tbs").val(4);
						</script>
					<% } %>
				</div>
				<div id="tabs-5" align="center">
					<% if (request.getParameter("tab").equals("5")) { %>
						<jsp:include page="PresupuestoRequisicion.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 5);
								$("#tbs").val(5);
						</script>
					<% } %>
				</div>
				<div id="tabs-6" align="center">
					<% if (request.getParameter("tab").equals("6")) { %>
						<jsp:include page="ApartadoRequisicion.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 6);
								$("#tbs").val(6);
						</script>
					<% } %>
				</div>
			</div>
	    </div>
	    <input type="hidden" name="session" id="session" value="<%= sesion %>" />
	    <input type="hidden" name="tbs" id="tbs" value = "">
  </body>
</html>
