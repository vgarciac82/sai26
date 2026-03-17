var oTable;
var daTable;
var oTableProvedoresCotizaciones;
var oTableEP;
function resizeDt(){
	var tab = parseInt($("#tbs").val());
	//alert("tab: "+tab)
	switch (tab){
		case 1://consulta
			if($('#tblProcedimientos >tbody >tr').length>0){
				oTable.fnAdjustColumnSizing();
			}
		break;
		case 2://carátula
			if($('#tblProvedoresCotizaciones >tbody >tr').length>0){
				daTable.fnAdjustColumnSizing();
			}
		break;
		case 3://proveedores
			if($('#tblProvedoresCotizaciones >tbody >tr').length>0){
				daTable.fnAdjustColumnSizing();
			}
		break;
		case 4://cotización
			autosize($('textarea'));
			if($('#tblProvedoresCotizaciones >tbody >tr').length>0){
				daTable.fnAdjustColumnSizing();
			}
		break;
	}
}
function showAndHideTabs(){
	$( "#CaratulaProcedimiento" ).hide();
	$( "#ProveedoresProcedimiento" ).hide();
	$( "#RequisitosProcedimiento" ).hide();
	$( "#CotizacionProcedimiento" ).hide();
	$( "#DocumentosProcedimiento" ).hide();
	$( "#EvaluacionProcedimiento" ).hide();
	$( "#PreguntasProcedimiento" ).hide();
	$( "#ArchivosProcedimiento" ).hide();
	$( "#presupuestoProcedimiento" ).hide();
	$( "#precompromisoProcedimiento" ).hide();
	$( "#AmpliacionVigenciaProcedimiento" ).hide();
	$( "#PosiblesContratantes" ).hide();
	$( "#AsistenciaJuntaAclaracionesProced" ).hide();
	$( "#ServidoresPublicosProcedimiento" ).hide();
	
	if(parseInt($("#tbs").val())>1){
		$( "#CaratulaProcedimiento" ).show();
		$( "#ProveedoresProcedimiento" ).show();
		//$( "#RequisitosProcedimiento" ).show();
		$( "#CotizacionProcedimiento" ).show();
		//$( "#DocumentosProcedimiento" ).show();
		$( "#EvaluacionProcedimiento" ).show();
		$( "#PreguntasProcedimiento" ).show();
		$( "#ArchivosProcedimiento" ).show();
		$( "#presupuestoProcedimiento" ).show();
		$( "#precompromisoProcedimiento" ).show();
		$( "#AmpliacionVigenciaProcedimiento" ).show();
		$( "#PosiblesContratantes" ).show();
		$( "#AsistenciaJuntaAclaracionesProced" ).show();
		$( "#ServidoresPublicosProcedimiento" ).show();
	}
}