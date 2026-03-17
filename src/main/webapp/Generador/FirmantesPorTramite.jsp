<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	Map rol =null;
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	rol =usuario.getRoles();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    
   
	<title>Firmantes Por Tramite</title>
	
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
		
	<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>	
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"> </script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"> </script>
	<script type="text/javascript" src="../Generador/js/Moment.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
	
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
   
	<script type="text/javascript" charset="utf-8">
		var roles="";
		var oTableLineas;
		
		$(document).ready(function() {
			reloadTabla();
		<%
			 String roles="";
			 String role="";
			 Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
		%>
			$("input.AyudaSyC").subIniciaDlg();
		    $("input.autoCompletaSyC").subIniciaAutoCompleta();
		    roles="<%=roles%>";
		    $("#buscar").button();
		    $("#guardar").button();
			init();
	
		});//Fin document ready
		function init(){
			if (roles.indexOf("ADMIN") >= 0){
				$("#isAdmin").val(0);
			} 
		    querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false,
		    	callback:function(){
		    		$("#cIdUnidadEjecutora").val($("#cIdUnidadEjecutoraUsuario").val());
		    	}
		    });
		    querySelectPost("tCatalogoTramitesFirmante", "cidTramite", {async: false,
		    	callback:function(){
		    		catFirmantes();
		    	}
		    });
		    
		}
		function desactivaBackspace(event){
			if(window.event && window.event.keyCode == 8){
		     	window.event.keyCode = 505;
	    	}
		    if(window.event && window.event.keyCode == 505){
	    	 	return false;
	    	}
	    	return true;
		}
		
		function reloadTabla(){
			oTableLineas = $("#tblFirmantes").dataTable({
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
				aaSorting: [[ 0, "asc" ]]
			});
		}
		function catFirmantes(){
			var tipoFirmante =  $('input[name=tipoAut]:checked').val();
			var qw="1=1 and cUnidadResponsable='"+$("#cIdUnidadEjecutora").val()+"' and cModulo='"+$("#cidTramite").val()+"' and cTipoFirmante = '" + tipoFirmante + "'";
			oTableLineas = $("#tblFirmantes").dataTable({
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
				"fnServerData": function ( sSource, aoData, fnCallback ) {
                                            $.ajax( {
                                                "dataType": 'json', 
                                                "type": "POST", 
                                                "url": sSource, 
                                                "data": aoData, 
                                                "success": fnCallback
                                            } );
                                        },
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_catFirmantesPorTramite&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{sName: "cModulo"},
					{sName: "cUnidadResponsable"},
					{sName: "cNombre"},
					{sName: "cApellidoPaterno"},
					{sName: "cApellidoMaterno"},
					{sName: "cPuesto"}
				]
			});
		}
		function guradarInfo(){
			if($("#NumEmpleado").val()==""){
				Swal.fire("Seleccione","Selecciona un empleado","info");
				return;
			}
			
			Swal.fire({
				  title: '¿Desea continuar?',
				  text: "Se guardará la información del firmante.",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					  queryFormPost("guardaFirmantesPorTipoTramite", { async: false,
							callback:function(){
								cleanFields();
								Swal.fire("OK","Datos Guardados.", "success");
						    	catFirmantes();
						    }
						 });
				  } 
				})
		}
		
		function cleanFields(){
			$("#NumEmpleado").val('');
			$("#nombreEmpleado").val('');
			$("#apellidoPatEmpleado").val('');
			$("#apellidoMatEmpleado").val('');
			$("#puestoEmpleado").val('');
		}
		
	</script>
  </head>
  <br/>
  <body id="dt_example">
    	<div id="container" class="container" style="width: 90%;">
    		<div class="card-header"> <h3> Cat&aacute;logo de firmantes para los diferentes tipos de tr&aacute;mites </h3> </div>
			<hr class="mt-3"/>
    			
   			<form action="" name="formCampos" id="formCampos">
   				<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
				<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
		    	<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
		    	<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
   			
   				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
						<label for="cIdUnidadEjecutora"> Unidad Ejecutora: </label>							 							 					
					</div>	
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">															
						<select id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" class="form-select form-select-sm"></select> 							 							
					</div>								
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
						<label for="cidTramite"> Tramite: </label> 							 							
					</div>								
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">															
						<select id="cidTramite" name="cidTramite" class="form-select form-select-sm"></select> 							 							
					</div>								
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">	
						<input type="button" id="buscar" onclick="catFirmantes()" value="Buscar" class="btn btn-secondary btn-sm"/>
					</div>
				</div>
				
				<br/>
				
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">								
						<div class="row">
							<h5> Vo. Bo. o Autorizaci&oacute;n </h5>
							<hr class="mt-3"/>
							<div class="row">
								<div class="columna-interior col-12 col-md-6">
									<input type="radio" id="VoBo" name="tipoAut" value="VOBO" class="form-check-input" checked="checked"/>&nbsp;VoBo
								</div>
								<div class="columna-interior col-12 col-md-6">
									<input type="radio" id="Autoriza" name="tipoAut" value="AUT" class="form-check-input"/>&nbsp;Autoriza
								</div>
							</div>
						</div>										
					</div>
				
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">		
						<div class="row">
							<h5> Delego/Suplencia </h5>
							<hr class="mt-3"/>
							<div class="row">
								<div class="columna-interior col-12 col-md-6">
									<input type="radio" id="VoBoSuplencia" name="tipoAut" value="SUPVOBO" class="form-check-input"/>&nbsp;VoBo
								</div>
								<div class="columna-interior col-12 col-md-6">
									<input type="radio" id="AutorizaSuplencia" name="tipoAut" value="SUPAUT" class="form-check-input"/>&nbsp;Autoriza
								</div>
							</div>
						</div>							
					</div>
				</div>
									
				<br/>
								
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="NumEmpleado" class="form-label"> # Empleado: </label>		
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">	
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-123"></i></span>							
							<input type="text" class="form-control form-control-sm AyudaSyC obligatorio" id="NumEmpleado" name="NumEmpleado" readonly onkeydown="return(desactivaBackspace(event))"/>
						</div>
					</div>
				</div>		
				
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="nombreEmpleado" class="form-label"> Nombre: </label>		
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">		
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-person"></i></span>						
							<input type="text" class="form-control form-control-sm" id="nombreEmpleado" name="nombreEmpleado" readonly/>
						</div>
					</div>
				</div>	
				
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="apellidoPatEmpleado" class="form-label"> Ap. Paterno: </label>		
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-person"></i></span>								
							<input type="text" class="form-control form-control-sm" id="apellidoPatEmpleado" name="apellidoPatEmpleado" readonly/>
						</div>
					</div>
				</div>		
				
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="apellidoMatEmpleado" class="form-label"> Ap. Materno: </label>		
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">	
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-person"></i></span>							
							<input type="text" class="form-control form-control-sm" id="apellidoMatEmpleado" name="apellidoMatEmpleado" readonly/>
						</div>
					</div>
				</div>	
				
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
						<label for="puestoEmpleado" class="form-label"> Puesto: </label>		
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">						
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-briefcase"></i></span>		
							<input type="text" class="form-control form-control-sm" id="puestoEmpleado" name="puestoEmpleado" readonly/>
						</div>
					</div>
				</div>	
				
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">
						<input type="button" value="Guardar" id="guardar" onclick="guradarInfo()" class="btn btn-secondary btn-sm"/>		
					</div>
				</div>	
							
  					
				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<table id="tblFirmantes" class="table table-striped table-bordered">								
							<thead >
								<tr>
									<th align="center">Modulo</th>
									<th align="center">UE</th>
									<th align="center">Nombre </th>
									<th align="center">Apellido <br />Paterno </th>
									<th align="center">Apellido <br />Materno </th>
									<th align="center">Puesto </th>									
								</tr>										
							</thead>
						</table>
					</div>
				</div>
								
  			</form>    		
    	</div>
  </body>
</html>
