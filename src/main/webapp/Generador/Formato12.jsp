<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>

<%
	

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Reporte Formatpo 12 Obra Publica</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3" />
<meta http-equiv="description" content="This is my page" />
<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
<style type="text/css" title="currentStyle" />
@import "css/demo_page.css"; @import "css/demo_table_jui.css"; 
@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
</style>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" charset="utf-8">
						
$(document).ready(function() {	
	$("#botonExtre").button();
	});
		
	function enviaConsulta() {
		document.ExportarForm.submit();
	}
</script>

</head>
<body id="dt_example">
<br/>
<br/>
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/Formato12" method="get">	
		<div id="container" style="width: 90%">
			
				<h1>
					Obra Publica Reporte "Formato 12"  
				</h1>
				<table align="center">
					<tr>
						<td><input id="botonExtre" name="botonExtrae" type="button" value="Generar" onclick="enviaConsulta()"/></td>
						<td>&nbsp;</td>
					</tr>			
				</table>
			
		</div>
	</form>
</body>
</html>


