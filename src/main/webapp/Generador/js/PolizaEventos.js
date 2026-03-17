/**
 * 
 */
function regresaRevision() {
	var done = false;
	try {
		$("#Status").val(2);
		$("#resp").val("REVISION_POLIZA");
		$("#usuario").val("");

		queryFormPost("ActualizaCOPERPolizaUpdate2,tdocPolizaUsuarioAutorizacion", //ActualizaCDATOPolizaUpdate
			{
				async : false,
				callback : function() {
					alert("Documento regresado a estatus de Revisión correctamente");
					document.liberardocumento.submit();
					done = true;
					return;
				}
			});

		if (!done) {
			alert("Ocurrio un error actualizando operaciones. Intente nuevamente, si persiste el propblema notifique al administrador ");
			return;
		}
	} catch (ex) {
		alert("Error 0029js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
	}
}

function avanzaConsulta() {
	try {
		$("#Status").val(4);
		$("#resp").val("CONSULTA_POLIZA");
		$("#txtObservaciones").text("");

		queryFormPost("ActualizaCOPERPolizaUpdate2,tdocPolizaUsuarioAutorizacion",
			{
				async : false,
				callback : function() {
					comprobacionPoliza();
					done = true;
					document.liberardocumento.submit();

				}
			});

	} catch (ex) {
		alert("Error 0028js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
	}
}

function comprobacionPoliza() {
	try {
		queryFormPost("ObtenerNumeroPolizaManualRead", {
			async : false
		});
		alert("Documento aplicado con el número de Póliza: "
			+ $("#nFolioPoliza").val() + "\rCentro Contable: "
			+ $("#cCentroContable").val() + "\rTipo de Póliza:  "
			+ $("#cTipoPoliza").val());

		$("#tOperacion").val("Documento guardado con numero de Poliza: " + $("#nFolioPoliza").val());
		queryFormPost("tDocPolizaBitacora", {
			async : false
		});
	} catch (ex) {
		alert("Error 0033js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
	}
}