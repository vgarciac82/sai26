
<script type="text/javascript" charset="utf-8">

var oTableLocal;

	$(document).ready(function () {
		listadoComprobados();
	});
		
	function listadoComprobados(){
			
 		oTableLocal = $('#dt_REP_Comprobados').dataTable({
				"bLengthChange" : true,
				"bFilter" : true,
				"bSort" : true,
				"bInfo" : true,
				"bPaginate" : true,
				"bAutoWidth" : true,
				"bScrollCollapse" : true,
				"sScrollXInner": "150%", 
				"sScrollX": "100%",
				"sPaginationType" : "full_numbers",
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide": true,
				"iDisplayLength": 25,
				"fnInitComplete": function() {    
					oTableLocal.fnAdjustColumnSizing();
			},
				oLanguage: {
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
					sSearch: "Buscar:"
				},
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_tReciboElectronico_PagoFactura&qw=nComprobado = 1",
				bProcessing: true,
				sPaginationType: "full_numbers",				
						
				aoColumns: [
					{ sName: "cTipoPago",			bSearchable: false,	bSortable: false, bVisible: true},
					{ sName: "nFolioPago",			bSearchable: true,	bSortable: true,  bVisible: true},
					{ sName: "caNoContrarrecibo",	bSearchable: true,	bSortable: true,  bVisible: true},
					{ sName: "FechaPagadoStr",		bSearchable: false,	bSortable: false, bVisible: true},
					{ sName: "MontoFactura",		bSearchable: false,	bSortable: false, bVisible: true},
					{ sName: "RFC",					bSearchable: false,	bSortable: false, bVisible: true},
					{ sName: "RazonSocial",			bSearchable: false,	bSortable: false, bVisible: true},
					{ sName: "MontoTotalREP",		bSearchable: false,	bSortable: false, bVisible: true}
				]
		});
	}
</script>

<div id="container" class="container" style="width: 100%">	
	<div class="table-responsive" style="width: 100%">
		<table id="dt_REP_Comprobados" class="table table-striped">
			<thead>
				<tr>
					<td>Tipo Pago</td>
					<td>Folio</td>
					<td>CXP</td>
					<td>Fecha Pago</td>
					<td>Monto Factura</td>
					<td>RFC</td>
					<td>Razon Social</td>
					<td>Monto REP</td>					
				</tr>
			</thead>
			<tbody />
		</table>
	</div>	
</div>
