	<%@ page import="com.syc.gestion.core.Usuario"%>
	<%@ page import="com.syc.gestion.servlet.GestionInterface"%>
	<%
		Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	%>

	<script type="text/javascript" charset="utf-8">
		var oTable;
		
		$(document).ready(function() {
			var complemento= " and nBCBEnviadoSICOP = 0";

			oTable = $("#tblBeneficiarios").dataTable({
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
					sInfoFiltered: "(filtado de _MAX_ registros)",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaBeneficiario&qw=nEnviadoSICOP=0" + complemento,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "id" },
					{ sName: "CBEN" },
					{ sName: "dRFC"   },
					{ sName: "Nombre" },
					{ sName: "cTipoPersonaRFC"	},
					{ sName: "cFolio"}
				]
			});
		});
	</script>
	
		<div id="container" class="ms-5" class="container" style="width: 90%">
			<h5> Beneficiarios generados </h5>
			<hr class="mt-3"/>
			<div class="row col-2 pt-3">
				<div class="form-check mx-3">
				  <input class="form-check-input" type="checkbox" id="chkTodos" name="chkTodos" value="" >
				  <label class="form-check-label" for="flexCheckDefault">
				    Seleccionar Todos
				  </label>
				</div>
			</div>
			<div class="row d-flex">								
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
		  			<div class="table-responsive">	    				           
			  			<table id="tblBeneficiarios" class="table table-striped table-bordered" >							
							<thead>
								<tr align="center">
									<th>Seleccione</th>
									<th>CBEN</th>
									<th>R.F.C.</th>
									<th>Nombre</th> 
									<th>Tipo Persona</th>
									<th>Folio</th> 
								</tr>
							</thead>
							<tbody/>
						</table>
					</div>
				</div>
			</div>
					
			<br/>
			
		</div>