
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
<title>Reporte Ejercido 33401 Capacitacion</title>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css"/>

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
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
						
	$(document).ready(function() {	
		$("#cCentroContable").val( "<%=cCentroContable%>");			
		$("#cmdxlsReporteProg").button();
		$("#fecha_fin").val(moment().format('yyyy-MM-DD'));
	});

	function extrae(){		
		var existeEdoEjercicio = false;
		queryFormPost("existeEdoEjercicioRead", {async : false});
		
		if($("#existeEdoEjercicio").val() == "1"){
			document.ExportarForm.submit();
		} else {
			Swal.fire({ icon: 'info',
				text: "Para conusltar es necesario que exista informacion en el Estado del Ejercio del mes " + $("#mes").val() + ". Favor de contactar al area de Informacion Financiera." });				
		}
	}		

	function habilita(){
		if ( $("#TIPO_REPORTE").prop("checked")) {
		 document.getElementById("Simp").disabled = false;
		 document.getElementById("Det").disabled = false;
		 }
		 else{
		 document.getElementById("Simp").disabled = true;
		 document.getElementById("Det").disabled = true;
		 }
		 
	}	
</script>

</head>
<body id="dt_example">
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReportePolizas" method="get" target="_blank">
		<input type="hidden" id="TIPO_REPORTE" name="TIPO_REPORTE" value="CAPACITACION"/>	
		<input type="hidden" id="existeEdoEjercicio" name="existeEdoEjercicio" value=""/>
		
		<div id="container" style="width: 80%" class="container">
			<div class="card-header"> <h3> Reporte Ejercido Capacitación </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex justify-content-center">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex justify-content-center">
					<div class="form-group">
						<label for="mes">Selecciona el Mes: &nbsp;&nbsp;</label>			
						<div class="input-group date" id="datepicker1">									
							<select id="mes" name="mes" class="form-select form-select-sm">
								<option value="1">Enero</option>
								<option value="2">Febrero</option>
								<option value="3">Marzo</option>
								<option value="4">Abril</option>
								<option value="5">Mayo</option>
								<option value="6">Junio</option>
								<option value="7">Julio</option>
								<option value="8">Agosto</option>
								<option value="9">Septiembre</option>
								<option value="10">Octubre</option>
								<option value="11">Noviembre</option>
								<option value="12">Diciembre</option>
							</select>
							<select id="ejercicioFiscal" name="ejercicioFiscal" class="form-select form-select-sm" style="visibility: hidden">
								<option value=<%=efa%>> <%=efa%> </option>
							</select>
					    </div>   				
					</div>	
				</div>				
			</div>	
			
			<br/>
			
			<div class="row">
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex justify-content-center">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex justify-content-center">
					<input id="cmdxlsReporteProg"  name="cmdxlsReporteProg" value="Extraer" type="button" class="btn btn-secondary btn-sm" onclick="extrae()" />
				</div>
			</div>
											
		</div>
	</form>

</body>
</html>


