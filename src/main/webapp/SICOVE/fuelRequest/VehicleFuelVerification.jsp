<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="action" value="${param.action}" />
<c:set var="title" value="${param.title}" />
<input type="hidden" id="idDetailReject" value="">
<div class="card w-75 mx-auto" id="verificationManagerDiv">
	 
		<input type="hidden" name="idProcess" id="idProcess" >
		<div class="card-header"><i class="fas fa-check"></i>&nbsp;${title}</div>
		
		<div class="card-body">
			
            <div class="row mt-1">
					<label for="authorizedAmmount" class="offset-lg-2 col-sm-4 col-form-label text-end">Cantidad Autorizada:</label>
					<div class="col-sm-6">
						<div class="input-group align-items-stretch">
							<input type="text" class="form-control-plaintext" id="authorizedAmmount" name="authorizedAmmount" readonly>
						</div>
					</div>
			</div>

            <div class="row mt-1">
                <label for="amountVerified" class="offset-lg-2 col-sm-4 col-form-label text-end">Comprobado:</label>
                <div class="col-sm-4">
                    <input type="number" required class="form-control-plaintext"  aria-label="Monto comprobado" value="0.00"  id="amountVerified" name="amountVerified" readonly>
                </div>
			</div>

            <div class="row mt-1">
                <label for="currentBalance" class="offset-lg-2 col-sm-4 col-form-label text-end">Saldo Actual en Tarjeta:</label>
                <div class="col-sm-4">
                    <div class="input-group align-items-stretch">
                        <span class="input-group-text" id="imgCurrentBalance"> 
                            <i class="fa-solid fa-credit-card"></i>
                        </span> 
                            <input type="number" required class="form-control" aria-label="Saldo Actual en Tarjeta" placeholder="0.00" aria-describedby="imgCurrentBalance" id="currentBalance" name="currentBalance">
                            <div class="invalid-feedback">
                                Solo numeros positivos.
                            </div>
                    </div>
                </div>
			</div>

            <div class="row mt-1">
                <label for="initialVehicleKilometers" class="offset-lg-2 col-sm-4 col-form-label text-end">Kilometraje Inicial:</label>
                <div class="col-sm-4">
                    <div class="input-group align-items-stretch">
                        <span class="input-group-text" id="imgInitialVehicleKms"> 
                            <i class="fa-solid fa-gauge-high"></i>
                        </span> 
                        <input type="number" required class="form-control" aria-label="Kilometraje Inicial" placeholder="0.00" aria-describedby="imgInitialVehicleKms" id="initialVehicleKilometers" name="initialVehicleKilometers">
                            <div class="invalid-feedback">
                                Solo numeros positivos.
                            </div>
                    </div>
                </div>
			</div>

            <div class="row mt-1">
                <label for="currentVehicleKms" class="offset-lg-2 col-sm-4 col-form-label text-end">Kilometraje Actual:</label>
                <div class="col-sm-4">
                    <div class="input-group align-items-stretch">
                        <span class="input-group-text" id="imgCurrentVehicleKms"> 
                            <i class="fa-solid fa-gauge-high"></i>
                        </span> 
                            <input type="number" required class="form-control" aria-label="Kilometraje Actual" placeholder="0.00" 
                            aria-describedby="imgCurrentVehicleKms" id="currentVehicleKms" name="currentVehicleKms">
                            <div class="invalid-feedback">
                                Solo numeros positivos.
                            </div>
                    </div>
                </div>
			</div>

            <div class="row mt-1">
                <label for="amountPending" class="offset-lg-2 col-sm-4 col-form-label text-end">Por Comprobar:</label>
                <div class="col-sm-4">
                    <input type="number" required class="form-control-plaintext"  aria-label="Pendiente de comprobar" value="0.00"  id="amountPending" name="amountPending" readonly>
                </div>
			</div>

            <div class="row mt-1">
                <div class="col-12 text-center">
                        <button class="btn btn-primary save" type="button" id="saveVerificationBtn" style="display: none" >Guardar</button>
                        <button class="btn btn-primary update" type="button" id="updateVerificationBtn" style="display: none" >Actualizar</button>
                        <button class="btn btn-primary update" type="button" id="addVerificationRowBtn" style="display: none" >Agregar Detalle</button>
                </div>
			</div>

            <div class="row mt-1">
                <div class="col-12">
                    <table class="table table-striped" id="verificationDetailTable">
                        <thead>
                            <tr>
                                <th scope="col" width="5%">ID</th>
                                <th scope="col" width="5%">Estatus</th>
                                <th scope="col" width="20%">Numero de Ticket</th>
                                <th scope="col" width="10%">Importe</th>
                                <th scope="col" width="13%">Fecha</th>
                                <th scope="col" width="27%">Observaciones</th>
                                <th scope="col" width="20%">Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            
                        </tbody>
                    </table>

                </div>
            </div>
		</div>
	 
</div>

<div class="modal fade" id="rejectVerifDetailModal" tabindex="-1" aria-labelledby="rejectVerifDetailModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="rejectVerifDetailModalLabel">Motivo de Rechazo</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			</div>
			
			<div class="modal-body">
				<label for="rejectJustification" class="col-form-label text-end">Indique el motivo:</label>
				<div class="input-group align-items-stretch  has-validation">
					<textarea class="form-control" aria-label="Motivo de Rechazo"  aria-describedby="justificationFeedback" id="rejectJustification" name="rejectJustification" required></textarea>
                    <div class="invalid-feedback" id="justificationFeedback">
                        El motivo es un campo requerido
                      </div>
				</div>
								
			</div>
			
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
				<button type="button" class="btn btn-primary" id="rejectDetailBtn">Enviar</button>
			</div>
			
		</div>
	</div>
</div>

<div class="modal fade" id="addVerificationModal" tabindex="-1" aria-labelledby="addVerificationModalLabel" aria-hidden="true" data-bs-backdrop="static" data-bs-keyboard="false">
    <form id="saveVerificationForm" enctype="multipart/form-data" method="post">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="addVerificationModalLabel">Agregar Detalle de Comprobación</h5>
                    <button type="button" class="btn-close verificationOps" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                
                <div class="modal-body">
                    
                    <div class="row mt-1">
                        <label for="ticketNumber" class="col-sm-4 col-form-label text-end">Numero de Ticket:</label>
                        
                        <div class="col-sm-8">
                            <div class="input-group align-items-stretch">
                                <span class="input-group-text" id="imgTicketNumber"> 
                                    <i class="fa-solid fa-ticket"></i>
                                </span> 
                                    <input type="text" required class="form-control infoTicket" aria-label="Numero de Ticket"
                                     placeholder="0000000000" aria-describedby="imgTicketNumber" id="ticketNumber" name="ticketNumber">
                                    <div class="invalid-feedback">
                                        Dato requerido
                                    </div>
                            </div>
                        </div>
                    </div>

                    <div class="row mt-1">
                        <label for="ticketAmount" class="col-sm-4 col-form-label text-end">Importe:</label>
                        <div class="col-sm-8">
                            <div class="input-group align-items-stretch">
                                <span class="input-group-text" id="imgTicketAmount"> 
                                    <i class="fa-solid fa-money-bill-1"></i>
                                </span> 
                                <input type="number" required class="form-control infoTicket" aria-label="Monto del ticket" placeholder="0.00" aria-describedby="imgTicketAmount" id="ticketAmount" name="ticketAmount" step="any">
                                <div class="invalid-feedback">
                                    Solo numeros positivos.
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row mt-1">
                        <label for="ticketDate" class="col-sm-4 col-form-label text-end">Fecha:</label>
                        <div class="col-sm-8">
                            <div class="input-group align-items-stretch">
                                <span class="input-group-text" id="imgTicketDate"> 
                                    <i class="fa-solid fa-calendar-days"></i>
                                </span> 
                                <input type="date" required class="form-control infoTicket" aria-label="Fecha del ticket"   aria-describedby="imgTicketDate" id="ticketDate" name="ticketDate">
                                <div class="invalid-feedback">
                                    La fecha es requerida
                                </div>
                            </div>
                        </div>
                    </div>

                    
                    <div class="row mt-1 uploadTicket" >
                        
                        <div class="input-group mb-3 col-sm-8">
                            
                            <input type="file" class="form-control infoTicket" required id="documentTicket" name="documentTicket" >
                            <div class="invalid-feedback">
                                El documento es requerido
                            </div>
                        </div>

                        <div class="col-12">
                            <div class="progress">
                                <div class="progress-bar" id="upload-ticket-progress" role="progressbar" aria-label="Progreso de Carga" style="width: 0%;" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">0%</div>
                            </div>
                        </div>
                    </div>
                

                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary verificationOps" id="saveVerificationDetailBtn">Guardar</button>
                    <button type="button" class="btn btn-secondary verificationOps" data-bs-dismiss="modal">Cancelar</button>
                    
                </div>
                
            </div>
        </div>
    </form>
</div>