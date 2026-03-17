<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}		
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    
    <title>Proveedor</title>
    <meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
	<link rel="stylesheet" href="https://cdn.datatables.net/2.0.6/css/dataTables.bootstrap5.min.css" />
	<link rel="stylesheet" type="text/css"	href="../../Generador/css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../../Generador/css/bootstrap.min.css"></link>
	<link rel="stylesheet" href="../../SISECOP/css/style.css">
		

	<link rel="stylesheet" type="text/css"	href="../../Generador/css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../../Generador/css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css"	href="../../Generador/css/bootstrap.min.css"></link>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	
	<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
			$(".tabs").tabs();						
			$("#aTab0").click(function() {
				window.location = "CatalogoEntidadesFederativas.jsp?tab=" + 0;
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
			if(event.keyCode==27){  //escape
				return false;
			}
			if(((event.srcElement.tagName.toUpperCase() != 'INPUT' && event.srcElement.tagName.toUpperCase() != 'TEXTAREA')
				|| document.getElementById(event.srcElement.id).style.readonly )	//backspace
				&& (event.keyCode==8 || event.keyCode==13)){					
				return false;
			}
		}
	</script>
  </head>
  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" onkeydown="return checkShortcut()">
   <form > 
		<div id="container" class="container">	
			<br/>
			<br/>
			<h4 class="mt-3 mb-3">Catalogos de Entidades Federativas</h4>
			<br/>
			<div class="tabs" id="tabs" name="tabs" >
				<ul>
					<li><a id="aTab0" href="#tabs-0">Consulta</a></li>
					<!--  <li><a id="aTab1" href="#tabs-1" >Cambios</a></li>-->				
				</ul>
				
				<div id="tabs-0" align="center">
					<% if (request.getParameter("tab").equals("0")) { %>
					<jsp:include page="CatalogoEntidadesFederativasGrid.jsp" />	
					<script type="text/javascript" charset="utf-8">
								var $tabs = $(".tabs").tabs();
								$tabs.tabs('select', 0);
					</script>
					<% } %>
				</div>
				
			</div>
	    </div>
	    
	</form>
  </body>
</html>
