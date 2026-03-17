<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String action = request.getParameter("a");

	int nFolioRectificacion = Integer
			.parseInt(StringUtils.isEmpty(request.getParameter("f")) ? "0" : request.getParameter("f"));

	Map<?, ?> rol = null;
		if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	

	String result = StringUtils.trimToEmpty((String) session.getAttribute("RESULT"));
	String tipoAutorizacion = ("VoBoPago".equals(action) ? "VOBO" : ("AutPago".equals(action) ? "AUT" : ""));
	String numeroEmpleado = "";

	String msg = StringUtils.trimToEmpty(request.getParameter("msgError"));

	boolean mostrarResultado = !StringUtils.isBlank(result);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Resumen de Solicitudes No Presupuestales</title>

<!-- Estilos estandar para los controles JQuery -->
<link href="../css/interfaz.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css"	href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"	href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/demo_page.css"></link>
<style type="text/css" title="currentStyle">
@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
@import "../Generador/css/demo_table_jui.css";
@import "../Generador/css/demo_page.css";
</style>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="js/funciones.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>

<script type="text/javascript" charset="utf-8">

	$(document).ready(function() {
		$("input.AyudaSyC").subIniciaDlg();
		$("input.autoCompletaSyC").subIniciaAutoCompleta();

		$("#rechazaPago").button().click(function() {
			rechazaPago();
		});

		$("#autorizaPago").button().click(function() {
			autorizaPago();
		});


		init();

		$("#dialog-procesar" ).hide();
		
		oTableDetalle = $("#tblDetalle").dataTable({
			bScrollCollapse : true,
			bInfo : false,
			sScrollX : "100%",
			bAutoWidth : false,
			bJQueryUI : true,
			bRetrive : true,
			bDestroy : true,
			sPaginationType : "full_numbers",
			oLanguage : {
				sProcessing : "Procesando...",
				sLengthMenu : "Mostrar _MENU_ registros",
				sZeroRecords : "No hay registros a mostrar",
				sEmptyTable : "No hay datos en la tabla",
				sLoadingRecords : "Cargando...",
				sInfo : "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty : "Registro 0 al 0 de 0",
				sInfoFiltered : "(filtado de _MAX_ registros)",
				sInfoPostFix : "",
				sInfoThousands : ",",
				sSearch : "Buscar:",
				oPaginate : {
					sFirst : "Primero",
					sPrevious : "Ant.",
					sNext : "Sigte.",
					sLast : "&Uacute;ltimo"
				}
			},
			bServerSide : true,
			sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fnResumenReint(" + $("#nFolioRectificacion").val() + ")",
			bJQueryUI : true,
			aaSorting : [ [ 0, "asc" ] ],
			aoColumns : [
				{sName : "cmes"	},
				{sName : "EP" },
				{sName : "cxp"	},
				{sName : "RFC"	},
				{sName : "CTAB" },
				{sName : "mImporte"	}
			]
		});

	});

	function init() {
		$("#nFolioRectificacion").val("<%=nFolioRectificacion%>");
		queryFormPost("resumen_reint", {async : false
		});
	}

	function autorizaPago() {
		$("#dialog-procesar" ).show();
		
		$("#dialog-procesar" ).hide();
		
	}

	function rechazaPago() {
		$("#dialog-procesar" ).show();
		
		$("#dialog-procesar" ).hide();
	}

	function dialogoProcesar() {
	}
</script>
</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form id="FormContrato" name="FormContrato">
		<div id="container" class="container">
			<h1>Resumen de Rectificaciones Presupuestales</h1>
					<fieldset>
						<legend>Operaciones:</legend>
						<table style="width: 98%">
							<tr style="width: 100%">
								<td align="center"><input title="Rechazar Pago"
									type="button" value="Rechazar pago" id="rechazaPago" /></td>
								<td align="center"><input title="Autorizar Pago"
									type="button" value="Autorizar pago" id="autorizaPago" /></td>
							</tr>
						</table>
					</fieldset>
				
			<div id ="generalDiv">
				<fieldset>
					<legend> Datos Generales </legend>
					<table>
						<tr>
							<td> Folio </td>
							<td> <input name = "nFolioRectificacionegro" id = "nFolioRectificacionegro" size = "15" readonly>
							<td> Oficio Rectificación </td>
							<td> <input name = "oficioRectif" id = "oficioRectif" size = "15" readonly>
							<td> Fecha Solicitud</td>
							<td> <input name = "fExp" id = "fExp" size = "15" readonly>
							</td>
						</tr>
						<tr>
							<td> Control Interno </td>
							<td> <input name = "ctr_int" id = "ctr_int" size = "15" readonly>
							
							<td> Tipo de Rectificación </td>
							<td> <input name = "cTipoRectificacion" id = "cTipoRectificacion" size = "15" readonly>
							<td> Fecha Afectación</td>
							<td> <input name = "fAplicacion" id = "fAplicacion" size = "15" readonly>
							</td>
						</tr>	
						<tr>
							<td> Unidad:
							</td>
							<td colspan = 5> <input  name = "descripcionUR" id = "descripcionUR" size = "90" readonly>
							</td>
						</tr>						
					</table>
				</fieldset>
			

			<div id="detalle">
				<fieldset>
				<legend> Datos del rectificación</legend>
				<table>		
					<tr>
						<td>
							Contrarrecibo
						</td>
						<td align="left">
							<input id="caNoContrarrecibo" name="caNoContrarrecibo" size ="" readonly>
						</td>
						<td >
							Usuario
						captura</td>
						<td align="left">
							<input id="cU_LoginCaptura" name="cU_LoginCaptura" readonly>
						</td>
					</tr>
					<tr>
						<td> Tipo Mov.
						</td>
						<td>
							<input type="text" name="dTipoMOVTO" id="dTipoMOVTO" size="20" readonly/>
						</td>
						<td> Folio SICOP</td>
						<td>
							<input type="text" name="aEjercicioFiscal" id="aEjercicioFiscal" size = "10" readonly/>
						</td>
						<td>
							 Importe Dice</td>
						<td>
							<input type="text" name="importeLC" id="importeLC" size="15" readonly/>
						</td>
					</tr>
					<tr>
						<td> Origen Presupuesto
						</td>
						<td>
							<input type="text" name="dTipoCausaAviso" id="dTipoCausaAviso"  size="15" readonly>
						</td>
						<td>  Folio SIAFF
						</td>
						<td>
							<input type="text" name="CatCausaAvisoReintegro" id="CatCausaAvisoReintegro"  size="10" readonly>
						</td>
						<td> Importe Debe</td>
						<td colspan="1">
							<input type="text" name="u_login" id="u_login" size="18"   readonly/>
						</td>
					</tr>
					<tr>
						<td> Concepto
						</td>
						<td colspan="5">
							<input type="text" name="concepto" id="concepto" size="110" readonly/>
						</td>
					</tr>
																
				</table>
				</fieldset>				
			</div>
			
			<div id="dv">
				<table id="tblDetalle" class="display" style="width: 100%"
					align="center">
					<thead style="width: 100%">
						<tr>
							<th>Mes</th>
							<th>Clave presupuestal</th>
							<th>Cuenta por Pagar</th>
							<th>RFC</th>
							<th>Cuenta Bancaria</th>
							<th>Importe</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
		</div>
	</form>


	<div id="dialog-procesar">
		<div id="esperar" align="center">
			Espere por favor.... <img border="0" src="../imagenes/espera.gif"
				height="30">
		</div>
	</div>

</body>
</html>