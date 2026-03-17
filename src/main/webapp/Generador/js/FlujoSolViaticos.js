var es_mx = {
	sProcessing : "Procesando...",
	sLengthMenu : "Mostrar _MENU_ registros",
	sZeroRecords : "No hay registros a mostrar",
	sEmptyTable : "No hay datos en la tabla",
	sLoadingRecords : "Cargando...",
	sInfo : "Registros _START_ al _END_ de _TOTAL_",
	sInfoEmpty : "Registro 0 al 0 de 0",
	sInfoFiltered : "(filtered from _MAX_ total entries)",
	sInfoPostFix : "",
	sInfoThousands : ",",
	sSearch : "Filtro:",
	oPaginate : {
		sFirst : "Primero",
		sPrevious : "Ant.",
		sNext : "Sigte.",
		sLast : "&Uacute;ltimo"
	}
};


function onPostDisplay(idOper) {
	return true;
}

function onPostSubmit(idOper) {
	var exito = true;

	if( idOper == 1 ) {
		exito = generaFirmantes();
	}

	return exito;
}

function onLoadPlantilla(idOper) {
	initUI();
	try {

		queryFormPost({
			queryName : "existeCapturaViaticos",
			async : false,
			callback : function() {
				if( $("#existeCaptura").val() == "0" )
					iniciaCaptura();
				else
					leeInformacionCaptura();

			}
		});
		$.unblockUI();
	} catch( e ) {
		alert(e);
	}
}

function onSubmit(idOper) {
	var continuar = false;
	if( idOper == 1 ) {
		try {
			guardaDatosGestion();
			continuar = true;
		} catch( e ) {
			alert("Ocurrio el siguiente error:  " + e.message + "Por favor intente de nuevo");
		}
	}
	return continuar;
}

function iniciaCaptura() {
	$("#dFechaCaptura").val(fechaCaptura);
	$("#SeleccionaBeneficiarioTR").css("display", "block");
	$("#GuardarBtn").css("display", "block");
	inicializaBeneficiario();
	inicializaDatosAgenda();
	iniciaTransporte();
}

function initUI() {
	$("#GuardarBtn").button().click(function() {
		guardaViaticante();
	});

	$("#EnviarBtn").button().click(function() {
		if( !agendaCapturada() ) {
			alert("Antes de enviar debe capturar la agenda de la comision.");
		} else {
			var transpCapturado = transporteCapturado();
			if( ( !transpCapturado && confirm("No se ha capturado informacion de transporte. Esta seguro que desea continuar ") ) || transpCapturado ) {
				if( confirm("Esta seguro que desea enviar la solicitud de viaticos a autorizacion?") )
					enviaTramiteAutorizacion();
			}
		}
	});
	$("#EliminarBtn").button().click(function() {
		descartaTramite();
	});

	$("#guardaTransporte").button().click(function() {
		guardar();
	});
	
	/* ========== Asignacion de Funciones ======= */
	$("#cargaFacturasBtn").button().click(function() {
		muestraDivFacturas();
	});

	$("#catIdTipoOperacion").change(function() {
		$("#idTipoDocumento").val($("#catIdTipoOperacion").val());
		if ($.trim($(this).val()) != "" && $(this).val() != "-1") {
			cargaDestinoGasto();
		} else {
			limpiaSelect("DESTINO_GASTO", "-1", "Seleccione Tipo Destino");
			$("#DESTINO_GASTO").change();
		}

	});

	$("#DESTINO_GASTO").change(function() {

		$("#idDestinoGasto").val($("#DESTINO_GASTO").val());

		if ($.trim($(this).val()) != "" && $(this).val() != "-1") {
			querySelectPost("CatalogoObraTConceptoRead", "tConcepto", {
				async : false
			});
		} else {
			limpiaSelect("tConcepto", "-1", "Seleccione Tipo de Concepto");
			$("#tConcepto").change();
		}
	});

	$("#tConcepto").change(function() {
		$("#idTipoConcepto").val($(this).val());
		$("#idConcepto").val($(this).val());
		$("#TIPO_CONCEPTO").val($(this).val());

		if ($.trim($(this).val()) != "" && $(this).val() != "-1") {
			querySelectPost({
				queryName : 'CatalogoObraTMovimendoRead',
				targetObjectId : 'tmovimiento',
				async : false,
				callback : function() {
					$("#tmovimiento").change();
				}
			});
		} else {
			limpiaSelect("tmovimiento", "-1", "Seleccione Tipo de Movimiento");
			$("#tmovimiento").change();
		}

	});

	$("#tmovimiento").change(function() {
		$("#idTipoMovimiento").val($(this).val());
	});
	$("#ctaBancaria").change(function() {
		$("#CTAB").val($("#ctaBancaria").val());
	});
}

function enviaTramiteAutorizacion() {
	$.blockUI();
	parent.document.getElementById("pb_save").click();
	parent.document.getElementById("pb_send").disabled = false;
	parent.document.getElementById("pb_send").click();
}

function agendaCapturada() {
	$("#agendaCapturada").val("0");
	var agendaCapturada = false;

	queryFormPost(
		{
			queryName : "agendaCapturadaRead",
			async : false,
			callback : function() {
				agendaCapturada = ( parseInt($("#agendaCapturada").val(), 10) > 0 );
			}
		});

	return agendaCapturada;
}

function transporteCapturado() {
	$("#transporteCapturado").val("0");
	var transCapturado = false;

	queryFormPost(
		{
			queryName : "transporteCapturadoRead",
			async : false,
			callback : function() {
				transCapturado = ( parseInt($("#transporteCapturado").val(), 10) > 0 );
			}
		});

	return transCapturado;
}



function guardaDatosGestion() {
	var p = window.parent;

	if( p.gestion.getFolio() == "" ) {
		p.gestion.setMensaje("");
		p.gestion.setFolio(nFolioSolicitudViaticos);
		p.gestion.setOperador(uLogin);
		p.gestion.setFechaDocumento($("#dFechaCaptura").val());
		p.gestion.setEjercicioFiscal(ejercicioFiscal);
		p.gestion.setConceptoMov("Solictud de viaticos");
		p.gestion.setMoneda("MXP");
		p.gestion.setMensaje("");
	}
}


/**
 * Calcula el responsable siguiente. Se llama si y solo si la respuesta de llamar al onSubmit es true. Debe regresar el grupo que sera el responsable siguiente.
 * En caso de que sea el final del flujo, se debe llamar al responsable "CONSULTA_XXXXX" donde XXXXX es el tramite. PE CONSULTA_OBRAPUBLICA
 */
function ResponsableSiguiente(idOper) {
	if( idOper == 1 )
		return "AUTORIZA_SOLVIATICOS";
}

/**
 * Calcula la operacion siguiente. Se llama si y solo si la respuesta de llamar al onSubmit es true. Debe regresar el grupo que sera el responsable siguiente.
 * En caso de que sea el final del flujo, se debe llamar al responsable "CONSULTA_XXXXX" donde XXXXX es el tramite. PE CONSULTA_OBRAPUBLICA
 */
function OperacionSiguiente(idOper) {
	if( idOper == 1 )
		return "autoriza_solviaticos";
}


function generaFirmantes() {
	var generados = false;
	try {
		var viaticante = viaticanteToJSON();

		$.ajax({
			url : '../viaticos/CreaFirmantes',
			type : 'post',
			dataType : 'json',
			async : false,
			data : viaticante,
			success : function(data) {
				generados = ( data.success == "true" );
				if( !generados )
					alert("No fue posible registrar la informacion debido al siguiente error:\n" + data.data_1.result);
			}
		});
	} catch( e ) {
		alert("Ocurrio el siguiente error generando firmantes: " + e.message);
	}

	if( !generados )
		$.unblockUI();
	
	return generados;

}