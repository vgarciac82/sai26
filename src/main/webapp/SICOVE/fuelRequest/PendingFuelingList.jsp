<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="header" value="${param.header}" />
<c:set var="tableId" value="${param.tableId}" />
<c:set var="title" value="${param.title}" />

<h1 class="pt-3 mt-3">${param.title}</h1>

<div class="row mt-2">
	<div class="col-sm-12">
		<div class="card w-100 mx-auto" id="accountCard">
			<div class="card-header">${param.header}</div>
			<div class="card-body">
				<div class="row">
					<table id="${param.tableId}" class="table table-striped" style="width: 100%">
						<thead>
							<tr>
								<th>Folio</th>
								<th>Tarjeta</th>
								<th>Marca</th>
								<th>Modelo</th>
								<th>Placas</th>
								<th>Solicitante</th>
								<th>Justificacion</th>
								<th>Inicio</th>
								<th>Fin</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>