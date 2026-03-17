<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%
	Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if( u == null ){
		response.sendRedirect("../index.jsp");
		return;
	} 
	
	boolean esRespuesta = request.getParameter("RESPUESTA") != null ? true: false;
	int idConciliacion = Integer.parseInt( request.getParameter("idConciliacion") == null ? "-1": request.getParameter("idConciliacion"));
	
	String aplicacion = StringUtils.isEmpty( request.getParameter("aplicacion") )?"":request.getParameter("aplicacion");
	
	String cMes = request.getParameter("mes");
	String tipo = request.getParameter("cTipoDoc");
	
	String msg = (String)session.getAttribute("RESULT");
	session.removeAttribute("RESULT");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Carga de Conciliacion Bancaria</title>

	<!-- Estilos estandar para los controles JQuery -->
	<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
	<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
	<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

	<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
	<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
	<script src="../Generador/js/bootstrap.bundle.min.js"></script>
	
	<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/jsquery.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>
		
	<script type="text/javascript">
		var idConciliacion = "<%=idConciliacion%>";
		var mes = <%=cMes%>;
		var nombreCarpeta = "";
		var aplicacion = "<%=aplicacion%>";
		
		$(document).ready(function() {
			
			querySelectPost({queryName: 'catalogoConciliacionRead', targetObjectId: 'conciliaciones', async: false});
			$("#mes").change(function(){cambiaMes();});
			$("#conciliaciones").change(function(){cambiaConciliaciones();});
			$("#closeBtn").button().click(function(){closeBtn();});
			
			$("#dlgRespuesta").dialog(
			{
				autoOpen : false,
				height : 400,
				width : 450,
				modal : true,
				buttons : {
							"Aceptar" : function() {
											closeBtn();
							            }
						  },
				close : function() {
							closeBtn();
						}
			});
			
			$("#conciliaciones").val(idConciliacion);
			$("#conciliaciones").change();
			$("#conciliacion").val(idConciliacion);
			
			nombreCarpeta = $("#conciliaciones option:selected").text();
			$("#msgLbl").text( nombreCarpeta );
			
			$("#nMes").val(mes);
			$("#mes").val(mes);
			$("#nombreCarpeta").val(nombreCarpeta);
			
			if( <%=esRespuesta%> ){
				$("#cargaDiv").hide();
				$("#dlgRespuesta").dialog("open");
			}
		});
		
		function cambiaConciliaciones(){
			$("#conciliacion").val( $("#conciliaciones").val() );
		}
		
		function cambiaMes(){
			$("#nMes").val( $("#mes").val() );
		}
		
		function cargar(){
			
			if( $("#archivoCB").val() != "" ){
				if(getFileExtension($("#archivoCB").val()) == "pdf"){
					$("#btnEnviar").prop( "disabled", true);
					$.blockUI({message: "Procesando espere ......"});
					$("#frmUpload").submit();
				}else{
					alert("Solo es posible cargar archivos PDF.");
				}
			}else{
				alert("Debe elgir el archivo a enviar.");
			}
			
		}
		
		function getFileExtension(filename) {
		  return filename.split('.').pop();
		}
		
		function closeBtn(){
			parent.aceptarCarga();
		}
		
		
	</script>

</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form id="frmUpload" name="frmUpload" enctype="multipart/form-data" action="../uploadConciliacionCont" method="post">
		<input type="hidden" name="nombreCarpeta" id="nombreCarpeta">
		<input type="hidden" name="aplicacion" id="aplicacion">
		
		<div id="container" class="container" style="width: 100%">
			<div id="cargaDiv" class="container">
				<div class="card-header"> <h5> Cargar Archivo PDF </h5> </div>
				<hr class="mt-3"/>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12">
						Seleccione el archivo <B>PDF</B> con el archivo:&nbsp;<label id="msgLbl" style="font-weight: bold;"></label> &nbsp;y a continuacion de clic en el boton Cargar Archivo	
					</div>
				</div>
				
				<br/>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-3 col-md-3 col-sm-3 p-1">
						<label class="form-label">Respaldo de:</label>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-6 p-1">
						<select class="form-select form-select-sm" id="conciliaciones" name="conciliaciones" disabled="disabled">
						</select>
						<input type="hidden" id="conciliacion" name="conciliacion" value="-1">
					</div>
				</div>		
				
				<div class="row d-flex">
					<div class="col-12 col-lg-3 col-md-3 col-sm-3 p-1">
						<label class="form-label">Mes:</label>
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-3 p-1">
						<select class="form-select form-select-sm" id="mes" name="mes" disabled="disabled">
								<option value="1">Enero</option>
								<option value="2">Febrero</option>
								<option value="3">Marzo</option>
								<option value="4">Abril</option>
								<option value="5">Mayo</option>
								<option value="6">Junio</option>
								<option value="7">Julio</option>
								<option value="8">Agosto</option>
								<option value="9">Septiembre</option>
								<option value="10">Octubre</option>
								<option value="11">Noviembre</option>
								<option value="12">Diciembre</option>
							</select>
							<input type="hidden" id="nMes" name="nMes" value="1">
					</div>
				</div>	
				
				<div class="row d-flex">
					<div class="col-12 col-lg-3 col-md-3 col-sm-3 p-1">
						<label class="form-label">Archivo:</label>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-6 p-1">
						<input class="form-control form-control-sm" type="file" id="archivoCB" name="archivoCB" style="width: 25em;" />						
					</div>
				</div>	
				
				<div class="row d-flex">
					<div class="col-12 col-lg-4 col-md-4 col-sm-4 p-1">						
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-6 p-1">
						<input type="button" class="btn btn-secondary btn-sm" id="btnEnviar" value="Cargar Archivo" onclick="cargar();">						
					</div>
				</div>					
							
			</div>
			
			<div id="dlgRespuesta" class="container">
				<h5> Resultado de Operacion </h5>
				<hr class="mt-3"/>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<textarea rows="15" cols="50" maxlength="1000" class="form-control"><%=msg%></textarea>						
					</div>
				</div>
				
			</div>
		</div>
	</form>
</body>
</html>