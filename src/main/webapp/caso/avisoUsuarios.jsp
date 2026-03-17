<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}		
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>Configuraci&oacute;n de avisos para usuarios</title>
<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>		
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>

<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

<script>
$(document).ready(function() {
	$("#Buscar").button();
	$("#cParametro").val("nMensajeUsuarios");
	$("input.AyudaSyC").subIniciaDlg();	//Para mostrar Ayudas
	$("#guardar").button();
	$("#cancelar").button();
<%
if(request.getParameter("aviso")== null){
%>
	queryFormPost("mensajeAvisoUsuarios", {async : false});
<%
}
%>
	var mensajeUsuarios=document.getElementById('cValor').value.replace("%0A","\n");
	mensajeUsuarios = decodeURIComponent(mensajeUsuarios);
	document.getElementById('cValor').value = mensajeUsuarios; 
});	
function guardarCambiosMensaje(){

	if(document.getElementById("txtGridaEjercicioFiscal").value==""){
		alert("Es necesario elegir el Ejercicio Fiscal");
		return;
	}
	
	if(confirm("¿Estas seguro de guardar los cambios?")){
	
	
		document.getElementById('cValor').value=encodeURIComponent(document.getElementById('cValor').value);
		
		
	//	queryFormPost("actualizaMensajeUsuarios", {async : false});
		var mensajeUsuarios=document.getElementById('cValor').value.replace("%0A","\n");
		mensajeUsuarios = decodeURIComponent(mensajeUsuarios);
		document.getElementById('cValor').value = mensajeUsuarios; 
		
		document.getElementById("avisos").action = "<%=request.getContextPath()%>/avisoUsuarios";
		document.getElementById("avisos").submit();
	}
}

function buscarAviso(){


	if(document.getElementById("txtGridaEjercicioFiscal").value==""){
		alert("Es necesario elegir el Ejercicio Fiscal");
		return;
	}
	
// 	if(document.getElementById("txtGridaEjercicioFiscal").value=="*"){
// 		alert("Es necesario elegir el año en el Ejercicio Fiscal");
// 		return;
// 	}
	
	document.getElementById("avisos").action = "<%=request.getContextPath()%>/avisoUsuarios?buscaranio=1";
	document.getElementById("avisos").submit();

}


<%
if(request.getParameter("msg")!=null && request.getParameter("msg").equals("1")){
%>
alert("Se han guardado los cambios");
<%
}else if(request.getParameter("msg")!=null && request.getParameter("msg").equals("0")){
%>
alert("Hubo un error al guardar los cambios");

<%
}
%>
</script>  
<body>
<br/>
<center>
<form id="avisos" action="" method="POST">
	<div id="container" class="container" style="width: 50%">
		<div class="card-header"> <h3> Avisos </h3> </div>
		<hr class="mt-3"/>
		
		<div class="row">
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
			</div>
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">										
				<label for="txtGridaEjercicioFiscal" class="form-label"> Ejercicio Fiscal: </label>
			</div>
			<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
				<input type="text" id="txtGridaEjercicioFiscal" name="txtGridaEjercicioFiscal" size=30 class="form-control form-control-sm AyudaSyC" readonly/>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" id="Buscar" name="Buscar" value="Buscar Aviso" onclick="buscarAviso();" class="btn-secondary btn-sm"/>
			</div>				
		</div>
		
		<div class="row">
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
			</div>
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">										
				<label for="cValor" class="form-label"> Descrip&oacute;n: </label>
			</div>
			<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
				<input type="hidden" name="cParametro" id="cParametro">
				<textarea name="cValor" id="cValor" cols="70" rows="6" class="form-control form-control-sm"><%if(request.getParameter("aviso")!= null){%><%=request.getParameter("aviso") %><%}%></textarea>
			</div>				
		</div>		
		
	</div>
	
	<div class="row">
		<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
		</div>
		<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">
			<input type="button" name="guardar" id="guardar" class="btn-secondary btn-sm" onClick="/*validarGuardarAviso(document.getElementById('aviso').value);*/guardarCambiosMensaje();" value = "Guardar" class="btnInterfaceBG"/>
			&nbsp;&nbsp;&nbsp;
			<input type="button" name="cancelar" id="cancelar" class="btn-secondary btn-sm" onClick="document.getElementById('cValor').value='';" value = "Cancelar" class="btnInterfaceBG"/>
		</div>				
	</div>
</form> 

</center>    
</body>
</html>
