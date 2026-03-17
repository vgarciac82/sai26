<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	String msg = "";
	if (session.getAttribute("RESULT") != null) {
		msg = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
	
	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	String efa = efbl.getEjercicioFiscalActivo().getaEjercicioFiscal();
	
	String destinoGasto = StringUtils.trimToEmpty( request.getParameter("DESTINO_GASTO") );
	String rfcPago = StringUtils.trimToEmpty( request.getParameter("RFC") );
	String folio = StringUtils.trimToEmpty( request.getParameter("FOLIO") );
	
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Validar Facturas</title>
	
		<!-- Estilos estandar para los controles JQuery -->
		<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
		<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
		<link rel="stylesheet" type="text/css"	href="css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
		
		<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
		
		
		<script type="text/javascript" src="js/jquery-3.5.1.min.js"></script>
		<script type="text/javascript" src="js/bootstrap.min.js"></script>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
		<script type="text/javascript">
				var msg = "<%=msg%>";
				var efa = <%=efa%>;
				var destinoGasto = "<%=destinoGasto%>";
				var RFC = "<%=rfcPago%>";
				var folio = "<%=folio%>"; 	
				var esComprobacion = false;
				var esComprobacionConFacturas = false;
				var esComisionNacional = false;
				var esComisionInternacional = false;
				var esCertificadoTransito = false;
				var esPagoAlimentacion = false;
				var contieneNoComprobable = false;
				var esPagoProveedor = false;
				var destinoPagoSinFactura = [
				  							 "CCRE", "2NRP", "CERG",
											 "CSSU", "GCRG", "GLRE",
											 "NORN", "RCRE", "CBRB",
											 "2NRE", "2NFA", "SPRE"
											];
				var destinoPagoComprobacion = ["CERE","GCRE","VDRE"];
				var tipoJustificacion ="0";
				
				$(document).ready(
					function(){
					
						$("#RFC").val(RFC);
						$("#aEjercicioFiscal").val(efa);
						$("#destinoGasto").val(destinoGasto);
						$("#nFolioPago").val(folio);
						
						$("#btnAceptar").button();
						$("#btnAceptar").button().click(function(){parent.togleDivFacts(0);});
						$("#limpia1").button();
						$("#limpia2").button();
						$("#limpia3").button();
						$("#limpia4").button();
						$("#limpia5").button();
						$("#limpia6").button();
						$("#limpia7").button();
						
						$("#sendButton").button().click(
							function(){
								$.blockUI({message: "Procesando espere ......"});
								submit();
							}
						);
						
						
						
						if( msg != ""){
							$("#msgDialog").show();
							$("#uploadDiv").hide();
							$("#cargaComprobacion").hide();
						}else{
							$("#msgDialog").hide();
							$("#uploadDiv").show();
						}
						
						esComprobacion = determinaEsComprobacion();
						
						if( esComprobacion ){
							
							queryFormPost( {
								queryName : "tipoDocumentacionRGRead",
								async : false,
								callback : function() {
									if( "true" == $("#esPagoAlimentacion").val()  ){
										$("#Alimentacion").attr("checked", true);
										$('input[name=tipoComision]:checked').click();
										leeInformacionAlimentacion();
									}else{
									
										if( "true" == $("#esComisionInternacional").val() ){
											$("#Extranjero").attr("checked", true);
											$('input[name=tipoComision]:checked').click();
											leeInformacionExtranjero();
										}else{
											$('input[name=tipoComision]:checked').click();
										}
										
										if( "true" == $("#esCertificadoTransito").val() ){
											leeInformacionCertTrans();
										}
										
										if( "true" == $("#contieneNoComprobable").val() ){
											leeInformacionSinFactura();
										}
									}
								}
							});
						}else{
						
							
							$("#cargaComprobacion").css("display", "none");
							
							var facturasCargadas = pagoConFacturas();
							var oficioCapturado = pagoConOficio();
							
							if( facturasCargadas ){
								$("#pagoConFacturasTR").css("display", "block");
								$("#tipoPago").css("display", "none");
							}else if (oficioCapturado ){
								$("#pagoConFacturasTR").css("display", "none");
								$("#tipoPago").css("display", "none");
								cargaInformacionOficio();
								$("#oficioFileUploadTR").css("display", "none");
							}else{
								if( rfcSinFacturas() || destinoGastoSinFactura() ){
									$("#tipoPago").css("display", "block");
								}else{
									
									$("#tipoPago").css("display", "none");
								}								
							}
						}
					}
				);
				
				
				function submit(){
					var continuar = false;
					continuar = validarMinimoCapturado();
					if( continuar ){
						continuar = continuar & validaComprobacionFacturas();
						continuar = continuar & validaComprobacionExtranjero();
						continuar = continuar & validaCertificadoDeTransito();
						continuar = continuar & validaSinComprobante();
					}
					
					if( contieneNoComprobable ){
						showMensajeNoComprobable();
					}
						
					if( continuar){
						Swal.fire({
							  title: '¿Desea continuar?',
							  text: "Se enviará la informacion al servidor",
							  icon: 'warning',
							  showCancelButton: true,
							  confirmButtonColor: '#288BA8',
							  cancelButtonColor: '#e6e6e6',
							  confirmButtonText: 'Aceptar',
							  cancelButtonText: 'Cancelar'
							}).then((result) => {
							  if (result.isConfirmed) {
								  	$.blockUI({message: "Procesando espere ......"});
								  	/* Actualiza Justificacion Facturas*/
									if (!$("#archivoAutNoComprobable").val() == "" ) {
										queryFormPost("consultaTieneJustificacion", {async: false});
										
										if ($("#existeJustificacion").val() > 0 ) {
											queryFormPost("actualizaJustificacionFactura", {async: false});
							 	   		
										} else {
											queryFormPost("insertaJustificacionFactura", {async: false});
							 	   		}
									} 
									$( "#submitFrm" ).submit();
							  } else {
								  $.unblockUI();
								  return;
							  }
							})
						
					} else {
						$.unblockUI();
					}
				}
				
				function validaSinComprobante(){
				
					var continuar = false;
					var msg = "";
					var existePrevio = $("#informacionSinFacturaExistente").val() == "true";
					continuar = existePrevio;
					
					if( parseFloat( $("#montoNoComprobable").val() ) == 0 && $("#archivoAutNoComprobable").val() == "" && $("#justificacionNoComprobable").val() == "" && $("#justificacionNoComprobable2").val() == "" ){
						contieneNoComprobable = false;
						continuar = true;
					}else{
						if( !existePrevio ){
							if( parseFloat( $("#montoNoComprobable").val() ) == 0 )
								msg += "\nDebe indicar el monto no comprobable";
							if ($("#justificacionNoComprobable").val() == "" && $("#justificacionNoComprobable2").val() == "") {
								msg += "\nDebe capturar una Justificacion de importe no Comprobable"
							}
							if(  $("#archivoAutNoComprobable").val() == "" || (  $("#archivoAutNoComprobable").val() != "" && !extensionArchivo(  $("#archivoAutNoComprobable").val(), "pdf"  )  )  )
								msg += "\nDebe seleccionar un archivo PDF con la justificacion de gastos sin factura."; 
								
							if( msg.length > 0 ){
								continuar = false;
								contieneNoComprobable = false;

								Swal.fire("Atención:", msg ,"info");
								$.unblockUI();

							}else{
								contieneNoComprobable = true;
								continuar = true;
							}
						}
					}
					
					return continuar;
				}
				
				function validaCertificadoDeTransito(){
					var continuar = true;
					var msg = "";
					var existePrevio = $("#informacionCertTransExistente").val() == "true";
					
					if(  $("#noCertificado").val() == "" && parseFloat( $("#montoCertificado").val() ) == 0 && $("#archivoOficioTransito").val() == ""  ){
						continuar = true;
						esCertificadoTransito = false;
					}else{
						if( !existePrevio ){
							if(  $("#noCertificado").val() == "" ) 
								msg += "\nDebe indicar el No. de Oficio del Certficado de Transito.\n";
							if( parseFloat( $("#montoCertificado").val() ) == 0 )
								msg += "Debe indicar el monto del certificado de transito.\n";
							if( $("#archivoOficioTransito").val() == "" || ( $("#archivoOficioTransito").val() != "" && !extensionArchivo( $("#archivoOficioTransito").val(), "pdf" ) ) )
								msg += "Debe seleccionar un archivo PDF con el certificado de transito digitalizado.\n";
								
							if( msg.length > 0 ){
								continuar = false;
								esCertificadoTransito = false;

								Swal.fire("Atención", msg ,"info");
								$.unblockUI();

							}else{
								esCertificadoTransito = true;
							}
						}
					}
					
					return continuar;
				}
				
				function validaComprobacionExtranjero(){
					
					var tipoComprobacion = $('input[name=tipoComision]:checked').val();
					var continuar = true;
					
					var existePrevio = $("#informacionExtrajeroExistente").val() == "true";
					
					if( parseInt( tipoComprobacion, 10)  == 1 && !existePrevio){
						if( parseFloat( $("#montoComisionExtranjero").val() ) == 0 || $("#archivoComisionExtranjero").val() == "" ){

							Swal.fire("Verifique!","Cuando indica comision en el extranjero es requerido el No. de oficio, el monto debe ser mayor a cero y debe adjuntar el oficio en formato PDF","info");
							$.unblockUI();
							continuar = false;
						}else if( $("#archivoComisionExtranjero").val() != "" && !extensionArchivo( $("#archivoComisionExtranjero").val(), "pdf" ) ){
							Swal.fire("Atención","El oficio a adjuntar debe ser PDF.","info");
							$.unblockUI();

							continuar = false;
						}
					}
					
					return continuar;
					
				}
				
				function validaComprobacionFacturas(){
					var correcto = false;
					
					if( $("#archivoZipFacturas").val() == "" ){
						correcto = true;
						esComprobacionConFacturas = false;
					}else{
						if( extensionArchivo( $("#archivoZipFacturas").val(), "zip") ){
							correcto = true;
							esComprobacionConFacturas = true;
						}else{ 
							Swal.fire("Solo se admiten archivos zip","Selecciono un archivo de carga de facturas que no es permitido.","info");
							$.unblockUI();

							esComprobacionConFacturas = false;
						}
					}
					
					return correcto;
				}
				
				function extensionArchivo( nombreArchivo, extensionPermitida ){
					var extension = ( nombreArchivo.substring( nombreArchivo.lastIndexOf(".") + 1 ) ).toLowerCase();
					return extension === extensionPermitida.toLocaleLowerCase();  
				}
				
				function focusMoney( componente ) {
					$( componente ).select();
				}
	
				function blurMoney( componente ) {
	
					if( $( componente ).val() == "" )
						$( componente ).val( "0.00" );
					
					if( $( componente ).hasClass("OficioPP") ){
					
						Sinfrmt( $( "#montoBrutoPP" )[0] );
						Sinfrmt( $( "#montoImpuestosPP" )[0] );
						Sinfrmt( $( "#montoNetoPP" )[0] );
						
						var montoBruto = Number( $( "#montoBrutoPP" ).val() );
						var montoImpuesto = Number( $( "#montoImpuestosPP" ).val() );
						var montoNeto = montoBruto + montoImpuesto;
						$( "#montoNetoPP" ).val( montoNeto.toFixed(2) );
						
						$( "#montoBrutoPP" ).formatCurrency();
						$( "#montoImpuestosPP" ).formatCurrency();
						$( "#montoNetoPP" ).formatCurrency();
					}
					
					$( componente ).formatCurrency();
	
				}
	
				function onlyNumbers( evt ) {
					var keyPressed = ( evt.which ) ? evt.which : event.keyCode;
					var strCheck = '-0123456789.';
	
					var key = String.fromCharCode( keyPressed );
					if( strCheck.indexOf( key ) == -1 )
						return false; // Valida que sea numero y punto decimal
	
					return true;
				}
		
				function Sinfrmt( fld ) {
					var valcol = fld.value;
					
					if( valcol == "" )
						valcol = "0.00";
						
					valcol = valcol.replace( /[$]/g, "" );
					valcol = valcol.replace( /,/g, "" );
					$( "#" + fld.id ).val( valcol );
				}
					
				function cambiaTipoComision(){
				
					var tipoComision = parseInt( $('input[name=tipoComision]:checked').val(), 10 );
					switch (tipoComision){
						//Nacional
						case 0:	
							esComisionNacional = true;
							esComisionInternacional = false;
							esPagoAlimentacion = false;
							
							
							$("#uploadDiv").css("display", "block");
							$("#comisionExtranjeroTR").css("display","none");
							$("#TipoCarga").val("COMPROBACION");
							limpiar('Extranjero');
							limpiar('Alimentacion');
						break;
						case 1:
							esComisionNacional = false;
							esComisionInternacional = true;
							esPagoAlimentacion = false;
							
							
							$("#uploadDiv").css("display", "block");
							$("#comisionExtranjeroTR").css("display","block");
							$("#TipoCarga").val("COMPROBACION");
							
							limpiar('Extranjero');
							limpiar('Alimentacion');
						break;
						
					}
				}
					
				function limpiar(clase){
					$("." + clase).each(
						function(){
							if( $( this ).hasClass( "Money" ) ){
								$(this).val("0.00");
								$(this).blur();
							}else if( $(this).attr("type") == "file" ){
								$(this).after($(this).clone(true)).remove();
							}else{
								$(this).val("");
							}
						}
					);
				}
				
				function validarMinimoCapturado(){
					var continuar = true;
					if( sinInformacionCapturada() ){
						 esCertificadoTransito = false;
						 contieneNoComprobable = false;

						Swal.fire("Revise","Debe ingresar la informacion referente al monto y documentacion soporte de la comprobacion o las facturas que la amparen.","info");

						continuar = false;
					}
					return continuar;
				}
				
				function sinInformacionCapturada(){
				
					var capturaVacia = true;
					retirarFormatoMoneda();
					
					$(".Validable").each(function(){
						if( !$(this).attr("disabled") ){
							if( $(this).hasClass( "Money" ) ){
								var valor = parseFloat( $(this).val() );
								if( valor !=  0 )
									capturaVacia = false;
							}else if(  ( $(this).attr("type") == "file" || $(this).attr("type") == "text" ) && $(this).val() != "" )
								capturaVacia = false;
						}
					});
					
					return capturaVacia;					
				}
			
				function retirarFormatoMoneda(){
					$(".Money").each(
						function(){
							Sinfrmt( $(this)[0] );
						}
					);
				}
				
				function leeInformacionExtranjero(){
				
					queryFormPost({
						queryName:"facturaComisionExtRead", 
						async:false,
						callback:function() {
							$("#informacionExtrajeroExistente").val("true");
							$("#Nacional")[0].disabled = true;
							$("#Extranjero")[0].disabled = true;
							
							$("#folioComisionExtranjero")[0].disabled = true;
							$("#montoComisionExtranjero")[0].disabled = true;
							$("#archivoComisionExtranjero")[0].disabled = true;
							
							$("#folioComisionExtranjero").addClass("disabledInput");
							$("#montoComisionExtranjero").addClass("disabledInput");
							$("#uploadArchivoExtranjeroTR").css("visibility", "hidden");
							
							
							$("#comisionNacionalTD").css("display","none");
						}
					});
					
				}
				
				function leeInformacionCertTrans(){
				
					queryFormPost({
						queryName:"facturaCertTransRead", 
						async:false,
						callback:function() {
						
							$("#informacionCertTransExistente").val("true");
							
							
						}
					});
					
				}
				
				function leeInformacionSinFactura(){
				
					queryFormPost({
						queryName:"facturaNoComprobablesRead", 
						async:false,
						callback:function() {
						
							$("#informacionSinFacturaExistente").val("true");
							
							
						}
					});
					
				}
				
				function leeInformacionAlimentacion(){
				
					queryFormPost({
						queryName:"RGAlimentacionRead", 
						async:false,
						callback:function() {
							$("#informacionAlimentacionExistente").val("true");
							
						}
					});
					
				}
				
				function determinaEsComprobacion(){
					var esComprobacion = false;
					for( i = 0; i < destinoPagoComprobacion.length; i++ )
						if( destinoPagoComprobacion[i] == destinoGasto ){
							esComprobacion = true;
							break;
						}
					return esComprobacion;
				}
				
				function destinoGastoSinFactura(){
					
					var esDGSinComprobacion = false;
					
					for( i = 0; i < destinoPagoSinFactura.length; i++ )
						if( destinoPagoSinFactura[i] == destinoGasto ){
							esDGSinComprobacion = true;
							break;
						}
						
					return esDGSinComprobacion;
					
				}
				
				function rfcSinFacturas(){
					var permitePagoOficio = false;
					
					$("#pagoXOficioPermitido").val("0");
					
					queryFormPost({
						queryName: "permitePagoOficio",
						async: false,
						callback: function(){
							if( parseInt( $("#pagoXOficioPermitido").val(), 10)  > 0 )
							permitePagoOficio = true; 
						}
					});
					
					return permitePagoOficio;
				}
				
				
				
				function pagoConFacturas(){
					var facturasCapturadas = false;
					
					queryFormPost({
						queryName:"facturasCapturadasRGRead",
						async:false,
						callback:function(){
							if( parseInt( $("#nFacturasCapturadas").val(), 10 ) > 0 )
								facturasCapturadas = true;
						}
					});
					
					return facturasCapturadas;
				}
				
				function validaOficioPagoProv(){
					var msg = "";
					if(  $("#archivoOficioPP").val() == "" || !extensionArchivo( $("#archivoOficioPP").val(), "pdf" )  )
						msg += "Debe seleccionar un archivo PDF con el oficio de comprobacion.\n";
					if( parseFloat(  $("#montoBrutoPP").val() ) <= 0 )
						msg += "Debe ingresar el monto bruto a pagar.\n";
					if( parseFloat(  $("#montoImpuestosPP").val() ) <= 0 && rfcImpuestoForzoso() )
						msg += "Debe ingresar el monto de impuestos.\n";
					
					return msg;
				}
				
				function pagoConOficio(){
					var oficioCapturado = false;
					
					queryFormPost({
						queryName:"OficioCapturadoRG",
						async:false,
						callback:function(){
							if( parseInt( $("#nOficioCapturado").val(), 10 ) > 0 )
								oficioCapturado = true;
						}
					});
					
					return oficioCapturado;
				}
				
				function cargaInformacionOficio(){
					queryFormPost({
						queryName:"InfoOficioCapturadoRG",
						async:false,
						callback:function(){
							$(".OficioPP").each(
								function(){
									if( $(this).hasClass("Money") ){
										$(this).blur();
										$(this).attr("readOnly", "readOnly");
										$(this).addClass("notEditable");
									}else if( $(this).attr("type") == "file" ){
										$(this).attr("disabled", "disabled" );
									}
								}
							);
						}
					});
				}
				
				function showMensajeNoComprobable(){
					alert( "¡¡Advertencia!!"   + 
						   "\nLa comprobación sin la carga del XML es responsabilidad de quien erogó el gasto."  
					        + "\nCon fundamento en la \"Ley Federal de Transparencia y Acceso a la Información Pública Gubernamental\" y la Ley General de Transparencia" +
					       "\n\" y Acceso a la Información Pública\" estos documentos deben cargarse testados en lo referente a información confidencial "+
					       "\n y con la leyenda de la Coordinación General Jurídica.");
					       
					
				}
				
				function rfcImpuestoForzoso(){
					var esImpuestoForzoso = false;
					
					$("#esImpuestoForzoso").val("0");
					
					queryFormPost({
						queryName: "permitePagoSinImpuestos",
						async: false,
						callback: function(){
							if( parseInt( $("#esImpuestoForzoso").val(), 10)  > 0 )
								esImpuestoForzoso = true; 
						}
					});
					
					return esImpuestoForzoso;
				}
				
			</script>
	</head>
	
	<body id="dt_example">
		<div id="container" class="container">
			<form action="../uploadFacturasRG" method="post" enctype="multipart/form-data" id="submitFrm">
			
				<input type="hidden" id="informacionAlimentacionExistente"  name="informacionAlimentacionExistente" value="false" />
				<input type="hidden" id="informacionExtrajeroExistente"  name="informacionExtrajeroExistente" value="false" />
				<input type="hidden" id="informacionCertTransExistente"  name="informacionCertTransExistente" value="false" />
				<input type="hidden" id="informacionSinFacturaExistente"  name="informacionSinFacturaExistente" value="false" />
				
				<input type="hidden" id="esComprobacionConFacturas"  name="esComprobacionConFacturas" value="false" />
				<input type="hidden" id="esComisionNacional"  name="esComisionNacional" value="false" />
				<input type="hidden" id="esComisionInternacional"  name="esComisionInternacional" value="false" />
				<input type="hidden" id="esCertificadoTransito"  name="esCertificadoTransito" value="false" />
				<input type="hidden" id="esPagoAlimentacion"  name="esPagoAlimentacion" value="false" />
				<input type="hidden" id="pagoXOficioPermitido"  name="pagoXOficioPermitido" value="0" />
				<input type="hidden" id="esImpuestoForzoso"  name="pagoXOficioPermitido" value="0" />
				<input type="hidden" id="contieneNoComprobable"  name="contieneNoComprobable" value="false" />
				<input type="hidden" id="esPagoProveedor"  name="esPagoProveedor" value="false" />
				<input type="hidden" id="aEjercicioFiscal"  name="aEjercicioFiscal" />
				<input type="hidden" id="destinoGasto"  name="destinoGasto" />
				<input type="hidden" id="viaticos" name="viaticos" value="TRUE"/>
				<input type="hidden" id="tipo_pago" name="tipo_pago" value="RELACIONGASTOS"/>
				<input type="hidden" id="RFC" name="RFC" value=""/>
				<input type="hidden" id="nFolioPago" name="nFolioPago" value=""/>
				<input type="hidden" id="folioCaso" name="folioCaso" value=""/>
				<input type="hidden" id="existeJustificacion" name="existeJustificacion" value=""/>
				<input type="hidden" id="nFacturasCapturadas" name="nFacturasCapturadas" value="0"/>
				<input type="hidden" id="nOficioCapturado" name="nOficioCapturado" value="0"/>
				<input type="hidden" id="TipoCarga" name="TipoCarga" value="COMPROBACION"/>
				
				<div id="msgDialog" title="Resultado de Carga">
					<fieldset>
						<legend>Resultado de carga.</legend>
						<table align="center">
							<tr>
								<td align="center">
									<textarea class="form-control"rows="10" cols="40" id="msgTxt"><%=msg.replaceAll( "<br>", "\n*" )%></textarea>
								</td>
							</tr>
							<tr>
								<td align="right">
									<input type="button" id="btnAceptar" value="Aceptar" class="btnInterfaceBG"/>
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
				
				<div id="cargaComprobacion">
					<div>
						<table width="100%">
							<tr>
								<td align="left">
									<table>
										<tr>
											<td colspan="3">
												<a href="#" onclick="parent.togleDivFacts(0);return false;">Ver informacion cargada</a>
											</td>
										</tr>
										<tr>
											<td id="comisionNacionalTD" ><input type="radio" name="tipoComision" id="Nacional" value="0" checked="checked" onclick="cambiaTipoComision()"> <label for="Nacional">Comisi&oacute;n Nacional</label></td>
											<td id="comisionExtranjerolTD"><input type="radio" name="tipoComision" id="Extranjero" value="1" onclick="cambiaTipoComision()"> <label for="Extranjero">Comisi&oacute;n al Extranjero</label></td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</div>
				
					<div>
						<div id="uploadDiv">
							<div class="card" id="comisionNacionalTR">
								<div class="card-header">
									<h5><b>Carga de Facturas</b></h5>
								</div>
								 <div class="card-body">
								 	<div class="row">
								 		<div class="col-12">
								 			<p>Adjunte el archivo <b>ZIP</b> que contiene las facturas de su comprobaci&oacute;n.</p>
								 		</div>
									 	<div class="col-12">
									 		<input type="file" value="" id="archivoZipFacturas" name="archivoZipFacturas" class="Facturas Validable form-control"/>
									 	</div>
									 	<div class="d-flex justify-content-center mt-2">
									 		<input type="button" value="Limpiar" id="limpia1" onclick="limpiar('Facturas')" class="btn btn-secondary"/>
									 	</div>
								 	</div>
								 </div>
							</div>
							
							<div class="card" id="comisionExtranjeroTR" style="display: none">
								<div class="card-header">
									<h5><b>Carga de anexo comision al extranjero</b></h5>
								</div>
								 <div class="card-body">
								 	<div class="row">
									 	<div class="col-12">
									 		<p>Adjunte el archivo <b>PDF</b> que contiene el anexo de comision al extranjero.</p>
									 	</div>
									 </div>
									 <div class="row">
									 	<div class="col-6">
									 		<label for="folioComisionExtranjero"> No. de Oficio: </label>
									 		<input type="text" id="folioComisionExtranjero" name="folioComisionExtranjero" value="OFICIO_EXTRANJERO" size="15"  class="Extranjero form-control">
									 	</div>
									 	<div class="col-6">
									 		<label for="montoComisionExtranjero"> Monto de comsion en el extranjero: </label>
									 		<input type="text" id="montoComisionExtranjero" name="montoComisionExtranjero" value="0.00" size="10" onfocus="focusMoney(this)" onkeypress="return onlyNumbers(event)" onblur="blurMoney(this)" class="Extranjero Money Validable form-control">
									 	</div>
									 </div>
									 <div class="row mt-1" id="uploadArchivoExtranjeroTR">
									 	<div class="col-12" >
									 		Archivo *.PDF:
											<input type="file" value="" id="archivoComisionExtranjero" name="archivoComisionExtranjero" class="form-control Extranjero Validable"/>
									 	</div>
									 </div>
									 <div class="row mt-1">
									 	<div class="d-flex justify-content-center">
									 		<input type="button" value="Limpiar" id="limpia2" onclick="limpiar('Extranjero')" class="btn btn-secondary"/>
									 	</div>
									 </div>
								</div>
							</div>
							
							<div class="card" id="certificado" >
									<div class="card-header">
										<h5><b>Certificado de tr&aacute;nsito</b></h5>
									</div>
									 <div class="card-body">
									 	<div class="row">
										 	<div class="col-12">
										 	 	Capturar solo si cuenta con certificado de transito. Debe adjuntar un archivo PDF.</p>
										 	</div>
										 </div>
										 <div class="row">
										 	<div class="col-6">
										 		<label for="noCertificado"> No. Oficio de Certificado de Transito: </label>
										 		<input type="text" name="noCertificado" id="noCertificado" size="12" value="" class="Certificado Validable form-control">
										 	</div>
										 	<div class="col-6">
										 		<label for="montoCertificado">Monto del oficio: </label>
										 		<input type="text" id="montoCertificado" name="montoCertificado" value="0.00" size="10" onfocus="focusMoney(this)" onkeypress="return onlyNumbers(event)" onblur="blurMoney(this)" class="form-control Certificado Money Validable">
										 	</div>
										 </div>
										 <div class="row mt-1">
										 	<div class="col-12">
										 		<label for="archivoOficioTransito"> Archivo *.PDF: </label>
										 		<input type="file" value="" id="archivoOficioTransito" name="archivoOficioTransito" class="Certificado Validable form-control"/> 
										 	</div>
										 </div>
										 <div class="row mt-2">
										 	<div class="d-flex justify-content-center">
										 		<input type="button" value="Limpiar" id="limpia3" onclick="limpiar('Certificado')" class="btn btn-secondary"/>
										 	</div>
										</div>
									</div>
							</div>
							
							<div class="card">
								<div class="card-header">
									<h5><b>Carga de monto no facturado</b></h5>
								</div>
								 <div class="card-body">
								 	<div class="row">
									 	<div class="col-12">
									 		<p>Seleccione el archivo <b>PDF</b> que contiene sus comprobantes de montos sin facturas.<p>
									 	</div>
								 	</div>
								 	<div class="row">
									 	<div class="col-6">
									 		<label for="montoNoComprobable"> Monto no comprobable: </label>
									 		<input type="text" id="montoNoComprobable" name="montoNoComprobable" value="0.00" size="10" onfocus="focusMoney(this)" onkeypress="return onlyNumbers(event)" onblur="blurMoney(this)" class="form-control Money NoComprobable Validable">
									 	</div>
									 	<div class="col-6">
									 		<label for="archivoAutNoComprobable"> Archivo *.PDF: </label>
									 		<input type="file" value="" id="archivoAutNoComprobable" name="archivoAutNoComprobable" class="NoComprobable Validable form-control"/> 
									 	</div>
								 	</div>
								 	<div class="row mt-1">
									 	<div class="col-12">
									 		<label for="justificacionNoComprobable"> 1)	Justificación por falta de facturas por cierre de mes (ejemplo casetas):</label>
									 		<textarea type="text" id="justificacionNoComprobable" name="justificacionNoComprobable" value="" rows="3"  class="form-control NoComprobable" maxlength="750"></textarea>
									 	</div>
								 	</div>
								 	<div class="row mt-1">
									 	<div class="col-12">
									 		<label for="justificacionNoComprobable2"> 2) Justificación por falta de facturación:</label>
									 		<textarea type="text" id="justificacionNoComprobable2" name="justificacionNoComprobable2" value="" rows="3"  class="form-control NoComprobable" maxlength="750"></textarea>
									 	</div>
								 	</div>
								 	<div class="row mt-2">
								 		<div class="d-flex justify-content-center">
								 			<input type="button" value="Limpiar" id="limpia4" onclick="limpiar('NoComprobable')" class="btn btn-secondary"/>
								 		</div>
								 	</div>
								 </div>
							</div>	
							
							<div class="row mt-2">
								<div class="d-flex justify-content-center">
									<input type="button" value="Enviar Archivo" alt="De click en este boton para enviar las facturas a validacion." id="sendButton" class="btn btn-primary"/>
								</div>
							</div>
						</div>
						
					</div>
				</div>
				
			</form>
		</div>
	</body>
</html>