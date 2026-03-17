<input type="hidden" id="epConsulta" name="epConsulta" />
<input type="hidden" id="mesConsulta" name="mesConsulta" />
<input type="hidden" id="importeConsulta" name="importeConsulta" />
<input type="hidden" id="importeRetencion" name="importeRetencion" />
<input type="hidden" id="totalCalendarizado" name="totalCalendarizado" />
<input type="hidden" id="epCalendario" name="epCalendario" />
<input type="hidden" id="importeCalendario" name="importeCalendario" />
<input type="hidden" id="epBorrar" name="epBorrar" />
<input type="hidden" id="diferencia" name="diferencia" />
<input type="hidden" id="totalRetencion" name="totalRetencion" />
<div id="capturaMovimientos">
	
		<h1 id="divTitle">Captura Movimientos Presupuestales</h1>
		<div id="totalesDiv">
			<div class="row">
				<div class="col-3">
				Monto a Ejercer:
					<input type="text" readonly="readonly" class="form-control money" id="mMontoEjercer"/>
				</div>
				<div class="col-3">
				Monto Calendarizado:
					<input type="text" readonly="readonly" class="form-control money" id="mMontoCalendarizado" value="0.00"/>
				</div>
				<div class="col-3">
				Por Ejercer:
					<input type="text" readonly="readonly" class="form-control money" id="mMontoPorEjercer" value="0.00"/>
				</div>
			</div>
			<div class="row" id ="divRetenciones">
				<div class="col-md-3">
					Ret. Facturas:
						<input type="text" readonly="readonly" class="form-control money" id="mMontoRetFact" value="0.00"/>
					</div>
					<div class="col-md-3">
					Ret. Capturadas:
						<input type="text" readonly="readonly" class="form-control money" id="mMontoRetenciones" value="0.00"/>
					</div>
					<div class="col-md-3">
					Ret. x Capturar:
						<input type="text" readonly="readonly" class="form-control money" id="mMontoRetPendientes" value="0.00"/>
					</div>
			</div>
			<div class="row" id="divConcepto">
				<div class="col-3">
					Tipo de Concepto
					<select id="tConcepto" name="tConcepto"style="width: 20em;" class="form-select form-select-sm">
							<option value="-1">Seleccione Tipo de Concepto</option>
						</select>
				</div>
				<div class="col-3">
					Tipo De Movimiento
					<select id="tmovimiento" name="tmovimiento" style="width: 20em;" class="form-select form-select-sm">
							<option value="-1">Seleccione Tipo de Movimiento</option>
						</select>
				</div>
			</div>
			<div class="row mt-2">
				<div class="col-12">
					<h6>
						<b>Seleccione de la siguiente tabla la estructura
							presupuestal (EP) de la que tomara el recurso a ejercer. Doble
							click para editar el monto</b>
					</h6>
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
						<h6>
							<b> Para eliminar un registro del calendario de doble clic en
								el. </b>
						</h6>
				</div>
			</div>
			<div class="row" id="divCalendario">
				<div class="col-12">
						<table id="tblCalendario" class="display">
							<thead>
								<tr>
									<th>EP</th>
									<th>Importe</th>
									<th>Concepto Movimiento</th>
									<th>Tipo de Movimiento</th>
								</tr>
							</thead>
						</table>
				</div>
			</div>
			<div class="row" id="divCalendarioRG">
				<div class="col-12">
						<table id="tblCalendarioRG" class="display">
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
		</div>
</div>

<div class="modal" tabindex="-1" role="dialog" id="dlg-CalendarioMontos" data-mdb-keyboard="true" data-mdb-backdrop="static">
  	<div class="modal-dialog" role="document">
    	<div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title">Calendario de Movimientos</h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	      </div>
	      <div class="modal-body">
		      	<div class="row">
					<div class="col-12">
						<label for="epShow">EP:</label>
						<input type="text"  id="epShow" name="epShow" class="form-control"/>
					</div>
					<div class="col-6">
						<label for="disponibleEPTotal">Disponible:</label>
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
							<input type="text"  id="disponibleEPTotal" name="disponibleEPTotal" class="form-control"/>
						</div>
					</div>
					<div class="col-6">
						<label for="montoEjercer">Monto a Ejercer:</label>
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
							<input type="text"  id="montoEjercer" name="montoEjercer" class="form-control"/>
						</div>
					</div>
				</div>
	       </div>
	      <div class="modal-footer">
	        <button type="button" id="btnGuardarMontos" onclick="guardaMontos();" class="btn btn-primary">Aceptar</button>
	        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
	      </div>
    	</div>
  	</div>
</div>

<div id="CalendarioResumenDiv" style="display: none">
	
		<h1>Calendario de Movimientos Presupuestales</h1>
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