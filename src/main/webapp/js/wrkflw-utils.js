function getData(sql, funcall, b, inXML) {
	if (getData.arguments.length == 2) {
		b = true;
		inXML = false;
	} else {
		if (getData.arguments.length == 3) {
			inXML = false;
		}
	}
	var bindArgs = {
		url:"../query",
		method:"post",
		content:{
			"respType":(inXML ? "xml" : "json"),
			"stmnt":sql
		},
		mimetype:"text/" + (inXML ? "xml" : "json"),
		load:function (type, data) {
			funcall(data, b);
		}, backButton:function () {},
		error:function (type, error) {
			dojo.debugShallow(error);
		}
	};
	dojo.io.bind(bindArgs);
}
function displayData(rs, prfx, readonly) {
	readonly = displayData.arguments.length == 2 ? false: readonly;
	var flds = rs.column;
	var rows = rs.row;
	prfx = (displayData.arguments.length == 1) ? "": prfx;
	for (var i = 0; i < rows.length; i++) {
		for (var j = 0; j < flds.length; j++) {
			var f = document.getElementById(prfx + flds[j]);
			if (f !== null) {
				f.value = eval("rows[" + i + "]." + flds[j]);
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
function getRadioGroupSelected(grp) {
	for (var i = 0; i < grp.length; i++) {
		if (grp[i].checked) {
			return parseInt(grp[i].value);
		}
	}
	return -1;
}
function save(action, callerNode) {
	dojo.io.bind({url:action, method:"POST", formNode:callerNode, load:function (type, data, evt) {
		eval(data);
	}, error:function (type, error) {
		dojo.debugShallow(error);
	}, mimetype:"text/plain"});
}
