<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	String roles="";
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	Map rol =usuario.getRoles();
	String nEstatus=(String)session.getAttribute(GestionInterface.ATT_EstatusContratCap4);
	String nEsAbierto=(String)session.getAttribute(GestionInterface.ATT_ContratCap4Abierto);
	String DATE_FORMAT = "dd/MM/yyyy";
	
 %>
<!DOCTYPE html>
<html>
<head>

<meta charset="UTF-8">
<title>Contratos Remanentes</title>
	<style type="text/css" title="currentStyle"> 
		@import "../../css/custom-theme/jquery-ui-1.8.16.custom.css";
 		@import "../../css/interfaz.css";
	</style>
	<script src="https://code.jquery.com/jquery-3.5.0.js"></script>
	<link rel="stylesheet" type="text/css" href="../../Bootstrap/Bootstrap502/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="../../Bootstrap/Bootstrap-dataTables/datatables.css"/>
	
	<script type="text/javascript" src="../../Bootstrap/Bootstrap502/js/bootstrap.js"></script>
	<script type="text/javascript" src="../../Bootstrap/Bootstrap502/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../../Bootstrap/Bootstrap-dataTables/datatables.js"></script>
	<script type="text/javascript" src="../../Bootstrap/Bootstrap-dataTables/datatables.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
	<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>
	
	<link rel="stylesheet" href="https://code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
	<script src="https://code.jquery.com/ui/1.13.2/jquery-ui.js"></script>
	
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/funciones.js"></script>
 	<script type="text/javascript" src="../js/ContratoAnterior.js"></script>
	<link href="../../css/reportesGRM.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../js/FixedColumns.js"></script>
	<script type="text/javascript" charset="utf-8">
	var tabb=0;	
	$(document).ready(function() {
		$(".tabs").tabs();
		$("#nuevoContratoAnterior").click(function() {
			window.location = "ContratoAnterior.jsp?tab=" + 0;
		});
		$("#consultaContratoMod").click(function() {
			window.location = "ContratoAnterior.jsp?tab=" + 1;
		});
		$("#caratulaContratoMod").click(function() {
			window.location = "ContratoAnterior.jsp?tab=" + 2;
		});
		$("#presupuestoContratoMod").click(function() {
			window.location = "ContratoAnterior.jsp?tab=" + 3;
		});
		
  	});
  	</script>

</head>
<body>
	<div class="container-fluid" >
		<div class="col-md-12 col-lg-12 col-sm-12">
 			<h1 style="color: #1A69A9;">Contratos Remanente Ejercicio Anterior</h1>
 			<div class="tabs" id="tabs">
	 			<ul class="nav nav-tabs">
	 				<li class="nav-item"><a href="#tabs-0" id="nuevoContratoAnterior" class="nav-link" aria-current="page">Nuevo</a></li>
				    <li class="nav-item"><a href="#tabs-1" id="consultaContratoMod" class="nav-link" aria-current="page">Consulta</a></li>
				    <li class="nav-item"><a href="#tabs-2" id="caratulaContratoMod" class="nav-link" aria-current="page">Car&aacute;tula</a></li>
				    <li class="nav-item"><a href="#tabs-3" id="presupuestoContratoMod" class="nav-link" aria-current="page">Presupuesto</a></li>
	 			</ul>
	 			<% if (request.getParameter("tab")!= null){ %>
					<div id="tabs-0" align="left" >
						 <% if (request.getParameter("tab").equals("0")) { %>
							<jsp:include page="ContratoAnteriorNuevo.jsp" />
							<script type="text/javascript" charset="utf-8">
								$( "#tabs" ).tabs({ active: 0 });
							</script>
						<% }  %>
					</div>
					<div id="tabs-1" align="left" >
						 <% if (request.getParameter("tab").equals("1")) { %>
							<jsp:include page="ContratoAnteriorConsulta.jsp" />
							<script type="text/javascript" charset="utf-8">
								$( "#tabs" ).tabs({ active: 1 });
							</script>
						<% }  %>
					</div>
					<div id="tabs-2" align="left" >
						 <% if (request.getParameter("tab").equals("2")) { %>
							<jsp:include page="ContratoAnteriorCaratula.jsp" />
							<script type="text/javascript" charset="utf-8">
								$( "#tabs" ).tabs({ active: 2 });
							</script>
						<% }  %>
					</div>
					<div id="tabs-3" align="left" >
						 <% if (request.getParameter("tab").equals("3")) { %>
							<jsp:include page="ContratoAnteriorPresupuesto.jsp" />
							<script type="text/javascript" charset="utf-8">
								$( "#tabs" ).tabs({ active: 3 });
							</script>
						<% }  %>
					</div>
			<% } %> 
				
	 		</div>
	 	</div>
	 	<input type="hidden" name="tab" id="tab" value="<%= request.getParameter("tab") %>" />
	 </div>
</body>
</html>