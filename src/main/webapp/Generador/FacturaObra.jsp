<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@page import="com.syc.contable.core.CatalogoURFIELBusinessLogic"%>

<%

boolean bAplicadoCont=false;
String DATE_FORMAT = "dd/MM/yyyy";
SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
Calendar c1 = Calendar.getInstance(); // today
String today= sdf.format(c1.getTime());
String cCentroContable="";
String cUR = "";
String cRamo = "";
String algo = "";

Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
if (c == null) {
	response.sendRedirect("../index.jsp");
	return;
}

int idTipoCaso = c.getIdTC();

if (c.getCasoDato("APLICADO_CONT").getValor() != null) {
	if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()))
		bAplicadoCont = true;
}


String mensaje="";
if(request.getParameter("msg")!=null&&!"".equals(request.getParameter("msg"))){
	mensaje=request.getParameter("msg");
	mensaje=mensaje.replace("[","");
	mensaje=mensaje.replace("]","");
	mensaje=mensaje.replace(",","<br>");
}

int id_oper = -1;
if(request.getParameter("id_oper")!=null)
	id_oper= new Integer(request.getParameter("id_oper")).intValue();
else
	id_oper=c.getCasoOperacion(0).getIdOperacion();

Empleado e = new Empleado();
EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
e.setClaveUsuario(usuario.getLogin());
e = ebl.getEmpleado(e);
EmpleadoArea ea = new EmpleadoArea();
ea.setId(e.getClaveArea());
ea = ebl.getEmpleadoArea(ea);

//documentos del caso
CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

String select=c.getTipoCaso().getGavetaAsociada()+"_G"+c.getIdGabinete();//se usa por separado abajo
String cAplicaDocto="No";
if ( request.getParameter("aplicaDocto")!= null && request.getParameter("aplicaDocto").equals("Si")){
	cAplicaDocto="Si";
}
//Valida Centro de Costos
if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
            cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
}

if (cCentroContable.isEmpty() || cCentroContable.equals("")){
		mensaje="Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
}

cUR = usuario.getU_UR();
cRamo = usuario.getU_Ramo();

algo = usuario.getLogin();
String numeroEmpleado = usuario.getNumeroEmpleado();

	/*FAV20171019 Se guarda en base el prefijo de CxP*/
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
	String cxpPrefijo = cabl.getSystemSetting("CXP_PREFIJO") == null?"CP":cabl.getSystemSetting("CXP_PREFIJO");
	
	boolean muestraCBRadicado = "S".equalsIgnoreCase(   cabl.getSystemSetting("MUESTRA_RADICADO_RGOC")  );
	boolean habilitaCBRadicado = "S".equalsIgnoreCase(   cabl.getSystemSetting("HABILITA_SEL_RADICADO_RG_OC")  );
	
	boolean esSAIFonden = "true".equals( cabl.getSystemSetting("SAI_FONDEN") );
	
	CatalogoURFIELBusinessLogic curbl = new CatalogoURFIELBusinessLogic( GestionInterface.ATT_CONEXION );
	boolean permitePagoSinFIEL = curbl.permitePagoSinFiel(cUR);

	boolean esConsulta = c.getCasoOperacion(0).getOperacion().getNumero() == 3;
	
	String cNombreElabora = e.getNombre();
	String cApellidoPaternoElabora  = e.getApellidoPaterno();
	String cApellidoMaternoElabora  = e.getApellidoMaterno();
	String cPuestoElabora = e.getCargo();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Documentos Diversos - Recepción de documentos</title>

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

	<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
	<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>		
	<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>
	<script src="../Generador/js/bootstrap.bundle.min.js"></script>
	<style type="text/css" title="currentStyle">
		@import "css/demo_page.css";
		@import "css/demo_table_jui.css";
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
	</style>
	<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
	
<style>
.notEditable {
	background-color: #CCCCCC;
	color: #000000;
}
</style>
	
		
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker-es.js"></script>
 	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
    <script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/ValidaMesContable.js"></script>
	<script type="text/javascript" src="js/Firmantes.js"></script>
	<script type="text/javascript" src="js/ActualizaFIEL.js"></script>
	<script type="text/javascript" src="js/ImpuestosRetenciones.js"></script>
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
	<script type="text/javascript" charset="utf-8">
	var muestraDivImprimePoliza = false;
	var cNombreElabora = "<%=cNombreElabora%>";
	var cApellidoPaternoElabora  = "<%=cApellidoPaternoElabora%>";
	var cApellidoMaternoElabora  = "<%=cApellidoMaternoElabora%>";
	var cPuestoElabora = "<%=cPuestoElabora%>";
	var refiirma = true;
	
	var bClicBtn = false;
	var breturnVal = false;
	var oTablevFact;
	var relaciones;
	var modalOli;
	
	/*FAV20171019 Se guarda en base el prefijo de CxP*/
	var cxpPrefijo = "<%=cxpPrefijo%>";
	var muestraCBRadicado = <%=muestraCBRadicado%>;
	var habilitaCBRadicado = <%=habilitaCBRadicado%>;
	var esSAIFonden = <%=esSAIFonden%>;
	
	/* Variable que indica si el pago es con firma (FIEL) */
	var permitePagoSinFIEL = <%=permitePagoSinFIEL%>;
	var numeroEmpleado = "<%=numeroEmpleado%>";
	var esConsulta = <%=esConsulta%>;

		$(document).ready(function(){
			
				if( permitePagoSinFIEL ) {
					$("#AutorizaConFielTD").css("display","block");
					$("#autorizadoPorFiel ").val("false");
				}else{
					$("#autorizadoPorFiel ").val("true");
				}
				
				$( "#Limpia" ).button();
				$( "#cancelar" ).button();
				$( "#agrega2" ).button();
				$( "#Agregar" ).button();
				$( "#Agregar1" ).button();
				modalOli= new bootstrap.Modal(document.getElementById('dialog-validaOLI'), 'data-bs-backdrop');
				
				queryFormPost({
					queryName : "tipoAutorizacionRead",
					async:false,
					callback: function(){
						if( $("#TipoAutorizacion").val() == "N" ){
							muestraDivImprimePoliza = true;
						}
					}
				});
				
				$( "#dialog-form" ).dialog({
					autoOpen: false,
					height: 400,
					width: 800,
					modal: true,
					beforeClose: function( event, ui ) {
						return bClicBtn;			
					}
				});
				
			$( "#dialog-Procesando" ).dialog({
				autoOpen: false,
				height: 400,
				width: 400,
				modal: true,
				open: function() {
					var tipo = "aplicarMotor";
					var caNoContrarrecibo = $("#caNoContrarrecibo").val();
					var campo = $("#campo").val();
					var tablaEnc = $("#tablaEnc").val();
					var campoCondicion = $("#campoCondicion").val(); 
					var tablaDet = $("#tablaDet").val(); 
					var tipoAplicar = $("#tipoAplicar").val();
					
					var numEmpleadoCaptura = numeroEmpleado;
					var numEmpleadoVoBo = $("#cboVoBo").val();
					var numEmpleadoAut = $("#cboAutoriza").val();
					
					var autorizadoPorFiel = $("#autorizadoPorFiel").val();
					
					$("#divEsperaProcesando").attr("style","visibility=visible");
					var fAppActualizada = actualizaMesAplicacion();
					
					if( !fAppActualizada){
						$("#divEsperaProcesando").attr("style","visibility=hidden");
						$( "#dialog-Procesando" ).dialog( "close" );
						return false;
					}
					
					/*Validar si la retencion insertada en el detalle corresponde al regimen fiscal del proveedor */			
					queryFormPost("validaRegimenFiscal", {async: false});
					var msgRF = $("#msgRF").val();
					
					if ( msgRF != "" ){
						alert(msgRF);
						$("#dialog-Procesando").dialog("close");
						return; 			
					}
					
					/*Validar la suma de retenciones del detalle vs el encabezado*/			
					queryFormPost("validaRetencionENCvsDET", {async: false});
					var msgRF = $("#msgRF").val();
					
					if ( msgRF != "" ){
						alert(msgRF);
						$("#dialog-Procesando").dialog("close");
						return; 			
					}
					
					/*Validar si el regimen es 626 y el tipo persona es Moral no debe tener retencion RESICO*/			
					queryFormPost("validaRESICOPersonaMoral", {async: false});
					var msgRF = $("#msgRF").val();
					
					if ( msgRF != "" ){
						alert(msgRF);
						$("#dialog-Procesando").dialog("close");
						return; 			
					}
					
					/*ARLA SI LAS FACTURAS ESTAN BORRADAS SE HACE EL INSERT A tPagoFactura Y SE BORRAN DE tPagoFactura_Borrada*/
					queryFormPost("validaExisteFacturas", {async: false});
					
					$.ajax({
							url:'./cierrePresupuestal.jsp',
							type:'post',
							dataType: 'json',
							data:{
								tipo:tipo,
								caNoContrarrecibo:caNoContrarrecibo,
								campo:campo,
								tablaEnc:tablaEnc,
								campoCondicion:campoCondicion,
								tablaDet:tablaDet,
								tipoAplicar:tipoAplicar,
								empleadoCaptura : numEmpleadoCaptura,
								empleadoVoBo : numEmpleadoVoBo,
								empleadoAutoriza : numEmpleadoAut,
								autorizadoPorFiel : autorizadoPorFiel
							},
							success:function(data){
									if(data.sinSesion == 'sinSesion'){
											location.href = "../index.jsp";
									}
									if(data.estatus == "guardado"){
										if( $("#autorizadoPorFiel").val() != "true" ){
											cmdImprimir('PolizaPago');
										}
										alert( "Documento Aplicado Correctamente: "+ $("#caNoContrarrecibo").val() ) ;
										
										$("#cTipoPago").val("PAGOOBRA");
										queryFormPost("cBuscaRadicado", {async: false });
										
										if ($("#cEsRadicado").val()=="S"){
											queryFormPost("devengadoIngresoPago",{async: false });
										}
										
										parent.document.getElementById("pb_send").disabled = false;
										parent.document.getElementById("pb_send").click();		
										breturnVal = true;
									}else{
										breturnVal = false;
										alert( "El Documento No Se Aplico: "+$("#caNoContrarrecibo").val() + " - " + data.estatus ) ;		
									}
									$("#divEsperaProcesando").attr("style","visibility=hidden");
									$( "#dialog-Procesando" ).dialog( "close" );
							}
					});
				},
				close: function() {										
				}				
			});
			
			
			
			creaDiagloFacturas();
			
			
			
			//radicado fijo
			cargaCtaBancariaRFC();
			
		
		$("#chk_ingProp").change(function(){
			if ($("#chk_ingProp").prop("checked")){
				$("#cEsIngProp").val("S");
				$("#chk_radicado").attr("checked", false);
				$("#cEsRadicado").val("N");
				document.getElementById("lbl_radicado").style.visibility = "hidden";
			}else{
				$("#cEsIngProp").val("N");
				$("#chk_radicado").attr("checked", true);
				$("#cEsRadicado").val("S");
				document.getElementById("lbl_radicado").style.visibility = "visible";
			}
		});
			
			$("#destGasto").change(function(){
				if ( $(this).val() != "" ){
					if( $("#cIdRFC").val() == "" ){
						$('#destGasto option[value=" "]').attr('selected','selected');
						Swal.fire("Seleccione","Favor de seleccionar un Beneficiario","info");						
						return false;
					}else{
						$("#mImporteBruto").attr("readonly", true);
						
					}
					if ( $.trim($(this).val())!=""){
						if($("#numPaso").val() == "1" ){
						}
					}else{
						$("#EditaFacturas").css('visibility', 'hidden');
					}
				}
			});
			

				creaDTFacturas();
				$('#Descrip_Concepto').bind('copy paste', function (e) {       
					e.preventDefault();
		    	});


				$("input.AyudaSyC").subIniciaDlg();
				$("input.autoCompletaSyC").subIniciaAutoCompleta();
				
				$("#OIRAUSU").val( "<%=algo%>" );
				$("#cCentroContable").val( "<%=cCentroContable%>");			
				$("#id_caso").val(<%=request.getParameter("folio")%>);
				$("#cNoFactura").val("POBR-"+$("#id_caso").val());
				
				$("#tabs").tabs( 
					{"show": function(event, ui) {
						var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
						if ( oTable.length > 0 ) 
						{
				      		oTable.fnAdjustColumnSizing();
				      	}
	        		}
	    		} );
			
		$('#tblDevengado1').dataTable(
				{
				"bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"

				} );
		$('#grdCompromisos').dataTable(
				{
				"bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"

				} );
		$('#grdFacturas').dataTable(
				{
					sScrollY: "150px",
					sScrollX: "700px",
					sScrollXInner: "200%",
					"bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"

				} );
		$('#grdRetClave').dataTable(
				{
				"bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"

				} );
 oTable = $('#tretencion').dataTable( );
		$('#tretencion').dataTable(
				{
				"bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"
				} );
	$(function() {
		$( "#desde" ).datepicker({
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true 
		});
	});
	$(function() {
		$( "#hasta" ).datepicker({
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true 
		});
	});
	$(function() {
		$( "#fRecepcion" ).datepicker({
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true 
		});
	});
	$(function() {
		$( "#fecha_Pago" ).datepicker({
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true 
		});
	});
	$(function() {
		$( "#DCD_FECHA_FACTURA" ).datepicker({
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true 
		});
	});

	$("#frmcontratoDeObra").ajaxForm({
				dataType:  "json",
				success: formSubmited
			});

	var suma = 0;
	var sredondea=0;
	var res_iva=0;
	
	
	$("#cNoEstimacion").change(function () {
		if($(this).val()=='-1'){
			Swal.fire("Seleccione","Seleccione una Estimacion","info");
		}else{
			var estimacionValida = true;
			if ($("#cValidaEstimacion").val() == "S"){
				$("#noEstimacion").val( $(this).val() );
				$("#cCveContrato").val( $("#cIDContratoObra").val() );
				$("#existeEstimacion").val( "" );
				
				queryFormPost("existeEstimacionRead", {async: false });
				if ($("#existeEstimacion").val() == "" && $("#noEstimacion").val()!=""){
					Swal.fire("Verifique!","No Existe la Estimación", "error");
					$(this).val( "" );
					estimacionValida = false;
					$("#mmontoestimacion").val(0);
					
					$("#mmontoestimacionIva").val(0);
					$("#mmontoestimacionMasIva").val(0);
					$("#mmontoestimacionAmortizado").val(0);
					$("#mmontoestimacionRetencion").val(0);
				}else{
					$("#existeEstimacion").val( "" );
					queryFormPost("existeEstimacionFactRead", {async: false });
					if ($("#existeEstimacion").val() == "1"){
						Swal.fire("Verifique!","La Estimación ya fue Utilizada en Otro Pago","warning");
						$(this).val( "" );
						estimacionValida = false;
						$("#mmontoestimacion").val(0);
						$("#mmontoestimacionIva").val(0);
						$("#mmontoestimacionMasIva").val(0);
						$("#mmontoestimacionAmortizado").val(0);
						$("#mmontoestimacionRetencion").val(0);
					}
				}
			}
			if (estimacionValida) { 
				leeMontosEstimacion();
				validaImporteEstimacion(); 
				calculaIVA();
			}
		}
	});
	
	
	$("#verFondenSPAN").hide();
		
	
	$( "#dlgSeleccionaFonden" ).dialog({
		autoOpen: false,
		height: 200,
		width: 320,
		modal: true,
		buttons: {
			"Aceptar": function() {
				if(<%=id_oper%> == 1  ){
					if( $("#proyectoFonden").val() == "" ){
						Swal.fire( "Seleccione","No ha seleccionado un proyecto de FONDEN","warning");
					}else{
						$.blockUI();
						$("#cProyectoFonden").val( $("#proyectoFonden").val() );
						queryFormPost({
							queryName:"proyectoFondenOB_Update",
							async:false,
							callback:function(){
								$( "#dlgSeleccionaFonden" ).dialog("close");
							}
						});
						$.unblockUI();
					}
				}
				else
					$( "#dlgSeleccionaFonden" ).dialog("close");
				
			},
			"Cerrar":function(){
				if(<%=id_oper%> != 1  ){
					$("#dlgSeleccionaFonden").dialog("close");
				}else{
					if( $("#proyectoFonden").val() == "" ){
						Swal.fire("No ha capturado el proyecto de FONDEN.","No podra continuar el pago hasta capturarlo.","warning");
					}else{
						$("#dlgSeleccionaFonden").dialog("close");
					}
				}
			}
		}
	});
	
	cssReadOnly();
});

		function aceptarOli() {
			var vEP = $("#EP").val();
			var vCapitulo = "%" + vEP.substring(31, 36) + "%";
			var vCartera = vEP.substring(44, 55);
			var vUnidEj = vEP.substring(56, 59);
			var vUnidRe = vEP.substring(60, 64);
			var url = '../proimpro/readInfo';
			$.ajax({
				url : url,
				dataType : 'json',
				data : {
					"accion" : "READ_IMPORTEOLI",
					"cartera" : vCartera,
					"capitulo" : vCapitulo,
					"oli": $("#cOLI").val(),
					"ue":vUnidEj
				},
				async : false,
				success : function(RS) {
					var exito = RS.success;
					if(exito=="true"){
						var MontoOlis = RS.data_1.result;
						if (Number( MontoOlis ) > 0){
							$("#cartera").val( vCartera );
							$("#capitulo").val( vEP.substring(31, 36) );
							$("#UE").val( vUnidEj );
							$("#mImporteCartera").val("0.00");
							queryFormPost("vAcumulaImporteCarteraEP", {async: false });
							var importeTotal = Number( $("#mImporteCartera").val() ) + Number( $("#montoDev").val() );
							var importeTotal = importeTotal.toFixed(2);
							
							if( Number( importeTotal ) <= Number( MontoOlis ) ){
								cambioMovmientos();	
							}else{
								Swal.fire("Verifique!","El Importe Capturado sobregira la Cartera ","warning");
							}
						}else{
							Swal.fire("Verifique!","El Número de OLI no es Válido ","error");
						}
					}else{
						var msg = RS.data_1.result;
						Swal.fire("Error al intentar validar OLI:", msg,"warning");
					}
				},
				error : function(xhr, textStatus, errorThrown) {
					alert("Advertencia: " + xhr.responseText + "\nEstatus: "
							+ textStatus + "\n" + errorThrown);
					r = true;
				}
			});
			modalOli.hide();
		}
		
		function cerrarOli() {
			breturnVal = false;
			modalOli.hide();
		}

function muestraJson(){
		for( i = 0; i < relaciones.length; i++ )
			alert( relaciones[i].folioIngreso );
	}

function guardaIngresoPago(){
		
		var table = document.getElementById("arrIngresoPago");
		var totalIngresoPago = 0;

		queryFormPost("buscaIngresoPago", {async:false});
		
		if ($("#nExisteIngresoPago").val() == "0"){
			queryFormPost("encabezadoIngresoPago", {async:false});
			queryFormPost("buscaIngresoPago", {async:false});
		}
		else{
			queryFormPost("getTotalIngresoPago",{async:false});
			totalIngresoPago = parseFloat($("#mTotalIngresoPago").val());
		}
		
		for( i = 0; i < relaciones.length; i++ ){
			
			if( relaciones[i].folioIngreso ){	
							
				$("#nExisteIngresoPago").val(); // nFolioIngresoPago
				$("#nFolioRegistroIngreso").val(relaciones[i].folioIngreso); //nFolioRegistroIngreso
				$("#cEPIngresoPago").val(relaciones[i].EP);//EP
				$("#nMesIngresoPago").val(relaciones[i].mes);//nMes
				$("#mImporteIngresoPago").val(relaciones[i].monto);//mImporte
				
				totalIngresoPago = totalIngresoPago + parseFloat(relaciones[i].monto);
		
				queryFormPost("detalleIngresoPago", {async:false});
			}
		}
		
		$("#mTotalIngresoPago").val(totalIngresoPago.toString());
		queryFormPost("updateIngresoPago",{async:false});		
		
		relaciones.length=0;
	}

function  validaImporteEstimacion(){
	queryFormPost("leeImporteEstimacion", {async: false });
	$("#mmontoestimacion").val(numberFormat(Number($("#mmontoestimacion").val()).toFixed(2)));
	$("#mmontoestimacionIva").val(numberFormat(Number($("#mmontoestimacionIva").val()).toFixed(2)));
	$("#mmontoestimacionMasIva").val(numberFormat(Number($("#mmontoestimacionMasIva").val()).toFixed(2)));
	$("#mmontoestimacionAmortizado").val(numberFormat(Number($("#mmontoestimacionAmortizado").val()).toFixed(2)));
	$("#mmontoestimacionRetencion").val(numberFormat(Number($("#mmontoestimacionRetencion").val()).toFixed(2)));
}

function validaEFO() {
		
		var esEFO = false;
		$("#esEFO").val("");
		$("#rfcValidar").val($("#cIdRFC_RelacionGasto").val());
		
		queryFormPost({
			queryName : "validaEFO",
			async : false,
			callback : function() {
				esEFO = $("#esEFO").val() == "S";
			}
		});
		
		return esEFO;
	} 
function cssReadOnly(){
	$( "[readOnly]" ).each(function(){	
		$(this).addClass("notEditable");	
	});
}
	function onSubmit(id_oper){//validaciones del boton guardar
  		montosFormatoQuitar();
  		var p = window.parent;
  		var valida_campos = true;
  		
  		if ($("#docAplicado").val() == "S" && !refiirma) {
  			alert("Documento ya fue aplicado y se avanzará a modo de CONSULTA");
  		}else {
				var esEFO = validaEFO();
				if( esEFO ){
					alert("No se puede realizar el pago a un EFO. Solicite mas informacion con el administrador");
					return false;
				}
				
  		  		$("#facturasDiferentes").val("0");
				queryFormPost("rfcDiferentesRead", {async : false});
				if( $("#facturasDiferentes").val() != "0" ){
					alert( "El pago contiene facturas para otro proveedor diferente a [" + $("#cIdRFC").val()  + "] por lo que no puede continuar." );
					return false;
				}
			try{
				if(id_oper==1){
				
					if ($.trim($("#ctaBancaria option:selected").text())==""){
						alert("El beneficiario " + $("#cIdRFC").val() + " no tiene dada de alta alguna cuenta bancaria, por lo que no se puede proceder con el pago.");
						return;
					}
					
					var importeSancion = 0;
					importeSancion = Number( quitaFmt( $("#mImporteSancion").val() ) );
					
					var diferencia = 0.00;
					var mPenaMasIVA = 0.0;
					mPenaMasIVA = importeSancion * 1.16; 
					
					diferencia = Math.abs(Number(parseFloat(quitaFmt($("#mmontoestimacion").val())).toFixed(2)) - Number(parseFloat($("#mEstimacion").val()).toFixed(2))).toFixed(2);
					//SASV 12/05/2015 Validacion de montos (Estimacion vs Factura)
					if (diferencia>0.01 && Number(parseFloat(quitaFmt($("#mImportePenalizacion").val())).toFixed(2)) == 0){
						alert("El importe de la Factura ("+$("#mImporteNeto").val()+") no corresponde al importe de la estimacion("+$("#mmontoestimacion").val()+").");
						return false;
					}
					
					//SASV 26/05/2015 Validacion de montos (Factura vs Calculado)
					diferencia = 0.00;
					diferencia = Math.abs(   Number(  parseFloat( quitaFmt( $("#mImporteNetoFactura").val() )  ).toFixed(2)  ) - Number(parseFloat($("#mImporteNeto").val()).toFixed(2)) - mPenaMasIVA ).toFixed(2);
					if(diferencia>0.01 && Number(parseFloat(quitaFmt($("#mImportePenalizacion").val())).toFixed(2)) == 0){
						alert("El importe de la Factura("+ $("#mImporteNeto").val() +"( no corresponde al importe de calculado en la Caratula("+$("#mImporteNetoFactura").val()+").");
						return false;
					}
					//SASV 03/04/2016 Validacion de montos (Estimacion vs Calculado)
					
					
					diferencia = 0.00;
					diferencia = Math.abs(  Number(  Number(  parseFloat(  $("#mImporteBrutoFactura").val()).toFixed(2) ) +  Number(  parseFloat( $("#mIvaFactura").val()).toFixed(2))).toFixed(2) - Number(parseFloat($("#mImpEjercer").val()).toFixed(2))).toFixed(2);
					
					var diferencia2=0.00;
					diferencia2 =(   parseFloat(  $("#mImporteNetoFactura").val()  ).toFixed(2) - parseFloat(  $("#mImporteNeto").val()  ).toFixed(2) - mPenaMasIVA  ).toFixed(2);
					
					if (diferencia>0.01){
						if(diferencia2>0.01){
							Swal.fire("Verifique","No se puede proceder con el pago debido a que el importe de la factura no corresponde al Calculado.","info");
						return false;
						} else if(diferencia2 != 0){
							if (!confirm("¿Tu pago saldra con diferencia de $"+diferencia2+", deseas continuar?")){
					   			Swal.fire("Borrar","Favor de Borrar el tramite.","warning");
					   			return;
					   			}
				   		}
					}else if(diferencia != 0||diferencia2 != 0){
						if (!confirm("¿Tu pago saldra con diferencia de $"+diferencia+", deseas continuar?")){
							Swal.fire("Borrar","Favor de Borrar el tramite.","warning");
				   			return;
				   		}
				   	}
				   if($("#destGasto").val()=="ANPO"){
				   		//validar que el anticipo que se va pagar sea igual al generado en la captura del contrato
				   		queryFormPost("leeImporteAnticipo", {async: false });
				   		if($("#mImporteNeto").val()==$("#mTotalAnticipo").val()){
				   			Swal.fire("Verifique!","El Importe de Anticipo no coincide con el registrado en el Contrato","warning");
				   			return;
				   		}
				   						   		
				   }
				}
				
				//validaciones de la forma
				//poner aqui las validaciones a efectual en el boton guardar, en este caso no hay
				
				//Guardado de los campos correspondientes a cada variable de caso
				p.gestion.setFolio( $("#FOLIO").val() );
				p.gestion.setOperador( $("#OPERADOR").val() );
				p.gestion.setFechaDocumento( $("#FECHA_CARGA").val() );
				p.gestion.setEjercicioFiscal( $("#aEjercicioFiscal").val() );
				p.gestion.setConceptoMov("Aplicación Pago Obra");
				p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
				p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
				p.gestion.setAplicadoCont("false");
	
	
				var nretval = cmdGuardar();
				if (nretval == -1) {
					return false;
				}
	
				if(id_oper==2){			
					$("#campo").val( "nFolio" + $("#cDocumento").val() );
					$("#tablaEnc").val( "t" + $("#cDocumento").val() + "Encabezado" );
					$("#campoCondicion").val( "caNoContrarrecibo" ); 
					$("#tablaDet").val( "t" + $("#cDocumento").val() + "Detalle" ); 
					$("#tipoAplicar").val( $("#cDocumento").val() );
					
					tipoFirmantes();
				}

			} catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}
		}
			return valida_campos;
}

	function procesar(){
		$( "#dialog-Procesando" ).dialog( "open" );
		return breturnVal;
	}
	
 	function fnAplicaMotor()
 	{
 		divAplica.innerHTML = "<iframe id='ifAplica' src='../gstnmngr/AppCont?elContra=" + $('#caNoContrarrecibo').val() + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
 	}


  	function onPostSubmit(id_oper){//validaciones del boton enviar
		if (id_oper==1)
			alert("Folio del Pago: " +$("#caNoContrarrecibo").val());
  		return true;
  	}

    function onLoadPlantilla(){
    	carga();
		if(<%=id_oper==1%>){
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=false;
			
			
		}
		if(<%=id_oper==2%>){
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=true;
			$("#chk_radicado").attr('disabled', true);
		}

		if(<%=id_oper==3%>){
			if( muestraDivImprimePoliza ){
				$("#EditaFirmas").css('visibility', 'visible');
			}
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=true;
		}
		
		$("#cDocumento").val("PAGOOBRA");

  	}
  	
	function BuscaPoliza(){
		var szWhere = " nfolioPagoObra = " + $("#id_caso").val();
		var elMonto = "1";
		$("#laPoliza").val("");
		$("#docAplicado").val("");
		var szTabla = "TPAGOOBRADOCAPLICADOREAD";
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
			for (var i = 0; i < j.length; i++) {
				$("#laPoliza").val(j[i].Col0);
				$("#docAplicado").val(j[i].Col1);
			}
		});
	}
  	
  	function ResponsableSiguiente(id_oper){

  		 if(id_oper==1){
 			return "AUTORIZA_" + $("#cDocumento").val();
		 }if(id_oper==2){
  		 	return "CONSULTA_" + $("#cDocumento").val();
		}
  				
  	}
  	  	
  	function OperacionSiguiente(id_oper){

  		if(id_oper==1)
  		 	return "autoriza_factura";
  		if(id_oper==2){
		
  		 	return "consulta_factura";}
  	}

	function onCancel(id_oper){
		queryFormPost(	"TFactOBRAEncabezadoUpdate", {async: false });
		queryFormPost("borraFactRelacionPagoBorrado", {async: false });
		return true;
	}
	
	
	function onPostDisplay(id_oper){
		if ($("#docAplicado").val() == "S" && !refiirma ) {
			parent.document.getElementById("pb_send").disabled = false;
			parent.document.getElementById("pb_send").click();
		}	
	}
	
	
	function formSubmited() {
            }

	function fnClickAddRowComp() {
		
		var exito = actualizaRetenciones();
		if( exito ){
			
			queryFormPost("leeImporteTotalPagoObraEncabezado",{async: false });
			queryFormPost("leeImporteTotalPagoObraDetalle",{async: false });
			
			var diferencia = Number($("#importeTotalEncabezado").val()) - Number($("#importeTotalDetalle").val());
			if (diferencia < 0.02 && diferencia > - 0.02) {
				queryFormPost("actualizaTotalUpdate",{async: false });
				$("#importeTotalEncabezado").val($("#importeTotalDetalle").val() ) ;
			}
			if ($("#importeTotalEncabezado").val() != $("#importeTotalDetalle").val()){
				Swal.fire("Borrar","No se puede seguir con el pago debido a que el detalle no corresponde al encabezado.","info");
				return;
			}
			$('#grdFacturas').dataTable().fnAddData( [
			$("#DCD_FACTURA").val(),
			$("#DCD_FECHA_FACTURA").val(),
			$("#DCD_TBEN").val(),
			$("#DCD_CBEN").val(),
			$("#DCD_TIPO_OPE").val(),
			$("#DESCRIPCION20").val(),
			$("#DCD_IMP_BRUTO").val(),
			$("#DCD_IVADES").val(),
			$("#DCD_IVA").val(),
			$("#DCD_ISR").val(),
			$("#DCD_MIL5").val(),
			$("#DCD_MIL2").val(),
			$("#DCD_CONTRIBUCION").val(),
			$("#DCD_OTRAS_RET").val(),
			$("#DCD_PENALIZACION").val()
			] );
			$("#fRecepcion2").val($("#fRecepcion").val());
				
			getNextSequenceVal({seqName: "CR-" + $("#cCentroContable").val(), async: false, callback: setSequenceVal});
		
			setTimeout("contrareibo()",1000);
			parent.document.getElementById("pb_send").disabled=false;
			return true;	
		}
		return false;
		
	}
	
function setSequenceVal(seqValue) {
	seqValue = "000000" + seqValue;
	seqValue = seqValue.substr(seqValue.length - 6);
	seqValue = "1" + seqValue.substr(seqValue.length - 5);
	seqValue = $("#cCentroContable").val() + cxpPrefijo + $("#aEjercicioFiscal").val() + seqValue;
	$("#caNoContrarrecibo").val( seqValue );
}

	function contrareibo(){
		$("#cIDContratoObra").attr('disabled', false);
		queryFormPost("SIG_FOLIO_CONTRAOBRAUpdate", {async: false });
		queryFormPost("StatusAnticipoFACTOBRAUpdate", {async: false });		
		queryFormPost("tDocumentacionComprobatoriaDetCreate",{async: false });
		$("#divImprime").show();
		$("#Agregar1").attr('disabled', true);
		setTimeout("elRetardo()",1000);
	}	
	
	function fnClickAddRowB(A, B,C) {
		$('#tretencion').dataTable().fnAddData( [A, B, C] );
	}
	
	function cmdBorrar(){
		//SASV 13/05/2015 Regresa estatus a la estimacion para poder ser modificada.
		$("#esPagada").val(0);
		$("#cIDContratoObra").val($("#cFolioContratoObra").val());
		queryFormPost("ActualizaEstatusPagoEstimacion",{async: false });
		$("#elcontra").val($("#caNoContrarrecibo").val());
				
		parent.document.getElementById("pb_send").disabled = true;
		parent.document.getElementById("pb_cancel").disabled = false;
		parent.document.getElementById("pb_cancel").click();
		parent.document.getElementById("pb_cancel").disabled = true;
		queryFormPost("borraFactRelacionPagoBorrado", {async: false });
		
	}
	function folio(){ 
		queryFormPost("tFacturaContratoObra1Read", {async: false });
		/*
		 * En CONAFOR no se usa AMF
		queryFormPost("tPagoAMFRead", {async: false });
		*/
		setTimeout("reten()",1000);
	}
	function reten(){
		montosFormatoQuitar();
		$("#cIDContratoObra").val($("#cFolioContratoObra").val());
		
		queryFormPost("FolioCompromisoContratoObra",{async: false });// Se agrega Para mostrar el nFolioCompromiso

		if ($("#cIDContratoObra").val()!=""){
			FiltroRetenciones();
			$("#cIdRFC_RelacionGasto2").val($("#cIdRFC").val());			
			$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());
			$("input[id='iva']").val($("input[id='nPorcIVAAplicable']").val());
			$("input[id='nIdConcepto']").val($('#TIPO_CONCEPTO option:selected').val());
			$("input[id='tConcepto2']").val($('#TIPO_CONCEPTO option:selected').text());
			$("input[id='noFactura2']").val($("input[id='cNoFactura']").val());
			$("input[id='cIdTipoOperacion2']").val($('#cIdTipoOperacion option:selected').val());
			$("input[id='tipoOper']").val($('#TIPO_OPERACION option:selected').val());
			$("input[id='impTotal']").val($("input[id='mImporteNeto']").val());
			$("#cIDContratoObra2").val($("#cIDContratoObra").val());		

			$("#impTotal2").val($("#impTotal").val());
			$("#iva").val($("#nPorcIVAAplicable").val());
			$("#poriva").val($("#mImporteIVA").val());
			queryFormPost("tRelacionGastClaveBeneficiarioRead","cIdRFC_RelacionGasto2",{async: false });		 
			$(".pasoDos").show();
			
			if(<%=id_oper%> != 3){
				
				parent.execOperacion();
				parent.execResponsable();
			
			}
			if(<%=id_oper%> == 3){
				$("#cancelar").hide();
			}
			
			claves();
			
			$(".subtotall").attr('disabled', true);
			$("#guardar").attr("disabled", true);
			
			setTimeout("retraso3()",100);				
			setTimeout("retraso5()",100);	
			
			$("#DCD_FACTURA").val( $("#cNoFactura").val());
			$("#DCD_FECHA_FACTURA").val($("#fRecepcion").val());
			$("#DESCRIPCION20").val($("#nPorcIVAAplicable").val());
			$("#DCD_IMP_BRUTO").val($("#mImporteBrutoFactura").val());
			$("#DCD_IVA").val($("#mImporteIVA").val());
			$("#DCD_NETO").val($("#mImporteNeto").val());				
			$("#DCD_SANCION").val($("#mImporteSancion").val());				
			$("#DCD_DEVOL").val($("#mImporteDevolucion").val());
			$("#DCD_CONCEPTO").val( $("#Descrip_Concepto").val());
			$("#DCD_AMORT").val( $("#mAmortizacionAnticipoMasIva").val() );//SASV 14/05/2015
			$("#DCD_RETENCION").val($("#mImporteRetencion").val() );
			$("#DCD_CONTRIBUCION").val( $("#mImporteFlete4").val() );
			$("#DCD_PENALIZACION").val( $("#mTesofe").val() );
			
			cssReadOnly();
			
		}else{
			$(".pasoDos").hide();
			$(".pasoTres").hide();
		}
	}	

	function retraso5(){
		
		if ($("#cllave").val()!=""){
		
		$("#Limpia").hide();
			$("#Limpia").attr('disabled', true);
			$(".paso9").attr('disabled', true);
			$(".pasoTres").show();	
		}
	}		
	function retraso3(){
		if ( $("#caNoContrarrecibo").val() !="0"  && (<%=id_oper%> == 2 )  ){
			$("#cIdRFC_RelacionGasto2").val($("#cIdRFC_RelacionGasto").val());  
			queryFormPost("tRelacionGastClaveBeneficiarioRead","cIdRFC_RelacionGasto2",{async: false });						  
			$("#Agregar1").attr('disabled', true);   
			$(".paso03").attr('disabled', true);   
			$("#numPaso").val('6');		
			
		}else{
			$(".paso03").attr('disabled', false);
		}
	}	
	function DOCUMENTACION(){    	
		var szWhere = "";     
		szWhere = " caNoContrarrecibo ='"+  $("#caNoContrarrecibo").val()+"'";
		
		var elMonto = "";     
		var este=0;          
		var szTabla = "DOCUMENTACION_RELACION_GASTO";                                                                                         
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){                       
			var options = '';
			for (var i = 0; i < j.length; i++) {              			
				$('#grdFacturas').dataTable().fnAddData( [
				j[i].Col0, j[i].Col1, j[i].Col2,j[i].Col3,j[i].Col4,j[i].Col5,j[i].Col6,j[i].Col7,j[i].Col8,j[i].Col9,j[i].Col10,j[i].Col11,j[i].Col12,j[i].Col13,j[i].Col14	
				] );
				$("#DCD_FACTURA").val(j[i].Col0);
				$("#DCD_FECHA_FACTURA").val(j[i].Col1);
				$("#DCD_TBEN").val(j[i].Col2);
				$("#DCD_CBEN").val(j[i].Col3);
				$("#DCD_TIPO_OPE").val(j[i].Col4);
				$("#DESCRIPCION20").val(j[i].Col5);
				$("#DCD_IMP_BRUTO").val(j[i].Col6);
				$("#DCD_IVADES").val(j[i].Col7);
				$("#DCD_IVA").val(j[i].Col8);
				$("#DCD_ISR").val(j[i].Col9);
				$("#DCD_MIL5").val(j[i].Col10);
				$("#DCD_MIL2").val(j[i].Col11);
				$("#DCD_CONTRIBUCION").val(j[i].Col12);
				$("#DCD_OTRAS_RET").val(j[i].Col13);
				$("#DCD_PENALIZACION").val(j[i].Col14);		
				$("#DCD_NETO").val($("#mImporteNeto").val());				
				$("#DCD_SANCION").val($("#mImporteSancion").val());				
				$("#DCD_DEVOL").val($("#mImporteDevolucion").val());
				$("#numPaso").val("4");
				$("#L04").click();
			}
			if($("#numPaso").val() == "4" ){
			
			$("#numPaso").val("6"); // Se agrega 6 Para Guardar el documento ultimo Paso
			$("#divImprime").show(); //para CONAFOR*/
			$("#Agregar1").attr("disabled",true);
			
				if(<%=id_oper%> == 1){
					parent.document.getElementById("pb_save").disabled = true; // CONAFOR
					parent.document.getElementById("pb_send").style.visibility='visible';
					parent.document.getElementById("pb_send").disabled = false;
				}
				if(<%=id_oper%> == 2){
					parent.document.getElementById("pb_save").disabled = false; // CONAFOR
					
				}
			}
			
		}) ;  
		
	}
	function  compDocumentacion(){
		var importeSancion = 0;
		importeSancion = Number( $("#mImporteSancion").val() );
		importeSancion = importeSancion.toFixed(2);
    	mPenaMasIVA = importeSancion * 1.16;
    	
		montosFormatoQuitar();
		$("#DCD_FACTURA").val( $("#cNoFactura").val());
		$("#DCD_FECHA_FACTURA").val($("#fRecepcion").val());
		$("#DESCRIPCION20").val($("#nPorcIVAAplicable").val());
		$("#DCD_IMP_BRUTO").val($("#mImporteBrutoFactura").val());
		$("#DCD_IVA").val($("#mImporteIVA").val());
		$("#DCD_NETO").val($("#mImporteNeto").val());				
		$("#DCD_SANCION").val($("#mImporteSancion").val());				
		$("#DCD_DEVOL").val($("#mImporteDevolucion").val());
		$("#DCD_CONCEPTO").val( $("#Descrip_Concepto").val());
		$("#DCD_AMORT").val( $("#mAmortizacionAnticipoMasIva").val() );
		$("#DCD_RETENCION").val($("#mImporteRetencion").val() );
		$("#DCD_CONTRIBUCION").val( $("#mImporteFlete4").val() );
		$("#DCD_PENALIZACION").val( mPenaMasIVA );
		
		if ($("#DCD_RETENCION").val() > 0 )
				CalculaRetencionesG();	
	}
	
	function claves(){        
		var contador=0;                          
		var szWhere = "";     
		szWhere = " tr.nFolioPAGOOBRA ="+  $("#id_caso").val();
		var elMonto = "";     
		var este=0;          
		var szTabla = "CLAVES_OBRA";                                                                                         
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){                       
			var options = '';
			for (var i = 0; i < j.length; i++) {              
				$("#montoDev").val( j[i].Col3 );
				$("#montoDev").formatCurrency();
				$('#grdCompromisos').dataTable().fnAddData( [	j[i].Col0,j[i].Col2, $("#montoDev").val() ] );
				$("#montoDev").val( "" );
				contador=contador + 1;
				$("#cllave").val(contador);
				$("#numPaso").val("3"); // Conafor
				$(".pasoTres").show();
				$("#L04").click();
				$("#nIdClaveEgresos2").attr('disabled', true);
			}
			
			if($("#numPaso").val() == "3" ){ // CONAFOR
				if(<%=id_oper%> != 3){
					parent.document.getElementById("pb_save").disabled = true; // CONAFOR
				}
				DOCUMENTACION(); // CONAFOR
			}
		}) ;  	
	}			  

	function cambioss(){
		$("#EP").val( $("#ep").val() );
		var vCartera = $("#EP").val();
		vCartera = vCartera.substring(44, 55);
		$('#grdRetClave').dataTable().fnClearTable(); 
		
		//VGC20150424 Para saltar la validacion. Quitar al definir la validacion de olis
		//vCartera = "00000000000";
		//VGC20150424 
		
		if(vCartera == "00000000000" || $("#IEsPasivo").val() == "1"){
			cambioMovmientos();	
		}else{
			modalOli.show();  
		}
	}

	
	function cambioMovmientos(){
		
		var vOGT = $("#ep").val();
	    vOGT = vOGT.substring(31, 36);
		$("#vOGT").val( vOGT );

		queryFormPost("tMsgPartidaRead", {async: false });
		
		if( $("#obs").val() != '' ){
			alert($("#obs").val());
		}
		$("#montoDev").val(quitaFmt($("#montoDev").val())); //SASV 25/06/2015 Quitar formato del monto a devengar
		
		var validM;
		var validacion=0;				
		validM = Number(  Number( $("#saldoCompM").val() ) + Number( $("#montoDev").val() )  ).toFixed(2);
								                           
		if ( validM  >  Number( $("#mImpEjercer").val()) ){
			alert("Ha superado el Importe a Ejercer");
			$("#montoDev").val("");
			$("#ep").val("");
			$("#codSIAFF").val("");
			return;
		}	 
		validM = Number( $("#montoDev").val() );						                           
		var campos="";
		var szWhere="";
		var elMonto=12;
		
		$("#cMes").val($("#fRecepcion").val().split("/")[1]);
		
		campos = $("#cMes").val()+ ", '" + $("#cIdContrato").val() + "', '" + $("#cCentroContable").val() + "', 'OB', '" + $("#ep").val() + "'";
		szWhere = "";		//" clavesiaff =substring('" + $("#ep").val() + "',1,55)  ";
                 var szTabla = "SALDOS_DISPONIBLE_PAGOOBRA";
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto,Campos:campos, Order:" order by 1 desc ", ajax: 'false'}
		, function(j){
                      
					var acumulado = 0.0;
					for (var i = 0; i < j.length; i++) {            
						acumulado = parseFloat(acumulado) + parseFloat(j[i].Col2);
						acumulado= acumulado.toFixed(2);
						$("#TOTALSUBCUENTA").val(acumulado);
					}	
					
					if (acumulado < validM){
						Swal.fire('Verifique!','La cuenta no tiene suficiente saldo comprometido','info');
						return;
					}	
					var resto = parseFloat(validM);
					var aplicar = 0.0;
					for (var i = 0; i < j.length; i++) {
						if (resto-parseFloat(j[i].Col2) >0){
							aplicar = j[i].Col2;
						} else{
							aplicar = resto;
						}
						resto = resto-aplicar;
						if (parseFloat(aplicar) > 0.0) {
							$('#grdRetClave').dataTable().fnAddData( [
							j[i].Col1,
							j[i].Col0,
							aplicar
							] );
						}
						$("#agrega2").attr('disabled', false);
					}
			}) ;  
	}
	
	
	function FiltroMovimientos(){ 
		montosFormatoQuitar();
		$("#nombre").val($("#cobjetocontrato").val());
		//$("#DCD_IMP_BRUTO").val($("#mImporteBrutoFactura").val());	
		
		var vOGT = $("#ep").val();
		vOGT = vOGT.substring(31, 36);
		$("#cOBGT").val(vOGT);
		document.getElementById("TIPO_CONCEPTO").removeAttribute("disabled",false);
		$("#cEvento").val( "" );
		
		if($("#cEsIngProp").val() == "S"){
			queryFormPost("eventpEP4Read", {async: false });
		}else{
			queryFormPost("eventpEP3Read", {async: false });
		}
		
		if ($("#cEvento").val() == "" ){
			Swal.fire('Verifique el evento','La cuenta no corresponde al concepto','info');
			return;
		}
			
		if ($("#cEsRadicado").val() == "S"){
			$("#cEvento").val("DR_"+$("#cEvento").val());
		}
		else{				
			$("#cEvento").val("D_"+$("#cEvento").val());
		}
	
		var mImporteADev = $("#montoDev").val();
		$("#montoDev").formatCurrency();
		
		var msg = "";
		try{
			msg = registraCalendarioEP($("#ep").val(), quitaFmt( mImporteADev) );
		}catch(err){
			msg = "ocurrio el siguiente error generando calendario: " + err.message;
		}
		
		if( msg != ""){
			Swal.fire("Error", msg ,"error");
			return false; 
		}
		
		$('#grdCompromisos').dataTable().fnAddData( [
			$("#codSIAFF").val(),
			$("#ep").val(),
			$("#montoDev").val()
		] );
		$("#montoDev").val( mImporteADev );
		
        var mTotalEjercer = parseFloat( $("#saldoCompM").val() );
		var table = document.getElementById('grdRetClave');
		var rowCount = table.rows.length;  
		          		
		for(var i=1; i<rowCount; i++) {
			var row = table.rows[i];                		
			var elMesGrd = '';
			var elMontoGrd = '';
			var elMesGrd = row.cells[1].childNodes[0].nodeValue;
			var elMontoGrd = row.cells[2].childNodes[0].nodeValue;
			var mess;
			
			if(null != elMesGrd ) {
				if(elMesGrd != ""){
					if(elMesGrd.length < 2 )
						mess='0'+ elMesGrd;
					
					$("#cMes").val(mess);	
					$("#DCD_IMP_BRUTO").val(elMontoGrd);

					if ($("#lHaySaldoAnticipo").val() == 0 || Number( $("#mAmortizado").val() ) > 0){			
						calculaRetencionesEP ( $("#DCD_IMP_BRUTO").val() );
						actualSaldosDet(1);
					}else{
						caculaIva($("#DCD_IMP_BRUTO").val());
					}

					mTotalEjercer = Number( mTotalEjercer ) + Number( elMontoGrd) ;
					mTotalEjercer = mTotalEjercer.toFixed(2);
					if ( Number( mTotalEjercer ) == Number( $("#mImpEjercer").val() ) ){
						AjustaRetencionesD();
					}

					/*VGC Aqui inserta el detalle*/
					queryFormPost("tContratoObraFacturaDetalleCreate",{async: false });
					
					table.deleteRow(i);
					rowCount--;
					i--;
					
					if($("#cEsRadicado").val()=="S")
					{
						guardaIngresoPago();
					}
				}
			}		
		}
		
	validacion = Number( Number($("#saldoCompM").val() ) + Number( $("#montoDev").val()) ).toFixed(2);
	
	if ( validacion  > parseFloat( $("#mImpEjercer").val()) ){
		$("#montoDev").val("0");
		validacion=0;
	}else{
	
		$("#saldoCompM").val( parseFloat( $("#saldoCompM").val() ) + parseFloat( $("#montoDev").val()));
		
		validacion=0;
		if ( parseFloat( $("#saldoCompM").val() ) == parseFloat( $("#mImpEjercer").val()) ){
				$("#montoDev").val("");
				$("#ep").val("");
				$("#codSIAFF").val("");
				$(".pasoTres").show();
				$("#agrega2").attr('disabled', true);
				compDocumentacion();
				$("#L04").click();
												 
		}
	}
		$("#agrega2").attr('disabled', true);
			$("#montoDev").val("");
			$("#ep").val("");
			$("#codSIAFF").val("");
		$("#TIPO_CONCEPTO").attr('disabled',true);
	}
	
	var TotalNeto=0;
    var	TotalRetenciones=0;
    
function comprobacionImportes(){
	montosFormatoQuitar();
		queryFormPost("tFACTOBRAImpNetoRead", {async: false });		
		if (  Number($("#TotalNeto").val()) !=  Number($("#mImporteNeto").val())){
		
		$("#TotalNeto").val( Number($("#mImporteNeto").val()) - Number( $("#TotalNeto").val()));
		queryFormPost("tFACTOBRAimpNetoUpdate", {async: false });		

		}
		queryFormPost("tFACTOBRARetencionesRead", {async: false });		
		if ( Number($("#TotalRetenciones").val()) !=  Number($("#mImporteRetencion").val())){
		
			if (Number($("#mObra5").val()) > 0 ){
				$("#campoRetencion").val("mObra5");
			}else  if (Number($("#mISRHonorarios").val()) > 0 ){
				$("#campoRetencion").val("mISRHonorarios");
			}else  if (Number($("#mImporteFlete4").val()) > 0 ){
				$("#campoRetencion").val("mImporteFlete4");
			}else  if (Number($("#mISRArrenda").val()) > 0 ){
				$("#campoRetencion").val("mISRArrenda");
			}else  if (Number($("#mTesofe").val()) > 0 ){
				$("#campoRetencion").val("mTesofe");
			}else  if (Number($("#mRetImpuestoCedular").val()) > 0 ){
				$("#campoRetencion").val("mRetImpuestoCedular");
			}else  if (Number($("#mCNIC").val()) > 0 ){
				$("#campoRetencion").val("mCNIC");
			}else  if (Number($("#mIMDT").val()) > 0 ){
				$("#campoRetencion").val("mIMDT");
			}else  if (Number($("#mImporteFlete23").val()) > 0 ){
				$("#campoRetencion").val("mImporteFlete23");
			}
			
			TotalRetenciones= Number($("#mImporteRetencion").val()) - Number($("#TotalRetenciones").val());
			$("#TotalRetenciones").val(	TotalRetenciones.toFixed(2)  );
			actualizaretencion();
			
		}     
		
	}
	function actualizaretencion(){
			
		elParametro = $("#campoRetencion").val()+ " = "+ $("#campoRetencion").val() +" + " + $("#TotalRetenciones").val()
		+" where  nFolioPAGOOBRA = "+$("#id_caso").val()+ " AND cEvento !='ANTICIPO' AND nDocRenglon=1 " ;						
		$.getJSON("../catalogos/InsertJson.jsp",{Tabla: "ACTUALIZARETENCIONOBRA", Param: elParametro, MaxReg: "", ajax: 'false'}, function(j){});
	}
	
	
	function carga(){
		queryFormPost("tEjercicioRead",{async: false });
		$("#cEjercicio").val( $("#aEjercicioFiscal").val() );
		$("#cRamo").val( "<%=cRamo%>" );
		$("#cUnidadResponsable").val( "<%=cUR%>" );
		$(".divAutorizar").hide();
		$("#divImprime").hide();
		$("#divImprimePoliza").hide();
		$("#divImprimeAnexo").hide();		
		$("#agrega2").attr('disabled', true);
		var con = $("#TO_TIPO_DOCTO").val(); 
		
		
		querySelectPost("CatalogoTipoRfcRead","tipoBenef", {async: false });
		querySelectPost("CatalogoObraDGastoRead","destGasto", {async: false });
		$("#ID_DESTINO_GASTO").val( $("#destGasto").val() );
		$("#idDestinoGasto").val( $("#destGasto").val() );

		querySelectPost("CatalogoObraTConceptoRead","TIPO_CONCEPTO", {async: false }); //Se Descomenta para mostrar TIPO CONCEPTO DE Acuerdo al ID_DESTINO
		querySelectPost("CAT_TIPO_OPERACIONRead", "TIPO_OPERACION",{async: false });
		querySelectPost("ImporteTotalObraRead","cIDContratoObra", {async: false });
		queryFormPost("fRecepcion2Read", {async: false });
		queryFormPost("fRecepcionRead", {async: false });
		queryFormPost("TipoPolizaRead", {async: false });
		
        if ( $("#fRecepcion").val().split("/")[2] != $("#aEjercicioFiscal").val() ) {
           	$("#fRecepcion").val( "31/12/" + $("#aEjercicioFiscal").val() );
           	$("#FECHA_CARGA").val( $("#fRecepcion").val() );
           }
		$("#fRecepcion").attr( "readonly","readonly" );
		
		$(".pasoTres").hide();
		$("#divImprime").hide();
		ponCerosD();
		folio();
		
		BuscaPoliza();
		
		if (<%=id_oper%> ==3 ){
		   $("#divAutorizar").hide();
		   
		   if( muestraDivImprimePoliza ){
		   	$("#divImprime").show();
		   	$("#divImprimePoliza").show();
		    $("#divImprimeAnexo").show();
		   }
		   			 
	  	   $("#Agregar").attr('disabled', true);
		   $("#Agregar1").attr('disabled', true);

		 }	
	}
	
	function montosFormato(){
			//SASV 12/05/2015 Campos para mostrar (formato).
			$("#mImporteBrutoFactura").val(numberFormat(Math.round($("#mImporteBrutoFactura").val()*100)/100));
			$("#mIvaFactura").val(numberFormat(Math.round($("#mIvaFactura").val()*100)/100));
			$("#mImporteNetoFactura").val(numberFormat(Math.round($("#mImporteNetoFactura").val()*100)/100));
			$("#mEstimacion").val(numberFormat(Math.round($("#mEstimacion").val()*100)/100));
			$("#mImporteSancion").val(numberFormat(Math.round($("#mImporteSancion").val()*100)/100));
			$("#mImporteDevolucion").val(numberFormat(Math.round($("#mImporteDevolucion").val()*100)/100));
			$("#subTotal_1").val(numberFormat(Math.round($("#subTotal_1").val()*100)/100));
			$("#mAmortizacionAnticipo").val(numberFormat(Math.round($("#mAmortizacionAnticipo").val()*100)/100));
			$("#subTotal_2").val(numberFormat(Math.round($("#subTotal_2").val()*100)/100));
			$("#mImporteIVA").val(numberFormat(Math.round($("#mImporteIVA").val()*100)/100));
			$("#mImporteRetencion").val(numberFormat(Math.round($("#mImporteRetencion").val()*100)/100));
			$("#mImportePenalizacion").val(numberFormat(Math.round($("#mImportePenalizacion").val()*100)/100));
			$("#mImporteNetoEP").val(numberFormat(Math.round($("#mImporteNetoEP").val()*100)/100));
			$("#mImporteNeto").val(numberFormat(Math.round($("#mImporteNeto").val()*100)/100));	
			$("#mImpEjercer").val(numberFormat(Math.round($("#mImpEjercer").val()*100)/100));
			$("#mAmortizacionAcumulado").val(numberFormat(Math.round($("#mAmortizacionAcumulado").val()*100)/100));
			$("#saldoCed").val(numberFormat(Math.round($("#saldoCed").val()*100)/100));
			$("#mTotal").val(numberFormat(Math.round($("#mTotal").val()*100)/100));
			$("#acumulado").val(numberFormat(Math.round($("#acumulado").val()*100)/100));
			$("#saldoAnticipo").val(numberFormat(Math.round($("#saldoAnticipo").val()*100)/100));
			$("#mImporteSancionAcumulado").val(numberFormat(Math.round($("#mImporteSancionAcumulado").val()*100)/100));
			$("#mImporteDevolucionAcumulado").val(numberFormat(Math.round($("#mImporteDevolucionAcumulado").val()*100)/100));
			$("#nPorcIVAAplicable").val(Math.round($("#nPorcIVAAplicable").val()*100)/100);
	}
	
	function montosFormatoQuitar(){//SASV 12/05/2015 quitar formato para calculos.
			$("#mImporteBrutoFactura").val(quitaFmt($("#mImporteBrutoFactura").val()));
			$("#mIvaFactura").val(quitaFmt($("#mIvaFactura").val()));
			$("#mImporteNetoFactura").val(quitaFmt($("#mImporteNetoFactura").val()));
			$("#mEstimacion").val(quitaFmt($("#mEstimacion").val()));
			$("#mImporteSancion").val(quitaFmt($("#mImporteSancion").val()));
			$("#mImporteDevolucion").val(quitaFmt($("#mImporteDevolucion").val()));
			$("#subTotal_1").val(quitaFmt($("#subTotal_1").val()));
			$("#mAmortizacionAnticipo").val(quitaFmt($("#mAmortizacionAnticipo").val()));
			$("#subTotal_2").val(quitaFmt($("#subTotal_2").val()));
			$("#mImporteIVA").val(quitaFmt($("#mImporteIVA").val()));
			$("#mImporteRetencion").val(quitaFmt($("#mImporteRetencion").val()));
			$("#mImportePenalizacion").val(quitaFmt($("#mImportePenalizacion").val()));
			$("#mImpEjercer").val(quitaFmt($("#mImpEjercer").val()));
			$("#mImporteNetoEP").val(quitaFmt($("#mImporteNetoEP").val()));
			$("#mImporteNeto").val(quitaFmt($("#mImporteNeto").val()));
			$("#mAmortizacionAcumulado").val(quitaFmt($("#mAmortizacionAcumulado").val()));
			$("#saldoCed").val(quitaFmt($("#saldoCed").val()));
			$("#mTotal").val(quitaFmt($("#mTotal").val()));
			$("#acumulado").val(quitaFmt($("#acumulado").val()));
			$("#saldoAnticipo").val(quitaFmt($("#saldoAnticipo").val()));
			$("#mImporteSancionAcumulado").val(quitaFmt($("#mImporteSancionAcumulado").val()));
			$("#mImporteDevolucionAcumulado").val(quitaFmt($("#mImporteDevolucionAcumulado").val()));
			$("#mmontoestimacion").val(quitaFmt($("#mmontoestimacion").val()));
			$("#mmontoestimacionIva").val(quitaFmt($("#mmontoestimacionIva").val()));
			$("#mmontoestimacionMasIva").val(quitaFmt($("#mmontoestimacionMasIva").val()));
			$("#mmontoestimacionAmortizado").val(quitaFmt($("#mmontoestimacionAmortizado").val()));
			$("#mmontoestimacionRetencion").val(quitaFmt($("#mmontoestimacionRetencion").val()));
	}
	
	
	function concepto(){
		querySelectPost("CatalogoObraTMovimendoRead", "TIPO_MOVIMIENTO",{async: false });
		concepto2();
	}
	
	function concepto2(){
 		querySelectPost("CatalogoObraTMovimendo2Read",{async: false });
 			
 		if( esSAIFonden && <%=id_oper%> == 3  ){
			$("#verProyectoFondenDiv").css('display', 'show');
			$("#selproyectoFondenDiv").css('display', 'none');
			
		}
		
		if( esSAIFonden  ){
			$("#verFondenSPAN").show();
			querySelectPost(
				{ 	     queryName: 'catFondenRead', 
					targetObjectId: 'proyectoFonden', 
					         async: false,
					      callback: function(){
					         	$("#dlgSeleccionaFonden").dialog("open");
					         }
				});
			$("#verProyectoFondenDiv").css('display', 'none');
			$("#selproyectoFondenDiv").css('display', 'show');
		}	
	}
	
	function validamAmortizacionAnticipoMasIva(){
		queryFormPost("ExtraemAmortizacionAnticipoAcumulado", {async: false });
		var acumulado=parseFloat((quitaFmt($("#mAmortizacionAnticipoAcumulado").val()))).toFixed(2);
		var anticipo=parseFloat((quitaFmt($("#saldoAnticipo").val()))).toFixed(2);
		var actualAmortizacion=parseFloat((quitaFmt($("#mAmortizacionAnticipo").val()))).toFixed(2);
		actualAmortizacion=(actualAmortizacion*1.16).toFixed(2);
		var restante=(anticipo-acumulado-actualAmortizacion).toFixed(2);
		if((restante>-.01&&restante<0)||(restante>0&&restante<.01)){
			var ajuste=(anticipo-acumulado).toFixed(2);
			$("#mAmortizacionAnticipoMasIva").val(ajuste);
			$("#mAmortizacionAnticipo").val((ajuste/1.16).toFixed(2));
			Swal.fire("OK","Se ajustó la Amortizacion","success");
		}else{
			$("#mAmortizacionAnticipoMasIva").val(( Number( $("#mAmortizacionAnticipo").val()) + ( Number( $("#mAmortizacionAnticipo").val()) * $("input[id='nPorcIVAAplicable']").val()/100    )).toFixed(2)  ) ;
		}
	}

	var mImporteFlete23=0;
	var mImporteFlete232=0;	
	var ayuda1=0;
	var ayuda2=0;
	var saldoo=0;
	var acumula=0;
	var comprometido=0;
	var DCD_IVADES=0;
	var DCD_MIL2=0;
	var DCD_MIL5=0;
	var DCD_RETENCION=0;
	var DCD_IVADESres=0;
	var DCD_MIL2res=0;
	var DCD_MIL5res=0;
	var DCD_RETENCIONres=0;
	var mAmortizacionAnticipo=0;
	var mAmortizacionAnticipoRes=0;
	var mAmortizacionAcumulado=0;
	var mAmortizacionAcumuladoRes=0;
	var mImporteSancionAcumulado=0;
	var mImporteDevolucionAcumulado=0;
	var contador=0;
	var mIMDT=0;
	var mCNIC=0;
	var CamInst=0;
	var mObra5=0;
	var mISRHonorarios=0;
	var mISRArrenda=0;
	var mPenalizacion=0;
	var mRetImpuestoCedular=0;
	var mImporteNetoEP=0;
	var mImporteNetoEPRes=0;
	var mImporteIVAEP=0;
	var montoEstima=0;//SASV Sobre este monto se haran los calculos, tanto como de retenciones como de amortizacion
	var convenioModificatorio;//SASV si pertecene o no el pago a un convenio Modificatorio.
	var conAmortizacion;//SASV Amortiza o no el pago.
	var ultimoamortiza=false;// SASV define si es el ultimo Pago con Amortizacion
	var recalcula=0;//SASV Evita varios mensajes de Superado Compromiso
	
	function calculaIVA(){
		
		montosFormatoQuitar();
		$("#nPorcIVAAplicable").val($("#porcIVA").val());
		
	     //SASV se define el monto de la estimacion segun sea el caso, con el cual parten todos los calculos.	
		if(parseFloat($("#mImporteNetoFactura").val())==0)
			ultimoamortiza=false;
		if(ultimoamortiza)//SASV si es el ultimo pago con amortizacion el monto de la estimacion no cambia(se calcula al definirse verdadero)
			montoEstima=montoEstima;
		
		montoEstima=parseFloat($("#mmontoestimacion").val());
		
		$("#mEstimacion").val(montoEstima.toFixed(2));
		$("#mAmortizacionAcumulado").val(mAmortizacionAcumuladoRes);
		$("#mAmortizacionAnticipo").val(mAmortizacionAnticipoRes);//ESTE ES 
		$("#mImporteDevolucionAcumulado").val(mImporteDevolucionAcumulado);
		$("#mImporteSancionAcumulado").val(mImporteSancionAcumulado);
		$("#acumulado").val(comprometido);
		
		suma = montoEstima;
		suma -= Number($("input[id='mImporteSancion']").val());
		suma += Number($("input[id='mImporteDevolucion']").val());
		
		var importeDevolucion = 0;
		importeDevolucion = Number( $("#mImporteDevolucion").val() ) + Number( $("#mImporteDevolucionAcumulado").val() );
		importeDevolucion = importeDevolucion.toFixed(2);
		
		$("#mImporteDevolucionAcumulado").val( importeDevolucion );
		
		var importeSancion = 0;
		importeSancion = Number( $("#mImporteSancion").val() ) + Number( $("#mImporteSancionAcumulado").val() );
		importeSancion = importeSancion.toFixed(2);
		
		$("#mImporteSancionAcumulado").val( importeSancion );
		
		if ( (Number($("#mImporteDevolucionAcumulado").val()) > Number($("#mImporteSancionAcumulado").val()) ) &&  Number($("#mImporteDevolucionAcumulado").val())!=0 ){
			Swal.fire("Revise","Las Devoluciones son mayores a las Sanciones ","info");
			
			$("#mImporteDevolucion").val(0);
			return;
		}
		$("#subTotal_1").val(suma);
		
		queryFormPost("obtieneAmortizacionEstimacionObraRead", {async: false });
		validamAmortizacionAnticipoMasIva();
		$("#DCD_AMORT").val( Math.round($("#mAmortizacionAnticipoMasIva").val()*100)/100 );
		$("#nPorcAmortizacion").val($("#porcAnticipo").val());//SASV se vuelve a definir el porcentaje original.

		if ( (parseFloat($("#mAmortizacionAnticipoMasIva").val()) + parseFloat( $("#mAmortizacionAcumulado").val())) <=  $("#saldoAnticipo").val() ){
			$("#mAmortizacionAcumulado").val( Math.round((parseFloat($("#mAmortizacionAnticipo").val()*1.16)  + parseFloat( $("#mAmortizacionAcumulado").val()))*100)/100);		
		
		}else if(parseFloat($("#mImporteNetoFactura").val())!=0) {	
			ultimoamortiza=true; // se define que el monto por aportizar es menor por lo que se calcula
			
			queryFormPost("obtieneAmortizacionEstimacionObraRead", {async: false });
			
			montoEstima=parseFloat($("#mImporteBrutoFactura").val());
			$("#mAmortizacionAcumulado").val(Math.round(( parseFloat($("#saldoAnticipo").val() ) )*100)/100);

		}
		
		suma -= $("#mAmortizacionAnticipo").val();
		$("#subTotal_2").val(Math.round(suma*100)/100);
		
		res_iva = Math.round((parseFloat($("input[id='nPorcIVAAplicable']").val()/100 )  *(parseFloat($("input[id='subTotal_2']").val())))*100)/100;

		$("#mImporteIVA").val(Math.round(Number(res_iva) *100 )/100);

		$("#mImporteMasIva").val(Math.round((Number ($("#mImporteIVA").val()) + Number($("#subTotal_2").val()))*100)/100);
		var subtotal2=$("#subTotal_2").val();
		var importeIVa=$("#mImporteIVA").val();
		var importemasiva=$("#mImporteMasIva").val();
		suma += Number($("input[id='mImporteIVA']").val());
		DCD_IVADES = 0 ;
		if ( mImporteFlete23 == 1){
			DCD_IVADES = (Number($("#mImporteIVA").val())*2)/3;
		}
		if ($("#destGasto").val()=="ANPO" ){//SASV si es anticipo no lleva retenciones

			$("#DCD_MIL2").val( 0);
			$("#DCD_MIL5").val( 0);		
			$("#DCD_RETENCION").val( 0);		
			$("#DCD_IVADES").val( DCD_IVADES.toFixed(2) );		
		}else{

			$("#DCD_MIL2").val(DCD_MIL2res);
			$("#DCD_MIL5").val(DCD_MIL5res);
			$("#sumareten").val(DCD_RETENCIONres);
			
			DCD_MIL2=parseFloat(Math.round(($("#DCD_MIL2").val() * parseFloat($("#subTotal_1").val()))*100)/100 );
			DCD_MIL5=parseFloat(Math.round(($("#DCD_MIL5").val() * parseFloat(montoEstima))*100)/100 );
			DCD_RETENCION=parseFloat( $("#sumareten").val() * $("#subTotal_1").val() );
			
			$("#DCD_MIL2").val( Math.round(DCD_MIL2 *100)/100 );
			$("#DCD_MIL5").val( Math.round(DCD_MIL5 *100)/100 );		
			$("#DCD_RETENCION").val( Math.round(DCD_RETENCION *100)/100 );		
			$("#DCD_IVADES").val( Math.round(DCD_IVADES *100)/100 );
		}
		
		$("#mImpEjercer").val(  numberFormat( $("#mImpEjercer").val() ) );
		
		if ($("#destGasto").val()=="ANPO" ){
			ayuda1=0.0;
		}else{
			ayuda1=Number($("#sumareten").val()) * Number($("input[id='subTotal_1']").val());
		}
		
		ayuda2=parseFloat (ayuda1) + parseFloat ($("#DCD_MIL2").val()) +parseFloat ( $("#DCD_MIL5").val()) + parseFloat ($("#DCD_IVADES").val());
		$("#mImporteRetencion").val(Math.round(Number(ayuda2) *100 )/100);
		suma -= Number($("input[id='mImporteRetencion']").val());
		suma -= Number($("input[id='mImportePenalizacion']").val());		
		$("#mImporteNeto").val(Math.round(Number(suma) *100 )/100);
		
		acumula = Number ( $("#acumulado").val() ) +  Number( $("#mImpEjercer").val() );
		
		if (  Number ($("#saldoCed").val()) >=  Number (acumula )  ){
			$("#acumulado").val(acumula);
		}else if( Number($("#mImporteBrutoFactura").val()>0)){//SASV para evitar mensajes antes de cargar factura
			if(recalcula>1){//SASV validacion para evitar mensajes duplicados o erroneos, ya que puede no haberse superado el compromiso al hacer el calculo de nuevo.
				$("#mImporteBruto").val(0);
				Swal.fire("Revise","Ha superado el monto del Compromiso","info");
				recalcula=0;
			}else 
				recalcula++;
		}
		
		if ( $("#PagoAMF").val() != "" && $("#mImporteNeto").val() != "0"){
			if ( Number( $("#mImporteNeto").val() ) != Number( $("#PagoAMF").val() ) ){
				$("#mImporteBruto").val(0);
				alert("Importe Neto es diferente al Importe AMF");
			
			}
		}
		
		montosFormato();//SASV 12/05/2015 Da Formato para Mostrar (Separador de miles)
		
		var mPenaMasIVA = 0.0;
		mPenaMasIVA = importeSancion * 1.16; 
		mImporteBrutoE = Number( quitaFmt( $("#mImporteBrutoFactura").val() ) );
		mImporteIVAE = Number( quitaFmt( $("#mIvaFactura").val() ) );
		mImporteAmortizacion = Number( quitaFmt( $("#mAmortizacionAnticipo").val() ) );
		
		$("#mMontoEje").val( Number( mImporteBrutoE + mImporteIVAE - mImporteAmortizacion ).toFixed(2)  );
		
		//SASV 24/08/2015 Mostrar Monto a ejercer en pestaña Devengado
		$("#mImpEjercer").val(  Number( (mImporteBrutoE - mImporteAmortizacion) + mImporteIVAE  ).toFixed(2)  );

	}
	
	function calculaIVAConsulta(){
		suma = Number($("input[id='mImporteBrutoFactura']").val());//SASV se generan los calculos desde el monto de la factura
		suma -= Number($("input[id='mImporteSancion']").val());
		suma += Number($("input[id='mImporteDevolucion']").val());		
		$("#subTotal_1").val(Math.round(Number(suma) *100 )/100);

		$("#mAmortizacionAnticipo").val(Math.round(( Number($("#mAmortizacionAnticipo").val())  /  (1 + Number ($("#nPorcIVAAplicable").val()/100 ) )) *100 )/100);	
		suma -= $("#mAmortizacionAnticipo").val();	
		
		$("#subTotal_2").val(Math.round(Number(suma) *100 )/100);
		
		$("input[id='mImporteIVA']").val( $("#subTotal_2").val() * Number($("input[id='nPorcIVAAplicable']").val()/100)    );
		suma += Number($("input[id='mImporteIVA']").val());		
	
		$("#mImpEjercer").val( Number(suma).toFixed(2) );	
			
		suma -= Number($("input[id='mImporteRetencion']").val());
		suma -= Number($("input[id='mImportePenalizacion']").val());
		$("#mImporteNeto").val(Math.round(Number(suma) *100 )/100);
							
	}
		
	function AnticipoTotal(){
			if ( parseFloat ($("#saldoAnticipo").val()) == parseFloat($("#mAmortizacionAcumulado").val())){
				$("#nPorcAmortizacion").val(0);
				document.getElementById('amortizar').style.display = 'none';
				document.getElementById('amortizar').checked=false;
				calculaIVA();
			}
	}
	function Amortizacion(){
		 if ( Number ($("#nPorcAmortizacion").val()) < Number( $("#nPorcAmortizacion2").val() )){
			Swal.fire("El porcentaje minimo es: ", $("#nPorcAmortizacion2").val(),"info" );
			$("#nPorcAmortizacion").val($("#nPorcAmortizacion2").val());
	 	}else{
			calculaIVA();
		}
	}	
	var cantidadsiniva=0;
	var cantidadrespaldo=0;
	var cantidadsinIvaEP=0;
	var cantidadsinIvaEP23=0;
	var epsinIva=0;
	
	function caculaIva( cantidad2 ){
		$("#mImporteFlete23").val( 0 );
		$("#mIMDT").val( 0 );
		$("#mCNIC").val( 0 );
		$("#mObra5").val( 0 );
		$("#mISRHonorarios").val( 0 );
		$("#mImporteFlete4").val( 0 );
		$("#mISRArrenda").val( 0 );
		$("#mAmortizacionAnticipoMasIvaEP").val( 0 );
		$("#mTesofe").val( 0 );
		$("#mRetImpuestoCedular").val( 0 );
		cantidadrespaldo = Number( cantidad2 );	
   		cantidadrespaldo = Number( cantidadrespaldo.toFixed(2) );

		epsinIva = cantidad2 / ( 1 + $("input[id='nPorcIVAAplicable']").val()/100);
		epsinIva = epsinIva.toFixed(2);
		
		$("#mIVA").val(cantidadrespaldo - Number( epsinIva ) );
		if ( mImporteFlete23 == 1){
			mImporteFlete232 = ($("#mIVA").val()*2)/3;
			mImporteFlete232 = mImporteFlete232.toFixed(2);
			
			$("#mImporteFlete23").val( mImporteFlete232 );
		}
		
		mImporteNetoEP = Number($("#mImporteFlete23").val());
		mImporteNetoEPRes = parseFloat(epsinIva) - parseFloat( mImporteNetoEP );	
		mImporteNetoEPRes = mImporteNetoEPRes.toFixed(2);
		
		if($("#destGasto").val()=="ANPO"){
			//SASV mImporteNetoEP en anticipo debe ser igual al importe con iva no lleva retenciones
			$("#mImporteNetoEP").val(cantidad2);
		}else
			$("#mImporteNetoEP").val(  mImporteNetoEPRes );

	}
	
	function calculaRetencionesEP ( cantidad ){
	
		cantidadsinIvaEP = cantidad / ( 1 + $("input[id='nPorcIVAAplicable']").val()/100);
		cantidadsinIvaEP = Number( cantidadsinIvaEP.toFixed(2) );
		mCNIC=0;
		mIMDT=0;
		mObra5=0;
		
		$("#mObra5").val($("#mObra52").val());
		$("#mISRHonorarios").val($("#mISRHonorarios2").val( ) );
		$("#mImporteFlete4").val($("#mImporteFlete42").val( ) );
		$("#mISRArrenda").val($("#mISRArrenda2").val());
		$("#mTesofe").val( $("#mTesofe2").val() );
		$("#mRetImpuestoCedular").val($("#mRetImpuestoCedular2").val( ) );

		cantidadrespaldo = Number( cantidad );	
   		cantidadrespaldo = Number( cantidadrespaldo.toFixed(2) );
		cantidad = cantidadsinIvaEP;
		$("#mIVA").val( Number( cantidadrespaldo - cantidadsinIvaEP ) );
		
		if ( mImporteFlete23 == 1){
			mImporteFlete232 = ($("#mIVA").val()*2)/3;
			mImporteFlete232 = mImporteFlete232.toFixed(2);
			
			$("#mImporteFlete23").val( mImporteFlete232 );
		}	
		
		if ( Number ( $("#CamInst").val() ) == 0 ){
			mCNIC = parseFloat( cantidad ) * parseFloat( CamInst );
			mCNIC = Number( mCNIC.toFixed(2) ) ;
			mCNIC = mCNIC / ( 1 - ($("#nPorcAmortizacion").val()/100));
			mCNIC = mCNIC.toFixed(2);
			$("#mCNIC").val( mCNIC );
		
		}else if ( Number ( $("#CamInst").val() ) == 1 ){
			mIMDT = parseFloat( cantidad ) * parseFloat( CamInst );
			mIMDT = Number( mIMDT.toFixed(2) );
			mIMDT = mIMDT / ( 1 - ($("#nPorcAmortizacion").val()/100));
			mIMDT = mIMDT.toFixed(2);
			$("#mIMDT").val( mIMDT );
		}else{
			$("#mIMDT").val(0);
			$("#mCNIC").val(0);
		}

		mObra5 = parseFloat ( cantidad ) * parseFloat( $("#mObra5").val() );
		mObra5 = Number( mObra5.toFixed(2) );
		mObra5 = mObra5 / ( 1 - ($("#nPorcAmortizacion").val() / 100));
		mObra5 = mObra5.toFixed(2);
		$("#mObra5").val( mObra5 );

		mISRHonorarios = parseFloat( cantidad ) * parseFloat( $("#mISRHonorarios").val() );
		mISRHonorarios = Number( mISRHonorarios.toFixed(2) );
		mISRHonorarios = mISRHonorarios / ( 1 - ($("#nPorcAmortizacion").val()/100));
		mISRHonorarios = mISRHonorarios.toFixed(2);
		$("#mISRHonorarios").val( mISRHonorarios );
		
		mImporteFlete4 = parseFloat( cantidad ) * parseFloat( $("#mImporteFlete4").val() );
		mImporteFlete4 = Number( mImporteFlete4.toFixed(2) );
		mImporteFlete4 = mImporteFlete4 / ( 1 - ($("#nPorcAmortizacion").val()/100) );
		mImporteFlete4 = mImporteFlete4.toFixed(2);
		$("#mImporteFlete4").val( mImporteFlete4 );
		
		mISRArrenda = parseFloat( cantidad ) * parseFloat( $("#mISRArrenda").val() );
		mISRArrenda = Number( mISRArrenda.toFixed(2) );
		mISRArrenda = mISRArrenda / ( 1 - ($("#nPorcAmortizacion").val() / 100) );
		mISRArrenda = mISRArrenda.toFixed(2);
		$("#mISRArrenda").val( mISRArrenda );

		var mAmortizaEP = ( Number( $("#mAmortizacionAnticipoMasIva").val() ) / Number( $("#mImpEjercer").val() ) ) * cantidadrespaldo;
		mAmortizaEP = mAmortizaEP.toFixed(2);
		$("#mAmortizacionAnticipoMasIvaEP").val( mAmortizaEP );

		var laRetencion = 0;
		var importeSancion = 0;
		importeSancion = Number( $("#mImporteSancion").val() );
		importeSancion = importeSancion.toFixed(2);
    	mPenaMasIVA = importeSancion * 1.16;
    
		var mPenaliza = mPenaMasIVA / Number( $("#mImpEjercer").val() ) * cantidadrespaldo;
		mPenaliza = mPenaliza.toFixed(2);
		$("#mTesofe").val( mPenaliza );
		
		var importeSancion = 0;
		importeSancion = Number( $("#mImporteSancion").val() );
		importeSancion = importeSancion.toFixed(2);
    	mPenaMasIVA = importeSancion * 1.16;
    
		$("#mTesofe").val( mPenaMasIVA );
		mRetImpuestoCedular = parseFloat( cantidad ) * parseFloat( $("#mRetImpuestoCedular").val() );
		mRetImpuestoCedular = Number( mRetImpuestoCedular.toFixed(2) );
		mRetImpuestoCedular = mRetImpuestoCedular / ( 1 - ($("#nPorcAmortizacion").val()/100) );
		mRetImpuestoCedular = mRetImpuestoCedular.toFixed(2);
		$("#mRetImpuestoCedular").val( mRetImpuestoCedular );

		var importeSancion = 0;
		importeSancion = Number( $("#mImporteSancion").val() );
		importeSancion = importeSancion.toFixed(2);
		mPenaMasIVA = importeSancion;
		
		mImporteNetoEP = Number( $("#mTesofe").val() ) + Number($("#m23IVA").val()) + Number($("#mCNIC").val()) + Number($("#mIMDT").val()) + Number($("#mObra5").val()) + Number ($("#mISRHonorarios").val())+
		                 Number($("#mImporteFlete4").val()) + Number( $("#mISRArrenda").val() ) + Number( $("#mRetImpuestoCedular").val()) + Number($("#mImporteFlete23").val());
		mImporteNetoEPRes = parseFloat(cantidadrespaldo) - parseFloat(mImporteNetoEP);
		mImporteNetoEPRes = mImporteNetoEPRes.toFixed(2);
		$("#mImporteNetoEP").val(  mImporteNetoEPRes );
	
	}

function ponCerosD(){
	
	$("#DCD_ISR_D").val( "0" );
	$("#DCD_IVA_D").val( "0" );
	$("#DCD_MIL5_D").val( "0" );
	$("#DCD_MIL2_D").val( "0" );
	$("#DCD_CONTRIBUCION_D").val( "0" );
	$("#DCD_OTRAS_RET_D").val( "0" );
	$("#DCD_PENALIZACION_D").val( "0" );
	$("#DCD_SANCION_D").val( "0" );
	$("#DCD_DEVOL_D").val( "0" );
	$("#DCD_AMORT_D").val( "0" );
	$("#DCD_RETENCION_D").val( "0" );
	
}
	
function actualSaldosDet( elTipo ){
    var laRetencion = 0;
	
    laRetencion = Number($("#m23IVA").val()) + Number($("#mCNIC").val())+Number($("#mIMDT").val())  + Number($("#mObra5").val()) + Number ($("#mISRHonorarios").val())+
				  Number($("#mImporteFlete4").val())+ Number( $("#mISRArrenda").val() )+Number( $("#mRetImpuestoCedular").val())+
				  Number($("#mImporteFlete23").val()) + Number($("#mTesofe").val());
    laRetencion = laRetencion.toFixed(2);
 
	$("#DCD_ISR_D").val(parseFloat($("#DCD_ISR_D").val()) + elTipo * parseFloat($("#mISRHonorarios").val()) + elTipo * Number( $("#mISRArrenda").val() ));
	$("#DCD_IVA_D").val(parseFloat($("#DCD_IVA_D").val()) + elTipo * parseFloat($("#m23IVA").val()));
	$("#DCD_MIL5_D").val(parseFloat($("#DCD_MIL5_D").val()) + elTipo * parseFloat($("#mObra5").val()));
	$("#DCD_MIL2_D").val(parseFloat($("#DCD_MIL2_D").val()) + elTipo * ( Number( $("#mCNIC").val() ) + Number( $("#mIMDT").val() ) ) );
	$("#DCD_CONTRIBUCION_D").val(parseFloat($("#DCD_CONTRIBUCION_D").val()) + elTipo * parseFloat($("#mImporteFlete4").val()));
	$("#DCD_OTRAS_RET_D").val(parseFloat($("#DCD_OTRAS_RET_D").val()) + elTipo * parseFloat($("#mRetImpuestoCedular").val()));
	$("#DCD_PENALIZACION_D").val( parseFloat($("#DCD_PENALIZACION_D").val()) + elTipo * parseFloat( $("#mTesofe").val() ) );
	$("#DCD_SANCION_D").val(parseFloat($("#DCD_SANCION_D").val()) + elTipo * parseFloat($("#mSancion").val()));
	$("#DCD_DEVOL_D").val(parseFloat($("#DCD_DEVOL_D").val()) + elTipo * parseFloat($("#mDevolucion").val()));
	$("#DCD_AMORT_D").val(parseFloat($("#DCD_AMORT_D").val()) + elTipo * parseFloat($("#mAmortizacionAnticipoMasIvaEP").val()));
	$("#DCD_RETENCION_D").val(parseFloat($("#DCD_RETENCION_D").val()) + elTipo * parseFloat(laRetencion));

}

function AjustaRetencionesD(){
		
    var laRetencion = 0;
	var importeSancion = 0;
	importeSancion = Number( $("#mImporteSancion").val() );
	importeSancion = importeSancion.toFixed(2);
    mPenaMasIVA = importeSancion * 1.16;
    
	$("#mDevolucion").val( Number( $("#mDevolucion").val() ) + ( Number( $("#DCD_DEVOL").val() ) - Number( $("#DCD_DEVOL_D").val() ) ) );
	$("#mTesofe").val( mPenaMasIVA );
	$("#mObra5").val( Number( $("#mObra5").val() ) + ( Number( $("#DCD_MIL5").val() ) - Number($("#DCD_MIL5_D").val()) ) );
	$("#mImporteFlete4").val( Number( $("#mImporteFlete4").val() ) + ( Number( $("#DCD_CONTRIBUCION").val() ) - Number($("#DCD_CONTRIBUCION_D").val()) ) );
	
	if ( Number( $("#mISRHonorarios").val() ) > 0)
		$("#mISRHonorarios").val( Number( $("#mISRHonorarios").val() ) + ( Number( $("#DCD_ISR").val() ) - Number($("#DCD_ISR_D").val()) ) );	
	else
		$("#mISRArrenda").val( Number( $("#mISRArrenda").val() ) + ( Number( $("#DCD_ISR").val() ) - Number($("#DCD_ISR_D").val()) ) );
	
	$("#mRetImpuestoCedular").val( Number( $("#mRetImpuestoCedular").val() ) + ( Number( $("#DCD_OTRAS_RET").val() ) - Number($("#DCD_OTRAS_RET_D").val()) ) );
	$("#mSancion").val( Number( $("#mSancion").val() ) + ( Number( $("#DCD_SANCION").val() ) - Number($("#DCD_SANCION_D").val()) ) );
	$("#mAmortizacionAnticipoMasIvaEP").val( Number( $("#mAmortizacionAnticipoMasIvaEP").val() ) + ( Number( $("#DCD_AMORT").val() ) - Number($("#DCD_AMORT_D").val()) ) );

    laRetencion = Number($("#m23IVA").val()) + Number($("#mCNIC").val())+Number($("#mIMDT").val())  + Number($("#mObra5").val()) + Number ($("#mISRHonorarios").val())+
		Number($("#mImporteFlete4").val())+ Number( $("#mISRArrenda").val() )+Number( $("#mRetImpuestoCedular").val())+
		Number($("#mImporteFlete23").val()) + Number( $("#mTesofe").val() );
    laRetencion = laRetencion.toFixed(2);
 
	$("#mImporteNetoEP").val( Number( $("#DCD_IMP_BRUTO").val() ) - Number( laRetencion ) );
	
}

/*Busca la EP que se elimino del DT y la elimina de las relaciones*/
function eliminaEPIngresos( epBuscar){
		
		var indice = -1;
		
		for( i = 0; i < relaciones.length; i++){
			if( relaciones[i] == epBuscar ){
				indice = i;
				break;
			}
		}
		
		if(indice >= 0 )
			relaciones[i].splice(indice,1);
		
		muestraJson();
	}
	
</script>
<script type="text/javascript">
	function CalculaRetencionesG(){
		var szWhere = "";
		szWhere = " cidcontrato ='"+ $("#cIDContratoObra").val()+"' AND cIdEntidadContable = '" +  $("#cCentroContable").val() + "'";
		var este=0;
		var elMonto = "100";
		var retencion=0;
		var szTabla = "CALC_RETENCIONES_FACT_CONT_OBRA";
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
			var options = '';
			var porretencion=0;
			$("#DCD_ISR").val( 0 );
			for (var i = 0; i < j.length; i++) {
				if ($("#lHaySaldoAnticipo").val() == 0  || Number( $("#mAmortizado").val() ) > 0){				
					retencion = parseFloat(j[i].Col2)* $("#subTotal_1").val();
					retencion.toFixed( 2 );
					if (j[i].Col0 == 4 ){
						retencion = Number($("#DCD_ISR").val()) + retencion ;
						retencion.toFixed( 2 );
						$("#DCD_ISR").val( retencion ); 						
					}else if (j[i].Col0 == 5 ){
						retencion.toFixed( 2 );
					}else if (j[i].Col0 == 6 ){						
						retencion = Number($("#DCD_ISR").val()) + retencion ;
						retencion.toFixed( 2 );
						$("#DCD_ISR").val( retencion ); 						
					}else if (j[i].Col0 == 9 ){
						retencion.toFixed( 2 );
						$("#DCD_OTRAS_RET").val( retencion ); 						
					}	
				}
			}
		});	
	}
	

	function noPagar(){
		$("#cIDContratoObra").val( "" );
		$("#cIdRFC").val( "" );
		$("#cobjetocontrato").val( "" );
		$("#mImporteBruto").val( "" );
		$("#nPorcIVAAplicable").val( "" );
		$("#mTotal").val( "" );
		$("#mImporteAnticipoIVA").val( "" );
		$("#Descrip_Concepto").val( "" );
		return;
	}
	
	function validaRecibosPendientes(){
		var recibosPendientes = -1;
		queryFormPost({
			queryName:"pagoObraRecibosPendientes",
			async:false,
			callback:function(){
				recibosPendientes = parseInt( $( "#pagosSinRecibo" ).val() == "" ? "0": $( "#pagosSinRecibo" ).val(),10) ;
			}
		});
		return recibosPendientes;
	}
	function FiltroRetenciones(){
		var recibosPendientes  = validaRecibosPendientes();
		/*
		if( recibosPendientes  > 0 ){
			Swal.fire("Adjunte recibos!","No es posible realizar el pago ya que cuenta con " + recibosPendientes + " recibos pendientes de adjuntar.","info");
			noPagar();
			return;
		}
		*/	
		//sasv 27/10/2015
		queryFormPost("tFACTOBRAIVA", {async: false });
	
		if ( $("#cSuspensionPago").val() == 'S'){
			Swal.fire("Atención!","Contrato se Encuentra en Suspensión de Pagos","info");
			noPagar();
			return;
		}
		
		// reinicia valores
		$("#mImporteFlete23").val( 0 );
		$("#mIMDT").val( 0 );
		$("#mCNIC").val( 0 );
		$("#mObra5").val( 0 );
		$("#mISRHonorarios").val( 0 );
		$("#mImporteFlete4").val( 0 );
		$("#mISRArrenda").val( 0 );
		$("#mAmortizacionAnticipoMasIvaEP").val( 0 );
		$("#mTesofe").val( 0 );
		$("#mRetImpuestoCedular").val( 0 );
		// termina reinicia valores
		
		/* CONAFOR no usa AMF
		querySelectPost("PagosAMFRead", "NumPagoAMF", {async: false });
		*/
		$("#cIdContrato").val( $("#cIDContratoObra").val() );
		var szWhere = "";
		szWhere = " cidcontrato ='"+ $("#cIDContratoObra").val()+"' AND cIdEntidadContable = '" +  $("#cCentroContable").val() + "'";
		var elMonto = "100";
		var este = 0;
		var retencion = 0;
		elMonto = $("#mTotal").val();
		var szTabla = "CALC_RETENCIONES_FACT_CONT_OBRA";
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
			var options = '';
			var porretencion = 0;
			for (var i = 0; i < j.length; i++) {
				fnClickAddRowB(j[i].Col0, j[i].Col1, j[i].Col2);
				porretencion = este * elMonto;
				if (j[i].Col0 == 0 ){
					mImporteFlete23=1;
				}else {
					if (j[i].Col0 == 2 ){
						$("#DCD_MIL2").val( parseFloat (j[i].Col2));
						CamInst=parseFloat (j[i].Col2);
						CamInst2=parseFloat (j[i].Col2);
						DCD_MIL2res=parseFloat (j[i].Col2);
			
					}else if (j[i].Col0 == 3 ){
						$("#DCD_MIL5").val( parseFloat (j[i].Col2));
						$("#mObra5").val( parseFloat (j[i].Col2) );
						$("#mObra52").val( parseFloat (j[i].Col2) );
						
						DCD_MIL5res	= parseFloat (j[i].Col2	);	
			
					}else{
						retencion = parseFloat( retencion ) + parseFloat (j[i].Col2);
						if (j[i].Col0 == 4 ){
							$("#mISRHonorarios").val( parseFloat (j[i].Col2));
							$("#mISRHonorarios2").val( parseFloat (j[i].Col2));						
						}else if (j[i].Col0 == 5 ){
							$("#mImporteFlete4").val( parseFloat (j[i].Col2));
							$("#mImporteFlete42").val( parseFloat (j[i].Col2));						
						}else if (j[i].Col0 == 6 ){						
								$("#mISRArrenda").val( parseFloat (j[i].Col2));		
								$("#mISRArrenda2").val( parseFloat (j[i].Col2));						
						}else if (j[i].Col0 == 7 ){						
							$("#mTesofe").val( parseFloat (j[i].Col2) );	
							$("#mTesofe2").val( parseFloat (j[i].Col2) );						
						}else if (j[i].Col0 == 9 ){						
							$("#mRetImpuestoCedular").val( parseFloat (j[i].Col2));
							$("#mRetImpuestoCedular2").val( parseFloat (j[i].Col2));
						}					
					}
				}

				$("#sumareten").val(retencion);
				DCD_RETENCIONres=retencion;
			}
			
			if (<%=id_oper%> == 1 ){
				cargaConvenios();//SASV CM
				cargaCtaBancariaRFC();
				Anticipo();
				querySelectPost("ExtraeInfoEstimaciones", "cNoEstimacion", {async : false});
				
				if ( $("#lHaySaldoAnticipo").val() == 1 && Number( $("#mAmortizado").val() ) > 0 ){
					$("#mAmortizacionAcumulado").val(  $("#mAmortizado").val() );
				}else {
					queryFormPost("tFACTOBRASEGUNDOPAGORead", {async: false });
				}
				
				$("#nPorcAmortizacion2").val( $("#nPorcAmortizacion").val() );
				mAmortizacionAcumuladoRes   = $("#mAmortizacionAcumulado").val();
				mImporteSancionAcumulado    = $("#mImporteSancionAcumulado").val();
				mImporteDevolucionAcumulado = $("#mImporteDevolucionAcumulado").val();
				comprometido = $("#acumulado").val();
				
				AnticipoTotal();
				amortiza();
			}else{
			 	desactivaCampos();
				calculaIVAConsulta();
			}
		});	

	}
	function desactivaCampos(){
		querySelectPost("readCtaBancxPago", "ctaBancaria",{async: false });
	 	$("#cNoEstimacion").attr('disabled', true);
	 	$("#ctaBancaria").attr('disabled', true);
	 	$("#cNoConvenio2").attr('disabled', true);
	 	$("#destGasto").attr('disabled', true);
	 	$("#fecha_Pago").attr('disabled', true);
	 	$("#desde").attr('disabled', true);
		$("#hasta").attr('disabled', true);
		$("#TIPO_OPERACION").attr('disabled', true);
	}
	function cargaCtaBancariaRFC(){
		$("#campoRFC").val($.trim($("#cIdRFC").val()));

		querySelectPost("cargaCtaBancariasRFC", "ctaBancaria",{async: false });
	}
	
	function cargaConvenios(){//SASV CM se leen convenios y se cargan en el select
		querySelectPost("leeConvenios","cNoConvenio2",{async: false });
		var listaSelect=document.forms['frmcontratoDeObra'].elements['cNoConvenio2'];
		if(listaSelect.options.length==0){
			convenioModificatorio=false;
			document.getElementById('cNoConvenio2').style.visibility = "hidden";
			//document.getElementById('CM').innerHTML = ' ';
			$("#CM").val(' ');
		}else{
			$("#CM").val('Convenio Modificatorio');
			//document.getElementById('CM').innerHTML = 'Convenio Modificatorio';
			convenioModificatorio=true;//el contrato cuenta con CM
			listaSelect.style.visibility='visible';
			var elementos=new Array;
			for(i=0;i<listaSelect.options.length;i++)//se extraen los datos de la lista 
				elementos[i]=listaSelect.options[i].text;			
			listaSelect.options[0]=new Option(" ","NA");//se deja la primera opcion vacia por si el pago se hace sin formar parte de algun convenio
			for(i=0;i<elementos.length;i++)//se agregan los convenios a la lista 
				listaSelect.options[i+1]=new Option(elementos[i],elementos[i]);
			}
	}
	//SASV Funcion para Amortizar o no el pago
	function amortiza(){
		conAmortizacion=document.getElementById("amortizar").checked;
		calculaIVA();
	}
	
	var ivaanticipo=0;
	function Anticipo(){
		queryFormPost("tFactObraAnticipoRead", {async: false });
		$("#porcAnticipo").val($("#nPorcAmortizacion").val());//SASV se guarda el % de anticipo original
		queryFormPost("tFACTOBRASaldocompromisoRead", {async: false });
		
		if ($("#lHaySaldoAnticipo").val() == "1" && Number( $("#mAmortizado").val() ) == 0){
			$("#mImporteBruto").attr("readonly",true);
			$("#nPorcAmortizacion").val(0);
			document.getElementById('amortizar').style.display = 'none';
		}else{
			$("#amortizar").css('visibility', 'visible');
			$("#amortizar").attr("checked", "checked" );
			
		}
		amortiza();
	}
	
			
	
	function elRetardo(){
	$("#cIDContratoObra").attr('disabled', true);
		$("#nombre").val($("#cobjetocontrato").val());	
		$(".paso01").attr('disabled', false);
		elParametro = "'"+$("#caNoContrarrecibo").val()+"', '"+$("#TIPO_OPERACION").val()+"', '0', '0', '"+$("#DCD_IMP_BRUTO").val()+"', '"+$("#DCD_SANCION").val()+"', '"+$("#DCD_DEVOL").val()+"', '"+$("#DCD_AMORT").val()+"', '"+$("#DCD_IVA").val()+"','"+$("#DCD_RETENCION").val()+"', '"+$("#DCD_PENALIZACION").val()+"', '"+$("#DCD_NETO").val()+"', '"+$("#DCD_FECHA_FACTURA").val()+"','" + $("#cCentroContable").val() + "'," + $("#aEjercicioFiscal").val() + ",'"+$("#nombre").val()+"', '1','0','"+$("#cIdRFC_RelacionGasto2").val()+"',0.00";
		
		$.getJSON("../catalogos/InsertJson.jsp",{Tabla: "TCONTRARECIBODIVERSOSCREATE", Param: elParametro, MaxReg: "", ajax: 'false'}, function(j){});
		$(".paso01").attr('disabled', true);
		$("#Agregar1").attr('disabled', true);
		
	parent.document.getElementById("pb_send").style.visibility='visible';
	parent.document.getElementById("pb_send").disabled = false;
	$("#cIDContratoObra").attr('disabled', false);
	}	
	function validar(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla==8) return true;
			patron =/[A-Za-z.\d\s\\. ]/;
			te = String.fromCharCode(tecla);
			return patron.test(te);
	}
	function validar2(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla==8) return true;
		patron =/[.\d]/;
		te = String.fromCharCode(tecla);
		return patron.test(te);
	}
	function cmdImprimir(elFormato){
		 if (elFormato =='PolizaPago'){
		 	elFormato = elFormato + 'N';
			 tipo='CR.';
		 }else{
			 tipo='';
		 }
					window.open(
						"../admin/SeguridadCatalogos?"
							+ "catalogo=CONTRARECIBO"
							+ "&accion=run"
							+ "&rn=" + elFormato + ".jasper"
							+ "&whereFolio= "+  tipo +"caNoContrarrecibo = '" + $("#caNoContrarrecibo").val()
							+"'", 			
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");
				
		setTimeout('anexo("' + elFormato + '")', 5000);	
					
					
}
function anexo( pfmt ){
	$("#totFacturas").val(0);
	queryFormPost("totFacturasPago", {async: false });
	
			if ( ($("#cllave").val() > 7 || $("#totFacturas").val()>7) && pfmt == "PolizaPagoN" ){
				
				
				window.open(
					"../admin/SeguridadCatalogos?"
						+ "catalogo=ANEXO"
						+ "&accion=run"
						+ "&rn=Anexo1.jasper"
						+ "&swhere=  and caNoContrarrecibo ='"+ $("#caNoContrarrecibo").val()+"' " ,  
					"Anexo",
					"scrollbars=1, resizable=yes, width=1024, height=768");
	}
}
	
	
	  function numberFormat(numero){//SASV 12/05/2015 Formato con separadores de miles
        var resultado = "";
        numero=numero+"";
 
        if(numero.indexOf('-',0)==0)
        nuevoNumero=numero.replace(/\,/g,'').substring(1);
        else
            nuevoNumero=numero.replace(/\,/g,'');
 
        if(numero.indexOf(".")>=0)
            nuevoNumero=nuevoNumero.substring(0,nuevoNumero.indexOf("."));
 
        for (var j, i = nuevoNumero.length - 1, j = 0; i >= 0; i--, j++)
            resultado = nuevoNumero.charAt(i) + ((j > 0) && (j % 3 == 0)? ",": "") + resultado;

        if(numero.indexOf(".")>=0)
            resultado+=numero.substring(numero.indexOf("."));
 
        if(numero.indexOf('-',0)==0)
            return "-"+resultado;
          else
            return resultado;
        
    }
	
	function quitaFmt( val ) {
		if(val=="")
			val="0";
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val;
	}
	function Sinfrmt( fld ){
		var valcol = fld.value ;
		valcol = valcol.replace("$", "");
		valcol = valcol.replace(/,/g, "");
		$("#" + fld.id).val( valcol );
	}
	function cambiafrmt( fld ){
	    $("#" + fld.id).formatCurrency();
	}
	
	function cambiafrmt2( fld ){
		montosFormatoQuitar();

	    $("#" + fld.id).formatCurrency();
	    var valcol = fld.value ;
		valcol = valcol.replace("$", "");
		valcol = valcol.replace(/,/g, "");
		$("#" + fld.id).val( valcol );
		calculaIVA();//URVP----TEMPORALMENTE
	}

	function compare_dates(hasta, hasta2) {  
		var xMonth=hasta.substring(3, 5);  
		var xDay=hasta.substring(0, 2);  
		var xYear=hasta.substring(6,10);  
		var yMonth=hasta2.substring(3, 5);  
		var yDay=hasta2.substring(0, 2);  
		var yYear=hasta2.substring(6,10);  
        if (xYear> yYear) {  
            return(true) ; 
        } else{  
          if (xYear == yYear) {   
            if (xMonth> yMonth){  
                return(true); 
            }  else   {   
              if (xMonth == yMonth)   {  
                if (xDay> yDay)  
                  return(true);  
                else  
                  return(false);  
              }  
              else  
                return(false);  
            }  
          }else  
            return(false);  
        }
    }  

	
	function fecha(){
		if (compare_dates( $("#hasta").val() , $("#hasta2").val() ) ){  
			Swal.fire("Revise","La Fecha tiene que ser menor o igual al Dia de Hoy","info");  
			$("#hasta").val("");
		}
		
	}
	function validaREPSE() {
			var numRepse = false;
			queryFormPost("consultarAplica15DObraRead", {async: false});
			
			if ( $("#cAplica15D").val() == 'S') {
				queryFormPost("consultaProveedorRead", {async: false});
				numRepse = true;
			}
			return numRepse;
	}

	function cmdGuardar(){
		montosFormatoQuitar();
		if ($("#numPaso").val() == '1'){
			
			if(convenioModificatorio){//SASV CM 25/05/2015 se guarda NoConvenio en Pago. (se modifica tabla)
					var convenio=$('#cNoConvenio2 option:selected').text();
					if(convenio==" ")
						convenio="n/a";
					$("#cNoConvenio").val(convenio);
					$.trim($("#cNoConvenio2 option:selected").text(convenio));
			}else{
					$("#cNoConvenio").val("n/a");
			}
			
			if( $("#TIPO_OPERACION").val() == "" || $("#TIPO_OPERACION").val() == "-1"){
				Swal.fire({
					  icon: 'error',
					  title: 'Capturar',
					  text: 'Debe seleccionar el tipo de operacion.'
					})
				$("#TIPO_OPERACION").focus();
				return false;
			}
			
			if( $("#cNoEstimacion").val() == "" || $("#cNoEstimacion").val() == "-1"){
				Swal.fire({
					  icon: 'error',
					  title: 'Capturar',
					  text: 'Debe seleccionar la estimación.'
					})
				$("#cNoEstimacion").focus();
				return false;
			}
			
			$('#cFolioContratoObra').val($('#id_caso').val());
			$("input[id='iva']").val($("input[id='nPorcIVAAplicable']").val());
			$("input[id='nIdConcepto']").val($('#TIPO_CONCEPTO option:selected').val());
			$("input[id='tConcepto2']").val($('#TIPO_CONCEPTO option:selected').text());
			$("input[id='noFactura2']").val($("input[id='cNoFactura']").val());
			$("input[id='tipoOper']").val($('#cIdTipoOperacion option:selected').val());
			$("input[id='impTotal']").val($("input[id='mImporteNeto']").val());
			$("#tipoBenef").val($('#cIdTipoRFC option:selected').text());
			$("#iva").val($("#nPorcIVAAplicable").val());
			$("#poriva").val($("#mImporteIVA").val());
			$("#cIdRFC_RelacionGasto2").val($("#cIdRFC").val());
			$("#cMes").val($("#fRecepcion").val().split("/")[1]);				
			queryFormPost("tRelacionGastClaveBeneficiarioRead","cIdRFC_RelacionGasto2",{async: false });
			var hayError='';
			$('.subtotall').each(function(){
			if ($(this).val()==''){
				hayError = hayError + this.name+', ';
			}
			});
			$('.paso01').each(function(){
			if ($(this).val()==''){
				hayError = hayError + this.name+', ';
			}
			});
			
			$('#cIdRFC').each(function(){
				if ($(this).val()==''){
					hayError = hayError + this.name+', ';
				}
			});
			
			if (hayError==''){
				$("#ID_DESTINO_GASTO").val( $("#destGasto").val() );
				$("#idDestinoGasto").val( $("#destGasto").val() );
				
				//Validar REPSE JGDS
				if( validaREPSE() ){
						if ( $("#cNumeroRepse").val() != ''){
							alert("Esta seguro de haber validado el numero de registro en el portal de la STPS?\nSe le recuerda que es su responsabilidad de tener el expediente actualizado y confirmar que el registro del proveedor no haya sido removido/cancelado");
							$("#repseDiv").show();
						} else { 
							alert("El pago indica que aplica el art. 15D al proveedor, sin embargo no se encontro su numero REPSE. Por favor actualice el proveedor con la informacion faltante para poder continuar. ");
							return false;
						}		
				}
				
				//SASV 13/05/2015 cambio de estatus en la estimacion "En Pago"
				$("#esPagada").val(1);
				queryFormPost("ActualizaEstatusPagoEstimacion",{async: false });
				querySelectPost("CatalogoObraTConceptoRead","TIPO_CONCEPTO", {async: false });
				concepto();
				queryFormPost("tContratoObraFactCreate",{async: false });
				queryFormPost("tPagoObraEncabezadoUpdate",{async: false });
				parent.document.getElementById("pb_save").disabled=true;
				
				$("#DCD_FACTURA").val( $("#cNoFactura").val());
				$("#DCD_FECHA_FACTURA").val($("#fRecepcion").val());
				$("#DESCRIPCION20").val($("#nPorcIVAAplicable").val());
				$("#DCD_IMP_BRUTO").val($("#mImporteBrutoFactura").val());
				$("#DCD_IVA").val($("#mImporteIVA").val());
				$("#DCD_NETO").val($("#mImporteNeto").val());				
				$("#DCD_SANCION").val($("#mImporteSancion").val());				
				$("#DCD_DEVOL").val($("#mImporteDevolucion").val());
				$("#DCD_CONCEPTO").val( $("#Descrip_Concepto").val());
				$("#DCD_AMORT").val( $("#mAmortizacionAnticipoMasIva").val() ); //SASV 14/05/2015
				$("#DCD_RETENCION").val($("#mImporteRetencion").val() );
				$("#DCD_CONTRIBUCION").val( $("#mImporteFlete4").val() );
				$("#DCD_PENALIZACION").val( $("#mTesofe").val() );
				
				$("#chk_radicado").attr('disabled', true);
              		queryFormPost("updateEsRadicadoOB",{async: false });
              	$("#chk_ingProp").attr('disabled', true);
              		queryFormPost("updateEsIngProp",{async: false });
				$("#cNoEstimacion").attr('disabled', true);
				cssReadOnly();
				$("#ctaBancaria").attr('disabled', true);
				$("#cNoConvenio2").attr('disabled', true);
				Swal.fire("OK","Carátula guardada!","success");

				$(".paso01").attr('disabled', true);
				
				$(".AyudaSyC").attr('disabled', true);
				$(".pasoDos").show();
				
				$("#numPaso").val('2');
				$("#guardar").attr("disabled", true);
				$("#L03").click();
			}else{
				Swal.fire('Debe ingresar los siguientes datos: ', hayError,"warning");
			}
		}	
	}
	
 	function Grid() {
     	if ($("#cEsRadicado").val() == "S") { //Presupuesto Radicado Validacion Ayuda
     		if (relaciones)	relaciones.length=0;
     		window.open("AyudasEPRadicado.jsp", 'AyudaEPsPagos', 'status=1, width=900px, height=780px, left=100px, scrollbars=1');
     	} else if($("#cEsIngProp").val() == "S") {
     		window.open("AyudasEPOBRA.jsp?fuenteFinancimiento=4", 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');     		
     	}
     	else {
     		window.open("AyudasEPOBRA.jsp", 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
    	 }
     	return false;
 	}
 	
	function Limpia1() {
		if(<%=id_oper==1%>){
			document.getElementById("TIPO_CONCEPTO").removeAttribute("disabled",false);
			$("#saldoCompM").val(0);
			queryFormPost(	"TFactOBRADetalleDelete", {async: false });
			$('#grdCompromisos').dataTable().fnClearTable();
			$('#grdRetClave').dataTable().fnClearTable();
			ponCerosD();
		}
	}

function creaDiagloFacturas(abrir){
	$( "#dialog-validaFact" ).dialog({
		autoOpen: false,
		height: 472,
		width: 558,
		modal: true,
		buttons: {
				"Aceptar": function(){
					bClicBtn = true;
					var aData = oTablevFact.fnGetData();
					$(".subtotall").change();
					$( this ).dialog( "close" );
					calculaIVA();		  //SASV 14/05/2015				
				},
				"Cancelar": function(){
					bClicBtn = true;
					$( this ).dialog( "close" );
				}
			},
		beforeClose: function( event, ui ) {
			return bClicBtn;			
		},							
		close: function() {	
			parent.document.getElementById("pb_save").disabled=false;
		},
		open: function(){
			leeMontosFacturas();
			creaDTFacturas();
			togleDivFacts(0);
			parent.document.getElementById("pb_save").disabled=true;
		}
	});
	if( abrir ){
		$( "#dialog-validaFact" ).dialog("open");	
	}
	//calculaIVA();//SASV
}
function leeMontosEstimacion() {
	queryFormPost({
		queryName : "montosEstimacionOP_Read",
		async : false,
		callback : function() {
			if (Number($("#mImporteBruto").val())==0){
				$("#mImporteBruto").val(importeBruto);
				$("#mImporteIVA").val($("#mIvaFactura").val());
				$("#subTotal_1").val(Number($("#mImporteBruto").val())-Number($("#mImporteSancion").val())+Number($("#mImporteDevolucion").val()));
				$(".subtotall").change();				
			}
			res_iva = parseFloat(quitaFmt($("#mImporteIVA").val() == "" ? "0"
					: $("#mImporteIVA").val()));
		}
	});
	calculaIVA();//SASV para actualizar los montos al cargar la factura.
}
function leeMontosFacturas() {
	queryFormPost({
		queryName : "readMontoFacturasPO",
		async : false,
		callback : function() {
			if (Number($("#mImporteBruto").val())==0){
				$("#mImporteBruto").val(importeBruto);
				$("#mImporteIVA").val($("#mIvaFactura").val());
				$("#subTotal_1").val(Number($("#mImporteBruto").val())-Number($("#mImporteSancion").val())+Number($("#mImporteDevolucion").val()));
				$(".subtotall").change();				
			}
			res_iva = parseFloat(quitaFmt($("#mImporteIVA").val() == "" ? "0"
					: $("#mImporteIVA").val()));
		}
	});
	calculaIVA();//SASV para actualizar los montos al cargar la factura.
}
function creaDTFacturas(){
		oTablevFact = $('#grdValidaFacturas').dataTable(
		{
			"bProcessing": true,
			"bServerSide": true,
			"bDestroy": true,
			"bSort": true, 
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vfacturaspagos&qw=folioPago=<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%> AND tipoPago='"+$("#cTipoPago").val()+"'" ,
			"bJQueryUI": true,
			"sScrollX": "100%",
			//"sScrollXInner": "100%",
			"sScrollY": "190px",
			"bPaginate": false,
			"bAutoWidth": true,
			"bInfo": true,
			aoColumns: [
						{ sName: "Serie" },
						{ sName: "Factura" },
						{ sName: "Importe" } ]	
		});
	}
	
function togleDivFacts(nIdDiv) {

	if (nIdDiv == 0) {
		$("#uploadFacturasDiv").hide();
		$("#facturasCapturadasDiv").show();
		creaDTFacturas();
		leeMontosFacturas();
	} else {
		$('#uploadFacturasFrm').attr('src',"UploadFacturasPF.jsp?tipo_pago=PAGOFEDERALIZADO");
		$("#uploadFacturasDiv").show();
		$("#facturasCapturadasDiv").hide();
	}

	queryFormPost({
		queryName : "facturasCapturadasRead",
		async : false,
		callback : function() {
			if (parseInt($("#nFacturasCapturadas").val(), 10) > 0) {
				$("#uploadFacturasTR").css("display", "none");
			} else {
				$("#uploadFacturasTR").css("display", "block");
			}
		}
	});
}
	function showDivOficio(esUpdate){
		var cmpName = "oficioDelegatorio" + (esUpdate?"Update":"");
		var divName = "oficioDelegatorioCaptura" + (esUpdate?"Update":"");
		if( $("#" + cmpName ).is(":checked") )
			$("#"+divName).show();
		else
			$("#"+divName).hide();
	}
	
	/*vgc290916 Valida que la EP que se intenta agreagar no exista ya en el pago.*/
	function validaEPCapturada(epFind){
		var existe = false;
		var eps = $("#grdCompromisos").dataTable().fnGetData();
		for( i = 0; i < eps.length; i++ ){
			if( eps[i][1] == epFind ){
				existe = true;
				break;
			}
		}
		
		return existe;
	}
	
	function insertaObjeto( folioIngresoP, EPP, montoP, mesP){
																
		if(!relaciones)
			relaciones = [];	
		
		var obj = { folioIngreso : folioIngresoP ,
					EP:EPP,
				    monto:montoP,
					mes:mesP
				  };
		
		relaciones[relaciones.length] =  obj ;
	}
	
	function verReferencia(){
		
		$("#dlgSeleccionaFonden").dialog("open");
		
		if( <%=id_oper%> != 1 ){
			queryFormPost( "proyectoFondenOB_Read", {async:false} );
			$("#verProyectoFondenDiv").css('display', 'block');
			$("#selproyectoFondenDiv").css('display', 'none');
		}		
	}
	
	function registraCalendarioEP(ep, monto) {
		var errMsg = "";
		var idTipoMovimiento = $("#TIPO_MOVIMIENTO").val();
		var idTipoConcepto = $("#TIPO_CONCEPTO").val();
		
		$.ajax({
			url : '../egresos/GeneraCalendario',
			dataType : 'json',
			type : "post",
			data : {
				"importeEP" : monto,
				"ep" : ep,
				"idTipoConcepto": idTipoConcepto,
				"idTipoMovimiento": idTipoMovimiento
			},
			async : false,
			success : function(json) {
				var exito = json.success;
				if (exito != "true") {
	
					var msg = json.data_1.result;
					errMsg = "No fue posible guardar el calendario debido al error:  " + msg;
				}
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
				errMsg = "Error guardando calendario";
			}
		});
	
		return errMsg;
	}
</script>
  </head>
	<body id="dt_example">
		<form id="frmcontratoDeObra">
		<div id="container" style="width: 90%" class="container">

				<input type="hidden" id="pagosSinRecibo"       name="pagosSinRecibo"       value="0"/>
				<input type="hidden" id="esEFO"       name="esEFO"       value="N"/>
				<input type="hidden" id="rfcValidar"  name="rfcValidar"  value=""/>
				<!-- PARA LKS EP -->
				<input type="hidden" id="TipoAutorizacion" name="TipoAutorizacion"  value="0"/>
				<input type="hidden" id="facturasDiferentes" name="facturasDiferentes"  value="0"/>
				<input type="hidden" id="otrosImpuestos" name="otrosImpuestos" value="0" />
				<input type="hidden" id="existeEstimacion" name="existeEstimacion" value="" />
				<input type="hidden" id="noEstimacion" name="noEstimacion" value="" />
				<input type="hidden" id="cValidaEstimacion" name="cValidaEstimacion" value="" />	
				<input type="hidden" id="cCveContrato" name="cCveContrato" value="" />
				<input type="hidden" id="vOGT" name="vOGT" value="" />
				<input type="hidden" id="obs" name="obs"/>
				<input type="hidden" value="0" id="m23IVA"name="m23IVA"/>
				<input type="hidden" value="0" id="mAmortizado"name="mAmortizado"/>
				<input type="hidden" value="0" id="mIMDT"name="mIMDT"/>
				<input type="hidden" value="0" id="mCNIC"name="mCNIC"/>
				<input type="hidden" value="0" id="mObra5"name="mObra5"/>
				<input type="hidden" value="0" id="mISRHonorarios"name="mISRHonorarios"/>
				<input type="hidden" value="0" id="mImporteFlete4"name="mImporteFlete4"/>
				<input type="hidden" value="0" id="mISRArrenda"name="mISRArrenda"/>
				<input type="hidden" value="0" id="mPenalizacion"name="mPenalizacion"/>		
				<input type="hidden" value="0" id="mRetImpuestoCedular"name="mRetImpuestoCedular"/>
				<input type="hidden" value="0" id="mTesofe"name="mTesofe"/>		
				<input type="hidden" value="0" id="CamInst"name="CamInst"/>
				<input type="hidden" value="0" id="mImporteFlete23"name="mImporteFlete23"/>			
				<input type="hidden" value=""  id="lHaySaldoAnticipo" name="lHaySaldoAnticipo" />	
				<input type="hidden" value="0" id="TotalNeto"  name="TotalNeto" />
				<input type="hidden" value="0" id="TotalNetoN"  name="TotalNetoN" />
				<input type="hidden" id="laPoliza" />
				<input type="hidden" id="docAplicado" value=""/>
				<input name="totFacturas" id="totFacturas" type="hidden" value="0"/>
				<input name="importeTotalEncabezado" id="importeTotalEncabezado" type="hidden" value="0"/>
				<input name="importeTotalDetalle" id="importeTotalDetalle" type="hidden" value="0"/>
				<input name="campoRFC" id="campoRFC" type="hidden" value=""/>
				<input name="tipoTramite" id="tipoTramite" type="hidden" value="<%=idTipoCaso %>"/>
				<input name="cEstatusPago" id="cEstatusPago" type="hidden" value=""/>
				<input type="hidden" name="cFolioGestion" id="cFolioGestion" value="<%=c.getFolio()%>"/>
				
				<!-- SASV 13/05/2015  -->
				<input type="hidden" id="esPagada" name="esPagada" value="" size="3" />
				<input type="hidden" id="porcAnticipo" name="porcAnticipo" value="" />
		
				<input type="hidden" value="0" id="TotalRetenciones" name="TotalRetenciones"/>
				<input type="hidden" value="0" id="campoRetencion"  name="campoRetencion"/>
				<input type="hidden" value="0" id="mIVA"name="mIVA"/> 
				
				<input type="hidden" value="0" id="mmontoestimacionMasIva"  name="mmontoestimacionMasIva" />
				<input type="hidden" value="0" id="mmontoestimacionAmortizado"name="mmontoestimacionAmortizado"/>
				<input type="hidden" value="0" id="mmontoestimacionIva"  name="mmontoestimacionIva" />
				<input type="hidden" value="0" id="mmontoestimacionRetencion"  name="mmontoestimacionRetencion" />
				
				<input type="hidden" value="0" id="DCD_ISR_D" name="DCD_ISR_D"/>
				<input type="hidden" value="0" id="DCD_IVA_D" name="DCD_IVA_D"/>
				<input type="hidden" value="0" id="DCD_MIL5_D" name="DCD_MIL5_D"/>
				<input type="hidden" value="0" id="DCD_MIL2_D" name="DCD_MIL2_D"/>
				<input type="hidden" value="0" id="DCD_CONTRIBUCION_D" name="DCD_CONTRIBUCION_D"/>
				<input type="hidden" value="0" id="DCD_OTRAS_RET_D" name="DCD_OTRAS_RET_D"/>
				<input type="hidden" value="0" id="DCD_PENALIZACION_D" name="DCD_PENALIZACION_D"/>
				<input type="hidden" value="0" id="DCD_SANCION_D" name="DCD_SANCION_D"/>
				<input type="hidden" value="0" id="DCD_DEVOL_D" name="DCD_DEVOL_D"/>
				<input type="hidden" value="0" id="DCD_AMORT_D" name="DCD_AMORT_D"/>
				<input type="hidden" value="0" id="DCD_RETENCION_D" name="DCD_RETENCION_D"/>				
					
				<input type="hidden" value="0" id="m23IVA2"name="m23IVA2"/>
				<input type="hidden" value="0" id="mIMDT2"name="mIMDT2"/>
				<input type="hidden" value="0" id="mCNIC2"name="mCNIC2"/>
				<input type="hidden" value="0" id="mObra52"name="mObra52"/>
				<input type="hidden" value="0" id="mISRHonorarios2"name="mISRHonorarios2"/>
				<input type="hidden" value="0" id="mImporteFlete42"name="mImporteFlete42"/>
				<input type="hidden" value="0" id="mISRArrenda2"name="mISRArrenda2"/>
				<input type="hidden" value="0" id="mPenalizacion2"name="mPenalizacion2"/>		
				<input type="hidden" value="0" id="mRetImpuestoCedular2"name="mRetImpuestoCedular2"/>
				<input type="hidden" value="0" id="mTesofe2"name="mTesofe2"/>		
				<input type="hidden" value="0" id="CamInst2"name="CamInst2"/>	
				<input type="hidden" value="0" id="mAmortizacionAnticipoMasIva"name="mAmortizacionAnticipoMasIva"/>	
				<input type="hidden" value="0" id="mAmortizacionAnticipoMasIvaEP"name="mAmortizacionAnticipoMasIvaEP"/>	
				<input type="hidden" id="altaAlmacen" name="altaAlmacen" />
				<input type="hidden" id="tConcepto2" name="tConcepto2"/>
				<input type="hidden" id="nIdConcepto" name="nIdConcepto" />
				<input type="hidden" id="ID_TIPO_MOVIMIENTO" name="ID_TIPO_MOVIMIENTO"/>
				<input type="hidden" id="ID_DESTINO_GASTO" name="ID_DESTINO_GASTO"/>
				<input type="hidden" id="idDestinoGasto" name="idDestinoGasto"/>
				<input type="hidden" id="codSIAFF" name="codSIAFF" />
				<input type="hidden" name="saldoCompA" id="saldoCompA"/>
				<input type="hidden" name="saldoPorEjercer" id="saldoPorEjercer"/>
				<input type="hidden" value="OBRA" id="TO_TIPO_DOCTO"name="TO_TIPO_DOCTO"/>
				<input type="hidden" name="cDocumento" id="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>"/>
				<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value=""/>
				<input type="hidden" name="elcontra" id="elcontra"/>
				<input type="hidden" value="" id="rowsAffected" name="rowsAffected"/>
				<input type="hidden" name="cFolioContratoObra" 	id="cFolioContratoObra" class="paso01" />
				<input type="hidden" name="tipoRet" id="tipoRet" value="" />
				<input type="hidden" name="resImpRet" id="resImpRet" value="" />
				<input type="hidden" name="nMes"  id="nMes" value="2" />
				<input type="hidden" name="cIdCuentaContable"  id="cIdCuentaContable"	value="" />
				<input type="hidden" name="numPaso" id="numPaso" value="1" />
				<input type="hidden" name="TOTALSUBCUENTA" id="TOTALSUBCUENTA"/>
				<input type="hidden" value="2012" id="cEjercicio" name="cEjercicio"/>				
				<input type="hidden" value="" id="tiene" name="tiene"/>
				<input type="hidden" id="cRamo" name="cRamo"/>
				<input type="hidden" id="cUnidadResponsable"name="cUnidadResponsable"/>
				<input type="hidden" name="OIRAUSU" id="OIRAUSU" />
				<input type="hidden" maxlength="15" size="15"	name="cIdRFC_RelacionGasto2" id="cIdRFC_RelacionGasto2" />
				<input type="hidden" maxlength="15" size="15" name="sumareten"	id="sumareten" />
				<input type="hidden" maxlength="15" size="15" name="doster"	id="doster" />
				<input type="hidden" maxlength="15" size="15" 	name="cIdTipoDocumento" id="cIdTipoDocumento" value="1" />
				<input type="hidden" maxlength="15" size="15" value="0"		name="nFolioCompromiso" id="nFolioCompromiso" />
				<input type="hidden" id="cCentroContable" name="cCentroContable"/>
				<input type="hidden" id="cMes" name="cMes"/>
				<input type="hidden" id="aEjercicioFiscal" value="2012"	name="aEjercicioFiscal"/>
				<input name="cEvento" type="hidden" id="cEvento" value=""/>
				<input name="contra2" type="hidden" id="contra2" value=""/>
				<input name="cllave" type="hidden" id="cllave" />
				<input name="nombre" type="hidden" id="nombre" value=""/>
				<input name="ALM" type="hidden" id="ALM" value="1"/>
				<input type="hidden" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>"/>
				<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>"/>
				<input type="hidden" name="FECHA_CARGA" id="FECHA_CARGA" value="<%=today%>"/>
				<input type="hidden" name="cSuspensionPago" id="cSuspensionPago" value=""/>
				<input type="hidden" name="cIdContrato" id="cIdContrato" value=""/>
				<input type="hidden" id="campo" name="campo" value=""/>
				<input type="hidden" id="tablaEnc" name="tablaEnc" value=""/>
				<input type="hidden" id="campoCondicion" name="campoCondicion" value=""/> 
				<input type="hidden" id="tablaDet" name="tablaDet" value=""/> 
				<input type="hidden" id="tipoAplicar" name="tipoAplicar" value=""/>
				<input type="hidden" id="mTotalAnticipo" name="mTotalAnticipo" value=""/>
				<input name="cartera" type="hidden" id="cartera" value=""/>
				<input name="capitulo" type="hidden" id="capitulo" value=""/>
				<input name="UE" type="hidden" id="UE" value=""/>
				<input name="EP" type="hidden" id="EP" value=""/>
				<input name="mImporteCartera" type="hidden" id="mImporteCartera" value=""/>
				<input type="hidden" name="IEsPasivo" id="IEsPasivo" value=""/>
				<input type="hidden" id="cOBGT" name="cOBGT" size="14"/>
				
				<!-- SASV CM  -->
				<input type="hidden" id="cNoConvenio" name="cNoConvenio" value="0"/>	
				<input type="hidden" id="firmanteExiste" name="firmanteExiste"/>
				<input type="hidden" id="cNombreVo" name="cNombreVo" />
				<input type="hidden" id="cPaternoVo" name="cPaternoVo" />
				<input type="hidden" id="cMaternoVo" name="cMaternoVo" />
				<input type="hidden" id="cPuestoVo" name="cPuestoVo" />
				<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" />
				<input type="hidden" id="tipoFirmante" name="tipoFirmante" value="PAGO_VOBO"/>
				<input type="hidden" id="cNombreA" name="cNombreA" />
				<input type="hidden" id="cPaternoA" name="cPaternoA" />
				<input type="hidden" id="cMaternoA" name="cMaternoA" />
				<input type="hidden" id="cPuestoA" name="cPuestoA" />
				<input type="hidden" id="firmanteAut" name="firmanteAut" />
				<input type="hidden" id="tipoFirmanteA" name="tipoFirmanteA" value="PAGO_AUT"/>
				
				<input name="tipo_pago" id="tipo_pago" type="hidden" value="<%=c.getTipoCaso().getGavetaAsociada()%>" />
				<input name="nFolioPago" id="nFolioPago" type="hidden" value="<%=c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>"/>
				<input name="cTipoPago" id="cTipoPago" type="hidden" value="PAGOOBRA"/>
				<input type="hidden" id="porcIVA" name="porcIVA" value=""/>
				
				<!-- SASV Radicado  -->	
				<input type="hidden" id="cEsRadicado" name="cEsRadicado" value="N"/>
				<input type="hidden" id="cEsIngProp" name="cEsIngProp" value="N"/>
				<input type="hidden" id="fuenteFinancimiento" name="fuenteFinancimiento" value=""/>
				
				<!-- hidden para la captura de oficio delegatorio -->
				<input type="hidden" name="cFolioOficioAux" id="cFolioOficioAux" value=""/>
				<input type="hidden" name="dFechaOficioAux" id="dFechaOficioAux" value=""/>
				<input type="hidden" name="cNombreTitularAux" id="cNombreTitularAux" value=""/>
				<input type="hidden" name="cApellidoPaternoTitularAux" id="cApellidoPaternoTitularAux" value=""/>
				<input type="hidden" name="cApellidoMaternoTitularAux" id="cApellidoMaternoTitularAux" value=""/>
				<input type="hidden" name="cPuestoTitularAux" id="cPuestoTitularAux" value=""/>
				<input type="hidden" name="firmanteOficioExiste" id="firmanteOficioExiste" value=""/>
				
				<!-- hidden para la captura de los datos de quien elaboro -->
				<input type="hidden" id="cNombreE" name="cNombreE" />
				<input type="hidden" id="cPaternoE" name="cPaternoE" />
				<input type="hidden" id="cMaternoE" name="cMaternoE" />
				<input type="hidden" id="cPuestoE" name="cPuestoE" />
				<input type="hidden" id="firmanteEla" name="firmanteEla" />
				
				<!-- Relacion Ingreso Fiscal con el pago -->
				<input type="hidden" id="nExisteIngresoPago" name="nExisteIngresoPago" value="0"/>
				<input type="hidden" id="mTotalIngresoPago" name="mTotalIngresoPago" value="0"/>
				<input type="hidden" id="nFolioRegistroIngreso" name="nFolioRegistroIngreso" value="0"/>
				<input type="hidden" id="cEPIngresoPago" name="cEPIngresoPago" value="0"/>
				<input type="hidden" id="nMesIngresoPago" name="nMesIngresoPago" value="0"/>
				<input type="hidden" id="mImporteIngresoPago" name="mImporteIngresoPago" value="0"/>
				<input type="hidden" id="EPAgregada" name="EPAgregada" value="0"/>
				
				<!-- hidden para la captura de oficio delegatorio VoBo-->
				<input type="hidden" name="cFolioOficioVoBoAux" id="cFolioOficioVoBoAux" value=""/>
				<input type="hidden" name="dFechaOficioVoBoAux" id="dFechaOficioVoBoAux" value=""/>
				<input type="hidden" name="cNombreTitularVoBoAux" id="cNombreTitularVoBoAux" value=""/>
				<input type="hidden" name="cApellidoPaternoTitularVoBoAux" id="cApellidoPaternoTitularVoBoAux" value=""/>
				<input type="hidden" name="cApellidoMaternoTitularVoBoAux" id="cApellidoMaternoTitularVoBoAux" value=""/>
				<input type="hidden" name="cPuestoTitularVoBoAux" id="cPuestoTitularVoBoAux" value=""/>
				<input type="hidden" name="firmanteOficioVoBoExiste" id="firmanteOficioVoBoExiste" value=""/>
				<input type="hidden" name="mAmortizacionAnticipoAcumulado" id="mAmortizacionAnticipoAcumulado" value=""/>
				<input type="hidden" id="cAplica15D" name="cAplica15D" value="false"/>
				<input type="hidden" id="cNumeroRepse" name="cNumeroRepse" value="false"/>
				
				<input type="hidden" id="msgRF" name="msgRF" value=""/>
				
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
				
				  <div class="dvGeneral">
				  	<div class="card-header"> <h3> Pago de Obra - Recepci&oacute;n De Documentos </h3> </div>
					<hr class="mt-3"/>
			
					<div id="ContratoDeObra">
						<div class="row" >					
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1 divAutorizar">
								<label for="lbAutoriza"> Autorizar: </label>&nbsp;
								<input type="radio" name="grpAutorizar" id="grpAutorizar" class="form-check-input" value = "Si" onClick="habilitaGuardar(1)" checked="checked"/> Si&nbsp;&nbsp;
								<input type="radio" name="grpAutorizar" id="grpAutorizar" class="form-check-input" value = "No" onClick="habilitaGuardar(2)"/> No
							</div>										
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1" id="divImprimePoliza">
								<img src="imagenes/Imprimir.png" width="25" height="21" onClick="cmdImprimir('PolizaPago');"/> Poliza  
							</div>					
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1" id="divImprimeAnexo">
								<img src="imagenes/Imprimir.png" width="25" height="21" onClick="anexo('PolizaPagoN');"/> Anexo  
							</div>						
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<span id="EditaFirmas" style="visibility:hidden"><a href="#" onclick="updateFirmantes();">Firmas*</a></span>  
							</div>
						</div>
						
						<div class="row">					
							<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="caNoContrarrecibo"> ContraRecibo: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="text" id="caNoContrarrecibo" name="caNoContrarrecibo" class="form-control form-control-sm" readonly value="0" />
							</div>											
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
							</div>
						</div>
						
						<div class="row" >					
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
								<label for="cIDContrato"> No Contrato: </label>
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
								<div class="input-group">	 														
									<input type="text" name="cIDContratoObra" id="cIDContratoObra" onChange="FiltroRetenciones();" class="form-control form-control-sm AyudaSyC" readonly title="Selecciona un contrato"/>
									<input type="hidden" name="cIDContratoObra2" id="cIDContratoObra2" size="40" maxlength="40" />
								</div>							
							</div>	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
								<label for="cIDContrato"> Folio: </label>													
							</div>	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input name="id_caso" type="text" id="id_caso" class="form-control form-control-sm" readonly/>							
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<input type="button" value="Borrar" onclick="cmdBorrar();" id="Borrar" name="Borrar" title="Elimina la solicitud temporal" class="btn btn-secondary btn-sm"/>
							</div>
						</div>	
						
						<div class="row" >					
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
								<label for="cIdRFC"> RFC: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																			
								<input type="text" name="cIdRFC" id="cIdRFC" class="form-control form-control-sm" size="15" maxlength="15" readonly/>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1"> 
								<input type="text" name="cobjetocontrato" id="cobjetocontrato" class="form-control form-control-sm" size="50" maxlength="50" readonly/>
								<select  id="cNoConvenio2" name="cNoConvenio2" style="visibility:hidden" class="form-select form-select-sm"></select>							
							</div>	
						</div>
						
						<div class="row">					
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
								<label for="fRecepcion"> Fecha Captura: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<div class="input-group">								
									<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
									<input type="text" class="form-control form-control-sm paso01" id="fRecepcion" name="fRecepcion" readonly/>
									<input type="hidden" maxlength="10" size="10" id="fRecepcion2" name="fRecepcion2"/> 							
								</div> 							
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
								<label for="fRecepcion"> Fecha Pago: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<div class="input-group">								
									<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
									<input type="text" maxlength="10" size="10" class="form-control form-control-sm paso01" id="fecha_Pago" name="fecha_Pago"/>
								</div>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">																			
								<label for="TIPO_OPERACION"> Tipo de Operaci&oacute;n: </label>
							</div>						
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<select class="form-select form-select-sm paso01" id="TIPO_OPERACION" name="TIPO_OPERACION"></select>
							</div>
						</div>
						
						<div class="row">					
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
								<label for="ctaBancaria"> Cta Bancaria: </label>
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">																			
								<select id="ctaBancaria" name="ctaBancaria" class="form-select form-select-sm"></select>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">																			
								<label for="chk_radicado"> Presupuesto Radicado: </label>
							</div>						
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="checkbox" id="chk_radicado" name="chk_radicado" value="S" class="form-check-input" checked="checked" disabled="disabled"/>
							</div>						
						</div>
						
						<div class="row">					
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
								<label for="destGasto"> Tipo Destino: </label>
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
								<select id="destGasto" name="destGasto" class="form-select form-select-sm paso01"></select>
								<input type="hidden" maxlength="10" size="10" id="ID_DESTINO_GASTO2" name="ID_DESTINO_GASTO2"/>						
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<span id="EditaFacturas" style="visibility:hidden">
									<a href="#" onclick="creaDiagloFacturas('true'); return false;"> Facturas... </a>
								</span>							
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
								<label for="chk_ingProp"> Ingresos Propios: </label>					
							</div>					
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								<input type="checkbox" id="chk_ingProp" name="chk_ingProp" class="form-check-input" value="N" />
							</div>
						</div>
						
						<div class="row">					
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
								<label for="cNoEstimacion"> Estimaci&oacute;n: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<select id="cNoEstimacion" name="cNoEstimacion" class="form-select form-select-sm" onchange="calculaIVA();"></select>
								<input type="hidden" maxlength="10" size="10" id="ID_TIPO_FONDO" name="ID_TIPO_FONDO"/>
								<input type="hidden" name="mmontoestimacion" id="mmontoestimacion" value="0" class="paso01" size="14" readonly style="text-align: right"/>						
							</div>					
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<label for="desde"> Periodo De: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<div class="input-group">								
									<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
									<input type="text" maxlength="10" size="10" class="form-control form-control-sm paso01" id="desde" name="desde"/>
								</div>												
							</div>					
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								<label for="hasta"> Hasta: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<div class="input-group">								
									<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
									<input type="text" maxlength="10" size="10" class="form-control form-control-smpaso01"	id="hasta" name="hasta" onChange="fecha()"/>
									<input type="hidden" maxlength="10" size="10" 	id="hasta2" name="hasta2"/>
								</div>						
							</div>
						</div>
					</div>			
				</div>
				
				<br/>
		
				<div id="multitabs" style="width: 120%">
					<div id="tabs" style="width: 80%">
						<ul>
							<li><a id="L01" href="#tabs-1">Detalle de operaci&oacute;n</a></li>
							<li><a id="L02" href="#tabs-2">Retenciones</a></li>
							<li><a id="L03" href="#tabs-3" class="pasoDos">Devengado</a></li>
							<li><a id="L04" href="#tabs-4" class="pasoTres">Documentaci&oacute;n</a></li>
						</ul>
						
						<div id="tabs-1">
							<div id="operacion">
								<select id="NumPagoAMF" name="NumPagoAMF" style="visibility:hidden">
									<option value="***" selected>- Selecciona AMF  -</option>
								</select>
								<input type="text" name="nClaveAMF" id="nClaveAMF" style="visibility:hidden"/>
								<input type="text" name="numFolioAMF" id="numFolioAMF" style="visibility:hidden"/>
								<input type="text" name="PagoAMF" id="PagoAMF" style="visibility:hidden"/>
								
								<div class="row">				
				    				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
									</div>	
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										&nbsp;
										<input type="hidden" name="cNoFactura" style="text-align: right" class="form-control form-control-sm paso01" id="cNoFactura" size="16" maxlength="10" style="visibility:hidden" title="Capture el Numero de Factura" />
									</div>
									<div class="col-12 col-lg-4 col-md-4 col-sm-12">
									</div>									
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										% Amortizacion <input type="text" name="nPorcAmortizacion" onblur="cambiafrmt2(this);" style="text-align: right" onChange="Amortizacion()" class="form-control form-control-sm" id="nPorcAmortizacion" maxlength="10" size="10" value="0.0" readonly/>
										<input type="hidden" name="nPorcAmortizacion2" id="nPorcAmortizacion2"/> 
									</div>
								</div>
								
								<div class="row">				
				    				<div class="col-12 col-lg-8 col-md-8 col-sm-12">
									</div>																		
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										Acumulado Amortizacion <input type="text" name="mAmortizacionAcumulado" onblur="cambiafrmt2(this);" style="text-align: right" id="mAmortizacionAcumulado" class="form-control form-control-sm" maxlength="10" size="10" value="0.0" readonly/>
									</div>
								</div>
								
								<div class="row">				
				    				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
									</div>	
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										Imp. Bruto Estimacion <input name="mImporteBrutoFactura" onkeypress="return validar2(event)" type="text" style="text-align: right" class="form-control form-control-sm paso01" id="mImporteBrutoFactura" value="0.0" size="16" readonly/>									
									</div>
									<div class="col-12 col-lg-1 col-md-1 col-sm-12">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										 Amortizaci&oacute;n Anticipo
										 <div class="input-group">									 	
										 	<input name="mAmortizacionAnticipo" type="text" class="form-control form-control-sm subtotall" id="mAmortizacionAnticipo" onblur="cambiafrmt2(this);" style="text-align: right" maxlength="10" size="10" value="0.0" readonly/>&nbsp;-
										 </div>
										 <input type="checkbox" name="amortizar" id="amortizar" onclick="amortiza()" checked="checked" style="visibility: hidden">
									</div>
									<div class="col-12 col-lg-1 col-md-1 col-sm-12">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										% IVA <input name="nPorcIVAAplicable" type="text" class="form-control form-control-sm subtotall" readonly id="nPorcIVAAplicable" maxlength="10" style="text-align: right" size="10" value="0.0"/>								
									</div>
								</div>
								
								<div class="row">				
				    				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
									</div>	
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										IVA Estimacion <input type="text" name="mIvaFactura" style="text-align: right" class="form-control form-control-sm paso01" id="mIvaFactura"  size="16" readonly value="0">								
									</div>
									<div class="col-12 col-lg-1 col-md-1 col-sm-12">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										 Subtotal Obra
										 <div class="input-group">
										 	<input name="subTotal_2" type="text" class="form-control form-control-sm subtotall" readonly id="subTotal_2" maxlength="10" style="text-align: right" size="10" value="0.0"/>&nbsp;=
										 </div>
									</div>
									<div class="col-12 col-lg-1 col-md-1 col-sm-12">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										Saldo Total Compromiso <input type="text" name="saldoCed" id="saldoCed" maxlength="10" style="text-align: right" class="form-control form-control-sm subtotall" value="0.0" size="10" readonly />
										<input type="hidden" name="saldoCed2" id="saldoCed2" maxlength="10" style="text-align: right" value="0" size="10" /> 								
									</div>
								</div>
								
								<div class="row">				
				    				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
									</div>	
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										Neto Estimacion <input name="mImporteNetoFactura" type="text" class="form-control form-control-sm paso01" value="0.0" id="mImporteNetoFactura" style="text-align: right" maxlength="10" size="16" readonly/>														
									</div>
									<div class="col-12 col-lg-1 col-md-1 col-sm-12">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										 Monto IVA
										 <div class="input-group">
										 	<input name="mImporteIVA" type="text" class="form-control form-control-sm subtotall" id="mImporteIVA" value="0.0" maxlength="10" style="text-align: right" size="10" readonly/>&nbsp;+
										 </div>
										 <input name="mImporteMasIva" type="hidden" class="subtotall" id="mImporteMasIva" style="text-align: right" maxlength="10" size="10" value="0"/>
									</div>
									<div class="col-12 col-lg-1 col-md-1 col-sm-12">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										Saldo del Contrato <input name="mTotal" onkeypress="return validar2(event)" class="form-control form-control-sm" type="text" style="text-align: right" id="mTotal" value="0.0" maxlength="10" readonly size="10"> 								
									</div>
								</div>
								
								<div class="row">				
				    				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
									</div>	
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										Monto Estimacion <input name="mEstimacion" onkeypress="return validar2(event)" type="text" class="form-control form-control-sm subtotall" value="0.0" id="mEstimacion" onblur="cambiafrmt2(this);" style="text-align: right" maxlength="10" size="16"/>														
									</div>
									<div class="col-12 col-lg-1 col-md-1 col-sm-12">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										 Monto Retenciones
										 <div class="input-group">
										 	<input name="mImporteRetencion" type="text" class="form-control form-control-sm subtotall" id="mImporteRetencion" value="0.0" onkeypress="return validar2(event);" maxlength="10" style="text-align: right" size="10"/>&nbsp;-
										 </div>
									</div>
									<div class="col-12 col-lg-1 col-md-1 col-sm-12">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										Comprometido <input name="acumulado" onkeypress="return validar2(event)" class="form-control form-control-sm" type="text" style="text-align: right" id="acumulado" value="0.0" maxlength="10" readonly size="10"> 								
									</div>
								</div>
								
								<div class="row">				
				    				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
									</div>	
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										Monto Sanciones
										<div class="input-group"> 
											<input name="mImporteSancion" onkeypress="return validar2(event)" type="text" class="form-control form-control-sm subtotall" value="0.0" id="mImporteSancion" onblur="cambiafrmt2(this);" style="text-align: right" maxlength="10" size="16"/>&nbsp;-
										</div>								
									</div>
									<div class="col-12 col-lg-1 col-md-1 col-sm-12">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										 Monto Penalizaciones
										 <div class="input-group">
										 	<input name="mImportePenalizacion" type="text" class="form-control form-control-sm subtotall" id="mImportePenalizacion" value="0.0" onblur="cambiafrmt2(this);" maxlength="10" style="text-align: right" size="10" />&nbsp;-
										 </div>
									</div>
									<div class="col-12 col-lg-1 col-md-1 col-sm-12">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										Acumulado Sanciones <input name="mImporteSancionAcumulado" onkeypress="return validar2(event)" class="form-control form-control-sm" type="text" style="text-align: right" id="mImporteSancionAcumulado" value="0.0" maxlength="10" readonly size="10">
										<input name="mTotal2" type="hidden" style="text-align: right" id="mTotal2" value="0" maxlength="10" size="14"  /> 								
									</div>
								</div>
								
								<div class="row">				
				    				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
									</div>	
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										Monto Devoluciones
										<div class="input-group"> 
											<input name="mImporteDevolucion" type="text" value="0.0" onkeypress="return validar2(event)" maxlength="10" size="16" class="form-control form-control-sm subtotall" id="mImporteDevolucion" onblur="cambiafrmt2(this);" style="text-align: right"/>&nbsp;+
										</div>								
									</div>
									<div class="col-12 col-lg-1 col-md-1 col-sm-12">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										 Importe Neto
										 <div class="input-group">
										 	<input name="mImporteNeto" value="0.0" style="text-align: right" type="text" class="form-control form-control-sm subtotall" readonly id="mImporteNeto" size="10"/>&nbsp;=
										 </div>
										 <input name="mImporteNetoEP" value="0" type="hidden" readonly id="mImporteNetoEP" size="10"/>
									</div>
									<div class="col-12 col-lg-1 col-md-1 col-sm-12">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										Acumulado Devoluciones <input type="text" name="mImporteDevolucionAcumulado" id="mImporteDevolucionAcumulado" class="form-control form-control-sm" onkeypress="return validar2(event)" maxlength="10" style="text-align: right" value="0.0" size="10" readonly/>
										 								
									</div>
								</div>
								
								<div class="row">				
				    				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
									</div>	
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										Subtotal 
										<div class="input-group">
											<input name="subTotal_1" value="0" type="text" style="text-align: right" onkeypress="return validar2(event)" class="form-control form-control-sm subtotall" id="subTotal_1" size="16" readonly/>&nbsp;=
										</div>								
									</div>
									<div class="col-12 col-lg-1 col-md-1 col-sm-12">
									</div>							
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										Importe a Ejercer <input name="mImpEjercer" onKeyPress="return validar2(event)" class="form-control form-control-sm" type="text" style="text-align: right" id="mImpEjercer" value="0.0" maxlength="10" size="10" readonly/> 
										<input type="hidden" name="mImporteAnticipoIVA" class="subtotall"	id="mImporteAnticipoIVA" value="0" />								 
									</div>
									<div class="col-12 col-lg-1 col-md-1 col-sm-12">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
										Anticipo <input type="text" name="saldoAnticipo" id="saldoAnticipo" value="0.0" class="form-control form-control-sm subtotall" maxlength="10" style="text-align: right" size="10" readonly />																
									</div>
								</div>
								
								<div class="row">				
				    				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
									</div>														
									<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">	
										Concepto <textarea cols="80" rows="8" name="Descrip_Concepto" onkeypress="return validar(event)" class="form-control form-control-sm paso01" id="Descrip_Concepto" title="Descripcion del pago" readonly></textarea>								  							
									</div>
								</div>	
								
							</div>
						</div>
		
						<div id="tabs-2">
							<div id="Retenciones">
				    			<div class="row">							
									<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
										<table align="center" id="tretencion" class="table table-striped table-bordered">								
											<thead>
												<tr>
													<th>Codigo</th>
													<th>Retencion</th>
													<th>Porcentaje</th>
												</tr>
											</thead>
											<tbody>
											</tbody>							
										</table>
									</div>
								</div>
							</div>				
						</div>
						
						<div id="tabs-3">				
							<div class="row">						    																	
								<div class="col-12 col-lg-3 col-md-3 col-sm-12">	
									Tipo De Concepto <select id="TIPO_CONCEPTO" name="TIPO_CONCEPTO" onchange="concepto();" class="form-select form-select-sm"></select>
									<span id="verFondenSPAN" >&nbsp; <a href="#" onclick="verReferencia();return false;">Ver Proyecto FONDEN</a> </span>								  							
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								</div>
								<div class="col-12 col-lg-3 col-md-3 col-sm-12">	
									Tipo De Movimiento <select id="TIPO_MOVIMIENTO" name="TIPO_MOVIMIENTO" class="form-select form-select-sm"></select>								  							
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
									<input type="button" value="Limpia" onclick="Limpia1();" id="Limpia" name="Limpia" class="btn btn-secondary btn-sm"/>
								</div>
							</div>
							
							<div class="row">																						
								<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">								
									Estructura Programática																			 							 				
								</div>															
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
									Monto Devegnado																									 							 				
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
									Monto Acumulado																									 							 				
								</div>
							</div>
							
							<div class="row">																			
								<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">								
									<input type="text" id="ep" name="ep" size="61" readonly class="form-control form-control-sm"/> 								 																		 							 				
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
									<input name="button" type="button" id="nIdClaveEgresos2" onclick="Grid()" value="..." size="5" class="btn btn-secondary btn-sm"/> 																		 							 					
								</div>							
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
									<input type="text" name="montoDev" value='0' id="montoDev" onblur="cambioss();" maxlength="16" style="text-align:right" class="form-control form-control-sm" onkeypress="return validar2(event)"/> 																		 							 					
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
									<input type="text" name="saldoCompM" id="saldoCompM" value="0" maxlength="16" style="text-align:right" class="form-control form-control-sm" readonly /> 																		 							 					
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">	
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
									<input type="button" value="Agregar" onclick=" FiltroMovimientos();" id="agrega2" class="btn btn-secondary btn-sm paso9" name="agrega2" title="Agrega EP" />																																				 							 				
								</div>	
							</div>		
			
							<div class="rowr">							
								<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">		
									<table   class="display" id="grdCompromisos"  >					
									    <thead>
							                <tr> 
							           			<th nowrap>Clave Presupuestaria SIAF</th>
												<th nowrap>Cod. SIF</th>
												<th>Importe</th>
							                </tr>
							            </thead>
							        	<tbody>
							 			</tbody>
							            <tfoot>
							            </tfoot>
									</table>
								</div>
							</div>
							
							<div class="rowr">							
								<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">							
									<table cellpadding="0" cellspacing="0" style="visibility:hidden" border="0" class="display" id="grdRetClave"  >
										<thead>
											<tr>
												<th>EP</th>
												<th>Mes</th>
												<th>Importe</th>
											</tr>
										</thead>
										<tbody>
										</tbody>
										<tfoot>
										</tfoot>
									</table>
								</div>
							</div>
						</div>
		
						<div id="tabs-4">
							<div class="row">
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">	
								</div>						    				
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									No. Folio <input name="DCD_FACTURA" type="text" id="DCD_FACTURA" onkeypress="return validar(event)" class="form-control form-control-sm paso03" readonly size="15"/>								
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Fecha de factura 
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
										<input name="DCD_FECHA_FACTURA" type="text" id="DCD_FECHA_FACTURA" class="form-control form-control-sm paso03" maxlength="10" size="10" readonly/>
									</div>
								</div>							
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Tipo de beneficiario <input name="DCD_TBEN" type="text" class="form-control form-control-sm paso03" size="15" id="DCD_TBEN" readonly/>
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">
									Clave de beneficiario <input name="DCD_CBEN" type="text" class="form-control form-control-sm paso03" size="15" id="DCD_CBEN" readonly/>
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								  	Tipo de operaci&oacute;n
								  	<select name="DCD_TIPO_OPE" id="DCD_TIPO_OPE" class="form-select form-select-sm paso03">
										<option value="85" class="paso03">85 OTROS</option>
									</select>							
								</div>
							</div>
							
							<div class="row">						    		
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">	
								</div>						    						
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Porcentaje IVA <input name="DESCRIPCION20" type="text" class="form-control form-control-sm paso03" id="DESCRIPCION20" size="15" onKeyPress="return validar2(event)" value="0" readonly/>								
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Importe Total <input type="text" id="DCD_IMP_BRUTO" class="form-control form-control-sm paso03" size="15" onkeypress="return validar2(event)" name="DCD_IMP_BRUTO" readonly/>								
								</div>							
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									IVA Desglose <input name="DCD_IVA" type="text" id="DCD_IVA" size="15" value="0" class="form-control form-control-sm paso03" onKeyPress="return validar2(event)" readonly/>
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">
									IVA Retención <input name="DCD_IVADES" type="text" id="DCD_IVADES" size="15" class="form-control form-control-sm paso03" value="0" onKeyPress="return validar2(event)" readonly/>
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								  	ISR <input name="DCD_ISR" type="text" id="DCD_ISR" size="15" value="0" class="form-control form-control-sm paso03" onkeypress="return validar2(event)" readonly/>							
								</div>
							</div>
							
							<div class="row">
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>						    				
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Ret. 0.5% <input name="DCD_MIL5" type="text" class="form-control form-control-sm paso03" id="DCD_MIL5" size="15" onKeyPress="return validar2(event)" value="0" readonly/>								
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Ret. 0.2% <input type="text" id="DCD_MIL2" class="form-control form-control-sm paso03" size="15" onkeypress="return validar2(event)" name="DCD_MIL2" readonly/>								
								</div>							
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Beneficio Social <input name="DCD_CONTRIBUCION" type="text" id="DCD_CONTRIBUCION" size="15" value="0" class="form-control form-control-sm paso03" onKeyPress="return validar2(event)" readonly/>
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">
									Impuesto Cedular <input name="DCD_OTRAS_RET" type="text" id="DCD_OTRAS_RET" size="15" class="form-control form-control-sm paso03" value="0" onKeyPress="return validar2(event)" readonly/>
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								  	Penalizaciones <input name="DCD_PENALIZACION" type="text" id="DCD_PENALIZACION" size="15" value="0" class="form-control form-control-sm paso03" onkeypress="return validar2(event)" readonly/>							
								</div>
							</div>
							
							<div class="row">
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>						    				
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Sanción <input name="DCD_SANCION" type="text" class="form-control form-control-sm paso03" id="DCD_SANCION" size="15" onKeyPress="return validar2(event)" value="0" readonly/>								
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Devolución <input type="text" id="DCD_DEVOL" class="form-control form-control-sm paso03" size="15" onkeypress="return validar2(event)" name="DCD_DEVOL" readonly/>								
								</div>							
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Amort. Anticipo <input name="DCD_AMORT" type="text" id="DCD_AMORT" size="15" value="0" class="form-control form-control-sm paso03" onKeyPress="return validar2(event)" readonly/>
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">
									Total Retención <input name="DCD_RETENCION" type="text" id="DCD_RETENCION" size="15" class="form-control form-control-sm paso03" value="0" onKeyPress="return validar2(event)" readonly/>
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								  	Neto <input name="DCD_NETO" type="text" id="DCD_NETO" size="15" value="0" class="form-control form-control-sm paso03" onkeypress="return validar2(event)" readonly/>							
								</div>
							</div>
							
							<br/>
							
							<div class="row">						    		
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								</div>		
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									<input type="button" id="Agregar1" name="Agregar1" value="Agregar" onclick="fnClickAddRowComp();" class="btn-secondary btn-sm"/>								
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">
									<div class="input-group" id="divImprime1" >
										<img src="imagenes/Imprimir.png" width="33" height="28" onClick="cmdImprimir('ContrareciboN');">Contrarecibo
									</div>
								</div>						
							</div>
							
							<div class="row">
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
								</div>												
								<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">	
									Concepto <textarea cols="50" rows="3" name="DCD_CONCEPTO" onkeypress="return validar(event)" class="form-control form-control-sm paso03" id="DCD_CONCEPTO" ></textarea>								  							
								</div>
							</div>
							
							<div class="rowr">							
								<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">	
									<table   class="display" id="grdFacturas">
										<thead>
											<tr>
												<th nowrap>&nbsp;</th>
												<th align="left">&nbsp;</th>
												<th align="left">&nbsp;</th>
												<th align="left">&nbsp;</th>
												<th align="left">&nbsp;</th>
												<th align="left">&nbsp;</th>
												<th align="left">&nbsp;</th>
												<th align="left">&nbsp;</th>
												<th align="left">Descuentos</th>
												<th align="left">&nbsp;</th>
												<th align="left">&nbsp;</th>
												<th align="left">&nbsp;</th>
												<th align="left">&nbsp;</th>
												<th align="left">&nbsp;</th>
												<th align="left">&nbsp;</th>
											</tr>
											<tr>
												<th nowrap>No. Factura</th>
												<th align="left">Fecha Factura</th>
												<th align="left">Tipo beneficiario</th>
												<th align="left">Cve Benef</th>
												<th align="left">Tipo oper</th>
												<th align="left">%IVA</th>
												<th align="left">Importe Total</th>
												<th align="left">IVA desglose</th>
												<th align="left">IVA</th>
												<th align="left">ISR</th>
												<th align="left">Ret. 0.5</th>
												<th align="left">Ret 0.2</th>
												<th align="left">Beneficio social</th>
												<th align="left">Impuesto cedular</th>
												<th align="left">Penalizaciones</th>
											</tr>
										</thead>
										<tbody>
										</tbody>
										<tfoot>
										</tfoot>
									</table>
								</div>
							</div>			
						</div>
					</div>
				</div>
		
				<div id="dialog-form" title="Aplicación Presupuestal/Contable">	
					<div id="divEspera" align="center">Espere por favor....
					  <img border="0" src="../imagenes/espera.gif" height="30">
					</div>
					<div id="divAplica" >				
						<iframe id="ifAplica" src="about:blank"></iframe>
					</div>
				</div>
				<div id="dialog-Procesando" title="Procesando">
		  			<div id="divEsperaProcesando" style="visibility: hidden" align="center">Espere por favor....
					  <img border="0" src="../imagenes/espera.gif" height="30">
					</div>
				</div>
				
				<div class="modal" tabindex="-1" role="dialog" id="dialog-validaOLI" data-mdb-keyboard="true" data-mdb-backdrop="static">
				  <div class="modal-dialog" role="document">
				    <div class="modal-content">
					      <div class="modal-header">	
					      	<h6 class="modal-title">Digite el Número de OLI a Validar</h6>		
					      	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
					      </div>
					      <div class="modal-body">
					      	<div class="row">										
								<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
									<input style="text-align: right;" name="cOLI" type="text" id="cOLI" class="form-control" value="" />
								</div>
							</div>		
					   	 </div>
					     <div class="modal-footer">
					        <button type="button" id="btnAceptarOli" onclick="aceptarOli();" class="btn btn-primary">Aceptar</button>
					        <button type="button" class="btn btn-secondary" onclick="cerrarOli();"  data-bs-dismiss="modal">Cerrar</button>
					     </div>
			    	 </div>
			  	   </div>
				</div>
										
				<div id="dialog-validaFact" title="Validación de Facturas">
					<div id="uploadFacturasDiv">
						<iframe id="uploadFacturasFrm" src="UploadFacturasPF.jsp?tipo_pago=PAGOOBRA" align="top" frameborder="0" height="360" width="510"></iframe>
					</div>
					<div id="facturasCapturadasDiv">
						<div class="row">										
							<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
							</div>		
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<a href="#" onclick="togleDivFacts(1);return false;">Cargar Facturas</a>								  							
							</div>
						</div>
						
						<h5> Facturas Cargadas </h5>
						<hr class="mt-3">
						
						<div class="rowr">							
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">	
								<table id="grdValidaFacturas">
									<thead>
										<tr>
											<th>Serie</th>
											<th>Factura</th>
											<th>Importe Bruto</th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
						
						<div class="rowr">							
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
								Total de las facturas:&nbsp;&nbsp;&nbsp;
								<input type="text" style="text-align: right;" name="mTotalFacturaV" id="mTotalFacturaV" value="0.00" size="15" maxlength="15" class="form-control form-control-sm" readonly />
							</div
						</div>	
					</div>
				</div>
				
				<div id="dlgSeleccionaFonden">
					<fieldset>
						<legend> Seleccione el proyecto de FONDEN </legend>
						<table>
							<tr>
								<td align="right"> Proyecto: </td>
								<td>
									<div id="selproyectoFondenDiv" >
										<select id="proyectoFonden" name="proyectoFonden">
										</select>
									</div>
									<div id="verProyectoFondenDiv"  >
										<input type="text" id="proyectoFondenRead" name="proyectoFondenRead" size="24" readonly="readonly"/>
									</div>									
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
		</div>
		</form>		
						
				<!-- Dialogo Firmantes -->
				<jsp:include page="Firmantes.jsp"></jsp:include>
				<!-- Fin Dialogo Firmantes -->
			
	</body>
</html>