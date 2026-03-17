let folio = 0;
let account = "";

const initUIFuelRequest = function(initError, idExecutiveUnit, requestAccount, requestFolio) {

    if (initError) {

        Swal.fire({
            title: 'Error',
            text: 'Ocurrio un error mientras se iniciaba el tramite. Notifique al administrador.',
            icon: 'error',
            confirmButtonText: 'Aceptar'
        });
        return;
    }

    commonContractInit(idExecutiveUnit);

    $("#loadAccountInfoBtn").click(function() {
        loadAccountInfo();
    });

    $("#saveFuelRequest").on("click", function() {
        createAccountFuelRequest();
    });

    $('#discardFuelRequest').on('click', function() {
        onDiscardFuelRequest();
    });

    $("#cleanFuelRequest").on("click", function() {
        onDiscardFuelRequest();
    });

    $("#sendFuelRequest").on("click", function() {
        onSendFuelRequest();
    });

    $('#filebrowser').on('select_node.jstree', function(e, data) {
        console.log('The user clicked on ' + data.node.id);
    });

    if (requestAccount) {
        account = requestAccount;
    }

    if (requestFolio) {
        folio = requestFolio;
        getAccountFuelRequest(folio, showInfo, onErrorProcess);

    } else {
        initialOperations();
    }
}

const initialOperations = function() {
    $("#saveFuelRequest").show();
    $("#cleanFuelRequest").show();
}

const showInfo = function(accountFuelRequest) {

    $("#amountRequested").val(accountFuelRequest.requestAmount);
    $("#justification").val(accountFuelRequest.requestJustification);
    onStatusChanged(accountFuelRequest);
}


const createAccountFuelRequest = function() {

    $('#saveFuelRequest').prop('disabled', true);


    accountFuelRequest.fuelContractAccountId = $("#idAccount").val();
    accountFuelRequest.userRequest = $("#userRequest").val();
    accountFuelRequest.requestAmount = $("#amountRequested").val();
    accountFuelRequest.requestJustification = $("#justification").val();
    accountFuelRequest.requestStatus = 1;
    if (accountFuelRequest.fuelProvisioningRequestId == 0)
        saveAccountFuelRequest(onSuccessCreateAccountRequest, onErrorCreateAccountRequest);
    else
        updateAccountFuelRequest(onSuccessCreateAccountRequest, onErrorCreateAccountRequest);
}


const onSuccessCreateAccountRequest = function(savedAccountFuelRequest) {
    Swal.fire({
        title: 'Registro Exitoso',
        text: 'Se registro exitosamente su solicitud con folio: ' + savedAccountFuelRequest.fuelProvisioningRequestId,
        icon: 'succes',
        confirmButtonText: 'Aceptar'
    });

    onStatusChanged(savedAccountFuelRequest);

}

const onStatusChanged = function(savedAccountFuelRequest) {
    if (savedAccountFuelRequest.requestStatus == FUELING_STATUS.CAPTURE)
        processCapture();
    if (savedAccountFuelRequest.requestStatus == FUELING_STATUS.DISCARDED)
        processDiscarded();
}

const processDiscarded = function() {
    Swal.fire({
        title: 'Solicitud Descartada',
        text: 'Esta solicitud fue descartada.',
        icon: 'info',
        confirmButtonText: 'Aceptar'
    });
}

const processCapture = function() {
    $('#saveFuelRequest').show();
    $('#saveFuelRequest').prop('disabled', false);
    $('#cleanFuelRequest').hide();
    $('#discardFuelRequest').show();
    $('#sendFuelRequest').show();
    $("#expedientDiv").show();
    getExpedient(accountFuelRequest.processId);
}


const onErrorCreateAccountRequest = function(errorMsg) {
    console.log(errorMsg);
    Swal.fire({
        title: 'Error',
        text: 'Ocurrio un error mientras se guardaba la solicitud.  \n Intente nuevamente, si el problema persiste notifique al adminitrador.',
        icon: 'error',
        confirmButtonText: 'Aceptar'
    });
    $('#saveFuelRequest').prop('disabled', false);
}

const loadAccountInfoByAccountNumber = function() {

    if ($('#idAccount').val() == "")
        Swal.fire({
            title: 'Sin Cuenta Seleccionada',
            text: 'Debe seleccionar una cuenta.',
            icon: 'info',
            confirmButtonText: 'Aceptar'
        });
    else
        queryFormPost({
            queryName: "accountContractInfoByNumber",
            async: true,
            callback: function() {

            }
        });
}

const onErrorProcess = function(errorMsg) {
    console.log(errorMsg);
    Swal.fire({
        title: 'Error',
        text: 'Ocurrio un error mientras se guardaba la solicitud.  \n Intente nuevamente, si el problema persiste notifique al adminitrador.',
        icon: 'error',
        confirmButtonText: 'Aceptar'
    });
}

const printRequestInfo = function(accountFuelRequest) {
    $("#amountRequested").val(accountFuelRequest.requestAmount);
    $("#justification").val(accountFuelRequest.requestJustification);
    $("#authAmount").val(accountFuelRequest.autorizedAmount);
    loadAccountInfoByAccountNumber();
}


const onDiscardFuelRequest = function() {
    Swal.fire({
        title: '¿Esta seguro de descartar esta solicitud?',
        text: "Esta acción no se puede deshacer.",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Aceptar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.value) {
            $('#discardFuelRequest').prop('disabled', true);

            if (accountFuelRequest.fuelProvisioningRequestId != 0)
                deleteAccountFuelRequest(onDiscardSuccess, onErrorProcess);
            else
                onDiscardSuccess();
        }
    })
}

const onDiscardSuccess = function() {
    Swal.fire({
        title: 'Operacion Exitosa',
        text: 'La solicitud se descarto exitosamente.',
        icon: 'success',
        confirmButtonText: 'Aceptar'
    }).then((result) => {
        parent.frames['content-iframe'].location.href = "AccountFuelRequest.jsp";
    });
}

const onSendFuelRequest = function() {
    Swal.fire({
        title: '¿Esta seguro de enviar esta solicitud?',
        text: "La solicitud se enviara a la unidad de abastecimiento para su autorización, no podra modificarla despues de enviarla.",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Aceptar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.value) {
            accountFuelRequest.requestStatus = FUELING_STATUS.AWAITING_AUTHORIZATION;
            $('#sendFuelRequest').prop('disabled', true);
            sendFuelRequest(onRequestSended, onRequestSendedError);
        }
    })
}

const onRequestSended = function() {
    Swal.fire({
        title: 'Operacion Exitosa',
        text: 'La solicitud se envio exitosamente.',
        icon: 'success',
        confirmButtonText: 'Aceptar'
    }).then((result) => {
        parent.frames['content-iframe'].location.href = "AccountFuelRequest.jsp";
    });
}

const onRequestSendedError = function(errorMsg) {
    console.log(errorMsg);
    accountFuelRequest.requestStatus = FUELING_STATUS.CAPTURE;
    Swal.fire({
        title: 'Error',
        text: 'Ocurrio un error mientras se enviaba la solicitud.  \n Intente nuevamente, si el problema persiste notifique al adminitrador.',
        icon: 'error',
        confirmButtonText: 'Aceptar'
    });
    $('#sendFuelRequest').prop('disabled', true);
}