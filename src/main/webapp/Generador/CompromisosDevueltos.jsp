<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%
	String mensaje = (String) session.getAttribute("msg");
	boolean msgResult = false;
	if (mensaje != null) {
		session.removeAttribute("msg");
		msgResult = true;
	} else
		mensaje = "";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Carga de layout compromiso</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>

<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>	
<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script src="https://cdn.jsdelivr.net/npm/block-ui@2.70.0/jquery.blockUI.min.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>

<script type="text/javascript">
	var modalRespuesta;
	var msgResult = <%=msgResult%>;

	var es_mx = {
			sProcessing : "Procesando...",
			sLengthMenu : "Mostrar _MENU_ registros",
			sZeroRecords : "No hay registros a mostrar",
			sEmptyTable : "No hay datos en la tabla",
			sLoadingRecords : "Cargando...",
			sInfo : "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty : "Registro 0 al 0 de 0",
			sInfoFiltered : "(filtered from _MAX_ total entries)",
			sInfoPostFix : "",
			sInfoThousands : ",",
			sSearch : "Filtro:",
			oPaginate : {
				sFirst : "Primero",
				sPrevious : "Ant.",
				sNext : "Sigte.",
				sLast : "&Uacute;ltimo"
			}	
		
		};
	
	$(document).ready(function() {
		
		$("#sndBtn").button().click(function() {

			validaEnvio();
			
		});

		$("#downloadBtn").button().click(function() {

			descargaFormatoAut();
			
		});
		
		
		modalRespuesta = new bootstrap.Modal(document.getElementById('dlgRespuesta'), 'data-bs-backdrop');
		modalRespuesta.hide();
		creaTableNoAplicados();
		
		if( msgResult )
			modalRespuesta.show();
			
	});

	function validaEnvio() {
		
		$.blockUI({message: "Procesando espere ......"});
		$("#formCompromiso").submit();
					
	}
	
	function descargaFormatoAut() {
		window.open('../compromisos/excel', '_blank'); 
	}
	
	
	function LimitAttach(tField,iType){
				var file=tField.value;
				var extArray = new Array(".csv"); 
				var allowSubmit = false; 
				
				if (!file){ 
					return; 
				}
			
				while (file.indexOf("\\") != -1){ 
					file = file.slice(file.indexOf("\\") + 1); 
				}
				var ext = file.slice(file.indexOf(".")).toLowerCase(); 
				for (var i = 0; i < extArray.length; i++) {
					if (extArray[i] == ext) {
						allowSubmit = true;
						break;
					}
				}

				if (!allowSubmit) {
					tField.value="";
					limpiarSesion();
					alert("Sólo puede subir archivos con extensiones " + (extArray.join(" ")) + "\nPor favor seleccione un nuevo archivo");
					
				}
			}
			
	function limpiarSesion()
				{
					window.location.href="CompromisosDevueltos.jsp";
				}
	

	function creaTableNoAplicados(){
		oTableAgenda = $('#tblCompromisos').dataTable(
			{
				"bPaginate" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				//"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=vLayoutCompromisos" ,
				aoColumns : [ {
					sName : "canocompromiso"
				}, {
					sName : "cIdContrato"
				}, {
					sName : "noCompromiso"
				}, {
					sName : "fAplicacionSICOP"
				},  {
					sName : "mimporteSicop"
				}, {
					sName : "importe"
				}, {
					sName : "diferencia",
				}],
				oLanguage : es_mx
			});	
	}
</script>

</head>

<body id="dt_example">
<br/>
<form action="../gstnmngr/RespuestaCompromisosSICOP" method="post" id="formCompromiso" enctype="multipart/form-data">		
	<div id="container" class="container">
		<div class="card-header"> <h3> Recibe Layout de compromiso SICOP </h3> </div>			
			<div class="mt-4 row d-flex justify-content-center ">
					
					<div class="col-2">
						<div class="form-check form-check-inline">
						  <input class="form-check-input" type="radio" name="tipoLayout" id="radioCompromiso" value="COMPROMISO" checked>
						  <label class="form-check-label" for="radioCompromiso">Compromiso</label>
						</div>
					</div>
					<div class="col-2">	
						<div class="form-check form-check-inline">
						  <input class="form-check-input" type="radio" name="tipoLayout" id="radioSuficiencia" value="SUFICIENCIA">
						  <label class="form-check-label" for="radioSuficiencia">Suficiencia</label>
						</div>
					</div>
					<div class="col-2">
						<div class="form-check form-check-inline">
						  <input class="form-check-input" type="radio" name="tipoLayout" id="radioCompromiso" value="APLICACOMPROMISO" checked>
						  <label class="form-check-label" for="radioCompromiso">Analisis Compromisos</label>
						</div>
					</div>
			</div>
			<div class="mt-2 d-flex justify-content-center">
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
					<label>Cargar Archivo CSV</label>
					<input type="file" id="layoutCompromiso" name="layoutCompromiso" class="form-control form-control-sm" style="width: 30em;" onblur = "LimitAttach(this, - 1);"/>
				</div>
				
			</div>
			
			<div class="mt-4 d-flex justify-content-center">
			    <input type="button" id="sndBtn" name="sndBtn" value="Enviar" class="btn btn-secondary me-1"/>
			    <input type="button" id="downloadBtn" name="downloadBtn" value="Descargar Formato SEMARNAT" class="btn btn-primary"/>
			</div>

			<div class="mt-4 row d-flex justify-content-center">
				<div class="col-12 col-lg-12 col-md-12 col-sm-12">
					<h5>Listado de compromisos no aplicados por diferencia con SICOP</h5>
				</div>
			</div>
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-12 col-md-12 col-sm-12">
					<div id="divTblCompromisos" class="table-responsive text-nowrap">
						<table id="tblCompromisos" class="table table-striped table-bordered">
							<thead>
							    <tr>
							      <th>Contrarrecibo</th>
							      <th>Contrato</th>
							      <th>No.Comp</th>
							      <th>Fecha</th>
							      <th>Importe SICOP</th>
							      <th>Importe SAI</th>
							      <th>Diferencia</th>
							    </tr>
							  </thead>
						</table>
					</div>
				</div>
			</div>
		</div>
</form>	

<div class="modal" tabindex="-1" role="dialog" id="dlgRespuesta" data-mdb-keyboard="true" data-mdb-backdrop="static">
	  <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
	    <div class="modal-content">
		    <div class="modal-header">
		        <h5 class="modal-title">Respuesta</h5>
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		    </div>
	      	<div class="modal-body">
	      		<textarea rows="5" cols="45" class="form-control form-control-sm"><%=mensaje%></textarea>	
	       	</div>
		    <div class="modal-footer">
	        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
	      	</div>
	    </div>
	  </div>
</div>
		
	
		
</body>

</html>