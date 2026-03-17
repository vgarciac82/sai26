/**
 * 
 */
let walletRefoundsDT = null;
const initWalletRefund = function (initError, idExecutiveUnit) {

    if (initError) {

        showAlert('Error', 'Ocurrio un error mientras se iniciaba el tramite. Notifique al adminitrador.', 'error');
        $('#walletsDataTable').DataTable();
        return;
    }

    walletsDT = $('#walletsDataTable').DataTable();

    commonContractInit(idExecutiveUnit);

    $("#loadAccountInfoBtn").click(function () {
        loadAccountInfo();
        createWalletDT(captureRefund, 2);
        createWalletRefundDT();
    });

    $("#saveRefundBtn").click(function () {
        saveRefundAction();
    });

    walletRefoundsDT = $('#walletRefundsDetail').DataTable();

    $("#btnDelRefund").on("click", function () { delRefundAction(); });

    $("#refundModal").on("show.bs.modal", function (evt) {
        console.log(evt);
        $(".spinSave").hide();
        $("#saveRefundBtn").prop("disabled", false);
        $("#cancelRefundBtn").prop("disabled", false);
        $("#refundAmount").val("");
    });

	$("#month").on("click", function() { createWalletDT(captureRefund,2);createWalletRefundDT() } ); 
    
}



const createWalletRefundDT = function () {

    if (walletRefoundsDT) {
        walletRefoundsDT.destroy();
        $("#walletRefundsDetail tbody").off("dblclick");
        $("#walletRefundsDetail tbody").off("click");
    }

    $.fn.dataTable.ext.legacy.ajax = true;

    let idAccount = ($("#idAccount").val() == "" ? "0" : $("#idAccount").val());
    let viewName = "vFuelingRefund";
    let condition = "is_supplier_refund = 0 AND month = " + $("#month").val();
    let columns = [
        { "name": "id_refund" },
        { "name": "wallet_number" },
        { "name": "vehicleLicensePlate" },
        { "name": "vehicleBrand" },
        { "name": "vehicleSubBrand" },
        { "name": "refund_amount" }
    ]

    walletRefoundsDT = $('#walletRefundsDetail').DataTable({
        "retrieve": true,
        "destroy": true,
        "serverSide": true,
        "ajax": {
            url: dataTableBaseURL + "/crud?rt=t&ql=" + viewName + "&qw=idContractAccount=" + idAccount + (condition ? " AND " + condition : ""),
            type: 'POST',
        },
        "aoColumns": columns
    });



    $('#walletRefundsDetail tbody').on('click', 'tr', function () {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
            console.log("unselected");
            $("#btnDelRefund").prop("disabled", true);
        } else {
            walletRefoundsDT.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            console.log("selected");
            $("#btnDelRefund").prop("disabled", false);
        }
    });

}

const delRefundAction = function () {
    let rowSelected = walletRefoundsDT.row('.selected');
    if (rowSelected) {
        idRefund = rowSelected.data()[0];
        Swal.fire({
            title: 'Seguro de Eliminar?',
            text: "Esta operacion no se podra revertir!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sí, Borrar!'
        }).then((result) => {
            if (result.isConfirmed) {
                $.blockUI({ message: 'Eliminando...' });
                deleteRefund(idRefund, onSuccesDeleteRefund, onErrorRefund);
            }
        })
    }
}

const onSuccesDeleteRefund = function (data) {
    $.unblockUI();
    showAlert('Exito', "La devolucion se elimino exitosamente.", 'success');
    walletRefoundsDT.ajax.reload();
    walletsDT.ajax.reload();
}

const captureRefund = function (data) {
    $("#idFuelAccountWalletsRefund").val(data[0]);
    $("#isSupplierRefund").val("0");
    $("#walletNumberRefund").val(data[1]);
    $("#currentBalanceInfo").val(data[5]);
    $("#refundModal").modal("show");
}
var registrationDate = new Date().toISOString();


const saveRefundAction = function () {
    if (validateRefundAction()) {

        FuelAccountWalletRefund.idFuelAccountWallets = $("#idFuelAccountWalletsRefund").val();
        FuelAccountWalletRefund.walletNumber = $("#walletNumberRefund").val();
        FuelAccountWalletRefund.refundAmount = $("#refundAmount").val();
        FuelAccountWalletRefund.registrationDate = $("#refundDate").val()+"T00:00:00";
        FuelAccountWalletRefund.userCapture = $("#employeeRegistration").val();

        Swal.fire({
            title: 'Seguro de Registrar?',
            text: "Esta seguro de registrar la devolucion?",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sí, guardar!'
        }).then((result) => {
            if (result.isConfirmed) {

                $(".spinSave").show();
                $("#saveRefundBtn").prop("disabled", true);
                $("#cancelRefundBtn").prop("disabled", false);
                saveWalletRefund(onSuccessSaveRefund, onErrorRefund);
            }
        })

    }
}

const onSuccessSaveRefund = function (data) {
    showAlert('Exito', "La devolucion se guardo exitosamente.", 'success');
    $("#refundModal").modal("hide");
    walletsDT.ajax.reload();
    walletRefoundsDT.ajax.reload();
}

const onErrorRefund = function (error) {
    console.error(error);
    $.unblockUI();
    showAlert('Error', 'Ocurrio un error al guardar la devolucion. Notifique al administrador.', 'error');
}


/*
    Validates that refundAmount is numeric positve less than the value into currentBalanceInfo and that refundDate is not empty and less than tomorrow
*/
const validateRefundAction = function () {

    let refundAmount = $("#refundAmount").val();
    if (refundAmount === "" || isNaN(refundAmount) || parseFloat(refundAmount) <= 0) {
        showAlert('Error', 'El monto a devolver debe ser un numero positivo.', 'error');
        return false;
    }

    let currentBalanceInfo = parseFloat($("#currentBalanceInfo").val());
    if (refundAmount > currentBalanceInfo) {
        showAlert('Error', 'El monto a devolver no puede ser mayor al saldo actual.', 'error');
        return false;
    }

    let refundDate = $("#refundDate").val();
    if (refundDate === "") {
        showAlert('Error', 'La fecha de devolucion no puede estar vacia.', 'error');
        return false;
    }

    let today = new Date();
    // Change the date by adding 1 to it (today + 1 = tomorrow)
    today.setDate(today.getDate() + 1);
    // return yyyy-mm-dd format
    if (refundDate > today.toISOString().split('T')[0]) {
        showAlert('Error', 'La fecha de devolucion no puede ser mayor a mañana.', 'error');
        return false;
    }

    return true;

}