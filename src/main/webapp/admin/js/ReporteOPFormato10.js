function init() {
	$("input.AyudaSyC").subIniciaDlg();
	//$("input.autoCompletaSyC").subIniciaAutoCompleta();

//	if ($("#rt").val() == "FORMATO_10" || $("#rt").val() == "REPORTE_CONTRATOS" || $("#rt").val() == "REPORTE_ACUMULADO" || $("#rt").val() == "REPORTE_SEGUIMIENTO" ){
		$("#fechaI").datepicker({
		minDate : -1000,
		maxDate : "+0M -1D",
		changeMonth : true,
		changeYear : true,
		showOn : "button",
		dateFormat : "dd/mm/yy",
		buttonImage : "../Generador/images/calendar.gif",
		buttonImageOnly : true
	});
//	}
	$("#fechaF").datepicker({
		minDate : -1000,
		maxDate : "+0M -1D",
		changeMonth : true,
		changeYear : true,
		showOn : "button",
		dateFormat : "dd/mm/yy",
		buttonImage : "../Generador/images/calendar.gif",
		buttonImageOnly : true
	});

	$("#limpiarBtn").button().click(function() {
		window.location = "ReporteOPFormato10.jsp"
		// limpiaPantalla();
	});
	$("#generarBtn").button().click(function() {
		generaReporte();
	});

	$("#opUnidadNormativa").change(function() {
		cargaCatalogoCC();
	});
	$("#opUnidadNormativa").blur(function() {
		cargaCatalogoCC();
	});
}

function asignaXReporte() {
	$( "#container" ).hide();
	$( "#fechas" ).show();
	
	if ($("#rt").val() == "FORMATO_10"){
		$( "#container" ).show();
		$( "#fechas" ).show();
		document.getElementById('titulo').innerHTML = "Consulta Obra P&uacute;blica (Formato 10)";
	}
	if ($("#rt").val() == "CONSOLI_NAL")
		document.getElementById('titulo').innerHTML = "Reporte Nacional por Unidades Ejecutoras";
	if ($("#rt").val() == "CONSOLI_AREA_RESP")
		document.getElementById('titulo').innerHTML = "Reporte Nacional Por Unidad Central Normativa";
	if ($("#rt").val() == "CONSOLI_PROYECTO")
		document.getElementById('titulo').innerHTML = "Consolidado por Proyecto";
	if ($("#rt").val() == "CONSOLI_PROYECTO_REGION")
		document.getElementById('titulo').innerHTML = "Consolidado por Proyecto por Region";
	if ($("#rt").val() == "CONSOLI_POR_AREA_EJECUTORA")
		document.getElementById('titulo').innerHTML = "Consolidado por Area Ejecutora";
	if ($("#rt").val() == "CONSOLI_POR_AREA_EJECUTORA_y_PROYECTO")
		document.getElementById('titulo').innerHTML = "Reporte Por Unidades Ejecutoras Por Titulos Unidades Ejecutoras";
	if ($("#rt").val() == "AVANCE_CONTRATACION")
		document.getElementById('titulo').innerHTML = "Formato Cocoa Avance De Obra Publica Por Unidad Central Normativa";
	if ($("#rt").val() == "NACIONAL_PRESUPUESTARIO"){
			document.getElementById('titulo').innerHTML = "Reporte de Presupuesto Nacional Por Proyecto Presupuestario ";
		$( "#fechas" ).show();
		//$( "#fechaI" ).hide();
		//$( "#nn" ).hide();
	}
	if ($("#rt").val() == "UNIDAD_PRESUPUESTARIO"){
		document.getElementById('titulo').innerHTML = "Reporte Por Unidades Ejecutoras Por Titulos De Proyecto Presupuestario";
	$( "#fechas" ).show();
	//$( "#fechaI" ).hide();
	//$( "#nn" ).hide();
}
}

function clearSelect(idSel) {
	for ( var i = 0; i < idSel.length; i++)
		$('#' + idSel[i]).find('option').remove();
}

function cargaCatalogoCC() {
	clearSelect([ "cCentroContable" ]);
	querySelectPost("catCentroContable", "cCentroContable", {
		async : false
	});
}

function limpiaPantalla() {
	$(":input")
			.each(
					function() {
						if ($(this).attr("type") == 'text'
								|| $(this).attr("type") == "hidden") {
							$(this).val('');
						} else if ($(this).attr("type").indexOf('select') >= 0) {
							clearSelect([ this.id ]);
							$(this)
									.append(
											'<option value="" selected="selected">Seleccione una opci&oacute;n</option>');
						}
					});
}


function generaReporte() {
	var nameReporte = $("#rt").val();
	//alert(nameReporte);
	   if ($("#fechaI").val() == "" || $("#fechaF").val() == ""){
		   alert("Debe ingresar las fechas de consuta");
		   return;
	   }   
	
	switch(nameReporte)
	 {
	  case 'CONSOLI_POR_AREA_EJECUTORA':
	        url = "../admin/SeguridadCatalogos?catalogo=REPORTE&accion=run&rn=\ConsolidadoPorAreaEjecutoraOP.jasper";
	        
			var ventimp = window.open(url, "popacuse","scrollbars=1, resizable=yes, width=300, height=200");
	        break;
	   case 'CONSOLI_POR_AREA_EJECUTORA_y_PROYECTO':
	        url = "../admin/SeguridadCatalogos?catalogo=REPORTE&accion=run&rn=\OP_consolidadoPorAreaEjecutoraProyecto3.jasper";
	        window.open(url, "popacuse","scrollbars=1, resizable=yes, width=300, height=200");	
	        break;
	   case 'AVANCE_CONTRATACION':
	        url = "../admin/SeguridadCatalogos?catalogo=REPORTE&accion=run&rn=\OP_AvanceContratacion.jasper&fIni="+$("#fechaI").val()+"&fFin="+$("#fechaF").val();
	        window.open(url, "popacuse","scrollbars=1, resizable=yes, width=300, height=200");	
	        break;
	   case 'REPORTE_CONTRATOS':
		   if ($("#fechaI").val() == "" || $("#fechaF").val() == ""){
			   alert("Debe ingresar las fechas de consuta");
			   return;
		   }
		   break;
	   case 'NACIONAL_PRESUPUESTARIO':
	        url = "../admin/SeguridadCatalogos?catalogo=REPORTE&accion=run&rn=\OP_Reporte_Nacional_Proyecto_Presupuestario.jasper&fIni="+$("#fechaI").val()+"&fFin="+$("#fechaF").val();
	        window.open(url, "popacuse","scrollbars=1, resizable=yes, width=300, height=200");	
	        break;
	   case 'UNIDAD_PRESUPUESTARIO':
	        url = "../admin/SeguridadCatalogos?catalogo=REPORTE&accion=run&rn=\OP_Reporte_Unidad_Ejecutora_Proyecto_Presupuestario.jasper&fIni="+$("#fechaI").val()+"&fFin="+$("#fechaF").val();
	        window.open(url, "popacuse","scrollbars=1, resizable=yes, width=300, height=200");	
	        break;     
	 default:
		    $("#frmOP").action = "../ObraPublica/reportes";
		    $("#frmOP").submit();
	 }
}