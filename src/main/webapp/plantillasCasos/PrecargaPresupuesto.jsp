<%@page session="false"
	import="com.syc.contable.anteproyecto.CargaProyectoBusinessLogic"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	response.setHeader("Cache-Control", "no-cache");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", -1);
	HttpSession session = request.getSession(false);
	if (session == null)
		response.sendRedirect("../index.jsp");

	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);

	if (usuario == null)
		response.sendRedirect("../index.jsp");

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	if (c == null)
		response.sendRedirect("../index.jsp");

	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar calendarToday = Calendar.getInstance();

	String ueUsuario = usuario.getU_UR();
	String uNombre = usuario.getNombre();
	String folio = c.getFolio();
	String fechaActualizacion = sdf.format(calendarToday.getTime());

	CargaProyectoBusinessLogic cpbl = new CargaProyectoBusinessLogic(
			usuario.getLogin());

	String partidasRestirngidas = cpbl
			.generaCadenaPartidasRestringidas();
	boolean esAdmin = (usuario.getRole("ADMIN_PRESUPUESTO") != null);

	/*Si ya se cargo un calendario, no permite se recargue*/
	boolean calendarioCargado = cpbl.calendarioUECargado(usuario);

	/*Mensaje recibido por del servidor al terminar la carga.*/
	String mensajeRetorno = (String) session
			.getAttribute("MENSAJE_CARGA");

	if (mensajeRetorno != null) {
		session.removeAttribute("MENSAJE_CARGA");
	} else
		mensajeRetorno = "";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Captura de Calendario de Proyecto</title>

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
<script type="text/javascript" src="../js/PrecargaPresupuesto.js">
	
</script>
<script type="text/javascript">
	var UEUsuario = "<%=ueUsuario%>";
	var folio = "<%=folio%>";
	var operador = "<%=uNombre%>";
	var fecha = "<%=fechaActualizacion%>";
	var mensaje = "<%=mensajeRetorno%>";
	var esAdmin = <%=esAdmin%>;
	var partidasRestringidas = "<%=partidasRestirngidas%>";
	var calendarioCargado =
<%=calendarioCargado%>
	;

	$(document).ready(function() {
		init();
	});
</script>

</head>
<body id="dt_example">
	<form id="formProyecto" name="formProyecto" method="get"
		action="../Anteproyecto/DescargaProyecto" target="_blank">
		<input type="hidden" id="DESCARGA_CALENDARIO"
			name="DESCARGA_CALENDARIO" value="true" />
	</form>
	<form id="FormCargaCalendario" name="FormCargaCalendario"
		enctype="multipart/form-data" method="post"
		action="../Anteproyecto/CargaCalendario">
		<input type="hidden" id="cEjercicio" name="cEjercicio" />
		<div id="container" class="container">
			<h1>Precarga de Presupuesto.</h1>
			<fieldset>
				<legend>Descarga de Archivo de Presupuesto</legend>
				<table align="center">
					<tr>
						<td align="left">Si no ha descargado el Proyecto de
							Presupuesto, a&uacute;n de clic en el bot&oacute;n para iniciar
							la descarga.</td>
					</tr>
					<tr>
						<td align="center"><input type="button" id="descargaBtn"
							value="Descargar Presupuesto" />
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset>
				<legend>Carga de archivo</legend>
				<table align="center">
					<tr>
						<td colspan="3" align="left"><br />Si ha terminado de
							calendarizar el proyecto, de clic en el boton <i>Examinar</i>,
							seleccione el archivo deseado y de clic en el bot&oacute;n <i>Cargar
								Archivo</i><br /> <br /> <b>Nota Importante:</b>Recuerde que solo
							se aceptan cifras cerradas, es decir, <b>sin centavos</b>. Si se
							encuentran cantidades como 100.50 sera rechazado.</td>
					</tr>
					<tr>
						<td align="right">Archivo de Presupuesto Calendarizado:</td>
						<td align="left" colspan="2"><input type="file"
							id="fPresupuesto" name="fPresupuesto" size="17" /> <input
							type="button" value="Cargar Archivo" id="cargaBtn"
							name="cargaBtn" /></td>
					</tr>
				</table>
			</fieldset>
			<div id="dv">
				<table id="dt_calendario" class="display">
					<thead>
						<tr>
							<th>E.P.</th>
							<th>Anual</th>
							<th>Enero</th>
							<th>Febreo</th>
							<th>Marzo</th>
							<th>Abril</th>
							<th>Mayo</th>
							<th>Junio</th>
							<th>Julio</th>
							<th>Agosto</th>
							<th>Septiembre</th>
							<th>Octubre</th>
							<th>Noviembre</th>
							<th>Diciembre</th>
						</tr>
					</thead>
					<tbody></tbody>
				</table>
			</div>
		</div>
		<div id="dialog-mensaje" title="Mensaje de Sistema">
			<table id="msgSist" align="center">
				<tr>
					<td align="center"><textarea rows="20" cols="90"
							id="msgTextArea"></textarea>
					</td>
				</tr>
			</table>
		</div>
	</form>
</body>
</html>