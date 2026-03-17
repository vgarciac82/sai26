/**
 * Funcion para actualizar los montos de las retenciones y los impuestos en el
 * detalle de los pagos.
 */
function actualizaRetenciones()
{

	var exito = false;
	var msg = "";
	
	$.ajax({
		
		url : '../recalculaDetalle',
		type :"POST",
		dataType : 'json',
		async : false,
		success : function(json) {
			exito = "true" == json.success;
			if( !exito ){
				r = json.data_1.result;
				alert( r );
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
			exito = false;
		}
	});
	
	return exito;
}