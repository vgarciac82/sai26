<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

	String action = request.getParameter("a");
	String cTipoPago = request.getParameter("d");
	int nServicioEnteraSatisfaccion = Integer.parseInt(request.getParameter("f"));

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
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Firma Electroniva del proceso entera satisfacción</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  
  	<style type="text/css" title="currentStyle"> 
 		@import "../Generador/css/demo_page.css";
		@import "../Generador/css/demo_table_jui.css"; 
		@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/interfaz.css";
	</style>
	
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
	<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>
	
	
	<script src="https://code.jquery.com/jquery-3.5.0.js"></script>
	<link rel="stylesheet" type="text/css" href="../Bootstrap/Bootstrap502/css/bootstrap.css"/>
	<script type="text/javascript" src="../Bootstrap/Bootstrap502/js/bootstrap.js"></script>
	<script type="text/javascript" src="../Bootstrap/Bootstrap502/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>
	
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../Generador/js/funciones.js"></script>
	<link href="../css/reportesGRM.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" charset="utf-8">
	var cUR = "<%=cUR%>";
	var cLogin = "<%=cLogin%>";
	var RFC = "<%=RFCUsuario%>";
	var numeroEmpleado = "<%=numeroEmpleado%>";
	var mostrarResultado = <%=mostrarResultado%>;
	var mensaje = "<%=msg%>";
	var myModalFIEL;
	var myModalMotivoRechazo
	$(document).ready(function() {
		$("#nServicioEnteraSatisfaccion").val("<%=nServicioEnteraSatisfaccion%>");
		$(".custom-file-input").on("change", function() {
			var fileName = $(this).val().split("\\").pop();
  		  	$(this).siblings(".custom-file-label").addClass("selected").html(fileName);
  		});
		creaDialogoLog();
		init();
		showOperationsDIV();

		$("#motivoRechazo")[0].readOnly = false;
		$("#motivoRechazo").css("background","#FFFFFF");
		showTrActaHechos();
	});//Fin del document ready
	function showOperationsDIV(){
		if( !mostrarResultado ){
			$("#operacionesDivTestigos").css("display", "none");
			$("#operacionesDiv").css("display","none");
			
			if("3" ==  $("#nIdEstatus").val()){
				$("#operacionesDiv").css("display", "block");
			}else if("4" ==  $("#nIdEstatus").val() || "5" ==  $("#nIdEstatus").val()){
				if(("4" ==  $("#nIdEstatus").val() && cLogin==  $("#cLoginTestigo1").val()) || ("5" ==  $("#nIdEstatus").val() && cLogin ==  $("#cLoginTestigo2").val() ) ){
					$("#operacionesDivTestigos").css("display", "block");
				}else{
					swal("Usted ya ha revisado este tramite. Estatus: " + $("#estatus").val() ,{icon:"warning",button: "Cerrar"});
				}
			}else{
				swal("Usted ya ha revisado este tramite. Estatus: " + $("#estatus").val() ,{icon:"warning",button: "Cerrar"});
			}
		}
	}
	function init() {
		
		$("#nServicioEnteraSatisfaccion").val("<%=nServicioEnteraSatisfaccion%>");
		$("#cTipoPago").val("<%=cTipoPago%>");
		queryFormPost("informacionENSAFirmaRead", {async : false});
		
	}
	function autorizarReporte() {
		clearInputsFIEL();
		$("#nFolios").val($("#nServicioEnteraSatisfaccion").val());
		myModalFIEL = new bootstrap.Modal(document.getElementById('modalFIEL'), {
			keyboard: false
		})
		myModalFIEL.show();
	}

	function rechazarReporte() {
		$("#motivoRechazo").val("");
		$("#folios").val($("#nServicioEnteraSatisfaccion").val());
		myModalMotivoRechazo = new bootstrap.Modal(document.getElementById('dlgMotivoRechazo'), {
			keyboard: false
		})
		myModalMotivoRechazo.show();
	}
	function showTrActaHechos(){
		$("#trActaHechos").hide();
		if(2==$("#nServicioPrestado").val()){
			$("#trActaHechos").show();
		}
	}
	/**
	 * Crea dialogo que muestra el log
	 */
	function creaDialogoLog() {
		if(mostrarResultado)
			$("#dlg-Msg").show();
		else 
			$("#dlg-Msg").hide();
						
	}
	
	function clearInputsFIEL() {
		$("#nFolios").val("");
		$("#keyFile").val("");
		$("#keyFile").siblings(".custom-file-label").addClass("selected").html("Adjuntar Archivo *.key");
		$("#cerFile").val("");
		$("#cerFile").siblings(".custom-file-label").addClass("selected").html("Adjuntar Archivo *.cer");
		$("#passwordLlave").val("");
	}
	function aceptarDlg() {
			var msgValidaciones = validaCamposCompletos();
			if( "" == msgValidaciones ) {
				var msg = "Esta a punto de aceptar el procesode entera satisfacción con folio: " + $("#cFolio").val();
				swal({
					title: "",
					text: msg,
					icon: "info",
					buttons: {
						confirm : "Aceptar",
						cancel: "Cancelar"
					},
				}).then((continuar) => {
					if (!continuar) {
						return;
					}else{
						$.blockUI({message: "Procesando espere ......"});
						$("#tipoPagoSeleccionado").val( $("#cTipoPago").val() );
						$("#firmaAnexo1A").submit();
						myModalFIEL.hide();
					}
				});
			} else {
				swal(msgValidaciones,{icon:"warning",button: "Cerrar"});
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
		
		function rechazaFolio(){
		
			if( $("#motivoRechazo").val() == "" ){
				swal("El motivo de rechazo es requerido.",{icon:"warning",button: "Cerrar"});
				return false;
			}
			swal({
				title: "",
				text: "¿Está seguro de rechazar el proceso?",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
				},
			}).then((continuar) => {
				if (!continuar) {
					$("#motivoRechazo").val("");
					myModalMotivoRechazo.hide();
					$("#operacionesDiv").show();
					return;
				}else{
					$.blockUI({message: "Procesando espere ......"});
					$("#tipoPagoSeleccionado").val( $("#cTipoPago").val() );
					$("#folios").val($("#nServicioEnteraSatisfaccion").val());
					$("#operacionesDiv").css("display", "none");
					$("#formRechazo").attr("action", "../firma/rechazaENSA");
					$("#formRechazo").submit();
				}
			});
		}	
		function  avanzarProceso(){
			swal({
				title: "",
				text: "¿Está seguro de aceptar el proceso?",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
				},
			}).then((continuar) => {
				if (!continuar) {
					showOperationsDIV();
					return;
				}else{
					$.blockUI({message: "Procesando espere ......"});
					$("#folios").val($("#nServicioEnteraSatisfaccion").val());
					$("#nAvanzaProceso").val(1);
					$("#operacionesDivTestigos").css("display", "none");
					$("#formRechazo").attr("action", "../firma/avanzaProcesoENSA");
					$("#formRechazo").submit();
				}
			});
		}
</script>
</head>
<body>
	<div class="container-fluid">
		<fieldset class="form-group border p-3">
	 		<legend class="w-auto px-2">Solicitud de Firma al Proceso de Entera Satisfacci&oacute;n</legend>
			<div class="col-md-12 col-lg-12 col-sm-12">
				<div class="row">
					<form id="frmEnteraSatisfaccion" name="frmEnteraSatisfaccion" method="post">
						<input type="hidden" name="nIdEstatus" id="nIdEstatus" value="" />
						<input type="hidden" name="estatus" id="estatus" value="" />
						<input type="hidden" name="firmado" id="firmado" value="" /> 
						<input type="hidden" name="nServicioEnteraSatisfaccion" id="nServicioEnteraSatisfaccion" value="">
						<input type="hidden" name="nServicioPrestado" id="nServicioPrestado" value="" />
						<input type="hidden" name="cLoginTestigo1" id="cLoginTestigo1" value="" />
						<input type="hidden" name="cLoginTestigo2" id="cLoginTestigo2" value="" />
						
						
						<div class="form-group">
							<div class="form-group row" id="dlg-Msg" style="display: none;">
								<%if(fielExpiringSoon){ %>
								<div class="row" id="fielWarning">
									<fieldset class="form-group border p-3">
		 								<legend class="w-auto px-2"> Firma a punto de Expirar</legend>
		 								<div class="input-group"><p id="msgWarning" style="font-weight: bold;"><%=fielMsg%></p></div>
		 							</fieldset>
								</div>
								<%} %>
								<div class="row">
									<fieldset class="form-group border p-3">
		 								<legend class="w-auto px-2">Resultado de la operaci&oacute;n</legend>
		 								<table id="mnLogTbl" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
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
						</div>
						<div class="form-group" id="generales">
							<div class="container">
			 					<div class="form-group row">
			 						<div class="col-auto">
				 						<label for="cidcontratodefinitivo">Contrato SAI</label>
										<input type="text" class="form-control" aria-describedby="basic-addon1"  id="cidcontratodefinitivo" name="cidcontratodefinitivo" readonly="readonly">
									</div>
									<div class="col-auto">
				 						<label for="beneficiario">Proveedor</label>
										<input type="text" class="form-control"  aria-describedby="basic-addon1"  id="beneficiario" name="beneficiario" readonly="readonly">
									</div>
									<div class="col-auto">
				 						<label for="cnocontratocnet">Compranet</label>
										<input type="text" class="form-control"  aria-describedby="basic-addon1"  id="cnocontratocnet" name="cnocontratocnet" readonly="readonly">
									</div>
			 					</div>
			 					<div class="form-group row">
			 						<div class="col-auto">
				 						<label for="cConceptoContrato">Concepto del contrato</label>
										<textarea class="form-control" rows="4" cols="90" readonly="readonly" id="cConceptoContrato"></textarea>
									</div>
			 					</div>
			 				</div>
		 				</div>
		 				<div class="form-group" id="nota">
							<div class="container">
								<div class="form-group row">
			 						<div class="col-auto">
				 						<label for="cFolio">Folio</label>
										<input type="text" class="form-control" aria-describedby="basic-addon1"  id="cFolio" name="cFolio" readonly="readonly">
									</div>
									<div class="col-auto">
				 						<label for="firmanteResponsable">Firmante</label>
										<input type="text" class="form-control"  aria-describedby="basic-addon1"  id="firmanteResponsable" name="firmanteResponsable" readonly="readonly">
									</div>
									<div class="col-auto">
				 						<label for="cMesPago">Mes de pago</label>
										<input type="text" class="form-control"  aria-describedby="basic-addon1"  id="cMesPago" name="cMesPago" readonly="readonly">
									</div>
									<div class="col-auto">
				 						<label for="cServicioPrestado">¿Servicio entregado a entera satisfacci&oacute;n?</label>
										<input type="text" class="form-control"  aria-describedby="basic-addon1"  id="cServicioPrestado" name="cServicioPrestado" readonly="readonly">
									</div>
			 					</div>
							</div>
						</div>
						<div class="form-group" id="operacionesDiv" style="display: none">
							<div class="container">
								<div class="form-group row">
									<div class="col-auto">
										<button type="button" class="btn btn-danger" title="Rechazar Reporte" type="button" id="rechazaReporte" onclick="rechazarReporte();">Rechazar Proceso</button>
									</div>
									<div class="col-auto">
										<button type="button" class="btn btn-primary" title="Autorizar Reporte" id="autorizaReporte" onclick="autorizarReporte();">Aceptar Proceso</button>
									</div>
								</div>
							</div>
						</div>
						<div class="form-group" id="operacionesDivTestigos" style="display: none">
							<div class="container">
								<div class="form-group row">
									<div class="col-auto">
										<button type="button" class="btn btn-primary" title="Aceptar Tramite" id="aceptarReporte" onclick="avanzarProceso();">Aceptar Tramite</button>
									</div>
								</div>
							</div>
						</div>
					</form>
				</div>
<!-- 				 Aqui me quede -->
				<div class="modal fade bd-example-modal-lg" id="modalFIEL" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true">
					<div class="modal-dialog modal-lg">
						<div class="modal-content">
							<form id="firmaAnexo1A" name="firmaAnexo1A" method="POST" action="../firma/AutorizaReporte" enctype="multipart/form-data">
								<input id="rfcFirma" name="rfcFirma" type="hidden" value="<%=RFCUsuario%>"> 
								<input id="urlRetorno" name="urlRetorno" type="hidden" value="../FIEL/ResumenENSAFIEL.jsp">
								<input id="tipoPagoSeleccionado" name="tipoPagoSeleccionado" type="hidden" value=""> 
								<input id="loginFirma" name="loginFirma" type="hidden" value="<%=cLogin%>"> 
								<input id="ueFirma" name="ueFirma" type="hidden" value="<%=cUR%>"> 
								<input id="nFolios" name="nFolios" type="hidden" value="">
								
								<div class="modal-header">
									<h5 class="modal-title">Ingrese su Firma Electr&oacute;nica "FIEL"</h5>
									<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
					      		</div>
					      		<div class="modal-body">
					        		<div class="form-group">
					        			<div class="container-fluid"">
											<div class="form-group row">
												<div class="col-sm-9">
													<div class="input-group">
													  <div class="custom-file">
													    <input type="file" class="custom-file-input" id="cerFile" name="cerFile" aria-describedby="inputGroupFileAddon01">
													    <label class="custom-file-label" for="cerFile">Adjuntar Archivo *.cer</label>
													  </div>
													</div>
												</div>
											</div>
											<div class="form-group row">
												<div class="col-sm-9">
													<div class="input-group">
													  <div class="custom-file">
													    <input type="file" class="custom-file-input" id="keyFile" name="keyFile" aria-describedby="inputGroupFileAddon01">
													    <label class="custom-file-label" for="keyFile">Adjuntar Archivo *.key</label>
													  </div>
													</div>
												</div>
											</div>
											<div class="form-group row">
						 						<div class="col-sm-9">
							 						<label for="passwordLlave">Password</label>
													<input type="password" class="form-control" placeholder="***************"   id="passwordLlave" name="passwordLlave">
												</div> 
						 					</div>
					 					</div>
					 				</div>
					      		</div>
					      		<div class="modal-footer">
					      			<button type="button" class="btn btn-primary" id="AceptarFIEL" onclick="aceptarDlg()">Firmar</button>
					      			<button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="CancelarFIEL" >Cancelar</button>
					      		</div>
				      		</form>
				    	</div>
				  	</div>
				</div>
				<div class="modal fade bd-example-modal-lg" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" id="dlgMotivoRechazo" >
					<div class="modal-dialog modal-lg">
						<div class="modal-content">
							<form id="formRechazo" name="formRechazo" method="POST" >
								<input type="hidden" name="cTipoPago" id="cTipoPago" value="" />
								<input type="hidden" name="folios" id="folios" value="" />
								<input type="hidden" name="nAvanzaProceso" id="nAvanzaProceso" value="0" />
								<input type="hidden" name="url_Retorno" id="url_Retorno" value="../FIEL/ResumenENSAFIEL.jsp">
								<div class="modal-header">
									<h5 class="modal-title">Motivo de Rechazo</h5>
									<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
					      		</div>
					      		<div class="modal-body">
					        		<div class="form-group">
					        			<div class="container-fluid"">
											<div class="form-group row">
						 						<div class="col-sm-9">
							 						<label for="motivoRechazo">Es requerido indicar el motivo del rechazo</label>
													<textarea class="form-control" rows="5" cols="65" id="motivoRechazo" name="motivoRechazo"></textarea>
												</div> 
						 					</div>
					 					</div>
					 				</div>
					      		</div>
					      		<div class="modal-footer">
					      			<button type="button" class="btn btn-primary" id="RechazarBtn" onclick="rechazaFolio();">Aceptar</button>
					      			<button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="CancelarBtn">Cancelar</button>
					      		</div>
				      		</form>
				    	</div>
				  	</div>
				</div>
				
				<div class="row" id="archivo">
					<table class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
						<tr>
							<td align="left"><b>Archivo(s) a Firmar:</b>
						</tr>
						<tr>
							<td>
								<iframe id="tbl-resp-oper" src="../rm/MuestraAnexo1A?nDocto=1&documento=<%=cTipoPago%>&f=<%=nServicioEnteraSatisfaccion%>" scrolling="auto" width="100%" height="400px" frameborder="0" marginheight="0" marginwidth="0" style="padding: 0px;">
								</iframe>
							</td>
						</tr>
						<tr id="trActaHechos" style="display: none;">
							<td>
								<iframe id="tbl-resp-oper" src="../rm/MuestraAnexo1A?nDocto=2&documento=<%=cTipoPago%>&f=<%=nServicioEnteraSatisfaccion%>" scrolling="auto" width="100%" height="400px" frameborder="0" marginheight="0" marginwidth="0" style="padding: 0px;">
								</iframe>
							</td>
						</tr>
					</table>
				</div>
			</div>
		</fieldset>
	</div>
</body>
</html>