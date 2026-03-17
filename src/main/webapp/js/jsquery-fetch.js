/**
 * jsquery-fetch.js
 *
 * Ejecuta query invocando un servlet (/query)
 *
 * Guillermo Jose Correa Gomez - guillermo.correa@syc.com.mx
 *
 * Last version of this code: http://dali.mty.itesm.mx/~hugo/js/datepickercontrol/
 *
 * Features:
 *   + sincrono
 *   + funciones configurables: 
 *     fetchrow - Lee el valor del renglon en el result-set
 *     fetchval - Lee el valor de cada columna en el result-set
 *
 *                                        Mexico, 2009.
 */
function JSQueryFetch(onServerResponse, onServerResponseError) {
	this.debug      = false;
	this.headers    = new Array();
	this.body       = null;
	this.url        = this.getURL();
	this.xmlHttpReq = null;
	this.load       = onServerResponse;
	this.error      = onServerResponseError;
	this.setHeader("Content-Type", "application/x-www-form-urlencoded");
}
JSQueryFetch.prototype.fetchval = function(i,j,value) {
};
JSQueryFetch.prototype.fetchrow = function(i) {
};
JSQueryFetch.prototype.setHeader = function (key, val) {
	this.headers[key] = val;
};
JSQueryFetch.prototype.setBody = function (body) {
	this.body = body;
};
JSQueryFetch.prototype.checkReadyStates = function (xmlHttpReq, debug, load, error, fetchval, fetchrow) {
	return function () {
		if (xmlHttpReq.readyState == 4) {
			if (xmlHttpReq.status == 200) {
				load(eval("(" + xmlHttpReq.responseText + ")"), fetchval, fetchrow);
			}
			/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
			//Esteban Badillo. Fecha: 08/Dic/2009. Se agrega un http header para la recepcion de errores de LDManagerServlet.java
			else if( xmlHttpReq.status == 400 )
			{
				var msgError = xmlHttpReq.getResponseHeader("msgError");
				error(xmlHttpReq.status, msgError);
			}
			/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
			else {
				if (debug) {
					window.alert(xmlHttpReq.status + " " + xmlHttpReq.statusText);
				}
				error(xmlHttpReq.status, xmlHttpReq.statusText);
				
			}
		}
	};
};

// param:	stmnt=<query>
//			jdata=<json-data>
JSQueryFetch.prototype.execute = function (param) {
	var async = false; // sincrono

	if (window.XMLHttpRequest) {
		this.xmlHttpReq = new XMLHttpRequest();
	} else {
		try {
			this.xmlHttpReq = new ActiveXObject("MSXML2.XMLHTTP.4.0");
		}
		catch (exc) {
			this.xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
		}
	}

	var onReadyStates = this.checkReadyStates(this.xmlHttpReq, this.debug, this.load, this.error, this.fetchval, this.fetchrow);
	this.xmlHttpReq.onreadystatechange = onReadyStates;
	this.xmlHttpReq.open("POST", this.url, async);
	for (var key in this.headers) {
		this.xmlHttpReq.setRequestHeader(key, this.headers[key]);
	}

	this.setBody("respType=json&" + param);
	this.xmlHttpReq.send(this.body);
};

function EncodeURL(url) {
	var segments = url.split("/");
	for (var i = 0; i < segments.length; i++) {
		segments[i] = encodeURIComponent(segments[i]);
	}
	return segments.join("/");
}

//
// getURL
//
// Parametros:
//		servlet:	El servlet-mapping del servlet a ejecutar (opcional)
// Regresa:	
//		URL del servlet a ejecutar
// Notas:
//
// URL-tokens ("/" separator):
// protocol://server:port/context/path
// 
// 0 = protocol: (usually http:)
// 1 = <empty>
// 2 = server:port
// 3 = path (includes query-string)
//
JSQueryFetch.prototype.getURL = function (servlet) {
	var href = window.location.href.replace(/\?[^?]*$/, "");	// Eliminar Query-string (a partir de ultimo ?)
	var segments = href.split("/");								// Separar en tokens separados por "/"
	var url = "http://";

	// Establecer valor de default para parametro servlet
    if (servlet === undefined) {
    	servlet = "query"
    }

	// "server:port" del URL
	if (segments.length >= 3) {
		url += segments[2];
	}
	
	// "context" del URL
	if (segments.length >= 4) {
		url += "/" + segments[3];
	}

	// "servlet" del URL
	url += "/" + servlet;

	return url;
}
