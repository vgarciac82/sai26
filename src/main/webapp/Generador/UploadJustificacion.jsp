<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	String msg = "";
	if (session.getAttribute("RESULT") != null) {
		msg = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
	
	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	String efa = efbl.getEjercicioFiscalActivo().getaEjercicioFiscal();
	
	String folio = StringUtils.trimToEmpty( request.getParameter("folioPago") );
	
	if (folio.isEmpty())
		folio = "0";
	
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Validar Facturas</title>
	
		<!-- Estilos estandar para los controles JQuery -->
		<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
		<link rel="stylesheet" type="text/css"	href="css/jquery-ui.min.css"></link>
		<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>
		<link rel="stylesheet" type="text/css"	href="css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
		
		<script type="text/javascript" src="js/jquery-3.5.1.min.js"></script>
		<script type="text/javascript" src="js/bootstrap.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
		
		<script type="text/javascript">
				var msg = "<%=msg%>";
				var efa = <%=efa%>;
				var folio =<%=folio%>; 
				
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
				
				$(document).ready(
					function(){
						creaTableFacturasFF();
						 if( msg != ""){
								$("#msgDialog").show();
								$("#uploadDiv").hide();
							}else{
								$("#msgDialog").hide();
								$("#uploadDiv").show();
							}
						
					});
				
				function creaTableFacturasFF(){
					sWhere ="nFolioPago =" + folio;
					oTableAutorizaciones = $('#facturasFF').dataTable(
						{
							"bPaginate" : false,
							"bLengthChange" : true,
							"bFilter" : false,
							"bSort" : true,
							"bInfo" : false,
							"bJQueryUI" : true,
							"bRetrive" : true,
							"bDestroy" : true,
							"bServerSide" : true,
							sAjaxSource : window.location.protocol + "//"
									+ window.location.host + "/"
									+ window.location.pathname.split("/")[1]
									+ "/crud?rt=t&ql=v_facturasFueraFecha&qw=" + sWhere ,
							aoColumns : [ {
								sName : "cFactura"
							}, {
								sName : "cRFCFactura"
							}, {
								sName : "dFechaFactura"
							}, {
								sName : "mimporteconIva"
							}],
							oLanguage : es_mx
						});					
				}
				
				function guardaJustif(){
						var logErrores = "";
						var guardado = false;
						
						$("#folio").val(folio);
						
						if( $("#fileJustificacion").val() === ""  && $("#msjJustificacion").val() === "" )  {
							Swal.fire({
						        icon: 'warning',
						        title: 'Datos Requeridos',
						        text: 'Debe seleccionar el archivo PDF para adjuntar o capturar una justificación.',
						        confirmButtonText: 'Aceptar'
						    });
						    return;
						}
						
						if( $("#fileJustificacion").val() != "" )  {
							    const progressBar = $('#upload-progress');
							    const form = $('#formJustif')[0];
							    const data = new FormData(form);
		
							    $.ajax({
							        url: window.location.protocol + "//"
										+ window.location.host + "/"
										+ window.location.pathname.split("/")[1]
										+ "/viaticos/adjuntaJustificacion",
							        type: 'POST',
							        enctype: 'multipart/form-data',
							        data: data,
							        beforeSend: function() {
							            $.blockUI({ message: 'Procesando...' });
							        },
							        processData: false,
							        contentType: false,
							        xhr: function() {
							            const xhr = new XMLHttpRequest();
							            xhr.upload.addEventListener('progress', function(event) {
							                if (event.lengthComputable) {
							                    const percentComplete = event.loaded / event.total * 100;
							                    progressBar.css('width', percentComplete + '%');
							                    progressBar.text(percentComplete + '%');
							                }
							            }, false);
							            return xhr;
							        },
							        success: function(j) {
							            $.unblockUI();
							            var exito = j.success;
										
										if (exito) {
											$("#msgDialog").show();
											$("#uploadDiv").hide();
											$("#msgTxt").val("El archivo se cargó correctamente.");
											parent.avanzarJust();
											guardado = true;
											
										} else {
											var errores = j.errorList;
											var cnt = 0;
											for (cnt = 0; cnt < errores.length; cnt++) {
												logErrores = logErrores + errores[cnt] + "\n";
											}
											
											$("#msgTxt").val(logErrores);
									    	
										}
							        },
							        error: function(error) {
							            console.log(error);
							            $.unblockUI();
							            Swal.fire({
							                icon: 'error',
							                title: 'Error',
							                text: 'Ocurrio un error al cargar el archivo.  \n Intente nuevamente, si el problema persiste notifique al adminitrador.',
							                confirmButtonText: 'Aceptar'
							            });
							            progressBar.css('width', '0%');
							            progressBar.text( '0%');
							        }
							    });
						
						}  else if ( $("#msjJustificacion").val()  != ""){

							$.ajax({
								url : "../viaticos/actualizarJustTickets",
								type : 'post',
								data :{ msjJustificacion : $("#msjJustificacion").val(),
										folio : $("#folio").val()
								},
								dataType : 'json',
								success : function(j) {
									var exito = j.success;
						
									if (exito) {
										guardado = true;
										parent.avanzarJust();
										parent.cerrarJustificacion();
										
									} else {
										var errores = j.errorList;
										var cnt = 0;
										for (cnt = 0; cnt < errores.length; cnt++) {
											logErrores = logErrores + errores[cnt] + "\n";
										}
									}
								},
								error : function(xhr, status, error) {
									logErrores =  errorThrown.ERROR;
								}
							});
						
							if (!guardado)
								Swal.fire("Error actualizando la justificacion", logErrores, "error")	
						}
					}

				
			</script>
	</head>
	
	<body id="dt_example">
		<div id="container">
			<form id = "formJustif" method="post" enctype="multipart/form-data" action="../viaticos/adjuntaJustificacion">
				<input type="hidden" name="folio" id="folio" />
				
				<div id="msgDialog" title="Resultado de Carga">
					<div class="row">
		      			<div class="col-12">
							<textarea class="form-control" rows="3" id="msgTxt"></textarea>
						</div>	
					</div>
					<div class="row mt-2">
		      			<div class="col-12">	
							<input type="button" id="btnAceptar" value="Aceptar" class="btn btn-primary" onclick="parent.cerrarJustificacion();"/>
						</div>	
					</div>	
				</div>
				<div id="uploadDiv">
					<div class="row">
		      			<div class="col-12">
		      				<p>En este pago se estan tramitando facturas emitidas en un periodo fuera de la comision, por lo que necesita adjuntar una justificación para que sea autorizada.</p>
		      			</div>
		      		</div>
		      		<div class="row mt-2">
						<div class="col-12">
							<p class="h6">Facturas fuera de fecha de la comisión</p>
							<div class="table-responsive text-nowrap">
								<table id="facturasFF"  class="table table-striped table-bordered">
									<thead>
									    <tr>
									      <th>UUID</th>
									      <th>RFC Factura</th>
									      <th>Fecha Factura</th>
									      <th>Importe</th>
									    </tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
		      		<div class="row mt-2">
		      			<div class="col-12">
		      				<p>Si tiene los tickets de pago con las fechas dentro de la comisión adjuntelos a continuación. (Archivo pdf o zip)</p>
		      			</div>
		      		</div>
			      	<div class="row">
						<div class="col-12">
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-bookmark"></i></span>
								<input type="file" value="" id="fileJustificacion" name="fileJustificacion" class="form-control"/>
							</div>
						</div>
					</div>
					
					<div class="row">
		      			<div class="col-12 mt-3">
		      				<p>En caso de no contar con los tickets de compra, justifique:</p>
		      				<p>La información capturada será firmada posteriormente</p>
		      			</div>
		      		</div>
		      		<div class="row">
		      			<div class="col-12">
							<textarea class="form-control" rows="3" id="msjJustificacion" maxlength="500"></textarea>
						</div>	
					</div>
		    		<div class="col-12 mt-4">
							<div class="progress">
							  <div class="progress-bar" id="upload-progress" role="progressbar" aria-label="Progreso de Carga" style="width: 0%;" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">0%</div>
							</div>
					</div>
					<div class="row mt-4">
		      			<div class="col-12">
		        			<button type="button" id="btnJustificacion" onclick="guardaJustif();" class="btn btn-primary">Guardar</button>
		    			</div>
		    		</div>
				</div>
				
			</form>
		
		</div>
	</body>
</html>