<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas = false;
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		mntoCuentas = "10".equals(usuario.getPropiedad(
				"CCENTROCONTABLE").getValor());
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Reporte de Ejercido</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico"
			href="../imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";

@import "../css/demo_table_jui.css";

@import "../css/demo_page.css";
</style>

		<script type="text/javascript" src="../js/jquery-1.6.2.min.js">
</script>
		<script type="text/javascript" src="../js/jquery.dataTables.js">
</script>
		<script type="text/javascript" src="../js/jquery.form-2.94.js">
</script>
		<script type="text/javascript"
			src="../js/jquery-ui-1.8.16.custom.min.js">
</script>
		<script type="text/javascript" src="../js/jquery.ui.datepicker.js">
</script>
		<script type="text/javascript" src="../js/jquery.ui.core">
</script>
		<script type="text/javascript" src="../js/jquery.ui.widget.js">
</script>
		<script type="text/javascript" src="../js/jquery.ui.tabs.js">
</script>
		<script type="text/javascript" src="../js/crud.js">
</script>
		<script type="text/javascript" src="../../js/catalogo/general.js">
</script>

		<script type="text/javascript" charset="utf-8">
$(document).ready(
		function() {
			querySelectPost("mCatalogoCapituloRead", "cIdCapitulo", {
				async : false
			});
			querySelectPost("CAT_UNIDAD_EJECUTORA1Read", "cUnidadResponsable",
					{
						async : false
					});

			mostrar();
		});

function openCSV() {
	var queryWhere = "";

	if ($("#cIdDocumento").val() != "")
		queryWhere += " AND cIdDocumento LIKE '%25" + $("#cIdDocumento").val()
				+ "%25'";

	if ($("#cIdRFC").val() != "")
		queryWhere += " AND cRFCBeneficiario LIKE '%25" + $("#cIdRFC").val()
				+ "%25'";

	if ($("#cIdCapitulo").val() != "0")
		queryWhere += " AND nCapitulo ~ " + $("#cIdCapitulo").val();

	if ($("#cUnidadResponsable").val() != "00")
		queryWhere += " AND cUnidadEjecutora ~ '"
				+ $("#cUnidadResponsable").val() + "'";

	var querySeleccion="";	
	var servletPath;
	servletPath = "../../servlet/CatalogosCSV?"
				+ "rn=rptmReporteEjercido.csv"
				+ "&vFilters="+queryWhere;
	window.open(servletPath ,"popacuse","scrollbars=1, resizable=yes, width=1024, height=768");
}
</script>


	</head>

	<body id="dt_example">
		<form>
			<div id="container" class="container"
				style="width: 100%; height: 100%">
				<h1 align="left">
					Reporte de Ejercido
				</h1>
				<table align="left">

					<tr id="trCapitulo" align="left">
						<td>
							Capitulo
						</td>
						<td>
							<select id="cIdCapitulo" name="cIdCapitulo" style="width: 40em;"></select>
						</td>
					</tr>
					<tr id="trUnidadEjecutora" align="left">
						<td>
							Unidad Ejecutora
						</td>
						<td>
							<select id="cUnidadResponsable" name="cUnidadResponsable"
								style="width: 40em;"></select>
						</td>
					</tr>
					<tr id="trDocumento" align="left">
						<td>
							Documento
						</td>
						<td>
							<input type="text" name="cIdDocumento" id="cIdDocumento"
								style="width: 40em;" />
						</td>
					</tr>
					<tr id="trRfc" align="left">
						<td>
							R.F.C
						</td>
						<td>
							<input type="text" name="cIdRFC" id="cIdRFC" style="width: 40em;" size="20"/>
						</td>
					</tr>
					<tr id="trCSV" align="center">
						<td colspan="2">
							<img id="cmdcsv" name="cmdcsv" style="cursor: pointer"
								src="../../imagenes/icono_excel.jpg"
								onclick="javascript:openCSV();" />
							csv&nbsp;
						</td>
					</tr>
					<tr>
						<td>
							&nbsp;
						</td>
					</tr>
				</table>
				<br />
			</div>
		</form>
	</body>
</html>