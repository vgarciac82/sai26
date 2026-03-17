<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	Caso c = (Caso)session.getAttribute(GestionInterface.ATT_CASE);
	int nFolioPago = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
	String tipoPago = c.getTipoCaso().getGavetaAsociada();
	
	String rfcOrigen = request.getParameter("campoRFC");	
	
	String msg = "";
	if( session.getAttribute("RESULT") != null ){
		msg = (String)session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
	
%>
<html>
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Validar Archivos</title>

	<!-- Estilos estandar para los controles JQuery -->
	<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
	<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
	<style type="text/css">
		#dt_example .container {
			width: 500px;
		}
	</style>
	<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
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
	var tipoPago = '<%=tipoPago%>';
	var nFolioPago = <%=nFolioPago%>;
	var msg = "<%=msg%>";
			
	$(document).ready(function(){
		$("#tipo_pago").val(tipoPago);
		$("#nFolioPago").val(nFolioPago);
		
		$("#sendButton").button().click(function(){submit();});
				
		if( msg != ""){
			$("#msgDialog").show();
			$("#uploadDiv").hide();
		}else{
			$("#msgDialog").hide();
			$("#uploadDiv").show();
		}
					
		
	});//FIN DEL READY
			
	function submit(){
		submitOficio();
	}
	
	function submitOficio(){
		if( $("#archivoZip").val() == "" ){
			alert("Debe elegir el archivo de carga");
			$("#archivoZip").focus();
		}else{
			$("#submitFrm").submit();
		}
	}
	
		
	
</script>

</head>
	<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
		<div id="container" class="container SyCData">
			<h1>Captura Justificaci&oacute;n</h1>
			<form action="../AdjuntaJustificacion" method="post" enctype="multipart/form-data" id="submitFrm">
				<input type="hidden" id="tipo_pago" name="tipo_pago" value="">
				<input type="hidden" id="nFolioPago" name="nFolioPago" value="">
				<input type="hidden" id="nFacturasCapturadas" name="nFacturasCapturadas" value="0">
				<input type="hidden" id="tipoComprobacion" name="tipoComprobacion" value="extranjero">
				<input type="hidden" id="noOficio" name="noOficio" value="extranjero">
				<input type="hidden" id="rfcOrigen" name="rfcOrigen" value="<%=rfcOrigen%>">
				
				<div id="msgDialog" title="Resultado de Carga">
					<fieldset>
						<legend>Resultado de carga.</legend>
							<table align="center">
								<tr>
									<td align="center">
										<textarea rows="10" cols="40" id="msgTxt"><%=msg.replaceAll("<br>", "\n*")%></textarea>
									</td>
								</tr>
								
							</table>
					</fieldset>
				</div>
				<div id="uploadDiv">
										
					<div>					
						<fieldset>
							<legend id="archivoLegend">Documento</legend>
							<table align="center">
								<tr>
								</tr>
								<tr>
									<td align="right">Seleccione el archivo:</td>
									<td align="left"><input type="file" value="" id="archivoZip" name="archivoZip"> 
								</tr>
								<tr>
									<td colspan="2" align="right">
										<input type="button" value="Enviar Archivo" alt="De click en este boton para enviar la justificacion para eliminar la retencion del IVA del 6%." id="sendButton">
									</td>
								</tr>
								<tr>
									<td></td>
								</tr>
								<tr>
									<td  colspan="2" align="left">
									Nota: Queda bajo su responsabilidad la omisión al cumplimiento del artículo 1-A fracción IV  de la Ley del Impuesto al Valor Agregado
									</td>
								</tr>
							</table>
						</fieldset>
					</div>
				</div>
			</form>
		</div>
	</body>
</html>