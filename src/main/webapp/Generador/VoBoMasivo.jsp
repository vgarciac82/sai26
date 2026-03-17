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
	String tblLegend = "Cargas Masivas Pendientes de " + ("VOBO".equalsIgnoreCase( tipoAutorizacion )?" Visto Bueno": "Autorizacion");
	
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
<title>Seleccion de Visto Bueno</title>

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
<script type="text/javascript" src="js/VistoBuenoMasivo.js"></script>

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
			<input id="u_Login" name="u_Login" type="hidden" value="<%=cLogin%>"/>
			<input id="cUR" name="cUR" type="hidden" value="<%=cUR%>"/>
			<input id="uEjecutora" name="uEjecutora" type="hidden" value="<%=cUR%>"/>
			<input id="cWhere" name="cWhere" type="hidden" value=" TipoPago = 'DI'"/>
			<input id="RFC" name="RFC" type="hidden" value=""/>
			<input id="nFolioCargaMasiva" name="nFolioCargaMasiva" type="hidden" value="-1"/>
			
		<div id="encaDiv">
			<div class="card-header"> <h3> <label id="tituloOperacion"></label> </h3> </div>
				<div class="mt-4 row d-flex justify-content">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					</div>
				</div>
				
				<h5>Tipo de Pago</h5>
				<hr class="mt-3">		
				
				<div class="row d-flex justify-content">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="form-check form-check-inline">
							<input type="radio" name="cTipoPago" id="tipoPago" class="form-check-input" value="RG" onclick="cargaGrid();" checked/>
							<label for="reporte1" class="form-check-label">Relaci&oacute;n de Gastos</label>
						</div>
					</div>
				</div>
			
			</div>
			
			<br/>
			
			<div class="display" class="container">
				<div class="card-header"> <h5><%=tblLegend%></h5> </div>
					<div id="pagos" class="table-responsive">
						<table id="dtCargasMasivas" class="table table-striped">				
							<thead>
								<tr>
									<th>Folio Carga</th>
									<th>Fecha Aplicacion</th>
									<th>Fecha de Carga</th>
									<th>Unidad Ejecutora</th>
									<th>Importe Total</th>
									<th>Relaciones de Gastos</th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
					</div>
				
			</div>
			
			<br/>
			
			<div class="display" class="container">
				<div class="card-header"> <h5>Detalle de Carga Masiva</h5> </div>	
					<div id="pagos" class="table-responsive">
						<table id="dtDetalleCarga" class="table table-striped">				
							<thead>
								<tr>
									<th>ID Relaci&oacute;n</th>
									<th>RFC</th>
									<th>Nombre</th>
									<th>Concepto</th>
									<th>Importe Total</th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
					</div>
			</div>
			
			<br/>
			
			<div class="row d-flex justify-content-right">
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<input type="button" id="btn_Autoriza" name="btn_Autoriza" value="Autorizar" onclick="enviaParaFirma()" class="btn btn-secondary btn-sm"/>
				</div>
			</div>
			
		</form>
	</div>
</body>
</html>