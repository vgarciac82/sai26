<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@page import="com.syc.gestion.core.NegativaPestana"%>
<%@page import="java.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>
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
    <title>'Reportes de Ordenes de compra'</title>
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
	  		init();
	  		agregaDatePickerFechas();
	  		filtros();
	  	});
	  	function init(){
	  		querySelectPost("mPeriodoRead2", "catMeses", {async : false});
	  		querySelectPost("tEjercicioReadNmaDB", "catEjercicios", {async : false,
	  			callback : function() {
					$("#cEjercicioAnt").val($("#catEjercicios :selected").text());
				}
			});
	  		
	  	}
	  	function enviaConsulta() {
			$.blockUI({message: "Procesando espere ......",timeout: 100000});
			document.ExportarForm.submit();
			$.unblockUI();
		}
	  	function filtros(){
	  		$("#trV2").hide();
	  		$("#trFechas").hide();
	  		$("#cmdcsvReporteProg").hide();
	  		$("#trV2Backup").hide();
	  		$("#reporteNombre").val($('[name="REPORTE"]:checked').val());
	  		if($('[name="REPORTE"]:checked').val()=='reporteV2' 
	  				|| $('[name="REPORTE"]:checked').val()=='reporteV2Backup' 
	  				|| $('[name="REPORTE"]:checked').val()=='reporteAnexo3'
	  				|| $('[name="REPORTE"]:checked').val()=='reporteFormato7y8'
	  				|| $('[name="REPORTE"]:checked').val()=='reporteFormato7y8PLU'
	  				|| $('[name="REPORTE"]:checked').val()=='reporteFormato14'
	  				|| $('[name="REPORTE"]:checked').val()=='reporteFormato14PLU'
	  		){
	  			if($('[name="REPORTE"]:checked').val()=='reporteV2'){
	  				$("#trV2").show();
	  			}
	  			else if($('[name="REPORTE"]:checked').val()=='reporteV2Backup'){
	  				$('#catMesesTodo').empty().append('<option selected="selected" value="0">*Todo</option>');
	  				clonaSelect("catMeses","catMesesTodo")
	  				$("#trV2Backup").show();
	  			}
	  			else if($('[name="REPORTE"]:checked').val()=='reporteFormato7y8' || $('[name="REPORTE"]:checked').val()=='reporteFormato14' 
	  					|| $('[name="REPORTE"]:checked').val()=='reporteFormato7y8PLU'|| $('[name="REPORTE"]:checked').val()=='reporteFormato14PLU'){
	  				$("#trFechas").show();
	  			}
	  			$("#cmdcsvReporteProg").show();
	  		}
	  	}
	  	function agregaDatePickerFechas(){
	  		$("#fInicial").datetimepicker({
				format: 'DD/MM/YYYY',
				altField: "#actualDate",
			 	currentText: "Now",
				changeYear: true
				
			});
			$("#fFinal").datetimepicker({
				format: 'DD/MM/YYYY',
				altField: "#actualDate",
			 	currentText: "Now",
				changeYear: true
				
			});
		}
		function actualizaEjercicioAnt(){
			$("#cEjercicioAnt").val($("#catEjercicios :selected").text());
		}
	</script>
  </head>
  
  <body>
  	<form id="ExportarForm" name="ExportarForm" action="../../reportes/ReportesINAI" method="get" target="_self">
		<div class="container-fluid">
			<div class="col-md-12 col-lg-12 col-sm-12">
	 			<fieldset class="form-group border p-3">
	 				<legend class="w-auto px-2"> Reportes de presidencia</legend>
	 				<div class="form-group">
						<div class="form-check">
							<input class="form-check-input" type="radio" id="reporteV2" name="REPORTE" checked="checked" onclick="filtros()" value="reporteV2" />
							<label class="form-check-label" for="reporteV2">
							 Formato V2-MATRIZ DE CONTRATOS
							</label>
						</div>
						<div class="form-check">
							<input class="form-check-input" type="radio" id="reporteAnexo3" name="REPORTE"  onclick="filtros()" value="reporteAnexo3" />
							<label class="form-check-label" for="reporteAnexo3" >
							 Anexo 3 para presidencia
							</label>
						</div>
						<div class="form-check">
							<input class="form-check-input" type="radio" id="reporteFormato7y8" name="REPORTE"  onclick="filtros()" value="reporteFormato7y8" />
							<label class="form-check-label" for="reporteFormato7y8" >
							 Concentrado general de contratos (formato 7)
							</label>
						</div>
						<div class="form-check">
							<input class="form-check-input" type="radio" id="reporteFormato14" name="REPORTE"  onclick="filtros()" value="reporteFormato14" />
							<label class="form-check-label" for="reporteFormato14">
							 Concentrado general de contratos (formato 14)
							</label>
						</div>
						<div class="form-check">
							<input class="form-check-input" type="radio" id="reporteFormato7y8PLU" name="REPORTE"  onclick="filtros()" value="reporteFormato7y8PLU" />
							<label class="form-check-label" for="reporteFormato7y8PLU" >
							 Concentrado general de contratos (formato 7 con Plurianuales Anteriores)
							</label>
						</div>
						<div class="form-check">
							<input class="form-check-input" type="radio" id="reporteFormato14PLU" name="REPORTE"  onclick="filtros()" value="reporteFormato14PLU" />
							<label class="form-check-label" for="reporteFormato14PLU">
							 Concentrado general de contratos (formato 14 con Plurianuales Anteriores)
							</label>
						</div>
					</div>
	 			</fieldset>
	 			<fieldset class="form-group border p-3">
	 				<legend class="w-auto px-2"> Backup Reportes</legend>
	 				<div class="form-group">
						<div class="form-check">
							<input class="form-check-input" type="radio" id="reporteV2Backup" name="REPORTE"  onclick="filtros()" value="reporteV2Backup" />
							<label class="form-check-label" >
							 Formato V2-MATRIZ DE CONTRATOS
							</label>
						</div>
						
					</div>
	 			</fieldset>
	 			<fieldset class="form-group border p-3">
	 				<legend class="w-auto px-2"> Filtros</legend>
	 				<div class="form-group">
	 					<div class="row justify-content-md-left" id="trV2">
			 				<div class="col-md-auto">
			 					<label for="catEjercicios">Ejercicio Fiscal Anterior</label>
								<select class="custom-select" id="catEjercicios" name="catEjercicios" onchange="actualizaEjercicioAnt()"> </select>
							</div>
							<div class="col-md-auto">
								<label for="catMeses">Mes</label>
								<select class="custom-select"  id="catMeses" name="catMeses"> </select> 
					        </div>
					    </div>
					    <div class="row justify-content-md-left" id="trV2Backup" style="display: none;" >
					    	<div class="col-md-auto">
			 					<label for="catMesesTodo">Mes</label>
								<select class="custom-select" id="catMesesTodo" name="catMesesTodo"></select>
							</div>
					    </div>
					    <div class="row justify-content-md-left" id="trFechas">
						    <div class="form-group row">
								<div class="col-md-auto">
									<label for="fInicial">Fecha Inicio</label>
									<div class="input-group date" id="fInicial" data-target-input="nearest">
							          <input type="text" class="form-control datetimepicker-input" data-target="#fInicial" title="Fecha Inicial" id="fInicio" name="fInicio" value="<%=todayAnt %>"/>
							          <div class="input-group-append" data-target="#fInicial" data-toggle="datetimepicker" title="Fecha Inicial">
							            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
							          </div>
							        </div>
						        </div>
						    </div>
						    <div class="form-group row">
						        <div class="col-md-auto">
									<label for="fFinal">Fecha Fin</label>
									<div class="input-group date col-xs-2" id="fFinal" data-target-input="nearest" >
										<input type="text" class="form-control datetimepicker-input" data-target="#fFinal" id="fFin" name="fFin" title="Fecha Final" value="<%=today %>"  />
										<div class="input-group-append" data-target="#fFinal" data-toggle="datetimepicker" title="Fecha Final">
										  <div class="input-group-text"><i class="fa fa-calendar"></i></div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
	 			</fieldset>
	 			<fieldset class="form-group border p-3">
	 				<legend class="w-auto px-2">Formato de Archivo</legend>
	 				<div class="form-group">
						<div class="form-check form-check-inline" id="tdXlsx">
						  <input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdcsvReporteProg" 		name="cmdcsvReporteProg" 	value="Excel"	onclick="enviaConsulta();" />
						</div>
					</div>
	 			</fieldset>
	 			
	 		</div>
		</div> 
		<input type="hidden" id="reporteNombre" name="reporteNombre" value="reporteV2"></input>
		<input type="hidden" id="cEjercicioAnt" name="cEjercicioAnt" value="2019"></input>
	</form>
  </body>
</html>
