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
		<title>Rectificaciones Presupuestales Capítulo Mil</title>

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
	
	var agregados;
	var debes;
	var actualizado = false;
	
	function limpiaTabla(){
		$("#busqueda").show();
		$('#agregados').dataTable().fnClearTable();
		$('#debes').dataTable().fnClearTable();
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
		var tipoMov;
		borraElementos();
		if(!actualizado)
			actualizaInfo();
		var arrData = $("#agregados").dataTable().fnGetData();
		var arrDataDebe = $("#debes").dataTable().fnGetData();
		for(var i = 0; i < arrData.length; i++  ){
			createInput('generaExcel', 'ep', arrData[i][1]);
			createInput('generaExcel', 'mes', arrData[i][2]);
			createInput('generaExcel', 'importe', arrData[i][3]);
			createInput('generaExcel', 'tipoConcepto', arrData[i][4]);
			createInput('generaExcel', 'tipoMovimiento', arrData[i][5]);
			tipoMov = arrData[i][5];
		}
		for(var i = 0; i < arrDataDebe.length; i++  ){
			createInput('generaExcel', 'epDebe', arrDataDebe[i][1]);
			createInput('generaExcel', 'mesDebe', arrDataDebe[i][2]);
			createInput('generaExcel', 'importeDebe', arrDataDebe[i][3]);
			createInput('generaExcel', 'concepto', arrDataDebe[i][4]);
			createInput('generaExcel', 'tipoMovimiento', tipoMov);
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
		debes = $('#debes').dataTable({
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
			iDisplayLength: 10
		});		
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
			iDisplayLength: 10
		});
		agregados = $('#agregados').dataTable({      
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
			iDisplayLength: 10
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
	var cxp = $("#caNoReciboCLC").val();
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
					
					buscarCXPVista(caNoContrarrecibo,cxp);
					
				}else{
					alert("No se Encontro Informacion Con La Solicitud de Pago Ingresada");
				}
	
		});
	$.unblockUI();
	}

function buscarCXPVista(caNoContrarrecibo,cuentaPorPagar){ //CaNocontrarrecibo es el folio con el que se paga en el sai
														//CUenta por pagar es la cuenta por pagar de la clc
	$('#dialog').dialog('option', 'modal', true).dialog('open');
	var szTabla = "V_TPAGADO";
	var camposWhere = " caNoContrarrecibo = '"+caNoContrarrecibo+"'";
	var elParametro = "";
	var order = "";
	$("#cuentaPorPagarCLC").val(cuentaPorPagar);
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:camposWhere, Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j){		
		for (var i = 0; i < j.length; i++){
			var tipoPago = j[i].Col0;
			$("#tipoPago").val(tipoPago);
		}		
	 });
	var	locationV='';
	if(cuentaPorPagar!='')
	 	locationV = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=V_tPagadoMil&qw=caNoContrarreciboCLC = '"+caNoContrarrecibo+"' and caNoContrarrecibo = '"+cuentaPorPagar+"'";
	else{
		alert('Debe especificar la cuenta por pagar');
		return;
	}
		//locationV = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=V_tPagadoMil&qw=caNoContrarrecibo = '"+caNoContrarrecibo+"'";
	var al = [
			{ sName: "checknDocRenglon",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "clc",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "folioDependenciaCLC",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "caNoContrarrecibo",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "caNoContrarreciboCLC",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "EP",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"150px"},
			{ sName: "cMes",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"25px"},
			{ sName: "mImporteNeto",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px"},
			{ sName: "tipoConcepto",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "movimiento",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" }
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
		aoColumns:al
	});	
} 

function agregarClave(){
	$("#busqueda").hide();
	$("#dialog").dialog('close');
	var oTable = $('#tablaCLC').dataTable();
	var aData = oTable.fnGetData();
	var arrlist = new Array();
	var i=0;
	$('#tablaCLC input:checked').each(function(idx, elm){
		var paso = aData[oTable.fnGetPosition($(this).closest('tr')[0])];
		arrlist[i] = new Array(i+1,paso[5],paso[6],Number(paso[7]).toFixed(2),paso[8],paso[9]);
		$("#tipoConcepto").val(paso[5]);
		i++;
	});		
	$("#agregados").dataTable().fnAddData( arrlist );
	$("#agregados tbody").click(function(event){
		var rowIndex = agregados.fnGetPosition($(event.target.parentNode)[0]);
		editarEP($(event.target.parentNode)[0],rowIndex+1);
		
		//editRow(oTable,rowIndex);		
	});
}

function editarEP(epEditar,index){
	window.open('ayudaEps.jsp?id='+index, 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
	$("#ExportarExcel").show();
	 //return false;
	//window.open('epsRectificacion.jsp', 'EPsRectificacionGrid', 'status=1, width=900px, height=900px');
	//$(epEditar).val($('#epGrid').val());
}

function actualizaInfo(){
	var arrDataDebes = $("#debes").dataTable().fnGetData();
	for(var i = 1; i <= arrDataDebes.length; i++){
		var mes = $('#cMes'+i).val();
		if(!(mes>=1 && mes<=12)){
			alert("mes incorrecto");
			return false;
		}
		var importe = $('#importe'+i).val();
		$('#debes').dataTable().fnUpdate(mes,(i-1),2);
		$('#debes').dataTable().fnUpdate(importe,(i-1),3);
	}
	actualizado = true;
}

	</script>

	</head>
<br/>
	<body id="dt_example">
		<div id="container" class="container">
			<div class="card-header"> <h3 id="tituloReintegro"> Rectificaciones Presupuestales Capítulo Mil </h3> </div>
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

			<div id="dialog" title="Detalle de Rectificaciones" class="container">
				<table id="tablaCLC" class="table table-striped">
					<thead>
						<tr>
							<th>Sel<input type="checkbox" onclick="toggleChecked(this.checked)"></th>
							<th>Folio SIAFF</th>
							<th>Folio Dependencia CLC</th>
							<th>Cuenta Por Pagar</th>
							<th>Cuenta Por Pagar CLC</th>
							<th>Estructura Programatica</th>
							<th>Mes</th>
							<th>Importe</th>
							<th>Tipo Concepto</th>
							<th>Tipo Movimiento</th>
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
			
			<div class="container">
				<br/>
				<h5> DICES </h5>
 				<hr class="mt-3">
			
				<table id="agregados" class="table table-striped">
					<thead>
						<tr>
							<th>Renglon</th>
							<th>Estructura Programatica</th>
							<th>Mes</th>
							<th>Importe</th>
							<th>Tipo Concepto</th>
							<th>Tipo Movimiento</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
			</div>
			
			<div class="container">
				<br/>
				<h5> DEBES </h5>
 				<hr class="mt-3">
 				
				<table id="debes" class="table table-striped">
					<thead>
						<tr>
							<th>Renglon</th>
							<th>Estructura Programatica</th>
							<th>Mes</th>
							<th>Importe</th>
							<th>Tipo Concepto</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
			</div> 
			
			<br/>
			
			<form id="generaExcel" name="generaExcel" action="../gstnmngr/RectificacionPresupuestaria" method="post" >
				<input type="hidden" id="tipoPago" name="tipoPago"/>
				<input type="hidden" id="generaExcel" name="generaExcel" value="2"/>
				<input type="hidden" id="cuentaPorPagar" name="cuentaPorPagar"/>
				<input type="hidden" id="cuentaPorPagarCLC" name="cuentaPorPagarCLC"/>
				<input type="hidden" id="nFolioSICOP" name="nFolioSICOP"/>
				<input type="hidden" id="nFolioSIAFF" name="nFolioSIAFF"/>
				<input type="hidden" id="tipoRectificacion" name="tipoRectificacion"/>

				<div style="text-align:right;" class="container">		
					<div class="row d-flex">							
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
						</div>					
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<input type="button" class="btn btn-secondary btn-sm" id="ExportarExcel" name="ExportarExcel" value="Exportar Excel" onclick="return generaInputs()" />											
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<input type="button" class="btn btn-secondary btn-sm" id="LimpiarTabla" value="Limpiar Tabla" onclick="limpiaTabla();"/>											
						</div>
					</div>					
				</div>
			
			</form>			
		</div>
	</body>
</html>
