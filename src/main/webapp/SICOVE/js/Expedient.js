let expedient;


const select = $('#documentSelect');
(function() {
    $("#documentSelect").on("change", function() {
        onChangeDocumentSelected();
    });
})();

const getExpedient = function(idProcess) {
    if (idProcess > 0) {

        $("#idProcess").val(idProcess);
        $.ajax({
            url:window.location.protocol + "//"
				+ window.location.host + "/"
				+ window.location.pathname.split("/")[1]
				+ "/SICOVE/Expedient?ID_PROCESS=" + idProcess,
            type: 'GET',
            contentType: 'application/json',
            success: function(data) {
                expedient = data;
                showExpedient();
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.error('Error:', jqXHR, textStatus, errorThrown);
            }
        });
    }
}

const getCapturedExpedient = function(idProcess) {
    if (idProcess > 0) {
        $("#idProcess").val(idProcess);
        $.ajax({
            url: window.location.protocol + "//"
				+ window.location.host + "/"
				+ window.location.pathname.split("/")[1]
				+ "/SICOVE/CapturedExpedient?ID_PROCESS=" + idProcess,
            type: 'GET',
            contentType: 'application/json',
            success: function(data) {
                expedient = data;
                showExpedient();
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.error('Error:', jqXHR, textStatus, errorThrown);
            }
        });
    }
}



const showExpedient = function() {
    $('#documentSelect').empty();
    if (expedient.length > 0) {
        addInitialOption();
        expedient.forEach(element => addDocument(element));
    } else
        addInitialOption("Sin Expediente Capturado");
}

const addInitialOption = function(optTxt) {

    let option = $('<option>');
    option.val("");
    if (optTxt)
        option.text(optTxt)
    else
        option.text("Seleccione un Documento");
    select.append(option);
}

const addDocument = function(fortimaxDocument) {

    let option = $('<option>');
    option.val(fortimaxDocument.fortimax);
    option.text(fortimaxDocument.documentPath);
    option.data('attached', fortimaxDocument.attached);
    select.append(option);
}


const onChangeDocumentSelected = function() {
    const selectedOption = select.find('option:selected');
    const attached = selectedOption.data('attached');

    if (selectedOption.val() === "") {
        $(".documentTools").hide();
        $('.uploadFile').hide();
        return;
    }

    if (attached) {
        showDocumentTools(selectedOption.val());
    } else {
        showUpload(selectedOption.val());
    }
}

const showUpload = function(fortimaxNode) {
    if (fortimaxNode === "")
        $('.uploadFile').hide();
    else
        $('.uploadFile').show();
}



const uploadFile = function() {
	
	if( $("#file").val() === "" ){
		Swal.fire({
	        icon: 'warning',
	        title: 'Datos Requeridos',
	        text: 'Debe seleccionar el archivo PDF para adjuntar.',
	        confirmButtonText: 'Aceptar'
	    });
	    return;
	}
		
    const progressBar = $('#upload-progress');
    const form = $('#fileUploadForm')[0];
    const data = new FormData(form);

    $.ajax({
        url: window.location.protocol + "//"
			+ window.location.host + "/"
			+ window.location.pathname.split("/")[1]
			+ "/SICOVE/Expedient",
        type: 'POST',
        enctype: 'multipart/form-data',
        data: data,
        beforeSend: function() {
            $.blockUI({ message: 'Procesando...' });
        },
        processData: false,
        contentType: false,
        xhr: function() {
            const xhr = new XMLHttpRequest();
            xhr.upload.addEventListener('progress', function(event) {
                if (event.lengthComputable) {
                    const percentComplete = event.loaded / event.total * 100;
                    progressBar.css('width', percentComplete + '%');
                    progressBar.text(percentComplete + '%');
                }
            }, false);
            return xhr;
        },
        success: function(response) {
            $.unblockUI();
            onSuccesUpload();
        },
        error: function(error) {
            console.log(error);
            $.unblockUI();
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Ocurrio un error al cargar el archivo.  \n Intente nuevamente, si el problema persiste notifique al adminitrador.',
                confirmButtonText: 'Aceptar'
            });
            progressBar.css('width', '0%');
            progressBar.text( '0%');
        }
    });
}

const showFile = function() {
    const selectedOption = select.find('option:selected');
    showDocument(selectedOption.val());
}

const showDocument = function(selectedNode) {
    window.open("../SAIFilestore?select=" + selectedNode, "", "scrollbars=1, resizable=yes, width=900, height=800");
}

const showDocumentTools = function(selectedNode) {
    if (selectedNode !== "")
        $(".documentTools").show();
    else
        $(".documentTools").hide();
}

const onSuccesUpload = function() {

    Swal.fire({
        icon: 'success',
        title: '¡Archivo cargado exitosamente!',
        text: 'El archivo se ha cargado correctamente.',
        confirmButtonText: 'Aceptar'
    });
    $(".documentTools").hide();
    $('.uploadFile').hide();
    getExpedient($("#idProcess").val());

}