<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String mensaje = null;
	if (session.getAttribute("RESULT") != null) {
		mensaje = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
	
	String login=usuario.getLogin();
	String cCentroContable=(usuario.getPropiedad("CCENTROCONTABLE") != null? (usuario.getPropiedad("CCENTROCONTABLE").getValor() != null? usuario.getPropiedad("CCENTROCONTABLE").getValor():"" ):"" );

	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int aEjercicioFiscal =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	System.out.println(aEjercicioFiscal);
%>

<%@ page 
	import="java.util.Calendar"
	import="com.syc.gestion.core.Usuario"
	import="com.syc.gestion.servlet.GestionInterface"
%>

<%@ page import="java.util.*,java.sql.*,com.syc.gestion.*,com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.Util" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Gasto Programable</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
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

<script type="text/javascript">

	$(document).ready(function() {
		queryFormPost("readFirmantesEstadosFinancieros", {async: false});
		queryFormPost("descCentroContableRead", {async: false});
		cssReadOnly();
		$("#excel").button();
	});
	
	$(function() {
		$( "#Fecha").datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
		hoy();		
	});
		
	function cssReadOnly(){
		$( "[readOnly]" ).each(
			function(){	
				$(this).addClass("notEditable");	
		});
	}		
	
	function hoy(){
		var dd = new Date().getDate();
		var mm = new Date().getMonth()+1;
		var aaaa = new Date().getFullYear();
		var cad = "";
		if (dd<10)
			cad=cad+"0"+dd+"/";
		else
			cad=cad+dd+"/";
		if (mm<10)
			cad=cad+"0"+mm+"/";
		else
			cad=cad+mm+"/";
		cad=cad+aaaa;
		$("#Fecha").val(cad);
	}
	
	function extrae(){				
		document.FormSIIWebFlujoEfectivo.submit();
	}
	
</script>


</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
<br/>
	<form id="FormSIIWebFlujoEfectivo" name="FormSIIWebFlujoEfectivo" action="../reportes/ReporteSIIWebFlujoEfectivo" method="get" >	
		<div id="container" class="container" style="width: 60%">
				
			<div class="card-header"> <h3> Gasto Programable </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12">																				
					<!-- 
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte11" name="reporte" value="Eje312"/>								
						<label for="reporte11" class="form-check-label">Análisis Programático Funcional, Ejercido (312)</label>							
					</div>
					 -->
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte12" name="reporte" value="Pag316" checked/>								
						<label for="reporte12" class="form-check-label">Análisis Programático Funcional, Pagado (316)</label>							
					</div>
					
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte13" name="reporte" value="Com318"/>								
						<label for="reporte13" class="form-check-label">Gasto Comprometido del Sector Paraestatal no Financiero (318)</label>							
					</div>
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte14" name="reporte" value="Dev319"/>								
						<label for="reporte14" class="form-check-label">Gasto devengado del Sector Paraestatal no Financiero (319)</label>							
					</div>
					<!-- 
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte15" name="reporte" value="361"/>								
						<label for="reporte15" class="form-check-label">Gasto Programable en Flujo de Efectivo por Entidad Federativa (361)</label>							
					</div>
					 -->
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte16" name="reporte" value="Com3110"/>								
						<label for="reporte16" class="form-check-label">Gasto Comprometido del Sector Paraestatal no Financiero, Inicial (3110)</label>							
					</div>										
				</div>
			</div>
			
			<p><b>Nota: Formatos anuales para Cuenta Publica solo 316 y 319</b></p>
			
			<div class="row" id= "tdFecha">
				<div class="col-12 col-md-6 mb-3">										
					<label for="mes" class="form-label"> Mes </label>											
					<select id="mes" name="mes" class="form-select form-select-sm" style="width: 12em;">
						<option value=1>Enero</option>
						<option value=2>Febrero</option>
						<option value=3>Marzo</option>
						<option value=4>Abril</option>
						<option value=5>Mayo</option>
						<option value=6>Junio</option>
						<option value=7>Julio</option>
						<option value=8>Agosto</option>
						<option value=9>Septiembre</option>
						<option value=10>Octubre</option>
						<option value=11>Noviembre</option>
						<option value=12>Diciembre</option>
						<option value=13>Anual CP</option>
					</select>																													
				</div>																																										
			</div>
			
			<div class="row">
				<div class="col-2" >
					<label for="csv" class="form-check-label">CSV</label>
					<input type="checkbox" name="csv" id="csv" class="form-check-input" value="csv"/>																																			
				</div>													
			</div>
			
			<br/>
			
			<div class="row">
				<div class="col-12" >													
					<input type="button" id="excel" name="btn_excel" value="  EXTRAE  " class="btn btn-secondary btn-sm" onclick="extrae()"/>												
				</div>														
			</div>
		<br/>
			
		</div>
		
		<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable %>"/>
		<input type="hidden" id="cdescripcion" name="cdescripcion" value=" "/>
		<input type="hidden" id="anio" name="anio" value=" "/>
		<input type="hidden" id="mes" name="mes" value=" "/>
		<input type="hidden" id="dia" name="dia" value="0"/>
		<input type="hidden" id="ejercicioFiscal" name="ejercicioFiscal" value="<%=aEjercicioFiscal%>"/>		
		
	</form>
</body>
</html>