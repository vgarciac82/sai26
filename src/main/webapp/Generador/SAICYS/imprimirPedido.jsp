<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import=" java.sql.CallableStatement" %>

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
	String cIdTipoPedido= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";	
	if (session.getAttribute(GestionInterface.ATT_PedidoEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_PedidoEjercicio);
		cIdTipoPedido = (String)session.getAttribute(GestionInterface.ATT_PedidoTipoPedido);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_PedidoUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_PedidoConsecutivo);		
	}else 
		response.sendRedirect("Pedidos.jsp?tab=0");	
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
		$(document).ready(function() {
			$("#tbs").val(6);
			showAndHideTabs();
			<%
			    String roles="";
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				int cmdPdfPedido=0;
				int cmdPdfAnexoA=0;
				int cmdPdfAnexoURM=0;
				int cmdPdfResumen=0;
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"Pedidos","imprimirPedido");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%   
					String img=(String) b.getValue();
					if ("cmdPdfPedido".equals(img)){  
						cmdPdfPedido=1;
					}
					if ("cmdPdfAnexoAlmacen".equals(img)){
						cmdPdfAnexoA=1; 
					}
					if ("cmdPdfAnexoURM".equals(img)){
						cmdPdfAnexoURM=1; 
					}
					if ("cmdPdfResumen".equals(img)){
						cmdPdfResumen=1; 
					}	
				}
				
				%>
		
			//asigna el valor de true a readonly para los campos que sirven de etiquetas
			document.getElementById("lblUnidadEjecutora").style.readonly=true;
			document.getElementById("lblProcedimiento").style.readonly=true;
			document.getElementById("lblDefinitivo").style.readonly=true;
			document.getElementById("lblPedido").style.readonly=true;
			document.getElementById("lblProveedor").style.readonly=true;
			document.getElementById("lblEstado").style.readonly=true;
			document.getElementById("lblTotal").style.readonly=true;
			//Click para seleccionar una clausula
			$('#tblPagos tr').live('click', function() {        
						$(oTableClausulas.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
						});						
						$(this).addClass('row_selected');   
						var anSelected = fnGetSelected( oTableClausulas );						
						var aData = oTableClausulas.fnGetData(anSelected[0]);					
			});
			//lectura de caratula
			queryFormPost("mPedidoCaratulaRead", {async: false});
			queryFormPost("mPedidoHeaderRead", {async: false});
			//En el click lista el clausulado
			$('#edit').click( function () {
        		    window.location = 'Pedidos.jsp?tab=8&cPedidoDefinitivo='+$("#cPedidoDefinitivo").val()+"_nuevo";
   			 } );
			$("#editClausula").click(function(){
					window.location = 'Pedidos.jsp?tab=8&cPedidoDefinitivo='+$("#cPedidoDefinitivo").val()+"_edit";
			});
				queryFormPost("mClausuladoRead", {async: false});
		if ($("#countClausulas").val()!=0){
			document.getElementById("editClausula").style.display="block";
			document.getElementById("edit").style.display="none";
			
		}else{	
			document.getElementById("editClausula").style.display="none";
			document.getElementById("edit").style.display="block";
		}

		queryFormPost("fnMontoSubtotalRead", {async: false});
		queryFormPost("fnMontoIVARead", {async: false});
		queryFormPost("fnMontoImpuesto1Read", {async: false});
		queryFormPost("fnMontoImpuesto2Read", {async: false});
		queryFormPost("fnMontoImpuesto3Read", {async: false});

		if($("#lblImporteImpuesto1").val() == ""){
			$("#divImporteImpuesto1").css("display","none");
		}
		if($("#lblImporteImpuesto2").val() == ""){
			$("#divImporteImpuesto2").css("display","none");
		}
		if($("#lblImporteImpuesto3").val() == ""){
			$("#divImporteImpuesto3").css("display","none");
		}
		queryFormPost("fnMontoTotalPedido", {async: false});
		
			if ($("#cIdTipoProcedimiento").val() == 'PF') {
				$( "#presupuestoPedido" ).attr("disabled", true);
				$( "#preCompromisoPedido" ).attr("disabled", true);
				$( "#pagosPedido" ).attr("disabled", true);
			}
			var nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoPedido)%>;
			if(nIdEstado == 1)
				$("#preCompromisoPedido").css("display", "none");
			else
				$("#preCompromisoPedido").css("display", "block");
		
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
			var cmdPdfAnexoA='<%=cmdPdfAnexoA%>';
			var cmdPdfAnexoURM='<%=cmdPdfAnexoURM%>';
			var cmdPdfPedido='<%=cmdPdfPedido%>';
			var cmdPdfResumen='';
			
			switch(reporte){
			case "Pedido.jasper":
				if(cmdPdfPedido==0){
					if(ext!='csv'){
						window.open(
						"../../servlet/SeguridadCatalogosMateriales?"
						+ "catalogo=REPORTE"
						+ "&accion=run"
						+ "&rn="+reporte
						+ "&cEjercicio=" + $("#cEjercicio").val()
						+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
						+ "&cIdTipoPedido=" + $("#cIdTipoPedido").val()
						+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val()
						+ "&formato="+ext
						+ "&cPedidoDefinitivo="+$("#cPedidoDefinitivo").val()
						+ "&cIdRFC=" + $("#cIdRFC").val()
						+ "&directorio=temporal",
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
						+ "&cIdTipoPedido=" + $("#cIdTipoPedido").val()
						+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val()
						+ "&formato="+ext
						+ "&cPedidoDefinitivo="+$("#cPedidoDefinitivo").val()
						+ "&cIdRFC=" + $("#cIdRFC").val()
						+ "&directorio=temporal",
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");
					}
				}else {
					swal("No tiene permisos para generar este reporte",{icon:"info",button: "Cerrar"});
				}
			break;
			case "rptPedidoAnexo1.jasper":
				if(cmdPdfAnexoA==0){
					if(ext!='csv'){
						window.open(
					"../../servlet/SeguridadCatalogosMateriales?"
						+ "catalogo=REPORTE"
						+ "&accion=run"
						+ "&rn="+reporte
						+ "&cEjercicio=" + $("#cEjercicio").val()
						+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
						+ "&cIdTipoPedido=" + $("#cIdTipoPedido").val()
						+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val()
						+ "&formato="+ext,
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
						+ "&cIdTipoPedido=" + $("#cIdTipoPedido").val()
						+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val()
						+ "&formato="+ext,
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");
					}
				}else {
					swal("No tiene permisos para generar este reporte.",{icon:"info",button: "Cerrar"});
				}
			break;
			case "rptPedidoAnexo2.jasper":
				if(cmdPdfAnexoURM==0){
					if(ext!='csv'){
						window.open(
					"../../servlet/SeguridadCatalogosMateriales?"
						+ "catalogo=REPORTE"
						+ "&accion=run"
						+ "&rn="+reporte
						+ "&cEjercicio=" + $("#cEjercicio").val()
						+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
						+ "&cIdTipoPedido=" + $("#cIdTipoPedido").val()
						+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val()
						+ "&formato="+ext,
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
						+ "&cIdTipoPedido=" + $("#cIdTipoPedido").val()
						+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val()
						+ "&formato="+ext,
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");
					}
				}else {
					swal("No tiene permisos para generar este reporte.",{icon:"info",button: "Cerrar"});
				}
			break;
			case "rptPedidoClavesComplementarias.jasper":
				if(cmdPdfAnexoURM==0){
					if(ext!='csv'){
						window.open(
						"../../servlet/SeguridadCatalogosMateriales?"
						+ "catalogo=REPORTE"
						+ "&accion=run"
						+ "&rn="+reporte
						+ "&cEjercicio=" + $("#cEjercicio").val()
						+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
						+ "&cIdTipoPedido=" + $("#cIdTipoPedido").val()
						+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val()
						+ "&formato="+ext,
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
						+ "&cIdTipoPedido=" + $("#cIdTipoPedido").val()
						+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val()
						+ "&formato="+ext,
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");
					}
				}else {
					swal("No tiene permisos para generar este reporte.",{icon:"info",button: "Cerrar"});
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
							+ "&documentoDefinitivo=" + $("#cPedidoDefinitivo").val()
							+ "&documento=" + $("#cIdTipoPedido").val() + $("#cIdUnidadEjecutora").val() + $("#nIdConsecutivo").val()
							+ "&formato=" + ext,
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");
					}
					else{
						swal("No tiene permisos para generar este reporte.",{icon:"info",button: "Cerrar"});1
						/*window.open(
						"../../servlet/CatalogosCSV?"
							+ "catalogo=REPORTE"
							+ "&accion=run"
							+ "&rn="+reporte
							+ "&cEjercicio=" + $("#cEjercicio").val()
							+ "&unidadEjecutoraStr=" + $("#lblUnidadEjecutora").val()
							+ "&documentoDefinitivo=" + $("#lblDefinitivo").val()
							+ "&documento=" + $("#lblContrato").val()
							+ "&formato=" + ext,
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");*/
					}
				}
				else{
					swal("No tiene permisos.",{icon:"info",button: "Cerrar"});
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
								<input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	  onclick="window.location = 'Pedidos.jsp?tab=0';" />
				    		</td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" id="lblUnidadEjecutora" name="lblUnidadEjecutora" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblProcedimiento" id="lblProcedimiento" readonly  /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 400px;border-width:0; background-color:transparent" name="lblDefinitivo" id="lblDefinitivo" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 400px;border-width:0; background-color:transparent" name="lblPedido" id="lblPedido" readonly /></td>
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
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteIVA" id="lblImporteIVA" readonly /></td>
				    	</tr>
				    	<tr id="divImporteImpuesto1">
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteImpuesto1" id="lblImporteImpuesto1" readonly /></td>
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
					<a href="javascript:void(0)" id="edit">Clausulas</a>
					<a href="javascript:void(0)" id="editClausula">Editar Clausulas</a>
					<table width="97%" align="left">
						<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="Pedido.jasper" CHECKED>Pedido </td></tr>
						<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rptPedidoAnexo1.jasper">Pedido (Anexo1)</td></tr>
						<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rptPedidoAnexo2.jasper">Pedido (Anexo2)</td></tr>
						<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rptDocumentoResumenClaves.jasper">Pedido (Resumen de Claves Presupuestales)</td></tr>
						<tr><td align="left"><INPUT TYPE="radio" NAME="REPORTE" VALUE="rptPedidoClavesComplementarias.jasper">Claves Complementarias</td></tr>
						
						<tr><td align="center">
						<input type="button" class="btnInterfacePDF ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdPdfPedido" 			name="cmdPdfPedido" 		value="PDF"		onclick="openPDF('pdf');" />&nbsp;&nbsp;
		    			<input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdPdfAnexoAlmacen" 	name="cmdPdfAnexoAlmacen" 	value="Excel"	onclick="openPDF('xls');" />&nbsp;&nbsp;
		    			<input type="button" class="btnInterfaceCSV ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdcsvReporteProg" 		name="cmdcsvReporteProg" 	value="CSV"		onclick="openPDF('csv');" />&nbsp;&nbsp;
						<input type="button" class="btnInterfaceDOC ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdPdfAnexoURM" 		name="cmdPdfAnexoURM" 		value="Word"	onclick="openPDF('doc');" />&nbsp;&nbsp;
						</td></tr>
					</table>
			</fieldset>
					
			<input type="hidden" name="cIdTipoPedido" id="cIdTipoPedido" value="<%=cIdTipoPedido%>" />
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
		    <input type="hidden" name="cPedidoDefinitivo" id="cPedidoDefinitivo" />
		    <input type="hidden" name="cIdUsuarioAprobacion" id="cIdUsuarioAprobacion" value="<%=usuarioTab.getLogin()%>"/>
		    <input type="hidden" name="cIdUsuarioAnulacion" id="cIdUsuarioAnulacion" value="<%=usuarioTab.getLogin()%>"/>	
		    <input type="hidden" name="countClausulas" id="countClausulas" value=""/>
		    <input type="hidden" name="cIdRFC" id="cIdRFC" value=""/>
	    </div>
	</form>
  </body>
</html>
