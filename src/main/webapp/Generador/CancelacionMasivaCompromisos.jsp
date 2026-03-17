<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas = false;
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		mntoCuentas = "10".equals(usuario.getPropiedad(
				"CCENTROCONTABLE").getValor());
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Cancelacion Compromisos Mensual</title>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<style type="text/css" title="currentStyle">
	@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
	@import "css/demo_table_jui.css";
	@import "css/demo_page.css";
</style>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>

<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">

	$(document).ready(function() {
		querySelectPost("catalogoMesesRead", "listadoMeses", {
			async : false
		});
		$("#btn_cancelar").button().click(function() {
			cancelarComp();
		});
	});

	function cancelarComp() {
		Swal.fire({				  
			  text: "¿Desea hacer la Cancelacion de Compromisos al mes de "
				+ $("#listadoMeses option:selected").text() + "?",
			  icon: "warning",
			  showCancelButton: true,
			  confirmButtonColor: '#7066E0',
			  cancelButtonColor: '#e6e6e6',
			  confirmButtonText: 'Aceptar',
			  cancelButtonText: 'Cancelar'
		}).then((result) => {
			if (result.isConfirmed) {
			$.ajax({
				url : '../compromiso/cancelacionMasivaMensual',
				type: "POST",
				dataType : 'json',
				data : {
					"action" : "CANCELAR_MENSUAL",
					"nMes" : $("#listadoMeses").val()
				},
				beforeSend : function(){
					$.blockUI({
						theme : true,
						title : "Cargando Informacion de la Tabla...",
						message : "<p><img src='../Generador/imagenes/wait24trans.gif' />&nbsp;&nbsp;Por favor espere...</p>"
					});
				},
				async : false,
				success : function(RS) {
					var exito = RS.success;
					var msg = RS.data_1.result;
					
					Swal.fire({ icon: "success",
								text: msn})
					
					$.unblockUI();
				},
				error : function(xhr, textStatus, errorThrown) {
					Swal.fire({ icon: "warning",
								text: "Advertencia: " + xhr.responseText + "\nEstatus: "
									  + textStatus + "\n" + errorThrown})					
					$.unblockUI();
				}
			});
			}
			return false;
		})			
	}
	
</script>

</head>
<body id="dt_example">
<br/>
	<form id="cancelaCompromisoMensual" name="cancelaCompromisoMensual"	action="" method="POST">
		<input id="cDocumentoHaplicado" name="cDocumentoHaplicado" value=""	type="hidden">
		<div id="container" class="container">		
		
			<div class="card-header"> <h3> Cancelación de Compromisos Mensual </h3> </div>
			<hr class="mt-3"/>
			
			<div class="form-group">
				<div class="row">					
					<div class="input-group">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12">	
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12">																
							<label for="listadoMeses" class="form-label">Mes:&nbsp; </label>																												
						</div>	
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">																																	
							<select id="listadoMeses" name="listadoMeses" class="form-select form-select-sm">					
							</select>
						</div>		
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							<input type="button" id="btn_cancelar" name="btn_cancelar" value="Cancelar" class="btn btn-secondary btn-sm"/>									
						</div>																					
					</div>
				</div>
			</div>
				
		</div>
	</form>
</body>
</html>