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
<title>ConciliacionesConta</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"> </script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript">

	
	$(document).ready(function() {
		
		$("#excel").button();
		$("#fecha").val(moment().format('yyyy-MM-DD'));
		queryFormPost("descCentroContableRead", {async: false});
		cssReadOnly();						
	
	});		
		
	function cssReadOnly(){
		$( "[readOnly]" ).each(
			function(){	
				$(this).addClass("notEditable");	
		});
	}
		
	function valida(){
			if($("#SaldoSIAFF").val() == "" && (rep===5)){
				alert("Falta dato en Saldos SIAFF..");
				return false;
			}
			return true;
	}
	
	function listareportes(rep){					
		if (rep===1){
			$("#TIPO_REPORTE").val("CONCILIAMOD");
			$("#dlgcomenterios").css('display', 'none');
		}else if (rep===2){
			$("#TIPO_REPORTE").val("CONCILIARADINGRESO");
			$("#dlgcomenterios").css('display', 'none');
		}else if (rep===3){
			$("#TIPO_REPORTE").val("CONCILIARADPAGADO");
			$("#dlgcomenterios").css('display', 'none');
		}else if (rep===4){
			$("#TIPO_REPORTE").val("CONCILIAINGRESOGASTO");
			$("#dlgcomenterios").css('display', 'none');
		}else if (rep===5){
			$("#TIPO_REPORTE").val("Concilia11225");
			$("#dlgcomenterios").css('display', 'block');
		}else if (rep===6){
			$("#TIPO_REPORTE").val("GastoDevengado");
			$("#dlgcomenterios").css('display', 'none');
		}
		else if (rep===7){
			$("#TIPO_REPORTE").val("ConciliaDevengado");
			$("#dlgcomenterios").css('display', 'none');
		}
			else if (rep===8){
			$("#TIPO_REPORTE").val("ConciliaEjercido");
			$("#dlgcomenterios").css('display', 'none');
		}
			else if (rep===9){
			$("#TIPO_REPORTE").val("ConciliaCompromiso");
			$("#dlgcomenterios").css('display', 'none');
		}
				else if (rep===10){
			$("#TIPO_REPORTE").val("ConciliaMomentos");
			$("#dlgcomenterios").css('display', 'none');
		}
				else if (rep===11){
			$("#TIPO_REPORTE").val("ConciliaIngreso");
			$("#dlgcomenterios").css('display', 'none');
		}
				else if (rep===12){
			$("#TIPO_REPORTE").val("ConciliaDevengadoIng");
			$("#dlgcomenterios").css('display', 'none');
		}
				else if (rep===13){
			$("#TIPO_REPORTE").val("ConciliaOrgModDispEjeIng");
			$("#dlgcomenterios").css('display', 'none');
		}
			else if (rep===14){
			$("#TIPO_REPORTE").val("ConciliaEgresos");
			$("#dlgcomenterios").css('display', 'none');
		}
			else if (rep===15){
			$("#TIPO_REPORTE").val("ConciliaRadicadoPagado");
			$("#dlgcomenterios").css('display', 'none');
		}
		$("#nReporte").val(rep);
		
		if (rep==1 || rep==2 || rep==3 || rep==4 || rep==6 || rep==7 || rep==8 || rep==9  || rep==10 || rep==11|| rep==12|| rep==13|| rep==14|| rep==15){ //Se desactivan los radiobutton del nivel ya que en estos reportes no se necesita el nivel
			$("#SaldoSIAFF").attr('disabled',true);
		
		}
		else{ //Se activan los radiobutton para los reportes que si se necesite capturar el nivel
			$("#SaldoSIAFF").prop("disabled", false);
			
		}
	}

	function extrae(){
		$("#fecha_fin").val($("#fecha").val().split('-').reverse().join('/'));
		document.FormConciliacionesConta.submit();
	}
	
</script>

</head>
<body id="dt_example">
<br/>
	<form id="FormConciliacionesConta" name="FormConciliacionesConta" action="../reportes/ReportePolizas" method="get" target="blank" onsubmit="return valida();" >
		<input type="hidden" id="TIPO_REPORTE" name="TIPO_REPORTE" value="CONCILIAMOD">
		<input type="hidden" id="imprimeCaracteristicas" name="imprimeCaracteristicas" value="1">
		<!--  <input type="hidden" id="imprimeDetalle" name="imprimeDetalle" value="2"> -->
		<input type="hidden" id="nReporte" name="nReporte" value="1">
		<input type="hidden" id="fecha_fin" name="fecha_fin">
				
		<div id="container" class="container" style="width: 80%">
		
			<div class="card-header"> <h3> Conciliaciones Contables y Presupuestales </h3> </div>
			<hr class="mt-3"/>			
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 d-flex p-1">
					<input type="radio" name="reporte" id="reporte1" class="form-check-input" onclick="listareportes(1)" value="CONCILIAMOD" checked/>						
					<label for="reporte1" class="form-check-label">&nbsp;Conciliación Modificado</label>																
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 d-flex p-1">
					<input type="radio" name="reporte" id="reporte2" class="form-check-input" onclick="listareportes(2)" value="CONCILIARADINGRESO"/>						
					<label for="reporte1" class="form-check-label">&nbsp;Conciliación Ingreso SAI - Radicado SIAFF</label>																
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 d-flex p-1">
					<input type="radio" name="reporte" id="reporte3" class="form-check-input" onclick="listareportes(3)" value="CONCILIARADPAGADO"/>						
					<label for="reporte1" class="form-check-label">&nbsp;Conciliación Egreso SAI - Radicado SIAFF</label>																
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 d-flex p-1">
					<input type="radio" name="reporte" id="reporte4" class="form-check-input" onclick="listareportes(4)" value="IngresoGasto"/>						
					<label for="reporte1" class="form-check-label">&nbsp;Conciliación Ingreso - Gasto</label>																
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 d-flex p-1">
					<input type="radio" name="reporte" id="reporte5" class="form-check-input" onclick="listareportes(5)" value="Concilia11225"/>						
					<label for="reporte1" class="form-check-label">&nbsp;Conciliación 11225</label>																
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 d-flex p-1">
					<input type="radio" name="reporte" id="reporte9" class="form-check-input" onclick="listareportes(9)" value="ConciliaCompromiso"/>						
					<label for="reporte1" class="form-check-label">&nbsp;Conciliación de Momentos Comprometido - Devengado</label>																
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 d-flex p-1">
					<input type="radio" name="reporte" id="reporte7" class="form-check-input" onclick="listareportes(7)" value="ConciliaDevengado"/>						
					<label for="reporte1" class="form-check-label">&nbsp;Conciliación de Momentos Devengado - Ejercido</label>																
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 d-flex p-1">
					<input type="radio" name="reporte" id="reporte8" class="form-check-input" onclick="listareportes(8)" value="ConciliaEjercido"/>						
					<label for="reporte1" class="form-check-label">&nbsp;Conciliación de Momentos Ejercido - Pagado</label>																
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 d-flex p-1">
					<input type="radio" name="reporte" id="reporte10" class="form-check-input" onclick="listareportes(10)" value="ConciliaMomentos"/>						
					<label for="reporte1" class="form-check-label">&nbsp;Conciliación de Momentos Contables vs Presupuesto</label>																
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 d-flex p-1">
					<input type="radio" name="reporte" id="reporte11" class="form-check-input" onclick="listareportes(11)" value="ConciliaIngreso"/>						
					<label for="reporte1" class="form-check-label">&nbsp;Conciliación de Ingreso Contable - Presupuestal</label>																
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 d-flex p-1">
					<input type="radio" name="reporte" id="reporte12" class="form-check-input" onclick="listareportes(12)" value="ConciliaDevengadoIng"/>						
					<label for="reporte1" class="form-check-label">&nbsp;Conciliación Devengado - Ejercido Ingreso</label>																
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 d-flex p-1">
					<input type="radio" name="reporte" id="reporte13" class="form-check-input" onclick="listareportes(13)" value="ConciliaOrgModDispEjeIng"/>						
					<label for="reporte1" class="form-check-label">&nbsp;Conciliación Original, Disponible, Modificado y Ejercido Ingreso</label>																
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 d-flex p-1">
					<input type="radio" name="reporte" id="reporte14" class="form-check-input" onclick="listareportes(14)" value="ConciliaEgresos"/>						
					<label for="reporte1" class="form-check-label">&nbsp;Conciliación del Egreso Contable - Presupuestal</label>																
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>							
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 d-flex p-1">
					<input type="radio" name="reporte" id="reporte15" class="form-check-input" onclick="listareportes(15)" value="ConciliaRadicadoPagado"/>						
					<label for="reporte1" class="form-check-label">&nbsp;Conciliación del Radicado 8151 - Pagado 8271</label>																
				</div>
			</div>
			
			<br/>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					<h6>Fecha</h6>
				</div>					
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>														
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<div class="form-group">
                       <div class="input-group date" id="datepicker1">
                           <input type="date" class="form-control form-control-sm" id="fecha" name="fecha"/>                                
                       </div>
                	</div>		
				</div>				
			</div>
					
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="button" class="btn btn-secondary btn-sm" id="excel" name="btn_excel" value="  EXTRAE  " onclick="extrae()">
				</div>
			</div>		
			
			<br/>
			
			<div id="dlgcomenterios" title="Datos Adicionales" style="display: none;">
				<fieldset class="form-group border px-3"> <br/>		
					<div class="card-header"> <h6> Datos adicionales </h6> </div>
					<hr class="mt-3"/>
					
					<div class="row">
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex p-1">
							Ingresa Saldo en SIAFF&nbsp; <input type="text" id="SaldoSIAFF" name="SaldoSIAFF" class="form-control form-control-sm" style="width: 10em;" maxlength="20" value=""/> 
						</div>
					</div>
				</fieldset>						
			</div>			
		</div>
		
		<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable %>"/>
		<input type="hidden" id="cdescripcion" name="cdescripcion" value=" "/>
		<input type="hidden" id="anio" name="anio" value=" "/>
		<input type="hidden" id="mes" name="mes" value=" "/>
		
	</form>
</body>
</html>