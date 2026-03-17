<div id="dlgDatosNotaAlmacenVirt" title="Solicita firma para los bienes que no se recepcionan en el almacén." style="display: none;">
	<div id="firmanteSelFirmanteAlmacenVirt">
		<fieldset>
			<legend>Selecci&oacute;n de Firmante</legend>
			<table>
				<tr>
					<td align="left" colspan="2">Seleccione el firmante y capture el folio de la
						atenta nota para solicitar la autorizaci&oacute;n de esta
						recepci&oacute;n de material y su posterior pago.</td>
				</tr>
				<tr>
					<td align="right">Folio de Atenta Nota:</td>
					<td align="left"><input type="text" id="folioNotaAlmacenVirt" size="20"> </td>
				</tr>
				<tr>
					<td align="right">Firmante:</td>
					<td align="left">
					<input type="text" class="AyudaSyC obligatorio desahabilitado" maxlength="40" size="50" name="ServidoresPublicos" id="ServidoresPublicos" readonly />
					</td>
				</tr>
				
			</table>
		</fieldset>
	</div>
	<div id="infAdicionalDivAlmacenVirt">
		<fieldset>
			<legend>Informacion Adicional.</legend>
			<p>
				En relaci&oacute;n con el contrato <b><span id="numeroContratoAntentaNotaAlmacenVirt"></span></b>
				<span id="conveniosAlmacenVirt"> </span>&nbsp; formalizado con la empresa <b><span
					id="beneficiarioNombreAntentaNotaAlmacenVirt"></span></b>

			</p>
			<table id="tblInformacionNota">
				<tr>
					<td><textarea rows="5" cols="75" id="dMotivoNotaAtentanotaAlmacenVirt"></textarea></td>
				</tr>
			</table>
			 
		</fieldset>
	</div>
</div>
<script>
	$(document).ready(function() {
		clearInputs();
		$("#dlgDatosNotaAlmacenVirt").dialog({
			autoOpen : false,
			height : 440,
			width : 660,
			modal : true,
			open : function() {
				informacionPreviaAlmVirt();
				$("#folioNotaAlmacenVirt").focus();
			},
			buttons : {
				"Aceptar" : function() {
					enviaSolAutorizacionAlmacenVirt();
				}
			}
		});
		
	});
	function clearInputs(){
		$("#dMotivoNotaAtentanotaAlmacenVirt").val('');
		$("#ServidoresPublicos").val('');
		$("#puestoEmpleado").val('');
		$("#folioNotaAlmacenVirt").val('');
		
	}
	function informacionPreviaAlmVirt() {
		queryFormPost({
			queryName : "numContratoCNETRead",
			async : false,
			callback : function() {
				$("#numeroContratoAntentaNotaAlmacenVirt").text($("#contratoCompranet").val());
				$("#beneficiarioNombreAntentaNotaAlmacenVirt").text(" " + $("#beneficiario").val());
			}
		});

	}

	function validaInformacionAlmVirt() {

		if ($("#numEmpleado").val() == "" || $("#numEmpleado").val() == "0") {
			swal("Seleccione el firmante. Si no existe, debe darse de alta.",{icon:"info",button: "Cerrar"});
			$("#numEmpleado").focus();
			return false;
		}

		if ($.trim($("#dMotivoNotaAtentanotaAlmacenVirt").val()) == "") {
			swal("Ingrese el detalle del servicio o bienes recibidos.",{icon:"info",button: "Cerrar"});
			$("#dMotivoNotaAtentanotaAlmacenVirt").focus();
			return false;
		}

		if ($.trim($("#folioNotaAlmacenVirt").val()) == "") {
			swal("Ingrese el folio de atenta nota.",{icon:"info",button: "Cerrar"});
			$("#folioNotaAlmacenVirt").focus();
			return false;
		}
		return true;
	}

	function enviaSolAutorizacionAlmacenVirt() {
		if (validaInformacionAlmVirt()) {
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
							"cIDRecepMat" : $("#textcidRecepcion").val(),
							"dMotivoNota" : $("#dMotivoNotaAtentanotaAlmacenVirt").val(),
							"cFolioNota" : $("#folioNotaAlmacenVirt").val(),
							"cNumeroEmpleado" : $("#numEmpleado").val(),
							"nIdEntraAlmacen" : $("#esAlmacenCentral").val(),
							"requiereAtentaNota" : 1,
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
	
	function capturaNotaAlmacenVirtual(){
		$("#dlgDatosNotaAlmacenVirt").dialog("open");
	}
</script>