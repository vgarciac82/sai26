<div class="form-group row">
	<div class="col">
		<h6 style="color: #1A69A9;" > Datos generales</h6>
	</div>	
</div>
<div class="form-group row">
	<div class="col-3">
		<label for="NoCntSolicitudPago" class="form-label">No. Contrato:</label>
		<input class="form-control"  id="NoCntSolicitudPago" name="NoCntSolicitudPago" type="text"  readonly="readonly" />
	</div>
	<div class="col-3">
		<label for="rfcSolicitudPagos" class="form-label">R.F.C :</label>
		<input class="form-control" id="rfcSolicitudPagos" type="text" readonly="readonly" />
	</div>
	<div class="col-3">
		<label for="rfcSolicitudPagos" class="form-label">Beneficiario:</label>
		<input class="form-control" id="beneficiarioSolicitudPagos" name="beneficiarioSolicitudPagos" type="text" readonly="readonly"/>
	</div>
	<div class="col-3">
		<label for="acumuladoAmortizacion" class="form-label">Acumulado Amortizado:</label>
		<input id="acumuladoAmortizacion" name="acumuladoAmortizacion" readonly="readonly" class="form-control"/> 
		<input type="hidden" id="fEntregaVentanilla" name="fEntregaVentanilla" size="10" readonly="readonly" class="fechaEstimacion"/> 
		<input type="hidden" id="fEntregaVentanillaInicial" size="10" readonly="readonly"/>
	</div>	
</div>
<div class="form-group row">
	<div class="col-3">
		<label for="mesEstimado" class="form-label">Seleccione mes Estimado:</label>
		<select id="mesEstimado"name="mesEstimado" class="custom-select estimacion">
				<option value="0">Seleccione el mes</option>
				<option value="1">Enero</option>
				<option value="2">Febrero</option>
				<option value="3">Marzo</option>
				<option value="4">Abril</option>
				<option value="5">Mayo</option>
				<option value="6">Junio</option>
				<option value="7">Julio</option>
				<option value="8">Agosto</option>
				<option value="9">Septiembre</option>
				<option value="10">Octubre</option>
				<option value="11">Noviembre</option>
				<option value="12">Diciembre</option>
		</select>
	</div>
	<div class="col-9">
		<div class="row">
			<div class="col">
				<label for="NoCntSolicitudPago" class="form-label">Periodo de estimaci&oacute;n:</label>
			</div>
		</div>
		<div class="row">
			<div class="col-auto" >
				<label for="fperiodoEstimacionIni_">De: </label>
			</div>
			<div class=" col-auto">
				<div class="input-group date" id="fperiodoEstimacionIni_" data-target-input="nearest">
		          <input type="text" class="form-control datetimepicker-input" data-target="#fperiodoEstimacionIni_" title="Fecha inicial de la estimación" id="fperiodoEstimacionIni" name="fperiodoEstimacionIni" />
		          <div class="input-group-append" data-target="#fperiodoEstimacionIni_" data-toggle="datetimepicker" title="Fecha inicial de la estimación">
		            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
		          </div>
		        </div>
	        </div>
	        <div class="col-auto" >
				<label for="fperiodoEstimacionFin_">A: </label>
			</div>
			<div class=" col-auto">
				<div class="input-group date" id="fperiodoEstimacionFin_" data-target-input="nearest">
		          <input type="text" class="form-control datetimepicker-input" data-target="#fperiodoEstimacionFin_" title="Fecha Fin de la estimación" id="fperiodoEstimacionFin" name="fperiodoEstimacionFin" />
		          <div class="input-group-append" data-target="#fperiodoEstimacionFin_" data-toggle="datetimepicker" title="Fecha Fin de la estimación">
		            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
		          </div>
		        </div>
	        </div>
		</div>
	</div>
	
</div>
<div class="form-group row">
	<div class="col-3">
		<label for="">Tipo de Captura: </label>
	</div>
	<div class="col-3">
		<select id="TipoCaptura" name="TipoCaptura" class="custom-select estimacion" onchange="tipoCaptura();" >
				<option value="0">Seleccionar</option>
				<option value="1">Anticipo</option>
				<option value="2">Estimacion</option>
		</select>
	</div>
</div>
<div class="form-group" id="CapturaEstimacion" style="display:none">
	<div class="row">
		<div class="col-3">
			<label for="eFiscalPago">Ejercicio Fiscal: </label>
		</div>
		<div class="col-3">
			<input id="eFiscalPago" name="eFiscalPago" type="text" readonly="readonly" class="form-control"/>
		</div>
	</div>
	<div class="row" id="trUltimaEstimacion" style="display: none;">
		
		<div class="col-3">
			<label class="form-check-label" for="checkbox_ultimaEstim">¿Es la ultima estimaci&oacute;n? </label>
		</div>
		<div class="col-3" style="padding-left: 32px">
			<input class="form-check-input" id="checkbox_ultimaEstim" name="checkbox_ultimaEstim" type="checkbox" onclick="esUltimaEstimacion()" />
		</div>
	
	</div>
	<div class="row" id="trCapitalizable" style="display: none;">
		<div class="col-3">		
			<label class="form-check-label" for="checkbox_ultimaEstim">¿Es capitalizable? </label>
		</div>
		<div class="col-3" style="padding-left: 32px">
			<input id="checkbox_obraCapitalizable" name="checkbox_obraCapitalizable" type="checkbox"  onclick="esObraCapitalizable()" class="form-check-input" />
		</div>
	</div>
	<div class="row" id="contenedorNoEstimacion">
		<div class="col-3">
			<label for="noEstimacion">N&uacute;mero Estimaci&oacute;n: </label>
		</div>
		<div class="col-3">
			<input type="text" id="noEstimacion" name="noEstimacion"  onkeypress="Validaciones(this,2)" class="form-control estimacion" readonly="readonly" /> 
		</div>
		<div class="col-3">
			<input type="text" id="noEstimacion2" name="noEstimacion2"  onkeypress="Validaciones(this,2)" readonly="readonly" class="form-control"/>
		</div>
	</div>
	<div class="row">
		<div class="col-3">
			<label for="eFiscalPago">Monto Estimaci&oacute;n: </label>
		</div>
		<div class="col-3">
			<input type="text" id="mMontoEstimacion" name="mMontoEstimacion" onkeypress="Validaciones(this,9)" class="form-control estimacion"  onchange="estimate();" />
		</div>
	</div>
	<div class="row" id="trAmort">
		<div class="col-3">
			<label for="mMontoEstimacionAmortizado">Amortizaci&oacute;n: </label>
		</div>
		<div class="col-3">
			<div class="input-group">
				<input type="text"  id="mMontoEstimacionAmortizado" name="mMontoEstimacionAmortizado" readonly="readonly" class="form-control notEditable" onblur="cambiafrmt(this);" onchange="cambiafrmt(this);calculaMontosEstimacion();" value="0" />
				<span class="input-group-text" id="inputMontoEstimacionAmortizado">-</span>
			</div>
		</div>
	</div>
	<div class="row" id="trSub">
		<div class="col-3">
			<label for="mMontoEstimacionSubtotal">SubTotal: </label>
		</div>
		<div class="col-3">
			<input type="text" id="mMontoEstimacionSubtotal" name="mMontoEstimacionSubtotal" readonly="readonly" class="form-control notEditable" />
		</div>
	</div>
	<div class="row" id="trRete">
		<div class="col-3">
			<label for="mMontoEstimacionRetencion">Retenci&oacute;n: </label>
		</div>
		<div class="col-3">
			<div class="input-group">
				<input type="text" id="mMontoEstimacionRetencion" name="mMontoEstimacionRetencion" readonly="readonly" class="form-control notEditable" />
				<span class="input-group-text" id="inputMontoEstimacionRetencion">-</span>
			</div>
		</div>
	</div>
	<div class="row" >
		<div class="col-3">
			<label for="mMontoEstimacionIva">Monto IVA: </label>
		</div>
		<div class="col-3">
			<input type="text" id="mMontoEstimacionIva" name="mMontoEstimacionIva" readonly="readonly" class="form-control notEditable" onblur="cambiafrmt(this);" onchange="cambiafrmt(this);" />
		</div>
	</div>
	<div class="row" >
		<div class="col-3">
			<label for="mMontoEstimacionMasIva">Monto Total: </label>
		</div>
		<div class="col-3">
			<input type="text" id="mMontoEstimacionMasIva" name="mMontoEstimacionMasIva" readonly="readonly" class="form-control notEditable" onblur="cambiafrmt(this);" onchange="cambiafrmt(this);" />
		</div>
	</div>
	<div class="row" >
		<div class="col-2">
			<input type="text"  id="nPorceAvanceFisicoEstimado" name="nPorceAvanceFisicoEstimado" class="estimacion" value="-1" style="visibility:hidden" />
		</div>
		<div class="col-2">
			<input type="text" id="nPorceAvanceFisicoEjecutado" name="nPorceAvanceFisicoEjecutado" class="estimacion" value="-1" style="visibility:hidden" />
		</div>
		<div class="col-2">
			<input type="text" id="mmontoFisicoEjecutado" name="mmontoFisicoEjecutado" class="estimacion" value="-1" style="visibility:hidden" />
		</div>
		<div class="col-2">
			<input type="text" size="3" maxlength="5" id="nPorceAvanceFisicoProgramado" name="nPorceAvanceFisicoProgramado" class="estimacion" value="-1" style="visibility:hidden" />
		</div>
		<div class="col-2">
			<input type="text" size="12" maxlength="13" id="mmontoFisicoProgramado" name="mmontoFisicoProgramado" class="estimacion" value="-1" style="visibility:hidden" />
		</div>
	</div>
	<div class="row" >
		<div class="col-3">
			<input type="button" id="btnAddEstimacion" class="estimacion btn btn-outline-secondary" value="Agregar Estimaci&oacute;n" />
			<input type="button" id="btnModEstimacion" value ="Actualizar Estimaci&oacute;n" class="btn btn-outline-secondary"/>
			<input type="button" id="btnCancelModificacion" value ="Cancelar" class="btn btn-outline-secondary"/>
		</div>
	</div>
</div>
<div class="form-group row">
	<div class="col">
		<table id="dt_solicitudPago" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
			<thead >
				<tr> 
        		    <th>No. Estimaci&oacute;n</th>
					<th>Ejercicio Fiscal</th>
					<th>Monto Estimaci&oacute;n</th>
					<th>%Avance F&iacute;sico Estimado</th>
					<th>%Avance F&iacute;sico Ejecutado</th>
					<th>%Avance F&iacute;sico Programado</th>
					<th>Monto F&iacute;sico Ejecutado</th>
					<th>Monto F&iacute;sico Programado</th>
					<th>Fecha entrega a ventanilla</th>
					<th>Mes estimado</th>
					<th>Fecha Inicial</th>
					<th>Fecha Final</th>
					<th>Estatus Pago</th>
					<th>Fecha de Pago</th>
					<th>Estatus Estimaci&oacute;n</th>
					<th>nIdEstatus</th>
					<th></th>
        		</tr>										
			</thead>
		</table>
	</div>
</div>

