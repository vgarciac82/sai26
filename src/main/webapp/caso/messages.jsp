<%@ page import="java.util.*,com.syc.gestion.core.*,com.syc.gestion.servlet.GestionInterface"%>
<%	String type = request.getParameter(GestionInterface.PRM_MSG_TYPE);
	String msgType = new String();
	boolean update = false;
	
	switch (Integer.parseInt(type)) {
		case GestionInterface.CMD_MSG_SENDED :
			msgType = "Enviados";
			break;
		case GestionInterface.CMD_MSG_RECIVED :
			msgType = "Recibidos";
			update = true;
			break;
		case GestionInterface.CMD_MSG_READED :
			msgType = "Leidos";
			break;
	}
	List l = (List) session.getAttribute(GestionInterface.ATT_MSG);
	if (l == null)
		l = new ArrayList();

	session.removeAttribute(GestionInterface.ATT_MSG);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>Mensajes&nbsp;<%=msgType%></title>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link type="text/css" href="../css/scrolltable.css" rel="stylesheet">
		<link type="text/css" href="../css/gestion.css" rel="stylesheet">
		<style>div.tableContainer { height: 100%; }</style>
		<script type="text/javascript" src="../js/dojo.js"></script>
		<script type="text/javascript">
		function readMessage(i, s, a) {
	        var bindArgs = {
				url: "../gstnmngr/gestion?cmd=<%=GestionInterface.CMD_GET_BODY_MSG%>",
				method: "post",
				content: { "id_msg": i, "msg_status": s, "update": a},
				mimetype: "text/html",
				load: function (type, data) {onServerResponse(data, "read");},
				backButton: function () {},
				error: function(type, error) {window.alert(error.message);}
			};
	        dojo.io.bind(bindArgs);
		}
		function deleteMessage(i, s) {
	        var bindArgs = {
				url: "../gstnmngr/gestion?cmd=<%=GestionInterface.CMD_DEL_MSG%>",
				method: "post",
				content: { "id_msg": i, "msg_status": s, "msg_type": "<%=msgType%>"},
				mimetype: "text/html",
				load: function (type, data) {onServerResponse(data, "delete");},
				backButton: function () {},
				error: function(type, error) {window.alert(error.message);}
			};
	        dojo.io.bind(bindArgs);
		}
		function onServerResponse(data, type) {
			switch (type) {
				case 'read':
					if (data.length <= 2002)
						document.getElementById("body").value = data;
					else
						document.write(data);
					break;
				case 'delete':
					document.write(data);
					break;
			}
		}
		</script>
	</head>

	<body scroll="no">
		<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td height="1%"><h3>Mensajes&nbsp;<%=msgType%></h3></td>
			</tr>
			<tr>
				<td width="100%" height="40%">
					<div id="tableContainer" class="tableContainer">
						<table  class="scrollTable">
							<thead id="fixedHeader" class="fixedHeader">
								<tr>
									<th>Recibido</th>
									<th>De</th>
									<th>Asunto</th>
									<th>&nbsp;</th>
									<th>&nbsp;</th>
								</tr>
							</thead>
							<tbody class="scrollContent">
<%if (l.isEmpty()) { %>			<tr>
									<td colspan="3">No hay mensajes disponibles</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
								</tr><%}%>
<%	int row = 0;
	for (Iterator iter = l.iterator(); iter.hasNext(); row++) {
		Mensaje msg = (Mensaje) iter.next();
%>								<tr class="<%=((row % 2) == 0 ? "AlternateRow" : "NormalRow")%>">
									<td><%=msg.getFecha()%></td>
									<td><%=msg.getDeNombre()%></td>
									<td><%=msg.getAsunto()%></td>
									<td align="center">
										<a href="javascript:readMessage(<%=msg.getIdMsg()%>,<%=msg.getStatus()%>,<%=update?"true":"false"%>)" onmouseover="window.status='Leer este mensaje';return true;" onmouseout="window.status='';return true;">
											<img src="../images/readmsg.png" alt="Leer este mensaje">
										</a>
									</td>
									<td align="center">
										<a href="javascript:deleteMessage(<%=msg.getIdMsg()%>,<%=msg.getStatus()%>)" onmouseover="window.status='Borrar este mensaje';return true;" onmouseout="window.status='';return true;">
											<img src="../images/delmsg.png" alt="Borrar este mensaje">
										</a>
									</td>
								</tr>
<%}%>						</tbody>
						</table>
					</div>
				</td>
			</tr>
			<tr>
				<td height="59%"><textarea id="body" style="width:100%; height:100%;" readonly></textarea></td>
			</tr>
		</table>
	</body>
</html>
