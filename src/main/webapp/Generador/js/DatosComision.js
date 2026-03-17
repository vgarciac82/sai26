/**
 * 
 */
var es_mx = {
			sProcessing : "Procesando...",
			sLengthMenu : "Mostrar _MENU_ registros",
			sZeroRecords : "No hay registros a mostrar",
			sEmptyTable : "No hay datos en la tabla",
			sLoadingRecords : "Cargando...",
			sInfo : "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty : "Registro 0 al 0 de 0",
			sInfoFiltered : "(filtered from _MAX_ total entries)",
			sInfoPostFix : "",
			sInfoThousands : ",",
			sSearch : "Filtro:",
			oPaginate : {
				sFirst : "Primero",
				sPrevious : "Ant.",
				sNext : "Sigte.",
				sLast : "&Uacute;ltimo"
			}

		};

function inicializaDatosAgenda() {
	$("#rdoNacional").change(function() {
		cambioDestinoViaticos();
	});
	$("#rdoInternacional").change(function() {
		cambioDestinoViaticos();
	});
	
	$("#chkHomologacion").change(function() {
		if( $("#chkHomologacion").prop("checked") ) {			
			$("#cboHomologacion").val(0);
			$("#nIdHomologacion").val("3");
			document.getElementById("cboHomologacion").disabled = false;
		}else{
			$("#cboHomologacion").val(0);
			$("#nIdHomologacion").val("3");
			cargaCuotaPorDia();
			if(!esValorCero("cboPaquete")){
				calcularCuotaPorDia();
			}
			document.getElementById("cboHomologacion").disabled = true;
		}
	});
	
	$("#cboHomologacion").change(function() {
		if(!esValorCero("cboHomologacion")){
			$("#nIdHomologacion").val($("#cboHomologacion").val());
			if($("#nIdHomologacion").val() == "2"){
				cargaCuotaPorDia();
				document.getElementById("mCuotaDia").disabled = false;
			}else{
				cargaCuotaPorDia();
				if(!esValorCero("cboPaquete")){
					calcularCuotaPorDia();
				}
			}			
		}else{
			$("#nIdHomologacion").val("3");
			cargaCuotaPorDia();
			if(!esValorCero("cboPaquete")){
				calcularCuotaPorDia();
			}
		}		
	});
	
	$("#cboPaquete").change(function() {
		if(!esValorCero("cboPaquete")){
			calcularCuotaPorDia();
		}		
	});

	$("#btnAgregar").button();
	
	setFechas();
	cargaPaises();
	cargaEstados();
	cargaMunicipios();
	cargaPaquetes();
	cargaHomologacion();
	document.getElementById("cboHomologacion").disabled = true;
	cargaTipoMoneda();
	creaDTAgenda();
	cargaCuotaPorDia();
	validaEsAnticipado();
}

function setFechas() {
	$("#fInicio").datepicker({
		dateFormat : "dd/mm/yy",
		showOn : "button",
		buttonImage : "../Generador/images/calendar.gif",
		buttonImageOnly : true
	});

	$("#fFin").datepicker({
		dateFormat : "dd/mm/yy",
		showOn : "button",
		buttonImage : "../Generador/images/calendar.gif",
		buttonImageOnly : true
	});
}

function cargaPaises() {
	querySelectPost("tCatalogoPaisesRead", "cboPais", {
		async : false
	});
	$("#cboPais").val(146);
}

function cargaEstados() {
	$("#pais").val(document.getElementById("cboPais").value);
	querySelectPost("cat_EstadosRead", "cboEstado", {
		async : false
	});

	if( $("#pais").val() != "146" ) {
		$("#cboEstado").val(33);
		document.getElementById("cboEstado").disabled = true;
	} else {
		document.getElementById("cboEstado").disabled = false;
	}
}

function cargaMunicipios() {
	$("#estado").val(document.getElementById("cboEstado").value);

	querySelectPost("cat_MunicipiosRead", "cboMunicipio", {
		async : false
	});

	if( $("#estado").val() == "33" ) {
		$("#cboMunicipio").val(32060);
		document.getElementById("cboMunicipio").disabled = true;
	} else {
		document.getElementById("cboMunicipio").disabled = false;
	}
}

var oTable;
function creaDTAgenda() {
	
	var where = " Folio = " + $("#nFolioSolicitudViaticos").val();
	
	oTable = $('#dtComisionesDet').dataTable(
		{
			"bPaginate" : false,
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth" : true,
			"sScrollY" : 300,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"bScrollCollapse" : true,
			"bServerSide" : true,
			sAjaxSource : window.location.protocol + "//"
			+ window.location.host + "/"
			+ window.location.pathname.split("/")[1]
			+ "/crud?rt=t&ql=vSolicitudViaticosAgenda&qw="
			+ where,
			aoColumns : [ {
				sName : "nDocRenglon", bVisible : false
			}, {
				sName : "fInicio", 
				sClass : "centerCls"
			}, {
				sName : "fFin", 
				sClass : "centerCls"
			}, {
				sName : "Tipo"
			}, {
				sName : "DESTINO"
			}, {
				sName : "cMotivo"
			}, {
				sName : "TarifaD", 
				sClass : "leftCls"
			}, {
				sName : "CuotaPorDia", 
				sClass : "leftCls"
			}, {
				sName : "DIAS", 
				sClass : "centerCls"
			}, {
				sName : "Total", 
				sClass : "leftCls"
			}, {
				sName : "descartar", 
				sClass : "centerCls"
			} ],
			oLanguage : es_mx
		});

}


function cambioDestinoViaticos() {
	if( $("#rdoNacional").prop("checked") ) {
		$("#cboPais").val(146);
		$("#cboEstado").val(0);
		document.getElementById("cboEstado").disabled = false;
		$("#cboMunicipio").val(0);
		document.getElementById("cboMunicipio").disabled = false;
		cargaEstados();
		cargaMunicipios();
	} else {
		$("#rdoNacional").prop("checked", false);
		$("#cboPais").val(0);
		cargaEstados();
		cargaMunicipios();
	}
}

function cargaPaquetes(){
	
	querySelectPost("catPaquetesComision_Read", "cboPaquete", {
		async : false
	});	
}

function cargaHomologacion(){
	
	querySelectPost("catHomologacionComision_Read", "cboHomologacion", {
		async : false
	});	
}

function cargaTipoMoneda(){
	
	querySelectPost("MonedaFONDENRead", "cboTipoMoneda", {
		async : false
	});	
}

function validarCaptura(){
	
	var bRegresa = true;
	
	$("#cActividadesAgenda").val($("#cActAgenda").val());
	
	var str = $("#cActividadesAgenda").val();
	var n = str.length;
	if(n > 256){
		alert("El numero de caracteres capturados exceden el limite permitido.");
		return;
	}
	
	if(isEmpty("fInicio")){
		alert("Favor de capturar la Fecha Inicio.");
		bRegresa = false;
		return;
	}else if(isEmpty("fFin")){
		alert("Favor de capturar la Fecha Fin.");
		bRegresa = false;
		return;
	}else if(!validarFechas()){
		alert("La fecha Inicio no puede ser mayor a fecha Fin. Verifique!!");
		bRegresa = false;
		return;
	}else if(validarExisteFechaAgenda()){
		alert("Ya existe una Comisión en la Agenda entre las Fechas capturadas. Verifique!!");
		bRegresa = false;
		return;
	}else if(!$("#rdoAnticipado").prop("checked") && !$("#rdoDevengado").prop("checked")){
		alert("Favor de seleccionar el tipo: Anticipado ó Devengado.");
		bRegresa = false;
		return;
	}else if(!$("#rdoNacional").prop("checked") && !$("#rdoInternacional").prop("checked")){
		alert("Favor de seleccionar el tipo: Nacional ó Internacional.");
		bRegresa = false;
		return;
	}else if(esValorCero("cboPais")){
		alert("Favor de seleccionar el País.");
		bRegresa = false;
		return;
	}else if(esValorCero("cboEstado")){
		alert("Favor de seleccionar el Estado.");
		bRegresa = false;
		return;
	}else if(esValorCero("cboMunicipio")){
		alert("Favor de seleccionar el Municipio.");
		bRegresa = false;
		return;
	}else if(isEmpty("cLocalidad")){
		alert("Favor de capturar la Localidad.");
		bRegresa = false;
		return;
	}else if(isEmpty("cMotivo")){
		alert("Favor de capturar el Motivo.");
		bRegresa = false;
		return;
	}else if(isEmpty("cActividadesAgenda")){
		alert("Favor de capturar las Actividades de Agenda.");
		bRegresa = false;
		return;
	}else if(n > 256){
		alert("El numero de caracteres capturados en Actividades de Agenda exceden el limite permitido.");
		bRegresa = false;
		return;
	}else if($("#chkHomologacion").prop("checked")){
		if(esValorCero("cboHomologacion")){
			alert("Favor de seleccionar la Homologación.");
			bRegresa = false;
			return;
		}
	}	
	
	if(bRegresa){
		validaExisteFinSemanaDiaFestivo();
		cargaInputsGuardar();
	}
	
	return bRegresa;
}

function cargaInputsGuardar(){
	
	if($("#rdoAnticipado").prop("checked")){
		$("#lEsAnticipado").val("S");
	}else{
		$("#lEsAnticipado").val("N");
	}
	
	if($("#rdoNacional").prop("checked")){
		$("#lEsNacional").val("S");
	}else{
		$("#lEsNacional").val("N");
	}
	
	$("#nIdPais").val($("#cboPais").val());
	$("#ID_ESTADO").val($("#cboEstado").val());
	$("#ID_MUNICIPIO").val($("#cboMunicipio").val());
	$("#nIDPaquete").val($("#cboPaquete").val());
	
	if($("#chkHomologacion").prop("checked")){
		$("#nIDHomologacion").val($("#cboHomologacion").val());
	}else{
		$("#nIDHomologacion").val($("#nIdHomologacion").val());
	}
	
	$("#cIDTipoMoneda").val($("#cboTipoMoneda").val());
	
	$("#mCuotaDia").val(quitaFmt($("input[id='mCuotaDia']").val()));
	
}

function comisionAgendaToJSON() {
	
	var objComisionAgenda = {
		"folioViatico" : $("#nFolioSolicitudViaticos").val(),
		"fInicio" : $("#fInicio").val(),
		"fFin" : $("#fFin").val(),
		"lEsAnticipado" : $("#lEsAnticipado").val(),
		"lEsNacional" : $("#lEsNacional").val(),
		"nIdPais" : $("#nIdPais").val(),
		"ID_ESTADO" : $("#ID_ESTADO").val(),
		"ID_MUNICIPIO" : $("#ID_MUNICIPIO").val(),
		"cLocalidad" : $("#cLocalidad").val(),
		"cMotivo" : $("#cMotivo").val(),
		"mCuotaDia" : $("#mCuotaDia").val(),
		"nIDPaquete" : $("#nIDPaquete").val(),
		"nIDHomologacion" : $("#nIDHomologacion").val(),
		"cIDTipoMoneda" : $("#cIDTipoMoneda").val(),
		"cActividadesAgenda" : $("#cActividadesAgenda").val()
	};

	return objComisionAgenda;
}

function guardaComisionAgenda() {
	if (validarCaptura()) {
		var agenda = comisionAgendaToJSON();
		var bExito = false;
		$.ajax({
			url : '../viaticos/CreaViaticoAgenda',
			type : 'post',
			dataType : 'json',
			async : false,
			data : agenda,
			success : function(data) {
				var exito = (data.success == "true");
				if (exito) {
					alert("Informacion guardada exitosamente. Continue registrando la agenda.");
					bExito = exito;
				} else {
					alert("No fue posible registrar la informacion debido al siguiente error:\n" + data.data_1.result);
				}
			}
		});
		
		if(bExito){
			limpiarInputs();
			cargaResumenViaticos();
		}		
	} 
}

function validarFechas(){
	var bRegresa = true;
    var fInicio = document.getElementById('fInicio').value.split("/");
    var fFin  = document.getElementById('fFin').value.split("/");
	    
    fInicio = new Date( fInicio[2], fInicio[1]-1, fInicio[0], 0, 0, 0, 0 );
    fFin = new Date( fFin[2], fFin[1]-1, fFin[0], 0, 0, 0, 0);

    if(fInicio > fFin){
    	bRegresa = false;        	
    }
    
    return bRegresa;
}

function isEmpty(idCampo){
    var val = $("#"+idCampo).val();
    val = val.replace(/\s/g, "" );
    
    if( val == "" )
           return true;
    else 
           return false;
}

function esValorCero(campo){
	var val = $("#"+campo).val();
	
	if( val == "0" )
        return true;
	else 
        return false;
}

function Sinfrmt(fld) {		
	var valcol = $(fld).val();
	valcol = valcol.replace("$", "");
	valcol = valcol.replace(",", "");		
	$(fld).val(valcol);
}

function cambiafrmt(fld) {		
	$(fld).formatCurrency();
}

function quitaFmt(val) {
   	val = val.replace("$", "");
   	val = val.replace(/,/g, "");

	if (val.indexOf("(") >= 0) {
		val = val.replace("(", "");
		val = val.replace(")", "");
		val = "-" + val;
	}
	return val;
}

function cargaCuotaPorDia(){
	
	document.getElementById("mCuotaDia").disabled = false;
	queryFormPost("cuotaPorDiaAgenda_Read", {async:false});
	$("#mCuotaDia").val($("#mImporteMaximo").val());
	$("#mCuotaPorDia").val($("#mImporteMaximo").val());	
	$("#mCuotaDia").formatCurrency();
	document.getElementById("mCuotaDia").disabled = true;
}

function calcularCuotaPorDia(){
	document.getElementById("mCuotaDia").disabled = false;
	$("#mCuotaPorDia").val(quitaFmt($("#mCuotaPorDia").val()));
	queryFormPost("calculaCuotaPorDiaAgenda_Read", {async:false});
	$("#mCuotaPorDia").val($("#mCuotaDia").val());
	$("#mCuotaDia").formatCurrency();
	document.getElementById("mCuotaDia").disabled = true;
}

function limpiarInputs(){	
	
	$("#fInicio").val("");
	$("#fFin").val("");
	
	$("#rdoAnticipado").prop("checked", true);
	$("#rdoDevengado").prop("checked", false);
	$("#rdoNacional").prop("checked", true);	
	$("#rdoInternacional").prop("checked", false);
	
	$("#cboPais").val(146);
	cargaEstados();
	$("#cboEstado").val(0);
	cargaMunicipios();
	$("#cboMunicipio").val(0);
	
	$("#cLocalidad").val("");
	$("#cMotivo").val("");
	
	$("#cboPaquete").val(0);
	$("#chkHomologacion").prop("checked", false);
	$("#cboHomologacion").val(0);
	
	document.getElementById("cboHomologacion").disabled = true;
	$("#nIdHomologacion").val("3");
	cargaCuotaPorDia();
	
	creaDTAgenda();
	validaEsAnticipado();
}

function descartar(renglon) {
	
	$("#nDocRenglon").val("0");
	if (confirm("Esta seguro de borrar el renglon?")) {
		$("#nDocRenglon").val(renglon);
		queryFormPost("solicitudViaticosAgendaDelete", {async:false});
		creaDTAgenda();
		validaEsAnticipado();
		cargaResumenViaticos();
	}	
}

function validarExisteFechaAgenda(){
	var bRegresa = false;
	$("#existeFAgenda").val("0");
	queryFormPost("validaExisteFechaAgenda_Read", {async:false});
	
	if($("#existeFAgenda").val() == "1"){
		bRegresa = true;
	}	
	return bRegresa;
}

function validaExisteFinSemanaDiaFestivo(){
	
	$("#existeFinSemana").val("0");
	queryFormPost("validaExisteFinSemanaAgenda_Read", {async:false});
	
	if($("#existeFinSemana").val() == "1"){
		alert("Acepto que: Esta comisión contiene fin(es) de semana ó días festivos.")
	}		
}

function validaEsAnticipado(){
	
	$("#existeDet").val("0");
	queryFormPost("existeDetViaticosAgenda_Read", {async:false});
	
	if(Number($("#existeDet").val()) > 0){
		$("#esAnticipado").val("");
		queryFormPost("esAnticipadoDatosComision_Read", {async:false});
		
		if($("#esAnticipado").val() == "S"){
			$("#rdoAnticipado").prop("checked", true);
		}else{
			$("#rdoDevengado").prop("checked", true);
		}		
		
		document.getElementById("rdoAnticipado").disabled = true;
		document.getElementById("rdoDevengado").disabled = true;
	}else{
		document.getElementById("rdoAnticipado").disabled = false;
		document.getElementById("rdoDevengado").disabled = false;
	}
	
}
;