function init() {
	$("input.AyudaSyC").subIniciaDlg();
	creaDiagloFacturas(false);
	creaDTFacturas();
	$("[readOnly]").each(function() {
		$(this).addClass("notEditable");
	});
	$("#cargarFacturasBtn").button();
}
function aceptValidaFact(){
	creaDTFacturas();
	leeMontosFacturas();
	myModalValidaFact.hide();
}
function creaDiagloFacturas(abrir) {
	
	myModalValidaFact = new bootstrap.Modal(document.getElementById('dialog-validaFact'), {
	  keyboard: false
	});
	if (abrir) {
		if($("#cIDContrato").val()==""){
			Swal.fire({ icon: 'warning',
						text: "Favor de capturar el contrato al que se le requiere cargar la factura global." });
			return;
		}
		if( facturaCapturada() ){
			Swal.fire({ icon: 'success',
						text: "La factura global ya fue cargada." });			
			return false;
		}
		myModalValidaFact.show();
		togleDivFacts(1);
	}
}

function creaDTFacturas() {
	oTablevFact = $('#grdValidaFacturas').dataTable(
		{
			"bProcessing" : true,
			"bServerSide" : true,
			"bDestroy" : true,
			"bSort" : true,
			sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vContratoFactura&qw=cTipoContrato='" + $("#TipoContrato").val() + "' AND cIDContrato='" + $("#ccvecontrato").val() + "'",
			"bJQueryUI" : true,
			"sScrollX" : "100%",
			//"sScrollXInner": "100%",
			"sScrollY" : "190px",
			"bPaginate" : false,
			"bAutoWidth" : true,
			"bInfo" : true,
			aoColumns : [
				{
					sName : "cFactura"
				},
				{
					sName : "importeBruto"
				},
				{
					sName : "importeImpuestos"
				},				
				{
					sName : "nMontoTotalFactura"
				}  ]
		});
}



function togleDivFacts(nIdDiv) {
	if (nIdDiv == 0) {
		creaDTFacturas();
		leeMontosFacturas();
		myModalValidaFact.hide();
	} else {
		var montoConIVA=$("#mImporteTotal").val();
		var montoIVA=$("#mImporteIVA").val();
		if(2==$("#tipoFactGlobal").val()){
			montoConIVA=$("#mImporteConvenioConIVA").val();
			montoIVA=$("#mImporteConvenioIVA").val();
		}
		$('#uploadFacturasFrm').attr('src', "../Generador/UploadCFDIContrato.jsp?TipoContrato=" + $("#TipoContrato").val() + "&IDContrato=" + $("#ccvecontrato").val() 
		+ "&RFC=" + $("#cIdRFC").val() + "&folioSAI=" + $("#foliosai").val()
		+ "&montoTotal=" + retiraFormatoMoney(montoConIVA) + "&montoIVA=" + montoIVA
		+ "&tipoFacturaGlobal=" + $("#tipoFactGlobal").val()
		);
		$("#uploadFacturasDiv").show();
	}
}

function retiraFormatoMoney(fld) {
	var valcol = fld;
	valcol = valcol.replace(/[$]/g, "");
	valcol = valcol.replace(/,/g, "");
	return valcol;
}

function leeMontosFacturas() {
	queryFormPost({
		queryName : "readMontoFacturasContrato",
		async : false,
		callback : function() {
			if ($("#mTotalFacturaV").val() == "") {
				$("#mTotalFacturaV").val("0.00");
			}
		}
	});
}

function cargaContrato() {
	$("#foliosai").val("");
	var encontrado = obtenFolioSAI();
	moneyFrmt("mImporteTotal");
	$("#ccvecontrato").val($("#IDContrato").val());
	return encontrado;
}

function obtenFolioSAI() {
	var encontrado = false;
	try {

		queryFormPost({
			queryName : "folioSAIOPRead",
			async : false,
			callback : function() {
				if ($("#foliosai").val() != "")
					encontrado = true;
			}
		});

	} catch (e) {		
		Swal.fire({ icon: 'warning',
					text: "No fue posible obtener el folio SAI ligado a este contrato." });
	}

	return encontrado;
}
function showContratosOb(){
	myModalCont = new bootstrap.Modal(document.getElementById('modalContracts'), {
	  keyboard: false
	})
	myModalCont.show();
	creaTablaContratos();
	clearParams();
}
function clearParams(){
	$("#cIdDefinitivo").val('');
	$("#cRazonSocial").val('');
	$("#cNumCNET").val('');
	$("#ccvecontrato").val('');
	$("#mImporteConvenioConIVA").val(0.00);
}
function creaTablaContratos() {
	var funcion="searchContratosObra";
	var cadCont="''";
	var cadProveedor="''";
	var cadContCNET="''";
	if($("#cIdDefinitivo").val()!=""){
		cadCont="'"+$("#cIdDefinitivo").val()+"'";
	}
	if($("#cRazonSocial").val()!=""){
		cadProveedor="'"+$("#cRazonSocial").val()+"'";
	}
	if($("#cNumCNET").val()!=""){
		cadContCNET="'"+$("#cNumCNET").val()+"'";
	}
	
	dtContratos = $("#tblConsultaCont").dataTable({
		bScrollCollapse: true,
		bInfo: false,
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
			sEmptyTable: "No hay partidas de contrato, favor de seleccionar un contrato SAI",
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0",
			sInfoFiltered: "(filtrado de _MAX_ registros)",
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
		bProcessing: true,
		sAjaxSource:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + ( funcion+"(" + (cadCont+","+cadProveedor+","+cadContCNET) +")" ) ,
		//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mreporteObraTotalizado&qw=" + qw,
		sPaginationType: "full_numbers",
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			{ sName: "FolioSAI" },
			{ sName: "cIdRFC" },
			{ sName: "cNoContratoCNET" },
			{ sName: "cBeneficiario" },
			{ sName: "mTotal" },
			{ sName: "mIVA" },
			{ sName: "esplurianual" },
			{ sName: "montoplurianualConIVA" },
			{ sName: "montoplurianualIVA" },
			{ sName: "cObjetoContrato" },
			{ sName: "montoConvenioConIVA" },
			{ sName: "montoConvenioIVA" }
		]
		
	});
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
function facturaCapturada(){
	var exito = false;
 
	$.ajax( {
		url : '../GeneraInformacionContrato',
		dataType : 'json',
		data : {
			"accion" : "CONTRATO_CON_FACTURA",
			"foliosai" : $("#foliosai").val(),
			"tipoFactGlobal" : $("#tipoFactGlobal").val()
		},
		async : false,
		success : function(RS) {
			if (RS.success == "true") {
				var capturados = parseInt( RS.data_1.result, 10);
				if( capturados > 0 )
					exito = true;
			}else{				
				Swal.fire({ icon: 'error',
							text: "No se puede continuar debido al siguiente error: " + RS.data_1.result });
			}
		},
		error : function(xhr, textStatus, errorThrown) {			
			Swal.fire({ icon: 'error',
						text: "Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown });
			exito = false;
		}
	});
	
 
	return exito;
}
function putValueTipoFact(){
	$("#tipoFactGlobal").val(1);
	if($("#cIDContrato").val()=="" || retiraFormatoMoney($("#mImporteConvenioConIVA").val())==0){
		if($("#cIDContrato").val()==""){			
			Swal.fire({ icon: 'warning',
						text: "Primero selecciona el contrato" });
		}else{
			Swal.fire({ icon: 'warning',
						text: "No hay convenio modificatorio autorizado en SAI para el contrato "+$("#cIDContrato").val()+ " por tanto no se debe de cargar factura global de convenio." });
		}
		document.getElementById("factConveio").checked=false
		document.getElementById("factContrato").checked=true;
		return;
	}
	if( $("#factConveio").is(':checked')){
		$("#tipoFactGlobal").val(2);
	}
}