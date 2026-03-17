<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.poi.util.StringUtil"%>
<%@page contentType="text/html" pageEncoding="ISO-8859-1"%>
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
<link rel="stylesheet" type="text/css"	href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"	href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"> </script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"> </script>
<script type="text/javascript" src="../Generador/js/Firmantes.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript">

	$(document).ready(function() {
		
		queryFormPost("readFirmantesConciliacionesInventarios", {async: false});		
		cssReadOnly();						
		$( "#excel").button();
		$( "#descarga").button();
		$("#fechaf").val(moment().format('yyyy-MM-DD'));
		
		$("#nombre3").addClass("notEditable");
		$("#nombre3").attr("readonly",true);
		$("#nombre6").addClass("notEditable");
		$("#nombre6").attr("readonly",true);
				
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
			$("#TIPO_REPORTE").val("CONCILIABM");
		}else if (rep===2){
			$("#TIPO_REPORTE").val("CONCILIADBM");			
		}else if (rep===3){
			$("#TIPO_REPORTE").val("CONCILIARBM");			
		}else if (rep===4){
			$("#TIPO_REPORTE").val("CONCILIARDBM");			
		}else if (rep===5){
			$("#TIPO_REPORTE").val("CONCILIABC");			
		}else if (rep===6){
			$("#TIPO_REPORTE").val("CONCILIABI");			
		}else if (rep===7){
			$("#TIPO_REPORTE").val("CONCILIADBI");		
		}
		
		$("#nReporte").val(rep);
			
	}
	
	function firmasGRF(){	
		if ($("#chk_firmasGRF").attr("checked")){
			document.getElementById("nombre3").readOnly = false;						
			$("#nombre3").removeClass("notEditable");					
			$("#nombre3").addClass("Editable");			
		}else{
			$("#nombre3").attr("readonly",true);			
			$("#nombre3").removeClass("Editable");			
			$("#nombre3").addClass("notEditable");			
		}
	}
	
	function firmasGRMO(){	
		if ($("#chk_firmasGRMO").attr("checked")){
			document.getElementById("nombre3").readOnly = false;						
			$("#nombre6").removeClass("notEditable");					
			$("#nombre6").addClass("Editable");			
		}else{
			$("#nombre6").attr("readonly",true);			
			$("#nombre6").removeClass("Editable");			
			$("#nombre6").addClass("notEditable");			
		}
	}
	
	function extrae(){
		$("#fecha_fin").val($("#fechaf").val().split('-').reverse().join('/'));
		document.FormConciliacionesBienes.submit();
			
	}
	
</script>


</head>
<br/>
	<body id="dt_example">
		<form id="FormConciliacionesBienes" name="FormConciliacionesBienes" action="../reportes/ConciliacionesBienes" method="get" target="blank" >
								
			<input type="hidden" id="nReporte" name="nReporte" value="1"/>
			<input type="hidden" id="TIPO_REPORTE" name="TIPO_REPORTE" value="CONCILIABM"/>
			<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable %>"/>
			<input type="hidden" id="fecha_fin" name="fecha_fin"/>
			
			<div id="container" style="width: 80%" class="container">
				<div class="card-header"> <h3> Conciliaciones Bienes Muebles e Inmuebles </h3> </div>
				<hr class="mt-3"/>
				
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>							
					<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
						<input type="radio" name="reporte" id="reporte1" class="form-check-input" onclick="listareportes(1)" value="CONCILIABM" checked/>
						<label for="reporte1" class="form-check-label">Conciliación de Bienes Muebles</label>																
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>							
					<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
						<input type="radio" name="reporte" id="reporte2" class="form-check-input" onclick="listareportes(2)" value="CONCILIADBM"/>
						<label for="reporte1" class="form-check-label">Conciliación de Depreciación Bienes Muebles</label>																
					</div>
				</div>
				<!-- 
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>							
					<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
						<input type="radio" name="reporte" id="reporte3" class="form-check-input" onclick="listareportes(3)" value="CONCILIARBM"/>
						<label for="reporte1" class="form-check-label">Conciliación de Re-Expresión de Bienes Muebles</label>																
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>							
					<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
						<input type="radio" name="reporte" id="reporte4" class="form-check-input" onclick="listareportes(4)" value="CONCILIARDBM"/>
						<label for="reporte1" class="form-check-label">Conciliación de Re-Expresión de Depreciación de Bienes Muebles</label>																
					</div>
				</div>							
				-->
				
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>							
					<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
						<input type="radio" name="reporte" id="reporte5" class="form-check-input" onclick="listareportes(5)" value="CONCILIABC"/>
						<label for="reporte1" class="form-check-label">Conciliación de Bienes Consumibles</label>																
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>							
					<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
						<input type="radio" name="reporte" id="reporte6" class="form-check-input" onclick="listareportes(6)" value="CONCILIADBI"/>
						<label for="reporte1" class="form-check-label">Conciliación de Depreciación de Bienes Inmuebles</label>																
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>							
					<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
						<input type="radio" name="reporte" id="reporte7" class="form-check-input" onclick="listareportes(7)" value="CONCILIABI"/>
						<label for="reporte1" class="form-check-label">Conciliación de Bienes Inmuebles</label>																
					</div>
				</div>
				
				<br/>
				
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 ">
						<h6> <strong>Fecha</strong> </h6>
					</div>					
				</div>
				
				<div class="row">				
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>										
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 ">
						<div class="form-group">			                	
		                    <div class="input-group date" id="datepicker1">
		                    	<input type="date" class="form-control form-control-sm" id="fechaf" name="fechaf"/>                                    
		                    </div>
		                </div>										
					</div>					
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
						<input type="button" id="excel" name="btn_excel" size="5" class="btn btn-secondary btn-sm" value="  EXTRAE  " onclick="extrae()" class="btnInterfaceBG"/>
					</div>						
				</div>			
				
				<br/>
				<!-- CG_GRUPO_PROPIEDADES GP_NOMBRE LIKE 'nombre_firma_conciliacion%' -->
				<div class="container" style="width: 80%">
					<h5> Firmas Gerencia de Programación y Presupuestos </h5>
					<hr class="mt-3"/>
				
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
						</div>
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<div class="input-group">								
								<span class="input-group-text"><i class="bi bi-person"></i></span>
								<input type="text" id="nombre1" name="nombre1" class="form-control form-control-sm" style="width: 20em;" maxlength="200" value="" />
							</div> 
						</div>
					</div>
			
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
						</div>
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<div class="input-group">								
								<span class="input-group-text"><i class="bi bi-person"></i></span>
								<input type="text" id="nombre2" name="nombre2" class="form-control form-control-sm" style="width: 20em;" maxlength="200" value="" />
							</div> 
						</div>									
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
						</div>
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<div class="input-group">								
								<span class="input-group-text"><i class="bi bi-person"></i></span>
								<input type="text" id="nombre3" name="nombre3" class="form-control form-control-sm" style="width: 20em;" maxlength="200" value="" />
							</div> 
						</div>
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<input type="checkbox" class="form-check-input" name="chk_firmasGRF" id="chk_firmasGRF" onclick="firmasGRF()" value=4 />
						</div>
					</div>
				
					<br/>
					
					<h5> Firmas Gerencia de Recursos Materiales </h5>
					<hr class="mt-3"/>
					
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
						</div>
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<div class="input-group">								
								<span class="input-group-text"><i class="bi bi-person"></i></span>
								<input type="text" id="nombre4" name="nombre4" class="form-control form-control-sm" style="width: 20em;" maxlength="200" value="" />
							</div> 
						</div>
					</div>
			
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
						</div>
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<div class="input-group">								
								<span class="input-group-text"><i class="bi bi-person"></i></span>
								<input type="text" id="nombre5" name="nombre5" class="form-control form-control-sm" style="width: 20em;" maxlength="200" value="" />
							</div> 
						</div>									
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
						</div>
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<div class="input-group">								
								<span class="input-group-text"><i class="bi bi-person"></i></span>
								<input type="text" id="nombre6" name="nombre6" class="form-control form-control-sm" style="width: 20em;" maxlength="200" value="" />
							</div> 
						</div>
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<input type="checkbox" class="form-check-input" name="chk_firmasGRMO" id="chk_firmasGRMO" onclick="firmasGRMO()" value=4 />
						</div>
					</div>
											
					<!-- <form id="FormExtraccionPolizas" name="FormExtraccionPolizas" action="com/syc/ws/controlinventarios/ControlInventariosWSServlet">
						<input type="button" id="descarga" name="btn_descarga" size="5"  value="  DESCARGAR  " onclick="descargar()" class="btnInterfaceBG"/>
					</form>
					 -->						
				</div>			
			</div>								
			
		</form>
	</body>
</html>