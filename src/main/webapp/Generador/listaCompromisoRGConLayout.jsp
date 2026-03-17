<script type="text/javascript">
	var oTableCompRGLayout;
	var seleccionado = -1;
	
	function generaLayoutIntCompromiso(){

		if(seleccionado<0){
			Swal.fire({ icon: "warning",
						text: "Debe seleccionar un renglon para generar su Layout"});
			return false;
		}
				
		var selData = oTableCompRGLayout.fnGetData(seleccionado);
		var caNoCompromiso = selData[6];
		$("#caNoCompromiso").val(caNoCompromiso);
		$("#RGCompromisoLayout").submit();
		
	}	
		
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
						    Swal.fire({ icon: "succes",
										text: "Compromiso actualizado exitosamente. Ahora puede generar el Layout para SICOP"});
							valFecha2( $("#FechaInicial2")[0] );
							$.unblockUI();
							limpiaValores();	
									
						}
					});
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
						
		queryFormPost("esIntegradaLayoutRGActiva_Read", {async:false});
		queryFormPost("esConsolidacionRGAplicada_Read", {async:false});
		
		var esLayoutRGActiva = $("#esLayoutRGActiva").val();
		var esConsolidaAplicada = $("#esConsolidaAplicada").val();
		var uLogin = $("#u_Login").val();
		
		if((esLayoutRGActiva == "1" || esConsolidaAplicada == "0")){			
			Swal.fire({ icon: "warning",
						text: "La integrada no esta Activa o ya se encuentra aplicada, verifique!"});
			bReturn = false;
			return;
		}
		
		return bReturn;
	}
	
	function fnGridCompromisoLayout(pParam, pFechaIni, pFechaFin) {
		seleccionado = -1;
		
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
				sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=v_LayoutsCreadosCompromisoRG&qw=" + ( pParam == 0 ? "" : "cUnidadEjecutora='" + pParam + "' AND " ) + "fechaProgramadaAutorizacion BETWEEN convert(date,'" + pFechaIni + "',103) AND convert(date,'" + pFechaFin + "',103)",
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

		$("#dt_CompRGLayout tbody").click(function(event) {
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
		
		$("#dt_CompRGLayout tbody").dblclick(function(event) {
		
			limpiaValores();
			var row = oTableCompRGLayout.fnGetPosition(event.target.parentNode);
			var data = oTableCompRGLayout.fnGetData(row);
			
			var integracion = data[7];
			var compromiso = data[6];
			
			$("#integracion").val(integracion);
			$("#caNoCompromisoTxt").val(compromiso);
			$("#caNoCompromisoSnd").val(compromiso);
			
			
			//$("#dialog-CapturaFolio").dialog("open");
			modalFolioSICOP.show();
		});

	}
	
	function reimprimeLayoutCompromiso(){
		if(seleccionado<0){
			Swal.fire({ icon: "warning",
						text: "Debe seleccionar un renglon para generar su Layout"});
			return false;
		}
				
		var selData = oTableCompRGLayout.fnGetData(seleccionado);
		var caNoCompromiso = selData[6];
		var url = "../gstnmngr/generaLayoutPDCompromiso?tipoLayout=3&caNoCompromiso="+caNoCompromiso;
		var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=100, height=100");
		/*
		try {
    		var table2 = document.getElementById('dt_paraEnvio');
        	var rowCount2 = table2.rows.length;
        	
        	if (rowCount2 == 0)
        	{
        		Swal.fire({ icon: "warning",
							text: "Debe seleccionar al menos un pago"});            		
        		return;
        	}
        	
        	document.getElementById('archivo').value = "1";
        	document.RGCompromisoLayout.action='../gstnmngr/generaLayoutPDCompromiso';
			document.RGCompromisoLayout.method='GET';
        	document.RGCompromisoLayout.submit();
			
        	fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val().split('-').reverse().join('/'), $("#FechaFinal").val().split('-').reverse().join('/'), tipoLayout);
        	
        	fnClickDellRows();
        	
        	$("#pbEnvia").css("visibility","hidden");
        	$("#pbCompromiso").css("visibility","hidden");
     	
			} catch(e) {
				Swal.fire({ icon: "error", text: e});
				
     	}
			*/
	}
</script>


	<form name="RGCompromisoLayout" id="RGCompromisoLayout" action="../gstnmngr/generaLayoutRelacionGastosCompromiso" method="post">
		<input type="hidden" id="caNoCompromisoSnd"name="caNoCompromisoSnd" value="">
		<input type="hidden" id="NoFolioSICOPSnd"name="NoFolioSICOPSnd" value="">
		<input type="hidden" id="sAuxiliarComodin"name="sAuxiliarComodin" value="">
		<input type="hidden" id="tipoLayout"name="tipoLayout" value="2">
		<input type="hidden" id="caNoCompromiso"name="caNoCompromiso" value="">
		<input type="hidden" id="folioSicop"name="folioSicop" value="">
		<input type="hidden" id="esLayoutRGActiva"name="esLayoutRGActiva" value="0">
		<input type="hidden" id="esConsolidaAplicada"name="esConsolidaAplicada" value="0">
		
		<div class="row d-flex">								
			<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
	  			<div class="table-responsive">	    				           
		  			<table id="dt_CompRGLayout" class="table table-striped table-bordered" >					
						<thead>
							<tr align="center">
								<th>Unidad Ejecutora</th>
								<th>Folio</th>
								<th>Cuenta Bancaria</th>
								<th>Concepto</th>
								<th>Fecha Programada</th>
								<th>Leyenda</th>
								<th>CxP</th>
								<th>#Integraci�n</th>
								<th>#Aut. SICOP</th>
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
				<button type="button" id="aceptarFolioSICOP" class="btn btn-primary btn-sm" onclick="actualizaFolioSICOP();" >Aceptar</button>
				<button type="button" class="btn btn-secondary btn-sm" onclick="limpiaValores();" data-bs-dismiss="modal">Cancelar</button>										    
			</div>
		</div>
	</div>		
</div>
<!-- 
<div id="dialog-CapturaFolio" title="Ingrese informacion">
	<table id="tblCaptura">
		<tr>
			<td align="right">Integracion:</td>
			<td align="left"><input type="text" id="integracion" value="" readonly class="lectura" style="background: #CCCCCC"/></td>
		</tr>
		<tr>
			<td align="right">Compromiso:</td>
			<td align="left"><input type="text" id="caNoCompromisoTxt" name="caNoCompromisoTxt" value="" style="background: #CCCCCC"/></td>
		</tr>
				<tr>
			<td align="right">#Folio SICOP:</td>
			<td align="left"><input type="text" id="NoFolioSICOP" name="NoFolioSICOP" value=""  /></td>
		</tr>
	</table>

 -->