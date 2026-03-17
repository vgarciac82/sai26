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
		createEmptyTable(tbl, 7, "Sin Operaciones");
		return;
	} else {
		clearTableRows(tbl);
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
		var resp = document.createElement("<textarea id=\"o_os_resp_" + rows[i].id_oper + "\" readonly>");
		var oper = document.createElement("<textarea id=\"o_os_oper_" + rows[i].id_oper + "\" readonly>");
		r.id = "row_sel_o_" + i;
		r.type = "radio";
		r.value = rows[i].id_oper;
		r.defaultChecked = (i === 0);
		r.checked = (i === 0);
		resp.cols = oper.cols = 80;
		resp.style.overflow = oper.style.overflow = "auto";
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
		getData("SELECT * FROM cg_operacion_siguiente WHERE id_tc = " + rows[i].id_tc + " AND id_oper = " + rows[i].id_oper, showOperSigte, b);
	}
}
var newline = (navigator.appVersion.lastIndexOf("Win") != -1) ? "\n\r" : "\n";
function showOperSigte(rs,b) {
	var rows = rs.row;
	if (rows.length > 0) {
		var resp = document.getElementById("o_os_resp_" + rows[0].id_oper);
		var oper = document.getElementById("o_os_oper_" + rows[0].id_oper);
		for (var i = 0; i < rows.length; i++) {
			resp.appendChild(document.createTextNode(rows[i].os_responsable + newline));
			resp.readOnly = b;
			oper.appendChild(document.createTextNode(rows[i].os_operacion + newline));
			oper.readOnly = b;
		}
	}
}
