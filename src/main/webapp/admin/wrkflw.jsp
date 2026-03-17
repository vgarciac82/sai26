<%@ page import="
		java.util.Map,
		java.util.Iterator,
		com.syc.gestion.CasoBusinessLogic,
		com.syc.gestion.core.Usuario,
		com.syc.gestion.core.TipoCaso,
		com.syc.fortimax.core.Aplicacion,
		com.syc.gestion.servlet.GestionInterface"%>
<%
	Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (u == null) {
			response.sendRedirect("../index.jsp");
			return;
	}
	CasoBusinessLogic cbl = new CasoBusinessLogic("jdbc/gestion");
	String id_tc = request.getParameter("id_tc");
	String mode = request.getParameter("mode");
	String onLoad = ((id_tc != null) ? "onload=\"consultaCaso(" + id_tc + ", false);" : "");
	if (mode != null) onLoad += "setTimeout('" + mode + "',1000);";
	if (onLoad.length() > 0) onLoad += "\"";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>Casos de Gestión</title>
		<link rel="stylesheet" type="text/css" href="../css/gestion.css">
		<link type="text/css" href="../css/scrolltable.css" rel="stylesheet">
		<link type="text/css" href="../css/shadow.css" rel="stylesheet">
		<script type="text/javascript" src="../js/dojo.js"></script>
		<script type="text/javascript" src="../js/wrkflw-utils.js"></script>
		<script type="text/javascript" src="../js/wrkflw.js"></script>
		<style type="text/css">
		html body {
			margin: 0px;
			padding: 0px;
			border: 0px;
			background-color: #f0f0f0;
			overflow: hidden;
		}
		fieldset {
			margin: 1px;
			padding: 1px;
		}
		</style>
	</head>
	<body <%=onLoad%>>
		<div class="shadow caso-var" id="caso-var">
			<div>
				<table id="tbl-shadow">
					<tr>
						<td>
							<form id="form-var" onSubmit="save(this); return false;" method="post" enctype="application/x-www-form-urlencoded">
								<input type="hidden" id="edv_id_tc" name="id_tc">
								<input type="hidden" id="edv_id_tcv" name="id_tcv">
								<fieldset>
									<legend>&nbsp;Variable&nbsp;</legend>
									<table>
										<tr>
											<td align="right">Nombre:</td>
											<td><input id="edv_tcv_nombre" name="tcv_nombre" type="text" size="16" maxlength="16"></td>
											<td align="right">Etiqueta:</td>
											<td><input id="edv_tcv_etiqueta" name="tcv_etiqueta" type="text" size="25" maxlength="25"></td>
										</tr>
										<tr>
											<td align="right">Tipo:</td>
											<td>
												<input id="shw_tcv_tipo" type="text" size="13" maxlength="13" readonly>
												<select id="edv_tcv_tipo" name="tcv_tipo">
													<option value="3">Small Integer</option>
													<option value="4">Long Integer</option>
													<option value="5">Decimal</option>
													<option value="7">Double, Float</option>
													<option value="8">Fecha</option>
													<option value="10" selected="selected">String</option>
													<option value="12">Long String</option>
												</select>
											</td>
											<td align="right">Longitud:</td>
											<td><input id="edv_tcv_longitud" name="tcv_longitud" type="text" size="5" maxlength="5"></td>
										</tr>
										<tr>
											<td>En Gaveta:</td>
											<td><input type="checkbox" id="edv_tcv_en_gaveta" name="tcv_en_gaveta" onclick="this.value=this.checked?'S':'N';return true;"></td>
											<td align="right">Descripci&oacute;n:</td>
											<td><input id="edv_tcv_descripcion" name="tcv_descripcion" type="text" size="40" maxlength="80"></td>
										</tr>
										<tr>
											<td align="right" colspan="4">
												<table>
													<tr>
														<td><input id="pb_tcv_ok" type="reset" value="Aceptar" onclick="return actionVar(true)"></td>
														<td><input id="pb_tcv_cancel" type="reset" value="Cancelar" onclick="return actionVar(false)"></td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</fieldset>
							</form>
						</td>
					</tr>
				</table>
			</div>
		</div>
		
		<div id="caso-oper" class="shadow caso-oper">
			<div>
				<table id="tbl-shadow">
					<tr>
						<td>
							<form id="form-oper" onSubmit="save(this); return false;" method="post" enctype="application/x-www-form-urlencoded">
								<input type="hidden" id="edo_id_tc" name="id_tc">
								<input type="hidden" id="edo_id_oper" name="id_oper">
								<fieldset>
									<legend>&nbsp;Operaci&oacute;n&nbsp;Caso&nbsp;</legend>
									<table>
										<tr>
											<td align="right">Línea:</td>
											<td><input id="edo_o_numero" name="o_numero" type="text" size="7" maxlength="7"></td>
											<td align="right">Nombre:</td>
											<td><input id="edo_o_nombre" name="o_nombre" type="text" size="20" maxlength="20"></td>
										</tr>
										<tr>
											<td align="right">Responsable:</td>
											<td><input id="edo_o_responsable" name="o_responsable" type="text" size="32" maxlength="32"></td>
											<td align="right">Descripci&oacute;n:</td>
											<td><input id="edo_o_descripcion" name="o_descripcion" type="text" size="40" maxlength="80"></td>
										</tr>
										<tr>
											<td align="right">Plantilla:</td>
											<td><input id="edo_o_plantilla" name="o_plantilla" type="text" size="40" maxlength="255"></td>
											<td align="right">Tiempo Límite:<br><font size="-2"><i>(-1 indefinido)</i></font></td>
											<td><input id="edo_o_tiempo_limite" name="o_tiempo_limite" type="text" size="7" maxlength="7">&nbsp;segundos</td>
										</tr>
										<tr>
											<td align="right" valign="top">Filtro:<br><font size="-2"><i>carpetas y/o documentos</i></font></td>
											<td><textarea id="edo_o_folder_docto" name="o_folder_docto" cols="30" rows="5" style="overflow: auto;"></textarea></td>
											<td align="right" valign="top">Alarma:</td>
											<td><textarea id="edo_o_alarma" name="o_alarma" cols="30" rows="5" style="overflow: auto;"></textarea></td>
										</tr>
										<tr>
											<td align="right" valign="top">On-Load:<br><font size="-2"><i>(javascript)</i></font></td>
											<td><textarea id="edo_o_on_load" name="o_on_load" cols="30" rows="5" style="overflow: auto;"></textarea></td>
											<td align="right" valign="top">Post-Display:<br><font size="-2"><i>(javascript)</i></font></td>
											<td><textarea id="edo_o_post_display" name="o_post_display" cols="30" rows="5" style="overflow: auto;"></textarea></td>
										</tr>
										<tr>
											<td align="right" valign="top">On-Submit:<br><font size="-2"><i>(javascript)</i></font></td>
											<td valign="top"><textarea id="edo_o_on_submit" name="o_on_submit" cols="30" rows="5" style="overflow: auto;"></textarea></td>
											<td colspan="2">
												<iframe id="tbl-resp-oper" src="wrkflw-oper-resp.jsp" scrolling="auto" width="100%" height="101%" frameborder="0" marginheight="0" marginwidth="0" style="padding: 0px;"></iframe>
											</td>
										</tr>
										<tr>
											<td align="right" colspan="4">
												<table>
													<tr>
														<td><input id="pb_o_ok" type="reset" value="Aceptar" onclick="return actionOper(true)"></td>
														<td><input id="pb_o_cancel" type="reset" value="Cancelar" onclick="return actionOper(false)"></td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</fieldset>
							</form>
						</td>
					</tr>
				</table>
			</div>
		</div>

		<div class="shadow sgte-resp-oper" id="sgte-resp-oper">
			<div>
				<table id="tbl-shadow">
					<tr>
						<td>
							<form id="form-resp-oper" onSubmit="save(this); return false;" method="post" enctype="application/x-www-form-urlencoded">
								<input type="hidden" id="ero_id_tc" name="id_tc">
								<input type="hidden" id="ero_id_oper" name="id_oper">
								<input type="hidden" id="ero_id_oper_sigte" name="id_oper_sigte">
								<fieldset>
									<legend>&nbsp;Siguiente&nbsp;</legend>
									<table>
										<tr>
											<th>Responsable</th>
											<th>Operaci&oacute;n</th>
										</tr>
										<tr>
											<td><textarea id="ero_os_responsable" name="os_responsable" cols="40" rows="10"></textarea></td>
											<td><textarea id="ero_os_operacion" name="os_operacion" cols="40" rows="10"></textarea></td>
										</tr>
										<tr>
											<td align="right" colspan="2">
												<table>
													<tr>
														<td><input id="pb_or_ok" type="reset" value="Aceptar" onclick="return actionRespOper(true)"></td>
														<td><input id="pb_or_cancel" type="reset" value="Cancelar" onclick="return actionRespOper(false)"></td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</fieldset>
							</form>
						</td>
					</tr>
				</table>
			</div>
		</div>
		
		<table width="99%" height="100%" cellpadding="0" cellspacing="0">
			<tr>
				<td height="30%">
					<table>
						<tr>
							<td>
								<form id="form-caso" onSubmit="save(this); return false;" method="post" enctype="application/x-www-form-urlencoded">
									<fieldset>
										<legend id="titCaso">&nbsp;Caso&nbsp;</legend>
										<table>
											<tr>
												<td>Tipo de Caso</td>
												<td>Asociado a Gaveta</td>
											</tr>
											<tr>
												<td>
													<input id="in_tc_descripcion" name="tc_descripcion" type="text" size="40" maxlength="80" style="display: none;">
													<select id="sel_tc_descripcion" name="id_tc" onchange="return consultaCaso(this.options[this.selectedIndex].value,true);">
														<%Map m = cbl.getAllTipoCaso();
														if (m.isEmpty()) {%>
														<option value="-1" selected="selected">&lt;No hay casos&gt;</option>
														<%} else {%>
														<option value="-1" selected="selected">&lt;Seleccione un Caso&gt;</option>
														<%for (Iterator iter = m.keySet().iterator(); iter.hasNext();) {
															String name = (String) iter.next();
															TipoCaso tc = (TipoCaso) m.get(name);%>
														<option value="<%=tc.getIdTC()%>"><%=name%></option>
												<%}}%></select>
													</td>
												<td>
													<input id="in_tc_gaveta_asociada" type="text" size="16" maxlength="16" readonly="readonly">
													<select id="sel_tc_gaveta_asociada" name="tc_gaveta_asociada" style="display: none;">
													<% Map mg = cbl.getGavetasFortimax();
													  if (mg.isEmpty()) {%>
														<option value="-1">&lt;Ninguna&gt;</option>
													<%} else {%>
														<option value="-1" selected="selected">&lt;Seleccione una Gaveta&gt;</option>
													<%for (Iterator iter = mg.keySet().iterator();iter.hasNext();) {
														String name = (String) iter.next();
														Aplicacion app = (Aplicacion) mg.get(name);%>
														<option value="<%=app.getTituloAplicacion()%>"><%=app.getTituloAplicacion()%></option>
												<%}}%></select>
												</td>
											</tr>
											<tr>
												<td>Alarma</td>
												<td valign="top" rowspan="2">
													<table style="margin: 0px; padding: 0px; border: 0px;">
														<tr>
															<td>Tiempo L&iacute;mite</td>
														</tr>
														<tr>
															<td><input id="in_tc_tiempo_limite" name="tc_tiempo_limite" type="text" size="7" maxlength="7" readonly="readonly">&nbsp;horas</td>
														</tr>
														<tr>
															<td>Quien lo puede Iniciar</td>
														</tr>
														<tr>
															<td>
																<input id="in_tc_who_can_init" name="tc_who_can_init" type="text" size="25" maxlength="32" readonly="readonly">
																<select id="sel_tc_who_can_init" name="tc_who_can_init" style="display: none;">
																<%
																%></select>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td><textarea id="in_tc_alarma" name="tc_alarma" cols="30" rows="5" readonly="readonly" style="overflow: auto;"></textarea></td>
											</tr>
											<tr>
												<td align="right" colspan="2">
													<table>
														<tr>
															<td><input id="pb_tc_ok" type="reset" value="Aceptar" onclick="return actionCaso()" style="display: none;"></td>
															<td><input id="pb_tc_new" type="reset" value="Nuevo" onclick="return modoCaso(1)"></td>
															<td><input id="pb_tc_del" type="button" value="Borrar" onclick="return modoCaso(3)" style="display: none;"></td>
															<td><input id="pb_tc_edit" type="button" value="Editar" onclick="return modoCaso(2)" style="display: none;"></td>
															<td><input id="pb_tc_cancel" type="reset" value="Cancelar" onclick="return modoCaso(4)" style="display: none"></td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</fieldset>
								</form>
							</td>
						</tr>
					</table>
				</td>
				<td align="right" height="30%">
					<table height="100%">
						<tr>
							<td>
								<fieldset style="height: 85%;">
									<legend>&nbsp;Variables&nbsp;del&nbsp;Caso&nbsp;</legend>
									<table>
										<tr>
											<td height="90%" valign="top">
												<div id="tableContainer" class="tableContainer" style="width: 300px; height: 148px;">
													<table id="tblVar"  class="scrollTable">
														<thead class="fixedHeader" id="fixedHeader">
															<tr>
																<th width="10%">&nbsp;</th>
																<th width="90%">Nombre</th>
															</tr>
														</thead>
														<tbody class="scrollContent">
															<tr>
																<td colspan="2">Sin Variables</td>
															</tr>
														</tbody>
													</table>
												</div>
											</td>
										</tr>
										<tr>
											<td height="10%" align="right" valign="bottom">
												<table>
													<tr>
														<td><input id="pb_tcv_new" type="button" value="Nuevo" onclick="return modoVar(1)" style="display: none;"></td>
														<td><input id="pb_tcv_edit" type="button" value="Editar" onclick="return modoVar(2)" style="display: none;"></td>
														<td><input id="pb_tcv_del" type="button" value="Borrar" onclick="return modoVar(3)" style="display: none;"></td>
														<td><input id="pb_tcv_show" type="button" value="Ver Detalle" onclick="return modoVar(4)" style="display: none;"></td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</fieldset>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td align="center" height="70%" colspan="2">
					<iframe id="tbl-caso-oper" src="wrkflw-oper.jsp" scrolling="auto" width="100%" height="100%" frameborder="0" marginheight="0" marginwidth="0" style="padding: 0px;"></iframe>
				</td>
			<tr>
		</table>
	</body>
</html>
