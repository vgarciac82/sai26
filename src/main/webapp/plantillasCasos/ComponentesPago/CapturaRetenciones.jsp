
<div id="retencionesDIV">
	<input type="hidden" id="obligatoria" name="obligatoria" value="" />
	<input type="hidden" id="diferencia" name="diferencia" value="" />
	<input type="hidden" id="cTipoRetencion" name="cTipoRetencion" value="" />
	<input type="hidden" id="idRetencion" name="idRetencion" value="" />
	<input type="hidden" id="prefijoResumenRetencion" name="prefijoResumenRetencion" value="" />
	
	<h1>Retenciones</h1>
	
	<div class="row">
		<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-2">
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
			<label for="retencionesFacturas" class="form-label">Retenciones en Facturas: </label>
			<div class="input-group">
				<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>				
				<input type="text" size="15" class="form-control form-control-sm" name="retencionesFacturas" id="retencionesFacturas" class="money" readonly />
			</div>
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
			<label for="retencionesCapturadas" class="form-label">Retenciones Capturadas: </label>
			<div class="input-group">
				<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>				
				<input type="text" size="15" class="form-control form-control-sm" name="retencionesCapturadas" id="retencionesCapturadas" class="money" readonly />
			</div>
		</div>
	</div>
	
	<div class="row">
		<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-2">
		</div>
		<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-2">
			<label for="cIdTipoRetencion" class="form-label">Tipo de Retenci&oacute;n: </label>						
			<select name="cIdTipoRetencion" id="cIdTipoRetencion" onChange="tipoRetencionChange(this)" class="form-select form-select-sm"></select>			
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
			<label for="importeRetencion" class="form-label">Importe Retenci&oacute;n: </label>
			<div class="input-group">
				<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>				
				<input type="text" readonly="readonly" class="form-control form-control-sm" id="importeRetencionCalculado" name="importeRetencion"/>				
			</div>
		</div>
		<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-2">
			<input type="button" id="btAgregaMov" name="btAgregaMov" value="Agregar" class="btn btn-secondary sm"/>
		</div>
	</div>
	
	<br/>
		
	<div class="row">
		<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-2">
		</div>
		<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-2">
			<p>
				<b>*Doble clic en un renglon para editar</b>
			</p>
		</div>
	</div>
	
	<div class="row">
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
		</div>
		<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-2">
			<table id="grdRetencion" class="table table-striped table-sm">
				<thead>
					<tr>
						<th>C&oacute;digo</th>
						<th>Retenci&oacute;n</th>
						<th>Importe Base</th>
						<th>Porcentaje</th>
						<th>Importe Retencion</th>
						<th>Descartar</th>
					</tr>
				</thead>
			</table>		
		</div>
	</div>
		
</div>

<div id="dlg-EditaRetencion">
	<h1>Editar Retenci&oacute;n</h1>
	
	<div class="row">
		<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-2">
		</div>	
		<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-2">
			<label for="idRetencionEdit" class="form-label">ID: </label>		
		</div>
		<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-2">
			<div class="input-group">
				<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>				
				<input type="text" size="3" readonly="readonly" id="idRetencionEdit" class="form-control form-control-sm">
			</div>	
		</div>
	</div>
	
	<div class="row">
		<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-2">
		</div>	
		<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-2">
			<label for="descRetencionEdit" class="form-label">Retencion: </label>		
		</div>
		<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-2">
			<div class="input-group">
				<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>				
				<input type="text" size="3" readonly="readonly" id="descRetencionEdit" class="form-control form-control-sm">
			</div>	
		</div>
	</div>
	
	<div class="row">
		<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-2">
		</div>	
		<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-2">
			<label for="importeCalculadoEdit" class="form-label">Importe Calculado: </label>		
		</div>
		<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-2">
			<div class="input-group">
				<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>				
				<input type="text" size="3" readonly="readonly" id="importeCalculadoEdit" class="form-control form-control-sm">
			</div>	
		</div>
	</div>
	
	<div class="row">
		<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-2">
		</div>	
		<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-2">
			<label for="nuevoImporteRetEdit" class="form-label">Nuevo Importe: </label>		
		</div>
		<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-2">
			<div class="input-group">
				<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>				
				<input type="text" size="3" id="nuevoImporteRetEdit" class="form-control form-control-sm" placeholder="0.00">
			</div>	
		</div>
	</div>
	
</div>

<div id="resumenRetencionDiv" style="display:none">	
	<h1>Retenciones</h1>
	<table id="resumenRetencion" class="table table-striped table-sm">	
		<thead>
			<tr>
				<th>Retenci&oacute;n</th>
				<th>Importe Base</th>
				<th>Porcentaje</th>
				<th>Importe Retencion</th>
			</tr>
		</thead>
	</table>	
</div>