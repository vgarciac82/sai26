
function JSQuery(onServerResponse, onServerResponseError) {
	this.debug = false;
	this.headers = new Array();
	this.body = null;
	this.url = getURL();
	this.xmlHttpReq = null;
	this.load = onServerResponse;
	this.error = onServerResponseError;
	this.setHeader("Content-Type", "application/x-www-form-urlencoded");
}
JSQuery.prototype.setHeader = function (key, val) {
	this.headers[key] = val;
};
JSQuery.prototype.setBody = function (body) {
	this.body = body;
};
JSQuery.prototype.checkReadyStates = function (xmlHttpReq, debug, load, error) {
	return function () {
		if (xmlHttpReq.readyState == 4) {
			if (xmlHttpReq.status == 200) {
				load(eval("(" + xmlHttpReq.responseText + ")"));
			} else {
				if (debug) {
					window.alert(xmlHttpReq.status + " " + xmlHttpReq.statusText);
				}
				error(xmlHttpReq.status, xmlHttpReq.statusText);
			}
		}
	};
};
JSQuery.prototype.execute = function (query) {
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
	var onReadyStates = this.checkReadyStates(this.xmlHttpReq, this.debug, this.load, this.error);
	this.xmlHttpReq.onreadystatechange = onReadyStates;
	this.xmlHttpReq.open("POST", this.url, true);
	for (var key in this.headers) {
		this.xmlHttpReq.setRequestHeader(key, this.headers[key]);
	}
	this.setBody("respType=json&stmnt=" + query);
	this.xmlHttpReq.send(this.body);
};

function EncodeURL(url) {
	var segments = url.split("/");
	for (var i = 0; i < segments.length; i++) {
		segments[i] = encodeURIComponent(segments[i]);
	}
	return segments.join("/");
}

function getURL() {
	var href = window.location.href.replace(/\?[^?]*$/, "")
	var segments = href.split("/");
	var path = "http://";
	if (segments.length >= 3) {
		path += segments[2];
	}
	if (segments.length >= 4) {
		path += "/" + segments[3];
	}
	return path + "/query";
}
