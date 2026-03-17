<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.commons.fileupload.FileItem"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.apache.commons.fileupload.DiskFileUpload"%>

<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.sql.Connection" %>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.CallableStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>

<%
Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
System.out.println("caso_"+c);
Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
System.out.println("usuario_"+usuario);
String mensaje = (request.getParameter("mensaje") == null) ? "sin_mensaje": request.getParameter("mensaje") ;
System.out.println("mensaje:"+mensaje);
/*c.getTipoCaso().getGavetaAsociada();
if (c == null) {
	response.sendRedirect("../index.jsp");
	return;
}*/

String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<%

 %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Subir Archivos</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="Subir Archivo">
	
	
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	
  	<script type="text/javascript">
	$(document).ready(function(){
		$("#btnLimpiar").click(function(){	limpiar(); });
		$("#btnArchivo").click(function(){	enviarArchivos(); });
		$("#tblCarga").hide();
		$("#tblAyuda").toggle(200);	
		var mensaje = $("#mensaje").val();
		if(mensaje )
		if(mensaje != "sin_mensaje"){
			alert($("#mensaje").val());
			$("#mensaje").val("sin_mensaje");
			//return;
			//location.reload();
		}
		
		
	});
			
	function enviarArchivos(tipo, archivo1){
		ext = new Array(".csv");
		
		if(!archivo1){
			alert("No se ha cargado el archivo"); 
			return;
		}else{ 
			permitida = false; 
			extension = (archivo1.substring(archivo1.lastIndexOf("."))).toLowerCase();
			if (ext[0] == extension){
				permitida = true;
			}
			if (!permitida) { 
				alert("Comprueba la extensión de los archivos a subir. \n S\u00f3lo se pueden subir archivos con extensiones: " + ext.join()); 
				$("#btnLimpiar1").click();
			}else{
				$("#btnEnviar1").attr("disabled","true");
				$("#btnEnviar2").attr("disabled","true");
				//$("#btnEnviar3").attr("disabled","true");
				$("#btnLimpiar").attr("disabled","true");
				$("#esperar").attr("style","visibility=visible"); 
				if(tipo == "btnEnviar1"){
					f = document.getElementById("flArchivo2"); 
					nodoPadre = f.parentNode; 
					nodoSiguiente = f.nextSibling; 
					nodoPadre.removeChild(f); 
					$("#tipoArchivo").val("SICOP");
					/*f = document.getElementById("flArchivo3"); 
					nodoPadre = f.parentNode; 
					nodoSiguiente = f.nextSibling; 
					nodoPadre.removeChild(f); */
				}if(tipo == "btnEnviar2"){
					f = document.getElementById("flArchivo1"); 
					nodoPadre = f.parentNode; 
					nodoSiguiente = f.nextSibling; 
					nodoPadre.removeChild(f); 
					$("#tipoArchivo").val("SIAFF");
					/*f = document.getElementById("flArchivo3"); 
					nodoPadre = f.parentNode; 
					nodoSiguiente = f.nextSibling; 
					nodoPadre.removeChild(f); */
				}/*if(tipo == "btnEnviar3"){
					f = document.getElementById("flArchivo1"); 
					nodoPadre = f.parentNode; 
					nodoSiguiente = f.nextSibling; 
					nodoPadre.removeChild(f); 
					f = document.getElementById("flArchivo2"); 
					nodoPadre = f.parentNode; 
					nodoSiguiente = f.nextSibling; 
					nodoPadre.removeChild(f); 
				}	*/		
				//Ejercutar Servlet y mandar parametro
				//var url = "../gstnmngr/SubirArchivosBase?tipoArchivo=SIAFF";
	  			//var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
				//document.location.href='../gstnmngr/SubirArchivosBase?tipoArchivo"+$("#tipoArchivo").val();
				document.getElementById("frmSubirArchivo").submit();				
			} 
		}
	}	
	</script>	
 </head>
  <body>
    <form method="post" action="../gstnmngr/SubirArchivosBase" name="frmSubirArchivo" id="frmSubirArchivo" enctype="multipart/form-data" > <!-- action="./SubirArchivosJ.jsp"-->
    	<!-- form-->
    	<input type="hidden" name="tipoArchivo" id="tipoArchivo" value=""/>
    	<div id="dvS" align="center" >
  		   	<table>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<td><input type="hidden" name="mensaje" id="mensaje" value="<%= mensaje%>"></td>
					
			</table>
			<!-- div id="cargando">Cargando...</div-->
			<label id="esperar" style="visibility: hidden">
					<div align="center">Espere por favor....
					  <img border="0" src="../imagenes/espera.gif" height="30">
					</div>
			</label>
		    <table id="tblSubirArchivo" border="1px" width="50%" height="45%" >
		    		<tr><td colspan="3" align="center"><h3>Agregar Archivo .csv</h3></td><td align="center"><!-- input type="button" id="btnAyuda" name="btnAyuda" value=" ? " onClick="$('#tblAyuda').toggle(200)"/ --></td></tr>
		    		<!-- tr><td>Archivo Sicop</td><td><input type="file" id="flArchivo1" name="'''" onchange="cambiar(this.value);" size=50/></td><td><input type="button" name="btnLimpiar" id="btnLimpiar" value="Limpiar" onClick="limpiar();"/></td></tr-->
		        	<tr><td>Archivo Sicop</td><td align="center"><input type="file" id="flArchivo1" name="flArchivo1" size=50/></td><td align="center"><input type="button" id="btnEnviar1" name="btnEnviar1" onclick="enviarArchivos('btnEnviar1',this.form.flArchivo1.value)" value="Enviar"></td><!-- td><input type="button" id="btnLimpiar1" name="btnLimpiar1" value="Limpiar" onClick="limpiar(this.name);"/></td--></tr>
		    		<tr><td>Archivo Siaff Encabezado</td><td align="center"><input type="file" id="flArchivo2" name="flArchivo2" size=50/></td><td align="center"><input type="button" id="btnEnviar2" name="btnEnviar2" onclick="enviarArchivos('btnEnviar2',this.form.flArchivo2.value)" value="Enviar"></td><!--td><input type="button" id="btnLimpiar2" name="btnLimpiar2" value="Limpiar" onClick="limpiar(this.name);"/></td--></tr>
		    		<!-- tr><td>Archivo Siaff Detalle</td><td align="center"><input type="file" id="flArchivo3" name="flArchivo3" size=50/></td><td align="center"><input type="button" id="btnEnviar3" name="btnEnviar3" onclick="enviarArchivos('btnEnviar3',this.form.flArchivo3.value)" value="Enviar" --></td><!--td><input type="button" id="btnLimpiar3" name="btnLimpiar3" value="Limpiar" onClick="limpiar(this.name);"/></td--></tr>
		    		<tr><td colspan="3" align="center"><input type="reset" id="btnLimpiar" name="btnLimpiar" value="Limpiar" onClick="Limpiar();"/></td></tr>
		    		
		     </table>
	    	<!-- table id="tblAyuda">
				<tr><td>-No se puede cambiar el orden de las columnas</td></tr>
				<tr><td>-Remplazar , por . </td></tr>
				<tr><td>-En columna AE poner RAPLACE</td></tr>		
			</table -->
	    </div>
   </form>
  </body>
</html>
