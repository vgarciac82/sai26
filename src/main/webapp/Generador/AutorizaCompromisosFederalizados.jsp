<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String cUR = "";
	String cIniciaEstatus = "NO";
	String cDevuelveConsolidada = "NO";

	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	String cLogin = "";

	cUR = usuario.getU_UR();
	cLogin = usuario.getLogin();

	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("INICIAESTATUS")) {
		cIniciaEstatus = usuario.getPropiedad("INICIAESTATUS").getValor();
	}

	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("DEVUELVECONSOLIDADA")) {
		cDevuelveConsolidada = usuario.getPropiedad("DEVUELVECONSOLIDADA").getValor();
	}

	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);	
	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	String aEjercicioFiscal = efbl.getEjercicioFiscalActivo().getaEjercicioFiscal();
	
	boolean esRespuesta = request.getParameter("RESPUESTA") != null ? true: false;
	String msg = (String)session.getAttribute("RESULT");
	session.removeAttribute("RESULT");	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Autoriza Compromisos Federalizados</title>

<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
	
<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>	
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>

<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>

	<script type="text/javascript" charset="utf-8">
		var modalFolio;
		$(document).ready(function(){
			
			modalFolio = new bootstrap.Modal(document.getElementById('dialog-CapturaFolio'), 'data-bs-backdrop');
		
			$("#tabs").tabs( {
				"show" : function(event, ui) {
				    		var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
				    		if ( oTable.length > 0 ) {
				    			oTable.fnAdjustColumnSizing();
				    		}
				    		
				    		if( ui.index == 1 ){
				    			muestraBotonesIntegracion();
				    		}
						},
						
			/*VGC20180119 Cambios para generar layout de compromiso cuando la integradora suma mas de 300 UMAS*/			
				
			 "select" : function(event, ui) {
			 			 
				 			 if( ui.index == 1 ){
					 			 return enviar() && validaTotalesGeneraSICOP();
				 			 }
				 			 
			      		}
			} );			
			
			querySelectPost("U_EJECUTORARead", "cIdUnidadEjecutora2", {async: false });
			
			queryFormPost("FechaInicialRead", {async: false });
			queryFormPost("FechaFinalRead", {async: false });	
									
			$("#FechaInicial2").val( $("#FechaInicial").val());
			$("#FechaFinal2").val( $("#FechaFinal").val() );			
			
			$("#FechaInicial2").val(moment().format('yyyy-01-01'));
			$("#FechaFinal2").val(moment().format('yyyy-MM-DD'));
			
		    fnGridCompromisosFederalizados($("#cIdUnidadEjecutora2").val(), $("#FechaInicial2").val().split('-').reverse().join('/'), $("#FechaFinal2").val().split('-').reverse().join('/'));		    
			
			$( "#cIdUnidadEjecutora2" ).change(function(){
				fnGridCompromisosFederalizados($("#cIdUnidadEjecutora2").val(), $("#FechaInicial2").val().split('-').reverse().join('/'), $("#FechaFinal2").val().split('-').reverse().join('/'));
			});
			
			$("#dlgRespuesta").dialog(
			{
				autoOpen : false,
				height : 550,
				width : 500,
				modal : true,
				buttons : {
					"Aceptar" : function() {
						closeBtn();
						$(this).dialog( "close" );						
		            }
				},
				close : function() {
					closeBtn();					
				}
			});
			
			if( <%=esRespuesta%> ){
				$("#dialog-CapturaFolio").hide();
				$("#dlgRespuesta").dialog("open");
			}			
				
		});	  		
 		
		
 		function convertirAFecha(string) 
		{
			 var date = new Date();
			 mes = parseInt(string.substring(3, 5), 10);
			 date.setMonth(mes - 1); //en javascript los meses van de 0 a 11
			 date.setDate(string.substring(0, 2));
			 date.setYear(string.substring(6, 10));
			 return date;
		}
		
		function valFecha(object1) 
		{
			var FechaIni = document.getElementById("FechaInicial").value;
		    var FechaFin = document.getElementById("FechaFinal").value;
			
		    if (object1.value != "") 
			{
		      if (convertirAFecha(FechaIni) > convertirAFecha(FechaFin)) 
		      {
		    	  Swal.fire({ icon: "warning",
							  text: "La fecha incial no puede ser mayor a la fecha final"});
		         document.getElementById("FechaFinal").value = document.getElementById("FechaInicial").value;
		      }		      
		   }
		}		
		
		function valFecha2(object1) 
		{
			var FechaIni = document.getElementById("FechaInicial2").value;
		    var FechaFin = document.getElementById("FechaFinal2").value;
			
		    if (object1.value != "") {
		      if (convertirAFecha(FechaIni) > convertirAFecha(FechaFin)) {		      	
		      	Swal.fire({ icon: "warning",
					  		text: "La fecha incial no puede ser mayor a la fecha final"});
		      	document.getElementById("FechaFinal2").value = document.getElementById("FechaInicial2").value;
		      }
		      
		      fnGridCompromisosFederalizados( $("#cIdUnidadEjecutora2").val(), $("#FechaInicial2").val().split('-').reverse().join('/'), $("#FechaFinal2").val().split('-').reverse().join('/') );
		      
		   }
		}	
		
		function closeBtn(){
			limpiaValores();
			fnGridCompromisosFederalizados( $("#cIdUnidadEjecutora2").val(), $("#FechaInicial2").val().split('-').reverse().join('/'), $("#FechaFinal2").val().split('-').reverse().join('/') );
		}
		
	</script>
	</head>
	<br/>
  	<body id="dt_example" >  	
		<div id="container" class="ms-5" class="container" style="width: 90%">
			<div class="card-header"> <h3> Autoriza Compromisos Federalizados </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row d-flex justify-content-center">
	      							
	       		<ul class="nav nav-tabs" id="list-opciones">
	       			<li class="nav-item" role="presentation">
		            	<button class="nav-link active" id="tabs-1" data-bs-toggle="tab" data-bs-target="#tabs-1-layout" type="button" role="tab" aria-controls="tabs-layout" aria-selected="true">Compromisos Federalizados</button>
		            </li>
	        	</ul>
	        		
	        	<div class="tab-content mt-3" id="tabContent">	
					<div class="tab-pane fade show active" id="tabs-1-layout" role="tabpanel" aria-labelledby="tabs-layout">
						<form  name="compromisoFed" id="compromisoFed" action="../gstnmngr/generaLayoutRelacionGastosCompromiso" method="post">
							<input type="hidden" id="cUResp2" name="cUResp2" class="paso04" value="<%=cUR%>">
							<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" class="paso04" value="<%=cUR%>">
				        	
				        	<div class="row d-flex">								
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="cIdUnidadEjecutora2" class="form-label"> U. Ejecutora: </label>
								</div>
								<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
									<select id="cIdUnidadEjecutora2" name="cIdUnidadEjecutora2" class="form-select form-select-sm">
					            		<option value="<%=cUR%>"></option>
						            </select>
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="FechaInicial2" class="form-label"> Fecha Inicio: </label>
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
									<div class="input-group">
										<span class="input-group date"><i class="datepicker1"></i></span>
										<input onchange="valFecha2(this)" name="FechaInicial2" type="date" id="FechaInicial2" class="form-control form-control-sm" size="10" />
									</div>						            
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="FechaFinal2" class="form-label"> Fecha Final: </label>
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
									<div class="input-group">
										<span class="input-group date"><i class="datepicker1"></i></span>
										<input onchange="valFecha2(this)" name="FechaFinal2" type="date" id="FechaFinal2" class="form-control form-control-sm" size="10" />
									</div>						            
								</div>								
							</div>
							
							<br/>
			          	
							<h5> Layouts de Compromiso Generados </h5>
							<hr class="mt-3"/>
							
						    <jsp:include page="listaCompromisosFederalizados.jsp"></jsp:include>
							
						</form>
					</div>
				</div>
			</div>
			
			<div id="dlgRespuesta">
				<h5> Resultado de Operacion </h5>
				<hr class="mt-3"/>
				
				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<textarea rows="10" cols="50" readonly="readonly" class="form-control form-control-sm"><%=msg%></textarea>
					</div>
				</div>		
					
			</div>
		</div>
	</body>
			
</html>