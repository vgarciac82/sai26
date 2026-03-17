<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);

String msg = "";
if (session.getAttribute("RESULT") != null) { 
	msg = (String) session.getAttribute("RESULT");
	session.removeAttribute("RESULT");
}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
       
    <title>My JSP 'SubirArchivoFTP.jsp' starting page</title>
    
	<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
	<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
	
	<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
	<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
	
	<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>

<script type="text/javascript">
var msg = "<%=msg%>";
var modalRespuesta;
$(document).ready(function () {
	
	document.getElementById("aplicaEjecidoPagado").style.display = "none";	

	modalRespuesta = new bootstrap.Modal(document.getElementById('msgDialog'), 'data-bs-backdrop');
	
	$("#btnEjecutarSicopEnc").click(function (){ subirArchivo("CLC_SICOP","sicopEnc",this.name); });
	$("#btnEjecutarSicopReten").click(function (){ subirArchivo("CLC_SICOP_RETENCION","sicopReten",this.name); });
	$("#btnEjecutarSiaff").click(function (){ subirArchivo("CLC_SIAFF_ENC","siaff",this.name); });
	$("#btnEjecutarEjercidoPagado").click(function () { ejercidoPagadoVarios(); });
	$("#btnArchivo").click(function (){ subirArchivoFTP( $("#archivo").val() ); });
	$("#btnEjecutarSicopPago").click(function (){ subirArchivo("CLC_SICOP_PAGO","sicopPago",this.name); });

	
	$("#btnCargar").click(function(){ 
		archivoSeleccionado();
	});
	
	$("#btnCargarAjena").click(function(){ 
		enviarArchivo();
	});
	
	if (msg != ""){
		document.getElementById("divEsperaProcesando").style.display = "none";	
		modalRespuesta.show();
	}
	
	queryFormPost("readSolicitudesConRechazo",{async: false });
		
});

function carga(){
	document.getElementById("cargaArchivos").style.display = "block";
	document.getElementById("aplicaEjecidoPagado").style.display = "none";	
	
}

function aplica(){
	document.getElementById("cargaArchivos").style.display = "none";
	document.getElementById("aplicaEjecidoPagado").style.display = "block";
}
function subirArchivo(nombreArchivo,mensaje,boton){

		document.getElementById("esperar").style.visibility = 'visible';
		
		if(confirm("Esta seguro que desea cargar Archivo ?")){
		
				$.ajax({
					url: './cierrePresupuestal.jsp',
					type: 'post',
					dataType: 'json',
					//async:    false,  
					data: {tipo:'subirArchivo', nombreArchivo:nombreArchivo },
					success: function(data){
						
						if(data.sinSesion == "sinSesion"){
						
							location.href = "../index.jsp";
							
						}else if(data.estatus == "correcto"){
							
							$("#"+mensaje).html("Cargado Correctamente");
							$("#"+boton).attr("disabled","disabled");
							
						}else{
							
							$("#"+mensaje).html("No Se Cargo Correctamente, Verifique Nombre Correcto del Archivo");
								
						}
						document.getElementById("esperar").style.visibility = 'hidden';
					}
				});
		}else{
			document.getElementById("esperar").style.visibility = 'hidden';
		}		
}

function subirArchivoFTP(nombreArchivo){

		if(confirm("Esta seguro que desea subir Archivo ?")){
		
				$.ajax({
					url: './cierrePresupuestal.jsp',
					type: 'post',
					data: {tipo:'subirArchivoFTP', nombreArchivo:nombreArchivo },
					success: function(data){
					
						if(data.estatus == "correcto"){
							Swal.fire({ icon: "success",
										text: "Aplicado Correctamente"});														
						}else{
							Swal.fire({ icon: "error",
										text: "No Se Cargo Correctamente"});														
						}
					}
				});
		}else{
			document.getElementById("esperar").style.visibility = 'hidden';
			Swal.fire({ icon: "warning",
						text: "retrun"});									
		}		
}

function ejercidoPagadoVarios(){
		
		document.getElementById("esperar").style.visibility = 'visible';
		$("#btnEjecutarEjercidoPagado").attr("disabled","disabled");
		
		if(confirm("Esta Seguro De Aplicar Ejercido y Pagado")){
			
			$.ajax({
				url: './ejercidoPagadoValidar.jsp',
				dataType: 'json',
				type: 'post',
				data: {tipo:'ejercidoPagadoVarios',cuentaPorPagar:$("#cuentaPorPagar").val()},
				success: function(data){
					
					if(data.status == "guardado"){
						Swal.fire({ icon: "success",
									text: "Aplicado Correctamente"});						
					}else if(data.status == "errorDetalles"){
						Swal.fire({ icon: "success",
									text: "Aplicado Correctamente y se Guardo Detalles"});						
						
					}else{
						Swal.fire({ icon: "error",
									text: "No Se Termino el Proceso "+ data.status});						
					}
					
					document.getElementById("esperar").style.visibility = 'hidden';
					document.getElementById("btnEjecutarEjercidoPagado").disabled = false;

				}
			});
		}else{
			document.getElementById("esperar").style.visibility = 'hidden';
			document.getElementById("btnEjecutarEjercidoPagado").disabled = false;
			return;
		}
}
function archivoSeleccionado(){
	var fichero = "";
	var correcto = false;
	var nombre = "";
	var tipo = $("input[name='cTipoArchivo']:checked").val();
	
	if($("#archivoCargar").val() != ""){
		fichero = $("#archivoCargar").val();
		fichero = fichero.split('\\');
  		nombre = fichero[fichero.length-1];
  		
  		if (tipo=="SIC"){
  		
  			if( nombre.toUpperCase() == "CLC_SICOP.CSV" || nombre.toUpperCase() == "CLC_SICOP.ZIP" )
				correcto = true;

  		}else if (tipo=="RET"){
  		
  			if (nombre.toUpperCase() == "CLC_SICOP_RETENCION.CSV" || nombre.toUpperCase() == "CLC_SICOP_RETENCION.ZIP" )
				correcto = true;
				
  		}else if (tipo=="ENC"){
  			if( nombre.toUpperCase() == "CLC_SIAFF_ENC.CSV" || nombre.toUpperCase() == "CLC_SIAFF_ENC.ZIP" )
				correcto = true;
  		}else if (tipo=="PAG"){
  			if( nombre.toUpperCase() == "CLC_SICOP_PAGO.CSV" || nombre.toUpperCase() == "CLC_SICOP_PAGO.ZIP")
				correcto = true;
  		}
  		
  		if (!correcto){
  			Swal.fire({ icon: "error",
						text: "Seleccione el archivo correcto."});  			
			return false;
  		}
  		$("#btnCargar").attr("disabled",true);
  		
  		$("#divEsperaProcesando").attr("style","visibility=visible");
  		$("#FormUpload").submit();
	}else{
		Swal.fire({ icon: "error",
					text: "Para continuar debe seleccionar un archivo a cargar."});		
		return false;
	}
	return true;
}

function enviarArchivo(){
	
	if($("#archivoCargarOA").val() != ""){
  		
  		$("#btnCargarAjena").attr("disabled",true);
  		$("#divEsperaProcesando").attr("style","visibility=visible");
  		$("#FormAjenas").submit();
  		
	}else{
		Swal.fire({ icon: "error",
					text: "Para continuar debe seleccionar un archivo a cargar."});		
		return false;
	}
	return true;
}

</script>	
</head>
<body id="dt_example">
<br/>
<form id="FormUpload" name="FormUpload" method="POST" action="../CargaArchivosSicopServlet" enctype="multipart/form-data" >
	  	<div id="container" class="container" style="width: 60%">
	  		<input type="hidden" id="usuario" name="usuario" value="<%=usuario.getLogin()%>">
	  		
	  		<div class="card-header"> <h3> Cargar Archivos Ejercido/Pagado </h3> </div>
	  		<div class="mt-4 row d-flex justify-content-center">		
		  		<ul class="nav nav-tabs" id="list-opciones">
		            <li class="nav-item" role="presentation">
		            	<button class="nav-link active" id="tabs-1" onClick="carga();" data-bs-toggle="tab" data-bs-target="#tabs-1-carga" type="button" role="tab" aria-controls="tabs-carga" aria-selected="true">Cargar Archivos</button>
		            </li>
		            <li class="nav-item" role="presentation">
		            	<button class="nav-link" id="tabs-2" onClick="aplica();" data-bs-toggle="tab" data-bs-target="#tabs-2-aplica" type="button" role="tab" aria-controls="tabs-aplica" aria-selected="false">Aplicar Ejercido / Pagado</button>
		            </li>
		            <li class="nav-item" role="presentation">
		            	<button class="nav-link" id="tabs-3" onClick="" data-bs-toggle="tab" data-bs-target="#tabs-3-OA" type="button" role="tab" aria-controls="tabs-oa" aria-selected="false">Cargar Archivos Retenciones</button>
		            </li>				           				           
		    	</ul>		
		    	
		    	<div align="center">
					<label id="esperar" style="visibility: hidden">	Espere por favor....
						<img border="0" src="../imagenes/espera.gif" height="30">
					</label>
				</div>
				
		    	<div class="tab-content mt-3" id="tabContent">		
					<div class="tab-pane fade show active" id="tabs-1-carga" role="tabpanel" aria-labelledby="tabs-carga">
						<div class="container" id="cargaArchivos">
							<div class="row d-flex">
								<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">								
									<label for="btnEjecutarSicopEnc" class="form-check-label"> Cargar Archivos CLC_SICOP </label>
								</div>
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
									<input type="radio" name="cTipoArchivo" id="tipoArchivo" class="form-check-input" value="SIC" checked/>&nbsp;&nbsp;		
									<input type="button" name="btnEjecutarSicopEnc" id="btnEjecutarSicopEnc" value="Ejecutar" class="btn btn-secondarybtn btn-secondary" />
									<div id="sicopEnc"  align="center"></div>								
								</div>
							</div>
							
							<div class="row d-flex">
								<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">								
									<label for="btnEjecutarSicopReten" class="form-check-label"> Cargar Archivos CLC_SICOP_RETENCION </label>
								</div>
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
									<input type="radio" name="cTipoArchivo" id="tipoArchivo" class="form-check-input" value="RET"/>&nbsp;&nbsp;		
									<input type="button" name="btnEjecutarSicopReten" id="btnEjecutarSicopReten" value="Ejecutar" class="btn btn-secondarybtn btn-secondary" />
									<div id="sicopReten"  align="center"></div>								
								</div>
							</div>
							
							<div class="row d-flex">
								<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">								
									<label for="btnEjecutarSiaff" class="form-check-label"> Cargar Archivos CLC_SIAFF_ENC </label>
								</div>
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
									<input type="radio" name="cTipoArchivo" id="tipoArchivo" class="form-check-input" value="ENC"/>&nbsp;&nbsp;		
									<input type="button" name="btnEjecutarSiaff" id="btnEjecutarSiaff" value="Ejecutar" class="btn btn-secondarybtn btn-secondary" />
									<div id="siaff"  align="center"></div>								
								</div>
							</div>
							
							<div class="row d-flex">
								<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">								
									<label for="btnEjecutarSicopPago" class="form-check-label"> Cargar Archivos CLC_SICOP_PAGO </label>
								</div>
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
									<input type="radio" name="cTipoArchivo" id="tipoArchivo" class="form-check-input" value="PAG"/>&nbsp;&nbsp;		
									<input type="button" name="btnEjecutarSicopPago" id="btnEjecutarSicopPago" value="Ejecutar" class="btn btn-secondarybtn btn-secondary" />
									<div id="sicopPago"  align="center"></div>								
								</div>
							</div>
							
							<br/>
							<div class="row">
								<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">
									<input type="file" id="archivoCargar" name="archivoCargar" class="form-control form-control-sm" />
								</div>
								
							</div>
							<div class="row">
								<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1"></div>
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-2">
									<input type="button" id="btnCargar" name="btnCargar" value="Cargar Archivo" class="btn btn-dark"/>
								</div>
							</div>
							
								
						</div>	
					</div>			
						
					<div class="tab-pane fade show active" id="tabs-2-aplica" role="tabpanel" aria-labelledby="tabs-aplica">
						<div class="container" id="aplicaEjecidoPagado">
						
							<div class="row d-flex">
								<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
									<label for="btnEjecutarEjercidoPagado" class="form-check-label"> Aplicar Ejercido / Pagado </label>
									<input type="button" id="btnEjecutarEjercidoPagado" name="btnEjecutarEjercidoPagado" value="Ejecutar" class="btn btn-secondary"/>
								</div>
							</div>										
							<div class="row d-flex">
								<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
									<span class="label">Captura las CxP que no se pagaran por Rechazo Bancario.</span>
									<span class="label">No Incluir CXP Ejemplo ('1CP','2CP')</span>
								</div>
							</div>
							<div class="row d-flex">
								<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
									<input class="form-control form-control-sm" type="text" name="cuentaPorPagar" id="cuentaPorPagar" size="35" value=""/>
								</div>
							</div>
								
						</div>			
					</div>
			</form>		
					<div class="tab-pane fade" id="tabs-3-OA" role="tabpanel" aria-labelledby="tabs-oa">
						<form id="FormAjenas" name="FormAjenas" method="post" action="../CargaArchivosSicopAjenasServlet" enctype="multipart/form-data" >
							<div class="container">
								<div class="row">
									<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">
										<label for="archivoCargarOA" class="form-label"> Extracción para generación de layouts de Operaciones Ajenas (.csv) </label>
										<input type="file" id="archivoCargarOA" name="archivoCargarOA" class="form-control" />
									</div>
								</div>
								<div class="row">
									<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1"></div>
									<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-2">
										<input type="button" id="btnCargarAjena" name="btnCargarAjena" value="Cargar" class="btn btn-dark"/>
									</div>
								</div>
							</div>	
						</form>		
					</div>
				</div>									
		</div>	    
	 </div>
	 
	<div class="modal" tabindex="-1" role="dialog" id="msgDialog" data-mdb-keyboard="true" data-mdb-backdrop="static">
	  	<div class="modal-dialog" role="document">
	    	<div class="modal-content">
		      	<div class="modal-header">
		      	<h5 class="modal-title">Resultado de Carga</h5>
		        	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		    	</div>
		    	<div class="modal-body">
			      	<div class="row">
			      		<div class="col-12">
			      			<textarea rows="10" cols="40" id="msgTxt" class="form-control form-control-sm"><%=msg.replaceAll("<br>", "\n*")%></textarea>
			      		</div>
			      	</div>
			    </div>
			    <div class="modal-footer">
		        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
		      	</div>
	    	</div>
	  	</div>
	</div>	
	
	<div id="divEsperaProcesando" style="visibility: hidden" align="center">
		Espere por favor...
		<img border="0" src="../imagenes/espera.gif" height="30">
	</div>

</body>
</html>
