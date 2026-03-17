
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
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Reporte Momentos Contables</title>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css"></link>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
						
$(document).ready(function() {	
	
	$("#cUR").val( "<%=cUR%>");
	var unidad = $("#cUR").val();
	
	$("#fechaI").val(moment().format('yyyy-01-01'));
	$("#fechaF").val(moment().format('yyyy-MM-DD'));
	
	if (unidad.substring(0,1)== "G")
		querySelectPost("cUnidadResponsableVistas", "cUnidadResponsable", {async: false });
	else
		querySelectPost("cUnidadEjecutoraAENSSRead", "cUnidadResponsable", {async: false });
		
	querySelectPost("CatalogoCentroContableRead", "cCentroContable", {async: false });
			
	$("#cmdxlsReporteProg").button();
	
});

	function extrae(){
		if ($("#cCentroContable").val() != "*" || 
			$("#ep").val() != "" || 
			$("#rfc").val() != "" || 
			$("#cRazonSocial").val() != "" || 
			$("#estatus").val() != "00" ||
			$("#cContrato").val() != "" ||
			($("#fechaI").val() != "" && $("#fechaF").val() != "")) 
		{
			$("#fecha_inicio").val($("#fechaI").val().split('-').reverse().join('/'));
			$("#fecha_fin").val($("#fechaF").val().split('-').reverse().join('/'));
			document.ExportarForm.submit();
		} else {
			Swal.fire("Revise","Llenar por lo menos un criterio de búsqueda, en caso de utilizar fechas seleccionar ambas","info");
		}
		
	}		

</script>

</head>
<body id="dt_example">
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReportePagosBeneficiarios" method="get" target="_blank">
		<!--<input type="hidden" id="cCContable" name="cCContable"/> --> 		
		<input type="hidden" id="reporteTipo" name="reporteTipo" value="conFactura"/>
		<input type="hidden" id="cUR" name="cUR" />
		<input type="hidden" id="fecha_inicio" name="fecha_inicio"/>
		<input type="hidden" id="fecha_fin" name="fecha_fin"/>
		
		<div id="container" style="width: 80%" class="container">			
			<div class="card-header"> <h3> Consulta de Pagos a Beneficiarios con factura </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																	
				</div>
	        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="cCentroContable" class="form-label"> Centro Contable:&nbsp; </label>												
				</div>				
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">
					<select name = "cCentroContable" id = "cCentroContable" class="form-select form-select-sm">							
					</select>	
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																	
				</div>
	        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="cUnidadResponsable" class="form-label"> Unidad Responsable:&nbsp; </label>												
				</div>				
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<select name = "cUnidadResponsable" id = "cUnidadResponsable" class="form-select form-select-sm">							
					</select>	
				</div>
			</div>
			
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																	
				</div>
	        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="ep" class="form-label"> EP:&nbsp; </label>												
				</div>		
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
					<div class= "input-group">
						<span class="input-group-text"><i class="bi bi-tag"></i></span>						
						<input type="text" name="ep" id="ep" class="form-control form-control-sm"/>
					</div>
				</div>							
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																	
				</div>
	        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="rfc" class="form-label"> RFC:&nbsp; </label>												
				</div>		
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" name="rfc" id="rfc" class="form-control form-control-sm"/>
					</div>
				</div>							
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																	
				</div>
	        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="cRazonSocial" class="form-label"> Raz&oacute;n social:&nbsp; </label>												
				</div>		
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-building"></i></span>
						<input type="text" name="cRazonSocial" id="cRazonSocial" class="form-control form-control-sm"/>
					</div>
				</div>							
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																	
				</div>
	        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="cContrato" class="form-label"> Contrato:&nbsp; </label>												
				</div>		
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-file-earmark-text"></i></span>
						<input type="text" name="cContrato" id="cContrato" class="form-control form-control-sm"/>
					</div>
				</div>							
			</div>
						
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																	
				</div>
	        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="estatus" class="form-label"> Status:&nbsp; </label>												
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<select name = "estatus" id = "estatus" class="form-select form-select-sm">		
						<option value= "00">Todas</option>
						<option value= "PENDIENTE">Pendiente</option>
						<option value= "CANCELADO">Cancelado</option>
						<option value= "AUTORIZADO">Autorizado</option>
						<option value= "DEVENGADO">Devengado</option>
						<option value= "ENVIO SICOP-SIAFF">Envio SICOP-SIAFF</option>
						<option value= "ENVIO SIAFF">Envio SIAFF</option>
						<option value= "EJERCIDO">Ejercido</option>
						<option value= "PAGADO">Pagado</option>						
					</select>	
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																	
				</div>
	        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="fechaI" class="form-label"> Desde:&nbsp; </label>												
				</div>		
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="form-group">
                       <div class="input-group date" id="datepicker1">
                          <input type="date" class="form-control form-control-sm" id="fechaI" name="fechaI"/>                                    
                       </div>
                	</div>										
				</div>							
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																	
				</div>
	        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="fechaF" class="form-label"> Hasta:&nbsp; </label>												
				</div>		
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="form-group">
                       <div class="input-group date" id="datepicker1">
                           <input type="date" class="form-control form-control-sm" id="fechaF" name="fechaF"/>                                
                       </div>
                	</div>										
				</div>							
			</div>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="button" id="cmdxlsReporteProg" name="cmdxlsReporteProg" value="Extraer" class="btn btn-secondary" onclick="extrae()"/>
				</div>						
			</div>
																				
		</div>
	</form>

</body>
</html>


