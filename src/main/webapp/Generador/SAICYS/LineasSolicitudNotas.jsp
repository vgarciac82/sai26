<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   <%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%


	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	String cEjercicio = "";
	String cIdTipoSolicitud = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	
	if (session.getAttribute(GestionInterface.ATT_ReqEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ReqEjercicio);
		cIdTipoSolicitud = (String)session.getAttribute(GestionInterface.ATT_ReqTipoSolicitud);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ReqUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ReqConsecutivo);
	}
	
%>

	<head>
		<title>Notas</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
			@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../css/demo_table_jui.css";
			@import "../css/demo_page.css";
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
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>
		
		 <script type="text/javascript" charset="utf-8">
		  	$(document).ready(function() {
		  		//var tipo=<%=request.getParameter("tipo")%>
		  		//alert("fffffffffffff");
		  		if(<%=request.getParameter("tipo")%>!=2){
		  			cerrardiv('anulacion');
		  		}
		  		else
		  			cerrardiv('devolucion');
		  		
		  	});
		  	function fnGuardar(){
		  		//alert("Devolucion de la requisicion");
		  		//alert('<%=request.getParameter("id")%>');
		  		$("#lblRequisicion").val('<%=request.getParameter("id")%>');
		  		//$("#cIdSolicitud").val($("#lblRequisicion").val());
		  		$("#mNotas").val($("#cNotas").val());
		  		//alert($("#Notas").val());
		  		//Para guardar las notas
		  		queryFormPost("mLineasSolicitudUpdate", { async:false });
		  		$("#fAprobacion").val("1900-01-01 00:00:00.000");
				$("#nIdEstado").val("1"); //capturada
				$("#nIdEstadoPrecomprometido").val("1"); //para votarla a capturada
				queryFormPost("mSolicitudDevolverUpdate", { async:false });
				queryFormPost("sp_mSolicitudEstadoPrecomprometido", {async:false});
				//Bitácora
				$("#cAccion").val("DEVOLVER");
				//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
				$("#cIdDocumento").val($("#lblRequisicion").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				//alert($("#cIdUsuario").val());
				opener.document.location.reload();
				//window.location = "Requisiciones.jsp?tab=3";
				//alert("La requicisión ha sido devuelta.");
		  		window.close();
		  		
		  	}
		  	
		  function cerrardiv(id) {
			var eldiv = document.getElementById(id);
			eldiv.style.display='none';
		}
		function mostrardiv(id) {
			var eldiv = document.getElementById(id);
			eldiv.style.display ='block';
		}
		function fnGuardarA(){
		  		if (confirm("¿Está seguro que desea anular esta requisición? Una vez eliminada no podrá hacer uso de ésta.")) {
		  			$("#lblRequisicion").val('<%=request.getParameter("id")%>');
		  			$("#mNotas").val($("#cNotasA").val());
		  			queryFormPost("mLineasSolicitudUpdate", { async:false });
					//Regla de negocio
					var currentTime = new Date();
					$("#fAnulacion").val(currentTime.getFullYear() +'-' + currentTime.getDate() + '-' + (currentTime.getMonth()+1) +  ' ' + currentTime.getHours() + ':' + currentTime.getMinutes() + ':' + currentTime.getSeconds() + '.' + currentTime.getMilliseconds());
					$("#nIdEstado").val("4"); //capturada
					$("#cIdEstadoLinea").val("C");
					queryFormPost("mSolicitudAnularUpdate", { async:false });
					queryFormPost("mSolicitudLineasCompletaAnular", {async:false});
					//Bitácora
					$("#cAccion").val("ANULAR");
					
					$("#cIdDocumento").val($("#lblRequisicion").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
					opener.document.location.reload();
					
					window.close();				                                    
			}
		  		
		}
		  </script>

  </head>
 
  <body>
  	<div id="devolucion">
	  <fieldset>
		<legend>
		Notas de Devolucion
		</legend>
	     <table>
			<tr>
				<td align="right">Descripción:</td>
	            <td align="left"><textarea id="cNotas" name="cNotas"  rows="4" cols="3" style="height: 91px; width: 400px" ></textarea></td>
			</tr>
			<tr>
				<td><input type="button" value="Guardar" name="btnGuardar" id="btnGuardar"	onclick="fnGuardar();"></td>
				
			</tr>
			
		</table>
		</fieldset>
	</div>
	<div id="anulacion">
	
		<fieldset>
		<legend>
		Notas de Anulacion
		</legend>
	     <table>
			<tr>
				<td align="right">Descripción:</td>
	            <td align="left"><textarea id="cNotasA" name="cNotasA"  rows="4" cols="3" style="height: 91px; width: 400px" ></textarea></td>
			</tr>
			<tr>
				<td><input type="button" value="Guardar" name="btnGuardar2" id="btnGuardar2"	onclick="fnGuardarA();"></td>
				
			</tr>
			
		</table>
		</fieldset>
	
	</div>
	<form action="">
		<input type="hidden" name="lblRequisicion" id="lblRequisicion"/>
		<input type="hidden" name="fAprobacion" id="fAprobacion"/>
		<input type="hidden" name="nIdEstado" id="nIdEstado"/>
		<input type="hidden" name="nIdEstadoPrecomprometido" id="nIdEstadoPrecomprometido"/>
		<input type="hidden" name="cAccion" id="cAccion"/>
		<input type="hidden" name="cIdDocumento" id="cIdDocumento"/>
		<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuario.getLogin()%>"/>
		<input type="hidden" name="mNotas" id="mNotas"/>
		<input type="hidden" name="cIdUsuarioAprobacion" id="cIdUsuarioAprobacion" value="<%=usuario.getLogin()%>"/>
		
		 <input type="hidden" name="cIdTipoSolicitud" id="cIdTipoSolicitud" value="<%=cIdTipoSolicitud%>"/>
	        <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
	        <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
		    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
		    <input type="hidden" name="cIdSolicitud" id="cIdSolicitud"/>
		    <!-- Para Anulacion -->
		 <input type="hidden" name="fAnulacion" id="fAnulacion"/>
		 
		 <input type="hidden" name="cIdEstadoLinea" id="cIdEstadoLinea"/>  
		 
		     
		    
	</form>
  	
  </body>
</html>
