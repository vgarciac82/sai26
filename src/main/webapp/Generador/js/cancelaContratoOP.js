/**
 * IRD RO-0003
 * DataTable que almacena los resultados de la busqueda.
 */
var dTable;

/**
 * Folio para el convenio modificatorio.
 */
var folioSAI = "";

/**
 * Parametros de lenguaje
 */
var lengParams = {
	sProcessing : "Procesando...",
	sLengthMenu : "Mostrar _MENU_ registros",
	sZeroRecords : "No hay registros a mostrar",
	sEmptyTable : "No hay datos en la tabla",
	sLoadingRecords : "Cargando...",
	sInfo : "Registros _START_ al _END_ de _TOTAL_",
	sInfoEmpty : "Registro 0 al 0 de 0",
	sInfoFiltered : "(filtado de _MAX_ registros)",
	sInfoPostFix : "",
	sInfoThousands : ",",
	sSearch : "Buscar:",
	oPaginate : {
		sFirst : "Primero",
		sPrevious : "Ant.",
		sNext : "Sigte.",
		sLast : "&Uacute;ltimo"
	}
};

/**
 * Inicializa los campos de la pantalla.
 */
function init() {
	dTable = $("#resultTable").dataTable({
		"bJQueryUI" : true,
		"sScrollY" : 250,
		"bPaginate" : false,
		"oLanguage" : lengParams,
		"aoColumnDefs" : [ {
			"bSearchable" : false,
			"aTargets" : [ 4 ]
		}, {
			"bSearchable" : false,
			"bVisible" : true,
			"aTargets" : [ 5 ]
		}, {
			"bSearchable" : false,
			"bVisible" : false,
			"aTargets" : [ 6 ]
		} ]
	});

	$("#searachButton").button().click(function() {
		search();
	});
	$("#aceptarButton").button().click(function() {
		var listaFolios = obtenFoliosCancelar();
		if (listaFolios == ""){
			alert("Debe ingresar al menos un contrato.");
			return;
		}
		if ($("#cMotivo").val() == "") {
			alert("Debe ingresar el motivo de cancelación");
			$("#cMotivo").focus();
			return;
		}
		$("#msgAdvertencia").text(
					"Se cancelaran los contratos seleccionados: "
							+ listaFolios);
		$.blockUI({
			message : $('#question'),
			css : {
				width : '375px'
			}
		});		
	});

	$("#limpiarButton").button().click(function() {
		window.location = "cancelaContratoOP.jsp"
	});

	$("#dialog-form-apcon").dialog({
		autoOpen : false,
		height : 400,
		width : 800,
		modal : true,
		close : function() {
			search()
		}
	});
	
	$("#yes").button().click(function() {
		divAplica.innerHTML = "Procesando, por favor espere.";
		$("#dialog-form-apcon").dialog("open");
		setTimeout('fnAplicaMotorForCancel()', 2000);


	});

	$("#no").button().click(function() {
		$.unblockUI();
	});

	queryFormPost("cEjercicioFiscalActivoRead", {
		async : false
	});

	$("#cveContrato").focus();
	querySelectPost("catalogoEntidadFederativaRead", "EntidadFederativa");
	document.getElementById("aceptarButton").style.visibility = 'hidden';

}

function terminaAppContCancel(success){
	
/*	if( success ){
	  parent.document.getElementById("pb_save").click();	
	  parent.document.getElementById("pb_send").click();
	  return;
	}else{
		$("#iCancelContract").val("0");
	}  */
	
}

function obtenFoliosCancelar (){
	var aData = dTable.fnGetData();
	var arrlist = new Array();
	var i=0;
	var listaFolios = "";
	$('#resultTable input:checked').each(function(idx, elm){
		var paso = aData[dTable.fnGetPosition($(this).closest('tr')[0])];
		arrlist[i] = new Array(paso[4]);	
		listaFolios += "|" + paso[4] + "|";
		i++;
	});
	return listaFolios;
}

function fnAplicaMotorForCancel() {

	var aData = dTable.fnGetData();
	var arrlist = new Array();
	var i=0;
	var strAction = "";
	var listaFolios = "";
	$('#resultTable input:checked').each(function(idx, elm){
		var paso = aData[dTable.fnGetPosition($(this).closest('tr')[0])];
		arrlist[i] = new Array(paso[4]);	
		listaFolios += "|" + paso[4] + "|";
		i++;
	});	
	strAction = "cancelaContratoOP.jsp?cancelContract=Si" + "&listaFolios="+ listaFolios + "&numFolios=" + i + "&motivo=" + $("#cMotivo").val();
	divAplica.innerHTML = "<iframe id='ifAplica' src='"+ strAction+ "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
	$.blockUI({
		message : "<h1>Espere ...</h1>"
	});


	$.unblockUI();
}

/**
 * Busca contratos en base al numero.
 */
function search() {
	folioSAI = "";
// else {
		$
				.blockUI({
					message : "<p><img src='imagenes/wait24trans.gif' />&nbsp;&nbsp;Buscando. Por favor espere...</p>"
				});
		var condition = "";
		var token = "";
		if ($("#cveContrato").val() != ""){
			condition += token + " cvecontrato LIKE '%" + $("#cveContrato").val() + "%'";
			token = " and ";
		}
		if ($("#UnidadNormativa").val() != ""){
			condition += token + " cU_UR = '" + $("#UnidadNormativa").val() + "'";
			token = " and ";
		}
		if ($("#cUnidadEjecutora").val() != ""){
			condition += token + " cU_UE = '" + $("#cUnidadEjecutora").val() + "'";
			token = " and ";
		}
		if ($("#ProgramaPresupuestario").val() != ""){
			condition += token + " cProgramapresupuestario = '" + $("#ProgramaPresupuestario").val() + "'";
			token = " and ";
		}
		if ($("#Cartera").val() != ""){
			condition += token + " cCveCartera = '" + $("#Cartera").val() + "'";
			token = " and ";
		}
		if (document.getElementById('EntidadFederativa').value != ""){
			condition += token + " cIdEntidadFederativa = '" + document.getElementById('EntidadFederativa').value + "'";
			token = " and ";
		}
		if (document.getElementById('estatusContrato').value != "" && document.getElementById('estatusContrato').value != "0"){
			condition += token + "  momento like '" + document.getElementById('estatusContrato').value + "%' ";
			token = " and ";
		}
		condition += token + " foliosai in (select folioSAI from dbo.fn_DetalleObraPublicaCancelacion( ) kk where kk.cmes <= " + $("#InfoRegMes").val() + ") ";
		
//		alert (document.getElementById('EntidadFederativa').value);
		var tableName = "VOBRAPUBLICAPENDIENTES";
		$.getJSON("../catalogos/SelectJson.jsp", {
			Tabla : tableName,
			Param : condition,
			MaxReg : "",
			ajax : 'false'
		}, function(data) {
			dTable.fnClearTable();
			for ( var i = 0; i < data.length; i++) {
				dTable.fnAddData([
						creaCheckBox(data[i].Col5, "chk"+i, data[i].Col0,
								"suspendePagos('" + i + "')",
								data[i].Col4),
								data[i].Col0,
						data[i].Col1,
						data[i].Col2,
						data[i].Col3,
						 data[i].Col4, data[i].Col5 ]);
			}
			$.unblockUI();
			if (i == 0){
				alert("No se encontraron coincidencias para el contrato: "
						+ $("#cveContrato").val());
				document.getElementById("aceptarButton").style.visibility = 'hidden';
			}else{
				document.getElementById("aceptarButton").style.visibility = '';
			}
		});
//	}

}

function creaCheckBox(name, id, val, onclickFn, activo) {
	var str = '<input type="checkbox" value="' + val + '" id="' + id
			+ '" name="' + name + '" onclick="' + onclickFn + '"'
			+ (activo == 'S' ? 'checked="checked"' : '') + '/>';
	return str;
}

function suspendePagos(cCveContrato) {

	var estatusPago, folioSAI, cCentroContable;
	var index = parseInt(cCveContrato,10);
	
	var rows = dTable.fnGetData();
	var dataCnt = rows[index];
	
	$("#estatusPago").val("");
	$("#folioSAI").val("");
	$("#cCentroContable").val("");
	
	estatusPago = $("#chk" + index).is(":checked") ? "S" : "N";
	folioSAI = dataCnt[0];
	cCentroContable = dataCnt[6];
		
/*	$("#estatusPago").val(estatusPago);
	$("#folioSAI").val(folioSAI);
	$("#cCentroContable").val(cCentroContable);
	
	if ("N" == estatusPago) {
		$("#msgAdvertencia").text(
				"Se activaran los pagos para el contrato seleccionado: "
						+ folioSAI);
	} else {
		$("#msgAdvertencia").text(
				"Se suspenderan los pagos para el contrato seleccionado: "
						+ folioSAI);
	}
	$.blockUI({
		message : $('#question'),
		css : {
			width : '375px'
		}
	});
	*/
}