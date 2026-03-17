<%@page import="java.util.List"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="com.syc.gestion.util.PaginaData"%>
<%@page import="com.syc.gestion.core.Seguimiento"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.SeguimientoBusinessLogic"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.syc.gestion.core.SeguimientoConsulta"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.ParseException"%>
<%@page import="java.util.Date"%>
<%@page import="com.syc.gestion.core.TipoCaso"%>
<%@page import="java.util.Hashtable"%>


<%!	private Logger log = Logger.getLogger(getClass());
	private String jniName = null;

	public void jspInit() {
		try {
			InitialContext ic = new InitialContext();
			jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");

			if (jniName == null) {
				jniName = "jdbc/gestion";
				log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
			} else
				log.info("dataSourceRefName=" + jniName);
		} catch (NamingException exc) {
			jniName = "jdbc/gestion";
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
		}
	}
	
	public String DateFormat(String fecha, int opc){
		String st = fecha + (opc==1? " 00:00:00": " 23:59:00");
		return st;
	}
	
	public Date Convert(String fecha, int opc){
		String st = fecha + (opc==1? " 00:00:00": " 23:59:00");
		Date date = null;
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
			date = sdf.parse(st);
		} catch (ParseException pe){
			log.info("P");
		}	
		return date;
	}

	public static String fnFormatDate(String strDate)
	{
		strDate = strDate.replaceAll("/","-");
		String strTemp[] = null;
		String strDia[] = null;
		String strHora[] = null;
		
		if (strDate.equals(""))
			return strDate;
		
		strTemp = strDate.split(" ");
		strDia = strTemp[0].split("-");
		
		if (strDate.indexOf(":") != -1)
			strHora = strTemp[1].split(":");
		
		if (strDia[0].length() != 4)
		{
			return strDate;
		}
			
		strHora[2] = strHora[2].substring(0,strHora[2].indexOf(".")); 
		
		strDate = strDia[2] + "-" + strDia[1] + "-" + strDia[0] + " " + strHora[0] + ":" + strHora[1] + ":" + strHora[2]; 
		return strDate;
	}
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%	String HeaderPaginacion = null;
	Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (u == null) {
		log.warn("No hay Usuario en sesion");
		session.invalidate();
		response.sendRedirect("../index.jsp");
		return;
	}

	session.setAttribute("gestion.call", new Boolean(true));
	
	List lSeg = new ArrayList();
	int id_tc = -1, id_caso = -1;
	boolean requestCorrect = true;
	boolean inMiGestion = "true".equals(request.getParameter("mg"));
	boolean inQuery     = "true".equals(request.getParameter("q"));
	boolean inList      = "true".equals(request.getParameter("l"));
	String  strIdTC     = request.getParameter("tc");
	String  strIdCaso   = request.getParameter("c");
    Hashtable hTipoInst = new Hashtable();
    hTipoInst.put("Procedente","Atención Procedente");
    hTipoInst.put("Conocimiento","Para su conocimiento");
    hTipoInst.put("Grupal","Atención Grupal");
    hTipoInst.put("Coordinada","Atención Coordinada");
	String  u_login     = u.getLogin();	
	String activo_turno="";
	PaginaData out_pd = null;
	PaginaData in_pd = new PaginaData();
	String busca_folio = ("folio".equals(request.getParameter("bfolio"))? "":request.getParameter("bfolio"));
	String pagina = request.getParameter("pagina");
	String param_consulta = ""; 

	SeguimientoBusinessLogic sbl = new SeguimientoBusinessLogic(jniName);
	List lTC = sbl.selectTipoCasos(u_login);
	
	if (inList) {
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		SeguimientoConsulta sc = new SeguimientoConsulta();
		
		String idTC = request.getParameter("idTC");
		String id_oper = request.getParameter("idOper");
		String fec_cre_ini = request.getParameter("fechaCreacionIni");
		String fec_cre_end = request.getParameter("fechaCreacionEnd");
	//	String fec_env_ini = request.getParameter("fechaEnvioIni");
	//	String fec_env_end = request.getParameter("fechaEnvioEnd");
		String fol_ini = request.getParameter("folioIni");
		String fol_end = request.getParameter("folioEnd");
		String resp_ejec = request.getParameter("responsableEjec");
		String resp_sigte = request.getParameter("responsableSigte");
		String referencia = request.getParameter("referencia");
		String asunto= request.getParameter("asunto");

		if ((idTC != null) && (idTC.length() > 0))
			sc.setIdTC(Integer.parseInt(idTC));

		if ((id_oper != null) && (!id_oper.equals("-1")) && (id_oper.length() > 0))
			sc.setIdOper(Integer.parseInt(id_oper));

		if ((fec_cre_ini != null) && (fec_cre_ini.length() > 0)){
			//sc.setFechaCreacionIni(new Timestamp(Convert(fec_cre_ini, 1).getTime()));
			sc.setFechaCreacionIni(DateFormat(fec_cre_ini, 1));
		}	
		
		if ((fec_cre_end != null) && (fec_cre_end.length() > 0)){
			//sc.setFechaCreacionEnd(new Timestamp(Convert(fec_cre_end, 2).getTime()));
			sc.setFechaCreacionEnd(DateFormat(fec_cre_end, 2));
		}	
		
//		if ((fec_env_ini != null) && (fec_env_ini.length() > 0)){ 
			//sc.setFechaEnvioIni(new Timestamp(Convert(fec_env_ini, 1).getTime()));
//			sc.setFechaEnvioIni(DateFormat(fec_env_ini, 1));
//		}	
		
//		if ((fec_env_end != null) && (fec_env_end.length() > 0)){ 
			//sc.setFechaEnvioEnd(new Timestamp(Convert(fec_env_end, 2).getTime()));
//			sc.setFechaEnvioEnd(DateFormat(fec_env_end, 2));
//		}	
		
		if ((fol_ini != null) && (fol_ini.length() > 0)) 
			sc.setFolioIni(fol_ini);
		
		if ((fol_end != null) && (fol_end.length() > 0)) 
			sc.setFolioEnd(fol_end);
		
		if ((resp_ejec != null) && (resp_ejec.length() > 0)) 
			sc.setResponsableEjec(resp_ejec);
		
		if ((resp_sigte != null) && (resp_sigte.length() > 0)) 
			sc.setResponsableSigte(resp_sigte);
		
		if ((referencia != null) && (referencia.length() > 0))
			sc.setReferencia(referencia);
			
		if ((asunto != null) && (asunto.length() > 0))
			sc.setAsunto(asunto);
		//if (inMiGestion)
		//	sc.setResponsableEjec(u_login);
		
		
		in_pd.setNumeroPagina(Integer.parseInt((pagina==null)? "0":pagina));
		in_pd.setTamanoPaginas(80);
		param_consulta = "seguimiento.jsp?" + (inMiGestion ? "mg=true&" : "") + "l=true"
					   + "&fechaCreacionIni=" + request.getParameter("fechaCreacionIni")
					   + "&fechaCreacionEnd=" + request.getParameter("fechaCreacionEnd")
					 //  + "&fechaEnvioIni=" + request.getParameter("fechaEnvioIni")
					 //  + "&fechaEnvioEnd=" + request.getParameter("fechaEnvioEnd")
					   + "&folioIni=" + request.getParameter("folioIni")
					   + "&folioEnd=" + request.getParameter("folioEnd")
					   + "&responsableEjec=" + request.getParameter("responsableEjec")
					   + "&responsableSigte=" + request.getParameter("responsableSigte")
					   + "&referencia=" + request.getParameter("referencia")
					   + "&asunto=" + request.getParameter("asunto");
					   
					   
		in_pd.setParamConsulta(param_consulta);
		in_pd.setBuscarFiltro(busca_folio);
	
			
		out_pd = sbl.selectAgrupado(sc, u_login, in_pd);
		lSeg = out_pd.getLista();
		out_pd.setTarget("resultado");
		HeaderPaginacion = Paginacion.getEncabezadoCSS(out_pd, in_pd).toString();
		
	} else if (inQuery == false) {
		try {
			if ((strIdTC != null) && (strIdCaso != null)) {
				id_tc = Integer.parseInt(strIdTC);
				id_caso = Integer.parseInt(strIdCaso);
			} else
				requestCorrect = false;
		} catch (NumberFormatException exc) {
			requestCorrect = false;
		}
	
		if (requestCorrect){
			
		
			out_pd = sbl.selectSeguimiento(id_tc, id_caso, (inMiGestion ? u_login : null), in_pd);
			
			lSeg = out_pd.getLista();
			//HeaderPaginacion = Paginacion.getEncabezadoCSS(out_pd, in_pd).toString();
			
		}	
	}
%>
<html>
	<head>
		<title><%=inMiGestion ? "Mi Gesti&oacute;n" : "Seguimiento"%></title>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
		<link rel="stylesheet" type="text/css" href="../css/busqueda.css" />
		<link rel="stylesheet" type="text/css" href="../css/paginacion.css" />
		
		<link rel="stylesheet" type="text/css" href="../css/dtree.css" />
		<%	if (!inList) { %>
		<link rel="stylesheet" type="text/css" href="../css/interfaz.css" />
		<%} else { %>
		<link rel="stylesheet" type="text/css" href="../css/gestion.css" />
		<link rel="stylesheet" type="text/css" href="../css/fortimax_sistema.css" />
		<%} %>
		<link rel="stylesheet" type="text/css" href="../css/scrolltable2x.css" />
		<script type="text/javascript" src="../js/datepickercontrol.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="../js/dtree.js"></script>
		<script type="text/javascript" src="../js/jquery-1.2.6.js" ></script>
		<script type="text/javascript" src="../js/jquery.tablesorter.min.js"></script>
		<script type="text/javascript" src="../js/date.js" ></script>
		<script type="text/javascript" src="../js/date-es.js" ></script>
		<Script Language=JavaScript>
			
			var n = 0;
			var currPos = 0;
			var newPos = 1;
			var isDiv = "";
			
			
			function startScroll(){
			if (newPos > currPos){
			currPos = isDiv.scrollTop;
			isDiv.scrollTop = 500;
			newPos = isDiv.scrollTop;
			startScroll();
			}
			}
			
			function toBottom(){
            /*   RICARDO				
			isDiv = document.getElementById('div1');
			isDiv.scrollTop = 0;
			startScroll();
			*/
			}
			
			window.onload=toBottom;

		</Script>
		<style type="text/css" media="screen">
			html, body {
				padding: 0;
				margin: 0;
				length: 98%;
			}
			body {
				font-family: Verdana, Arial, Tahoma, sans-serif; 
				font-size: 10pt;
			}
			
			#texto {
				font-family: Verdana, Arial, Helvetica, sans-serif;
				font-size: 12px;
				color: #666666;
				padding-top: 2px;
				padding-right: 4px;
				padding-bottom: 2px;
				padding-left: 4px;
			}
			
			div.tableContainer {
				height: 100%;
			}
		
		</style>
		
		<script type="text/javascript">
		function obtenOpciones(id_tc) {
			var sql = "SELECT id_oper, o_descripcion FROM cg_operacion WHERE id_tc = " + id_tc;
			var query = new JSQuery(onServerResponseOperacion, onServerResponseError);
			query.execute(sql);
		}
		function onServerResponseOperacion(resultSet) {
			//var arrOpc = new Array("Registro y Captura de Asuntos", "Asunto Turnado", "En espera de Acuse de Recibo");
			var cellOper = document.getElementById("cellOperacion");
			var rows = resultSet.row;
			var strHtml;
			strHtml = "<fieldset><legend>Operaci&oacute;n</legend>";
			strHtml += "<select name='idOper' id='idOper'><option value='-1'>&lt;Seleccione una Operaci&oacute;n&gt;</option>";
			for (var i = 0; i < rows.length; i++){
				//for (var iArrOpc = 0; iArrOpc < arrOpc.length; iArrOpc++) {
					//if (arrOpc[iArrOpc] == rows[i].o_descripcion) {
						strHtml += '<option value="' + rows[i].id_oper + '">' + rows[i].o_descripcion + '</option>';
					//}
				//}
			}	
		
			strHtml+='</select></fieldset>';	
			cellOper.innerHTML = strHtml;
		}
		
		function onServerResponseError(status, message) {
			window.alert(message);
		}   
		
		<%if (inList) {%>
		$(document).ready(
			function()
			{
				
				$.tablesorter.defaults.headers = { 0: { sorter: false} } 
				$.tablesorter.defaults.sortList = [[2,1]];  		
				$("#tblSort").tablesorter();
				
			}
		)
		<%}%>
		</script>
		
	</head>
	 
	<body background="../imagenes/steel_BG.gif">
	<!--div class="TituloSeguimiento">
  		<table class="TituloSeguimientoC">
    		<tr>
      			<td><img src="../imagenes/iconos/seguimiento.png" alt="" width="16" height="16"> Seguimiento</td>
    		</tr>
  		</table>
	</div-->
	<div class="Anchor" style="height: 97.8%">
<%	if (!inList) { %>	
	<div class="TituloRuta" > 
		<table class="TituloRutaC">
			<tr>
				<td>
					<img src="../imagenes/iconos/seguimiento.png" alt="" width="16"
						height="16">
					<font color="#FFFFFF">
					<strong>
						Seguimiento		
					</strong>
					</font>
				</td>
				<td>
					&nbsp;
				</td>
			</tr>
		</table>
	</div>
<%} %>	
<%	if (inQuery) {%>

		<form action="seguimiento.jsp?<%=inMiGestion ? "mg=true&" : ""%>l=true" method="post" target="resultado">
			<table align="center"   width="98%">
				<tr>
					<td align="center" height="1%">
						<table>
							<tr>
								<td>
									<%if(lTC.size() > 0) {%>
									<fieldset>
										<legend>Caso</legend>
										<table>
											<tr>
												<td>
												
													<fieldset>
														<legend>Tipo de Caso</legend>
														<select name="idTC" id="idTC" onchange="obtenOpciones(this.value)">
															<option value="-1">&lt;Seleccione un Tipo de Caso&gt;</option>
													<%	for (int i = 0; i < lTC.size(); i++) { %>
													<%		TipoCaso tc = (TipoCaso) lTC.get(i); %>
															<option value="<%=tc.getIdTC()%>"><%=tc.getDescripcion()%></option>
													<%	} %>
														</select>
													</fieldset>
									
												</td>
												<td id="cellOperacion">
													<fieldset>
														<legend>Operaci&oacute;n</legend>
														<select name="idOper" id="idOper">
															<option value="-1">&lt;Seleccione una Operaci&oacute;n&gt;</option>
														</select>
													</fieldset>
												</td>
											</tr>
										</table>
									</fieldset>
									<%} else { %>
										<input type="hidden" name="idTC" id="idTC" value="-1">
										<input type="hidden" name="idOper" id="idOper" value="-1">
									<%} %>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td  height="20%">
						<fieldset>
							<legend></legend>
							<table>
								<tr>
									<td>
										<table>
											<tr>
												<td></td>
												<td>De</td>
												<td>A</td>
											</tr>
											<tr>
												<td width="150">Fecha de Creaci&oacute;n:&nbsp;</td>
												<td width="185">
													<input name="fechaCreacionIni" id="fechaCreacionIni" type="text" datepicker_format="DD/MM/YYYY" datepicker="true" maxlength="10" size="20">
												</td>
												<td width="185">
													<input name="fechaCreacionEnd" id="fechaCreacionEnd"  type="text" datepicker_format="DD/MM/YYYY" datepicker="true" maxlength="10" size="20">
												</td>
											</tr>
										<%--<tr>
											<td></td>
												<td>De</td>
												<td>A</td>
											</tr>--%>
										<%-- 	<tr>
												<td width="150">Fecha de Modificaci&oacute;n:&nbsp;</td>
												<td width="185">
													<input name="fechaEnvioIni" id="fechaEnvioIni"  type="text" datepicker_format="DD/MM/YYYY" datepicker="true" maxlength="10" size="20">
													<!--a href="#" onclick="document.getElementById('fechaEnvioIni').value = '';return false;" class="dp-erase" title="Borra el contenido de fecha"></a-->
												</td>
												<td width="185">
													<input name="fechaEnvioEnd" id="fechaEnvioEnd"  type="text" datepicker_format="DD/MM/YYYY" datepicker="true" maxlength="10" size="20">
													<!-- a href="#" onclick="document.getElementById('fechaEnvioEnd').value = '';return false;" class="dp-erase" title="Borra el contenido de fecha"></a-->
												</td>
											</tr>  --%>
										</table>
									</td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				<tr>	
					<td  height="20%">
						<fieldset>
							<legend></legend>
							<table>
								<tr>
									<td>
										<table>
											<tr>
												<td width="150"></td>
												<td>Del</td>
												<td>Al</td>
											</tr>
											<tr>
												<td width="140">Folio:&nbsp;</td>
												<td width="184"><input name="folioIni" type="text" id="folioIni" size="30">&nbsp;</td>
										    	<td width="184"><input name="folioEnd" type="text" id="folioEnd" size="30"></td>  
											</tr>
											<tr>
												<td width="140">Referencia:&nbsp;</td>
												<td width="184"><input name="referencia" type="text" id="referencia" size="30">&nbsp;</td>
											</tr>
											<tr>
												<td width="140">Asunto:&nbsp;</td>
												<td width="184"><textarea name="asunto" COLS=40 ROWS=3 id="asunto"></TEXTAREA></td>
											</tr>
											
										<!--  	
											<tr>
												<td></td>
												<td>Envio</td>
												<td>Recepci&oacute;n</td>
											</tr>
										-->
											<tr>
												<!-- <td width="140">Usuario (login):&nbsp;</td> -->
												<td width="184"><input name="responsableEjec" type="hidden" id="responsableEjec" size="30"<%=inMiGestion ? "value=\"" + u_login + "\" readonly=\"readonly\"" : ""%>>&nbsp;</td>
												<td width="184"><input name="responsableSigte" type="hidden" id="responsableSigte" size="30"></td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="2"  height="5%"><input type="reset" name="pb_limpiar" value="Limpiar">&nbsp;<input type="submit" name="pb_query" value="Buscar"></td>
				</tr>
				<!--tr>
					<td colspan="2" height="50%">
						<iframe id="target_res" name="target_res" frameborder="1" width="100%" height="90%"></iframe>
					</td>
				</tr-->	
			</table>
		</form>

<%	} else if (inList) { %>
	<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
		<!--  tr>
			<td>
				<div class="contenido">
					<div class="digg">
						<label for="q">Folio</label>&nbsp;<input type="text" name="bfolio" value="" id="text"/><input type="button" value="" id="buscar" onclick="javascript:filtra(document.getElementById('frmInbx'),4);" onmouseover="window.status='Búsqueda por Folio';return true;"   onmouseout="window.status='';return true;" />
					</div>
				</div>
			</td>
		</tr-->
		<tr>
			<td>
				<table width="100%">
					<tr>
						<td width="05%" class="normal"><strong>Resultado de Seguimiento</strong></td>          
						<td width="05%" class="normal"><div class="Anchor"><%=HeaderPaginacion%></div></td>
						<td width="05%" class="normal"></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" height="100%" valign="top">
				<!--fieldset>
					<legend>Resultado consulta</legend-->
					
					
					<div id="tableContainer" >
					<table id="tblSort"  class="scrollTable">
						<thead class="fixedHeader" id="fixedHeader">
							<tr>
								<th class="{sorter: false}">&nbsp;</th>
								<th class="{sorter: 'text'}">Folio</th>
								<th class="{sorter: 'date'}">Fecha creaci&oacute;n</th>
						<%-- 		<th class="{sorter: 'date'}">Fecha modificaci&oacute;n</th> --%>
								<th class="{sorter: 'text'}">Asunto</th>
								<th class="{sorter: 'text'}">Estado</th>
								<th class="{sorter: 'text'}">Referencia</th>
							</tr>
						</thead>
						<tbody class="scrollContent">
				<%	if (lSeg.isEmpty()) { %>
							<tr class="alternateRow">
								<td colspan="5"><h3>No hay informaci&oacute;n para mostrar</h3></td>
							</tr>
				<%	} else { %>
					<%	for (int i = 0; i < lSeg.size(); i++) { %>
					<%		SeguimientoConsulta sc = (SeguimientoConsulta) lSeg.get(i); %>
							<tr class="<%=((i % 2) == 0 ? "AlternateRow" : "NormalRow")%>">
								<td  width="1%" align="right"><%=(i+1)%></td>								
								<td  width="15%" align="center"><a href="seguimiento.jsp?<%=inMiGestion ? "mg=true&" : ""%>tc=<%=sc.getIdTC()%>&c=<%=sc.getIdCaso()%>" target="target"><%=sc.getFolioIni().trim()%></a></td>
								<td  width="10%" nowrap><a href="seguimiento.jsp?<%=inMiGestion ? "mg=true&" : ""%>tc=<%=sc.getIdTC()%>&c=<%=sc.getIdCaso()%>" target="target"><%=sc.getFechaCreacionIni().trim()%></a></td>
								<td  width="30%"><Div id='div1' style='overflow:auto;border:solid black 1px;height:50px;width=600px;padding:10px;padding-bottom:10px;text-align:justify'><a href="seguimiento.jsp?<%=inMiGestion ? "mg=true&" : ""%>tc=<%=sc.getIdTC()%>&c=<%=sc.getIdCaso()%>" target="target"><%=(sc.getDescripcion()==null? "SIN DESCRIPCI&Oacute;N".trim():sc.getDescripcion().trim())%></a></Div></td>
								<td  width="10%" align="center"><a href="seguimiento.jsp?<%=inMiGestion ? "mg=true&" : ""%>tc=<%=sc.getIdTC()%>&c=<%=sc.getIdCaso()%>" target="target"><%=sc.getEstado().trim()%></a></td>
								<td  width="10%" align="center"><a href="seguimiento.jsp?<%=inMiGestion ? "mg=true&" : ""%>tc=<%=sc.getIdTC()%>&c=<%=sc.getIdCaso()%>" target="target"><%=sc.getReferencia().trim()%></a></td>
							</tr>
					<%	} %>
				<%	} %>
						</tbody>
					</table>
					</div>
				<!--/fieldset-->
			</td>
			<!--td width="50%" valign="top">
				<iframe id="target" name="target" frameborder="1" width="100%" height="600"></iframe>
			</td-->
		</tr>
	</table>
<%	} else { %>
<%	if (requestCorrect) { %>
	
		<table width="100%" height="99%">
			<tr>
				<td valign="top">
					<div class="" style="clear: both;	overflow: auto; border-color: #0c6d3d; order-style: solid; width: 98%; height: 100%">
						<script type="text/javascript">
						d = new dTree('d');
			<%Hashtable hTurnos = new Hashtable();
			  Hashtable hRespuestas = new Hashtable();
			  Hashtable hProrrogas = new Hashtable();
			  Hashtable hCopias = new Hashtable();
			  String asunto ="";
			  String responsable="";
			  String fechaRec="";	
			  for(int i = 0; i < lSeg.size(); i++) {
				 Seguimiento s = (Seguimiento) lSeg.get(i); 
	             if(i==0){//Pintamos la recepcion
	               if(lSeg.size()>1){ 
			          Seguimiento s2 = (Seguimiento) lSeg.get(1);
			          asunto= s2.getAsunto().replaceAll("\""," ");
			          asunto = asunto.replaceAll("'"," ");
			          asunto = asunto.replaceAll("<br />"," ");
			          fechaRec=s2.getFechaRecepcion();
			          }
			       %>
			       d.add(0,-1,'Folio <%=s.getFolio()%>','../gstnmngr/migestion?cmd=10&id_tc=<%=s.getIdTC()%>&id_caso=<%=s.getIdCaso()%>&id_gabinete=<%=s.getIdGabinete()%>&id_caso_oper=<%=s.getHijo()%>','Folio <%=s.getFolio()%>','_blank','../js/img/folder.gif','../js/img/folderopen.gif');
				   d.add(<%=100 + 1 %>,0,'Fecha Registro: <%=s.getFechaCreacion()%>');
				   d.add(<%=100 + 2 %>,0,'Referencia: <%=s.getReferencia() %>');
				   d.add(<%=100 + 3 %>,0,'Asunto: <%=asunto %>');	
				   d.add(<%=100 + 4 %>,0,'Status: <%=(s.getEstadoAsunto().equals("S")?"Cerrado":"Por Resolver")%>');
			       d.add(<%=1  %>,0,'Recepción', '','<%=s.getResponsableEjec()+" en "+s.getOperacionEjec() %>','_blank','../js/img/base.gif','../js/img/base.gif');
				   d.add(<%=101 %>,<%=1 %>,'Registro: <%=s.getRegistro()  +" Cargo: "+ s.getRegistroCargo() %>');						
				   d.add(<%=103 %>,<%=1 %>,'Fecha Recepción: <%=fechaRec%> ');
				   d.add(<%=104 %>,<%=1 %>,'Status: <%=(s.getEstadoAsunto().equals("S")?"Cerrado":"Por Resolver")%>');
				   d.add(<%=102 %>,<%=1 %>,'Remitente: <%=s.getRemitente() +" Cargo: "+ s.getRemitenteCargo() %>');	
			     <%responsable= s.getResponsableEjec();
			       }
			      else{
			           if(s.getIdOperacion()==3)continue;
			           if(s.getIdOperacion()==2){//Turno Enviado ya sea al responsable o turno normal:
			             if(s.getResponsableOperacion().equals(responsable)){//Identificamos al responsable
			                hTurnos.put(s.getResponsable(),s.getSecuencialOperacion());
			                 activo_turno = sbl.activo_turno(""+s.getResponsable(),""+s.getIdCaso(),s.getEstadoAsunto());
			                %>
			                d.add(<%=s.getSecuencialOperacion()%>,0,'Responsable', '','<%=s.getResponsableEjec()+ " en "+s.getOperacionEjec() %>','_blank','../js/img/base.gif','../js/img/base.gif');
				        	d.add(<%=201 %>,<%=s.getSecuencialOperacion()%>,'Nombre: <%=s.getResponsableEjec() +" Cargo: "+ s.getResponsableCargo() %>');
				        	d.add(<%=202 %>,<%=s.getSecuencialOperacion()%>,'Fecha Envio: <%=s.getFechaCreacion()%>');
				        	d.add(<%=203%>,<%=s.getSecuencialOperacion()%>,'Status: <%=activo_turno%>');
			             <%}
			             else{//Turno Normal
			                  if(hTurnos.containsKey(s.getIdRemitente())){
			                     activo_turno = sbl.activo_turno(""+s.getResponsable(),""+s.getIdCaso(),s.getEstadoAsunto());
			                     if(!hTurnos.containsKey(s.getResponsable()))
			                       hTurnos.put(s.getResponsable(),s.getSecuencialOperacion());%>
			                     d.add(<%=s.getSecuencialOperacion()%>,<%=hTurnos.get(s.getIdRemitente()) %>,'Turnado a ' , '','','_blank','../js/img/base.gif','../js/img/base.gif');		
								 d.add(<%=201%>,<%=s.getSecuencialOperacion()%>,'Nombre : <%=s.getResponsableOperacion()%>  ', '','<%=s.getResponsableOperacion()%>','_blank','../js/img/user_1_create.gif','../js/img/user_1_create.gif');					
 								 d.add(<%=202%>,<%=s.getSecuencialOperacion()%>,'Tipo de Instrucción: <%=/*Esteban Badillo. Fecha: 14/01/2010. Se agrega condicional para evitar nulos o valores inesperados*/ (s.getTipoInstruccion() == null || s.getTipoInstruccion().trim().equals("")) ? "" : hTipoInst.get(s.getTipoInstruccion())%>');
 								 d.add(<%=203%>,<%=s.getSecuencialOperacion()%>,'Fecha Recibido: <%=s.getFechaEnvio()%> ');
 								 d.add(<%=204%>,<%=s.getSecuencialOperacion()%>,'Status: <%=activo_turno%>');
			                  <%}
			                  else if(s.getSecuencialAnterior()==1){
			                         hTurnos.put(s.getResponsable(),s.getSecuencialOperacion());
			                         activo_turno = sbl.activo_turno(""+s.getResponsable(),""+s.getIdCaso(),s.getEstadoAsunto());%>
			                         d.add(<%=s.getSecuencialOperacion()%>,<%=1%>,'Turnado a ' , '','','_blank','../js/img/base.gif','../js/img/base.gif');		
								     d.add(<%=201%>,<%=s.getSecuencialOperacion()%>,'Nombre : <%=s.getResponsableOperacion()%>  ', '','<%=s.getResponsableOperacion()%>','_blank','../js/img/user_1_create.gif','../js/img/user_1_create.gif');					
 								 	 d.add(<%=202%>,<%=s.getSecuencialOperacion()%>,'Tipo de Instrucción: <%=hTipoInst.get(s.getTipoInstruccion())%>');
 								 	 d.add(<%=203%>,<%=s.getSecuencialOperacion()%>,'Fecha Recibido: <%=s.getFechaEnvio()%> ');
 								     d.add(<%=204%>,<%=s.getSecuencialOperacion()%>,'Status: <%=activo_turno%>');
			                   <% }
			                  else out.println("<!-- Error: No se puede colgar el turno de :" + s.getIdRemitente() + "  a:" +s.getResponsable() +" -->");  
			                 }
			             }//fin de Operacion 2
			           if(s.getIdOperacion()==4 ||s.getIdOperacion()==5 || s.getIdOperacion()==12){//Respuesta Enviada: 
			             if(s.getResponsable().equals(s.getIdRemitente()))continue;  
			             if(hTurnos.containsKey(s.getIdRemitente())){
			               String resPar = s.getRespuestaParcial();
			               resPar = resPar.replaceAll("'"," ");
			               resPar = resPar.replaceAll("\""," ");
			               resPar = resPar.replaceAll("<br />"," ");
			               hRespuestas.put(i + "" + s.getIdRemitente(),s.getSecuencialOperacion());%>
			               d.add(<%=120 + s.getSecuencialOperacion()%>,<%=hTurnos.get(s.getIdRemitente())%>,'Respuesta de ', '','','_blank','../js/img/folder.gif','../js/img/folderopen.gif');
						   d.add(<%=200 + s.getSecuencialOperacion()%>,<%=120 + s.getSecuencialOperacion()%>,'Respuesta:<%=resPar%>','','', '_blank','../js/img/page.gif','../js/img/page.gif');		   
			             <%}
			             else out.println("<!-- Error: No se puede colgar la Respuesta del turno de :" + s.getIdRemitente() + "  a:" +s.getResponsable() +" -->");
			             }//fin operacion 4,5,12
			           if(s.getIdOperacion()==10){//Respuesta Rechazada:
			             String resRec = s.getRechazoRespuesta().replaceAll("\"","");
			             resRec = resRec.replaceAll("'"," ");
			             resRec = resRec.replaceAll("<br />"," ");
			             boolean encontroRespuesta= false;
			             for(int k=i; k>=1; k--){
			                String aux= k+""+s.getResponsable();
			                if(hRespuestas.containsKey(aux)){
			                  int valor_sec_res = 200 + Integer.parseInt(""+ hRespuestas.get(aux));%>
   			                  d.add(<%=500 + s.getSecuencialOperacion()%>,<%=valor_sec_res%>,'Respuesta Rechazada ', '','','_blank','../js/img/folder.gif','../js/img/folderopen.gif');
							  d.add(<%=700%>,<%=500 + s.getSecuencialOperacion()%>,'Motivo:<%=resRec%> ', '');		
			                <%encontroRespuesta= true;
			                  break;
			                  }
			                }
			             if(encontroRespuesta==false)out.println("<!-- Error: No se puede colgar la Respuesta Rechazada de:"+ s.getIdRemitente()+ " a:"+s.getResponsable()+" -->");  
			             }//fin de Operacion 10
			           if(s.getIdOperacion()==7){
			              //Vamos a identificar si turno disfrasado o es una copia
			              String instrucc="";
			              if(s.getTipoInstruccion()!=null) instrucc=s.getTipoInstruccion(); 
			              if(s.getIdRemitente().equals(s.getResponsable())) continue;
			              String valor ="";
			              if(hTurnos.containsKey(s.getIdRemitente()))valor =""+hTurnos.get(s.getIdRemitente());
			              else{for(int k=i; k>=1; k--){
			                      String aux= k+""+s.getIdRemitente();
			                      //System.out.println("buscamos = " + aux);
			                	  if(hCopias.containsKey(aux)){
			                        valor =""+ hCopias.get(aux);
			                        break;
			                        }
			                      }
			                  }
			              if(valor.equals("") && s.getSecuencialAnterior() ==1) valor = "0";
			                  
			              if(!valor.equals("")){
			                 hCopias.put(i + "" + s.getResponsable(),s.getSecuencialOperacion());
			                 //System.out.println("copia guardada = " + i + "" + s.getResponsable());
			                 if(!instrucc.equals("Conocimiento")){%>
			                   d.add(<%=s.getSecuencialOperacion()%>,<%=valor%>,'Copiado a: ' , '','','_blank','../js/img/base.gif','../js/img/base.gif');
						       d.add(<%=100%>,<%=s.getSecuencialOperacion()%>,'Nombre : <%=s.getResponsableOperacion()%> ', '','<%=s.getResponsableOperacion()%>','_blank','../js/img/user_1_create.gif','../js/img/user_1_create.gif');
						       d.add(<%=101%>,<%=s.getSecuencialOperacion()%>,'Fecha Recibido: <%=s.getFechaEnvio()%> ');
 		     				   d.add(<%=102%>,<%=s.getSecuencialOperacion()%>,'Status : <%=(s.getEstado().equals("S")?"Cerrado":"Activo")%>');
			                 <%}
			                  else{//Es turno disfrasado de copia
			                      %>
			                       d.add(<%=s.getSecuencialOperacion()%>,<%=valor %>,'Turnado a ' , '','','_blank','../js/img/base.gif','../js/img/base.gif');		
								   d.add(<%=201%>,<%=s.getSecuencialOperacion()%>,'Nombre : <%=s.getResponsableOperacion()%>  ', '','<%=s.getResponsableOperacion()%>','_blank','../js/img/user_1_create.gif','../js/img/user_1_create.gif');					
 								   d.add(<%=202%>,<%=s.getSecuencialOperacion()%>,'Tipo de Instrucción: <%=hTipoInst.get(s.getTipoInstruccion())%>');
 								   d.add(<%=203%>,<%=s.getSecuencialOperacion()%>,'Fecha Recibido: <%=s.getFechaEnvio()%> ');
 								   d.add(<%=204%>,<%=s.getSecuencialOperacion()%>,'Status: <%=(s.getEstado().equals("S")?"Cerrado":"Activo")%>');
			                       <%} 
			                 }
			               else out.println("<!--Error: No se puede colgar la copia de :" + s.getIdRemitente() + "  a:" +s.getResponsable() +" -->");       
			             }//Fin de Operacion 7
			           if(s.getIdOperacion()==8){//Prorrogas
			             if(hTurnos.containsKey(s.getIdRemitente())){
			                hProrrogas.put(i + "" + s.getIdRemitente(),s.getSecuencialOperacion());
			               %>
			               d.add(<%=s.getSecuencialOperacion()%>,<%=hTurnos.get(s.getIdRemitente())%>,'Prorroga ', '','','_blank','../js/img/folder.gif','../js/img/folderopen.gif');
						   d.add(<%=800 + s.getSecuencialOperacion()%>,<%=s.getSecuencialOperacion()%>,'Fecha Limite    <%=s.getFechaLimite()%>','','', '_blank','../js/img/page.gif','../js/img/page.gif');
	   				       d.add(<%=900 + s.getSecuencialOperacion()%>,<%=s.getSecuencialOperacion()%>,'Fecha Solicitada    <%=s.getFechaOtorgada()%>','','', '_blank','../js/img/page.gif','../js/img/page.gif');
			               <%if(s.getRechazoRespuesta()!=null){
			                   if(!s.getRechazoRespuesta().equals("")){%>
			                     d.add(<%=1000 + s.getSecuencialOperacion()%>,<%=900 + s.getSecuencialOperacion()%>,'Rechazada','','', '_blank','../js/img/page.gif','../js/img/page.gif');
						         d.add(<%=1100 + s.getSecuencialOperacion()%>,<%=1000 + s.getSecuencialOperacion()%>,'Motivo: <%=s.getRechazoRespuesta()%>','','', '_blank','../js/img/page.gif','../js/img/page.gif');			
			                   <%}
			                   }
			               }
			              else out.print("<!-- Error: Hubo un problema al colgar la prorroga de: " + s.getIdRemitente() + " a:" + s.getResponsable()); 
			              }//fin de operacion 8
			           if(s.getIdOperacion()==11){
			             String valor=""; 
			             for(int k=i; k>=1; k--){
			                 String aux= k+""+s.getResponsable();
			                 if(hProrrogas.containsKey(aux)){
			                   valor =""+ hProrrogas.get(aux);
			                   break;
			                   }
			                }
			              if(!valor.equals("")){%>
			                 d.add(<%=1300 + s.getSecuencialOperacion()%>,<%=900 +Integer.parseInt(valor)%>,'Aceptada','','', '_blank','../js/img/page.gif','../js/img/page.gif');	         
			                 d.add(<%=1400 + s.getSecuencialOperacion()%>,<%=1300 + s.getSecuencialOperacion() %>,'Fecha Otorgada  <%=s.getFechaRecepcion()%>','','', '_blank','../js/img/page.gif','../js/img/page.gif');		
			              <%  }
			              else
			                 out.print("<!-- Error: Hubo un problema al colgar la Aceptacion de prorroga de: " + s.getIdRemitente() + " a:" + s.getResponsable()); 
			              }//Fin de operacion 11            
			          }//fin del else i!=0  
			   	 }//fin del For  
			%>
				document.write(d); 
				d.openAll();
						</script>
					</div>
				</td>
			</tr>
		</table>
		
<%	hTurnos = null; 
    hRespuestas = null;
	hProrrogas = null;
	hCopias = null;
    } //Fin del requestCorrect
   else { %>
		<br><br><center><h1><font color="red">Petici&oacute;n Inv&aacute;lida</font></h1></center>
<%	} %>
<%	} %>
	</div>
	</body>
</html>
