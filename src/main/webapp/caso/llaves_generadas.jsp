<%@ page contentType="text/html; charset=iso-8859-1" language="java"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
Usuario u = (Usuario)session.getAttribute("usuario");
boolean generadas = request.getParameter("generadas")!=null && "true".equalsIgnoreCase(request.getParameter("generadas"));
String msg = "Llave y certificado generados con &eacute;xito.<br>De clic en la liga correspondiente para almacenar en su equipo el archivo correspondiente.";
if(!generadas){
	if(session.getAttribute("errorMsg")!=null)
		msg = (String)session.getAttribute("errorMsg");
	else		
		msg = "No se genero con &eacute;xito la llave y el certificado, inténte de nuevo.";
}
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
		<title>CG-Flow / ITAM</title>
		<link href="../css/gestion.css" rel="stylesheet" type="text/css" media="all" />
	</head>
	<body>
		<table width="100%"  border="1" align="center" cellpadding="0" cellspacing="0" id="texto">					
			<tr>
				<td width="100%" align="center" valign="middle">
					<table width="100%" height="100%">
						<tr>
							<td><%=msg %></td>
						<tr>
						<%if(generadas){ %>
						<tr>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td>- <a href="../gstnmngr/showllaves.jsp?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_DOWNLOAD_KEYS%>&opt=key">Llave privada</a></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td>- <a href="../gstnmngr/showllaves.jsp?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_DOWNLOAD_KEYS%>&opt=cer">Certificado p&uacute;blico</a></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td>Password: <%=u.getPassword() %></td>
						</tr>						
						<%} %>
					</table>
				</td>
			</tr>
		</table>
	</body>
</html>
