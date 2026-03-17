<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>

<%
Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
if (usuario == null) {
	response.sendRedirect("../index.jsp");
	return;
}
String ur = usuario.getU_UR();
String usuLogin = usuario.getLogin();
String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();

String mensaje = (request.getParameter("mensaje") == null) ? "vacio" : request.getParameter("mensaje") ;
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
        
    <title>Cargar Archivo</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
		
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>

	<style type="text/css" title="currentStyle">
			@import "css/demo_page.css";
			@import "css/demo_table_jui.css";
			@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
	</style>
	

<script type="text/javascript">
$(document).ready(function (){
	
	var mensaje = "<%= mensaje%>";
	if(mensaje != "vacio"){ alert(mensaje);  mensaje = "vacio";  }
	
	$("#tabs").tabs({} );
	$("#tdBancomer").hide();
	$("input.AyudaSyC").subIniciaDlg();
	$("#tBancosRDB").change(function(){ 
	
			querySelectPost("cuentasBancariasRBD", "cuentaBancaria", {async: false });  
			($("#tBancosRDB").val() == "BBVA BANCOMER") ? $("#tdBancomer").show() : $("#tdBancomer").hide();
	});
	
	 $("#fecha").datepicker({
				minDate: new Date(2012, 0, 1), maxDate: "+1Y", changeMonth: true, changeYear: true, showOn:"button",
				dateFormat : "dd/mm/yy",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
				});
});

function cargarArchivo(){
	
	document.getElementById("esperar").style.visibility = 'visible';
	
	if(confirm(" ¿ Esta Seguro de Cargar Archivo ? ")){
		
		if($("#fecha").val() == ""){ alert("Falta Ingresar Fecha"); document.getElementById("esperar").style.visibility = 'hidden'; }
		if($("#tBancosRDB").val() == ""){ alert("Falta Seleccionar Banco"); document.getElementById("esperar").style.visibility = 'hidden';}
		if($("#cuentaBancaria").val() == ""){ alert("Falta Seleccionar Cuenta Bancaria"); document.getElementById("esperar").style.visibility = 'hidden'; }
		if($("#flCarga").val() == ""){ alert("Falta Seleccionar Archivo"); document.getElementById("esperar").style.visibility = 'hidden'; }
		
		$("#frmCuentasBancarias").attr("enctype","multipart/form-data");
		$("#frmCuentasBancarias").attr("action","../gstnmngr/SubirArchivoGeneralServlet");
		$("#frmCuentasBancarias").submit();
		
	}else{
		
		document.getElementById("esperar").style.visibility = 'hidden';

	}	
}
</script>	
</head>
<body id="dt_example">
	<form id="frmCuentasBancarias" name="frmCuentasBancarias" method="post">
			<div id="container" class="container SyCData" style="width:800px; align:center">
		  		
		  		<h1>Carga Archivo</h1>
		  		<input type="hidden" id="usuario" name="usuario" value="<%=usuLogin%>">
		  		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=ur%>" />
		   		<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>" />
		   		<input type="hidden" id="cBanco" name="cBanco" />
		   		<input type="hidden" id="tipo" name="tipo" value="archivosTxtRDB" />		   		
					<div id="tabs">
					
							<ul>
								<li><a href="#tabs-1">Carga de Archivo Generado Por Banco</a></li>
							</ul>
							<div align="center">
									<label id="esperar" style="visibility: hidden">	Espere por favor....
										<img border="0" src="../imagenes/espera.gif" height="30">
									</label>
							</div>
							<div id="tabs-1">
								<fieldset>
									<table height="250">
										<tr><td>&nbsp;</td></tr>
											<tr>
												<td>Fecha</td><td><input type="text" id="fecha" name="fecha" readonly='readonly' /></td>
												<td>&nbsp;</td>
											</tr>
											<tr>
												<td>Banco</td><td><input type="text" id="tBancosRDB" name="tBancosRDB" class="AyudaSyC" readonly='readonly' /></td>
												<td id="tdBancomer">
														<select id="tipoBancomer" name="tipoBancomer">
																<option value="BANCOMERCOM">BANCOMERCOM</option>
														     	<option value="BANCOMERNETCASH">BANCOMERNETCASH</option>
												        </select>
												</td>
												<td>&nbsp;</td>
											</tr>
											<tr>
												<td>Cuenta Bancaria</td><td><select id="cuentaBancaria" name="cuentaBancaria"></select></td>
												<td>&nbsp;</td>
											</tr>
											<tr>
												<td >Archivo</td><td colspan="4" align="center"><input type="file" id="flCarga" name="flCarga" size=70 /></td>
												<td colspan="4" align="center"><input type="button" id="btnCargar" name="btnCargar" onclick="cargarArchivo()" value="Cargar" />
									
											</tr>
										<tr><td>&nbsp;</td></tr>
									</table>	
								</fieldset>	
							</div>
					</div>
		    </div>
</form>
</body>
</html>
