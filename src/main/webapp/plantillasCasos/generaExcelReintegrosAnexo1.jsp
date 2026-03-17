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
		<title>Reintegro de Solicitud de Recursos x Pagar</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
		<link type="text/css" rel="stylesheet" href="../css/themes/base/jquery.ui.all.css" />
		<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />

		<style type="text/css" title="currentStyle">
			@import "../Generador/css/demo_page.css";
			@import "../Generador/css/demo_table_jui.css";
			@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
		</style>
		
		<style>
			.notEditable {
				background-color: #CCCCCC;
				color: #000000;
			}
		</style>
		
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
		if(!bActualizaImportes){
			borraElementos();
			var arrData = $("#agregados").dataTable().fnGetData();
			for(var i = 0; i < arrData.length; i++  ){
				createInput('generaExcelForm', 'clc', arrData[i][0]);
				createInput('generaExcelForm', 'cxp', arrData[i][1]);
				createInput('generaExcelForm', 'ep', arrData[i][2]);
				createInput('generaExcelForm', 'mes', arrData[i][3]);
				createInput('generaExcelForm', 'importe', arrData[i][4]);
			}				
			$("#generaExcelForm").submit();
		}
		return true;
	}
	
	$(document).ready(function (){
		$("input.AyudaSyC").subIniciaDlg();
		$("#ExportarExcel").hide();
		$("#btnAgregaClaves").button();
		$("#LimpiarTabla").button();
		$("#ExportarExcel").button();
		$("#btnBuscar").button();
		$("#iniciar").button();
		$("#iniciar").hide();
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
			sScrollY: "250px", 
			sScrollX: "800px",
			Height: "250px",
			Width: "800px",
			aoColumns: [
				{ sName: "checknDocRenglon",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
				{ sName: "clc",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px" },
				{ sName: "caNoContrarrecibo",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px" },
				{ sName: "EP",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"150px"},
				{ sName: "cMes",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"25px"},
				{ sName: "remanente",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px"},
				{ sName: "cCentroContable",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px"}
		    ]
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
			bServerSide: false,
			bProcessing: true,
			bJQueryUI: true,
			bAutoWidth : false,
			bRetrive: true,
			bDestroy: true,
			bPaginate: false,			
			bSort: false, 
			iDisplayLength: 10,
			sScrollY: "250px", 
			sScrollX: "800px",
			Height: "250px",
			Width: "800px",
			aoColumns: [			
			{ sName: "clc",bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "caNoContrarrecibo",bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "EP",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"150px"},
			{ sName: "cMes",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"25px"},
			{ sName: "remanente",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px"},
			{ sName: "cCentroContable",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px"}
		    ]
		});	
		cssReadOnly();
	});
	
function toggleChecked(status) {
	$("#tablaCLC input").each( function() {
		$(this).attr("checked",status);
	});
}

function buscarCxp(){	
	$('#tablaCLC').dataTable().fnClearTable();
	$('#agregados').dataTable().fnClearTable();
	var idCXP = $("#caNoRecibo").val();
	var elParametro = "nIdIntegracion = '"+idCXP+"'";
	var datos = "sinDatos";
	var arrData = $("#agregados").dataTable().fnGetData();
	//Buscar el Folio Para Mostrar Sus Detalles
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "TCONSOLIDACIONANEXO1", Campos:" nFolioConsolidacion, isnull(cDocumentoHaplicado,'sinAplicar') cDocumentoHaplicado, nIdIntegracion ", Param:elParametro, Order:"", MaxReg: "10", ajax: 'true'}, function(j){
				
		for (var i = 0; i < j.length; i++) {
			var nFolioConsolidacion = j[i].Col0;
			var cDocumentoHaplicado = j[i].Col1;
			var caNoContrarrecibo = j[i].Col2;
			datos = "datos";
		}
				
		if(cDocumentoHaplicado == 'C' || cDocumentoHaplicado == 'N'){
			alert("El Documento Esta Cancelado");
			return;
			
		}else if(cDocumentoHaplicado == 'sinAplicar' || cDocumentoHaplicado == ""){
			alert("El Documento No Esta Aplicado");
			return;					
		}
		
		// Si Encuentra La Cuenta Por Pagar Buscar Sus Detalles
		if(datos == "datos"){
			buscarCXPVista(caNoContrarrecibo);
		}else{
			alert("No se encontró información con la Cuenta por Pagar ingresada");
		}
	
	});
}

function buscarCXPVista(caNoContrarrecibo){
	actualizaImportes();
	$('#dialog').dialog('option', 'modal', true).dialog('open');
	var szTabla = "V_TPAGADO";
	var camposWhere = " caNoContrarrecibo = '"+caNoContrarrecibo+"'";
	var elParametro = "";
	var order = "";
	var ur='<%=usuario.getU_UR()%>';
	var cc = '<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>';
	var locationAjaxSource = "";
	if(ur=='A02')
		locationAjaxSource = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vIngresoAnexo1Remanente&qw=caNoContrarrecibo = '"+caNoContrarrecibo+"'";
	else
		locationAjaxSource = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vIngresoAnexo1Remanente&qw=caNoContrarrecibo = '"+caNoContrarrecibo+"' AND cCentroContable = '"+cc+"'";
		
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
		sAjaxSource: locationAjaxSource,
		bProcessing: true,
		"fnDrawCallback": function(oSettings) {
			if (oSettings.aiDisplay.length == 0)
			{
				$("#dialog").dialog('close');
				alert("No hay información en el sistema con los criterios proporcionados o no tiene permisos para consultarla.");
			}
		},
		bJQueryUI: true,
		bAutoWidth : false,		
		bRetrive: true,		
		bDestroy: true,        
		bPaginate: false,		
		iDisplayLength: 30,		
		sScrollY: "300px", 	
		sScrollX: "1200px",		
		Height: "400px",
		aoColumns: [
			{ sName: "checknDocRenglon",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "clc",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "caNoContrarrecibo",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "EP",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"150px"},
			{ sName: "cMes",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"25px"},
			{ sName: "remanente",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRi", sWidth:"40px"},
			{ sName: "cCentroContable",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px"}
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
	//var oTableA = $('#agregados').dataTable();
	var aData = $("#tablaCLC").dataTable().fnGetData();//oTable.fnGetData();
	var aDataA = $("#agregados").dataTable().fnGetData();//oTableA.fnGetData();
	var arrlist = new Array();
	var cxp = $("#caNoRecibo").val().substring(0, 14).toUpperCase();	
	var i=0;	
	$('#tablaCLC input:checked').each(function(idx, elm){
		var paso = aData[oTable.fnGetPosition($(this).closest('tr')[0])];
		var agregaD = true;
		var j=0;
		while(j<aDataA.length){			
			if(aDataA[j][2]==paso[3] && cxp==aDataA[j][1].substring(0, 14).toUpperCase()){
				agregaD = false;
			}
			j++;
		}
		if(agregaD){			
			arrlist[i] = new Array(paso[1],paso[2],paso[3],paso[4],Number(paso[5]).toFixed(2), paso[6] );
			i++;
		}
	});		
	$("#agregados").dataTable().fnAddData( arrlist );
}

function capturaEncabezado(){
	$("#cxpintegracion").val($("#caNoRecibo").val());
	if(actualizaImportes()){
		$('#dialogEncabezado').dialog('option', 'modal', true).dialog('open');
		$("#iniciarEncabezado").button();
	}
}

function iniciarTramiteNuevo(){
	actualizaImportes();
	borraElementos();
	$('#generaCaso').val('1');
	if($("#observaciones").val()=="" || $("#concepto").val()=="" || $("#CatMovimientoReintegro").val()=="" || $("#CatTipoCausaAvisoReintegro").val()=="" || $("#CatCausaAvisoReintegro").val()=="" || $("#CatFormaPagoAvisoReintegro").val()==""){
		alert("Favor de llenar todos los campos para iniciar el caso");
		return;
	}
	var arrData = $("#agregados").dataTable().fnGetData();
	for(var i = 0; i < arrData.length; i++){
		createInput('generaExcelForm', 'clc', arrData[i][0]);
		createInput('generaExcelForm', 'cxp', arrData[i][1]);
		createInput('generaExcelForm', 'ep', arrData[i][2]);
		createInput('generaExcelForm', 'mes', arrData[i][3]);
		createInput('generaExcelForm', 'importe', arrData[i][4]);
		createInput('generaExcelForm', 'centrocontablecxp', arrData[i][5]);
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
				$('#agregados').dataTable().fnUpdate($('#edit'+i).val(),i,4);
			}
			$("#EditarImportes").show();
		}else{
			alert("Los importes exceden el remanente, favor de verificarlos, renglon "+renglon);
			return false;
		}
	}
	return true;
}

var importes = new Array();
var arrData = new Array();
function editaMonto(){
	bActualizaImportes = true;
	var arrData = $("#agregados").dataTable().fnGetData();
	for(var i = 0; i < arrData.length; i++){
		var importe = 	arrData[i][4]*1;//$('#agregados tbody tr:eq('+i+') td:eq(4)').html()*1;//se multiplica por 1 para convertirlo en un número y no se quede como texto		
		importes[i] = importe;
		$('#agregados tbody tr:eq('+i+') td:eq(4)').html('<input type="text" id="edit'+i+'" value="'+importe+'" onkeypress="return validaKeyPress(event)" onblur="validaImporte(this)">');
	}
	$("#EditarImportes").hide();
}

function validaKeyPress(e) {
	tecla = (document.all) ? e.keyCode : e.which;
	if (tecla == 8)
		return true;
	patron = /[.\d]/;
	te = String.fromCharCode(tecla);
	return patron.test(te);
}

function validaImporte(input) {
	$("#" + input.id).formatCurrency();

	var importeEditado = input.value;
	importeEditado = importeEditado.replace(/[$]/g, "");
	importeEditado = importeEditado.replace(/,/g, "");

	$("#" + input.id).val(importeEditado);

}

function cssReadOnly(){
		$( "[readOnly]" ).each(function(){	
			$(this).addClass("notEditable");	
		});
	}

	</script>

	</head>

	<body id="dt_example">
		<div id="container" class="container SyCData">
			<h1 align="center">
				Reintegro de Solicitud de Recursos x Pagar
			</h1>
			Folio de Integracion
			<input type="text" name="caNoRecibo" id="caNoRecibo" size=30>
			<input type="button" name="btnBuscar" id="btnBuscar" value="Buscar" >
			<div id="dialog" title="Detalle de Reintegros">
				<table id="tablaCLC" class="display">
					<thead>
							<tr>
								<th>Sel<input type="checkbox" onclick="toggleChecked(this.checked)"></th>
								<th>CLC</th>
								<th>Cuenta Por Pagar</th>
								<th>Estructura Programatica</th>
								<th>Mes</th>
								<th>Remanente</th>
								<th>Centro Contable</th>
							</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
				<input type="button" id="btnAgregaClaves" name="btnAgregaClaves" value="Agregar"/>
			</div>
			<table id="agregados" class="display">
					<thead>
							<tr>
								
								<th>CLC</th>
								<th>Cuenta Por Pagar</th>
								<th>Estructura Programatica</th>
								<th>Mes</th>
								<th>Remanente</th>
								<th>Centro Contable</th>
							</tr>
					</thead>
					<tbody>
					</tbody>
			</table>
			<form id="generaExcelForm" name="generaExcelForm" action="../servlet/ReintegrosAnexo1Servlet" method="post" >
				<div style="text-align:right;">
					<input type="hidden" id="generaExcel" name="generaExcel" value="1"/>
					<input type="hidden" id="generaCaso" name="generaCaso" value="0"/>
					<input type="button" id="iniciar" name="iniciar" value="Nuevo Trámite" onclick="capturaEncabezado();" />
					<input type="button" id="EditarImportes" name="EditarImportes" value="Editar Importes" onclick="return editaMonto()" />
					<input type="button" id="ExportarExcel" name="ExportarExcel" value="Exportar Excel" onclick="return generaInputs()" disabled="disabled" />
					<input type="button" id="LimpiarTabla" value="Limpiar Tabla" onclick="limpiaTabla();"/>
				</div>
				
				<div id="dialogEncabezado" title="Encabezado Avisos de Reintegro">
					<p>Campos del encabezado</p>
					<table><tr><td>Observaciones:</td><td><input type="text" name="observaciones" id="observaciones" size="45" maxlength="60"/></td></tr>
					<tr><td>Concepto:</td><td><textarea id="concepto" name="concepto" rows="4" cols="45"></textarea></td></tr>
					</table>
					<table>
						<tr>	
							<td>
								 Movto.
							</td>
							<td>
								<input type="text" class="AyudaSyC autoCompletaSyC" name="CatMovimientoReintegro" id="CatMovimientoReintegro" size="5" readonly="readonly"/>
							</td>
							<td>
								 Tipo de Aviso
							</td>
							<td>
								<input type="text" name="CatTipoCausaAvisoReintegro" id="CatTipoCausaAvisoReintegro" class="AyudaSyC autoCompletaSyC" size="5" readonly="readonly">
							</td>
						</tr>
						<tr>
							<td>
								 Causa del Aviso.
							</td>
							<td>
								<input type="text" name="CatCausaAvisoReintegro" id="CatCausaAvisoReintegro" class="AyudaSyC autoCompletaSyC" size="5" readonly="readonly">
							</td>
							<td width="200">
								 Forma de Pago
							</td>
							<td>
								<input type="text" class="AyudaSyC autoCompletaSyC" name="CatFormaPagoAvisoReintegro" id="CatFormaPagoAvisoReintegro" size="5" readonly="readonly"/>
							</td>
						</tr>
					</table>
					<input type="button" id="iniciarEncabezado" name="iniciarEncabezado" value="Iniciar Trámite" onclick="iniciarTramiteNuevo();" />
				</div>
			</form>
		</div>
	</body>
</html>
