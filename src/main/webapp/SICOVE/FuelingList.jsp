<%@page import="com.axtel.sai.sicove.SICOVE"%>
<%@page import="com.syc.gestion.UsuarioBusinessLogic"%>
<%@page import="com.axtel.user.entities.ExecutiveUnit"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.slf4j.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%!
	private static final Logger log = LoggerFactory.getLogger( "Asignatarjeta.jsp" );
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
	String processType = "";
	try{
		
		UsuarioBusinessLogic ubl = new UsuarioBusinessLogic(GestionInterface.ATT_CONEXION);
		executiveUnit = ubl.getExecutiveUnit( u );
		processType = request.getParameter( SICOVE.PARAM_INBOX_TYPE );
		
	}catch(Exception e){
		log.error(e,e);
		initError = true;
	}
	
%>
<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Asignaci&oacute;n de Combustible en Cuenta</title>
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet" >
	<link href="../css/sai.css" rel="stylesheet" >
	<link href="https://cdn.datatables.net/1.13.2/css/jquery.dataTables.css" rel="stylesheet" type="text/css">
</head>
<body>
	<form>
	<input type="hidden" id="processType" value="<%=processType%>">
	<div class="container-lg">
		<h1 class="pt-2 pb-2">Asignaci&oacute;n de Combustible  </h1>
		<% if( "".equals(processType) || "1".equals(processType) ) { %>
		<div class="row mt-2">
			<div class="col-sm-12">
				<div class="card w-75 mx-auto" id="accountCard">
					<div class="card-header">Seleccione una solicitud</div>
					<div class="card-body">
						<div class="row">
							<table id="fuelingDataTable" class="table table-striped" style="width:100%">
								<thead>
									<tr>
										<th>Folio</th>
										<th>Unidad</th>
										<th>Contrato</th>
										<th>Cuenta</th>
										<th>Cantidad Solicitada</th>
										<th>Fecha</th>
										<th>Estatus</th>
									</tr>
								</thead>
							
							</table> 
						</div>
					</div>
				</div>
			</div>
		</div>
		<% } else{%>
		
		<ul class="nav nav-tabs" id="authPendingTabs" role="tablist">
  			<li class="nav-item" role="presentation">
    			<button class="nav-link active" id="authPending-tab" data-bs-toggle="tab" data-bs-target="#authPending-tab-pane" type="button" role="tab" aria-controls="authPending-tab-pane" aria-selected="true">Autorizar</button>
			</li>
			<li class="nav-item" role="presentation">
    			<button class="nav-link" id="processPending-tab" data-bs-toggle="tab" data-bs-target="#processPending-tab-pane" type="button" role="tab" aria-controls="processPending-tab-pane" aria-selected="false">Procesar</button>
			</li>
		</ul>
		<div class="tab-content" id="authPendingTabsContent">
		  	<div class="tab-pane fade show active" id="authPending-tab-pane" role="tabpanel" aria-labelledby="authPending-tab" tabindex="0">
		  		<div class="row mt-2">
					<div class="col-sm-12">
						<div class="card w-75 mx-auto" id="accountCard">
							<div class="card-header">Seleccione una solicitud para autorizar</div>
							<div class="card-body">
								<div class="row">
									<table id="fuelingDataTable" class="table table-striped" style="width:100%">
										<thead>
											<tr>
												<th>Folio</th>
												<th>Unidad</th>
												<th>Contrato</th>
												<th>Cuenta</th>
												<th>Cantidad Solicitada</th>
												<th>Fecha</th>
												<th>Estatus</th>
											</tr>
										</thead>
									
									</table> 
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		 	<div class="tab-pane fade" id="processPending-tab-pane" role="tabpanel" aria-labelledby="processPending-tab" tabindex="0">
		 		<div class="row mt-2">
					<div class="col-sm-12">
						<div class="card w-75 mx-auto" id="finishCard">
							<div class="card-header">Seleccione una solicitud para finalizar</div>
							<div class="card-body">
								<div class="row">
									<table id="fuelingAuthDataTable" class="table table-striped" style="width:100%">
										<thead>
											<tr>
												<th>Folio</th>
												<th>Unidad</th>
												<th>Contrato</th>
												<th>Cuenta</th>
												<th>Cantidad Solicitada</th>
												<th>Fecha</th>
												<th>Estatus</th>
											</tr>
										</thead>
									</table> 
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<% } %>

	
	</div>
	</form>	
	
	<script src="https://code.jquery.com/jquery-3.6.3.min.js"></script>
	<script src="https://cdn.datatables.net/1.13.2/js/jquery.dataTables.js" type="text/javascript" charset="utf8"></script>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js" ></script>
	<script src="https://kit.fontawesome.com/aad2c3aad1.js" ></script>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="js/FuelRequestStatus.js"></script>
	<script type="text/javascript" src="js/FuelingList.js"></script>

	
	<script type="text/javascript">
		const initError = <%=initError%>;
		
		let idExecutiveUnit = <%=(executiveUnit != null ? String.valueOf(executiveUnit.getIdExecutiveUnit()) : "0" )%>;
		
		$(document).ready(function () {
			initUIFuelingList(initError,idExecutiveUnit,$("#processType").val());
		});
		
	</script>
</body>
</html>