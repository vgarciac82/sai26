<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.*"%>
<%@page import="com.syc.gestion.core.NodeInformation"%>
<%@page import="com.syc.fortimax.core.GetDatosNodo"%>
<%@page import="com.syc.fortimax.core.DocumentoManager"%>
<%@page import="com.syc.fortimax.core.Documento"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%
	//String select = request.getParameter("select");
	boolean firmamasiva=false;
	if(request.getParameter("masiva")!=null&&"true".equals(request.getParameter("masiva")))
		firmamasiva=true;
		
	CasoBusinessLogic cbl = new CasoBusinessLogic("jdbc/gestion");
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String nodeId = request.getParameter("select");
	//System.out.println(nodeId);
	GetDatosNodo gdn = new GetDatosNodo(nodeId);
	if(!firmamasiva)
		gdn.separaDatosCarpeta();
	//Agregado para validación firmar documento PDF
	/*DocumentoManager dmanA=new DocumentoManager();
	Documento dA=dmanA.buscaDocumento("HOMO_VIATICOS",gdn.getGabinete()
	,gdn.getIdCarpeta(),Integer.parseInt(
	nodeId.substring(nodeId.lastIndexOf("D")+1)));*/
	//System.out.println(dA.getExtension());
	// AAR - Agregado para validación firmar documento PDF
	final String CARPETA = "c", IMG_DOC = "i", OPCION = "o", TIPO = "t";
	boolean isFolder = CARPETA.equals(request.getParameter(OPCION));
	boolean isImgDoc = IMG_DOC.equals(request.getParameter(TIPO));
	boolean flagFirma = false;
	if ("d".equals(request.getParameter(OPCION))&& "f".equals(request.getParameter(TIPO)))
		flagFirma = true;
	if(firmamasiva)
		flagFirma = true;
	// AAR - Agregado para validación firmar documento PDF
	//Para checar vigencia del Landed Cost en caso de que quieran subir doctos en la carpeta de Gross Margin
	boolean vigencia_landed_cost = true;
	boolean vigencia_gross_margin = true;
	boolean ya_hay_un_landed = false;
	boolean ya_hay_un_gross = false;
	boolean ya_hay_dos_en_arteyempaque = false;

	String fecha_vigencia_landed = "";//para ponersela en automatico al gross margin
	if ("ANP".equals(gdn.getGaveta())
			&& (gdn.getIdCarpeta() == 7 || gdn.getIdCarpeta() == 8)) {//carpetas de landed y gross
		DocumentoManager dman = new DocumentoManager();
		Documento d = dman.buscaDocumento("ANP", gdn.getGabinete(), 7,
				1);//EL landed cost esta en la carpeta 7
		Calendar calendar = new GregorianCalendar();
		if (d != null) {
			ya_hay_un_landed = true;
			if (d.getFh_vigencia() != null
					&& !"".equals(d.getFh_vigencia())) {
				fecha_vigencia_landed = sdf.format(d.getFh_vigencia());
				if (d.getFh_vigencia().getTime() < calendar
						.getTimeInMillis()) {
					vigencia_landed_cost = false;
				}
			}
		}
		if (gdn.getIdCarpeta() == 8) { //si esta en la carpeta del gross margin
			d = dman.buscaDocumento("ANP", gdn.getGabinete(), 8, 1);//leemos la fecha de vigencia del gross que a su vez es la del landed anterior
			if (d != null) {
				ya_hay_un_gross = true;
				if (d.getFh_vigencia() != null
						&& !"".equals(d.getFh_vigencia())) {
					if (d.getFh_vigencia().getTime() < calendar
							.getTimeInMillis()) {
						vigencia_gross_margin = false;
					}
				}
			}
		}
		if (gdn.getIdCarpeta() == 10) { //si esta en la carpeta de Arte y empaque
			ArrayList docs = new ArrayList();
			docs = cbl.getDocumentosDeCarpeta("ANP", gdn.getGabinete(),
					10);//esta carpeta debe tener maximo dos documentos
			if (docs != null && docs.size() >= 2) {
				ya_hay_dos_en_arteyempaque = true;
			}
		}
	}
	//
%>
<html>
	<head>
		<title>Crear <%=isFolder ? "Carpeta" : "Documento"%></title>
		<link rel="stylesheet" href="../css/gestion.css" type="text/css" />
		<link rel="stylesheet" type="text/css"
			href="../css/datepickercontrol_bluegray.css" />
		<script type="text/javascript" src="../js/datepickercontrol.js">
</script>
		<script type="text/javascript" src="./js/jquery-1.2.6.js">
</script>
		<script type="text/javascript" src="./js/jsquery.js">
</script>
		<script type="text/javascript" src="./js/jsquery-fetch.js">
</script>
		<script type="text/javascript" src="./js/utils/syctools.js">
</script>
		<script type="text/javascript">
function validate() {
	var n = document.getElementById("nombre");
	var d = document.getElementById("descripcion");
	//Ethiel, a peticion de vasconia se solicitara vigencia solo para este caso
<%if (!isFolder
					&& ("ANP".equals(gdn.getGaveta()) && gdn.getIdCarpeta() == 7)) {%>
				var v = document.getElementById("FECHA_VIGENCIA");
				if (v.value==""){
					alert("La vigencia es requerida");
					v.focus();
					return false;				
				}
			<%}%>
			
			if (d.value.length > 254)
				d.value = d.value.substring(0,254);
			var iniciaEspacio = false;
			if(n.value!=""){
				if(n.value.substring(0,1)==" "){iniciaEspacio=true; }
				
			}
			
			if(n.value == "") {
				alert("Por favor escribe el nombre de<%=isFolder ? " la carpeta" : "l documento"%>.");
				n.focus();
				return false;
			}else if(iniciaEspacio){
				alert("El nombre de<%=isFolder ? " la carpeta" : "l documento"%> no puede iniciar con espacios.");
				n.focus();
				return false;				
			}else if(n.value.indexOf(".")!=-1){
				alert("El nombre de<%=isFolder ? " la carpeta" : "l documento"%> no puede tener puntos.");
				n.focus();
				return false;				
			}else{
				return confirm("Desea crear <%=isFolder ? "la carpeta" : "el documento"%> con los datos proporcionados?")
			}

			return false;
		}
		//AAR validación para campos firma documento
		function validateCamposFirma() {
			var key = document.getElementById("key");
			var cer = document.getElementById("cer");
			var password = document.getElementById("password");
			if (key.value.length > 0 && cer.value.length > 0 && password.value.length > 0){
				alert("Informar todos los campos solicitados");
				return false;
			}
			if (key.value.length == 0 && cer.value.length > 0 && password.value.length > 0){
				alert("Informar campo Key.");
				return false;
			}
			if (key.value.length > 0 && cer.value.length > 0 && password.value.length == 0){
				alert("Informar campo CER");
				return false;
			}
			if (key.value.length > 0 && cer.value.length > 0 && password.value.length == 0){
				alert("Informar campo Password");
				return false;
			}
			if (key.value.length > 0 && cer.value.length > 0 && password.value.length > 0){
				return true;
			}
					
			
		}
		</script>
		<!-- AAR para subir archivos para firmar documento -->
		<script type="text/javascript">
			extArray = new Array(".gif", ".jpg");
			function LimitAttach(form, file) {
			allowSubmit = false;
			if (!file)
				return;
			while (file.indexOf("\\") != -1)
				file = file.slice(file.indexOf("\\") + 1);
			ext = file.slice(file.indexOf(".")).toLowerCase();
			for ( var i = 0; i < extArray.length; i++) {
				if (extArray[i] == ext) {
					allowSubmit = true;
					break;
				}
			}
			if (allowSubmit)
				form.submit();
			elsealert("Se permiten únicamente archivos con la extención: "
					+ (extArray.join("  ")) + "\nPor favor, seleccione otro archivo "
					+ "e intente de nuevo.");
}</script>
		<style type="text/css">
html body {
	margin: 0px;
	border: 0px;
	padding: 0px;
	overflow: hidden;
}
</style>
	</head>

	<body>
		<table width="100%" height="100%">
			<tr>
				<td>
					&nbsp;
				</td>
			</tr>
			<tr>
				<td>
					&nbsp;
				</td>
			</tr>
			<tr>
				<td align="center" valign="middle">

					<form id="forma_crea" onsubmit="return validate();"
						action="actions?select=<%=nodeId%>&fldr=<%=isFolder%>&img=<%=isImgDoc%>"
						method="post">
					

						<input type="hidden" name="docto" value="false">
						<table style="border: 1px solid navy;">

							<!--Ethiel, restricciones para subir documentos segun la carpeta y la operacion  -->
							<%
								if (!vigencia_landed_cost && "ANP".equals(gdn.getGaveta())) {
							%>
							<tr>
								<td>
									<h4>
										Vigencia vencida en Landed Cost.
										<br>
										<br><%=gdn.getIdCarpeta() == 7 ? "Debe borrar el Landed Cost vencido y después subir el nuevo."
								: "No puede agregar Gross Margin, favor de guardar y enviar para regresar el caso al Gerente de Categoría"%>
									</h4>
								</td>
							</tr>
							<%
								} else if ("ANP".equals(gdn.getGaveta()) && ya_hay_un_landed
										&& vigencia_landed_cost && gdn.getIdCarpeta() == 7) {
							%>
							<tr>
								<td>
									<h4>
										Solo se puede adjuntar un Landed Cost.
									</h4>
								</td>
							</tr>
							<%
								} else if ("ANP".equals(gdn.getGaveta()) && ya_hay_un_gross
										&& vigencia_gross_margin && gdn.getIdCarpeta() == 8) {
							%>
							<tr>
								<td>
									<h4>
										Solo se puede adjuntar un Gross Margin.
									</h4>
								</td>
							</tr>
							<%
								} else if ("ANP".equals(gdn.getGaveta()) && !vigencia_gross_margin
										&& gdn.getIdCarpeta() == 8) {
							%>
							<tr>
								<td>
									<h4>
										Debe borrar el Gross Margin y después subir el actualizado.
									</h4>
								</td>
							</tr>
							<%
								} else if ("ANP".equals(gdn.getGaveta())
										&& ya_hay_dos_en_arteyempaque && gdn.getIdCarpeta() == 10) {
							%>
							<tr>
								<td>
									<h4>
										S&oacute;lo esta permitido adjuntar 2 documentos.
									</h4>
								</td>
							</tr>
							<%
								} else if ("ANP".equals(gdn.getGaveta())
										&& (gdn.getIdCarpeta() == 0 || gdn.getIdCarpeta() == 1)) {
							%>
							<tr>
								<td>
									<h4>
										Carpeta restringida.
									</h4>
								</td>
							</tr>
							<%
								} else {
							%>
							<%
							if (!flagFirma) {
							%>
							<!-- AAR:13/10/11: Agregado para nueva validación de firma de documento -->
							<tr>
								<!--AAR:13/10/11 Modificado para incluir Firma documento-->
								<td colspan=3">
									<h4><%=isFolder ? "Crear carpeta": isImgDoc ? "Digitalizar documento": "Crear documento"%><hr>
									</h4>
								</td>
							<!--AAR:13/10/11 Modificado para incluir Firma documento-->
							</tr>
							<tr>
								<td align="right">
									Nombre:
								</td>
								<td>
									<input type="text" name="nombre" size="32" maxlength="32">
								</td>
							</tr>
							<tr>
								<td align="right">
									Descripci&oacute;n:
									<br>
									(
									<em>opcional</em>)
								</td>
								<td>
									<textarea cols="24" rows="3" name="descripcion"></textarea>
								</td>
							</tr>
							<!-- Ethiel, a peticion de vasconia se solicitara vigencia solo para este caso  -->
							<%
								if (!isFolder
												&& ("ANP".equals(gdn.getGaveta()) && gdn
														.getIdCarpeta() == 7)) {
							%>
							<tr>
								<td align="right">
									Vigencia del documento:
								</td>
								<td>
									<input name="FECHA_VIGENCIA" id="FECHA_VIGENCIA" type="text"
										readonly="readonly" datepicker_format="DD/MM/YYYY"
										datepicker="true" datepicker_min="<%=today%>" maxlength="10"
										size="12" />
								</td>
							</tr>
							<%
								}
										if (!isFolder
												&& ("ANP".equals(gdn.getGaveta()) && gdn
														.getIdCarpeta() == 8)) {
							%>
							<input name="FECHA_VIGENCIA" id="FECHA_VIGENCIA" type="hidden"
								value="<%=fecha_vigencia_landed%>" />
							<%
								}
							%>
							<tr>
								<td colspan="2">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td colspan="2" align="right">
									<input name="submit" type="submit" value="Crear">
									&nbsp;&nbsp;&nbsp;&nbsp;
									<input name="cancelar" type="button"
										onClick="javascript:parent.frames['main'].location.href='blank.jsp';"
										value="Cancelar">
								</td>
							</tr>
						</form>	
							
								<%
								} else {
											if (!isImgDoc) {
								%>
							<form method="post" name="upform" action="cgi-bin/tu-script.cgi"
								enctype="multipart/form-data">
							<tr>
								<!--AAR:13/10/11 Modificado para incluir Firma documento-->
								<td colspan=3">
									<h4>
										Firmar Documento
									</h4>
								</td>
								<!--AAR:13/10/11 Modificado para incluir Firma documento-->
							</tr>
							<tr>
								<td align="right">
									Key:
								</td>
								<td>
									<input type="file" name="uploadfile_key">
								</td>
							</tr>
							<tr>
								<td align="right">
									CER:
								</td>
								<td>
									<input type="file" name="uploadfile_cer">
								</td>
							</tr>
							<tr>
								<td align="right">
									Password:
								</td>
								<td>
									<input type="password" name="password" size="32" maxlength="32">
								</td>
							</tr>
							<tr>
								<td></td>
								<td align=left>
									&nbsp;&nbsp;&nbsp;&nbsp;
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<input type="button" name="firmar" value="Firmar">
								</td>
								
							</tr>
							<%
									}
										}
									}
								%>

							<!-- AAR:13/10/11: Agregado para nueva validación firma de documento -->
						</table>
					</form>
				</td>
			</tr>
		</table>
	</body>
</html>
