<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.poi.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@ page 
	import="java.util.Calendar"
	import="com.syc.gestion.core.Usuario"
	import="com.syc.gestion.servlet.GestionInterface"
%>

<%@ page import="java.util.*,java.sql.*,com.syc.gestion.*,com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.Util" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String login=usuario.getLogin();
	String cCentroContable=(usuario.getPropiedad("CCENTROCONTABLE") != null? (usuario.getPropiedad("CCENTROCONTABLE").getValor() != null? usuario.getPropiedad("CCENTROCONTABLE").getValor():"" ):"" );
	
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	System.out.println(efa);	

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>FormatoSIPOT</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css">
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"> </script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript">
	
	$(document).ready(function() {
		$("#excel").button();
	
		//Inicializa las ayudas. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		//$("input.AyudaSyC").subIniciaDlg();
		//Inicializa el autocompletar. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		//$("input.autoCompletaSyC").subIniciaAutoCompleta();
		queryFormPost("readFirmantesReportesConac", {async: false});
		queryFormPost("descCentroContableRead", {async: false});
		$("#reporte1").click();
		cssReadOnly();		
	
	});
	
	function cssReadOnly(){
		$( "[readOnly]" ).each(
			function(){	
				$(this).addClass("notEditable");	
		});
	}
	
	function listareportes(rep){	
		
		$("#nReporte").val(rep);
							
			if (rep===1){
				$("#TIPO_REPORTE").val("FLUJOCA");
			}else if (rep===2){
				$("#TIPO_REPORTE").val("FLUJOOBGT");
			}else if (rep===3){
				$("#TIPO_REPORTE").val("Flujoecon");
			}else if (rep===4){
				$("#TIPO_REPORTE").val("FLUJOFUNC");
			}else if (rep===5){
				$("#TIPO_REPORTE").val("FLUJOPROG");
			}else if (rep===6){
				$("#TIPO_REPORTE").val("FLUJOOBGTCE");
			}else if (rep===7){
				$("#TIPO_REPORTE").val("FLUJOCFPE");
			}else if (rep===8){
				$("#TIPO_REPORTE").val("FLUJOEFE");
			}else if (rep===9){
				$("#TIPO_REPORTE").val("FLUJOIFE");
			}else if (rep===10){
				$("#TIPO_REPORTE").val("ANALITING");
			}else if (rep===11){
				$("#TIPO_REPORTE").val("FLUJOCAA");
			}else if (rep===12){
				$("#TIPO_REPORTE").val("FLUJOCFPEA");
			}else if (rep===14){
				$("#TIPO_REPORTE").val("ANALITING2");
		}
	}
	
	function extrae(){	
		$("#fecha_fin").val($("#fecha").val().split('-').reverse().join('/'));
		document.FormFormatoSipot.submit();		
	}

</script>

</head>
<body id="dt_example">
<br/>
	<form id="FormFormatoSipot" name="FormFormatoSipot" action="../reportes/ReporteSipot" method="post" target="blank">
		<input type="hidden" id="TIPO_REPORTE" name="TIPO_REPORTE" value="FLUJOCA">
		<input type="hidden" id="imprimeCaracteristicas" name="imprimeCaracteristicas" value="1">
		<input type="hidden" id="fecha_fin" name="fecha_fin">
		<input type="hidden" id="nReporte" name="nReporte" value="1">
		
		<div id="container" class="container" style="width: 80%">
			
			<div class="card-header"> <h3> Estado Analítico del Ejercicio del Presupuesto de Egresos </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">																						
					<input type="radio" name="reporte" id="reporte1" class="form-check-input" onclick="listareportes(1)" value="FLUJOCA" checked/>
					<label for="reporte1" class="form-check-label">Clasificación Administrativa Armonizada</label>																
				</div>
			</div>				
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">											
					<input type="radio" name="reporte" id="reporte11" class="form-check-input" onclick="listareportes(11)" value="FLUJOCAA"/>
					<label for="reporte11" class="form-check-label">Clasificación Administrativa</label>												
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">										
					<input type="radio" name="reporte" id="reporte2" class="form-check-input" onclick="listareportes(2)" value="FLUJOOBGT"/>
					<label for="reporte2" class="form-check-label">Clasificación por Objeto del Gasto</label>											
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">										
					<input type="radio" name="reporte" id="reporte3" class="form-check-input" onclick="listareportes(3)" value="Flujoecon"/>
					<label for="reporte3" class="form-check-label">Clasificación Económica</label>												
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">										
					<input type="radio" name="reporte" id="reporte4" class="form-check-input" onclick="listareportes(4)" value="FLUJOFUNC"/>
					<label for="reporte4" class="form-check-label">Clasificación Funcional (Finalidad y Función)</label>												
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">										
					<input type="radio" name="reporte" id="reporte5" class="form-check-input" onclick="listareportes(5)" value="FLUJOPROG"/>
					<label for="reporte5" class="form-check-label">Gasto por Categoría Programática</label>												
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">										
					<input type="radio" name="reporte" id="reporte12" class="form-check-input" onclick="listareportes(12)" value="FLUJOCFPEA"/>
					<label for="reporte12" class="form-check-label">Gasto por Categoría Programatica Armonizado</label>												
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">					
					<input type="radio" name="reporte" id="reporte6" class="form-check-input" onclick="listareportes(6)" value="FLUJOOBGTCE"/>
					<label for="reporte6" class="form-check-label">Estado Analítico por Objeto del Gasto y Clasificación Económica</label>												
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">										
					<input type="radio" name="reporte" id="reporte7" class="form-check-input" onclick="listareportes(7)" value="FLUJOCFPE"/>
					<label for="reporte7" class="form-check-label">Estado Analítico por Clasificación Funcional - Programática</label>												
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">										
					<input type="radio" name="reporte" id="reporte8" class="form-check-input" onclick="listareportes(8)" value="FLUJOEFE"/>
					<label for="reporte8" class="form-check-label">Egresos de Flujo de Efectivo</label>												
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">										
					<input type="radio" name="reporte" id="reporte9" class="form-check-input" onclick="listareportes(9)" value="FLUJOIFE"/>
					<label for="reporte9" class="form-check-label">Ingresos de Flujo de Efectivo</label>												
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">										
					<input type="radio" name="reporte" id="reporte10" class="form-check-input" onclick="listareportes(10)" value="ANALITING"/>
					<label for="reporte10" class="form-check-label">Analítico del Ingreso</label>												
				</div>
			</div>
				
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">										
					<input type="radio" name="reporte" id="reporte14" class="form-check-input" onclick="listareportes(14)" value="ANALITING2"/>
					<label for="reporte14" class="form-check-label">Analítico del Ingreso Cuenta Pública</label>												
				</div>				
			</div>
			
			<br/>
			
			<div class="row">					
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="form-group">
						<label for="fecha">Fecha:</label>
	                    <div class="input-group date" id="datepicker1">
	                    	<input type="date" class="form-control form-control-sm" id="fecha" name="fecha"/>                                    
	                    </div>
	                </div>
				</div>
			</div>
			
			<br/>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="excel" class="form-check-label">EXCEL</label>
					<input type="checkbox" name="excel" class="form-check-input" value="excel"/>																																			
				</div>	
				
				<div class="col-4" >
					<input type="button" id="excel" name="btn_excel" value="  EXTRAE  " class="btn btn-secondary btn-sm" onclick="extrae()"/>
				</div>												
			</div>
			
			<br/>
						
			<h5> Firmas </h5>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1" id="divnombre1" title="Firma1">								
					<label for="nombre1"> Nombre&nbsp; </label> 							 							
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1" id="divcargo1" title="DivCargo1">								
					<label for="cargo1"> Cargo&nbsp; </label> 															
				</div>
			</div>
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1" id="divnombre1" title="Firma1">															 
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="nombre1" name="nombre1" class="form-control form-control-sm" maxlength="200" value="" />
					</div> 								
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1" id="divcargo1" title="DivCargo1">															 
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
						<input type="text" id="cargo1" name="cargo1" class="form-control form-control-sm" maxlength="200" value="" />
					</div>								
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<label for="nombre2"> Nombre&nbsp; </label> 
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<label for="cargo2"> Cargo&nbsp; </label>
				</div>
			</div>
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="nombre2" name="nombre2" class="form-control form-control-sm" maxlength="200" value="" />
					</div> 
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
						<input type="text" id="cargo2" name="cargo2" class="form-control form-control-sm" maxlength="200" value="" />
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<label for="nombre3"> Nombre&nbsp; </label> 
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<label for="cargo3"> Cargo&nbsp; </label>
				</div>
			</div>
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="nombre3" name="nombre3" class="form-control form-control-sm" maxlength="200" value="" />
					</div> 
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
						<input type="text" id="cargo3" name="cargo3" class="form-control form-control-sm" maxlength="200" value="" />
					</div>
				</div>
			</div>											
		</div>
				
		<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable %>"/>
		<input type="hidden" id="cdescripcion" name="cdescripcion" value=" "/>
		<input type="hidden" id="anio" name="anio" value=" "/>
		<input type="hidden" id="mes" name="mes" value=" "/>
		
	</form>
</body>
</html>