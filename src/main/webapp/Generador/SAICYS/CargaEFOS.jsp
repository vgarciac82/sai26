<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
%>

<!DOCTYPE html>
<html>
  <head>
    
    <title>My JSP 'CargaEFOS.jsp' starting page</title>
    <meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  	<style type="text/css" title="currentStyle"> 
 		@import "../css/demo_page.css";
		@import "../css/demo_table_jui.css"; 
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../../css/interfaz.css";
	</style>
	
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
	<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>
		
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/funciones.js"></script>
	<link href="../../css/reportesGRM.css" rel="stylesheet" type="text/css" />
	
		
		<script type="text/javascript">
		  	$(document).ready(function() {
		  		$("input.AyudaSyC").subIniciaDlg();
		  		cssDisabledTextArea();
		  		$(".custom-file-input").on("change", function() {
		  		  var fileName = $(this).val().split("\\").pop();
		  		  $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
		  		});

			});//Fin del document ready
			
			function ejecutaAjax(){
				var hayDocumento=false;
				var data = new FormData();
				jQuery.each(jQuery('#nameArchivo')[0].files, function(i, file) {
				    data.append('file-'+i, file);
				    hayDocumento=true;
				});
				if(!hayDocumento){
					swal("Favor de seleccionar un archivo.",{icon:"info",button: "Cerrar"});
					return;
				}
				data.append('operacion', $("#operacion").val());
				data.append('namePlantilla', $("#namePlantilla").val());
				$.blockUI({message: "Procesando espere ......"});
				$("#observaciones").val('');
				jQuery.ajax({
				    url: '../../servlet/LeeArchivos',
				    data: data,
				    cache: false,
				    contentType: false,
				    dataType: "json",
				    processData: false,
				    method: 'POST',
				    type: 'POST', // For jQuery < 1.9
				    success: function(j){
				    	$("#observaciones").val(j[0].MSG);
				    	$.unblockUI();
				    },
				    error: function(j){
				    	$("#observaciones").val(j[0].MSG);
				    	$.unblockUI();
				    }
				});
			}
		</script>
  </head>
  
  <body >
  	<form id="formArchivoEFO" action="../../servlet/LeeArchivos?operacion=1"  enctype = "multipart/form-data" method = "post">
		<div class="container-fluid">
			<div class="col-md-12 col-lg-12 col-sm-12">
	 			<fieldset class="form-group border p-3">
	 				<legend class="w-auto px-2"> Carga de archivo EFOS</legend>
	 				<div class="form-group">
						<div class="form-group row">
							<div class="form-group col-md-4">
								<div class="input-group">
								  <div class="custom-file">
								    <input type="file" class="custom-file-input" id="nameArchivo"
								      aria-describedby="inputGroupFileAddon01">
								    <label class="custom-file-label" for="nameArchivo">Adjuntar Archivo Excel con extenci&oacute;n xls o xlsx</label>
								  </div>
								</div>
							</div>
						</div>
						<div class="form-group row">
							<div class="col-md-auto">
								<input id="btnSubmit1" name="btnSubmit1" type="button" value="Submit" onclick="ejecutaAjax()" class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all"/>
							</div>
						</div>
						<div class="form-group row">
							<div class="form-group col-md-6">
								<label for="observaciones">Respuesta de carga de archivo</label>
    							<textarea class="form-control" id="observaciones" name="observaciones" rows="5" readonly></textarea>
							</div>
						</div>
					</div>
	 			</fieldset>
	 		</div>
	 	</div>
	 	<input type="hidden" id="operacion" name="operacion" value="1" />
	 	<input type="hidden" id="namePlantilla" name="namePlantilla" value="ReporteProveedoresEfos.xlsx" />
	 </form>
  </body>
</html>
