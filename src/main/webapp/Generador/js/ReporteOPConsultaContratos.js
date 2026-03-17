var dTable;
var lengParams = {
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
};

function init() {
	dTable = $("#resultTable").dataTable({
		"bJQueryUI" : true,
		"sScrollY" : 250,
		"bPaginate" : false,
		"oLanguage" : lengParams
	});
	setDblClck();
	
	querySelectPost("CatalogoTipoContratoObraReadBusqueda", "IdTipoContrato", {
		async : false
	});

	querySelectPost("readCatalogoUN_OP", "unidadResponsable", {
		async : false
	});

	querySelectPost("tCatalogoTipoArticuloReadBusqueda", "IdTipoAdjudicacion",
			{
				async : false
			});

	querySelectPost("tCatalogoAdicionalesRead", "idTipoAdicional", {
		async : false
	});

	querySelectPost("catEntFedRead", "EntidadDondeSeDesarrollaObra", {
		async : false
	});

	querySelectPost("PorcIVAReadBusqueda", "IVAObra", {
		async : false
	});

	querySelectPost("tCatalogoTipoRecursosReadBusqueda", "idTipoRecurso", {
		async : false
	});

	querySelectPost("tCatalogoTipoObraReadBusqueda", "idTipoObra", {
		async : false
	});

	createDateInput("FechaJuntaAclaracionPrecompromiso");
	createDateInput("FechaRecepcionPropuestaPrecompromiso");
	createDateInput("FechaDeFalloPrecompromiso");
	createDateInput("FechaDeInicioContrato");
	createDateInput("FechaFinDeContrato");
	createDateInput("FechaDeOficio");

	$("#searchBtn").button().click(function() {
		search();
	});
	$("#clearBtn").button().click(function() {
		clearForm();
	});
	$("#prntBtn").button().click(function() {
		printExcel();
	});

	
}

function createDateInput(inptID) {
	$("#" + inptID).datepicker({
		showOn : "button",
		dateFormat : "dd/mm/yy",
		buttonImage : "images/calendar.gif",
		buttonImageOnly : true
	});
}

function generateCondition() {
	var request = "";
	var token = "";
	$("input[type=text],select,input[type=checkbox]").each(
			function() {
				var paramName = $(this).attr("name");
				var paramVal = $(this).val();
				if (paramVal != '') {
					if( $(this).attr("type") == 'checkbox' ){
						if($(this).attr("checked")){
							request = request + token + paramName + " LIKE '"
							+ paramVal + "%'";
							token = " AND ";
						}
					}else if ($(this).hasClass("fecha")){
						request = request + token + " convert(date," + paramName
								+ ",103)" + " = CONVERT( date, '" + paramVal
								+ "', 103)";
						token = " AND ";
					}else if($(this).hasClass("numerico")){
						request = request + token + " convert(float," + paramName
						+ ")" + " = CONVERT( float, '" + paramVal
						+ "')";
						token = " AND ";
					}else{
						request = request + token + paramName + " LIKE '"
								+ paramVal + "%'";
						token = " AND ";
					}
					
				}
			});
	return request;
}

function search() {
	$
			.blockUI({
				message : "<p><img src='imagenes/wait24trans.gif' />&nbsp;&nbsp;Buscando. Por favor espere...</p>"
			});
	var condition = generateCondition();
	
	var tableName = "BUSCAR_CONTRATOS_OP";
	$
			.getJSON(
					"../catalogos/SelectJson.jsp",
					{
						Tabla : tableName,
						Param : condition,
						MaxReg : "",
						ajax : 'false'
					},
					function(data) {
						dTable.fnClearTable();
						for ( var i = 0; i < data.length; i++) {
							dTable.fnAddData([ data[i].Col0, data[i].Col1,
									data[i].Col2, data[i].Col3, data[i].Col4,
									data[i].Col5, data[i].Col6, data[i].Col7,
									data[i].Col8 ]);
						}
						$.unblockUI();
						if (i == 0)
							alert("No se encontraron coincidencias con los filtros capturados");
					});
}


function printExcel() {
			
	var condition = generateCondition();
	var strCondicion="";
	var strCondicion = condition.replace(/%/gi,"[porc]");   

	
	
	var url = "../admin/SeguridadCatalogos?xls=SI&catalogo=REPORTE&accion=run&rn=ReporteOP_ConvenioModificatorio.jasper&strCadena="+  strCondicion;
	var ventimp = window.open(url, "popacuse","scrollbars=1, resizable=yes, width=300, height=200");
	
}





function onlyNumbers(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	if (keyPressed == 47) {
		return false;
	}
	return ((keyPressed > 47 && keyPressed < 58) || keyPressed == 46);
}

function clearForm(){
	$("input[type=text],select,input[type=checkbox]").each(
			function() {
					if( $(this).attr("type") == 'checkbox' )
						$(this).removeAttr("checked")
					else 
						$(this).val("");
			});
}

/**
 * Establece la funcion para el doble click. Lee de la tabla los valores
 * necesarios para iniciar el convenio modificatorio.
 */
function setDblClck() {
	$("#resultTable tbody").dblclick(function(evt) {

		var aPos = dTable.fnGetPosition(evt.target.parentNode);

		if (aPos instanceof Array)
			currIndex = aPos[0];
		else
			currIndex = aPos;

		var arr = dTable.fnGetData()[currIndex];
		folioSAI = arr[0];
		var urlWndw = "ConsultaObraPublica.jsp?folioSAI="+folioSAI;
		openWindow(urlWndw, "consulta", 800, 600, "1");
	});
}


function openWindow(url, windowName, width, height, scrollbars){
	var leftPosition = 0;
	var topPosition = 0;
	if (screen.width)
		leftPosition = (screen.width -  width)/2;
	if (screen.height)
		topPosition = (screen.height -  height)/2;
	var winArgs =
		'height=' + height +
		',width=' + width +
		',top=' + topPosition +
		',left=' + leftPosition +
		',scrollbars=' + scrollbars +
		',resizable';
	var win = window.open(url,windowName,winArgs);
	return win;
}