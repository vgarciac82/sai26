<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
String name_user=usuarioTab.getLogin();
	String idRol="0";		
	Map rol =usuarioTab.getRoles();
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}	
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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>    
    <title>Imprimir</title>    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">	
		
	<script type="text/javascript" charset="utf-8">
		var oTableClausulas;
		cIdTipoContrato="<%=cIdTipoContrato%>";
		$(document).ready(function() {
			$("#tbs").val(6);
			showAndHideTabs();
		<%
			    String roles="";
			    //botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				int cmdPdfContratoAnexo1=0;
				int cmdPdfContratoAnexo2=0;
				int cmdPdfResumen=0;
			
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"Contratos","imprimirContrato");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%   
					String img=(String) b.getValue();
					if ("cmdPdfContratoAnexo1".equals(img)){  
						cmdPdfContratoAnexo1=1;
					}
					if ("cmdPdfContratoAnexo2".equals(img)){
						cmdPdfContratoAnexo2=1; 
					}
					if ("cmdPdfResumen".equals(img)){
						cmdPdfResumen=1; 
					}
				}
				
				%>
			var nom_reporte	
				//asigna el valor de true a readonly para los campos que sirven de etiquetas
			document.getElementById("lblUnidadEjecutora").style.readonly=true;
			document.getElementById("lblProcedimiento").style.readonly=true;
			document.getElementById("lblDefinitivo").style.readonly=true;
			document.getElementById("lblContrato").style.readonly=true;
			document.getElementById("lblProveedor").style.readonly=true;
			document.getElementById("lblEstado").style.readonly=true;
			document.getElementById("lblTotal").style.readonly=true;		
			queryFormPost("mContratoCaratulaRead", {async: false});
			queryFormPost("mContratoHeaderRead", {async: false});
			queryFormPost("fnMontoSubtotalReadContrato", {async: false});
			queryFormPost("fnMontoIVAReadContrato", {async: false});
			queryFormPost("fnMontoImpuesto1ReadContrato", {async: false});
			queryFormPost("fnMontoImpuesto2ReadContrato", {async: false});
			queryFormPost("fnMontoImpuesto3ReadContrato", {async: false});
			
			if($("#cIdTipoContrato").val() == "CA"){
				$("#radio_reporte").val("rptContratoArrendamiento.jasper");
						
			}else{
				$("#radio_reporte").val("rptContratoAnexo1.jasper");
			
			}
			
			
			if($("#lblImporteImpuesto1").val() == ""){
				$("#divImporteImpuesto1").css("display","none");
			}
			if($("#lblImporteImpuesto2").val() == ""){
				$("#divImporteImpuesto2").css("display","none");
			}
			if($("#lblImporteImpuesto3").val() == ""){
				$("#divImporteImpuesto3").css("display","none");
			}

			queryFormPost("fnMontoTotalContrato", {async: false});
			
			if ($("#cIdTipoProcedimiento").val() == 'PF') {
				$( "#presupuestoContrato" ).attr("disabled", true);
				$( "#preCompromisoContrato" ).attr("disabled", true);
				$( "#pagosContrato" ).attr("disabled", true);
				$( "#plurianualidad" ).attr("disabled", true);
				$( "#pasivo" ).attr("disabled", true);
			}
			var nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoContrato)%>;
			if(nIdEstado == 1)
				$("#preCompromisoContrato").css("display", "none");
			else
				$("#preCompromisoContrato").css("display", "block");
		});	
		function formateaMoneda(importe){
			var importeSeparado = importe.toString().split("\.");
			var importeParte1 = importeSeparado[0];
			var cont=0;
			var tem="";
			
			for(var i=importeParte1.length; i>0; i--){
				if(cont == 3){
					tem = ","+tem;
					cont=0;
				}
				tem = importeParte1.substring(i-1,i)+tem;
				cont++;
			}
			if(importe.toString().indexOf("\.")>0){
				for(var i=importeSeparado[1].length; i<2; i++){
					importeSeparado[1]+="0";
				}
				return tem+"."+importeSeparado[1];
			}
			else{
				return tem+".00";
			}
		}
		/* Get the rows which are currently selected */
		function fnGetSelected( oTableLocal )
		{
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();
			
			for ( var i=0 ; i<aTrs.length ; i++ )
			{
				if ( $(aTrs[i]).hasClass('row_selected') )
				{
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
		}
		function openPDF(ext){
			var reporte=$('[name="REPORTE"]:checked').val();
			var cmdPdfContratoAnexo1='<%=cmdPdfContratoAnexo1%>';
			var cmdPdfContratoAnexo2='<%=cmdPdfContratoAnexo2%>';
			var cmdPdfResumen='<%=cmdPdfResumen%>';
			switch(reporte){
			case "rptContratoArrendamiento.jasper":
			if(cmdPdfContratoAnexo1==0){
				if(ext!='csv'){
					window.open(
					"../../servlet/SeguridadCatalogosMateriales?"
						+ "catalogo=REPORTE"
						+ "&accion=run"
						+ "&rn="+reporte
						+ "&cEjercicio=" + $("#cEjercicio").val()
						+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
						+ "&cIdTipoContrato=" + $("#cIdTipoContrato").val()
						+ "&formato=" + ext
						+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val(),
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768");
				}
				else{
					window.open(
					"../../servlet/CatalogosCSV?"
						+ "catalogo=REPORTE"
						+ "&accion=run"
						+ "&rn="+reporte
						+ "&cEjercicio=" + $("#cEjercicio").val()
						+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
						+ "&cIdTipoContrato=" + $("#cIdTipoContrato").val()
						+ "&formato=" + ext
						+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val(),
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768");
				}
			}else{
				swal("No tiene permisos",{icon:"info",button: "Cerrar"});
			}
			break;
			////////////////////////////////////////////////////
			case "rptContratoAnexo1.jasper":
			if(cmdPdfContratoAnexo1==0){
			 	if(ext!='csv'){
					window.open(
					"../../servlet/SeguridadCatalogosMateriales?"
						+ "catalogo=REPORTE"
						+ "&accion=run"
						+ "&rn="+reporte
						+ "&cEjercicio=" + $("#cEjercicio").val()
						+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
						+ "&cIdTipoContrato=" + $("#cIdTipoContrato").val()
						+ "&formato=" + ext
						+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val(),
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768");
				}
				else{
					window.open(
					"../../servlet/CatalogosCSV?"
						+ "catalogo=REPORTE"
						+ "&accion=run"
						+ "&rn="+reporte
						+ "&cEjercicio=" + $("#cEjercicio").val()
						+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
						+ "&cIdTipoContrato=" + $("#cIdTipoContrato").val()
						+ "&formato=" + ext
						+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val(),
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768");
				}
			}else{
				swal("No tiene permisos",{icon:"info",button: "Cerrar"});
			}
			break;
			
			////////////////////////////////////////////////////////////////////////////////////
			case "rptContratoAnexo.jasper":
				if(cmdPdfContratoAnexo2==0){
					if(ext!='csv'){
						window.open(
						"../../servlet/SeguridadCatalogosMateriales?"
							+ "catalogo=REPORTE"
							+ "&accion=run"
							+ "&rn="+reporte
							+ "&formato="+ext
							+ "&cEjercicio=" + $("#cEjercicio").val()
							+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
							+ "&cIdTipoContrato=" + $("#cIdTipoContrato").val()
							+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val(),
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");
	
					}
					else{
						window.open(
						"../../servlet/CatalogosCSV?"
							+ "catalogo=REPORTE"
							+ "&accion=run"
							+ "&rn="+reporte
							+ "&formato="+ext
							+ "&cEjercicio=" + $("#cEjercicio").val()
							+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
							+ "&cIdTipoContrato=" + $("#cIdTipoContrato").val()
							+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val(),
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");
					}
				}
				else{
					swal("No tiene permisos",{icon:"info",button: "Cerrar"});
				}
			break;
			case "rptDocumentoResumenClaves.jasper":
				if(cmdPdfResumen==0){
					if(ext!='csv'){
						var descipcionUR = $("#lblUnidadEjecutora").val().split("-");
						window.open(
						"../../servlet/SeguridadCatalogosMateriales?"
							+ "catalogo=REPORTE"
							+ "&accion=run"
							+ "&rn="+reporte
							+ "&cEjercicio=" + $("#cEjercicio").val()
							+ "&unidadEjecutoraStr=" + descipcionUR[1]
							+ "&documentoDefinitivo=" + $("#cContratoDefinitivo").val()
							+ "&documento=" + $("#cIdTipoContrato").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val()  
							+ "&formato=" + ext,
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");
					}
					else{
						swal("El reporte no esta disponible en este formato.",{icon:"info",button: "Cerrar"});
					}
				}else{
					swal("No tiene permisos",{icon:"info",button: "Cerrar"});
				}
			break;
		}
	}
	
		function muestraClausulas(){
			document.getElementById("tdClausulas").style.display="display";	
		}
	</script>
	
  </head>  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form> 
		<div id="container" class="container" style="width: 95%">
			
			<fieldset>
				<legend>Imprimir</legend>
					<table align="left" cellpadding="2" width="100%">
				    	<tr>
				    		<td align="right" colspan="2">
								<input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	  onclick="window.location = 'Contratos.jsp?tab=0';" />
				    		</td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" id="lblUnidadEjecutora" name="lblUnidadEjecutora" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblProcedimiento" id="lblProcedimiento" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 400px;border-width:0; background-color:transparent" name="lblDefinitivo" id="lblDefinitivo" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 400px;border-width:0; background-color:transparent" name="lblContrato" id="lblContrato" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblProveedor" id="lblProveedor" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 500px;border-width:0; background-color:transparent" name="lblEstado" id="lblEstado" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblSubtotal" id="lblSubtotal" readonly /></td>
				    	</tr>
				    	<tr id="divImporteIVA">
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteIVA" id="lblImporteIVA" readonly/></td>
				    	</tr>
				    	<tr id="divImporteImpuesto1">
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteImpuesto1" id="lblImporteImpuesto1" readonly/></td>
				    	</tr>
				    	<tr id="divImporteImpuesto2">
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteImpuesto2" id="lblImporteImpuesto2" readonly /></td>
				    	</tr>
				    	<tr id="divImporteImpuesto3">
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteImpuesto3" id="lblImporteImpuesto3" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblTotal" id="lblTotal" readonly /></td>
				    	</tr>							    	
				    </table>
			</fieldset>	
			<fieldset>
				<legend>Imprimir</legend>
					<table width="97%" align="left">
						<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" id="radio_reporte"  CHECKED>Resumen del Contrato</td></tr>
						<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rptContratoAnexo.jasper">Claves Complementarias</td></tr>
						<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rptDocumentoResumenClaves.jasper">Contrato (Resumen de Claves Presupuestales)</td></tr>
						
						<tr><td align="center">
						<input type="button" class="btnInterfacePDF ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdPdfContrato" 		name="cmdPdfContrato" 		value="PDF"		onclick="openPDF('pdf');" />&nbsp;&nbsp;
		    			<input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdXlsContrato" 		name="cmdXlsContrato" 		value="Excel"	onclick="openPDF('xls');" />&nbsp;&nbsp;
		    			<input type="button" class="btnInterfaceCSV ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdCsvContrato" 		name="cmdCsvContrato" 		value="CSV"		onclick="openPDF('csv');" />&nbsp;&nbsp;
						<input type="button" class="btnInterfaceDOC ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdWordContrato" 		name="cmdWordContrato" 		value="Word"	onclick="openPDF('doc');" />&nbsp;&nbsp;
						</td></tr>
						
				</table>
			</fieldset>
					
			<input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoContrato%>" />
			<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>" />
			<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>" />
			<input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>" />			
			<input type="hidden" name="nIdConsecutivoProcedimiento" id="nIdConsecutivoProcedimiento" />
	    	<input type="hidden" name="cIdTipoProcedimiento" id="cIdTipoProcedimiento" />
			<input type="hidden" name="nIdFirmante" id="nIdFirmante" />	
			<input type="hidden" name="nNumeroFirmante" id="nNumeroFirmante" />		
			<input type="hidden" name="nIdEstadoReq" id="nIdEstadoReq" />		    
		    <input type="hidden" name="fAprobacion" id="fAprobacion" />
		    <input type="hidden" name="fAnulacion" id="fAnulacion" />
		    <input type="hidden" name="nIdEstado" id="nIdEstado" />		     
		    <input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" />
		    <input type="hidden" name="cIdUsuarioAprobacion" id="cIdUsuarioAprobacion" value="<%=usuarioTab.getLogin()%>"/>
		    <input type="hidden" name="cIdUsuarioAnulacion" id="cIdUsuarioAnulacion" value="<%=usuarioTab.getLogin()%>"/>	
	    </div>
	</form>
  </body>
</html>
