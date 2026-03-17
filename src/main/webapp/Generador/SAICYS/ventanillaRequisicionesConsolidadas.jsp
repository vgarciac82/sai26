<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	Calendar c2= Calendar.getInstance();
	String today= sdf.format(c1.getTime());
	String vigencia=sdf.format(c2.getTime());
	String cCentroContable="";
	String cUR = "";
	String cRamo = "";
	
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
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
	var  oDTVigencia;
	$(document).ready(function() {
		
		$tabs = $("#tabs").tabs();
		
		init(false);

		$("#imgExtenderApartado").click( imgExtenderClickHandler );
		$("#imgRechazarExtender").click( imgRechazarExtenderClickHandler );
		
	});
	
	function init(isUpdating) {
		queryFormPost("mAmpliacionConsolidado", {async:false});
		$("#cIdConsolidado").val($("#lblConsolidado").val());

		oDTDetalle = initDTDetalle();
	}
	
	 function initDTDetalle(){
		
		var params = "'" + $("#cEjercicio").val() + "', '" +
			$("#cIdUnidadEjecutora").val() + "', '" + 
			$("#cIdSolicitud").val() + "'";
			campos = $("#cIdConsolidado").val();
		return 	$('#tblResumenpartidas').dataTable({         
			 				 sScrollX: "100%",
							 sScrollXInner: "97%",
							 bScrollCollapse: true,
							 bDestroy: true,
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
							bAutoWidth: false,
							bProcessing: true,
							sPaginationType: "full_numbers",
							bJQueryUI: true,
							bServerSide: true,
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mRequisicionesConsolidadasVentanilla('"+campos+"')",
							aaSorting: [[ 0, "asc" ]] ,
							aoColumns: [						 
							{ sName: "cIdSolicitud" },
							{ sName: "cIdSubPartida"},
							{ sName: "cDescripcion"},
							{ sName: "C_FOLIO_APA", bVisible:false}
							]      		
					});
 	}
	
	function imgExtenderClickHandler() {
		document.getElementById("imgExtenderApartado").disabled = true;
		document.getElementById("imgRechazarExtender").disabled = true;
		var aprobar=true;
		var aTrs = $('#tblResumenpartidas').dataTable().fnGetNodes();								
	    for ( var i=aTrs.length-1 ; i>=0; i-- ){ 
		    var nTr = $('#tblResumenpartidas').dataTable().fnGetData(aTrs[i]);										
			var idSolicitud = nTr[0];
			queryFormPost("mSolicitudApartadoVigencia", {async : false});
			var aVence = $("#venceApartado").val().split("-");
			var vence = new Date(aVence[0], aVence[1] -1, aVence[2]);
			
			var today = new Date();
			today.setHours(0,0,0,0);
			
			if(vence.getTime() < today.getTime()){
				aprobar=false;
				break;
			}
		}
		
		if(aprobar){
			$.ajax({
				url: '../../servlet/ConsolidadoServlet', 
				type:'get', 
				async: false,
				data:'operacion=4', 
				dataType: 'json', 
				success: function(json){
					if (json[0].Success == "true"){
						var aTrs = $('#tblResumenpartidas').dataTable().fnGetNodes();								
					    for ( var i=aTrs.length-1 ; i>=0; i-- ){ 
						    var nTr = $('#tblResumenpartidas').dataTable().fnGetData(aTrs[i]);										
							var idSolicitud = nTr[0];
							$("#cIdSolicitud").val(idSolicitud);
							var cadena_campos=idSolicitud.split('-');
							$("#cIdUnidadEjecutora").val(cadena_campos[1]); 
							$("#folioCasoApartado").val(nTr[3]);
							queryFormPost("sp_mEstadoVigenciaApartado", {async: false });
						}
						
					//Bitácora
					$("#cAccion").val("EXTIENDE_VIGENCIA_REQUISICIONES");
					$("#cIdDocumento").val($("#cIdSolicitud").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
					$("#motivo").val("");
					queryFormPost("motivoRechazoAmpliacion", { async : false});
					alert("Vigencia extendida");
						init(true);
					}else{
						document.getElementById("imgExtenderApartado").disabled = false;
						document.getElementById("imgRechazarExtender").disabled = false;
					}
				}
			});
		}else{
			alert("Existen requisiciones con apartados vencidos");
		}		
	}
	
	function imgRechazarExtenderClickHandler() {
		document.getElementById("divMotivo").style.display="block";		
		//$("#divMotivo").css("display", "block");
	
	}
	
	function rechazaVigencia(){
		if($("#motivo").val()=="" || $("#motivo").val()=="0"){
			alert("Es necesario un motivo de rechazo");
			return;
		}
		
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
					//guarda el motivo de rechazo
					queryFormPost("motivoRechazoAmpliacion", { async : false});
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
	
	function cerrarDiv(){
		$("#divMotivo").css("display", "none");
	}
  	
</script>
</head>
<body  id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" onkeydown="return checkShortcut()">
<form>
	<div id="container" class="container">
			<h1>Autorización Apartado</h1>
			<div id="tabs">
				<ul>
					<li><a id="aTab0" href="#tabs-0" >Vigencia</a></li>
				</ul>
				<div id="tabs-0" align="center">
					<table  align="left" width="100%">
						<tr>
							<td align="right" colspan="2">
								<img id="imgExtenderApartado" src="../imagenes/accept_green.png" style="cursor: pointer" />&nbsp;Autorizar
								<img id="imgRechazarExtender" src="../imagenes/cancel_round.png" style="cursor: pointer" />&nbsp;Rechazar
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
											<td width="20%" align="left">Folio Consolidado: </td>
											<td align="left"><input type="text" style="width: 100%" id="lblConsolidado" name="lblConsolidado" readonly="readonly" style="border-width:0; background-color:transparent" /></td>
										</tr>
										<tr>
											<td width="20%" align="left">Folio Apartado: </td>
											<td align="left"><input type="text" style="width: 100%" name="lblFolio" id="lblFolio" readonly="readonly" style="border-width:0; background-color:transparent" /></td>
										</tr>
										<tr>
											<td>
												<div id="divMotivo" style="display: none">
													<table width="50%" align="right">
														<tr>
															<td align="center" valign="top">Motivo de Rechazo:</td>
														</tr>
														<tr>
															<td align="center" valign="top"><textarea id="motivo" name="motivo" cols="30" rows="5"></textarea></td>
														</tr>
														<tr>
															<td width="40%" align="center" valign="top">
															<input type="button" value="Enviar" onclick="rechazaVigencia();">&nbsp;&nbsp;
															<input type="button" value="Cancelar" onclick="cerrarDiv();"></td>
														</tr>
													</table>
												</div>
											</td>
										</tr>
									</table>
									<table id="tblResumenpartidas" class="display">
							            <thead>
							                <tr>
							                	<th width="5%">Requisición</th>
							                	<th width="10%">Partida</th>
							                	<th>Descripcion</th>
							                	<th></th>
											</tr>
							            </thead>
							        </table>
									   
								</fieldset>
							</td>
						</tr>
					</table>
				</div>
			</div>
			
			<!-- Variables de Folio Apartado -->
			<input type="hidden" id="nFolioAmpliacion" name="nFolioAmpliacion" value="<%=nFolioApartado%>" />
			<input type="hidden" name="folioCasoAmpliacion" id="folioCasoAmpliacion" value="<%=folioCasoApartado%>" />
			
			<!--  Para saber en que operacion del caso está: aprobar / vigencia -->
			<input type="hidden" id="nCasoOperacion" name="nCasoOperacion" />
			
			<!-- Variables para traer el presupuesto -->
			<input type="hidden" id="cEjercicio" name="cEjercicio" />
			<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora">
			<input type="hidden" name="cIdConsolidado" id="cIdConsolidado" />
			<input type="hidden" name="cIdSolicitud" id="cIdSolicitud" />
			<input type="hidden" name="venceApartado" id="venceApartado" />
			<input type="hidden" name="folioCasoApartado" id="folioCasoApartado" />
			
			
			
			
			
			<!-- <input type="hidden" name="nIdEstadoSolicitud" id="nIdEstadoSolicitud" />
			<input type="hidden" name="nIdEstadoPrecomprometido" id="nIdEstadoPrecomprometido" />
			 -->
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
			
			<input type="hidden" name="cAccion" id="cAccion" />
			<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuario.getLogin()%>"/>
			<input type="hidden" name="cIdDocumento" id="cIdDocumento" />
			
			<input type="hidden" name="estado" id="estado" />
						
			
		</div>
		</div>
	</form>
</body>
</html>
