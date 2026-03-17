<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	Usuario usuario = ( Usuario ) session.getAttribute( GestionInterface.ATT_USER );

	if ( usuario == null ) {
		response.sendRedirect( "../index.jsp" );
		return;
	}
	
	String msg = StringUtils.trimToEmpty( (String)session.getAttribute("MSG") );
	session.removeAttribute("MSG");
	
%>
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="js/pendientesTiendaDigital.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		init();
	});
</script>

<title>Pagos por Tienda Digital.</title>
</head>
<br/>
<body id="dt_example" >
	<form>
		<input type="hidden" id="nEnviadoSICOP" name="nEnviadoSICOP">
		<input type="hidden" id="nFolioPagoDiverso" name="nFolioPagoDiverso">
		
		<div id="container" class="container" style="width: 100%" >
			<div class="card-header"> <h3> Pendientes de Tienda Digital </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row d-flex">						
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
					<p class="text-info">Seleccione el pago dando doble clic para actualizar su informaci&oacute;n</p>					
				</div>
			</div>
			
			<div class="row d-flex">						
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
					<table id="pagosPendientesTbl"  class="table table-striped">
						<thead>
							<tr>
								<th>Folio</th>
								<th>Cuenta por Pagar</th>
								<th>Beneficiario</th>
								<th>Concepto</th>
								<th>Monto</th>
								<th>Estatus</th>
								<th>Gabinete</th>
							</tr>
						</thead>
					</table>				
				</div>			
			</div>
		</div>
	</form>
	
</body>

</html>