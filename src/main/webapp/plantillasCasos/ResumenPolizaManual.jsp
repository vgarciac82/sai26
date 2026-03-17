<%@page import="com.syc.gestion.core.Operacion"%>
<%@page import="com.syc.gestion.core.CasoOperacion"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% 
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (c == null || usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String folioDoc = c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1);
	
	int idOperacion = c.getCasoOperacion( 0 ).getOperacion().getNumero(  );
	boolean esConsulta = (idOperacion == 4);
	
	String cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
%>
	
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Resumen Poliza Manual</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"  href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/resumenPago.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/ResumenFIEL.css"></link>
<link rel="stylesheet" type="text/css" href="ComponentesPago/CSS/EgresoFirmantes.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="ComponentesPago/js/EgresoFirmantes.js"></script>
<script type="text/javascript" src="js/ResumenPolizaManual.js"></script>
<script type="text/javascript" src="../Generador/js/PolizaEventos.js"></script>
<script type="text/javascript">
	var esConsulta = <%=esConsulta%>;
	$(document).ready(function() {
		$("#pb_cancel", parent.window.document).hide();
		$("#pb_leave", parent.window.document).hide();
		$("#pb_save", parent.window.document).hide();
		$("#pb_send", parent.window.document).hide();
		
		if(!esConsulta)
			$("#operacionesDiv").show();
	});
</script>

</head>

<body id="dt_example" bottomMargin="0"  leftMargin="0" topMargin="0" style="width: 99%">
	<form action="" method="post" id="">
		<input type="hidden" name="cTipoPoliza" id="cTipoPoliza" value="">
		<input type="hidden" name="cCentroContable" id="cCentroContable" value="<%=cCentroContable%>">
		<input type="hidden" name="empleadoCaptura" id="empleadoCaptura" value="">
		<input type="hidden" name="folioDocumento" id="folioDocumento" value="<%=folioDoc%>">
		<input type="hidden" name="nFolioPago" id="nFolioPago" value="<%=folioDoc%>">
		<input type="hidden" name="cDocumento" id="cDocumento" value="POLIZA">
		<input type="hidden" name="cTipoDocumento," id="cTipoDocumento," value="POLIZA">
		<input type="hidden" name="cTipoPago" id="cTipoPago" value="POLIZA">
		<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value="">
		<input type="hidden" name="cTipoFirmante" id="cTipoFirmante" value="">
		<input type="hidden" name="tOperacion" id="tOperacion" value="">
		<input type="hidden" name="Status" id="Status" value="">
		<input type="hidden" name="resp" id="resp" value="">
		<input type="hidden" name="usuario" id="usuario" value="<%=usuario.getLogin()%>">
		<input type="hidden" name="id_caso" id="id_caso" value="<%=c.getIdCaso()%>">
		<input type="hidden" name="nFolioPoliza" id="nFolioPoliza" value="<%=c.getIdCaso()%>">
		
		
		<div id="container" class="container" style="width: 99%">
			<div class="card-header"> 
				<h3>Poliza Manual.</h3>
			</div>
			
			<jsp:include page="PolizaDescripcion.jsp"></jsp:include>
			
			<div id="firmantesDiv" style="width: 99%">
				<div class= "card">
					<div class="card-body">	
						<h5> Seleccione los Firmantes </h5>
						<hr class="mt-3">
						
						<jsp:include page="../plantillasCasos/ComponentesPago/EgresoFirmantesBs.jsp"></jsp:include>
					</div>
				</div>
			</div>
			
			<div id="operacionesDiv" style="display: none">
				<fieldset>
					<legend>Operaciones</legend>
					<table align="center">
						<tr>
							<td align="center">
								<input type="button" id="autorizarBtn" value="Autorizar">
								<input type="button" id="rechazarBtn" value="Rechazar">
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
			<div id="operacionesConsultaDiv" style="display: none">
				<fieldset>
					<legend>Operaciones</legend>
					<table align="center">
						<tr>
							<td align="center">
								<input type="button" id="imprimirBtn" value="Imprimir P&oacute;liza">
								<input type="button" id="actualizarFirmantesBtn" value="Actualizar Firmantes">
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
		</div>
	</form>
	
	<form id="liberardocumento" name="liberardocumento" action="../gstnmngr/gestion?cmd=22" target="content-iframe" method="post">
	</form>
</body>

</html>