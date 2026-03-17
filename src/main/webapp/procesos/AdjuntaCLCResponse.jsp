<%@page import="com.syc.gestion.util.Util"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	if (session == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	Usuario u = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (u == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	if (u.getPropiedad("CCENTROCONTABLE") == null
			|| StringUtils.isEmpty(u.getPropiedad("CCENTROCONTABLE")
					.getValor())) {
		response.sendRedirect("../index.jsp");
		return;
	}

	String urUsuario = u.getU_UR();
	String nombreUsuario = u.getLogin();
	String centroContableUsuario = u.getPropiedad("CCENTROCONTABLE")
			.getValor();
	String hoy = Util.getTodayESMX();
	String operador = u.getNombre();
	String msg = (String) session.getAttribute("MSG");
	boolean muestraMensaje = false;
	if (!StringUtils.isEmpty(msg)) {
		muestraMensaje = true;
		session.removeAttribute("MSG");
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Resultado de Proceso</title>

	<!-- Estilos estandar para los controles JQuery -->
	<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
	<script src="../Generador/js/bootstrap.bundle.min.js"></script>
	<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
	<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
		
	<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../js/jsquery.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>
	
<script type="text/javascript">
		var nombreUsuario = '<%=nombreUsuario%>';
		var UR = '<%=urUsuario%>';	
		var CC = '<%=centroContableUsuario%>';
		var fAplicacion = '<%=hoy%>';
		var operador = '<%=operador%>';
		var muestraMensaje = <%=muestraMensaje%>;
		var oTable;
		var doTable;
		var cIDProceso;
		
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

	$(document).ready(function() {

		if (muestraMensaje)
			$("#msgDiv").css("display", "block");

		creaDTProcesos();
		creaDTArchivos();
		$("#reloadBtn").button().click(function(){recarga();});
	});

	function recarga(){
		location.reload();
	}
	function creaDTProcesos() {

		oTable = $('#dtProcesos').dataTable(
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
					"fnInitComplete": function() {
					    oTable.fnAdjustColumnSizing();
					},
					sAjaxSource : window.location.protocol + "//"
							+ window.location.host + "/"
							+ window.location.pathname.split("/")[1]
							+ "/crud?rt=t&ql=vProcesosAdjuntaCLC&qw=UE='" + UR
							+ "'",
					aoColumns : [ {
						sName : "Proceso"
					}, {
						sName : "Nombre"
					}, {
						sName : "Unidad"
					}, {
						sName : "Fecha"
					}, {
						sName : "Estatus"
					}, {
						sName : "Log"
					} ],
					oLanguage : es_mx
				});

		$("#dtProcesos tbody").click(function(event) {

			$(oTable.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});

			$(event.target.parentNode).addClass('row_selected');
			borrafiltros();
			var aPos = oTable.fnGetPosition(event.target.parentNode);
			var aData = oTable.fnGetData(aPos);
			cIDProceso = aData[0];
			$("#cIDProceso").val(cIDProceso);
			var condicion = "cIDProceso='" + cIDProceso + "'";
			creaDTArchivos(condicion);
		});

	}

	function creaDTArchivos(cond) {

		if (!cond)
			cond = "1<>1";

		doTable = $('#dtArchivosProceso').dataTable(
				{
					"bPaginate" : false,
					"bLengthChange" : true,
					"bFilter" : true,
					"bSort" : true,
					"bInfo" : true,
					"bAutoWidth" : true,					
					"bJQueryUI" : true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType" : "full_numbers",
					"sScrollY" : "400px",
					"bScrollCollapse" : true,
					"bServerSide" : true,
					"fnInitComplete": function() {
					    doTable.fnAdjustColumnSizing();
					},
					sAjaxSource : window.location.protocol + "//"
							+ window.location.host + "/"
							+ window.location.pathname.split("/")[1]
							+ "/crud?rt=t&ql=v_CLC_Adjunta&qw=" + cond,
					aoColumns : [ {
						sName : "cIDProceso"
					}, {
						sName : "nombreArchivo"
					}, {
						sName : "cTipoPago"
					}, {
						sName : "nFolioPago"
					}, {
						sName : "caNoContraRecibo"
					}, {
						sName : "cLogOperacion"
					}, {
						sName : "fecha"
					} ],
					oLanguage : es_mx
				});

	}
	
	function filtraArchivos(){
		var condicion = "cIDProceso='" + $("#cIDProceso").val() + "'";
		var filtrados = "";
		var tokenOR = "";
		
		$("[name='filtros']").each(
			function(){
				var seleccionado = $(this).attr("checked");
				if( seleccionado){
					filtrados = filtrados + tokenOR + ' cEstatusAdjunto IN ( ' + $(this).val() + ')';
					tokenOR = " OR ";
				}
			}
		);
		
		if(filtrados != ""){
			condicion = condicion + " AND (" + filtrados + ")";
		}
		creaDTArchivos(condicion);
	}
	
	function borrafiltros(){
		$("[name='filtros']").each(
			function(){
				var seleccionado = $(this).attr("checked");
				if( seleccionado){
					$(this).attr("checked",false);
				}	
			}
		);
	}
</script>

</head>
<br/>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<form>
		<input type="hidden" id="cIDProceso" value="">
		<div id="container" class="container" style="width: 80%">
			<div class="card-header"> <h3> Consulta de Proceso Adjuntar Archivos </h3> </div>
			<hr class="mt-3"/>
									
			<div id="msgDiv" style="display: none">
				<fieldset>
					<legend>Mensajes</legend>
					<table>
						<tr>
							<td align="left">Respuesta del proceso:</td>
						</tr>
						<tr>
							<td><textarea rows="10" cols="80" readonly="readonly"
									class="lectura"><%=msg%></textarea></td>
						</tr>
					</table>
				</fieldset>
			</div>
			
			<div class="container" style="width: 80%">
				<h6> Procesos de la Unidad </h6> 
				<hr class="mt-3"/>
							
				<div class="row d-flex">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<span class="label">En la siguiente tabla se muestran las diversas cargas que se han realizado desde esta unidad. Las
											cargas tienen diferentes estados:</span>						
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<ul>
							<li><b>Terminado</b> Es una carga que ha finalizado, puede
												ser que no todos los archivos esten adjuntos exitosamente
							</li>
							<li><b>En Ejecucion</b> Es una carga que se esta procesando
													aun. Actualice la pantalla para ver el estatus actual.
													Dependiendo de la cantidad de archivos, puede tomar mas tiempo
													el terminar de procesarlos.
							</li>
							<li><b>Error</b> Ocurrio un error mientras se cargaba el
											 archivo. En la columna <i>log</i> se muestra el motivo del
											 error
							</li>
						</ul>
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<span class="label"><b>Seleccione un renglon para ver el
									contenido cargado en el proceso y su estatus.</b></span>						
					</div>
				</div>
				
				<div class="row d-flex justify-content-right">					
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						<input type="button" id="reloadBtn" name="reloadBtn" value="Actualizar" class="btn btn-secondary btn-sm"/>
					</div>
					<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
					</div>
				</div>
				
				<div id="pagos" class="table-responsive">
					<table id="dtProcesos" class="table table-striped">							
						<thead>
							<tr>
								<th>Proceso</th>
								<th>Usuario</th>
								<th>U.E.</th>
								<th>Fecha</th>
								<th>Estatus</th>
								<th>Log</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>				
				</div>
				
			</div>
			
			<br/>
			
			<div class="container" style="width: 80%">
				<h6> Archivos Procesados </h6> 
				<hr class="mt-3"/>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<span class="label">En la siguiente tabla se muestran los archivos que
											contenia el zip que se envio al sistema. Puede utilizar los
											filtros para mostrar solo los exitosos, los ignorados o aquellos
											que no se adjuntaron. <b>En la columna <i>log</i> se muestra
											la informacion</b></span>						
					</div>
				</div>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<div class="form-check form-check-inline">
							<input type="checkbox" name="filtros" id="exito" class="form-check-input" value="57" onclick="filtraArchivos()"/>
							<label for="exito" class="form-check-label">Adjuntados Exitosamente</label>
						</div>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<div class="form-check form-check-inline">
							<input type="checkbox" name="filtros" id="ignorado" class="form-check-input" value="51" onclick="filtraArchivos()"/>
							<label for="ignorado" class="form-check-label">Ignorados</label>
						</div>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<div class="form-check form-check-inline">
							<input type="checkbox" name="filtros" id="error" class="form-check-input" value="48,49,52" onclick="filtraArchivos()"/>
							<label for="error" class="form-check-label">No adjuntados</label>
						</div>
					</div>					
				</div>

				<div>
					<table id="dtArchivosProceso" class="table table-striped" style="width: 100%">											
						<thead>
							<tr>
								<th>Proceso</th>
								<th>Archivo</th>
								<th>Pago</th>
								<th>Folio</th>
								<th>Contrarecibo</th>
								<th>Log</th>
								<th>Fecha</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</form>
</body>
</html>