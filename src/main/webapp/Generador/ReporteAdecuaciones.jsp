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
	String DATE_FORMAT = "yyyy/MM/dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
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

	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	System.out.println(efa);	

	cUR = usuario.getU_UR();	
	cRamo = usuario.getU_Ramo();
	algo = usuario.getLogin();
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Reporte de Adecuaciones</title>

<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css"/>

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
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

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

	
	$("#cmdxlsReporteProg").button();
});

	function extrae(){
		$("#fecha_inicio").val($("#fechaI").val().split('-').reverse().join('/'));
		$("#fecha_fin").val($("#fechaF").val().split('-').reverse().join('/'));
		
		if ($("#fecha_inicio").val() == "" || $("#fecha_fin").val() == "") {
			Swal.fire({ icon: 'warning',
						text: "Favor de seleccionar la fecha de inicio y fecha de fin." });			
		}
		else{		
			document.ExportarForm.submit();
		}	
	}		
	
</script>

</head>
<body id="dt_example">
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReporteAdecuaciones" method="get" target="_blank">
		<input type="hidden" id="cUR" name="cUR"/>
		<input type="hidden" id="fecha_inicio" name="fecha_inicio"/>
		<input type="hidden" id="fecha_fin" name="fecha_fin"/>
		
		<div id="container" style="width: 80%" class="container">
		
			<div class="card-header"> <h3> Reporte de Adecuaciones </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row d-flex">	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Unidad Responsable:</label>											
				</div>					
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
					<select name = "cUnidadResponsable" id = "cUnidadResponsable" class="form-select form-select-sm">																					
					</select>	
				</div>
			</div>
			
			<div class="row d-flex">	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">EP:</label>											
				</div>					
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
					<input type="text" name="ep" id="ep" class="form-control form-control-sm" />	
				</div>
			</div>
			
			<div class="row d-flex">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Desde:</label>											
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">		
					<div class="input-group date" id="datepicker1">
                    	<input type="date" class="form-control form-control-sm" id="fechaI" name="fecha_inicio"/>                                    
                    </div>			
				</div>
			</div>				
			
			<div class="row d-flex">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label class="form-label">Hasta:</label>											
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">		
					<div class="input-group date" id="datepicker1">
                    	<input type="date" class="form-control form-control-sm" id="fechaF" name="fecha_fin"/>                                    
                    </div>			
				</div>
			</div>	
			
			<div class="row d-flex">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">															
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="button" id="cmdxlsReporteProg" name="cmdxlsReporteProg" value="Extraer" class="btn btn-secondary" onclick="extrae()"/>													
				</div>						
			</div>		
																	
		</div>
	</form>

</body>
</html>


