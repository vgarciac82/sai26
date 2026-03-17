<%@page import="com.syc.gestion.util.Util"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.obrapublica.EjercicioFiscal"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
 	String fatalError = "";
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	EjercicioFiscal aEjercicioFiscal = efbl.getEjercicioFiscalActivo();
	if (c == null || usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	String centroContable = (usuario.getPropiedad("CCENTROCONTABLE") != null
			? usuario.getPropiedad("CCENTROCONTABLE").getValor()
			: "");
			
	if (StringUtils.isBlank(centroContable))
		fatalError = "Error Fatal: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable. Consulte a su administrador.";

	String unidadEjecutora = usuario.getU_UR();
	if (StringUtils.isBlank(unidadEjecutora))
		fatalError = "Error Fatal: El Usuario no tiene Unidad Ejecutora asignada y no podra realizar aplicacion Contable. Consulte a su administrador.";

	String login = usuario.getLogin();
	String operador = usuario.getNombre();
	String fechaCaptura = Util.getTodayESMX();
	String fechaAplicacion = Util.calculaFechaAplicacion(aEjercicioFiscal);
	int nFolioPago = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
	String ramo = usuario.getU_Ramo();
	String ejercicioFiscal = aEjercicioFiscal.getaEjercicioFiscal();
	String folio = c.getFolio();
	String unidadResponsable = usuario.getU_UR( );
	
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Autorizacion de Pago Federalizado</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/resumenPago.css"></link>
<link rel="stylesheet" type="text/css" href="ComponentesPago/CSS/EgresoFirmantes.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Generador/js/validaciones.js"></script>
<script type="text/javascript" src="../Generador/js/funciones.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="js/ConsultaPago.js"></script>
<script type="text/javascript" src="ComponentesPago/js/ResumenPagoFed.js"></script>
<script type="text/javascript" src="ComponentesPago/js/EgresoFirmantes.js"></script>
<script type="text/javascript" src="../Generador/js/ActualizaFIEL.js"></script>
<script type="text/javascript">
	var errorFatalMSG = "<%=fatalError%>";
	var errorFatal = <%=!StringUtils.isBlank(fatalError)%>;
	var avanzaTab = false;
	var p = window.parent;
	
	var infoObj = {
		login : '<%=login%>',
		operador : '<%=operador%>',
		fechaCaptura : '<%=fechaCaptura%>',
		fechaAplicacion : '<%=fechaAplicacion%>',
		nFolioPagoFederalizado : '<%=nFolioPago%>',
		ramo : '<%=ramo%>',
		ejercicioFiscal : '<%=ejercicioFiscal%>',
		folio : '<%=folio%>',
		cTipoPago : "<%=c.getTipoCaso().getGavetaAsociada()%>",
		nFolioPago : "<%=nFolioPago%>",
		cUnidadPago : "<%=unidadResponsable%>",
		cDocumento : "<%=c.getTipoCaso().getGavetaAsociada()%>",
		cUnidadResponsable : "<%=unidadResponsable%>"
	};
	
</script>
</head>
<body id="dt_example" onkeydown="return(desactivaBackspace(event))">
	<form id="formPagos" method="POST" >
		<input type="hidden" name="operacion" id="operacion" value=""/>
		<input type="hidden" name="login" id="login" value=""/>
		<input type="hidden" name="operador" id="operador" value=""/>
		<input type="hidden" name="fechaCaptura" id="fechaCaptura" value=""/>
		<input type="hidden" name="fechaAplicacion" id="fechaAplicacion" value=""/>
		<input type="hidden" name="nFolioPagoFederalizado" id="nFolioPagoFederalizado" value=""/>
		<input type="hidden" name="ramo" id="ramo" value=""/>
		<input type="hidden" name="ejercicioFiscal" id="ejercicioFiscal" value=""/>
		<input type="hidden" name="folio" id="folio" value=""/>
		<input type="hidden" name="cTipoPago" id="cTipoPago" value="<%=c.getTipoCaso().getGavetaAsociada()%>"/>
		<input type="hidden" name="nFolioPago" id="nFolioPago" value="<%=nFolioPago%>"/>
		<input type="hidden" name="cUnidadPago" id="cUnidadPago" value=""/>
		<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value=""/>
		<input type="hidden" name="cDocumento" id="cDocumento" value=""/>
		<input type="hidden" name="cTipoFirmante" id="cTipoFirmante" value=""/>
		<input type="hidden" name="nIDEstatus" id="nIDEstatus" value=""/>
		
		<div id="resumenDiv" style="width: 100%">
			<jsp:include page="ComponentesPago/ResumenPagoFed.jsp"></jsp:include>
		</div>
		<div id="firmantesDiv" >
			<fieldset>
				<legend>Firmantes</legend>
				<jsp:include page="ComponentesPago/EgresoFirmantesBs.jsp"></jsp:include>
			</fieldset>
		</div>
		<div id="operacionesConsulta" style="display: none;">
			<fieldset>
				<legend>Operaciones</legend>
					<table align="center">
						<tr>
							<td>
								<input type="button" id="imprimirCxP" value="Imprimir Solicitud de Pago" class="btnInterfaceBG"/>
							</td>
							<td>
								<input type="button" id="actualizarFrmnts" value="Actualizar Firmantes" style="display:none" class="btnInterfaceBG"/>
							</td>
						</tr>
					</table>
			</fieldset>
		</div>
	</form>
</body>
</html>