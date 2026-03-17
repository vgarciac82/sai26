<%@page import="org.apache.log4j.LogManager"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="java.sql.SQLException"%>
<%@page import="com.axtel.contratos.RequisitionBussinessLogic"%>
<%@page import="com.axtel.contratos.Requisition"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	Logger log = LogManager.getLogger( "ResumenApartado.jsp" );
%>
<%

	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

	String action = request.getParameter("a");
	String cTipoPago = request.getParameter("d");
	int nFolioApartado = Integer.parseInt(request.getParameter("f"));

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
	
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	Caso c = cbl.findByFolioLike( "APARTADO", String.valueOf( nFolioApartado) );
	String folioCasoApartado = c .getFolio();
	
	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	String cEjercicio = efbl.getEjercicioFiscalActivo(  ).getaEjercicioFiscal(  );
	String msg = StringUtils.trimToEmpty(request.getParameter("msgError"));
	
	Requisition requisition = null;
	
	String fielMsg = "";
	boolean fielExpiringSoon = session.getAttribute("EXPIRING_SOON") != null && ((Boolean)session.getAttribute("EXPIRING_SOON")); 
	if(fielExpiringSoon){
		fielMsg = (String) session.getAttribute("EXPIRING_MSG");
		session.removeAttribute("EXPIRING_SOON");
		session.removeAttribute("EXPIRING_MSG");
	}
	
	String idSolicitud = "";
	String ueSolicitud = "";
	try{
		String folio = c.getFolio(  );
		RequisitionBussinessLogic rbl = new RequisitionBussinessLogic(GestionInterface.ATT_CONEXION);
		requisition = rbl.getRequisition( c );
		if( requisition == null )
			throw new RuntimeException("No se encontro apartado con folio: " +  folio );
		
		idSolicitud = requisition.getIdSolicitud(  );
		ueSolicitud = requisition.getIdUnidadEjecutora(  );
	}catch(Exception e ){
		log.error(e,e);
		msg += "Error buscando apartado: " + e;
	}

	boolean mostrarResultado = !StringUtils.isBlank(result);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Firma Electronica de Cuestionario / Requisicion.</title>

<!-- Estilos estandar para los controles JQuery -->
<link href="../css/interfaz.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

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
	var mensaje = "<%=StringUtils.isBlank( msg )? StringUtils.trimToEmpty( result ): StringUtils.trimToEmpty(msg)%>";
	var oDTDetalle;
	
	$(document).ready(function() {
		
		$("#nFolioApartado").val("<%=nFolioApartado%>");
		
		$("#rechazaTramite").button().click(function() {
			rechazaTramite();
		});

		$("#autorizaTramite").button().click(function() {
			autorizaTramite();
		});
		
		readOnlyTextArea();
		cssDisabledTextArea();
		creaDialogoLog();
		init();

		$("#dialog-procesar").hide();

		if( !mostrarResultado ){
			if( "6" !=  $("#nIdEstadoSolicitud").val() ){
				$("#operacionesDiv").css("display","none");
				alert( "Usted ya ha revisado este tramite. Estatus: " + $("#lblEstado").val() );
			}else{
				$("#operacionesDiv").css("display", "block");
			}
		}
		
		$("#motivoRechazo")[0].readOnly = false;
		$("#motivoRechazo").css("background","#FFFFFF");
	});

	function init() {
		
		$("#nFolioApartado").val("<%=nFolioApartado%>");
		$("#cTipoPago").val("<%=cTipoPago%>");
		
		queryFormPost("mApartadoVentanillaLabels", {async:false});
		queryFormPost( {
			queryName : "mApartadoTotalRead",
			async:false,
			callback:function(){
				convertMoney();
				if($("#applyQuestionnaire").val()=='N'){
					$("#trCustionario").hide();
					$("#trFileCuestionario").hide();
				}
			}
		});
		
		$("#AceptarFIEL").button().click(function(){
			aceptarDlg();
		});
		
		$("#CancelarFIEL").button().click(function(){
			cancelarDlg();
		});
		
		oDTDetalle = initDTDetalle();
	}

	function initDTDetalle(){
		
		var params = "'" + $("#cEjercicio").val() + "', '" +
			$("#cIdUnidadEjecutora").val() + "', '" + 
			$("#cIdSolicitud").val() + "'";

		return $("#tblDetalle").dataTable({
			"bDestroy": true,
			"bAutoWidth" : false,
			"bPaginate" : false,
			"sScrollX": "100%",
			"sScrollY": "150",
			"oLanguage": {
				"sProcessing": "Procesando...",
				"sLengthMenu": "Mostrar _MENU_ registros",
				"sZeroRecords": "No hay registros a mostrar",
				"sEmptyTable": "No hay datos en la tabla",
				"sLoadingRecords": "Cargando...",
				"sInfo": "Registros _START_ al _END_ de _TOTAL_",
				"sInfoEmpty": "Registro 0 al 0 de 0",
				"sInfoFiltered": "(filtado de _MAX_ registros)",
				"sInfoPostFix": "",
				"sInfoThousands": ",",
				"sSearch": "Buscar:",
				"oPaginate": {
					"sFirst":    "Primero",
					"sPrevious": "Ant.",
					"sNext":     "Sigte.",
					"sLast":     "&Uacute;ltimo" }
			},
			"bServerSide": true,
			"sAjaxSource": window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_pivotApartado(" + params + ")",
			"bProcessing": true,
			"sPaginationType": "full_numbers",
			"bJQueryUI": true,
			"aoColumns": [
				{ "sName": "EP",			"bSortable": false, "sWidth":"70%"},
				{ "sName": "cMes",			"bSortable": false, "sWidth":"12%" },
				{ "sName": "mImporte",		"bSortable": false, "sWidth":"28%" }]
       	});
 	}
 	
	function autorizaTramite() {
		$("#nFolios").val($("#nFolioApartado").val());
		$("#dlg-FIEL").show();
	}

	function rechazaTramite() {
		$("#folios").val($("#nFolioApartado").val());
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
			rechazaRequisicon();
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
				var msg = "Esta a punto de aceptar la requisicion:  " + $("#lblRequisicion").val();
				if( confirm(msg) ) {
					$("#tipoPagoSeleccionado").val( $("#cTipoPago").val() );
					$("#firmaRecepcion").submit();
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
		
		function rechazaRequisicon(){
		
			if( $("#motivoRechazo").val() == "" ){
				alert("El motivo de rechazo es requerido.");
				return false;
			}
			
			if( confirm("Esta seguro de rechazar la solicitud?\n\nSe cancelara el recurso apartado y se debera capturar nuevamente esta requisicion.") ){
				$.blockUI({
						message : "<h1>Espere ...</h1>"
					});
				$("#tipoPagoSeleccionado").val( $("#cTipoPago").val() );
				$("#nFolios").val($("#nFolioApartado").val());
				$("#folios").val($("#nFolioApartado").val());
				$("#operacionesDiv").css("display", "none");
				$("#formRechazo").attr("action", "../firma/rechazaTramite");
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
		 <form>
			<h1>Autorización Apartado</h1>
			<div id="tabs">
				<div id="tabs-0" align="center">
					<table  align="left" width="100%">
						<tr>
							<td>
								<fieldset style="width: 99%" align="left">
									<legend>
										Información de la Requisición
									</legend>
									<table  align="left" width="100%">
										<tr>
											<td width="20%" align="left">Folio Requisición: </td>
											<td align="left"><input type="text" style="width: 100%" id="lblRequisicion" name="lblRequisicion" readonly="readonly" class="readOnly" /></td>
										</tr>
										<tr>
											<td width="20%" align="left">Folio Apartado: </td>
											<td align="left"><input type="text" style="width: 100%" name="lblFolio" id="lblFolio" readonly="readonly"  class="readOnly" /></td>
										</tr>
										<tr>
											<td width="20%" align="left">Total Apartado: </td>
											<td align="left"><input type="text" style="width: 100%" name="lblTotal" id="lblTotal" readonly="readonly" class="readOnly money"  /></td>
										</tr>
										<tr>
											<td width="20%" align="left">Estado: </td>
											<td align="left"><input type="text" style="width: 100%" name="lblEstado" id="lblEstado" readonly="readonly" class="readOnly"  /></td>
										</tr>
										<tr>
											<td width="20%" align="left">Descripción: </td>
											<td align="left"><textarea id="lblDescripcion"  name="lblDescripcion" rows="5" readonly="readonly" style="width: 100%"></textarea></td>
										</tr>
										
									</table>
								</fieldset>
							</td>
						</tr>
						<tr>
							<td>
								<fieldset style="width: 99%" align="left">
									<legend>
										Lineas de Apartado
									</legend>
									<br />
									<table id="tblDetalle" class="display" width="90%" align="left">
							            <thead>
							                <tr>
							                    <th width="70%">EP</th>
							                    <th width="6%">mes</th>
							                    <th width="28%">Monto</th>
							                </tr>
							            </thead>
							        </table>
							        <br />
								</fieldset>
							</td>
						</tr>
						
						<tr>
							<td>
								 <div id="operacionesDiv" style="display: none">
									<fieldset>
										<legend>Operaciones:</legend>
										<table style="width: 100%">
											<tr style="width: 100%">
												<td align="center"><input title="Rechazar Reporte" type="button" value="Rechazar Reporte" id="rechazaTramite" /></td>
												<td align="center"><input title="Autorizar Reporte" type="button" value="Autorizar Reporte" id="autorizaTramite" /></td>
											</tr>
										</table>
									</fieldset>
								</div>
							</td>
						</tr>
						
						
					</table>
				</div>
				
			</div>
		</form>
		
		
		<div id="dlg-FIEL" title="Seleccion de archivos" style="display: none;">
			<form id="firmaRecepcion" name="firmaRecepcion" method="POST" action="../firma/AutorizaTramite" enctype="multipart/form-data">
				<input id="rfcFirma" name="rfcFirma" type="hidden" value="<%=RFCUsuario%>">
				<input id="nFolioApartado" name="nFolioApartado" type="hidden" value=""> 
				<input id="urlRetorno" name="urlRetorno" type="hidden" value="../FIEL/ResumenApartado.jsp">
				<input id="nIdEstadoSolicitud" name="nIdEstadoSolicitud" type="hidden" value="">
				<input id="applyQuestionnaire" name="applyQuestionnaire" type="hidden" value="S">
				<input id="tipoPagoSeleccionado" name="tipoPagoSeleccionado" type="hidden" value=""> 
				<input id="loginFirma" name="loginFirma" type="hidden" value="<%=cLogin%>"> 
				<input id="ueFirma" name="ueFirma" type="hidden" value="<%=cUR%>"> 
				<input id="nFolios" name="nFolios" type="hidden" value=""> 
				<input id="folioCasoApartado" name="folioCasoApartado" type="hidden" value="<%=folioCasoApartado%>">
				<input id="cEjercicio" name="cEjercicio" type="hidden" value="<%=cEjercicio%>">
				<input id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden" value="<%=ueSolicitud%>">
				<input id="cIdSolicitud" name="cIdSolicitud" type="hidden" value="<%=idSolicitud%>">
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
							<input type="button" id="CancelarFIEL" value="Cancelar"></td>
						</tr>
					</table>
				</fieldset>
			</form>
		</div>
		
		<div id="dlgMotivoRechazo" style="display:none">
			<form id="formRechazo" method="post">
				<input type="hidden" name="cTipoPago" id="cTipoPago" value="" />
				<input type="hidden" name="folios" id="folios" value="" />
				<input type="hidden" name="url_Retorno" id="url_Retorno" value="../FIEL/ResumenApartado.jsp">
				
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
			</form>
		</div>
		
		<div id="archivo">
			<table width="100%">
				<tr>
					<td align="left"><b>Archivos a Firmar:</b>
				</tr>
				<tr id="trCustionario">
					<td align="left"><b>Cuestionario:</b>
				</tr>
				<tr id="trFileCuestionario">
					<td>
						<iframe id="tbl-requisicion" src="../rm/requisitionFiles?documento=<%=cTipoPago%>&f=<%=nFolioApartado%>&g=<%=c.getIdGabinete()%>&docName=Cuestionario%20Firmado" scrolling="auto" width="100%" height="400px" frameborder="0" marginheight="0" marginwidth="0" style="padding: 0px;">
						</iframe>
					</td>
				</tr>
				<tr>
					<td align="left"><b>Requisicion:</b>
				</tr>
				<tr>
					<td>
						<iframe id="tbl-cuestionario" src="../rm/requisitionFiles?documento=<%=cTipoPago%>&f=<%=nFolioApartado%>&g=<%=c.getIdGabinete()%>&docName=Comprobante" scrolling="auto" width="100%" height="400px" frameborder="0" marginheight="0" marginwidth="0" style="padding: 0px;">
						</iframe>
					</td>
					
				</tr>
			</table>
		</div>
	</div>
</body>
</html>
