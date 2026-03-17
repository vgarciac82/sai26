<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%	String name = request.getParameter("name");
	String msg = (String) session.getAttribute(GestionInterface.ATT_MSG);
	if( StringUtils.isEmpty(msg) )
		msg = "Documento <strong>" + name + "</strong> borrado exitosamente";
	session.removeAttribute(GestionInterface.ATT_MSG);
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Documento "<%=name%>" borrado exitosamente</title>
	<link rel="stylesheet" href="../css/gestion.css" type="text/css" />
	<script type="text/javascript">
	parent.frames["doctree"].location.href="document-tree.jsp";
	</script>
  </head>
  <body>
		<table width="100%" height="100%">
			<tr>
				<td>
					&nbsp;
				</td>
			</tr>
			<tr>
				<td>
					<table align="center">
						<tr>
							<td>
								<h2><%=msg%></h2>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					&nbsp;
				</td>
			</tr>
		</table>
  </body>
</html>
