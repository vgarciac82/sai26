<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Reporte de Firma Electronica</title>
		
		<!-- Estilos estandar para los controles JQuery -->

		<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/datatables.min.css"></link>
		<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css"></link>
		
		<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
		<script src="../Generador/js/bootstrap.bundle.min.js"></script>

		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="js/ReporteFirmas.js"></script>
		<script type="text/javascript" src="../Generador/js/Moment.js"></script>
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>
		
		<script type="text/javascript">
			var oTableSolicitudes;
		
			$(document).ready(function() {
				
				oTableSolicitudes = $('#dt_solicitudes').dataTable();
				creaTablaResumen();
				$("#fIni").val(moment().format('yyyy-01-01'));
				$("#fF").val(moment().format('yyyy-MM-DD'));
								
				init();
			});
			
			function creaTablaResumen() {			
				oTableSolicitudes = $('#dt_solicitudes').dataTable({
					bPaginate : false,
					bLengthChange : false,
					bFilter : true,
					bInfo : false,
					fnInitComplete: function() {   
						oTableSolicitudes.fnAdjustColumnSizing();
	    			},
					oLanguage : {
						sProcessing : "Procesando...",
						sLengthMenu : "Mostrar _MENU_ registros",
						sZeroRecords : "No hay registros a mostrar",
						sEmptyTable : "No hay datos en la tabla",
						sLoadingRecords : "Cargando...",
						sInfo : "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty : "Registro 0 al 0 de 0",
						sInfoFiltered : "(filtado de _MAX_ registros)",
						sInfoPostFix : "",
						sInfoThousands : ",",
						sSearch : "Buscar:",
						oPaginate : {
							sFirst : "Primero",
							sPrevious : "Ant.",
							sNext : "Sigte.",
							sLast : "&Uacute;ltimo"
						}
					},
					bAutoWidth : false,
					sScrollX : 100,
					sScrollY : 200,
					bScrollCollapse : true,
					bJQueryUI : true,
					bDestroy : true,
					bServerSide : true,
				});
			}

		</script>
	
	</head>

	<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<br/>
		<form id="rptFirmaElectronica">
			<input type="hidden" id="fInicio" name="fInicio" value=""/>
			<input type="hidden" id="fFin" name="fFin" value=""/>
			
			<div id="container" class="container" style="width: 80%">									
				<div class="card-header"> <h3> Bitacora de Firma Electronica </h3> </div>
				<hr class="mt-3"/>
							
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-6 col-md-6 col-sm-12">
						<label for="fechaOper" class="form-label"> <strong> Fecha de Operación: </strong></label>
					</div>						
				</div>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<div class="form-group">
		                	<label for="fIni">Fecha Inicio:</label>
		                    <div class="input-group date" id="datepicker1">
		                    	<input type="date" class="form-control form-control-sm" id="fIni" name="fIni" value=""/>                                    
		                    </div>
		                </div>							
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<div class="form-group">
		                	<label for="fIni">Fecha Fin:</label>
		                    <div class="input-group date" id="datepicker1">
		                    	<input type="date" class="form-control form-control-sm" id="fF" name="fF" value=""/>                                    
		                    </div>
		                </div>							
					</div>						
				</div>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-6 col-md-6 col-sm-12">
						<label for="operaciones" class="form-label"> <strong> Operaciones: </strong></label>
					</div>						
				</div>		
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="checkbox" name="tramiteSel" id="VOBO" class="form-check-input" checked>
						<label for="VOBO" class="form-check-label">Visto Bueno</label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="checkbox" name="tramiteSel" id="AUT" class="form-check-input" checked>
						<label for="AUT" class="form-check-label">Autorizaci&oacute;n</label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="checkbox" name="tramiteSel" id="CANCELAR" class="form-check-input" checked>
						<label for="CANCELAR" class="form-check-label">Rechazo</label>
					</div>									
				</div>
				
				<br/>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-6 col-md-6 col-sm-12">
						<label for="firmanteLogin" class="form-check-label">Nombre Firmante:</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-person"></i></span>
							<input type="text" name="firmanteLogin" id="firmanteLogin" class="form-control form-control-sm"/>
						</div>							
					</div>
				</div>
				
				<br/>
				
				<div class="row">
					<div class="col-12 d-flex justify-content-center">
						<input type="button" id="filtrar" name="filtrar" value="Filtrar Bitacora" class="btn btn-secondary btn-sm"/>
					</div>
				</div>	
									
				<br/> 		
				
				<div id="solicitudes" class="table-responsive">	      
					<table id="dt_solicitudes" class="table table-striped">
						<thead>
							<tr>
								<th>Usuario</th>
								<th>Tramite</th>
								<th>Folio</th>
								<th>Operacion</th>
								<th>Fecha</th>
								<th>Motivo</th>
							</tr>
						</thead>												
					</table>												
				</div>
				
				<br/>
	
			</div>
		</form>
	</body>

</html>