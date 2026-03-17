<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String cCentroContable = "";
Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);

if (usuario == null || c == null) {
	response.sendRedirect("../index.jsp");
	return;
}

if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
	cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
}

String ur = "";
ur = usuario.getU_UR();

int nFolio = Util.folio(c);
String cFolio = c.getFolio();
String login = usuario.getLogin();
String ejercicioFiscal = efbl.getEjercicioFiscalActivo().getaEjercicioFiscal();
%>

<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8" />
<title>Consulta Suficiencia para Pago Directo</title>
<meta name="viewport" content="width=device-width, initial-scale=1" />

<!-- Bootstrap 5 -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<!-- DataTables -->
<link
	href="https://cdn.datatables.net/1.13.8/css/dataTables.bootstrap5.min.css"
	rel="stylesheet" />
<!-- Font Awesome -->
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css"
	rel="stylesheet" />
<!-- SweetAlert2 (opcional para avisos) -->
<link
	href="https://cdn.jsdelivr.net/npm/sweetalert2@11.12.4/dist/sweetalert2.min.css"
	rel="stylesheet" />

<style>
body {
	background: #f8f9fc;
}

.card {
	border: 0;
	border-radius: 1rem;
	box-shadow: 0 8px 24px rgba(0, 0, 0, .06);
}

.card-header {
	background: #fff;
	border-bottom: 0;
	font-weight: 600;
}
/* Labels más notorios */
.form-label { 
  font-weight: 700;     /* antes 600 */
  color: #333;
}

</style>


<script>
	
	const folioSAI = "<%=cFolio%>";
	const folio =<%=nFolio%>;
	const cCentroContable = "<%=cCentroContable%>";
	const unidadResponsable = "<%=ur%>";
	const ejercicioFiscal = "<%=ejercicioFiscal%>";
	const login = "<%=login%>";

	function onLoadPlantilla() {
		console.log("onLoadPlantilla called");
	}
</script>

</head>
<body>
	<div class="container py-4">
		<h1 class="h4 mb-4">Consulta Suficiencia para Pago Directo</h1>

		<!-- Barra superior: Folio / Referencia / Fecha -->
		<div class="card mb-4">
			<div class="card-body">
				<div class="row g-3">
					<div class="col-12 col-md-4">
						<label class="form-label">Folio</label> <input
							class="form-control-plaintext" id="folio" type="text" readonly>
					</div>
					<div class="col-12 col-md-4">
						<label class="form-label">Referencia</label> <input
							class="form-control-plaintext" id="idContrato" type="text"
							readonly>
					</div>
					<div class="col-12 col-md-4">
						<label class="form-label">Fecha</label> <input
							class="form-control-plaintext" id="fechaHoy" type="text" readonly>
					</div>
				</div>
			</div>
		</div>

		<!-- Importes -->
		<div class="card mb-4">
			<div class="card-header">Importes</div>
			<div class="card-body">
				<div class="row g-3 align-items-end">

				<div class="col-12 col-lg-2">
					<label class="form-label">Importe</label>
					<input class="form-control-plaintext" id="importe" readonly>
				</div>

				<div class="col-12 col-lg-2">
					<label class="form-label">% IVA</label>
					<input class="form-control-plaintext" id="porcentajeIVAText" readonly>
				</div>

				<div class="col-12 col-lg-2">
					<label class="form-label">Importe IVA</label>
					<input class="form-control-plaintext" id="importeIVA" readonly>
				</div>

				<div class="col-12 col-lg-3">
					<label class="form-label">Importe Retenciones</label>
					<input class="form-control-plaintext" id="importeRetenciones" readonly>
				</div>

				<div class="col-12 col-lg-3">
					<label class="form-label">Total</label>
					<input class="form-control-plaintext" id="total" readonly>
				</div>

				</div>
			</div>
			</div>


		<!-- General -->
		<div class="card mb-4">
			<div class="card-header">General</div>
			<div class="card-body">
				<div class="row g-3">
					<div class="col-12 col-md-4">
						<label class="form-label">RFC</label> <input
							class="form-control-plaintext" id="rfc" readonly>
					</div>
					<div class="col-12 col-md-8">
						<label class="form-label">Nombre</label> <input
							class="form-control-plaintext" id="nombre" readonly>
					</div>
					<div class="col-12">
						<label class="form-label">Justificación</label>
						<textarea class="form-control-plaintext" id="justificacion"
							rows="3" readonly></textarea>
					</div>
				</div>
			</div>
		</div>

		<!-- Retenciones -->
		<div class="card mb-4" id="cardRetenciones">
			<div class="card-header">Retenciones</div>
			<div class="card-body">
				<table id="tblRetenciones"
					class="table table-striped table-hover w-100">
					<thead>
						<tr>
							<th>ID</th>
							<th>Retención</th>
							<th>Porcentaje</th>
							<th>Importe</th>
						</tr>
					</thead>
					<tbody></tbody>
				</table>
			</div>
		</div>

		<!-- EPs Seleccionadas -->
		<div class="card mb-4" id="cardCalendario">
			<div class="card-header">EPs Seleccionadas</div>
			<div class="card-body">
				<table id="tblCalendario"
					class="table table-striped table-hover w-100">
					<thead>
						<tr>
							<th>EP</th>
						</tr>
					</thead>
					<tbody></tbody>
				</table>
			</div>
		</div>

	</div>

	<!-- jQuery, Bootstrap, DataTables, SweetAlert2 -->
	<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<script
		src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
	<script
		src="https://cdn.datatables.net/1.13.8/js/dataTables.bootstrap5.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/sweetalert2@11.12.4/dist/sweetalert2.all.min.js"></script>

	<script>
	const SYSTEM_URL = window.location.protocol +
    "//" +
    window.location.host +
    "/" +
    window.location.pathname
        .split("/")[1];

    // ===== CONFIG / HELPERS =====
    const API = {
      ENC: '../../api/suficiencia',
      EP:  '../../api/suficiencia/ep',
      RET: '../../api/suficiencia/retencion',
	  DT_RET: SYSTEM_URL + '/crud?rt=nt&ql=v_suficiencia_contrato_directo_retencion',
      DT_EP: SYSTEM_URL + '/crud?rt=nt&ql=tsuficienciapagodirectoep'
    };

    const es_mx = {
      sProcessing: "Procesando...",
      sLengthMenu: "Mostrar _MENU_ registros",
      sZeroRecords: "No hay registros a mostrar",
      sEmptyTable: "No hay datos en la tabla",
      sLoadingRecords: "Cargando...",
      sInfo: "Registros _START_ al _END_ de _TOTAL_",
      sInfoEmpty: "Registro 0 al 0 de 0",
      sInfoFiltered: "(filtrado de _MAX_ registros)",
      sSearch: "Filtro:",
      oPaginate: { sFirst:"Primero", sPrevious:"Ant.", sNext:"Sigte.", sLast:"Último" },
    };

    const toMoney = function(v){
      const n = Number(v||0);
      return n.toLocaleString('es-MX', { style:'currency', currency:'MXN' });
    };

    const toMXDate = function (iso) {
      if (!iso) return '';
      const s = String(iso).substring(0, 10); // yyyy-MM-dd
      const m = /^(\d{4})-(\d{2})-(\d{2})$/.exec(s);
      return m ? `${m[3]}/${m[2]}/${m[1]}` : iso;
    };

    const getFolioFromQS = function(){
      return new URLSearchParams(location.search).get('folio');
    };

    // ===== CARGA DE ENCABEZADO =====
    const cargarEncabezado = async function(folio){
      try{
        const enc = await $.ajax({ url: API.ENC + '?folio=' + encodeURIComponent(folio), method:'GET' });
        if (!enc) return;

        $('#folio').val(enc.folioSuficienciaPagoDirecto || '');
        $('#idContrato').val(enc.idContrato || enc.cIdContrato || '');
        $('#fechaHoy').val(toMXDate(enc.fechaAplicacion || enc.fAplicacion || enc.fechaCarga));
        $('#importe').val((enc.importe ?? 0).toFixed(2));
        $('#importeIVA').val((enc.importeIVA ?? 0).toFixed(2));
        $('#total').val((enc.total ?? 0).toFixed(2));
        $('#rfc').val(enc.rfc || enc.RFC || '');
        $('#nombre').val(enc.nombre || enc.nombreProveedor || enc.proveedorNombre || '');
        $('#justificacion').val(enc.justificacion || '');
        const pct = Number(enc.porcentajeIVA ?? enc.nPorcentajeIVA ?? 0);
        $('#porcentajeIVAText').val((isNaN(pct)?0:pct) + '%');

      } catch (xhr){
        if (xhr && xhr.status === 404) {
          Swal.fire({ icon:'info', title:'No encontrado', text:'No existe información para el folio '+folio });
          return;
        }
        let msg = 'No fue posible cargar la suficiencia.';
        try { const r = JSON.parse(xhr.responseText||'{}'); if (r.error) msg = r.error; } catch(e){}
        Swal.fire({ icon:'error', title:'Error', text: msg });
      }
    };

    // ===== DATA TABLES (CONSULTA) =====
    let dtRet, dtEP;

    const initDTs = function(folio){
       initRetencionesDT();
	   initEP_DT();
    };

	
// Datatable para retenciones
const initRetencionesDT = function () {

    if ($.fn.DataTable.isDataTable('#tblRetenciones')) {
        dtRet = $('#tblRetenciones').DataTable();
        return;
    }

    dtRet = $('#tblRetenciones').DataTable({
        ajax: {
            url: API.DT_RET + '&qw=nFolioSuficienciaPagoDirecto=' + encodeURIComponent(folio),
            type: 'POST'
        },
        columns: [
            { data: 'cIdTipoRetencion' },
            { data: 'nombre_retencion' },
            { data: 'porcentajeRetencion' },
            { data: 'mImporteRetencion' }
        ],
        columnDefs: [
            { targets: 0, visible: false, searchable: false }
        ],
        order: [[0, 'asc']],
        rowId: 'cIdTipoRetencion',
        paging: true,
        processing: true,
        serverSide: true,
        scrollCollapse: true,
        scrollY: '300px',
        language: es_mx,
        pageLength: 10,
        searching: false,
        info: false,
        lengthChange: false
    });

    // Selección simple
    $('#tblRetenciones tbody').off('click').on('click', 'tr', function () {
        dtRet.$('tr.selected').removeClass('selected');
        $(this).addClass('selected');
    });

    // Recalcula total de retenciones cuando llega la data
    dtRet.off('xhr').on('xhr', function () {
        const json = dtRet.ajax.json();
        let rows = [];
        if (Array.isArray(json)) rows = json;
        else if (json && Array.isArray(json.data)) rows = json.data;

        const totalRet = rows.reduce((acc, r) => acc + Number(r.mImporteRetencion || 0), 0);
        $('#importeRetenciones').val(totalRet.toFixed(2));
    });
};


// Datatable para EP
const initEP_DT = function () {

    if ($.fn.DataTable.isDataTable('#tblCalendario')) {
        dtEP = $('#tblCalendario').DataTable();
        return;
    }



    dtEP = $('#tblCalendario').DataTable({
        ajax: {
            url: API.DT_EP + '&qw=nFolioSuficienciaPagoDirecto=' + encodeURIComponent(folio),
            type: 'POST'
        },
        columns: [
            { data: 'EP' }
        ],
        order: [[0, 'asc']],
        rowId: 'EP',
        paging: true,
        processing: true,
        serverSide: true,
        scrollCollapse: true,
        scrollY: '300px',
        language: es_mx,
        pageLength: 10,
        searching: false,
        info: false,
        lengthChange: false
    });

    // Selección simple
    $('#tblCalendario tbody').off('click').on('click', 'tr', function () {
        dtEP.$('tr.selected').removeClass('selected');
        $(this).addClass('selected');
    });


};
    // ===== On Ready =====
    $(function(){
      
      if (!folio) {
        Swal.fire({ icon:'warning', title:'Folio requerido', text:'Agregue ?folio=#### en la URL para consultar.' });
        return;
      }
      cargarEncabezado(folio);
      initDTs(folio);
    });
  </script>
</body>
</html>
