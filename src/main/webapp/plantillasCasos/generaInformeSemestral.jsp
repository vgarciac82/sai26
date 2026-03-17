<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.ArrayList"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="javax.swing.text.MaskFormatter"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>



<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Informe Semestral</title>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.core"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../js/masks.js"></script>
<script type="text/javascript">
	
	
	
	function openExcel(tiporep) {
		var url = "../gstnmngr/InformeSemestral?tipo_rep="+tiporep+
				"&ParamRep="+$("#hPrestamo").val(); // es excel con plantilla;	
		var ventimp = window.open(url, "popacuse",
				"scrollbars=1, resizable=no, width=624, height=468");

				
	}
		
	function activa_boton( ){
	var opcion = document.frm.hPrestamo.value;
	if (opcion != " "){
		document.frm.cmdExcel.disabled=false;
	} else {
		document.frm.cmdExcel.disabled=true;
	}
}


function activa_campo( ){
	var opcion = document.frm.hPrestamo.value;
	if (opcion != " "){
		document.frm.hPrestamo.disabled=false;
	} else {
		document.frm.hPrestamo.disabled=true;
	}
}


	</script>
<script language="javascript" src="js/jquery-1.2.6.min.js"></script>
<script language="javascript">
	
	
	$(document).ready(function() {
		$("input.AyudaSyC").subIniciaDlg();
		$("input.autoCompletaSyC").subIniciaAutoCompleta();


	});
</script>
</head>
<body>
	<h4>Informe Semestral</h4>
	<br>
	<br>
	<br>
	<form action="" method="post" name="frm" id="frm">
		<br>


			
		<div id="tabla2">
			<table align="center" border="0" cellpadding="" >
				<tr>
					<td>Folio Prestamo :</td>
		<td><input id="hPrestamo" name="hPrestamo" type="text" value=""
				size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC" onChange="activa_boton( )" /> <input
				id="Prestamo" name="Prestamo" type="hidden" value="" size="5"
				maxlength="5"  class="" />
			</td>			
				</tr>
				<br>
			</table>
		</div>
		
		<br>
		<br>
		<table align="center" border="0" cellpadding="" >
		<tr>
					<td align="center" valign="bottom" colspan="2"><input
						type="button" id="cmdExcel" name="cmdExcel"
						value="Generar Excel" disabled=true onclick="openExcel(1);" />
					</td>
				</tr>
				</table>
				</form>

</body>
</html>