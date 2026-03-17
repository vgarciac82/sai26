
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
		r.value = rows[i].id_tcv;
		r.defaultChecked = (i === 0);
		r.checked = (i === 0);
		tdRadio.appendChild(r);
		tdText.appendChild(document.createTextNode(rows[i].tcv_nombre));
		tr.appendChild(tdRadio);
		tr.appendChild(tdText);
		tbl.tBodies[0].appendChild(tr);
	}
}
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
		getVariables(false);
		d.style.display = "block";
		break;
	  case 3: // delete
		getVariables(false);
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
		getVariables(true);
		d.style.display = "block";
		break;
	}
	return true;
}
function modoOper(type) {
	var f = document.getElementById("form-oper");
	var frm = document.frames["tbl-caso-oper"];
	var d = document.getElementById("caso-oper");
	var t = document.getElementById("sel_tc_descripcion");
	var grp = frm.document.getElementsByName("row_sel_o");
	var tc = document.getElementById("edo_id_tc");
	var op = document.getElementById("edo_id_oper");
	var pbOOk = document.getElementById("pb_o_ok");
  	var frmOR = document.frames["tbl-resp-oper"];
  	var pbRONew = frmOR.document.getElementById("pb_ro_new");
  	var pbROEdit = frmOR.document.getElementById("pb_ro_edit");
  	var pbRODel = frmOR.document.getElementById("pb_ro_del");
  	var pbROShow = frmOR.document.getElementById("pb_ro_show");
	pbROShow.style.display = "block";
  	frmOR.createEmptyTable(frmOR.document.getElementById("tblRespOper"), 7, "Sin Responsable/Operacion");
  	tc.value = t.value;
  	op.value = getRadioGroupSelected(grp);
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
			getOperaciones(type == 4);
		}
		pbRONew.style.display = (type == 4) ? "none" : (type == 1) ? "none": "block";
		pbROEdit.style.display = (type == 4) ? "none" : (type == 1) ? "none": "block";
		pbRODel.style.display = (type == 4) ? "none" : (type == 1) ? "none": "block";
		pbROShow.style.display = (type == 4) ? "block" : "none";
		d.style.display = "block";
		break;
	  case 3: // delete
		getOperaciones(false);
		d.style.display = "none";
		if (window.confirm("Desea borrar la operaci\xf3n del caso seleccionada?")) {
			return actionOper(true);
		}
		break;
	}
	return true;
}
function modoRespOper(type) {
	var f = document.getElementById("form-resp-oper");
	var d = document.getElementById("sgte-resp-oper");
	var frm = document.frames["tbl-resp-oper"];
	var t = document.getElementById("sel_tc_descripcion");
	var o = document.getElementById("edo_id_oper");
	var tc = document.getElementById("ero_id_tc");
	var op = document.getElementById("ero_id_oper");
	var os = document.getElementById("ero_id_oper_sigte");
	var grp = frm.document.getElementsByName("row_sel_ro");
	var resp = document.getElementById("ero_os_responsable");
	var oper = document.getElementById("ero_os_operacion");
	var pbOROk = document.getElementById("pb_or_ok");
	var pbORCancel = document.getElementById("pb_or_cancel");
	resp.value = "";
	oper.value = "";
	tc.value = t.value;
	op.value = o.value;
	os.value = getRadioGroupSelected(grp);
	f.action = "mantoCaso?type=r&mode=" + type;
	switch (type) {
	  case 1: // new
		pbOROk.style.display = "block";
		pbORCancel.style.display = "block";
		d.style.display = "block";
		break;
	  case 2: // edit
		pbOROk.style.display = "block";
		pbORCancel.style.display = "block";
		getOperacionResponsable(false);
		d.style.display = "block";
		break;
	  case 3: // delete
		getOperacionResponsable(false);
		d.style.display = "none";
		if (window.confirm("Desea borrar el Responsable/Operacion del caso seleccionada?")) {
			return actionRespOper(true);
		}
		break;
	  case 4: // show
		f.action = "";
		pbOROk.style.display = "none";
		pbORCancel.style.display = "block";
		getOperacionResponsable(true);
		d.style.display = "block";
		break;
	}
	return true;
}
function getOperacionResponsable(readonly){
	var frm = document.frames["tbl-resp-oper"];
	var grp = frm.document.getElementsByName("row_sel_ro");
	var tc = document.getElementById("sel_tc_descripcion").value;
	var o = document.getElementById("edo_id_oper").value;
	var idx = getRadioGroupSelected(grp);
	if (idx >= 0) {
		getData("SELECT * FROM cg_operacion_siguiente WHERE id_tc = " + tc + " AND id_oper = " + o + " AND id_oper_sigte = " + idx, showOperacionResponsable, readonly);
	}
}
function showOperacionResponsable(rs,b) {
	displayData(rs,"ero_",b);
}
function getOperaciones(readonly) {
	var frm = document.frames["tbl-caso-oper"];
	var grp = frm.document.getElementsByName("row_sel_o");
	var tc = document.getElementById("sel_tc_descripcion").value;
	var o = document.getElementById("edo_id_oper");
	readonly = (getOperaciones.arguments.length == 0) ? false : readonly;
	var idx = getRadioGroupSelected(grp);
	if (idx >= 0) {
		o.value = idx;
		getData("SELECT * FROM cg_operacion WHERE id_tc = " + tc + " AND id_oper = " + idx, showOperaciones, readonly);
	}
}
function showOperaciones(rs, b) {
	var frm = document.frames["tbl-resp-oper"];
	displayData(rs, "edo_", b);
	getData("SELECT * FROM cg_operacion_siguiente WHERE id_tc = " + rs.row[0].id_tc + " AND id_oper = " + rs.row[0].id_oper, frm.showTableOperSigte, b);
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
function actionOper(submit) {
	var f = document.getElementById("form-oper");
	var d = document.getElementById("caso-oper");
	if (submit) {
		save(f.action, f);
	}
	d.style.display = "none";
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
function actionRespOper(submit) {
	var f = document.getElementById("form-resp-oper");
	var d = document.getElementById("sgte-resp-oper");
	if (submit) {
		save(f.action, f);
	}
	d.style.display = "none";
	return true;
}
function getVariables(readonly) {
	var tc = document.getElementById("sel_tc_descripcion").value;
	var v = document.getElementById("edv_id_tcv");
	var grp = document.getElementsByName("row_sel_cv");
	readonly = (getVariables.arguments.length == 0) ? false : readonly;
	var idx = getRadioGroupSelected(grp);
	if (idx >= 0) {
		v.value = idx;
		getData("SELECT * FROM cg_tipo_caso_variable WHERE id_tc = " + tc + " AND id_tcv = " + idx, showVariables, readonly);
	}
}
function showVariables(rs, b) {
	displayData(rs, "edv_", b);
}
