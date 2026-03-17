




//***************************************************************************** no son llamadas desde PolizaEvento**************************************************************

function Limpia() {
	if (toUpdate >= 0) {
		if (confirm('Se eliminar\u00E1 el movimiento que est\u00E1 editando \u00BFContinuar?')) {
			oTableCuen.fnDeleteRow(toUpdate);
			toUpdate = -1;
			renumeraTabla();
		} else {
			return;
		}
	}

	$("#Cargos").val(0);
	moneyFrmt("Cargos", $("#Cargos").val());

	$("#Abonos").val(0);
	moneyFrmt("Abonos", $("#Abonos").val());

	$("#nSubCuenta").val("");
	$("#nCuenta").val("");
	$("#dCuenta").val("");
	$("#cIDRFC").val("");
	$("#CTABAN").val("");
	$("#ALM").val("");
	ocultaSubCuentas();
	$("#idCuenta").val("");
}






function disableKeys(e) {
	var tecla = (document.all) ? e.keyCode : e.which;
	if (tecla == 8)
		return false;
	else
		return true;
}



function captura(nFolioDocPolizaParam,ctipoPoliza)
{
	  //alert("En captura");

	$("#id_caso_lbl").val($("#id_caso").val());
	nFolioDocPolizaParam=$("#id_caso_lbl").val();

	$('#PolTipo').val(ctipoPoliza);

	/*
	$('#pCuentas').dataTable({         
        			oLanguage: lengParams,
        			async: false,
					sAjaxSource:"../export/GeneraJsonTxt",
					"fnServerParams":  function ( aoData ) {
                     aoData.push( { "name": "more_data", "value": nFolioDocPolizaParam } );
                      },
					bProcessing: true,
					bJQueryUI: true,
					bAutoWidth : true,
					bRetrive: true,
					bDestroy: true,
		    		bPaginate: true,
		    		sScrollX: "100%",
		      		bPaginate : true,
       				bLengthChange : true,
					bInfo : true,
					bFilter : true,
					bSort : true,
					left : true			      		
				      		
		        });	
	*/

	if ( oTableCuen.length > 0 ) 	oTableCuen.fnAdjustColumnSizing();
}




function revision(nFolioDocPolizaParam,ctipoPoliza) {
	
    
	
    creaTablaCuentasRev();
	$('#pCuentasRev').dataTable().fnClearTable();
	$("#TcargosRev").val('$'+$("#Tcargos").val());
	$("#TabonosRev").val('$'+$("#Tabonos").val());
	$("#EFRev").val($("#EF").val());
	$("#fAplicacionRev").val($("#fAplicacion").val());
	$("#FolioPolizaRev").val($("#nFolioPolizaDef").val());
	$("#PolTipoRev").val(ctipoPoliza);
    $("#id_casoRev").val($("#id_caso").val());
	$("#hPolCtroContableRev").val($("#hPolCtroContable").val());
	//$("#cConceptoRev").val($("#cConcepto").val());

	var docrenglonRev = 1;

	var rows = oTableCuen.fnGetData();
	var rowCount = rows.length;
	
	nFolioDocPolizaParam=$("#id_casoRev").val();

		
	 $('#pCuentasRev').dataTable({         
		        			oLanguage: lengParams,
		        			async: false,
							sAjaxSource:"../export/GeneraJsonTxt",
							"fnServerParams":  function ( aoData ) {
                             aoData.push( { "name": "more_data", "value": nFolioDocPolizaParam } );
                              },
							bProcessing: true,
							bJQueryUI: true,
							bAutoWidth : true,
							bRetrive: true,
							bDestroy: true,
				    		bPaginate: true,
				    		sScrollX: "100%",
				      		bPaginate : true,
	           				bLengthChange : true,
							bInfo : true,
							bFilter : true,
							bSort : true,
							left : true			      		
				      		
		        });	
	
	
	Limpia();

}



function autorizacion(nFolioDocPolizaParam,ctipoPoliza) {
	
	
	creaTablaCuentasAut();
	$('#pCuentasAut').dataTable().fnClearTable();
	$("#TcargosAut").val('$'+$("#Tcargos").val());
	$("#TabonosAut").val('$'+$("#Tabonos").val());
	$("#cComentarios").val($("#cComentariosAut").val());
	queryFormPost("tdocPolizaComentarioCaptUpdate", {
		async : false
	});
	$("#EFAut").val($("#EF").val());
	$("#fAplicacionAut").val($("#fAplicacion").val());
	$("#FolioPolizaAut").val($("#nFolioPolizaDef").val());
	$("#PolTipoAut").val(ctipoPoliza);
	$("#id_casoAut").val($("#id_caso").val());
	$("#hPolCtroContableAut").val($("#hPolCtroContable").val());
	$("#cConceptoAut").val($("#cConcepto").val());
	var docrenglonRev = 1;

	//var rows = oTableCuen.fnGetData();
	//var rowCount = rows.length;

		nFolioDocPolizaParam=$("#id_casoAut").val();
		
	 $('#pCuentasAut').dataTable({         
		        			oLanguage: lengParams,
		        			async: false,
							sAjaxSource:"../export/GeneraJsonTxt",
							"fnServerParams":  function ( aoData ) {
                             aoData.push( { "name": "more_data", "value": nFolioDocPolizaParam } );
                              },
							bProcessing: true,
							bJQueryUI: true,
							bAutoWidth : true,
							bRetrive: true,
							bDestroy: true,
				    		bPaginate: true,
				    		sScrollX: "100%",
				      		bPaginate : true,
	           				bLengthChange : true,
							bInfo : true,
							bFilter : true,
							bSort : true,
							left : true			      		
				      		
		        });	
	
	
	Limpia();
	$("#Parciales").val("");
}



function CopiaPoliza() {
	$("#PolTipo").val($("#PolTipoDet").val());
	$("#PolOrigen").val($("#PolOrigenDet").val());

	$("#Tcargos").val(formatCurrency($("#mTotalCargo").val()));
	$("#TcargosLbl").text($("#Tcargos").val());

	$("#Tabonos").val(formatCurrency($("#mTotalAbono").val()));
	$("#TabonosLbl").text($("#Tabonos").val());

	$("#hPolCtroContable").val($("#hPolCtroContableDet2").val());

	$("#cConcepto").val($("#cConceptoDet").val());

	document.getElementById("esperar").style.visibility = "visible";
	document.getElementById("esperardet").style.visibility = "visible";
	$('#pCuentas').dataTable().fnClearTable();

	var elParametro = "nFolioPoliza=" + $("#polizaDet").val()
			+ " AND cTipoDocumento= '" + $("#PolOrigenDet").val()
			+ "' AND cCentroContable='" + $("#hPolCtroContableDet2").val()
			+ "' AND mMovimiento !=0 ";

	var szTabla = "TPOLIZAMOVIMIENTOS";
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : szTabla,
		Param : elParametro,
		MaxReg : "",
		ajax : 'false'
	}, function(j) {
		var abono = 0;
		var cargo = 0;
		var docrenglon = 1;
		var subcuenta1 = '';
		var arrlist = new Array();

		for ( var i = 0; i < j.length; i++) {
			if (j[i].Col2 == 'C') {
				cargo = j[i].Col3;
				abono = 0;
			} else {
				abono = j[i].Col3;
				cargo = 0;
			}

			if (j[i].Col1 != 'undefined' || j[i].Col1 != null) {
				j[i].Col1;
			} else {
				j[i].Col1 = ' ';
			}
			if (j[i].Col5 != 'undefined' || j[i].Col5 != null) {
				j[i].Col5;
			} else {
				j[i].Col5 = ' ';
			}

			arrlist[ i ] = [ docrenglon, j[i].Col0, j[i].Col4, j[i].Col1, j[i].Col5,
							formatCurrency(cargo), formatCurrency(abono),
							j[i].Col6 ];

			docrenglon++;
		}
		$('#pCuentas').dataTable().fnAddData( arrlist );

		$("#dialog-Procesando" ).dialog( "close" );

		renglon = docrenglon;
	});


}





function getSelectedText() {
	var txt = "";
	if (typeof window.getSelection != "undefined") {
		txt = window.getSelection();
	} else if (typeof document.selection != "undefined") {
		txt = document.selection.createRange().text;
	} else if (document.getSelection) {
		txt = document.getSelection();
	}
	return txt;
}




function editaCuenta(elmnt) {
	if (Number(elmnt.selectionEnd) > 0)
		clearSelection();
	else
		elmnt.select();
	$("#valSubCuentaCompar").val("");
	$("#dCuenta").val("");
	ocultaSubCuentas();

}

function clearSelection() {
	if (document.selection) {
		document.selection.empty();
	} else if (window.getSelection) {
		window.getSelection().removeAllRanges();
	}
}



function BuscarMovimiento() {
	$('#pMovimiento').dataTable().fnClearTable();

	document.getElementById("esperar").style.visibility = "visible";
	document.getElementById("esperardet").style.visibility = "visible";

	var elParametro = "";

	// =================================== TIPO DE POLIZA =====================
	elParametro = elParametro + " nPolizaAutomatica =  "
			+ $("#PolAutomatica").val();

	// =================================== Ejercicio Fical ====================
	elParametro = elParametro + " AND aEjercicioFiscal = '" + $("#EFC").val()
			+ "'";

	// =================================== Centro Contable ====================
	if ($("#hPolCtroContableC").val() != "") {
		elParametro = elParametro + " AND ";
		elParametro = elParametro + " cCentroContable = '"
				+ $("#hPolCtroContableC").val() + "'";
	}

	// =================================== Fecha de Captura ===================
	if ($("#fCapturaC").val() != "" && $("#fCaptura2C").val() != "") {
		elParametro = elParametro + " AND ";
		elParametro = elParametro
				+ " CONVERT(DATE, (CONVERT(VARCHAR, fcreacion, 103)), 103) >= "
				+ "CONVERT(DATE, (CONVERT(VARCHAR, '"
				+ $("#fCapturaC  ").val()
				+ "', 103)), 103)"
				+ " AND  CONVERT(DATE, (CONVERT(VARCHAR, fcreacion, 103)), 103)  <= CONVERT(DATE, (CONVERT(VARCHAR, '"
				+ $("#fCaptura2C  ").val() + "', 103)), 103)";

	} else if ($("#fCapturaC").val() != "") {
		elParametro = elParametro + " AND ";
		elParametro = elParametro
				+ " CONVERT(DATE, (CONVERT(VARCHAR, fcreacion, 103)), 103) >= CONVERT(DATE, (CONVERT(VARCHAR, '"
				+ $("#fCapturaC  ").val() + "', 103)), 103)";
	} else if ($("#fCaptura2C").val() != "") {
		elParametro = elParametro + " AND ";
		elParametro = elParametro
				+ " CONVERT(DATE, (CONVERT(VARCHAR, fcreacion, 103)), 103) <= CONVERT(DATE, (CONVERT(VARCHAR, '"
				+ $("#fCaptura2C  ").val() + "', 103)), 103)";
	}
	// =================================== Fecha de Aplicacion ================
	if ($("#fAplicacionC").val() != "" && $("#fAplicacion2C").val() != "") {

		elParametro = elParametro + " AND ";
		elParametro = elParametro
				+ " CONVERT(DATE, (CONVERT(VARCHAR, fAplicacion, 103)), 103) >= CONVERT(DATE, (CONVERT(VARCHAR, '"
				+ $("#fAplicacionC").val()
				+ "', 103)), 103) AND  CONVERT(DATE, (CONVERT(VARCHAR, fAplicacion, 103)), 103)  <= CONVERT(DATE, (CONVERT(VARCHAR, '"
				+ $("#fAplicacion2C").val() + "', 103)), 103)";

	} else if ($("#fAplicacionC").val() != "") {

		elParametro = elParametro + " AND ";
		elParametro = elParametro
				+ " CONVERT(DATE, (CONVERT(VARCHAR, fAplicacion, 103)), 103) >= CONVERT(DATE, (CONVERT(VARCHAR, '"
				+ $("#fAplicacionC").val() + "', 103)), 103)";

	} else if ($("#fAplicacion2C").val() != "") {

		elParametro = elParametro + " AND ";
		elParametro = elParametro
				+ " CONVERT(DATE, (CONVERT(VARCHAR, fAplicacion, 103)), 103) <= CONVERT(DATE, (CONVERT(VARCHAR, '"
				+ $("#fAplicacion2C").val() + "', 103)), 103)";
	}

	// =================================== No. de poliza ======================
	if ($("#polizaC").val() != "" && $("#poliza2C").val() != "") {

		elParametro = elParametro + " AND ";
		elParametro = elParametro + " nFolioPoliza >= " + $("#polizaC").val()
				+ " AND  nFolioPoliza <=	" + $("#poliza2C").val();

	} else if ($("#polizaC").val() != "") {

		elParametro = elParametro + " AND ";
		elParametro = elParametro + " nFolioPoliza >= " + $("#polizaC").val();

	} else if ($("#poliza2C").val() != "") {

		elParametro = elParametro + " AND ";
		elParametro = elParametro + " nFolioPoliza <= " + $("#poliza2C").val();

	}

	// ========================== Tipo de Poliza ==============================
	if ($("#PolTipoC").val() != "") {
		elParametro = elParametro + " 	AND ";
		elParametro = elParametro + " cTipoPoliza = '" + $("#PolTipoC").val()
				+ "'";

	}

	// ========================== Estatus de Poliza ===========================
	if ($("#PolStatus").val() != "") {
		elParametro = elParametro + " AND ";
		elParametro = elParametro + " DocHAplicado= '" + $("#PolStatus").val()
				+ "'";
	}

	// ========================== Origen de Poliza ============================
	if ($("#PolOrigen").val() != "") {
		elParametro = elParametro + " AND ";
		elParametro = elParametro + " ctipodocumento= '"
				+ $("#PolOrigen").val() + "'";
	}

	var szTabla = "BUSCARMOV_POLIZA";
	var arrlist = new Array();

	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : szTabla,
		Param : elParametro,
		MaxReg : "",
		ajax : 'false'
	}, function(j) {
		for ( var i = 0; i < j.length; i++) {
			arrlist[ i ] = [ j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4,
							j[i].Col5, j[i].Col6, j[i].Col7, j[i].Col8 ];

		}
		$('#pMovimiento').dataTable().fnAddData( arrlist );

		$("#dialog-Procesando" ).dialog( "close" );

	});
}


function limpiaPantallaBusqueda() {
	$("#fCapturaC").val("");
	$("#fCaptura2C").val("");
	$("#fAplicacionC").val("");
	$("#fAplicacion2C").val("");
	$("#polizaC").val("");
	$("#poliza2C").val("");
	$("#PolTipoC").val("PD");
	$("#PolOrigen").val("");
	$("#PolAutomatica").val("0");
	$("#PolStatus").val("");
	$("#polizaC").focus();
}


function restringeTipoPol() {
	$("#tipoPolizaSel").val($("#PolTipo option:selected").val());
	$('#PolTipo option').each(function(index, option) {
		$(option).remove();
	});
	querySelectPost({
		queryName : "catalogoTipoPolizaRestringido",
		targetObjectId : "PolTipo",
		async : false
	});
}

function showAjuste() {
	if ($("#nMes").val() != 13) {
		$("#Periodo13Div").hide();
		$("#periodo13RevDiv").hide();
		$("#periodo13AutDiv").hide();

		$("#AjusteCapt").hide();
		$("#AjusteRev").hide();
		$("#AjusteAut").hide();

	} else {
		$("#Periodo13Div").show();
		$("#periodo13RevDiv").show();
		$("#periodo13AutDiv").show();

		$("#periodo13").val("S");
		$("#periodo13Rev").val("S");
		$("#periodo13Aut").val("S");

		$("#AjusteCapt").show();
		$("#AjusteRev").show();
		$("#AjusteAut").show();
	}
}





