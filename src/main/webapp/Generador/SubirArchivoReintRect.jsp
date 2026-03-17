<%@page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	String msg = "";
	if (session.getAttribute("RESULT") != null) {
		msg = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
	
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(
			GestionInterface.ATT_CONEXION);
	int aEjercicioFiscal = Integer
			.parseInt(adbl.obtenEjercicioFiscal());
	System.out.println(aEjercicioFiscal);
	
	boolean usrRegeneraMod = ( usuario.getPropiedad("REGENERA_MODIFICADO") != null && "SI".equalsIgnoreCase( usuario.getPropiedad("REGENERA_MODIFICADO").getValor() )  );
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>

<title>Cargar archivos Estado del Ejercicio</title>

<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>

<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>

<style type="text/css" title="currentStyle">
@import "css/demo_page.css";

@import "css/demo_table_jui.css";

@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
</style>

<script type="text/javascript">
var msg = "<%=msg%>";
var usrRegeneraMod = <%=usrRegeneraMod%>;

	$(document).ready(function() {
		
		if( usrRegeneraMod )
			$("#RegeneraModDiv").show();
		
		$("#regeneraModBtn").button().click(function(){	    	
			regeneraModificado();
	    });
		
		$("#btnAceptar").button().click(function() {
			parent.togleDivFacts(0);
		});

		$("#msgDialog").dialog({
			autoOpen : false,
			height : 250,
			width : 400,
			modal : true,
			buttons : {
				"Aceptar" : function() {
					$(this).dialog("close");
				}
			}
		});

	$("#enviar").button();
		$("#enviar").button().click(function() {
			if (validacionCorrecta()) {
				$("#FormUpload").submit();
			}
		});

		if (msg != "")
			$("#msgDialog").dialog("open");
	});

	function validacionCorrecta() {
		var msg = "";
		var tkn = "";
		if ($("#archivoSubir").val() == "") {
			msg += tkn + "Archivo a cargar.";
		}

		if (msg != "") {
			alert("Para continuar debe seleccionar un archivo a cargar");
			return false;
		}

		return true;
	}
	
	function cambiaMes(){
		$("#mesMod").val($("#mes").val());
	}
	
	function regeneraModificado(){
		var mes = $("#mes").val();
		
		Swal.fire({
			  title: '¿Desea continuar?',
			  text: "¿Esta seguro de Regenerar el Modificado para el estado del ejercicio en mes " + mes + "?",
			  icon: 'warning',
			  showCancelButton: true,
			  confirmButtonColor: '#288BA8',
			  cancelButtonColor: '#e6e6e6',
			  confirmButtonText: 'Aceptar',
			  cancelButtonText: 'Cancelar'
			}).then((result) => {
			  if (result.isConfirmed) {
				  document.FormRegenera.submit();
				if (msg != "")
					$("#msgDialog").dialog("open");
			  } 
			})
	}
	
</script>
</head>
<body id="dt_example">
<br/>
	<form id="FormUpload" name="FormUpload" method="POST" action="../CargaReintRectServlet" enctype="multipart/form-data">
		<div id="container" class="container" style="width: 80%">
			
			<div class="card-header"> <h3> Cargar Archivos Estado del Ejercicio </h3> </div>
			
			<div class="mt-4 row">
				<div class="col-4 col-lg-4 col-md-4 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="mes" class="form-label"> Selecciona el Mes: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				
					<select id="mes" name="mes" class="form-select form-select-sm" onchange="cambiaMes();">
						<option value = 1> Enero </option>
						<option value = 2> Febrero </option>
						<option value = 3> Marzo </option>
						<option value = 4> Abril </option>
						<option value = 5> Mayo </option>
						<option value = 6> Junio </option>
						<option value = 7> Julio </option>
						<option value = 8> Agosto </option>
						<option value = 9> Septiembre </option>
						<option value = 10> Octubre </option>
						<option value = 11> Noviembre </option>
						<option value = 12> Diciembre </option>
					</select>							
				</div>	
			</div>
			<div class="row d-flex justify-content-center">
				<div class="col-2 col-lg-2 col-md-2 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
					<input type="file" id="archivoSubir" name="archivoSubir" class="form-control form-control-sm" style="width: 30em;"/>
				</div>
			</div>
			<div class="mt-4 row d-flex justify-content-center">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<input type="button" id="enviar" name="enviar" value="Cargar" class="btn btn-secondary"/>
				</div>
			</div>
										
		</div>

		<div id="msgDialog" title="Resultado de Carga" class="container">
			<textarea rows="5" cols="45" id="msgTxt" class="form-control form-control-sm"><%=msg.replaceAll("<br>", "\n*")%></textarea>				
		</div>		
				
	</form>
	
	<form id="FormRegenera" name="FormRegenera" method="get" action="../reportes/RegeneraModificado" enctype="multipart/form-data">		
		<input type="hidden" id="mesMod" name="mesMod" value="0" />
		<input type="hidden" id="ejercicioFiscal" name="ejercicioFiscal" value="<%=aEjercicioFiscal%>" />
		
		<div style="display:none;" id="RegeneraModDiv" class="container">
			<!-- ARLA 14072025 Se agrega boton para regenerar el modificado. -->
					
			<h5> Operaciones para el Modificado </h5>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12 col-md-6 mb-3">													
					<input type="button" id="regeneraModBtn" name="regeneraModBtn" value="Regenerar Modificado" class="btn btn-secondary btn-sm"/>																
				</div>														
			</div>														
		
		</div>
	</form>
	
</body>
</html>
