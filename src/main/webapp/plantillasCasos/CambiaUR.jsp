<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (u == null)
		response.sendRedirect("../index.jsp");

	String login = u.getLogin();
	String urActual = u.getU_UR();
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Cambiar Unidad Responsable</title>

<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

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
	var login = '<%=login%>';
	var urActual = '<%=urActual%>';
	$(document).ready(function() {

		$("#login").val(login);
		$("#unidadResponsable").val(urActual);
		queryFormPost({
			queryName : "nombreUsuarioRead",
			async : false,
			callback : function() {}
		});

		queryFormPost({
			queryName : "nombreUnidadRead",
			async : false,
			callback : function() {
				querySelectPost("catUnidadesRead", "unidadesDisponibles", {
					async : false
				});
			}
		});

		$("#cambiarUnidadBtn").button().click(function() {
			cambiaUnidad()
		});

	});

	function cambiaUnidad() {
		if( $("#unidadesDisponibles").val() == "" ) {
			Swal.fire({ icon: "warning",
						text: "Debe seleccionar la nueva unidad responsable."});			
			return;
		}
		
		Swal.fire({				  
			  text: "Al cambiarse de unidad todos los tramites y las afectaciones contables seran en la unidad seleccionada. ¿Desea continuar?",
			  icon: "warning",
			  showCancelButton: true,
			  confirmButtonColor: '#7066E0',
			  cancelButtonColor: '#e6e6e6',
			  confirmButtonText: 'Aceptar',
			  cancelButtonText: 'Cancelar'
		}).then((result) => {
			if (result.isConfirmed) {	
				$.ajax({
					url : '../usuarios/CambiaUnidad',
					type:'get',
					dataType : 'json',
					data : {
						"unidadSeleccionada" : $("#unidadesDisponibles").val()
					},
					async : false,
					success : function(json) {
						var exito = json.status == true;
						var msg = json.msg;
						Swal.fire({ icon: "info",
									text: msg});					
						if( exito ){
							Swal.fire({ icon: "success",
										text: "Cambio de unidad realizado correctamente."});						 
						}
						
					}
				});
			}
		})		
	}
</script>

</head>
<br/>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form action="../gstnmngr/gestion" method="post" id="formCambia" >
		<input type="hidden" id="login" name="login" value="">
		<input type="hidden" id="cmd" name="cmd" value="0">
		
		<div id="container" class="container">
			<div class="card-header"> <h3> Cambiar unidad responsable </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row d-flex">					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Usuario:</label>											
				</div>					
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="nombreUsuario" name="nombreUsuario" value="" class="form-control form-control-sm" readonly/>
					</div>	
				</div>
			</div>
			
			<div class="row d-flex">					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Unidad Actual:</label>											
				</div>					
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">					
					<input type="text" id="unidadResponsable" name="unidadResponsable" value="" class="form-control form-control-sm" readonly/> 					
				</div>
				<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" size="40" id="nombreUnidad" name="nombreUnidad" class="form-control form-control-sm" readonly/>
					</div>	
				</div>
			</div>
			
			<div class="row d-flex">					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Unidad Nueva:</label>											
				</div>					
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">					
					<select id="unidadesDisponibles" class="form-select form-select-sm">
						<option value="">Seleccione unidad responable</option>
					</select> 					
				</div>				
			</div>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">															
				</div>						
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="button" class="btn btn-secondary btn-sm" value="Cambiar Unidad" id="cambiarUnidadBtn" name="cambiarUnidadBtn"/>											
				</div>							
			</div>
			
		</div>
	</form>
</body>

</html>