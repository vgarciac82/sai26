var mx =  {
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
						} 
/**
 * Realiza los cambios necesarios en la pantalla en caso de quue el contrato sea
 * un contrato descentralizado.
 */
function cambiaCC() {

	$("#cUnidadResponsable").val($("#cUnidadCompromiso").val());

	queryFormPost({
		queryName : "ccPorURRead",
		async : false,
		callback : function() {

			if ($("#cIdEntidadContable").val() == "") {
				alert("No se encuentra centro contable para la Unidad Ejecutora seleccionada. Notifique al administrador");
				return;
			}

			$("#cCentroContable").val($("#cIdEntidadContable").val());

			var vcontrato = $("#cIdContratoCompromiso").val();
			var esDescentralizado = ("1" == $("#nesdescentralizado").val());
			if ($("#chk_radicado").prop("checked"))
				tipoDisponible = " AND cuentaDisp = '82109' ";
			else
				tipoDisponible = " AND cuentaDisp = '82106' ";

			creaDT(vcontrato, esDescentralizado, tipoDisponible)

		}
	});

}

function creaDT(vcontrato, esDescentralizado, tipoDisponible) {
	// VGC201150508 Por la nueva dinamica de los contratos abiertos (Max y Min)
	// se lee para la unidad ejecutora seleccionada el importe de contrato para
	// esa unidad. Como aclaracion. El monto que se muestra en este caso y solo
	// en este caso NO es el del contrato. Es el monto comprometido inicial mas
	// las ampliaciones autorizadas a la UE.
	if (esDescentralizado) {
		queryFormPost({
			queryName : "maximoContratoUERead",
			async : false,
			callback : function() {
			}
		});
		
		queryFormPost({
			queryName:"tSaldoActualCompromisoAbierto", 
			async: false,
			callback:function(){
				if( $("#mComprometido").val() == "" )
					$("#mComprometido").val("0.00");
			}
		  });
		
		queryFormPost("tRemanenteContratoAbierto", {async:false});
		
		queryFormPost("readMaximoIncrementoCompromisoAbierto", {async:false});
	}
	
	// VGC20150416 Se cambio la vista, de manera que muestra el CC de la UE en
	// la EP. FALTA!!! Que tome los CC de las vistas para poderlo filtrar, ya
	// que un contrato puede tener varias EPs de diferentes UE lo que haria que
	// no se vean.

	$('#dt_compromiso')
			.dataTable(
					{
						bPaginate : false,
						//bLengthChange : false,
						bFilter : false,
						bInfo : false,
						oLanguage : mx,
						bAutoWidth : true,
						//bScrollX : 100,
						//bScrollY : 100,
						//bScrollCollapse : false,
						bJQueryUI : true,
						bDestroy : true,
						bServerSide : true,
						sAjaxSource : window.location.protocol
								+ "//"
								+ window.location.host
								+ "/"
								+ window.location.pathname.split("/")[1]
								+ "/crud?rt=t&ql=vContratoEPs&qw=cIdContrato='"
								+ vcontrato
								+ "' "
								+ (esDescentralizado ? (" and cIdEntidadContable = '"
										+ $("#cIdEntidadContable").val() + "'")
										: (" and cc = '"
												+ $("#cIdEntidadContable")
														.val() + "'")),

						aoColumns : [ {
							sName : "ClaveSIAFF"
						}, {
							sName : "ClaveInterna"
						}, {
							sName : "compromiso01"
						}, {
							sName : "compromiso02"
						}, {
							sName : "compromiso03"
						}, {
							sName : "compromiso04"
						}, {
							sName : "compromiso05"
						}, {
							sName : "compromiso06"
						}, {
							sName : "compromiso07"
						}, {
							sName : "compromiso08"
						}, {
							sName : "compromiso09"
						}, {
							sName : "compromiso10"
						}, {
							sName : "compromiso11"
						}, {
							sName : "compromiso12"
						}, {
							sName : "cIdContrato",
							bSearchable : false,
							bSortable : false,
							bVisible : false
						}, {
							sName : "EP",
							bSearchable : false,
							bSortable : false,
							bVisible : false
						} ]
					});

	// VGC20150416 Se cambio la vista, de manera que muestra el CC de la UE en
	// la EP. FALTA!!! Que tome los CC de las vistas para poderlo filtrar, ya
	// que un contrato puede tener varias EPs de diferentes UE lo que haria que
	// no se vean.
	$('#dt_comprometido')
			.dataTable(
					{
						bPaginate : false,
						//bLengthChange : false,
						bFilter : false,
						bInfo : false,
						oLanguage :mx,
						//bAutoWidth : false,
						//bScrollX : 100,
						//bScrollY : 100,
						//bScrollCollapse : true,
						bJQueryUI : true,
						bDestroy : true,
						bServerSide : true,
						sAjaxSource : window.location.protocol
								+ "//"
								+ window.location.host
								+ "/"
								+ window.location.pathname.split("/")[1]
								+ (esDescentralizado ? ("/crud?rt=t&ql=vcompromisocontratoepcentrocontable&qw=cIdContrato='"
										+ vcontrato
										+ "' AND cIdEntidadContable = '"
										+ $("#cIdEntidadContable").val() + "'")
										: ("/crud?rt=t&ql=vCompromisoContratoEP&qw=cIdContrato='"
												+ vcontrato + "'")),
						aoColumns : [ {
							sName : "ClaveSIAFF"
						}, {
							sName : "ClaveInterna"
						}, {
							sName : "MontoEnero"
						}, {
							sName : "MontoFebrero"
						}, {
							sName : "MontoMarzo"
						}, {
							sName : "MontoAbril"
						}, {
							sName : "MontoMayo"
						}, {
							sName : "MontoJunio"
						}, {
							sName : "MontoJulio"
						}, {
							sName : "MontoAgosto"
						}, {
							sName : "MontoSeptiembre"
						}, {
							sName : "MontoOctubre"
						}, {
							sName : "MontoNoviembre"
						}, {
							sName : "MontoDiciembre"
						}, {
							sName : "MontoAnual"
						}, {
							sName : "cIdContrato",
							bSearchable : false,
							bSortable : false,
							bVisible : false
						} ]
					});

	// VGC20150416 Se cambio la vista, de manera que muestra el CC de la UE en
	// la EP. FALTA!!! Que tome los CC de las vistas para poderlo filtrar, ya
	// que un contrato puede tener varias EPs de diferentes UE lo que haria que
	// no se vean.
	$('#dt_suficiencia')
			.dataTable(
					{
						bPaginate : false,
						//bLengthChange : false,
						bFilter : false,
						bInfo : false,
						oLanguage : mx,
						//bAutoWidth : false,
						//bScrollX : 100,
						//bScrollY : 100,
						//bScrollCollapse : true,
						bJQueryUI : true,
						bDestroy : true,
						bServerSide : true,
						sAjaxSource : window.location.protocol
								+ "//"
								+ window.location.host
								+ "/"
								+ window.location.pathname.split("/")[1]
								+ "/crud?rt=t&ql=vDisponibleContratoEP&qw=cIdContrato='"
								+ vcontrato
								+ "'"
								+ tipoDisponible
								+ (esDescentralizado ? (" and cIdEntidadContable = '"
										+ $("#cIdEntidadContable").val() + "'")
										: (" and cc = '"
												+ $("#cIdEntidadContable")
														.val() + "'")),
						aoColumns : [ {
							sName : "ClaveSIAFF"
						}, {
							sName : "ClaveInterna"
						}, {
							sName : "MontoEnero"
						}, {
							sName : "MontoFebrero"
						}, {
							sName : "MontoMarzo"
						}, {
							sName : "MontoAbril"
						}, {
							sName : "MontoMayo"
						}, {
							sName : "MontoJunio"
						}, {
							sName : "MontoJulio"
						}, {
							sName : "MontoAgosto"
						}, {
							sName : "MontoSeptiembre"
						}, {
							sName : "MontoOctubre"
						}, {
							sName : "MontoNoviembre"
						}, {
							sName : "MontoDiciembre"
						}, {
							sName : "MontoAnual"
						}, {
							sName : "cIdContrato",
							bSearchable : false,
							bSortable : false,
							bVisible : false
						} ]
					});
}



	function cargaContrato() {
	    if ($("#cIdContrato").val() != '') {
			$("#cIdContratoCompromiso").val( $("#cIdContrato").val() );
			$("#cIdContratoCompromiso").change();
			var vfolio = $("#nFolioCompromiso").val();
			var vcontrato = $("#cIdContrato").val();

			bCarga = true;
 			
 			$('#dt_compromiso').dataTable( {
				bPaginate : false,
				bLengthChange : false,
				bFilter : false,
				bInfo : false,
				oLanguage : mx,
				bAutoWidth : true,
				bScrollX : 100,
				bScrollY : 100,
				bScrollCollapse : false,
				bJQueryUI : true,
				bDestroy : true,
				bServerSide : true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vConsultaCompromiso&qw=cIdContrato='" + vcontrato + "' and nFolioCompromiso = " + vfolio,
				aoColumns: [
					{ sName: "ClaveSIAFF" },
					{ sName: "ClaveInterna" },
					{ sName: "compromiso01" },
					{ sName: "compromiso02" },
					{ sName: "compromiso03" },
					{ sName: "compromiso04" },
					{ sName: "compromiso05" },
					{ sName: "compromiso06" },
					{ sName: "compromiso07" },
					{ sName: "compromiso08" },
					{ sName: "compromiso09" },
					{ sName: "compromiso10" },
					{ sName: "compromiso11" },
					{ sName: "compromiso12" },
					{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }, 
					{ sName: "EP",	bSearchable: false,	bSortable: false, bVisible: false  }]
			} ) ;

		}
	}
	
	
	function validaAplicaPreComp(){
		var bAplica = true;
		
		$("#aplicaPreComp").val("");
		queryFormPost("validaAplicarPreCompFinanciero_Read", {async: false });
		
		if($("#aplicaPreComp").val() == "0"){
			 bAplica = false;
		}else if( $("#aplicaPreComp").val() == "1" && !esSAIAlterno){
			 
			 queryFormPost("esContratoAnterior",{async:false});
			 
			if ( $("#esSICOP").val() == 0) {
				if (esConvenioColaboracion()) {
					bAplica = true;
				}else{
					var mCompromiso = parseFloat( quitaFmt( $("#mComprometer").val() ) );
					
					if( mCompromiso  > 0 ){
									 
						 queryFormPost("montoMaxCompromisoDirecto",{async:false});
						 var mMaximoCD = parseFloat( $("#montoMaxCompromisoD").val() );
						 
						 if( mCompromiso <= mMaximoCD )
						 	bAplica = false;
						 else
						 	bAplica = true;
					 }else{
					 	 bAplica = false;
					 }
				}
			} else
				bAplica = true;
				
		}else if(esSAIAlterno){
			 bAplica = false;
		}
		
		return bAplica;
	}

	function esConvenioColaboracion(){
		var esConvenio = false;
		queryFormPost({ 
			queryName: "esConvenioColaboracion",
			async:false,
			callback:function(){
				if( parseInt( $("#esConvenio").val(), 10 ) > 0 )
					esConvenio = true;
			}
		});
		return esConvenio;
	}
	
	function procesar(){
		$( "#dialog-Procesando" ).dialog( "open" );
		return breturnVal;
	}


 	function fnAplicaMotor()
 	{
 		divAplica.innerHTML = "<iframe id='ifAplica' src='../gstnmngr/AppCont' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
 	}

  	function onPostSubmit(id_oper){
  		return true;
  	}
  	
  	
  	function ResponsableSiguiente(id_oper){

  		 if(id_oper==1)
  		 	return "CONSULTA_" + $("#cDocumento").val();
  				
  	}
  	  	
  	function OperacionSiguiente(id_oper){

  		if(id_oper==1)
  		 	return "consulta_compromiso";
  	}

	function onPostDisplay(){
		if ($("#cDocumentoHaplicado").val() == "S") {
			parent.document.getElementById("pb_send").disabled = false;
			parent.document.getElementById("pb_send").click();
		}
	}


	
	
	function editRow ( oTable, nRow )
	{
	    var aData = oTable.fnGetData(nRow[0].rowIndex -1);

	    queryFormPost("CompromisoNegativoRead", {async: false });
	    queryFormPost("CompromisoPositivoRead", {async: false });
	    
	    if($("#compromisoNegativo").val()>0){
			$("#trNegativo").show();
			$("#trPositivo").hide();
			$("#trDeshabilitado").hide();
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[2].innerHTML = '<input style="width: 100%" type="text" id="mes01-' + nRow[0].rowIndex + '" name="mes01" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[2]+'" onKeyPress="return(onlyNumbers(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[3].innerHTML = '<input style="width: 100%" type="text" id="mes02-' + nRow[0].rowIndex + '" name="mes02" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[3]+'" onKeyPress="return(onlyNumbers(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[4].innerHTML = '<input style="width: 100%" type="text" id="mes03-' + nRow[0].rowIndex + '" name="mes03" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[4]+'" onKeyPress="return(onlyNumbers(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[5].innerHTML = '<input style="width: 100%" type="text" id="mes04-' + nRow[0].rowIndex + '" name="mes04" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[5]+'" onKeyPress="return(onlyNumbers(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[6].innerHTML = '<input style="width: 100%" type="text" id="mes05-' + nRow[0].rowIndex + '" name="mes05" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[6]+'" onKeyPress="return(onlyNumbers(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[7].innerHTML = '<input style="width: 100%" type="text" id="mes06-' + nRow[0].rowIndex + '" name="mes06" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[7]+'" onKeyPress="return(onlyNumbers(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[8].innerHTML = '<input style="width: 100%" type="text" id="mes07-' + nRow[0].rowIndex + '" name="mes07" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[8]+'" onKeyPress="return(onlyNumbers(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[9].innerHTML = '<input style="width: 100%" type="text" id="mes08-' + nRow[0].rowIndex + '" name="mes08" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[9]+'" onKeyPress="return(onlyNumbers(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[10].innerHTML = '<input style="width: 100%" type="text" id="mes09-' + nRow[0].rowIndex + '" name="mes09" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[10]+'" onKeyPress="return(onlyNumbers(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[11].innerHTML = '<input style="width: 100%" type="text" id="mes10-' + nRow[0].rowIndex + '" name="mes10" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[11]+'" onKeyPress="return(onlyNumbers(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[12].innerHTML = '<input style="width: 100%" type="text" id="mes11-' + nRow[0].rowIndex + '" name="mes11" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[12]+'" onKeyPress="return(onlyNumbers(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[13].innerHTML = '<input style="width: 100%" type="text" id="mes12-' + nRow[0].rowIndex + '" name="mes12" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[13]+'" onKeyPress="return(onlyNumbers(event))">'; 
		}else if($("#compromisoPositivo").val()>0){
			$("#trPositivo").show();
			$("#trNegativo").hide();
			$("#trDeshabilitado").hide();
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[2].innerHTML = '<input style="width: 100%" type="text" id="mes01-' + nRow[0].rowIndex + '" name="mes01" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[2]+'" onKeyPress="return(onlyNumbersPositivo(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[3].innerHTML = '<input style="width: 100%" type="text" id="mes02-' + nRow[0].rowIndex + '" name="mes02" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[3]+'" onKeyPress="return(onlyNumbersPositivo(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[4].innerHTML = '<input style="width: 100%" type="text" id="mes03-' + nRow[0].rowIndex + '" name="mes03" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[4]+'" onKeyPress="return(onlyNumbersPositivo(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[5].innerHTML = '<input style="width: 100%" type="text" id="mes04-' + nRow[0].rowIndex + '" name="mes04" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[5]+'" onKeyPress="return(onlyNumbersPositivo(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[6].innerHTML = '<input style="width: 100%" type="text" id="mes05-' + nRow[0].rowIndex + '" name="mes05" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[6]+'" onKeyPress="return(onlyNumbersPositivo(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[7].innerHTML = '<input style="width: 100%" type="text" id="mes06-' + nRow[0].rowIndex + '" name="mes06" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[7]+'" onKeyPress="return(onlyNumbersPositivo(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[8].innerHTML = '<input style="width: 100%" type="text" id="mes07-' + nRow[0].rowIndex + '" name="mes07" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[8]+'" onKeyPress="return(onlyNumbersPositivo(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[9].innerHTML = '<input style="width: 100%" type="text" id="mes08-' + nRow[0].rowIndex + '" name="mes08" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[9]+'" onKeyPress="return(onlyNumbersPositivo(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[10].innerHTML = '<input style="width: 100%" type="text" id="mes09-' + nRow[0].rowIndex + '" name="mes09" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[10]+'" onKeyPress="return(onlyNumbersPositivo(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[11].innerHTML = '<input style="width: 100%" type="text" id="mes10-' + nRow[0].rowIndex + '" name="mes10" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[11]+'" onKeyPress="return(onlyNumbersPositivo(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[12].innerHTML = '<input style="width: 100%" type="text" id="mes11-' + nRow[0].rowIndex + '" name="mes11" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[12]+'" onKeyPress="return(onlyNumbersPositivo(event))">'; 
			$("#dt_compromiso").children().children()[nRow[0].rowIndex].children[13].innerHTML = '<input style="width: 100%" type="text" id="mes12-' + nRow[0].rowIndex + '" name="mes12" onchange="valSufic(this)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[13]+'" onKeyPress="return(onlyNumbersPositivo(event))">'; 
		}else{
			$("#trDeshabilitado").show();
			$("#trNegativo").hide();
			$("#trPositivo").hide();
		}
	}


	function quitaFmt( val ) {
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val ==''? "0.00": val;
	}	
	
	function Sinfrmt( fld )
	{
	   	var valcol = fld.value ;
	   	var vcompr = $("#mComprometer").val();
	   	vcompr = quitaFmt( vcompr );
	   	valcol = quitaFmt( valcol );
		$("#" + fld.id).val( valcol );
	   	fld.select();
		$("#mComprometer").val( parseFloat( vcompr ) - parseFloat( valcol ) );
		$("#mComprometer").formatCurrency();
	}

	function cambiafrmt( fld )
	{
	   	var vcompr = $("#mComprometer").val();
	   	var vfld = $("#" + fld.id).val();
	   	if (vfld == "")
	   		vfld = '0';
		vcompr = quitaFmt( vcompr );
	   	$("#mComprometer").val( parseFloat(vcompr) + parseFloat( vfld ) );
		$("#" + fld.id).formatCurrency();
		$("#mComprometer").formatCurrency();
	}

	function valSufic( fld ) { 
		if ($('#dt_suficiencia').dataTable().fnGetData().length==0){
			$("#" + fld.id).val( "0" );
			alert("No existe registro de presupuesto disponible.");			
			return false;
		}
		
		var oTableLocal = $('#dt_suficiencia').dataTable( ); //Tabla que tiene los disponibles.
		var oTableComp = $('#dt_compromiso').dataTable( ); //Tabla que tiene los disponibles.
		
		var nren = parseInt( fld.id.substring(fld.id.lastIndexOf("-") + 1), 10) -1;
		var ncol = parseInt( fld.id.substring(5, 3), 10 ) + 1 ;
		
		var nrenC = parseInt( fld.id.substring(fld.id.lastIndexOf("-") + 1), 10) -1;
		var ncolC = parseInt( fld.id.substring(5, 3), 10 ) + 1 ;
		
		var aData = oTableLocal.fnGetData( nren );
		var aDataC = oTableComp.fnGetData( nrenC );
			
		var valor = $("#" + fld.id).val(); //Valor del monto a comprometer en el mes 
		
		if (valor == "") {
			valor = '0';
			$("#" + fld.id).val( "0" );
			return true;
		}
		
		valor = parseFloat(valor);
		
		if( valor >= 0 ){
			var disponibleMes = parseFloat( quitaFmt( aData[ ncol ] ) ); //Monto disponible en el mes
			var epValida = aData[ 0 ] + "." + aData[ 1 ];
			
			if(validaComprometidoRadicado(epValida)){
				return validaAmpliacion( valor, disponibleMes, fld.id );
			}else{
				$("#" + fld.id).val( "0" );
				return false;
			}
		
		}else{
			
			var epValidar = aDataC[ 0 ] + "." + aDataC[ 1 ];
			var mesValidar = ncolC - 1;
			
			return validaReduccion( valor, epValidar, mesValidar, fld.id );
			
		}
	}


/* Get the rows which are currently selected */
function fnGetSelected( oTableLocal )
{
	var aReturn = new Array();
	var aTrs = oTableLocal.fnGetNodes();
	
	for ( var i=0 ; i<aTrs.length ; i++ )
	{
			aReturn.push( aTrs[i] );
			editRow ( oTableLocal, aReturn );
			aReturn.shift();
	}
	return aReturn;
}

function cmdImprimir(elFormato) {
	return 
}

function onlyNumbers(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	var strCheck = '-0123456789.';

	var key = String.fromCharCode( keyPressed );
	if (strCheck.indexOf( key ) == -1)
		return false; // Valida que sea numero y punto decimal

	return true ;
}

function onlyNumbersPositivo(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	var strCheck = '0123456789.';

	var key = String.fromCharCode( keyPressed );
	if (strCheck.indexOf( key ) == -1)
		return false; // Valida que sea numero y punto decimal

	return true ;
}

/**
 * Valida el monto que se requiere ampliar.
 *
 * Para que se pueda ampliar el compromiso, se debe cumplir las siguientes reglas.
 * 1) No puedo ampliar mas del disponible que tengo en el mes que quiero 
 * ampliar. Es decir, si en Enero tengo 20.00 de disponible y quiero ampliar 
 * 30.00 no lo permitira. Mi maximo por mes a ampliar es el total disponible en
 * es mes.
 *
 * 2) Una vez que valide que no he sobregirado el disponible del mes en el que
 * deseo ampliar, la segunda validacion es que, lo que he comprometido, mas el
 * valor no sobrepase el remanente del contrato. El remanente del contrato se 
 * define como el total del contrato menos los pagos realizados menos el 
 * comprometido ya aplicado. 
 */
function validaAmpliacion(valorAmpliacion, disponibleMes, inputID){
	
	if( reduccionesCapturadas() ){
		alert("No se pueden combinar movimientos de reduccion de compromiso y ampliacion.");
		$("#" + inputID).val( "0" );
    	return false;
	}
	// compromisoActual. Contiene la suma del remanente del compromiso mas los
	// incrementos capturados al momento.
	var compromisoActual = parseFloat( quitaFmt(  $("#mComprometer").val() ) );
	
	//compromiso. Es la cantidad que quedaria comprometida si no se revasan los montos.
	var compromiso =  parseFloat( (valorAmpliacion + compromisoActual).toFixed(2) );
	
	//disponibleMes. Es el recurso que tengo disponible al mes. No se puede superar
	var disponible = parseFloat( disponibleMes ); 
	
	//topeCompromiso. Maximo al que puede recurrir 
	var topeCompromiso =  parseFloat( quitaFmt(  $("#maximoIncremento").val() ) );
	
	/* 
	 * ========================================================================
	 *                            Validacion 1
	 * ========================================================================
	 */
	if( valorAmpliacion > disponibleMes ){
		alert("No puede comprometer mas recurso del disponible en el mes:. Disponible [$" + disponible + "] monto a comprometer: [$" + valorAmpliacion + "]" );
		$("#" + inputID).val( "0" );
		return false;
	}
	/* 
	 * ========================================================================
	 *                            Validacion 2
	 * ========================================================================
	 */
	else if( compromiso > topeCompromiso ){
		alert("Con el recurso a comprometer [$" + valorAmpliacion + "] sobrepasa el saldo actual del contrato [$" + topeCompromiso + "]" );
		$("#" + inputID).val( "0" );
		return false;
	}
		
	return true;
	
}

/**
 * Valida el monto que se requiere reducir.
 *
 * Para que se pueda reducir el compromiso, se debe cumplir las siguientes reglas.
 *
 * 1) No puedo reducir mas del comprometido  que tengo en el mes que quiero 
 * reducir. Es decir, si en Enero tengo 20.00 de compromiso y quiero reducir 
 * 30.00 no lo permitira. Mi maximo por mes a reducir es el total comprometido en
 * es mes. A este valor ya se han descontado los pagos realizados.
 *
 * 2) Una vez que valide que no he sobrepasado el monto comprometido del mes en el que
 * deseo reducir, la segunda validacion es que, lo que he reducido, mas el
 * valor no deje en rojo el remanente del contrato. El remanente del contrato se 
 * define como el total del contrato menos los pagos realizados menos el 
 * comprometido ya aplicado. 
 */
function validaReduccion(valorReduccion, epValidar, mesValidar, fldId){
	
	if( ampliacionesCapturadas() ){
		alert("No se pueden combinar movimientos de reduccion de compromiso y ampliacion.");
		$("#" + fldId).val( "0" );
    	return false;
	}
	// compromisoActual. Contiene la suma del remanente del compromiso mas los
	// decrementos capturados al momento.
	var compromisoActual = parseFloat( quitaFmt(  $("#mComprometer").val() ) );
	
	//compromiso. Es la cantidad que quedaria comprometida si no se revasan los
	//montos.
	var compromiso =  valorReduccion + compromisoActual;
	
	//saldoCompromiso. Maximo que se puede reducir 
	var saldoCompromiso =  parseFloat( quitaFmt(  $("#mComprometido").val() ) );
	
	/* 
	 * ========================================================================
	 *                            Validacion 1
	 * ========================================================================
	 */
	
	$("#EPValida").val( epValidar );
	$("#MesValida").val( mesValidar );	
	$("#mImporteEPMes").val("0");
	
	var cCentroCtbleRespaldo = $("#cIdEntidadContable").val();
	if( esAdmin  )
		$("#cIdEntidadContable").val("-1");
	
	queryFormPost("tValidaContratoCompromisoEPMesRead", {async: false });

	$("#cIdEntidadContable").val(cCentroCtbleRespaldo);
	
	if ( parseFloat( $("#mImporteEPMes").val() ) + valorReduccion < 0 ) {

    	alert("No es posible cancelar mas del saldo del compromiso mensual. Comprometido [$" + $("#mImporteEPMes").val() + "] a cancelar [$" + valorReduccion + "]" );
    	$("#" + fldId).val( "0" );
    	return false;
    	
	} 
		
	/* 
	 * ========================================================================
	 *                            Validacion 2
	 * ========================================================================
	 */
	else if( compromiso  + saldoCompromiso < 0 ){
		alert("Con el recurso a comprometer [$" + valorReduccion + "] el compromiso sera negativo." );
		$("#" + inputID).val( "0" );
		return false;
	}
		
	return true;
	
}

function reduccionesCapturadas(){
	var reduccionesCapturadas = false;
	
	var inputs = $("#dt_compromiso input").each(
		function(){
			var valorInpt = parseFloat( $(this).val() == "" ? "0.0" : quitaFmt( $(this).val() ) );
			if( valorInpt < 0 )
				reduccionesCapturadas = true;
		}
	);
	
	return reduccionesCapturadas;
}


function ampliacionesCapturadas(){
	var ampliacionesCapturadas = false;
	
	var inputs = $("#dt_compromiso input").each(
		function(){
			var valorInpt = parseFloat( $(this).val() == "" ? "0.0" : quitaFmt( $(this).val() ) );
			if( valorInpt > 0 )
				ampliacionesCapturadas = true;
		}
	);
	
	return ampliacionesCapturadas;
}

function validaComprometidoRadicado(epValida){
	var bReturn = true;
	var comprometido = parseFloat( quitaFmt(  $("#mComprometido").val() ) );
	$("#EPValida").val(epValida);
	var cFF = "";
	
	cFF = epValida.substring(39, 40);
	
	if ($("#chk_radicado").prop("checked")){
	
		if(cFF != "4"){
			queryFormPost("tieneComprometidoRadicadoRead", {async: false });
			
			if(comprometido > 0 && $("#tieneCOMPRadicado").val() == "0"){
			
				$("#mComprometidoFiscal").val("");
				queryFormPost("validaComprometidoFiscal_Read", {async: false });
				
				var mComprometidoFiscal = parseFloat( $("#mComprometidoFiscal").val() );
				
				if(mComprometidoFiscal > 0){
					bReturn = false;
					alert("La EP que esta editando tiene Comprometido de DISPONIBLE NETO.\nFavor de liberar el compromiso y pasar el monto a DISPONIBLE RADICADO.");
				}
			}
		}		
		$("#tieneCOMPRadicado").val("");
	}
	
	$("#EPValida").val("");
	
	return bReturn;
}

var contrato = "";
/*
function validaEP_IP(ep){
	var bAplica = true;
	var cFF = "";	
	cFF = ep.substring(39, 40);
	
	contrato = $("#cIdContratoCompromiso").val().substring(0,2);
	$("#aplicaPreComp").val("1");
	
	if(cFF == "4"){		
		bAplica = false;
		$("#aplicaPreComp").val("0");		
	}else if(contrato == "PE"){
		bAplica = false;
		$("#aplicaPreComp").val("0");
	}else if(esSAIAlterno){
		bAplica = false;
		$("#aplicaPreComp").val("0");
	}else {
		queryFormPost("esContratoAnterior",{async:false});
		if ( parseInt(  $("#esSICOP").val(), 10)  > 0) {
			bAplica = true;
			$("#aplicaPreComp").val("1");
		}else{
			if (esConvenioColaboracion()) {
				bAplica = true;
			}else{		
				var mCompromiso = parseFloat( quitaFmt( $("#mComprometer").val() ) );
				
				if( mCompromiso  > 0 ){
								 
					 queryFormPost("montoMaxCompromisoDirecto",{async:false});
					 var mMaximoCD = parseFloat( $("#montoMaxCompromisoD").val() );
					 
					 if( mCompromiso <= mMaximoCD){
					 	bAplica = false;
					 	$("#aplicaPreComp").val("0");
					 }else{
					 	bAplica = true;
					 	$("#aplicaPreComp").val("1");
					 }
				 }else{
				 	 bAplica = false;
				 	 $("#aplicaPreComp").val("0");
				 }
			}
		}
	}
	
	return bAplica;
}
*/
function activaDesactivaRadicado(valor){

	if(valor){
		$("#divRadicado").show();
		$("#chk_radicado").attr("checked", valor);
		$("#chk_radicado").change();
		document.getElementById("chk_radicado").disabled = true;
	}else{
		$("#divRadicado").hide();
		$("#chk_radicado").attr("checked", valor);
		$("#chk_radicado").change();
	}
}

function cmdImprimirOficio(){
		 
	window.open(
				"../admin/SeguridadCatalogos?"
				+ "catalogo=CONTRARECIBO"
				+ "&accion=run"
				+ "&rn=rptContratoFederalizado.jasper"
				+ "&nFolioCompromiso= " + $("#nFolioCompromiso").val(), 			
				"popacuse",
				"scrollbars=1, resizable=yes, width=1024, height=768"
				);
											
}

function creaDlgFirmantes(){
	$( "#dialog-firmantes" ).dialog( {
		autoOpen : false,
		height : 490,
		width : 700,
		modal : true,
		open : function(){
			/*$(".Firmante").each(function(){
				alert( $(this).attr("name") );
		        $(this).removeAttr("readOnly");		         
		    });*/ 
		},
		buttons : {
			"Aceptar" : function() {
				if( validaCapturaFirmantes() ){
					setValoresFirmantes();
					if( confirm("Esta seguro de Guardar ï¿½ Actualizar los firmantes?") ){
						creaActualizaFirmantes();
						cmdImprimirOficio();
						$(this).dialog( "close" );						
					}
				}
			},
			"Cancelar" : function() {
				$(this).dialog( "close" );
			}
		},
		close : function() {}
	} );

	$("#oficioDelegatorioCaptura").hide();
	$("#oficioDelegatorioVoBo").hide();
	
	$( "#dFechaOficio" ).datepicker({
		showOn: "button",
		buttonImage: "images/calendar.gif",
		buttonImageOnly: true
	});
	
	$( "#dFechaOficioVoBo" ).datepicker({
		showOn: "button",
		buttonImage: "images/calendar.gif",
		buttonImageOnly: true
	});
}

function abrirDlgFirmantes(){	
	$("#dialog-firmantes").dialog("open");
	queryFormPost("firmantesModuloRead", {async: false });
	queryFormPost("firmantesModuloReadAut", {async: false });
	queryFormPost("tPagoFirmanteDelagatorioRead", {async: false }); // Para los firmantes de oficio delegatorio en caso de que existan.
	queryFormPost("tPagoFirmanteDelegatorioVoBoRead", {async: false }); // Para los firmantes de oficio delegatorio VoBo en caso de que existan.   
}

function validaCapturaFirmantes() {
	
	if( $( "#cNombreVoBo" ).val() == "" ) {
		alert( "Falta Ingresar Nombre en Datos Vï¿½ Bï¿½" );
		return false;
	} else if( $( "#cPaternoVoBo" ).val() == "" ) {
		alert( "Falta Ingresar Apellido Paterno en Datos Vï¿½ Bï¿½" );
		return false;
	} else if( $( "#cMaternoVoBo" ).val() == "" ) {
		alert( "Falta Ingresar Apellido Materno en Datos Vï¿½ Bï¿½" );
		return false;
	} else if( $( "#cPuestoVoBo" ).val() == "" ) {
		alert( "Falta Ingresar Puesto en Datos Vï¿½ Bï¿½" );
		return false;
	}

	if( $( "#cNombreAut" ).val() == "" ) {
		alert( "Falta Ingresar Nombre en Datos Autorizar" );
		return false;
	} else if( $( "#cPaternoAut" ).val() == "" ) {
		alert( "Falta Ingresar Apellido Paterno en Datos Autorizar" );
		return false;
	} else if( $( "#cMaternoAut" ).val() == "" ) {
		alert( "Falta Ingresar Apellido Materno en Datos Autorizar" );
		return false;
	} else if( $( "#cPuestoAut" ).val() == "" ) {
		alert( "Falta Ingresar Puesto en Datos Autorizar" );
		return false;
	}

	if( $( "#cNombreEla" ).val() == "" ) {
		alert( "Falta Ingresar Nombre en Datos Elabora" );
		return false;
	} else if( $( "#cPaternoEla" ).val() == "" ) {
		alert( "Falta Ingresar Apellido Paterno en Datos Elabora" );
		return false;
	} else if( $( "#cMaternoEla" ).val() == "" ) {
		alert( "Falta Ingresar Apellido Materno en Datos Elabora" );
		return false;
	} else if( $( "#cPuestoEla" ).val() == "" ) {
		alert( "Falta Ingresar Puesto en Datos Elabora" );
		return false;
	}

	if( $( "#oficioDelegatorio" ).prop( "checked" ) ) {
		if( $( "#cFolioOficio" ).val() == "" ) {
			alert( "Falta Ingresar el folio de Oficio." );
			return false;
		} else if( $( "#dFechaOficio" ).val() == "" ) {
			alert( "Falta Ingresar la fecha del Oficio." );
			return false;
		} else if( $( "#cNombreTitular" ).val() == "" ) {
			alert( "Falta Ingresar Nombre del Titular." );
			return false;
		} else if( $( "#cApellidoPaternoTitular" ).val() == "" ) {
			alert( "Falta Ingresar Apellido Paterno del Titular." );
			return false;
		} else if( $( "#cApellidoMaternoTitular" ).val() == "" ) {
			alert( "Falta Ingresar Apellido Materno del Titular." );
			return false;
		} else if( $( "#cPuestoTitular" ).val() == "" ) {
			alert( "Falta Ingresar Puesto del Titular." );
			return false;
		}

	}
	
	if( $( "#oficioDelegatorioVoBo" ).prop( "checked" ) ) {
		if( $( "#cFolioOficioVoBo" ).val() == "" ) {
			alert( "Falta Ingresar el folio de Oficio." );
			return false;
		} else if( $( "#dFechaOficioVoBo" ).val() == "" ) {
			alert( "Falta Ingresar la fecha del Oficio." );
			return false;
		} else if( $( "#cNombreTitularVoBo" ).val() == "" ) {
			alert( "Falta Ingresar Nombre del Titular." );
			return false;
		} else if( $( "#cApellidoPaternoTitularVoBo" ).val() == "" ) {
			alert( "Falta Ingresar Apellido Paterno del Titular." );
			return false;
		} else if( $( "#cApellidoMaternoTitularVoBo" ).val() == "" ) {
			alert( "Falta Ingresar Apellido Materno del Titular." );
			return false;
		} else if( $( "#cPuestoTitularVoBo" ).val() == "" ) {
			alert( "Falta Ingresar Puesto del Titular." );
			return false;
		}
	}
	
	return true;
	
}
	
function setValoresFirmantes() {

	$( "#cNombreVo" ).val( $( "#cNombreVoBo" ).val() );
	$( "#cPaternoVo" ).val( $( "#cPaternoVoBo" ).val() );
	$( "#cMaternoVo" ).val( $( "#cMaternoVoBo" ).val() );
	$( "#cPuestoVo" ).val( $( "#cPuestoVoBo" ).val() );

	$( "#cNombreA" ).val( $( "#cNombreAut" ).val() );
	$( "#cPaternoA" ).val( $( "#cPaternoAut" ).val() );
	$( "#cMaternoA" ).val( $( "#cMaternoAut" ).val() );
	$( "#cPuestoA" ).val( $( "#cPuestoAut" ).val() );

	$( "#cNombreE" ).val( $( "#cNombreEla" ).val() );
	$( "#cPaternoE" ).val( $( "#cPaternoEla" ).val() );
	$( "#cMaternoE" ).val( $( "#cMaternoEla" ).val() );
	$( "#cPuestoE" ).val( $( "#cPuestoEla" ).val() );

	if( $( "#oficioDelegatorio" ).prop( "checked" ) ) {
		$( "#cFolioOficioAux" ).val( $( "#cFolioOficio" ).val() );
		$( "#dFechaOficioAux" ).val( $( "#dFechaOficio" ).val() );
		$( "#cNombreTitularAux" ).val( $( "#cNombreTitular" ).val() );
		$( "#cApellidoPaternoTitularAux" ).val( $( "#cApellidoPaternoTitular" ).val() );
		$( "#cApellidoMaternoTitularAux" ).val( $( "#cApellidoMaternoTitular" ).val() );
		$( "#cPuestoTitularAux" ).val( $( "#cPuestoTitular" ).val() );
		$( "#tipoSuplenciaAux" ).val( $( "#tipoSuplencia" ).val() );
	}
	
	if( $( "#oficioDelegatorioVoBo" ).prop( "checked" ) ) {
		$( "#cFolioOficioVoBoAux" ).val( $( "#cFolioOficioVoBo" ).val() );
		$( "#dFechaOficioVoBoAux" ).val( $( "#dFechaOficioVoBo" ).val() );
		$( "#cNombreTitularVoBoAux" ).val( $( "#cNombreTitularVoBo" ).val() );
		$( "#cApellidoPaternoTitularVoBoAux" ).val( $( "#cApellidoPaternoTitularVoBo" ).val() );
		$( "#cApellidoMaternoTitularVoBoAux" ).val( $( "#cApellidoMaternoTitularVoBo" ).val() );
		$( "#cPuestoTitularVoBoAux" ).val( $( "#cPuestoTitularVoBo" ).val() );
		$( "#tipoSuplenciaVoBoAux" ).val( $( "#tipoSuplenciaVoBo" ).val() );
	}
}

function creaActualizaFirmantes(){	
	var msn = "";

	if($("#firmanteExiste").val() == "Existe"){
								
		// Actualiza informacion
		queryFormPost({	queryName : "firmanteModuloUpdate, firmanteModuloElaboraUpdate", 
							async : false, 
						 callback : function(){ 									
									msn = "Actualizado Correctamente Firmantes"; } 
				  	});
	} else{
		
		queryFormPost({	queryName : "firmanteModuloCreate, firmanteModuloElaboraUpdate", 
							async : false, 
						 callback : function(){ 			
									msn = "Guardado Correctamente Firmantes";
								} 
				  });
	}
	
	alert(msn);
	
	if ($("#oficioDelegatorio").prop("checked")){
		if($("#firmanteOficioExiste").val() == "Existe"){
			queryFormPost({	queryName : "tPagoFirmanteDelagatorioUpdate", 
								async : false, 
							 callback : function(){ 
									msn = "Firmantes Oficio Delegatorio actualizado correctamente.";
								} 
				  });									
		} else {
			queryFormPost({	queryName : "tPagoFirmanteDelagatorioCreate", 
								async : false, 
							 callback : function(){ 			
										msn = "Firmantes Oficio Delegatorio guardado correctamente.";
									} 
					  });
		}
		
		alert(msn);			
	}
	
	if( $( "#oficioDelegatorioVoBo" ).prop( "checked" ) ){
		if($("#firmanteOficioVoBoExiste").val() == "Existe"){
			queryFormPost({	queryName : "tPagoFirmanteDelegatorioVoBoUpdate", async : false, callback : function(){ 
									
									msn = "Firmantes Oficio Delegatorio VoBo actualizado correctamente.";
								} 
				  });									
		}else{
			queryFormPost({	queryName : "tPagoFirmanteDelegatorioVoBoCreate", async : false, callback : function(){ 
										
										msn = "Firmantes Oficio Delegatorio VoBo guardado correctamente.";
									} 
					  });
		}
			
		alert(msn);
	}	
	
}

function showDivOficio(esUpdate){
	var cmpName = "oficioDelegatorio" + (esUpdate?"Update":"");
	var divName = "oficioDelegatorioCaptura" + (esUpdate?"Update":"");
	if( $("#" + cmpName ).is(":checked") )
		$("#"+divName).show();
	else
		$("#"+divName).hide();
}

function showDivOficioVoBo(esUpdate){
	var cmpName = "oficioDeleVoBo" + (esUpdate?"Update":"");
	var divName = "oficioDelegatorioVoBo" + (esUpdate?"Update":"");
	if( $("#" + cmpName ).is(":checked") )
		$("#"+divName).show();
	else
		$("#"+divName).hide();
}