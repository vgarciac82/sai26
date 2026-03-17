<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String TipoContrato = request.getParameter("TipoContrato");
	String IDContrato = request.getParameter("IDContrato");
	String folioSAI = StringUtils.trimToEmpty(  request.getParameter("folioSAI") );
	String rfc = request.getParameter("RFC");
	String montoTotal=request.getParameter("montoTotal");
	String montoIVA=request.getParameter("montoIVA");
	String tipoFactGlobal=(null==request.getParameter("tipoFacturaGlobal") || "".equals( request.getParameter("tipoFacturaGlobal") )?"1":request.getParameter("tipoFacturaGlobal"));
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
		<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
		<link rel="stylesheet" type="text/css" href="../css/interfaz.css"></link>
		
		<style type="text/css">
			#dt_example .container {
				width: 500px;
			}
		</style>
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript">
			var TipoContrato = '<%=TipoContrato%>';
			var IDContrato = '<%=IDContrato%>';
			var folioSAI = '<%=folioSAI%>';
			var rfc = '<%=rfc%>';
			var montoTotal = '<%=montoTotal%>';
			var montoIVA = "<%=montoIVA%>";
			var msg = "<%=msg%>";
			var tipoFact = "<%=tipoFactGlobal%>";
			$(document).ready(
				function(){
					$("#TipoContrato").val(TipoContrato);
					$("#IDContrato").val(IDContrato);
					$("#folioSAI").val(folioSAI);
					$("#montoIVA").val(montoIVA);
					$("#montoConIVA").val(montoTotal);
					$("#tipoFacturaGlobal").val(tipoFact);
					$("#btnAceptar").button().click(function(){parent.togleDivFacts(0);});
					$("#sendButton").button().click(
						function(){
							try{
								$("#cIdRFC").val( rfc );
								$.blockUI();
								submit();
								document.getElementById('sendButton').setAttribute("disabled","disabled");
							}catch (e) {
								$.unblockUI();
								document.getElementById('sendButton').removeAttribute("disabled");
								alert(e);
							}
						}
					);
					if( msg != ""){
						$("#msgDialog").show();
						$("#uploadDiv").hide();
					}else{
						$("#msgDialog").hide();
					}
				}
			);
			
			function submit(){
				
				if( $("#archivoZip").val() == "" ){
					alert("Debe elegir el archivo de carga");
					$("#archivoZip").focus();
				}else{
					if( $('input[name="seleccionTipo"]:checked').val() == "CREDITO" ){
						if( confirm("\u00BFEsta seguro de que est\u00E1 enviando NOTAS DE CR\u00C9DITO?") )
							$("#submitFrm").submit();
					}else{
						$("#submitFrm").submit();
					}
				}
				
			}
			
			function cambiaTipoFactura(idOpcion){
				if( parseInt(idOpcion, 10) > 0  ){
					$("#lgndDiv").text("Enviar Nota de Cr\u00E9dito");
					alert("La nota de credito disminuir\u00E1 la cantidad del monto total de las facturas.");
				}
				else	
					$("#lgndDiv").text("Enviar Factura");
			}
		</script>

	</head>
	<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
		<div id="container" class="container SyCData">
			<h1>Recepción de facturas de contrato</h1>
			<form action="../uploadFacturasContrato" method="post" enctype="multipart/form-data" id="submitFrm">
				<input type="hidden" id="TipoContrato" name="TipoContrato" value="">
				<input type="hidden" id="IDContrato" name="IDContrato" value="">
				<input type="hidden" id="folioSAI" name="folioSAI" value="">
				<input type="hidden" id="cIdRFC" name="cIdRFC" value="">
				<input type="hidden" id="montoConIVA" name="montoConIVA" value="">
				<input type="hidden" id="montoIVA" name="montoIVA" value="">
				<input type="hidden" id="tipoFacturaGlobal" name="tipoFacturaGlobal" value="1">
				<div id="msgDialog" title="Resultado de Carga">
					<fieldset>
						<legend>Resultado de carga.</legend>
							<table align="center">
								<tr>
									<td align="center">
										<textarea rows="10" cols="40" id="msgTxt"><%=msg.replaceAll("<br>", "\n*")%></textarea>
									</td>
								</tr>
								<tr>
									<td align="right">
										<input type="button" id="btnAceptar" value="Aceptar" class="btnInterfaceBG ui-button ui-corner-all" />
									</td>
								</tr>
							</table>
					</fieldset>
				</div>
				<div id="uploadDiv">
					<table align="center" width="100%">
						<tr>
							<td align="right" colspan="2">
								<a href="#" onclick="parent.togleDivFacts(0);return false;">Ver facturas capturadas</a>
							</td>
						</tr>
					</table>
					<div id="facturasDiv">
						<fieldset>
							<legend id="lgndDiv">Enviar factura de Contrato.</legend>
							<table align="center">
								<tr>
									<td colspan="2" align="left">
										<input type="checkbox" id="seleccionTipo" name="seleccionTipo" value="CREDITO" >
										<label for="seleccionTipo">Es nota de credito</label>
									</td> 
								</tr>
								<tr>
									<td align="right">Archivo *.zip:</td>
									<td align="left"><input type="file" value="" id="archivoZip" name="archivoZip" > 
								</tr>
							</table>
						</fieldset>
					</div>
					<table width="100%">
						<tr>
							<td colspan="2" align="right">
								<input type="button" value="Enviar Archivo" alt="De click en este boton para enviar las facturas a validacion." id="sendButton" class="btnInterfaceBG ui-button ui-corner-all" />
							</td>
						</tr>
					</table>
				</div>
			</form>
		</div>
	</body>
</html>