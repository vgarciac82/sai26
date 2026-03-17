<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="com.syc.gestion.UsuarioBusinessLogic"%>
<%@page import="com.axtel.user.entities.ExecutiveUnit"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%!private static final Logger log = LoggerFactory.getLogger("ReemplazaFactura.jsp");%>

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
	<title>Autoriza Comprobaci&oacute;n de Combustible en Tarjeta</title>
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
	<link href="../css/sai.css" rel="stylesheet">
	<link href="https://cdn.datatables.net/1.13.2/css/jquery.dataTables.css" rel="stylesheet" type="text/css">
	<link href="../SICOVE/fontawesome/css/fontawesome.css" rel="stylesheet">
  	<link href="../SICOVE/fontawesome/css/brands.css" rel="stylesheet">
  	<link href="../SICOVE/fontawesome/css/solid.css" rel="stylesheet">
</head>
<body>
	<form action="../reemplazoFacturas" method="post" enctype="multipart/form-data">
		<input type="hidden" id="userRequest" value="${userRequest}">
		<input type="hidden" id="employeeNumber" value="${employeeNumber}">
		
		<div class="container-lg pt-3 mt-3">
			<h1>Reemplazo de Facturas en Pagos.</h1>
			 
			 <div class="row mb-3">
			 	<div class="col-sm-12 col-md-6 "> 
				 	<label for="tipoPago" class="form-label">Tipo de Pago*</label>
				 	<select id="tipoPago" name="tipoPago" class="form-select" required>
				 		<option value="">Seleccione el tipo de pago.</option>
				 		<option value="PAGODIVERSO">Pago Diverso</option>
				 		<option value="PAGODIRECTO">Pago Directo</option>
				 		<option value="PAGOFEDERALIZADO">Pago Federalizado</option>
				 		<option value="PAGOOBRA">Pago de Obra</option>
				 	</select>
				  	
			  	</div>
			  	
			  	<div class="col-sm-12 col-md-6"> 
				 	<label for="cxp" class="form-label">Cuenta por Pagar </label>
				  	<input type="text" class="form-control" id="cxp" name="cxp" placeholder="10CP000000000" required>
			  	</div>
			 </div>
			 
			
			 <div class="row mb-3"> 	
			  	<div class="col-sm-12 col-md-6">
					<label for="facturas" class="form-label">Facturas*</label>
					<input class="form-control" type="file" id="facturas"  name="facturas" required>
				</div>
			</div>
			
			<div class="row mb-3">
			    <div class="col-sm-12  text-center">
			         <button type="submit" class="btn btn-primary" onclick="bloquearInterfaz()">Cargar</button>
			    </div>
			</div>

			<br>
			 <small class="text-muted">
			* No se pueden reemplazar facturas en Relacion de Gastos.
			</small>
		</div>
	</form>

	<script src="https://code.jquery.com/jquery-3.6.3.min.js"></script>
	<script src="https://cdn.datatables.net/1.13.2/js/jquery.dataTables.js" type="text/javascript" charset="utf8"></script>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
	<script src="https://kit.fontawesome.com/aad2c3aad1.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script src="../Generador/js/jquery.blockUI-2.70.0.js"></script>
	
	<script type="text/javascript">
		const initError = <c:out value="${initError}" />;
		
		let idExecutiveUnit = <%=(executiveUnit != null ? String.valueOf(executiveUnit.getIdExecutiveUnit()) : "0")%>;

		$(document).ready(function() {
			
		 	
		});
		
		 const bloquearInterfaz = function() {
			 $.blockUI({ message: "Cargando Información" });
	        }
	</script>
</body>
</html>