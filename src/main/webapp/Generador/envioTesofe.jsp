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

String DATE_FORMAT = "dd-MM-yyyy";
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
	<script type="text/javascript" src="js/crud.js"></script>
	
<script type="text/javascript">	
$(document).ready( function(){
	
	queryFormPost("cEjercicioRead",{async: false });
	var nFolioInt = "<%=c.getFolio()%>";
	nF =  nFolioInt.split("-");
	$("#nFolio").val(nF[2]);
	informacionRevisionCuentaBancaria(nF[2]);
	$("#tblGuardar").hide();
	
});

function onPostDisplay(){
	
		parent.document.getElementById("pb_send").disabled = false;
		parent.document.getElementById("pb_send").click();
		
}

function onPostSubmit(id_oper){
  		
  		return true;
}

// Despues de cargar la jsp
function onLoadPlantilla(){
		
		if(<%=id_oper == 3%>){
			
			parent.document.getElementById("pb_save").style.visibility='visible';
			parent.document.getElementById("pb_send").disabled=true;
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=false;
			
		}else{
			
			parent.document.getElementById("pb_save").style.visibility='hidden'; // Guardar doSave()
			parent.document.getElementById("pb_send").style.visibility='hidden'; // Enviar
			parent.document.getElementById("pb_cancel").style.visibility='hidden'; // Descartar
			
		}
}

// Boton Guardar
function onSubmit(id_oper){
	
		//validaciones del boton guardar
  		
		var p = window.parent;
  		var valida_campos = true;
  		
  		try{
				p.gestion.setFolio( $("#FOLIO").val() );
				p.gestion.setOperador( $("#OPERADOR").val() );
				p.gestion.setFechaDocumento( $("#fechaToday").val() );
				p.gestion.setEjercicioFiscal( $("#cEjercicio").val() );
				p.gestion.setConceptoMov("Enviado Tesofe");
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
	
	var valor = "sinGuardar";
			
	(rdEstatus == true)? $("#envioTesofeAutorizar").val(1) : $("#envioTesofeAutorizar").val(0);
	
	var status = (rdEstatus == true)? "¿ Esta Seguro de Autorizar Envio TESOFE ?" : "¿ Esta Seguro de Rechazar Envio TESOFE ?";
	var statusGuardar = (rdEstatus == true)? " Autorizado Enviado TESOFE Correctamente " : " Rechazado Enviado TESOFE";
	
	if(confirm(status)){
		
			if( ($("#envioTesofeFolio").val() != "" && $("#envioTesofeComentario").val() != "" && rdEstatus == true) || (rdEstatus == false) ){	
		
				queryFormPost("tCuentaBancariaEncabezadoRead",{async: false });
				
				queryFormPost({
							queryName : "tCuentaBancariaEncabezado",
							async : false,
							callback : function() {
								
									alert(statusGuardar);
									valor = "guardado";
							}
				});
		
			}else{
				
				alert("Todos Los Campos Son Obligatorio");
				
			}
	}else{
		
		valor = "sinConfirmar";
	}
	
	return valor;
	
}

function ResponsableSiguiente(id_oper){
		
		// Depende de O_Responsable de CG_OPERACION
  		 if(id_oper == 3 && $("#envioTesofeAutorizar").val() == 1){
  			 
  			return "APERTURA_DE_CUENTAS";
  			
  		 }else{
  			 
  			return "REVISION_DE_CUENTAS";
  		 }	
}

function OperacionSiguiente(id_oper){
		
		//Depende del campo O_Nombre CG_OPERACION
  		if(id_oper == 3 && $("#envioTesofeAutorizar").val() == 1){
  			
  			return "apertura_cuentas";
  			
  		}else{
  			
  			return "revision_cuentas";
  			
  		 }	
}

function valida_concepto(e){
	
		var nChars = $("#aperturaCuentaComentario").val();
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

function informacionRevisionCuentaBancaria(folio){
	
	var camposWhere = " TOP 1 aperturaCuentaAutorizar, aperturaCuentaTesofe, aperturaCuentaComentario "
	var param = " nFolioCuentaBancaria = "+folio+" AND activo = 1";
	var order = " ORDER BY nDocRenglon DESC ";
	
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"TCUENTABANCARIAENCABEZADOINF", Campos:camposWhere, Param:param, Order:order, MaxReg: "1", ajax:"true" }, function(j){ 
													
			for(var i = 0; i < j.length; i++ ){
				
					$("#aperturaCuentaAutorizar").val(j[i].Col0);		
					$("#aperturaCuentaTesofe").val(j[i].Col1);	
					$("#aperturaCuentaComentario").val(j[i].Col2);	
			}
	});
}
</script>
</head>
<body id="dt_example">
	<form id="frmCuentasBancarias" name="frmCuentasBancarias" >
			<div id="container" class="container SyCData" style="width:800px; align:center">
			
				<h1>Enviado a TESOFE<label style="font-size: 9pt"></label></h1>
   				
   				<table id="tblGuardar">
	   				<tr>
		   				<td>id_oper<input type="text" name="id_oper" id="id_oper" value="<%=id_oper%>" /></td>
		   				<td>nFolio<input type="text" name="nFolio" id="nFolio" /></td>
		   				<td>renglon<input type="text" id="nDocRenglon" name="nDocRenglon" /></td>
		   				<td>nFolioCaso<input type="text" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" /></td>
					</tr>
					<tr>	
						<td>operador<input type="text" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" /></td>
						<td>fecha<input type="text" name="fechaToday" id="fechaToday" value="<%=today%>" /></td>
						<td>ejercicio<input type="text" name="cEjercicio" id="cEjercicio"  /></td>
						<td>cDocumento<input type="text" id="cDocumento" name="cDocumento" value="<%=c.getIdCaso()%>" /></td>
					</tr>
					<tr>
						<td>centroContable<input type="text" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>" /></td>
						<td>UR<input type="text" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /></td>
						<td>login<input type="text" id="usuarioLogin" name="usuarioLogin" value="<%=u_login%>" /></td>
		   			</tr>
		   			<tr>	
						<td><input type="text" id="aperturaCuentaAutorizar" name="aperturaCuentaAutorizar" /></td>
						<td><input type="text" id="aperturaCuentaTesofe" name="aperturaCuentaTesofe" /></td>
						<td><input type="text" id="aperturaCuentaComentario" name="aperturaCuentaComentario" /></td>
					</tr>	
					<tr>
						<td><input type="hidden" id="envioTesofeAutorizar" name="envioTesofeAutorizar" /></td>
						<!-- input type="hidden" id="envioTesofeFolio" name="envioTesofeFolio" />
						<input type="hidden" id="envioTesofeComentario" name="envioTesofeComentario" / -->
					</tr>	
					<tr>
						<td><input type="hidden" id="aperturaCuentaBanco" name="aperturaCuentaBanco" /></td>
						<td><input type="hidden" id="aperturaCuentaClabe" name="aperturaCuentaClabe" /></td>
						<td><input type="hidden" id="aperturaCuentaRfc" name="aperturaCuentaRfc" /></td>
						<td><input type="hidden" id="aperturaCuentaFecha" name="aperturaCuentaFecha" /></td>
					</tr>
					<tr>	
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
						
						<td><input type="hidden" id="enviadoTesofeAutorizar" name="enviadoTesofeAutorizar" /></td>
						<td><input type="hidden" id="enviadoTesofeFolio" name="enviadoTesofeFolio" /></td>
						<td><input type="hidden" id="enviadoTesofeComentario" name="enviadoTesofeComentario" /></td>
					</tr>
					<tr>	
						<td><input type="hidden" id="confirmacionTesofe" name="confirmacionTesofe" /></td>
					</tr>	
				</table>		
   				<table id="tblEstatus" align="center">
					<tr>
						<td><input type="radio" name="rdEstatus" id="autorizar" ></td><td>Autorizar</td>
						<td><input type="radio" name="rdEstatus" id="rechazar"  checked="checked"></td><td>Rechazar</td>
					</tr>
				</table>
				<tr><td>&nbsp;</td></tr>
   				<fieldset> 
   					<table id="tblTESOFE" width="800px" height ="140px" style="text-align:center">
   						
   							<tr><td>Para Continuar con este Tramite favor de anexar los documentos siguientes:</td></tr>
   							
   							<tr><td>- Capture el Folio y Adjunte el Oficio de Autorizacion de TESOFE</td></tr>
   							<tr><td><input type="text" name="envioTesofeFolio" id="envioTesofeFolio" size="50" maxlength="20"></td></tr>
   							
   					</table>
   				</fieldset> 
   				<tr><td>&nbsp;</td></tr>
   				<fieldset>
   					<legend>Comentarios</legend>
   						
   						<table id="tblTESOFE" width="800px" height ="150px">
   							<tr><td>&nbsp;</td></tr>
   							<tr><td><textarea name="envioTesofeComentario" cols="90" rows="7" style="font-size: 10.5pt" onkeydown="return valida_concepto(event)"></textarea></td></tr>
   						</table>
   				   						
   				</fieldset>
   			</div>
 	</form>  
</body>
</html>