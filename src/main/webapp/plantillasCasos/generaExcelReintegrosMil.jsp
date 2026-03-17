<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="java.util.*"%>
<%@page	import="com.syc.gestion.core.*"%>
<%@page	import="com.syc.gestion.servlet.*"%>
<%@page	import="com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.ReintegrosBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.ReintegroEncabezado"%>
<%@page import="com.syc.contable.core.ReintegroDetalle"%>
<%
//Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

/*if (c == null) {
	response.sendRedirect("../index.jsp");
	return;
}*/

//final int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();


String mensaje = "";
if (request.getParameter("msg") != null
		&& !"".equals(request.getParameter("msg"))) {
	mensaje = request.getParameter("msg");
	mensaje = mensaje.replace("[", "");
	mensaje = mensaje.replace("]", "");
	mensaje = mensaje.replace(",", "<br>");
}



/*int id_oper = -1;
if (request.getParameter("id_oper") != null)
	id_oper = new Integer(request.getParameter("id_oper")).intValue();
else
	id_oper = c.getCasoOperacion(0).getIdOperacion();*/

/*Empleado e = new Empleado();
EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
e.setClaveUsuario(usuario.getLogin());
e = ebl.getEmpleado(e);
EmpleadoArea ea = new EmpleadoArea();
ea.setId(e.getClaveArea());
ea = ebl.getEmpleadoArea(ea);*/

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Reintegros Presupuestales Capítulo Mil</title>

		<link rel="stylesheet" type="text/css"  href="../css/datepickercontrol_bluegray.css" />
		<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
		
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.core"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/ui/jquery.ui.accordion.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
		
	<script type="text/javascript" charset="utf-8">
	
	var bActualizaImportes = false;
	
	function limpiaTabla(){
		$('#agregados').dataTable().fnClearTable();
		$("#iniciar").hide();
		$("#ExportarExcel").hide();
		$("#EditarImportes").hide();
	}
	
	function createInput(form, name, value){
		$('<input>').attr({
			type: 'hidden',
			name: name,
			value: value
		}).addClass('remove').appendTo('#' + form);
	}
	
	function borraElementos(){
		$('.remove').remove();	
	}
	
	function generaInputs(){
		actualizaImportes();
		//terminarEdicionMonto();	
		if(!bActualizaImportes){
			borraElementos();
			var arrData = $("#agregados").dataTable().fnGetData();
			for(var i = 0; i < arrData.length; i++  ){
				createInput('generaExcelForm', 'clc', arrData[i][0]);
				createInput('generaExcelForm', 'cxpsai', arrData[i][1]);
				createInput('generaExcelForm', 'cxp', arrData[i][2]);
				createInput('generaExcelForm', 'ep', arrData[i][3]);
				createInput('generaExcelForm', 'mes', arrData[i][4]);
				createInput('generaExcelForm', 'importe', arrData[i][5]);
				createInput('generaExcelForm', 'tipoConcepto', arrData[i][6]);
				createInput('generaExcelForm', 'tipoMovimiento', arrData[i][7]);
			}				
			$("#generaExcelForm").submit();
		}
		return true;
	}
	
	$(document).ready(function (){
		$("input.AyudaSyC").subIniciaDlg();
		$("#ExportarExcel").hide();
		$("#iniciar").hide();
		$("#btnAgregaClaves").button();
		$("#LimpiarTabla").button();
		$("#ExportarExcel").button();
		$("#btnBuscar").button();
		$("#iniciar").button();
		$("#EditarImportes").hide();
		$("#EditarImportes").button();
		$(function() {		
      		$('#dialog').dialog({
      			autoOpen: false,
      			width: 800,
      			height:500
    		});
    	});
		$(function() {		
		    $('#dialogEncabezado').dialog({
		      	autoOpen: false,
	  			width: 900,
	 			heigth: 2900
		    });
		});
		$("#btnBuscar").click(function() { buscarCxp(); });
		$("#btnAgregaClaves").click(function() { agregarClave(); });
		var tablaCierre = $('#tablaCLC').dataTable({      
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
			bServerSide: false,
			//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_NominaCapituloMil&qw=" + " caNoContrarrecibo = '0' ",
			bProcessing: true,
			bJQueryUI: true,
			bAutoWidth : false,
			bRetrive: true,
			bDestroy: true,
			bPaginate: false,
			iDisplayLength: 10,
		});
		var agregados = $('#agregados').dataTable({      
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
			bProcessing: true,
			bJQueryUI: true,
			bAutoWidth : false,
			bRetrive: true,
			bDestroy: true,
			bPaginate: false,
			bSort: false, 
			iDisplayLength: 10
		});	
	});
	
function toggleChecked(status) {
	$("#tablaCLC input").each( function() {
		$(this).attr("checked",status);
	});
}

function buscarCxp(){	
	//$('#tablaCLC').dataTable().fnClearTable();
	var idCXP = $("#caNoRecibo").val();
	var cxp = $("#caNoReciboCLC").val();
	var elParametro = "caNoContrarrecibo = '"+idCXP+"'";
	var datos = "sinDatos";
	//Buscar el Folio Para Mostrar Sus Detalles
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "TPAGADOENCABEZADOR", Campos:"cTipoPago,nFolioPagado,caNoContrarrecibo, isnull(cDocumentoHaplicado, 'sinAplicar') cDocumentoHaplicado,nFolioSICOP,nFolioSIAFF,CASE cTipoPago WHEN 'PAGOOBRA' THEN 'COMPROMISO' WHEN 'PAGODIVERSO' THEN 'COMPROMISO' WHEN 'PAGODIRECTO' THEN 'DIRECTA' WHEN 'RELACIONGASTOS' THEN 'DIRECTA' WHEN 'NOMINA' THEN 'COMPROMISO' when 'AJENAS' then 'DIRECTA' when 'FEDERALIZADO' then 'COMPROMISO' ELSE '' END AS dTipoPago ", Param:elParametro, Order:"", MaxReg: "10", ajax: 'true'}, function(j){
				
				for (var i = 0; i < j.length; i++) {
						var cTipoPago = j[i].Col0;
						var nFolioPagado = j[i].Col1;
						var caNoContrarrecibo = j[i].Col2;
						var cDocumentoHaplicado = j[i].Col3;
						var nFolioSICOP = j[i].Col4;
						var nFolioSIAFF = j[i].Col5;
						var tipoRectificacion = j[i].Col6;
						$("#cuentaPorPagar").val(caNoContrarrecibo);
						$("#nFolioSICOP").val(nFolioSICOP);
						$("#nFolioSIAFF").val(nFolioSIAFF);
						datos = "datos";
				}		
				
				if(cDocumentoHaplicado == 'C' ){
					Swal.fire({ icon: 'warning',
								text: "El Documento Esta Cancelado."});					
					return;
					
				}else if(cDocumentoHaplicado == 'sinAplicar' || cDocumentoHaplicado == ""){					
					Swal.fire({ icon: 'warning',
								text: "El Documento No Esta Aplicado."});					
					return;					
				}
				
				// Si Encuentra La Cuenta Por Pagar Buscar Sus Detalles
				
				if(datos == "datos"){
					//$('#tablaCLC').dataTable().fnClearTable();
					
					buscarCXPVista(caNoContrarrecibo,cxp);
					
				}else{
					Swal.fire({ icon: 'warning',
								text: "No se Encontro Informacion Con La Solicitud de Pago Ingresada."});					
				}
	
		});
	}

function buscarCXPVista(caNoContrarrecibo,cuentaPorPagar){
	$('#dialog').dialog('option', 'modal', true).dialog('open');
	var szTabla = "V_TPAGADO";
	var camposWhere = " caNoContrarrecibo = '"+caNoContrarrecibo+"'";
	var elParametro = "";
	var order = "";
	$("#cuentaPorPagarCLC").val(cuentaPorPagar);
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:camposWhere, Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j){
		for (var i = 0; i < j.length; i++){	
			var importeTotal = j[i].Col0;
			//$("#importeTotal").html(importeTotal);
			//$("#importeTotal").formatCurrency();
		}		
	});
	var	locationV='';
	if(cuentaPorPagar!='')
	 	locationV = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=V_tPagadoMil&qw=caNoContrarreciboCLC = '"+caNoContrarrecibo+"' and caNoContrarrecibo = '"+cuentaPorPagar+"'";
	else{
		Swal.fire({ icon: 'warning',
					text: "Debe especificar la cuenta por pagar."});		
		return;
	}
	$('#tablaCLC').dataTable({         
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
		sAjaxSource: locationV ,
		bProcessing: true,
		bJQueryUI: true,
		bAutoWidth : false,		
		bRetrive: true,		
		bDestroy: true,        
		bPaginate: false,		
		iDisplayLength: 30,	
		aoColumns: [
			{ sName: "checknDocRenglon",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "clc",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "folioDependenciaCLC",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "caNoContrarreciboCLC",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "caNoContrarrecibo",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "EP",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"150px"},
			{ sName: "cMes",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"25px"},
			{ sName: "mImporteNeto",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px"},
			{ sName: "tipoConcepto",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "movimiento",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "Remanente",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" }
		]
	});	
} 

function agregarClave(){
	actualizaImportes();
	$("#ExportarExcel").show();
	$("#iniciar").show();
	$("#EditarImportes").show();
	$("#dialog").dialog('close');
	var oTable = $('#tablaCLC').dataTable();
	var oTableA = $('#agregados').dataTable();
	var aData = oTable.fnGetData();
	var aDataA = oTableA.fnGetData();
	var arrlist = new Array();
	var cxp = $("#caNoRecibo").val().substring(0, 14).toUpperCase();
	var i=0;
	$('#tablaCLC input:checked').each(function(idx, elm){
		var paso = aData[oTable.fnGetPosition($(this).closest('tr')[0])];
		var agregaD = true;
		var j=0;
		while(j<aDataA.length){
			if(aDataA[j][3]==paso[5] && cxp==aDataA[j][1].substring(0, 14).toUpperCase()){
				agregaD = false;
			}
			j++;
		}
		if(agregaD){
			arrlist[i] = new Array(paso[1],paso[3],paso[4],paso[5],paso[6],Number(paso[7]).toFixed(2),paso[8],paso[9],paso[10]);
			i++;
		}		
	});		
	$("#agregados").dataTable().fnAddData( arrlist );
}

function capturaEncabezado(){
	if(actualizaImportes()){
		$('#dialogEncabezado').dialog('option', 'modal', true).dialog('open');
		$("#iniciarEncabezado").button();
	}
}

function iniciarTramiteNuevo(){
	//terminarEdicionMonto();
	actualizaImportes();
	borraElementos();
	$('#generaCaso').val('2');
	if($("#observaciones").val()=="" || $("#concepto").val()=="" || $("#CatMovimientoReintegro").val()=="" || $("#CatTipoCausaAvisoReintegro").val()=="" || $("#CatCausaAvisoReintegro").val()=="" || $("#CatFormaPagoAvisoReintegro").val()==""){
		Swal.fire({ icon: 'warning',
					text: "Favor de llenar todos los campos para iniciar el caso."});		
		return;
	}
	var arrData = $("#agregados").dataTable().fnGetData();
	for(var i = 0; i < arrData.length; i++){
		createInput('generaExcelForm', 'clc', arrData[i][0]);
		createInput('generaExcelForm', 'cxpsai', arrData[i][1]);
		createInput('generaExcelForm', 'cxp', arrData[i][2]);
		createInput('generaExcelForm', 'ep', arrData[i][3]);
		createInput('generaExcelForm', 'mes', arrData[i][4]);
		createInput('generaExcelForm', 'importe', arrData[i][5]);
		createInput('generaExcelForm', 'tipoConcepto', arrData[i][6]);
		createInput('generaExcelForm', 'tipoMovimiento', arrData[i][7]);
	}				
	createInput('generaExcelForm', 'observaciones', $("#observaciones").val());
	createInput('generaExcelForm', 'concepto', $("#concepto").val());
	createInput('generaExcelForm', 'CatMovimientoReintegro', $("#CatMovimientoReintegro").val());
	createInput('generaExcelForm', 'CatTipoCausaAvisoReintegro', $("#CatTipoCausaAvisoReintegro").val());
	createInput('generaExcelForm', 'CatCausaAvisoReintegro', $("#CatCausaAvisoReintegro").val());
	createInput('generaExcelForm', 'CatFormaPagoAvisoReintegro', $("#CatFormaPagoAvisoReintegro").val());
	$('#generaExcel').val('0');
	$('#generaExcelForm').submit();
}

var importes = new Array();
function editaMonto(){
	bActualizaImportes = true;
	var arrData = $("#agregados").dataTable().fnGetData();
	for(var i = 0; i < arrData.length; i++){
		var importe = 	$('#agregados tbody tr:eq('+i+') td:eq(8)').html()*1;//se multiplica por 1 para convertirlo en un número y no se quede como texto
		importes[i] = importe;
		$('#agregados tbody tr:eq('+i+') td:eq(5)').html('<input type="text" class="form-control form-control-sm" id="edit'+i+'" value="'+importe+'">');
	}
	$("#EditarImportes").hide();
}

function terminarEdicionMonto(){
	var arrData = $("#agregados").dataTable().fnGetData();
	for(var i = 0; i < arrData.length; i++){
		var valor = $('#edit'+i).val()*1; //se multiplica por 1 para convertirlo en un número y no se quede como texto
		$('#agregados tbody tr:eq('+i+') td:eq(5)').html(valor);
	}				
}

function actualizaImportes(){

	if(bActualizaImportes){
		var validacionImporte = true;
		var renglon = 0;
		var arrData = $("#agregados").dataTable().fnGetData();
		for(var i = 0; i < arrData.length; i++){
			if(importes[i]<$('#edit'+i).val()*1){				
				validacionImporte = false;
				renglon = i+1; 
			}
		}
		if(validacionImporte){
			bActualizaImportes = false;
			for(var i = 0; i < arrData.length; i++){
				$('#agregados').dataTable().fnUpdate($('#edit'+i).val(),i,5);
			}
			$("#EditarImportes").show();
		}else{
			Swal.fire({ icon: 'warning',
						text: "Los importes exceden el remanente, favor de verificarlos, renglon " + renglon});			
			return false;
		}
	}else{
		var tListado = $("#agregados").dataTable().fnGetData();
		var exedeRemanente = false;
		for (i=0; i<tListado.length;i++)
			if(Number(tListado[i][5]) > Number(tListado[i][8]))
				exedeRemanente = true;
		if (exedeRemanente){
			Swal.fire({ icon: 'warning',
						text: "Los importes exceden el remanente, favor de editar los importes."});			
			return false;
		}
	}
	return true;
}

	</script>

	</head>
<br/>
	<body id="dt_example">
		<div id="container" class="container">
			<div class="card-header"> <h3 id="tituloReintegro"> Reintegros Presupuestales Capítulo Mil </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row d-flex">					
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Cuenta Por Pagar SAI:</label>											
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">					
					<input type="text" name = "caNoRecibo" id = "caNoRecibo" class="form-control form-control-sm" placeholder="10NC2022100001"/>									
				</div>			
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label id="tipoReintegrolabel" class="form-label">Cuenta Por Pagar CLC:</label>											
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">				
					<div class="input-group">	
						<input type="text" name="caNoReciboCLC" id="caNoReciboCLC" size=20 class="form-control form-control-sm" placeholder="10CP2022100001">
						<input type="button" class="btn btn-secondary btn-sm" name="btnBuscar" id="btnBuscar" value="Buscar" >
					</div>					
				</div>
			</div>
			
			<br/>
			
			<div id="dialog" title="Detalle de Reintegros" class="container">
				<table id="tablaCLC" class="table table-striped">
					<thead>
						<tr>
							<th>Sel<input type="checkbox" onclick="toggleChecked(this.checked)"></th>
							<th>CLC</th>
							<th>Folio Dependencia CLC</th>
							<th>Cuenta Por Pagar SAI</th>
							<th>Cuenta Por Pagar</th>
							<th>Estructura Programatica</th>
							<th>Mes</th>
							<th>Importe</th>
							<th>Tipo Concepto</th>
							<th>Tipo Movimiento</th>
							<th>Remanente</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
				
				<br/>
				
				<div class="row d-flex">	
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">					
						<input type="button" class="btn btn-secondary btn-sm" id="btnAgregaClaves" name="btnAgregaClaves" value="Agregar"/>						
					</div>
				</div>
				
			</div>
			
			<table id="agregados" class="table table-striped">
				<thead>
					<tr>
						<th>CLC</th>
						<th>Cuenta Por Pagar SAI</th>
						<th>Cuenta Por Pagar</th>
						<th>Estructura Programatica</th>
						<th>Mes</th>
						<th>Importe</th>
						<th>Tipo Concepto</th>
						<th>Tipo Movimiento</th>
						<th>Remanente</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			
			<br/>
					
			<form id="generaExcelForm" name="generaExcelForm" action="../servlet/ReintegrosServlet" method="post" >
				<input type="hidden" id="generaExcel" name="generaExcel" value="2"/>
				<input type="hidden" id="tipoPago" name="tipoPago"/>
				<input type="hidden" id="cuentaPorPagar" name="cuentaPorPagar"/>
				<input type="hidden" id="cuentaPorPagarCLC" name="cuentaPorPagarCLC"/>
				<input type="hidden" id="nFolioSICOP" name="nFolioSICOP"/>
				<input type="hidden" id="nFolioSIAFF" name="nFolioSIAFF"/>
				<input type="hidden" id="generaCaso" name="generaCaso" value="0"/>
	
			<div style="text-align:right;" class="container">
			
				<div class="row d-flex">							
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<input type="button" class="btn btn-secondary btn-sm" id="iniciar" name="iniciar" value="Nuevo Trámite" onclick="capturaEncabezado();" />														
					</div>				
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<input type="button" class="btn btn-secondary btn-sm" id="EditarImportes" name="EditarImportes" value="Editar Importes" onclick="return editaMonto()" />										
					</div>					
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<input type="button" class="btn btn-secondary btn-sm" id="ExportarExcel" name="ExportarExcel" value="Exportar Excel" onclick="return generaInputs()" />											
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<input type="button" class="btn btn-secondary btn-sm" id="LimpiarTabla" value="Limpiar Tabla" onclick="limpiaTabla();"/>											
					</div>
				</div>
				
			</div>
					
			<div id="dialogEncabezado" title="Avisos de Reintegro">
				<br/>
					<h6><b> Datos de Encabezado </b></h6>
  					<hr class="mt-3">
  										
					<div class="row d-flex">											
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<label class="form-label">Observaciones:</label>											
						</div>					
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
							<input type="text" name="observaciones" id="observaciones" class="form-control form-control-sm" size="45" maxlength="60"/>
						</div>
					</div>
					
					<div class="row d-flex">											
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<label class="form-label">Concepto:</label>											
						</div>					
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
							<textarea id="concepto" name="concepto" rows="4" cols="45" maxlength="1000" class="form-control"></textarea>
						</div>
					</div>
					
					<div class="row d-flex">											
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<label class="form-label">Movto:</label>											
						</div>					
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<div class="input-group">
								<input type="text" class="form-control form-control-sm AyudaSyC autoCompletaSyC" name="CatMovimientoReintegro" id="CatMovimientoReintegro" size="5"/>
							</div>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<label class="form-label">Tipo de Aviso:</label>											
						</div>		
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<div class="input-group">
								<input type="text" class="form-control form-control-sm AyudaSyC autoCompletaSyC" name="CatTipoCausaAvisoReintegro" id="CatTipoCausaAvisoReintegro" size="5"/>
							</div>
						</div>			
					</div>
					
					<div class="row d-flex">											
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<label class="form-label">Causa del Aviso:</label>											
						</div>					
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<div class="input-group">
								<input type="text" class="form-control form-control-sm AyudaSyC autoCompletaSyC" name="CatCausaAvisoReintegro" id="CatCausaAvisoReintegro" size="5"/>
							</div>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<label class="form-label">Forma de Pago:</label>											
						</div>		
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<div class="input-group">
								<input type="text" class="form-control form-control-sm AyudaSyC autoCompletaSyC" name="CatFormaPagoAvisoReintegro" id="CatFormaPagoAvisoReintegro" size="5"/>
							</div>
						</div>			
					</div>
					
					<br/>									
					
					<div class="row d-flex">											
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<input type="button" class="btn btn-secondary btn-sm" id="iniciarEncabezado" name="iniciarEncabezado" value="Iniciar Trámite" onclick="iniciarTramiteNuevo();" />
						</div>
					</div>							
				</div>
			</form>
		</div>
	</body>
</html>
