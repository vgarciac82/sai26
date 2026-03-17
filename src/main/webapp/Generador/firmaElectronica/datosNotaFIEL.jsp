<div id="dlgDatosNota" title="Solicita Autorizacion." style="display: none;">
	<div id="firmanteSelDiv">
		<fieldset>
			<legend>Seleccion de Firmante</legend>
			<table>
				<tr>
					<td align="left" colspan="2">Seleccione el firmante y capture el folio de la
						atenta nota para solicitar la autorizaci&oacute;n de esta
						recepcion de material y su posterior pago.</td>
				</tr>
				<tr>
					<td align="right">Folio de Atenta Nota:</td>
					<td align="left"><input type="text" id="folioNota" size="20"> </td>
				</tr>
				<tr>
					<td align="right">Firmante:</td>
					<td align="left"><select id="firmanteNota" name="firmanteNota"
						style="width: 400px;">
							<option value="">Seleccione Firmante</option>
					</select></td>
				</tr>
			</table>
		</fieldset>
	</div>
	<div id="infAdicionalDiv">
		<fieldset>
			<legend>Informacion Adicional.</legend>
			<p>
				En relaci&oacute;n con el contrato <b><span id="numeroContrato"></span></b>
				<span id="convenios"> </span>&nbsp; formalizado con la empresa <b><span
					id="beneficiarioNombre"></span></b>

			</p>
			<table id="tblInformacionNota">
				<tr>
					<td><textarea rows="5" cols="75" id="dMotivoNota"></textarea></td>
				</tr>
			</table>
			 
		</fieldset>
	</div>
</div>		
<script>
	$(document).ready(function() {
		$("#dlgDatosNota").dialog({
			autoOpen : false,
			height : 440,
			width : 660,
			modal : true,
			open : function() {
				informacionPrevia();
				$("#folioNota").focus();
			},
			buttons : {
				"Aceptar" : function() {
					enviaSolAutorizacion();
				}
			}
		});

		querySelectPost("catFirmantesRead", "firmanteNota", {
			async : false
		});

	});

	function informacionPrevia() {
		queryFormPost({
			queryName : "numContratoCNETRead",
			async : false,
			callback : function() {
				$("#numeroContrato").text($("#contratoCompranet").val());
				$("#beneficiarioNombre").text(" " + $("#beneficiario").val());
			}
		});

	}

	function validaInformacion() {

		if ($("#firmanteNota").val() == "" || $("#firmanteNota").val() == "0") {
			swal("Seleccione el firmante. Si no existe, debe darse de alta.",{icon:"info",button: "Cerrar"});
			$("#firmanteNota").focus();
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

	function enviaSolAutorizacion() {
		if (validaInformacion()) {
			swal({
				title: "Esta seguro de enviar la recepción de material a autorización?",
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
						url : '../../fiel/solicitaAutRM',
						dataType : 'json',
						type : "POST",
						beforeSend : function() {
							$.blockUI({
								message : 'Enviando, espere ...'
							});
						},
						data : {
							"cIDContrato" : $("#contratoCompranet").val(),
							"requiereAtentaNota" : 1,
							"cIDRecepMat" : $("#textcidRecepcion").val(),
							"dMotivoNota" : $("#dMotivoNota").val(),
							"cFolioNota" : $("#folioNota").val(),
							"cNumeroEmpleado" : $("#firmanteNota").val(),
							"nIdEntraAlmacen" : $("#esAlmacenCentral").val(),
							"nIdEstatusAtentaNotaFirmada" : $("#nIdEstatusAtentaNotaFirmada").val(),
							"cIdContratoDefinitivo" : $("#pedidoContratoCompromiso").val()
						},
						async : true,
						success : function(objResp) {
							
							var success = objResp.success;
							
							if( success == true ){
								swal("Solicitud de autorización registrada exitosamente!",{icon:"info",button: "Cerrar"});
								window.location = "RecepcionMaterial.jsp?tab=1";
								$("#dlgDatosNota").dialog("close");
								
							}else{
								swal(objResp.message,{icon:"info",button: "Cerrar"});
							}
							
							$.unblockUI();
							
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
						}
					});
				}
			});
		}
	}
	
	function capturaNota(){
		$("#dlgDatosNota").dialog("open");
	}
</script>