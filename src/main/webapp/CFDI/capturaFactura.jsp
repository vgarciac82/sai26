<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page contentType="text/html; charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%
    Usuario user = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

    if (user == null) {
        response.sendRedirect("../index.jsp");
        return;
    }

    String id = StringUtils.trimToEmpty( request.getParameter("id") );
%>

<!DOCTYPE html>
<html lang="es">
	<head>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<title>Crear Factura.</title>
		<link
			href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
			rel="stylesheet"
			integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
			crossorigin="anonymous"
			defer />
		<link
			href="https://cdn.datatables.net/2.1.5/css/dataTables.dataTables.min.css"
			rel="stylesheet"
			defer />
		<link
			href="https://cdn.datatables.net/1.13.1/css/dataTables.bootstrap5.min.css"
			rel="stylesheet"
			crossorigin="anonymous"
			defer />
		<link href="../SICOVE/fontawesome/css/fontawesome.css" rel="stylesheet" />
		<link href="../SICOVE/fontawesome/css/brands.css" rel="stylesheet" />
		<link href="../SICOVE/fontawesome/css/solid.css" rel="stylesheet" />
		<script type="text/javascript">
			let idInvoice = "<%=id%>";
		</script>
	</head>
    <jsp:include page="capturaFactura.html" />
</html>