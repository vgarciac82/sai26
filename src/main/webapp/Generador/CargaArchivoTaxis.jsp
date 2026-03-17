<%@page language="java" pageEncoding="utf-8"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.reportes.servlet.*"%>
<%@page import="java.text.DecimalFormat"%>
<%

Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
boolean error = "SI".equals( request.getParameter("error") );

String importado ="";

String mensaje = (String) session.getAttribute("msg");
	boolean msgResult = false;
	if (mensaje != null) {
		session.removeAttribute("msg");
		msgResult = true;
	} else
		mensaje = "";
	
	
	if (request.getParameter( "importado" ) != null && !"".equals(request.getParameter( "importado" )) ){
		importado = request.getParameter("importado") ;
		}
%>


<!DOCTYPE HTML PUBLIC>
<html>
<head>

<title>Cargar archivo de Taxis</title>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
  	<link rel="stylesheet" href="https://cdn.datatables.net/1.13.8/css/jquery.dataTables.min.css">
  	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
  	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css"/>
  	<link rel="stylesheet" href="../SISECOP/css/style.css">
  	
</head>
<body id = "dt_example" >
	<form id="ExportarForm" name="ExportarForm" action="../reportes/CargaArchivoTaxiServlet" enctype="multipart/form-data" method="post">
		<div class="card mt-2">
		<div class="card-header"> <h3> Carga de layout de Taxis </h3> </div>
		</div>
		<br>
		<div id="contCarga">
			<div class="row" >
				<div class="col-4">
					Cargar Archivo con extension .csv:
					<input id="cargaArchivo" name="cargaArchivo" type="file" size="25" onblur="LimitAttach(this, - 1);" class="form-control"/>
				</div>
				<div class="col-2">
					<br>
					<input type="button" id="importa" name="importa" value="Carga Archivo" class="btn btn-secondary" />
				</div>
			</div>
		</div>
		<div id="divTaxis" class="table-responsive">
			<div class="row" >
				<div class="col-11">
				<table id="tablaTaxis" class="table table-striped table-bordered">
					<thead>
					    <tr>
					      <th>Fecha</th>
					      <th>No Emp</th>
					      <th>Nombre Completo</th>
					      <th>Destino</th>
					      <th>UR</th>
					      <th>Descripción UR</th>
					      <th>Agenda</th>
					      <th>Folio</th>
					      <th>Monto</th>
					    </tr>
					</thead>
					<tbody></tbody>
				</table>
				</div>
			</div>	
		</div>
		<input type="hidden" id="seImporto" name="seImporto" value="<%=importado%>" />
		
		<div class="modal fade" id="dlgRespuesta" tabindex="-1" aria-labelledby="dlgRespuestaLabel" aria-hidden="true">
			<div class="modal-dialog modal-dialog-centered modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="dlgRespuestaLabel">Mensaje del servidor</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
					</div>
					<div class="modal-body">
						<div class="mb-3">
							<label class="form-label fw-semibold">Respuesta:</label>
							<textarea rows="10" class="form-control" readonly><%=mensaje%></textarea>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-primary" data-bs-dismiss="modal">Cerrar</button>
					</div>
				</div>
			</div>
		
	<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>	
  	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  	<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
  	<script src="https://cdn.datatables.net/1.13.6/js/dataTables.bootstrap5.min.js"></script>
  	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
		
		<script type="text/javascript" charset="utf-8">
		
			var msgResult = <%=msgResult%>;
			$(document).ready(function() {
		
				var modalRespuesta = new bootstrap.Modal(document.getElementById('dlgRespuesta'), 'data-bs-backdrop');
				
				$("#importa").button().click(function() {

					$("#ExportarForm").submit();
		
				});
		
				cargarTaxis();
				modalRespuesta.hide();
				
				if(msgResult != ""){
					modalRespuesta.show();
				}
		
			});
		
		
			function LimitAttach(tField, iType) {
				var file = tField.value;
				var extArray = new Array(".csv");
				var allowSubmit = false;
		
				if (!file) {
					return;
				}
		
				while (file.indexOf("\\") != -1) {
					file = file.slice(file.indexOf("\\") + 1);
				}
				var ext = file.slice(file.indexOf(".")).toLowerCase();
				for (var i = 0; i < extArray.length; i++) {
					if (extArray[i] == ext) {
						allowSubmit = true;
						break;
					}
				}
		
				if (!allowSubmit) {
					tField.value = "";
					alert("Usted sólo puede subir archivos con extensiones " + (extArray.join(" ")) + "\nPor favor seleccione un nuevo archivo");
					limpiarSesion();
				}
			}
		
			function limpiarSesion() {
				window.location.href="CargaArchivoTaxis.jsp";
			}
			
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
			
			function cargarTaxis() {
				
		    	var oTable = new DataTable('#tablaTaxis', {

		    		ajax: {
		    			url:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=nt&ql=vLayoutTaxis",
		    			type: 'POST'
		    		},
		    		columns: [
		    			{data: "faplicacion", 		visible: true, searchable: true, orderable: true},
		                {data: "nidempleado", 		visible: true, searchable: true, orderable: true},
		                { data: "nombreCompleto", 	visible: true, searchable: false, orderable: false },
		                { data: "cDestino",	 		visible: true, searchable: false, orderable: false },
		    			{ data: "cUnidadResponsable", 	visible: true, searchable: true, orderable: false },
		    			{ data: "cUR", 				visible: true, searchable: false, orderable: false },
		    			{ data: "nidComision", 		visible: true, searchable: false, orderable: false },
		    			{ data: "cFolioTaxi", 		visible: true, searchable: false, orderable: false },
		    			{ data: "mmonto", 			visible: true, searchable: false, orderable: false }
		    		],
		    		order: [[1, 'asc']],
		    		rowId: 'ep',
		    		processing: true,
		    		serverSide: true,
		    		scrollCollapse: true,
		    		language: es_mx,
		    		paging: false,
		    		searching: true,
		    		bDestroy: true,
		    		"bInfo" : false
		    	});
			}
			
		</script>
	</form>
</body>
</html>
