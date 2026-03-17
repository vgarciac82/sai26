<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);

String msg = "";
String resultado = (String) session.getAttribute("RESULT");
if (resultado != null) { 
	msg = (String) session.getAttribute("RESULT");
	session.removeAttribute("RESULT");
}
%>

<!DOCTYPE html>
<html>
  <head>
	<link rel="stylesheet" type="text/css"	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/css/bootstrap-datetimepicker.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>
	
	<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-3.5.1.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-ui.min.js"></script>
	<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../Generador/js/Moment.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script src="../Generador/js/jquery.blockUI-2.70.0.js"></script>
	<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>

	<script type="text/javascript">
	var msg = "<%= (msg != null ? msg.replaceAll("\n", "\\n").replaceAll("\"", "\\\"") : "") %>";
	$(document).ready(function () {
		
		$("#btnCargarAjena").click(function(){ 
			enviarArchivo();
		});
		
		if (msg != ""){
			document.getElementById("divEsperaProcesando").style.display = "none";	
			document.getElementById("msgTxt").value = msg;

			// Inicializa correctamente el modal
			let modalRespuesta = new bootstrap.Modal(document.getElementById('msgDialog'), {
				backdrop: 'static',
				keyboard: true
			});
			modalRespuesta.show();
		}
		
	});
	
	function enviarArchivo(){
		
		if($("#archivoCargarOA").val() != ""){	
	  		$("#btnCargarAjena").attr("disabled",true);
	  		$("#divEsperaProcesando").attr("style","visibility=visible");
	  		$("#FormUpload").submit();
	  		
		}else{
			Swal.fire({ icon: "error",
						text: "Para continuar debe seleccionar un archivo a cargar."});		
			return false;
		}
		return true;
	}


	function LimitAttach(tField,iType){
		var file=tField.value;
		var extArray = new Array(".xlsx"); 
		var allowSubmit = false; 
		
		if (!file){ 
			return; 
		}
	
		while (file.indexOf("\\") != -1){ 
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
			tField.value="";
			limpiarSesion();
			alert("Sólo puede subir archivos con extensiones " + (extArray.join(" ")) + "\nPor favor seleccione un nuevo archivo");
			
		}
	}
	
	function limpiarSesion()
	{
		window.location.href="SubirArchivoCfdis.jsp";
	}

</script>	
</head>
<body id="dt_example">
<br/>
	<form id="FormUpload" name="FormUpload" method="POST" action="../CargaArchivosCFDI" enctype="multipart/form-data" >
	  	<div id="container" class="container" style="width: 80%">
	  		<input type="hidden" id="usuario" name="usuario" value="<%=usuario.getLogin()%>">
				<div class="container">
					
						<div class="card-header"> <h3> Comparación de archivos SAT vs SAI </h3> </div>
					  	<div class="card-body">
								<div class="row">
									<div class="col-12 col-lg-7 col-md-7 col-sm-12">
										<label for="archivoCargarOA" class="form-label"> Cargar Archivo del sistema SAT con el contenido de los CFDIs</label>
										<input type="file" id="archivoCargarOA" name="archivoCargarOA" class="form-control"  onchange = "LimitAttach(this, - 1);"  />
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12">
										<br>
										<input type="button" id="btnCargarAjena" name="btnCargarAjena" value="Cargar" class="btn btn-dark"/>
									</div>
								</div>
						</div>
				</div>		
	 	</div>
	</form>	
	<div class="modal fade" tabindex="-1" id="msgDialog" data-bs-backdrop="static" data-bs-keyboard="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">Resultado de Carga</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<div class="row">
						<div class="col-12">
							<textarea rows="10" cols="40" id="msgTxt" class="form-control form-control-sm"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
				</div>
			</div>
		</div>
	</div>

	
	<div id="divEsperaProcesando" style="visibility: hidden" align="center">
		Espere por favor...
		<img border="0" src="../imagenes/espera.gif" height="30">
	</div>

</body>
</html>
