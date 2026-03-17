<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@page import="com.syc.obrapublica.ObraPublicaContractBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>

<%

	String ipNombreServidor = java.net.InetAddress.getByName(request.getServerName()).toString();
	String[] ipServidor = ipNombreServidor.split("/");
	boolean correoPrd = false;
	if (ipServidor[1].equals(GestionInterface.SYS_IP_PRODUCCION))
		correoPrd = true;
		
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String cancelContr = "No";
	String typeDocument ="";
	ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(GestionInterface.ATT_CONEXION);
	opbl.correoProduccion = correoPrd;
				
	if (request.getParameter("cancelContract") != null
			&& request.getParameter("cancelContract").equals("Si")) {
		cancelContr = "Si";
	}
	if (cancelContr.equals("Si")) {
		typeDocument = (String) request.getParameter("typeDocument");
			String[][] r = opbl.CancelContractMultiple(request.getParameter("listaFolios"),request.getParameter("motivo"));
			int numFolios = Integer.parseInt(request.getParameter("numFolios"));
			opbl = null;
			
			out.println("<html>");
			out.println("<head>");
			out.println("<script type=\"text/javascript\">");
			out.println("function onLoad(){");
			out.println("parent.terminaAppContCancel(" + r[1][0] + ")");
			out.println("}");
			out.println("</script>");
			out.println("</head>");
			out.println("<body onload=\"onLoad()\">");
			out.println("<table>");
			for (int i = 1; i <= numFolios; i++ ){
				out.println("<tr>");
				out.println("<td>" + r[i][1] + "</td>");
				out.println("</tr>");
			}
			out.println("</table>");
			out.println("</body>");
			out.println("</html>");
			out.flush();
			
			return;
	}
 %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type"
			content="text/html; charset=ISO-8859-1">
		<title>Suspensi&oacute;n de Pagos Obra P&uacute;blica</title>
<link href="../admin/js/jq9/css/smoothness/jquery-ui-1.9.0.custom.css" rel="stylesheet">
<link href="css/demo_page.css" rel="stylesheet">
<link href="css/demo_table_jui.css" rel="stylesheet">
<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../admin/js/jq9/jquery-ui-1.9.0.custom.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/cancelaContratoOP.js"></script>
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<link rel="stylesheet" type="text/css" href="../css/interfaz.css" />
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<script type="text/javascript" src="../js/jsquery.js"></script>

<script type="text/javascript" charset="utf-8">
		function LetrasNums(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			if (keyPressed > 123 && keyPressed != 209 && keyPressed != 241){
			alert ("Solo se permiten Letras y Numeros");
			 }
	if (keyPressed == 61 || keyPressed == 63 || keyPressed == 62
			|| keyPressed == 59 || keyPressed == 58 || keyPressed == 60
			|| keyPressed == 91 || keyPressed == 92 || keyPressed == 93
			|| keyPressed == 94 || keyPressed == 95 || keyPressed == 96) {
			return false;
			 }
	return !(keyPressed > 32 && (keyPressed < 48 || keyPressed > 122)
			&& keyPressed != 209 && keyPressed != 241);
		}
		</script>

		<script type="text/javascript">
			$(document).ready(function() {
				init();
				$("input.AyudaSyC").subIniciaDlg();
				$("input.autoCompletaSyC").subIniciaAutoCompleta();
			});
		</script>
	</head>
	<body id="dt_example">
		<form action="../SuspensionPagos" method="post" id="mainForm">
			<input type="hidden" value="" id="cEjercicio" name="cEjercicio">
			<input type="hidden" value="" id="cCentroContable"
				name="cCentroContable">
			<input type="hidden" value="" id="folioSAI" name="folioSAI">
			<input type="hidden" value="" id="estatusPago" name="estatusPago">
			<h1>
				<label id="titulo">
					Cancelaci&oacute;n Masiva
				</label>
			</h1>
			<div id="container" class="container SyCData">
				<h5>
					Ingrese la siguiente informaci&oacute;n:
				</h5>
				<table>
					<tr>
				<td nowrap="nowrap" align="right">Unidad Normativa:</td>
					<td><input id="hUnidadNormativa" name="hUnidadNormativa" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
					<input id="UnidadNormativa" name="UnidadNormativa" type="hidden"   value="" size="5" maxlength="5" class=""/></td>
					
					<td nowrap="nowrap" align="right">Unidad Ejecutora:</td><td><input id="hcUnidadEjecutora" name="hcUnidadEjecutora" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
					<input id="cUnidadEjecutora" name="cUnidadEjecutora" type="hidden"   value="" size="5" maxlength="5" class=""/></td>
					</tr>
					<tr>
				<td nowrap="nowrap" align="right">Entidad Federativa:</td>
				<td>&nbsp;&nbsp;<select id="EntidadFederativa" name="EntidadFederativa">
					<option value="Z:">A</option>
					<option value="Y:">B</option>
					<option value="X:">C</option>
					<option value="xx" selected>--</option>
				</select></td>
				<td align="right">Status</td>
				<td><select id="estatusContrato" name="estatusContrato">
					<option value="APARTADO">APARTADO</option>
					<option value="PRECOMPROMISO">PRECOMPROMISO</option>
					<option value="COMPROMISO">COMPROMISO</option>
					<option value="" selected>--</option>
				</select></td>
				</tr>
					<tr>
								<td nowrap="nowrap" align="right">Programa Presupuestario:</td>
					<td><input id="hProgramaPresupuestario" name="hProgramaPresupuestario" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
					<input id="ProgramaPresupuestario" name="ProgramaPresupuestario" type="hidden"   value="" size="5" maxlength="5" class=""/></td>
				
				<td nowrap="nowrap" align="right">Cartera de proyecto:</td>
					<td><input id="hCartera" name="hCartera" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC"/>
					<input id="Cartera" name="Cartera" type="hidden"   value="" size="5" maxlength="5" class=""/></td>
				</tr>
				
					<tr>
											<td colspan="1" align="right">
							N&uacute;mero de Contrato:
						</td>
						<td colspan="1" align="left">
							&nbsp;&nbsp;<input type="text" id="cveContrato" size="30">
						</td>
						<td align="left">
						  Calendarios a: 
						</td>
						<td colspan="1" align="left">
											<select name="InfoRegMes" id="InfoRegMes" onchange=""  value="">
						<option value="1">ENERO</option>
						<option value="2">FEBRERO</option>
						<option value="3">MARZO</option>
						<option value="4">ABRIL</option>
						<option value="5">MAYO</option>
						<option value="6">JUNIO</option>
						<option value="7">JULIO</option>
						<option value="8">AGOSTO</option>
						<option value="9">SEPTIEMBRE</option>
						<option value="10">OCTUBRE</option>
						<option value="11">NOVIEMBRE</option>
						<option value="12" selected>DICIEMBRE</option>
						</td>
						
					</tr>
						<tr>
							<td colspan="4">
								Motivo de la Cancelaci&oacute;n:
							</td>
						</tr>
						<tr>
							<td colspan="8">
								<textarea title="Motivo por el que se cancelan los contratos." id="cMotivo" name="cMotivo"
									class="apart" rows="3"
									style="width: 99%; max-width: 99%; font-family: verdana; font-size: 10pt;"
									onKeyPress="return LetrasNums(event)"></textarea>
							</td>
						</tr>
				</table>
				<table width="100%">
					<tr>
					<td colspan="3" align="left">
				<h5>
						Seleccione los contratos que deben ser cancelados.
				</h5>
						</td>
						<td colspan="1" align="right">
							<input type="button" id="limpiarButton" value="Limpiar">
						</td>
						<td colspan="1" align="right">
							<input type="button" id="aceptarButton" value="Aceptar">
						</td>
						<td colspan="1" align="right">
							<input type="button" id="searachButton" value="Buscar">
						</td>
					</tr>
				</table>
				<table id="resultTable" width="100%">
					<thead>
						<tr>
							<th>
								&nbsp;
							</th>
							<th>
								No.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Contrato
							</th>
							<th>
								&nbsp;&nbsp;&nbsp;Beneficiario&nbsp;&nbsp;&nbsp;
							</th>
							<th>
								Monto
							</th>
							<th>
								Folio SAI
							</th>
							<th>
								Estatus
							</th>
							<th>
								C. Contable
							</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
				<div id="question" style="display: none; cursor: default">
					<h4>
						&#191;Desea continuar?
					</h4>
					<br>
					<h5>
						<label id="msgAdvertencia"></label>
					</h5>
					<input type="button" id="yes" value="Continuar" />
					<input type="button" id="no" value="Cancelar" />
				</div>
			</div>
			<div id="dialog-form-apcon"
				title="Aplicaci&oacute;n Presupuestal/Contable">
				<div id="divAplica">
					<iframe id="ifAplica" src="about:blank"></iframe>
				</div>
			</div>
			
		</form>
	</body>
</html>