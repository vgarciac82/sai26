<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% 
	Usuario u = (Usuario)session.getAttribute( GestionInterface.ATT_USER );
	if( u == null ){
		response.sendRedirect( "../index.jsp" );
		return;
	}
	
	String msg = StringUtils.trimToEmpty(   (String)session.getAttribute( "msg" )  );
	session.removeAttribute( "msg" );
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Actualiza Num. REPSE</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript">
	var oTableProveedores;

	$(document).ready(function() {
		
		$("#rfc").focus();
		
		creaDialogProveedores();
		creaDialogResultado();
		creaTablaProveedores();
		
		$("#dtProveedores tbody").click(function(event) {
			$(oTableProveedores.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');
		});

		$("#dtProveedores tbody")
				.dblclick(
						function(evt) {

							var aPos = oTableProveedores
									.fnGetPosition(evt.target.parentNode);

							if (aPos instanceof Array)
								currIndex = aPos[0];
							else
								currIndex = aPos;

							var arr = oTableProveedores.fnGetData()[currIndex];

							if (arr[3] != "S")
								alert("No se ha dado el Alta de Proveedor para el RFC: "
										+ arr[0]
										+ "\n Por favor realice el tramite de Alta Proveedor");
							else {
								llenaDatos(arr[0], arr[1], arr[2]);
							}
						});
		
		$("#buscarBtn").button().click(function() {
			search();
		});
		
		
		$("#limpiarBtn").button().click(function() {
			limpiar();
		});
		
		$("#guardarBtn").button().click(function() {
			guardar();
		});
		
		$("#cancelarBtn").button().click(function() {
			limpiar();
		});
		
		  
		if( $.trim( $("#msgResult").val() ) != "" )
			$("#dlgResultado").dialog("open");
	});


	
	function limpiar(){

		$(".captura").each(function(){
			$(this).val("");
			$(this)[0].readOnly = "";
			$(this).removeClass("lectura");
		});
		
		$("#rfc").focus();
		$("#buscarBtn").show();
		$("#limpiarBtn").show();
		$("#docComprobatoriaDiv").hide();
	}

	function search() {
		if ($.trim($("#rfc").val()) === ""
				&& $.trim($("#razonSocial").val()) === "") {
			alert("Debe ingresar parametros de búsqueda:\nTodo o Parte del RFC \n y/o \nTodo o parte de la razón social");
			return;
		}

		filtraTabla();

	}

	function filtraTabla() {
		$("#dlgSeleccion").dialog("open");
		creaTablaProveedores();
	}
	function creaDialogResultado() {
		$("#dlgResultado").dialog({
			autoOpen : false,
			height : 350,
			width : 700,
			modal : true,
			buttons : {
				"Cerrar" : function() {
					$("#dlgResultado").dialog("close");
				}
			}
		});
	}
	function creaDialogProveedores() {
		$("#dlgSeleccion").dialog({
			autoOpen : false,
			height : 450,
			width : 850,
			modal : true,
			buttons : {
				"Cerrar" : function() {
					$("#dlgSeleccion").dialog("close");
				}
			}
		});
	}

	function creaTablaProveedores() {

		var where = "1<>1";
		var token = "";

		if ($.trim($("#rfc").val()) != "") {
			where = " rfc LIKE '%" + $("#rfc").val() + "%'";
			token = " AND ";
		}

		if ($.trim($("#razonSocial").val()) != "") {
			where += token + " razonSocial LIKE '%" + $("#razonSocial").val()
					+ "%'";
		}

		oTableProveedores = $("#dtProveedores").dataTable(
				{
					bAutoWidth : true,
					bSort : true,
					bPaginate : false,
					bLengthChange : false,
					bFilter : false,
					bInfo : true,
					sScrollX : "100%",
					sScrollY : "150",
					bJQueryUI : true,
					bDestroy : true,
					bServerSide : true,
					fnServerData : function(sSource, aoData, fnCallback) {
						$.ajax({
							"dataType" : 'json',
							"type" : "POST",
							"url" : sSource,
							"data" : aoData,
							"success" : fnCallback
						});
					},
					sAjaxSource : window.location.protocol + "//"
							+ window.location.host + "/"
							+ window.location.pathname.split("/")[1]
							+ "/crud?rt=t&ql=v_dt_ProveedorREPSE&qw="
							+ encodeURI(where),
					aoColumns : [ {
						sName : "rfc",
						bSortable : false
					}, {
						sName : "razonSocial"
					}, {
						sName : "numeroREPSE"
					}, {
						sName : "altaProcesada"
					} ],
					"order" : [ [ 0, "desc" ] ]
				});

	}

	function llenaDatos(rfc, razonSocial, noRepse) {
		$("#dlgSeleccion").dialog("close");
		$("#rfc").val(rfc);
		$("#razonSocial").val(razonSocial);
		
		$("#rfc").addClass("lectura");
		document.getElementById("rfc").readOnly = true;
		
		$("#razonSocial").addClass("lectura");
		document.getElementById("razonSocial").readOnly = true;
		
		$("#noRepse").val(noRepse);
		if (noRepse == "") {
			$("#noRepse").removeClass("lectura");
			document.getElementById("noRepse").readOnly = "";
			$("#noRepse").focus();
			$("#docComprobatoriaDiv").show();
			$("#buscarBtn").hide();
			$("#limpiarBtn").hide();
		} else {
			$("#noRepse").addClass("lectura");
			document.getElementById("noRepse").readOnly = true;
			alert("El proveedor ya cuenta con Numero de REPSE. Si necesita alguna modificacion notifique al administrador.");
		}

	}

	function validar() {
		if ($.trim($("#noRepse").val()) == "") {
			alert("Debe capturar el Num. de REPSE");
			$("#noRepse").focus();
			return false;
		}

		if ($.trim($("#archivoEvidencia").val()) == "") {
			alert("Debe seleccionar el archivo de evidencia.");
			$("#archivoEvidencia").focus();
			return false;
		}
		return true;
	}

	function guardar() {
		if (validar()) {
			if (confirm("Esta seguro de asignar el numero de REPSE al proveedor?")) {
				$.blockUI("");
				$("#mainFrm").submit();

			}
		}
	}
</script>
</head>
<body id="dt_example">
	<form action="../proveedores/actualizaREPSE" method="post" id="mainFrm" enctype="multipart/form-data">
		<div id="container" class="container">
			<h1>Actualiza/Captura REPSE de Proveedor</h1>
			<div class= "card">
				<div class="card-body"> 
					<p>
						Ingrese los datos para buscar el proveedor
					</p>
					<div class="row">
						<div class="col-2">RFC</div>
						<div class="col-4">
							<div class="input-group">
					        	<span class="input-group-text"><i class="bi bi-tag"></i></span>
					        	<input type="text" id="rfc" name="rfc" size="15" class="captura form-control">
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-2">Razon Social:</div>
						<div class="col-6">
							<div class="input-group">
					        	<span class="input-group-text"><i class="bi bi-person"></i></span>
					        	<input type="text" id="razonSocial" size="90"  class="captura form-control">
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-2">#REPSE:</div>
						<div class="col-4">
							<div class="input-group">
					        	<span class="input-group-text"><i class="bi bi-card-checklist"></i></span>
					        	<input type="text" id="noRepse" name ="noRepse" size="20"  class="captura form-control">
							</div>
						</div>
					</div>
					<div class="row">
						<div class="d-flex justify-content-center">
							<input type="button" id="buscarBtn" value="Buscar" class="btn btn-secondary"/>
							<input type="button" id="limpiarBtn" value="Limpiar" class="btn btn-secondary"/>
						</div>
					</div>
				</div>
			</div>
			<br>
		<div id="docComprobatoriaDiv" style="display: none">
			<div class= "card">
				<div class="card-body"> 
				 	<h1>Carga de Archivos.</h1>
					<p>
						Para obtener la evidencia ingrese a: 
						<a href="https://repse.stps.gob.mx" target="_blank">https://repse.stps.gob.mx</a>
						y obtenga la captura de pantalla donde se vea claramente la informacion del proveedor y los servicios que ofrece.
					</p>
					<p>
						<b>El archivo se espera en formato PDF, por favor guarde la captura en este formato.</b>
					</p>
					<table align="center">
						<tr>
							<td align="right">
								Archivo Evidencia:			
							</td>
							<td align="left">
								<input type="file" size="50" id="archivoEvidencia" name="archivoEvidencia" class="form-control">			
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center">
								<input type="button" id="guardarBtn" value="Guardar" class="btn btn-secondary"/>
								<input type="button" id="cancelarBtn" value="Cancelar" class="btn btn-secondary"/>
							</td>
						</tr>
					</table>
				</div>
			</div>
		</div>

		<div id="dlgSeleccion" title="Seleccion de Proveedor">
			<fieldset>
				<legend>Resultado de la B&uacute;squeda</legend>
				<table class="display" id="dtProveedores">
					<thead>
						<tr>
							<th align="center">RFC</th>
							<th align="center">Razon Social</th>
							<th align="center"># REPSE</th>
							<th align="center">Registrado</th>
						</tr>
					</thead>
				</table>
			</fieldset>
		</div>
		
		<div id="dlgResultado" title="Resultado de Actualizacion!">
			<fieldset>
				<legend>Mensaje del Sistema:</legend>
				<textarea rows="10" cols="80" id="msgResult"><%=msg%></textarea>
			</fieldset>
		</div>
	</form>
</body>
</html>