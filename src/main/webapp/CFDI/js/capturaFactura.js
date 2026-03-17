let cfdi = {
  encabezado: {
    cfdiId: 0,
    version: "4.0",
    serie: {},
    folio: "",
    fecha: "",
    sello: "",
    formaPago: null,
    noCertificado: "",
    certificado: "",
    condicionesDePago: null,
    subTotal: 0,
    moneda: null,
    total: 0,
    tipoDeComprobante: null,
    metodoPago: null,
    lugarExpedicion: null,
    regimenFiscal: null,
    donativoAutorizacion: "",
    donativoFechaAutorizacion: "",
    tipoRelacion: null,
    usoCFDI: null,
    receptor: null,
    hora: "00",
    minutos: "00",
    estatusId: 0
  },
  detalles: [],
};
const es_mx = {
  sProcessing: "Procesando...",
  sLengthMenu: "Mostrar _MENU_ registros",
  sZeroRecords: "No hay registros a mostrar",
  sEmptyTable: "No hay datos en la tabla",
  sLoadingRecords: "Cargando...",
  sInfo: "Registros _START_ al _END_ de _TOTAL_",
  sInfoEmpty: "Registro 0 al 0 de 0",
  sInfoFiltered: "(filtered from _MAX_ total entries)",
  sInfoPostFix: "",
  sInfoThousands: ",",
  sSearch: "Filtro:",
  oPaginate: {
    sFirst: "Primero",
    sPrevious: "Ant.",
    sNext: "Sigte.",
    sLast: "&Uacute;ltimo",
  },
};

let selectedRow = null;

$(document).ready(function () {
  $.when(
    loadCatalog("cat_serie_cfdi_read", "serie"),
    loadCatalog("cat_uso_cfdi", "usoCfdi"),
    loadCatalog("cat_condiciones_pago", "condicionesPago"),
    loadCatalog("cat_tipo_comprobante", "tipoComprobante"),
    loadCatalog("cat_metodo_pago", "metodoPago"),
    loadCatalog("cat_forma_pago", "formaPago"),
    loadCatalog("cat_moneda_read", "moneda"),
    loadCatalog("cat_unidad_cfdi", "unitMeasurement"),
    loadCatalog("cat_prodserv_cfdi", "descSAT")

  ).done(function () {
    if (idInvoice > 0)
      loadInvoice(idInvoice);
  });

  $("#searchBtn").on("click", function () {
    filterTable();
  });

  $("#clearBtn").on("click", function () {
    $(".filter").val("");
    clearTable();
  });

  createTable();

  $("#selectRFCBtn").on("click", function () {
    selectedRow = dtResultTable.row(".selected").data();
    if (selectedRow != null) {
      loadClient(selectedRow.RFC);
    }
    $("#searchClientModal").modal("hide");
  });

  $("#searchClientModal").on("shown.bs.modal", function () {
    $(".filter").val("");
    clearTable();
  });

  $("input, select").on("input", function () {
    $(this).removeClass("is-invalid");
  });

  $("#enviarBtn").on("click", function () {
    solicitaAutCFDI();
  });

  $("#guardarBtn").on("click", function () {
    if (validarFormulario()) {
      let fechaCompleta = $('#fecha').val() + " " + $('#hora').val() + ":" + $('#minutos').val();
      let fechaFormatoISO = moment.tz(fechaCompleta, "YYYY-MM-DD HH:mm", "America/Mexico_City").format("YYYY-MM-DDTHH:mm:ss");

      cfdi.encabezado.serie = { idSerie: $("#serie").val() };
      cfdi.encabezado.moneda = { moneda: $("#moneda").val() };
      cfdi.encabezado.fecha = fechaFormatoISO;
      cfdi.encabezado.hora = $("#hora").val();
      cfdi.encabezado.minutos = $("#minutos").val();
      cfdi.encabezado.tipoDeComprobante = { tipoDeComprobante: $("#tipoComprobante").val() };
      cfdi.encabezado.formaPago = { formaPago: $("#formaPago").val() };
      cfdi.encabezado.condicionesDePago = $("#condicionesPago").val();
      cfdi.encabezado.metodoPago = { metodoPago: $("#metodoPago").val() };
      cfdi.encabezado.lugarExpedicion = { codigoPostal: $("#lugarExpedicion").val() };
      cfdi.encabezado.usoCFDI = { usoCFDI: $("#usoCfdi").val() };

      // Asignar receptor
      cfdi.encabezado.receptor = {
        receptorID: $("#clienteId").val(),
        rfc: $("#rfcClient").val(),
        nombre: $("#razonSocialClient").val(),
        regimenFiscal: $("#regimenFiscalCliente").val(),
        domicilioFiscal: $("#codigoPostalCliente").val(),
        email: $("#emailCliente").val()
      };

      // Verificar si ya se había guardado (cfdiId > 0)
      if (cfdi.encabezado.cfdiId && cfdi.encabezado.cfdiId > 0) {
        enviarCFDI("PUT");
      } else {
        enviarCFDI("POST");
      }
    }
  });

  $("#btn-agregar-detalle").on("click", function () {
    if (!validarDetalle()) {
      return;
    }

    saveDetail();

  });

  // Eventos para actualizar el importe en tiempo real
  $("#cantidad, #precioUnit, #descuento").on("input", actualizarImporte);


});

const enviarCFDI = function (metodo, esAvance) {
  let url = "../CFDIManagment";
  console.log("Sending cfdi by " + metodo);

  if (metodo === "PUT") {
    url += "?cfdiId=" + cfdi.encabezado.cfdiId;
  }

  $.ajax({
    url: url,
    type: metodo,
    contentType: "application/json",
    data: JSON.stringify(cfdi),
    beforeSend: function () {
      disableActions();
      $.blockUI({ message: "Guardando CFDI..." });
    },
    complete: function () {
      enableActions();
      $.unblockUI();
    },
    success: function (data) {
      esAvance = esAvance || false;
      // Éxito: CFDI guardado correctamente
      cfdi = data;
      if (esAvance) {
        parent.window.frames["content-iframe"].location.href = "CapturaPendienteCFDI.jsp"
      } else {
        fillForm(data);
        Swal.fire({
          icon: "success",
          title: "Éxito",
          text: "CFDI guardado correctamente",
        });
      }
    },
    error: function (jqXHR) {
      console.log(jqXHR);
      let errorMsg = "Hubo un problema al guardar el CFDI";

      // Verificar si la respuesta contiene un JSON con el campo "error"
      if (jqXHR.responseText) {
        try {
          const responseJson = JSON.parse(jqXHR.responseText);
          if (responseJson.error) {
            errorMsg = responseJson.error;
          }
        } catch (e) {
          console.error("Error parsing JSON response:", e);
        }
      }
      if (esAvance)
        cfdi.encabezado.estatusId = 0;

      Swal.fire({
        icon: "error",
        title: "Error",
        text: errorMsg,
      });
    },
  });
};

const loadClient = function (rfc) {
  $.ajax({
    url: SYSTEM_URL + "/cfdi/receptor?rfc=" + rfc,
    type: "GET",
    success: function (data) {
      $("#clienteId").val(data.receptorID);
      $("#rfcClient").val(data.rfc);
      $("#razonSocialClient").val(data.nombre);
      $("#regimenFiscalCliente").val(
        data.regimenFiscal ? data.regimenFiscal : ""
      );
      $("#codigoPostalCliente").val(data.domicilioFiscal);
      $("#emailCliente").val(data.email);
    },
    error: function (jqXHR, textStatus, errorThrown) {
      Swal.fire({
        icon: "error",
        title: "Oops...",
        text: "No se pudo cargar el cliente!",
      });
    },
  });
};

const clearTable = function () {
  dtResultTable.ajax.url(SYSTEM_URL + "/crud?rt=nt&ql=receptor&qw=1<>1").load();
};

const SYSTEM_URL =
  window.location.protocol +
  "//" +
  window.location.host +
  "/" +
  window.location.pathname.split("/")[1];
let dtResultTable;

const createTable = function () {
  let condition = " 1<>1";
  dtResultTable = new DataTable("#resultTable", {
    ajax: {
      url: SYSTEM_URL + "/crud?rt=nt&ql=receptor&qw=" + condition,
      type: "POST",
    },
    columns: [{ data: "ReceptorID" }, { data: "RFC" }, { data: "Nombre" }],
    order: [[2, "asc"]],
    rowId: "ReceptorID",
    processing: true,
    serverSide: true,
    scrollCollapse: true,
    scrollY: "200px",
    language: es_mx,
  });

  dtResultTable.on("click", "tbody tr", (e) => {
    let classList = e.currentTarget.classList;

    if (classList.contains("selected")) {
      classList.remove("selected");
    } else {
      dtResultTable
        .rows(".selected")
        .nodes()
        .each((row) => row.classList.remove("selected"));
      classList.add("selected");
    }
  });
};

const filterTable = function () {
  let condition = "";
  let token = "";

  if ($("#rfc").val() != "") {
    condition += "RFC like '%" + $("#rfc").val() + "%'";
    token = " AND ";
  }

  if ($("#nombre").val() != "") {
    condition += token + "Nombre like '%" + $("#nombre").val() + "%'";
  }

  if (condition === "") {
    Swal.fire({
      icon: "error",
      title: "Oops...",
      text: "Debe ingresar al menos un criterio de búsqueda!",
    });
  } else {
    let encodedCondition = encodeURIComponent(condition);
    dtResultTable.ajax
      .url(SYSTEM_URL + "/crud?rt=nt&ql=receptor&qw=" + encodedCondition)
      .load();
  }
};

const loadCatalog = function (queryName, targetObjectId) {
  return $.ajax({
    type: "POST",
    url: SYSTEM_URL + "/crud?rt=s&ql=" + queryName,
    cache: false,
    async: true,
    data: $("form").serialize(),
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
      }
    },
    error: function (xhr, textStatus, errorThrown) {
      console.error(
        `Advertencia: ${xhr.responseText}\nEstatus: ${textStatus}\n${errorThrown}`
      );
      if (callbackError) callbackError(responseText, textStatus, errorThrown);
    },
  });
};

const makePipeList = function (cols, names) {
  let value, token;
  value = token = "";

  for (let i = 1; i < names.length; i++) {
    let val = eval("cols." + names[i]);

    value += token + val;
    token = "|";
  }
  return value;
};

const validarFormulario = function () {
  // Obtener los valores de los campos
  const clienteId = $("#clienteId").val().trim();
  const serie = $("#serie").val().trim();
  const moneda = $("#moneda").val().trim();
  const fecha = $("#fecha").val().trim();
  const hora = $("#hora").val().trim();
  const minutos = $("#minutos").val().trim();
  const tipoComprobante = $("#tipoComprobante").val().trim();
  const formaPago = $("#formaPago").val().trim();
  const condicionesPago = $("#condicionesPago").val().trim();
  const metodoPago = $("#metodoPago").val().trim();
  const lugarExpedicion = $("#lugarExpedicion").val().trim();
  const usoCfdi = $("#usoCfdi").val().trim();

  // Lista de campos faltantes
  let camposFaltantes = [];

  // Verificar cada campo requerido
  if (!clienteId) camposFaltantes.push("Cliente ID");
  if (!serie) camposFaltantes.push("Serie");
  if (!moneda) camposFaltantes.push("Moneda");
  if (!fecha) camposFaltantes.push("Fecha");
  if (!hora) camposFaltantes.push("Hora");
  if (!minutos) camposFaltantes.push("Minutos");
  if (!tipoComprobante) camposFaltantes.push("Tipo de Comprobante");
  if (!formaPago) camposFaltantes.push("Forma de Pago");
  if (!condicionesPago) camposFaltantes.push("Condiciones de Pago");
  if (!metodoPago) camposFaltantes.push("Método de Pago");
  if (!lugarExpedicion) camposFaltantes.push("Lugar de Expedición");
  if (!usoCfdi) camposFaltantes.push("Uso de CFDI");

  // Resaltar campos faltantes
  resaltarCamposFaltantes([
    { id: "#clienteId", valido: !!clienteId },
    { id: "#serie", valido: !!serie },
    { id: "#moneda", valido: !!moneda },
    { id: "#fecha", valido: !!fecha },
    { id: "#hora", valido: !!hora },
    { id: "#minutos", valido: !!minutos },
    { id: "#tipoComprobante", valido: !!tipoComprobante },
    { id: "#formaPago", valido: !!formaPago },
    { id: "#condicionesPago", valido: !!condicionesPago },
    { id: "#metodoPago", valido: !!metodoPago },
    { id: "#lugarExpedicion", valido: !!lugarExpedicion },
    { id: "#usoCfdi", valido: !!usoCfdi },
  ]);

  // Mostrar mensaje si hay campos faltantes
  if (camposFaltantes.length > 0) {
    Swal.fire({
      icon: "error",
      title: "Campos faltantes",
      text: `Por favor, complete los siguientes campos: ${camposFaltantes.join(
        ", "
      )}.`,
    });
    return false; // No continuar con la operación
  }

  // Si no hay campos faltantes, permitir continuar
  return true;
};

const resaltarCamposFaltantes = function (campos) {
  campos.forEach((campo) => {
    if (!campo.valido) {
      $(campo.id).addClass("is-invalid"); // Agregar borde rojo
    } else {
      $(campo.id).removeClass("is-invalid"); // Eliminar borde rojo si ya está completado
    }
  });
};

const validarDetalle = function () {
  const cantidad = parseFloat($("#cantidad").val());
  const valorUnitario = parseFloat($("#precioUnit").val());
  const descuento = parseFloat($("#descuento").val());

  if (cantidad <= 0 || valorUnitario <= 0) {
    Swal.fire({
      icon: 'error',
      title: 'Error',
      text: 'Las cantidades y el valor unitario deben ser mayores a cero.',
    });
    return false;
  }

  if (descuento > 100) {
    Swal.fire({
      icon: 'error',
      title: 'Error',
      text: 'El descuento no puede ser mayor al 100%.',
    });
    return false;
  }

  return true;
}


const saveDetail = function () {
  $("#spinSaveDetail").show();

  $("#btn-agregar-detalle").prop("disabled", true);
  $("#cancelaAddDetail").prop("disabled", true);


  const detalle = fillDetalle();

  $.ajax({
    url: '../CFDIManagment/detalle',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(detalle),
    success: function (response) {
      $("#spinSaveDetail").hide();
      $("#detailModal").modal("hide");
      agregarRenglonDetalle(response);

      cfdi.detalles.push(response);

      limpiarFormularioDetalle();

      Swal.fire({
        icon: 'success',
        title: 'Éxito',
        text: 'Detalle agregado correctamente.',
      });

      $("#btn-agregar-detalle").prop("disabled", false);
      $("#cancelaAddDetail").prop("disabled", false);

    },
    error: function (xhr) {
      $("#spinSaveDetail").hide();
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'Hubo un problema al agregar el detalle. Intente nuevamente.',
      });

      $("#detalleModal").modal("show");
      $("#btn-agregar-detalle").prop("disabled", false);
      $("#cancelaAddDetail").prop("disabled", false);
    }
  });
}

const fillDetalle = function () {
  return detalle = {
    cfdiId: cfdi.encabezado.cfdiId,
    claveProdServ: { claveProdServ: $("#descSAT").val() },
    noIdentificacion: $("#idSAT").val(),
    cantidad: $("#cantidad").val(),
    claveUnidad: { claveUnidad: $("#unitMeasurement").val() },
    unidad: $('select[name="unitMeasurement"] option:selected').text(),
    descripcion: $("#desc").val(),
    valorUnitario: parseFloat($("#precioUnit").val()),
    descuento: $("#descuento").val(),
    objetoImp: { objetoImp: "01" },
    importe: $("#importe").val()
  };
}

const agregarRenglonDetalle = function (detalle) {
  $("#detalleTable tbody").append(`
      <tr data-id="${detalle.cfdiDetalleId}">
          <td>${detalle.noIdentificacion}</td>
          <td>${detalle.claveProdServ.descripcion}</td>
          <td>${detalle.cantidad}</td>
          <td>${detalle.claveUnidad.descripcion}</td>
          <td>${detalle.descripcion}</td>
          <td>${detalle.valorUnitario}</td>
          <td>${detalle.importe}</td>
          <td>${detalle.descuento}</td>
          <td>0.00</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>
              <button type="button"
                      role="button"
                      onclick="eliminarRenglonDetalle(${detalle.cfdiDetalleId})">
                  <i class="fa-solid fa-trash-can"></i>
              </button>
          </td>
      </tr>
  `);
}


const limpiarFormularioDetalle = function () {
  $("#claveProdServ").val('');
  $("#noIdentificacion").val('');
  $("#cantidad").val('');
  $("#claveUnidad").val('');
  $("#unidad").val('');
  $("#descripcion").val('');
  $("#valorUnitario").val('');
  $("#importe").val('');
  $("#descuento").val('');
  $("#objetoImp").val('');
}


// Función para actualizar el importe
const actualizarImporte = function () {
  const cantidad = parseFloat($("#cantidad").val()) || 0;
  const valorUnitario = parseFloat($("#precioUnit").val()) || 0;
  const descuento = parseFloat($("#descuento").val()) || 0;

  // Calcular importe con fórmula
  const importe = (cantidad * valorUnitario) * (1 - (descuento / 100));

  // Actualizar el campo importe con el resultado, con dos decimales
  $("#importe").val(importe.toFixed(2));
}


const loadInvoice = function (idInvoice) {

  $.ajax({
    url: '../CFDIManagment?idInvoice=' + idInvoice,
    type: 'GET',
    success: function (data) {
      cfdi = data;
      fillForm(data);
    },
    beforeSend: function () {
      $.blockUI({ message: 'Cargando factura...' });
    },
    complete: function () {
      $.unblockUI();
    },
    error: function (jqXHR, textStatus, errorThrown) {
      if (jqXHR.status === 401) {
        Swal.fire({
          icon: 'warning',
          title: 'Sesión expirada',
          text: 'Por favor, inicia sesión nuevamente.',
          didClose: () => {
            window.location.href = '../index.jsp';
          }
        });
      } else if (jqXHR.status === 404) {
        Swal.fire({
          icon: 'error',
          title: 'CFDI no encontrado',
          text: `No se encontró el CFDI con el folio ${idInvoice}.`
        });
      } else if (jqXHR.status === 500) {
        Swal.fire({
          icon: 'error',
          title: 'Error interno del servidor',
          text: 'Hubo un error al cargar la factura. Por favor, inténtalo de nuevo.'
        });
      } else {
        Swal.fire({
          icon: 'error',
          title: 'Error desconocido',
          text: 'No se pudo cargar la factura.'
        });
      }
    }
  });
};

const fillForm = function (data) {
  // Encabezado
  $('#clienteId').val(data.encabezado.receptor.receptorID);
  $('#rfcClient').val(data.encabezado.receptor.rfc);
  $('#razonSocialClient').val(data.encabezado.receptor.nombre);
  $('#regimenFiscalCliente').val(data.encabezado.receptor.regimenFiscal ? data.encabezado.receptor.regimenFiscal : "");
  $('#codigoPostalCliente').val(data.encabezado.receptor.domicilioFiscal);
  $('#emailCliente').val(data.encabezado.receptor.email);
  $('#serie').val(data.encabezado.serie.idSerie);
  $('#moneda').val(data.encabezado.moneda.moneda);
  $('#tipoCambio').val(data.encabezado.tipoCambio);

  $('#fecha').val(moment(data.encabezado.fecha, 'MMM DD, YYYY').format('YYYY-MM-DD'));

  setHourAndMinute(data.encabezado.fecha);

  $('#formaPago').val(data.encabezado.formaPago.formaPago);
  $('#metodoPago').val(data.encabezado.metodoPago.metodoPago);
  $('#cuentaPago').val(''); // Si hay cuenta de pago, agrégala en el JSON.
  $('#tipoComprobante').val(data.encabezado.tipoDeComprobante.tipoDeComprobante);
  $('#condicionesPago').val(data.encabezado.condicionesDePago);
  $('#lugarExpedicion').val(data.encabezado.lugarExpedicion.codigoPostal);
  $('#usoCfdi').val(data.encabezado.usoCFDI.usoCFDI);

  $("#detalle").show();
  $("#tab-Detalle").show();
  $("#enviarBtn").show();

  // Detalles
  const detalleTable = $('#detalleTable tbody');
  detalleTable.empty(); // Limpia la tabla antes de agregar filas.
  data.detalles.forEach((detalle, index) => {
    agregarRenglonDetalle(detalle);
  });
};


const solicitaAutCFDI = function () {

  if (cfdi.detalles.length === 0) {
    Swal.fire({
      icon: 'error',
      title: 'Error',
      text: 'No se puede enviar un CFDI sin detalles.'
    });
    return;
  }

  Swal.fire({
    icon: 'question',
    title: 'Enviar CFDI',
    text: '¿Estás seguro de enviar este CFDI a autorización?',
    showCancelButton: true,
    confirmButtonText: 'Sí',
    cancelButtonText: 'No',
  }).then((result) => {
    if (result.isConfirmed) {
      let fechaCompleta = $('#fecha').val() + " " + $('#hora').val() + ":" + $('#minutos').val();
      let fechaFormatoISO = moment.tz(fechaCompleta, "YYYY-MM-DD HH:mm", "America/Mexico_City").format("YYYY-MM-DDTHH:mm:ss");
      cfdi.encabezado.fecha = fechaFormatoISO;
      cfdi.encabezado.estatusId = 1;
      enviarCFDI("PUT", true);
    }
  }
  );


}

const disableActions = function () {
  $("#guardarBtn").prop("disabled", true);
  $("#enviarBtn").prop("disabled", true);
  $("#eliminarBtn").prop("disabled", true);
}

const enableActions = function () {
  $("#guardarBtn").prop("disabled", false);
  $("#enviarBtn").prop("disabled", false);
  $("#eliminarBtn").prop("disabled", false);
}

const eliminarRenglonDetalle = function (idDetalle) {
  Swal.fire({
    icon: 'warning',
    title: 'Eliminar detalle',
    text: '¿Estás seguro de eliminar este detalle?',
    showCancelButton: true,
    confirmButtonText: 'Sí',
    cancelButtonText: 'No',
  }).then((result) => {
    if (result.isConfirmed) {
      $.ajax({
        url: `../CFDIManagment/detalle?idDetalle=${idDetalle}`,
        type: 'DELETE',
        beforeSend: function () {
          disableActions();
          $.blockUI({ message: 'Eliminando detalle...' });
        },
        complete: function () {
          enableActions();
          $.unblockUI();
        },
        success: function () {


          Swal.fire({
            icon: 'success',
            title: 'Éxito',
            text: 'Detalle eliminado correctamente.',
          });

          // Eliminar el detalle de la lista
          cfdi.detalles = cfdi.detalles.filter(detalle => detalle.cfdiDetalleId !== idDetalle);

          // Eliminar el renglón de la tabla
          $(`#detalleTable tbody tr[data-id="${idDetalle}"]`).remove();
        },
        error: function (xhr) {
          if (xhr.status === 401) {
            Swal.fire({
              icon: 'warning',
              title: 'Sesión expirada',
              text: 'Tu sesión ha expirado. Por favor, inicia sesión nuevamente.',
            }).then(() => {
              window.location.href = '../login.jsp';
            });
          } else if (xhr.status === 400) {
            Swal.fire({
              icon: 'error',
              title: 'Solicitud inválida',
              text: 'No se pudo procesar la solicitud. Verifica los datos e intenta nuevamente.',
            });
          } else if (xhr.status === 404) {
            Swal.fire({
              icon: 'info',
              title: 'No encontrado',
              text: `No se encontró el detalle con ID ${idDetalle}.`,
            });
          } else if (xhr.status === 500) {
            Swal.fire({
              icon: 'error',
              title: 'Error del servidor',
              text: 'Ocurrió un error al intentar eliminar el detalle. Intenta nuevamente más tarde.',
            });
          } else {
            Swal.fire({
              icon: 'error',
              title: 'Error inesperado',
              text: 'Algo salió mal. Por favor, intenta nuevamente.',
            });
          }
        }
      });
    }
  });
}


const setHourAndMinute = function (dateInput) {

  const date = typeof dateInput === "string" ? new Date(dateInput) : dateInput;

  if (isNaN(date.getTime())) {
    console.error("Fecha inválida proporcionada:", dateInput);
    return;
  }

  const hora24 = date.getHours();
  const minutos = date.getMinutes();

  $("#hora").val(hora24);
  $("#minutos").val(minutos);
}
