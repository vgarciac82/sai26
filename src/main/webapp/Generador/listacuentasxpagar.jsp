    	<script type="text/javascript">
    		$(document).ready(function () {
    			var oTableLocal = $('#dt_generados').dataTable({
					sScrollY: "340",
					sScrollX: "100%",
					sScrollXInner: "100%",

					bRetrive: true,
					bPaginate: false,
					bDestroy: true,
					//bLengthChange: false,
        			bFilter: false,
        			bSort: true,
        			bInfo: false,
        			bAutoWidth: true,

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

					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaCompromisos&qw=estadoCompromiso = 'CREADO'",
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aoColumns: [
					    { sName: "id",						bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cIdEntidadContable",		bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "caNoCompromiso",			bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cIdContrato",				bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cTipoDocumento",			bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "estadoCompromiso",		bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "fAplicacion",				bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cRamo",					bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cIdUnidadAdministrativa",	bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cIdRFC",					bSearchable: false,	bSortable: false, bVisible: true}
					]

        		});
			});
		</script>

  		<div id="container">
            <div id="demo_jui">
  				<table id="dt_generados">
					<thead>
						<tr align="center">
							<th>
				<!-- 
							<input type="checkbox" name="integraTodos" id="integraTodos" value="" onclick="toggleReactivar(this)"/>
				 
							<input type="checkbox" name="integraTodos" id="integraTodos" value="" />
				-->
							<font size="2">Seleccione</font>
							</th>
            				<th><font size="2">E.C.</font></th>
            				<th><font size="2">Compromiso</font></th>
            	<!--
            	    		<th><font size="2">C&eacute;dula</font></th>
            	    		<th><font size="2">Movimiento</font></th>
            	-->
            	    		<th><font size="2">Documento</font></th>
            				<th><font size="2">Tipo Documento</font></th>
            	<!--
            				<th><font size="2">Subtipo Documento</font></th>
            	-->
            				<th><font size="2">Estado</font></th>
            	    		<th><font size="2">Fecha</font></th>
            	    		<th><font size="2">Ramo</font></th>
            	    		<th><font size="2">Unidad</font></th>
            	    		<th><font size="2">RFC</font></th>
            			</tr>
					</thead>
					<tbody />
				</table>
			</div>
		</div>
