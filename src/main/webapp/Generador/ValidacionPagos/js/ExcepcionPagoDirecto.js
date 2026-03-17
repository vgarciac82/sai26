const dataTableBaseURL = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1];

let user;
let dtProveedores;

$(document).ready(function () {

    $.fn.dataTable.ext.legacy.ajax = true;
    creaDataTable();
    loadUserInfo();
    cargaUnidades();
    cargaCapitulos();

    $("#searchProveedor").on("click", function () {
        searchProveedor();
    });

    $("#eliminarProveedor").on("click", function () {
        elminaProveedor();
    });

    $("#rfc").change(function () { $("#justificacion").focus(); });
    $("#capitulo").change(function () { cargaPartida(); });

    $("#guardarProveedor").on("click", function () {
        
        if( informacionValida() ){
            Swal.fire({
                title: '¿Estas seguro?',
                text: "Se guardara la informacion del proveedor.",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#6c757d',
                confirmButtonText: 'Si, guardar!'
            }).then((result) => {
                if (result.value) {
                    guardarInformacion();
                }
            });
        }else{
            Swal.fire(
                'Error!',
                'Favor de llenar todos los campos.',
                'error'
            );
        }
    });

});

const elminaProveedor = function () {
    if( dtProveedores.row('.selected').data() ){

        Swal.fire({
            title: '¿Estás seguro?',
            text: "Se eliminara el proveedor seleccionado. No se podran realizar mas pagos directos en la unidad ejecutora y partida seleccionada.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            cancelButtonText: 'Cancelar',
            confirmButtonText: 'Sí, eliminar!'
        }).then((result) => {
            if (result.isConfirmed) {
                let data = dtProveedores.row('.selected').data();
                let rfc = data[0];
                let ue = data[6];
                let partida = data[7];
                
                $("#rfcDel").val(rfc);
                $("#ueDel").val(ue);
                $("#partidaDel").val(partida);

                callEliminaRegistro();
            } 
        });
        
    }else{
        Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: 'Selecciona un registro de la tabla!',
        });
    }
    
}

const callEliminaRegistro = function (rfc, ue, partida) {
    
    $.blockUI({ message: "Eliminando proveedor..." });

    queryFormPost(
        {
            queryName: "tProveedoresException300UMASDelete",
            async: true,
            callback: function (data) {
                $.unblockUI();
                Swal.fire(
                    'Exito!',
                    'Se ha eliminado el proveedor correctamente.',
                    'success'
                ).then((result) => {
                    dtProveedores.ajax.reload();
                }
                );
            }
        });
}

const creaDataTable = function () {
    dtProveedores = $('#proveedorTable').DataTable({
        "retrieve": true,
        "destroy": true,
        "serverSide": true,
        "processing": true,
        "ajax": {
            url: dataTableBaseURL + "/crud?rt=t&ql=v_Proveedores_Autorizados_300_UMAS",
            type: 'POST',
        },
        "aoColumns": [
            { "name": "rfc" },
            { "name": "nombre" },
            { "name": "desc_unidad_ejecutora" },
            { "name": "desc_partida" },
            { "name": "justificacion" },
            { "name": "fecha_registro" },
            { "name": "unidad_ejecutora", "bVisible": false },
            { "name": "partida", "bVisible": false }
        ]
    });

    $('#proveedorTable tbody').on('click', 'tr', function () {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        } else {
            dtProveedores.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
    });
}
const informacionValida = function () {
  
    if ($("#rfc").val() == "") {
        return false;
    }
    if ($("#justificacion").val() == "") {
        return false;
    }
    if ($("#uniadEjecutora").val() == "0") {
        return false;
    }
    if ($("#capitulo").val() == "0") {
        return false;
    }
    if ($("#partida").val() == "0") {
        return false;
    }
    if ($("#razonSocial").val() == "") {
        return false;
    }

    return true;
    
}

const guardarInformacion = function () {

    
    queryFormPost(
        {
            queryName: "tProveedoresException300UMASCreate",
            async: true,
            callback: function (data) {
                Swal.fire(
                    'Exito!',
                    'Se ha guardado la informacion correctamente.',
                    'success'
                ).then((result) => {
                    dtProveedores.ajax.reload();
                }
                );
            }
        });
}


const loadUserInfo = function () {

    $.ajax({
        type: "GET",
        url: "../../user/info",
        cache: false,
        async: true,
        beforeSend: function () {
            $.blockUI({ message: "Cargando informacion del usuario..." });
        },
        error: function (xhr, textStatus, errorThrown) {
            $.unblockUI();
            console.error("Error al cargar la informacion del usuario", xhr, textStatus, errorThrown);
            Swal.fire(
                'Error!',
                'Error al cargar la informacion del usuario. Ingrese nuevamente al sistema.',
                'error'
            ).then((result) => {
                window.location.href = "../../login.jsp";
            }
            )
        },
        success: function (RS) {
            user = RS;
            $("#login").val(user.login);
            $.unblockUI();
        }
    });

}

const cargaUnidades = function () {

    $("#ueSpiner").show();
    querySelectPost({
        queryName: "cat_ue_Excepcion300Umas",
        targetObjectId: "uniadEjecutora",
        async: true,
        callback: function (data) {
            $("#ueSpiner").hide();
        }
    });

}

const cargaCapitulos = function () {
    $("#capituloSpiner").show();
    querySelectPost({
        queryName: "cat_Capitulo_Excepcion300Umas",
        targetObjectId: "capitulo",
        async: true,
        callback: function (data) {
            $("#capituloSpiner").hide();
        }
    });
}

const cargaPartida = function () {

    $("#partidaSpiner").show();
    querySelectPost({
        queryName: "cat_partida_Excepcion300Umas",
        targetObjectId: "partida",
        async: true,
        callback: function (data) {
            $("#partidaSpiner").hide();
        }
    });
}


const searchProveedor = () => {
    window.open('../CatalogoBeneficiariosRG.jsp?formName=mainFrm&inputRFCTarget=rfc&inputDRFCTarget=razonSocial', 'Beneficiarios', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');
}
