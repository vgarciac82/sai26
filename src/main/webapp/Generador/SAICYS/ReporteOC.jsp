<%@ page language="java" pageEncoding="UTF-8"%>
<%@page	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@page import="com.syc.gestion.core.NegativaPestana"%>
<%@page import="java.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	Map<String, Role> rol =usuario.getRoles();
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today= sdf.format(c1.getTime());
	c1.add(Calendar.MONTH, -1);
	String todayAnt= sdf.format(c1.getTime());
 %>
<!DOCTYPE html>

<html>
	<head>	
		<title>Reportes de Ordenes de compra</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">    
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
		
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
		<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>
			
		<script type="text/javascript" src="../js/crud.js"></script>
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	  	<link href="../../css/reportesGRM.css" rel="stylesheet" type="text/css" />
	  	
		
		<script type="text/javascript">
		  	$(document).ready(function() {
		  		<%
				    String role="";
				    String roles="";
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
					Map botones=nb.getBotones(role,"ReporteOC","ReortesPedidosContratos");
					Iterator btn = botones.entrySet().iterator();
					while (btn.hasNext()) {
						Map.Entry b = (Map.Entry)btn.next();%>
						$("#<%=b.getValue()%>").attr("disabled", true);<%
					}
	
					%>
		  		roles="<%=roles%>";
				if (roles.indexOf("ADMIN_RECMAT") >= 0 || roles.indexOf("ANALISTA") >= 0 ||roles.indexOf("JEFES") >= 0){
					$("#isAdmin").val(0);
					querySelectPost("UnidadBusca2", "desUnidadResponsable2", {async: false });
				}else{
					querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "desUnidadResponsable2", {async: false});
				}
				querySelectPost("catalogoFudamentoRead", "fundamentoLegal", {async : false});
				querySelectPost("mCatFuenteFinanciamiento", "tipoIngreso", {async : false});
				
				habilitaFundamento();
				agregaDatePickerFechas();
				
		  	});
		  	function openCSV(){
		  		//Guarda en la Bitácora
				$("#cAccion").val("IMPRIME_REPORTESOC");
				$("#cIdDocumento").val("ReporteOC");
				var fundamentoLegal=$("#fundamentoLegal").val();
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				var where='';
				if($("#desUnidadResponsable2").val()!='0'){
					where=" and cIdUnidadEjecutora ='"+$("#desUnidadResponsable2").val()+"'";
				}
				if($("#cRazonSocial").val()!=''){
					where+=" and RazonSocial LIKE '%25"+$("#cRazonSocial").val()+"%25'";
				}
				if($("#cIdRfc").val()!=''){
					where+=" and RFC LIKE '%25"+$("#cIdRfc").val()+"%25'";
				}
				if($("#cIdPedCont").val()!=''){
					where+=" and PedidoContrato LIKE '%25"+$("#cIdPedCont").val()+"%25'";
				}
				if($("#tipoIngreso").val()!='0'){
					where +=" and tipoIngreso ="+$("#tipoIngreso").val();
				}
				if($("#tipoContrato").val()==1){
					where +=" and cNoContratoCNET LIKE 'CE%25'";
				}
				if($("#tipoContrato").val()==2){
					where +=" and cNoContratoCNET NOT LIKE 'CE%25'";
				}
				if($("#fundamentoLegal").val()!='0' && document.getElementById("Totalizado").checked){
					where +=" and nIdFundamentoLeg ="+$("#fundamentoLegal").val();
				}		
				window.open(
					"../../servlet/CatalogosCSV?"
					+ "rn="+$('[name="REPORTE"]:checked').val()
					+ "&fundamentoLegal=" + fundamentoLegal
					+ "&nTipoIngreso=" + $("#nTipoIngreso").val()
					+"&fechaInicio="+$("#fInicio").val()
					+"&fechaFin="+$("#fFin").val()
					+ "&cIdUnidadEjecutora=" + where,
					 'Procesando', 'status=1, width=500px, height=100px, left=150px');
			}
			function openARCH(ext){
						
				//Guarda en la Bitácora
				$("#cAccion").val("IMPRIME_REPORTESOC");
				$("#cIdDocumento").val("ReporteOC");
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});		
				var where='';
				if($("#desUnidadResponsable2").val()!='0')
					where=" and cIdUnidadEjecutora ='"+$("#desUnidadResponsable2").val()+"'";
				if($("#cRazonSocial").val()!='')
					where +=" and RazonSocial LIKE '%25"+$("#cRazonSocial").val()+"%25'";
				if($("#cIdRfc").val()!='')
					where +=" and RFC LIKE '%25"+$("#cIdRfc").val()+"%25'";
				if($("#cIdPedCont").val()!='')
					where +=" and PedidoContrato LIKE '%25"+$("#cIdPedCont").val()+"%25'";
				
		
				$("#formato").val(ext);
				$("#cIdUnidadEjecutora").val(where);
				
						
				var myWindow=window.open("../../servlet/SeguridadCatalogosMateriales?"
					+"catalogo=REPORTE"
					+"&accion=run"
					+"&rn="+$('[name="REPORTE"]:checked').val()
					+"&formato="+ext
					+"&cIdUnidadEjecutora=" + where, 'Procesando', 'status=1, width=400px, height=200px, left=150px');
			}
			function openXLSX(){
				var ext="xlsx";		
				var where='';
				var unidadE='';
				if($("#desUnidadResponsable2").val()!='0'){
					unidadE=$("#desUnidadResponsable2").val();
					if($("#nTipoReporte").val() == 7)
						where=" and cIdUnidadAdministrativa ='"+$("#desUnidadResponsable2").val()+"'";
					else 
						where=" and cIdUnidadEjecutora ='"+$("#desUnidadResponsable2").val()+"'";
				}
				if($("#cRazonSocial").val()!='')
					where +=" and RazonSocial LIKE '%25"+$("#cRazonSocial").val()+"%25'";
				if($("#cIdRfc").val()!='')
					where +=" and RFC LIKE '%25"+$("#cIdRfc").val()+"%25'";
				if($("#cIdPedCont").val()!='')
					where +=" and PedidoContrato LIKE '%25"+$("#cIdPedCont").val()+"%25'";
				if($("#tipoIngreso").val()!='0'){
					where +=" and tipoIngreso ="+$("#tipoIngreso").val();
				}
				if($("#tipoContrato").val()==1){
					where +=" and cNoContratoCNET LIKE 'CE%25'";
				}
				if($("#tipoContrato").val()==2){
					where +=" and cNoContratoCNET NOT LIKE 'CE%25'";
				}
				if($("#fundamentoLegal").val()!='0' && document.getElementById("Totalizado").checked){
					where +=" and nIdFundamentoLeg ="+$("#fundamentoLegal").val();
				}
				$("#formato").val(ext);
				$("#cIdUnidadEjecutora").val(where);
				var myWindow=window.open("../../servlet/ReportesGRM?"
					+"nTipoReporte="+$("#nTipoReporte").val()
					+"&nTipoIngreso="+$("#nTipoIngreso").val()
					+"&operacion=1"
					+"&fechaInicio="+$("#fInicio").val()
					+"&fechaFin="+$("#fFin").val()
					+"&cEjercicioActual=2020"
					+"&reporteNombre="+$('[name="REPORTE"]:checked').val()+"."+ext
					+"&formato="+ext
					+"&where=" + where
					+"&cUnidadE=" + unidadE
					, 'Procesando', 'status=1, width=400px, height=200px, left=150px');
			}		
			function habilitaFundamento(){
				mostrarFiltros();
				if(document.getElementById("Totalizado").checked){
					$("#tdFundamentoLegal").show();
				}else{
					$("#tdFundamentoLegal").hide();
					document.getElementById("fundamentoLegal").selectedIndex = "0";
				}
			}
			function validaPDF(){
				if($("#fundamentoLegal").val()==0){
					$("#tdPDF").show();
				}else{
					$("#tdPDF").hide();
				}
			}
			function mostrarFiltros(){
				$("#tipoIngreso").val(0);
				$("#nTipoIngreso").val(1);
				$("#tdTipoIngreso").hide();
				$("#tdXlsx").hide();
				$("#tdPDF").show();
				$("#tdcsv").show();
				$("#tdIdPedidoCont").show();
				$("#trFiltrosPedCont").show();
				$("#trOtrosFiltros").show();
				$("#trFechas").hide();
				$("#tdRazonSocial").show();
				$("#tdRFC").show();
				$("#trUnidadEjecutora").show();
				$("#tdTipoContrato").show();
				if(document.getElementById("Detalle").checked){
					$("#tdTipoIngreso").show();
				}
				if(document.getElementById("TotalizadoIngFiscales").checked){
					$("#nTipoIngreso").val(1);
					//$("#tdXlsx").show();
					$("#tdPDF").hide();
				}
				if(document.getElementById("TotalizadoIngPropios").checked){
					$("#nTipoIngreso").val(4);
					//$("#tdXlsx").show();
					$("#tdPDF").hide();
				}
				if(document.getElementById("Totalizado").checked){
					$("#tdXlsx").show();
					$("#tdPDF").hide();
					$("#nTipoReporte").val(6);
					$("#tdcsv").hide();
				}
				if(document.getElementById("Plantilla_ProveedoresIncumplidos").checked){
					$("#trFiltrosPedCont").hide();
					$("#tdXlsx").show();
					$("#tdPDF").hide();
					$("#nTipoReporte").val(11);
					$("#tdcsv").hide();
					$("#tdTipoContrato").hide();
				}
				
				if(document.getElementById("reporteMIPyMes").checked || document.getElementById("reporte70_30").checked 
				|| document.getElementById("reporteCOCODI").checked || document.getElementById("Plantilla_IndicadoreCNET").checked){//
					$("#tipoContrato").val(0);
					$("#tdTipoContrato").hide();
					if(document.getElementById("reporteCOCODI").checked ){
						$("#tdXlsx").show();
						$("#tdPDF").hide();
						$("#tdcsv").hide();
						$("#nTipoReporte").val(1);
						$("#trFiltrosPedCont").hide();
						$("#trOtrosFiltros").hide();
						$("#trFechas").show();
					}
					if(document.getElementById("Plantilla_IndicadoreCNET").checked){
						$("#tdXlsx").show();
						$("#tdPDF").hide();
						$("#nTipoReporte").val(5);
						$("#trFiltrosPedCont").hide();
						$("#trOtrosFiltros").hide();
						$("#trFechas").show();
						$("#tdcsv").hide();
					}
					if(document.getElementById("reporte70_30").checked){
						$("#tdPDF").hide();
						$("#tdcsv").hide();
						$("#trFiltrosPedCont").hide();
						$("#trFechas").show();
						$("#tdTipoIngreso").show();
						$("#nTipoIngreso").val(0);
						$("#nTipoReporte").val(3);
						$("#tdXlsx").show();
					}
				}
				if(document.getElementById("ReportePresionGast").checked){
					$("#trFiltrosPedCont").hide();
					$("#trOtrosFiltros").hide();
					$("#tdXlsx").show();
					$("#tdPDF").hide();
					$("#tdcsv").hide();
					
					$("#nTipoReporte").val(2);
				}
				if(document.getElementById("ReportePagoDirecto").checked){
					$("#cIdPedCont").val('');
					$("#tdIdPedidoCont").hide();
					$("#trOtrosFiltros").hide();
					$("#tdXlsx").show();
					$("#tdPDF").hide();
					$("#tdcsv").hide();
					$("#nTipoReporte").val(4);
				}
				if(document.getElementById("Plantilla_Garantias").checked){
					$("#cIdPedCont").val('');
					$("#tdIdPedidoCont").hide();
					$("#trOtrosFiltros").hide();
					$("#tdXlsx").show();
					$("#tdPDF").hide();
					$("#tdcsv").hide();
					$("#nTipoReporte").val(12);
					$("#tdRazonSocial").hide();
					$("#tdRFC").hide();
				}
				
				if(document.getElementById("Plantilla_ReporteArrendamientoGRM").checked || document.getElementById("Plantilla_ReporteContratosCap4").checked
						|| document.getElementById("Plantilla_ReporteContratosPSP").checked || document.getElementById("Plantilla_Penas").checked 
						|| document.getElementById("Plantilla_CompromisosContrato").checked || document.getElementById("Plantilla_ENSA").checked
						|| document.getElementById("Plantilla_UCACP").checked
				){
					$("#cIdPedCont").val('');
					$("#tdIdPedidoCont").hide();
					$("#trOtrosFiltros").hide();
					$("#tdXlsx").show();
					$("#tdPDF").hide();
					$("#tdcsv").hide();
					$("#nTipoReporte").val(7);
					$("#trFiltrosPedCont").show();
					$("#tdRazonSocial").hide();
					$("#tdRFC").hide();
					if(document.getElementById("Plantilla_ReporteContratosCap4").checked ){
						$("#nTipoReporte").val(8);
					}else if(document.getElementById("Plantilla_ReporteContratosPSP").checked ){
						$("#nTipoReporte").val(9);
						$("#trUnidadEjecutora").hide();
					}else if(document.getElementById("Plantilla_Penas").checked ){
						$("#nTipoReporte").val(10);
					}else if(document.getElementById("Plantilla_CompromisosContrato").checked ){
						$("#nTipoReporte").val(13);
					}else if(document.getElementById("Plantilla_ENSA").checked ){
						$("#nTipoReporte").val(14);
					}
					else if(document.getElementById("Plantilla_UCACP").checked ){
						$("#nTipoReporte").val(15);
						$("#trFechas").show();
						$("#trFiltrosPedCont").hide();
					}
					
				}
				
			}
			function agregaDatePickerFechas(){
				$("#fInicial").datetimepicker({
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
					
				});
				$("#fFinal").datetimepicker({
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
					
				});
				
			}
			function actTipoIngreso(){
				$("#nTipoIngreso").val($("#tipoIngreso").val());
			}
			

		 </script>
	</head>
	<body>
		<form action="">
			<div class="container-fluid">
			  <div class="col-md-12 col-lg-12 col-sm-12">
			  	<fieldset class="form-group border p-3">
			  		<legend class="w-auto px-2">Reportes de Pedidos y Contratos</legend>
			  		  <div class="form-group" >		  		  
			  		  <table id="tblFiltros" >
			  		  	<tr id="trFiltrosPedCont"> 
							<td align="left" id="trUnidadEjecutora" style="width: 30%;"> 
								<label for="desUnidadResponsable2">Unidad Ejecutora</label>
								<select class="custom-select"  id="desUnidadResponsable2" name="desUnidadResponsable2" > 
									<option value="<%=usuario.getU_UR()%>" selected="selected"> </option>
								</select> 
							</td>
							<td id="tdRazonSocial" style="width: 30%;">
								<label for="cRazonSocial"></label> 
								<input type="text" class="form-control" placeholder="Razón Social" name="cRazonSocial" id="cRazonSocial">
							</td>
							<td id="tdRFC">
								<label for="cIdRfc"></label> 
								<input type="text" class="form-control" placeholder="RFC" name="cIdRfc" id="cIdRfc">
							</td>
							<td id="tdIdPedidoCont">
								<label for="cIdPedCont" ></label> 
								<input type="text" class="form-control" placeholder="Pedido o Contrato SAI" name="cIdPedCont" id="cIdPedCont">
							</td>
							
						</tr>
						<tr id="trOtrosFiltros">
							<td id="tdFundamentoLegal" style="width: 30%;">
								<label for="fundamentoLegal">Fundamento Legal</label> 
								<select class="custom-select" id="fundamentoLegal" name= "fundamentoLegal" onchange="validaPDF();"  ></select>
							</td>
							<td id="tdTipoIngreso">
								<label for="tipoIngreso">Tipo de Ingreso</label>
								<select class="custom-select" id="tipoIngreso" name= "tipoIngreso" onchange="actTipoIngreso();"></select>
							</td>
							<td id="tdTipoContrato">
								<label for="tipoContrato">Tipo de Contratos</label>
								<select class="custom-select" id="tipoContrato" name= "tipoContrato">
									<option value="0" selected="selected">TODO</option>
									<option value="1">CONTRATOS CREDITO EXTERNO</option>
									<option value="2">CONTRATOS NORMALES</option>
								</select>
							</td>
							<td></td>
						</tr>
						<tr id="trFechas">
							<td colspan="2" align="left">
								<br />
								<label for="fInicial">Fecha Inicio</label>
								<div class="input-group date" id="fInicial" data-target-input="nearest">
						          <input type="text" class="form-control datetimepicker-input" data-target="#fInicial" title="Fecha Inicial" id="fInicio" name="fInicio" value="<%=todayAnt %>"/>
						          <div class="input-group-append" data-target="#fInicial" data-toggle="datetimepicker" title="Fecha Inicial">
						            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
						          </div>
						        </div>
								<br />
								<label for="fFinal">Fecha Fin</label>
								<div class="input-group date" id="fFinal" data-target-input="nearest">
									<input type="text" class="form-control datetimepicker-input" data-target="#fFinal" id="fFin" name="fFin" title="Fecha Final" value="<%=today %>"/>
									<div class="input-group-append" data-target="#fFinal" data-toggle="datetimepicker" title="Fecha Final">
									  <div class="input-group-text"><i class="fa fa-calendar"></i></div>
									</div>
								</div>
							</td>
						</tr>
			  		  	
			  		  </table>
			  		  </div>
			  		  <div class="form-group">
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE" value="reporteOCDetallado.jasper" checked id="Detalle" onclick="habilitaFundamento();">
								<label class="form-check-label" >
								  Pedidos y Contratos Detallado
								</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="reporteOCPartida.jasper"  id="Partida" onclick="habilitaFundamento();">
								<label class="form-check-label" >
								 Pedidos y Contratos Por Partida
								</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="reporteOCCUCOP.jasper"  id="Cucop" onclick="habilitaFundamento();">
								<label class="form-check-label" >Pedidos y Contratos Por CUCOP</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="Reporte_Totalizado"  id="Totalizado" onclick="habilitaFundamento();">
								<label class="form-check-label" >Reporte Totalizado (Pedidos, Contratos Adquisiciones y Contratos de Obra)</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="reporteOCTotalizadoPorTipoRec.jasper"  id="TotalizadoIngFiscales" onclick="habilitaFundamento();">
								<label class="form-check-label" >Pedidos y Contratos Totalizado Ingresos Fiscales</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="reporteOCTotalizadoPorTipoRec.jasper"  id="TotalizadoIngPropios" onclick="habilitaFundamento();">
								<label class="form-check-label" >Pedidos y Contratos Totalizado Ingresos Propios</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="reporteOCPartidaPedCont.jasper"  id="PartidaContrato" onclick="habilitaFundamento();">
								<label class="form-check-label" >Pedidos y Contratos Por Partida del mismo</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="reporteOIC1.jasper"  id="OIC" onclick="habilitaFundamento();">
								<label class="form-check-label" >Pedidos y Contratos OIC1</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="reporteOCDetalladoASF.jasper"  id="ASF" onclick="habilitaFundamento();">
								<label class="form-check-label" >Pedidos y Contratos Detallado ASF</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="reporteMIPyMes.jasper"  id="reporteMIPyMes" onclick="habilitaFundamento();">
								<label class="form-check-label" >Reporte MI_PyMe</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="Formato7030"  id="reporte70_30" onclick="habilitaFundamento();">
								<label class="form-check-label" >Reporte 70_30</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="ReporteCOCODI"  id="reporteCOCODI" onclick="habilitaFundamento();">
								<label class="form-check-label" >Reporte Documento de Gestión (Pesos y Miles de pesos)</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="PLANTILLA_PRESIONES_GASTO_CONTRATOS"  id="ReportePresionGast" onclick="habilitaFundamento();">
								<label class="form-check-label" >Presi&oacute;n de Gasto (Millones de pesos)</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="Plantilla_PagoDirecto"  id="ReportePagoDirecto" onclick="habilitaFundamento();">
								<label class="form-check-label" >Reporte de Pagos Directos</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="Plantilla_IndicadoreCNET"  id="Plantilla_IndicadoreCNET" onclick="habilitaFundamento();">
								<label class="form-check-label" >Reporte Indicadores CNET</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="Plantilla_ReporteArrendamientoGRM"  id="Plantilla_ReporteArrendamientoGRM" onclick="habilitaFundamento();">
								<label class="form-check-label" >Reporte de Contrataciones de Arrendamiento</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="Plantilla_ReporteContratosCap4"  id="Plantilla_ReporteContratosCap4" onclick="habilitaFundamento();">
								<label class="form-check-label" >Reporte de Contratos Cap&iacute;tulo 4 mil</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="Plantilla_ReporteContratosPSP"  id="Plantilla_ReporteContratosPSP" onclick="habilitaFundamento();">
								<label class="form-check-label" >Reporte de PSP´S (Solo para ejercicios a partir del 2022)</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="Plantilla_Penas"  id="Plantilla_Penas" onclick="habilitaFundamento();">
								<label class="form-check-label" >Reporte de penas convencionales y/o deducciones al pago</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="Plantilla_ProveedoresIncumplidos"  id="Plantilla_ProveedoresIncumplidos" onclick="habilitaFundamento();">
								<label class="form-check-label" >Reporte de proveedores sancionados (incumplidos y recisi&oacute;n de contratos)</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="Plantilla_Garantias"  id="Plantilla_Garantias" onclick="habilitaFundamento();">
								<label class="form-check-label" >Reporte de Garantías</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="Plantilla_CompromisosContrato"  id="Plantilla_CompromisosContrato" onclick="habilitaFundamento();">
								<label class="form-check-label" >Reporte de Contratos con Compromisos</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="Plantilla_ENSA"  id="Plantilla_ENSA" onclick="habilitaFundamento();">
								<label class="form-check-label" >Reporte Proceso Entera Satisfacci&oacute;n</label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio" name="REPORTE"  value="Plantilla_UCACP"  id="Plantilla_UCACP" onclick="habilitaFundamento();">
								<label class="form-check-label" >Formato 421 UCACP "Unidad de Control Administrativo y Contrataciones P&uacute;blicas"</label>
							</div>
						</div>
						<div class="form-group" >
							<div class="form-check form-check-inline" id="tdPDF">
							  <input type="button" class="btnInterfacePDF ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdPdfReporteProg" 		name="cmdPdfReporteProg" 	value="PDF"		onclick="openARCH('pdf');" />
							</div>
							<div class="form-check form-check-inline" id="tdcsv">
							  <input type="button" class="btnInterfaceCSV ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdcsvReporteProg" 		name="cmdcsvReporteProg" 	value="CSV"		onclick="openCSV();" />
							</div>
							<div class="form-check form-check-inline" id="tdXlsx">
							  <input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdXlsxReporteProg" 		name="cmdXlsxReporteProg" 	value="Excel"	onclick="openXLSX();" />
							</div>
						</div>
						
			   		<input type="hidden" id="cIdDocumento" name="cIdDocumento" value="" />
			   		<input type="hidden" id="cAccion" name="cAccion" value="" />
			   		<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>" />
			   		<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
			    	<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
			    	<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
			    	<input type="hidden" name="nTipoIngreso" id="nTipoIngreso" value="0" />
			    	<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
			    	<input type="hidden" name="nTipoReporte" id="nTipoReporte"  value="0"/>
			  	</fieldset>
			  </div>
			</div>
		</form>
	</body>
</html>