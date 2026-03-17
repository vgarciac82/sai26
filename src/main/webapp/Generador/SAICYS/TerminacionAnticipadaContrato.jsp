<%@ page language="java" pageEncoding="UTF-8"%>
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
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today= sdf.format(c1.getTime());
	c1.add(Calendar.DAY_OF_MONTH, +20);
	String fechaLimite= sdf.format(c1.getTime());
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    
    <title>My JSP 'TerminacionAnticipadaContrato.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" charset="utf-8">
		var roles='';
		var fechaLimiteDefault = "<%=fechaLimite%>";
		
		$(document).ready(function() {
			$("#tbs").val(20);
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
			$("input.AyudaSyC").subIniciaDlg();
			$("#esperar").dialog({
				autoOpen : false,
				height : 110,
				width : 200,
				modal : true,
				open: function(event, ui){
					$(".ui-dialog-titlebar").hide();
				},
				close : function() {
				}
			});
			queryFormPost("mContratoCaratulaRead", {async: false,
				callback : function() {
					muestra();
				}
			});
			queryFormPost("mContratoHeaderRead", {async: false});
			queryFormPost("fnMontoSubtotalReadContrato", {async: false});
			queryFormPost("fnMontoIVAReadContrato", {async: false});
			queryFormPost("fnMontoTotalContrato", {async: false});
			
			var options = { 
				dataType: 'json', 
			    success:function(j) { 
			       swal(j[0].MSG,{icon:"info",button: "Cerrar"});
			       $("#esperar").dialog("close");
			    },
			     error: function(){
			    	swal("Error",{icon:"error",button: "Cerrar"});
			    	$("#esperar").dialog("close");
			    }
			}; 
		  	$('#formArchivoEFO').ajaxForm(options);
		});
		
		function muestra(){
			if($("#terminacionAnti").val()==1){
				$("#divCapturaDatos").hide();
				$("#divConsultaDatos").show();
				cargaTabla();
			}else{
				agregaDatePickerFechas();
				functionIframe();
				$("#divConsultaDatos").hide();
				$("#divCapturaDatos").show();
			}
		}
		function agregaDatePickerFechas(){
			$("#fTermino").datepicker({
				dateFormat: "dd/mm/yy",
				altField: "#actualDate",
			 	currentText: "Now",
				showOn: 'button',
				buttonImageOnly: true,	
			   	buttonImage: '../images/calendar.gif',			    					 
				changeYear: true
			});
			
			$("#fNotificacionUAF").datepicker({
				dateFormat: "dd/mm/yy",
				altField: "#actualDate",
			 	currentText: "Now",
				showOn: 'button',
				buttonImageOnly: true,	
			   	buttonImage: '../images/calendar.gif',			    					 
				changeYear: true
			});
			
			$("#fLimitePagoPendiente").datepicker({
				dateFormat: "dd/mm/yy",
				altField: "#actualDate",
			 	currentText: "Now",
				showOn: 'button',
				buttonImageOnly: true,	
			   	buttonImage: '../images/calendar.gif',			    					 
				changeYear: true
			});
			
			
		}
		
		function functionIframe(){
			$('#uploadFrm').attr('src', '../SAICYS/UploadDocto.jsp');
		}
		
		function pasaparametros(){
			
			var iframe = document.getElementById('uploadFrm');
            var doc = iframe.contentDocument || iframe.contentWindow.document;
            
            doc.getElementById("descripcionCausa").value=encodeURIComponent($("#causaTerminoContrato").val());
            doc.getElementById("fechaTermino").value=$("#fTermino").val();
            doc.getElementById("fechaLimitePagoPendiente").value=$("#fLimitePagoPendiente").val();
            doc.getElementById("cFolio").value=$("#C_FOLIO").val();
            doc.getElementById("cContratoDefinitivo").value=$("#cContratoDefinitivo").val();
            doc.getElementById("fechaNotificacionUAF").value=$("#fNotificacionUAF").val();
            
            if(document.getElementById("terminacionaAnt").checked) {
            	doc.getElementById("nTipoTerminacionCont").value=$("#terminacionaAnt").val();
            }else if(document.getElementById("minimosAgotados").checked) {
            	doc.getElementById("nTipoTerminacionCont").value=$("#minimosAgotados").val();
            }else{
            	doc.getElementById("nTipoTerminacionCont").value=$("#resicionContrato").val();
            }
            
		}
		
		function validaFechaNotificacion(){
			if( $("#resicionContrato").attr("checked") )
				return  $("#fNotificacionUAF").val() !== "" 
			else 
				return true;
		}
		
		function Guardar(){
			$("#esperar").dialog("open");
			
			if( !validaFechaNotificacion() ){
				swal("Favor de ingresar la fecha de notificacion a la UAF.",{icon:"info",button: "Cerrar"});
				$("#esperar").dialog("close");
				return;
			}
			
			if( !validaFechaUltimoPago() ){
				swal("Favor de ingresar la fecha limite para tramitar el pago.",{icon:"info",button: "Cerrar"});
				$("#esperar").dialog("close");
				return;
			}
			if($("#causaTerminoContrato").val()==""){
				swal("Favor de escribir la causa.",{icon:"info",button: "Cerrar"});
				$("#esperar").dialog("close");
				return;
			}else{
				pasaparametros();
				document.getElementById('uploadFrm').contentWindow.onSubmitAjax();
			}
		}
		function termina(){
			swal($("#observaciones").val(),{icon:"info",button: "Cerrar"});
			queryFormPost("mContratoCaratulaRead", {async: false,
				callback : function() {
					muestra();
				}
			});
			$("#esperar").dialog("close");
		}
		function error(){
			swal("Error",{icon:"error",button: "Cerrar"});
			$("#esperar").dialog("close");
		}
		function faltaArchivo(){
			swal("Favor de seleccionar un archivo .zip",{icon:"info",button: "Cerrar"});
			$("#esperar").dialog("close");
		}
		function cargaTabla(){
			var qw="cIdContratoDefinitivo='"+$("#cContratoDefinitivo").val()+"'";
			oTableDocumentos = $("#tblConsultaDatos").dataTable({
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mContratosTerminacionAnticipada&qw="+qw,
					bJQueryUI: true,
					aaSorting: [[ 0, "asc" ]] ,
					aoColumns: [
						{sName: "tipoTerminacion"},
						{sName: "cCausa"},
						{sName: "fFechaTermino"},
						{sName: "fFechaLimitePagoPendiente"},
						{sName: "fFechaCaptura"},
						{sName: "cLogin"}
					]
			});
		}
		
		function muestraUltimoPago(show){
			if( show ){
				$("#pagoPendienteTr").show();
				$("#limitePagoPendienteTr").show();
				$("#fNotificacionUAF_DIV").show();
				$("#fNotificacionUAF").val("");
			}else{
				$("#pagoPendienteTr").hide();
				$("#fLimitePagoPendiente").val(fechaLimiteDefault);
				$("#llevaPagoPendiente").attr("checked",true);
				$("#limitePagoPendienteTr").show();
				
				$("#fNotificacionUAF_DIV").hide();
				$("#fNotificacionUAF").val("");
			}
		}
		
		function llevaPagoPendienteAction(){
			if( $("#llevaPagoPendiente").attr("checked")){
				$("#fLimitePagoPendiente").val(fechaLimiteDefault);
				$("#limitePagoPendienteTr").show();
			}else{
				$("#fLimitePagoPendiente").val("");
				$("#limitePagoPendienteTr").hide();
			}
		}
		
		function validaFechaUltimoPago(){
			
			if( !$("#llevaPagoPendiente").attr("checked") || ( $("#llevaPagoPendiente").attr("checked") && $("#fLimitePagoPendiente").val() !== "" ) )
				return true;
			else 
				return false;
		}
	</script>

  </head>
  
  <body>
 	<form >
    	<div id="container" class="container" style="width: 95%;">
    		<fieldset>
    			<legend>Datos del Contrato</legend>
    			<div align="left" style="width: 98%">
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
    			</div>
    		</fieldset>
    		<fieldset>
    			<legend>Terminaci&oacute;n Anticipada de contrato</legend>
    			<div align="left" style="width: 98%; " id="divCapturaDatos">
	    			<table align="left" style="width: 92%">
						<tr align="left">
							<td><input type="radio" id="terminacionaAnt" name="radioTerminacionCont" value="1" checked="checked" onclick="muestraUltimoPago(false)"/> Terminaci&oacute;n anticipada.</td>
						</tr>
						<tr align="left">
							<td><input type="radio" id="minimosAgotados" name="radioTerminacionCont" value="2" onclick="muestraUltimoPago(false)"/> Por haber agotado los m&iacute;nimos.</td>
						</tr>
						<tr align="left" style="display: none;">
							<td><input type="radio" id="resicionContrato" name="radioTerminacionCont" value="3"  onclick="muestraUltimoPago(true)"/> Por recisi&oacute;n de Contrato.</td>
						</tr>
						<tr align="left">
							<td>
								Causa:<br/><textarea rows="3" cols="60" id="causaTerminoContrato" name="causaTerminoContrato"></textarea>
							</td>
						</tr>
						
						<tr align="left">
							<td>
								Fecha de terminaci&oacute;n:<input type="text" id="fTermino" name="fTermino" value="<%=today %>" readonly="readonly" class="desahabilitado"/>
							</td>
						</tr>
						
						<tr align="left" id="fNotificacionUAF_DIV" style="display: none">
							<td>
								Fecha de noticiaci&oacute;n a la UAF:<input type="text" id="fNotificacionUAF" name="fNotificacionUAF" value="" readonly="readonly" class="desahabilitado"/>
							</td>
						</tr>
						
						
						<tr align="left" id="pagoPendienteTr" style="display: none">
							<td>
								<input type="checkbox" id="llevaPagoPendiente" name="llevaPagoPendiente" checked="checked" onclick="llevaPagoPendienteAction();"/>
								<label for="llevaPagoPendiente">Existe pago pendiente</label>
							</td>
						</tr>
						<tr align="left" id="limitePagoPendienteTr">
							<td>
								Fecha limite para tramitar pago pendiente:<input type="text" id="fLimitePagoPendiente" name="fLimitePagoPendiente" value="<%=fechaLimite %>" readonly="readonly" class="desahabilitado"/>
							</td>
						</tr>
						<tr align="left">
							<td>
								<iframe id="uploadFrm" align="top" frameborder="0" style="height: 45px;"></iframe>
							</td>
						</tr>
						<tr id="trGuardar">
							<td>
								<input id="btnGuardar" name="btnGuardar" type="button" value="Guardar" onclick="Guardar()" class="btnInterfaceBG ui-button ui-corner-all"/>
							</td>
						</tr>
					</table>
				</div>
				<div align="left" style="width: 98%;" id="divConsultaDatos">
					<table id="tblConsultaDatos" class="display" style="width: 100%" >
						<thead >
							<tr>
								<th align="center">Tipo de Termino</th>
								<th align="center">Causa</th>
								<th align="center">Fecha de Termino</th>
								<th align="center">Fecha limite para<br>tramitar pago pendiente</th>
								<th align="center">Fecha de captura</th>
								<th align="center">Usuario</th>
							</tr>										
						</thead>
					</table>
				</div>
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
		<input type="hidden" id="operacion" name="operacion" value="3"/>
		<input type="hidden" id="C_FOLIO" name="C_FOLIO" value=""/>
		<input type="hidden" id="observaciones" name="observaciones" value=""/>
		<input type="hidden" id="nTipoTerminacionCont" name="nTipoTerminacionCont" value="1"/>
		<input type="hidden" id="terminacionAnti" name="terminacionAnti" value="-1"/>
		<input type="hidden" id="cIdEntidadContableTbl" name="cIdEntidadContableTbl" value="<%= usuarioTab.getPropiedad("CCENTROCONTABLE").getValor()%>"/>
    </form>
  </body>
</html>
