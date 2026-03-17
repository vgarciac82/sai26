// params = (url, htmlElementId)
function com_jenkov_prizetags_ajaxLoadPage() {
	var args = com_jenkov_prizetags_ajaxLoadPage.arguments;
	if (document.getElementById) {
		var x = (window.ActiveXObject) ? new ActiveXObject("Microsoft.XMLHTTP") : new XMLHttpRequest();
	}
	if (x) {
		x.onreadystatechange = function () {
			if (x.readyState == 4 && x.status == 200) {
				var el = document.getElementById(args[1]);
				el.innerHTML = x.responseText;
			}
			if (x.readyState == 4 && x.status != 200) {
				//window.alert("error getting xml data via AJAX");
				window.alert("error obteniendo datos XML via AJAX");
			}
		};
	}
	x.open("GET", args[0], true);
	x.send(null);
	parent.frames['doctree'].location.reload(); //Ethiel, se agrega para refrescar el menu de acciones cuando se hace click en algun nodo
	parent.frames['main'].location = "blank.jsp"; //Ethiel, se agrega para ocultar la pantalla de crear-carpeta_docto cuando cambia de nodo

}


// params  = (url, htmlElementId, formElementId)
function com_jenkov_prizetags_ajaxSubmitForm() {
	var args = com_jenkov_prizetags_ajaxSubmitForm.arguments;
	if (document.getElementById) {
		var x = (window.ActiveXObject) ? new ActiveXObject("Microsoft.XMLHTTP") : new XMLHttpRequest();
	}
	if (x) {
		x.onreadystatechange = function () {
			if (x.readyState == 4 && x.status == 200) {
				el = document.getElementById(args[1]);
				el.innerHTML = x.responseText;
			}
			if (x.readyState == 4 && x.status != 200) {
				//window.alert("error getting xml data via AJAX");
				window.alert("error obteniendo datos XML via AJAX");
			}
		};
	}
	formObj = args[0];
	formParams = "";
	for (i = 0; i < formObj.elements.length; i++) {
		if (i > 0) {
			formParams = formParams + "&";
		}
		if (formObj.elements[i].type == "submit" || formObj.elements[i].type == "button" || formObj.elements[i].type == "image") {
			if (formObj.elements[i].clicked && formObj.elements[i].clicked) {
				formParams = formParams + formObj.elements[i].name;
				formParams = formParams + "=";
				formParams = formParams + formObj.elements[i].value;
			}
		} else {
			formParams = formParams + formObj.elements[i].name;
			formParams = formParams + "=";
			formParams = formParams + formObj.elements[i].value;
		}
	}
	x.open("POST", formObj.action, true);
	x.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	x.setRequestHeader("Content-length", formParams.length);
	x.setRequestHeader("Connection", "close");
	x.send(encodeURI(formParams));
	return false;
}
function com_jenkov_prizetags_ajaxButtonClicked(button) {
	formObj = button.form;
	for (i = 0; i < formObj.elements.length; i++) {
		if (formObj.elements[i].type == "submit" || formObj.elements[i].type == "button" || formObj.elements[i].type == "image") {
			formObj.elements[i].clicked = false;
		}
	}
	button.clicked = true;
}
function com_jenkov_prizetags_ajaxReloadPage(url, element, time) {
	aDate = new Date();
	aDateString = aDate.getYear() + "_" + aDate.getMonth() + "_" + aDate.getDate() + "_";
	aDateString += aDate.getHours() + "_" + aDate.getMinutes() + "_" + aDate.getSeconds();
	forceRefreshParameter = "com_jenkov_prizetags_ajax_refresh=" + Math.random() * 1000000;
	forceRefreshParameter += "&com_jenkov_prizetags_ajax_time=" + aDateString;
	com_jenkov_prizetags_ajaxLoadPage(url, element);
	setTimeout("com_jenkov_prizetags_ajaxReloadPage('" + url + "', '" + element + "', " + time + ")", time);
}

