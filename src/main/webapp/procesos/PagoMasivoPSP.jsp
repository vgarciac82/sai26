<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%!private static final Logger log = LoggerFactory.getLogger("AccountAsignationReport.jsp");%>
<%
Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
if (u == null) {
	response.sendRedirect("../index.jsp");
	return;
}
%>
<%@ taglib prefix="custom" tagdir="/WEB-INF/tags"%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Pago Masivo de PSPs</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
	<input type="hidden" id="employeeLoading" value="<%=u.getLogin()%>">
	<div class="container mt-4">
		<h2 class="mb-3">Pago Masivo de PSPs</h2>

		<div class="card mb-4">
			<div class="card-header">Firmantes</div>
			<div class="card-body">
				<h5 class="mt-3">Seleccione Firmante Visto Bueno</h5>
				<custom:firmante type="VOBO" ur="<%=u.getU_UR()%>"
					modulo="PAGODIVERSO" />

				<h5 class="mt-3">Seleccione Firmante Autoriza</h5>
				<custom:firmante type="AUT" ur="<%=u.getU_UR()%>"
					modulo="PAGODIVERSO" />

				<div class="form-check mt-3">
					<input class="form-check-input" type="checkbox"
						id="chkSuplenteVoBo"> <label class="form-check-label"
						for="chkSuplenteVoBo">Delega Vo Bo</label>
				</div>
				<div id="suplenteVoBoContainer" class="d-none">
					<h5 class="mt-3">Seleccione Firmante Suplente Visto Bueno</h5>
					<custom:suplenteFirmante type="SUPVOBO" ur="<%=u.getU_UR()%>"
						modulo="PAGODIVERSO" />
				</div>

				<div class="form-check mt-3">
					<input class="form-check-input" type="checkbox" id="chkSuplenteAut">
					<label class="form-check-label" for="chkSuplenteAut">Delega
						Autorización</label>
				</div>
				<div id="suplenteAutContainer" class="d-none">
					<h5 class="mt-3">Seleccione Firmante Suplente Autoriza</h5>
					<custom:suplenteFirmante type="SUPAUT" ur="<%=u.getU_UR()%>"
						modulo="PAGODIVERSO" />
				</div>


			</div>
		</div>

		<div class="card mb-4">
			<div class="card-header">Facturas</div>
			<div class="card-body">
				
				<h5 class="mt-3">Seleccione la partida y el archivo con las facturas e
					informe de comisión</h5>
				
				<label for="budgetItem" class="form-label mt-3">Partida de pago</label>
				<select id="budgetItem" class="form-select" required="required">
					<option value=""> Seleccione Partida </option>
					<option value="33103"> 33103 - Consultorías para programas o proyectos financiados por organismos internacionales </option>
					<option value="33104"> 33104 - Otras asesorías para la operación de programas </option>
				</select>
				
				<label for="fileUpload" class="form-label mt-3">Facturas:</label>
				<input type="file" class="form-control mb-3" id="fileUpload">
				
				<label for="fileUpload2" class="form-label mt-3">Formatos 32D - SAT:</label>
				<input type="file" class="form-control mb-3" id="fileUpload2">
				
				<div class="progress mt-3 d-none" id="progressContainer">
					<div id="progressBar" class="progress-bar" role="progressbar"
						style="width: 0%;" aria-valuenow="0" aria-valuemin="0"
						aria-valuemax="100">0%</div>
				</div>
				
				
				<div class="progress mt-3 d-none" id="progressContainer">
					<div id="progressBar" class="progress-bar" role="progressbar"
						style="width: 0%;" aria-valuenow="0" aria-valuemin="0"
						aria-valuemax="100">0%</div>
				</div>
			</div>
		</div>

		<div class="text-center mt-4">
			<button class="btn btn-primary" id="btnEnviar">
				<span id="btnText">Enviar</span>
				<span id="btnSpinner" class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
			</button>
		</div>
		
		<div class="card mb-4 d-none" id="resultContainer">
			<div class="card-header">Resultado del Proceso</div>
			<div class="card-body">
				<table class="table table-striped">
					<thead>
						<tr>
							<th>Folio</th>
							<th>Archivo</th>
							<th>Log</th>
							<th>Recibo</th>
							<th>Estado</th>
						</tr>
					</thead>
					<tbody id="resultTable">
					</tbody>
				</table>
			</div>
		</div>

		
	</div>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<script>
	
	$(document).ready(function(){

		$('#chkSuplenteVoBo').change(function() {
            $('#suplenteVoBoContainer').toggleClass('d-none', !this.checked);
        });

        $('#chkSuplenteAut').change(function() {
            $('#suplenteAutContainer').toggleClass('d-none', !this.checked);
        });

        $('.firmante-select').change(function() {
            let puestoInput = $('#' + $(this).data('puesto-id'));
            let empleadoId = $(this).val();
            
            if (puestos[empleadoId]) {
                puestoInput.val(puestos[empleadoId]);
            } else {
                puestoInput.val('');
            }
        });
        
        $('#btnEnviar').click(function() {

            let fileInvoices = $('#fileUpload')[0].files[0];
            let fileOpinions = $('#fileUpload2')[0].files[0];

            let authEmployee = $('.firmante-select[data-puesto-id="puesto_AUT"]').val();
            let voBoEmployee = $('.firmante-select[data-puesto-id="puesto_VOBO"]').val();

            if (!fileInvoices) {
                Swal.fire('Error', 'Debe seleccionar el archivo de Facturas.', 'error');
                return;
            }

            if (!fileOpinions) {
                Swal.fire('Error', 'Debe seleccionar el archivo de Formatos 32D - SAT.', 'error');
                return;
            }

            if (!authEmployee || !voBoEmployee) {
                Swal.fire('Error', 'Debe seleccionar los firmantes.', 'error');
                return;
            }

            if ($("#budgetItem").val() === "") {
                Swal.fire('Error', 'Debe elegir la partida del pago', 'error');
                return;
            }

            let formData = new FormData();
            formData.append('authEmployeeNumber', authEmployee);
            formData.append('voBoEmployeeNumber', voBoEmployee);

            formData.append('invoices', fileInvoices);
            formData.append('opinions', fileOpinions);

            formData.append('budgetItem', $("#budgetItem").val());
            formData.append('employeeLoading', $("#employeeLoading").val());

            // ===============================
            // VALIDACIÓN DE SUPLENTES
            // ===============================

            if ($('#chkSuplenteVoBo').is(':checked')) {

                let suplenteVoBo = $('.firmante-select[data-puesto-id="puesto_SUPVOBO"]').val();
                let officeNumberVoBo = $('#suplenteVoBoContainer input[type="text"]').eq(0).val();
                let officeDateVoBo = $('#suplenteVoBoContainer input[type="date"]').eq(0).val();
                let reasonVoBo = $('#Motivo_SUPVOBO').val();

                if (!suplenteVoBo || !officeNumberVoBo || !officeDateVoBo || !reasonVoBo) {
                    Swal.fire('Error', 'Debe completar toda la información del Suplente Vo Bo.', 'error');
                    return;
                }

                formData.append('isSubtitutionVoBo', 'true');
                formData.append('substitApprovalEmployeeNumber', suplenteVoBo);
                formData.append('substitApprovalOfficeNumber', officeNumberVoBo);
                formData.append('substitApprovalOfficeDate', officeDateVoBo);
                formData.append('substitApprovalReason', reasonVoBo);
            }

            if ($('#chkSuplenteAut').is(':checked')) {

                let suplenteAuth = $('.firmante-select[data-puesto-id="puesto_SUPAUT"]').val();
                let officeNumberAuth = $('#suplenteAutContainer input[type="text"]').eq(0).val();
                let officeDateAuth = $('#suplenteAutContainer input[type="date"]').eq(0).val();
                let reasonAuth = $('#Motivo_SUPAUT').val();

                if (!suplenteAuth || !officeNumberAuth || !officeDateAuth || !reasonAuth) {
                    Swal.fire('Error', 'Debe completar toda la información del Suplente Autorizador.', 'error');
                    return;
                }

                formData.append('isSubtitutionAuth', 'true');
                formData.append('substitAuthorizationEmployeeNumber', suplenteAuth);
                formData.append('substitAuthorizationOfficeNumber', officeNumberAuth);
                formData.append('substitAuthorizationOfficeDate', officeDateAuth);
                formData.append('substitAuthorizationReason', reasonAuth);
            }

            // ===============================
            // CONFIRMACIÓN
            // ===============================

            Swal.fire({
                title: '¿Está seguro?',
                text: 'Esta acción enviará los pagos.',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: 'Sí, enviar',
                cancelButtonText: 'Cancelar'
            }).then((result) => {

                if (result.isConfirmed) {

                    $('#progressContainer').removeClass('d-none');
                    $('#progressBar').css('width', '0%').text('0%');

                    $("#btnEnviar").prop('disabled', true);
                    $('#btnText').text('Enviando...');
                    $('#btnSpinner').removeClass('d-none');

                    $.ajax({
                        url: '../payments/masivePayment',
                        type: 'POST',
                        data: formData,
                        processData: false,
                        contentType: false,
                        xhr: function() {
                            let xhr = new window.XMLHttpRequest();
                            xhr.upload.addEventListener('progress', function(e) {
                                if (e.lengthComputable) {
                                    let percent = Math.round((e.loaded / e.total) * 100);
                                    $('#progressBar').css('width', percent + '%').text(percent + '%');
                                    if (percent === 100) {
                                        $('#progressBar').text('Procesando pagos...');
                                    }
                                }
                            });
                            return xhr;
                        },
                        success: function(response) {
                            $('#progressContainer').addClass('d-none');
                            $('#resultContainer').removeClass('d-none');
                            $('#resultTable').empty();

                            response.forEach(item => {
                                let row = "<tr>" 
                                    + "<td>" + (item.folio == null ? "-" : item.folio) + "</td>"
                                    + "<td>" + item.fileName + "</td>" 
                                    + "<td>" + item.log + "</td>" 
                                    + "<td>" + item.recipt + "</td>"
                                    + "<td>" + (item.success ? "✅ Éxito" : "❌ Error") + "</td>"
                                    + "</tr>";

                                $('#resultTable').append(row);
                            });

                            Swal.fire('Completado', 'Los pagos fueron procesados.', 'success');
                        },
                        error: function() {
                            Swal.fire('Error', 'Ocurrió un error en el proceso.', 'error');
                        },
                        complete: function (){
                            $('#btnEnviar').prop('disabled', false);
                            $('#btnText').text('Enviar');
                            $('#btnSpinner').addClass('d-none');
                        }
                    });
                }
            });
        });
        
		 
	});
		

	</script>
</body>
</html>
