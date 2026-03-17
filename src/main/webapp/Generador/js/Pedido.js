var oTable;
var oTablePartidas;
var oTableFirmantes;
var oTableCatalogo;
var oTableClaves;
var oTablePagos;
var oTableRetencion;
var oTableAnticipo;
function resizeDt(){
	var tab = parseInt($("#tbs").val());
	switch (tab){
		case 0://consulta
			if($('#tblConsultaPedidos >tbody >tr').length>0){
				oTable.fnAdjustColumnSizing();
			}
		break;
		case 2://partidas
			if($('#tblPartidas >tbody >tr').length>0){
				oTablePartidas.fnAdjustColumnSizing();
			}
			
		break;
		case 3://firmantes
			if($('#tblFirmantesPedido >tbody >tr').length>0){
				oTableFirmantes.fnAdjustColumnSizing();
			}
			if($('#tblCatalogoFirmantes >tbody >tr').length>0){
				oTableCatalogo.fnAdjustColumnSizing();
			}
		break;
		case 4://presupuesto
			if($('#dt_clavepresup >tbody >tr').length>0){
				oTableClaves.fnAdjustColumnSizing();
			}
			
		break;
		case 5://precompromiso
			
		break;
		case 6://pagos
			if($('#tblPagos >tbody >tr').length>0){
				oTablePagos.fnAdjustColumnSizing();
			}
		break;
		case 17://Retenciones
			if($('#dt_retencion >tbody >tr').length>0){
				oTableRetencion.fnAdjustColumnSizing();
			}
			if($('#dt_anticipos >tbody >tr').length>0){
				oTableAnticipo.fnAdjustColumnSizing();
			}
		break;
	}
}
function showAndHideTabs(){
	$( "#nuevoPedidoFONDEN" ).hide();
	$( "#nuevoPedidoCap1000" ).hide();
	$( "#caratulaPedido" ).hide();
	$( "#partidasPedido" ).hide();
	$( "#firmantesPedido" ).hide();
	$( "#presupuestoPedido" ).hide();
	$( "#preCompromisoPedido" ).hide();
	$( "#pagosPedido" ).hide();
	$( "#imprimirPedido" ).hide();
	$( "#clausulasPedido" ).hide();
	$( "#plurianualidad" ).hide();
	$( "#pasivo" ).hide();
	$( "#responsablesAlmacen" ).hide();
	$( "#reportePedido" ).hide();
	$( "#propuestaConjunta" ).hide();
	$( "#anticiposRetencion" ).hide();
	$( "#reclasificaTipoAdj" ).hide();
	
	
	if(parseInt($("#tbs").val())>0){
		$( "#caratulaPedido" ).show();
		$( "#partidasPedido" ).show();
		$( "#firmantesPedido" ).show();
		$( "#presupuestoPedido" ).show();
		$( "#imprimirPedido" ).show();
		if(nIdEstado == 4){
			$( "#reclasificaTipoAdj" ).show();
			$( "#pagosPedido" ).show();
		}
		if(nIdEstado > 1 && nIdEstado <= 4){
			$( "#preCompromisoPedido" ).show();
			$( "#anticiposRetencion" ).show();
		}
	}
}