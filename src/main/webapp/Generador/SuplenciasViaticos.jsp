<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.syc.obrapublica.EjercicioFiscal"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>

<%

	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String login = usuario.getLogin();
	String operador = usuario.getNombre();
	
	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	EjercicioFiscal aEjercicioFiscal = efbl.getEjercicioFiscalActivo();
	String fechaAplicacion = Util.calculaFechaAplicacion(aEjercicioFiscal);
%>


<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Suplencias de Viaticos</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"	href="css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>

</head>

<body>
<form id="formViaticos" name="formViaticos">
	<input type="hidden" name="cTipoRfc" id="cTipoRfc" value=""/>
	<input type="hidden" name="cnombre" id="cnombre" value=""/>
	<input type="hidden" name="nEmpleado" id="nEmpleado" value=""/>
	<input type="hidden" name="cUR" id="cUR" value=""/>
	<input type="hidden" name="cPlaza" id="cPlaza" value=""/>
	<input type="hidden" name="cNivel" id="cNivel" value=""/>
	<input type="hidden" name="noEmpleado" id="noEmpleado" value=""/>
	<input type="hidden" name="noEmpleadoTitular" id="noEmpleadoTitular" value=""/>
	<input type="hidden" name="login" id="login" value="<%=login%>"/>
	

	<div id="container" class="container-fluid">
		<div class="row mt-2">
				<div class="card-header d-flex justify-content-center"><h3>Suplencias viáticos</h3> </div>
			</div>
			<div class="row mt-2">	
				<div class="col-md-1"></div>
				<div class="col-md-8">
					<div class= "card">
						<div class="card-header">
						    Captura de suplencias para el módulo de viáticos
						</div>
						<div class="card-body mt-2"> 
							<div class="row justify-content-center">
								<div class="col-md-3">
									RFC Suplencia					    
							        <div class="input-group">
							        	<span class="input-group-text"><i class="bi bi-tag"></i></span>
							        	<input class="form-control"  type="text" maxlength="15" size="15" name="cIdRFC" id="cIdRFC" readonly/>
							        	<input type="button" class="btn btn-secondary" name="btnBeneficiario" id="btnBeneficiario" value="..." size="5" onclick="cat_beneficiario();" />
							        </div>
						    	</div>
						    	<div class="col-md-3">
									RFC Titular					    
							        <div class="input-group">
							        	<span class="input-group-text"><i class="bi bi-tag"></i></span>
							        	<input class="form-control"  type="text" maxlength="15" size="15" name="cIdRFC_Titular" id="cIdRFC_RelacionGasto" readonly/>
							        	<input type="button" class="btn btn-secondary" name="btnBeneficiario" id="btnBeneficiario" value="..." size="5" onclick="catbeneficiarioTitular();" />
							        </div>
						    	</div>
						    	<div class="col-md-5">	
									Comentarios / Notas
									<input class="form-control"  type="text" maxlength="50" size="50" name="cNota" id="cNota"/>
								</div>
						    </div>
						    
							<div class="row justify-content-center mt-2">
								<div class="col-auto text-center">
									
						    		<input type="button" class="btn btn-secondary" name="btnAceptar" id="btnAceptar" value="Guardar" onclick="agregarSuplencia()"/>
						    	</div>
							</div>
						</div>
					</div>
				</div>
		  	</div>
		  	<br>
			<div class="row mx-2">
				
				<div class="col-md-12">
					<div id="divTablaSuplentes" class="table-responsive">
							<table id="tablaSuplentes" class="table table-striped table-hover">
								<thead>
								    <tr>
									  <th>#</th>
								      <th>Nombre Suplente</th>
								      <th>Plaza Suplente</th>
								      <th>#</th>
								      <th>Nombre Titular</th>
								      <th>Plaza Titular</th>
								      <th>Fecha Agregado</th>
								      <th>Usuario</th>
								      <th>Notas Suplencia</th>
								      <th>X</th>
								    </tr>
								</thead>
								<tbody></tbody>
							</table>
					</div>
				</div>
			</div>
	</div>

		
	<script type="text/javascript" src="js/jquery-3.5.1.min.js"></script>
	<script type="text/javascript" src="js/bootstrap.min.js"></script>
	<script type="text/javascript" src="js/Moment.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
	
	
</form>		
</body>
<script type="text/javascript" charset="utf-8">

$(document).ready(function() {	
	
	creaTableSuplencias();
});

var es_mx = {
		sProcessing : "Procesando...",
		sLengthMenu : "Mostrar _MENU_ registros",
		sZeroRecords : "No hay registros a mostrar",
		sEmptyTable : "No hay datos en la tabla",
		sLoadingRecords : "Cargando...",
		sInfo : "Registros _START_ al _END_ de _TOTAL_",
		sInfoEmpty : "Registro 0 al 0 de 0",
		sInfoFiltered : "(filtered from _MAX_ total entries)",
		sInfoPostFix : "",
		sInfoThousands : ",",
		sSearch : "Filtro:",
		oPaginate : {
			sFirst : "Primero",
			sPrevious : "Ant.",
			sNext : "Sigte.",
			sLast : "&Uacute;ltimo"
		}	
	
	};

function cat_beneficiario(){
	$("#cTipoRfc").val("3");
	window.open('CatalogoBeneficiarios.jsp','Beneficiarios', 'status=1, width=900px, height=430px, left=100px, resizable=yes');
	
}

function catbeneficiarioTitular() {
	$("#cTipoRfc").val("3");
	window.open('CatalogoBeneficiariosSuplencias.jsp','Beneficiarios', 'status=1, width=900px, height=430px, left=100px, resizable=yes');
	
}

function creaTableSuplencias(){
		
		var oTabla = $('#tablaSuplentes').dataTable(
			{
				"bPaginate" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=vSuplenciasViaticos",
				aoColumns : [ {
					sName : "nEmpleado"
				}, {
					sName : "nombreCompleto"
				}, {
					sName : "cPlaza"
				}, {
					sName : "nEmpleadoTitular"
				}, {
					sName : "NombreTitular"
				}, {
					sName : "plazaTitular"
				}, {
					sName : "fAgregado"
				}, {
					sName : "cUsuario"
				}, {
					sName : "notas"
				}, {
					sName : "eliminar"
				}],
				oLanguage : es_mx
			});			
		
	}
	
function agregarSuplencia() {
	
	queryFormPost("guardarSuplencia", {async: false});
	location.reload();
	
}

function eliminarRegistro(nEmpleado) {
	Swal.fire({
		  title: '¿ Desea continuar ?',
		  text: "Se eliminará la suplencia del empleado",
		  icon: 'warning',
		  showCancelButton: true,
		  confirmButtonColor: '#288BA8',
		  cancelButtonColor: '#e6e6e6',
		  confirmButtonText: 'Aceptar',
		  cancelButtonText: 'Cancelar'
		}).then((result) => {
		  if (result.isConfirmed) {
			  	var trClone = $('#tablaSuplentes tr:eq(' +  nEmpleado + ')').clone();
				$("#noEmpleado").val(trClone.find('td:eq(0)').html());
				$("#noEmpleadoTitular").val(trClone.find('td:eq(3)').html());
				queryFormPost("eliminarSuplencia", {async: false}); 
				location.reload();
		  }
		});
}
	
</script>
</html>
