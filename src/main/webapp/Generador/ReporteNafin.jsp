
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
		id_oper = new Integer(request.getParameter("id_oper")).intValue();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
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
	algo = usuario.getLogin();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Reporte NAFIN</title>

<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<script type="text/javascript" src="js/jquery-3.5.1.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" charset="utf-8">
						
$(document).ready(function() {	
	
	inicio();
	
	$("#cmdReportesNafin").button();
	$("#cmdFinalizaProv").button();
	$("#cmdFinPagos").button();
	$("#reporteTipo").val("Proveedores");
});


	function inicio(){
		
		queryFormPost("consultaFolioNafin", {async: false});
		
	    $("#fecha_inicio").val(moment().format('yyyy-01-01'));
		$("#fecha_fin").val(moment().format('yyyy-MM-DD'));
		
		$("#checkgroup").css('display', 'none');
		var folioGroup = document.getElementById('folioGroup');
	    var classes = folioGroup.className;

	    if (classes.indexOf('d-none') === -1) {
	      folioGroup.className += ' d-none';
	    }
	}
	
	function listareportes(rep){
		var folioGroup = document.getElementById('folioGroup');
	    var classes = folioGroup.className;
	    
		if (rep == 1) {
			$("#reporteTipo").val("Proveedores");
			$("#checkgroup").css('display', 'none');
		    
			if (classes.indexOf('d-none') === -1) {
		      folioGroup.className += ' d-none';
		    }
			
		} else if (rep == 2) {
			$("#reporteTipo").val("Pagos");
			$("#checkgroup").css('display', 'block');
			
			if (classes.indexOf('d-none') !== -1) {
			      folioGroup.className = classes.replace('d-none', '').trim();
			}
		}	
	}
	
	function extrae(){
		if (($("#fDesde").val() != "" && $("#fHasta").val() != "")) 
		{
			if ($('#check_excel').prop('checked')) {
				$("#tipoExtraccion").val("Excel");
			
			} else 
				$("#tipoExtraccion").val("txt");
				
			document.ExportarForm.submit();
			
		} else {
				
				Swal.fire("Capture","Favor de llenar la fecha de Inicio y la fecha Final del reporte.","info");
		}
		
	}
	
	function guardar() {
		
		queryFormPost("actualizarFolioNafin", {async: false});
		Swal.fire("OK","Se actualizó el folio con éxito.","success");
		
	}
	
	function finProveedor() {
		$.ajax({
				url : '../reportes/FinalizarProveedor',
				dataType : 'json',
				type :"POST",
				data : {
					"reporte": "Proveedor"
				},
				async : false,
				success : function(json) {
					if( json.valueOf()=="success"){
						Swal.fire("OK","Se finalizo el alta de proveedores NAFIN correctamente","success");
					}
				},
				error : function (){
					Swal.fire("No se finalizo el proceso", "Favor de reportarlo con el administrador del sistema.","warning");
						
					return false;}
			});
	}		

	function finPagos() {
		$.ajax({
				url : '../reportes/FinalizarProveedor',
				dataType : 'json',
				type :"POST",
				data : {
					"reporte": "Pagos",
					fecha_inicio: $("#fecha_inicio").val(),
					fecha_fin: $("#fecha_fin").val()
				},
				async : false,
				success : function(json) {
					if( json.valueOf()=="success"){
						Swal.fire("OK","Se finalizo el alta de pagos correctamente","success");
					}
				},
				error : function (){
					Swal.fire("No se finalizo el proceso de pagos", "Favor de reportarlo con el administrador del sistema.","warning");
						
					return false;}
			});
	}		

</script>

</head>
<body id="dt_example">
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReporteNafin" method="get" target="_blank">
		<input type="hidden" id="reporteTipo" name="reporteTipo" value="tipoReporte"/>
		<input type="hidden" id="cUR" name="cUR" />
		<input type="hidden" id="tipoExtraccion" name="tipoExtraccion"  value ="txt"/>
		
		<div id="container" style="width: 90%" class="container SyCData">
	
	  		<div class="card-header"> <h3> Generar layout para carga en sistema de Nacional Financiera (NAFIN) </h3> </div>

		  		
		  	<div class="card-body">
		  		
		  		<div align="center">
			  		<div class="form-check form-check-inline">
					  <input type="radio" name="tipoReporte" id="Proveedores" onclick="listareportes(1)" value = "Proveedores" checked/> 
					  <label class="custom-control-label" for="Proveedores">Alta Proveedores</label>
					</div>
					<div class="form-check form-check-inline">
					  <input type="radio" name="tipoReporte" id="Pagos" onclick="listareportes(2)" value ="Pagos"/> 
					  <label class="custom-control-label" for="Pagos">Pagos a proveedor</label>
					</div>
				</div>
				<br>
				<div class="row">
					  	<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					  	</div>
					    <div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					      <label class="custom-control-label" for="fecha_inicio">Desde:</label>
					    </div>
					    <div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					      <input type="date" name="fecha_inicio" id="fecha_inicio" class="form-control form-control-sm"/>
					    </div>
				</div>
				<div class="row">
				  	<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
				  	</div>
				    <div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				      	<label class="custom-control-label" for="fecha_fin"> Hasta:</label>
				    </div>
				    <div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				      <input type="date" name="fecha_fin" id="fecha_fin"  class="form-control form-control-sm"/>
				    </div>
				</div>
			<br>	
				<div class="d-flex justify-content-center">
				  <div class="d-flex flex-column">
				    <div class="input-group" id="folioGroup">
				      <div class="input-group-prepend">
				        <span class="input-group-text" id="consecutivo">Última factura</span>
				      </div>
				      <input type="number" name="numPago" id="numPago" class="form-control form-control-sm"/>
				      <div class="input-group-append">
				        <input type="button" id="btnGuardar" name="btnGuardar" class="btn btn-outline-secondary" value="Actualizar" onclick="guardar()"/>
				      </div>
				    </div>
				  </div>
				</div>
			</div>
			<div class="d-flex justify-content-center">
				<div class="d-flex flex-column">
					<div class="input-group mb-3" id="checkgroup">
					  <div class="input-group-prepend">
					    <div class="input-group-text">
					     <input id="check_excel" name="check_excel" type="checkbox" />
					      <label>En excel</label>
					    </div>
					  </div>
					</div>
				</div>
				<div class="d-flex flex-column"
					<div class="d-flex flex-column">
						<input id="cmdReportesNafin" name="cmdReportesNafin" value="Extraer archivo" type="button" class="btn btn-primary" onclick="extrae()"/>
					</div>
				</div>
				<div class="mt-4 d-flex justify-content-center">
					<input id="cmdFinalizaProv" name="cmdFinalizaProv" value="Finalizar Proveedores" type="button" class="btn btn-secondary" onclick="finProveedor()"/>	
				</div>
				<div class="mt-4 d-flex justify-content-center">
					<input id="cmdFinPagos" name="cmdFinPagos" value="   Finalizar Pagos  " type="button" class="btn btn-dark" onclick="finPagos()"/>	
				</div>
			</div>
		</div>	
		</div>																							
	</form>

</body>
</html>


