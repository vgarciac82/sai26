<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	if( session == null ){
		response.sendRedirect("../index.jsp");
		return;
	}
	
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if( usuario == null )
		response.sendRedirect("../index.jsp");
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Genera Libro de Balance</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$("#aceptarBtn").button().click(
			function() {
				generaLibro();
			}
		);
	});
	
	function generaLibro(){
		if( $("#mesLibro").val() == "-1" )
			alert("Debe seleccionar el mes para generar el reporte.");
		else
			$("#formLibroBalance").submit();
	}
	
</script>

</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
<br/>
	<form action="../LibroBalance" method="post" id="formLibroBalance" target="_blank">
		<div id="container" class="container" style="width: 80%">			
			<div class="card-header"> <h3> Generar Libro de Balance </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-md-6 mb-3">										
					<label for="mesLibro" class="form-label"> Seleccione el mes para generar el libro de balance y a 
															  continuacion el boton generar. </label>											
					<select id="mesLibro" name="mesLibro" class="form-select form-select-sm" style="width: 12em;">
						<option value=1>Enero</option>
						<option value=2>Febrero</option>
						<option value=3>Marzo</option>
						<option value=4>Abril</option>
						<option value=5>Mayo</option>
						<option value=6>Junio</option>
						<option value=7>Julio</option>
						<option value=8>Agosto</option>
						<option value=9>Septiembre</option>
						<option value=10>Octubre</option>
						<option value=11>Noviembre</option>
						<option value=12>Diciembre</option>
					</select>																													
				</div>																																										
			</div>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-md-6 mb-3">
					<div class="form-check">
						<input type="checkbox" name="acumulado" id="acumulado" class="form-check-input" value = "0" checked="checked">
						<label for="acumulado" class="form-check-label">Acumulado</label>
					</div>
				</div>
			</div>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-md-6 mb-3">													
					<input type="button" id="aceptarBtn" name="aceptarBtn" value="Generar Libro" class="btn btn-secondary btn-sm"/>												
				</div>														
			</div>
						
		</div>
	</form>
</body>

</html>