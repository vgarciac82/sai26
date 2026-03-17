function showAndHideTabs(){
	if(tabb==0 || tabb==1){
		$("#CaratulaContratoPluriCap4").hide();
		$("#PartidasContratoPluriCap4").hide();
		$("#PresupuestoContratoPluriCap4").hide();
		$("#PrecompromisoContratoPluriCap4").hide();
		$("#NuevasEpsContratoPluriCap4").hide();
		$("#AmpliacionContratoPluriCap4").hide();
	}else{
		$("#CaratulaContratoPluriCap4").show();
		$("#PartidasContratoPluriCap4").show();
		$("#PresupuestoContratoPluriCap4").show();
		$("#PrecompromisoContratoPluriCap4").show();
		$("#AmpliacionContratoPluriCap4").hide();
		$("#NuevasEpsContratoPluriCap4").hide();
		if(parseInt($("#nIdEstado").val(),10)==4){
			$("#NuevasEpsContratoPluriCap4").show();
			if(parseInt($("#isAbierto").val(),10)==1){
				$("#AmpliacionContratoPluriCap4").show();
			}
		}
	}
}
function fnGetSelected( oTableLocal ){
	var aReturn = new Array();
	var aTrs = oTableLocal.fnGetNodes();
	
	for ( var i=0 ; i<aTrs.length ; i++ ){
		if ( $(aTrs[i]).hasClass('row_selected') ){
			aReturn.push( aTrs[i] );
		}
	}
	return aReturn;
}
function cambiaCentrocontableUsuario(){
	$.ajax({
		url: '../../servlet/CambiaPropiedadesUsuario',
		dataType: 'json',
		data: {"UnidadEjecutora" : $("#cIdUnidadEjecutora").val()},
		async : false,
		success : function(j) {
			if(j[0].error){
				swal("No se hizo el cambio de centro contable y unidad ejecutora",{icon:"warning",button: "Cerrar"});
			}else{
				$("#cIdUnidadEjecutoraUsuario").val(j[0].unidadEjecutora);
			}
		}
	});
}
function initQuerys() {
	queryFormPost("obtieneDatosContPluriCap4", {
		async : false
			
	});
}
function guardaBitacora(accion,documento){
	//Bitácora
	$("#cAccion").val(accion);
	$("#cIdDocumento").val(documento);
	queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
}