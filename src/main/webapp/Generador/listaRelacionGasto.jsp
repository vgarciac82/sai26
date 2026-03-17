
<script type="text/javascript">

		function fnGrid(pParam, pFechaIni, pFechaFin, tipoIntegracion, radicado)
		{
			if (pParam == 'A02') pParam = 0; //RHQ -->A02 SE CAMBIA PARA EN CASO DE PERTENECER A GRF, MUESTRE LAS RG'S DE TODAS LAS UNIDADES EJECUTORAS PARA PODER HACER UNA INTEGRACION MULTIPLE
			var destinoGasto = " AND 1=2 ";
			if (tipoIntegracion=='CPRP')
				destinoGasto = " AND (ID_DESTINO_GASTO IN ('CPRP') OR (CTAB = 'Pago_Referenciado' AND ID_DESTINO_GASTO NOT IN ('2NRQ')))";
			else if (tipoIntegracion=='Otros')
				destinoGasto = " AND (ID_DESTINO_GASTO NOT IN ('CPRP','NORN','NORE','CQRE','COMP','2NRQ', 'RIVA') AND CTAB <> 'Pago_Referenciado')";
			else if (tipoIntegracion=='NORN')
				destinoGasto = " AND (ID_DESTINO_GASTO IN ('NORN') AND CTAB <> 'Pago_Referenciado')";
			else if (tipoIntegracion=='LaudosRetSICOP')
				destinoGasto = " AND (ID_DESTINO_GASTO IN ('NORE','CLRE','GLRE','CQRE')  and retSICOP='S' AND CTAB <> 'Pago_Referenciado')";
			else if (tipoIntegracion=='LaudosComp')
				destinoGasto = " AND (ID_DESTINO_GASTO IN ('NORE','CLRE','GLRE','CQRE') and retSICOP='N' AND CTAB <> 'Pago_Referenciado')";
			else if (tipoIntegracion=='Comprobacion')
				destinoGasto = " AND (ID_DESTINO_GASTO IN ('COMP') AND CTAB <> 'Pago_Referenciado')";
			else if (tipoIntegracion=='2NRQ')
				destinoGasto = " AND ID_DESTINO_GASTO IN ('2NRQ')";
			else if (tipoIntegracion=='RIVA')
				destinoGasto = " AND (ID_DESTINO_GASTO IN ('RIVA') AND CTAB <> 'Pago_Referenciado')";
			
			var cRadicado = " AND cRadicado = '"  + radicado + "'";
			$("#totalIntegrado").val( "0.00" );
			
			var oTableLocal = $('#dt_generados').dataTable({  
								"bLengthChange" : true,
					            "bFilter" : true,
					            "bSort" : true,
					            "bInfo" : true,
					            "bPaginate" : false,
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
								bServerSide : true,
								sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaRelacionGastos&qw=" + (pParam == 0 ? "" : "UNIDAD_EJECUTORA='" + pParam + "' and ") + "FECHA_PROGRAMADAQuery BETWEEN convert(datetime,'" + pFechaIni + " 00:00',103) AND convert(datetime,'" + pFechaFin + " 23:59:59',103)" + destinoGasto + cRadicado +" AND Folio NOT IN (SELECT DISTINCT encabezado.nFolioRELACIONGASTOS FROM tRELACIONGASTOSEncabezado encabezado WITH(nolock) INNER JOIN tRELACIONGASTOSDetalle detalle   WITH(nolock) on encabezado.nFolioRELACIONGASTOS = detalle.nFolioRELACIONGASTOS  WHERE SUBSTRING( ep, 40,1) = 4 AND encabezado.cDocumentoHaplicado = 'S')",																
								bProcessing : true,
								sPaginationType : "full_numbers",
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
								aaSorting : [ [ 3, "asc" ] ],
								aoColumns: [
						    		{ sName: "id",						bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
									{ sName: "cEsFirmaElectronica",		bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
									{ sName: "UNIDAD_EJECUTORA",		bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
									{ sName: "Folio",					bSearchable: true,	bSortable: true, bVisible: true},
									{ sName: "RFC",						bSearchable: true,	bSortable: true, bVisible: true},
									{ sName: "MONTO",					bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"},
									{ sName: "CUENTA_BANCARIA",			bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
									{ sName: "CONCEPTO",				bSearchable: true,	bSortable: false, bVisible: true},
									{ sName: "FECHA_PROGRAMADA",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
									{ sName: "LEYENDA",					bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
									{ sName: "CuentaXPagar",			bSearchable: true,	bSortable: true, bVisible: true},
									//{ sName: "cFolioRelacionGastos",	bSearchable: true,	bSortable: true, bVisible: true},
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
				if ($(aTrs[i]).hasClass('table-primary')) 
				{
					aReturn.push(aTrs[i]);
				}
			}
			return aReturn;
		}
</script>


	<div class="row d-flex">								
		<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
			<div class="table-responsive">	    				           
				<table id="dt_generados" class="table table-striped table-bordered" >	
					<thead>
						<tr align="center">
							<th>Seleccionar</th>
							<th align="center">FIEL</th>
							<th align="center">Unidad Ejecutora</th>
							<th align="center">Folio</th>
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


