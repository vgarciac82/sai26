
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>

<%
	boolean bAplicadoCont = false;
	String DATE_FORMAT = "yyyy/MM/dd";
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
<title>Reporte FFM</title>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>

<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
						
$(document).ready(function() {	

	$("#cCentroContable").val( "<%=cCentroContable%>");
	
	$("#cmdxlsReporte").button();
});

	function extrae(){	
		if ($("#cxp").val() == "" && $("#ep").val() == "") {
			Swal.fire({ icon: 'warning',			
						text: "Debes escribir al menos en una opción." });					
		}
		else{			
			document.ExportarForm.submit();
		}	
	}		

</script>

</head>
<body id="dt_example">
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReporteNomina" method="get" target="_blank">
		 	
		<input type="hidden" id="TIPO_REPORTE" name="TIPO_REPORTE" value="SCOMP"/>
		
		<div id="container" style="width: 90%" class="container">
			<div class="card-header"> <h3> Reporte de saldos en comprometido </h3> </div>
			
			<br/>
			<div class="row d-flex">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="cxp" class="form-label"> CxP Compromiso: </label>											
				</div>	
        		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="text" name="cxp" id="cxp" class="form-control form-control-sm" placeholder="10CO0000000000"/>
				</div>				
			</div>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="ep" class="form-label"> EP: </label>											
				</div>	
        		<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
					<input type="text" name="ep" id="ep" class="form-control form-control-sm"/>
				</div>				
			</div>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">															
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="button" class="btn btn-secondary btn-sm" id="cmdxlsReporte" name="cmdxlsReporte" value="Consultar" onclick="extrae()" />																		
				</div>						
			</div>	
															
		</div>					
	</form>

</body>
</html>


