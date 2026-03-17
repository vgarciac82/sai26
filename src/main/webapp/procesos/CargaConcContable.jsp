<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% 
	if( session == null){
		response.sendRedirect("../index.jsp");
		return;
	}	
	
	Usuario u = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if( u == null ){
		response.sendRedirect("../index.jsp");
		return;
	}
	
	if( u.getPropiedad("CCENTROCONTABLE") == null || StringUtils.isEmpty(  u.getPropiedad("CCENTROCONTABLE").getValor() ) ){
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String urUsuario = u.getU_UR();
	String nombreUsuario = u.getLogin();
	String centroContableUsuario =  u.getPropiedad("CCENTROCONTABLE").getValor();
	String hoy = Util.getTodayESMX();
	String operador = u.getNombre();
	
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	String ejercicioFiscal = adbl.obtenEjercicioFiscal();
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Pago de penas convencionales</title>
		
		<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
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
			var nombreUsuario = '<%=nombreUsuario%>';
			var UR = '<%=urUsuario%>';	
			var CC = '<%=centroContableUsuario%>';
			var fAplicacion = '<%=hoy%>';
			var operador = '<%=operador%>';
			var ejercicioFiscal = <%=ejercicioFiscal%>;
			var oTable;
			
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
				
			$(document).ready(function() {
				cargaDataTable();
				$("#CargaDialog").dialog({
					autoOpen : false,
					height : 600,
					width : 700,
					modal : true
				});
			});	
					
			function cargaDataTable(){
				var cond = "";

				if( "00" !=  CC )
					cond = " ccentrocontable=" + CC;
				else{
					cond = "1=2";
					alert("No se pueden realizar operaciones en el centro contable consolidado");
				} 
				oTable = $('#dtDetalleConciliaciones').dataTable(
			    {
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
					"iDisplayLength": 10,
					"fnInitComplete": function() {    
						oTable.fnAdjustColumnSizing();
					},
					sAjaxSource : window.location.protocol + "//"
							+ window.location.host + "/"
							+ window.location.pathname.split("/")[1]
							+ "/crud?rt=t&ql=vInfoConciliaContable&qw=" + cond,
					aoColumns : [ {	 sName : "CentroContableDesc"
								}, {
									sName : "cnombreconciliacion"
								}, {
									sName : "Enero"
								}, {
									sName : "Febrero"
								}, {
									sName : "Marzo"
								}, {
									sName : "Abril"
								}, {
									sName : "Mayo"
								}, {
									sName : "Junio"
								}, {
									sName : "Julio"
								}, {
									sName : "Agosto"
								}, {
									sName : "Septiembre"
								}, {
									sName : "Octubre"
								}, {
									sName : "Noviembre"
								}, {
									sName : "Diciembre"
								}],
					oLanguage : es_mx
				});
					 
			}
			function cargaConciliacion(nMes, nIDConciliacion){
				 $('#cargaFrm').attr('src','../Generador/CargaConciliacionContable.jsp?mes='+nMes+'&idConciliacion='+nIDConciliacion);
				 $("#CargaDialog").dialog("open");
			}
			
			function muestraArchivo(nodeID){
				window.open("../SAIFilestore?select="+nodeID,"" ,"scrollbars=1, resizable=no, width=850, height=700");
			}
			
			function aceptarCarga(){
				 $('#cargaFrm').attr('src','');
				 $("#CargaDialog").dialog("close");
				 cargaDataTable();
			}
		</script>
				
	</head>
	<br/>
	<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">				
		<form id="mainFrm" name="mainFrm">
			<div id="container" style="width:100%" class="container">
				<div class="card-header"> <h3> Carga de Conciliaciones y Reportes Firmados </h3> </div>
				<hr class="mt-3"/>
			
				<div id="dtDetalleConciliacionesDiv">
					<table class="table table-striped" cellspacing="0" cellpadding="0" align="center" id="dtDetalleConciliaciones">
						<thead>
							<tr>
								<th>Centro Contable</th>
								<th>Conciliacion</th>
								<th>Enero</th>
								<th>Febrero</th>
								<th>Marzo</th>
								<th>Abril</th>
								<th>Mayo</th>
								<th>Junio</th>
								<th>Julio</th>
								<th>Agosto</th>
								<th>Septiembre</th>
								<th>Octubre</th>
								<th>Noviembre</th>
								<th>Diciembre</th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>
			</div>
		</form>	
		
		<div id="CargaDialog" title="Seleccion de pago" class="container">
			<iframe id="cargaFrm" width="695" height="600"></iframe>		
		</div>
		
	</body>
</html>