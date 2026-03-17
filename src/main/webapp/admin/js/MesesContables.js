/*
 * Inicializa la pagina.
 */
function init() {
	$("input.AyudaSyC").subIniciaDlg();
	$("input.autoCompletaSyC").subIniciaAutoCompleta();
	$("#abrirMes").button().click(function() {
		abrirMes();
	});
	$("#cerrarMes").button().click(function() {
		cerrarMes();
	});
}

function limpiaBusqueda(evt) {
	var charCode = (evt.which) ? evt.which : evt.keyCode;
	var lngth = getSelText().length;

	if (charCode == 8
			|| charCode == 46
			|| ((charCode != 16 && charCode != 17 && charCode != 18 && charCode != 9) && lngth > 0)) {
		$("#dCuenta").val("");
		$("#nSubCuenta").val("");
		return true;
	}
	if (charCode == 9) {
		$("#divACnCuenta").hide();
		blurNCta();
		return false;
	}

}