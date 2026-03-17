<%@page import="com.syc.obrapublica.EjercicioFiscal"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	EjercicioFiscal aEjercicioFiscal = efbl.getEjercicioFiscalActivo();
	int nFolioPago = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
	String tipoPago = c.getTipoCaso().getGavetaAsociada();
	String fechaCaptura = Util.getTodayESMX();
	String fechaAplicacion = Util.calculaFechaAplicacion( aEjercicioFiscal );
%>
	<table width="100%">
		<tr>
			<td id="tblGeneralTD" align="center">
				<fieldset>
					<legend>
						Encabezado
					</legend>
								
					<table>
						<tr>
							<td align="right">
								Contrarecibo:
							</td>
							<td align="left">
								<input readonly="readonly" type="text" name="caNoContrarrecibo" id="caNoContrarrecibo" size="17" />
							</td>
							<td align="right">
								Folio:
							</td>
							<td align="left">
								<input readonly="readonly" type="text" name="nFolio<%=tipoPago%>" id="nFolio<%=tipoPago%>" size="6" value="<%=nFolioPago%>" class="numeric"/>
							</td>
							<td align="right">
								Fecha de Captura:
							</td>
							<td align="left">
								<input readonly="readonly" type="text" name="fechaCaptura" id="fechaCaptura" size="12" value="<%=fechaCaptura%>" />
							</td>
							<td align="right">
								Fecha de Aplicaci&oacute;n:
							</td>
							<td align="left">
								<input readonly="readonly" type="text" name="fechaAplicacion" id="fechaAplicacion" size="12"  value="<%=fechaAplicacion%>">
							</td>
						</tr>
					</table>
				</fieldset>	
			</td>
		</tr>
		<tr>
			<td id="tblContratoTD" align="center">
				<fieldset>
					<legend>Contrato</legend>
					<table id="contratoTbl">
						<tr>
							<td align="right" colspan="1">
								Numero de Contrato:
							</td>
							<td align="left" colspan="3">
								<input type="text" id="numeroContrato" name="numeroContrato" size="30" readonly="readonly" class="AyudaSyC"/>
							</td>
							<td colspan="2">
								&nbsp;
							</td>
						</tr>
						<tr>
							<td align="right">
								Beneficiario: 
							</td>
							<td align="left" colspan="3">
								<input type="text" id="dRFC" size="15" readonly="readonly" class="informativo"/>
								<input type="text" id="cRazonSocial" size="35" readonly="readonly" class="informativo"/> 
							</td>
							<td colspan="2">
								&nbsp;
							</td>
						</tr>
						<tr>
							<td align="right">
								Fecha de Inicio: 
							</td>
							<td align="left">
								<input type="text" id="fInicio" size="11" readonly="readonly" class="informativo"/>
							</td>
							<td align="right">
								Fecha de Termino: 
							</td>
							<td align="left">
								<input type="text" id="fFin" name="fFin" size="11" readonly="readonly" class="informativo"/>
							</td>
							<td colspan="2">
								&nbsp;
							</td>
						</tr>
						<tr>
							<td align="right">
								Monto Bruto:
							</td>
							<td align="left">
								<input type="text" class="decimal informativo" id="montoBrutoCnt" size="18" readonly="readonly">
							</td>
							<td align="right">
								Monto Impuestos:
							</td>
							<td align="left">
								<input type="text" class="decimal informativo" id="montoImpuestosCnt" size="18" readonly="readonly">
							</td>
							<td align="right">
								Monto Total:
							</td>
							<td align="left">
								<input type="text" class="decimal informativo" id="montoTotalCnt" size="18" readonly="readonly">
							</td>
						</tr>
						<tr>
							<td colspan="6" align="left">
								Concepto:
							</td>
						</tr>
						<tr>
							<td colspan="6" align="left">
								<textarea rows="5" cols="80" readonly="readonly" id="conceptoCnt" class="informativo"></textarea>
							</td>
						</tr>
						
					</table>
				</fieldset>
			</td>
		</tr>
		<tr>
			<td align="center">
				<fieldset>
					<legend>Car&aacute;tula</legend>
					<table>
						<tr>
							<td align="right">
								Recepcion de material:
							</td>
							<td align="left">
								<select id="cIdRecepMat" name="cIdRecepMat" style="width: 280px;" class="catalogoDependiente">
									<option value="">Seleccione la recepcion de material</option>
								</select>
							</td>
							<td align="right">
								Operacion:
							</td>
							<td align="left">
								<select id="TIPO_OPERACION" name="TIPO_OPERACION" style="width: 280px;" class="catalogoIndependiente">
									<option value="">Seleccione el tipo de Operacion</option>
								</select>
							</td>
						</tr>
						<tr>
							<td align="right">
								Destino del Gasto:
							</td>
							<td align="left">
								<select id="destGasto" name="destGasto" style="width: 280px;" class="catalogoIndependiente">
									<option value="">Seleccione el destino de gasto</option>
								</select>
							</td>
							<td align="right">
								Concepto:
							</td>
							<td align="left">
								<select id="TIPO_CONCEPTO" name="TIPO_CONCEPTO" style="width: 280px;" class="catalogoDependiente">
									<option value="">Seleccione el concepto</option>
								</select>
							</td>
						</tr>
						<tr>
							<td align="right">
								Tipo de Movimiento:
							</td>
							<td align="left">
								<select id="TIPO_MOVIMIENTO" name="TIPO_MOVIMIENTO" style="width: 280px;" class="catalogoDependiente">
									<option value="">Seleccione el tipo de movimiento</option>
								</select>
							</td>
							<td align="right">
								Almacen:
							</td>
							<td align="left">
								<select id="ALM" name="ALM" style="width: 280px;"  class="catalogoIndependiente">
									<option value="">Seleccione el almacen</option>
								</select>
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
	</table>
