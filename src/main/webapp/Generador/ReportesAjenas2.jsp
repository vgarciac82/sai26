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
<title>Operaciones Ajenas</title>

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
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
						
$(document).ready(function() {
	//querySelectPost("tGrupoOpAjenasRead", "cBeneficiario", {async: false });
	
	$("#cCentroContable").val( "<%=cCentroContable%>");
	//$("#cUR").val( "<%=cUR%>");
	var unidad = $("#cUR").val();
	$("#fechaI").val(moment().format('yyyy-01-01'));
	$("#fechaF").val(moment().format('yyyy-MM-DD'));
	
	if (unidad.substring(0,1)== "G")
		querySelectPost("cUnidadResponsableVistas", "cUnidadResponsable", {async: false });
	else
		querySelectPost("cUnidadEjecutoraAENSSRead", "cUnidadResponsable", {async: false });
		
		$("#pbExcel").button();
	
	});

	function grupoRetencion() {
		queryFormPost("tGrupoOpAjenasOnclicRead", {
			async : false
		});
	}
	
	function conResumen(seleccion){		
		if(seleccion.value == 1 || seleccion.value == 4 || seleccion.value == 6 || seleccion.value == 7 || seleccion.value == 8 || seleccion.value == 9 ){				
			document.getElementById("TIPO_REPORTE2").disabled = false;
		}		
	}
	
	function enviaConsulta() {
		$("#fecha_inicio").val($("#fechaI").val().split('-').reverse().join('/'));
		$("#fecha_fin").val($("#fechaF").val().split('-').reverse().join('/'));
		
		if ($("#fecha_inicio").val() == "" || $("#fecha_fin").val() == "" || $("#tipo_ajena").val() == 0){
			Swal.fire({ icon: 'warning',
						text: "Los rangos de fechas y el grupo de retenciones son obligatorios." });			
		}
		else if ($("#tipo_ajena").val() ==10 && document.getElementById("TIPO_REPORTE2").checked == true){			
			Swal.fire({ icon: 'warning',
						text: "Para ese grupo debe seleccionar la opcion detalle." });		
		}
		else {
			document.ExportarForm.submit();
		}
	}
	
	function tipoReporte(){
		if (document.getElementById("TIPO_REPORTE2").checked == true) {
			document.getElementById("Coord").checked = false;
			document.getElementById("Coord").disabled = true;
			document.getElementById("EP").checked = false;
		}
		
		if (document.getElementById("TIPO_REPORTE1").checked == true) {
			document.getElementById("Coord").disabled = true; 
			document.getElementById("Coord").checked = false;
			document.getElementById("EP").checked = false;
		}
	}
	function habilitar(){
		 
		if ((document.getElementById("EP").checked == true) && (document.getElementById("TIPO_REPORTE1").checked == true)) 
			document.getElementById("Coord").disabled = false;
		else {
			document.getElementById("Coord").disabled = true; 
			document.getElementById("Coord").checked == false;
			}
	}
	
</script>

</head>
<body id="dt_example">
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReporteRetenciones" method="get">
		
		<input type="hidden" id="cUR" name="cUR" value="<%=cUR%>"/> 		
		<input type="hidden" id="cCentroContable" name="cCentroContable"/>
		<input type="hidden" id="fecha_inicio" name="fecha_inicio"/> 		
		<input type="hidden" id="fecha_fin" name="fecha_fin"/> 		 		

		<div id="container" style="width: 80%" class="container">
			<div class="card-header"> <h3> Reporte Operaciones Ajenas </h3> </div>
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
					<label class="form-label">Selecciona un grupo:</label>											
				</div>					
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<select name = "tipo_ajena" id = "tipo_ajena" class="form-select form-select-sm" onchange="conResumen(this)">
						<option value= "0">Grupo de Retenciones</option>
						<!-- <option value= "1">TESOFE1</option>  --> <!-- IVA -> (HONORARIOS, ARRENDAMIENTO, AUTOTRANSPORTE) ISR -> (HONORARIOS, ARRENDAMIENTO, OTROS, RESICO) -->
						<option value= "7">TESOFE1 IVA</option> <!-- IVA (HONORARIOS, ARRENDAMIENTO, AUTOTRANSPORTE) --> 
						<option value= "8">TESOFE1 ISR</option> <!-- ISR (HONORARIOS, ARRENDAMIENTO, OTROS, RESICO) -->
						<option value= "4">TESOFE2</option> <!-- 5 AL MILLAR -->								
						<option value= "6">VARIOS (Gob. Estado)</option> <!-- CEDULAR -->
						<option value= "9">LAUDOS ISR</option> <!-- ISR LAUDOS -->	
						<option value= "10">Acumulada con LC</option> <!-- Cedula Acumulada con Linea de captura -->						
					</select> 
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
                    	<input type="date" class="form-control form-control-sm" id="fechaI" name="fechaI"/>                                    
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
                    	<input type="date" class="form-control form-control-sm" id="fechaF" name="fechaF"/>                                    
                    </div>			
				</div>
			</div>	
			
			<div class="row d-flex">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
                   	<label for="TIPO_REPORTE1" class="form-check-label">Detalle: &nbsp;</label>
                   	<input type="radio" name="TIPO_REPORTE" id="TIPO_REPORTE1" onclick="tipoReporte()" value="1" class="form-check-input" checked/>						                                                   			
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
                   	<label for="TIPO_REPORTE1" class="form-check-label">Resumen: &nbsp;</label>
                   	<input type="radio" name="TIPO_REPORTE" id="TIPO_REPORTE2" onclick="tipoReporte()" value="2" class="form-check-input" disabled/>						                                                   			
				</div>
			</div>
			
			<div class="row d-flex">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
                   	<label for="TIPO_REPORTE1" class="form-check-label">Con EP: &nbsp;</label>
                   	<input type="checkbox" name="EP" id="EP" onclick="habilitar()" value="S" class="form-check-input"/>						                                                   			
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
                   	<label for="TIPO_REPORTE1" class="form-check-label">Por Coordinación: &nbsp;</label>
                   	<input type="checkbox" name="Coord" id="Coord" value="S" class="form-check-input" disabled/>						                                                   			
				</div>
			</div>		
			
			<div class="row d-flex">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">															
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="button" id="pbExcel" name="pbExcel" value="Extraer" class="btn btn-secondary btn-sm" onclick="enviaConsulta()"/>													
				</div>						
			</div>		

		</div>
	</form>

</body>
</html>


