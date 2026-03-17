<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

	String action = request.getParameter("a");
	String cTipoPago = request.getParameter("d");
	int idNota = Integer.parseInt(request.getParameter("f"));

	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	String cLogin = usuario.getLogin();
	String cUR = usuario.getU_UR();
	String cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	String RFCUsuario = usuario.getuRFC();
	String result = StringUtils.trimToEmpty( (String) session.getAttribute("RESULT") );
	session.removeAttribute("RESULT");
	
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
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Firma Electronica para Estimaciones de Obra P&uacute;blica.</title>

<!-- Estilos estandar para los controles JQuery -->
<link href="../css/interfaz.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

 
<style>
	div#dialog-form fieldset {
		padding: 0;
		border: 0;
		margin-top: 25px;
	}
</style>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/funciones.js"></script>
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
	
	$(document).ready(function() {
		
		$("#idNota").val("<%=idNota%>");
		$("#rechazaEstimacion").button().click(function() {
			rechazaEstimacion();
		});

		$("#autorizaReporte").button().click(function() {
			autorizaEstimacion();
		});
		
		readOnlyTextArea();
		cssDisabledTextArea();
		creaDialogoLog();
		init();

		$("#dialog-procesar").hide();

		if( !mostrarResultado ){
			if( "2" !=  $("#idEstatusEst").val() && "5" !=  $("#idEstatusEst").val() && "6" !=  $("#idEstatusEst").val() ){
				$("#operacionesDiv").css("display","none");
				alert( "Usted ya ha revisado este tramite. Estatus: " + $("#estatus").val() );
			}else{
				$("#operacionesDiv").css("display", "block");
			}
		}
		
		$("#motivoRechazo")[0].readOnly = false;
		$("#motivoRechazo").css("background","#FFFFFF");
	});

	function init() {
		
		$("#idNota").val("<%=idNota%>");
		$("#cTipoPago").val("<%=cTipoPago%>");
		
		queryFormPost("informacionEstimacionFirmaRead", {async : false});
		
		$("#AceptarFIEL").button().click(function(){
			aceptarDlg();
		});
		
		$("#CancelarFIEL").button().click(function(){
			cancelarDlg();
		});
	}

	function autorizaEstimacion() {
		$("#nFolios").val($("#idNota").val());
		$("#dlg-FIEL").show();
	}

	function rechazaEstimacion() {
		$("#folios").val($("#idNota").val());
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
			rechazaEstimacionFolio();
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
				var msg = "Esta a punto de aceptar la estimación : " + $("#estimacion").val() +" de obra pública";
				if( confirm(msg) ) {
					$("#tipoPagoSeleccionado").val( $("#cTipoPago").val() );
					$("#firmaEstimacion").submit();
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
		
		function rechazaEstimacionFolio(){
		
			if( $("#motivoRechazo").val() == "" ){
				alert("El motivo de rechazo es requerido.");
				return false;
			}
			
			if( confirm("Esta seguro de rechazar la solicitud?") ){
				$.blockUI({
						message : "<h1>Espere ...</h1>"
					});
				$("#tipoPagoSeleccionado").val( $("#cTipoPago").val() );
				$("#nFolios").val($("#idNota").val());
				$("#operacionesDiv").css("display", "none");
				$("#formRechazo").attr("action", "../firma/rechazaEstimacion");
				$("#formRechazo").submit();
			}else{
				$("#motivoRechazo").val("");
				$("#dlgMotivoRechazo").hide();
				$("#operacionesDiv").show();
			}
		}	
		function regresar(){
			location.href = "../FIEL/ListadoRMPendientes.jsp";		
		}
</script>
</head>

<body id="dt_example">
	<div id="container" class="container">
		<form id="frmEstimacionObra" name="frmEstimacionObra" method="post">
			<input type="hidden" name="idEstatusEst" id="idEstatusEst" value="" />
			<input type="hidden" name="estatus" id="estatus" value="" />
			<input type="hidden" name="firmado" id="firmado" value="" /> 
			<input type="hidden" name="idNota" id="idNota" value="">
			<h1>Solicitud de Autorizaci&oacute;n para Estimaciones de Obra P&uacute;blica.</h1>
			
			<div style="display: none ">
				<table>
					<tr>
						<td align="right">
							<a href="#" onclick="regresar();return false;">Ver listado de pendientes</a>
						</td>
					<tr>
				</table>
			</div>
			
			<div id="dlg-Msg" style="display: none">
				<%if(fielExpiringSoon){ %>
				<div id="fielWarning">
					<fieldset>
						<legend>Firma a punto de Expirar</legend>
						<div><p id="msgWarning"><%=fielMsg%></p></div>
					</fieldset>
				</div>
				<%} %>
				<div>
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
			</div>
			
			<div id="generales">
				
				<div id="enc">
					<fieldset>
						<legend>Estimaci&oacute;n de Obra P&uacute;blica</legend>
						<table align="center">
							<tr>
								<td align="left">Folio SAI:</td>
								<td align="left">Estimaci&oacute;n:</td>
								<td align="left">Proveedor:</td>
								<td align="left">#Compranet:</td>
							</tr>
							<tr>
								<td align="left">
									<input type="text" size="20" id="cFolioSAI" name="cFolioSAI" readonly>
								</td>
								<td align="left">
									<input type="text" size="10" id="estimacion" name="estimacion" readonly>
								</td>
								<td align="left">
									<input type="text" size="25" id="beneficiario" name="beneficiario" readonly>
								</td>
								<td align="left">
									<input type="text" size="30" id="cnocontratocnet" name="cnocontratocnet" readonly>
								</td>
							</tr>
							<tr>
								<td align="left" colspan="4">Concepto del contrato:</td>
							</tr>
							<tr>
								<td align="center" colspan="4">
									<textarea rows="5" cols="80" readonly="readonly" id="cConceptoContrato"></textarea>
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
				
				<div id="nota">
					<fieldset>
						<legend>Atenta Nota:</legend>
						<table align="center">
							<tr>
								<td align="left">Folio:</td>
								<td align="left">Fecha:</td>
							</tr>
							<tr>
								<td align="left">
									<input type="text" size="20" id="cFolioNota" name="cFolioNota" readonly="readonly">
								</td>
								<td align="left">
									<input type="text" size="10" id="dfechanota" name="dfechanota" readonly="readonly">
								</td>
							</tr>
							<tr>
								<td align="left" colspan="2">Motivo:</td>
							</tr>
							<tr>
								<td align="center"  colspan="2">
									<textarea rows="5" cols="80" readonly="readonly" id="dmotivonota"></textarea>
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
				<div id="operacionesDiv" style="display: none">
					<fieldset>
						<legend>Operaciones:</legend>
						<table style="width: 98%">
							<tr style="width: 100%">
								<td align="center"><input title="Rechazar Reporte" type="button" value="Rechazar Reporte" id="rechazaEstimacion" /></td>
								<td align="center"><input title="Autorizar Reporte" type="button" value="Autorizar Reporte" id="autorizaReporte" /></td>
							</tr>
						</table>
					</fieldset>
				</div>
			</div>
		</form>	
		
		
		<div id="dlg-FIEL" title="Seleccion de archivos" style="display: none;">
			<form id="firmaEstimacion" name="firmaEstimacion" method="POST" action="../firma/AutorizaReporte" enctype="multipart/form-data">
				<input id="rfcFirma" name="rfcFirma" type="hidden" value="<%=RFCUsuario%>"> 
				<input id="urlRetorno" name="urlRetorno" type="hidden" value="../FIEL/ResumenEstimacionFIEL.jsp">
				<input id="tipoPagoSeleccionado" name="tipoPagoSeleccionado" type="hidden" value=""> 
				<input id="loginFirma" name="loginFirma" type="hidden" value="<%=cLogin%>"> 
				<input id="ueFirma" name="ueFirma" type="hidden" value="<%=cUR%>"> 
				<input id="nFolios" name="nFolios" type="hidden" value=""> 

				<fieldset>
					<legend>Ingrese su firma Electronica</legend>
					<table align="center">
						<tr>
							<td align="right">Archivo *.cer</td>
							<td align="left"><input type="file" size="30" name="cerFile"
								id="cerFile" class="dlgFielInpt"></td>
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
							<td align="center" colspan="2">
								<input type="button" id="AceptarFIEL" value="Firmar"> 
								<input type="button" id="CancelarFIEL" value="Cancelar">
							</td>
						</tr>
					</table>
				</fieldset>
			</form>
		</div>
		
		<div id="dlgMotivoRechazo" style="display:none">
			<form id="formRechazo" method="post">
				<input type="hidden" name="cTipoPago" id="cTipoPago" value="" />
				<input type="hidden" name="folios" id="folios" value="" />
				<input type="hidden" name="url_Retorno" id="url_Retorno" value="../FIEL/ResumenEstimacionFIEL.jsp">
				
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
							<td align="center">
								<input type="button" id="RechazarBtn" value="Aceptar"> 
								<input type="button" id="CancelarBtn" value="Cancelar">
							</td>
						</tr>
					</table>
				</fieldset>
			</form>
		</div>
		
		<div id="archivo">
			<table width="100%">
				<tr>
					<td align="left"><b>Archivo a Firmar:</b>
				</tr>
				<tr>
					<td>
						<iframe id="tbl-resp-oper" src="../rm/MuestraEstimacion?documento=<%=cTipoPago%>&f=<%=idNota%>" scrolling="auto" width="100%" height="400px" frameborder="0" marginheight="0" marginwidth="0" style="padding: 0px;">
						</iframe>
					</td>
				</tr>
			</table>
		</div>
	</div>
</body>
</html>
