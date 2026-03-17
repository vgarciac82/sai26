<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.sai.contabilidad.caja.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String msg = "";
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String login= usuario.getLogin();
	String cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor() ;
	String cUR = "";
	cUR = usuario.getU_UR();	
	
	String fAplicacion[]=CajaBusinessLogic.readfAplicacion(cCentroContable, cUR);
	if( session.getAttribute("RESULT") != null ){
		msg = (String)session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
	String AlcanceVistas=CajaBusinessLogic.LeerVistas(login);
	
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>Cancelaci\u00F3n de Solicitudes No Presupuestales</title>

<!-- Estilos estandar para los controles JQuery -->

<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<link rel="stylesheet" href="css/bootstrap.min.css">
<script src="js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript">
	var togle = 0;
	var msg = "<%=msg%>";
	var login = "<%=login%>";
	var vistas="<%=AlcanceVistas%>";
	var table;
	
	$(document).ready(function() {		
		
		$("#msgDialog").dialog(
			{
				autoOpen : false,
				height : 400,
				width : 450,
				modal : true,
				buttons : {
					"Aceptar" : function() {
						$(this).dialog("close");
					}
				}
			}
		);
		
		if( msg != ""){
			$("#msgDialog").dialog("open");
		}					
		
		$("#fecha").val(moment().format('yyyy-MM-DD'));
		var condicion_UR=" ";
			
		if(login=="admin")		
			condicion_UR =" != 'A16' ";
		else
			condicion_UR =" IN ("+vistas+")";				

		//Crea el DataTable con los campos de config. minimos. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		table = $('#dt_solicitudes')
				.dataTable(
						{
							"bLengthChange" : true,
							"bFilter" : true,
							"bSort" : true,
							"bInfo" : true,
							"bPaginate" : true,
							"bAutoWidth" : true,
							"bScrollCollapse" : true,
							"sScrollXInner": "100%", 
							"sScrollX": "100%",
							"sPaginationType" : "full_numbers",
							"bJQueryUI" : true,
							"bRetrive" : true,
							"bDestroy" : true,
							"bServerSide": true,
							"fnInitComplete": function() {    
								    table.fnAdjustColumnSizing();
								},
							"iDisplayLength": 25,  
							sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_solicitudes_aplicadas&qw=" + encodeURIComponent( "cUnidadEjecutora"+condicion_UR),
							aoColumns : [
									{sName : "cmd"},
									{sName : "nfoliocaja"},
									{sName : "cdescripcionpoliza"},
									{sName : "cunidadejecutora"},
									{sName : "mmontosolicitud"} 
									],
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
								sSearch : "Filtro:",
								oPaginate : {
									sFirst : "Primero",
									sPrevious : "Ant.",
									sNext : "Sigte.",
									sLast : "&Uacute;ltimo"
								}
							}
						});
	
		
		
		$("#cancelarTramite").button().click(
			function(){
				
				var vacio = true;
				var tListado = $("#dt_solicitudes").dataTable().fnGetData();
				var tListado2 = document.getElementById('dt_solicitudes');
				var cont = 0;
			    var id_Caja = 0;
			    var monto = 0;
			    if($("#fecha").val() == ""){
			    	Swal.fire({ icon: 'warning',
								text: "Debes seleccionar una fecha para la cancelación" });			    			    	
			    	return false;
			    }
			    $("#fechaAplicacion").val($("#fecha").val().split('-').reverse().join('/'));
			        
				for (i=0; i<tListado.length;i++){
					var row = tListado2.rows[i+1];
					var chkbox = row.cells[0].childNodes[0];
					if(null != chkbox && true == chkbox.checked){
						id_Caja=tListado[i][1];
						monto=tListado[i][4];
						vacio = false;
						$("#nFolioCaja").val(id_Caja);
						$("#nfoliocomprobacion").val("0");
						queryFormPost( "buscaBonificacion", {async:false}  );
						if($("#nfoliocomprobacion").val() != '0'){
							alert("El Folio: "+$("#nFolioCaja").val()+" cuenta con Comprobacion en el Folio: "+$("#nfoliocomprobacion").val());
							return false;
						}else
							cont++;
					}			  
				}
				
				$("#nFolioCaja").val(id_Caja);								
				$("#mmontosolicitud").val(monto);
				$("#mmontosolicitud").val();

				if( $(":checked").length > 0 ){
					//if( confirm("Esta seguro de cancelar las solicitudes seleccionadas?\nEste proceso no se puede revertir.") ){
					Swal.fire({				  
						  text: "¿Esta seguro de cancelar las solicitudes seleccionadas?\nEste proceso no se puede revertir.",
						  icon: "warning",
						  showCancelButton: true,
						  confirmButtonColor: '#7066E0',
						  cancelButtonColor: '#e6e6e6',
						  confirmButtonText: 'Aceptar',
						  cancelButtonText: 'Cancelar'
					}).then((result) => {
						if (result.isConfirmed) {		
							$.blockUI( {
								message : "Procesando espere ......"
							});
							
							$("input[name='chk_caja']:checked").each( 
								function(){												
									 $("#nFolioCaja").val( $(this).val() );
									 queryFormPost( "buscaEvento", {async:false}  );
									 if ($("#cEvento").val().split("_")[0] == "5" && $("#cEvento").val().split("_")[2]=="2")
									 {
									 	queryFormPost( "fechaCancelUpdateSNP", {async:false}  );
									 	queryFormPost( "ActualizaCajaComprobadoCancelacion", {async:false}  );
									 }
									 if ($("#cEvento").val().split("_")[0] == "8" && $("#cEvento").val().split("_")[2]=="2")
									 {										
										queryFormPost( "fechaCancelUpdateSNP", {async:false}  );
									 	queryFormPost( "viaticosEncabezado,viaticosDetalle", {async:false}  );
										
									 }
									 if ($("#cEvento").val().split("_")[0] == "8" && ($("#cEvento").val().split("_")[2]=="1") || ($("#cEvento").val().split("_")[2]=="7"))
									 {
										queryFormPost( "fechaCancelUpdateSNP", {async:false}  );
										queryFormPost( "validaTieneCompronaciones", {async:false}  );
										if ($("#tieneComprobaciones").val() == "N"){								 									 		
									 		queryFormPost( "viaticosEncabezadoDelete", {async:false}  );
										}else {
											queryFormPost( "viaticosEncabezadoCancelado", {async:false}  );
										}																					 									
									 }
									 if ($("#cEvento").val().split("_")[0] == "31")
									 {										
										queryFormPost( "fechaCancelUpdateSNP", {async:false}  );
									 	queryFormPost( "ingresoGreenMexEncabezadoDelete", {async:false}  );
										
									 }
									 else {
									 	queryFormPost( "fechaCancelUpdateSNP", {async:false}  );
									 	} 
								} 
							);
							
							$("#FormCancelacion").submit();
						}
					})				
				}else{
					Swal.fire({ icon: 'warning',
								text: "Debe seleccionar al menos una solicitud a cancelar." });						
				}
			}
		);
	});
		

	function seleccionar() {
		 let isChecked = $('#select-all').prop('checked');
	     $('.chk_caja').prop('checked', isChecked);
	}
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
<br/>
	<form id="FormCancelacion" name="FormCancelacion" method="post" action="../CancelaSolNP">
		<input type="hidden" value="" id="buscaVal" name="buscaVal"> 
		<input type="hidden" value="" id="existe" name="existe" value="">
		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=usuario.getU_UR()%>" />
		<input type="hidden" id="nFolioCaja" name="nFolioCaja" value="">
		<input type="hidden" id="nfoliocomprobacion" name="nfoliocomprobacion" value="">
		<input type="hidden" name="cEvento" id="cEvento" value="" />
		<input type="hidden" name="mmontosolicitud" id="mmontosolicitud" value="0"/>
		<input type="hidden" name="fechaAplicacion" id="fechaAplicacion" />		
		<input type="hidden" name="tieneComprobaciones" id="tieneComprobaciones" value="" />
		
		<div id="container" style="width:70%" class="container">		
		
			<div class="card-header"> <h3> Cancelaci&oacute;n de Solicitudes NO Presupuestales </h3> </div>
			<hr class="mt-3"/>
			
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-lg-2 col-md-2 col-sm-12">
							<br>
							<input class="form-check-input" type="checkbox" value="" id="select-all" onChange="seleccionar()"/> 
							<label class="form-check-label" for="select-all"> Seleccionar Todos </label>
						</div>
						
						<div class="col-4"></div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							<div class="form-group">
								<label for="fechaO">Fecha de cancelaci&oacute;n:</label>
			                    <div class="input-group date" id="datepicker1">
			                    	<input type="date" class="form-control form-control-sm" id="fecha" name="fecha"/>                                    
			                    </div>
			                </div>										
						</div>
						<div class="col-1"></div>	
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
							<input type="button" id="cancelarTramite" name="cancelarTramite" value="Cancelar tramite" class="btn btn-secondary"/>																																		
						</div>																				
					</div>
				</div>
			</div>

			<br/>
			
			<div id="solicitudes" class="table-responsive">
				<table id="dt_solicitudes" class="table table-striped">
					<thead>
						<tr>	
							<th>&nbsp;</th>
							<th>Folio Solicitud</th>
							<th>Descripcion</th>
							<th>Unidad Ejecutora</th>
							<th>Monto</th>
						</tr>
					</thead>
				</table>
			</div>	
		</div>
	</form>
	
	<div id="msgDialog" title="Resultado de Carga">
		<br/>	
		<div class="row d-flex justify-content-center">				
			<div class="col-12 col-lg-12 col-md-12 mb-3">					
				<textarea id="msgTxt" name="msgTxt" rows="10" cols="100" maxlength="1000" class="form-control"><%=msg.replaceAll("<br>", "\n")%></textarea>
			</div>
		</div>		
	</div>
</body>
</html>