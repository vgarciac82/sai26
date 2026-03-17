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
String DATE_FORMAT = "dd/MM/yyyy";
SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
Calendar c1 = Calendar.getInstance(); // today
String today = sdf.format(c1.getTime());

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    
    
    <title>My JSP 'consultaEjercidoPagadoLogs.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<style type="text/css" title="currentStyle">
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "css/demo_table_jui.css";
		@import "css/demo_page.css";
	</style>
	<style media="all" type="text/css">     
		.alignRight { text-align: right; } 
		.alignCenter { text-align: center; }
	</style> 
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	
<script type="text/javascript">
$(document).ready(function(){
	
	$("#btnConsulta").click(function () { consultaEjercidoPagado(); });
	$("#btnLimpiar").click(function () { $("#movimientosLogs").dataTable().fnClearTable();  });
	
	$( "#txtfechIni" ).datepicker({
				showOn: "button",
				dateFormat: "dd/mm/yy",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true,
				changeYear: true,
				changeMonth: true
	});
	
	$( "#txtfechFin" ).datepicker({
				showOn: "button",
				dateFormat: "dd/mm/yy",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true,
				changeYear: true,
				changeMonth: true
	});
	
	
		var tablaCierre = $("#movimientosLogs").dataTable({         
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
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tDetalleEjercidoPagado&qw=" + " caNoContrarrecibo = '0' ",
							bProcessing: true,
							bJQueryUI: true,
							bAutoWidth : true,
							bRetrive: true,
							bDestroy: true,
				    		bPaginate: false,
				    		iDisplayLength: 10,
				      			sScrollY: "450px", 
				      			sScrollX: "1200px",
				      			Height: "450px",
				      			Width: "1200px"
				      		
				      		
		        });	
			
	
	
	var TableMov = $('#movimientosLogs').dataTable({         
			sScrollY: "500px",
			sScrollX: "1230px",
			//bPaginate: false,
        	bLengthChange: false,
        	bFilter: true,
        	bSort: true,
        	bInfo: true,
        	bAutoWidth: true,
			bJQueryUI: true,
			bRetrive : true,
			bDestroy : true,
			aaSorting: [[ 1, "asc" ]] ,
			sPaginationType: "full_numbers",
			iDisplayLength: 20,
   			sScrollY: "500px", 
   			sScrollX: "1220px",
   			Height: "500px",
   			Width: "1220px",
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
			aoColumns: [
						{  "sWidth": "8%", sClass: "alignCenter"},
						{  "sWidth": "10%", sClass: "alignCenter"},
						{  "sWidth": "12%", sClass: "alignCenter"},
						{  "sWidth": "8%", sClass: "alignCenter"},
						{  "sWidth": "10%", sClass: "alignCenter"},
						{  "sWidth": "20%", sClass: "alignCenter"},
						{  "sWidth": "12%", sClass: "alignRight"},
						{  "sWidth": "25%", sClass: "alignLeft"}
			]
			
		});
	

});


function consultaEjercidoPagado(){
	
	$("#esperar").attr("style","visibility=visible");

	$("#movimientosLogs").dataTable().fnClearTable();
	var where = "";
	
	if($("#txtCXP").val() != "" ){ 
	
		where += " AND caNoContrarrecibo = '"+$("#txtCXP").val()+"'";
	}
	
	if($("#txtfechIni").val() && $("#txtfechFin").val()){
	
		var fI = $("#txtfechIni").val().split("/");
		var fF = $("#txtfechFin").val().split("/");
		
		var fechIni = fI[2]+"-"+fI[1]+"-"+fI[0];
		var fechFin = fF[2]+"-"+fF[1]+"-"+fF[0];
		
		where += " AND fechaAplicacion BETWEEN '"+fechIni+"' AND '"+fechFin+"'";
	
	}
	
	var szTabla = "tDetalleEjercidoPagado";
	var camposWhere = where; 
	var elParametro = "";
	var order = "";
	var datos = "sinDatos";
									
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:camposWhere, Param:elParametro, Order:order, MaxReg: "100", ajax: 'true'}, function(j){
	
		var ep = "-";
		var arrlist = new Array();
		
		for (var i = 0; i < j.length; i++) {
		    
		    ep = j[i].Col5;
		    
		    if( ep == "null" ){ ep = "-"; }
		
			arrlist[ i ] = [ j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, ep, j[i].Col6, j[i].Col7 ];
			datos = "datos";

		}
		$("#movimientosLogs").dataTable().fnAddData( arrlist );
		if(datos == "sinDatos"){
			
			alert("No Se encontro Informacion");
		}
		document.getElementById("esperar").style.visibility = 'hidden';
	});	

}


function consultaEjercidoPagado(){
	
	$("#movimientosLogs").dataTable().fnClearTable();
	var where = " 1=1 ";
	
	if($("#txtCXP").val() != "" ){ 
	
		where += " AND caNoContrarrecibo = '"+$("#txtCXP").val()+"'";
	}
	
	if($("#txtfechIni").val() && $("#txtfechFin").val()){
	
		var fI = $("#txtfechIni").val().split("/");
		var fF = $("#txtfechFin").val().split("/");
		
		var fechIni = fI[2]+"-"+fI[1]+"-"+fI[0];
		var fechFin = fF[2]+"-"+fF[1]+"-"+fF[0];
		
		where += " AND fechaAplicacion BETWEEN '"+fechIni+"' AND '"+fechFin+"'";
	
	}
	alert(where);
	var oTable = $('#movimientosLogs').dataTable();
	oTable.fnClearTable();
	$('#movimientosLogs').dataTable({         
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
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tDetalleEjercidoPagado&qw=" + where,
							bProcessing: true,
							bJQueryUI: true,
							bAutoWidth : true,
							bRetrive: true,
							bDestroy: true,
        					bPaginate: false,
							iDisplayLength: 10,
		        			sScrollY: "450px", 
		        			sScrollX: "1000px",
		        			Height: "450px", 
							aaSorting: [[ 3, "asc" ]] ,
							aoColumns: [
							    { sName: "tipoDocumento", 		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"25px"},
								{ sName: "cTipoPago",	 		bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"25px"},
								{ sName: "caNoContrarrecibo", 	bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignRight", sWidth:"27px"},
								{ sName: "clcSicop",	        bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"32px"},
								{ sName: "fechaAplicacion",		bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignLeft", sWidth:"102px" },
								{ sName: "EP",  				bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignCenter", sWidth:"22px"},
								{ sName: "importe",				bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignLeft", sWidth:"22px"},
								{ sName: "descripcion",			bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"20px"}
								
							]
							
		        });	
	
} 


</script>	
</head>
<body id="dt_example">
  	<form id="frmConsulta">
		<div   id="container" class="container SyCData" style="width:85%">
			<h1>Consulta Ejercido / Pagado Logs<label style="font-size: 8pt"></label></h1>		
			
			<div id="tabsl"> 
			
				<label id="esperar" style="visibility: hidden">
									<div align="center">Espere por favor....
									  <img border="0" src="../imagenes/espera.gif" height="30">
									</div>
				</label>
				   
				<fieldset>		
	        		<table id="tblEjecidoPagado" align="center" width="900px" height="60">	
						
						<td>No Cuenta Por Pagar<input type="text" name="txtCXP" id="txtCXP"/></td>
						<td>Fecha Inicio<input type="text" name="txtfechIni" id="txtfechIni" value="<%=today%>" size="10" readonly="readonly" /></td>
						<td>Fecha Final<input type="text" name="txtfechFin" id="txtfechFin" value="<%=today%>" size="10" readonly="readonly" /></td>
						<td><input type="button" name="btnConsulta" id="btnConsulta" value="Buscar"/></td>
						<td><input type="button" name="btnLimpiar" id="btnLimpiar" value="Limpiar" onclick="this.form.reset();" /></td>
				
					</table>
				</fieldset>
				<tr><td>&nbsp;</td></tr>
				<fieldset>
					<legend>Detalles del Logs</legend>
					<table id="movimientosLogs" width="1000px">
						<tbody>
							<thead>
							
								<tr align="center">
									<th>-</th>
									<th>Tipo Pago</th>
									<th>CXP</th>
									<th>Sicop</th>
									<th>Fecha</th>
									<th>Estructura Programatica</th>
									<th>Importe</th>
									<th>Descripcion</th>
									
								</tr>
							</thead>
						</tbody>
					</table>	
				</fieldset>
			</div>
		</div>	
	</form>	
 </body>
</html>
