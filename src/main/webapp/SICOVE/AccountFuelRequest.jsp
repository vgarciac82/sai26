<%@page import="com.axtel.user.entities.ExecutiveUnit"%>
<%@page import="com.syc.gestion.UsuarioBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="org.apache.log4j.LogManager"%>
<%@page import="org.apache.log4j.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%!private static final Logger log = LogManager.getLogger("AccountFuelRequest.jsp");%>
<%
boolean initError = false;

Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
if (u == null) {
	response.sendRedirect("../index.jsp");
	return;
}

ExecutiveUnit executiveUnit = null;
String employeeRegistration = u.getLogin();

try {
	UsuarioBusinessLogic ubl = new UsuarioBusinessLogic(GestionInterface.ATT_CONEXION);
	executiveUnit = ubl.getExecutiveUnit(u);
} catch (Exception e) {
	log.error(e, e);
	initError = true;
}
%>

<c:set var="REQUEST_ACCOUNT" value="${param.REQUEST_ACCOUNT}" />
<c:set var="REQUEST_FOLIO" value="${param.REQUEST_FOLIO}" />
	
<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Solicitud de Combustible</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="../css/sai.css" rel="stylesheet">
<link href="https://cdn.datatables.net/1.13.2/css/jquery.dataTables.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css" >

</head>
<body>
	<input type="hidden" id="userRequest" value="<%=employeeRegistration%>">
	<input type="hidden" id="requestAccount" value="${REQUEST_ACCOUNT}">
	<input type="hidden" id="requestFolio" value="${REQUEST_FOLIO}">
	
	<div class="container mt-3 h-100">
		<form>
			<h1>Solicitud de Combustible a mi cuenta.</h1>
			<div class="row mt-2">
				<div class="col-sm-12">
					<div class="card w-75 mx-auto" id="accountCard">
						<div class="card-header">
							Selecci&oacute;n de Cuenta.
						</div>
						
						<div class="card-body">
							<jsp:include page="fuelRequest/account.jsp">
								 <jsp:param name="action" value="1"/>
							</jsp:include>
						</div>
						
					</div>
				</div>
			</div>

			<div class="row mt-2">
				<div class="col-sm-12">
					<div class="card w-75 mx-auto" id="requestCard">
						<div class="card-header">Datos de Solicitud.</div>
						
						<div class="card-body">
							<jsp:include page="fuelRequest/FuelingDetail.jsp">
								<jsp:param name="action" value="1"/>
								<jsp:param name="requestFolio" value="0"/>
							</jsp:include>
						</div>
					</div>
				</div>
			</div>
		</form>
		<div class="row mt-2" id="expedientDiv" style:"display:none">
			<div class="col-sm-12">
				<jsp:include page="fuelRequest/documents.jsp">
					<jsp:param value="Documentos" name="title"/>
					<jsp:param value="1" name="action"/>
				</jsp:include>
			</div>
		</div>
		<div class="row mt-2">
			<div class="col-sm-12">
				<div class="card w-75 mx-auto" id="actions">
					<div class="card-header">Operaciones</div>
					<div class="card-body">
						<div class="row mt-1">
							<div class="col-4 offset-lg-4">
								<button class="btn btn-primary" type="button" id="saveFuelRequest"  style="display:none">Guardar</button>
								<button class="btn btn-primary" type="button" id="sendFuelRequest" style="display:none">Enviar</button>
								<button class="btn btn-secondary" type="button" id="cleanFuelRequest" style="display:none">Limpiar</button>
								<button class="btn btn-secondary" type="button" id="discardFuelRequest" style="display:none">Descartar</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	

	<script src="https://code.jquery.com/jquery-3.6.3.min.js" ></script>
	<script src="https://cdn.datatables.net/1.13.2/js/jquery.dataTables.js" ></script>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js" ></script>
	<script src="https://kit.fontawesome.com/aad2c3aad1.js" ></script>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11" ></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"></script>
	<script src="../Generador/js/jquery.blockUI-2.70.0.js"></script>
	<script src="js/FuelContract.js"></script>
	<script src="js/FuelRequestStatus.js"></script>
	<script src="js/WalletDTCommons.js"></script>
	<script src="js/AsignWallet.js"></script>
	<script src="js/SICOVECommons.js"></script>
	<script src="js/ContractCommons.js"></script>
	<script src="js/AccountFuelRequest.js"></script>
	<script src="js/FuelRequest.js"></script>
	<script src="js/Expedient.js"></script>
	
	
	<script src="../Generador/js/crud.js" ></script>
	<script type="text/javascript">
		const initError = <%=initError%> ;
		const idExecutiveUnit = <%=(executiveUnit != null ? String.valueOf(executiveUnit.getIdExecutiveUnit()) : "0")%>;
		$(document).ready(function () {
			initUIFuelRequest(initError,idExecutiveUnit,$("#requestAccount").val(),$("#requestFolio").val());
		});
	</script>
</body>
</html>