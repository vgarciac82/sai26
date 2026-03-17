
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>

<%
	boolean bAplicadoCont = false;
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String algo = "";

	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);

	String mensaje = "";
	if (request.getParameter("msg") != null
			&& !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper"))
				.intValue();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(
			GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	//documentos del caso
	CasoBusinessLogic cbl = new CasoBusinessLogic(
			GestionInterface.ATT_CONEXION);

	//Valida Centro de Costos
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}
	
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	System.out.println(efa);	

	cUR = usuario.getU_UR();	
	cRamo = usuario.getU_Ramo();
	//cRamo="16";
	algo = usuario.getLogin();
	//de aki para arriba es de cajon
	Grupo grupo = usuario.getGrupo("EXPORTA_MOMENTOS_MASIVO");
	boolean permiteExportarPDFMasivo = (grupo != null);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Reporte Momentos Contables</title>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/crud.js"></script>

<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
	
var masivoPDF = <%=permiteExportarPDFMasivo%>;
$(document).ready(function() {	
	
	$("#cmdxlsReporteProg").button();
	$("#cmdPdfReporteProg").button();	
	$("#masivoPDF").hide();
	
	if( masivoPDF )
		$("#masivoPDF").show();
		
	$("#cCentroContable").val( "<%=cCentroContable%>");
	
	$("#pbExcel").button();
});
	
	function selecciona(elemento){
		//System.out.println("Opcion " + elemento);		
		if(elemento.value == "2"){
			document.getElementById("por_solicitud").style.display = "none";
			document.getElementById("masivo").style.display = "block";
		}
		else {
			document.getElementById("por_solicitud").style.display = "block";
			document.getElementById("masivo").style.display = "none";
		}
		
	}
	
	function enviaConsulta(tipoReporte) {
		if (tipoReporte == 2){
			if ($("#fechaI").val() == "" || $("#fechaF").val() == "") {
				alert("Los rangos de fechas son obligatorios.");
			}
			else{		
				$("#fecha_inicio").val($("#fechaI").val().split('-').reverse().join('/'));
				$("#fecha_fin").val($("#fechaF").val().split('-').reverse().join('/'));
				document.ExportarForm.submit();
			}
		}
	}
	
	function openARCH(ext){
		if (ext == "xls"){			
			if ($("#fechaI").val() == "" || $("#fechaF").val() == "") {
				alert("Los rangos de fechas son obligatorios.");
			}
			else{		
				$("#fecha_inicio").val($("#fechaI").val().split('-').reverse().join('/'));
				$("#fecha_fin").val($("#fechaF").val().split('-').reverse().join('/'));
				document.ExportarForm.submit();
			}
		}
		else if (ext == "pdf"){
			var tipo = $('input:radio[name=TIPO_REPORTE]:checked').val();
			if ( tipo != 3 && ( ($("#no_solicitud").val() == "" && $("#cxp").val() == "") || $("#tipo_solicitud").val() == "0" )  ) {
				alert("Los datos de numero y tipo de solicitud son obligatorios.");
			}
			else{		
				document.ExportarForm.submit();	
			}
		}
	}		

</script>

</head>
<body id="dt_example">
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReporteMomentos" method="get" target="_blank">
		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /> 
		<input type="hidden" id="cCentroContable" name="cCentroContable"/> 		
		<input type="hidden" name="TIPO_REPORTE2" name="TIPO_REPORTE2" value="ReporteMomentosSolicitud_cxp.jasper"/>
		<input type="hidden" name="TIPO_REPORTE3" name="TIPO_REPORTE3" value="ReporteMomentosSolicitud_COM.jasper"/>
		<input type="hidden" name="TIPO_REPORTE4" name="TIPO_REPORTE4" value="ReporteMomentosSolicitud_cxp_COM.jasper"/>		
		<input type="hidden" id="fecha_inicio" name="fecha_inicio"/>
		<input type="hidden" id="fecha_fin" name="fecha_fin"/>

		<div id="container" style="width: 80%" class="container">
		
			<div class="card-header"> <h3> Reporte de Momentos Contables </h3> </div>
			<hr class="mt-3"/>
							
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
					<input type="radio" name="TIPO_REPORTE" onclick="selecciona(this)" value="ReporteMomentosSolicitud.jasper" class="form-check-input" checked/>
					<label for="solicitud" class="form-check-label">Por Solicitud</label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
					<input type="radio" name="TIPO_REPORTE" onclick="selecciona(this)" value="2" class="form-check-input"/>
					<label for="masivo" class="form-check-label">Masivo</label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12" id="masivoPDF">
					<input type="radio" name="TIPO_REPORTE" onclick="selecciona(this)" value="3"/>
					<label for="masivoPDF" class="form-check-label">Masivo PDF</label>
				</div>
			</div>
			
			<div id="por_solicitud">
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="BusqIntegra" class="form-label"> No. Solicitud: </label>											
					</div>	
	        		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<input type="text" name="no_solicitud" id="no_solicitud" class="form-control form-control-sm" placeholder="0"/>
					</div>				
				</div>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="BusqIntegra" class="form-label"> Cuenta por Pagar: </label>											
					</div>	
	        		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<input type="text" name="cxp" id="cxp" class="form-control form-control-sm" placeholder="10CP0000000000"/>
					</div>				
				</div>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="BusqIntegra" class="form-label"> Tipo de Solicitud: </label>											
					</div>	
	        		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<select name="tipo_solicitud" id="tipo_solicitud" class="form-select form-select-sm">
							<option value= "0">Selecciona un pago</option>
							<option value= "1">Pago Directo</option>
							<option value= "2">Pago Diverso</option>								
							<option value= "3">Pago Federalizado</option>
							<option value= "4">Pago Obra</option>
							<option value= "5">Relacion Gastos</option>
							<option value= "6">Operaciones Ajenas</option>
							<option value= "7">Integracion</option>	
							<option value= "8">Nomina</option>
							<option value= "9">Penalizaciones</option>
							<option value= "10">Registro Ingreso Propio</option>
							<option value= "11">Registro Ingreso Fiscal</option>
						</select>
					</div>				
				</div>
									
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<input type="button" id="cmdPdfReporteProg" name="cmdPdfReporteProg" value="Extraer" class="btn btn-secondary btn-sm" onclick="javascript:openARCH('pdf')"/>													
					</div>						
				</div>
			    
			</div>
											
			<div id="masivo" style="display:none;">
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="form-group">
	                       <label for="fecha_inicio">Desde:</label>
	                       <div class="input-group date" id="datepicker1">
	                          <input type="date" class="form-control form-control-sm" id="fechaI" name="fechaI"/>                                    
	                       </div>
	                	</div>										
					</div>			        				
				</div>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="form-group">
	                       <label for="fecha_fin">Hasta:</label>
	                       <div class="input-group date" id="datepicker1">
	                          <input type="date" class="form-control form-control-sm" id="fechaF" name="fechaF"/>                                    
	                       </div>
	                	</div>										
					</div>			        				
				</div>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<input type="button" id="cmdxlsReporteProg" name="cmdxlsReporteProg" value="Extraer" class="btn btn-secondary btn-sm" onclick="javascript:openARCH('xls')"/>													
					</div>						
				</div>
												
			</div>		
		</div>
	</form>

</body>
</html>


