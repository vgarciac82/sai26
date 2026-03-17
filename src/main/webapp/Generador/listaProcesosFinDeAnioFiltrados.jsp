	<%@page import="com.syc.gestion.core.Usuario"%>
	<%@page import="com.syc.gestion.servlet.GestionInterface"%>
	<%
		Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	%>
		<script type="text/javascript">
			$(document).ready(function () {
				var oTableEnviados = $('#tbl_envioListadoPagos').dataTable({
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
   					"iDisplayLength": 150,	 
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
						oPaginate: { sFirst: "Primero", sPrevious: "Ant.", sNext: "Sigte.", sLast: "&Uacute;ltimo" }
					},
					bServerSide: false,
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true		
			});
		});
		</script>
		
		<div id="container" class="ms-5" class="container" style="width: 90%" >
			<form name="envioLayOutProcesos" id="envioLayOutProcesos" action="../gstnmngr/LayoutProcesosFinAnio" method="post">			
				<input type="hidden" id="evento" name="evento" />
				<input type="hidden" id="cFolios" name="cFolios" />
				<input type="hidden" id="cTipoPago" name="cTipoPago" />
				
				<div class="row d-flex">								
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<input type="button" type="button" onClick="generar();" value="Lay Out" id="buttonLayOut" class="btn btn-secondary btn-sm "/>
					</div>
				</div>
				
				<br/>
			
  				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
			            <div class="table-responsive">	    				           
			  				<table id="tbl_envioListadoPagos" class="table table-striped table-bordered" >
						
								<thead>
									<tr>
										<th>Folio Pagado</th>
										<th>Tipo Pago</th>
										<th>Concepto</th>
										<th>Monto</th>
										<th>RFC</th>
										<th>Cuenta Bancaria</th>
										<th>Cuenta Contable</th>
										<th>Evento</th>
									</tr>
								</thead>
								<tbody/>
							</table>
						</div>
					</div>
				</div>
				
				<br/>				
																
			</form>
		</div>