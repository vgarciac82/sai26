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
	
	<script type="text/javascript" charset="utf-8">
		var roles='';
		$(document).ready(function() {
			$("#tbs").val(5);
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
			initDataTable();
			queryFormPost("mContratoCaratulaRead", {async: false});
			queryFormPost("mContratoHeaderRead", {async: false});
			queryFormPost("fnMontoSubtotalReadContrato", {async: false});
			queryFormPost("fnMontoIVAReadContrato", {async: false});
			queryFormPost("fnMontoTotalContrato", {async: false});
			loadClavesPresupuestalesContrato();
			activacheck();
		});
		
		
		
		function loadClavesPresupuestalesContrato(){
			if(roles.toString().indexOf("ADMIN_RECMAT") < 0 && roles.toString().indexOf("ANALISTA") < 0 && roles.toString().indexOf("JEFE") < 0){
				var qw = " cEjercicio = '" + $("#cEjercicio").val() +
				 "' AND cIdTipoContrato = '" + $("#cIdTipoContrato").val() +
				 "' AND cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutoraEP").val() +
				 "' AND cIdContrato = '" + $("#cIdContratoMat").val()+"'";	
			}else{
				 var qw = " cEjercicio = '" + $("#cEjercicio").val() +
				 "' AND cIdTipoContrato = '" + $("#cIdTipoContrato").val() +
				 "' AND cIdContrato = '" + $("#cIdContratoMat").val()+"'";
			}
			
			$("#cIdContratoTbl").val( $("#cContratoDefinitivo").val());
			oTableClaves=$('#dt_clavepresup').dataTable( {
					bPaginate: false,
        			bLengthChange: false,
        			bFilter: false,
        			bInfo: false,
					bAutoWidth: false,
					sScrollY: "100%",
					bScrollCollapse: true,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,   
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tContratoEP_TMP&qw="+qw,
					aoColumns: [
						{ sName: "nIdClaveEgresos"},
						{ sName: "ClaveInterna" }
						]
			}) ;
		}
		function initDataTable(){
			$('#dt_clavepresup').dataTable(
				{         
	   			    "bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false, 
					"sScrollY": 100,         
			        "sScrollX": "100%",
			        "sScrollXInner": "100%",
			        "bScrollCollapse": true,
					"bJQueryUI": true,    
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"    
				} );
		}
		function buscaClavePresupuestal()
		{
			var idContrato = $('#cIdTipoContrato').val() + "-" + $('#cIdUnidadEjecutora').val() + "-" + $('#nIdConsecutivo').val();
			$('#cIdContratoMat').val(idContrato);
			queryFormPost("getcIdUsuarioCreacionContrato", {async: false});
			pp = window.open('../MultiReporteGridSacel.jsp?cIdUsuarioCreacion=' + $('#cIdUsuarioCreacion').val() + '&cIdDocumento=' + $('#cIdContratoMat').val() 
					+ '&cIdRFC=' + $('#cIdRFC').val() + '&cIdProcedimiento=' + $('#cIdProcedimiento').val()+ '&cuentaDisponible=' + $('#cuentaDisponible').val()
					, 'MultiReporteGridSacel', 'status=1, width=900px, height=680px, left=100px');	
		}
		function fnClickAddRowC() {
			
			rowCount = $('#dt_clavepresup tr').length;
			var vep = $('#ep').val();
			if (vep == '') {
				return ;
			}
			var tmp = vep.lastIndexOf( "\." );
			var uEje= vep.substring( tmp - 3, tmp);
			var cint = vep.substring( tmp -3 );
			$("#cIdUnidadEjecutoraEP").val(uEje);
			$("#ClaveInterna").val(cint);
			$("#nIdClaveEgresos").val(vep);
			swal({
				title: "Seguro que desea agregar la siguiente EP: "+vep+"\nNo se podr\u00e1 revertir el cambio.\n¿Desea continuar?",
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
					queryFormPost("agregaEPContratoCreate,ContratoEPCreate", {async: false,
						callback: function(){
							loadClavesPresupuestalesContrato();
							$('#ep').val("");
						}
					});
				}
			});
			
		}
		function muestraDispRadicado(){
			$("#cuentaDisponible").val('82106');	
			if($('#checkDispRadicado').is(':checked')){
				$("#cuentaDisponible").val('82109');
			}
		}
		function activacheck(){
			$("#checkDispRadicado").attr("checked",false);
			if($("#cuentaDisponible").val()=='82109'){
				$("#checkDispRadicado").attr("checked",true);
			}
		}
	</script>
  </head>
  
  <body>
    <form id="nuevasEps">
    	<div id="container" class="container" style="width: 90%;">
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
    			<legend>Claves</legend>
    			<table style="width: 100%">
    				<tr style="display: none;">
   						<td align="left" >
   						<label>Disponible Radicado:</label> <input type="checkbox" name="checkDispRadicado" id="checkDispRadicado" onclick="muestraDispRadicado()"  />
   						</td>
   					</tr>
    				<tr>
    					<td ><span style="color: red;" >Agregar nuevas EP´S sin recurso al contrato.</span> </td>
    				</tr>
    				<tr>
    					<td>
    						EP:<input type="text" id="ep" name="ep" readonly size="65" value="" />
    						<input type="button" name="nIdClaveEgresosXContrato" id="nIdClaveEgresosXContrato" size="5" value="..." onclick="buscaClavePresupuestal()" class="btnInterfaceBG ui-button ui-corner-all"/>
    						<input type="button" value="Agregar" name="Add2" id="AgregarEPContrato"	onclick="fnClickAddRowC();" class="btnInterfaceBG ui-button ui-corner-all" /> 
    						
    					</td>
    				</tr>
    			</table>
    			<table id="dt_clavepresup" class="display">
    				<thead>
    					<tr>
    					<th>C&oacute;digo SAI</th> 
						<th>Clave SHCP</th>
						</tr> 
    				</thead>
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
    </form>
  </body>
</html>
