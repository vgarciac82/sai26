<%@page import="java.math.RoundingMode"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.syc.admin.TableroAlertaSubejercicio"%>
<%@page import="java.util.Map"%>
<%@page import="com.syc.admin.TableroAlertaSubejercicioBusinessLogic"%>
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
	if (session == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	Usuario usuario = (Usuario) session
	.getAttribute(GestionInterface.ATT_USER);

	if (usuario == null)
		response.sendRedirect("../index.jsp");
	
	List<Map<String,String>> resultado = (List<Map<String,String>>)session.getAttribute("RESULTADO");
	
	String entre = request.getParameter("entre") == null? "": request.getParameter("entre");
	String hasta = request.getParameter("hasta") == null? "": request.getParameter("hasta");
	
	TableroAlertaSubejercicioBusinessLogic tblbl = new TableroAlertaSubejercicioBusinessLogic();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Tablero de Alertas de Subejercicio</title>

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
<script type="text/javascript" src="../js/tableroAlertaSubejercicio.js">
	
</script>
<script type="text/javascript">
	var hasta = "<%=hasta%>";
	var entre = "<%=entre%>";
	$(document).ready(function() {
		init();
	});
</script>

</head>
<body id="dt_example">
	<form id="formAlertaSubejercicio" name="formAlertaSubejercicio"
		action="../admin/AlertaSubejercicio" method="get">
		<input type="hidden" name="unidad_normativa" id="unidad_normativa" />
		<input type="hidden" name="saldo_Servicios_Personales"
			id="saldo_Servicios_Personales" /> <input type="hidden"
			name="saldo_Gastos_Operacion" id="saldo_Gastos_Operacion" /> <input
			type="hidden" name="saldo_Subsidios_Corrientes"
			id="saldo_Subsidios_Corrientes" /> <input type="hidden"
			name="saldo_Otros_Corrientes" id="saldo_Otros_Corrientes" /> <input
			type="hidden" name="saldo_Muebles_Inmuebles"
			id="saldo_Muebles_Inmuebles" /> <input type="hidden"
			name="saldo_Obra_Publica" id="saldo_Obra_Publica" /> <input
			type="hidden" name="saldo_Inversion_Fisica"
			id="saldo_Inversion_Fisica" /> <input type="hidden"
			name="saldo_Subsidios_Inversion" id="saldo_Subsidios_Inversion" /> <input
			type="hidden" name="pctg_Servicios_Personales"
			id="pctg_Servicios_Personales" /> <input type="hidden"
			name="pctg_Gastos_Operacion" id="pctg_Gastos_Operacion" /> <input
			type="hidden" name="pctg_Subsidios_Corrientes"
			id="pctg_Subsidios_Corrientes" /> <input type="hidden"
			name="pctg_Otros_Corrientes" id="pctg_Otros_Corrientes" /> <input
			type="hidden" name="pctg_Muebles_Inmuebles"
			id="pctg_Muebles_Inmuebles" /> <input type="hidden"
			name="pctg_Obra_Publica" id="pctg_Obra_Publica" /> <input
			type="hidden" name="pctg_Inversion_Fisica" id="pctg_Inversion_Fisica" />
		<input type="hidden" name="pctg_Subsidios_Inversion"
			id="pctg_Subsidios_Inversion" /> <input type="hidden" name="accion"
			id="accion" value="ASIGNA_CONSULTA" /> <input type="hidden"
			id="cEjercicio" name="cEjercicio" />
		<div id="container" class="container">
			<h1>Alertas Subejercicio.</h1>
			<fieldset>
				<legend>Ayuda</legend>
				<table align="center">
					<tr>
						<td align="left">A continuaci&oacute;n se muestra Resumen de
							las Unidades Normativas con el porcentaje de Subejercicio.
							</>Descripci&oacute;n de iconos:<img
							src="../Generador/imagenes/incorrecto.png" alt="Subejercicio"
							width="16" height="16"></img>Subejercicio<img
							src="../Generador/imagenes/alerta.png" alt="alerta" width="16"
							height="16"></img>Alerta de Posible subejercicio<img
							src="../Generador/imagenes/correcto.png" alt="Correcto"
							width="16" height="16"></img>Correcto</td>
					</tr>
				</table>
			</fieldset>
			<fieldset>
				<legend>Rangos</legend>
				<table align="left">
					<tr>
						<td align="right">Limite <b>Disponible</b> Medio %:</td>
						<td align="left"><input name="entre" id="entre" size="3"
							maxlength="3"></input>
						</td>
						<td align="right"><b>Disponible</b> Mayor de %:</td>
						<td align="left"><input name="hasta" id="hasta" size="3"
							maxlength="3"></input>
						</td>
					</tr>
				</table>
				<table align="right">
					<tr>
						<td align="right"><input type="button" id="consulta"
							name="consulta" value="Asignar/Consulta" /></td>
					</tr>
				</table>
			</fieldset>
			<div id="dv">
				<table id="dt_subejercicio" class="display">
					<thead>
						<tr>
							<th>Unidad Normativa</th>
							<th>Modificado</th>
							<th>Disponible</th>
							<th>Porcentaje Disponible</th>
						</tr>
					</thead>
					<%
						if (resultado != null) {
					%>
					<tbody>

						<%
							for (Iterator<Map<String, String>> i = resultado.iterator(); i
										.hasNext();) {
						%>
						<%
							Map<String, String> renglon = i.next();
						%>
						<%
							out.println("\t\t\t\t\t\t<tr>");
						%>
						<%
							NumberFormat formato = NumberFormat
											.getNumberInstance(new Locale("es", "MX"));
									DecimalFormat formatoDecimal = (DecimalFormat) DecimalFormat
											.getInstance(new Locale("es", "MX"));//new DecimalFormat("##.00");
									formatoDecimal.setRoundingMode(RoundingMode.UNNECESSARY);
									formatoDecimal.applyPattern("0.00");

									out.println("\t\t\t\t\t\t\t<td align=\"center\">"
											+ renglon.get("unidad_normativa") + "</td>");
									out.println("\t\t\t\t\t\t\t<td align=\"center\">"
											+ "$"
											+ (formato.format(tblbl
													.sumaRenglonModificado(renglon))) + "</td>");
									out.println("\t\t\t\t\t\t\t<td align=\"center\">"
											+ "$"
											+ (formato.format(tblbl
													.sumaRenglonDisponible(renglon))) + "</td>");
									System.out.println("valor: "
											+ tblbl.porcentaje_subejercicio_Total(renglon)
											+ " convertido: "
											+ formatoDecimal.parse(tblbl
													.porcentaje_subejercicio_Total(renglon)));
									out.println("\t\t\t\t\t\t\t<td align=\"center\">"
											+ "<br>"
											+ (formatoDecimal.parse(tblbl
													.porcentaje_subejercicio_Total(renglon)))
											+ " % "
											+ ((Double.parseDouble(tblbl
													.porcentaje_subejercicio_Total(renglon))) == 0 ? "<img src=\"../Generador/imagenes/correcto.png\" alt=\"Correcto\" width=\"16\" height=\"16\"></img><br/>"
													: Double.parseDouble(tblbl
															.porcentaje_subejercicio_Total(renglon)) > 0
															&& Double.parseDouble(tblbl
																	.porcentaje_subejercicio_Total(renglon)) < Double
																	.parseDouble(entre) ? "<img src=\"../Generador/imagenes/correcto.png\" alt=\"Correcto\" width=\"16\" height=\"16\"></img><br/>"
															: Double.parseDouble(tblbl
																	.porcentaje_subejercicio_Total(renglon)) >= Double
																	.parseDouble(entre)
																	&& Double.parseDouble(tblbl
																			.porcentaje_subejercicio_Total(renglon)) <= Double
																			.parseDouble(hasta) ? "<img src=\"../Generador/imagenes/alerta.png\" alt=\"Posible Subejercicio\" width=\"16\" height=\"16\"></img><br/>"
																	: Double.parseDouble(tblbl
																			.porcentaje_subejercicio_Total(renglon)) > Double
																			.parseDouble(hasta) ? "<img src=\"../Generador/imagenes/incorrecto.png\" alt=\"Subejercicio\" width=\"16\" height=\"16\"></img><br/>"
																			: "") + "</td>");
						%>
						<%
							out.println("\t\t\t\t\t\t</tr>");
						%>
						<%
							}
						%>
						<%
							session.removeAttribute("RESULTADO");
						%>
					</tbody>
					<%
						}
					%>
				</table>
				<table align="center">
					<tr>
						<td align="right"><input type="button" id="correo"
							name="correo" value="Envia Correo" /></td>
						<td align="right"><input type="button" id="limpiar"
							name="limpiar" onclick="limpiarSesion();" value="Limpiar" /></td>
					</tr>
				</table>
			</div>
		</div>
	</form>
	<div id="div_detalle" title="Detalle">
		<table align="center">
			<tr>
				<td align="left">A continuaci&oacute;n se muestran detalle de
					unidades normativas seleccionada con los tipos de gasto Corriente e
					inversi&oacute;n</td>
			</tr>
			<tr>
				<td align="right"><font class="LabelSalida">Unidad</font>
				</td>
				<td><input type="text" align="left" id="unidad" name="unidad"
					value="" style="text-transform: uppercase" />
				</td>
			</tr>
		</table>
		<table align="center" border="1" width="100%" id="detalleTraza">
			<thead>
				<tr style="background-color: #DADADA">
					<th align="center"><font class="LabelSalida">Servicios
							Personales</font>
					</th>
					<th align="center"><font class="LabelSalida">Gastos
							Operaci&oacute;n</font>
					</th>
					<th align="center"><font class="LabelSalida">Subsidios
							Corrientes</font>
					</th>
					<th align="center"><font class="LabelSalida">Otros
							Corrientes</font>
					</th>
					<th align="center"><font class="LabelSalida">Muebles e
							Inmuebles</font>
					</th>
					<th align="center"><font class="LabelSalida">Obra
							P&uacute;blica</font>
					</th>
					<th align="center"><font class="LabelSalida">Inversi&oacute;n
							F&iacute;sica</font>
					</th>
					<th align="center"><font class="LabelSalida">Subsidios
							Inversi&oacute;n</font>
					</th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
	</div>

</body>
</html>