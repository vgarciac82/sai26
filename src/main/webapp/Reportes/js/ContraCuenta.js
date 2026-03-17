/**
 * 
 */
function init() {
	$.unblockUI();
	getCentroContable();
	$("#cCentroContable").val(newCC);
	$("#FechaI").datepicker({
		showOn : "button",
		buttonImage : "../admin/images/calendar.png",
		buttonImageOnly : true
	}).datepicker("option", "dateFormat", 'dd/mm/yy');
	$("#FechaF").datepicker({
		showOn : "button",
		buttonImage : "../admin/images/calendar.png",
		buttonImageOnly : true
	}).datepicker("option", "dateFormat", 'dd/mm/yy');
	
	if( reportType == 'ConciliacionCostoOperacion')
		querySelectPost("readCuenta52101", "nCuenta", {
			async : false
		});
	else
		querySelectPost("catCtasContraCuentas", "nCuenta", {
			async : false
		});
	
	if( reportType == 'ReporteCxPCLC'){
		querySelectPost("tBeneficiarioCapituloMilIdRFCRead", "cIdRFC", {async : false});
		querySelectPost("CatNominaCLCRead", "CLC", {async : false});
		querySelectPost("CatNominaCxPRead", "CxP", {async : false});
		querySelectPost("CatTipoConceptoRead", "TIPO_CONCEPTO", {async : false});
		querySelectPost("CatTipoMovimendoRead", "cIdMOVIMIENTO", {async : false});
	}
	if( reportType == 'ConsolidadoPolizasXEventos'){
		querySelectPost("ConsEventoGrupoRead", "nGrupo", {async : false});
		querySelectPost("ConsEventoSubGrupoRead", "nSubGrupo", {async : false});
		querySelectPost("ConsEventoEventoRead", "nEvento", {async : false});
		querySelectPost("ConsEventoPartidaRead", "nCOG", {async : false});
	}

$("#generar").button().click(function() {
		generateReport();
	});
	$("#limpiar").button().click(function() {
		cleanForm();
	});

	$("#titulo").text("Reporte de " + reportTitle);

	$("#CxPCLC").hide();
	$("#cIdRFC").hide();
	$("#CLC").hide();
	$("#TIPO_CONCEPTO").hide();
	$("#cIdMOVIMIENTO").hide();
	$("#CxP").hide();
	$("#CxPCLC").hide();
	$("#lblcIdRFC").hide();
	$("#lblCLC").hide();
	$("#lblTIPO_CONCEPTO").hide();
	$("#lblcIdMOVIMIENTO").hide();
	$("#lblCxP").hide();
	$("#lblGrupo").hide();
	$("#lblSubgrupo").hide();
	$("#lblEvento").hide();
	$("#lblPartida").hide();
	$("#nGrupo").hide();
	$("#nSubGrupo").hide();
	$("#nEvento").hide();
	$("#nCOG").hide();

	if (reportType == "PreCierre" || 
		reportType == "ReporteCapitulo1000concepto" || 
		reportType == "ConsolidadoPolizasXEventos") {
		$("#lblCtaCtbl").hide();
		$("#nCuenta").hide();
}

	if (reportType == "ReporteCxPCLC") {
		$("#lblCtaCtbl").hide();
		$("#nCuenta").hide();
		$("#CxPCLC").show();
		$("#cIdRFC").show();
		$("#CLC").show();
		$("#TIPO_CONCEPTO").show();
		$("#cIdMOVIMIENTO").show();
		$("#CxP").show();
		$("#lblcIdRFC").show();
		$("#lblCLC").show();
		$("#lblTIPO_CONCEPTO").show();
		$("#lblcIdMOVIMIENTO").show();
		$("#lblCxP").show();
}
	if (reportType == "ConsolidadoPolizasXEventos") {
		$("#lblGrupo").show();
		$("#lblSubgrupo").show();
		$("#lblEvento").show();
		$("#lblPartida").show();
		$("#nGrupo").show();
		$("#nSubGrupo").show();
		$("#nEvento").show();
		$("#nCOG").show();
}

	fnMuestraFecha();
	
}

function generateReport() {

	var url;
	url = "../reports/CuentaPublica?isSP=true" + "&reportType=" + reportType
			+ "&condicion=" + $("#FechaI").val() + "&condicion=" + $("#FechaF").val()  
			+ ((reportType == "ReporteCxPCLC" ) ? "" : "&condicion=" + $("#cCentroContable").val())
			+ ((reportType == "ConsolidadoPolizasXEventos" ) ?  "&condicion=" + $("#nGrupo").val()
			                                                 	+ "&condicion=" + $("#nSubGrupo").val()
			                                                 	+ "&condicion=" + $("#nEvento").val()
			                                                 	+ "&condicion=" + $("#nCOG").val()
															: "" )
			+ ((reportType == "PreCierre" || 
				reportType == "ReporteCxPCLC" || 
				reportType == "ReporteCapitulo1000concepto" || 
				reportType == "ConsolidadoPolizasXEventos") ? "" : "&condicion=" + $("#nCuenta").val())
			+ ((reportType == "ReporteCxPCLC") ?  	"&condicion=" + $("#cIdRFC").val() + 
				                                	"&condicion=" + $("#CLC").val()  + 
				                                	"&condicion=" + $("#CxP").val()  + 
				                                	"&condicion=" + $("#TIPO_CONCEPTO").val()  + 
				                                	"&condicion=" + $("#cIdMOVIMIENTO").val() 
				                                : "");

	var ventimp = window.open(url, "popacuse",
			"scrollbars=1, resizable=yes, width=512, height=300");
}

function cleanForm() {
	$("input[type=text]").each(function() {
		$(this).val("");
	});
	//$("#nGrupo").selectOptions("Value 1", true);
	$("#nGrupo").removeOption(/./);
}

function fnMuestraFecha(){
	var mydate=new Date(); 
	var year=mydate.getYear(); 
	if (year < 1000) 
	year+=1900; 
	//year=year.toString().substring(4,2)
	var day=mydate.getDay(); 
	var month=mydate.getMonth()+1; 
	if (month<10) 
	month="0"+month; 
	var daym=mydate.getDate(); 
	if (daym<10) 
	daym="0"+daym; 
//		document.write("<small><font color='000000' face='Arial'><b>"+daym+"/"+month+"/"+year+"</b></font></small>")
	$("#FechaI").val("01/01/"+year);
	$("#FechaF").val(daym+"/"+month+"/"+year);
}
