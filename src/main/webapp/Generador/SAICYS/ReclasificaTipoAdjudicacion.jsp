<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	Map rol =usuarioTab.getRoles();
	String name_user=usuarioTab.getLogin();
	String cEjercicio = "";
	String cIdTipoPedContrato= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	if (session.getAttribute(GestionInterface.ATT_ContratoEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoEjercicio);
		cIdTipoPedContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoTipoContrato);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ContratoUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ContratoConsecutivo);		
	}
	else if (session.getAttribute(GestionInterface.ATT_PedidoEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_PedidoEjercicio);
		cIdTipoPedContrato = (String)session.getAttribute(GestionInterface.ATT_PedidoTipoPedido);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_PedidoUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_PedidoConsecutivo);		
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>Re-clasifica Tipo de Adjudicaci&oacute;n</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
			$("#tbs").val(15);
			showAndHideTabs();
			<%
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"PedidosContratos","reclasificaTipoAdj");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();
					//System.out.println(b.getValue());
					%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
		
			initQuery();
	
	
		});//fin document ready
		function initQuery(){
			queryFormPost("datosReclasificaTipoAdj", { async : false});
			
			querySelectPost("CategoriaRead", "cboCategoriaCaratula", {async : false,
			callback : function() 
				{
					$("#cboCategoriaCaratula").val($("#nIdCategoriaOrig").val());
					$("#tablaFechasProcedimiento").css("display", "none"); 
					querySelectPost("FundamentoLegReadFiltrado", "cboFundamentoLeg", {async : false,
						callback : function() 
						{
							$("#cboFundamentoLeg").val($("#nIdFundamentoLegOrig").val());
						}
					});
					queryInnerDivPost("llenaFechasProcedimientoCont",{async:false});
				}
			});
			queryFormPost("fnMontoTotalContrato", {async: false});	
			queryInnerDivPost("minimoMaximoTipoProcedimiento", {async : false});
		}
		function cambiaFundamentoLeg(){
			querySelectPost("FundamentoLegReadFiltrado", "cboFundamentoLeg", {async : false});
			muestraCapturaDeFechas();
		}
		function muestraCapturaDeFechas(){
			if(parseInt($("#nIdCategoriaOrig").val(),10)!=parseInt($("#cboCategoriaCaratula").val(),10)){
				$("#cboCategoria").val($("#cboCategoriaCaratula").val());
				$("#tablaFechasProcedimiento").css("display", "block");
				queryInnerDivPost("fn_mFechasProcedimientoRead", {async : false}); //Trae el html con los inputs
			}else{
				$("#tablaFechasProcedimiento").css("display", "none");
			}
		}
		function actualizarDatos(){
			if(validaTipoCategoria()){
				$.ajax({
					url: '../../servlet/ProcedimientoServlet',
					dataType: 'json', 					
					async: false, 
	          		type:'post',
					data:{"cIdProcedimiento":$("#cIdProcedimiento").val(),
							"cIdRFC":$("#cIdRFC").val(),
							"cIdContratoDef":$("#cIdContratoDefinitivo").val(),
							"cTipoProcedimiento":$("#cIdTipoProcedimiento").val(),
							"cUnidadEjecutora":$("#cIdUnidadEjecutora").val(),
							"nCategoriaProcedimiento":$("#nIdCategoriaOrig").val(),
							"nCategoriaProcedimientoNuevo":$("#cboCategoriaCaratula").val(),
							"nConsecutivoProcedimiento":$("#nIdConsecutivoProced").val(),
							"nFundamentoLegal":$("#nIdFundamentoLegOrig").val(),
							"nFundamentoLegalNuevo":$("#cboFundamentoLeg").val(),
							"mMontoNetoContrato":$("#mMontoNetoContrato").val(),
							"operacion":10,
							"arrayFechas":$("#arrayFechas").val()},
					success: function(json){
						swal({
							title: "",
							text: json[0].mensaje,
							icon: "info",
							buttons: {
								confirm : "Cerrar"
								},
							}).then((continuar) => {
								initQuery();
						});
						
					}
				});
			}
		}
		function validaTipoCategoria(){
			var resp=true;
			if(parseInt($("#cboCategoriaCaratula").val(),10)!=parseInt($("#nIdCategoriaOrig").val(),10)){
				resp=validaFechas();
			}else{
				if(parseInt($("#cboFundamentoLeg").val(),10)==parseInt($("#nIdFundamentoLegOrig").val(),10)){
					swal("No es necesario actualizar los datos por que son los mismos que ya est\u00e1n guardados.",{icon:"info",button: "Cerrar"});
					resp=false;
				}
			}
			return resp;
		}
		function validaFechas(){
			var inputTablaFechasProcedimiento = $('input','#tablaFechasProcedimiento');
			var banFechasProcedimiento = false;
			var fechaTem="";
			var numFecha="";
			var numFechaAnt="0";
			var arregloTmp=new Array();
			var arrayFila=new Object();
			for(var i=0;i<inputTablaFechasProcedimiento.length;i++){
				if(inputTablaFechasProcedimiento[i].type == "text" || inputTablaFechasProcedimiento[i].type == "TEXT"){
					if(inputTablaFechasProcedimiento[i].id.indexOf("fechaProcedimiento") >= 0){
						banFechasProcedimiento=true;
						numFecha=parseInt(inputTablaFechasProcedimiento[i].id.replace("fechaProcedimiento",""),10);
						if(i>0){
							
							if(inputTablaFechasProcedimiento[i-1].value != "" && inputTablaFechasProcedimiento[i].value != ""){
								if(numFechaAnt!=12){
									fechaTem=inputTablaFechasProcedimiento[i-1].value;
								}else{
									fechaTem=inputTablaFechasProcedimiento[i-2].value;
								}
								if(!compare_dates(inputTablaFechasProcedimiento[i].value, fechaTem)){
									swal("Las fechas se deben introducir en orden cronologico",{icon:"info",button: "Cerrar"});
									banFechasProcedimiento = false;
									return;
								}
							}else{
								swal("Falta insertar algunas fechas",{icon:"info",button: "Cerrar"});
								banFechasProcedimiento = false;
								return;
							}
						}
						numFechaAnt=numFecha;
						arrayFila=[numFecha,inputTablaFechasProcedimiento[i].value,"|"];
						arregloTmp.push(arrayFila);
					}
				}
			}
			$("#arrayFechas").val(arregloTmp);
			return banFechasProcedimiento;
		}
		function guardaBitacora(accion){
			$("#cAccion").val(accion);
			$("#cIdDocumento").val($("#cIdTipoPedContrato").val()+"-"+$("#cIdUnidadEjecutora").val()+"-"+$("#nIdConsecutivo").val()+"/"+$("#cEjercicio").val());
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
		}
		///Desabilita sabados y domingos del datepicker
		function nonWorkingDates(date){
	        var day = date.getDay(), Sunday = 0, Monday = 1, Tuesday = 2, Wednesday = 3, Thursday = 4, Friday = 5, Saturday = 6;
	        //var closedDates = [[7, 29, 2009], [8, 25, 2010]];
	        var closedDays = [[Sunday], [Saturday]];
	        /*for (var i = 0; i < closedDays.length; i++) {
	            if (day == closedDays[i][0]) {
	                return [false];
	            }
	
	        }*/
	
	        return [true];
	    }
	</script>
  </head>
  
  <body>
    <form action="">
    	<div id="container" class="container" style="width: 90%;">
    		<fieldset>
    			<legend>Datos Generales</legend>
    			<table align="left">
    				<tr>
    					<td><input type="text" id="lblUnidadEjecutora" name="lblUnidadEjecutora" value=""  style="width: 600px;border-width:0; background-color:transparent"  readonly /></td>
    				</tr>
    				<tr>
    					<td><input type="text" id="lblProcedimiento" name="lblProcedimiento" value=""  style="width: 600px;border-width:0; background-color:transparent"  readonly /></td>
    				</tr>
    				<tr>
    					<td><input type="text" id="lblPedidoCont" name="lblPedidoCont" value=""  style="width: 600px;border-width:0; background-color:transparent"  readonly /></td>
    				</tr>
    				<tr>
    					<td><input type="text" id="lblConcepto" name="lblConcepto" value=""  style="width: 600px;border-width:0; background-color:transparent"  readonly /></td>
    				</tr>
    				<tr>
    					<td><input type="text" id="lblProveedor" name="lblProveedor" value=""  style="width: 600px;border-width:0; background-color:transparent"  readonly /></td>
    				</tr>
    				<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblTotal" id="lblTotal" readonly /></td>
			    	</tr>
    				<tr>
    					<td><input type="text" id="lblcCategoria" name="lblcCategoria" value=""  style="width: 600px;color: blue; border-width:0; background-color:transparent;"  readonly /></td>
    				</tr>
    				<tr>
    					<td><input type="text" id="lblcFundamentoLegal" name="lblcFundamentoLegal" value=""  style="width: 600px;color: blue; border-width:0; background-color:transparent"  readonly /></td>
    				</tr>
    				<tr align="left">
						<td>
					 		<div id="tablaFechasProcedimientoCont" style="width:100%;">
								<!-- Carga las fechas -->
							</div>
						</td>
					</tr>
					<tr align="left">
						<td>
							<div id="tblMinimoMaximo" style="width:100%;"></div>
						</td>
					</tr>
    			</table>
    		</fieldset>
    		<fieldset>
    			<legend>Actualizar</legend>
    			<table align="left">
    				<tr>
    					<td>
    						<input name="Categoria" id="Categoria"  value="Tipo de Procedimiento:"size="20" style="border-width:0; background-color:transparent ">
    					</td>
    					<td>
    						<select id="cboCategoriaCaratula" name="cboCategoriaCaratula" 
								style="width: 30em;" onchange="cambiaFundamentoLeg()">
								<option value="" selected="selected">
								</option>
							</select>
    					</td>
    				</tr>
    				<tr>
    					<td><input name="FundamentoLeg" id="FundamentoLeg"  value="Fundamento Legal:"size="20" style="border-width:0; background-color:transparent "> </td>
    					<td>
    						<select id="cboFundamentoLeg" name="cboFundamentoLeg" style="width: 30em;">
								<option value="" selected="selected"></option>
							</select>
    					</td>
    				</tr>
    				<tr>
						<td colspan="2">
							<input type="hidden" name="nIdProcedimientoNuevo" id="nIdProcedimientoNuevo" />
							<input type="hidden" name="nIdFechaProcedimiento" id="nIdFechaProcedimiento" />
							<input type="hidden" name="nFechaProcedimiento" id="nFechaProcedimiento" />
							<div id="tablaFechasProcedimiento" style="width:100%;"></div>
						</td>
					</tr>
    				<tr >
    					<td colspan="2"> <input type="button" id="actulizaTipAdj" name="actulizaTipAdj" value="Actualiza" onclick="actualizarDatos()" class="btnInterfaceBG ui-button ui-corner-all"/> </td>
    					
    				</tr>
    			</table>
    		</fieldset>
    	</div>
    	<input type="hidden" id="nIdCategoriaOrig"  name="nIdCategoriaOrig" value=""/>
    	<input type="hidden" id="nIdFundamentoLegOrig"  name="nIdFundamentoLegOrig" value=""/>
    	<input type="hidden" id="cEjercicio"  name="cEjercicio" value="<%= cEjercicio%>"/>
    	<input type="hidden" id="cIdTipoPedContrato"  name="cIdTipoPedContrato" value="<%= cIdTipoPedContrato%>"/>
    	<input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoPedContrato%>" />
    	<input type="hidden" id="cIdUnidadEjecutora"  name="cIdUnidadEjecutora" value="<%= cIdUnidadEjecutora%>"/>
    	<input type="hidden" id="nIdConsecutivo"  name="nIdConsecutivo" value="<%= nIdConsecutivo%>"/>
    	<input type="hidden" id="cIdProcedimiento"  name="cIdProcedimiento" value=""/>
    	<input type="hidden" id="cIdDocumento" name="cIdDocumento" value="" />
   		<input type="hidden" id="cAccion" name="cAccion" value="" />
   		<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuarioTab.getLogin() %>" />
   		<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuarioTab.getLogin() %>"/>
   		<input type="hidden" name="cIdRFC" id="cIdRFC" value=""/>
   		<input type="hidden" name="cIdTipoProcedimiento" id="cIdTipoProcedimiento" value=""/>
   		<input type="hidden" id="nIdConsecutivoProced"  name="nIdConsecutivoProced" value=""/>
   		<input type="hidden" id="cboCategoria"  name="cboCategoria" value=""/>
   		<input type="hidden" name="esServicio" id="esServicio" />
   		<input type="hidden" name="arrayFechas" id="arrayFechas" value="" />
   		<input type="hidden" name="cIdContratoDefinitivo" id="cIdContratoDefinitivo" value="" />
   		<input type="hidden" name="mMontoNetoContrato" id="mMontoNetoContrato" value="" />
    </form>
  </body>
</html>
