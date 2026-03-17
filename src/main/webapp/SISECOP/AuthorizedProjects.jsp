<%@page import="com.google.gson.Gson"%>
<%@page import="com.syc.gestion.core.UnidadEjecutora"%>
<%@page import="java.util.List"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="com.syc.gestion.core.UsuarioVistaBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%!Logger log = LoggerFactory.getLogger("RegisterProject.jsp");%>
<%
Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

if (u == null) {
	response.sendRedirect("../index.jsp");
	return;
}
Gson gson = new Gson();
String executiveUnit = u.getU_UR();
boolean isAdmin = (u.getRole("SISECOP_ADMIN") != null || u.getRole("ADMIN") != null
		|| u.getRole("ADMIN_RECMAT") != null);

String views = "*";
String viewsJsonArray = "[]";

if (!isAdmin) {

	UsuarioVistaBusinessLogic uvbl = new UsuarioVistaBusinessLogic(GestionInterface.ATT_CONEXION);
	List<UnidadEjecutora> unitView = uvbl.getVistasUsuario(u.getLogin(), "MATERIALES");

	views = Util.getConcatenatedViews(unitView, u);
	viewsJsonArray = gson.toJson(unitView);

}
%>
<!DOCTYPE html>
<html lang="es">

<head>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Inbox Autorizados</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
<link rel="stylesheet" href="https://cdn.datatables.net/2.0.6/css/dataTables.bootstrap5.min.css" />
<link href="../SICOVE/fontawesome/css/fontawesome.css" rel="stylesheet">
<link href="../SICOVE/fontawesome/css/brands.css" rel="stylesheet">
<link href="../SICOVE/fontawesome/css/solid.css" rel="stylesheet">

<link rel="stylesheet" href="css/style.css">
<script src="https://code.jquery.com/jquery-3.7.1.js" integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>
<scipt src="../SICOVE/fontawesome/all.js"></scipt>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script type="text/javascript">
    	
    	let user = "<%=u.getLogin()%>";
    	let views = "<%=views%>";
    	let viewList = <%=viewsJsonArray%>;
    	let executiveUnit = "<%=executiveUnit%>";
</script>
</head>

<jsp:include page="InboxAuthorized.jsp" flush="true" />

</html>