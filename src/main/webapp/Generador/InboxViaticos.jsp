<%@page import="com.syc.gestion.core.Caso"%>
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

Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
if(c != null)
	session.removeAttribute( GestionInterface.ATT_CASE );
	
String cLogin = "";
String cUR = "";
String RFCUsuario = "";
String numeroEmpleado = "";
String tipoAutorizacion = StringUtils.trimToEmpty(  request.getParameter("TYPE") );

cLogin = usuario.getLogin();
cUR = usuario.getU_UR();
numeroEmpleado = usuario.getNumeroEmpleado();
RFCUsuario = usuario.getuRFC();

%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Inbox Viáticos</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"	href="css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/bootstrap.min.css"></link>
<link rel="stylesheet" href="https://cdn.datatables.net/1.13.8/css/jquery.dataTables.min.css">
<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>
</head>
<body id="dt_example" style= "width: 95%">
	<div>
		<form id="inboxViaticos" name="inboxViaticos" >
			<input type="hidden" id="u_Login" 		name="u_Login" 		value="<%=cLogin%>"/>
			<input type="hidden" id="cUR" 			name="cUR" 			value="<%=cUR%>"/>
			<input type="hidden" id="nEmpleadoUsuario" name="nEmpleadoUsuario" value="<%=numeroEmpleado%>"/>
			<input type="hidden" id="cWhere" 		name="cWhere" 		value=" TipoPago = 'DI'"/>
			<input type="hidden" id="RFCUsuario" 	name="RFCUsuario" 	value=""/>
			<input type="hidden" id="idEmpleado" 	name="idEmpleado" 	value=""/>
			<input type="hidden" id="nFolios" 		name="nFolios" 		value=""/>
			<input type="hidden" id="idComision" 	name="idComision" 	value=""/>
			<input type="hidden" id="existenPagos" 	name="existenPagos" value=""/>
			<input type="hidden" id="folioCaso" 	name="folioCaso" 	value=""/>
			<input type="hidden" id="IDproyecto" 	name="IDproyecto" 	value=""/>
			<input type="hidden" id="nivel1" 		name="nivel1" 		value=""/>
			<input type="hidden" id="saldoPagos" 	name="saldoPagos" 	value=""/>
			<input type="hidden" id="cDocHaplicado" name="cDocHaplicado" value=""/>
			<input type="hidden" id="tieneRolPagos" name="tieneRolPagos" value=""/>
			<input type="hidden" id="tieneComSinViat" name="tieneComSinViat" value=""/>
			<input type="hidden" name="cTipoRfc" id="cTipoRfc" value=""/>
			<input type="hidden" name="cPlaza" id="cPlaza" value=""/>
			<input type="hidden" name="cNivel" id="cNivel" value=""/>
			<input type="hidden" name="cRFCEmpleado" id="cRFCEmpleado" value=""/>
			
				
			<div class="row mt-2">
				<div class="card-header"> <h3> Inbox viáticos </h3> </div>
			</div>
			<div class="card mt-2">
				<div class="card-body">
					<div class="row">
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							<label for="nFolio" class="form-label">Folio:</label>
							<div class="input-group">								
								<span class="input-group-text"><i class="bi bi-123"></i></span>
								<input type="text" id="nFolio" name="nFolio" class="form-control form-control-sm" onkeyup="bloqueaEmpleado()" placeholder="Busqueda por comisión"/>
							</div>
						</div>
		
						<div class="col-12 col-lg-2 col-md-2 col-sm-12" id="divEmp">
							<label for="nidEmpleado" class="form-label">Empleado:</label>
							<div class="input-group">								
								<span class="input-group-text"><i class="bi bi-person"></i></span>
								<input type="text" id="nidEmpleado" name="nidEmpleado" class="form-control form-control-sm" onkeyup="bloqueaFolio()" placeholder="Num. Empleado" onChange="cambiaNumEmp()"/>
								<input type="button" class="btn btn-secondary" name="btnBeneficiario" id="btnBeneficiario" value="..." size="5" onclick="catalogo_beneficiario();" />
								
							</div>	
						</div>
						<div class="col-12 col-lg-4 col-md-4 col-sm-12">
							<label for="cnombre" class="form-label">Nombre:</label>
							<input type="text" id="cnombre" name="cnombre" class="form-control" readonly/>
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12">	
							<br>				
							<input type="button" id="btnBuscar" name="btnBuscar" value="Buscar" onclick="buscar();" class="btn btn-secondary" />					
							<input type="button" id="btnLimpiar" name="btnLimpiar" value="Limpiar Busqueda" onclick="limpiaCampos();" class="btn btn-dark" />
						</div>
					</div>
				</div>
			</div>
			<div class="row mt-2">
				<div class="col-md-8">
					<label for="uEjecutora" class="form-label">Unidad Ejecutora:</label> 
					<select id="uEjecutora" name="uEjecutora" onchange="cargaGrid();" class="form-select">
					</select> 
				</div>
			</div>
			<br/>
			<div class="row mt-2">
				<div class="col-md-3">					
					<input type="checkbox" name="finalizados" id="finalizados" class="form-check-input" value = "F" onclick="filtroStatus()" />
					<label for="finalizados" class="form-check-label">Muestra Finalizados</label>
				</div>
				<div class="col-md-3">					
					<input type="checkbox" name="rechazados" id="rechazados" class="form-check-input" value = "C" onclick="filtroStatus()" />
					<label for="rechazados" class="form-check-label">Muestra Rechazados</label>
				</div>
			</div>
			
			<div class="row table-responsive text-nowrap">
				<table id="dt_listaViaticos" class="table table-striped table-bordered">
					<thead>
						<tr class="encabezado">
							<th><font size="2">Folio</font></th>
							<th><font size="2">NoEmpleado</font></th>
							<th><font size="2">UR</font></th>
							<th><font size="2">Nombre</font></th>
							<th><font size="2">Fecha Inicio</font></th>  
							<th><font size="2">Fecha Fin</font></th>
							<th><font size="2">Estatus</font></th>
							<th><font size="2">Origen</font></th>
							<th><font size="2">Seguimiento</font></th>
						</tr>
					</thead>
					<tbody>
					</tbody>
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
      		<div class="modal-body" id="modalBodyDetalle">
			</div>
		 	<div class="modal-footer">
		 		<button type="button" class="btn btn-dark" style="position: absolute; left: 30px;" id="btnCancelaComision" onclick="cancelar();">Cancelar trámite</button>
        		<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
      		</div>
    	</div>
  	</div>
</div>

<div class="modal hide fade in" tabindex="-1" role="dialog" id="dialog-Editar" data-mdb-keyboard="false" data-mdb-backdrop="static" aria-labelledby="staticBackdropLabel" aria-hidden="true">
 	<div class="modal-dialog modal-xl" role="document">
    	<div class="modal-content">
      		<div class="modal-body">
				<jsp:include page="EdicionViaticos.jsp"></jsp:include>
			</div>
		 	<div class="modal-footer">
        		<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
      		</div>
    	</div>
  	</div>
</div>

<div class="modal fade" tabindex="-1" role="dialog" id="dialog-capturaPagos" data-mdb-keyboard="false" data-mdb-backdrop="static" aria-labelledby="staticBackdropLabel" aria-hidden="true">
  	<div class="modal-dialog modal-fullscreen" role="document">
    	<div class="modal-content">
	      	<div class="modal-header">
		        <h5 class="modal-title">Captura/Consulta Pagos</h5>
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	      	</div>
    		<div class="modal-body">
      			<div id="uploadPagos" name="uploadPagos" >
					<iframe id="uploadPagosFrm" height="650" width="100%">
					</iframe>
				</div>
       		</div>
	     	<div class="modal-footer">
	        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
	    	</div>
    	</div>
  	</div>
</div>

		<script type="text/javascript" src="../Generador/js/jquery-3.5.1.min.js"></script>
		<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="../Generador/js/Moment.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui.min.js"></script>
		<script type="text/javascript" src="../Generador/js/inboxViaticos.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>	
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>	
		<script type="text/javascript" src="../Generador/js/ComisionViaticos.js"></script>
		<script type="text/javascript" src="js/EdicionViaticos.js"></script>
		
</body>
	<script type="text/javascript" charset="utf-8">		
		var cUR = "<%=cUR%>";
		var cLogin = "<%=cLogin%>";
		var RFCUsuario = "<%=RFCUsuario%>";
		var numeroEmpleado = "<%=numeroEmpleado%>";

	</script>
</html>
