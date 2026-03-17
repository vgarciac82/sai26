<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="title" value="${param.title}" />
<c:set var="action" value="${param.action}" />

<div class="card w-75 mx-auto" id="walletRegistration">
	
	<div class="card-header"><i class="fa-solid fa-car"></i>&nbsp;${title}</div>
	
	<div class="card-body">
	
		<c:choose>
		
			<c:when test="${action == 1}">
				<div class="row mt-1">
					<label for="walletNum" class="offset-lg-2 col-sm-2 col-form-label text-end">N&uacute;mero de Tarjeta:</label>
					<div class="col-sm-6">
						<div class="input-group align-items-stretch">
							<span class="input-group-text" id="imgCredit">
								<i class="fa-solid fa-credit-card"></i>
							</span> 
							<input type="text" id="walletNum" name="walletNum" class="form-control" placeholder="Ingrese el Numero de la Tarjeta" aria-label="walletNum" aria-describedby="imgCredit">
						</div>
					</div>
				</div>
		
				<div class="row mt-1">
					<label for="vehicle" class="offset-lg-2 col-sm-2 col-form-label text-end">Veh&iacute;culo:</label>
					<div class="col-sm-6">
						<div class="input-group align-items-stretch">
							<span class="input-group-text" id="imgVehicle">
								<i class="fa-solid fa-car"></i>
							</span> 
							<input type="text" class="form-control helper vehicleInfo" aria-label="Click para buscar" aria-describedby="imgVehicle" id="vehicle" name="vehicle">
						</div>
					</div>
				</div>
			</c:when>
			
			<c:when test="${action == 2}">
				<div class="row mt-1">
					<label for="vehicle" class="offset-lg-2 col-sm-2 col-form-label text-end">Veh&iacute;culo:</label>
					<div class="col-sm-6">
						<div class="input-group align-items-stretch">
							<span class="input-group-text" id="imgVehicle">
								<i class="fa-solid fa-car"></i>
							</span> 
							<input type="text" class="form-control helper vehicleInfo" aria-label="Click para buscar" aria-describedby="imgVehicle" id="vehicle" name="vehicle" required value="">
							<div class="invalid-feedback">
						    	Indique el vehiculo.
						    </div>
						</div>
					</div>
				</div>
				
				<div class="row mt-1">
					<label for="walletNum" class="offset-lg-2 col-sm-2 col-form-label text-end">N&uacute;mero de Tarjeta:</label>
					<div class="col-sm-6">
						<div class="input-group align-items-stretch">
							<span class="input-group-text" id="imgCredit">
								<i class="fa-solid fa-credit-card"></i>
							</span> 
							<select id="walletNum" name="walletNum" required class="form-select vehicleInfo"  aria-label="walletNum" aria-describedby="imgCredit">
							</select>
							<div class="invalid-feedback">
						    	El vehiculo debe contar con tarjeta asignada.
						    </div>
						</div>
					</div>
				</div>
			</c:when>
			
			<c:otherwise>
					<div class="row mt-1">
					<label for="vehicle" class="offset-lg-2 col-sm-2 col-form-label text-end">Veh&iacute;culo:</label>
					<div class="col-sm-6">
						<div class="input-group align-items-stretch">
							<input type="text" class="form-control-plaintext" aria-label="Vehiculo seleccionado" id="vehicle" name="vehicle" value="" readonly>
						</div>
					</div>
				</div>
				
				<div class="row mt-1">
					<label for="walletNum" class="offset-lg-2 col-sm-2 col-form-label text-end">N&uacute;mero de Tarjeta:</label>
					<div class="col-sm-6">
						<div class="input-group align-items-stretch">
							<input type="text" id="walletNum" name="walletNum" readonly class="form-control-plaintext"   aria-label="Numero de Tarjeta" >
						</div>
					</div>
				</div>
			</c:otherwise>
			
		</c:choose>
		
		<div class="row">
		
			<label for="description" class="offset-lg-2 col-sm-2 col-form-label text-end">Descripci&oacute;n</label>
			<div class="col-sm-6">
				<input type="text" readonly class="form-control-plaintext vehicleInfo" id="description">
			</div>
			<div class="col-sm-2">&nbsp;</div>
			
			<label for="inventoryCode" class="offset-lg-2 col-sm-2 col-form-label text-end">Inventario No.</label>
			<div class="col-sm-6">
				<input type="text" readonly class="form-control-plaintext vehicleInfo" id="inventoryCode">
			</div>
			<div class="col-sm-2">&nbsp;</div>
			
			<label for="brand" class="offset-lg-2 col-sm-2 col-form-label text-end">Marca:</label>
			<div class="col-sm-6">
				<input type="text" readonly class="form-control-plaintext vehicleInfo" id="brand" >
			</div>
			<div class="col-sm-2">&nbsp;</div>
			
			<label for="subBrand" class="offset-lg-2 col-sm-2 col-form-label text-end">Modelo:</label>
			<div class="col-sm-6">
				<input type="text" readonly class="form-control-plaintext vehicleInfo" id="subBrand" >
			</div>
			<div class="col-sm-2">&nbsp;</div>
			
			<label for="model" class="offset-lg-2 col-sm-2 col-form-label text-end">A&ntilde;o:</label>
			<div class="col-sm-6">
				<input type="text" readonly class="form-control-plaintext vehicleInfo" id="model" >
			</div>
			<div class="col-sm-2">&nbsp;</div>
			
			<label for="liscencePlate" class="offset-lg-2 col-sm-2 col-form-label text-end">Placas:</label>
			<div class="col-sm-6">
				<input type="text" readonly class="form-control-plaintext vehicleInfo" id="liscencePlate" name="liscencePlate" >
			</div>
			<div class="col-sm-2">&nbsp;</div>
			
			
			<label for="resonsibleVehicle" class="offset-lg-2 col-sm-2 col-form-label text-end">Resguardante:</label>
			<div class="col-sm-6">
				<input type="text" readonly class="form-control-plaintext vehicleInfo" id="resonsibleVehicle" >
			</div>
			<div class="col-sm-2">&nbsp;</div>
			
		</div>
	</div>
</div>