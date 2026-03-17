<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuarioTab.getLogin();		
	Map rol =usuarioTab.getRoles();
	String cEjercicio = "";
	String cIdTipoContrato= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";		
	if (session.getAttribute(GestionInterface.ATT_ContratoEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoEjercicio);
		cIdTipoContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoTipoContrato);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ContratoUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ContratoConsecutivo);		
	}else 
		response.sendRedirect("Contratos.jsp?tab=0");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   
    
    <title>NuevasEPSContrato</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script type="text/javascript" charset="utf-8">
		var roles='';
		var actualiza=false;
		$(document).ready(function() {
			$("#tbs").val(16);
			showAndHideTabs();
			<%
			    String roles="";
				//botones
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				
				%> 
			roles="<%=roles%>";
			queryFormPost("mContratoCaratulaRead", {async: false});
			queryFormPost("mContratoHeaderRead", {async: false});
			
			queryFormPost("cargaHiperv", { async : false});
			queryFormPost("ExisteHiperv", { async : false});
			if($("#existeRegistro").val()!=0){
				actualiza=true;
			}
			ocultaTRs();
		});
		function validaCampos(){
			var token="";
			var msg="";
			var resp=true;
			if($("#cHipConvAut").val()==""){
				msg="Capturar el Hipervinculo de convocatoria autorizada.";
				token="\n";
			}
			if($("#cHipDocCont").val()==""){
				msg=msg+token+"Capturar el Hipervinculo de contrato y anexos.";
				token="\n";
			}
			if($("#cHipInfAvance").val()==""){
				msg=msg+token+"Capturar el Hipervinculo informes de avance fisico.";
				token="\n";
			}
			if($("#cHipfalloJuntaAclaraciones").val()=="" && parseInt($("#nIdCategoria").val(),10)<10){
				msg=msg+token+"Capturar el Hipervinculo fallo de la junta de aclaraciones.";
				token="\n";
			}
			if($("#cHipPresentacionPropuestas").val()=="" && parseInt($("#nIdCategoria").val(),10)<10){
				msg=msg+token+"Capturar el Hipervinculo presentaci\u00f3n de propuestas.";
				token="\n";
			}
			if($("#cHipDictamen").val()=="" && parseInt($("#nIdCategoria").val(),10)<10){
				msg=msg+token+"Capturar el Hipervinculo dictamen.";
				token="\n";
			}
			if($("#cHipAvanceFinanciero").val()=="" && parseInt($("#nIdCategoria").val(),10)<10){
				msg=msg+token+"Capturar el Hipervinculo avance financiero.";
				token="\n";
			}
			if($("#cHipRecepcionFisicaTrabajosEjec").val()=="" && parseInt($("#nIdCategoria").val(),10)<10){
				msg=msg+token+"Capturar el Hipervinculo recepci\u00f3n fisica de trabajos ejecutados.";
				token="\n";
			}
			if($("#cHipFiniquito").val()=="" && parseInt($("#nIdCategoria").val(),10)<10){
				msg=msg+token+"Capturar el Hipervinculo finiquito.";
				token="\n";
			}
			if(msg!=""){
				swal(msg,{icon:"info",button: "Cerrar"});
				resp=false;
			}
			return resp;
		}
		function actualizarDatos(){
			if(!validaCampos()){
				return;
			}
			if(actualiza){
				queryFormPost("updatemDocumentacionHipervinculo", {async : false,
				callback : function() 
					{
						//Guarda en la Bitácora
						guardaBitacora("DocumetacionHipervinculos");
						swal("Datos Guardados",{icon:"info",button: "Cerrar"});
						initQuery();
					}
				});
			}else{
				queryFormPost("insertamDocumentacionContrato", {async : false,
				callback : function() 
					{
						//Guarda en la Bitácora
						guardaBitacora("DocumetacionHipervinculos");
						swal("Datos Guardados",{icon:"info",button: "Cerrar"});
						initQuery();
					}
				});
			}
		}
		function guardaBitacora(accion){
			$("#cAccion").val(accion);
			$("#cIdDocumento").val($("#cContratoDefinitivo").val());
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
		}
		function ocultaTRs(){
			if(parseInt($("#nIdCategoria").val(),10)>=10){
				$("#trJuntaAclaraciones").hide();
				$("#trPresentacionPropuestas").hide();
				$("#trDictamen").hide();
				$("#trAvanceFinanciero").hide();
				$("#trRecepcionFisicaTrabajosEjec").hide();
				$("#trFiniquito").hide();
			}
		}
	</script>
  </head>
  
  <body>
    <form id="nuevasEps">
    	<div id="container" class="container" style="width: 90%;">
    		<fieldset>
    			<legend>Datos del Contrato</legend>
	    		<div>
	    			<table align="left">
	    				<tr>
	    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent;" id="lblUnidadEjecutora" name="lblUnidadEjecutora" readonly /></td>
	    				</tr>
	    				<tr>
	    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent;" name="lblProcedimiento" id="lblProcedimiento" readonly /></td>
	    				</tr>
	    				<tr>
	    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent;" name="lblDefinitivo" id="lblDefinitivo" readonly /></td>
	    				</tr>
	    				<tr>
	    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent;" name="lblContrato" id="lblContrato" readonly /></td>
	    				</tr>
	    				<tr>
	    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblProveedor" id="lblProveedor" readonly /></td>
	    				</tr>
	    				<tr>
	    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblEstado" id="lblEstado" readonly /></td>
	    				</tr>
	    				<tr>
	    				</tr>
	    			</table>
	    		</div>
    		</fieldset>
    		<fieldset>
    			<legend>Hiperv&iacute;nculos</legend>
    			<div>
	    			<table align="left">
	    				<tr>
	    					<td>
	    						<input name="ConvAut" id="ConvAut"  value="Convocatoria Autorizada :  "size="30" style="border-width:0; background-color:transparent ">
	    					</td>
	    					<td>
	    						<input name="cHipConvAut" id="cHipConvAut" size="55">
	    					</td>
	    				</tr>
	    				<tr>
	    					<td>
	    						<input name="DocContr" id="DocContr"  value="Contrato y Anexos :  "size="30" style="border-width:0; background-color:transparent ">
	    					</td>
	    					<td>
	    						<input name="cHipDocCont" id="cHipDocCont" size="55">
	    					</td>
	    				</tr>
	    				<tr>
	    					<td>
	    						<input name="InfAvance" id="InfAvance"  value="Informes de avance físico :  "size="30" style="border-width:0; background-color:transparent ">
	    					</td>
	    					<td>
	    						<input name="cHipInfAvance" id="cHipInfAvance" size="55">
	    					</td>
	    				</tr>
	    				
	    				<!--  -->
	    				<tr id="trJuntaAclaraciones">
	    					<td>
	    						<input name="falloJuntaAclaraciones" id="falloJuntaAclaraciones"  value="Fallo de la Junta de Aclaracioens :  "size="30" style="border-width:0; background-color:transparent ">
	    					</td>
	    					<td>
	    						<input name="cHipfalloJuntaAclaraciones" id="cHipfalloJuntaAclaraciones" size="55">
	    					</td>
	    				</tr>
	    				<tr id="trPresentacionPropuestas">
	    					<td>
	    						<input name="presentacionPropuestas" id="presentacionPropuestas"  value="Presentación de Propuestas :  "size="30" style="border-width:0; background-color:transparent ">
	    					</td>
	    					<td>
	    						<input name="cHipPresentacionPropuestas" id="cHipPresentacionPropuestas" size="55">
	    					</td>
	    				</tr>
	    				<tr id="trDictamen">
	    					<td>
	    						<input name="dictamen" id="dictamen"  value="Dictamen :  "size="30" style="border-width:0; background-color:transparent ">
	    					</td>
	    					<td>
	    						<input name="cHipDictamen" id="cHipDictamen" size="55">
	    					</td>
	    				</tr>
	    				<tr id="trAvanceFinanciero">
	    					<td>
	    						<input name="avanceFinanciero" id="avanceFinanciero"  value="Avance Financiero :  "size="30" style="border-width:0; background-color:transparent ">
	    					</td>
	    					<td>
	    						<input name="cHipAvanceFinanciero" id="cHipAvanceFinanciero" size="55">
	    					</td>
	    				</tr>
	    				<tr id="trRecepcionFisicaTrabajosEjec">
	    					<td>
	    						<input name="recepcionFisicaTrabajosEjec" id="recepcionFisicaTrabajosEjec"  value="Recep. Fisica Trabajos Ejecutados :"size="30" style="border-width:0; background-color:transparent ">
	    					</td>
	    					<td>
	    						<input name="cHipRecepcionFisicaTrabajosEjec" id="cHipRecepcionFisicaTrabajosEjec" size="55">
	    					</td>
	    				</tr>
	    				<tr id="trFiniquito">
	    					<td>
	    						<input name="finiquito" id="finiquito"  value="Finiquito :  "size="30" style="border-width:0; background-color:transparent ">
	    					</td>
	    					<td>
	    						<input name="cHipFiniquito" id="cHipFiniquito" size="55">
	    					</td>
	    				</tr>
	    				<tr >
	    					<td colspan="2"> <input type="button" id="guardarhiper" name="guardarhiper" value="Guardar" onclick="actualizarDatos()" class="btnInterfaceBG ui-button ui-corner-all"/> </td>
	    					
	    				</tr>
	    			</table>
	    		</div>	
    		</fieldset>
    	</div>
    	<input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoContrato%>"/>
		<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
		<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
		<input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
		<input type="hidden" name="cIdContratoMat" id="cIdContratoMat"/>
		<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%= usuarioTab.getLogin() %>"/>
		<input name="cIdDocumento" id="cIdDocumento" type="hidden">
		<input type="hidden" name="cIdRFC" id="cIdRFC"/>
		<input type="hidden" name="cIdProcedimiento" id="cIdProcedimiento"/>
		<input type="hidden" name="cIdUnidadEjecutoraEP" id="cIdUnidadEjecutoraEP" value="<%= usuarioTab.getU_UR()%>" />
		<input type="hidden" id="cContratoDefinitivo" name="cContratoDefinitivo" value="1"/>
		<input type="hidden" id="cAccion" name="cAccion" value="1"/>
		<input type="hidden" name="existeRegistro" id="existeRegistro" value="0"/>
		<input type="hidden" name="nIdCategoria" id="nIdCategoria" value="0"/>
    </form>
  </body>
</html>
