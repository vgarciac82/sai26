<%@page language="java" pageEncoding="UTF-8"  import="java.util.*"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CUENTAS_BANCARIAS_BENEFICIARIOS")){
		mntoCuentas = "SI".equals(usuario.getPropiedad("CUENTAS_BANCARIAS_BENEFICIARIOS").getValor());
	}

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Reporte General de Facturas</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Reporte General de Préstamos">
		
		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
			@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../Generador/css/demo_table_jui.css";
			@import "../Generador/css/demo_page.css";
		</style>
		
		<style>			// estilos del dialogo
			div#dialog-form fieldset { padding:0; border:0; margin-top:25px; }
			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
		</style>

		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.core"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
						
		<script type="text/javascript">
		
			$(document).ready(function() {
			
				$("#fechaInicial").datepicker({ showOn : "button",dateFormat:"dd/mm/yy",showAnim:"slideDown",buttonImage : "../Generador/images/calendar.gif", buttonImageOnly : true	});
				$("#fechaFinal").datepicker({ showOn : "button",dateFormat:"dd/mm/yy",showAnim:"slideDown",buttonImage : "../Generador/images/calendar.gif", buttonImageOnly : true	});
			});
			
				function GeneraExcel() {
		
					var fechaIni = $.trim($("#fechaInicial").val());
					var fechaFin = $.trim($("#fechaFinal").val());
									
					if ((fechaIni != "") && (fechaFin !="")){
					} else {
						alert("Ambas fechas deben ser seleccionadas");
						return;
						};
				
					var url = "../admin/SeguridadCatalogos?xls=SI&catalogo=REPORTE&accion=run&rn=ReporteGeneralPrestamos.jasper&WhereList=FechaFactura BETWEEN CONVERT(DATE,'" + fechaIni + "') AND CONVERT(DATE,'" + fechaFin + "')";
					var ventimp = window.open(url, "popacuse","scrollbars=1, resizable=yes, width=1024, height=768");
				};

		</script>

</head>
<body>
	<h4>Reporte General de Facturas</h4>
	<br>
	<br>
	<br>
	<form action="" method="post" name="frm" id="frm">
		<br>
		<div id="TablaFechas">
			<table align="center" border="0" cellpadding="" >
			<tr>
				<td>
					<input type="hidden" id="lbl_fecha" name="lbl_fecha" value="FECHA"/>Fechas</td>
				<td align="right">Desde:</td>
				<td width="25%">
				&nbsp;&nbsp;
					<input type="text" name="fechaInicial" id="fechaInicial" style="background:#f0f0f0; text-align:center" readonly  size="10" title="Fecha de Factura Inicial"/>
				</td>
				<td align="right">Hasta:</td>
				<td>
				&nbsp;&nbsp;
					<input type="text" name="fechaFinal"   id="fechaFinal" style="background:#f0f0f0; text-align:center" readonly   size="10" title="Fecha de Factura Final"/>
				</td>
			</tr>
			</table>
		</div>
		<br>
		<br>
		<table align="center" border="0" cellpadding="" >
			<tr>
				<td align="center" valign="bottom">
					<input type="button" id="cmdExcel" name="cmdExcel" value="Genera Excel" onclick="GeneraExcel();" />					
				</td>
			</tr>
		</table>
	</form>

</body>
</html>