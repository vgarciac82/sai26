<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
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
		<title>Cat&aacute;logo de Firmantes</title>

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
			$( "#aTab1" ).attr("disabled", false);
			$( "#aTab2" ).attr("disabled", true);
			if($("#cIdUnidadEjecutora").val() != null &&$("#cIdUnidadEjecutora").val() != "null"&&$("#nIdFirmante").val() != null &&$("#nIdFirmante").val() != "null"){		
				queryFormPost("mCatalogoFirmante", {async : false});
				querySelectPost("mCatalogoEntidadFederativaEditarRead", "cIdEntidadFederativaF", {async : false});
			}
		
			$( "#dialog:ui-dialog" ).dialog( "destroy" );
			var 
				cNombre = $( "#cNombre" ),
				cPaterno = $( "#cPaterno" ),
				cMaterno = $( "#cMaterno" ),
				cPuesto = $( "#cPuesto" ),
				lHabilitado = $( "#lHabilitado" ),
				
				allFields = $( [] ).add( cNombre ).add( cPaterno ).add( cMaterno ).add( cPuesto ).add( lHabilitado ),
				tips = $( ".validateTips" );
			function updateTipsDlg( t ) {
				tips
					.text( t );
					alert(t);
			}

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
			
			$("#btnGuardar").button().click(function() {
					var bValid = true;
						tips.text("");
						allFields.removeClass( "ui-state-error" );
						
						bValid = bValid && checkRequerido( cNombre, "cNombre" );
						bValid = bValid && checkLength( cNombre, "cNombre", 3, 50 );
						
						bValid = bValid && checkRequerido( cPaterno, "cPaterno" );
						bValid = bValid && checkLength( cPaterno, "cPaterno", 3, 50 );
						
						bValid = bValid && checkRequerido( cMaterno, "cMaterno" );
						bValid = bValid && checkLength( cMaterno, "cMaterno", 3, 50 );
						
						bValid = bValid && checkRequerido( cPuesto, "cPuesto" );
						bValid = bValid && checkLength( cPuesto, "cPuesto", 3, 100 );
						
						bValid = bValid && checkRequerido( lHabilitado, "lHabilitado" );
		
					if (bValid){						
						queryFormPost("mCatalogoFirmantesUpdate",  {async : false});		
						alert("Registro Actualizado Firmante");
						location.href ="CatalogoFirmantes.jsp?tab=1&cIdUnidadEjecutora="+$("#cIdUnidadEjecutora").val()+"&nIdFirmante="+$("#nIdFirmante").val(); 
							
					}
			});			
			
			$("#btnSalir")
				.button()
				.click(function() {
					location.href = 'CatalogoFirmantes.jsp?tab=0';				
			});			
		
		});
		
		
	</script>
</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form id="proveedor" name="proveedor">			
			
		
		<input type="hidden" name="h_habilitado" id="h_habilitado"  value=""/>
		<input type="hidden" name="cmd" id="cmd"  value=""/>
		<input type="hidden" name="nIdTelefono" id="nIdTelefono"  value=""/>
		
		<div id="container" class="container">				
		<fieldset>
			<legend>Datos Generales:</legend>
			<table align="left">
				<tr align="left">
					<td>Unidad Ejecutora:</td>
					<td><input type="text" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora"  value="<%=request.getParameter("cIdUnidadEjecutora")%>" readonly style="border-width:0; background-color:transparent"/></td>
				</tr>			
				<tr align="left">
					<td>Clave:</td>
					<td><input type="text" name="nIdFirmante" id="nIdFirmante"  value="<%=request.getParameter("nIdFirmante")%>" readonly style="border-width:0; background-color:transparent" /></td>
				</tr>
				<tr align="left">
					<td>Nombre:</td>
					<td><input type="text" style="width: 500px" name="cNombre" id="cNombre" maxlength="50" /></td>
				</tr>
				<tr align="left">
					<td>Apellido Paterno:</td>
					<td><input type="text" style="width: 500px" name="cPaterno" id="cPaterno" maxlength="50"/></td>
				</tr>			
				<tr align="left">
					<td>Apellido Materno:</td>
					<td><input type="text" style="width: 500px" name="cMaterno" id="cMaterno" maxlength="50"/></td>
				</tr>
				<tr align="left">
					<td>Puesto:</td>
					<td><input type="text" style="width: 500px" name="cPuesto" id="cPuesto" maxlength="100"/></td>
				</tr>
				<tr align="left">
					<td>Habilitado:</td>
					<td><input type="text" style="width: 20px" name="lHabilitado" id="lHabilitado" maxlength="2"/></td>
				</tr>
				<tr align="left">
					<td colspan="2"><label class="validateTips ui-state-error" ></label></br></td>
				</tr>
				<tr align="center">
					<td colspan="2"><input type="button" name="btnGuardar" id="btnGuardar" value="Guardar"  />&nbsp;&nbsp;
						<input type="button" name="btnSalir" id="btnSalir" value="Salir"  /></td>
				</tr>
			</table>
		</fieldset>
		<!-- <fieldset>
			<legend>Direcci&oacute;n:</legend>
			<table align="left">
				<tr align="left">
					<td>Estado:</td>
					<td><select id="cIdEntidadFederativaF" name="cIdEntidadFederativaF" style="width: 30em;"></select></td>
				</tr>	
				<tr align="left">
					<td>Calle:</td>
					<td><input type="text" name="cCalleF" id="cCalleF" style="width: 30em;" maxlength="300"/></td>
				</tr>			
				<tr align="left">
					<td>N&uacute;mero Externo:</td>
					<td><input type="text" name="cNumExtF" id="cNumExtF" style="width: 30em;" maxlength="15"/></td>
				</tr>
				<tr align="left">
					<td>N&uacute;mero Interno:</td>
					<td><input type="text" name="cNumIntF" id="cNumIntF" style="width: 30em;" maxlength="15"/></td>
				</tr>
				<tr align="left">
					<td>Colonia:</td>
					<td><input type="text" name="cColoniaF" id="cColoniaF" style="width: 30em;" maxlength="200"/></td>
				</tr>
				<tr align="left">
					<td>Delegaci&oacute;n &oacute; Municipio:</td>
					<td><input type="text" name="cDelMpioF" id="cDelMpioF" style="width: 30em;" maxlength="100"/></td>
				</tr>
				<tr align="left">
					<td>C&oacute;digo Postal:</td>
					<td><input type="text" name="cCodigoPostalF" id="cCodigoPostalF" style="width: 30em;" maxlength="10"/></td>
				</tr>	
				<tr align="left">
					<td>Ciudad:</td>
					<td><input type="text" name="cCiudadF" id="cCiudadF" style="width: 30em;" maxlength="200"/></td>
				</tr>	
				<tr align="left">
					<td>Estado:</td>
					<td><input type="text" name="cEstadoF" id="cEstadoF" style="width: 30em;" maxlength="200"/></td>
				</tr>				
			</table>
		</fieldset> -->
		<!-- <fieldset>
			<legend>Tel&eacute;fono </legend>
			<table align="left">
				<tr align="left">
					<td>Telefono </td>
					<td><select id="cTipoTelefono" name="cTipoTelefono" style="width: 10em;"></select>&nbsp;
					<input type="text" name="cTelefono" id="cTelefono" style="width: 20em;" maxlength="30"/>
					</td>
				</tr>
				<tr align="center">
					<td colspan="2">
					<table id="tblTelefonoFirmantes" class="display" align="center">
									            <thead>
									                <tr>									                
									                	<th>Num. Telefono</th>
									                    <th>Tipo</th>
									                    <th>Telefono</th>
									                     <th>Eliminar</th>
									                </tr>
									            </thead>
									        </table>
					</td>
				</tr>
				
				
				
															
			</table>
		</fieldset> -->
		</div>
	</form>	
</body>
</html>