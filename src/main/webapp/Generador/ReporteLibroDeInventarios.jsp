
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.sai.contabilidad.caja.CajaBusinessLogic"%>
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
	if (session.getAttribute("RESULT") != null) {
		mensaje = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}

	//Valida Centro de Costos
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}
	
	String fAplicacion[] = CajaBusinessLogic.readfAplicacion(cCentroContable, cUR);
	
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
<title>Libro de Inventarios</title>

<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
						
$(document).ready(function() {	
	
	$("#cCentroContable").val( "<%=cCentroContable%>");	
	
	$("#generarPdf").button();	
	
	$("#enviar").button().click(function() {
			if (validacionCorrecta()) {
				
				queryFormPost("consultaMesCargado", {async: false });
		
				if($("#total").val() != "0"){					
					if(!confirm("Ya se encuentra cargado el trimestre seleccionado. ¿Desea continuar para remplazar la infomación? ")){
						return;
					}																						
				}
				
				$("#FormUpload").submit();
			}
		});
		
	var mensaje = "<%=mensaje%>";
	if (mensaje != "")
		Swal.fire({ icon: "succes",
					text: mensaje});

});	
	
	function openARCH(ext){				
		$("#fecha_inicio").val($("#fecha").val().split('-').reverse().join('/'));			
		document.ExportarForm.submit();			
	}		
	
	function firmass(){
		if ($("#chk_firmas").attr("checked")){
			document.getElementById("nombre4").readOnly = false;
			document.getElementById("cargo4").readOnly = false;
			$("#nombre4").removeClass("notEditable");
			$("#cargo4").removeClass("notEditable");			
			$("#nombre4").addClass("Editable");
			$("#cargo4").addClass("Editable");
		}else{
			$("#nombre4").attr("readonly",true);
			$("#cargo4").attr("readonly",true);
			$("#nombre4").removeClass("Editable");
			$("#cargo4").removeClass("Editable");
			$("#nombre4").addClass("notEditable");
			$("#cargo4").addClass("notEditable");
		}
	}

	function validacionCorrecta() {
		var msg = "";
		var tkn = "";
		if ($("#archivoSubir").val() == "") {
			msg += tkn + "Archivo a cargar.";
		}
							
		if (msg != "") {
			Swal.fire({ icon: "warning",
						text: "Para continuar debe seleccionar un archivo a cargar"});			
			return false;
		}			

		return true;
	}
	
</script>

</head>
<body id="dt_example">
<br/>
	<form id="FormUpload" name="FormUpload" method="post" action="../CargaGermoplasmaServlet" enctype="multipart/form-data">
		<div id="container" class="container" style="width: 80%">
		<input type="hidden" name="total" id="total" />
						
			<div class="card-header"> <h3> Libro de Inventarios </h3> </div>
			<hr class="mt-3"/>
			
			<h6> Cargar Archivo de Germoplasma </h6> 
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12 mb-3 d-flex justify-content-center" >
					<label for="archivoSubir"></label>
			  		<input class="form-control form-control-sm" type="file" id="archivoSubir" name="archivoSubir" style="width: 25em;" />
				</div>
			</div>
						 
			<div class="row">
				<div class="col-12 mb-3 d-flex justify-content-center">	
					<div class="form-group">									
						<label for="mes" class="form-label"><strong> Seleccione el trimestre: </strong></label>											
						<select id="mes" name="mes" class="form-select form-select-sm" style="width: 15em;">
							<option value = "3"> Enero - Marzo </option>
							<option value = "6"> Abril - Junio </option>
							<option value = "9"> Julio - Septiembre </option>
							<option value = "12"> Octubre - Diciembre </option>	
						</select>				
					</div>																									
				</div>																																										
			</div>
			
			<div class="row">
				<div class="col-12 mb-3 d-flex justify-content-center" >
					<input type="button" id="enviar" name="enviar" value="Cargar" class="btn btn-secondary btn-sm"/>
				</div>
			</div>

		</div>		
	</form>

	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReporteLibros" method="get">

		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /> 
		<input type="hidden" id="tipo_reporte" name="tipo_reporte" value="libroDeInventarios.jasper" /> 
		<input type="hidden" id="cCentroContable" name="cCentroContable"/> 	
		<input type="hidden" id="fecha_inicio" name="fecha_inicio"/>	
		
		<div id="container" class="container" style="width: 50%">
					
			<h6> Reporte </h6>
			<hr class="mt-3"/>
							
			<div class="row">					
				<div class="col-12 mb-3 d-flex justify-content-center">
					<div class="form-group">
	                    <label for="fecha"><strong> Mes de consulta: </strong></label>
	                    <div class="input-group date" id="datepicker1">
	                    	<input type="date" class="form-control form-control-sm" id="fecha" name="fecha"/>		                                                             
	           			</div>
	                </div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 mb-3 d-flex justify-content-center" >
					<input type="button" id="generarPdf" name="generarPdf" value="Generar" class="btn btn-secondary btn-sm" onclick="openARCH('pdf')"/>
				</div>
			</div>	
							
		</div>	
		
	</form>

</body>
</html>


