<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String cUR = "";
	String cIniciaEstatus = "NO";

	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	cUR = usuario.getU_UR();

%>

    	<script type="text/javascript">
    		$(document).ready(function () {
    			var nFolioPago = 0;
    			
	
		var oTableLocal =	$( "#dialog-Rechazo" ).dialog({
						autoOpen: false,
						height: 300,
						width: 350,
						modal: true,
						buttons: {
							"Aceptar": function() {
											
							queryFormPost("tFederalizadoAUTDelete", {async: false });
							
							location.reload();
		
							$( this ).dialog( "close" );
							},
							"Cancelar": function() {
								
								$( this ).dialog( "close" );
							}
						},
						close: function() {										
						}
					});			
    				
					var cUR = '';
					if ('<%=cUR%>' != 'A02'){
						cUR = "&qw=cUnidadResponsable = '" + $( "#cIdUnidadEjecutora" ).val() + "'";
					}
			
			   creaTablaEnviados(cUR, UMA);
    				
				var oTableLocal =	$( "#dt_paraEnvio" ).dataTable();
				
				$("#dt_paraEnvio tbody").click(function(event) {
					$(oTableLocal.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
					//$("#pbEnvia").css("visibility","visible");
					//$("#pbEnviaDocumentacion").css("visibility","visible");
										
				});
				
				$('#pbRechazo')
					.button()
					.click( function() {
					var bSeleccionados = false;
					var nCuantos = 0;
					var caNoFolio = 0;
					var table = document.getElementById('dt_paraEnvio');
	 				var aTrs = $('#dt_paraEnvio').dataTable().fnGetNodes();
					var cCOLUMNALLAVE = 2;
	 				
	 				for ( var i=1; i<=aTrs.length;  i++ )    
					{         
						var row= table.rows[i];
						var chkbox = row.cells[0].childNodes[0];
						
						if(null != chkbox && true == chkbox.checked){
							nFolioPago = row.cells[cCOLUMNALLAVE].innerHTML;
							$("#nFolioPago").val( nFolioPago );
							$("#motivo").val( row.cells[ 12 ].innerHTML );
							bSeleccionados = true;
							nCuantos ++;
					    } 			
					}
	 				if (!bSeleccionados || nCuantos > 1)
					{
						alert("Debe marcar como seleccionado solo un renglón para Verificar Rechazo.");
						return;
					}
	 				if ($("#motivo").val() == ''){
	 					return
	 				}
	 				$( "#dialog-Rechazo" ).dialog( "open" );
					
				} );
    		});		
			function creaTablaEnviados(cUR, UMA) {
				
					var whereUMA = "";
					var token = "";
										
    				if(UMA == "0"){  // MONTO MAYOR A 300 UMAS
	    				whereUMA = " tipoUMA = 0 ";
	    			}else if(UMA == "1"){ // MONTO MENOR A 300 UMAS
	    				whereUMA = " tipoUMA = 1 ";
	    			}
					
    				var oTableEnviados = $('#dt_paraEnvio').dataTable({
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
						bServerSide: true,
						sAjaxSource: window.location.protocol + "//" + window.location.host + "/" 
						+ window.location.pathname.split("/")[1] 
						+ "/crud?rt=t&ql=vListaPAGOSFEDERALIZADOSAUT&qw=" + whereUMA,
						bProcessing: true,
						sPaginationType: "full_numbers",
						bJQueryUI: true,
						aoColumns: [
						    { sName: "id",						bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
							{ sName: "cUnidadResponsable",		bSearchable: false,	bSortable: false, bVisible: true},
							{ sName: "nFolioPAGOFEDERALIZADO",	bSearchable: false,	bSortable: false, bVisible: true},
							{ sName: "RFC",						bSearchable: false,	bSortable: false, bVisible: true},
							{ sName: "mImporteNeto",			bSearchable: false,	bSortable: false, bVisible: true},
							{ sName: "dCuentaBancaria",			bSearchable: false,	bSortable: false, bVisible: true},
							{ sName: "cConcepto",				bSearchable: false,	bSortable: false, bVisible: true},
							{ sName: "fProgramada",				bSearchable: false,	bSortable: false, bVisible: true},
							{ sName: "IdLeyenda",				bSearchable: false,	bSortable: false, bVisible: true},
							{ sName: "caNoContrarrecibo",		bSearchable: false,	bSortable: false, bVisible: true},
							{ sName: "U_LOGIN",					bSearchable: false,	bSortable: false, bVisible: true},
							{ sName: "cEstatus",				bSearchable: false,	bSortable: false, bVisible: true}, 
							{ sName: "dMotivoRechazo",			bSearchable: false,	bSortable: false, bVisible: true}]
	
	        		});
			}	
		</script>

  			<form name="envioSICOP" id="envioSICOP" action="../gstnmngr/generaLayoutPagosProgFederalizados" method="post"> 			
				<input type="hidden" id="nFolioPago" name="nFolioPago" />					
				<input type="hidden" id="archivo" name="archivo" />					
	    		<input type="hidden" id="sDataH" name="sDataH" />
	    		<input type="hidden" id="sDataHCB" name="sDataHCB" />
	    		<input type="hidden" id="sDataHFecha" name="sDataHFecha" />
	    		<input type="hidden" id="sDataHLeyenda" name="sDataHLeyenda" />
	    		<input type="hidden" id="sValorUMA" name="sValorUMA" />
	    	
  				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
			  			<div class="table-responsive">	    				           
				  			<table id="dt_paraEnvio" class="table table-striped table-bordered" >
								<thead>
									<tr align="center">
										<th>Seleccionar</th>
			            				<th>Unidad Ejecutora</th>
			            				<th>Folio</th>
			            				<th>RFC</th>
			            	    		<th>Monto</th>
			            	           	<th>Cuenta Bancaria</th>
			            				<th>Concepto</th>
			            	    		<th>Fecha Programada</th>
			            	    		<th>Leyenda</th>
			            	    		<th>Cuenta por Pagar</th>
			            	    		<th>Autorizado por</th>
			            	    		<th>Estatus</th>
			            	    		<th>dMotivoRechazo</th>
		            	    		</tr>
								</thead>
								<tbody />
							</table>
						</div>
					</div>
				</div>
				
				<div class="d-flex ">								
					<div class="p-2">
						<input type="button" id="pbEnvia" style="visibility: visible" type="button" onClick="generar();" value="Generar Layout" class="btn btn-primary"/>
					</div>
					<div class="ml-auto p-2">								        	
						<input type="button" id="pbRechazo" name="pbRechazo" value="Verifica Rechazo" class="btn btn-secondary">
			        </div>	
				</div>
		
				<div id="dialog-Rechazo" title="Motivo de Rechazo">
					Motivo de Rechazo: 
		           	<textarea name="motivo" id="motivo" rows="15" style="width: 200%;" ></textarea> 
				</div>		
			</form>
		
