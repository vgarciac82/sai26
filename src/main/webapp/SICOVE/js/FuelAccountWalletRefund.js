const FuelAccountWalletRefund = {
    idFuelAccountWallets: 0,
    walletNumber: null,
    refundAmount: 0.00,
    registrationDate: null,
    userCapture: null,
    supplierRefund: false
}

const saveWalletRefund = function(onSuccessSave, onErrorSave){

    $.ajax({
        url: "walletRefund",
        type: "POST",
        data: JSON.stringify(FuelAccountWalletRefund),
        success: function(data) {
            onSuccessSave(data);
        },
        error: function() {
           onErrorSave();
        }
    });

}

const deleteRefund = function(idRefund, onSuccessDelete, onErrorDelete){

    $.ajax({
        url: "walletRefund?ID_REFUND=" + idRefund,
        type: "DELETE", 
        success: function(data) {
            onSuccessDelete(data);
        },
        error: function() {
            onErrorDelete();
        }
    });

}
 