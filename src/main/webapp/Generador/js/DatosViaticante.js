/**
 * 
 */
function inicializaBeneficiario() {
	leeDatosEmpleado(numeroEmpleado);
}

function leeDatosEmpleado(numEmpleado) {
	$("#empleadoBusqueda").val(numEmpleado);
	queryFormPost({
		queryName : "informacionEmpleadoRead", 
		async : false,
		callback: function(){
			if( $("#d_email").val() == "" )
				$("#d_email").val( empleadoEmail );
		}
	});
}


function limpiaDatosViaticante() {
	$(".datosViaticante").each(function() {
		$(this).val("");
	});
}

function buscaEmpleado() {
	var noEmpleado = $("#numEmpleadoBeneficiario").val();
	leeDatosEmpleado(noEmpleado);
}

function cambiaEmpleado() {
	var receptorViaticoTipo = $('input[name="viaticosPropiosRB"]:checked').val();
	if ("PROPIO" == receptorViaticoTipo) {
		leeDatosEmpleado(numeroEmpleado);
	} else {
		limpiaDatosViaticante();
		seleccionaEmpleado();
	}
}

function seleccionaEmpleado() {
	window.open('AyudaEmpleados.jsp?inputName=numEmpleadoBeneficiario', 'Beneficiarios', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');
}


function guardaViaticante() {
	if (datosCompletosViaticante()) {
		var viaticante = viaticanteToJSON();
		$.ajax({
			url : '../viaticos/CreaViatico',
			type : 'post',
			dataType : 'json',
			async : false,
			data : viaticante,
			success : function(data) {
				var exito = (data.success == "true");
				if (exito) {
					alert("Informacion guardada exitosamente. Continue registrando la agenda.");
					$("#datosComisionDIV").css("display","block");
					$("#datosTransporteDIV").css("display","block");
					$("#SeleccionaBeneficiarioTR").css("display","none");
					$("#GuardarBtn").css("display","none");
					$("#EnviarBtn").css("display","block");
				} else {
					alert("No fue posible registrar la informacion debido al siguiente error:\n" + data.data_1.result);
				}
			}
		});

	} else {
		alert("No ha seleccionado al beneficiario o los datos del beneficiario no estan completos.");
	}
}

function datosCompletosViaticante() {
	var beneficiarioCapturado = ($("#nombreEmpleadoBeneficiario").val() != "" && ($("#aPaternoEmpleadoBeneficiario").val() != "" || $("#aMaternoEmpleadoBeneficiario").val() != "") && $("#numEmpleadoBeneficiario").val() != "-1" && $("#UEEmpleadoBeneficiario").val() != "")
	return beneficiarioCapturado;
}

function viaticanteToJSON() {
	
	var objViaticante = {
		"folioViatico" : $("#nFolioSolicitudViaticos").val(),
		"fechaCaptura" : $("#dFechaCaptura").val(),
		"loginCaptura" : uLogin,
		"numeroEmpleadoBeneficiario" : $("#numEmpleadoBeneficiario").val(),
		"esSolicitudPropia" : ($("#Propios").attr("checked") ? "S" : "N"),
		"cUnidadEjecutora" : cUnidadResponsable,
		"email": $("#d_email").val(),
		"dCuentaEmpleado": $("#dCuentaEmpleado").val()
	};

	return objViaticante;
}

function leeInformacionCaptura(){
	
	queryFormPost({
		queryName : "infoTramiteViaticosRead",
		async : false,
		callback : function() {
			
			inicializaDatosAgenda();
			iniciaTransporte();
			
			$("#datosComisionDIV").css("display","block");
			$("#datosTransporteDIV").css("display","block");
			$("#EnviarBtn").css("display","block");
			
		}
	
	});
}


function descartaTramite() {
	var estatus = "0";
	$("#nIDEstatus").val("0");

	queryFormPost({
		queryName : "estatusSolViaticos_Read",
		async : false,
		callback : function() {
			if( $("#nIDEstatus").val() == "" )
				$("#nIDEstatus").val("0");
		}
	});
	
	if( parseInt( $("#nIDEstatus").val(), 10) <= 1){
		parent.document.getElementById("pb_cancel").disabled = false;
		parent.document.getElementById("pb_cancel").click();
	}else{
		alert("El tramite se encuentra en un estatus que no permite cancelacion. Solicite apoyo a mesa de ayuda.");
	}
}