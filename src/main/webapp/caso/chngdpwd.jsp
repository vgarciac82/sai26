<%@ page import="com.syc.gestion.servlet.GestionInterface"%>
<%	String msg = (String) session.getAttribute(GestionInterface.ATT_MSG);
	if (msg == null)
		msg = new String();
		
	session.removeAttribute(GestionInterface.ATT_MSG);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>Contrase&ntilde;a Cambiada</title>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link type="text/css" href="../css/gestion.css" rel="stylesheet">
		<script type="text/javascript">
			setTimeout("document.getElementById('frmInbx').submit()", 2000);
		</script>
	</head>

	<body>
		<table width="100%">
			<tr>
				<td align="center" nowrap>
					<form id="frmInbx" action="../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_OPEN_INBOX%>" method="post">
						<h3><%=msg%></h3>
					</form>
				</td>
			</tr>
			<tr>
				<td>&nbsp;
					
				</td>
			</tr>
			<tr>
				<td align="center">
					<font size="-2">Esta forma se redireccionar&aacute; automaticamente en 2 segundos. Si no es as&iacute; de clic <a href="javascript:document.getElementById('frmInbx').submit()" onmouseover="window.status='';return true;"
						onmouseout="window.status='';return true;">aqui</a></font>
				</td>
			</tr>
		</table>
	</body>
</html>
