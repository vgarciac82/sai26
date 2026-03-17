<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%
	String cCentroContable = "";
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String msg = "";
	if( session.getAttribute("RESULT") != null ){
		msg = (String)session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
		
	
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	
	String cUE_Usuario = "";
	cUE_Usuario = usuario.getU_UR();
	String U_LOGIN = "";
	U_LOGIN = usuario.getLogin();
%>
<!DOCTYPE html>
<html>
	<head>
		<title>Captura Folio Validacion</title>


  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.datatables.net/1.13.8/css/jquery.dataTables.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css"/>

  	<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>	
  	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  	<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
  	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	
	<script type="text/javascript" charset="utf-8">
		var msg = "<%=msg%>";
		var modalFolioValidacion;
		$(document).ready(function() {
		 
			modalFolioValidacion = new bootstrap.Modal(document.getElementById('dialog-CapturaFolio'), 'data-bs-backdrop');
			const options = {
					bRetrieve: true,
			        language: es_mx,
			        paging: true,
			        searching: true,
			        autoWidth: false
			    };
			const optsSinPaginacion = Object.assign({}, options, { paging: false , searching: false, "bInfo" : false });

		    if (msg!="") {
				   Swal.fire({
					  title: 'Revise',
					  text: msg,
					  icon: 'error'
					})
		    }
			
		    datosUnidadEjecutora();	
		    var unidad = document.getElementById("cIdUnidadEjecutoraUsuario").value;

				// Inicializa tabla visible por defecto
			    $('#tablaContratos3300').DataTable(options);

			    buscaContratos3300();
							    
			 	// Inicializar al cambiar de tab (solo una vez)
		    	let initialized = {};


			    $('button[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
			        const target = $(e.target).data('bsTarget');

			        switch (target) {
			            case '#contratos-3300':
			                if (!initialized['tablaContratos3300']) {
			                	$('#tablaContratos3300').DataTable(optsSinPaginacion);
			                    initialized['tablaContratos3300'] = true;
			                } else {
			                    $('#tablaContratos3300').DataTable().columns.adjust().draw();
			                }
			                
			                contratos3300FolioValidacion();
			                			               
			                break;				        
			        }
			    });				  				
			    				
		});
		 
		 var es_mx = {
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
					sSearch : "Filtro:",
					oPaginate : {
						sFirst : "Primero",
						sPrevious : "Ant.",
						sNext : "Sigte.",
						sLast : "&Uacute;ltimo"
					}
		
				};
		 
	    function buscaContratos3300(){
			$("#cUR").val($("#cboUnidadEjecutora").val());
			contratos3300FolioValidacion();

		}
	    
	    var oTableContratos3300;
	    
	    function contratos3300FolioValidacion(){ 
	    	var cUR = $("#cUR").val();
			var cWhereUR = "";
			var esAdmin = $("#isAdmin").val();
			var cUE_Usuario = $("#cIdUnidadEjecutoraUsuario").val();
			
			if (cUR != "*" && cUR != "**"){
				cWhereUR = " AND cUnidadResponsable = '" + cUR + "'";
			} else if (cUR == "**") {
				cWhereUR += " AND cUnidadResponsable in ('G01', 'G02', 'G03', 'G04', 'G05', 'G06', 'G07', 'G08', 'G09', 'G10', 'G11', 'G12', 'G13', 'G14', 'G15', 'G16', 'G17', 'G18', 'G19', 'G20', 'G21', 'G22', 'G23', 'G24', 'G25', 'G26', 'G27', 'G28', 'G29', 'G30', 'G31', 'G32')";
			}
			
			if(cUE_Usuario == "A02"){
				cWhereUR += " AND (cTipoContrato IN ('FE', 'RE', 'RI', 'PD') )";
			}else if(cUE_Usuario == "A04"){
				cWhereUR += " AND cTipoContrato NOT IN ('FE', 'RE', 'RI', 'PD')";
			}	
			
			oTableContratos3300 = $('#tablaContratos3300').DataTable({
				bRetrive: true,
				bDestroy: true,
				oLanguage: es_mx,
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" 
							+ window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_contratos3300&qw=(nFolioSuficiencia IS NOT NULL AND nFolioSuficiencia <> -1)" + cWhereUR,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				order: [[1, 'asc']],
				aoColumns: [
					//{ sName: "id",					bSearchable: false, bSortable: false, bVisible: true},
					{ sName: "nFolioCompromiso",	bSearchable: true, bSortable: false, bVisible: true},
					{ sName: "fCarga",				bSearchable: true, bSortable: false, bVisible: true},
					{ sName: "cUnidadResponsable",	bSearchable: true, bSortable: false, bVisible: true},
					{ sName: "cIdContrato",			bSearchable: true, bSortable: false, bVisible: true},
					{ sName: "cTipoContrato",		bSearchable: true, bSortable: false, bVisible: true},
					{ sName: "cDescripcionPoliza",	bSearchable: true, bSortable: false, bVisible: true},
					{ sName: "caNoCompromiso",		bSearchable: true, bSortable: false, bVisible: true},
					{ sName: "cFolioValidacion",	bSearchable: true, bSortable: false, bVisible: true}	
				]
			});
			
	    	$("#tablaContratos3300 tbody").on("dblclick", "tr", function(event) {
	    		limpiaValores();
	    		
	    		var row = oTableContratos3300.row($(this));
	    		var data = row.data();
	    		
	    		var compromiso = data[0];
	    		var contrato = data[3];
	    		var folioValidacion = data[7];
	    		
	    		$("#integracion").val(contrato);
	    		$("#folioCompromisoTxt").val(compromiso);
	    		$("#folioCompromisoSnd").val(compromiso);
	    		$("#NoFolioValidacion").val(folioValidacion);
	    		
	    		modalFolioValidacion.show();
	    	});
		}
		
	    function datosUnidadEjecutora(){
			
			queryFormPost("validaUsuarioCentralesMAT_Read", {async: false});
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cboUnidadEjecutora", {async: false});
			$("#cboUnidadEjecutora").val($("#cIdUnidadEjecutoraUsuario").val());

			
			if($("#isAdmin").val() == "0"){
				$("#cboUnidadEjecutora").prepend("<option value='*'> * - OFICINAS CENTRALES</option>");
				$("#cboUnidadEjecutora").prepend("<option value='**'> ** - GERENCIAS ESTATALES</option>");
			}
			
		}
	    
		function limpiaValores(){
			$("#integracion").val("");
			$("#folioCompromisoTxt").val("");
			$("#folioCompromisoSnd").val("");
			
			$("#NoFolioValidacion").val("");
			$("#NoFolioValidacionSnd").val("");
		}

		function actualizaFolioValidacion(){
				if( validaEnvio() && confirm("Esta seguro que desea actualizar el id " + $("#folioCompromisoSnd").val() + " con el folio de Validacion " + $("#NoFolioValidacion").val() + "?") ){
					$("#NoFolioValidacionSnd").val($("#NoFolioValidacion").val() );
					try{
						queryFormPost({
							queryName:"numFolioValidacionUpdate",
							async:false,
							callback:function(){
								modalFolioValidacion.hide();
							    Swal.fire({ icon: "success",
											text: "Folio de Validacion actualizado exitosamente"});
								limpiaValores();	
										
							}
						});
						contratos3300FolioValidacion();
						
					}catch(e){
						Swal.fire({ icon: "error",
									text: e});
						
					}
					
					modalFolioValidacion.hide();
				}
			
		
		}
	    
		function validaEnvio(){
			$("#sAuxiliarComodin").val("");
			$("#sAuxiliarComodin").val($("#integracion").val());
			
			if( $.trim( $("#NoFolioValidacion").val() ) == ""){			
				Swal.fire({ icon: "warning",
							text: "El Num. de Folio de Suficiencia es requerido"});
				return false;
			}else if( $("#folioCompromisoSnd").val() == "" ){			
				Swal.fire({ icon: "warning",
							text: "No se encontro folio seleccionado. Intente nuevamente"});
				return false;
			}
			
			return true;
		}	
		
	</script>
	
	</head>
		  	<body id="dt_example"  >
		  	<br/>  	
				<div class="container mt-4">
					<form id="envioSICOP" action="../gstnmngr/generaLayoutSuficiencia" method="post">
						<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
				    	<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= cUE_Usuario%>" />
				    	<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
				    	<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%= U_LOGIN%>"/>
				    	<input type="hidden" name="cUR" id="cUR"  value=""/>			
				    	<input type="hidden" id="folioCompromisoSnd"name="folioCompromisoSnd" value="">
						<input type="hidden" id="NoFolioValidacionSnd"name="NoFolioValidacionSnd" value="">
					</form>
			<div class="card-header"> <h3> Captura Folio Validacion </h3> </div>
			<br>
		    <ul class="nav nav-tabs" id="TabSuf" role="tablist">		        
		        <li class="nav-item" role="presentation">
		            <button class="nav-link active" id="contratos-3300-tab" data-bs-toggle="tab" data-bs-target="#contratos-3300" type="button" role="tab">Contratos 3300</button>
		        </li>	
		    </ul>
		
		    <div class="tab-content mt-3" id="tabsLayouts">		       
		        <!-- TAB 1 -->
		        <div class="tab-pane fade show active" id="contratos-3300" role="tabpanel">
		        	<h5> Folios no autorizados de suficiencia presupuestal </h5>
		        	 <div class="row">
			        	<div class="col-sm-2 col-md-4  col-lg-6" align="left">	
							<label for = "cboUnidadEjecutora">Unidad Ejecutora: </label>
							<select class="form-select" name="cboUnidadEjecutora" id="cboUnidadEjecutora" onchange="buscaContratos3300();">
				    				<option value = "*" >TODAS LAS UNIDADES</option>
				    		</select>
						</div>
					</div>
		            <br>
		            <table class="table table-bordered table-striped" id="tablaContratos3300">
		                <thead>
		                <tr>		                	
		                    <th>Folio</th>		                    
		                    <th>Fecha</th>
		                    <th>UR</th>
		                    <th>Contrato</th>
		                    <th>Tipo</th>
		                    <th>Descripción</th>
		                    <th>Contrarrecibo</th>
		                    <th>Folio Validacion</th>			                    		                    	                   		                    	                   		                    
		                </tr>
		                </thead>
		                <tbody >
		                </tbody>
		            </table>		            
		        </div>		
		    </div>
		</div>
		
<div class="modal fade" id="dialog-CapturaFolio" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
	<div class="modal-dialog"> <!-- Caja de dialogo -->
		<div class="modal-content"> <!-- Contenido de la caja -->
			  <div class="modal-header"> <!-- Encabezado de la caja -->
				<h5 class="modal-title">Ingrese informacion Suficiencia</h5>
				<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
			  </div>
			<div class="modal-body"> <!-- Cuerpo de la caja -->
				<div class="row d-flex">								
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">					
						<label for="integracion" class="form-label"> Contrato: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="integracion" name="integracion" value="" readonly/>
			        </div>						
				</div>		
				<div class="row d-flex">								
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">					
						<label for="folioCompromisoTxt" class="form-label"> Id Compromiso: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="folioCompromisoTxt" name="folioCompromisoTxt" value="" readonly/>
			        </div>						
				</div>	
				<div class="row d-flex">								
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">					
						<label for="NoFolioValidacion" class="form-label"> #Folio Validacion SICOP: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="NoFolioValidacion" name="NoFolioValidacion" value=""/>
			        </div>						
				</div>				
			</div>
			<div class="modal-footer"> <!-- Pie de pagina de la caja -->
				<button type="button" id="aceptarFolioValidacion" class="btn btn-primary" onclick="actualizaFolioValidacion();" >Aceptar</button>
				<button type="button" class="btn btn-secondary" onclick="limpiaValores();" data-bs-dismiss="modal">Cancelar</button>										    
			</div>
		</div>
	</div>		
</div>
		
		

	</body>
			
</html>
