<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String action = request.getParameter("a");
	
	int nFolioPago = Integer.parseInt(StringUtils.isEmpty(request.getParameter("f")) ? "0" : request.getParameter("f"));
	String cTipoPago = request.getParameter("d");
	
	Map<?, ?> rol = null;
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	rol = usuario.getRoles();
	
	String cLogin = usuario.getLogin();
	String cUR = usuario.getU_UR();
	String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	String RFCUsuario = usuario.getuRFC();
	String result = StringUtils.trimToEmpty((String) session.getAttribute("RESULT"));
	String tipoAutorizacion = ( "VoBoPagoMasivo".equals(action) ? "VOBO" : ( "AutPagoMasivo".equals(action) ? "AUT": "") );
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>


<title>Resumen de Pagos Masivos</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<link href="../css/interfaz.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>

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

div#users-contain {
	width: 100%;
	margin: 20px 0;
}

div#users-contain table {
	margin: 1em 0;
	border-collapse: collapse;
	width: 100%;
}

div#users-contain table td, div#users-contain table th {
	border: 1px solid #eee;
	padding: .6em 10px;
	text-align: left;
}

.ui-dialog .ui-state-error {
	padding: .3em;
}

.validateTips {
	border: 1px solid transparent;
	padding: 0.3em;
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
		var roles="";
		var cUR = "<%=cUR%>";
		var cLogin = "<%=cLogin%>";
		var RFC = "<%=RFCUsuario%>";
		var numeroEmpleado = "<%=numeroEmpleado%>";
		var mostrarResultado = <%=mostrarResultado%>;
		var mensaje = "<%=msg%>";
		var tipoAutorizacion = "<%=tipoAutorizacion%>";
		 
		$(document).ready(function() {
		
			$("#cTipoPago").val("<%=cTipoPago%>");
			
			$("#rechazaPago").button().click(function(){
				rechazaPago();
			});
			
			$("#autorizaPago").button().click(function(){
				autorizaPago();
			});
			
			queryFormPost("estausAutorizacionPagoMasivo_Read", {async:false});
			
// 			reloadTabla();
			readOnlyInput();
			readOnlyTextArea();
			cssDisabledInput();
			cssDisabledTextArea();
			
			<%String roles = "";
			String role = "";
			Iterator it1 = rol.entrySet().iterator();
			while (it1.hasNext()) {
				Map.Entry r = (Map.Entry) it1.next();
				role = (String) r.getKey();
				roles += r.getKey().toString() + ",";
			}%>
			
			$("input.AyudaSyC").subIniciaDlg();
		    $("input.autoCompletaSyC").subIniciaAutoCompleta();
		    roles="<%=roles%>";
			
			init();
			
			consultaTablas();
			creaDialogoSeleccion();
			creaDialogoLog();
			
			if( !mostrarResultado ){
				
				if( "VOBO" == tipoAutorizacion ){
				
					if( parseInt(  $("#nenviadosicop").val(), 10  ) == -2 ){
						$("#operacionesDiv").css("display","block");
					}else if( parseInt(  $("#nenviadosicop").val(), 10  ) > -2 ){
						alert("Usted ya ha firmado este documento. El tramite se encuentra en estatus " + $("#cEstatus").val() );
					}
					
				}else if( "AUT" == tipoAutorizacion ){
					if( parseInt(  $("#nenviadosicop").val(), 10  ) == -1 ){
						$("#operacionesDiv").css("display","block");
					}else if( parseInt(  $("#nenviadosicop").val(), 10  ) > -1 ){
						alert("Usted ya ha firmado este documento.");
					}else if( parseInt(  $("#nenviadosicop").val(), 10  ) > -1 ){
						alert("El tramite se encuentra en estatus " + $("#cEstatus").val() );
					}
				}
			}
			
			dialogoProcesar();
			
			if( mensaje != "" )
				alert(mensaje);
			
			$('#passwordLlave').prop('readonly', false);
			$('#cerFile').prop('readonly', false);
			$('#keyFile').prop('readonly', false);
			
			$('#passwordLlave').css('background-color', "#FFFFFF");
		});
		
		function muestraLog() {
			$("#dlg-Msg").dialog("open");
		}
				
		function regresar(){
			location.href = "../Generador/VoBoMasivo.jsp?TYPE=" + tipoAutorizacion ;		
		}
		
		/**
		 * Crea dialogo que muestra el log
		 */
		function creaDialogoLog() {
		
			if( mostrarResultado )
				$("#logTable").css("display", "block");
		
			$("#dlg-Msg").dialog({
				autoOpen : mostrarResultado,
				height : 600,
				width : 800,
				modal : true,
				buttons : {
					"Aceptar" : function() {
						$(this).dialog("close");
					}
				}
			});
		}
				
		/**
		 * Crea dialogo de seleccion
		 */
		function creaDialogoSeleccion() {
		
			$("#dlg-FIEL").dialog({
				autoOpen : false,
				height : 450,
				width : 500,
				modal : true,
				buttons : {
					"Aceptar" : function() {
						aceptarDlg();
					},
					"Cancelar" : function() {
						cancelarDlg();
					}
				},
				close : function() {},
				open : function() {
					$(".dlgFielInpt").each(function() {
						$(this).val("");
					});
				}
			});
			
		}
		
		function dialogoProcesar(){
			$('#dialog-procesar').dialog({
			    autoOpen: false,
			    modal: true,
			    resizable: false,
			    width: 350,
			    heigth: 200,
			    title: 'Procesando Cancelaci\u00f3n de Devengado',
			    show: "blind",
			    hide: "scale",
			    closeOnEscape: false,
				beforeClose: function( event, ui ) {
					return true;			
				},
			    overlay: { backgroundColor: '#FFF',
						   opacity: 6.5   
				}
			});
		}
		
		function reloadTabla(){
		
			oTableIntegradas = $("#tblIntegradas").dataTable({
				bPaginate: true,
       			bLengthChange: true,
       			bFilter: true,
       			bSort: false,
       			bInfo: false,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay Datos",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				},
				aaSorting: [[ 0, "asc" ]]
			});
			
		}
		
		function init(){
			$("#cTipoPago").val("<%=cTipoPago%>");
			 queryFormPost("datosResumenPagoMasivo", {async: false});
		}
		
		function consultaTablas(){
			
			var condDTDetalle = " folio = " + $("#nFolioPago").val();
			oTableIntegradas = $("#tblIntegradas").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
				sScrollX: "100%",
				bAutoWidth: false,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				},
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_CargaMasivaDetalle&qw="+condDTDetalle,
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{sName: "folio"},
					{sName: "cidrfc"},
					{sName: "cnombre"},
					{sName: "cconcepto"},
					{sName: "mimportemasiva"} 
				]
			});
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

		function autorizaPago(){
			$("#nFolios").val( $("#nFolioPago").val() );
			$("#dlg-FIEL").dialog("open");
		}
		
		function rechazaPago(){
			var msg = "Est\u00e1 a punto de cancelar la integraci\u00f3n con folio: " + $("#nFolioPago").val();
			if( confirm(msg) ) {
				$("#operacionesDiv").hide();
				$("#dialog-procesar" ).dialog( "open" );
				$.ajax({
						url: '../egresos/cancelaIntegracion',
						type: 'post',
						dataType: 'json',
						data: {nFolio:$("#FolioCargaMasiva").val() },
						success: function(data){
							var exito = data.success === "true";
							if( exito ){
								alert("Solicitud Cancelada exitosamente.");
							}else{
								var msg = data.data_1.result;
								alert("No se cancelo la solicitud debido al error: \n" + msg );
								$("#operacionesDiv").show();								
							}
							
							$("#dialog-procesar" ).dialog( "close" );
							
						}
				});
			}
		}
		 
		function cancelarDlg() {
			$("#nFolios").val("");
			$("#dlg-FIEL").dialog("close");
			$(".dlgFielInpt").each(function() {
				$(this).val("");
			});
		}
		
		function aceptarDlg() {
			var msgValidaciones = validaCamposCompletos();
			if( "" == msgValidaciones ) {
				var msg = "Esta a punto de aceptar el proceso de los pagos con folio: " + $("#nFolios").val();
				if( confirm(msg) ) {
					$("#tipoPagoSeleccionado").val( $("#cTipoPago").val() );
					$("#autorizaLayouts").submit();
					$("#dlg-FIEL").dialog("close");
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
	</script>
</head>

<body id="dt_example" onkeydown="return(desactivaBackspace(event))">
	<div id="container" class="container" style="width: 98%;">
		<table>
			<tr>
				<td align="right">
					<a href="#" onclick="regresar();return false;">Ver listado de pendientes</a>
				</td>
			<tr>
		</table>
		<fieldset>
			<legend>Resumen del Pago.</legend>
			<form action="" name="formCampos" id="formCampos">
				<input type="hidden" id="nenviadosicop" name="nenviadosicop" value="">
				<input type="hidden" id="cEstatus" name="cEstatus" value="">
				<input type="hidden" name="cTipoPago" id="cTipoPago" value="" />
				<input type="hidden" name="nFolioDev" id="nFolioDev" value="" />
				<input type="hidden" id="nFolioPago" name="nFolioPago" value="<%=nFolioPago%>" />
				<div>
					<table style="width: 98%">
						<tr>
							<td>Tipo de Pago:<br />
							<input id="tipoPago" name="tipoPago" value="Relacion de Gastos" style="width: 200px" />
							</td>
							<td>Folio de Integracion:<br />
								<input type="text" id="FolioCargaMasiva" name="FolioCargaMasiva" value="" />
							</td>
							<td>Fecha de Carga:<br />
							<input id="fechaCarga" name="fechaCarga" value="" />
							</td>
							<td>Fecha de Aplicaci&oacute;n:<br />
							<input id="fechaAplicacion" name="fechaAplicacion" value="" />
							</td>
						</tr>
						<tr>
							<td>Importe Total Integracion:<br />
							<input id="importeCarga" name="importeCarga" value="$ 0.00" />
							</td>
							<td>Tramites Integrados:<br />
								<input id="totalCargadas" name="totalCargadas" value="" />
							</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
						</tr>
					</table>
				</div>
				<div>
					<fieldset>
						<legend>Tramites Integrados.</legend>
						<div style="width: 100%">
							<table id="tblIntegradas" class="display">
								<thead>
									<tr align="center">
										<th align="center" style="width: 5%">Folio</th>
										<th align="center" style="width: 15%">RFC</th>
										<th align="center" style="width: 15%">Beneficiario</th>
										<th align="center" style="width: 55%">Concepto</th>
										<th align="center" style="width: 10%">Importe</th>
									</tr>
								</thead>
								<tbody></tbody>
							</table>
						</div>
					</fieldset>
				</div>
				<div id="operacionesDiv" style="display: none">
					<fieldset>
						<legend>Operaciones:</legend>
						<table style="width: 98%">
							<tr style="width: 100%">
								<td><input title="Rechazar Pago" type="button" value="Rechazar pago" id="rechazaPago" /></td>
								<td align="right"><input title="Autorizar Pago" type="button" value="Autorizar pago" id="autorizaPago" /></td>
							</tr>
						</table>
					</fieldset>
				</div>
			</form>
		</fieldset>
	</div>
	<div id="dlg-FIEL" title="Seleccion de archivos">
		<form id="autorizaLayouts" name="autorizaLayouts" method="POST" action="../firmaSolicitudPago" enctype="multipart/form-data">
			<input id="urlRetorno" name="urlRetorno" type="hidden" value="Generador/ResumenPagosMasivo.jsp">
			<input id="tipoPagoSeleccionado" name="tipoPagoSeleccionado" type="hidden" value="">
			<input id="loginFirma" name="loginFirma" type="hidden" value="<%=cLogin%>">
			<input id="ueFirma" name="ueFirma" type="hidden" value="<%=cUR%>">
			<input id="rfcFirma" name="rfcFirma" type="hidden" value="<%=RFCUsuario%>">
			<input id="tipoAutorizacion" name="tipoAutorizacion" type="hidden" value="<%=tipoAutorizacion%>">
			<input id="nFolios" name="nFolios" type="hidden" value="">
			<input id="esTramiteMasivo" name="esTramiteMasivo" type="hidden" value="true">
			<fieldset>
				<legend>Ingrese su firma Electronica</legend>
				<table>
					<tr>
						<td align="right">Archivo *.cer</td>
						<td align="left"><input type="file" size="30" name="cerFile" id="cerFile" class="dlgFielInpt"></td>

					</tr>
					<tr>
						<td align="left">Archivo *.key</td>
						<td align="left"><input type="file" size="30" name="keyFile" id="keyFile" class="dlgFielInpt"></td>
					</tr>
					<tr>
						<td align="left">Password</td>
						<td align="left"><input type="password" size="30" name="passwordLlave" id="passwordLlave" class="dlgFielInpt"></td>
					</tr>
				</table>
			</fieldset>
		</form>
	</div>
	
	
	<div id="dlg-Msg">
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
	
	<div id="dialog-procesar">
		<div id="esperar" align="center">Espere por favor....
				<img border="0" src="../imagenes/espera.gif" height="30">
		</div>
	</div>
	
</body>
</html>
