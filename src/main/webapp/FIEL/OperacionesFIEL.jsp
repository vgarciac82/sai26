<div id="operacionesDiv" class="container my-5" style="display: none;">
	<div   class="card m-3"  >
		<div class="card-header ">
			<h5 class="mb-0">Operaciones Firma Electronica</h5>
		</div>
	    <div class="card-body">
			<div class="row justify-content-center g-2">
				<div class="col-auto">
					<button title="Rechazar Pago" type="button" class="btn btn-outline-secondary" id="rechazaPago"> Rechazar pago </button>
				</div>
				<div class="col-auto">
					<button title="Autorizar Pago" type="button" class="btn btn-primary" id="autorizaPago">Autorizar pago</button>
				</div>
			</div>
		</div>
	</div>
</div>    
        
<!-- MODAL FIEL -->
<div class="modal fade" tabindex="-1" role="dialog" id="dlg-FIEL"
	data-bs-keyboard="true" data-bs-backdrop="static">
	<form id="autorizaLayouts" name="autorizaLayouts" method="POST"
		action="../firmaSolicitudPago" enctype="multipart/form-data">

		<!-- Hidden fields -->
			<input id="urlRetorno" name="urlRetorno" type="hidden" value="Generador/ResumenPagos.jsp"> 
			<input id="tipoPagoSeleccionado" name="tipoPagoSeleccionado" type="hidden" value=""> 
			<input id="folder" name="folder" type="hidden" value="">
			<input id="loginFirma" name="loginFirma" type="hidden" value=""> 
			<input id="ueFirma" name="ueFirma" type="hidden" value=""> 
			<input id="rfcFirma" name="rfcFirma" type="hidden" value=""> 
			<input id="tipoAutorizacion" name="tipoAutorizacion" type="hidden" value="">
			<input id="nFolios" name="nFolios" type="hidden" value="">

		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title mb-0">Ingrese su Firma Electrónica</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>

				<div class="modal-body">
					<!-- .cer -->
					<div class="row mb-3 justify-content-center">
						<div class="col-12 col-md-10">
							<label for="cerFile" class="form-label">Archivo *.cer</label> <input
								type="file" id="cerFile" name="cerFile"
								class="form-control form-control-sm dlgFielInpt">
						</div>
					</div>

					<!-- .key -->
					<div class="row mb-3 justify-content-center">
						<div class="col-12 col-md-10">
							<label for="keyFile" class="form-label">Archivo *.key</label> <input
								type="file" id="keyFile" name="keyFile"
								class="form-control form-control-sm dlgFielInpt">
						</div>
					</div>

					<!-- Password -->
					<div class="row justify-content-center">
						<div class="col-12 col-md-5">
							<label for="passwordLlave" class="form-label">Password</label> <input
								type="password" id="passwordLlave" name="passwordLlave"
								class="form-control form-control-sm">
						</div>
					</div>
				</div>

				<div class="modal-footer">
					<button type="button" class="btn btn-primary" id="bntAceptar">
						Aceptar</button>
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal" id="btnCerrarDlg">Cerrar</button>
				</div>
			</div>
		</div>
	</form>
</div>


<!-- Mensajes / Log -->
<div class="modal fade" id="dlg-Msg" tabindex="-1" aria-labelledby="dlg-Msg-Label" aria-hidden="true">
    <div class="modal-dialog modal-lg"> <div class="modal-content">
            
            <div class="modal-header">
                <h5 class="modal-title" id="dlg-Msg-Label">Resultado de la Operación</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            
            <div class="modal-body">
                
                <div id="fielWarning" class="alert alert-warning d-none" role="alert">
                    <h6 class="alert-heading mb-1">Firma a punto de expirar</h6>
                    <div id="msgWarning" class="mb-0">
                    </div>
                </div>

                <div class="card">
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table id="mnLogTbl" class="table table-bordered table-sm mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th>Log</th>
                                    </tr>
                                </thead>
                                <tbody>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                </div>
            
        </div>
    </div>
</div>
