<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<table width="100%">
	<tr>
		<td id="tblFirmantesTD" align="center">
			<fieldset>
				<legend>Firmantes</legend>
				<table>
					<tr>
						<td align="left">
							<fieldset>
								<legend>Suplencia</legend>
								<table width="100%">
									<tr>
										<td>
											<input type="checkbox" id="DelegatorioVoBo" name="DelegatorioVoBo">
										</td>
										<td>
											<label for="DelegatorioVoBo">Oficio delegatorio Visto Bueno</label>
										</td>
										<td>
											<input type="checkbox" id="DelegatorioAut" name="DelegatorioAut">
										</td>
										<td>
											<label for="DelegatorioAut">Oficio delegatorio Autorizacion</label>
										</td>
									</tr>
								</table>
							</fieldset>
						</td>
					</tr>
					<tr>
						<td>
							<fieldset>
								<legend>Captura</legend>
								<table width="100%">
									<tr>
										<td align="right" width="15%">
											Responsable:
										</td>
										<td align="left" width="35%">
											<input type="text" id="usuarioCaptura" name="usuarioCaptura" size="40">
										</td>
										<td align="right" width="15%">Puesto:</td>
										<td align="left" width="35%">
											<input type="text" id="usuarioCaptura" name="usuarioCaptura" size="40">
										</td>
									</tr>
								</table>
							</fieldset>
						</td>
					</tr>
					<tr>
						<td>
							<fieldset>
								<legend>Visto Bueno</legend>
								<table width="100%">
									<tr>
										<td align="right" width="15%">Responsable:</td>
										<td align="left" width="35%">
											<select style="width: 250px;"></select>
										</td>
										<td align="right">Puesto:</td>
										<td align="left" width="35%">
											<input type="text" id="usuarioCaptura" name="usuarioCaptura" size="40" readonly="readonly">
										</td>
									</tr>
								</table>
							</fieldset>
						</td>
					</tr>
					<tr id="suplenteVoBoTR" style="display: none">
						<td>
							<fieldset>
								<legend>Suplente Visto Bueno:</legend>
								<table width="100%">
									<tr>
										<td align="right" width="15%">Responsable:</td>
										<td align="left" width="35%"><select style="width: 250px;"></select></td>
										<td align="right">Puesto:</td>
										<td align="left" width="35%">
											<input type="text" id="usuarioCaptura"name="usuarioCaptura" size="40" readonly="readonly">
										</td>
									</tr>
									<tr>
										<td align="right" width="15%">Oficio:</td>
										<td align="left"><input type="text" id="usuarioCaptura"
											name="usuarioCaptura" size="25"></td>
										<td align="right">Fecha de Oficio:</td>
										<td align="left" width="35%"><input type="text" id="usuarioCaptura"
											name="usuarioCaptura" size="13"></td>
									</tr>
									<tr>
										<td align="right" width="15%">Tipo de Suplencia:</td>
										<td align="left"><select style="width: 150px;"></select></td>
										<td colspan="2">&nbsp;</td>
									</tr>
								</table>
							</fieldset>
						</td>
					</tr>
					<tr>
						<td>
							<fieldset>
								<legend>Autoriza</legend>
								<table width="100%">
									<tr>
										<td align="right" width="15%">
											Responsable:
										</td>
										<td align="left" width="35%">
											<select style="width: 250px;"></select>
										</td>
										<td align="right" width="15%">
											Puesto:
										</td>
										<td align="left" width="35%">
											<input type="text" id="usuarioCaptura" name="usuarioCaptura" size="40" readonly="readonly">
										</td>
									</tr>
								</table>
							</fieldset>
						</td>
					</tr>

					<tr id="suplenteAutTR" style="display: none">
						<td>
							<fieldset>
								<legend>Suplente Autoriza:</legend>
								<table width="100%">
									<tr>
										<td align="right" width="15%">
											Responsable:
										</td>
										<td align="left"  width="35%">
											<select style="width: 250px;"></select>
										</td>
										<td align="right"  width="15%">
											Puesto:
										</td>
										<td align="left" width="35%">
											<input type="text" id="usuarioCaptura" name="usuarioCaptura" size="40" readonly="readonly">
										</td>
									</tr>
									<tr>
										<td align="right" width="15%">
											Oficio:
										</td>
										<td align="left"  width="35%">
											<input type="text" id="usuarioCaptura" name="usuarioCaptura" size="40">
										</td>
										<td align="right" width="15%">
											Fecha de Oficio:
										</td>
										<td align="left" width="35%">
											<input type="text" id="usuarioCaptura" name="usuarioCaptura" size="40">
										</td>
									</tr>
									<tr>
										<td align="right" width="15%">Tipo de Suplencia:</td>
										<td align="left"  width="35%" ><select style="width: 150px;"></select></td>
										<td colspan="2">&nbsp;</td>
									</tr>
								</table>
							</fieldset>
						</td>
					</tr>
				</table>
			</fieldset>
		</td>
	</tr>
</table>