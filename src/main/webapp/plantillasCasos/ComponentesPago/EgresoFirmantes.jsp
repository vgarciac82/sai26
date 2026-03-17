<input type="hidden" id="nNumEmpleadoBusqueda" name="nNumEmpleadoBusqueda">
<input type="hidden" id="cPuestoEmpleado" name="cPuestoEmpleado">
<input type="hidden" id="tipoFirmanteConsulta" name="tipoFirmanteConsulta">
<input type="hidden" id="tieneSuplenteVoBo" name="tieneSuplenteVoBo">
<input type="hidden" id="tieneSuplenteAut" name="tieneSuplenteAut">
<input type="hidden" id="esSuplente" name="esSuplente">
<input type="hidden" id="cEsFirmaElectronica" name="esFirmaElectronica">
<input type="hidden" id="estatusPago" name="estatusPago">
<div id="seleccionFirmantes">
	<table align="center" >
		<tr id="trOpcionesFirmante">
			<td align="center" colspan="10">
				<table id="opcionesTbl">
					<tr>
						<td colspan="6" align="left"><b>Opciones</b></td>
					</tr>
					<tr>
						<td align="right">
							<input type="checkbox" id="firmaElectronica" name="firmaElectronica" class="firmaElectronica">
						</td>
						<td align="left" class="firmaElectronica">
							<label for="firmaElectronica" id="firmaElectronicaLbl">Firma Electr&oacute;nica</label>
						</td>
						<td align="right">
							<input type="checkbox" id="VoBoSuplencia"  name="VoBoSuplencia" onclick="return changeSuplencia('VoBoSuplencia')">
						</td>
						<td align="left">
							<label id="VoBoSuplenciaLbl" for="VoBoSuplencia">Oficio Delegatorio VoBo</label>
						</td>
						<td align="right">
							<input type="checkbox" id="AutSuplencia"  name="AutSuplencia" onclick="changeSuplencia('AutSuplencia')">
						</td>
						<td align="left">
							<label for="AutSuplencia"> Oficio  Delegatorio Autoriza</label>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" colspan="2">
				<b>Elabor&oacute;</b>
			</td>
			<td align="center" colspan="2">
				<b>Visto Bueno</b>
			</td>
			
			<td align="center" colspan="2" class="">
				<b>Autoriza</b>
			</td>
		</tr>
		<tr>
			<td align="right">Nombre:</td>
			<td align="left">
				<input type="text" id="nombreElabora" readonly="readonly" size="58">
			</td>
			<td align="right">Nombre:</td>
			<td align="left">
				<SELECT id="nombreVoBo"  name="nombreVoBo" style="width: 378px" onchange="informacionFirmante('VOBO')">
					<option id="">Seleccione Firmante Visto Bueno</option>
				</SELECT>
			</td>
			<td align="right">Nombre:</td>
			<td align="left">
				<SELECT id="nombreAut" name="nombreAut" style="width: 378px" onchange="informacionFirmante('AUT')">
					<option id="">Seleccione Firmante Autoriza</option>
				</SELECT>
			</td>
		</tr>
		<tr>
			<td align="right">Puesto:</td>
			<td align="left">
				<input type="text" id="puestoElabora" readonly="readonly" size="58">
			</td>
			<td align="right">Puesto:</td>
			<td align="left">
				<input type="text" id="puestoVOBO" name="puestoVOBO"readonly="readonly" size="50">
			</td>
			<td align="right">Puesto:</td>
			<td align="left">
				<input type="text" id="puestoAUT" name="puestoAUT" readonly="readonly" size="50">
			</td>
		</tr>
		
		<tr>
			<td colspan="2">
				&nbsp;
			</td>
			<td align="center" colspan="2" class="VoBoSuplencia">
				<b>Visto Bueno en Suplencia</b>
			</td>
			<td align="center" colspan="2" class="AutSuplencia">
				<b>Autoriza en Suplencia</b>
			</td>
		</tr>
		
		<tr>
			<td colspan="2">
				&nbsp;
			</td>
			<td align="right" class="VoBoSuplencia">No. de Oficio:</td>
			<td align="left" class="VoBoSuplencia">
				<input type="text" id="noOficioVoBo" name="noOficioVoBo">
			</td>
			<td align="right" class="AutSuplencia">No. de Oficio:</td>
			<td align="left" class="AutSuplencia">
				<input type="text" id="noOficioAut" name="noOficioAut">
			</td>
		</tr>
		<tr>
			<td colspan="2">
				&nbsp;
			</td>
			<td align="right" class="VoBoSuplencia">Fecha:</td>
			<td align="left" class="VoBoSuplencia">
				<input type="text" id="fechaOficioVobo" name="fechaOficioVobo" size="13" class="fecha" readonly="readonly">
			</td>
			<td align="right" class="AutSuplencia">Fecha:</td>
			<td align="left" class="AutSuplencia">
				<input type="text" id="fechaOficioAut" name="fechaOficioAut" size="13" class="fecha" readonly="readonly">
			</td>
		</tr>
		<tr>
			<td colspan="2">
				&nbsp;
			</td>
			<td align="right" class="VoBoSuplencia">Motivo:</td>
			<td align="left" class="VoBoSuplencia">
				<select id="VoBoSuplenciaMotivo" name="VoBoSuplenciaMotivo"  style="width: 280px">
					<option value="">Seleccione el Motivo</option>
				</select>
			</td>
			<td align="right" class="AutSuplencia">Motivo:</td>
			<td align="left" class="AutSuplencia">
				<select id="AutSuplenciaMotivo" name="AutSuplenciaMotivo"  style="width: 280px">
					<option value="">Seleccione el Motivo</option>
				</select>
			</td>
		</tr>
		
		<tr>
			<td colspan="2">
				&nbsp;
			</td>
			<td align="right" class="VoBoSuplencia">Nombre:</td>
			<td align="left" class="VoBoSuplencia">
				<SELECT id="nombreVOBOSuplente" name="nombreVOBOSuplente" style="width: 378px" onchange="informacionFirmante('SUPVOBO')">
					<option id="">Seleccione Suplente Vo Bo</option>
				</SELECT>
			</td>
			<td align="right" class="AutSuplencia">Nombre:</td>
			<td align="left" class="AutSuplencia">
				<SELECT id="nombreAUTSuplente" name="nombreAUTSuplente"  style="width: 378px" onchange="informacionFirmante('SUPAUT')">
					<option id="">Seleccione Suplente Autoriza</option>
				</SELECT>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				&nbsp;
			</td>
			<td align="right" class="VoBoSuplencia">Puesto:</td>
			<td align="left" class="VoBoSuplencia">
				<input type="text" id="puestoSUPVOBO" name="puestoSUPVOBO"  readonly="readonly" size="50">
			</td>
			<td align="right" class="AutSuplencia">Puesto:</td>
			<td align="left" class="AutSuplencia">
				<input type="text" id="puestoSUPAUT" name="puestoSUPAUT" readonly="readonly" size="50">
			</td>
		</tr>
	</table>
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