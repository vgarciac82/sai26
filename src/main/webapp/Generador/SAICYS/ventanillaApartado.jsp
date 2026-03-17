<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	
	boolean esResumenFIEL = StringUtils.isNotEmpty( request.getParameter("a") );
	
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	Calendar c2= Calendar.getInstance();
	String today= sdf.format(c1.getTime());
	String vigencia=sdf.format(c2.getTime());
	String cCentroContable="";
	String cUR = "";
	String cRamo = "";
	

	if (usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
	    cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	if (c == null) {
		//response.sendRedirect("../index.jsp");
		//return;
	}
	//int oper = ((CasoOperacion)c.getCasoOperacion().get(0)).getIdOperacion();
	
	String folioCasoApartado = c .getFolio();
	int nFolioApartado = Integer.parseInt(folioCasoApartado.substring(folioCasoApartado.lastIndexOf('-')+1));
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Ventanilla Apartado</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
	</style>
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.editable-1.3.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../js/FixedColumns.js"></script>
	<script type="text/javascript" src="../../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" charset="utf-8">
	
	var $tabs;
	var oDTDetalle, oDTVigencia;
	$(document).ready(function() {
		
		$tabs = $("#tabs").tabs();
		
		init(false);
		
		//Handlers de botones aprobar y rechazar
		$("#imgAprobarApartado").click( imgAprobarClickHandler );
		$("#imgRechazarApartado").click( imgRechazarClickHandler );
		
		//
		$("#imgExtenderApartado").click( imgExtenderClickHandler );
		$("#imgRechazarExtender").click( imgRechazarExtenderClickHandler );
		
		$( "#dialog" ).dialog({
			autoOpen: false,
			minWidth: 500,
			draggable: false,
			modal: true,
			buttons: { 
				"Cancelar": function() {
					$( this ).dialog( "close" );
				},
				"Aceptar": function() {
					queryFormPost("mSolicitudNotas", {async: false });
					//validar que la solicitud no se encuentre en un consolidado
					queryFormPost("mTieneConsoliado",{async:false });
					if($("#tieneConsolidado").val()==1){
						$("#nIdEstadoSolicitud").val(5);
					}else{
						//Pone el estado de la Requisicion en SOLICITADA
						$("#nIdEstadoSolicitud").val(1);
					}
					
					queryFormPost("mSolicitudApartadoUpdate", {async: false });
					//Retrocede el caso
					$.ajax({
						url: '../../servlet/SolicitudServlet', 
						type:'get', 
						async: false,
						data:'operacion=4', 
						dataType: 'json', 
						success: function(){
							init(true);
						}
					});
					//Actualiza el estado del precomprometido
					$("#nIdEstadoPrecomprometido").val(4);
					queryFormPost("mSolicitudPrecomprometidoUpdate", {async: false });
					//Bitácora
					$("#cAccion").val("RECHAZA_APARTADO");
					$("#cIdDocumento").val($("#cIdSolicitud").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
					$( this ).dialog( "close" );
					alert("Apartado rechazado");
				}
			}
		});
	});
	
	function init(isUpdating) {
		//Encabezado y algunos hiddens
		//lblRequisicion, lblFolio, lblDescripcion, cEjercicio,	cIdUnidadEjecutora,	cIdSolicitud
		queryFormPost("mApartadoVentanillaLabels", {async:false});
		queryFormPost("mApartadoTotalRead", {async:false});
		$("#lblTotal").formatCurrency();
		
		//Si acaba de rechazar, no puede aprobar ni rechazar
		if ($("#nIdEstadoSolicitud").val() == 1){
			document.getElementById("imgAprobarApartado").disabled = true;
			document.getElementById("imgRechazarApartado").disabled = true;
		}
		
		//Si no se ha autorizado, desactiva pestaña de vigencias
		if ($("#nIdEstadoSolicitud").val() == 2){
			$tabs.tabs("select", 0);
			$( "#aTab1" ).attr("disabled", true);
		}
		
		//Si ya autorizó se va a pestaña de vigencias y desactiva botones aut y rechazar
		if ($("#nIdEstadoSolicitud").val() == 3){
			queryFormPost("mSolicitudApartadoVigencia", { async:false });//carga la vigencia actual
			
			$( "#aTab1" ).attr("disabled", false);
			$tabs.tabs("select", 1);
			
			document.getElementById("imgAprobarApartado").disabled = true;
			document.getElementById("imgRechazarApartado").disabled = true;
		}
		
		//La operacion actual del caso
		queryFormPost("mSolicitudApartadoOperacion", {async:false});
		//Si la operacion no es solicitud de extension de vigencia, desactiva boton
		if($("#nCasoOperacion").val() != 4){
			document.getElementById("imgExtenderApartado").disabled = true;
			document.getElementById("imgRechazarExtender").disabled = true;
		}

		oDTDetalle = initDTDetalle();
		oDTVigencia = initDTVigencia();
		
		

	}
	
	function initDTDetalle(){
		
		var params = "'" + $("#cEjercicio").val() + "', '" +
			$("#cIdUnidadEjecutora").val() + "', '" + 
			$("#cIdSolicitud").val() + "'";

		return $("#tblDetalle").dataTable({
			"bDestroy": true,
			"bAutoWidth" : false,
			"bPaginate" : false,
			"sScrollX": "100%",
			"sScrollY": "150",
			//"bScrollCollapse": true,
			//"sScrollXInner": "100%",
			"oLanguage": {
				"sProcessing": "Procesando...",
				"sLengthMenu": "Mostrar _MENU_ registros",
				"sZeroRecords": "No hay registros a mostrar",
				"sEmptyTable": "No hay datos en la tabla",
				"sLoadingRecords": "Cargando...",
				"sInfo": "Registros _START_ al _END_ de _TOTAL_",
				"sInfoEmpty": "Registro 0 al 0 de 0",
				"sInfoFiltered": "(filtado de _MAX_ registros)",
				"sInfoPostFix": "",
				"sInfoThousands": ",",
				"sSearch": "Buscar:",
				"oPaginate": {
					"sFirst":    "Primero",
					"sPrevious": "Ant.",
					"sNext":     "Sigte.",
					"sLast":     "&Uacute;ltimo" }
			},
			"bServerSide": true,
			"sAjaxSource": window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_pivotApartado(" + params + ")",
			"bProcessing": true,
			"sPaginationType": "full_numbers",
			"bJQueryUI": true,
			"aoColumns": [
				{ "sName": "EP",			"bSortable": false, "sWidth":"70%"},
				{ "sName": "cMes",			"bSortable": false, "sWidth":"12%" },
				{ "sName": "mImporte",		"bSortable": false, "sWidth":"28%" }]
       	});
 	}
	
	function initDTVigencia(){
		
		var url = " cFolioApartado = '" + $("#folioCasoApartado").val() + "'";
		
		return $("#tblVigencia").dataTable({
			"bDestroy": true,
			"bAutoWidth" : true,
			"bPaginate" : false,
			"sScrollX": "100%",
			"sScrollY": "150",
			//"bScrollCollapse": true,
			//"sScrollXInner": "100%",
			"oLanguage": {
				"sProcessing": "Procesando...",
				"sLengthMenu": "Mostrar _MENU_ registros",
				"sZeroRecords": "No hay registros a mostrar",
				"sEmptyTable": "No hay datos en la tabla",
				"sLoadingRecords": "Cargando...",
				"sInfo": "Registros _START_ al _END_ de _TOTAL_",
				"sInfoEmpty": "Registro 0 al 0 de 0",
				"sInfoFiltered": "(filtado de _MAX_ registros)",
				"sInfoPostFix": "",
				"sInfoThousands": ",",
				"sSearch": "Buscar:",
				"oPaginate": {
					"sFirst":    "Primero",
					"sPrevious": "Ant.",
					"sNext":     "Sigte.",
					"sLast":     "&Uacute;ltimo" }
			},
			"bServerSide": true,
			"sAjaxSource": window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mApartadoVigencia&qw="+url,
			"bProcessing": true,
			"sPaginationType": "full_numbers",
			"bJQueryUI": true,
			"aoColumns": [
				{ "sName": "tipo", "bSortable": false, "sWidth":"50%"},
				{ "sName": "vence", "bSortable": false, "sWidth":"50%"}]
       	});
 	}
	
	
	function imgAprobarClickHandler() {
		//deshabilitamos boton para que solo una vez se active
		document.getElementById("imgAprobarApartado").disabled = true;
		//Mes actual
		var nMes = "<%=today%>";
		nMes = nMes.substring(5, 7) ;
		$("#nMes").val( nMes );
		
		//Obtiene folio raro de financiero (caNoPreCompromiso), para luego usarlo en el encabezado
		getNextSequenceVal({
			seqName: "CO-" + "<%=cCentroContable%>", 
			async: false, 
			callback: function(){
				seqValue = 100000 + parseInt(seqValue,10);
			//seqValue = seqValue.substr(seqValue.length - 6);
				vcaNoCompromiso = 
					"<%=cCentroContable%>" + "CO" + $("#cEjercicio").val() + seqValue;
				
				$("#caNoPreCompromiso").val( vcaNoCompromiso );
			}
		});
		
		//crud para crear el encabezado del caso 
		queryFormPost('tApartadoECreate', {async: false });
		//crud para crear el detalle del caso (sp_insertApartadoDetalle)
		queryFormPost('tApartadoDCreate', {async: false });
		
		aplicacionContableApartado();
					
	   
	}

	function aplicacionContableApartado(){
		$.ajax({
			url: '../../servlet/SolicitudServlet', 
			type:'get',
			async: false,
			data:"operacion=1&nFolioApartado="+$("#nFolioApartado").val()+"&cEjercicio="+$("#cEjercicio").val()+"&folioCasoApartado="+$("#folioCasoApartado").val()+"&cIdSolicitud="+$("#cIdSolicitud").val(), 
			dataType: 'json', 
			success: function(j){
			var mensaje=j[0].Contable1;
				if (j[0].Success == "true"){
				
				    /*
					//Actualiza el estado del precomprometido
					$("#nIdEstadoPrecomprometido").val(3);
					$("#nIdEstadoSolicitud").val(3);
					queryFormPost("mSolicitudPrecomprometidoUpdate", {async: false });
					queryFormPost("mSolicitudEstadoUpdate", {async: false });
					*/
					
					//queryFormPost("sp_mEstadoVigenciaApartado", {async: false });
					
					
					//Bitácora
					$("#cAccion").val("AUTORIZA_APARTADO");
					$("#cIdDocumento").val($("#cIdSolicitud").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
					init(true);
					alert(mensaje);
					
				}				
				else{
				      
				    if (j[0].Error == "true"){
				    queryFormPost('deleteApartadoDetalle', {async: false });
					queryFormPost('deleteApartadoEncabezado', {async: false });
				    }
				
					//$("#nIdEstadoSolicitud").val(2);
					//pone nulos en consecutivo apartado y folio apartado
				   //	queryFormPost("mSolicitudApartadoUpdateonError1", {async: false });
					//queryFormPost("mSolicitudEstadoUpdate", {async: false });
					
					document.getElementById("imgAprobarApartado").disabled = false;
					alert(mensaje);
				}
			}
		});
	}
	
	function imgRechazarClickHandler() {
		$( "#dialog" ).dialog("open");
	}
	
	
	function imgExtenderClickHandler() {
		document.getElementById("imgExtenderApartado").disabled = true;
		document.getElementById("imgRechazarExtender").disabled = true;
		
		var aVence = $("#venceApartado").val().split("-");
		var vence = new Date(aVence[0], aVence[1] -1, aVence[2]);
		
		var today = new Date();
		today.setHours(0,0,0,0);
		
		if(vence.getTime() < today.getTime()){
			alert("No se puede aprobar la vigencia porque el apartado ya venció");
			document.getElementById("imgExtenderApartado").disabled = false;
			document.getElementById("imgRechazarExtender").disabled = false;
			return;
		}
		
		$.ajax({
			url: '../../servlet/SolicitudServlet', 
			type:'get', 
			async: false,
			data:'operacion=3', 
			dataType: 'json', 
			success: function(json){
				if (json[0].Success == "true"){
					queryFormPost("sp_mEstadoVigenciaApartado", {async: false });
				//Bitácora
				$("#cAccion").val("EXTIENDE_VIGENCIA");
				$("#cIdDocumento").val($("#cIdSolicitud").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				alert("Vigencia extendida");
					init(true);
				}else{
					document.getElementById("imgExtenderApartado").disabled = false;
					document.getElementById("imgRechazarExtender").disabled = false;
				}
			}
		});
	}
	
	function imgRechazarExtenderClickHandler() {
		document.getElementById("imgRechazarExtender").disabled = true;
		document.getElementById("imgExtenderApartado").disabled = true;
		$.ajax({
			url: '../../servlet/SolicitudServlet', 
			type:'get', 
			async: false,
			data:'operacion=3', 
			dataType: 'json', 
			success: function(json){
				if (json[0].Success == "true"){
					//queryFormPost("sp_mEstadoVigenciaApartado", {async: false });
				//Bitácora
				$("#cAccion").val("RECHAZA_EXTENCION_VIGENCIA");
				$("#cIdDocumento").val($("#cIdSolicitud").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				alert("Extensión de vigencia rechazada");
					init(true);
				}else{
					document.getElementById("imgExtenderApartado").disabled = false;
					document.getElementById("imgRechazarExtender").disabled = false;
				}
			}
		});
	}
	
	// INICIO funciones heredadas de Contrato diverso preCompAvanzado
  	function onLoadPlantilla(){
		parent.document.getElementById("pb_send").	style.visibility='hidden';
		parent.document.getElementById("pb_save").	style.visibility='hidden';
		parent.document.getElementById("pb_save").	style.visibility='hidden';
		parent.document.getElementById("pb_cancel").style.visibility='hidden';
		parent.document.getElementById("pb_leave").	disabled=false;
  	}

  	function onSubmit(id_oper){ //validaciones del boton guardar
  	}

	function ResponsableSiguiente(id_oper) {
	 	//return "CONSULTA_PAGOS";
  	}
	
  	function OperacionSiguiente(id_oper){
 		//return "consulta_compromiso";
  	}
  	
	function onPostDisplay(){
	
	}
	
	// FIN funciones heredadas de Contrato diverso
	function checkShortcut() {				
		//Deshabilita el ESC y BACKSPACE
		if(event.keyCode==27){
			return false;
		}
		if((event.srcElement.tagName.toUpperCase() != 'INPUT' &&
				event.srcElement.tagName.toUpperCase() != 'TEXTAREA')
			&& (event.keyCode==8 || event.keyCode==13)){					
			return false;
		}
	}	
  	
</script>
</head>
<body  id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" onkeydown="return checkShortcut()">
<form>
	<div id="container" class="container">
			<h1>Autorización Apartado</h1>
			<div id="tabs">
				<ul>
					<li><a id="aTab0" href="#tabs-0" >Autorización</a></li>
					<li><a id="aTab1" href="#tabs-1" >Vigencia</a></li>
				</ul>
				<div id="tabs-0" align="center">
					<table  align="left" width="100%">
						<tr>
							<td align="right" colspan="2">
								<img id="imgAprobarApartado" src="../imagenes/accept_green.png" style="cursor: pointer" />&nbsp;Autorizar
								<img id="imgRechazarApartado" src="../imagenes/cancel_round.png" style="cursor: pointer"/>&nbsp;Rechazar
							</td>
						</tr>
						<tr>
							<td>
								<fieldset style="width: 99%" align="left">
									<legend>
										Información de la Requisición
									</legend>
									<table  align="left" width="100%">
										<tr>
											<td width="20%" align="left">Folio Requisición: </td>
											<td align="left"><input type="text" style="width: 100%" id="lblRequisicion" name="lblRequisicion" readonly="readonly" style="border-width:0; background-color:transparent" /></td>
										</tr>
										<tr>
											<td width="20%" align="left">Folio Apartado: </td>
											<td align="left"><input type="text" style="width: 100%" name="lblFolio" id="lblFolio" readonly="readonly" style="border-width:0; background-color:transparent" /></td>
										</tr>
										<tr>
											<td width="20%" align="left">Total Apartado: </td>
											<td align="left"><input type="text" style="width: 100%" name="lblTotal" id="lblTotal" readonly="readonly" style="border-width:0; background-color:transparent" /></td>
										</tr>
										<tr>
											<td width="20%" align="left">Estado: </td>
											<td align="left"><input type="text" style="width: 100%" name="lblEstado" id="lblEstado" readonly="readonly" style="border-width:0; background-color:transparent" /></td>
										</tr>
										<tr>
											<td width="20%" align="left">Descripción: </td>
											<td align="left"><textarea id="lblDescripcion"  name="lblDescripcion" rows="5" readonly="readonly" cols="70"></textarea></td>
										</tr>
										
									</table>
								</fieldset>
							</td>
						</tr>
						<tr>
							<td>
								<fieldset style="width: 99%" align="left">
									<legend>
										Lineas de Apartado
									</legend>
									<br />
									<table id="tblDetalle" class="display" width="90%" align="left">
							            <thead>
							                <tr>
							                    <th width="70%">EP</th>
							                    <th width="6%">mes</th>
							                    <th width="28%">Monto</th>
							                </tr>
							            </thead>
							        </table>
							        <br />
								</fieldset>
							</td>
						</tr>
					</table>
				</div>
				<div id="tabs-1" align="center">
					<table  align="left" width="100%">
						<tr>
							<td align="right" colspan="2">
								<img id="imgExtenderApartado" src="../imagenes/accept_green.png" style="cursor: pointer" />&nbsp;Autorizar
								<img id="imgRechazarExtender" src="../imagenes/cancel_round.png" style="cursor: pointer" />&nbsp;Rechazar
							</td>
						</tr>
						<tr>
							<td>
								<fieldset style="width: 97%" align="left">
									<legend>
										Solicitud de Extensión de Vigencia
									</legend>
									<br />
									<table id="tblVigencia" class="display" width="" align="left">
							            <thead>
							                <tr>
							                    <th>Autorización</th>
							                     <th>Vigencia</th>
							                </tr>
							            </thead>
							        </table></div>
							        <br />
								</fieldset>
							</td>
						</tr>
					</table>
				</div>
			</div>
			<!-- Variables de Folio Apartado -->
			<input type="hidden" id="nFolioApartado" name="nFolioApartado" value="<%=nFolioApartado%>" />
			<input type="hidden" name="folioCasoApartado" id="folioCasoApartado" value="<%=folioCasoApartado%>" />
			
			<!--  Para saber en que operacion del caso está: aprobar / vigencia -->
			<input type="hidden" id="nCasoOperacion" name="nCasoOperacion" />
			
			<!-- Variables para traer el presupuesto -->
			<input type="hidden" id="cEjercicio" name="cEjercicio" />
			<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora">
			<input type="hidden" name="cIdSolicitud" id="cIdSolicitud" />
			<input type="hidden" name="nIdEstadoSolicitud" id="nIdEstadoSolicitud" />
			<input type="hidden" name="nIdEstadoPrecomprometido" id="nIdEstadoPrecomprometido" />
			
			<!-- Variables de Encabezado Apartado --><!-- java -->
			<input type="hidden" id="fCarga" name="fCarga" value="<%=today%>"><!-- java -->
			<input type="hidden" id="fAplicacion" name="fAplicacion" value="<%=today%>"><!-- java -->
			<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>"><!-- java -->
			<input type="hidden" id="cRamo" name="cRamo" value="<%=cRamo%>"><!-- java -->
			<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>"><!-- java -->
			<input type="hidden" id="caNoPreCompromiso" name="caNoPreCompromiso" value="">
			<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="PR"><!-- fijo -->
			<input type="hidden" id="nMes" name="nMes" value="">
			<input type="hidden" id="nStatusFinanciero" name="nStatusFinanciero" value="0"><!-- fijo -->
			<input type="hidden" id="fVigencia" name="fVigencia" value="<%=vigencia%>"><!-- java -->
			<input type="hidden" id="nEnviadoSICOP" name="nEnviadoSICOP" value="0"><!-- fijo -->
			<input type="hidden" name="venceApartado" id="venceApartado" />
			<input type="hidden" name="cAccion" id="cAccion" />
			<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuario.getLogin()%>"/>
			<input type="hidden" name="cIdDocumento" id="cIdDocumento" />
			
			<input type="hidden" name="estado" id="estado" />
			<input type="hidden" name="tieneConsolidado" id="tieneConsolidado" />
						
			<!-- Motivo de cancelacion 
			<input type="hidden" id="mNotas" name="mNotas" >-->
		</div>
		<div id="dialog" title="Motivo de rechazo" >
		    <br />Por favor especifique el motivo del rechazo <br />
		    <form>
		    <textarea id="mNotas"  name="mNotas" rows="5" cols="60"></textarea></form>
		</div>
</form>
</body>
</html>
