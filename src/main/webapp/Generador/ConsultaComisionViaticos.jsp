	<div class="form-group mx-auto">

		<input type="hidden" name="operador" 	 	id="operador" 		value=""/>
		<input type="hidden" name="fechaCaptura" 	id="fechaCaptura" 	value=""/>
		<input type="hidden" name="fechaAplicacion" id="fechaAplicacion" value=""/>
		<input type="hidden" name="nFolioPagoFed" 	id="nFolioPagoFed" 	value=""/>
		<input type="hidden" name="ramo" 			id="ramo" 			value=""/>
		<input type="hidden" name="ejercicioFiscal" id="ejercicioFiscal" value=""/>
		<input type="hidden" name="folio" 			id="folio" 			value=""/>
		<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value=""/>
		<input type="hidden" name="nIdEstatus" 		id="nIdEstatus" 	value=""/>
		<input type="hidden" name="cDocHaplicado" 	id="cDocHaplicado"/>
		
		
		<div id="container" class="container-fluid">
			
			<div id="divDatosEmpleado">
				<div class="row">	
					<div class="col-md-6">
						<div class= "card">
							<div class="card-body">
								<div class="row">
								 	<div class="col-md-2">
								     	<b> #Emp </b><input type="text" class="form-control-plaintext" id="numEmpleado" name="numEmpleado" />
								    </div>
									<div class="col-md-4">
										<label><b>RFC Empleado</b></label>					    
								        <div class="input-group">
								        	<input class="form-control-plaintext"  type="text" maxlength="15" size="15" name="cIdRFC" id="cIdRFC" onchange="cargaCtaBancariaRFC();"/>
								        </div>
							    	</div>
							    	<div class="col-md-2">
								      <b>UR </b><input type="text" class="form-control-plaintext" id="UR" name="UR"   />
								    </div>
									
									<div class="col-md-2">
									   	<b>Nivel </b><input type="text" class="form-control-plaintext" id="cNivel" name="cNivel"  value="0" >
								  	</div>
									<div class="col-md-2">
									   	<b>Folio</b> <input type="text" class="form-control-plaintext" id="nidComision" name="nidComision" value="" />
									</div>
								</div>
								<div class="row">		
								    <div class="col-md-12">
								    	<b>Nombre</b>
								       <div class="input-group">
									      	<input type="text" class="form-control-plaintext" id="nombreCompleto" name="nombreCompleto" />
									    </div>
								    </div>
								</div>
								<div class="row">
								    <div class="col-md-12">
								      <b>Plaza </b> 
								      <div class="input-group">
									      	<input type="text" class="form-control-plaintext" id="cPlaza" name="cPlaza"   />
									   </div>
								    </div>
								</div>
								<div class="row">
								    <div class="col-md-12">
								      <b>Unidad Responsable </b> 
								      <div class="input-group">
									      	<input type="text" class="form-control-plaintext" id="dUnidadResponsable" name="dUnidadResponsable"   />
									   </div>
								    </div>
								</div>
							</div>
						</div>
				</div>
				<div class="col-md-6">
					<div class= "card">
						<div class="card-body"> 
							<div class="row">
								<div id="resumen1" class="col-5" > 
					             	<h6 class="h6" align="center">Dias acumulados</h6>
					             	<div class="form-group row">
				              				<label class="col-7 col-form-label" for="diasNacional">Nacional</label>
				              				<div class="col-5">
					              				<input type="text" class="form-control-plaintext col-2" id="diasNacional" name="diasNacional" size="5" value = "0" readonly/>
					              			</div>
									</div>
									<div class="form-group row">
										<label class="col-7 col-form-label" for="diasInternacional">Internacional</label>
										<div class="col-5">
												<input type="text" class="form-control-plaintext col-2" id="diasInternacional" name="diasInternacional" size="5" value = "0" readonly/>
										</div>
									</div>
									<div class="form-group row">
										<label for="totalDias" class="col-7 col-form-label">Pendientes</label>
										<div class="col-5">
												<input type="text" class="form-control-plaintext col-2" id="totalDias" name="totalDias" size="5" value ="0" readonly/>
										</div>
									</div>
									<div class="form-group row">
									    <label for="totalAcDias" class="col-7 col-form-label">Total Dias</label>
									    <div class="col-5">
									      		<input type="number" class="form-control-plaintext" id="totalAcDias" value ="0" readonly/>
									    </div>
									</div>
				           </div>
				              	<div id="resumen2" class="col-7" align="center">
				              		<h6 class="h6">Resumen Viáticos</h6>
				              		
				              		<div class="form-group row">
				              				<label class="col-6 col-form-label text-end" for="totalAgenda">Total Agenda $</label>
				              				<div class="col-6">
					              					<input type="text" class="form-control-plaintext col-2" id="totalAgenda" name="totalAgenda" size="10" readonly/>
					              				
					              			</div>
									</div>
									<div class="form-group row">
										<label class="col-6 col-form-label text-end"  for="totalTransporte">Total Transporte $</label>
										<div class="col-6">
											<div class="form-group">
										    	
												<input type="text" class="form-control-plaintext col-2" id="totalTransporte" name="totalTransporte" size="10" readonly/>
											</div>
										</div>
									</div>
									<div class="form-group row">
										<label class="col-6 col-form-label text-end" id="tblTotalGral" for="totalGeneral">Total General $</label>
										<div class="col-6">
											<div class="form-group">
									    		
												<input type="text" class="form-control-plaintext col-2" id="totalGeneral" name="totalGeneral" size="10" readonly/>
											</div>
										</div>
									</div>
				              	</div>
				             </div>
						</div>
					</div> <!-- Termina Card empleado -->
				  </div>
				</div><!-- Termina row -->
			</div>
			<br>
			<div>
				<h6 class="h6">Agenda</h6>
				<div class="table-responsive">
						<table id="tablaAgendaConsulta" class="table table-striped">
							<thead>
							    <tr>
							      <th>#</th>
							      <th style="width:15%">F.Inicio</th>
							      <th style="width:15%">F.Fin</th>
							      <th style="width:25%">Tipo</th>
							      <th style="width:25%">Motivo</th>
							      <th>Cuota x dia</th>
							      <th>Dias</th>
							      <th>Importe</th>
							      <th>%</th>
							    </tr>
							  </thead>
							  <tbody></tbody>
						</table>
				</div>
			</div>
			<div class="row mt-2">
				<div class="col-12">
					<p class="h6">Transporte</p>
					<div class="table-responsive">
							<table id="tablaTransporteConsulta" class="table table-striped">
								<thead>
								    <tr>
								      <th>#</th>
								      <th>Tipo</th>
								      <th>Origen</th>
								      <th>Monto</th>
								      <th>Km</th>
								      <th>Num Economico</th>
								      <th>Tiene Vales</th>
								    </tr>
								</thead>
							</table>
					</div>
				</div>
			</div>
			<br>
			<div class="row">
				<div class="col-12">
					<p class="h6">Autorizadores</p>
					<div class="table-responsive text-nowrap">
							<table id="tablaAutoriza" class="table table-striped">
								<thead>
								    <tr>
								      <th>#</th>
								      <th>Nombre</th>
								      <th>Puesto</th>
								      <th>Fecha</th>
								      <th>Aut.</th>
								      <th>Notas</th>
								    </tr>
								</thead>
							</table>
					</div>
				</div>
			</div>
		</div>
	</div>			
 
