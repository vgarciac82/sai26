<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
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
	
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
	boolean uneViaticosGastos = "S".equalsIgnoreCase(cabl.getSystemSetting("UNE_VIATICOS_GASTOS"));
	
	
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
<title>ReporteViaticos</title>

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
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"> </script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript">

	var uneViaticosGastos = <%=uneViaticosGastos%>;
		
	$(document).ready(function() {
		cssReadOnly();		
		$("#fechaDel").val(moment().format('yyyy-01-01'));
		$("#fechaAl").val(moment().format('yyyy-MM-DD'));
		
		$("#excel").button();
				
		if (uneViaticosGastos){
			$("#divseparaViaticosyGastos").hide();
			$("#divViaticosyGastos").show();
		}
		else { 
			$("#divseparaViaticosyGastos").show();
			$("#divViaticosyGastos").hide();
		}
	});
	
	
		
	function cssReadOnly(){
		$( "[readOnly]" ).each(
			function(){	
				$(this).addClass("notEditable");	
		});
	}
	


	function listareportes(rep){					
		if (rep===1){
			$("#TIPO_REPORTE").val("REPORTEVIAT");
		}else if (rep===2){
			$("#TIPO_REPORTE").val("REPORTEGASTOS");
		}
		else if (rep===3){
			$("#TIPO_REPORTE").val("REPORTEVIATICOSYGASTOS");
		}
		$("#nReporte").val(rep);
		
		
	}
	
	function hoy(){
		var dd = new Date().getDate();
		var mm = new Date().getMonth()+1;
		var aact = new Date().getFullYear();
		var aaaa = "<%=efa%>";
		var cad = "";
		if (dd<10)
			cad=cad+"0"+dd+"/";
		else
			cad=cad+dd+"/";
		if (mm<10)
			cad=cad+"0"+mm+"/";
		else
			cad=cad+mm+"/";
		if (aact != aaaa)
			cad = "01/01/" + aaaa;
		else 
			cad=cad+aaaa;
		$("#fecha_fin").val(cad);
		$("#fecha_ini").val(cad);			
	}
	
	function extrae(){
		$("#fecha_ini").val($("#fechaDel").val().split('-').reverse().join('/'));
		$("#fecha_fin").val($("#fechaAl").val().split('-').reverse().join('/'));
		if(reporte3.checked )
			$("#TIPO_REPORTE").val("REPORTEVIATICOSYGASTOS");
		document.FormReporteViaticos.submit();		
	}
	

</script>


</head>
<body id="dt_example">
<br/>
	<form id="FormReporteViaticos" name="FormReporteViaticos" action="../reportes/ReporteViaticos" method="get">
		<input type="hidden" id="TIPO_REPORTE" name="TIPO_REPORTE" value="REPORTEVIAT"/>
		<input type="hidden" id="imprimeCaracteristicas" name="imprimeCaracteristicas" value="1"/>
		
		<input type="hidden" id="nReporte" name="nReporte" value="1"/>
		<input type="hidden" id="fecha_ini" name="fecha_ini" value=""/>
		<input type="hidden" id="fecha_fin" name="fecha_fin" value=""/>
		
		<div id="container" class="container" style="width: 60%">
			<div class="card-header"> <h3 class="w-auto px-3"> Reporte Viaticos</h3> </div>
			<hr class="mt-3">
			
			<h6> Reportes para el portal de transparencia - Viaticos y Gastos de Representacion </h6>
			
			<br/>
			
			<div id="divseparaViaticosyGastos">
				<div class="row">
					<div class="col-12">																										
						<div class="form-check">
							<input type="radio" class="form-check-input" id="reporte1" name="reporte"  onclick="listareportes(1)" value="REPORTEVIAT"/>								
							<label for="reporte1" class="form-check-label">Viaticos y pasajes</label>							
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12">																										
						<div class="form-check">
							<input type="radio" class="form-check-input" id="reporte2" name="reporte"  onclick="listareportes(2)" value="REPORTEGASTOS"/>								
							<label for="reporte2" class="form-check-label">Gastos de representación</label>							
						</div>
					</div>
				</div>
				
			</div>
							
			<div id="divViaticosyGastos">
				<div class="row">
					<div class="col-12">																										
						<div class="form-check">
							<input type="radio" class="form-check-input" id="reporte3" name="reporte" onclick="listareportes(3)" value="REPORTEVIATIVOSYGASTOS" checked/>								
							<label for="reporte3" class="form-check-label">Gastos de viaticos y gastos de representación</label>							
						</div>
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-6 col-md-6 col-sm-12">
					<div class="input-group mb-3">						
						<div class="form-group">
	                    	<label for="fecha">Del:</label>
	                       	<div class="input-group date" id="datepicker1">
	                        	<input type="date" class="form-control form-control-sm" id="fechaDel" name="fechaDel"/>                                    
	                       	</div>
	                	</div>
					</div>
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					<div class="form-group mb-3">
						<div class="form-group">
	                    	<label for="fecha">Al:</label>
	                       	<div class="input-group date" id="datepicker1">
	                        	<input type="date" class="form-control form-control-sm" id="fechaAl" name="fechaAl"/>                                    
	                       	</div>
	                	</div>
                	</div>
				</div>			
			</div>	
			
			<div class="row">
				<div class="col-12" >													
					<input type="button" id="excel" name="btn_excel" value="  EXTRAE  " class="btn btn-secondary btn-sm" onclick="extrae()"/>												
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