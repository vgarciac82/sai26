<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>

<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String ur = usuario.getU_UR();
	String login = usuario.getLogin();
	String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat fe = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = fe.format(c1.getTime());
	
	String mensaje =(request.getParameter("mensaje") == null) ? "vacio" : request.getParameter("mensaje") ;
	
	/*FAV20171019 Se guarda en base el prefijo de CxP*/
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
	String cxpPrefijo = cabl.getSystemSetting("CXP_PREFIJO") == null?"CP":cabl.getSystemSetting("CXP_PREFIJO");
	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>Capitulo Mil</title>
    
    <meta http-equiv="description" content="Capitulo Mil">
    
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
		
	<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>	
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="../Generador/js/Moment.js"></script>  
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>

<script type="text/javascript">

/*FAV20171019 Se guarda en base el prefijo de CxP*/
var cxpPrefijo = "<%=cxpPrefijo%>";

$(document).ready(function(){
		$("input.AyudaSyC").subIniciaDlg();	//Para mostrar Ayudas
		$("#btnEnviarComprometido").button();
		$("#btnAplicarCompromiso").button();
		$("#btnGenerarLayoutComprometido").button();
		$("#btnCancelaCompromiso").button();
		$("#btnActualizaFolioSicop").button();
		$("#btnEnviarNomina").button();
		$("#btnAplicarNomina").button();
		$("#btnGenerarLayoutCXP").button();
		$("#btnCancelarCXP").button();
		$("#btnActualizaSolicitudPago").button();
		$("#btnAplicar").button();
		$("#btnAnexo").button();
		$("#btnEnviarComprometidoAmpliacion").button();
		$("#btnAplicarCompromisoAmpliacion").button();
		$("#btnGenerarLayoutComprometidoAmpliacion").button();
		$("#btnCancelaCompromisoAmpliacion").button();
		$("#btnActualizaFolioSicopAmpliacion").button();
		
		$("#btnAplicar").hide();
		$("#btnAnexo").hide();
		$("#btnAplicarCompromiso").hide();
		$("#btnGenerarLayoutComprometido").hide();
		$("#btnAplicarNomina").hide();
		$("#btnGenerarLayoutCXP").hide();
		$("#btnCancelarCXP").hide();
		$("#btnActualizaFolioSicop").hide();
		$("#btnActualizaSolicitudPago").hide();
		$("#folioSicop").hide();
		$("#solicitudPago").hide();
		$("#cConceptoTxt").hide();
		$("#btnCancelaCompromiso").hide();
		$("#btnAplicarCompromisoAmpliacion").hide();
		$("#btnGenerarLayoutComprometidoAmpliacion").hide();
		$("#btnCancelaCompromisoAmpliacion").hide();
		$("#folioSicopAmpliacion").hide();
		$("#btnActualizaFolioSicopAmpliacion").hide();
		
		$("#btnActualizaFolioSicop").click(function (){  actualiza("compromiso"); });
		$("#btnActualizaSolicitudPago").click(function (){  actualiza("nomina"); });
		$("#BuscarAplicarComprometido").change(function (){  muestraBoton("compromiso");  });
		$("#BuscarAplicarNomina").change(function (){  muestraBoton("nomina");  });
		$("#BuscarAplicarLayoutCLCCapituloMil").change(function(){ querySelectPost("cuentasBancarias", "cCuentaBancaria", {async: false }); });
		$("#BuscarAplicarComprometidoAmpliacion").change(function (){  muestraBoton("compromisoAmpliacion");  });
		$("#btnActualizaFolioSicopAmpliacion").click(function (){  actualiza("compromisoAmpliacion"); });
		
		querySelectPost("tEjercicioRead", "aEjercicioFiscal", {async: false });
		
		$("#fAplicacion1").val(moment().format('yyyy-MM-DD'));
		
		$(function() {
						
		var mensaje = "<%= mensaje%>";
		
		if(mensaje != "vacio"){
			
			var ar = mensaje.split("/");
			mensaje = ar[0];
			var folioError = ar[1];
			
			Swal.fire({ icon: "warning",
						text: mensaje});
			mensaje = "vacio";
			$("#txtMesaje").html("Cerrar Pestaña Nueva Del Navegador");
			
			if(folioError > 0){
				
				nominaError(folioError);
				$("#dialog" ).dialog( "open" );
			}
			if(folioError.lastIndexOf("No hay suficiente remanente") != -1)
				Swal.fire({ icon: "error",
							text: folioError});				
		}
		$( "#dialog" ).dialog({
		
				autoOpen: false,
				height: 300,
				width: 720,
				modal: true,
				buttons: {
						"Aceptar": function() 
						{
							$( this ).dialog( "close" );
						}
						
				},
				close: function() {
						
					$( this ).dialog( "close" );
						
				}							
		});
	});
	
	$("#dialog-firmantesUpdate").dialog({
		autoOpen: false,
		height: 620,
		width: 500,
		modal: true,
		buttons: {
				"Aceptar": function() 
				{
					if($.trim($("#cNombreVoBoUpdate").val()) == ""){
						Swal.fire({ icon: "warning",
									text: "Falta Ingresar Nombre en Datos Vº Bº."});						
						return; 
					} 
					else if($.trim($("#cPaternoVoBoUpdate").val()) == ""){ 
						Swal.fire({ icon: "warning",
									text: "Falta Ingresar Apellido Paterno en Datos Vº Bº."});											
						return; 
					}
					else if($.trim($("#cMaternoVoBoUpdate").val()) == ""){ 
						Swal.fire({ icon: "warning",
									text: "Falta Ingresar Apellido Materno en Datos Vº Bº."});											
						return;
					}
					else if($.trim($("#cPuestoVoBoUpdate").val()) == ""){ 
						Swal.fire({ icon: "warning",
									text: "Falta Ingresar Puesto en Datos Vº Bº."});											
						return;
					}
					
					if($.trim($("#cNombreAutUpdate").val()) == ""){ 
						Swal.fire({ icon: "warning",
									text: "Falta Ingresar Nombre en Datos Autorizar."});																	
						return; 
					} 
					else if($.trim($("#cPaternoAutUpdate").val()) == ""){ 
						Swal.fire({ icon: "warning",
									text: "Falta Ingresar Apellido Paterno en Datos Autorizar."});																	
						return; 						
					}
					else if($.trim($("#cMaternoAutUpdate").val()) == ""){ 
						Swal.fire({ icon: "warning",
									text: "Falta Ingresar Apellido Materno en Datos Autorizar."});
						return; 
					}
					else if($.trim($("#cPuestoAutUpdate").val()) == ""){ 
						Swal.fire({ icon: "warning",
									text: "Falta Ingresar Puesto en Datos Autorizar."});
						return; 
					}
					
					if($.trim($("#cNombreElaUpdate").val()) == ""){ 
						Swal.fire({ icon: "warning",
									text: "Falta Ingresar Nombre en Datos Elabora."}); 
						return; 
					} 
					else if($.trim($("#cPaternoElaUpdate").val()) == ""){ 
						Swal.fire({ icon: "warning",
									text: "Falta Ingresar Apellido Paterno en Datos Elabora."});
						return; 
					}
					else if($.trim($("#cMaternoElaUpdate").val()) == ""){ 
						Swal.fire({ icon: "warning",
									text: "Falta Ingresar Apellido Materno en Datos Elabora."});
						return; 
					}
					else if($.trim($("#cPuestoElaUpdate").val()) == ""){ 
						Swal.fire({ icon: "warning",
									text: "Falta Ingresar Puesto en Datos Elabora."});					
						return; 
					}
					
					$("#cNombreE").val($("#cNombreElaUpdate").val());
					$("#cPaternoE").val($("#cPaternoElaUpdate").val());
					$("#cMaternoE").val($("#cMaternoElaUpdate").val());
					$("#cPuestoE").val($("#cPuestoElaUpdate").val());
					
					$("#firmanteVoBo").val($("#cNombreVoBoUpdate").val()+" "+$("#cPaternoVoBoUpdate").val()+" "+$("#cMaternoVoBoUpdate").val());
					$("#cPuestoVoBo").val($("#cPuestoVoBoUpdate").val());
					$("#firmanteAut").val($("#cNombreAutUpdate").val()+" "+$("#cPaternoAutUpdate").val()+" "+$("#cMaternoAutUpdate").val());
					$("#cPuestoAut").val($("#cPuestoAutUpdate").val());
					
					$("#firmanteEla").val($("#cNombreE").val()+" "+$("#cPaternoE").val()+" "+$("#cMaternoE").val());
					
					try{
						Swal.fire({
							text: "¿Desea actualizar los datos de los firmantes con la informacion capturada?",
							icon: "info",
							showCancelButton: true,
							confirmButtonColor: '#288BA8',
							cancelButtonColor: '#e6e6e6',
							confirmButtonText: 'Aceptar',
							cancelButtonText: 'Cancelar'
						}).then((result) => {
							if(result.isConfirmed){						
								queryFormPost("tNOMINAEncabezadoFirmante_Update", {async: false });							
								Swal.fire({ icon: "success",
											text: "Firmantes Actualizados Correctamente."});			
							}
						})
						
						$("#cNombreVoBoUpdate").val("");
						$("#cPaternoVoBoUpdate").val("");
						$("#cMaternoVoBoUpdate").val("");
						$("#cPuestoVoBoUpdate").val("");
						
						$("#cNombreAutUpdate").val("");
						$("#cPaternoAutUpdate").val("");
						$("#cMaternoAutUpdate").val("");
						$("#cPuestoAutUpdate").val("");
						
						$("#cNombreElaUpdate").val("");
						$("#cPaternoElaUpdate").val("");
						$("#cMaternoElaUpdate").val("");
						$("#cPuestoElaUpdate").val("");
						
					}catch(e){
						Swal.fire({ icon: "error",
									text: "No se pudo actualizar los firmantes, intente mas tarde."});						
					}
					$(this).dialog("close");
				},
				"Cancelar": function() {
					$("#cNombreVoBoUpdate").val("");
					$("#cPaternoVoBoUpdate").val("");
					$("#cMaternoVoBoUpdate").val("");
					$("#cPuestoVoBoUpdate").val("");
					
					$("#cNombreAutUpdate").val("");
					$("#cPaternoAutUpdate").val("");
					$("#cMaternoAutUpdate").val("");
					$("#cPuestoAutUpdate").val("");
					
					$("#cNombreElaUpdate").val("");
					$("#cPaternoElaUpdate").val("");
					$("#cMaternoElaUpdate").val("");
					$("#cPuestoElaUpdate").val("");
					
					$(this).dialog("close");
				}
			},
		close: function(){}							
	});
});
		function cargarArchivoCompromiso(tipo, archivo){
			$("#fAplicacion").val($("#fAplicacion1").val().split('-').reverse().join('/'))
			
				var fechaAplicacion = $("#fAplicacion").val();
			
				if(tipo == "btnGenerarLayoutCXP"){
					if ( $("#BuscarAplicarNomina").val() == "") {
						Swal.fire({ icon: "warning",
									text: "Debe Seleccionar la Cuenta por Pagar."});												
					}else {
						
						$("#rfcAMF").val("");
						$("#cConcepto").val("");
						$("#cCuentaBancaria").val("");
						$("#solicitudPago").val("");
						$("#cConceptoTxt").val("");
						$("#btnAplicar").hide();
						$("#btnAnexo").hide();
						$("#BuscarAplicarComprometido").val("");
						$('#frmCapituloMil').attr('enctype','application/x-www-form-urlencoded');
						$("#frmCapituloMil").attr("action","../gstnmngr/generaLayoutPagosNomina");
						$("#frmCapituloMil").submit();
						$("#BuscarAplicarNomina").val("");
					}
					return;
				}
				if(tipo == "BuscarGenerarLayoutComprometido"){
					
					$("#nFolioCompromisoNomina").val("");
					$("#cIdContrato").val("");
					$("#docAplicado").val("");
					$("#BuscarAplicarNomina").val("");
					$("#frmCapituloMil").attr("action","../gstnmngr/generaLayoutNOMCompromisos");
					$("#frmCapituloMil").submit();
					$("#BuscarAplicarComprometido").val("");
					return;
					
				}
				if(tipo == "BuscarGenerarLayoutComprometidoAmpliacion"){
					
					$("#nFolioCompromisoNomina").val("");
					$("#cIdContrato").val("");
					$("#docAplicado").val("");
					$("#BuscarAplicarNomina").val("");
					$("#frmCapituloMil").attr("action","../gstnmngr/generaLayoutNOMCompromisos");
					$("#frmCapituloMil").submit();
					$("#BuscarAplicarComprometidoAmpliacion").val("");
					return;
					
				}
				
				if(tipo == "generaLayoutPagosNomina"){
					
					var concepto = "";
					
					if($("#BuscarAplicarLayoutCLCCapituloMil").val() == "" ){
						Swal.fire({ icon: "warning",
									text: "Falta Seleccionar Layout."});												
						return;
					}
					if($("#solicitudPago").val() == "" ){
						Swal.fire({ icon: "warning",
									text: "Falta Agregar Solicitud de Pago."});												
						return;
					}
					if($("#cCuentaBancaria").val() == "" ){
						Swal.fire({ icon: "warning",
									text: "Falta Agregar Cuenta Bancaria."});												
						return;
					}
					
					if($("#checkAP").is(":checked")){
						concepto += "'AP',";
					}if($("#checkPT").is(":checked")){
						concepto += "'PT',";
					}if($("#checkPP").is(":checked")){
						concepto += "'PP',";
					}if($("#checkPN").is(":checked")){
						concepto += "'PN',";
					}if($("#checkPI").is(":checked")){
						concepto += "'PI',";
					}if($("#checkFR").is(":checked")){
						concepto += "'FR',";
					}if($("#checkTodo").is(":checked")){
						concepto = "'AP','PT','PP','PN','PI','FR',";
					}
					
					$("#cConcepto").val(concepto);
					
					if ( $("#cConcepto").val() == "") {
						Swal.fire({ icon: "warning",
									text: "Debe Seleccionar al menos un Concepto."});						
					}else{
						if ( $("#solicitudPago").val() == "") {
							Swal.fire({ icon: "warning",
										text: "Debe Capturar el Folio de la Solicitud de Pago."});							
						}else{
							$('#frmCapituloMil').attr('enctype','application/x-www-form-urlencoded');
							$("#frmCapituloMil").attr("action","../gstnmngr/generaLayoutPagosNomina");
							$("#frmCapituloMil").submit();
						}
					}
					return;
				}
				
				if(tipo == "flComprometidoAmpliacion"){
					if($("#BuscarCompromisoAmpliacion").val() == "" ){
						Swal.fire({ icon: "warning",
									text: "Favor de seleccionar el compromiso que se va ampliar."});						
						return;
					}
					
					if ($("#chk_reduccion").prop("checked"))
						$("#tipoAmplRed").val("reducir");
					else
						$("#tipoAmplRed").val("ampliar");
					
					if (!confirm("¿Desea " + $("#tipoAmplRed").val() + " el compromiso para el folio " + $("#BuscarCompromisoAmpliacion").val() + "?"))
						return;
				}
				
				var ext = new Array(".csv");
				
				if(!archivo){
					Swal.fire({ icon: "error",
								text: "No se a cargado ningun Archivo."});					
					return;
					
				}else{
					
					$("#esperar").attr("style","visibility=visible"); 
					
					var correcto = false;
					extension = (archivo.substring(archivo.lastIndexOf("."))).toLowerCase();
					if (ext[0] == extension){
						correcto = true;
					}
					if (!correcto){ 
						Swal.fire({ icon: "warning",
									text: "Comprueba la extensión de los archivos a subir. \n S\u00f3lo se pueden subir archivos con extensiones: " + ext.join()});						
						$("#esperar").attr("style","visibility=hidden"); 
					
					}else{
						
						if(tipo == "flComprometido"){
							
							var archivo = (archivo.substring(archivo.lastIndexOf('\\')));
							var arr =  archivo.split('.');
							var contrato = arr[0];
							
							Swal.fire({
									text: "Esta Seguro De Cargar El Archivo Compromiso " + contrato + " ? Con Fecha " + fechaAplicacion,
									icon: "info",
									showCancelButton: true,
									confirmButtonColor: '#288BA8',
									cancelButtonColor: '#e6e6e6',
									confirmButtonText: 'Aceptar',
									cancelButtonText: 'Cancelar'
								}).then((result) => {
									if(result.isConfirmed){									
										f = document.getElementById("flNomina"); 
										nodoPadre = f.parentNode; 
										nodoSiguiente = f.nextSibling; 
										nodoPadre.removeChild(f); 
										f = document.getElementById("flComprometidoAmpliacion"); 
										nodoPadre = f.parentNode; 
										nodoSiguiente = f.nextSibling; 
										nodoPadre.removeChild(f);
										$("#tipoArchivo").val("docComprometido");
									
										getNextSequenceVal({seqName: "CO-" + "<%=cCentroContable%>", async: false, callback: setSequenceVal});
										getNextSequenceVal({seqName: "COMPROMISO" + "", async: false, callback: setSequenceValComp});
										getNextSequenceVal({seqName: "ID_CASO" + "", async: false, callback: setSequenceValCaso});
										
										$("#cIdContrato").val(contrato);
										$('#frmCapituloMil').attr('action','../gstnmngr/SubirArchivosCapituloMil');
										$('#frmCapituloMil').attr('enctype','multipart/form-data');
										$("#tipoArchivo").val("docComprometido");
										$("#frmCapituloMil").submit();
									} else {
										$("#esperar").attr("style","visibility=hidden"); 
									}
								})
														
						}else if(tipo == "flComprometidoAmpliacion"){
							
							var archivo = (archivo.substring(archivo.lastIndexOf('\\')));
							var arr =  archivo.split('.');
							var contrato = arr[0];
							
							Swal.fire({
									text: "Esta Seguro De Cargar El Archivo Compromiso " + contrato + " ? Con Fecha " + fechaAplicacion,
									icon: "info",
									showCancelButton: true,
									confirmButtonColor: '#288BA8',
									cancelButtonColor: '#e6e6e6',
									confirmButtonText: 'Aceptar',
									cancelButtonText: 'Cancelar'
								}).then((result) => {
									if(result.isConfirmed){
										$("#compromisoAmpliado").val($("#BuscarCompromisoAmpliacion").val());
										f = document.getElementById("flNomina"); 
										nodoPadre = f.parentNode; 
										nodoSiguiente = f.nextSibling; 
										nodoPadre.removeChild(f);
										f = document.getElementById("flComprometido"); 
										nodoPadre = f.parentNode; 
										nodoSiguiente = f.nextSibling; 
										nodoPadre.removeChild(f);
										$("#tipoArchivo").val("docComprometido");
									
										getNextSequenceVal({seqName: "CO-" + "<%=cCentroContable%>", async: false, callback: setSequenceVal});
										getNextSequenceVal({seqName: "COMPROMISO" + "", async: false, callback: setSequenceValComp});
										getNextSequenceVal({seqName: "ID_CASO" + "", async: false, callback: setSequenceValCaso});
										
										$("#cIdContrato").val(contrato);
										$('#frmCapituloMil').attr('action','../gstnmngr/SubirArchivosCapituloMil');
										$('#frmCapituloMil').attr('enctype','multipart/form-data');
										$("#tipoArchivo").val("docComprometido");
										$("#frmCapituloMil").submit();
									}else{
										$("#esperar").attr("style","visibility=hidden"); 
									}
								})
							
							$("#compromisoAmpliado").val("");
							
						}else if(tipo == "flNomina"){
							
							var archivo = (archivo.substring(archivo.lastIndexOf('\\')));
							var arr =  archivo.split('.');
							var contrato = arr[0];
						
							Swal.fire({
								text: "Esta Seguro De Cargar El Archivo Nomina " + contrato + " ? Con Fecha " + fechaAplicacion,
								icon: "info",
								showCancelButton: true,
								confirmButtonColor: '#288BA8',
								cancelButtonColor: '#e6e6e6',
								confirmButtonText: 'Aceptar',
								cancelButtonText: 'Cancelar'
							}).then((result) => {
								if(result.isConfirmed){	
									f = document.getElementById("flComprometido"); 
									nodoPadre = f.parentNode; 
									nodoSiguiente = f.nextSibling; 
									nodoPadre.removeChild(f); 
									f = document.getElementById("flComprometidoAmpliacion"); 
									nodoPadre = f.parentNode; 
									nodoSiguiente = f.nextSibling; 
									nodoPadre.removeChild(f);
									$("#tipoArchivo").val("docNomina");
								
									var contraReciboNomina = "CR-"+$("#cCentroContable").val();
									$("#caNoContrarreciboNomina").val( contraReciboNomina );
									getNextSequenceVal({seqName: "NOMINA" , async: false, callback: setSequenceValFolioNomina});
									getNextSequenceVal({seqName: "ID_CASO" + "", async: false, callback: setSequenceValCaso});
									
									$("#frmCapituloMil").attr("action","../gstnmngr/SubirArchivosCapituloMil");
									$("#frmCapituloMil").attr("enctype","multipart/form-data");
									
									$("#tipoArchivo").val("docNomina");
									$("#frmCapituloMil").submit();
								}else{
									$("#esperar").attr("style","visibility=hidden"); 
								}
							})
							
						}else if(tipo == "BuscarGenerarLayoutComprometido"){
							
							$("#frmCapituloMil").attr("action","../gstnmngr/generaLayoutNOMCompromisos");
							$("#frmCapituloMil").submit();
							
						}
						
					}
				}		
		}
		
		function setSequenceVal(seqValue) {
			
			seqValue = 100000 + parseInt(seqValue, 10);
			seqValue = "<%=cCentroContable%>" + "CO" + $("#aEjercicioFiscal").val() + seqValue;
			$("#caNoCompromiso").val( seqValue );
		}
		
		function setSequenceValComp(seqValue) {
			$("#nFolioCompromisoNomina").val( seqValue );
			
		}
		
		function setSequenceValCaso(seqValue) {
			$("#idCaso").val( seqValue );
		}
		
		function setSequenceValNominaRecibo(seqValue) {
			seqValue = "000000" + seqValue;
			seqValue = seqValue.substr(seqValue.length - 6);
			seqValue = "1" + seqValue.substr(seqValue.length - 5);
			seqValue = $("#cCentroContable").val() + cxpPrefijo + $("#aEjercicioFiscal").val() + seqValue;
			$("#caNoContrarreciboNomina").val( seqValue );
		}
		function setSequenceValFolioNomina(seqValue) {
			$("#nFolioNOMINA").val( seqValue );
		}
		function aplicarContablemente(documento){
			
			var fechaAplicacion = $("#fAplicacion").val();
			var caNoContrarrecibo = "";
			var tipo = "";
			var tipoDocumento = "validarArchivoCapituloMil";
			var doc = "";
			
			if(documento == "compromiso"){
				
				caNoContrarrecibo = $("#BuscarAplicarComprometido").val();
				tipo = "aplicarCompromisoCapituloMil";
				doc = "Comprometido";	
				
			}else if(documento == "compromisoAmpliacion"){
				caNoContrarrecibo = $("#BuscarAplicarComprometidoAmpliacion").val();
				tipo = "aplicarCompromisoCapituloMil";
				doc = "Comprometido";
			}else{
				
				caNoContrarrecibo = $("#BuscarAplicarNomina").val();
				tipo = "aplicarNominaCapituloMil";
				doc = "Nomina";
			}	
			
			if(caNoContrarrecibo != ""){
				
					$.ajax({
							url:'./cierrePresupuestal.jsp',
							type:'post',
							dataType: 'json',
							data:{tipo:tipoDocumento,caNoContrarrecibo:caNoContrarrecibo,documento:documento },
							success:function(data){
								
									var arr = data.respuesta.split("/");
									var cuantos = arr[0];
									var total =  arr[1];
									$("#esperar").attr("style","visibility=visible");
								
									Swal.fire({
										text: "Esta Seguro de Aplicar: \n - "+cuantos+ " Estructuras Programaticas \n - Total de $ "+total+ " \n - Fecha "+fechaAplicacion,
										icon: "info",
										showCancelButton: true,
										confirmButtonColor: '#288BA8',
										cancelButtonColor: '#e6e6e6',
										confirmButtonText: 'Aceptar',
										cancelButtonText: 'Cancelar'
									}).then((result) => {
										if(result.isConfirmed){
											$.ajax({
													url:'./cierrePresupuestal.jsp',
													type:'post',
													dataType: 'json',
													data:{tipo:tipo,caNoContrarrecibo:caNoContrarrecibo},
													success:function(data){
														if(data.sinSesion == 'sinSesion'){
															location.href = "../index.jsp";
														}
														$("#docAplicado").val("");
														if(data.estatus == "guardado"){
															Swal.fire({ icon: "success",
																		text: "Aplicado Correctamente el Documento "+caNoContrarrecibo});															
															$("#docAplicado").val("S");
														}else{
															//alert("MENSAJE COMPLETO: "+data.estatus);
															var detalle = data.estatus.split(":");
															//alert("LENGHT :"+detalle.length);
															if( detalle.length == 2){
																Swal.fire({ icon: "warning",
																			text: detalle[1]});																
																
															}else if( detalle.length == 3){
																Swal.fire({ icon: "warning",
																			text: detalle[2]});																
															}else{
																Swal.fire({ icon: "warning",
																			text: data.estatus});																
															}
														}
														$("#btnAplicarCompromiso").hide();
														$("#btnGenerarLayoutComprometido").hide();
														$("#btnAplicarCompromisoAmpliacion").hide();
														$("#btnGenerarLayoutComprometidoAmpliacion").hide();
														$("#btnAplicarNomina").hide();
														$("#btnGenerarLayoutCXP").hide();
														$("#btnCancelarCXP").hide();
														$("#BuscarAplicarComprometido").val("");
														$("#BuscarAplicarComprometidoAmpliacion").val("");
														$("#BuscarAplicarNomina").val("");
														$("#esperar").attr("style","visibility=hidden");
														
														if(documento == "compromiso"){
															$("#BuscarAplicarComprometido").val(caNoContrarrecibo);
															muestraBoton("compromiso");
														}else if(documento == "compromisoAmpliacion"){
															$("#BuscarAplicarComprometidoAmpliacion").val(caNoContrarrecibo);
															muestraBoton("compromisoAmpliacion");
														}else{
															$("#BuscarAplicarNomina").val(caNoContrarrecibo);
															muestraBoton("nomina");
														}
													}
											});
										
										}else{
											$("#esperar").attr("style","visibility=hidden");
										}
									})
							}
					});
			}else{
				Swal.fire({ icon: "warning",
							text: "Falta Seleccionar "+doc});				
				
			}
		}
		
		function muestraBoton(documento){
			
			if(documento == "compromiso"){
				
					if($("#docAplicado").val() == 'S'){
						
						$("#btnAplicarCompromiso").hide();
						$("#btnGenerarLayoutComprometido").show();
						
						$("#btnActualizaFolioSicop").hide();
						$("#folioSicop").hide();

						if($("#nEnviadoSICOP").val() == 1){
							$("#btnActualizaFolioSicop").show();
							$("#folioSicop").show();
							
						}
							
					}else if($("#docAplicado").val() == 'C'){
							$("#btnAplicarCompromiso").hide();
							$("#btnGenerarLayoutComprometido").hide();
							$("#btnAplicar").hide();
							$("#btnAnexo").hide();
					}else{
							$("#btnAplicarCompromiso").show();
							$("#btnCancelaCompromiso").show();
							$("#btnGenerarLayoutComprometido").hide();
							$("#btnAplicar").hide();
							$("#btnAnexo").hide();
					}
			}else if(documento == "compromisoAmpliacion"){
					if($("#docAplicado").val() == 'S'){
						$("#btnAplicarCompromisoAmpliacion").hide();
						$("#btnGenerarLayoutComprometidoAmpliacion").show();
						$("#btnActualizaFolioSicopAmpliacion").hide();
						$("#folioSicopAmpliacion").hide();
						if($("#nEnviadoSICOP").val() == 1){
							$("#btnActualizaFolioSicopAmpliacion").show();
							$("#folioSicopAmpliacion").show();
						}
					}else{
							$("#btnAplicarCompromisoAmpliacion").show();
							$("#btnCancelaCompromisoAmpliacion").show();
							$("#btnGenerarLayoutComprometidoAmpliacion").hide();
							$("#btnAplicar").hide();
							$("#btnAnexo").hide();
					}
			}else if(documento == "nomina"){
					if($("#docAplicado").val() == 'S'){
						
						$("#btnAplicarNomina").hide();
						$("#btnGenerarLayoutCXP").show();
						$("#btnCancelarCXP").hide();
						$("#btnAplicar").show();
						$("#EditaFirmas").css('visibility', 'visible');
						$("#btnAnexo").show();

						$("#btnActualizaSolicitudPago").hide();
						$("#solicitudPago").hide();
						$("#cConceptoTxt").hide();
						
						if($("#nEnviadoSICOP").val() == 1){
					
							$("#btnActualizaSolicitudPago").show();
							$("#solicitudPago").show();
							$("#cConceptoTxt").show();
							
						}

						if( $("#cConceptoTxt").val() != "" ){
							
							$("#btnGenerarLayoutCXP").show();
						}
							
					}else if($("#docAplicado").val() == 'C'){
							$("#btnAplicarNomina").hide();
							$("#EditaFirmas").css('visibility', 'hidden');
							$("#btnGenerarLayoutCXP").hide();
							$("#btnCancelarCXP").hide();
							$("#btnAplicar").hide();
							$("#btnAnexo").hide();
					}else{
							$("#btnAplicarNomina").show();
							$("#EditaFirmas").css('visibility', 'hidden');
							$("#btnGenerarLayoutCXP").hide();
							$("#btnCancelarCXP").show();
							$("#btnAplicar").hide();
							$("#btnAnexo").hide();
					}
	
			
		}
	}
function updateFirmantes(){
	$("#dialog-firmantesUpdate").dialog("open");
}
function cmdImprimir(){
	 
	 var elFormato = 'PolizaPagoN';
	 tipo='CR.';
	 var f = tipo +"caNoContrarrecibo = '" + $("#BuscarAplicarNomina").val();
	 
				window.open(
					"../admin/SeguridadCatalogos?"
						+ "catalogo=CONTRARECIBO"
						+ "&accion=run"
						+ "&rn=" + elFormato + ".jasper"
						+ "&whereFolio= "+  tipo +"caNoContrarrecibo = '" + $("#BuscarAplicarNomina").val()	+"'",
						//+ "&cargo="    + ""
						//+ "&area="     + "",
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768");
					
}
function anexo(){
		window.open(
					"../admin/SeguridadCatalogos?"
						+ "catalogo=ANEXO"
						+ "&accion=run"
						+ "&rn=Anexo1.jasper"
						+ "&swhere= and caNoContrarrecibo ='"+ $("#BuscarAplicarNomina").val()+"'" ,  
					"Anexo",
					"scrollbars=1, resizable=yes, width=1024, height=768");
}

function actualiza(tipo){
	
	if(tipo == "compromiso"){
		if($("#folioSicop").val() != ""){
			
			queryFormPost("UpdatetCompromisoNominaEncabezado", {async: false });
			Swal.fire({ icon: "success",
						text: "Actualizado Correctamente el Documento Con el Folio SICOP"});			
			location.reload();
			
		}else{
			alert("Falta Agregar Folio Sicop");
			return;
		}	
	}else if(tipo == "compromisoAmpliacion"){
		if($("#folioSicopAmpliacion").val() != ""){
			$("#folioSicop").val($("#folioSicopAmpliacion").val());
			$("#BuscarAplicarComprometido").val($("#BuscarAplicarComprometidoAmpliacion").val());			
			queryFormPost("UpdatetCompromisoNominaEncabezado", {async: false });
			Swal.fire({ icon: "success",
						text: "Actualizado Correctamente el Documento Con el Folio SICOP"});			
			location.reload();
		}else{
			Swal.fire({ icon: "warning",
						text: "Falta Agregar Folio Sicop"});			
			return;
		}	
	}else{
		if($("#solicitudPago").val() != "" && $("#cConceptoTxt").val() != ""){
			
			var numNomina = $("#BuscarAplicarNomina").val();
			var datos = "sinDatos";
			var solicitudPago = $("#solicitudPago").val();
			var elParametro = "";
			var camposWhere = " solicitudPago = '"+solicitudPago+"' AND caNoContrarrecibo != '"+numNomina+"'"; 
			
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "TNOMINAENCABEZADO_MIL", Campos:camposWhere, Param:elParametro, Order:"", MaxReg: "10", ajax: 'true'}, function(j){
				
					for (var i = 0; i < j.length; i++) {
							
							var solicitudPago = j[i].Col0;
							datos = "datos";
							Swal.fire({ icon: "info",
										text: "Ya Existe La Solicitud de Pago"});
							return;
							
					}
					if(datos == "sinDatos"){
						queryFormPost("UpdatetNominaEncabezado", {async: false });
						Swal.fire({ icon: "success",
									text: "Actualizado Correctamente el Documento Con Solicitud Pago."});						
						location.reload();
					}
			});
		}else{
			Swal.fire({ icon: "warnin",
						text: "Falta Agregar Solicitud de Pago o Concepto."});			
			return;
			
		}
	}
}
function onlyNumbers(evt) 
{
		var keyPressed = (evt.which) ? evt.which : event.keyCode
		if (keyPressed == 47) { return false; }
		return !(keyPressed > 31 && (keyPressed < 46 || keyPressed > 57));
}

function valida_concepto(e) {
		var nChars = $("#cConceptoTxt").val();
		nChars = nChars.length;
		tecla = (document.all) ? e.keyCode : e.which;
		
		if (tecla==8) {
			$("#nChars").val( --nChars );
			if (nChars < 0)
				$("#nChars").val( 0 );
			return true;
		}
		
		if (nChars >= 400) {
			return false;
		}
		$("#nChars").val( ++nChars );
		patron =/[A-Za-z.\d\s\\. `]/;
		te = String.fromCharCode(tecla);
		return patron.test(te);
}

function nominaError(folioError){
	
	var camposWhere = " nFolio = "+folioError;
	var elParametro = "";
	var res = "";
	
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "TNOMINAERROR_MIL", Campos:camposWhere, Param:elParametro, Order:"", MaxReg: "10", ajax: 'true'}, function(j){
					
						for (var i = 0; i < j.length; i++) {
								
								var ep = j[i].Col0;
								var error = j[i].Col1;
								var monto = j[i].Col2;
								
								res += ep +" "+ error +""+ monto +" \n";
													
						}
						$("#txtDif").val(res);
						$("#dialog").dialog( "open" );
		});	
}

function updateCancelaCompromiso(tipo){
	var compromiso = "";
	
	if (tipo == "compromiso")
		compromiso = $("#BuscarAplicarComprometido").val();
	else if (tipo == "compromisoAmpliacion")
		compromiso = $("#BuscarAplicarComprometidoAmpliacion").val();
	
	if (confirm("¿Desea cancelar el compromiso " + compromiso + "?")){
		$("#compromisoUpdateCancelar").val(compromiso);
		queryFormPost("updateCompromisoCanceladoNomina", {async: false });
		location.reload();
	}
}

function updateCancelaCXP(){
	var cuentaPagar;
	
	cuentaPagar = $("#BuscarAplicarNomina").val();
	
	if (confirm("¿Desea cancelar la cuenta por pagar: " + cuentaPagar + "?")){		
		queryFormPost("documentoAplicadoNominaEncUpdate", {async: false });
		Swal.fire({ icon: "success",
					text: "Cuenta por Pagar cancelada correctamente."});		
		location.reload();
	}
}
</script>
</head>
<br/>
<body id="dt_example">
<form method="post" id="frmCapituloMil" name="frmCapituloMil" >

	<div id="container" class="container" style="width: 70%">
		<input type="hidden" name="mensaje" id="mensaje" value="" />
		<input type="hidden" name="caNoCompromiso" id="caNoCompromiso" />
		<input type="hidden" name="nFolioCompromisoNomina" id="nFolioCompromisoNomina" />
		<input type="hidden" name="tipoArchivo" id="tipoArchivo" />
		<input type="hidden" name="cCentroContable" id="cCentroContable" value="<%= cCentroContable%>" />
		<input type="hidden" name="cRamo" id="cRamo" value="16" />
		<select style="visibility: hidden" name="aEjercicioFiscal" id="aEjercicioFiscal"></select>
		<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value="<%=ur%>" />
		<input type="hidden" name="idCaso" id="idCaso" />
		<input type="hidden" name="login" id="login" value="<%=login%>"/>
		<input type="hidden" name="cIdContrato" id="cIdContrato" />
		<input type="hidden" name="cConcepto" id="cConcepto" />
		<input type="hidden" name="docAplicado" id="docAplicado" />
		<input type="hidden" name="rfcAMF" id="cIdRFC" />
		<input type="hidden" name="nEnviadoSICOP" id="nEnviadoSICOP" value="0" />
		<input type="hidden" name="nChars" id="nChars" value="0"/>
		
		<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" size=40/>
		<input type="hidden" id="cPuestoVoBo" name="cPuestoVoBo" size=40/>
		<input type="hidden" id="firmanteAut" name="firmanteAut" size=40/>
		<input type="hidden" id="cPuestoAut" name="cPuestoAut" size=40/>
		<input type="hidden" id="compromisoAmpliado" name="compromisoAmpliado" size=40 value=""/>
		<input type="hidden" id="tipoAmplRed" name="tipoAmplRed" size=40 value=""/>
		<input type="hidden" id="compromisoUpdateCancelar" name="compromisoUpdateCancelar" size=40 value=""/>
		
		<!-- hidden para la captura de los datos de quien elaboro -->
		<input type="hidden" id="cNombreE" name="cNombreE" size=40/>
		<input type="hidden" id="cPaternoE" name="cPaternoE" size=40/>
		<input type="hidden" id="cMaternoE" name="cMaternoE" size=40/>
		<input type="hidden" id="cPuestoE" name="cPuestoE" size=40/>
		<input type="hidden" id="firmanteEla" name="firmanteEla" size=40/>
		<input type="hidden" id="fAplicacion" = name="fAplicacion"/>
		
		<div class="card-header"> <h3> Genera Layout Capitulo Mil </h3> </div>
		<hr class="mt-3"/>
		
		<div class="row d-flex justify-content-center">
		      							
       		<ul class="nav nav-tabs" id="list-opciones">
       			 <li class="nav-item" role="presentation">
	            	<button class="nav-link active" id="tabs-1" data-bs-toggle="tab" data-bs-target="#tabs-1-compromiso" type="button" role="tab" aria-controls="tabs-compromiso" aria-selected="true">Agregar Archivo Compromiso (csv)</button>
	            </li>
	            <li class="nav-item" role="presentation">
	            	<button class="nav-link" id="tabs-2" data-bs-toggle="tab" data-bs-target="#tabs-2-cxp" type="button" role="tab" aria-controls="tabs-cxp" aria-selected="false">Agregar Archivo Nomina Cuenta Por Pagar (csv)</button>
	            </li>								     
	            <li class="nav-item" role="presentation">
	            	<button class="nav-link" id="tabs-3" data-bs-toggle="tab" data-bs-target="#tabs-3-aCompromiso" type="button" role="tab" aria-controls="tabs-aCompromiso" aria-selected="false">Amplia o Reduce Compromiso</button>
	            </li>								            
			</ul>
		
			<div class="row d-flex">
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
					<label id="esperar" style="visibility: hidden">	Espere por favor....
						<img border="0" src="../imagenes/espera.gif" height="30">
					</label>
				</div>
			</div>								
			
			<div class="row d-flex">
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
				</div>								
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="fAplicacion1" class="form-label"> Fecha Aplicación: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<div class="input-group">
						<span class="input-group date"><i class="datepicker1"></i></span>
						<input onchange="valFecha(this)" name="fAplicacion1" type="date" id="fAplicacion1" class="form-control form-control-sm" size="10" />
					</div>	
				</div>								
	       	</div>	
					
			<div class="tab-content mt-3" id="tabContent">	
				<div class="tab-pane fade show active" id="tabs-1-compromiso" role="tabpanel" aria-labelledby="tabs-compromiso">
							
					<div class="row d-flex p-1">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">								
							<label for="flComprometido" class="form-check-label"> Archivo Compromiso </label>
						</div>
						<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">
							<input type="file" id="flComprometido" name="flComprometido" class="form-control form-control-sm" />							
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
							<input type="button" id="btnEnviarComprometido" name="btnEnviarComprometido" class="btn btn-secondary btn-sm" onclick="cargarArchivoCompromiso('flComprometido',this.form.flComprometido.value)" value="Cargar" />
						</div>
					</div>
					
					<div class="row d-flex p-1">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">								
							<label for="flComprometido" class="form-check-label"> Compromiso </label>
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
							<div class="input-group">
								<input type="text" id="BuscarAplicarComprometido" name="BuscarAplicarComprometido" class="form-control AyudaSyC form-control-sm" readonly/>
							</div>							
						</div>
						<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
							<input type="button" id="btnGenerarLayoutComprometido" name="btnGenerarLayoutComprometido" class="btn btn-secondary btn-sm" onclick="cargarArchivoCompromiso('BuscarGenerarLayoutComprometido','BuscarGenerarLayoutComprometido')" value="Generar"/>							
						</div>
					</div>
					
					<div class="row d-flex p-1">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
							<td align="center"><input type="button" id="btnAplicarCompromiso" name="btnAplicarCompromiso" onclick="aplicarContablemente('compromiso')" value="Aplicar" class="btn btn-secondary btn-sm"/></td>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<td align="center"><input type="button" id="btnCancelaCompromiso" name="btnCancelaCompromiso" onclick="updateCancelaCompromiso('compromiso')" value="Cancelar" class="btn btn-secondary btn-sm" /></td>
						</div>
					</div>
					
					<div class="row d-flex p-1">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">								
							<label for="folioSicop" class="form-check-label"> Folio SICOP </label>
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">							
							<input type="text" id="folioSicop" name="folioSicop" class="form-control form-control-sm" onKeyPress="return(onlyNumbers(event))"/>										
						</div>
						<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
							<input type="button" id="btnActualizaFolioSicop" name="btnActualizaFolioSicop" value="Actualizar" class="btn btn-secondary btn-sm"/>
						</div>
					</div>
						
				</div>		
				
				<div class="tab-pane fade" id="tabs-2-cxp" role="tabpanel" aria-labelledby="tabs-cxp">
					<input type="hidden" name="caNoContrarreciboNomina" id="caNoContrarreciboNomina" />
					<input type="hidden" name="nFolioNOMINA" id="nFolioNOMINA" />
					
					<div class="row d-flex p-1">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">								
							<label for="flNomina" class="form-check-label"> Archivo Nomina CXP </label>
						</div>
						<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">
							<input type="file" id="flNomina" name="flNomina" class="form-control form-control-sm" />							
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
							<input type="button" id="btnEnviarNomina" name="btnEnviarNomina" class="btn btn-secondary btn-sm" onclick="cargarArchivoCompromiso('flNomina',this.form.flNomina.value)" value="Cargar" />
						</div>
					</div>
					
					<div class="row d-flex p-1">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">								
							<label for="BuscarAplicarNomina" class="form-check-label"> Aplicar Nomina CXP </label>
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
							<div class="input-group">
								<input type="text" id="BuscarAplicarNomina" name="BuscarAplicarNomina" class="form-control AyudaSyC form-control-sm" readonly/>
							</div>							
						</div>
						<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
							<input type="button" id="btnGenerarLayoutCXP" name="btnGenerarLayoutCXP" class="btn btn-secondary btn-sm" onclick="cargarArchivoCompromiso('btnGenerarLayoutCXP','btnGenerarLayoutCXP')" value="Generar"/>							
						</div>
					</div>
					
					<div class="row d-flex p-1">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
							<td align="center"><input type="button" id="btnAplicarNomina" name="btnAplicarNomina" onclick="aplicarContablemente('nomina')" value="Aplicar" class="btn btn-secondary btn-sm"/></td>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<td align="center"><input type="button" id="btnCancelarCXP" name="btnCancelarCXP" onclick="updateCancelaCXP()" value="Cancelar" class="btn btn-secondary btn-sm" /></td>
						</div>
					</div>
					
					<div class="row d-flex p-1">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">								
							<label for="solicitudPago" class="form-check-label"> Solicitud Pago </label>
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">							
							<input type="text" id="solicitudPago" name="solicitudPago" class="form-control form-control-sm"/>										
						</div>
						<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
							<input type="button" id="btnActualizaSolicitudPago" name="btnActualizaSolicitudPago" value="Actualizar" class="btn btn-secondary btn-sm"/>
						</div>
					</div>
					
					<div class="row d-flex p-1">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">								
							<label for="solicitudPago" class="form-check-label"> Concepto </label>
						</div>
						<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">							
							<textarea name="cConceptoTxt" rows="2" id="cConceptoTxt" rows="2" cols="8" onkeydown="return valida_concepto(event)" class="form-control form-control-sm"></textarea>													
						</div>
					</div>
					
					<div class="row d-flex p-1">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
							<td align="center"><input type="button" id="btnAplicar" name="btnAplicar" onclick="cmdImprimir()" value="Imprimir Poliza" class="btn btn-secondary btn-sm"/></td>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<td align="center"><input type="button" id="btnAnexo" name="btnAnexo" onclick="anexo()" value="Imprimir Anexo" class="btn btn-secondary btn-sm" /></td>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<span id="EditaFirmas" style="visibility:hidden"><a href="#" onclick="updateFirmantes();">Firmas*</a></span>
						</div>
					</div>
										
				</div>
				
				<div class="tab-pane fade" id="tabs-3-aCompromiso" role="tabpanel" aria-labelledby="tabs-aCompromiso">
					
					<div class="row d-flex p-1">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">								
							<label for="BuscarCompromisoAmpliacion" class="form-check-label"> Compromiso Original </label>
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
							<div class="input-group">
								<input type="text" id="BuscarCompromisoAmpliacion" name="BuscarCompromisoAmpliacion" class="form-control AyudaSyC form-control-sm" readonly/>
							</div>							
						</div>
						<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
							Reduccion <input type="checkbox" id="chk_reduccion" name="chk_reduccion" class="form-check-input">							
						</div>
					</div>
					
					<div class="row d-flex p-1">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">								
							<label for="flComprometidoAmpliacion" class="form-check-label"> Archivo Compromiso </label>
						</div>
						<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">
							<input type="file" id="flComprometidoAmpliacion" name="flComprometidoAmpliacion" class="form-control form-control-sm" />							
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
							<input type="button" id="btnEnviarComprometidoAmpliacion" name="btnEnviarComprometidoAmpliacion" class="btn btn-secondary btn-sm" onclick="cargarArchivoCompromiso('flComprometidoAmpliacion',this.form.flComprometidoAmpliacion.value)" value="Cargar" />
						</div>
					</div>
					
					<div class="row d-flex p-1">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">								
							<label for="BuscarAplicarComprometidoAmpliacion" class="form-check-label"> Compromiso Ampliación </label>
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
							<div class="input-group">
								<input type="text" id="BuscarAplicarComprometidoAmpliacion" name="BuscarAplicarComprometidoAmpliacion" class="form-control AyudaSyC form-control-sm" readonly/>
							</div>							
						</div>
						<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
							<input type="button" id="btnGenerarLayoutComprometidoAmpliacion" name="btnGenerarLayoutComprometidoAmpliacion" class="btn btn-secondary btn-sm" onclick="cargarArchivoCompromiso('BuscarGenerarLayoutComprometidoAmpliacion','BuscarGenerarLayoutComprometidoAmpliacion')" value="Generar" />						
						</div>
					</div>
					
					<div class="row d-flex p-1">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
							<td align="center"><input type="button" id="btnAplicarCompromisoAmpliacion" name="btnAplicarCompromisoAmpliacion" onclick="aplicarContablemente('compromisoAmpliacion')" value="Aplicar" class="btn btn-secondary btn-sm"/></td>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<td align="center"><input type="button" id="btnCancelaCompromisoAmpliacion" name="btnCancelaCompromisoAmpliacion" onclick="updateCancelaCompromiso('compromisoAmpliacion')" value="Cancelar" class="btn btn-secondary btn-sm" /></td>
						</div>
					</div>
					
					<div class="row d-flex p-1">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">								
							<label for="folioSicopAmpliacion" class="form-check-label"> Folio SICOP </label>
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">							
							<input type="text" id="folioSicopAmpliacion" name="folioSicopAmpliacion" class="form-control form-control-sm" onKeyPress="return(onlyNumbers(event))"/>										
						</div>
						<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
							<input type="button" id="btnActualizaFolioSicopAmpliacion" name="btnActualizaFolioSicopAmpliacion" value="Actualizar" class="btn btn-secondary btn-sm"/>
						</div>
					</div>					
					
				</div>
			</div>
		</div>
		
	</div>
	
	<div id="txtMesaje"></div>
	
	<div id="dialog" title="Detalles de Carga">
		<fieldset>
			<table id="tblText" align="center">
				<tr>
					<td>
						<textarea rows="14" cols="135" id="txtDif" name="txtDif" style="font-size: 9pt; font-family: Arial" readonly="readonly"></textarea>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
	
	<div id="dialog-firmantesUpdate" title="Actualizacion de Firmantes" class="container">
		<h5> Datos VºBº </h5>
		<hr class="mt-3">
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
				<label for="cNombreVoBoUpdate" class="form-label"> Nombre: </label>		
			</div>
			<div class="col-12 col-lg-9 col-md-9 col-sm-12 d-flex p-1">								
				<input type="text" class="form-control form-control-sm" id="cNombreVoBoUpdate" name="cNombreVoBoUpdate"/>
			</div>
		</div>
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
				<label for="cPaternoVoBoUpdate" class="form-label"> Ap. Paterno: </label>		
			</div>
			<div class="col-12 col-lg-9 col-md-9 col-sm-12 d-flex p-1">								
				<input type="text" class="form-control form-control-sm" id="cPaternoVoBoUpdate" name="cPaternoVoBoUpdate"/>
			</div>
		</div>
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
				<label for="cMaternoVoBoUpdate" class="form-label"> Ap. Materno: </label>		
			</div>
			<div class="col-12 col-lg-9 col-md-9 col-sm-12 d-flex p-1">								
				<input type="text" class="form-control form-control-sm" id="cMaternoVoBoUpdate" name="cMaternoVoBoUpdate"/>
			</div>
		</div>
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
				<label for="cPuestoVoBoUpdate" class="form-label"> Puesto: </label>		
			</div>
			<div class="col-12 col-lg-9 col-md-9 col-sm-12 d-flex p-1">								
				<input type="text" class="form-control form-control-sm" id="cPuestoVoBoUpdate" name="cPuestoVoBoUpdate"/>
			</div>
		</div>
		
		<h5> Datos Autoriza </h5>
		<hr class="mt-3">
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
				<label for="cNombreAutUpdate" class="form-label"> Nombre: </label>		
			</div>
			<div class="col-12 col-lg-9 col-md-9 col-sm-12 d-flex p-1">								
				<input type="text" class="form-control form-control-sm" id="cNombreAutUpdate" name="cNombreAutUpdate"/>
			</div>
		</div>
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
				<label for="cPaternoAutUpdate" class="form-label"> Ap. Paterno: </label>		
			</div>
			<div class="col-12 col-lg-9 col-md-9 col-sm-12 d-flex p-1">								
				<input type="text" class="form-control form-control-sm" id="cPaternoAutUpdate" name="cPaternoAutUpdate"/>
			</div>
		</div>
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
				<label for="cMaternoAutUpdate" class="form-label"> Ap. Materno: </label>		
			</div>
			<div class="col-12 col-lg-9 col-md-9 col-sm-12 d-flex p-1">								
				<input type="text" class="form-control form-control-sm" id="cMaternoAutUpdate" name="cMaternoAutUpdate"/>
			</div>
		</div>
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
				<label for="cPuestoAutUpdate" class="form-label"> Puesto: </label>		
			</div>
			<div class="col-12 col-lg-9 col-md-9 col-sm-12 d-flex p-1">								
				<input type="text" class="form-control form-control-sm" id="cPuestoAutUpdate" name="cPuestoAutUpdate"/>
			</div>
		</div>
		
		<h5> Datos Elabora </h5>
		<hr class="mt-3">
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
				<label for="cNombreElaUpdate" class="form-label"> Nombre: </label>		
			</div>
			<div class="col-12 col-lg-9 col-md-9 col-sm-12 d-flex p-1">								
				<input type="text" class="form-control form-control-sm" id="cNombreElaUpdate" name=cNombreElaUpdate/>
			</div>
		</div>
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
				<label for="cPaternoElaUpdate" class="form-label"> Ap. Paterno: </label>		
			</div>
			<div class="col-12 col-lg-9 col-md-9 col-sm-12 d-flex p-1">								
				<input type="text" class="form-control form-control-sm" id="cPaternoElaUpdate" name="cPaternoElaUpdate"/>
			</div>
		</div>
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
				<label for="cMaternoElaUpdate" class="form-label"> Ap. Materno: </label>		
			</div>
			<div class="col-12 col-lg-9 col-md-9 col-sm-12 d-flex p-1">								
				<input type="text" class="form-control form-control-sm" id="cMaternoElaUpdate" name="cMaternoElaUpdate"/>
			</div>
		</div>
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
				<label for="cPuestoElaUpdate" class="form-label"> Puesto: </label>		
			</div>
			<div class="col-12 col-lg-9 col-md-9 col-sm-12 d-flex p-1">								
				<input type="text" class="form-control form-control-sm" id="cPuestoElaUpdate" name="cPuestoElaUpdate"/>
			</div>
		</div>
		
	</div>

</form>			
 </body>
</html>
