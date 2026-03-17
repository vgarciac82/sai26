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
<title>Excepciones para Layout de Pago Diverso</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"	href="css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>


</head>

<body id="dt_example">
<form id="formViaticos" name="formViaticos">
	<input type="hidden" name="cIDContrato" id="cIDContrato" value=""/>
	<div class="form-group mt-3">
		<div id="container" class="container-fluid">
				
			<div class="row mt-2">
				<div class="card-header d-flex justify-content-center "><h4> Excepciones de layout de pagos diversos</h4> </div>
			</div>
				<div class="row mt-3 justify-content-center">	
					<div class="col-md-5">
						<div class= "card">
							<div class="card-header">
							    Seleccionar contrato para generar layout sin compromiso
							</div>
							<div class="card-body"> 
								<div class="row">	
									
								</div>
								<div class="row">
									<div class="col-md-6">
										Contrato
										<div class="input-group">	 														
											<input type="text" name="cIDContratoExcepcion" id="cIDContratoExcepcion" class="form-control form-control-sm AyudaSyC" readonly title="Selecciona un contrato"/>
										</div>					    
								       
							    	</div>
							    	<div class="col-md-1">
							    		<br>
							    		<input type="button" class="btn btn-secondary" name="btnAceptar" id="btnAceptar" value="Aceptar" onclick="agregarExcepcion()"/>
							    	</div>
							    </div>
							</div>
						</div>
					</div>
			  	</div>
			  	<br>
				<div class="row mx-2 justify-content-center">
					<div class="col-md-5">
						
								<table id="tablaExcepcion" class="table table-striped">
									<thead>
									    <tr>
									      <th>Contrato</th>
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
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/bootstrap.min.js"></script>
	<script type="text/javascript" src="js/Moment.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
	
	
</form>		
</body>
<script type="text/javascript" charset="utf-8">
$(document).ready(function() {	
	$("input.AyudaSyC").subIniciaDlg();
	$("input.autoCompletaSyC").subIniciaAutoCompleta();
	 creaTableExcepciones();
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



function creaTableExcepciones(){
		
		oTabla = $('#tablaExcepcion').dataTable(
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
						+ "/crud?rt=t&ql=vExcepcionesLayoutDiverso",
				aoColumns : [ {
					sName : "cIdContrato"
				}, {
					sName : "eliminar"
				}],
				oLanguage : es_mx
			});			
			
	}
	
function agregarExcepcion() {
	
	queryFormPost("guardarExcepcionLayout", {async: false});
	location.reload();
	
}

function eliminarRegistro(cIdContrato) {
	Swal.fire({
		  title: '¿ Desea eliminar el registro seleccionado ?',
		  text: "Se elimina el contrato para que se genere layout con compromiso.",
		  icon: 'warning',
		  showCancelButton: true,
		  confirmButtonColor: '#288BA8',
		  cancelButtonColor: '#e6e6e6',
		  confirmButtonText: 'Aceptar',
		  cancelButtonText: 'Cancelar'
		}).then((result) => {
		  if (result.isConfirmed) {
			  	var trClone = $('#tablaExcepcion tr:eq(' +  cIdContrato + ')').clone();
				$("#cIdContrato").val(trClone.find('td:eq(0)').html());
				queryFormPost("eliminarExcepcionLayout", {async: false}); 
				location.reload();
		  }
		});
}
	
</script>
</html>
