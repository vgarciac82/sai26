<%@page language="java" session="true" %>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Lista Layouts Compromisos</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
		<style type="text/css" title="currentStyle">
			@import "css/demo_page.css";
			@import "css/demo_table_jui.css";
			@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		</style>
<% 
	String msg = "";
	if(request.getParameter("mensaje") != null)
		msg = request.getParameter("mensaje"); 
%>

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
 	
	
	
	<script type="text/javascript" charset="utf-8">
		function LimitAttach(tField,iType){
			var file=tField.value;
			if(iType==1){
				var extArray = new Array(".xls");
			}

			var allowSubmit = false;

			if (!file){
				return;
			}

			while (file.indexOf("\\") != -1){
				file = file.slice(file.indexOf("\\") + 1);
			}

			var ext = file.slice(file.indexOf(".")).toLowerCase();

			for(var i = 0; i < extArray.length; i++){
				if(extArray[i] == ext){
					allowSubmit = true;
					break;
				}
			}

			if(allowSubmit){

			} else { 
				tField.value=""; 
				$("#limpiar").click();
				alert("Usted sólo puede subir archivos con extensiones " + (extArray.join(" ")) + "\nPor favor seleccione un nuevo archivo");
			}
		}

		function validarArchivo(FileSaldos, archivo1){
			//$("#cArchivo").attr('disabled', true);
			//$("#Validar1").attr('disabled', true);
			//$("#Borrar").attr('disabled', true);
			//$("#Limpia").attr('disabled', true);
			alert("Cargando Archivo");
			//$("#esperar").style.visibility="visible";
			extensiones = new Array(".xls");
			if(!archivo1){
				alert("No se ha cargado el archivo");
			}
			else{
				permitida = false; 
				extension1 = (archivo1.substring(archivo1.lastIndexOf("."))).toLowerCase();

				if (extensiones[0] == extension1){
					permitida = true;
				}
			
				if (!permitida) { 
					alert("Comprueba la extensión de los archivos a subir. \nSólo se pueden subir archivos con extensiones: " + extensiones.join()); 
					$("#limpiar").click();
				}
				
				else{
					$('#SaldosI').submit();
				}
			}
			return 0;
		}
		</script>
	</head>
	<body id="dt_example">
		<div id="container" class="container">
			<h1> Saldos Iniciales </h1>
			<div align="center">
				<form method="post" action="../gstnmngr/SaldosInicialesServlet" name="SaldosI" id="SaldosI" enctype="multipart/form-data" >
					<table cellspacing="2" cellpadding="2" border="0"> 
						<tr> 
							<td align="right"> 
								<font class="LabelSalida">Saldos Inicilales (*.xls):</font> 
							</td> 
							<td align="left">
								<input type="file" id="FileSaldos" name="FileSaldos" onblur="LimitAttach(this,1);">
							</td>
						</tr>
					</table>
				</form>
			</div>
			<div align="right">
				<table>
						<tr>
							<td>
								<input type="button" value="Cargar Archivo" id="cArchivo" name="cArchivo" onclick="javaScript:validarArchivo(SaldosI, SaldosI.FileSaldos.value);"> 
							</td>
							<td>
								<input type="hidden" id="mensaje" name="mensaje" value="<%= msg%>">
							</td>
						</tr>
					</table>
			</div>
		</div>
	</body>
</html>