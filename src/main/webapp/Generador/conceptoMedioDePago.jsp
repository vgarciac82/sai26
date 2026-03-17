<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
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

String cUR = usuario.getU_UR();
String u_login = usuario.getLogin();

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
       
    <title>Registro Diario Banco</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
	
	<style type="text/css" title="currentStyle">
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "css/demo_table_jui.css";
		@import "css/demo_page.css";
		.lectura { 	background-color: #E9E9E9;  }
		
		.tbl{
				/*estilo de el cuerpo de nuestra tabla*/
				/*background-color: #F8F8FF;*/
				background-color: #F5F5F5;
				
				width:95%;
				margin:24px auto;
				border-left: 1px solid #ccc;
				border-right: 1px solid #ccc;
				
				border-color: 1px solid #ccc;
				font-family:helvetica,arial,sans-serif;
				font-weight:normal;
				/*text-transform: uppercase;*/
		}
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
$(document).ready( function(){
	
	querySelectPost("tRdbCat_Concepto","sConcepto", {async: false });
	querySelectPost("tRdbCat_MedioPago","sMedioPago", {async: false });
	$("#btnBuscar").click(function(){ buscarConceptoMedioPago(); });
	$("#btnAgregar").click(function(){ agregarConceptoMedioPago(); buscarConceptoMedioPago(); });
	
	 var tableCM = $("#tableCM").dataTable({   
		 				
		 					bSortClasses: false,
							ScrollY: "520px",
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
							aoColumnDefs:[
								{"bVisible":false, "aTargets":[0]}
							]
							
	 								
	 });
	 
	 $("#tableCM tbody").dblclick(function(event) {
		
		 if(confirm("¿ Desea Quitar el Registro ?")){
			 
				var aPos = tableCM.fnGetPosition( event.target.parentNode );
		     	var aData = tableCM.fnGetData( aPos );
		     	$("#activo").val(0);
		     	
		     	$("#idRdbCat").val(aData[0]);
		     	
		     	queryFormPost({
					queryName : "tRdbConceptoMedioPago_Update",
					async : false,
					callback : function() {
					
							alert("Eliminado Correctamente Concepto Con Medio de Pago");
							tableCM.fnDeleteRow( aPos);
					}		
				});
		}	
	});

});

function buscarConceptoMedioPago(){
	
	$('#tableCM').dataTable().fnClearTable();
	var camposWhere = " WHERE tCMP.activo = 1 ";
	var concepto = $("#sConcepto").val();
	var medio = $("#sMedioPago").val();
	var param = "";
	
	if(concepto > 0){
		
		camposWhere += " AND tCMP.sConcepto = "+concepto ;
		
	}if( medio > 0){
		
		camposWhere += " AND tCMP.sMedioPago = "+medio; 
	
	}
			
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"TRDBCAT_CONCEPTOMEDIOPAGO", Campos:camposWhere, Param:param, MaxReg: "5", ajax:"true" }, function(j){ 
															
						for(var i = 0; i < j.length; i++ ){
									
							$("#tableCM").dataTable().fnAddData([j[i].Col0,j[i].Col1,j[i].Col2]);			
									
						}
	});	
}

function agregarConceptoMedioPago(){
	
	if($("#sConcepto").val() > 0 && $("#sMedioPago").val() > 0){
		
		if(confirm("Seguro de Agregar Concepto con Medio de Pago")){
		
			
				var concepto = $("#sConcepto").val();
				var medio = $("#sMedioPago").val();
				var param = "";
				var datos = "sinDatos";
				
				var	camposWhere = " WHERE tCMP.sConcepto = "+concepto+" AND tCMP.sMedioPago = "+medio ;
					
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"TRDBCAT_CONCEPTOMEDIOPAGO", Campos:camposWhere, Param:param, MaxReg: "5", ajax:"true" }, function(j){ 
																	
								for(var i = 0; i < j.length; i++ ){
											
									$("#idRdbCat").val(j[i].Col0);
									var activo = j[i].Col3;
									datos = "datos";
									
								}
								
								if(datos == "sinDatos"){
										
										queryFormPost({
										queryName : "tRdbConceptoMedioPago_Create",
										async : false,
										callback : function() {
										
												alert("Guardado Correctamente Concepto Con Medio de Pago");
												
												}		
										});
										
								}else if(datos == "datos" && activo == 0){
									
									$("#activo").val(1);
									
									queryFormPost({
										queryName : "tRdbConceptoMedioPago_Update",
										async : false,
										callback : function() {
										
												alert("Guardado Correctamente Concepto Con Medio de Pago");
												
												}		
										});
									
								}else if(datos == "datos" && activo == 1){
									
									alert("Concepto y Medio de Pago ya Existe");
									
								}
								buscarConceptoMedioPago();
				});	
				
		}		
		
	}else{
		
		alert("Ingrese Concepto y Medio de Pago");
	}
	
}
</script>
</head>
<body id="dt_example" >
	<form id="frmConceptos" name="frmConceptos" >
			<div id="container" class="container SyCData" style="width:950px; align:center">
				<h1>Conceptos / Medios de Pago<label style="font-size: 9pt"></label></h1>
  					
  					<table id="tblGuardar">
  						<tr>
  							<td><input type="hidden" name="sConcep" id="sConcep" /></td>
  							<td><input type="hidden" name="sMedioPag" id="sMedioPag" /></td>
  							<td><input type="hidden" name="idRdbCat" id="idRdbCat" /></td>
  							<td><input type="hidden" name="activo" id="activo" /></td>
  						</tr>
  					</table>
  					<div id="dvConceptos">
  						
  						<fieldset>
  								<table height ="80px">
  									<tr>
  										<td>Concepto</td><td><select id="sConcepto" name="sConcepto" style="width:300px"></select></td>
  										<td>Medio de Pago</td><td><select id="sMedioPago" name="sMedioPago" style="width:300px"></select></td>
  										<td><input type="button" id="btnAgregar" name="btnAgregar" value="Agregar"></td>
  										<td><input type="button" id="btnBuscar" name="btnBuscar" value="Buscar"></td>
  									</tr>
  									
  								
  								</table>
  						</fieldset>
  								<table id="tableCM" width="900px"  >
									<tbody>
										<thead>
											<tr align="center">
												<th></th>
												<th>Concepto</th>
												<th>Medio de Pago</th>	
											
											</tr>
										</thead>	
									</tbody>
								</table>
  					</div>
  			</div>
  	</form>
 </body>
</html>
