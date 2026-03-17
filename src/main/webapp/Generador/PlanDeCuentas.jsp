
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
	String DATE_FORMAT = "dd/MM/yyyy";
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
<title>Libro Mayor</title>
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

<style>
	.notEditable {
		background-color: #CCCCCC;
		color: #000000;
	}
	.Editable
	{
		background-color: ##FFFFFF;
		color: #000000;
	}
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
	
	$("#cCentroContable").val( "<%=cCentroContable%>");	
	
	$("#generarPdf").button();	
	
	$(function() {
			$("#fecha_inicio").datepicker({
				showOn : "button",
				dateFormat : "dd/mm/yy",
				buttonImage : "images/calendar.gif",
				buttonImageOnly : true
			});
		});

	cssReadOnly();
	
});	
	
	function openARCH(ext){				
			if ($("#mes").val() == "0") {
				alert("Debes seleccionar un mes de consulta.");
			}
			else{		
				document.ExportarForm.submit();	
			}
	}		
	
	function cssReadOnly(){
		$( "[readOnly]" ).each(
			function(){	
				$(this).addClass("notEditable");	
		});
	}
	
	function firmass(){
		if ($("#chk_firmas").attr("checked")){
			document.getElementById("nombre4").readOnly = false;
			document.getElementById("cargo4").readOnly = false;
			$("#nombre4").removeClass("notEditable");
			$("#cargo4").removeClass("notEditable");			
			$("#nombre4").addClass("Editable");
			$("#cargo4").addClass("Editable");
		}else{
			$("#nombre4").attr("readonly",true);
			$("#cargo4").attr("readonly",true);
			$("#nombre4").removeClass("Editable");
			$("#cargo4").removeClass("Editable");
			$("#nombre4").addClass("notEditable");
			$("#cargo4").addClass("notEditable");
		}
	}

</script>

</head>
<body id="dt_example">
<br/>
<br/>
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReporteLibros" method="get" target="_blank">

		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /> 
		<input type="hidden" id="tipo_reporte" name="tipo_reporte" value="PlanDecuentas.jasper" /> 
		<input type="hidden" id="cCentroContable" name="cCentroContable"/> 		
		<h1> Plan De Cuentas   </h1>
		<div id="container" style="width: 70%" align="center">
			
			
			<fieldset>											
				<table align="center">											
					<tr>
						<td>
							&nbsp;
						</td>
						<td>
							<input type="button" value="Generar PDF" id="generarPdf" onclick="openARCH('pdf')"/>										
						</td>
					</tr>
				</table>				
											
			</fieldset>
		
		<fieldset>
				<legend>Firmas</legend>
				<table>
					<tr>
						<td>
							Nombre <input type="text" id="nombre1" name="nombre1" size=59 style="font-family: Arial; font-size: 9pt" value=""/> 
						</td>
						<td>
							Cargo <input type="text" id="cargo1" name="cargo1" size=59 style="font-family: Arial; font-size: 9pt" value=""/>
						</td>
						<td>
						</td>
					</tr>
					<tr>
						<td>
							Nombre <input type="text" id="nombre2" name="nombre2" size=59 style="font-family: Arial; font-size: 9pt" value=""/>
						</td>
						<td>
							Cargo <input type="text" id="cargo2" name="cargo2" size=59 style="font-family: Arial; font-size: 9pt" value=""/>
						</td>
						<td>
						</td>
					</tr>
					<tr>
						<td>
							Nombre <input type="text" id="nombre3" name="nombre3" size=59 style="font-family: Arial; font-size: 9pt" value=""/>
						</td>
						<td>
							Cargo <input type="text" id="cargo3" name="cargo3" size=59 style="font-family: Arial; font-size: 9pt" value=""/>
						</td>
						<td>
						</td>
					</tr>
					<tr>
						<td>
							Nombre <input type="text" id="nombre4" name="nombre4" size=59 readOnly style="font-family: Arial; font-size: 9pt" value=""/>
						</td>
						<td>
							Cargo <input type="text" id="cargo4" name="cargo4" size=59 readOnly style="font-family: Arial; font-size: 9pt" value=""/>
						</td>
						<td>
							<input type="checkbox" name="chk_firmas" id="chk_firmas" onclick="firmass()" value=4 >
						</td>
					</tr>
				</table>
			</fieldset>
		</div>	
		
	</form>

</body>
</html>


