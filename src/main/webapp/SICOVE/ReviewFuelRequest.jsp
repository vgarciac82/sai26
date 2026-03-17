<%@page import="com.axtel.sai.sicove.SICOVE"%>
<%@page import="com.syc.gestion.UsuarioBusinessLogic"%>
<%@page import="com.axtel.user.entities.ExecutiveUnit"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.slf4j.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%!
	private static final Logger log = LoggerFactory.getLogger( "reviewFuelRequest.jsp" );
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
	String requestAccount = "";
	String requestFolio = "";
	try{
		
		UsuarioBusinessLogic ubl = new UsuarioBusinessLogic(GestionInterface.ATT_CONEXION);
		executiveUnit = ubl.getExecutiveUnit( u );
		requestAccount = request.getParameter( SICOVE.PARAM_REQUEST_ACCOUNT );
		requestFolio = request.getParameter( SICOVE.PARAM_REQUEST_FOLIO );
		log.debug("Reviewing account: " + requestAccount + " Request Folio: " + requestFolio);
		
	}catch(Exception e){
		log.error(e.getMessage(), e);
		initError = true;
	}
	
%>
<!doctype html>
<html lang="es">
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Revisión de Solicitud.</title>
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet" >
	<link href="../css/sai.css" rel="stylesheet" >
	<link href="https://cdn.datatables.net/1.13.2/css/jquery.dataTables.css" rel="stylesheet" type="text/css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css" >
</head>

<body>
	
	<div class="container-lg mt-3 h-100">
	 
			 
		<form>
			<input type="hidden" id="requestAccount" value="<%=requestAccount%>">
			<input type="hidden" id="requestFolio" name="requestFolio" value="<%=requestFolio%>">

			<h1 class="pt-2 pb-2">Asignación de Combustible en cuenta de usuario.  </h1>
		
			<div class="row mt-2">
				<div class="col-sm-12">
					<div class="card w-75 mx-auto" id="accountCard">
						<div class="card-header">Informaci&oacute;n de la Cuenta</div>
						<div class="card-body">
							<jsp:include page="fuelRequest/account.jsp">
									<jsp:param name="action" value="2"/>
									<jsp:param name="requestAccount" value="<%=requestAccount%>"/>
									<jsp:param name="requestFolio" value="<%=requestFolio%>"/>
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
								<jsp:param name="action" value="2"/>
								<jsp:param name="requestFolio" value="<%=requestFolio%>"/>
							</jsp:include>
						</div>
					</div>
				</div>
			</div>

			<div class="row mt-2" id="expedientDiv" style:"display:none">
				<div class="col-sm-12">
					<jsp:include page="fuelRequest/documents.jsp">
						<jsp:param value="Documentos" name="title"/>
						<jsp:param value="3" name="action"/>
					</jsp:include>
				</div>
			</div>
		
			<div class="row mt-2">
				<div class="col-sm-12">
					<div class="card w-75 mx-auto" id="requestCard">
						<div class="card-header">Operaciones</div>
						
						<div class="card-body">
							<jsp:include page="fuelRequest/FuelingOptions.jsp">
								<jsp:param name="action" value="2"/>
								<jsp:param name="requestFolio" value="<%=requestFolio%>"/>
							</jsp:include>
						</div>
					</div>
				</div>
			</div>
		</form> 
 	</div>

	<div class="modal" tabindex="-1" role="dialog" id="rejectionModal">
		<div class="modal-dialog">
			<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title">Motivo de Rechazo</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			</div>
			<div class="modal-body">
				<div class="form-group">
				<label for="rejectionReason">Motivo de Rechazo:</label>
				<textarea class="form-control" id="rejectionReason" rows="3"></textarea>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" id="rejectBtn">Aceptar</button>
				<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
			</div>
			</div>
		</div>
	</div>
	
	<script src="https://code.jquery.com/jquery-3.6.3.min.js"></script>
	<script src="https://cdn.datatables.net/1.13.2/js/jquery.dataTables.js" type="text/javascript" charset="utf8"></script>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js" ></script>
	<script src="https://kit.fontawesome.com/aad2c3aad1.js" ></script>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"></script>
	<script src="../Generador/js/jquery.blockUI-2.70.0.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script src="js/SICOVECommons.js"></script>
	<script src="js/ContractCommons.js"></script>
	<script type="text/javascript" src="js/AccountFuelRequest.js"></script>
	<script type="text/javascript" src="js/ReviewFuelRequest.js"></script>
	<script type="text/javascript" src="js/FuelRequest.js"></script>
	<script type="text/javascript" src="js/FuelRequestStatus.js"></script>
	<script src="js/Expedient.js"></script>
	
	<script type="text/javascript">
		const initError = <%=initError%>;
		
		let idExecutiveUnit = <%=(executiveUnit != null ? String.valueOf(executiveUnit.getIdExecutiveUnit()) : "0" )%>;
		
		$(document).ready(function () {
			getAccountFuelRequest($("#requestFolio").val(),showAuthInfo, onErrorProcess);
			
		});
		
	</script>
</body>
</html>