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
	String cUR = usuario.getU_UR();
	String cLogin = usuario.getLogin();

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
<title>Reportes Viaticos</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"	href="css/jquery-ui.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-3.5.1.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript">
	
	$(document).ready(function() {
		setFechas();
		$("#excel").button();
		
		$("#reporte1").click();
		cssReadOnly();		
	
		if($("#cUR").val()=="A02"){
			querySelectPost("cURVistasViaticosTodos", "uEjecutora", { async : false });
		} else
			querySelectPost("cUnidadEjecutoraVistasViaticos", "uEjecutora", { async : false });
		
		$('.unidadResp').hide();
	});
	

	function setFechas(){

		$("#fInicio").datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
		});
		
		$("#fFin").datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
		});
		
	}
	function cssReadOnly(){
		$( "[readOnly]" ).each(
			function(){	
				$(this).addClass("notEditable");	
		});
	}
		
	function seleccionaComision() {
		 if($("#cTodos").prop("checked")) {
			 $("#folio").attr('readonly', true );
			 $("#nFolio").val("%");
		 } else {
			 $("#folio").attr('readonly', false );
			 
		 }
	}
	
	
	function listareportes(rep){
	
	$("#nReporte").val(rep);
		if (rep===1){
			$("#TIPO_REPORTE").val("Deudores");
			$('.unidadResp').hide();
			$('.capturaFechas').hide();
			$('.folioComision').show();
		}else if (rep===2){
			$("#TIPO_REPORTE").val("DiasXUR");
			$('.folioComision').hide();
			$('.capturaFechas').hide();
			$('.unidadResp').show();
		}else if (rep===3){
			$("#TIPO_REPORTE").val("MontoXComision");
			$('.unidadResp').show();
			$('.capturaFechas').hide();
			$('.folioComision').hide();
		}else if (rep===4){
			$("#TIPO_REPORTE").val("SaldoVencimiento");
			$('.unidadResp').show();
			$('.capturaFechas').hide();
			$('.folioComision').hide();
		}else if (rep===5){
			$("#TIPO_REPORTE").val("BoletosXUR");
			$('.unidadResp').show();
			$('.capturaFechas').show();
			$('.folioComision').hide();	
		}else if (rep===6){
			$("#TIPO_REPORTE").val("AgendasP");
			$('.unidadResp').hide();
			$('.capturaFechas').hide();
			$('.folioComision').hide();
		}
	}
	
	function extrae(){
		if($("#cTodos").prop("checked")) {
			 $("#nFolio").val("%");
		 } else {
			 $("#nFolio").val($("#folio").val());	 
		 }
		
		$("#unidadEjecutora").val($("#uEjecutora").val());
		
		document.FormFormatoPptal.submit();			
	}

	function validarFechas(){
		var bRegresa = true;
        fInicio = document.getElementById('fInicio').value.split("/");
        fFin  = document.getElementById('fFin').value.split("/");
	      
        fInicio = new Date( fInicio[2], fInicio[1]-1, fInicio[0], 0, 0, 0, 0 );
        fFin = new Date( fFin[2], fFin[1]-1, fFin[0], 0, 0, 0, 0);

        if(fInicio > fFin){
        	bRegresa = false;        	
        }
        
        return bRegresa;
  	}
	
	
	function verificaFechas(){
		if(!validarFechas()){
			Swal.fire("VERIFICAR FECHAS","La fecha de Inicio no puede ser mayor a la fecha Fin", "error");
			$("#fFin").val("");
		}
	} 
	
</script>

</head>
<body id="dt_example">
<br/>
	<form id="FormFormatoPptal" name="FormFormatoPptal" action="../reportes/ReporteViaticos" method="get" target="blank" onsubmit="return valida();" >
		<input type="hidden" id="TIPO_REPORTE" name="TIPO_REPORTE" value="Deudores">
		<input type="hidden" id="nReporte" 	name="nReporte" value="1">
		<input type="hidden" id="u_Login" 	name="u_Login" 	value="<%=cLogin%>"/>
		<input type="hidden" id="cUR" 		name="cUR" 		value="<%=cUR%>"/>
		<input type="hidden" id="unidadEjecutora"	name="unidadEjecutora" value = "%"/>
		<input type="hidden" id="nFolio"	name="nFolio" value = "%"/>
		
		<div id="container" style="width: 80%" class="container">
			<div class="card-header"> <h3> Reportes de viáticos </h3> </div>
			
			<div class="row d-flex justify-content-center mt-3">		
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
					<input type="radio" name="reporte" id="reporte1" class="form-check-input" onclick="listareportes(1)" value="Deudores" checked/>
					<label for="reporte1" class="form-check-label">Reporte de Deudores por Comisión</label>																
				</div>
			</div>
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
					<input type="radio" name="reporte" id="reporte3" class="form-check-input" onclick="listareportes(3)" value="MontoXComision"/>
					<label for="reporte3" class="form-check-label">Reporte de comisión por UR con el detalle de gastos por rubro</label>																
				</div>
			</div>
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
					<input type="radio" name="reporte" id="reporte2" class="form-check-input" onclick="listareportes(2)" value="DiasXUR"/>
					<label for="reporte2" class="form-check-label">Reporte de días acumulados por empleado</label>																
				</div>
			</div>
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
					<input type="radio" name="reporte" id="reporte4" class="form-check-input" onclick="listareportes(4)" value="BoletosXUR"/>
					<label for="reporte4" class="form-check-label">Reporte de Viáticos con vencimiento y saldo</label>																
				</div>
			</div>
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
					<input type="radio" name="reporte" id="reporte5" class="form-check-input" onclick="listareportes(5)" value="BoletosXUR"/>
					<label for="reporte4" class="form-check-label">Reporte de Boletos por UR</label>																
				</div>
			</div>
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
					<input type="radio" name="reporte" id="reporte7" class="form-check-input" onclick="listareportes(6)" value="AgendasP"/>
					<label for="reporte4" class="form-check-label">Reporte de Agendas Pendientes</label>																
				</div>
			</div>
			<div class="row d-flex">
				 <div class="col-md-1"></div>
				 <div class="col-8 col-lg-8 col-md-8 col-sm-8 p-1">
					<label for="uEjecutora" class="form-label unidadResp">Unidad Ejecutora:</label> 
					<select id="uEjecutora" name="uEjecutora" class="form-select unidadResp">
					</select>														
				</div>
			</div>
			<div class="row d-flex">
				<div class="col-md-1"></div>
				<div class="col-lg-3 col-md-3 col-sm-6">
					    <label for="fInicio" class="capturaFechas">Fecha Inicio</label>
					    <div class="input-group capturaFechas">
					      	<span class="input-group-text capturaFechas"><i class="bi bi-calendar3"></i></span> 
					      	<input type="text" class="form-control capturaFechas" id="fInicio" name="fInicio" size="15"  readonly/>
					    </div>
				</div>
			    <div class="col-lg-3 col-md-3 col-sm-6">
			      	<label for="fFin" class="capturaFechas">Fecha Fin</label>
			      	 <div class="input-group capturaFechas">
					      <span class="input-group-text capturaFechas"><i class="bi bi-calendar3"></i></span> 
					      <input type="text" class="form-control capturaFechas" id="fFin" name="fFin" size="15" onchange="verificaFechas()"  readonly/>
					 </div>
			    </div>
			</div>
			<div class="row d-flex">
				<div class="col-md-1"></div>
				<div class="col-md-2">					
					<input type="checkbox" name="cTodos" id="cTodos" class="form-check-input folioComision" value = "todos" onclick="seleccionaComision()" checked/>
					<label for="cTodos" class="form-check-label folioComision">Todos</label>
				</div>
				 <div class="col-2 col-lg-2 col-md-2 col-sm-2 p-1">
					<div class="input-group">
						<label for="folio" class="form-label folioComision">Folio:</label> 
						<input id="folio" name="folio" class="form-control folioComision" readonly/>
					</div>														
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