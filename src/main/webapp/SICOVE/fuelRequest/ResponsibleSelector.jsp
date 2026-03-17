<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="action" value="${param.action}" />
<c:set var="title" value="${param.title}" />

<div class="card w-75 mx-auto" id="userSelection">
	<div class="card-header"><i class="fa-solid fa-user-tie"></i>&nbsp;${title}</div>
	<div class="card-body">
		<c:choose>
		
			<c:when test="${action == 3}">
				<div class="row mt-1">
					<label for="employeeResponsibleName" class="offset-lg-2 col-sm-2 col-form-label text-end">Responsable:</label>
					<div class="col-sm-6">
						<div class="input-group align-items-stretch">
							<input type="hidden" size="10" name="idUnit" id="idUnit" value="${ idExecutiveUnit }">
							<input type="hidden" size="10" name="executiveUnit" id="executiveUnit" value="${executiveUnit}">
							<input type="text" class="form-control-plaintext" aria-label="Empleado Responsable"  id="employeeResponsibleName" name="employeeResponsibleName" readonly>
							<input type="hidden" name="employeeNumber" id="employeeNumber">
						</div>
					</div>
				</div>
				
				<div class="row mt-1">
					<label for="position" class="offset-lg-2 col-sm-2 col-form-label text-end">Cargo:</label>
					<div class="col-sm-6">
						<div class="input-group align-items-stretch">
							<input type="text" class="form-control-plaintext" id="position" readonly>
						</div>
					</div>
				</div>
			</c:when>
			
			<c:otherwise>
				<div class="row mt-1">
					<label for="employeeResponsibleName" class="offset-lg-2 col-sm-2 col-form-label text-end">Responsable:</label>
					<div class="col-sm-6">
						<div class="input-group align-items-stretch">
							<span class="input-group-text"  >
								<i class="fa-solid fa-user-tie"></i>
							</span> 
							<input type="hidden" size="10" name="idUnit" id="idUnit" value="${ idExecutiveUnit }">
							<input type="hidden" size="10" name="executiveUnit" id="executiveUnit" value="${executiveUnit}">
							<input type="text" class="form-control helper responsibleInput" aria-label="Click para buscar"   id="employeeResponsibleName" name="employeeResponsibleName">
							<input type="hidden" name="employeeNumber" id="employeeNumber">
						</div>
					</div>
				</div>
				<div class="row mt-1">
					<label for="position" class="offset-lg-2 col-sm-2 col-form-label text-end">Cargo:</label>
					<div class="col-sm-6">
						<div class="input-group align-items-stretch">
							<span class="input-group-text"  >
								<i class="fa-solid fa-address-card"></i>
							</span> 
							<input type="text" class="form-control" id="position" readonly="readonly">
						</div>
					</div>
				</div>
			</c:otherwise>
			
		</c:choose>
		
		
	</div>
</div>