<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="com.axtel.sai.sicove.SICOVE"%>
<%@page import="com.syc.gestion.UsuarioBusinessLogic"%>
<%@page import="com.axtel.user.entities.ExecutiveUnit"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.slf4j.Logger"%>

<%!private static final Logger log = LoggerFactory.getLogger("PendingFuelRequestVerification.jsp");%>

<%
boolean initError = false;

Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
if (u == null) {
	response.sendRedirect("../index.jsp");
	return;
}

ExecutiveUnit executiveUnit = null;
String employeeRegistration = u.getLogin();
String userRequest = "";
String employeeNumber = "";

try {
	UsuarioBusinessLogic ubl = new UsuarioBusinessLogic(GestionInterface.ATT_CONEXION);
	executiveUnit = ubl.getExecutiveUnit(u);
	userRequest = u.getLogin();
	employeeNumber = u.getNumeroEmpleado();
} catch (Exception e) {
	log.error(e, e);
	initError = true;
}
%>

<c:set var="initError" value="<%=initError%>" />
<c:set var="executiveUnit" value="<%=executiveUnit%>" />
<c:set var="employeeRegistration" value="<%=employeeRegistration%>" />
<c:set var="userRequest" value="<%=userRequest%>" />
<c:set var="employeeNumber" value="<%=employeeNumber%>" />

<c:choose>
  <c:when test="${not empty executiveUnit}">
    <c:set var="idExecutiveUnit" value="${executiveUnit.idExecutiveUnit}" />
  </c:when>
  <c:otherwise>
    <c:set var="idExecutiveUnit" value="0" />
  </c:otherwise>
</c:choose>

<!doctype html>
<html lang="es">
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Comprueba Asignaci&oacute;n de Combustible en Tarjeta</title>
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
	<link href="../css/sai.css" rel="stylesheet">
	<link href="https://cdn.datatables.net/1.13.2/css/jquery.dataTables.css" rel="stylesheet" type="text/css">
</head>
<body>
	<form>
		<input type="hidden" id="userRequest" value="${userRequest}">
		<input type="hidden" id="employeeNumber" value="${employeeNumber}">
		
		<div class="container-lg pt-3 mt-3">

			<ul class="nav nav-tabs" id="pendingVerificationTabs" role="tablist">
				<li class="nav-item" role="presentation">
					<button class="nav-link active" id="pendingVerification-tab" data-bs-toggle="tab" data-bs-target="#pendingVerification-tab-pane" type="button" role="tab" aria-controls="pendingVerification-tab-pane" aria-selected="true">Por Comprobar</button>
				</li>
				<li class="nav-item" role="presentation">
					<button class="nav-link" id="requestVerificated-tab" data-bs-toggle="tab" data-bs-target="#requestVerificated-tab-pane" type="button" role="tab" aria-controls="requestVerificated-tab-pane" aria-selected="false">Comprobadas</button>
				</li>
			</ul>
			
			<div class="tab-content" id="pendingVerificationTabsContent">
				
				<div class="tab-pane fade show active" id="pendingVerification-tab-pane" role="tabpanel" aria-labelledby="pendingVerification-tab" tabindex="0">
					<jsp:include page="fuelRequest/PendingFuelingList.jsp">
						<jsp:param value="Seleccione una solicitud" name="header" />
						<jsp:param value="pendingVerificationDataTable" name="tableId" />
						<jsp:param value="Comprueba Asignaci&oacute;n de Combustible" name="title" />
					</jsp:include>
				</div>
				
				<div class="tab-pane fade" id="requestVerificated-tab-pane" role="tabpanel" aria-labelledby="requestVerificated-tab" tabindex="1">
					<jsp:include page="fuelRequest/PendingFuelingList.jsp">
						<jsp:param value="Seleccione una solicitud" name="header" />
						<jsp:param value="verificatedRequestDataTable" name="tableId" />
						<jsp:param value="Asignaciones Comprobadas" name="title" />
					</jsp:include>
				</div>
				
			</div>
			
		</div>
	</form>

	<script src="https://code.jquery.com/jquery-3.6.3.min.js"></script>
	<script src="https://cdn.datatables.net/1.13.2/js/jquery.dataTables.js" type="text/javascript" charset="utf8"></script>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
	<script src="https://kit.fontawesome.com/aad2c3aad1.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="js/FuelingWalletList.js"></script>
	<script type="text/javascript" src="js/PendingFuelRequestVerification.js"></script>
	<script type="text/javascript" src="js/Fuelling.js"></script>

	<script type="text/javascript">
		const initError = <c:out value="${initError}" />;
		
		let idExecutiveUnit = <%=(executiveUnit != null ? String.valueOf(executiveUnit.getIdExecutiveUnit()) : "0")%>;

		$(document).ready(function() {
			const dts = [];
			dts.push({"id":"pendingVerificationDataTable","status":AUTHORIZED_FUELING_REQUEST,"fn": processVerification});
			dts.push({"id":"verificatedRequestDataTable","status":VERIFIED_FUELING_REQUEST,"fn": processVerification});
			initUIFuelingWalletAuthList(initError,idExecutiveUnit,dts);
			$('.nav-tabs button').on('shown.bs.tab', function(e) {
				if (e.target.id === 'requestVerificated-tab') {
					$('#verificatedRequestDataTable').DataTable().columns.adjust();
				}
			});
		});
	</script>
</body>
</html>