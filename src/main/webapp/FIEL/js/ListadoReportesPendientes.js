/**
 * 	Inicializa los mensajes
 * 	Crea el dialogo para leer la FIEL
 *  Crea el dialogo de resultado
 *  Inicia la DT
 *  Carga la informacion inical del DT
 */
 var modalFirma;
$(document).ready(function() {


	init();

 	modalFirma = new bootstrap.Modal(document.getElementById('dlg-FIEL'), 'data-bs-backdrop');
	//creaDialogoFirmantes();
	creaDialogoLog();

	var oTable = $("#tblResumen").dataTable();

	$("#u_Login").val(cLogin);
	$("#RFC").val(RFC);
	$("#uEjecutora").val(cUR);
	cargaGrid();

	$("#chkTodos").change(function() {
					if ($("#chkTodos").prop("checked")) {
						$("input:checkbox").attr('checked', 'checked');
					}else{
						$("input:checkbox").removeAttr('checked');
					}	
				});
				
	if (mensaje != "")
		alert(mensaje);

	$("#btn_Autoriza").button();
});


/**
 * Crea dialogo que muestra el log
 */
function creaDialogoLog() {
	if (mostrarResultado)
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
 
function creaDialogoFirmantes() {
		$(".dlgFielInpt").each(function() {
				$(this).val("");
		});
		modalFirma.show();
/*
	$("#dlg-FIEL").dialog({
		autoOpen : false,
		height : 450,
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
	*/
}

/**
 * Crea y obtiene la informacion del DT que muestra los
 */
function cargaGrid() {
	$("#cWhere").val(generaCondicion());
	var vista = "vReporteFIELDT";

	oTable = $("#tblResumen").dataTable({
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
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + vista + "&qw=" + " " + encodeURI($("#cWhere").val()),
		aoColumns : [
			{
				sName : "comando",
				bSearchable : false,
				bSortable : false,
				bVisible : true,
				sClass : "alignCenter"
			},
			{
				sName : "nIDEdoFinanciero",
				bSearchable : true,
				bSortable : false,
				bVisible : true,
				sClass : "alignCenter"
			},
			{
				sName : "cDescReporte",
				bSearchable : true,
				bSortable : true,
				bVisible : true,
				sClass : "alignLeft"
			},
			{
				sName : "nMes",
				bSearchable : true,
				bSortable : true,
				bVisible : true,
				sClass : "alignCenter"
			},
			{
				sName : "cDescMoneda",
				bSearchable : false,
				bSortable : false,
				bVisible : true,
				sClass : "alignLeft"
			},
			{
				sName : "cDescNivel",
				bSearchable : false,
				bSortable : false,
				bVisible : true,
				sClass : "alignLeft"
			},
			{
				sName : "nOrden",
				bSearchable : false,
				bSortable : false,
				bVisible : false
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

	$("#tblResumen tbody").click(function(event) {
		$(oTable.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');
	});


	$("#tblResumen tbody").dblclick(
		function(event) {

			$(oTable.fnSettings().aoData).each(
				function() {
					$(this.nTr).removeClass('row_selected');
				}
			);

			$(event.target.parentNode).addClass('row_selected');
			aPos = oTable.dataTable().fnGetPosition(event.target.parentNode);

			var aData = oTable.fnGetData(aPos);

			var a = "FirmaReporte";
			var d = "REPORTE";
			var o = aData[6];
			var f = aData[1];

			location.href = "../Generador/ReportesFirma.jsp?a=" + a + "&f=" + f + "&d=" + d + "&o=" + o;

			return;
		});
}

/**
 * Genera la condicion para la DT
 */
function generaCondicion() {
	var where = "cNumeroEmpleado=" + numeroEmpleado;
	return where;
}

/**
 * Funcion que envia los folios seleccionados a su firma.
 */
function AutorizaLayouts() {
	$("#nFolios").val("");
	var vacio = true;
	var tListado = $("#tblResumen").dataTable().fnGetData();
	var tListado2 = document.getElementById('tblResumen');

	$("#nFolios").val("");
	var folios = "";
	var ordenes = "";

	var token = "";

	for (i = 0; i < tListado.length; i++) {

		var row = tListado2.rows[i + 1];
		var chkbox = row.cells[0].childNodes[0];

		if (null != chkbox && true == chkbox.checked) {

			folios += token + tListado[i][1];
			ordenes += token + tListado[i][6];

			$("#nFolios").val(folios);
			$("#ordenes").val(ordenes);
			token = ",";
			vacio = false;
		}
	}

	if (!vacio) {

		creaDialogoFirmantes();

	} else {
		alert("Seleccione al menos un Reporte para autorizar.");
		return;
	}
}

/**
 * Funcion llamada cuando el usuario no desea enviar los seleccionados a firma.
 */
function cancelarDlg() {
	$("#nFolios").val("");
	$("#dlg-FIEL").dialog("close");
	$(".dlgFielInpt").each(function() {
		$(this).val("");
	});
}

/**
 * Funcion llamada cuando el usuario acepta enviar los folios a firma. 
 */
function aceptarDlg() {
	var msgValidaciones = validaCamposCompletos();
	if ("" == msgValidaciones) {
		var msg = "Esta a punto de aceptar el proceso de los reportes con folio: " + $("#nFolios").val();
		if (confirm(msg)) {
			$("#firmaReportes").submit();
			$("#dlg-FIEL").dialog("close");
			$.blockUI({
				message : "<h1>Espere ...</h1>"
			});
		}
	} else {
		alert(msgValidaciones);
	}
	
	modalFirma.hide();
}

/**
 * Funcion que valida que se cuenta con la documentacion minima para enviar a firma.
 */
function validaCamposCompletos() {
	var msg = "";
	var token = "";

	if ($("#cerFile").val() == "") {
		msg = "Es necesario que adjunte su certificado.";
		token = "\n";
	} else if (!fileValidation(".cer", $("#cerFile").val())) {
		msg = msg + token + "El certificado debe tener una extencion .cer";
		token = "\n";
	}

	if ($("#keyFile").val() == "") {
		msg = msg + token + "Es necesario que adjunte su llave privada.";
		token = "\n";
	} else if (!fileValidation(".key", $("#keyFile").val())) {
		msg = msg + token + "La llave privada debe tener una extencion .key";
		token = "\n";
	}

	if ($("#passwordLlave").val() == "")
		msg = msg + token + "El password de su llave privada es requerido";

	return msg;
}

/**
 * Valida que las extenciones de los archivos esten en los permitidos
 */
function fileValidation(extencionesPermitidas, filePath) {
	var allowedExtensions = eval("/(" + extencionesPermitidas + ")$/i");
	if (allowedExtensions.exec(filePath))
		return true;
	else
		return false;
}

function muestraLog() {
	$("#dlg-Msg").dialog("open");
}

function init() {
	$("#tituloOperacion").text("Firma Electronica de Reportes");
}