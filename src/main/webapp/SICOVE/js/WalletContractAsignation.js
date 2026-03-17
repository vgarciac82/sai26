const saveContract = function(loginUsr) {

    let employeeAdministrator = parseInt($("#employeeAdministrator").val() == "" ? 0 : $("#employeeAdministrator").val());

    if (employeeAdministrator <= 0) {
        Swal.fire(
            'Sin numero de empleado',
            'No se encontro numero de empleado. Solo empleados pueden registrar contratos',
            'warning'
        );
        return;
    }

    fuelContract.contractNumber = $("#cIdContratoCompromiso").val();
    fuelContract.totalMaxAmount = quitaFmt($("#totalMaxAmount").val());
    fuelContract.active = $("#active").is(":checked");
    fuelContract.employeeRegistration = loginUsr;
    fuelContract.byLiters = $("#byLiters").is(":checked");
    fuelContract.employeeAdministrator = employeeAdministrator;
    save(onSuccessSave, onErrorSave);
}

const addContractAccount = function(loginUsr) {

    if (fuelContract.id == 0) {
        Swal.fire(
            'Sin Contrato',
            'Debe seleccionar o guardar el contrato para poder realizar asignaciones de cuentas.',
            'warning'
        );

        return;
    }

    let toAdd = JSON.parse(JSON.stringify(fuelAccount));
    toAdd.idContract = fuelContract.id;
    toAdd.employeeResponsible = $("#employeeNumber").val();
    toAdd.idUnit = $("#idUnit").val();
    toAdd.accountNumber = $("#accountNumber").val();
    toAdd.monthlyAsignation = $("#monthlyAsignation").val();
    toAdd.userRegistration = loginUsr;

    addAccount(toAdd, onSuccesAdd, onErrorSave);

}

const cleanAccountInfo = function() {
    $(".accountInfo").each(function() {
        $(this).val("");
    });
}

const onSuccesAdd = function(objSaved) {
    Swal.fire(
        'Registro Exitoso',
        'La cuenta se asigno correctamente al contrato.',
        'success'
    );
    getDataTable(fuelContract.id);
    cleanAccountInfo();
}

const onSuccessSave = function(objSaved) {
    Swal.fire(
        'Registro Exitoso',
        'El contrato fue asignado exitosamente.',
        'success'
    );
    getSAIContractInfo();
}

const onErrorSave = function(errorCause) {
    console.log(errorCause);
    Swal.fire({
        icon: 'error',
        title: 'Problemas...',
        text: errorCause.responseText
    });
}