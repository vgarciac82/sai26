
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.poi.util.StringUtil"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@ page 
	import="java.util.Calendar"
	import="com.syc.gestion.servlet.GestionInterface"
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
 
	if (u == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
		
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	System.out.println(efa);	
%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Reporte Avance Finaciero PPI</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"> </script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>

<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">

$(document).ready(function() {
			
		cssReadOnly();
		
		$("#procBtn").button();
	}); 

	function cssReadOnly(){
		$( "[readOnly]" ).each(
			function(){	
				$(this).addClass("notEditable");	
		});
	}

	function extrae(){ //OK	
		$("#fecha_fin").val($("#fechaf").val().split('-').reverse().join('/'));
		document.formCFDI.submit();
	}
	
</script>

</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
<br/>
	<form action="../reportes/ReporteSipot" method="post" id="formCFDI" target="_blank">
		<input type="hidden" id="TIPO_REPORTE" name="TIPO_REPORTE" value="AvanceFinancieroProg">
		<input type="hidden" id="nReporte" name="nReporte" value="0">
		<input type="hidden" id="nombre3" name="nombre3" value="0">
		<input type="hidden" id="cargo3" name="cargo3" value="0">
		<input type="hidden" id="nReporte" name="nReporte" value="0">
		<input type="hidden" id="fecha_fin" name="fecha_fin" value="">
		
		<div id="container" class="container" style="width: 60%">
		
			<div class="card-header"> <h3>Reporte de avance financiero de proyectos de inversion</h3> </div>
			<hr class="mt-3"/>
				
			<h5> Firmantes </h5>
			<hr class="mt-3"/>	
			
			<div class="row">
				<div class="col-12 d-flex justify-content-center">
					<label for="sinFirmas" class="form-check-label"><strong>Firma Elabor&oacute;/Revis&oacute;</strong></label>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1" id="divnombre1" title="Firma1">								
					<label id="nombre1"> Nombre&nbsp; </label> 							 							
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1" id="divcargo1" title="DivCargo1">								
					<label id="cargo1"> Cargo&nbsp; </label> 															
				</div>
			</div>
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1" id="divnombre1" title="Firma1">															 
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="nombre1" name="nombre1" class="form-control form-control-sm" maxlength="200" value="Luz María Muñoz Santos" />
					</div> 								
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1" id="divcargo1" title="DivCargo1">															 
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
						<input type="text" id="cargo1" name="cargo1" class="form-control form-control-sm" maxlength="200" value="Subgerencia de Programación, Control y Seguimiento Presupuestal" />
					</div>								
				</div>
			</div>

			<div class="row">
				<div class="col-12 d-flex justify-content-center">
					<label for="sinFirmas" class="form-check-label"><strong>Firma Autoriz&oacute;</strong></label>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<label id="nombre2"> Nombre&nbsp; </label> 
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<label id="cargo2"> Cargo&nbsp; </label>
				</div>
			</div>
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="nombre2" name="nombre2" class="form-control form-control-sm" maxlength="200" value="Tania Ananí Limón Magaña" />
					</div> 
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
						<input type="text" id="cargo2" name="cargo2" class="form-control form-control-sm" maxlength="200" value="Gerencia de Programación y Presupuesto" />
					</div>
				</div>
			</div>
			
			<br/>
			<br/>
				
			<h5> Datos generales </h5>
			<hr class="mt-3"/> 
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 ">
					<h6> <strong>Fecha</strong> </h6>
				</div>					
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
				</div>														
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
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 " >
					<div class="input-group">
						<label for="excel" class="form-check-label"><strong>Generar Excel</strong></label>&nbsp;&nbsp;
						<input type="checkbox" name="excel" id="excel" class="form-check-input" value="excel"/>
					</div>																																			
				</div>													
			</div>
			
			<br/>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					<input type="submit" id="procBtn" name="procBtn" value="Generar Reporte" class="btn btn-secondary btn-sm" onclick="extrae()"/>
				</div>
			</div>		
		</div>
	</form>
</body>

</html>