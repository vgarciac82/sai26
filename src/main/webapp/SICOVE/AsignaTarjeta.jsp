<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.axtel.user.entities.ExecutiveUnit"%>
<%@page import="com.syc.gestion.UsuarioBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="org.apache.log4j.LogManager"%>
<%@page import="org.apache.log4j.Logger"%>
<%!
	private static final Logger log = LogManager.getLogger( "Asignatarjeta.jsp" );
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
	
	try{
		UsuarioBusinessLogic ubl = new UsuarioBusinessLogic(GestionInterface.ATT_CONEXION);
		executiveUnit = ubl.getExecutiveUnit( u );
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
<title>Registro de Monederos Electrónicos</title>
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet" >
	<link href="../css/sai.css" rel="stylesheet" >
	<link href="https://cdn.datatables.net/1.13.2/css/jquery.dataTables.css" rel="stylesheet" type="text/css">
</head>
<body>
	<form>
		<input type="hidden" name ="employeeRegistration" id="employeeRegistration" value="<%=employeeRegistration%>">
		<input type="hidden" name ="walletStatus" id="walletStatus" value="0">
		
		<div class="container-lg">
			<h1 class="pt-2 pb-2">Captura de Monederos Electrónicos</h1>
			
			<div class="row mt-2">
				<div class="col-sm-12">
					<div class="card w-75 mx-auto" id="accountCard">
						<div class="card-header">Selecci&oacute;n de Cuenta.</div>
						<div class="card-body">
							<div class="row">
								<label for="idAccount" class="offset-lg-2 col-sm-2 col-form-label text-end">Cuenta:</label>
								<div class="col-sm-6">
									<div class="input-group">
										<select class="form-select" id="idAccount" name="idAccount"
											aria-label="Cuentas Registradas">
											<option selected value="">Seleccione una cuenta</option>
										</select>
										<button class="btn btn-outline-secondary" type="button" id="loadAccountInfoBtn">
											Cargar
										</button>
									</div>
								</div>
							</div>

							<div class="row">
								<label for="contractNumber"
									class="offset-lg-2 col-sm-2 col-form-label text-end">Contrato:</label>
								<div class="col-sm-6">
									<input type="text" readonly class="form-control-plaintext"
										id="contractNumber" value="">
								</div>
								<div class="col-sm-2">&nbsp;</div>
								
								<label for="provider"
									class="offset-lg-2 col-sm-2 col-form-label text-end">Proveedor:</label>
								<div class="col-sm-6">
									<input type="text" readonly class="form-control-plaintext"
										id="provider" value="">
								</div>
								<div class="col-sm-2">&nbsp;</div>
								
								<label for="employeeResponsibleName"
									class="offset-lg-2 col-sm-2 col-form-label text-end">Responsable:</label>
								<div class="col-sm-6">
									<input type="text" readonly class="form-control-plaintext"
										id="employeeResponsibleName" value="">
								</div>
								
								<div class="col-sm-2">&nbsp;</div>
								<label for="position"
									class="offset-lg-2 col-sm-2 col-form-label text-end">Cargo:</label>
								<div class="col-sm-6">
									<input type="text" readonly class="form-control-plaintext"
										id="position"
										value="">
								</div>
								<div class="col-sm-2">&nbsp;</div>
								<label for="organizationUnitName"
									class="offset-lg-2 col-sm-2 col-form-label text-end">Unidad:</label>
								<div class="col-sm-6">
									<input type="text" readonly class="form-control-plaintext"
										id="organizationUnitName" value="">
								</div>
								<div class="col-sm-2">&nbsp;</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="row mt-2">
				<div class="col-sm-12">
					<jsp:include page="fuelRequest/VehicleSelector.jsp">
						<jsp:param value="Registro de Monedero y Vehiculo." name="title"/>
						<jsp:param value="1" name="action"/>
					</jsp:include>
				</div>
			</div>
			
			<div class="row mt-2">
				<div class="col-sm-12">
					<div class="card w-75 mx-auto" id="accountActions">
						<div class="card-header">Operaciones.</div>
						<div class="card-body">
							<div class="row" id="addOperations">
								<div class="col-4 offset-lg-4">
									<button class="btn btn-primary" type="button" id="addWalletAccountBtn">Agregar</button>
									<button class="btn btn-primary" type="button" id="cleanVehicleInfo">Limpiar</button>
								</div>
							</div>
							
							<div class="row" id="updateOperations" style="display:none">
								<div class="col-8 offset-2">
									<button class="btn btn-warning" type="button" id="updateWalletAccountBtn">Actualizar</button>
									<button class="btn btn-primary" type="button" id="enableWalletAccountBtn" style="display:none">Activar</button>
									<button class="btn btn-secondary" type="button" id="disableWalletAccountBtn" style="display:none">Desactivar</button>
									<button class="btn btn-primary" type="button" id="cancelUpdateWalletAccountBtn">Cancelar</button>
									<button class="btn btn-secondary" type="button" id="cleanUpdateVehicleInfoBtn">Limpiar</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			
			<div class="row mt-2">
				<div class="col-sm-12">
					<jsp:include page="fuelRequest/WalletTable.jsp">
						<jsp:param value="Tarjetas Registradas" name="title"/>
						<jsp:param value="1" name="action"/>
					</jsp:include>
				</div>
			</div>
		
		</div>
	</form>	

	<script src="https://code.jquery.com/jquery-3.6.3.min.js"></script>
	<script src="https://cdn.datatables.net/1.13.2/js/jquery.dataTables.js" type="text/javascript" charset="utf8"></script>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js" ></script>
	<script src="https://kit.fontawesome.com/aad2c3aad1.js" ></script>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<script src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script src="js/SICOVECommons.js"></script>
	<script src="js/ContractCommons.js"></script>
	<script src="js/FuelContract.js"></script>
	<script src="js/WalletDTCommons.js"></script>
	<script src="js/AsignWallet.js"></script>
	<script src="js/Vehicle.js"></script>
	<script src="js/Employee.js"></script>
	<script src="js/FuelContractWallet.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	
	<script type="text/javascript">
		const initError = <%=initError%>;
		
		let idExecutiveUnit = <%=(executiveUnit != null ? String.valueOf(executiveUnit.getIdExecutiveUnit()) : "0" )%>;
		
		$(document).ready(function () {
			initUI(initError,idExecutiveUnit);
		});
		
	</script>
</body>
</html>