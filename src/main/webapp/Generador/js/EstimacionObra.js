/**
 * 
 */
function estimate(){
	if($("#checkbox_ultimaEstim").is(':checked')){//ES la ultima estimación
		estimateRemainder();
	}else{
		estimateAmounts()
	}

}
function estimateAmounts(){
	var brutoEstimacion =0.00 //D2
	var porcentajeEstimacion=0;//D3
	var amortizacionEstimacion=0;//D4
	var montoIVAEstimacion=0;//D5
	var totalEstimacion=0;//D6
	var retencionEstimacion=0;//D7
	var netoEstimacion=0;//D8
	var remanenteEstimacion=0;
	var brutoTotalContrato=(parseFloat($("#brutoContrato").val(),10)).toFixed(2);
	var totalEstimacion=0;
	//Calculo de montos
	brutoEstimacion = (parseFloat((quitaFrmt($("#mMontoEstimacion").val())))).toFixed(2);
	totalEstimacion=(parseFloat(brutoEstimacion,10)+parseFloat($("#brutoEstimaciones").val(),10)).toFixed(2);
	
	//alert("totalEstimacion : "+totalEstimacion+"  brutoTotalContrato : "+brutoTotalContrato);
	if( parseFloat(totalEstimacion,10)> parseFloat(brutoTotalContrato,10)){
		brutoTotalContrato=(parseFloat($("#brutoContrato").val(),10)+parseFloat($("#montoConvSinIVA").val(),10)).toFixed(2) ;
	}
	//alert("brutoTotalContrato : "+brutoTotalContrato);
	remanenteEstimacion = (parseFloat(brutoTotalContrato,10)-parseFloat($("#brutoEstimaciones").val(),10)).toFixed(2);
	if(parseFloat(brutoEstimacion,10)<=parseFloat(remanenteEstimacion,10)){
		porcentajeEstimacion=(parseFloat(brutoEstimacion,10)/parseFloat(brutoTotalContrato,10)).toFixed(2);
		if(parseFloat($("#amortizacionEstimaciones").val(),10)<parseFloat(quitaFrmt($("#brutoAnticipo").val()),10) ){
			amortizacionEstimacion=((parseFloat(brutoEstimacion,10)/parseFloat(brutoTotalContrato,10))*parseFloat(quitaFrmt($("#brutoAnticipo").val()),10)).toFixed(2);
			//alert("amortizacionEstimacion : "+amortizacionEstimacion +" amortizacionEstimaciones: "+$("#amortizacionEstimaciones").val());
			if((parseFloat(amortizacionEstimacion,10)+parseFloat($("#amortizacionEstimaciones").val(),10))>parseFloat($("#brutoAnticipo").val(),10) ){
				amortizacionEstimacion=(parseFloat($("#brutoAnticipo").val(),10)-parseFloat($("#amortizacionEstimaciones").val(),10)).toFixed(2);
			}
			//alert("amortizacionEstimacion : "+amortizacionEstimacion);
		}
		montoIVAEstimacion=(parseFloat(parseFloat(brutoEstimacion,10)-parseFloat(amortizacionEstimacion,10))*$("#porcentajeContrato").val()).toFixed(2);
		totalEstimacion=(parseFloat(brutoEstimacion,10)+parseFloat(montoIVAEstimacion,10)-parseFloat(amortizacionEstimacion,10)).toFixed(2);
		retencionEstimacion=(parseFloat(brutoEstimacion,10)*parseFloat($("#porcentajeRetencion5").val(),10)).toFixed(2);
		netoEstimacion=(parseFloat(totalEstimacion,10)-parseFloat(retencionEstimacion,10)).toFixed(2);
	}else{
		brutoEstimacion=0;
		alert("Se supero el monto del contrato, al contrato le queda un remanente de : "+remanenteEstimacion);
	}
	
	
	//Agregar los montos
	$("#mMontoEstimacion").val(brutoEstimacion);
	$("#mMontoEstimacionSubtotal").val(brutoEstimacion-amortizacionEstimacion);
	$("#mMontoEstimacionIva").val(montoIVAEstimacion);
	$("#mMontoEstimacionMasIva").val(netoEstimacion);
	$("#mMontoEstimacionAmortizado").val(amortizacionEstimacion);
	$("#mMontoEstimacionRetencion").val(retencionEstimacion);
		
	//Formato
	addFormato();
}
function estimateRemainder(){
	var brutoEstimacion =0.00 //D2
	var porcentajeEstimacion=0;//D3
	var amortizacionEstimacion=0;//D4
	var montoIVAEstimacion=0;//D5
	var totalEstimacion=0;//D6
	var retencionEstimacion=0;//D7
	var netoEstimacion=0;//D8
	var brutoTotalContrato=0;
	brutoTotalContrato=(parseFloat($("#brutoContrato").val(),10)+parseFloat($("#montoConvSinIVA").val(),10)).toFixed(2) ;
	$("#brutoContrato").val($("#brutoContrato").val()+$("#montoConvSinIVA").val());
	//Calculo de montos
	brutoEstimacion = (parseFloat(brutoTotalContrato-$("#brutoEstimaciones").val(),10)).toFixed(2);
	porcentajeEstimacion=(parseFloat(brutoEstimacion,10)/parseFloat(brutoTotalContrato,10)).toFixed(2);
	amortizacionEstimacion=parseFloat($("#montoAmortizacionContrato").val()-$("#amortizacionEstimaciones").val(),10).toFixed(2);
	montoIVAEstimacion=(parseFloat($("#montoIVAContrato").val()-$("#montoIVAAnticipo").val()-$("#montoIVAEstimaciones").val(),10)).toFixed(2);
	totalEstimacion=(parseFloat($("#montoTotalContrato").val()-$("#montoAnticipoTotal").val()-$("#montoEstimacionTotal").val(),10)).toFixed(2);
	retencionEstimacion=(parseFloat($("#retencionTotalContrato").val()-$("#montoEstimacionRetencion").val(),10)).toFixed(2);
	netoEstimacion=(parseFloat(totalEstimacion,10)-parseFloat(retencionEstimacion,10)).toFixed(2);
	
	//Agregar los montos
	$("#mMontoEstimacion").val(brutoEstimacion);
	$("#mMontoEstimacionSubtotal").val(brutoEstimacion-amortizacionEstimacion);
	$("#mMontoEstimacionIva").val(montoIVAEstimacion);
	$("#mMontoEstimacionMasIva").val(netoEstimacion);
	$("#mMontoEstimacionAmortizado").val(amortizacionEstimacion);
	$("#mMontoEstimacionRetencion").val(retencionEstimacion);
		
	//Formato
	addFormato();
}
function addFormato(){
	//Formato a campos
	cambiafrmt($("#mMontoEstimacion")[0]);
	cambiafrmt($("#mMontoEstimacionSubtotal")[0]);
	cambiafrmt($("#mMontoEstimacionIva")[0]);
	cambiafrmt($("#mMontoEstimacionMasIva")[0]);
	cambiafrmt($("#mMontoEstimacionAmortizado")[0]);
	cambiafrmt($("#mMontoEstimacionRetencion")[0]);
}
function calculaMontosEstimacion(ajuste) {
	var mMontoEstimacion = 0.00;
	var mMontoEstimacionIva = 0.00;
	var mMontoEstimacionMasIva = 0.00;
	var mMontoEstimacionAmortizado = 0.00;
	var mMontoEstimacionRetencion = 0.00;
	var mMontoEstimacionSubtotal = 0.00;

	mMontoEstimacion = parseFloat((quitaFrmt($("#mMontoEstimacion").val()))).toFixed(2);

	$("#mMontoEstimacion").val(mMontoEstimacion);
	queryFormPost("ExtraeRetenciones", {
		async : false
	});
	mMontoEstimacionRetencion = parseFloat((quitaFrmt($("#mMontoEstimacionRetencion").val()))).toFixed(2);

	$("#mMontoEstimacionRetencion").val(mMontoEstimacionRetencion);

	if (ajuste) {
		primerAjuste++;
		mMontoEstimacionAmortizado = parseFloat((quitaFrmt($("#mMontoEstimacionAmortizado").val()))).toFixed(2);
	} else {
		if( modificacionEstimacion ){
			queryFormPost("remanenteAmortizacionOPEstimacionEdit", {
				async : false
			});	
		}else{
			if($("#esFonden").val() == 1){
				$("#restoAmortizar").val(quitaFrmt($("#mMontoEstimacionAmortizado").val()));
			}else {
				queryFormPost("remanenteAmortizacionOPEstimacion", {
					async : false
				});
			}
		}

		

		primerAjuste = 0;

		var restoAmortizar = parseFloat($("#restoAmortizar").val());
		if (restoAmortizar > 0){
			if($("#esFonden").val() == 1){
				mMontoEstimacionAmortizado = restoAmortizar;
			}else{
				mMontoEstimacionAmortizado = ((mMontoEstimacion * parseFloat($("#nPorcAmortizacion").val())) / 100).toFixed(2);
			}
		}else{
			mMontoEstimacionAmortizado = 0.00;
		}
			
		
		/*VGC20201111 Primer intento de solucionar el calculo 
		 * final de la amortizacion. Si el contrato tiene CM de monto, 
		 * el sistema continua amortizando. Se realiza el cambio de que
		 * compara el monto calculado vs el remanente. Si calcula mas 
		 * de lo que resta por amortizar toma el valor del remanente.*/
		if( restoAmortizar < mMontoEstimacionAmortizado )
			mMontoEstimacionAmortizado = restoAmortizar;
		$("#mMontoEstimacionAmortizado").val(mMontoEstimacionAmortizado);
	}
	//subtotal
	mMontoEstimacionSubtotal = (parseFloat(mMontoEstimacion) - parseFloat(mMontoEstimacionAmortizado)).toFixed(2);
	$("#mMontoEstimacionSubtotal").val(mMontoEstimacionSubtotal);
	//iva
	mMontoEstimacionIva = parseFloat(mMontoEstimacionSubtotal) * $("#nPorcIVAAplicable").val();
	mMontoEstimacionIva = parseFloat((mMontoEstimacionIva)).toFixed(2)
	$("#mMontoEstimacionIva").val(mMontoEstimacionIva);
	//total
	mMontoEstimacionMasIva = parseFloat(mMontoEstimacionIva) + parseFloat(mMontoEstimacionSubtotal) - parseFloat(mMontoEstimacionRetencion);
	$("#mMontoEstimacionMasIva").val(parseFloat((mMontoEstimacionMasIva)).toFixed(2));

	//$("#mMontoEstimacionRetencion").val('0');	
	cambiafrmt($("#mMontoEstimacion")[0]);
	cambiafrmt($("#mMontoEstimacionSubtotal")[0]);
	cambiafrmt($("#mMontoEstimacionIva")[0]);
	cambiafrmt($("#mMontoEstimacionMasIva")[0]);
	cambiafrmt($("#mMontoEstimacionAmortizado")[0]);
	cambiafrmt($("#mMontoEstimacionRetencion")[0]);
}
function enviarEstimacionAut(numEstimacion){
	$("#nEstimacionAut").val(numEstimacion);
	myModalDatosNotaAutEst.show();
	enableInputs();
}