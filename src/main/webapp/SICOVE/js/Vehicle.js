const inventoryBaseURL = 'http://10.0.0.56:9090/';

const getVehicleResponsibleEmployee = function(inventoryId, onSuccess, onError){
	
	$.ajax({
		url: inventoryBaseURL + "vehicle/assignations/" + inventoryId,
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

const getVehicleAsignedWallet = function( onSuccess, onError ){
	
	querySelectPost({
		queryName:"walletsNumberAssigned",
		targetObjectId:"walletNum",
		async:true,
		callback: function(){
			if(onSuccess)
				onSuccess();
		}
	});
	
}