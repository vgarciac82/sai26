
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
<title>Libro Diario</title>

<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css"></link>

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

</script>

</head>
<body id="dt_example">
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReporteLibros" method="get">

		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /> 
		<input type="hidden" id="tipo_reporte" name="tipo_reporte" value="libroDiario.jasper" /> 
		<input type="hidden" id="cCentroContable" name="cCentroContable"/> 		
		<input type="hidden" id="fecha_inicio" name="fecha_inicio"/>
		
		<div id="container" style="width: 80%" class="container">
			
			<div class="card-header"> <h3>Libro Diario</h3> </div>
			<hr class="mt-3"/>														
			
			<div class="row">					
				<div class="col-12 mb-3 d-flex justify-content-center">
					<div class="form-group">
	                       <label for="fecha">Mes de consulta:</label>
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
			
			<br/>
		
			<h5> Firmas </h5>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<label id="nombre1"> Nombre Elaboró </label>
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					Cargo Elaboró						
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
			</div>			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="nombre1" name="nombre1" class="form-control form-control-sm" style="width: 20em;" maxlength="200" value="M.A.T. ADANELY LÓPEZ GONZÁLEZ" />
					</div> 
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
						<input type="text" id="cargo1" name="cargo1" class="form-control form-control-sm" style="width: 20em;" maxlength="200" value="JEFATURA DE DEPARTAMENTO" />
					</div>						
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					Nombre Revisó 
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					Cargo Revisó
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
			</div>			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="nombre2" name="nombre2" class="form-control form-control-sm" style="width: 20em;" maxlength="200" value="L.C.P. GABRIELA GÓMEZ RUVALCABA" />
					</div> 
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
						<input type="text" id="cargo2" name="cargo2" class="form-control form-control-sm" style="width: 20em;" maxlength="200" value="SUBGERENTE DE CONTABILIDAD" />
					</div>
				</div>							
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					Nombre Autorizó 
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					Cargo Autorizó
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
			</div>
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="nombre3" name="nombre3" class="form-control form-control-sm" style="width: 20em;" maxlength="200" value="M.B.A. TANIA ANANÍ LIMÓN MAGAÑA" />
					</div> 
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
						<input type="text" id="cargo3" name="cargo3" class="form-control form-control-sm" style="width: 20em;" maxlength="200" value="GERENCIA DE PROGRAMACIÓN Y PRESUPUESTO" />
					</div>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					Nombre Autorizó 
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					Cargo Autorizó											
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
			</div>	
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="nombre4" name="nombre4" class="form-control form-control-sm" style="width: 20em;" maxlength="200" value="ALMA GUADALUPE GODOY RAMOS" readOnly />
					</div> 
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">				
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
						<input type="text" id="cargo4" name="cargo4" class="form-control form-control-sm" style="width: 20em;" maxlength="200" value="UNIDAD DE ADMINISTRACIÓN Y FINANZAS" readOnly />&nbsp;
					</div>	
					<input type="checkbox" name="chk_firmas" id="chk_firmas" onclick="firmass()" value="4"/>						
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
			</div>
		</div>			
	</form>

</body>
</html>


