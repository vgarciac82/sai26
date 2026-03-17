    	<script type="text/javascript">
    		function fnGridConLayout(pParam, pFechaIni, pFechaFin, UMA)
    		{
    		
    			var sWhereUMAS = "";
    			if(UMA == "0"){  // MONTO MAYOR A 300 UMAS
    				sWhereUMAS = " AND tipoUMA = 0 ";
    			}else if(UMA == "1"){ // MONTO MENOR A 300 UMAS
    				sWhereUMAS = " AND tipoUMA = 1 ";
    			}
    			
    			if (pParam == 'A02' )	pParam=0;    			    			
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaPagofederalConLayout" + (pParam == 0 ? "" : "&qw=UNIDAD_EJECUTORA='" + pParam + "'" + sWhereUMAS ),
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aaSorting: [[ 2, "asc" ]] ,
					aoColumns: [
					    { sName: "id",						bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "UNIDAD_EJECUTORA",		bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "Folio",					bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "RFC",						bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "MONTO",					bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "CUENTA_BANCARIA",			bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "CONCEPTO",				bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "FECHA_PROGRAMADAquery",	bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "LEYENDA",					bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "TIPO_DOCTO",				bSearchable: false,	bSortable: false, bVisible: true}
					]
					
        		});
   			
    			$("#dt_CuentaConLayout tbody").click(function(event) {
					$(oTableCuentaConLayout.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
					//$("#pbStatusInicial").css("visibility","visible");
				});
    			
    			function fnGetSelected( oTableLocal ) {     
				var aReturn = new Array();     
				var aTrs = oTableCuentaConLayout.fnGetNodes();           
				
				for ( var i=0 ; i<aTrs.length ; i++ )     
				{         
					if ( $(aTrs[i]).hasClass('row_selected') )         
					{             
						aReturn.push( aTrs[i] );         
					}     
				}     
				return aReturn; 
			}
    			
    		}
    		
		</script>

  		
	  		<form name="PagoProgFederalizadosConLayout" id="ConLayout" action="../gstnmngr/PagoProgFederalizadosConLayout" method="post">
	  			<input type="hidden" id="sDataFolios" name="sDataFolios" />
			    	
	            <div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
			  			<div class="table-responsive">	    				           
				  			<table id="dt_CuentaConLayout" class="table table-striped table-bordered" >
								<thead>
									<tr align="center">
										<th><font size="2">Seleccionar</font></th>
			            				<th><font size="2">Unidad Ejecutora</font></th>
			            				<th><font size="2">Folio</font></th>
			            				<th><font size="2">RFC</font></th>
			            	    		<th><font size="2">Monto</font></th>
			            	           	<th><font size="2">Cuenta Bancaria</font></th>
			            				<th><font size="2">Concepto</font></th>
			            	    		<th><font size="2">Fecha Programada</font></th>
			            	    		<th><font size="2">Leyenda</font></th>
			            	    		<th><font size="2">Tipo Docto</font></th>
			            			</tr>
								</thead>
								<tbody />
							</table>
						</div>
					</div>
				</div>
			</form>
		
