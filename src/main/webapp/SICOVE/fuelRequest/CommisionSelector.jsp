<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="action" value="${param.action}" />
<c:set var="title" value="${param.title}" />

<div class="card w-75 mx-auto" id="commisionRegistration">
	<div class="card-header">
		<i class="fa-solid fa-earth-americas"></i>&nbsp;${title}
	</div>

	<div class="card-body">
		<c:choose>
			<c:when test="${action == 3}">
				<input class="form-check-input" type="checkbox" role="switch" id="noCommision" style="display: none">
				<div id="commisionDetailDiv">
					<div class="row mt-1">

						<label for="idAgenda" class="offset-sm-1 col-sm-4 col-form-label text-end">Comision:</label>
						<div class="col-sm-4">
							<div class="input-group align-items-stretch">
								<span class="input-group-text" id="imgidAgenda"> <i
									class="fa-solid fa-truck-plane"></i>
								</span> <input type="text" required class="form-control schedule"
									aria-label="Click para buscar" aria-describedby="imgidAgenda"
									id="idAgenda" name="idAgenda">
								<div class="invalid-feedback">La comision es un dato
									requerido.</div>
							</div>
						</div>
					</div>

					<div class="row mt-1">
						<label for="cConcepto"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Concepto
							de la comisión:</label>
						<div class="col-sm-4">
							<textarea readonly="readonly"
								class="form-control-plaintext schedule" id="cConcepto"
								name="cConcepto" style="width: 100%; height: 100px;"></textarea>
						</div>
					</div>


					<div class="row">

						<label for="fechaIniAgenda"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Fecha
							Inicial:</label>
						<div class="col-sm-4">
							<input type="text" readonly
								class="form-control-plaintext schedule" id="fechaIniAgenda">
						</div>

						<label for="fechaFinAgenda"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Fecha
							Final:</label>
						<div class="col-sm-4">
							<input type="text" readonly
								class="form-control-plaintext schedule" id="fechaFinAgenda">
						</div>

						<label for="pais"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Pais:</label>
						<div class="col-sm-4">
							<input type="text" readonly
								class="form-control-plaintext schedule" id="pais">
						</div>

						<label for="estado"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Estado:</label>
						<div class="col-sm-4">
							<input type="text" readonly
								class="form-control-plaintext schedule" id="estado">
						</div>

						<label for="municipio"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Municipio:</label>
						<div class="col-sm-4">
							<input type="text" readonly
								class="form-control-plaintext schedule" id="municipio">
						</div>

					</div>
				</div>

				<div id="noCommisionDetailDiv" style="display: none">

					<div class="row mt-1">
						<label for="requestJustification"  class="offset-sm-1 col-sm-4 col-form-label text-end">
							Justificaci&oacute;n de Solicitud:
						</label>
						<div class="col-sm-4">
							<textarea class="form-control-plaintext" id="requestJustification" name="requestJustification"  style="width: 100%; height: 100px;" readonly="readonly"></textarea>
						</div>
					</div>

					<div class="row mt-1">
						<label for="initialDate" class="offset-sm-1 col-sm-4 col-form-label text-end">
							Fecha Inicial:
						</label>
						<div class="col-sm-4">
							<div class="input-group align-items-stretch">
								<input type="text" class="form-control-plaintext" aria-label="Fecha Inicio" id="initialDate" name="initialDate">
							</div>
						</div>
					</div>

					<div class="row mt-1">
						<label for="finalDate" class="offset-sm-1 col-sm-4 col-form-label text-end">
							Fecha Final:
						</label>
						<div class="col-sm-4">
							<div class="input-group align-items-stretch">
								<input type="text" required class="form-control-plaintext" aria-label="Fecha Final" id="finalDate" name="finalDate">
							</div>
						</div>
					</div>

					<div class="row mt-1">
						<label for="state" class="offset-sm-1 col-sm-4 col-form-label text-end">
							Estado:
						</label>
						<div class="col-sm-4">
							<div class="input-group align-items-stretch">
								<input class="form-control-plaintext" id="state" name="state" type="text">
							</div>
						</div>
					</div>

					<div class="row mt-1">
						<label for="municipality" class="offset-sm-1 col-sm-4 col-form-label text-end">
							Municipio:
						</label>
						<div class="col-sm-4">
							<div class="input-group align-items-stretch">
								<input class="form-control-plaintext" id="municipality" name="municipality" type="text">
							</div>
						</div>
					</div>

				</div>

			</c:when>
			<c:otherwise>
				<div class="row mt-1">
					<div class="offset-sm-4 col-sm-6">
						<div class="form-check form-switch">
							<input class="form-check-input" type="checkbox" role="switch"
								id="noCommision"> <label class="form-check-label"
								for="noCommision">Sin Comision</label>
						</div>
					</div>
				</div>

				<div id="commisionDetailDiv">
					<div class="row mt-1">

						<label for="idAgenda"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Comision:</label>
						<div class="col-sm-4">
							<div class="input-group align-items-stretch">
								<span class="input-group-text" id="imgidAgenda"> <i
									class="fa-solid fa-truck-plane"></i>
								</span> <input type="text" required class="form-control schedule"
									aria-label="Click para buscar" aria-describedby="imgidAgenda"
									id="idAgenda" name="idAgenda">
								<div class="invalid-feedback">La comision es un dato
									requerido.</div>
							</div>
						</div>
					</div>

					<div class="row mt-1">
						<label for="cConcepto"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Concepto
							de la comisión:</label>
						<div class="col-sm-4">
							<textarea readonly="readonly"
								class="form-control-plaintext schedule" id="cConcepto"
								name="cConcepto" style="width: 100%; height: 100px;"></textarea>
						</div>
					</div>


					<div class="row">

						<label for="fechaIniAgenda"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Fecha
							Inicial:</label>
						<div class="col-sm-4">
							<input type="text" readonly
								class="form-control-plaintext schedule" id="fechaIniAgenda">
						</div>

						<label for="fechaFinAgenda"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Fecha
							Final:</label>
						<div class="col-sm-4">
							<input type="text" readonly
								class="form-control-plaintext schedule" id="fechaFinAgenda">
						</div>

						<label for="pais"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Pais:</label>
						<div class="col-sm-4">
							<input type="text" readonly
								class="form-control-plaintext schedule" id="pais">
						</div>

						<label for="estado"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Estado:</label>
						<div class="col-sm-4">
							<input type="text" readonly
								class="form-control-plaintext schedule" id="estado">
						</div>

						<label for="municipio"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Municipio:</label>
						<div class="col-sm-4">
							<input type="text" readonly
								class="form-control-plaintext schedule" id="municipio">
						</div>

					</div>
				</div>

				<div id="noCommisionDetailDiv" style="display: none">

					<div class="row mt-1">
						<label for="requestJustification"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Justificaci&oacute;n
							de Solicitud:</label>
						<div class="col-sm-4">
							<textarea class="form-control justification"
								id="requestJustification" name="requestJustification"
								style="width: 100%; height: 100px;" required></textarea>
							<div class="invalid-feedback">La justificacion es un dato
								requerido.</div>
						</div>
					</div>

					<div class="row mt-1">
						<label for="initialDate"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Fecha
							Inicial:</label>
						<div class="col-sm-4">
							<div class="input-group align-items-stretch">
								<span class="input-group-text" id="imgInitialDate"> <i
									class="fa-solid fa-calendar-days"></i>
								</span> <input type="date" required class="form-control justification"
									aria-label="Fecha Inicio" aria-describedby="imgInitialDate"
									id="initialDate" name="initialDate">
								<div class="invalid-feedback">La fecha inicial es un dato
									requerido</div>
							</div>
						</div>
					</div>

					<div class="row mt-1">
						<label for="finalDate"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Fecha
							Final:</label>
						<div class="col-sm-4">
							<div class="input-group align-items-stretch">
								<span class="input-group-text" id="imgFinalDate"> <i
									class="fa-solid fa-calendar-days"></i>
								</span> <input type="date" required class="form-control justification"
									aria-label="Fecha Final" aria-describedby="imgFinalDate"
									id="finalDate" name="finalDate">
								<div class="invalid-feedback">La fecha final es un dato
									requerido, mayor o igual a la fecha de inicio.</div>
							</div>
						</div>
					</div>

					<div class="row mt-1">
						<input type="hidden" id="country" name="country" value="146">
						<input type="hidden" id="countryName" name="countryName"
							value="México"> <label for="state"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Estado:</label>
						<div class="col-sm-4">
							<div class="input-group align-items-stretch">
								<span class="input-group-text" id="imgState"> <i
									class="fa-solid fa-flag"></i>
								</span> <select class="form-select justificationSelect" id="state"
									name="state" required>
								</select>
								<div class="invalid-feedback">El estado es un dato
									requerido.</div>
							</div>
						</div>
					</div>

					<div class="row mt-1">
						<label for="municipality"
							class="offset-sm-1 col-sm-4 col-form-label text-end">Municipio:</label>
						<div class="col-sm-4">
							<div class="input-group align-items-stretch">
								<span class="input-group-text" id="imgMunicipality"> <i
									class="fa-solid fa-flag"></i>
								</span> <select class="form-select justificationSelect"
									id="municipality" name="municipality" required>
								</select>
								<div class="invalid-feedback">El municipio es un dato
									requerido.</div>
							</div>
						</div>
					</div>

				</div>
			</c:otherwise>
		</c:choose>
	</div>
</div>