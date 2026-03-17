<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Carga Recibo Electronica de Pago a Contrato</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="js/AdjuntaREPContrato.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		init();
	});
</script>

</head>
<br/>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0" style="width: 100%">
	<form>
		<input type="hidden" id="MontoTotalContrato">
		<input type="hidden" id="MontoTotalCFDIs">
		
		<div id="container" class="container" style="width: 100%">
			<div class="card-header"> <h3> Adjuntar Recibo Electronico de Pago a Contrato </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="tipoContrato">Seleccione el tipo de contrato:</label>		  																									
				</div>												
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<select id="tipoContrato" class="form-select form-select-sm">
						<option value="" selected="selected">Seleccione una opcion</option>
						<option value="OB" >Obra P&uacute;blica</option>
						<option value="DI">Diverso</option>
					</select>
				</div>																														
			</div>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="tipoContrato">Seleccione el contrato:</label>		  																									
				</div>												
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<div class="input-group">	
						<input value="" type="text" class="form-control form-control-sm AyudaSyC notEditable" name="IDContrato" id="IDContrato" onChange="cargaPagosContrato();" size="40" maxlength="40" readonly="readonly" />
					</div>
				</div>																														
			</div>

			<div class="container" style="width: 100%">
				<h5> Pagos Pendientes de Recibo </h5>
				<hr class="mt-3">
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<table class="table table-striped" id="dtAdjuntarREP">
							<thead>
								<tr>
									<th>CxP</th>
									<th>Folio</th>
									<th>Monto</th>
									<th>&nbsp;</th>
								</tr>
							</thead>
						</table>
					</div>
				</div>
			</div>
			
			<div class="container" style="width: 100%">
				<h5> Estado de cuenta del CFDI de contrato </h5>
				<hr class="mt-3">
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
						<b>Total Contrato: <span id="totalContrato"> </span></b>
						<b>Total en CFDIs: <span id="totalCFDIS"></span></b>
					</div>
				</div>	
				
				<div class="container" style="width: 100%">
					<div class="row d-flex justify-content-center">
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
							<table class="table table-striped" id="dtInfoContratoCFDI">
								<thead>
									<tr>
										<th>UUID</th>
										<th>RFC</th>
										<th>Importe Bruto</th>
										<th>Importe IVA</th>
										<th>Otro Impuestos</th>
										<th>Total</th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
					
			</div>
				
			<div class="container" style="width: 100%">
				<h5> Recibos de Pago </h5>
				<hr class="mt-3">
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<table class="table table-striped" id="dt_RecibosPago">
							<thead>
								<tr>
									<th>Contrarecibo</th>
									<th>UUID Padre</th>
									<th>UUID Recibo</th>
									<th>#Parcialidad</th>
									<th>Importe Contrarecibo</th>
									<th>Importe Recibo</th>
									<th>Importe anterior</th>
									<th>Importe Pagado</th>
									<th>Importe Insoluto</th>
								</tr>
							</thead>
						</table>
					</div>
				</div>
			</div>
		</div>
		
		<div id="msgDialog" class="container">
			<h5> Resultado de la carga </h5>
			<hr class="mt-3"/>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
					<textarea rows="10" cols="40" maxlength="1000" class="form-control" id="msgTxt"></textarea>						
				</div>
			</div>

		</div>
				
		<div id="dlgCargar" title="Carga de archivos">
			<iframe  src="about:blank" align="top" frameborder="0" height="360" width="600" id="cargaRepFrame">
			</iframe>
		</div>
					
	</form>
</body>

</html>