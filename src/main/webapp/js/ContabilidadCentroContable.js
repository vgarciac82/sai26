var newCC;

function getCentroContable() {
	var getURL = function() {
		var url = window.location.protocol + "//";
		url += window.location.host + "/";
		url += window.location.pathname.split("/")[1];
		return url;
	};

	var settings = {
		async : false,
		callback : function() {
		}
	};

	$.ajax({
		type : "POST",
		url : getURL() + "/session/readValue",
		cache : false,
		async : settings.async,
		data : {
			action : 'R',
			propName : 'CentroContable'
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
		},
		success : function(RS) {
			var data, value;
			if (RS.success == "true") {
				value = RS.CentroContable;
				if (value != null)
					newCC =  value;
				else
					newCC =  '';
			}
		}
	});
}

function setCentroContable(val) {
	var getURL = function() {
		var url = window.location.protocol + "//";
		url += window.location.host + "/";
		url += window.location.pathname.split("/")[1];
		return url;
	};

	var settings = {
		async : false,
		callback : function() {
		}
	};

	$.ajax({
		type : "POST",
		url : getURL() + "/session/setValue",
		cache : false,
		async : settings.async,
		data : {
			action : 'W',
			propName : 'CentroContable',
			propVal : val
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
		},
		success : function(RS) {
			if (RS.success == "true") {
				Swal.fire('Centro Contable actual: ' + (val==0?'CONSOLIDADO':val) , 'Todas las operaciones se realizaran sobre este', "info");
			}
		}
	});
}