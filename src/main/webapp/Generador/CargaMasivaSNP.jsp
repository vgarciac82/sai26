<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<% String msg = (String)session.getAttribute("MENSAJE_CARGA"); 
	session.removeAttribute("MENSAJE_CARGA");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Carga masiva de SNP</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>

<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>

<script type="text/javascript">

	$(document).ready(function() {
		$("#enviar").button().click(
			function(){
				if( $("#archivoCarga").val() == "" )
					Swal.fire("Importante","Debe seleccionar un archivo xls para enviar.", "info");
				else{
					$.blockUI();
					$("#uploadCargaMasivaSNP").submit();
				}
			}
		);
		
		$("#dlg-inf").dialog({
			autoOpen : <%=msg != null%>,
			height : 250,
			width : 400,
			modal : true,
			buttons : {
				"Aceptar" : function() {
					$(this).dialog("close");
				}
			}
		});
		
	});
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
<br/>
	<form id="uploadCargaMasivaSNP" name="uploadCargaMasivaSNP" action="../CargaMasivaSNP" method="post" enctype="multipart/form-data">
	<div id="container" class="container" style="width: 60%">		
		<div class="card-header"> <h3> Carga masiva de SNP </h3> </div>
			<div class="mt-4 row d-flex justify-content-center">
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
					<p>
								Seleccione el archivo Excel que contiene la carga masiva 
								para su proceso y de clic en el boton "Enviar Archivo" 
								<br>
								Nota: Solo se admiten archivos Excel formato 97-2003 (xls)
					</p>
				</div>
			</div>
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
					<input type="file" id="archivoCarga" name="archivoCarga" class="form-control form-control-sm" style="width: 30em;" onblur = "LimitAttach(this, - 1);"/>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<input type="button" id="enviar" name="enviar" value="Enviar" class="btn btn-secondary"/>
				</div>
			</div>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
					<a href="../Reportes/PlantillaCargaMasivaSNP.xls" class="link-primary">Descargar plantilla de muestra</a>
				</div>
			</div>				
			
		</div>
		
		<div title="Mensaje" id="dlg-inf">
			<fieldset>
				<legend>
					Rersultado de la carga...
				</legend>
				<textarea rows="10" cols="40" class="form-control form-control-sm">
					<%=msg %>
				</textarea>
			</fieldset>
		</div>
		
	</form>
</body>
</html>