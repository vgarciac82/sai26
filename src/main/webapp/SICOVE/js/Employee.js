const employeeBaseURL = 'http://10.0.0.56:9090/';

const getEmployee = function(employeeNumber, onSuccess, onError) {

	$.ajax({
		url: employeeBaseURL + 'HumanResources/employee/' + employeeNumber,
		type: 'GET',
		contentType: 'application/json',
		success: function(data) {
			onSuccess(data);
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.error('Error:', textStatus, errorThrown);
			if (onError)
				onError(jqXHR);
		}
	});


}