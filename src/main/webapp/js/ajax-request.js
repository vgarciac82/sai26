
if (!this.AjaxRequest) {
	AjaxRequest = function () {
	};
}
proto = AjaxRequest.prototype;
proto.die = function (e) {
	throw (e);
};
AjaxRequest.get = function (url, callback) {
	return (new AjaxRequest()).get({"url":url, "onComplete":callback});
};
AjaxRequest.post = function (url, data, callback) {
	return (new AjaxRequest()).post({"url":url, "data":data, "onComplete":callback});
};
proto.get = function (params) {
	this._init_object(params);
	this.request.open("GET", this.url, Boolean(this.onComplete));
	return this._send();
};
proto.post = function (params) {
	this._init_object(params);
	this.request.open("POST", this.url, Boolean(this.onComplete));
	this.request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	return this._send();
};
proto._init_object = function (params) {
	for (var key in params) {
		if (!key.match(/^url|data|onComplete$/)) {
			throw ("Parametro AjaxRequest: '" + key + "' invalido");
		}
		this[key] = params[key];
	}
	if (!this.url) {
		throw ("El 'url' es requerido para el metodo get/post de AjaxRequest");
	}
	if (this.request) {
		throw ("Todavia no se soprotan multiples peticiones en el mismo objeto AjaxRequest");
	}
	this.request = new XMLHttpRequest();
	if (!this.request) {
		return this.die("Su browser no puede usuar AjaxRequest");
	}
	if (this.request.readyState !== 0) {
		return this.die("AjaxRequest readyState debe ser 0");
	}
	return this;
};
proto._send = function () {
	var self = this;
	if (this.onComplete) {
		this.request.onreadystatechange = function () {
			self._check_asynchronous();
		};
	}
	this.request.send(this.data);
	return Boolean(this.onComplete) ? this : this._check_synchronous();
};
proto._check_status = function () {
	if (this.request.status !== 200) {
		return this.die("La envio AjaxRequest a \"" + this.url + "\" fallo. Estatus: " + this.request.status);
	}
};
proto._check_synchronous = function () {
	this._check_status();
	return this.request.responseText;
};
proto._check_asynchronous = function () {
	if (this.request.readyState != 4) {
		return;
	}
	this._check_status();
	this.onComplete(this.request.responseText);
};
if (window.ActiveXObject && !window.XMLHttpRequest) {
	window.XMLHttpRequest = function () {
		var name = (navigator.userAgent.toLowerCase().indexOf("msie 5") != -1) ? "Microsoft.XMLHTTP" : "Msxml2.XMLHTTP";
		return new ActiveXObject(name);
	};
}

