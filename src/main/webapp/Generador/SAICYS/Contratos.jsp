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
	
	if(session.getAttribute(GestionInterface.ATT_ContratoAbierto) == null)
		session.setAttribute(GestionInterface.ATT_ContratoAbierto,0);
	if(request.getParameter("lContratoAbierto") != null)
		session.setAttribute(GestionInterface.ATT_ContratoAbierto,Integer.parseInt(request.getParameter("lContratoAbierto").toString()));
	
	if(session.getAttribute(GestionInterface.ATT_EstadoContrato) == null)
		session.setAttribute(GestionInterface.ATT_EstadoContrato,0);
	if(request.getParameter("nIdEstadoContrato") != null)
		session.setAttribute(GestionInterface.ATT_EstadoContrato,Integer.parseInt(request.getParameter("nIdEstadoContrato").toString()));
	
	if (session.getAttribute(GestionInterface.ATT_ReqEjercicio) != null ) {
	    session.setAttribute(GestionInterface.ATT_ReqEjercicio, null);
		session.setAttribute(GestionInterface.ATT_ReqTipoSolicitud, null);
		session.setAttribute(GestionInterface.ATT_ReqUnidadEjec, null);
		session.setAttribute(GestionInterface.ATT_ReqConsecutivo, null);
	}
	if (request.getParameter(GestionInterface.ATT_PedidoEjercicio) != null) { 
		session.setAttribute(GestionInterface.ATT_PedidoEjercicio,null);
		session.setAttribute(GestionInterface.ATT_PedidoTipoPedido, null);
		session.setAttribute(GestionInterface.ATT_PedidoUnidadEjec, null);
		session.setAttribute(GestionInterface.ATT_PedidoConsecutivo, null);
	}
	Boolean OnSession=false;
	if (request.getParameter("cEjercicio") != null) { 
		session.setAttribute(GestionInterface.ATT_ContratoEjercicio, request.getParameter("cEjercicio").toString());
		session.setAttribute(GestionInterface.ATT_ContratoTipoContrato, request.getParameter("cIdTipoContrato").toString());
		session.setAttribute(GestionInterface.ATT_ContratoUnidadEjec, request.getParameter("cIdUnidadEjecutora").toString());
		session.setAttribute(GestionInterface.ATT_ContratoConsecutivo, request.getParameter("nIdConsecutivo").toString());
		session.setAttribute(GestionInterface.ATT_ContratoDefinitivo, request.getParameter("cIdContratoDefinitivo").toString());
	}
	if ((String)session.getAttribute(GestionInterface.ATT_ContratoEjercicio) != null)
		OnSession = true;
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Contratos</title>
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
		<script type="text/javascript" src="../js/funciones.js"></script>
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	  	<script type="text/javascript" src="../../js/jquery.blockUI-2.4.2.js"></script>
	  	<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/FuncionesContrato.js"></script>
	
	
	<script type="text/javascript" charset="utf-8">
		var nIdEstado=1;
		var nContratoAbierto=0;
		var cIdTipoContrato="";
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
				Map pestanas=ebl.getPestana(roles,"Contratos");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);<%
				}
				pestanas=ebl.getPestana(roles,"PedidosContratos");
				it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);<%
				}
			%>
			nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoContrato)%>;
			nContratoAbierto=<%=Integer.parseInt(session.getAttribute(GestionInterface.ATT_ContratoAbierto).toString())%>;
			
			$(".tabs").tabs();						
			$("#consultaContrato").click(function() {
				window.location = "Contratos.jsp?tab=" + 0;
			});
			$("#caratulaContrato").click(function() {
				window.location = "Contratos.jsp?tab=" + 1;
			});			
			$("#partidasContrato").click(function() {
				window.location = "Contratos.jsp?tab=" + 2;
			});
			$("#presupuestoContrato").click(function() {
				window.location = "Contratos.jsp?tab=" + 3;
			});			
			$("#preCompromisoContrato").click(function() {
				window.location = "Contratos.jsp?tab=" + 4;
			}); 
			$("#pagosContrato").click(function() {
				window.location = "Contratos.jsp?tab=" + 5;
			}); 
			$("#imprimirContrato").click(function() {
				window.location = "Contratos.jsp?tab=" + 6; 
			});
			$("#plurianualidadContrato").click(function() {
				window.location = "Contratos.jsp?tab=" + 7; 
						
			});
			
			$("#pasivoContrato").click(function() {
				window.location = "Contratos.jsp?tab=" + 8; 
			});
			
			$("#nuevoContratoCap1000").click(function() {
				window.location = "Contratos.jsp?tab=" + 9;
			});
			$("#nuevoContratoFONDEN").click(function() {
				window.location = "Contratos.jsp?tab=" + 10;
			});
			$("#ampliacionesContrato").click(function() {
				window.location = "Contratos.jsp?tab=" + 11;
			});		
			$("#anticiposRetencion").click(function() {
				window.location = "Contratos.jsp?tab=" + 12;
			});
			$("#firmantesContrato").click(function() {
				window.location = "Contratos.jsp?tab=" + 13;
			});
			$("#nuevasEps").click(function() {
				window.location = "Contratos.jsp?tab=" + 14;
			});
			$("#reclasificaTipoAdj").click(function() {
				window.location = "Contratos.jsp?tab=" + 15;
			});
			$("#documentacion").click(function() {
				window.location = "Contratos.jsp?tab=" + 16;
			});
			$("#actDatCNET").click(function() {
				window.location = "Contratos.jsp?tab=" + 17;
			});
			$("#facturaGlobal").click(function() {
				window.location = "Contratos.jsp?tab=" + 18;
			});
			$("#docContrato").click(function() {
				window.location = "Contratos.jsp?tab=" + 19;
			});
			$("#terminacionAnticipada").click(function() {
				window.location = "Contratos.jsp?tab=" + 20;
			});
			if ($("#session").val() == "false") {
				$( "#caratulaContrato" ).attr("disabled", true);
				$( "#partidasContrato" ).attr("disabled", true);
				$( "#presupuestoContrato" ).attr("disabled", true);
				$( "#preCompromisoContrato" ).attr("disabled", true);
				$( "#pagosContrato" ).attr("disabled", true);
				$( "#imprimirContrato" ).attr("disabled", true);
				$( "#plurianualidadContrato" ).attr("disabled", true);
				$( "#pasivoContrato" ).attr("disabled", true);
				$( "#ampliacionesContrato" ).attr("disabled", true);
				$( "#anticiposRetencion").attr("disabled", true);
				$( "#firmantesContrato").attr("disabled", true);
				$( "#reclasificaTipoAdj").attr("disabled", true);
				$( "#documentacion").attr("disabled", true);
				$( "#actDatCNET").attr("disabled", true);
				$( "#facturaGlobal").attr("disabled", true);
				$( "#docContrato").attr("disabled", true);
				$( "#nuevasEps").attr("disabled", true);
			}
			$( "#nuevasEps").css("display", "none");
			$( "#reclasificaTipoAdj").css("display", "none");
			$( "#documentacion").css("display", "none");
			$( "#actDatCNET").css("display", "none");
			$( "#terminacionAnticipada").css("display", "none");
			$( "#docContrato").css("display", "none");
			$( "#terminacionAnticipada").css("display", "none");
			
			if(nIdEstado == 3){
				$( "#docContrato").css("display", "block");
			}
			if(nIdEstado == 4 || nIdEstado == 5 || nIdEstado == 6 || nIdEstado == 1){
				//$("#anticiposRetencion").css("display", "none");
				$( "#anticiposRetencion").attr("disabled", true);
				if(nIdEstado == 4 ){
					$( "#nuevasEps").css("display", "block");
					$( "#reclasificaTipoAdj").css("display", "block");
					$( "#documentacion").css("display", "block");
					$( "#actDatCNET").css("display", "block");
					$( "#facturaGlobal").css("display", "block");
					$( "#docContrato").css("display", "block");
					$( "#terminacionAnticipada").css("display", "block");
				}
			}
			$(window).bind('resize', function (){
				resizeDt();
			});
		});		
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
	function muestraObserv(){
		$("#trDuplicidad").hide();
		var cad=$("#cadenaDuplicidad").val();
		if(cad.length>0){
			$("#trDuplicidad").show();
			cad=cad.replace(/(?:<b>|<\/b>)/g, '');
			cad=cad.replace(/(?:<br>)/g, '\n');
			cad=cad.replace(/(?:<br \/>)/g, '\n');
			$("#cadenaDuplicidad").val(cad);
		}
	}
	</script>
  </head>  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0"  onkeydown="return checkShortcut();" >
  	<form> 
		<div id="container" class="container" style="width: 95%">	
			<br/>
			<br/>
			<h1>M&oacute;dulo de Contratos<label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<br/>
			<div class="tabs" id="tabs" >
				<ul>
					<li><a id="nuevoContratoFONDEN" href="#tabs-10" >Nuevo-OTROS</a></li>
					<li><a id="nuevoContratoCap1000" href="#tabs-9" >Nuevo-CAP1000</a></li>
					<li><a id="consultaContrato" href="#tabs-0" >Consulta</a></li>
					<li><a id="caratulaContrato" href="#tabs-1" >Car&aacute;tula</a></li>
					<li><a id="partidasContrato" href="#tabs-2" >Partidas</a></li>
					<li><a id="firmantesContrato" href="#tabs-13" >Firmantes</a></li>
					<li><a id="presupuestoContrato" href="#tabs-3" >Presupuesto</a></li>
					<li><a id="preCompromisoContrato" href="#tabs-4" >Pre-Compromiso</a></li>
					<li><a id="pagosContrato" href="#tabs-5" >Pagos</a></li>
					<li><a id="imprimirContrato" href="#tabs-6" >Imprimir</a></li>
					<li><a id="plurianualidadContrato" href="#tabs-7" >Plurianualidad</a></li>
					<li><a id="pasivoContrato" href="#tabs-8" >Pasivo</a></li>
					
					<%if (Integer.parseInt(session.getAttribute(GestionInterface.ATT_ContratoAbierto).toString()) == 0 || Integer.parseInt(session.getAttribute(GestionInterface.ATT_EstadoContrato).toString()) != 4 ){
						%>
						<li><a id="ampliacionesContrato" href="#tabs-11" style="display: none;">Ampliaciones</a></li>
						<%
					}
					else{
						%>
						<li><a id="ampliacionesContrato" href="#tabs-11" >Ampliaciones</a></li>
						<%
					}%>
					<li><a id="anticiposRetencion" href="#tabs-12" >Anticipos y Retenciones</a></li>
					<li><a id="nuevasEps" href="#tabs-14" >Nuevas EP´S</a></li>
					<li><a id="reclasificaTipoAdj" href="#tabs-15" >Reclasifica Tipo Adj</a></li>
					<li><a id="documentacion" href="#tabs-16" >Hiperv&iacute;nculos</a></li>
					<li><a id="actDatCNET" href="#tabs-17" >Actualiza Datos CNET</a></li>
					<li><a id="facturaGlobal" href="#tabs-18" >Factura Global</a></li>
					<li><a id="docContrato" href="#tabs-19" >Carga de Documentos</a></li>
					<li><a id="terminacionAnticipada" href="#tabs-20" >Terminaci&oacute;n Anticipada</a></li>
				</ul>
				<% 
				String tab=request.getParameter("tab").toString(); 
				System.out.println("tab:"+tab);
				if (request.getParameter("tab")!= null){ %>
					<div id="tabs-10" align="center" >
						
						 <% if (request.getParameter("tab").equals("10")) {
						 OnSession=false; %>
						 <jsp:include page="ContratoNuevoFONDEN.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 0);
							</script>
						<% }  %>
					</div>
					<div id="tabs-9" align="center" >
						 <% if (request.getParameter("tab").equals("9")) {
						 OnSession=false; %>
						 <jsp:include page="ContratoNuevoCap1000.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 1);
							</script>
						<% }  %>
					</div>
					<div id="tabs-0" align="center">
					 	<% if (request.getParameter("tab").equals("0")) {
					 		OnSession=false; %>
							<jsp:include page="consultaContrato.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 2);
							</script>
						<% } %> 
					</div>
					<div id="tabs-1" align="center" >
						 <% if (request.getParameter("tab").equals("1")  && OnSession ) { %>
							<jsp:include page="caratulaContrato.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 3);
							</script>
						<% } %>						 
					</div>
					<div id="tabs-2" align="center" >
						 <% if (request.getParameter("tab").equals("2") && OnSession) { %>
							<jsp:include page="partidasContrato.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 4);
							</script>
						<% }  %>
					</div>
					<div id="tabs-13" align="center" >
						 <% if (request.getParameter("tab").equals("13") && OnSession) { %>
							<jsp:include page="FirmantesContrato.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 5);
							</script>
						<% }  %>
					</div>
					<div id="tabs-3" align="center" >
						 <% if (request.getParameter("tab").equals("3") && OnSession) { %>
							<jsp:include page="presupuestoContrato.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 6);
							</script>
						<% }  %>
					</div>
					<div id="tabs-4" align="center" >
						 <% if (request.getParameter("tab").equals("4") && OnSession) { %>
							<jsp:include page="preCompromisoContrato.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 7);
							</script>
						<% }  %>
					</div>
					<div id="tabs-5" align="center" >
						 <% if (request.getParameter("tab").equals("5") && OnSession) { %>
							<jsp:include page="pagosContrato.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 8);
							</script>
						<% }  %>
					</div>
					<div id="tabs-6" align="center" >
						 <% if (request.getParameter("tab").equals("6") && OnSession) { %>
							<jsp:include page="imprimirContrato.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 9);
							</script>
						<% }  %>
					</div>
					<div id="tabs-7" align="center" >
						 <% if (request.getParameter("tab").equals("7") && OnSession) { %>
							<jsp:include page="plurianualidadContrato.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 10);
							</script>
						<% }  %>
					</div>
					<div id="tabs-8" align="center" >
						 <% if (request.getParameter("tab").equals("8") && OnSession) { %>
							<jsp:include page="pasivoContrato.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 11);
							</script>
						<% }  %>
					</div>
					<div id="tabs-11" align="center" >
						 <% if (request.getParameter("tab").equals("11") && OnSession) { %>
							<jsp:include page="AmpliacionesContrato.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 12);
							</script>
						<% }  %>
					</div>
					<div id="tabs-12" align="center" >
						 <% if (request.getParameter("tab").equals("12") && OnSession) { %>
							<jsp:include page="AnticiposRetenciones.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 13);
							</script>
						<% }  %>
					</div>
					<div id="tabs-14" align="center" >
						 <% if (request.getParameter("tab").equals("14") && OnSession) { %>
							<jsp:include page="NuevasEPSContrato.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 14);
							</script>
						<% }  %>
					</div>
					<div id="tabs-15" align="center" >
						 <% if (request.getParameter("tab").equals("15") && OnSession) { %>
							<jsp:include page="ReclasificaTipoAdjudicacion.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 15);
							</script>
						<% }  %>
					</div>
					<div id="tabs-16" align="center" >
						 <% if (request.getParameter("tab").equals("16") && OnSession) { %>
							<jsp:include page="DocumentacionContratos.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 16);
							</script>
						<% }  %>
					</div>
					<div id="tabs-17" align="center" >
						 <% if (request.getParameter("tab").equals("17") && OnSession) { %>
							<jsp:include page="ActDatosCNET.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 17);
							</script>
						<% }  %>
					</div>
					<div id="tabs-18" align="center" >
						 <% if (request.getParameter("tab").equals("18") && OnSession) { %>
							<jsp:include page="FacturaGlobalContratos.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 18);
							</script>
						<% }  %>
					</div>
					<div id="tabs-19" align="center" >
						 <% if (request.getParameter("tab").equals("19") && OnSession) { %>
							<jsp:include page="CargaDocumentosContrato.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 19);
							</script>
						<% }  %>
					</div>
					<div id="tabs-20" align="center" >
						 <% if (request.getParameter("tab").equals("20") && OnSession) { %>
							<jsp:include page="TerminacionAnticipadaContrato.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 20);
							</script>
						<% }  %>
					</div>
				<% } %> 			
			</div>
	    </div>
	   <input type="hidden" name="session" id="session" value="<%= OnSession %>" />
	   <input type="hidden" name="tbs" id="tbs" value = "0">
	</form>
  </body>
</html>
