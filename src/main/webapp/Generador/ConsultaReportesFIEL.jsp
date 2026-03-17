<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%
Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
if (usuario == null) {
	response.sendRedirect("../index.jsp");
	return;
}

Empleado e = new Empleado();
EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
e.setClaveUsuario(usuario.getLogin());
e = ebl.getEmpleado(e);
EmpleadoArea ea = new EmpleadoArea();
ea.setId(e.getClaveArea());
ea = ebl.getEmpleadoArea(ea);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Consulta de Reportes con Firma Electronica</title>
		
		<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
		<script src="../Generador/js/bootstrap.bundle.min.js"></script>
		<link rel="stylesheet" type="text/css" href="../Generador/css/datatables.min.css"></link>
		
		<style type="text/css" title="currentStyle">
		@import "css/demo_page.css";
		@import "css/demo_table_jui.css";
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		</style>
		
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>
		
		<script type="text/javascript" charset="utf-8">
			
			var oTableReporte;
			var oTableFirmantes;
			
			$(document).ready(function() {
				oTableReporte = $('#tblResumen').dataTable();
				$("input.AyudaSyC").subIniciaDlg();
				$("input.autoCompletaSyC").subIniciaAutoCompleta();
				creaTablaResumen(); 
				creaTablaFirmantes(-1);
			});
			
			function openCenteredWindow(url, name, height, width, parms) {
				var left = Math.floor((screen.width - width) / 2);
				var top = Math.floor((screen.height - height) / 2);
				var winParms = "top=" + top + ",left=" + left + ",height=" + height + ",width=" + width + ",scrollbar=yes,status=yes";
				if (parms) {
					winParms += "," + parms;
				}
				var win = window.open(url, name, winParms);
				if (parseInt(navigator.appVersion) >= 4) {
					win.window.focus();
				}
				return win;
			}
			
			function abreReporte(folioReporte){
				openCenteredWindow("../firma/MuestraReporte?T=R&f="+folioReporte, "Reporte", 800, 1024, "scrollbars=yes,resizable=yes");
			}
			
			function abreAcuse(folioReporte){
				openCenteredWindow("../firma/MuestraReporte?T=A&f="+folioReporte, "Reporte", 800, 1024, "scrollbars=yes,resizable=yes");
			}
			
			function cancelaReporte(folioReporte){
				var seguro=confirm("¿ Esta seguro de cancelar el Estado Financiero firmado (Es un proceso que NO se puede revertir)?");
 				     $("#folioReporte").val(folioReporte);
 				     if (seguro==true){	
						queryFormPost({ queryName:"updateCancelaReporteFiel",async:false, callback:function(){creaTablaResumen();}   });
					}			
			}
			
			
			function creaTablaFirmantes(folioReporte){
				
				oTableFirmantes= $('#tblFirmantes').dataTable({
					bPaginate : false,
					bLengthChange : false,
					bFilter : false,
					bInfo : false,
					fnInitComplete: function() {   
						oTableFirmantes.fnAdjustColumnSizing();
	    			},
					oLanguage : {
						sProcessing : "Procesando...",
						sLengthMenu : "Mostrar _MENU_ registros",
						sZeroRecords : "No hay registros a mostrar",
						sEmptyTable : "No hay datos en la tabla",
						sLoadingRecords : "Cargando...",
						sInfo : "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty : "Registro 0 al 0 de 0",
						sInfoFiltered : "(filtado de _MAX_ registros)",
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
					bAutoWidth : false,
					sScrollX : "100%",
					sScrollY : 100,					
					bScrollCollapse : true,
					bJQueryUI : true,
					bDestroy : true,
					bServerSide : true,
					sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vfirmantereporte&qw=nidedofinanciero="+folioReporte,
					aoColumns : [
						{
							sName : "norden"
						},
						{
							sName : "nombrefirmante"
						},
						{
							sName : "cargo"
						},
						{
							sName : "cdesctipofirmante"
						},
						{
							sName : "estatus"
						} ] 
				}) ;
			}
			
			function creaTablaResumen() {
				
				oTableReporte = $('#tblResumen').dataTable({
					bPaginate : false,
					bLengthChange : false,
					bFilter : true,
					bInfo : false,
					fnInitComplete: function() {   
						oTableReporte.fnAdjustColumnSizing();
	    			},
					oLanguage : {
						sProcessing : "Procesando...",
						sLengthMenu : "Mostrar _MENU_ registros",
						sZeroRecords : "No hay registros a mostrar",
						sEmptyTable : "No hay datos en la tabla",
						sLoadingRecords : "Cargando...",
						sInfo : "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty : "Registro 0 al 0 de 0",
						sInfoFiltered : "(filtado de _MAX_ registros)",
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
					bAutoWidth : false,
					sScrollX : 100,
					sScrollY : 250,
					bScrollCollapse : true,
					bJQueryUI : true,
					bDestroy : true,
					bServerSide : true,
					sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vReporteFIELDT&qw=1=1",
					aoColumns : [
						{
							sName : "nIDEdoFinanciero"
						},
						{
							sName : "cDescReporte"
						},
						{
							sName : "nMes"
						},
						{
							sName : "cDescMoneda"
						},
						{
							sName : "cDescNivel"
						},
						{
							sName : "cEstatus"
						},
						{
							sName : "ligaReporte"
						},
						{
							sName : "ligaAcuse"
						} ,
						{
							sName : "cancelar"
						} ] 
				}) ;
				
				$("#tblResumen tbody").click(function(event) {
					var currIndex;
					
					$(oTableReporte.fnSettings().aoData).each(function() {
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
					
					var aPos = oTableReporte.fnGetPosition(event.target.parentNode);

					if (aPos instanceof Array)
						currIndex = aPos[0];
					else
						currIndex = aPos;
					
					var folio = $("#tblResumen").dataTable().fnGetData()[currIndex][0];
					creaTablaFirmantes(folio);
				});
			}
		</script>
	</head>

	<body id="dt_example">
	<br/>
		<form id="reporteFrm" name="reporteFrm">
			<div id="container" class="container" style="width: 80%">
				<input type="hidden" Id="folioReporte" name="folioReporte">
											
				<div class="card-header"> <h3> Estados Financieros Generados </h3> </div>
				<hr class="mt-3"/>
				
				<span>
					<b>Seleccione el reporte para ver el detalle de sus firmantes.</b>
				</span>			
						
				<div id="resumen" class="table-responsive">	  
					<table id="tblResumen" class="table table-striped">
						<thead>
							<tr align="center">
								<th>Folio</th>
								<th>Reporte</th>
								<th>Mes</th>
								<th>Moneda</th>
								<th>Nivel</th>
								<th>Estatus</th>
								<th>Reporte</th>
								<th>Acuse</th>
								<th>&nbsp;</th>
							</tr>
						</thead>
					</table>		
				</div>
			
				<br/>
								
				<div id="firmantes" class="table-responsive">	  
					<table id="tblFirmantes" class="table table-striped">
						<thead>
							<tr align="center">
								<th>#</th>
								<th>Firmante</th>
								<th>Cargo</th>
								<th>Tipo</th>
								<th>Estatus</th>
							</tr>
						</thead>
					</table>
				</div>
			
			</div>			
		</form>
	</body>
</html>
