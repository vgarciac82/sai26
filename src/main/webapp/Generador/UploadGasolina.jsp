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
		<title>Justificación Gasolina</title>
	
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
						
						 if( msg != ""){
								$("#msgDialog").show();
								$("#uploadDiv").hide();
							}else{
								$("#msgDialog").hide();
								$("#uploadDiv").show();
							}
						
					});
				
				
				
				function guardaJustif(){
						var logErrores;						
						$("#folio").val(folio);
						
						validaDatos();
						
						if ( $("#msjJustificacionGasolina").val()  != ""){

							$.ajax({
								url : "../viaticos/actualizarJustGasolina",
								type : 'post',
								async : false,
								data :$("#formJustif").serialize(),
								dataType : 'json',
								success : function(j) {
									var exito = j.success;
									if (exito) {
										guardado = true;
										parent.abrirBoletosCuandoTieneGasolina();
										parent.cerrarGasolina();
									} else {
										var errores = j.errorList;
										var cnt = 0;
										for (cnt = 0; cnt < errores.length; cnt++) {
											logErrores = logErrores + errores[cnt] + "\n";
										}
									}
								},
								error : function(errorThrown) {
									logErrores =  errorThrown.ERROR;
								}
							});
						
							if (!guardado)
								Swal.fire("Error actualizando la justificacion", logErrores, "error")	
						}
					}

				function validaKM() {
					if (Number($("#kmFin").val() )  > 0 ) {
						if(  Number($("#kmIni").val())  >=  Number($("#kmFin").val() )  )  {
							Swal.fire("Verifique!", "El Km Inicial no debe ser mayor o igual al Km Final.","warning");
							$("#kmFin").val("0");
						    return;
						}
					}
				}
				
				function validaDatos() {
					validaKmLt();
					
					if( $("#msjJustificacionGasolina").val() === "" )  {
						Swal.fire("Capture Justificación", "Debe capturarse la justificación del gasto de Gasolina local para continuar.","warning");
					    return;
					}
					
					if(  Number($("#rendimiento").val()) == 0 || $("#rendimiento").val()  == ""  )  {
						Swal.fire("Rendimiento", "Favor de dar clic en calcular rendimiento, no puede ser cero.","warning");
					    return;
					}
				}
				
				function validaKmLt() {
					if( Number($("#kmIni").val() ) == 0 || $("#kmIni").val()  == "" )  {
						Swal.fire("Capture el Km Inicial", "El Kilometraje inicial no puede ser cero.","warning");
					    return;
					}
					if(  Number($("#kmFin").val()) == 0 || $("#kmFin").val()  == "" )  {
						Swal.fire("Capture Justificación", "El Kilometraje final no puede ser cero.","warning");
					    return;
					}
					if(  Number($("#litros").val()) == 0 || $("#litros").val()  == ""  )  {
						Swal.fire("Capture Justificación", "Los litros de gasolina no pueden se cero.","warning");
					    return;
					}
				}
				
				function calculaRendimiento() {
					validaKmLt();
					 
					var rend = ( Number($("#kmFin").val()) -  Number($("#kmIni").val()) ) / Number($("#litros").val())
					$("#rendimiento").val(rend);
				}
			</script>
	</head>
	
	<body id="dt_example">
		<div id="container">
			<form id = "formJustif">
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
		      			<div class="col-12 mt-3">
		      				<p>Capture la siguiente información para justificar el gasto de Gasolina en Viáticos:</p>
		      			</div>
		      		</div>
		      		<div class="row">
		      			<div class="col-3">
		      				Kilometraje Inicial
		      				<div class="input-group">
		      					<span class="input-group-text"><i class="bi bi-ev-front"></i></span>
								<input type="number" class="form-control" id="kmIni" name="kmIni" onChange="validaKM();" value="0"/>
							</div>
						</div>
						<div class="col-3">
							Kilometraje Final
		      				<div class="input-group">
		      					<span class="input-group-text"><i class="bi bi-ev-front-fill"></i></span>
								<input type="number" class="form-control" id="kmFin" name="kmFin" onChange="validaKM();"  value="0"/>
							</div>
						</div>
						<div class="col-3">
							Litros
		      				<div class="input-group">
		      					<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
								<input type="number" class="form-control" id="litros" name="litros" onChange="calculaRendimiento();" value="0"/>
							</div>
						</div>
						<div class="col-3">
							Rendimiento
		      				<div class="input-group">
		      					<span class="input-group-text"><i class="bi bi-speedometer"></i></span>
								<input type="number" class="form-control" id="rendimiento" name="rendimiento" value="0" readonly/>
								
							</div>
						</div>	
					</div>
					<div class="row">
						<div class="col-9"></div>
						<div class="col-3">
		      				<button type="button" id="btnCalculo" onclick="calculaRendimiento();" class="btn btn-secondary">Calcula Rendimiento</button>
						</div>
					</div>
		      		<div class="row mt-2">
		      			<div class="col-12">
		      				Justificación:
							<textarea class="form-control" rows="4" id="msjJustificacionGasolina" name="msjJustificacionGasolina" maxlength="500"></textarea>
							<p>La información capturada será firmada posteriormente</p>
						</div>	
					</div>
		    		
					<div class="row">
		      			<div class="col-12">
		        			<button type="button" id="btnJustificacion" onclick="guardaJustif();" class="btn btn-primary">Guardar</button>
		    			</div>
		    		</div>
				</div>
				
			</form>
		
		</div>
	</body>
</html>