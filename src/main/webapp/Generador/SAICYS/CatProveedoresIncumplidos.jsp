
<!DOCTYPE html>
<html>
<head>

	<title>Proveedores incumplidos</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	

	<script type="text/javascript" >
		var oTableConsulta="";
		
		var data = new FormData();
		$(document).ready(function() {
			myModal = new bootstrap.Modal(document.getElementById('modalProveedores'), {
				  keyboard: false
			});

			cargaTabla();
			agregaDatePickerFechasSancion();
		////EVENTO CLICK EN EL RENGLON DE LA TABLA DE PROVEEDORES DISPONIBLES
			$('#tblProveedoresDisponibles').on('dblclick','tr', function() {
				$(oTableConsulta.fnSettings().aoData).each(function() {
					$(this.nTr).removeClass('row_selected');
				});
				$(this).addClass('row_selected');
				var aTrs = $('#tblProveedoresDisponibles')
						.dataTable().fnGetNodes();
				for ( var i = aTrs.length; i >= 0; i--) {
					if ($(aTrs[i]).hasClass('row_selected')) {
						var nTr = $('#tblProveedoresDisponibles')
								.dataTable().fnGetData(aTrs[i]);
						$("#cProveedor").val("[[ " + nTr[0] + " ]] " + nTr[1]);
						$("#cIdRFC").val(nTr[0]);
						$("#cRazonSocial").val(nTr[1]);
						$('#cIdRFC').prop('readonly', true);
						$('#cRazonSocial').prop('readonly', true);
						myModal.hide();
						$("#rfcProveedor").val("");
						$("#rSocialProveedor").val("");
						
					}
				}
			});
		
			
		});
		
	</script>
</head>
<body>
	<form id="formCatProvIncumplido" class="needs-validation" >		
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"><h5 class="modal-title" id="exampleModalLabel">Captura de proveedores que no firmaron contrato</h5></legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="form-group col-md-12  ">
							<div class=" input-group">
								<input type="text" class="form-control" placeholder="Proveedor incumplido" id="cProveedor" name="cProveedor" readonly="readonly" required="required">
								<div class="input-group-append">
									<button class="btn btn-info" type="button" id="agregaProveedor" name="agregaProveedor" onclick="buscaProveedor()">Buscar Proveedor</button>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="input-group has-validation">
						<div class="form-group col-md-4">
	 						<label for="cIdRFC">RFC</label>
							<input type="text" class="form-control" placeholder="Ejemplo: ABCD-112211-XYZ"  id="cIdRFC" name="cIdRFC" required="required">
						</div> 
						<div class="form-group col-md-4 ">
	 						<label for="cRazonSocial">Raz&oacute;n Social</label>
							<input type="text" class="form-control" placeholder="Captura Razón Social"  id="cRazonSocial" name="cRazonSocial" required="required">
							<div class="invalid-feedback">
						        Please choose a username.
						    </div>
						</div>
						<div class="form-group col-md-4">
	 						<label for="cNoContratoCNET">No. de Contrato</label>
							<input type="text" class="form-control" placeholder="Captura Número de Contrato"  id="cNoContratoCNET" name="cNoContratoCNET">
						</div>
					</div>
				</div>
				<div class="row">
					<div class="input-group">
						<div class="form-group col-md-4">
	 						<label for="cNoProcedimientoCNET">C&oacute;digo de Procedimiento</label>
							<input type="text" class="form-control" placeholder="Captura C&oacute;digo de Procedimiento"  id="cCodProcedimientoCNET" name="cNoProcedimientoCNET">
						</div> 
						<div class="form-group col-md-4">
	 						<label for="nCodExpedienteCNET">N&uacute;mero de Expediente</label>
							<input type="text" class="form-control" placeholder="Captura N&uacute;mero de Expediente"  id="cCodExpedienteCNET" name="cCodExpedienteCNET">
						</div> 
						<div class="form-group col-md-4">
	 						<label for="nCodContratoCNET">C&oacute;digo de Contrato</label>
							<input type="text" class="form-control" placeholder="Captura C&oacute;digo de Contrato"  id="cCodContratoCNET" name="cCodContratoCNET">
						</div> 
					</div>
				</div>
				<div class=" row">
					<div class="input-group">
						<div class="form-group col-md-4">
	 						<label for="cOficioSancion">Oficio de Sanci&oacute;n</label>
							<input type="text" class="form-control" placeholder="Captura N&uacute;mero de Oficio de Sanci&oacute;n"  id="cOficioSancion" name="cOficioSancion" required="required">
						</div> 
						<div class="form-group col-md-4">
	 						<label for="fOficioSancionado">Fecha de &Oacute;ficio de Sanci&oacute;n</label>
	 						<div class="input-group date" id="fOficioSancionado" data-target-input="nearest">
								<input type="text" class="form-control datetimepicker-input" data-target="#fOficioSancionado" placeholder="dd/mm/aaaa"  id="fOficioSancion" name="fOficioSancion" value="" required="required"/>
					          	<div class="input-group-append" data-target="#fOficioSancionado" data-toggle="datetimepicker" >
					            	<div class="input-group-text"><i class="fa fa-calendar"></i></div>
					          	</div>
					        </div>
						</div> 
						<div class="form-group col-md-4">
	 						<label for="fTerminoFirmado">Fecha de Termino Para Firmar</label>
	 						<div class="input-group date" id="fTerminoFirmado" data-target-input="nearest">
								<input type="text" class="form-control datetimepicker-input" data-target="#fTerminoFirmado" placeholder="dd/mm/aaaa"  id="fTerminoFirma" name="fTerminoFirma" value="" required="required"/>
					          	<div class="input-group-append" data-target="#fTerminoFirmado" data-toggle="datetimepicker">
					            	<div class="input-group-text"><i class="fa fa-calendar"></i></div>
					          	</div>
					        </div>
						</div>
					</div>
				</div>
				<div class="row" style="display: none;">
					<div class="input-group">
						<div class="form-group col-md-12">
							<div class="custom-file" align="left">
						    	<input type="file" class="custom-file-input" id="nameArchivo" aria-describedby="inputGroupFileAddon01" >
						    	<label class="custom-file-label" for="nameArchivo" >Favor de adjuntar el archivo de evidencia con extensi&oacute;n .zip</label>
						  	</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="input-group">
					  	<div class="form-group col-md-12">
					  		<textarea class="form-control" placeholder="Favor de capturar una breve descripci&oacute;n de la causa de incumplimiento." 
					  		id="causaProveedorIncum" name="causaProveedorIncum" rows="3" title="Favor de capturar una breve descripci&oacute;n de la causa de incumplimiento."></textarea>
					  	</div>
				  	</div>
				</div>
				<div class="row">
					<div class="input-group">
					  	<div class="form-group col-auto">
							<button type="button" class="btn btn-primary" id="btnGuardarProvIncump" name="btnGuardarProvIncump"  onclick="guardar()">Guardar</button>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group" id="divConsultaDatos" >
				<div class="form-group row">
					<div class="col">
						<table id="tblConsultaDatos" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th align="center">Ejercicio<br>Fiscal de Captura</th>
									<th align="center">RFC</th>
									<th align="center">Raz&oacute;n Social</th>
									<th align="center"># Contrato</th>
									<th align="center">C&oacute;digo de Procedimiento</th>
									<th align="center"># Expediente </th>
									<th align="center">C&oacute;digo de Contrato</th>
									<th align="center">Oficio de Sanci&oacute;n</th>
									<th align="center">Fecha Ocicio de Sanci&oacute;n</th>
									<th align="center">Fecha de Termino Para Firmar</th>
									<th align="center">Fecha de Captura</th>
									<th align="center">Usuario Captura</th>
									<th align="center">Descripci&oacute;n</th>
								</tr>										
							</thead>
						</table>
					</div>
				</div>
			</div>
		</fieldset>
		<!-- Modal Proveedores-->
		<div class="modal fade bd-example-modal-lg" id="modalProveedores" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
			<div class="modal-dialog modal-auto">
		    	<div class="modal-content">
			      	<div class="modal-header">
			        	<h5 class="modal-title" id="exampleModalLabel">PROVEEDORES DISPONIBLES</h5>
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
								<input type="button" id="searchProveedor" value="Buscar" class="btn btn-primary" onclick="mostrarTablaProveedores('tblProveedoresDisponibles');"/>
			      			</div>
			       		</div>
				       	<div class="form-group">
							<div class="row">
								<div class="col-auto">
									<table id="tblProveedoresDisponibles" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
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
		<input type="hidden" id="operacion" name="operacion" value="13" />
	</form>
</body>
</html>