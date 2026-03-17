const fuelAccount = {
    "idAccount": 0,
    "idContract": 0,
    "employeeResponsible": 0,
    "idUnit": 0,
    "accountNumber": "",
    "monthlyAsignation": 0,
    "userRegistration": ""
}

let fuelContract = {
    "id": 0,
    "contractNumber": "",
    "totalMaxAmount": 0.00,
    "active": false,
    "registrationDate": null,
    "employeeRegistration": null,
    "byLiters": false,
    "employeeAdministrator": 0
}



const save = function(onSuccess, onError) {


    $.ajax({
        url: 'FuelContract',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(fuelContract),
        success: function(data) {
            fuelContract = data;
            onSuccess(fuelContract);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });

}

const addAccount = function(fuelAccountToSave, onSuccess, onError) {

    $.ajax({
        url: 'FuelContract/addAccount',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(fuelAccountToSave),
        success: function(data) {
            onSuccess(data);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });


}

const getAccountsByUnit = function(unitId, onSuccess, onError) {

    $.ajax({
        url: 'FuelContract/getAccounts?unitId=' + unitId,
        type: 'GET',
        contentType: 'application/json',
        success: function(data) {
            onSuccess(data);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error('Error:', textStatus, errorThrown);
            onError(jqXHR);
        }
    });


}