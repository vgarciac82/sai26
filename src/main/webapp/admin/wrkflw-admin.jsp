<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="com.syc.gestion.servlet.*" %>
<%@page import="com.syc.gestion.core.*"%>
<html>
<%	
	Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (u == null) {
			response.sendRedirect("../index.jsp");
			return;
	}

	String opc_tab = (String) request.getParameter("opc_tab");
%>
  <head>
    <title>Mantenimiento Tablas</title>
    
	<link rel="stylesheet" type="text/css" href="../css/gestion.css">
	<link rel="stylesheet" href="../css/catalogo/ayuda.css" type="text/css" />
	<link rel="stylesheet" href="../css/scrolltable.css" type="text/css"></link>
	<link rel="stylesheet" href="../Ayudas/css/autocompleta.css" type="text/css"></link>
	<style type="text/css">
	div.tblContainer {
		clear: both;
		overflow: auto;
		border-color: #0c6d3d;
		border-style: solid;
		border-width: 1px;
	}
	
	.Estilo8 {
		font-family: Verdana, Arial, Helvetica, sans-serif;
		font-size: 10pt;
		bgcolor: #123456
	}
	
	.Etq {
		font-weight: normal;
		padding: 4px 3px;
		text-align: right;
		width: 125px;
		font-family: Verdana, Tahoma;
		font-size: 12px;
	}
	
	.Tabs {
		font-weight: bold;
		font-family: Verdana, Tahoma;
		font-size: 10px;
	}
	</style>
	
	<script type="text/javascript" src="../js/ajax-commons.js"></script>
	<script type="text/javascript" src="../js/jsquery.js"></script>
	<script type="text/javascript" src="../js/jquery-1.2.6.js"></script>

	<!--script type="text/javascript" src="../js/catalogo/jquery.js"></script-->
	<!--script type="text/javascript" src="../js/catalogo/jquery.tablesorter.js"></script-->
	<script type="text/javascript" src="../js/jquery.tablesorter.min.js"></script>
	<script type="text/javascript" src="../js/catalogo/ayudas.js"></script>
	<script type="text/javascript" src="../js/catalogo/ayudas.reglas.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>

	<!--script type="text/javascript" src="../Ayudas/js/jquery.js"></script-->
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

	<!-- script type="text/javascript" src="js/md5.js"></script-->
	<script type="text/javascript" src="js/wrkflwadmin.js"></script>
  
	<script type="text/javascript" >
		$(document).ready(
			function() {
				$("input.AyudaSyC").subIniciaDlg();	
				$("input.autoCompletaSyC").subIniciaAutoCompleta();
				//$("input.txtAyuda").subInicia();		
			}
		);
  </script>  
  </head>
  <body onload="javascript:opcTab(<%=opc_tab%>);">
	<div id="divCatalogos" class="tblContainer" style="top: 10; overflow: auto; width: 98%;">
		<!-- font face="Verdana" size="2"><b>Ficha de captura</b></font><br-->
		<!-- table border="0" width="100%">
			<tr>
				<td width="85%"><span id="msg" class="Estilo8"></span></td>
				<td width="5%"><input type="button" id="cmdAgrega" name="cmdAgrega" value="Agregar" onclick="accion(0);" /></td>
				<td width="5%"><input type="button" id="cmdActualiza" name="cmdActualiza" value="Actualizar" onclick="accion(1)" /></td>
				<td width="5%"><input type="button" id="cmdElimina" name="cmdElimina" value="Eliminar" onclick="accion(2)" /></td>
			</tr>
		</table-->
		<div id="divCapturaUsr" class="tableContainer" style="overflow: auto; height: 86%; width: 100%;" align="center">
			<table width="45%" cellspacing="2" cellpadding="2">
				<tr>
					<td colspan="2">
						<fieldset>
							<legend>&nbsp;Usuarios&nbsp;</legend>
							<table border="1" cellspacing="0" cellpadding="2">
								<tr>	
									<td class="Etq" nowrap><font face="Verdana" size="-1">Id de Usuario:</font></td>
									<td>
										<table border="0">
											<tr>
												<td width="5%"><input name="txtIdUsuario" id="txtIdUsuario" type="text"  value="" size="36" maxlength="32"></td>
												<td align="left" nowrap><div id="divIdUsuario" align="left"></div></td>
											</tr>
										</table>	
									</td>
								</tr>
								<tr>	
									<td class="Etq">Contraseña:</td>
									<td>
										<table border="0">
											<tr>
												<td width="5%"><input name="txtPwd" id="txtPwd" type="text" value="" size="36" maxlength="32"></td>
												<td width="100px" align="left"><div id="divPwd" align="left"></div></td>
												<td nowrap><input name="chkResetPwd" id="chkResetPwd" type="checkbox" value="Reiniciar Contraseña" onclick="resetContrasena()">Reiniciar Contraseña</td>
											</tr>
										</table>
									</td>			
								</tr>
								<tr>	
									<td class="Etq">Saludo:</td>
									<td>
										<table border="0">
											<tr>
												<td width="30%"><input name="txtSaludo" id="txtSaludo" type="text" value="" size="10" maxlength="10"></td>
												<td align="left"><div id="divSaludo" align="left"></div></td>
											</tr>
										</table>
									</td>										
								</tr>
								<tr>	
									<td class="Etq">Apellido Paterno:</td>
									<td>
										<table border="0">
											<tr>
												<td width="30%"><input name="txtAPaterno" id="txtAPaterno" type="text" value="" size="40" maxlength="40"></td>
												<td align="left"><div id="divAPaterno" align="left"></div></td>
											</tr>
										</table>
									</td>										
								</tr>
								<tr>	
									<td class="Etq">Apellido Materno:</td>
									<td>
										<table border="0">
											<tr>
												<td width="30%"><input name="txtAMaterno" id="txtAMaterno" type="text" value="" size="40" maxlength="40"></td>
												<td align="left"><div id="divAMaterno" align="left"></div></td>
											</tr>
										</table>
									</td>										
								</tr>
								<tr>	
									<td class="Etq">Nombre(s):</td>
									<td>
										<table border="0">
											<tr>
												<td width="30%"><input name="txtNombres" id="txtNombres" type="text" value="" size="40" maxlength="40"></td>
												<td align="left"><div id="divNombres" align="left"></div></td>
											</tr>
										</table>
									</td>										
								</tr>
								<tr>	
									<td class="Etq">Area:</td>
									<td>
										<table border="0">
											<tr>
												<td width="90%"><input id="txtDirigidoA" name="txtDirigidoA" class="txtAyuda" tabla="DirigidoA" filtroadicional="" maxlength="20" size="9"/>
																<input id="lbDirigidoADesc" name="lbDirigidoADesc" class="lbAyudaDesc" value="seleccionar..." size="60" maxlength="100" disabled>
												</td>
												<td align="left"><div id="divDirigidoA" align="left"></div></td>
											</tr>
										</table>		
									</td>							
								</tr>								
								<tr>	
									<td class="Etq">Cargo:</td>
									<td>
										<table border="0">
											<tr>
												<td width="30%"><input name="txtCargo" id="txtCargo" type="text" value="" size="80" maxlength="110"></td>
												<td align="left"><div id="divCargo" align="left"></div></td>
											</tr>
										</table>		
									</td>							
								</tr>								
							</table>
						</fieldset>
					</td>
				</tr>			
			</table>
		</div>
		<div id="divCapturaUsrGrp" class="tableContainer" style="overflow: auto; height: 86%; width: 100%;" align="center">
			<table width="90%" cellspacing="2" cellpadding="2">
				<tr>
					<td colspan="2">
						<fieldset>
							<legend>&nbsp;Mantenimiento Usuarios-Grupos&nbsp;</legend>
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>	
									<td class="Etq" nowrap><font face="Verdana" size="-1">Id de Usuario:</font></td>
									<td>
										<!--  <INPUT name="txtIdUsuarioGrp" id="txtIdUsuarioGrp" TYPE="text" VALUE="" SIZE="32" maxlength="32"> -->
										<input id="txtPersona3" name="txtPersona3"  class="txtAyuda"  tabla="Persona3" maxlength="20" size="20" tabindex="12"/>
									    <input id="lbPersona3Desc" name="lbPersona3Desc" class="lbAyudaDesc" value="seleccionar..." size="37" maxlength="100" disabled tabindex="13"/>
									</td>
								</tr>
								<tr>	
									<td class="Etq">Nombre:</td>
									<td>
										<!--  <INPUT name="txtNombreGrp" id="txtNombreGrp" TYPE="text" VALUE="" size="80" maxlength="80"> -->
										<input id="txtGrupoUsuario" name="txtGrupoUsuario"  class="txtAyuda"  tabla="GrupoUsuario" maxlength="40" size="35" tabindex="12"/>
									    <input id="lbGrupoUsuarioDesc" name="lbGrupoUsuarioDesc" class="lbAyudaDesc" value="seleccionar..." size="37" maxlength="100" disabled tabindex="13"/>
									</td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>			
			</table>
		</div>
		<div id="divCapturaProp" class="tableContainer" style="overflow: auto; height: 86%; width: 100%;" align="center">
			<table width="90%" cellspacing="2" cellpadding="2">
				<tr>
					<td colspan="2">
						<fieldset>
							<legend>&nbsp;Propiedades&nbsp;</legend>
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>	
									<td class="Etq" nowrap><font face="Verdana" size="-1">Id de Usuario:</font></td>
									<td><input name="txtIdUsuarioProp" id="txtIdUsuarioProp" type="text" value="" size="30" maxlength="32"></td>
								</tr>
								<tr>	
									<td class="Etq">Nombre:</td>
									<td><input name="txtNombreProp" id="txtNombreProp" type="text" value="" size="80" maxlength="255"></td>
								</tr>
								<tr>	
									<td class="Etq">Valor:</td>
									<td><input name="txtValorProp" id="txtValorProp" type="text" value="" size="80" maxlength="255"></td>
								</tr>
			
							</table>
						</fieldset>
					</td>
				</tr>			
			</table>
		</div>
		<div id="divCapturaRole" class="tableContainer" style="overflow: auto; height: 86%; width: 100%;" align="center">
			<table width="90%" cellspacing="2" cellpadding="2">
				<tr>
					<td colspan="2">
						<fieldset>
							<legend>&nbsp;Role&nbsp;</legend>
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>	
									<td class="Etq" nowrap><font face="Verdana" size="-1">Id de Usuario:</font></td>
									<td><input name="txtIdUsuarioRole" id="txtIdUsuarioRole" type="text" value="" size="20" maxlength="32"></td>
								</tr>
								<tr>	
									<td class="Etq">Nombre:</td>
									<td><input name="txtNombreRole" id="txtNombreRole" type="text" value="" size="20" maxlength="32"></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>			
			</table>
		</div>
		<div id="divCapturaMttoGrp" class="tableContainer" style="overflow: auto; height: 86%; width: 100%;" align="center">
			<table width="90%" cellspacing="2" cellpadding="2">
				<tr>
					<td colspan="2">
						<fieldset>
							<legend>&nbsp;Mantenimiento a Grupos&nbsp;</legend>
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>	
									<td class="Etq" nowrap><font face="Verdana" size="-1">Nombre Grupo:</font></td>
									<td><input name="txtNombreGrupo" id="txtNombreGrupo" type="text" value="" size="32" maxlength="32"></td>
								</tr>
								<tr>	
									<td class="Etq">Descripción:</td>
									<td><input name="txtDescripcionGrupo" id="txtDescripcionGrupo" type="text" value="" size="80" maxlength="80"></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>			
			</table>
		</div>
		<div id="divCapturaMttoGrpProp" class="tableContainer" style="overflow: auto; height: 86%; width: 100%;" align="center">
			<table width="90%" cellspacing="2" cellpadding="2">
				<tr>
					<td colspan="2">
						<fieldset>
							<legend>&nbsp;Mantenimiento a Grupos - Propiedades&nbsp;</legend>
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>	
									<td class="Etq" nowrap><font face="Verdana" size="-1">Nombre Grupo:</font></td>
									<td><input name="txtNombreGrupoPropG" id="txtNombreGrupoPropG" type="text" value="" size="32" maxlength="32"></td>
								</tr>
								<tr>	
									<td class="Etq" nowrap><font face="Verdana" size="-1">Nombre Propiedad:</font></td>
									<td><input name="txtNombrePropiedadPropG" id="txtNombrePropiedadPropG" type="text" value="" size="32" maxlength="32"></td>
								</tr>
								<tr>	
									<td class="Etq">Descripción:</td>
									<td><input name="txtValorPropiedadGrupoPropG" id="txtValorPropiedadGrupoPropG" type="text" value="" size="80" maxlength="80"></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>			
			</table>
		</div>
		<div id="divCapturaMttoRole" class="tableContainer" style="overflow: auto; height: 86%; width: 100%;" align="center">
			<table width="90%" cellspacing="2" cellpadding="2">
				<tr>
					<td colspan="2">
						<fieldset>
							<legend>&nbsp;Mantenimiento a Roles&nbsp;</legend>
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>	
									<td class="Etq" nowrap><font face="Verdana" size="-1">Nombre Role:</font></td>
									<td><input name="txtMNombreRole" id="txtMNombreRole" type="text" value="" size="32" maxlength="32"></td>
								</tr>
								<tr>	
									<td class="Etq">Descripcion:</td>
									<td><input name="txtDescripcionRole" id="txtDescripcionRole" type="text" value="" size="80" maxlength="80"></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>			
			</table>
		</div>
		<div id="divCapturaAreas" class="tableContainer" style="overflow: auto; height: 86%; width: 100%;" align="center">
			<table width="90%" cellspacing="2" cellpadding="2">
				<tr>
					<td colspan="2">
						<fieldset>
							<legend>&nbsp;Mantenimiento a Areas&nbsp;</legend>
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>	
									<td width="20%" nowrap align="right"><font face="Verdana" size="-1">Clave Area:</font></td>
									<td><input name="txtMClaveArea" id="txtMClaveArea" type="text" value="" size="20" maxlength="20"></td>
								</tr>
								<tr>	
									<td nowrap align="right">Descripcion Area:</td>
									<td><input name="txtMDescripcionArea" id="txtMDescripcionArea" type="text" value="" size="80" maxlength="100"></td>
								</tr>
								<tr>	
									<td nowrap align="right">Tipo Area:</td>
									<td>
										<select id="txtTipoArea" name="txtTipoArea" length="2">
											<option value="" >&nbsp;&nbsp;</option>
											<option value="0" >Interna</option>
											<option value="1" >Externa</option> 
										</select>
									</td>
								</tr>
								<tr>	
									<td nowrap align="right"><font face="Verdana" size="-1">Prefijo del Folio:</font></td>
									<td><input name="txtMPrefijoFolio" id="txtMPrefijoFolio" type="text" value="" size="15" maxlength="15"></td>
								</tr>
								<tr>
									<td nowrap align="right"><font face="Verdana" size="-1">Area Padre:</font></td>
									<td>
										<!--  
										<input id="txtDirigidoA" name="txtDirigidoA" class="txtAyuda" tabla="DirigidoA" filtroAdicional="" maxLength="20" size="9"/>
										<input id="lbDirigidoADesc" name="lbDirigidoADesc" class="lbAyudaDesc" value="seleccionar..." size="60" maxLength="100" disabled>
										<td align="left"><div id="divDirigidoA2" align="left"></div></td>
										-->
										<input id="txtAreaPadre" 	name="txtAreaPadre" 	class="txtAyuda" 	tabla="AreaPadre" filtroadicional="" maxlength="20" size="9"/>
										<input id="lbAreaPadreDesc"	name="lbAreaPadreDesc"	class="lbAyudaDesc" value="seleccionar..." size="60" maxlength="100" disabled/>
									</td>
								</tr>
								<tr>	
									<td nowrap align="right">Bandeja Compartida de Entrada:</td>
									<td>
										<select id="txtBandejaCompartidaEntrada" name="txtBandejaCompartidaEntrada" length="2">
											<option value="" >&nbsp;&nbsp;</option>
											<option value="N" >No</option>
											<option value="S">Si</option> 
										</select>
									</td>
								</tr>
								<tr>	
									<td nowrap align="right">Bandeja Compartida de Salida:</td>
									<td>
										<select id="txtBandejaCompartidaSalida" name="txtBandejaCompartidaSalida" length="2">
											<option value="" >&nbsp;&nbsp;</option>
											<option value="N" >No</option>
											<option value="S">Si</option> 
										</select>
									</td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>			
			</table>
		</div>
		<div id="divUCopyMove" class="tableContainer" style="overflow: auto; height: 86%; width: 100%;" align="center">
			<table width="90%" cellspacing="2" cellpadding="2">
				<tr>
					<td colspan="2">
						<fieldset>
							<legend>&nbsp;USUARIO Copiar / Mover&nbsp;</legend>
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>	
									<td class="Etq" nowrap><font face="Verdana" size="-1">Nuevo usuario:</font></td>
									<td><input name="txtUCMTo" id="txtUCMTo" type="text" value="" size="32" maxlength="32" class="AyudaSyC"></td>
								</tr>
								<tr>	
									<td class="Etq">Usuario Existente:</td>
									<td><input name="txtUCMFrom" id="txtUCMFrom" type="text" value="" size="32" maxlength="32" class="AyudaSyC"></td>
								</tr>
								<tr>	
									<td class="Etq">Mover informacion:</td>
									<td><input name="chkUCMInfo" id="chkUCMInfo" type="checkbox" value="Mover información" checked></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>			
			</table>
		</div>
		
		<table border="0" width="100%">
			<tr>
				<td width="85%"></td>
				<td width="5%"><input type="button" id="cmdAgrega1"    name="cmdAgrega1"     value="Agregar"    onclick="accion(0);" /></td>
				<td width="5%"><input type="button" id="cmdActualiza1" name="cmdAactualiza1" value="Actualizar" onclick="accion(1);" /></td>
				<td width="5%"><input type="button" id="cmdElimina1"   name="cmdElimina1"    value="Eliminar"   onclick="accion(2);" /></td>
				<!--  td width="5%"><input type="button" id="cmdCancelar"   name="cmdCancelar"    value="Cancelar"   onclick="accion(3);" /></td -->
				<td width="5%"><input type="button" id="cmdCancelar"   name="cmdCancelar"    value="Filtro"   onclick="accion(3);" /></td>
			</tr>
		</table>
		<div id="divListaDatos" class="tblContainer" style="top: 10; height: 260px; width: 100%; display: none;">
			<!-- font face="Verdana" size="2"><b>Datos CheckList</b></font><br-->
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<!-- tr>
					<td colspan="2">Filtro:
						<select id="tc_Caso" onchange="getGrupos();">
						   <option value="-1" selected="selected">&lt;Seleccione un Caso&gt;</option>
			                  <option value=""></option>
			    
						</select>
						<select id="g_grupos" onchange="obtenOperacionesPorTipoCaso();">
							<option value ="">&lt;Seleccione un Grupo&gt;</option>
						</select>
						<select id="id_oper">
							<option value ="">&lt;Seleccione una Operación&gt;</option>
							<option value="">Apertura de crédito Persona Moral</option>
						</select>
						<a href="javascript:consultarFiltro()">Buscar</a>
					<br><br><br><br><br><br><br><br><br></td>	
				</tr-->
				<tr>
					<td><font face="Verdana" size="-1"><b>Detalle</b></font><span id="msg" class="Estilo8"></span></td>
					<td align="right"><div id="numPagActual">Página Actual: 0</div></td>
				</tr>
			</table>
			<div id="tableContainer" class="tableContainer" style="overflow: auto; height: 200px; width: 100%;">
		    	<span class="Estilo8">No hay información para mostrar</span>
			</div>
			<table border="0" width="100%">
				<tr>
					<td valign="top">
						<span><strong>Total de registros:&nbsp;</strong></span>&nbsp;&nbsp;<span id="totalRegistros"></span>
					</td>
					<td align="center" valign="top">
						<span id="totalPaginas">Total de Páginas : 0</span>			
					</td>
					<td align="right"><a href="javascript:getPrimeraPagina();">Primera</a>&nbsp;|&nbsp;<a href="javascript:getAnteriorPagina();">Anterior</a>&nbsp;-&nbsp;<a href="javascript:getSiguientePagina();">Siguiente</a>&nbsp;|&nbsp;<a href="javascript:getUltimaPagina();">Ultima</a>
					</td>	
				</tr>
			</table>
		</div>
	</div>
  </body>
</html>
