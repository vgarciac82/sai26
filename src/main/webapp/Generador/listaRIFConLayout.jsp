<script type="text/javascript">
	function fnGridConLayout(pParam, pFechaIni, pFechaFin){
 		if (pParam == 'A02')
 			pParam = 0;	    			
 		var oTableCuentaConLayout = $('#dt_CuentaConLayout').dataTable({
 			"bFilter" : false,
			"bSort" : true,
			"bPaginate" : false,
			"bLengthChange" : true,
			"bInfo" : false,
			"bJQueryUI" : true,
			"bAutoWidth" : false,
			"sPaginationType" : "full_numbers",
			"bRetrive" : true,
			"bDestroy" : true,
			"bServerSide": true,
			fnInitComplete: function() {    
                                                   oTableCuentaConLayout.fnAdjustColumnSizing();
                                               },
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
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaRIFConLayout&qw=" + (pParam == 0 ? "" : "UNIDAD_EJECUTORA='" + pParam + "' AND ") + "FECHA_PROGRAMADAQuery BETWEEN convert(datetime,'" + pFechaIni + " 00:00',103) AND convert(datetime,'" + pFechaFin + " 23:59:59',103)",
			bProcessing: true,
			sPaginationType: "full_numbers",
			bJQueryUI: true,
			aoColumns: [
			    { sName: "id",						bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "UNIDAD_EJECUTORA",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "Folio",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "RFC",						bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "MONTO",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignRight"},
				{ sName: "CUENTA_BANCARIA",			bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "CONCEPTO",				bSearchable: false,	bSortable: false, bVisible: true},
				{ sName: "FECHA_PROGRAMADA",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "LEYENDA",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "TIPO_DOCTO",				bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "caNoContrarrecibo",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "sAuxiliarComodin",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"}
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
	
		for ( var i=0 ; i<aTrs.length ; i++ ){         
			if ($(aTrs[i]).hasClass('table-primary')){             
				aReturn.push( aTrs[i] );         
			}
		}
		return aReturn; 
	}
}
 		
</script>


	<form name="RGConLayout" id="RGConLayout" action="../gstnmngr/RegistroIngresoConLayoutServlet" method="post">
		<div>
			<input type="hidden" id="sDataFolios" name="sDataFolios" />
			<input type="hidden" id="u_login" name="u_login"/>
		</div>
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
		         	    		<th><font size="2">CXP</font></th>
		          	    		<th><font size="2">#Integración</font></th>
		         			</tr>
						</thead>
					</table>
				</div>
			</div>
		</div>
	</form>
