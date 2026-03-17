<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

	String action = request.getParameter("a");
	String cTipoPago = request.getParameter("d");
	int idReporte = Integer.parseInt(request.getParameter("f"));
	int orden = Integer.parseInt(request.getParameter("o"));

	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	String cLogin = usuario.getLogin();
	String cUR = usuario.getU_UR();
	String cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	String RFCUsuario = usuario.getuRFC();
	String result = StringUtils.trimToEmpty( (String) session.getAttribute("RESULT") );
	String fielMsg = null;
	session.removeAttribute("RESULT");
	
	String numeroEmpleado = "";
	numeroEmpleado = usuario.getNumeroEmpleado();
	String msg = StringUtils.trimToEmpty(request.getParameter("msgError"));

	boolean fielExpiringSoon = session.getAttribute("EXPIRING_SOON") != null && ((Boolean)session.getAttribute("EXPIRING_SOON")); 
	if(fielExpiringSoon){
		fielMsg = (String) session.getAttribute("EXPIRING_MSG");
		session.removeAttribute("EXPIRING_SOON");
		session.removeAttribute("EXPIRING_MSG");
	}
	
	boolean mostrarResultado = !StringUtils.isBlank(result);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Firma Electronica de Reportes.</title>

<!-- Estilos estandar para los controles JQuery -->
<link href="../css/interfaz.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css"	href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"	href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/demo_page.css"></link>

<style type="text/css" title="currentStyle">
	@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
	@import "../Generador/css/demo_table_jui.css";
	@import "../Generador/css/demo_page.css";
</style>
<style>
div#dialog-form fieldset {
	padding: 0;
	border: 0;
	margin-top: 25px;
}
</style>
<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="js/funciones.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>

<script type="text/javascript" charset="utf-8">
	var cUR = "<%=cUR%>";
	var cLogin = "<%=cLogin%>";
	var RFC = "<%=RFCUsuario%>";
	var numeroEmpleado = "<%=numeroEmpleado%>";
	var mostrarResultado = <%=mostrarResultado%>;
	var mensaje = "<%=msg%>";
	var orden = <%=orden%>;
	
	$(document).ready(function() {
		
		$("#rechazaReporte").button().click(function() {
			rechazaReporte();
		});

		$("#autorizaReporte").button().click(function() {
			autorizaReporte();
		});
		
		readOnlyTextArea();
		cssDisabledTextArea();
		creaDialogoLog();
		init();

		$("#dialog-procesar").hide();

		if( !mostrarResultado ){
			if( "CANCELADO" ==  $("#cEstatus").val() ){
				$("#operacionesDiv").css("display","none");
				alert( "El tramite ha sido cancelado." );
			}else if( "FIRMADO" ==  $("#cEstatus").val() ){
				$("#operacionesDiv").css("display","none");
				alert("El tramite ha sido autorizado.");
			}else{
					if( "F" == $("#firmado").val()){
						$("#operacionesDiv").css("display","none");
						alert("Usted ya ha firmado este reporte. ");
					}else{
						$("#operacionesDiv").css("display", "block");
					}
				}
		}
		
		$("#motivoRechazo")[0].readOnly = false;
		$("#motivoRechazo").css("background","#FFFFFF");
	});

	function init() {
		
		$("#idReporte").val("<%=idReporte%>");
		$("#orden").val("<%=orden%>");
		$("#cTipoPago").val("<%="REPORTE"%>");
		
		queryFormPost("estausFirmaReporte_Read", {async:false});
		queryFormPost("estausFirmanteReporte_Read", {async : false});
		queryFormPost("resumenEdoFinFIELRead", {async : false});
		
		$("#AceptarFIEL").button().click(function(){
			aceptarDlg();
		});
		$("#CancelarFIEL").button().click(function(){
			cancelarDlg();
		});
	}

	function autorizaReporte() {
		$("#nFolios").val($("#idReporte").val());
		$("#dlg-FIEL").show();
	}

	function rechazaReporte() {
		$("#folios").val($("#idReporte").val());
		$("#dlg-FIEL").hide();
		$("#operacionesDiv").hide();
		$("#dlgMotivoRechazo").show();
		$("#motivoRechazo").focus();
	}

	/**
	 * Crea dialogo que muestra el log
	 */
	function creaDialogoLog() {
		if(mostrarResultado)
			$("#dlg-Msg").show();
		else 
			$("#dlg-Msg").hide();
		
		$("#RechazarBtn").button().click( function() {
			cancelaSolCaja();
		});
					
		$("#CancelarBtn").button().click( function() {
			$("#motivoRechazo").val("");
			$("#dlgMotivoRechazo").hide();
			$("#operacionesDiv").show();
		});
	}
	
	function cancelarDlg() {
		$("#nFolios").val("");
		$("#dlg-FIEL").hide();
		$(".dlgFielInpt").each(function() {
			$(this).val("");
		});
	}
	
	function aceptarDlg() {
			var msgValidaciones = validaCamposCompletos();
			if( "" == msgValidaciones ) {
				var msg = "Esta a punto de aceptar el contenido en el reporte: " + $("#nFolios").val();
				if( confirm(msg) ) {
					$("#tipoPagoSeleccionado").val( $("#cTipoPago").val() );
					$("#autorizaLayouts").submit();
					$("#dlg-FIEL").hide();
					$.blockUI({
						message : "<h1>Espere ...</h1>"
					});
				}
			} else {
				alert(msgValidaciones);
			}
	}
	
	function validaCamposCompletos() {
			var msg = "";
			var token = "";
		
			if( $("#cerFile").val() == "" ) {
				msg = "Es necesario que adjunte su certificado.";
				token = "\n";
			} else if( !fileValidation(".cer", $("#cerFile").val()) ) {
				msg = msg + token + "El certificado debe tener una extencion .cer";
				token = "\n";
			}
		
			if( $("#keyFile").val() == "" ) {
				msg = msg + token + "Es necesario que adjunte su llave privada.";
				token = "\n";
			} else if( !fileValidation(".key", $("#keyFile").val()) ) {
				msg = msg + token + "La llave privada debe tener una extencion .key";
				token = "\n";
			}
		
			if( $("#passwordLlave").val() == "" )
				msg = msg + token + "El password de su llave privada es requerido";
		
			return msg;
		}
		
		function fileValidation(extencionesPermitidas, filePath) {
			var allowedExtensions = eval("/(" + extencionesPermitidas + ")$/i");
			if( allowedExtensions.exec(filePath) )
				return true;
			else
				return false;
		}	
		
		function cancelaSolCaja(){
		
			if( $("#motivoRechazo").val() == "" ){
				alert("El motivo de rechazo es requerido.");
				return false;
			}
			
			if( confirm("Esta seguro de rechazar la solicitud?") ){
				$.blockUI({
						message : "<h1>Espere ...</h1>"
					});
				$("#operacionesDiv").css("display", "none");
				$("#FormContrato").attr("action", "../firma/RechazaReporte");
				$("#FormContrato").submit();
			}else{
				$("#motivoRechazo").val("");
				$("#dlgMotivoRechazo").hide();
				$("#operacionesDiv").show();
			}
		}	
		function regresar(){
			location.href = "../FIEL/ListadoReportesPendientes.jsp";		
		}
</script>
</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<div id="container" class="container">
		<form id="FormContrato" name="FormContrato" method="post">
			<input type="hidden" name="folios" id="folios" value="" />
			<input type="hidden" name="firmado" id="firmado" value="" /> 
			<input type="hidden" name="cTipoPago" id="cTipoPago" value="REPORTE" /> 
			<input type="hidden" name="cEstatus" id="cEstatus" value=""> 
			<input type="hidden" name="idReporte" id="idReporte" value="">
			<input type="hidden" name="nOrden" id="nOrden" value="<%=orden%>">
			<h1>Solicitud de Firma Electronica de Reporte</h1>
			<div>
				<table>
					<tr>
						<td align="right">
							<a href="#" onclick="regresar();return false;">Ver listado de pendientes</a>
						</td>
					<tr>
				</table>
			</div>
			<div id="dlg-Msg" style="display: none">
				
				<%if( fielMsg != null){ %>
				<div id="fielWarning" class="col-12 col-lg-12 col-md-12 col-sm-12">
					<fieldset>
						<legend>Firma a punto de Expirar</legend>
						<div><p id="msgWarning" style="font-weight: bold;"><%=fielMsg%></p></div>
					</fieldset>
				</div>
				<%} %>
				
				<fieldset>
					<legend>Resultado de la operacion</legend>
					<table id="mnLogTbl" align="center" border="1">
						<thead>
							<tr>
								<th>Log</th>
							</tr>
						</thead>
						<tbody>
							<%=result%>
						</tbody>
					</table>
				</fieldset>
			</div>
			<div id="generales">
				<div id="enc">
					<fieldset>
						<legend>Datos Generales</legend>
						<table align="center">
							<tr>
								<td align="left">Folio:</td>
								<td align="left">Estado Financiero:</td>
								<td align="left">Moneda:</td>
								<td align="left">Nivel:</td>
								<td align="left">Mes:</td>
							</tr>
							<tr>
								<td align="left"><input type="text" size="5"
									id="nIDEdoFinanciero" name="nIDEdoFinanciero" readonly></td>
								<td align="left"><input type="text" size="40"
									id="cDescReporte" name="cDescReporte" readonly></td>
								<td align="left"><input type="text" size="10"
									id="cDescMoneda" name="cDescMoneda" readonly></td>
								<td align="left"><input type="text" size="10"
									id="cDescNivel" name="cDescNivel" readonly></td>
								<td align="left"><input type="text" size="10" id="nMes"
									name="nMes" readonly></td>

							</tr>
						</table>
					</fieldset>
				</div>
				<div id="reporteShow">
					<table width="100%">
						<tr>
							<td align="left"><b>Reporte Generado:</b>
						</tr>
						<tr>
							<td><iframe id="tbl-resp-oper"
									src="../firma/MuestraReporte?f=<%=idReporte%>" scrolling="auto"
									width="100%" height="400px" frameborder="0" marginheight="0"
									marginwidth="0" style="padding: 0px;"></iframe></td>
						</tr>
					</table>
				</div>
			</div>
			
			<div id="dlgMotivoRechazo" style="display:none">
				<fieldset>
					<legend>Motivo de Rechazo</legend>
					<table width="40%" align="center">
						<tr>
							<td align="left"><label for="motivoRechazo">Es
									requerido indicar el motivo del rechazo</label></td>
						</tr>
						<tr>
							<td align="left"><textarea rows="5" cols="60"
									id="motivoRechazo" name="motivoRechazo"></textarea></td>
						</tr>
						<tr>
							<td align="center"><input type="button" id="RechazarBtn"
								value="Aceptar"> <input type="button" id="CancelarBtn"
								value="Cancelar"></td>
						</tr>
					</table>
				</fieldset>
			</div>
		</form>
		<div id="operacionesDiv" style="display: none">
			<fieldset>
				<legend>Operaciones:</legend>
				<table style="width: 98%">
					<tr style="width: 100%">
						<td align="center"><input title="Rechazar Reporte"
							type="button" value="Rechazar Reporte" id="rechazaReporte" /></td>
						<td align="center"><input title="Autorizar Reporte"
							type="button" value="Autorizar Reporte" id="autorizaReporte" /></td>
					</tr>
				</table>
			</fieldset>
		</div>

		<div id="dlg-FIEL" title="Seleccion de archivos" style="display: none;">
			<form id="autorizaLayouts" name="autorizaLayouts" method="POST" action="../firma/AutorizaReporte" enctype="multipart/form-data">
				<input id="rfcFirma" name="rfcFirma" type="hidden" value="<%=RFCUsuario%>"> 
				<input id="urlRetorno" name="urlRetorno" type="hidden" value="../Generador/ReportesFirma.jsp">
				<input id="tipoPagoSeleccionado" name="tipoPagoSeleccionado" type="hidden" value=""> 
				<input id="loginFirma" name="loginFirma" type="hidden" value="<%=cLogin%>"> 
				<input id="ueFirma" name="ueFirma" type="hidden" value="<%=cUR%>"> 
				<input id="nFolios" name="nFolios" type="hidden" value=""> 
				<input id="orden" name="orden" type="hidden" value="<%=orden%>">

				<fieldset>
					<legend>Ingrese su firma Electronica</legend>
					<table align="center">
						<tr>
							<td align="right">Archivo *.cer</td>
							<td align="left">
								<input type="file" size="30" name="cerFile" id="cerFile" class="dlgFielInpt">
							</td>
						</tr>
						<tr>
							<td align="left">Archivo *.key</td>
							<td align="left"><input type="file" size="30" name="keyFile"
								id="keyFile" class="dlgFielInpt"></td>
						</tr>
						<tr>
							<td align="left">Password</td>
							<td align="left"><input type="password" size="30"
								name="passwordLlave" id="passwordLlave" class="dlgFielInpt"></td>
						</tr>
						<tr>
							<td align="center" colspan="2"><input type="button"
								id="AceptarFIEL" value="Firmar"> <input type="button"
								id="CancelarFIEL" value="Cancelar"></td>
						</tr>
					</table>
				</fieldset>
			</form>
		</div>

	</div>
</body>
</html>
