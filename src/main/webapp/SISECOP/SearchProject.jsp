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

%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Página de Búsqueda</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <script>
        const isAdmin = <%=isAdmin%>;
        const URL_ELASTIC_SEARCH = "<%=URL_ELASTIC_SEARCH%>";
    </script>
</head>
<body>
    <div class="container mt-5">
        <!-- Formulario de búsqueda -->
        <div class="row">
            <div class="col-md-9">
                <form id="searchForm">
                    <div class="form-group">
                        <label for="folio">Folio:</label>
                        <input type="text" class="form-control" id="folio">
                    </div>
                    <div class="form-group">
                        <label for="titulo">Título:</label>
                        <input type="text" class="form-control" id="titulo">
                    </div>
                    <div class="form-group">
                        <label for="objetivos">Objetivos:</label>
                        <textarea class="form-control" id="objetivos" rows="3"></textarea>
                    </div>
                    
                     
                </form>
            </div>
            <div class="col-md-3">
                <button type="button" class="btn btn-primary mb-2"  id="searchBtn">Buscar Coincidencias</button>
                <button type="button" class="btn btn-secondary">Limpiar</button>
            </div>
        </div>

        <!-- Tabla de resultados -->
        <div class="row mt-4">
            <div class="col-md-12">
                 
                <table class="table" >
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Folio</th>
                            <th>Título</th>
                            <th>Detalles</th>
                        </tr>
                    </thead>
                    <tbody id="resultsTable">
                        <!-- Aquí se añadirán las filas de la tabla dinámicamente -->
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
    <!-- Bootstrap JS y dependencias (Opcional: jQuery y Popper.js para Bootstrap 4) -->
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.9.4/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script src="js/SearchProject.js"></script>
</body>
</html>
