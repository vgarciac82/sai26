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
 %>

<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>ContratoCap4</title>
    
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
 	<script type="text/javascript" src="../js/ContratoCap4.js"></script>
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
	  		if(parseInt($("#inpTab").val(),10)>1){
	  			$("#nIdEstatus").val("<%=nEstatus %>");
	  			$("#nEsAbierto").val("<%=nEsAbierto %>");
				$( "#CaratulaContratoCap4" ).css("display", "block");
				$( "#PartidasContratoCap4" ).css("display", "block");
				$( "#PresupuestoContratoCap4").css("display", "block");
				$( "#PrecompromisoContratoCap4" ).css("display", "block");
				$( "#NuevasEpsContratoCap4" ).css("display", "none");
				$( "#AmpliacionContratoCap4" ).css("display", "none");
				if(parseInt($("#nIdEstatus").val(),10)==4){
					$( "#NuevasEpsContratoCap4" ).css("display", "block");
					if(parseInt($("#nEsAbierto").val(),10)==1){
						$( "#AmpliacionContratoCap4" ).css("display", "block");
					}else{
						$( "#AmpliacionContratoCap4" ).css("display", "none");
					}
				}else{
					$( "#NuevasEpsContratoCap4" ).css("display", "none");
					$( "#AmpliacionContratoCap4" ).css("display", "none");
				}
			}else{
				$( "#CaratulaContratoCap4" ).css("display", "none");
				$( "#PartidasContratoCap4" ).css("display", "none");
				$( "#PresupuestoContratoCap4" ).css("display", "none");
				$( "#PrecompromisoContratoCap4" ).css("display", "none");
				$( "#NuevasEpsContratoCap4" ).css("display", "none");
				$( "#AmpliacionContratoCap4" ).css("display", "none");
			}
			
			
			
	  		$("#NuevoContratoCap4").click(function() {
					window.location = "ContratoCap4.jsp?tab=" + 0;
			});
			$("#ConsultaContratoCap4").click(function() {
					window.location = "ContratoCap4.jsp?tab=" + 1;
			});
			$("#CaratulaContratoCap4").click(function() {
					window.location = "ContratoCap4.jsp?tab=" + 2;
			});
			
			$("#PartidasContratoCap4").click(function() {
					window.location = "ContratoCap4.jsp?tab=" + 3;
			});
			$("#PresupuestoContratoCap4").click(function() {
					window.location = "ContratoCap4.jsp?tab=" + 4;
			});
			$("#PrecompromisoContratoCap4").click(function() {
					window.location = "ContratoCap4.jsp?tab=" + 5;
			});
			$("#NuevasEpsContratoCap4").click(function() {
					window.location = "ContratoCap4.jsp?tab=" + 6;
			});
			$("#AmpliacionContratoCap4").click(function() {
					window.location = "ContratoCap4.jsp?tab=" + 7;
			});
			
			
			
		});
		function checkShortcut()
		{			
			if(event.keyCode==27){  //escape
				return false;
			}
			if(((event.srcElement.tagName.toUpperCase() != 'INPUT' && event.srcElement.tagName.toUpperCase() != 'TEXTAREA')
				|| document.getElementById(event.srcElement.id).style.readonly )	//backspace
				&& (event.keyCode==8 || event.keyCode==13)){					
				return false;
			}
		}
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
  	</script>
  </head>
  
  <body>
		<div class="container-fluid" >
			<div class="col-md-12 col-lg-12 col-sm-12">
	 			<h1 style="color: #1A69A9;">Contratos cap&iacute;tulo 4 mil</h1>
	 			<div class="tabs" id="tabs">
		 			<ul class="nav nav-tabs">
		 				<li class="nav-item"><a href="#tabs-0" id="NuevoContratoCap4" class="nav-link" aria-current="page">Nuevo</a></li>
					    <li class="nav-item"><a href="#tabs-1" id="ConsultaContratoCap4" class="nav-link" aria-current="page">Consulta</a></li>
					    <li class="nav-item"><a href="#tabs-2" id="CaratulaContratoCap4" class="nav-link" aria-current="page">Car&aacute;tula</a></li>
					    <li class="nav-item"><a href="#tabs-3" id="PartidasContratoCap4" class="nav-link" aria-current="page">Partidas</a></li>
					    <li class="nav-item"><a href="#tabs-4" id="PresupuestoContratoCap4" class="nav-link" aria-current="page">Presupuesto</a></li>
					    <li class="nav-item"><a href="#tabs-5" id="PrecompromisoContratoCap4" class="nav-link" aria-current="page">Precompromiso</a></li>
					    <li class="nav-item"><a href="#tabs-6" id="NuevasEpsContratoCap4" class="nav-link" aria-current="page">Nuevas EP´S</a></li>
					    <li class="nav-item"><a href="#tabs-7" id="AmpliacionContratoCap4" class="nav-link" aria-current="page">Ampliaci&oacute;n</a></li>
					    
		 			</ul>
					<div id="tabs-0" align=left>
						<% if (request.getParameter("tab").equals("0")) { %>
							<jsp:include page="NuevoContratoCap4.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$( "#tabs" ).tabs({ active: 0 });
							</script>
						<% } %>
					</div>
					<div id="tabs-1" align="left" >
						<% if (request.getParameter("tab").equals("1")) { %>
							<jsp:include page="ConsultaContratoCap4.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$( "#tabs" ).tabs({ active: 1 });
							</script>
						<% } %>
					</div>
					<div id="tabs-2" align="left">
						<% if (request.getParameter("tab").equals("2")) { %>
							<jsp:include page="CaratulaContratoCap4.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$( "#tabs" ).tabs({ active: 2 });
							</script>
						<% } %>
					</div>
					<div id="tabs-3" align="left">
						<% if (request.getParameter("tab").equals("3")) { %>
							<jsp:include page="PartidasContratoCap4.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$( "#tabs" ).tabs({ active: 3 });
							</script>
						<% } %>
					</div>
					<div id="tabs-4" align="left">
						<% if (request.getParameter("tab").equals("4")) { %>
							<jsp:include page="PresupuestoContratoCap4.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$( "#tabs" ).tabs({ active: 4 });
							</script>
						<% } %>
					</div>
					<div id="tabs-5" align="left">
						<% if (request.getParameter("tab").equals("5")) { %>
							<jsp:include page="PrecompromisoContratoCap4.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$( "#tabs" ).tabs({ active: 5 });
							</script>
						<% } %>
					</div>
					<div id="tabs-6" align="left">
						<% if (request.getParameter("tab").equals("6")) { %>
							<jsp:include page="NuevasEpsContratoCap4.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$( "#tabs" ).tabs({ active: 6 });
							</script>
						<% } %>
					</div>
					<div id="tabs-7" align="left">
						<% if (request.getParameter("tab").equals("7")) { %>
							<jsp:include page="AmpliacionContratoCap4.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$( "#tabs" ).tabs({ active: 7 });
							</script>
						<% } %>
					</div>
				</div>
	    	</div>
	    </div>
	    <input type="hidden" name="inpTab" id="inpTab" value="<%= request.getParameter("tab")  %>" />
	    <input type="hidden" name="nIdEstatus" id="nIdEstatus" value="1" />
	    <input type="hidden" name="nEsAbierto" id="nEsAbierto" value="0" />
	    
  </body>
</html>
