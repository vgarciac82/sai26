<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Editar la Comisión Autorizada</title>
<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>

</head>
<body>
	<div id="container" class="container">
	<form id="formPagos" name="formPagos" >
	<input type="hidden" name="idComision" id="idComision" value=""/>
	<input type="hidden" name="cTipoRfc" id="cTipoRfc" value=""/>
	<input type="hidden" name="cnombre" id="cnombre" value=""/>
	<input type="hidden" name="nEmpleado" id="nEmpleado" value=""/>
<div class="row mt-4">
	<div class="card-header"><h3>  Consulta de Agendas por Empleado </h3> </div>
</div>	
	<div class="col-6 mt-2">
		<div class= "card">
			<div class="card-body"> 
				<div class="row">
					<div class="col-md-6">
						RFC Empleado					    
					        <div class="input-group">
					        	<span class="input-group-text"><i class="bi bi-person"></i></span>
					        	<input class="form-control"  type="text" name="cIdRFC_RelacionGasto" id="cIdRFC_RelacionGasto" onchange="cargaCtaBancariaRFC();"  readonly/>
					        	<input type="button" class="btn btn-secondary" name="btnBeneficiario" id="btnBeneficiario" value="..." onclick="cat_beneficiario();" />
					        </div>
					</div>
					<div class="col-md-2">
						<br>
						<input type="button" class="btn btn-secondary" name="btnBeneficiario" id="btnBeneficiario" value="Buscar" size="5" onclick="consultarAgenda();" />
					</div>
				</div>
				
			</div>
		</div>
	</div>
	<br>
		<div class="row">
			<div id="divTablaAgenda" class="table-responsive">
						<table id="tablaAgenda" class="table table-striped table-bordered">
							<thead>
							    <tr>
							      <th>#</th>
							      <th>Nombre</th>
							      <th>Total Agenda</th>
							      <th>Anticipos</th>
							      <th>Pagos</th>
							      <th>Terminado</th>
							    </tr>
							</thead>
							<tbody></tbody>
						</table>
				</div>
		</div>
		<div class="row" id="solicitudes" name="solicitudes">
				<p class="h6">Solicitudes</p>
					<div class="table-responsive text-nowrap">
						<table id="tablaSolicitud" class="table table-striped table-bordered">
							<thead>
							    <tr>
							      <th>#</th>
							      <th>Evento</th>
							      <th>Viaticos</th>
							      <th>Local</th>
							      <th>Total</th>
							      <th>Anticipo</th>
							      <th>Comprobación</th>
							      <th>Estatus</th>
							    </tr>
							</thead>
						</table>
					</div>
			</div>
	</form>
<div class="modal" tabindex="-1" role="dialog" id="dialog-Detalle" data-mdb-keyboard="true" data-mdb-backdrop="static">
 	<div class="modal-dialog modal-xl" role="document">
    	<div class="modal-content">
		    <div class="modal-header">
		        <h6 class="modal-title">Mostrar los datos de la comision</h6>
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		    </div>
      		<div class="modal-body">
				<jsp:include page="ConsultaComisionViaticos.jsp"></jsp:include>
			</div>
		 	<div class="modal-footer">
        		<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
      		</div>
    	</div>
  	</div>
</div>
	<script type="text/javascript" src="../Generador/js/jquery-3.5.1.min.js"></script>
	<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../Generador/js/Moment.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-ui.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/js/bootstrap-datetimepicker.min.js"></script>
	<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="../Generador/js/resumenAgenda.js"></script>
	<script type="text/javascript" src="../Generador/js/ConsultaComision.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../js/jsquery.js"></script>
	
	</div>			
</body>
</html>