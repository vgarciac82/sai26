<%@page import="com.syc.gestion.util.Util"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	Caso c = (Caso)session.getAttribute( GestionInterface.ATT_CASE );
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	int nFolioPago = Util.folio( c );
	String cTipoPago = c.getTipoCaso().getGavetaAsociada(  );
	
	String action = request.getParameter("a");

	String cLogin = usuario.getLogin();
	String cUR = usuario.getU_UR();
	String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	String RFCUsuario = usuario.getuRFC();
	String result = StringUtils.trimToEmpty((String) session.getAttribute("RESULT"));
	String numeroEmpleado = "";
	numeroEmpleado = usuario.getNumeroEmpleado();

	String msg = StringUtils.trimToEmpty(request.getParameter("msgError"));
	
	boolean mostrarResultado = !StringUtils.isBlank(result);
	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>


<title>Resumen de Pago</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link href="css/ResumenFIEL.css" rel="stylesheet" type="text/css" />
<!-- estilos del dialogo-->

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="js/funciones.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../FIEL/js/ResumenFIEL.js"></script>
<script type="text/javascript" src="js/ActualizaFIEL.js"></script>
<script type="text/javascript" charset="utf-8">
		
		var cUR = "<%=cUR%>";
		var cLogin = "<%=cLogin%>";
		var RFC = "<%=RFCUsuario%>";
		var numeroEmpleado = "<%=numeroEmpleado%>";
		var mostrarResultado = <%=mostrarResultado%>;
		var mensaje = "<%=msg%>";
		 
		$(document).ready(function() {
		
			$('#mnLogTbl tbody').html("<%=result%>");
			$("#loginFirma").val( cLogin );
			$("#ueFirma").val( cUR );
			$("#rfcFirma").val( RFC );
			
			$.ajax({
				url : '../fortimax/documents',
				dataType : 'json',
				type : "GET",
				data : {
					"accion" : "send_tree"
				},
				async : true,
				success : function(objResp) {
					var nodos = objResp.nodos;
					for( var i in nodos){
						nodo = nodos[i];
						$("#bodyDoctos")
						.append("<tr>"
						      + "<td>" + nodo.nombreDocumento + "</td>"
						      + "<td><a style='color:black;' href=\"#\" onclick=\"muestraDocumento('" + nodo.fortimax + "')\">" + nodo.path + "</a></td>"
						      + "</tr>" );
					}
					creaTablaArchivos();
				},
				error : function(xhr, textStatus, errorThrown) {
					try{
						var obj = eval(xhr.responseText);
						var msg = obj.errCause;
						alert("No fue posible consultar los documentos debido al error: " + msg);
					}catch (e) {
						alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
					}
					
					$.unblockUI();
				}
			});
			
			
			init();
			consultaTablas();

			if ($("#cEsFirmaElectronica").val() == 'N'){
				$("#divImprimePoliza").show();
			} else
				$("#divImprimePoliza").hide();
			
		});
		
		function muestraDocumento(fortimaxNode){
			openCenteredWindow("../filestore?select=" + fortimaxNode, "_blank", 800, 1024);
		}
		
		function openCenteredWindow(url, name, height, width, parms) {
			var left = Math.floor((screen.width - width) / 2);
			var top = Math.floor((screen.height - height) / 2);
			var winParms = "top=" + top + ",left=" + left + ",height=" + height + ",width=" + width + ",scrollbar=yes,status=yes,resizable=1";
			if (parms) {
				winParms += "," + parms;
			}
			var win = window.open(url, name, winParms);
			if (parseInt(navigator.appVersion) >= 4) {
				win.window.focus();
			}
			return win;
		}

		function regresar(){
			location.href = "../Generador/AutorizaGeneracionLayouts.jsp?TYPE=" + tipoAutorizacion ;		
		}
		 
		function reloadTabla(){
		
			oTableEPS = $("#tblEPS").dataTable({
				bPaginate: true,
       			//bLengthChange: true,
       			bFilter: true,
       			bSort: false,
       			bInfo: false,
				sScrollX: "100%",
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay Datos",
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
				aaSorting: [[ 0, "asc" ]]
			});
			
			
			oTableFacturas = $("#tblFacturas").dataTable({
				bPaginate: true,
       			bLengthChange: true,
       			bFilter: true,
       			bSort: false,
       			bInfo: false,
				sScrollX: "100%",
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay Datos",
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
				aaSorting: [[ 0, "asc" ]]
			});
			
		}
		
		function init(){
			$("#cTipoPago").val("<%=cTipoPago%>");
			 queryFormPost("datosResumenPago", {async: false});
		}
		
		function consultaTablas(){
			
			oTableEPS = $("#tblEPS").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
				sScrollX: "100%",
				bAutoWidth: false,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
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
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fnResumenPagosEP('"+$("#cTipoPago").val()+"',"+$("#nFolioPago").val()+")",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{sName: "row_number"},
					{sName: "EP"},
					{sName: "totalEP"},
					
					{sName: "Enero"},
					{sName: "Febrero"},
					{sName: "Marzo"},
					{sName: "Abril"},
					{sName: "Mayo"},
					{sName: "Junio"},
					{sName: "Julio"},
					{sName: "Agosto"},
					{sName: "Septiembre"},
					{sName: "Octubre"},
					{sName: "Noviembre"},
					{sName: "Diciembre"}
				]
			});
			
			
			oTableFacturas = $("#tblFacturas").dataTable({
				bScrollCollapse: false,
        		bInfo: false,
				sScrollX: "100%",
				bAutoWidth: true,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
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
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fnResumenPagosFacturas('"+$("#cTipoPago").val()+"',"+$("#nFolioPago").val()+")",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{sName: "row_number"},
					{sName: "cRFCFactura"},
					{sName: "cfactura"},
					{sName: "mimporteconiva"},
					{sName: "mImporteBruto"},
					{sName: "mimporteiva"}
					
				]
			});
			
		}
		
		function creaTablaArchivos(){
			$("#tblFiles").dataTable({
				bPaginate: false,
				sScrollX: "100%",
       			bFilter: false,
       			bSort: false,
       			bInfo: false,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay Datos",
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
				}
			});
		}

		function rechazaPago(){
		
		 
		}
		
		 
		
		
		function aceptarDlg() {
			 
		}
		

		function cmdImprimir(elFormato) {
			if (elFormato == 'PolizaPago') {
				elFormato = elFormato + 'N';
				tipo = 'CR.';
			} else {
				tipo = '';
			}
			window.open(
				"../admin/SeguridadCatalogos?"
				+ "catalogo=CONTRARECIBO"
				+ "&accion=run"
				+ "&rn=" + elFormato + ".jasper"
				+ "&whereFolio= " + tipo + "caNoContrarrecibo = '" + $("#caNoContrarrecibo").val()
				+ "'",
				"popacuse",
				"scrollbars=1, resizable=yes, width=1024, height=768");

		}
		
		</script>
</head>

<body id="dt_example"  >	
<form action="" name="formCampos" id="formCampos">
	<input type="hidden" id="revisionFIEL" value="S">
	<input type="hidden" id="cEsFirmaElectronica">
	<div id="container" class="container" style="width: 100%;">
		<div class= "card">
			<div class="card-body"> 
				<h6>Resumen del Pago</h6>
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1" id="divImprimePoliza">
							<img src="imagenes/Imprimir.png" width="25" height="21" onClick="cmdImprimir('PolizaPago');"/> Imprimir  
					</div>	
				</div>
				<div class="row">
					<div class="col-2 col-lg-2 col-md-2 col-sm-12">
						<label for="tipoPago">Tipo de Pago:</label>
						<input id="tipoPago" name="tipoPago" value="" class="form-control" />
					</div>
					<div class="col-2 col-lg-2 col-md-2 col-sm-12">
						<label for="nFolioPago">Folio de Pago:</label>
						<input id="nFolioPago" name="nFolioPago" value="<%=nFolioPago%>" class="form-control"/>
					</div>
					<div class="col-2 col-lg-2 col-md-2 col-sm-12">
						<label for="cNoContrato">No Contrato:</label>
						<input id="cNoContrato" name="cNoContrato" value="" class="form-control"/>
					</div>
					<div class="col-2 col-lg-2 col-md-2 col-sm-12">
						<label for="caNoContrarrecibo">Contra Recibo:</label>
						<input id="caNoContrarrecibo" name="caNoContrarrecibo" value="" class="form-control" />
					</div>
				</div>
				<div class="row">
					<div class="col-lg-2 col-md-2 col-sm-12">
						<label for="mImporteFacturas">Importe en Facturas:</label>
						<input id="mImporteFacturas" name="mImporteFacturas" value="$ 0.00" class="form-control"/>
					</div>
					<div class="col-lg-2 col-md-2 col-sm-12">
						<label for="mImporteBruto">Importe Bruto:</label>
						<input id="mImporteBruto" name="mImporteBruto" value="$ 0.00" class="form-control"/>
					</div>
					<div class="col-lg-2 col-md-2 col-sm-12">
						<label for="mImporteOtrosImpuestos">Otros Impuestos:</label>
						<input id="mImporteOtrosImpuestos" name="mImporteOtrosImpuestos" value="$ 0.00" class="form-control"/>
					</div>
					<div class="col-lg-2 col-md-2 col-sm-12">
						<label for="mMontoSanciones">Monto Sanciones:</label>
						<input id="mMontoSanciones" name="mMontoSanciones" value="$ 0.00" class="form-control"/>
					</div>
					<div class="col-lg-2 col-md-2 col-sm-12">
						<label for="mMontoDevoluciones">Monto Devoluciones:</label>
						<input id="mMontoDevoluciones" name="mMontoDevoluciones" value="$ 0.00" class="form-control"/>
					</div>
					<div class="col-lg-2 col-md-2 col-sm-12">
						<label for="mMontoSubtotal">Importe Subtotal:</label>
						<input id="mMontoSubtotal" name="mMontoSubtotal" value="$ 0.00" class="form-control"/>
					</div>
				</div>
				<div class="row">
					<div class="col-lg-2 col-md-2 col-sm-12">
						<label for="mMontoAmortizaAnticipo">Amortizaci&oacute;n anticipo:</label>
						<input id="mMontoAmortizaAnticipo" name="mMontoAmortizaAnticipo" value="$ 0.00" class="form-control"/>
					</div>
					<div class="col-lg-2 col-md-2 col-sm-12">
						<label for="mMontoIVA">Monto IVA:</label>
						<input id="mMontoIVA" name="mMontoIVA" value="$ 0.00" class="form-control"/>
					</div>
					<div class="col-lg-2 col-md-2 col-sm-12">
						<label for="mMontoRetencion">Monto de Retenciones:</label>
						<input id="mMontoRetencion" name="mMontoRetencion" value="$ 0.00" class="form-control"/>
					</div>
					<div class="col-lg-2 col-md-2 col-sm-12">
						<label for="mMontoPenalizacion">Monto de Penalizaci&oacute;n:</label>
						<input id="mMontoPenalizacion" name="mMontoPenalizacion" value="$ 0.00" class="form-control"/>
					</div>
					<div class="col-lg-2 col-md-2 col-sm-12">
						<label for="mImporteNeto">Importe Neto:</label>
						<input id="mImporteNeto" name="mImporteNeto" value="$ 0.00" class="form-control"/>
					</div>
					<div class="col-lg-2 col-md-2 col-sm-12">
						<label for="mImporteEjercer">Importe a Ejercer:</label>
						<input id="mImporteEjercer" name="mImporteEjercer" value="$ 0.00" class="form-control"/>
					</div>
				</div>
				<div class="row">
					<div class="col-lg-12 col-md-12 col-sm-12">
						Concepto:
						<textarea rows="4" cols="190" id="cConceptoPago" name="cConceptoPago" class="form-control"></textarea>
					</div>
				</div>
			</div>
		</div>
				<!-- Encabezado del pago -->
			<div class= "card">	
				<div class= "card-body">
						<h6>Documentos del pago</h6>
						<div>
							<table class="text-nowrap" id="tblFiles">
								<thead >
									<tr>
										<th align="center" >Documento</th>
										<th align="center" >Consultar</th>
									</tr>
								</thead>
								<tbody id="bodyDoctos">
								</tbody>
							</table>
						</div>
				</div>
			</div>
				<!-- Detalle del pago -->
			<div class= "card">	
				<div class= "card-body">	
					<h6>Facturas del pago</h6>
					<div>
						<table id="tblFacturas" class="display text-nowrap">
							<thead >
								<tr>
									<th align="center" style="width: 5%">#</th>
									<th align="center" style="width: 20%">RFC</th>
									<th align="center" style="width: 45%">Factura</th>
									<th align="center" style="width: 10%">Importe con IVA</th>
									<th align="center" style="width: 10%">Importe sin IVA</th>
									<th align="center" style="width: 10%">Importe IVA</th>


								</tr>
							</thead>
						</table>
					</div>
				</div>
			</div>	
			<div class= "card">	
				<div class= "card-body">
					<h6>Estructuras presupuestales del pago</h6>
					<div>
						<table id="tblEPS" class="display text-nowrap">
							<thead>
								<tr align="center">
									<th align="center">#</th>
									<th align="center">Estructura Program&aacute;tica</th>
									<th align="center">Total EP</th>
									<th align="center">Enero</th>
									<th align="center">Febrero</th>
									<th align="center">Marzo</th>
									<th align="center">Abril</th>
									<th align="center">Mayo</th>
									<th align="center">Junio</th>
									<th align="center">Julio</th>
									<th align="center">Agosto</th>
									<th align="center">Sept</th>
									<th align="center">Octubre</th>
									<th align="center">Nov</th>
									<th align="center">Dic</th>
								</tr>
							</thead>
						</table>
					</div>
				</div>
			</div>
				<input type="hidden" name="cTipoPago" id="cTipoPago" value="<%=cTipoPago%>" />
				<input type="hidden" name="nFolioDev" id="nFolioDev" value="" />
	</div>
</form>

</body>
</html>
