<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
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
	String DATE_FORMAT = "dd-MM-yyyy";
	SimpleDateFormat fe = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = fe.format(c1.getTime());
	
	Calendar calendario = new GregorianCalendar();
	int hora = calendario.get(Calendar.HOUR_OF_DAY);
	int min = calendario.get(Calendar.MINUTE);
	int seg = calendario.get(Calendar.SECOND);
	
	String FechHora = today+ " " +hora+":"+min;
	

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
   
    <link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
	<style type="text/css" title="currentStyle">
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "css/demo_table_jui.css";
		@import "css/demo_page.css";
	</style>
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>	
	
<script type="text/javascript">
$(document).ready(function () {

	$("#btnExportaExcel").click(function() { $("#frmConsulta").submit(); limpiar(); } );
	/*var TableComparativo = $('#tablaComparativo').dataTable({         
			//iDisplayLength: 20,
			bSortClasses: false,
			ScrollY: "500px",
			sScrollX: "1000px",
			bPaginate: false,
        	bLengthChange: false,
        	bFilter: false,
        	bSort: true,
        	bInfo: false,
        	bAutoWidth: false,
			bJQueryUI: true,
			bRetrive : true,
			bDestroy : true,
			sPaginationType: "full_numbers",
			bScrollCollapse: true,
			//sScrollXInner: "100%",
			aaSorting: [[ 1, "asc" ]] ,
			bRetrive: true,
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
			aoColumnDefs:[{"bVisible":true, "aTargets":[1], "sClass":"right"},
						  {"aTargets":[2], "sClass":"right"},
						  {"sClass": "dt-center", "aTargets": ['dt-center']}
						 ] 
		});*/

	
	datosVista();  
	//tablaComparativo.$('tr:odd').css('backgroundColor', 'blue');
	
	tablaTotales();
});

function datosComparativo(){

	
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "V_EJERCIDOCOMPARATIVODATOS", Campos:"", Param:"", MaxReg: "5", ajax: 'true'}, function(j){
			
				for (var i = 0; i < j.length; i++) {
												
					aoColumns:[
							   	{ sName: j[i].Col0,	bSearchable: true,	bSortable: false, bVisible: true, sClass:"right"},
							   	{ sName: j[i].Col1,	bSearchable: false,	bSortable: false, bVisible: true, sWidth:"15px"},
							   	{ sName: j[i].Col2,	bSearchable: false,	bSortable: false, bVisible: true},
							   	{ sName: j[i].Col3,	bSearchable: false,	bSortable: false, bVisible: true},
							   	{ sName: j[i].Col4,	bSearchable: false,	bSortable: false, bVisible: true},
							   	{ sName: j[i].Col5,	bSearchable: false,	bSortable: false, bVisible: true}
					     	  ]
														
					$("#tablaComparativo").dataTable().fnAddData( [ j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3,j[i].Col4, j[i].Col5, j[i].Col6 ]);
				}
		});	
} 

function tablaTotales(){

	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "V_EJERCIDOCOMPARATIVO", Campos:"", Param:"", Order:"", MaxReg: "10", ajax: 'true'}, function(j){
	
		var sicop = 0.00;
		var totalEjercidoSICOP = 0.00;
		var cxp = 0.00;
		var totalEjercidoSAI = 0.00;
		var totalDiferencia = 0.00;
		
		for (var i = 0; i < j.length; i++) {
		    
		    sicop = j[i].Col0;
		    totalEjercidoSICOP = j[i].Col1;
		    cxp = j[i].Col2;
		    totalEjercidoSAI = j[i].Col3;
		    totalDiferencia = j[i].Col4;
		    		    
		}
		$("#sicop").html(sicop);
		$("#totalEjercidoSICOP").html(totalEjercidoSICOP);
		$("#cxp").html(cxp);
		$("#totalEjercidoSAI").html(totalEjercidoSAI);
		$("#totalDiferencia").html(totalDiferencia);
		
		
	});	

}

function datosVista(){

	$("#tablaComparativo").dataTable({
    					bAutoWidth : true,
						sScrollX: "100%",
						sScrollY: "500",
						bFilter: false,
						oLanguage: {
							sProcessing: "Procesando...",
							sLengthMenu: "Mostrar _MENU_ registros",
							sZeroRecords: "No hay registros a mostrar",
							sEmptyTable: "No hay datos en la tabla",
							sLoadingRecords: "Cargando...",
							sInfo: "Registros _START_ al _END_ de _TOTAL_",
							sInfoEmpty: "Registro 0 al 0 de 0",
							//sInfoFiltered: "(filtado de _MAX_ registros)",
							sInfoPostFix: "",
							sInfoThousands: ",",
							//sSearch: "Buscar:",
							oPaginate: {
								sFirst:    "Primero",
								sPrevious: "Ant.",
								sNext:     "Sigte.",
								sLast:     "&Uacute;ltimo"
							}
						},
						bPaginate: false,
						bRetrive: false,
						bDestroy: true,
						bServerSide: true,
						bProcessing: true,
						sPaginationType: "full_numbers",
						bJQueryUI: true,
						sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_EjercidoComparativo&qw=" + " UnidadResponsableSicop != '' " ,
						aaSorting: [[ 3, "asc" ]] ,
						aoColumns: [
							    { sName: "UnidadResponsableSicop", 		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
								{ sName: "sicop",	 		            bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"},
								{ sName: "Total_Ejercido_SICOP", 		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight" },
								{ sName: "UnidadResponsableCXP",	    bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight" },
								{ sName: "CXP",					        bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"},
								{ sName: "Total_Ejercido_SAI",  		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"},
								{ sName: "diferencia",			        bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight" }
							]
					});
	


	/*var tablaComparativo = $('#tablaComparativo').dataTable({         
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
							bFilter:false,
							
							//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_EjercidoComparativo&qw=" + " ORDER BY  = "+nFolioNOMINA+" "+concepto+" "+tMovimiento ,
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_EjercidoComparativo&qw=" + " UnidadResponsableSicop != '' " ,
							bProcessing: true,
							bJQueryUI: true,
							bAutoWidth : false,
							bRetrive: false,
							bDestroy: true,
        					bPaginate: false,
							iDisplayLength: 10,
		        			//sScrollY: "450px", 
		        			//sScrollX: "1000px",
		        			sScrollY: "500",
		        			sScrollX: "100%",
							Height: "450px", 
							aaSorting: [[ 3, "asc" ]] ,
							aoColumns: [
							    { sName: "UnidadResponsableSicop", 		bSearchable: false,	bSortable: false, bVisible: true, sClass:"right", sWidth:"35px" },
								{ sName: "sicop",	 		            bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
								{ sName: "Total_Ejercido_SICOP", 		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
								{ sName: "UnidadResponsableCXP",	    bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"35px" },
								{ sName: "CXP",					        bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"35px"},
								{ sName: "Total_Ejercido_SAI",  		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px"},
								{ sName: "diferencia",			        bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" }
							]
		       });*/

}
</script>
</head>
  
<body id="dt_example">
  	<form id="frmConsulta" name="frmConsulta" method="post" action="../gstnmngr/LayoutEjercidoComparativoServlet" target="_blank">
		<div   id="container" class="container SyCData" style="width:65%">
			<h1>Consulta Ejercido Comparativo<label style="font-size: 8pt"></label></h1>		
			
			<div id="tabsl">    
				<fieldset>
						<legend>Comision Nacional Forestal Comparativo Del Ejercido</legend>
						<table align="right" >
							<td><%=FechHora%></td>
							<td><input type="button" id="btnExportaExcel" name="btnExportaExcel" value="Exportar Excel" /></td>
							<td><input type="hidden" name="fecha" id="fecha" value="<%=FechHora%>"/></td>
							<td><input type="hidden" name="tipoConsulta" id="tipoConsulta" value="consultaEjercidoComparativo"/></td>
						</table>
						<table id="tablaComparativo" class="display">
							
								<thead>
									<tr align="center">
										<th align="center">Unidad Sicop</th>
										<th align="center">Num CLC Sicop</th>
										<th>Total Sicop</th>
										<th>Unidad SAI</th>
										<th>Num CXP SAI</th>
										<th>Total SAI</th>
										<th>Diferencia</th>
									</tr>
								</thead>
							
						</table>	
				</fieldset>
				<fieldset>
				
					<legend>Totales</legend>
					
						<table width="915px" align="center" height="70px" >
							
							
							<td style="font-size: 11pt">Num Total CLC's Sicop: <div id="sicop" style="font-size: 12pt"/>0.00</div></td>
							<td style="font-size: 11pt">Importe Total Sicop: <div id="totalEjercidoSICOP" style="font-size: 12pt"/>0.00</div></td>
							<td style="font-size: 11pt">Num Total CXP's SAI: <div id="cxp" style="font-size: 12pt"/>0.00</div></td>
							<td style="font-size: 11pt">Importe Total SAI: <div id="totalEjercidoSAI" style="font-size: 12pt"/>0.00</div></td>
							<td style="font-size: 11pt">Total Diferencia: <div id="totalDiferencia" style="font-size: 12pt"/>0.00</div></td>
							
						</table>
				</fieldset>
			</div>
		</div>
	</form>	
</body>
</html>
