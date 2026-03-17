<%@page import="java.util.Iterator"%>
<%@page import="com.syc.contable.anteproyecto.TableroControlBean"%>
<%@page import="java.util.Map"%>
<%@page
	import="com.syc.contable.anteproyecto.TableroControlPEFBusinessLogic"%>
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
	if (session == null){
		response.sendRedirect("../index.jsp");
		return;
	}
	Usuario usuario = (Usuario) session
	.getAttribute(GestionInterface.ATT_USER);

	if (usuario == null)
		response.sendRedirect("../index.jsp");
	
	TableroControlPEFBusinessLogic tcpefbl = new TableroControlPEFBusinessLogic();
	Map<String, Map<String, TableroControlBean>> tablero = tcpefbl.getRelacionCalendariosCargados(false);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Tablero de Control del Calendario de Proyecto</title>

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
<script type="text/javascript" src="../js/tableroControlCalendario.js">
	
</script>
<script type="text/javascript">
	$(document).ready(function() {
		init();
	});
</script>

</head>
<body id="dt_example">
	<form id="formDescargaCalendario" name="formDescargaCalendario"
		action="../Anteproyecto/DescargaProyecto" method="get" target="_blank">
		<input type="hidden" name="ue" id="ue" value="" /> <input
			type="hidden" name="un" id="un" value="" /> <input type="hidden" name="accion" id="accion"
			value="DESCARGA_COMPLETA" />
	</form>
	<form id="formTableroControl" name="formTableroControl">
		<input type="hidden" id="cEjercicio" name="cEjercicio" />
		<div id="container" class="container">
			<h1>Tablero de Control.</h1>
			<fieldset>
				<legend>Ayuda</legend>
				<table align="center">
					<tr>
						<td align="left">A continuaci&oacute;n se muestra la
							relaci&oacute;n de unidades ejecutoras y sus correspondientes
							unidades normativas. Se muestran los siguientes iconos: <br />
							&nbsp;&nbsp;&nbsp;&nbsp; <img
							src="../Generador/imagenes/correcto.png" alt="Captura completa"
							width="16" height="16"></img>&nbsp;&nbsp;Se ha completado de
							cargar el calendario comparando contra la carga de Proyecto (PEF)
							<br /> &nbsp;&nbsp;&nbsp;&nbsp;<img
							src="../Generador/imagenes/alerta.png" alt="Captura Parcial"
							width="16" height="16"></img>&nbsp;Se ha cargado de manera
							incompleta comparado contra la carga de Proyecto (PEF) <br />&nbsp;&nbsp;&nbsp;&nbsp;
							<img src="../Generador/imagenes/incorrecto.png"
							alt="Captura completa" width="16" height="16"></img>&nbsp;No se
							ha cargado informaci&oacute;n y se cuenta con informaci&oacute;n
							en la carga de Proyecto (PEF) <br />Se muestra tambi&eacute;n la
							relaci&oacute;n de Datos Cargados/Datos Calendarizados</td>
					</tr>
					<tr>
						<td align="right"><input type="button"
							id="descargaCalendarioCompleto" name="descargaCalendarioCompleto"
							value="Descargar Calendario" />
						</td>
					</tr>
				</table>
			</fieldset>
			<div id="dv">
				<table id="dt_calendario" class="display">
					<thead>
						<tr>
							<th>UE/UN</th>
							<th>B00</th>
							<th>B01</th>
							<th>B02</th>
							<th>B03</th>
							<th>B04</th>
							<th>B05</th>
							<th>B06</th>
							<th>B07</th>
							<th>B08</th>
							<th>B09</th>
							<th>B10</th>
							<th>B11</th>
							<th>B12</th>
							<th>B13</th>
							<th>B14</th>
							<th>B15</th>

						</tr>
					</thead>
					<tbody>

						<%
							for (Iterator<String> i = tablero.keySet().iterator(); i.hasNext();) {
								String key = i.next();
								Map<String, TableroControlBean> un = tablero.get(key);
								out.print("\n\t\t\t\t\t\t<tr>");
								out.print("\n\t\t\t\t\t\t\t<td>" + key + "</td>");
								for (Iterator<String> j = un.keySet().iterator(); j.hasNext();) {
									String unNombre = j.next();
									TableroControlBean tcb = un.get(unNombre);
									int totalCapturado = tcb.getTotalCapturado();
									int totalCalendario = tcb.getTotalCalendario();
									if (totalCapturado == totalCalendario)
										out.print("\n\t\t\t\t\t\t\t<td align=\"center\"><img src=\"../Generador/imagenes/correcto.png\" alt=\"Captura completa\" width=\"16\" height=\"16\"></img><br />"
												+ totalCapturado
												+ "/"
												+ totalCalendario
												+ "</td>");
									else if (totalCapturado < totalCalendario
											&& totalCalendario > 0 && totalCapturado > 0)
										out.print("\n\t\t\t\t\t\t\t<td align=\"center\"><img src=\"../Generador/imagenes/alerta.png\" alt=\"Captura Parcial\" width=\"16\" height=\"16\"></img><br />"
												+ totalCapturado
												+ "/"
												+ totalCalendario
												+ "</td>");
									else if (totalCalendario > 0 && totalCapturado == 0)
										out.print("\n\t\t\t\t\t\t\t<td align=\"center\"><img src=\"../Generador/imagenes/incorrecto.png\" alt=\"Captura completa\" width=\"16\" height=\"16\"></img><br />"
												+ totalCapturado
												+ "/"
												+ totalCalendario
												+ "</td>");
								}
								out.print("\n\t\t\t\t\t\t</tr>");
							}
						%>
					</tbody>
				</table>
			</div>
		</div>
	</form>
</body>
</html>