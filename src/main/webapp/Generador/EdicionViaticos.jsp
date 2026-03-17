<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Editar la Comisión antes de ser Autorizada</title>
</head>
<body>
 <form id="formEdicion">
	<div id="encabezado" class="col-12">
		<input type="hidden" id="pais" 			name="pais"/>
		<input type="hidden" id="estado" 		name="estado"/>
		<input type="hidden" id="municipio" 	name="municipio"/>
		<input type="hidden" id="nidMunicipio" 	name="nidMunicipio"/>
		<input type="hidden" id="nidtipo" 		name="nidtipo"/>
		<input type="hidden" id="tipoTransp" 	name="tipoTransp"/>
		<input type="hidden" id="niveles" 		name="niveles"/>
		<input type="hidden" id="paquetes" 		name="paquetes"/>
		<input type="hidden" id="idTransporte" 	name="idTransporte"/>	
		<input type="hidden" id="folioA" 		name="folioA" value = 0/>
		<input type="hidden" id="dateTemp" 		name="dateTemp"/>
		<input type="hidden" id="dateExist" 	name="dateExist"/>
		<input type="hidden" id="cTieneHomologacion" name="cTieneHomologacion"/>
		<input type="hidden" id="cTienePaquete" name="cTienePaquete"/>
		<input type="hidden" id="hasPackage" 	name="hasPackage" value= 0/>
		<input type="hidden" id="hasSameRate" 	name="hasSameRate" value = 0/>
		<input type="hidden" id="id" 			name="id" />
		<input type="hidden" id="tipoOperacion" name="tipoOperacion" value= "EDICION_SIN_FIRMAS"/>
		<input type="hidden" id="operacion" 	name="operacion" value= "EDITAR"/>
		<input type="hidden" id="tieneVales" 	name="tieneVales" />
		<input type="hidden" id="noEmpleadoComision" name="noEmpleadoComision" />
		<input type="hidden" id="tieneAgenda" 	name="tieneAgenda" />
		<input type="hidden" id="saldoPagos" 	name="saldoPagos" />
		<input type="hidden" id="saldoComision" name="saldoComision" />
		<input type="hidden" id="nEmpleado" 	name="nEmpleado" />
		<input type="hidden" id="nComisionAnterior" name="nComisionAnterior"/>
		<input type="hidden" id="nComisionAnteriorF" name="nComisionAnteriorF"/>
		<input type="hidden" id="fechaInicio" 	name="fechaInicio"/>
		<input type="hidden" id="fechaFin" 		name="fechaFin"/>
		<input type="hidden" id="fFinAnterior" name="fFinAnterior" />
		<input type="hidden" id="registros" 	name="registros" />
		<input type="hidden" id="nidAgenda" 	name="nidAgenda" />
		
		<p class="h5">Agenda</p>
		<hr>
	     	<div class="row ms-2">
				<div class="col-1">
				      Comision
				      <input type="text" class="form-control" id="cComision" name="cComision"   readonly/>
			    </div>
			    <div class="col-1">
				      Agenda
				      <input type="text" class="form-control" id="idAgenda" name="idAgenda"   readonly/>
			    </div> 								   
				<div class="col-md-3">
					    Fecha Inicio
					    <div class="input-group">
					      <span class="input-group-text"><i class="bi bi-calendar3"></i></span> 
					      <input type="text" class="form-control" id="fInicio" name="fInicio" size="15"  readonly/>
					    </div>
				</div>
			    <div class="col-3">
			      	Fecha Fin 
			      	 <div class="input-group">
					      <span class="input-group-text"><i class="bi bi-calendar3"></i></span> 
					      <input type="text" class="form-control" id="fFin" name="fFin" size="15" onchange = "verificaFechasEdicion();" readonly/>
					    </div>
			    </div>
				<div class="col-4">
			    	País:
			      	<div class="input-group">
					      <span class="input-group-text"><i class="bi bi-globe"></i></span> 
					      <select class="form-select form-select" name="cPais" id="cPais" onchange="cargaEstados(), cargaMunicipios()" ></select>
					</div>
			    </div>
			</div>
			<div class="row ms-2">
			    <div class="col-4">
			      	Estado:
			      	<div class="input-group">
					      <span class="input-group-text"><i class="bi bi-buildings"></i></span> 
					      <select class="form-select form-select" name="estadocombo" id="estadocombo" onchange="cargaMunicipios()" ></select>
					</div>
			      	<div id="dlgAgregar" title="Agregar nuevo Estado" style="display: none;">
			      		<div class="form-row">
								Estado/Provincia: 
								<div class="input-group">	
									<span class="input-group-text"><i class="bi bi-hospital"></i></span>
	    							<input type="text" class="form-control" id="nombre_estado" name="nombre_estado" size="25" />
	    							<input type="button" class="btn btn-secondary" id="btnAgregar" name="btnAgregar" onclick="agregarEstado()" value ="Agregar"/>
	    						</div>
	    				</div>
	    			</div>	
			    </div>
				<div class="col-4">
				    Municipio:
				    <div class="input-group">
					      <span class="input-group-text"><i class="bi bi-building"></i></span> 
					      <select class="form-select form-select" name="municipiocombo" id="municipiocombo" onchange="validaMunicipio()"></select>
					</div>
				    <div id="dlgAgregarMpio" title="Agregar nueva Ciudad" style="display: none;">
						<div class="form-row">
									Ciudad:	
									<div class="input-group">	
									 	<span class="input-group-text"><i class="bi bi-building-add"></i></span>						
									<input type="text" class="form-control" id="nombre_municipio" name="nombre_municipio" size="25" />
									<input type="button" class="btn btn-secondary" id="btnAgregarMpio" name="btnAgregarMpio" onclick="agregarMunicipio()" value="Agregar"/>
									</div>
						</div>
					</div>
			  	</div>
			  	<div class="col-4">
					    Localidad:
					    <div class="input-group">
						      <span class="input-group-text"><i class="bi bi-houses"></i></span> 
						      <input type="text" class="form-control" id="cLocalidad" name="cLocalidad" size="20" aria-describedby="inputGroup-sizing-sm"/>
						</div>
			  	</div>
			</div>
			<div class="row ms-2">
				    <div class="col-12">
				      	Motivo de la comisión
						<textarea class="form-control" id="motivoComision" name="motivoComision" rows="2" ></textarea>
				    </div>
			</div>
			<div class="row ms-2">
				    <div class="col-auto">
				      	Nombre de la comisión
				      	<div class="input-group">
					      <span class="input-group-text"><i class="bi bi-airplane"></i></span> 
					      <input type="text" class="form-control" id="nombreComision" name="nombreComision" size=30 maxlength="40" />
						</div>
				    </div>
				    <div class="col-auto">
						<label for = "cCuota">Cuota por día</label>
				    	<div class="input-group">
				    		<span class="input-group-text">$</span>
							<input type="number" class="form-control" id="cCuota" name="cCuota" readonly/>
						</div>
				    </div>
				    <div class="col-auto">										    	
							<label for="cMoneda">Moneda: </label>
							<select class="form-select form-select" name="cMoneda" id="cMoneda" onchange="leerTipoMoneda()"></select>
					</div>
					<div class="col-auto">
							<label for="tipoCambio">T.Cambio: </label>
							<input type="text" class="form-control" id="tipoCambio" name="tipoCambio" value="1" />
					</div>
			</div>
			<div class="row ms-4">
				<div class="form-check mt-2">
					<div class="input-group">
						<div class="col-md-2">
							<input class="form-check-input" type="checkbox" id="chk_homologa" onclick="activarBotones()" value="0">
							<label class="form-check-label" for="chk_homologa">Homologar</label>
						</div>
					    <div class="col-md-3 inputHomologa">
					    	<label id="lblNivel">Nivel Homologa</label>
					    	<div class="input-group">
									<span class="input-group-text"><i class="bi bi-arrow-up-right-circle"></i></span>
					    			<select class="form-select" id="nivelHomologa" name="nivelHomologa" onchange="cargaNiveles()"></select>
					    	</div>
					    </div>
				
					    <div class="col-md-3 inputHomologa">
					    	<label id="lblPlaza">Plaza</label>
					    	<input type="text" class="form-control" id="plazaHomologa" name ="plazaHomologa" />
					    </div>
					    <div class="col-md-3 inputHomologa">
					    	<label id="lblJustifica">Justificación</label>
					    	<input type="text" class="form-control" id="plazaJustifica" name ="plazaJustifica" />
					    </div>
					</div>
				</div>
			</div>	
			<div class="row ms-4">
				<div class="form-check">
					<div class="input-group"> 
							<div class="col-md-2">
								<input class="form-check-input" type="checkbox" id="chk_paquete" onclick="activarPaquete()" value="0">
								<label class="form-check-label" for="chk_paquete">Paquetes</label>
							</div>
							<div class="col-md-5 inputPaquete">
								<div class="input-group">
									<span class="input-group-text"><i class="bi bi-box-seam"></i></span>
									<select class="form-select" id="paqueteCombo" name="paqueteCombo" onchange="cargaPaquetes()"></select>
								</div>
							</div>
							<div class="col-md-2 inputPaquete">
								<div class="input-group">
									<input class="form-control" type="text"  id="nPorcentaje" name ="nPorcentaje" readonly/>
									<span class="input-group-text">%</span>
								</div>
							</div>
							<div class="col-md-3"></div>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-7"></div>
				<div class="col-md-5">
						<input type="button" class="btn btn-secondary"  id="btnAgregaAgenda" name="btnAgregaAgenda" onclick="guardaNuevaAgenda()" value ="Agregar Nueva Agenda"/>
						<input type="button" class="btn btn-primary"  id="btnEditarAgenda" name="btnEditarAgenda" onclick="updateAgenda()" value ="Guardar Agenda"/>
						<input type="button" class="btn btn-dark"  id="btnLimpiar" name="btnLimpiar" onclick="limpiar(); mostrarBotonesAgenda();" value ="Limpiar"/>
				</div>
			</div>
		</div>
			<div id="divTablaAgenda" class="table-responsive">
				Doble click para editar el renglón de la Agenda
				<table id="tablaAgendaEditar" class="table table-striped table-bordered">
					<thead>
					    <tr>
					      <th>#</th>
					      <th>F.Inicio</th>
					      <th>F.Fin</th>
					      <th>Destino</th>
					      <th>Motivo</th>
					      <th>Cuota x dia</th>
					      <th>Dias</th>
					      <th>Importe</th>
					      <th>Eliminar</th>
					    </tr>
					  </thead>
				</table>
			</div>
			<br>
			<div id="transporteEncabezado" >	
				<p class="h5">Transporte</p>
            	<div class="row">
						<div class="col-md-3">
						      Tipo Transporte 
						      <div class="input-group">								
									<span class="input-group-text"><i class="bi bi-taxi-front"></i></span>
									<select class="form-select" name="transporteCombo" id="transporteCombo" onchange="cargaTransporte(); validaTransporte();"></select>
							  </div>
					    </div>
					    <div class="col-md-3">
					      		Descripción <input type="text" class="form-control" id="descripcion" name="descripcion" size="15"/>
					    </div>
					    <div class="col-md-2">
					    	<label for= "importeT">Importe</label>
					    	<div class="input-group">
					    		<span class="input-group-text">$</span>
					      	 	<input type="number" class="form-control" id="importeT" name="importeT" size="15"/>
					      	 </div>
					    </div>
					    <div class="col-md-4">
					   		  <br>
						      <input type="button" class="btn btn-secondary" onclick="agregarNuevoTransporte()"  id="btnAgregarTrans" name="btnAgregarTrans" value="Agregar Nuevo Transp"/>
						      <input type="button" class="btn btn-primary" onclick="editarTransporte()"  id="btnEditarTrans" name="btnEditarTrans" value="Guardar Transporte"/>
						      <input type="button" class="btn btn-dark" onclick="limpiarTransporte(); mostratBotonesTransporte();"  id="btnLimpiarTrans" name="btnLimpiarTrans" value="Limpiar"/>	 	 
					    </div>
				</div>
				<div id="transporteDet">
					<div class="row">
						<div class="col-md-2">
					      Kilometraje <input type="number" class="form-control" id="km" name="km" value="0"/>
					    </div>
					    <div class="col-md-1"> </div>
					    <div class="col-md-2">
					    	<label id="lblnEconomico">Num Economico </label>
					      <input type="text" class="form-control" id="nEconomico" name="nEconomico" size="15"/>
					    </div>
					    <div class="col-md-1"></div>
					    <div class="col-md-3">
					       	<div class="form-check"> 	
  								<input class="form-check-input" type="checkbox" id="chk_tieneVales" value="1">
  								<label class="form-check-label" for="chk_tieneVales">Tiene Vales</label>
 								</div>									      
					    </div>
					</div>		
				</div>
				<div class="col-md-12">
					<div class="table-responsive text-nowrap mt-2" id="divTransporte">
						Doble click para editar el renglón de Transporte
						<table id="tablaTransporteEditar" class="table table-striped table-bordered">
							<thead>
							    <tr>
							      <th>#</th>
							      <th>Tipo</th>
							      <th>Origen</th>
							      <th>Monto</th>
							      <th>Km</th>
							      <th>Num Economico</th>
							      <th>Tiene Vales</th>
							      <th>Eliminar</th>
							    </tr>
							</thead>
						</table>
					</div>
				</div>
			<div class="row">
				<div class="col-9"></div>
				<div class="col-3">
						<input type="button" class="btn btn-primary"  id="btnGuardarEnviar" name="btnGuardarEnviar" onclick="guardarEnviarAgenda()" value ="Guardar y Enviar a Firmar"/>
				</div>
			</div>
		</div>
	</form>
		
</body>
</html>