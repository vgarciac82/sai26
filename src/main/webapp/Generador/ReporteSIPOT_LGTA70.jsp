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
<title>Reporte SIPOT Art. 70</title>

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
	document.ReporteSIPOT_LGTA70.submit();						
	}	
	
</script>

</head>
<body id="dt_example">
<br/>
	<form id="ReporteSIPOT_LGTA70" name="ReporteSIPOT_LGTA70" action="../reportes/ReporteSIPOT_LGTA70" method="post">		
		
		<div id="container" class="container" style="width: 60%">			
		
			<div class="card-header"> <h3> SIPOT Art. 70 </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12">																										
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte1" name="reporte" value="43A" onclick="habilita()"/>								
						<label for="reporte1" class="form-check-label">Ingresos Recibidos 43-XLIII-A</label>							
					</div>						 
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte1" name="reporte" value="43B" onclick="habilita()"/>								
						<label for="reporte2" class="form-check-label">Responsables - Fraccion 43-XLIII-B</label>							
					</div>
					
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte3" name="reporte" value="31" onclick="habilita()"/>								
						<label for="reporte3" class="form-check-label">Informe Ingresos Presupuestales 31-XXXI</label>							
					</div>
				</div>
			</div>			
			
			<div class="row">
				<div class="col-12 col-md-6 mb-3">										
					<label for="mes" class="form-label"><strong> Trimestre </strong></label>											
					<select id="mes" name="mes" class="form-select form-select-sm" style="width: 15em;">
						<option value = 3> Enero - Marzo </option>
						<option value = 6> Abril - Junio </option>
						<option value = 9> Julio - Septiembre </option>
						<option value = 12> Octubre - Diciembre </option>	
					</select>																													
				</div>																																										
			</div>
			
			<div class="row">
				<div class="col-12" >													
					<input type="button" id="excel" name="btn_excel" value="  EXTRAE  " class="btn btn-secondary btn-sm" onclick="extrae()"/>												
				</div>														
			</div>
			
		</div>
		
		<input type="hidden" id="ejercicioFiscal" name="ejercicioFiscal" value="<%=aEjercicioFiscal%>"/>
		<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable %>"/>
		<input type="hidden" id="cdescripcion" name="cdescripcion" value=" "/>
		<input type="hidden" id="anio" name="anio" value=" "/>
		<input type="hidden" id="mes" name="mes" value=" "/>
		<input type="hidden" id="nFolioeCs" name="nFolioeCs" value=" "/>
		<input type="hidden" id="validaExiste" name="validaExiste" value=" "/>
		<input type="hidden" id="validaExiste1112" name="validaExiste1112" value=" "/>
		<input type="hidden" id="mesOnceDoce" name="mesOnceDoce" value=" "/>
		<input type="hidden" id="validaMes112" name="validaMes112" value=" "/>
		<input type="hidden" id="validaMes1112" name="validaMes1112" value=" "/>
		<input type="hidden" id="mes112" name="mes112" value=" "/>
	</form>
</body>
</html>