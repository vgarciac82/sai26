var oTablePartidasServicios;
var oTableLienasAnticipo;
var oTablePartidas;
var oTableLineas;
var oTableConsulta;
function resizeDt(){
	var tab = parseInt($("#tbs").val());
	switch (tab){
		case 0://Nuevo
			if($('#tblLineasAnticipo >tbody >tr').length>0){
				oTableLienasAnticipo.fnAdjustColumnSizing();
			}
			if($('#tblLineas >tbody >tr').length>0){
				oTableLineas.fnAdjustColumnSizing();
			}
			if($('#tblPartidasServicios >tbody >tr').length>0){
				oTablePartidasServicios.fnAdjustColumnSizing();
			}
			if($('#tblPartidas >tbody >tr').length>0){
				oTablePartidas.fnAdjustColumnSizing();
			}
		break;
		case 1://Consulta
			if($('#tblConsulta >tbody >tr').length>0){
				oTableConsulta.fnAdjustColumnSizing();
			}
		break;
		case 2://Carátula
			if($('#tblLineasAnticipo >tbody >tr').length>0){
				oTableLienasAnticipo.fnAdjustColumnSizing();
			}
			if($('#tblLineas >tbody >tr').length>0){
				oTableLineas.fnAdjustColumnSizing();
			}
			if($('#tblPartidasServicios >tbody >tr').length>0){
				oTablePartidasServicios.fnAdjustColumnSizing();
			}
			if($('#tblPartidas >tbody >tr').length>0){
				oTablePartidas.fnAdjustColumnSizing();
			}
		break;
		
		
	}
}
function showAndHideTabs(){
	$( "#CaratulaRecepcion" ).show();
	if(parseInt($("#tbs").val())!=2){
		$( "#CaratulaRecepcion" ).hide();	
	}
}
function muestraLineas(){
	var qw="1=1";
	if($("#cIdRecepMat").val()!='' && $("#cIdContrato").val()!='')
		qw=qw+" AND cIdRecepMat='"+$("#cIdRecepMat").val()+"' AND cIdpedContDef='"+$("#cIdContrato").val()+"'";
	oTableLineas = $("#tblLineas").dataTable({
		bScrollCollapse: true,
		bInfo: false,
		//sScrollY : "100%",
		sScrollX: "100%",
		bAutoWith: true,
		bJQueryUI: true,
		bRetrive : true,
		bDestroy : true,
		sPaginationType: "full_numbers",
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay datos en la tabla",
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0",
			sInfoFiltered: "(filtado de _MAX_ registros)",
			sInfoPostFix: "",
			sInfoThousands: ",",
			sSearch: "Buscar:",
			oPaginate: {
				sFirst:    "Primero",
				sPrevious: "Ant.",
				sNext:     "Sigte.",
				sLast:     "&Uacute;ltimo"
			}
		},
		bServerSide: true,
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mLineasSolAbastecimiento('"+$("#cIdRecepMat").val()+"','"+$("#cIdContrato").val()+"')",
		bJQueryUI: true,
		aaSorting: [[ 1, "asc" ]] ,
		aoColumns: [
			{sName: "nIdConsecutivoRecepM",bVisible: false},
			{sName: "nIdLineaConsolidado"},
			{sName: "cIdRecepMat"},
			{sName: "nCantidad"},
			{sName: "mMontoConIVA"},
			{sName: "mMontoSinIVA"},
			{sName: "mMontoIVA"},
			{sName: "eliminar"},
			{sName: "cIdpedContDef",bVisible: false}
		]
	});
}
function crearSolAbastecimiento(){
	var cadenaLineaConsCant="";
	//Para los ped/contratos de bienes
	if(($("#cIdTipoProcedimiento").val()=='PC' && $("#esCucopGasolina").val()=='')
		||($("#cIdTipoProcedimiento").val()=='PR'  && $("#cucopGasolina").val()=='') 
		||( $("#nServicio_A_Bienes").val()==1 ) 
		|| $("#cIdTipoProcedimiento").val()=='PT'){
		cadenaLineaConsCant=joinChain(oTablePartidas, 'BIEN');
		if(cadenaLineaConsCant==''){
			swal("No hay nada que agregar, favor de capturar la cantidad o monto a recepcionar de cada línea de contrato",{icon:"info",button: "Cerrar"});
			return;
		}
		$("#cadenaLineaCantidad").val(cadenaLineaConsCant);
		if($("#cIdRecepMat").val()==''){
			queryFormPost("mRecepMat_siguienteConsecutivoRead", {async: false, 
				callback : function() 
				{
					$("#cIdRecepMat").val('RM-'+$("#cIdUnidadEjecutora").val()+'-'+$("#nIdConsecutivoRecepMat").val());
					creaActualizaRecep("CREA");
				}
			});
		}else{
			creaActualizaRecep();
		}
	}else{//Cuando son servicios
		
		cadenaLineaConsCant=joinChain(oTablePartidasServicios, 'SRV');
		
		if(cadenaLineaConsCant==''){
			swal("No hay nada que agregar",{icon:"info",button: "Cerrar"});
			return;
		}
		$("#cadenaLineaCantidad").val(cadenaLineaConsCant);
		if($("#cIdRecepMat").val()==''){
			queryFormPost("mRecepMat_siguienteConsecutivoRead", {async: false, 
				callback : function() 
				{
					$("#cIdRecepMat").val('RM-'+$("#cIdUnidadEjecutora").val()+'-'+$("#nIdConsecutivoRecepMat").val());
					//Cuando es arrendamiento el tipo del procedimiento es vacio
					if($("#cIdTipoProcedimiento").val()==''){
						queryFormPost("creaActualizaRecepMatArrend",  {async : false, 
							callback : function() 
							{
								muestraDatosServicios();
								muestraLineas();
								//Guarda en la Bitácora
								guardaBitacora("CREA RECEPCIÓN DE ARRENDAMIENTO");
							}
						});
					}else{
						creaActualizaRecep("CREA");
					}
				}
			});
		}else{
			//Cuando es arrendamiento el tipo del procedimiento es vacio
			if($("#cIdTipoProcedimiento").val()==''){
				queryFormPost("creaActualizaRecepMatArrend",  {async : false, 
					callback : function() 
					{
						muestraDatosServicios();
						muestraLineas();
						//Guarda en la Bitácora
						guardaBitacora("ACTUALIZA RECEPCIÓN DE ARRENDAMIENTO");
					}
				});
			}else{
				creaActualizaRecep("ACTUALIZA");
			}
		}
	}
	$("#textcidRecepcion").val($("#cIdRecepMat").val());
	queryFormPost("mRecepMat_montosTtales",{async: false});
	queryFormPost("mRecepMat_montosOtrosImp",{async: false});
}
function joinChain(oTable, tipo){
	var cadenaLineaConsCant="";
	var token="";
	var aTrs =  oTable.fnGetNodes();
	var montoDisp=0;
	for ( var i=0 ; i<aTrs.length; i++ )     
	{  
		var nTr = oTable.fnGetData(aTrs[i]);
		var subtotal=0.0;
		if(tipo!='SRV'){
			subtotal=parseInt($("#cantAgregar_"+nTr[0]).val(),10)*parseFloat(nTr[11]);
			if(parseInt($("#cantAgregar_"+nTr[0]).val(),10)<=parseInt(nTr[5],10) && parseInt($("#cantAgregar_"+nTr[0]).val(),10)>0 && ($("#mDescuentoSinIVA_"+nTr[0]).val()<= subtotal ) ){
				cadenaLineaConsCant=cadenaLineaConsCant+token+nTr[0]+'-'+$("#cantAgregar_"+nTr[0]).val()+'|'+$("#mDescuentoSinIVA_"+nTr[0]).val();
				token=",";
			}else{
				if(parseInt($("#cantAgregar_"+nTr[0]).val(),10)>0 && parseInt($("#cantAgregar_"+nTr[0]).val(),10)>parseInt(nTr[5],10))
					swal("En la linea "+nTr[0]+" sobrepasaste la cantidad disponible.",{icon:"info",button: "Cerrar"});
				
				if($("#mDescuentoSinIVA_"+nTr[0]).val()> subtotal)
					swal("En la linea "+nTr[0]+" el descuento de : "+$("#mDescuentoSinIVA_"+nTr[0]).val()+" es mayor al subtotal (PU * Cantidad capturada): "+subtotal,{icon:"info",button: "Cerrar"});
			}
		}else{
			montoDisp=nTr[5];
			montoDisp = montoDisp.replace("$", "");
			montoDisp = montoDisp.replace(/,/g, "");
			subtotal=parseFloat($("#mMontConIVA_"+nTr[0]).val())/(1+(0.01*parseInt(nTr[11])))
			if(parseFloat($("#mMontConIVA_"+nTr[0]).val())<=parseFloat(montoDisp) && parseFloat($("#mMontConIVA_"+nTr[0]).val())>0.0 && ($("#mDescuentoSinIVA_"+nTr[0]).val()<= subtotal )){
				cadenaLineaConsCant=cadenaLineaConsCant+token+nTr[0]+'-'+$("#mMontConIVA_"+nTr[0]).val()+'|'+$("#mDescuentoSinIVA_"+nTr[0]).val();
				token=",";
			}else{
				if(parseInt($("#mMontConIVA_"+nTr[0]).val(),10)>0 && parseFloat($("#mMontConIVA_"+nTr[0]).val())>parseFloat(montoDisp))
					swal("En la linea "+nTr[0]+" sobrepasaste el monto disponible.",{icon:"info",button: "Cerrar"});
				if($("#mDescuentoSinIVA_"+nTr[0]).val()> subtotal)
					swal("En la linea "+nTr[0]+" el descuento de : "+$("#mDescuentoSinIVA_"+nTr[0]).val()+" es mayor al subtotal: "+subtotal,{icon:"info",button: "Cerrar"});
			}
		}
		
	}
	return cadenaLineaConsCant;
}
function muestraDatos(){
	var varRFC=$('#cIdRFC').val();
	var cIdContrato=$("#cIdContrato").val();
	if(varRFC.toString().indexOf("&")>=0 ){//encodeURI
		varRFC=encodeURIComponent( $('#cIdRFC').val() );
	}
	var func = "'" + $("#cEjercicio").val() + "','" + $("#cIdUnidadEjecutora").val() +
		"','" + $("#cIdTipoProcedimiento").val() + "','" + $("#nIdConsecutivoProcedimiento").val() +
		"','"+ varRFC+"'"+",'"+ $('#cIdContrato').val()+"',"+ $("#nIdConsecutivoAdj").val();
	
	if(cIdContrato.indexOf("PLU")>=0 && $("#esProcesoNormPluri").val()==0){
		func = "'" + $("#cEjercicio").val() + "','" + $("#cIdUnidadEjecutora").val() +
		"','"+ varRFC  +"','"+ $('#cIdContrato').val()+"'";
	}
	var funcion="fn_PartidasPedidoContratoSolAbast";
	
	if($("#esDescentralizado").val() =='1'){
		funcion="fn_PartidasPedidoContratoSolAbastDesc";
		if(cIdContrato.indexOf("PLU")>=0 && $("#esProcesoNormPluri").val()==0){
			funcion="fn_PartidasContratoPluriSolAbastDesc";
		}
	}else{
		funcion="fn_PartidasPedidoContratoSolAbast";
		if(cIdContrato.indexOf("PLU")>=0 && $("#esProcesoNormPluri").val()==0){
			funcion="fn_PartidasContratoPluriSolAbast";
		}
	}
	if(cIdContrato.indexOf("CF")>=0){
		func = "'" + $("#cEjercicio").val() + "','" + $("#cIdUnidadEjecutora").val() +
			"','"+ varRFC  +"','"+ $('#cIdContrato').val()+"'";
		funcion="fn_mPartidasContratoPluriSolAbastContCap4";
	}
	if(cIdContrato.indexOf("CS")>=0 || cIdContrato.indexOf("CR")>=0){
		func = "'" + $("#cEjercicio").val() + "','" + $("#cIdUnidadEjecutora").val() +
			"','"+ varRFC  +"','"+ $('#cIdContrato').val()+"'";
		funcion="fn_mPartidasContratoArt25";
	}
	if($("#isConvEjercicioAnt").val()==1){
		func = "'" + $("#cEjercicio").val() + "','" + $("#cIdUnidadEjecutora").val() +
			"','"+ varRFC  +"','"+ $('#cIdContrato').val()+"'";
		funcion="fn_mPartidasContratoEjeAntSolAbastcontMod";
	}
	oTablePartidas = $("#tblPartidas").dataTable({
			bScrollCollapse: true,
    		bInfo: false,
    		//sScrollY : "100%",
			sScrollX: "100%",
			bAutoWith: true,
			bJQueryUI: true,
			bRetrive : true,
			bDestroy : true,
			sPaginationType: "full_numbers",
			oLanguage: {
				sProcessing: "Procesando...",
				sLengthMenu: "Mostrar _MENU_ registros",
				sZeroRecords: "No hay registros a mostrar",
				sEmptyTable: "No hay datos en la tabla",
				sLoadingRecords: "Cargando...",
				sInfo: "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty: "Registro 0 al 0 de 0",
				sInfoFiltered: "(filtado de _MAX_ registros)",
				sInfoPostFix: "",
				sInfoThousands: ",",
				sSearch: "Buscar:",
				oPaginate: {
					sFirst:    "Primero",
					sPrevious: "Ant.",
					sNext:     "Sigte.",
					sLast:     "&Uacute;ltimo"
				}
			},
			bServerSide: true,
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+funcion+"("+func+")",
			bJQueryUI: true,
			aaSorting: [[ 0, "asc" ]] ,
			aoColumns: [
				{sName: "nIdLineaConsolidado"},
				{sName: "cIdSubPartida"},
				{sName: "cIdCABM"},
				{sName: "cDescripcion"},
				{sName: "nCantidad"},
				{sName: "cantidadDisp"},
				{sName: "montoTotalLinea"},
				{sName: "cantidadAgregada"},
				{sName: "mMontoDescuentoSinIVA"},
				{sName: "idUnidad"},
				{sName: "nPorcentajeIVA"},//,bVisible: false
				{sName: "precioUnitario"}//,bVisible: false
			]
		});
}
function muestraDatosServicios(){
	var varRFC=$('#cIdRFC').val();
	if(varRFC.toString().indexOf("&")>=0 ){
		varRFC=encodeURIComponent( $('#cIdRFC').val() );
	}
	var cIdContrato=$("#cIdContrato").val();
	
	var func = "'" + $("#cEjercicio").val() + "','" + $("#cIdUnidadEjecutora").val() +
		"','" + $("#cIdTipoProcedimiento").val() + "','" + $("#nIdConsecutivoProcedimiento").val() +
		"','"+ varRFC  +"'"+",'"+ $('#cIdContrato').val()+"',"+ $("#nIdConsecutivoAdj").val();
	
	if(cIdContrato.indexOf("PLU")>=0 && $("#esProcesoNormPluri").val()==0){
		func = "'" + $("#cEjercicio").val() + "','" + $("#cIdUnidadEjecutora").val() +
		"','"+ varRFC  +"','"+ $('#cIdContrato').val()+"'";
	}	
	var funcion="fn_PartidasPedidoContratoSolAbast";
	if($("#esDescentralizado").val() =='1'){
		funcion="fn_PartidasPedidoContratoSolAbastDesc";
		if(cIdContrato.indexOf("PLU")>=0 && $("#esProcesoNormPluri").val()==0){
			funcion="fn_PartidasContratoPluriSolAbastDesc";
		}
	}else{
		funcion="fn_PartidasPedidoContratoSolAbast";
		if(cIdContrato.indexOf("PLU")>=0 && $("#esProcesoNormPluri").val()==0){
			funcion="fn_PartidasContratoPluriSolAbast";
		}
	}
	if($("#cIdTipoProcedimiento").val() ==''){
		func = "'" + $("#cEjercicio").val() + "','" + $("#cIdUnidadEjecutora").val() +
		"','" + $("#cIdTipoProcedimiento").val() + "','" + $("#nIdConsecutivoProcedimiento").val() +
		"','"+ varRFC  +"'"+",'"+ $('#cIdContrato').val()+"'";
		funcion="fn_PartidasPedidoContratoSolAbastA";
	}
	if(cIdContrato.indexOf("CF")>=0){
		func = "'" + $("#cEjercicio").val() + "','" + $("#cIdUnidadEjecutora").val() +
			"','"+ varRFC  +"','"+ $('#cIdContrato').val()+"'";
		funcion="fn_mPartidasContratoPluriSolAbastContCap4";
	}
	if(cIdContrato.indexOf("CS")>=0 || cIdContrato.indexOf("CR")>=0){
		func = "'" + $("#cEjercicio").val() + "','" + $("#cIdUnidadEjecutora").val() +
			"','"+ varRFC  +"','"+ $('#cIdContrato').val()+"'";
		funcion="fn_mPartidasContratoArt25";
	}
	if($("#isConvEjercicioAnt").val()==1){
		func = "'" + $("#cEjercicio").val() + "','" + $("#cIdUnidadEjecutora").val() +
			"','"+ varRFC  +"','"+ $('#cIdContrato').val()+"'";
		funcion="fn_mPartidasContratoEjeAntSolAbastcontMod";
	}
	//encodeURIComponent
	oTablePartidasServicios = $("#tblPartidasServicios").dataTable({
		bScrollCollapse: true,
		bInfo: false,
		//sScrollY : "100%",
		sScrollX: "100%",
		bAutoWith: true,
		bJQueryUI: true,
		bRetrive : true,
		bDestroy : true,
		sPaginationType: "full_numbers",
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay datos en la tabla",
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0",
			sInfoFiltered: "(filtado de _MAX_ registros)",
			sInfoPostFix: "",
			sInfoThousands: ",",
			sSearch: "Buscar:",
			oPaginate: {
				sFirst:    "Primero",
				sPrevious: "Ant.",
				sNext:     "Sigte.",
				sLast:     "&Uacute;ltimo"
			}
		},
		bServerSide: true,
		sAjaxSource:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + ( funcion+"(" + (func) +")" ) ,
		bJQueryUI: true,
		aaSorting: [[ 0, "asc" ]] ,
		"fnInitComplete" : function(settings, json) {
					if(remamenteAnticipo){
						tieneRemanenteAnticipo();
					}
			},
		aoColumns: [
			{sName: "nIdLineaConsolidado"},
			{sName: "cIdSubPartida"},
			{sName: "cIdCABM"},
			{sName: "cDescripcion"},
			{sName: "montoTotalLinea"},
			{sName: "montoConIVADisp"},
			{sName: "mMontoConIVA"},
			{sName: "mMontoDescuentoSinIVA"},
			{sName: "mMontoSinIVA",bVisible: false},
			{sName: "mMontoIVA",bVisible: false},
			{sName: "idUnidad"},
			{sName: "nPorcentajeIVA"},//,bVisible: false
			{sName: "precioUnitario"}//,bVisible: false
		]
	});
	
}
function creaActualizaRecep(oper){
	if(1==$("#isConvEjercicioAnt").val()){
		queryFormPost("creaActualizaRecepMatConConEjeAnt",  {async : false, 
			callback : function() 
			{
				if(($("#cIdTipoProcedimiento").val()=='PC' || $("#cIdTipoProcedimiento").val()=='PT')){
					muestraDatos();
				}else{
					muestraDatosServicios();
				}
				muestraLineas();
				//Guarda en la Bitácora
				guardaBitacora(oper+" RECEPCIÓN PEDCONTMOD EJEANT");
			}
		});
	}else{
		if($("#esDescentralizado").val() =='1'){
			if($("#cIdContrato").val().indexOf("CF")>=0){
				swal("Falta codificar.\n Notifica a soporte SAI.",{icon:"info",button: "Cerrar"});
				return;
			}
			else if($("#cIdContrato").val().indexOf("CS")>=0 || $("#cIdContrato").val().indexOf("CR")>=0){
				swal("Falta codificar.\n Notifica a soporte SAI.",{icon:"info",button: "Cerrar"});
				return;
			}else{
				queryFormPost("creaActualizaRecepMatDesc",  {async : false, 
					callback : function() 
					{
						if(($("#cIdTipoProcedimiento").val()=='PC' && $("#esCucopGasolina").val()=='')
							||($("#cIdTipoProcedimiento").val()=='PR'  && $("#cucopGasolina").val()=='') 
							||( $("#nServicio_A_Bienes").val()==1 ) ||$("#cIdTipoProcedimiento").val()=='PT'){
							muestraDatos();
						}else{
							muestraDatosServicios();
						}
						muestraLineas();
						//Guarda en la Bitácora
						guardaBitacora(oper+" RECEPCIÓN PEDCONT DESCENTRALIZADO");
					}
				});
				
			}
				
		}else{
			if($("#cIdContrato").val().indexOf("CF")>=0){
				queryFormPost("creaActualizaRecepMatContCap4",  {async : false, 
					callback : function() 
					{
						if($("#cIdTipoProcedimiento").val()=='PC' || $("#cIdTipoProcedimiento").val()=='PT'){
							muestraDatos();
						}else{
							muestraDatosServicios();
						}
						muestraLineas();
						//Guarda en la Bitácora
						guardaBitacora(oper+" RECEPCIÓN CONTRATO CAP4");
					}
				});	
			}else{
				queryFormPost("creaActualizaRecepMat",  {async : false, 
					callback : function() 
					{
						if(($("#cIdTipoProcedimiento").val()=='PC' && $("#esCucopGasolina").val()=='')
						||($("#cIdTipoProcedimiento").val()=='PR'  && $("#cucopGasolina").val()=='') 
						||( $("#nServicio_A_Bienes").val()==1 ) || $("#cIdTipoProcedimiento").val()=='PT'){
							muestraDatos();
						}else{
							muestraDatosServicios();
						}
						muestraLineas();
						//Guarda en la Bitácora
						guardaBitacora(oper+" RECEPCIÓN PEDCONT CENTRALIZADO");
					}
				});
			}
				
		}
	}
}
function onlyIntegers(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	return (keyPressed >= 48 && keyPressed <= 57);
}
function onlyNum(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	return (keyPressed >= 48 && keyPressed <= 57 || keyPressed==46);
}
function tieneRemanenteAnticipo(){
	var dtPartidas = $('#tblPartidasServicios').dataTable();
	var aTrs = dtPartidas.fnGetData();
	for ( var i=0 ; i<aTrs.length; i++ )     {  
			var nTr = dtPartidas.fnGetData(aTrs[i]);
			$("#nIdLineaConsolidado").val(i+1);
			queryFormPost("obtieneDatosRemaneteXlinea", {async : false});
			//aqui hay que actulizar valores del dataTAble
			//la variable que hay que sumarle a la cantidad que trae el DT es mSaldoRemantentexLinea
			
	}
}
function unFormatCurrency(str){
	str = str.replace("$","");
	str = str.replace(/\,/g,'');
	return parseFloat(str);
}
function unFormatCurrency2(fld){
	var str=$("#"+fld.id).val();
	str = str.replace("$","");
	str = str.replace(/\,/g,'');
	$("#"+fld.id).val(str);
}
function onlyMoney(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	if (keyPressed == 46 || keyPressed == 36)
		return true;
	return (keyPressed >= 48 && keyPressed <= 57);
}
function validaCampoServidorPublico(){
	var resp=true;
	if($("#ServidoresPublicos").val()==""){
		resp=false;
		swal("Favor de seleccionar un servidor público.",{icon:"info",button: "Cerrar"});
	}
	return resp;
}
function enviaSolAbaste(){
	var aTrs = $('#tblLineas').dataTable().fnGetNodes();
	if($("#cIdRecepMat").val()!='' && $("#cIdContrato").val()!=''&& aTrs.length>0){
		if($("#cIdSolAnticipo").val()!='' && $("#estadoAnticipo").val()!='3'){
			swal("No se pueden enviar recepciones, hasta que el anticipo se pague.",{icon:"info",button: "Cerrar"});
			return;
		}
		if(requiereAntentaNotaAlmacenVirtual()){
			capturaNotaAlmacenVirtual();
		}else{
			if(requiereAutorizacion()){
				capturaNota();
			}else{
				if(requiereVoboSCDB()){//requiere visto bueno de la SCDB
					sendAjaxVoBo();
				}else
					llamadaAjax(1,1);
			}
		}
	}else{
		swal("No hay nada que enviar.",{icon:"info",button: "Cerrar"});
	}
}
function sendAjaxVoBo(){
	$.ajax({
		url : '../../fiel/solicitaAutRM',
		dataType : 'json',
		type : "POST",
		beforeSend : function() {
			$.blockUI({
				message : 'Enviando, espere ...'
			});
		},
		data : {
			"cIDContrato" : $("#contratoCompranet").val(),
			"requiereAtentaNota" : 0,
			"cIDRecepMat" : $("#cIdRecepMat").val(),
			"dMotivoNota" : $("#dMotivoNota").val(),
			"cFolioNota" : -1,
			"cNumeroEmpleado" : $("#firmanteNota").val(),
			"nIdEntraAlmacen" : $("#esAlmacenCentral").val(),
			"nIdEstatusAtentaNotaFirmada" : $("#nIdEstatusAtentaNotaFirmada").val(),
			"cIdContratoDefinitivo" : $("#cIdContrato").val()
		},
		async : true,
		success : function(objResp) {
			
			var success = objResp.success;
			
			if( success == true ){
				swal({
					title: "",
					text: "Solicitud enviada a VoBO de la SCDB!",
					icon: "info",
					buttons: {
						confirm : "Cerrar"
					},
				}).then((continuar) => {
					window.location = "RecepcionMaterial.jsp?tab=1";
				});
			}else{
				swal(objResp.message,{icon:"info",button: "Cerrar"});
			}
			
			$.unblockUI();
			
		},
		error : function(xhr, textStatus, errorThrown) {
			try {
				var obj = eval(xhr.responseText);
				var msg = obj.errCause;
				swal("No fue posible registrar la recepcion debido al error: "+ msg,{icon:"info",button: "Cerrar"});
			} catch (e) {
				swal("Advertencia: " + xhr.responseText
						+ "\nEstatus: " + textStatus + "\n"
						+ errorThrown,{icon:"error",button: "Cerrar"});
			}
	
			$.unblockUI();
		}
	});
	
}
function llamadaAjax(oper,isRecepMat){
	$.ajax({url: "../../servlet/RecepcionServlet" , type:'post' , async: false
	,data:'operacion='+oper+'&cIdContrato='+$("#cIdContrato").val()+'&cIdSolAnticipo='+$("#cIdSolAnticipo").val()+'&cIdRecepMat='+$("#cIdRecepMat").val()
	+'&factAmort='+$("#factAmort").val()+'&isRecepMat='+isRecepMat
	, dataType: 'json', success: 
		function(j){
			var mensaje=j[0].MENSAJE;
			var resp=j[0].RESPUESTA;
			alert(mensaje)
			swal(mensaje,{icon:"info",button: "Cerrar"});
			if(resp){
				window.location = "RecepcionMaterial.jsp?tab=1";
			}
		}
	});
}
function requiereAutorizacion(){
	var requerido = true;
	queryFormPost({
		queryName: "rmRequiereNotaRead",
		async:false,
		callback:function(){
			requeridoVl = $("#requiereNota").val() == ""?"0":$("#requiereNota").val();
			requerido = ( parseInt(requeridoVl,10) > 0 );
		}
	});
	return requerido;
}
function requiereAntentaNotaAlmacenVirtual(){
	var requerido = false;
	if(esContratoDeBienes() && !partidaRestringida && $("#esAlmacenCentral").val()>1){
		requerido = true;
	}
	return requerido;
}
function requiereVoboSCDB(){
	var requerido = false;
	if(esContratoDeBienes() && !partidaRestringida && $("#esAlmacenCentral").val()==1){
		requerido = true;
	}
	return requerido;
}
function esContratoDeBienes(){
	var contratoBienes=false;
	var cidcont=$("#pedidoContratoCompromiso").val();
	if( (cidcont.indexOf("PE")<0)&&(($("#cIdTipoProcedimiento").val()=='PC' ) ||($("#cIdTipoProcedimiento").val()=='PR' ) || $("#cIdTipoProcedimiento").val()=='PT') ){
		contratoBienes=true;
	}
	return contratoBienes;
}
function elContratoTienePartidaRestringida(){
	var partRest = false;
	queryFormPost({
		queryName: "contratoPartidaRestringida",
		async:false,
		callback:function(){
			partRest = ( parseInt($("#tienePartidaRestringida").val(),10) > 0 );
		}
	});
	return partRest;
}