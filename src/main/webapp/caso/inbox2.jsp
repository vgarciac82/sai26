<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="utf-8"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.syc.gestion.core.TipoCaso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%
Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
if (u == null) {
	response.sendRedirect("../index.jsp");
	return;
}
CasoBusinessLogic ct = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Sistema de Administración Integral</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<style>
body {
	background-color: #f8f9fa; /* Color de fondo claro */
	display: flex;
	justify-content: center;
	align-items: center;
	min-height: 100vh; /* Ocupar al menos el 100% del alto de la ventana */
	margin: 0;
}

.center-content {
	background-color: #ffffff;
	padding: 40px;
	border-radius: 10px;
	box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
	text-align: center;
	max-width: 800px; /* Ancho mÃ¡ximo del div central */
	width: 90%; /* Ancho responsivo */
}

.center-content img {
	max-width: 100%;
	height: auto;
	margin-bottom: 30px;
}

.center-content h1 {
	color: #343a40; /* Color de texto oscuro para el tÃ­tulo */
	font-weight: 700;
	margin-bottom: 20px;
}

.center-content p {
	color: #6c757d; /* Color de texto gris para el pÃ¡rrafo */
	font-size: 1.1em;
	line-height: 1.6;
}

.highlight {
	color: #007bff; /* Color azul para destacar */
	font-weight: 600;
}
</style>
</head>
<body>

	<div class="center-content">

		<!-- Formulario de TrÃ¡mites -->
		<div class="mb-4">
			<form id="frmUpd"
				action="../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_INIT_CASE%>"
				method="post"
				class="row g-3 justify-content-center align-items-center">

				<%
				if (msg != null) {
				%>
				<div class="col-12">
					<div class="alert alert-danger py-2" role="alert">
						<%=msg%>
					</div>
				</div>
				<%
				}
				%>

				<div class="col-auto">
					<select name="id_tc" class="form-select">
						<option value="-1" selected="selected">&lt;Seleccione un
							tipo de Tr&aacute;mite&gt;</option>
						<%
						Map m = ct.getAllTipoCaso(u.getLogin());
						for (Iterator iter = m.keySet().iterator(); iter.hasNext();) {
							String name = (String) iter.next();
							TipoCaso tc = (TipoCaso) m.get(name);
						%>
						<option value="<%=tc.getIdTC()%>"><%=name%></option>
						<%
						}
						%>
					</select>
				</div>
				<div class="col-auto">
					<button type="submit" class="btn btn-primary" id="pb_crear"
						name="pb_crear">Iniciar Tr&aacute;mite</button>
				</div>
			</form>
		</div>

		<img src="../imagenes/logo_central.png"
			alt="Sistema de Administración Integral Logo">

		<h1>¡Bienvenido a su Plataforma Integral!</h1>
		<p>
			Para acceder a sus notificaciones y documentos pendientes, por favor
			diríjase a la sección <span class="highlight"> Documentos >
				Inbox</span>.
		</p>
		<p>Si busca otras funcionalidades, explore las opciones del menú
			para encontrar lo que necesita. ¡Estamos aquí para facilitar su
			gestión!</p>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
