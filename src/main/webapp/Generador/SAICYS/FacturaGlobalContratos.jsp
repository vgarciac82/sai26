<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
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
	String cIdContratoDefinitivo="";	
	Caso c =null;
		
	if (session.getAttribute(GestionInterface.ATT_ContratoEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoEjercicio);
		cIdTipoContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoTipoContrato);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ContratoUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ContratoConsecutivo);
		cIdContratoDefinitivo=(String)session.getAttribute(GestionInterface.ATT_ContratoDefinitivo);
		CasoBusinessLogic ct = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
		c = ct.consultaCasoContDiv(cIdContratoDefinitivo);
		session.setAttribute(GestionInterface.ATT_CASE, c);
	}else{
		response.sendRedirect("Contratos.jsp?tab=0");
	}
		
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>My JSP 'FacturaGlobalContratos.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script type="text/javascript" charset="utf-8">
		var roles='';
		$(document).ready(function() {
			$("#tbs").val(18);
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
			togleDivFacts(0);
			//functionIframeFact();
		});
		function functionIframeFact(){
			var rfc=$("#cIdRFC").val();
			rfc=rfc.replace("-", "");
			rfc=rfc.replace("-", "");
			$('#uploadFacturasFrm').attr('src', '../UploadCFDIContrato.jsp?TipoContrato=DI&IDContrato='+$("#cContratoDefinitivo").val()+'&RFC='+rfc
				+'&montoTotal='+$("#mMontoNetoContrato").val()+'&montoIVA='+$("#importeIVA").val() +'&folioSAI='+$("#C_FOLIO").val());
				
				
		}
		function togleDivFacts(nIdDiv) {
			var rfc=$("#cIdRFC").val();
			rfc=rfc.replace("-", "");
			rfc=rfc.replace("-", "");
			if (nIdDiv == 0) {
				$("#uploadFacturasDiv").hide();
				$("#facturasCapturadasDiv").show();
				creaDTFacturas();
			} else {
				var montoTotalContrato=$("#mMontoNetoContrato").val();
				var montoIVAContrato=$("#importeIVA").val();
				if($("#isPlurianual").val()==1){
					montoTotalContrato=$("#totalPluri").val();
					montoIVAContrato=$("#montoIVAPluri").val();
				}
				$('#uploadFacturasFrm').attr('src', '../UploadCFDIContrato.jsp?TipoContrato=DI&IDContrato='+$("#cContratoDefinitivo").val()+'&RFC='+rfc
				+'&montoTotal='+unFrmt2(montoTotalContrato)+'&montoIVA='+unFrmt2(montoIVAContrato)+'&folioSAI='+$("#C_FOLIO").val());
				$("#uploadFacturasDiv").show();
				$("#facturasCapturadasDiv").hide();
			}
		}
		function leeMontosFacturas() {
			queryFormPost({
				queryName : "readMontoFacturasPO",
				async : false,
				callback : function() {
					if (Number($("#mImporteBruto").val())==0){
						$("#mImporteBruto").val(importeBruto);
						$("#mImporteIVA").val($("#mIvaFactura").val());
						$("#subTotal_1").val(Number($("#mImporteBruto").val())-Number($("#mImporteSancion").val())+Number($("#mImporteDevolucion").val()));
						$(".subtotall").change();				
					}
					res_iva = parseFloat(quitaFmt($("#mImporteIVA").val() == "" ? "0"
							: $("#mImporteIVA").val()));
				}
			});
			calculaIVA();//SASV para actualizar los montos al cargar la factura.
		}
		function creaDTFacturas(){
			oTablevFact = $('#grdValidaFacturas').dataTable(
			{
				"bProcessing": true,
				"bServerSide": true,
				"bDestroy": true,
				"bSort": true, 
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tContratoFactura&qw=cIDContrato='"+$("#cContratoDefinitivo").val()+"' AND cTipoContrato='DI'" ,
				"bJQueryUI": true,
				"sScrollX": "100%",
				//"sScrollXInner": "100%",
				"sScrollY": "190px",
				"bPaginate": false,
				"bAutoWidth": true,
				"bInfo": true,
				aoColumns: [
					{ sName: "cFactura" },
					{ sName: "mImporteSinImpuestos" },
					{ sName: "mImporteConImpuestos" },
					{ sName: "mimporteIVA" } ]	
			});
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
    			<legend>Cargar factura global</legend>
    			<div id="dialog-validaFact" title="Carga de Facturas de Contrato">
					<div id="uploadFacturasDiv" style="width: 100%">
						<iframe id="uploadFacturasFrm" align="top" frameborder="0" style="width: 100%;">
						</iframe>
					</div>
					<div id="facturasCapturadasDiv">
						<table width="100%">
							<tr id="uploadFacturasTR">
								<td align="right">
									<a href="#" onclick="togleDivFacts(1);return false;" >Cargar Facturas</a>
								</td>
							</tr>
						</table>
						<fieldset>
						<legend>Facturas Capturadas.</legend>
						<table id="grdValidaFacturas" style="width: 100%">
								<thead>
									<tr>
										<th>Serie de la Factura</th>
										<th>Importe Sin Impuestos</th>
										<th>Importe con Impuestos</th>
										<th>Importe  IVA</th>
									</tr>
								</thead>
							</table>					
						</fieldset>
					</div>
				</div>
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
		<input type="hidden" id="importeIVA" name="importeIVA" value=""/>
		<input type="hidden" id="mMontoNetoContrato" name="mMontoNetoContrato" value=""/>
		<input type="hidden" id="nIdClaveEgresos" name="nIdClaveEgresos" value=""/>
		<input type="hidden" id="cIdEntidadContableTbl" name="cIdEntidadContableTbl" value="<%= usuarioTab.getPropiedad("CCENTROCONTABLE").getValor()%>"/>
		<input type="hidden" id="isPlurianual" name="isPlurianual" value=""/>
		<input type="hidden" id="montoSinIVAPluri" name="montoSinIVAPluri" value=""/>
		<input type="hidden" id="totalPluri" name="totalPluri" value=""/>
		<input type="hidden" id="montoIVAPluri" name="montoIVAPluri" value=""/>
		<input type="hidden" id="C_FOLIO" name="C_FOLIO" value=""/>
    </form>
  </body>
</html>
