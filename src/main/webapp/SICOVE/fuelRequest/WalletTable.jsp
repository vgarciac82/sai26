<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- 
 Action 1: Captura Tarjetas
 Action 2: Devuelve monto
-->
<c:set var="action" value="${param.action}" />
<c:set var="title" value="${param.title}" />
<input type="hidden" id="action" value="${title}">
<div class="row mt-2">
	<div class="col-sm-12">
		<div class="card w-75 mx-auto" id="registredWallets">
			<div class="card-header">${title}</div>
			<div class="card-body">
				<c:if test="${action == 2}">
				<div class="row mt-1">
					<div class="col-sm-12 w-75">
						<h6 class="h6 pt-2 pb-2 text-sm-left">Doble clic en el la
							tarjeta para registrar devolución</h6>
					</div>
					<div class="col-sm-12 w-75">
						<label for="month" class="form-label">Mes</label> <select
							id="month" class="form-select">
							<option value="1">Enero</option>
							<option value="2">Febrero</option>
							<option value="3">Marzo</option>
							<option value="4">Abril</option>
							<option value="5">Mayo</option>
							<option value="6">Junio</option>
							<option value="7">Julio</option>
							<option value="8">Agosto</option>
							<option value="9">Septiembre</option>
							<option value="10">Octubre</option>
							<option value="11">Noviembre</option>
							<option value="12">Diciembre</option>
						</select>
					</div>
				</div>
				</c:if>
				<div class="row mt-2">
					<table id="walletsDataTable" class="table table-striped" style="width:100%">
						<thead>
							<tr>
								<th>ID</th>
								<th>Num. Tarjeta</th>
								<th>Placa Vehiculo</th>
								<th>Marca</th>
								<th>Modelo</th>
								<c:choose>
							        <c:when test = "${action == 2}">
							        	<th>Saldo</th>
							        </c:when>
							   </c:choose>
							</tr>
						</thead>
					
					</table> 
				</div>
			</div>
		</div>
	</div>
</div>