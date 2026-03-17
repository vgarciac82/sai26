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
		mntoCuentas = "SI".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
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
		<meta http-equiv="description" content="Catálogo de Beneficiarios">

		<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />

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
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>		
		
		<script type="text/javascript" charset="utf-8">
		
		$(document).ready(function() {
			//$( "#aTab1" ).attr("disabled", false);
			if($("#cUnidadResponsable").val() != null &&$("#cUnidadResponsable").val() != "null"){	
				//alert($("#cUnidadResponsable").val());
				//queryFormPost("mUnidadAdministrativaRead", {async : false});
				queryFormPost("mUnidadesAdministrativasRead", {async : false});
				//alert($("#estado").val());
				//cargo los valores a los campos
				$("#cUnidadAdministrativa").val($("#cUnidadResponsable").val());
				$("#cCentroContable").val($("#centroContable").val());
				$("#cDecripcionUnidadAdministrativa").val($("#D_DESCRIPCION").val());
				
				$("#cCalleUA").val($("#calle").val());
				$("#cNumExtUA").val($("#numExterior").val());
				$("#cNumIntUA").val($("#numInterior").val());
				$("#cColoniaUA").val($("#colonia").val());
				$("#cDelMpioUA").val($("#delegacionMunicipio").val());
				$("#cCodigoPostalUA").val($("#codigoPostal").val());
				$("#cCiudadUA").val($("#ciudad").val());
				$("#cEstadoUA").val($("#estado").val());
				$("#cTelefono1UA").val($("#telefono1").val());
				$("#cTelefono2UA").val($("#telefono2").val());
				querySelectPost("mCatalogoEntidadFederativaEditarRead", "cIdEntidadFederativa", {async : false});
				//$("#cIdEntidadFederativa").val(09);
			}
			
			//querySelectPost("mCatalogoTipoTelefonoRead", "cTipoTelefono", {async : false});						
			
			

			function checkLength( o, n, min, max ) {
				if ( o.val().length > max || o.val().length < min ) {
					o.addClass( "ui-state-error" );
					if (min == max)
						updateTipsDlg( "La longitud de " + n + " debe ser de " + min + " caracteres." );
					else
						updateTipsDlg( "La longitud de " + n + " debe estar entre " + min + " y " + max + "." );
					o.focus();
					return false;
				} else {
					return true;
				}
			}
			function checkRequerido( o, n) {
				var sTemp = $.trim(o.val());
				o.val(sTemp);
				if ( sTemp.length == 0  ) {
					o.addClass( "ui-state-error" );
					updateTipsDlg(  n + " es un dato requerido." );
					o.focus();
					return false;
				} else {
					return true;
				}
			}

			function checkRegexp( o, regexp, n ) {
				if ( !( regexp.test( o.val() ) ) ) {
					o.addClass( "ui-state-error" );
					updateTipsDlg( n );
					return false;
				} else {
					return true;
				}
			}
			
			$("#btnGuardar")
				.button()
				.click(function() {
					queryFormPost("checaRolUsuario",{async:false});
					if(($("#cUnidadResponsable").val()== $("#UE").val())||( $('#usuarioRole').val()=='ADMIN_RECMAT')){	
						queryFormPost("mCatalogoUnidadesAdministrativasUpdate",  {async : false});		
						alert("Datos Actualizados ");
						location.href = 'CatalogoUnidadesAdministrativas.jsp?tab=0';
					}
					 else{
					 	alert("Solo el Administrador puede hacer cambios, usted solo puede hacer cambios a su Unidad Ejecutora");
					  	return;
					 }
					
			});			
			
			$("#btnSalir")
				.button()
				.click(function() {
					location.href = 'CatalogoUnidadesAdministrativas.jsp?tab=0';				
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
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form >			
		<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora"  value="<%=usuario.getU_UR() %>"/>	
		<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable"  value="<%=request.getParameter("claveUnidadAdministrativa")%>"/>
		<input type="hidden" name="h_habilitado" id="h_habilitado"  value=""/>
		<input type="hidden" name="consecutivoNum" id="consecutivoNum"  value=""/>
		<input type="hidden" name="nIdTelefono" id="nIdTelefono"  value=""/>
		<input type="hidden" name="cmd" id="cmd"  value=""/>
		
		<input type="hidden" name="centroContable" id="centroContable"  value=""/>
		<input type="hidden" name="D_DESCRIPCION" id="D_DESCRIPCION"  value=""/>
		<input type="hidden" name="calle" id="calle"  value=""/>
		<input type="hidden" name="numExterior" id="numExterior"  value=""/>
		<input type="hidden" name="numInterior" id="numInterior"  value=""/>
		<input type="hidden" name="colonia" id="colonia"  value=""/>
		<input type="hidden" name="delegacionMunicipio" id="delegacionMunicipio"  value=""/>
		<input type="hidden" name="codigoPostal" id="codigoPostal"  value=""/>
		<input type="hidden" name="ciudad" id="ciudad"  value=""/>
		<input type="hidden" name="estado" id="estado"  value=""/>
		<input type="hidden" name="telefono1" id="telefono1"  value=""/>
		<input type="hidden" name="telefono2" id="telefono2"  value=""/>
		
		<input type="hidden" name="usuarioRole" id="usuarioRole" value="">
		<input type="hidden" name="UE" id="UE" value="<%=usuario.getU_UR()%>">
		<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=usuario.getLogin()%>">
		
		<div id="container" class="container" style="width: 100%">				
		<fieldset >
			<legend>Datos Generales:</legend>
			<table align="left">
				<tr align="left">
					<td>Clave de la Unidad Administrativa:</td>
					<td><input type="text" style="width: 300px" name="cUnidadAdministrativa" id="cUnidadAdministrativa" readonly style="border-width:0; background-color:transparent"/></td>
				</tr>			
				<tr align="left">
					<td>Centro Contable:</td>
					<td><input type="text" style="width: 300px" name="cCentroContable" id="cCentroContable" readonly style="border-width:0; background-color:transparent"/></td>
				</tr>
				<tr align="left">
					<td>Nombre de la Unidad Administrativa:</td>
					<td><input type="text" style="width: 400px" name="cDecripcionUnidadAdministrativa" id="cDecripcionUnidadAdministrativa" readonly style="border-width:0; background-color:transparent"/></td>
				</tr>
				<tr align="left">
				<td>Estado:</td>
					<td><input type="text" name="cEstadoUA" id="cEstadoUA" style="width: 300px;" readonly style="border-width:0; background-color:transparent" /></td>
				</tr>			
			</table>
		</fieldset>
		<fieldset>
			<legend>Direcci&oacute;n:</legend>
			<table align="left">
				
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
				<!--<tr align="left">
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
					<td colspan="2"><input type="button" name="btnGuardar" id="btnGuardar" value="Guardar" class="btnInterfaceBG" />&nbsp;&nbsp;
						<input type="button" name="btnSalir" id="btnSalir" value="Salir" class="btnInterfaceBG" /></td>
				</tr>			
			</table>
		</fieldset>
		
		</div>
	</form>	
</body>
</html>