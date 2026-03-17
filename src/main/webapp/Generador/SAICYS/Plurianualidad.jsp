<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	String role="";
	int tabla=0;
	Map rol =usuario.getRoles();
	
	String lContratoAbierto="0";
	Boolean OnSession=false;
	 if (request.getParameter("cEjercicio") != null) { 
		session.setAttribute(GestionInterface.ATT_ContratoEjercicioPlurianual,request.getParameter("cEjercicio").toString());
		System.out.println(request.getParameter("cIdTipoContrato").toString());
		session.setAttribute(GestionInterface.ATT_ContratoUnidadEjecPlurianual, request.getParameter("cIdUnidadEjecutora").toString());
		session.setAttribute(GestionInterface.ATT_ContratoPlurianual, request.getParameter("cIdContrato").toString());
		session.setAttribute(GestionInterface.ATT_ContratoPlurianualDefinitivo, request.getParameter("cIdContratoDefinitivo").toString());
		session.setAttribute(GestionInterface. ATT_tipoCambioPlurianual, request.getParameter("cIdTipoCambio").toString());
		session.setAttribute(GestionInterface. ATT_tipoContratoPlurianual, request.getParameter("cIdTipoContrato").toString());
		session.setAttribute(GestionInterface.ATT_ContratoPluAbierto, request.getParameter("lContratoAbierto").toString()==null?"0":request.getParameter("lContratoAbierto").toString());
		lContratoAbierto=request.getParameter("lContratoAbierto").toString()==null?"0":request.getParameter("lContratoAbierto").toString();
		
	}else{
		lContratoAbierto=(String)session.getAttribute(GestionInterface.ATT_ContratoPluAbierto);
	
	}
	
	
	 if ((String)session.getAttribute(GestionInterface.ATT_ContratoEjercicioPlurianual) != null)
		OnSession = false;
	
	
	System.out.println("lContratoAbierto ="+lContratoAbierto);
%>

<!doctype html>
<html >
  <head>
    <title>Plurianuales</title>
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
	<script type="text/javascript" src="../js/ContPlurianuales.js"></script>
	<link href="../../css/reportesGRM.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../js/FixedColumns.js"></script>
	
	<script type="text/javascript" charset="utf-8">
		var tabb=0;
		$(document).ready(function() {
		<%
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
				}
				Map pestanas=ebl.getPestana(role,"PlurianualidadContratos");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);<%
				}
			%>
			$(".tabs").tabs();
			$("#nuevaPlurianualidad").click(function() {
				window.location = "Plurianualidad.jsp?tab=" + 0;
			});
			$("#consultaPlurianualidad").click(function() {
				window.location = "Plurianualidad.jsp?tab=" + 1;
			});
			$("#caratulaPlurianualidad").click(function() {
				window.location = "Plurianualidad.jsp?tab=" + 2;
			});
			$("#presupuestoPlurianualidad").click(function() {
				window.location = "Plurianualidad.jsp?tab=" + 3;
			});
			$("#precompromisoPlurianualidad").click(function() {
				window.location = "Plurianualidad.jsp?tab=" + 4;
			});
			$("#nuevasEps").click(function() {
				window.location = "Plurianualidad.jsp?tab=" + 5;
			});
			$("#ampliacionContratoPluri").click(function() {
				window.location = "Plurianualidad.jsp?tab=" + 6;
			});
			$("#terminacionAnticipada").click(function() {
				window.location = "Plurianualidad.jsp?tab=" + 7;
			});
		});
		function getUrlParameter(param) {
			param = param.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
			var r1 = "[\\?&]"+param+"=([^&#]*)";
			var r2 = new RegExp(r1);
			var r3 = r2.exec(window.location.href);
			if (r3 == null)
				return "";
			else
				return r3[1];
		}
		function checkShortcut()
		{			
			if(event.keyCode==27){
				return false;
			}
			if((event.srcElement.tagName.toUpperCase() != 'INPUT'
				|| document.getElementById(event.srcElement.id).style.readonly )						
				&& (event.keyCode==8 || event.keyCode==13)){					
				return false;
			}
		}
		
	</script>
</head>
	<body>
		<form action="">
		<div class="container-fluid" >
			<div class="col-md-12 col-lg-12 col-sm-12">
	 			<h1 style="color: #1A69A9;">Contratos Plurianuales de Ejercicios Anteriores</h1>
	 			<div class="tabs" id="tabs">
		 			<ul class="nav nav-tabs">
		 				<li class="nav-item"><a href="#tabs-0" id="nuevaPlurianualidad" class="nav-link" aria-current="page">Nueva</a></li>
					    <li class="nav-item"><a href="#tabs-1" id="consultaPlurianualidad" class="nav-link" aria-current="page">Consulta</a></li>
					    <li class="nav-item"><a href="#tabs-2" id="caratulaPlurianualidad" class="nav-link" aria-current="page">Car&aacute;tula</a></li>
					    <li class="nav-item"><a href="#tabs-3" id="presupuestoPlurianualidad" class="nav-link" aria-current="page">Presupuesto</a></li>
					    <li class="nav-item"><a href="#tabs-4" id="precompromisoPlurianualidad" class="nav-link" aria-current="page">PreCompromiso</a></li>
					    <li class="nav-item"><a href="#tabs-5" id="nuevasEps" class="nav-link" aria-current="page">Nuevas EP´S</a></li>
					    <li class="nav-item"><a href="#tabs-6" id="ampliacionContratoPluri" class="nav-link" aria-current="page">Ampliaciones</a></li>
					    <li class="nav-item"><a href="#tabs-7" id="terminacionAnticipada" class="nav-link" aria-current="page">Terminaci&oacute;n Anticipada</a></li>
		 			</ul>
		 			<% if (request.getParameter("tab")!= null){ %>
						<div id="tabs-0" align="center" >
							 <% if (request.getParameter("tab").equals("0")) { %>
								<jsp:include page="NuevaPlurianualidad.jsp" />
								<script type="text/javascript" charset="utf-8">
									$( "#tabs" ).tabs({ active: 0 });
								</script>
							<% }  %>
						</div>
						<div id="tabs-1" align="center" >
							 <% if (request.getParameter("tab").equals("1")) { %>
								<jsp:include page="ConsultaPlurianualidad.jsp" />
								<script type="text/javascript" charset="utf-8">
									$( "#tabs" ).tabs({ active: 1 });
								</script>
							<% }  %>
						</div>
						<div id="tabs-2" align="center" >
							 <% if (request.getParameter("tab").equals("2")) { %>
								<jsp:include page="CaratulaPlurianualidad.jsp" />
								<script type="text/javascript" charset="utf-8">
									$( "#tabs" ).tabs({ active: 2 });
								</script>
							<% }  %>
						</div>
						<div id="tabs-3" align="center" >
							 <% if (request.getParameter("tab").equals("3")) { %>
								<jsp:include page="presupuestoPlurianualidad.jsp" />
								<script type="text/javascript" charset="utf-8">
									$( "#tabs" ).tabs({ active: 3 });
								</script>
							<% }  %>
						</div>
						<div id="tabs-4" align="center" >
							 <% if (request.getParameter("tab").equals("4")) { %>
								<jsp:include page="precompromisoPlurianualidad.jsp" />
								<script type="text/javascript" charset="utf-8">
									$( "#tabs" ).tabs({ active: 4 });
								</script>
							<% }  %>
						</div>
						<div id="tabs-5" align="center" >
							 <% if (request.getParameter("tab").equals("5")) { %>
								<jsp:include page="NuevasEPSContratoPluri.jsp" />
								<script type="text/javascript" charset="utf-8">
									$( "#tabs" ).tabs({ active: 5 });
								</script>
							<% }  %>
						</div>
						<div id="tabs-6" align="center" >
							 <% if (request.getParameter("tab").equals("6")) { %>
								<jsp:include page="AmpliacionContratoPluri.jsp" />
								<script type="text/javascript" charset="utf-8">
									$( "#tabs" ).tabs({ active: 6 });
								</script>
							<% }  %>
						</div>
						<div id="tabs-7" align="center" >
							 <% if (request.getParameter("tab").equals("7")) { %>
								<jsp:include page="TerminacionAnticipadaContratoPluri.jsp" />
								<script type="text/javascript" charset="utf-8">
									$( "#tabs" ).tabs({ active: 7 });
								</script>
							<% }  %>
						</div>
				<% } %> 
					
		 		</div>
		 	</div>
		 	<input type="hidden" name="session" id="session" value="<%= OnSession %>" />
		   	<input type="hidden" name="esContratoAbierto" id="esContratoAbierto" value="<%= lContratoAbierto %>" />
			<input type="hidden" name="tab" id="tab" value="<%= request.getParameter("tab") %>" />
		 </div>
	</form>
	</body>
</html>
