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
	Map<String, Role> rol =usuario.getRoles();
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
		<title>Cat&aacute;logo de Unidades Administrativas</title>

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
			querySelectPost("vUnidadAdministrativaClaveRead", "cUnidadResponsable", {async : false});
			cargaCombos();
			$("#btnGuardar")
				.button()
				.click(function() {
					queryFormPost("checaRolUsuario",{async:false});
					if(($("#cUnidadResponsable").val()== $("#UE").val())||( $('#usuarioRole').val()=='ADMIN_RECMAT')){	
						//alert("Entre");
						$("#claveUnidadAdministrativa").val($("#cUnidadResponsable").val());
						$("#centroContable").val($("#cCentroContable").val());
						queryFormPost("mCatalogoUnidadesAdministrativasExiste",  {async : false});//SE CHECA SI YA EXISTE LA UE EN LA TABLA
						//alert($("#regreso").val());
						if($("#regreso").val()!='EXISTE'){
							queryFormPost("mCatalogoUnidadesAdministrativasInsert",  {async : false});		
							alert("Registro Cargado de la Unidad Administrativa");
							location.href = 'CatalogoUnidadesAdministrativas.jsp?tab=0';
						}
						else{
							if(!window.confirm("La Unidad Administrativa ya Existe, Desea Remplazarla"))
								return;
							else{
								$("#cUnidadAdministrativa").val($("#claveUnidadAdministrativa").val());
								queryFormPost("mCatalogoUnidadesAdministrativasUpdate",  {async : false});		
								alert("Registro Guardado");
								location.href = 'CatalogoUnidadesAdministrativas.jsp?tab=0';
								
							}
						}
					}
					 else{
					 	alert("Solo el Administrador puede hacer cambios, usted solo puede hacer cambios a su Unidad Ejecutora");
					  	return;
					 }
				});
			$("#cUnidadResponsable").change(function () {				
				cargaCombos();				
			});
			$("#btnSalir")
				.button()
				.click(function() {
					location.href = 'CatalogoUnidadesAdministrativas.jsp?tab=0';				
			});			
		
        });
		function cargaCombos() {
			querySelectPost("vUnidadAdministrativaCCRead", "cCentroContable", {async : true});
			//querySelectPost("vUnidadAdministrativaNombreRead", "D_DESCRIPCION", {async : true});
		}
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
	<input type="hidden" name="claveUnidadAdministrativa" id="claveUnidadAdministrativa"  value=""/>
	<input type="hidden" name="centroContable" id="centroContable"  value=""/>
	<input type="hidden" name="regreso" id="regreso"  value=""/>
	<input type="hidden" name="cUnidadAdministrativa" id="cUnidadAdministrativa"  value=""/>
	
	<input type="hidden" name="usuarioRole" id="usuarioRole" value="">
	<input type="hidden" name="UE" id="UE" value="<%=usuario.getU_UR()%>">
	<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=usuario.getLogin()%>">
	<fieldset>
		<legend>UE</legend>
			<table align="left">
				<tr id="trcUnidadResponsable" align="left">
					<td>Clave de la Unidad Administrativa:</td>
					<td>
						<select id="cUnidadResponsable" name="cUnidadResponsable" style="width: 20em;">
						<option value="<%=usuario.getU_UR()%>" selected="selected"> 
						</option>
					</td>	
				</tr>
				<tr id="trcCentroContable" align="left">
					<td>Centro Contable:</td>
					<td>
					<select id="cCentroContable" name="cCentroContable" style="width: 20em;"></select>
					</td>
				</tr>
			</table>
	</fieldset>
	<fieldset>
			<legend>Direcci&oacute;n:</legend>
			<table align="left">
				<!-- <tr align="left">
					<td>Estado:</td>
					<td><select id="cIdEntidadFederativa" name="cIdEntidadFederativa" style="width: 30em;">
					<option value="09" selected="selected"> </option></select> 
					</td>
				</tr>
					  --> 
				<tr align="left">
					<td>Calle:</td>
					<td><input type="text" name="cCalleUA" id="cCalleUA" style="width: 30em;" maxlength="30"/></td>
				</tr>			
				<tr align="left">
					<td>N&uacute;mero Externo:</td>
					<td><input type="text" name="cNumExtUA" id="cNumExtUA" style="width: 30em;" maxlength="10" /></td>
				</tr>
				<tr align="left">
					<td>N&uacute;mero Interno:</td>
					<td><input type="text" name="cNumIntUA" id="cNumIntUA" style="width: 30em;" maxlength="10" /></td>
				</tr>
				<tr align="left">
					<td>Colonia:</td>
					<td><input type="text" name="cColoniaUA" id="cColoniaUA" style="width: 30em;" maxlength="30"/></td>
				</tr>
				<tr align="left">
					<td>Delegaci&oacute;n &oacute; Municipio:</td>
					<td><input type="text" name="cDelMpioUA" id="cDelMpioUA" style="width: 30em;" maxlength="30"/></td>
				</tr>
				<tr align="left">
					<td>C&oacute;digo Postal:</td>
					<td><input type="text" name="cCodigoPostalUA" id="cCodigoPostalUA" style="width: 30em;" maxlength="5" onkeypress="return(onlyNumbers2(event));"/></td>
				</tr>	
				<tr align="left">
					<td>Ciudad:</td>
					<td><input type="text" name="cCiudadUA" id="cCiudadUA" style="width: 30em;" maxlength="30"/></td>
				</tr>	
				<!-- <tr align="left">
					<td>Estado:</td>
					<td><input type="text" name="cEstadoUA" id="cEstadoUA" style="width: 30em;" maxlength="300"/></td>
				</tr>
				 -->
				<tr align="left">
					<td>Telefono1:</td>
					<td><input type="text" name="cTelefono1UA" id="cTelefono1UA" style="width: 30em;" maxlength="15" onkeypress="return(onlyNumbers2(event));"/></td>
				</tr>
				<tr align="left">
					<td>Telefono2:</td>
					<td><input type="text" name="cTelefono2UA" id="cTelefono2UA" style="width: 30em;" maxlength="15" onkeypress="return(onlyNumbers2(event));"/></td>
				</tr>	
				<tr align="center">
					<td colspan="2"><input type="button" name="btnGuardar" id="btnGuardar" value="Guardar"  class="btnInterfaceBG"/>&nbsp;&nbsp;
						<input type="button" name="btnSalir" id="btnSalir" value="Salir" class="btnInterfaceBG" /></td>
				</tr>			
			</table>
		</fieldset>
		</form>	
</body>
</html>