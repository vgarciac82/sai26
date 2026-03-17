
<div id="generalPAAS" style="width: 98%" class="container">
	<input type="hidden" id="cIdCABM" name="cIdCABM" value=""/>
	<input type="hidden" id="cIdSubPartida" name="cIdSubPartida" value=""/>
	<input type="hidden" id="cDescripcion" name="cDescripcion" value=""/>
	<input type="hidden" id="nIdFundamentoLeg" name="nIdFundamentoLeg" value="3"/>
	<input type="hidden" id="cIdAlmacenEntrega" name="cIdAlmacenEntrega" value=""/>
	
	<h1>Informaci&oacute;n General</h1>
	
	<div onkeydown="return(desactivaBackspace(event))">
		<div class="row">					
			<div class="col-12 col-lg-4 col-md-4 col-sm-12">													
				<label for="cIdCapitulo" class="form-label"> Cap&iacute;tulo </label>
				<select name="cIdCapitulo" id="cIdCapitulo" onchange="clearDatosPartida();" class="form-select form-select-sm">
					<option value="-1" selected="selected">Seleccione un capitulo</option>
					<option value="2">2000 ADQUISICIÓN DE MATERIALES Y SUMINISTROS</option>
					<option value="3">3000 CONTRATACIÓN DE SERVICIOS</option>
				</select>																
			</div>
			<div class="col-12 col-lg-4 col-md-4 col-sm-12">									
				<label for="contrarecibo" class="form-label"> Partida </label>
				<div class="input-group">
					<input readonly="readonly" type="text" name="partida" id="partida" onchange="creaDTCucop()" class="form-control form-control-sm"/>
					<input type="button" id="btnCatPartidas" onclick="muestraPartidas()" value="..." class="btn btn-secondary btn-sm"/>
				</div>												
			</div>
			<div class="col-12 col-lg-4 col-md-4 col-sm-12">									
				<label for="fAplicacion" class="form-label"> Lugar de Entrega </label>
				<div class="input-group">
					<input readonly="readonly" type="text" name="cAlmacen" id="cAlmacen" class="form-control form-control-sm"/>
					<input type="button" id="btnCatAlmacen" onclick="muestraAlmacenes()" value="..." class="btn btn-secondary btn-sm"/>
				</div>						
			</div>
		</div>	
	</div>
		
	<table style="width: 100%" onkeydown="return(desactivaBackspace(event))">			
		<tr id="trDescripFechas" style="display: none;">
			<td align="left">Fecha de Inicio</td>
			<td align="left">Fecha de Fin o de Entrega</td>
			<td align="left">Tipo de Adjudicacion</td>
		</tr>
		<tr id="trFechas" style="display: none;">
			<td align="left"><input type="text" size="13" readonly="readonly" class="fecha" id="fechaInicio"></td>
			<td align="left"><input type="text" size="13" readonly="readonly" class="fecha" id="fechaFin"></td>
			<td><select id="tipoAdjudicacion" style="widows: 85%">
					<option value="14">Adjudicaci&oacute;n Directa Artículo 42 de la LAASSP Pedidos</option>
			</select></td>
		</tr>			
	</table>

</div>

<!-- DIV Seleccion de CUCOPs con disponible al momento -->
<div id="CUCOPSDIV"  style="width: 98%" class="container">	
	<h1> CUCOPS Calendarizados en el PAAS </h1>
		
	<div class="row">
		<div class="col-12 col-lg-8 col-md-8 col-sm-12">
			<label class="form-label"> <b>Seleccione de la siguiente lista el CUCOP del que realizara el pago y de doble clic en el.</b> </label>
		</div>									
		<div class="col-12 col-lg-2 col-md-2 col-sm-12">
			<label for="PAAS" class="form-label"> <a id="PAAS" onclick="showPAAS();" title="Calendarizar Programa Anual">Programa Anual</a> </label>								
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12">
			<label for="Refrescar" class="form-label"> <a><img alt="Refrescar" src="../imagenes/iconos/recur.png" onclick="creaDTCucop();">Actualizar tabla</a> </label>								
		</div>
	</div>
		
	<div id="DT_TblCucop"  class="table-responsive">
		<table id="tblCucop" class="table table-striped" >
			<thead >
				<tr>
					<th align="center" >CUCOP</th>
					<th align="center">Descripción</th>
					<th align="center">CantidadDisp_PAAS</th>
					<td style="display: none;"></td>
				</tr>
			</thead>
		</table>
	</div>
	
</div> <!-- FIN DIV Seleccion de CUCOPs con disponible al momento -->

<!-- DIV Calendarizacion de CUCOPS -->
<div id="CalendarioCUCOPS" style="width: 98%" class="container">
	<h1> Calendario. </h1>
	
	<div class="row">
		<div class="col-12 col-lg-8 col-md-8 col-sm-12">
			<label class="form-label"> <b>Indique la cantidad de bienes/servicios recibidos segun el calendario. Y al terminar presione el boton Guardar.</b> </label>
		</div>
	</div>
	
	<div id="DT_tblRequisicionMeses" class="table-responsive">
		<table id="tblRequisicionMeses2" class="table table-striped"  >
			<thead>
				<tr>
					<th align="center" style="display: none;">&nbsp;</th>
					<th align="center">Mes</th>
					<th align="center">Cucop</th>
					<th align="center">Cantidad <br />Disponible</th>
					<th align="center">Monto <br />Disponible</th>
					<th align="center" style="display: none;">nPorcentaje IVA</th>
					<th align="center" style="display: none;">Descripci&oacute;n</th>
					<th align="center">Cantidad</th>
					<th align="center" style="display: none;">&nbsp;</th>
				</tr>
			</thead>
		</table>
	</div>
	
	<br/>
	
	<div class="row">
		<div class="col-12 col-lg-10 col-md-10 col-sm-12">
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12">
			<label class="form-label"> <input type="button" id="AgregarLineasAgrup" name="AgregarLineasAgrup" value="Agregar" onclick="agregarLineasAgrupadas()" class="btn btn-secondary btn-sm"/> </label>
		</div>
	</div>
	
</div> <!-- FIN DIV Calendarizacion de CUCOPS -->

<!-- DIV CUCOPS Seleccionados. -->
<div id="CalendarioCUCOPS" style="width: 98%" class="container">
	<h1> CUCOPS Seleccionados. </h1>
		
	<div class="row">
		<div class="col-12 col-lg-12 col-md-12 col-sm-12">
			<label class="form-label"> <b>Se muestran los CUCOPS seleccionados para realizar el presente pago. El monto en facturas debe coincidir con el monto en CUCOPs</b> </label>
		</div>
	</div>
	
	<div class="row">
		<div class="col-12 col-lg-6 col-md-6 col-sm-12">
			<label class="form-label"> Monto Bruto Capturado: </label>
			<input type="text"  id="montoBruto" name="montoBruto" readonly style="border-width:0; background-color:transparent" />
		</div>
		<div class="col-12 col-lg-6 col-md-6 col-sm-12">
			<label class="form-label"> Monto Neto Capturado: </label>
			<input type="text"  id="montoNeto" name="montoNeto" readonly style="border-width:0; background-color:transparent" />
		</div>
	</div>
		
	<div class="table-responsive">
		<table id="tblLineas" class="table table-striped" >
			<thead>
				<tr>
					<th>#</th>
					<th>CUCOP</th>
					<th>Descripci&oacute;n</th>
					<th >Descripci&oacute;n Adicional</th>
					<th>Cantidad</th>
					<th>Precio<br />Unitario</th>
					<th>IVA</th>
					<th>Importe<br />Neto</th>
					<th>Unidad de<br />Medida</th>
					<th></th>
				</tr>
			</thead>
		</table>
	</div>
	
	<br/>
	
	<div class="row">
		<div class="col-12 col-lg-10 col-md-10 col-sm-12">
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12">
			<label class="form-label"> <input type="button" id="btnGuardarLineasPagoDirecetoPAAS" value="Guardar" class="btn btn-primary btn-sm" /> </label>
		</div>
	</div>
	
</div> <!-- FIN DIV CUCOPS Seleccionados. -->