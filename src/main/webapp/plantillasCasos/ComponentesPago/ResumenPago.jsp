<input type="hidden" id="questionnaireAnswered" name="questionnaireAnswered" value=""/>
<div id="Resumen">
	<fieldset>
		<legend>Resumen del pago</legend>
		<table id="mainTable" align="center" class="tablaResumen"
			cellspacing="0" width ="65%">
			<tr>
				<td style="padding: 0px;">
					<!-- TABLA General -->
					<table class="tablaResumen" cellpadding="5" width="100%">
						<tr>
							<td colspan="3" width="31%">&nbsp;</td>
							<td class="tituloSuperior infoTDR" width="23%">No. Documento</td>
							<td class="tituloSuperior" width="23%">Tipo de Documento</td>
							<td class="tituloSuperiorDer" width="23%" nowrap>Fecha de
								Aplicacion</td>
						</tr>
						<tr>
							<td colspan="3" width="31%">&nbsp;</td>
							<td class="infoInferior" width="23%"
								id="caNoContrarreciboResumen">&nbsp;</td>
							<td class="infoInferior" width="23%" id="tipoDocumentoResumen">&nbsp;</td>
							<td class="infoInferiorDer" width="23%"
								id="fechaAplicacionResumen">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="3" width="31%">&nbsp;</td>
							<td colspan="1" width="34%" class="tituloSuperior">Folio</td>
							<td colspan="2" width="35%" class="infoSuperiorDer"
								id="destinoGastoResumen" style="text-align: left">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="3" width="31%" nowrap><b>CUENTA POR PAGAR</b></td>
							<td colspan="1" width="34%" class="infoInferior"
								id="nFolioResumen">&nbsp;</td>
							<td colspan="2" width="35%" class="infoInferiorDer"
								id="tipoConceptoResumen" style="text-align: left">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="4" width="65%" nowrap class="tituloSuperior">&nbsp;</td>
							<td colspan="2" width="35%" class="tituloSuperiorDer">Tipo
								de C x P</td>
						</tr>
						<tr>
							<td colspan="2" width="32%" nowrap class="infoInferiorH"
								id="centroContableResumen"
								style="border-left: 1px solid rgb(50, 50, 50);">&nbsp;</td>
							<td colspan="2" width="33%" nowrap class="infoInferiorH"
								id="unidadResponsableResumen">&nbsp;</td>
							<td colspan="2" width="35%" class="infoInferiorDer"
								id="tipoPagoDirectoResumen">&nbsp;</td>
						</tr>
					</table>
				</td>
			</tr>
			<!-- Detalle beneficiario y Montos -->
			<tr>
				<td style="padding: 0px;" class="BL BB BR" width="100%">
					<table class="tablaResumen" width="100%">
						<tr>
							<td width="50%" class="BR">
								<table width="100%" class="tablaResumen" cellpadding="5">
									<tr>
										<td class="tituloResumenR">Importe a Pagar:</td>
										<td colspan="2" id="importePagarResumen" align="left">&nbsp;</td>
									</tr>
									<tr>
										<td colspan="3" id="importeLetraResumen" class="BB">&nbsp;</td>
									</tr>
									<tr>
										<td class="tituloResumenR">A favor de:</td>
										<td colspan="2" align="left" id="rfcResumen"></td>
									</tr>
									<tr>
										<td colspan="3" id="cNombreResumen" class="BB" align="left">&nbsp;</td>
									</tr>
									<tr>
										<td class="BB tituloResumenR">Cuenta Bancaria:</td>
										<td class="BB" id="ctaBanResumen" align="center">&nbsp;</td>
										<td class="BB" id="ctaBanBancoResumen" align="center">&nbsp;</td>
									</tr>
									<tr>
										<td colspan="3" class="tituloResumenR">Por concepto de:</td>
									</tr>
									<tr>
										<td colspan="3" id="conceptoResumen" align="left">&nbsp;</td>
									</tr>
								</table>

							</td>
							<td width="50%" class="BR">
								<table width="100%" class="tablaResumen" cellpadding="5">
									<tr>
										<td class="BB tituloResumenR">Importe Bruto</td>
										<td class="BB" id="mImporteBrutoResumen"></td>
									</tr>
									<tr>
										<td class="BB tituloResumenR">Penalizaciones</td>
										<td class="BB" id="importePenasResumen"></td>
									</tr>
									<tr>
										<td class="BB tituloResumenR">IVA</td>
										<td class="BB" id="importeIVAResumen"></td>
									</tr>
									<tr>
										<td class="BB tituloResumenR">Otros Impuestos</td>
										<td class="BB" id="importeOtrosImpResumen"></td>
									</tr>
									<tr>
										<td class="BB tituloResumenR">Retenciones</td>
										<td class="BB" id="importeRetResumen"></td>
									</tr>
									<tr>
										<td class="tituloResumenR">Importe Neto</td>
										<td id="importeNetoResumen"></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<!-- Resumen Retenciones -->
			<tr>
				<td width="100%" class="tituloResumen BL BR BB">
					R&nbsp;&nbsp;&nbsp;E&nbsp;&nbsp;&nbsp;T&nbsp;&nbsp;&nbsp;E&nbsp;&nbsp;&nbsp;N&nbsp;&nbsp;&nbsp;C&nbsp;&nbsp;&nbsp;I&nbsp;&nbsp;&nbsp;O&nbsp;&nbsp;&nbsp;N&nbsp;&nbsp;&nbsp;E&nbsp;&nbsp;&nbsp;S
				</td>
			</tr>
			<tr>
				<td style="padding: 0px;" class="BL BB BR" width="100%">
					<table width="100%" class="tablaResumen" id="tblResumenPagoR"
						cellpadding="5">
						<thead>
							<tr>
								<th class="tituloResumen BR BB">Retenci&oacute;n</th>
								<th class="tituloResumen BR BB">Importe Base</th>
								<th class="tituloResumen BR BB">Porcentaje</th>
								<th class="tituloResumen BR BB">Importe Retencion</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</td>
			</tr>
			<!-- Resumen Presupuesto -->
			<tr>
				<td width="100%" class="tituloResumen BL BR BB">
					P&nbsp;&nbsp;&nbsp;R&nbsp;&nbsp;&nbsp;E&nbsp;&nbsp;&nbsp;S&nbsp;&nbsp;&nbsp;U&nbsp;&nbsp;&nbsp;P&nbsp;&nbsp;&nbsp;U&nbsp;&nbsp;&nbsp;E&nbsp;&nbsp;&nbsp;S&nbsp;&nbsp;&nbsp;T&nbsp;&nbsp;&nbsp;O
				</td>
			</tr>
			<tr>
				<td style="padding: 0px;" class="BL BB BR" width="100%">
					<table width="100%" class="tablaResumen" id="tblResumenMovtosR"
						cellpadding="5">
						<thead>
							<tr>
								<th class="tituloResumen BR BB">EP</th>
								<th class="tituloResumen BR BB">Concepto Movimiento</th>
								<th class="tituloResumen BR BB">Importe</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			
			<tr id="adjuntarDocRete" style="display:none">
				<td width="100%">
					<div >
						<table width="100%" align="center">
							<tr>
								<td align="right">
									<input type="button" id="btnAdjuntaDocRete" value="Adjuntar Justificacion" class="btnInterfaceBG"/>
								</td>
							</tr>
						</table>
					</div>
				</td>
			</tr>

			<!-- Resumen Presupuesto -->
			<!-- tr id="resumenCuestionario" style="display:none" -->
			<tr id="resumenCuestionario" style="display: none">
				<td style="padding: 0px;" width="100%" class="tituloResumen BL BR BB tituloSuperior">
					<table width="100%" class="tablaResumen" id="tblResumenPagoR"
						cellpadding="5">
						<thead>
							<tr>
								<th class="tituloResumen BR BB" colspan="2">Cuestionario de determinaci&oacute;n Art. 15-D</th>
							</tr>
							<tr>
								<th class="tituloResumen BR BB" width="80%">Pregunta</th>
								<th class="tituloResumen BR BB" width="20%">Respuesta</th>
							</tr>
						</thead>
						<tbody id="answersBdy">
							
						</tbody>
					</table> 
				</td>
			</tr>
			
		</table>
	</fieldset>
</div>

<div id="dlgQuestionnaire" title="Cuestionario de Contratacion">
	<iframe id="questionnaire" src="about:blank" align="top" frameborder="0" height="360" width="800"> </iframe>
</div>

