<div class="modal fade" id="authRequestModal" tabindex="-1" aria-labelledby="authRequestModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="authRequestModalLabel">Autoriza Solicitud</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			</div>
			
			<div class="modal-body">
				<label for="authorizedAmount" class="col-form-label text-end">Cantidad Autorizada:</label>
				<div class="input-group align-items-stretch">
					<span class="input-group-text" id="imgAuthorizedAmount"> 
						<i class="fas fa-gas-pump"></i>
					</span> 
					<input type="text" class="form-control" aria-label="Cantidad" aria-describedby="imgAuthorizedAmount"
						id="authorizedAmount" name="authorizedAmount">
				</div>
								
			</div>
			
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
				<button type="button" class="btn btn-primary" id="authDlgBtn">Enviar</button>
			</div>
			
		</div>
	</div>
</div>


<div class="modal" tabindex="-1" role="dialog" id="rejectionModal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Motivo de Rechazo</h5>
		<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <div class="form-group">
          <label for="rejectionReason">Motivo de Rechazo:</label>
          <textarea class="form-control" id="rejectionReason" rows="3"></textarea>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" id="rejectBtn">Aceptar</button>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
      </div>
    </div>
  </div>
</div>
