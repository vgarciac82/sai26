    	<script type="text/javascript">
    		$(document).ready(function () {
    			var oTableEnviados = $('#dt_paraEnvio').dataTable({
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

					bServerSide: false,
					//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaCompromisos&qw=estadoCompromiso = 'CREADO'",
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true					
        		});
        	});
		</script>

  		<div id="container">
  			<form name="envioSICOP" id="envioSICOP" action="../gstnmngr/generaLayoutCompromisos" method="post">
  			     <div>
		    		<input type="hidden" id="sDataH" name="sDataH" />
		    	</div>
  				<div id="demo_jui">
					<table id="dt_paraEnvio">
						<thead>
							<tr align="center">
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
				<div style="text-align:left; padding-bottom: 1em;">
					<br/>
					<button type="button" onClick="generar();">Generar Layout</button>
				</div>
			</form>
		</div>
