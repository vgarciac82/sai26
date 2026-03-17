    	
		<script type="text/javascript" src="js/jquery-1.6.2.min.js">
</script>
		<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js">
</script>
		<script type="text/javascript" src="js/jquery.dataTables.js">
</script>
		<script type="text/javascript"
			src="js/jquery.dataTables.editable-1.3.js">
</script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js">
</script>
		<script type="text/javascript" src="js/jquery.ui.datepicker.js">
</script>
		<script type="text/javascript" src="js/jquery.ui.core.js">
</script>
		<script type="text/javascript" src="js/jquery.ui.widget.js">
</script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js">
</script>
		<script type="text/javascript" src="js/jquery.formatCurrency.js">
</script>
		<script type="text/javascript" src="js/jquery.formatCurrency.all.js">
</script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js">
</script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js">
</script>
		<script type="text/javascript" src="js/crud.js">
</script>

		<script type="text/javascript" charset="utf-8">
$(document).ready(function(){ 
	querySelectPost("tCadenasPRead", "cIdMotivo", {async: false });
	
	$( "#cIdMotivo" ).change(function(){
		$("#cIdMotivo2").val($("#cIdMotivo").val());	
	});
	
	$( "#dialog-Motivo" ).dialog({
		
				autoOpen: false,
				height: 150,
				width: 650,
				modal: true,
				buttons: {
						"Aceptar": function() 
						{
							if ($("#cIdMotivo").val() == "- SELECCIONA MOTIVO -"){
								alert("Debe Seleccionar un Motivo");
								return;
							}
							if ($("#cIdMotivo").val() != "OMITE CADENA PRODUCTIVA"){
								queryFormPost("tCadenasPDetalleDelete", {	async : false});
								queryFormPost("tCadenasPDetalleCreate", {	async : false});
							}
							$("#cIdMotivo2").val( "" );
							$( this ).dialog( "close" );
						},
						
						"Cancelar": function() {
							
							$( "#" + $("#Name").val() ).attr('checked',true);
							$( this ).dialog( "close" );
						}
					},
				close: function() {										
				}							
				});

	
	$("#dt_generados").dataTable({
						bPaginate : false,			//muestra las flechas y pagina dependiendo el rango
						bLengthChange : false,  //
						bInfo : false,			//es el que muestra los numeros de los registros		
						sScrollX: "900px",
						sScrollY: "400px",
						bJQueryUI: true,  //se coloca el dise?o que contiene en css
						bFilter : false,
						bSort : false, // para colocar los filtros en los campos
						bDestroy: true,
						bRetrieve:true,
						bleft:true,
						bAutoWidth : true,
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
							oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }
						}
				});	
});		
				

	
	
	
//$("#cIdMotivo").hide();	
//});
    	
		function fnGrid(pParam, pFechaIni, pFechaFin){
			
  			var nRows = $("#dt_generados tr").length - 1;
			if (nRows > 0) {
				
				var DtCAdenas = $("#dt_generados").dataTable();
				var cuenta = DtCAdenas.fnSettings().aoData.length;
				
				DtCAdenas.fnClearTable();
			}

			var campos = " '"+ pParam+ "' , '" + pFechaIni + "' , '" + pFechaFin + "' "
			var elParametro2 = '';
			var szTabla = "CADENASP";
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla : szTabla,Param : elParametro2,Campos : campos,MaxReg : "",ajax : 'false'},function(j) {

					
				for ( var i = 0; i < j.length; i++) {
								
				$('#dt_generados').dataTable().fnAddData([ j[i].Col0, j[i].Col1,j[i].Col2,j[i].Col4,j[i].Col5,j[i].Col6,j[i].Col7,j[i].Col9,j[i].Col3,j[i].Col12 ]);			
				
				}
				
				$("#SumDoc").val(cuenta);	
			});
			
 }
			   	
function checado (name){	
					
	if ( $( "#"+name ).is(':checked') == false  ){

		var table = document.getElementById('dt_generados');
		var rowCount = table.rows.length;
		
		for ( var i = 1; i < rowCount; i++) {
			var row = table.rows[i];
			if(row.cells[2].childNodes[0].data == name){
				
				var SumDoc1 = rowCount-1;
				var RFC = row.cells[3].childNodes[0].data;
				var Monto = row.cells[4].childNodes[0].data;
				var contrarecibo = row.cells[8].childNodes[0].data;
			  
			    $("#Name").val(name);
				$("#RFC").val(RFC);
	  			$("#Monto").val(Monto);
				$("#contrarecibo").val(contrarecibo);
				$( "#dialog-Motivo" ).dialog( "open" );
			}
		
			
		}
	}else{
		if(confirm ("Desea habilitar la Cuenta por Pagar ")){
			
			
			var table = document.getElementById('dt_generados');
			var rowCount = table.rows.length;
		
			for ( var i = 1; i < rowCount; i++) {
				var row = table.rows[i];
				
				if(row.cells[2].childNodes[0].data == name){
			
					var RFC = row.cells[3].childNodes[0].data;
					var Monto = row.cells[4].childNodes[0].data;
					var contrarecibo = row.cells[8].childNodes[0].data;
			  
			   	 	$("#Name").val(name);
					$("#RFC").val(RFC);
	  				$("#Monto").val(Monto);
					$("#contrarecibo").val(contrarecibo);
				}
				
			}
			queryFormPost("tCadenasPDetalleDelete", {	async : false});
			
		}else{
			
			$( "#" + name).attr('checked', false);
			
		}
		
		
		
	}
	 
}   	
  function desmarkar(){  
    	
    	if ($('#Desmar').val() == "Desmarcar" ){
			
    		if(confirm ("Los archivos se guardaran en Base de Datos Con el Motivo DESCARTAR ")){
				
				var table = document.getElementById('dt_generados');
				var rowCount = table.rows.length;
		
				for ( var i = 1; i < rowCount; i++) {
					
					var row = table.rows[i];
					var RFC = row.cells[3].childNodes[0].data;
					var Monto = row.cells[4].childNodes[0].data;
					var contrarecibo = row.cells[8].childNodes[0].data;
			  
				    $("#Name").val(name);
					$("#RFC").val(RFC);
		  			$("#Monto").val(Monto);
					$("#contrarecibo").val(contrarecibo);
					queryFormPost("tCadenasPDetalleDelete", {	async : false});
					queryFormPost("tCadenasPDetalleCreate", {	async : false});
					}	
				$(".desmarcar").attr('checked', false);
				$('#Desmar').val("Marcar");
			}
   		}else{
   				var table = document.getElementById('dt_generados');
				var rowCount = table.rows.length;
		
				for ( var i = 1; i < rowCount; i++) {
					
					var row = table.rows[i]
					var RFC = row.cells[3].childNodes[0].data;
					var Monto = row.cells[4].childNodes[0].data;
					var contrarecibo = row.cells[8].childNodes[0].data;
			  
				    $("#Name").val(name);
					$("#RFC").val(RFC);
		  			$("#Monto").val(Monto);
					$("#contrarecibo").val(contrarecibo);
					queryFormPost("tCadenasPDetalleDelete", {	async : false});
					}	
   			$(".desmarcar").attr('checked', true);
   			$('#Desmar').val("Desmarcar");
   		}
    }
  
function guardartbLayout (){	
					
		var table = document.getElementById('dt_generados');
		var rowCount = table.rows.length;
		var oTable = $('#dt_generados').dataTable();
		var data = $('#dt_generados').dataTable().fnGetNodes();
	
		for ( var i = 1; i < rowCount; i++) {
			if ( $('input', data[i-1] )[0].checked ){
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
						
				queryFormPost("tLayoutCreadosCadenasPCreate", {	async : false});
			}		
			
	
		
	}
}	
//function insertar(){
	
//queryFormPost("tCadenasPDetalleCreate", {	async : false});
	
//}
		
	
				
		</script>
<form>
			
	
	<input type="hidden" id="Name" name="Name" />
	<input type="hidden" id="unidadEjec" name="unidadEjec" />
	<input type="hidden" id="folio" name="folio" />
	<input type="hidden" id="RFC" name="RFC" />
	<input type="hidden" id="Monto" name="Monto" />
	<input type="hidden" id="CuentaBancaria" name="CuentaBancaria" />
	<input type="hidden" id="Concepto" name="Concepto" />
	<input type="hidden" id="Fechaprog" name="Fechaprog" />
	<input type="hidden" id="contrarecibo" name="contrarecibo" />
	<input type="hidden" id="tipoDoc" name="tipoDoc" />
	<input type="hidden" id="cIdMotivo2" name="cIdMotivo2" />

  		<div id="container">
            <div id="demo_jui">
  				<table id="dt_generados" class="display" >
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
		</div>
		
		<div id="dialog-Motivo" title="Motivo">
		 		<select id="cIdMotivo"  name="cIdMotivo" style="width: 600px;" ></select>         
		</div>
	
</form>			
			