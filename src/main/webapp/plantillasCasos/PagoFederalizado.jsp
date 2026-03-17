<%@page import="com.syc.egresos.core.EgresoEncabezado"%>
<%@page import="com.syc.obrapublica.EjercicioFiscal"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
	int nFolioPagoFed = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
	String ramo = usuario.getU_Ramo();
	String ejercicioFiscal = aEjercicioFiscal.getaEjercicioFiscal();
	String folio = c.getFolio();
	String numeroEmpleado = usuario.getNumeroEmpleado();
	int idCaso = c.getIdCaso();
	
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today= sdf.format(c1.getTime());
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
<title>Pago Federalizado</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>

<style type="text/css">

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
</style>


<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="js/PagoFederalizado.js"></script>
<script type="text/javascript" src="ComponentesPago/js/CapturaRetencion.js"></script>
<script type="text/javascript" src="../Generador/js/validaciones.js"></script>
<script type="text/javascript" src="ComponentesPago/js/CapturaMovimientos.js"></script>
<script type="text/javascript" src="ComponentesPago/js/ResumenPagoFed.js"></script>
<script type="text/javascript" src="ComponentesPago/js/AdjuntaDocumentacionRet.js"></script>
<script type="text/javascript" src="js/contrarecibo.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>

<script type="text/javascript">
	var errorFatalMSG = "<%=fatalError%>";
	var errorFatal = <%=!StringUtils.isBlank(fatalError)%>;
	var avanzaTab = false;
	var p = window.parent;
	var numeroEmpleado = "<%=numeroEmpleado%>"; 
	var idCaso = <%=idCaso%>;
	
	$(document).ready(function(){
			
		$("input.AyudaSyC").subIniciaDlg();
		$("input.autoCompletaSyC").subIniciaAutoCompleta();
		$(function() {
		$( "#fDesde" ).datepicker({
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
				} );
	$(function() {
		$( "#fHasta" ).datepicker({
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
				} );
	});
</script>

</head>

<body id="dt_example" >
	<form id="formPagos" name="formPagos">
	
		<input type="hidden" name="idCaso" id="idCaso" value="<%=idCaso%>"/>
		<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=unidadEjecutora%>"/>
		<input type="hidden" name="id_gabinete" id="id_gabinete" value="<%=c.getIdGabinete( )%>" />
		<input type="hidden" name="TIPO_CONCEPTO" id="TIPO_CONCEPTO" value="0" />
		<input type="hidden" name="idTipoConcepto" id="idTipoConcepto" value="0" />	
		<input type="hidden" name="retencionEditada" id="retencionEditada" value="0" />
		<input type="hidden" name="totRetencionesCapturas" id="totRetencionesCapturas" value="0" />
		<input type="hidden" name="tipo_pago" id="tipo_pago" value="PAGOFEDERALIZADO" />
		<input type="hidden" name="nIDEstatus" id="nIDEstatus" value = "0"/>
		<input type="hidden" name="idTipoMovimiento" id="idTipoMovimiento" />
		<input type="hidden" name="cuentaOrigen" id="cuentaOrigen" value = "82103" />
		<input type="hidden" name="idConcepto" id="idConcepto" value="-1"/>
		<input type="hidden" name="cTipoPago" id="cTipoPago" value="PAGOFEDERALIZADO"/> 
		<input type="hidden" name="nFolioPago" id="nFolioPago" value="<%=nFolioPagoFed%>"/> 
		<input type="hidden" name="login" id="login" value="<%=login%>"/> 
		<input type="hidden" name="unidadEjecutora" id="unidadEjecutora" value="<%=unidadEjecutora%>"/> 
		<input type="hidden" name="operador" id="operador" value="<%=operador%>"/> 
		<input type="hidden" name="fechaCaptura" id="fechaCaptura" value="<%=fechaCaptura%>"/> 
		<input type="hidden" name="fechaAplicacion" id="fechaAplicacion" value="<%=fechaAplicacion%>"/> 
		<input type="hidden" name="TO_TIPO_DOCTO" id="TO_TIPO_DOCTO" value="FEDERALIZADO"/> 
		<input type="hidden" name="cDocumento" id="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>"/> 
		<input type="hidden" name="ramo" id="ramo" value="<%=ramo%>"/> 
		<input type="hidden" name="ejercicioFiscal" id="ejercicioFiscal" value="<%=ejercicioFiscal%>"/> 
		<input type="hidden" name="centroContable" id="centroContable" value="<%=centroContable%>"/>
		<input type="hidden" name="IDDocumento" id="IDDocumento" value="<%=nFolioPagoFed%>"/> 
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
		<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=ejercicioFiscal%>"/>
		<input type="hidden" name="cTipoRfc" id="cTipoRfc" value="" />
		<input type="hidden" name="rfc" id="rfc" />
		<input type="hidden" name="campoRFC" id="campoRFC" />
		<input type="hidden" name="esExtranjero" id="esExtranjero" />
		<input type="hidden" name="diferenciaCFDIPago" id="diferenciaCFDIPago" />
		<input type="hidden" name="cRegimenFiscal" id="cRegimenFiscal" value = "0" />
		<input type="hidden" name="cPrograma" id="cPrograma" value="0"/>
		<input type="hidden" name="nIdSubPrograma" id="nIdSubPrograma" value=""/>
		<input type="hidden" name="nIDPrograma" id="nIDPrograma" value="0"/>
		<input type="hidden" name="cEsFirmaElectronica" id="cEsFirmaElectronica" value="N"/>
		<input type="hidden" name="claves" id="claves" value="0"/>
		<input type="hidden" name="listaSol" id="listaSol" value="0"/>
		<input type="hidden" name="msgRF" id="msgRF" value="0"/>
		<input type="hidden" id="esPPD" name="esPPD" value=""/>
		<input type="hidden" name="correo" id="correo" />
		<input type="hidden" name="nCorreo" id="nCorreo" />
		<input type="hidden" name="paternoCorreo" id="paternoCorreo" />
		<input type="hidden" name="maternoCorreo" id="maternoCorreo" />
		<input type="hidden" name="cCargo" id="cCargo" />
		
		<div id="container" class="container" style="width: 99%">
			<h1>Pago Federalizado</h1>
			<div id="encabezadoPD">
				<table align="center" width="100%" onkeydown="return(desactivaBackspace(event))">
					<tr>
					</tr>
					<tr>
						<td align="left">Anexo/Convenio</td>			
						<td align="left">Folio</td>
						<td>&nbsp;</td>
						<td align="left">No. contrarrecibo</td>
						<td>&nbsp;</td>
						<td align="left">Fecha de Aplicaci&oacute;n</td>					
					</tr>
					<tr>
						<td>
							<input type="text"  name="cIDContratoFed" id="cIDContratoFed" onChange="setInitialValuesConcepto();cargaCtaBancariaRFC();cargaConcepto();" class="AyudaSyC " size="40" maxlength="40" readonly/>
							<input type="hidden" name="cIDContrato" id="cIDContrato"/>
						</td>
						<td align="left">
							<input readonly type="number" id="nFolioPagoFed" name="nFolioPagoFed" class="form-control"/></td>
						<td>&nbsp;</td>
						<td align="left">
							<input readonly type="text" name="contrarecibo" id="contrarecibo" size="17" value="" class="form-control"/></td>
						<td>&nbsp;</td>
						<td align="left">
							<input readonly type="text" name="fAplicacion" id="fAplicacion" size="12" class="form-control"/></td>
					</tr>
				</table>
			</div>
			 
			<div id="tabs" style="width: 100%">
				<ul>
					<li><a id="Concepto" href="#TabConcepto" onclick="setInitialValuesConcepto();creaDTResumenFacturas()">Generales</a></li>
					<li><a id="Retenciones" href="#TabRetenciones" style="display: none" onclick="iniciaCapturaRetenciones();creaDTRetenciones()">Retenciones</a></li>
					<li><a id="Movimientos" href="#TabMovimientos" style="display: none" onclick="iniciaCapturaMovimientos()">Movimientos</a></li>
					<li><a id="Resumen" href="#TabResumen" style="display: none" onclick="return onLoadResumen()">Resumen</a></li>
				</ul>

			<!-- TAB para Encabezado -->
			<div id="TabConcepto">
				<div id="capturaConceptoPago" >
						<table id="generalesPagoTbl" width="100%">
							<tr>
								<td align="left">
									
										<h1>Informaci&oacute;n General</h1>
										<table align="center">
											<tr>
												<td align="left">R.F.C.</td>
												<td>&nbsp;</td>
												<td align="left" colspan=2>Nombre/Raz&oacute;n Social</td>
											</tr>
											<tr>
												<td align="left">
													<input  type="text" maxlength="15" size="15" name="cIDRFC" id="cIDRFC" class="form-control" readonly/> 
												</td>
												<td>&nbsp;</td>
												<td align="left" colspan="2">
													<input type="text" maxlength="150" size="80" name="cnombre" ID="cnombre" class="form-control" readonly/>
												</td>
											</tr>
											<tr>
												<td align="left">Cuenta Bancaria:</td>
												<td>&nbsp;</td>
												<td>
													Fecha periodo de   :  
												</td>
												<td>
													Fecha periodo hasta: 
												</td>
											</tr>
											<tr><td align="left">
													<select id="ctaBancaria" name="ctaBancaria" style="width: 20em;" class="form-select form-select-sm"></select>
												</td>
												<td>&nbsp;</td>
												<td align="left">
													<input type="text" class="form-control" name="fDesde" id="fDesde" size="10" value="<%=today%>" />
												</td>
												<td align="left">
													<input value="<%=today%>" class="form-control" type="text" name="fHasta" id="fHasta" size="10" />
												</td>
											</tr>
											<tr>
												<td align="left">Tipo de Operación</td>
												<td>&nbsp;</td>
												<td align="left" colspan=2>Tipo Destino</td>
											</tr>
										<tr>
											<td align="left">
												<select id="TIPO_OPERACION" name="TIPO_OPERACION" style="width: 30em;" class="form-select form-select-sm">
													<option value="-1">Seleccione Tipo de Operacion</option>
												</select>
											</td>
											<td>&nbsp;</td>
											<td align="left" colspan=2>
												<select id="DESTINO_GASTO" name="DESTINO_GASTO" style="width: 30em;" class="form-select form-select-sm">
													<option value="-1">Seleccione Tipo Destino</option>
												</select>
											</td>
										</tr>
										<tr>
											<td colspan="6" align="left">Concepto:</td>
										</tr>
										<tr>
											<td colspan="6">
												<textarea name="concepto" id="concepto" class="form-control" rows="3" cols="120" onkeypress="valFmt(this,15)"></textarea>
											</td>
										</tr>
										<tr>
											<td colspan = 3>
												<table id ="tblPrograma" style="display: none">
													<tr>
														<td>Programa:</td>
														<td>Subprograma:</td>
													</tr><tr>
														<td align="left">
															<select id="cProgramaDesc" onchange="cambiaPrograma()" class="form-control" class="form-select form-select-sm">
															</select>
														</td>
														
														<td align="left">
															<select id="cSubProgramaDesc" onchange="cambiaSubPrograma()" class="form-select form-select-sm"></select>
														</td>						
													</tr>
												</table>
										</td></tr>
									</table>
								
							</td>
						</tr>	
					</table>
				</div>
				<div id="resumenCaratula" style="display: none">
					<div id="resumenConceptoDiv">
						<table id="generalesPagoResumen" width="100%" >
							<tr>
								<td align="center">
									<fieldset>
										<h1>Concepto del Pago</h1>
										<table align="center"  class="resumenTbl">
											<tr>
												<td class="resumenEncabezado" style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">Tipo de Pago </td>
												<td align="left" id="idTipoDocumentoLbl" class="infoResumen" style="border: 1px solid rgb(190,190,190); padding: 10px 20px;"> &nbsp;</td>
											</tr>
											<tr>
												<td  class="resumenEncabezado" style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">Tipo Destino</td>
												<td align="left" id="idDestinoGastoLbl" class="infoResumen"style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">&nbsp;</td>
											</tr>
											<tr>
												<td  class="resumenEncabezado" style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">Concepto:</td>
												<td align="left" colspan="2" style="border: 1px solid rgb(190,190,190); padding: 10px 20px;"><p id="conceptoLbl">&nbsp;</p></td>
											</tr>
											<tr>
												<td class="resumenEncabezado" colspan="2" style="border: 1px solid rgb(190,190,190); padding: 10px 20px; text-align: center">Beneficiario</td>
											</tr>
											<tr>
												<td  class="resumenEncabezado"  style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">RFC - Nombre/Razon Social</td>
												<td align="left"   id="rfcLbl" class="infoResumen"style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">&nbsp;</td>
											</tr>
											<tr>
												
												<td class="resumenEncabezado"  style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">Cuenta Bancaria</td>
												<td align="left" id="CTABLbl" class="infoResumen"style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">&nbsp;</td>
											</tr>
											<tr>
												
												<td class="resumenEncabezado"  style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">Programa</td>
												<td align="left" id="ProgramaLbl" class="infoResumen"style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">&nbsp;</td>
											</tr>
											<tr>
												
												<td class="resumenEncabezado"  style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">Subprograma</td>
												<td align="left" id="subProgramaLbl" class="infoResumen"style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">&nbsp;</td>
											</tr>
											<tr>
												<td colspan="2" class="resumenEncabezado" style="border: 1px solid rgb(190,190,190); padding: 10px 20px; text-align: center;">
													Facturas
												</td>
											</tr>
											<tr>
												<td colspan="2">
													<table id="grdResumenFacturas" class="display">
														<thead>
															<tr>
																<th>Factura</th>
																<th>Importe Bruto</th>
																<th>IVA</th>
																<th>Retenciones</th>
																<th>Total</th>
															</tr>
														</thead>
													</table>
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
						</table>
					</div>
				</div><!-- Fin resumen caratula -->
				<div id="listaFacturas">
						<h1>Facturas</h1>
							<table align="center">
								<tr>
									<td align="left">
										<input type="button" value="Cargar Facturas" id="cargaFacturasBtn" class="btn btn-secondary"/>
										<input type="button" value="Capturar correo" id="capturaCorreo" class="btn btn-secondary" onclick="abrirCorreo();"/>
									</td>
									<td align="right">Total en Bruto: &nbsp; 
										<input type="text" style="text-align: right;" name="mTotalFacturaV" id="mTotalFacturaV" value="0.00" size="12" maxlength="20" readonly="readonly" class="form-control" />
									</td>
									<td align="right">IVA: &nbsp; 
										<input type="text" style="text-align: right;" name="mImporteIVA" id="mImporteIVA" value="0.00" size="12" maxlength="20" readonly="readonly" class="form-control" />
									</td>
									<td align="right">Total en Neto: &nbsp; 
										<input type="text" style="text-align: right;" name="totalFactura" id="totalFactura" value="0.00" size="12" maxlength="20" readonly="readonly" class="form-control" />
									</td>
								</tr>
								<tr>
									<td align="left" colspan="4">
										<table id="grdValidaFacturas" class="display" width="100%">
											<thead>
												<tr>
													<th>Factura</th>
													<th>Importe Bruto</th>
													<th>IVA</th>
													<th>Retenciones</th>
													<th>Total</th>
												</tr>
											</thead>
										</table>
									</td>
								</tr>
							</table>
					
				</div><!-- FIN DIV FACTURAS -->	
			</div><!-- FIN DIV CONCEPTO -->
			<!-- TAB para seleccion de retenciones. -->
			<div id="TabRetenciones" style="display: none">
				<jsp:include page="ComponentesPago/CapturaRetencion.jsp"></jsp:include>  
			</div>
			
			<!-- TAB para calendarizacion de montos -->
			<div id="TabMovimientos" style="display: none">
				<jsp:include page="ComponentesPago/CapturaMovimientos.jsp"></jsp:include>
			</div>
			
			<!-- TAB Resumen -->
			<div id="TabResumen" style="display: none">
				<jsp:include page="ComponentesPago/ResumenPagoFed.jsp"></jsp:include>
			</div>		
		</div><!-- FIN TABS -->
	</div>
			
		<!-- DIV Para la carga de facturas -->
		<div id="dialog-validaFact" title="Validacion de Facturas">
			<div id="uploadFacturasDiv">
				<iframe id="uploadFacturasFrm"
					src="../Generador/UploadFacturasPF.jsp?tipo_pago=PAGOFEDERALIZADO"
					align="top" frameborder="0" height="400" width="840"> </iframe>
			</div>
		</div>
		<!-- FIN DIV Para la carga de facturas -->
		
		<!-- DIV Para capturar correo -->
		<div class="modal" tabindex="-1" role="dialog" id="dialog-actualizaCorreo" data-mdb-keyboard="true" data-mdb-backdrop="static">
		  	<div class="modal-dialog" role="document" id="actualizaCorreoDiv">
		    	<div class="modal-content">
			      	<div class="modal-header">
			        	<h5 class="modal-title">Favor de capturar los siguientes datos para seguimiento del PPD</h5>
			        	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			    	</div>
			    	<div class="modal-body">
				      	<div class="row">
							<div class="col-12">
								<label for="nombreCorreo">Nombre</label>
								<div class="input-group">
									<span class="input-group-text"><i class="bi bi-person"></i></span>
									<input type="text" id="nombreCorreo" name="nombreCorreo" class="form-control"/>
									<input type="text" id="aPCorreo" name="aPCorreo" class="form-control"/>
									<input type="text" id="aMCorreo" name="aMCorreo" class="form-control"/>
									
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-12">
								<label for="cargo">Cargo</label>
								<div class="input-group">
									<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
									<input type="text" id="cargo" name="cargo" class="form-control" placeholder="Cargo"/>
								</div>	
							</div>
						</div>
						<div class="row">
							<div class="col-12">
								<div class="form-group">
									<label class="sr-only" for="correoActual">Correo</label>
									<div class="input-group">
								        <div class="input-group-prepend">
								          <div class="input-group-text">@</div>
								        </div>
								        <input type="text" class="form-control" id="correoActual" name="correoActual" placeholder="Correo electrónico"/>
								    </div>
									
								</div>
							</div>		
						</div>	
			    	</div>
			      	<div class="modal-footer">
			        	<button type="button" id="btnCorreo" onclick="mostrarCorreo();" class="btn btn-primary">Guardar</button>
			        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
			      	</div>
		    	</div>
		  	</div>
		</div>
	</form>
  </body>
</html>