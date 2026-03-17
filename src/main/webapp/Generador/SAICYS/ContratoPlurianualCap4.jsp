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
 %>

<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>ContratoPlurianualCap4</title>
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
	
	<link rel="stylesheet" href="https://code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
	<script src="https://code.jquery.com/ui/1.13.2/jquery-ui.js"></script>
	
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/funciones.js"></script>
 	
	<link href="../../css/reportesGRM.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../js/FixedColumns.js"></script>
	
 	<script type="text/javascript" src="../js/ContratoPluriCap4.js"></script>
	
	
  	
	<script type="text/javascript" charset="utf-8">
		var tabb=0;	
  		$(document).ready(function() {
  			<%
  				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map pestanas=ebl.getPestana(roles,"ContratoCap4");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
				}
				
  			%>
	  		$(".tabs").tabs();
	  		
	  		$("#NuevoContratoPluriCap4").click(function() {
					window.location = "ContratoPlurianualCap4.jsp?tab=" + 0;
			});
			$("#ConsultaContratoPluriCap4").click(function() {
					window.location = "ContratoPlurianualCap4.jsp?tab=" + 1;
			});
			$("#CaratulaContratoPluriCap4").click(function() {
					window.location = "ContratoPlurianualCap4.jsp?tab=" + 2;
			});
			$("#PresupuestoContratoPluriCap4").click(function() {
					window.location = "ContratoPlurianualCap4.jsp?tab=" + 3;
			});
			$("#PrecompromisoContratoPluriCap4").click(function() {
					window.location = "ContratoPlurianualCap4.jsp?tab=" + 4;
			});
			$("#NuevasEpsContratoPluriCap4").click(function() {
					window.location = "ContratoPlurianualCap4.jsp?tab=" + 5;
			});
			$("#AmpliacionContratoPluriCap4").click(function() {
					window.location = "ContratoPlurianualCap4.jsp?tab=" + 6;
			});
			
			
			
		});//Fin del documentReady
  	</script>
  </head>
  
  <body>
		<div class="container-fluid" >
			<div class="col-md-12 col-lg-12 col-sm-12">
	 			<h1 style="color: #1A69A9;">Contratos Plurianuales Cap&iacute;tulo 4 mil</h1>
	 			<div class="tabs" id="tabs">
		 			<ul class="nav nav-tabs">
		 				<li class="nav-item"><a href="#tabs-0" id="NuevoContratoPluriCap4" class="nav-link" aria-current="page">Nuevo</a></li>
					    <li class="nav-item"><a href="#tabs-1" id="ConsultaContratoPluriCap4" class="nav-link" aria-current="page">Consulta</a></li>
					    <li class="nav-item"><a href="#tabs-2" id="CaratulaContratoPluriCap4" class="nav-link" aria-current="page">Car&aacute;tula</a></li>
					    <li class="nav-item"><a href="#tabs-3" id="PresupuestoContratoPluriCap4" class="nav-link" aria-current="page">Presupuesto</a></li>
					    <li class="nav-item"><a href="#tabs-4" id="PrecompromisoContratoPluriCap4" class="nav-link" aria-current="page">Precompromiso</a></li>
					    <li class="nav-item"><a href="#tabs-5" id="NuevasEpsContratoPluriCap4" class="nav-link" aria-current="page">Nuevas EP´S</a></li>
					    <li class="nav-item"><a href="#tabs-6" id="AmpliacionContratoPluriCap4" class="nav-link" aria-current="page">Ampliaci&oacute;n</a></li>
					    
		 			</ul>
					<div id="tabs-0" align=left>
						<% if (request.getParameter("tab").equals("0")) { %>
							<jsp:include page="NuevoContratoPluriCap4.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$( "#tabs" ).tabs({ active: 0 });
							</script>
						<% } %>
					</div>
					<div id="tabs-1" align="left" >
						<% if (request.getParameter("tab").equals("1")) { %>
							<jsp:include page="ConsultaContratoPluriCap4.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$( "#tabs" ).tabs({ active: 1 });
							</script>
						<% } %>
					</div>
					<div id="tabs-2" align="left">
						<% if (request.getParameter("tab").equals("2")) { %>
							<jsp:include page="CaratulaContratoPluriCap4.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$( "#tabs" ).tabs({ active: 2 });
							</script>
						<% } %>
					</div>
					<div id="tabs-3" align="left">
						<% if (request.getParameter("tab").equals("3")) { %>
							<jsp:include page="PresupuestoContratoPluriCap4.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$( "#tabs" ).tabs({ active: 3 });
							</script>
						<% } %>
					</div>
					<div id="tabs-4" align="left">
						<% if (request.getParameter("tab").equals("4")) { %>
							<jsp:include page="PrecompromisoContratoPluriCap4.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$( "#tabs" ).tabs({ active: 4 });
							</script>
						<% } %>
					</div>
					<div id="tabs-5" align="left">
						<% if (request.getParameter("tab").equals("5")) { %>
							<jsp:include page="NuevasEpsContratoPluriCap4.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$( "#tabs" ).tabs({ active: 5 });
							</script>
						<% } %>
					</div>
					<div id="tabs-6" align="left">
						<% if (request.getParameter("tab").equals("6")) { %>
							<jsp:include page="AmpliacionContratoPluriCap4.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$( "#tabs" ).tabs({ active: 6 });
							</script>
						<% } %>
					</div>
				</div>
	    	</div>
	    </div>
	    <input type="hidden" name="inpTab" id="inpTab" value="<%= request.getParameter("tab")  %>" />
	    
  </body>
</html>
