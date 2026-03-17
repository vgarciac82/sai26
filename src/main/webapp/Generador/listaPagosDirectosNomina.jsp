
	
   	<script type="text/javascript">
   	
   		function fnGrid(pParam, pFechaIni, pFechaFin, compromiso)
   		{
			if (pParam == 'A03') pParam = 0;
							
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
					sSearch: "Buscar:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				},
				bServerSide: true,					
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaCuentasPorPagar_Nomina&qw=" + (pParam == 0 ? "" : "UNIDAD_EJECUTORA='" + pParam + "' and ") + "CAST(FECHA_PROGRAMADA AS DATE) BETWEEN '" + pFechaIni + "' AND '" + pFechaFin + "' AND cTieneCompromiso = '" + compromiso + "'",
				bProcessing: true,
				sPaginationType: "full_numbers",
				fnInitComplete: function(settings, json) {
									$(".RG_SEL").each(function(){
										$(this).click(function(){
											actualizaTotales();
										});
									});
								  },
				fnDrawCallback :function( oSettings ) {
							      $(".RG_SEL").each(function(){
										$(this).click(function(){
											actualizaTotales();
										});
									});
							    },
				bJQueryUI: true,
				aaSorting: [[ 3, "asc" ]] ,
				aoColumns: [
				    { sName: "id",						bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
					{ sName: "UNIDAD_EJECUTORA",		bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignCenter"},
					{ sName: "Folio",					bSearchable: true,	bSortable: true,  bVisible: true},
					{ sName: "RFC",						bSearchable: true,	bSortable: true	, bVisible: true},
					{ sName: "MONTO",					bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"},
					{ sName: "CUENTA_BANCARIA",			bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
					{ sName: "CONCEPTO",				bSearchable: true,	bSortable: false, bVisible: true},
					{ sName: "FECHA_PROGRAMADA",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
					{ sName: "LEYENDA",					bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
					{ sName: "CuentaXPagar",			bSearchable: true,	bSortable: true,  bVisible: true},
					{ sName: "cTieneCompromiso",		bSearchable: true,	bSortable: true,  bVisible: true, sClass: "alignCenter"}						
				],
				aLengthMenu: [10, 50, 100, 200, 500, "ALL"]
       		});	
   			
    			$("#dt_generados tbody").click(function(event) {
    				$(oTableLocal.fnSettings().aoData).each(function() {
    					$(this.nTr).removeClass('table-primary');
    				});
    				$(event.target.parentNode).addClass('table-primary');
    				$("#pbCuentasBancarias").css("visibility", "visible");
    				$("#pbFechaProgramada").css("visibility", "visible");
    				$("#pbLeyenda").css("visibility", "visible");
    			});
    			
    			/*VGC20180119 Cambios para generar layout de compromiso cuando la integradora suma mas de 300 UMAS*/
    			$("#checkAll").removeAttr('checked');
   		}
   			
   		function fnGetSelected(oTableLocal) {
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();

			for ( var i = 0; i < aTrs.length; i++) 
			{
				if ($(aTrs[i]).hasClass('row_selected')) 
				{
					aReturn.push(aTrs[i]);
				}
			}
			return aReturn;
		}
   	  		
	</script>

<div id="container" class="ms-5" class="container" style="width: 90%">
	<div class="row d-flex">								
		<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
  			<div class="table-responsive">	    				           
	  			<table id="dt_generados" class="table table-striped table-bordered" >	
					<thead>
						<tr align="center">
							<th>Seleccionar</th>
		       				<th>Unidad Ejecutora</th>
		       				<th>Folio</th>
		       				<th>RFC</th>
		       	    		<th align="right">Monto</th>
		       	           	<th>Cuenta Bancaria</th>
		       				<th>Concepto</th>
		       	    		<th>Fecha Programada</th>
		       	    		<th align="center">Leyenda</th>
		       				<th>CXP</th>
		       				<th>Compromiso</th>
		         		</tr>
					</thead>			
				</table>	
			</div>
		</div>			
	</div>
</div>