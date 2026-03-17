<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.core.TipoCaso"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>

<%
	String mensaje = request.getParameter("mensaje") != null ? request
			.getParameter("mensaje") : "";
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	String prefixPath = getServletContext().getRealPath(
			"/WEB-INF/mail-bodies/");

	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	CasoBusinessLogic ct = new CasoBusinessLogic("jdbc/gestion");

	String msj = "";
	msj = (String) session.getAttribute("mensaje");

	if (msj != null)
		session.removeAttribute("mensaje");
	else
		msj = "";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Seguimiento de un trámite</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"
	href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"
	href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_page.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js">
	
</script>

<script type="text/javascript" src="../Generador/js/crud.js">
	
</script>

<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js">
	
</script>

<script type="text/javascript" src="../Ayudas/js/autoCompleta.js">
	
</script>

<script type="text/javascript" src="../js/jsquery.js">
	
</script>

<script type="text/javascript"
	src="../Generador/js/jquery.dataTables.js">
	
</script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery-ui-1.8.16.custom.min.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.ui.datepicker.js">
	
</script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js">
	
</script>
<script type="text/javascript" src="../js/catalogo/general.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.all.js">
	
</script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js">
	
</script>
<script type="text/javascript">
	var tb ;
	
	/**
/**
 * Folio para el contrato de estimacion.
 */
var folioSAI = "";
	$(document).ready(function() {
		init();

	});
	
	
	function init() {
	
	$("#fDesde").datepicker( {
		showOn : "button",
		dateFormat : "dd/mm/yy",
		buttonImage : "../Generador/images/calendar.gif",
		buttonImageOnly : true
	});


	$("#fHasta").datepicker( {
		showOn : "button",
		dateFormat : "dd/mm/yy",
		buttonImage : "../Generador/images/calendar.gif",
		buttonImageOnly : true
	});


		//Inicializa las ayudas. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.AyudaSyC").subIniciaDlg();
		//Inicializa el autocompletar. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.autoCompletaSyC").subIniciaAutoCompleta();


			$.fn.dataTableExt.oApi.fnReloadAjax = function ( oSettings, sNewSource ) {
				if ( typeof sNewSource != 'undefined' )
				oSettings.sAjaxSource = sNewSource;
				
				this.fnClearTable( this );
				this.oApi._fnProcessingDisplay( oSettings, true );
				var that = this;
				
				$.getJSON( oSettings.sAjaxSource, null, function(json) {
				/* Got the data - add it to the table */
				for ( var i=0 ; i<json.aaData.length ; i++ ) {
				that.oApi._fnAddData( oSettings, json.aaData[i] );
				}
				
				oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
				that.fnDraw( that );
				that.oApi._fnProcessingDisplay( oSettings, false );
				});
			};


		//Crea el DataTable con los campos de config. minimos. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		tb = $('#dt_resumen_traza').dataTable({
			        "bPaginate": true,
        			"bLengthChange": true,
        			"bFilter": true,
        			"bSort": true,
        			"bInfo": true,
        			"bAutoWidth": false,
					"sScrollY": 270,
					"sScrollYInner": "100%",
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers",
					"sScrollX": "100%",
					"sScrollXInner": "110%",
					"bScrollCollapse": true,	
					"bServerSide": true, 
					sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vimx_seguimiento_tramite&qw=folio='-1'",
			        aoColumns   : [
						{ sName: "FOLIO" },
						{ sName: "DESC_TRAMITE" },
						{ sName: "OPERACION"},
						{ sName: "RESPONSABLE"}
					], oLanguage: {
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
						sSearch: "Filtro:",
						oPaginate: {
							sFirst:    "Primero",
							sPrevious: "Ant.",
							sNext:     "Sigte.",
							sLast:     "&Uacute;ltimo"
						}
					}
		});
		

		$("#buscar").button().click(function() {
				($("#tc").val(), //RECUPERACIÓN DE LOS CAMPOS
				$("#noFolio").val(),
				$("#fDesde").val(), 
				$("#fHasta").val()
				);
				
				 
				var token = "";   //armar url
				var cond = "";
		
					if( $("#tc").val() != "" ){
						cond = token + cond + " id_tramite = " + $("#tc").val();
						token = " AND ";
					}if( $("#noFolio").val() != "" ){
						cond += token + " folio = '" + $("#noFolio").val() + "'";
						token = " AND ";
					}if( $("#fDesde").val() != "" && $("#fHasta").val() == "" ){
						cond += token + " convert (DATE , fecha ,103) >=" + " convert (DATE ,'" + $("#fDesde").val() + "', 103)";
						token = " AND ";
					}if( $("#fDesde").val() == "" && $("#fHasta").val() != "" ){
						cond += token + " convert (DATE , fecha ,103) >=" + " convert (DATE ,'" + $("#fHasta").val() + "', 103)";
						token = " AND ";
					}if( $("#fDesde").val() != "" && $("#fHasta").val() != "" ){
						cond += token + " convert (DATE , fecha, 103) BETWEEN  convert (DATE , '" + $("#fDesde").val() + "', 103)" + " AND " +"convert (DATE , '" + $("#fHasta").val() + "', 103)";
						token = " AND ";}
					
				var url = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vimx_seguimiento_tramite&qw=" + cond; //nueva ruta con los where
				tb.fnSettings().sAjaxSource = url;
				tb.fnReloadAjax(); // funcion para el recargado de ajax
		
		});

		$("#cancelar").button().click(function() {
		});
		
				$("#dialog-mensaje").dialog({
			autoOpen : false, // se juega con el true o false para que se muestre o no
			height : 250,
			width : 400,
			modal : true,
			buttons : {
				"Aceptar" : function() {
					$(this).dialog("close");
				}
			}
		});
		
		
		setDblClck();

			$("#div_traza").dialog({
			autoOpen : false, // se juega con el true o false para que se muestre o no
			height : 450,
			width : 550,
			modal : true,
			buttons : {
				"Aceptar" : function() {
					$(this).dialog("close");
				}
			}
		});
		
		
		<%if (msj != null && !"".equals(msj)) {%>
			$("#dialog-mensaje").dialog("open");
		<%}%>

		}
		
	function limpiarSesion()
		{
			window.location.href="SeguimientoTramite.jsp?id=<%=request.getParameter("tc")%>";
		}

function cargaDetalleTraza(){
		$("#detalleTraza tbody tr").remove();
		$.getJSON("../catalogos/SelectJson.jsp", {
			Tabla : "CONSULTA_TRAZA_COMPLETA",
			Param : $("#folioSAI").val(),
			MaxReg : "",
			ajax : 'false'
		}, function(data) {
			var trText = "";
			for ( var i = 0; i < data.length; i++) {
				trText += "<tr><td>" + data[i].Col4 + "</td><td>" + data[i].Col6 + "</td><td>" + data[i].Col3 + "</td></tr>";
				$('#detalleTraza > tbody:first').append(trText);
				trText = "";
			}
		});
}
function cargaEncabezado(folio){
		$("#folioSAI").val(folio);
		 queryFormPost({
			queryName:"readTrazaCompletaTramite",
			async:false,
            callback:function(){
						cargaDetalleTraza();
					}
			});


}		
	
	function setDblClck() {
	$("#dt_resumen_traza tbody").dblclick(function(evt) {

		var aPos = tb.fnGetPosition(evt.target.parentNode);

		if (aPos instanceof Array)
			currIndex = aPos[0];
		else
			currIndex = aPos;

		var arr = tb.fnGetData()[currIndex];
		folioSAI = arr[0];
		cargaEncabezado(folioSAI);
			//alert ("resultado" + FOLIO );
			$("#div_traza").dialog("open");
	});
}


	
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<form action="../gstnmngr/SeguimientoTramite" id="FormTraza"
		name="FormTraza" method="post">
		<input type="hidden" value="" id="folioSAI" name="folioSAI">
		<input type="hidden" value="MuestraTraza" id="accion" name="accion">
		<input type="hidden" value="" id="OperacionActual" name="OperacionActual">
		<input type="hidden" value="" id="buscaVal" name="buscaVal">
		<input type="hidden" value="" id="existe" name="existe">
		<div id="container" class="container">
			<h1>Seguimiento de un Trámite</h1>
			<fieldset>
				<legend>Filtros de busqueda</legend>
				<table align="center">
					<tr>
						<td align="right">&nbsp; <font class="LabelSalida">
								Tr&aacute;mite:</font></td>
						<td>&nbsp; <SELECT id="tc" name="tc">
								<%
									Map map = ct.getAllTipoCaso();
									for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
										String name = (String) iter.next();
										TipoCaso tc = (TipoCaso) map.get(name);
								%>
								<option value="<%=tc.getIdTC()%>"><%=name%></option>
								<%
									}
								%>
						</SELECT></td>
					</tr>
					<tr>
						<td align="right">&nbsp; <font class="LabelSalida">
								Folio:</font>
						</td>
						<td>&nbsp; <input type="text" id="noFolio" name="noFolio"
							value="" style="text-transform: uppercase" />
						</td>
					</tr>
					<tr>
						<td align="right">&nbsp; <font class="LabelSalida">
								Fecha: inicio&nbsp;</font>
						</td>
						<td>&nbsp; <input type="text" id="fDesde" name="fDesde"
							readOnly="readonly" value=""> &nbsp; <font
							class="LabelSalida">&nbsp;fin&nbsp;</font>&nbsp; <input
							type="text" id="fHasta" name="fHasta" readOnly="readonly"
							onchange="">
						</td>
					</tr>
					<tr>
					</tr>
					<tr>
					</tr>
					<tr>
					</tr>
					<tr>
					</tr>
					<tr>
					</tr>
					<tr>
					</tr>
					<tr>
					</tr>
					<tr>
						<td colspan="2" align="right"><input type="button"
							value="Consultar" name="buscar" id="buscar" /> <input
							type="button" id="cancelar" name="cancelar" value="Cancelar"
							onclick="limpiarSesion();" /></td>

					</tr>
				</table>
			</fieldset>
			<div id="dv">
				<table id="dt_resumen_traza" class="display" cellspacing="0"
					cellpadding="2" align="center">
					<thead>
						<tr>
							<th>Folio</th>
							<th>Tipo Tr&aacute;mite</th>
							<th>Operaci&oacute;n</th>
							<th>Responsable</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
	</form>
	<div id="dialog-mensaje" title="Mensaje de Sistema">
		<table align="center">
			<tr>
				<td><textarea cols="60" rows="10" id="mensaje"><%=msj%></textarea>
				</td>
			</tr>

		</table>

	</div>
	<div id="div_traza"  title="Seguimiento de Trámite - Detalle">
		<table align="center">
				<tr>
					<td align="right"><font class="LabelSalida">Folio</font></td><td><input type="text" align="left" id="folio" name="folio" value="" style="text-transform: uppercase" /></td>
				</tr>
				<tr>
					<td align="right"><font class="LabelSalida">Estatus</font></td><td><input type="text" align="left" id="estatus" name="estatus" value=""/></td>
				</tr>
				<tr>
					<td align="right"><font class="LabelSalida">Fecha Captura</font></td><td><input type="text" align="left" id="fCaptura" name="fCaptura" value=""/></td>
				</tr>
				<tr>
					<td align="right"><font class="LabelSalida">Tipo de Documento</font></td><td><input type="text" align="left" id="tDocumento" name="tDocumento" size="25" value=""/></td>
				</tr>
				<tr>
					<td align="right"><font class="LabelSalida">Usuario o Grupo Responsable</font></td><td><input type="text" align="left" id="uGResponsable" name="uGResponsable" size="40" value=""/></td>
				</tr>
		</table>
				<table align="center" border="2" width="100%" id="detalleTraza">
					<thead>
					<tr style="background-color: gray">
						<th align="center"><font class="LabelSalida">Fecha</font></th>
						<th align="center"><font class="LabelSalida">Operacion</font></th>
						<th align="center"><font class="LabelSalida">Responsable</font></th>
					</tr>
					</thead>
					<tbody>
					
					</tbody>
				</table>

	</div>

</body>
</html>