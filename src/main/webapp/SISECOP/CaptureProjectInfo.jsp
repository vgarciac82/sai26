<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.syc.gestion.core.UnidadEjecutora"%>
<%@page import="java.util.List"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="com.syc.gestion.core.UsuarioVistaBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="org.apache.log4j.Logger"%>
<%!Logger log = Logger.getLogger("RegisterProject.jsp");%>
<%

Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

if (u == null) {
	response.sendRedirect("../index.jsp");
	return;
}

ConfiguraAplicativoBusinessLogic appConfig = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
Gson gson = new Gson();

String executiveUnit = u.getU_UR();
String URL_ELASTIC_SEARCH = appConfig.getSystemSetting( "URL_ELASTICSEARCH" );

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

int folio = (request.getParameter( "folio" ) == null? -1:Integer.parseInt( request.getParameter( "folio" ) ));

%>
<!DOCTYPE html>
<html lang="es">

<head>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Alta de Proyecto</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
<link rel="stylesheet" href="https://cdn.datatables.net/2.0.6/css/dataTables.bootstrap5.min.css" />
<link href="../SICOVE/fontawesome/css/fontawesome.css" rel="stylesheet">
<link href="../SICOVE/fontawesome/css/brands.css" rel="stylesheet">
<link href="../SICOVE/fontawesome/css/solid.css" rel="stylesheet">

<link rel="stylesheet" href="css/style.css">
<script src="https://code.jquery.com/jquery-3.6.3.min.js"></script>
<scipt src="../SICOVE/fontawesome/all.js"></scipt>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="../Generador/js/jquery.blockUI-2.70.0.js"></script>
<script type="text/javascript">
    	
		const URL_ELASTICSEARCH = '<%=URL_ELASTIC_SEARCH%>';

    	let views = "<%=views%>";
    	let viewList = <%=viewsJsonArray%>;
    	let executiveUnit = "<%=executiveUnit%>";
    	let servicioId = <%=folio%>;
        let status = 0;
        let userLogin = "<%=u.getLogin()%>";
        
</script>
</head>

<jsp:include page="CaptureProjectUI.jsp" flush="true" />

</html>