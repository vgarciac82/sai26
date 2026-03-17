<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ejemplo carga de CB</title>
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
	<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
	<link rel="stylesheet" type="text/css" href="../css/interfaz.css"/>
	<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"/>
			
	<style type="text/css" title="currentStyle">
		@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";	
	
		@import "../Generador/css/demo_page.css";
	</style>
	
<script src="https://code.jquery.com/jquery-3.5.0.js"></script>
		
<link rel="stylesheet" type="text/css" href="../Bootstrap/Bootstrap502/css/bootstrap.css"/>
<link rel="stylesheet" type="text/css" href="../Bootstrap/Bootstrap-dataTables/datatables.css"/>

<script type="text/javascript" src="../Bootstrap/Bootstrap502/js/bootstrap.js"></script>
<script type="text/javascript" src="../Bootstrap/Bootstrap502/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Bootstrap/Bootstrap-dataTables/datatables.js"></script>

<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">

<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>
<script type="text/javascript" src="js/CargaFacturaObra.js"></script>
<script type="text/javascript">
	var myModalValidaFact,myModalCont;
	var dtContratos;
	$(document).ready(function() {
		init();
		$('#tblConsultaCont').on('dblclick', 'tr',function(){
			if ($(this).hasClass('row_selected'))             
				$(this).removeClass('row_selected');         
			else            
				$(this).addClass('row_selected');
		  	var anSelected=fnGetSelected(dtContratos);
		  	aData=dtContratos.fnGetData(anSelected[0]);
		  	
		  	$("#cIDContrato").val(aData[2]);
		  	$("#ccvecontrato").val(aData[2]);
		  	
		  	$("#cProveedor").val("[ "+aData[1]+" ] "+aData[3]);
		  	$("#cIdRFC").val(aData[1]);
		  	$("#foliosai").val(aData[0]);
		  	document.getElementById("factContrato").checked=true;
		  	$("#cobjetocontrato").val(aData[9]);
		  	$("#mImporteTotal").val(aData[4]);
		  	$("#mImporteIVA").val(aData[5]);
		  	if("Si"==aData[6]){
		  		$("#mImporteTotal").val(aData[7]);
		  		$("#mImporteIVA").val(aData[8]);
		  	}
		  	$("#mImporteTotal").formatCurrency();
		  	$("#mTotalFacturaV").val(0.00);
		  	$("#mImporteConvenioConIVA").val(aData[10]);
		  	$("#mImporteConvenioIVA").val(aData[11]);
		  	$("#mImporteConvenioConIVA").formatCurrency();
		  	dtContratos="";
		  	creaDTFacturas();
			leeMontosFacturas();
		  	myModalCont.hide();
		  	
		});
	});
</script>

</head>
<br/>
<body>
	<form action="CFDI/Create" method="post" id="formCFDI">
		<div id="container" class="container-fluid">
			<div class="card-header"> <h3> Carga de Factura Global </h3> </div>
			<hr class="mt-3"/>
				
			<div class="form-group" id="seleccion">
				<div class="row">
					<div class="col-2">
						<label for="IDContrato">Contrato: </label>
					</div>
					<div class="col-6">
						<input type="text" id="cIDContrato" name="cIDContrato"  readonly="readonly" class="form-control"  />
					</div>
					<div class="col-1">
						<input type="button" id="showContratos" name="showContratos" onclick="showContratosOb();" value="..." class="form-control" style="width: 30px" title="Buscar contrato."/>
					</div>
				</div>
				<div class="row">
					<div class="col-2">
						<label for="cIdRFC">Beneficiario: </label>
					</div>
					<div class="col-6">
						<input type="text" id="cProveedor" name="cProveedor"  readonly="readonly" class="form-control"  />
						<input type="hidden" id="cIdRFC" name="cIdRFC"  readonly="readonly" class="form-control"  />
					</div>
				</div>
				<div class="row">
					<div class="col-2">
						<label for="cobjetocontrato">Objeto del Contrato: </label>
					</div>
					<div class="col-6">
						<textarea id="cobjetocontrato" name="cobjetocontrato"  rows="3" cols="80" class="form-control" readonly="readonly"></textarea>
					</div>
				</div>
				<div class="row">
					<div class="col-2">
						<label for="mImporteTotal">Monto del Contrato: </label>
					</div>
					<div class="col-6">
						<input type="text"readonly="readonly" id="mImporteTotal" name="mImporteTotal" class="form-control">
					</div>
				</div>
				<div class="form-group row">
					<div class="col-2">
						<label for="mImporteConvenioConIVA">Monto del Convenio: </label>
					</div>
					<div class="col-6">
						<input type="text"readonly="readonly" id="mImporteConvenioConIVA" name="mImporteConvenioConIVA" class="form-control">
					</div>
				</div>
				<div class="form-group row">
					<div class="col">
						<div class="form-check  form-check-inline">
							<input class="form-check-input" type="radio" name="factGlobal" id="factContrato"  checked="checked" onclick="putValueTipoFact()">
						  	<label class="form-check-label" for="factContrato">
						    	Factura de contrato
						  	</label>
						</div>
						<div class="form-check  form-check-inline">
							<input class="form-check-input" type="radio" name="factGlobal" id="factConveio" onclick="putValueTipoFact()">
						  	<label class="form-check-label" for="factConveio">
						    	Factura de Convenio
						  	</label>
						</div>
					</div>
				</div>
				<div class="form-group row">
					<div class="col-2">
					</div>
					<div class="col-2">
						<input type="button" onclick="creaDiagloFacturas(true);" value="Cargar Facturas" id="cargarFacturasBtn" class="btn btn-outline-secondary btn-sm"/>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row" id="facturasCapturadasDiv">
					<div class="col">
						<h5 style="color: #1A69A9;" >Facturas Capturadas</h5>
					</div>
				</div>
	 			<div class="row" id="facturasCapturadasDiv">
					<div class="col">
						<table id="grdValidaFacturas" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr> 
				        		    <th>Factura</th>
									<th>Importe Bruto</th>
									<th>Impuestos</th>
									<th>Total</th>
				        		</tr>										
							</thead>
						</table>
					</div>
				</div>
		 		<div class="row" id="facturasCapturadasDiv">
					<div class="col-2">
						<label for="mTotalFacturaV">Total de las facturas: </label>
					</div>
					<div class="col-4">
						<input type="text" style="text-align: right;" name="mTotalFacturaV" id="mTotalFacturaV" value="0.00"  readonly class="form-control" />
					</div>
				</div>
			</div>
		
		</div>
		<!-- Modal valida factura-->
		<div class="modal fade" id="dialog-validaFact" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" title="Carga de Facturas de Contrato">
	  		<div class="modal-dialog modal-lg">
	    		<div class="modal-content">
	    			<div class="modal-header">
	        			<h5 class="modal-title" id="modalValidaFact">Carga Facturas de Contrato </h5>
	        			<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	      			</div>
					<div class="modal-body">
						<div id="uploadFacturasDiv" class="form-group row">
							<iframe id="uploadFacturasFrm" src="../Generador/UploadCFDIContrato.jsp?TipoContrato=OB&IDContrato=&RFC=" height="360" width="510">
							</iframe>
						</div>
						
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-primary" id="btnAcepValidaFact" name="btnAcepValidaFact" onclick="aceptValidaFact()">Aceptar</button>
						<button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="btnCancelValidaFact" name="btnCancelValidaFact">Cerrar</button>
					</div>
	    		</div>
	    	</div>
	    </div>
	    <!-- Modal de contratos -->
		<div class="modal fade bd-example-modal-lg" id="modalContracts" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
		  <div class="modal-dialog modal-lg">
		    <div class="modal-content">
		      <div class="modal-header">
		        <h5 class="modal-title" id="exampleModalLabel">Contratos Autorizados</h5>
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		      </div>
		      <div class="modal-body">
		        	<div class="form-group row" >
	        			<div class="col-6 col-sm-4">
							<label for="cIdDefinitivo" class="form-label">Contrato SAI</label>
							<input type="text" class="form-control" id="cIdDefinitivo" name="cIdDefinitivo" placeholder="Ejemplo OBRP-A04-1">
	        			</div>
						<div class="col-3 col-sm-4">
							<label for="cRazonSocial" class="form-label">Proveedor</label>
							<input type="text" class="form-control" id="cRazonSocial" name="cRazonSocial" placeholder="Raz&oacute;n Social">
	        			</div>
	        			<div class="col-3 col-sm-4">
							<label for="cNumCNET" class="form-label">Contrato CNET</label>
							<input type="text" class="form-control" id="cNumCNET" name="cNumCNET" placeholder="Ejemplo CNF-LO-016RHQ001-E193">
	        			</div>
		        	</div>
		        	<div class="form-group row" >
		        		<div class="col-3 col-sm-4">
							<input type="button" id="btnSearchContract" value="Buscar" class="btn btn-primary" onclick="creaTablaContratos();"/>
	        			</div>
		        	</div>
		        	<div class="form-group">
						<div class="row">
							<div class="col">
								<table id="tblConsultaCont" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
									<thead >
										<tr>
											<th align="center">Folio</th>
											<th align="center">RFC</th>
											<th align="center">Contrato CNET</th>
											<th align="center">Proveedor </th>
											<th align="center">Monto Con IVA </th>
											<th align="center">Monto IVA </th>
											<th align="center">¿Es plurianual? </th>
											<th align="center">Monto total plurianual </th>
											<th align="center">Monto IVA plurianual </th>
											<th align="center">Concepto </th>
											<th align="center">Monto Convenio con IVA </th>
											<th align="center">Monto Convenio IVA </th>
										</tr>										
									</thead>
								</table>
							</div>
						</div>
					</div>
		      </div>
		    </div>
		  </div>
		</div>
		<input type="hidden" id="TipoContrato" value="OB" name="TipoContrato" />
		<input type="hidden" id="ccvecontrato" value="" name="ccvecontrato" />
		<input type="hidden" id="foliosai" value="OB" name="foliosai" />
		<input type="hidden" id="mImporteIVA" value="0" name="mImporteIVA" />
		<input type="hidden" id="tipoFactGlobal" value="1" name="tipoFactGlobal" />
		<input type="hidden" id="mImporteConvenioIVA" value="0" name="mImporteConvenioIVA" />
	</form>
</body>
</html>