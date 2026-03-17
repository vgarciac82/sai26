<input type="hidden" name="TIPO_CONCEPTO" id="TIPO_CONCEPTO" />
<input type="hidden" name="cTipoRfc" id="cTipoRfc" value="" />
<input type="hidden" name="rfc" id="rfc" />
<input type="hidden" name="tieneCedular" id="tieneCedular" />
<input type="hidden" name="campoRFC" id="campoRFC" />
<input type="hidden" name="esExtranjero" id="esExtranjero" />
<input type="hidden" name="diferenciaCFDIPago" id="diferenciaCFDIPago" />
<input type="hidden" name="mImporteRetencion5" id="mImporteRetencion5" />
<input type="hidden" name="UUID" id="UUID" />
<input type="hidden" name="cRegimenFiscal" id="cRegimenFiscal" value = "0" />
<input type="hidden" name="correo" id="correo" />
<input type="hidden" name="nCorreo" id="nCorreo" />
<input type="hidden" name="paternoCorreo" id="paternoCorreo" />
<input type="hidden" name="maternoCorreo" id="maternoCorreo" />

<div id="capturaConceptoPago" class="container" style="width: 98%">
	<div id="generalesPago">
		<div id="generalesPagoTbl">
			<h1>Informaci&oacute;n General</h1>
			
			<div class="row">					
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-2">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-2">													
					<label for="catIdTipoOperacion" class="form-label"> Tipo de Pago Directo </label>
					<select id="catIdTipoOperacion" class="paso01 form-select form-select-sm" name="catIdTipoOperacion">
						<option value="0">Seleccione Tipo de Pago</option>
					</select>											
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-2">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-2">									
					<label for="DESTINO_GASTO" class="form-label"> Tipo Destino </label>
					<select class="paso01 form-select form-select-sm" id="DESTINO_GASTO" name="DESTINO_GASTO" >
						<option value="-1">Seleccione Tipo Destino</option>
					</select>						
				</div>
				
				<div class="row">					
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-2">
					</div>
					<div class="col-12 col-lg-9 col-md-9 col-sm-12 p-2">													
						<label for="concepto" class="form-label"> Concepto </label>
						<textarea name="concepto" readonly="readonly" id="concepto" rows="3" cols="120" onkeypress="valFmt(this,15)" class="form-control form-control-sm"></textarea>															
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					</div>
				</div>	
				
			</div>
			
		</div>
	</div>
	
	<div id="infoBeneficiario">		
		<h1>Beneficiario</h1>
		
		<div class="row">													
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-2">													
				<label for="cIDRFC" class="form-label"> R.F.C </label>
				<div class="input-group">
					<input readonly="readonly" type="text" name="cIDRFC" id="cIDRFC" onchange="cargaCtaBancariaRFC();validaMontoPartidaUMAChange()" class="form-control form-control-sm"/> 
				</div>									
			</div>
			<div class="col-12 col-lg-6 col-md-6 col-sm-12">
				<label for="cnombre" class="form-label"> Nombre/Raz&oacute;n Social </label>
				<input readonly="readonly" type="text" name="cnombre" class="paso01 form-control form-control-sm" ID="cnombre" />
			</div>
			<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-2">
				<label for="ctaBancaria" class="form-label"> Cuenta Bancaria </label>
				<select id="ctaBancaria" name="ctaBancaria" class="form-select form-select-sm">
				</select>
			</div>
		</div>
			
	</div>
	
	<br/>
	
	<div id="listaFacturas">	
		<h1>Facturas</h1>
		
		<div class="row">													
			<div class="col-12 col-lg-2 col-md-2 col-sm-12">													
				<input type="button" value="Cargar Facturas" id="cargaFacturasBtn" class="btn btn-secondary sm"/>
			</div>											
			<div class="col-12 col-lg-2 col-md-2 col-sm-12">				
				<input type="button" value="Calcular Impuesto" id="calcularBtn" class="btn btn-secondary sm" onclick="calcularImpuesto()"/>
			</div>
			<div class="col-12 col-lg-2 col-md-2 col-sm-12">
				<input type="button" value="Capturar correo" id="capturaCorreo" class="btn btn-secondary sm" onclick="creaDialogCorreo(true)"/>
			</div>
			<div class="col-12 col-lg-4 col-md-4 col-sm-12">
			</div>
			<div class="col-12 col-lg-2 col-md-2 col-sm-12">
				<label for="mTotalFacturaV" class="form-label"> Total en facturas: </label>
				<div class="input-group">
					<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
					<input type="text" style="text-align: right;" name="mTotalFacturaV" id="mTotalFacturaV" value="0.00" size="15" readonly="readonly" class="form-control form-control-sm"/>
				</div>
			</div>
		</div>
		
		<br/>
		
		<div class="table-responsive">
			<table id="grdValidaFacturas" class="table table-striped">			
				<thead>
					<tr>
						<th>Factura</th>
						<th>Importe Bruto</th>
						<th>IVA</th>
						<th>Otros Impuestos</th>
						<th>Descuentos</th>
						<th>Retenciones</th>
						<th>Total</th>
					</tr>
				</thead>
			</table>
		</div>
		
	</div>
</div>

<div id="resumenCaratula" style="display: none">
	<div id="resumenConceptoDiv">
		<table id="generalesPagoResumen" width="100%" >
			<tr>
				<td align="center">
					<fieldset>
						<legend>Concepto del Pago</legend>
						<table align="center"  class="resumenTbl">
							<tr>
								<td class="resumenEncabezado" style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">Tipo de Pago Directo</td>
								<td align="left" id="idTipoDocumentoLbl" class="infoResumen" style="border: 1px solid rgb(190,190,190); padding: 10px 20px;"> &nbsp;</td>
							</tr>
							<tr>
								<td  class="resumenEncabezado" style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">Tipo Destino</td>
								<td align="left" id="idDestinoGastoLbl" class="infoResumen"style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">&nbsp;</td>
							</tr>
							<tr>
								<td  class="resumenEncabezado" style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">Concepto:</td>
								<td align="left" colspan="2" style="border: 1px solid rgb(190,190,190); padding: 10px 20px;"><p id="conceptoLbl" class="infoResumen">&nbsp;</p></td>
							</tr>
							<tr>
								<td class="resumenEncabezado" colspan="2" style="border: 1px solid rgb(190,190,190); padding: 10px 20px; text-align: center">Beneficiario</td>
							</tr>
							<tr>
								<td  class="resumenEncabezado"  style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">RFC - Nombre/Razon Social</td>
								<td align="left"   id="rfcLbl" class="infoResumen"style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">&nbsp;</td>
							</tr>
							<tr>
								
								<td class="resumenEncabezado"  style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">Cuenta Bancaria</td>
								<td align="left" id="CTABLbl" class="infoResumen"style="border: 1px solid rgb(190,190,190); padding: 10px 20px;">&nbsp;</td>
							</tr>
							<tr>
								<td colspan="2" class="resumenEncabezado" style="border: 1px solid rgb(190,190,190); padding: 10px 20px; text-align: center;">
									Facturas
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<table id="grdResumenFacturas" class="display">
										<thead>
											<tr>
												<th>Factura</th>
												<th>Importe Bruto</th>
												<th>IVA</th>
												<th>Otros Impuestos</th>
												<th>Descuentos</th>
												<th>Retenciones</th>
												<th>Total</th>
											</tr>
										</thead>
									</table>
								</td>
							</tr>
						</table>
					</fieldset>
				</td>
			</tr>
		</table>
	</div>
</div>
<!-- DIV Para capturar correo -->
		<div id="dialog-actualizaCorreo" title="Actualiza correo del proveedor">
			<div id="actualizaCorreoDiv">
				<p>Favor de capturar los siguientes datos para seguimiento del PPD:</p>
					<div class="row">
						<div class="col-12">
							<label for="nombreCorreo">Nombre</label>
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-person"></i></span>
								<input type="text" id="nombreCorreo" name="nombreCorreo" placeholder="Nombre"   class="form-control"/>
								<input type="text" id="aPCorreo" name="aPCorreo" placeholder="Apellido Paterno" class="form-control"/>
								<input type="text" id="aMCorreo" name="aMCorreo" placeholder="Apellido Materno" class="form-control"/>
								
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-12">
							<label for="cargo">Cargo</label>
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
								<input type="text" id="cargo" name="cargo" class="form-control" placeholder="Cargo"/>
							</div>	
						</div>
					</div>
					<div class="row">
						<div class="col-12">
						<label class="sr-only" for="correoActual">Correo</label>
						<div class="input-group">
					        <div class="input-group-prepend">
					          <div class="input-group-text">@</div>
					        </div>
					        <input type="text" class="form-control" id="correoActual" name="correoActual" placeholder="Correo electr�nico"/>
					      </div>
						</div>		
					</div>	
			</div>
		</div>
