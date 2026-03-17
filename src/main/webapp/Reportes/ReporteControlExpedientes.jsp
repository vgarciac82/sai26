<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
    if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
    }
    
    String unidadEjecutora = usuario.getU_UR();
    
    boolean esAdmin = (usuario.getRole("ADMIN") != null || "A02".equals(unidadEjecutora) );

 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Expedientes Pendientes.</title>
	
	<!-- Estilos estandar para los controles JQuery -->
	<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
	<script src="../Generador/js/bootstrap.bundle.min.js"></script>
	<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
	<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
		
	<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../js/jsquery.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>
	
	
	<script type="text/javascript">
		var oTableExpedientes;
		var esAdmin = <%=esAdmin%>;
		var cUnidadEjecutora = "<%=unidadEjecutora%>";
		
		$(document).ready(function() {
			$.blockUI({
			 	theme: true,
			 	title: "Alimentando informacion, Espere...",
			 	message: "  Cargando...",
				timeout: 30000
			 });
			 
			oTableExpedientes = $( '#tblExpedientes' ).dataTable( {
				"bJQueryUI" : true
			} );
			
			$("#exportar").button().click(function(){
				var seleccionados = $(".chkFiltro:checked").length;
				if( seleccionados > 0 )
					$("#mainFrm").submit();
				else
					alert("Debe seleccionar un tipo de pago para exportar");
			});
			$.unblockUI();
		});
		
		function generaCondicion(){
			var cond = "";
			var token = "";
			
			$(".chkFiltro").each( function(){
					if( $(this).attr("checked") ){
						cond += token + " '" + $(this).attr("name") + "' ";
						token = " , ";
					}
				} 
			);
			
			if( cond != "" )
				return  " AND titulo_aplicacion IN ( " + cond + ") " + (!esAdmin?" AND cunidadresponsable = '" + cUnidadEjecutora + "'": "" );
			else
				return null;
			
		}
		
		function filtraReporte(){
			var cond = generaCondicion();
			creaDTExpedientes(cond);
		}
		
		function creaDTExpedientes(cond){
			if( !cond )
				cond = " AND 1 <> 1";
			
			oTableExpedientes = $( '#tblExpedientes' ).dataTable({
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
					oTableExpedientes.fnAdjustColumnSizing();
				},
				"fnPreDrawCallback": function () {
										$.blockUI({
											title: "Populating Table, please wait...",
											message: "  Please Wait..."
										});
									},
				"fnDrawCallback": function () {
				 	$.unblockUI();
				 },
				sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split( "/" )[ 1 ] + "/crud?rt=t&ql=v_ControlExpedientesSAI&qw=cumpleExpediente='N' " + cond,
				aoColumns : [
							{sName : "cunidadresponsable"},
							{sName : "titulo_aplicacion"}, 
							{sName : "nfolio"},
							{sName : "canocontrarrecibo"},
							{sName : "Solicitud_de_Pago_Firmada"},
							{sName : "Comprobante_Banco"},
							{sName : "Otros"},
							{sName : "CLC"},
							{sName : "TipoTramite"},
							{sName : "tipoIngreso"},
							{sName : "cDescripcionPoliza"},
							{sName : "cTipoPoliza"},
							{sName : "OficioPago"}
							]
			});
		}
	</script>

</head>

<br/>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<div id="container" class="container" style="width: 100%">
		<div class="card-header"> <h3> Expedientes Pendientes </h3> </div>
		<hr class="mt-3"/>
		
		<div id="encabezadoFiltros" class="container" style="width: 100%">
			<form id="mainFrm" method="get" action="../ExportaExpedientesPendientes" target="_blank">
			
				<h6> Filtros </h6> 
				<hr class="mt-3"/>
				
				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12">
						<p class="text-info">Seleccione los tramites para conocer los expedientes pendientes de completar</p>							
					</div>
				</div>
				
				<div class="row d-flex">		
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="checkbox" class="form-check-input chkFiltro" id="PAGODIVERSO" name="PAGODIVERSO" onclick="filtraReporte()">  &nbsp; Pago Diverso
					</div>			
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">							
						<input type="checkbox" class="form-check-input chkFiltro" id="RELACIONGASTOS" name="RELACIONGASTOS" onclick="filtraReporte()"> &nbsp; Relacion de Gastos
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="checkbox" class="form-check-input chkFiltro" id="CAJA" name="CAJA" onclick="filtraReporte()"> &nbsp; Solicitud No Presupuestal
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="checkbox" class="form-check-input chkFiltro" id="PAGODIRECTO" name="PAGODIRECTO" onclick="filtraReporte()"> &nbsp; Pago Directo
					</div>
				</div>
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="checkbox" class="form-check-input chkFiltro" id="PAGOFEDERALIZADO" name="PAGOFEDERALIZADO" onclick="filtraReporte()"> &nbsp; Pago Federalizado
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="checkbox" class="form-check-input chkFiltro" id="PAGOOBRA" name="PAGOOBRA" onclick="filtraReporte()"> &nbsp; Pago de Obra
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="checkbox" class="form-check-input chkFiltro" id="POLIZA" name="POLIZA" onclick="filtraReporte()"> &nbsp; Poliza Manual
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="button" class="btn btn-secondary btn-sm" id="exportar" value="Exportar">
					</div>						
				</div>										
			
			</form>
		</div>
		
		<div id="infoDiv" class="container" style="width: 100%">
			<div class="row d-flex">								
				<div class="col-12 col-lg-12 col-md-12 col-sm-12">
					<table  id="tblExpedientes" class="table table-striped">
						<thead>
							<tr>
								<th>Unidad Ejecutora</th>
								<th>Tramite			</th>
								<th>Folio			</th>
								<th>Contrarecibo	</th>
								<th>Solicitud de Pago</th>
								<th>Comprobante Bancario</th>
								<th>Otros</th>
								<th>CLC				</th>
								<th>Tipo Tramite	</th>
								<th>Tipo Ingreso	</th>
								<th>Concepto		</th>
								<th>Tipo de Poliza	</th>
								<th>Facturas / Oficios	</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
			
		</div>				
	</div>
</body>

</html>