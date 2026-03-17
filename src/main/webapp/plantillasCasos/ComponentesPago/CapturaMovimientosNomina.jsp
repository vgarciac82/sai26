<input type="hidden" id="epConsulta" name="epConsulta" />
<input type="hidden" id="mesConsulta" name="mesConsulta" />
<input type="hidden" id="importeConsulta" name="importeConsulta" />
<input type="hidden" id="totalCalendarizado" name="totalCalendarizado" />
<input type="hidden" id="epCalendario" name="epCalendario" />
<input type="hidden" id="importeCalendario" name="importeCalendario" />
<input type="hidden" id="epBorrar" name="epBorrar" />
<input type="hidden" id="diferencia" name="diferencia" />
<div id="capturaMovimientos">
	
		<h1>Captura Movimientos Presupuestales</h1>
		<div id="totalesDiv">
			<div class="row">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
					<label for="mMontoEjercer" class="form-label"> Monto a Ejercer: </label>
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
						<input type="text" readonly="readonly" class="form-control" id="mMontoEjercer"/>
					</div>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
					<label for="mMontoCalendarizado" class="form-label"> Monto Calendarizado: </label>
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>				
						<input type="text" readonly="readonly" class="form-control" id="mMontoCalendarizado" value="0.00"/>
					</div>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
					<label for="mMontoPorEjercer" class="form-label"> Por Ejercer: </label>
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>					
						<input type="text" readonly="readonly" class="form-control" id="mMontoPorEjercer" value="0.00"/>
					</div>
				</div>
			</div>
			
			<br/>
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-2">
					<label for="tConcepto" class="form-label"> Tipo de Concepto </label>					
					<select id="tConcepto" name="tConcepto"style="width: 20em;" class="form-select form-select-sm">
						<option value="-1">Seleccione Tipo de Concepto</option>
					</select>
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-2">
					<label for="tmovimiento" class="form-label"> Tipo De Movimiento </label>						
					<select id="tmovimiento" name="tmovimiento" style="width: 20em;" class="form-select form-select-sm">
						<option value="-1">Seleccione Tipo de Movimiento</option>
					</select>
				</div>
			</div>
			
			<br/>

			<div class="row">
				<div class="col-12">
					<p>
						<b>Seleccione de la siguiente tabla la estructura
							presupuestal (EP) de la que tomara el recurso a ejercer. Doble
							click para editar el monto</b>
					</p>
				</div>
			</div>
			<div class="row">
				<div class="col-12">
					<table id="tblEP" class="table table-striped">
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
			<div class="row">
				<div class="col-12">
						<p>
							<b> Para eliminar un registro del calendario de doble clic en
								el. </b>
						</p>
				</div>
			</div>
			<div class="row">
				<div class="col-12">
						<table id="tblCalendario" class="table table-striped">
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
		</div>
</div>

<div id="dlg-CalendarioMontos">
		<h1>Calendario de Movimientos</h1>
		
		<div class="row">			
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
				<label for="epShow" class="form-label">EP: </label>		
			</div>
			<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-2">						
				<input type="text" size="3" readonly="readonly" id="epShow" class="form-control form-control-sm">				
			</div>
		</div>
		
		<div class="row">			
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
				<label for="disponibleEPTotal" class="form-label">Disponible: </label>		
			</div>
			<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-2">
				<div class="input-group">
					<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>										
					<input type="text" size="3" readonly="readonly" id="disponibleEPTotal" class="form-control form-control-sm">
				</div>				
			</div>
		</div>
		
		<div class="row">			
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
				<label for="montoEjercer" class="form-label">Monto a Ejercer: </label>		
			</div>
			<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-2">	
				<div class="input-group">
					<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>									
					<input type="text" size="3" id="montoEjercer" class="form-control form-control-sm" onKeyPress="return onlyNumbers(event)" onblur="cambiafrmt(this)" onfocus="Sinfrmt(this)" placeholder="0.00">
				</div>				
			</div>
		</div>		
</div>

<div id="CalendarioResumenDiv" style="display: none">
		<h1>Calendario de Movimientos Presupuestales</h1>
		<table id="resumenCalendario" class="table table-striped table-sm">
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