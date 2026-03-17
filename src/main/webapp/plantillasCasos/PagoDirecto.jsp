<%@page import="com.syc.egresos.core.EgresoEncabezado"%>
<%@page import="com.syc.obrapublica.EjercicioFiscal"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
	String numeroEmpleado = usuario.getNumeroEmpleado();
	int idCaso = c.getIdCaso();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Pago Directo</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="js/PagoDirectoCaso.js"></script>

<script type="text/javascript"	src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Generador/js/validaciones.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>

<style type="text/css">

.infoTDR{
	
}

.tituloSuperior {
	border-top: 1px solid rgb(50, 50, 50);
	border-left: 1px solid rgb(50, 50, 50);
	text-align: center;
	font-weight: bold;
}

.infoInferior {
	border-bottom: 1px solid rgb(50, 50, 50);
	border-left: 1px solid rgb(50, 50, 50);
	text-align: center;
}

.infoInferiorDer {
	border-bottom: 1px solid rgb(50, 50, 50);
	border-left: 1px solid rgb(50, 50, 50);
	border-right: 1px solid rgb(50, 50, 50);
	text-align: center;
}

.infoSuperiorDer {
	border-top: 1px solid rgb(50, 50, 50);
	border-left: 1px solid rgb(50, 50, 50);
	border-right: 1px solid rgb(50, 50, 50);
	text-align: center;
}

.tituloSuperiorDer {
	border-top: 1px solid rgb(50, 50, 50);
	border-left: 1px solid rgb(50, 50, 50);
	border-right: 1px solid rgb(50, 50, 50);
	text-align: center;
	font-weight: bold;
}

.renglonVacio {
	border-left: 1px solid rgb(50, 50, 50);
}

.renglonVacioDer {
	border-left: 1px solid rgb(50, 50, 50);
	border-right: 1px solid rgb(50, 50, 50);
}

#mainTable {
	border-collapse: collapse;
}

.infoInferiorH {
	border-bottom: 1px solid rgb(50, 50, 50);
	text-align: center;
}

.allBorder {
	border-left: 1px solid rgb(50, 50, 50);
	border-right: 1px solid rgb(50, 50, 50);
	border-bottom: 1px solid rgb(50, 50, 50);
	border-top: 1px solid rgb(50, 50, 50);
}

.tablaResumen {
	border-collapse: collapse;
	border-spacing: 0px;
}

.BR {
	border-right: 1px solid rgb(50, 50, 50);
}

.BL {
	border-left: 1px solid rgb(50, 50, 50);
}

.BT {
	border-top: 1px solid rgb(50, 50, 50);
}

.BB {
	border-bottom: 1px solid rgb(50, 50, 50);
}

.tituloResumen {
	text-align: center;
	font-weight: bold;
}

.tituloResumenR {
	text-align: left;
	font-weight: bold;
}


.notEditable {
	background-color: #CCCCCC;
	color: #000000;
}

.money {
	text-align: right;
}

.numeric {
	text-align: right;
}
 
a img {
	border:none;
}

.resumenEncabezado{
	background-color:#EEEEEE;
	text-align: right;
	font-weight: bold;
}

#resumenRetencion th{
	background-color:#EEEEEE;
	text-align: center;
	font-weight: bold;
	border: 1px solid rgb(190,190,190); 
	padding: 10px 20px;
}


#resumenCalendario th{
	background-color:#EEEEEE;
	text-align: center;
	font-weight: bold;
	border: 1px solid rgb(190,190,190); 
	padding: 10px 20px;
}

.infoResumen{
	border: 1px solid rgb(190,190,190); 
	padding: 10px 20px;
}

.infoResumenMonto{
	border: 1px solid rgb(190,190,190); 
	padding: 10px 20px;
	text-align: right;
}

.even {background: #CCC}
.odd {background: #FFF}

thead, th {text-align: center;}

/* Hace evidente la selección y funciona aunque haya zebra striping */
#tblSuficiencias tbody tr { cursor: pointer; }
#tblSuficiencias tbody tr.row_selected td {
  background-color: #cfe8ff !important; /* azul claro */
  color: #0b3d6e !important;
}

</style>

<script type="text/javascript" src="js/PagoDirecto.js"></script>
<script type="text/javascript" src="ComponentesPago/js/Concepto.js"></script>
<script type="text/javascript" src="ComponentesPago/js/CapturaCUCOPS.js"></script>
<script type="text/javascript" src="ComponentesPago/js/CapturaRetenciones.js"></script>
<script type="text/javascript" src="ComponentesPago/js/CapturaMovimientos.js"></script>
<script type="text/javascript" src="ComponentesPago/js/ResumenPago.js"></script>
<script type="text/javascript" src="ComponentesPago/js/AdjuntaDocumentacionRet.js"></script>
<script type="text/javascript" src="ComponentesPago/js/SeleccionaSuficiencia.js"></script>
<script type="text/javascript" src="../plantillasCasos/js/contrarecibo.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>

<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>

<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>

<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">


<script type="text/javascript">
	var errorFatalMSG = "<%=fatalError%>";
	var errorFatal = <%=!StringUtils.isBlank(fatalError)%>;
	var avanzaTab = false;
	var p = window.parent;
	var numeroEmpleado = "<%=numeroEmpleado%>"; 
	var idCaso = <%=idCaso%>;
	
</script>

</head>

<body id="dt_example" >
	<form id="formPagos" name="formPagos">
	
		<input type="hidden" id=nFolioAutSICOP name="nFolioAutSICOP" />
		<input type="hidden" id="nFolioSuficiencia" name="nFolioSuficiencia" />
		<input type="hidden" id="cIDContrato" name="cIDContrato" />
		<input type="hidden" id="idCaso" name="idCaso" value="<%=idCaso%>"/>
		<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" value="<%=unidadEjecutora%>"/>
		<input type="hidden" name="id_gabinete" id="id_gabinete" value="<%=c.getIdGabinete( )%>" />
		<input type="hidden" name="TIPO_CONCEPTO" id="TIPO_CONCEPTO" value="0" />
		<input type="hidden" name="idTipoConcepto" id="idTipoConcepto" value="0" />	
		<input type="hidden" name="retencionEditada" id="retencionEditada" value="0" />
		<input type="hidden" name="totRetencionesCapturas" id="totRetencionesCapturas" value="0" />
		<input type="hidden" name="tipo_pago" id="tipo_pago" value="PAGODIRECTO" />
		<input type="hidden" name="nIDEstatus" id="nIDEstatus" />
		<input type="hidden" name="idTipoMovimiento" id="idTipoMovimiento" />
		<input type="hidden" name="cuentaOrigen" id="cuentaOrigen" value = "82103" />
		<input type="hidden" name="idConcepto" id="idConcepto" value="-1">
		<input type="hidden" name="cTipoPago" id="cTipoPago" value="PAGODIRECTO"> 
		<input type="hidden" name="nFolioPago" id="nFolioPago" value="<%=nFolioPagoDirecto%>"/> 
		<input type="hidden" name="login" id="login" value="<%=login%>"/> 
		<input type="hidden" name="unidadEjecutora" id="unidadEjecutora" value="<%=unidadEjecutora%>"/> 
		<input type="hidden" name="operador" id="operador" value="<%=operador%>"/> 
		<input type="hidden" name="fechaCaptura" id="fechaCaptura" value="<%=fechaCaptura%>"/> 
		<input type="hidden" name="fechaAplicacion" id="fechaAplicacion" value="<%=fechaAplicacion%>"/> 
		<input type="hidden" name="TO_TIPO_DOCTO" id="TO_TIPO_DOCTO" value="DIRECTO"/> 
		<input type="hidden" id="cDocumento" name="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>"/> 
		<input type="hidden" name="ramo" id="ramo" value="<%=ramo%>"/> 
		<input type="hidden" name="ejercicioFiscal" id="ejercicioFiscal" value="<%=ejercicioFiscal%>"/> 
		<input type="hidden" name="centroContable" id="centroContable" value="<%=centroContable%>"/>
		<input type="hidden" name="IDDocumento" id="IDDocumento" value="<%=nFolioPagoDirecto%>"/> 
		<input type="hidden" name="accion" id="accion"/>
		<input type="hidden" name="cFolio" id="cFolio" value="<%=folio%>"/>
		<input type="hidden" name="nFacturasCapturadas" id="nFacturasCapturadas" value="0"/>
		<input type="hidden" name="idTipoDocumento" id="idTipoDocumento" value="0"/>
		<input type="hidden" name="idDestinoGasto" id="idDestinoGasto" value="-1"/>
		<input type="hidden" name="importeBruto" id="importeBruto" value="0"/>
		<input type="hidden" name="importeNeto" id="importeNeto" value="0"/>
		<input type="hidden" name="importeIVA" id="importeIVA" value="0"/>
		<input type="hidden" name="importeDescuento" id="importeDescuento" value="0"/>
		<input type="hidden" name="otrosImpuestos" id="otrosImpuestos" value="0"/>
		<input type="hidden" name="importeRetencion" id="importeRetencion" value="0"/>
		<input type="hidden" name="idTipoFondo" id="idTipoFondo" value="FF"/>
		<input type="hidden" name="CTAB" id="CTAB" value=""/>
		<input type="hidden" name="idUsuarioCaptura" id="idUsuarioCaptura" value="<%=login%>"/>
		<input type="hidden" name="numEmpleadoElab" id="numEmpleadoElab" value=""/>
		<input type="hidden" id="cEjercicio" name="cEjercicio" value="<%=ejercicioFiscal%>"/>
		<input type="hidden" id="applyQuestionnaire" name="applyQuestionnaire" value="N"/>
		<input type="hidden" id="aplicaArt15D" name="aplicaArt15D" value=""/>
		<input type="hidden" id="esPPD" name="esPPD" value=""/>
		<input type="hidden" id="cCargo" name="cCargo" value=""/>
		<input type="hidden" id="folioAutSICOP" name="folioAutSICOP" value=""/>
		
		<div id="container" class="container" style="width: 99%">
			<div class="card-header"> <h3> Pago Directo </h3> </div>
			<hr class="mt-3"/>
			
			<div id="encabezadoPD">
				<div class="row">					
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">													
						<label for="nFolioPagoDirecto" class="form-label"> Folio </label>
						<input type="text" id="nFolioPagoDirecto" name="nFolioPagoDirecto" class="form-control form-control-sm numeric" readonly/>																
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
						<label for="contrarecibo" class="form-label"> No. contrarrecibo </label>
						<input type="text" id="contrarecibo" name="contrarecibo" class="form-control form-control-sm" size="17" value ="" readonly/>												
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
						<label for="fAplicacion" class="form-label"> Fecha de Aplicación </label>
						<input type="text" id="fAplicacion" name="fAplicacion" class="form-control form-control-sm" size="12" readonly/>						
					</div>
				</div>	
				
				<div class="row mt-3 mb-3" id="suficienciaDiv" style="display: none;">
					<jsp:include page="ComponentesPago/SeleccionaSuficiencia.jsp"></jsp:include>
				</div>
			</div>
			
			<br/>
			
			<div id="tabs" style="width: 100%">
				<ul>
					<li><a id="PAAS" href="#TabPaas" style="display:none">PAAS</a></li>
					<li><a id="Concepto" href="#TabConcepto" style="display: none" onclick="setInitialValuesConcepto();creaDTResumenFacturas()">Generales</a></li>
					<li><a id="Retenciones" href="#TabRetenciones" style="display: none" onclick="iniciaCapturaRetenciones();creaDTRetenciones()">Retenciones</a></li>
					<li><a id="Movimientos" href="#TabMovimientos" style="display: none" onclick="iniciaCapturaMovimientos()">Movimientos</a></li>
					<li><a id="Resumen" href="#TabResumen" style="display: none" onclick="return onLoadResumen()">Resumen</a></li>
				</ul>

				<!-- TAB para programacion de PAAS -->
				<div id="TabPaas" style="width: 100%; display: none;">
					<jsp:include page="ComponentesPago/CapturaCUCOPS.jsp"></jsp:include>
				</div>
				<!-- FIN TAB para programacion de PAAS -->

				<!-- TAB para CONCEPTO -->
				<div id="TabConcepto" style="display: none">
					<jsp:include page="ComponentesPago/Concepto.jsp"></jsp:include>
				</div>
				<!-- FIN TAB para CONCEPTO -->

				<!-- TAB para seleccion de retenciones. -->
				<div id="TabRetenciones" style="display: none">
					<jsp:include page="ComponentesPago/CapturaRetenciones.jsp"></jsp:include>
				</div>
				<!-- FIN TAB para seleccion de retenciones. -->

				<!-- TAB para calendarizacion de montos. -->
				<div id="TabMovimientos" style="display: none">
					<jsp:include page="ComponentesPago/CapturaMovimientos.jsp"></jsp:include>
				</div>
				<!-- FIN TAB para calendarizacion de montos. -->

				<!-- TAB Resumen. -->
				<div id="TabResumen" style="display: none">
					<jsp:include page="ComponentesPago/ResumenPago.jsp"></jsp:include>
				</div>
				<!-- FIN TAB para calendarizacion de montos. -->
			</div>
			<!-- FIN TABS -->
		</div>
		<div id="dlgMensajes"></div>

		<!-- DIV Para la carga de facturas -->
		<div id="dialog-validaFact" title="Validacion de Facturas">
			<div id="uploadFacturasDiv">
				<iframe id="uploadFacturasFrm"
					src="../Generador/UploadFacturas.jsp?tipo_pago=PAGODIRECTO"
					align="top" frameborder="0" height="600" width="950"> </iframe>
			</div>
		</div>
		<!-- FIN DIV Para la carga de facturas -->
		
		
	</form>
</body>
</html>