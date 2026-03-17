<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="org.apache.log4j.Logger"%>
<html>
<head>
<title>Autoriza Solicitud de Viaticos</title>
<%!Logger log = Logger.getLogger("ResumenViaticos.jsp");%>
<%
	int nFolioSolicitudViaticos = Integer.parseInt( (String) session.getAttribute("folio") );
	String usuarioAutoriza = (String) session.getAttribute("usuario");
 	boolean esAutorizacion = "true".equalsIgnoreCase((String) session.getAttribute("autorizacion"));
 	int IdRenglonAutorizador = Integer.parseInt( (String) session.getAttribute("IdRenglonAutorizador") );
	
	session.removeAttribute("folio");
	session.removeAttribute("usuario");
	session.removeAttribute("autorizacion");
	session.removeAttribute("IdRenglonAutorizador");
	
%>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">

<style type="text/css" title="currentStyle">
@import "css/demo_page.css";

@import "css/demo_table_jui.css";

@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
</style>

<style type="text/css">
input[readonly] {
	background-color: #EEEEEE;
}
</style>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker-es.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" charset="utf-8">
	var nFolioSolicitudViaticos = '<%=nFolioSolicitudViaticos%>';
	var empleadoAutoriza = '<%=usuarioAutoriza%>';
	var esAutorizacion = <%=esAutorizacion%>;
	var IdRenglonAutorizador = <%=IdRenglonAutorizador%>;
	
	var oTableAgenda;

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

	$(document).ready(
		function() {
		
			$("#nFolioSolicitudViaticos").val(nFolioSolicitudViaticos);
			$("#empleadoAutoriza").val(empleadoAutoriza);
			$("#esAutorizacion").val(esAutorizacion);
			$("#IdRenglonAutorizador").val(IdRenglonAutorizador);
			
			loadData();
			
			$("#enviaRespuestaBtn").button().click(function() {
				enviarRespuesta();
			});

			$("#DialogComentarios").dialog({
				autoOpen : false,
				height : 220,
				width : 490,
				modal : true,
				buttons : {
					"Aceptar" : function() {
						$("#motivoRechazo").val($("#motivoRechazoTxt").val());
						$(this).dialog("close");
					}
				}
			});
		});

	function enviarRespuesta() {
	
		var autorizado = $("input[name='AutorizoRB']:checked").val();
		if( "Si" == autorizado ) {
			$("#motivoRechazo").val("");
			$("#motivoRechazoTxt").val("");
		}
		
		if( confirm("Esta seguro de enviar la respuesta?") ){
			$.blockUI();
			$("#autorizacionFrm").submit();
		}

	}

	function loadData() {
		try {
			leeInformacionCaptura();
			creaDTAgenda();
			creaTransporteDT();
			cargaResumenViaticos();
		} catch( e ) {
			alert(e);
		}
	}

	function leeInformacionCaptura() {
		queryFormPost({
			queryName : "infoTramiteViaticosRead",
			async : false,
			callback : function() {}
		});
	}

	function creaDTAgenda() {
		var where = " Folio = " + $("#nFolioSolicitudViaticos").val();

		oTableAgenda = $('#dtComisionesDet').dataTable(
			{
				"bPaginate" : false,
				"bLengthChange" : true,
				"bFilter" : true,
				"bSort" : true,
				"bInfo" : true,
				"bAutoWidth" : true,
				"sScrollY" : 300,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"sPaginationType" : "full_numbers",
				"bScrollCollapse" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
					+ window.location.host + "/"
					+ window.location.pathname.split("/")[ 1 ]
					+ "/crud?rt=t&ql=vSolicitudViaticosAgenda&qw="
					+ where,
				aoColumns : [ {
					sName : "nDocRenglon",
					bVisible : false
				}, {
					sName : "fInicio"
				}, {
					sName : "fFin"
				}, {
					sName : "Tipo"
				}, {
					sName : "DESTINO"
				}, {
					sName : "cMotivo"
				}, {
					sName : "TarifaD",
					sClass : "alignLeft"
				}, {
					sName : "CuotaPorDia",
					sClass : "alignLeft"
				}, {
					sName : "DIAS"
				}, {
					sName : "Total",
					sClass : "alignLeft"
				} ],
				oLanguage : es_mx
			});

	}


	var oTableTransporte;
	function creaTransporteDT() {
		var cWhere = "nFolioSolicitudViaticos = " + $("#nFolioSolicitudViaticos").val();
		oTableTransporte = $('#dtTransporteDet').dataTable(
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
				sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=vSolicitudViaticosTransporte&qw=" + " " + encodeURI(cWhere),
				aoColumns : [
					{
						sName : "nFolioSolicitudViaticos",
						bSearchable : false,
						bSortable : false,
						bVisible : false,
						sClass : "alignLeft"
					},
					{
						sName : "nIdTransporte",
						bSearchable : false,
						bSortable : false,
						bVisible : true,
						sClass : "alignLeft"
					},
					{
						sName : "cOrigen",
						bSearchable : false,
						bSortable : false,
						bVisible : true,
						sClass : "alignLeft"
					},
					{
						sName : "mMontoT",
						bSearchable : false,
						bSortable : false,
						bVisible : true,
						sClass : "alignLeft"
					}
				],
				oLanguage : es_mx
			});

	}
	
	function autorizaRBClick(){
		var autorizado = $("input[name='AutorizoRB']:checked").val();
		if( "No" == autorizado ){
			$("#DialogComentarios").dialog("open");
		}else{
			$("#motivoRechazo").val("");
			$("#motivoRechazoTxt").val("");
		}
	}
	
	function cargaResumenViaticos(){
	
		queryFormPost("resumenDiasSolicitudViaticos_Read", {async:false});
		
		var mTotal = Number($("#mImporteAgenda").val()) + Number($("#mTransporte").val());	
		mTotal = mTotal.toFixed(2);
		$("#mTOTAL").val(mTotal);
		$("#mImporteAgenda").formatCurrency();
		$("#mTransporte").formatCurrency();
		$("#mTOTAL").formatCurrency();
		
		var nTotalDias = Number($("#nDNacional").val()) + Number($("#nDInternacional").val()) + Number($("#nDPendientes").val());
		nTotalDias = nTotalDias.toFixed(1);
		$("#nDTotal").val(nTotalDias);
	}
</script>
</head>
<body id="dt_example">
	<form id="autorizacionFrm" method="get" action="../viaticos/RegistraAutorizacion">
		<input type="hidden" id="motivoRechazo" name="motivoRechazo" value="">
		<input type="hidden" id="empleadoAutoriza" name="empleadoAutoriza" value="">
		<input type="hidden" id="esAutorizacion" name="esAutorizacion" value="">
		<input type="hidden" id="IdRenglonAutorizador" name="IdRenglonAutorizador" value="">
		
		<div id="container" class="container" style="width: 80%">
			<h1>Solicitud de Viaticos</h1>
			<div id="OpcionesAutorizacion">
				<fieldset>
					<legend> Autorizaci&oacute;n </legend>
					<table align="center">
						<tr>
							<td>Autoriza usted la siguiente comisi&oacute;n:</td>
							<td align="left">
								<input type="radio" value="Si" id="SiAutorizo" name="AutorizoRB" checked="checked" onclick="autorizaRBClick()"> 
								<label for="SiAutorizo">S&iacute;</label> 
								<input type="radio" value="No" id="NoAutorizo" name="AutorizoRB" onclick="autorizaRBClick()"> 
								<label for="NoAutorizo">No</label>
							</td>
							<td align="left">
								<input type="button" id="enviaRespuestaBtn" value="Enviar Respuesta" />
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
			<div>
				<fieldset>
					<legend>General</legend>
					<div>
						<table width="100%">
							<tr>
								<td align="left">
									<table>
										<tr>
											<td align="right">Folio Solicitud:</td>
											<td align="left"><input type="text" readonly="readonly" size="18" id="nFolioSolicitudViaticos" name="nFolioSolicitudViaticos"></td>
										</tr>
									</table>
								</td>
								<td align="right">
									<table>
										<tr>
											<td align="right">Fecha de Captura:</td>
											<td align="left"><input type="text" readonly="readonly" size="11" id="dFechaCaptura" name="dFechaCaptura"></td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</div>
				</fieldset>
			</div>
			<div id="datosViaticanteDIV">
				<fieldset>
					<legend>Datos Viaticante</legend>
					<table>
						<tr>
							<td>
								<table>
									<tr>
										<td align="right">Beneficiario:</td>
										<td align="left" align="left"><input type="text" size="30" readonly="readonly" id="nombreEmpleadoBeneficiario" name="nombreEmpleadoBeneficiario" class="datosViaticante"></td>
										<td align="left"><input type="text" size="15" readonly="readonly" id="aPaternoEmpleadoBeneficiario" name="aPaternoEmpleadoBeneficiario" class="datosViaticante"></td>
										<td align="left"><input type="text" size="15" readonly="readonly" id="aMaternoEmpleadoBeneficiario" name="aMaternoEmpleadoBeneficiario" class="datosViaticante"></td>
										<td align="right">Unidad Ejecutora:</td>
										<td align="left"><input type="text" size="3" readonly="readonly" id="UEEmpleadoBeneficiario" name="UEEmpleadoBeneficiario" class="datosViaticante"> &nbsp; <input type="text" size="35" readonly="readonly" id="DescUEEmpleadoBeneficiario" class="datosViaticante"></td>
									</tr>
									<tr>
										<td align="left" colspan="6">No. Empleado: <input type="text" size="8" readonly="readonly" id="numEmpleadoBeneficiario" name="numEmpleadoBeneficiario" class="datosViaticante" onchange="buscaEmpleado()"> Plaza: <input type="text" size="45" readonly="readonly" id="plazaEmpleadoBeneficiario" name="plazaEmpleadoBeneficiario" class="datosViaticante"> Nivel: <input type="text" size="8" readonly="readonly" id="nivelEmpleadoBeneficiario" name="nivelEmpleadoBeneficiario"
												class="datosViaticante"
											> <input type="hidden" id="d_email" name="d_email" value="" class="datosViaticante" />
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
			<div id="datosComisionDIV">
				<fieldset>
					<legend>Datos Comisi&oacute;n</legend>
					<table id="dtComisionesDet" class="display">
						<thead>
							<tr>
								<th>Renglon</th>
								<th>F. Inicio</th>
								<th>F. Fin</th>
								<th>Tipo</th>
								<th>Destino</th>
								<th>Motivo</th>
								<th>Tarifa</th>
								<th>Cuota por Dia</th>
								<th>Dias</th>
								<th>Total</th>
							</tr>
						</thead>
					</table>
				</fieldset>
			</div>
			<div id="datosTransporteDIV">
				<fieldset>
					<legend>Transporte</legend>
					<table id="dtTransporteDet">
						<thead>
							<tr>
								<th>Nfolio</th>
								<th>Tipo</th>
								<th>Monto</th>
								<th>OrigenDestino</th>
							</tr>
						</thead>
					</table>
				</fieldset>
			</div>
			<div id="divResumenViaticos" align="right">
				<fieldset>				
					<legend>Resumen Viaticos</legend>					
						<table>
							<tr>							
								<td align="right">D&iacute;as Acumulados</td>									
							</tr>
							<tr>
								<td align="right"><label id="lblNacional">Nacional:</label></td>
								<td align="right"><input type="text" readonly="readonly" id="nDNacional" name="nDNacional" size="8" style="text-align: right" ></td>									
							</tr>
							<tr>
								<td align="right"><label id="lblInternacional">Internacional:</label></td>
								<td align="right"><input type="text" readonly="readonly" id="nDInternacional" name="nDInternacional" size="8" style="text-align: right" ></td>									
							</tr>
							<tr>
								<td align="right"><label id="lblNacional">Este Formato:</label></td>
								<td align="right"><input type="text" readonly="readonly" id="nDEsteFormato" name="nDEsteFormato" size="8" style="text-align: right" ></td>
								<td nowrap>&nbsp;</td>
								<td nowrap>&nbsp;</td>
								<td nowrap>&nbsp;</td>
								<td align="right"><label id="lblmAgenda">Importe Agenda:</label></td>
								<td align="right"><input type="text" readonly="readonly" id="mImporteAgenda" name="mImporteAgenda" size="18" style="text-align: right" ></td>									
							</tr>
							<tr>
								<td align="right"><label id="lblNacional">Pendientes:</label></td>
								<td align="right"><input type="text" readonly="readonly" id="nDPendientes" name="nDPendientes" size="8" style="text-align: right" ></td>
								<td nowrap>&nbsp;</td>
								<td nowrap>&nbsp;</td>
								<td nowrap>&nbsp;</td>
								<td align="right"><label id="lblTransporte">Transporte:</label></td>
								<td align="right"><input type="text" readonly="readonly" id="mTransporte" name="mTransporte" size="18" style="text-align: right" ></td>
							</tr>
							<tr>
								<td align="right"><label id="lblNacional">TOTAL:</label></td>
								<td align="right"><input type="text" readonly="readonly" id="nDTotal" name="nDTotal" size="8" style="text-align: right" ></td>
								<td nowrap>&nbsp;</td>
								<td nowrap>&nbsp;</td>
								<td nowrap>&nbsp;</td>
								<td align="right"><label id="lblTOTAL">TOTAL:</label></td>
								<td align="right"><input type="text" readonly="readonly" id="mTOTAL" name="mTOTAL" size="18" style="text-align: right" ></td>
							</tr>
						</table>
				</fieldset>
			</div>
		</div>
		<div id="DialogComentarios">
			<fieldset>
				<legend>Motivo de Rechazo</legend>
				<table>
					<tr>
						<td><textarea rows="5" cols="60" id="motivoRechazoTxt"></textarea></td>
					</tr>
				</table>
			</fieldset>
		</div>		
	</form>
</body>
</html>