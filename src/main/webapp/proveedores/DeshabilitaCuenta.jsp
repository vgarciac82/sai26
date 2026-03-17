<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%!
	private static final Logger log = LoggerFactory.getLogger("DeshabilitaCuenta.jsp");%>
<%

ConfiguraAplicativoBusinessLogic systemConfig = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
if (u == null) {
	response.sendRedirect("../index.jsp");
	return;
}


String employee = u.getLogin();
String urlService = systemConfig.getSystemSetting( "URL_BENEFICIARY_WS" );
log.info( employee + " entró a deshabilitar una cuenta." );
log.info("URL Servicio Beneficiarios: " + urlService);

%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Búsqueda de Beneficiario</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css"
	rel="stylesheet">
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="bg-light p-4">
	<div class="container">
		<!-- Card 1 -->
		<div class="card mb-4">
			<div class="card-header fw-bold">Búsqueda de Beneficiario</div>
			<div class="card-body">
				<div class="row mb-3">
					<label class="col-sm-2 col-form-label">RFC</label>
					<div class="col-sm-6">
						<input type="text" id="inputRFC" class="form-control"
							placeholder="Ingrese RFC">
					</div>
					<div class="col-sm-4">
						<button id="btnBuscar" class="btn btn-primary">
							<i class="fas fa-search"></i> Buscar
						</button>
					</div>
				</div>
				<div class="text-center mb-3 d-none" id="spinner">
					<div class="spinner-border text-primary" role="status">
						<span class="visually-hidden">Cargando...</span>
					</div>
				</div>
				<div class="row mb-2">
					<label class="col-sm-2 col-form-label">Nombre</label>
					<div class="col-sm-10">
						<input type="text" id="nombre" class="form-control-plaintext"
							readonly>
					</div>
				</div>
				<div class="row mb-2">
					<label class="col-sm-2 col-form-label">Apellido Paterno</label>
					<div class="col-sm-10">
						<input type="text" id="apellidoPaterno"
							class="form-control-plaintext" readonly>
					</div>
				</div>
				<div class="row">
					<label class="col-sm-2 col-form-label">Apellido Materno</label>
					<div class="col-sm-10">
						<input type="text" id="apellidoMaterno"
							class="form-control-plaintext" readonly>
					</div>
				</div>
			</div>
		</div>

		<!-- Card 2 -->
		<div class="card">
			<div class="card-header fw-bold">Cuentas Bancarias</div>
			<div class="card-body">
				<table class="table table-bordered" id="tblBankAccounts">
					<thead class="table-light">
						<tr>
							<th>CLABE</th>
							<th>Banco</th>
							<th>Estatus</th>
							<th>Acciones</th>
						</tr>
					</thead>
					<tbody>
						<!-- Dinámico -->
					</tbody>
				</table>
			</div>
		</div>
	</div>

	<script>
	const employee = "<%=employee%>";
	const URL_SERVICE = "<%=urlService%>"
    function limpiarCampos() {
        $('#nombre').val('');
        $('#apellidoPaterno').val('');
        $('#apellidoMaterno').val('');
        $('#tblBankAccounts tbody').empty();
    }

    $(document).ready(function () {
        $('#btnBuscar').click(function () {
            const rfc = $('#inputRFC').val().trim();
            if (!rfc) {
                Swal.fire('Atención', 'Ingrese un RFC', 'warning');
                return;
            }

            limpiarCampos();
            $('#spinner').removeClass('d-none');

            $.get( URL_SERVICE + '/api/beneficiaries/by-rfc?rfc=' +rfc)
                .done(function (beneficiario) {
                    $('#nombre').val(beneficiario.firstName);
                    $('#apellidoPaterno').val(beneficiario.lastName);
                    $('#apellidoMaterno').val(beneficiario.middleName);

                    $('#tblBankAccounts tbody').empty();
                    $.get(URL_SERVICE + '/api/bank-accounts/' + rfc )
                        .done(function (cuentas) {
                            const tbody = $('#tblBankAccounts tbody');
                            cuentas.forEach(c => {
                                const estatus = c.status === 1 ? 'Activo' : 'Inactivo';
                                const trashIcon = c.status === 1
                                    ? "<button class='btn btn-sm btn-danger btnEliminar' data-id='" + c.subCuentaBancaria + "'><i class='fas fa-trash'></i></button>"
                                    : '';

                                const row = '' +
                                    '<tr>'  +
                                    '    <td>' + c.subCuentaBancaria + '</td>' +
                                    '    <td>' + c.bankName + '</td>' +
                                    '    <td>' + estatus + '</td>' +
                                    '    <td>' + trashIcon + '</td>' +
                                    '</tr>';
                                tbody.append(row);
                            });
                        })
                        .fail(() => Swal.fire('Error', 'No se pudieron cargar las cuentas', 'error'))
                        .always(() => $('#spinner').addClass('d-none'));
                })
                .fail(function () {
                    $('#spinner').addClass('d-none');
                    Swal.fire('Error', 'No se encontró el beneficiario', 'error');
                });
        });

        $(document).on('click', '.btnEliminar', function () {
            const id = $(this).data('id');
            Swal.fire({
                title: 'Confirmación',
                text: 'Escriba ELIMINAR para confirmar la acción',
                input: 'text',
                inputPlaceholder: 'Escriba ELIMINAR',
                showCancelButton: true,
                confirmButtonText: 'Confirmar',
                cancelButtonText: 'Cancelar',
                preConfirm: (value) => {
                    if (value !== 'ELIMINAR') {
                        Swal.showValidationMessage('Debe escribir exactamente: ELIMINAR');
                    }
                }
            }).then(result => {
                if (result.isConfirmed) {
                    $.ajax({
                        url: URL_SERVICE + '/api/bank-accounts/' + id + '/status?status=0&username='+employee,
                        type: 'PATCH'
                    }).done(function () {
                        Swal.fire('Listo', 'Cuenta desactivada', 'success');
                        $('#btnBuscar').click();
                    }).fail(function () {
                        Swal.fire('Error', 'No se pudo desactivar la cuenta', 'error');
                    });
                }
            });
        });
    });
</script>

</body>
</html>
