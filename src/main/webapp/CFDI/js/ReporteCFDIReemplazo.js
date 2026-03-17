const dataTableBaseURL = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1];

let pagosDT;
let dtCfdiOriginal;
let dtCfdiNuevo

const initUI = function() {
	$.fn.dataTable.ext.legacy.ajax = true;
	$("#buscarBtn").on("click", function() {
		console.log("Buscando ... ");
		buscarFacturas();
	});

	pagosDT = $("#tblPagos").DataTable({
		"retrieve": true,
		"destroy": true,
		"serverSide": true,
		"scrollX": true,
		"searching": true,
		"ajax": {
			url: dataTableBaseURL + "/crud?rt=t&ql=v_Tramite_Factura_Reemplazada&qw=1<>1",
			type: 'POST',
		},
		"aoColumns": [
			{ "name": "canocontrarrecibo" },
			{ "name": "ctipopago" },
			{ "name": "nfoliopago" },
			{ "name": "rfc" },
			{ "name": "nombre" },
			{ "name": "cconcepto" },
			{
				"name": "mimporte",
				"render": function(data, type, row) {
					if (type === 'display') {
						return formatCurrency(data);
					}
					return data;
				}
			},
			{ "name": "uuid", "visible": false }
		]
	});



	$('#tblPagos tbody').on('click', 'tr', function() {

		let data = $('#tblPagos').DataTable().row(this).data();
		console.log(data);
		cargaFacturasOriginales(data[1], data[2]);
		cargaFacturasNuevas(data[1], data[2]);

	});

	dtCfdiOriginal = $("#tblCfdiOriginal").DataTable({
		"retrieve": true,
		"destroy": true,
		"serverSide": true,
		"scrollX": true,
		"searching": true,
		"ajax": {
			url: dataTableBaseURL + "/crud?rt=t&ql=v_Lista_Facturas_Anteriores&qw=1<>1",
			type: 'POST',
		},
		"aoColumns": [
			{ "name": "uuid" },
			{
				"name": "mimportebruto",
				"render": function(data, type, row) {
					if (type === 'display') {
						return formatCurrency(data);
					}
					return data;
				}
			},
			{
				"name": "mimporteiva",
				"render": function(data, type, row) {
					if (type === 'display') {
						return formatCurrency(data);
					}
					return data;
				}
			},
			{
				"name": "motrosimpuestos",
				"render": function(data, type, row) {
					if (type === 'display') {
						return formatCurrency(data);
					}
					return data;
				}
			},
			{
				"name": "mimportedescuento",
				"render": function(data, type, row) {
					if (type === 'display') {
						return formatCurrency(data);
					}
					return data;
				}
			},
			{
				"name": "mimporteretencion",
				"render": function(data, type, row) {
					if (type === 'display') {
						return formatCurrency(data);
					}
					return data;
				}
			},
			{
				"name": "importeNeto",
				"render": function(data, type, row) {
					if (type === 'display') {
						return formatCurrency(data);
					}
					return data;
				}
			},
			{ "name": "cmetodopago" }
		]
	});

	dtCfdiNuevo = $("#tblCfdiNuevo").DataTable({
		"retrieve": true,
		"destroy": true,
		"serverSide": true,
		"scrollX": true,
		"searching": true,
		"ajax": {
			url: dataTableBaseURL + "/crud?rt=t&ql=v_Lista_Facturas_Nuevas&qw=1<>1",
			type: 'POST',
		},
		"aoColumns": [
			{ "name": "uuid" },
			{ "name": "mimportebruto" },
			{ "name": "mimporteiva" },
			{ "name": "motrosimpuestos" },
			{ "name": "mimportedescuento" },
			{ "name": "mimporteretencion" },
			{ "name": "importeNeto" },
			{ "name": "cmetodopago" }
		]
	});

}

const formatCurrency = function(value) {
	return new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' }).format(value);
}


const cargaFacturasOriginales = function(tipoPago, folio) {

	let condition = "ctipopago='" + tipoPago + "' AND nfoliopago = " + folio;
	dtCfdiOriginal.clear().draw();
	dtCfdiOriginal.ajax.url(dataTableBaseURL + "/crud?rt=t&ql=v_Lista_Facturas_Anteriores&qw=" + condition).load();

}

const cargaFacturasNuevas = function(tipoPago, folio) {

	let condition = "ctipopago='" + tipoPago + "' AND nfoliopago = " + folio;
	dtCfdiNuevo.clear().draw();
	dtCfdiNuevo.ajax.url(dataTableBaseURL + "/crud?rt=t&ql=v_Lista_Facturas_Nuevas&qw=" + condition).load();

}

const buscarFacturas = function() {


	let uuid = $("#uuidReemplazado").val().trim();
	let rfc = $("#rfcPago").val().trim();

	let condition = "";
	let token = "";

	if (uuid !== "") {
		condition += " uuid = '" + uuid + "' ";
		token = " AND ";
	}
	if (rfc !== "")
		condition += token + " rfc = '" + rfc + "' ";

	pagosDT.clear().draw();

	pagosDT.ajax.url(dataTableBaseURL + "/crud?rt=t&ql=v_Tramite_Factura_Reemplazada&qw=" + condition).load();


}