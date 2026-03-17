
<%
	boolean isFolder = "true".equals(request.getParameter("fldr"));
	boolean isDocImg = "true".equals(request.getParameter("img"));
	String name = request.getParameter("name");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title><%=isFolder ? "Carpeta \"" + name + "\" creada" : "Documento \"" + name + "\" creado"%> exitosamente</title>
		<link rel="stylesheet" href="../css/gestion.css" type="text/css" />
		<style type="text/css">
		html body {
			margin: 0px;
			border: 0px;
			padding: 0px;
			overflow: hidden;
		}
		</style>
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
								<h2>
									<%=isFolder ? "Carpeta \"" + name + "\" creada" : "Documento \"" + name + "\" creado"%>
									exitosamente
								</h2>
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
