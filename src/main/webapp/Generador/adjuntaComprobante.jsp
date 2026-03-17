<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Carga de Archivo Comprobante Bancario</title>
		
		<!-- Estilos estandar para los controles JQuery -->
		
		<link rel="stylesheet" href="css/bootstrap.min.css"/>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		<link rel="stylesheet" href="css/bootstrap-datetimepicker.min.css"></link>
		<script src="js/bootstrap.bundle.min.js"></script>
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
		<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>
		
		<script type="text/javascript">
		 
		 $(document).ready(function() {
		 	$("#sendBtn").button().click(
		 		function(){
		 			sendFile();
		 		}
		 	);
		 	
		 	querySelectPost("catalogoTramitesAdjuntarRead", "TITULO_APLICACION", {
				async : false
			});
		 } );
		 
		 function sendFile(){
		 	if( $("#fileCLC").val() == "" ){
		 		alert("Debe eligir el archivo de carga.");
		 		return;
		 	}else if( confirm("Est seguro de realizar la carga?") ){
		 		$.blockUI("Procesando por favor espere. No cierre esta pagina.");
		 		$("#frmCLC").submit();
		 	}
		 }
		</script>
	</head>
	<br/>
	<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
 		<form id="frmCLC" name="frmCLC" method="post" enctype="multipart/form-data" action="../AdjuntaComprobanteBanco">
			<div id="container" class="container">
				<div class="card-header"> <h3> Carga masiva de Comprobante Bancario </h3> </div>
				<hr class="mt-3"/>
				
				<h6> Cargar Archivo </h6> 
				<hr class="mt-3"/>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<label for="tramite">Tramite:</label>			  																									
					</div>												
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<select class="form-select form-select-sm" id="TITULO_APLICACION" name="TITULO_APLICACION">
							<option value="-1">Seleccione una opcion</option>
						</select>
					</div>																														
				</div>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<label for="fileCLC">Archivo:</label>			  																									
					</div>												
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<input class="form-control form-control-sm" type="file" id="fileCLC" name="fileCLC" style="width: 25em;" />
					</div>																														
				</div>
				
				<br/>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12">
						<input type="button" class="btn btn-secondary btn-sm" id="sendBtn" value="Cargar">
					</div>
				</div>
				
				<br/>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12">
						<b>* Solo se procesar&aacute;n archivos .zip Cualquier otro tipo sera ignorado</b>
					</div>
				</div>
				
			</div>
 		</form>
	</body>
</html>