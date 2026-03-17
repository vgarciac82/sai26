<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
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
     
    <title>Cancelacion de Cuentas Bancarias</title>
    
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
	
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>	
	
<script type="text/javascript">	
$(document).ready( function(){
	
	$("input.AyudaSyC").subIniciaDlg();	//Para mostrar Ayudas
	$("#btnBuscar").click(function () { buscarInformacionIgresada(); });
	queryFormPost("cEjercicioRead",{async: false });
	queryFormPost("BancosRead",{async: false });
	var nFolioInt = "<%=c.getFolio()%>";
	nF =  nFolioInt.split("-");
	$("#nFolioCan").val(nF[2]);
	informacionIgresada(nF[2]);
	//buscarInformacionIgresada();
	//informacionIgresadaMod(nF[2]);
	$("#tblGuardar").hide();
	$("#tblRechazo").hide();
	
	$("#aperturaCuentaFecha").datepicker({
			showOn: "button",
			dateFormat:"dd/mm/yy",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
	});
			
	//$("#btnPrueba").click(function(){  var nRows = $("#tableCuentas tr").length -1; alert(nRows); /*cmdGuardarDetalle();*/ /*agregarFirmante(); cmdGuardar();*/ });
	
});

function onPostDisplay(){
	
		parent.document.getElementById("pb_send").disabled = false;
		parent.document.getElementById("pb_send").click();
		
}

function onPostSubmit(id_oper){
  		
  		return true;
}

function onLoadPlantilla(){
		
		if(<%=id_oper == 1%>){
			
			parent.document.getElementById("pb_save").style.visibility='hidden';
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
	
function onSubmit(id_oper){
	
  //validaciones del boton guardar
  if(confirm(" ¿ Esta Seguro de Mandar Solicitud de Cancelacion ?")){
	  
		var faltaDato = "";
		
		var p = window.parent;
  		var valida_campos = true;
  		
  		try{
				p.gestion.setFolio( $("#FOLIOCan").val() );
				p.gestion.setOperador( $("#OPERADOR").val() );
				p.gestion.setFechaDocumento( $("#fechaToday").val() );
				p.gestion.setEjercicioFiscal( $("#cEjercicio").val() );
				p.gestion.setConceptoMov("Cancelacion Cuentas Bancarias");
				p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
				p.gestion.setFechaApCont( $("#fechaToday").val() );
	
				var nretval = cmdGuardar();
				
				if(nretval == "guardado"){
					
					p.validaEnviar();
					
				}else{
					// sinGuardar
					alert("No se Puedo Hacer Solicitud de Cancelacion, Intente de Nuevo");
					return false;
				}
	
		}catch(e){
				window.alert("onSubmit: Error: " + e.message);
				return false;
		}

		return valida_campos;
	
   }
}

function cmdGuardar(){
	
	var valor = "sinGuardar";
	
	queryFormPost("tCuentaBancariaCanEncabezadoRead",{async: false });
	
	queryFormPost({
				queryName : "tCuentaBancariaCanEncabezadoF",
				async : false,
				callback : function() {
					
						alert("Solicitud de Cancelacion Guardado Correctamente");
						valor = "guardado";
				}		
	});
	
	return valor;
}

function ResponsableSiguiente(id_oper){
		
		// Depende de O_Responsable de CG_OPERACION
  		 if(id_oper == 1){
  			 
  			return "AUTORIZA_CUENTAS_CAN";
  		 
  		 }
}

function OperacionSiguiente(id_oper){
		
  		if(id_oper == 1){
  			
  			return "autoriza_cuentas_can";
  		
  		}
}

function buscarInformacionIgresada(){
	
	var datos = "sinDatos";
	var datosExist = "sinDatos";
	var nFolioInt = $("#buscarFolio").val();
	nF =  nFolioInt.split("-");
	
	if(nF[0] == "SRCB"){
		
			// Verifica si Existe en tCuentaBancariaEncabezado y esta confirmacionTesofe en 1 y verifica que no este en Modificado.
			
			var campos = " TOP 1 aperturaCuentaTesofe, aperturaCuentaComentario, aperturaCuentaAutorizar, envioTesofeAutorizar, envioTesofeFolio, envioTesofeComentario, aperturaCuentaBanco, aperturaCuentaClabe, aperturaCuentaRfc, aperturaCuentaFecha, aperturaCuentaTipoCuenta, aperturaCuentaNumeroCuenta, aperturaCuentaBeneficiario,  "
			    campos += " id_oper, nFolioCuentaBancaria, nDocRenglon, confirmacionTesofe, ";
				campos += " isnull((select TOP 1 confirmacionTesofe from tCuentaBancariaModEncabezado with(nolock) where nFolioCuentaBancaria = "+nF[2]+" and activo = 1 order by nDocRenglon desc ),2) as existeMod ";
				campos+= " ,isnull((select top 1 confirmacionTesofe from tCuentaBancariaCanEncabezado where nFolioCuentaBancaria = "+nF[2]+" order by nDocRenglon desc ),2) as procesoCancelado ";
			var param = " NFOLIOCUENTABANCARIA = "+nF[2]+" AND activo = 1 ";
			var order = " ORDER BY nDocRenglon DESC ";
							
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"TCUENTABANCARIAENCABEZADOINF", Campos:campos, Param:param, Order:order, MaxReg: "1", ajax:"true" }, function(j){ 
																			
						for(var i = 0; i < j.length; i++ ){
										
											if(j[i].Col16 == 0){
												
												datos = "datosSinConfirmarTesofe";
												
											}else if(j[i].Col17 == 0 ){
												
												datos = "datosModificando";
												
											}else{
																					
												datos = "datos";
												$("#aperturaCuentaTesofe").val(j[i].Col0);		
												$("#aperturaCuentaComentario").val(j[i].Col1);	
												$("#aperturaCuentaAutorizar").val(j[i].Col2);
												$("#envioTesofeAutorizar").val(j[i].Col3);
												$("#envioTesofeFolio").val(j[i].Col4);
												$("#envioTesofeComentario").val(j[i].Col5);
												$("#aperturaCuentaBanco").val(j[i].Col6);
												$("#aperturaCuentaClabe").val(j[i].Col7);
												$("#cIDRFC").val(j[i].Col8);
												
												var fe = j[i].Col9;
												
												if(fe != ""){
												
													fe = fe.split("-");
													fe = fe[2]+"/"+fe[1]+"/"+fe[0];
													$("#aperturaCuentaFecha").val(fe);
												}
												
												$("#aperturaCuentaTipoCuenta").val(j[i].Col10);
												$("#aperturaCuentaNumeroCuenta").val(j[i].Col11);
												$("#cnombre").val(j[i].Col12);
												$("#id_oper").val(j[i].Col13);
												$("#nFolio").val(j[i].Col14);
												//$("#nDocRenglon").val(j[i].Col15);
												
											}
						}
									
							if(datos == "datos"){
										
								parent.document.getElementById("pb_save").style.visibility='visible';
								alert("Puede Seguir con Proceso de Cancelacion");
								return;
										
							}else if(datos == "datosSinConfirmarTesofe"){
								
								parent.document.getElementById("pb_save").style.visibility='hidden';
								alert("La Cuenta Ingresada Sigue en Proceso de Apertura de Cuenta Bancaria");
								return;
								
							}else if(datos == "datosModificando"){
								
								parent.document.getElementById("pb_save").style.visibility='hidden';
								alert("La Cuenta Ingresada Esta en Proceso de Modificacion");
								return;
								
							}else{
								
								parent.document.getElementById("pb_save").style.visibility='hidden';
								alert("No se Encontro Informacion Con el Folio Ingresado ");
								return;
							}
				});
	}else{
		
		alert("Informacion Incorrecta");
		return;
	}
}

function informacionIgresada(folio){
	
	var datos = "sinDatos";
	var nFolioInt = $("#buscarFolio").val();
	nF =  nFolioInt.split("-");
	
	// Verifica si Existe en tCuentaBancariaCanEncabezado
	
	var campos = " TOP 1 aperturaCuentaComentario, (select top 1 nFolio from tCuentaBancariaEncabezado where nFolioCuentaBancaria = (select top 1 nFolioCuentaBancaria from TCUENTABANCARIACANENCABEZADO where nFolioCuentaBancariaCan = "+folio+" )) as nFolioEnc ";
	var param = " NFOLIOCUENTABANCARIACAN = "+folio+" AND activo = 1 ";
	var order = " ORDER BY nDocRenglon DESC ";
					
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"TCUENTABANCARIAENCABEZADOCANINF", Campos:campos, Param:param, Order:order, MaxReg: "1", ajax:"true" }, function(j){ 
																	
				for(var i = 0; i < j.length; i++ ){
								
						datos = "datos";
						$("#txtComentario").html(j[i].Col0);
						$("#buscarFolio").val(j[i].Col1);
						$("#tblRechazo").show();
						parent.document.getElementById("pb_save").style.visibility='visible';
				
				}
		});
}
</script>
</head>
<body id="dt_example">
	<form id="frmCuentasBancarias" name="frmCuentasBancarias" >
			<div id="container" class="container SyCData" style="width:800px; align:center">
				<h1>Solicitud de Cancelacion de Cuenta<label style="font-size: 9pt"></label></h1>
   				
   				<table id="tblGuardar">
	   				<tr>
			   				<td>id_oper_can<input type="text" name="id_oper_can" id="id_oper_can" value="<%=id_oper%>" /></td>
			   				<td>nFolio_can<input type="text" name="nFolioCan" id="nFolioCan" /></td>
			   				<td>renglon<input type="text" id="nDocRenglon" name="nDocRenglon" /></td>
			   				<td>nFolio_can<input type="text" name="FOLIOCan" id="FOLIOCan" value="<%=c.getFolio()%>" /></td>
					</tr>
					<tr>
							<td>id_oper<input type="text" name="id_oper" id="id_oper" value="" /></td>
							<td>nFolio<input type="text" name="nFolio" id="nFolio" value="" /></td>
					</tr>
					<tr>		
							<td>operador<input type="text" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" /></td>
							<td>fecha<input type="text" name="fechaToday" id="fechaToday" value="<%=today%>" /></td>
							<td>ejercicio<input type="text" name="cEjercicio" id="cEjercicio"  /></td>
							<td>cDocumento<input type="text" id="cDocumento" name="cDocumento" value="<%=c.getIdCaso()%>" /></td>
					</tr>	
					<tr>
							<td>caso<input type="text" id="caso" name="caso" value="<%=c.getCasoOperacion(0).getOperacion()%>" /></td>
							<!-- td><input type="text" value="DIRECTO" id="TO_TIPO_DOCTO"	name="TO_TIPO_DOCTO" /></td -->
							<td>centroConta<input type="text" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>" /></td>
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
							<td><input type="hidden" id="enviadoTesofeFolio" name="enviadoTesofeFolio" /></td>
							<td><input type="hidden" id="enviadoTesofeComentario" name="enviadoTesofeComentario" /></td>
					</tr>
					<tr>		
							<td><input type="hidden" id="confirmacionTesofe" name="confirmacionTesofe" /></td>
							
							<td><input  type="hidden" id="guardaTipoFirmante" name="guardaTipoFirmante" /></td>
							<td><input  type="hidden" id="guardaNombreFirmante" name="guardaNombreFirmante" /></td>
					</tr>
				</table>
				<fieldset> 
   					<table id="tblCancelacion" width="800px" height ="140px" style="text-align:center">
   							
   							<tr><td>Para Poder Iniciar su Tramite Favor de Anexar los Documentos Siguientes: </td></tr>
   							<tr><td></td></tr>
   							<tr><td>- Oficio Solicitud al Banco pidiendo la Cancelacion de la Cuenta con el Sello de Recibido </td></tr>
   							<tr><td>- Carta Confirmacion del Banco informando la Cancelacion de la Cuenta con la Fecha de Cancelacion</td></tr>
   							<tr><td>&nbsp;</td></tr>
   							<tr><td colspan="3">Cuenta a Cancelar: <input type="text" name="buscarFolio" id="buscarFolio" size="20" maxlength="50" /><input type="button" name="btnBuscar" id="btnBuscar" value="Verificar"/></td></tr>
   					</table>
   					
   				</fieldset> 	
   				<fieldset> 
   						Este Tramite se enviara a oficinas centrales para su revision y seguimiento. 
   				</fieldset> 
   				
   				<fieldset> 
   					<legend>Motivo de Rechazo Cancelacion de Cuentas</legend>
	   				<table id="tblRechazo">
	   				
	   						<!-- tr><td>Folio: <div id="txtFolio"></div></td></tr -->
	   				 		<tr><td>Comentario:<div id="txtComentario"></div></td></tr>
	   				
	   				</table>
	   			</fieldset>
	   			 
   				
 	</form>  
</body>
</html>
