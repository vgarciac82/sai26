<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.poi.util.StringUtil"%>
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
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	System.out.println(efa);	

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
<title>FormatosPresupuestales</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"	href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/demo_page.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"> </script>

<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript">
	
	$(document).ready(function() {
	
		$("#excel").button();
		
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
			$("#TIPO_REPORTE").val("APARTADO");
		}else if (rep===2){
			$("#TIPO_REPORTE").val("PRECOMP");
		}else if (rep===3){
			$("#TIPO_REPORTE").val("COMPROMETIDO");
		}else if (rep===4){
			$("#TIPO_REPORTE").val("DEVENGADO");
		}else if (rep===5){
			$("#TIPO_REPORTE").val("ACUMULADO");
		}else if (rep===6){
			$("#TIPO_REPORTE").val("PPTO_DEV");
		}
	}
	
	function extrae(){
		document.FormFormatoPptal.submit();			
	}

</script>

</head>
<body id="dt_example">
<br/>
	<form id="FormFormatoPptal" name="FormFormatoPptal" action="../reportes/ReportePresupuestal" method="get" target="blank" onsubmit="return valida();" >
		<input type="hidden" id="TIPO_REPORTE" name="TIPO_REPORTE" value="APARTADO">
		<input type="hidden" id="nReporte" name="nReporte" value="1">
		
		<div id="container" style="width: 80%" class="container">
			<div class="card-header"> <h3> Reporte detallado de los momentos presupuestales </h3> </div>
			<hr class="mt-3">
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
					<input type="radio" name="reporte" id="reporte1" class="form-check-input" onclick="listareportes(1)" value="APARTADO" checked/>
					<label for="reporte1" class="form-check-label">Reporte del presupuesto Apartado</label>																
				</div>
			</div>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
					<input type="radio" name="reporte" id="reporte2" class="form-check-input" onclick="listareportes(2)" value="PRECOMP"/>
					<label for="reporte2" class="form-check-label">Reporte del presupuesto Precomprometido</label>																
				</div>
			</div>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
					<input type="radio" name="reporte" id="reporte3" class="form-check-input" onclick="listareportes(3)" value="COMPROMETIDO"/>
					<label for="reporte3" class="form-check-label">Reporte del presupuesto Comprometido</label>																
				</div>
			</div>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
					<input type="radio" name="reporte" id="reporte4" class="form-check-input" onclick="listareportes(4)" value="DEVENGADO"/>
					<label for="reporte4" class="form-check-label">Reporte del presupuesto Devengado</label>																
				</div>
			</div>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
					<input type="radio" name="reporte" id="reporte5" class="form-check-input" onclick="listareportes(5)" value="ACUMULADO"/>
					<label for="reporte5" class="form-check-label">Reporte Apartado, Comprometido, Precomprom, Devengado Calendarizado</label>																
				</div>
			</div>

			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
					<input type="radio" name="reporte" id="reporte5" class="form-check-input" onclick="listareportes(6)" value="PPTO_DEV"/>
					<label for="reporte5" class="form-check-label">Reporte Devengado (En solicitud y enviado a SICOP) Calendario y acumulado</label>																
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">							
					<input type="button" class="btn btn-secondary" id="excel" name="btn_excel" value="  EXTRAE  " onclick="extrae()">							
				</div>
			</div>	

		</div>
	</form>
</body>
</html>