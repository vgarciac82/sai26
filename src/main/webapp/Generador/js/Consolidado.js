var oTable;
var oTable1;
var oTableSolicitudDispPreSel;
var oTableSolicitudPreSel;
var oTableSolicitudDisponible
var oTableLineasPreSel;
var rpTable;
var oTableResumenLineasPartidas;

function resizeDt(){
	var tab = parseInt($("#tbs").val());
	//alert("tab: "+tab)
	switch (tab){
		case 2://consulta
			if($('#tblConsultaConsolidados >tbody >tr').length>0){
				oTable.fnAdjustColumnSizing();
			}
		break;
		case 4://Preselección
			if($('#tblSolicitudPreSel >tbody >tr').length>0){
				oTableSolicitudPreSel.fnAdjustColumnSizing();
			}
			if($('#tblSolicitudDisponible >tbody >tr').length>0){
				oTableSolicitudDisponible.fnAdjustColumnSizing();
			}
			
		break;
		case 5://partidas
			if($('#tblResumenpartidas >tbody >tr').length>0){
				rpTable.fnAdjustColumnSizing();
			}
			if($('#tblResumenLineasPartidas >tbody >tr').length>0){
				oTableResumenLineasPartidas.fnAdjustColumnSizing();
			}
			if($('#tblCucops >tbody >tr').length>0){
				oTable1.fnAdjustColumnSizing();
			}
			
		break;
	}
}
function showAndHideTabs(){
	$( "#CaratulaConsolidado" ).hide();
	$( "#PreseleccionConsolidado" ).hide();
	$( "#PartidasConsolidado" ).hide();
	$( "#presupuestoConsolidado" ).hide();
	$( "#preCompromisoConsolidado" ).hide();
	$( "#ampliacionPrecompromiso" ).hide();
	$( "#vigenciaRequisicionesConsolidadas" ).hide();
	
	if(parseInt($("#tbs").val())>2){
		$( "#CaratulaConsolidado" ).show();
		$( "#PreseleccionConsolidado" ).show();
		$( "#PartidasConsolidado" ).show();
		$( "#presupuestoConsolidado" ).show();
		$( "#preCompromisoConsolidado" ).show();
		$( "#ampliacionPrecompromiso" ).show();
		$( "#vigenciaRequisicionesConsolidadas" ).show();
	}
}