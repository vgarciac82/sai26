<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Enumeration"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	if (session.getAttribute(GestionInterface.ATT_USER) == null)
		response.sendRedirect("../index.jsp");

	String mensajeRetorno = (String) session
	.getAttribute("MENSAJE_CARGA");
	if (mensajeRetorno != null) {
		session.removeAttribute("MENSAJE_CARGA");
	} else
		mensajeRetorno = "";

	List<List<String>> resultado = (List<List<String>>)session.getAttribute("DIFERENCIAS");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Comparaci&oacute;n SAI vs SICOP</title>

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
	var mensaje = "<%=mensajeRetorno%>";
	$(document).ready(function() {
	
					<%String value [] = request.getParameterValues("mPresupuestales");%>
					<% if(value != null){%>
						<%for( int j = 0; j < value.length; j++ ){ %>
						<%out.println("$(\"[value='" + value[j] + "']\").attr(\"checked\",true);" );%>
							
							<%}%>
					<%}%>
		$("#CargaArchivoButton").button().click(function() {
			if ($("#CargaArchivo").val() == "")
				alert("Debe seleccionar un archivo a cargar.");
			else {
				$.blockUI({
					message : "Cargando Archivo. Por favor espere ......"
				});
				$("#FormComparacion").submit();
			}
		});

		if (mensaje != "") {
			$("#resultadoTR").show();
			$("#mensajeTA").val(mensaje);
		} else
			$("#resultadoTR").hide();

		queryFormPost("readUltimaVersionComparacionsaisicop", {
			async : false
		});

		$("#generar").button().click(function() {
			$("#accion").val("GeneraReporte");
			$("#FormConsulta").submit();
		});

		$("#exportar").button().click(function() {
			$("#accion").val("ExportaExcel");
			$("#FormConsulta").attr("target", "_blank");
			$("#FormConsulta").submit();
			$("#FormConsulta").removeAttr("target");
		});

		$("input.AyudaSyC").subIniciaDlg();
		$("input.autoCompletaSyC").subIniciaAutoCompleta();

		$('#dt_comparacion').dataTable({
			"bPaginate" : true,
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth" : true,
			"sScrollY" : 270,
			"sScrollYInner" : "100%",
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"sScrollX" : "1250",
			"bScrollCollapse" : true,
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

	});
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<div id="container" class="container">
		<h1>Comparaci&oacute;n SAI vs SICOP</h1>
		<form action="../gstnmngr/ComparaSaiSicop/CargaArchivo"
			id="FormComparacion" name="FormComparacion" method="post"
			enctype="multipart/form-data">
			<fieldset>
				<table width="100%">
					<tr>
						<td>
							<table align="right">
								<tr>
									<td align="right">Ultima actualizaci&oacute;n:</td>
									<td align="left"><input type="text" size="12" value=""
										id="ultimaActualizacion" readonly="readonly" class="readOnly">
									</td>
								</tr>
							</table></td>
					</tr>
					<tr>
						<td>
							<fieldset>
								<legend>Carga Base de Datos SICOP</legend>
								<table align="center">
									<tr>
										<td align="left" colspan="2">
											<p>
												Seleccione el archivo Excel que contiene la Base de Datos
												SICOP para su comparaci&oacute;n y de clic en el boton
												"Cargar Archivo"<br> <b>Nota:</b> Solo se admiten
												archivos Excel formato 97-2003 (csv)
											</p>
										</td>
									</tr>
									<tr>

										<td align="right">Archivo Excel:</td>
										<td align="left"><input type="file" size="30"
											name="CargaArchivo" id="CargaArchivo">
										</td>
									</tr>
									<tr>
										<td align="center" colspan="2"><input type="button"
											value="Cargar Archivo" id="CargaArchivoButton">
										</td>
									</tr>
								</table>
							</fieldset></td>
					</tr>
					<tr id="resultadoTR">
						<td>
							<fieldset>
								<legend>Resultado:</legend>
								<table width="100%">
									<tr>
										<td align="center"><textarea rows="10" cols="80"
												readonly="readonly" id="mensajeTA"></textarea></td>
									</tr>
								</table>
							</fieldset></td>
					</tr>

				</table>
			</fieldset>
		</form>
		<form action="../gstnmngr/ComparaSaiSicop/Consulta" id="FormConsulta"
			name="FormConsulta" method="post">
			<input type="hidden" value="Consulta" name="accion" id="accion" />
			<fieldset>
				<legend>Selecci&oacute;n de Momentos Presupuestales</legend>
				<table align="center">
					<tr>
						<td align="left"><input type="checkbox" id="ejercido"
							name="mPresupuestales" title="mPresupuestales" value="82105"
							class="ck">Ejercido</td>
						<td align="left"><input type="checkbox" id="modificado"
							name="mPresupuestales" title="mPresupuestales" value="81102"
							class="ck">Modificado</td>
						<td align="left"><input type="checkbox" id="comprometido"
							name="mPresupuestales" title="mPresupuestales" value="82103"
							class="ck">Comprometido</td>
						<td align="left"><input type="checkbox" id="devengado"
							name="mPresupuestales" title="mPresupuestales" value="82104"
							class="ck">Devengado</td>
						<td align="left"><input type="checkbox" id="disponibleNeto"
							name="mPresupuestales" title="mPresupuestales" value="82106"
							class="ck">Disponible Neto</td>
					</tr>
					<tr>
					</tr>
					<tr>
						<td colspan="6" align="right"><input type="button"
							value="Generar Reporte" value="generar" name="generar"
							id="generar"></td>
						<td colspan="6" align="right"><input type="button"
							value="Exportar Reporte" value="exportar" name="exportar"
							id="exportar"></td>
					</tr>
				</table>
			</fieldset>
		</form>
		<div id="dv">
			<table id="dt_comparacion" class="display" cellspacing="0"
				cellpadding="2" align="center">
				<thead>
					<tr>
						<th>EP</th>
						<th>Momento Presupuestal</th>
						<th>Diferencia</th>
						<th>Enero SAI/SICOP</th>
						<th>Febrero SAI/SICOP</th>
						<th>Marzo SAI/SICOP</th>
						<th>Abril SAI/SICOP</th>
						<th>Mayo SAI/SICOP</th>
						<th>Junio SAI/SICOP</th>
						<th>Julio SAI/SICOP</th>
						<th>Agosto SAI/SICOP</th>
						<th>Septiembre SAI/SICOP</th>
						<th>Octubre SAI/SICOP</th>
						<th>Noviembre SAI/SICOP</th>
						<th>Diciembre SAI/SICOP</th>
					</tr>
				</thead>
				<%
					if (resultado != null) {
				%>
				<tbody>

					<%
						for (Iterator<List<String>> i = resultado.iterator(); i
									.hasNext();) {
					%>
					<%
						List<String> renglon = i.next();
					%>
					<%
						out.println("\t\t\t\t\t\t<tr>");
					%>
					<%
						for (Iterator<String> j = renglon.iterator(); j.hasNext();) {
					%>
					<%
						out.println("\t\t\t\t\t\t\t<td>" + j.next() + "</td>");
					%>
					<%
						}
					%>
					<%
						out.println("\t\t\t\t\t\t</tr>");
					%>
					<%
						}
					%>
					<%
						session.removeAttribute("DIFERENCIAS");
					%>
				</tbody>
				<%
					}
				%>
			</table>
		</div>
	</div>

</body>
</html>