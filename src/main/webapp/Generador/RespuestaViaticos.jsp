<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="org.apache.log4j.Logger"%>
<html>
<head>
<title>Autoriza Solicitud de Viaticos</title>
<%!Logger log = Logger.getLogger("ResumenViaticos.jsp");%>
<%
	/*
		1) Exito
		2) Advertencia
		3) Error
	*/
	int tipoRespuesta = Integer.parseInt((String) ( session.getAttribute("tipoRespuesta") == null? "0" : session.getAttribute("tipoRespuesta")) );
	String mensaje = (String) session.getAttribute("mensaje");
	
	session.removeAttribute("tipoRespuesta");
	session.removeAttribute("mensaje");
	
%>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">

<style type="text/css" title="currentStyle">
@import "css/demo_page.css";

@import "css/demo_table_jui.css";

@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
</style>

<style type="text/css">
input[readonly] {
	background-color: #EEEEEE;
}
</style>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker-es.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" charset="utf-8">
	var tipoRespuesta = '<%=tipoRespuesta%>';

	$(document).ready(
		function() {
			if( tipoRespuesta == "1" )
				$("#imagen").attr("src", "../imagenes/iconos/correctoIcono.png");
			else if( tipoRespuesta == "2" )
				$("#imagen").attr("src", "../imagenes/iconos/AdvertenciaIcono.jpg");
			else if( tipoRespuesta == "3" )
				$("#imagen").attr("src", "../imagenes/iconos/errorIcono.png");
				
			$("#DialogComentarios").dialog({
				autoOpen : true,
				height : 250,
				width : 600,
				modal : true,
				buttons : {
					"Aceptar" : function() {
						$("#motivoRechazo").val($("#motivoRechazoTxt").val());
						$(this).dialog("close");
						window.close();
					}
				}
			});
		});
</script>
</head>
<body id="dt_example">
	<div id="DialogComentarios">
		<fieldset>
			<legend>Mensaje del Sistema</legend>
			<table>
				<tr>
					<td align="center" valign="center"><img id="imagen" src="" width="70" height="70" /></td>
					<td><textarea rows="5" cols="60" id="mensaje"><%=mensaje %></textarea></td>
				</tr>
			</table>
		</fieldset>
	</div>
	</body>
</html>