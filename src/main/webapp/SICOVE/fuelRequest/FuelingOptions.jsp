<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="action" value="${param.action}" />
<c:set var="requestFolio" value="${param.requestFolio}" />

<div class="card-body">
	<div class="row m-2 p-2">
		<div class="col-sm-4 text-end">
			<label for="authAmount" class="col-form-label">
				Monto Autorizado:
			</label>
		</div>
		<div class="col-sm-4">
			<div class="input-group">
				<span class="input-group-text" id="imgFuel"> 
						<i class="fa-solid fa-gas-pump"></i>
					</span> 
				<input type="text" id="authAmount" name="authAmount"  class="form-control" placeholder="Cantidad Autorizada" aria-label="CantidadAutorizada" aria-describedby="imgFuel">
			</div>
		</div>
	</div>

	<div class="row mt-1">
		<div class="col-4 offset-lg-4">
			<button class="btn btn-primary" type="button" id="authFuelRequest">Autorizar</button>
			<button class="btn btn-danger" type="button"  id="rejectFuelRequest">Rechazar</button>
		</div>
	</div>

</div>