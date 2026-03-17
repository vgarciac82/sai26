
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
	System.out.println(today);	
	
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
			
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	System.out.println(efa);	
	
	String finicial = "01/01/" + efa;
	System.out.println(finicial);	
	
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
	//finicial = usuario.getPropiedad("aEjercicioFiscal").getValor();
	
	
	//Valida Centro de Costos
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();		
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}

	cUR = usuario.getU_UR();	
	cRamo = usuario.getU_Ramo();
	//cRamo="16";
	algo = usuario.getLogin();
	//de aki para arriba es de cajon
			
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Reporte de Ingresos</title>

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
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
						
	$(document).ready(function() {	
		
		$("#cCentroContable").val( "<%=cCentroContable%>");	
	    $("#limpiarBtn").button();
	    
	    $("#fechaI").val(moment().format('01-01-yyyy'));
		$("#fechaF").val(moment().format('MM-DD-yyyy'));
		
		$("#cmdxlsReporte").button();
		
	});	
	
	function extrae(){	
		if($("#fechaI").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Debes seleccinar una fecha de Inicio."});
			return false;
		}
		
		$("#fecha_inicio").val($("#fechaI").val().split('-').reverse().join('/'));
					
		document.ExportarForm.submit();				
	}
		
</script>

</head>
<body id="dt_example">
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReportesGreenMex" method="get">	

		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>"/> 
		<input type="hidden" id="cCentroContable" name="cCentroContable"/>
		<input type="hidden" id="fecha_inicio" name="fecha_inicio"/>
		<input type="hidden" id="Nombre_Reporte" name="Nombre_Reporte" value="FINANCIEROGREENMEX"/>
				
		<div id="container" style="width: 60%" class="container">
			
			<div class="card-header"> <h3> Reporte Financiero de GreenMex </h3> </div>
			<hr class="mt-3"/>			
									
			<div class="row">				
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="form-group">
                       <label for="fecha_inicio">Fecha:</label>                       
                	</div>										
				</div>		
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="form-group">                       
                       <div class="input-group date" id="datepicker1">
                          <input type="date" class="form-control form-control-sm" id="fechaI" name="fechaI" value="<%=finicial%>"/>                                    
                       </div>
                	</div>										
				</div>		
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="button" id="cmdxlsReporte" name="cmdxlsReporte" value="Extraer" class="btn btn-secondary btn-sm" onclick="extrae()"/>
				</div>
			</div>
					
		</div>					
												
	</form>		
</body>
</html>


