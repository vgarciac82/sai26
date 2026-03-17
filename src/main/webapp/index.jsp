<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.implementacion.tesoreria.EgresosInterface"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%
	
	String msg = StringUtils.trimToEmpty((String) session.getAttribute("login.message"));
	session.removeAttribute("login.message");

	String aviso = StringUtils.trimToEmpty( (String) session.getAttribute("login.aviso") );
	session.removeAttribute("login.aviso");

	String action = StringUtils.trimToEmpty((String) session.getAttribute(GestionInterface.ATT_CMD_AUT));
	session.removeAttribute( GestionInterface.ATT_CMD_AUT );
	
	boolean logueoParaAutorizacion = false;
	logueoParaAutorizacion = action.toUpperCase( ).contains( "AUT" )  || action.toUpperCase( ).contains( "VOBO" ) || (  "FirmaReporte".equalsIgnoreCase(action) );
	
	String ul = StringUtils.trimToEmpty((String) session.getAttribute(GestionInterface.ATT_LOGIN));
	String un = StringUtils.trimToEmpty((String) session.getAttribute(EgresosInterface.USER_PRM));
	String d = StringUtils.trimToEmpty((String) session.getAttribute(EgresosInterface.DOCUMENT_PRM));
	Integer f = (Integer) session.getAttribute(EgresosInterface.FOLIO_PRM);
	Integer o = (Integer) session.getAttribute(EgresosInterface.ORDEN);

	session.removeAttribute(GestionInterface.ATT_LOGIN);
	session.removeAttribute(EgresosInterface.USER_PRM);
	session.removeAttribute(EgresosInterface.DOCUMENT_PRM);
	session.removeAttribute(EgresosInterface.FOLIO_PRM);
	
	boolean abrirDialogo = !StringUtils.isBlank( aviso );
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>SAI - Acceso al Sistema</title>
<link rel="stylesheet" type="text/css" href="./css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="./Ayudas/css/autocompleta.css" />
<link rel="stylesheet" type="text/css" href="./Generador/themes/smoothness/jquery-ui-1.8.4.custom.css" />
<link rel="stylesheet" type="text/css" href="./Generador/css/demo_table_jui.css" />
<link rel="stylesheet" type="text/css" href="./Generador/css/demo_page.css" />

<script src="./js/funciones-interfaz.js" type="text/javascript"></script>
<script type="text/javascript" src="./Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="./Generador/js/crud.js"></script>
<script type="text/javascript" src="./js/jsquery.js"></script>
<script type="text/javascript" src="./Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="./Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="./js/catalogo/general.js"></script>



<script type="text/javascript">
	var strMsg = "<%=msg%>";
	var logueoParaAutorizacion = <%=logueoParaAutorizacion%>;
	var abrirDialogo = <%=abrirDialogo%>;
	
	var  ul = "<%=ul%>";
	var  un = "<%=un%>";
	var  d =  "<%=d%>";
	var  f =  "<%=(f == null ? "" : f.toString())%>";
	var  a =  "<%=action%>";
	var  o =  "<%=o%>";
	
	$(document).ready(
		function() {
		
			if( !Number.prototype.truncate ) {
				Number.prototype.truncate = function() {
					return Math.floor(this * 100) / 100;
				}
			}
			if( top.location != document.location ) {
				top.location.href = document.location.href;
			}
			OnInit();
			
			if( abrirDialogo ){
				$("#dialog-mensaje").dialog("open");
				return;
			}
				
			
			if( logueoParaAutorizacion ){
				$("#login").val( ul );
				$("#login").attr("readonly", "readonly");
				$("#login").css("background-color", "#CCCCCC");
				$("#password").focus();
				
				$("#ul").val( ul );
				$("#un").val( un );
				$("#d").val( d );
				$("#f").val( f );
				$("#a").val( a );
				$("#o").val( o );
				$("#logueoParaAutorizacion").val(logueoParaAutorizacion);
				
		}
	});

	function OnInit() {
		try {
			$("#dialog-mensaje").dialog({
				autoOpen : false, // se juega con el true o false para que se muestre o no
				height : 250,
				width : 400,
				modal : true,
				buttons : {
					"Aceptar" : function() {
						$(this).dialog("close");
						top.location.href = "index.jsp";
					}
				}
			});
		} catch( e ) {
			window.alert("No se logro determinar el usuario de este equipo.\n\nRazon: " + e.message);
		}
	}

	function OnCambioTam() {
		
		var iAltoWindow = 0;
		var iAnchWindow = 0;
		if( 'innerHeight' in window ) { // all browsers, except IE before version 9
			iAltoWindow = window.innerHeight;
			iAnchWindow = window.innerWidth;
			
		} else { // Internet Explorer before version 9
			iAltoWindow = document.documentElement.clientHeight;
			iAnchWindow = document.documentElement.clientWidth;
		}
		
		var navegador=navigator.userAgent; //busco el "userAgent" del usuario.
		//lista de palabras del "userAgent" en los móviles
		var moviles=["Mobile","iPhone","iPod","BlackBerry","Opera Mini","Sony","MOT","Nokia","samsung"];
		var detector=0; //Variable que detectará si se usa un móvil
		for (i in moviles) { //comprobar en la lista ...
		   //si el método "indexOf" no devuelve -1, indica que la palabra está en el "userAgent"
		   compruebo=navegador.indexOf(moviles[i]); 
		   if (compruebo>-1) { 
		      detector=1; //Si es un móvil, cambio el valor del detector
		      }
		   }
		if (detector==1) { //si es un móvil redirecciono la página.
		  		document.getElementById("tblFooter").style.display = 'none';
		  		document.getElementById("indexPr").style.width = "80%";
				document.getElementById("indexPr").style.height = "100%";
		   }else  {
			if( document.getElementById("tblFooter") != null ) {
				document.getElementById("tblFooter").style.top = ( iAltoWindow - 150 ) + "px";
				document.getElementById("tblFooter").style.left = ( iAnchWindow / 2 - 400 ) + "px";
			}
			
			
			if( logueoParaAutorizacion )
				$("#password").focus();
			else
				document.getElementById("login").focus();
		}

	}

	function loggin() {
		if( document.getElementById("login").value == "" || document.getElementById("password").value == "" )
			alert("usuario y contrase\u00f1a son requeridos");
		else
			document.getElementById("fmrLogin").submit();
	}

	function openCenteredWindow(url, name, height, width, parms) {
		var left = Math.floor(( screen.width - width ) / 2);
		var top = Math.floor(( screen.height - height ) / 2);
		var winParms = "top=" + top + ",left=" + left + ",height=" + height + ",width=" + width + ",scrollbar=yes,status=yes";
		if( parms ) {
			winParms += "," + parms;
		}
		var win = window.open(url, name, winParms);
		if( parseInt(navigator.appVersion) >= 4 ) {
			win.window.focus();
		}
		return win;
	}
</script>
</head>
<!-- body background = "./imagenes/fondo-degradado-v.png" onload="if(!showButton){loginForm.submit()};if(user='null'){badUser();};"   -->
<body id="bodyf" style="font-family: Verdana,Arial,sans-serif; font-size: 1em; color:#9191A2; overflow:hidden; background-color:#F5F5F5; " 
onload="OnCambioTam();">

	<div align="center">
		<form id="fmrLogin" action="gstnmngr/login" method="post">
			<input type="hidden" id="ul" name ="ul" value="" />
			<input type="hidden" id="un" name ="un" value="" />
			<input type="hidden" id="d" name ="d" value="" />
			<input type="hidden" id="f" name ="f" value="" />
			<input type="hidden" id="a" name ="a" value="" />
			<input type="hidden" id="o" name ="o" value="" />
			<input type="hidden" id="logueoParaAutorizacion" name ="logueoParaAutorizacion" value="" />
			
			<div style="border: 1px solid #DEDEDE; padding: 1px; width:480px;  " id ="indexPr">
				<table cellpadding="0" cellspacing="0" style="border: 1px solid #DEDEDE; width:100%; font-size: .8em; background-color:#FAFAFA">
					<tr style="font-size: 1.2em; background-color:white">
						<td style="padding-top: 15px; "><img src="imagenes/CONAFOR_NvoLogoDic2013_HD_Horizontal.jpg" /></td>
						<td style="padding-top: 15px; "  style="font-size: .7em; color:gainsboro">Sistema de Administraci&oacute;n Integral <br />
							Coordinaci&oacute;n General de Administraci&oacute;n</font></td>
					</tr>
					<tr>
						<td colspan="2"><img src="imagenes/fondo-azul.png" style="width:100%; height:20px"></img></td>
					</tr>
					<tr>
						<td align="right" style="padding-top: 15px; ">Usuario:</td>
						<td align="left" style="padding-top: 15px; "><input id="login" name="login" type="text" class="campo" style="FONT-WEIGHT: bold; " size="15" maxlength="32" tabindex="1" /></td>
					</tr>
					<tr>
						<td align="right" style="padding-top: 10px; ">Contraseña:</td>
						<td align="left" style="padding-top: 10px; "><input id="password" name="password" type="password" class="campo" style="FONT-WEIGHT: bold;" size="15" maxlength="32" tabindex="2" /></td>
					</tr>
					<tr>
						<td align="right" style="padding-top: 10px; ">Ejercicio:</td>
						<td align="left" style="padding-top: 10px; ">
						<select name="ejercicio">
								<option value="2015">2015</option>
								<option value="2015">2016</option>
								<option value="2015">2017</option>
								<option value="2015" >2018</option>
								<option value="2015" >2019</option>
								<option value="2015" >2020</option>
								<option value="2015" >2021</option>
								<option value="2015" >2022</option>
								<option value="2015" >2023</option>
								<option value="2015" selected="selected">2024</option>
						</select>
						</td>
					</tr>
					<tr>
						<td colspan="2" style="padding: 30px;" align="center"><input name="aceptar" type="button" onclick="loggin();" style="width:120px;" id="aceptar" value="Ingresar" tabindex="3" /></td>
					</tr>
					<tr style="font-size: .7em;">
						<td colspan="2" align="center">Versi&oacute;n DEV.1.25_0414.0907 ( RELEASE 1.01.31)</td>
					</tr>
				</table>
			</div>

			<!---------------------- Mensajes del Sistema -------------------------->
			<br />
			<br />
			<div style="border: 1px solid #DEDEDE; padding: 1px; width:480px; font-size: .8em; color:red">
				<%=msg%>
			</div>

			<!---------------------- Footer -------------------------->
			<table id="tblFooter" width="800px" cellpadding="0" cellspacing="0" border=0 style="position: absolute; font-size: .6em">
				<tr>
					<td colspan="3">
						<div style="border: 1px solid #DEDEDE; height:1px"></div> <br />
					</td>
				</tr>
				<tr>
					<td style="width:30%" align="left"><div style="border: 1px solid #DEDEDE; height:1px"></div></td>
					<td align="center"><img src="imagenes/logo_semarnat_trans.png" width="220" height="70" /></td>
					<td style="width:30%" align="right"><div style="border: 1px solid #DEDEDE; height:1px"></div></td>
				</tr>
				<tr>
					<td colspan="3" align="center">
						<div>
							<br /> Perif&eacute;rico Poniente #5360 Col. San Juan de Ocot&aacute;n.<br /> Zapopan, Jalisco, C.P. 45019, Tel. +52 (33) 3777-7000 / 01 800 - 7370 000.<br /> <br />
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="3" align="center">
						<div>CONAFOR - Algunos derechos reservados &copy; 2015</div>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<div id="dialog-mensaje" title="Mensaje de Sistema">
		<table align="center">
			<tr>
				<td><textarea cols="65" rows="18" id="aviso" ><%=aviso%></textarea></td>
			</tr>
		</table>
	</div>
</body>
</html>
