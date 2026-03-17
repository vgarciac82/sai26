<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="action" value="${param.action}" />
<c:set var="requestAccount" value="${param.requestAccount}" />


<div class="row">
	<label for="idAccount" class="offset-lg-2 col-sm-2 col-form-label text-end">Cuenta:</label>
	
	<div class="col-sm-6">
		<div class="input-group">
			<c:choose>
			  <c:when test="${action == '1'}">
			    <select class="form-select" id="idAccount" name="idAccount" aria-label="Cuentas Registradas">
			      <option selected value="">Seleccione una cuenta</option>
			    </select>
			    <button class="btn btn-outline-secondary" type="button" id="loadAccountInfoBtn">Cargar</button>
			  </c:when>
			  <c:otherwise>
			    <input type="text" readonly class="form-control-plaintext" id="idAccount" name="idAccount" value="${requestAccount}">
			  </c:otherwise>
			</c:choose>
		</div>
	</div>
</div>

<div class="row">
	
	<label for="contractNumber" class="offset-lg-2 col-sm-2 col-form-label text-end">Contrato:</label>
	<div class="col-sm-6">
		<input type="text" readonly class="form-control-plaintext" id="contractNumber" value="">
	</div>
	<div class="col-sm-2">&nbsp;</div>
	
	
	<label for="provider" class="offset-lg-2 col-sm-2 col-form-label text-end">Proveedor:</label>
	<div class="col-sm-6">
		<input type="text" readonly class="form-control-plaintext" id="provider" value="">
	</div>
	<div class="col-sm-2">&nbsp;</div>

	<label for="employeeResponsibleName" class="offset-lg-2 col-sm-2 col-form-label text-end">Responsable:</label>
	<div class="col-sm-6">
		<input type="text" readonly class="form-control-plaintext" id="employeeResponsibleName" value="">
	</div>
	<div class="col-sm-2">&nbsp;</div>
		
	<label for="position" class="offset-lg-2 col-sm-2 col-form-label text-end">Cargo:</label>
	<div class="col-sm-6">
		<input type="text" readonly class="form-control-plaintext" id="position" value="">
	</div>
	<div class="col-sm-2">&nbsp;</div>
		
	<label for="organizationUnitName" class="offset-lg-2 col-sm-2 col-form-label text-end">Unidad:</label>
	<div class="col-sm-6">
		<input type="text" readonly class="form-control-plaintext" id="organizationUnitName" value="">
	</div>
	<div class="col-sm-2">&nbsp;</div>
		
</div>