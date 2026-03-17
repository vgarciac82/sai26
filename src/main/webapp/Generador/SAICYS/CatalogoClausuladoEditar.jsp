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
	
	String idClausulado = request.getParameter("nIdClausula");
	
	Map rol =usuario.getRoles();
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Cat&aacute;logo de Responsables por Unidad Administrativa</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
			@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../css/demo_table_jui.css";
			@import "../css/demo_page.css";
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
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>

		<script type="text/javascript" charset="utf-8">
		var oTable;

		
		$(document).ready(function() {
		
			queryFormPost("fnReadClausula", {async: false });
			

			$("#btnGuardar").button().click(function(){	
				//$("#direccion").val($("#cDirec").val());
				//$("#responsable").val($("#cResponsable").val());
					
				if($("#cNumClau").val()==""){				
					alert("El Número de Cláusula es Necesario.");
					return;
				}	
				
				if($("#cDesc").val()==""){				
					alert("La Descripción de la Cláusula es Necesaria.");
					return;
				}	
				
				queryFormPost("mClausuladoUpdate",  {async : false});
				alert("Datos Guardados");
				
				
				window.location = "CatalogoClausulado.jsp?tab=" + 0;
			});
			
			
        });
		
	function onlyNumbers2(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode
		var strCheck = '0123456789';
	
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1)
			return false; // Valida que sea numero y punto decimal
	
		return true 
		//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
	}
</script>
		

</head>

<body id="dt_example">
	<form action="">
	

	<input type="hidden" name="direccion" id="direccion"  />
	<input type="hidden" name="responsable" id="responsable"  />
	
	
	<input type="hidden" name="usuarioRole" id="usuarioRole" >
	<input type="hidden" name="UE" id="UE" value="<%=usuario.getU_UR()%>">
	<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=usuario.getLogin()%>">
	
	<fieldset>
			<legend>Datos de la Cl&aacute;usula</legend>
			<table align="left">
				<tr align="left">
					<td>N&uacute;mero de Cl&aacute;usula: 
					<input type="hidden" id="idClausula" name="idClausula" value="<%=idClausulado %>">
					</td>
					<td><input type="text" name="cNumClau" id="cNumClau" value="" style="width: 30em;" maxlength="50"/><br>Ejemplo: PRIMERA, PRIMERA (Seg&uacute;n aplique)</td>
				</tr>
				<tr align="left">
					<td>Descripci&oacute;n:</td>
					<td><textarea  name="cDesc" id="cDesc" cols="50" rows="6" ></textarea></td>
				</tr>		
				
				
				<tr align="center">
					<td colspan="2"><input type="button" name="btnGuardar" id="btnGuardar" value="Editar"  />&nbsp;&nbsp;
						</td>
				</tr>			
			</table>
		</fieldset>
		</form>	
</body>
</html>