<%@page import="com.syc.gestion.core.Role"%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.admin.servlet.*"%>
<%@ page import="java.util.*" %>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	session.setAttribute("Termino", "cero");
	//int term=0;
	Map rol =usuario.getRoles();
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
	//else if((String)session.getAttribute("Termino")!=null)
		//term=1;
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    
    <title>Procesando</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
	</style>

	<style>			// estilos del dialogo
			div#dialog-form fieldset { padding:0; border:0; margin-top:25px; }
			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
		</style>

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
	
	<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
			cerrardiv("Term");
			$("#rn").val('<%=request.getParameter("rn")%>');
			$("#formato").val('<%=request.getParameter("formato")%>');
			$("#cIdUnidadEjecutora").val("<%=request.getParameter("cIdUnidadEjecutora")%>");
			
			
			document.formReportes.submit();
			//if(<%=request.getParameter("indicaMensaje")%> == 1)
				//alert("Ha Terminado ...");
		//alert($("#cIdUnidadEjecutora").val());
		//rutaAjax=(String)sesion.getAttribute("ruta");
			//window.setInterval("alert('Hello');", 100000, "JavaScript"); 
			//setInterval(function(){alert("Hello")},3000);
			//setInterval(function(){alert("Hello")},1000);
			//self.close();	
			
		});
		function cerrardiv(id) {
			var eldiv = document.getElementById(id);
			eldiv.style.display='none';
		}
		function mostrardiv(id) {
			var eldiv = document.getElementById(id);
			eldiv.style.display ='block';
		}
		
	
	</script>
  </head>
  
  <body >
  <div id="Proc">
  	<h4>Procesando ...</h4><br />
  </div>
  <div id="Term">
  	<h4>Termino de Procesar ...</h4><br />
  </div> 
  <form name="formReportes" id="formReportes" action="../../servlet/SeguridadCatalogosMateriales" method="GET" ><!-- target="_self" -->
		<input type="hidden" name="catalogo" id="catalogo" value="REPORTE">
		<input type="hidden" name="accion" id="accion" value="run">
		<input type="hidden" name="rn" id="rn">
		<input type="hidden" name="formato" id="formato">
		<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora">
		
		
	</form>
  </body>
</html>
