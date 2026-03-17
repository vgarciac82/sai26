<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioTab == null){
		response.sendRedirect("../../index.jsp");
		return;
	}
	String role="";
	Map rol =usuarioTab.getRoles();
	String cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioEjercicio);
	String	cIdContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioId);
	String	cIdContratoDefinitivo = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo);
	String	cIdConsecutivoMod = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioConsecutivo);
	
	String cIdTipoContrato = cIdContrato.split("-")[0];
	String cIdUnidadEjecutora = cIdContrato.split("-")[1];
	String nIdConsecutivo = cIdContrato.split("-")[2];
%>

<!DOCTYPE html>
<html>
  <head>
    
    <title>Imprimir Convenios.</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" charset="utf-8">
		var roles="";
		$(document).ready(function() {
			tabb=7;
			showAndHideTabs();
			<%
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role+= r.getKey().toString()+",";
				}
				if(role.length()>0){
					role = role.substring(0,role.length()-1);
				}
			%>
			roles="<%=role%>";
			 init();
			 showHidePestanas();
		});//Fin del document ready
		function init(){
			queryFormPost("mContratoHeaderRead", {async: false});
			if(parseInt($("#lContratoAbierto").val())==1){
				queryFormPost("mContratoModificadoTotalesMax", {async: false});
			}else{
				queryFormPost("mContratoModificadoTotales", {async: false});
			}
		}
		function openPDF(ext){
			var reporte=$('[name="REPORTE"]:checked').val();
			var where='';
			if($("#desUnidadResponsable2").val()!='')
				where=" and cm.cIdContratoDefinitivo ='"+$("#cContratoDefinitivo").val()+"'";
			if($("#cConsecutivoMod").val()!='')
				where+=" and cm.nConsecutivoModificacion ="+$("#cConsecutivoMod").val();
			
			var url="../../servlet/CatalogosCSV?";	
			if(ext!='csv'){
				url="../../servlet/SeguridadCatalogosMateriales?";
			}
			var myWindow=window.open(url
				+"catalogo=REPORTE"
				+"&accion=run"
				+"&rn="+$('[name="REPORTE"]:checked').val()
				+"&formato="+ext
				+"&where=" + where, 'Procesando', 'status=1, width=400px, height=200px, left=150px');
		}
	</script>
  </head>
  
  <body>
  	<form id="formPrint">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Datos del Contrato Modificatorio</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceBackToTop ui-button ui-corner-all float-right" 	id="imgSalir" 	name="imgSalir" value="Salir" onclick="window.location = 'ContratoModificatorio.jsp?tab=1';" />
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control  transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblProcedimiento" id="lblProcedimiento" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblDefinitivo" id="lblDefinitivo" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-12">
						<input type="text" class="form-control transpInput" name="lblContrato" id="lblContrato" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblProveedor" id="lblProveedor" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput font-weight-bold" name="lblEstadoMod" id="lblEstadoMod" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblTipoMod" id="lblTipoMod" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblTotalContratoOriginal" id="lblTotalContratoOriginal" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblTotalAnterior" id="lblTotalAnterior" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblTotalModificado" id="lblTotalModificado" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblTotal" id="lblTotal" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblPorcentajeMod" id="lblPorcentajeMod" readonly />
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Imprimir</legend>
			<div class="form-group">
				<div class="form-check">
					<input class="form-check-input" type="radio" name="REPORTE" value="reporteConvenioMod.jasper" checked id="convenio" >
					<label class="form-check-label" > Convenio</label>
				</div>
			</div>
			<div class="form-group" >
				<div class="form-check form-check-inline" id="tdPDF">
				  <input type="button" class="btnInterfacePDF ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdPdfConvenio" 		name="cmdPdfConvenio" 	value="PDF"		onclick="openPDF('pdf');" />
				</div>
				<div class="form-check form-check-inline" id="tdXlsx">
				  <input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdXLSConvenio" 		name="cmdXLSConvenio" 	value="Excel"	onclick="openPDF('xls');" />
				</div>
				<div class="form-check form-check-inline" id="tdcsv">
				  <input type="button" class="btnInterfaceCSV ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdCSVConvenio" 		name="cmdCSVConvenio" 	value="CSV"		onclick="openPDF('csv');" />
				</div>
				<div class="form-check form-check-inline" id="tdword">
				  <input type="button" class="btnInterfaceWord ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdDOCConvenio" 		name="cmdDOCConvenio" 	value="Word"		onclick="openPDF('doc');" />
				</div>
			</div>
		</fieldset>
    	<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
	    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
	    <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
	    <input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoContrato%>" />
	    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab.getLogin() %>"/>
	    <input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
	    <input type="hidden" name="contratoDefinitivo" id="contratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
		<input type="hidden" name="cConsecutivoMod" id="cConsecutivoMod" value="<%=cIdConsecutivoMod%>" />
	    <input type="hidden" name="lContratoAbierto" id="lContratoAbierto" value="0" />
	    <input type="hidden" name="tipoMod" id="tipoMod" />	
    </form>
  </body>
</html>
