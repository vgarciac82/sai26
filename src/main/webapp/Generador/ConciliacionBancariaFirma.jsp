<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

	String action = request.getParameter("a");
	String cTipoPago = request.getParameter("d");
	int idComision = Integer.parseInt(request.getParameter("f"));
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
<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"	href="css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>

<script type="text/javascript" src="js/jquery-3.5.1.min.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/Moment.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="js/funciones.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.blockUI-2.70.0.js"></script>
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
		
		$("#idComision").val("<%=idComision%>");
		$("#orden").val("<%=orden%>");
		
		queryFormPost("estatusFirmanteConciliacion_Read", {async : false});
		queryFormPost("resumenConciliacionFIELRead", {async : false});
		
		$("#AceptarFIEL").button().click(function(){
			aceptarDlg();
		});
		$("#CancelarFIEL").button().click(function(){
			cancelarDlg();
		});
	}

	function autorizaReporte() {
		$("#nFolios").val($("#idComision").val());
		$("#dlg-FIEL").show();
	}

	function rechazaReporte() {
		$("#folios").val($("#idComision").val());
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
					$.blockUI({message: "Procesando espere ......"});
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
				$.blockUI({message: "Procesando espere ......"});
				$("#operacionesDiv").css("display", "none");
				$("#FormContrato").attr("action", "../firma/RechazaReporte");
				$("#FormContrato").submit();
			}else{
				$("#motivoRechazo").val("");
				$("#dlgMotivoRechazo").hide();
				$("#operacionesDiv").show();
			}
		}	
		
</script>
</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<div id="container" class="container">
		<form id="FormContrato" name="FormContrato" method="post">
			<input type="hidden" name="folios" id="folios" value="" />
			<input type="hidden" name="firmado" id="firmado" value="" /> 
			<input type="hidden" name="cTipoPago" id="cTipoPago" value="CONCILIABANCOS" />
			<input type="hidden" name="documento"  	id="documento" 		value="CONCILIABANCOS"/> 
			<input type="hidden" name="cEstatus" id="cEstatus" value=""> 
			<input type="hidden" name="idComision" id="idComision" value="">
			<input type="hidden" name="nOrden" id="nOrden" value="<%=orden%>">
			<p class="h5 mt-4">Solicitud de Firma Electronica de la Conciliacion Bancaria</p>
			<div id="dlg-Msg" style="display: none">
				<%if( fielMsg != null){ %>
				<div id="fielWarning" class="alert alert-danger" role="alert">
				  <b>Firma a punto de Expirar</b><br> 
				  <div><p id="msgWarning" style="font-weight: bold;"><%=fielMsg%></p></div>
				</div>
				<%} else {%>
				<div class="card" id="mnLogTbl" >
					<div class="card-header">
						<h5>Resultado de la operacion</h5>
					</div>
					<div class="card-body">
						<div class="row">
							<div class="col-12">
								<%=result%>
							</div>
						</div>
					</div>
				</div>
				<%}%>
			</div>
			<div id="generales">
				<div id="enc">
					<div class="card">
						<div class="card-header">
							<h5>Datos Generales</h5>
						</div>
						<div class="card-body">
							<div class="row">	
								<div class="col-md-3">
									Folio:
									<input type="text" class="form-control" id="nconciliacion" name="nconciliacion" readonly/>
								</div>
								<div class="col-md-3">
									Mes:
									<input type="text" class="form-control" id="nMes" name="nMes" readonly/>
								</div>
								<div class="col-md-6">
									Cuenta Bancaria:
									<input type="text" class="form-control" id="nCban" name="nCban" readonly/>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div id="reporteShow" class="mt-2">
					<table width="100%">
						<tr>
							<td align="left"><b>Conciliación Bancaria Generada:</b>
						</tr>
						<tr>
							<td>
								<iframe id="tbl-resp-oper" src="../firma/MuestraReporte?f=<%=idComision%>&documento=CONCILIABANCOS" scrolling="auto"
									width="100%" height="400px" frameborder="0" marginheight="0"
									marginwidth="0" style="padding: 0px;"></iframe>
							</td>
						</tr>
					</table>
				</div>
			</div>
			
			<div id="dlgMotivoRechazo" style="display:none" class="mt-4">
				<div class="card">
					<div class="card-header">
							<h5>Motivo de Rechazo:</h5>
					</div>
					<div class="card-body">
						<div class="row">
							<div class="col-8">
								<label class="col-form-label" for="motivoRechazo">Es requerido indicar el motivo del rechazo</label>
								<textarea class="form-control" rows="3" id="motivoRechazo" name="motivoRechazo"></textarea>
							</div>
						</div>
						<div class="row">
							<div class="col-3"></div>
							<div class="col-3">
								<input type="button" class="btn btn-secondary"  id="CancelarBtn" value="Cancelar">
							</div>
							<div class="col-3">
								<input type="button" class="btn btn-primary"    id="RechazarBtn"	value="Aceptar"/>
							</div>
						</div>
					</div>
				</div>
			</div>
		</form>
		
		<div id="operacionesDiv" style="display: none" class="mt-2">
			<div class="card">
						<div class="card-header">
							<h5>Operaciones:</h5>
						</div>
						<div class="card-body">
							<div class="row">
								<div class="col-4"></div>
								<div class="col-3">
									<input title="Rechazar Reporte" class="btn btn-secondary"  type="button" value="Rechazar Reporte" id="rechazaReporte" />
								</div>
								<div class="col-3">
									<input title="Autorizar Reporte" class="btn btn-primary" type="button" value="Autorizar Reporte" id="autorizaReporte" />
								</div>
							</div>
						</div>
				</div>
		</div>

		<div id="dlg-FIEL" title="Seleccion de archivos" style="display: none;" class="mt-2">
			<form id="autorizaLayouts" name="autorizaLayouts" method="POST" action="../firma/AutorizaReporte" enctype="multipart/form-data">
				<input id="rfcFirma" name="rfcFirma" type="hidden" value="<%=RFCUsuario%>"> 
				<input id="urlRetorno" name="urlRetorno" type="hidden" value="../Generador/ConciliacionBancariaFirma.jsp">
				<input id="tipoPagoSeleccionado" name="tipoPagoSeleccionado" type="hidden" value=""> 
				<input id="loginFirma" name="loginFirma" type="hidden" value="<%=cLogin%>"> 
				<input id="ueFirma" name="ueFirma" type="hidden" value="<%=cUR%>"> 
				<input id="nFolios" name="nFolios" type="hidden" value=""> 
				<input id="orden" name="orden" type="hidden" value="<%=orden%>">

				<div class="card">
					<div class="card-header">
							<h5>Ingrese su firma Electronica:</h5>
					</div>
					<div class="card-body">
							<div class="row">
								<div class="col-12">
									Archivo *.cer
									<input type="file" class="form-control" name="cerFile" id="cerFile" class="dlgFielInpt"/>
								</div>
								<div class="col-12">
									Archivo *.key
									<input type="file" class="form-control" name="keyFile" id="keyFile" class="dlgFielInpt"/>
								</div>
								<div class="col-4">
									Password
									<input type="password" class="form-control" name="passwordLlave" id="passwordLlave" class="dlgFielInpt"/>
								</div>
							</div>
							<div class="row mt-2">
								<div class="col-4"> </div>
								<div class="col-2"> 
									<input type="button" class="btn btn-secondary" id="CancelarFIEL" value="Cancelar"/>
								</div>
								<div class="col-2"> 
									<input type="button" class="btn btn-primary" id="AceptarFIEL" value="Firmar"/>
								</div>
							</div>
					</div>
				</div>
			</form>
		</div>

	</div>
</body>
</html>
