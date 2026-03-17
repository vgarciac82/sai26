
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
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

	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(
			GestionInterface.ATT_CONEXION);
	int aEjercicioFiscal = Integer
			.parseInt(adbl.obtenEjercicioFiscal());
	System.out.println(aEjercicioFiscal);

	//Valida Centro de Costos
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}

	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	//cRamo="16";
	algo = usuario.getLogin();
	//de aki para arriba es de cajon
	
	// VGC20160617 Se agrega boton para limpieza del estado del ejercicio. RC-111
	boolean usrLimpiaEdoEjercicio = ( usuario.getPropiedad("BORRA_EDO_EJERCICIO") != null && "SI".equalsIgnoreCase( usuario.getPropiedad("BORRA_EDO_EJERCICIO").getValor() )  );
	
	String msg = "";
	boolean esRespuesta=false;
	if (session.getAttribute("msg") != null) {
		msg = (String) session.getAttribute("msg");
		session.removeAttribute("msg");
		esRespuesta = true;
	}
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
						
     var usrLimpiaEdoEjercicio = <%=usrLimpiaEdoEjercicio%>;
     var msg = "<%=msg%>";
     var esRespuesta = <%=esRespuesta%>;
     
$(document).ready(function() {	
	
	if( usrLimpiaEdoEjercicio )
		$("#limpiarDiv").show();
	
	$("#cCentroContable").val( "<%=cCentroContable%>" );
    $("#limpiarBtn").button().click(function(){
    	
    	limpiarEdoEjercicio();
    });
    
	$("#pbExcel").button();
	
	if(esRespuesta){
		Swal.fire({ icon: "succes",
					text: msg});
		//$("#msgDialog").dialog("open");
	}
	
});

	function openARCH(ext) {
		if (ext == "xls") {
			document.getElementById("limpiar").checked=false;
			document.ExportarForm.submit();
		}		
	}
	
	function limpiarEdoEjercicio(){		
		document.getElementById("limpiar").checked=true;		
		document.ExportarForm.submit();
	}
	
</script>

</head>
<body id="dt_example">
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReporteEstadoEjercicio" method="get">

		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /> 
		<input type="hidden" id="cCentroContable" name="cCentroContable" />

		<div id="container" style="width: 60%" class="container">			
			
			<div class="card-header"> <h3> Estado del Ejercicio </h3> </div>
			<hr class="mt-3"/>			
												
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-md-6 mb-3">										
					<label for="mes" class="form-label"> Mes de Consulta: </label>											
					<select id="mes" name="mes" class="form-select form-select-sm" style="width: 12em;">
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
				</div>																																										
			</div>
						
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-md-6 mb-3" >													
					<input type="button" id="pbExcel" name="pbExcel" value="Generar" class="btn btn-secondary btn-sm" onclick="openARCH('xls');"/>														
				</div>														
			</div>
			
			<select id="ejercicioFiscal" name="ejercicioFiscal" class="form-select form-select-sm" style="visibility: hidden">
				<option value=<%=aEjercicioFiscal%>> <%=aEjercicioFiscal%> </option>
			</select>
		
			<div style="display:none;" id="limpiarDiv" class="container">
				<!-- VGC20160617 Se agrega boton para limpieza del estado del ejercicio. -->
						
				<h5> Operaciones de Administracion </h5>
				<hr class="mt-3"/>
				
				<div class="row">
					<div class="col-12 col-md-6 mb-3">													
						<input type="button" id="limpiarBtn" name="limpiarBtn" value="Limpiar Edo del Ejercicio" class="btn btn-secondary btn-sm" onclick="extrae()"/>
						<input type="radio" name="limpiar" id="limpiar" value="limpiar" style="visibility: hidden"/>												
					</div>														
				</div>														
			
			</div>
			
			<br/>
			
		</div>						
	</form>

</body>
</html>


