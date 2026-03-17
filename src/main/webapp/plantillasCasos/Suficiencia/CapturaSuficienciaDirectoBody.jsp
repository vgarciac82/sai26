<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<div class="container py-4">
		<input type="hidden" id="montoSalarioMinimo" name="montoSalarioMinimo" value="" />
		<input type="hidden" id="diaUsarMesSiguiente" name="diaUsarMesSiguiente" value="" />
		<h1 class="h4 mb-4">Captura Suficiencia para Pago Directo</h1>

		<!-- Barra superior: Referencia / Fecha / Importe -->
		<div class="card mb-4">
			<div class="card-body">
				<div class="row g-3">
					<div class="col-12 col-md-4">
						<label class="form-label">Folio</label> <input class="form-control" id="folio" type="text"
							minlength="10" maxlength="20" readonly placeholder="">
					</div>
					<div class="col-12 col-md-4">
						<label class="form-label">Referencia</label> <input class="form-control" id="idContrato"
							type="text" minlength="10" maxlength="20" readonly placeholder="">
					</div>
					<div class="col-12 col-md-4">
						<label class="form-label">Fecha</label> <input class="form-control" id="fechaHoy" type="text"
							readonly>
					</div>
				</div>
			</div>
		</div>

		<!-- General -->
		<div class="card mb-4">
			<div class="card-header">General</div>
			<div class="card-body">
				<div class="row g-3">


					<!-- RFC con input-group + botón addon -->
					<div class="col-12 col-md-4">
						<label class="form-label" for="rfc">RFC</label>
						<div class="input-group">
							<input class="form-control" id="rfc" maxlength="13" placeholder="XAXX010101000" readonly>
							<button class="btn btn-outline-secondary" type="button" id="btnBuscarRFC"
								title="Buscar RFC">
								<i class="fa-solid fa-magnifying-glass"></i>
							</button>
						</div>
					</div>

					<div class="col-12 col-md-8">
						<label class="form-label" for="nombre">Nombre</label> <input class="form-control" id="nombre"
							placeholder="Nombre del proveedor / beneficiario " readonly>
					</div>

					<div class="col-12">
						<label class="form-label" for="justificacion">Justificación</label>
						<textarea class="form-control" id="justificacion" rows="3"
							placeholder="Describe la justificación..."></textarea>
					</div>
				</div>


			</div>
		</div>

		<!-- Importe -->
		<div class="card mb-4">
			<div class="card-header">Importes</div>
			<div class="card-body">
				<div class="row g-3">
					<div class="col-12 col-md-3">
						<label class="form-label">Importe</label> <input class="form-control" id="importe"
							placeholder="0.00" required readonly="readonly">
					</div>

					<div class="col-12 col-md-3">
						<label class="form-label" for="importeIVA">Importe IVA</label> <input class="form-control"
							id="importeIVA" type="text" placeholder="0.00" readonly>
					</div>

					<div class="col-12 col-md-3">
						<label class="form-label" for="importeRetenciones">Importe
							Retenciones</label> <input class="form-control" id="importeRetenciones" type="text"
							placeholder="0.00" readonly>
					</div>

					<div class="col-12 col-md-3">
						<label class="form-label" for="total">Total</label> <input class="form-control" id="total"
							type="text" placeholder="0.00" readonly>
					</div>
				</div>

				<div class="row mt-3 d-none">

					<div class="col-12">
						<label class="form-label" for="porcentajeIVA">IVA</label>
					</div>

					<div class="col-12">
						<input type="text" class="form-control" id="porcentajeIVA">
					</div>


				</div>

			</div>

		</div>




		<!-- CARGA DE FACTURAS -->
		<div class="card mb-4 d-none" id="cardCargaFacturas">
			<div class="card-header">Carga de Facturas</div>
			<div class="card-body">
				<div class="mb-4 col-6">
					<label for="fileUpload" class="form-label">Cargar Archivo</label>
					<div class="input-group">
						<input class="form-control" type="file" id="fileUpload">
						<button class="btn btn-success" type="button" id="sendButton">
							<i class="bi bi-cloud-upload me-2"></i>Enviar
						</button>
					</div>
				</div>

				<div id="progressBarContainer" class="mb-4 d-none">
					<div class="progress" style="height: 25px;">
						<div class="progress-bar progress-bar-striped progress-bar-animated" role="progressbar"
							style="width: 0%;" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" id="progressBar">
							<span id="progressText">0%</span>
						</div>
					</div>
				</div>

				<div>
					<h6 class="border-bottom pb-2 mb-3">Facturas Cargadas</h6>
					<div class="table-responsive">
						<table class="table table-striped table-hover" id="invoicesTable">
							<thead class="bg-light">
								<tr>
									<th scope="col">UUID</th>
									<th scope="col">Importe Bruto</th>
									<th scope="col">Impuestos</th>
									<th scope="col">Total</th>
									<th scope="col">Retenciones</th>
									<th scope="col">Importe Neto</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td></td>
									<td></td>
									<td></td>
									<td></td>
									<td></td>
									<td></td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>

				<div id="errorTableContainer" class="mt-4 d-none">
					<h6 class="border-bottom pb-2 mb-3 text-danger">Errores de
						Carga</h6>
					<div class="table-responsive">
						<table class="table table-bordered table-sm">
							<thead class="bg-danger text-white">
								<tr>
									<th scope="col">Archivo</th>
									<th scope="col">Mensaje de Error</th>
								</tr>
							</thead>
							<tbody id="errorTableBody">
							</tbody>
						</table>
					</div>
				</div>

			</div>
		</div>

		<!-- Retenciones Pendientes-->
		<div class="card mb-4 d-none" id="cardRetencionesPendientes">
			<div class="card-header">Retenciones Pendientes</div>
			<div class="card-body">
				<hr class="my-4">
				<div class="d-flex flex-wrap gap-2 align-items-end mb-3">
					<table id="tblRetencionesPendientes" class="table table-striped table-hover w-100">
						<thead>
							<tr>
								<th>UUID</th>
								<th>Tipo</th>
								<th>Base</th>
								<th>Importe</th>
								<th>Tipo SAI</th>
								<th>Importe SAI</th>
								<th>Diferencia</th>
								<th>&nbsp;</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td colspan="8">Sin Retenciones</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>


		<!-- Retenciones Capturadas-->
		<div class="card mb-4 d-none" id="cardRetenciones">
			<div class="card-header">Retenciones Capturadas</div>
			<div class="card-body">
				<hr class="my-4">
				<div class="table-responsive">
					<table id="tblRetenciones" class="table table-striped table-hover w-100">
						<thead>
							<tr>
								<th>ID</th>
								<th>UUID</th>
								<th>Retención</th>
								<th>Porcentaje</th>
								<th>Base</th>
								<th>Importe</th>
								<th>&nbsp;</th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>
			</div>
		</div>

		<!-- Disponible Neto -->
		<div class="card mb-4 d-none" id="cardNeto">
			<div class="card-header">Disponible Neto</div>
			<div class="card-body">

				<table id="tblNeto" class="table table-striped table-hover w-100">
					<thead>
						<tr>
							<th>EP</th>
							<th>Disponible Neto</th>
						</tr>
					</thead>
					<tbody></tbody>
				</table>
				<div class="col-12 col-md-1 d-grid">
					<button class="btn btn-primary" id="btnAgregarDetalle">
						<i class="fa-solid fa-plus"></i>Agregar
					</button>
				</div>

			</div>
		</div>

		<!-- Calendario Capturado 
  <div class="card mb-4 d-none" id="cardCalendario">
    <div class="card-header">Calendario Capturado</div>
    <div class="card-body">
      <table id="tblCalendario" class="table table-striped table-hover w-100">
        <thead>
          <tr>
            <th>EP</th>
            <th>Ene</th>
            <th>Feb</th>
            <th>Mar</th>
            <th>Abr</th>
            <th>May</th>
            <th>Jun</th>
            <th>Jul</th>
            <th>Ago</th>
            <th>Sep</th>
            <th>Oct</th>
            <th>Nov</th>
            <th>Dic</th>
          </tr>
        </thead>
        <tbody>
          
        </tbody>
      </table>

      <div class="d-flex gap-2">
        <button class="btn btn-outline-secondary" id="btnEditarCal">
          <i class="fa-solid fa-pen-to-square me-1"></i>Editar
        </button>
        <button class="btn btn-outline-danger" id="btnEliminarCal">
          <i class="fa-solid fa-trash me-1"></i>Eliminar
        </button>
      </div>
    </div>
  </div>
  -->

		<div class="card mb-4 d-none" id="cardCalendario">
		    <div class="card-header">EPs Seleccionadas:</div>
		    <div class="card-body">
				
				<div class="d-flex align-items-center gap-3 mb-3">
					<label class="form-label mb-0">Monto Calendarizado</label>
					<input type="text" readonly class="form-control-plaintext" id="montoCalendarizado" name="montoCalendarizado" value="0.00" style="width: auto; min-width: 120px;">
					<label class="form-label mb-0">Monto Pendiente</label>
					<input type="text" readonly class="form-control-plaintext" id="montoPendiente" name="montoPendiente" value="0.00" style="width: auto; min-width: 120px;">
				</div>
				
				<div class="row g-3">
					<div class="col-12 col-md-12">
						<table id="tblCalendario" class="table table-striped table-hover w-100">
							<thead>
								<tr>
									<th>EP</th>
									<th>Ene</th>
									<th>Feb</th>
									<th>Mar</th>
									<th>Abr</th>
									<th>May</th>
									<th>Jun</th>
									<th>Jul</th>
									<th>Ago</th>
									<th>Sep</th>
									<th>Oct</th>
									<th>Nov</th>
									<th>Dic</th>
								</tr>
							</thead>
							<tbody>
				
							</tbody>
						</table>
					</div>
				</div>


				<div class="d-flex gap-2">

					<button class="btn btn-outline-danger" id="btnEliminarCal">
						<i class="fa-solid fa-trash me-1"></i>Eliminar
					</button>
				</div>
			</div>
		</div>

		<!-- Footer acciones -->
		<div class="sticky-footer d-flex justify-content-end gap-2">

			<button class="btn btn-outline-secondary" id="btnCancelar">
				<i class="fa-solid fa-xmark me-1"></i>Eliminar Solicitud
			</button>
			<button class="btn btn-success" id="btnGuardar">
				<i class="fa-solid fa-floppy-disk me-1"></i>Guardar
			</button>
			<button class="btn btn-success d-none" id="btnFinalizar" visible="false">
				<i class="fa-solid fa-check"></i> Terminar Solicitud
			</button>
		</div>
	</div>

	<!-- MODAL: Búsqueda de Proveedores -->
	<div class="modal fade" id="modalProveedores" tabindex="-1" aria-hidden="true">
		<div class="modal-dialog modal-xl modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">
						<i class="fa-solid fa-building me-2"></i> Búsqueda de Proveedores
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
				</div>

				<div class="modal-body">
					<div class="row g-3 mb-2">
						<div class="col-12 col-md-4">
							<label class="form-label">RFC</label> <input type="text" id="fRFC" class="form-control"
								placeholder="Ej. AXT000000AAA" maxlength="13">
						</div>
						<div class="col-12 col-md-8">
							<label class="form-label">Nombre</label> <input type="text" id="fNombre"
								class="form-control" placeholder="Razón social">
						</div>
					</div>

					<table id="tblProveedores" class="table table-striped table-hover w-100">
						<thead>
							<tr>
								<th>Nombre</th>
								<th>RFC</th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>

				<div class="modal-footer">

					<button type="button" class="btn btn-primary" id="btnProvSelect">
						<i class="fa-solid fa-check me-1"></i>Seleccionar
					</button>
					<button type="button" class="btn btn-outline-secondary" id="btnProvCancel" data-bs-dismiss="modal">
						<i class="fa-solid fa-xmark me-1"></i>Cancelar
					</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Diálogo Bootstrap - Calendario EP -->
	<div class="modal fade" id="modalCalendarioEP" tabindex="-1" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered modal-lg">
			<div class="modal-content">

				<!-- Encabezado del modal -->
				<div class="modal-header justify-content-center">
					<h5 class="modal-title fw-semibold text-center w-100 mb-0">
						Calendarizar EP
					</h5>
					<button type="button" class="btn-close position-absolute end-0 me-3" data-bs-dismiss="modal"
						aria-label="Cerrar"></button>
				</div>

				<!-- Cuerpo del modal -->
				<div class="modal-body">

					<!-- Encabezado -->
					<div class="mb-3">
						<table class="table table-borderless table-sm mb-0">
							<tbody>
								<tr>
									<th style="width: 80px;">EP</th>
								</tr>
								<tr>
									<td>
										<input type="text" readonly class="form-control-plaintext" id="epSelected"
											value="">
									</td>
								</tr>
							</tbody>
						</table>
					</div>

					<!-- Detalle -->
					<div class="table-responsive" style="max-height: 380px; overflow-y: auto;">
						<table class="table table-sm align-middle mb-0">
							<thead>
								<tr>
									<th style="width: 120px;" class="text-start">Mes</th>
									<th style="width: 140px;" class="text-end">Disponible</th>
									<th style="width: 160px;">Calendario</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td class="text-start">Enero</td>
									<td>
										<input type="text" readonly
											class="form-control-plaintext form-control-sm text-end cal-disp"
											id="disponibleEnero" value="">
									</td>
									<td>
										<input type="text" class="form-control form-control-sm cal-input"
											id="calendarioEnero" maxlength="12" value="">
									</td>
								</tr>
								<tr>
									<td class="text-start">Febrero</td>
									<td>
										<input type="text" readonly
											class="form-control-plaintext form-control-sm text-end cal-disp"
											id="disponibleFebrero" value="">
									</td>
									<td>
										<input type="text" class="form-control form-control-sm cal-input"
											id="calendarioFebrero" maxlength="12" value="">
									</td>
								</tr>
								<tr>
									<td class="text-start">Marzo</td>
									<td>
										<input type="text" readonly
											class="form-control-plaintext form-control-sm text-end cal-disp"
											id="disponibleMarzo" value="">
									</td>
									<td>
										<input type="text" class="form-control form-control-sm cal-input"
											id="calendarioMarzo" maxlength="12" value="">
									</td>
								</tr>
								<tr>
									<td class="text-start">Abril</td>
									<td>
										<input type="text" readonly
											class="form-control-plaintext form-control-sm text-end cal-disp"
											id="disponibleAbril" value="">
									</td>
									<td>
										<input type="text" class="form-control form-control-sm cal-input"
											id="calendarioAbril" maxlength="12" value="">
									</td>
								</tr>
								<tr>
									<td class="text-start">Mayo</td>
									<td>
										<input type="text" readonly
											class="form-control-plaintext form-control-sm text-end cal-disp"
											id="disponibleMayo" value="">
									</td>
									<td>
										<input type="text" class="form-control form-control-sm cal-input"
											id="calendarioMayo" maxlength="12" value="">
									</td>
								</tr>
								<tr>
									<td class="text-start">Junio</td>
									<td>
										<input type="text" readonly
											class="form-control-plaintext form-control-sm text-end cal-disp"
											id="disponibleJunio" value="">
									</td>
									<td>
										<input type="text" class="form-control form-control-sm cal-input"
											id="calendarioJunio" maxlength="12" value="">
									</td>
								</tr>
								<tr>
									<td class="text-start">Julio</td>
									<td>
										<input type="text" readonly
											class="form-control-plaintext form-control-sm text-end cal-disp"
											id="disponibleJulio" value="">
									</td>
									<td>
										<input type="text" class="form-control form-control-sm cal-input"
											id="calendarioJulio" maxlength="12" value="">
									</td>
								</tr>
								<tr>
									<td class="text-start">Agosto</td>
									<td>
										<input type="text" readonly
											class="form-control-plaintext form-control-sm text-end cal-disp"
											id="disponibleAgosto" value="">
									</td>
									<td>
										<input type="text" class="form-control form-control-sm cal-input"
											id="calendarioAgosto" maxlength="12" value="">
									</td>
								</tr>
								<tr>
									<td class="text-start">Septiembre</td>
									<td>
										<input type="text" readonly
											class="form-control-plaintext form-control-sm text-end cal-disp"
											id="disponibleSeptiembre" value="">
									</td>
									<td>
										<input type="text" class="form-control form-control-sm cal-input"
											id="calendarioSeptiembre" maxlength="12" value="">
									</td>
								</tr>
								<tr>
									<td class="text-start">Octubre</td>
									<td>
										<input type="text" readonly
											class="form-control-plaintext form-control-sm text-end cal-disp"
											id="disponibleOctubre" value="">
									</td>
									<td>
										<input type="text" class="form-control form-control-sm cal-input"
											id="calendarioOctubre" maxlength="12" value="">
									</td>
								</tr>
								<tr>
									<td class="text-start">Noviembre</td>
									<td>
										<input type="text" readonly
											class="form-control-plaintext form-control-sm text-end cal-disp"
											id="disponibleNoviembre" value="">
									</td>
									<td>
										<input type="text" class="form-control form-control-sm cal-input"
											id="calendarioNoviembre" maxlength="12" value="">
									</td>
								</tr>
								<tr>
									<td class="text-start">Diciembre</td>
									<td>
										<input type="text" readonly
											class="form-control-plaintext form-control-sm text-end cal-disp"
											id="disponibleDiciembre" value="">
									</td>
									<td>
										<input type="text" class="form-control form-control-sm cal-input"
											id="calendarioDiciembre" maxlength="12" value="">
									</td>
								</tr>
							</tbody>
						</table>
					</div>

				</div>

				<div class="modal-footer">
					<button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
					<button type="button" class="btn btn-primary" id="btnGuardarCalendario">Guardar</button>
				</div>

			</div>
		</div>
	</div>