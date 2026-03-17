<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
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
<title>Disponibilidades Financieras</title>

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
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"> </script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript">

	$(document).ready(function() {
		
		$("#excel").button();
		
		$("input.AyudaSyC").subIniciaDlg();
		$("input.autoCompletaSyC").subIniciaAutoCompleta();
		
	});
		
	function extrae(){
		if ($("#hbuscaCedulaE06").val()){
			document.FormSIIWebDisFinancieras.submit();
		}
		else{
			Swal.fire({ icon: "warning",
						text: "Falta Seleccionar la Cedula E06, para extraer los datos..." });			
		}
	}
	
</script>

</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
<br/>
	<form id="FormSIIWebDisFinancieras" name="FormSIIWebDisFinancieras" action="../reportes/ReporteSIIWebDisFinancieras" method="get">		
		<input  type="hidden" id="nMes" name="nMes" value="" />
	
		<div id="container" class="container" style="width: 60%">
		
			<div class="card-header"> <h3> Disponibilidades Financieras </h3> </div>
			<hr class="mt-3"/>
				
			<div class="row">
				<div class="col-12">																				
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte11" name="reporte" value="210" checked/>								
						<label for="reporte11" class="form-check-label">Registro de cuentas de depósito o inversión (210)</label>							
					</div>							
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte12" name="reporte" value="221"/>								
						<label for="reporte12" class="form-check-label">Saldos e instituciones fiancieras de las disponibilidades y activos financieros - 
																		Paraestatales no financieras (221)</label>							
					</div>
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte13" name="reporte" value="222"/>								
						<label for="reporte13" class="form-check-label">Saldos contables de disponibilidades y activos financieros - Paraestatales no financieras (222)</label>							
					</div>																
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-md-6 mb-3">									
					<h5><label for="cRFC" class="form-label">Seleciona Cedula E06</label></h5>
					<div class="input-group">
						<input type="text" id="hbuscaCedulaE06" name="hbuscaCedulaE06" value="" class="form-control AyudaSyC form-control-sm" style="width: 20em !important;flex: none;">
						<input type="hidden" id="buscaCedulaE06" name="buscaCedulaE06" value="" class="form-control" size="5" maxlength="5" class="">
					</div>
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
		<input type="hidden" id="nFolioeCs" name="nFolioeCs" value=""/>
	</form>
</body>
</html>