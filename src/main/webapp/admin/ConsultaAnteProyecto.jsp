<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Enumeration"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	if (session.getAttribute(GestionInterface.ATT_USER) == null)
		response.sendRedirect("../index.jsp");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Consulta AnteProyecto</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"
	href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"
	href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_page.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js">
	
</script>

<script type="text/javascript" src="../Generador/js/crud.js">
	
</script>

<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js">
	
</script>

<script type="text/javascript" src="../Ayudas/js/autoCompleta.js">
	
</script>

<script type="text/javascript" src="../js/jsquery.js">
	
</script>

<script type="text/javascript"
	src="../Generador/js/jquery.dataTables.js">
	
</script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery-ui-1.8.16.custom.min.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.ui.datepicker.js">
	
</script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js">
	
</script>
<script type="text/javascript" src="../js/catalogo/general.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.all.js">
	
</script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js">
	
</script>

<script type="text/javascript">
	function filtraInbox() {
		$("#cUE").val($("#col1_filter").val());
		$("#cUN").val($("#col2_filter").val());
		$("#cEP").val($("#col3_filter").val());
		$("#mMontoCalculado").val($("#col4_filter").val());
		$("#mMontoOptimo").val($("#col5_filter").val());
		$("#mMontoIrreductible").val($("#col6_filter").val());

		var token = "";
		var condicionWhere = "";
		if ($("#col1_filter").val() != "") {
			condicionWhere += token + "cUE='" + $("#col1_filter").val() + "'";
			token = " AND ";
		}
		if ($("#col2_filter").val() != "") {
			condicionWhere += token + "cUN='" + $("#col2_filter").val() + "'";
			token = " AND ";
		}
		if ($("#col3_filter").val() != "") {
			condicionWhere += token + "cEP='" + $("#col3_filter").val() + "'";
			token = " AND ";
		}
		if ($("#col4_filter").val() != "") {
			condicionWhere += token + "mMontoCalculado='"
					+ $("#col4_filter").val() + "'";
			token = " AND ";
		}
		if ($("#col5_filter").val() != "") {
			condicionWhere += token + "mMontoOptimo='"
					+ $("#col5_filter").val() + "'";
			token = " AND ";
		}
		if ($("#col6_filter").val() != "") {
			condicionWhere += token + "mMontoIrreductible='"
					+ $("#col6_filter").val() + "'";
			token = " AND ";
		}

		creaDT(condicionWhere);
	}

	function creaDT(condicion) {
		var urlDT = window.location.protocol + "//" + window.location.host
				+ "/" + window.location.pathname.split("/")[1]
				+ "/crud?rt=t&ql=vtanteproyecto_autorizadodt";
		if (condicion != "")
			urlDT = urlDT + "&qw=" + condicion;

		$('#dt_listado').dataTable({
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth" : true,
			"sScrollY" : 300,
			"sScrollYInner" : "100%",
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"sScrollX" : "100%",
			"bScrollCollapse" : true,
			"bServerSide" : true,
			sAjaxSource : urlDT,
			aoColumns : [ {
				sName : "cUE"
			}, {
				sName : "cUN"
			}, {
				sName : "cEP"
			}, {
				sName : "mMontoCalculado"
			}, {
				sName : "mMontoOptimo"
			}, {
				sName : "mMontoIrreductible"
			}, {
				sName : "vacio"
			} ],
			oLanguage : {
				sProcessing : "Procesando...",
				sLengthMenu : "Mostrar _MENU_ registros",
				sZeroRecords : "No hay registros a mostrar",
				sEmptyTable : "No hay datos en la tabla",
				sLoadingRecords : "Cargando...",
				sInfo : "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty : "Registro 0 al 0 de 0",
				sInfoFiltered : "(filtered from _MAX_ total entries)",
				sInfoPostFix : "",
				sInfoThousands : ",",
				sSearch : "Filtro:",
				oPaginate : {
					sFirst : "Primero",
					sPrevious : "Ant.",
					sNext : "Sigte.",
					sLast : "&Uacute;ltimo"
				}
			}
		});

	}

	$(document).ready(function() {
		creaDT("");
		$("#exportar").button().click(function() {
			$("#accion").val("ExportaExcel");
			$("#FormExportar").attr("target", "_blank");
			$("#FormExportar").submit();
			$("#FormExportar").removeAttr("target");
		});

		$("#limpiar").button().click(function() {
			window.location.href = "ConsultaAnteProyecto.jsp";
		});

	});
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<div id="container" class="container">
		<h1>Consulta AnteProyecto</h1>
		<form action="../gstnmngr/ConsultaAnteProyecto/Exportar"
			id="FormExportar" name="Exportar" method="post">
			<input type="hidden" value="Exportar" name="accion" id="accion" /> <input
				type="hidden" value="" name="cUN" id="cUN" /> <input type="hidden"
				value="" name="cUE" id="cUE" /> <input type="hidden" value=""
				name="cEP" id="cEP" /> <input type="hidden" value=""
				name="mMontoCalculado" id="mMontoCalculado" /> <input type="hidden"
				value="" name="mMontoOptimo" id="mMontoOptimo" /> <input
				type="hidden" value="" name="mMontoIrreductible"
				id="mMontoIrreductible" />
			<table width="100%">
				<tr>
					<td>
						<fieldset>
							<legend>Generación de archivo Excel</legend>
							<table align="right">
								<tr>
									<td align="justify">A continuaci&oacute;n se muestra el
										anteproyecto autorizado al momento. Ademas de contar con
										opción de Exportaci&oacute;n a excel</td>
									<td align="center" colspan="2"><input type="button"
										value="Exportar" id="exportar"> <input type="button"
										id="limpiar" name="limpiar" value="Limpiar"></input>
									</td>

								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
			</table>
		</form>
		<div id="dv">
			<table id="dt_listado" class="display" cellspacing="0"
				cellpadding="2" align="center">
				<thead>
					<tr>
						<th>UE</th>
						<th>UN</th>
						<th>EP</th>
						<th>Calculado</th>
						<th>Óptimo</th>
						<th>Ireductible</th>
						<th>&nbsp;</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
				<tfoot>
					<tr>
						<td><input type="text" name="col1_filter" id="col1_filter" />
						</td>
						<td><input type="text" name="col2_filter" id="col2_filter" />
						</td>
						<td><input type="text" name="col3_filter" id="col3_filter" />
						</td>
						<td><input type="text" name="col4_filter" id="col4_filter" />
						</td>
						<td><input type="text" name="col5_filter" id="col5_filter" />
						</td>
						<td><input type="text" name="col6_filter" id="col6_filter" />
						</td>
						<td align="center"><a href="#" onclick="filtraInbox()"> <img
								src="../imagenes/buscar.gif" alt="Filtrar..." width="16"
								height="16"> </a></td>
					</tr>
				</tfoot>

			</table>

		</div>
	</div>

</body>

</html>