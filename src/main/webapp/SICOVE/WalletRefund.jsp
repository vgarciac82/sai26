<%@page import="com.syc.gestion.util.Util"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.axtel.user.entities.ExecutiveUnit"%>
<%@page import="com.syc.gestion.UsuarioBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="org.apache.log4j.LogManager"%>
<%@page import="org.apache.log4j.Logger"%>
<%!private static final Logger log = LogManager.getLogger("WalletRefund.jsp");%>
<%
boolean initError = false;

Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
if (u == null) {
	response.sendRedirect("../index.jsp");
	return;
}

ExecutiveUnit executiveUnit = null;
String employeeRegistration = u.getLogin();
int month = 1;

try {
	UsuarioBusinessLogic ubl = new UsuarioBusinessLogic(GestionInterface.ATT_CONEXION);
	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	month = Util.calculaMesAplicacion(efbl.getEjercicioFiscalActivo());

	executiveUnit = ubl.getExecutiveUnit(u);
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
<title>Devolucion de Saldo en Monederos Electrónicos</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link href="../css/sai.css" rel="stylesheet">
<link href="fontawesome/css/fontawesome.css" rel="stylesheet">
<link href="fontawesome/css/brands.css" rel="stylesheet">
<link href="fontawesome/css/solid.css" rel="stylesheet">
<link href="https://cdn.datatables.net/1.13.2/css/jquery.dataTables.css"
	rel="stylesheet" type="text/css">
</head>

<body>
	<form>
		<input type="hidden" name="employeeRegistration"
			id="employeeRegistration" value="<%=employeeRegistration%>">


		<div class="container-lg">
			<h1 class="pt-2 pb-2">Devolucion de Saldo en Monederos
				Electrónicos</h1>

			<div class="row mt-2">

				<div class="col-sm-12">

					<div class="card w-75 mx-auto" id="accountCard">
						<div class="card-header">Selecci&oacute;n de Cuenta.</div>
						<div class="card-body">
							<jsp:include page="fuelRequest/account.jsp">
								<jsp:param name="action" value="1" />
							</jsp:include>
						</div>
					</div>

				</div>

			</div>

			 
			<div class="row mt-2">
				<div class="col-sm-12">

					<jsp:include page="fuelRequest/WalletTable.jsp">
						<jsp:param value="Tarjetas con Saldo" name="title" />
						<jsp:param value="2" name="action" />
					</jsp:include>
				</div>
			</div>

			<div class="row mt-2">
				<div class="col-sm-12">

					<div class="card w-75 mx-auto" id="walletRefundsDetailDiv">
						<div class="card-header">Devoluciones Capturadas.</div>
						<div class="card-body">

							<table id="walletRefundsDetail" class="table table-striped"
								style="width: 100%">
								<thead>
									<tr>
										<th>ID</th>
										<th>Num. Tarjeta</th>
										<th>Placa Vehiculo</th>
										<th>Marca</th>
										<th>Modelo</th>
										<th>Monto Devuelto</th>

									</tr>
								</thead>
							</table>
							<button type="button" class="btn btn-secondary btn btn-warning"
								id="btnDelRefund" disabled>Eliminar Reembolso</button>
						</div>
					</div>
				</div>
			</div>
		</div>


	</form>


	<!-- Modal -->
	<div class="modal fade" id="refundModal" data-bs-backdrop="static"
		data-bs-keyboard="false" tabindex="-1"
		aria-labelledby="staticBackdropLabel" aria-hidden="true">
		<input type="hidden" id="idFuelAccountWalletsRefund"
			class="refundCapture" /> <input type="hidden" id="isSupplierRefund"
			class="refundCapture" />
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h2 class="modal-title fs-5" id="staticBackdropLabel">Capture
						Monto de Devolución</h2>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">

					<div class="row">
						<div class="col-2 text-sm-end">
							<label for="walletNumberRefund" class="form-label">Tarjeta:</label>
						</div>
						<div class="col-4 text-sm-start  ">
							<input type="text" readonly
								class="form-control-plaintext refundCapture "
								id="walletNumberRefund" name="walletNumberRefund">
						</div>

						<div class="col-2 text-sm-end">
							<label for="currentBalanceInfo" class="form-label">Saldo:</label>
						</div>
						<div class="col-4 text-sm-start  ">
							<input type="text" readonly
								class="form-control-plaintext refundCapture  "
								id="currentBalanceInfo" name="currentBalanceInfo">
						</div>
					</div>

					<div class="row">
						<div class="col-6 text-sm-end">
							<label for="refundAmount" class="form-label">Monto de
								Devolución</label>
						</div>
						<div class="col-6 text-sm-start">
							<input type="text" class="form-control refundCapture"
								id="refundAmount" name="refundAmount"
								placeholder="Monto de Devolución" required>
						</div>
					</div>

					<div class="row">
						<div class="col-6 text-sm-end">
							<label for="refundDate" class="form-label">Fecha de
								Devolución</label>
						</div>
						<div class="col-6 text-sm-end">
							<input type="date" class="form-control refundCapture"
								id="refundDate" name="refundDate" required>
						</div>
					</div>


				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" id="saveRefundBtn">
						<span class="spinner-border spinner-border-sm spinSave"
							role="status" aria-hidden="true" style="display: none"></span> <span
							class="sr-only spinSave" style="display: none">Loading...</span>
						Guardar
					</button>
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal" id="cancelRefundBtn">Cancelar</button>
				</div>
			</div>
		</div>
	</div>

	<script src="https://code.jquery.com/jquery-3.6.3.min.js"></script>
	<script src="https://cdn.datatables.net/1.13.2/js/jquery.dataTables.js"
		type="text/javascript" charset="utf8"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script src="js/SICOVECommons.js"></script>
	<script src="js/ContractCommons.js"></script>
	<script src="js/FuelContract.js"></script>
	<script src="js/WalletDTCommons.js"></script>
	<script src="js/AsignWallet.js"></script>
	<script src="js/FuelAccountWalletRefund.js"></script>
	<script src="js/WalletRefund.js"></script>
	<script src="../Generador/js/jquery.blockUI-2.70.0.js"></script>



	<script type="text/javascript">
		const initError = <%=initError%>;
		let idExecutiveUnit = <%=(executiveUnit != null ? String.valueOf(executiveUnit.getIdExecutiveUnit()) : "0")%>;

		$(document).ready(function() {
			$("#month").val(<%=month%>);
			initWalletRefund(initError, idExecutiveUnit);
		});
	</script>
</body>
</html>