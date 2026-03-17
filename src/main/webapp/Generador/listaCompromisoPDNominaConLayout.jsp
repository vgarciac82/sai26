<script type="text/javascript">
	var oTableCompRGLayout;
	var seleccionado = -1;
	$(document).ready(function () {
		
	});
	
	function generaLayoutIntCompromiso(){

		if(seleccionado<0){
			Swal.fire({ icon: "warning",
						text: "Debe seleccionar un renglon para generar su Layout."});			
			return false;
		}
				
		var selData = oTableCompRGLayout.fnGetData(seleccionado);
		var caNoCompromiso = selData[6];
		$("#caNoCompromiso").val(caNoCompromiso);
		$("#PDNominaCompromisoLayout").submit();
		
	}	
		
	function actualizaFolioSICOP(){
	
		//if(validaUpdateFolioSICOP()){		
			if( validaEnvio() && confirm("Esta seguro que desea actualizar el compromiso " + $("#caNoCompromisoSnd").val() + " con el folio de SICOP " + $("#NoFolioSICOP").val() + "?") ){				
				$("#NoFolioSICOPSnd").val($("#NoFolioSICOP").val() );
				try{
					queryFormPost({
						queryName:"numSICOPCompUpdate",
						async:false,
						callback:function(){
						    //$("#dialog-CapturaFolio").dialog("close");
						    Swal.fire({ icon: "success",
										text: "Compromiso actualizado exitosamente. Ahora puede generar el Layout para SICOP."});
							valFecha2( $("#FechaInicial2")[0] );							
							limpiaValores();	
									
						}
					});
					modalFolioSicop.hide();
				}catch(e){
					Swal.fire({ icon: "error",
								text: e});					
			
				}
			}
		//}
	}
	
	function limpiaValores(){
		$("#integracion").val("");
		$("#caNoCompromisoTxt").val("");
		$("#caNoCompromisoSnd").val("");
		
		$("#NoFolioSICOP").val("");
		$("#NoFolioSICOPSnd").val("");
		modalFolioSicop.hide();
	}
	
	function validaEnvio(){
		if( $.trim( $("#NoFolioSICOP").val() ) == ""){
			Swal.fire({ icon: "warning",
						text: "El Num. de Folio SICOP es requerido."});			
			return false;
		}else if( $("#caNoCompromisoSnd").val() == "" ){
			Swal.fire({ icon: "warning",
						text: "No se encontro folio de compromiso. Intente nuevamente."});			
			return false;
		}
		
		return true;
	}
	
	function validaUpdateFolioSICOP(){
		var bReturn = true;
		$("#sAuxiliarComodin").val("");
		$("#sAuxiliarComodin").val($("#integracion").val());
						
		queryFormPost("esIntegradaLayoutRGActiva_Read", {async:false});
		queryFormPost("esConsolidacionRGAplicada_Read", {async:false});
		
		var esLayoutRGActiva = $("#esLayoutRGActiva").val();
		var esConsolidaAplicada = $("#esConsolidaAplicada").val();
		var uLogin = $("#u_Login").val();
		
		if((esLayoutRGActiva == "0" || esConsolidaAplicada == "0")){
			Swal.fire({ icon: "warning",
						text: "Su usuario no tiene permisos para Actualizar el Folio SICOP."});			
			bReturn = false;
			return;
		}
		
		return bReturn;
	}
	
	function fnGridCompromisoLayout(pParam, pFechaIni, pFechaFin) {
		seleccionado = -1;
		
		if( pParam == 'RHQ' )
			pParam = 0 ;

		oTableCompRGLayout = $('#dt_CompPNNominaLayout').dataTable(
			{
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
				oLanguage : {
					sProcessing : "Procesando...",
					sLengthMenu : "Mostrar _MENU_ registros",
					sZeroRecords : "No hay registros a mostrar",
					sEmptyTable : "No hay datos en la tabla",
					sLoadingRecords : "Cargando...",
					sInfo : "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty : "Registro 0 al 0 de 0",
					sInfoFiltered : "(filtered from _MAX_ total entries)",
					sInfoPostFix : "",
					sInfoThousands : ",",
					sSearch : "Buscar:",
					oPaginate : {
						sFirst : "Primero",
						sPrevious : "Ant.",
						sNext : "Sigte.",
						sLast : "&Uacute;ltimo"
					}
				},
				bServerSide : true,
				sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=v_LayoutsCreadosCompromiso_PDNomina&qw=" + ( pParam == 0 ? "" : "cUnidadEjecutora='" + pParam + "' AND " ) + "fechaProgramadaAutorizacion BETWEEN CAST('" + pFechaIni + "'as date) AND CAST('" + pFechaFin + "'as date)",
				bProcessing : true,
				sPaginationType : "full_numbers",
				bJQueryUI : true,
				aoColumns : [
					{
						sName : "cUnidadEjecutora",
						bSearchable : false,
						bSortable : false,
						bVisible : true
					},
					{
						sName : "Folio",
						bSearchable : false,
						bSortable : false,
						bVisible : true
					},
					{
						sName : "cCuentaBancaria",
						bSearchable : false,
						bSortable : false,
						bVisible : true
					},
					{
						sName : "cConcepto",
						bSearchable : false,
						bSortable : false,
						bVisible : true
					},
					{
						sName : "fechaProgramadaAutorizacion",
						bSearchable : false,
						bSortable : false,
						bVisible : true
					},
					{
						sName : "strDescripcion",
						bSearchable : false,
						bSortable : false,
						bVisible : true
					},
					{
						sName : "canocontrarrecibo",
						bSearchable : false,
						bSortable : false,
						bVisible : true
					},
					{
						sName : "integracion",
						bSearchable : false,
						bSortable : false,
						bVisible : true
					},
					{
						sName : "NoAutorizacionSICOP",
						bSearchable : false,
						bSortable : false,
						bVisible : true
					}
				]
			});

		$("#dt_CompPNNominaLayout tbody").click(function(event) {
			$(oTableCompRGLayout.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('table-primary');
			});
			$(event.target.parentNode).addClass('table-primary');
			
			var row = oTableCompRGLayout.fnGetPosition(event.target.parentNode);
			if (row instanceof Array)
				var currIndex = row[0];
			else
				var currIndex = row;
			var data = oTableCompRGLayout.fnGetData(currIndex);
			var nFolioSICOP = data[8]; 
			if( !$.trim(nFolioSICOP) == "" )
				$("#generaLayoutIntBtn").css("visibility","visible");
			else
				$("#generaLayoutIntBtn").css("visibility","hidden");
				
			seleccionado = currIndex;
		});
		
		$("#dt_CompPNNominaLayout tbody").dblclick(function(event) {
			
			modalFolioSicop.show();
			limpiaValores();
			var row = oTableCompRGLayout.fnGetPosition(event.target.parentNode);
			var data = oTableCompRGLayout.fnGetData(row);
			
			var integracion = data[7];
			var compromiso = data[6];
			
			$("#integracion").val(integracion);
			$("#caNoCompromisoTxt").val(compromiso);
			$("#caNoCompromisoSnd").val(compromiso);			
			
		});

	}
	
	function reimprimeLayoutCompromiso(){
		if(seleccionado<0){
			Swal.fire({ icon: "warning",
						text: "Debe seleccionar un renglon para generar su Layout."});			
			return false;
		}
				
		var selData = oTableCompRGLayout.fnGetData(seleccionado);
		var caNoCompromiso = selData[6];
		var url = "../gstnmngr/generaLayoutPDNominaCompromiso?tipoLayout=3&caNoCompromiso="+caNoCompromiso;
		var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=100, height=100");
	}
</script>

<div id="container" class="ms-5" class="container" style="width: 90%">
	<form name="PDNominaCompromisoLayout" id="PDNominaCompromisoLayout" action="../gstnmngr/generaLayoutPDNominaCompromiso" method="post">
		<input type="hidden" id="caNoCompromisoSnd"name="caNoCompromisoSnd" value="">
		<input type="hidden" id="NoFolioSICOPSnd"name="NoFolioSICOPSnd" value="">
		<input type="hidden" id="sAuxiliarComodin"name="sAuxiliarComodin" value="">
		<input type="hidden" id="tipoLayout"name="tipoLayout" value="5">
		<input type="hidden" id="caNoCompromiso"name="caNoCompromiso" value="">
		<input type="hidden" id="folioSicop"name="folioSicop" value="">
		<input type="hidden" id="esLayoutRGActiva"name="esLayoutRGActiva" value="0">
		<input type="hidden" id="esConsolidaAplicada"name="esConsolidaAplicada" value="0">
		
		<div class="row d-flex">								
			<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
	  			<div class="table-responsive">	    				           
		  			<table id="dt_CompPNNominaLayout" class="table table-striped table-bordered" >				
						<thead>
							<tr>
								<th>Unidad Ejecutora</th>
								<th>Folio</th>
								<th>Cuenta Bancaria</th>
								<th>Concepto</th>
								<th>Fecha Programada</th>
								<th>Leyenda</th>
								<th>CxP COMP</th>
								<th>CxP</th>
								<th>#Aut. SICOP</th>
					</table>
				</div>
			</div>
		</div>
		
	</form>
</div>

<div class="modal fade" id="dialog-CapturaFolio" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
	<div class="modal-dialog"> <!-- Caja de dialogo -->
    	<div class="modal-content"> <!-- Contenido de la caja -->
      		<div class="modal-header"> <!-- Encabezado de la caja -->
        		<h5 class="modal-title">Ingrese Información</h5>
        		<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
      		</div>
      	<div class="modal-body"> <!-- Cuerpo de la caja -->
      		<div class="row d-flex">								
      			<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
      			</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="integracion" class="form-label"> CxP: </label>
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
					<input type="text" id="integracion" value="" class="form-control form-control-sm" readonly/>
				</div>
			</div>
			<div class="row d-flex">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
      			</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="caNoCompromisoTxt" class="form-label"> Compromiso: </label>
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
					<input type="text" id="caNoCompromisoTxt" value="" class="form-control form-control-sm" readonly/>
				</div>
			</div>
			<div class="row d-flex">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
      			</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="NoFolioSICOP" class="form-label"> #Folio SICOP: </label>
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
					<input type="text" id="NoFolioSICOP" value="" class="form-control form-control-sm"/>
				</div				
			</div>
      	</div>
		<div class="modal-footer"> <!-- Pie de pagina de la caja -->
			<button type="button" id="aceptarFecha" class="btn btn-primary btn-sm" onclick="actualizaFolioSICOP();" >Aceptar</button>
			<button type="button" class="btn btn-secondary btn-sm" onClick="limpiaValores();" data-bs-dismiss="modal">Cancelar</button>										    
	    </div>
    	</div>
	</div>
</div>
