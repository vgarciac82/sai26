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
<title>Estados Financieros</title>

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

	function cssReadOnly(){
		$( "[readOnly]" ).each(
			function(){	
				$(this).addClass("notEditable");	
		});
	}		
	
	function extrae(){
		var dia = new Date($("#ejercicioFiscal").val(), $("#mes").val(),0).getDate();
		$("#dia").val(dia);
		
		document.FormSIIWebFlujoEfectivo.submit();
	}
	
</script>


</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
<br/>
	<form id="FormSIIWebFlujoEfectivo" name="FormSIIWebFlujoEfectivo" action="../reportes/ReporteSIIWebFlujoEfectivo" method="get">		
		<div id="container" class="container" style="width: 60%">			
			<input type="hidden" id="ejercicioFiscal" name="ejercicioFiscal" value="<%=aEjercicioFiscal%>"/>
				
			<div class="card-header"> <h3> Explicaciones 145, 146, 147 </h3> </div>
			<hr class="mt-3"/>						
																			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-md-6 mb-3">										
					<label for="mes" class="form-label"> Mes de Consulta: </label>											
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
					</select>																													
				</div>																																										
			</div>													
		
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-md-6 mb-3">													
					<input type="button" id="excel" name="btn_excel" value="  EXTRAE  " class="btn btn-secondary btn-sm" onclick="extrae()"/>												
				</div>														
			</div>
					
		</div>
		
		<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable %>"/>
		<input type="hidden" id="cdescripcion" name="cdescripcion" value=" "/>
		<input type="hidden" id="anio" name="anio" value=" "/>
		<input type="hidden" id="mes" name="mes" value=" "/>
		<input type="hidden" id="reporte" name="reporte" value="explicaciones"/>
		<input type="hidden" id="dia" name="dia" value=""/>			
		
	</form>
</body>
</html>