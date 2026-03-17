let existsProjectObjectives = false;
let existsProjectTitle = false;

const ALL_TERRITORIES = 0;

 
let ProyectoServicio;

$(document).ready(function() {
	
	getInfo(servicioId);
	if(!consulta){
		
		
		$("#saveBtn").on("click", function() { authProjectAction() });
		$("#correctBtn").on("click", function() {

			Swal.fire({
				title: "¿Está seguro de rechazar el proyecto?",
				text: "Se solicitará la correccion o eliminación del proyecto.",
				icon: "warning",
				showCancelButton: true,
				confirmButtonColor: "#3085d6",
				cancelButtonColor: "#d33",
				confirmButtonText: "Aceptar",
				cancelButtonText: "Cancelar",
			}).then((result) => {
				if (result.isConfirmed) {
					$('#observationsModal').modal('show');
				}
			}
			);
		});
	}
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


	$("#btnReturnRequest").on("click", function() {
		sendReturnRequest();
	});

});

const getInfo = function(servicioId) {
	$.blockUI({ message: "Cargando Información del Proyecto ..." });
	readSaveRequest(
		servicioId,
		(projectSave) => {
			ProyectoServicio = projectSave;
			status = ProyectoServicio.estatus.estatusId;
			activateControls(status);
			fillProjectInfo();
			$.unblockUI();
		},
		(error) => {
			$.unblockUI();
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
};

const readSaveRequest = function(id, onSuccess, onError) {

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


const activateControls = function(status) {
	if (status == 2) {
		$("#saveBtn").show();
		$("#saveBtn").prop("disabled", false);

		$("#correctBtn").show();
		$("#correctBtn").prop("disabled", false);
	}if(status==10){
		$("#proyectosTable").hide();
	}
	
};



const fillProjectInfo = function() {

	document.getElementById("titleInput").value =
		ProyectoServicio.servicioTitulo;
	document.getElementById("objectivesInput").value =
		ProyectoServicio.servicioObjetivos;
	document.getElementById("vinculacionInput").value =
		ProyectoServicio.servicioVinculacion;
	document.getElementById("creationDate").value = formatDate(
		new Date(ProyectoServicio.servicioCreacion)
	);
	document.getElementById("lastModifiedDate").value = formatDate(new Date(ProyectoServicio.servicioModificacion)
	);
	
	if(!consulta)
		buscar();

	$("#tipoProyecto").val(ProyectoServicio.proyectoTipo.tipoProyectoNombre);

	document.getElementById("confidentialityLevel").value = ProyectoServicio.confidencialidad.confidencialidadNombre;


	if (ProyectoServicio.servicioGerencia != null) {
		document.getElementById("servicioGerencia").value = ProyectoServicio.servicioGerencia;
	}

	if (ProyectoServicio.servicioCoordinacion != null) {
		document.getElementById("servicioCoordinacion").value = ProyectoServicio.servicioCoordinacion;
	}

	document.getElementById("folioInput").value =
		ProyectoServicio.servicioFolioPre +
		"-" +
		ProyectoServicio.servicioFolioAnio +
		"-" +
		ProyectoServicio.servicioFolioNum;

	if (ProyectoServicio.servicioDuracion != null) {
		document.getElementById("duracionProyecto").value = ProyectoServicio.servicioDuracion + ' Meses';
		document.getElementById("duracionProyectoDias").value = ProyectoServicio.servicioDuracionDias + ' Dias';
	}

	if (ProyectoServicio.territorios) {
		fillTerritoriesTable(ProyectoServicio.territorios, true);
	}

	if (ProyectoServicio.productos) {
		fillProductsTable(ProyectoServicio.productos, true);
	}

	if (ProyectoServicio.serviciosTDR) {
		fillTDRTable(ProyectoServicio.serviciosTDR, true);
	}

	if (ProyectoServicio.servicioClaves) {
		fillBudgetTable(ProyectoServicio.servicioClaves, true);
	}

	if (ProyectoServicio.actividades) {
		fillActivitiesTable(ProyectoServicio.actividades, true);
	}

	if (ProyectoServicio.pagos) {
		fillPaymentTable(ProyectoServicio.pagos, true);
	}
	$.unblockUI();
}

const fillTerritoriesTable = function(territories, avoidButtons) {
	$("#territoryTable > tbody").html("");

	for (let i = 0; i < territories.length; i++) {
		addTerritoryToTable(territories[i], avoidButtons);
	}
};

const addTerritoryToTable = function(data, avoidButtons) {
	const tableBody = document.getElementById("territoryTableBody");
	const row = tableBody.insertRow();

	const stateCell = row.insertCell();
	stateCell.textContent = data.entidadFederativa.nombre;

	const municipalityCell = row.insertCell();
	municipalityCell.textContent = data.municipio.municipioNombre;

	const deleteCell = row.insertCell();
	if (!avoidButtons)
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


const fillProductsTable = function(products, avoidButtons) {
	$("#productTable > tbody").html("");

	for (let i = 0; i < products.length; i++) {
		addProductToTable(products[i], avoidButtons);
	}
};

const addProductToTable = function(data, avoidButtons) {
	const tableBody = document.getElementById("productTableBody");
	const row = tableBody.insertRow();

	const classCell = row.insertCell();
	classCell.textContent = data.productoClase.claseNombre;

	const productoNombreCell = row.insertCell();
	productoNombreCell.textContent = data.productoNombre;

	const productoDescripcionCell = row.insertCell();
	productoDescripcionCell.textContent = data.productoDescripcion;

	const deleteCell = row.insertCell();
	if (!avoidButtons)
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

const fillTDRTable = function(data, avoidButtons) {
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

		if (!avoidButtons)
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

const fillBudgetTable = function(data, avoidButtons) {
	$("#budgetTable tbody").empty();

	if (data.length == 0) {
		let row = $("<tr></tr>");
		row.append($("<td colspan='6' class='text-center'>No se ha registrado clave presupuestal</td>"));

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

		if (!avoidButtons)
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

const fillActivitiesTable = function(data, avoidButtons) {
	$("#activitiesTable tbody").empty();

	if (data.length == 0) {
		let row = $("<tr></tr>");
		row.append($("<td colspan='4' class='text-center'>No se ha registrado actividades</td>"));

		$("#activitiesTable tbody").append(row);
		return;
	}

	data.forEach((element) => {
		let row = $("<tr></tr>");
		row.append($("<td>" + element.servicioactividadAnio + "</td>"));
		row.append($("<td>" + element.sisecopMes.mesNombre + "</td>"));
		row.append($("<td>" + element.servicioactividadDescripcion + "</td>"));

		let btns = $("<td></td>");

		if (!avoidButtons)
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

const fillPaymentTable = function(data, avoidButtons) {
	$("#paymentsTable tbody").empty();

	if (data.length == 0) {
		let row = $("<tr></tr>");
		row.append($("<td colspan='4' class='text-center'>No se ha registrado pagos</td>"));

		$("#paymentsTable tbody").append(row);
		return;
	}

	data.forEach((element) => {
		let row = $("<tr></tr>");
		row.append($("<td>" + element.servicioPagoAnio + "</td>"));
		row.append($("<td>" + element.mesPago.mesNombre + "</td>"));
		row.append($("<td>" + element.servicioPagoCantidad + "</td>"));

		let btns = $("<td></td>");

		if (!avoidButtons) {
			btns.append(
				$("<button></button>")
					.addClass("btn btn-danger btn-sm ml-1")
					.attr("type", "button")
					.attr("onclick", "deletePayment(" + element.servicioPagoId + ")")
					.append($("<i></i>").addClass("fas fa-trash"))
			);
		}
		row.append(btns);

		$("#paymentsTable tbody").append(row);
	});
};



const authProjectAction = function() {

	if (ProyectoServicio.servicioId == null) {
		Swal.fire({
			title: "Error",
			text: "No se ha seleccionado un proyecto",
			icon: "error",
			confirmButtonText: "Aceptar",
		});
		return;
	}

	ProyectoServicio.estatus.estatusId = 10;

	Swal.fire({
		title: "¿Está seguro de aceptar el proyecto?",
		text: "Esto indica que no se encontraron proyectos duplicados.",
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
						text: "Se ha finalizado el tramite exitosamente.",
						icon: "success",
						confirmButtonText: "Aceptar",
					}).then(() => {
						$.blockUI({ message: "Cargando..." });
						parent.window.frames['content-iframe'].location.href = 'AutProject.jsp';
					});
				},
				(error) => {

					$.unblockUI();
					enableActions();
					ProyectoServicio.estatus.estatusId = 2;

					Swal.fire({
						title: "Error",
						text: "Ocurrió un error al solicitar la revisión del proyecto: " + error,
						icon: "error",
						confirmButtonText: "Aceptar",
					});

				}
			);
		}
	});

}

const requestRevision = function(onSuccess, onError) {
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
}

const sendReturnRequest = function() {
	let observations = document.getElementById("observations").value;

	if (observations == null || observations.trim() == "") {
		Swal.fire({
			title: "Error",
			text: "Debe ingresar una observación",
			icon: "error",
			confirmButtonText: "Aceptar",
		});
		return;
	}

	ProyectoServicio.estatus.estatusId = 8;
	ProyectoServicio.observaciones = observations;
	$('#observationsModal').modal('hide');
	$.blockUI({ message: "Enviando a corrección el proyecto ..." });

	requestRevision(
		(data) => {
			$.unblockUI();
			Swal.fire({
				title: "Éxito",
				text: "Se ha enviado a corrección exitosamente.",
				icon: "success",
				confirmButtonText: "Aceptar",
			}).then(() => {
				$.blockUI({ message: "Cargando..." });
				parent.window.frames['content-iframe'].location.href = 'AutProject.jsp';
			});
		},
		(error) => {
			$.unblockUI();

			ProyectoServicio.estatus.estatusId = 2;

			Swal.fire({
				title: "Error",
				text: "Ocurrió un error al solicitar la corrección del proyecto: " + error,
				icon: "error",
				confirmButtonText: "Aceptar",
			});
		}
	);
}


const buscar = function() {
 
	
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



const mostrarResultados = function(data, titulo, objetivos) {
	const resultsTable = document.getElementById("resultsTable");

	resultsTable.innerHTML = "";

	if (data.hits.hits.length === 0) {
		resultsTable.innerHTML = '<tr><td colspan="3">Sin Resultados</td></tr>';
	} else {
		data.hits.hits.forEach((hit, index) => {
			const row = resultsTable.insertRow(index);

			const cellId = row.insertCell(0);
			const cellFolio = row.insertCell(1);
			const cellTitulo = row.insertCell(2);
			const cellObjetivos = row.insertCell(3);

			cellId.textContent = hit._source.servicioId;
			cellFolio.textContent = hit._source.servicioFolio;
			cellTitulo.textContent = hit._source.servicioTitulo;
			cellObjetivos.textContent = hit._source.servicioObjetivos;

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

const printBtnAction = function () {
  const url = `anexo2/download?id=${ProyectoServicio.servicioId}`;
  
  // Abrir la URL en una nueva ventana o pestaña
  window.open(url, '_blank');
};