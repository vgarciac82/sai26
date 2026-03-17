/*
function displayRespOperVar(rs, id_oper, readonly) {
	var tbl = document.getElementById("tblRespOper");
	for (var j = 0; j < rs.row.length; j++) {
		if (rs.row[j].id_oper != id_oper) {
			continue;
		}
		var tr = document.createElement("tr");
		var td1 = document.createElement("<td>");
		var td2 = document.createElement("<td nowrap=\"nowrap\">");
		var td3 = document.createElement("<td nowrap=\"nowrap\">");
		var inResp = document.createElement("<textarea id=\"edo_os_resp_" + j + "\" name=\"os_responsable\">");
		var inOper = document.createElement("<textarea type=\"text\" id=\"edo_os_oper_" + j + "\" name=\"os_operacion\">");
		var rb = document.createElement("<input type=\"radio\" name=\"row_sel_ro\"" + (j == 0 ? " checked " : "") + ">");
		rb.value = rs.row[j].id_oper_sigte;
		td1.appendChild(rb);
		td1.appendChild(document.createTextNode(j + 1));
		inResp.value = rs.row[j].os_responsable;
		inResp.readOnly = readonly;
		inOper.value = rs.row[j].os_operacion;
		inOper.readOnly = readonly;
		td2.appendChild(inResp);
		td3.appendChild(inOper);
		tr.appendChild(td1);
		tr.appendChild(td2);
		tr.appendChild(td3);
		tbl.tBodies[0].appendChild(tr);
	}
}
*/
function showTableOperSigte(rs, b) {
	var rows = rs.row;
	if (rows.length > 0) {
		var tbl = document.getElementById("tblRespOper");
		clearTableRows(tbl);
		for (var i = 0; i < rows.length; i++) {
			var tr = document.createElement("tr");
			var td1 = document.createElement("<td>");
			var td2 = document.createElement("<td nowrap=\"nowrap\">");
			var td3 = document.createElement("<td nowrap=\"nowrap\">");
			var inResp = document.createElement("<textarea id=\"edo_os_resp_" + i + "\" name=\"os_responsable\">");
			var inOper = document.createElement("<textarea type=\"text\" id=\"edo_os_oper_" + i + "\" name=\"os_operacion\">");
			var rb = document.createElement("<input type=\"radio\" name=\"row_sel_ro\"" + (i == 0 ? " checked " : "") + ">");
			rb.value = rows[i].id_oper_sigte;
			td1.appendChild(rb);
			td1.appendChild(document.createTextNode(i + 1));
			inResp.value = rows[i].os_responsable;
			inResp.readOnly = b;
			inOper.value = rows[i].os_operacion;
			inOper.readOnly = b;
			td2.appendChild(inResp);
			td3.appendChild(inOper);
			tr.appendChild(td1);
			tr.appendChild(td2);
			tr.appendChild(td3);
			tbl.tBodies[0].appendChild(tr);
		}
	} else {
	  	var pbROEdit = document.getElementById("pb_ro_edit").style.display = "none";
	  	var pbRODel = document.getElementById("pb_ro_del").style.display = "none";
	  	var pbROShow = document.getElementById("pb_ro_show").style.display = "none";
  	}
}

