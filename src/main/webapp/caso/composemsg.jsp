<%@ page import="com.syc.gestion.servlet.GestionInterface"%>
<%boolean close = "true".equals(request.getParameter("close"));%>
<html>
	<head>
		<title>Enviar Mensaje</title>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link type="text/css" href="../css/gestion.css" rel="stylesheet">
		<script type="text/javascript" src="../js/ajax-commons.js"></script>
		<script type="text/javascript">
		var docXML;
		function validityLength(theField, length)
		{	var retval = true;
			var usedLength = document.getElementById("usedLength");
			usedLength.innerText = theField.value.length;
			if (theField.value.length > length)
			{	if ((window.event.keyCode != 8) &&
				   (window.event.keyCode != 37) &&
				   (window.event.keyCode != 38) &&
				   (window.event.keyCode != 39) &&
				   (window.event.keyCode != 40) &&
				   (window.event.keyCode != 46))
				{	alert("Se alcanzo la longitud maxima.");
					theField.value = theField.value.substring(0,2000);
					retval = false;
				}
			}
			return retval;
		}
		function requestToServer() {
			var req = new XMLHttp("../gstnmngr/gestion?cmd=<%=GestionInterface.CMD_GET_USRES_GROUPS%>",onServerResponse);
			req.doPost();
			return true;
		}
		function onServerResponse(responseXML) {
			docXML = responseXML;
			removeOptions(document.getElementById("enviar"));
			updUsersAndGroups();
			document.getElementById('paraBox').style.visibility='visible';
		}
		function updUsersAndGroups() {
			var sel = document.getElementById("usuario");
			removeOptions(sel);
			var xml = docXML.getElementsByTagName("usuario");
			for (var i = 0; i < xml.length; i++) {
				if (xml[i].getAttribute("inList") == "true")
					continue;
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
				if (xml[i].getAttribute("inList") == "true")
					continue;
				var id = xml[i].getAttribute("nombre");
				var name = xml[i].firstChild.firstChild.nodeValue;
				var opt = document.createElement("option");
				opt.setAttribute("type", "grp");
				opt.setAttribute("value", id);
				opt.appendChild(document.createTextNode(name));
				sel.appendChild(opt);
			}
		}
		function addSelectedUserToList(sel, all) {
			var list = document.getElementById("enviar");
			for (var i = 0; i < sel.length; i++) {
				if (all || sel[i].selected) {
					var id = sel[i].getAttribute("value");
					var xml = docXML.selectSingleNode(".//usuario[@login='" + id + "']");
					xml.setAttribute("inList", "true");
					var name = sel[i].firstChild.nodeValue;
					var opt = document.createElement("option");
					opt.setAttribute("type", "usr");
					opt.setAttribute("value", id);
					opt.appendChild(document.createTextNode(name));
					list.appendChild(opt);
				}
			}
			updUsersAndGroups();
		}
		function addSelectedGroupToList(sel, all) {
			var list = document.getElementById("enviar");
			for (var i = 0; i < sel.length; i++) {
				if (all || sel[i].selected) {
					var id = sel[i].getAttribute("value");
					var xml = docXML.selectSingleNode(".//grupo[@nombre='" + id + "']");
					xml.setAttribute("inList", "true");
					var name = sel[i].firstChild.nodeValue;
					var opt = document.createElement("option");
					opt.setAttribute("type", "grp");
					opt.setAttribute("value", id);
					opt.appendChild(document.createTextNode(name));
					list.appendChild(opt);
				}
			}
			updUsersAndGroups();
		}
		function unSelectFromList(sel, all) {
			var del = [], j = 0;
			for(var i = 0; i < sel.length; i++) {
				if (all || sel[i].selected) {
					var id = sel[i].getAttribute("value");
					var xml = docXML.selectSingleNode(".//(usuario[@login='" + id + "'] | grupo[@nombre='" + id + "'])");
					xml.removeAttribute("inList");
					del[j++] = sel[i];
				}
			}
			for( var i = 0; i < del.length; i++)
				sel.removeChild(del[i]);
			updUsersAndGroups();
		}
		function removeOptions(sel) {
			while (sel.length > 0)
				sel.removeChild(sel[0]);
		}
		function copyListSend() {
			var list = document.getElementById("enviar");
			var para = document.getElementById("para");
			var pName = document.getElementById("paraName");
			var token = "";
			para.value = pName.value = "";
			for (var i = 0; i < list.length; i++) {
				para.value += token + list[i].getAttribute("value");
				pName.value += token + list[i].firstChild.nodeValue;
				token = ";";
			}
			document.getElementById('paraBox').style.visibility='hidden';
		}
		</script>
	</head>
	<body>
		<div id="paraBox">
			<table width="100%" height="100%" border="0">
				<tr>
					<td height="1%" colspan="4">Seleccione a quien(es) quiere enviar un mensaje<hr></td>
				</tr>
				<tr>
					<td width="48%" height="1%" align="center">Usuarios</td>
					<td width="1%" height="1%" rowspan="2">
						<table align="center">
							<tr>
								<td align="center" valign="middle"><input type="button" value="&gt;" onClick="addSelectedUserToList(document.getElementById('usuario'),false)" style="width:22px;"/></td>
							</tr>
							<tr>
								<td align="center" valign="middle"><input type="button" value="&gt;&gt;" onClick="addSelectedUserToList(document.getElementById('usuario'),true)" style="width:22px;"/></td>
							</tr>
						</table>
					</td>
					<td width="48%" height="1%" align="center">Enviar a</td>
					<td width="1%" rowspan="4" align="center">
						<table align="center">
							<tr>
								<td align="center" valign="middle"><input type="button" value="&lt;" onClick="unSelectFromList(document.getElementById('enviar'), false)" style="width:22px;"/></td>
							</tr>
							<tr>
								<td align="center" valign="middle"><input type="button" value="&lt;&lt;" onClick="unSelectFromList(document.getElementById('enviar'), true)" style="width:22px;"/></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="49%"><select id="usuario" size="8" multiple style="width: 100%;"></select></td>
					<td height="49%" rowspan="3"><select id="enviar" name="enviar" size="17" multiple style="width: 100%;"></select></td>
				</tr>
				<tr>
					<td height="1%" align="center">Grupos</td>
					<td height="1%" rowspan="2">
						<table align="center">
							<tr>
								<td align="center" valign="middle"><input type="button" value="&gt;" onClick="addSelectedGroupToList(document.getElementById('grupo'),false)" style="width:22px;"/></td>
							</tr>
							<tr>
								<td align="center" valign="middle"><input type="button" value="&gt;&gt;" onClick="addSelectedGroupToList(document.getElementById('grupo'),true)" style="width:22px;"/></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="48%"><select id="grupo" size="7" multiple style="width: 100%;"></select></td>
				</tr>
				<tr>
					<td height="1%" colspan="4">
						<table align="right">
							<tr>
								<td><input type="button" value="Cerrar" onClick="document.getElementById('paraBox').style.visibility='hidden'"></td>
								<td><input type="button" value="Aceptar" onClick="copyListSend()"></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</div>
		<table width="100%" height="100%">
			<tr>
				<td><h3>Enviar Mensaje</h3>
				</td>
			</tr>
			<tr>
				<td>
					<table align="center">
						<tr>
							<td>
								<form action="../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_SEND_MSG%>&close=<%=close%>" method="post">
									<input type="hidden" id="para" name="para">
									<table>
										<tr>
											<td><input id="pb_para" type="button" value="Para:" onClick="requestToServer()"></td>
											<td><input id="paraName" type="text" size="80" readOnly></td>
											<td>&nbsp;</td>
										</tr>
										<tr>
											<td>Asunto:</td>
											<td><input id="asunto" type="text" name="asunto" size="80" maxlength="255"></td>
											<td align="right">
												<table>
													<tr>
														<td><input name="pb_reset" type="reset" value="Limpiar"></td>
														<td><input name="pb_send" type="submit" value="Enviar"></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td colspan="3" align="left">
												<table cellpadding="0" cellspacing="0">
													<tr>
														<td>Total de car&aacute;cteres permitidos 2000 usuados&nbsp;</td>
														<td id="usedLength">0</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td colspan="3"><textarea id="body" cols="97" rows="21" name="body" onKeyUp="event.returnValue = validityLength(this,2000)" onKeyDown="event.returnValue = validityLength(this,2000)"></textarea></td>
										</tr>
									</table>
								</form>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</body>
</html>
