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
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CUENTAS_BANCARIAS_BENEFICIARIOS")){
		mntoCuentas = "SI".equals(usuario.getPropiedad("CUENTAS_BANCARIAS_BENEFICIARIOS").getValor());
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Firmantes</title>

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
			$( "#aTab1" ).attr("disabled", true);
			querySelectPost("mCatalogoUnidadEjecutoraBusca", "UnidadResponsable", {async : false});

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
					if (validaDatos()){
						alert("Registro del firmante realizado");
						if(document.getElementById("lHabilitadochk").checked){
							$("#lHabilitado").val(1);
						}else{
							$("#lHabilitado").val(0);
						}
						
						queryFormPost("mCatalogoFirmanteCreate",  {async : false});
										
						location.href ="CatalogoFirmantes.jsp?tab=0&cIdUnidadEjecutora="+$("#unidad").val()+"&nIdFirmante="+$("#nIdFirmante").val();
					}
			});			
			
		
			$("#btnSalir").button().click(function() {
					location.href = 'Proveedores.jsp?tab=0';				
			});
		});
		
		
		
		function generaIdFirmante(){
			$("#unidad").val($("#UnidadResponsable").val());
			queryFormPost("mGeneraIdFirmante",  {async : false});
			$("#nIdFirmante").val($("#auxiliar").val());
		}
		
		function validaDatos(){
		 var valido=true;
			if($("#UnidadResponsable").val()=="" || $("#UnidadResponsable").val()=="*"){
				alert("Debe elegir una unidad ejecutora");
				valido=false;
				return valido;
			}
			if($("#cNombre").val()==""){
				alert("Debe ingresar el nombre del firmante");
				valido=false;
				return valido;
			}
			
			if($("#cNombre").val()==""){
				alert("Debe ingresar el nombre del firmante");
				valido=false;
				return valido;
				
			}
			if($("#cPaterno").val()==""){
				alert("Debe ingresar el apellido paterno del firmante");
				valido=false;
				return valido;
			}
			
			if($("#cPuesto").val()==""){
				alert("Debe ingresar el puesto del firmante");
				valido=false;
				return valido;
			}
			return valido;
		}
	</script>
</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form>			
		<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora"  value="<%=usuario.getU_UR() %>"/>	
		<input type="hidden" name="consecutivoNum" id="consecutivoNum"  value=""/>
		<input type="hidden" name="unidad" id="unidad"  value=""/>
		<input type="hidden" name="auxiliar" id="auxiliar"/>
		<input type="hidden" name="lHabilitado" id="lHabilitado"/>
				
		
		<div id="container" class="container">				
		<fieldset>
			<legend>Datos Generales:</legend>
			<table align="left">
				<tr id="trcUnidadResponsable" align="left">
					<td>Unidad Administrativa:</td>
					<td>
					<select id="UnidadResponsable" name="UnidadResponsable" style="width: 40em;"  onchange="generaIdFirmante();"></select>
					</td>
				</tr>
				<tr align="left">
					<td>Clave:</td>
					<td><input type="text" name="nIdFirmante" id="nIdFirmante" readonly style="border-width:0; background-color:transparent"/></td>
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
					<td><input type="checkbox" style="width: 20px" name="lHabilitadochk" id="lHabilitadochk" maxlength="2"/></td>
				</tr>
				<tr align="left">
					<td colspan="2"><label class="validateTips ui-state-error" ></label></td>
				</tr>
				<tr align="center">
					<td colspan="2"><input type="button" name="btnGuardar" id="btnGuardar" value="Guardar"  />&nbsp;&nbsp;
						<input type="button" name="btnSalir" id="btnSalir" value="Salir"  /></td>
				</tr>
			</table>
		</fieldset>
		</div>
	</form>
</body>
</html>