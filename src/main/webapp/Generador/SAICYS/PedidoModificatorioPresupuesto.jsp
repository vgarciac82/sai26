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
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuarioTab.getLogin();
	String role="";
	Map rol =usuarioTab.getRoles();
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String cIdPedido = "";
	String cIdPedidoDefinitivo = "";
	String cIdConsecutivoMod = "";
	String cEjercicio = "";
	String cIdTipoPedido= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	if (session.getAttribute(GestionInterface.ATT_PedidoModificatorioDefinitivo) != null) {
		cIdPedidoDefinitivo = (String)session.getAttribute(GestionInterface.ATT_PedidoModificatorioDefinitivo);
		cIdConsecutivoMod = (String)session.getAttribute(GestionInterface.ATT_PedidoModificatorioConsecutivo);
		cIdPedido = (String)session.getAttribute(GestionInterface.ATT_PedidoModificatorioId);
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_PedidoModificatorioEjercicio);
		
		cIdTipoPedido = cIdPedido.split("-")[0];
		cIdUnidadEjecutora = cIdPedido.split("-")[1];
		nIdConsecutivo = cIdPedido.split("-")[2];		
	}else 
		response.sendRedirect("PedidoModificatorio.jsp?tab=1");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>    
    <title>Presupuesto</title>    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">	
	<!-- <meta http-equiv="refresh" content="5;url=../Generador/SAICYS/Pedidos.jsp">-->
	
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
	</style>
	<style>			// estilos del dialogo
			div#dialog-form fieldset { padding:0; border:0; margin-top:25px; }
			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
	</style>
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.core"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
 	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>	
	<script type="text/javascript" src="../js/catalogo/general.js"></script>	
	<script type="text/javascript" charset="utf-8">
		var oTableClaves;
		$(document).ready(function() {	
				<%
			    String roles="";
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				int imgAnular=0;
				int imgAprobar=0;
				int imgDevolver=0;
				int imgPdf=0;
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"PedidoModificatorio","presupuestoPedidoMod");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					if ("imgAprobarPresupuestoPedMod".equals(img)){  
						%>$("#<%=img%>").attr("disabled", false);<%
						imgAprobar=1;
					}
					if ("imgDevolverPresupuestoPedMod".equals(img)){
						%>$("#<%=img%>").attr("disabled", false);<%
						imgDevolver=1; 
					}	
				}

				%> 
			setReadOnly();
			headerQuery();
						
			//Para ver si tiene apartados en nApartadosUsados
			queryFormPost("fn_mApartadoPedidoModificadoCuenta", {async: false} );
			
			//Esconde el letrero de apartado encontrado
			$("#apartadoNotice").hide();
			//Si existe al menos un apartado, consulta el monto total - apartado disponible y lo pone en monto real
			if ($("#nApartadosUsados").val() > 0){
				queryFormPost("fn_mApartadoPedidoModificadoTotal", {async: false} );
				
				//Si el valor del apartado disponible es > 0
				if ($("#mApartadoReal").val() > 0){
					$("#mApartadoReal").formatCurrency();
					$("#aptdReal").html($("#mApartadoReal").val());
					$("#apartadoNotice").show();
				}
			}
			
			//Muestra las claves presupuestales
			loadClavesPresupuestalesPedidoMod();	
		});
		
		function quitaFmt( val ) {
		   	val = val.replace("$", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
		   	}
		   	return val;
		}
		
		function loadClavesPresupuestalesPedidoMod(){
			var campos = "'" + $("#cPedidoDefinitivo").val() + "'," + $("#cConsecutivoMod").val();
			oTableClaves=$('#dt_clavepresup').dataTable( {
					bPaginate: false,
        			bLengthChange: false,
        			bFilter: false,
        			bInfo: false,
					bAutoWidth: false,
					sScrollY: 100,
					bScrollCollapse: true,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,   
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mApartadoPedidoModificado(" + campos + ")",
					aoColumns: [
						{ sName: "ep"},
						{ sName: "claveInterna" }
						]
			}) ;
		}
			
		function setReadOnly(){
			document.getElementById("lblUnidadEjecutora").style.readonly = true;
			document.getElementById("lblProcedimiento").style.readonly = true;
			document.getElementById("lblDefinitivo").style.readonly = true;
			document.getElementById("lblPedido").style.readonly = true;
			document.getElementById("lblProveedor").style.readonly = true;
			document.getElementById("lblEstadoMod").style.readonly = true;
			document.getElementById("lblTotalAnterior").style.readonly = true;
			document.getElementById("lblTotalModificado").style.readonly = true;
			document.getElementById("lblTotal").style.readonly = true;
		}
		
		function headerQuery(){
			queryFormPost("mPedidoHeaderRead", {async: false});
			queryFormPost("mPedidoModificadoTotales", {async: false});
			queryFormPost("cg_roleRead", {async: false});
			//alert($("#tipoMod").val());
			if ($("#tipoMod").val() != 1){
				document.getElementById("reduccionPedidoMod").disabled = true;
			}
			
			if(parseInt($("#nIdEstado").val(),10) == 1){
				document.getElementById("precompromisoPedidoMod").disabled = true;
			}

			queryFormPost("mPedidoModicadoChecaRolUsuario", {async: false});
			queryFormPost("mPedidoModicadoUsuarioCreacionOriginalRead",{async: false });
		}
		
		function fnGetSelected( oTableLocal ) {
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
		
		function guardaPedidoMod(){
			var imgAprobar='<%=imgAprobar%>';
			if(imgAprobar==0){
				var proc=""+$("#cEjercicio").val()
							+","+$("#cPedidoDefinitivo").val()
							+","+$("#cConsecutivoMod").val()
							+","+$("#U_LOGIN").val()
							+","+$("#nTipoPago").val();
				
				if ($("#usuarioLoginRole").val() != 'ADMIN_RECMAT') { 
					if ($("#usuarioCreacionOriginal").val() != $("#usuarioLogin").val()){
						alert("No tiene permisos para realizar esta accion");
						return;
					}
				}
				
				if ($("#nIdEstado").val() > 1) {
					alert("No es posible aprobar el presupuesto de este modificatorio");
					return;
				}
				
				//Si existe un apartado, recarga las eps de apartado
				if ($("#nApartadosUsados").val() > 0 && quitaFmt($("#mApartadoReal").val()) > 0){
					queryFormPost("pa_mAptdPrcpEPMod", {async: false});
				}
				else {
					alert("No es necesario aprobar este modificatorio porque no tiene EPs asociadas o estas no cuentan con presupuesto");
					return;
				}
				
				//Llama al PedidoServlet para llamar el stored procedure que aprueba el Pedido
				$.getJSON("../../servlet/PedidoModificadoServlet?operacion=0",{Tabla: "", Param: proc, MaxReg: "", ajax: 'false'}, function(j){
							for(var i = 0; i < j.length; i++)
								 var col=j[i].Col1
							switch(col){
							case "0": 
								alert("El Modificatorio se ha aprobado y está en el SAI");
								queryFormPost("mPedidoHeaderRead", {async: false});
								queryFormPost("mPedidoModificadoTotales", {async: false});
								//deshabilita la imagen de aprobar una vez que se ha aprobado el pedido
								document.getElementById("imgAprobarPresupuestoPedMod").disabled = true;
								document.getElementById("imgDevolverPresupuestoPedMod").disabled = false;							
								$("#nIdEstado").val("2"); 
								document.getElementById("precompromisoPedidoMod").disabled = false; 
							break;
							case "1":  
								alert("Este Modificatorio ya se encuentra aprobado");
							break;
							case "2":  
								alert("Este proveedor no se encuentra registrado en el sistema Financiero");
							break;
							case "3":  
								alert("No se han registrado EPs para este pedido");
							break;
							case "4":  
								alert("Ha ocurrido un error al registrar el pedido, por favor contacte a su administrador");
							break;
							case "5":  
								alert("Ha ocurrido un error al registrar las EPs, por favor contacte a su administrador");
							break;
							case "6","7":  
								alert("Ha ocurrido un error al actualizar el pedido, por favor contacte a su administrador");
							break;
							}
					});
			}else{
				alert("No tiene permiso para realizar esta accion, contacte a su administrador");
			}
		}
		
		//devuelve el pedido
		function devuelvePedidoMod(){
			var imgDevolver='<%=imgDevolver%>';
			if (imgDevolver == 0){

			if ($("#usuarioLoginRole").val() != 'ADMIN_RECMAT') { 
				if ($("#usuarioCreacionOriginal").val() != $("#usuarioLogin").val()){
					alert("No tiene permisos para realizar esta accion");
					return;
				}
			}
			
			if(parseInt($("#nIdEstado").val(),10) == 2){
						var proc=""+$("#cEjercicio").val()
						+","+$("#cPedidoDefinitivo").val()
						+","+$("#cConsecutivoMod").val()
						+","+$("#nTipoPago").val();
					$.getJSON("../../servlet/PedidoModificadoServlet?operacion=1",{Tabla: "", Param: proc, MaxReg: "", ajax: 'true'}, function(j){
				 		for(var i = 0; i < j.length; i++)
		                	 var col=j[i].Col1
		                switch(col){
						case "0":  
							alert("Se ha devuelto el Modificatorio");
							queryFormPost("mPedidoHeaderRead", {async: false});
							queryFormPost("mPedidoModificadoTotales", {async: false});
							document.getElementById("imgAprobarPresupuestoPedMod").disabled = false;
							document.getElementById("imgDevolverPresupuestoPedMod").disabled = true;
							$("#nIdEstado").val("1");
							document.getElementById("precompromisoPedidoMod").disabled = true;
						break;
						case "1":  
							alert("No se puede regresar el modificatorio porque su estado no lo permite");
						break;
						case "2":  
							alert("Ha ocurrido un error al eliminar las EPs del modificatorio, por favor contacte a su administrador");
						break;
						case "3":  
							alert("Ha ocurrido un error al devolver el modificatorio, por favor contacte a su administrador");
						break;
						case "4":  
							alert("Ha ocurrido un error al cambiar el estado del modificatorio, por favor contacte a su administrador");
						break;
	                 	}
				});
			}
		}else{
			alert("No tiene permisos para realizar esta acción, contacte a su administrador");
		}
	}
	</script>
</head>  
<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form action="#" name="formPedido"> 
		<div id="container" class="container">
			<table align="left" width="750px">
				<tr>
					<td width="750px" >
						<fieldset>&nbsp; 
							<legend>Car&aacute;tula del Pedido Modificado</legend>
								<table align="left" cellpadding="2" width="100%">
							    	<tr>
							    		<td align="right" colspan="2">
							    			<img id="imgAprobarPresupuestoPedMod" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="guardaPedidoMod();" />&nbsp;Aprobar 
											<img id="imgDevolverPresupuestoPedMod" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer" onclick="devuelvePedidoMod();"/>&nbsp;Devolver
											<img id="imgSalir" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="window.location = 'PedidoModificatorio.jsp?tab=1';"/>&nbsp;Salir
							    		</td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" id="lblUnidadEjecutora" name="lblUnidadEjecutora" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" id="lblProcedimiento" name="lblProcedimiento" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 400px" id="lblDefinitivo" name="lblDefinitivo" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 400px" name="lblPedido" id="lblPedido" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblProveedor" id="lblProveedor" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 300px" name="lblEstadoMod" id="lblEstadoMod" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTipoMod" id="lblTipoMod" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotalAnterior" id="lblTotalAnterior" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotalModificado" id="lblTotalModificado" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotal" id="lblTotal" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr id="apartadoNotice">
										<td align="left" colspan="2"><span style="color:red;">Monto cubierto por el apartado: <span id="aptdReal"></span>.</span></td>
									</tr>			    	
							    </table>
						</fieldset>
					</td>
				</tr>
				
		    	<tr>
		    		<td>
		    			<fieldset style="width:750px" ><legend>Claves</legend>		    			
		    				<table align="left"  width="100%">
			    				<tr>
			    					<td style="width: 740px;">
				    					<table  id="dt_clavepresup" class="display">
											<thead>
												<tr align="left">
													<th>C&oacute;digo SAI</th> 
													<th>Clave SHCP</th> 
											 	</tr> 
											</thead> 
										</table>
			    					</td>
		    					</tr>
		    				</table>		
	    				</fieldset>				    
			         </td>              	
		    	</tr>
	        </table>
	        <!-- Hiddens de sesion -->
	        <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
		    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
		    <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
		    <input type="hidden" name="cIdTipoPedido" id="cIdTipoPedido" value="<%=cIdTipoPedido%>" />
		    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab.getLogin() %>"/>
		    <input type="hidden" name="cPedido" id="cPedido" value="<%=cIdPedido%>" />
		    <input type="hidden" name="cPedidoDefinitivo" id="cPedidoDefinitivo" value="<%=cIdPedidoDefinitivo%>" />
		    <input type="hidden" name="pedidoDefinitivo" id="pedidoDefinitivo" value="<%=cIdPedidoDefinitivo%>" />
		    <input type="hidden" name="cConsecutivoMod" id="cConsecutivoMod" value="<%=cIdConsecutivoMod%>" />
		    <input type="hidden" name="tipoMod" id="tipoMod" />
	        <input type="hidden" name="totalAnterior" id="totalAnterior" />
		    <input type="hidden" name="totalMod" id="totalMod" />
		    <input type="hidden" name="totalNuevo" id="totalNuevo" />
		    <input type="hidden" name="nIdEstado" id="nIdEstado" />
			<input type="hidden" name="nTipoPago" id="nTipoPago" />
			
			<!-- Para unir apartado con precompromiso -->
		    <input type="hidden" name="nApartadosUsados" id="nApartadosUsados" />
		    <input type="hidden" name="mApartadoReal" id="mApartadoReal" />
			<input type="hidden" name="cIdProcedimiento" id="cIdProcedimiento" />
			
		    <!--  Auxiliares para consulta -->		    
	       	<input type="hidden" name="epAUX" id="epAUX" />
	       	<input type="hidden" name="cIdUnidadEjecutoraEP" id="cIdUnidadEjecutoraEP" />
		    <input type="hidden" name="R_NOMBRE" id="R_NOMBRE" />
		    <input type="hidden" name="epsConsulta" id="epsConsulta" />
		    
		    <input type="hidden" name="usuarioCreacionOriginal" id="usuarioCreacionOriginal" value="" />
  			<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=name_user%>" />
  			<input type="hidden" name="usuarioLoginRole" id="usuarioLoginRole" value="" />   
		    
	    </div>
	</form>
  </body>
</html>
