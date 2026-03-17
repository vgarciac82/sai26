<!DOCTYPE html>
<html>
<head>

	<title>Recisión de contratos</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript">
		var dataRecision = new FormData();
		$(document).ready(function() {
			agregaDatePickerFechas();
			consultaRecisiones();
			myModalProvRecision = new bootstrap.Modal(document.getElementById('modalProveedoresRecision'), {
				  keyboard: false
			});
			myModalcontracts = new bootstrap.Modal(document.getElementById('modalContracts'), {
			  keyboard: false
			})
			$(".custom-file-input").on("change", function() {
				var fileName = $(this).val().split("\\").pop();
	  		  	$(this).siblings(".custom-file-label").addClass("selected").html(fileName);
	  		});
			
			$('#tblProveedoresDisponiblesRecision').on('dblclick','tr', function() {
				$(oTableConsulta.fnSettings().aoData).each(function() {
					$(this.nTr).removeClass('row_selected');
				});
				$(this).addClass('row_selected');
				var aTrs = $('#tblProveedoresDisponiblesRecision')
						.dataTable().fnGetNodes();
				for ( var i = aTrs.length; i >= 0; i--) {
					if ($(aTrs[i]).hasClass('row_selected')) {
						var nTr = $('#tblProveedoresDisponiblesRecision')
								.dataTable().fnGetData(aTrs[i]);
						$("#cProveedorRecision").val("[[ " + nTr[0] + " ]] " + nTr[1]);
						$("#cIdRFCRecision").val(nTr[0]);
						$("#cRazonSocialRecision").val(nTr[1]);
						myModalProvRecision.hide();
						$("#rfcProveedor").val("");
						$("#rSocialProveedor").val("");
						clearInputsContract();
					}
				}
				
			});//Fin document ready
			$('#tblConsultaContract').on('dblclick','tr', function() {
				$(tableConsultaContracts.fnSettings().aoData).each(function() {
					$(this.nTr).removeClass('row_selected');
				});
				$(this).addClass('row_selected');
				var aTrs = $('#tblConsultaContract')
						.dataTable().fnGetNodes();
				for ( var i = aTrs.length; i >= 0; i--) {
					if ($(aTrs[i]).hasClass('row_selected')) {
						var nTr = $('#tblConsultaContract')
								.dataTable().fnGetData(aTrs[i]);
						$("#cEjercicioContrato").val( nTr[0]);
						$("#cIdContratoDefinitivo").val(nTr[1]);
						$("#cNoContratoRecision").val(nTr[4]);
						$("#cCodProcedimientoCNETRecision").val(nTr[5]);
						$("#cCodExpedienteCNETRecision").val(nTr[6]);
						$("#cCodContratoCNETRecision").val(nTr[7]);
						$("#folioCaso").val(nTr[8]);
						$("#cConceptoContrato").val(nTr[9]);
						$("#cNameDB").val(nTr[10]);
						myModalcontracts.hide();
					}
				}
			});
		});
		
	</script>
</head>
<body>
	<form id="formRecisionContratos" class="needs-validation" >	
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"><h5 class="modal-title" id="exampleModalLabel">Captura Recisi&oacute;n de Contratos</h5></legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="form-group col-md-12 ">
							<div class=" input-group">
								<input type="text" class="form-control" placeholder="Busca Proveedor" id="cProveedorRecision" name="cProveedorRecision" readonly="readonly" required="required">
								<div class="input-group-append">
									<button class="btn btn-info" type="button" id="agregaProveedorRecision" name="agregaProveedorRecision" onclick="buscaProveedorRecision()">Proveedor</button>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="input-group">
						<div class="form-group col-md-12  ">
							<div class=" input-group">
								<input type="text" class="form-control" placeholder="Buscar contrato" id="cNoContratoRecision" name="cNoContratoRecision" readonly="readonly" required="required">
								<div class="input-group-append">
									<button class="btn btn-info" type="button" id="btnSearchContracts" name="btnSearchContracts" onclick="searchContract()">Contrato</button>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="input-group">
						<div class="form-group col-md-4">
	 						<label for="cNoProcedimientoCNET">C&oacute;digo de Procedimiento</label>
							<input type="text" class="form-control" placeholder="Captura C&oacute;digo de Procedimiento"  id="cCodProcedimientoCNETRecision" name="cCodProcedimientoCNETRecision" readonly="readonly" required="required">
						</div> 
						<div class="form-group col-md-4">
	 						<label for="nCodExpedienteCNET">N&uacute;mero de Expediente</label>
							<input type="text" class="form-control" placeholder="Captura N&uacute;mero de Expediente"  id="cCodExpedienteCNETRecision" name="cCodExpedienteCNETRecision" readonly="readonly" required="required">
						</div> 
						<div class="form-group col-md-4">
	 						<label for="nCodContratoCNET">C&oacute;digo de Contrato</label>
							<input type="text" class="form-control" placeholder="Captura C&oacute;digo de Contrato"  id="cCodContratoCNETRecision" name="cCodContratoCNETRecision" readonly="readonly" required="required">
						</div> 
					</div>
				</div>
				<div class="row">
					<div class="input-group">
					  	<div class="form-group col-md-12">
					  		<label for="cConceptoContrato">Objeto de Contrato</label>
					  		<textarea class="form-control" placeholder="Objeto del contrato." id="cConceptoContrato" name="cConceptoContrato" rows="3" readonly="readonly" ></textarea>
					  	</div>
				  	</div>
				</div>
				<div class="row">
					<div class="input-group">
					  	<div class="form-group col-md-12">
					  		<label for="causaRecision">Causa de Recisi&oacute;n</label>
					  		<textarea class="form-control" placeholder="Favor de capturar una breve descripci&oacute;n de la causa de recisi&oacute;n." 
					  		id="causaRecision" name="causaRecision" rows="3" title="Favor de capturar una breve descripci&oacute;n de la causa de recisi&oacute;n." required="required"></textarea>
					  	</div>
				  	</div>
				</div>
				<div class=" row">
					<div class="input-group">
						<div class="form-group col-md-4">
							<div class="row" id="divCheck3" >
								<div class="col-auto">
									<div class="form-check form-check-inline">
						  				<input class="form-check-input" type="checkbox" id="llevaPagoPendiente" name="llevaPagoPendiente" checked="checked" onclick="llevaPagoPendienteAction()">
						  				<label class="form-check-label" for="resicionContrato">Existe pago pendiente</label>
					  				</div>
					  			</div>
							</div>
						</div>
					</div>
				</div>
				<div class=" row">
					<div class="input-group">
						<div class="form-group col-md-4" >
	 						<label for="fOficioSancionado">Fecha de Recisi&oacute;n</label>
	 						<div class="input-group date" id="fTerminacion" data-target-input="nearest">
					          	<input type="text" class="form-control datetimepicker-input" data-target="#fTerminacion" placeholder="dd/mm/aaaa" title="Fecha de Recisión" id="fTermino" name="fTermino" required="required"/>
					          	<div class="input-group-append" data-target="#fTerminacion" data-toggle="datetimepicker" title="Fecha de Recisión">
					            	<div class="input-group-text"><i class="fa fa-calendar"></i></div>
					          	</div>
					        </div>
						</div> 
						<div class="form-group col-md-4">
	 						<label for="fTerminoFirmado">Fecha de Notificación a la UAF</label>
	 						<div class="input-group date" id="fNotificacion" data-target-input="nearest">
					          	<input type="text" class="form-control datetimepicker-input" data-target="#fNotificacion" placeholder="dd/mm/aaaa" title="Fecha de notificación a la UAF" id="fNotificacionUAF" name="fNotificacionUAF" required="required"/>
					          	<div class="input-group-append" data-target="#fNotificacion" data-toggle="datetimepicker" title="Fecha de notificación a la UAF">
					            	<div class="input-group-text"><i class="fa fa-calendar"></i></div>
					          	</div>
					    	</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="input-group">
						<div class="form-group col-md-12">
							<div class="custom-file" align="left">
						    	<input type="file" class="custom-file-input" id="nameArchivoRecision" name="nameArchivoRecision" aria-describedby="inputGroupFileAddon01" required="required">
						    	<label class="custom-file-label" for="nameArchivo" >Favor de adjuntar archivos .zip</label>
						  	</div>
						  	
						</div>
					</div>
				</div>
				<div class="row">
					<div class="input-group">
					  	<div class="form-group col-auto">
							<button type="button" class="btn btn-primary" id="btnGuardarRecisionCont" name="btnGuardarRecisionCont"  onclick="GuardaRecision()">Guardar</button>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group" id="divConsultaDatos" >
				<div class="form-group row">
					<div class="col">
						<table id="tblConsultaRecisionCont" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th align="center">Ejercicio Fiscal</th>
									<th align="center">RFC</th>
									<th align="center">Raz&oacute;n Social</th>
									<th align="center">Contrato SAI</th>
									<th align="center"># Contrato</th>
									<th align="center">C&oacute;digo de Procedimiento</th>
									<th align="center"># Expediente </th>
									<th align="center">C&oacute;digo de Contrato</th>
									<th align="center">Fecha Recisi&oacute;n</th>
									<th align="center">Fecha de Notificaci&oacute;n UAF</th>
									<th align="center">Existe Pago Pendiente</th>
									<th align="center">Fecha de Captura</th>
									<th align="center">Usuario Captura</th>
									<th align="center">Causa de Recisi&oacute;n</th>
								</tr>										
							</thead>
						</table>
					</div>
				</div>
			</div>
		</fieldset>
		<!-- Modal Proveedores-->
		<div class="modal fade bd-example-modal-lg" id="modalProveedoresRecision" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
			<div class="modal-dialog modal-auto">
		    	<div class="modal-content">
			      	<div class="modal-header">
			        	<h5 class="modal-title" id="exampleModalLabel">CATALOGO DE PROVEEDORES</h5>
			        	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			      	</div>
			      	<div class="modal-body">
			        	<div class="form-group row" >
			       			<div class="col-sm-4">
								<label for="rfcProveedor" class="form-label">RFC</label>
								<input type="text" class="form-control" id="rfcProveedor" name="rfcProveedor" >
			       			</div>
			       			<div class="col-sm-6">
								<label for="nCantidad" class="form-label">Raz&oacute;n Social</label>
								<input type="text" class="form-control" id="rSocialProveedor" name="rSocialProveedor" >
			       			</div>
			       			
			        	</div>
			        	<div class="form-group row" >
			        		<div class="col-3 col-sm-4">
								<input type="button" id="searchProveedor" value="Buscar" class="btn btn-primary" onclick="mostrarTablaProveedores('tblProveedoresDisponiblesRecision');"/>
			      			</div>
			       		</div>
				       	<div class="form-group">
							<div class="row">
								<div class="col-auto">
									<table id="tblProveedoresDisponiblesRecision" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
										<thead>
											<tr>
												<th scope="col">RFC</th>
												<th scope="col">Raz&oacute;n Social</th>
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
		<!-- Modal -->
		<div class="modal fade bd-example-modal-lg" id="modalContracts"
			tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="exampleModalLabel">Contrataciones en SAI</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"
							aria-label="Close"></button>
					</div>
					<div class="modal-body">
						<div class="form-group row">
							<div class="col-12 col-sm-12">
								<label for="cIdDefinitivo" class="form-label">**Doble clic al contrato mas reciente por ejercicio fiscal**</label>
							</div>
							
						</div>
						<div class="form-group">
							<div class="row">
								<div class="col">
									<table id="tblConsultaContract" class="table table-striped table-bordered dt-responsive nowrap"
										style="width: 100%">
										<thead>
											<tr>
												<th align="center">Ejercicio<BR> Fiscal</th>
												<th align="center">Contrato SAI</th>
												<th align="center" style="display: none;">RFC</th>
												<th align="center" style="display: none;">Proveedor</th>
												<th align="center">Contrato CNET</th>
												<th align="center">Código de Procedimiento</th>
												<th align="center" style="display: none;">Número de Expediente</th>
												<th align="center" style="display: none;">Código de Contrato</th>
												<th align="center" style="display: none;">Folio</th>
												<th align="center" style="display: none;">Objeto Contrato</th>
												<th align="center" style="display: none;">nameDB</th>
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
		<input type="hidden" id="cIdRFCRecision" name="cIdRFCRecision" value=""/>
		<input type="hidden" id="cRazonSocialRecision" name="cRazonSocial" value=""/>
		<input type="hidden" id="cEjercicioContrato" name="cEjercicioContrato" value=""/>
		<input type="hidden" id="cIdContratoDefinitivo" name="cIdContratoDefinitivo" value=""/>
		<input type="hidden" id="folioCaso" name="folioCaso" value=""/>
		<input type="hidden" id="operacion" name="operacion" value="5"/>
		<input type="hidden" id="cNameDB" name="cNameDB" value=""/>
		<input type="hidden" id="nTipoTerminacionCont" name="nTipoTerminacionCont" value="3"/>
		<input type="hidden" id="lTienePagoPendiente" name="lTienePagoPendiente" value="1"/>
	</form>
</body>
</html>