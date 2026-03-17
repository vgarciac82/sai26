<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@page import="com.syc.gestion.core.NegativaPestana"%>
<%@page import="java.util.*"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	Map rol =usuario.getRoles();
 %>

<!DOCTYPE html>
<html>
  <head>
    
    
    <title>Conciliaciones de Recurso</title>
	<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">    
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
	  	<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" /> 
		<style type="text/css" title="currentStyle"> 
		 	@import "../../css/custom-theme/jquery-ui-1.8.16.custom.css";
	 		@import "../css/demo_table_jui.css"; 
			@import "../css/demo_page.css"; 
			@import "../css/demo_table.css";
			@import "../../css/interfaz.css";
		</style>
		
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
		<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>
			
		<script type="text/javascript" src="../js/crud.js"></script>
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script> 
		<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script> 
		<script type="text/javascript" src="../../js/utils/syctools.js"></script>
	  	<link href="../../css/reportesGRM.css" rel="stylesheet" type="text/css" />
	  	
  	<script type="text/javascript">
	  	$(document).ready(function() {
	  		<%
			    String role="";
			    String roles="";
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
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
	  	});
	  	function checkShortcut(){			
			if(event.keyCode==27){  //escape
				return false;
			}
			if(((event.srcElement.tagName.toUpperCase() != 'INPUT' && event.srcElement.tagName.toUpperCase() != 'TEXTAREA')
				|| document.getElementById(event.srcElement.id).style.readonly )	//backspace
				&& (event.keyCode==8 || event.keyCode==13)){					
				return false;
			}
		}
		function openARCH(){
			window.open(
				"../../servlet/CatalogosCSV?"
				+ "rn="+$('[name="REPORTE"]:checked').val(),
				 'Procesando', 'status=1, width=500px, height=100px, left=150px');
		}
	</script>
  </head>
  
  <body >
	<form action="">
		<div class="container-fluid">
			<fieldset class="form-group border p-3">
			  		<legend class="w-auto px-2">Conciliaciones</legend>
			  		
			  		<div class="form-group">
						<div class="form-check">
							<input class="form-check-input" type="radio" id="reporteApartPrecomComp" name="REPORTE" checked="checked" onclick="habilitaFundamento()" value="reportesConPlantillaXLS" />
							<label class="form-check-label" >
							  Apartado, Precompromiso y compromiso
							</label>
						</div>
					</div>
					
					<div class="form-group">
						<div class="form-check form-check-inline" id="tdXlsx">
						  <input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdXlsxReporteProg" 		name="cmdXlsxReporteProg" 	value="Excel"	onclick="openARCH();" />
						</div>
					</div>
			</fieldset>
		</div>
	</form>
  </body>
</html>
