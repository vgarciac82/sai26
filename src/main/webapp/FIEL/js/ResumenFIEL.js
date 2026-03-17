

function initFiel() {
	const dlgFielElement = document.getElementById("dlg-FIEL");
	if (dlgFielElement) {
		modalFielDlg = new bootstrap.Modal(dlgFielElement, {
			backdrop: "static",
			keyboard: true
		});
	}

	queryFormPost({
		queryName: "estausAutorizacionPago_Read",
		async: false,
		callback: function() {

			if ("VOBO" == tipoAutorizacion || "R_VOBO" == tipoAutorizacion) {
				if (parseInt($("#nenviadosicop").val(), 10) == -2 || parseInt($("#nenviadosicop").val(), 10) == -7) {
					$("#operacionesDiv").css("display", "block");
				} else if (parseInt($("#nenviadosicop").val(), 10) > -2 || parseInt($("#nenviadosicop").val(), 10) != -7) {
					Swal.fire({
						icon: 'warning',
						title: 'Advertencia',
						text: 'Usted ya ha firmado este documento.',
					});
				} else if (parseInt($("#nenviadosicop").val(), 10) != -2 || parseInt($("#nenviadosicop").val(), 10) != -7) {
					Swal.fire({
						icon: 'info',
						title: 'Información',
						text: "El tramite se encuentra en estatus " + $("#cEstatus").val(),
					});
				}
			} else if ("AUT" == tipoAutorizacion || "R_AUT" == tipoAutorizacion) {
				if (parseInt($("#nenviadosicop").val(), 10) == -1 || parseInt($("#nenviadosicop").val(), 10) == -6) {
					$("#operacionesDiv").css("display", "block");
				} else if (parseInt($("#nenviadosicop").val(), 10) > -1 || parseInt($("#nenviadosicop").val(), 10) != -6) {
					Swal.fire({
						icon: 'warning',
						title: 'Advertencia',
						text: 'Usted ya ha firmado este documento.',
					});
				} else if (parseInt($("#nenviadosicop").val(), 10) != -1 || parseInt($("#nenviadosicop").val(), 10) != -6) {
					Swal.fire({
						icon: 'info',
						title: 'Información',
						text: "El tramite se encuentra en estatus " + $("#cEstatus").val(),
					});
				}
			}
		}
	});



	// 2. Botones principales de operación
	$("#rechazaPago").on("click", function() {
		rechazaPago();
	});

	$("#autorizaPago").on("click", function() {
		autorizaPago();
	});

	// 3. Botones del modal
	$("#bntAceptar").on("click", function(e) {
		e.preventDefault();
	 	var $btn = $(this);
		$btn.prop('disabled', true);
		aceptarDlg();
	});

	$("#btnCerrarDlg").on("click", function() {
		cancelarDlg();
	});

	const modalLogDiv = document.getElementById('dlg-Msg');
	const modalLog = new bootstrap.Modal(modalLogDiv);

	if (mostrarResultado)
		modalLog.show();
}

function fileValidation(extencionesPermitidas, filePath) {
	var allowedExtensions = eval("/(" + extencionesPermitidas + ")$/i");
	if (allowedExtensions.exec(filePath))
		return true;
	else
		return false;
}

function validaCamposCompletos() {
	var msg = "";
	var token = "";

	if ($("#cerFile").val() == "") {
		msg = "Es necesario que adjunte su certificado.";
		token = "\n";
	} else if (!fileValidation(".cer", $("#cerFile").val())) {
		msg = msg + token + "El certificado debe tener una extencion .cer";
		token = "\n";
	}

	if ($("#keyFile").val() == "") {
		msg = msg + token + "Es necesario que adjunte su llave privada.";
		token = "\n";
	} else if (!fileValidation(".key", $("#keyFile").val())) {
		msg = msg + token + "La llave privada debe tener una extencion .key";
		token = "\n";
	}

	if ($("#passwordLlave").val() == "")
		msg = msg + token + "El password de su llave privada es requerido";

	return msg;
}

/**
 * Limpia los campos relacionados con la FIEL.
 */
function limpiarCamposFiel() {
	$(".dlgFielInpt").val("");
	$("#passwordLlave").val("");
}

/**
 * Abre el modal de FIEL (por ejemplo desde autorizaPago/rechazaPago)
 */
function abrirDlgFiel() {
	limpiarCamposFiel();
	if (modalFielDlg) {
		modalFielDlg.show();
	}
}

/**
 * Llamado al dar clic en "Aceptar" dentro del modal.
 *  
 */
function aceptarDlg() {
	const cer = $("#cerFile").val();
	const key = $("#keyFile").val();
	const pwd = $("#passwordLlave").val();

	// Validaciones simples de ejemplo
	if (!cer) {
		alert("Debes seleccionar el archivo .cer");
		$("#bntAceptar").prop('disabled', false);
		return;
	}
	if (!key) {
		alert("Debes seleccionar el archivo .key");
		$("#bntAceptar").prop('disabled', false);
		return;
	}
	if (!pwd) {
		alert("Debes capturar el password de la llave privada");
		$("#bntAceptar").prop('disabled', false);
		return;
	}
	
	modalFielDlg.hide();
	$.blockUI();
	$("#autorizaLayouts").trigger("submit");
}

/**
 * Cierra el modal de FIEL y limpia los campos.
 */
function cancelarDlg() {
	limpiarCamposFiel();
	if (modalFielDlg) {
		modalFielDlg.hide();
	}
}

/**
 * Ejemplo de implementación básica de autorizaPago.
 * Ajusta según tu lógica anterior (folios seleccionados, etc.).
 */
function autorizaPago() {
	abrirDlgFiel();
}

/**
 * Ejemplo de implementación básica de rechazaPago.
 */
function rechazaPago() {

	abrirDlgFiel();
}



