
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>

<%
	boolean bAplicadoCont = false;
	String DATE_FORMAT = "yyyy/MM/dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String algo = "";

	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);

	String mensaje = "";
	if (request.getParameter("msg") != null
			&& !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper"))
				.intValue();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(
			GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	//documentos del caso
	CasoBusinessLogic cbl = new CasoBusinessLogic(
			GestionInterface.ATT_CONEXION);

	//Valida Centro de Costos
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}

	cUR = usuario.getU_UR();	
	cRamo = usuario.getU_Ramo();
	//cRamo="16";
	algo = usuario.getLogin();
	//de aki para arriba es de cajon
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Reporte Momento Contable del Ingreso</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3" />
<meta http-equiv="description" content="This is my page" />
<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" /> 
<style type="text/css" title="currentStyle" />
@import "css/demo_page.css"; @import "css/demo_table_jui.css"; @import
"themes/smoothness/jquery-ui-1.8.4.custom.css";
</style>
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" charset="utf-8">
						
$(document).ready(function() {	

	/*querySelectPost("readCentroContable", "cCentroContable", {
					async : false
				});
	*/
	$("#cCentroContable").val( "<%=cCentroContable%>");

		$(function() {
			$("#fecha_inicio").datepicker({
				showOn : "button",
				dateFormat : "yy/mm/dd",
				buttonImage : "images/calendar.gif",
				buttonImageOnly : true
			});
		});
		
		$(function() {
			$("#fecha_fin").datepicker({
				showOn : "button",
				dateFormat : "yy/mm/dd",
				buttonImage : "images/calendar.gif",
				buttonImageOnly : true
			});
		});
	
	$("#cmdxlsReporteProg").button();
});
	
	function extrae(){
		if ($("#fecha_inicio").val() == "" || $("#fecha_fin").val() == "") {
			alert("Los rangos de fechas son obligatorios.");
		}
		else{		
			document.ExportarForm.submit();
		}	
	}		

	function habilita(){
		if ( $("#TIPO_REPORTE").prop("checked")) {
		 document.getElementById("Simp").disabled = false;
		 document.getElementById("Det").disabled = false;
		 //document.getElementById("TIPO_REPORTE").value  = "1";
		 }
		 else{
		 document.getElementById("Simp").disabled = true;
		 document.getElementById("Det").disabled = true;
		 //document.getElementById("TIPO_REPORTE").value  = "0";
		 }
		 
	}	
</script>

</head>
<body id="dt_example">
<br/>
<br/>
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReportePolizas" method="get" target="_blank">

		<!-- <input type="hidden" id="cCentroContable" name="cCentroContable"/> --> 	
		<input type="hidden" id="TIPO_REPORTE" name="TIPO_REPORTE" value="CONCILIARADINGRESO"/>	

		<div id="container" style="width: 90%" class="container SyCData">
			<h1>
				Conciliacion Contable - Presupuestal del Ingreso vs SIAFF</h1>
			
			<div id="por_solicitud">
				<table align="center">													
					<tr>						
						<td align="right">
							Selecciona la fecha del reporte:
						</td>
						<td>
							<input type="text" name="fecha_fin" id="fecha_fin" maxlength="12" size="12" />
						</td>					
					</tr>
					<tr>
						<td>
							&nbsp;
						</td>
						<td align="center">
							<input id="cmdxlsReporteProg" name="cmdxlsReporteProg" value="Extraer" type="button" onclick="extrae()" />
						</td>
					</tr>	
				</table>															
			</div>				
		</div>
	</form>

</body>
</html>


