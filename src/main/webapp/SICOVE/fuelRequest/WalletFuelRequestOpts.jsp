<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="action" value="${param.action}" />
<c:set var="title" value="${param.title}" />


<div class="card w-75 mx-auto" id="commisionRegistration">
	<div class="card-header">${title}</div>
	<div class="card-body">
		<div class="row mt-1">
			<c:choose>
				<c:when test="${action==3}">
					<div class="col-8 offset-md-4">
						<button class="btn btn-primary" type="button" id="authRequest" style="display:none;">Autorizar</button>
						<button class="btn btn-primary" type="button" id="finishRequest" style="display:none;">Finalizar</button>
						<button class="btn btn-danger" type="button" id="rejectRequest" style="display:none;">Rechazar</button>
						<button class="btn btn-primary verificationAction" type="button" id="sendVerification" disabled>Enviar</button>
					</div>
				</c:when>
			<c:otherwise>
				<div class="col-8 offset-md-4">
					<button class="btn btn-primary" type="button" id="saveFuelRequest">Guardar</button>
					<button class="btn btn-primary" type="button" id="sendFuelRequest" disabled>Enviar</button>
					<button class="btn btn-danger" type="button" id="discardFuelRequest">Descartar</button>
				</div>
			</c:otherwise>
			</c:choose>
		</div>

	</div>
</div>
