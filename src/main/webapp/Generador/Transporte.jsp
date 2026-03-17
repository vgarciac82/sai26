<div>
	<input type="hidden" id="tipo" name="tipo">
	<input type="hidden" id="mMonto" name="mMonto">
		<div>
	
			<table>
				<tr>
					<td>Tipo
					<td><select id="tipoTransporte" name="tipoTransporte"
						onchange="cargaTransporte(), cambiaTransporte()"></select></td>
				</tr>
				<tr>
					<td>Origen - Destino:</td>
					<td><input type="text" name="Origen" id="Origen" value=""
						maxlength="100" size="100" /></td>
				</tr>
				<tr>
					<td>Monto:</td>
					<td><input type="text" name="monto" id="monto" value=""
						maxlength="10" size="10" /></td>
				</tr>
			</table>
		</div>
		
		<div id="dlgKm" title="dlgKm" style="display:none">
			<table>
				<tr>
					<td>No. de Km a Recorrer:</td>
					<td>	
						<input type="text" name="km" id="km" value="" maxlength="5" size="5" />
					</td>
				</tr>
			</table>
		</div>

		<div id="dlgDetalle" title="Detalle" style="display: none;">
			<table>
				<tr>
					<td>N&uacute;mero Economico:</td>
					<td><input type="text" name="placas" id="placas" value=""
						maxlength="10" size="10" /></td>
				</tr>
				<tr>
					<td>¿Vales de gasolina?:</td>
					<td><input type="checkbox" name="gasolina" id="gasolina"
						value="" maxlength="10" size="10" /></td>
				</tr>
	
			</table>
		</div>


	<div>
		<fieldset>
			<table>
				<tr>
					<td>
						<input type="button" value="Agregar" id="guardaTransporte">
					</td>
				</tr>
			</table>

			<legend>Transporte cargado</legend>
			<table id="dtTransporteDet" width="50%">
				<thead>
					<tr>
						<th>Nfolio</th>
						<th>Tipo transporte</th>
						<th>OrigenDestino</th>
						<th>Monto</th>
						<th>Descartar</th>
					</tr>
				</thead>
			</table>
		</fieldset>
	</div>
</div>