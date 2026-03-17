
    	<script type="text/javascript">
    	


$(document).ready(function () {

$( "#dialog-Reporte" ).dialog({
		
				autoOpen: false,
				height: 250,
				width: 800,
				modal: true,
					buttons: {
						"Aceptar": function(){
	
						
							$( this ).dialog( "close" );
						},
						
						"Cancelar": function() {
							
							
							$( this ).dialog( "close" );
						}
					},
				close: function() {										
				}							
});

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
						//{ sName: "LEYENDA",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
						//{ sName: "TIPO_DOCTO",				bSearchable: false,	bSortable: false, bVisible: false},
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


function dialogo(){  
var table = document.getElementById('dt_paraEnvio');
var rowCount = table.rows.length;

	for ( var i = 1; i < rowCount; i++) {
		
		var row = table.rows[i]
		var cuenta =rowCount;
	 	
	    
					
	}
	$("#totalDoc").val(rowCount-1);
	//$("#totalDoc").val();	
	//$("#ImpMin").val(ImporteMin);
}

function quitaFmt( val ) {
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");
		
	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val
	}	
    

</script>

  		<div id="container">
  			<form name="envioSICOP" id="envioSICOP" action="../gstnmngr/generaLayoutCadenasP" method="post">
				<div>
					<input type="hidden" id="archivo" name="archivo" />					
		    		<input type="hidden" id="sDataH" name="sDataH" />
		    		
		    		<input type="hidden" id="sDataHCB" name="sDataHCB" />
		    		<input type="hidden" id="sDataHFecha" name="sDataHFecha" />
<%--		    		<input type="hidden" id="sDataHLeyenda" name="sDataHLeyenda" />--%>
		    		<input type="hidden" id="sDataHcontrarecibo" name="sDataHcontrarecibo" />
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
<%--	            	    		<th>Leyenda</th>--%>
	            	    		
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
				<div id="dialog-Reporte" title="Reporte">
				<table align="Center" border="0">
					<tr >
						<td colspan="3" align="left"> Total de Documentos Pendientes: 		
				 		<input style="text-align: right; background-color:#F5F5F5; " type="text" id="SumDoc" name="SumDoc" size="3" readonly="readonly"/>
				 		</td>
			 		</tr>
			 		<tr>
				 		<td colspan="3" align="left"> Total de Documentos en Proceso:		
				 		<input style="text-align: right; background-color:#F5F5F5; " type="text" id="totalDoc" name="totalDoc" size="3" readonly="readonly"/>
				 		</td>
			 		</tr>
			 		<tr>
				 		<td align="right"> Importe Minimo:		
				 		<input style="text-align: right; background-color:#F5F5F5; " type="text" id="ImpMin" name="ImpMin" size="15" readonly="readonly"/>
				 		<td align="right"> Unidad Ejecutora:
				 		<input style="text-align: right; background-color:#F5F5F5; " type="text" id="Unidad" name="Unidad" size="3" readonly="readonly"/>
				 		<td align="right"> Cuenta por Pagar:
				 		<input style="text-align: right; background-color:#F5F5F5; " type="text" id="CxP" name="CxP" size="17" readonly="readonly"/>
				 		</td>
			 		</tr>
			 		<tr>
				 		<td align="right"> Importe Maximo:		
				 		<input style="text-align: right; background-color:#F5F5F5; " type="text" id="ImpMax" name="ImpMax" size="15" readonly="readonly"/>
				 		<td align="right"> Unidad Ejecutora:
				 		<input style="text-align: right; background-color:#F5F5F5; " type="text" id="Unidad2" name="Unidad2" size="3" readonly="readonly"/>
				 		<td align="right"> Cuenta por Pagar:
				 		<input style="text-align: right; background-color:#F5F5F5; " type="text" id="CxP2" name="CxP2" size="17" readonly="readonly"/>
				 		</td>
			 		</tr>
			 		<tr>
				 		<td colspan="3" align="left"> Suma de Documentos en Proceso:		
				 		<input style="text-align: right; background-color:#F5F5F5; " type="text" id="SumaDoc" name="SumaDoc" size="15" readonly="readonly"/>
				 		</td>
			 		</tr>
		 		</table>
			</div>
			</form>
		</div>
