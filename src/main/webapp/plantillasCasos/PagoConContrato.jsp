<%@page import="com.syc.gestion.util.Util"%>
<%@page import="com.syc.obrapublica.EjercicioFiscal"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String fatalError = "";
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	EjercicioFiscal aEjercicioFiscal = efbl.getEjercicioFiscalActivo();
	
	if( c == null || usuario == null ){
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String centroContable = ( usuario.getPropiedad("CCENTROCONTABLE") != null ? usuario.getPropiedad("CCENTROCONTABLE").getValor(): "" );
	if( StringUtils.isBlank( centroContable ) )
		fatalError = "Error Fatal: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable. Consulte a su administrador.";
	
	String unidadEjecutora = usuario.getU_UR();
	if( StringUtils.isBlank( unidadEjecutora ) )
		fatalError = "Error Fatal: El Usuario no tiene Unidad Ejecutora asignada y no podra realizar aplicacion Contable. Consulte a su administrador.";
			
	String login = usuario.getLogin();
	String operador = usuario.getNombre();
	
	int nFolioPago = Integer.parseInt( c.getFolio().substring( c.getFolio().lastIndexOf('-') + 1) );
	String ramo = usuario.getU_Ramo();
	String ejercicioFiscal = aEjercicioFiscal.getaEjercicioFiscal();
	String tipoPago = c.getTipoCaso().getGavetaAsociada();
	String tipoDocumento = tipoPago.toUpperCase().replace("PAGO", "").trim(); 
	String tipoContrato = tipoPago.substring(0,2).toUpperCase();
	String descTipoPago = c.getTipoCaso().getDescripcion();

	String conceptoPago = "ComponentesPago/Concepto" + tipoPago + ".jsp";	
	String montosPago = "ComponentesPago/Montos" + tipoPago + ".jsp";
	String retencionesPago = "ComponentesPago/Retenciones" + tipoPago + ".jsp";
	String movimientosPago = "ComponentesPago/Movimientos" + tipoPago + ".jsp";
	String firmantesPago = "ComponentesPago/Firmantes.jsp";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=descTipoPago%></title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<style type="text/css">

.notEditable {
	background-color: #CCCCCC;
	color: #000000;
}

.money{
	text-align: right;
}

.numeric{
	text-align: right;
}

.decimal{
	text-align: right;
}
</style>



<script type="text/javascript" src="../js/cycle.js"></script>
<script type="text/javascript" src="../js/json2.js"></script>
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
<script type="text/javascript" src="../Generador/js/validaciones.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="ComponentesPago/js/<%=tipoPago%>.js"></script>
<script type="text/javascript" src="ComponentesPago/js/Concepto<%=tipoPago%>.js"></script>
<script type="text/javascript" src="ComponentesPago/js/Montos<%=tipoPago%>.js"></script>
<script type="text/javascript" src="ComponentesPago/js/CapturaRetenciones<%=tipoPago%>.js"></script>
<script type="text/javascript" src="ComponentesPago/js/CapturaMovimientos<%=tipoPago%>.js"></script>
<script type="text/javascript" src="ComponentesPago/js/Resumen<%=tipoPago%>.js"></script>

<script type="text/javascript">
	var errorFatalMSG = "<%=fatalError%>";
	var errorFatal = <%=!StringUtils.isBlank(fatalError)%>;
	$(document).ready(function() {
		initUI();
	});
</script>

</head>

<body id="dt_example">
	<form id="formPagos" name="formPagos">
		<input type="hidden" name="cTipoPago" id="cTipoPago"  value="<%=tipoPago%>">
		<input type="hidden" name="nFolioPago" id="nFolioPago"  value="<%=nFolioPago%>">
		<input type="hidden" name="login" id="login"  value="<%=login%>">
		<input type="hidden" name="unidadEjecutora" id="unidadEjecutora"  value="<%=unidadEjecutora%>">
		<input type="hidden" name="operador" id="operador"  value="<%=operador%>">
		<input type="hidden" name="tipoDocumento" id="tipoDocumento" value="<%=tipoDocumento%>">
		<input type="hidden" name="cDocumento" id="cDocumento"   value="<%=c.getTipoCaso().getGavetaAsociada()%>">
		<input type="hidden" name="ramo" id="ramo" value="<%=ramo%>">
		<input type="hidden" name="ejercicioFiscal" id="ejercicioFiscal" value="<%=ejercicioFiscal%>">
		<input type="hidden" name="centroContable" id="centroContable" value="<%=centroContable%>">
		<input type="hidden" name="IDDocumento" id="IDDocumento" value="<%=nFolioPago%>">
		<input type="hidden" name="accion" id="accion">
		<input type="hidden" name="esAnticipo" id="esAnticipo" value="N">    
		
		<div id="container" class="container" style="width: 99%">
			<h1><%=descTipoPago%></h1>
			<jsp:include page="<%=conceptoPago%>"></jsp:include>
			<jsp:include page="<%=montosPago%>"></jsp:include>
			<jsp:include page="<%=retencionesPago%>"></jsp:include>
			<jsp:include page="<%=movimientosPago%>"></jsp:include>
			<jsp:include page="<%=firmantesPago%>"></jsp:include>
			<div id="dlgMensajes">
			</div>
		</div>
	</form>
</body>
</html>