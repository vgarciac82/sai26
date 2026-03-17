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

ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
boolean esSAIAlterno = "true".equals(cabl.getSystemSetting("SAI_AMBIENTAL")) || "true".equals(cabl.getSystemSetting("SAI_FONDEN"));

String msg = StringUtils.trimToEmpty(request.getParameter("msgError"));
String result = StringUtils.trimToEmpty((String) session.getAttribute("RESULT"));
session.removeAttribute("RESULT");
boolean mostrarResultado = !StringUtils.isBlank(result);

%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Comisiones de Viáticos</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"	href="css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>

</head>

<body id="dt_example" style= "width: 90%">
	<div>
		<form id="formAutViaticos" name="formAutViaticos" >
			<input id="u_Login" name="u_Login" type="hidden" value="<%=cLogin%>">
			<input id="cUR" name="cUR" type="hidden" value="<%=cUR%>">
			<input id="nEmpleadoUsuario" name="nEmpleadoUsuario" type="hidden" value="<%=numeroEmpleado%>">
			<input id="cWhere" name="cWhere" type="hidden" value=" TipoPago = 'DI'">
			<input id="RFCUsuario" name="RFCUsuario" type="hidden" value="">
			<input id="idEmpleado" name="idEmpleado" type="hidden" value="">
			<input id="nFolios" name="nFolios" type="hidden" value="">
			<input id="idComision" name="idComision" type="hidden" value="">
			<input type="hidden" name="operacion" 	id="operacion" 		value=""/>
			<input type="hidden" name="idComision"	id="idComision" 	value=""/>
			<input type="hidden" name="tienePagos"	id="tienePagos" 	value=""/>
			<input type="hidden" name="saldoPagos"	id="saldoPagos" 	value="0"/>
			<input type="hidden" id="jBoleto" name="jBoleto"/>
			<input type="hidden" id="hasTicket" name="hasTicket" value = 0/>
			<input type="hidden" id="cTieneBoleto" name="cTieneBoleto" value = 0/>
		<div class="alert alert-danger" role="alert">
		  <b>Una o varias agendas solicitan compra de Boleto de avión. Favor de revisar.</b><br> Al terminar el proceso de Autorización se enviará correo electrónico a la Agencia para la cotización del boleto.
		</div>
			<div class="row mt-2">
				<div class="card-header"><h3>Autorización de comisiones</h3> </div>
			</div>
			<div class="row mt-2">
				<div class="col-md-8">
					Unidad Ejecutora: 
						<select id="uEjecutora" name="uEjecutora" onchange="cargaGrid();" class="form-select">
						</select> 
				</div>
			</div>
			<div class="row">
				<table id="logTable" style="display: none">
						<tr>
							<td colspan="9" align="left">
							<td><a href="#" onclick="muestraLog();return false;">Ver log</a></td>
						</tr>
				</table>
			</div>
			
			<div class="row">
				<div class="col-md-10">
				</div>
				<div class="col-md-1">
					<input type="button" id="btn_Autoriza" name="btn_Autoriza" value="Autorizar" onclick="AutorizaComisiones()" class="btn btn-primary"/>
				</div>
				<div class="col-md-1">
					<input type="button" id="btn_Rechazar" name="btn_Rechazar" value="Rechazar" onclick="rechazoComisiones()" class="btn btn-secondary"/>
				</div>
			</div>
			<div class="row">	
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="checkbox" id="checkAll" name="checkAll" class="form-check-input" value=""> Todos
					</div>
			</div>
			<div class="row table-responsive">
				<p><b>Doble click en el renglón para consultar el detalle de la Agenda</b></p>
				<table id="dt_AutorizarLayouts" class="table table-striped">
					<thead>
						<tr>
							<th></th>
							<th>UE</th>
							<th>Folio</th>
							<th>RFC</th>  
							<th style="width:25%">Nombre Completo</th>
							<th>Importe Viáticos</th>
							<th>Importe Transporte</th>
							<th style="width:20%">Nombre Comisión</th>
							<th>Dias</th>
							<th style="width:25%">Notas</th>
							<th>Boleto Avión</th>
						</tr>
					</thead>
				</table>
			</div>
		
		</form>
	</div>
	
<div class="modal hide fade in" tabindex="-1" role="dialog" id="dialog-Detalle" data-mdb-keyboard="false" data-mdb-backdrop="static" aria-labelledby="staticBackdropLabel" aria-hidden="true">
 	<div class="modal-dialog modal-xl" role="document">
    	<div class="modal-content">
		    <div class="modal-header">
		        <h6 class="modal-title">Mostrar los datos de la comision</h6>
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		    </div>
      		<div class="modal-body">
				<jsp:include page="ConsultaComisionViaticos.jsp"></jsp:include>
			</div>
		 	<div class="modal-footer">
		 		<button type="button" class="btn btn-dark" style="position: absolute; left: 30px;"  onclick="cancelar();">Cancelar trámite</button>
        		<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
      		</div>
    	</div>
  	</div>
</div>


		<div id="dlg-Msg">
			<div class="row">
				<p>Resultado de la operacion</p>
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
			</div>
		</div>
	
		<script type="text/javascript" src="../Generador/js/jquery-3.5.1.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="../Generador/js/Moment.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui.min.js"></script>
		 <script type="text/javascript" src="../Generador/js/ComisionViaticos.js"></script> 
		<script type="text/javascript" src="../Generador/js/AutorizaComisionViaticos.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="../Generador/js/validaciones.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
		
		
</body>
	<script type="text/javascript" charset="utf-8">
		var esSAIAlterno = <%=esSAIAlterno%>;
		var cUR = "<%=cUR%>";
		var cLogin = "<%=cLogin%>";
		var RFCUsuario = "<%=RFCUsuario%>";
		var numeroEmpleado = "<%=numeroEmpleado%>";
		var mostrarResultado = <%=mostrarResultado%>;
		var mensaje = "<%=msg%>";

	</script>
</html>
