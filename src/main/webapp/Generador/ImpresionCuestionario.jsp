
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
<title>Reimpresion cuentionario 15D</title>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/crud.js"></script>

<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
						
	$(document).ready(function() {	
		$("#imprimir").button();
	});

		function cmdImprimir() {
		
		if($("#cIdSolicitud").val()== "")
			Swal.fire({ icon: 'warning',
						text: "No debe estar vacio el folio de la solicitud." });			
		else {
			window.open(
				"../admin/SeguridadCatalogos?"
				+ "catalogo=CONTRARECIBO"
				+ "&accion=run"
				+ "&rn=ReporteCuestionarioFirma.jasper"
				+ "&cIdSolicitud= '" + $("#cIdSolicitud").val() + "'", 			
				"popacuse",
				"scrollbars=1, resizable=yes, width=1024, height=768");
			}
	}
			
</script>

</head>
<body id="dt_example">
<br/>
	<form id="ExportarForm" name="ExportarForm"  target="_blank">

		<div id="container" style="width: 80%" class="container" >
			<div class="card-header"> <h3> Reimpresión cuestionario 15D </h3> </div>
			<hr class="mt-3"/>
			
			<div id="por_solicitud" class="container">
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						<label for="solicitud" class="form-check-label">Folio de solicitud de requisición</label>												
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="text" name="cIdSolicitud" id="cIdSolicitud" class="form-control form-control-sm" placeholder="RS-A02-1"/>												
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 d-flex justify-content-center">
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							<button type="button" id="imprimir" name="imprimir" class="btn btn-secondary btn-sm" onclick="cmdImprimir()">Imprimir</button>							
						</div>
					</div>
					<br/>									
				</div>																		
			</div>				
		</div>
	</form>

</body>
</html>


