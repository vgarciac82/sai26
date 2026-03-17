<script type="text/javascript">
	$(document).ready(function(){
		
		var oTableEnviados = $('#dt_paraEnvio').dataTable({
			"bLengthChange" : true,
	            "bFilter" : true,
	            "bSort" : true,
	            "bInfo" : true,
	            "bPaginate" : true,
	            "bAutoWidth" : false,
	            "bScrollCollapse" : true,   		            
	            "sPaginationType" : "full_numbers",
	            "bJQueryUI" : true,
	            "bRetrive" : true,
	            "bDestroy" : true,
	            "bServerSide": true,                   
				"iDisplayLength": 25,	       
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

			bServerSide: false,						
			bProcessing: true,
			sPaginationType: "full_numbers",
			bJQueryUI: true,
			aoColumns: [
				{ sName: "UNIDAD_EJECUTORA",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "Folio",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "RFC",						bSearchable: false,	bSortable: false, bVisible: true},
				{ sName: "MONTO",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignRight"},
				{ sName: "CUENTA_BANCARIA",			bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "CONCEPTO",				bSearchable: false,	bSortable: false, bVisible: true},
				{ sName: "FECHA_PROGRAMADA",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "LEYENDA",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},				
				{ sName: "TIPO_DOCTO",				bSearchable: false,	bSortable: false, bVisible: false},
				{ sName: "CuentaXPagar",			bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"}
				]
      	});			
  				
  		$("#dt_paraEnvio tbody").click(function(event){
			$(oTableLocal.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('table-primary');
			});
			$(event.target.parentNode).addClass('table-primary');
		});
	});
</script>


	<form name="envioSICOP" id="envioSICOP" action="../gstnmngr/generaLayoutRegistroIngreso" method="post">
		<input type="hidden" id="archivo" name="archivo" />					
		<input type="hidden" id="sDataH" name="sDataH" />
		<input type="hidden" id="sDataHCB" name="sDataHCB" />
		<input type="hidden" id="sDataHFecha" name="sDataHFecha" />
		<input type="hidden" id="sDataHLeyenda" name="sDataHLeyenda" />

		<div class="row d-flex">								
			<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
				<div class="table-responsive">	    				           
					<table id="dt_paraEnvio" class="table table-striped table-bordered" >
						<thead>
							<tr align="center">
								<th>Unidad Ejecutora</th>
								<th>Folio</th>
								<th>RFC</th>
								<th>Monto</th>
								<th>Cuenta Bancaria</th>
								<th>Concepto</th>
								<th>Fecha Programada</th>
								<th>Leyenda</th>
								<th>Tipo Docto</th>
								<th>Cuenta por Pagar</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
		</div>


		<div class="row d-flex">								
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				<input type="button" id="pbEnvia" style="visibility: hidden" type="button" onClick="generar();" value="Generar Layout" class="btn btn-secondary btn-sm"/>
			</div>
		</div>

	</form>
