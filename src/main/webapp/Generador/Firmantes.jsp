<input type="hidden" id="numeroEmpleadoVoBo" name="numeroEmpleadoVoBo" value="">
<input type="hidden" id="numeroEmpleadoAutoriza" name="numeroEmpleadoAutoriza" value="">
<input type="hidden" name="tipoSuplenciaAux" id="tipoSuplenciaAux" value="">
<input type="hidden" name="tipoSuplenciaVoBoAux" id="tipoSuplenciaVoBoAux" value="">

<!-- hidden para lo numeros de empleados de los firmantes -->
<input type="hidden" name="nNumEmpleadoVoBo" id="nNumEmpleadoVoBo" value=""/>
<input type="hidden" name="nNumEmpleadoAut" id="nNumEmpleadoAut" value=""/>
<input type="hidden" name="nNumEmpleadoVoBoSuplencia" id="nNumEmpleadoVoBoSuplencia" value=""/>
<input type="hidden" name="nNumEmpleadoAutSuplencia" id="nNumEmpleadoAutSuplencia" value=""/>

<input type="hidden" id="cNombreEmpleado" name="cNombreEmpleado" value=""/>
<input type="hidden" id="cPaternoEmpleado" name="cPaternoEmpleado" value=""/>
<input type="hidden" id="cMaternoEmpleado" name="cMaternoEmpleado" value=""/>
<input type="hidden" id="cPuestoEmpleado" name="cPuestoEmpleado" value=""/>
<input type="hidden" id="cTipoFirmante" name="cTipoFirmante" value=""/>
<input type="hidden" name="nNumEmpleadoBusqueda" id="nNumEmpleadoBusqueda" value=""/>

<input type="hidden" name="cPaternoTitular" id="cPaternoTitular" value=""/>
<input type="hidden" name="cMaternoTitular" id="cMaternoTitular" value=""/>

<!-- HIDDEN PARA VALIDAR SI SE AUTORIZA CON FIEL -->
<input type="hidden" id="autorizadoPorFiel" name="autorizadoPorFiel" value="false">

<div class="modal fade" tabindex="-1" role="dialog" id="dialog-firmantes" data-mdb-keyboard="true" data-mdb-backdrop="static">
	<div class="modal-dialog modal-lg" role="document">
	    <div class="modal-content">
		    <div class="modal-header">
		        <h5 class="modal-title">Captura Firmantes</h5>
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		    </div>
		    <div class="modal-body">
	      		<div class="row">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex AutorizaConFielTD">
						<input type="checkbox" class="form-check-input" id="autorizadoPorFielChk" name="autorizadoPorFielChk"  onclick="autorizadoPorFielAction()"> <label for="autorizadoPorFielChk"> Autorizar con Firma Electronica</label>
					</div>
				</div>
				<div class="row">
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
						<input type="checkbox" class="form-check-input" id="oficioDelegatorio" name="oficioDelegatorio" onclick="showDivOficio(false);"/>Oficio Delegatorio Autoriza
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
						<input type="checkbox" class="form-check-input" id="oficioDeleVoBo" name="oficioDeleVoBo" onclick="showDivOficioVoBo(false);"/>Oficio Delegatorio VoBo
					</div>
				</div>								
				<br/>
				<h5> Datos VºBº </h5>
				<hr class="mt-3">
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cboVoBo" class="form-label"> VoBo: </label>		
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
						<select class="form-select form-select-sm" id="cboVoBo" name="cboVoBo" onchange="infoEmpleado('VOBO');"> </select>
					</div>
				</div>			
				<div class="mt-3" id="tblVoBo">
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cNombreVoBo" class="form-label"> Nombre: </label>
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
							<input type="text" class="form-control form-control-sm" id="cNombreVoBo" name="cNombreVoBo" size=40 maxlength="70"/>
						</div>
					</div>	
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cPaternoVoBo" class="form-label"> Ap. Paterno: </label>
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
							<input type="text" class="form-control form-control-sm" id="cPaternoVoBo" name="cPaternoVoBo" size=40 maxlength="70"/>
						</div>
					</div>	
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cMaternoVoBo" class="form-label"> Ap. Materno: </label>
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
							<input type="text" class="form-control form-control-sm" id="cMaternoVoBo" name="cMaternoVoBo" size=40 maxlength="70"/>
						</div>
					</div>	
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cPuestoVoBo" class="form-label"> Puesto: </label>
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
							<input type="text" class="form-control form-control-sm" id="cPuestoVoBo" name="cPuestoVoBo" size=40 maxlength="70"/>
						</div>
					</div>			
				</div>
				<h5> Datos Autoriza </h5>
				<hr class="mt-3">
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cboAutoriza" class="form-label"> Autoriza: </label>
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
						<select class="form-select form-select-sm" id="cboAutoriza" name="cboAutoriza" onchange="infoEmpleado('AUT');"> </select>
					</div>
				</div>	
				<div class="mt-3" id="tblAut">
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cNombreAut" class="form-label"> Nombre: </label>
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
							<input type="text" class="form-control form-control-sm" id="cNombreAut" name="cNombreAut" size=40 maxlength="70"/>
						</div>
					</div>	
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cPaternoAut" class="form-label"> Ap. Paterno: </label>
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
							<input type="text" class="form-control form-control-sm" id="cPaternoAut" name="cPaternoAut" size=40 maxlength="70"/>
						</div>
					</div>	
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cMaternoAut" class="form-label"> Ap. Materno: </label>
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
							<input type="text" class="form-control form-control-sm" id="cMaternoAut" name="cMaternoAut" size=40 maxlength="70"/>
						</div>
					</div>	
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cPuestoAut" class="form-label"> Puesto: </label>
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
							<input type="text" class="form-control form-control-sm" id="cPuestoAut" name="cPuestoAut" size=40 maxlength="70"/>
						</div>
					</div>		
				</div>
				
				<div id="elaboraDIV">
					<h5> Datos Elabora </h5>
					<hr class="mt-3">
							
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cNombreEla" class="form-label"> Nombre: </label>
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
							<input type="text" class="form-control form-control-sm" id="cNombreEla" name="cNombreEla" size=40 maxlength="70"/>
						</div>
					</div>	
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cPaternoEla" class="form-label"> Ap. Paterno: </label>
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
							<input type="text" class="form-control form-control-sm" id="cPaternoEla" name="cPaternoEla" size=40 maxlength="70"/>
						</div>
					</div>	
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cboAutoriza" class="form-label"> Ap. Materno: </label>
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
							<input type="text" class="form-control form-control-sm" id="cMaternoEla" name="cMaternoEla" size=40 maxlength="70"/>
						</div>
					</div>	
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cPuestoEla" class="form-label"> Puesto: </label>
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
							<input type="text" class="form-control form-control-sm" id="cPuestoEla" name="cPuestoEla" size=40 maxlength="70"/>
						</div>
					</div>					
				</div>
				
				<br/>
				
				<div id="oficioDelegatorioCaptura">
					<h5> Datos del Suplente </h5>
					<hr class="mt-3">	
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cFolioOficio" class="form-label"> No Oficio: </label>
						</div>
						<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">						
							<input type="text" class="form-control form-control-sm" id="cFolioOficio" name="cFolioOficio" size=30 maxlength="70" />
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="dFechaOficio" class="form-label"> Fecha: </label>
						</div>
						<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">			
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-calendar"></i></span>			
								<input type="text" class="form-control form-control-sm" id="dFechaOficio" name="dFechaOficio"/>
							</div>
						</div>
					</div>	
					
					<div class="row">					
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="tipoSuplencia" class="form-label"> T Suplencia: </label>
						</div>
						<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">						
							<select class="form-select form-select-sm" id="tipoSuplencia" name="tipoSuplencia" > </select>
						</div>
					</div>	
					
					<div id="tblSuplenteAut">
						<div class="row">
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
								<label for="cNombreTitular" class="form-label"> Nombre Sup.: </label>
							</div>
							<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
								<input type="text" class="form-control form-control-sm" id="cNombreTitular" name="cNombreTitular" size=40 maxlength="70"/>
							</div>
						</div>	
						
						<div class="row">
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
								<label for="cApellidoPaternoTitular" class="form-label"> Ap. Paterno: </label>
							</div>
							<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
								<input type="text" class="form-control form-control-sm" id="cApellidoPaternoTitular" name="cApellidoPaternoTitular" size=40 maxlength="70"/>
							</div>
						</div>	
						
						<div class="row">
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
								<label for="cApellidoMaternoTitular" class="form-label"> Ap. Materno: </label>
							</div>
							<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
								<input type="text" class="form-control form-control-sm" id="cApellidoMaternoTitular" name="cApellidoMaternoTitular" size=40 maxlength="70"/>
							</div>
						</div>	
						
						<div class="row">
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
								<label for="cPuestoTitular" class="form-label"> Puesto: </label>
							</div>
							<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
								<input type="text" class="form-control form-control-sm" id="cPuestoTitular" name="cPuestoTitular" size=40 maxlength="70"/>
							</div>
						</div>		
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex tblcboSuplenteAut">
							<label for="cboSuplenteAut" class="form-label"> S. Autoriza: </label>
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">						
							<select class="form-select form-select-sm" id="cboSuplenteAut" name="cboSuplenteAut" onchange="infoEmpleado('SUPAUT');"> </select>
						</div>
					</div>		
				</div>
				
				<br/>
			
				<div id="oficioDelegatorioVoBo">
					<h5> Datos del Suplente VoBo</h5>
					<hr class="mt-3">	
				
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="cFolioOficioVoBo" class="form-label"> No Oficio: </label>
						</div>
						<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">						
							<input type="text" class="form-control form-control-sm" id="cFolioOficioVoBo" name="cFolioOficioVoBo" size=30 maxlength="70" />
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="dFechaOficioVoBo" class="form-label"> Fecha: </label>
						</div>
						<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">			
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-calendar"></i></span>			
								<input type="text" class="form-control form-control-sm" id="dFechaOficioVoBo" name="dFechaOficioVoBo"/>
							</div>
						</div>
					</div>	
					
					<div class="row">					
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
							<label for="tipoSuplenciaVoBo" class="form-label"> T Suplencia: </label>
						</div>
						<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">						
							<select class="form-select form-select-sm" id="tipoSuplenciaVoBo" name="tipoSuplenciaVoBo" > </select>
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex tblcboSuplenteVoBo">
							<label for="cboSuplenteVoBo" class="form-label"> S. VoBo: </label>
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">						
							<select class="form-select form-select-sm" id="cboSuplenteVoBo" name="cboSuplenteVoBo" onchange="infoEmpleado('SUPVOBO');"> </select>
						</div>
					</div>
					
					<div id="tblSuplenteVoBo">		
						<div class="row">
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
								<label for="cNombreTitularVoBo" class="form-label"> Nombre Sup.: </label>
							</div>
							<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
								<input type="text" class="form-control form-control-sm" id="cNombreTitularVoBo" name="cNombreTitularVoBo" size=40 maxlength="70"/>
							</div>
						</div>	
						
						<div class="row">
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
								<label for="cApellidoPaternoTitularVoBo" class="form-label"> Ap. Paterno: </label>
							</div>
							<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
								<input type="text" class="form-control form-control-sm" id="cApellidoPaternoTitularVoBo" name="cApellidoPaternoTitularVoBo" size=40 maxlength="70"/>
							</div>
						</div>	
						
						<div class="row">
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
								<label for="cApellidoMaternoTitularVoBo" class="form-label"> Ap. Materno: </label>
							</div>
							<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
								<input type="text" class="form-control form-control-sm" id="cApellidoMaternoTitularVoBo" name="cApellidoMaternoTitularVoBo" size=40 maxlength="70"/>
							</div>
						</div>	
						
						<div class="row">
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
								<label for="cPuestoTitularVoBo" class="form-label"> Puesto: </label>
							</div>
							<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
								<input type="text" class="form-control form-control-sm" id="cPuestoTitularVoBo" name="cPuestoTitularVoBo" size=40 maxlength="70"/>
							</div>
						</div>						
					</div>		
				</div>
		    </div>
			<div class="modal-footer">
	       		<button type="button" id="btnAceptar" onclick="aceptarFirmantes();" class="btn btn-primary">Aceptar</button>
	        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
	       	</div>
    	</div>
  	</div>
</div>

<div id="dialog-firmantesUpdate" title="Actualizacion de Firmantes" class="container">
	<div class="row">
		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
			<input type="checkbox" class="form-check-input" id="oficioDelegatorioUpdate" name="oficioDelegatorioUpdate" onclick="showDivOficio(true);"/>Oficio Delegatorio Autoriza
		</div>
		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
			<input type="checkbox" class="form-check-input" id="oficioDeleVoBoUpdate" name="oficioDeleVoBoUpdate" onclick="showDivOficioVoBo(true);"/>Oficio Delegatorio VoBo
		</div>
	</div>
	
	<div class="row">
		<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
			<input type="button" id="borrarOficioDelegatorio" name="borrarOficioDelegatorio" value="Borrar Of. Delegatorio" class="btn btn-secondary btn-sm" onclick="borraOfiDelegatorio()"/>
		</div>
	</div>								

	<br/>
	
	<h5> Datos VºBº </h5>
	<hr class="mt-3">
	
	<div id="tblVoBoUpdate">
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="cNombreVoBoUpdate" class="form-label"> Nombre: </label>
			</div>
			<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
				<input type="text" class="form-control form-control-sm" id="cNombreVoBoUpdate" name="cNombreVoBoUpdate" size=40 />
			</div>
		</div>	
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="cPaternoVoBoUpdate" class="form-label"> Ap. Paterno: </label>
			</div>
			<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
				<input type="text" class="form-control form-control-sm" id="cPaternoVoBoUpdate" name="cPaternoVoBoUpdate" size=40 />
			</div>
		</div>	
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="cMaternoVoBoUpdate" class="form-label"> Ap. Materno: </label>
			</div>
			<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
				<input type="text" class="form-control form-control-sm" id="cMaternoVoBoUpdate" name="cMaternoVoBoUpdate" size=40 />
			</div>
		</div>	
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="cPuestoVoBoUpdate" class="form-label"> Puesto: </label>
			</div>
			<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
				<input type="text" class="form-control form-control-sm" id="cPuestoVoBoUpdate" name="cPuestoVoBoUpdate" size=40 />
			</div>
		</div>						
	</div>
	
	<div id="tblcboVoBoUpdate">
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="cboVoBoUpdate" class="form-label"> VoBo: </label>
			</div>
			<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
				<select class="form-select form-select-sm" id="cboVoBoUpdate" name="cboVoBoUpdate" onchange="cargaFirmanteVoBoUpdate();"> </select>
			</div>
		</div>		
	</div>
		
	<h5> Datos Autoriza </h5>
	<hr class="mt-3">	
	
	<div id="tblAutUpdate">
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="cNombreAutUpdate" class="form-label"> Nombre: </label>
			</div>
			<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
				<input type="text" class="form-control form-control-sm" id="cNombreAutUpdate" name="cNombreAutUpdate" size=40 />
			</div>
		</div>	
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="cPaternoAutUpdate" class="form-label"> Ap. Paterno: </label>
			</div>
			<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
				<input type="text" class="form-control form-control-sm" id="cPaternoAutUpdate" name="cPaternoAutUpdate" size=40 />
			</div>
		</div>	
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="cMaternoAutUpdate" class="form-label"> Ap. Materno: </label>
			</div>
			<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
				<input type="text" class="form-control form-control-sm" id="cMaternoAutUpdate" name="cMaternoAutUpdate" size=40 />
			</div>
		</div>	
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="cPuestoAutUpdate" class="form-label"> Puesto: </label>
			</div>
			<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
				<input type="text" class="form-control form-control-sm" id="cPuestoAutUpdate" name="cPuestoAutUpdate" size=40 />
			</div>
		</div>						
	</div>
	
	<div id="tblcboAutUpdate">
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="cboAutorizaUpdate" class="form-label"> Autoriza: </label>
			</div>
			<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
				<select class="form-select form-select-sm" id="cboAutorizaUpdate" name="cboAutorizaUpdate" onchange="cargaFirmanteAutUpdate();"> </select>
			</div>
		</div>	
	</div>
	
	<div id="elaboraDIVUpdate">
		<h5> Datos Elabora </h5>
		<hr class="mt-3">		
			
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="cNombreElaUpdate" class="form-label"> Nombre: </label>
			</div>
			<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
				<input type="text" class="form-control form-control-sm" id="cNombreElaUpdate" name="cNombreElaUpdate" size=40 />
			</div>
		</div>	
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="cPaternoElaUpdate" class="form-label"> Ap. Paterno: </label>
			</div>
			<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
				<input type="text" class="form-control form-control-sm" id="cPaternoElaUpdate" name="cPaternoElaUpdate" size=40 />
			</div>
		</div>	
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="cMaternoElaUpdate" class="form-label"> Ap. Materno: </label>
			</div>
			<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
				<input type="text" class="form-control form-control-sm" id="cMaternoElaUpdate" name="cMaternoElaUpdate" size=40 />
			</div>
		</div>	
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="cPuestoElaUpdate" class="form-label"> Puesto: </label>
			</div>
			<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
				<input type="text" class="form-control form-control-sm" id="cPuestoElaUpdate" name="cPuestoElaUpdate" size=40 />
			</div>
		</div>	
	</div>
	
	<div id="oficioDelegatorioCapturaUpdate" style="display: none">
		<h5> Datos del Suplente </h5>
		<hr class="mt-3">		
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="cFolioOficioUpdate" class="form-label"> No Oficio: </label>
			</div>
			<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">						
				<input type="text" class="form-control form-control-sm" id="cFolioOficioUpdate" name="cFolioOficioUpdate" size=30 maxlength="70" />
			</div>
		</div>
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="dFechaOficioUpdate" class="form-label"> Fecha: </label>
			</div>
			<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">			
				<div class="input-group">
					<span class="input-group-text"><i class="bi bi-calendar"></i></span>			
					<input type="text" class="form-control form-control-sm" id="dFechaOficioUpdate" name="dFechaOficioUpdate" readonly/>
				</div>
			</div>
		</div>		
		
		<div class="row">					
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="tipoSuplenciaUpdate" class="form-label"> T Suplencia: </label>
			</div>
			<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">						
				<select class="form-select form-select-sm" id="tipoSuplenciaUpdate" name="tipoSuplenciaUpdate" > </select>
			</div>
		</div>
		
		<h5> Datos del Suplente VoBo</h5>
		<hr class="mt-3">	
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="cFolioOficioUpdate" class="form-label"> No Oficio: </label>
			</div>
			<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">						
				<input type="text" class="form-control form-control-sm" id="cFolioOficioUpdate" name="cFolioOficioUpdate" size=30 maxlength="70" />
			</div>
		</div>
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="dFechaOficioUpdate" class="form-label"> Fecha: </label>
			</div>
			<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">			
				<div class="input-group">
					<span class="input-group-text"><i class="bi bi-calendar"></i></span>			
					<input type="text" class="form-control form-control-sm" id="dFechaOficioUpdate" name="dFechaOficioUpdate" readonly/>
				</div>
			</div>
		</div>		
		
		<div class="row">					
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="tipoSuplenciaUpdate" class="form-label"> T Suplencia: </label>
			</div>
			<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">						
				<select class="form-select form-select-sm" id="tipoSuplenciaUpdate" name="tipoSuplenciaUpdate" > </select>
			</div>
		</div>

		<div id="tblSuplenteAutUpdate">
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cNombreTitularUpdate" class="form-label"> Nombre: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cNombreTitularUpdate" name="cNombreTitularUpdate" size=40 maxlength="70"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cApellidoPaternoTitularUpdate" class="form-label"> Ap. Paterno: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cApellidoPaternoTitularUpdate" name="cApellidoPaternoTitularUpdate" size=40 maxlength="70"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cApellidoMaternoTitularUpdate" class="form-label"> Ap. Materno: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cApellidoMaternoTitularUpdate" name="cApellidoMaternoTitularUpdate" size=40 maxlength="70"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cPuestoTitularUpdate" class="form-label"> Puesto: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cPuestoTitularUpdate" name="cPuestoTitularUpdate" size=40 maxlength="70"/>
				</div>
			</div>				
		</div>
		
		<div id="tblcboSuplenteAutUpdate">
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cboSuplenteAutUpdate" class="form-label"> Suplente Autoriza: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<select class="form-select form-select-sm" id="cboSuplenteAutUpdate" name="cboSuplenteAutUpdate" onchange="cargaSuplenteAutUpdate();"> </select>
				</div>
			</div>				
		</div>
		
	</div>
	
	<div id="oficioDelegatorioVoBoUpdate" style="display: none">
		<h5> Datos del Suplente </h5>
		<hr class="mt-3">		
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="cFolioOficioVoBoUpdate" class="form-label"> No Oficio: </label>
			</div>
			<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">						
				<input type="text" class="form-control form-control-sm" id="cFolioOficioVoBoUpdate" name="cFolioOficioVoBoUpdate" size=30 maxlength="70" />
			</div>
		</div>
		
		<div class="row">
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="dFechaOficioVoBoUpdate" class="form-label"> Fecha: </label>
			</div>
			<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">			
				<div class="input-group">
					<span class="input-group-text"><i class="bi bi-calendar"></i></span>			
					<input type="text" class="form-control form-control-sm" id="dFechaOficioVoBoUpdate" name="dFechaOficioVoBoUpdate" readonly/>
				</div>
			</div>
		</div>		
		
		<div class="row">					
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
				<label for="tipoSuplenciaVoBoUpdate" class="form-label"> T Suplencia: </label>
			</div>
			<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">						
				<select class="form-select form-select-sm" id="tipoSuplenciaVoBoUpdate" name="tipoSuplenciaVoBoUpdate" > </select>
			</div>
		</div>
				
		<div id="tblSuplenteVoBoUpdate">
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cNombreTitularVoBoUpdate" class="form-label"> Nombre: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cNombreTitularVoBoUpdate" name="cNombreTitularVoBoUpdate" size=40 maxlength="70"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cApellidoPaternoTitularVoBoUpdate" class="form-label"> Ap. Paterno: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cApellidoPaternoTitularVoBoUpdate" name="cApellidoPaternoTitularVoBoUpdate" size=40 maxlength="70"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cApellidoMaternoTitularVoBoUpdate" class="form-label"> Ap. Materno: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cApellidoMaternoTitularVoBoUpdate" name="cApellidoMaternoTitularVoBoUpdate" size=40 maxlength="70"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cPuestoTitularVoBoUpdate" class="form-label"> Puesto: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cPuestoTitularVoBoUpdate" name="cPuestoTitularVoBoUpdate" size=40 maxlength="70"/>
				</div>
			</div>				
		</div>
		
		<div id="tblcboSuplenteVoBoUpdate">
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cboSuplenteVoBoUpdate" class="form-label"> Suplente VoBo: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">						
					<select class="form-select form-select-sm" id="cboSuplenteVoBoUpdate" name="cboSuplenteVoBoUpdate" onchange="cargaSuplenteVoBoUpdate();"> </select>
				</div>
			</div>			
		</div>
		
	</div>
</div>		