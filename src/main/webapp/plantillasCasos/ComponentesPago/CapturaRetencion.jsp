
<div id="retencionesDIV">
	<input type="hidden" id="obligatoria" name="obligatoria" value="" />
	<input type="hidden" id="diferencia" name="diferencia" value="" />
	<input type="hidden" id="cTipoRetencion" name="cTipoRetencion" value="" />
	<input type="hidden" id="idRetencion" name="idRetencion" value="" />
	<input type="hidden" id="prefijoResumenRetencion" name="prefijoResumenRetencion" value="" />
	
	<fieldset>
		<h1>Retenciones</h1>
		<table>
			<tr>
				<td>Retenciones en Facturas:</td>			
				<td>Retenciones Contrato:</td>			
			</tr>
			<tr>
				<td align="left">
					<input type="text" size="15" name="retencionesFacturas" id="retencionesFacturas" class="form-control money" readonly /></td>
				<td align="left">
					<input type="text" size="15" name="retencionesCapturadas" id="retencionesCapturadas" class="form-control money" readonly /></td>
			</tr>
			
			<tr>
				<td colspan="7">*Doble clic en un renglon para editar</td>
			</tr>
			<tr>
				<td colspan="7">
					<div>
						<table id="grdRetencion" class="display">
							<thead>
								<tr>
									<th>C&oacute;digo</th>
									<th>Retenci&oacute;n</th>
									<th>Importe Base</th>
									<th>Porcentaje</th>
									<th>Importe Retencion</th>
								</tr>
							</thead>
						</table>						
					</div>
				</td>
			</tr>
		</table>
	</fieldset>
</div>
<div class="modal" tabindex="-1" role="dialog" id="dlg-EditaRetencion" data-mdb-keyboard="true" data-mdb-backdrop="static">
  	<div class="modal-dialog" role="document">
    	<div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title">Editar Retenci&oacute;n</h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	      </div>
	      <div class="modal-body">
		      	<div class="row">
					<div class="col-6">
						<label for="idRetencionEdit">ID:</label>
						<input type="text"  id="idRetencionEdit" name="idRetencionEdit" class="form-control"/>
					</div>
					<div class="col-6">
						<label for="descRetencionEdit">Retencion:</label>
						<input type="text"  id="descRetencionEdit" name="descRetencionEdit" class="form-control"/>
					</div>
				</div>
				<div class="row">
					<div class="col-6">
						<label for="importeCalculadoEdit">Importe Calculado:</label>
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
							<input type="text"  id="importeCalculadoEdit" name="importeCalculadoEdit" class="form-control"/>
						</div>
					</div>
					<div class="col-6">
						<label for="nuevoImporteRetEdit">Nuevo Importe:</label>
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
							<input type="text"  id="nuevoImporteRetEdit" name="nuevoImporteRetEdit" class="form-control"/>
						</div>
					</div>
				</div>
	       </div>
	      <div class="modal-footer">
	        <button type="button" id="btnActualizaRet" onclick="actualizaRetencion();" class="btn btn-primary">Aceptar</button>
	        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
	      </div>
    	</div>
  	</div>
</div>

<div id="resumenRetencionDiv" style="display:none">
	<fieldset>
		<legend>Retenciones</legend>
		<table id="resumenRetencion" align="center">	
			<thead>
				<tr>
					<th>Retenci&oacute;n</th>
					<th>Importe Base</th>
					<th>Porcentaje</th>
					<th>Importe Retencion</th>
				</tr>
			</thead>
			<tbody></tbody>
		</table>
	</fieldset>
</div>