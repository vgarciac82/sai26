function showAndHideTabs(){
	if(tabb==0 || tabb==1){
		$("#CaratulaContratoCap4").hide();
		$("#PartidasContratoCap4").hide();
		$("#PresupuestoContratoCap4").hide();
		$("#PrecompromisoContratoCap4").hide();
		$("#NuevasEpsContratoCap4").hide();
		$("#AmpliacionContratoCap4").hide();
	}else{
		$("#CaratulaContratoCap4").show();
		$("#PartidasContratoCap4").show();
		$("#PresupuestoContratoCap4").show();
		$("#PrecompromisoContratoCap4").show();
		$("#AmpliacionContratoCap4").hide();
		if(parseInt($("#nIdEstado").val(),10)==4){
			$("#NuevasEpsContratoCap4").show();
			if(parseInt($("#isAbierto").val(),10)==1){
				$("#AmpliacionContratoCap4").show();
			}
		}
	}
}