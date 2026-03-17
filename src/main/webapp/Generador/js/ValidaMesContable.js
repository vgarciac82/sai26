function actualizaMesAplicacion() {
	var exito = false;
	$
			.ajax({
				url : '../ActualizaFechaAplicacion',
				dataType : 'json',
				type : "POST",
				data : {
					"accion" : "UPDATE_APP_DATE",
					"documento" : $("#cDocumento").val(),
					"folio" : $("#nFolioPago").val()
				},
				async : false,
				success : function(json) {
					exito = "true" == json.success;

					if (!exito) {
						alert("ATENCION! No fue posible actializar la fecha de aplicacion debido a:\n"
								+ json.data_1.result
								+ "\nIntente nuevamente. Si el problema persiste reportelo al administrador del sistema.");
					} else {
						if( json.data_1.result == "1" )
							exito = true;
						else
							exito = false;
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