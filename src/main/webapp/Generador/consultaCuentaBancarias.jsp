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
   
    <title>Consulta  Cuenta Bancarias</title>
    
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
	
	var nFolioInt = "<%=c.getFolio()%>";
	nF =  nFolioInt.split("-");
	$("#nFolio").val(nF[2]);
	informacionIgresada(nF[0],nF[2]);
	$("#tblGuardar").hide();
	
	var tableCuentas = $('#tableCuentas').dataTable({
					"iDisplayLength": 20,
        			"bPaginate": false,
        			"bFilter": false,
        			"bSort": false,
        			"bJQueryUI": true,
        			"bInfo": false 
    });
    
 });

function onPostDisplay(){
	
		parent.document.getElementById("pb_send").disabled = false;
		parent.document.getElementById("pb_send").click();
		
}

function onPostSubmit(id_oper){
  		
  		return true;
}

function onLoadPlantilla(){
		
	// Consulta no entra	
	if(<%=id_oper == 8 %>){ 
			
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=true;
			
		}
	parent.document.getElementById("pb_cancel").style.visibility='hidden';
	parent.document.getElementById("pb_cancel").disabled=true;
}

function onSubmit(id_oper){
	
		var p = window.parent;
  		var valida_campos = true;
	
		try{
				p.gestion.setFolio( $("#FOLIO").val() );
				p.gestion.setOperador( $("#OPERADOR").val() );
				p.gestion.setFechaDocumento( $("#fechaToday").val() );
				p.gestion.setEjercicioFiscal( $("#cEjercicio").val() );
				p.gestion.setConceptoMov("Apertura Cuentas Bancarias");
				p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
				p.gestion.setFechaApCont( $("#fechaToday").val() );
	
		}catch(e){
				window.alert("onSubmit: Error: " + e.message);
				return false;
		}

		return valida_campos;
}

function ResponsableSiguiente(id_oper){
		
		// Depende de O_Responsable de CG_OPERACION
  		/* if(id_oper == 9 ){
  			 
  			return "CONSULTA_CUENTA_BAN";
  		 
  		 }*/
}

function OperacionSiguiente(id_oper){
		
  		/*if(id_oper == 9 ){
  			
  			return "consulta_cuenta";
  		
  		}*/
}

function informacionIgresada(tipoDoc, folio){
	
	if(tipoDoc == "MOCB"){
		
		$("#titulo").html("Consulta Modificacion de Cuenta Bancaria");
		queryFormPost({
						queryName : "folioCuentaBancariaMod",
						async : false,
						callback : function() {
								
									folio = $("#nFolioCuentaBancaria").val();
						}		
		});
	}
	if(tipoDoc == "BACB"){
		
		$("#titulo").html("Consulta Cancelacion de Cuenta Bancaria");
		queryFormPost({
						queryName : "folioCuentaBancariaCan",
						async : false,
						callback : function() {
								
									folio = $("#nFolioCuentaBancaria").val();
						}		
		});
		
	}else{
		
		$("#titulo").html("Consulta Apertura Cuenta Bancaria");
	}
	
	var campos = " TOP 1 aperturaCuentaBanco, aperturaCuentaClabe, aperturaCuentaRfc, aperturaCuentaFecha, aperturaCuentaTipoCuenta, aperturaCuentaNumeroCuenta, aperturaCuentaBeneficiario  "
	var param = " NFOLIOCUENTABANCARIA = "+folio ;
	var order = " ORDER BY nDocRenglon DESC ";
	
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"TCUENTABANCARIAENCABEZADOINF", Campos:campos, Param:param, Order:order, MaxReg: "1", ajax:"true" }, function(j){ 
													
			for(var i = 0; i < j.length; i++ ){ 
				
					$("#aperturaCuentaBanco").val(j[i].Col0);
					$("#aperturaCuentaClabe").val(j[i].Col1);
					$("#cIDRFC").val(j[i].Col2);
					
					var fe = j[i].Col3;
					if(fe != ""){
					
						fe = fe.split("-");
						fe = fe[2]+"/"+fe[1]+"/"+fe[0];
						$("#aperturaCuentaFecha").val(fe);
					}
					
					$("#aperturaCuentaTipoCuenta").val(j[i].Col4);
					$("#aperturaCuentaNumeroCuenta").val(j[i].Col5);
					$("#cnombre").val(j[i].Col6);
			}
			
			// Detalles
			var camposDet = " nFolioCuentaBancaria, tipoFirmante, nombreFirmante ";
			var paramDet = " NFOLIOCUENTABANCARIA = "+folio+" AND activo = 1";
			var orderDet = "";
			
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"TCUENTABANCARIAENCABEZADOINFDET", Campos:camposDet, Param:paramDet, Order:orderDet, MaxReg: "1", ajax:"true" }, function(j){ 
	
					for(var i = 0; i < j.length; i++ ){
			
						$("#tableCuentas").dataTable().fnAddData( [ j[i].Col0, j[i].Col1, j[i].Col2 ]);
						
					}
			});
			
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
						<td>foo<input type="text" id="nFolioCuentaBancaria" name="nFolioCuentaBancaria" /></td>
					</tr>
				</table>
				<fieldset> 
	   				<table id="tblEstatus" height ="150px">
						<tr>
							<td>Nombre de Banco</td><td><input type="text" name="aperturaCuentaBanco" id="aperturaCuentaBanco" size="45" maxlength="50" disabled="disabled" /></td>
							<td>Tipo de Cuenta</td><td><input type="text" name="aperturaCuentaTipoCuenta" id="aperturaCuentaTipoCuenta" size="45" maxlength="20" class="valida" disabled="disabled" /></td>
						</tr>
						<tr>
							<td>CLABE</td><td><input type="text" name="aperturaCuentaClabe" id="aperturaCuentaClabe" size=45 maxlength="20" disabled="disabled" /></td>
							<td>Numero de Cuenta</td><td><input type="text" name="aperturaCuentaNumeroCuenta" id="aperturaCuentaNumeroCuenta" size=45 maxlength="20" disabled="disabled"/></td>
						</tr>
						<tr>
							<td>RFC</td><td><input type="text" name="aperturaCuentaRfc" id="cIDRFC" size="25" maxlength="20" disabled="disabled" /></td>
							<td>Fecha Apertura</td><td><input type="text" name="aperturaCuentaFecha" id="aperturaCuentaFecha" size=15 disabled="disabled" /></td>
						</tr>
						<tr>
							<td>Beneficiario</td><td colspan="4"><input type="text" name="aperturaCuentaBeneficiario" id="cnombre" size=73 disabled="disabled" /></td>
						</tr>
						
					</table>
				</fieldset> 
				<tr><td>&nbsp;</td></tr>
   				
   				<table>
	   				<tr>
	   					<table id="tableCuentas" class="display" >
									
								<thead>
									<tr align="center">
										<th>Folio</th>	
										<th>Tipo Firmante</th>	
										<th>Nombre Firmante</th>
									</tr>
								</thead>							
								
						</table>
	  				</tr>
				</table>
   			</div>
 	</form>  
</body>
				
</html>
