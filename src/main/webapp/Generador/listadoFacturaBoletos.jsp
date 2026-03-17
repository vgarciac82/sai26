<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.io.File"%>
<%@page import="javax.swing.text.MaskFormatter"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.syc.contable.CancelaDocumento"%>


<%
	String cCentroContable = "";
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	String resultadoCancelacion = "";
	String mensaje = "";
	Connection conn = null;

	int nFolioDocumento;
	String cTipoDocumento;

	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}

	String cUE_Usuario = "";
	cUE_Usuario = usuario.getU_UR();

	boolean esAdmin = (usuario.getRole("JEFATURA_PAGOS") != null  || usuario.getRole("ADMIN") != null);
	
	String resultado = (String)session.getAttribute("MSG");
	resultado = StringUtils.trimToEmpty( resultado );
	
	boolean mostrarResultado = !StringUtils.isEmpty(resultado);
	session.removeAttribute("MSG");
	
%>
<!DOCTYPE HTML>
<html>
<head>
<title>Reporte de Solicitudes con Boletos de Avión.</title>

<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
		
			var unidadEjecutora = "<%=cUE_Usuario%>";
			var mostrarResultado = <%=mostrarResultado%>;
			var oTableLocal;
			
			$(document).ready(function() {
				if($("#cUR").val()=="A02"){
					querySelectPost("cURVistasViaticosTodos", "uEjecutora", { async : false });
				} else
					querySelectPost("cUnidadEjecutoraVistasViaticos", "uEjecutora", { async : false });
				
				listadoPorComprobar();
		
				$("#tabs").tabs({
					"show" : function(event, ui) {
						var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
						if( oTable.length > 0 ) {
							oTable.fnAdjustColumnSizing();
						}
					}
				});
		
				$("#resultDialog").dialog({
					autoOpen : mostrarResultado,
					width : "800px",
					heigth : "450px",
					modal : true
				});
				
				if( mostrarResultado )
					$("#resultadoSpan").css("display","block");
				
			});
		
			function cargaGrid(){
				listadoPorComprobar() ;
			}
					
			
			function clearSelect(idSel){
				$('#' + idSel).find('option').remove().end().append(
							'<option value="-1"></option>');
			}
			
			function listadoPorComprobar() {
				var condicionUE = $("#uEjecutora").val() == "" ? "" : " AND cUnidadResponsable = '" + $("#uEjecutora").val() + "' ";
				
				oTableLocal = $('#dt_PorComprobar').dataTable({
					"bLengthChange" : true,
					"bFilter" : true,
					"bSort" : true,
					"bInfo" : true,
					"bPaginate" : true,
					"bAutoWidth" : true,
					"bScrollCollapse" : true,
					"sScrollXInner": "150%", 
					"sScrollX": "100%",
					"sPaginationType" : "full_numbers",
					"bJQueryUI" : true,
					"bRetrive" : true,
					"bDestroy" : true,
					"bServerSide": true,
					"iDisplayLength": 25,
					"fnInitComplete": function() {    
						oTableLocal.fnAdjustColumnSizing();
					},
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
						sSearch : "Buscar:"
					},			
					sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=v_solicitudes_pendientes_boleto&qw=" + condicionUE ,
					bProcessing : true,			
					aoColumns : [
						{sName : "nFolioComision", 		bSearchable : false, bSortable : false, bVisible : true},
						{sName : "RFC", 				bSearchable : true, bSortable :  false, bVisible : true},
						{sName : "nombreCompleto", 		bSearchable : false, bSortable : false, bVisible : true},
						{sName : "cInformeComision", 	bSearchable : true, bSortable :  false, bVisible : true},
						{sName : "faplicacion", 		bSearchable : false, bSortable : false, bVisible : true},
						{sName : "Documento", 			bSearchable : false, bSortable : false, bVisible : true},
						{sName : "cBoleto", 			bSearchable : false, bSortable : false, bVisible : true},
						{sName : "cRuta", 				bSearchable : false, bSortable : false, bVisible : true},
						{sName : "mImporteBoleto", 		bSearchable : false, bSortable : false, bVisible : true},
						{sName : "cEsFirmaElectronica", bSearchable : false, bSortable : false, bVisible : true}
					]
				});
			}
			
			function listadoComprobados() {
				
				oTableLocal = $('#dt_Comprobados').dataTable({
					"bLengthChange" : true,
					"bFilter" : true,
					"bSort" : true,
					"bInfo" : true,
					"bPaginate" : true,
					"bAutoWidth" : true,
					"bScrollCollapse" : true,
					"sScrollXInner": "150%", 
					"sScrollX": "100%",
					"sPaginationType" : "full_numbers",
					"bJQueryUI" : true,
					"bRetrive" : true,
					"bDestroy" : true,
					"bServerSide": true,
					"iDisplayLength": 25,
					"fnInitComplete": function() {    
						oTableLocal.fnAdjustColumnSizing();
					},
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
						sSearch : "Buscar:"
					},			
					sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=v_solicitudes_con_boleto&qw=" + condicionUE ,
					bProcessing : true,			
					aoColumns : [
						{sName : "nFolioRelacionGastos", 		bSearchable : false, bSortable : false, bVisible : true},
						{sName : "RFC", 				bSearchable : true, bSortable :  false, bVisible : true},
						{sName : "cnombre", 		bSearchable : false, bSortable : false, bVisible : true},
						{sName : "cConcepto", 	bSearchable : true, bSortable :  false, bVisible : true},
						{sName : "faplicacion", 		bSearchable : false, bSortable : false, bVisible : true},
						{sName : "Documento", 			bSearchable : false, bSortable : false, bVisible : true},
						{sName : "cUnidadResponsable", 	bSearchable : false, bSortable : false, bVisible : true},
						{sName : "cEsFirmaElectronica", bSearchable : false, bSortable : false, bVisible : true}
					]
				});
			}
			
			function muestraLog(){
				$("#resultDialog").dialog("open");
			}
			
			function extraerListado() {
				
				$("#UR").val($("#uEjecutora").val());
				$("#cIdRFC").val($("#cRFC").val());
				document.location.href='../gstnmngr/generaExtraccionListado?UR=' + $("#cUR").val();
				
			}
	</script>

</head>
<body id="dt_example">
<br/>
	<form id="frmRecibosElectronicos" method="post" action="../notificaREPFaltante" >
		<input type="hidden" id="cCC" name="cCC" value="<%= cCentroContable%>" />
		<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= cUE_Usuario%>" />
		<input type="hidden" name="u_Login" id="u_Login" value="" />
		<input type="hidden" name="cUR" id="cUR" value="" />		
		<input type="hidden" name="cMovto" id="cMovto" value="" />
		
		<div id="container" class="container" style="width: 100%">
		   
		
			<div class="card-header"> <h3> Solicitudes de Viáticos con facturas de boletos de avión </h3> </div>
			<hr class="mt-3"/>
						
			<div id="resultadoSpan" style="display: none">
				<a href="#" onclick="muestraLog();return false">Mostrar Log</a>
			</div>
			
			<div class="row d-flex justify-content-center">	
				
	       		<ul class="nav nav-tabs" id="list-opciones">
		            <li class="nav-item" role="presentation">
		            	<button class="nav-link active" id="tabs-1" onClick="" data-bs-toggle="tab" data-bs-target="#tabs-1-temp" type="button" role="tab" aria-controls="tabs-temp" aria-selected="true">Por Comprobar</button>
		            </li>
		            <li class="nav-item" role="presentation">
		            	<button class="nav-link" id="tabs-2" onClick="" data-bs-toggle="tab" data-bs-target="#tabs-2-version" type="button" role="tab" aria-controls="tabs-version" aria-selected="false">Comprobados</button>
		            </li>			           				           
		    	</ul>
				
				<div class="tab-content mt-3" id="tabContent">		
					<div class="tab-pane fade show active" id="tabs-1-temp" role="tabpanel" aria-labelledby="tabs-temp">
						<h5> Solicitudes con boletos pendientes de adjuntar </h5>
						<hr class="mt-3"/>
						<div class="row d-flex justify-content">
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
										<label for="nEmpleado" class="form-label">Unidad Ejecutora:</label> 
										<select id="uEjecutora" name="uEjecutora" onchange="cargaGrid();" class="form-select">
										</select> 
								</div>
										
						</div>							
						<div class="table-responsive" style="width: 100%">
							<table id="dt_PorComprobar" class="table table-striped">
								<thead>
									<tr>
										<td>Folio</td>
										<td>RFC</td>
										<td>Nombre</td>
										<td>Concepto</td>
										<td>Fecha</td>
										<td>Documento</td>
										<td width="19%" style="text-align: center">Boleto</td>
										<td>Ruta</td>
										<td>Importe boleto</td>
										<td>Firma Electrónica</td>
									</tr>
								</thead>
								<tbody />
							</table>
						</div>
						<div class="row d-flex justify-content">
							<div class="col-11"></div>
							<div class="col-1 col-lg-1 col-md-1 col-sm-1 p-1">
									<input type="button" id="btnExtraer" name="btnExtraer" value="Extraer" onclick="extraerListado()" class="btn btn-secondary"/>
								</div>	
						</div>
						
					</div>
					
					<div class="tab-pane fade" id="tabs-2-version" role="tabpanel" aria-labelledby="tabs-version">
						<h5> Solicitudes con facturas de boletos adjuntos</h5>
						<hr class="mt-3"/>				
						<div class="table-responsive" style="width: 100%">
							<table id="dt_Comprobados" class="table table-striped">
								<thead>
									<tr>
										<td>Folio</td>
										<td>RFC</td>
										<td>Nombre</td>
										<td>Concepto</td>
										<td>Fecha</td>
										<td>Documento</td>
										<td width="19%" style="text-align: center">Boleto</td>
										<td>Ruta</td>
										<td>Importe boleto</td>
										<td>Firma Electrónica</td>
									</tr>
								</thead>
								<tbody />
							</table>
						</div>
						<div class="row d-flex justify-content">
							<div class="col-11"></div>
							<div class="col-1 col-lg-1 col-md-1 col-sm-12 p-1">
									<input type="button" id="btnExtraer" name="btnExtraer" value="Extraer" onclick="extraerListado()" class="btn btn-secondary"/>
							</div>			
						</div>							
					</div>
					
					<div id="resultDialog">							
						<h5> Resultados </h5>
						<hr class="mt-3"/>
						<textarea rows="20" cols="100" class="form-control"><%=resultado%></textarea>							
					</div>
					
				</div> <!-- FIN class="tab-content mt-3" -->
			</div> <!-- FIN class="row" -->			
		</div> <!-- FIN class="container" -->
	</form>
	
	</body>
</html>
