$(document).ready(function() {
	$("#searchBtn").click(buscar);
});

let results = [];

const buscar = function() {
	const folio = document.getElementById("folio").value;
	const titulo = document.getElementById("titulo").value;
	const objetivos = document.getElementById("objetivos").value;

	let bodyQuery = {
		query: {
			bool: {
				must: [],
			},
		},
		sort: [{ _score: { order: "desc" } }],
	};

	if (titulo) {
		bodyQuery.query.bool.must.push({ match: { servicioTitulo: titulo } });
	}

	if (objetivos) {
		bodyQuery.query.bool.must.push({ match: { servicioObjetivos: objetivos } });
	}

	if (folio) {
		bodyQuery.query.bool.must.push({ match_phrase: { servicioFolio: folio } });
	}

	if (bodyQuery.query.bool.must.length > 0) {
		fetch(URL_ELASTIC_SEARCH + "/proyectos/_search", {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify(bodyQuery),
		})
			.then((response) => response.json())
			.then((data) => {
				mostrarResultados(data, titulo, objetivos);
			})
			.catch((error) => console.error("Error:", error));
	} else {
		console.error("No se especificaron filtros para la búsqueda.");
	}
};

const mostrarResultados = function(data, folio, titulo, objetivos) {
	const resultsTable = document.getElementById("resultsTable");

	resultsTable.innerHTML = "";

	if (data.hits.hits.length === 0) {
		resultsTable.innerHTML = '<tr><td colspan="4">Sin Resultados</td></tr>';
		results = [];
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
			cellObjetivos.innerHTML = (
				hit._source.servicioObjetivos ? (
					hit._source.servicioObjetivos.substring(0, 85)
					+ '<a href="#" onclick="showInfo(' + hit._source.servicioId + ')">... Ver más</a>'
				)
					: "");
			results[hit._source.servicioId] = hit._source.servicioObjetivos

			//			if (titulo && $.trim(titulo) === $.trim(hit._source.servicioTitulo)) {
			//				existsProjectTitle = true;
			//				cellTitulo.style.backgroundColor = "#add8e6";
			//			}
		});
	}
};


const showInfo = function(idElement) {
    Swal.fire({
        html: `
            <textarea class="form-control w-100" rows="10" readonly>${results[idElement]}</textarea>
        `,
        width: '50rem' // Ajusta el ancho del SweetAlert
    });
}
