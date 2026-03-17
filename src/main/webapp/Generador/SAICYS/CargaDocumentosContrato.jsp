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
	Map <String, Role> rol =usuarioTab.getRoles();
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


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    
    <title>My JSP 'CargaDocumentosContrato.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" charset="utf-8">
		var roles='';
		
		$(document).ready(function() {
			$("#tbs").val(19);
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
			cargaTabla();
			/*Dialogo*/
			$("input.AyudaSyC").subIniciaDlg();
			$("#dialog-documentos").dialog({
				autoOpen : false,
				height : 750,
				width : 550,
				modal : true,
				buttons : {
					"Enviar Archivo" : function() {
						submit();
					},
					Cancel : function() {
						$(this).dialog("close");
					}
				},
				close : function() {
				}
			});	
		});
		function cargaTabla(){
			var funcion="fn_mDocumentosContrato";
			var func="'" +$("#cContratoDefinitivo").val()+"'" 
			oTableDocumentos = $("#tblDocContrato").dataTable({
				bScrollCollapse: true,
	        		bInfo: false,
	        		//sScrollY : "100%",
					sScrollX: "100%",
					bAutoWith: true,
					bJQueryUI: true,
					bRetrive : true,
					bDestroy : true,
					sPaginationType: "full_numbers",
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay datos en la tabla",
						sLoadingRecords: "Cargando...",
						sInfo: "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty: "Registro 0 al 0 de 0",
						sInfoFiltered: "(filtado de _MAX_ registros)",
						sInfoPostFix: "",
						sInfoThousands: ",",
						sSearch: "Buscar:",
						oPaginate: {
							sFirst:    "Primero",
							sPrevious: "Ant.",
							sNext:     "Sigte.",
							sLast:     "&Uacute;ltimo"
						}
					},
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+funcion+"("+func+")",
					bJQueryUI: true,
					aaSorting: [[ 0, "asc" ]] ,
					aoColumns: [
						{sName: "cDocumento"},
						{sName: "estatus"},
						{sName: "cUsuarioCargaDocumento"},
						{sName: "fFechaCarga"},
						{sName: "cargarArchivo"}
					]
			});
		}
		function cargaDocumentos(nTipoDoc) {
			if(roles.toString().indexOf("ANALISTA") >= 0 || roles.toString().indexOf("ADMIN_RECMAT") >= 0 || roles.indexOf("JEFE")>=0){
				if(nTipoDoc==1){
					window.open("SubmitFile.jsp?operacion=2&contratoDef="+$("#cContratoDefinitivo").val(), 'Submit', 'status=1, width=500px, height=350px, left=150px');
				}
			}else{
				swal("No tienes permiso para ejecutar está acción",{icon:"info",button: "Cerrar"});
			}
		}
	</script>
  </head>
  
  <body>
    <form>
    	<div id="container" class="container" style="width: 95%;">
    		<fieldset>
    			<legend>Datos del Contrato</legend>
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
    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent;" name="lblProveedor" id="lblProveedor" readonly /></td>
    				</tr>
    				<tr>
    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent;" name="lblEstado" id="lblEstado" readonly /></td>
    				</tr>
    				<tr>
    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent;" name="lblSubtotal" id="lblSubtotal" readonly /></td>
    				</tr>
    				<tr>
    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent;" name="lblImporteIVA" id="lblImporteIVA" readonly /></td>
    				</tr>
    				<tr>
    					<td><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblTotal" id="lblTotal" readonly /></td>
    				</tr>
    				
    			</table>
    		</fieldset>
    		<fieldset>
    			<legend>Documentos de Contrato</legend>
    			<table id="tblDocContrato" class="display" >
					<thead >
						<tr>
							<th align="center">Documento</th>
							<th align="center">Estatus</th>
							<th align="center">Usuario Carga Documento</th>
							<th align="center">Fecha de Carga</th>
							<th align="center">Cargar Archivo .zip</th>
						</tr>										
					</thead>
				</table>
    		</fieldset>
    		
    	</div>
    	<div style="display: none;" id="msgDialog" title="Resultado de Carga">
			<fieldset>
				<legend>Resultado de carga.</legend>
					<table align="center">
						<tr>
							<td align="center">
								<textarea rows="10" cols="40" id="msgTxt"></textarea>
							</td>
						</tr>
						<tr>
							<td align="right">
								<input type="button" id="btnAceptar" value="Aceptar" class="btnInterfaceBG ui-button ui-corner-all" />
							</td>
						</tr>
					</table>
			</fieldset>
		</div>
		<div id="dialog-documentos">
			<div id="facturasDiv">
				<fieldset>
					<legend id="lgndDiv">Enviar Documento de Contrato.</legend>
					<table align="center">
						<tr>
							<td align="right">Archivo *.zip:</td>
							<td align="left"><input type="file" value="" id="archivoZip" name="archivoZip"> 
						</tr>
					</table>
				</fieldset>
			</div>
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
		<input type="hidden" id="operacion" name="operacion" value="2"/>
		<input type="hidden" id="cIdEntidadContableTbl" name="cIdEntidadContableTbl" value="<%= usuarioTab.getPropiedad("CCENTROCONTABLE").getValor()%>"/>
		
    </form>
  </body>
</html>
