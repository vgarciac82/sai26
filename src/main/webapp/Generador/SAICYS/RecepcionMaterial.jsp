<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@ page import="java.util.*" %>

<%
	
	String roles="";
	String cIdTipo = "";
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=null;
	Map rol =null;
	String cCentroContable="";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	name_user=usuario.getLogin();
	rol =usuario.getRoles();
	ConfiguraAplicativoBusinessLogic configApp = new ConfiguraAplicativoBusinessLogic(
			GestionInterface.ATT_CONEXION);
	boolean esSAIAlterno = "true".equals(configApp.getSystemSetting("SAI_AMBIENTAL"))
			|| "true".equals(configApp.getSystemSetting("SAI_FONDEN"));

 %>

<!DOCTYPE HTML>
<html>
  <head>
  
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content ="IE-edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Recepción de Materiales</title>
	
	<link rel="stylesheet" type="text/css"	href="../css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>
	<style type="text/css" title="currentStyle"> 
	    @import "../../css/custom-theme/jquery-ui-1.8.16.custom.css";
 		@import "../css/demo_table_jui.css"; 
		@import "../css/demo_page.css"; 
		@import "../css/demo_table.css"; 
	</style>
	

	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="../js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../js/Moment.js"></script>
	    <script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
  	<script type="text/javascript" src="../js/RecepcionMaterial.js"></script>
	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
  	<script type="text/javascript" src="../../js/jquery.blockUI-2.4.2.js"></script>
  	<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.all.js"></script>
	
  	<script type="text/javascript" charset="utf-8">
  	var esSAIAlterno = <%=esSAIAlterno%>;
  	var remamenteAnticipo=false;
  		$(document).ready(function() {
  			$("#dlgDatosNota").hide();
  			
			<%
			    String role="";
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				//	NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				Map botones=nb.getBotones(role,"RecepcionMaterial","NuevaRecepcion");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
			roles="<%=roles%>";
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			}
	  		$(".tabs").tabs();
	  		
	  		$("#NuevaRecepcion").click(function() {
					window.location = "RecepcionMaterial.jsp?tab=" + 0;
			});
			$("#ConsultaRecepcion").click(function() {
					window.location = "RecepcionMaterial.jsp?tab=" + 1;
			});
			$("#CaratulaRecepcion").click(function() {
					window.location = "RecepcionMaterial.jsp?tab=" + 2;
			});
			$(window).bind('resize', function (){
				resizeDt();
			});
			showAndHideTabs();
		});
		function checkShortcut()
		{			
			if(event.keyCode==27){  //escape
				return false;
			}
			if(((event.srcElement.tagName.toUpperCase() != 'INPUT' && event.srcElement.tagName.toUpperCase() != 'TEXTAREA'))
				&& (event.keyCode==8 || event.keyCode==13)){					
				return false;
			}
		}
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
  	</script>
  	
  </head>
  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" onkeydown="return checkShortcut()">
    <div id="container" class="container" style="width: 90%;">	
			<br/>
			<br/>
			<h1>Recepci&oacute;n de Material<label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<br/>
			<div class="tabs" id="tabs" name="tabs">
				<ul>
					<li><a id="NuevaRecepcion" href="#tabs-0" >Nuevo</a></li>
					<li><a id="ConsultaRecepcion" href="#tabs-1" >Consulta</a></li>
					<li><a id="CaratulaRecepcion" href="#tabs-2" >Car&aacute;tula</a></li>
					
				</ul>
				<div id="tabs-0" align="center">
					<% if (request.getParameter("tab").equals("0")) { %>
						<jsp:include page="NuevaRecepcion.jsp" />
						<script type="text/javascript" charset="utf-8">
								$("#tbs").val(0);
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 0);
								
						</script>
					<% } %>
				</div>
				<div id="tabs-1" align="center" >
					<% if (request.getParameter("tab").equals("1")) { %>
						<jsp:include page="ConsultaRecepcion.jsp" />
						<script type="text/javascript" charset="utf-8">
							$("#tbs").val(1);
							var $tabs = $(".tabs").tabs();
							$tabs.tabs('select', 1);
						</script>
					<% } %>
				</div>
				<div id="tabs-2" align="center">
					<% if (request.getParameter("tab").equals("2")) { %>
						<jsp:include page="CaratulaRecepcion.jsp" />
						<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 2);
						</script>
					<% } %>
				</div>
				
				
			</div>
	    </div>
	    <jsp:include page="../firmaElectronica/datosNotaFIEL.jsp"></jsp:include>
	    <jsp:include page="../firmaElectronica/datosNotaAlmacenVirtualFIEL.jsp"></jsp:include>
	<form action="">
		<input type="hidden" id="requiereNota" name="requiereNota" value="0"/>
    	<input type="hidden" id="cIdTipoProcedimiento" name="cIdTipoProcedimiento" value="PC"/>
    	<input type="hidden" id="nIdConsecutivoProcedimiento" name="nIdConsecutivoProcedimiento" value=""/>
    	<input type="hidden" id="cIdRFC" name="cIdRFC" value=""/>
    	<input type="hidden" id="cEjercicio" name="cEjercicio" value=""/>
    	<input type="hidden" id="cIdContrato" name="cIdContrato" value=""/>
    	<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
    	<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
    	<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
    	<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
    	<input type="hidden" name="nIdConsecutivoRecepMat" id="nIdConsecutivoRecepMat" value="" />
    	<input type="hidden" name="nIdEstadoRecepMat" id="nIdEstadoRecepMat" value="" />
    	<input type="hidden" name="cIdRecepMat" id="cIdRecepMat" value="" />
    	<input type="hidden" name="cIdSolAnticipo" id="cIdSolAnticipo" value="" />
    	<input type="hidden" name="cadenaLineaCantidad" id="cadenaLineaCantidad" value="" />
    	<input type="hidden" name="nlineaConsolidado" id="nlineaConsolidado" value="" />
    	<input type="hidden" name="estatusEnviado" id="estatusEnviado" value="2" />
    	<input type="hidden" name="estatusCreado" id="estatusCreado" value="1" />
    	<input type="hidden" name="hayRecep" id="hayRecep" value="0" />
    	<input type="hidden" name="estadoAnticipo" id="estadoAnticipo" value="0" />
    	<input type="hidden" name="esCucopGasolina" id="esCucopGasolina" value="" />
    	<input name="cIdEntidadContable" type="hidden" id="cIdEntidadContable"  value="<%=cCentroContable%>"/>
    	<input type="hidden" name="calculosErroneosAnticipo" id="calculosErroneosAnticipo" value="0" />
    	<input type="hidden" name="esDescentralizado" id="esDescentralizado" value="" />
    	<input type="hidden" id="cIdDocumento" name="cIdDocumento" value="" />
   		<input type="hidden" id="cAccion" name="cAccion" value="" />
   		<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>" />
   		<input type="hidden" id="existeUEFactAmort" name="existeUEFactAmort" value="" />
   		<input type="hidden" id="factAmort" name="factAmort" value="0" />
   		<input type="hidden" id="rmaSinPagar" name="rmaSinPagar" value="" />
   		<input type="hidden" id="mMontoTotalPlurianual" name="mMontoTotalPlurianual" value="0" />
   		<input type="hidden" id="esProcesoNormPluri" name="esProcesoNormPluri" value="0" />
   		<input type="hidden" name="nServicio_A_Bienes" id="nServicio_A_Bienes" value="0"/>
   		<input type="hidden" name="montoNetoAmortizadoEjerAnt" id="montoNetoAmortizadoEjerAnt" value="0"/>
   		<input type="hidden" name="isConvEjercicioAnt" id="isConvEjercicioAnt" value="0"/>
   		<input type="hidden" name="isAmortizaEjercAnt" id="isAmortizaEjercAnt" value="0"/>
   		<input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" />
   		<input type="hidden" name="mMontoRemanente" id="mMontoRemanente" />
   		<input type="hidden" name="mMontoPagado" id="mMontoPagado" />
   		<input type="hidden" name="mMontoNetoPluri" id="mMontoNetoPluri" />
   		<input type="hidden" name="nIdLineaConsolidado" id="nIdLineaConsolidado" />
   		<input type="hidden" name="mMontoNetoLineaMaximo" id="mMontoNetoLineaMaximo" />
   		<input type="hidden" name="mMontoRegistradoActual" id="mMontoRegistradoActual" />
   		<input type="hidden" name="mSaldoRemantentexLinea" id="mSaldoRemantentexLinea" />
   		<input type="hidden" name="nFolioAutSICOP" id="nFolioAutSICOP" value="-1"/>
   		<input type="hidden" name="lArchivoContCargado" id="lArchivoContCargado" value="0"/>
   		<input type="hidden" name="nTerminacionAnticipada" id="nTerminacionAnticipada" value="0"/>
   		<input type="hidden" name="nIdConsecutivoAdj" id="nIdConsecutivoAdj" value="0"/>
		<input type="hidden" name="tbs" id="tbs" value = "0">
		<input type="hidden" name="nOtorgaAnticipo" id="nOtorgaAnticipo" value = "0">
		<input type="hidden" name="nFacturaGlobalCargada" id="nFacturaGlobalCargada" value = "0">
		<input type="hidden" name="permiteHacerRecep" id="permiteHacerRecep" value = "1">
		<input type="hidden" name="nPorcentajeIVA" id="nPorcentajeIVA" value = "16">
		<input type="hidden" name="montoNetoMaximo" id="montoNetoMaximo" value = "0">
		<input type="hidden" name="cucopGasolina" id="cucopGasolina" value = "">
		<input type="hidden" name="tienePartidaRestringida" id="tienePartidaRestringida" value ="0">
		<input type="hidden" name="nIdEstatusAtentaNotaFirmada" id="nIdEstatusAtentaNotaFirmada" value ="0">
		<input type="hidden" name="numEmpleado"  id="numEmpleado" value="0">
		<input type="hidden" name="puestoEmpleado"  id="puestoEmpleado" value="">
		<input type="hidden" name="contratoCompranet" id="contratoCompranet" value="">
		<input type="hidden" name="beneficiario" id="beneficiario" value="">
	</form>
	
  </body>
</html>
