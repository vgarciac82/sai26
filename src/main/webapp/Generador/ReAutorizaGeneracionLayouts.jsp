<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String cLogin = "";
	String cUR = "";
	String RFCUsuario = "";
	String numeroEmpleado = "";
	String tipoAutorizacion = StringUtils.trimToEmpty(  request.getParameter("TYPE") );
	
	cLogin = usuario.getLogin();
	cUR = usuario.getU_UR();
	numeroEmpleado = usuario.getNumeroEmpleado();
	RFCUsuario = usuario.getuRFC();

	boolean mntoCuentas = false;
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}

	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
	boolean esSAIAlterno = "true".equals(cabl.getSystemSetting("SAI_AMBIENTAL")) || "true".equals(cabl.getSystemSetting("SAI_FONDEN"));

	String msg = StringUtils.trimToEmpty(request.getParameter("msgError"));
	String result = StringUtils.trimToEmpty((String) session.getAttribute("RESULT"));
	session.removeAttribute("RESULT");
	boolean mostrarResultado = !StringUtils.isBlank(result);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Autorizar Generaci&oacute;n de Layout's</title>

<style type="text/css">
#dt_example .container {
	width: 1024px;
}
</style>

<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>

<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="js/ReAutorizaGeneracionLayouts.js"></script>

<script type="text/javascript" charset="utf-8">
	
	var esSAIAlterno = <%=esSAIAlterno%>;
	var cUR = "<%=cUR%>";
	var cLogin = "<%=cLogin%>";
	var RFC = "<%=RFCUsuario%>";
	var numeroEmpleado = "<%=numeroEmpleado%>";
	var mostrarResultado = <%=mostrarResultado%>;
	var mensaje = "<%=msg%>";
	var tipoAutorizacion = "<%=tipoAutorizacion%>";
	
</script>
</head>
<body id="dt_example">
<br/>
	<div id="container" class="container" style="width: 80%">
		<form>			
			<input id="u_Login" name="u_Login" type="hidden" value="<%=cLogin%>">
			<input id="cUR" name="cUR" type="hidden" value="<%=cUR%>">
			<input id="cWhere" name="cWhere" type="hidden" value=" TipoPago = 'DI'">
			<input id="RFC" name="RFC" type="hidden" value="">
			
		<div class="card-header"> <h3> <label id="tituloOperacion"></label> </h3> </div>
			<div class="mt-4 row d-flex justify-content">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="uEjecutora" class="form-label"> Unidad Ejecutora: </label>
					</div>
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
					<select id="uEjecutora" name="uEjecutora" class="form-select form-select-sm" onchange="cargaGrid();"></select>
				</div>				
			</div>
				
			<br/>
			
			<h5>Tipo de Pago</h5>
			<hr class="mt-3">
			
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="form-check form-check-inline">
							<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="DI" onclick="cargaGrid();" checked/>
							<label for="reporte1" class="form-check-label">Pago Directo</label>
						</div>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="form-check form-check-inline">
							<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="DV" onclick="cargaGrid();"/>
							<label for="reporte1" class="form-check-label">Pago Diverso</label>
						</div>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="form-check form-check-inline">
							<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="FE" onclick="cargaGrid();"/>
							<label for="reporte1" class="form-check-label">Pago Federalizado</label>
						</div>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="form-check form-check-inline">
							<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="RG" onclick="cargaGrid();"/>
							<label for="reporte1" class="form-check-label">Relación Gastos</label>
						</div>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="form-check form-check-inline">
							<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="PO" onclick="cargaGrid();"/>
							<label for="reporte1" class="form-check-label">Pago de Obra</label>
						</div>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="form-check form-check-inline">
							<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="IF" onclick="cargaGrid();"/>
							<label for="reporte1" class="form-check-label">Ingreso Fiscal</label>
						</div>
					</div>
				</div>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">		
						<div class="form-check form-check-inline">			
							<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="PC" onclick="cargaGrid();"/>
							<label for="reporte1" class="form-check-label">Penas Convencionales</label>
						</div>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="form-check form-check-inline">
							<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="CA" onclick="cargaGrid();"/>
							<label for="reporte1" class="form-check-label">Caja</label>
						</div>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="form-check form-check-inline">
							<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="CV" onclick="cargaGrid();"/>
							<label for="reporte1" class="form-check-label">Comision Sin Viaticos</label>
						</div>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="form-check form-check-inline">
							<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="PM" onclick="cargaGrid();"/>
							<label for="reporte1" class="form-check-label">P&oacute;liza Manual</label>
						</div>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="form-check form-check-inline">
							<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="OA" onclick="cargaGrid();"/>
							<label for="reporte1" class="form-check-label">Operaciones Ajenas</label>
						</div>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="form-check form-check-inline">
							<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="RC" onclick="cargaGrid();"/>
							<label for="reporte1" class="form-check-label">Reintegros Caja</label>
						</div>
					</div>
				</div>
				
				<!--<td align="left"><input type="radio" id="tipoPago" name="cTipoPago" value="AX" onclick="cargaGrid();"> Sol. de Recursos x Pagar</td>-->
						
				<table id="logTable" style="display: none">
					<tr>
						<td colspan="9" align="left">
						<td><a href="#" onclick="muestraLog();return false;">Ver log</a></td>
					</tr>
				</table>
			
			<br/>
			
			<div id="pagos" class="table-responsive">
				<table id="dt_AutorizarLayouts" class="table table-striped">			
					<thead>
						<tr>
							<th><font size="2"></font></th>
							<th><font size="2">UE</font></th>
							<th><font size="2">Folio</font></th>
							<th><font size="2">CXP</font></th>
							<!-- <th><font size="2">RFC</font></th>  -->
							<th><font size="2">Nombre</font></th>
							<th><font size="2">Importe</font></th>
							<th><font size="2">Cuenta Bancaria</font></th>
							<th><font size="2">Concepto</font></th>
							<th><font size="2">Fecha Captura</font></th>
						</tr>
					</thead>
				</table>
			</div>
			
			<div class="row d-flex justify-content-right">
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<input type="button" id="btn_Autoriza" name="btn_Autoriza" value="Autorizar" onclick="AutorizaLayouts()" class="btn btn-secondary btn-sm"/>
				</div>
			</div>			
		</form>
	</div>
	<div id="dlg-FIEL" class="container" style="width: 80%">
		<form id="autorizaLayouts" name="autorizaLayouts" method="POST" action="../firmaSolicitudPago" enctype="multipart/form-data">
			<input id="folder" name="folder" value="" type="hidden"/>
			<input id="tipoPagoSeleccionado" name="tipoPagoSeleccionado" type="hidden" value="">
			<input id="loginFirma" name="loginFirma" type="hidden" value="<%=cLogin%>">
			<input id="ueFirma" name="ueFirma" type="hidden" value="<%=cUR%>">
			<input id="rfcFirma" name="rfcFirma" type="hidden" value="<%=RFCUsuario%>">
			<input id="tipoAutorizacion" name="tipoAutorizacion" type="hidden" value="<%=tipoAutorizacion%>">
			<input id="nFolios" name="nFolios" type="hidden" value="">
			
			<div class="card-header"> <h6> Ingrese su firma Electronica </h6> </div>
			<hr class="mt-3"/>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">
					<label for="cerFile" class="form-label"> Archivo *.cer </label>
					<input type="file" id="cerFile" name="cerFile" class="form-control form-control-sm dlgFielInpt"/>
				</div>
			</div>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">
					<label for="keyFile" class="form-label"> Archivo *.key </label>
					<input type="file" id="keyFile" name="keyFile" class="form-control form-control-sm dlgFielInpt"/>
				</div>
			</div>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">
					<label for="passwordLlave" class="form-label"> Password </label>
					<input type="password" id="passwordLlave" name="passwordLlave" class="form-control form-control-sm"/>
				</div>
			</div>	
			
		</form>
	</div>

	<div id="dlg-Msg">
		<fieldset>
			<legend>Resultado de la operacion</legend>
			<table id="mnLogTbl" align="center" border="1">
				<thead>
					<tr>
						<th>Log</th>
					</tr>
				</thead>
				<tbody>
					<%=result%>
				</tbody>
			</table>
		</fieldset>
	</div>
	

</body>
</html>