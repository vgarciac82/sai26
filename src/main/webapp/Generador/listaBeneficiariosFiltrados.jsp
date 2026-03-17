	<%@page import="com.syc.gestion.core.Usuario"%>
	<%@page import="com.syc.gestion.servlet.GestionInterface"%>
	<%
		Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	%>
		<script type="text/javascript">
			var oTableEnviados
		
			$(document).ready(function () {
				oTableEnviados = $('#dt_envioBeneficiarios').dataTable({
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
			});
		});
		</script>
		
		<div id="container" class="ms-5" class="container" style="width: 90%">
			<form name="envioBeneficiariosSICOP" id="envioBeneficiariosSICOP" action="../gstnmngr/LayoutBeneficiarios" method="post">			
				<input type="hidden" id="envioP" name="envioP" />
				<input type="hidden" id="cFolios" name="cFolios" />
			
  				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
			  			<div class="table-responsive">	    				           
				  			<table id="dt_envioBeneficiarios" class="table table-striped table-bordered" >								
								<thead>
									<tr>
										<th>CBEN</th>
										<th>R.F.C.</th>
										<th>Nombre</th>
										<th>Tipo Persona</th>
									</tr>
								</thead>
								<tbody/>
							</table>
						</div>
					</div>
				</div>
				
				<br/>
				
				<div align="center">
					<h5> Descargar Layouts </h5>
					<hr class="mt-3"/>
										
					<div class="row d-flex">
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						</div>				
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1" >
							<input type="button" type="button" onClick="generarB();" value="Beneficiario" id="buttonBen" class="btn btn-primary"/>
						</div>				
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<input type="button" type="button" onClick="generarBDC();" value="Documentaci&oacute;n Comprobatoria" id="buttonDC" class="btn btn-secondary"/>
						</div>				
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<input type="button" type="button" onClick="generarRB();" value="Roles de Beneficiario" id="buttonRB" class="btn btn-success"/>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<input type="button" type="button" onClick="generarBCB();" value="Cuentas Bancarias" id="buttonCB" class="btn btn-dark"/>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						</div>										
					</div>						
				</div>
						
				<br/>
						
				<div class="row d-flex">
					<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
					</div>								
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<input type="button" type="button" onClick="cambiarEstatus();" value="Finalizar descarga de archivos" id="buttonFin" class="btn btn-secondary">
					</div>
				</div>
				
			</form>
		</div>