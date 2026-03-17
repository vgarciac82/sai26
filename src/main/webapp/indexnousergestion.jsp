<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String msg = (String) session.getAttribute("login.message"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
		<title>Control de Gesti&oacute;n - Fortimax</title>	 	
		<script src="./js/funciones-interfaz.js" type="text/javascript"></script>			
		<link href="./css/interfaz.css" rel="stylesheet" type="text/css"/>
		<script type="text/javascript">
		</script>
	</head>
	<!-- body background = "./imagenes/fondo-degradado-v.png" onload="if(!showButton){loginForm.submit()};if(user='null'){badUser();};" -->
	<body background = "./imagenes/fondo-degradado-v.png" onload="">
	   <div align = center>
		<div id="loginDiv">
		  <div id="logotipos">
		  	<img src="./imagenes/logotipo-usuario.png" width="150" height="42" />
		  </div>

		    <table width="100%">
			    <tr>
			        <td valign="middle"><img src="./imagenes/banner1.jpg"/></td>
			    </tr>
	       		<tr>
	        		<td valign="middle">
	        			<!-- h1 class="letrerologin">Control de Gesti&oacute;n</h1> -->
	        			<h1>Control de Gesti&oacute;n</h1>

	        			<h1><%=msg%></h1>
		      		
	      			</td>
	      		</tr>			    
		    </table>

		  <span class="pieLogin">D.R. &copy;2005-2008 S&amp;C Constructores de Sistemas S.A. de C.V.| www.fortimax.com</span>
		</div>
	  </div>
	</body>
</html>
