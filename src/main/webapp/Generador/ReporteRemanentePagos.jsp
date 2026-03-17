
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
<title>Reporte Saldo Pagos</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3" />
<meta http-equiv="description" content="This is my page" />
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
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" charset="utf-8">
						
$(document).ready(function() {	

	/*querySelectPost("readCentroContable", "cCentroContable", {
					async : false
				});
	*/
	$("#cCentroContable").val( "<%=cCentroContable%>");

		
	$("#cmdxlsReporteProg").button();
});
	
	function extrae(){
		if ($("#cCentroContable").val() != "" || 
		$("#ep").val() != "" || 
		$("#contrarecibo").val() != "" || 
		$("#siaff").val() != "" )
		{
			document.ExportarForm.submit();
		} else {
			alert("Llenar por lo menos un criterio de búsqueda, en caso de utilizar fechas seleccionar ambas");
		}
		
	}		

</script>

</head>
<body id="dt_example">
<br/>
<br/>
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReporteRemanentePagos" method="get" target="_blank">

		<!-- <input type="hidden" id="cCentroContable" name="cCentroContable"/> --> 		

		<div id="container" style="width: 90%" class="container SyCData">
			<h1>
				Consulta de Saldos de pagos
				presupuestales<!-- <label style="font-size: 11pt"></label> -->
			</h1>

				<table align="center">									
					<tr>
						<td align="right">
							Centro Contable:
						</td>						
						<td>
							<select name = "cCentroContable" id = "cCentroContable">
								<option value="00">Todas las Unidades</option>
								<option value="10">Oficinas Centrales</option>
								<option value="11">GERENCIA ESTATAL AGUASCALIENTES</option>
								<option value="12">GERENCIA ESTATAL BAJA CALIFORNIA</option>
								<option value="13">GERENCIA ESTATAL BAJA CALIFORNIA SUR</option>
								<option value="14">GERENCIA ESTATAL CAMPECHE</option>
								<option value="15">GERENCIA ESTATAL COAHUILA</option>
								<option value="16">GERENCIA ESTATAL COLIMA</option>
								<option value="17">GERENCIA ESTATAL CHIAPAS</option>
								<option value="18">GERENCIA ESTATAL CHIHUAHUA</option>
								<option value="19">GERENCIA ESTATAL DISTRITO FEDERAL</option>
								<option value="20">GERENCIA ESTATAL DURANGO</option>
								<option value="21">GERENCIA ESTATAL GUANAJUATO</option>
								<option value="22">GERENCIA ESTATAL GUERRERO</option>
								<option value="23">GERENCIA ESTATAL HIDALGO</option>
								<option value="24">GERENCIA ESTATAL JALISCO</option>
								<option value="25">GERENCIA ESTATAL MEXICO</option>
								<option value="26">GERENCIA ESTATAL MICHOACAN</option>
								<option value="27">GERENCIA ESTATAL MORELOS</option>
								<option value="28">GERENCIA ESTATAL NAYARIT</option>
								<option value="29">GERENCIA ESTATAL NUEVO LEON</option>
								<option value="30">GERENCIA ESTATAL OAXACA</option>
								<option value="31">GERENCIA ESTATAL PUEBLA</option>
								<option value="32">GERENCIA ESTATAL QUERETARO</option>
								<option value="33">GERENCIA ESTATAL QUINTANA ROO</option>
								<option value="34">GERENCIA ESTATAL SAN LUIS POTOSI</option>
								<option value="35">GERENCIA ESTATAL SINALOA</option>
								<option value="36">GERENCIA ESTATAL SONORA</option>
								<option value="37">GERENCIA ESTATAL TABASCO</option>
								<option value="38">GERENCIA ESTATAL TAMAULIPAS</option>
								<option value="39">GERENCIA ESTATAL TLAXCALA</option>
								<option value="40">GERENCIA ESTATAL VERACRUZ</option>
								<option value="41">GERENCIA ESTATAL YUCATAN</option>
								<option value="42">GERENCIA ESTATAL ZACATECAS</option>
							</select>								
						</td>						
					</tr>
					<tr>
						<td align="right">
							EP:
						</td>
						<td>
							<input type="text" name="ep" id="ep" maxlength="64" size="64" />
						</td>
					</tr>
					<tr>
						<td align="right">
							Contrarecibo:
						</td>
						<td>
							<input type="text" name="contrarecibo" id="contrarecibo" maxlength="15" size="18" />
						</td>
					</tr>
					<tr>
						<td align="right">
							Folio SIAFF:
						</td>						
						<td>
								<input type="text" name="siaff" id="siaff" maxlength="7" size="18" />							
						</td>						
					</tr>					
					<tr>
						<td align="right">
							</td>
					</tr>
				
					<tr>
						<td>
							&nbsp;
						</td>
						<td align="left">
							<input id="cmdxlsReporteProg" name="cmdxlsReporteProg" value="Extraer" type="button" onclick="extrae()" />
						</td>
					</tr>		
				</table>																			
		</div>
	</form>

</body>
</html>


