<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="action" value="${param.action}" />
<c:set var="title" value="${param.title}" />

<div class="card w-75 mx-auto" id="documentManagerDiv">
	<form id="fileUploadForm" enctype="multipart/form-data">
		<input type="hidden" name="idProcess" id="idProcess" >
		<div class="card-header"><i class="fas fa-archive"></i>&nbsp;${title}</div>
		
		<div class="card-body">
			<c:choose>
				<c:when test="${action == 3}">
					<div class="row mt-1">
						<div class="input-group mb-3">
							<label class="input-group-text" for="documentSelect">
								<i class="fa-solid fa-file-pdf"></i>
							</label> 
							<select class="form-select" id="documentSelect" name="documentSelect">
								<option selected value="">Seleccione un Documento</option>
							</select> 
							<button class="btn btn-outline-secondary documentTools" type="button" onclick="showFile()">Ver Archivo</button>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<div class="row mt-1">
						<div class="input-group mb-3">
							<label class="input-group-text" for="documentSelect">
								<i class="fa-solid fa-file-pdf"></i>
							</label> 
							<select class="form-select" id="documentSelect" name="documentSelect">
								<option selected value="">Seleccione un Documento</option>
							</select> 
							<button class="btn btn-outline-secondary documentTools" type="button" onclick="showFile()" style="display: none">Ver Archivo</button>
						</div>
					</div>
					
					<div class="row mt-1 uploadFile" style="display: none">
						<div class="input-group mb-3 col-12">
							<input type="file" class="form-control" id="file" name="file" >
							<button class="btn btn-outline-secondary" type="button"  onclick="uploadFile()">Enviar</button>
						</div>
						<div class="col-12">
							<div class="progress">
							  <div class="progress-bar" id="upload-progress" role="progressbar" aria-label="Progreso de Carga" style="width: 0%;" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">0%</div>
							</div>
						</div>
					</div>
				</c:otherwise>
			 </c:choose>
		</div>
	</form>
</div>