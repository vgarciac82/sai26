<%@page import="java.util.Calendar"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalManager"%>
<%@page import="com.axtel.user.entities.ExecutiveUnit"%>
<%@page import="com.syc.gestion.UsuarioBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.slf4j.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%!
	private static final Logger log = LoggerFactory.getLogger("AccountAsignationReport.jsp");%>
<%
boolean initError = false;

Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
if (u == null) {
	response.sendRedirect("../index.jsp");
	return;
}

ExecutiveUnit executiveUnit = null;
String employeeRegistration = u.getLogin();
String fiscalYear = "";

try {
	
	UsuarioBusinessLogic ubl = new UsuarioBusinessLogic(GestionInterface.ATT_CONEXION);
	EjercicioFiscalBusinessLogic fybl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	
	executiveUnit = ubl.getExecutiveUnit(u);
	fiscalYear = fybl.getEjercicioFiscalActivo(  ).getaEjercicioFiscal(  );
	
} catch (Exception e) {
	log.error(e, e);
	initError = true;
}
%>
	
<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Reporte de Asginación de Combustible</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="../css/sai.css" rel="stylesheet">
<link href="https://cdn.datatables.net/1.13.2/css/jquery.dataTables.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css" >

</head>
<body>
	<div class="container mt-3 h-100">
		<form>
			<h1>Reporte de Asginación de Combustible</h1>
			
			<div class="row mt-2">

				<div class="col-sm-12">
					
					<div class="card w-100 mx-auto" id="accountCard">
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

			<div class="row mt-2" id="accountCardDiv" style="display:none;">

				<div class="col-sm-12">
					
					<div class="card w-100 mx-auto" id="accountCard" >
						<div class="card-header">
							Resumen Asignación de Combustible.
						</div>
						<div class="card-body align-items-center">
							<div class="container">
								<div class="row"> 
									<div class="col-sm-6">
										<canvas id="consumptionChart"></canvas>
									</div>
									<div class="col-sm-6">
										<div class="row">
											<div class="col-12">
												<div class="input-group mb-3">
													  <label class="input-group-text" for="asignationMonth">Mes</label>
													  <select class="form-select" id="asignationMonth">
														    <option value="1">Enero</option>
														    <option value="2">Febrero</option>
														    <option value="3">Marzo</option>
														    <option value="4">Abril</option>
														    <option value="5">Mayo</option>
														    <option value="6">Junio</option>
														    <option value="7">Julio</option>
														    <option value="8">Agosto</option>
														    <option value="9">Septiembre</option>
														    <option value="10">Octubre</option>
														    <option value="11">Noviembre</option>
														    <option value="12">Diciembre</option>
													  </select>
												</div>
											</div>
											
											<div class="col-12">
												<h6 class="h6 text-center">RESUMEN</h6>
											</div>
											
											<div class="col-12">
												<table class="table" id="asignedSummary">
													<thead>
														<tr>
															<th scope="col">Recibido</th>
															<th scope="col">Dispersado</th>
															<th scope="col">Devuelto</th>
															<th scope="col">Disponible</th>
														</tr>
													</thead>
													<tbody>
														 <tr>
														 	<td colspan="4">Sin información</td>
														 </tr>
													</tbody>
												</table>
											</div>
											
											<div class="col-12">
												<h6 class="h6 text-center">DETALLE</h6>
											</div>
											<div class="col-12">
												<table class="table" id="detailTable">
													<thead>
														<tr>
															<th scope="col">Fecha</th>
															<th scope="col">Solicitado</th>
															<th scope="col">Asignado</th>
														</tr>
													</thead>
													<tbody>
														<tr>
														 	<td colspan="3">Sin información</td>
														 </tr>
													</tbody>
												</table>
											</div>
											
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>

				</div>
			</div>

		</form>
		 
		<div class="row mt-2" id="actionsDiv" style="display:none;">
			<div class="col-sm-12">
				<div class="card w-100 mx-auto" id="actions">
					<div class="card-header">Operaciones</div>
					<div class="card-body">
						<div class="row mt-1">
							<div class="col-4 offset-lg-4">
								<button class="btn btn-primary" type="button" id="generateReport">Generar Cedula</button>
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
	<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

	<script src="../Generador/js/jquery.blockUI-2.70.0.js"></script>
	<script src="js/SICOVECommons.js"></script>
	<script src="js/ContractCommons.js"></script>
	<script src="js/FuelContract.js"></script>
	<script src="js/AccountAsignationReport.js"></script>
	
	
	
	<script src="../Generador/js/crud.js" ></script>
	<script type="text/javascript">
		
		let executiveUnit = <%=(executiveUnit != null ? String.valueOf(executiveUnit.getIdExecutiveUnit()) : "0" )%>;;
 		<%if( !String.valueOf( Calendar.getInstance().get(Calendar.YEAR ) ).equals( fiscalYear ) ){ %>
 			$("#asignationMonth").val("12");		
 		<%}%>
	</script>
</body>
</html>