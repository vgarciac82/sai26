/**
 * 
 */
var oTableFacturaCnt;
function init() {
	$("input.AyudaSyC").subIniciaDlg();
	$("input.autoCompletaSyC").subIniciaAutoCompleta();
	$("[readonly]").each(function() {
		$(this).css("background", "#CCCCCC");
	});
	creaDT();
}

function complementaInformacion() {
	queryFormPost({
		queryName:"readInfoComplementoContrato", 
		async : false,
		callback:function(){
			creaDT();
		}
	});
	
}

function creaDT() {
	
	var qw = " cTipoContrato = '" + $("#cTipoContrato").val() + "' AND cIDContrato = '" + $("#cIDContratoObra").val() + "'";
	
	oTableFacturaCnt = $("#dtFacturasContrato").dataTable({
		bScrollCollapse : true,
		bInfo : false,
		sScrollX : "100%",
		bAutoWidth : false,
		bJQueryUI : true,
		bRetrive : true,
		bDestroy : true,
		sPaginationType : "full_numbers",
		oLanguage : {
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
		},
		bServerSide : true,
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=v_ContratoFactura&qw=" + qw,
		bJQueryUI : true,
		aaSorting : [ [ 0, "asc" ] ],
		aoColumns : [
			{
				sName : "cFactura"
			},
			{
				sName : "cRFCFactura"
			},
			{
				sName : "mImporteSinImpuestos"
			},

			{
				sName : "mimporteIVA"
			},
			{
				sName : "mImporteConImpuestos"
			},
			{
				sName : "mImporteRemanente"
			}
		]
	});

}