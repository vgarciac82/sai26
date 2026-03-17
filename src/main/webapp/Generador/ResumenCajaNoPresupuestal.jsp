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
	String cTipoPago = request.getParameter("d");

	int nFolioCaja = Integer
			.parseInt(StringUtils.isEmpty(request.getParameter("f")) ? "0" : request.getParameter("f"));

	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	String cLogin = usuario.getLogin();
	String cUR = usuario.getU_UR();
	String cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	String RFCUsuario = usuario.getuRFC();
	String result = StringUtils.trimToEmpty((String) session.getAttribute("RESULT"));
	String tipoAutorizacion = ("VoBoPago".equals(action) ? "VOBO" : ("AutPago".equals(action) ? "AUT" : ""));
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
<title>Resumen de Solicitudes No Presupuestales</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<style>
estilos del dialogo
		div#dialog-form fieldset {
	padding: 0;
	border: 0;
	margin-top: 25px;
}
</style>
<script type="text/javascript" src="../Generador/js/jquery-3.5.1.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="js/funciones.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>


<script type="text/javascript" charset="utf-8">
	
	var cUR = "<%=cUR%>";
	var cLogin = "<%=cLogin%>";
	var RFC = "<%=RFCUsuario%>";
	var numeroEmpleado = "<%=numeroEmpleado%>";
	var mostrarResultado = <%=mostrarResultado%>;
	var mensaje = "<%=msg%>";
	var tipoAutorizacion = "<%=tipoAutorizacion%>";
	let modalRechazo;
	$(document).ready(function() {
		
		modalRechazo = new bootstrap.Modal(document.getElementById('dlgMotivoRechazo'), 'data-bs-backdrop');
		
		$("#rechazaPago").button().click(function() {
			rechazaPago();
		});

		$("#autorizaPago").button().click(function() {
			autorizaPago();
		});
		
// 		readOnlyInput();
		readOnlyTextArea();
// 		cssDisabledInput();
		cssDisabledTextArea();

		creaDialogoSeleccion();
		creaDialogoLog();
		init();

		$("#dialog-procesar").hide();

		oTableDetalle = $("#tblDetalle").dataTable({
			bScrollCollapse : true,
			bInfo : false,
			sScrollX : "100%",
			bAutoWidth : true,
			bJQueryUI : true,
			bRetrive : true,
			bDestroy : true,
			sPaginationType : "full_numbers",
			oLanguage : {
				sProcessing : "Procesando...",
				sLengthMenu : "Mostrar _MENU_ registros",
				sZeroRecords : "No hay registros a mostrar",
				sEmptyTable : "No hay datos en la tabla",
				sLoadingRecords : "Cargando...",
				sInfo : "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty : "Registro 0 al 0 de 0",
				sInfoFiltered : "(filtado de _MAX_ registros)",
				sInfoPostFix : "",
				sInfoThousands : ",",
				sSearch : "Buscar:",
				oPaginate : {
					sFirst : "Primero",
					sPrevious : "Ant.",
					sNext : "Sigte.",
					sLast : "&Uacute;ltimo"
				}
			},
			bServerSide : true,
			sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fnResumenCaja(" + $("#nFolioCaja").val() + ")",
			bJQueryUI : true,
			aaSorting : [ [ 0, "asc" ] ],
			aoColumns : [
				{
					sName : "ndocRenglon"
				},
				{
					sName : "RFC"
				},
				{
					sName : "CTAB"
				},
				{
					sName : "mImporte"
				}

			]
		});
		
		if( !mostrarResultado ){
				if( "CANCELADO" ==  $("#cEstatus").val() ){
					$("#operacionesDiv").css("display","none");
					Swal.fire("Cancelado", "El tramite ha sido cancelado." ,"error");
				}//else if( "AUTORIZADO" ==  $("#cEstatus").val() ){
// 					$("#operacionesDiv").css("display","none");
// 					alert("El tramite ha sido autorizado.");
				//}
				else{
					if( "VOBO" == tipoAutorizacion ){
						if( parseInt(  $("#nenviadosicop").val(), 10  ) == -2 ){
							$("#operacionesDiv").css("display","block");
						}else if( parseInt(  $("#nenviadosicop").val(), 10  ) > -2 ){
							Swal.fire("Usted ya ha firmado este documento.", "El tramite se encuentra en estatus " + $("#cEstatus").val(), "info" );
						}
					}else if( "AUT" == tipoAutorizacion ){
						if( parseInt(  $("#nenviadosicop").val(), 10  ) == -1 ){
							$("#operacionesDiv").css("display","block");
						}else if( parseInt(  $("#nenviadosicop").val(), 10  ) > -1 ){
							Swal.fire("Usted ya ha firmado este documento.", "El tramite se encuentra en estatus " + $("#cEstatus").val(),"info" );
						}
					}
				}
			}
		$("#motivoRechazo")[0].readOnly = false;
		$("#motivoRechazo").css("background","#FFFFFF");
	});

	function init() {
		$("#nFolioCaja").val("<%=nFolioCaja%>");
		$("#nFolioPago").val("<%=nFolioCaja%>");
		$("#cTipoPago").val("<%=cTipoPago%>");

		queryFormPost("estausAutorizacionPago_Read", {async:false});
		
		queryFormPost("resumen_caja", {
			async : false
		});
	}

	function autorizaPago() {
		$("#nFolios").val($("#nFolioCaja").val());
		$("#dlg-FIEL").dialog("open");

	}

	function rechazaPago() {
		$("#motivoRechazo").focus();
		modalRechazo.show();
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

	/**
	 * Crea dialogo que muestra el log
	 */
	function creaDialogoLog() {
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
				var msg = "Esta a punto de aceptar el proceso de la solicitud con folio: " + $("#nFolios").val();
				if( confirm(msg) ) {
					$("#tipoPagoSeleccionado").val( $("#cTipoPago").val() );
					$("#autorizaLayouts").submit();
					$("#dlg-FIEL").dialog("close");
					$.blockUI({
						message : "<h1>Espere ...</h1>"
					});
				}
			} else {
				Swal.fire("Revise", msgValidaciones, "info");
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
				Swal.fire("Capture motivo","El motivo de rechazo es requerido.","info");
				return false;
			}
			
			if( confirm("Esta seguro de rechazar la solicitud?") ){
				$.ajax({
					url : '../CancelaSolNP',
					dataType : 'json',
					type:"post",
					data : {
						"chk_caja" : $("#nFolioCaja").val(),
						"responseType" : "JSON",
						"reason":$("#motivoRechazo").val(),
						"autType":"FIEL"
					},
					async : false,
					success : function(json) {
						var exito = json.success;
						if( exito == "true" ){
						
							Swal.fire("OK","Solictud cancelada exitosamente","success");
							modalRechazo.hide();
							$("#operacionesDiv").css("display", "none");
							
						}else{
						
							var msg = json.data_1.result;
							Swal.fire("No fue posible cancelar la solicitud", " debido al error:  " +  msg , "info");
							
						}	
					},
					error : function(xhr, textStatus, errorThrown) {
						alert("Advertencia: " + xhr.responseText + "\nEstatus: "
								+ textStatus + "\n" + errorThrown);
					}
				});
				
			}else{
				$("#motivoRechazo").val("");
				modalRechazo.hide();
			}
		}	
</script>
</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form id="FormContrato" name="FormContrato">

		<input type="hidden" name="cTipoPago" id="cTipoPago" value="" />
		<input type="hidden" name="nenviadosicop" id="nenviadosicop" value="" />
		<input type="hidden" name="cEstatus" id="cEstatus" value="">
		<input type="hidden" name="nFolioPago" id="nFolioPago" value="">
		<div id="container" class="container">
			<h1>Resumen de Solicitudes No Presupuestales</h1>
			<div class= "card" id="operacionesDiv" style="display: none">
						<div class="card-body">
							<div class="row justify-content-around align-items-center">
								<div class="col-auto" >
									<input title="Rechazar Pago" type="button" class="btn btn-secondary text-align: center" value="Rechazar pago" id="rechazaPago" />
									<input title="Autorizar Pago" type="button" class="btn btn-primary text-align: center" value="Autorizar pago" id="autorizaPago" />
								</div>
							</div>
						</div>
			</div>
			<div class= "card">
				<div class="card-header">
					Datos Generales
				</div>
				<div class="card-body">
					<div class="row">
						<div class="col-2" >
							Folio:
							<input type="text" id="nFolioCaja" value="" name="nFolioCaja" size="18" class="form-control" readonly/>
						</div>
						<div class="col-6"></div>
						<div class="col-3">
							Fecha Captura:
							<input type="text" size="18" id="fcreacion" name="fcreacion" class="form-control" readonly/>
						</div>
					</div>
					<div class="row">
						<div class="col-8">
							Unidad Ejecutora:
							<input type="text" size="70" id="ur" name="ur" class="form-control" readonly/>
						</div>
						<div class="col-3">
							Fecha de Aplicación:
							<input type="text" size="18" id="faplicacion" readonly name="faplicacion" class="form-control"/>
						</div>
					</div>
					<div class="row">
						<div class="col-12">
							Concepto:
							<textarea rows=3 id="concepto" name="concepto" class="form-control" style="width: 714px; " readonly> </textarea>
						</div>
					</div>
				</div>
			</div>
			<div id="dv">
				<table id="tblDetalle" class="display" style="width: 100%" align="center">
					<thead style="width: 100%">
						<tr>
							<th>DocRenglon</th>
							<th>RFC</th>
							<th>CuentaBco</th>
							<th>Monto</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
	</form>

	<div id="dlg-FIEL" title="Seleccion de archivos">

		<form id="autorizaLayouts" name="autorizaLayouts" method="POST" action="../firmaSolicitudPago" enctype="multipart/form-data">
			<input id="rfcFirma" name="rfcFirma" type="hidden" value="<%=RFCUsuario%>">
			<input id="urlRetorno" name="urlRetorno" type="hidden" value="Generador/ResumenCajaNoPresupuestal.jsp">
			<input id="tipoPagoSeleccionado" name="tipoPagoSeleccionado" type="hidden" value="">
			<input id="loginFirma" name="loginFirma" type="hidden" value="<%=cLogin%>">
			<input id="ueFirma" name="ueFirma" type="hidden" value="<%=cUR%>">
			<input id="tipoAutorizacion" name="tipoAutorizacion" type="hidden" value="<%=tipoAutorizacion%>">
			<input id="nFolios" name="nFolios" type="hidden" value="">

			<fieldset>
				<legend>Ingrese su firma Electronica</legend>
				<table>
					<tr>
						<td align="right">Archivo *.cer</td>
						<td align="left"><input type="file" size="30" name="cerFile" id="cerFile" class="dlgFielInpt form-control"></td>
					</tr>
					<tr>
						<td align="left">Archivo *.key</td>
						<td align="left"><input type="file" size="30" name="keyFile" id="keyFile" class="dlgFielInpt form-control"></td>
					</tr>
					<tr>
						<td align="left">Password</td>
						<td align="left"><input type="password" size="30" name="passwordLlave" id="passwordLlave" class="dlgFielInpt form-control"></td>
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
	
	
<div class="modal" tabindex="-1" role="dialog" id="dlgMotivoRechazo" data-mdb-keyboard="true" data-mdb-backdrop="static">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
	    <div class="modal-header">
	        <h5 class="modal-title">Motivo de Rechazo</h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	    </div>
      	<div class="modal-body">
	      	<div class="row">
				<div class="col-11">
					<label for="motivoRechazo">Es requerido indicar el motivo del rechazo</label>
					<textarea rows="5" cols="60" id="motivoRechazo" name="motivoRechazo" class="form-control"></textarea>
				</div>
			</div>
       	</div>
	    <div class="modal-footer">
	        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
	        <button type="button" id="btnCancela" onclick="cancelaSolCaja();" class="btn btn-primary">Guardar</button>
	    </div>
    </div>
  </div>
</div>	
	
</body>
</html>
