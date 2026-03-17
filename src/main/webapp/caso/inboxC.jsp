<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="java.util.*,java.sql.*,com.syc.gestion.*,com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.Util" %>
<%@page import="java.util.*,com.syc.gestion.*,com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.Util" %>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.syc.gestion.documental.Documental"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="com.syc.gestion.util.PaginaData"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="org.slf4j.LoggerFactory"%>
<html>
<%!private Logger log = LoggerFactory.getLogger(getClass());
	private String jniName = null;

	public void jspInit() {
		try {
			InitialContext ic = new InitialContext();
			jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");

			if (jniName == null) {
				jniName = GestionInterface.ATT_CONEXION;
				log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
			} else
				log.info("dataSourceRefName=" + jniName);
		} catch (NamingException exc) {
			jniName = "jdbc/gestion";
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
		}
	}%>
<%
	int[] totales = { 0, 0, 0, 0 };
	String dateFormat = "dd-MMM-yyyy HH:mm:ss";
	String msg = request.getParameter(GestionInterface.PRM_USER_MSG);
	String gavetaAsociada = request.getParameter("gavetaAsociada"); //brenda
	String noFolio = request.getParameter("noFolio");
	String fDesde = request.getParameter("fDesde");
	String fHasta = request.getParameter("fHasta");
	String documento = request.getParameter("documento");
	String operador = request.getParameter("operador");
	String estatus = request.getParameter("estatus");
	String importe = request.getParameter("importe");
	String folioSICOP = request.getParameter("folioSICOP");
	String folioMAP = request.getParameter("folioMAP");
	String folioCAL = request.getParameter("folioCAL");
	Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (u == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	CasoBusinessLogic ct = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	
	CasoOperacionBusinessLogic cobl = new CasoOperacionBusinessLogic(GestionInterface.ATT_CONEXION);

	int promFilter = -1;
	String strPromFltr = request.getParameter(GestionInterface.PRM_PROM_FILTER);
	if (strPromFltr != null)
		promFilter = Integer.parseInt(strPromFltr);

	Empleado e = (Empleado) session.getAttribute( GestionInterface.ATT_EMPLEADO );
 
	String UR = u.getU_UR();
	String cCentroContable = null;
	if (u.getPropiedades() != null && u.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = u.getPropiedad("CCENTROCONTABLE").getValor();
	}

	String u_login = u.getLogin();
	if (u.getPropiedades() != null && u.getPropiedades().containsKey("INTEGRADOR_ADECUACIONES")) {
		if ("SI".equals(u.getPropiedad("INTEGRADOR_ADECUACIONES").getValor())) {
			u_login = "INTEGRADOR_ADECUACIONES";
		}
	}
			
	List v = cobl.getCasoOperacionConsulta(gavetaAsociada, noFolio, fDesde, fHasta, documento, operador, estatus, importe, folioSICOP, folioMAP, folioCAL, u); //brenda
	
	if (UR == null || cCentroContable == null) {
		if (UR == null)
			session.setAttribute("login.message", "UR no asignada al usuario, favor de contactar al administrador");
		if (cCentroContable == null)
			session.setAttribute("login.message", "Centro Contable no asignado al usuario, favor de contactar al administrador");
		if (UR == null && cCentroContable == null)
			session.setAttribute("login.message", "Centro Contable y UR no asignados al usuario, favor de contactar al administrador");
		response.sendRedirect("../index.jsp");
		return;
	}
%>
<head>
<title>Operaciones de<%=u.getNombre()%></title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

	<style type="text/css" title="currentStyle">
		@import "../Generador/css/demo_page.css";
		@import "../Generador/css/demo_table_jui.css";
		@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
	</style>


<link type="text/css" href="../css/gestion.css" rel="stylesheet">
<link type="text/css" href="../css/scrolltable.css" rel="stylesheet">
<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.core"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-1.2.6.js"></script>
<script type="text/javascript">

function exportExpedientes(){

	var data =  $('#dt_inbox').dataTable().fnGetData();
	
	if( data.length > 0  && data.length <= 100 ){
		
		var queryString = "";
		var token = "";
		
		for( i = 0; i < data.length; i++ ){
			var val =data[i][0];
			queryString = queryString + token + "export="+val;
			token ="&";
		}
		
		var ventimp = window.open("../export/expedienteCasos?" + queryString,"" ,"scrollbars=1, resizable=no, width=850, height=700");
		
	}else if( data.length > 100 ){
		alert("No es posible exportar mas de 100 expedientes. Por favor reduzca los resultados aplicando mas filtros de busqueda.");
	}
	
	
}


$(document).ready(function() {
	$("#exportBtn").button().click(
		function(){
			exportExpedientes();
		}
	);
	
	var oTable = $('#dt_inbox').dataTable({
			"aaSorting": [[<%=(u.getPropiedades() != null && u.getPropiedades().containsKey("INTEGRADOR_ADECUACIONES") && "SI".equals(u.getPropiedad("INTEGRADOR_ADECUACIONES").getValor())) ? 2 : 1%>, "asc"]],
			"bJQueryUI": true,
			"iDisplayLength": 20,
			"sPaginationType": "full_numbers",
			"sScrollY": 457,
			"oLanguage": {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				}
		});


    $("#dt_inbox tbody").click(function(event) { 
        $(oTable.fnSettings().aoData).each(function (){ 
            $(this.nTr).removeClass('row_selected'); 
        }); 
        $(event.target.parentNode).addClass('row_selected'); 
    }); 
	
});

function abrecaso( urlcaso ) {
	document.frmabrecaso.action += urlcaso;
	document.frmabrecaso.submit();	
}

</script>
<style>
div.tableContainer {
	height: 100%;
}
</style>
</head>
<body scroll="no">
		<table class="display">
       		<tr>
       			<td align="right">
       				<input type="button" id="exportBtn" value="Exportar Expediente">
       			</td>
       		</tr>
       	</table>
        <table id="dt_inbox" class="display">
          <thead>
            <tr>
              <th>Folio</th>
              <% if(u.getPropiedades() != null && u.getPropiedades().containsKey("INTEGRADOR_ADECUACIONES") && "SI".equals(u.getPropiedad("INTEGRADOR_ADECUACIONES").getValor())){%>
              <th>Folio Sicop</th>
              <% }else{ %>
              	<th>Documento</th>
              <%} %>
              <th>Fecha</th>
              <th>Tramite</th>
              <th>Estatus Operaci&oacute;n</th>
              <th>Aplicaci&oacute;n contable / Nombre </th>
              <th>Importe</th>
              <th>SICOP</th>
              <th>MAP</th>
              <th>CAL</th>
              <th>Operador</th>
			<%
			if (u.getPropiedades() != null && u.getPropiedades().containsKey("INTEGRADOR_ADECUACIONES")) {
				if ("SI".equals(u.getPropiedad("INTEGRADOR_ADECUACIONES").getValor())) {
			%>
              <th>Tipo</th>
              <th>Nivel</th>
			<%
				}
			}
			%> 
            </tr>
          </thead>
          <tbody>
            <%
            	{
            		int row = 0;
            		for (Iterator iter = v.iterator(); iter.hasNext(); row++) {
            			CasoOperacion co = (CasoOperacion) iter.next();
            			Caso c = ct.getCaso(co.getIdCaso());

            %>
            			<%if (c.getIdTC()==3 && co!=null && co.getStatusC()!=null && (!"AUTORIZADO".equals(co.getStatusC()) && !"CANCELADO".equals(co.getStatusC()))){ %>
            				<tr>
            			<%}else{ %>
            				<tr ondblclick="javascript:abrecaso('<%=GestionInterface.PRM_CASE%>=<%=co.getIdCaso()%>&<%=GestionInterface.PRM_CASE_OPER%>=<%=co.getIdCasoOper()%>');">
			            <%} %>
			              	<td>
			              		<%=(c!=null&&c.getFolio() != null ? c.getFolio() : "&nbsp;")%>
			              	</td>
			              	<td><%=(co.getDocumento() != null ? co.getDocumento() : "")%></td>	
			              	<td>
			              		<%=(c!=null&&c.getCasoDato("FECHA_DOCUMENTO")!=null&&c.getCasoDato("FECHA_DOCUMENTO").getValor() != null ? c.getFormatFechaInicio(c.getCasoDato("FECHA_DOCUMENTO").getValor()) : "&nbsp;")%>
			              	</td>
			              	<td>
			              		<%=c!=null&&c.getTipoCaso()!=null&&c.getTipoCaso().getDescripcion()!=null?c.getTipoCaso().getDescripcion():"&nbsp;"%>
			              	</td>
			              	<td>
			              		<%=co!=null&&co.getStatusC()!=null?co.getStatusC():"&nbsp;"%>
			              	</td>
			              	<td>
			              		<%=(co!=null&&co.getfechaAppCont()!=null?co.getfechaAppCont():"&nbsp;")%>
			              	</td>
			              	<td>
			              		<%=(co!=null&&co.getImporte()!=null?co.getImporte():"&nbsp;")%>
			              	</td>
			              	<td>
			              		<%=(co!=null&&co.getFolioSicop()!=null?co.getFolioSicop():"&nbsp;")%>
			              	</td>
			              	<td>
			              		<%=(co!=null&&co.getFolioMap()!=null?co.getFolioMap():"&nbsp;")%>
			              	</td>
			              	<td>
			              		<%=(co!=null&&co.getFolioCal()!=null?co.getFolioCal():"&nbsp;")%>
			              	</td>
			              	<td>
			              		<!-- a href="show-caso?<%=GestionInterface.PRM_CASE%>=<%=co.getIdCaso()%>&<%=GestionInterface.PRM_CASE_OPER%>=<%=co.getIdCasoOper()%>"></a> -->
			              		<%=(c!=null&&c.getCasoDato("OPERADOR")!=null&&c.getCasoDato("OPERADOR").getValor()!= null ? c.getCasoDato("OPERADOR").getValor() : "&nbsp;")%>			              		
			              	</td>
							<%
							if (u.getPropiedades() != null && u.getPropiedades().containsKey("INTEGRADOR_ADECUACIONES")) {
								if ("SI".equals(u.getPropiedad("INTEGRADOR_ADECUACIONES").getValor())) {
							%>
							<td><%=(co!=null&&co.getTipoAdecuacion()!=null?co.getTipoAdecuacion():"")%></td>
							<td><%=(co!=null&&co.getNivelAdecuacion()!=null?co.getNivelAdecuacion():"")%></td>
							<%
								}
							}
							%> 
			            </tr>
	            <%
	            	}
	            	}
	            %>
          </tbody>
        </table>
	        <form name="frmabrecaso" action="show-caso?" method="post">
	        <!-- no se necesitan campos porque se usa para abrir el caso -->
	        </form>

</body>
</html>
