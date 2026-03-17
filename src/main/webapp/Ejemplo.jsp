<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Ejemplo</title>
<script type="text/javascript">
	function abreFactura(){
		openCenteredWindow("CapturaCFD.jsp", "mensaje", 600, 700, "scrollbars=yes,resizable=yes");
	}
	
	function openCenteredWindow(url, name, height, width, parms) {
		var left = Math.floor((screen.width - width) / 2);
		var top = Math.floor((screen.height - height) / 2);
		var winParms = "top=" + top + ",left=" + left + ",height=" + height + ",width=" + width + ",scrollbar=yes,status=yes";
		if (parms) {
			winParms += "," + parms;
		}
		var win = window.open(url, name, winParms);
		if (parseInt(navigator.appVersion) >= 4) {
			win.window.focus();
		}
		return win;
	}
</script>
</head>
<body>
	....<br>
	...<br>
	Contenido Aplicacion CUSTF<br>
	...<br>
	...<br>
	Agregar este boton:<br>
	<input type="button" value="Enviar Solicitud de CFDI" onclick="abreFactura()">
</body>
</html>