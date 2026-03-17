
<script type="text/javascript">

	function fnGrid(pParam, pFechaIni, pFechaFin, radicado) {
		if( pParam == 'A02'  )
			pParam = 0;
		var cRadicado = " AND cRadicado = '" + radicado + "'";
		var oTableLocal = $('#dt_generados').dataTable({
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
			oLanguage : {
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
				sSearch : "Buscar:",
				oPaginate : {
					sFirst : "Primero",
					sPrevious : "Ant.",
					sNext : "Sigte.",
					sLast : "&Uacute;ltimo"
				}
			},
			bServerSide : true,
			sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=vListaPagoObras&qw=" + ( pParam == 0 ? "" : "UNIDAD_EJECUTORA='" + pParam + "' and " ) + "FECHA_PROGRAMADA BETWEEN convert(datetime,'" + pFechaIni + " 00:00',103) AND convert(datetime,'" + pFechaFin + " 23:59',103)" + cRadicado,
			bProcessing : true,
			sPaginationType : "full_numbers",
			bJQueryUI : true,
			aaSorting : [ [ 3, "asc" ] ],
			aoColumns : [
				{
					sName : "id",
					bSearchable : true,
					bSortable : false,
					bVisible : true,
					sClass : "alignCenter"
				},
				{
					sName : "cEsFirmaElectronica",
					bSearchable : true,
					bSortable : true,
					bVisible : true
				},
				{
					sName : "UNIDAD_EJECUTORA",
					bSearchable : true,
					bSortable : true,
					bVisible : true
				},
				{
					sName : "Folio",
					bSearchable : true,
					bSortable : true,
					bVisible : true
				},
				{
					sName : "RFC",
					bSearchable : true,
					bSortable : true,
					bVisible : true
				},
				{
					sName : "MONTO",
					bSearchable : true,
					bSortable : false,
					bVisible : true,
					sClass : "alignRight"
				},
				{
					sName : "CUENTA_BANCARIA",
					bSearchable : true,
					bSortable : false,
					bVisible : true,
					sClass : "alignCenter"
				},
				{
					sName : "CONCEPTO",
					bSearchable : true,
					bSortable : false,
					bVisible : true
				},
				{
					sName : "FECHA_PROGRAMADA",
					bSearchable : true,
					bSortable : false,
					bVisible : true,
					sClass : "alignCenter"
				},
				{
					sName : "LEYENDA",
					bSearchable : true,
					bSortable : false,
					bVisible : true,
					sClass : "alignCenter"
				},
				{
					sName : "CuentaXPagar",
					bSearchable : true,
					bSortable : false,
					bVisible : true
				}
			]
		});

		$("#dt_generados tbody").click(function(event) {
			$(oTableLocal.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('table-primary');
			});
			$(event.target.parentNode).addClass('table-primary');
			$("#pbFechaProgramada").css("visibility", "visible");
			$("#pbLeyenda").css("visibility", "visible");
		});

		function fnGetSelected(oTableLocal) {
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();

			for( var i = 0; i < aTrs.length; i++ ) {
				if( $(aTrs[ i ]).hasClass('table-primary') ) {
					aReturn.push(aTrs[ i ]);
				}
			}
			return aReturn;
		}
	}
</script>


	<div class="row d-flex">								
		<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
  			<div class="table-responsive">	    				           
	  			<table id="dt_generados" class="table table-striped table-bordered" >			
					<thead>
						<tr align="center">
							<th>Seleccionar</th>
							<th>FIEL</th>
							<th>Unidad Ejecutora</th>
							<th>Folio</th>
							<th>RFC</th>
							<th align="right">Monto</th>
							<th>Cuenta Bancaria</th>
							<th>Concepto</th>
							<th>Fecha Programada</th>
							<th align="center">Leyenda</th>
							<th>Cuenta por Pagar</th>
						</tr>
					</thead>
					<tbody />
				</table>
			</div>
		</div>
	</div>

