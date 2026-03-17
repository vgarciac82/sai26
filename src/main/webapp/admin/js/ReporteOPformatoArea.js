function initArea() {
	$("#opUnidadNormativa__").change(function() {
		cargaCatalogoCC();
	});
	$("#opUnidadNormativa__").blur(function() {
		cargaCatalogoCC();
	});
}

function clearSelect(idSel) {
	for ( var i = 0; i < idSel.length; i++)
		$('#' + idSel[i]).find('option').remove();
}

function cargaCatalogoCC() {
	clearSelect([ "cAreaContable" ]);
	querySelectPost("catAreaContable", "cAreaContable", {
		async : false
	});
}

function limpiaPantalla() {
	$(":input").each(
			function() {
				if ($(this).attr("type") == 'text'
						|| $(this).attr("type") == "hidden") {
					$(this).val('');
				} else if ($(this).attr("type").indexOf('select') >= 0) {
					clearSelect([ this.id ]);
					$(this).append('<option value="" selected="selected">Seleccione una opci&oacute;n</option>');
				}
			});
}

function generaReporte(){
	   if ($("#fechaI").val() == "" || $("#fechaF").val() == ""){
		   alert("Debe ingresar las fechas de consuta");
		   return;
	   }

	$("#frmOP").submit();
}
