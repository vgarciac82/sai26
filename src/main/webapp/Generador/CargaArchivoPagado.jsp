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

<title>Cargar Archivos para pagado</title>



<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" href="https://cdn.datatables.net/1.13.8/css/jquery.dataTables.min.css">
<link rel="stylesheet" href="https://code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">

</head>
<body id = "dt_example" >
	<form id="ExportarForm" name="ExportarForm" action="../reportes/CargaArchivoServlet" enctype="multipart/form-data"	method="post">
		<br>
		<h1 class="text-center mb-4">Carga para Ejercido - Pagado Automático</h1>

		<div id="contCarga" class="card shadow-sm p-4 mx-auto" style="max-width: 600px;">
			<form>
				<div class="mb-3">
					<label for="cargaArchivo" class="form-label fw-semibold">Carga Archivo en formato csv:</label>
					<input id="cargaArchivo" name="cargaArchivo" type="file" class="form-control" onblur="LimitAttach(this, -1);" />
				</div>

				<div class="d-grid">
					<button type="button" id="importa" name="importa" class="btn btn-secondary">
						Cargar Archivo
					</button>
				</div>
			</form>
		</div>
		
		<input type="hidden" id="seImporto" name="seImporto" value="<%=importado%>">

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
	    <script src="https://code.jquery.com/ui/1.13.2/jquery-ui.min.js"></script>
	    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	    <script src="https://cdn.jsdelivr.net/npm/jquery.blockui@2.70.0/jquery.blockUI.min.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" charset="utf-8">
		
			var msgResult = <%=msgResult%>;
			$(document).ready(function() {
		
				var modalRespuesta = new bootstrap.Modal(document.getElementById('dlgRespuesta'), 'data-bs-backdrop');
				
				$("#importa").button().click(function() {
					 $("#ExportarForm").submit();
				});
		
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
				window.location.href="CargaArchivoPagado.jsp";
			}
		</script>
	</form>
</body>
</html>
