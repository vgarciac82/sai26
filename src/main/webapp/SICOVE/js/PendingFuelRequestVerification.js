/**
 *
 */

const processVerification = function(requestRow, action) {
    let requestFolio = requestRow[0];
    parent.frames["content-iframe"].location.href =
        "VehicleFuelRequest.jsp?REQUEST_FOLIO=" +
        requestFolio +
        (action ? "&ACTION=" + action : "");
};

const captureVerification = function() {
    if (
        $("#currentBalance").val() == "" ||
        parseFloat($("#currentBalance").val()) < 0
    ) {
        Swal.fire({
            title: "Advertencia",
            text: "El saldo actual no puede ser menor  a cero.",
            icon: "warning",
            confirmButtonText: "Aceptar",
        });
    } else {
        $("#addVerificationModal").modal("show");
    }
};


const successVerificationUpload = function() {
    getPendingFuelRequestVerification();

    Swal.fire({
        title: "Éxito",
        text: "Se agrego el comprobante exitosamente.",
        icon: "success",
        confirmButtonText: "OK",
    });

}

const getPendingFuelRequestVerification = function() {
    getWalletFuelRequestVerificationByAsignation(
        vehicleFuelRequest.fuelingRequestId,
        onSuccesLoadFuelRequestVerification,
        onErrorLoadFuelRequestVerification
    );
};

const onSuccesLoadFuelRequestVerification = function(data) {
    if (vehicleFuelRequest.idStatus == AUTHORIZED_FUELING_REQUEST || vehicleFuelRequest.idStatus == CAPTURE_VERIFICATION) {
        if (data.idVerification != 0) {
            updateVerificationBtns();
            fillVerificationData(data);
            getWalletFuelRequestVerificationList(fillVerificationTable, onErrorLoadFuelRequestVerification);
            if (data.validationAmount == 0) {
                $("#addVerificationRowBtn").hide();
                $("#sendVerification").prop("disabled", false);
            }
        } else {
            saveVerificationBtns();
        }
    } else if (vehicleFuelRequest.idStatus == VALIDATING_VERIFICATION) {
        fillVerificationData(data);
        getWalletFuelRequestVerificationList(fillVerificationTable, onErrorLoadFuelRequestVerification);
    }
};

const fillVerificationTable = function(data) {
    $("#verificationDetailTable tbody").empty();

    if (data.length == 0) {
        let row = $("<tr></tr>");
        row.append($("<td colspan='7' class='text-center'>Sin información</td>"));

        $("#verificationDetailTable tbody").append(row);
        return;
    }

    data.forEach((element) => {
        let row = $("<tr></tr>");
        row.append($("<td>" + element.idDetail + "</td>"));

        if (element.acepted)
            row.append($("<td class='text-center'><i class='fa-regular fa-circle-check'></i></td>"));
        else
            row.append($("<td class='text-center'><i class='fa-solid fa-xmark'></i></i></td>"));

        row.append($("<td>" + element.ticketNumber + "</td>"));
        row.append($("<td>" + element.ticketAmount + "</td>"));
        row.append($("<td>" + element.ticketDate + "</td>"));
        if (element.ticketObservations != null)
            row.append($("<td>" + element.ticketObservations + "</td>"));
        else
            row.append($("<td></td>"));

        let btns = $("<td></td>");

        btns.append(
            $("<button></button>")
            .addClass("btn btn-info btn-sm ml-1 pl-1")
            .attr("type", "button")
            .attr("onclick", "viewVerificationDetail('" + element.ticketReference + "')")
            .append($("<i></i>").addClass("fas fa-eye"))
        );

        if (vehicleFuelRequest.idStatus == VALIDATING_VERIFICATION) {

            btns.append(
                $("<button></button>")
                .addClass("btn btn-warning btn-sm ml-1 pl-1")
                .attr("type", "button")
                .attr("onclick", "acceptVerificationDetail(" + element.idDetail + ")")
                .append($("<i></i>").addClass("fa-regular fa-circle-check"))
            );

            btns.append(
                $("<button></button>")
                .addClass("btn btn-danger btn-sm ml-1 pl-1")
                .attr("type", "button")
                .attr("onclick", "rejectVerificationDetail(" + element.idDetail + ")")
                .append($("<i></i>").addClass("fa-solid fa-xmark"))
            );

        } else if (vehicleFuelRequest.idStatus == AUTHORIZED_FUELING_REQUEST || vehicleFuelRequest.idStatus == CAPTURE_VERIFICATION) {
            if (!(element.acepted && element.acepted === true))
                btns.append(
                    $("<button></button>")
                    .addClass("btn btn-danger btn-sm ml-1")
                    .attr("type", "button")
                    .attr("onclick", "deleteVerificationDetail(" + element.idDetail + ")")
                    .append($("<i></i>").addClass("fas fa-trash"))
                );

        }

        row.append(btns);

        $("#verificationDetailTable tbody").append(row);
    });
};



const fillVerificationData = function(data) {

    $("#amountPending").val(data.validationAmount);
    $("#currentBalance").val(data.currentWalletBalance);
    $("#currentVehicleKms").val(data.currentVehicleKilometers);
    $("#initialVehicleKilometers").val(data.initialVehicleKilometers);

    queryFormPost({
        queryName: "readVerifiedAmount",
        async: true,
        callback: function() {
            currentBalanceChanged();
        }
    })
};
const onSuccesUpdateVerification = function(data) {
    swal.fire({
        title: "Éxito",
        text: "Se actualizo la comprobacion de la solicitud de combustible exitosamente.",
        icon: "success",
        confirmButtonText: "Aceptar",
    });

    onSuccesLoadFuelRequestVerification(data);
}

const onErrorSaveVerification = function(errCause) {
    swal.fire({
        title: "Error",
        text: "Ocurrió un error al guardar la comprobacion de la solicitud de combustible.",
        icon: "error",
        confirmButtonText: "Aceptar",
    });
};

const onErrorLoadFuelRequestVerification = function(errCause) {
    swal.fire({
        title: "Error",
        text: "Ocurrió un error al cargar la comprobacion de la solicitud de combustible.",
        icon: "error",
        confirmButtonText: "Aceptar",
    });
};

const saveVerification = function() {
    if (verificationComplete()) {
        let currentBalance = $("#currentBalance").val();
        let currentVehicleKms = $("#currentVehicleKms").val();
        let initialVehicleKms = $("#initialVehicleKilometers").val();
        let amountPending = $("#amountPending").val();

        walletFuelRequestVerification = {
            idVerification: 0,
            fuelingRequestId: vehicleFuelRequest.fuelingRequestId,
            currentWalletBalance: currentBalance,
            currentVehicleKilometers: currentVehicleKms,
            initialVehicleKilometers:initialVehicleKms,
            validationAmount: amountPending,
            verificationCaptured: new Date(),
            userCapture: $("#employeeRegistration").val(),
        };

        saveWalletFuelRequestVerification(
            onSuccesLoadFuelRequestVerification,
            onErrorSaveVerification
        );
    }
};


const verificationComplete = function() {
    let currentBalance = $("#currentBalance").val();
    
    let initialVehicleKilometers = parseFloat( $("#initialVehicleKilometers").val() === ""? "0.0":$("#initialVehicleKilometers").val() );
    let currentVehicleKms = parseFloat( $("#currentVehicleKms").val() === ""? "0.0":$("#currentVehicleKms").val() );
    let authorizedAmmount = $("#authorizedAmmount").val();
    let isValid = true;
    
    if (currentVehicleKms <= 0) {
        Swal.fire({
            title: "Advertencia",
            text: "El kilometraje actual no puede ser menor o igual a cero.",
            icon: "warning",
            confirmButtonText: "Aceptar",
        });
        isValid = false;
    }else{
		if (  initialVehicleKilometers < 0 ) {
	        Swal.fire({
	            title: "Advertencia",
	            text: "El kilometraje actual no puede ser menor al kilometraje inicial",
	            icon: "warning",
	            confirmButtonText: "Aceptar",
	        });
	        isValid = false;
        }else if ( currentVehicleKms  < initialVehicleKilometers) {
	        Swal.fire({
	            title: "Advertencia",
	            text: "El kilometraje actual no puede ser menor al kilometraje inicial",
	            icon: "warning",
	            confirmButtonText: "Aceptar",
	        });
	        isValid = false;
        }else {
			if (currentBalance == "" || parseFloat(currentBalance) < 0) {
		        Swal.fire({
		            title: "Advertencia",
		            text: "El saldo actual no puede ser menor  a cero.",
		            icon: "warning",
		            confirmButtonText: "Aceptar",
		        });
		        isValid = false;
		    } else if (parseFloat(currentBalance) > parseFloat(authorizedAmmount)) {
		        Swal.fire({
		            title: "Advertencia",
		            text: "El saldo actual no puede ser mayor al asignado.",
		            icon: "warning",
		            confirmButtonText: "Aceptar",
		        });
		        isValid = false;
		    }
		}
	}
   
    return isValid;
};

const saveVerificationBtns = function() {
    $(".save").show();
    $(".update").hide();
};

const updateVerificationBtns = function() {
    $(".update").show();
    $(".save").hide();
};

const saveVerificationDetail = function(onSuccesUploadVerification, onErrorUploadVerification) {

    if ($('#saveVerificationForm')[0].checkValidity() === false) {
        $('#saveVerificationForm').addClass('was-validated');
        return;
    }

    const progressBar = $('#upload-ticket-progress');
    const form = $('#saveVerificationForm')[0];

    const data = new FormData(form);
    data.append('idProcess', vehicleFuelRequest.idProcess);
    data.append('idVerification', walletFuelRequestVerification.idVerification);
    data.append('folderName', 'Comprobaciones');

    $.ajax({
        url: 'fuelingExpedient',
        type: 'POST',
        enctype: 'multipart/form-data',
        data: data,
        beforeSend: function() {
            $(".verificationOps").hide();
        },
        processData: false,
        contentType: false,
        xhr: function() {
            const xhr = new XMLHttpRequest();
            xhr.upload.addEventListener('progress', function(event) {
                if (event.lengthComputable) {
                    const percentComplete = event.loaded / event.total * 100;
                    progressBar.css('width', percentComplete + '%');
                    progressBar.text(percentComplete + '%');
                }
            }, false);
            return xhr;
        },
        success: function(response) {
            $("#addVerificationModal").modal("hide");
            onSuccesUploadVerification();
            $(".verificationOps").show();
        },
        error: function(error) {
            console.log(error);
            $("#addVerificationModal").modal("hide");
            onErrorUploadVerification(error);
            $(".verificationOps").show();
        }
    });
}

const sendVerificationClick = function() {
    if (vehicleFuelRequest.idStatus == CAPTURE_VERIFICATION || vehicleFuelRequest.idStatus == AUTHORIZED_FUELING_REQUEST)  {
        Swal.fire({
            title: "¿Está seguro de enviar a validación el trámite de comprobación?",
            text: "Una vez enviado no podrá realizar cambios.",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Aceptar",
            cancelButtonText: "Cancelar",
        }).then((result) => {
            if (result.isConfirmed) {
                vehicleFuelRequest.idStatus = VALIDATING_VERIFICATION;
                sendVerification(onSuccesSendVerification, onErrorSendVerification);
            }
        });
    } else if (vehicleFuelRequest.idStatus == VALIDATING_VERIFICATION) {

        validateEndVerification();

    }
};

const validateEndVerification = function() {
    queryFormPost({
        queryName: "verificationHasRejectedTickets",
        async: true,
        callback: function() {
            let rejectedTickets = ($("#rejectedTickets").val() == "" ? 0 : parseInt($("#rejectedTickets").val()));
            if (rejectedTickets > 0) {
                Swal.fire({
                    title: "¿Está seguro de regresar a captura el trámite de comprobación?",
                    text: "Una vez enviado no podrá realizar cambios.",
                    icon: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#3085d6",
                    cancelButtonColor: "#d33",
                    confirmButtonText: "Aceptar",
                    cancelButtonText: "Cancelar",
                }).then((result) => {
                    if (result.isConfirmed) {
                        vehicleFuelRequest.idStatus = CAPTURE_VERIFICATION;
                        sendVerification(onSuccesSendVerification, onErrorSendVerification);
                    }
                });
            } else {

                Swal.fire({
                    title: "¿Está seguro de ACEPTAR la comprobacion?",
                    text: "Una vez terminado no podrá realizar cambios.",
                    icon: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#3085d6",
                    cancelButtonColor: "#d33",
                    confirmButtonText: "Aceptar",
                    cancelButtonText: "Cancelar",
                }).then((result) => {
                    if (result.isConfirmed) {
                        vehicleFuelRequest.idStatus = VERIFIED_FUELING_REQUEST;
                        sendVerification(onSuccesSendVerification, onErrorSendVerification);
                    }
                })
            }

        }
    })
}

const onErrorSendVerification = function(errCause) {
    vehicleFuelRequest.idStatus = CAPTURE_VERIFICATION;
    swal.fire({
        title: "Error",
        text: "Ocurrió un error al enviar la comprobacion de la solicitud de combustible.",
        icon: "error",
        confirmButtonText: "Aceptar",
    });
};


const onSuccesSendVerification = function() {


    if (vehicleFuelRequest.idStatus == VERIFIED_FUELING_REQUEST) {
        Swal.fire({
            title: "Éxito",
            text: "La solicitud de comprobacion se autorizo exitosamente.",
            icon: "success",
            confirmButtonText: "OK",
        }).then((result) => {
            parent.frames["content-iframe"].location.href = "../SICOVE/AuthFuelRequestVerification.jsp?INBOX_TYPE=" + VERIFIED_FUELING_REQUEST;
        });
    } else if (vehicleFuelRequest.idStatus == CAPTURE_VERIFICATION) {
        Swal.fire({
            title: "Éxito",
            text: "La solicitud se envió a captura de comprobacion exitosamente.",
            icon: "success",
            confirmButtonText: "OK",
        }).then((result) => {
            parent.frames["content-iframe"].location.href = "../SICOVE/AuthFuelRequestVerification.jsp?INBOX_TYPE=" + VALIDATING_VERIFICATION;
        });
    } else if (vehicleFuelRequest.idStatus == VALIDATING_VERIFICATION) {
        Swal.fire({
            title: "Éxito",
            text: "La solicitud se envió a validación exitosamente.",
            icon: "success",
            confirmButtonText: "OK",
        }).then((result) => {
            parent.frames["content-iframe"].location.href = "../SICOVE/PendingFuelRequestVerification.jsp?INBOX_TYPE=" + AUTHORIZED_FUELING_REQUEST;
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

const viewVerificationDetail = function(fmxNode) {
    showDocument(fmxNode);
}

const acceptVerificationDetail = function(idDetail) {
    Swal.fire({
        title: "¿Está seguro de aceptar el registro?",
        text: "Esta seguro de haber revisado el contenido del documento y de aceptar el contenido?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Aceptar",
        cancelButtonText: "Cancelar",
    }).then((result) => {
        if (result.isConfirmed) {
            updateVerificationDetail(idDetail, true, "", getPendingFuelRequestVerification, onErrorLoadFuelRequestVerification);
        }
    });
};

const rejectVerificationDetail = function(idDetail) {
    Swal.fire({
        title: "¿Está seguro de rechazar el registro?",
        text: "Esta seguro de haber revisado el contenido del documento y de rechazar el contenido?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Aceptar",
        cancelButtonText: "Cancelar",
    }).then((result) => {
        if (result.isConfirmed) {
            $("#rejectJustification").val("");
            $("#rejectJustification").removeClass("is-invalid");
            $("#rejectJustification").removeClass("is-valid");
            $("#rejectVerifDetailModal").modal("show");
            console.log("Cancelando el registro: " + idDetail);
            $("#idDetailReject").val(idDetail);
            console.log("Cancelando el registro: " + $("#idDetailReject").val());
        }
    });
}

const deleteVerificationDetail = function(idDetail) {
    Swal.fire({
        title: "¿Está seguro de eliminar el registro?",
        text: "Debera capturar nuevamente la comprobacion.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Aceptar",
        cancelButtonText: "Cancelar",
    }).then((result) => {
        if (result.isConfirmed) {
            deleteDetail(idDetail, getPendingFuelRequestVerification, onErrorLoadFuelRequestVerification);
        }
    });
}

const updateVerificationClick = function() {

    if (verificationComplete()) {

        let currentBalance = $("#currentBalance").val();
        let initialVehicleKms = $("#initialVehicleKilometers").val();
        let currentVehicleKms = $("#currentVehicleKms").val();
        let amountPending = $("#amountPending").val();

        walletFuelRequestVerification.currentWalletBalance = currentBalance;
        walletFuelRequestVerification.currentVehicleKilometers = currentVehicleKms;
        walletFuelRequestVerification.initialVehicleKilometers = initialVehicleKms;
        walletFuelRequestVerification.validationAmount = amountPending;

        updateWalletFuelRequestVerification(
            onSuccesUpdateVerification,
            onErrorSaveVerification
        );
    }
};


const clearVerificationDialog = function() {

    $(".infoTicket").val("");
    const progressBar = $('#upload-ticket-progress');
    progressBar.css('width', '0%');
    progressBar.text('0%');
    $('#saveVerificationForm').removeClass('was-validated');

}


const ticketAmountChanged = function() {
    let ticketAmount = parseFloat(  $("#ticketAmount").val() == "" ? "0" : $("#ticketAmount").val() );
    if (ticketAmount <= 0 ) {
        Swal.fire({
            title: "Error",
            text: "El monto del ticket debe ser mayor a cero.",
            icon: "error",
            confirmButtonText: "Aceptar",
        }).then((result) => {
            $("#ticketAmount").val("0.00");
        });
        return false;
    }

    let amountPending = parseFloat(  $("#amountPending").val() == "" ? "0" : $("#amountPending").val() );

    if (ticketAmount > amountPending ) {
        Swal.fire({
            title: "Error",
            text: "El monto del ticket no puede ser mayor al monto pendiente.",
            icon: "error",
            confirmButtonText: "Aceptar",
        }).then((result) => {
            $("#ticketAmount").val("0.00");
        });
    }
}

const ticketDateChanged = function() {
    let ticketDate = new Date( $("#ticketDate").val() + "T00:00");

    let minDate =  new Date( vehicleFuelRequest.justification.initialDate+ "T00:00");
    let maxDate =  new Date( vehicleFuelRequest.justification.endDate+ "T00:00");

   
    if ( ticketDate > maxDate || ticketDate < minDate) {
        Swal.fire({
            title: "Error",
            text: "La fecha del ticket debe estar en el rango de la solicitud.",
            icon: "error",
            confirmButtonText: "Aceptar",
        }).then((result) => {
            $("#ticketDate").val("");
        });
    }
}