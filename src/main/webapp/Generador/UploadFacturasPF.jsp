<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	Caso c = (Caso)session.getAttribute(GestionInterface.ATT_CASE);
	int nFolioPago = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1),10);
	String tipoPago = c.getTipoCaso().getGavetaAsociada();	
	
	String msg = "";
	if( session.getAttribute("RESULT") != null ){
		msg = (String)session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
	
	
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Validar Facturas</title>
	
		<!-- Estilos estandar para los controles JQuery -->
		<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
		<link rel="stylesheet" type="text/css"	href="css/jquery-ui.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
		<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
		
		
		<script type="text/javascript" src="js/jquery-3.5.1.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="js/bootstrap.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="js/jquery-ui.min.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
		
		<script type="text/javascript">
			var tipoPago = '<%=tipoPago%>';
			var nFolioPago = <%=nFolioPago%>;
			var msg = "<%=msg%>";
			
			$(document).ready(
				function(){
					$("#tipo_pago").val(tipoPago);
					$("#nFolioPago").val(nFolioPago);
					$("#btnAceptar").button().click(function(){parent.cerrarDlg();});
					
					$("#saveButton").button().click(
						function(){
							submit();
						}
					);
					
					$("#sendButton").button().click(
						function(){
							$("#cIdRFC").val( parent.$("#campoRFC").val() );
							submit();
						}
					);
					
					if( msg != ""){
						$("#msgDialog").show();
						$("#lblEncabezado").hide();
						$("#uploadDiv").hide();
					}else{
						$("#msgDialog").hide();
						$("#uploadDiv").show();
					}
					
					queryFormPost({
						queryName : "facturasCapturadasPFRead",
						async : false,
						callback : function() {
							var totalFacturas = parseInt(  (  $("#nFacturasCapturadas").val() == "" ? "0" : $("#nFacturasCapturadas").val()  ), 10 );
							var totalOficios =  parseInt(  (  $("#nOficiosCapturadas").val()  == "" ? "0" : $("#nOficiosCapturadas").val()  ), 10 );
							
							if( ( totalFacturas == 0 && totalOficios == 0 ) || totalFacturas > 0 ){
								$("#uploadFacturasTR").css( "display",  "block" );
								if(totalFacturas > 0)
									$("#certificado").attr('disabled', true);
							}else{
								$("#uploadFacturasTR").css( "display",  "none" );
							}
						}
					});
					
					if (tipoPago=="PAGOOBRA"){
						$("#certificado").attr('disabled', true);
					}
				}
			);
			
			function submit(){
				var tipoArchivoSubir = $('input[name=tipoComprobacion]:checked').val();
				if( tipoArchivoSubir == "comprobacion" || tipoArchivoSubir == "credito" )
					subtmitFacturas();
				else if( tipoArchivoSubir == "certificado")
					submitOficio();
				
			}
			
			function submitOficio(){
				Sinfrmt($("#monto")[0]);
				if( $("#noOficio" ).val() == "" ){
					Swal.fire("Capture","El n\u00FAmero de oficio es obligatorio","info");
					$("#noOficio" ).focus();
				}else if( parseFloat( $("#monto").val() ) == 0 ){
					Swal.fire("Capture","El monto es obligatorio", "info");
					$("#monto").focus();
				}else{
					$("#submitFrm").submit();
				}
				
			}
			
			function subtmitFacturas(){
				
				if( $("#archivoZip").val() == "" ){
					Swal.fire("Adjunte","Debe elegir el archivo de carga","info");
					$("#archivoZip").focus();
				}else{
					if( $('input[name="tipoComprobacion"]:checked').val() == "credito" ){
						if( confirm("\u00BFEsta seguro de que est\u00E1 enviando NOTAS DE CR\u00C9DITO?") )
							$("#submitFrm").submit();
					}else{
						$("#submitFrm").submit();
					}
				}
				
			}
							
			function focusMoney(){
				$("#montoNoComprobable").select();
			}
			
			function blurMoney(){
				
				if( $("#montoNoComprobable").val() == "" )
					$("#montoNoComprobable").val("0.00");
					
				$("#monto").val(  $("#montoNoComprobable").val() );
				Sinfrmt($("#monto")[0]);
				$("#montoNoComprobable").formatCurrency();
				
			}
			
			function onlyNumbers(evt) {
				var keyPressed = (evt.which) ? evt.which : event.keyCode;
				var strCheck = '-0123456789.';
			
				var key = String.fromCharCode( keyPressed );
				if (strCheck.indexOf( key ) == -1)
					return false; // Valida que sea numero y punto decimal
			
				return true; 
			}
			
			function tipoArchivo(idTipoArchivo){
				if( idTipoArchivo == 1 || idTipoArchivo == 3){
					$("#montoDiv").css( "display", "none");
					$("#uploadZipDiv").css( "display", "block");
					$("#archivoLegend").text(  ( idTipoArchivo == 1 ? "Enviar Factura":"Enviar Nota de Cr\u00E9dito")  );
				}else if( idTipoArchivo == 2){
					$("#montoDiv").css( "display", "block");
					$("#uploadZipDiv").css( "display", "none");
				}
			}
			
			/**
			 * Retira el formato monetario
			 * 
			 * @param fld
			 *            campo.
			 */
			function Sinfrmt(fld) {
				var valcol = fld.value;
				valcol = valcol.replace(/$/g, "");
				valcol = valcol.replace(/,/g, "");
				$("#" + fld.id).val(valcol);
			}
		</script>

	</head>
	<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
		<div id="container" class="container SyCData">
			<h1 id="lblEncabezado">Recepción de facturas</h1>
			<form action="../uploadFacturasPF" method="post" enctype="multipart/form-data" id="submitFrm">
				<input type="hidden" id="tipo_pago" name="tipo_pago" value=""/>
				<input type="hidden" id="nFolioPago" name="nFolioPago" value=""/>
				<input type="hidden" id="nFacturasCapturadas" name="nFacturasCapturadas" value="0"/>
				<input type="hidden" id="nOficiosCapturadas" name="nOficiosCapturadas" value="0"/>
				<input type="hidden" id="cIdRFC" name="cIdRFC" value=""/>
				<div id="msgDialog" title="Resultado de Carga">
						<h1>Resultado de carga.</h1>
							<div class="row">
								<div class="col-12">
									<textarea rows="10" cols="30" class="form-control" id="msgTxt"><%=msg.replaceAll("<br>", "\n*")%></textarea>
								</div>
							</div>
				</div>
				<div id="uploadDiv">
					<div>
						<table width="100%">
							<tr>
								<td align="right">
									<a href="#" onclick="parent.togleDivFacts(0);return false;">Ver facturas capturadas</a>
								</td>
							</tr>
						</table>
					</div>					
					<div>
						<input type="radio"  class="form-check-input" id="comprobacion" name="tipoComprobacion" value="comprobacion" checked="checked" onclick="tipoArchivo(1)"/>
						Pago por factura
						<input type="radio"  class="form-check-input" id="CREDITO"      name="tipoComprobacion" value="credito" onclick="tipoArchivo(3);"/>
						Cargar Nota de Credito
						<input type="radio"  class="form-check-input" id="certificado"  name="tipoComprobacion" value="certificado" onclick="tipoArchivo(2)"/>
						Pago por monto
					</div>
					
					<div id="montoDiv" style="display: none">
						<div class= "card">
							<div class="card-header" id="montosLegend">Monto</div>
  							<div class="card-body">
								<table align="center">
									<tr id="noOficioTR">
										<td align="right">
											Ingrese el No. de Documento
										</td>
										<td align="left">
											<input class="form-control" type="text" name="noOficio" id="noOficio" size="12" value="" />
										</td>
									</tr>
									<tr>
										<td align="right">
											<label id="motosLbl">
												Ingrese el monto :
											</label>
										</td>
										<td align="left">
											<input class="form-control" type="text" id="montoNoComprobable" name="montoNoComprobable" value="$0.00" size="10" onfocus="focusMoney()" onkeypress="return onlyNumbers(event)" onblur="blurMoney()"/>
											<input type="hidden" id="monto" name="monto" value="0.00"> 
										</td>
									</tr>
									<tr>
										<td colspan="2" align="right">
											<input class="btn btn-secondary" type="button" value="Enviar" alt="De click en este boton para enviar la informacion." id="saveButton" class="btnInterfaceBG"/>
										</td>
									</tr>
								</table>
							</div>
						</div>
					</div>
					
					<div id="uploadZipDiv">					
						<div class= "card">
							<div class="card-header" id="archivoLegend">Enviar facturas.</div>
  							<div class="card-body">
									<div class="row">
										<div class="col-12">
											<label>Archivo *.zip:</label>
											<input type="file" class="form-control" id="archivoZip" name="archivoZip">
										</div>
									</div>
									<div class="row">
										<div class="d-flex align-items-end flex-column">
											<input type="button" class="btn btn-secondary"  value="Enviar Archivo" alt="De click en este boton para enviar las facturas a validacion." id="sendButton" class="btnInterfaceBG"/>
										</div>
									</div>
									
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
	</body>
</html>