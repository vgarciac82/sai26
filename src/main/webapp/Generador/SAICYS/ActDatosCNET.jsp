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
   
    
    <title>My JSP 'ActDatosCNET.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script type="text/javascript" charset="utf-8">
		var roles='';
		$(document).ready(function() {
			$("#tbs").val(17);
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
			queryFormPost("fnMontoSubtotalReadContrato", {async: false});
			queryFormPost("fnMontoIVAReadContrato", {async: false});
			queryFormPost("fnMontoTotalContrato", {async: false});
			queryFormPost("mContratoCategoriaProcemientoRead", {async: false});
			init();
		});
		function init(){
			if(parseInt($("#cIdCategoriaProcedimiento").val(),10)!=11){
				$("#trCodContCNET").css("display", "block");
				$("#trCodExpCNET").css("display", "block");
				$("#trNumProcedimientoCNET").css("display", "block");
			}else{
				$("#inpNumContCNET").val("No. Convenio de Colaboraci\u00f3n");
			}
		}
		function actualiza(){
			if(parseInt($("#cIdCategoriaProcedimiento").val(),10)!=11){
				swal({
					title: "Seguro que desea actualizar los datos?",
					text: "",
					icon: "info",
					buttons: {
						confirm : "Aceptar",
						cancel: "Cancelar"
						},
					}).then((continuar) => {
						if (!continuar) {
							return;
						}else{
							queryFormPost("actualizaDatCNET", {async: false,
								callback:function(){
									swal({
										title: "",
										text: "Datos Actualizados",
										icon: "info",
										buttons: {
											confirm : "Cerrar"
											},
										}).then((continuar) => {
											location.reload();
									});
								}
							});
						}
				});
			}else{
				swal("Para Adjudicación Directa Artículo 1° de la Ley LAASSP, No se pueden actualizar estos datos.",{icon:"info",button: "Cerrar"});
			}
			
		}
	</script>
  </head>
  
  <body>
    <form action="">
    	<div id="container" class="container" style="width: 90%;">
    		<fieldset>
    			<legend>Datos del Contrato</legend>
    			<table align="left">
    				<tr>
    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent" id="lblUnidadEjecutora" name="lblUnidadEjecutora" readonly /></td>
    				</tr>
    				<tr>
    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblProcedimiento" id="lblProcedimiento" readonly /></td>
    				</tr>
    				<tr>
    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblDefinitivo" id="lblDefinitivo" readonly /></td>
    				</tr>
    				<tr>
    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblContrato" id="lblContrato" readonly /></td>
    				</tr>
    				<tr>
    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblProveedor" id="lblProveedor" readonly /></td>
    				</tr>
    				<tr>
    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblEstado" id="lblEstado" readonly /></td>
    				</tr>
    				<tr>
    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblSubtotal" id="lblSubtotal" readonly /></td>
    				</tr>
    				<tr>
    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteIVA" id="lblImporteIVA" readonly /></td>
    				</tr>
    				<tr>
    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblTotal" id="lblTotal" readonly /></td>
    				</tr>
    			</table>
    		</fieldset>
    		<fieldset>
    			<legend>Actualiza datos de CNET</legend>
    			<table style="width: 100%" align="left" >
    				<tr>
			    		
			    		<td align="left" colspan="2">
			    		<input type="text" style="width: 220px;border-width:0; background-color:transparent" name="inpNumContCNET" id="inpNumContCNET" value="No. de Contrato Compranet" readonly />
			    		<input type="text" id="cnumCompranet" name="cnumCompranet" style="width: 400px" maxlength="100"/></td>
			    	</tr>
			    	<tr id="trNumProcedimientoCNET" >
			    		<td align="left">N&uacute;mero de Procedimiento Compranet</td>
			    		<td align="left"><input type="text" id="cNumProcedCNET" name="cNumProcedCNET" style="width: 398px" maxlength="150" /></td>
			    	</tr>
    				<tr id="trCodExpCNET" >
			    		<td align="left">C&oacute;digo de Expediente Compranet</td>
			    		<td align="left"><input type="text" id="nCodExpedienteCNET" name="nCodExpedienteCNET" style="width: 220px" maxlength="150" /></td>
			    	</tr>
			    	<tr id="trCodContCNET" >
			    		<td align="left">C&oacute;digo de Contrato Compranet</td>
			    		<td align="left"><input type="text" id="nCodContratoCNET" name="nCodContratoCNET" style="width: 220px" maxlength="150" /></td>
			    	</tr>
    				<tr>
    					<td colspan="2">
    						<input type="button" id="" name="" value="Actualizar" title="Actualiza datos de CNET" onclick="actualiza();" class="btnInterfaceBG ui-button ui-corner-all"/>
    					</td>
    				</tr>
    			</table>
    		</fieldset>
    	</div>
    	<input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoContrato%>"/>
		<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
		<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
		<input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
		<input type="hidden" name="cIdContratoMat" id="cIdContratoMat"/>
		<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuarioTab.getLogin() %>"/>
		<input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />
		<input name="cIdDocumento" id="cIdDocumento" type="hidden">
		<input type="hidden" name="cIdRFC" id="cIdRFC"/>
		<input type="hidden" name="cIdProcedimiento" id="cIdProcedimiento"/>
		<input type="hidden" name="cIdUnidadEjecutoraEP" id="cIdUnidadEjecutoraEP" value="<%= usuarioTab.getU_UR()%>" />
		<input type="hidden" id="Partida" name="Partida" value="2"/>
		<input type="hidden" id="Partida2" name="Partida2" value="3"/>
		<input type="hidden" id="Partida3" name="Partida3" value="5"/>
		<input type="hidden" id="Partida1" name="Partida1" value="1"/>
		<input type="hidden" id="cContratoDefinitivo" name="cContratoDefinitivo" value="1"/>
		<input type="hidden" id="ClaveInterna" name="ClaveInterna" value="1"/>
		
		<input type="hidden" id="cEjercicioTbl" name="cEjercicioTbl" value="<%=cEjercicio%>"/>
		<input type="hidden" id="cIdContratoTbl" name="cIdContratoTbl" value=""/>
		<input type="hidden" id="cTContratoTbl" name="cTContratoTbl" value="DI"/>
		<input type="hidden" id="nIdClaveEgresos" name="nIdClaveEgresos" value=""/>
		<input type="hidden" id="cIdEntidadContableTbl" name="cIdEntidadContableTbl" value="<%= usuarioTab.getPropiedad("CCENTROCONTABLE").getValor()%>"/>
		<input type="hidden" name="cuentaDisponible" id="cuentaDisponible" value="82106" />
		<input type="hidden" name="cIdCategoriaProcedimiento" id="cIdCategoriaProcedimiento" />
		<input type="hidden" name="ValObtenidoFL" id="ValObtenidoFL" />
    </form>
  </body>
</html>
