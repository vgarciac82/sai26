/**
 * 
 */
$(document).ready(function() {
	cargaCatalogos();

	$("#numeroContrato").change(function() {
		cambiaContrato();
	});

	$("#destGasto").change(function() {
		cargaTipoConcepto();
	});

	$("#TIPO_CONCEPTO").change(function() {
		cargaTipoMovimiento();
		cargaListaAlmacenes();
	});

	$("#cIdRecepMat").change(function() {
		cargaMontosRecepcion();
	});

});

function cambiaContrato() {
	limpiaInformacion();
	cargaInformacionContrato();

}

function limpiaInformacion() {
	$(".informativo").each(function() {
		$(this).val("");
	});

	$(".catalogoDependiente").each(function() {
		var selector = "#" + $(this).attr("id") + " option[value!='']";
		$(this).val("");
		$("" + selector).each(function() {
			$(this).remove();
		})
	});

	$(".catalogoIndependiente").each(function() {
		$(this).val("");
	});

	$(".capturable").each(function() {
		$(this).val("");
	});

	reiniciaMontosPago();

}

function cargaInformacionContrato() {
	queryFormPost({
		queryName : "InfoContratoRead",
		async : false,
		callback : function() {
			cargaRecepcionesMaterial();
		}
	});
}

function cargaRecepcionesMaterial() {
	querySelectPost("InfoContratoRMRead", "cIdRecepMat", {
		async : false
	});
}


function cargaCatalogos() {
	querySelectPost("CatTipoOperacionRead", "TIPO_OPERACION", {
		async : false
	});
	querySelectPost("CatTipoMovimientoRead", "destGasto", {
		async : false
	});
}

function cargaTipoConcepto() {
	limpiaSelectCatalogo("TIPO_CONCEPTO");
	querySelectPost("CatTipoConceptoCntRead", "TIPO_CONCEPTO", {
		async : false
	});
}

function limpiaSelectCatalogo(idSelect) {
	$("#" + idSelect).val("");
	$("#" + idSelect + " option[value!='']").each(function() {
		$(this).remove();
	});
	$("#" + idSelect).change();
}

function cargaTipoMovimiento() {
	limpiaSelectCatalogo("TIPO_MOVIMIENTO");
	querySelectPost("CatTipoMovimientoCntRead", "TIPO_MOVIMIENTO", {
		async : false
	});
}

function cargaListaAlmacenes() {
	limpiaSelectCatalogo("ALM");
	if ($("#TIPO_CONCEPTO").val() == "AL") {
		querySelectPost("CatAlmacenCntRead", "ALM", {
			async : false
		});
	} else {
		querySelectPost("CatAlmacenVacioCntRead", "ALM", {
			async : false
		});
	}
}
