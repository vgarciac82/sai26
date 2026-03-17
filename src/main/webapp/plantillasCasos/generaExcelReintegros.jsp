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

boolean esReintegroContable =  "S".equalsIgnoreCase( request.getParameter("contable") );
String generaExcel = "1";

System.out.println("Reintegro contable: " + esReintegroContable );

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
		<title>Reintegros Presupuestales</title>

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
	var esReintegroCOntable = <%=esReintegroContable%>;
	var oTableAgregado;
	
	function limpiaTabla(){
		$('#agregados').dataTable().fnClearTable();		
		$("#iniciar").hide();
		$("#ExportarExcel").hide();
		$("#EditarImportes").hide();
		document.getElementById("caNoRecibo").removeAttribute("readonly", false);
		$("#caNoRecibo").val("");
		$("#caNoRecibo").focus();				
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
		
		if( esReintegroCOntable ){
			$("#tituloReintegro").text("Solicitud de Liberacion");
			generaExcel = "3";		
		}else{
			$("#tipoReintegrolabel").hide();
			$("#tiporeintegro").hide();
		}
		
		querySelectPost("catTipoReintegroRead","tiporeintegro", {async:false});
		
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
			aoColumns: [			
			{ sName: "clc",bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "caNoContrarrecibo",bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "EP",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"150px"},
			{ sName: "cMes",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"25px"},
			{ sName: "remanente",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px"},
			{ sName: "cCentroContable",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px"}
		    ]
		});	
	});

function toggleChecked(status) {
	$("#tablaCLC input").each( function() {
		$(this).attr("checked",status);
	});
}

function buscarCxp(){	
	//$('#tablaCLC').dataTable().fnClearTable();		
	$("#caNoRecibo").attr('readonly',true);		
	var idCXP = $("#caNoRecibo").val();
	var sw = "caNoContrarrecibo = '"+idCXP+"'";
	var datos = "sinDatos";
	var arrData = $("#agregados").dataTable().fnGetData();
	var szTabla = "TPAGADOENCABEZADOR";
	
	// Validamos si es un pago con recurso de Anexo1
	$("#cxp").val(idCXP);
	queryFormPost("esReintegroDeAnexo1Read", {async:false});
				
	//Buscar el Folio Para Mostrar Sus Detalles
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:"cTipoPago,nFolioPagado,caNoContrarrecibo, isnull(cDocumentoHaplicado, 'sinAplicar') cDocumentoHaplicado ", Param:sw, Order:"", MaxReg: "10", ajax: 'true'}, function(j){
				
				for (var i = 0; i < j.length; i++) {
						
						var cTipoPago = j[i].Col0;
						var nFolioPagado = j[i].Col1;
						var caNoContrarrecibo = j[i].Col2;
						var cDocumentoHaplicado = j[i].Col3;
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
					buscarCXPVista(caNoContrarrecibo);					
				}else{
					Swal.fire({ icon: 'warning',
								text: "No se encontró información con la Cuenta por Pagar ingresada."});						
					limpiaTabla();									
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
	
	if ( $("#Radicado").val() == "S"){
		szTable = "V_TAPGADORADICADO";
		if(ur=='A02')
			locationAjaxSource = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_TPagadoRadicado&qw=caNoContrarrecibo = '"+caNoContrarrecibo+"'";
		else
			locationAjaxSource = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_TPagadoRadicado&qw=caNoContrarrecibo = '"+caNoContrarrecibo+"' AND cCentroContable = '"+cc+"'";
	}else{
		if(ur=='A02')
			locationAjaxSource = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_TPagado&qw=caNoContrarrecibo = '"+caNoContrarrecibo+"'";
		else
			locationAjaxSource = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_TPagado&qw=caNoContrarrecibo = '"+caNoContrarrecibo+"' AND cCentroContable = '"+cc+"'";		
	}
		
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:camposWhere, Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j){
				
					for (var i = 0; i < j.length; i++){	
						var importeTotal = j[i].Col0;
					}		
			 });
			 
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
				Swal.fire({ icon: 'warning',
							text: "No hay información en el sistema con los criterios proporcionados o no tiene permisos para consultarla."});
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
			{ sName: "remanente",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px"},
			{ sName: "cCentroContable",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px"}
		]
	});	
} 

function agregarClave(){
	actualizaImportes();
	if(esReintegroCOntable){
		$("#ExportarExcel").hide();
	}else{
		$("#ExportarExcel").show();
	}	
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
	if(actualizaImportes()){
		if(esReintegroCOntable){
			if(document.getElementById("tiporeintegro").value == 0){
				Swal.fire({ icon: 'warning',
							text: "Favor de seleccionar el tipo de reintegro."});
				return;
			}else{
				if(!validaEsIngresoPropio()){
					return;
				}
				$('#dialogEncabezado').dialog('option', 'modal', true).dialog('open');
				$("#iniciarEncabezado").button();
			}
		}else{
			$('#dialogEncabezado').dialog('option', 'modal', true).dialog('open');
			$("#iniciarEncabezado").button();
		}
		
	}
}

function iniciarTramiteNuevo(){
	actualizaImportes();
	borraElementos();
	if(esReintegroCOntable){
		$('#generaCaso').val('3');		
	}else{
		$('#generaCaso').val('1');
	}
	
	if($("#observaciones").val()=="" || $("#concepto").val()=="" || $("#CatMovimientoReintegro").val()=="" || $("#CatTipoCausaAvisoReintegro").val()=="" || $("#CatCausaAvisoReintegro").val()=="" || $("#CatFormaPagoAvisoReintegro").val()==""){
		Swal.fire({ icon: 'warning',
					text: "Favor de llenar todos los campos para iniciar el caso."});		
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
	createInput('generaExcelForm', 'CatTipoReintegro', document.getElementById("tiporeintegro").value);
	createInput('generaExcelForm', 'esIngresoPropio', $("#esIngresoPropio").val());
	
	$('#generaExcel').val('0');
	$('#generaExcelForm').submit();
}

function actualizaImportes(){
	
	if(bActualizaImportes){		
		var validacionImporte = true;
		var renglon = 0;
		var arrData = $("#agregados").dataTable().fnGetData();			
		for(var i = 0; i < arrData.length; i++){			
			if((importes[i]<$('#edit'+i).val()*1) || ($('#edit'+i).val()*1) < 0){		
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
			Swal.fire({ icon: 'warning',
						text: "Los importes exceden el remanente ó es importe negativo, favor de verificarlos, renglon " + renglon});			
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
		$('#agregados tbody tr:eq('+i+') td:eq(4)').html('<input type="text" class="form-control form-control-sm" id="edit'+i+'" value="'+importe+'">');
	}
	$("#EditarImportes").hide();
}

function validaEsIngresoPropio(){
	var bRegresa = true;
	$("#IdTipoReintegro").val("");	
	$("#IdTipoReintegro").val(document.getElementById("tiporeintegro").value);
	
	queryFormPost("esTipoReintegroIPRead", {async:false});
	
	var arrData = $("#agregados").dataTable().fnGetData();
	var ep = "";
	var valor = "";
	var esIP = 0; 
	
	for(var i = 0; i < arrData.length; i++){
		ep = "";
		valor = "";
		ep = arrData[i][2];
		valor = ep.substring(39, 40);
		
		if(valor == "4"){
			i = arrData.length;
			esIP = 1;
		}
	}
	
	if( esIP == 1 && $("#esIngresoPropio").val() == "0" ){
		bRegresa = false;
		Swal.fire({ icon: 'warning',
					text: "La Cuenta por Pagar capturada es de ingresos propios.\nFavor de seleccionar el Tipo de Reintegro correcto."});		
	}else if(esIP == 0 && $("#esIngresoPropio").val() == "1"){
		bRegresa = false;
		Swal.fire({ icon: 'warning',
					text: "La Cuenta por Pagar capturada NO es de ingresos propios.\nFavor de seleccionar el Tipo de Reintegro correcto."});		
	}
	
	return bRegresa;
}
	</script>

	</head>
<br/>
	<body id="dt_example">
		<div id="container" class="container" style="width: 90%">
			<div class="card-header"> <h3 id="tituloReintegro"> Reintegros Contables-Presupuestales </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row d-flex">					
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Cuenta Por Pagar:</label>											
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="input-group">
						<input type="text" name = "caNoRecibo" id = "caNoRecibo" class="form-control form-control-sm"/>
						<input type="button" class="btn btn-secondary btn-sm" name="btnBuscar" id="btnBuscar" value="Buscar" >
					</div>					
				</div>
			</div>	
								
			<div class="row d-flex">		
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>			
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label id="tipoReintegrolabel" class="form-label">Tipo de Liberación:</label>											
				</div>					
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					<select name="tiporeintegro" id="tiporeintegro" class="form-select form-select-sm">
					</select>					
				</div>
			</div>
			
			<br/>
			
			<div id="dialog" title="Detalle de Reintegros" class="container">
				<table id="tablaCLC" class="table table-striped">
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
				
				<br/>
				
				<div class="row d-flex">	
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<input type="button" class="btn btn-secondary btn-sm" id="btnAgregaClaves" name="btnAgregaClaves" value="Agregar"/>															
					</div>	
				</div>
				
			</div>
			
			<table id="agregados" class="table table-striped table-sm">
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
			
			<br/>
			
			<form id="generaExcelForm" name="generaExcelForm" action="../servlet/ReintegrosServlet" method="post" >
				<input type="hidden" id="cxp" name="cxp" value="" />
				<input type="hidden" id="Radicado" name="Radicado" value="N" />
				<input type="hidden" id="IdTipoReintegro" name="IdTipoReintegro" value=""/>
				<input type="hidden" id="esIngresoPropio" name="esIngresoPropio" value="0"/>	
				<input type="hidden" id="generaExcel" name="generaExcel" value="<%=generaExcel%>"/>
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
				
				<div id="dialogEncabezado" title="Avisos de Reintegro" class="container">
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
