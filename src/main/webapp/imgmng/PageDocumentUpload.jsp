<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" %>
<%String idNode = request.getParameter("select");
if (idNode == null) {
	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin nodo seleccionado");
	return;
}%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Agregar im&aacute;genes al expediente</title>
<script type="text/javascript">
<!--
function formatTable(oTable) {
	var tot = document.getElementById("totFile");
	var rows = document.all(oTable).rows;
	for (var i = 1; i < rows.length; i++) {
		rows[i].setAttribute('id', i);
		if (i % 2 == 0) {
			rows[i].style.backgroundColor = "#FFFFFF";
			rows[i].style.color = "000000";
		} else {
			rows[i].style.backgroundColor = "#DEE7F0";
			rows[i].style.color = "#003366";
		}
	}
	tot.value = rows.length;
}

function delRow(tbl, tableID) {
	var table = document.getElementById(tableID);
	var obj = tbl.parentNode;
	var r = obj.parentNode;
	table.deleteRow(r.getAttribute('id'));
	formatTable(tableID);
}

function addRow(tableID) {
	var table = document.getElementById(tableID);
	var lastRow = table.rows.length;
	var fieldName = "file" + lastRow;
	var row = table.insertRow(table.rows.length);
	var cell = row.insertCell(0);
	cell.innerHTML = "<input type=\"file\" name=\"file" + lastRow + "\" id=\"file" + lastRow + "\" size=\"40\" maxlength=\"256\"\">";
	var cell1 = row.insertCell(1);
	cell1.innerHTML = "&nbsp;<input type=\"button\" value=\"Quitar\" onclick=\"delRow(this, 'tableFile')\">&nbsp;";
	formatTable(tableID);
	document.getElementById(fieldName).focus();
}

function valExtension(tableID) {
	var t = "";
	var table = document.getElementById(tableID);
	for (var i = 0; i < table.rows.length; i++) {
		var o = document.getElementById("file" + i);
		var f = o.value.toLowerCase();
		var t = f.substring(f.lastIndexOf(".") + 1);
		if (isWhitespace(t)) {
			alert("De clic en Examinar para indicar la ruta de la fotos a agregar,\n o quite las que no desee.");
			o.focus();
			return false;
		} else if (isNotValidExtension(t)) {
			alert("El tipo de archivo \"." + t + "\" no es soportado, como foto para este álbum.");
			o.focus();
			return false;
		}
	}
	return true;
}

function isNotValidExtension(e) {
	var types = ["bmp", "gif", "fpx", "jpg", "png", "pnm", "tif", "wbmp"];
	for (var j = 0; j < types.length; j++)
		if (e == types[j])
			return false;
	return true;
}

function isWhitespace(s) {
	var whitespace = " \t\n\r";
	if ((s == null) || (s.length == 0)) return true;
	for (var i = 0; i < s.length; i++) {
		var c = s.charAt(i);
		if (whitespace.indexOf(c) == -1) return false;
	}
	return true;
}
//-->
</script>
<link href="../css/fortimax_sistema.css" rel="stylesheet" type="text/css">
</head>
<body onload="formatTable('tableFile')" leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0" marginwidth="0" marginheight="0">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" id="texto">
	<tr>
		<td width="70%" align="center" class="bordetit">&nbsp;&nbsp;Agregar im&aacute;genes al expediente</td>
		<%--<td width="30%" align="right" class="bordetit">
			<a href="javascript:window.close();">
				<img src="../imagenes/b_cerrar.gif" alt="Cerrar ventana" width="17" height="13" border="0">
			</a>
		</td> --%>
	</tr>
	<tr>
		<td rowspan="2" align="center">Los formatos de im&aacute;genes soportados en el expediente son:<br>
			<strong>tif, bmp, gif, jpg, png, pnm, wbmp y fpx</strong>.
		</td>
		<td align="center">
			<%--<input type="button" value="Opci&oacute;n Optimizada" onClick="window.open('PageDocumentUploadApplet.jsp?select=<%=idNode%>','_self','',true)">--%>
		</td>
	</tr>
	<tr>
		<td align="center">
			<input type="button" value="Agregar" onClick="addRow('tableFile')">
		</td>
	</tr>
	<tr>
		<td colspan="2" align="center">
			<%--<img src="../imagenes/espacio.gif" width="1" height="5">--%>
		</td>
	</tr>
	<tr>
		<td colspan="2" align="right">
			<form action="../upload?select=<%=idNode%>&close=true&current=true" name="form1" enctype="multipart/form-data" method="post" onsubmit="return valExtension('tableFile')">
				<div id="divFileUpload" style="overflow: auto; width: 100%; height: 326; border-right: #333399 1px solid; border-top: #333399 1px solid; border-left: #333399 1px solid; border-bottom: #333399 1px solid">
					<table id="tableFile" width="100%" align="center"  >
						<tr>
							<td><input type="file" name="file0" id="file0" size="40" maxlength="256"></td>
						</tr>
					</table>
				</div>
				<input type="text" id="totFile" value="" size="3" readonly> &nbsp;&nbsp; <input type="submit" value="Enviar">
			</form>
		</td>
	</tr>
</table>
</body>
</html>
