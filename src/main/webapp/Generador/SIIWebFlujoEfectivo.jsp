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
<title>Flujo de Efectivo</title>

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
		
		if ( $("#reporte2").prop("checked")){		
				switch ($("#mesOnceDoce").val()) {
				     case "Enero":
				         $("#mes112").val(1);
				         break;
				     case "Febrero":
				         $("#mes112").val(2);
				         break;
				     case "Marzo":
				         $("#mes112").val(3);
				         break;
				     case "Abril":
				         $("#mes112").val(4);
				         break;
				     case "Mayo":
				         $("#mes112").val(5);
				         break;
				     case "Junio":
				         $("#mes112").val(6);
				         break;
				     case "Julio":
				         $("#mes112").val(7);
				         break;
				     case "Agosto":
				         $("#mes112").val(8);
				         break;
				     case "Septiembre":
				         $("#mes112").val(9);
				         break;
				     case "Octubre":
				         $("#mes112").val(10);
				         break;
				     case "Noviembre":
				         $("#mes112").val(11);
				         break;
				     case "Diciembre":
				         $("#mes112").val(12);
				         break;
				 }  
				
				queryFormPost("validaMes112", {async: false});
				
				
				if ( $("#validaMes112").val() == "true" || $("#mesOnceDoce").val() == "Enero" ){
						
					queryFormPost("validaExiste", {async: false});	
					document.FormSIIWebFlujoEfectivo.submit();
				}
				else {
					Swal.fire({ icon: "warning",
								text: "Falta generar el mes anterior, Favor de rectificar!!!" });					
				}		
								
			}
			else if ($("#reporte10").prop("checked")){
					switch ($("#mesOnceDoce").val()) {
				     case "Enero":
				         $("#mes112").val(1);
				         break;
				     case "Febrero":
				         $("#mes112").val(2);
				         break;
				     case "Marzo":
				         $("#mes112").val(3);
				         break;
				     case "Abril":
				         $("#mes112").val(4);
				         break;
				     case "Mayo":
				         $("#mes112").val(5);
				         break;
				     case "Junio":
				         $("#mes112").val(6);
				         break;
				     case "Julio":
				         $("#mes112").val(7);
				         break;
				     case "Agosto":
				         $("#mes112").val(8);
				         break;
				     case "Septiembre":
				         $("#mes112").val(9);
				         break;
				     case "Octubre":
				         $("#mes112").val(10);
				         break;
				     case "Noviembre":
				         $("#mes112").val(11);
				         break;
				     case "Diciembre":
				         $("#mes112").val(12);
				         break;
				 } 
					queryFormPost("validaMes1112", {async: false});
					
				if ( $("#validaMes1112").val() == "true" || $("#mesOnceDoce").val() == "Enero" ){
						
					queryFormPost("validaExiste1112", {async: false});	
					document.FormSIIWebFlujoEfectivo.submit();
				}
				else {
					Swal.fire({ icon: "warning",
								text: "Falta generar el mes anterior, Favor de rectificar!!!" });					
				}	
			}		
			else {
				document.FormSIIWebFlujoEfectivo.submit();
			}
			
	}
	
	function habilita(){
		if($("#reporte4").prop("checked"))
			document.getElementById("csv").style.visibility= "hidden";
		else {
			document.getElementById("csv").style.visibility= "visible";
			document.getElementById("tdFecha").style.visibility= "visible";
		}
	}
	
	function habilita_112(){
		if ( $("#reporte2").prop("checked"))
		  document.getElementById("tdFecha").style.visibility= "hidden";		 
		 else		 
			document.getElementById("tdFecha").style.visibility= "visible";		 
	}
	
	function habilita_1112(){
		 if ( $("#reporte10").prop("checked"))
		   document.getElementById("tdFecha").style.visibility= "hidden";		 
		 else		 
		 	document.getElementById("tdFecha").style.visibility= "visible";		 
	}
	
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
<br/>
	<form id="FormSIIWebFlujoEfectivo" name="FormSIIWebFlujoEfectivo" action="../reportes/ReporteSIIWebFlujoEfectivo" method="get">		
		<div id="container" class="container" style="width: 60%">
		
			<div class="card-header"> <h3> Flujo Efectivo </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12">													
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte1" name="reporte" value="Org111" onclick="habilita()" checked/>								
						<label for="reporte1" class="form-check-label">Flujo de Efectivo Original (111)</label>							
					</div>
					<!-- 
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte2" name="reporte" value="Obs112" onclick="habilita_112()"/>								
						<label for="reporte2" class="form-check-label">Flujo de Efectivo Observado (112)</label>							
					</div>
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte3" name="reporte" value="Mod113" onclick="habilita()"/>								
						<label for="reporte3" class="form-check-label">Flujo de Efectivo Modificado 2006 (113)</label>							
					</div>
					 -->
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte4" name="reporte" value="Ant114" onclick="habilita()"/>								
						<label for="reporte4" class="form-check-label">Flujo de Efectivo Ejercicios Anteriores (114) <span class="label"><b>(No tiene version csv)</b></span></label>							
					</div>
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte5" name="reporte" value="Org161" onclick="habilita()"/>								
						<label for="reporte5" class="form-check-label">Detalle de los Ingresos del Sector Paraestatal No Financiero. Original (161)</label>							
					</div>
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte6" name="reporte" value="Rad162" onclick="habilita()"/>								
						<label for="reporte6" class="form-check-label">Detalle de los Ingresos del Sector Paraestatal No Financiero. Recaudado (162)</label>							
					</div>
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte7" name="reporte" value="Mod163" onclick="habilita()"/>								
						<label for="reporte7" class="form-check-label">Detalle de los Ingresos del Sector Paraestatal No Financiero. Modificado (163)</label>							
					</div>
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte8" name="reporte" value="Sol164" onclick="habilita()"/>								
						<label for="reporte8" class="form-check-label">Detalle de los Ingresos del Sector Paraestatal No Financiero. Devengado (164)</label>							
					</div>
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte9" name="reporte" value="Mod1111" onclick="habilita()"/>								
						<label for="reporte9" class="form-check-label">Flujo de Efectivo Modificado (1111)</label>							
					</div>	
					<!-- 
					<div class="form-check">
						<input type="radio" class="form-check-input" id="reporte10" name="reporte" value="Efe1112" onclick="habilita_1112()"/>								
						<label for="reporte10" class="form-check-label">Flujo de Efectivo (1112)</label>							
					</div>
					 -->					
				</div>
			</div>
			
			<p><b>Nota: Formatos anuales para Cuenta Publica solo 162 y 164</b></p>
			
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
		<input type="hidden" id="nFolioeCs" name="nFolioeCs" value=" "/>
		<input type="hidden" id="validaExiste" name="validaExiste" value=" "/>
		<input type="hidden" id="validaExiste1112" name="validaExiste1112" value=" "/>
		<input type="hidden" id="mesOnceDoce" name="mesOnceDoce" value=" "/>
		<input type="hidden" id="validaMes112" name="validaMes112" value=" "/>
		<input type="hidden" id="validaMes1112" name="validaMes1112" value=" "/>
		<input type="hidden" id="mes112" name="mes112" value=" "/>
		<input type="hidden" id="dia" name="dia" value="0"/>
		<input type="hidden" id="ejercicioFiscal" name="ejercicioFiscal" value="<%=aEjercicioFiscal%>"/>
	</form>
</body>
</html>