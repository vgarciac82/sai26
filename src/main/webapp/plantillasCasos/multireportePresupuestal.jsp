<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.gestion.core.*"%>
<%@page import="com.syc.gestion.servlet.*"%>
<%@page	import="com.syc.gestion.util.*"%>
<%@page import="java.util.*"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>

<%
	String centroContable = null;
	//Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String UR = usuario.getU_UR();

	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

	String U_Login = usuario.getLogin();
	usuario.getU_Ramo();
	String cUnidadResponsable = usuario.getU_UR();
	
	String Meses[] = { "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE" };
	String Control[] = {"ProgramaPresupuestario", "UnidadResponsableEP", "Cartera", "Partida", "cUnidadEjecutora"};

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Multireporte Presupuestos</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<style type="text/css" title="currentStyle">
#feedback {
	font-size: 1.4em;
}

#selectable .ui-selecting {
	background: #FECA40;
}

#selectable .ui-selected {
	background: #F39814;
	color: white;
}

#selectable {
	list-style-type: none;
	margin: 0;
	padding: 0;
	width: 60%;
}

#selectable li {
	margin: 3px;
	padding: 0.4em;
	font-size: 1.4em;
	height: 18px;
}
</style>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript">
	var oTable;
	var oTableEp;
	var giRedraw = false;
	var catalogoEp;
	
		function limpiarSesion()
	{
		window.location.href="multireportePresupuestal.jsp?id=<%=request.getParameter("id")%>";//Esto recarga la pagina
	}
	
	function Grid()
	{
		window.open('gridMultiReportePresupuestal.jsp?id=<%=request.getParameter("id")%>', 'gridMultiReportePresupuestal', 'status=1, width=850px, height=500px scrollbars=yes');
		//if ($.trim(document.getElementById("ep").value)!=""){
			//window.alert($.trim(document.getElementById("ep").value));
			//rellenaCampos();
		//}
	 	return false;
	}
	
	function onPostDisplay(id_oper) {
		return true;
	}

	function onPostSubmit(id_oper) {
		return true;
	}

	$(document).ready(function() {
		//Inicializa las ayudas. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.AyudaSyC").subIniciaDlg();

		//Inicializa el autocompletar. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.autoCompletaSyC").subIniciaAutoCompleta();

		//Crea el DataTable con los campos de config. minimos. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		catalogoEp = $('#dt_catalogo').dataTable({
			"bPaginate" : true,
			"bLengthChange" : true,
			"bFilter" : false,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth" : false,
			"sScrollY" : 270,
			"sScrollYInner" : "100%",
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"sScrollX" : "100%",
			"sScrollXInner" : "110%",
			"bScrollCollapse" : true,
			oLanguage : {
				sProcessing : "Procesando...",
				sLengthMenu : "Mostrar _MENU_ registros",
				sZeroRecords : "No hay registros a mostrar",
				sEmptyTable : "No hay datos en la tabla",
				sLoadingRecords : "Cargando...",
				sInfo : "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty : "Registro 0 al 0 de 0",
				sInfoFiltered : "(filtered from _MAX_ total entries)",
				sInfoPostFix : "",
				sInfoThousands : ",",
				sSearch : "Filtro:",
				oPaginate : {
					sFirst : "Primero",
					sPrevious : "Ant.",
					sNext : "Sigte.",
					sLast : "&Uacute;ltimo"
				}
			}
		});

		$("#dt_catalogo tbody").click(function(event) {
			$(oTableEp.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');
		});
		
		oTableEp = $('#dt_catalogo').dataTable();

		//Detalle
		var camposWhere = " EP";
		var elParametro =" nFolioPagoAnticipado = '"+$("#nFolioPagoAnticipado").val()+"'";
		var order = "";
	
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "TPAGOANTICIPADODETALLE", Campos:camposWhere, Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j)
		{	
			for (var i = 0; i < j.length; i++)
 			{
 				var tipoPago = new Array(); 
 				tipoPago[i] = j[i].Col0;
 				$('#dt_catalogo').dataTable().fnAddData([tipoPago[i]]);
 			}	
		});
	});

	function fnGetSelected(oTableLocal) {
		var aReturn = new Array();
		var aTrs = oTableLocal.fnGetNodes();

		for ( var i = 0; i < aTrs.length; i++) {
			if ($(aTrs[i]).hasClass('row_selected')) {
				aReturn.push(aTrs[i]);
			}
		}
		return aReturn;
	}

	function borrarEp() {
		if (fnGetSelected(oTableEp).length <= 0) 
		{
			alert("Se debe seleccionar un registro");
		} 
		else 
		{
			var anSelected = fnGetSelected(oTableEp);
			oTableEp.fnDeleteRow(anSelected[0]);
		}
	}

	function fnClickAddRow() {
		$('#dt_catalogo').dataTable().fnAddData([ ".1", ".2", ".3" ]);
	}
	
	function fnGeneraReporte() {
		var principal = parent.parent.document;
		var asunto    = document.datawork;

		var ProgramaPresupuestario	= document.getElementById('ProgramaPresupuestario').value;
		var hProgramaPresupuestario	= document.getElementById('hProgramaPresupuestario').value;
		var Partida					= document.getElementById('Partida').value;
		var hPartida				= document.getElementById('hPartida').value;
		var UnidadResponsableEP		= document.getElementById('UnidadResponsableEP').value;
		var hUnidadResponsableEP	= document.getElementById('hUnidadResponsableEP').value;
		var cUnidadEjecutora 		= document.getElementById('cUnidadEjecutora').value;
		var hcUnidadEjecutora 		= document.getElementById('hcUnidadEjecutora').value;
		var Cartera 				= document.getElementById('Cartera').value;
		var hCartera 				= document.getElementById('hCartera').value;
		var chkReintegros 			= ($("#chkReintegros").is(':checked')) ? "1" : "0";
		var chkRectificaciones 		= ($("#chkRectificaciones").is(':checked')) ? "1" : "0";
		var chkAdecuaciones 		= ($("#chkAdecuaciones").is(':checked')) ? "1" : "0";
		var chkPagosAnticipados 	= ($("#chkPagosAnticipados").is(':checked')) ? "1" : "0";
		var chkPlurianuales 		= ($("#chkPlurianuales").is(':checked')) ? "1" : "0";
		var chkOriginal 			= ($("#chkOriginal").is(':checked')) ? "1" : "0";
		var chkModificado 			= ($("#chkModificado").is(':checked')) ? "1" : "0";
		var chkAmpAutorizada 		= ($("#chkAmpAutorizada").is(':checked')) ? "1" : "0";
		var chkRedAutorizada		= ($("#chkRedAutorizada").is(':checked')) ? "1" : "0";
		var chkAmpenTramite 		= ($("#chkAmpenTramite").is(':checked')) ? "1" : "0";
		var chkRedenTramite  		= ($("#chkRedenTramite ").is(':checked')) ? "1" : "0";
		var chkReienTramite 		= ($("#chkReienTramite").is(':checked')) ? "1" : "0";
		var chkRectificación 		= ($("#chkRectificación").is(':checked')) ? "1" : "0";
		var chkRedSHCPenTramite 	= ($("#chkRedSHCPenTramite").is(':checked')) ? "1" : "0";
		var chkRedSHCPAplicada 		= ($("#chkRedSHCPAplicada").is(':checked')) ? "1" : "0";
		var chkApartado 			= ($("#chkApartado").is(':checked')) ? "1" : "0";
		var chkPrecomprometido 		= ($("#chkPrecomprometido").is(':checked')) ? "1" : "0";
		var chkComprometido 		= ($("#chkComprometido").is(':checked')) ? "1" : "0";
		var chkDevengado 			= ($("#chkDevengado").is(':checked')) ? "1" : "0";
		var chkEjernoPagado 		= ($("#chkEjernoPagado").is(':checked')) ? "1" : "0";
		var chkDisponibleNeto 		= ($("#chkDisponibleNeto").is(':checked')) ? "1" : "0";
		var chkDisponibleBruto 		= ($("#chkDisponibleBruto").is(':checked')) ? "1" : "0";
		var chkEjercidoPagado 		= ($("#chkEjercidoPagado").is(':checked')) ? "1" : "0";
	
		if ((chkReintegros=="0")&&(chkRectificaciones=="0")&&(chkAdecuaciones=="0")&&(chkPagosAnticipados=="0")&&(chkPlurianuales=="0")){
			chkReintegros="1";
			chkRectificaciones="1";
			chkAdecuaciones="1";
			chkPagosAnticipados="1";
			chkPlurianuales=="1";
		}
			
	
		var EPs = "";
		var reporte = "reporte";
		var rows = oTableEp.dataTable().fnGetNodes();
		if (rows.length > 0)
		{
			for (var i = 0 ; i < rows.length ; i++)
			{
				EPs = EPs + "'" + $(rows[i]).find("td:eq(0)").html() + "', ";
			}
			EPs = EPs.substring(0, EPs.length - 2);
		}
		var param = "&opcion=" + reporte 
			 + "&EPs=" + EPs
			 + "&ProgramaPresupuestario=" + ProgramaPresupuestario
			 + "&Partida=" + Partida
			 + "&UnidadResponsableEP=" + UnidadResponsableEP
			 + "&cUnidadEjecutora=" + cUnidadEjecutora
			 + "&Cartera=" + Cartera
			 + "&chkReintegros=" + chkReintegros
			 + "&chkRectificaciones=" + chkRectificaciones
			 + "&chkAdecuaciones=" + chkAdecuaciones
			 + "&chkPagosAnticipados=" + chkPagosAnticipados
			 + "&chkPlurianuales=" + chkPlurianuales
			 + "&chkOriginal=" + chkOriginal
			 + "&chkModificado=" + chkModificado
			 + "&chkAmpAutorizada=" + chkAmpAutorizada
			 + "&chkRedAutorizada=" + chkRedAutorizada
			 + "&chkAmpenTramite=" + chkAmpenTramite
			 + "&chkRedenTramite=" + chkRedenTramite
			 + "&chkReienTramite=" + chkReienTramite
			 + "&chkRectificacion=" + chkRectificación
			 + "&chkRedSHCPenTramite=" + chkRedSHCPenTramite
			 + "&chkRedSHCPAplicada=" + chkRedSHCPAplicada
			 + "&chkApartado=" + chkApartado
			 + "&chkPrecomprometido=" + chkPrecomprometido
			 + "&chkComprometido=" + chkComprometido
			 + "&chkDevengado=" + chkDevengado 
			 + "&chkEjernoPagado=" + chkEjernoPagado 
			 + "&chkDisponibleNeto=" + chkDisponibleNeto 
			 + "&chkDisponibleBruto=" + chkDisponibleBruto 
			 + "&chkEjercidoPagado=" + chkEjercidoPagado
			 + "&sULogin=<%=U_Login%>"  ;
		//alert("param=" + param);

		$("#GenerarReporte").attr("disabled", true);
		$("#Limpiar").attr("disabled", true);
		window.open('../admin/MultiReportePresupuestal2.jsp?'+ param, 'MultiReporteResultado2','toolbar=no,menubar=no,scrollbars=yes,resizable=yes');
		$("#GenerarReporte").attr("disabled", false);
		$("#Limpiar").attr("disabled", false);
		return true;
	}

	$(function() {
		  enable_cb();
		  $("#chkReintegros").click(enable_cb);
		  $("#chkRectificaciones").click(enable_cb);
		  $("#chkAdecuaciones").click(enable_cb);
		  $("#chkPagosAnticipados").click(enable_cb);
		  $("#chkPlurianuales").click(enable_cb);
		});
	
	function enable_cb() {
	  if ($("input[name=chkReintegros]").is(":checked")) {
		  	
		    $("#grupo1").show(); 
		    
	  } else {
		  
		    $("#grupo1").hide();   
	  } 
	  
	   if ($("input[name=chkRectificaciones]").is(":checked")) {
	   		
	    	$("#grupo2").show(); 
	     
	  } else {
	     	$("#grupo2").hide();    	
	  } 
	  
	   if ($("input[name=chkAdecuaciones]").is(":checked")) {
	   		$("#grupo3").show();
	  } else {
	        $("#grupo3").hide();
	   	   
	  } 
	 
	  
	}  
		
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form id="FormContrato" name="FormContrato">
		<input type="hidden" name="Ep" id="Ep" />
		<input type="hidden" name="epMonto" id="epMonto" />
		<input type="hidden" name="montoTotal" id="montoTotal" />
		<input type="hidden" name="nrenglonEp" id="nrenglonEp" />
		<input type="hidden" name="nDocRenglon" id="nDocRenglon" />
		<input type="hidden" name="existe" id="existe" value="0" />
		<input type="hidden" name="centroContable" id="centroContable" value="<%=centroContable%>" />
		<input type="hidden" name="U_Login" id="U_Login" value="<%=U_Login%>" />
		<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value="<%=cUnidadResponsable%>" />
		<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value="<%=UR%>" />
		<input type="hidden" name="nOrden" id="nOrden" value=""/>

		<h1>Multireporte Presupuestal</h1>
		
		<fieldset>
		<legend>Estructura Programatica</legend>
			<table align="left">
				<tr>
					<td align="left">Estructura Programática:</td>
					<td>
						<input id="ep" name="ep" type="text" value="" size="64" maxlength="64" class=""/>
						<input type="button" value="..." onclick="Grid()"/>
					</td>
					<td colspan="2">
					</td>
				</tr>
 				<tr>
					<td align="left">Programa Presupuestario:</td>
					<td>
						<input id="hProgramaPresupuestario" name="hProgramaPresupuestario" type="text" value="" size="40" maxlength="50" class="AyudaSyC  "/>
						<input id="ProgramaPresupuestario" name="ProgramaPresupuestario" type="hidden" value="" size="5" maxlength="5" class=""/>
					</td>
					<td align="left">Partida:</td>
					<td>
						<input id="hPartida" name="hPartida" type="text" value="" size="40" maxlength="50" class="AyudaSyC  "/>
						<input id="Partida" name="Partida" type="hidden" value="" size="5" maxlength="5" class=""/>
					</td>
				</tr>
				<tr>
					<td align="left">Unidad Responsable:</td>
					<td>
						<input id="hUnidadResponsableEP" name="hUnidadResponsableEP" type="text" value="" size="40" maxlength="50" class="AyudaSyC  "/>
						<input id="UnidadResponsableEP" name="UnidadResponsableEP" type="hidden" value="" size="5" maxlength="5" class=""/>
					</td>
					<td align="left">Unidad Ejecutora:</td>
					<td>
						<input id="hcUnidadEjecutora" name="hcUnidadEjecutora" type="text" value="" size="40" maxlength="50" class="AyudaSyC  "/>
						<input id="cUnidadEjecutora" name="cUnidadEjecutora" type="hidden" value="" size="5" maxlength="5" class=""/>
					</td>
				</tr>
				<tr>
					<td align="left">Cartera:</td>
					<td>
						<input id="hCartera" name="hCartera" type="text" value="" size="40" maxlength="50" class="AyudaSyC  "/>
						<input id="Cartera" name="Cartera" type="hidden" value="" size="5" maxlength="5" class=""/>
					</td>
					<td colspan="2">
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset>
			<legend>Tipo de Documento </legend>
				<table align="center" height="10">
					<tr>
						<td width="5%" align="right">
							<input name="chkReintegros" id="chkReintegros" type="checkbox" value="1"/>
						</td>
						<td width="15%" align="left">
							Reintegros
						</td>
						<td width="5%" align="right">
							<input name="chkRectificaciones" id="chkRectificaciones" type="checkbox" value="1"/>
						</td>
						<td width="15%" align="left">
							Rectificaciones
						</td>
					    <td width="5%" align="right">
							<input name="chkAdecuaciones" id="chkAdecuaciones" type="checkbox" value="1"/>
						</td>
						<td width="15%" align="left">
							Adecuaciones
						</td>
						<td width="5%" align="right">
							<input name="chkPagosAnticipados" id="chkPagosAnticipados" type="checkbox" value="1"/>
						</td>
						<td width="15%" align="left">
							Pagos Anticipados
						</td>
						<td width="5%" align="right">
							<input name="chkPlurianuales" id="chkPlurianuales" type="checkbox" value="1"/>
						</td>
						<td width="15%" align="left">
							Plurianuales
						</td>
					</tr>
				</table>
		</fieldset>
		<fieldset>
			<legend>Tipo de Presupuesto </legend>
				<table align="center" height="10">
					<tr bgcolor="#cccccc" id="grupo1" >
					<td width="2%" align="right"  >
							<input name="chkReienTramite" id="chkReienTramite" type="checkbox" value="1" />
						</td>
						<td width="18%" align="left">
							Reintegro en Tramite
						</td>
						<td width="2%" align="right">
							<input name="chkRedAutorizada" id="chkRedAutorizada" type="checkbox" value="1" />
						</td>
						<td width="18%" align="left">
							Reducción Autorizada
						</td>
						<td width="2%" align="right">
							<input name="chkDisponibleNeto" id="chkDisponibleNeto" type="checkbox" value="1" />
						</td>
						<td width="18%" align="left">
							Disponible Neto
						</td>
						 <td width="2%" align="right">
							<input name="chkComprometido" id="chkComprometido" type="checkbox" value="1" />
						</td>
						<td width="18%" align="left">
							Comprometido
						</td>
						 <td width="2%" align="right">
							<input name="chkEjercidoPagado" id="chkEjercidoPagado" type="checkbox" value="1" />
						</td>
						<td width="18%" align="left">
							Ejercido Pagado
						</td>
					</tr>
					<tr bgcolor="#cccccc" id="grupo2">
						<td width="2%" align="right"  >
							<input name="chkReienTramite" id="chkReienTramite" type="checkbox" value="1" />
						</td>
						<td width="18%" align="left">
							Reintegro en Tramite
						</td>
						<td width="2%" align="right" >
							<input name="chkRedenTramite" id="chkRedenTramite" type="checkbox" value="1" />
						</td>
						<td width="18%" align="left">
							Reducción en Tramite
						</td>
						<td width="2%" align="right" >
							<input name="chkEjernoPagado" id="chkEjernoPagado" type="checkbox" value="1" />
						</td>
						<td width="18%" align="left">
							Ejercido no Pagado
						</td>
						  <td>
							<input name="chkRectificación" id="chkRectificación" type="checkbox" value="1" />
						</td>
						<td>
							Rectificación
						</td>
					
					</tr>
					<tr bgcolor="#cccccc" id="grupo3">
					<td>
							<input name="chkDisponibleBruto" id="chkDisponibleBruto" type="checkbox" value="1" />
						</td>
						<td>
							Disponible Bruto
						</td>
						<td width="2%" align="right">
							<input name="chkModificado" id="chkModificado" type="checkbox" value="1" />
						</td>
						<td width="18%" align="left">
							Modificado
						</td>
						<td width="2%" align="right">
							<input name="chkAmpenTramite" id="chkAmpenTramite" type="checkbox" value="1" />
						</td>
						<td width="18%" align="left">
							Ampliación en Tramite
						</td>
						 <td width="2%" align="right">
							<input name="chkAmpAutorizada" id="chkAmpAutorizada" type="checkbox" value="1" />
						</td>
						<td width="18%" align="left">
							Ampliación Autorizada
						</td>
						<td>
							<input name="chkRedSHCPenTramite" id="chkRedSHCPenTramite" type="checkbox" value="1" />
						</td>
						<td width="18%" align="left">
							Reducción SHCP en Tramite
						</td>
						<td>
							<input name="chkRedSHCPAplicada" id="chkRedSHCPAplicada" type="checkbox" value="1"/>
						</td>
						<td width="18%" align="left">
							Reducción SHCP Aplicada
						</td>
					
					</tr>

					<tr bgcolor="#cccccc" id="grupo4">
						<td width="2%" align="right">
							<input name="chkOriginal" id="chkOriginal" type="checkbox" value="1" />
						</td>
						<td width="18%" align="left">
							Original
						</td>

						<td  width="2%" align="right">
							<input name="chkApartado" id="chkApartado" type="checkbox" value="1" />
						</td>
						<td width="18%" align="left">
							Apartado
						</td>
						<td  width="2%" align="right">
							<input name="chkPrecomprometido" id="chkPrecomprometido" type="checkbox" value="1" />
						</td>
						<td width="18%" align="left">
							Precomprometido
						</td>
					   
						<td  width="2%" align="right">
							<input name="chkDevengado" id="chkDevengado" type="checkbox" value="1" />
						</td>
						<td width="18%" align="left">
							Devengado
						</td>
						
					</tr>
					<tr>

						<td colspan="4">
							&nbsp;
						</td>
					</tr>
				</table>
		</fieldset>
		<br>
		<table align="center"  height="10">
			<tr>
				<td>
					<input type="button" id="GenerarReporte" name="GenerarReporte" value="Generar Reporte" onClick="return fnGeneraReporte();">
					<input type="button"  id="Limpiar" name="Limpiar" value="Limpiar" onClick="limpiarSesion();"/>
				</td>
			</tr>
		</table>
		<fieldset>
			<legend>Claves Presupuestales</legend>
				<table id="dt_catalogo" class="display" cellspacing="0" cellpadding="2" align="center">
					<thead>
						<tr>
							<th>Clave presupuestal</th>
						</tr>
					</thead>
				</table>
				<table align="right">
				<tr>
				<td>
					<input type="button" value="Eliminar" id="buscarEliminar" onclick="borrarEp()">
				</td>
			</tr>
			</table>
		</fieldset>
	</form>
</body>
</html>