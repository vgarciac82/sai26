<%@page import="com.google.gson.Gson"%>
<%@page import="com.syc.gestion.core.UnidadEjecutora"%>
<%@page import="java.util.List"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="com.syc.gestion.core.UsuarioVistaBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="org.apache.log4j.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%!Logger log = Logger.getLogger("inboxAutorizaFIEL.jsp");%>
<%
Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

if (usuario == null) {
	response.sendRedirect("../index.jsp");
	return;
}

boolean usrROL = (usuario.getPropiedad("ROL_NOMINA") != null
		&& "SI".equalsIgnoreCase(usuario.getPropiedad("ROL_NOMINA").getValor()));
%>
<!DOCTYPE html>
<html lang="es">

<head>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Inbox Pendientes FIEL</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
	crossorigin="anonymous">
<link rel="stylesheet"
	href="https://cdn.datatables.net/2.0.6/css/dataTables.bootstrap5.min.css" />
<link href="../SICOVE/fontawesome/css/fontawesome.css" rel="stylesheet">
<link href="../SICOVE/fontawesome/css/brands.css" rel="stylesheet">
<link href="../SICOVE/fontawesome/css/solid.css" rel="stylesheet">
<link rel="stylesheet" href="../SISECOP/css/style.css">

<style type="text/css">
h1 {
	font: normal 0.8rem "Lucida Grande", Verdana, Arial, Helvetica,
		sans-serif;
	line-height: 1.45;
}

#divTable {
	overflow-x: auto;
	white-space: nowrap;
}
</style>

<script src="https://code.jquery.com/jquery-3.7.1.js"
	integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4="
	crossorigin="anonymous"></script>
<scipt src="../SICOVE/fontawesome/all.js"></scipt>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="../Generador/js/jquery.blockUI-2.70.0.js"></script>
<script type="text/javascript">
    	const user = "<%=usuario.getLogin()%>";
    	const usrROL = <%=usrROL%>;
    	let oTableTramites;
    	
    	const es_mx = {
    			sProcessing : "Procesando...",
    			sLengthMenu : "Mostrar _MENU_ registros",
    			sZeroRecords : "No hay registros a mostrar",
    			sEmptyTable : "No hay datos en la tabla",
    			sLoadingRecords : "Cargando...",
    			sInfo : "Registros _START_ al _END_ de _TOTAL_",
    			sInfoEmpty : "Registro 0 al 0 de 0",
    			sInfoFiltered : "(filtered from _MAX_ total entries)",
    			sInfoPostFix : "",
    			sInfoThousands : ",",
    			sSearch : "Filtro:",
    			oPaginate : {
    				sFirst : "Primero",
    				sPrevious : "Ant.",
    				sNext : "Sigte.",
    				sLast : "&Uacute;ltimo"
    			}
    		};
    	
    	$(document).ready(function() {
    		creaDTTramites();
    		$('#select-all').on('change', function () {
    	        let isChecked = $(this).prop('checked');
    	        $('.row-checkbox').prop('checked', isChecked);
    	    });
    	});
    	
    	const btnEnviarAction = function (){

    		let selectedIds = [];
	        $('.row-checkbox:checked').each(function () {
	            selectedIds.push($(this).val());
	        });

	        if (selectedIds.length === 0) {
	            alert("No se han seleccionado registros.");
	            return;
	        }
	        
	        Swal.fire({
	        	  title: "Esta seguro de autorizar el envio a SICOP?",
	        	  showCancelButton: true,
	        	  confirmButtonText: "Enviar",
	        	  denyButtonText: `Cancelar`
	        	}).then((result) => {
	        	  if (result.isConfirmed) {
	        		  sendRequest(selectedIds);	        	    
	        	  }
	        	});
	        
	        

	        
    	}
    	
    	const sendRequest = function(selectedIds){
    		
    		$.ajax({
    	        url: '../FIEL/autorizaEnvioSICOPMasivo',  
    	        type: 'POST',
    	        traditional: true,  
    	        data: { idCaso: selectedIds },
    	        beforeSend: function () {$.blockUI({ message: '<h1><img src="../Generador/imagenes/wait24trans.gif" /> procesando ...</h1>' });},
    	        complete:function(){ $.unblockUI() },
    	        success: function(response) {
					let icon = (response.success === response.total? "success":"warning");
					oTableTramites.ajax.reload(null, false);  
					$('#select-all').prop('checked', false);

    	        	Swal.fire({
    	        		  title: "Proceso Terminado.",
    	        		  text: "Se logro avanzar " + response.success + " de " + response.total + " solicitudes.",
    	        		  icon: icon
    	        		});
    	        },
    	        error: function(xhr, status, error) {
    	            console.error("Error en la petición:", error);
    	            Swal.fire({
    	            	  icon: "error",
    	            	  title: "Probelmas...",
    	            	  text: "No se logro avanzar la(s) solicitud(es) seleccionada(s). Intente nuevamente o reporte al administrador.",
    	            	});
    	        }
    	    });
    		
    	}
    	
    	 const fmtMoneyNoSymbol = function (v) {
    		    if (v == null || v === '') return '';
    		    const n = Number(v);
    		    if (isNaN(n)) return v;
    		    return n.toLocaleString('es-MX', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    		  };

   		  const fmtDateDMY = function (v) {
   		    if (!v) return '';
   		    const s = String(v);

   		    // ISO o "yyyy-MM-dd..." -> dd/MM/aaaa
   		    let m = s.match(/^(\d{4})-(\d{2})-(\d{2})/);
   		    if (m) return `${m[3]}/${m[2]}/${m[1]}`;

   		    // Ya viene como dd/MM/aaaa o dd-MM-aaaa -> normaliza a dd/MM/aaaa
   		    m = s.match(/^(\d{2})[\/-](\d{2})[\/-](\d{4})$/);
   		    if (m) return `${m[1]}/${m[2]}/${m[3]}`;

   		    // Fallback con Date (por si llega otro formato)
   		    const d = new Date(s);
   		    if (isNaN(d)) return s;
   		    const dd = String(d.getDate()).padStart(2, '0');
   		    const MM = String(d.getMonth() + 1).padStart(2, '0');
   		    const yyyy = String(d.getFullYear());
   		    return `${dd}/${MM}/${yyyy}`;
   		  };
   		  
    	const creaDTTramites = function() {
    		var myWhere = "";		
    		
    		if (usrROL)
    			myWhere = "  tc_descripcion IN ('Pago Directo Nomina','Solicitud NO Presupuestal') AND SUBSTRING(folio,6,3) = 'A03'";
    		else
    			myWhere = "  tc_descripcion NOT IN ('Pago Directo Nomina') OR SUBSTRING(folio,6,3) != 'A03'";
    		
    		oTableTramites = new DataTable('#dt_inbox', {
    		    ajax: {
    		        url: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=nt&ql=vTramitesAutorizaFIEL&qw=" + myWhere,
    		        type: 'POST'
    		    },
    		    columns: [
    		    	  { 
    		    	    data: null, 
    		    	    orderable: false, 
    		    	    searchable: false, 
    		    	    className: 'text-center',
    		    	    render: function (data, type, row) {
    		    	      return `<input type="checkbox" class="row-checkbox" value="${row.id_caso}">`;
    		    	    }
    		    	  },
    		    	  { data: 'folio' },
    		    	  { data: 'tc_descripcion' },
    		    	  { data: 'nfolio' },
    		    	  { data: 'canocontrarrecibo' },
    		    	  { data: 'documento' },
    		    	  { 
    		    	    data: 'mmontosolicitud', 
    		    	    className: 'text-end',
    		    	    render: function (data, type, row) {
    		    	      // Para ordenar/buscar correctamente en DataTables
    		    	      if (type === 'sort' || type === 'type') return Number(data || 0);
    		    	      return fmtMoneyNoSymbol(data);
    		    	    }
    		    	  },
    		    	  { 
    		    	    data: 'c_fecha_ini',
    		    	    render: function (data, type, row) {
    		    	      // Para ordenar por fecha cruda si DataTables lo requiere
    		    	      if (type === 'sort' || type === 'type') return data || '';
    		    	      return fmtDateDMY(data);
    		    	    }
    		    	  },
    		    	  { data: 'tipoCarga' },
    		    	  { 
    		    	    data: 'dfechaoperacion',
    		    	    render: function (data, type, row) {
    		    	      if (type === 'sort' || type === 'type') return data || '';
    		    	      return fmtDateDMY(data);
    		    	    }
    		    	  },
    		    	  { data: 'id_caso', visible: false, searchable: false },
    		    	  { data: 'id_tc', visible: false, searchable: false },
    		    	  { data: 'id_caso_oper', visible: false, searchable: false },
    		    	  { data: 'co_responsable', visible: false, searchable: false },
    		    	  { data: 'ctipodocumento', visible: false, searchable: false }
    		    	],
    		    order: [[12, 'desc'],[2, 'desc']], // Orden por dfechaoperacion
    		    rowId: 'folio',
    		    processing: true,
    		    serverSide: true,
    		    paging: false, // Se desactiva la paginación
    		    lengthChange: false, // Se desactiva la opción de cambiar el número de registros mostrados
    		    searching: true, 
    		    ordering: true, 
    		    info: true
    		    , // Oculta la información de "Mostrando X de Y registros"
    		    autoWidth: true,
    		    scrollCollapse: true,
    		    scrollY: '400px', // Se ajusta la altura para mostrar más filas
    		    language: es_mx,
    		    retrieve: true,
    		    destroy: true
    		});

    		// Asegura que la tabla se redibuje al cambiar el tamaño de la ventana
    		$(window).on('resize', function() {
    		    oTableTramites.columns.adjust().draw();
    		});

    		$('#dt_inbox tbody').on('click', 'tr', function () {
    		    $('#dt_inbox tbody tr').removeClass('row_selected');
    		    $(this).addClass('row_selected');
    		});

    		$('#dt_inbox tbody').on('dblclick', 'tr', function () {
    		    let rowData = oTableTramites.row(this).data();

    		    if (!rowData) return;

    		    let idCaso = rowData.id_caso;
    		    let idTC = rowData.id_tc;
    		    let idOper = rowData.id_caso_oper;
    		    let coResponsable = rowData.co_responsable;

    		    let urlCaso = (coResponsable.toLowerCase().indexOf("consulta") >= 0)
    		        ? "show-caso?"
    		        : "../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_EXEC_CASE%>&";

    		    urlCaso += `<%=GestionInterface.PRM_CASE%>=${idCaso}&<%=GestionInterface.PRM_CASE_OPER%>
	=${idOper}`;

							document.frmabrecaso.action = urlCaso;
							document.frmabrecaso.submit();
						});

	}
</script>
</head>

<body>
	<form name="frmabrecaso" method="post">
		<div class="container">
			<h1 class="mt-3 mb-3 h1">Solicitudes autorizadas pendientes de
				procesar</h1>



			<div class="form-check">
				<input class="form-check-input" type="checkbox" value=""
					id="select-all"> <label class="form-check-label"
					for="select-all"> Seleccionar Todos </label>
			</div>

			<div id="divTable" class="mt-4">
				<table id="dt_inbox" class="table table-striped table-bordered"
					style="width: 100%">
					<thead>
						<tr>
							<th></th>
							<th>SAI</th>
							<th>Tramite</th>
							<th>Folio</th>
							<th>Contrarecibo</th>
							<th>Contrato</th>
							<th>Monto</th>
							<th>Inicio del <br>Tramite
							</th>
							<th>Tipo de Carga</th>
							<th>Fecha <br>de Autorizacion</th>
							<th style="display: none">ID Caso</th>
							<th style="display: none">ID TC</th>
							<th style="display: none">ID Oper</th>
							<th style="display: none">CO Responsable</th>
							<th style="display: none">Tipo Documento</th>
							
						</tr>

					</thead>
				</table>
			</div>

			<div class="mt-4">
				<button class="btn btn-primary" id="btnUpdate"
					onClick="filterTable()">Actualizar</button>
				<button class="btn btn-success" id="btnEnviar" role="button"
					onclick="btnEnviarAction(); return false">Enviar
					Seleccionados</button>
			</div>

		</div>


		<script
			src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
			integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
			crossorigin="anonymous"></script>
		<script src="https://cdn.datatables.net/2.0.6/js/dataTables.min.js"></script>
		<script
			src="https://cdn.datatables.net/2.0.6/js/dataTables.bootstrap5.min.js"></script>

	</form>
</body>

</html>