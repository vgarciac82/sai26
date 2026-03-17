<%@page import="com.syc.contable.core.CatalogoURFIELBusinessLogic"%>
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>

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
//cRamo="16";
algo = usuario.getLogin();
String numeroEmpleado = usuario.getNumeroEmpleado();

	/*FAV20171019 Se guarda en base el prefijo de CxP*/
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
	String cxpPrefijo = cabl.getSystemSetting("CXP_PREFIJO") == null?"CP":cabl.getSystemSetting("CXP_PREFIJO");
	
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
    <title>Pago Federalizado - Recepción de documentos</title>

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

	<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

	<style type="text/css" title="currentStyle">
		@import "css/demo_page.css";
		@import "css/demo_table_jui.css";
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/interfaz.css";
	</style>
	
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
	<script type="text/javascript" src="js/FacturaFederalizado.js"></script>
	<script type="text/javascript" src="js/ImpuestosRetenciones.js"></script>
	<script type="text/javascript" src="js/ValidaMesContable.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="js/Firmantes.js"></script>
	<script type="text/javascript" src="js/ActualizaFIEL.js"></script>
	<script type="text/javascript" charset="utf-8">
	var bClicBtn = false;
	var breturnVal = false;
	var oTablevFact;
	var id_oper = <%=id_oper%>;
	var numeroEmpleado = "<%=numeroEmpleado%>"


	var cNombreElabora = "<%=cNombreElabora%>";
	var cApellidoPaternoElabora  = "<%=cApellidoPaternoElabora%>";
	var cApellidoMaternoElabora  = "<%=cApellidoMaternoElabora%>";
	var cPuestoElabora = "<%=cPuestoElabora%>";
	
	var muestraDivImprimePoliza = false;
		
	/*FAV20171019 Se guarda en base el prefijo de CxP*/
	var cxpPrefijo = "<%=cxpPrefijo%>";
	
	/* Variable que indica si el pago es con firma (FIEL) */
	var permitePagoSinFIEL = <%=permitePagoSinFIEL%>;
	
	var esConsulta = <%=esConsulta%>;

		$(document).ready(
			function()
			{
				if( permitePagoSinFIEL ) {
					$("#AutorizaConFielTD").css("display","block");
				}else{
					$("#autorizadoPorFiel ").val("true");
				}
			
				queryFormPost({
					queryName : "tipoAutorizacionRead",
					async:false,
					callback: function(){
						if( $("#TipoAutorizacion").val() == "N" ){
							muestraDivImprimePoliza = true;
						}
					}
				});
				
				creaDialogoFID();
				
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

			$( "#dialog-validaOLI" ).dialog({
				autoOpen: false,
				height: 250,
				width: 250,
				modal: false,
				buttons: {
						"Aceptar": function() 
						{
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
												alert("El Importe Capturado sobregira la Cartera ");
											}
										}else{
											alert("El Número de OLI no es Válido ");
										}
									}else{
										var msg = RS.data_1.result;
										alert("Error al intentar validar OLI:\n"+msg);
									}
								},
								error : function(xhr, textStatus, errorThrown) {
									alert("Advertencia: " + xhr.responseText + "\nEstatus: "
											+ textStatus + "\n" + errorThrown);
									r = true;
								}
							});
							$( this ).dialog( "close" );
						},
						"Cancelar": function() {
							breturnVal = false;
							$( this ).dialog( "close" );							
						}
					},
				close: function() {										
				}							
			});
			
			creaDiagloFacturas();
			creaDTFacturas();

			$("#Limpia").button();
			$("#agrega2").button();
			$("#nIdClaveEgresos2").button();
			$("#Agregar1").button();
			$("#cancelar").button();
			$("#pbAgregar").button().click(function() {
					if ( $("#cFactura").val() == "" || Number( $("#cFactura").val() ) <= 0 ){
						alert("Falta Capturar el Número de Factura");
						return;
					}

					if ( $("#mImporteFact").val() == "" ||  Number( $("#mImporteFact").val() ) <= 0 ){
						alert("Falta Capturar el Importe de la Factura");
						return;
					}

					var aData = oTablevFact.fnGetData();
					for(var i=0; i<aData.length; i++) {
						var cFactura = aData[ i ][ 0 ] + aData[ i ][ 1 ];
						if( $("#cSerie").val() + $("#cFactura").val() == cFactura){
							alert("La Factura ya está capturada");
							$("#cSerie").val( "" );
							$("#cFactura").val( "" );
							$("#mImporteFact").val( "" );
							return;
						}
					}

					var mImporte = quitaFmt( $("#mImporteFact").val() );
					$("#mImporteFact").formatCurrency();
					$('#grdValidaFacturas').dataTable().fnAddData( [ $("#cSerie").val(), $("#cFactura").val(), $("#mImporteFact").val()] );
					mImporte = Number( quitaFmt( $("#mTotalFacturaV").val() ) ) + Number( mImporte );
					$("#mTotalFacturaV").val( mImporte );
					$("#mTotalFacturaV").formatCurrency();
					$("#cSerie").val("");
					$("#cFactura").val("");
					$("#mImporteFact").val("");
			});	
			

 			$("#grdValidaFacturas tbody").dblclick(function(event) {
					$(oTablevFact.fnSettings().aoData).each(function (){
   						$(this.nTr).removeClass('row_selected');
   						var aPos = oTablevFact.fnGetPosition( this.nTr );
    					// Get the data array for this row
    					var aData = oTablevFact.fnGetData( aPos[0] );

 					});
  					$(event.target.parentNode).addClass('row_selected');
     				var aPos = oTablevFact.fnGetPosition( event.target.parentNode );
     				if (aPos != null){
	     				var aData = oTablevFact.fnGetData( aPos );
	     				var mImporte = Number( quitaFmt( $("#mTotalFacturaV").val() ) ) - Number( quitaFmt( aData[ 2 ] ) );
						$("#mTotalFacturaV").val( mImporte );
						$("#mTotalFacturaV").formatCurrency();
						oTablevFact.fnDeleteRow( aPos);
     				}
 			});

			$("#grdValidaFacturas tbody").click(function(event) {
					$(oTablevFact.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
					var aPos = oTablevFact.fnGetPosition( this.nTr );
					// Get the data array for this row
					var aData = oTablevFact.fnGetData( aPos[0] );
				});
				$(event.target.parentNode).addClass('row_selected');
			});
			
			
			$("#destGasto").change(function(){
				if ( $(this).val() == "AL" ){
					document.getElementById("Imp_Bruto").removeAttribute("readonly",false);
				}else if ( $(this).val() != "" ){
					if( $("#cIdRFC").val() == "" ){
						$('#destGasto option[value=" "]').attr('selected','selected');
						alert("Favor de seleccionar un Beneficiario");						
						return false;
					}else{
						$("#mImporteBruto").attr("readonly", true);
						creaDiagloFacturas(true);	
					}
					if ( $.trim($(this).val())!=""){
						if($("#numPaso").val() == "1" )
							$("#EditaFacturas").css('visibility', 'visible');
					}else{
						$("#EditaFacturas").css('visibility', 'hidden');
					}
				}
			});
			
				$('#Descrip_Concepto').bind('copy paste', function (e) {       
					e.preventDefault();
		    	});

				$("input.AyudaSyC").subIniciaDlg();
				$("input.autoCompletaSyC").subIniciaAutoCompleta();
				
				$("#OIRAUSU").val( "<%=algo%>" );
				$("#cCentroContable").val( "<%=cCentroContable%>");			
				$("#id_caso").val(<%=request.getParameter("folio")%>);
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
				showOn: "button",
		//		dateFormat: "yy/mm/dd",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
				} );
	$(function() {
		$( "#hasta" ).datepicker({
				showOn: "button",
		//		dateFormat: "yy/mm/dd",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
				} );
	$(function() {
		$( "#fRecepcion" ).datepicker({
				showOn: "button",
		//		dateFormat: "yy/mm/dd",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
				} );
	
	$(function() {
		$( "#DCD_FECHA_FACTURA" ).datepicker({
				showOn: "button",
	//			dateFormat: "yy/mm/dd",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
				});
			///para las consultas crud
	$("#frmcontratoDeObra").ajaxForm({
				dataType:  "json",
				success: formSubmited
			});

	var suma = 0;
	var sredondea=0;
	
	
	init();

	$("#NumPagoAMF").change(function () {
		$("#nClaveAMF").val('');
		$("#numFolioAMF").val('');
		$("#PagoAMF").val('');
		//queryFormPost("tPagoAMFRead", {async: false });
		var vConcepto = $("#Descrip_Concepto").val();
		vConcepto = vConcepto + ' ' + $("#referenciaAMF").val(); 
		$("#Descrip_Concepto").val( vConcepto );
	});
	cssReadOnly();
	
	$('#montoDev').bind('copy paste', function (e) {       
		e.preventDefault();
   	});
	
});

function cssReadOnly(){
	$( "[readOnly]" ).each(function(){	
		$(this).addClass("notEditable");	
	});
}
	
	function habilitaGuardar(elPar){
		if (elPar == 0){
			$("#grpAutorizar").val("No");
		}
		else{
			$("#grpAutorizar").val("Si");
			$("#numPaso").val("6");
		}
	}

	function onSubmit(id_oper){
		//validaciones del boton guardar
  		var p = window.parent;
  		var valida_campos = true;
  		
  		var esEFO = validaEFO();
		if( esEFO ){
			alert("No se puede realizar el pago a un EFO. Solicite mas informacion con el administrador");
			return false;
		}
		
		if( $("#TIPO_OPERACION").val() == "" || $("#TIPO_OPERACION").val() == "-1"){
			alert("Debe seleccionar el tipo de operacion");
			$("#TIPO_OPERACION").focus();
			return false;
		}
			 
  		$("#facturasDiferentes").val("0");
		queryFormPost("rfcDiferentesRead", {async : false});
		if( $("#facturasDiferentes").val() != "0" ){
			alert( "El pago contiene facturas para otro proveedor diferente a [" + $("#cIdRFC").val()  + "] por lo que no puede continuar." );
			return false;
		}
  		
		if ($("#grpAutorizar").val()=="No"){//URVP.23062014 valida si el RadioButton de NO(autorizar) esta activo para no aplicar motor
			cmdBorrar();
			return;
		}
  		
  		if ($("#docAplicado").val() == "S") {
  			alert("Documento ya fue aplicado y se avanzará a modo de CONSULTA");
  		}else {
	  		
			try{ 
	
				//Guardado de los campos correspondientes a cada variable de caso
				p.gestion.setFolio( $("#FOLIO").val() );
				p.gestion.setOperador( $("#OPERADOR").val() );
				p.gestion.setFechaDocumento( $("#FECHA_CARGA").val() );
				p.gestion.setEjercicioFiscal( $("#cEjercicio").val() );
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
				//Control de estado de botones
			}
			catch (e) {
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
  		if (id_oper==1){
  			alert("Folio: "+ $("#caNoContrarrecibo").val());
  		}
  		//var valida_doctos_requeridos = true;
  		//validaciones de documentos requeridos
  		//return valida_doctos_requeridos;
  		//return confirm("Confirmar que quiere avanzar a la siguiente operación.");
  		return true;
  	}

    function onLoadPlantilla(){
    	carga();
    	if(<%=id_oper>1%>){
    		$("#EditaFacturas").css('visibility', 'hidden');
    		$("#ctaBancaria").attr('disabled', true);
    	}

		if(<%=id_oper==1%>){
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=false;
			
		}
		if(<%=id_oper==2%>){
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=true;
		}

		if(<%=id_oper==3%>){
			if( muestraDivImprimePoliza ){
				$("#EditaFirmas").css('visibility', 'visible');
			}
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=true;
		}
		
		queryFormPost({
			queryName:"facturasCapturadasPFRead",
			async: false,
			callback:function(){
			
				var totalFacturas = parseInt(  (  $("#nFacturasCapturadas").val() == "" ? "0" : $("#nFacturasCapturadas").val()  ), 10 );
				var totalOficios =  parseInt(  (  $("#nOficiosCapturadas").val()  == "" ? "0" : $("#nOficiosCapturadas").val()  ), 10 );
				
				if( ( totalFacturas == 0 && totalOficios == 0 ) || totalFacturas > 0 ){
					$("#uploadFacturasTR").css( "display",  "block" );
				}else{
					$("#uploadFacturasTR").css( "display",  "none" );
				}
				
			}
		});
		
		
		queryFormPost({
			queryName:"readImporteBrutoPagoPF", 
			async:false, 
			callback:function(){
				if( $("#mImporteBruto").val() == "" )
					$("#mImporteBruto").val("$0.00");
				if( $("#mImporteIVA").val() == "" )
					$("#mImporteIVA").val("$0.00");
				if( $("#otrosImpuestos").val() == "" )
					$("#otrosImpuestos").val("$0.00");
				$(".subtotall"). change();
			} 
		});

  	}
  	
	function BuscaPoliza(){
		var szWhere = " nfolioPagoFederalizado = " + $("#id_caso").val();
		var elMonto = "1";
		$("#laPoliza").val("");
		$("#docAplicado").val("");
		var szTabla = "TPAGOFEDDOCAPLICADOREAD";
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
		}
  		 if(id_oper==2){

  		 	return "CONSULTA_" + $("#cDocumento").val();
  		 }
  				
  	}
  	  	
  	function OperacionSiguiente(id_oper){

  		if(id_oper==1)
  		 	return "autoriza_factura";
  		if(id_oper==2){
		
  		 	return "consulta_factura";}
  	}

	function onPostDisplay(id_oper){
		if ($("#docAplicado").val() == "S") {
			parent.document.getElementById("pb_send").disabled = false;
			parent.document.getElementById("pb_send").click();
		}	
	}
	
	function onCancel(id_oper){
		//queryFormPost(	"TFactFederalEncabezadoUpdate", {async: false });
		return true;
	}
	

	function formSubmited() {
                //alert("Lo que sea!");
            }

	
	function contrareibo(){
		//$("#cIDContratoFed").attr('disabled', false);//URVP
		queryFormPost("SIG_FOLIO_CONTRAFEDERALUpdate", {async: false });
		queryFormPost("StatusAnticipoFACTFEDEREALUpdate", {async: false });		
		queryFormPost("tDocumentacionComprobatoriaDetCreate",{async: false });
		
		$("#divImprime").show();
		$("#Agregar1").attr('disabled', true);
		setTimeout("elRetardo()",1000);
		$("#DCD_TIPO_OPE").attr('disabled', true);//URVP - se deshabilita nuevamente
	}					
	function fnClickAddRowB(A, B,C) {
		$('#tretencion').dataTable().fnAddData( [
				A,
				B,
				C] );
	}
	function cmdBorrar(){
		$("#elcontra").val($("#caNoContrarrecibo").val());
		//queryFormPost(	"TFactOBRAEncabezadoDelete", {async: false });
		//queryFormPost(	"TFactOBRADetalleDelete", {async: false });
		//queryFormPost(	"tcontrarreciboRELGASTOSxFolioDelete", {async: false });
		//queryFormPost(	"TDOCUMENTACIONCOMPROBATORIADET_RELGASTOSFolioDelete", {async: false });		
		parent.document.getElementById("pb_send").disabled = true;
		parent.document.getElementById("pb_cancel").disabled = false;
		parent.document.getElementById("pb_cancel").click();
		parent.document.getElementById("pb_cancel").disabled = true;
		
		if ($("#docAplicado").val() != "S") 
			queryFormPost("borraFactRelacionPagoBorrado", {async: false }); //URVP.10092014 ELIMINA LAS FACTURAS CAPTURADAS EN EL PAGO A BORRAR PARA PODER REGISTRARLAS NUEVAMENTE EN OTRO PAGO
		
	}
	function folio(){ 
		queryFormPost("tFacturaContratoFEDERALRead","id_caso", {async: false });
		//queryFormPost("tPagoAMFRead", {async: false });
		
		setTimeout("reten()",1000);
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
			$("#divAutorizar").show();
			$("#numPaso").val('6');		
			//		parent.document.getElementById("pb_save").disabled=true;
			//	    parent.document.getElementById("pb_send").disabled=false;
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
					//parent.document.getElementById("pb_send").style.visibility='visible';
					//parent.document.getElementById("pb_send").disabled = false;
				}
			}
			
		}) ;  
		
	}
	function  compDocumentacion(){
		$("#DCD_FACTURA").val( $("#cNoFactura").val());
		$("#DCD_FECHA_FACTURA").val($("#fRecepcion").val());
		$("#DESCRIPCION20").val($("#nPorcIVAAplicable").val());
		$("#DCD_IMP_BRUTO").val($("#mImporteBruto").val());
		$("#DCD_IVA").val($("#mImporteIVA").val());
		$("#DCD_NETO").val($("#mImporteNeto").val());				
		$("#DCD_SANCION").val($("#mImporteSancion").val());				
		$("#DCD_DEVOL").val($("#mImporteDevolucion").val());
		$("#DCD_CONCEPTO").val( $("#Descrip_Concepto").val());
		$("#DCD_AMORT").val( $("#mAmortizacionAnticipo").val() );
		$("#DCD_RETENCION").val($("#mImporteRetencion").val() );
		$("#DCD_CONTRIBUCION").val( $("#mImporteFlete4").val() );
		$("#DCD_PENALIZACION").val( $("#mTesofe").val() );
		CalculaRetencionesG();
	}
	function claves(){  
		                         
		var szWhere = "";     
		szWhere = " tr.nFolioPAGOFederalizado ="+  $("#id_caso").val();
		var elMonto = "";     
		var este=0;          
		var szTabla = "CLAVES_FED";                                                                                         
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
		
		});   	
	}			  

	
	
	var TotalNeto=0;
    var	TotalRetenciones=0;
    
	function carga(){
		//queryFormPost("tRelacionGastClaveBeneficiarioRead","cIdRFC_RelacionGasto2",{async: false });
		leeMontosFacturas();
		$("#cRamo").val( "<%=cRamo%>" );
		$("#cUnidadResponsable").val( "<%=cUR%>" );
		$("#divAutorizar").hide();
		
		
		$("#divImprime").hide();
		$("#divImprimePoliza").hide();
		$("#divImprimeAnexo").hide();
						
		//$("#agrega2").attr('disabled', true);
		var con = $("#TO_TIPO_DOCTO").val(); 
		
		querySelectPost("CatalogoObraDGastoRead","destGasto", {async: false });
		$("#ID_DESTINO_GASTO").val( $("#destGasto").val() );
		$("#idDestinoGasto").val( $("#destGasto").val() );
		
		querySelectPost("CatalogoTipoRfcRead","tipoBenef", {async: false });
		querySelectPost("CAT_TIPO_OPERACIONRead", "TIPO_OPERACION",{async: false });

		querySelectPost({queryName: "CatalogoObraTConceptoReadPF", targetObjectId: "TIPO_CONCEPTO", async: false, callback:function(){
			queryFormPost("leeBorrarFFM", {async: false });
			if( $("#cCentroContable").val() != '10' || $("#borrarFFM").val() == '1' || $.trim($("#cIdRFC").val()) != "BMN930209927"){
				var opts = document.getElementById("TIPO_CONCEPTO").options;
				var indexFF = -1;
				
				for( cntFF = 0;cntFF < opts.length; cntFF++)
					if( "FFM" == opts[cntFF].value){
						indexFF = cntFF;
						break;
					}
					
				if( indexFF >= 0  )
					document.getElementById("TIPO_CONCEPTO").remove( indexFF );
			}else if ($.trim($("#cIdRFC").val()) == "BMN930209927" && $("#destGasto").val() == "CSPF" ){
				var opts = document.getElementById("TIPO_CONCEPTO").options;
				for( cntFF = 0;cntFF < opts.length; cntFF++ ){
					if( "FFM" != opts[cntFF].value && "" != $.trim(opts[cntFF].value) )
						document.getElementById("TIPO_CONCEPTO").remove( cntFF );
				}
			}
		}});

		querySelectPost("ImporteTotalFedRead","cIDContratoFed", {async: false });
		queryFormPost("cEjercicioRead", {async: false });
		
		queryFormPost("fRecepcion2Read", {async: false });
		queryFormPost("fRecepcionRead", {async: false });
		queryFormPost("TipoPolizaRead", {async: false });
		$("#aEjercicioFiscal").val($("#cEjercicio").val());
		
		if ($("#cEjercicio").val() >= '2013'){
            $("#fRecepcion").val( "<%=today%>" );
            if ( $("#fRecepcion").val().split("/")[2] != $("#cEjercicio").val() ) {
            	$("#fRecepcion").val( "31/12/" + $("#cEjercicio").val() );
            	$("#FECHA_CARGA").val( $("#fRecepcion").val() );
            }
            $("#fRecepcion").attr( "readonly","readonly" );
		}

		$("#divImprime").hide();
		folio();
		//concepto();
		//concepto2();
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
		   				   
		   //$("#Limpia").attr('disabled', true);
		   //$("#Borrar").attr('disabled', true);
		 }	
		if($("#numPaso").val()!="1"){
			querySelectPost("readCtaBancxPago", "ctaBancaria",{async: false });
			$("#ctaBancaria").attr('disabled', true);
		}
	}
	
	function concepto(){
		querySelectPost("CatalogoObraTMovimendoRead", "TIPO_MOVIMIENTO",{async: false });
		concepto2();
	}
	function concepto2(){
 		querySelectPost("CatalogoObraTMovimendo2Read",{async: false });
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
	
	function calculaIVAConsulta(){
		suma = Number($("input[id='mImporteBruto']").val());
		suma -= Number($("input[id='mImporteSancion']").val());
		suma += Number($("input[id='mImporteDevolucion']").val());		
		$("#subTotal_1").val(Math.round(Number(suma) *100 )/100);

		$("#mAmortizacionAnticipo").val( Number($("#mAmortizacionAnticipo").val())  /  (1 + Number ($("#nPorcIVAAplicable").val()/100 ) ));	
		suma -= $("#mAmortizacionAnticipo").val();	
		
		$("#subTotal_2").val(Math.round(Number(suma) *100 )/100);
		$("input[id='mImporteIVA']").val( $("#subTotal_2").val() * Number($("input[id='nPorcIVAAplicable']").val()/100)    );
		suma += Number($("input[id='mImporteIVA']").val());		
		$("#mImpEjercer").val(Math.round(Number(suma) *100 )/100);		
		suma -= Number($("input[id='mImporteRetencion']").val());
		suma -= Number($("input[id='mImportePenalizacion']").val());
		$("#mImporteNeto").val(Math.round(Number(suma) *100 )/100);					
	}
		
	function AnticipoTotal(){
			if ( parseFloat ($("#saldoAnticipo").val()) == parseFloat($("#mAmortizacionAcumulado").val())){
				$("#nPorcAmortizacion").val(0);
				calculaIVA();
			}
	}
	function Amortizacion(){
		 if ( Number ($("#nPorcAmortizacion").val()) < Number( $("#nPorcAmortizacion2").val() )){
			alert("El porcentaje minimo es: " +$("#nPorcAmortizacion2").val() );
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
	
	function calculaIva( cantidad2 ){
		epsinIva=cantidad2 / ( 1 + $("input[id='nPorcIVAAplicable']").val()/100);
		$("#mIVA").val(Number(cantidad2-epsinIva));
		if ( mImporteFlete23 == 1){
			mImporteFlete232 = ($("#mIVA").val()*2)/3;
			mImporteFlete232 = mImporteFlete232.toFixed(2);
			
			$("#mImporteFlete23").val( mImporteFlete232 );

		}
		mImporteNetoEP = Number($("#mImporteFlete23").val());
		mImporteNetoEPRes = parseFloat( cantidad2 ) - parseFloat( mImporteNetoEP );
		mImporteNetoEPRes = mImporteNetoEPRes.toFixed(2);
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
//		$("#mImporteFlete23").val($("#mImporteFlete232").val( ) );
		$("#mISRArrenda").val($("#mISRArrenda2").val());
		$("#mTesofe").val($("#mTesofe2").val());
		$("#mRetImpuestoCedular").val($("#mRetImpuestoCedular2").val( ) );

		cantidadrespaldo = cantidad;	
   		cantidad = cantidadsinIvaEP;
   		var mMontodelIVA;
   		mMontodelIVA = Number( cantidadrespaldo ) - Number( cantidadsinIvaEP );
   		mMontodeIva = mMontodelIVA.toFixed(2);
		$("#mIVA").val( mMontodelIVA );
		
		if ( mImporteFlete23 == 1){
			mImporteFlete232 = ($("#mIVA").val()*2)/3;
			mImporteFlete232 = mImporteFlete232.toFixed(2);
			
			$("#mImporteFlete23").val( mImporteFlete232 );

		}	
		//cantidadsinIvaEP23=cantidad -cantidadsinIvaEP;	
		
		//	$("#m23IVA").val( Math.round( ((cantidadsinIvaEP23 * 2) /3 ) *100 )/100 );
		//}	
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
		//$("#mObra5").toFixed(2);

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
		//mTesofe=parseFloat ( cantidad )* parseFloat( $("#mTesofe").val());
		//$("#mTesofe").val(Math.round( mTesofe *100 )/100 );

		var mAmortizaEP = ( Number( $("#mAmortizacionAnticipoMasIva").val() ) / Number( $("#mImpEjercer").val() ) ) * cantidadrespaldo;
		mAmortizaEP = mAmortizaEP.toFixed(2);
		$("#mAmortizacionAnticipoMasIvaEP").val( mAmortizaEP );

		var mPenaliza = ( Number( $("#mImportePenalizacion").val() ) / Number( $("#mImpEjercer").val() ) ) * cantidadrespaldo;
		mPenaliza = mPenaliza.toFixed(2);
		$("#mTesofe").val( mPenaliza );
		
		//$("#mTesofe").val( $("#mImportePenalizacion").val());
		mRetImpuestoCedular = parseFloat( cantidad ) * parseFloat( $("#mRetImpuestoCedular").val() );
		mRetImpuestoCedular = Number( mRetImpuestoCedular.toFixed(2) );
		mRetImpuestoCedular = mRetImpuestoCedular / ( 1 - ($("#nPorcAmortizacion").val()/100) );
		mRetImpuestoCedular = mRetImpuestoCedular.toFixed(2);
		$("#mRetImpuestoCedular").val( mRetImpuestoCedular );

		mImporteNetoEP = Number($("#m23IVA").val()) + Number($("#mCNIC").val()) + Number($("#mIMDT").val()) + Number($("#mObra5").val()) + Number ($("#mISRHonorarios").val())+
		Number($("#mImporteFlete4").val()) + Number( $("#mISRArrenda").val() ) + Number( $("#mRetImpuestoCedular").val()) + Number($("#mImporteFlete23").val());
		mImporteNetoEPRes = parseFloat(cantidadrespaldo) - parseFloat(mImporteNetoEP);
		mImporteNetoEPRes = mImporteNetoEPRes.toFixed(2);
		$("#mImporteNetoEP").val(  mImporteNetoEPRes );
	
	}
	
	    function creaDiagloFacturas(abrir){
			$( "#dialog-validaFact" ).dialog({
				autoOpen: false,
				height: 500,
				width: 860,
				modal: true,
				buttons: {
						"Aceptar": function(){
							bClicBtn = true;
							var aData = oTablevFact.fnGetData();
							$("#mImporteBruto").val( quitaFmt( $("#mTotalFacturaV").val() ) );
							$(".subtotall").change();
							$( this ).dialog( "close" );							
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
	}
	
	function creaDTFacturas(){
		oTablevFact = $('#grdValidaFacturas').dataTable(
		{
			"bProcessing": true,
			"bServerSide": true,
			"bDestroy": true,
			"bSort": true, 
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vfacturaspagos&qw=folioPago=<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%> AND tipoPago='PAGOFEDERALIZADO'" ,
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
	
</script>
<script type="text/javascript">

	function CalculaRetencionesG(){
		var szWhere = "";
		szWhere = " cidcontrato ='"+ $("#cIDContratoFed").val()+"' AND cIdEntidadContable = '" +  $("#cCentroContable").val() + "'";
		var este=0;
		var elMonto = "100";
		var retencion=0;
		var szTabla = "CALC_RETENCIONES_FACT_CONT_FED";
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
			var options = '';
			var porretencion=0;
			$("#DCD_ISR").val( 0 );
			for (var i = 0; i < j.length; i++) {
				if ($("#lHaySaldoAnticipo").val() == 0  || $("#mAmortizado").val() != '0.0000'){				
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
	


	function FiltroRetenciones(){
	
		cargaCtaBancariaRFC();
	
		$("#nPorcIVAAplicable").val(Number($("#nPorcIVAAplicable").val()).toFixed(2));
		$("#saldoCed").val(Number($("#saldoCed").val()).toFixed(2));
		$("#mTotal").val(Number($("#mTotal").val()).toFixed(2));

		//querySelectPost("PagosAMFRead", "NumPagoAMF", {async: false });  Solo por Conafor
		$("#cIdContrato").val( $("#cIDContratoFed").val() );
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
		$('#grdValidaFacturas').dataTable().fnClearTable();
		$("#mTotalFacturaV").val( "0.00" );
		$("#mImporteBruto").val( "0.00" );
		$(".subtotall").change();
		$("#destGasto").val("");
		// termina reinicia valores

		var szWhere = "";
		szWhere = " cidcontrato ='"+ $("#cIDContratoFed").val()+"' AND cIdEntidadContable = '" +  $("#cCentroContable").val() + "'";
		var elMonto = "100";
		var este=0;
		var retencion=0;
		elMonto = $("#mTotal").val();
		var szTabla = "CALC_RETENCIONES_FACT_CONT_FED";
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
			var options = '';
			var porretencion=0;
			for (var i = 0; i < j.length; i++) {
				fnClickAddRowB(j[i].Col0, j[i].Col1, j[i].Col2);
				porretencion=este*elMonto;
				//$("#mTotal2").val((Number (porretencion)+Number(elMonto)));
				if (j[i].Col0 == 0 ){
					//$("#DCD_IVADES").val( 1);
					//if ($("#lHaySaldoAnticipo").val() == 0 || $("#mAmortizado").val() != '0.0000'){///1 espara cuando hay anticipo
						mImporteFlete23 = 1;
						
					//}
				}else if (j[i].Col0 == 2 ){
					$("#DCD_MIL2").val( parseFloat (j[i].Col2));

					if ($("#lHaySaldoAnticipo").val() == 0  || $("#mAmortizado").val() != '0.0000'){
						CamInst=parseFloat (j[i].Col2);
						CamInst2=parseFloat (j[i].Col2);
					}
					DCD_MIL2res=parseFloat (j[i].Col2);
		
				}else if (j[i].Col0 == 3 ){
					$("#DCD_MIL5").val( parseFloat (j[i].Col2));
					if ($("#lHaySaldoAnticipo").val() == 0 || $("#mAmortizado").val() != '0.0000' ){	
						$("#mObra5").val( parseFloat (j[i].Col2) );
						$("#mObra52").val( parseFloat (j[i].Col2) );
					}
					
					DCD_MIL5res	= parseFloat (j[i].Col2	);	
		
				}else{
					retencion= parseFloat( retencion )+ parseFloat (j[i].Col2);
					if ($("#lHaySaldoAnticipo").val() == 0  || $("#mAmortizado").val() != '0.0000'){ ///1 espara cuando hay anticipo
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
			
			if (<%=id_oper%> ==1 ){
				Anticipo();
	
				if ($("#lHaySaldoAnticipo").val() == 1 && $("#mAmortizado").val() != '0.0000'){
					$("#mAmortizacionAcumulado").val(  $("#mAmortizado").val() );
				}else {
					$("#acumulado").val(0);//URVP.24062014 se asigna en 0 para que en el queryFormPost ponga el importe real en caso de tener documentos != 'C', ya que en caso contrario dejaba el importe anterior
					queryFormPost("tFACTFEDERALSEGUNDOPAGORead", {async: false });
				}
				$("#nPorcAmortizacion2").val($("#nPorcAmortizacion").val());
				mAmortizacionAcumuladoRes = $("#mAmortizacionAcumulado").val();
				mImporteSancionAcumulado=$("#mImporteSancionAcumulado").val();
				mImporteDevolucionAcumulado=$("#mImporteDevolucionAcumulado").val();
				comprometido=$("#acumulado").val();
				
				AnticipoTotal();
			}else{
				calculaIVAConsulta();
			}	
		});
		
		if (<%=id_oper%> == 1 ){
			var RFC = "";
			RFC = $.trim($("#cIdRFC").val());			
			if(RFC == $("#RFCFIBBanorte").val()){ 				
				var ffm = document.getElementById("destGasto").options;
				for( i = 0;i < ffm.length; i++ ){
					if( "CSPF" != ffm[i].value && "" != $.trim(ffm[i].value) ){
						document.getElementById("destGasto").remove( i );
					}
				}				
			}
		}	
	}
	
	var ivaanticipo=0;
	
	function Anticipo(){
		queryFormPost("tFactfEDERALAnticipoRead", {async: false });
		queryFormPost("tFACTFEDERALSaldocompromisoRead", {async: false });
		if ($("#lHaySaldoAnticipo").val() == "1" && $("#mAmortizado").val() == '0.0000'){
			//ivaanticipo=Number($("#mImporteBruto").val()) + Number( $("#mImporteBruto").val() * ($("input[id='nPorcIVAAplicable']").val()/100)  );
			//ivaanticipo= Math.round(ivaanticipo *100 )/100;
			//$("#saldoAnticipo").val(ivaanticipo);
			//$("#saldoAnticipo").val($("#mImporteBruto").val());
			$("#mImporteBruto").attr("readonly",true);
			$("#nPorcAmortizacion").val(0);
			setTimeout("calculaIVA();",400);
		}else{
			calculaIVA();			
		}
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
				+"'", 			//+ "&nombre="   + ""
				//+ "&cargo="    + ""
				//+ "&area="     + "",
			"popacuse",
			"scrollbars=1, resizable=yes, width=1024, height=768");
		setTimeout('anexo("' + elFormato + '")', 5000);	
	}
	
	function anexo( pfmt ){
		$("#totFacturas").val(0);
		queryFormPost("totFacturasPago", {async: false });
		
		if (($("#cllave").val()>7 || $("#totFacturas").val()>7) && pfmt == "PolizaPagoN" ){ //se cambia a EP's mayor a 7 o mas de 7 facturas					
			window.open(
				"../admin/SeguridadCatalogos?"
					+ "catalogo=ANEXO"
					+ "&accion=run"
					+ "&rn=AnexoFed.jasper"
					+ "&swhere=  and caNoContrarrecibo ='"+ $("#caNoContrarrecibo").val()+"' " ,  
				"Anexo",
				"scrollbars=1, resizable=yes, width=1024, height=768");
		}
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
	    $("#" + fld.id).formatCurrency();
	    var valcol = fld.value ;
		valcol = valcol.replace("$", "");
		valcol = valcol.replace(/,/g, "");
		$("#" + fld.id).val( valcol );
	}
	

	function compare_dates(hasta, hasta2) { 
		var xMonth=hasta.substring(3, 5);  
		var xDay=hasta.substring(0, 2);  
		var xYear=hasta.substring(6,10);  
		var yMonth=hasta2.substring(3, 5);  
		var yDay=hasta2.substring(0, 2);  
		var yYear=hasta2.substring(6,10);  
        if (xYear> yYear) {  
            return(true);  
        } else{  
          if (xYear == yYear) {   
            if (xMonth> yMonth){  
                return(true) ; 
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
		if (compare_dates($("#desde").val(),$("#hasta").val())){
			alert("La fecha Hasta debe ser posterior a la Desde");
			$("#hasta").val($("#desde").val());
		}
		if (compare_dates( $("#hasta").val() , $("#hasta2").val() ) ){  
			alert("La Fecha tiene que ser menor o igual al Dia de Hoy");
			$("#hasta").val("<%=today%>"); 
		}
		
	}
	

	function cmdGuardar(){
		if ($("#numPaso").val() == '1'){
		
			if ($.trim($("#ctaBancaria option:selected").text())==""){											   
				alert("El beneficiario " + $("#cIdRFC").val() + " no tiene dada de alta alguna cuenta bancaria, por lo que no se puede proceder con el pago.");
				return;
			} 
			if ($.trim($("#destGasto").val())==""){
				alert("Favor de seleccionar el tipo Destino del Gasto.");
				return;
			}
			//URVP.29092014 Se validan importes de sancion, devolucion y penalizacion
			var bruto = 0.00;
			var sancion = 0.00;
			var devolucion = 0.00;
			var penalizacion = 0.00;
			var subtot = 0.00;
			bruto = Number($("#mImporteBruto").val());
			sancion = Number($("#mImporteSancion").val());
			devolucion = Number($("#mImporteDevolucion").val());
			penalizacion = Number($("#mImportePenalizacion").val());
			subtot = Number($("#subTotal_2").val());
			if (bruto<0 || bruto==0){
				alert("El importe no puede ser menor o igual a 0.");
				return;
			}
			if (sancion>bruto){
				alert("La Sancion no puede ser mayor que el Importe Bruto");
				return;
			}
			else if (devolucion>sancion){
				alert("La Devolucion no puede ser mayor que la Sancion");
				return;
			}
			if (penalizacion>subtot){
				alert("La Penalizacion no puede ser mayor que el Subtotal");
				return;
			}
			
			var aData = oTablevFact.fnGetData();
			
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
				
				querySelectPost({queryName: "CatalogoObraTConceptoReadPF", targetObjectId: "TIPO_CONCEPTO", async: false, callback:function(){
					queryFormPost("leeBorrarFFM", {async: false });
					if( $("#cCentroContable").val() != '10' || $("#borrarFFM").val() == '1' || $.trim($("#cIdRFC").val()) != "BMN930209927"){
						var opts = document.getElementById("TIPO_CONCEPTO").options;
						var indexFF = -1;
						
						for( cntFF = 0;cntFF < opts.length; cntFF++)
							if( "FFM" == opts[cntFF].value){
								indexFF = cntFF;
								break;
							}
							
						if( indexFF >= 0  )
							document.getElementById("TIPO_CONCEPTO").remove( indexFF );
					}else if ($.trim($("#cIdRFC").val()) == "BMN930209927" && $("#destGasto").val() == "CSPF" ){
						var opts = document.getElementById("TIPO_CONCEPTO").options;
						for( cntFF = 0;cntFF < opts.length; cntFF++ ){
							if( "FFM" != opts[cntFF].value && "" != $.trim(opts[cntFF].value) )
								document.getElementById("TIPO_CONCEPTO").remove( cntFF );
						}
					}
				}});
				
				
				concepto();
				queryFormPost("tContratoFEDERALFactCreate",{async: false });
				queryFormPost("tPagoFEDERALEncabezadoUpdate",{async: false });
				parent.document.getElementById("pb_save").disabled=true;
	
				$("#fecha_Pago").attr("readonly", true); //URVP
				$("#desde").attr("readonly", true); //URVP
				$("#hasta").attr("readonly", true); //URVP
				$("#cNoEstimacion").attr("readonly", true); //URVP
				$("#cNoFactura").attr("readonly", true); //URVP
				$("#mImporteSancion").attr("readonly", true); //URVP
				$("#mImporteDevolucion").attr("readonly", true); //URVP
				$("#mAmortizacionAnticipo").attr("readonly", true); //URVP
				$("#mImportePenalizacion").attr("readonly", true); //URVP
				$("#nPorcAmortizacion").attr("readonly", true); //URVP
				$("#mAmortizacionAcumulado").attr("readonly", true); //URVP
				$("#Descrip_Concepto").attr("readonly", true); //URVP
				$("#destGasto").attr("disabled", true); //URVP
				$("#TIPO_OPERACION").attr("disabled", true); //URVP
				$("#mImporteBruto").attr("readonly", true);
				
				$("#DCD_FACTURA").val( $("#cNoFactura").val());
				$("#DCD_FECHA_FACTURA").val($("#fRecepcion").val());
				$("#DESCRIPCION20").val($("#nPorcIVAAplicable").val());
				$("#DCD_IMP_BRUTO").val($("#mImporteBruto").val());
				$("#DCD_IVA").val($("#mImporteIVA").val());
				$("#DCD_NETO").val($("#mImporteNeto").val());				
				$("#DCD_SANCION").val($("#mImporteSancion").val());				
				$("#DCD_DEVOL").val($("#mImporteDevolucion").val());
				$("#DCD_CONCEPTO").val( $("#Descrip_Concepto").val());
				$("#DCD_AMORT").val( $("#mAmortizacionAnticipo").val() );
				$("#DCD_RETENCION").val($("#mImporteRetencion").val() );
				$("#DCD_CONTRIBUCION").val( $("#mImporteFlete4").val() );
				$("#DCD_PENALIZACION").val( $("#mTesofe").val() );
				cssReadOnly();
				$("#EditaFacturas").css('visibility', 'hidden');
				$("#ctaBancaria").attr('disabled', true);
				alert("Carátula guardada!");	
				
				//$(".paso01").attr('disabled', true);//URVP - se deshabilita para hacer cssreadonly por individual
				//$(".subtotall").attr('disabled', true);//URVP - se deshabilita para hacer cssreadonly por individual
				//$(".AyudaSyC").attr('disabled', true);//URVP - se deshabilita para hacer cssreadonly por individual
				$(".pasoDos").show();//URVP - se deshabilita para hacer cssreadonly por individual
				$(".pasoTres").hide();
				$("#numPaso").val('2');
				$("#guardar").attr("disabled", true);
				$("#L03").click();
			}else{
				alert('Debe ingresar los siguientes datos: '+ hayError);
			}
		}else if($("#numPaso").val() == "6" && <%=id_oper==1%>){
			
			parent.document.getElementById("pb_save").disabled=true;
			parent.document.getElementById("pb_send").disabled=false;
			parent.document.getElementById("pb_send").click();
		}	
	}
	
 	function Grid(){
		window.open("AyudasEPOBRA.jsp", 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
	 	return false;
	}
	
	function Limpia1() {
		if(<%=id_oper==1%>){
			document.getElementById("TIPO_CONCEPTO").removeAttribute("disabled",false);
			$("#saldoCompM").val(0);
			queryFormPost(	"TFactFEDERALDetalleDelete", {async: false });
			$("#ep").val("");
			$("#montoDev").val("");
			$('#grdCompromisos').dataTable().fnClearTable();
			$('#grdRetClave').dataTable().fnClearTable();
		}
	}
	
	function cargaCtaBancariaRFC(){
		$("#campoRFC").val($.trim($("#cIdRFC").val()));
		querySelectPost("cargaCtaBancariasRFC", "ctaBancaria",{async: false });
		queryFormPost("esProveedorExtranjero", {async:false});
	}
	
	function borraOfiDelegatorio(){
				
		if($("#firmanteOficioExiste").val() == "Existe"){
			queryFormPost({	queryName : "borrarOficioDelegatorio", async : false, callback : function(){ 															
								msn = "Oficios borrados del pago " + $("#cTipoPago").val() + " no. " + $("#nFolioPago").val();
								} 
						});
			$("#cFolioOficioUpdate").val("");
			$("#dFechaOficioUpdate").val("");
			$("#cNombreTitularUpdate").val("");
			$("#cApellidoPaternoTitularUpdate").val("");
			$("#cApellidoMaternoTitularUpdate").val("");
			$("#cPuestoTitularUpdate").val("");
		}
		
		if($("#firmanteOficioVoBoExiste").val() == "Existe"){
			queryFormPost({	queryName : "borrarOficioDelegatorioVoBo", async : false, callback : function(){ 															
								msn = "Oficios borrados del pago " + $("#cTipoPago").val() + " no. " + $("#nFolioPago").val();
								} 
						});
			$("#cFolioOficioVoBoUpdate").val("");
			$("#dFechaOficioVoBoUpdate").val("");
			$("#cNombreTitularVoBoUpdate").val("");
			$("#cApellidoPaternoTitularVoBoUpdate").val("");
			$("#cApellidoMaternoTitularVoBoUpdate").val("");
			$("#cPuestoTitularVoBoUpdate").val("");
		}											
		alert(msn);	
	}
	
</script>
  </head>
	<body id="dt_example">
		<div id="container" class="container SyCData">
			<form id="frmcontratoDeObra">
			<input type="hidden" id="esEFO"       name="esEFO"       value="N"/>
			<input type="hidden" id="rfcValidar"  name="rfcValidar"  value=""/>		
			<input type="hidden" id="TipoAutorizacion" name="TipoAutorizacion" value=""/>
			<input type="hidden" id="cSubPrograma" name="cSubPrograma"  value=""/>
			<input type="hidden" id="nIDPrograma" name="nIDPrograma"  value=""/>
			<input type="hidden" id="esPagoFID" name="esPagoFID"  value="0"/>
			<input type="hidden" id="cPrograma" name="cPrograma"  value="0"/>
			<input type="hidden" id="facturasDiferentes" name="facturasDiferentes"  value="0"/>
			<input name="esPagoConfactura" id="esPagoConfactura" type="hidden" value="0"/>				
			<input name="nFacturasCapturadas" id="nFacturasCapturadas" type="hidden" value="0" />
			<input name="nOficiosCapturadas" id="nOficiosCapturadas" type="hidden" value="0" />
			<input name="tipo_pago" id="tipo_pago" type="hidden" value="<%=c.getTipoCaso().getGavetaAsociada()%>" />
			<input name="nFolioPago" id="nFolioPago" type="hidden" value="<%=c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>"/>
			<input name="cTipoPago" id="cTipoPago" type="hidden" value="PAGOFEDERALIZADO"/>
			<input type="hidden" name="cFolioGestion" id="cFolioGestion" value="<%=c.getFolio()%>"/>		
			<input name="cEstatusPago" id="cEstatusPago" type="hidden" value=""/>
			<input name="tipoTramite" id="tipoTramite" type="hidden" value="<%=idTipoCaso %>"/>
			<input name="campoRFC" id="campoRFC" type="hidden"/>

			<!-- PARA LKS EP -->
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
			<input type="hidden" value=""  id="lHaySaldoAnticipo" name="lHaySaldoAnticipo"  />	
			<input type="hidden" value="0" id="TotalNeto"  name="TotalNeto" />
			<input type="hidden" value="0" id="TotalNetoN"  name="TotalNetoN" />
			<input type="hidden" id="laPoliza" />
			<input type="hidden" id="docAplicado" value=""/>
			<input type="hidden" value="" id="borrarFFM"  name="borrarFFM" />
	
			<input type="hidden" value="0" id="TotalRetenciones"  name="TotalRetenciones" />
			<input type="hidden" value="0" id="campoRetencion"  name="campoRetencion" />
			<input type="hidden" value="0" id="mIVA"name="mIVA"/>
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
			<input type="hidden" name="cIdContrato" id="cIdContrato" value=""/>
			<input type="hidden" id="tConcepto2" name="tConcepto2"/>
			<input type="hidden" id="nIdConcepto" name="nIdConcepto" maxlength="10" size="10"/>
			<input type="hidden" maxlength="10" size="10" id="ID_TIPO_MOVIMIENTO" name="ID_TIPO_MOVIMIENTO"/>
			<input type="hidden" maxlength="10" size="10" id="ID_DESTINO_GASTO" name="ID_DESTINO_GASTO"/>
			<input type="hidden" maxlength="10" size="10" id="idDestinoGasto" name="idDestinoGasto"/>										 
			<input type="hidden" id="codSIAFF" name="codSIAFF" />
			<input type="hidden" name="saldoCompA" id="saldoCompA" maxlength="30" style="background-color: #C0C0C0" />
			<input type="hidden" name="saldoPorEjercer" id="saldoPorEjercer" size="10" maxlength="10" style="background-color: #C0C0C0" />
			<input name="altaAlmacen" type="hidden" class="paso04" id="altaAlmacen" style="text-align: right;"
								onKeyPress="valFmt(this,9)" value=" " size="15" maxlength="15"/>
			<input type="hidden" value="FEDERALIZADO" id="TO_TIPO_DOCTO"name="TO_TIPO_DOCTO"/>
			<input name="cDocumento" type="hidden" id="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>"/>
			<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value=""/>
			<input name="elcontra" type="hidden" id="elcontra"/>
			<input type="hidden" value="" id="rowsAffected" name="rowsAffected"/>
			<input type="hidden" name="cFolioContratoObra" 		id="cFolioContratoObra" class="paso01" size="40" maxlength="40" />
			<input type="hidden" name="tipoRet" id="tipoRet" value="" />
			<input type="hidden" name="resImpRet" id="resImpRet" value="" />
			<input name="nMes" type="hidden" id="nMes" value="2" size="20"/>
			<input name="cIdCuentaContable" type="hidden" id="cIdCuentaContable"	value="" size="20"/>
			<input name="numPaso" type="hidden" id="numPaso" value="1" size="5"/>
			<input name="TOTALSUBCUENTA" type="hidden" id="TOTALSUBCUENTA"/>
			<input type="hidden" value="" id="cEjercicio" name="cEjercicio"/>				
			<input type="hidden" value="" id="tiene" name="tiene"/>
			<input type="hidden" id="cRamo" name="cRamo"/>
			<input type="hidden" id="cUnidadResponsable"name="cUnidadResponsable"/>
			<input name="OIRAUSU" type="hidden" id="OIRAUSU" />
			<input type="hidden" maxlength="15" size="15"	name="cIdRFC_RelacionGasto2" id="cIdRFC_RelacionGasto2" />
			<input type="hidden" maxlength="15" size="15" name="sumareten"	id="sumareten" />
			<input type="hidden" maxlength="15" size="15" name="doster"	id="doster" />
			<input type="hidden" maxlength="15" size="15" 	name="cIdTipoDocumento" id="cIdTipoDocumento" value="7" />
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
			<input type="hidden" id="campo" name="campo" value=""/>
			<input type="hidden" id="tablaEnc" name="tablaEnc" value=""/>
			<input type="hidden" id="campoCondicion" name="campoCondicion" value=""/> 
			<input type="hidden" id="tablaDet" name="tablaDet" value=""/> 
			<input type="hidden" id="tipoAplicar" name="tipoAplicar" value=""/>
			<input name="cartera" type="hidden" id="cartera" value=""/>
			<input name="capitulo" type="hidden" id="capitulo" value=""/>
			<input name="UE" type="hidden" id="UE" value=""/>
			<input name="EP" type="hidden" id="EP" value=""/>
			<input name="mImporteCartera" type="hidden" id="mImporteCartera" value=""/>
			<input type="hidden" id="cValidaFactura" name="cValidaFactura" value=""/>
			<input type="hidden" id="cExiste" name="cExiste" value="0"/>
			<input type="hidden" id="mImporteBrutoF" name="mImporteBrutoF" value="0"/>
			<input type="hidden" id="cOBGT" name="cOBGT" size="14"/>             
             <input type="hidden" id="firmanteExiste" name="firmanteExiste" size="14"/>
                 
             <input type="hidden" id="cNombreVo" name="cNombreVo"/>
             <input type="hidden" id="cPaternoVo" name="cPaternoVo" size=40 >
             <input type="hidden" id="cMaternoVo" name="cMaternoVo" size=40>
             <input type="hidden" id="cPuestoVo" name="cPuestoVo" size=40>
             <input type="hidden" id="firmanteVoBo" name="firmanteVoBo" size=40>
             <input type="hidden" id="tipoFirmante" name="tipoFirmante" size=15 value="PAGO_VOBO">
             
             <input type="hidden" id="cNombreA" name="cNombreA" size=40 >
             <input type="hidden" id="cPaternoA" name="cPaternoA" size=40 >
             <input type="hidden" id="cMaternoA" name="cMaternoA" size=40>
             <input type="hidden" id="cPuestoA" name="cPuestoA" size=40>
             <input type="hidden" id="firmanteAut" name="firmanteAut" size=40>
             <input type="hidden" id="tipoFirmanteA" name="tipoFirmanteA" size=15 value="PAGO_AUT">
             <input name="importeTotalEncabezado" id="importeTotalEncabezado" type="hidden" value="0">
             <input name="importeTotalDetalle" id="importeTotalDetalle" type="hidden" value="0">
             <input type="hidden" id="esExtranjero" name="esExtranjero" value="">
             
             <!-- hidden para la captura de oficio delegatorio -->
             <input type="hidden" name="cFolioOficioAux" id="cFolioOficioAux" value="">
             <input type="hidden" name="dFechaOficioAux" id="dFechaOficioAux" value="">
             <input type="hidden" name="cNombreTitularAux" id="cNombreTitularAux" value="">
             <input type="hidden" name="cApellidoPaternoTitularAux" id="cApellidoPaternoTitularAux" value="">
             <input type="hidden" name="cApellidoMaternoTitularAux" id="cApellidoMaternoTitularAux" value="">
             <input type="hidden" name="cPuestoTitularAux" id="cPuestoTitularAux" value="">
             <input type="hidden" name="firmanteOficioExiste" id="firmanteOficioExiste" value="">
             
             <!-- hidden para validacion de beneficiario FIB BANORTE -->
             <input type="hidden" id="RFCFIBBanorte" name="RFCFIBBanorte" value="BMN930209927" />
             
             <!-- hidden para la captura de los datos de quien elaboro -->
             <input type="hidden" id="cNombreE" name="cNombreE" size=40>
             <input type="hidden" id="cPaternoE" name="cPaternoE" size=40>
             <input type="hidden" id="cMaternoE" name="cMaternoE" size=40>
             <input type="hidden" id="cPuestoE" name="cPuestoE" size=40>
             <input type="hidden" id="firmanteEla" name="firmanteEla" size=40>
             
             <!-- hidden para la captura de oficio delegatorio VoBo-->
             <input type="hidden" name="cFolioOficioVoBoAux" id="cFolioOficioVoBoAux" value="">
             <input type="hidden" name="dFechaOficioVoBoAux" id="dFechaOficioVoBoAux" value="">
             <input type="hidden" name="cNombreTitularVoBoAux" id="cNombreTitularVoBoAux" value="">
             <input type="hidden" name="cApellidoPaternoTitularVoBoAux" id="cApellidoPaternoTitularVoBoAux" value="">
             <input type="hidden" name="cApellidoMaternoTitularVoBoAux" id="cApellidoMaternoTitularVoBoAux" value="">
             <input type="hidden" name="cPuestoTitularVoBoAux" id="cPuestoTitularVoBoAux" value="">
             <input type="hidden" name="firmanteOficioVoBoExiste" id="firmanteOficioVoBoExiste" value="">
             
			<input type="hidden" name="cxpPrefijo" id="cxpPrefijo" value="<%=cxpPrefijo%>">
				
				<!--   -->

			  <div class="dvGeneral">
					<h1>
						Pago de Subsidios - Recepci&oacute;n De Documentos
					</h1>

					<table id="ContratoDeObra" border="0" cellspacing="0"
						cellpadding="0">
						<tr>
							<td>
								<div id="divAutorizar">
									<label id="lbAutorizar">
										Autorizar
									</label>
									<input type="radio" id="grpAutorizar" name="grpAutorizar"
										onClick="habilitaGuardar(1)"  checked="checked" value="Si">
									Si
									<input type="radio" id="grpAutorizar" name="grpAutorizar"
										onClick="habilitaGuardar(0)" value="No">
									No &nbsp;&nbsp;&nbsp;&nbsp;
								</div>
								<div id="divImprimePoliza">
									<img src="imagenes/Imprimir.png" width="25" height="21"
										onClick="cmdImprimir('PolizaPago');">
									Poliza
								</div>
						<div id="divImprimeAnexo">
							<img src="imagenes/Imprimir.png" width="25" height="21"
								onClick="anexo('PolizaPagoN');">
							Anexo
						</div>
						<span id="EditaFirmas" style="visibility:hidden"><a href="#" onclick="updateFirmantes();">Firmas*</a></span>

							</td>
							<td>
								ContraRecibo
								<input type="text" id="caNoContrarrecibo"
									name="caNoContrarrecibo" readonly="readonly" value="0"
									size="20" maxlength="20">
						</tr>
						<tr>
							<td nowrap>
								Anexo/Convenio
								<input type="text" name="cIDContratoFed" id="cIDContratoFed" onChange="FiltroRetenciones();" class="AyudaSyC " size="40" maxlength="40" readonly/>
								<input type="hidden" name="cIDContratoObra2" id="cIDContratoObra2" size="40" maxlength="40" />
							</td>
						  <td nowrap align="right">
								Folio
								<input name="id_caso" type="text" id="id_caso" size="10" maxlength="40" readonly>
								<input type="button" value="Borrar" name="cancelar"	id="cancelar" onClick="cmdBorrar()" class="btnInterfaceBG"/>							
							</td>
						</tr>
						<tr>
							<td nowrap>
								RFC
								<input type="text" name="cIdRFC" id="cIdRFC" readonly="readonly"
									size="15" maxlength="15" class="paso01" />
								<input type="text" name="cobjetocontrato" id="cobjetocontrato"
									readonly="readonly" size="50" maxlength="50" class="paso01" />
							</td>
							<td align="right">&nbsp; </td>
						</tr>
						
						<tr>
							<td colspan="4">Cuenta Bancaria: <select id="ctaBancaria" name="ctaBancaria"></select></td>
						</tr>

						<tr>
							<td nowrap>
								Fecha De Captura
								<input type="text" maxlength="10" size="10"   class="paso01"
									id="fRecepcion" name="fRecepcion" readonly>
								<input type="hidden" maxlength="10" size="10" 
									id="fRecepcion2" name="fRecepcion2">									
								<!-- Fecha De Pago -->
								<input type="hidden" maxlength="10" size="10" class="paso01"
									id="fecha_Pago" name="fecha_Pago" value="<%=today%>">
							</td>
							<td nowrap>
								Tipo de Operaci&oacute;n
								<select class="paso01" id="TIPO_OPERACION" name="TIPO_OPERACION">
								</select>
							</td>
						</tr>

						<tr>
							<td nowrap>
								Destino Del Gasto
								<select id="destGasto" name="destGasto" class="paso01"
									style="width: 300px;">
								</select>
								<span id="EditaFacturas" style="visibility:hidden"><a href="#" onclick="creaDiagloFacturas(true); return false;"> Facturas </a></span>
								<input type="hidden" maxlength="10" size="10"
									id="ID_DESTINO_GASTO2" name="ID_DESTINO_GASTO2">
							</td>
							<td nowrap>&nbsp;
								
							</td>
							
							</tr>

						<tr>
							<td nowrap>
								Periodo De:
								<input type="text" maxlength="10" size="10" class="paso01" 	id="desde" name="desde" value="<%=today%>">
								Hasta:
								<input type="text" maxlength="10" size="10" class="paso01" id="hasta" name="hasta" onChange="fecha()" value="<%=today%>">
								<input type="hidden" maxlength="10" size="10" 
									id="hasta2" name="hasta2">									
							</td>
							<td nowrap>
								<!--  No. Estimaci&oacute;n -->
								<input type="hidden" name="cNoEstimacion" id="cNoEstimacion"
									value="1" class="paso01" size="14" maxlength="14"/>
								<input type="hidden" maxlength="10" size="10" id="ID_TIPO_FONDO"
									name="ID_TIPO_FONDO">
							</td>
							
							
						</tr>
					</table>
				</div>

				<div id="demo2">
				</div>

				<div class="dvFuente">
				</div>
				<div id="multitabs" style="width: 860px">
					<div id="tabs" style="width: 100%">
						<ul>
							<li>
								<a id="L01" href="#tabs-1">Detalle de operaci&oacute;n</a>
							</li>
							<li>
								<a id="L02" href="#tabs-2">Retenciones</a>
							</li>
							<li>
								<a id="L03" href="#tabs-3" class="pasoDos">Devengado</a>
							</li>
							<li>
								<a id="L04" href="#tabs-4" class="pasoTres">Documentaci&oacute;n</a>
							</li>

						</ul>
						<div id="tabs-1">
							<table width="96%" id="operacion" align="center" border="0"	cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<table width="80%" border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td width="40%" style="visibility:hidden">#Pago AMF</td>
												<td width="55%">
													<select id="NumPagoAMF" name="NumPagoAMF" style="visibility:hidden">
														<option value="***" selected>- Selecciona AMF  -</option>
													</select>
												</td>
												<td width="5%">&nbsp;</td>
											</tr>
											<tr>
												<td width="40%" style="visibility:hidden">Clave AMF</td>
												<td width="40%">
													<input type="text" name="nClaveAMF" id="nClaveAMF" readonly="readonly"
														size="15" maxlength="15" style="visibility:hidden"/>
												</td>												
												<td width="5%">&nbsp;</td>
											</tr>
											<tr>
												<td width="55%" style="visibility:hidden">Folio AMF</td>
												<td width="55%">
													<input type="text" name="numFolioAMF" id="numFolioAMF" readonly="readonly"
														size="15" maxlength="15" style="visibility:hidden"/>
												</td>
												<td width="5%">&nbsp;</td>
											</tr>
											<tr>
												<td width="40%">&nbsp;</td>
												<td width="55%">
													<input type="text" name="PagoAMF" id="PagoAMF" readonly="readonly"
														size="15" maxlength="15" style="visibility:hidden"/>												
												</td>
												<td width="5%">&nbsp;</td>
											</tr>
											<tr>
												<td align="right" nowrap width="40%">
													<div align="left">
														No. de Recibo
													</div>
												</td>
												<td width="55%">
													<input type="text" name="cNoFactura" style="text-align: right" class="paso01" id="cNoFactura"
														size="10" maxlength="10" value="<%=request.getParameter("folio")%>"/>
												</td>
												<td>
												</td>
											</tr>
											<tr>
												<td align="right" nowrap width="5%">
													<div align="left">
														Importe Bruto
													</div>
												</td>
												<td>
													<input name="mImporteBruto" onkeypress="return validar2(event)" type="text"
														style="text-align: right" class="subtotall" id="mImporteBruto" value="0" maxlength="10" size="10" readonly/>
												</td>
												<td>
												</td>
											</tr>
											<tr>
												<td align="right" nowrap width="40%">
													<div align="left">
														Monto Sanciones
													</div>
												</td>
												<td width="55%">
													<input name="mImporteSancion" onkeypress="return validar2(event)" type="text" onblur="cambiafrmt2(this);"
														class="subtotall"   value="0" id="mImporteSancion" style="text-align: right" maxlength="10" size="10"/>	
												</td>
												<td align="left" width="5%">
													<b>-&nbsp;</b>
												</td>
											</tr>
											<tr>
												<td align="right" nowrap width="40%">
													<div align="left">
														Monto Devoluciones
													</div>
												</td>
												<td width="55%">
													<input name="mImporteDevolucion" type="text" value="0" onkeypress="return validar2(event)" maxlength="10"
														onblur="cambiafrmt2(this);" size="10" class="subtotall" id="mImporteDevolucion" style="text-align: right" />
												</td>
												<td align="left" width="5%">
													<b>+&nbsp;</b>
												</td>
											</tr>
											<tr>
												<td align="right" nowrap width="40%">
													<div align="left">
														Subtotal
													</div>
												</td>
											  	<td width="55%">
													<input name="subTotal_1" value="0" type="text" onkeypress="return validar2(event)" class="subtotall"
														id="subTotal_1" size="10" readonly style="text-align: right"/>
												</td>
												<td align="left" width="5%">
													<b>=&nbsp;</b>
												</td>
											</tr>
										</table>
									</td>

									<td>
										<table width="80%" align="center" border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td width="40%"></td>
												<td width="55%"></td>
												<td width="5%"></td>
											</tr>
											<tr>
												<td align="left" nowrap width="40%">
													<div align="left">
														<!-- Amortizaci&oacute;n Anticipo  URVP.10102014 SE OCULTAN CAMPOS DE AMORTIZACION Y ANTICIPO--> 													
													</div>												
												</td>
												<td width="55%">
													<input name="mAmortizacionAnticipo" style="text-align: right" type="hidden" class="subtotall" onblur="cambiafrmt2(this);" id="mAmortizacionAnticipo" maxlength="10" size="10" value="0"/>												
												</td>
												<td align="left" width="5%">
													<b>-&nbsp;</b>												
												</td>
											</tr>
											<tr>
												<td align="right" nowrap width="40%">
													<div align="left">Subtotal Obra</div>												
												</td>
												<td width="55%">
													<input name="subTotal_2" type="text" value="0" class="subtotall" style="text-align: right" id="subTotal_2" size="10" readonly/>
												</td>
												<td align="left" width="5%">
													<b>=&nbsp;</b>												
												</td>
											</tr>
											<tr>
												<td align="right" nowrap width="40%">
													<div align="left">Monto IVA</div>												
												</td>
												<td width="55%">
													<input name="mImporteIVA" type="text" class="subtotall" id="mImporteIVA" style="text-align: right" maxlength="10" size="10" value="0" readonly/>
													<input name="mImporteMasIva" type="hidden" class="subtotall" id="mImporteMasIva" style="text-align: right" maxlength="10" size="10" value="0"/>												
												</td>
												<td align="left" width="5%">
													<b>+&nbsp;</b>												
												</td>
											</tr>
											<tr>
												<td align="right" nowrap width="40%">
													<div align="left">Otros Impuestos</div>												
												</td>
												<td width="55%">
													<input name="otrosImpuestos" type="text" class="subtotall" id="otrosImpuestos" style="text-align: right" maxlength="10" size="10" value="0" readonly/>
												</td>
												<td align="left" width="5%">
													<b>+&nbsp;</b>												
												</td>
											</tr>
											<tr>
												<td align="right" nowrap width="40%">
													<div align="left">
														Monto Retenciones													
													</div>												
												</td>
												<td width="55%">
													<input name="mImporteRetencion" type="text" class="subtotall" readonly="readonly" id="mImporteRetencion"
														maxlength="10" style="text-align: right" size="10" value="0"/>												
												</td>
												<td align="left" width="5%">
													<b>-&nbsp;</b>												
												</td>
											</tr>
											<tr>
												<td align="right" nowrap width="40%">
													<div align="left">
														Monto Penalizaciones													
													</div>												
												</td>
												<td width="55%">
													<input name="mImportePenalizacion" type="text"
														class="subtotall" id="mImportePenalizacion" value="0"
														onblur="cambiafrmt2(this);" maxlength="10" style="text-align: right" size="10"/>												
												</td>
												<td align="left" width="5%">
													<b>-&nbsp;</b>												
												</td>
											</tr>
											<tr>
												<td align="left" nowrap width="40%">
													<div align="left">
														Importe Neto													
													</div>												
												</td>
												<td width="55%">
													<input name="mImporteNeto" value="0" style="text-align: right" type="text"
														class="subtotall" readonly id="mImporteNeto" size="10"/>												
													<input name="mImporteNetoEP" value="0"	style="text-align: right" type="hidden" 
														 readonly id="mImporteNetoEP" size="10"/>												
												</td>
												<td width="5%">	
													<b>=&nbsp;</b>												
												</td>
											</tr>
											<tr>
											<td align="left" nowrap width="40%">
													<div align="left">
											  			Importe a Ejercer</div>											  
											  </td>
												<td width="55%"><input name="mImpEjercer" onKeyPress="return validar2(event)"
														type="text" style="text-align: right" id="mImpEjercer" value="0" maxlength="10" size="10" readonly/>
											   		 <input type="hidden" name="mImporteAnticipoIVA"
														style="text-align: right" class="subtotall"
														id="mImporteAnticipoIVA" maxlength="10" size="10" value="0" />
												</td>
											</tr>
										</table>
									</td>

									<td>
										<table width="200" border="0">
											<tr>
												 <td><!--<p>% Amortizaci&oacute;n	</p>  URVP.10102014 SE OCULTAN CAMPOS DE AMORTIZACION Y ANTICIPO--> 
												<!-- <p>Acumulado de Amortizaci&oacute;n </p> URVP.10102014 SE OCULTAN CAMPOS DE AMORTIZACION Y ANTICIPO-->
												</td>
												<td>
													<input type="hidden" name="nPorcAmortizacion"
															style="text-align: right"  onChange="Amortizacion()"  id="nPorcAmortizacion"
															onblur="cambiafrmt2(this);" maxlength="10" size="10" value="0" />
													<input type="hidden" name="nPorcAmortizacion2"
															style="text-align: right" id="nPorcAmortizacion2" maxlength="10" size="10" value="0" />		
													<input type="hidden" name="mAmortizacionAcumulado"
															style="text-align: right" id="mAmortizacionAcumulado" onblur="cambiafrmt2(this);"
															maxlength="10" size="10" value="0" />												
												</td>
											</tr>
											<tr>
												<td nowrap>
													% IVA												
												</td>
												<td>
													<input name="nPorcIVAAplicable" readonly type="text" id="nPorcIVAAplicable" value="0" class="subtotall"
														maxlength="10" style="text-align: right" size="10"  />												
												</td>
											</tr>
											<tr>
												<td nowrap>
													<p>Saldo del Total Compromiso </p>
													<p> Saldo del Contrato </p>
													<p> Comprometido </p>													
												</td>
												<td>
													<p>
													  <input type="text" name="saldoCed" id="saldoCed"
														maxlength="10" style="text-align: right" class="subtotall" value="0"   readonly size="10" />
													  <input type="hidden" name="saldoCed2" id="saldoCed2" maxlength="10" 
													  	style="text-align: right" value="0" size="10" />
												  </p>
													<input name="mTotal" onkeypress="return validar2(event)"
														type="text" style="text-align: right" id="mTotal"
														value="0" maxlength="10" readonly size="10">
													<p>
													  <input type="text" name="acumulado" id="acumulado"
														maxlength="10" style="text-align: right"   readonly value="0" size="10" />
												    </p>
												</td>
											</tr>
											<tr>
												<td nowrap>
													<p>Acumulado Sanciones</p>
													<p> Acumulado Devoluciones </p></td>
												<td>
													<input type="text" name="mImporteSancionAcumulado" id="mImporteSancionAcumulado"
														value="0" class="subtotall" maxlength="10" style="text-align: right" size="10"  readonly />
													<input name="mTotal2" type="hidden"
														style="text-align: right" id="mTotal2" value="0" maxlength="10" size="10"  />
													<input name="mImporteDevolucionAcumulado" type="text" value="0" style="text-align: right"  readonly
														onkeypress="return validar2(event)" maxlength="10" size="10"  id="mImporteDevolucionAcumulado" />																										</td>
											</tr>
											<tr>
												<td nowrap>
												    Anticipo												
												</td>
												<td>
													<input type="text" name="saldoAnticipo"  readonly id="saldoAnticipo" maxlength="10" value="0"
														class="subtotall" style="text-align: right" size="10" />																									</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td nowrap>
										Concepto :
									</td>
								</tr>
								<tr>
									<td width="100%" colspan=5 height="100%" align="center">
										<textarea cols="80" rows="10" name="Descrip_Concepto" onkeypress="return validar(event)" class="paso01"
											id="Descrip_Concepto"></textarea>
									</td>
								</tr>
							</table>
						</div>
						<div id="tabs-2">
							<table width="95%" align="center" border="0" id="Retenciones"
								cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<table cellpadding="0" cellspacing="0" border="1" class="display" id="tretencion">
											<thead>
												<tr>
													<th>
														Codigo
													</th>
													<th>
														Retencion
													</th>
													<th>
														Porcentaje
													</th>
												</tr>
											</thead>
											<tbody>
											</tbody>
											<tfoot>
											</tfoot>
										</table>
									</td>
								</tr>
								<tr>
									<td>
										<br />
										<br />
									</td>
								</tr>
							</table>
						</div>
						<div id="tabs-3">
							<table align="center" border="0" cellspacing="0" cellpadding="0" class="display">
								<tr>
									<td>Tipo de Concepto</td>	
									<td colspan="3">Tipo De Movimiento</td>
								  	<td align="right">
										<input type="button" value="Limpia" onclick="Limpia1();" class="btnInterfaceBG"	id="Limpia" name="Limpia" />
								  	</td>									
								</tr>

								<tr>
									<td><select id="TIPO_CONCEPTO" name="TIPO_CONCEPTO"
											onChange="concepto();"></select>																											
									</td>	
									<td colspan="3">
										<select id="TIPO_MOVIMIENTO" name="TIPO_MOVIMIENTO">
								      	</select>								    
								    </td>
									<td align="right">
										<input type="button" value="Agregar" onClick="FiltroMovimientos();" class="paso9 btnInterfaceBG" id="agrega2" name="agrega2" />
									</td>
								</tr>
							</table>
							<table align="center" border="0" cellspacing="0" cellpadding="0" class="display">
								<tr>
									<td align="center"><div align="left">Estructura Programática </div></td>
									<td>&nbsp;</td>
                                    <td  align="right">
										<font size="2">Monto Devengado</font>					
									</td>
                                    <td  align="right">
										<font size="2">Monto Acumulado</font>					
									</td>
								</tr>	
								<tr>
									<td align="left">
										<input type="text" maxlength="60" size="50" id="ep" name="ep" readonly/>
								  	</td> 
									<td align="left">
										<input name="button" type="button" id="nIdClaveEgresos2" onclick="Grid()" class="btnInterfaceBG" value="..." size="5" />
									</td>
									<td align="right">
										<input type="text" name="montoDev" value='0' id="montoDev"
											onChange="cambioss();" maxlength="16"   style="text-align:right" onkeypress="return validar2(event)" />
									</td>
									<td align="right">
										<input type="text" name="saldoCompM" id="saldoCompM" value="0" maxlength="10" size="18" style="text-align:right"  readonly />
									</td>
								</tr>
							</table>
							<table   class="display" id="grdCompromisos">
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
							<table cellpadding="0" cellspacing="0" style="visibility:hidden" border="0" class="display" id="grdRetClave">
								<thead>
									<tr>
										<th>
											EP
										</th>
										<th>
											Mes
										</th>
										<th>
											Importe
										</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
								<tfoot>
								</tfoot>
							</table>
						</div>
		<div id="tabs-4">			
			<table border="0" cellspacing="0" cellpadding="0" width="50%" >
          		<tr><td>
				   <table border="0" cellspacing="2" cellpadding="0" >
                  <tr> 
                    <td>No. factura</td>
                    <td>Fecha de factura</td>
                    <td>Tipo de beneficiario</td>
                    <td>Clave de beneficiario</td>
                    <td>Tipo de operaci&oacute;n</td>
                    </tr>
                  <tr> 
                    <td><input name="DCD_FACTURA" type="text" id="DCD_FACTURA" onkeypress="return validar(event)" class= "notEditable" size="15"/>                    </td>
                    <td> <input name="DCD_FECHA_FACTURA" type="text" id="DCD_FECHA_FACTURA" class= "paso03 notEditable" maxlength="10"  size="13"></td>
                    <td><input name="DCD_TBEN" type="text" class= "paso03 notEditable" id="DCD_TBEN" readonly size="15"/></td>
                    <td><input name="DCD_CBEN" type="text" class= "paso03 notEditable" id="DCD_CBEN" readonly size="15"/></td>
                    <td><select name="DCD_TIPO_OPE" id="DCD_TIPO_OPE" class= "paso03 notEditable">
                        <option value="85" class= "paso03" >85 OTROS</option>
                      </select> </td>
                    </tr>
                  <tr> 
                    <td>Porcentaje IVA</td>
                    <td>Importe Total</td>
                    <td>IVA Desgloce</td>
                    <td>IVA Retención </td>
                    <td>ISR</td>
                    </tr>
                  <tr> 
                    <td><input name="DESCRIPCION20" type="text" class= "notEditable" id="DESCRIPCION20" onKeyPress="return validar2(event)" value="0" size="15"/></td>
                    <td><input type="text" id="DCD_IMP_BRUTO" class= "notEditable" onkeypress="return validar2(event)" name="DCD_IMP_BRUTO" size="15"/>                    </td>
                    <td><input name="DCD_IVA" type="text" id="DCD_IVA" value="0" class= "notEditable" onKeyPress="return validar2(event)" size="15"/></td>
                    <td><input name="DCD_IVADES" type="text" id="DCD_IVADES" class= "notEditable" value="0" onKeyPress="return validar2(event)" size="15"/></td>
                    <td><input name="DCD_ISR" type="text" id="DCD_ISR" value="0" class= "notEditable" onkeypress="return validar2(event)" size="15"/>                    </td>
                    </tr>
                  <tr> 
                    <td>Ret. 0.5%</td>
                    <td>Ret. 0.2%</td>
                    <td>Beneficio social</td>
                    <td>Impuesto cedular</td>
                    <td>Penalizaciones</td>
                    </tr>
                  <tr> 
                    <td><input name="DCD_MIL5" type="text" id="DCD_MIL5" class= "notEditable" value="0" onkeypress="return validar2(event)" size="15"></td>
                    <td><input name="DCD_MIL2" type="text" id="DCD_MIL2" value="0"  class= "notEditable" onkeypress="return validar2(event)" size="15"/></td>
                    <td><input name="DCD_CONTRIBUCION" type="text" id="DCD_CONTRIBUCION" value="0" class= "notEditable" onkeypress="return validar2(event)" size="15"/></td>
                    <td><input name="DCD_OTRAS_RET" type="text" id="DCD_OTRAS_RET" value="0" class= "notEditable" onkeypress="return validar2(event)" size="15"></td>
                    <td><input name="DCD_PENALIZACION" type="text" id="DCD_PENALIZACION" value="0" class= "notEditable" onkeypress="return validar2(event)" size="15"/></td>
                    </tr>
                  <tr>
                    <td>Sanci&oacute;n</td>
                    <td>Devoluci&oacute;n</td>
                    <td>Amort. Anticipo</td>
                    <td>Total Retenci&oacute;n</td>
                    <td>Neto</td>
                    </tr>
                  <tr> 
                    <td><input name="DCD_SANCION" type="text" id="DCD_SANCION" class= "notEditable" value="0" onkeypress="return validar2(event)" size="15"/></td>
                    <td><input name="DCD_DEVOL" type="text" id="DCD_DEVOL" value="0" class= "notEditable" onkeypress="return validar2(event)" size="15"/></td>
                    <td><input name="DCD_AMORT" type="text" id="DCD_AMORT" value="0" class= "notEditable" onkeypress="return validar2(event)" size="15"/></td>
                    <td><input name="DCD_RETENCION" type="text" id="DCD_RETENCION" value="0" class= "notEditable" onkeypress="return validar2(event)" size="15" /></td>
                    <td><input name="DCD_NETO" type="text" id="DCD_NETO" value="0" class= "notEditable" onkeypress="return validar2(event)" size="15"/></td>
                    </tr>
                  <tr> 
                    <td>Concepto:</td>
                    <td><input type="button"  id="Agregar1" name="Agregar1" value="Agregar"  onclick="validaFID();" class="btnInterfaceBG"/>
					  <div id="divImprime"  style="font-size:18px">  
					   <img src="imagenes/Imprimir.png" width="33" height="28" onClick="cmdImprimir('ContrareciboN');" >Contrarecibo 
					   </div>					  
					</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    </tr>
                  <tr> 
                    <td colspan="3"><textarea id="DCD_CONCEPTO" name="DCD_CONCEPTO" class= "paso03" style="height: 91px; width: 448px" onkeypress="return validar(event)"></textarea></td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    </tr>
                </table>
				   </td></tr>
				<tr> 
            		<td colspan="3" align="left"> </td>
          		</tr>
        </table>
				<table   class="display" id="grdFacturas">
                  <thead>
                    <tr> 
                      <th nowrap >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >Descuentos</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                      <th align="left" >&nbsp;</th>
                    </tr>
                    <tr> 
                      <th nowrap >No. Factura</th>
                      <th align="left" >Fecha Factura</th>
                      <th align="left" >Tipo beneficiario</th>
                      <th align="left" >Cve Benef</th>
                      <th align="left" >Tipo oper</th>
                      <th align="left" >%IVA</th>
                      <th align="left" >Importe Total</th>
                      <th align="left" >IVA desglose</th>
                      <th align="left" >IVA</th>
                      <th align="left" >ISR</th>
                      <th align="left" >Ret. 0.5</th>
                      <th align="left" >Ret 0.2</th>
                      <th align="left" >Beneficio social</th>
                      <th align="left" >Impuesto cedular</th>
                      <th align="left" >Penalizaciones</th>
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
				<div id="dialog-validaOLI" title="Validación de OLI">
					<h1>Digite el Número de OLI a Validar</h1>
		  			<input style="text-align: right;" name="cOLI" type="text" id="cOLI" value="" size="6" maxlength="6" />
				</div>				
				<div id="dialog-validaFact" title="Validación de Facturas">
					<div id="uploadFacturasDiv">
						<iframe id="uploadFacturasFrm" src="UploadFacturasPF.jsp?tipo_pago=PAGOFEDERALIZADO" align="top" frameborder="0" height="400" width="840">
						</iframe>
					</div>
					<div id="facturasCapturadasDiv">
						<table width="100%">
							<tr id="uploadFacturasTR">
								<td align="right">
									<a href="#" onclick="togleDivFacts(1);return false;">Cargar Facturas</a>
								</td>
							</tr>
							<tr>
								<td>
									<fieldset>
									<legend>Facturas Capturadas.</legend>
									<table id="grdValidaFacturas">
											<thead>
												<tr>
													<th>Serie</th>
													<th>Factura</th>
													<th>Importe Bruto</th>
												</tr>
											</thead>
									</table>
										Total de las facturas:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										<input type="text" style="text-align: right;" name="mTotalFacturaV"
											id="mTotalFacturaV" value="0.00" size="15" maxlength="15" readonly />							
									</fieldset>
								</td>
							</tr>
						</table>
					</div>
				</div>
				<!-- Dialogo Firmantes -->
				<jsp:include page="Firmantes.jsp"></jsp:include>
				<!-- Fin Dialogo Firmantes -->				
			</form>
		</div>
		<div id="capturaFID_DIV">
			<fieldset>
				<legend>Capture Datos FID</legend>
				<table>
					<tr>
						<td align="right">
							Programa:
						</td>
						<td align="left">
							<select id="cProgramaDesc" onchange="cambiaPrograma()">
							</select>
						</td>
					</tr>
					<tr>
						<td align="right">
							Subprograma:
						</td>
						<td align="left">
							<select id="cSubProgramaDesc"></select>
						</td>						
					</tr>
				</table>
			</fieldset>
		</div>	
	</body>
</html>
