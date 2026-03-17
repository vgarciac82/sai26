    	<script type="text/javascript">
    		$(document).ready(function () {
    			
    				var oTableEnviados = $('#dt_paraEnvio').dataTable({
					//sScrollY: "240",
					//sScrollX: "100%",
					//sScrollXInner: "100%",

					bRetrive: true,
					bPaginate: false,
					bDestroy: true,
					//bLengthChange: false,
        			bFilter: false,
        			bSort: true,
        			bInfo: false,
        			bAutoWidth:false,

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
					bJQueryUI: true,
					aoColumns: [
					    
						{ sName: "UNIDAD_EJECUTORA",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "Folio",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "RFC",						bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "MONTO",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignRight"},
						{ sName: "CUENTA_BANCARIA",			bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "CONCEPTO",				bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "FECHA_PROGRAMADA",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "LEYENDA",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "TIPO_DOCTO",				bSearchable: false,	bSortable: false, bVisible: false},
						{ sName: "CuentaXPagar",			bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"}
					]
					
        		});
    				
    				$("#dt_paraEnvio tbody").click(function(event) {
					$(oTableLocal.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
					//$("#pbEnvia").css("visibility","visible");
					//$("#pbEnviaDocumentacion").css("visibility","visible");
										
				});
        	});
		</script>

  		<div id="container">
  			<form name="envioSICOP" id="envioSICOP" action="../gstnmngr/generaLayoutOperacionAjena" method="post">
				<div>
					<input type="hidden" id="archivo" name="archivo" />					
		    		<input type="hidden" id="sDataH" name="sDataH" />
		    		<input type="hidden" id="sDataHCB" name="sDataHCB" />
		    		<input type="hidden" id="sDataHFecha" name="sDataHFecha" />
		    		<input type="hidden" id="sDataHLeyenda" name="sDataHLeyenda" />
		    	</div>
  				<div id="demo_jui">
					<table id="dt_paraEnvio" class="display">
						<thead>
							<tr align="center">
	            				<th>Unidad Ejecutora</th>
	            				<th>Folio</th>
	            				<th>RFC</th>
	            	    		<th>Monto</th>
	            	           	<th>Cuenta Bancaria</th>
	            				<th>Concepto</th>
	            	    		<th>Fecha Programada</th>
	            	    		<th>Leyenda</th>
	            	    		<th>Tipo Docto</th>
	            	    		<th>Cuenta por Pagar</th>
            	    		</tr>
						</thead>
						<tbody />
					</table>
				</div>
				<div style="text-align: left; padding-bottom: 1em;">
					<br/>
					<input type="button" id="pbEnvia" style="visibility: hidden" type="button" onClick="generar();" value="Generar Layout"/>
				</div>
			</form>
		</div>
