<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%
String cCentroContable="";
String cUR = "";
String algo = "";
String mensaje="";

Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);

//Valida Centro de Costos
if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
	cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
}

if (cCentroContable.isEmpty() || cCentroContable.equals("")){
	mensaje="Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
}

cUR = usuario.getU_UR();
algo = usuario.getLogin();

 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Compromiso Solicitud de Recursos por Pagar</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

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
var doTable;
var coTable;
var es_mx = {
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
		};
		
	$( document ).ready( function() {
			$("input.AyudaSyC").subIniciaDlg();
		    $("input.autoCompletaSyC").subIniciaAutoCompleta();
			$("#cIdContratoCompromiso").change(function(){
				cargaDatosCompromiso();
			});
			cargaDataTableCompromisos();
			cargaDataTableCompromisosDet();
			creaDataTableCompromisoSolicitud();
			
			$( "#dlgLigaCompromiso" ).dialog({
				autoOpen: false,
				height: 300,
				width: 650,
				modal: true,
				buttons:{
					"Aceptar": function(){ guardaInfo(); },
					"Cancelar":function(){ $( "#dlgLigaCompromiso" ).dialog("close"); }
				}
			});
			
		$("#pbAceptar").button().click(function() {	
			
			alert ("Creamos encabezado e Insertamos detalle en tIngresoPago");	
			
			$("#nFolioPago").val($("#nFolioComp").val());
			$("#cTipoPago").val("COMPROMISO");
			$("#cEsRadicado").val("S");
			$("#cCentroContable").val( "<%=cCentroContable%>");		
			$("#contrato").val($("#cIdContratoCompromiso").val());
			var totalIngresoPago = 0;
			var bandera = 0;
				
				queryFormPost("buscaIngresoPago", {async:false});
				
				if ($("#nExisteIngresoPago").val() == "0"){
					queryFormPost("encabezadoIngresoPagoComp", {async:false});
					queryFormPost("buscaIngresoPago", {async:false});
				}
				else{
					alert("Esta Relacion ya existe, Favor de Rectificar!!!");
					bandera = 1;
				}
				
				if (bandera == 0){
					var relaciones = $("#dtCompromisoSolicitud").dataTable().fnGetData();
					
					for( i = 0; i < relaciones.length; i++ ){
						
						if( relaciones[i][0] ){	
										
							$("#nExisteIngresoPago").val(); // nFolioIngresoPago
							$("#nFolioRegistroIngreso").val(relaciones[i][1]); //nFolioRegistroIngreso
							$("#cEPIngresoPago").val(relaciones[i][2]);//EP
							$("#nMesIngresoPago").val(relaciones[i][3]);//nMes
							$("#mImporteIngresoPago").val(relaciones[i][4]);//mImporte
							
							totalIngresoPago = totalIngresoPago + parseFloat(relaciones[i][4]);
							
							queryFormPost("detalleIngresoPago", {async:false});
						}
					}
					
					$("#mTotalIngresoPago").val(totalIngresoPago.toString());
					queryFormPost("updateIngresoPago",{async:false});
					//TODO: Cerrar Ventana o Inicializar 		
				}else{
					$("#dtCompromisoSolicitud").dataTable().fnClearTable();
					$("#pbAceptar").css("visibility","hidden");
					$("#pbBorrar").css("visibility","hidden");
				}
				
			});		
			
		$("#pbCancelar").button().click(function() { alert("Regresamos a Documentos");	});	
		
		$("#pbBorrar").button().click(function() { 
			$("#dtCompromisoSolicitud").dataTable().fnClearTable();
			$("#pbAceptar").css("visibility","hidden");
			$("#pbBorrar").css("visibility","hidden");
			});	
	} );
	
	function guardaInfo(){
		 $("#bEP").val();
		 $("#bMes").val();
		 $("#dispRadH").val();
		 $("#nFolioRad").val();
		 $("#nFolioComp").val();
		
		
		if (parseFloat($("#dispRad").val()) > parseFloat($("#dispRadH").val()) ){//Monto a Relacionara mayor que el Disp Rad
			alert("El monto no puede superar el Disponible Radicado");
			$("#dispRad").val($("#dispRadH").val());
		}else if (parseFloat($("#dispRad").val()) > parseFloat($("#monto").val()))  { //Monto a Relacionar mayor al Compromiso
			alert("El monto no puede superar el Compromios");
			$("#dispRad").val($("#dispRadH").val());
		}else if ((parseFloat($("#monto").val()) < parseFloat($("#dispRadH").val())) && (parseFloat($("#dispRad").val()) < parseFloat($("#monto").val())))  { //Monto a Relacionar mayor al Compromiso
			alert("No es necesario dividir el Compromiso, favor de rectificar!!!");
			$("#dispRad").val($("#dispRadH").val());
		}else { 
			
			//TODO: Validamos que sea el mismo folio del compromiso si no no permitimos la relacion
			
			$("#dtCompromisoSolicitud").dataTable().fnAddData(
   					[ $("#nFolioComp").val() //Folio Compromiso
   					, $("#nFolioRad").val() //Folio Anexo1
   					, $("#bEP").val() //EP
   					, $("#bMes").val() // Mes
   					, $("#dispRad").val() //Monto
   					]);
		    
			$("#dlgLigaCompromiso" ).dialog("close");
			$("#pbBorrar").css("visibility","visible");
			$("#dispRad").val("0.00");
			document.getElementById("nFolioRadicado").options.length = 0;
			
			if ($("#dtCompromisoSolicitud").dataTable().fnGetData().length ==$("#dtCompromisoDet").dataTable().fnGetData().length ){
				$("#pbAceptar").css("visibility","visible");
			}
			
		}
		
	}
	
	function cargaDatosCompromiso(){
		cargaDataTableCompromisos();
	}
	
	function cargaDataTableCompromisos(){
		var contrato = $("#cIdContratoCompromiso").val();
		if( "" == contrato )
			contrato = "-1-2";
			
		oTable = $('#dtCompromisos').dataTable(
				{
					"bPaginate" : false,
					"bLengthChange" : true,
					"bFilter" : true,
					"bSort" : true,
					"bInfo" : true,
					"bAutoWidth" : true,
					"sScrollY" : 270,
					"bJQueryUI" : true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType" : "full_numbers",
					"bScrollCollapse" : true,
					"bServerSide" : true,
					sAjaxSource : window.location.protocol + "//"
							+ window.location.host + "/"
							+ window.location.pathname.split("/")[1]
							+ "/crud?rt=t&ql=vCompPendientes&qw=cIdContrato='" + contrato + "'",
					aoColumns : [ {
						sName : "nFolioCompromiso"
					}, {
						sName : "cIdContrato"
					}, {
						sName : "fechaCompromiso"
					}, {
						sName : "total"
					} ],
					oLanguage : es_mx
				});

		$("#dtCompromisos tbody").click(function(event) {

			$(oTable.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});

			$(event.target.parentNode).addClass('row_selected');
			var aPos = oTable.fnGetPosition(event.target.parentNode);
			var aData = oTable.fnGetData(aPos);
			nFolioCompromiso = aData[0];
			$("#nFolioComp").val(nFolioCompromiso);
			var condicion = "nFolioCompromiso='" + nFolioCompromiso + "'";
			cargaDataTableCompromisosDet(condicion);
		});
	}
	
	function cargaDataTableCompromisosDet(cond) {

		if (!cond)
			cond = "1<>1";

		doTable = $('#dtCompromisoDet').dataTable(
				{
					"bPaginate" : false,
					"bLengthChange" : true,
					"bFilter" : true,
					"bSort" : true,
					"bInfo" : true,
					"bAutoWidth" : true,
					"sScrollX" : "900px",
					"sScrollY" : "500px",
					"bJQueryUI" : true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType" : "full_numbers",
					"bScrollCollapse" : true,
					"bServerSide" : true,
					sAjaxSource : window.location.protocol + "//"
							+ window.location.host + "/"
							+ window.location.pathname.split("/")[1]
							+ "/crud?rt=t&ql=vRelSRxPComp&qw=" + cond,
					aoColumns : [ {
						sName : "nFolioCompromiso"
					}, {
						sName : "ep"
					}, {
						sName : "cMes"
					}, {
						sName : "mImporte"
					}],
					oLanguage : es_mx
				});

		$("#dtCompromisoDet tbody").dblclick(function(event) {

			$(doTable.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});

			$(event.target.parentNode).addClass('row_selected');
			var aPos = doTable.fnGetPosition(event.target.parentNode);
			var aData = doTable.fnGetData(aPos);
			nFolioCompromiso = aData[0];
			
			$("#EP").val(aData[1]);
			$("#mes").val(aData[2]);
			$("#monto").val(aData[3]);
			
			$("#bEP").val($("#EP").val());
			$("#bMes").val($("#mes").val());
					
			querySelectPost("foliosRadicadoComp","nFolioRadicado", {async: false });
				
			$("#dlgLigaCompromiso").dialog("open");
			
		});
	}
	
	function creaDataTableCompromisoSolicitud(){
					
		coTable = $('#dtCompromisoSolicitud').dataTable(
				{
					"bPaginate" : false,
					"bLengthChange" : true,
					"bFilter" : true,
					"bSort" : true,
					"bInfo" : true,
					"bAutoWidth" : true,
					"sScrollY" : 270,
					"bJQueryUI" : true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType" : "full_numbers",
					"bScrollCollapse" : true,
					aoColumns : [ {	sName : "Compromiso"},
					              {	sName : "SRxP" },
					              {	sName : "EP" },
					              {	sName : "Mes"},
					              {	sName : "Monto"} ],
					oLanguage : es_mx
				});
	}
		
	function modificaMonto(){
		var importe = 0;
		
		importe = $("#nFolioRadicado option:selected").val();
		
		$("#dispRadH").val(importe);
		$("#disponible").val(importe);
		$("#nFolioRad").val($("#nFolioRadicado option:selected").text());
		
		if (importe > $("#monto").val()){
			$("#dispRad").val($("#monto").val());
		}else{
			$("#dispRad").val(importe);
		} 

		
	}
</script>

</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form id="FormContrato" name="FormContrato">
		<input type="hidden" id="bEP"  name="bEP"  value="0"/>
		<input type="hidden" id="bMes" name="bMes" value="0"/>
		<input type="hidden" id="nFolioRad" name="nFolioRad" value="0"/>
		<input type="hidden" id="nFolioComp" name="nFolioComp" value="0"/>
		<input type="hidden" id="dispRadH" name="dispRadH" value="0"/>
						
		<!--  Variables para insertar en tIngresoPago -->
		<input type="hidden" id="nFolioPago" name="nFolioPago" value="0"/>
		<input type="hidden" id="cTipoPago" name="cTipoPago" value="COMPROMISO"/>
		<input type="hidden" id="cEsRadicado" name="cEsRadicado" value="N"/>
		<input type="hidden" id="nExisteIngresoPago" name="nExisteIngresoPago" value="0"/>
		<input type="hidden" id="cCentroContable" name="cCentroContable">
		<input type="hidden" id="mTotalIngresoPago" name="mTotalIngresoPago" value="0"/>
		<input type="hidden" id="nFolioRegistroIngreso" name="nFolioRegistroIngreso" value="0"/>
		<input type="hidden" id="cEPIngresoPago" name="cEPIngresoPago" value=""/>
		<input type="hidden" id="nMesIngresoPago" name="nMesIngresoPago" value="0"/>
		<input type="hidden" id="mImporteIngresoPago" name="mImporteIngresoPago" value="0"/>
		<input type="hidden" id="contrato" name="contrato" value=""/>
		
				
		<div id="container" class="container">
			<h1>Ligar Compromiso con Solicitud de Recursos Por Pagar.</h1>
			<fieldset>
				<legend> Comprmiso </legend>
				<table>
					<tr>
						<td>Seleccione el contrato:</td>
						<td><input type="text" class="AyudaSyC obligatorio desahabilitado" maxlength="40" size="50" name="cIdContrato" id="cIdContratoCompromiso" readonly></td>
					</tr>
				</table>
			</fieldset>
			<fieldset>
				<legend>Compromisos Radicados</legend>
				<span>Seleccione un renglon en la tabla siguiente para ligar el compromiso con la solicitud de recursos por pagar.</span>
				<div>
					<table class="display" cellspacing="0" cellpadding="0" align="center" id="dtCompromisos">
						<thead>
							<tr>
								<th>Folio</th>
								<th>Contrato</th>
								<th>Fecha</th>
								<th>Monto Total</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
			</fieldset>
			<fieldset>
				<legend>Detalle de Compromiso</legend>
				<span>Doble renglon en la tabla siguiente para ligar el compromiso con la solicitud de recursos por pagar.</span>
				<div>
					<table class="display" cellspacing="0" cellpadding="0" align="center" id="dtCompromisoDet">
						<thead>
							<tr>
								<th>Folio</th>
								<th>EP</th>
								<th>Mes</th>
								<th>Monto Total</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
			</fieldset>
			<fieldset>
				<legend>Liga Compromiso Solicitud</legend>
				<span>En la siguiente tabla se muestran las relaciones entre compromiso y solicitud de recursos por pagar.</span>
				<div>
					<table class="display" cellspacing="0" cellpadding="0" align="center" id="dtCompromisoSolicitud">
						<thead>
							<tr>
								<th>Compromiso</th>
								<th>SRxP</th>
								<th>EP</th>
								<th>Mes</th>
								<th>Monto</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
			</fieldset>
			<br/>
			<center>
				<input type="button" id="pbAceptar" style="visibility: hidden" value ="Aceptar"/>&nbsp;&nbsp;&nbsp;
				<input type="button" id="pbCancelar" style="visibility: hidden" value ="Cancelar"/> &nbsp;&nbsp;&nbsp;
				<input type="button" id="pbBorrar" style="visibility: hidden" value="Borrar Tabla"/>	
			</center>
		</div>
		<div id="dlgLigaCompromiso">
			<fieldset>
				<legend>Captura de Informacion</legend>
				<table>
					<tbody>
						<tr>
							<td align="right">EP:</td>
							<td align="left"><input type="text" value="" id="EP" name="EP" size="70" class="lectura" readonly="readonly"></td>
						<tr>
						<tr>
							<td align="right">Mes:</td>
							<td align="left"><input type="text" value="" id="mes" name="mes" size="12" class="lectura" readonly="readonly"></td>
						<tr>
						<tr>
							<td align="right">Monto:</td>
							<td align="left"><input type="text" value="" id="monto" name="monto" size="30" class="lectura" readonly="readonly"></td>
						<tr>
						<tr>
							<td align="right">Solicitud:</td>
							<td align="left"><select id= "nFolioRadicado" name="nFolioRadicado" onChange="modificaMonto();"></select></td>
						<tr>
						<tr>
							<td align="right">Monto:</td>
							<td align="left"><input type="text" value="" id="dispRad" name="dispRad" size="30"> </td> 
						<tr>
						<tr>
							<td align="right">Disponible:</td>
							<td align="left"><input type="text" value="" id="disponible" name="disponible" size="30" class="lectura" readonly="readonly"> </td> 
						<tr>
					</tbody>
				</table>
			</fieldset>
		</div>
	</form>
</body>

</html>