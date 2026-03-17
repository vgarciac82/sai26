<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		//response.sendRedirect("../index.jsp");
		//return;
	}
	String ur = usuario.getU_UR();
	String usuLogin = usuario.getLogin();
	String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
       
    <title>Cancela Ejercido Pagado</title>
    
    <link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
	<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
	<link rel="stylesheet" href="css/bootstrap.min.css">
	<script src="js/bootstrap.bundle.min.js"></script>
	
	<style type="text/css" title="currentStyle">
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "css/demo_table_jui.css";
		@import "css/demo_page.css";		
	</style>
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>
	
<script type="text/javascript">
let modalMotivo;
$(document).ready(function(){
	
	$("#btnProcesar").button();
	$("#btnProcesar").click(function () { procesar(); });		

	pParam = "<%=ur%>"; 
	
	modalMotivo= new bootstrap.Modal(document.getElementById('dialog-Motivo'), 'data-bs-backdrop');
			
	$("#caNOcontrarrecibo").change(function () {
		$("#nFolioAdefa").val("");

		queryFormPost("BuscaPagadoRead",{async: false });
		queryFormPost("BuscaEsIPRead",{async: false });
		
		if ( $("#cTipoPago").val() == 'RELACIONGASTOS' && $("#cEsIP").val() == 'N' ){
			Swal.fire({ icon: "warning",
						text: "No se pueden cancelar relaciones de Gastos de Recursos Fiscales."})			
			$("#cTipoPago").val("");
			$("#caNOcontrarrecibo").val("");
			return;
		}
		
		if ( $("#cEstatusPagado").val() == 'S' ){
			Swal.fire({ icon: "warning",
						text: "Debe Cancelar primero la póliza del pagado."})			
			return;
		}else{
			if ( $("#tipoDocumento").val() == 'Ejercido'){
				queryFormPost("BuscaEjercidoRead",{async: false });				
			}
			
			if ( $("#nFolioAdefa").val() == "" ){
				Swal.fire({ icon: "warning",
							text: "Contrarrecibo no válido."})				
			}
		}
	});
});

function procesar(){			
	$("#esperar").attr("style","visibility=visible");
	$("#btnProcesar").hide();	
	
	modalMotivo.show();
}

function aceptar(){
	
	var nFolioPagado = $("#nFolioAdefa").val();
	var cTipoDocto = $("#tipoDocumento").val();
	$.ajax({
		url: './cierrePresupuestal.jsp',
		type: 'post',
		dataType: 'json',
		data: {tipo:'cancelaEjercidoPagado', nFolioAdefa:nFolioPagado, tipoDocumento:cTipoDocto},
		success: function(data){
				if(data.sinSesion == "sinSesion"){
					location.href = "../index.jsp";
				}else if(data.estatus == "guardado"){
					if ( $("#cTipoPago").val() == 'RELACIONGASTOS' && $("#cEsIP").val() == 'S' && $("#tipoDocumento").val() == "Ejercido" ){
						queryFormPost("ActualizaEnvioSICOP",{async: false });
					}
					Swal.fire({ icon: "success",
								text: "Cancelado Correctamente."})											
				}else{
					Swal.fire({ icon: "warning",
								text: "No Se Cancelo Correctamente."})											
				}
				$("#esperar").css("visibility", "hidden");
				$("#btnProcesar").show();
		}
	});
	
	modalMotivo.hide();
}

function cancelar() {
	modalMotivo.hide();
	
	$("#esperar").css("visibility", "hidden");
	$("#btnProcesar").show();
}

</script>
  </head>
  <body id="dt_example">
  <br/>
		<form id="formAdefas" name="formAdefas">
			<input type="hidden" id="nFolioAdefaTabla" name="nFolioAdefaTabla" />
			<input type="hidden" id="Name" name="Name" />
			<input type="hidden" id="cEstatusPagado" name="cEstatusPagado" />
			<input type="hidden" id="cTipoPago" name="cTipoPago" />
			<input type="hidden" id="cEsIP" name="cEsIP" />
			
  			<div id="container" class="container">		
  			
				<div class="card-header"> <h4> Cancela Ejercido Pagado </h4> </div>
				<hr class="mt-3"/>
 					
 					<div class="form-group">
					<div class="row">
						<div class="input-group">
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">	
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">																
								<select id="tipoDocumento" name="tipoDocumento" class="form-select form-select-sm">
									<option>Ejercido</option>
									<option>Pagado</option>
								</select>																										
							</div>	
							<div class="col-1">
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12">																																	
								<input type="text" id="caNOcontrarrecibo" name="caNOcontrarrecibo" class="form-control form-control-sm">
								<input type="hidden" id="nFolioAdefa" name="nFolioAdefa">
							</div>	
							<div class="col-1">
							</div>	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<input type="button" id="btnProcesar" name="btnProcesar" value="Procesar" class="btn btn-secondary btn-sm" onclick="procesar();"/>									
							</div>																					
						</div>
					</div>
				</div>
				
				<div class="row">				
					<div class="col-12 col-lg-12 col-md-12 col-sm-12">	
						<label id="esperar" style="visibility: hidden">
							<div align="center">Espere por favor....
							  <img border="0" src="../imagenes/espera.gif" height="30">
							</div>
						</label>
		  			</div>
		  		</div>	
		  		
  			</div>
			 <div class="modal" tabindex="-1" role="dialog" id="dialog-Motivo" data-mdb-keyboard="true" data-mdb-backdrop="static">
			  <div class="modal-dialog modal-lg" role="document">
			     <div class="modal-content">
				      <div class="modal-body">
				      	<div class="row">
				      		<div class="col-12">
				      			<h6>¿ Esta Seguro Que Desea Cancelar El Documento ?</h6>
				      		</div>
					  	</div>
					  </div>
					  <div class="modal-footer">
					        <button type="button" id="btnAceptar" onclick="aceptar();" class="btn btn-primary">Aceptar</button>
					        <button type="button" class="btn btn-secondary" onclick="cancelar();"  data-bs-dismiss="modal">Cerrar</button>
				      </div>
				 </div>
			   </div>
			</div>
		</form>
  </body>
</html>
