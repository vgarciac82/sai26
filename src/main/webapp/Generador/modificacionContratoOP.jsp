<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@page import="com.syc.obrapublica.ObraPublicaContractBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>

<%
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String cancelContr = "No";
	String typeDocument ="";
	ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(
			GestionInterface.ATT_CONEXION);
	if (request.getParameter("cancelContract") != null
			&& request.getParameter("cancelContract").equals("Si")) {
		cancelContr = "Si";
	}
	if (cancelContr.equals("Si")) {
		typeDocument = (String) request.getParameter("typeDocument");
			String[][] r = opbl.CancelContractMultiple(request.getParameter("listaFolios"),request.getParameter("motivo"));
			int numFolios = Integer.parseInt(request.getParameter("numFolios"),10);
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
<script type="text/javascript" src="js/modificacionContratoOP.js"></script>
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<link rel="stylesheet" type="text/css" href="../css/interfaz.css" />
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<script type="text/javascript" src="../js/jsquery.js"></script>


		<script type="text/javascript">
			$(document).ready(function() {
				init();
				$("input.AyudaSyC").subIniciaDlg();
				$("input.autoCompletaSyC").subIniciaAutoCompleta();
			});
		</script>
	</head>
	<body id="dt_example">
		<form action="" method="post" id="mainForm">
			<input type="hidden" value="" id="cEjercicio" name="cEjercicio">
			<input type="hidden" value="" id="cCentroContable"
				name="cCentroContable">
			<input type="hidden" value="" id="folioSAI" name="folioSAI">
			<input type="hidden" value="" id="estatusPago" name="estatusPago">
			<input type="hidden" value="" id="idCaso" name="idCaso">
			<input type="hidden" value="" id="idOper" name="idOper">
			<h1>
				<label id="titulo">
					Modificaci&oacute;n de Contratos
				</label>
			</h1>
			<div id="container" class="container SyCData">
				<h5>
					Ingrese la siguiente informaci&oacute;n:
				</h5>
				<table>
					<tr>
				<td nowrap="nowrap" align="right">Unidad Normativa:</td>
					<td nowrap="nowrap"><input id="hUnidadNormativa" name="hUnidadNormativa" type="text"   value="" size="40" maxlength="50" class="AyudaSyC"/>
					<input id="UnidadNormativa" name="UnidadNormativa" type="hidden"   value="" size="5" maxlength="5" class=""/></td>
					
					<td nowrap="nowrap" align="right">Unidad Ejecutora:</td>
					<td nowrap="nowrap"><input id="hcUnidadEjecutora" name="hcUnidadEjecutora" type="text"   value="" size="40" maxlength="50" class="AyudaSyC"/>
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
					<td><input id="hProgramaPresupuestario" name="hProgramaPresupuestario" type="text"   value="" size="40" maxlength="50" class="AyudaSyC"/>
					<input id="ProgramaPresupuestario" name="ProgramaPresupuestario" type="hidden"   value="" size="5" maxlength="5" class=""/></td>
				
				<td nowrap="nowrap" align="right">Cartera de proyecto:</td>
					<td><input id="hCartera" name="hCartera" type="text"   value="" size="40" maxlength="50" class="AyudaSyC"/>
					<input id="Cartera" name="Cartera" type="hidden"   value="" size="5" maxlength="5" class=""/></td>
				</tr>
				
					<tr>
											<td colspan="1" align="right">
							N&uacute;mero de Contrato:
						</td>
						<td colspan="2" align="left">
							&nbsp;&nbsp;<input type="text" id="cveContrato" size="30">
						</td>
					</tr>
				</table>
				<table width="100%">
					<tr>
					<td colspan="3" align="left">
				<h5>
						Doble Click en el contrato para modificarlo. 
				</h5>
						</td>
						<td colspan="1" align="right">
							<input type="button" id="limpiarButton" value="Limpiar">
						</td>
						<!-- <td colspan="1" align="right">
							<input type="button" id="aceptarButton" value="Aceptar">
						</td> -->
						<td colspan="1" align="right">
							<input type="button" id="searachButton" value="Buscar">
						</td>
					</tr>
				</table>
				<table id="resultTable" width="100%">
					<thead>
						<tr>
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
							<th>
								ID CASO
							</th>
							<th>
								ID OPER
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