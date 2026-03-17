<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%
	String cCentroContable = "";
	String ue = "";	
	String login = "";
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	
	ue = usuario.getU_UR();
	login = usuario.getLogin();
%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>Solicitud de Firma Electrónica de Compromisos</title>

<!-- Bootstrap 5 -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet" />
<!-- DataTables Bootstrap 5 -->
<link
	href="https://cdn.datatables.net/v/bs5/dt-2.1.6/datatables.min.css"
	rel="stylesheet" />
<!-- Font Awesome (para icono de lupa) -->
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"
	rel="stylesheet" />
<!-- SweetAlert2 -->
<link
	href="https://cdn.jsdelivr.net/npm/sweetalert2@11.12.4/dist/sweetalert2.min.css"
	rel="stylesheet" />

<style>
.card {
	border: 0;
	border-radius: 1rem;
	box-shadow: 0 10px 25px rgba(0, 0, 0, 0.06);
}

.section-title {
	font-weight: 700;
}

table.dataTable tbody tr.selected-row {
	background-color: rgba(13, 110, 253, .08);
}

.dt-container .row {
	align-items: center;
}

h1 {
	font-size: 1.3em;
	font-weight: normal;
	line-height: 1.6em;
	color: #4E6CA3;
	border-bottom: 1px solid #B0BED9;
	clear: both;
	margin-top: 0px;
}
</style>
</head>
<body>
	<form>
		<input type="hidden" name="cTipoFirmante" id="cTipoFirmante"
			value="AUT" />
		<input type="hidden" id="cDocumento" name="cDocumento"
			value="COMPROMISO" />
		<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable"  value="<%=ue%>"/>
	
		<div class="container py-4">
			<!-- Título -->
			<div class="mb-4 text-left">
				<h1 class="section-title">Solicitud de Firma Electrónica de
					Compromisos</h1>
			</div>

			<!-- Tabla -->
			<div class="card mb-4">
				<div class="card-header fw-semibold">Tramites Pendientes</div>
				<div class="card-body">
					<div class="table-responsive">
						<table id="tabla-contratos"
							class="table table-striped table-hover w-100 align-middle">
							<thead>
								<tr>
									<th style="width: 36px;"><input type="checkbox"
										id="selectAll" class="form-check-input"
										title="Seleccionar/Deseleccionar todo" /></th>
									<th>Contrato</th>
									<th>Tipo</th>
									<th>Contrarrecibo</th>
									<th>Unidad</th>
									<th>RFC</th>
									<th>Razón Social</th>
									<th>Descripción</th>
									<th class="text-end">Importe</th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
					</div>
				</div>
			</div>

			<!-- Seleccion de Firmante -->
			<div class="card mb-4">
				<div class="card-header fw-semibold">Seleccione Firmante</div>
				<div class="card-body">
					<div class="row g-3 align-items-center">
						<div class="col-12">
							<label for="empleado" class="form-label"># Empleado</label>
							<div class="input-group">
								<select id="empleado" name="empleado" class="form-select" onchange="getEmployeeRole();">
									<option value="">SELECCIONE FIRMANTE</option>
								</select>
							</div>
						</div>
	
						<div class="col-12">
							<label for="cargo" class="form-label">Cargo</label> <input
								type="text" id="cargo" class="form-control" readonly />
						</div>
						
						<div class="col-12">
						<label for="justificacion_adicional" class="form-label">Justificacion Adicional</label> 
						<textarea id="justificacion_adicional" name="justificacion_adicional" class="form-control" rows="4"></textarea>
						</div>
					</div>
				</div>
			</div>

			<!-- Operaciones -->
			<div class="card mb-5">
				<div class="card-header fw-semibold">Operaciones</div>
				<div class="card-body d-flex justify-content-center gap-2">
					<button id="btnSolicitarFirma" class="btn btn-primary">
						Solicitar Firma</button>
					<button id="btnLimpiar" class="btn btn-outline-secondary">
						Limpiar</button>
				</div>
			</div>
			
			<!-- Resultado de la solicitud -->
		   <div class="container pb-5">
		    <div id="card-resultado" class="card" style="display:none;">
		      <div class="card-header fw-semibold">Resultado de la Solicitud</div>
		      <div class="card-body">
		        <div class="table-responsive">
		          <table class="table table-sm align-middle" id="tabla-resultado">
		            <thead>
		              <tr>
		                <th style="width:120px;">Folio</th>
		                <th style="width:120px;">Estatus</th>
		                <th>Causa de Error</th>
		              </tr>
		            </thead>
		            <tbody></tbody>
		          </table>
		        </div>
		      </div>
		    </div>
		  </div>
		</div>
	</form>
	<!-- jQuery -->
	<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
	<!-- Bootstrap 5 -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<!-- DataTables -->
	<script
		src="https://cdn.datatables.net/v/bs5/dt-2.1.6/datatables.min.js"></script>
	<!-- SweetAlert2 -->
	<script
		src="https://cdn.jsdelivr.net/npm/sweetalert2@11.12.4/dist/sweetalert2.all.min.js"></script>
	<script src="js/SolicitudFirmaElectronicaCompromiso.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script>
		
	</script>
</body>
</html>
