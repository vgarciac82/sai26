<%@page import="com.syc.sai.contabilidad.polizamanual.controller.DocPolizaEncabezadoBusinessLogic"%>
<%@page import="com.syc.sai.contabilidad.polizamanual.model.DocPolizaEncabezadoManager"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String action = request.getParameter("a");
	String cTipoPago = request.getParameter("d");
	int nFolioPago = Integer.parseInt( StringUtils.isEmpty(request.getParameter("f")) ? "0" : request.getParameter("f"));
	
	String cLogin = usuario.getLogin();
	String cUR = usuario.getU_UR();
	String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	String RFCUsuario = usuario.getuRFC();
	String result = StringUtils.trimToEmpty((String) session.getAttribute("RESULT"));
	String tipoAutorizacion = ( "VoBoPago".equals(action) ? "VOBO" : ( "AutPago".equals(action) ? "AUT": "") );
	String numeroEmpleado = "";
	numeroEmpleado = usuario.getNumeroEmpleado();
	String msg = StringUtils.trimToEmpty(request.getParameter("msgError"));
	
	boolean mostrarResultado = !StringUtils.isBlank(result);
	String fielMsg = "";
	boolean fielExpiringSoon = session.getAttribute("EXPIRING_SOON") != null && ((Boolean)session.getAttribute("EXPIRING_SOON")); 
	if(fielExpiringSoon){
		fielMsg = (String) session.getAttribute("EXPIRING_MSG");
		session.removeAttribute("EXPIRING_SOON");
		session.removeAttribute("EXPIRING_MSG");
	}
	
	int mesAbierto=DocPolizaEncabezadoManager.readnMesAbierto(new DocPolizaEncabezadoBusinessLogic().getConnection(),cCentroContable, cUR);

%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Resumen Poliza Manual</title>

<!-- Estilos estandar para los controles JQuery -->

<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"  href="../Generador/css/ResumenFIEL.css" />

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../js/funciones.js"></script>
<script type="text/javascript" src="ComponentesPago/js/EgresoFirmantes.js"></script>
<script type="text/javascript" src="js/ResumenPolizaManual.js"></script>
<script type="text/javascript" src="../Generador/js/PolizaEventos.js"></script>
<script type="text/javascript" src="../FIEL/js/ResumenFIEL.js"></script>

<script type="text/javascript">
	var cUR = "<%=cUR%>";
	var cLogin = "<%=cLogin%>";
	var RFC = "<%=RFCUsuario%>";
	var numeroEmpleado = "<%=numeroEmpleado%>";
	var mostrarResultado = <%=mostrarResultado%>;
	var mensaje = "<%=msg%>";
	var tipoAutorizacion = "<%=tipoAutorizacion%>";
	$(document).ready(function() {
		
		$("#cTipoPago").val("<%=cTipoPago%>");
		init(false);
		initFiel();
		
		queryFormPost("folioSAI_FIELRead", {async:false});
		
		$("#loginFirma").val( cLogin );
		$("#ueFirma").val( cUR );
		$("#rfcFirma").val( RFC );
		$("#tipoAutorizacion").val( tipoAutorizacion ); 
		$('#mnLogTbl tbody').html("<%=result%>");
	});
</script>

</head>

<body id="dt_example" bottomMargin="0" leftMargin="0" topMargin="0" style="width: 99%">
	<form action="" method="post" id="">
		<input type="hidden" name="mesAbierto" id="mesAbierto"	value="<%=mesAbierto%>"/>
		<input type="hidden" name="folioSAI" id="folioSAI"	value=""/>
		<input type="hidden" name="folioDocumento" id="folioDocumento" value="<%=nFolioPago%>"> 
		<input type="hidden" name="nFolioPago" id="nFolioPago" value="<%=nFolioPago%>"> 
		<input type="hidden" name="cDocumento" id="cDocumento" value="POLIZA"> 
		<input type="hidden" name="cTipoDocumento" id="cTipoDocumento" value="POLIZA"> 
		<input type="hidden" name="cTipoPago" id="cTipoPago" value=""> 
		<input type="hidden" name="usuario" id="usuario" value="<%=usuario.getLogin()%>">
		<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value="<%=usuario.getU_UR()%>"> 
		<input type="hidden" name="nenviadosicop" id="nenviadosicop" value="">
		<input type="hidden" name="cEstatus" id="cEstatus" value="">
		


		<div id="container" class="container" style="width: 99%">
			<br/>
			<table>
				<tr>
					<td align="right">
						<a href="#" onclick="regresar();return false;">Ver listado de pendientes</a>
					</td>
				<tr>
			</table>
			<br/>
			<jsp:include page="PolizaDescripcion.jsp"></jsp:include>


		</div>

	</form>
	<jsp:include page="../FIEL/OperacionesFIEL.jsp"></jsp:include>
</body>

</html>