const initAuthRequest = function(amountRequested, onAuthBtnClick) {
    $("#authRequest").click(function() {
        $("#authRequestModal").modal("show");
    });

    $("#authorizedAmount").val(amountRequested);
    $("#authDlgBtn").on("click", function() {
        onAuthBtnClick();
    });

}