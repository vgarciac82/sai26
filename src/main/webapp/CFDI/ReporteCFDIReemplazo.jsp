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
	String userRequest = "";
	String employeeNumber = "";
	
	try{
		
		UsuarioBusinessLogic ubl = new UsuarioBusinessLogic(GestionInterface.ATT_CONEXION);
		executiveUnit = ubl.getExecutiveUnit( u );
		
		userRequest = u.getLogin();
		employeeNumber = u.getNumeroEmpleado();
		
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
<title>Reporte de Facturas Reemplazadas</title>
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet" >
	<link href="../css/sai.css" rel="stylesheet" >
	<link href="https://cdn.datatables.net/1.13.2/css/jquery.dataTables.css" rel="stylesheet" type="text/css">
</head>
<body>

	<form>
	
	<input type="hidden" id="userRequest" value="<%=userRequest%>">
	<input type="hidden" id="employeeNumber" value="<%=employeeNumber%>">
	
	<div class="container-lg">
		<h1 class="pt-3 mt-3">Reporte de Facturas Reemplazadas.</h1>

		<div class="row mt-2">
			<div class="col-sm-12">
				<div class="card w-100 mx-auto" id="filtersCard">
					<div class="card-header">Ingrese los filtros:</div>
					<div class="card-body">
						<div class="row mb-3">
							<div class="col-sm-12 col-md-6">
								<label for="uuidReemplazado" class="form-label">UUID Reemplazado:</label>
  								<input type="text" class="form-control" id="uuidReemplazado"  name="uuidReemplazado"placeholder="UUID">
							</div>
							<div class="col-sm-12 col-md-6">
								<label for="rfcPago" class="form-label">RFC Proveedor:</label>
  								<input type="text" class="form-control" id="rfcPago" name="rfcPago" placeholder="XXXX000000HHH">
							</div>
						</div>
						
						<div class="row mb-3">
						    <div class="col-sm-12  text-center">
						         <button type="button" class="btn btn-primary" role="button" id="buscarBtn" >Buscar</button>
						    </div>
						</div>
						
					</div>
				</div>
			</div>
		</div>
		
		<div class="row mt-2">
			<div class="col-sm-12">
				<div class="card w-100 mx-auto" id="resultCard">
					<div class="card-header">Pagos con facturas reemplazadas </div>
					<div class="card-body">
						<div class="row">
							<table id="tblPagos" class="table table-striped" style="width: 100%">
								<thead>
									<tr>
										<th>CxP</th>
										<th>Tipo de Pago</th>
										<th>Folio</th>
										<th>RFC</th>
										<th>Nombre</th>
										<th>Concepto</th>
										<th>Importe</th>
										<th style="display: none">uuid</th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="row mt-2">
			<div class="col-sm-12">
				<div class="card w-100 mx-auto" id="resultCard">
					<div class="card-header">Facturas Originales </div>
					<div class="card-body">
						<div class="row">
							<table id="tblCfdiOriginal" class="table table-striped" style="width: 100%">
								<thead>
									<tr>
										<th>UUID</th>
										<th>Bruto</th>
										<th>IVA</th>
										<th>Otros Imp.</th>
										<th>Descto.</th>
										<th>Reten.</th>
										<th>Neto</th>
										<th>Metodo Pago</th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="row mt-2">
			<div class="col-sm-12">
				<div class="card w-100 mx-auto" id="resultCard">
					<div class="card-header">Facturas Nuevas </div>
					<div class="card-body">
						<div class="row">
							<table id="tblCfdiNuevo" class="table table-striped" style="width: 100%">
								<thead>
									<tr>
										<th>UUID</th>
										<th>Bruto</th>
										<th>IVA</th>
										<th>Otros Imp.</th>
										<th>Descto.</th>
										<th>Reten.</th>
										<th>Neto</th>
										<th>Metodo Pago</th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
		
	</div>
	</form>	
	
	<script src="https://code.jquery.com/jquery-3.6.3.min.js"></script>
	<script src="https://cdn.datatables.net/1.13.2/js/jquery.dataTables.js" type="text/javascript" charset="utf8"></script>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js" ></script>
	<script src="https://kit.fontawesome.com/aad2c3aad1.js" ></script>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="js/ReporteCFDIReemplazo.js"></script>
	
	<script type="text/javascript">
		 
		$(document).ready(function () {
			initUI();
		});
		
	</script>
</body>
</html>