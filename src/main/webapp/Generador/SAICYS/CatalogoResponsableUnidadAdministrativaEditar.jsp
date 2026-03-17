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
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	Map rol =usuario.getRoles();
	
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "SI".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
	//System.out.println("La ***********"+request.getParameter("claveUnidadAdministrativa"));
	//System.out.println("La ++++++++++"+request.getParameter("estadoF"));
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Cat&aacute;logo de Responsables por Unidad Administrativas</title>

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
				queryFormPost("mResponsablesUnidadesAdministrativasRead", {async : false});
				//querySelectPost("MCATALOGOENTIDADfEDERATIVA", "UNIDAD", {async : false});
				//alert($("#U_EMAIL").val());
				//cargo los valores a los campos cDecripcionUnidadAdministrativa
				$("#cUnidadAdministrativa").val($("#cUnidadResponsable").val());
				$("#cDecripcionUnidadAdministrativa").val($("#AREAS").val());
				$("#cEstadoUA").val($("#estado").val());
				$("#cNombre").val($("#nombre").val());
				$("#cCargo").val($("#cargo").val());
				$("#cCalleUA").val($("#calle").val());
				$("#cNumExtUA").val($("#numExt").val());
				$("#cNumIntUA").val($("#numInt").val());
				$("#cColoniaUA").val($("#colonia").val());
				$("#cDelMpioUA").val($("#delegacionMunicipio").val());
				$("#cCodigoPostalUA").val($("#codigoPostal").val());
				$("#cCiudadUA").val($("#ciudad").val());
				$("#cEstado").val($("#estado").val());
				$("#cTelefono1UA").val($("#telefono1").val());
				$("#cextTel1").val($("#extTelefono").val());
				$("#cTelefono2UA").val($("#Telefono2").val());
				$("#cFax").val($("#fax").val());
				//$("#cLogin").val($("#U_Login").val());
				//$("#cEmail").val($("#U_EMAIL").val());
				$("#cTelCelular").val($("#telCelular").val());
		
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
						if($("#cNombre").val()!=''){
							queryFormPost("mResponsableExiste",  {async : false});//SE CHECA SI YA EXISTE el usuario en CG_Usuario
							//Se inserta el nombre en otra tabla cuando no existe en CG_Usuario
							
							if($("#existeNombre").val()!='EXISTE'){//Cuando No existe en cg_usuario
								//alert($("#cUnidadResponsable").val());
								queryFormPost("mResponsableUnidadAdministrativaUpdate",  {async : false});	
								//queryFormPost("mResponsableExisteenUR",  {async : false});
								//Y que no exista en la tabla mUsuariosResponsables
								//alert($("#existeNombreUR").val());
								//if($("#existeNombreUR").val()!='EXISTE'){
									//alert("El Usuario no esta dado de alta "+$("#cNombre").val());
									//queryFormPost("mUsuariosResponsablesInsert",  {async : false});
								//}
							}
							
							queryFormPost("mCatalogoResponsableUnidadAdministrativaUpdate",  {async : false});		
							alert("Registro Actualizado ");
							location.href = 'CatalogoResponsableUnidadAdministrativa.jsp?tab=0';
						}
						else
							alert("El Campo Nombre es requerido");
					}
					else{
					 	alert("Solo el Administrador puede hacer cambios, usted solo puede hacer cambios a su Unidad Ejecutora");
					  	return;
					}
			});			
			
			$("#btnSalir")
				.button()
				.click(function() {
					location.href = 'CatalogoResponsableUnidadAdministrativa.jsp?tab=0';				
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
		
		<input type="hidden" name="ENTIDAD_FEDERATIVA" id="ENTIDAD_FEDERATIVA"  value=""/>
		<input type="hidden" name="AREAS" id="AREAS"  value=""/>
		<input type="hidden" name="nombre" id="nombre"  value=""/>
		<input type="hidden" name="cargo" id="cargo"  value=""/>
		<input type="hidden" name="calle" id="calle"  value=""/>
		<input type="hidden" name="numExt" id="numExt"  value=""/>
		<input type="hidden" name="numInt" id="numInt"  value=""/>
		<input type="hidden" name="colonia" id="colonia"  value=""/>
		<input type="hidden" name="delegacionMunicipio" id="delegacionMunicipio"  value=""/>
		<input type="hidden" name="codigoPostal" id="codigoPostal"  value=""/>
		<input type="hidden" name="ciudad" id="ciudad"  value=""/>
		<input type="hidden" name="estado" id="estado"  value=""/>
		<input type="hidden" name="telefono1" id="telefono1"  value=""/>
		<input type="hidden" name="extTelefono" id="extTelefono"  value=""/>
		<input type="hidden" name="Telefono2" id="Telefono2"  value=""/>
		<input type="hidden" name="fax" id="fax"  value=""/>
		<input type="hidden" name="existeNombre" id="existeNombre"  value=""/>
		<input type="hidden" name="existeNombreUR" id="existeNombreUR"  value=""/>
		
		<input type="hidden" name="telCelular" id="telCelular"  value=""/>
		
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
					<td>Nombre de la Unidad Administrativa:</td>
					<td><input type="text" style="width: 300px" name="cDecripcionUnidadAdministrativa" id="cDecripcionUnidadAdministrativa" readonly style="border-width:0; background-color:transparent"/></td>
				</tr>
				<tr align="left">
					<td>Estado:</td>
					<td><input type="text" style="width: 300px" name="cEstado" id="cEstado" readonly style="border-width:0; background-color:transparent"/></td>
				</tr>	
			</table>
		</fieldset>
		<fieldset>
			<legend>Direcci&oacute;n:</legend>
			<table align="left">
				
				<tr align="left">
					<td>Nombre:</td>
					<td><input type="text" name="cNombre" id="cNombre" style="width: 30em;" maxlength="30"/></td>
				</tr>
				<tr align="left">
					<td>Cargo</td>
					<td><input type="text" name="cCargo" id="cCargo" style="width: 30em;" maxlength="30"/></td>
				</tr>		
				<tr align="left">
					<td>Calle:</td>
					<td><input type="text" name="cCalleUA" id="cCalleUA" style="width: 30em;" maxlength="30"/></td>
				</tr>			
				<tr align="left">
					<td>N&uacute;mero Exterior:</td>
					<td><input type="text" name="cNumExtUA" id="cNumExtUA" style="width: 30em;" maxlength="10" /></td>
				</tr>
				<tr align="left">
					<td>N&uacute;mero Interior:</td>
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
					
			
				<tr align="left">
					<td>Telefono1:</td>
					<td><input type="text" name="cTelefono1UA" id="cTelefono1UA" style="width: 30em;" maxlength="15" onkeypress="return(onlyNumbers2(event));"/></td>
					<td>Ext:</td>
					<td><input type="text" name="cextTel1" id="cextTel1" style="width: 10em;" maxlength="5" onkeypress="return(onlyNumbers2(event));"/></td>
				
				</tr>
				<tr align="left">
					<td>Telefono2:</td>
					<td><input type="text" name="cTelefono2UA" id="cTelefono2UA" style="width: 30em;" maxlength="15" onkeypress="return(onlyNumbers2(event));"/></td>
				</tr>	
				<tr align="left">
					<td>Fax:</td>
					<td><input type="text" name="cFax" id="cFax" style="width: 30em;" maxlength="15" onkeypress="return(onlyNumbers2(event));"/></td>
				</tr>
				<!-- 
				<tr align="left">
					<td>Login:</td>
					<td><input type="text" name="cLogin" id="cLogin" style="width: 30em;" maxlength="15" /></td>
				</tr>
				<tr align="left">
					<td>Email:</td>
					<td><input type="text" name="cEmail" id="cEmail" style="width: 30em;" maxlength="15" /></td>
				</tr>
				 -->
				<tr align="left">
					<td>TelCelular:</td>
					<td><input type="text" name="cTelCelular" id="cTelCelular" style="width: 30em;" maxlength="15" onkeypress="return(onlyNumbers2(event));"/></td>
				</tr>
				<tr align="center">
					<td colspan="2"><input type="button" name="btnGuardar" id="btnGuardar" value="Guardar"  class="btnInterfaceBG"/>&nbsp;&nbsp;
						<input type="button" name="btnSalir" id="btnSalir" value="Salir"  class="btnInterfaceBG"/></td>
				</tr>			
			</table>
		</fieldset>
		
		</div>
	</form>	
</body>
</html>