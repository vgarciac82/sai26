
<div id="encabezadoPolManual">
	<div class="card-header"> <h3>Resumen Póliza Manual</h3> </div>					
	<hr class="mt-3"> 				
	
	<div class="row d-flex justify-content">
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<label for="nFolioDocPoliza"> Folio Documento </label>															
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<label for="nFolioPoliza"> Folio Poliza </label>															
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<label for="fCaptura"> Fecha de Captura </label>															
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<label for="fAplicacion"> Fecha de Aplicacion </label>															
		</div>
	</div>
	
	<div class="row d-flex justify-content">
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<input id="nFolioDocPoliza"  name="nFolioDocPoliza"size="10" class="form-control form-control-sm" readonly/>			
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<input id="nFolioPoliza"  name="nFolioPoliza" size="14" class="form-control form-control-sm" readonly/>															
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<input id="fCaptura" name="fCaptura" size="14" class="form-control form-control-sm" readonly="readonly"/>							
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<input id="fAplicacion" name="fAplicacion" size="14" class="form-control form-control-sm" readonly="readonly"/>													
		</div>
	</div>
			
	<div class="row d-flex justify-content">
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<label for="EF"> Ejercicio Fiscal </label>															
		</div>
		<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
			<label for="hPolCtroContable"> Centro Contable </label>															
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<label for="PolTipo"> Tipo de Poliza </label>															
		</div>
	</div>
	
	<div class="row d-flex justify-content">
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<input id="EF"  name="EF"size="10" class="form-control form-control-sm" readonly="readonly"/>			
		</div>
		<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
			<input id="hPolCtroContable"  name="hPolCtroContable" size="50" class="form-control form-control-sm" readonly="readonly"/>		
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<input id="PolTipo" name="PolTipo" size="25" class="form-control form-control-sm" readonly="readonly"/>					
		</div>
	</div>
	
	<div class="row d-flex justify-content">
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
		</div>
		<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
			<label for="cConcepto"> Concepto </label>															
		</div>
		<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
			<label for="txtObservaciones"> Observacion </label>															
		</div>
	</div>
	
	<div class="row d-flex justify-content">
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
		</div>
		<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
			<textarea id="cConcepto" rows="5" cols="50" name="cConcepto" class="form-control form-control-sm" readonly="readonly"></textarea>
		</div>
		<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
			<textarea id="txtObservaciones" name="txtObservaciones" rows="5" cols="50" class="form-control form-control-sm areaTexto"></textarea>															
		</div>
	</div>
	
	<div class="row d-flex justify-content">
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<label for="fComprobacion"> Folio de Comprobacion </label>															
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<label for="Tcargos"> Total Cargos </label>															
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<label for="Tabonos"> otal Abonos </label>															
		</div>
	</div>
	
	<div class="row d-flex justify-content">
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<input  type="text" id="fComprobacion" style="font-weight: bolder;" readonly="readonly" class="form-control form-control-sm money">	
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<input  type="text" id="Tcargos" style="font-weight: bolder;" readonly="readonly" class="form-control form-control-sm money">															
		</div>
		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			<input type="text" id="Tabonos" style="font-weight: bolder;" readonly="readonly" class="form-control form-control-sm money">															
		</div>
	</div>
		
</div>

<div id="detailDiv">
	<h5> Detalle </h5>
	<hr class="mt-3">	
	
	<table id="pCuentas" class="table table-striped">
		<thead>
			<tr align="center">
				<th> <!-- 0 --> Rengl&oacute;n </th>
				<th> <!-- 1 --> Cuenta </th>
				<th> <!-- 2 --> Desc. Cuenta </th>
				<th> <!-- 3 --> Auxiliar </th>
				<th> <!-- 4 --> Descripci&oacute;n Mov. </th>
				<th> <!-- 5 --> Cargos </th>
				<th> <!-- 6 --> Abonos </th>
				<th> <!-- 7 --> Parcial </th>
				<th> <!-- 8 --> Grupo </th>
				<th> <!-- 9 --> SubGrupo </th>
				<th> <!-- 10 --> Evento </th>
				<th> <!-- 11 --> Partida </th>
				<th> <!-- 12 --> CABMS </th>
				<th> <!-- 13 --> CUCOP </th>
				<th> <!-- 14 --> No. Evento </th>
				<th> <!-- 15 --> Cta Aplicacion </th>
			</tr>
		</thead>
	</table>

</div>