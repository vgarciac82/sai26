const CLABE_LENGTH = 18;
const CLABE_WEIGHTS = [3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7];
let proveedor;

const URL_CRUD =
    window.location.protocol +
    "//" +
    window.location.host +
    "/" +
    window.location.pathname.split("/")[1];

$(document).ready(function () {

    $('.mayusculas').on('blur', toUpper);

    $('#pb_save', window.parent.document).hide();
    $('#pb_send', window.parent.document).hide();
    $('#pb_cancel', window.parent.document).hide();
    $('#pb_leave', window.parent.document).hide();


    $("#folio").val(folio);

    $("#guardarBtn").on("click", function () {
        guardarBtnAction();
    });
    $("#cerrarBtn").on("click", function () {
        cerrarBtnAction();
    });
    
    $("#enviarBtn").on("click", function () {
        validaEnvio();
    });

    $("#descartarBtn").on("click", function () {
        descartaTramite();
    });
 
    $("#agregaArchivoBtn").on("click", function () {
        uploadFile();
    });

    $("#agregarCtaBtn").on("click", function () {
        muestraDlgNuevaCuenta();
    });

    $("#tipoPersona").on("change", function () {
        cargaRegimenFiscal();
        muestraCamposTipoPersona();
    });

    $("#estado").on("change", function () {
        cargaMunicipios();
    });

    $("#clabeStr").on("blur", function () {
        muestraElementosCLABE();
    });

    $("#agregarCuentaBtn").on("click", function () {
        guardaCuentaBancaria();
    });

    cargaExpediente(idCaso);
    cargaCuentas(folio);

    $.when(
        loadCatalog(
            "cat_tipo_persona_APPD",
            "tipoPersona",
            function () {
                console.log("Cat. Tipo Persona cargado");
            },
            null
        ),
        loadCatalog(
            "cat_pyme_APPD",
            "pyme",
            function () {
                console.log("Cat. PYME Cargado");
            },
            null
        ),
        loadCatalog(
            "mCatalogoTipoTelefonoRead",
            "tipoTelefono",
            function () {
                console.log("Cat. tipo telefono Cargado");
            },
            null
        ),
        loadCatalog(
            "cat_ef_APPD",
            "estado",
            function () {
                console.log("Cat. estado cargado");
            },
            null
        )
    ).done(function (a1, a2, a3) {
        leeDatosProveedor(folio);
        console.log("Todos los catálogos cargados.");
    });

    console.log("El documento ha sido cargado completamente.");
});

const cargaCuentas = function (folio) {
    $.ajax({
        url:
            "../altaProveedor/cuentasCapturadas?folio=" + encodeURIComponent(folio),
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
                actualizaArbol();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                $.unblockUI();
                console.error("Error:", jqXHR, textStatus, errorThrown);
            },
        });
    }
};

const cargaMunicipios = function (loadingInfo) {
    let estado = $("#estado").val();
    console.log("Estado seleccionado: " + estado);
    loadCatalog(
        "CAT_MUNICIPIO_APPD",
        "municipio",
        function () {
            console.log("Cat. Municipios cargado ");
            if (loadingInfo && proveedor) {
                $("#municipio").val(proveedor.municipio);
            }
        },
        null
    );
};

const cargaRegimenFiscal = function (loadingInfo) {
    let tipoPersona = $("#tipoPersona").val();
    let regimen = "REGIMEN_PERSONA_MORAL_APPD";

    console.log("Tipo de persona seleccionado: " + tipoPersona);
    if (tipoPersona == "2") regimen = "REGIMEN_PERSONA_FISICA_APPD";

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

const muestraDlgNuevaCuenta = function () {
    if (!validarRFC($("#rfc1").val() + $("#rfc2").val() + $("#rfc3").val())) {
        Swal.fire({
            title: "Advertencia",
            text: "Debe capturar el RFC del proveedor antes de agregar una cuenta.",
            icon: "warning",
            confirmButtonText: "Aceptar",
        });
        return;
    }
    console.log("Abriendo el diálogo de nueva cuenta.");
    $("#nuevaCuenta").modal("show");
};

const muestraSubirArchivo = function (fmxNode, docName) {
    $("#destino").val(docName);
    $("#documentSelect").val(fmxNode);
    console.log("Abriendo el diálogo de subir archivo.");
    $("#uploadDocument").modal("show");
};

const makePipeList = function (cols, names) {
    let value, token;
    value = token = "";

    for (let i = 1; i < names.length; i++) {
        let val = eval("cols." + names[i]);
        if (val == "0") val = "";
        value += token + val;
        token = "|";
    }
    return value;
};

const loadCatalog = function (
    queryName,
    targetObjectId,
    callbackSuccess,
    callbackError
) {
    return $.ajax({
        type: "POST",
        url: URL_CRUD + "/crud?rt=s&ql=" + queryName,
        cache: false,
        async: true,
        data: $("form").serialize(),
        error: function (xhr, textStatus, errorThrown) {
            console.error(
                `Advertencia: ${xhr.responseText}\nEstatus: ${textStatus}\n${errorThrown}`
            );
            if (callbackError) callbackError(responseText, textStatus, errorThrown);
        },
        success: function (RS) {
            let index;
            let data;
            let col;
            let names;

            if (RS.success == "true") {
                index = 1;
                while (true) {
                    data = eval("RS.data_" + index++);

                    if (!data) break;

                    names = [];

                    for (let f in data[0]) names.push(f);

                    $("#" + targetObjectId + " option").remove();

                    for (let i = 0; i < data.length; i++) {
                        col = data[i];
                        $("#" + targetObjectId).append(
                            '<option value="' +
                            makePipeList(col, names) +
                            '">' +
                            eval("col." + names[0]) +
                            "</option>"
                        );
                    }
                }
                if (callbackSuccess) callbackSuccess();
            } else {
                if (callbackError) callbackError(RS.message, null, null);
            }
        },
    });
};

const validarRFC = function (rfc, aceptarGenerico) {
    const re =
        /^([A-ZÑ&]{3,4}) ?(?:- ?)?(\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\d|3[01])) ?(?:- ?)?([A-Z\d]{2})([A\d])$/;
    let validado = rfc.match(re);

    if (!validado)
        //Coincide con el formato general del regex?
        return false;

    //Separar el dígito verificador del resto del RFC
    const digitoVerificador = validado.pop(),
        rfcSinDigito = validado.slice(1).join(""),
        len = rfcSinDigito.length,
        //Obtener el digito esperado
        diccionario = "0123456789ABCDEFGHIJKLMN&OPQRSTUVWXYZ Ñ",
        indice = len + 1;
    let suma, digitoEsperado;

    if (len == 12) suma = 0;
    else suma = 481; //Ajuste para persona moral

    for (let i = 0; i < len; i++)
        suma += diccionario.indexOf(rfcSinDigito.charAt(i)) * (indice - i);
    digitoEsperado = 11 - (suma % 11);
    if (digitoEsperado == 11) digitoEsperado = 0;
    else if (digitoEsperado == 10) digitoEsperado = "A";

    //El dígito verificador coincide con el esperado?
    // o es un RFC Genérico (ventas a público general)?
    if (
        digitoVerificador != digitoEsperado &&
        (!aceptarGenerico || rfcSinDigito + digitoVerificador != "XAXX010101000")
    )
        return false;
    else if (
        !aceptarGenerico &&
        rfcSinDigito + digitoVerificador == "XEXX010101000"
    )
        return false;
    return rfcSinDigito + digitoVerificador;
};

const muestraElementosCLABE = function () {
    let elementosIncorrectos = [];
    elementosIncorrectos = validaCLABE($("#clabeStr").val());
    if (elementosIncorrectos.length > 0) {
        Swal.fire({
            title: "Advertencia",
            html:
                "La CLABE es incorrecta, favor de verificar los siguientes elementos: " +
                elementosIncorrectos.join("<br>"),
            icon: "warning",
            confirmButtonText: "Aceptar",
        });
        return;
    }

    let clabe = obtenerElementosCLABE($("#clabeStr").val());
    $("#clabe").val($("#clabeStr").val());

    queryFormPost({
        queryName: "nombre_banco_APPD",
        async: false,
        callback: function (RS) {
            $("#plaza").val(clabe.sucursal);
            $("#cuenta").val(clabe.numeroCuenta);
            $("#digitoControl").val(clabe.digitoControl);
        },
    });
};

const obtenerElementosCLABE = function (clabe) {
    clabe = clabe.replace(/\s+/g, "").replace(/-/g, "");

    let banco = clabe.substr(0, 3);
    let sucursal = clabe.substr(3, 3);
    let digitoControl = clabe.substr(17, 1);
    let numeroCuenta = clabe.substr(6, 11);

    return {
        banco: banco,
        sucursal: sucursal,
        digitoControl: digitoControl,
        numeroCuenta: numeroCuenta,
    };
};

const validaCLABE = function (clabe) {
    clabe = clabe.replace(/\s+/g, "").replace(/-/g, "");
    let elementosIncorrectos = [];

    if (clabe.length !== 18) {
        elementosIncorrectos.push("La CLABE debe tener 18 dígitos.");
    }

    if (!/^\d+$/.test(clabe)) {
        elementosIncorrectos.push("La CLABE debe contener sólo dígitos.");
    }

    /*
      let digitoControl = clabe.substr(17, 1);
      
      let digitoControlCalculado = computeControlDigit(clabe);
      console.log("Digito de control: " + digitoControl) ;
      console.log("Digito de control Calculado: " + digitoControl) ;
  
      if (digitoControlCalculado != parseInt(digitoControl)) {
          elementosIncorrectos.push("El dígito de control de la CLABE es incorrecto.");
      }
     
     */

    return elementosIncorrectos;
};

const computeControlDigit = function (clabe) {
    const clabeList = clabe.split("");
    const clabeInt = clabeList.map((i) => Number(i));
    const weighted = [];

    for (let i = 0; i < CLABE_LENGTH - 1; i++) {
        weighted.push((clabeInt[i] * CLABE_WEIGHTS[i]) % 10);
    }
    const summed = weighted.reduce((curr, next) => curr + next) % 10;
    const controlDigit = (10 - summed) % 10;
    return controlDigit.toString();
};

const guardaCuentaBancaria = function () {
    if (!informacionCompletaCtaBancaria()) {
        Swal.fire({
            icon: "error",
            title: "Error",
            text: "Favor de completar la información de la cuenta bancaria y seleccione el archivo adjunto.",
        });
        return;
    }

    const progressBar = $("#carga-cuenta-progreso");
    const form = $("#agregaCuentaBancariaForm")[0];

    const data = new FormData(form);
    data.append("clabe", $("#clabeStr").val());
    data.append("plaza", $("#plaza").val());
    data.append("folio", $("#folio").val());
    data.append("usuario", $("#login").val());
    data.append("bancoNombre", $("#bancoNombre").val());

    data.append("rfc", $("#rfc1").val() + $("#rfc2").val() + $("#rfc3").val());

    $.ajax({
        url: "../altaProveedor/registraCuentaBancaria",
        type: "POST",
        enctype: "multipart/form-data",
        data: data,
        beforeSend: function () {
            progressBar.css("width", "0%");
            progressBar.text("0%");

            $("#agregarCuentaBtn").attr("disabled", true);
            $("#cancelAgregarCuentaBtn").attr("disabled", true);
        },
        processData: false,
        contentType: false,
        xhr: function () {
            const xhr = new XMLHttpRequest();
            xhr.upload.addEventListener(
                "progress",
                function (event) {
                    if (event.lengthComputable) {
                        const percentComplete = (
                            (event.loaded / event.total) *
                            100
                        ).toFixed(2);
                        progressBar.css("width", percentComplete + "%");
                        progressBar.text(percentComplete + "%");
                    }
                },
                false
            );
            return xhr;
        },
        success: function (response) {
            console.log(response);
            $("#agregarCuentaBtn").attr("disabled", false);
            $("#cancelAgregarCuentaBtn").attr("disabled", false);

            $("#nuevaCuenta").modal("hide");
            Swal.fire({
                icon: "success",
                title: "Cuenta Bancaria Agregada",
                text: "La cuenta bancaria ha sido agregada correctamente.",
            });

            parent.parent.window.frames["doctree"].location.reload();
            cargaCuentas(folio);

        },
        error: function (error) {
            console.log(error);
            $("#agregarCuentaBtn").attr("disabled", false);
            $("#cancelAgregarCuentaBtn").attr("disabled", false);

            Swal.fire({
                icon: "error",
                title: "Error",
                text: "Ocurrió un error al agregar la cuenta bancaria. Intente nuevamente o notifique al administrador.",
            });
            progressBar.css("width", "0%");
            progressBar.text("0%");
        },
    });
};

const informacionCompletaCtaBancaria = function () {
    if ($("#clabeStr").val() === "") {
        return false;
    } else if ($("#bancoNombre").val() === "") {
        return false;
    } else if ($("#plaza").val() === "") {
        return false;
    } else if ($("#cuenta").val() === "") {
        return false;
    } else if ($("#digitoControl").val() === "") {
        return false;
    } else if ($("#sucursal").val() === "") {
        return false;
    } else if ($("#formFile").val() === "") {
        return false;
    }

    return true;
};

const uploadFile = function () {
    if ($("#file").val() === "") {
        Swal.fire({
            icon: "warning",
            title: "Datos Requeridos",
            text: "Debe seleccionar el archivo PDF para adjuntar.",
            confirmButtonText: "Aceptar",
        });
        return;
    }

    const progressBar = $("#upload-progress");
    const form = $("#fileUploadForm")[0];
    const data = new FormData(form);
    data.append("idProcess", idCaso);

    $.ajax({
        url: "../SICOVE/Expedient",
        type: "POST",
        enctype: "multipart/form-data",
        data: data,
        beforeSend: function () {
            progressBar.css("width", "0%");
            progressBar.text("0%");

            $("#cancelaAgregaArchivoBtn").attr("disabled", true);
            $("#agregaArchivoBtn").attr("disabled", true);
        },
        processData: false,
        contentType: false,
        xhr: function () {
            const xhr = new XMLHttpRequest();
            xhr.upload.addEventListener(
                "progress",
                function (event) {
                    if (event.lengthComputable) {
                        const percentComplete = (event.loaded / event.total) * 100;
                        progressBar.css("width", percentComplete + "%");
                        progressBar.text(percentComplete + "%");
                    }
                },
                false
            );
            return xhr;
        },
        success: function (response) {
            console.log(response);
            $("#cancelaAgregaArchivoBtn").attr("disabled", false);
            $("#agregaArchivoBtn").attr("disabled", false);

            $("#uploadDocument").modal("hide");

            Swal.fire({
                icon: "success",
                title: "Archivo Cargado",
                text: "El archivo ha sido cargado correctamente.",
            });
            cargaExpediente(idCaso);
            parent.parent.window.frames["doctree"].location.reload();
        },
        error: function (error) {
            console.log(error);
            $("#cancelaAgregaArchivoBtn").attr("disabled", false);
            $("#agregaArchivoBtn").attr("disabled", false);
            Swal.fire({
                icon: "error",
                title: "Error",
                text: "Ocurrio un error al cargar el archivo.  \n Intente nuevamente, si el problema persiste notifique al administrador.",
                confirmButtonText: "Aceptar",
            });
            progressBar.css("width", "0%");
            progressBar.text("0%");
        },
    });
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

            let tdClabe = $("<td></td>").text(obj.CLABE);
            let tdBanco = $("<td></td>").text(obj.BANCO);
            let tdEstatus = $("<td></td>").text("Capturada");

            let tdAcciones = $("<td></td>");
            let icon = $("<i></i>").addClass("fa-regular fa-eye");
            let iconDel = $("<i></i>").addClass("fa-solid fa-trash");

            let a = $("<a class='m-1'></a>").addClass("btn btn-primary").attr("href", "#");
            a.on("click", function () {
                showDocument(obj.FORTIMAX);
                return false;
            });
            a.append(icon);

            let aDelete = $("<a class='m-1'></a>").addClass("btn btn-primary").attr("href", "#");
            aDelete.on("click", function () {
                eliminaCuenta(obj.CLABE);
                return false;
            });

            aDelete.append(iconDel);

            tdAcciones.append(a);
            tdAcciones.append(aDelete);

            tr.append(tdClabe);
            tr.append(tdBanco);
            tr.append(tdEstatus);
            tr.append(tdAcciones);

            tbody.append(tr);
        });
    }
};

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
            let iconDel = $("<i></i>").addClass("fa-solid fa-trash");

            let a = $("<a></a>").addClass("btn btn-primary").attr("href", "#");
            let aDelete = $("<a class='m-1'></a>").addClass("btn btn-primary").attr("href", "#");
            aDelete.on("click", function () {
                eliminaDocumentoExpediente(obj.fortimax);
                return false;
            });
            
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
            aDelete.append(iconDel);

            tdAcciones.append(a);
            if(obj.attached){
                tdAcciones.append(aDelete);
            }

            tr.append(tdDocumento);
            tr.append(tdAcciones);

            tbody.append(tr);
        });
    }
};

const actualizaArbol = function () {
    $.ajax({
        url: "../altaProveedor/actualizaArbol",
        type: "GET",
        data: {},
        success: function (response) {
            parent.parent.window.frames["doctree"].location.reload();
        },
        error: function (error) {
            console.log(error);
        },
    });
};


const guardarBtnAction = function () {
    console.log("Guardar");
    let form = $("#proveedorForm")[0];

    if (form.checkValidity()) {
        console.log("Formulario Valido");
        $.ajax({
            type: "POST",
            url: "../altaProveedorRapida",
            data: $("#proveedorForm").serialize(),
            beforeSend: function () {
                $("#guardarBtn").attr("disabled", true);
                $.blockUI("Guardando proveedor, espere un momento por favor...");
                $("#proveedorForm").removeClass('was-validated')

            },
            success: function (response) {

                $("#guardarBtn").attr("disabled", false);
                $("#enviarBtn").show();
                console.log("Respuesta del servidor:", response);
                Swal.fire({
                    icon: "success",
                    title: "Proveedor Registrado",
                    text: "El proveedor ha sido registrado correctamente.",
                    confirmButtonText: "Aceptar"
                });
                $.unblockUI();
            },
            error: function (xhr, status, error) {
                $("#guardarBtn").attr("disabled", false);
                console.log("Error en la solicitud AJAX. Error:", error);
                console.log("Error en la solicitud AJAX. Status:", status);
                console.log("Error en la solicitud AJAX. xhr:", xhr);
                $.unblockUI();
                Swal.fire({
                    icon: "error",
                    title: "Error",
                    html: "Ocurrio el siguiente error al registrar el proveedor:<br> " + xhr.responseJSON + "<br> Intente nuevamente, si el problema persiste notifique al administrador.",
                    confirmButtonText: "Aceptar"
                });
            }
        });
    } else {
        console.log("Formulario Invalido");
        $("#proveedorForm").addClass('was-validated')
        Swal.fire({
            icon: "warning",
            title: "Datos Requeridos",
            text: "Favor de completar la información del proveedor.",
            confirmButtonText: "Aceptar"
        });
    }


}

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
             

        }, error: function (error) {
            console.log(error);
        },
    });

}

const llenaInformacion = function (data) {

    if (data) {
        let rfcs = data.rfc.split("-");
        $("#rfc1").val(rfcs[0]);
        $("#rfc2").val(rfcs[1]);
        $("#rfc3").val(rfcs[2]);

        for (let key in data) {
            $("#" + key).val(data[key]);
        }

        cargaMunicipios(true);
        cargaRegimenFiscal(true);
        $("#tipoPersona").change();

        /*Si hay observaciones, mostrar el div observacionesDiv en otro caso ocultarlo*/
        if (data.observaciones) {
            $("#observacionesDiv").show();
        } else {
            $("#observacionesDiv").hide();
        }

        if (idOper == 4) {
            $("#rfcParts").hide();
            $("#rfcCompuesto").val(data.rfc);
            $("#rfcCompuesto").show();

            $("#tipoPersona").hide();
            $("#descTipoPersona").show();

        }
    }
}


const showDocument = function (selectedNode) {
    window.open("../SAIFilestore?select=" + selectedNode, "", "scrollbars=1, resizable=yes, width=900, height=800");
}

const validaEnvio = function () {
    queryFormPost({
        queryName: "constanciaAdjunta",
        async: true,
        beforeSend: function () {
            $.blockUI({ message: "<h1>Validando Constancia Adjunta...</h1>" });
        },
        callback: function () {
            $.unblockUI();
            if (parseInt($("#constancia_adjunta").val()) == 0) {
                Swal.fire({
                    title: "Mensaje",
                    text: "El proveedor no cuenta con la constancia de situación fiscal adjunta",
                    icon: "warning",
                    confirmButtonText: "Aceptar",
                });
            } else {

                queryFormPost({
                    queryName: "cuentaAdjunta",
                    async: true,
                    beforeSend: function () {
                        $.blockUI({ message: "<h1>Validando Cuentas Bancarias...</h1>" });
                    },
                    callback: function () {
                        if (parseInt($("#cuenta_adjunta").val()) == 0) {
                            Swal.fire({
                                title: "Mensaje",
                                text: "El proveedor no cuenta con al menos una cuenta bancaria capturada",
                                icon: "warning",
                                confirmButtonText: "Aceptar",
                            });
                        } else {
                            enviaProveedor();
                        }
                    }
                });
            }
        }
    });
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

const enviaProveedor = function () {
    Swal.fire({
        title: "Mensaje",
        text: "¿Desea enviar el proveedor a validación?",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Aceptar",
        cancelButtonText: "Cancelar",
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                type: "POST",
                url: "../altaProveedorRapida/avanzaAutorizacion",
                cache: false,
                async: true,
                data: $("form").serialize(),
                beforeSend: function () {
                    $.blockUI({ message: "<h1>Enviando Proveedor...</h1>" });
                },
                error: function (xhr, textStatus, errorThrown) {
                    console.error("Error:", xhr, textStatus, errorThrown);
                    Swal.fire({
                        title: "Error",
                        text: "Ocurrió un error al cargar la información",
                        icon: "error",
                        confirmButtonText: "Aceptar",
                    });
                    $.unblockUI();
                },
                success: function (RS) {
                    parent.frmLeave.submit();
                }
            });
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

const eliminaCuenta = function (id) {

    // Confirma antes de enviar la cuenta a eliminar
    Swal.fire({
        title: "Mensaje",
        text: "¿Esta seguro que desea eliminar la cuenta bancaria?",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Aceptar",
        cancelButtonText: "Cancelar",
    }).then((result) => {
        if (result.isConfirmed) {
                    

            $("#clabeEliminar").val(id);

            $.ajax({
                url: "../altaProveedor/eliminaCuenta?clabeEliminar=" + $("#clabeEliminar").val() + "&folio=" + folio,
                type: "DELETE",
                contentType: "application/x-www-form-urlencoded",
                data: $("#proveedorForm").serialize(),
                processData: false,
                beforeSend: function () {
                    $.blockUI({ message: "<h1>Eliminando Cuenta Bancaria...</h1>" });
                },
                success: function (response) {
                    $.unblockUI();
                    console.log(response);

                    Swal.fire({
                        icon: "success",
                        title: "Cuenta Bancaria Eliminada",
                        text: "La cuenta bancaria ha sido eliminada correctamente.",
                    });

                    parent.parent.window.frames["doctree"].location.reload();
                    cargaCuentas(folio);
                    $("#clabeEliminar").val();
                },
                error: function (error) {
                    $("#clabeEliminar").val();
                    $.unblockUI();
                    console.log(error);
                    Swal.fire({
                        icon: "error",
                        title: "Error",
                        text: "Ocurrió un error al eliminar la cuenta bancaria. Intente nuevamente o notifique al administrador.",
                    });

                },
            });
        }
    });
}

const eliminaDocumento = function (id) {

    $("#clabeEliminar").val(id);

    $.ajax({
        url: "../altaProveedor/eliminaCuenta?clabeEliminar=" + $("#clabeEliminar").val() + "&folio=" + folio,
        type: "DELETE",
        contentType: "application/x-www-form-urlencoded",
        data: $("#proveedorForm").serialize(),
        processData: false,
        beforeSend: function () {
            $.blockUI({ message: "<h1>Eliminando Cuenta Bancaria...</h1>" });
        },
        success: function (response) {
            $.unblockUI();
            console.log(response);

            Swal.fire({
                icon: "success",
                title: "Cuenta Bancaria Eliminada",
                text: "La cuenta bancaria ha sido eliminada correctamente.",
            });

            parent.parent.window.frames["doctree"].location.reload();
            cargaCuentas(folio);
            $("#clabeEliminar").val();
        },
        error: function (error) {
            $("#clabeEliminar").val();
            $.unblockUI();
            console.log(error);
            Swal.fire({
                icon: "error",
                title: "Error",
                text: "Ocurrió un error al eliminar la cuenta bancaria. Intente nuevamente o notifique al administrador.",
            });

        },
    });
}

const eliminaDocumentoExpediente = function (fortimaxID) {
    $.ajax({
        url: "../SICOVE/Expedient?cleanOnly=true&nodeId=" + fortimaxID,
        type: "DELETE", 
        beforeSend: function () {
            $.blockUI({ message: "<h1>Eliminando Documento ...</h1>" });
        },
        success: function (response) {

            $.unblockUI();
            console.log(response);

            Swal.fire({
                icon: "success",
                title: "Documento Eliminado",
                text: "El documento ha sido eliminado correctamente.",
            });

            parent.parent.window.frames["doctree"].location.reload();
            cargaExpediente(idCaso); 
        },
        error: function (error) { 
            $.unblockUI();
            console.log(error);
            Swal.fire({
                icon: "error",
                title: "Error",
                text: "Ocurrió un error al eliminar el documento. Intente nuevamente o notifique al administrador.",
            });

        },
    });
}



const descartaTramite = function (fortimaxID) {

    // Confirma antes de enviar la cuenta a eliminar
    Swal.fire({
        title: "Mensaje",
        text: "¿Esta seguro que desea descartar el trámite?",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Aceptar",
        cancelButtonText: "Cancelar",
    }).then((result) => {
            if(result.isConfirmed)
            $.ajax({
                url: "../altaProveedorRapida?rfc=" + $("#rfc").val() +"&folio=" + folio,
                type: "DELETE", 
                beforeSend: function () {
                    $.blockUI({ message: "<h1>Eliminando  ...</h1>" });
                },
                success: function (response) {
                    parent.frmLeave.submit();
                },
                error: function (error) { 
                    $.unblockUI();
                    console.log(error);
                    Swal.fire({
                        icon: "error",
                        title: "Error",
                        text: "Ocurrió un error al eliminar el tramite. Intente nuevamente o notifique al administrador.",
                    });

                },
            });
    });
}