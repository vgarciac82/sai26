<script type="text/javascript">
	var oTableCompLayout;
	var seleccionado = -1;
	
	
		
	function actualizaFolioSICOP(){
	
		if(validaUpdateFolioSICOP()){		
			if( validaEnvio() && confirm("Esta seguro que desea actualizar el compromiso " + $("#caNoCompromisoSnd").val() + " con el folio de SICOP " + $("#NoFolioSICOP").val() + "?") ){
				$.blockUI({message: "Procesando espere ......"});
				$("#NoFolioSICOPSnd").val($("#NoFolioSICOP").val() );
				try{
					queryFormPost({
						queryName:"numSICOPCompUpdate",
						async:false,
						callback:function(){
							modalFolioSICOP.hide();
						    Swal.fire({ icon: "success",
										text: "Compromiso actualizado exitosamente. Ahora puede generar el Layout para SICOP"});
							valFecha2( $("#FechaInicial2")[0] );
							$.unblockUI();
							limpiaValores();	
									
						}
					});
					fnGridCompromisoLayout();
					$.unblockUI();
				}catch(e){
					Swal.fire({ icon: "error",
								text: e});
					$.unblockUI();
				}
				
				modalFolioSICOP.hide();
			}
		}
	}
	
	function limpiaValores(){
		$("#integracion").val("");
		$("#caNoCompromisoTxt").val("");
		$("#caNoCompromisoSnd").val("");
		
		$("#NoFolioSICOP").val("");
		$("#NoFolioSICOPSnd").val("");
	}
	
	function validaEnvio(){
		if( $.trim( $("#NoFolioSICOP").val() ) == ""){			
			Swal.fire({ icon: "warning",
						text: "El Num. de Folio SICOP es requerido"});
			return false;
		}else if( $("#caNoCompromisoSnd").val() == "" ){			
			Swal.fire({ icon: "warning",
						text: "No se encontro folio de compromiso. Intente nuevamente"});
			return false;
		}
		
		return true;
	}
	
	function validaUpdateFolioSICOP(){
		var bReturn = true;
		$("#sAuxiliarComodin").val("");
		$("#sAuxiliarComodin").val($("#integracion").val());
						
		queryFormPost("pagoDirectoConLayout_Read", {async:false});
		
		var tieneLayout = $("#tieneLayout").val();
		var uLogin = $("#u_Login").val();
		
		if((tieneLayout == "0" )){			
			Swal.fire({ icon: "warning",
						text: "Su usuario no tiene permisos para Actualizar el Folio SICOP."});
			bReturn = false;
			return;
		}
		
		return bReturn;
	}
	
	function fnGridCompromisoLayout() {
		seleccionado = -1;
		
		oTableCompLayout = $('#dt_CompLayout').dataTable(
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
				 "aaSorting": [[2, "asc"]],
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
				sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=v_LayoutsCreadosCompromisoPD" ,
				aoColumns : [
					{
						sName : "id",
						bSearchable : false,
						bSortable : false,
						bVisible : true
					},
					{
						sName : "cUnidadEjecutora",
						bSearchable : false,
						bSortable : false,
						bVisible : true
					},
					{
						sName : "Folio",
						bSearchable : true,
						bSortable : true,
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
						bSearchable : true,
						bSortable : true,
						bVisible : true
					},
					{
						sName : "integracion",
						bSearchable : true,
						bSortable : true,
						bVisible : true
					},
					{
						sName : "NoAutorizacionSICOP",
						bSearchable : false,
						bSortable : false,
						bVisible : true
					},
					{
						sName : "nFolioPagoDirecto",
						bSearchable : false,
						bSortable : false,
						bVisible : true
					}
				]
			});

		$("#dt_CompLayout tbody").click(function(event) {
			$(oTableCompLayout.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('table-primary');
			});
			$(event.target.parentNode).addClass('table-primary');
			
			var row = oTableCompLayout.fnGetPosition(event.target.parentNode);
			if (row instanceof Array)
				var currIndex = row[0];
			else
				var currIndex = row;

			var data = oTableCompLayout.fnGetData(currIndex);
			var nFolioSICOP = data[8]; 
			if( !$.trim(nFolioSICOP) == "" )
				$("#generaLayoutPCompromiso").css("visibility","visible");
			else
				$("#generaLayoutPCompromiso").css("visibility","hidden");
				
			//document.getElementById('sDataH').value = data[10] ;
			document.getElementById('sDataHCB').value = data[3];
			document.getElementById('sDataHFecha').value = data[5] ;
			document.getElementById('sDataHLeyenda').value = "1" ;
			seleccionado = currIndex;
		});
		
		$("#dt_CompLayout tbody").dblclick(function(event) {
		
			limpiaValores();
			var row = oTableCompLayout.fnGetPosition(event.target.parentNode);
			var data = oTableCompLayout.fnGetData(row);
			
			var compromiso = data[7];
			var folioSicop = data[8];
			
			$("#integracion").val(compromiso);
			$("#caNoCompromisoTxt").val(compromiso);
			$("#caNoCompromisoSnd").val(compromiso);
			$("#NoFolioSICOP").val(folioSicop);
			
			modalFolioSICOP.show();
		});

	}
	
	function reimprimeLayoutCompromiso(){
		if(seleccionado<0){
			Swal.fire({ icon: "warning",
						text: "Debe seleccionar un renglon para generar su Layout"});
			return false;
		}
				
		var selData = oTableCompLayout.fnGetData(seleccionado);
		var caNoCompromiso = selData[7];
		var url = "../gstnmngr/generaLayoutPDCompromiso?archivo=1&caNoCompromiso="+caNoCompromiso;
		var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=100, height=100");
		
	}
</script>

<div id="container" class="ms-5" class="container" style="width: 90%">
	<form name="PDCompromisoLayout" id="PDCompromisoLayout" action="../gstnmngr/generaLayoutPagosDirectos" method="get">
		<input type="hidden" id="caNoCompromisoSnd"name="caNoCompromisoSnd" value="">
		<input type="hidden" id="NoFolioSICOPSnd"name="NoFolioSICOPSnd" value="">
		<input type="hidden" id="sAuxiliarComodin"name="sAuxiliarComodin" value="">
		<input type="hidden" id="tipoLayout"name="tipoLayout" value="2">
		<input type="hidden" id="caNoCompromiso"name="caNoCompromiso" value="">
		<input type="hidden" id="folioSicop"name="folioSicop" value="">
		<input type="hidden" id="tieneLayout"name="tieneLayout" value="0">
		<input type="hidden" id="esConsolidaAplicada"name="esConsolidaAplicada" value="0">
		
		<div class="row d-flex">								
			<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
	  			<div class="table-responsive">	    				           
		  			<table id="dt_CompLayout" class="table table-striped table-bordered" >					
						<thead>
							<tr align="center">
								<th></th>
								<th>Unidad Ejecutora</th>
								<th>#Compromiso</th>
								<th>Cuenta Bancaria</th>
								<th>Concepto</th>
								<th>Fecha Programada</th>
								<th>Leyenda</th>
								<th>CxP</th>
								<th>#Integración</th>
								<th>#Aut. SICOP</th>
								<th>FolioPD</th>
							</tr>
						</thead>
						<tbody />
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
				<h5 class="modal-title">Ingrese informacion Compromiso</h5>
				<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
			  </div>
			<div class="modal-body"> <!-- Cuerpo de la caja -->
				<div class="row d-flex">								
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">					
						<label for="integracion" class="form-label"> Integracion: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="integracion" name="integracion" value="" readonly/>
			        </div>						
				</div>		
				<div class="row d-flex">								
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">					
						<label for="caNoCompromisoTxt" class="form-label"> Compromiso: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="caNoCompromisoTxt" name="caNoCompromisoTxt" value="" readonly/>
			        </div>						
				</div>	
				<div class="row d-flex">								
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">					
						<label for="NoFolioSICOP" class="form-label"> #Folio SICOP: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="NoFolioSICOP" name="NoFolioSICOP" value=""/>
			        </div>						
				</div>				
			</div>
			<div class="modal-footer"> <!-- Pie de pagina de la caja -->
				<button type="button" id="aceptarFolioSICOP" class="btn btn-primary" onclick="actualizaFolioSICOP();" >Aceptar</button>
				<button type="button" class="btn btn-secondary" onclick="limpiaValores();" data-bs-dismiss="modal">Cancelar</button>										    
			</div>
		</div>
	</div>		
</div>
