<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
	String reportType = new String (request.getParameter("reportType").getBytes("ISO-8859-1"),"UTF8");
	String reportTitle = new String (request.getParameter("reportTitle").getBytes("ISO-8859-1"),"UTF-8");
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type"
			content="text/html; charset=ISO-8859-1">
		<title>Reporte Contra Cuenta</title>
		<link href="../admin/js/jq9/css/smoothness/jquery-ui-1.9.0.custom.css"
			rel="stylesheet">
		<link href="../Generador/css/demo_page.css" rel="stylesheet">
		<link href="../Generador/css/demo_table_jui.css" rel="stylesheet">
		<script type="text/javascript" src="../admin/js/jq9/jquery-1.8.2.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript"
			src="../admin/js/jq9/jquery-ui-1.9.0.custom.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript"
			src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/ContraCuenta.js"></script>
		<script type="text/javascript"
			src="../js/ContabilidadCentroContable.js"></script>
		<script type="text/javascript">
			var dTable;
			var reportType='<%=reportType%>';
			var reportTitle='<%=reportTitle%>';
			
			$(document).ready(function() {
				init();
				$("#TIPO_CONCEPTO").change(function() {
					querySelectPost("CatTipoMovimendoRead", "cIdMOVIMIENTO", {async : false});
				});
				$("#nGrupo").change(function() {
//					querySelectPost("ConsEventoGrupoRead", "nGrupo", {async : false});
					querySelectPost("ConsEventoSubGrupoRead", "nSubGrupo", {async : false});
				});
				$("#nSubGrupo").change(function() {
					querySelectPost("ConsEventoEventoRead", "nEvento", {async : false});
				});
				$("#nEvento").change(function() {
					querySelectPost("ConsEventoPartidaRead", "nCOG", {async : false});
				});
//				$("#nCOG").change(function() {
//				});
			});
		</script>
	</head>
	<body id="dt_example">
		<form id="mainForm">
			<input type="hidden" name="cCentroContable" id="cCentroContable"
				value="">
			<h1>
				<label id="titulo">
				</label>
			</h1>
			<table align="center" cellpadding="5" cellspacing="5">
				<tr>
					<td align="left" colspan="3">
						<b> Ingrese los filtros de b&uacute;squeda que necesite para
							generar el reporte. </b>
					</td>
				</tr>
				<tr>
					<td align="left" colspan="3">
						&nbsp;
					</td>
				</tr>
				<tr>														
					<td align="right">
					<label id="lblGrupo">
						Grupo:
					</label>
					</td>
					<td colspan="2" align="left">
						<select id="nGrupo" name="nGrupo">
						</select>
					</td>
				</tr>
				<tr>														
					<td align="right">
					<label id="lblSubgrupo">
						Subgrupo:
					</label>
					</td>
					<td colspan="2" align="left">
						<select id="nSubGrupo" name="nSubGrupo">
						</select>
					</td>
				</tr>
				<tr>														
					<td align="right">
					<label id="lblEvento">
						Evento:
					</label>
					</td>
					<td colspan="2" align="left">
						<select id="nEvento" name="nEvento">
						</select>
					</td>
				</tr>
				<tr>														
					<td align="right">
					<label id="lblPartida">
						Partida:
					</label>
					</td>
					<td colspan="2" align="left">
						<select id="nCOG" name="nCOG">
						</select>
					</td>
				</tr>												
				<tr>
					<td align="right">
						Fecha Inicial:
					</td>
					<td align="left" colspan="2">
						<input id="FechaI" name="FechaI" type="text" readonly="readonly"
							maxlength="12" size="12" value="">
					</td>
				</tr>
				<tr>
					<td align="right">
						Fecha Final:
					</td>
					<td align="left" colspan="2">
						<input id="FechaF" name="FechaF" type="text" readonly="readonly"
							maxlength="12" size="12" value="">
					</td>

				</tr>
				<tr>
					<td align="right">
						<label id="lblCtaCtbl">
						Cuenta Contable:
						</label>
					</td>
					<td colspan="2" align="left">
						<select id="nCuenta" name="nCuenta">
						</select>
					</td>
				</tr>
					<tr>
						<td align="right">
						<label id="lblcIdRFC">
							RFC:
						</label>
						</td>
						<td colspan="2" align="left">
							<select id="cIdRFC" name="cIdRFC">
							</select>
						</td>
					</tr>
					<tr>
						<td align="right">
						<label id="lblCLC">
							Numero de CLC:
						</label>
						</td>
						<td colspan="2" align="left">
							<select id="CLC" name="CLC">
							</select>
						</td>
					</tr>
					<tr>
						<td align="right">
						<label id="lblCxP">
							Cuenta por Pagar:
						</label>
						</td>
						<td colspan="2" align="left">
							<select id="CxP" name="CxP">
							</select>
						</td>
					</tr>
					<tr>
						<td align="right">
						<label id="lblTIPO_CONCEPTO">
							Tipo de Concepto:
						</label>
						</td>
						<td colspan="2" align="left">
							<select id="TIPO_CONCEPTO" name="TIPO_CONCEPTO">
							</select>
						</td>
					</tr>
					<tr>
						<td align="right">
						<label id="lblcIdMOVIMIENTO">
							Tipo de Movimiento:
						</label>
						</td>
						<td colspan="2" align="left">
							<select id="cIdMOVIMIENTO" name="cIdMOVIMIENTO">
							</select>
						</td>
					</tr>
				<tr>
					<td colspan="3" align="center">
						<input type="button" value="Generar Reporte" id="generar">
						<input type="button" value="Limpiar" id="limpiar">
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>