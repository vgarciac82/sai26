<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	//String path = request.getContextPath();
	//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if(usuario==null){
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
    String roles="";
    String cIdTipo="";
    String cEjercicio="";
    String nIdUnidadEjecutora="";
    String nIdTipoProcedimiento="";
    String nIdConsecutivo="";
    Map<String, Role> rol =usuario.getRoles();
	
	
	Boolean sesion=false;
	if (request.getParameter("cEjercicio") != null) {
		// // && !request.getAttribute("cEjercicio").equals("")) {
		session.setAttribute(GestionInterface.ATT_ProEjercicio, request.getParameter("cEjercicio").toString());
		session.setAttribute(GestionInterface.ATT_ProTipoProcedimiento, request.getParameter("cIdTipoProcedimiento").toString());
		session.setAttribute(GestionInterface.ATT_ProUnidadEjecutora, request.getParameter("cIdUnidadEjecutora").toString());
		session.setAttribute(GestionInterface.ATT_ProConsecutivo, request.getParameter("nIdConsecutivo").toString());
	}

	if ((String)session.getAttribute(GestionInterface.ATT_ProEjercicio) != null){
		sesion = true;
		cIdTipo = (String)session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento);
		cEjercicio =(String)session.getAttribute(GestionInterface.ATT_ProEjercicio);
		nIdUnidadEjecutora= (String)session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora);
		nIdTipoProcedimiento=(String)session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento);
		nIdConsecutivo=(String)session.getAttribute(GestionInterface.ATT_ProConsecutivo);
		
	}
		
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Procedimiento</title>
	    <meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Procedimiento">
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
	  	<script type="text/javascript" src="../js/Procedimiento.js"></script>
		<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	  	<script type="text/javascript" src="../../js/jquery.blockUI-2.4.2.js"></script>
	  	<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.all.js"></script>	
		<script type="text/javascript" charset="utf-8">
		
		//-----------------------------CARGA DEL DOCUMENTO------------------------
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
				Map pestanas=ebl.getPestana(roles,"Procedimiento");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
				}
				%>
				
			$(".tabs").tabs();
			//$("#tabs-3").click();
			
			$("#NuevoProcedimiento").click(function() {
				window.location = "Procedimiento-copia.jsp?tab=" + 0;
			});
			$("#ConsultaProcedimiento").click(function() {
				window.location = "Procedimiento-copia.jsp?tab=" + 1;
			});
			$("#CaratulaProcedimiento").click(function() {
				window.location = "Procedimiento-copia.jsp?tab=" + 2;
			});
			$("#ProveedoresProcedimiento").click(function() {
				window.location = "Procedimiento-copia.jsp?tab=" + 3;
			});
			
			$("#CotizacionProcedimiento").click(function() {
				window.location = "Procedimiento-copia.jsp?tab=" + 4;
			});
			
			$("#PreguntasProcedimiento").click(function() {
				window.location = "Procedimiento-copia.jsp?tab=" + 5;
			});
			
			$("#DocumentosProcedimiento").click(function() {
				window.location = "Procedimiento-copia.jsp?tab=" + 6;
			}); 
			
			$("#EvaluacionProcedimiento").click(function() {
				window.location = "Procedimiento-copia.jsp?tab=" + 7;
			}); 
			
			$("#RequisitosProcedimiento").click(function() {
				window.location = "Procedimiento-copia.jsp?tab=" + 8;
			});  
			
			$("#ArchivosProcedimiento").click(function() {
				window.location = "Procedimiento-copia.jsp?tab=" + 9;
			}); 
			
			$("#presupuestoProcedimiento").click(function() {
				window.location = "Procedimiento-copia.jsp?tab=" + 10;
			}); 
			
			$("#precompromisoProcedimiento").click(function() {
				window.location = "Procedimiento-copia.jsp?tab=" + 11;
			}); 
			
			$("#AmpliacionVigenciaProcedimiento").click(function() {
				window.location = "Procedimiento-copia.jsp?tab=" + 12;
			}); 
			$("#PosiblesContratantes").click(function() {
				window.location = "Procedimiento-copia.jsp?tab=" + 13;
			}); 
			$("#AsistenciaJuntaAclaracionesProced").click(function() {
				window.location = "Procedimiento-copia.jsp?tab=" + 14;
			}); 
			$("#ServidoresPublicosProcedimiento").click(function() {
				window.location = "Procedimiento-copia.jsp?tab=" + 15;
			});
			
			$("#cIdUnidadEjecutora").val("<%=nIdUnidadEjecutora%>");
			$("#cEjercicio").val("<%=cEjercicio%>");
			$("#cIdTipoProcedimiento").val("<%=nIdTipoProcedimiento%>");
			$("#nIdConsecutivo").val("<%=nIdConsecutivo%>");
			
			if ($("#session").val() == "false") {
				$( "#CaratulaProcedimiento" ).attr("disabled", true);
				$( "#ProveedoresProcedimiento" ).attr("disabled", true);
				$( "#RequisitosProcedimiento" ).attr("disabled", true);
				$( "#CotizacionProcedimiento" ).attr("disabled", true);
				$( "#DocumentosProcedimiento" ).attr("disabled", true);
				$( "#EvaluacionProcedimiento" ).attr("disabled", true);
				$( "#PreguntasProcedimiento" ).attr("disabled", true);
				$( "#ArchivosProcedimiento" ).attr("disabled", true);
				$( "#presupuestoProcedimiento" ).attr("disabled", true);
				$( "#precompromisoProcedimiento" ).attr("disabled", true);
				$( "#AmpliacionVigenciaProcedimiento" ).attr("disabled", true);
				$( "#PosiblesContratantes" ).attr("disabled", true);
				$( "#AsistenciaJuntaAclaracionesProced" ).attr("disabled", true);
				$( "#ServidoresPublicosProcedimiento" ).attr("disabled", true);
				
				
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
			<h1>Procedimiento<label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<br/>
			<div class="tabs" id="tabs" >
				<ul>
						<li><a id="NuevoProcedimiento" href="#tabs-0">Nuevo</a></li>
						<li><a id="ConsultaProcedimiento" href="#tabs-1">Consultar</a></li>
						<li><a id="CaratulaProcedimiento" href="#tabs-2">Car&aacute;tula</a></li>
						<li><a id="ProveedoresProcedimiento" href="#tabs-3">Proveedores</a></li>
						<li><a id="CotizacionProcedimiento" href="#tabs-4">Cotizaci&oacute;n</a></li>
						<li><a id="PreguntasProcedimiento" href="#tabs-5">Preguntas</a></li>
						<li><a id="DocumentosProcedimiento" href="#tabs-6">Documentos</a></li>
						<li><a id="EvaluacionProcedimiento" href="#tabs-7">Evaluaci&oacute;n</a></li>
						<li><a id="RequisitosProcedimiento" href="#tabs-8">Requisitos</a></li>	
						<li><a id="ArchivosProcedimiento" href="#tabs-9">Actas Archivos</a></li>
						<li><a id="presupuestoProcedimiento" href="#tabs-10">Presupuesto</a></li>
						<li><a id="precompromisoProcedimiento" href="#tabs-11">Precompromiso</a></li>
						<li><a id="AmpliacionVigenciaProcedimiento" href="#tabs-12">Ampliación vigencia</a></li>
						<li><a id="PosiblesContratantes" href="#tabs-13">Posibles contratantes</a></li>
						<li><a id="AsistenciaJuntaAclaracionesProced" href="#tabs-14">Junta de Aclaraciones</a></li>
						<li><a id="ServidoresPublicosProcedimiento" href="#tabs-15">Servidores P&uacute;blicos</a></li>
						
				</ul>	  
				<div id="tabs-0" align="center">
					<% if (request.getParameter("tab").equals("0")) { 
						sesion=false;%>
						<jsp:include page="NuevoProcedimiento.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 0);
						</script>
					<% } %>
				</div>
				<div id="tabs-1" align="center">
					<% if (request.getParameter("tab").equals("1")) { 
					sesion=false;%>
						<jsp:include page="ConsultaProcedimiento.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 1);
						</script>
					<% } %>
				</div>
				<div id="tabs-2" align="center">
					<% if (request.getParameter("tab").equals("2") && sesion==true) { %>
						<jsp:include page="CaratulaProcedimiento.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 2);
						</script>
					<% } %>
				</div>
				<div id="tabs-3" align="center">
					<% if (request.getParameter("tab").equals("3") && sesion==true) { %>
						<jsp:include page="ProveedoresProcedimiento.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 3);
						</script>
					<% } %>
				</div>
				<div id="tabs-4" align="center">
					<% if (request.getParameter("tab").equals("4") && sesion==true) { %>
						<jsp:include page="CotizacionProcedimiento.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 4);
						</script>
					<% } %>
				</div>
				<div id="tabs-5" align="center">
					<% if (request.getParameter("tab").equals("5") && sesion==true) { %>
						<jsp:include page="PreguntasProcedimiento.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 5);
						</script>
					<% } %>
				</div>
				
				<div id="tabs-6" align="center">
					<% if (request.getParameter("tab").equals("6") && sesion==true) { %>
						<jsp:include page="DocumentosProcedimiento.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 6);
						</script>
					<% } %>
				</div>
				
				<div id="tabs-7" align="center">
					<% if (request.getParameter("tab").equals("7") && sesion==true) { %>
						<jsp:include page="EvaluacionProcedimiento.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 7);
						</script>
					<% } %>
				</div>
				
				<div id="tabs-8" align="center">
					<% if (request.getParameter("tab").equals("8") && sesion==true) { %>
						<jsp:include page="RequisitosProcedimiento.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 8);
						</script>
					<% } %>
				</div>
				<div id="tabs-9" align="center">
					<% if (request.getParameter("tab").equals("9") && sesion==true) { %>
						<jsp:include page="ProcedimientoFile.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 9);
						</script>
					<% } %>
				</div>
				<div id="tabs-10" align="center">
					<% if (request.getParameter("tab").equals("10") && sesion==true ) { %>
						<jsp:include page="PresupuestoProcedimiento.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 10);
						</script>
					<% } %>
				</div>
				<div id="tabs-11" align="center">
					<% if (request.getParameter("tab").equals("11") && sesion==true) { %>
						<jsp:include page="PrecompromisoProcedimiento.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 11);
						</script>
					<% } %>
				</div>
				<div id="tabs-12" align="center">
				<% if (request.getParameter("tab").equals("12") && sesion==true) { %>
					<jsp:include page="AmpliacionVigenciaProcedimiento.jsp" />
					<script type="text/javascript" charset="utf-8">
							var $tabs = $(".tabs").tabs();
							$tabs.tabs('select', 12);
					</script>
				<% } %>
				</div>
				<div id="tabs-13" align="center">
				<% if (request.getParameter("tab").equals("13") && sesion==true) { %>
					<jsp:include page="PosiblesContratantesProcedimiento.jsp" />
					<script type="text/javascript" charset="utf-8">
							var $tabs = $(".tabs").tabs();
							$tabs.tabs('select', 13);
					</script>
				<% } %>
				</div>
				<div id="tabs-14" align="center">
				<% if (request.getParameter("tab").equals("14") && sesion==true) { %>
					<jsp:include page="AsistenciaJuntaAclaracionesProced.jsp" />
					<script type="text/javascript" charset="utf-8">
							var $tabs = $(".tabs").tabs();
							$tabs.tabs('select', 14);
					</script>
				<% } %>
				</div>
				<div id="tabs-15" align="center">
				<% if (request.getParameter("tab").equals("15") && sesion==true) { %>
					<jsp:include page="ServidoresPublicosProcedimiento.jsp" />
					<script type="text/javascript" charset="utf-8">
							var $tabs = $(".tabs").tabs();
							$tabs.tabs('select', 15);
					</script>
				<% } %>
				</div>
			</div>
		<input type="hidden" name="session" id="session" value="<%= sesion %>" />
	    <input type="hidden" name="nIdEstado" id="nIdEstado"/>
	    <input type="hidden" name="cEjercicio" id="cEjercicio"/>
	    <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora"/>
	    <input type="hidden" name="cIdTipoConsolidado" id="cIdTipoConsolidado" />
	    <input id="cIdTipoProcedimiento" name="cIdTipoProcedimiento" type="hidden" size="4" >
	    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" />
	    <input type="hidden" name="tbs" id="tbs" value = "0">
	    </div> 
	    
	    
	</form> 
  </body>
</html>
