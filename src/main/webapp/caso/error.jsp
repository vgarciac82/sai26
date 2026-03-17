<%@ page import="java.util.*,com.syc.gestion.core.*,com.syc.gestion.servlet.GestionInterface"%>
<%	
String err = request.getParameter("err");
int ierr = new Integer(err).intValue();
String msg = "";
switch(ierr){
	case 1:
		msg = "El acta NO fue creada con &eacute;xito. Int&eacute;nte nuevamente.";
		break;
	case 2:
		msg = "El acta NO fue firmada con &eacute;xito. Int&eacute;nte nuevamente.";
		break;
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>Mensajes de Error</title>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link type="text/css" href="../css/scrolltable.css" rel="stylesheet">
		<link type="text/css" href="../css/gestion.css" rel="stylesheet">
		<style>div.tableContainer { height: 100%; }</style>
		<script type="text/javascript" src="../js/dojo.js"></script>		
	</head>
	<body scroll="no">
		<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td height="1%"><h3>Mensajes de error</h3></td>
			</tr>
			<tr>
				<td width="100%" height="40%">
					<div id="tableContainer" class="tableContainer">
						<table  class="scrollTable">
							<thead id="fixedHeader" class="fixedHeader">
								<tr>
									<th>Mensaje</th>
								</tr>
							</thead>
							<tbody class="scrollContent">
								<tr>
									<td><%=msg %></td>
								</tr>
							</tbody>
						</table>
					</div>
				</td>
			</tr>
		</table>
	</body>
</html>
