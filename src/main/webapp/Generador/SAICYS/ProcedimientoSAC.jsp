<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%@page import="java.text.SimpleDateFormat"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	Map rol =usuario.getRoles();
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today= sdf.format(c1.getTime());
	c1.add(Calendar.MONTH, -1);
	String todayAnt= sdf.format(c1.getTime());
	int nIdProcedimientoSAC=(null==request.getParameter( "nIdProcedimientoSAC" ) || "".equals( request.getParameter( "nIdProcedimientoSAC" ) )?-1:Integer.parseInt( request.getParameter( "nIdProcedimientoSAC" ) ) );
 %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>My JSP 'ProcedimientoSAC.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script type="text/javascript" charset="utf-8">
		var oTableParticipantes="";
		$(document).ready(function() {
			$("#tbs").val(2);
			showAndHideTabs();
			<%
			    String role="";
			    String roles="";
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
			//	NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				Map botones=nb.getBotones(role,"SAC","ConsultaProcedimientoSAC");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}

				%>
			roles="<%=roles%>";
			$("input.AyudaSyC").subIniciaDlg();
		    $("input.autoCompletaSyC").subIniciaAutoCompleta();
		    $("#esperar").dialog({
				autoOpen : false,
				height : "310px",
				width : "400 px",
				modal : true,
				open: function(event, ui){
					$(".ui-dialog-titlebar").hide();
				},
				close : function() {
				}
			});
			
			$("#nIdProcedimientoSAC").val("<%= nIdProcedimientoSAC%>");
			initTableProced();
			consultaDatosProced();
		});
		
	</script>

  </head>
  
  <body>
  	<form action="">
		<div id="container" class="container" style="width: 98%;">
    		<fieldset>
    			<legend>Nuevo Procedimiento SAC</legend>
    			
    			<table style="width: 100%" align="left" >
	    			<tr id="trBotones">						
						<td align="right" colspan="2"  >
							<input type="button" class="btnInterfaceCancelar ui-button ui-widget ui-state-default ui-corner-all" 	id="imgPlayDesierto" 	name="imgPlayDesierto" 	value="Desierto"	onclick="procedimientoDesierto(4);"/>&nbsp;&nbsp;
							<input type="button" class="btnInterfaceReturn ui-button ui-widget ui-state-default ui-corner-all" 		id="imgPlayDevolver" 	name="imgPlayDevolver" 	value="Devolver"	 onclick="procedimientoDesierto(5);"/>&nbsp;&nbsp;
                        
						</td>
					</tr>
					<tr id="trDesierto" style="display: none;">
						<td colspan="2">
							<input type="text" id="lblNotaDesierto" name="lblNotaDesierto" value="Est&eacute; procedimiento fue declarado desierto." style="width: 40%;color: red; border: 0px solid black;" readonly="readonly">
						</td>
					</tr>
					<tr id="trDevuelto" style="display: none;">
						<td colspan="2">
							<input type="text" id="lblNotaDevuelto" name="lblNotaDevuelto" value="Est&eacute; procedimiento fue devuelto." style="width: 40%;color: red; border: 0px solid black;" readonly="readonly">
						</td>
					</tr>
    				<tr>
    					<td style="width: 15%">
    						Fecha de Solicitud:
    					</td>
    					<td style="width: 85%">
    						<input type="text" name="fSolicitud" id="fSolicitud" value="" title="Fecha de Solicitud" style='width: 10%' readonly="readonly" class="desahabilitado"/>
    					</td>
    				</tr>
    				<tr>
    					<td style="width: 15%">
    						Fecha de Atenci&oacute;n:
    					</td>
    					<td style="width: 85%">
    						<input type="text" name="fAtencion" id="fAtencion" value="" title="Fecha de Atención" style='width: 10%' readonly="readonly" class="desahabilitado"/>
    					</td>
    				</tr>
    				<tr>
    					<td style="width: 15%">
    						Oficio de Solicitud:
    					</td>
    					<td style="width: 85%">
    						<input type="text" name="cOficioSolicitud" id="cOficioSolicitud" value="" title="Oficio de Solicitud" style='width: 40%' onkeydown="return(desactivaBackspace(event))"/>
    					</td>
    				</tr>
    				<tr>
    					<td style="width: 15%">
    						&Aacute;rea Requirente:
    					</td>
    					<td style="width: 85%">
    						<select name='cAreaReq' id='cAreaReq' style='width: 40%' onchange="obtieneAreasResponsables()" title="&Aacute;rea requirente"></select>
    					</td>
    				</tr>
    				<tr>
    					<td style="width: 15%">
    						&Aacute;rea T&eacute;cnica:
    					</td>
    					<td style="width: 85%">
    						<input type="text" name="cAreaTec" id="cAreaTec" value="" title="&Aacute;rea T&eacute;cnica" style='width: 40%'/>
    					</td>
    				</tr>
    				<tr>
    					<td style="width: 15%">
    						&Aacute;rea Responsable:
    					</td>
    					<td style="width: 85%">
    						<select name='cAreaResp' id='cAreaResp' style='width: 40%' title="&Aacute;rea responsable"></select>
    					</td>
    				</tr>
    				<tr>
    					<td style="width: 15%">
    						Procedimiento de Contratación Turnado A:
    					</td>
    					<td style="width: 85%">
    						<input type="text" class="desahabilitado"  name="ServidorPublico" id="ServidorPublico" value="" title="Procedimiento de Contratación Turnado" style='width: 40%' readonly="readonly"/>
    					</td>
    				</tr>
    				<tr>
    					<td style="width: 15%">
    						Tipo de Procedimiento:
    					</td>
    					<td style="width: 85%">
    						<select name='nTipoProcedimiento' id='nTipoProcedimiento' style='width: 40%' onchange="showAndHideInputs();obtieneProcesosContratacion();" title="Tipo de procedimiento"></select>
    					</td>
    				</tr>
    				<tr>
    					<td style="width: 15%">
    						Materia del Procedimiento:
    					</td>
    					<td style="width: 85%">
    						<select name='nIdMateriaProcedimiento' id='nIdMateriaProcedimiento' style='width: 40%' title="Materia del procedimiento" ></select>
    					</td>
    				</tr>
    				<tr>
    					<td style="width: 15%">
    						Denominación del Procedimiento:
    					</td>
    					<td style="width: 85%">
    						<input type="text" name="cDenominacionProcedimiento" id="cDenominacionProcedimiento" value="" title="Denominación del Procedimiento" style='width: 40%'/>
    					</td>
    				</tr>
    				<tr>
    					<td style="width: 15%">
    						N&uacute;mero de Procedimiento:
    					</td>
    					<td style="width: 85%">
    						<input type="text" name="cNumProcedimiento" id="cNumProcedimiento" value="" title="N&uacute;mero de Procedimiento" style='width: 40%'/>
    					</td>
    				</tr>
    				<tr id="trProyectoConv">
    					<td style="width: 15%">Proyecto de Convocatoria:
    					</td>
    					<td style="width: 85%">
    						<input type="text" name="cProyectoConvocatoria" id="cProyectoConvocatoria" value="" title="Proyecto de Convocatoria" style='width: 40%'/>
    					</td>
    				</tr>
    				<tr id="trRevAutConvocatoria">
    					<td style="width: 15%">
    						Revisi&oacute;n o Autorizaci&oacute;n de Convocatoria (Fecha):
    					</td>
    					<td style="width: 85%">
    						<input type="text" name="fAutConvocatoria" id="fAutConvocatoria" value="" title="Revisi&oacute;n o Autorizaci&oacute;n de Convocatoria" style='width: 10%' readonly="readonly" class="desahabilitado"/>
    					</td>
    				</tr>
    				<tr id="trConvocatoriaFechaPub">
    					<td style="width: 15%">
    						Convocatoria (Fecha de Publicaci&oacute;n):
    					</td>
    					<td style="width: 85%">
    						<input type="text" name="fPublicacionConvocatoria" id="fPublicacionConvocatoria" title="Convocatoria (Fecha de Publicaci&oacute;n)" value="" style='width: 10%' readonly="readonly" class="desahabilitado"/>
    					</td>
    				</tr>
    				<tr id="trJuntaAclaraciones">
    					<td style="width: 15%">
    						Junta de Aclaraciones(Fecha):
    					</td>
    					<td style="width: 85%">
    						<input type="text" name="fJuntaAclara" id="fJuntaAclara" value="" title="Junta de Aclaraciones" style='width: 10%' readonly="readonly" class="desahabilitado"/>
    					</td>
    				</tr>
    				<tr id="trAperturaProposiciones">
    					<td style="width: 15%">
    						Apertura de Proposiciones(Fecha):
    					</td>
    					<td style="width: 85%">
    						<input type="text" name="fApertProposiciones" id="fApertProposiciones" title="Apertura de Proposiciones"  value="" style='width: 10%' readonly="readonly" class="desahabilitado"/>
    					</td>
    				</tr>
    				<tr>
    					<td style="width: 15%">
    						Evaluaci&oacute;n T&eacute;cnica(Fecha de env&iacute;o al area t&eacute;cnica):
    					</td>
    					<td style="width: 85%">
    						<input type="text" name="fEvaluacionTecnica" id="fEvaluacionTecnica" value="" title="Evaluaci&oacute;n T&eacute;cnica" style='width: 10%' readonly="readonly" class="desahabilitado"/>
    					</td>
    				</tr>
    				<tr>
    					<td style="width: 15%">
    						Fallo / Acta de Adjudicaci&oacute;n (Fecha):
    					</td>
    					<td style="width: 85%">
    						<input type="text" name="fFallo" id="fFallo" value="" title="Fallo / Acta de Adjudicaci&oacute;n" style='width: 10%' readonly="readonly" class="desahabilitado"/>
    					</td>
    				</tr>
    				<tr>
    					<td style="width: 15%">
    						Fecha de Generaci&oacute;n de Contrato y Datos Relevantes CNET:
    					</td>
    					<td style="width: 85%">
    						<input type="text" name="fGeneracionContrato" id="fGeneracionContrato" value="" title="Fecha de Generaci&oacute;n de Contrato y Datos Relevantes CNET" style='width: 10%' readonly="readonly" class="desahabilitado"/>
    					</td>
    				</tr>
    				<tr>
    					<td style="width: 15%">
    						Expediente Turnado Para Contrato (Fecha):
    					</td>
    					<td style="width: 85%">
    						<input type="text" name="fExpediente" id="fExpediente" value="" title="Expediente Turnado Para Contrato" style='width: 10%' readonly="readonly" class="desahabilitado"/>
    					</td>
    				</tr>
    				<tr>
    					<td style="width: 15%">
    						Proveedor dado de Alta (SAI, CNET):
    					</td>
    					<td style="width: 85%">
    						<select name='nProveedorDadoAlta_SAICNET' id='nProveedorDadoAlta_SAICNET' style='width: 40%' title="Proveedor dado de alta" ></select>
    					</td>
    				</tr>
    				<tr>
    					<td style="width: 15%">
    						Estatus al Proceso de Contrataci&oacute;n:
    					</td>
    					<td style="width: 85%">
    						<select name='cProcesoContratacion' id='cProcesoContratacion' style='width: 40%' title="Estatus Proceso de Contrataci&oacute;n" ></select>
    					</td>
    				</tr>
    				<tr>
    					<td style="width: 15%">
    						Observaciones del proceso :
    					</td>
    					<td style="width: 85%">
    						<textarea rows="6" style="width: 40%" id="cObservaciones" name="cObservaciones" title="Captura de notas"></textarea>
    					</td>
    				</tr>
    				<tr height="100px">
    					<td colspan="2">
    						<fieldset>
    							<legend>Participantes en el proceso</legend>
    							<table>
    								<tr>
    									<td>
    										Servidor P&uacute;blico:<input type="text" class="AyudaSyC obligatorio desahabilitado" maxlength="40" size="50" name="ServidoresPublicos" id="ServidoresPublicos" readonly />
    										<input type="button" id="btnAgregar" name="btnAgregar" value="Agregar" onclick="addParticipante()" class="btnInterfaceBG ui-button ui-corner-all"/>
    									</td>
    								</tr>
    								<tr>
    									<td>
    										<table id="tblParticipantes" class="display" >
												<thead >
													<tr>
														<th align="center"># Empleado</th>
														<th align="center">Nombre</th>
														<th align="center">Apellido Paterno</th>
														<th align="center">Apellido Materno</th>
														<th align="center"></th>
													</tr>										
												</thead>
											</table>
    									</td>
    								</tr>
    							</table>
    						</fieldset>
    					</td>
    				</tr>
    				<tr>
    					<td colspan="1" align="right">
    						<input type="button" id="btnGuardar" name="btnGuardar" value="Actualizar" onclick="actualizaProced()" align="right" class="btnInterfaceBG ui-button ui-corner-all"/>
    					</td>
    				</tr>
    			</table>
    		</fieldset>
    	</div>
    	<div id="esperar" align="center" title="Espera">
			<fieldset>
				<table>
					<tr>
						<td>Espere por favor.... <img border="0"src="../../imagenes/espera.gif" height="30"></td>
					</tr>
				</table>
			</fieldset>
		</div>
    	<input type="hidden" id="tipoProceso" name="tipoProceso" value="2" />
    	<input type="hidden" id="tipoOperacion" name="tipoOperacion" value="1" />
    	<input type="hidden" id="nIdProcedimientoSAC" name="nIdProcedimientoSAC" value="-1" />
    	<input type="hidden" id="nIdParticipanteProceso" name="nIdParticipanteProceso" value="-1" /> 
    	<input type="hidden" id="numEmpleado" name="numEmpleado" value="" />
    	<input type="hidden" id="nombreEmpleado" name="nombreEmpleado" value="" />
    	<input type="hidden" id="apellidoPatEmpleado" name="apellidoPatEmpleado" value="" />
    	<input type="hidden" id="apellidoMatEmpleado" name="apellidoMatEmpleado" value="" />
    	<input type="hidden" id="nEstatus" name="nEstatus" value="1" />  
    		
	</form>
  </body>
</html>
