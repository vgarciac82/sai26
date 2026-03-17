function showAndHideTabs(){
	if(tabb==0 || tabb==1){
		$("#caratulaPlurianualidad").hide();
		$("#presupuestoPlurianualidad").hide();
		$("#precompromisoPlurianualidad").hide();
		$("#nuevasEps").hide();
		$("#ampliacionContratoPluri").hide();
		$("#terminacionAnticipada").hide();
	}else{
		$("#caratulaPlurianualidad").show();
		$("#presupuestoPlurianualidad").show();
		$("#precompromisoPlurianualidad").show();
		$("#terminacionAnticipada").show();
		$("#nuevasEps").show();
		if(parseInt($("#esContratoAbierto").val(),10) == 0 || parseInt($("#tab").val(),10)<2  ){
			$( "#ampliacionContratoPluri" ).hide();
		}else{
			$( "#ampliacionContratoPluri" ).show();
		}
	}
}