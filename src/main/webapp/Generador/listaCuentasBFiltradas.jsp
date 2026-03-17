		<script type="text/javascript">
    		$(document).ready(function () {
    			
   				var oTableEnviados = $('#dt_envioCuentasB').dataTable({
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
					//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaCuentasporPagar",
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true
					
        		});
   				

        	});
		</script>
		
		<div id="container" class="ms-5" class="container" style="width: 90%">
			<form name="envioCuentasBSICOP" id="envioCuentasBSICOP" action="../gstnmngr/LayoutCuentasB" method="post">			
				<input type="hidden" id="envioP" name="envioP" />
				<input type="hidden" id="cFolios" name="cFolios" />
			
  				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
			  			<div class="table-responsive">	    				           
				  			<table id="dt_envioCuentasB" class="table table-striped table-bordered" >					
								<thead>
									<tr>
										<th>Banco</th>
										<th>No. Cuenta</th>
										<th>Estatus</th>
										<th>CBEN</th>
										<th>RFC</th>
										<th>Nombre Beneficiario</th>
			                		</tr>
								</thead>
								<tbody />
							</table>
						</div>
					</div>
				</div>
				
				<br/>
				
				<div class="row d-flex">												
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<input type="button" onClick="generarLCuentasB();" id="generaCuentas" class="btn btn-secondary btn-sm" value="Generar Layout"/>						
					</div>
				</div>
				
			</form>
		</div>