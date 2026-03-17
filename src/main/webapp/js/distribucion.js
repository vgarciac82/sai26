function removeOptions(sel) {
	while (sel.length > 0) {
		sel.removeChild(sel[0]);
	}
}
function requestToServerNA() {
	var sel = document.getElementById("prod");
	var id = sel.options[sel.selectedIndex].value;
	var req = new XMLHttp("../gstnmngr/gestion?cmd=17&class=com.afirme.gestion.AfirmeGetXMLCatalogos&action=6&tc=" + id, onServerResponseNA);
	req.doPost();
	return true;
}
function requestToServerUsersGroups() {
	var selProd = document.getElementById("prod");
	var idProd = selProd.options[selProd.selectedIndex].value;
	var sel = document.getElementById("nvlAprob");
	var tbl = document.getElementById("tblAtencion");
	var id = sel.options[sel.selectedIndex].value;
	var reqUsr = new XMLHttp("../gstnmngr/gestion?cmd=17&class=com.afirme.gestion.AfirmeGetXMLCatalogos&action=7&tc=" + idProd + "&nap=" + id + "&t=true", onServerResponseUsers);
	var reqGrp = new XMLHttp("../gstnmngr/gestion?cmd=17&class=com.afirme.gestion.AfirmeGetXMLCatalogos&action=7&tc=" + idProd + "&nap=" + id + "&t=false", onServerResponseGroups);
	reqUsr.doPost();
	reqGrp.doPost();
	tbl.style.display = "";
	return true;
}
function requestToServerCN() {
	var req = new XMLHttp("../gstnmngr/gestion?cmd=17&class=com.afirme.gestion.AfirmeGetXMLCatalogos&action=8", onServerResponseCN);
	req.doPost();
	return true;
}
function onServerResponseNA(responseXML) {
	var sel = document.getElementById("nvlAprob");
	removeOptions(sel);
	var xml = responseXML.getElementsByTagName("NivelAprobacion");
	var opt = document.createElement("option");
	opt.setAttribute("value", "-1");
	sel.appendChild(opt);
	for (var i = 0; i < xml.length; i++) {
		opt = document.createElement("option");
		opt.setAttribute("value", xml[i].getAttribute("id"));
		opt.appendChild(document.createTextNode(xml[i].text));
		sel.appendChild(opt);
	}
}
function onServerResponseUsers(responseXML) {
	var sel = document.getElementById("usrSel");
	removeOptions(sel);
	var xml = responseXML.getElementsByTagName("usuario");
	var opt = document.createElement("option");
	opt.setAttribute("value", "-1");
	sel.appendChild(opt);
	for (var i = 0; i < xml.length; i++) {
		opt = document.createElement("option");
		opt.setAttribute("value", xml[i].getAttribute("login"));
		opt.appendChild(document.createTextNode(xml[i].text));
		sel.appendChild(opt);
	}
}
function onServerResponseGroups(responseXML) {
	var sel = document.getElementById("grpSel");
	removeOptions(sel);
	var xml = responseXML.getElementsByTagName("subgrupo");
	var opt = document.createElement("option");
	opt.setAttribute("value", "-1");
	sel.appendChild(opt);
	for (var i = 0; i < xml.length; i++) {
		opt = document.createElement("option");
		opt.setAttribute("value", xml[i].getAttribute("id"));
		opt.appendChild(document.createTextNode(xml[i].text));
		sel.appendChild(opt);
	}
}
function onServerResponseCN(responseXML) {
	var tbl = document.getElementById("tblCN");
	while (tbl.tBodies[0].rows.length > 0) {
		tbl.tBodies[0].deleteRow(tbl.tBodies[0].rows.length - 1);
	}
	var xml = responseXML.getElementsByTagName("area");
	for (var i = 0; i < xml.length; i++) {
		var tr = document.createElement("tr");
		var col1 = document.createElement("td");
		var col2 = document.createElement("td");
		var col3 = document.createElement("td");
		var r = document.createElement('<input name="cn" onclick="showAceptar()">');
		r.id = "row_sel_cv_" + i;
		r.type = "checkbox";
		r.value = xml[i].getAttribute("id");
		var idText = r.value;
		if (idText.length < 3) {
			var zeros = '000';
			idText = zeros.substring(0, 3 - idText.length) + idText;
		}
		var idCN = document.createTextNode(idText);
		var CN = document.createTextNode(xml[i].text);
		col1.appendChild(r);
		col2.appendChild(idCN);
		col3.appendChild(CN);
		col2.align = "right";
		tr.appendChild(col1);
		tr.appendChild(col2);
		tr.appendChild(col3);
		tbl.tBodies[0].appendChild(tr);
	}
}
function displayUsrGroupAtention() {
	var usr = document.getElementById("tdUsr");
	var grp = document.getElementById("tdGrp");
	usr.style.display = (usr.style.display === "") ? "none" : "";
	grp.style.display = (grp.style.display === "") ? "none" : "";
}
function showCN() {
	var div = document.getElementById("tableContainer");
	div.style.display = "";
}
function showAceptar() {
	var disabled = true;
	var chks = document.getElementsByName("cn");
	var pb = document.getElementById("pb_ok");
	for (var i = 0; i < chks.length; i++) {
		if (chks[i].checked) {
			disabled = false;
			break;
		}
	}
	pb.disabled = disabled;
}
