<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%!Logger log = LoggerFactory.getLogger("CargarPagosDiversosMasivo.jsp"); %>
<%
	Usuario u = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if( u == null){
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String ur = u.getU_UR();
	String cc = u.getPropiedad("CCENTROCONTABLE").getValor();
	
	String msg = (String)session.getAttribute("msg");
	boolean showResult = "true".equalsIgnoreCase(request.getParameter("showResult"));
 %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Carga masiva de Certificado de Transito</title>
		
		<!-- Estilos estandar para los controles JQuery -->
		<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
		<script src="../Generador/js/bootstrap.bundle.min.js"></script>
		<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
		
		<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
		<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
		
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript"src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript"src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript"src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript"src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript"src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>
		
		<script type="text/javascript">
			
			var showResult = <%=showResult%>;
			
			$(document).ready(
				function() {
					$("#enviarBtn").button().click(function() {
						sendFile(false);
					});
					
					$("#enviarPagos").button().click(function() {
						sendFile(true);
					});
				
					creaDigalogoRespuesta();
					if (showResult){
						$("#divLblCarga").css("display", "block");
						muestraResultados();
					}
					
				}
			);
			
			function sendFile(esPago) {
				if ($("#fileName").val() == "") {
					alert("Debe eligir el archivo de carga.");
					return;
				} else if (confirm("Esta seguro de realizar la carga de los pagos diversos?")) {
					
						$.blockUI({message: "Procesando espere ......"});
					
					if (esPago ==true)
						$("#formCargar").attr("action", "../CargaMasivaPagosDiversos");
					
					$("#formCargar").submit();
					
				}
			}
			
			function creaDigalogoRespuesta() {
				$("#repuestaLog").dialog({
					autoOpen : false,
					width : "800px",
					heigth : "450px",
					modal : true
				});
			}
			
			function muestraResultados() {
				$("#repuestaLog").dialog("open");
			}
		</script>
		
	</head>
	<br/>
	<body id="dt_example" >
		<form id="formCargar" name="formCargar" method="post" enctype="multipart/form-data" action="../CargaMasivaCompromisosDiversos">
			
			<div id="container" class="container" style="width: 60%">
				<div class="card-header"> <h3> Cargar Pagos Diversos Masivos </h3> </div>
				<hr class="mt-3"/>
				
				<h6> Capture la informaci&oacute;n </h6>
				<hr class="mt-3">
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">							
						<input class="form-control form-control-sm" type="file" id="fileName" name="fileName" style="width: 25em;" />
					</div>	
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="button" class="btn btn-secondary btn-sm" id="enviarBtn" value="Enviar Compromiso">	
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="button" class="btn btn-secondary btn-sm" id="enviarPagos" value="Enviar Pagos">
					</div>																													
				</div>
				
				<br/>
							
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<div id="divLblCarga" style="display: none">
							<a href="#" onclick="muestraResultados();return false;">Mostrar resultado de carga</a>
						</div>
					</div>
				</div>

				<br/>
				
				<h5> Importante </h5>
				<hr class="mt-3">
				
				<div class="row d-flex">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<ul class="list-unstyled text-small">
							<li>* El archivo a cargar debe ser un archivo de Excel con la extensión xlsx</li>													
						</ul>
					</div>
				</div>	
				
			</div>
			
			<div id="repuestaLog" class="container" title="Atencion">
				<h5> Resultado</h5>
				<hr class="mt-3"/>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<textarea rows="20" cols="80" class="form-control"><%=msg%></textarea>						
					</div>
				</div>
	
			</div>	
		</form>
	</body>
</html>
