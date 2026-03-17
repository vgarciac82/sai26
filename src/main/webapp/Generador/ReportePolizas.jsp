
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
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
						
$(document).ready(function() {	

	/*querySelectPost("readCentroContable", "cCentroContable", {
					async : false
				});
	*/
	$("#fechaI").val(moment().format('yyyy-01-01'));
	$("#fechaF").val(moment().format('yyyy-MM-DD'));
	
	$("#cCentroContable").val( "<%=cCentroContable%>");
	$("#cUR").val( "<%=cUR%>");
	var unidad = $("#cUR").val();
	
	if (unidad.substring(0,1)== "G")
		querySelectPost("cUnidadResponsableVistas", "cUnidadResponsable", {async: false });
	else
		querySelectPost("cUnidadEjecutoraAENSSRead", "cUnidadResponsable", {async: false });

	$("#cmdxlsReporteProg").button();
});

	function extrae(){
		if ($("#fechaI").val() == "" || $("#fechaF").val() == "") {
			alert("Los rangos de fechas son obligatorios.");
		}
		else{		
			$("#fecha_inicio").val($("#fechaI").val().split('-').reverse().join('/'));
			$("#fecha_fin").val($("#fechaF").val().split('-').reverse().join('/'));
			document.ExportarForm.submit();
		}	
	}		

	function habilita(){
		if ( $("#TIPO_REPORTE").prop("checked")) {
		 document.getElementById("Simp").disabled = false;
		 document.getElementById("Det").disabled = false;
		 //document.getElementById("TIPO_REPORTE").value  = "1";
		 }
		 else{
		 document.getElementById("Simp").disabled = true;
		 document.getElementById("Det").disabled = true;
		 //document.getElementById("TIPO_REPORTE").value  = "0";
		 }
		 
	}	
	
</script>

</head>
<body id="dt_example">
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReportePolizas" method="get" target="_blank">
		<!-- <input type="hidden" id="cCentroContable" name="cCentroContable"/> --> 		
		<input type="hidden" id="cUR" name="cUR"/>
		<input type="hidden" id="fecha_inicio" name="fecha_inicio"/>
		<input type="hidden" id="fecha_fin" name="fecha_fin"/>
		
		<div id="container" style="width: 80%" class="container">
		
			<div class="card-header"> <h3> Reporte de Polizas </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																	
				</div>
	        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="cCentroContable" class="form-label"> Centro Contable:&nbsp; </label>												
				</div>				
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">
					<select name = "cCentroContable" id = "cCentroContable" class="form-select form-select-sm">
						<option value="00">Todas las Unidades</option>
						<option value="10">Oficinas Centrales</option>
						<option value="11">GERENCIA ESTATAL AGUASCALIENTES</option>
						<option value="12">GERENCIA ESTATAL BAJA CALIFORNIA</option>
						<option value="13">GERENCIA ESTATAL BAJA CALIFORNIA SUR</option>
						<option value="14">GERENCIA ESTATAL CAMPECHE</option>
						<option value="15">GERENCIA ESTATAL COAHUILA</option>
						<option value="16">GERENCIA ESTATAL COLIMA</option>
						<option value="17">GERENCIA ESTATAL CHIAPAS</option>
						<option value="18">GERENCIA ESTATAL CHIHUAHUA</option>
						<option value="19">GERENCIA ESTATAL DISTRITO FEDERAL</option>
						<option value="20">GERENCIA ESTATAL DURANGO</option>
						<option value="21">GERENCIA ESTATAL GUANAJUATO</option>
						<option value="22">GERENCIA ESTATAL GUERRERO</option>
						<option value="23">GERENCIA ESTATAL HIDALGO</option>
						<option value="24">GERENCIA ESTATAL JALISCO</option>
						<option value="25">GERENCIA ESTATAL MEXICO</option>
						<option value="26">GERENCIA ESTATAL MICHOACAN</option>
						<option value="27">GERENCIA ESTATAL MORELOS</option>
						<option value="28">GERENCIA ESTATAL NAYARIT</option>
						<option value="29">GERENCIA ESTATAL NUEVO LEON</option>
						<option value="30">GERENCIA ESTATAL OAXACA</option>
						<option value="31">GERENCIA ESTATAL PUEBLA</option>
						<option value="32">GERENCIA ESTATAL QUERETARO</option>
						<option value="33">GERENCIA ESTATAL QUINTANA ROO</option>
						<option value="34">GERENCIA ESTATAL SAN LUIS POTOSI</option>
						<option value="35">GERENCIA ESTATAL SINALOA</option>
						<option value="36">GERENCIA ESTATAL SONORA</option>
						<option value="37">GERENCIA ESTATAL TABASCO</option>
						<option value="38">GERENCIA ESTATAL TAMAULIPAS</option>
						<option value="39">GERENCIA ESTATAL TLAXCALA</option>
						<option value="40">GERENCIA ESTATAL VERACRUZ</option>
						<option value="41">GERENCIA ESTATAL YUCATAN</option>
						<option value="42">GERENCIA ESTATAL ZACATECAS</option>
					</select>	
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																	
				</div>
	        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="cUnidadResponsable" class="form-label"> Unidad Responsable: </label>												
				</div>				
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">
					<select name = "cUnidadResponsable" id = "cUnidadResponsable" class="form-select form-select-sm">
					</select>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																	
				</div>
	        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="tipoPoliza" class="form-label"> Tipo Póliza: </label>												
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<select name = "tipoPoliza" id = "tipoPoliza" class="form-select form-select-sm">
						<option value= "00">Todas</option>
						<option value= "DI">Diario</option>
						<option value= "EG">Egreso</option>
						<option value= "IN">Ingreso</option>	
					</select>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																	
				</div>
	        	<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="tipoPoliza" class="form-label"> Solo Cap. 5 y 6: </label>												
				</div>	
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">
					<input type="checkbox" id="TIPO_REPORTE" name="TIPO_REPORTE" class="form-check-input" value="0" onclick="habilita()"/> &nbsp; &nbsp;						
					<input type="radio" id="Simp" name="Detalle" value="false" class="form-check-input" disabled checked/> Simplificado &nbsp;
					<input type="radio" id="Det" name="Detalle" value="true" class="form-check-input" disabled/> Detallado
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																	
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="tipoPoliza" class="form-label"> Desde: </label>												
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<div class="form-group">
                       <div class="input-group date" id="datepicker1">
                          <input type="date" class="form-control form-control-sm" id="fechaI" name="fechaI"/>                                    
                       </div>
                	</div>												
				</div>	        				
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																	
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<label for="tipoPoliza" class="form-label"> Hasta: </label>												
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<div class="form-group">	                      
                       <div class="input-group date" id="datepicker1">
                          <input type="date" class="form-control form-control-sm" id="fechaF" name="fechaF"/>                                    
                       </div>
                	</div>										
				</div>			        				
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																	
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																	
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
					<input type="button" id="cmdxlsReporteProg" name="cmdxlsReporteProg" value="Extraer" class="btn btn-secondary btn-sm" onclick="extrae()"/>																											
				</div>						
			</div>
			
			<br/>
						
		</div>
	</form>
</body>
</html>


