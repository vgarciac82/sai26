<%@ page language="java" contentType="text/html; charset=ISO-8859-1"%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="com.syc.viewer.servlet.ViewerParametersInterface" %><html>
<%@ taglib uri="/WEB-INF/tlds/imagetag.tld" prefix="image" %><head>
<%	int paginateCount = 10;
	String treeNodeId = request.getParameter("select");
	String paginate = request.getParameter("paginate");
	int start = (paginate == null) ? 0 : Integer.parseInt(paginate.substring(0, paginate.indexOf("-")));
%><meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="../css/imageview.css" type="text/css" />
<link href="../css/gestion.css" rel="stylesheet">
<style>
html body form table {
	padding: 0px;
	margin: 0px;
	border: 0px;
}
</style>
<title>Listado de Imagenes</title>
<script language="javascript">
<!--
function newPage(type) {
 var objSelect = document.getElementById("paginate");
 var max = objSelect.options.length - 1;
 	for (i = 0; i < objSelect.options.length; i++) {
 		if (objSelect.options(i).selected) {
 			switch (type) {
 				case 0:	// Izq.
 					objSelect.value = objSelect.options(((i > 0) ? i - 1: 0)).value;
 					break;
 				case 1: // Der.
 					objSelect.value = objSelect.options(((i < max) ? i + 1: i)).value;
 					break;
 			}
			document.forms[0].submit();
 			break;
 		}
 	}
}
-->
</script>
</head>
<body>
<form action="ListaDeImagenes.jsp?select=<%=treeNodeId%>" method="get">
<table width="100%" height="100%" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td height="8%">
		<table align="center" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
					<a class="button" href="javascript:newPage(0)">
						<img src="../images/atras.gif" alt="Anterior" width="22" height="22" border="0">
					</a>
				</td>
				<td valign="top">
<%	String strTotPage = (String) session.getAttribute(ViewerParametersInterface.INDEX_MAX);
	if (strTotPage == null)
		strTotPage = request.getParameter(ViewerParametersInterface.INDEX_MAX);

	int totPages = Integer.parseInt(strTotPage);
	if (totPages > 0) {
		int ini = 0;
		int fin = 0;
		out.println("\t\t\t\t<select id=\"paginate\" name=\"paginate\" onchange=\"submit();\">");
		while ((fin += paginateCount) < totPages) {
			out.println(
				"\t\t\t\t  <option value=\""
					+ ini
					+ "-"
					+ fin
					+ "\""
					+ ((ini == start) ? " selected>" : ">")
					+ (ini + 1)
					+ "-"
					+ fin
					+ "</option>");
			ini = fin;
		}
		if (totPages > 0) {
			out.println(
				"\t\t\t\t  <option value=\""
					+ ini
					+ "-"
					+ totPages
					+ "\""
					+ ((ini == start) ? " selected>" : ">")
					+ (ini + 1)
					+ "-"
					+ totPages
					+ "</option>");
		}
		out.println("\t\t\t\t</select>");
	} else {
		out.println("\t\t\t\t<select>");
		out.println("\t\t\t\t  <option selected>Vacio</option>");
		out.println("\t\t\t\t</select>");
	}%>		</td>
				<td>
					<a class="button" href="javascript:newPage(1)">
						<img src="../images/adelante.gif" alt="Siguiente" width="22" height="22" border="0">
					</a>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td height="92%">
		<div id="imgDivList" style="overflow: auto; width: 100%; height: 100%; top: 0; border-right: #333399 1px solid; border-top: #333399 1px solid; border-left: #333399 1px solid; border-bottom: #333399 1px solid">
			<image:imagelist param="select" paginateCount="<%=paginateCount%>" paginate="paginate" thumbnailPrefix="_thumbnail" />
		</div>
		</td>
	</tr>
</table>
</form>
</body>
</html>
