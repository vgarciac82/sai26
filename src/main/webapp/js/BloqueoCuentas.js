/**
 * Data Table en la que se muestra la relacion Cuenta/Bloqueo
 */
var dTable;

/**
 * Alimenta la informacion de la tabla.
 */
var dataInfo = new Array();

/**
 * Almacen temporal para cambios en el bloqueo de cuentas para abono.
 */
var bloqueosAbono = new Array();

/**
 * Almacen temporal para la cuenta a editar.
 */
var cta;

/**
 * Almacen temporal para la descripcion de la cuenta a editar.
 */
var ctaDesc;

/**
 * Almacen temporal para el alcance a editar.
 */
var alcance;

/**
 * Almacen temporal para cambios en el bloqueo de cuentas para cargos.
 */
var bloqueosCargo = new Array();

/**
 * Etiquetas en espanol para la tabla.
 */
var tableLang = {
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

var currIndex;

/**
 * Crea la tabla que muestra la informacion de Cuenta/Bloqueo
 * 
 * @returns
 */
function createTable() {
	dTable = $("#tblBloqueoCuentas").dataTable({
		aaData : dataInfo,
		bFilter : true,
		bAutoWidth : false,
		bInfo : true,
		bServerSide : false,
		bProcessing : true,
		sScrollX : "1300",
		bJQueryUI : true,
		sPaginationType : "full_numbers",
		bJQueryUI : true
	});
}
/**
 * Establece la funcion para el doble click. Lee de la tabla los valores
 * necesarios para la edicion de cuentas.
 */
function setDblClck() {
	$("#tblBloqueoCuentas tbody").dblclick(function(evt) {

		var aPos = dTable.fnGetPosition(evt.target.parentNode);

		if (aPos instanceof Array)
			currIndex = aPos[0];
		else
			currIndex = aPos;

		var arr = dataInfo[currIndex];

		getAbonoBloqueos(arr);
		getCargoBloqueos(arr);
		cta = arr[0];
		alcance = arr[27];
		loadAccountDesc();
		showDialog();

	});
}

/**
 * Extrae de la lista la informacion de la cuenta.
 * 
 * @param arrAbonos
 *            Arreglo que contiene los abonos.
 */
function getAbonoBloqueos(arrAbonos) {
	bloqueosAbono = new Array(13);
	var j = 0;
	for ( var i = 14; i <= 26; i++) {
		bloqueosAbono[j] = arrAbonos[i];
		j++;
	}
}

/**
 * 
 * @param arrCargos
 */
function getCargoBloqueos(arrCargos) {
	bloqueosCargo = new Array(13);
	var j = 0;
	for ( var i = 1; i <= 13; i++) {
		bloqueosCargo[j] = arrCargos[i];
		j++;
	}
}

/**
 * Crea el cuadro de dialogo que muestra las opciones de bloqueo.
 */
function createDialog() {
	$("#altaBloqueo").dialog({
		autoOpen : false,
		height : 500,
		width : 600,
		modal : true,
		title : "Cuentas Contables",
		buttons : {
			"Aceptar" : function() {
				blockAccount();
			},
			Cancelar : function() {
				$(this).dialog("close");
			}

		},
		close : function() {
		}
	});
}

/**
 * Obtiene mediante AJAX la informacion para la tabla.
 */
function loadTableData() {
	$.getJSON("../catalogos/SelectJson.jsp", {
		Tabla : "BLOQUEO_CUENTAS",
		Param : "",
		MaxReg : "",
		ajax : false
	}, function(j) {
		var i = 0;
		for ( var cnt in j) {

			var obj = j[cnt];
			var nCuenta = obj.Col0;
			var cBloqueaAbonos = obj.Col2;
			var cBloqueaCargos = obj.Col3;
			var cNivelBloqueo = obj.Col4;
			dataInfo[i] = createTableRow(nCuenta, cBloqueaCargos,
					cBloqueaAbonos, cNivelBloqueo);
			i++;

		}
		createTable();
	});

}

/**
 * Crea un renglon para la tabla. El renglon resultado se compone de: R[0]
 * Numero de Cuenta. R[1] ... R[13] Meses en los que se encuentra cerrada la
 * cuenta (0 cerrado, 1 abierto) para ABONOS R[14]... R[26] Meses en los que se
 * encuentra cerrada la cuenta (0 cerrado, 1 abierto) para CARGOS R[27] Tipo de
 * bloqueo (T todos, C centrales, F foraneas)
 * 
 * @param nCuenta
 *            Numero de cuenta
 * @param cBloqueaAbonos
 *            Cadena de la forma XXXXXXXXXXXXX en la que X = (0, 1) indicando
 *            los meses en los que la cuenta se encuentra activa para abonos
 * @param cBloqueaCargos
 *            Cadena de la forma XXXXXXXXXXXXX en la que X = (0, 1) indicando
 *            los meses en los que la cuenta se encuentra activa para cargos
 * @param cNivelBloqueo
 *            Nivel de bloqueo
 */
function createTableRow(nCuenta, cBloqueaAbonos, cBloqueaCargos, cNivelBloqueo) {
	var r = new Array(28);
	var defaultBloq = '0000000000000';
	cBloqueaAbonos = ((cBloqueaAbonos == null || cBloqueaAbonos == '' || cBloqueaAbonos == ' ') ? defaultBloq
			: cBloqueaAbonos);
	cBloqueaCargos = ((cBloqueaCargos == null || cBloqueaCargos == '' || cBloqueaCargos == ' ') ? defaultBloq
			: cBloqueaCargos);
	r[0] = nCuenta;

	expandString(r, cBloqueaAbonos, 1);
	expandString(r, cBloqueaCargos, parseInt(1 + cBloqueaAbonos.length, 10));
	r[1 + cBloqueaAbonos.length + cBloqueaCargos.length] = tipoBloqueo(cNivelBloqueo);
	return r;
}

/**
 * Devuelve la descripcion del tipo de bloqueo.
 * 
 * @param cNivelBloqueo
 *            Caracter que identifica el tipo de bloqueo.
 * @returns {String} Descripcion de bloqueo.
 */
function tipoBloqueo(cNivelBloqueo) {
	if (cNivelBloqueo == null || cNivelBloqueo == '' || cNivelBloqueo == ' '
			|| cNivelBloqueo == 'T')
		return 'Todas';
	else if ('F' == cNivelBloqueo)
		return 'Oficinas Foraneas';
	else if ('C' == cNivelBloqueo)
		return 'Oficinas Centrales';

}
/**
 * Expande la cadena, convirtiendo cada caracter de la cadena en una entrada del
 * arreglo
 * 
 * @param arr
 *            Arreglo en el que se agrega cada caracter.
 * @param str
 *            Cadena a descomponer.
 * @param idx
 *            Indice de inicio.
 */
function expandString(arr, str, idx) {
	for ( var i = 0; i < str.length; i++) {
		if (idx + i >= 1 && idx + i <= 13)
			arr[idx + i] = str.charAt(i) == 1 ? '<div class="tdBloqCargo">&nbsp;</div>'
					: '<div class="tdNormal">&nbsp;</div>';
		else
			arr[idx + i] = str.charAt(i) == 1 ? '<div class="tdBloqAbono">&nbsp;</div>'
					: '<div class="tdNormal">&nbsp;</div>';
	}
}

/**
 * Muestra el cuadro de dialogo.
 */
function showDialog() {
	$("#altaBloqueo").dialog("open");
}

/**
 * Crea la pantalla inicial
 */
function init() {
	loadTableData();
	setDblClck();
	createDialog();
}

/**
 * Obtiene mediante AJAX la informacion para la cuenta (Descripcion/Nivel de
 * bloqueo).
 */
function loadAccountDesc() {
	$
			.getJSON("../catalogos/SelectJson.jsp", {
				Tabla : "BLOQUEO_CUENTAS_DESC",
				Param : " nCuenta = '" + cta + "'",
				MaxReg : "",
				ajax : true
			},
					function(j) {
						for ( var cnt in j) {

							var obj = j[cnt];
							var bloqStrDefault = "0000000000000";
							var bloqAbonoStr = (obj.Col2 == ' '
									|| obj.Col2 == '' || obj.Col2 == null
									|| obj.Col2 == undefined ? bloqStrDefault
									: obj.Col2);
							var bloqCargoStr = (obj.Col3 == ' '
									|| obj.Col3 == '' || obj.Col3 == null
									|| obj.Col3 == undefined ? bloqStrDefault
									: obj.Col3);

							cta = obj.Col0;
							ctaDesc = obj.Col1;

							alcance = (obj.Col4 == ' ' || obj.Col4 == ''
									|| obj.Col4 == null
									|| obj.Col4 == undefined ? 'T' : obj.Col4);

							bloqueosAbono = new Array();
							bloqueosCargo = new Array();

							bloqsToArray(bloqueosAbono, bloqAbonoStr);
							bloqsToArray(bloqueosCargo, bloqCargoStr);
							updateDialog();

						}
					});
}
/**
 * Descompone una cadena de caracteres en un arreglo donde cada entrada se
 * corresponde con el caracter en el mismo indice de la cadena
 * 
 * @param arr
 *            Arreglo en el que se agregara la informacion.
 * @param bloqStr
 *            Cadena de bloqueos.
 */
function bloqsToArray(arr, bloqStr) {
	for ( var i = 0; i < 13; i++) {
		arr[i] = bloqStr.charAt(i);
	}
}

function bloqsToStr(elmntName) {
	var tmpArr = new Array(13);

	$("[ name = \"" + elmntName + "\" ]").each(function() {
		var idx = this.value;
		if (this.checked)
			tmpArr[idx] = 1;
		else
			tmpArr[idx] = 0;
	});

	return tmpArr.toString().replace(/,/g, '');
}

function updateDialog() {
	$("#cta").text(cta + "\t");
	$("#descCta").text(ctaDesc);
	$("[ name = \"cargoChck\" ]").each(function() {
		if (bloqueosCargo[this.value] == 1)
			this.checked = true;
		else
			this.checked = false;
	});
	$("[ name = \"abonoChck\" ]").each(function() {
		if (bloqueosAbono[this.value] == 1)
			this.checked = true;
		else
			this.checked = false;
	});
	$("[ name = \"alcance\" ]").each(function() {
		if (this.id == alcance)
			this.checked = true;
		else
			this.checked = false;
	});
}

function blockAccount() {

	var strBloqueosAbono = bloqsToStr("abonoChck");
	var strBloqueosCargo = bloqsToStr("cargoChck");
	var strAlcance = "T";

	$("[ name = \"alcance\" ]").each(function() {
		if (this.checked) {
			strAlcance = this.value;
		}
	});

	$("#cBloqueaAbonos").val(strBloqueosAbono);
	$("#cBloqueaCargos").val(strBloqueosCargo);
	$("#cNivelBloqueo").val(strAlcance);
	$("#nCuenta").val(cta);

	querySelectPost({
		queryName : "ActualizaBloqueoCuentas",
		async : false,
		callback : function() {
			succesUpdate();
		}
	});
}

function succesUpdate() {
	var strBloqueosAbono = $("#cBloqueaAbonos").val();
	var strBloqueosCargo = $("#cBloqueaCargos").val();
	var strAlcance = $("#cNivelBloqueo").val();

	var rw = createTableRow(cta, strBloqueosCargo, strBloqueosAbono, strAlcance);
	dataInfo[currIndex] = rw;
	dTable.fnUpdate(rw, currIndex);
	$("#altaBloqueo").dialog("close");
}