<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@ page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>'firma.jsp'</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

  </head>
	<link rel="stylesheet" type="text/css" href="./css/interfaz.css">
<script type="text/javascript">
  	function validaCamposLlave(btn){
  		if(
   			document.getElementById("uploadfile_key").value == ""||
  			document.getElementById("uploadfile_cer").value == ""||
  			document.getElementById("password").value == ""){
  			alert("La llave, el certificado y el password son requeridos");
  		}
  		else{
  			document.upform.submit();
  		}
  	}
</script>
  
  <body>
  	<table style="height: 500px; width: 100%;">
  		<tr>
  		<td valign="top">
		  	<form name="upform" id = "upform" action="../caso/firmardocs"  enctype = "multipart/form-data" method = "post" >
		  		<table>
					<tr>
						<td align="right">Key:</td><td><input type="file" name="uploadfile_key" id = "uploadfile_key" size="32" value=""></td>
					</tr>
					<tr>
						<td align="right">CER:</td><td><input type="file" name="uploadfile_cer" id = "uploadfile_cer" size="32" value=""></td>
					</tr>
					<tr>
						<td align="right">Password:</td><td><input type="password" name="password" id ="password" size="32" maxlength="32" value=""></td>
					</tr>
					<tr>
						<td colspan = "2" align="right">
							<input type="button" name="btnFirmar" id="btnFirmar" value = "Firmar" onclick = "validaCamposLlave();">
						</td>
					</tr>
				</table>
				<input type="hidden" name="listaCasos" id="listaCasos" value="<%=request.getParameter("listaCasos") %>">
				<input type="hidden" name="gaveta" id="gaveta" value="HOMOVIATI">
			</form>
	  	</td>
  	</table>
  </body>
</html>
