<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%

    
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	String roles = "";
	Map rol = usuario.getRoles();
	Boolean OnSession=false;
	
	



		
	//if (session.getAttribute(GestionInterface.ATT_RelacionGastosEjercicio)== null ) {
	if (Integer.parseInt(request.getParameter("tab").toString()) == 1 || Integer.parseInt(request.getParameter("tab").toString()) == 0) {
	    session.setAttribute(GestionInterface.ATT_RelacionGastosEjercicio, null);
		session.setAttribute(GestionInterface.ATT_RelacionGastosFolio, null);
		session.setAttribute(GestionInterface.ATT_RelacionGastosEstado, null);
		//se agregan las variables de tipo concepto y destino del gasto
		session.setAttribute(GestionInterface.ATT_RelacionGastosDESTINO_GASTO, null);
		session.setAttribute(GestionInterface.ATT_RelacionGastosTIPO_CONCEPTO, null);
	
		
	}
		
		
	if (request.getParameter("cIdDocumento")!= null) {
	    OnSession = true;
		session.setAttribute(GestionInterface.ATT_RelacionGastosEjercicio, request.getParameter("cEjercicio").toString());
		session.setAttribute(GestionInterface.ATT_RelacionGastosFolio, request.getParameter("cIdDocumento").toString());
		session.setAttribute(GestionInterface.ATT_RelacionGastosEstado, request.getParameter("nIdEstado").toString());
		//se agregan las variables de tipo concepto y destino del gasto
		session.setAttribute(GestionInterface.ATT_RelacionGastosDESTINO_GASTO, request.getParameter("DESTINO_GASTO").toString());
		session.setAttribute(GestionInterface.ATT_RelacionGastosTIPO_CONCEPTO, request.getParameter("tConcepto").toString());
	}
	
	if ((String)session.getAttribute(GestionInterface.ATT_RelacionGastosFolio) != null)
		OnSession = true;
	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title></title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="Consolidado">
	<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />
	<style type="text/css" title="currentStyle">
			@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../css/demo_table_jui.css";
			@import "../css/demo_page.css";
	</style>
	<link href="../../css/interfaz.css" rel="stylesheet" type="text/css" />

	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.core"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
		<%
				int tabla=0;
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
				Map pestanas=ebl.getPestana(roles,"RelacionGastos");
				Iterator it = pestanas.entrySet().iterator();
				
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
				}
		%> 	
		    var sesion = "<%=OnSession%>";
			$(".tabs").tabs();
				$("#nuevoRelacionGastos").click(function() {
				window.location = "RelacionGastos.jsp?tab=" + 0;
			});
			   $("#consultaRelacionGastos").click(function() {
			 	window.location = "RelacionGastos.jsp?tab=" + 1;
			});
			
			 $("#partidasRelacionGastos").click(function() {
			 	window.location = "RelacionGastos.jsp?tab=" + 5;
			});
			
			
			$("#caratulaRelacionGastos").click(function() {
				window.location = "RelacionGastos.jsp?tab=" + 2;
			});
		
			
			$("#facturasRelacionGastos").click(function() {
				window.location = "RelacionGastos.jsp?tab=" + 4;
			});
			
			
			$("#firmantesRelacionGastos").click(function() {
				window.location = "RelacionGastos.jsp?tab=" + 3;
			});
			
			
			
			$("#apartadoRelacionGastos").click(function() {
				window.location = "RelacionGastos.jsp?tab=" + 6;
			});
			$("#pagosRelacionGastos").click(function() {
			 	window.location = "RelacionGastos.jsp?tab=" + 7;
			});
			
		
			if (sesion == "false") {
			   $( "#caratulaRelacionGastos" ).attr("disabled", true);
			   $( "#partidasRelacionGastos" ).attr("disabled", true);
			   $( "#apartadoRelacionGastos" ).attr("disabled", true);
			   $( "#pagosRelacionGastos" )	.attr("disabled", true);
			   $( "#facturasRelacionGastos" ).attr("disabled", true);
			   $( "#firmantesRelacionGastos" ).attr("disabled", true);
			   
			   
			}
			
			
		
			
		});
		
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
<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form id="form1"> 
		<div id="container" class="container">
			<br/>
			<br/>
			<h1>Relacion Gastos<label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<br/>
			<div class="tabs" id="tabs" >
				<ul>
					<li><a id="nuevoRelacionGastos" href="#tabs-0" >Nuevo</a></li>
					<li><a id="consultaRelacionGastos" href="#tabs-1" >Consulta</a></li>
					<li><a id="caratulaRelacionGastos" href="#tabs-2" >Car&aacute;tula</a></li>
					<li><a id="firmantesRelacionGastos" href="#tabs-3" >Firmantes</a></li>
					<li><a id="facturasRelacionGastos" href="#tabs-4" >Facturas</a></li>
					<li><a id="partidasRelacionGastos" href="#tabs-5" >Partidas</a></li>
					<li><a id="apartadoRelacionGastos" href="#tabs-6" >Apartado</a></li>
					<li><a id="pagosRelacionGastos"   href="#tabs-7" >Pagos</a></li>
				</ul>
				<% if (request.getParameter("tab")!= null){ %>
					<div id="tabs-0" align="center" >
						 <% if (request.getParameter("tab").equals("0")) {
						 OnSession=false; %>
						 <jsp:include page="NuevoRelacionGastos.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 0);
							</script>
						<% }  %>
					</div>
					<div id="tabs-1" align="center" >
						 <% if (request.getParameter("tab").equals("1")) {
						  OnSession=false;
						   %>
						 <jsp:include page="ConsultaRelacionGastos.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 1);
							</script>
						<% }  %>
					</div>
										
					<div id="tabs-5" align="center" >
						 <% if (request.getParameter("tab").equals("5") ) {  %>
						 <jsp:include page="PartidasRelacionGastos.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 5);
							</script>
						<% }  %>
					</div>
					
					
					
					<div id="tabs-2" align="center">
					 	<% if (request.getParameter("tab").equals("2")  ) { 
					 	OnSession=false;%>
							<jsp:include page="CaratulaRelacionGastos.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 2);
							</script>
						<% } %> 
					</div>
					
					
					
					<div id="tabs-4" align="center" >
						 <% if (request.getParameter("tab").equals("4") && OnSession) {  %>
						 <jsp:include page="FacturasRelacionGastos.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 4);
							</script>
						<% }  %>
					</div>
					
					
					<div id="tabs-3" align="center" >
						 <% if (request.getParameter("tab").equals("3")&& OnSession ) {  %>
						 <jsp:include page="FirmantesRelacionGastos.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 3);
							</script>
						<% }  %>
					</div>
					
					
					
					<div id="tabs-6" align="center" >
						 <% if (request.getParameter("tab").equals("6")&& OnSession   ) {  %>
							<jsp:include page="ApartadoRelacionGastos.jsp" />
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 6);
							</script>
						<% }  %>						 
					</div>
					<div id="tabs-7" align="center" >
						 <% if (request.getParameter("tab").equals("7")&& OnSession  ) { %>
							<jsp:include page="PagosRelacionGastos.jsp"/>
							<script type="text/javascript" charset="utf-8">
									var $tabs = $(".tabs").tabs();
									$tabs.tabs('select', 7);
							</script>
						<% }  %>						 
					</div>
				<% } %>
			</div>
	    </div>
	    </form>
</body>
</html>
