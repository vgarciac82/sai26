<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
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
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
       
    <title>Consulta Saldos Compromiso Capitulo Mil</title>
    
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
$(document).ready(function (){

	$("#btnBuscar").click(function() { buscarCompromiso();});
	$("#btnNuevo").click(function() { if(confirm("¿ Desea Hacer Nueva Consulta ?" )){	location.reload();	} });
	$("#btnExportaExcel").click(function() { $("#frmCapituloMilDevComp").submit();  } );
	$("#btnExportaPdf").click(function() { exportarPDF();  } );
	
	var tablaCierre = $('#tablaCompromiso').dataTable({         
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
							bFilter: false,
							bInfo: true,
							//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_NominaCapituloMil&qw=" + " caNoContrarrecibo = '0' ",
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_CompromisoDevCLC&qw=" + " caNoCompromiso = '0' ",
							bProcessing: false,
							bJQueryUI: true,
							bAutoWidth : true,
							bRetrive: true,
							bDestroy: true,
				    		bPaginate: false,
				    		iDisplayLength: 10,
				      		sScrollY: "600px", 
				      		sScrollX: "1050px",
				      		Height: "600px",
				      		Width: "1050px"
				 });
});

function buscarCompromiso(){
	
	$("#esperar").attr("style","visibility=visible");
	
	var compromiso = $("#compromiso").val();
	var camposWhere = " retencionTe > 0.00 ";
	var szTabla = "v_CompromisoDevCLC";
	var statusDoc = "";
	var oTable = $('#tablaCompromiso').dataTable();
	//var camposWhere = " AND retencionTe > 0.00 ";
	oTable.fnClearTable();
	
	if(compromiso != ""){
			
		 	camposWhere += " and caNoCompromiso = '"+compromiso+"' ";
	}
	if($("#estatus").val() != "todos"){
		
				camposWhere += " and cDocumentoHaplicado = '"+$("#estatus").val()+"' ";
				estatusTxt = " and cDocumentoHaplicado = '"+$("#estatus").val()+"' ";
				$("#estatusTxt").val(estatusTxt);
	}
	
	var elParametro = "";
	var order = "";
			 
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:camposWhere, Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j){
						
							for (var i = 0; i < j.length; i++){
								
								var importeTotal = j[i].Col0;
								$("#importeTotal").html(importeTotal);
								$("#importeTotal").formatCurrency();
							}	
							
			var oTable = $('#tablaCompromiso').dataTable();
			oTable.fnClearTable();
	
			var retencionTe = "retencionTe";
	
			$('#tablaCompromiso').dataTable({         
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
							bFilter: false,
							bInfo: true,
							bServerSide: true,
							//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_CompromisoDevCLC&qw=" + " caNoCompromiso = '"+compromiso+"' AND  retencionTe > 0.00",
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_CompromisoDevCLC&qw=" +camposWhere ,
							bProcessing: true,
							bJQueryUI: true,
							bAutoWidth : false,
							bRetrive: true,
							bDestroy: true,
        					bPaginate: false,
							iDisplayLength: 10,
		        			sScrollY: "600px", 
		        			sScrollX: "1050px",
		        			Height: "600px", 
		        			Width: "1050px",
							aaSorting: [[ 0, "asc" ]] ,
							aoColumns: [
								{ sName: "renglon",			        bSearchable: true,	bSortable: true, bVisible: true, sClass: "center", sWidth:"32px"},
							    { sName: "caNoCompromiso", 		bSearchable: true,	bSortable: false, bVisible: true, sClass: "center",    sWidth:"20px"},
								{ sName: "nFolioCompromisoNomina", 	bSearchable: true,	bSortable: true, bVisible: true, sClass: "center",  sWidth:"80px"},
								{ sName: "EPtxt",						bSearchable: true,	bSortable: true	, bVisible: true, sClass: "center", sWidth:"402px" },
								{ sName: "cMestxt",					bSearchable: true,	bSortable: true	, bVisible: true, sClass: "center", sWidth:"30px" },
								{ sName: "retencionTeFormato",		bSearchable: false,	bSortable: true, bVisible: true, sClass: "center", sWidth:"32px"},
								{ sName: "cDocumentoHaplicado",		bSearchable: false,	bSortable: true, bVisible: true, sClass: "left", sWidth:"32px"}
							]		
		        });	
			
			document.getElementById("esperar").style.visibility = 'hidden';
	});
}
function exportarPDF(){
	
	var whereCampos = " retencionTe > 0.00 ";
	whereCampos += ($("#compromiso").val() != "" ) ? " and caNoCompromiso = '"+$("#compromiso").val()+"'" : " " ;
	whereCampos += ($("#estatusTxt").val() != "" ) ? " and cDocumentoHaplicado = '"+$("#estatus").val()+"'" : " ";
	
	window.open(	"../admin/SeguridadCatalogos?"
					+ "catalogo=CONTRARECIBO"
					+ "&accion=run"
					+ "&rn=saldosCompromisoCapituloMil.jasper"
					+ "&whereCampos=" + whereCampos + " ORDER BY renglon ASC ",
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768"
				);
}

</script>
</head>
<body id="dt_example">
<form id="frmCapituloMilDevComp" name="frmCapituloMilDevComp" method="post" action="../gstnmngr/LayoutGeneral" target="_blank">
  			<div id="container" class="container SyCData" style="width:1080px; align:center">
  			
  				<h1>Consulta Compromiso Capitulo Mil<label style="font-size: 9pt"></label></h1>
  						
  									<div id="tabsl" style="width:1080px; align:center"> 
  									<label id="esperar" style="visibility: hidden">
										<div align="center">Espere por favor....
									  		<img border="0" src="../imagenes/espera.gif" height="30">
										</div>
									</label>    
  									
										  	<fieldset> 
										  		
											  		<table id="tbl" width="500px" height ="90px">
											  											  											  		
											  			<tr>
															<td>Compromiso</td>
															<td align="left" colspan="8"><input type="text" name="compromiso" id="compromiso" size=20 ></td>
															<td><input type="button" name="btnBuscar" id="btnBuscar" value="Buscar" ></td>
															<td><input type="button" name="btnNuevo" id="btnNuevo" value="Nuevo" ></td>
															<td><input type="button" name="btnExportaExcel" id="btnExportaExcel" value="Excel" ></td>
															<td><input type="button" name="btnExportaPdf" id="btnExportaPdf" value="PDF" ></td>
															<td><input type="hidden" name="tipoConsulta" id="tipoConsulta" value="consultaSaldosCompromisoCapituloMil"></td>
															<td><input type="hidden" name="estatusTxt" id="estatusTxt" ></td>
														</tr>
														<tr>
											  				<td>Estatus</td>
											  				<td colspan="3"><select id="estatus" name="estatus">
											  						<option value="todos">Todos</option>
											  						<option value="Aplicado">Aplicado</option>
											  						<option value="Sin Aplicar">Sin Aplicar</option>
											  						<option value="Cancelado">Cancelado</option>
											  					</select>
											  				</td>
											  			</tr>
														
											  		</table>
										  	</fieldset>	
								   </div>
								   <div style="width:1080px; align:center">
									  		<br>
									  		<fieldset>
												<legend>Datos Compromiso</legend>
													<div align="right"> Total Compromiso </div><div id="importeTotal" align="right" style="font-size: 12pt">0.00</div>
											  		</br>
											  		<table id="tbl">	
														<tr>	
																<td>
																	<table id="tablaCompromiso" class="display">
																		<tbody>
																			<thead>
																				<tr align="center">
																					<th>Numero</th>
																					<th>Compromiso</th>
																					<th>Folio Compromiso Nomina</th>
																					<th>Estructura Programatica</th>
																					<th>Mes</th>
																					<th>Importe</th>
																					<th>Estatus</th>
																				</tr>	
																			</thead>							
																		</tbody>	
																	</table>						
																</td>
														</tr>
												   </table>
										   </fieldset>
								 </div>
				</div>
	</form>
</body>
</html>
