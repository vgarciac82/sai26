<%@ page language="java" contentType="text/html; charset=ISO-8859-1" session="true" pageEncoding="ISO-8859-1"%>
<%
	String bIsLDAP = getServletContext().getInitParameter("LDAPOn");
	//System.out.println("bIsLDAP=["+bIsLDAP+"]");

	boolean isLDAP = (bIsLDAP==null) ? false : ("true".equals(bIsLDAP)) ? true : false;

	String msg = (String) session.getAttribute("login.message");
	String remoteUser = request.getRemoteUser();
	if (remoteUser != null) {
		int specialChar = remoteUser.indexOf('\\')+1;
		remoteUser = remoteUser.substring(specialChar);
	}
	//System.out.println("index remoteUser=["+remoteUser+"]");

	String showButton = (String) request.getParameter("showbutton");
	boolean bShowButton = false;
	if (showButton != null) {
		bShowButton = "true".equals(showButton);
	}
	//System.out.println("index.jsp --> showButton=["+showButton+"], bShowButton=["+bShowButton+"]");
	session.setAttribute("cluster","replica.session");
	session.removeAttribute("cluster");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
		<title>SAI - Acceso al Sistema</title>
		<script src="./js/funciones-interfaz.js" type="text/javascript"></script>
		<link href="./css/interfaz.css" rel="stylesheet" type="text/css"/>
		<script type="text/javascript">
		var showButton = <%=bShowButton%>;
		var user = <%=remoteUser%>;
		var strMsg = "<%=msg%>";

		if (top.location != document.location) {
		    top.location.href = document.location.href;
		}

		function badUser() {
			alert("El usuario no ha sido adecuadamente autentificado por el Directorio Activo (LDAP)\n"
				 +"en el Servidor de Aplicaciones (Internet Information Server).\n"
				 +"Favor de establecer crevolverlo a intentar");
		}

	    function OnInit() {
			try {
				var wsh = new ActiveXObject('WScript.Shell');
				var usuario = wsh.ExpandEnvironmentStrings('%USERNAME%');
				document.getElementById("login").value = usuario;
				if (strMsg == "null")
					fmrLogin.submit();
				else
					document.getElementById("password").value = "qwertyuiop"
			} catch(e) {
				window.alert("No se logró determinar el usuario de este equipo.\n\nRazón: " + e.message);
			}
	    }
	    function OnCambioTam()
	    {
	    	//spPiePage.style.top = document.body.clientHeight - 25;
	    	//spPiePage2.style.top = document.body.clientHeight - 12;
	    	document.getElementById("login").focus();
	    }
		function loggin(){
			if(document.getElementById("login").value==""||document.getElementById("password").value=="")
				alert("usuario y contraseña son requeridos");
			else
				document.getElementById("fmrLogin").submit();
		}
		</script>
	</head>
	<!-- body background = "./imagenes/fondo-degradado-v.png" onload="if(!showButton){loginForm.submit()};if(user='null'){badUser();};" -->
	<body scroll="no" background = "./imagenes/fondo-degradado-v.png" onload="<%=(isLDAP)?"OnInit();OnCambioTam();":"OnCambioTam();"%>" onresize="OnCambioTam()">
	   
	   <table border="0" width="100%" cellpadding="0" cellspacing="0" style="BORDER-BOTTOM: solid 1px silver; background-image:url('imagenes/bckgnd-header.png');">
			<tr>
				<td width="216px" height="50px"> <img src="imagenes/logotipo-sistema.png" /></td>
				<td align="center" style="FONT-SIZE: 12pt; Color:gray"> Acceso al Sistema

				</td>
				<td width="150px" height="40" align="right"> <img src="imagenes/logotipo-usuario.gif" /></td>
			</tr>
		</table>					
	   <div align="center">
		<form id="fmrLogin" action="gstnmngr/login" method="post">
			<input name="ldap" type="hidden" id="ldap" value="<%=isLDAP%>"/>
			<input name="ldapuser" type="hidden" id="ldap" value="<%=remoteUser%>"/>
		    <table border="0">
	       		<tr>
	        		<td valign="middle" style="COLOR: white; BORDER: solid 1px silver;">
	        			</br>
	        			Introduzca su clave de Usuario y Contraseña para </br>verificar que perfil cumple para trabajar en el sistema.</br></br>
	        			<!-- h1 class="letrerologin">Homologaci&oacute;n de vi&aacute;ticos</h1> -->
	        			<% if (!isLDAP) {%>
		          	 		<div style="FONT-WEIGHT: bold;">Usuario:
			            		<input name="login" type="text" class="campo" id="login" style="margin-left:33px; FONT-WEIGHT: bold;" size="15" maxlength="32" tabindex="1"/>
					 		</div>
	   				 		<div style="FONT-WEIGHT: bold;">Contrase&ntilde;a:
	     						<input name="password" type="password" class="campo" id="password" style="margin-left:10px; FONT-WEIGHT: bold;" size="15" maxlength="32" tabindex="2"/>
	   						</div>
	   					<%}%>
						<%=(msg!=null?msg+"<br></br>":"&nbsp;") %>
						</br></br>
						<input name="aceptar" type="button"  onclick="loggin();" style="width:120px;" id="aceptar" value="Ingresar" tabindex="3"/>
						</br></br>
	      			</td>
	      		</tr>
			    <tr>
			        <td valign="middle"></br></br><img src="./imagenes/portada-conagua.JPG"/><br></br>&nbsp;</td>
			    </tr>
		    </table>
		  </form>
	  </div>
	  <!-- 
	  <span id="spPiePage" class="pieLogin" style="POSITION: absolute;  WIDTH: 100%;">D.R. &copy;2005-2012 S&amp;C Constructores de Sistemas S.A. de C.V.</span>
	  <span id="spPiePage2" class="pieLogin" style="POSITION: absolute;  WIDTH: 100%;">Advertencia: Este programa esta protegido por leyes y tratados internacionales de Derechos de Autor. La reproducción o distribución no autorizada total o cualquiera de sus partes, es un delito y es penalizado por la ley.</span>
	   -->
	</body>
</html>
