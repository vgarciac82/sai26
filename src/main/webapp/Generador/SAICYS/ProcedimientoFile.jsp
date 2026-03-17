<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
	   return;
	}
	String roles="";
	Map rol =usuario.getRoles();
	String cEjercicio = "";
	String cIdTipoProcedimiento= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	
	if (session.getAttribute(GestionInterface.ATT_ProEjercicio) != null) {
		cEjercicio =(String)session.getAttribute(GestionInterface.ATT_ProEjercicio);
		cIdUnidadEjecutora= (String)session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora);
		cIdTipoProcedimiento=(String)session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento);
		nIdConsecutivo=(String)session.getAttribute(GestionInterface.ATT_ProConsecutivo);
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Procedimiento</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Consolidado">
		
		<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
			$("#tbs").val(9);
			showAndHideTabs();
		<%
			NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
			Map botonesArchivos=nb.getBotones(roles,"Procedimiento","ArchivosProcedimiento");
			Iterator btnArchivos = botonesArchivos.entrySet().iterator();
			while (btnArchivos.hasNext()) {
				Map.Entry bArchivos = (Map.Entry)btnArchivos.next();%>
				$("#<%=bArchivos.getValue()%>").attr("disabled", true);<%	
				String img=(String) bArchivos.getValue();
			}
		%>	
		
		$("#usuarioLogin").val('<%=usuario.getLogin()%>');
		$("#cIdEntidadContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
		$("#cIdUsuarioCreacion").val('<%=usuario.getLogin()%>');
		$("#usuarioRoleProcedimiento").val('<%=roles%>');
		
		muestraInformacion();
		habilitarPestanas();
		enviaDatosArchivoProcedimiento();
	});
	
	
	
	function muestraInformacion (){
		   queryFormPost("llenaCaratulaProcedimiento",{async:false});
		   queryFormPost("llenaUnidadEjecutoraProcedimiento",{async:false});
		   queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
		   queryFormPost("esActivoProcedimiento",{async:false});
		   
		   var cadena_campos=$("#cIdConsolidado").val().split('-');
			$("#TipoConsolidado").val(cadena_campos[0]);
			$("#ConsecutivoConsolidado").val(cadena_campos[2]);
	}


	function validaModificarProcedimiento(){
		if(($('#cIdUsuarioCreacion').val()== $("#usuarioLogin").val())||( $('#usuarioRoleProcedimiento').val().toString().indexOf('ADMIN_RECMAT') >= 0)){
			return true;
		}
		else{
			alert("El usuario no tiene permiso para realizar esta acci\xF3n.");
			return false;
		}
	}

   
    function habilitarPestanas(){
			$("#EvaluacionProcedimiento" ).css("display", "block");
			$("#PreguntasProcedimiento" ).css("display", "block");
			$("#ArchivosProcedimiento" ).css("display", "block");
			$("#CotizacionProcedimiento" ).css("display", "none");
		
			if($("#cIdTipoProcedimiento").val()=="PN" || $("#cIdTipoProcedimiento").val()=="PS" ){
				$("#RequisitosProcedimiento" ).css("display", "block");
			}else{
				$("#RequisitosProcedimiento" ).css("display", "none");
			}
			if($("#nIdEstado").val()==2){
				queryFormPost("existePrecompromiso",{async:false});
				if($("#existePrecompromiso").val()==1){
					$("#AmpliacionVigenciaProcedimiento" ).css("display", "block");
				}else{
					$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
				}
			}else{
				$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
			}
			if(esRegularizacion()){
				$("#presupuestoProcedimiento" ).css("display", "none");
				$("#precompromisoProcedimiento" ).css("display", "none");
			}else{
				queryFormPost("mValidaPrecompromiso",{async:false});
				if($("#documentoAplicado").val()==0){ // precompromiso consolidado cancelado
				 	$("#presupuestoProcedimiento" ).css("display", "block");
					$("#precompromisoProcedimiento" ).css("display", "block");	
				}else{
				 	$("#presupuestoProcedimiento" ).css("display", "none");
					$("#precompromisoProcedimiento" ).css("display", "none");
				}
			}
		}
	
	function enviaDatosArchivoProcedimiento(){
		$("#cEjercicioArchivo").val($("#cEjercicio").val());
		$("#cIdProcedimientoArchivo").val($("#cIdProcedimiento").val());
		$("#cTipoArchivo").val($("#tipoArchivo").val());
		$("#cIdConsolidadoArchivo").val($("#cIdConsolidado").val());
		document.formArchivo.submit();
	}
	function esRegularizacion(){
		var regularizacion=false;
		var apartables = [ "PC", "PO", "PS", "PA", "PT" ];
		if ($.inArray($("#cIdTipoProcedimiento").val(), apartables) == -1){ // no traen apartado
			regularizacion=true;
		}else
			regularizacion=false;
		return regularizacion;
	}
	</script>
	</head>
	<body id="dt_example" >
	 <form>
		<div id="agregarArchivos">
			<input id="cEjercicio" name="cEjercicio" type="hidden" size="4" value="<%= cEjercicio %>">
			<input id="cIdTipoProcedimiento" name="cIdTipoProcedimiento" type="hidden" size="4" value="<%= cIdTipoProcedimiento %>">
			<input id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden" size="3" value="<%= cIdUnidadEjecutora %>">
			<input id="nIdConsecutivo" name="nIdConsecutivo" type="hidden" size="4" value="<%= nIdConsecutivo %>">	
			<input id="tipoProceso" name="tipoProceso" type="hidden" size="10">
			<input name="documentoAplicado" id="documentoAplicado" type="hidden"/>
				<fieldset><legend>Informaci&oacute;n del Procedimiento</legend>
					<table align="left" width="100%">						    
						<tr align="left">
						<td colspan="2">
							[[<input name="cIdProcedimiento" id="cIdProcedimiento" type="text" size ="10" style="border: 0px solid black;" readonly="readonly"/>]]&nbsp;<input name="desProcedimientoCaratula" id="desProcedimientoCaratula" type="text" maxlength="150" style="border: 0px solid black; width: 40em;" readonly="readonly"></input>  
						</td>								
					</tr>
				   <tr align="left">
						<td colspan="2">
							<input name="lblcIdUnidadEjecutora" id="lblcIdUnidadEjecutora" type="text" readonly="readonly" style="border: 0px solid black; width: 30em;"></input>  
						</td>								
					</tr>
					<tr align="left">
						<td colspan="2">
						Consolidado: [[<input name="cIdConsolidado" id="cIdConsolidado" type="text" size ="10" style="border: 0px solid black;" readonly="readonly"></input>]]  
						</td>								
					</tr>
					<tr align="left">
						<td colspan="2">
						Tipo de Proceso: [[<input name="lbltipoProceso" id="lbltipoProceso" type="text" size ="18" style="border: 0px solid black;" readonly="readonly"></input>]]  
						</td>								
					</tr>
					<tr align="left">
						<td colspan="2">
							<img id="imgEstado"/><input name="nIdEstadoProcedimiento" id="nIdEstadoProcedimiento" type="text"  style="border: 0px solid black; width: 45em;"></input>  
						</td>								
					</tr>
					<tr>
					<tr align="left">
						<td>
			 				<div id="tablaFechasProcedimientoArchivos" style="width:100%;">
								<!-- Carga las fechas -->
							</div>
						</td>
					</tr>
					<tr>
						<td>
			 				<br/>
						</td>
					</tr>
				</table>
		 	</fieldset>
		 	<br>
		 	<table>
				<tr>
 						<td style="width:100px;">
 							Tipo:
 						</td>
 						<td>
 							<select name="tipoArchivo" id="tipoArchivo" onChange="enviaDatosArchivoProcedimiento();">
							<option value="aperturaProcedimiento" selected>Apertura</option>
							<option value="falloProcedimiento">Fallo</option>
						</select>
 						</td>
 					</tr>
 				</table>
		 	<iframe name="iframe-archivos" width="100%" marginwidth="0" height="100%" marginheight="0" align="top" scrolling="auto" frameborder="0"></iframe>
		</div>
    </form>
    <form name="formArchivo" id="formArchivo" action="ArchivosProcedimiento.jsp" target="iframe-archivos">
    	<input type="hidden" id="cEjercicioArchivo" name="cEjercicioArchivo" />
    	<input type="hidden" id="cIdProcedimientoArchivo" name="cIdProcedimientoArchivo" />
    	<input type="hidden" id="cTipoArchivo" name="cTipoArchivo"/>
    	<input type="hidden" id="cMensaje" name="cMensaje" value=""/>
    	<input type="hidden" id="cIdConsolidadoArchivo" name="cIdConsolidadoArchivo" value=""/>
    	
    	<!-- habilita pestanas -->
 		<input id="existePrecompromiso" name="existePrecompromiso" type="hidden" size="10">
 		<input id="nIdEstado" name="nIdEstado" type="hidden" size="10">
 			
	</form>	
	</body>
</html>