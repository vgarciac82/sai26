
<div id="container" class="container" style="width: 100%">
	<form name="listadoREP" id="listadoREP" action="../gstnmngr/generaExtraccionListado" method="post">
		<div class="container">
		
			<div class="row d-flex justify-content">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="cUnidadEjecutoraSel" class="form-label"> Unidad Ejecutora: </label>
					</div>
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
					<select id="cUnidadEjecutoraSel" name="cUnidadEjecutoraSel" class="form-select form-select-sm" onchange="cambiaUnidad()"></select>
				</div>				
			</div>
			
			<div class="row d-flex justify-content">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="cRFC" class="form-label"> Proveedor: </label>
					</div>
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
					<select id="cRFC" name="cRFC" class="form-select form-select-sm" onchange="cambiaBeneficiario()"></select>
				</div>	
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<input type="button" id="btnExtraer" name="btnExtraer" value="Extraer" onclick="extraerListado()" class="btn btn-secondary btn-sm"/>
				</div>			
			</div>

		</div>
		
		<div id="CapturaNotificacionDIV" style="display: none">
			<h5><b>Solicitar Recibo a:</b></h5>
			<hr class="mt-3">
			
			<div class="row">				
				<div class="col-12 col-lg-4 col-md-4 col-sm-12">
					<label for="cNombreResponsable" class="form-label">Nombre</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" name="cNombreResponsable" id="cNombreResponsable" class="form-control form-control-sm" size="36"/>
					</div>							
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12">
					<label for="cApPaternoResponsable" class="form-label">Ap. Paterno</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" name="cApPaternoResponsable" id="cApPaternoResponsable" class="form-control form-control-sm" size="36"/>
					</div>							
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12">
					<label for="cApMaternoResponsable" class="form-label">Ap. Materno</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" name="cApMaternoResponsable" id="cApMaternoResponsable" class="form-control form-control-sm" size="36"/>
					</div>							
				</div>
			</div>
			
			<br/>
			
			<div class="row">				
				<div class="col-12 col-lg-4 col-md-4 col-sm-12">
					<label for="cCargoResponsable" class="form-label">Cargo</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
						<input type="text" name="cCargoResponsable" id="cCargoResponsable" class="form-control form-control-sm" size="85"/>
					</div>							
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12">
					<label for="cCorreoNotificaciones" class="form-label">Correo</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-at"></i></span>
						<input type="text" name="cCorreoNotificaciones" id="cCorreoNotificaciones" class="form-control form-control-sm" size="36"/>
					</div>							
				</div>
			</div>
			
			<div class="row d-flex justify-content-center">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="button" id="enviarNotificacionesBtn" name="enviarNotificacionesBtn" value="Enviar Notificaciones" onclick="enviaNotificaciones()" class="btn btn-secondary btn-sm"/>
				</div>
			</div>
			
		</div>
		
		<div class="table-responsive" style="width: 100%">
			<table id="dt_REP_PorComprobar" class="table table-striped">
				<thead>
					<tr>
						<td>Tipo Pago</td>
						<td>Folio</td>
						<td>Fecha Pago</td>
						<td>CXP</td>
						<td>RFC</td>
						<td>Razon Social</td>
						<td width="19%" style="text-align: center">UUID</td>
						<td>Monto Factura</td>
						<td>Monto Pendiente</td>
						<td>Unidad Responsable</td>
					</tr>
				</thead>
				<tbody />
			</table>
		</div>
		
		<div>
			<input type="hidden" name="cIdRFC" id="cIdRFC" value="" />
			<input type="hidden" name="UR" id="UR" value="" />
		</div>
	</form>
</div>
