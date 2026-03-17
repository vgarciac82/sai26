<%@ page import="java.util.*,java.sql.*" %>
<%@ page import="com.syc.gestion.core.Usuario" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect("../index.jsp");
        return;
    }
    
    String cUR = usuario.getU_UR();
%>
<!DOCTYPE html>
<html lang="es">
		<head>
		    <meta charset="UTF-8">
		    <title>Consulta Contratos</title>
		    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
		    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.6/css/jquery.dataTables.min.css">
		    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.6/css/dataTables.bootstrap5.min.css">
		    <link rel="stylesheet" href="../SISECOP/css/style.css">
		    
		     <style>
    /* Scroll vertical para la tabla */
    .table-responsive {
      max-height: 200px; /* Ajusta la altura según necesites */
      overflow-y: auto;
    }

    /* Encabezado con fondo azul personalizado */
    thead th {
      background-color: #3571B6;
      color: white;
      font-weight: 600;
    }

    /* Filas con alternancia suave */
    tbody tr:nth-child(odd) {
      background-color: #f8f9ff;
    }

    tbody tr:nth-child(even) {
      background-color: #f0f2ff;
    }
  </style>
		</head>
		<body>
		<div class="container">
		    <h1 class="mb-3">Búsqueda de Contratos</h1>
		    <form id="formConsulta">
		    	<input type="hidden" name="rfcContrato" id="rfcContrato" value=""/>
				<input type="hidden" name="nombreContrato" id="nombreContrato" value=""/>
				<input type="hidden" name="importeContrato" id="importeContrato" value=""/>
		        <div class="row mb-3">
		            <div class="col-md-4">
		                <label for="cIdContratoCompromiso" class="form-label">Contrato</label>
		                <input type="text" class="form-control" id="cIdContratoCompromiso" name="cIdContratoCompromiso" onclick="selecciona()"/>
		            </div>
		            <div class="col-md-2 d-flex align-items-end gap-2">
		            	<button type="button" class="btn btn-primary" onclick="cargarGrid()">Buscar</button>
		            </div>
		            <div class="col-md-6 d-flex align-items-end gap-2">
		                <button type="button" class="btn btn-secondary" onclick="enviar()">Aceptar</button>
		            </div>
		        </div>
		    </form>
		
		    <table id="dt_Contrato" class="table-responsive table-hover " style="width:100%">
		        <thead>
		        <tr>
		            <th>Contrato</th>
		            <th>RFC</th>
		            <th>Nombre</th>
		            <th>Importe</th>
		            <th>UR</th>
		        </tr>
		        </thead>
		    </table>
		    		
		</div>

  <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  <script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
  <script src="https://cdn.datatables.net/1.13.6/js/dataTables.bootstrap5.min.js"></script>
  <script type="text/javascript" src="../Generador/js/crud.js"></script>

<script>
    let oTable;
    const formName = "formCompromiso";
    const inputTarget = "cIDContrato";
    var cUR = "<%=cUR%>";

    $(document).ready(function () {
        
    	cargarGrid();
    	
       
    });
    
    var es_mx = {
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
    	
	const SYSTEM_URL =  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1]; 
    const cargarGrid = function() {

    	let condition = generaCondicion();
    	
    	
    	oTable = new DataTable('#dt_Contrato', {

    		ajax: {
    			url: SYSTEM_URL + "/crud?rt=nt&ql=vContratosConCompromiso&qw= " + encodeURI(condition),
    			type: 'POST'
    		},
    		columns: [
    			 {data: 'cIdContrato'},
                 {data: 'cIDRFC'},
                 {data: 'cNombre'},
                 {data: 'mImporteTotal'},
                 {data: 'cUnidadResponsable'}
    		],
    		order: [[1, 'asc']],
    		rowId: 'cIdContrato',
    		processing: true,
    		serverSide: true,
    		scrollCollapse: true,
    		scrollY: '400px',
    		language: es_mx,
    		paging: false,
    		searching: false,
    		bDestroy: true,
    	});
    	
    	$('#dt_Contrato tbody').on('click', 'tr', function () {
    	    var data = oTable.row(this).data();
    	    if (data) {
    	        $('#cIdContratoCompromiso').val(data.cIdContrato);
    	        $('#rfcContrato').val(data.cIDRFC);
    	        $('#importeContrato').val(data.mImporteTotal);
    	        $('#nombreContrato').val(data.cNombre);
    	    }
    	});


    }
   
    function generaCondicion() {
        const val = $('#cIdContratoCompromiso').val().trim();
        
        var filtro = "cUnidadResponsable = '<%=cUR.replace("\"", "")%>'";
       	if (val != "") {
       		filtro += " AND cIdContrato LIKE '%" + val + "%'";
       		
       	} 
        return  filtro;
    }



    function selecciona() {
        $(oTable.rows().nodes()).removeClass('table-primary');
    }

    function enviar() {
        if (!$('#cIdContratoCompromiso').val()) {
            alert('Por favor selecciona una fila.');
            return;
        }
       
        if (window.opener && window.opener.document) {
            let input = window.opener.document.getElementById('cIdContrato');

            if (input) {
                input.value =$('#cIdContratoCompromiso').val();
                window.opener.document.getElementById('cIDRFC').value = $('#rfcContrato').val();
                window.opener.document.getElementById('cnombre').value = $('#nombreContrato').val();
                window.opener.document.getElementById('importeTotal').value = $('#importeContrato').val();
                window.opener.guardarEncabezado();
                window.opener.cargarMovimientos();
                
                if (typeof window.opener.cargarEpReduccion === 'function') {
                    window.opener.cargarEpReduccion();
                }

                if (typeof window.opener.cargarEpAmpliacion === 'function') {
                    window.opener.cargarEpAmpliacion();
                }
                
                window.close(); 
            } else {
                alert("No se encontró el input en la ventana principal.");
            }
        } else {
        	window.close(); 
        }
        
    }
</script>
</body>
</html>
