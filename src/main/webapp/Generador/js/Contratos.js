/**
 * Utilidades de contratos en general
 */

/**
 * En base al monto del compromiso determina si es un contrato directo
 */
function esContratoDirecto() {
	var bEsContratoDirecto = false;

	$("#contratoDirecto").val("");
	queryFormPost({
		queryName : "esContratoDirectoRead",
		async : false,
		callback : function() {
			var contratoDirecto = $("#contratoDirecto").val();
			bEsContratoDirecto = (contratoDirecto === 'true');
		}
	});
	return bEsContratoDirecto;
}