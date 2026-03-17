function requestToServer() {
	var req = new XMLHttp("../gstnmngr/gestion?cmd=17&class=com.afirme.gestion.AfirmeGetXMLCatalogos&action=1", onServerResponseFilial);
	clearEnviar();
	req.doPost();
	return true;
}
function requestDeptos() {
	var f = document.getElementById("selFilial");
	var req = new XMLHttp("../gstnmngr/gestion?cmd=17&class=com.afirme.gestion.AfirmeGetXMLCatalogos&action=2&f="+f.value, onServerResponseDeptos);
	req.doPost();
	return true;
}
function requestAreas() {
	var f = document.getElementById("selFilial");
	var d = document.getElementById("selDepto");
	var req = new XMLHttp("../gstnmngr/gestion?cmd=17&class=com.afirme.gestion.AfirmeGetXMLCatalogos&action=3&f="+f.value+"&d="+d.value, onServerResponseAreas);
	req.doPost();
	return true;
}
function requestSgrps() {
	var f = document.getElementById("selFilial");
	var d = document.getElementById("selDepto");
	var a = document.getElementById("selArea");
	var req = new XMLHttp("../gstnmngr/gestion?cmd=17&class=com.afirme.gestion.AfirmeGetXMLCatalogos&action=4&f="+f.value+"&d="+d.value+"&a="+a.value, onServerResponseSgrps);
	req.doPost();
	return true;
}
function requestUsersAndGrps() {
	var f = document.getElementById("selFilial");
	var d = document.getElementById("selDepto");
	var a = document.getElementById("selArea");
	var s = document.getElementById("selSgrp");
	var req = new XMLHttp("../gstnmngr/gestion?cmd=17&class=com.afirme.gestion.AfirmeGetXMLCatalogos&action=5&f="+f.value+"&d="+d.value+"&a="+a.value+"&s="+s.value, onServerResponseUsersAndGrps);
	req.doPost();
	return true;
}
function onServerResponseFilial(responseXML) {
	var arrSel = new Array();
	arrSel[0] = document.getElementById("selFilial");
	arrSel[1] = document.getElementById("selDepto");
	arrSel[2] = document.getElementById("selArea");
	arrSel[3] = document.getElementById("selSgrp");
	arrSel[4] = document.getElementById("usuario");
	arrSel[5] = document.getElementById("grupo");
	arrSel[6] = document.getElementById("oper");
	for (var i = 0; i < arrSel.length; i++) {
		removeOptions(arrSel[i]);
	}
	var xml = responseXML.getElementsByTagName("filial");
	for (var i = 0; i < xml.length; i++) {
		var id = xml[i].getAttribute("id");
		var desc = xml[i].firstChild.text;
		var opt = document.createElement("option");
		opt.setAttribute("value", id);
		opt.appendChild(document.createTextNode(desc));
		if (i == 0)
			arrSel[0].appendChild(document.createElement('<option value="-1" selected>'));
		arrSel[0].appendChild(opt);
	}
	openSelRespOper();
}
function onServerResponseDeptos(responseXML) {
	var arrSel = new Array();
	arrSel[0] = document.getElementById("selDepto");
	arrSel[1] = document.getElementById("selArea");
	arrSel[2] = document.getElementById("selSgrp");
	arrSel[3] = document.getElementById("usuario");
	arrSel[4] = document.getElementById("grupo");
	arrSel[5] = document.getElementById("oper");
	for (var i = 0; i < arrSel.length; i++) {
		removeOptions(arrSel[i]);
	}
	var xml = responseXML.getElementsByTagName("depto");
	for (var i = 0; i < xml.length; i++) {
		var id = xml[i].getAttribute("id");
		var desc = xml[i].firstChild.text;
		var opt = document.createElement("option");
		opt.setAttribute("value", id);
		opt.appendChild(document.createTextNode(desc));
		if (i == 0)
			arrSel[0].appendChild(document.createElement('<option value="-1" selected>'));
		arrSel[0].appendChild(opt);
	}
}
function onServerResponseAreas(responseXML) {
	var arrSel = new Array();
	arrSel[0] = document.getElementById("selArea");
	arrSel[1] = document.getElementById("selSgrp");
	arrSel[2] = document.getElementById("usuario");
	arrSel[3] = document.getElementById("grupo");
	arrSel[4] = document.getElementById("oper");
	for (var i = 0; i < arrSel.length; i++) {
		removeOptions(arrSel[i]);
	}
	var xml = responseXML.getElementsByTagName("area");
	for (var i = 0; i < xml.length; i++) {
		var id = xml[i].getAttribute("id");
		var desc = xml[i].firstChild.text;
		var opt = document.createElement("option");
		opt.setAttribute("value", id);
		opt.appendChild(document.createTextNode(desc));
		if (i == 0)
			arrSel[0].appendChild(document.createElement('<option value="-1" selected>'));
		arrSel[0].appendChild(opt);
	}
}
function onServerResponseSgrps(responseXML) {
	var arrSel = new Array();
	arrSel[0] = document.getElementById("selSgrp");
	arrSel[1] = document.getElementById("usuario");
	arrSel[2] = document.getElementById("grupo");
	arrSel[3] = document.getElementById("oper");
	for (var i = 0; i < arrSel.length; i++) {
		removeOptions(arrSel[i]);
	}
	var xml = responseXML.getElementsByTagName("subgrupo");
	for (var i = 0; i < xml.length; i++) {
		var id = xml[i].getAttribute("id");
		var desc = xml[i].firstChild.text;
		var opt = document.createElement("option");
		opt.setAttribute("value", id);
		opt.appendChild(document.createTextNode(desc));
		if (i == 0)
			arrSel[0].appendChild(document.createElement('<option value="-1" selected>'));
		arrSel[0].appendChild(opt);
	}
}
var docXML;
function onServerResponseUsersAndGrps(responseXML) {
	docXML = responseXML;
	updUsersAndGroups();
}
function removeOptions(sel) {
	while (sel.length > 0) {
		sel.removeChild(sel[0]);
	}
}
function updUsersAndGroups() {
	var sel = document.getElementById("usuario");
	removeOptions(sel);
	var xml = docXML.getElementsByTagName("usuario");
	for (var i = 0; i < xml.length; i++) {
		if (xml[i].getAttribute("inList") == "true") {
			continue;
		}
		var id = xml[i].getAttribute("login");
		var name = xml[i].getElementsByTagName("nombre")[0].firstChild.nodeValue;
		var opt = document.createElement("option");
		opt.setAttribute("type", "usr");
		opt.setAttribute("value", id);
		opt.appendChild(document.createTextNode(name));
		sel.appendChild(opt);
	}
	sel = document.getElementById("grupo");
	removeOptions(sel);
	xml = docXML.getElementsByTagName("grupo");
	for (var i = 0; i < xml.length; i++) {
		if (xml[i].getAttribute("inList") == "true") {
			continue;
		}
		var id = xml[i].getAttribute("nombre");
		var name = xml[i].firstChild.firstChild.nodeValue;
		var opt = document.createElement("option");
		opt.setAttribute("type", "grp");
		opt.setAttribute("value", id);
		opt.appendChild(document.createTextNode(name));
		sel.appendChild(opt);
	}
	removeOptions(document.getElementById("oper"));
}
function updUserOpers(sel) {
	var usr = sel[sel.selectedIndex].getAttribute("value");
	var oprSel = document.getElementById("oper");
	var usrXml = docXML.selectSingleNode(".//usuario[@login='" + usr + "']");
	var xml = usrXml.getElementsByTagName("oper");
	removeOptions(oprSel);
	for (var i = 0; i < xml.length; i++) {
		if (xml[i].getAttribute("inList") == "true") {
			continue;
		}
		var id = xml[i].getAttribute("nombre");
		var name = xml[i].getElementsByTagName("descripcion")[0].firstChild.nodeValue;
		var opt = document.createElement("option");
		opt.setAttribute("type", "usr");
		opt.setAttribute("value", id);
		opt.appendChild(document.createTextNode(name));
		oprSel.appendChild(opt);
	}
}
function updGrpOpers(sel) {
	var grp = sel[sel.selectedIndex].getAttribute("value");
	var oprSel = document.getElementById("oper");
	var usrXml = docXML.selectSingleNode(".//grupo[@nombre='" + grp + "']");
	var xml = usrXml.getElementsByTagName("oper");
	removeOptions(oprSel);
	for (var i = 0; i < xml.length; i++) {
		if (xml[i].getAttribute("inList") == "true") {
			continue;
		}
		var id = xml[i].getAttribute("nombre");
		var name = xml[i].getElementsByTagName("descripcion")[0].firstChild.nodeValue;
		var opt = document.createElement("option");
		opt.setAttribute("type", "grp");
		opt.setAttribute("value", id);
		opt.appendChild(document.createTextNode(name));
		oprSel.appendChild(opt);
	}
}
function addSelectionToList() {
	var usrSel = document.getElementById("usuario");
	var grpSel = document.getElementById("grupo");
	var oprSel = document.getElementById("oper");
	var list = document.getElementById("enviar");
	var isUsr = (usrSel.selectedIndex > -1);
	if (oprSel.selectedIndex == -1) {
		return;
	}
	var keyId = isUsr ? usrSel[usrSel.selectedIndex].getAttribute("value") : grpSel[grpSel.selectedIndex].getAttribute("value");
	var keyName = isUsr ? usrSel[usrSel.selectedIndex].firstChild.nodeValue : grpSel[grpSel.selectedIndex].firstChild.nodeValue;
	var id = oprSel[oprSel.selectedIndex].getAttribute("value");
	var tmpXml = docXML.selectSingleNode(".//(usuario[@login='" + keyId + "'] | grupo[@nombre='" + keyId + "'])");
	var xml = tmpXml.selectSingleNode(".//oper[@nombre='" + id + "']");
	xml.setAttribute("inList", "true");
	var name = oprSel[oprSel.selectedIndex].firstChild.nodeValue;
	var opt = document.createElement("option");
	opt.setAttribute("type", "usr");
	opt.setAttribute("value", (isUsr ? keyName : keyId) + "," + id);
	opt.appendChild(document.createTextNode(keyName));
	opt.appendChild(document.createTextNode(", "));
	opt.appendChild(document.createTextNode(name));
	list.appendChild(opt);
	updUsersAndGroups();
}
function unSelectFromList(all) {
	var sel = document.getElementById("enviar");
	var del = [], j = 0;
	for (var i = 0; i < sel.length; i++) {
		if (all || sel[i].selected) {
			var id = sel[i].getAttribute("value").split(",");
			var tmpXml = docXML.selectSingleNode(".//(usuario[@login='" + id[0] + "'] | grupo[@nombre='" + id[0] + "'])");
			var xml = tmpXml.selectSingleNode(".//oper[@nombre='" + id[1].replace(/^\s*|\s*$/g, "") + "']");
			xml.removeAttribute("inList");
			del[j++] = sel[i];
		}
	}
	for (var i = 0; i < del.length; i++) {
		sel.removeChild(del[i]);
	}
	updUsersAndGroups();
}
function copyListSend() {
	var list = document.getElementById("enviar");
	var resp = document.getElementById("gstnTo");
	var oper = document.getElementById("gstnSubject");
	var rsp = document.getElementById("responsable");
	var opr = document.getElementById("operacion");
	resp.value = oper.value = "";
	for (var i = 0; i < list.length; i++) {
		var data = list[i].getAttribute("value").split(",");
		if (data.length != 2)
			continue;
		resp.value += data[0].replace(/^\s*|\s*$/g, "") + ";";
		oper.value += data[1].replace(/^\s*|\s*$/g, "") + ";";
	}
	rsp.value = resp.value;
	opr.value = oper.value;
	removeOptions(list);
	closeSelRespOper();
}
function clearEnviar() {
	removeOptions(document.getElementById("enviar"));
}
function openSelRespOper() {
	var dw = document.getElementById("datawork");
	var ro = document.getElementById("respOper");
	dw.style.visibility = "hidden";
	ro.style.visibility = "visible";
}
function closeSelRespOper() {
	var dw = document.getElementById("datawork");
	var ro = document.getElementById("respOper");
	dw.style.visibility = "visible";
	ro.style.visibility = "hidden";
}
