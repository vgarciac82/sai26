<%@page import="com.syc.egresos.core.EgresoEncabezado"%>
<%@page import="com.syc.egresos.core.impl.EgresoPAGODIRECTOEncabezado"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.obrapublica.EjercicioFiscal"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
 	String fatalError = "";
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	EjercicioFiscal aEjercicioFiscal = efbl.getEjercicioFiscalActivo();
	if (c == null || usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	String centroContable = (usuario.getPropiedad("CCENTROCONTABLE") != null
			? usuario.getPropiedad("CCENTROCONTABLE").getValor()
			: "");
			
	if (StringUtils.isBlank(centroContable))
		fatalError = "Error Fatal: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable. Consulte a su administrador.";

	String unidadEjecutora = usuario.getU_UR();
	if (StringUtils.isBlank(unidadEjecutora))
		fatalError = "Error Fatal: El Usuario no tiene Unidad Ejecutora asignada y no podra realizar aplicacion Contable. Consulte a su administrador.";

	String login = usuario.getLogin();
	String operador = usuario.getNombre();
	String fechaCaptura = Util.getTodayESMX();
	String fechaAplicacion = Util.calculaFechaAplicacion(aEjercicioFiscal);
	int nFolioPagoDirecto = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
	String ramo = usuario.getU_Ramo();
	String ejercicioFiscal = aEjercicioFiscal.getaEjercicioFiscal();
	String folio = c.getFolio();
	String unidadResponsable = usuario.getU_UR( );
	EgresoEncabezado pagoDirecto = new EgresoPAGODIRECTOEncabezado();
	pagoDirecto = pagoDirecto.cargaEncabezado(nFolioPagoDirecto);
	
	
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Autorizacion de Pago Directo</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/resumenPago.css"></link>
<link rel="stylesheet" type="text/css" href="ComponentesPago/CSS/EgresoFirmantes.css"></link>
<link rel="stylesheet" type="text/css" href="../css/interfaz.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Generador/js/validaciones.js"></script>
<script type="text/javascript" src="../Generador/js/funciones.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="js/AutorizaPagoDirectoNomina.js"></script> 
<script type="text/javascript" src="ComponentesPago/js/ResumenPago.js"></script>
<script type="text/javascript" src="ComponentesPago/js/EgresoFirmantes.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript">
	var errorFatalMSG = "<%=fatalError%>";
	var errorFatal = <%=!StringUtils.isBlank(fatalError)%>;
	var avanzaTab = false;
	var p = window.parent;
	
	var infoObj = {
		login : '<%=login%>',
		operador : '<%=operador%>',
		fechaCaptura : '<%=fechaCaptura%>',
		fechaAplicacion : '<%=fechaAplicacion%>',
		nFolioPagoDirecto : '<%=nFolioPagoDirecto%>',
		ramo : '<%=ramo%>',
		ejercicioFiscal : '<%=ejercicioFiscal%>',
		folio : '<%=folio%>',
		cTipoPago : "<%=c.getTipoCaso().getGavetaAsociada()%>",
		nFolioPago : "<%=nFolioPagoDirecto%>",
		cUnidadPago : "<%=unidadResponsable%>",
		cDocumento : "<%=c.getTipoCaso().getGavetaAsociada()%>",
		cUnidadResponsable : "<%=unidadResponsable%>"
	};
	
</script>
</head>
<body id="dt_example" onkeydown="return(desactivaBackspace(event))">
	<form id="autPago" method="POST" >
		
		<input type="hidden" id="aplica15D" value="<%=pagoDirecto.isAplica15D()%>">
		<input type="hidden" id="cuestionarioRespondido" value="<%=pagoDirecto.isQuestionaireAnswered()%>">
		<input type="hidden" name="cMotivoRechazo" id="cMotivoRechazo" value="">
		<input type="hidden" name="operacion" id="operacion" value="">
		<input type="hidden" name="login" id="login" value="">
		<input type="hidden" name="operador" id="operador" value="">
		<input type="hidden" name="fechaCaptura" id="fechaCaptura" value="">
		<input type="hidden" name="fechaAplicacion" id="fechaAplicacion" value="">
		<input type="hidden" name="nFolioPagoDirecto" id="nFolioPagoDirecto" value="">
		<input type="hidden" name="ramo" id="ramo" value="">
		<input type="hidden" name="ejercicioFiscal" id="ejercicioFiscal" value="">
		<input type="hidden" name="folio" id="folio" value="">
		<input type="hidden" name="cTipoPago" id="cTipoPago" value="">
		<input type="hidden" name="nFolioPago" id="nFolioPago" value="">
		<input type="hidden" name="cUnidadPago" id="cUnidadPago" value="">
		<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value="">
		<input type="hidden" name="cDocumento" id="cDocumento" value="">
		<input type="hidden" name="cTipoFirmante" id="cTipoFirmante" value="">
		<input type="hidden" name="nIDEstatus" id="nIDEstatus" value="">
		<input type="hidden" name="idUsuarioRevision" id="idUsuarioRevision" value="">
		<input type="hidden" name="idUsuarioAprobacion" id="idUsuarioAprobacion" value="">
		<input type="hidden" name="directoNomina" id="directoNomina" value="NOMINA"/>
		
		<input type="hidden" id="msgRF" name="msgRF" value="">
		
			<div id="repseDiv" style="display: none">
				<fieldset>
					<legend>Informaci&oacute;n Importante</legend>
					<table align="center">
						<tr>
							<td>
								El proveedor cuenta con Numero de  REPSE. Por favor valide antes de autorizar el pago que el registro sea vigente en la ruta:
								<br>
								<a href="https://repse.stps.gob.mx" target="_blank">https://repse.stps.gob.mx</a> 
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
			<div id="resumenDiv" style="width: 100%">
				<jsp:include page="ComponentesPago/ResumenPago.jsp"></jsp:include>
			</div>
			
			<div id="dialogMotivo" title="Rechazo de Pago Directo">
				<fieldset>
					<legend>
						Ingrese el motivo del rechazo.
					</legend>
					<table>
						<tr>
							<td>
								<textarea rows="5" cols="35" id="motivoRechazo"></textarea>
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
			
			<div id="operacionesDiv" style="display:none">
				<fieldset>
					<legend>
						Operaciones Disponibles
					</legend>
					<table align="center">
						<tr>
							<td>
								<input type="button" id="aceptarBtn" value="Autorizar" class="btnInterfaceBG"/>
							</td>
							<td>
								<input type="button" id="rechazarBtn" value="Rechazar" class="btnInterfaceBG"/>
							<td>
						</tr>
					</table>
				</fieldset>
			</div>
			
			<div id="firmantesDiv" style="display:none">
				<div class= "card">
					<div class="card-body">	
						<h5> Seleccione los Firmantes </h5>
						<hr class="mt-3">
						
						<jsp:include page="ComponentesPago/EgresoFirmantesBs.jsp"></jsp:include>
					</div>
				</div>
			</div>
	</form>
</body>
</html>