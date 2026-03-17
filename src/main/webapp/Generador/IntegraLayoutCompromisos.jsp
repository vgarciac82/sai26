<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.io.File"%>
<%@page import="javax.swing.text.MaskFormatter"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.syc.contable.CancelaDocumento"%>
<%
String cCentroContable = "";
Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
if (usuario == null) {
	response.sendRedirect("../index.jsp");
	return;
}

String resultadoCancelacion = "";
String mensaje = "";
Connection conn = null;

int nFolioDocumento;
String cTipoDocumento;

if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
	cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
}

String cUE_Usuario = "";
cUE_Usuario = usuario.getU_UR();
String U_LOGIN = "";
U_LOGIN = usuario.getLogin();
%>
<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8">
<title>Operaciones Layout de Compromiso</title>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>

</head>
<body>
	<br/>
	<div id="container" class="ms-5" style="width: 90%" class="container">
		<form id="frmIntegraLayoutComp">
			<input type="hidden" id="cCC" name="cCC" value="<%= cCentroContable%>" />
			<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
	    	<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= cUE_Usuario%>" />
	    	<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
	    	<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%= U_LOGIN%>"/>
	    	<input type="hidden" name="cUR" id="cUR"  value=""/>
	    	<input type="hidden" name="cMovto" id="cMovto"  value=""/>
		</form>
		<div class="card-header"> <h3> Genera Layouts de Compromisos </h3> </div>
			<div class="mt-3 row pb-3" align="center">
				<div class="col-3">
					<fieldset class="form-group px-5 ">			 	
						<label class="form-label">Tipo Movimiento Compromisos </label>
						<hr class="mt-1">
						<div class="form-check form-check-inline">
						  <input class="form-check-input" type="radio" id="rdoOriginal" >
						  <label class="form-check-label" for="rdoOriginal">Original</label>
						</div>
						<div class="form-check form-check-inline">
						  <input class="form-check-input" type="radio" id="rdoModificado" >
						  <label class="form-check-label" for="rdoModificado">Modificado</label>
						</div>
					</fieldset>
				</div>
				<div class="col-sm-2 col-md-4  col-lg-6" align="left">	
					<label for = "cboUnidadEjecutora">Unidad Ejecutora: </label>
					<select class="form-select" name='cboUnidadEjecutora' id='cboUnidadEjecutora' onchange="buscaComprommisosMovto();">
		    				<option value = "*" >TODAS LAS UNIDADES</option>
		    		</select>
				</div>
			</div>
			<ul class="nav nav-tabs" id="layout-list" role="tablist">
				  <li class="nav-item" role="presentation">
				    <button class="nav-link active" id="xGenerar-tab" onClick="llenaCompromisos();" data-bs-toggle="tab" data-bs-target="#xGenerar" type="button" role="tab" aria-controls="xGenerar-tab" aria-selected="true">Por Generar</button>
				  </li>
				  <li class="nav-item" role="presentation">
				    <button class="nav-link" id="enviados-tab" onClick="enviar();" data-bs-toggle="tab" data-bs-target="#enviados" type="button" role="tab" aria-controls="enviados-tab" aria-selected="false">Proceso de Env&iacute;o</button>
				  </li>
				  <li class="nav-item" role="presentation">
				    <button class="nav-link" id="compromiso-tab" onClick="checkSeleccionaTodos();compromisosEnviadosSICOP();" data-bs-toggle="tab" data-bs-target="#compromiso" type="button" role="tab" aria-controls="compromiso-tab" aria-selected="false">Enviados</button>
				  </li>
				   <li class="nav-item" role="presentation">
				   	<button class="nav-link" id="autorizados-tab" data-bs-toggle="tab" data-bs-target="#autorizado" type="button" role="tab" aria-controls="autorizados-tab" aria-selected="false">Autorizados</button>
				  </li>
				  <li class="nav-item" role="presentation">
				   	<button class="nav-link" id="rechazados-tab"  data-bs-toggle="tab" data-bs-target="#rechazados" type="button" role="tab" aria-controls="rechazados-tab" aria-selected="false">Rechazados</button>
				  </li>
				  <li class="nav-item" role="presentation">
				    <button class="nav-link" id="incompletos-tab" onClick="compromisosIncompletos();" data-bs-toggle="tab" data-bs-target="#incompletos" type="button" role="tab" aria-controls="incompletos-tab" aria-selected="false">Incompletos</button>
				  </li>
			  
			</ul>
			<div class="tab-content" id="myTabContent">
			  <div class="tab-pane fade show active" id="xGenerar" role="tabpanel" aria-labelledby="xGenerar-tab">
				
				<jsp:include page="listaCompromisosCreados.jsp"></jsp:include>
				
			  </div>
			  <div class="tab-pane fade" id="enviados" role="tabpanel" aria-labelledby="enviados-tab">
			  		<fieldset>
						<legend> Integraci&oacute;n del Layout para ser Enviado a SICOP </legend>
						<jsp:include page="listaCompromisosEnviados.jsp"></jsp:include>
					</fieldset>
			  </div>
			  <div class="tab-pane fade" id="compromiso" role="tabpanel" aria-labelledby="compromiso-tab">
			  	
			  		<jsp:include page="listaCompromisos.jsp"></jsp:include>
			  	
			  </div>
			  <div id="autorizado" class="tab-pane fade" role="tabpanel" aria-labelledby="autorizados-tab">
						
					<jsp:include page="listaCompromisosDevueltos.jsp"></jsp:include>
				</div>
				<div class="tab-pane fade" id="rechazados" role="tabpanel" aria-labelledby="rechazados-tab">
					<fieldset>
						<legend> Rechazados SICOP </legend>
						<jsp:include page="listaCompromisosRechazados.jsp"></jsp:include>
					</fieldset>
				</div>
				<div class="tab-pane fade" id="incompletos" role="tabpanel" aria-labelledby="incompletos-tab">

						<jsp:include page="listaCompromisosIncompletos.jsp"></jsp:include>
					
				</div>
			</div>
	
		
	</div>
	<script type="text/javascript" src="js/jquery-3.5.1.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="js/bootstrap.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui.min.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="js/jquery.blockUI-2.70.0.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="js/LayoutCompromiso.js"></script>

	
	</body>
</html>
