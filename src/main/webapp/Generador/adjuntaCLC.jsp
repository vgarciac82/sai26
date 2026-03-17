<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Carga de Archivo CLC</title>
		
		<!-- Estilos estandar para los controles JQuery -->
		<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
		<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
		<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
		
		<script src="../Generador/js/bootstrap.bundle.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
		
		<script type="text/javascript">
		 
		 $(document).ready(function() {
		 	$("#sendBtn").button().click(
		 		function(){
		 			sendFile();
		 		}
		 	);
		 } );
		 
		 function sendFile(){
		 	if( $("#fileCLC").val() == "" ){
		 		Swal.fire("Cargar archivo","Debe eligir el archivo de carga.", "info");
		 		return;
		 	}else if( confirm("Est seguro de realizar la carga?") ){
		 		$("#frmCLC").submit();
		 	}
		 }
		</script>
	</head>
	<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
 		<form id="frmCLC" name="frmCLC" method="post" enctype="multipart/form-data" action="../AdjuntaCLCServlet">
			<div id="container" class="container">
				<div class="card-header"> <h3> Carga masiva de CLC </h3> </div>
				<br/>
				
				<h6> Cargar Archivo </h6> 
				<hr class="mt-3"/>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-10 col-md-10 col-sm-12">	
						<label for="fileCLC"></label>
			  			<input class="form-control form-control-sm" type="file" id="fileCLC" name="fileCLC" style="width: 25em;" />																							
					</div>												
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
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