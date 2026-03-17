<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	Map<String, Role> rol =usuario.getRoles();
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today= sdf.format(c1.getTime());
	c1.add(Calendar.MONTH, -1);
	String todayAnt= sdf.format(c1.getTime());
%>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  	<style type="text/css" title="currentStyle"> 
 		@import "../css/demo_page.css";
		@import "../css/demo_table_jui.css"; 
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../../css/interfaz.css";
	</style>
	
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
	<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>
		
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/funciones.js"></script>
	<link href="../../css/reportesGRM.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript">
	  	$(document).ready(function() {
	  		<%
			    String role="";
			    String roles="";
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
			//	NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				Map botones=nb.getBotones(role,"RecepcionMaterial","ConsultaRecepcion");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}

				%>
	  		roles="<%=roles%>";
	  		
	  	});//Fin del document ready
	  	function generaReporte() {
			$.blockUI({message: "Procesando espere ......",timeout: 100000});
			document.ExportarForm.submit();
			$.unblockUI();
		}
		function filtros(){
			alert($('[name="REPORTE"]:checked').val())
	  		$("#reporteNombre").val($('[name="REPORTE"]:checked').val());
	  		if("Formato_1120_SIIWEB.xlsm"==$('[name="REPORTE"]:checked').val()){
	  			$("#nTipoReporte").val(1);
	  		}
	  	}
	  	</script>
  </head>
  
  <body >
  		<form id="ExportarForm" name="ExportarForm" action="../../servlet/ReportesGRM" method="get" target="_self">
		<div class="container-fluid">
			<div class="col-md-12 col-lg-12 col-sm-12">
	 			<fieldset class="form-group border p-3">
	 				<legend class="w-auto px-2"> Reporte SII@Web</legend>
	 				<div class="form-group">
						<div class="form-check">
							<input class="form-check-input" type="radio" id="Foramto1120" name="REPORTE" checked="checked" onclick="filtros()" value="Formato_1120_SIIWEB.xlsx" />
							<label class="form-check-label" for="Foramto1120">
							 Formato 1120 Estadisticas por Acci&oacute;n de Compra
							</label>
						</div>
					</div>
					<div class="form-group" >
						<div class="form-check form-check-inline" id="tdXlsx">
						  <input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdcsvReporteProg" 		name="cmdcsvReporteProg" 	value="Excel"	onclick="generaReporte();" />
						</div>
					</div>
	 			</fieldset>
	 		</div>
	 	</div>
	 	<input type="hidden" name="operacion" id="operacion" value="0" />
		<input type="hidden" name="nTipoReporte" id="nTipoReporte" value="1" />
		<input type="hidden" name="reporteNombre" id="reporteNombre" value="Formato_1120_SIIWEB.xlsx" />
		<input type="hidden" name="cEjercicioActual" id="cEjercicioActual" value="2019" />
		<input type="hidden" name="cEjercicioAnterior" id="cEjercicioAnterior" value="2018" />
	 	</form>
  </body>
</html>