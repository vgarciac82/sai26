<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>Menú de Administración</title>
		<link rel="stylesheet" type="text/css" href="../css/gestion.css">
		<link type="text/css" href="../css/scrolltable.css" rel="stylesheet">
		<link type="text/css" href="../css/shadow.css" rel="stylesheet">
		<style type="text/css">
		html body {
			margin: 0px;
			padding: 0px;
			border: 0px;
			background-color: #f0f0f0;
			overflow: hidden;
		}
		fieldset {
			margin: 2px;
			padding: 2px;
		}
		</style>
		<script>
			function validate(){
				var msg = "";
				var liga = "../reportespdf?";
				if(document.getElementById("persona").value!=-1 && document.getElementById("area").value != -1){
					msg += "- No es posible seleccionar usuario Y area, seleccione solo alguno de los dos.\n";					
				}
				if(document.getElementById("persona").value==-1 && document.getElementById("area").value == -1){
					msg += "- Debe seleccionar un usuario o una área.\n";					
				}
				if(document.getElementById("persona").value!=-1){
					liga += "titulo_reporte=Reporte por persona: "+document.getElementById("persona").value+"&tipo_reporte=PERSONA&area_persona="+document.getElementById("persona").value;
				}
				else if(document.getElementById("area").value!=-1){
					liga += "titulo_reporte=Reporte por área: "+document.getElementById("area").value+"&tipo_reporte=AREA&area_persona="+document.getElementById("area").value;
				}
				document.getElementById("frmReportes").action = liga;
				if(msg==""){
					document.getElementById("frmReportes").submit();
				}
				else{
					alert(msg);
				}
			}
		</script>
	</head>
	<body>		
		<%if(request.getParameter("noDatos")!=null){ %>
		<br/>
		<p align="center">
		<font color="red">No existen datos para generar este reporte, seleccione otro usuario o &aacute;rea.</font>
		</p>
		<br/>
		<%} %>
		<form action="../reportespdf" method="post" id="frmReportes">
			<table align="center">
				<tr>
					<td align="center">
						<table id="tbl-shadow">
							<tr>
								<td>
									<fieldset>
										<legend>Seleccione usuario o &aacute;rea: </legend>
										<table>
											<tr>
												<th>Usuarios:</th>
												<th>&Aacute;reas:</th>
											</tr>
											<tr>
												<td>
													<select name="persona" id="persona">
														<option value="-1" selected></option>
														<jsp:include flush="true" page="reportespdf.jsp"></jsp:include> <!-- Ethiel, esto marca error y ni se usa...  page="../reporteslistas?tipo=usuarios" -->
													</select>
												</td>
												<td><select name="area" id="area">
														<option value="-1" selected></option>
														<jsp:include flush="true" page="reportespdf.jsp"></jsp:include> <!-- Ethiel, esto marca error y ni se usa...  page="../reporteslistas?tipo=areas" -->
													</select>
												</td>
											</tr>						
										</table>
									</fieldset>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
				</tr>
				
				<tr>
					<td align="center">
						<input type="button" value="Consultar" onClick="validate();">
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>
