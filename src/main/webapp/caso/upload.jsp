<%String select = request.getParameter("select");%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>Resguardo de Archivo</title>
		<link href="../css/gestion.css" rel="stylesheet">
		<style>
			html body form {
				padding: 0px;
				margin: 0px;
				border 0px;
				overflow: hidden;
			}
		</style>
		<script type="text/javascript">
		function validaInformacion(){
		
			if( document.getElementById("archivo").value == '' ){
				alert("Debe seleccionar el archivo de carga.");
				return false;
			}else{
				return true;
			}
		} 
		
		</script>
	</head>
	<body>
		<table width="100%" height="100%">
			<tr>
				<td>
					<table align="center" style="border: 1px solid navy;">
						<tr>
							<td><h4>Documento vacio<hr></h4></td>
						</tr>
						<tr>
							<td>
								<form name="upload" method="post" action="../upload?upd=true&select=<%=select%>">
									<table align="right">
										<tr>
											<td>Cambiar para</td>
											<td>
												<input name="pb_digitaliza" type="submit" id="pb_digitaliza" value="Digitalizar">
											</td>
											<td>documentos</td>
										</tr>
									</table>
								</form>
							</td>
						</tr>
						<tr>
							<td><hr></td>
						</tr>
						<tr>
							<td>
								<form name="upload" method="post" enctype="multipart/form-data" action="../upload?select=<%=select%>" onsubmit="return validaInformacion()">
									<table>
										<tr>
											<td><strong>Enviar un archivo de mi equipo</strong></td>
										</tr>
										<tr>
											<td>
												Archivo:&nbsp;
												<input name="archivo" id="archivo" type="file" size="50" maxlength="256">
											</td>
											<td>
												<input name="pb_send" type="submit" id="pb_send" value="Enviar">&nbsp;&nbsp;&nbsp;&nbsp;<input name="cancelar" type="button" onClick="javascript:parent.frames['main'].location.href='blank.jsp';" value="Cancelar">
											</td>
										</tr>
									</table>
								</form>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>
	</body>
</html>
