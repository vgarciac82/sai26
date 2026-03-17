    	<script type="text/javascript">
    		function fnGridConLayout(pParam, pFechaIni, pFechaFin, UMA)
    		{
    			if (pParam == 'A02' )   pParam =0 ;
    			
    			var sWhereUMAS = "";
    			
    			if(UMA == "0"){  // MONTO MAYOR A 300 UMAS
    				sWhereUMAS = " AND tipoUMA = 0 ";
    			}else if(UMA == "1"){ // MONTO MENOR A 300 UMAS
    				sWhereUMAS = " AND tipoUMA = 1 ";
    			}
    				    			    			
    			var oTableCuentaConLayout = $('#dt_CuentaConLayout').dataTable({
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaPagosDiversosConLayout" + (pParam == 0 ? "" : "&qw=UNIDAD_EJECUTORA='" + pParam + "'" + sWhereUMAS ),
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aoColumns: [
					    { sName: "id",						bSearchable: false,	bSortable: false, bVisible: true},
					    { sName: "CXP",						bSearchable: true,	bSortable: false, bVisible: true},
						{ sName: "UNIDAD_EJECUTORA",		bSearchable: true,	bSortable: true, bVisible: true},
						{ sName: "Folio",					bSearchable: true,	bSortable: true, bVisible: true},
						{ sName: "RFC",						bSearchable: true,	bSortable: true, bVisible: true},
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

  		<div id="container" class="ms-5" class="container" style="width: 90%">
	  		<form name="PagosDiversosConLayout" id="PagosDiversosConLayout" action="../gstnmngr/PagosDiversosConLayout" method="post">
		  		<input type="hidden" id="sDataFolios" name="sDataFolios" />
			    	
	            <div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
			  			<div class="table-responsive">	    				           
				  			<table id="dt_CuentaConLayout" class="table table-striped table-bordered" >		  						
								<thead>
									<tr>
										<th scope="col">Seleccione</th>
										<th scope="col">CXP</th>
			            				<th scope="col">Unidad Ejecutora</th>
			            				<th scope="col">Folio</th>
			            				<th scope="col">RFC</th>
			            	    		<th scope="col">Monto</th>
			            	           	<th scope="col">Cuenta Bancaria</th>
			            				<th scope="col">Concepto</th>
			            	    		<th scope="col">Fecha Programada</th>
			            	    		<th scope="col">Leyenda</th>
			            	    		<th scope="col">Tipo Docto</th>
			            			</tr>
								</thead>
								<tbody />
							</table>
						</div>
					</div>
				</div>
			</form>
		</div>
