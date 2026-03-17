<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	/*	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	 if (usuario == null) {
	 response.sendRedirect("../index.jsp");
	 return;
	 }*/
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>

		<title>Programa Anual de Adquisiciones, -Arrendamientos y Servicios</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
		<style type="text/css" title="currentStyle">
			@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../css/demo_table_jui.css";
			@import "../css/demo_page.css";
		</style>

		<style>			// estilos del dialogo
			div#dialog-form fieldset { padding:0; border:0; margin-top:25px; display:none; }
			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
		</style>
				<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.core"></script>
		<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
				
		<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>

		<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
		querySelectPost("mCatalogoCapituloRead", "mCatalogoCapitulo", {async : false});
		querySelectPost("mCatalogoSubPartidaRead", "mCatalogoSubPartida", {async : false});
		querySelectPost("fn_mProgramaAnualMontosPorCapitulomMontoC2Read", "fn_mProgramaAnualMontosPorCapitulomMontoC2", {async : false});
		querySelectPost("fn_mProgramaAnualMontosPorCapitulomMontoC3Read", "fn_mProgramaAnualMontosPorCapitulomMontoC3", {async : false});
		querySelectPost("fn_mProgramaAnualMontosPorCapitulomMontoC5Read", "fn_mProgramaAnualMontosPorCapitulomMontoC5", {async : false});
		querySelectPost("fn_mProgramaAnualMontosPorCapituloTotalRead", "fn_mProgramaAnualMontosPorCapituloTotal", {async : false});
		$( "#chkCAMBS" )
			.change(function() {if ($(chkCAMBS).is(':checked')) $("#trCUCOP").css("visibility","visible"); else $("#trCUCOP").css("visibility","hidden");});

		$( "#chkdescripcionCAMBS" )
			.change(function() {if ($(chkCAMBS).is(':checked')) $("#trDescripcion").css("visibility","visible"); else $("#trDescripcion").css("visibility","hidden");});
		
		$( "#chktipoProceso" )
			.change(function() {if ($(chkCAMBS).is(':checked')) $("#trtipoProceso").css("visibility","visible"); else $("#trtipoProceso").css("visibility","hidden");});						
});
</script>		
	</head>
	<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">

		<table width="80%" height="83" border="0">
			<tr>
				<td width="110" align="left">
					<input name="chkCAMBS" 			id="chkCAMBS" 			  type="checkbox"   />CAMBS</td>
				<td width="120" align="left">
					<input  name="chkdescripcionCAMBS" id="chkdescripcionCAMBS" type="checkbox"  />Descripción</td>
				<td width="130" align="left">
					<input  name="chktipoProceso" 		id="chktipoProceso" 	  type="checkbox" />Tipo Proceso</td>
			</tr>
		</table>
		
			<fieldset>
				<table border="1">
					<tr>
						<td align="left">
							Capítulo:
						</td>
						<td>
							<select id="mCatalogoCapitulo" name="mCatalogoCapitulo"
								style="width: 50em;">
							</select>
						</td>
					</tr>
					<tr>
						<td align="left">
							Partida:
						</td>
						<td align="left">
							<select id="mCatalogoSubPartida" name="mCatalogoSubPartida"
								style="width: 50em;">
							</select>
						</td>
					</tr>
					<tr id="trCUCOP" name="trCUCOP" style="visibility:hidden">
						<td align="right">
							CUCOPS:
						</td>
						<td align="left">
							 <input type="text" name="textfield" value="Inserte" />
						</td>
					</tr>
					<tr id="trDescripcion" name="trDescripcion" style="visibility:hidden"> 
						<td align="right">
							Descripción:
						</td>
						<td align="left">
							<input type="text" name="textfield" value="Inserte" />
						</td>
					</tr>
					<tr id="trtipoProceso" name="trtipoProceso" style="visibility:hidden">
						<td align="right">
							Tipo Proceso:
						</td>
						<td align="left">
							<select id="mCatalogoSubPartida" name="mCatalogoSubPartida"
								style="width: 50em;">
							</select>
						</td>
					</tr>
				</table>
			</fieldset>
	
		<table width="80%" height="43" border="0">
			<tr>
				<td width="130">
					Cap&iacute;tulo 2000:
				</td>
				<td width="130" id="fn_mProgramaAnualMontosPorCapitulomMontoC2" name="fn_mProgramaAnualMontosPorCapitulomMontoC2">
					
				</td>
				<td width="130">
					Cap&iacute;tulo 3000:
				</td>
				<td width="130" id="fn_mProgramaAnualMontosPorCapitulomMontoC3" name="fn_mProgramaAnualMontosPorCapitulomMontoC3">
					
				</td>
				<td width="130">
					Cap&iacute;tulo 5000:
				</td>
				<td width="130"  id="fn_mProgramaAnualMontosPorCapitulomMontoC5" name="fn_mProgramaAnualMontosPorCapitulomMontoC5">
					
				</td>
				<td width="130">
					Total:
				</td>
				<td width="130" id="fn_mProgramaAnualMontosPorCapituloTotal" name="fn_mProgramaAnualMontosPorCapituloTotal">
					
				</td>
			</tr>
		</table>
		<table>
			<tr>
				<td align="right">
					<input id="btn_buscar_urm" name="btn_buscar_urm" type="button"
						value="Buscar" />
				</td>

				<td align="left">
					<input id="btn_nuevo_urm" name="btn_nuevo_urm" type="button"
						value="Nuevo" />
				</td>
			</tr>
			<tr>
				<td align="right">
					 IVA:
          <input type="text" name="textfield" />
				</td>

				<td align="left">

				</td>
			</tr>
		</table>

	</body>
</html>
