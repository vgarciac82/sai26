    	<script type="text/javascript">
    		function fnGridConLayout(pParam, pFechaIni, pFechaFin)
    		{
    			if (pParam == 'A02')   
    				pParam=0 ;
    			
    			var oTableCuentaConLayout = $('#dt_CuentaConLayout').dataTable({
    				"bFilter" : false,
     				"bSort" : true,
     				"bPaginate" : false,
     				"bLengthChange" : true,
     				"bInfo" : false,
     				"bJQueryUI" : true,
     				"bAutoWidth" : false,
     				"sPaginationType" : "full_numbers",
     				"bRetrive" : true,
     				"bDestroy" : true,
     				"bServerSide": true,
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
					//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaPagoObrasConLayout" + (pParam == 0 ? "" : "&qw=UNIDAD_EJECUTORA='" + pParam + "'"),							
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaPagoObrasConLayout" + (pParam == 0 ? "" : "&qw=UNIDAD_EJECUTORA='" + pParam + "'"),
							aaSorting: [[ 2, "asc" ]] ,
					aoColumns: [
					    { sName: "id",						bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "UNIDAD_EJECUTORA",		bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "Folio",					bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "RFC",						bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "MONTO",					bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "CUENTA_BANCARIA",			bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "CONCEPTO",				bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "FECHA_PROGRAMADA",		bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "LEYENDA",					bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "TIPO_DOCTO",				bSearchable: false,	bSortable: false, bVisible: true}
					]
					
        		});
   			
    			$("#dt_CuentaConLayout tbody").click(function(event) {
					$(oTableCuentaConLayout.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('table-primary');
					});
					$(event.target.parentNode).addClass('table-primary');
				});
    			
    			function fnGetSelected( oTableLocal ) {     
				var aReturn = new Array();     
				var aTrs = oTableCuentaConLayout.fnGetNodes();           
				
				for ( var i=0 ; i<aTrs.length ; i++ )     
				{         
					if ( $(aTrs[i]).hasClass('table-primary') )         
					{             
						aReturn.push( aTrs[i] );         
					}     
				}     
				return aReturn; 
			}
    			
    		}
    		
		</script>

  		
	  		<form name="PagoObrasConLayout" id="ConLayout" action="../gstnmngr/PagoObrasConLayout" method="post">
	  			<input type="hidden" id="sDataFolios" name="sDataFolios" />
			    			    
	            <div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
			  			<div class="table-responsive">	    				           
				  			<table id="dt_CuentaConLayout" class="table table-striped table-bordered" >							  											
								<thead>
									<tr>
										<th>Seleccionar</th>
			            				<th>Unidad Ejecutora</th>
			            				<th>Folio</th>
			            				<th>RFC</th>
			            	    		<th>Monto</th>
			            	           	<th>Cuenta Bancaria</th>
			            				<th>Concepto</th>
			            	    		<th>Fecha Programada</th>
			            	    		<th>Leyenda</th>
			            	    		<th>Tipo Docto</th>
			            			</tr>
								</thead>								
							</table>
						</div>
					</div>
				</div>
			</form>

