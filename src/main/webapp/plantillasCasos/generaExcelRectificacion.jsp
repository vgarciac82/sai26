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
		<title>Rectificaciones Presupuestales</title>

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
	
	function limpiaTabla(){
		$("#busqueda").show();
		$('#agregados').dataTable().fnClearTable();
		$("#ExportarExcel").hide();
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
		borraElementos();
		var arrData = $("#agregados").dataTable().fnGetData();
		for(var i = 0; i < arrData.length; i++  ){
			createInput('generaExcel', 'ep', arrData[i][0]);
			createInput('generaExcel', 'mes', arrData[i][1]);
			createInput('generaExcel', 'importe', arrData[i][2]);
		}				
		$("#generaExcel").submit();
		return true;
	}
	
	$(document).ready(function (){
		$("#ExportarExcel").hide();
		$("#btnAgregaClaves").button();
		$("#LimpiarTabla").button();
		$("#ExportarExcel").button();
		$("#btnBuscar").button();
		$(function() {		
      		$('#dialog').dialog({
      			autoOpen: false,
      			width: 800,
      			height:500
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
			Width: "800px"
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
			iDisplayLength: 10,
			sScrollY: "250px", 
			sScrollX: "800px",
			Height: "250px",
			Width: "800px"
		});	
	});
	
function toggleChecked(status) {
	$("#tablaCLC input").each( function() {
		$(this).attr("checked",status);
	});
}

function buscarCxp(){
	$.blockUI({message: "Procesando espere ......"});
	//$('#tablaCLC').dataTable().fnClearTable();
	var idCXP = $("#caNoRecibo").val();
	var elParametro = "caNoContrarrecibo = '"+idCXP+"'";
	var datos = "sinDatos";
	//Buscar el Folio Para Mostrar Sus Detalles
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "TPAGADOENCABEZADOR", Campos:"cTipoPago,nFolioPagado,caNoContrarrecibo, isnull(cDocumentoHaplicado, 'sinAplicar') cDocumentoHaplicado,nFolioSICOP,nFolioSIAFF,CASE cTipoPago WHEN 'PAGOOBRA' THEN 'COMPROMISO' WHEN 'PAGODIVERSO' THEN 'COMPROMISO' WHEN 'PAGODIRECTO' THEN 'DIRECTA' WHEN 'RELACIONGASTOS' THEN 'DIRECTA' WHEN 'NOMINA' THEN 'COMPROMISO' when 'AJENAS' then 'DIRECTA' when 'FEDERALIZADO' then 'COMPROMISO' ELSE '' END AS dTipoPago", Param:elParametro, Order:"", MaxReg: "10", ajax: 'true'}, function(j){
				
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
						$("#tipoRectificacion").val(tipoRectificacion);
						datos = "datos";
				}		
				
				if(cDocumentoHaplicado == 'C' ){
					alert("El Documento Esta Cancelado");
					return;
					
				}else if(cDocumentoHaplicado == 'sinAplicar' || cDocumentoHaplicado == ""){					
					alert("El Documento No Esta Aplicado");
					return;					
				}
				
				// Si Encuentra La Cuenta Por Pagar Buscar Sus Detalles
				
				if(datos == "datos"){
					//$('#tablaCLC').dataTable().fnClearTable();
					
					buscarCXPVista(caNoContrarrecibo);
					
				}else{
					alert("No se Encontro Informacion Con La Solicitud de Pago Ingresada");
				}
	
		});
	$.unblockUI();
	}

function buscarCXPVista(caNoContrarrecibo){
	$('#dialog').dialog('option', 'modal', true).dialog('open');
	var szTabla = "V_TPAGADO";
	var camposWhere = " caNoContrarrecibo = '"+caNoContrarrecibo+"'";
	var elParametro = "";
	var order = "";
	$("#tipoPago").val('');
		
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:camposWhere, Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j){		
		for (var i = 0; i < j.length; i++){
			var tipoPago = j[i].Col0;
			$("#tipoPago").val(tipoPago);
		}		
	 });
	var locationV = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_TPagado&qw=caNoContrarrecibo = '"+caNoContrarrecibo+"'";
	var al = [
			{ sName: "checknDocRenglon",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "clc",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "folioDependenciaCLC",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "caNoContrarrecibo",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "EP",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"150px"},
			{ sName: "cMes",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"25px"},
			{ sName: "mImporteNeto",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px"}
		];
	
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
		sScrollY: "300px", 	
		sScrollX: "1200px",		
		Height: "400px",
		aoColumns:al
	});	
} 

function agregarClave(){
	$("#busqueda").hide();
	$("#ExportarExcel").show();
	$("#dialog").dialog('close');
	var oTable = $('#tablaCLC').dataTable();
	var aData = oTable.fnGetData();
	var arrlist = new Array();
	var i=0;
	$('#tablaCLC input:checked').each(function(idx, elm){
		var paso = aData[oTable.fnGetPosition($(this).closest('tr')[0])];
		arrlist[i] = new Array(paso[4],paso[5],paso[6]);
		i++;
	});		
	$("#agregados").dataTable().fnAddData( arrlist );
}
	</script>

	</head>

	<body id="dt_example">
		<div id="container" class="container SyCData">
			<h1>
				Rectificaciones Presupuestales
			</h1>
			<div id="busqueda" style="display: block;">
				Cuenta Por Pagar
				<input type="text" name="caNoRecibo" id="caNoRecibo" size=30>
				<input type="button" name="btnBuscar" id="btnBuscar" value="Buscar" >
			</div>
			<div id="dialog" title="Detalle de Reintegros">
				<table id="tablaCLC" class="display">
					<thead>
							<tr>
								<th>Sel<input type="checkbox" onclick="toggleChecked(this.checked)"></th>
								<th>Folio SIAFF</th>
								<th>Folio Dependencia CLC</th>
								<th>Cuenta Por Pagar</th>
								<th>Estructura Programatica</th>
								<th>Mes</th>
								<th>Importe</th>
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
								<th>Estructura Programatica</th>
								<th>Mes</th>
								<th>Importe</th>
							</tr>
					</thead>
					<tbody>
					</tbody>
			</table>
			<div style="text-align:right;">
				<form id="generaExcel" name="generaExcel" action="../gstnmngr/RectificacionPresupuestaria" method="post" >
							<input type="hidden" id="tipoPago" name="tipoPago"/>
							<input type="hidden" id="generaExcel" name="generaExcel" value="1"/>
							<input type="hidden" id="cuentaPorPagar" name="cuentaPorPagar"/>
							<input type="hidden" id="nFolioSICOP" name="nFolioSICOP"/>
							<input type="hidden" id="nFolioSIAFF" name="nFolioSIAFF"/>
							<input type="hidden" id="tipoRectificacion" name="tipoRectificacion"/>
							<input type="button" id="ExportarExcel" name="ExportarExcel" value="Exportar Excel" onclick="return generaInputs()" />
							<input type="button" id="LimpiarTabla" value="Limpiar Tabla" onclick="limpiaTabla();"/>
				</form>
			</div>
		</div>
	</body>
</html>
