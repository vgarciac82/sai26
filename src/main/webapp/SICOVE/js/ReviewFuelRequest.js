let lastStatus;

const showAuthInfo = function(accountFuelRequest) {

    printRequestInfo(accountFuelRequest);

    $("#rejectFuelRequest").click(function() {
        $("#rejectionModal").modal("show");
    });

    initRejectBtn();
    getCapturedExpedient(accountFuelRequest.processId);
    lastStatus = accountFuelRequest.requestStatus;

    if (accountFuelRequest.requestStatus == FUELING_STATUS.AWAITING_AUTHORIZATION) {
        onAwaitingAuthorization();
    } else if (accountFuelRequest.requestStatus == FUELING_STATUS.AWAITING_VENDOR_PROVISIONING) {
        onAwaitingVendorProvisioning();
    } else if (accountFuelRequest.requestStatus == FUELING_STATUS.AUTHORIZED) {
        onFuelingAuthorized();
    }

}

const initRejectBtn = function() {
    $("#rejectBtn").click(function() {
        Swal.fire({
            title: "¿Está seguro de rechazar la solicitud?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Sí",
            cancelButtonText: "Cancelar",
        }).then((result) => {
            let rejectionReason = $("#rejectionReason").val().trim();
            if (result.isConfirmed) {
                lastStatus = accountFuelRequest.requestStatus;
                if (rejectionReason === "") {
                    Swal.fire({
                        icon: "error",
                        text: "El motivo de rechazo es requerido",
                        confirmButtonText: "OK",
                    });
                    $("#rejectionReason").focus();
                    return;
                }


                accountFuelRequest.requestStatus = FUELING_STATUS.REJECTED;
                accountFuelRequest.rejectJustification = rejectionReason;
                $("#rejectionModal").modal("hide");
                rejectFuelRequest(onSuccesRejectRequest, onErrorRejectRequest);

            }
        });
    });

    $("#rejectionModal").on("hidden.bs.modal", function() {
        $("#rejectionReason").val("");
    });
}

const onAwaitingAuthorization = function() {
    $("#authFuelRequest").val("Autoriza Solicitud");
    $("#authFuelRequest").on("click", function() {
        sendToSupplier();
    });
    $("#authAmount").val(accountFuelRequest.requestAmount)
    $("#authAmount").focus();
}

const sendToSupplier = function() {
    Swal.fire({
        title: 'Confirme Autorización',
        text: '¿Está seguro de autorizar la solicitud de combustible?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, autorizar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            sendToVendorProvisioning()
        }
    });

}

const sendToVendorProvisioning = function() {
    accountFuelRequest.requestStatus = FUELING_STATUS.AWAITING_VENDOR_PROVISIONING;
    accountFuelRequest.autorizedAmount = $("#authAmount").val();
    updateAccountFuelRequest(onAwaitingVendorProvisioning, onErrorVendorProvisioning);
}

const onErrorVendorProvisioning = function() {
    onErrorProcess();
    accountFuelRequest.requestStatus = FUELING_STATUS.AWAITING_AUTHORIZATION;
}

const onAwaitingVendorProvisioning = function() {
    $("#authFuelRequest").text("Termina Solicitud");
    $("#authFuelRequest").on("click", function() {
        authorizeFueling();
    });
}

const authorizeFueling = function() {

    Swal.fire({
        title: 'Confirme Autorización',
        text: '¿Está seguro de cambiar el estatus a Autorizado en la solicitud de combustible?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, autorizar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            sendToAuthorizeProvisioning()
        }
    });

}

const sendToAuthorizeProvisioning = function() {
    let currentDate = new Date();
    accountFuelRequest.requestStatus = FUELING_STATUS.AUTHORIZED;
    accountFuelRequest.autorizationDate = currentDate;
    authAccountFuelRequest(onFuelingAuthorized, onErrorAuthorizeProvisioning)
}

const onFuelingAuthorized = function() {
    Swal.fire({
        icon: 'success',
        title: '¡Éxito!',
        text: 'La solicitud ha sido autorizada'
    }).then((result) => {
        parent.frames['content-iframe'].location.href = "FuelingList.jsp?INBOX_TYPE=2";
    });
}

const onErrorAuthorizeProvisioning = function() {
    onErrorProcess();
    accountFuelRequest.requestStatus = FUELING_STATUS.AWAITING_VENDOR_PROVISIONING;
}

const onSuccesRejectRequest = function() {
    Swal.fire({
        title: "Éxito",
        text: "La solicitud se rechazó exitosamente.",
        icon: "success",
        confirmButtonText: "OK",
    }).then((result) => {
        parent.frames['content-iframe'].location.href = "FuelingList.jsp?INBOX_TYPE=2";
    });
}

const onErrorRejectRequest = function() {
    onErrorProcess();
    accountFuelRequest.requestStatus = lastStatus;
}