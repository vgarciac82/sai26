<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String tipoPago = request.getParameter("tipo_pago");
	String tipoModulo = request.getParameter("tipo_modulo");
	String rfc = request.getParameter("RFC");
	String contratoVales = StringUtils.isBlank(  request.getParameter("contratoVales") )? "false": request.getParameter("contratoVales");
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
		<style type="text/css" title="currentStyle">	
			@import "themes/smoothness/jquery-ui-1.8.4.custom.css";	
		</style>
		
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
		<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
		
		<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

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
		<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
		<script type="text/javascript">
			var tipoPago = '<%=tipoPago%>';
			var tipoModulo = '<%=tipoModulo%>'
			var rfc = '<%=rfc%>';
			var msg = "<%=msg%>";
			var contratoVales = "<%=contratoVales%>";
			
			$(document).ready(
				function(){
					$("#contratoVales").val(contratoVales);
					$("#tipo_pago").val(tipoPago);
					$("#tipo_modulo").val(tipoModulo);
					$("#btnAceptar").button().click(function(){parent.togleDivFacts(0);});
					$("#sendButton").button().click(
						function(){
							$.blockUI({message: "Procesando espere ......"});
							$("#cIdRFC").val( parent.$("#campoRFC").val() );	

							submit();
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
				$("#btnAceptar").button();
				if( $("#archivoZip").val() == "" ){
					Swal.fire("Archivo de Carga","Debe elegir el archivo de carga","info");
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
					Swal.fire("Nota de credito","La nota de credito disminuir\u00E1 la cantidad del monto total de las facturas.","info");
				}
				else	
					$("#lgndDiv").text("Enviar Factura");
			}
		</script>

	</head>
	<br/>
	<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
		<div id="container" style="width: 100%" class="container">			
			<div class="card-header"> <h5> Recepción de facturas </h5> </div>
			
			<form action="../uploadFacturas" method="post" enctype="multipart/form-data" id="submitFrm">
					
				<input type="hidden" id="contratoVales" name="contratoVales" value="">
				<input type="hidden" id="tipo_pago" name="tipo_pago" value="">
				<input type="hidden" id="tipo_modulo" name="tipo_modulo" value="">
				<input type="hidden" id="cIdRFC" name="cIdRFC" value="">
				
				<div id="msgDialog" title="Resultado de Carga">
					<div class="row">										
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						</div>		
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">	
							<textarea cols="40" rows="8" id="msgTxt" class="form-control form-control-sm"><%=msg.replaceAll("<br>", "\n*")%></textarea>								  							
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						</div>		
					</div>
					
					<div class="row">										
						<div class="col-12 col-lg-9 col-md-9 col-sm-12 p-1">
						</div>		
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
							<input type="button" id="btnAceptar" value="Aceptar" class="btn-secondary"/>								  							
						</div>
					</div>
				</div>
				
				<div id="uploadDiv" class="mt-2">
					<div class="row">				
						<div class="col-12 col-lg-6 col-md-6 col-sm-8 d-flex p-1">
							<input type="radio" name="seleccionTipo" id="FACTURA" class="form-check-input" value="FACTURA" checked="checked" onclick="cambiaTipoFactura(0);">&nbsp;Cargar Factura
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="seleccionTipo" id="CREDITO" class="form-check-input" value="CREDITO" onclick="cambiaTipoFactura(1);">&nbsp;Cargar Nota de Credito
						</div>
						<div class="col-12 col-lg-4 col-md-2 col-sm-4 d-flex p-1">
							<a href="#" onclick="parent.togleDivFacts(0);return false;">Ver facturas capturadas</a>
						</div>
					</div>
						
					<div id="facturasDiv" class="mt-2">
						<div id="lgndDiv">
							<h5> Enviar Facturas </h5>
							<hr class="mt-3"/>
						</div>
						
						<div class="row">										
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
								Archivo *.zip:&nbsp;<input type="file" id="archivoZip" name="archivoZip" class="form-control form-control-sm" />
							</div>	
						</div>																				
					</div>
					
					<div class="row">					
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							&nbsp;<input type="button" value="Enviar Archivo" alt="De click en este boton para enviar las facturas a validacion." id="sendButton" class="btn-secondary"/>
						</div>
					</div>
					
				</div>
			</form>
		</div>
	</body>
</html>