
let proveedor;


$(document).ready(function () {

    $('.mayusculas').on('blur', toUpper);

    $('#pb_save', window.parent.document).hide();
    $('#pb_send', window.parent.document).hide();
    $('#pb_cancel', window.parent.document).hide();
    $('#pb_leave', window.parent.document).hide();

    $("#folio").val(folio);

    cargaExpediente(idCaso);

    leeDatosProveedor(folio);

    console.log("El documento ha sido cargado completamente.");

        
    
    $("#cerrarBtn").on("click", function () {
        cerrarBtnAction();
    });

});
   

const cargaCuentas = function (rfc) {
    $.ajax({
        url:
            "../altaProveedor/cuentasAutorizadas?rfc=" + encodeURIComponent(rfc),
        type: "GET",
        contentType: "application/json",
        beforeSend: function () {
            $("#tablaCuentas tbody").empty();
        },
        success: function (data) {
            generarTablaCuentas(data);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                title: "Error",
                text: "Ocurrió un error al cargar las cuentas bancarias del proveedor.",
                icon: "error",
                confirmButtonText: "Aceptar",
            });
            console.error("Error:", jqXHR, textStatus, errorThrown);
        },
    });
};

const cargaRegimenFiscal = function (loadingInfo) {
    let tipoPersona = $("#tipoPersona").val();
    let regimen = "AUT_REGIMEN_PERSONA_MORAL_APPD";

    console.log("Tipo de persona seleccionado: " + tipoPersona);
    if (tipoPersona == "2") regimen = "AUT_REGIMEN_PERSONA_FISICA_APPD";

    loadCatalog(
        regimen,
        "regimenFiscal",
        function () {

            if (loadingInfo && proveedor) {
                $("#regimenFiscal").val(proveedor.regimenFiscal);
            }
        },
        null
    );
};

const cargaExpediente = function (idProcess) {
    if (idProcess > 0) {
        let excludeParam = "Comprobantes Bancarios";
        let encodedURL =
            "../SICOVE/Expedient?ID_PROCESS=" +
            encodeURIComponent(idProcess) +
            "&EXCLUDE=" +
            encodeURIComponent(excludeParam);

        $.ajax({
            url: encodedURL,
            type: "GET",
            contentType: "application/json",
            beforeSend: function () {
                $.blockUI({ message: "<h1>Cargando...</h1>" });
                $("#tablaExpediente tbody").empty();
            },
            success: function (data) {
                $.unblockUI();
                generarTabla(data);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                $.unblockUI();
                console.error("Error:", jqXHR, textStatus, errorThrown);
            },
        });
    }
};


const generarTablaCuentas = function (data) {
    let tbody = $("#tablaCuentas tbody");
    if (data.length === 0) {
        let tr = $("<tr></tr>");
        let tdSinExpediente = $("<td></td>")
            .attr("colspan", 4)
            .text("Sin Cuentas Bancarias Registradas");

        tr.append(tdSinExpediente);
        tbody.append(tr);
    } else {
        data.forEach(function (obj) {
            console.log(obj);
            let tr = $("<tr></tr>");

            let tdClabe = $("<td></td>").text(obj.CCLABEINTERBANCARIA);
            let tdBanco = $("<td></td>").text(obj.CNAMEBANCO);
            let tdEstatus = $("<td></td>").text(
				obj.NBCBENVIADOSICOP == 1? "AUTORIZADA":"PENDIENTE SICOP"
			);

            tr.append(tdClabe);
            tr.append(tdBanco);
            tr.append(tdEstatus);

            tbody.append(tr);
        });
    }
};

  

const muestraCamposTipoPersona = function () {
    let tipoPersona = $("#tipoPersona").val();
    console.log("Tipo de persona seleccionado: " + tipoPersona);
    if (tipoPersona == "2") {
        $("#curp").prop("required", true);
        $("#razonSocial").prop("required", false);
        $(".personaFisica").show();
        $(".personaMoral").hide();
    } else {
        $("#curp").prop("required", false);
        $("#razonSocial").prop("required", true);
        $(".personaFisica").hide();
        $(".personaMoral").show();
    }

}
   
 

const generarTabla = function (data) {
    let tbody = $("#tablaExpediente tbody");
    if (data.length === 0) {
        let tr = $("<tr></tr>");
        let tdSinExpediente = $("<td></td>")
            .attr("colspan", 2)
            .text("Sin Expediente");

        tr.append(tdSinExpediente);
        tbody.append(tr);
    } else {
        data.forEach(function (obj) {
            
            let tr = $("<tr></tr>");
            let docName =
                '<i class="fa-regular fa-file"></i>&nbsp;' + obj.documentName;
            let tdDocumento = $("<td></td>").html(docName);

            let tdAcciones = $("<td></td>");
            let icon = $("<i></i>").addClass("fa-regular fa-eye");
            let a = $("<a></a>").addClass("btn btn-primary").attr("href", "#");

            if (obj.attached) {
                a.on("click", function () {
                    showDocument(obj.fortimax);
                    return false;
                });
            } else {
                a.on("click", function () {
                    muestraSubirArchivo(obj.fortimax, obj.documentName);
                    return false;
                });

                icon = $("<i></i>").addClass("fas fa-file-upload");
            }

            a.append(icon);
            tdAcciones.append(a);

            tr.append(tdDocumento);
            tr.append(tdAcciones);

            tbody.append(tr);
        });
    }
};
 

 

const toUpper = function () {
    $(this).val(function (_, textoActual) {
        return textoActual.toUpperCase();
    });
}



const leeDatosProveedor = function (folio) {
    $.ajax({
        url: "../altaProveedorRapida?folio=" + folio,
        type: "GET",
        data: {},
        success: function (response) {
            proveedor = response;
            console.log(response);
            llenaInformacion(response);
			cargaCuentas(response.rfc);
        }, error: function (error) {
            console.log(error);
        },
    });

}

const llenaInformacion = function (data) {
    if (data) {

        for (let key in data) {
            $("#" + key).val(data[key]);
        }

        muestraCamposTipoPersona();
        /*Si hay observaciones, mostrar el div observacionesDiv en otro caso ocultarlo*/
        if (data.observaciones) {
            $("#observacionesDiv").show();
        } else {
            $("#observacionesDiv").hide();
        }
    }
}


const showDocument = function (selectedNode) {
    window.open("../SAIFilestore?select=" + selectedNode, "", "scrollbars=1, resizable=yes, width=900, height=800");
}


const queryFormPost = function (settings) {

    $.ajax({

        type: "POST",
        url: URL_CRUD + "/crud?rt=s&ql=" + settings.queryName,
        cache: false,
        async: true,
        data: $("form").serialize(),
        beforeSend: function () {
            if (settings.onBeforeSend) settings.onBeforeSend();
        },
        error: function (xhr, textStatus, errorThrown) {
            console.error("Error:", xhr, textStatus, errorThrown);
            Swal.fire({
                title: "Error",
                text: "Ocurrió un error al cargar la información",
                icon: "error",
                confirmButtonText: "Aceptar",
            });

        },
        success: function (RS) {
            let index, data, col;
            if (RS.success == "true") {
                index = 1;
                while (true) {
                    data = eval("RS.data_" + index++);
                    if (!data)
                        break;
                    for (let i = 0; i < data.length; i++) {
                        col = data[i];
                        for (let name in col) {
                            $("#" + name).val(eval("col." + name));
                        }
                    }
                }
                if (settings.callback) settings.callback();
            } else {
                Swal.fire({
                    title: "Mensaje",
                    text: RS.message,
                    icon: "warning",
                    confirmButtonText: "Aceptar"
                });
            }
        }
    });
}
 
const cerrarBtnAction = function () {
    Swal.fire({
        title: "Confirmar Operacion",
        text: "¿Desea cerrar el tramite? Los cambios no guardados se perderan.",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Aceptar",
        cancelButtonText: "Cancelar",
    }).then((result) => {
        if (result.isConfirmed) {
            $.blockUI( { message: "<h1>Cerrando Trámite...</h1>" });
            parent.frmLeave.submit();
        }
    });
}
