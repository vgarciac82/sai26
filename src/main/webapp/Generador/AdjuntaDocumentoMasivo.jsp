<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%> 
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String msg = (String) session.getAttribute("msg");
	if( msg != null ){
		msg = msg.replaceAll("\n", "").replaceAll("'", "").replaceAll("\"", "");
		session.removeAttribute("msg");
	}else{
		msg = "";
	}
	
	Usuario u = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if( u == null ){
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String cCentroContable = (   ( u.getPropiedad("CCENTROCONTABLE") != null && u.getPropiedad("CCENTROCONTABLE").getValor() != null ) ? u.getPropiedad("CCENTROCONTABLE").getValor() : ""   );
	
	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic();
	String eFiscal = efbl.getEjercicioFiscalActivo().getaEjercicioFiscal();
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Adjunta documento Masivo</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

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
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript">
	var msg = "<%=msg%>";
	var centroContableUsr = '<%=cCentroContable%>';
	$(document).ready(function() {
		cargaInformacionInicial();
		$("#enviar").button().click(function() {
			if( validacionCorrecta() ){
				$.blockUI();
				$("#FormUpload").submit();
			}
		});
		
		if( msg != "" )
			alert(msg);
	});

	function cargaInformacionInicial() {
	
		$("#centroContableUsr").val(centroContableUsr);
		querySelectPost("CatalogoCentroContMasivoRead", "cCentroContable", {async:false});
		querySelectPost("CarpetaAdjuntarMasivoRead", "carpeta", {async:false} );
		$('#RG').hide();
		$('#POL').hide();
	
	}
	
	function cargaCarpeta(){
		querySelectPost("CarpetaAdjuntarMasivoRead", "carpeta", {async:false} );
		
	}
	
	  function onlyNumbers(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode;
		var strCheck = '0123456789';
	
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1)
			return false; 
	
		return true; 
  	}
  
  function onlyNumberscg(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode;
		var strCheck = '0123456789,-';
	
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1)
			return false; 
	
		return true; 
  	}
  	
	function validacionCorrecta(){
		var msg = "";
		var tkn = "";
		var tipodoc = document.getElementById('tituloAplicacion').value;
		
		if($("#tituloAplicacion").val() == "-1"){
		        alert("Selecciona Documento\n");
				return false;
			}	
		if($("#carpeta").val() == "-1"){
				alert("Selecciona Carpeta\n");
				return false;
		}
		if($("#nombreDocumento").val() == "-1"){
				alert("Selecciona Nombre de Documento\n");
				return false;
		}	
		
		switch (tipodoc){
			case "RELACIONGASTOS":
				if( $("#desde").val() == "" ){
					msg += tkn + "El numero de inicio P.E. 100";
					tkn = "\n";
				}
				if( $("#hasta").val() == "" ){
					msg += tkn + "El numero de terminacion P.E. 1000";
					tkn = "\n";
				}
			break;
		
			case "POLIZA":
				var cadena = document.getElementById('secPoliza').value;
				var a=0; 
				
				
				if( cadena == "" ){
					msg += tkn + "Falta información de consecutivo, ejemplo: 1-4,7-8,10-21";
					tkn = "\n";
				}else{
					if (cadena.charAt(0)== ',' || cadena.charAt(0)== '-'){
					msg += tkn + "La cadena no debe empezar con ',' o '-'";
					tkn = "\n";
					}else{
						for(i=0; i<cadena.length; i++){
							if (cadena.charAt(i)==',' || cadena.charAt(i)=='-'){ 
									if (cadena.charAt(i+1) == ',' || cadena.charAt(i+1) == '-'){
										msg += tkn + "La cadena no debe tener ',' o '-' consecutivos";
										tkn = "\n";
										break;
									}	
						 	}
						 	if(cadena.charAt(i)==',' && a==1)a=0;
						 	if (cadena.charAt(i)=='-' && a==0){a=1;
						 	}else if (cadena.charAt(i)=='-' && a==1){
						 			msg += tkn + "Rango invalido";
									tkn = "\n";
									break;
						 		}
						}
					}
				}
				if (cadena.charAt(cadena.length-1)==',' || cadena.charAt(cadena.length-1)=='-'){
				msg += tkn + "La cadena no debe terminar en ',' o '-'";
				tkn = "\n";
				}
			break; 
		}
		if( $("#archivoSubir").val() == "" ){
				msg += tkn + "Archivo a cargar.";
				}
		if( msg != "" ){
				alert("Para continuar corrija lo siguiente:\n"+msg);
				return false;
				}
		$("#secPoliza2").val($("#secPoliza").val());
		$("#desde2").val($("#desde").val());
		$("#hasta2").val($("#hasta").val());
			return true;
	}
	
	function cargaDocumentos(){
	
		querySelectPost("DocumentoAdjuntarMasivoRead", "nombreDocumento", {async:false} );
		
	}
	
	function ocultarMostrar(){
	var doc = document.getElementById('tituloAplicacion').value;
	switch(doc){
		case "RELACIONGASTOS": 
		case "PAGODIVERSO":
			$('#RG').show();
			$('#POL').hide();
			break;
		case "POLIZA":
			$('#RG').hide();
			$('#POL').show();
			break;			  
		}
	}
</script>

</head>
<br/>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<form id="FormUpload" name="FormUpload" method="POST" action="../AdjuntaMasivo" enctype="multipart/form-data">
		<input type="hidden" name="centroContableUsr" id="centroContableUsr" value="" />
		<input type="hidden" id="secPoliza2" name="secPoliza2" value="">
		<input type="hidden" id="desde2" name="desde2" value="">
		<input type="hidden" id="hasta2" name="hasta2" value="">
		
		<div id="container" class="container" style="width: 80%">
			<div class="card-header"> <h3> Adjunta documento masivo </h3> </div>
			<hr class="mt-3"/>
			
			<h6> Capture la informaci&oacute;n </h6>
			<hr class="mt-3">
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="cc">Centro contable:</label>			  																									
				</div>												
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<select class="form-select form-select-sm" id="cCentroContable" name="cCentroContable">						
					</select>
				</div>		
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="cc">Ejercicio Fiscal:</label>			  																									
				</div>												
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<select class="form-select form-select-sm" id="aEjercicioFiscal" name="aEjercicioFiscal">	
						<option value="<%=eFiscal%>"><%=eFiscal%></option>					
					</select>
				</div>																														
			</div>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="cc">Documento:</label>			  																									
				</div>												
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<select class="form-select form-select-sm" id="tituloAplicacion" name="tituloAplicacion" onchange="cargaCarpeta();cargaDocumentos();ocultarMostrar();">
						<option value='-1'>Seleccione una opci&oacute;n</option>
						<option value="RELACIONGASTOS">Relaci&oacute;n de Gastos</option>
						<option value="POLIZA">Poliza Manual</option>
						<option value="PAGODIVERSO">Pago Diverso</option>
					</select>
				</div>		
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="cc">Carpeta:</label>			  																									
				</div>												
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<select class="form-select form-select-sm" id="carpeta" name="carpeta" onchange="cargaDocumentos();">								
					</select>
				</div>																														
			</div>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="cc">Nombre Documento:</label>			  																									
				</div>												
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<select class="form-select form-select-sm" id="nombreDocumento" name="nombreDocumento">
						<option value="-1">Seleccione Documento</option>
					</select>
				</div>		
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="cc">Archivo:</label>			  																									
				</div>												
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<input class="form-control form-control-sm" type="file" id="archivoSubir" name="archivoSubir"  />
				</div>																														
			</div>
			
			<div class="container" id="RG">
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<label for="cc">Del N&uacute;mero:</label>			  																									
					</div>												
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="desde" name="desde" size="7" maxlength="6" onKeyPress="return onlyNumbers(event);" style="width: 100px;">
					</div>		
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<label for="cc">Al N&uacute;mero:</label>			  																									
					</div>												
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="hasta" name="hasta" size="7" maxlength="6" onKeyPress="return onlyNumbers(event);" style="width: 100px;">
					</div>																														
				</div>
			</div>
			
			<div class="container" id="POL">
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">	
						<label for="pol">Indica documentos consecutivos:</label>			  																									
					</div>												
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="secPoliza" name="secPoliza" size="50" maxlength="50" onKeyPress="return onlyNumberscg(event);">
					</div>		
				</div>
			</div>
			
			<br/>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-5 col-md-5 col-sm-4 p-1">						
				</div>
				<div class="col-12 col-lg-6 col-md-6 col-sm-6 p-1">
					<input type="button" class="btn btn-secondary btn-sm" id="enviar" value="Cargar">						
				</div>
			</div>							

		</div>
	</form>
</body>
</html>