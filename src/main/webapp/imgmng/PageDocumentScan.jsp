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
<title>Escaneo de Documentos</title>
<link href="../css/fortimax_sistema.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
<!--
function addRow(tableID, imagePath) {
	var count = document.getElementById("totFile");
	var table = document.getElementById(tableID);
	var lastRow = table.rows.length;
	var fieldName = "file" + lastRow;
	var row = table.insertRow(table.rows.length);
	row.insertCell().innerHTML = "<a id=\"img" + lastRow + "\" href=\"javascript:showImage(document.getElementById('img" + lastRow + "').parentNode.parentNode.rowIndex)\" onmouseover=\"window.status='Seleccionar'; return true;\">p&aacute;gina"+ (lastRow + 1) + "</a>";
	count.value = table.rows.length;
}

function delRow(tableID) {
	var currRow = document.getElementById("currentRow");
	var count = document.getElementById("totFile");
	var table = document.getElementById(tableID);
	var ret = false;
	if ((currRow.value < 0) || (currRow.value.length == 0)) {
		alert((table.rows.length > 0)? "Seleccione una página a eliminar": "No hay páginas a eliminar");
	} else if (confirm("Desea eliminar la página actual? " + currRow.value)) {
		table.deleteRow(parseInt(currRow.value));
		count.value = table.rows.length;
		showImage(parseInt(currRow.value));
		ret = true;
	}
	return ret;
}

function showImage(imgIdx) {
	var img = document.getElementById("crrntImg");
	var app = document.getElementById("ScanApplet");
	var imgName = (imgIdx > -1)? app.getImagePath(imgIdx): "../imagenes/interfaz_castillo.jpg";
	var currRow = document.getElementById("currentRow");

	currRow.value = imgIdx;
	img.src = imgName;
	img.alt = (imgIdx > -1)? "Página" + (imgIdx + 1): "FortImax.com";
}

function replace(sMarkup){
    var oNewDoc = document.open("text/html", "replace");
    oNewDoc.write(sMarkup);
    oNewDoc.close();
}
//-->
</script>
</head>
<body leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0" marginwidth="0" marginheight="0">
<input type="hidden" id="currentRow" value="-1">
<table width="100%" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td align="center" class="bordetit">&nbsp;&nbsp;Escaneo de Documentos</td>
		<td width="17" align="right" class="bordetit">
			<%--<a href="javascript:window.close();">
				<img src="../imagenes/b_cerrar.gif" alt="Cerra ventana" width="17" height="13" border="0">
			</a>--%>
		</td>
	</tr>
	<!--tr>
		<td colspan="2" align="center">Los documentos escaneados ser&aacute;n resguardados con formato JPEG</td>
	</tr-->
	<tr>
		<td colspan="2" align="center"><img src="../imagenes/espacio.gif" alt="FortImax" width="1" height="5"></td>
	</tr>
	<tr>
		<td colspan="2">
		<table cellpadding="0" cellspacing="2">
			<tr>
				<td colspan="2">
				<table width="100%">
					<tr>
						<td>
							<jsp:plugin type="applet"
										name="ScanApplet"
										codebase="../jars"
										archive="scanner.jar"
										code="com.syc.client.ScanApplet"
										jreversion="1.5.0_03"
										width="100%"
										height="28"
										iepluginurl="http://java.sun.com/update/1.5.0/jinstall-1_5_0_03-windows-i586.cab#Version=1,5,0,0"
										nspluginurl="http://java.sun.com/update/1.5.0/jinstall-1_5_0_03-windows-i586.cab#Version=1,5,0,0">
								<jsp:params>
									<jsp:param name="token" value="<%=session.getId()%>"/>
									<jsp:param name="idnode" value="<%=idNode%>"/>
									<jsp:param name="tableid" value="tableFile"/>
									<jsp:param name="addfunc" value="addRow"/>
									<jsp:param name="delfunc" value="delRow"/>
									<jsp:param name="showfunc" value="showImage"/>
									<jsp:param name="replfunc" value="replace"/>
									<jsp:param name="component" value="../upload"/>
									<jsp:param name="qryString" value="close=true"/>
								</jsp:params>
								<jsp:fallback>Su Browser no soporta Applets!</jsp:fallback>
							</jsp:plugin>
						</td>
					</tr>
				</table>
				</td>
			</tr>
			<tr id="viewImg">
				<td width="90%" align="center" valign="top">
					<div style="overflow: auto; width: 100%; height: 400; border-right: #333399 1px solid; border-top: #333399 1px solid; border-left: #333399 1px solid; border-bottom: #333399 1px solid">
						<img id="crrntImg" src="../imagenes/logotipo-cgflow.jpg" alt="FortImax.com" border="0">
					</div>
				</td>
				<td width="10%" valign="top">
					<table width="100%" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<div id="dataDiv" style="overflow: auto; width: 100%; height: 400; border-right: #333399 1px solid; border-top: #333399 1px solid; border-left: #333399 1px solid; border-bottom: #333399 1px solid">
									<table id="tableFile" cellspacing="1" cellpadding="0">
									</table>
								</div>
							</td>
						</tr>
						<tr>
							<td align="left">
								<input type="text" id="totFile" value="0" size="3" readonly>&nbsp;
								<input type="text" value="Imagenes a enviar" size="17" readonly>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</body>
</html>
