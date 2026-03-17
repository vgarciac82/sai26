<script type="text/javascript">
	var oTableCompRGLayout;
	var seleccionado = -1;
	var fSICOP = "";
	
	function actualizaFolioSICOP(){
		if( validaEnvio() ){
			Swal.fire({
				  title: "¿Desea continuar?",
				  text: "Esta seguro que desea actualizar el compromiso " + $("#caNoCompromisoSnd").val() + " con el Num. Documento de SICOP " + $("#NoFolioSICOP").val() + "?",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					  $.blockUI({message: "Procesando espere ......"});
						$("#NoFolioSICOPSnd").val($("#NoFolioSICOP").val() );
						$("#caNoCompromiso").val($("#caNoCompromisoTxt").val() );
						$("#fAplSICOP").val($("#fSICOP").val() );
						
						$("#compromisoFed").submit();
				  } 
				})
			//confirm("Esta seguro que desea actualizar el compromiso " + $("#caNoCompromisoSnd").val() + " con el Num. Documento de SICOP " + $("#NoFolioSICOP").val() + "?") ){			
		}	
	}
	
	
	function limpiaValores(){
		$("#cIdContrato").val("");
		$("#caNoCompromisoTxt").val("");
		$("#caNoCompromisoSnd").val("");
		
		$("#NoFolioSICOP").val("");
		$("#NoFolioSICOPSnd").val("");
				
		$("#fSICOP").val("");
		$("#fAplSICOP").val("");
		
		modalFolio.hide();
			
	}
	
	function validaEnvio(){
		if( isEmpty("NoFolioSICOP")){
			Swal.fire({ icon: "warning",
		  				text: "El Num. de Folio SICOP es requerido"});			
			return false;
		}else if( isEmpty("fSICOP")){
			Swal.fire({ icon: "warning",
  						text: "La Fecha de Aplicacion SICOP es requerida"});			
			return false;
		}
		
		return true;
	}
	
	function isEmpty(idCampo){
        var val = $("#"+idCampo).val();
        val = val.replace(/\s/g, "" );
        
        if( val == "" )
               return true;
        else 
               return false;
    }
	
	function fnGridCompromisosFederalizados(pParam, pFechaIni, pFechaFin) {
		seleccionado = -1;
		
		fSICOP = pFechaFin;
		
		if( pParam == 'RHQ' )
			pParam = 0 ;

		oTableCompRGLayout = $('#dt_CompRGLayout').dataTable(
			{
				"bLengthChange" : true,
 	            "bFilter" : true,
 	            "bSort" : true,
 	            "bInfo" : true,
 	            "bPaginate" : true,
 	            "bAutoWidth" : false,
 	            "bScrollCollapse" : true,
 	            "sScrollXInner": "100%",      	    		
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
				sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=v_ListaCompromisosFederalizados&qw=" + ( pParam == 0 ? "" : "cUnidadEjecutora='" + pParam + "' AND " ) + "fCarga BETWEEN convert(datetime,'" + pFechaIni + " 00:00',103) AND convert(datetime,'" + pFechaFin + " 23:59:59',103)",
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
						sName : "cIdContrato",
						bSearchable : false,
						bSortable : false,
						bVisible : true
					},
					{
						sName : "caNoCompromiso",
						bSearchable : false,
						bSortable : false,
						bVisible : true
					},
					{
						sName : "fCarga",
						bSearchable : false,
						bSortable : false,
						bVisible : true
					},
					{
						sName : "cDescripcionPoliza",
						bSearchable : false,
						bSortable : false,
						bVisible : true
					}
				]
			});

		
		
		$("#dt_CompRGLayout tbody").dblclick(function(event) {
		
			limpiaValores();
			var row = oTableCompRGLayout.fnGetPosition(event.target.parentNode);
			var data = oTableCompRGLayout.fnGetData(row);
			
			var cIdContrato = data[2];
			var compromiso = data[3];
			
			$("#fSICOP").val(moment().format('yyyy-MM-DD'));
			
			$("#cIdContrato").val(cIdContrato);
			$("#caNoCompromisoTxt").val(compromiso);
			$("#caNoCompromisoSnd").val(compromiso);	
			
			modalFolio.show();
		});

	}
	
	function guardarFolio(){
		actualizaFolioSICOP();
		modalFolio.hide();
	}
	
</script>

	<div id="container" class="ms-5" class="container" style="width: 90%">
		<input type="hidden" id="caNoCompromisoSnd"name="caNoCompromisoSnd" value="">
		<input type="hidden" id="NoFolioSICOPSnd"name="NoFolioSICOPSnd" value="">
		<input type="hidden" id="tipoLayout"name="tipoLayout" value="4">
		<input type="hidden" id="caNoCompromiso"name="caNoCompromiso" value="">
		<input type="hidden" id="cFolioSICOP"name="cFolioSICOP" value="">
		<input type="hidden" id="cIdProceso"name="cIdProceso" value="">
		<input type="hidden" id="cTipoSolicitud"name="cTipoSolicitud" value="">
		<input type="hidden" id="fAplSICOP"name="fAplSICOP" value="">
				
		<div class="row d-flex">								
			<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
	  			<div class="table-responsive">	    				           
		  			<table id="dt_CompRGLayout" class="table table-striped table-bordered" > 			
						<thead>
							<tr>
								<th>Unidad Ejecutora</th>
								<th>Folio</th>
								<th>Contrato</th>
								<th>Compromiso</th>
								<th>Fecha Compromiso</th>
								<th>Descripción Compromiso</th>	
							</tr>					
					</table>
				</div>
			</div>
		</div>
		
	</div>
	
	<div class="modal fade" id="dialog-CapturaFolio" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
		<div class="modal-dialog"> <!-- Caja de dialogo -->
	    	<div class="modal-content"> <!-- Contenido de la caja -->
	      		<div class="modal-header"> <!-- Encabezado de la caja -->
	        		<h5 class="modal-title">Ingrese informacion</h5>
	        		<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
	      		</div>
	      	<div class="modal-body"> <!-- Cuerpo de la caja -->
		        <div class="row d-flex">								
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<label for="cIdContrato" class="form-label"> Contrato: </label>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">	
						<input type="text" name="cIdContrato" id="cIdContrato" class="form-control form-control-sm" readonly/>
					</div>
				</div>
				
				<div class="row d-flex">								
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<label for="caNoCompromisoTxt" class="form-label"> Compromiso: </label>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">	
						<input type="text" name="caNoCompromisoTxt" id="caNoCompromisoTxt" class="form-control form-control-sm" readonly/>
					</div>
				</div>
				
				<div class="row d-flex">								
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<label for="NoFolioSICOP" class="form-label"> #Folio SICOP: </label>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">	
						<input type="text" name="NoFolioSICOP" id="NoFolioSICOP" value="" class="form-control form-control-sm"/>
					</div>
				</div>
				
				<div class="row d-flex">								
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<label for="fSICOP" class="form-label"> Aplicacion SICOP: </label>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">	
						<div class="input-group">
							<span class="input-group date"><i class="datepicker1"></i></span>
							<input onchange="valFecha(this)" name="fSICOP" type="date" id="fSICOP" class="form-control form-control-sm" size="10" />
						</div>	
					</div>
				</div>
	      	</div>
			<div class="modal-footer"> <!-- Pie de pagina de la caja -->
				<button type="button" id="aceptarFecha" class="btn btn-primary btn-sm" onclick="guardarFolio();" >Aceptar</button>
				<button type="button" class="btn btn-secondary btn-sm" onclick="limpiaValores();" data-bs-dismiss="modal">Cancelar</button>										    
		    </div>
	    </div>
	  </div>
	</div>
