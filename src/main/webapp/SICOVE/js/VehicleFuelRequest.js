const decimalPattern = /^\d+(\.\d+)?$/;
let currentStatus = 0;

const initVehicleFuelRequest = function(
    initError,
    idExecutiveUnit,
    fuelingRequestId
) {
    if (initError) {
        Swal.fire({
            title: "Error",
            text: "Ocurrio un error mientras se iniciaba el tramite. Notifique al administrador.",
            icon: "error",
            confirmButtonText: "Aceptar",
        });
        return;
    }

    if (fuelingRequestId == 0) {
        initElements();
    } else {
        loadWalletFuelingRequest(
            fuelingRequestId,
            onSuccesRequestLoad,
            onErrorRequestLoad
        );
    }
};

const onErrorRequestLoad = function(errCause) {
    console.log(errCause);
    Swal.fire({
        title: "Error",
        text: "Ocurrio un error mientras se cargaba informacion.  \n Intente nuevamente, si el problema persiste notifique al adminitrador.",
        icon: "error",
        confirmButtonText: "Aceptar",
    });
};

const loadVehicleInfo = function(fuelingRequestObj) {
    queryFormPost({
        queryName: "vehicleInfoRead",
        async: true,
        callback: function() {
            loadAsignationInfo();
            if( fuelingRequestObj.idStatus == CAPTURE_FUELING_REQUEST)
                getVehicleAsignedWallet( () => {$("#walletNum").val(vehicleFuelRequest.idWallet)} );
            
        },
    });
};

const loadResponsibleInfo = function() {
    queryFormPost({
        queryName: "responsibleInfoRead",
        async: true,
        callback: function() {},
    });
};

const onSuccesRequestLoad = function(fuelingRequestObj) {
    initElements();
    currentStatus = fuelingRequestObj.idStatus;

    if (vehicleFuelRequest.idStatus == CAPTURE_FUELING_REQUEST){
        getExpedient(vehicleFuelRequest.idProcess);
    }else if (
        currentStatus == WAITING_AUTHORIZATION_FUELING_REQUEST ||
        currentStatus == WAITING_SUFFICIENCY_FUELING_REQUEST ||
        currentStatus == AUTHORIZED_FUELING_REQUEST ||
        currentStatus == CAPTURE_VERIFICATION ||
        currentStatus == VALIDATING_VERIFICATION ||
        currentStatus == REJECTED_FUELING_REQUEST
    ) {
        getCapturedExpedient(vehicleFuelRequest.idProcess);
        if (currentStatus == WAITING_AUTHORIZATION_FUELING_REQUEST)
            initAuthRequest(fuelingRequestObj.fuelingAmount, nextStep);
        else if (currentStatus == WAITING_SUFFICIENCY_FUELING_REQUEST)
            initSufficiencyRequest(fuelingRequestObj.fuelingAmount, nextStep);
    }

    $("#vehicle").val(fuelingRequestObj.vehicleId);
    
    loadVehicleInfo(fuelingRequestObj);

    if( fuelingRequestObj.idStatus == CAPTURE_FUELING_REQUEST )
        $("#walletNum").val(fuelingRequestObj.walletNumber);
    else
        $("#walletNum").val(fuelingRequestObj.walletNumber + " - " + fuelingRequestObj.walletSupplierName );

    $("#employeeNumber").val(fuelingRequestObj.employeeResponsible);
    loadResponsibleInfo();

    loadJustificationInfo(fuelingRequestObj);

    $("#requestAmmount").val(fuelingRequestObj.fuelingAmount);
    $("#estimatedDistance").val(fuelingRequestObj.estimatedKilometers);
    $("#authorizedAmmount").val(fuelingRequestObj.authorizedAmount);

    processStatus();
};

const loadJustificationInfo = function(fuelingRequestObj) {
    if (currentStatus == CAPTURE_FUELING_REQUEST) {
        if (fuelingRequestObj.justification.withJustification) {
            $("#noCommision").prop("checked", true);
            onChangeCommision(
                fuelingRequestObj.justification.stateName,
                fuelingRequestObj.justification.municipalityName
            );
            $("#requestJustification").val(
                fuelingRequestObj.justification.justification
            );
            $("#initialDate").val(fuelingRequestObj.justification.initialDate);
            $("#finalDate").val(fuelingRequestObj.justification.endDate);
        } else {
            $("#idAgenda").val(fuelingRequestObj.justification.idCommision);
            $("#cConcepto").val(fuelingRequestObj.justification.justification);
            
            let iDate = new Date( fuelingRequestObj.justification.initialDate + "T00:00")
            let eDate = new Date( fuelingRequestObj.justification.endDate + "T00:00")

            $("#fechaIniAgenda").val( dateToStringMX(iDate) );
            $("#fechaFinAgenda").val( dateToStringMX(eDate) );

            $("#pais").val(fuelingRequestObj.justification.countryId);
            $("#estado").val(fuelingRequestObj.justification.stateName);
            $("#municipio").val(fuelingRequestObj.justification.municipalityName);
        }
    } else if (
        currentStatus == WAITING_AUTHORIZATION_FUELING_REQUEST ||
        currentStatus == WAITING_SUFFICIENCY_FUELING_REQUEST ||
        currentStatus == AUTHORIZED_FUELING_REQUEST ||
        currentStatus == CAPTURE_VERIFICATION ||
        currentStatus == VALIDATING_VERIFICATION ||
        currentStatus == REJECTED_FUELING_REQUEST
    ) {
        if (fuelingRequestObj.justification.withJustification) {
            $("#noCommision").prop("checked", true);
            onChangeCommision(
                fuelingRequestObj.justification.stateName,
                fuelingRequestObj.justification.municipalityName
            );
            $("#state").val(fuelingRequestObj.justification.stateName);
            $("#municipality").val(fuelingRequestObj.justification.municipalityName);
            $("#requestJustification").val(
                fuelingRequestObj.justification.justification
            );
            $("#initialDate").val(fuelingRequestObj.justification.initialDate);
            $("#finalDate").val(fuelingRequestObj.justification.endDate);
        } else {
            $("#idAgenda").val(fuelingRequestObj.justification.idCommision);
            $("#cConcepto").val(fuelingRequestObj.justification.justification);
            $("#fechaIniAgenda").val(fuelingRequestObj.justification.initialDate);
            $("#fechaFinAgenda").val(fuelingRequestObj.justification.endDate);
            $("#pais").val(fuelingRequestObj.justification.countryId);
            $("#estado").val(fuelingRequestObj.justification.stateName);
            $("#municipio").val(fuelingRequestObj.justification.municipalityName);
        }
    }
};

/*Recibe un objeto DATE, devuelve la fecha en formato dd/MM/yyyy */
const dateToStringMX = function(date) {
    
    let day = date.getDate();
    let month = date.getMonth() + 1;
    let year = date.getFullYear();

    return (day < 10 ? "0" + day : day) + "/" + (month < 10 ? "0" + month : month) + "/" + year;
    
}
const initElements = function() {
    $("#idAgenda").on("click", function() {
        onCommisionClick();
    });

    subIniciaDlgBootsprap();

    $("#vehicle").on("change", function() {
        loadAsignationInfo();
        getVehicleAsignedWallet();
    });

    $("#noCommision").on("click", function() {
        onChangeCommision();
    });

    $("#state").on("change", function() {
        loadMunicipality();
    });

    $("#saveFuelRequest").on("click", function() {
        sendFuelRequest();
    });

    $("#discardFuelRequest").on("click", function() {
        onDiscardRequest();
    });
    $("#sendFuelRequest").on("click", function() {
        nextStep();
    });
    $("#rejectRequest").on("click", function() {
        onRejectRequest();
    });

    
    $("#initialDate").on("change", function() {
        setMinimalDate();
    });

    $("#rejectBtn").click(function() {
        Swal.fire({
            title: "¿Está seguro de rechazar la solicitud?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Sí",
            cancelButtonText: "Cancelar",
        }).then((result) => {
            let rejectionReason = $("#rejectionReason").val().trim();

            if (rejectionReason === "") {
                Swal.fire({
                    icon: "error",
                    text: "El motivo de rechazo es requerido",
                    confirmButtonText: "OK",
                });
                $("#rejectionReason").focus();
                return;
            }

            if (result.isConfirmed) {
                vehicleFuelRequest.idStatus = REJECTED_FUELING_REQUEST;
                vehicleFuelRequest.rejectJustification = rejectionReason;
                $("#rejectionModal").modal("hide");
                rejectRequest(onSuccesRejectRequest, onErrorSaveFueling);
            }
        });
    });

    $("#rejectionModal").on("hidden.bs.modal", function() {
        $("#rejectionReason").val("");
    });
};

const loadAsignationInfo = function() {
    getVehicleResponsibleEmployee(
        $("#vehicle").val(),
        function(employeeInfo) {
            getEmployee(
                employeeInfo.employeeNumber,
                onSuccesLoadEmployee,
                onErrorLoad
            );
        },
        onErrorLoad
    );
};

const onErrorLoad = function(errCause) {
    console.log(errCause);
    Swal.fire({
        title: "Error",
        text: "Ocurrio un error mientras se cargaba informacion.  \n Intente nuevamente, si el problema persiste notifique al adminitrador.",
        icon: "error",
        confirmButtonText: "Aceptar",
    });
};

const onChangeCommision = function(stateName, municipalityName) {
    if (currentStatus == CAPTURE_FUELING_REQUEST) {
        if ($("#noCommision").prop("checked")) {
            cleanSchedule();
            loadStates(stateName, municipalityName);
            getVehicleAsignedWallet( () => {$("#walletNum").val(vehicleFuelRequest.idWallet)} );
            $("#noCommisionDetailDiv").show();
            $("#commisionDetailDiv").hide();
        } else {
            cleanJustification();
            $("#commisionDetailDiv").show();
            $("#noCommisionDetailDiv").hide();
        }
    } else if (currentStatus == WAITING_AUTHORIZATION_FUELING_REQUEST || currentStatus == WAITING_SUFFICIENCY_FUELING_REQUEST ||
        currentStatus == AUTHORIZED_FUELING_REQUEST || currentStatus == CAPTURE_VERIFICATION || currentStatus == VALIDATING_VERIFICATION ||
        currentStatus == REJECTED_FUELING_REQUEST
    ) {
        if ($("#noCommision").prop("checked")) {
            cleanSchedule();
            $("#noCommisionDetailDiv").show();
            $("#commisionDetailDiv").hide();
        } else {
            cleanJustification();
            $("#commisionDetailDiv").show();
            $("#noCommisionDetailDiv").hide();
        }
    } else {
        if ($("#noCommision").prop("checked")) {
            cleanSchedule();
            loadStates(stateName, municipalityName);
            $("#noCommisionDetailDiv").show();
            $("#commisionDetailDiv").hide();
        } else {
            cleanJustification();
            $("#commisionDetailDiv").show();
            $("#noCommisionDetailDiv").hide();
        }
    }
};

const cleanSchedule = function() {
    $(".schedule").val("");
};

const cleanJustification = function() {
    $(".justification").val("");
    $(".justificationSelect").empty();
};

const loadStates = function(stateName, municipalityName) {
    querySelectPost({
        queryName: "statesCatalogRead",
        targetObjectId: "state",
        async: true,
        callback: function() {
            if (stateName) {
                setSelectedText("state", stateName);
                loadMunicipality(municipalityName);
            }
        },
    });
};
const loadMunicipality = function(municipalityName) {
    $("#municipality").empty();

    querySelectPost({
        queryName: "municipalityCatalogRead",
        targetObjectId: "municipality",
        async: true,
        callback: function() {
            if (municipalityName) {
                setSelectedText("municipality", municipalityName);
            }
        },
    });
};

const sendFuelRequest = function() {
    let isValid = validateRequestFuel();

    if (isValid) {
        Swal.fire({
            title: "¿Está seguro de guardar la información?",
            showCancelButton: true,
            confirmButtonText: "Aceptar",
            cancelButtonText: "Cancelar",
        }).then((result) => {
            if (result.isConfirmed) {
                saveRequest();
            }
        });
    }
};

const saveRequest = function() {
    makeObject();
    if (vehicleFuelRequest.fuelingRequestId == 0)
        saveFuelRequest(onSuccessSaveFueling, onErrorSaveFueling);
    else if (currentStatus == CAPTURE_FUELING_REQUEST)
        updateFuelRequest(onSuccessSaveFueling, onErrorSaveFueling);
};

const makeObject = function() {
    vehicleFuelRequest.vehicleId = $("#vehicle").val();
    vehicleFuelRequest.employeeResponsible = $("#employeeNumber").val();
    vehicleFuelRequest.userRequest = $("#employeeRegistration").val();
    vehicleFuelRequest.justification.withJustification =
        $("#noCommision").is(":checked");

    if (currentStatus == 0) vehicleFuelRequest.idStatus = 1;
    else vehicleFuelRequest.idStatus = currentStatus;

    vehicleFuelRequest.walletNumber = $('#walletNum option:selected').text();
    vehicleFuelRequest.idWallet = $("#walletNum").val();

    if ($("#noCommision").is(":checked")) {
        vehicleFuelRequest.justification.idCommision = 0;
        vehicleFuelRequest.justification.justification = $(
            "#requestJustification"
        ).val();
        vehicleFuelRequest.justification.initialDate = $("#initialDate").val();
        vehicleFuelRequest.justification.endDate = $("#finalDate").val();
        vehicleFuelRequest.justification.countryId = $("#countryName").val();
        vehicleFuelRequest.justification.stateName = $("#state")
            .find("option:selected")
            .text();
        vehicleFuelRequest.justification.municipalityName = $("#municipality")
            .find("option:selected")
            .text();
    } else {
        vehicleFuelRequest.justification.idCommision = $("#idAgenda").val();
        vehicleFuelRequest.justification.justification = $("#cConcepto").val();

        vehicleFuelRequest.justification.initialDate = toISO_FROM_MXDate( $("#fechaIniAgenda").val() ) +"T00:00";
        vehicleFuelRequest.justification.endDate = toISO_FROM_MXDate( $("#fechaFinAgenda").val() ) +"T00:00";
        
        vehicleFuelRequest.justification.countryId = $("#pais").val();
        vehicleFuelRequest.justification.stateName = $("#estado").val();
        vehicleFuelRequest.justification.municipalityName = $("#municipio").val();
    }

    vehicleFuelRequest.fuelingAmount = $("#requestAmmount").val();
    vehicleFuelRequest.estimatedKilometers = $("#estimatedDistance").val();
};

const toISO_FROM_MXDate = function(date) {
    let dateParts = date.split("/");
    return dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
};

const validateRequestFuel = function() {
    let isValid = true;

    $(".vehicleInfo, .responsibleInput").each(function() {
        if ($(this).val() == "") {
            $(this).removeClass("is-valid");
            $(this).addClass("is-invalid");
            isValid = false;
        } else {
            $(this).removeClass("is-invalid");
            $(this).addClass("is-valid");
        }
    });

    $(".requestFuelData").each(function() {
        const $this = $(this);
        const hasValue = $this.val() !== "";
        const isValidDecimal =
            decimalPattern.test($this.val()) && parseFloat($this.val()) > 0;
        $this.toggleClass("is-invalid", !hasValue || !isValidDecimal);
        $this.toggleClass("is-valid", hasValue && isValidDecimal);
        isValid = isValid && hasValue && isValidDecimal;
    });

    if ($("#noCommision").is(":checked")) {
        $(".justification, .justificationSelect").each(function() {
            if ($(this).val() == "") {
                isValid = false;
                $(this).removeClass("is-valid");
                $(this).addClass("is-invalid");
            } else {
                $(this).removeClass("is-invalid");
                $(this).addClass("is-valid");
            }
        });
    } else {
        if ($("#idAgenda").val() == "") {
            isValid = false;
            $("#idAgenda").removeClass("is-valid");
            $("#idAgenda").addClass("is-invalid");
        } else {
            $("#idAgenda").removeClass("is-invalid");
            $("#idAgenda").addClass("is-valid");
        }
    }

    if ($("#initialDate").val() != "" && $("#finalDate").val() != "") {
        let endDate = new Date($("#finalDate").val());
        let initialDate = new Date($("#initialDate").val());

        if (endDate < initialDate) {
            $("#finalDate").removeClass("is-valid");
            $("#finalDate").addClass("is-invalid");
            isValid = false;
        } else {
            $("#finalDate").removeClass("is-invalid");
            $("#finalDate").addClass("is-valid");
        }
    }

    return isValid;
};

const setMinimalDate = function() {
    let initialDate = new Date($("#initialDate").val());
    $("#finalDate").prop("min", initialDate.toISOString().split("T")[0]);
};

const onSuccessSaveFueling = function(vehicleFuelRequest) {
    currentStatus = vehicleFuelRequest.idStatus;
    Swal.fire({
        title: "Éxito",
        text: "Se guardó exitosamente la solicitud",
        icon: "success",
        confirmButtonText: "OK",
    });
    processStatus();
};

const processStatus = function() {
    if (currentStatus == CAPTURE_FUELING_REQUEST) onRequestSaved();
    else if (currentStatus == WAITING_AUTHORIZATION_FUELING_REQUEST) {
        onWaitingAuthorization();
    } else if (currentStatus == WAITING_SUFFICIENCY_FUELING_REQUEST) {
        onWaitingSufficiency();
    } else if (currentStatus == AUTHORIZED_FUELING_REQUEST || currentStatus == CAPTURE_VERIFICATION) {
        onAuthorizedSufficiency();
    } else if (currentStatus == VALIDATING_VERIFICATION) {
        onValidatingVerification();
    }
};

const postAuthActions = function() {

    getPendingFuelRequestVerification();

    $("#expedientDiv").show();
    $("#verificationDiv").show();
    $("#sendVerification").on("click", function() {
        sendVerificationClick();
    });
};

const onValidatingVerification = function() {
    postAuthActions();
    $("#initialVehicleKilometers").prop("readonly",true);
    $("#currentVehicleKms").prop("readonly",true);
    $("#currentBalance").prop("readonly",true);
    $("#sendVerification").prop("disabled", false);
    $("#rejectDetailBtn").on("click", function() {
        if("" == $.trim( $("#rejectJustification").val() )){
            $("#rejectJustification").removeClass("is-valid");
            $("#rejectJustification").addClass("is-invalid");
            return;
        }
        $("#rejectJustification").removeClass("is-invalid");
        $("#rejectJustification").addClass("is-valid");
        $("#rejectVerifDetailModal").modal("hide");
        updateVerificationDetail($("#idDetailReject").val(), false, $("#rejectJustification").val(), getPendingFuelRequestVerification, onErrorLoadFuelRequestVerification);
    });
}

const onAuthorizedSufficiency = function() {

    $("#currentBalance").on("change", currentBalanceChanged);
    $("#currentBalance").trigger("change");
    postAuthActions();

    $("#addVerificationRowBtn").on("click", function() {
        clearVerificationDialog();
        captureVerification();
    });

    $("#saveVerificationBtn").on("click", function() {
        saveVerification();
    });

    $("#updateVerificationBtn").on("click", function() {
        updateVerificationClick();
    });

    $("#saveVerificationDetailBtn").on("click", function() {
        saveVerificationDetail(successVerificationUpload, onErrorFinishRequest);
    });

    $("#ticketAmount").on("change",function(){ ticketAmountChanged()});
    $("#ticketAmount").on("blur",function(){ ticketAmountChanged()});   
    $("#ticketDate").on("change",function(){ ticketDateChanged()});

    
};


const onWaitingAuthorization = function() {
    $("#authRequest").show();
    $("#rejectRequest").show();
    $("#expedientDiv").show();
};

const onWaitingSufficiency = function() {
    $("#authRequest").hide();
    $("#finishRequest").show();
    $("#rejectRequest").hide();
    $("#expedientDiv").show();
    initSufficiencyRequest();
};

const onRequestSaved = function() {
    $("#sendFuelRequest").prop("disabled", false);
    $("#expedientDiv").show();
    if (vehicleFuelRequest.idStatus == CAPTURE_FUELING_REQUEST)
        getExpedient(vehicleFuelRequest.idProcess);
    else getCapturedExpedient(vehicleFuelRequest.idProcess);
};

const onErrorSaveFueling = function(errMsg) {
    Swal.fire({
        title: "Error",
        text: "No fue posible guardar la solicitud. Intente nuevamente o reporte al administrador.",
        icon: "error",
        confirmButtonText: "OK",
    });
};

const setSelectedText = function(selectId, optionText) {
    $("#" + selectId + " option")
        .filter(function() {
            return $(this).text() == optionText;
        })
        .prop("selected", true);
};

const nextStep = function() {
    let idNextStep = calculateNextStep();
    if (idNextStep == WAITING_AUTHORIZATION_FUELING_REQUEST)
        changeRequestStatus(idNextStep, onSuccesUpdateStatus, onErrorSaveFueling);
    else if (idNextStep == WAITING_SUFFICIENCY_FUELING_REQUEST) sendAuthRequest();
    else if (idNextStep == AUTHORIZED_FUELING_REQUEST) finishRequest();
};

const calculateNextStep = function() {
    if (vehicleFuelRequest.idStatus == CAPTURE_FUELING_REQUEST)
        return WAITING_AUTHORIZATION_FUELING_REQUEST;
    else if (vehicleFuelRequest.idStatus == WAITING_AUTHORIZATION_FUELING_REQUEST)
        return WAITING_SUFFICIENCY_FUELING_REQUEST;
    else if (vehicleFuelRequest.idStatus == WAITING_SUFFICIENCY_FUELING_REQUEST)
        return AUTHORIZED_FUELING_REQUEST;
    else return vehicleFuelRequest.idStatus;
};

const onSuccesUpdateStatus = function(newStatus) {
    currentStatus = newStatus;
    if (vehicleFuelRequest.idStatus == WAITING_AUTHORIZATION_FUELING_REQUEST) {
        Swal.fire({
            title: "Éxito",
            text: "La solicitud se avanzó exitosamente.",
            icon: "success",
            confirmButtonText: "OK",
        }).then((result) => {
            parent.frames["content-iframe"].location.href =
                "FuelingWalletList.jsp?INBOX_TYPE=" + CAPTURE_FUELING_REQUEST;
        });
    } else if (
        vehicleFuelRequest.idStatus == WAITING_SUFFICIENCY_FUELING_REQUEST
    ) {
        Swal.fire({
            title: "Éxito",
            text: "La solicitud se autorizó exitosamente.",
            icon: "success",
            confirmButtonText: "OK",
        });

        processStatus();
    }
};

const sendAuthRequest = function() {
    let newStatus = calculateNextStep();
    let authorizedAmount = $("#authorizedAmount").val();

    if (
        vehicleFuelRequest.idStatus == WAITING_AUTHORIZATION_FUELING_REQUEST &&
        (!authorizedAmount || isNaN(authorizedAmount) || authorizedAmount <= 0)
    ) {
        swal(
            "Advertencia",
            "La cantidad autorizada debe ser mayor a cero",
            "warning"
        ).then(() => {
            $("#authorized-amount").focus();
        });
        return;
    }

    vehicleFuelRequest.idStatus = newStatus;
    vehicleFuelRequest.authorizedAmount = authorizedAmount;

    $("#authRequestModal").modal("hide");
    $.blockUI({ message: "Procesando..." });

    authRequest(onSuccesUpdateStatus, onErrorSaveFueling);
};

const initSufficiencyRequest = function() {
    $("#finishRequest").on("click", function() {
        nextStep();
    });
};

const finishRequest = function() {
    Swal.fire({
        title: "¿Está seguro que desea terminar la solicitud?",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Aceptar",
        cancelButtonText: "Cancelar",
    }).then((result) => {
        if (result.isConfirmed) {
            currentStatus = vehicleFuelRequest.idStatus;
            vehicleFuelRequest.idStatus = AUTHORIZED_FUELING_REQUEST;
            requestComplete(onSuccesFinishRequest, onErrorFinishRequest);
        } else if (result.dismiss === Swal.DismissReason.cancel) {
            Swal.close();
        }
    });
};

const onSuccesFinishRequest = function() {
    Swal.fire({
        title: "Éxito",
        text: "La solicitud se avanzó exitosamente.",
        icon: "success",
        confirmButtonText: "OK",
    }).then((result) => {
        parent.frames["content-iframe"].location.href =
            "FuelingWalletList.jsp?INBOX_TYPE=" +
            WAITING_AUTHORIZATION_FUELING_REQUEST;
    });
};

const onErrorFinishRequest = function(errMsg) {
    vehicleFuelRequest.idStatus = currentStatus;
    Swal.fire({
        title: "Error",
        text: "No fue posible guardar la solicitud. Intente nuevamente o reporte al administrador.",
        icon: "error",
        confirmButtonText: "OK",
    });
};

const onDiscardRequest = function() {
    Swal.fire({
        title: "¿Está seguro que desea descartar la solicitud?",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Aceptar",
        cancelButtonText: "Cancelar",
    }).then((result) => {
        if (result.isConfirmed) {
            if (currentStatus == 0) onSuccesDiscardRequest();
            else {
                discardRequest(onSuccesDiscardRequest, onErrorSaveFueling);
            }
        } else {
            Swal.close();
        }
    });
};

const onSuccesDiscardRequest = function() {
    Swal.fire({
        title: "Éxito",
        text: "La solicitud se descartó exitosamente.",
        icon: "success",
        confirmButtonText: "OK",
    }).then((result) => {
        parent.frames["content-iframe"].location.href = "VehicleFuelRequest.jsp";
    });
};

const onRejectRequest = function() {
    $("#rejectionModal").modal("show");
};

const onSuccesRejectRequest = function() {
    Swal.fire({
        title: "Éxito",
        text: "La solicitud se rechazó exitosamente.",
        icon: "success",
        confirmButtonText: "OK",
    }).then((result) => {
        parent.frames["content-iframe"].location.href =
            "FuelingWalletAuthList.jsp?INBOX_TYPE=" +
            WAITING_AUTHORIZATION_FUELING_REQUEST;
    });
};

const currentBalanceChanged = function() {

    let currentBalance = $("#currentBalance").val() == "" ? 0 : $("#currentBalance").val();
    let authorizedAmount = $("#authorizedAmmount").val();
    let amountVerified = $("#amountVerified").val() == "" ? 0 : parseFloat($("#amountVerified").val());

    if (isNaN(currentBalance) || currentBalance < 0)
        currentBalance = 0;


    currentBalance = parseFloat(currentBalance);
    authorizedAmount = parseFloat(authorizedAmount);

    if ((currentBalance + amountVerified) > authorizedAmount)
        currentBalance = 0;

    let newBalance = authorizedAmount - currentBalance - amountVerified;

    $("#amountPending").val(newBalance.toFixed(2)  );
    $("#currentBalance").val(currentBalance);

};