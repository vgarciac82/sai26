function showAndHideTabs(){
	if(tabb==0 || tabb==1){
		$("#caratulaContratoMod").hide();
		$("#presupuestoContratoMod").hide();
		
	}else{
		$("#caratulaContratoMod").show();
		$("#presupuestoContratoMod").show();
		
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
				alert("No se hizo el cambio de centro contable y unidad ejecutora");
			}else{
				$("#cIdUnidadEjecutoraUsuario").val(j[0].unidadEjecutora);
				$("#cIdUnidadResponsableUsuario").val(j[0].unidadEjecutora);
				
			}
		}
	});
}
function formateaMoneda(importe){
	var importeSeparado = importe.toString().split("\.");
	var importeParte1 = importeSeparado[0];
	var cont=0;
	var tem="";
	for(var i=importeParte1.length; i>0; i--){
		if(cont == 3){
			tem = ","+tem;
			cont=0;
		}
		tem = importeParte1.substring(i-1,i)+tem;
		cont++;
	}
	if(importe.toString().indexOf("\.")>0){
		for(var i=importeSeparado[1].length; i<2; i++){
			importeSeparado[1]+="0";
		}
		return tem+"."+importeSeparado[1];
	}
	else{
		return tem+'.00';
	}
}
function quitaFmt( val ) {
   	val = val.replace("$", "");
   	val = val.replace(/,/g, '');
   	if ( val.indexOf( "(" ) >= 0 ) {
		val = val.replace("(", "");
		val = val.replace(")", "");
		val = "-" + val;
   	}
   	return val;
}