
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>

<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	Map rol =usuario.getRoles();
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today= sdf.format(c1.getTime());
	c1.add(Calendar.MONTH, -1);
	String todayAnt= sdf.format(c1.getTime());

%>
<!DOCTYPE html>

<html>
<head>
<title>Reportes INAI</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3" />
<meta http-equiv="description" content="This is my page" />
<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
<link rel="stylesheet" type="text/css" href="../css/interfaz.css"/>


	<style type="text/css" title="currentStyle">
		@import "css/demo_page.css";
		@import "css/demo_table_jui.css"; 
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/interfaz.css";
	</style>

	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
	<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>	
	
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	<link href="../css/reportesGRM.css" rel="stylesheet" type="text/css" />
	
	
	
<script type="text/javascript" charset="utf-8">
var nameReport="Reporte_ProveedoresContratistas";					
$(document).ready(function() {
	agregaDatePickerFechas();
	
});//Fin del document ready
		
	function enviaConsulta() {
		$.blockUI({message: "Procesando espere ......"});
		event.preventDefault(); // Prevent default form submission
		let form = $("#ExportarForm");
		$.ajax({
			type: "GET",
			url: form.attr('action'),
			xhrFields: {
			  responseType: 'blob' // Handle binary data
			},
			data: form.serialize(), // Serialize form data
			success: function (data) {
				const url = window.URL.createObjectURL(data);
				const a = document.createElement('a');
				a.href = url;
				a.download = nameReport+'.xlsx'; // Specify the file name
				document.body.appendChild(a);
				a.click();
				a.remove();
				window.URL.revokeObjectURL(url); // Clean up
				$.unblockUI();
			},
			error: function (xhr, estado, error) {
				$.unblockUI();
				alert("Error contacte a soporte SAI."+ error);
			}
		});
	}
	function consultaReporte(){
		$.blockUI({message: "Procesando espere ......"});
		$('#ExportarForm').submit(function() {
		    $.get($(this).attr('action'), $(this).serialize(), function(data) {
		        if (data === 'successfull') {
		            alert('success!');
		        } else {
		            alert('A problem occurred while submitting your data. Please try again later.');
		        }
		    });
		    return false; // don't actually submit, let AJAX handle the posting
		});
	
	}
	function cambiaReporte(valorReporte){
		switch(valorReporte){
			case 1: 
				$("#trFechas").hide();
				$("#reporteNombre").val("ProveedoresContratistas");
				nameReport="Reporte_ProveedoresContratistas";
				document.getElementById("AdjudicacionDirecta").checked = false;
				document.getElementById("LicitacionesInvitaciones").checked = false;
			break;
			case 2:
				$("#trFechas").show();
				$("#reporteNombre").val("AdjudicacionDirecta");
				document.getElementById("ProveedoresContratistas").checked = false;
				document.getElementById("LicitacionesInvitaciones").checked = false;
				nameReport="Reporte_AdjudicacionDirecta";
			break;
			case 3: 
				$("#trFechas").show();
				$("#reporteNombre").val("LicitacionesInvitaciones");
				document.getElementById("AdjudicacionDirecta").checked = false;
				document.getElementById("ProveedoresContratistas").checked = false;
				nameReport="Reporte_AdjLicitacionesInvitaciones";
			break;
			default:
				alert("Debe seleccionar algun Reporte");
			break;
				
		}
	}
	function agregaDatePickerFechas(){
		$("#fInicial").datetimepicker({
			format: 'DD/MM/YYYY',
			altField: "#actualDate",
		 	currentText: "Now",
			changeYear: true
			
		});
		$("#fFinal").datetimepicker({
			format: 'DD/MM/YYYY',
			altField: "#actualDate",
		 	currentText: "Now",
			changeYear: true
			
		});
	}
	
</script>

</head>
<body >
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReportesINAI" method="get" target="_self">
		<input type="hidden" id="reporteNombre" name="reporteNombre" value="ProveedoresContratistas" />
		<div class="container-fluid">
			<div class="col-md-12 col-lg-12 col-sm-12">
			<fieldset class="form-group border p-3">
			  		<legend class="w-auto px-2">Reportes INAI</legend>
				<div class="form-group row">
					<div class="col-xs-2">
						<label for="fInicial">Fecha Inicio</label>
						<div class="input-group date" id="fInicial" data-target-input="nearest">
				          <input type="text" class="form-control datetimepicker-input" data-target="#fInicial" title="Fecha Inicial" id="fInicio" name="fInicio" value="<%=todayAnt %>"/>
				          <div class="input-group-append" data-target="#fInicial" data-toggle="datetimepicker" title="Fecha Inicial">
				            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
				          </div>
				        </div>
			        </div>
			    </div>
				<div class="form-group row">
			        <div class="col-xs-2">
						<label for="fFinal">Fecha Fin</label>
						<div class="input-group date col-xs-2" id="fFinal" data-target-input="nearest" >
							<input type="text" class="form-control datetimepicker-input" data-target="#fFinal" id="fFin" name="fFin" title="Fecha Final" value="<%=today %>"  />
							<div class="input-group-append" data-target="#fFinal" data-toggle="datetimepicker" title="Fecha Final">
							  <div class="input-group-text"><i class="fa fa-calendar"></i></div>
							</div>
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="form-check">
						<input class="form-check-input" type="radio" id="ProveedoresContratistas" name="ProveedoresContratistas" checked="checked" onclick="cambiaReporte(1)" value="ProveedoresContratistas" />
						<label class="form-check-label" >
						  Reporte de Proveedores y Contratistas
						</label>
					</div>
					<div class="form-check" style="display: none;">
						<input class="form-check-input" type="radio" id="AdjudicacionDirecta" name="AdjudicacionDirecta" onclick="cambiaReporte(2)" value="AdjudicacionDirecta" />
						<label class="form-check-label" >
						   Adjudicacion Directa
						</label>
					</div>
					<div class="form-check">
						<input class="form-check-input" type="radio" id="LicitacionesInvitaciones" name="LicitacionesInvitaciones" onclick="cambiaReporte(3)" value="LicitacionesInvitaciones" />
						<label class="form-check-label" >
						   Adjudicacion Directa, Licitaciones e Invitaciones
						</label>
					</div>
				</div>
				<div class="form-group">
					<input id="botonExtre" name="botonExtrae" type="button" value="Generar" onclick="enviaConsulta()" class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all"/>
				</div>
				
			</fieldset>
			</div>
		</div>
	</form>
</body>
</html>