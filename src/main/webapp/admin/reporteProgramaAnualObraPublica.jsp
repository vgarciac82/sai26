<%-- <%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%> --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Empleado"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.core.EmpleadoArea"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.syc.gestion.reportes.ReporteBussinesLogic"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>


<%
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	String UR = usuario.getU_UR();
	Integer UR_Int = new Integer(UR.substring(1));
	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(
			"jdbc/gestion");
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);
	String Meses[] = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO",
			"JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE",
			"NOVIEMBRE", "DICIEMBRE"};
%>
<%!private Logger log = Logger.getLogger(getClass());
	String headerParameterHtml = "";
	private String jniName = null;

	public void jspInit() {
		try {
			InitialContext ic = new InitialContext();
			jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");

			if (jniName == null) {
				jniName = "jdbc/gestion";
				log.info("Environment Entry \"dataSourceRefName\" nula usando default \""
						+ jniName + "\"");
			} else
				log.info("dataSourceRefName=" + jniName);
		} catch (NamingException exc) {
			jniName = "jdbc/gestion";
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \""
					+ jniName + "\"");
		}
	}

	public String getValor(String data) {
		return (data == null ? "" : data);
	}%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- <meta http-equiv="Content-Type"	content="text/html; charset=ISO-8859-1"> -->
<title>Programa Anual de Obra P&uacute;blica (PAOP)</title>

	<style type="text/css" title="currentStyle">
		@import "../Generador/css/demo_page.css";
		@import "../Generador/css/demo_table_jui.css";
		@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
	</style>

<!-- 		<script type="text/javascript" src="../js/datepickercontrol.js"></script> -->
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.core"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/ContabilidadCentroContable.js"></script>

<!-- 		<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css"> -->
		<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css">
		<link rel="stylesheet" type="text/css" href="../css/interfaz.css">


<script type="text/javascript">
function limpiarSesion()
	{
		parent.frames['resultado'].location.href = "blank.htm";
		window.location.href="reporteProgramaAnualObraPublica.jsp?id=<%=request.getParameter("id")%>"; //Esto recarga la pagina
	}

function fnPrintTituloReporte(TipoSalida) {
	var Meses = new Array(
		"Enero",
		"Febrero",
		"Marzo",
		"Abril",
		"Mayo",
		"Junio",
		"Julio",
		"Agosto",
		"Septiembre",
		"Octubre",
		"Noviembre",
		"Diciembre"
	);
	var principal = parent.parent.document;
	var asunto = document.datawork; 
	var cCentroContable = (document.getElementById("cCentroContable").value == 'undefined' ? '' : document.getElementById("cCentroContable").value);
	var hcCentroContable = (document.getElementById("hcCentroContable").value == ""|| document.getElementById("hcCentroContable").value == "00"? "00 CONSOLIDADO" :  document.getElementById("hcCentroContable").value );
    var mesIni = document.getElementById("InfoRegMesIni").value;
	var aEjercicioFiscal = document.getElementById("aEjercicioFiscal").value;
    var qry = "EXECUTE sp_tBalanzaAnalitico_syc @Salida OUTPUT, '"+aEjercicioFiscal+"', '"+mesIni+"'; ";
    //var qry = "EXECUTE sp_tBalanzaAnalitico_syc @Salida OUTPUT| '"+buscaCuentaIni+"'| '"+buscaCuentaFin+"'| '"+aEjercicioFiscal+"'| '"+DepuraLineas+"'| '"+DepuraColumnas+"'| '"+cCentroContable+"'| '"+TipoReporte+"'| '"+mesIni+"'| '"+mesFin+"'| '"+FiltroSubcuenta+"'; ";

	var param = "";
	 		param = "catalogo=REPORTE"
	 		+ "&accion=run"
	 		+ "&cCentroContable=" 	+ cCentroContable
			+ "&aEjercicioFiscal=" 	+ aEjercicioFiscal
			+ "&mesIni=" 			+ mesIni
			+ "&qry=" 				+ qry
			+ "&rn=" 				+ "reporteProgramaAnualObraPublica.jasper" ;

			//alert(param);

		$("#GenerarReporte").attr("disabled", true);
		$("#Limpiar").attr("disabled", true);
		if (TipoSalida == "XLS"){
			window.open('reporteProgramaAnualObraPublicaResultado.jsp?' + param, 'reporteProgramaAnualObraPublicaResultado', 'status=1, width=900px, height=500px,scrollbars=yes,resizable=yes');
		}
		$("#GenerarReporte").attr("disabled", false);
		$("#Limpiar").attr("disabled", false);
		limpiarSesion();
		return true;
}


</script>
<script language="javascript" type="text/javascript">

	var op_meses = '';
	var Meses = new Array(
		"Enero",
		"Febrero",
		"Marzo",
		"Abril",
		"Mayo",
		"Junio",
		"Julio",
		"Agosto",
		"Septiembre",
		"Octubre",
		"Noviembre",
		"Diciembre"
	);

$(document).ready(
	function()
	{
		$("input.AyudaSyC").subIniciaDlg();
		$("input.autoCompletaSyC").subIniciaAutoCompleta();
		$(this).ajaxForm({
			dataType: "json",
			success: formSubmited
		});

		$("#divcCentroContable").hide();

		getCentroContable();
		$("#cCentroContable").val( newCC);		
		querySelectPost("CatalogoCentroContReadBalanza", "hcCentroContable", {async: false });
		
		$("#GenerarReporte").attr("disabled", true);
		$("#Limpiar").attr("disabled", true);
		// Inicializaciones CRUD
		querySelectPost("catalogoEjercicioFiscalRead", "aEjercicioFiscal", {async: false });
		
		querySelectPost("MesContableAbierto2Read", "InfoRegMesFin", {async: false });
		
		$("#GenerarReporte").attr("disabled", false);
		$("#Limpiar").attr("disabled", false);
	});


function formSubmited() {
alert("Beneficiario enviado!");
}

	function MesFin() {
		for (i = 0; i < calendar.get(Calendar.MONTH) + 1; i++) {
			op_meses += '" <option value="' + (i + 1) + '"';
			if (calendar.get(Calendar.MONTH) == i) {
				op_meses += ' selected ';
			}
			op_meses += '>' + Meses[i] + '</option>';
		}
	}
</script>
</head>
<body scroll="yes"
	style="width: 97%; background-image: url(../imagenes/steel_BG.gif);">
	<form style="height: 430px;" id="form1" name="form1" method="post"
		target="formulario"
		action="./BalanzadeComprobacion.jsp?select=u_login">
		<input type="hidden" id="mesAbierto" name="mesAbierto" value="0" />
		<input type="hidden" id="nMes" name="nMes" />
		<div class="Contenido" style="height: 33px;">
			<table class="TituloRutaCA" height="10">
				<tbody>
					<tr>
						<td><img src="../imagenes/iconos/reportes.png" alt=""
							height="16" width="16"><font color="#ffffff"><strong>
									Programa Anual de Obra P&uacute;blica (PAOP)</strong> </font></td>
					</tr>
				</tbody>
			</table>
		</div>
		<table style="width: 1053px; height: 58px;" border="0" align="center">
			<tbody>
				<tr>
					<td style="text-align: right; width: 300px;" align="right"><input
						id="UnidadEjecutora" name="UnidadEjecutora" value="" type="hidden">
						<input type="hidden" id="hidEXCEL" name="hidEXCEL" /></td>
					<td style="height: 10px; text-align: left; width: 737px;"
						align="left">
						<%
							//Calendar calendar = Calendar.getInstance();
							//java.util.Calendar fecha = java.util.Calendar.getInstance();
							
							
						%>
				</tr>
				<tr>
					<td style="text-align: right; width: 300px;" align="right">
						Ejercicio Fiscal:</td>
					<td style="height: 10px; text-align: left; width: 737px;"
						align="left"><select id="aEjercicioFiscal"
						name="aEjercicioFiscal">
							<option value="Z:">A</option>
							<option value="Y:">B</option>
							<option value="X:">C</option>
							<option value="xx" selected="selected">--</option>
					</select>
				</tr>
				<tr>
					<td style="text-align: right; width: 300px;" align="right">
						Mes</td>
					<td style="height: 10px; text-align: left; width: 737px;"
						align="left"><select name="InfoRegMesIni" id="InfoRegMesIni"
						onchange="" value="">
							<%
								for (int i = 0; i <  12; i++) {
							%>
							<option value="<%=i + 1%>" <%if (0 == i) {%> selected <%}%>><%=Meses[i]%></option>
							<%
								}
							%>
					</select> 
					</td>
				</tr>
			</tbody>
		</table>
		<div id="divcCentroContable" style="display: none;">
			<table style="width: 1053px; height: 58px;" border="0" align="center">
				<tbody>
					<tr>
						<td style="text-align: right; width: 300px;" align="right">
							Centro Contable:</td>
						<td style="height: 10px; text-align: left; width: 737px;"
							align="left">
							<!--<input id="hcCentroContable" name="hcCentroContable" value="" size="50" maxlength="50" class="AyudaSyC autoCompletaSyC" type="text">-->
							<td align="left"><select id="hcCentroContable" name="hcCentroContable">
							<option value="Z:">A</option>
							<option value="Y:">B</option>
							<option value="X:">C</option>
							<option value="xx" selected>--</option></select></td>
							<input id="cCentroContable" name="cCentroContable" value="" size="5"
							maxlength="5" class="" type="hidden">
							<%if (UR_Int >= 1 && UR_Int <= 14) {%>
								<input id="cUnidadResponsable" name="cUnidadResponsable" value="" size="5" maxlength="5" class="" type="hidden">
							<% 	} else { %>
								<input id="cUnidadResponsable" name="cUnidadResponsable" value="<%=UR%>" size="5" maxlength="5" class="" type="hidden">
							<%	} %>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<br>
		<table align="center">
			<tbody>
				<tr>
					<td><input id="GenerarReporte"
						name="GenerarReporte" value="Generar"
						onclick="return fnPrintTituloReporte('XLS');" type="button">
						<input id="Limpiar" name="Limpiar" value="Limpiar"
						onclick="limpiarSesion();" type="button">
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>
