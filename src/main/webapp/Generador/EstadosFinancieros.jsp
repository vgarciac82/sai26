<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.poi.util.StringUtil"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	System.out.println(efa);
	
	String login=usuario.getLogin();
	String cCentroContable=(usuario.getPropiedad("CCENTROCONTABLE") != null? (usuario.getPropiedad("CCENTROCONTABLE").getValor() != null? usuario.getPropiedad("CCENTROCONTABLE").getValor():"" ):"" );
	
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
	//String sufijoReporte = StringUtils.isEmpty( cabl.getSystemSetting("SUFIJO_RPT_FINANCIEROS")  ) ? "": cabl.getSystemSetting("SUFIJO_RPT_FINANCIEROS"); 
	String ocultaFirmante =  cabl.getSystemSetting("OCULTA FIRMANTE").toUpperCase(); 
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
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

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
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"> </script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>

<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>		
<script type="text/javascript">

var ocultaFirmante = "<%=ocultaFirmante%>";
	
	$(document).ready(function() {

		queryFormPost("readFirmantesEstadosFinancieros", {async: false});
		queryFormPost("descCentroContableRead", {async: false});
		queryFormPost("puedeFirmantarRead", {async: false});
		
		$("#cMesAA").css('display', 'none');
		$("#cEsConac").css('display', 'none'); 
		
		cssReadOnly();		
		$("#lblDesagrega").hide();
		$("#dlgcomenterios").hide();
		$("#nombre4").attr("readonly",true);
		$("#cargo4").attr("readonly",true);
		$("#nombre4").removeClass("Editable");
		$("#cargo4").removeClass("Editable");
		$("#nombre4").addClass("notEditable");
		$("#cargo4").addClass("notEditable");
		
		if( ocultaFirmante == "N"){
			$("#divingreso").hide();
			$("#divnombre1").show();
			$("#divcargo1").show();
			$("#divEmp1").show();
		}else {
			$("#divingreso").show();
			$("#divnombre1").hide();
			$("#divcargo1").hide();
			$("#divEmp1").hide();
		}
		
		if ($("#puedeEnviaraFirmar").val() == "1" ){
			$("#divFiel").show();
		} else {
			$("#divFiel").hide();
		}
		
		$("#btn_saldo").button();
		$("#excel").button();
	});
		
	function cssReadOnly(){
		$( "[readOnly]" ).each(
			function(){	
				$(this).addClass("notEditable");	
		});
	}
	
	function firmass(){
		if ($("#chk_firmas").attr("checked")){
			document.getElementById("nombre4").readOnly = false;
			document.getElementById("cargo4").readOnly = false;
			$("#nombre4").removeClass("notEditable");
			$("#cargo4").removeClass("notEditable");			
			$("#nombre4").addClass("Editable");
			$("#cargo4").addClass("Editable");
		}else{
			$("#nombre4").attr("readonly",true);
			$("#cargo4").attr("readonly",true);
			$("#nombre4").removeClass("Editable");
			$("#cargo4").removeClass("Editable");
			$("#nombre4").addClass("notEditable");
			$("#cargo4").addClass("notEditable");
		}
	}
	
	function listareportes(rep){
	 					
		if (rep===1){
			$("#reporteTipo").val("BalanzaMayor.jasper");
			$("#dlgcomenterios").css('display', 'none');
			$("#cEsConac").css('display', 'none'); 
			$("#chk_desagrega").css('display', 'none');
			$("#lblDesagrega").css('display', 'none');
			$("#cMesAA").css('display', 'none'); 
		}else if (rep===2){
			$("#reporteTipo").val("EdoSituacionFinanciera.jasper");
			$("#dlgcomenterios").css('display', 'none');
			$("#cEsConac").css('display', 'block');
			$("#chk_desagrega").css('display', 'none');
			$("#lblDesagrega").css('display', 'none');
			$("#cMesAA").css('display', 'block');
		}else if (rep===3){
			$("#reporteTipo").val("EdoActividades.jasper");
			$("#dlgcomenterios").css('display', 'none');
			$("#cEsConac").css('display', 'block');
			$("#chk_desagrega").css('display', 'none');
			$("#lblDesagrega").css('display', 'none');
			$("#cMesAA").css('display', 'block');
		}else if (rep===4){
			$("#reporteTipo").val("EdoVariacionHacienda.jasper");
			$("#dlgcomenterios").css('display', 'none');
			$("#cEsConac").css('display', 'block');
			$("#chk_desagrega").css('display', 'none');
			$("#lblDesagrega").css('display', 'none');
			$("#cMesAA").css('display', 'none');
		}else if (rep===5){
			$("#reporteTipo").val("EdoAnaliticoActivo.jasper");
			$("#dlgcomenterios").css('display', 'none');
			$("#cEsConac").css('display', 'block');
			$("#chk_desagrega").css('display', 'none');
			$("#lblDesagrega").css('display', 'none');
			$("#cMesAA").css('display', 'none');
		}else if (rep===6){
			$("#reporteTipo").val("EdoAnaliticoPasivo.jasper");
			$("#dlgcomenterios").css('display', 'none');
			$("#chk_desagrega").css('display', 'none');
			$("#lblDesagrega").css('display', 'none');
			$("#cMesAA").css('display', 'none');
		}else if (rep==7){
			$("#reporteTipo").val("EstadoActivoNoCirculante.jasper");
			$("#dlgcomenterios").css('display', 'none');
			$("#cEsConac").css('display', 'none');
			$("#chk_desagrega").css('display', 'none');
			$("#lblDesagrega").css('display', 'none');
			$("#cMesAA").css('display', 'none');
		}else if (rep==8){
			$("#reporteTipo").val("EdoFlujoEfectivo.jasper");
			$("#dlgcomenterios").css('display', 'none');
			$("#cEsConac").css('display', 'block');
			$("#chk_desagrega").css('display', 'none');
			$("#lblDesagrega").css('display', 'none');
			$("#cMesAA").css('display', 'block');
		}else if (rep==9){
			$("#reporteTipo").val("EdoCambiosSituacionFinanciera.jasper");
			$("#dlgcomenterios").css('display', 'none');
			$("#cEsConac").css('display', 'block');
			$("#chk_desagrega").css('display', 'none');
			$("#lblDesagrega").css('display', 'none');
			$("#cMesAA").css('display', 'block');
		}else if (rep==10){			
			$("#dlgcomenterios").css('display', 'block');				
			$("#reporteTipo").val("InfPasivosContingentes.jasper");		
			$("#imprimeCaracteristicas").val("InfPasivosCaracteristicas");		
			$("#imprimeDetalle").val("InfPasivosDetalle");	
			$("#cEsConac").css('display', 'none');	
			$("#chk_desagrega").css('display', 'none');
			$("#lblDesagrega").css('display', 'none');
			$("#cMesAA").css('display', 'none');
		}else if (rep==11){				
				$("#reporteTipo").val("ConciliacionCONAC.jasper");			
				$("#dlgcomenterios").css('display', 'none');
				$("#cEsConac").css('display', 'none');
				$("#chk_desagrega").css('display', 'block');
				$("#lblDesagrega").css('display', 'none');
				$("#chk_desagrega").css('display', 'none');
				$("#cMesAA").css('display', 'none');
			/*	if ($("#excel").attr("checked")){
					$("#desagregada").css('display', 'none');				
				} 
				else if($("#desagregada").attr("checked")){
					$("#excel").css('display', 'none');		
				}*/						
		}
		else if (rep==12){									
			$("#reporteTipo").val("ConciliacionCONACEg.jasper");			
			$("#dlgcomenterios").css('display', 'none');	
			$("#cEsConac").css('display', 'none');
			$("#chk_desagrega").css('display', 'block');
			$("#lblDesagrega").css('display', 'block');
			$("#cMesAA").css('display', 'none');
			if ($("#excel").attr("checked")){
				$("#desagregada").val("");				
			} 
			else if($("#desagregada").attr("checked")){
				$("#excel").val("");		
			}	
		}else if (rep==13){
			$("#reporteTipo").val("EdoAnaliticoPasivoCONAC.jasper");
			$("#dlgcomenterios").css('display', 'none');
			$("#cEsConac").css('display', 'none');
			$("#chk_desagrega").css('display', 'none');
			$("#lblDesagrega").css('display', 'none');
			$("#cMesAA").css('display', 'none');
		}
		$("#nReporte").val(rep);
		
		if (rep==1 || rep==4 || rep==6 || rep==7 || rep==8 || rep==10 || rep==11 || rep==12){ //Se desactivan los radiobutton del nivel ya que en estos reportes no se necesita el nivel
			$("#detalle3").attr('disabled',true);
			$("#detalle4").attr('disabled',true);
		}
		else{ //Se activan los radiobutton para los reportes que si se necesite capturar el nivel
			$("#detalle3").prop("disabled", false);
			$("#detalle4").prop("disabled", false);
		}
		
		
	}
	
	function valida(){
		if ($("#nombre1").val()=="" & ocultaFirmante == "N"){
			alert("Favor de capturar el nombre del primer firmante");
			return false;
		}
		if ($("#cargo1").val()=="" & ocultaFirmante == "N"){
			alert("Favor de capturar el cargo del primer firmante");
			return false;
		}
		if ($("#nombre2").val()==""){
			alert("Favor de capturar el nombre del segundo firmante");
			return false;
		}
		if ($("#cargo2").val()==""){
			alert("Favor de capturar el cargo del segundo firmante");
			return false;
		}
		if ($("#nombre3").val()==""){
			alert("Favor de capturar el nombre del tercer firmante");
			return false;
		}
		if ($("#cargo3").val()==""){
			alert("Favor de capturar el cargo del tercer firmante");
			return false;
		}
		if ($("#chk_firmas").attr("checked")){//En caso de estar seleccionado el CheckBox de la 4ta firma valida que exista nombre y firma de dicha firma
			if ($("#nombre4").val()==""){
				alert("Favor de capturar el nombre del cuarto firmante");
				return false;
			}
			if ($("#cargo4").val()==""){
				alert("Favor de capturar el cargo del cuarto firmante");
				return false;
			}
		}		
		if ($("#comentarios").val()==""){
			alert("Favor de capturar los comentarios");
			return false;
		}
		if ($("#caracteristicas").val()==""){
			alert("Favor de capturar las carateristicas");
			return false;
		}					
		
		return true;
	}
		
	function extrae(){
		$("#Fecha").val($("#fechaf").val().split('-').reverse().join('/'));
	
		if( $("#chk_esConac").attr("checked") )
			$("#esConac").val("S");		
		else
			$("#esConac").val("N");
			
		if( $("#chk_mesAA").attr("checked") )
			$("#esMesAA").val("S");		
		else
			$("#esMesAA").val("N");		
		
		if( $("#chk_esFIEL").attr("checked") )
			$("#esFiel").val("S");		
		else
			$("#esFiel").val("N");
		document.FormEdosFinancieros.submit();			
	}
	
	function deshabilitarExcel(){
		if( $("#chk_esFIEL").attr("checked") ){
			$("#lblExcel").css('display', 'none');
			$("#chk_excel").css('display', 'none');
		} else {
			$("#chk_excel").css('display', 'block');
			$("#lblExcel").css('display', 'block');
			}	
	}
	function repExcel(){

		if( $("input[name=chk_esConac]:checked"))
			$("#esConac").val("S");		
		else
			$("#esConac").val("N");
		
		if( $("input[name=chk_mesAA]:checked"))
			$("#esMesAA").val("S");		
		else
			$("#esMesAA").val("N");
		
		document.FormEdosFinancieros.submit();				
	}
	function saldos(){
		$.blockUI({message: "Procesando espere ......"});
		$.ajax({
				url : '../reportes/ReporteEstadosFinancieros',
				dataType : 'json',
				type :"GET",
				data : {
					"mes": "12"
				},
				async : false,
				success : function(json) {
					if( json.valueOf()=="success"){
						Swal.fire("OK","Saldos verificados correctamente","success");
						$.unblockUI();
					}
				},
				error : function (){
					Swal.fire(" No se corrieron los saldos.", "Por favor intente mas tarde.","info");
					$.unblockUI();	
					return false;}
			});
		
	}
	
</script>


</head>
<body id="dt_example">
<br/>
	<form id="FormEdosFinancieros" name="FormEdosFinancieros" action="../reportes/ReporteEstadosFinancieros" method="post" target="blank" onsubmit="return valida();">
		<input type="hidden" id="reporteTipo" name="reporteTipo" value="BalanzaMayor.jasper"/>
		<input type="hidden" id="imprimeCaracteristicas" name="imprimeCaracteristicas" value="1"/>
		<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>"/>
		<input type="hidden" id="imprimeDetalle" name="imprimeDetalle" value="2"/>
		<input type="hidden" id="nReporte" name="nReporte" value="1"/>
		<input type="hidden" id="cdescripcion" name="cdescripcion" value=" "/>
		<input type="hidden" id="anio" name="anio" value=" "/>
		<input type="hidden" id="mes" name="mes" value=" "/>
		<input type="hidden" id="esConac" name="esConac" value="N"/>
		<input type="hidden" id="esMesAA" name="esMesAA" value="N"/>			
		<input type="hidden" id="esFiel" name="esFiel" value=""/>	
		<input type="hidden" id="Fecha" name="Fecha">
		<input type="hidden" id="puedeEnviaraFirmar" name="puedeEnviaraFirmar" value ="0">		
		<input type="hidden" id="usuario" name="usuario" value="<%=login%>">
		 
		<div id="container" class="container" style="width: 80%">
			<div class="row">
				<div class="col-12 col-lg-9 col-md-9 col-sm-12">
					<div class="card-header"> <h5> Estados Financieros </h5> </div>
					<hr class="mt-3"/>
					
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>							
						<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
							<input type="radio" name="reporte" id="reporte1" class="form-check-input" onclick="listareportes(1)" value="BalanzaMayor.jasper" checked/>
							<label for="reporte1" class="form-check-label">Balanza de Comprobacion (Nivel Mayor)</label>																
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>							
						<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
							<input type="radio" name="reporte" id="reporte2" class="form-check-input" onclick="listareportes(2)" value="EdoSituacionFinanciera.jasper"/>
							<label for="reporte2" class="form-check-label">Estado de Situación Financiera</label>																
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>							
						<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
							<input type="radio" name="reporte" id="reporte3" class="form-check-input" onclick="listareportes(3)" value="EdoActividades.jasper"/>
							<label for="reporte3" class="form-check-label">Estado de Actividades</label>																
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>							
						<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
							<input type="radio" name="reporte" id="reporte4" class="form-check-input" onclick="listareportes(4)" value="EdoVariacionHacienda.jasper"/>
							<label for="reporte4" class="form-check-label">Estado de Variaciones en la Hacienda Pública</label>																
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>							
						<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
							<input type="radio" name="reporte" id="reporte5" class="form-check-input" onclick="listareportes(5)" value="EdoAnaliticoActivo.jasper"/>
							<label for="reporte5" class="form-check-label">Estado Analítico del Activo</label>																
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>							
						<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
							<input type="radio" name="reporte" id="reporte6" class="form-check-input" onclick="listareportes(6)" value="EdoAnaliticoPasivo.jasper"/>
							<label for="reporte6" class="form-check-label">Estado Analítico de la Deuda Pública y Otros Pasivos</label>																
						</div>
					</div>
					
					<div class="row" id="divingreso" title="ingreso">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>							
						<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
							<input type="radio" name="reporte" id="reporte13" class="form-check-input" onclick="listareportes(13)" value="EdoAnaliticoPasivoCONAC.jasper"/>
							<label for="reporte11" class="form-check-label">Estado Analítico de la Deuda Pública y Otros Pasivos CONAC</label>																
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>							
						<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
							<input type="radio" name="reporte" id="reporte7" class="form-check-input" onclick="listareportes(7)" value="EstadoActivoNoCirculante.jasper"/>
							<label for="reporte7" class="form-check-label">Estado Analítico del Activo no Circulante</label>																
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>							
						<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
							<input type="radio" name="reporte" id="reporte8" class="form-check-input" onclick="listareportes(8)" value="EdoFlujoEfectivo.jasper"/>
							<label for="reporte8" class="form-check-label">Flujo de Efectivo</label>																
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>							
						<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
							<input type="radio" name="reporte" id="reporte9" class="form-check-input" onclick="listareportes(9)" value="EdoCambiosSituacionFinanciera.jasper"/>
							<label for="reporte9" class="form-check-label">Estado de Cambios en la Situacion Financiera</label>																
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>							
						<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
							<input type="radio" name="reporte" id="reporte10" class="form-check-input" onclick="listareportes(10)" value="InfPasivosContingentes.jasper"/>
							<label for="reporte10" class="form-check-label">Informe de Pasivos Contingentes</label>																
						</div>
					</div>
					
					<div class="row" id="divingreso" title="ingreso">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>							
						<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">
							<input type="radio" name="reporte" id="reporte11" class="form-check-input" onclick="listareportes(11)" value="ConciliacionCONAC.jasper"/>
							<label for="reporte11" class="form-check-label">Conciliacion  Contable - Presupuestal Ingreso</label>																
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>							
						<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
							<input type="radio" name="reporte" id="reporte12" class="form-check-input" onclick="listareportes(12)" value="ConciliacionCONACEg.jasper"/>
							<label for="reporte12" class="form-check-label">Conciliacion  Contable - Presupuestal Egreso</label>																
						</div>							
						<!-- 
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1" id="lblDesagrega">
							<input type="checkbox" name="desagregada" id="chk_desagrega" class="form-check-input" value="desagregada"/>
							<label for="desagregada" class="form-check-label">Desagregación</label>																
						</div>
						 -->
					</div>			
					
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>
					</div>
					<div class="row">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>
					</div>		
				</div>
								
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					<div class="card-header"> <h5> Filtros </h5> </div>
					<hr class="mt-3"/>
					
					<div class="row">
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<input type="button" class="btn btn-primary btn-sm" id="btn_saldo" name="btn_saldo" value="  Verificar Saldos  " onclick="saldos()">
						</div>
					</div>	
					
					<div class="row">
						<div class="col-12 col-lg-6 col-md-6 col-sm-12 ">
							<h6> <strong>Moneda</strong> </h6>
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-6 col-md-6 col-sm-12 ">
							<input type="radio" id="pesos" name="Moneda" class="form-check-input" value="0" checked> Pesos
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-6 col-md-6 col-sm-12 ">
							<input type="radio" id="miles" name="Moneda" class="form-check-input" value="1"> Miles				
						</div>
					</div>
					
					<br/>
					
					<div class="row">
						<div class="col-12 col-lg-6 col-md-6 col-sm-12 ">
							<h6> <strong>Nivel</strong> </h6>							
						</div>
					</div>
					
					<div class="row">							
						<div class="col-12 col-lg-6 col-md-6 col-sm-12 ">
							<input type="radio" id="detalle3" name="Formato" class="form-check-input" value="3" disabled> 3
						</div>
					</div>
					<div class="row">
						<div class="col-12 col-lg-6 col-md-6 col-sm-12 ">
							<input type="radio" id="detalle4" name="Formato" class="form-check-input" value="4" disabled checked> 4				
						</div>	
					</div>
					
					<br/>
					
					<div class="row">
						<div class="col-12 col-lg-6 col-md-6 col-sm-12 ">
							<h6> <strong>Fecha</strong> </h6>
						</div>					
					</div>
					
					<div class="row">														
						<div class="col-12 col-lg-6 col-md-6 col-sm-12 ">
							<div class="form-group">			                	
			                    <div class="input-group date" id="datepicker1">
			                    	<input type="date" class="form-control form-control-sm" id="fechaf" name="fechaf"/>                                    
			                    </div>
			                </div>										
						</div>				
					</div>
					
					<br/>
					
					<div class="row" id="divFiel">
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
							<strong>Firma Electrónica:&nbsp;</strong><input type="checkbox" id="chk_esFIEL" name="chk_esFIEL" class="form-check-input" onclick="deshabilitarExcel();">
						</div>
					</div>
											
					<div class="row">
						<div class="col-6 col-lg-6 col-md-6 col-sm-12 p-1" id="cEsConac">
							Es CONAC:&nbsp;<input type="checkbox" name="chk_esConac" id="chk_esConac" class="form-check-input"/>
						</div>
						<div class="col-6 col-lg-6 col-md-6 col-sm-12 p-1" id="cMesAA">
							Vs mismo mes AA:&nbsp;<input type="checkbox" name="chk_mesAA" id="chk_mesAA" class="form-check-input"/>
						</div>
					</div>								
					
					<div class="row">
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">							
							<label id="lblExcel"> EXCEL </label> 
							<input type="checkbox" id="chk_excel" name="excel" class="form-check-input" value="excel"/>							
							<input type="button" class="btn btn-secondary" id="excel" name="btn_excel" value="  EXTRAE  " onclick="extrae()">							
						</div>
					</div>	
				</div>
			</div>
			
			<br/>
						
			<div class="row">
				<div class="col-12 col-lg-12 col-md-12 col-sm-12">
					<h5> Firmas </h5>
					<hr class="mt-3"/>
					
					<div id="divnombre1">
						<div class="row">
							<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
								<label for="nombre1"> Nombre&nbsp; </label> 
							</div>
							<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
								<label for="cargo1"> Cargo&nbsp; </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1" id="divEmp1" title="NoEmp1">								
								<label for="noEmp1"> No Emp.&nbsp; </label>								
							</div>
						</div>
						<div class="row">
							<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
								<div class="input-group">
									<span class="input-group-text"><i class="bi bi-person"></i></span>
									<input type="text" id="nombre1" name="nombre1" class="form-control form-control-sm" style="width: 30em;" maxlength="200" value="" />
								</div> 
							</div>
							<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
								<div class="input-group">
									<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
									<input type="text" id="cargo1" name="cargo1" class="form-control form-control-sm" style="width: 30em;" maxlength="200" value="" />
								</div>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1" id="divEmp1" title="NoEmp1">								
								<div class="input-group">
									<span class="input-group-text">#</span>
									<input type="text" id="numEmpleado1" name="numEmpleado1" class="form-control form-control-sm" style="width: 5em;" maxlength="5" value="" />								
								</div>
							</div>
						</div>
					</div>
							
					<div class="row">
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<label for="nombre2"> Nombre&nbsp; </label> 
						</div>
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<label for="cargo2"> Cargo&nbsp; </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1" id="divEmp1" title="NoEmp1">								
							<label for="noEmp2"> No Emp.&nbsp; </label>								
						</div>
					</div>
					<div class="row">
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-person"></i></span>
								<input type="text" id="nombre2" name="nombre2" class="form-control form-control-sm" style="width: 30em;" maxlength="200" value="" />
							</div> 
						</div>
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
								<input type="text" id="cargo2" name="cargo2" class="form-control form-control-sm" style="width: 30em;" maxlength="200" value="" />
							</div>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1" id="divEmp1" title="NoEmp1">								
							<div class="input-group">
								<span class="input-group-text">#</span>
								<input type="text" id="numEmpleado2" name="numEmpleado2" class="form-control form-control-sm" style="width: 5em;" maxlength="5" value="" />								
							</div>
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<label for="nombre3"> Nombre&nbsp; </label> 
						</div>
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<label for="cargo3"> Cargo&nbsp; </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1" id="divEmp1" title="NoEmp1">								
							<label for="noEmp3"> No Emp.&nbsp; </label>								
						</div>
					</div>
					<div class="row">
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-person"></i></span>
								<input type="text" id="nombre3" name="nombre3" class="form-control form-control-sm" style="width: 30em;" maxlength="200" value="" />
							</div> 
						</div>
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
								<input type="text" id="cargo3" name="cargo3" class="form-control form-control-sm" style="width: 30em;" maxlength="200" value="" />
							</div>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1" id="divEmp1" title="NoEmp1">
							<div class="input-group">
								<span class="input-group-text">#</span>								
								<input type="text" id="numEmpleado3" name="numEmpleado3" class="form-control form-control-sm" style="width: 5em;" maxlength="5" value="" />								
							</div>	
						</div>
					</div>
					
					<div class="row">
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<label for="nombre4"> Nombre&nbsp; </label> 
						</div>
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<label for="cargo4"> Cargo&nbsp; </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1" id="divEmp1" title="NoEmp1">								
							<label for="noEmp4"> No Emp.&nbsp; </label>						
						</div>
					</div>		
					<div class="row">
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-person"></i></span>
								<input type="text" id="nombre4" name="nombre4" class="form-control form-control-sm" style="width: 30em;" maxlength="200" value=""/>
							</div> 
						</div>
						<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
								<input type="text" id="cargo4" name="cargo4" class="form-control form-control-sm" style="width: 30em;" maxlength="200" value=""/>
							</div>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1" id="divEmp1" title="NoEmp1">								
							<div class="input-group">
								<span class="input-group-text">#</span>		
								<input type="text" id="numEmpleado4" name="numEmpleado4" class="form-control form-control-sm" style="width: 5em;" maxlength="5" value=""/>
							</div>
							<input type="checkbox" name="chk_firmas" id="chk_firmas" onclick="firmass()" class="form-check-input" value="4"/>								
						</div>
					</div>							 
				</div>
			</div>
		</div>
		
		<div id="dlgcomenterios" title="Comentarios y Caracteristicas" class="container" style="width: 80%">
			<div class="row">
				<div class="col-12 col-lg-12 col-md-12 col-sm-12">
					<fieldset class="form-group border px-3"> <br/>		
						<div class="card-header"> <h5> Captura de Comentarios y Caracteristicas </h5> </div>
						<hr class="mt-3"/>
						
						<div class="row d-flex justify-content-center">
							<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
								<label for="comentarios" class="form-label"> Comentarios </label>
								<textarea id="comentarios" name="comentarios" rows="7" cols="80" class="form-control"></textarea>
							</div>
						</div>
						
						<div class="row d-flex justify-content-center">
							<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
								<label for="caracteristicas" class="form-label"> Caracteristicas </label>
								<textarea id="caracteristicas" name="caracteristicas" rows="7" cols="80" class="form-control"></textarea>
							</div>
						</div>																								
					</fieldset>
				</div>
			</div>
		</div>	
	</form>
</body>
</html>