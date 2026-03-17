<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
String cCentroContable = "", mensaje = "";

if (c == null) {
	response.sendRedirect("../index.jsp");
	return;
}

int id_oper = -1;
if (request.getParameter("id_oper") != null){
	id_oper = new Integer(request.getParameter("id_oper")).intValue();
}else{
	id_oper = c.getCasoOperacion(0).getIdOperacion();
}

String DATE_FORMAT = "yyyy-MM-dd";
SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
Calendar c1 = Calendar.getInstance(); // today
String today = sdf.format(c1.getTime());

if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
	cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
}
if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
	mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
}
String cUR = usuario.getU_UR();
String u_login = usuario.getLogin();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
     
    <title>Enviado a TESOFE</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page"> 
	
	<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
	<style type="text/css" title="currentStyle">
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "css/demo_table_jui.css";
		@import "css/demo_page.css";
	</style>
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	
	<script type="text/javascript" src="js/crud.js"></script>
	
<script type="text/javascript">	
$(document).ready( function(){
	
	queryFormPost("cEjercicioRead",{async: false });
	var nFolioInt = "<%=c.getFolio()%>";
	nF =  nFolioInt.split("-");
	$("#nFolio").val(nF[2]);
	informacionIgresada(nF[2]);
	$("#tblGuardar").hide();
	
	// Apertura Normal
	if(<%=id_oper == 7 %> && nF[0] == "SRCB"){
		
		$("#nFolio").val(nF[2]);
		$("#titulo").html("Enviado Registro a TESOFE");
		
	}else if(<%=id_oper == 4 %> && nF[0] == "MOCB"){
		
		$("#nFolioMod").val(nF[2]);
		$("#titulo").html("Confirmacion de Envio de Informacion a TESOFE");
		
	}else if(<%=id_oper == 4 %> && nF[0] == "BACB"){
		
		$("#nFolioCan").val(nF[2]);
		$("#titulo").html("Confirmacion de Envio de Informacion a TESOFE");
		
	}
	
});

function onPostDisplay(){
	
		parent.document.getElementById("pb_send").disabled = false;
		parent.document.getElementById("pb_send").click();
		
}

function onPostSubmit(id_oper){
  		
  		return true;
}

function onLoadPlantilla(){
		
		if(<%=id_oper ==7 %> && nF[0] == "SRCB"){
			
			parent.document.getElementById("pb_save").style.visibility='visible';
			parent.document.getElementById("pb_send").disabled=true;
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=false;
			
		}else if(<%=id_oper == 4 %> && nF[0] == "MOCB"){
			
			parent.document.getElementById("pb_save").style.visibility='visible'; // Guardar doSave()
			parent.document.getElementById("pb_send").disabled=true;              // Enviar
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").style.visibility='hidden'; 
			parent.document.getElementById("pb_cancel").disabled=false;          // Descartar
			
		}else if(<%=id_oper == 4 %> && nF[0] == "BACB"){
			
			parent.document.getElementById("pb_save").style.visibility='visible'; // Guardar doSave()
			parent.document.getElementById("pb_send").disabled=true;              // Enviar
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").style.visibility='hidden'; 
			parent.document.getElementById("pb_cancel").disabled=false;          // Descartar
			
		}
		
}
	
function onSubmit(id_oper){
	
	//validaciones del boton guardar
  		
		var p = window.parent;
  		var valida_campos = true;
  		
  		try{
				//validaciones de la forma
				//poner aqui las validaciones a efectual en el boton guardar, en este caso no hay
	
				//Guardado de los campos correspondientes a cada variable de caso
				
				if(<%=id_oper ==7 %> && nF[0] == "SRCB"){
					
					p.gestion.setFolio( $("#FOLIO").val() );
					
				}else if(<%=id_oper == 4 %> && nF[0] == "MOCB"){
					
					p.gestion.setFolio( $("#FOLIOMod").val() );
				
				}else if(<%=id_oper == 4 %> && nF[0] == "BACB"){
					
					p.gestion.setFolio( $("#FOLIOCan").val() );
				}
				
				p.gestion.setOperador( $("#OPERADOR").val() );
				p.gestion.setFechaDocumento( $("#fechaToday").val() );
				p.gestion.setEjercicioFiscal( $("#cEjercicio").val() );
				p.gestion.setConceptoMov("Revision Cuentas Bancarias");
				p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
				p.gestion.setFechaApCont( $("#fechaToday").val() );
	
				var rdEstatus = frmCuentasBancarias.rdEstatus[0].checked;
				
				var nretval = cmdGuardar(rdEstatus);
				
				if(nretval == "guardado") {
					
					p.validaEnviar();
					
				}else if(nretval == "sinConfirmar"){
					
					return false;
					
				}else{
					// sinGuardar
					alert("No se Puedo Guardar La Informacion, Intente de Nuevo");
					return false;
				}
		
		}catch(e){
				window.alert("onSubmit: Error: " + e.message);
				return false;
		}

		return valida_campos;
}

function cmdGuardar(rdEstatus){
	
	var rdEstatus = frmCuentasBancarias.rdEstatus[0].checked;
	
	var valor = "sinGuardar";
	var status = "";
	
	(rdEstatus == true)? $("#enviadoTesofeAutorizar").val(1) : $("#enviadoTesofeAutorizar").val(0);
	var msn = (rdEstatus == true)? "- Para Autorizar es Necesario Todos Los Campos " : "- Para Rechazar es Necesario Agregar Un Comentario";
	var status = (rdEstatus == true)? "¿ Esta Seguro Envio a TESOFE ?" : "¿ Esta Seguro de Rechazar Envio a TESOFE ?";
	var statusGuardar = (rdEstatus == true)? " Enviado a TESOFE Correctamente " : " Rechazado Enviado a TESOFE ";
	
	if(confirm(status)){
			
			var maxEncab = (<%=id_oper == 7 %> && nF[0] == "SRCB")? "tCuentaBancariaEncabezadoRead" : (<%=id_oper == 4 %> && nF[0] == "MOCB") ? "tCuentaBancariaModEncabezadoRead" : "tCuentaBancariaCanEncabezadoRead" ;
			var ejecutarEncab = (<%=id_oper == 7 %> && nF[0] == "SRCB")? "tCuentaBancariaEncabezadoF" : (<%=id_oper == 4 %> && nF[0] == "MOCB") ? "tCuentaBancariaModEncabezadoF" : "tCuentaBancariaCanEncabezadoF" ;
		
		
			if( ($("#enviadoTesofeFolio").val() != "" && $("#enviadoTesofeComentario").val() != "" && rdEstatus == true) || (rdEstatus == false && $("#enviadoTesofeComentario").val() != "") ){	
		
				queryFormPost(maxEncab,{async: false });
				
				queryFormPost({
							queryName : ejecutarEncab,
							async : false,
							callback : function() {
								
									alert(statusGuardar);
									valor = "guardado";
							}
				});
		
			}else{
				
				alert(msn);
				return;
				
			}
	}else{
		
		valor = "sinConfirmar";
	}
	
	return valor; 
}

function ResponsableSiguiente(id_oper){
		
		// Depende de O_Responsable de CG_OPERACION
  		 if(id_oper == 7 && $("#enviadoTesofeAutorizar").val() == 1 && nF[0] == "SRCB"){
  			 
  			return "CONFIRMACION_CUENTA_REG";
  			
  		 }else if(id_oper == 7 && $("#enviadoTesofeAutorizar").val() == 0 && nF[0] == "SRCB"){
  			 
  			return "CONFIRMAR_UR";
  		 
  		 }else if(id_oper == 4 && $("#enviadoTesofeAutorizar").val() == 1 && nF[0] == "MOCB"){
  			 
  			 return "CONFIRMACION_CUENTA_REG_MOD";
  			 
  		 }else if(id_oper == 4 && $("#enviadoTesofeAutorizar").val() == 0 && nF[0] == "MOCB"){
  			 
  			 return "CONFIRMAR_UR_MOD";
  			 
  		 }else if(id_oper == 4 && $("#enviadoTesofeAutorizar").val() == 1 && nF[0] == "BACB"){
  			 
  			 return "CONFIRMACION_CUENTA_REG_CAN";
  			 
  		 }else if(id_oper == 4 && $("#enviadoTesofeAutorizar").val() == 0 && nF[0] == "BACB"){
  			 
  			 return "CONFIRMAR_UR_CAN";
  			 
  		 }
}

function OperacionSiguiente(id_oper){
		
		//Depende del campo O_Nombre CG_OPERACION
  		if(id_oper == 7 && $("#enviadoTesofeAutorizar").val() == 1 && nF[0] == "SRCB"){
  			
  			return "confir_cuenta_reg";
  			
  		}else if(id_oper == 7 && $("#enviadoTesofeAutorizar").val() == 0 && nF[0] == "SRCB"){
  			
  			return "confirmar_UR";
  			
  		}else if(id_oper == 4 && $("#enviadoTesofeAutorizar").val() == 1 && nF[0] == "MOCB"){
  			
  			return "confir_cuenta_mod";  			
  			
  		}else if(id_oper == 4 && $("#enviadoTesofeAutorizar").val() == 0 && nF[0] == "MOCB"){
  			
  			return "confirmar_UR_mod";
  			
  		}else if(id_oper == 4 && $("#enviadoTesofeAutorizar").val() == 1 && nF[0] == "BACB"){
  			
  			return "confir_cuenta_can";
  		
  		}else if(id_oper == 4 && $("#enviadoTesofeAutorizar").val() == 0 && nF[0] == "BACB"){
  			
  			return "confirmar_UR_can";
  		}
		
}
function valida_concepto(e){
	
		var nChars = $("#autorizacionAperturaComentario").val();
		nChars = nChars.length;
		tecla = (document.all) ? e.keyCode : e.which;
		
		if (tecla==8) {
			$("#nChars").val( --nChars );
			if (nChars < 0)
				$("#nChars").val( 0 );
			return true;
		}
		
		if (nChars >= 500) {
			return false;
		}
		$("#nChars").val( ++nChars );
		patron =/[A-Za-z.\d\s\\. `,$-_%&]/;
		te = String.fromCharCode(tecla);
		return true; // patron.test(te);
}

function informacionIgresada(folio){
	
	var SRCB = " TOP 1 aperturaCuentaTesofe, aperturaCuentaComentario, aperturaCuentaAutorizar, envioTesofeAutorizar, envioTesofeFolio, envioTesofeComentario, aperturaCuentaBanco, aperturaCuentaClabe, aperturaCuentaRfc, aperturaCuentaFecha, aperturaCuentaTipoCuenta, aperturaCuentaNumeroCuenta, aperturaCuentaBeneficiario, autorizacionAperturaAutorizar, autorizacionAperturaContrato, autorizacionAperturaComentario, confirmacionEnvioTesofe ";
	var MOCB = " TOP 1 aperturaCuentaTesofe, aperturaCuentaComentario, aperturaCuentaAutorizar, envioTesofeAutorizar, envioTesofeFolio, envioTesofeComentario, aperturaCuentaBanco, aperturaCuentaClabe, aperturaCuentaRfc, aperturaCuentaFecha, aperturaCuentaTipoCuenta, aperturaCuentaNumeroCuenta, aperturaCuentaBeneficiario, autorizacionAperturaAutorizar, autorizacionAperturaContrato, autorizacionAperturaComentario, confirmacionEnvioTesofe, id_oper, nFolioCuentaBancaria ";
	var BACB = " TOP 1 aperturaCuentaTesofe, aperturaCuentaComentario, aperturaCuentaAutorizar, envioTesofeAutorizar, envioTesofeFolio, envioTesofeComentario, aperturaCuentaBanco, aperturaCuentaClabe, aperturaCuentaRfc, aperturaCuentaFecha, aperturaCuentaTipoCuenta, aperturaCuentaNumeroCuenta, aperturaCuentaBeneficiario, autorizacionAperturaAutorizar, autorizacionAperturaContrato, autorizacionAperturaComentario, confirmacionEnvioTesofe, id_oper, nFolioCuentaBancaria ";
	
	var camposWhere = (<%=id_oper%> == 7 && nF[0] == "SRCB") ? SRCB : (<%=id_oper%> == 4 && nF[0] == "MOCB") ? MOCB : BACB;
	var param = (<%=id_oper%> == 7 && nF[0] == "SRCB") ? " nFolioCuentaBancaria = "+folio : (<%=id_oper%> == 4 && nF[0] == "MOCB") ? "nFolioCuentaBancariaMod = "+folio : "nFolioCuentaBancariaCan = "+folio;
	var order = " ORDER BY nDocRenglon DESC ";
	var tabla = (<%=id_oper%> == 7 && nF[0] == "SRCB") ? "TCUENTABANCARIAENCABEZADOINF" : (<%=id_oper%> == 4 && nF[0] == "MOCB") ? "TCUENTABANCARIAENCABEZADOMODINF" : "TCUENTABANCARIAENCABEZADOCANINF";
	
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla:tabla, Campos:camposWhere, Param:param, Order:order, MaxReg: "1", ajax:"true" }, function(j){ 
													
			for(var i = 0; i < j.length; i++ ){
				
					$("#aperturaCuentaTesofe").val(j[i].Col0);		
					$("#aperturaCuentaComentario").val(j[i].Col1);	
					$("#aperturaCuentaAutorizar").val(j[i].Col2);
					$("#envioTesofeAutorizar").val(j[i].Col3);
					$("#envioTesofeFolio").val(j[i].Col4);
					$("#envioTesofeComentario").val(j[i].Col5);
					$("#aperturaCuentaBanco").val(j[i].Col6);
					$("#aperturaCuentaClabe").val(j[i].Col7);
					$("#aperturaCuentaRfc").val(j[i].Col8);
					$("#aperturaCuentaFecha").val(j[i].Col9);
					$("#aperturaCuentaTipoCuenta").val(j[i].Col10);
					$("#aperturaCuentaNumeroCuenta").val(j[i].Col11);
					$("#aperturaCuentaBeneficiario").val(j[i].Col12);
					$("#autorizacionAperturaAutorizar").val(j[i].Col13);
					$("#autorizacionAperturaContrato").val(j[i].Col14);
					$("#autorizacionAperturaComentario").val(j[i].Col15);
					$("#confirmacionEnvioTesofe").val(j[i].Col16);	
					
					if(<%=id_oper == 4 %> && nF[0] == "MOCB" || <%=id_oper == 4 %> && nF[0] == "BACB"){
						$("#id_oper").val(j[i].Col17);
						$("#nFolio").val(j[i].Col18);
					}
			}
	});
}

</script>
</head>
<body id="dt_example">
	<form id="frmCuentasBancarias" name="frmCuentasBancarias" >
			<div id="container" class="container SyCData" style="width:800px; align:center">
				<h1><div id="titulo"></div><label style="font-size: 9pt"></label></h1>
   				
   				<table id="tblGuardar">
   					<tr>	
			   				<td>id_oper<input type="text" name="id_oper" id="id_oper" value="<%=id_oper%>" /></td>
			   				<td>nFolio<input type="text" name="nFolio" id="nFolio" /></td>
			   				<td>renglon<input type="text" id="nDocRenglon" name="nDocRenglon" /></td>
			   				<td>nFolioCaso<input type="text" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" /></td>
					</tr>	
					<tr>
			   				<td>id_oper_mod<input type="text" name="id_oper_mod" id="id_oper_mod" value="<%=id_oper%>" /></td>
			   				<td>nFolio_mod<input type="text" name="nFolioMod" id="nFolioMod" /></td>
			   				<td>nFolio_mod<input type="text" name="FOLIOMod" id="FOLIOMod" value="<%=c.getFolio()%>" /></td>
					</tr>
					<tr>
			   				<td>id_oper_can<input type="text" name="id_oper_can" id="id_oper_can" value="<%=id_oper%>" /></td>
			   				<td>nFolio_can<input type="text" name="nFolioCan" id="nFolioCan" /></td>
			   				<td>nFolio_can<input type="text" name="FOLIOCan" id="FOLIOCan" value="<%=c.getFolio()%>" /></td>
					</tr>
					<tr>			
							<td>operador<input type="text" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" /></td>
							<td>fecha<input type="text" name="fechaToday" id="fechaToday" value="<%=today%>" /></td>
							<td>ejercicio<input type="text" name="cEjercicio" id="cEjercicio"  /></td>
							<td>cDocumento<input type="text" id="cDocumento" name="cDocumento" value="<%=c.getIdCaso()%>" /></td>
					</tr>
					<tr>	
							<td>caso<input type="text" id="caso" name="caso" value="<%=c.getCasoOperacion(0).getOperacion()%>" /></td>
							<!-- input type="text" value="DIRECTO" id="TO_TIPO_DOCTO"	name="TO_TIPO_DOCTO" / -->
							<td>centroContable<input type="text" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>" /></td>
							<td>UR<input type="text" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /></td>
							<td>login<input type="text" id="usuarioLogin" name="usuarioLogin" value="<%=u_login%>" /></td>
					</tr>
					<tr>				
							<td><input type="hidden" id="aperturaCuentaAutorizar" name="aperturaCuentaAutorizar" /></td>
							<td><input type="hidden" id="aperturaCuentaTesofe" name="aperturaCuentaTesofe" /></td>
							<td><input type="hidden" id="aperturaCuentaComentario" name="aperturaCuentaComentario" /></td>
					</tr>	
					<tr>			
							<td><input type="hidden" id="envioTesofeAutorizar" name="envioTesofeAutorizar" /></td>
							<td><input type="hidden" id="envioTesofeFolio" name="envioTesofeFolio" /></td>
							<td><input type="hidden" id="envioTesofeComentario" name="envioTesofeComentario" /></td>
					</tr>
					<tr>			
							<td><input type="hidden" id="aperturaCuentaBanco" name="aperturaCuentaBanco" /></td>
							<td><input type="hidden" id="aperturaCuentaClabe" name="aperturaCuentaClabe" /></td>
							<td><input type="hidden" id="aperturaCuentaRfc" name="aperturaCuentaRfc" /></td>
							<td><input type="hidden" id="aperturaCuentaFecha" name="aperturaCuentaFecha" /></td>
							<td><input type="hidden" id="aperturaCuentaTipoCuenta" name="aperturaCuentaTipoCuenta" /></td>
							<td><input type="hidden" id="aperturaCuentaNumeroCuenta" name="aperturaCuentaNumeroCuenta" /></td>
							<td><input type="hidden" id="aperturaCuentaBeneficiario" name="aperturaCuentaBeneficiario" /></td>
					</tr>
					<tr>				
							<td><input type="hidden" id="autorizacionAperturaAutorizar" name="autorizacionAperturaAutorizar" /></td>
							<td><input type="hidden" id="autorizacionAperturaContrato" name="autorizacionAperturaContrato" /></td>
							<td><input type="hidden" id="autorizacionAperturaComentario" name="autorizacionAperturaComentario" /></td>
					</tr>
					<tr>				
							<td><input type="hidden" id="confirmacionEnvioTesofe" name="confirmacionEnvioTesofe" /></td>
					</tr>			
					<tr>	
							<td><input type="hidden" id="enviadoTesofeAutorizar" name="enviadoTesofeAutorizar" /></td>
							<!-- input type="hidden" id="enviadoTesofeFolio" name="enviadoTesofeFolio" />
							<input type="hidden" id="enviadoTesofeComentario" name="enviadoTesofeComentario" / -->
							
							<td><input type="hidden" id="confirmacionTesofe" name="confirmacionTesofe" /></td>
					</tr>			
					</table>		
				<table id="tblEstatus" align="center">
					<tr>
						<td><input type="radio" name="rdEstatus" id="autorizar" /></td><td>Autorizar</td>
						<td><input type="radio" name="rdEstatus" id="rechazar" checked="checked" /></td><td>Rechazar</td>
					</tr>
				</table>
				<tr><td>&nbsp;</td></tr>
   				<fieldset> 
   					<table id="tblTESOFE" width="800px" height ="140px" style="text-align:center">
   						
   							<tr><td>Para Continuar con este Tramite favor de adjuntar oficio de confirmacion de TESOFE:</td></tr>
   							
   							<tr><td>- Capture el Folio y Adjunte el Oficio de Autorizacion de TESOFE</td></tr>
   							<tr><td><input type="text" name="enviadoTesofeFolio" id="enviadoTesofeFolio" size ="50" maxlength="20"></td></tr>
   							
   					</table>
   				</fieldset> 
   				<tr><td>&nbsp;</td></tr>
   				<fieldset>
   					<legend>Comentarios</legend>
   						
   						<table id="tblTESOFE" width="800px" height ="150px">
   							<tr><td>&nbsp;</td></tr>
   							<tr><td><textarea name="enviadoTesofeComentario" cols="90" rows="7" style="font-size: 10.5pt" onkeydown="return valida_concepto(event)" ></textarea></td></tr>

   						</table>
   				</fieldset>
   			</div>
 	</form>  
</body>
</html>