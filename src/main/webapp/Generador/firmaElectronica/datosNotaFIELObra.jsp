<div class="modal fade  bd-example-modal-lg" id="dlgDatosNotaAutEst" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
	<div class="modal-dialog  modal-lg">
		<div class="modal-content">
	  		<div class="modal-header">
	    		<h5 class="modal-title" id="modalNotaAutEst">Solicita Autorizaci&oacute;n.</h5>
	    		<input type="button" id="closeDatosNotaAutEst" name="closeDatosNotaAutEst" class="btn-close" data-bs-dismiss="modal" aria-label="Close"/>
			</div>
			<div class="modal-body">
	      		<div class="form-group" >
					<div class="row" id="firmante">
						<div class="col">
			  				<fieldset>
								<legend>Selecci&oacute;n de Firmante</legend>
								<div class="row" >
									<div class="col">
										<label>Seleccione el firmante y capture el folio de la
												atenta nota para solicitar la autorizaci&oacute;n de esta
												estimaci&oacute;n de obra y su posterior pago.
										</label>
									</div>
								</div>
								<div class="row" >
									<div class="col">
										<label for="folioNota" >Folio de Atenta Nota:</label>
										<input type="text" id="folioNota" name="folioNota" class="form-control"> 
									</div>
								</div>
								<div class="row" >
									<div class="col">
										<label for="firmanteNota" >Firmante <u>(Residente de Obra)</u>:</label>
										<select id="firmanteNota" name="firmanteNota" class="custom-select">
											<option value="">Seleccione Firmante (Residente de Obra)</option>
										</select>
									</div>
								</div>
								<div class="row" >
									<div class="col">
										<label for="firmanteNotaJefe" >Firmante <u>(Jefe de Obra)</u> :</label>
										<select id="firmanteNotaJefe" name="firmanteNotaJefe" class="custom-select">
											<option value="">Seleccione Firmante (Jefe de Obra)</option>
										</select>
									</div>
								</div>
								<div class="row" >
									<div class="col">
										<label for="firmanteNotaSubgerente" >Firmante <u>(Subgerente de Obra)</u> :</label>
										<select id="firmanteNotaSubgerente" name="firmanteNotaSubgerente" class="custom-select">
											<option value="">Seleccione Firmante (Subgerente de Obra)</option>
										</select>
									</div>
								</div>
							</fieldset>
			  			</div>
					</div>
					<div class="row" id="infAdicionalDiv">
						<div class="col">
			  				<fieldset>
								<legend>Informacion Adicional</legend>
								<div class="row" >
									<div class="col">
										<p>
											En relaci&oacute;n con el contrato <b><span id="numeroContrato"></span></b>
											<span id="convenios"> </span>&nbsp;formalizado con la empresa <b><span
												id="beneficiarioNombre"></span></b>
							
										</p>
									</div>
								</div>
								<div class="row" id="tblInformacionNota">
									<div class="col">
										<textarea rows="2" cols="75" id="dMotivoNota" class="form-control"></textarea>
									</div>
								</div>
							</fieldset>
			  			</div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" id="btnEnviarNotaAutEst" name="btnEnviarNotaAutEst" onclick="enviaSolAutorizacion()">Enviar</button>
	     		<button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="btnCancelNotaAutEst" name="btnCancelNotaAutEst" >Cancelar</button>
			</div>
		</div>
	</div>
</div>
<input type="hidden" id="nEstimacionAut" name="nEstimacionAut" value="-1">
<input type="hidden" id="beneficiario" value="">
<script>
	var myModalDatosNotaAutEst;
	$(document).ready(function() {
		myModalDatosNotaAutEst = new bootstrap.Modal(document.getElementById('dlgDatosNotaAutEst'), {
			  keyboard: false
		});
		
		querySelectPost("catFirmantesReadObra", "firmanteNota", {async : false,
			callback : function() 
			{
				$('#firmanteNota').find('option').clone().appendTo('#firmanteNotaJefe');
				$('#firmanteNota').find('option').clone().appendTo('#firmanteNotaSubgerente');
				$("#firmanteNota").val(0);
				$("#firmanteNotaJefe").val(0);
				$("#firmanteNotaSubgerente").val(0);
			}
		});
		

	});
	function validaInformacion() {
		if ($("#firmanteNota").val() == "" || $("#firmanteNota").val() == "0") {
			swal("Seleccione el firmante Residente de Obra. Si no existe, debe darse de alta.",{icon:"info",button: "Cerrar"});
			$("#firmanteNota").focus();
			return false;
		}
		if ($("#firmanteNotaJefe").val() == "" || $("#firmanteNotaJefe").val() == "0") {
			swal("Seleccione el firmante Jefe de Obra. Si no existe, debe darse de alta.",{icon:"info",button: "Cerrar"});
			$("#firmanteNotaJefe").focus();
			return false;
		}
		if ($("#firmanteNotaSubgerente").val() == "" || $("#firmanteNotaSubgerente").val() == "0") {
			swal("Seleccione el firmante Subgerente de Obra. Si no existe, debe darse de alta.",{icon:"info",button: "Cerrar"});
			$("#firmanteNotaSubgerente").focus();
			return false;
		}
		if ($.trim($("#dMotivoNota").val()) == "") {
			swal("Ingrese el detalle del servicio o bienes recibidos.",{icon:"info",button: "Cerrar"});
			$("#dMotivoNota").focus();
			return false;
		}

		if ($.trim($("#folioNota").val()) == "") {
			swal("Ingrese el folio de atenta nota.",{icon:"info",button: "Cerrar"});
			$("#folioNota").focus();
			return false;
		}
		return true;

	}
	function enableInputs() {
		$('#firmanteNota').prop('disabled', false);
		$('#firmanteNotaJefe').prop('disabled', false);
		$('#firmanteNotaSubgerente').prop('disabled', false);
		$('#dMotivoNota').prop('readonly', false);
		$('#folioNota').prop('readonly', false);
		$("#folioNota").css("background-color", "#FFFFFF");
		$("#dMotivoNota").css("background-color", "#FFFFFF");
		
		$("#btnEnviarNotaAutEst").show();
		$("#btnCancelNotaAutEst").show();
		$("#closeDatosNotaAutEst").show();
		
		$("#numeroContrato").text($("#NoCntSolicitudPago").val());
		$("#beneficiarioNombre").text(" " + $("#beneficiarioSolicitudPagos").val());
		
	}
	function enviaSolAutorizacion(){
		if (validaInformacion()) {
			swal({
				title: "Esta seguro de enviar la estimación de obra a autorización?",
				text: "",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
					},
				}).then((continuar) => {
					if (!continuar) {
						return;
				}else{
					$.ajax({
						url : '../FirmaAutEstimacionObraServlet',
						dataType : 'json',
						type : "POST",
						beforeSend : function() {
							$.blockUI({
								message : 'Enviando, espere ...'
							});
						},
						data : {
							"cIDContrato" : $("#NoCntSolicitudPago").val(),
							"nEstimacionAut" : $("#nEstimacionAut").val(),
							"dMotivoNota" : $("#dMotivoNota").val(),
							"cFolioNota" : $("#folioNota").val(),
							"cNumeroEmpleado" : $("#firmanteNota").val(),
							"cNumeroEmpleadoJefe" : $("#firmanteNotaJefe").val(),
							"cNumeroEmpleadoSubgerente" : $("#firmanteNotaSubgerente").val(),
							"folioSAI" : $("#FOLIO").val()
						},
						async : true,
						success : function(objResp) {
							var success = objResp.success;
							if( success == true ){
								swal({
									title: "",
									text: "Solicitud de autorización registrada exitosamente!",
									icon: "info",
									buttons: {
										confirm : "Cerrar"
										},
									}).then((continuar) => {
										$.unblockUI();
										//$("#dlgDatosNotaAutEst").dialog("close");
										myModalDatosNotaAutEst.hide();
										muestraTabla();
										
									});
								
							}else{
								swal(objResp.message,{icon:"info",button: "Cerrar"});
								swal({
									title: "",
									text: objResp.message,
									icon: "info",
									buttons: {
										confirm : "Cerrar"
									},
									}).then((continuar) => {
										$.unblockUI();
										//$("#dlgDatosNotaAutEst").dialog("close");
										myModalDatosNotaAutEst.hide();
									});
							}
							
						},
						error : function(xhr, textStatus, errorThrown) {
							try {
								var obj = eval(xhr.responseText);
								var msg = obj.errCause;
								swal("No fue posible registrar la recepcion debido al error: "+ msg,{icon:"info",button: "Cerrar"});
							} catch (e) {
								swal("Advertencia: " + xhr.responseText
										+ "\nEstatus: " + textStatus + "\n"
										+ errorThrown,{icon:"error",button: "Cerrar"});
							}
							$.unblockUI();
							//$("#dlgDatosNotaAutEst").dialog("close");
							myModalDatosNotaAutEst.hide();
						}
					});
				}
			});
		}
	}
</script>