<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.poi.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@ page 
	import="java.util.Calendar"
	import="com.syc.gestion.core.Usuario"
	import="com.syc.gestion.servlet.GestionInterface"
%>

<%@ page import="java.util.*,java.sql.*,com.syc.gestion.*,com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.Util" %>
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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Reimpresion Pagos</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css">
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"> </script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		$("#formato").button();
		
	});
		
	function listareportes(rep){	
		
		$("#nReporte").val(rep);
							
			if (rep===1){
				$("#TIPO_FORMATO").val("PolizaPago");
				document.getElementById("ckFiel").disabled = false;
			}else if (rep===2){
				$("#TIPO_FORMATO").val("PolizaPagoN");
				document.getElementById("ckFiel").disabled = false;
			}else if (rep===3){
				$("#TIPO_FORMATO").val("PolizaPagoN");
				document.getElementById("ckFiel").disabled = false;
			}else if (rep===4){
				$("#TIPO_FORMATO").val("PolizaPagoN");
				document.getElementById("ckFiel").disabled = false;
			}else if (rep===5){
				$("#TIPO_FORMATO").val("PolizaPagoN");
				document.getElementById("ckFiel").disabled = false;
			}else if (rep===6){
				$("#TIPO_FORMATO").val("PolizaPagoNuevoN");
				document.getElementById("ckFiel").disabled = false;
			}else if (rep===7){
				$("#TIPO_FORMATO").val("PolizaOperAjenas");
				document.getElementById("ckFiel").disabled = false;
			}else if (rep===8){
				$("#TIPO_FORMATO").val("PolizaPenas");
				document.getElementById("ckFiel").disabled = true;
				document.getElementById("ckFiel").checked = false;
			}else if (rep===9){
				$("#TIPO_FORMATO").val("PolizaInformeComisionRG");
				document.getElementById("ckFiel").checked = false;
			}
	}
	
	function extrae(){
		var miCheckbox = document.getElementById('ckFiel');
		
		if(miCheckbox.checked){	
			$("#fiel").val("SI");			
		} else {
			$("#fiel").val("NO");			
		}	
		document.FormFormatoSipot.submit();		
	}

</script>

</head>
<body id="dt_example">
<br/>
	<form id="FormFormatoSipot" name="FormFormatoSipot" action="../reportes/ReImpresionFormatos" method="post" target="blank">
		<input type="hidden" id="TIPO_FORMATO" name="TIPO_FORMATO" value="PolizaPago">
		<input type="hidden" id="nReporte" name="nReporte" value="1">
		<input type="hidden" id="fiel" name="fiel">
		
		<div id="container" class="container" style="width: 80%">
			
			<div class="card-header"> <h3> Re-Imprimir formatos de Pagos </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="cxp" class="form-label"> Cuenta por Pagar: </label>											
				</div>	
        		<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="text" name="cxp" id="cxp" class="form-control form-control-sm" placeholder="10CP0000000000"/>
				</div>				
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">																						
					<input type="radio" name="formato" id="formatoRG" class="form-check-input" onclick="listareportes(1)" value="RG" checked/>
					<label for="formatoRG" class="form-check-label">Relacion Gastos</label>																
				</div>
			</div>				
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">											
					<input type="radio" name="formato" id="formatoFE" class="form-check-input" onclick="listareportes(2)" value="FE"/>
					<label for="formatoFE" class="form-check-label">Federalizado</label>												
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">										
					<input type="radio" name="formato" id="formatoDV" class="form-check-input" onclick="listareportes(3)" value="DV"/>
					<label for="formatoDV" class="form-check-label">Diverso</label>											
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">										
					<input type="radio" name="formato" id="formatoRGOC" class="form-check-input" onclick="listareportes(4)" value="RGOC"/>
					<label for="formatoRGOC" class="form-check-label">Relacion Gastos con Orden de Compra</label>												
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">										
					<input type="radio" name="formato" id="formatoOB" class="form-check-input" onclick="listareportes(5)" value="OB"/>
					<label for="formatoOB" class="form-check-label">Obra</label>												
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">										
					<input type="radio" name="formato" id="formatoDR" class="form-check-input" onclick="listareportes(6)" value="DR"/>
					<label for="formatoDR" class="form-check-label">Directo</label>												
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">										
					<input type="radio" name="formato" id="formatoOA" class="form-check-input" onclick="listareportes(7)" value="OA"/>
					<label for="formatoOA" class="form-check-label">Operaciones Ajenas</label>												
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">					
					<input type="radio" name="formato" id="formatoPN" class="form-check-input" onclick="listareportes(8)" value="PN"/>
					<label for="formatoPN" class="form-check-label">Penalizaciones</label>												
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">					
					<input type="radio" name="formato" id="formatoIC" class="form-check-input" onclick="listareportes(9)" value="IC"/>
					<label for="formatoIC" class="form-check-label">Informe Comision</label>												
				</div>
			</div>
			
			<br/>
						
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<label for="fiel" class="form-check-label">FIEL</label>
					<input type="checkbox" name="ckFiel" id="ckFiel" class="form-check-input" value="fiel"/>																																			
				</div>					
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1" >
					<input type="button" id="formato" name="btn_formato" value="  EXTRAE  " class="btn btn-secondary btn-sm" onclick="extrae()"/>
				</div>												
			</div>
		
	</form>
</body>
</html>