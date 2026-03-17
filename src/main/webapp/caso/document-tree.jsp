<%@ taglib uri="/WEB-INF/tlds/templatetag.tld" prefix="tmpl" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>Arbol Documento</title>
		<link rel="stylesheet" href="../css/gestion.css" type="text/css"/>
		<style>
		html, body {
			padding: 0px;
			margin: 0px;
			overflow: hidden;

		}
		</style>
	</head>
	<body>
		<table width="100%" height="100%" cellpadding="0" cellspacing="0">			
			<tr valign="top">
				<td height="99%">
					<div id="treeContainer" style="width: 100%; height: 100%; overflow: auto;">
						<tmpl:include uri="arbol.jsp"/>
					</div>
				</td>
			</tr>
		</table>
	</body>
</html>
