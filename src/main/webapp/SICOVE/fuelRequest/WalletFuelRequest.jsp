<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="action" value="${param.action}" />
<c:set var="title" value="${param.title}" />

<div class="card w-75 mx-auto" id="requestDatailCard">
	<div class="card-header"><i class="fa-solid fa-gas-pump"></i>&nbsp;${title}</div>
	<c:choose>
		<c:when test="${action == 3}">
			
			<div class="card-body">
				 
				<div class="row mt-1">
					<label for="requestAmmount" class="offset-lg-2 col-sm-4 col-form-label text-end">Cantidad Solicitada:</label>
					<div class="col-sm-6">
						<div class="input-group align-items-stretch">
							<input type="text" class="form-control-plaintext" id="requestAmmount" name="requestAmmount" readonly>
						</div>
					</div>
				</div>
				
				<div class="row mt-1">
					<label for="estimatedDistance" class="offset-lg-2 col-sm-4 col-form-label text-end">Recorrido Estimado (KM):</label>
					<div class="col-sm-6">
						<div class="input-group align-items-stretch">
							<input type="text" class="form-control-plaintext" readonly id="estimatedDistance" name="estimatedDistance">
						</div>
					</div>
				</div>
			</div>
			
		</c:when>
		<c:otherwise>
			<div class="card-body">
				 
				<div class="row mt-1">
					<label for="requestAmmount" class="offset-lg-2 col-sm-2 col-form-label text-end">Cantidad Solicitada:</label>
					<div class="col-sm-6">
						<div class="input-group align-items-stretch">
							<span class="input-group-text" id="imgRequestAmmount"> 
								<i class="fa-solid fa-gas-pump"></i>
							</span> 
								<input type="number" required class="form-control requestFuelData" aria-label="Cantidad Solicitada" placeholder="Cantidad Solicitada" aria-describedby="imgRequestAmmount" id="requestAmmount" name="requestAmmount">
								<div class="invalid-feedback">
							    	Solo numeros positivos.
							    </div>
						</div>
					</div>
				</div>
				
				<div class="row mt-1">
					<label for="estimatedDistance" class="offset-lg-2 col-sm-2 col-form-label text-end">Recorrido Estimado (KM):</label>
					<div class="col-sm-6">
						<div class="input-group align-items-stretch">
							<span class="input-group-text" id="imgEstimatedDistance"> 
								<i class="fa-solid fa-gauge-simple-high"></i>
							</span> 
								<input type="text" class="form-control requestFuelData" required aria-label="Recorrido Estimado" placeholder="Kilometros" aria-describedby="imgEstimatedDistance" id="estimatedDistance" name="estimatedDistance">
								<div class="invalid-feedback">
							    	Solo numeros positivos.
							    </div>
						</div>
					</div>
				</div>
				
			</div>
		</c:otherwise>
	</c:choose>
</div>