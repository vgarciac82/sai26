<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String role="";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	Map rol =usuario.getRoles();
	String name_user=usuario.getLogin();
	if (session.getAttribute(GestionInterface.ATT_ReqEjercicio) != null ) {
	    session.setAttribute(GestionInterface.ATT_ReqEjercicio, null);
		session.setAttribute(GestionInterface.ATT_ReqTipoSolicitud, null);
		session.setAttribute(GestionInterface.ATT_ReqUnidadEjec, null);
		session.setAttribute(GestionInterface.ATT_ReqConsecutivo, null);
	}
	if (session.getAttribute(GestionInterface.ATT_ContratoEjercicio) != null) { 
		session.setAttribute(GestionInterface.ATT_ContratoEjercicio, null);
		session.setAttribute(GestionInterface.ATT_ContratoTipoContrato, null);
		session.setAttribute(GestionInterface.ATT_ContratoUnidadEjec, null);
		session.setAttribute(GestionInterface.ATT_ContratoConsecutivo,null);
	}
	Boolean OnSession=false;
	if (request.getParameter("cDefinitivo") != null) { 
		session.setAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo, request.getParameter("cDefinitivo").toString());
		session.setAttribute(GestionInterface.ATT_ContratoModificatorioConsecutivo, request.getParameter("mod").toString());
		session.setAttribute(GestionInterface.ATT_ContratoModificatorioId, request.getParameter("cContrato").toString());
		session.setAttribute(GestionInterface.ATT_ContratoModificatorioEjercicio, request.getParameter("cEjercicio").toString());
		session.setAttribute(GestionInterface.ATT_ContratoIsModificatorioEjercicioAnt, request.getParameter("isConvEjercicioAnt").toString());
	}
	
	if ((String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo) != null){
		OnSession = true;
	}
	
%>
<!doctype html>
<html lang="en">
<head>
  
  <title>Contratos Modificatorios</title>
	<meta charset="utf-8">
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
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
	
	<link rel="stylesheet" href="//code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
	<script src="https://code.jquery.com/ui/1.13.2/jquery-ui.js"></script>
	
	
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/funciones.js"></script>
	<link href="../../css/reportesGRM.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="../../Generador/js/Functions_ContratoModificatorio.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../js/FixedColumns.js"></script>
  <script type="text/javascript" charset="utf-8">
  var tabb=0;
  $(document).ready(function() {
		<%
			int tabla=0;
			NegativaPestana NegPestana = new NegativaPestana();
			NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
			Iterator it1 = rol.entrySet().iterator();
			while (it1.hasNext()) {
				Map.Entry r = (Map.Entry)it1.next();
				role += r.getKey().toString()+",";
			}
			if(role.length()>0){
					role = role.substring(0,role.length()-1);
			}
			Map pestanas = ebl.getPestana(role,"ContratoModificatorio");
			Iterator it = pestanas.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry e = (Map.Entry)it.next();%>
				$( "#<%=e.getValue()%>" ).attr("disabled", true);
				
		<% } %>
		 	
			$("#tabs").tabs();
			$("#nuevoContratoMod").click(function() {
				window.location = "ContratoModificatorio.jsp?tab=" + 0;
			});
			$("#consultaContratoMod").click(function() {
				window.location = "ContratoModificatorio.jsp?tab=" + 1;
			});
			$("#caratulaContratoMod").click(function() {
				window.location = "ContratoModificatorio.jsp?tab=" + 2;
			});
			$("#presupuestoContratoMod").click(function() {
				window.location = "ContratoModificatorio.jsp?tab=" + 3;
			});
			$("#precompromisoContratoMod").click(function() {
				window.location = "ContratoModificatorio.jsp?tab=" + 4;
			});
			$("#compromisoContratoMod").click(function() {
				window.location = "ContratoModificatorio.jsp?tab=" + 5;
			});
			$("#pagosContratoMod").click(function() {
				window.location = "ContratoModificatorio.jsp?tab=" + 6;
			});
			
			$("#imprimeConvenio").click(function() {
				window.location = "ContratoModificatorio.jsp?tab=" + 7;
			});
		});//fin document ready


  </script>
</head>
<body>
	<form action="">	
	  	<div class="container-fluid" >
			<div class="col-md-12 col-lg-12 col-sm-12">
	 			<h1 style="color: #1A69A9;">Convenios Modificatorios</h1>
	 			<div class="tabs" id="tabs">
		 			<ul class="nav nav-tabs">
		 				<li class="nav-item"><a href="#tabs-0" id="nuevoContratoMod" class="nav-link" aria-current="page">Nuevo</a></li>
					    <li class="nav-item"><a href="#tabs-1" id="consultaContratoMod" class="nav-link" aria-current="page">Consulta</a></li>
					    <li class="nav-item"><a href="#tabs-2" id="caratulaContratoMod" class="nav-link" aria-current="page">Car&aacute;tula</a></li>
					    <li class="nav-item"><a href="#tabs-3" id="presupuestoContratoMod" class="nav-link" aria-current="page">Presupuesto</a></li>
					    <li class="nav-item"><a href="#tabs-4" id="precompromisoContratoMod" class="nav-link" aria-current="page">PreCompromiso</a></li>
					    <li class="nav-item"><a href="#tabs-5" id="compromisoContratoMod" class="nav-link" aria-current="page">Compromiso</a></li>
					    <li class="nav-item"><a href="#tabs-6" id="pagosContratoMod" class="nav-link" aria-current="page">Pagos</a></li>
					    <li class="nav-item"><a href="#tabs-7" id="imprimeConvenio" class="nav-link" aria-current="page">Imprime</a></li>
		 			</ul>
		 			<% if (request.getParameter("tab")!= null){ %>
					<div id="tabs-0" align="left">
					 	<% if (request.getParameter("tab").equals("0")) {
					 		OnSession=false; %>
							<jsp:include page="ContratoModificatorioNuevo.jsp" />
							<script type="text/javascript" charset="utf-8">
								$( "#tabs" ).tabs({ active: 0 });
							</script>
						<% } %> 
					</div>
					<div id="tabs-1" align="left">
					 	<% if (request.getParameter("tab").equals("1")) {
					 		OnSession=false; %>
							<jsp:include page="ContratoModificatorioConsulta.jsp" />
							<script type="text/javascript" charset="utf-8">
								$( "#tabs" ).tabs({ active: 1 });
							</script>
						<% } %> 
					</div>
					<div id="tabs-2" align="left">
					 	<% if (request.getParameter("tab").equals("2")) {
					 		OnSession=false; %>
							<jsp:include page="ContratoModificatorioCaratula.jsp" />
							<script type="text/javascript" charset="utf-8">
								$( "#tabs" ).tabs({ active: 2 });
							</script>
						<% } %> 
					</div>
					<div id="tabs-3" align="left">
					 	<% if (request.getParameter("tab").equals("3")) {
					 		OnSession=false; %>
							<jsp:include page="ContratoModificatorioPresupuesto.jsp" />
							<script type="text/javascript" charset="utf-8">
								$( "#tabs" ).tabs({ active: 3 });
							</script>
						<% } %> 
					</div>
					<div id="tabs-4" align="left">
					 	<% if (request.getParameter("tab").equals("4")) {
					 		OnSession=false; %>
							<jsp:include page="ContratoModificatorioPreCompromiso.jsp" />
							<script type="text/javascript" charset="utf-8">
								$( "#tabs" ).tabs({ active: 4 });
							</script>
						<% } %> 
					</div>
					
					<div id="tabs-5" align="left">
					 	<% if (request.getParameter("tab").equals("5")) {
					 		OnSession=false; %>
							<jsp:include page="ContratoModificatorioCompromiso.jsp" />
							<script type="text/javascript" charset="utf-8">
								$( "#tabs" ).tabs({ active: 5 });
							</script>
						<% } %> 
					</div>
					<div id="tabs-6" align="left">
					 	<% if (request.getParameter("tab").equals("6")) {
					 		OnSession=false; %>
							<jsp:include page="ContratoModificatorioPagos.jsp" />
							<script type="text/javascript" charset="utf-8">
								$( "#tabs" ).tabs({ active: 6 });
							</script>
						<% } %> 
					</div>
					<div id="tabs-7" align="left">
					 	<% if (request.getParameter("tab").equals("7")) {
					 		OnSession=false; %>
							<jsp:include page="ImprimirConvenio.jsp" />
							<script type="text/javascript" charset="utf-8">
								$( "#tabs" ).tabs({ active: 7 });
							</script>
						<% } %> 
					</div>
				<% } %>
		 		</div>
		 	</div>
		</div>
	</form>
</body>
</html>