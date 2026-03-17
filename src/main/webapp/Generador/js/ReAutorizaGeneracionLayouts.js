var tiposPago = {
	"DI" : "PAGODIRECTO",
	"DV" : "PAGODIVERSO",
	"FE" : "PAGOFEDERALIZADO",
	"RG" : "RELACIONGASTOS",
	"PO" : "PAGOOBRA",
	"AX" : "ANEXO1",
	"IF" : "REINTEGROINGRESO",
	"PC" : "PAGOPENASCONV",
	"CA" : "CAJA",
	"CV" : "COMSINVIATICOS",
	"PM" : "POLIZA",
	"OA" : "OPERAJENAS"
};

$(document).ready(function() {

	init();
	creaDialogoSeleccion();
	creaDialogoLog();
	var oTable = $("#dt_AutorizarLayouts").dataTable();

	$("#u_Login").val(cLogin);
	$("#RFC").val(RFC);

	if( ( $("#cUR").val() != "A02" && $("#cUR").val() != "A03") || esSAIAlterno )
		querySelectPost("cUnidadEjecutoraVistasTesoreria", "uEjecutora", {
			async : false
		});
	else
		querySelectPost("cUnidadEjecutoraRead", "uEjecutora", {
			async : false
		});

	$("#uEjecutora").val(cUR);
	$("#btn_Autoriza").button();
	cargaGrid();

	if( mensaje != "" )
		alert(mensaje);

});


/**
 * Crea dialogo que muestra el log
 */
function creaDialogoLog() {
	if( mostrarResultado )
		$("#logTable").css("display", "block");

	$("#dlg-Msg").dialog({
		autoOpen : mostrarResultado,
		height : 600,
		width : 800,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				$(this).dialog("close");
			}
		}
	});
}
/**
 * Crea dialogo de seleccion
 */
function creaDialogoSeleccion() {
	$("#dlg-FIEL").dialog({
		autoOpen : false,
		height : 410,
		width : 500,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				aceptarDlg();
			},
			"Cancelar" : function() {
				cancelarDlg();
			}
		},
		close : function() {},
		open : function() {
			$(".dlgFielInpt").each(function() {
				$(this).val("");
			});
		}
	});
}

/**
 * 
 */
function cargaGrid() {
	$("#cWhere").val(generaCondicion());

	var vista = "";
	if( tipoAutorizacion == "R_VOBO" )
		vista = "vListaPagosReVoBo";
	else if( type = "R_AUT" )
		vista = "vListaPagosReAut";

	oTable = $("#dt_AutorizarLayouts").dataTable({
		"bPaginate" : true,
		"iDisplayLength" : "20",
		"bLengthChange" : true,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bAutoWidth" : false,
		"sScrollY" : "100%",
		"sScrollYInner" : "100%",
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"sPaginationType" : "full_numbers",
		"sScrollX" : "100%",
		"sScrollXInner" : "100%",
		"bScrollCollapse" : true,
		"bServerSide" : true,
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=" + vista + "&qw=" + " " + encodeURI($("#cWhere").val()),
		aoColumns : [
			{
				sName : "id",
				bSearchable : false,
				bSortable : false,
				bVisible : true,
				sClass : "alignLeft"
			},
			{
				sName : "cUnidadResponsable",
				bSearchable : true,
				bSortable : false,
				bVisible : true,
				sClass : "alignLeft"
			},
			{
				sName : "Folio",
				bSearchable : true,
				bSortable : true,
				bVisible : true,
				sClass : "alignLeft"
			},
			{
				sName : "caNoContrarrecibo",
				bSearchable : true,
				bSortable : true,
				bVisible : true,
				sClass : "alignLeft"
			},
			//{ sName: "cIdRFC",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
			{
				sName : "cnombre",
				bSearchable : false,
				bSortable : false,
				bVisible : true,
				sClass : "alignLeft"
			},
			{
				sName : "mImporteNeto",
				bSearchable : false,
				bSortable : false,
				bVisible : true,
				sClass : "alignLeft"
			},
			{
				sName : "CTAB",
				bSearchable : false,
				bSortable : false,
				bVisible : true,
				sClass : "alignLeft"
			},
			{
				sName : "cConcepto",
				bSearchable : false,
				bSortable : false,
				bVisible : true,
				sClass : "alignLeft"
			},
			{
				sName : "fAplicacion",
				bSearchable : false,
				bSortable : true,
				bVisible : true,
				sClass : "alignLeft"
			}
		],
		oLanguage : {
			sProcessing : "Procesando...",
			sLengthMenu : "Mostrar _MENU_ registros",
			sZeroRecords : "No hay registros a mostrar",
			sEmptyTable : "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
			sLoadingRecords : "Cargando...",
			sInfo : "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty : "Registro 0 al 0 de 0",
			sInfoFiltered : "(filtered from _MAX_ total entries)",
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
	});

	$("#dt_AutorizarLayouts tbody").click(function(event) {
		$(oTable.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');
	});
	
	$("#dt_AutorizarLayouts tbody").dblclick(
			function(event) {
				
				$(oTable.fnSettings().aoData).each(
					function() {
						$(this.nTr).removeClass('row_selected');
					}
				);

				$(event.target.parentNode).addClass('row_selected');
				aPos = oTable.dataTable().fnGetPosition(event.target.parentNode);
				
				var aData = oTable.fnGetData(aPos);
				var a = tipoAutorizacion == "R_VOBO" ? "RVoBoPago" : ( tipoAutorizacion == "R_AUT" ? "RAutPago" : "" );
				var f = aData[2];
				var d = tiposPago[ $('input[name=cTipoPago]:checked').val() ];
				
				if( d == "CAJA")
					location.href = "../Generador/ResumenCajaNoPresupuestal.jsp?a=" + a + "&f=" + f  + "&d=" + d;
				else if( d == "COMSINVIATICOS" )
					location.href = "../Generador/ComisionesSinComprobacionFirma.jsp?a=" + a + "&f=" + f  + "&d=" + d;
				else if( d == "POLIZA" )
					location.href = "../plantillasCasos/ResumenPolizaManualFIEL.jsp?a=" + a + "&d=" + d + "&f=" + f
				else
					location.href = "../Generador/ResumenPagos.jsp?a=" + a + "&f=" + f  + "&d=" + d;
				return;
			});
}

function generaCondicion() {
	var where = "";
	if( 'cUR' != "A02" || esSAIAlterno )
		where = where + " tipoPago = '" + $("input[name='cTipoPago']:checked").val() + "'";
	else
		where = where + " cUnidadResponsable IN (SELECT cUnidadRespnNumEmpleadoAutonsable FROM dbo.tCatUnidadResponsable WITH (NOLOCK) WHERE nAlcance=1) AND tipoPago = '" + $("input[name='cTipoPago']:checked").val() + "'";

	where = where + (  tipoAutorizacion == "R_VOBO" ? " AND nNumEmpleadoVoBo = " : tipoAutorizacion == "R_AUT" ? " AND nNumEmpleadoAut = " : "  AND 1 = " ) + numeroEmpleado
	return where;
}

function AutorizaLayouts() {

	$("#nFolios").val("");
	var vacio = true;
	var tListado = $("#dt_AutorizarLayouts").dataTable().fnGetData();
	var tListado2 = document.getElementById('dt_AutorizarLayouts');

	$("#nFolios").val("");
	var folios = "";
	var token = "";

	for( i = 0; i < tListado.length; i++ ) {
		var row = tListado2.rows[ i + 1 ];
		var chkbox = row.cells[ 0 ].childNodes[ 0 ];

		if( null != chkbox && true == chkbox.checked ) {
			folios += token + tListado[ i ][ 2 ];
			$("#nFolios").val(folios);
			token = ",";
			vacio = false;
		}
	}

	if( !vacio ) {

		$("#dlg-FIEL").dialog("open");

	} else {
		alert("Seleccione al menos un Pago para autorizar.");
		return;
	}
}

function cancelarDlg() {
	$("#nFolios").val("");
	$("#dlg-FIEL").dialog("close");
	$(".dlgFielInpt").each(function() {
		$(this).val("");
	});
}

function aceptarDlg() {
	var msgValidaciones = validaCamposCompletos();
	if( "" == msgValidaciones ) {
		var msg = "Esta a punto de aceptar el proceso de los pagos con folio: " + $("#nFolios").val();
		if( confirm(msg) ) {
			$("#tipoPagoSeleccionado").val(tiposPago[ $('input[name=cTipoPago]:checked').val() ]);
			if( "OPERAJENAS"== $("#tipoPagoSeleccionado").val())
				$("#folder").val("Solicitud Firmada");
			else
				$("#folder").val("");
			$("#autorizaLayouts").submit();
			$("#dlg-FIEL").dialog("close");
			$.blockUI({
				message : "<h1>Espere ...</h1>"
			});
		}
	} else {
		alert(msgValidaciones);
	}
}

function validaCamposCompletos() {
	var msg = "";
	var token = "";

	if( $("#cerFile").val() == "" ) {
		msg = "Es necesario que adjunte su certificado.";
		token = "\n";
	} else if( !fileValidation(".cer", $("#cerFile").val()) ) {
		msg = msg + token + "El certificado debe tener una extencion .cer";
		token = "\n";
	}

	if( $("#keyFile").val() == "" ) {
		msg = msg + token + "Es necesario que adjunte su llave privada.";
		token = "\n";
	} else if( !fileValidation(".key", $("#keyFile").val()) ) {
		msg = msg + token + "La llave privada debe tener una extencion .key";
		token = "\n";
	}

	if( $("#passwordLlave").val() == "" )
		msg = msg + token + "El password de su llave privada es requerido";

	return msg;
}

function fileValidation(extencionesPermitidas, filePath) {
	var allowedExtensions = eval("/(" + extencionesPermitidas + ")$/i");
	if( allowedExtensions.exec(filePath) )
		return true;
	else
		return false;
}

function muestraLog() {
	$("#dlg-Msg").dialog("open");
}

function init() {
	
	if( tipoAutorizacion == "R_VOBO" ) {
		$("#tituloOperacion").text("Re-Visto Bueno");
	} else if( tipoAutorizacion == "R_AUT" ) {
		$("#tituloOperacion").text("Re-Autoriza");
	}
}