
function getData(sql, funcall, b, inXML) {
	if (getData.arguments.length == 2) {
		b = true;
		inXML = false;
	} else {
		if (getData.arguments.length == 3) {
			inXML = false;
		}
	}
	var bindArgs = {url:"../query", method:"post", content:{"respType":(inXML ? "xml" : "json"), "stmnt":sql}, mimetype:"text/" + (inXML ? "xml" : "json"), load:function (type, data) {
		funcall(data, b);
	}, backButton:function () {
	}, error:function (type, error) {
		dojo.debugShallow(error);
	}};
	dojo.io.bind(bindArgs);
}
function displayData(rs, prfx) {
	var flds = rs.column;
	var rows = rs.row;
	if (displayData.arguments.length == 1) {
		prfx = "";
	}
	for (var i = 0; i < rows.length; i++) {
		for (var j = 0; j < flds.length; j++) {
			var f = document.getElementById(prfx + flds[j]);
			if (f !== null) {
				f.value = eval("rows[" + i + "]." + flds[j]);
			}
		}
	}
}
function getRadioGroupSelected(grp) {
	for (var i = 0; i < grp.length; i++) {
		if (grp[i].checked) {
			return parseInt(grp[i].value);
		}
	}
	return -1;
}
function displayEditVar(grp, rs, readonly) {
	var v = document.getElementById("edv_id_tcv");
	readonly = (displayEditVar.arguments.length == 2) ? false : readonly;
	var idx = getRadioGroupSelected(grp);
	if (idx >= 0) {
		var flds = rs.column;
		v.value = idx;
		for (var i = 0; i < flds.length; i++) {
			var f = document.getElementById("edv_" + flds[i]);
			if (f !== null) {
				f.value = eval("rs.row[" + idx + "]." + flds[i]);
				f.readOnly = readonly;
				if ((f.type == "checkbox") || (f.type == "radio")) {
					f.checked = (f.value === "S");
				}
			}
		}
	}
}
function displayOprVar(grp, rs, readonly) {
	var o = document.getElementById("edo_id_oper");
	readonly = (displayOprVar.arguments.length == 2) ? false : readonly;
	var idx = getRadioGroupSelected(grp);
	if (idx >= 0) {
		var flds = rs.column;
		o.value = idx;
		for (var i = 0; i < flds.length; i++) {
			var f = document.getElementById("edo_" + flds[i]);
			if (f !== null) {
				f.value = eval("rs.row[" + idx + "]." + flds[i]);
				f.readOnly = readonly;
			}
		}
		
		var tbl = document.frames["tbl-resp-oper"].document.getElementById("tblRespOper");
		var frm = document.frames["tbl-caso-oper"];
		var orRS = (currentOprSgte.row !== undefined) ? currentOperSgte : frm.currentOprSgte;
		if (orRS.row !== undefined) {
			clearTableRows(tbl);
			document.frames["tbl-resp-oper"].displayRespOperVar(orRS,o.value,readonly);
		} else {
			createEmptyTable(tbl,3,"Sin Operacion/Responsable");
		}
	}
}
function displayEditOR(grp, rs, readonly) {
	var or = document.getElementById("ero_id_oper_sigte");
	readonly = (displayEditOR.arguments.length == 2) ? false : readonly;
	var idx = getRadioGroupSelected(grp);
	if (idx >= 0) {
		var flds = rs.column;
		or.value = idx;
		for (var i = 0; i < flds.length; i++) {
			var f = document.getElementById("ero_" + flds[i]);
			if (f !== null) {
				f.value = eval("rs.row[" + idx + "]." + flds[i]);
				f.readOnly = readonly;
				if ((f.type == "checkbox") || (f.type == "radio")) {
					f.checked = (f.value === "S");
				}
			}
		}
	}
}
function clearTableRows(tbl) {
	while (tbl.tBodies[0].rows.length > 0) {
		tbl.tBodies[0].deleteRow(tbl.tBodies[0].rows.length - 1);
	}
}
function createEmptyTable(tbl, cspan, text) {
	var tr = document.createElement("tr");
	var td = document.createElement("td");
	td.colSpan = cspan;
	td.appendChild(document.createTextNode(text));
	tr.appendChild(td.cloneNode(true));
	clearTableRows(tbl);
	tbl.tBodies[0].appendChild(tr);
}
function modoVar(type) {
	var f = document.getElementById("form-var");
	var d = document.getElementById("caso-var");
	var inTcvTipo = document.getElementById("shw_tcv_tipo");
	var selTcvTipo = document.getElementById("edv_tcv_tipo");
	var pbTcvOk = document.getElementById("pb_tcv_ok");
	f.action = "mantoCaso?type=v&mode=" + type;
	switch (type) {
	  case 1: // new
		inTcvTipo.style.display = "none";
		selTcvTipo.style.display = "block";
		pbTcvOk.style.display = "block";
		d.style.display = "block";
		break;
	  case 2: // edit
		inTcvTipo.style.display = "none";
		selTcvTipo.style.display = "block";
		pbTcvOk.style.display = "block";
		displayEditVar(document.getElementsByName("row_sel_cv"), currentVars, false);
		d.style.display = "block";
		break;
	  case 3: // delete
		displayEditVar(document.getElementsByName("row_sel_cv"), currentVars, false);
		d.style.display = "none";
		if (window.confirm("Desea borrar la variable del caso seleccionada?")) {
			return actionVar(true);
		}
		break;
	  case 4: // show
		f.action = "";
		inTcvTipo.value = selTcvTipo.options[selTcvTipo.selectedIndex].text;
		inTcvTipo.style.display = "block";
		selTcvTipo.style.display = "none";
		pbTcvOk.style.display = "none";
		displayEditVar(document.getElementsByName("row_sel_cv"), currentVars, true);
		d.style.display = "block";
		break;
	}
	return true;
}
function actionVar(submit) {
	var f = document.getElementById("form-var");
	var d = document.getElementById("caso-var");
	if (submit) {
		save(f.action, f);
	}
	d.style.display = "none";
	return true;
}
function modoOper(type) {
	var f = document.getElementById("form-oper");
	var frm = document.frames["tbl-caso-oper"];
	var d = document.getElementById("caso-oper");
	var pbOOk = document.getElementById("pb_o_ok");
	f.action = "mantoCaso?type=o&mode=" + type;
	switch (type) {
	  case 1: // new
	  case 2: // edit
	  case 4: // show
		if (type == 4) {
			f.action = "";
		}
		pbOOk.style.display = (type == 4) ? "none" : "block";
		if (type != 1) {
			displayOprVar(frm.document.getElementsByName("row_sel_o"), frm.currentOpr, (type == 4));
		}
		d.style.display = "block";
		break;
	  case 3: // delete
		displayOprVar(frm.document.getElementsByName("row_sel_o"), frm.currentOpr, false);
		d.style.display = "none";
		if (window.confirm("Desea borrar la operaci\xf3n del caso seleccionada?")) {
			return actionOper(true);
		}
		break;
	}
	return true;
}
function actionOper(submit) {
	var f = document.getElementById("form-oper");
	var d = document.getElementById("caso-oper");
	if (submit) {
		save(f.action, f);
	}
	d.style.display = "none";
	return true;
}
function modoRespOper(type) {
	var f = document.getElementById("form-resp-oper");
	var d = document.getElementById("sgte-resp-oper");
	var frm = document.frames["tbl-resp-oper"];
	var frmOpr = document.frames["tbl-caso-oper"];
	var pbOROk = document.getElementById("pb_or_ok");
	var pbORCancel = document.getElementById("pb_or_cancel");
	f.action = "mantoCaso?type=ro&mode=" + type;
	switch (type) {
	  case 1: // new
		pbOROk.style.display = "block";
		pbORCancel.style.display = "block";
		d.style.display = "block";
		break;
	  case 2: // edit
		pbOROk.style.display = "block";
		pbORCancel.style.display = "block";
		displayEditOR(frm.document.getElementsByName("row_sel_ro"), frmOpr.currentOprSgte, false);
		d.style.display = "block";
		break;
	  case 3: // delete
		displayEditOR(frm.document.getElementsByName("row_sel_ro"), frmOpr.currentOprSgte, false);
		d.style.display = "none";
		if (window.confirm("Desea borrar el Responsable/Operacion del caso seleccionada?")) {
			return actionVar(true);
		}
		break;
	  case 4: // show
		f.action = "";
		pbOROk.style.display = "none";
		pbORCancel.style.display = "block";
		displayEditOR(frm.document.getElementsByName("row_sel_ro"), frmOpr.currentOprSgte, true);
		d.style.display = "block";
		break;
	}
	return true;
}
function actionRespOper(submit) {
	var f = document.getElementById("form-resp-oper");
	var d = document.getElementById("sgte-resp-oper");
	if (submit) {
		save(f.action, f);
	}
	d.style.display = "none";
	return true;
}
var action = -1;
function modoCaso(type) {
	var f = document.getElementById("form-caso");
	var tit = document.getElementById("titCaso");
	var inTcDesc = document.getElementById("in_tc_descripcion");
	var inTcGav = document.getElementById("in_tc_gaveta_asociada");
	var selTcDesc = document.getElementById("sel_tc_descripcion");
	var selTcGav = document.getElementById("sel_tc_gaveta_asociada");
	var inTcWho = document.getElementById("in_tc_who_can_init");
	var inTcTime = document.getElementById("in_tc_tiempo_limite");
	var inTcAlarm = document.getElementById("in_tc_alarma");
	var pbTcOk = document.getElementById("pb_tc_ok");
	var pbTcNew = document.getElementById("pb_tc_new");
	var pbTcEdit = document.getElementById("pb_tc_edit");
	var pbTcDel = document.getElementById("pb_tc_del");
	var pbTcCancel = document.getElementById("pb_tc_cancel");
	var pbTcvNew = document.getElementById("pb_tcv_new");
	var pbTcvEdit = document.getElementById("pb_tcv_edit");
	var pbTcvDel = document.getElementById("pb_tcv_del");
	var pbTcvShow = document.getElementById("pb_tcv_show");
	var frm = document.frames["tbl-caso-oper"];
	var pbONew = frm.document.getElementById("pb_o_new");
	var pbOEdit = frm.document.getElementById("pb_o_edit");
	var pbODel = frm.document.getElementById("pb_o_del");
	var pbOShow = frm.document.getElementById("pb_o_show");
	action = type;
	f.action = "mantoCaso?type=c&mode=" + type;
	switch (type) {
	  case 1: // new
		tit.innerHTML = "&nbsp;Caso&nbsp;<font size=\"-3\">(Nuevo)&nbsp;";
		selTcDesc.style.display = "none";
		inTcDesc.readOnly = false;
		inTcDesc.style.display = "block";
		selTcGav.value = -1;
		selTcGav.style.display = "block";
		inTcGav.style.display = "none";
		inTcWho.readOnly = false;
		inTcTime.readOnly = false;
		inTcAlarm.readOnly = false;
		pbTcOk.style.display = "block";
		pbTcNew.style.display = "none";
		pbTcDel.style.display = "none";
		pbTcEdit.style.display = "none";
		pbTcCancel.style.display = "block";
		pbTcvNew.style.display = "none";
		pbTcvEdit.style.display = "none";
		pbTcvDel.style.display = "none";
		pbTcvShow.style.display = "none";
		pbONew.style.display = "none";
		pbOEdit.style.display = "none";
		pbODel.style.display = "none";
		pbOShow.style.display = "none";
		break;
	  case 2: // edit
		tit.innerHTML = "&nbsp;Caso&nbsp;<font size=\"-3\">(Editar)&nbsp;";
		selTcDesc.style.display = "none";
		inTcDesc.readOnly = false;
		inTcDesc.style.display = "block";
		selTcGav.value = inTcGav.value;
		selTcGav.style.display = "block";
		inTcGav.style.display = "none";
		inTcWho.readOnly = false;
		inTcTime.readOnly = false;
		inTcAlarm.readOnly = false;
		pbTcOk.style.display = "block";
		pbTcNew.style.display = "none";
		pbTcEdit.style.display = "none";
		pbTcDel.style.display = "block";
		pbTcCancel.style.display = "block";
		pbTcvNew.style.display = "block";
		pbTcvEdit.style.display = "block";
		pbTcvDel.style.display = "block";
		pbTcvShow.style.display = "none";
		pbONew.style.display = "block";
		pbOEdit.style.display = "block";
		pbODel.style.display = "block";
		pbOShow.style.display = "none";
		break;
	  case 3: // delete
		tit.innerHTML = "&nbsp;Caso&nbsp;<font size=\"-3\">(Borrar)&nbsp;";
		selTcDesc.style.display = "none";
		inTcDesc.style.display = "block";
		selTcGav.value = inTcGav.value;
		inTcGav.style.display = "block";
		selTcGav.style.display = "none";
		inTcDesc.readOnly = true;
		inTcWho.readOnly = true;
		inTcTime.readOnly = true;
		inTcAlarm.readOnly = true;
		pbTcOk.style.display = "block";
		pbTcNew.style.display = "none";
		pbTcEdit.style.display = "none";
		pbTcDel.style.display = "none";
		pbTcCancel.style.display = "block";
		pbTcvNew.style.display = "none";
		pbTcvEdit.style.display = "none";
		pbTcvDel.style.display = "none";
		pbTcvShow.style.display = "none";
		pbONew.style.display = "none";
		pbOEdit.style.display = "none";
		pbODel.style.display = "none";
		pbOShow.style.display = "none";
		break;
	  case 4: // inicial
		action = -1;
		f.action = "";
		tit.innerHTML = "&nbsp;Caso&nbsp;";
		selTcDesc.style.display = "block";
		inTcDesc.style.display = "none";
		inTcGav.style.display = "block";
		selTcGav.style.display = "none";
		inTcDesc.readOnly = true;
		inTcWho.readOnly = true;
		inTcTime.readOnly = true;
		inTcAlarm.readOnly = true;
		pbTcOk.style.display = "none";
		pbTcNew.style.display = "block";
		pbTcEdit.style.display = "none";
		pbTcDel.style.display = "none";
		pbTcCancel.style.display = "none";
		pbTcvNew.style.display = "none";
		pbTcvEdit.style.display = "none";
		pbTcvDel.style.display = "none";
		pbTcvShow.style.display = "none";
		pbONew.style.display = "none";
		pbOEdit.style.display = "none";
		pbODel.style.display = "none";
		pbOShow.style.display = "none";
		createEmptyTable(document.getElementById("tblVar"), 2, "Sin Variables");
		frm.createEmptyTable(frm.document.getElementById("tblOper"), 7, "Sin Operaciones");
		break;
	}
	return true;
}
function actionCaso() {
	var f = document.getElementById("form-caso");
	if (action > 0) {
		if (action == 3) {
			if (window.confirm("Desea borrar el caso seleccionado?")) {
				save(f.action, f);
			}
		} else {
			save(f.action, f);
		}
	}
	return true;
}
function consultaCaso(key, b) {
	var o = document.getElementById("edo_id_tc");
	var v = document.getElementById("edv_id_tc");
	var frm = document.frames["tbl-caso-oper"];
	if (key > 0) {
		o.value = key;
		v.value = key;
		if (b === false) {
			var sel = document.getElementById("sel_tc_descripcion");
			sel.value = key;
		}
		getData("SELECT * FROM cg_tipo_caso WHERE id_tc = " + key, showCaso, b);
		getData("SELECT * FROM cg_tipo_caso_variable WHERE id_tc = " + key, showVariablesTable, b);
		getData("SELECT * FROM cg_operacion WHERE id_tc = " + key, frm.showOperacionesTable, b);
		getData("SELECT * FROM cg_operacion_siguiente WHERE id_tc = " + key, frm.showOperSigte, b);
	} else {
		var pbTcCancel = document.getElementById("pb_tc_cancel");
		pbTcCancel.click();
	}
}
function showCaso(rs, b) {
	displayData(rs, "in_");
	if (b) {
		var pbTcOk = document.getElementById("pb_tc_ok");
		var pbTcNew = document.getElementById("pb_tc_new");
		var pbTcEdit = document.getElementById("pb_tc_edit");
		var pbTcCancel = document.getElementById("pb_tc_cancel");
		pbTcOk.style.display = "none";
		pbTcNew.style.display = "none";
		pbTcEdit.style.display = "block";
		pbTcCancel.style.display = "none";
	}
}
var currentVars = {};
function showVariablesTable(rs, b) {
	var rows = rs.row;
	var tbl = document.getElementById("tblVar");
	if (b) {
		var pbTcvNew = document.getElementById("pb_tcv_new");
		var pbTcvEdit = document.getElementById("pb_tcv_edit");
		var pbTcvDel = document.getElementById("pb_tcv_del");
		var pbTcvShow = document.getElementById("pb_tcv_show");
		pbTcvNew.style.display = "none";
		pbTcvEdit.style.display = "none";
		pbTcvDel.style.display = "none";
		pbTcvShow.style.display = "block";
	}
	if (rows.length === 0) {
		createEmptyTable(tbl, 2, "Sin Variables");
		return;
	} else {
		clearTableRows(tbl);
	}
	for (var i = 0; i < rows.length; i++) {
		var tr = document.createElement("tr");
		var tdRadio = document.createElement("td");
		var tdText = document.createElement("td");
		var r = document.createElement("<input name=\"row_sel_cv\">");
		r.id = "row_sel_cv_" + i;
		r.type = "radio";
		r.value = i;
		r.defaultChecked = (i === 0);
		r.checked = (i === 0);
		tdRadio.appendChild(r);
		tdText.appendChild(document.createTextNode(rows[i].tcv_nombre));
		tr.appendChild(tdRadio);
		tr.appendChild(tdText);
		tbl.tBodies[0].appendChild(tr);
	}
	currentVars = rs;
}
var currentOprSgte = {};
function showOperSigte(rs) {
	currentOprSgte = rs;
}
var currentOpr = {};
var newline = (navigator.appVersion.lastIndexOf("Win") != -1) ? "\n\r" : "\n";
function showOperacionesTable(rs, b) {
	var parent = window.parent;
	var rows = rs.row;
	var tbl = document.getElementById("tblOper");
	if (b) {
		var pbONew = document.getElementById("pb_o_new");
		var pbOEdit = document.getElementById("pb_o_edit");
		var pbODel = document.getElementById("pb_o_del");
		var pbOShow = document.getElementById("pb_o_show");
		pbONew.style.display = "none";
		pbOEdit.style.display = "none";
		pbODel.style.display = "none";
		pbOShow.style.display = "block";
	}
	if (rows.length === 0) {
		try {
			createEmptyTable(tbl, 7, "Sin Operaciones");
		}
		catch (e) {
			parent.createEmptyTable(tbl, 7, "Sin Operaciones");
		}
		return;
	} else {
		parent.clearTableRows(tbl);
	}
	for (var i = 0; i < rows.length; i++) {
		var tr = document.createElement("tr");
		var tdRadio = document.createElement("td");
		var td1 = document.createElement("<td nowrap=\"nowrap\">");
		var td2 = document.createElement("<td nowrap=\"nowrap\">");
		var td3 = document.createElement("<td nowrap=\"nowrap\">");
		var td4 = document.createElement("<td nowrap=\"nowrap\">");
		var td5 = document.createElement("<td nowrap=\"nowrap\">");
		var td6 = document.createElement("<td nowrap=\"nowrap\">");
		var r = document.createElement("<input name=\"row_sel_o\">");
		var resp = document.createElement("textarea");
		var oper = document.createElement("textarea");
		r.id = "row_sel_o_" + i;
		r.type = "radio";
		r.value = i;
		r.defaultChecked = (i === 0);
		r.checked = (i === 0);
		resp.cols = oper.cols = 80;
		resp.style.overflow = oper.style.overflow = "auto";
		var os_rows = currentOprSgte.row;
		if (os_rows !== undefined) {
			for (var j = 0; j < os_rows.length; j++) {
				if (rows[i].id_oper != os_rows[j].id_oper) {
					continue;
				}
				resp.appendChild(document.createTextNode(os_rows[j].os_responsable + newline));
				oper.appendChild(document.createTextNode(os_rows[j].os_operacion + newline));
			}
		}
		tdRadio.appendChild(r);
		td1.appendChild(document.createTextNode(rows[i].o_numero));
		td2.appendChild(document.createTextNode(rows[i].o_nombre));
		td3.appendChild(document.createTextNode(rows[i].o_responsable));
		td4.appendChild(document.createTextNode(rows[i].o_descripcion));
		td5.appendChild(resp);
		td6.appendChild(oper);
		tr.appendChild(tdRadio);
		tr.appendChild(td1);
		tr.appendChild(td2);
		tr.appendChild(td3);
		tr.appendChild(td4);
		tr.appendChild(td5);
		tr.appendChild(td6);
		tbl.tBodies[0].appendChild(tr);
	}
	currentOpr = rs;
}
function save(action, callerNode) {
	dojo.io.bind({url:action, method:"POST", formNode:callerNode, load:function (type, data, evt) {
		eval(data);
	}, error:function (type, error) {
		dojo.debugShallow(error);
	}, mimetype:"text/plain"});
}

