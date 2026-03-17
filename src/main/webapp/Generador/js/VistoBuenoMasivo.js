/**
 * 
 */



var tiposPago = {
	"DI" : "PAGODIRECTO",
	"DV" : "PAGODIVERSO",
	"FE" : "PAGOFEDERALIZADO",
	"RG" : "RELACIONGASTOS",
	"PO" : "PAGOOBRA",
	"AX" : "ANEXO1",
	"IF" : "REINTEGROINGRESO",
	"PC" : "PAGOPENASCONV",
	"CA" : "CAJA",
	"CV" : "COMSINVIATICOS"
};

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

$(document).ready(function() {

	init();
	var oTable = $("#dtCargasMasivas").dataTable();

	$("#u_Login").val(cLogin);
	$("#RFC").val(RFC);

	cargaGrid();
	creaDTDetalleCargasMasivas(-1);
	$("#btn_Autoriza").button();
});



/**
 * 
 */
function cargaGrid() {
	$("#cWhere").val(generaCondicion());

	var vista = "";
	if (tipoAutorizacion == "VOBO")
		vista = "vVistoBuenoCargaMasiva";
	else if (type = "AUT")
		vista = "vautorizacargamasiva";

	oTable = $('#dtCargasMasivas').dataTable({
		"bPaginate" : true,
		"bLengthChange" : true,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"sPaginationType" : "full_numbers",
		"bScrollCollapse" : true,
		"bServerSide" : true,
		"sScrollX" : "100%",
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="
			+ vista + "&qw=" + encodeURI($("#cWhere").val()),
		aoColumns : [ {
			sName : "nFolioCargaMasiva"
		}, {
			sName : "fechaAplicacion"
		}, {
			sName : "fechaCarga"
		}, {
			sName : "cunidadresponsable"
		}, {
			sName : "importeCarga"
		}, {
			sName : "totalCargadas"
		} ],
		oLanguage : es_mx
	});

	$("#dtCargasMasivas tbody").click(function(event) {

		$(oTable.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');

		var aPos = oTable.fnGetPosition(event.target.parentNode);
		var aData = oTable.fnGetData(aPos);

		var nFolioRG = aData[0];
		$("#nFolioCargaMasiva").val(nFolioRG);
		creaDTDetalleCargasMasivas(nFolioRG);

	});

	$("#dtCargasMasivas tbody").dblclick(
		function(event) {

			$(oTable.fnSettings().aoData).each(
				function() {
					$(this.nTr).removeClass('row_selected');
				}
			);

			$(event.target.parentNode).addClass('row_selected');
			aPos = oTable.dataTable().fnGetPosition(event.target.parentNode);

			var aData = oTable.fnGetData(aPos);
			sendPage(aData[0]);
			return;
		});
}

function enviaParaFirma() {
	var nFolioRG = $("#nFolioCargaMasiva").val();
	try {
		nFolioRG = parseInt(nFolioRG, 10);
		if (nFolioRG <= 0) {
			Swal.fire({ icon: "warning",
						text: "Debe seleccionar la carga masiva primero."});					
			return;
		} else {
			sendPage(nFolioRG);
		}
	} catch (e) {
		Swal.fire({ icon: "warning",
					text: "Debe seleccionar la carga masiva primero."});		
	}

}

function sendPage(nFolioRG) {
	var a = tipoAutorizacion == "VOBO" ? "VoBoPagoMasivo" : (tipoAutorizacion == "AUT" ? "AutPagoMasivo" : "");
	var f = nFolioRG;
	var d = tiposPago[$('input[name=cTipoPago]:checked').val()];

	location.href = "../Generador/ResumenPagosMasivo.jsp?a=" + a + "&f=" + f + "&d=" + d;
}

function creaDTDetalleCargasMasivas(idCargaMasiva) {
	var condDTDetalle = " folio = " + idCargaMasiva;

	oTableDetalle = $("#dtDetalleCarga").dataTable({
		"bPaginate" : true,
		"bLengthChange" : true,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"sPaginationType" : "full_numbers",
		"bScrollCollapse" : true,
		"bServerSide" : true,
		"scrollX" : true,
		"sScrollX" : "1024",
		"sScrollY" : "300",
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_CargaMasivaDetalle&qw=" + condDTDetalle,
		aoColumns : [ {
			sName : "cidrelacion"
		}, {
			sName : "cidrfc"
		}, {
			sName : "cnombre"
		}, {
			sName : "cconcepto"
		}, {
			sName : "mimportemasiva"
		} ],
		oLanguage : es_mx
	});

}

function generaCondicion() {
	var where = "";
	where = where + " tipoPago = '" + $("input[name='cTipoPago']:checked").val() + "'";
	where = where + " AND cEsFirmaElectronica = 'S'";

	if (tipoAutorizacion == "VOBO")
		where = where + " AND nEnviadoSICOP = -2 AND nNumEmpleadoVoBo = " + numeroEmpleado;
	else
		where = where + " AND nEnviadoSICOP = -1 AND nNumEmpleadoAut = " + numeroEmpleado;

	return where;
}


function init() {
	if (tipoAutorizacion == "VOBO") {
		$("#tituloOperacion").text("Visto Bueno de Egreso");
	} else if (tipoAutorizacion == "AUT") {
		$("#tituloOperacion").text("Autoriza Egreso");
	}
}