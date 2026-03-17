const ALL_TERRITORIES = 0;

const EXCEPTIONS = [];

let existsProjectTitle = false;
let existsProjectObjectives = false;

let ProyectoServicio = {
  loginUsuario: userLogin,
  servicioCompleto: false,
  servicioCoordinacion: null,
  servicioCreacion: null,
  servicioDuracion: null,
  servicioFolioAnio: null,
  servicioFolioNum: null,
  servicioFolioPre: null,
  servicioGerencia: null,
  servicioId: null,
  servicioListo: null,
  servicioModificacion: null,
  servicioObjetivos: null,
  servicioTitulo: null,
  servicioVinculacion: null,
  servicioDuracionDias:0,
  estatus: {
    estatusId: 0,
    estatusNombre: "No iniciado",
  },
};

$(document).ready(function () {
  EXCEPTIONS["ampliacion"] = 1;
  EXCEPTIONS["modificacion"] = 2;
  EXCEPTIONS["complemento"] = 3;
  EXCEPTIONS["continuacion"] = 4;

  getInfo(servicioId);

  $("#saveCVFileTDRBtn").on("click", function () {
    saveFileCV(successFileCVUpload, (error) => {
      console.error("Error:", error);
      Swal.fire({
        title: "Error",
        text: "Ocurrió un error al guardar el CV: " + error,
        icon: "error",
        confirmButtonText: "Aceptar",
      });
    });
  });

  $("#duracionProyectoDias").on("input", function() {
      this.value = this.value.replace(/[^0-9]/g, ''); // Solo permite números
  });

  $("#duracionProyecto").on("input", function() {
      this.value = this.value.replace(/[^0-9]/g, ''); // Solo permite números
  });

  $("#saveFileTDRBtn").on("click", function () {
    saveFileTDR(successFileUpload, (error) => {
      console.error("Error:", error);
      Swal.fire({
        title: "Error",
        text:
          "Ocurrió un error al obtener la información del proyecto: " + error,
        icon: "error",
        confirmButtonText: "Aceptar",
      });
    });
  });

  $("#saveBudgetClasificationBtn").on("click", function () {
    saveBudget(successBudgetSaved, (error) => {
      console.error("Error:", error);
      Swal.fire({
        title: "Error",
        text: "Ocurrió un error al guardar la clave presupuestal: " + error,
        icon: "error",
        confirmButtonText: "Aceptar",
      });
    });
  });

  $("#savePaymentBtn").on("click", function () {
    savePayment(successPaymentSaved, (error) => {
      console.error("Error:", error);
      Swal.fire({
        title: "Error",
        text: "Ocurrió un error al guardar el pago: " + error,
        icon: "error",
        confirmButtonText: "Aceptar",
      });
    });
  });

  $("#saveActivitiesBtn").on("click", function () {
    saveActivity(successActivitySaved, (error) => {
      console.error("Error:", error);
      Swal.fire({
        title: "Error",
        text: "Ocurrió un error al guardar la actividad: " + error,
        icon: "error",
        confirmButtonText: "Aceptar",
      });
    });
  });

  $("#saveBtn").on("click", function () {
    console.log("Guardando...");
    if (ProyectoServicio.servicioId == null) saveBtnAction();
    else updateBtnAction();
  });

  $("#printBtn").on("click", function () {
    if (ProyectoServicio.servicioId != null) printBtnAction();
    else
      Swal.fire({
        title: "Error",
        text: "No se puede imprimir un proyecto que no ha sido guardado.",
        icon: "error",
        confirmButtonText: "Aceptar",
      });
  });

  $("#sendBtn").on("click", function () {
    requestRevisionAction();
  });

  $("#generarVersionBtn").on("click", function () {
    generarVersionAction();
  });
});

const successFileUpload = function (data) {
  $.unblockUI();

  if (ProyectoServicio.serviciosTDR == null) {
    ProyectoServicio.serviciosTDR = [];
  }

  ProyectoServicio.serviciosTDR.push(data);
  fillTDRTable(ProyectoServicio.serviciosTDR);

  Swal.fire({
    title: "Éxito",
    text: "Se agrego el archivo exitosamente.",
    icon: "success",
    confirmButtonText: "OK",
  });
};

const fillTDRTable = function (data) {
  $("#tdrTable tbody").empty();

  if (data.length == 0) {
    let row = $("<tr></tr>");
    row.append($("<td colspan='2' class='text-center'>Sin información</td>"));

    $("#tdrTable tbody").append(row);
    return;
  }

  data.forEach((element) => {
    let row = $("<tr></tr>");
    row.append($("<td>" + element.tdrArchivo + "</td>"));

    let btns = $("<td></td>");

    btns.append(
      $("<button></button>")
        .addClass("btn btn-info btn-sm ml-1 pl-1")
        .attr("type", "button")
        .attr("onclick", "showDocument('" + element.tdrRuta + "')")
        .append($("<i></i>").addClass("fas fa-eye"))
    );

    btns.append(
      $("<button></button>")
        .addClass("btn btn-danger btn-sm ml-1")
        .attr("type", "button")
        .attr("onclick", "deleteTDRFile(" + element.tdrId + ")")
        .append($("<i></i>").addClass("fas fa-trash"))
    );

    row.append(btns);

    $("#tdrTable tbody").append(row);
  });
};

const saveFileCV = function (onSuccesUploadCV, onErrorUploadCV) {
  if ($("#saveCVForm")[0].checkValidity() === false) {
    $("#saveCVForm").addClass("was-validated");
    return;
  }

  const progressBar = $("#upload-cvFile-progress");
  const form = $("#saveCVForm")[0];

  const data = new FormData(form);
  data.append("idProcess", ProyectoServicio.idProcess);
  data.append("servicioId", ProyectoServicio.servicioId);
  data.append("folderName", "CV Postulante");
  data.append(
    "procedimientoContratacion",
    $("#procedimientoContratacion").val()
  );
  data.append(
    "nombrePostulante",
    $("#nombrePostulante").val() == ""
      ? "No Aplica"
      : $("#nombrePostulante").val()
  );
  data.append(
    "justicacionPostulacion",
    $("#justicacionPostulacion").val() == ""
      ? "No Aplica"
      : $("#justicacionPostulacion").val()
  );

  $.ajax({
    url: "cvExpedient",
    type: "POST",
    enctype: "multipart/form-data",
    data: data,
    beforeSend: function () {
      $.blockUI();
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
      $.unblockUI();
      console.log(response);
      onSuccesUploadCV(response.tdr);
    },
    error: function (error) {
      $.unblockUI();
      console.log(error);
      onErrorUploadCV(error);
    },
  });
};

const saveFileTDR = function (
  onSuccesUploadVerification,
  onErrorUploadVerification
) {
  if ($("#saveTDRForm")[0].checkValidity() === false) {
    $("#saveTDRForm").addClass("was-validated");
    return;
  }

  const progressBar = $("#upload-file-progress");
  const form = $("#saveTDRForm")[0];

  const data = new FormData(form);
  data.append("idProcess", ProyectoServicio.idProcess);
  data.append("servicioId", ProyectoServicio.servicioId);
  data.append("folderName", "Terminos de Referencia");
  data.append("tdrDescription", $("#tdrDescription").val());

  $.ajax({
    url: "tdrExpedient",
    type: "POST",
    enctype: "multipart/form-data",
    data: data,
    beforeSend: function () {
      $.blockUI();
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
      onSuccesUploadVerification(response.tdr);
    },
    error: function (error) {
      $.unblockUI();
      console.log(error);
      onErrorUploadVerification(error);
    },
  });
};

const saveBtnAction = function () {
  if (status === 0) {
    Swal.fire({
      title: "¿Estás seguro?",
      text: "¿Deseas guardar el proyecto?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Sí",
      cancelButtonText: "No",
    }).then((result) => {
      if (result.isConfirmed) {
        ProyectoServicio.servicioTitulo =
          document.getElementById("titleInput").value;
        ProyectoServicio.servicioObjetivos =
          document.getElementById("objectivesInput").value;
        ProyectoServicio.servicioVinculacion =
          document.getElementById("vinculacionInput").value;
        preSaveUI();
        sendSaveRequest(onRequestSuccess, onRequestError);
      }
    });
  }
};

const preSaveUI = function () {
  $.blockUI({ message: "Guardando espere..." });
  $("#saveBtn").prop("disabled", true);
};

const postSaveUI = function () {
  $.unblockUI();
  $("#saveBtn").prop("disabled", false);
};

const readSaveRequest = function (id, onSuccess, onError) {
  fetch("proyectos?id=" + id, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Error en la solicitud: " + response.status);
      }
      return response.json();
    })
    .then((data) => {
      console.log("Respuesta del servidor:", data);
      onSuccess(data);
    })
    .catch((error) => {
      console.error("Error en la solicitud:", error);
      onError(error);
    });
};

const sendSaveRequest = function (onSuccess, onError) {
  ProyectoServicio.estatus.estatusId = 1;

  fetch("proyectos", {
    method: "POST",
    headers: {
      "Content-Type": "application/json; charset=UTF-8",
    },
    body: JSON.stringify(ProyectoServicio),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Error en la solicitud: " + response.status);
      }
      return response.json();
    })
    .then((data) => {
      console.log("Respuesta del servidor:", data);
      onSuccess(data);
    })
    .catch((error) => {
      console.error("Error en la solicitud:", error);
      onError(error);
    });
};

const onRequestSuccess = function (data) {
  postSaveUI();
  ProyectoServicio = data;

  status = ProyectoServicio.estatus.estatusId;
  servicioId = ProyectoServicio.servicioId;
  activateControls(status);

  $("#folioInput").val(
    ProyectoServicio.servicioFolioPre +
      "-" +
      ProyectoServicio.servicioFolioAnio +
      "-" +
      ProyectoServicio.servicioFolioNum
  );
  console.log("Proyecto guardado con éxito");
  console.log(data);
  Swal.fire({
    title: "Proyecto guardado",
    text: "El proyecto se ha guardado con éxito",
    icon: "success",
    confirmButtonText: "Aceptar",
  });
};

const onRequestError = function (data) {
  postSaveUI();
  console.log(data);
  console.log("Error guardando proyecto");

  Swal.fire({
    title: "Error",
    text: "Ocurrió un error al guardar el proyecto: " + data,
    icon: "error",
    confirmButtonText: "Aceptar",
  });
};

const getInfo = function (servicioId) {
  readSaveRequest(
    servicioId,
    (projectSave) => {
      if (projectSave != null) ProyectoServicio = projectSave;
      status = ProyectoServicio.estatus.estatusId;
      activateControls(status);
      if (status > 0) {
        fillProjectInfo();
        if (ProyectoServicio.observaciones != null) {
          processObservations();
          $("#observationsDiv").show();
        }
      }
    },
    (error) => {
      console.error("Error:", error);
      Swal.fire({
        title: "Error",
        text:
          "Ocurrió un error al obtener la información del proyecto: " + error,
        icon: "error",
        confirmButtonText: "Aceptar",
      });
    }
  );
  activateControls(status);
};

const activateControls = function (status) {
  if (status <= 0) {
    let currentDate = new Date();

    $("#creationDate").val(formatDate(currentDate));
    $("#lastModifiedDate").val(formatDate(currentDate));

    $("#projectDetail-tab").hide();
    $("#duration-tab").hide();
    $("#saveBtn").show();
    $("#saveBtn").prop("disabled", false);
  } else if (status === 1 || status === 8) {
    loadDocumentType();
    loadStateCatalog();
    loadProductCatalog();
    loadConfidentialityCatalog();

    $("#projectDetail-tab").show();
    $("#duration-tab").show();
    $("#saveBtn").text("Actualizar");
    $("#deleteBtn").show();
    $("#sendBtn").show();
    $("#printBtn").show();
  }
};

const loadCatalogMunicipality = function () {
  cleanMunicipalityCatalog();
  if (parseInt(getSelected("state"), 10) <= 0) {
    return;
  }

  loadCatalog("catalogMunicipalityRead", "municipality", null, null);
};

const cleanSubproductCatalog = function () {
  $("#catalogSubproductRead").empty();
  $("#catalogSubproductRead").append(
    '<option value="0">Sin Clase de Producto Seleccionado.</option>'
  );
};

const cleanMunicipalityCatalog = function () {
  $("#municipality").empty();
  $("#municipality").append(
    '<option value="0">Sin Estado seleccionado.</option>'
  );
};

const loadConfidentialityCatalog = function () {
  loadCatalog(
    "catalogConfidentialityRead",
    "confidentialityLevel",
    () => {
      document.getElementById("confidentialityLevel").value =
        ProyectoServicio.confidencialidad.confidencialidadId;
    },
    null
  );
};

const loadProductCatalog = function () {
  loadCatalog("catalogProductRead", "productClasification", null, null);
};

const loadSubproduct = function () {
  cleanSubproductCatalog();
  if (parseInt(getSelected("productClasification"), 10) <= 0) {
    return;
  }
  loadCatalog("catalogSubproductRead", "subProduct", null, null);
};

const loadStateCatalog = function () {
  loadCatalog("catalogStateRead", "state", null, null);
};

const loadDocumentType = function () {
  loadCatalog(
    "catTipoDocumentoSISECOP",
    "tipoProyecto",
    () => {
      if (ProyectoServicio.proyectoTipo)
        $("#tipoProyecto").val(ProyectoServicio.proyectoTipo.tipoProyectoId);
    },
    () => {
      console.error("Error cargando catTipoDocumentoSISECOP");
      Swal.fire({
        title: "Error",
        text: "Ocurrió un error al cargar el catálogo de tipo de proyecto. Refresque la página e intente de nuevo",
        icon: "error",
        confirmButtonText: "Aceptar",
      });
    }
  );
};

const limpiar = function () {
  document.getElementById("resultsTable").innerHTML = "";
  document.getElementById("folio").value = "";
  document.getElementById("titulo").value = "";
  document.getElementById("objetivos").value = "";
};

const clearOtherFields = function (source) {
  if (source === "folio") {
    document.getElementById("titulo").value = "";
    document.getElementById("objetivos").value = "";
  } else {
    document.getElementById("folio").value = "";
  }
};

const mostrarResultados = function (data, titulo, objetivos) {
  const resultsTable = document.getElementById("resultsTable");

  resultsTable.innerHTML = "";

  if (data.hits.hits.length === 0) {
    resultsTable.innerHTML = '<tr><td colspan="4">Sin Resultados</td></tr>';
  } else {
    data.hits.hits.forEach((hit, index) => {
      const row = resultsTable.insertRow(index);

      const cellId = row.insertCell(0);
	  const folio = row.insertCell(1);
      const cellTitulo = row.insertCell(2);
      const cellObjetivos = row.insertCell(3);
      const cellSeleccionar = row.insertCell(4);

      cellId.textContent = hit._source.servicioId;
      cellTitulo.textContent = hit._source.servicioTitulo;
      cellObjetivos.textContent = hit._source.servicioObjetivos;
	  folio.textContent = hit._source.servicioFolio;

      if (ProyectoServicio.servicioId == null || ProyectoServicio.servicioId == 0) {
        // Añadir el icono de seleccionar
        const icon = document.createElement("i");
        icon.className = "fas fa-check-circle"; // Clase FontAwesome para el icono
        icon.style.cursor = "pointer";
        icon.onclick = function () {
          showModalExcepion(hit._source.servicioId, hit._source.servicioTitulo);
        };
        cellSeleccionar.appendChild(icon);
      } else {
        cellSeleccionar.innerHTML = "&nbsp;";
      }

      if (titulo && $.trim(titulo) === $.trim(hit._source.servicioTitulo)) {
        existsProjectTitle = true;
        cellTitulo.style.backgroundColor = "#add8e6";
      }

      if (
        objetivos &&
        $.trim(objetivos) === $.trim(hit._source.servicioObjetivos)
      ) {
        existsProjectObjectives = true;
        cellObjetivos.style.backgroundColor = "#add8e6";
      }
    });
  }
};

let idProyectoSeleccionado = null;
let idTipoExcepcion = null;
let tipoExcepcion = null;
let tituloOriginal = null;

const showModalExcepion = function (id, title) {
  $("#exceptionModal").modal("show");

  idProyectoSeleccionado = id;
  tituloOriginal = title;

  $("#titleNuevoInput").val(title);
  $("#idOriginal").val(id);
  $("#consecutivoNuevo").val("1");

  //createException(id, title);
};

const showExplanation = function (type) {
  $(".excepcion").hide();
  $(`#${type}_explicacion`).show();

  tipoExcepcion = type;
  idTipoExcepcion = EXCEPTIONS[type];

  getLastVersion();
};

const getLastVersion = function () {
  fetch(
    "excepciones/consulta?id_proyecto=" +
      idProyectoSeleccionado +
      "&tipo_excepcion=" +
      idTipoExcepcion,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    }
  )
    .then((response) => response.json())
    .then((data) => {
      let version = parseInt(data.version, 10) + 1;
      let motivoExcepcion = tipoExcepcion + " " + version;
      $("#motivoExcepcionInput").val(motivoExcepcion);
    })
    .catch((error) => console.error("Error:", error));
};

const buscar = function () {
  const titulo = document.getElementById("titleInput").value;
  const objetivos = document.getElementById("objectivesInput").value;
  let bodyQuery;

  existsProjectTitle = false;
  existsProjectObjectives = false;

  if (titulo && objetivos) {
    bodyQuery = {
      query: {
        bool: {
          must: [
            { match: { servicioTitulo: titulo } },
            { match: { servicioObjetivos: objetivos } },
          ],
        },
      },
      sort: [{ _score: { order: "desc" } }],
    };
  } else if (objetivos) {
    bodyQuery = {
      query: {
        match: {
          servicioObjetivos: objetivos,
        },
      },
      sort: [{ _score: { order: "desc" } }],
    };
  } else if (titulo) {
    bodyQuery = {
      query: {
        match: {
          servicioTitulo: titulo,
        },
      },
      sort: [{ _score: { order: "desc" } }],
    };
  }

  if (bodyQuery) {
    fetch(URL_ELASTICSEARCH + "/proyectos/_search", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(bodyQuery),
    })
      .then((response) => response.json())
      .then((data) => {
        mostrarResultados(data, titulo, objetivos);
        if (existsProjectObjectives || existsProjectTitle) {
          Swal.fire({
            title: "Advertencia",
            text: "Se encontraron coincidencias exactas para el titulo y/o los objetivos",
            icon: "warning",
            confirmButtonText: "Aceptar",
          });
        }
      })
      .catch((error) => console.error("Error:", error));
  }
};

const addProduct = function () {
  let productID;
  if (getSelected("productClasification"))
    productID = parseInt(getSelected("productClasification"), 10);
  else {
    showErrorMessage("Debe seleccionar una Clasificación de producto.");
    return;
  }

  let subproductID;
  if (getSelected("subProduct"))
    subproductID = parseInt(getSelected("subProduct"), 10);
  else {
    showErrorMessage("Debe seleccionar una subclasificación de producto.");
    return;
  }

  let productDescription = $("#productDescription").val();

  let newProduct = {
    productoId: subproductID,
    descripcion: productDescription,
    servicioId: ProyectoServicio.servicioId,
  };

  createProduct(newProduct)
    .then((response) => {
      if (!response.ok) {
        throw new Error("Error en la solicitud: " + response.status);
      }
      return response.json();
    })
    .then((data) => {
      console.log("Respuesta del servidor:", data);

      if (ProyectoServicio.productos == null) ProyectoServicio.productos = [];

      ProyectoServicio.productos.push(data);
      fillProductsTable(ProyectoServicio.productos);
    })
    .catch((error) => {
      console.error("Error en la solicitud:", error);
      Swal.fire({
        title: "Error",
        text: "Ocurrió un error al guardar el producto: " + error,
        icon: "error",
        confirmButtonText: "Aceptar",
      });
    });
};

const addTerritory = function () {
  let municipalityId;
  if (getSelected("municipality"))
    municipalityId = parseInt(getSelected("municipality"), 10);
  else municipalityId = -1;

  let stateId = getSelected("state");

  if (stateId == "" || stateId == "-1") {
    showErrorMessage("Debe seleccionar un estado.");
    return;
  }

  if (stateId === ALL_TERRITORIES) municipalityId = 0;
  else if (municipalityId < 0) {
    showErrorMessage("Debe seleccionar un municipio.");
    return;
  }

  let newTeeritory = {
    stateId: stateId,
    municipalityId: municipalityId,
    servicioId: ProyectoServicio.servicioId,
  };

  createTerritory(newTeeritory)
    .then((response) => {
      if (!response.ok) {
        throw new Error("Error en la solicitud: " + response.status);
      }
      return response.json();
    })
    .then((data) => {
      console.log("Respuesta del servidor:", data);
      addTerritoryToTable(data);
    })
    .catch((error) => {
      console.error("Error en la solicitud:", error);
      Swal.fire({
        title: "Error",
        text: "Ocurrió un error al guardar el territorio: " + error,
        icon: "error",
        confirmButtonText: "Aceptar",
      });
    });
};

const createTerritory = function (territory) {
  return fetch("territory", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(territory),
  });
};

const createProduct = function (product) {
  return fetch("products", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(product),
  });
};

const readTerritories = function () {
  return fetch("territory?servicioId=" + ProyectoServicio.servicioId, {
    method: "get",
    headers: {
      "Content-Type": "application/json",
    },
  });
};

const readProducts = function () {
  return fetch("products?servicioId=" + ProyectoServicio.servicioId, {
    method: "get",
    headers: {
      "Content-Type": "application/json",
    },
  });
};

const deleteTerritory = function (territoryId) {
  return fetch(`territory?territorioId=${territoryId}`, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
    },
  });
};

const deleteProduct = function (productID) {
  return fetch(`products?servicioProductoId=${productID}`, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
    },
  });
};

const addProductToTable = function (data) {
  const tableBody = document.getElementById("productTableBody");
  const row = tableBody.insertRow();

  const classCell = row.insertCell();
  classCell.textContent = data.productoClase.claseNombre;

  const productoNombreCell = row.insertCell();
  productoNombreCell.textContent = data.productoNombre;

  const productoDescripcionCell = row.insertCell();
  productoDescripcionCell.textContent = data.productoDescripcion;

  const deleteCell = row.insertCell();
  deleteCell.innerHTML = `<button class="btn btn-danger" onclick="deleteProductAction(${data.servicioProductoID});return false"><i class="fas fa-trash"></i></button>`;

  const initialRow = tableBody.querySelector("tr");
  if (
    initialRow &&
    initialRow.cells.length === 1 &&
    initialRow.cells[0].textContent === "No se ha registrado productos"
  ) {
    tableBody.removeChild(initialRow);
  }
};

const addTerritoryToTable = function (data) {
  const tableBody = document.getElementById("territoryTableBody");
  const row = tableBody.insertRow();

  const stateCell = row.insertCell();
  stateCell.textContent = data.entidadFederativa.nombre;

  const municipalityCell = row.insertCell();
  municipalityCell.textContent = data.municipio.municipioNombre;

  const deleteCell = row.insertCell();
  deleteCell.innerHTML = `<button class="btn btn-danger" onclick="deleteTerritoryAction(${data.territorioId})"><i class="fas fa-trash"></i></button>`;

  const initialRow = tableBody.querySelector("tr");
  if (
    initialRow &&
    initialRow.cells.length === 1 &&
    initialRow.cells[0].textContent === "No se ha registrado territorio"
  ) {
    tableBody.removeChild(initialRow);
  }
};

const deleteProductAction = function (productID) {
  Swal.fire({
    title: "¿Está seguro?",
    text: "Esta acción no se puede deshacer.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Sí, eliminar!",
    cancelButtonText: "Cancelar",
  }).then((result) => {
    console.log(result);
    if (result.isConfirmed) {
      console.log("Eliminando producto...");
      deleteProduct(productID)
        .then((response) => response.json())
        .then((data) => {
          if (data.success) {
            Swal.fire(
              "Eliminado!",
              "El producto ha sido eliminado.",
              "success"
            ).then(() => {
              readProducts()
                .then((response) => response.json())
                .then((data) => {
                  ProyectoServicio.productos = data;
                  fillProductsTable(data);
                })
                .catch((error) => {
                  Swal.fire(
                    "Error!",
                    "Error de comunicación con el servidor mientras se recargaban los productos",
                    "error"
                  );
                });
            });
          } else {
            Swal.fire(
              "Error!",
              "No se pudo eliminar el producto: " + data.message,
              "error"
            );
          }
        })
        .catch((error) => {
          Swal.fire(
            "Error!",
            "Error de comunicación con el servidor.",
            "error"
          );
        });
    }
  });
};

const deleteTerritoryAction = function (territorioId) {
  Swal.fire({
    title: "¿Está seguro?",
    text: "Esta acción no se puede deshacer.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Sí, eliminar!",
    cancelButtonText: "Cancelar",
  }).then((result) => {
    if (result.isConfirmed) {
      deleteTerritory(territorioId)
        .then((response) => response.json())
        .then((data) => {
          if (data.success) {
            Swal.fire(
              "Eliminado!",
              "El territorio ha sido eliminado.",
              "success"
            ).then(() => {
              readTerritories()
                .then((response) => response.json())
                .then((data) => {
                  fillTerritoriesTable(data);
                })
                .catch((error) => {
                  Swal.fire(
                    "Error!",
                    "Error de comunicación con el servidor mientras se recargaban los territorios",
                    "error"
                  );
                });
            });
          } else {
            Swal.fire(
              "Error!",
              "No se pudo eliminar el territorio: " + data.message,
              "error"
            );
          }
        })
        .catch((error) => {
          Swal.fire(
            "Error!",
            "Error de comunicación con el servidor.",
            "error"
          );
        });
    }
  });
};

const fillProductsTable = function (products) {
  $("#productTable > tbody").html("");

  for (let i = 0; i < products.length; i++) {
    addProductToTable(products[i]);
  }
};

const fillTerritoriesTable = function (territories) {
  $("#territoryTable > tbody").html("");

  for (let i = 0; i < territories.length; i++) {
    addTerritoryToTable(territories[i]);
  }
};

const deleteTDRFile = function (idTDR) {
  Swal.fire({
    title: "¿Está seguro de eliminar el registro?",
    text: "Esta operacion no se puede deshacer.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Aceptar",
    cancelButtonText: "Cancelar",
  }).then((result) => {
    if (result.isConfirmed) {
      doDeleteTDR(
        idTDR,
        (data) => {
          console.log(data);

          deleteServicioTDRById(ProyectoServicio, idTDR);
          fillTDRTable(ProyectoServicio.serviciosTDR);

          Swal.fire({
            title: "Éxito",
            text: "Se eliminó el archivo exitosamente.",
            icon: "success",
            confirmButtonText: "OK",
          });
        },
        (error) => {
          console.error("Error:", error);
          Swal.fire({
            title: "Error",
            text: "Ocurrió un error al eliminar el archivo: " + error,
            icon: "error",
            confirmButtonText: "Aceptar",
          });
        }
      );
    }
  });
};

const deleteServicioTDRById = function (servicio, tdrId) {
  servicio.serviciosTDR = servicio.serviciosTDR.filter(
    (tdr) => tdr.tdrId !== tdrId
  );
};

const doDeleteTDR = function (idTDR, onSuccess, onError) {
  $.ajax({
    url: "tdrExpedient?idTDR=" + idTDR,
    type: "DELETE",
    beforeSend: function () {
      $.blockUI({ message: "Eliminando Registro ..." });
    },
    success: function (response) {
      $.unblockUI();
      onSuccess(response);
    },
    error: function (error) {
      $.unblockUI();
      console.log(error);
      onError(error);
    },
  });
};

const saveBudget = function (onSuccess, onError) {
  if ($("#budgetClasificationForm")[0].checkValidity() === false) {
    $("#budgetClasificationForm").addClass("was-validated");
    return;
  }

  const budgetItem = {
    idService: ProyectoServicio.servicioId,
    endYear: $("#endYear").val(),
    initialYear: $("#initialYear").val(),
    management: $("#responsibleManager").val(),
    budgetItem: $("#budgetSection").val(),
    administrativeUnit: $("#administrativeUnit").val(),
  };

  fetch("budget", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(budgetItem),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Error en la solicitud: " + response.status);
      }
      return response.json();
    })
    .then((data) => {
      console.log("Respuesta del servidor:", data);
      onSuccess(data);
    })
    .catch((error) => {
      console.error("Error en la solicitud:", error);
      onError(error);
    });
};

const successBudgetSaved = function (data) {
  if (ProyectoServicio.servicioClaves == null) {
    ProyectoServicio.servicioClaves = [];
  }

  ProyectoServicio.servicioClaves.push(data);
  fillBudgetTable(ProyectoServicio.servicioClaves);

  Swal.fire({
    title: "Éxito",
    text: "Se agrego la clave exitosamente.",
    icon: "success",
    confirmButtonText: "OK",
  });
};

const fillBudgetTable = function (data) {
  $("#budgetTable tbody").empty();

  if (data.length == 0) {
    let row = $("<tr></tr>");
    row.append(
      $(
        "<td colspan='6' class='text-center'>No se ha registrado clave presupuestal</td>"
      )
    );

    $("#budgetTable tbody").append(row);
    return;
  }

  data.forEach((element) => {
    let row = $("<tr></tr>");
    row.append($("<td>" + element.initialYear + "</td>"));
    row.append($("<td>" + element.endYear + "</td>"));
    row.append($("<td>" + element.budgetItem + "</td>"));
    row.append($("<td>" + element.administrativeUnit + "</td>"));
    row.append($("<td>" + element.management + "</td>"));

    let btns = $("<td></td>");

    btns.append(
      $("<button></button>")
        .addClass("btn btn-danger btn-sm ml-1")
        .attr("type", "button")
        .attr("onclick", "deleteBudgetItem(" + element.id + ")")
        .append($("<i></i>").addClass("fas fa-trash"))
    );

    row.append(btns);

    $("#budgetTable tbody").append(row);
  });
};

const deleteBudgetItem = function (idBudget) {
  Swal.fire({
    title: "¿Está seguro de eliminar el registro?",
    text: "Esta operacion no se puede deshacer.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Aceptar",
    cancelButtonText: "Cancelar",
  }).then((result) => {
    if (result.isConfirmed) {
      doDeleteBudget(
        idBudget,
        (data) => {
          console.log(data);

          deleteBudgetById(ProyectoServicio, idBudget);
          fillBudgetTable(ProyectoServicio.servicioClaves);

          Swal.fire({
            title: "Éxito",
            text: "Se eliminó el registro exitosamente.",
            icon: "success",
            confirmButtonText: "OK",
          });
        },
        (error) => {
          console.error("Error:", error);
          Swal.fire({
            title: "Error",
            text: "Ocurrió un error al eliminar el registro: " + error,
            icon: "error",
            confirmButtonText: "Aceptar",
          });
        }
      );
    }
  });
};

const doDeleteBudget = function (id, onSuccess, onError) {
  $.ajax({
    url: "budget?id=" + id,
    type: "DELETE",
    beforeSend: function () {
      $.blockUI({ message: "Eliminando Registro ..." });
    },
    success: function (response) {
      $.unblockUI();
      onSuccess(response);
    },
    error: function (error) {
      $.unblockUI();
      console.log(error);
      onError(error);
    },
  });
};

const deleteBudgetById = function (servicio, id) {
  servicio.servicioClaves = servicio.servicioClaves.filter(
    (budgetObj) => budgetObj.id !== id
  );
};

const saveActivity = function (onSuccess, onError) {
  if ($("#activitiesForm")[0].checkValidity() === false) {
    $("#activitiesForm").addClass("was-validated");
    return;
  }

  let activityDTO = {
    idService: ProyectoServicio.servicioId,
    servicioactividadAnio: $("#activityYear").val(),
    servicioactividadDescripcion: $("#activityDescription").val(),
    sisecopMes: {
      mesId: $("#activityMonth").val(),
    },
  };

  fetch("activity", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(activityDTO),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Error en la solicitud: " + response.status);
      }
      return response.json();
    })
    .then((data) => {
      console.log("Respuesta del servidor:", data);
      onSuccess(data);
    })
    .catch((error) => {
      console.error("Error en la solicitud:", error);
      onError(error);
    });
};

const successActivitySaved = function (data) {
  if (ProyectoServicio.actividades == null) {
    ProyectoServicio.actividades = [];
  }

  ProyectoServicio.actividades.push(data);
  fillActivitiesTable(ProyectoServicio.actividades);

  Swal.fire({
    title: "Éxito",
    text: "Se agrego la actividad exitosamente.",
    icon: "success",
    confirmButtonText: "OK",
  });
};

const fillActivitiesTable = function (data) {
  $("#activitiesTable tbody").empty();

  if (data.length == 0) {
    let row = $("<tr></tr>");
    row.append(
      $(
        "<td colspan='4' class='text-center'>No se ha registrado actividades</td>"
      )
    );

    $("#activitiesTable tbody").append(row);
    return;
  }

  data.forEach((element) => {
    let row = $("<tr></tr>");
    row.append($("<td>" + element.servicioactividadAnio + "</td>"));
    row.append($("<td>" + element.sisecopMes.mesNombre + "</td>"));
    row.append($("<td>" + element.servicioactividadDescripcion + "</td>"));

    let btns = $("<td></td>");

    btns.append(
      $("<button></button>")
        .addClass("btn btn-danger btn-sm ml-1")
        .attr("type", "button")
        .attr("onclick", "deleteActivity(" + element.servicioactividadId + ")")
        .append($("<i></i>").addClass("fas fa-trash"))
    );

    row.append(btns);

    $("#activitiesTable tbody").append(row);
  });
};

const deleteActivity = function (idActivity) {
  Swal.fire({
    title: "¿Está seguro de eliminar el registro?",
    text: "Esta operacion no se puede deshacer.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Aceptar",
    cancelButtonText: "Cancelar",
  }).then((result) => {
    if (result.isConfirmed) {
      doDeleteActivity(
        idActivity,
        (data) => {
          console.log(data);

          deleteActivityById(ProyectoServicio, idActivity);
          fillActivitiesTable(ProyectoServicio.actividades);

          Swal.fire({
            title: "Éxito",
            text: "Se eliminó el registro exitosamente.",
            icon: "success",
            confirmButtonText: "OK",
          });
        },
        (error) => {
          console.error("Error:", error);
          Swal.fire({
            title: "Error",
            text: "Ocurrió un error al eliminar el registro: " + error,
            icon: "error",
            confirmButtonText: "Aceptar",
          });
        }
      );
    }
  });
};

const doDeleteActivity = function (id, onSuccess, onError) {
  $.ajax({
    url: "activity?id=" + id,
    type: "DELETE",
    beforeSend: function () {
      $.blockUI({ message: "Eliminando Registro ..." });
    },
    success: function (response) {
      $.unblockUI();
      onSuccess(response);
    },
    error: function (error) {
      $.unblockUI();
      console.log(error);
      onError(error);
    },
  });
};

const deleteActivityById = function (servicio, id) {
  servicio.actividades = servicio.actividades.filter(
    (activityObj) => activityObj.servicioactividadId !== id
  );
};

// =========================== Payments =====================================

const savePayment = function (onSuccess, onError) {
  if ($("#paymentForm")[0].checkValidity() === false) {
    $("#paymentForm").addClass("was-validated");
    return;
  }

  let paymentDTO = {
    idService: ProyectoServicio.servicioId,
    servicioPagoAnio: $("#paymentYear").val(),
    servicioPagoCantidad: $("#paymentAmount").val(),
    mesPago: {
      mesId: $("#paymentMonth").val(),
    },
  };

  fetch("payment", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(paymentDTO),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Error en la solicitud: " + response.status);
      }
      return response.json();
    })
    .then((data) => {
      console.log("Respuesta del servidor:", data);
      onSuccess(data);
    })
    .catch((error) => {
      console.error("Error en la solicitud:", error);
      onError(error);
    });
};

const successPaymentSaved = function (data) {
  if (ProyectoServicio.pagos == null) {
    ProyectoServicio.pagos = [];
  }

  ProyectoServicio.pagos.push(data);
  fillPaymentTable(ProyectoServicio.pagos);

  Swal.fire({
    title: "Éxito",
    text: "Se agrego el pago exitosamente.",
    icon: "success",
    confirmButtonText: "OK",
  });
};

const fillPaymentTable = function (data) {
  $("#paymentsTable tbody").empty();

  if (data.length == 0) {
    let row = $("<tr></tr>");
    row.append(
      $("<td colspan='4' class='text-center'>No se ha registrado pagos</td>")
    );

    $("#paymentsTable tbody").append(row);
    return;
  }

  data.forEach((element) => {
    let row = $("<tr></tr>");
    row.append($("<td>" + element.servicioPagoAnio + "</td>"));
    row.append($("<td>" + element.mesPago.mesNombre + "</td>"));
    row.append($("<td>" + element.servicioPagoCantidad + "</td>"));

    let btns = $("<td></td>");

    btns.append(
      $("<button></button>")
        .addClass("btn btn-danger btn-sm ml-1")
        .attr("type", "button")
        .attr("onclick", "deletePayment(" + element.servicioPagoId + ")")
        .append($("<i></i>").addClass("fas fa-trash"))
    );

    row.append(btns);

    $("#paymentsTable tbody").append(row);
  });
};

const deletePayment = function (idPayment) {
  Swal.fire({
    title: "¿Está seguro de eliminar el registro?",
    text: "Esta operacion no se puede deshacer.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Aceptar",
    cancelButtonText: "Cancelar",
  }).then((result) => {
    if (result.isConfirmed) {
      doDeletePayment(
        idPayment,
        (data) => {
          console.log(data);

          deletePaymentById(ProyectoServicio, idPayment);
          fillPaymentTable(ProyectoServicio.pagos);

          Swal.fire({
            title: "Éxito",
            text: "Se eliminó el registro exitosamente.",
            icon: "success",
            confirmButtonText: "OK",
          });
        },
        (error) => {
          console.error("Error:", error);
          Swal.fire({
            title: "Error",
            text: "Ocurrió un error al eliminar el registro: " + error,
            icon: "error",
            confirmButtonText: "Aceptar",
          });
        }
      );
    }
  });
};

const doDeletePayment = function (id, onSuccess, onError) {
  $.ajax({
    url: "payment?id=" + id,
    type: "DELETE",
    beforeSend: function () {
      $.blockUI({ message: "Eliminando Registro ..." });
    },
    success: function (response) {
      $.unblockUI();
      onSuccess(response);
    },
    error: function (error) {
      $.unblockUI();
      console.log(error);
      onError(error);
    },
  });
};

const deletePaymentById = function (servicio, id) {
  servicio.pagos = servicio.pagos.filter(
    (paymentObj) => paymentObj.servicioPagoId !== id
  );
};

const updateBtnAction = function () {
  //Pide confirmacion antes de enviar a actulizar llamando el metodo sendUpdateRequest
  Swal.fire({
    title: "¿Está seguro de actualizar el proyecto?",
    text: "Los datos se actualizaran con la información actual.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Aceptar",
    cancelButtonText: "Cancelar",
  }).then((result) => {
    if (result.isConfirmed) {
      $.blockUI({ message: "Actualizando Proyecto ..." });
      sendUpdateRequest(
        (data) => {
          $.unblockUI();
          onRequestSuccess(data);
          fillProjectInfo();
        },
        (error) => {
          $.unblockUI();
          console.error("Error:", error);
          Swal.fire({
            title: "Error",
            text: "Ocurrió un error al actualizar el proyecto: " + error,
            icon: "error",
            confirmButtonText: "Aceptar",
          });
        }
      );
    }
  });
};

const sendUpdateRequest = function (onSuccess, onError) {
  ProyectoServicio.servicioTitulo = $("#titleInput").val();
  ProyectoServicio.servicioObjetivos = $("#objectivesInput").val();
  ProyectoServicio.confidencialidad.confidencialidadId = $(
    "#confidentialityLevel"
  ).val();
  ProyectoServicio.servicioVinculacion = $("#vinculacionInput").val();
  ProyectoServicio.servicioDuracion = $("#duracionProyecto").val();
  ProyectoServicio.servicioDuracionDias = $("#duracionProyectoDias").val();
  ProyectoServicio.servicioGerencia = $("#servicioGerencia").val();
  ProyectoServicio.servicioCoordinacion = $("#servicioCoordinacion").val();
  ProyectoServicio.proyectoTipo.tipoProyectoId = $("#tipoProyecto").val();

  fetch("proyectos", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(ProyectoServicio),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Error en la solicitud: " + response.status);
      }
      return response.json();
    })
    .then((data) => {
      console.log("Respuesta del servidor:", data);
      onSuccess(data);
    })
    .catch((error) => {
      console.error("Error en la solicitud:", error);
      onError(error);
    });
};

const fillProjectInfo = function () {
  $.blockUI({ message: "Cargando Información del Proyecto ..." });
  document.getElementById("titleInput").value = ProyectoServicio.servicioTitulo;
  document.getElementById("objectivesInput").value =
    ProyectoServicio.servicioObjetivos;
  document.getElementById("vinculacionInput").value =
    ProyectoServicio.servicioVinculacion;
  document.getElementById("creationDate").value = formatDate(
    new Date(ProyectoServicio.servicioCreacion)
  );
  document.getElementById("lastModifiedDate").value = formatDate(new Date());
  buscar();
  $("#tipoProyecto").val(ProyectoServicio.proyectoTipo.tipoProyectoId);
  document.getElementById("confidentialityLevel").value =
    ProyectoServicio.confidencialidad.confidencialidadId;

  if (ProyectoServicio.servicioGerencia != null) {
    document.getElementById("servicioGerencia").value =
      ProyectoServicio.servicioGerencia;
  }

  if (ProyectoServicio.servicioCoordinacion != null) {
    document.getElementById("servicioCoordinacion").value =
      ProyectoServicio.servicioCoordinacion;
  }

  document.getElementById("folioInput").value =
    ProyectoServicio.servicioFolioPre +
    "-" +
    ProyectoServicio.servicioFolioAnio +
    "-" +
    ProyectoServicio.servicioFolioNum;

  if (ProyectoServicio.servicioDuracion != null) {
    document.getElementById("duracionProyecto").value =
      ProyectoServicio.servicioDuracion;
 
  }

  if( ProyectoServicio.servicioDuracionDias )
     $("#duracionProyectoDias").val(ProyectoServicio.servicioDuracionDias);

  if (ProyectoServicio.territorios) {
    fillTerritoriesTable(ProyectoServicio.territorios);
  }

  if (ProyectoServicio.productos) {
    fillProductsTable(ProyectoServicio.productos);
  }

  if (ProyectoServicio.serviciosTDR) {
    fillTDRTable(ProyectoServicio.serviciosTDR);
  }

  if (ProyectoServicio.servicioClaves) {
    fillBudgetTable(ProyectoServicio.servicioClaves);
  }

  if (ProyectoServicio.actividades) {
    fillActivitiesTable(ProyectoServicio.actividades);
  }

  if (ProyectoServicio.pagos) {
    fillPaymentTable(ProyectoServicio.pagos);
  }
  $.unblockUI();
};

const disableActions = function () {
  $("#sendBtn").prop("disabled", true);
  $("#saveBtn").prop("disabled", true);
  $("#printBtn").prop("disabled", true);
  $("#deleteBtn").prop("disabled", true);
};

const enableActions = function () {
  $("#sendBtn").prop("disabled", false);
  $("#saveBtn").prop("disabled", false);
  $("#printBtn").prop("disabled", false);
  $("#deleteBtn").prop("disabled", false);
};

const validateRequestRevision = function () {
  let missingFields = [];
  if ($("#titleInput").val() === "") {
    missingFields.push("titleInput");
  }
  if ($("#objectivesInput").val() === "") {
    missingFields.push("objectivesInput");
  }
  if ($("#confidentialityLevel").val() === "") {
    missingFields.push("confidentialityLevel");
  }
  if ($("#duracionProyecto").val() === "") {
    missingFields.push("duracionProyecto");
  }
  if( $("#duracionProyectoDias").val() === "") {
    missingFields.push("duracionProyecto");
  }
  
  if ($("#servicioGerencia").val() === "") {
    missingFields.push("servicioGerencia");
  }
  if ($("#servicioCoordinacion").val() === "") {
    missingFields.push("servicioCoordinacion");
  }
  if ($("#tipoProyecto").val() === "") {
    missingFields.push("tipoProyecto");
  }

  return missingFields;
};

const requestRevisionAction = function () {
  if (ProyectoServicio.servicioId == null) {
    Swal.fire({
      title: "Error",
      text: "No se ha seleccionado un proyecto",
      icon: "error",
      confirmButtonText: "Aceptar",
    });
    return;
  }

  const value = parseInt($("#duracionProyectoDias").val(), 10);
  if (isNaN(value) || value < 0 || value > 31) {
    Swal.fire({
      title: "Error",
      text: "La duración del proyecto en días debe ser un número entero.",
      icon: "error",
      confirmButtonText: "Aceptar",
    });
    return;
  } 
  
  const valueMes = parseInt($("#duracionProyecto").val(), 10);
  if (isNaN(valueMes) || valueMes <= 0 || valueMes > 60) {
    Swal.fire({
      title: "Error",
      text: "La duración del proyecto en meses debe ser un número entero mayor a cero.",
      icon: "error",
      confirmButtonText: "Aceptar",
    });
    return;
  } 

  let missingFields = validateRequestRevision();
  
  let missingFieldsNames = "";

  if (missingFields.length > 0) {
    missingFields.forEach((field) => {
      $("#" + field).addClass("is-invalid");
      missingFieldsNames += field + "\n ";
    });
    Swal.fire({
      title: "Error",
      text: "Debe llenar los campos del formulario: \n" + missingFieldsNames,
      icon: "error",
      confirmButtonText: "Aceptar",
    });
    return;
  }

  ProyectoServicio.estatus.estatusId = 2;

  // Confirma la operacion antes de enviar.
  Swal.fire({
    title: "¿Está seguro de enviar el proyecto a revisión?",
    text: "El proyecto se enviará a revisión con la información actual.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Aceptar",
    cancelButtonText: "Cancelar",
  }).then((result) => {
    if (result.isConfirmed) {
      $.blockUI({ message: "Enviando a revisión del proyecto ..." });
      requestRevision(
        (data) => {
          $.unblockUI();
          Swal.fire({
            title: "Éxito",
            text: "Se ha solicitado la revisión del proyecto exitosamente.",
            icon: "success",
            confirmButtonText: "Aceptar",
          }).then(() => {
            $.blockUI({ message: "Cargando..." });
            parent.window.frames["content-iframe"].location.href =
              "RegisterProject.jsp";
          });
        },
        (error) => {
          $.unblockUI();
          enableActions();
          ProyectoServicio.estatus.estatusId = 1;

          Swal.fire({
            title: "Error",
            text:
              "Ocurrió un error al solicitar la revisión del proyecto: " +
              error,
            icon: "error",
            confirmButtonText: "Aceptar",
          });
        }
      );
    }
  });
};

const requestRevision = function (onSuccess, onError) {
  fetch("proyectosWorkflow", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(ProyectoServicio),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Error en la solicitud: " + response.status);
      }
      return response.json();
    })
    .then((data) => {
      console.log("Respuesta del servidor:", data);
      onSuccess(data);
    })
    .catch((error) => {
      console.error("Error en la solicitud:", error);
      onError(error);
    });
};

const processObservations = function () {
  Swal.fire({
    title: "Observaciones",
    text:
      "El tramite se ha rechazado por las siguientes observaciones:\n " +
      ProyectoServicio.observaciones,
    icon: "info",
    confirmButtonText: "Aceptar",
  });
};

const generarVersionAction = function () {
  $("#exceptionModal").modal("hide");

  Swal.fire({
    title: "¿Está seguro de generar una nueva versión del proyecto?",
    text: "Se generará una nueva versión con la información actual.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Aceptar",
    cancelButtonText: "Cancelar",
  }).then((result) => {
    if (result.isConfirmed) {
      // Datos a enviar en la solicitud POST
      const data = {
        idProyectoOriginal: idProyectoSeleccionado,
        idTipoExcepcion: idTipoExcepcion,
        login: userLogin,
        tituloProyecto: tituloOriginal,
      };

      // Realiza la solicitud POST a la URL relativa "excepciones"
      $.ajax({
        url: "excepciones",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (response) {
          // Asigna el resultado al objeto ProyectoServicio
          ProyectoServicio = response;

          // Llama las funciones necesarias
          fillProjectInfo();

          status = ProyectoServicio.estatus.estatusId;
          servicioId = ProyectoServicio.servicioId;
          activateControls(status);

          $("#folioInput").val(
            ProyectoServicio.servicioFolioPre +
              "-" +
              ProyectoServicio.servicioFolioAnio +
              "-" +
              ProyectoServicio.servicioFolioNum
          );

          Swal.fire(
            "Versión generada",
            "La nueva versión del proyecto ha sido generada exitosamente.",
            "success"
          );
        },
        error: function (xhr, status, error) {
          Swal.fire(
            "Error",
            "Ocurrió un error al generar la nueva versión del proyecto. Por favor, intente de nuevo.",
            "error"
          );
        },
      });
    }
  });
};


const printBtnAction = function () {
  const url = `anexo2/download?id=${ProyectoServicio.servicioId}`;
  
  // Abrir la URL en una nueva ventana o pestaña
  window.open(url, '_blank');
};




 