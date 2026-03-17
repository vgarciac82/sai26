<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.poi.util.StringUtil"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@page language="java" pageEncoding="utf-8"%>
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
	
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	System.out.println(efa);
	
	String login=usuario.getLogin();
	String cCentroContable=(usuario.getPropiedad("CCENTROCONTABLE") != null? (usuario.getPropiedad("CCENTROCONTABLE").getValor() != null? usuario.getPropiedad("CCENTROCONTABLE").getValor():"" ):"" );
	
	//ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
	//String sufijoReporte = StringUtils.isEmpty( cabl.getSystemSetting("SUFIJO_RPT_FINANCIEROS")  ) ? "": cabl.getSystemSetting("SUFIJO_RPT_FINANCIEROS"); 
	//String ocultaFirmante =  cabl.getSystemSetting("OCULTA FIRMANTE").toUpperCase(); 
%>



<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   	<title>NotasEdosFinancieros</title>

<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>

<script src="../Generador/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>

<script type="text/javascript">

$(document).ready(function() {
		
		$("#excel").button();
		$("#fechaI").val(moment().format('yyyy-MM-DD'));
		
		 $('input:radio[name=pdf]').click(function() {
		        let nameReport = $( "input[name=pdf]:checked" ).val();                 
		        $("#tipoReporte").val(nameReport);
		    });  
				
	});
	
	function extrae(){
		$("#Fecha").val($("#fechaI").val().split('-').reverse().join('/'));
		document.ExportarForm.submit();	
	};
	
</script>

  </head>
  <body id="dt_example"> 
  <br/>
  	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReporteNotas" method="post" target="blank">
  		<input type="hidden" id="Fecha" name="Fecha"/>
  		<input type="hidden" id="tipoReporte" name="tipoReporte" value="excel"/>
  		
		<div id="container" style="width: 90%" class="container">			
		
			<div class="card-header"> <h3> Notas a los Estados Financieros </h3> </div>
			<hr class="mt-3"/>
			
			<h5>Tipo de Reporte</h5>
			
			<div class="row">												
	        	<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">
	        		<div class="row">
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex">
							<input type="radio" name="reporte" id="reporte1" value="resumen" class="form-check-input" checked/> &nbsp;Resumen (Trimestral)
						</div>												
						<div class="row">
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex">
								<input type="radio" name="reporte" id="reporte2" value="detalle" class="form-check-input"/> &nbsp;Detalle (Anual)				
							</div>
						</div>		
					</div>											
				</div>								
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
					<label for="fechaF" class="form-label"> Fecha:&nbsp; </label>
					<div class="form-group">
                       <div class="input-group date" id="datepicker1">
                           <input type="date" class="form-control form-control-sm" id="fechaI" name="fechaI"/>                                
                       </div>
                	</div>												
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex">																	
				</div>						
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">	
																										
						<div class="row">
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex" style="padding-left: 25px">
								<h6>Moneda</h6>
							</div>
							<div class="row">
								<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex" style="padding-left: 35px">
									<input type="radio" name="Moneda" id="pesos" value="0" class="form-check-input" checked/> &nbsp;Pesos
								</div>
								<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
									<input type="radio" name="Moneda" id="miles" value="1" class="form-check-input"/> &nbsp;Miles
								</div>		
							</div>					
						</div>
					
				</div>																										
			</div>								

			<br/>
		
			<h5><strong> I.	NOTAS AL ESTADO DE SITUACIÓN FINANCIERA </strong></h5>
			<hr class="mt-1"/>
			
			<div class="row">												
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<label for="efectivo" class="form-label"> Efectivo y Equivalentes </label>
        		</div>
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<label for="derechos" class="form-label"> Derechos a Recibir Efectivo o Equivalentes y Bienes o Servicios a Recibir </label>							
        		</div>
        	</div>
        	
			<div class="row">												
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<textarea id="efectivo" name="efectivo" rows="2" class="form-control"></textarea>
        		</div>
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<textarea id="derechos" name="derechos" rows="2" class="form-control"></textarea>
        		</div>
        	</div>
        	
        	<div class="row">												
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<label for="derechos_antig" class="form-label"> Derechos a Recibir Efectivo o Equivalentes y Bienes o Servicios a Recibir Explicacion Antiguedad </label>
        		</div>
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<label for="almacenes" class="form-label"> Almacenes </label>
        		</div>
        	</div>
        	
        	<div class="row">												
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<textarea id="derechos_antig" name="derechos_antig" rows="2" class="form-control"></textarea>
        		</div>
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<textarea id="almacenes" name="almacenes" rows="2" class="form-control"></textarea>
        		</div>
        	</div>
        		
        	<br/>	
	    
			<h5><strong> PASIVO </strong></h5>
			<hr class="mt-1"/>
			
			<div class="row">												
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<label for="cxp" class="form-label"> Cuentas y Documentos por Pagar </label>
        		</div>
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<label for="otrascxp" class="form-label"> Otras Cuentas por Pagar a Corto Plazo </label>
        		</div>
        	</div>
        	
        	<div class="row">												
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<textarea id="cxp" name="cxp" rows="2" class="form-control"></textarea>
        		</div>
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<textarea id="otrascxp" name="otrascxp" rows="2" class="form-control"></textarea>
        		</div>
        	</div>
        	
        	<div class="row">												
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<label for="pasivo" class="form-label"> Notas al Estado de Flujo de Efectivo (1) </label>
        		</div>
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<label for="pasivo2" class="form-label"> Notas al Estado de Flujo de Efectivo (2) </label>
        		</div>
        	</div>
        	
        	<div class="row">												
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<textarea id="pasivo" name="pasivo" rows="2" class="form-control"></textarea>
        		</div>
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<textarea id="pasivo2" name="pasivo2" rows="2" class="form-control"></textarea>
        		</div>
        	</div>
        	
        	<div class="row">												
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<label for="eventos" class="form-label"> Eventos Posteriores al Cierre (1) </label>
        		</div>
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<label for="eventos2" class="form-label"> Eventos Posteriores al Cierre (2) </label>
        		</div>
        	</div>
        	
        	<div class="row">												
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<textarea id="eventos" name="eventos" rows="2" class="form-control"></textarea>
        		</div>
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<textarea id="eventos2" name="eventos2" rows="2" class="form-control"></textarea>
        		</div>
        	</div>
        	
        	<div class="row">												
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<label for="eventos3" class="form-label"> Eventos Posteriores al Cierre (3) </label>
        		</div>
        	</div>
        	
        	<div class="row">												
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<textarea id="eventos3" name="eventos3" rows="2" class="form-control"></textarea>
        		</div>
        		<div class="btn-group col-12 col-lg-4 col-md-4 col-sm-12 d-flex" id="filtros" data-toggle="buttons">		 
        			<div class="form-check">
						<input type="radio" name="pdf" id="excel" value="excel" class="form-check-input" checked/>								
						<label for="excel" class="form-check-label">Cuadros en Excel</label>&nbsp;&nbsp;
					</div>
					<div class="form-check">								
						<input type="radio" name="pdf" id="pdf" value="pdf" class="form-check-input"/>
						<label for="pdf" class="form-check-label">Notas en Word</label>
					</div>       										
        		</div>
        	</div>
        	
        	<div class="row">												
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<label for="fechaAut" class="form-label"> Fecha de autorización </label>
        		</div>		        		
        	</div>
        	
        	<div class="row">												
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">		        			
					<textarea id="fechaAut" name="fechaAut" rows="1" class="form-control"></textarea>
        		</div>
        		<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
	        		<div class="row d-flex justify-content-center">
	        			<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">		        			
							<input type="button" class="btn btn-secondary btn-sm" id="excel" name="btn_excel" value="  EXTRAE  " onclick="extrae()">
	        			</div>
	        		</div>
	        	</div>
        	</div>
        	
        	<br/>
		</div>
	</form>
  </body>
</html>
