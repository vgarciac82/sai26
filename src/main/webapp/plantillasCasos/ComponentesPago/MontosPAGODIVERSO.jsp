
<input type="hidden" id="isFactAmort" value="0">
<table width="100%">
	<tr>
		<td id="tblMontosTD" align="center">
			<fieldset>
				<legend>Montos de Factura</legend>
				<table>
					<tr>
						<td align="right">Importe Bruto:</td>
						<td>&nbsp;</td>
						<td align="left"><input type="text" size="20" class="decimal montoRecepcion" id="mBruto" readonly="readonly"></td>
					</tr>
					<tr>
						<td align="right">Importe Amortizacion:</td>
						<td align="center">-</td>
						<td align="left"><input type="text" size="20" class="decimal montoRecepcion" id="mAmortizacion" name="mAmortizacion" readonly="readonly" onfocus="return onFocusMoney('mAmortizacion');" onblur="return onBlurMoney( 'mAmortizacion' );"></td>
					</tr>
					<tr>
						<td align="right">Importe Penalizacion:</td>
						<td align="center">-</td>
						<td align="left"><input type="text" size="20" class="decimal montoRecepcion" id="mPenas" name="mPenas"  onfocus="return onFocusMoney('mPenas');" onblur="return onBlurMoney( 'mPenas' );"></td>
					</tr>
					<tr>
						<td align="right">Importe Descuento:</td>
						<td align="center">-</td>
						<td align="left"><input type="text" size="20" class="decimal montoRecepcion" id="mDescuentos" name="mDescuentos" onfocus="return onFocusMoney('mDescuentos');" onblur="return onBlurMoney( 'mDescuentos' );"></td>
					</tr>
					<tr>
						<td align="right">Subtotal:</td>
						<td align="center">=</td>
						<td align="left"><input type="text" size="20" class="decimal montoRecepcion" id="mSubtotal" readonly="readonly"></td>
					</tr>
					<tr>
						<td align="right">I.V.A:</td>
						<td align="center">+</td>
						<td align="left"><input type="text" size="20" class="decimal montoRecepcion" id="mIVA" readonly="readonly"></td>
					</tr>
					<tr>
						<td align="right">Otros Impuestos:</td>
						<td align="center">+</td>
						<td align="left"><input type="text" size="20" class="decimal montoRecepcion" id="mOtrosImpuestos" readonly="readonly"></td>
					</tr>
					<tr>
						<td align="right">Retenciones:</td>
						<td align="center">-</td>
						<td align="left"><input type="text" size="20" class="decimal montoRecepcion" id="mRetenciones" readonly="readonly"></td>
					</tr>
					<tr>
						<td align="right">Importe Neto:</td>
						<td align="center">=</td>
						<td align="left"><input type="text" size="20" class="decimal montoRecepcion" id="mImporteNeto" readonly="readonly"></td>
					</tr>
					<tr>
						<td align="right">Total Egreso:</td>
						<td align="center">=</td>
						<td align="left"><input type="text" size="20" class="decimal montoRecepcion" id="mImporteMasIVA" readonly="readonly"></td>
					</tr>
				</table>
			</fieldset>
		</td>
	</tr>
</table>