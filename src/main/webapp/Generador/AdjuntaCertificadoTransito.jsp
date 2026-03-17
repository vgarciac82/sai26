<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="org.apache.log4j.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%!Logger log = Logger.getLogger("AdjuntaCertificadoTransito.jsp"); %>
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
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
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
			
			var UR = "<%=ur%>";
			var CC = "<%=cc%>";
			var showResult = <%=showResult%>;
			
			$(document).ready(
				function() {
					init();
				}
			);
			
			function init() {
		
				$("#enviarBtn").button().click(function() {
					sendFile();
				});
			
				creaDigalogoRespuesta();
				if (showResult){
					$("#divLblCarga").css("display", "block");
					muestraResultados();
				}
			}

			function sendFile() {
				if ($("#fileName").val() == "") {
					alert("Debe eligir el archivo de carga.");
					return;
				} else if (confirm("Esta seguro de realizar la carga?")) {
					if (confirm("Se adjuntaran los comprobantes al tramite "
							+ $("#TITULO_APLICACION").val() + " Es correcto?")){
						$.blockUI({message: "Procesando espere ......"});
						$("#formAdjuntar").submit();
					}
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
		<form id="formAdjuntar" name="formAdjuntar" method="post" enctype="multipart/form-data" action="../AdjuntaDocumento">
			<input type="hidden" name="TITULO_APLICACION" id="TITULO_APLICACION" value="RELACIONGASTOS">
			<input type="hidden" name="esCertificadoTransito" id="esCertificadoTransito" value="1">
			
			<div id="container" class="container" style="width: 60%">
				<div class="card-header"> <h3> Carga masiva de Certificado de Transito </h3> </div>
				<hr class="mt-3"/>
				
				<h6> Capture la informaci&oacute;n </h6>
				<hr class="mt-3">
				
				<div class="row d-flex">
					<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">	
						<label for="fileCLC"></label>			  																									
						<input class="form-control form-control-sm" type="file" id="fileName" name="fileName" style="width: 25em;" />
					</div>	
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<input type="button" class="btn btn-secondary btn-sm" id="enviarBtn" value="Enviar">
					</div>																													
				</div>
				
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
							<li>* El archivo adjunto debe ser un ZIP que contiene los Certificados de Transito.</li>
							<li>* Cada Certificado de Transito debe nombrarse identico a la CxP. Por ejemplo: 10CP2018121546.</li>							
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
