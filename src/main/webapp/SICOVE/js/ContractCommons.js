
const commonContractInit = function(idExecutiveUnit) {
    getAccountsByUnit(idExecutiveUnit, onLoadAccounts, onErrorLoadAccounts);
}

const onLoadAccounts = function(accountList,account) {
    if (accountList && accountList.length > 0) {

        let select = $('#idAccount');
        select.find('option:not(:first)').remove();

        $.each(accountList, function(index, item) {
            select.append($('<option>', {
                value: item.idAccount,
                text: item.accountNumber
            }));
        });

        if (account && account !== "") {
            $("#idAccount option").filter(function() {
                return $(this).text() === account;
            }).prop("selected", true);
            loadAccountInfo()
        }

        $('#idAccount').change();
    } else
        showAlert('Sin Cuenta Asignada', 'No hay una cuenta asignada a su unidad para registrar tarjetas.', 'warning');
}

const onErrorLoadAccounts = function(errCause) {
    console.log(errCause);
    showAlert( "Error", "Ocurrio un error mientras se cargaba informacion.  \n Intente nuevamente, si el problema persiste notifique al administrador.", "error");
};

const loadAccountInfo = function(onSuccessLoad) {

    if ($('#idAccount').val() == "")
        Swal.fire({
            title: 'Sin Cuenta Seleccionada',
            text: 'Debe seleccionar una cuenta.',
            icon: 'info',
            confirmButtonText: 'Aceptar'
        });
    else
        queryFormPost({
            queryName: "accountContractInfo",
            async: true,
            callback: function() {
                if(onSuccessLoad)
                    onSuccessLoad();
            }
        });
}