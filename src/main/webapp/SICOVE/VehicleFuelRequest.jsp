<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="com.axtel.user.entities.ExecutiveUnit"%>
<%@page import="com.syc.gestion.UsuarioBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="org.apache.log4j.LogManager"%>
<%@page import="org.apache.log4j.Logger"%>
<% request.setCharacterEncoding("utf-8"); %>
<%!
	private static final Logger log = LogManager.getLogger( "VehicleFuelRequest.jsp" );
%>
<%
	boolean initError = false;
	
	Usuario u = (Usuario)session.getAttribute( GestionInterface.ATT_USER );
	if( u == null){
		response.sendRedirect("../index.jsp");
		return;
	}
	ExecutiveUnit executiveUnit = null;
	String employeeRegistration = u.getLogin();
	int action = 2;
	try{
		UsuarioBusinessLogic ubl = new UsuarioBusinessLogic(GestionInterface.ATT_CONEXION);
		
		executiveUnit = ubl.getExecutiveUnit( u );
		String actionStr = request.getParameter( "ACTION" );
		if( !StringUtils.isBlank( actionStr ) )
			action = Integer.parseInt( actionStr );
			
	}catch(Exception e){
		log.error(e,e);
		initError = true;
	}
%>

<c:set var="folio" value="${param.REQUEST_FOLIO}" />
<c:set var="action" value="<%=action%>" />

<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Solicitud de Combustible para Vehiculos</title>
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet" >
	<link href="../css/sai.css" rel="stylesheet" >
	<link href="https://cdn.datatables.net/1.13.2/css/jquery.dataTables.css" rel="stylesheet" type="text/css">
</head>

<body>
	<div class="container-lg">
		<form id="fuelRequestForm" class="needs-validation" novalidate>
	
			<input type="hidden" name ="rejectedTickets" id="rejectedTickets" value="" />
			<input type="hidden" name ="fueling_request_id" id="fueling_request_id" value="<c:out value="${folio}" />">
			<input type="hidden" name ="employeeRegistration" id="employeeRegistration" value="<%=employeeRegistration%>">
			<input type="hidden" name ="action" id="action" value="${action}">
			<input type="hidden" name ="noEmpleadoRFC" id="noEmpleadoRFC" value="9449">
			<h1 class="pt-2 pb-2">Solicitud de Combustible en Tarjeta</h1>
			
			<div class="row mt-2">
				<div class="col-sm-12">
					<jsp:include page="fuelRequest/VehicleSelector.jsp">
						<jsp:param value='<%=(action==3?"Vehículo":"Selección de Vehículo.")%>' name="title"/>
						<jsp:param value="${action}" name="action"/>
					</jsp:include>
				</div>
			</div>
		
			<div class="row mt-2">
				<div class="col-sm-12">
					<jsp:include page="fuelRequest/ResponsibleSelector.jsp">
						<jsp:param value="Responsable de la Solicitud" name="title"/>
						<jsp:param value="${action}" name="action"/>
						<jsp:param name="idUnit"   value="<%=executiveUnit.getIdExecutiveUnit()%>" />
						<jsp:param name="executiveUnit"  value="<%=executiveUnit.getExecutiveUnit()%>" />
					</jsp:include>
				</div>
			</div>
		
			<div class="row mt-2">
				<div class="col-sm-12">
					<jsp:include page="fuelRequest/CommisionSelector.jsp">
						<jsp:param value='<%=(action==3?"Justificacion":"Selección de Comision.")%>' name="title"/>
						<jsp:param value="${action}" name="action"/>
					</jsp:include>
				</div>
			</div>
		
			<div class="row mt-2">
				<div class="col-sm-12">
					<jsp:include page="fuelRequest/WalletFuelRequest.jsp">
						<jsp:param value="Solicitud" name="title"/>
						<jsp:param value="${action}" name="action"/>
					</jsp:include>
				</div>
			</div>
		</form>	
		
		<div class="row mt-2" id="expedientDiv" style="display: none">
			<div class="col-sm-12">
				<jsp:include page="fuelRequest/documents.jsp">
					<jsp:param value="Documentos" name="title"/>
					<jsp:param value="${action}" name="action"/>
				</jsp:include>
			</div>
		</div>
		
		<c:if test="${action == 3}">
		<div class="row mt-2" id="verificationDiv" style="display: none">
			<div class="col-sm-12">
				<jsp:include page="fuelRequest/VehicleFuelVerification.jsp">
					<jsp:param value="Comprobaciones" name="title"/>
					<jsp:param value="${action}" name="action"/>
				</jsp:include>
			</div>
		</div>
		</c:if>

		<div class="row mt-2">
			<div class="col-sm-12">
				<jsp:include page="fuelRequest/WalletFuelRequestOpts.jsp">
					<jsp:param value="Operaciones" name="title"/>
					<jsp:param value="${action}" name="action"/>
				</jsp:include>
			</div>
		</div>
		
		<jsp:include page="fuelRequest/AuthRequestDialog.jsp">
			<jsp:param value="Operaciones" name="title"/>
			<jsp:param value="${action}" name="action"/>
		</jsp:include>
				
	</div>
	
	<script src="https://code.jquery.com/jquery-3.6.3.min.js"></script>
	<script src="../Generador/js/jquery.blockUI-2.70.0.js"></script>
	<script src="https://cdn.datatables.net/1.13.2/js/jquery.dataTables.js" type="text/javascript" charset="utf8"></script>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js" ></script>
	<script src="https://kit.fontawesome.com/aad2c3aad1.js" ></script>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<script src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script src="js/VehicleFuelRequest.js"></script>
	<script src="js/Vehicle.js"></script>
	<script src="js/Employee.js"></script>
	<script src="js/FuelContractWallet.js"></script>
	<script src="js/Fuelling.js"></script>
	<script src="js/Expedient.js"></script>
	<script src="js/AuthRequestDialog.js"></script>
	<script src="js/WalletFuelRequestVerification.js"></script>
	<script src="js/PendingFuelRequestVerification.js"></script>
	
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	
	<script type="text/javascript">
		const initError = <%=initError%>;
		
		let idExecutiveUnit = <%=(executiveUnit != null ? String.valueOf(executiveUnit.getIdExecutiveUnit()) : "0" )%>;
		let fuelingRequestId = $("#fueling_request_id").val() === ""? 0: parseInt($("#fueling_request_id").val());
		
		$(document).ready(function () {
			initVehicleFuelRequest(initError,idExecutiveUnit,fuelingRequestId);
		});
		
		const onCommisionClick = function(){
			window.open('../Generador/listaAgendas.jsp?idEmpleado=' + $("#employeeNumber").val()+ '&formName=fuelRequestForm', 'Comisiones', 'status=1, width=900px, height=620px, left=0px, scrollbars=Yes,resizable=yes');
		}
		
	</script>
</body>
</html>