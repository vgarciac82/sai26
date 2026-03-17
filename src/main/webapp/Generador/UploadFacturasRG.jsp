<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	int nFolioPago = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
	String tipoPago = c.getTipoCaso().getGavetaAsociada();

	String msg = "";
	if (session.getAttribute("RESULT") != null) {
		msg = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
	
	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	String efa = efbl.getEjercicioFiscalActivo().getaEjercicioFiscal();
	
	String destinoGasto = StringUtils.trimToEmpty( request.getParameter("DESTINO_GASTO") );
	String rfcPago = StringUtils.trimToEmpty( request.getParameter("RFC") );
	
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
		<link rel="stylesheet" type="text/css" href="../css/interfaz.css"/>
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
				var tipoPago = '<%=tipoPago%>';
				var nFolioPago = <%=nFolioPago%>;
				var msg = "<%=msg%>";
				var efa = <%=efa%>;
				var destinoGasto = "<%=destinoGasto%>";
				var RFC = "<%=rfcPago%>"; 
				
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
											 "2NRE", "2NFA", "SPRE",
											 "NARG"
											];
				var destinoPagoComprobacion = ["CERE","GCRE","VDRE"];
				
				$(document).ready(
					function(){
					
						$("#RFC").val(RFC);
						$("#tipo_pago").val(tipoPago);
						$("#nFolioPago").val(nFolioPago);
						$("#aEjercicioFiscal").val(efa);
						$("#destinoGasto").val(destinoGasto);
						
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
						
					
						$("#enviarPagoProveedorBtn").button().click(
							function(){
								submitPagoProveedor();
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
										$('input[name=tipoComision]:checked').click();
										
										if( "true" == $("#contieneNoComprobable").val() ){
											leeInformacionSinFactura();
										}
									
								}
							});
						}else{
						
							$("#cargaPagoProveedor").css("display", "block");
							$("#cargaComprobacion").css("display", "none");
							
							var facturasCargadas = pagoConFacturas();
							var oficioCapturado = pagoConOficio();
							
							if( facturasCargadas ){
								$("#pagoConOficioTR").css("display", "none");
								$("#pagoConFacturasTR").css("display", "block");
								$("#tipoPago").css("display", "none");
							}else if (oficioCapturado ){
								$("#pagoConOficioTR").css("display", "block");
								$("#pagoConFacturasTR").css("display", "none");
								$("#tipoPago").css("display", "none");
								$("#enviarPagoProveedorBtn").css("display", "none");
								cargaInformacionOficio();
								$("#oficioFileUploadTR").css("display", "none");
							}else{
								if( rfcSinFacturas() || destinoGastoSinFactura() ){
									$("#tipoPago").css("display", "block");
								}else{
									$("#pagoConOficioTR").css("display", "none");
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
									$( "#submitFrm" ).submit();
							  } else {
								  $.unblockUI();
								  return;
							  }
							})
						
					}
				}
				
				function validaSinComprobante(){
				
					var continuar = false;
					var msg = "";
					var existePrevio = $("#informacionSinFacturaExistente").val() == "true";
					continuar = existePrevio;
					
					if( parseFloat( $("#montoNoComprobable").val() ) == 0 && $("#archivoAutNoComprobable").val() == "" ){
						contieneNoComprobable = false;
						continuar = true;
					}else{
						if( !existePrevio ){
							if( parseFloat( $("#montoNoComprobable").val() ) == 0 )
								msg += "\nDebe indicar el monto no comprobable";
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
							$("#uploadDiv").css("display", "block");
							$("#TipoCarga").val("COMPROBACION");
							
						break;
						case 1:
							esComisionNacional = false;
							esComisionInternacional = true;
							$("#uploadDiv").css("display", "block");
							$("#TipoCarga").val("COMPROBACION");
							
						break;
						case 2:
							esComisionNacional = false;
							esComisionInternacional = false;
							limpiar('Facturas');
							limpiar('Certificado');
							limpiar('NoComprobable');
							$("#uploadDiv").css("display", "none");
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
				
				
				function leeInformacionSinFactura(){
				
					queryFormPost({
						queryName:"facturaNoComprobablesRead", 
						async:false,
						callback:function() {
						
							$("#informacionSinFacturaExistente").val("true");
							
							$("#montoNoComprobable")[0].disabled = true;
							$("#montoNoComprobable").addClass("disabledInput");
							$("#archivoAutNoComprobableTR").css("display","none");

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
				
				function tipoPagoProveedor(){
				
					var idChecked = $('input[name=PagoProveedor]:checked').attr("id");
					limpiar("OficioPP");
					limpiar("FacturasPP");
					
					if( "PPOficio" == idChecked ){
						$("#pagoConFacturasTR").css("display", "none");
						$("#pagoConOficioTR").css("display", "block");
					}else{
						$("#pagoConFacturasTR").css("display", "block");
						$("#pagoConOficioTR").css("display", "none");
					}
					
				}
				
				function submitPagoProveedor(){
					
					var continuar = false;
					var idChecked = $('input[name=PagoProveedor]:checked').val();
					var mensaje = "";
					retirarFormatoMoneda();
					
					if( "PPFactura" == idChecked ){
						if( $("#archivoZipFacturasPP").val() == "" || !extensionArchivo( $("#archivoZipFacturasPP").val(), "zip" ) ){

							Swal.fire( "Archivo ZIP","Debe seleccionar el archivo con la(s) factura(s) a pagar y debe ser en formato ZIP","info" );
						}else{
							
							$("#TipoCarga").val("FACTURA_PROVEEDOR");
							$.blockUI({message: "Procesando espere ......"});
							$("#submitFrm").submit();
						}
						
					}else if( "PPOficio" == idChecked){
						mensaje = validaOficioPagoProv();
						if( $.trim(mensaje) == "" ){
							$("#TipoCarga").val("OFICIO_PROVEEDOR");
							$.blockUI({message: "Procesando espere ......"});
							$("#submitFrm").submit();

						}else {
							Swal.fire("Antes de enviar corrija lo siguiente: ", mensaje , "info");
							$.unblockUI();
						}

					}
						
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
			<h1>Recepción de facturas</h1>
			<form action="../uploadFacturasRG" method="post" enctype="multipart/form-data" id="submitFrm">
			
				<input type="hidden" id="informacionAlimentacionExistente"  name="informacionAlimentacionExistente" value="false" />
				<input type="hidden" id="informacionExtrajeroExistente"  name="informacionExtrajeroExistente" value="false" />
				<input type="hidden" id="informacionCertTransExistente"  name="informacionCertTransExistente" value="false" />
				<input type="hidden" id="informacionSinFacturaExistente"  name="informacionSinFacturaExistente" value="false" />
				
				<input type="hidden" id="esComprobacionConFacturas"  name="esComprobacionConFacturas" value="false" />
				<input type="hidden" id="esComisionNacional"  name="esComisionNacional" value="false" />
				<input type="hidden" id="esPagoAlimentacion"  name="esPagoAlimentacion" value="false" />
				<input type="hidden" id="pagoXOficioPermitido"  name="pagoXOficioPermitido" value="0" />
				<input type="hidden" id="esImpuestoForzoso"  name="pagoXOficioPermitido" value="0" />
				<input type="hidden" id="contieneNoComprobable"  name="contieneNoComprobable" value="false" />
				<input type="hidden" id="esPagoProveedor"  name="esPagoProveedor" value="false" />
				<input type="hidden" id="aEjercicioFiscal"  name="aEjercicioFiscal" />
				<input type="hidden" id="destinoGasto"  name="destinoGasto" />

				<input type="hidden" id="tipo_pago" name="tipo_pago" value=""/>
				<input type="hidden" id="RFC" name="RFC" value=""/>
				<input type="hidden" id="nFolioPago" name="nFolioPago" value=""/>
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
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</div>
				
					<div>
						
						<div id="uploadDiv">
							<table>
								<tr id="comisionNacionalTR">
									<td>
										<fieldset>
											<legend>Carga de Facturas</legend>
											<table align="center" cellpadding="0" cellspacing="0">
												<tr>
													<td colspan="3" align="left">
														Seleccione el archivo <b>zip</b> que contiene las facturas de su comprobaci&oacute;n.
													</td>
												</tr>
												<tr>
													<td align="right" nowrap="nowrap">
														<label for="archivoZipFacturas">Archivo con facturas *.zip:</label>
													</td>
													<td align="left">
														<input type="file" value="" id="archivoZipFacturas" name="archivoZipFacturas" class="Facturas Validable form-control"/> 
														<input type="button" value="Limpiar" id="limpia1" onclick="limpiar('Facturas')" class="btn btn-secondary"/>
													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
		
								<tr>
									<td>
										<fieldset>
											<legend>Carga de monto no facturado</legend>
											<table align="center">
												<tr>
													<td colspan="2">
														Seleccione el archivo <b>PDF</b> que contiene sus comprobantes de montos sin facturas.
													</td>
												</tr>
												<tr>
													<td align="right">
														<label for="montoNoComprobable"> Ingrese el monto no comprobable: </label>
													</td>
													<td align="left">
														<input type="text" id="montoNoComprobable" name="montoNoComprobable" value="0.00" size="10" onfocus="focusMoney(this)" onkeypress="return onlyNumbers(event)" onblur="blurMoney(this)" class="form-control Money NoComprobable Validable">
													</td>
												</tr>
												<tr id="archivoAutNoComprobableTR">
													<td align="right" nowrap="nowrap">
														<label for="archivoAutNoComprobable"> Archivo *.PDF: </label>
													</td>
													<td align="left">
														<input type="file" value="" id="archivoAutNoComprobable" name="archivoAutNoComprobable" class="NoComprobable Validable form-control"/> 
														<input type="button" value="Limpiar" id="limpia4" onclick="limpiar('NoComprobable')" class="btn btn-secondary"/>
													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
		
								<tr>
									<td align="center">
										<input type="button" value="Enviar Archivo" alt="De click en este boton para enviar las facturas a validacion." id="sendButton" class="btn btn-secondary"/>
									</td>
								</tr>
								
							</table>
						</div>
					</div>
				</div>
				
				<div id="cargaPagoProveedor" style="display: none">
					<div id="pagoProveedorDiv">
						<table align="center">
							<tr>
								<td>
									<div id="tipoPago" style="display: none">
										<table align="left">
											<tr>
												<td>
													<div>
														<a href="#" onclick="parent.togleDivFacts(0);return false;">Ver informacion cargada</a>
													</div>
													<span id="pagoConFacturaSpan">
														<input type="radio" value="PPFactura" id="PPFactura" name="PagoProveedor" checked="checked" onclick="tipoPagoProveedor()">
														<label for="PPFactura">Pago con Factura</label>
													</span>
													<span id="pagoConOficioSpan" >
														<input type="radio" value="PPOficio" id="PPOficio" name="PagoProveedor"  onclick="tipoPagoProveedor()">
														<label for="PPOficio">Pago por oficio</label>
													</span>
												</td>
											</tr>
										</table>
									</div>
								</td>
							</tr>
							
							<tr id="pagoConFacturasTR">
								<td>
									<fieldset>
										<legend>Carga de Facturas</legend>
										<table align="center" cellpadding="0" cellspacing="0">
											<tr>
												<td colspan="3" align="left">
													Seleccione el archivo <b>zip</b> que contiene la(s) factura(s) a pagar.
												</td>
											</tr>
											<tr>
												<td align="right" nowrap="nowrap">
													<label for="archivoZipFacturasPP">Archivo con facturas *.zip:</label>
												</td>
												<td align="left">
													<input type="file" value="" id="archivoZipFacturasPP" name="archivoZipFacturasPP" class="FacturasPP Validable PagoProveedor form-control"/> 
													<input type="button" value="Limpiar" id="limpia6" onclick="limpiar('FacturasPP')" class="btn btn-secondary"/>
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							
							<tr id="pagoConOficioTR" style="display: none">
								<td>
									<fieldset>
										<legend>Pago sin facturas</legend>
										<table>
											<tr>
												<td align="right">
													Ingrese el monto neto a pagar:
												</td>
												<td>
													<input type="text" id="montoBrutoPP" name="montoBrutoPP" class="ValidablePP Money OficioPP form-control" size="14" value="$0.00" onfocus="focusMoney(this)" onkeypress="return onlyNumbers(event)" onblur="blurMoney(this)">
												</td>
											</tr>
											<tr>
												<td align="right">
													Ingrese el monto de Impuestos:
												</td>
												<td>
													<input type="text" id="montoImpuestosPP" name="montoImpuestosPP" class="ValidablePP Money OficioPP form-control"  size="14" value="$0.00" onfocus="focusMoney(this)" onkeypress="return onlyNumbers(event)" onblur="blurMoney(this)">
												</td>
											</tr>
											<tr>
												<td align="right">
													Monto neto a pagar:
												</td>
												<td>
													<input type="text" id="montoNetoPP" name="montoNetoPP" size="14" class="ValidablePP Money notEditable OficioPP form-control" readonly="readonly" onblur="blurMoney(this)" value="$0.00" >
												</td>
											</tr>
											<tr id="oficioFileUploadTR">
												<td align="right">
													Oficio comprobante de pago:
												</td>
												<td>
													<input type="file" value="" id="archivoOficioPP" name="archivoOficioPP" class="OficioPP Validable PagoProveedor form-control"/> 
													<input type="button" value="Limpiar" id="limpia7" onclick="limpiar('OficioPP')" class="btn btn-secondary"/>
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td align="center" nowrap="nowrap" >
									<input type="button" value="Enviar Informacion" id="enviarPagoProveedorBtn" class="btn btn-secondary"/>
								</td>
							</tr>
						</table>
					</div>
				</div>
			</form>
		</div>
	</body>
</html>