const dataTableBaseURL = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1];
let walletsDT;

const initUI = function(initError, idExecutiveUnit) {

    subIniciaDlgBootsprap();

    if (initError) {

        Swal.fire({
            title: 'Error',
            text: 'Ocurrio un error mientras se iniciaba el tramite. Notifique al adminitrador.',
            icon: 'error',
            confirmButtonText: 'Aceptar'
        });
        $('#walletsDataTable').DataTable();
        return;
    }

    $("#vehicle").on("change", function() {
        loadAsignationInfo();
    });

    getAccountsByUnit(idExecutiveUnit, onSuccessLoadAccounts, onErrorLoad);

    $("#loadAccountInfoBtn").click(function() {
        loadAccountInfo();
    });

    $("#addWalletAccountBtn").click(function() {
        addWalletAccountAction(false);
    });

    $("#updateWalletAccountBtn").click(function() {
        addWalletAccountAction(true);
    });

    $('#idAccount').on("change", function() {
        onChangeAccounts();
    });

    $("#cleanVehicleInfo").on("click", function() {
        cleanVehicleInfo();
    });

    $("#enableWalletAccountBtn").on("click", function() {
        enableWalletAccountAction();
    });

    $("#disableWalletAccountBtn").on("click", function() {
        disableWalletAccountAction();
    });

    $("#cancelUpdateWalletAccountBtn").on("click", function() {
        changeOperations("add");
    });


    $("#cleanUpdateVehicleInfoBtn").on("click", function() {
        cleanVehicleInfo();
    });
}

// Funcion cleanVehicleInfo limpia los campos de la informacion del vehiculo. Todos los campos con la clase vehicleInfo
const cleanVehicleInfo = function() {
    $(".vehicleInfo").val("");
}

const onChangeAccounts = function() {
    createWalletDT(asignWalletTblCallback,1);
}

const asignWalletTblCallback = function(data) {
    cleanVehicleInfo();
    fillWalletInfo(data);
    changeOperations("edit");
}

const changeOperations = function(operation) {
    if (operation == "edit") {

        $("#updateOperations").show();
        $("#addOperations").hide();
        readWalletStatus(function() {
            if ($("#walletStatus").val() == "1") {
                $("#disableWalletAccountBtn").show();
                $("#enableWalletAccountBtn").hide();
            } else {

                $("#disableWalletAccountBtn").hide();
                $("#enableWalletAccountBtn").show();
            }
        });
    } else {
        fuelWalletAccount.idFuelAccountWallet = 0;
        $("#updateOperations").hide();
        $("#addOperations").show();
    }
}

const readWalletStatus = function(fnCallback) {
    queryFormPost({
        queryName: "walletStatusRead",
        async: true,
        callback: function() {
            fnCallback();
        }
    });
}

const fillWalletInfo = function(walletData) {
    console.log(walletData);
    $("#liscencePlate").val(walletData[2]);
    $("#walletNum").val(walletData[1]);
    if($("#liscencePlate").val() != ""){
        queryFormPost({
            queryName: "vehicleInfoReadByPlate",
            async: true,
            callback: function() {
                fuelWalletAccount.idFuelAccountWallet = walletData[0];
            }
        });
    }else{
        fuelWalletAccount.idFuelAccountWallet = walletData[0];
    }

}

const disableWalletAccountAction = function() {

    // Valida que los campos walletNum y idAccount tenga un valor, en caso contrario lanza una alerta con sweetalert para notificar al usuario que debe llenar los campos
    if ($("#walletNum").val() == "" || $("#idAccount").val() == "") {
        Swal.fire({
            title: 'Error',
            text: 'Debe llenar los campos de numero de cuenta y numero de monedero electronico.',
            icon: 'error',
            confirmButtonText: 'Aceptar'
        });
        return;
    }

    let wallet = JSON.parse(JSON.stringify(fuelWalletAccount));
    wallet.idContractAccount = $("#idAccount").val();
    wallet.walletNumber = $("#walletNum").val();
    wallet.vehicleInventoryId = null;
    wallet.status = 0;
    updateWalletAccount(wallet, onSuccessUpdateWallet, onErrorSave);
}

const enableWalletAccountAction = function() {
    fuelWalletAccount.status = 1;
    addWalletAccountAction(true);
}

const addWalletAccountAction = function(isUpdate) {

    // Valida que los campos walletNum y idAccount tenga un valor, en caso contrario lanza una alerta con sweetalert para notificar al usuario que debe llenar los campos
    if ($("#walletNum").val() == "" || $("#idAccount").val() == "") {
        Swal.fire({
            title: 'Error',
            text: 'Debe llenar los campos de numero de cuenta y numero de monedero electronico.',
            icon: 'error',
            confirmButtonText: 'Aceptar'
        });
        return;

    }

    let wallet = JSON.parse(JSON.stringify(fuelWalletAccount));
    wallet.idContractAccount = $("#idAccount").val();
    wallet.walletNumber = $("#walletNum").val();
    wallet.vehicleInventoryId = $("#vehicle").val();
    wallet.userRegistration = $("#employeeRegistration").val();
    if (!isUpdate) {
        wallet.status = 1;
        saveWalletAccount(wallet, onSuccessSaveWallet, onErrorSave);
    } else {

        updateWalletAccount(wallet, onSuccessUpdateWallet, onErrorSave);
    }
}

const onSuccessUpdateWallet = function(wallet) {
    Swal.fire({
        title: 'Actualizacion Exitos',
        text: 'Se actualizo exitosamente el monedero electronico a la cuenta.',
        icon: 'success',
        confirmButtonText: 'Aceptar'
    });
    changeOperations("add");
    cleanVehicleInfo();
    $("#walletNum").val("");
    createWalletDT(asignWalletTblCallback,1);
}
const onSuccessSaveWallet = function(wallet) {

    Swal.fire({
        title: 'Registro Exitoso',
        text: 'Se registro exitosamente el monedero electronico a la cuenta.',
        icon: 'success',
        confirmButtonText: 'Aceptar'
    });
    cleanVehicleInfo();
    $("#walletNum").val("");
    createWalletDT(asignWalletTblCallback,1);
}

const onErrorSave = function(errCause) {
    console.log(errCause);
    Swal.fire({
        title: 'Error',
        text: 'Ocurrio un error mientras se guardaba el monedero electronico.  \n Intente nuevamente, si el problema persiste notifique al adminitrador.',
        icon: 'error',
        confirmButtonText: 'Aceptar'
    });
}

const onSuccessLoadAccounts = function(accountList) {
    if (accountList && accountList.length > 0) {

        let select = $('#idAccount');
        select.find('option:not(:first)').remove();

        $.each(accountList, function(index, item) {
            select.append($('<option>', {
                value: item.idAccount,
                text: item.accountNumber
            }));
        });

        $('#idAccount').change();
    } else
        Swal.fire({
            title: 'Sin Cuenta Asignada',
            text: 'No hay una cuenta asignada a su unidad para registrar tarjetas.',
            icon: 'warning',
            confirmButtonText: 'Aceptar'
        });
}

const onErrorLoad = function(errCause) {
    console.log(errCause);
    Swal.fire({
        title: 'Error',
        text: 'Ocurrio un error mientras se cargaban las cuentas.  \n Intente nuevamente, si el problema persiste notifique al adminitrador.',
        icon: 'error',
        confirmButtonText: 'Aceptar'
    });
}



const loadAsignationInfo = function() {

    getVehicleResponsibleEmployee(
        $("#vehicle").val(),
        function(employeeInfo) {
            getEmployee(employeeInfo.employeeNumber, onSuccesLoadEmployee, onErrorLoad);
        },
        onErrorLoad
    );
}