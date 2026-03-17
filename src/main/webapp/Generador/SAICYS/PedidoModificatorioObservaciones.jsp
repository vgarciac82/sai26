<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuarioTab.getLogin();
	String role="";
	Map rol =usuarioTab.getRoles();
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	
	String mensaje = request.getParameter("cMensaje");
	if (mensaje == null) {
		mensaje = "";
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
		
		cIdPedido = cIdPedidoDefinitivo.split("/")[0];
		cEjercicio = cIdPedidoDefinitivo.split("/")[1];
		
		cIdTipoPedido = cIdPedido.split("-")[0];
		cIdUnidadEjecutora = cIdPedido.split("-")[1];
		nIdConsecutivo = cIdPedido.split("-")[2];		
	}else 
		response.sendRedirect("PedidoModificatorio.jsp?tab=1");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Observaciones Pedido Modificado</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
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
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" charset="utf-8">	
		$(document).ready(function() {
			<%
			   
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
			//	NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
				}
				Map botones=nb.getBotones(role,"PedidoModificatorio","observacionesPedidoMod");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
				
			%>
			
			setFieldsInit();
			
		});
		
		function setFieldsInit(){
			//asigna el valor de true a readonly para los campos que sirven de etiquetas
			document.getElementById("lblUnidadEjecutora").style.readonly = true;
			document.getElementById("lblProcedimiento").style.readonly = true;
			document.getElementById("lblDefinitivo").style.readonly = true;
			document.getElementById("lblPedido").style.readonly = true;
			document.getElementById("lblProveedor").style.readonly = true;
			document.getElementById("lblEstadoMod").style.readonly = true;
			document.getElementById("lblTotalAnterior").style.readonly = true;
			document.getElementById("lblTotalModificado").style.readonly = true;
			document.getElementById("lblTotal").style.readonly = true;
						
			//Carga de cabecera
			queryFormPost("mPedidoHeaderRead", {async: false});
			queryFormPost("mPedidoModificadoTotales", {async: false});
			if ($("#tipoMod").val() == 1){
				
				document.getElementById("presupuestoPedidoMod").disabled = true;
				document.getElementById("precompromisoPedidoMod").disabled = true;
				document.getElementById("pagosPedidoMod").disabled = true;
			
			}
			else{
				document.getElementById("reduccionPedidoMod").disabled = true;
			}
			
			if(parseInt($("#nIdEstado").val(),10) == 1){
				document.getElementById("precompromisoPedidoMod").disabled = true;
			}
			
			if ($("#rutaImagen").val() != 'sinimagen') {
				$("#imagen").attr("src",$("#rutaImagen").val());
			}
			else {
				$("#imagen").css("visibility","hidden");
			}

			queryFormPost("mPedidoModicadoChecaRolUsuario", {async: false});
			if ($("#usuarioLoginRole").val() != 'ADMIN_RECMAT') { 
				$("#usuarioCreacionOriginal").val('');
				queryFormPost("mPedidoModicadoUsuarioCreacionOriginalRead",{async: false });
				if ($("#usuarioCreacionOriginal").val() != $("#usuarioLogin").val()){
					document.getElementById("form_cargarArchivo").disabled = true;
				} 
			}
		}
		
		
		function validaCargarArchivo(){
			var nomTem = document.getElementById("cargaArchivo").value;
			if(nomTem == ""){
				alert("No se cargo ningun archivo.");
			}
			else{
				var extensiones_permitidas = new Array(".png", ".jpg", ".gif"); 
				//recupero la extensión de este nombre de archivo 
				var extension = (nomTem.substring(nomTem.lastIndexOf("."))).toLowerCase(); 
				//compruebo si la extensión está entre las permitidas 
				var permitida = false; 
				for (var i = 0; i < extensiones_permitidas.length; i++) { 
					if (extensiones_permitidas[i] == extension) { 
						permitida = true; 
						break;
					}
				}
				
				if (!permitida) { 
					alert ("Formato de imagen no soportado. \nSólo se pueden subir imágenes con extensiones: " + extensiones_permitidas.join()); 
				}
				else{
	         		document.form_cargarArchivo.submit();
      			}
			}
		}
		
	</script>
  </head>
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" >
  	<form > 
		<div id="container" class="container" >
			<table width="94%" align="left">
				<tr>
					<td width="750px" >
						<fieldset>&nbsp; 
							<legend>Observaciones del Pedido Modificado</legend>
								<table align="left" cellpadding="2" width="100%">
							    	<tr>
							    		<td align="right" colspan="2">
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
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotalAnterior" id="lblTotalAnterior" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotalModificado" id="lblTotalModificado" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotal" id="lblTotal" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>				    	
							    </table>
						</fieldset>
					</td>
				</tr>
				
			</table>
		    <!-- Hidden's -->
		    <!-- Sesion  -->
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
		    <input type="hidden" name="R_NOMBRE" id="R_NOMBRE" >
		    
		    <input type="hidden" name="totalAnterior" id="totalAnterior" />
		    <input type="hidden" name="totalMod" id="totalMod" />
		    <input type="hidden" name="totalNuevo" id="totalNuevo" />
		    
		    <!-- Resultado de consultas -->
		    <input type="hidden" name="nIdEstado" id="nIdEstado" />
		    <input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />
		    <input type="hidden" name="rutaImagen" id="rutaImagen" />	    
		    <!--  Hidden valores auxiliares -->
		    
		    <input type="hidden" name="usuarioCreacionOriginal" id="usuarioCreacionOriginal" value="" />
  			<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=usuarioTab.getLogin()%>" />
  			<input type="hidden" name="usuarioLoginRole" id="usuarioLoginRole" value="" />
		    
	    </div>
	</form>
	
	<form name="form_cargarArchivo" id = "form_cargarArchivo" action="../../servlet/imagenModificatorio"  enctype="multipart/form-data" method="post" >
		<table >
	  		<tr>
	  			<td>
					Seleccione la imagen
				</td>
				<td width="10px" >
				</td>
				<td>
					<input type="file" name="cargaArchivo" id="cargaArchivo"  value="" >
				</td>
			</tr>
		</table>
		<div id="divMensaje" style="font-family: 'Arial', serif; font-size: 10pt; color:#FF0000;">
		</div>
		<script>
			document.getElementById("divMensaje").innerHTML="<%=mensaje%>".replace("'","");
		</script>
		<button id="" onclick="validaCargarArchivo();" >Guardar Imagen</button>
		<br/>
		<img id="imagen" src="#" />
	</form>
	
  </body>
</html>
