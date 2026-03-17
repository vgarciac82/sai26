<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%
	Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if( u == null ){
		response.sendRedirect("../index.jsp");
		return;
	}
	
	boolean esRespuesta = request.getParameter("RESPUESTA") != null ? true: false;
	String ctaBan = request.getParameter("ctaBan");
	String cMes = request.getParameter("mes");
	String tipo = request.getParameter("cTipoDoc");
	String nombreCarpeta ="";
	String tipoMensaje = "";
	
	if( tipo == null  || "DOCCOMP".equalsIgnoreCase(tipo) ){
		nombreCarpeta = "Documentacion Comprobatoria";
		tipoMensaje = " la conciliacion bancaria ";	
	}else if( "EDOCTA".equals(tipo) ){
		nombreCarpeta = "Estado de Cuenta";
		tipoMensaje = " el estado de cuenta ";	
	}
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
	<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css">
	
	<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
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
	<script type="text/javascript">
		var ctaBan = "<%=ctaBan%>";
		var mes = <%=cMes%>;
		var nombreCarpeta = "<%=nombreCarpeta%>";
		
		$(document).ready(function() {
			$("#mes").change(function(){cambiaMes();});
			$("#closeBtn").button().click(function(){closeBtn();});
			
			$("#dlgRespuesta").dialog(
			{
				autoOpen : false,
				height : 400,
				width : 400,
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
			
			$("#CTAB").val(ctaBan);
			$("#nMes").val(mes);
			$("#mes").val(mes);
			$("#nombreCarpeta").val(nombreCarpeta);
			$("#btnEnviar").button();
			if( <%=esRespuesta%> ){
				$("#cargaDiv").hide();
				$("#dlgRespuesta").dialog("open");
			}
		});
		
		function cambiaMes(){
			$("#nMes").val( $("#mes").val() );
		}
		
		function cargar(){
			
			var tipo = $("#archivoCB").val().substr($("#archivoCB").val().length - 3);
			
			if( $("#archivoCB").val() != "" ){
				if ( tipo == "pdf"){
					$("#btnEnviar").prop( "disabled", true);
					$.blockUI({message: "Procesando espere ......"});
					$("#frmUpload").submit();
				}
				else{
					alert("El archivo a adjuntar tienen que ser un PDF.");
				}
			}else{
				alert("Debe elgir el archivo a enviar.");
			}
			
		}
		
		function closeBtn(){
			parent.aceptarCarga();
		}
	</script>

</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form id="frmUpload" name="frmUpload" enctype="multipart/form-data" action="../uploadConciliacionBan" method="post">
		<input type="hidden" name="nombreCarpeta" id="nombreCarpeta">
		<input type="hidden" id="nMes" name="nMes" value="1">
		<div id="container" class="container">
			<div id="cargaDiv">
				<div class="card">
						<h5 class="card-header">Cargar Archivo PDF</h5>
						<div class="card-body">
							<div class="row">
								<div class= "col-12">
									Seleccione el archivo PDF con <%=tipoMensaje%> y a continuacion de clic en el boton Cargar Archivo
								</div>
							</div>
							<div class="row mt-2">
								<div class= "col-12">
									Cuenta Bancaria: 
									<input type="text" class="form-control" id="CTAB" name="CTAB" size="20" readonly="readonly" style="background-color: #DADADA">
								</div>
							</div>
							<div class="row">
								<div class= "col-5">
									Mes: 
									<select id="mes" name="mes" class="form-select" disabled="disabled">
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
								</div>
							</div>
						<div class="row">
								<div class= "col-12">
									Archivo:
									<input type="file" class="form-control" name="archivoCB" id="archivoCB">
								</div>
						</div>
						<br>
						<div class="row">
								<div class= "col-12">
									<input type="button" id="btnEnviar" value="Cargar Archivo" onclick="cargar();" class="btn btn-secondary"/>
								</div>
						</div>		
					</div>
				</div>	
			</div>
			<div id="dlgRespuesta">
				<div class="card">
					<h5 class="card-header">Resultado de Operacion:</h5>
					<div class="card-body">
							<div class="row">
								<div class= "col-12">
									<textarea rows="15" class="form-control" cols="50" readonly="readonly"><%=msg%></textarea>
								</div>
							</div>
					</div>
				</div>
			</div>
		</div>
	</form>
</body>
</html>