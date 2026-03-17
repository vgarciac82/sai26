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
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
  
  	<title>Consulta Saldos Capitulo Mil</title> 
  	
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
	<script type="text/javascript" src="../Generador/js/Moment.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
 
 <script type="text/javascript">
$(document).ready(function (){
	
	inittable()
	
	$("#btnBuscar").button();
	$("#btnNuevo").button();
	$("#btnExportaExcel").button();
	$("#btnExportaPdf").button();	
	
	$("#btnBuscar").click(function() { buscarCxp(); });
	$("#btnNuevo").click(function() { if(confirm(" ¿ Desea Hacer Nueva Consulta ?" )){ 	location.reload();  } });
	$("#btnExportaExcel").click(function() {     $("#frmCapituloMil").submit();  } );
	$("#btnExportaPdf").click(function() { exportarPDF();  } );
	//cargaDT();
});

var valorConcepto = "";
var idCXP  = "";
var tmovimiento  = "";
var table;

function buscarCxp(){
	
	$("#conceptoFiltro").val("");
	$("#movimientoFiltro").val("");
	
	valorConcepto = "";
	var concepto = "";
	var datos = "sinDatos";
	idCXP = $("#caNoRecibo").val();
	var elParametro = "caNoContrarrecibo = '"+idCXP+"'";
	var caNoCompromiso = "";
	tmovimiento = $("#tmovimiento").val();
	var movi = "";
		
	if($("#checkAP").is(":checked")){
			concepto += "'AP',";
	}if($("#checkPT").is(":checked")){
			concepto += "'PT',";
	}if($("#checkPP").is(":checked")){
			concepto += "'PP',";
	}if($("#checkPN").is(":checked")){
			concepto += "'PN',";
	}if($("#checkPI").is(":checked")){
			concepto += "'PI',";
	}if($("#checkFR").is(":checked")){
			concepto += "'FR',";
	}
	
	if(concepto != ""){
	
			var max = concepto.length;
			concepto = concepto.substring(0,max - 1);
			valorConcepto = "AND ID_TIPO_CONCEPTO IN ("+concepto+")";
			
	}
	if(tmovimiento != ""){
		
			var tmov = tmovimiento.split(",");
			var count = tmov.length;
						
			for(var i = 0; i < count; i++ )
			{
				movi += "'"+tmov[i]+"'";
				movi += ",";
			}	
				
			tiposMovi = movi.substring(0, movi.length - 1);
			tmovimiento = " AND ID_TIPO_MOVIMIENTO IN ("+tiposMovi+")";
			
	}
	
	$("#conceptoFiltro").val(valorConcepto);
	$("#movimientoFiltro").val(tmovimiento);
						
	buscarCXPVista(idCXP, valorConcepto, tmovimiento);
	
}
var table;

function inittable(){
	/*Inicializar tabla*/
	table= $("#tabla").dataTable({
				"bLengthChange" : true,
	           "bFilter" : true,
	           "bSort" : true,
	           "bInfo" : true,
	           "bPaginate" : true,
	           "bAutoWidth" : false,
	           "bScrollCollapse" : true,
	           "sScrollXInner": "100%",        		    		
	           "sPaginationType" : "full_numbers",
	           "bJQueryUI" : true,
	           "bRetrive" : true,
	           "bDestroy" : true,
	           "bServerSide": true,                   
				"iDisplayLength": 25,	       
       			"fnInitComplete": function() {   
       				oTable.fnAdjustColumnSizing();
    			},
               oLanguage : {
               	    sProcessing: "Procesando...",
          			sLengthMenu: "Mostrar _MENU_ registros",
          			sZeroRecords: "No hay registros a mostrar",
          			sEmptyTable: "No hay datos en la tabla",
          			sLoadingRecords: "Cargando...",
          			sInfo: "Registros _START_ al _END_ de _TOTAL_",
          			sInfoEmpty: "Registro 0 al 0 de 0",
          			sInfoFiltered: "(filtado de _MAX_ registros)",
          			sInfoPostFix: "",
          			sInfoThousands: ",",
          			sSearch: "Buscar:",
          			oPaginate: {
          				sFirst:    "Primero",
          				sPrevious: "Ant.",
          				sNext:     "Sigte.",
          				sLast:     "&Uacute;ltimo"
                      }
               }
        });    
	
	
}

function buscarCXPVista(idCXP, concepto, tMovimiento){
	
	$("#conceptoFiltro").val(concepto);
	$("#movimientoFiltro").val(tMovimiento);
	
	var estatusTxt = " ";
	
	var szTabla = "V_NOMINACAPITULOMILDEV";
	var caNoContrarrecibo = "";
	
	if(idCXP != ""){ caNoContrarrecibo = " and caNoContrarreciboTxt = '"+idCXP+"'";  }
	
	if($("#estatus").val() != "todos"){		
		
		estatusTxt = " and estatus = '"+$("#estatus").val()+"' ";
		$("#estatusTxt").val(estatusTxt);
	}
	
	
	var camposWhere = "1 = 1" +caNoContrarrecibo+ " " +concepto+ " " +tMovimiento+ " "+estatusTxt;
	var elParametro = "";
	var order = "";
	
	$("#importeTotal").html(0.00);
	
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:camposWhere, Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j){
				
					for (var i = 0; i < j.length; i++){
						
						var importeTotal = j[i].Col0;
						$("#importeTotal").html(importeTotal);
						$("#importeTotal").formatCurrency();
					}		
	 

			table = $('#tabla').dataTable({         
						"bLengthChange" : true,
			            "bFilter" : true,
			            "bSort" : true,
			            "bInfo" : true,
			            "bPaginate" : true,
			            "bAutoWidth" : false,
			            "bScrollCollapse" : true,
			            "sScrollXInner": "100%",        		    		
			            "sPaginationType" : "full_numbers",
			            "bJQueryUI" : true,
			            "bRetrive" : true,
			            "bDestroy" : true,
			            "bServerSide": true,                   
						"iDisplayLength": 25,	       
						"fnInitComplete": function() {   
							table.fnAdjustColumnSizing();
							},
						"iDisplayLength": 25, 
						oLanguage: {
							sProcessing: "Procesando...",
							sLengthMenu: "Mostrar _MENU_ registros",
							sZeroRecords: "No hay registros a mostrar",
							sEmptyTable: "No hay datos en la tabla",
							sLoadingRecords: "Cargando...",
							sInfo: "Registros _START_ al _END_ de _TOTAL_",
							sInfoEmpty: "Registro 0 al 0 de 0",
							sInfoFiltered: "(filtado de _MAX_ registros)",
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
		        			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_NominaCapituloMilDev&qw=importeNeto > 0 "+caNoContrarrecibo+" "+concepto+" "+tMovimiento+ " "+estatusTxt ,
							aaSorting: [[ 3, "asc" ]] ,
							aoColumns: [
							    { sName: "caNoContrarreciboTxt",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"},
								{ sName: "EPTxt",					bSearchable: true,	bSortable: true	, bVisible: true, sClass: "center"},
								{ sName: "cMesTxt",  				bSearchable: true,	bSortable: true	, bVisible: true, sClass: "center"},
								{ sName: "ID_TIPO_MOVIMIENTO",			bSearchable: true,	bSortable: true	, bVisible: true, sClass: "center"},
								{ sName: "ID_TIPO_CONCEPTO",			bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"},
								{ sName: "mImporteNetoDetFormato",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "center"},
								{ sName: "caNoCompromisoTxt",		bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"},
								{ sName: "estatus",		        bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"}
								]
		        			
					
				});	
		
			});

}

function cargaDT(){
	table = $('#tabla').dataTable({         
		   "bLengthChange" : true,
           "bFilter" : true,
           "bSort" : true,
           "bInfo" : true,
           "bPaginate" : true,
           "bAutoWidth" : false,
           "bScrollCollapse" : true,
           "sScrollXInner": "100%",        		    		
           "sPaginationType" : "full_numbers",
           "bJQueryUI" : true,
           "bRetrive" : true,
           "bDestroy" : true,
           "bServerSide": true,                   
			"iDisplayLength": 25,	       
			"fnInitComplete": function() {   
			table.fnAdjustColumnSizing();
			},
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay datos en la tabla",
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0",
			sInfoFiltered: "(filtado de _MAX_ registros)",
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
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_NominaCapituloMilDev&qw=importeNeto > 0 and 1 != 1",
			aaSorting: [[ 3, "asc" ]] ,
			aoColumns: [
			    { sName: "caNoContrarreciboTxt",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"},
				{ sName: "EPTxt",					bSearchable: true,	bSortable: true	, bVisible: true, sClass: "center"},
				{ sName: "cMesTxt",  				bSearchable: true,	bSortable: true	, bVisible: true, sClass: "center"},
				{ sName: "ID_TIPO_MOVIMIENTO",			bSearchable: true,	bSortable: true	, bVisible: true, sClass: "center"},
				{ sName: "ID_TIPO_CONCEPTO",			bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"},
				{ sName: "mImporteNetoDetFormato",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "center"},
				{ sName: "caNoCompromisoTxt",		bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"},
				{ sName: "estatus",		        bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"}
				]
			
	
});	
}

function exportarPDF(){
	
	whereCampos = "  1 = 1 ";
	
	if($("#caNoRecibo").val() != ""){
		
		whereCampos += " AND caNoContrarreciboTxt = '"+$("#caNoRecibo").val()+"'";	
	
	}if($("#conceptoFiltro").val() != ""){
		
		//whereCampos += (whereCampos =! "" )? " AND " : "";
		whereCampos += $("#conceptoFiltro").val();
		
	}if($("#movimientoFiltro").val() != "" ){
		
		//whereCampos += (whereCampos =! "" )? " AND " : "";
		whereCampos += $("#movimientoFiltro").val();
	
	}if($("#estatus").val() != "" ){
		
		//whereCampos += (whereCampos =! "" )? " AND " : "";
		whereCampos += $("#estatusTxt").val();
	
	}
	
	window.open("../admin/SeguridadCatalogos?"
					+ "catalogo=CONTRARECIBO"
					+ "&accion=run"
					+ "&rn=saldosCapituloMil.jasper"
					+ "&caNoContrarrecibo=" + whereCampos,
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768"
				);
}
</script>
</head>
<body id="dt_example">
<br/>
<form id="frmCapituloMil" name="frmCapituloMil" method="post" action="../gstnmngr/LayoutGeneral" target="_blank">
	<input type="hidden" name="tipoConsulta" id="tipoConsulta" value="consultaSaldosCapituloMil">
	<input type="hidden" name="conceptoFiltro" id="conceptoFiltro" >
	<input type="hidden" name="movimientoFiltro" id="movimientoFiltro" >
	<input type="hidden" name="estatusTxt" id="estatusTxt" >
	
	<div id="container" class="container" style="width:90%; align:center">
		<div class="card-header"> <h3> Consulta Capitulo Mil </h3> </div>
		<hr class="mt-3"/>	
  				
  		<h6><b> Buscar Datos </b></h6>
  		<hr class="mt-3">
  		  		
  		<div class="row d-flex">				
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				<label class="form-label">Cuenta Por Pagar</label>											
			</div>					
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				<input type="text" name="caNoRecibo" id="caNoRecibo" class="form-control form-control-sm" size=30 placeholder="10CP2022012345"/>	
			</div>
		</div>
		
		<div class="row d-flex">				
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				<label class="form-label">Concepto</label>											
			</div>					
			<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				<input type="checkbox" name="checkAP" id="checkAP" class="form-check-input" value = "AP"> &nbsp;AP				
			</div>
			<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				<input type="checkbox" name="checkPT" id="checkPT" class="form-check-input" value = "PT"> &nbsp;PT				
			</div>
			<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">				
				<input type="checkbox" name="checkPN" id="checkPN" class="form-check-input" value = "PN"> &nbsp;PN				
			</div>
			<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">				
				<input type="checkbox" name="checkPP" id="checkPP" class="form-check-input" value = "PP"> &nbsp;PP				
			</div>
			<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">				
				<input type="checkbox" name="checkPI" id="checkPI" class="form-check-input" value = "PI"> &nbsp;PI				
			</div>
			<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">								
				<input type="checkbox" name="checkFR" id="checkFR" class="form-check-input" value = "FR"> &nbsp;FR
			</div>
		</div>
		
		<div class="row d-flex">				
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				<label class="form-label">Tipo Movimiento Dividido por comas (,)</label>
			</div>					
			<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
				<input type="text" name="tmovimiento" id="tmovimiento" class="form-control form-control-sm" size="100" placeholder="102,198,113">
			</div>
		</div>
		
		<div class="row d-flex">				
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				<label class="form-label">Estatus</label>
			</div>					
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				<select id="estatus" name="estatus" class="form-select form-select-sm">
					<option value="todos">Todos</option>
					<option value="Aplicado">Aplicado</option>
					<option value="Sin Aplicar">Sin Aplicar</option>
					<option value="Cancelado">Cancelado</option>
				</select>
			</div>
		</div>	
		
		<div class="row d-flex">				
			<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">														
			</div>					
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				<input type="button" class="btn btn-secondary btn-sm" name="btnBuscar" id="btnBuscar" value="Buscar" >			
			</div>
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				<input type="button" class="btn btn-secondary btn-sm" name="btnNuevo" id="btnNuevo" value="Nueva Busqueda" >			
			</div>			
		</div>

		<br/>
		<br/>
		
		<h6><b> Datos Cuenta por Pagar </b></h6>
  		<hr class="mt-3"> 		

		<div class="row d-flex">							
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
				<div class="input-group">								
					<span class="input-group-text"><i class="bi bi-filetype-csv"></i></span>			
					<input type="button" class="btn btn-secondary btn-sm" name="btnExportaExcel" id="btnExportaExcel" value="Extrae" >
				</div>		
			</div>
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">				
				<div class="input-group">								
					<span class="input-group-text"><i class="bi bi-filetype-pdf"></i></span>
					<input type="button" class="btn btn-secondary btn-sm" name="btnExportaPdf" id="btnExportaPdf" value="Extrae" >
				</div>
			</div>
			<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
			</div>
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
				Total Cuenta Por Pagar			
				<div id="importeTotal" style="font-size: 12pt">0.00</div>
			</div>
		</div>
						  		
  		<div id="notas" class="table-responsive">	    
			<table id="tabla" class="table table-striped">
	            <thead>
	                <tr>
	                	<th>Cuenta Por Pagar</th>
						<th>Estructura Programatica</th>
						<th>Mes</th>
						<th>Movimiento</th>
						<th>Concepto</th>
						<th>Importe</th>
						<th>Compromiso</th>
						<th>Estatus</th>					                    
	                </tr>
	            </thead>
	        </table>
		</div>

	</div>
</form>
</body>
</html>
