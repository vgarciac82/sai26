    	<script type="text/javascript">
    		function fnGridConLayout(pParam, pFechaIni, pFechaFin){
    			if (pParam == 'RHQ' )   pParam =0 ;			    			
    			var oTableCuentaConLayout = $('#dt_CuentaConLayout').dataTable({
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
					//bAutoWidth : false,
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaCadenasPConLayout&qw=" + (pParam == 0 ? "" : "cUnidadEjecutora='" + pParam + "' AND ") + "FPROGRAMADA BETWEEN convert(datetime,'" + pFechaIni + " 00:00',103) AND convert(datetime,'" + pFechaFin + " 23:59:59',103)",
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aaSorting: [[ 2, "asc" ]] ,
					aoColumns: [
					    { sName: "id",						bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cUnidadEjecutora",		bSearchable: false,	bSortable: true, bVisible: true},
						{ sName: "CFolio",					bSearchable: false,	bSortable: true, bVisible: true},
						{ sName: "RFC",						bSearchable: false,	bSortable: true, bVisible: true},
						{ sName: "mMONTO",					bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cCuentaBancaria",			bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "CCONCEPTO",				bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "FPROGRAMADA",				bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "caNoContrarrecibo",		bSearchable: false,	bSortable: false, bVisible: true},
						{ sName: "cTIPODOCTO",				bSearchable: false,	bSortable: false, bVisible: true}
					]
					
        		});
   			
}

 function BorratbLayout (name){	
 
 	if ( $( "#"+name ).is(':checked') == true  ){

		var table = document.getElementById('dt_CuentaConLayout');
		var rowCount = table.rows.length;
		
		for ( var i = 1; i < rowCount; i++) {
		
			var row = table.rows[i];
			if(row.cells[2].childNodes[0].data == name){
				
			var row = table.rows[i];
			var checado =  row.cells[0].childNodes[0].name;
			var UnidadEjecutora = row.cells[1].childNodes[0].data;
			var Folio = row.cells[2].childNodes[0].data;
			var RFC = row.cells[3].childNodes[0].data;
			var Monto = row.cells[4].childNodes[0].data;
			var CuentaB = row.cells[5].childNodes[0].data;
			var concepto = row.cells[6].childNodes[0].data;
			var FechaProgramada = row.cells[7].childNodes[0].data;
			var contrarecibo = row.cells[8].childNodes[0].data;
			var TipoDocto = row.cells[9].childNodes[0].data;
			
			$("#unidadEjec").val(UnidadEjecutora);
			$("#folio").val(Folio);
			$("#RFC").val(RFC);
			$("#Monto").val(Monto);
			$("#CuentaBancaria").val(CuentaB);
			$("#Concepto").val(concepto);
			$("#Fechaprog").val(FechaProgramada);
			$("#contrarecibo").val(contrarecibo);
			$("#tipoDoc").val(TipoDocto);
			
				if(confirm ("Desea habilitar la Cuenta por Pagar ")){
				
				queryFormPost("tLayoutCreadosCadenasPDelete", {	async : false});
				
				}
			
			
			}	
		}
	} 	
} 
 
 
					
		/* var table = document.getElementById('dt_CuentaConLayout');
		var rowCount = table.rows.length;
		
		for ( var i = 1; i < rowCount; i++) {
			
			var row = table.rows[i];
			var checado =  row.cells[0].childNodes[0].name;
			var UnidadEjecutora = row.cells[1].childNodes[0].data;
			var Folio = row.cells[2].childNodes[0].data;
			var RFC = row.cells[3].childNodes[0].data;
			var Monto = row.cells[4].childNodes[0].data;
			var CuentaB = row.cells[5].childNodes[0].data;
			var concepto = row.cells[6].childNodes[0].data;
			var FechaProgramada = row.cells[7].childNodes[0].data;
			var contrarecibo = row.cells[8].childNodes[0].data;
			var TipoDocto = row.cells[9].childNodes[0].data;
			
			$("#unidadEjec").val(UnidadEjecutora);
			$("#folio").val(Folio);
			$("#RFC").val(RFC);
			$("#Monto").val(Monto);
			$("#CuentaBancaria").val(CuentaB);
			$("#Concepto").val(concepto);
			$("#Fechaprog").val(FechaProgramada);
			$("#contrarecibo").val(contrarecibo);
			$("#tipoDoc").val(TipoDocto);
					
			if ( $("#"+checado).is(':checked')== true ){
			
			alert("hola");
			//queryFormPost("tLayoutCreadosCadenasPCreate", {	async : false});
				
			}
		*/	
  		
		</script>

  		<div id="container">
	  		<form name="ConLayout" id="ConLayout" action="../gstnmngr/CadenasPConLayout" method="post">
<!--	  			     <div>-->
<!--			    		<input type="hidden" id="contrarecibo" name="contrarecibo" />-->
<!--			    		-->
<!--			    	</div>-->
	            <div id="demo_jui">
	  				<table id="dt_CuentaConLayout" class="display">
						<thead>
							<tr align="center">
								<th>Seleccionar</th>
            					<th>Unidad Ejecutora</th>
            					<th>Folio</th>
            					<th>RFC</th>
            	    			<th align="right">Monto</th>
            	           		<th>Cuenta Bancaria</th>
            					<th >Concepto</th>
            	    			<th>Fecha Programada</th>
           						<th>Cuenta por Pagar</th>
            					<th>Tipo de Documento</th>
	            			</tr>
						</thead>
						<tbody />
					</table>
				</div>
			</form>
		</div>
