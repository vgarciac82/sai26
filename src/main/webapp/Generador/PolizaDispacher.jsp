<%@page import="com.syc.sai.contabilidad.polizamanual.controller.DocPolizaEncabezadoBusinessLogic"%>
<%@page import="com.syc.sai.contabilidad.polizamanual.model.DocPolizaEncabezadoManager"%>
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.syc.sai.contabilidad.polizamanual.*"%>
<%@page import="org.apache.log4j.Logger"%>

<%!private final int POLIZA_POR_CUENTAS = 1;%>
<%!private final int POLIZA_POR_EVENTOS = 2;%>
<%!private Logger log = Logger.getLogger(getClass());%>
<%
	if (request.getMethod().equals("POST")) {

		int option = new Integer(request.getParameter("formato"))
				.intValue();

		switch (option) {
		case POLIZA_POR_CUENTAS:
			response.sendRedirect("Polizas.jsp?folio="
					+ request.getParameter("folio"));
			break;

		case POLIZA_POR_EVENTOS:
			response.sendRedirect("PolizasEvento.jsp?folio="
					+ request.getParameter("folio"));
			break;
		}
	}

	boolean cPolizaManualCuenta = false;

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);

	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper"))
				.intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();

	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CPOLIZAMANUALCUENTA")) 
	{
		cPolizaManualCuenta = usuario.getPropiedad("CPOLIZAMANUALCUENTA").getValor().contentEquals("SI");
	}

	try 
	{
		Connection conn = cbl.getConnection();
		CasoDato cd = new CasoDato();
		cd.setIdCaso(c.getIdCaso());
		c.setCasoDato(CasoDatoManager.select(conn, cd));
		conn.close();
		conn = null;
	} catch (Exception expropcte)
	{
		log.error("Error leyendo CasoDato: ", expropcte);
	}

	String folio = null;
	
	
	if (c.getCasoDato("FOLIO") != null) 
	{
		folio = c.getFolio();
	}
	
	
	Integer nFolioDocPoliza=null;
	
	if(!cPolizaManualCuenta && folio==null)
	{
		response.sendRedirect("PolizasEvento.jsp?folio="+ request.getParameter("folio"));
		
	}
	
	
	
	else if (folio != null) 	
	{
		nFolioDocPoliza = new Integer(folio.split("-")[2]);
		String restrictions = "nFolioDocPoliza=" + nFolioDocPoliza;
		
	  List<DocPolizaEncabezado> l = DocPolizaEncabezadoManager.readDocPolizaEncabezadoBy(new DocPolizaEncabezadoBusinessLogic().getConnection(), restrictions);

		if (id_oper != 1 || !l.isEmpty()) {
			DocPolizaEncabezado dpe = l.get(0);
			if (dpe.getnFormatoPoliza() != null) {
				int option = dpe.getnFormatoPoliza();
				
				switch (option) {
				
				case POLIZA_POR_CUENTAS:
					try {
						if (cPolizaManualCuenta || (usuario.getGrupo("REVISION_POLIZA") != null && id_oper==2) || (usuario.getGrupo("AUTORIZA_POLIZA") != null && id_oper==3 ))
							response.sendRedirect("Polizas.jsp?folio="+nFolioDocPoliza);
						//else
							//response.sendRedirect("../gstnmngr/gestion?cmd=1");
						//response.sendRedirect("../index.jsp");
					} catch (Exception f) {
						System.out.println("ERROR " + f);
					}
					break;

				case POLIZA_POR_EVENTOS:

					try {
						response.sendRedirect("PolizasEvento.jsp?folio="+nFolioDocPoliza);
					} catch (Exception f) {
						System.out.println("ERROR " + f);
					}

					break;
				}
			}
		}	
		
	
	else if(!cPolizaManualCuenta)
	{
		response.sendRedirect("PolizasEvento.jsp?folio="+nFolioDocPoliza);		
	}
		
		

	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>P&oacute;lizas</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="-1">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
		
		<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>		
		<style>
			.ui-autocomplete {
				max-height: 100px;
				overflow-y: auto;
				/* prevent horizontal scrollbar */
				overflow-x: hidden;
			}
			
			/* IE 6 doesn't support max-height
				     * we use height instead, but this forces the menu to always be this tall
				     */
			* html .ui-autocomplete {
				height: 100px;
			}
		</style>

		<style type="text/css" title="currentStyle">
@import "css/demo_page.css";

@import "css/demo_table_jui.css";

@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
</style>

		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="js/jquery.maskedinput.min.js" ></script>
		
		<script type="text/javascript">
		
	function onLoadPlantilla()
	{
		
		var cPolizaManualCuenta = <%=cPolizaManualCuenta%>;
		
		
		//document.getElementById("container").style.visibility = "hidden";
			
		//if(!cPolizaManualCuenta)
		 //{			
			parent.document.getElementById("pb_cancel").style.display = 'none';
			parent.document.getElementById("pb_send").style.display = 'none';
			parent.document.getElementById("pb_save").style.display = 'none';

			parent.document.getElementById("pb_leave").style.display = '';
			
		 	//alert("Usted no cuenta con los permisos necesarios para acceder a una Póliza por Cuentas");
		 	//document.redireccionar.submit();
		 	
		 //}
		//else
		document.getElementById("container").style.visibility = "visible";
		
		
	}
	
		
			
		</script>
		

	</head>
	
	<body id="dt_example">
			
		<div id="container" class="container"  >
		
			<h1>
				P&oacute;lizas
			</h1>
			<center>
			<fieldset style="width:500px">
				<legend>Seleccione un formato de p&oacute;liza</legend>
				<form action="PolizaDispacher.jsp" method="post">
					<center>
						<table>
							<tr>
								<td><br>Formato de p&oacute;liza : </td>
								<td>
									<br>
									<select id="formato" name="formato" style="width:200px">
										<option value="1">Por cuentas</option>
										<option value="2">Por eventos</option>
									</select>
								</td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td>
									<br>
									<input type="hidden" id="folioCaso" name="folio" value="<%=request.getParameter("folio")%>"/>
									<input type="submit" value="Aceptar" style="height:30px;width:100px"/>
								</td>
							</tr>			
						</table>
					</center>
				</form>
			</fieldset>
			</center>
		</div>
		
	<form id="redireccionar" name="redireccionar"  action="../gstnmngr/gestion?cmd=1" target="content-iframe"	method="post"></form>
	
	</body>
</html>
