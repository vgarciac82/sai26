<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	String today = Util.getTodayESMX();

	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(
			GestionInterface.ATT_CONEXION);
	String ejercicioFiscal = adbl.obtenEjercicioFiscal();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Agregar Conciliaciones Autorizadas</title>
	<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
	<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
		
	<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/jsquery.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript">
	/**
	 * Carga informacion guardada. Se llama al terminar de cargar la pagina JSP
	 */
	function onLoadPlantilla(idOper) {
		alert("onLoadPlantilla");
	}

	function onPostDisplay(idOper) {
		alert("onPostDisplay");
		return true;
	}

	function onPostSubmit(idOper) {
		alert("onPostSubmit");

	}

	/**
	 * Valida que la informacion sea correcta. Si lo es, regresa true y continua el flujo. En caso contrario se regresa false y se detiene el flujo
	 */
	function onSubmit(idOper) {
		
		var p = window.parent;
		var resultado = false;

		p.gestion.setFolio($("#FOLIO").val());
		p.gestion.setOperador("<%=usuario.getNombre()%>");
		p.gestion.setFechaDocumento($("#fRecepcion").val()); //en la variable de caso dice FECHA_DOCUMENTO se quita el underscore y se capitalizan las primeras letras
		p.gestion.setEjercicioFiscal("<%=ejercicioFiscal%>");
		p.gestion.setMoneda("MXP");

		return true;
	}

	/**
	 * Calcula el responsable siguiente. Se llama si y solo si la respuesta de llamar al onSubmit es true. Debe regresar el grupo que sera el responsable siguiente.
	 * En caso de que sea el final del flujo, se debe llamar al responsable "CONSULTA_XXXXX" donde XXXXX es el tramite. PE CONSULTA_OBRAPUBLICA
	 */
	function ResponsableSiguiente(idOper) {
		if (idOper == 1)
			return "CONSULTA_CONCILIABANCOS";
	}

	/**
	 * Calcula la operacion siguiente. Se llama si y solo si la respuesta de llamar al onSubmit es true. Debe regresar el grupo que sera el responsable siguiente.
	 * En caso de que sea el final del flujo, se debe llamar al responsable "CONSULTA_XXXXX" donde XXXXX es el tramite. PE CONSULTA_OBRAPUBLICA
	 */
	function OperacionSiguiente(idOper) {
		if (idOper == 1)
			return "consulta_CONCILIABANCOS";
	}
</script>
</head>
<body>
	<form id="mainForm">
		<input type="hidden" id="FOLIO" name="FOLIO" value="<%=c.getFolio()%>" />
	</form>
</body>
</html>