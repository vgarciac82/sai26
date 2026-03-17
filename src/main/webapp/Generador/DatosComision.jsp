<style type="text/css">
.centerCls {
	text-align: center;
}

.rightCls {
	text-align: right;
}

.leftCls {
	text-align: left;
}
</style>

<div>
	<input type="hidden" id="pais" name="pais">
	<input type="hidden" id="estado" name="estado">
	<input type="hidden" id="municipio" name="municipio">
	<input type="hidden" id="mImporteMaximo" name="mImporteMaximo" value="0" >
	<input type="hidden" id="esImpteAbierto" name="esImpteAbierto" value="0" >
	<input type="hidden" id="nIdHomologacion" name="nIdHomologacion" value="3" >
	<input type="hidden" id="cTipoMoneda" name="cTipoMoneda" value="" >
	
	<input type="hidden" id="lEsAnticipado" name="lEsAnticipado" value="">
	<input type="hidden" id="lEsNacional" name="lEsNacional" value="">
	<input type="hidden" id="nIdPais" name="nIdPais" value="">
	<input type="hidden" id="ID_ESTADO" name="ID_ESTADO" value="">
	<input type="hidden" id="ID_MUNICIPIO" name="ID_MUNICIPIO" value="">
	<input type="hidden" id="nIDPaquete" name="nIDPaquete" value="">
	<input type="hidden" id="nIDHomologacion" name="nIDHomologacion" value="">
	<input type="hidden" id="cIDTipoMoneda" name="cIDTipoMoneda" value="">
	<input type="hidden" id="mCuotaPorDia" name="mCuotaPorDia" value="0" >
	<input type="hidden" id="cActividadesAgenda" name="cActividadesAgenda" value="" >
	<input type="hidden" id="nDocRenglon" name="nDocRenglon" value="0" >
	<input type="hidden" id="existeFAgenda" name="existeFAgenda" value="0" >
	<input type="hidden" id="existeFinSemana" name="existeFinSemana" value="0" >
	<input type="hidden" id="esAnticipado" name="esAnticipado" value="" >
	<input type="hidden" id="existeDet" name="existeDet" value="0" >		
		
	<fieldset>
		<table>
			<tr>
				<td align="right">Fecha Inicio:</td>
				<td align="left"><input type="text" id="fInicio" name="fInicio" size="15"></td>
				<td nowrap>&nbsp;</td>
				<td align="right">Fecha Fin:</td>
				<td align="left"><input type="text" id="fFin" name="fFin" size="15"></td>
				<td nowrap>&nbsp;</td>
				<td nowrap>&nbsp;</td>
				<td align="right"><input type="radio" id="rdoAnticipado" name="tipoViatico" value="Anticipado" checked="checked"> <label for="rdoAnticipado"> Anticipado </label></td>
				<td nowrap>&nbsp;</td>
				<td align="right"><input type="radio" id="rdoDevengado" name="tipoViatico" value="Devengado"> <label for="rdoDevengado"> Devengado </label></td>
			</tr>
		</table>

		<table>
			<tr>
				<td><input type="radio" id="rdoNacional" name="alcanseViatico" value="Nacional" checked="checked"> <label for="rdoNacional"> Nacional </label></td>
				<td nowrap>&nbsp;</td>
				<td><input type="radio" id="rdoInternacional" name="alcanseViatico" value="Internacional"> <label id="rdoInternacional"> Internacional </label></td>
			</tr>
		</table>
		<table>
			<tr>
				<td align="left"><label id="lblpais">País:</label></td>
				<td align="left"><label id="lblestado">Estado:</label></td>
			</tr>
			<tr>
				<td><select name="cboPais" id="cboPais" onchange="cargaEstados(), cargaMunicipios()"></select></td>
				<td><select name="cboEstado" id="cboEstado" onchange="cargaMunicipios()"></select></td>
			</tr>
			<tr>
				<td align="left"><label id="municipiolabel">Municipio:</label></td>
				<td align="left">Localidad:</td>
			</tr>
			<tr>
				<td><select name="cboMunicipio" id="cboMunicipio"></select></td>
				<td align="left"><input type="text" id="cLocalidad" name="cLocalidad" size="50"></td>
			</tr>
		</table>
	</fieldset>
	<fieldset>
		<legend>Motivo</legend>
		<table>
			<tr>
				<td align="center"><input type="text" id="cMotivo" name="cMotivo" size="123"></td>
			</tr>
			<tr>
				<td align="left"><label id="lblActAgenda">Actividades de Agenda</label></td>				
			</tr>
			<tr>
				<td>
					<textarea id="cActAgenda" name="cActAgenda" cols="47" rows="4"></textarea>
				</td>
			</tr>
		</table>
		<table>
			<tr>								
				<td align="left"><label id="lblPaquete">Paquete</label></td>
				<td><input type="checkbox" id="chkHomologacion" name="chkHomologacion" > <label for="chkHomologacion"> Homologación </label></td>
				<td nowrap>&nbsp;</td>
				<td align="left"><label id="lblcuota">Tipo Moneda</label></td>
				<td nowrap>&nbsp;</td>
				<td align="left"><label id="lblcuota">Cuota x Dia</label></td>								
			</tr>
			<tr>								
				<td><select name="cboPaquete" id="cboPaquete"></select></td>
				<td><select name="cboHomologacion" id="cboHomologacion"></select></td>
				<td nowrap>&nbsp;</td>
				<td><select name="cboTipoMoneda" id="cboTipoMoneda"></select></td>
				<td nowrap>&nbsp;</td>
				<td align="right"><input type="text" id="mCuotaDia" name="mCuotaDia" size="12" style="text-align: right" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);"></td>
				<td nowrap>&nbsp;</td>
				<td>
					<input type="button" id="btnAgregar" name="btnAgregar" onclick="guardaComisionAgenda()" value="Agregar" >
				</td>
			</tr>
		</table>
	</fieldset>
	<div>
		<fieldset>
			<legend>Agenda</legend>
			<table id="dtComisionesDet" class="display">
				<thead>
					<tr>
						<th>Renglon</th>
						<th>F. Inicio</th>
						<th>F. Fin</th>
						<th>Tipo</th>
						<th>Destino</th>
						<th>Motivo</th>
						<th>Tarifa</th>
						<th>Cuota por Dia</th>
						<th>Dias</th>
						<th>Total</th>
						<th>Descartar</th>
					</tr>
				</thead>
			</table>
		</fieldset>
	</div>
</div>