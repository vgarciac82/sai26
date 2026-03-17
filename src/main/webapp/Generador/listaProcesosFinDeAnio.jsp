	<%@ page import="com.syc.gestion.core.Usuario"%>
	<%@ page import="com.syc.gestion.servlet.GestionInterface"%>	
	<%
		Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
		System.out.println(usuario.getU_UR());
	%>
	
	<script type="text/javascript" charset="utf-8">
		var oTable;
		
		$(document).ready(function() {
			oTable = $("#tblListadoPagos").dataTable({
				"bLengthChange" : true,
		            "bFilter" : true,
		            "bSort" : true,
		            "bInfo" : true,
		            "bPaginate" : true,
		            "bAutoWidth" : false,
		            "bScrollCollapse" : true,   		            
		            "sPaginationType" : "full_numbers",
		            "bJQueryUI" : true,
		            //"bRetrive" : true,
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
					sInfoFiltered: "(filtado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: { sFirst: "Primero", sPrevious: "Ant.", sNext: "Sigte.", sLast: "&Uacute;ltimo" }
				},												
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaFinDeAnio",
				bProcessing: true,
				sPaginationType: "full_numbers",				
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "id" },
					{ sName: "cUnidadResponsable" },
					{ sName: "nFolioPagado" },
					{ sName: "cTipoPago" },
					{ sName: "caNoContrarrecibo" },
					{ sName: "cDescripcionPoliza" },
					{ sName: "importe" },
					{ sName: "RFC" },
					{ sName: "ctab" },
					{ sName: "nCuenta" },
					{ sName: "cEvento" }
				]
			});
		});
	</script>
	
	<div id="container" class="ms-5" class="container" style="width: 90%" >
		
		<div class="row d-flex">								
			<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
	            <div class="table-responsive">	    				           
	  				<table id="tblListadoPagos" class="table table-striped table-bordered" >
						<thead>
							<tr>
								<th>Seleccione</th>
								<th>Unidad Responsable</th>
								<th>Folio Pagado</th>
								<th>Tipo Pago</th>
								<th>CxP</th>
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
		
	</div>