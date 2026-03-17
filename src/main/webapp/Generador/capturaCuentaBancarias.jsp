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
     
    <title>Captura de Cuentas Bancarias</title>
    
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
	//nFolioInt =  nFolioInt.substr(nFolioInt.length - 2);
	nF =  nFolioInt.split("-");
	$("#nFolio").val(nF[2]);
	$("#tblGuardar").hide();
	$("#tblRechazo").hide();
	informacionIgresada(nF[2]);
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
	
	//Guardar campos, id_oper(llave 1),  nFolio(llave 2), id_caso, centro contbla, unidad responsable,	usuario, fechaToday, 
	
		if(<%= id_oper == 1%>){
			
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
  		parent.document.getElementById("pb_send").disabled=true;
		var p = window.parent;
  		var valida_campos = true;
  		
  		try{
				//Validaciones a Efectuar en el boton Guardar
	
				//Guardado de los campos correspondientes a cada variable de caso
				
				p.gestion.setFolio( $("#FOLIO").val() );
				p.gestion.setOperador( $("#OPERADOR").val() );
				p.gestion.setFechaDocumento( $("#fechaToday").val() );
				p.gestion.setEjercicioFiscal( $("#cEjercicio").val() );
				p.gestion.setConceptoMov("Captura Cuentas Bancarias");
				p.gestion.setMoneda("MXP");  //el presupuesto siempre es en pesos
				p.gestion.setFechaApCont( $("#fechaToday").val() );
	
				var nretval = cmdGuardar();
				
				if(nretval == 0) {
					
					p.validaEnviar();
					
				}else{
					
					alert("No se Puedo Guardar La Informacion, Intente de Nuevo");
					return false;
				}
				
	
  		}catch(e){
				window.alert("onSubmit: Error: " + e.message);
				return false;
		}
		parent.document.getElementById("pb_send").disabled=false;
		return valida_campos;
}

function cmdGuardar(){
	
	var valor = -1;
	
	//parent.document.getElementById("pb_send").disabled=false;
	//parent.document.getElementById("pb_send").style.visibility='visible';
	//parent.document.getElementById("pb_save").disabled=true;
	//getNextSequenceVal({seqName: "CUENTAS_BANCARIAS" , async: false, callback: setSequenceValE});
	//queryFormPost("tCuentaBancariaEncabezado", {async: false });
	queryFormPost("tCuentaBancariaEncabezadoRead",{async: false });
	
	queryFormPost({
				queryName : "tCuentaBancariaEncabezado",
				async : false,
				callback : function() {
					
						alert("Guardado Correctamente");
						valor = 0;

				}
			    });
	
	return valor;
}

// Siguiente Responsable del Documento

function ResponsableSiguiente(id_oper){
		
		// Depende de O_Responsable de CG_OPERACION
  		 
		if(id_oper==1){
			
  			return "REVISION_DE_CUENTAS";
  		 
		}	
}

function OperacionSiguiente(id_oper){
		
		//Depende del campo O_Nombre CG_OPERACION
		
  		if(id_oper == 1){
  			
  			return "revision_cuentas";
  		
  		}
}

function informacionIgresada(folio){
	
	var camposWhere = " TOP 1 aperturaCuentaAutorizar, aperturaCuentaComentario ";
	var param = " nFolioCuentaBancaria = "+folio+" AND aperturaCuentaComentario != '' ";
	var order = " ORDER BY nDocRenglon DESC ";
	
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"TCUENTABANCARIAENCABEZADOINF", Campos:camposWhere, Param:param, Order:order, MaxReg: "1", ajax:"true" }, function(j){ 
													
			for(var i = 0; i < j.length; i++ ){
				
					//$("#txtFolio").val(j[i].Col0);
					$("#txtComentario").html(j[i].Col1);
					$("#tblRechazo").show();
			}
			
	});
}

/*function setSequenceValE(seqValue) 
{
		//seqValue = seqValue;
		//seqValue = seqValue.substr(seqValue.length - 6);
		//seqValue = seqValue;
		$("#nFolio").val( seqValue );
}*/
</script>
</head>
<body id="dt_example">
<div id="container" class="container SyCData">

	<form id="frmCuentasBancarias" name="frmCuentasBancarias" >
			<div id="container" class="container SyCData" style="width:800px; align:center">
			
				<h1>Solicitud de Apertura de Cuenta<label style="font-size: 9pt"></label></h1>
   					
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
							<td>caso<input type="text" id="caso" name="caso" value="<%=c.getCasoOperacion(0).getOperacion()%>" /></td>
							<!-- input type="text" value="DIRECTO" id="TO_TIPO_DOCTO" name="TO_TIPO_DOCTO" / -->
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
							<!-- input type="hidden" id="aperturaCuentaFecha" name="aperturaCuentaFecha" / -->
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
						</tr>
						<tr>	
							<td><input type="hidden" id="enviadoTesofeAutorizar" name="enviadoTesofeAutorizar" /></td>
							<td><input type="hidden" id="enviadoTesofeFolio" name="enviadoTesofeFolio" /></td>
							<td><input type="hidden" id="enviadoTesofeComentario" name="enviadoTesofeComentario" /></td>
						</tr>
						<tr>	
							<td><input type="hidden" id="confirmacionTesofe" name="confirmacionTesofe" value = "0" /></td>
						</tr>
					</table>		
   				<fieldset> 
   					<table id="tbl" width="800px" height ="160px">
   						
   							<tr><td>Para Poder Iniciar Tramite favor de anexar los documentos siguientes:</td></tr>
   							
   							<tr><td>- MEMO de Solicitud de Autorizacion de Apertura de Cuenta de Cheques</td></tr>
   							<tr><td>- Formato F1 de hacienda en PDF y RCB</td></tr>
   					</table>
   				</fieldset> 
   				<table height="30px">
   					<tr><td>Este tramite se enviara a oficinas centrales para su revision y seguimiento</td></tr>
   				</table>
   				<table>
   					<tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr>
   				</table>
   				<fieldset> 
   					<legend>Motivo de Rechazo Por Apertura</legend>
	   				<table id="tblRechazo">
	   				
	   						<!-- tr><td>Folio: <div id="txtFolio"></div></td></tr -->
	   				 		<tr><td>Comentario:<div id="txtComentario"></div></td></tr>
	   				
	   				</table>
	   			</fieldset> 
   			</div>
 	</form>  
 	</div>
</body>
</html>









