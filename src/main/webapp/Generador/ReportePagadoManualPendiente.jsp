<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
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
	String u_login = "";

	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

	String mensaje = "";
	if (request.getParameter("msg") != null && !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

	//Valida Centro de Costos
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}

	cUR = usuario.getU_UR();	
	cRamo = usuario.getU_Ramo();
	u_login = usuario.getLogin();
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

	$( "#uEjecutora" ).val('<%=cUR%>');
	$("#cCentroContable").val( "<%=cCentroContable%>");
		
    $("#fechaI").val(moment().format('yyyy-01-01'));
	$("#fechaF").val(moment().format('yyyy-MM-DD'));
	
	$("#cmdxlsReporteProg").button();
	
	if ($("#uEjecutora").val()!="A02"){
		querySelectPost("cUnidadEjecutoraVistasTesoreria", "cUnidadEjecutora", {async : false});
		$("#checkAll").hide();
	}else
		querySelectPost("CAT_UNIDAD_EJECUTORARead", "cUnidadEjecutora", {async : false});
			
	cssReadOnly();
});
	function cssReadOnly(){
		$( "[readOnly]" ).each(function(){	
			$(this).addClass("notEditable");	
		});
	}

	function extrae(){
		if ($("#checkAll").prop("checked"))
			$("#todos").val("SI");
		else
			$("#todos").val("NO");
		
		if ($.trim($("#fechaI").val()) != "" && $.trim($("#fechaF").val()) != ""){
			$("#fecha_inicio").val($("#fechaI").val().split('-').reverse().join('/'));
			$("#fecha_fin").val($("#fechaF").val().split('-').reverse().join('/'));
			document.ExportarForm.submit();
		}
		else{
			alert("Favor de seleccionar las fechas.");
			return;
		}
	}		

</script>

</head>
<body id="dt_example">
<br/>	
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReportePagadoManualPendiente" method="get" target="_blank">
		<input id="u_Login" name="u_Login" type="hidden" value="<%=u_login%>"/>
		<input id="uEjecutora" name="uEjecutora" type="hidden"/>
		<input id="todos" name="todos" type="hidden" value=""/>
		<input type="hidden" id="fecha_inicio" name="fecha_inicio"/>
		<input type="hidden" id="fecha_fin" name="fecha_fin"/>
		
		<div id="container" style="width: 80%" class="container">			
			<div class="card-header"> <h3> Consulta de Pagado Manual Pendiente </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																	
				</div>
	        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="cUnidadEjecutora" class="form-label"> Unidad Ejecutora:&nbsp; </label>												
				</div>				
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">
					<select name = "cUnidadEjecutora" id = "cUnidadEjecutora" class="form-select form-select-sm">							
					</select>	
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">							
					<input type="checkbox" id="checkAll" name="checkAll" class="form-check-input"/>	&nbsp;Todas las Unidades										
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
                          <input type="date" class="form-control form-control-sm" id="fechaI" name="fechaI" value="<%=today%>"/>                                    
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
                           <input type="date" class="form-control form-control-sm" id="fechaF" name="fechaF" value="<%=today%>"/>                                
                       </div>
                	</div>										
				</div>							
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">																	
				</div>					
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<input type="button" id="cmdxlsReporteProg" name="cmdxlsReporteProg" value="Extraer" class="btn btn-secondary btn-sm" onclick="extrae()"/>													
				</div>						
			</div>			
																					
		</div>
	</form>
</body>
</html>