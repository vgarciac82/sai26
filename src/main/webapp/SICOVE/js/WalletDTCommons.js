

const actions = {
    1:{
        "name":"asign",
        "viewName": "vFuelWallets",
        "columns": [
            { "name": "idWallet" },
            { "name": "walletNumber" },
            { "name": "vehicleLicensePlate" },
            { "name": "vehicleBrand" },
            { "name": "vehicleSubBrand" }
        ],
        "condition":null
    },
    2:{
        "name":"refund",
        "viewName":"v_wallet_current_balance",
        "columns": [
            { "name": "id_wallet" },
            { "name": "wallet_number" },
            { "name": "vehicle_liscence_plate" },
            { "name": "vehicle_brand" },
            { "name": "vehicle_subbrand" },
            { "name": "current_balance" }
        ],
        "condition":"current_balance > 0 "
    }

}

const createWalletDT = function( onDblClickCallback, action) {

	
	
    if (walletsDT) {
        walletsDT.destroy();
        $("#walletsDataTable tbody").off("dblclick");
        $("#walletsDataTable tbody").off("click");
    }

    $.fn.dataTable.ext.legacy.ajax = true;
    
    let idAccount = ($("#idAccount").val() == "" ? "0" : $("#idAccount").val());
    let viewName = actions[action].viewName;
    let condition = actions[action].condition;
    let columns = actions[action].columns;

	if( action == 2 )
		condition = condition + "AND month = " +  $("#month").val();
		
    walletsDT = $('#walletsDataTable').DataTable({
        "retrieve": true,
        "destroy": true,
        "serverSide": true,
        "ajax": {
            url: dataTableBaseURL + "/crud?rt=t&ql=" + viewName  + "&qw=idContractAccount=" + idAccount  + (condition? " AND " + condition : ""),
            type: 'POST',
        },
        "aoColumns": columns
    });

    $("#walletsDataTable tbody").on('dblclick', 'tr', function() {

        let data = $('#walletsDataTable').DataTable().row(this).data();
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
            console.log("unselected");
        } else {
            walletsDT.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            console.log("selected");
        }
        if( onDblClickCallback )
            onDblClickCallback(data);

    });

    $('#walletsDataTable tbody').on('click', 'tr', function () {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
            console.log("unselected");
        } else {
            walletsDT.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            console.log("selected");
        }
    });

}