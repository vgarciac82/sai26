<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="action" value="${param.action}" />

<div class="row mt-1">
	
	<label for="amountRequested" class="offset-lg-2 col-sm-2 col-form-label text-end">
		Monto Solicitado:
	</label>
	
	<div class="col-sm-6">
		
		<div class="input-group align-items-stretch">
			<c:choose>
			  <c:when test="${action == '1'}">
				<span class="input-group-text" id="imgFuel"> 
					<i class="fa-solid fa-gas-pump"></i>
				</span> 
			  	<input type="text" id="amountRequested" name="amountRequested"  class="form-control" placeholder="Cantidad" aria-label="CantidadSolicitada" aria-describedby="imgFuel">
			  </c:when>
			  <c:otherwise>
			    <input type="text" readonly class="form-control-plaintext" id="amountRequested" name="amountRequested" value="">
			  </c:otherwise>
			</c:choose>
			
		</div>
	</div>

</div>

<div class="row mt-1">
	<label for="justification" class="offset-lg-2 col-sm-2 col-form-label text-end">
		Justificacion:
	</label>
	<div class="col-sm-6 form-floating">
		<c:choose>
		  <c:when test="${action == '1'}">
		  	<textarea class="form-control" id="justification" style="height: 200px" placeholder="Motivo de la solicitud"></textarea>
		  </c:when>
		  <c:otherwise>
		    <textarea class="form-control-plaintext" style="height: 200px"  id="justification" readonly="readonly"></textarea>
		  </c:otherwise>
		</c:choose>
	</div>
	<div class="col-sm-2">&nbsp;</div>
</div>