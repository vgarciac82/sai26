<input type="hidden" id="epConsulta" name="epConsulta" />
<input type="hidden" id="mesConsulta" name="mesConsulta" />
<input type="hidden" id="importeConsulta" name="importeConsulta" />
<input type="hidden" id="importeRetencion" name="importeRetencion" />
<input type="hidden" id="totalCalendarizado" name="totalCalendarizado" />
<input type="hidden" id="totalRetencion" name="totalRetencion" />
<input type="hidden" id="epCalendario" name="epCalendario" />
<input type="hidden" id="importeCalendario" name="importeCalendario" />
<input type="hidden" id="epBorrar" name="epBorrar" />
<input type="hidden" id="esPPD" 	name="esPPD" />
<input type="hidden" id="cIDRFC" 	name="cIDRFC" />
<input type="hidden" id="diferencia" name="diferencia" />
<input type="hidden" id="gp_valor" name="gp_valor" />
<input type="hidden" name="cidcontrato" 	id="cidcontrato" value="RE-A02-1"  />

<div id="capturaMovimientos">
	
<h4>Captura Movimientos Presupuestales</h4>
	<div class="row">
        <div class="col-md-6">
				 <div class="row g-3">
					<div class="col-md-4">
						Fuente de financiamiento
						<select class="form-select" name="cFuenteFinanciamiento" id="cFuenteFinanciamiento" onChange="cambioFF()">
							<option selected>Seleccione Fuente de Financiamiento</option>
						</select>
					</div>
					<div class="col-md-8">
					</div>
					<div class="col-md-4">
					Total:
						<input type="text" readonly="readonly" class="form-control money" id="mMontoEjercer"/>
					</div>
					<div class="col-md-4">
					Calendarizado:
						<input type="text" readonly="readonly" class="form-control money" id="mMontoCalendarizado" value="0.00"/>
					</div>
					<div class="col-md-4">
					Por Ejercer:
						<input type="text" readonly="readonly" class="form-control money" id="mMontoPorEjercer" value="0.00"/>
					</div>
					<div class="col-md-4">
					Ret. Facturas:
						<input type="text" readonly="readonly" class="form-control money" id="mMontoRetFact" value="0.00"/>
					</div>
					<div class="col-md-4">
					Ret. Capturadas:
						<input type="text" readonly="readonly" class="form-control money" id="mMontoRetenciones" value="0.00"/>
					</div>
					<div class="col-md-4">
					Ret. x Capturar:
						<input type="text" readonly="readonly" class="form-control money" id="mMontoRetPendientes" value="0.00"/>
					</div>
			</div>
		</div>
		<div class="col-md-6">
				Catálogo de conceptos y partidas
				<table id="tblConceptosRG" class="display">
						<thead>
							<tr>
								<th align="center">Tipo</th>
								<th align="center">Partidas</th>
								<th align="center">Concepto</th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
		</div>
	</div>
			<div class="row mt-2">
				<div class="col-12">
					<p>
						Seleccione de la siguiente tabla la estructura
						presupuestal (EP) de la que tomara el recurso a ejercer. Doble
						click para editar el monto
					</p>
				</div>
			</div>
			<div class="row">
				<div class="col-12">
					<table id="tblEP" class="display">
							<thead>
								<tr>
									<th align="center">EP</th>
									<th align="center">Enero</th>
									<th align="center">Febrero</th>
									<th align="center">Marzo</th>
									<th align="center">Abril</th>
									<th align="center">Mayo</th>
									<th align="center">Junio</th>
									<th align="center">Julio</th>
									<th align="center">Agosto</th>
									<th align="center">Septiembre</th>
									<th align="center">Octubre</th>
									<th align="center">Noviembre</th>
									<th align="center">Diciembre</th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
				</div>
			</div>
			<div class="row mt-2">
				<div class="col-12">
						<p> Para eliminar un registro del calendario de doble clic en el. </p>
				</div>
			</div>
			<div class="row">
				<div class="col-12">
						<table id="tblCalendario" class="display">
							<thead>
								<tr>
									<th>EP</th>
									<th>Importe</th>
									<th>Retencion</th>
									<th>Concepto Movimiento</th>
									<th>Tipo de Movimiento</th>
								</tr>
							</thead>
						</table>
				</div>
			</div>
		
		<br>
</div>


<div class="modal" tabindex="-1" role="dialog" id="dlg-CalendarioMontos" data-mdb-keyboard="true" data-mdb-backdrop="static">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Calendario de Movimientos</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <div class="form-group row">
				<label for="epShow">EP:</label>
				<div class="col-sm-12 col-form-label">
					<input type="text" id="epShow" size="60" class="form-control"  readonly />
				</div>
			</div>
			<div class="form-group row">
				<label for="epShow">Disponible:</label>
				<div class="col-sm-6 col-form-label">
					<input type="text" id="disponibleEPTotal" class="form-control" readonly />
				</div>
			</div>
			<div class="row">
				<div class="col-sm-6 col-form-label">
					<label for="montoEjercer">Monto a Ejercer:</label>
					<input type="text" id="montoEjercer" class="form-control money" onKeyPress="return onlyNumbers(event)"/>
				</div>
				<div class="col-sm-6 col-form-label">
					<label for="montoRetencion">Monto Retenciones:</label>
					<input type="text" id="montoRetencion" class="form-control money" onKeyPress="return onlyNumbers(event)" onChange="validaRetenciones();" value="0.00"/>
				</div>
			</div>
      </div>
      <div class="modal-footer">
        <button type="button" id="btnAceptarDlgEP" class="btn btn-primary">Aceptar</button>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
      </div>
    </div>
  </div>
</div>

<div id="CalendarioResumenDiv" style="display: none">
		<h4>Calendario de Movimientos Presupuestales</h4>
		<table id="resumenCalendario" align="center">
			<thead>
				<tr>
					<th>EP</th>
					<th>Importe</th>
					<th>Concepto Movimiento</th>
					<th>Tipo de Movimiento</th>
				</tr>
			</thead>
			<tbody></tbody>
		</table>
	
</div>