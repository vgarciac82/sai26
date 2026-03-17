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
	
	String fielMsg = null;
	boolean fielExpiringSoon = session.getAttribute("EXPIRING_SOON") != null && ((Boolean)session.getAttribute("EXPIRING_SOON")); 
	if(fielExpiringSoon){
		fielMsg = (String) session.getAttribute("EXPIRING_MSG");
		session.removeAttribute("EXPIRING_SOON");
		session.removeAttribute("EXPIRING_MSG");
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Listado de Reportes pendientes de firma</title>

<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>

<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.core.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="js/ListadoReportesPendientes.js"></script>

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
			<div class="card-header"> <h3> <label id="tituloOperacion">&nbsp;</label> </h3> </div>
			
			<input id="u_Login" name="u_Login" type="hidden" value="<%=cLogin%>">
			<input id="cUR" name="cUR" type="hidden" value="<%=cUR%>">
			<input id="cWhere" name="cWhere" type="hidden" value=" TipoPago = 'DI'">
			<input id="RFC" name="RFC" type="hidden" value="">
			<input type="hidden" name="cTipoPago" id="cTipoPago" value="REPORTE" />
						
			<div class="container">
				<table id="logTable" style="display: none">
					<tr>
						<td colspan="9" align="left">
						<td><a href="#" onclick="muestraLog();return false;">Ver log</a></td>
					</tr>
				</table>
			</div>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
					<span class="label">Doble click para ver el detalle o firmar un documento.</span>
					<span class="label">Si desea firmar mas de un documento, seleccione de la tabla y clic en el boton autorizar.</span>
				</div>
			</div>
			<div class="row col-2 pt-3">
				<div class="form-check mx-3">
				  <input class="form-check-input" type="checkbox" id="chkTodos" name="chkTodos" value="" >
				  <label class="form-check-label" for="flexCheckDefault">
				    Seleccionar Todos
				  </label>
				</div>
				
			</div>
			<div id="pagos" class="table-responsive">
				<table id="tblResumen" class="table table-striped">					
					<thead>
						<tr align="center">
							<th>&nbsp;</th>
							<th>Folio</th>
							<th>Reporte</th>
							<th>Mes</th>
							<th>Moneda</th>
							<th>Nivel</th>
							<th style="display: none">&nbsp;</th>
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
	

<div>
	<form id="firmaReportes" name="firmaReportes" method="POST" action="../firma/AutorizaReporte" enctype="multipart/form-data">
			<input type="hidden" name="tipoPagoSeleccionado" id="tipoPagoSeleccionado" value="REPORTE" />
			<input id="loginFirma" name="loginFirma" type="hidden" value="<%=cLogin%>">
			<input id="ueFirma" name="ueFirma" type="hidden" value="<%=cUR%>">
			<input id="rfcFirma" name="rfcFirma" type="hidden" value="<%=RFCUsuario%>">
			<input id="tipoAutorizacion" name="tipoAutorizacion" type="hidden" value="<%=tipoAutorizacion%>">
			<input id="nFolios" name="nFolios" type="hidden" value="">
			<input type="hidden" name="ordenes" id="ordenes" value="" />
			<input id="urlRetorno" name="urlRetorno" type="hidden" value="../FIEL/ListadoReportesPendientes.jsp">

			<div class="modal" tabindex="-1" role="dialog" id="dlg-FIEL" data-mdb-keyboard="true" data-mdb-backdrop="static">
			  <div class="modal-dialog" role="document">
			    <div class="modal-content">
			      <div class="modal-header">
			        <h5 class="modal-title">Ingrese su firma Electronica</h5>
			        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			      </div>
			      <div class="modal-body">
						<div class="row d-flex">
							<div class="col-12 col-lg-10 col-md-10 col-sm-12">
								<label for="cerFile" class="form-label"> Archivo *.cer </label>
								<input type="file" id="cerFile" name="cerFile" class="form-control form-control-sm dlgFielInpt"/>
							</div>
						</div>
						<div class="row d-flex">
							<div class="col-12 col-lg-10 col-md-10 col-sm-12">
								<label for="keyFile" class="form-label"> Archivo *.key </label>
								<input type="file" id="keyFile" name="keyFile" class="form-control form-control-sm dlgFielInpt"/>
							</div>
						</div>
			
						<div class="row d-flex">
							<div class="col-12 col-lg-5 col-md-5 col-sm-12">
								<label for="passwordLlave" class="form-label"> Password </label>
								<input type="password" id="passwordLlave" name="passwordLlave" class="form-control form-control-sm"/>
							</div>
						</div>	
					</div>		
					 <div class="modal-footer">
				        <button type="button" id="btnAceptarCorreo" onclick="aceptarDlg();" class="btn btn-primary">Aceptar</button>
				        <button type="button" class="btn btn-secondary" onclick="cancelarDlg();" data-bs-dismiss="modal">Cerrar</button>
			      </div>
			    </div>
			  </div>
			</div>
		</form>
</div>

	<div id="dlg-Msg" class="container" title="Resultado de la operacion">
		<div class="card-header"> <h6> Log </h6> </div>
		<hr class="mt-3"/>
		
		<div class="row d-flex justify-content-center p-1">
			<%if( fielMsg != null){ %>
				<div id="fielWarning" class="col-12 col-lg-12 col-md-12 col-sm-12">
					<fieldset>
						<legend>Firma a punto de Expirar</legend>
						<div><p id="msgWarning" style="font-weight: bold;"><%=fielMsg%></p></div>
					</fieldset>
				</div>
				<%} %>
			<div class="col-12 col-lg-12 col-md-12 col-sm-12">
				<table id="mnLogTbl">
					<thead>
						<tr>						
						</tr>
					</thead>
					<tbody>
						<%=result%>
					</tbody>
				</table>
			</div>
		</div>
	</div>

</body>
</html>