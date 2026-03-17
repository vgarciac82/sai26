const es_mx = {
    "processing": "Procesando...",
    "info": "Registros _START_ al _END_ de _TOTAL_",
    "infoEmpty": "Mostrando 0 a 0 de 0 registros",
    "emptyTable": "Sin registros en la tabla",
    "infoFiltered": "(filtrado de _MAX_ registros)",
    "lengthMenu": "Mostrar _MENU_ registros",
    "zeroRecords": "No hay registros a mostrar",
    "emptyTable": "No hay datos en la tabla",
    "loadingRecords": "Cargando...",
    "infoEmpty": "Registro 0 al 0 de 0",

    "infoPostFix": "",
    "thousands": ",",
    "search": "Filtro:",
    "paginate": {
        "first": "Primero",
        "previous": "Ant.",
        "next": "Sigte.",
        "last": "&Uacute;ltimo"
    },
    "aria": {
        "orderable": "Ordenar por esta columna",
        "orderableReverse": "Orden inverso esta columna",
    }
};




let dtRegisterInbox;

$(document).ready(

    function () {
        $.blockUI({ message: '<h1>Cargando...</h1>' });
        if (views === "*") {
            $.when(
                loadCatalog(
                    "cat_ue_Excepcion300Umas",
                    "executiveUnit",
                    function () {
                        console.log("Cat. Unit Exec. loaded");
                    }
                )
            ).done(function () {
                $.unblockUI();
                $("#executiveUnit").val(executiveUnit);
                createTable();
            });
        } else {

            viewList.forEach(function (element) {
                $("#executiveUnit").append(
                    '<option value="' + element.ue + '">' + element.ue + ' ' + element.descripcion + '</option>'
                );
            });

            $("#executiveUnit").val(executiveUnit);
            createTable();
            $.unblockUI();
        }


    });


const createTable = function () {

    let condition = " estatusid IN (1,8) and (login_captura = '" + user + "' OR cUnidadEjecutora = '" + $("#executiveUnit").val() + "')";
    dtRegisterInbox = new DataTable('#pendingProjects', {

        ajax: {
            url: SYSTEM_URL + "/crud?rt=nt&ql=v_inbox_sisecop&qw=" + condition,
            type: 'POST'
        },
        columns: [
            { data: 'folio' },
            { data: 'servicioTitulo' },
            { data: 'usuario_captura' },
            { data: 'servicioId',
              render: function (data, type) {

                    if (type === 'display') {
                        return '<a href="#" onclick="showRequest(' + data + ');return false;"><i class="fa-solid fa-arrow-right"></i></a>';
                    }
    
                    return data;
                }
             }
        ],
        order: [[3, 'asc']],
        rowId: 'folio',
        processing: true,
        serverSide: true,
        scrollCollapse: true,
        scrollY: '200px',
        language: es_mx,
    });
}

const showRequest = function (folio) {

   
    Swal.fire({
        title: '¿Desea continuar la solicitud?',
        showDenyButton: true,
        confirmButtonText: `Si`,
        denyButtonText: `No`,
    }).then((result) => {
        if (result.isConfirmed) {
            parent.window.frames['content-iframe'].location.href = 'CaptureProjectInfo.jsp?folio=' + folio;
        }else{
            return;
        }
    });
    
}

const registerProject = function(){
    parent.window.frames['content-iframe'].location.href = 'CaptureProjectInfo.jsp';
}

const filterTable = function () {
    let condition = " estatusid IN (1,8) and (login_captura = '" + user + "' OR cUnidadEjecutora = '" + $("#executiveUnit").val() + "')";
    dtRegisterInbox.ajax.url(SYSTEM_URL + "/crud?rt=nt&ql=v_inbox_sisecop&qw=" + condition).load();
}
