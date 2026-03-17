/**
 *
 * @param pQueryName Query nombrado
 * @param options    Objeto javascript con las opciones deseadas, los atributos que acepta son:
 * 		queryName: [query nombrado, si 'options' es el primer argumento]
 *		async: [true|false]
 *		data: [nombre=valor[&nombre=valor...]]
 *		callback: [funcion a ejecutar en caso de exito en la llamada]
 *
 * ej.:
 * 		queryFormPost('miQueryNombrado');
 * 		queryFormPost('miQueryNombrado', {async: false});
 * 		queryFormPost({queryName: 'miQueryNombrado', async: false});
 */
function queryFormPost(pQueryName, options) {
	var getURL = function() {
		var url = window.location.protocol + "//";
		url += window.location.host + "/";
		url += window.location.pathname.split("/")[1];
		return url;
	};
	var queryFormSettings = {
		async : true,
		data : $("form").serialize(),
		callback: function(){}
	};

	if (typeof pQueryName === "object") {
		options = pQueryName;
		pQueryName = undefined;
	}

	options = options || {};

	if (!options.queryName)
		options = jQuery.extend({ queryName : pQueryName }, options);

	var settings = jQuery.extend({}, queryFormSettings, options);

	$.ajax({
		type : "POST",
		url : getURL() + "/crud?rt=s&ql=" + settings.queryName,
		cache : false,
		async : settings.async,
		data : settings.data,
		contentType: "application/x-www-form-urlencoded; charset=UTF-8", // Asegurarse de que la codificación sea UTF-8
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
		},
		success : function(RS) {
			var index, data, col;
			if (RS.success == "true") {
				index = 1;
				while (true) {
					data = eval("RS.data_" + index++);
					if (!data)
						break;
					for ( var i = 0; i < data.length; i++) {
						col = data[i];
						for ( var name in col) {
							$("#" + name).val(eval("col." + name));
						}
					}
				}
				settings.callback();
			} else {
				alert(RS.message);
			}
		}
	});
}

/**
 *
 * @param pQueryName Query nombrado
 * @param pObjectId  Id del componente a ser llenado
 * @param options    Objeto javascript con las opciones deseadas, los atributos que acepta son:
 * 		queryName: [query nombrado, si 'options' es el primer argumento]
 *		targetObjectId: Id del componente a ser llenado
 *		async: [true|false]
 *		callback: [funcion a ejecutar en caso de exito en la llamada]
 *
 * ej.:
 * 		querySelectPost('miQueryNombrado', 'miIdComponente');
 * 		querySelectPost('miQueryNombrado', {targetObjectId: 'miIdComponente', async: false});
 * 		querySelectPost({queryName: 'miQueryNombrado', targetObjectId: 'miIdComponente', async: false});
 */
function querySelectPost(pQueryName, pObjectId, options) {
	var getURL = function() {
		var url = window.location.protocol + "//";
		url += window.location.host + "/";
		url += window.location.pathname.split("/")[1];
		return url;
	};
	var makePipeList = function(cols, names) {
		var value, token;
		value = token = "";
		for ( var i = 1; i < names.length; i++) {
			value += token + eval('cols.' + names[i]);
			token = "|";
		}
		return value;
	};
	var querySelectSettings = {
		async : true,
		callback: function(){}
	};

	if (typeof pQueryName === "object") {
		options = pQueryName;
		pQueryName = pObjectId = undefined;
	} else if (typeof pObjectId === "object") {
		options = pObjectId;
		pObjectId = undefined;
	}

	options = options || {};

	if (!options.queryName)
		options = jQuery.extend({ queryName : pQueryName }, options);

	if (!options.targetObjectId)
		options = jQuery.extend({ queryName : pQueryName, targetObjectId : pObjectId }, options);

	var settings = jQuery.extend({}, querySelectSettings, options);

	$.ajax({
		type : "POST",
		url : getURL() + "/crud?rt=s&ql=" + settings.queryName,
		cache : false,
		async : settings.async,
		data : $("form").serialize(),
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
		},
		success : function(RS) {
			var index, data, col, names, value;
			if (RS.success == "true") {
				index = 1;
				while (true) {
					data = eval("RS.data_" + index++);
					if (!data)
						break;
					value = $('#' + settings.targetObjectId).val();
					names = [];
					for ( var f in data[0])
						names.push(f);
					$('#' + settings.targetObjectId + ' option').remove();
					for ( var i = 0; i < data.length; i++) {
						col = data[i];
						$("#" + settings.targetObjectId).append('<option value="' + makePipeList(col, names) + '">' + eval('col.' + names[0]) + '</option>');
					}
					if (value != null)
						$('#' + settings.targetObjectId).val(value);
				}
				settings.callback();
			} else {
				alert(RS.message);
			}
		}
	});
}
/**
 *
 * @param pSeqName Nombre de la sequencia (de preferencia en mayusculas)
 * @param options  Objeto javascript con las opciones deseadas, los atributos que acepta son:
 * 		seqName: [Nombre de la secuencia, si 'options' es el primer argumento]
 * 		async: [true|false]
 *		callback: [funcion a ejecutar en caso de exito en la llamada, recibe como argumento el valor siguiente de la secuencia]
 *
 * ej.:
 * 		getNextVal('mySequenceName');
 * 		queryFormPost('mySequenceName', {async: false});
 * 		queryFormPost({seqName: 'mySequenceName', async: false});
 */
function getNextSequenceVal(pSeqName, options) {
	var getURL = function() {
		var url = window.location.protocol + "//";
		url += window.location.host + "/";
		url += window.location.pathname.split("/")[1];
		return url;
	};
	var nVSettings = {
			async : false,
			callback: function(value)
			{
				alert("El valor de la secuencia es: " + value);
			}
	};

	if (typeof pSeqName === "object") {
		options = pSeqName;
		pSeqName = undefined;
	}

	options = options || {};

	if (!options.seqName)
		options = jQuery.extend({ seqName : pSeqName }, options);

	var settings = jQuery.extend({}, nVSettings, options);

	$.ajax({
		type   : "GET",
		url    : getURL() + "/crud?ql=seq&rt=seq&n=" + settings.seqName,
		cache  : false,
		async  : settings.async,
		error  : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
		},
		success: function(RS) {
			if (RS.success == "true") {
				settings.callback.call(this, RS.nextVal);
			} else {
				alert(RS.message);
			}
		}
	});
}
/**
*
* @param pQueryName Query nombrado
* @param options    Objeto javascript con las opciones deseadas, los atributos que acepta son:
* 		queryName: [query nombrado, si 'options' es el primer argumento]
*		async: [true|false]
*		data: [nombre=valor[&nombre=valor...]]
*		callback: [funcion a ejecutar en caso de exito en la llamada]
*
* ej.:
* 		queryFormPost('miQueryNombrado');
* 		queryFormPost('miQueryNombrado', {async: false});
* 		queryFormPost({queryName: 'miQueryNombrado', async: false});
*/

function queryInnerDivPost(pQueryName, options) {
	var getURL = function() {
		var url = window.location.protocol + "//";
		url += window.location.host + "/";
		url += window.location.pathname.split("/")[1];
		return url;
	};
	var queryFormSettings = {
		async : true,
		data : $("form").serialize(),
		callback: function(){}
	};

	if (typeof pQueryName === "object") {
		options = pQueryName;
		pQueryName = undefined;
	}

	options = options || {};

	if (!options.queryName)
		options = jQuery.extend({ queryName : pQueryName }, options);

	var settings = jQuery.extend({}, queryFormSettings, options);

	$.ajax({
		type : "POST",
		url : getURL() + "/crud?rt=s&ql=" + settings.queryName,
		cache : false,
		async : settings.async,
		data : settings.data,
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
		},
		success : function(RS) {
			var index, data, col;
			if (RS.success == "true") {
				index = 1;
				while (true) {
					data = eval("RS.data_" + index++);
					if (!data)
						break;
					for ( var i = 0; i < data.length; i++) {
						col = data[i];
						for ( var name in col) {
							$("#" + name).html(eval("col." + name));
						}
					}
				}
				settings.callback();
			} else {
				alert(RS.message);
			}
		}
	});
}