<input type="hidden" id="nNumEmpleadoBusqueda" name="nNumEmpleadoBusqueda">
<input type="hidden" id="cPuestoEmpleado" name="cPuestoEmpleado">
<input type="hidden" id="tipoFirmanteConsulta" name="tipoFirmanteConsulta">
<input type="hidden" id="tieneSuplenteVoBo" name="tieneSuplenteVoBo">
<input type="hidden" id="tieneSuplenteAut" name="tieneSuplenteAut">
<input type="hidden" id="esSuplente" name="esSuplente">
<input type="hidden" id="cEsFirmaElectronica" name="esFirmaElectronica">
<input type="hidden" id="estatusPago" name="estatusPago">
<div id="seleccionFirmantes">
	<div class="row">
		<div class="col-4">
			<input type="checkbox" id="firmaElectronica" name="firmaElectronica" class="form-check-input firmaElectronica"/>
			<label for="firmaElectronica" id="firmaElectronicaLbl">Firma Electr&oacute;nica</label>
		</div>
		<div class="col-4">
			<input type="checkbox" id="VoBoSuplencia"  name="VoBoSuplencia" onclick="return changeSuplencia('VoBoSuplencia')" class="form-check-input"/>
			<label id="VoBoSuplenciaLbl" for="VoBoSuplencia">Oficio Delegatorio VoBo</label>
		</div>
		<div class="col-4">
			<input type="checkbox" id="AutSuplencia"  name="AutSuplencia" onclick="changeSuplencia('AutSuplencia')" class="form-check-input"/>
			<label for="AutSuplencia"> Oficio  Delegatorio Autoriza</label>
		</div>
	</div>
	
	<div class="row">
		<div class="col-4">
			<b>Elabor&oacute;</b>
		</div>
		<div class="col-4">
			<b>Visto Bueno</b>
		</div>
		<div class="col-4">
			<b>Autoriza</b>
		</div>
	</div>
	<div class="row">
		<div class="col-4">
			Nombre:
			<input type="text" id="nombreElabora" readonly size="58" class="form-control"/>
		</div>
		<div class="col-4">
			Nombre Visto Bueno:
			<SELECT id="nombreVoBo"  name="nombreVoBo" onchange="informacionFirmante('VOBO')" class="form-select">
					<option id="">Seleccione Firmante Visto Bueno</option>
				</SELECT>
		</div>
		<div class="col-4">
			Nombre Autoriza:
			<SELECT id="nombreAut" name="nombreAut" onchange="informacionFirmante('AUT')" class="form-select">
					<option id="">Seleccione Firmante Autoriza</option>
				</SELECT>
		</div>
	</div>
	<div class="row">
		<div class="col-4">
			Puesto:
			<input type="text" id="puestoElabora" readonly="readonly" size="58" class="form-control">
		</div>
		<div class="col-4">
			Puesto:
			<input type="text" id="puestoVOBO" name="puestoVOBO"readonly="readonly" size="50" class="form-control">
		</div>
		<div class="col-4">
			Puesto:
			<input type="text" id="puestoAUT" name="puestoAUT" readonly="readonly" size="50" class="form-control">
		</div>
	</div>
	<div class="row">
		<div class="col-4">
		</div>
		<div class="col-4 VoBoSuplencia">
			<b>Visto Bueno en Suplencia</b>
		</div>
		<div class="col-4 AutSuplenciaDiv">
		</div>
		<div class="col-4 AutSuplencia">
			<b>Autoriza en Suplencia</b>
		</div>
	</div>
	<div class="row">
		<div class="col-4">
		</div>
		<div class="col-2 VoBoSuplencia">
			No. de Oficio:
			<input type="text" id="noOficioVoBo" name="noOficioVoBo" class="VoBoSuplencia form-control" />
		</div>
		<div class="col-2 VoBoSuplencia">
			Fecha:
			<input type="text" id="fechaOficioVobo" name="fechaOficioVobo" size="13" readonly class="form-control fecha VoBoSuplencia"/>
		</div>
		<div class="col-4 AutSuplenciaDiv">
		</div>
		<div class="col-2 AutSuplencia">
			No. de Oficio:
			<input type="text" id="noOficioAut" name="noOficioAut" class="AutSuplencia form-control"/>
		</div>
		<div class="col-2 AutSuplencia">
			Fecha:
			<input type="text" id="fechaOficioAut" name="fechaOficioAut" size="13" readonly class="form-control fecha AutSuplencia"/>
		</div>
	</div>
	<div class="row">
		<div class="col-4">
		</div>
		<div class="col-2 VoBoSuplencia">
			Motivo:
			<select id="VoBoSuplenciaMotivo" name="VoBoSuplenciaMotivo"  class="VoBoSuplencia form-select" class="form-select">
					<option value="">Seleccione el Motivo</option>
				</select>
		</div>
		<div class="col-2"></div>
		<div class="col-4 AutSuplenciaDiv">
		</div>
		<div class="col-2 AutSuplencia">
			Motivo:
			<select id="AutSuplenciaMotivo" name="AutSuplenciaMotivo"  class="AutSuplencia form-select" class="form-select">
					<option value="">Seleccione el Motivo</option>
				</select>
		</div>
	</div>
	<div class="row">
		<div class="col-4">
		</div>
		<div class="col-4 VoBoSuplencia">
			Nombre:
			<SELECT id="nombreVOBOSuplente" name="nombreVOBOSuplente" onchange="informacionFirmante('SUPVOBO')" class="form-select">
					<option id="">Seleccione Suplente Vo Bo</option>
				</SELECT>
		</div>
		<div class="col-4 AutSuplenciaDiv">
		</div>
		<div class="col-4 AutSuplencia">
			Nombre:
			<SELECT id="nombreAUTSuplente" name="nombreAUTSuplente"  onchange="informacionFirmante('SUPAUT')" class="form-select">
					<option id="">Seleccione Suplente Autoriza</option>
				</SELECT>
		</div>
	</div>
	<div class="row">
		<div class="col-4">
		</div>
		<div class="col-4 VoBoSuplencia">
			Puesto:
			<input type="text" id="puestoSUPVOBO" name="puestoSUPVOBO"  readonly="readonly" size="40" class="form-control"/>
		</div>
		<div class="col-4 AutSuplenciaDiv">
		</div>
		<div class="col-4 AutSuplencia">
			Puesto:
			<input type="text" id="puestoSUPAUT" name="puestoSUPAUT" readonly="readonly" size="40" class="form-control"/>
		</div>
	</div>
</div>
<div id="resumenFirmantes" style="display: none">
	<table align="center" class="tablaResumen BL BR" cellspacing="0" >
			<tr>
				<th class="BL BT firmanteResumenEnc">
					Elabor&oacute;
				</th>
				<th class="BL BT firmanteResumenEnc">
					Visto Bueno
				</th>
				<th class="BL BT BR firmanteResumenEnc">
					Autoriz&oacute;
				</th>
			</tr>
			<tr>
				<td class="BL BR firmanteResumen" >
				</td>
				<td id="nombreEmpleadoSupVoBo" class=" BL BR firmanteResumen VoBoSuplencia">
					&nbsp;
				</td>
				<td id="nombreEmpleadoSupAut" class="BL BR firmanteResumen AutSuplencia">
				</td>
			</tr>
			<tr>
				<td class="BL firmanteResumen">
				</td>
				<td id="puestoEmpleadoSupVoBo" class=" BL BR firmanteResumen VoBoSuplencia">
					&nbsp;
				</td>
				<td id="puestoEmpleadoSupAut" class=" BL BR firmanteResumen AutSuplencia">
				</td>
			</tr>
			<tr>
				<td class=" BL firmanteResumen">
				</td>
				<td id="motivoSuplenciaSupVoBo" class=" BL BR firmanteResumen VoBoSuplencia" style="font-weight: bold;">
					&nbsp;
				</td>
				<td id="motivoSuplenciaSupAut" class=" BL BR firmanteResumen AutSuplencia"  style="font-weight: bold;">
				</td>
			</tr>
			<tr>
				<td id="nombreEmpleadoElabora" class="BL firmanteResumen"></td>
				<td id="nombreEmpleadoVoBo" class=" BL firmanteResumen">
					&nbsp;
				</td>
				<td id="nombreEmpleadoAut" class="BL BR firmanteResumen">
				</td>
			</tr>
			<tr>
				<td id="puestoEmpleadoElabora" class="BB BL firmanteResumen">&nbsp;</td>
				<td id="puestoEmpleadoVoBo" class="BB BL firmanteResumen">
				</td>
				<td id="puestoEmpleadoAut" class="BB BL BR firmanteResumen">
				</td>
			</tr>
	</table>
</div>