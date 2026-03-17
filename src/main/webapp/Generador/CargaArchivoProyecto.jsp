
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
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Carga Archivo Proyecto</title>

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
		$("#fileUploadButton").button().click(function() {
			if ($("#fileUpload").val() == "")
				alert("Debe seleccionar un archivo a cargar.");
			else {
				$.blockUI({
					message : "Cargando Archivo. Por favor espere ......"
				});
				$("#uploadFileProyecto").submit();
			}
		});
		if (mensaje != "") {
			$("#resultadoTR").show();
			$("#mensajeTA").val(mensaje);
		} else
			$("#resultadoTR").hide();

		queryFormPost("readUltimaVersionProyecto", {
			async : false
		});
	});
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<form id="uploadFileProyecto" name="uploadFileProyecto"
		action="../Anteproyecto/CargaProyecto" method="post"
		enctype="multipart/form-data">
		<div id="container" class="container">
			<h1>Carga de archivo de Proyecto (PEF)</h1>
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
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<fieldset>
							<legend>Carga de archivo</legend>
							<table align="center">
								<tr>
									<td align="left" colspan="2">
										<p>
											Seleccione el archivo Excel que contiene el proyecto (PEF)
											para su carga/actualizaci&oacute;n y de clic en el boton
											"Enviar Archivo"<br> <b>Nota:</b> Solo se admiten
											archivos Excel formato 97-2003 (xls)
										</p></td>
								</tr>
								<tr>

									<td align="right">Archivo de Proyecto (PEF):</td>
									<td align="left"><input type="file" size="30"
										name="fileUpload" id="fileUpload"></td>
								</tr>
								<tr>
									<td align="center" colspan="2"><input type="button"
										value="Enviar Archivo" id="fileUploadButton"></td>
								</tr>
							</table>
						</fieldset>
					</td>
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
		</div>
	</form>
</body>
</html>