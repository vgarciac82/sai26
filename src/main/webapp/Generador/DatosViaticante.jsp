<input type="hidden" id="empleadoBusqueda" name="empleadoBusqueda" value="" class="datosViaticante"/>
<div>
	<table>
		<tr id="SeleccionaBeneficiarioTR" style="display: none">
			<td align="left">
				<input type="radio" name="viaticosPropiosRB" id="Propios" value="PROPIO" checked="checked" onclick="cambiaEmpleado()"><label for="Propios">Datos Propios</label>
				<input type="radio" name="viaticosPropiosRB" id="Otro" value="OTRO" onclick="cambiaEmpleado()"><label for="Otro">Otro beneficiario</label>
			</td>
		</tr>
		<tr>
			<td>
				<table>
					<tr>
						<td align="right" >
							Beneficiario:
						</td>
						<td align="left" align="left">
							<input type="text" size="30" readonly="readonly" id="nombreEmpleadoBeneficiario" name="nombreEmpleadoBeneficiario" class="datosViaticante">
						</td>
						<td align="left">
							<input type="text" size="15" readonly="readonly" id="aPaternoEmpleadoBeneficiario" name="aPaternoEmpleadoBeneficiario" class="datosViaticante">
						</td>
						<td align="left">
							<input type="text" size="15" readonly="readonly" id="aMaternoEmpleadoBeneficiario" name="aMaternoEmpleadoBeneficiario" class="datosViaticante">
						</td>
						<td align="right">
							Unidad Ejecutora:
						</td>
						<td align="left">
							<input type="text" size="3" readonly="readonly" id="UEEmpleadoBeneficiario" name="UEEmpleadoBeneficiario" class="datosViaticante">
							&nbsp;
							<input type="text" size="35" readonly="readonly" id="DescUEEmpleadoBeneficiario" class="datosViaticante">
						</td>
					</tr>
					<tr>
						<td align="left" colspan="6">
							No. Empleado:	
							<input type="text" size="8" readonly="readonly" id="numEmpleadoBeneficiario" name="numEmpleadoBeneficiario" class="datosViaticante" onchange="buscaEmpleado()">
							Plaza:	
							<input type="text" size="45" readonly="readonly" id="plazaEmpleadoBeneficiario" name="plazaEmpleadoBeneficiario" class="datosViaticante">
							Nivel:
							<input type="text" size="8" readonly="readonly" id="nivelEmpleadoBeneficiario" name="nivelEmpleadoBeneficiario" class="datosViaticante">
							<input type="hidden" id="d_email" name="d_email" value="" class="datosViaticante"/>
							Cuenta Empleado: 
							<input type="text" size="20" readonly="readonly" id="dCuentaEmpleado" name="dCuentaEmpleado" class="datosViaticante">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</div><br>


