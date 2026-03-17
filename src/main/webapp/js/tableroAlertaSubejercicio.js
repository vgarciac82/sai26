/**
 * tableroAlertaSubejercicio.js Archivo de funciones JavaScript auxiliares en el
 * tablero de alerta subejercicio
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

/**
 * Funcion de incio que crea componentes JQuery y carga informacion inicial.
 */
function init() {
	$("input.AyudaSyC").subIniciaDlg();
	$("input.autoCompletaSyC").subIniciaAutoCompleta();

	var oTableSubEjercicio = $('#dt_subejercicio').dataTable({
		"bPaginate" : false,
		"bLengthChange" : true,
		"bFilter" : true,
		"bSort" : false,
		"bInfo" : true,
		"bAutoWidth" : true,
		"sScrollX" : "800",
		// "sScrollY" : "450px",
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"bScrollCollapse" : true,
		// "bServerSide" : true,
		// sAjaxSource : "",
		aoColumns : [ {
			sName : "UN"
		}, {
			sName : "MODIFICADO"
		}, {
			sName : "EJERCIDO"
		}, {
			sName : "PORCDISPONIBLE"
		} ],
		oLanguage : es_mx
	});

	$("#consulta").button().click(function() {
		if (validaPorcentajes()) {
			$("#formAlertaSubejercicio").submit();
		}
	});

	$("#correo").button().click(function() {
		$("#accion").val("ENVIA_CORREOS");
		$("#formAlertaSubejercicio").submit();
	});

	$("#limpiar").button().click(function() {
	});

	$("#entre").val(entre);
	$("#hasta").val(hasta);

	$("#div_detalle").dialog({
		autoOpen : false, // se juega con el true o false para que se muestre
							// o no
		height : 280,
		width : 800,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				$(this).dialog("close");
			}
		}
	});

	$("#dt_subejercicio tbody").dblclick(function(event) {
		var aPost = oTableSubEjercicio.fnGetPosition(event.target.parentNode);
		var aData = oTableSubEjercicio.fnGetData(aPost);
		var unidad = aData[0];
		if (unidad)
			$("#unidad_normativa").val(unidad);
		var cUnidadEjecutora = $("#unidad_normativa").val()
		//alert(unidad);
		cargaDetalle(cUnidadEjecutora);
		$("#div_detalle").dialog("open");
		
	});

}

function defineImagen(pctg, saldo){
	pctg = parseFloat(pctg);
	if(pctg == 0)
		return "<img src=\"../Generador/imagenes/correcto.png\" alt=\"" + "$ " + saldo + "\" width=\"16\" height=\"16\"></img>";
	else if ( pctg > 0 && pctg < $("#entre").val())
		return "<img src=\"../Generador/imagenes/correcto.png\" alt=\"" + "$ " + saldo + "\" width=\"16\" height=\"16\"></img>";
	else if ( pctg >= $("#entre").val() && pctg <= $("#hasta").val())
		return "<img src=\"../Generador/imagenes/alerta.png\" alt=\"" + "$ "  + saldo + "\" width=\"16\" height=\"16\"></img>";
	else if ( pctg > $("#hasta").val())
		return "<img src=\"../Generador/imagenes/incorrecto.png\" alt=\"" + "$ "  + saldo + "\" width=\"16\" height=\"16\"></img>";
	else if ( pctg == 100 )
		return "<img src=\"../Generador/imagenes/incorrecto.png\" alt=\"" + "$ "  + saldo + "\" width=\"16\" height=\"16\"></img>";
}

function cargaDetalle(unidad) {
	$("#detalleTraza tbody tr").remove();
	$("#unidad_normativa").val(unidad);
	queryFormPost({
		queryName : "readDetalleUnidadSubejercicio",
		async : false,
		callback : function() {
			var trText = "";
			trText += "<tr>";
			trText += "<td align=\"center\" >" + ( $("#pctg_Servicios_Personales").val() == "" ? "0.00" : parseInt($("#pctg_Servicios_Personales").val()).toFixed(2))  + " % " + "<br/>" + defineImagen(parseInt($("#pctg_Servicios_Personales").val()).toFixed(2), $("#saldo_Servicios_Personales").val() ) + "</td>";
			trText += "<td align=\"center\" >" + ( $("#pctg_Gastos_Operacion").val() == "" ? "0.00" : parseInt($("#pctg_Gastos_Operacion").val()).toFixed(2))  + " % " + "<br/>" + defineImagen(parseInt($("#pctg_Gastos_Operacion").val()).toFixed(2), $("#saldo_Gastos_Operacion").val() ) + "</td>";
			trText += "<td align=\"center\" >" + ( $("#pctg_Subsidios_Corrientes").val()== "" ? "0.00" : parseInt($("#pctg_Subsidios_Corrientes").val()).toFixed(2))  + " % " + "<br/>" + defineImagen(parseInt($("#pctg_Subsidios_Corrientes").val()).toFixed(2), $("#saldo_Subsidios_Corrientes").val() ) + "</td>";
			trText += "<td align=\"center\" >" + ( $("#pctg_Otros_Corrientes").val() == "" ? "0.00" : parseInt($("#pctg_Otros_Corrientes").val()).toFixed(2))  + " % " + "<br/>" + defineImagen(parseInt($("#pctg_Otros_Corrientes").val()).toFixed(2), $("#saldo_Otros_Corrientes").val() ) + "</td>";
			trText += "<td align=\"center\" >" + ( $("#pctg_Muebles_Inmuebles").val() == "" ? "0.00" : parseInt($("#pctg_Muebles_Inmuebles").val()).toFixed(2))  + " % " + "<br/>" + defineImagen(parseInt($("#pctg_Muebles_Inmuebles").val()).toFixed(2), $("#saldo_Muebles_Inmuebles").val() ) + "</td>";
			trText += "<td align=\"center\" >" + ( $("#pctg_Obra_Publica").val() == "" ? "0.00" : parseInt($("#pctg_Obra_Publica").val()).toFixed(2))  + " % " + "<br/>" + defineImagen(parseInt($("#pctg_Obra_Publica").val()).toFixed(2), $("#saldo_Obra_Publica").val() ) + "</td>";
			trText += "<td align=\"center\" >" + ( $("#pctg_Inversion_Fisica").val() == "" ? "0.00" : parseInt($("#pctg_Inversion_Fisica").val()).toFixed(2))  + " % " + "<br/>" + defineImagen(parseInt($("#pctg_Inversion_Fisica").val()).toFixed(2), $("#saldo_Inversion_Fisica").val() ) + "</td>";
			trText += "<td align=\"center\" >" + ( $("#pctg_Subsidios_Inversion").val() == "" ? "0.00" : parseInt($("#pctg_Subsidios_Inversion").val()).toFixed(2))  + " % " + "<br/>" + defineImagen(parseInt($("#pctg_Subsidios_Inversion").val()).toFixed(2), $("#saldo_Subsidios_Inversion").val() ) + "</td>";
			trText += "</tr>";
			$('#detalleTraza > tbody:first').append(trText);
			trText = "";
		}

	});

}

function validaPorcentajes() {

	if ($("#entre").val() == "" || $("#hasta").val() == "") {
		alert("Debe capturar el porcentaje medio y el porcentaje final");
		return false;
	} else if (parseInt($("#entre").val(), 10) > 99
			|| parseInt($("#entre").val(), 10) <= 0) {
		alert("El porcentaje medio debe ser menor igual a 99%");
		return false;
	} else if (parseInt($("#hasta").val(), 10) > 100
			|| parseInt($("#hasta").val(), 10) <= 0) {
		alert("El porcentaje medio debe ser menor igual a 100%");
		return false;
	}

	return true;
}

function limpiarSesion() {
	window.location.href = "tableroAlertaSubejercicio.jsp";
}
