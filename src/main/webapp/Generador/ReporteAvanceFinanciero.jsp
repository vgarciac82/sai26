
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

<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"> </script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"> </script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"> </script>

<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
						
	$(document).ready(function() {	
		
		$("#cCentroContable").val( "<%=cCentroContable%>");
		
		$("#pbExcel").button();
	});

	
	function openARCH(ext){
		if (ext == "xls"){
			if ($("#fechaf").val() == "") {
				alert("Seleccione el mes que desea trabajar.");
			}
			else{		
				$("#fecha_inicio").val($("#fechaf").val().split('-').reverse().join('/'));
				document.ExportarForm.submit();
			}
		}
	}		

</script>

</head>
<body id="dt_example">
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReporteAvanceFinanciero" method="get" target="_blank">

		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /> 
		<input type="hidden" id="cCentroContable" name="cCentroContable"/>
		<input type="hidden" id="fecha_inicio" name="fecha_inicio"/> 		 		

		<div id="container" class="container" style="width: 60%">
			
			<div class="card-header"> <h3> Avance Financiero </h3> </div>
			<hr class="mt-3"/>								
			
			<div id="masivo" >
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 ">
						<h6> <strong>Fecha</strong> </h6>
					</div>					
				</div>
				
				<div class="row d-flex justify-content-center">														
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 ">
						<div class="form-group">			                	
		                    <div class="input-group date" id="datepicker1">
		                    	<input type="date" class="form-control form-control-sm" id="fechaf" name="fechaf"/>                                    
		                    </div>
		                </div>										
					</div>				
				</div>			
				
				<br/>
							
				<div class="row">
					<div class="col-12 mb-3 d-flex justify-content-center">
						<input type="button" id="pbExcel" name="pbExcel" value="Generar" class="btn btn-secondary btn-sm" onclick="openARCH('xls');"/>
					</div>
				</div>
								
			</div>		
		</div>
	</form>

</body>
</html>


