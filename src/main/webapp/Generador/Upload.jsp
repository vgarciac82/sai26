<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title></title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=yes">

	<script type="text/javascript" src="../Ayudas/js/jquery.js"></script>
	<script type="text/javascript" src="../JS/lenguaje.js"></script>
	<script type="text/javascript" src="../JS/Style.js"></script>

	<link href="../css/styles.css" type="text/css" rel="stylesheet">
	<link rel="shortcut icon" href="../images/favicon.ico" type="image/x-icon">

	<script type="text/javascript">
		var strImage = "";
		var strImageName = "";
		function OnInit()
		{
			if(strImage == "OK")
			{
				//opener.fnImageLoad(strImageName);
				//close();
			}
		}
		
		function fnUpload()
		{
			if ($("#cmdFile").val() == "")
			{
				alert(funGetPalabra("msgImageRequerido"));
				return false;
			}
				
			return true;
		}
	</script>
  </head>
  
  <body>
	<form id="Form1" name="Form1" action="../servlet/Upload" method="post" enctype="multipart/form-data">
		<table align="center" width="100%">
			<tr>
				<td colspan="2">
					<input id="cmdFile" name="cmdFile" type="file" maxlength="50" size="50" accept="Importar (*.jpg,*.gif,*.bmp,*.png)">
				</td>
			</tr>
			<tr>
				<td align="center">
					<input id="cmdSave" name="cmdSave" type="submit" class="buttonBase inIdioma" onclick="return fnUpload();">
				</td>
				<td align="center">
					<input id="cmdCancelar" type="button" class="buttonBase inIdioma">
				</td>
			</tr>

		</table>
	</form>

  </body>
</html>
