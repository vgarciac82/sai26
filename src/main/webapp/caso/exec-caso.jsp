<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%
	CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	int id_oper = c.getCasoOperacion(0).getIdOperacion();
	
	boolean isExec = "false".equals(request.getParameter("search"));
	
	CasoHerramientas casoHerramientas = cbl.getCasoHerramientas(c.getTipoCaso().getIdTC(), c.getCasoOperacion(0).getOperacion().getNumero() );
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>Caso en Ejecuci&oacute;n</title>
		<link href="../css/gestion.css" rel="stylesheet"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		<script type="text/javascript" src="../js/gestion.js"></script>
		<script type="text/javascript" src="../js/ajax-commons.js"></script>
		<script type="text/javascript" src="../js/exec-caso.js"></script>		
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
		<script for="datawork" event="onLoad" type="text/javascript"></script>
		
		<script type="text/javascript">
			var	docSaved = false;
			function onLoad() {				
				gestion.requestXMLGestion();
				<%if (c.getCasoOperacion(0).getOperacion().getOnLoad() != null) {%>
					<%=c.getCasoOperacion(0).getOperacion().getOnLoad()%>
				<%}%>
				
			}
			
			function doSave() {
				try {
					if (!processData(true)) return false;
					gestion.sendFormData(onSave);
					<%if (c.getCasoOperacion(0).getOperacion().getPostDisplay() != null) {%>
						<%=c.getCasoOperacion(0).getOperacion().getPostDisplay()%>
					<%}%>
					
				} catch (e) {
					if( e.name || e.message)
						alert("Numero: " + e.number + "\nNombre: " + e.name + "\nMensaje: " + e.message + "\nDescripcion: " + e.description);
					else
						alert("Ocurrio un error! " + e);
				}
			}
			
			function doCancel(){
				Swal.fire({
					  title: '¿Desea continuar?',
					  text: "¡Perderá los datos capturados!",
					  icon: 'warning',
					  showCancelButton: true,
					  confirmButtonColor: '#288BA8',
					  cancelButtonColor: '#e6e6e6',
					  confirmButtonText: 'Aceptar',
					  cancelButtonText: 'Cancelar'
					}).then((result) => {
					  if (result.isConfirmed) {
						  	<%if (c.getCasoOperacion(0).getOperacion().getOnCancel() != null) {%>
								<%=c.getCasoOperacion(0).getOperacion().getOnCancel()%>
							<%}%>
							frmCancel.submit();
					  } 
					})
				
			}
			
			function doLeave() {				
				Swal.fire({
					  title: '¿Desea continuar?',
					  text: "¡Al cerrar el documento podrá ser trabajado por otro usuario!",
					  icon: 'warning',
					  showCancelButton: true,
					  confirmButtonColor: '#288BA8',
					  cancelButtonColor: '#e6e6e6',
					  confirmButtonText: 'Aceptar',
					  cancelButtonText: 'Cancelar'
					}).then((result) => {
					  if (result.isConfirmed) {
						  frmLeave.submit();
						  
					  } else if (result.dismiss === Swal.DismissReason.cancel) {
						  document.getElementById("pb_leave").disabled=false;
							return;
					  }
					})
			}
			
			function onSave(xmlData) {
				if (xmlData) {
					docSaved = true;
					window.parent.frames["doctree"].location.reload(1);

				}
			}
			function actionSend(httpRequest) {
				//creo que aqui iba originalmente el postSubmit
			}
			
			function validaEnviar() {//boton enviar, si se requieren validaciones para el boton enviar

				var respuesta = true;
				<%if (c.getCasoOperacion(0).getOperacion().getPostSubmit() != null) {%>
					respuesta = <%=c.getCasoOperacion(0).getOperacion().getPostSubmit()%>
				<%}%>
				if (respuesta) {
					frmSend.submit();
				}
				//return respuesta;
			}
			
			function doSubmit() {
				if(<%=id_oper%>==1)
					document.getElementById('pb_save').disabled = true;
				if(<%=id_oper%>>1){
					document.getElementById('pb_rechazar').disabled = true;
					document.getElementById('pb_autorizar').disabled = true;
				}
				document.getElementById('pb_send').disabled = true;
				try {
					if (!processData(false)) return false;
					gestion.sendFormData(actionSend);
				} catch (e) {
					alert("Numero: " + e.number + "\nNombre: " + e.name + "\nMensaje: " + e.message + "\nDescripción: " + e.description);
					return false;
				}
				return true;
			}<%String token = " = ";%>
			
			function processData(execRespOper){
				try {

					if (execRespOper) {
					
						var form_validations = "";//Ethiel, se recupera en una variable para decidir si continua o no
						form_validations = <%=c.getCasoOperacion(0).getOperacion().getOnSubmit()%> 
						if(!form_validations) return false; //Ethiel, esto no existia y nunca respetaba la validacion de la forma
					
						execResponsable();
						execOperacion();
					}
					var obsrv = document.getElementById("observaciones");
					document.getElementById("<%=GestionInterface.PRM_OBSERV%>").value = obsrv.value+(obsrv.value!=''?'<br>':'');//Ethiel, seconcatena retorno de carro
					gestion.setGestionData();
				
				} catch (e) {
					throw e;
				}
				return true;
			}
			function execResponsable() {
				var resp = document.getElementById("gstnTo");<%for (int i = 0; i < c.getCasoOperacion(0).getOperacion().getOperacionSgte().size(); i++) {%>
				resp.value<%=token%>resp<%=i%>();<%token = " += \" \" + "; }%>
				document.getElementById("<%=GestionInterface.PRM_RESP%>").value = resp.value;
			}<%token = " = ";%>
			function execOperacion() {
				var oper = document.getElementById("gstnSubject");<%for (int i = 0; i < c.getCasoOperacion(0).getOperacion().getOperacionSgte().size(); i++) {%>
				oper.value<%=token%>oper<%=i%>() + ";";<%token = " += \" \" + "; }%>
				document.getElementById("<%=GestionInterface.PRM_OPER%>").value = oper.value;
			}<%for (int i = 0; i < c.getCasoOperacion(0).getOperacion().getOperacionSgte().size(); i++) {%>
			function resp<%=i%>() {
				<%=c.getCasoOperacion(0).getOperacion().getOperacionSgte(i).getResponsable()%>
			}<%	} for (int i = 0; i < c.getCasoOperacion(0).getOperacion().getOperacionSgte().size(); i++) {%>
			function oper<%=i%>() {
				<%=c.getCasoOperacion(0).getOperacion().getOperacionSgte(i).getOperacion()%>
			}<%}%>
			function validaGabinete() {
				if ((<%=c.getIdGabinete()%> == -1) && (docSaved === false)) {
					window.alert("No existe el gabinete para este Caso\n\n1. Capture los datos del documento\n2. Y posteriormente de click en el botón Guardar");
					return false;
				}
				return true;
			}
			

					
		</script>
		<style>
			html, body {
				background-color: #f5ffff;
				width: 100%;
				height: 100%;
				padding: 0px;
				margin: 0px;
				overflow: hidden;
			}

		</style>
	</head>
	<body onLoad="onLoad()">
		<table width="100%" height="100%"  >
			<tr height="95%" >
				<td >
					<iframe id="datawork" style="overflow: auto; height:100%; width:100%;" name="datawork" frameborder="0"  src="<%=c.getCasoOperacion(0).getOperacion().getPlantilla()%>?folio=<%=new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-')+1)).intValue()%>"></iframe>
				</td>
			</tr>
			
			<tr height="2.5%">
				<td>
					<table width="100%" cellpadding="0" cellspacing="0" >
						<tr>
							<td colspan="2" width="100%">
								<table  align="right">
									<tr>
									
										<%if (isExec) {%>
											<td>
												<input type="button" id="pb_save" value="Guardar" onClick="doSave();" title="Guardar los cambios realizados" style="FONT-SIZE: 11pt; <%="S".equalsIgnoreCase( casoHerramientas.getGuardar() )? "" :  "display:none" %>" >
											</td>
										<%}%>
										<%if (isExec) {%>
										<!-- <td>
											<form id="frmMenu" action="../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_MSG_COMPOSE%>&close=true" method="post" target="_blank">
												<input type="submit" id="pb_msg" value="Mensaje" title="Enviar un mensaje nuevo">
											</form>
										</td>-->
										<%}%>
										<%if (isExec) {%><td>
											<form id="frmSend"
												action="../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_ADVANCE_CASE%>"
												method="post" onSubmit="doSubmit();" target="content-iframe">												
												<input type="hidden" name="<%=GestionInterface.PRM_RESP%>" id="<%=GestionInterface.PRM_RESP%>">
												<input type="hidden" name="<%=GestionInterface.PRM_OPER%>" id="<%=GestionInterface.PRM_OPER%>">
												<input type="hidden" name="<%=GestionInterface.PRM_OBSERV%>" id="<%=GestionInterface.PRM_OBSERV%>">
												<input type="button" id="pb_send" disabled value="Enviar"  title="Avanzar a la siguiente opreaci&oacute;n"  onClick="validaEnviar();" style="FONT-SIZE: 11pt <%="S".equalsIgnoreCase( casoHerramientas.getEnviar() )? "" :  ";display:none" %>"> <!-- doSave(); -->
												<!--<input type="button" id="pb_rechazar"  value="Rechazar" onclick="document.getElementById('pb_send').disabled = true; this.disabled = true;" disabled title="Rechazar y regresar a la operacion anterior.">-->
											</form>
										</td><%}%>
									
										<!--<td>
											<form id="frmPend"
												action="../afirme/caso-pendiente"
												method="post" onSubmit="doSubmit()" target="content-iframe">
												<input type="submit" id="pb_pend" value="Pendiente" disabled title="Dejar pendiente esta operaci&oacute;n">
											</form>
										</td>-->
										<%if(isExec){ %>
										<td>
											<form id="frmCancel"
												action="../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_DELETE_CASE%>"
												method="post" target="content-iframe">
												<%if( c.getIdTC()==3 && (id_oper==1||id_oper==5) &&!"true".equals(c.getCasoDato("APLICADO_CONT").getValor())){ %>
												<input type="button" id="pb_cancel" value="Descartar" title="Descartar" onclick="doCancel();" style="FONT-SIZE: 11pt <%="S".equalsIgnoreCase( casoHerramientas.getDescartar() )? "" :  ";display:none" %>"/>
												<%}else if( (c.getIdTC()!=3) && (c.getIdTC()!=23) ) {%>
												<input type="button" id="pb_cancel" value="Descartar" title="Descartar" onclick="doCancel();" style="FONT-SIZE: 11pt <%="S".equalsIgnoreCase( casoHerramientas.getDescartar() )? "" :  ";display:none" %>"/>
												<%} %>
											</form>
										</td>
										<%}%>
										<td>
											<form id="frmLeave"
												action="../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_USER_LEAVE_CASE%>"
												method="post" target="content-iframe">

												<input type="button" id="pb_leave" value="Cerrar" title="Cerrar" onclick="doLeave();" style="FONT-SIZE: 11pt"/>
												
											</form>	
										</td>
									</tr>
								</table>
							</td>
						</tr>
		
						 <!-- Ethiel, se comenta este <tr> a peticion del usuario, pero como son necesarias se ponen como hidden en las lineas de arriba
						<tr>
							<td width="100%">
								<table width="100%" cellpadding="0" cellspacing="0">
									<tr>
										<td align="right"><strong>De:</strong>&nbsp;</td>
										<td><!%=c.getCasoOperacion(0).getResponsable()%></td>
										<td rowspan="3">
											<table cellpadding="0" cellspacing="0">
												<tr>
													<td><strong>Observaciones:</strong>&nbsp;</td>
												</tr>
												<tr>
													<td><textarea name="observaciones" cols="30" rows="3"></textarea></td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td id="toLevel" align="right"><strong>Para:</strong>&nbsp;</td>
										<td><input id="gstnTo" type="text" size="60" maxlength="255" disabled></td>
									</tr>
									<tr>
										<td align="right"><strong>Operaci&oacute;n:</strong>&nbsp;</td>
										<td><input id="gstnSubject" type="text" size="60" maxlength="255" disabled></td>
									</tr>
								</table>
							</td>
						</tr>
						aqui cerrar el comentario-->
					</table>
				</td>
			</tr>
		</table>

		<!-- Ethiel, son las variables de lo que se comenta abajo para ocultar reponsable y operacion siguientes-->
		<input id="gstnSubject" type="hidden" value=""/>
		<input id="gstnTo" type="hidden" value=""/>
		<input id="observaciones" type="hidden" value=""/> 
						
		<div id="respOper" >
			<table width="100%" height="100%">
				<tr>
					<td colspan="5">Seleccione a quien(es) se le enviar&aacute; la operaci&oacute;n(es)<hr /></td>
				</tr>
				<tr>
					<td colspan="5">
						<table>
							<tr>
								<td>Filial:&nbsp;<select id="selFilial" onchange="requestDeptos()"></select></td>
								<td>Departamento:&nbsp;<select id="selDepto" onchange="requestAreas()"></select></td>
								<td>&Aacute;rea:&nbsp;<select id="selArea" onchange="requestSgrps()"></select></td>
								<td>Subgrupo:&nbsp;<select id="selSgrp" onchange="requestUsersAndGrps()"></select></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td width="32%" height="1%" align="center">Usuarios</td>
					<td width="32%" height="1%" align="center">Operaci&oacute;n</td>
					<!--td width="1%" rowspan="4" align="center" valign="middle"><input type="button" onclick="addSelectionToList()" value="&gt;" style="width:22px;" /></td-->
					<td width="1%" rowspan="2" align="center" valign="middle"><input type="button" onclick="addSelectionToList()" value="&gt;" style="width:22px;FONT-SIZE: 11pt" /></td>
					<td width="36%" height="1%" align="center">Enviar a</td>
					<!--td width="1%" rowspan="4" align="center" valign="middle"-->
					<td width="1%" rowspan="2" align="center" valign="middle">
						<table>
							<tr>
								<td align="center"><input type="button" value="&lt;" onclick="unSelectFromList(false)" style="width:22px;" style="FONT-SIZE: 11pt"/></td>
							</tr>
							<tr>
								<td align="center"><input type="button" value="&lt;&lt;" onclick="unSelectFromList(true)" style="width:22px;" style="FONT-SIZE: 11pt"/></td>
							</tr>
							<tr>
								<td align="center"><input type="button" value="L" title="Limpiar Enviar a" onclick="clearEnviar()" style="width:22px;" style="FONT-SIZE: 11pt"/></td>
							</tr>
						</table>
					</td>
				</tr>
				<!--tr>
					<td width="32%"><select id="usuario" size="10" onchange="updUserOpers(this)" style="width: 100%;"></select></td>
					<td width="32%" rowspan="3"><select id="oper" size="22" style="width: 100%;"></select></td>
					<td width="36%" rowspan="3"><select id="enviar" size="22" style="width: 100%;"></select></td>
				</tr-->
				<tr>
					<td width="32%"><select id="usuario" size="22" onchange="updUserOpers(this)" style="width: 100%;"></select></td>
					<td width="32%"><select id="oper" size="22" style="width: 100%;"></select></td>
					<td width="36%"><select id="enviar" size="22" style="width: 100%;"></select></td>
				</tr>
				<tr style="display: none">
					<td width="32%" height="1%" align="center">Grupos</td>
				</tr>
				<tr style="display: none">
					<td width="32%"><select id="grupo" size="10" onchange="updGrpOpers(this)" style="width: 100%;"></select><br></td>
				</tr>
				<tr>
					<td height="1%">&nbsp;</td>
					<td height="1%">&nbsp;</td>
					<td height="1%">&nbsp;</td>
					<td height="1%">
						<table align="right">
							<tr>
								<td><input name="button" type="button" onClick="closeSelRespOper()"value="Cerrar" style="FONT-SIZE: 11pt"></td>
								<td><input name="button" type="button" onClick="copyListSend()" value="Aceptar" style="FONT-SIZE: 11pt"></td>
							</tr>
						</table>
					</td>
					<td height="1%">&nbsp;</td>
				</tr>
			</table>
		</div>

	</body>
</html>
