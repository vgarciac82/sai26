<%@page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>


<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

	String msg = "";
	if (session.getAttribute("RESULT") != null) {
		msg = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>

<title>'Subir adecuaciones de MAP'</title>

<!-- Estilos estandar para los controles JQuery -->

<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">

<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-3.5.1.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>	
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>

<style type="text/css" title="currentStyle">
@import "css/demo_page.css";
</style>

<script type="text/javascript">
	var msg = "<%=msg.replace("\n", "\\\\n")%>";
	$(document).ready(function() {

		$('#list-opciones button').on('click', function (e) {
			  e.preventDefault()
			  $(this).tab('show')
			})
		$("#btnEjecutarSicopEnc").click(function() {
			
			archivoSeleccionado();
		});
		
		
		consulta();
		$("#mesAdec").val(1);
	 
	
		
	
	$("#btnConsulta").click(function() {
			
			$("#FormUpload").attr("action", "../consultaLayout");
			$("#FormUpload").submit();	
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


		if (msg != "") {
			$("#msgDialog").dialog("open");
			$("#divEsperaProcesando").attr("style", "visibility=hidden");
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
	function validar() {
		if ($("#esIp").attr("checked")) {
			$("#archivoCargar").css("display", "none");
			$("#lbLayout").css("display", "none");
			$("#cEsIP").val("S");

		} else {
			$("#archivoCargar").css("display", "block");
			$("#lbLayout").css("display", "block");
			$("#cEsIP").val("N");
		}
	}
	function validarIP(){
		if ($("#consultaIP").attr("checked")) {
			$("#cEsIP").val("S");
		} else {
			$("#cEsIP").val("N");
		}
	}
	function consulta(){
	
		querySelectPost( "mesesCargados","mesesAdec",{ async : false } )
		$("#nMes").val($("#mesesAdec").val());
		
		//$("#mesesAdec").change(function(){querySelectPost("versionModificado", "idVersion", {	async : false});});	
		querySelectPost("versionModificado", "idVersion", { async : false });
		
	}
//function consulta2(){
	
	// var version = $("select idVersion option[value = idVersion]").attr('selected', 'selected');
		
//	}

	function limpiarSesion() {
		window.location.href = "SubirArchivoAdecuaciones.jsp";
	}

	function validaEnvio() {
		
		$("#FormUpload").submit();		
	}

	function archivoSeleccionado() {
		var fichero = "";
		var correcto = false;
		var nombre = "";


		if ($("#esIp").attr("checked")) {
			$("#FormUpload").attr("action", "../procesaLayout");
			validaEnvio();
			return;
		} else {
			if ($("#archivoCargar").val() != "") {
				fichero = $("#archivoCargar").val();
				fichero = fichero.split('\\');
				nombre = fichero[fichero.length - 1];

				if (nombre.toUpperCase() == "ADECUACIONES_MAP.CSV")
					correcto = true;

				if (!correcto) {
					alert("Seleccione el archivo se debe llamar ADECUACIONES_MAP.CSV");
					return false;
				}
				$("#btnEjecutarSicopEnc").attr("disabled", true);

				$("#FormUpload").attr("enctype", "multipart/form-data");
// 				$("#FormUpload").attr("target", "_blank");
				$("#FormUpload").attr("action", "../CargaArchivosAdecuacionesServlet");
				validaEnvio();

			} else {

				alert("Para continuar debe seleccionar un archivo a cargar.");
				return false;
			}
			return true;
			
		}
	}
</script>
</head>
<body id="dt_example">
	<form id="FormUpload" name="FormUpload" method="POST" action="../CargaArchivosAdecuacionesServlet">
		<div id="container" class="container">
			<input type="hidden" id="usuario" name="usuario" value="<%=usuario.getLogin()%>">
			<input type="hidden" id="nMes" name = "nMes" value = "0">
			<input type="hidden" id="cEsIP" name = "cEsIP" value = "N">
			<h1>Cargar Archivo adecuaciones MAP</h1>
				<div class="row d-flex justify-content-center"><!--Inicio Div row para iniciar los tabs-->
					<ul class="nav nav-tabs" id="list-opciones" role="tablist">
						<li class="nav-item" role="presentation">  
							<button class="nav-link active" href="#tabs-1" onClick="consulta();" role="tab" aria-controls="tabs-layout" aria-selected="true">Carga de adecuaciones</button>
						</li>   
						<li class="nav-item" role="presentation">  
							<button class="nav-link"  href="#tabs-2" role="tab" aria-controls="tabs-archivos" aria-selected="false" >Consulta archivos cargados</button>
						</li>
					</ul>   	     		           
        		<div class="tab-content mt-3" id="tabContent"><!--Inicio div contenido tabs-->		
					<div class="tab-pane fade show active" id="tabs-1" role="tabpanel" aria-labelledby="tabs-layout"><!--Inicio tab-1-->	                             
	            		<div class="row d-flex">	
	            			<div class="col-6 col-lg-3 col-md-3 col-sm-6 p-1">
	            				Ingresos Propios:
	            			</div>
	            			<div class="col-6 col-lg-3 col-md-3 col-sm-6 p-1">
	            				<input type="checkbox" name="esIp" id="esIp" value="0" onclick="validar()" class="form-check-input" />
	            			</div>
	            		</div>
	            		<div class="row d-flex">	
	            			<div class="col-6 col-lg-3 col-md-3 col-sm-6 p-1">
	            				Mes a Cargar:
	            			</div>
	            			<div class="col-6 col-lg-3 col-md-3 col-sm-6 p-1">
	            				<select name="mesAdec" id="mesAdec" class="form-select form-select-sm">
										<option value=1>Enero</option>
										<option value=2>Febrero</option>
										<option value=3>Marzo</option>
										<option value=4>Abril</option>
										<option value=5>Mayo</option>
										<option value=6>Junio</option>
										<option value=7>Julio</option>
										<option value=8>Agosto</option>
										<option value=9>Septiembre</option>
										<option value=10>Octubre</option>
										<option value=11>Noviembre</option>
										<option value=12>Diciembre</option>
								</select>
	            			</div>
	            		</div>
	            		<div class="row d-flex">	
	            			<div class="col-6 col-lg-3 col-md-3 col-sm-6 p-1">
	            				<label>Layout (*.csv):</label>
	            			</div>
	            			<div class="col-6 col-lg-6 col-md-6 col-sm-6 p-1">
	            				<input type="file" id="archivoCargar" name="archivoCargar" size="20" onblur="LimitAttach(this, - 1);" class="form-control">
	            			</div>
	            		</div>
	            		<div class="row d-flex">
	            			<div class="col-6 col-lg-6 col-md-6 col-sm-6 p-1">
	            			</div>
	            			<div class="col-6 col-lg-3 col-md-3 col-sm-6 p-1">
	            				<input type="button" name="btnEjecutarSicopEnc" id="btnEjecutarSicopEnc" value="Ejecutar" class="btn btn-secondary"/>
	            			</div>
	            		</div>
	            	</div>
	            	
					<div class="tab-pane fade show" id="tabs-2" role="tabpanel" aria-labelledby="tabs-archivos"><!--Inicio tab-2-->	                             
	            		<div class="row d-flex">	
	            			<div class="col-6 col-lg-3 col-md-3 col-sm-6 p-1">	
	            				Ingresos Propios:
	            			</div>
	            			<div class="col-6 col-lg-3 col-md-3 col-sm-6 p-1">
	            				<input type="checkbox" name="consultaIP" id="consultaIP" class="form-check-input" value="0" onclick="validarIP()"/>
	            			</div>
	            		</div>
	            		<div class="row d-flex">	
	            			<div class="col-6 col-lg-3 col-md-3 col-sm-6 p-1">	
	            				Mes a consultar:
	            			</div>
	            			<div class="col-6 col-lg-3 col-md-3 col-sm-6 p-1">
	            				<select name="mesesAdec" id="mesesAdec" onchange="consulta();" class="form-select form-select-sm">
										<option value=1>Enero</option>
										<option value=2>Febrero</option>
										<option value=3>Marzo</option>
										<option value=4>Abril</option>
										<option value=5>Mayo</option>
										<option value=6>Junio</option>
										<option value=7>Julio</option>
										<option value=8>Agosto</option>
										<option value=9>Septiembre</option>
										<option value=10>Octubre</option>
										<option value=11>Noviembre</option>
										<option value=12>Diciembre</option>
										</select>
	            			</div>
	            		</div>
	            		<div class="row d-flex">	
	            			<div class="col-6 col-lg-3 col-md-3 col-sm-6 p-1">	
	            				Versión:
	            			</div>
	            			<div class="col-6 col-lg-3 col-md-3 col-sm-6 p-1">
	            				<select name="idVersion" id="idVersion" class="form-select form-select-sm">
										<option>Selecciona una version</option>
									</select>
	            			</div>
	            		</div>
	            		<div class="row d-flex">
	            			<div class="col-6 col-lg-6 col-md-6 col-sm-6 p-1">
	            			</div>
	            			<div class="col-6 col-lg-3 col-md-3 col-sm-6 p-1">
	            				<input type="button" name="btnConsulta" id="btnConsulta" value="Consultar" class="btn btn-secondary"/>
	            			</div>
	            		</div>
	            	</div>
		        </div>
			</div>
		</div>
	
		<div id="msgDialog" title="Resultado de Carga">
			<h4>Resultado de carga.</h4>
			<div class="row d-flex">
	            	<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
	            		<td align="center"><textarea rows="10" cols="40" id="msgTxt"><%=msg.replaceAll("<br>", "\n*")%></textarea>
	            	</div>
	        </div>
		</div>
		<div id="divEsperaProcesando" style="visibility: hidden"
			align="center">
			Espere por favor... <img border="0" src="../imagenes/espera.gif"
				height="30">
		</div>
		

	</form>
</body>
</html>
