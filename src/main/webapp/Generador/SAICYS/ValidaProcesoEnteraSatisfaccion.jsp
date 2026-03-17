 <%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	String uLogin = usuario.getLogin();
	String folio = c.getFolio();
	Calendar c1 = Calendar.getInstance();
	String today = sdf.format(c1.getTime());
	String msg;
	String ue=folio.substring( 5, 8 );
	if (session.getAttribute("RESULT") != null) {
		msg = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}

	int idCaso = c.getIdCaso();
	
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF8">
<title>Captura  proceso a entera satisfacción de contratos de servicio</title>
<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<style type="text/css" title="currentStyle"> 
		@import "../../css/custom-theme/jquery-ui-1.8.16.custom.css";
 		@import "../../css/interfaz.css";
	</style>
	<script src="https://code.jquery.com/jquery-3.5.0.js"></script>
	<link rel="stylesheet" type="text/css" href="../../Bootstrap/Bootstrap502/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="../../Bootstrap/Bootstrap-dataTables/datatables.css"/>
	
	<script type="text/javascript" src="../../Bootstrap/Bootstrap502/js/bootstrap.js"></script>
	<script type="text/javascript" src="../../Bootstrap/Bootstrap502/js/bootstrap.min.js"></script>
	
	<script type="text/javascript" src="../../Bootstrap/Bootstrap-dataTables/datatables.js"></script>
	<script type="text/javascript" src="../../Bootstrap/Bootstrap-dataTables/datatables.min.js"></script>
	
	
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
	<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>
	
	<link rel="stylesheet" href="https://code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
	<script src="https://code.jquery.com/ui/1.13.2/jquery-ui.js"></script>
	
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/funciones.js"></script>
 	
	<link href="../../css/reportesGRM.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../js/FixedColumns.js"></script>
	
	<script type="text/javascript" src="../js/ProcesoEnteraSatisfaccion.js"></script>
	
	<script type="text/javascript" charset="utf-8">
		var oTabletblDetalleInasistencia="";
		var oTabletblDetalleInasistenciaDeficiente="";
		var oTableConsultaFirmante;
		var myModal;
		var myModalFirmante;
		var myModalItems;
		var devolucion=false;
		$(document).ready(function() {
			
		});//Fin del document Ready
	</script>
</head>
<body>
	<form id="formValidaPrestacionEnteraSatisfaccion">
		<div class="container-fluid fondoWhite margin">
			<div class="row">
				<h4 ><span style="color: #1A69A9;" id="spanEncabezado">Validar proceso a entera satisfacci&oacute;n de contratos de servicios "vigilancia, limpieza, jardinería y fumigaci&oacute;n"</span> </h4>
			</div>
			<div class="row">
				<fieldset class="form-group border p-3">
					<div class="form-group  row">
						<div class="form-group  col-3">
							<div class="input-group">
					      		<span class="input-group-text input-group-sm">FOLIO SAI:</span>
					      		<input type="text" class=" form-control input-group-sm font-weight-bold" disabled title="Número de Folio, se asigna automáticamente" value="<%=c.getFolio()%>"  id="cFolio" name="cFolio" />
				      		</div>
				    	</div>
				    	<div class="form-group col-4">
							<div class="input-group">
							  	<span class="input-group-text">CONTRATO SAI:</span>
								<input type="text" aria-label="Contrato" class="form-control input-group-sm " placeholder="Favor de seleccionar el n&uacute;mero de contrato SAI"   onkeypress="return notWritte();" 
								onkeydown="no_backspaces(event);" id="pedidoContratoCompromiso" name="pedidoContratoCompromiso" value="" disabled>
							</div>
						</div>
				    </div>
				    <div class="form-group  row">
						<div class="form-group  col-7">
							<div class="input-group">
					      		<span class="input-group-text input-group-sm">CONTRATO CNET:</span>
					      		<input type="text" class="form-control input-group-sm font-weight-bold" disabled title="Número de Contrato CNET" value=""  id="cNumCNET" name="cNumCNET" />
				      		</div>
				    	</div>
					</div>
					<div class="form-group  row">
						<div class="form-group  col-3">
							<div class="input-group">
					      		<span class="input-group-text input-group-sm">Mes de pago:</span>
					      		<select class="custom-select" id="catMesPago" name="catMesPago" onchange="addDato();" disabled>
								</select>
				      		</div>
				    	</div>
				    	<div class="form-group  col-4">
							<div class="input-group">
					      		<span class="input-group-text input-group-sm">Prestación del servicio:</span>
					      		<select class="custom-select" id="prestacionServicio" name="prestacionServicio" disabled>
								</select>
				      		</div>
				    	</div>
					</div>
					<div class="form-group  row">
						<div class="form-group  col-7">
							<div class="input-group">
					      		<span class="input-group-text input-group-sm">Partida de Contrato:</span>
					      		<select class="custom-select" id="nPartidaCont" name="nPartidaCont" disabled>
								</select>
				      		</div>
				    	</div>
				    </div>
				    <div class="form-group  row">
				    	<div class="form-group col-7">
							<div class="input-group">
							  	<span class="input-group-text">Firmante:</span>
								<input type="text" aria-label="Contrato SAI" class="form-control input-group-sm " placeholder="Favor de seleccionar el firmante"  
								onkeydown="no_backspaces(event);" id="firmanteResponsable" name="firmanteResponsable" value="Nombre del jefe directo" disabled>
							</div>
						</div>
					</div>
					<div class="form-group  row">
						<div class="form-group  col-7">
							<div class="input-group">
					      		<span class="input-group-text input-group-sm">¿El servicio se prest&oacute; a entera satisfacci&oacute;n?</span>
					      		<select class="custom-select" id="nServicioPrestado" name="nServicioPrestado" disabled >
					      			<option value="0">Seleccionar</option>
					      			<option value="1">SI</option>
					      			<option value="2">NO</option>
								</select>
				      		</div>
				    	</div>
					</div>
					<div class="form-group" id="divFirmantesTestigos" style="display: none;">
						<div class="row">
					    	<div class="form-group col-7">
								<div class="input-group">
								  	<span class="input-group-text">Testigo 1:</span>
									<input type="text" aria-label="Testigo 1" class="form-control input-group-sm " placeholder="Favor de seleccionar el testigo 1"   disabled 
									onkeydown="no_backspaces(event);" id="testigo1" name="testigo1" >
								</div>
							</div>
						</div>
						<div class="row">
							<div class="form-group col-7">
								<div class="input-group">
								  	<span class="input-group-text">Testigo 2:</span>
									<input type="text" aria-label="Testigo 2" class="form-control input-group-sm " placeholder="Favor de seleccionar el testigo 2"   disabled
									onkeydown="no_backspaces(event);" id="testigo2" name="testigo2" >
								</div>
							</div>
						</div>
					</div>
					<div class="form-group" id="divRespuesta" >
						<div class="row">
					    	<div class="form-group" id="divValidation">
								<div class="form-check form-check-inline">
									<h4 ><span  id="spanProcesoCalculo">¿Los datos capturados son correctos? </span> </h4>
								</div>
								<div class="form-check form-check-inline">
								  	<input class="form-check-input" type="radio" name="radioAut" id="radioSiAut" value="1" onclick="showAndHideObservation()" >
								  	<label class="form-check-label" for="radioSiAut">Si</label>
								</div>
								<div class="form-check form-check-inline">
								  	<input class="form-check-input" type="radio" name="radioAut" id="radioNoAut" value="0" onclick="showAndHideObservation()">
								  	<label class="form-check-label" for="radioNoAut">No</label>
								</div>
							</div>
						</div>
					</div>
					<div class="form-group" id="divObservacion">
						<div class="row">
							<div  class="form-group col-10">
								<div class="form-floating" id="divObservaciones">
								  <textarea class="form-control" placeholder="Comentarios" id="cObservations" name="cObservations"></textarea>
								  <label for="cObservations" id="floatingTextarea">  Favor de capturar las observaciones.</label>
								</div>
							</div>
						</div>
					</div>
					<div class="form-group  row">
						<div class="accordion accordion-flush" id="accordionFlushExample">
							<div class="accordion-item">
							    <h5 class="accordion-header" id="flush-headingOne">
							      	<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#flush-collapseOne" aria-expanded="false" aria-controls="flush-collapseOne">
							        	ANEXO 1 A
							        	<br>
							        	ESCRITO DE CONFORMIDAD DE LA PRESTACI&Oacute;N DEL SERVICIO
							      	</button>
							    </h5>
							    <div id="flush-collapseOne" class="accordion-collapse collapse show" aria-labelledby="flush-headingOne" data-bs-parent="#accordionFlushExample">
							      	<div class="accordion-body">
							      		<div class="form-group  row">
							      			<p class="text-start font-weight-bold" id="dirigidoA">
							      			<input type="text" class="transpInput font-weight-bold" name="nombreGerenteMateriales" id="nombreGerenteMateriales" value="Nombre del Gerente" readonly/><br/>
							      			Gerente de Recursos Materiales
							      			</p>
							      		</div>
							      		<div class="form-group  row">
							      			<dl><p id="primerParrafo" class="text-start">
								      			Hago referencia al contrato con número 
								      			<input id="lblContratoCNET" name="lblContratoCNET" class="transpInput" value="_______________________________________________________"  
								      			readonly="readonly" style="width: 320px;background-color: #e5e7e9;"/> 
								      			ratificado y firmado electrónicamente 
								      			por las “PARTES” el día <input id="lblDia" name="lblDia" class="transpInput" value="__"  style="width: 25px;background-color: #e5e7e9;" readonly="readonly"/>  
								      			de <input id="lblMesFormalizado" name="lblMesFormalizado" class="transpInput" value="______________"  style="width: 80px;background-color: #e5e7e9;" readonly="readonly"/> de  
								      			<input id="lblEjercicio" name="lblEjercicio" class="transpInput" value="____"  style="width: 35px;background-color: #e5e7e9;" readonly="readonly" />
								      			, formalizado con el proveedor <input id="lblProveedor" name="lblProveedor" class="transpInput" value="_____________________________________________"  
								      			style="width: 255px;background-color: #e5e7e9;" readonly="readonly" />
								      			, informándole que en seguimiento 
								      			a la declaración <input id="lblDeclaracion" name="lblDeclaracion" class="transpInput" value=""  style="width: 35px;background-color: #e5e7e9;" readonly="readonly" />   
								      			de “LA CONAFOR, como área responsable de administrar y verificar la prestación de los servicios en sitio, 
								      			informándole que el servicio contratado 
								      			<input id="lblSatisfaccion" name="lblSatisfaccion" class="transpInput" value="se prestó a satisfacción o no satisfacción (solicitando el cálculo y aplicación de penas convencionales o deducciones al pago)"  
								      			style="width: 750px;background-color: #e5e7e9;" readonly="readonly"/> 
								      			 en el inmueble de 
												<input id="lblInmueble" name="lblInmueble" class="transpInput" value=""  style="width: 50px;background-color: #e5e7e9;"  readonly="readonly"/> 
								      			de la CONAFOR, en estricto apego a la propuesta técnica presentada por el proveedor, en el Anexo 1 “Términos de Referencia”, 
								      			de la convocatoria y de acuerdo a las fechas pactadas para ello, durante el mes de 
								      			<input id="lblMes" name="lblMes" class="transpInput" value=""  style="width: 65px;background-color: #e5e7e9;" readonly="readonly" /> 
								      			de <input id="lblEjercioPago" name="lblEjercioPago" class="transpInput" value="2025"  style="width: 38px;background-color: #e5e7e9;"  readonly="readonly"/>,  
								      			no omito mencionar que las obligaciones laborales con sus empleados fueron cumplidas satisfactoriamente , 
								      			consistente en lo siguiente:
							      			</p>
							      			<dd>
												<ol>
													<li>La planilla con el nombre del supervisor que sea contratado por éste, de manera mensual y en caso de existir terminación de la relación 
							      				    laboral con alguno de los trabajadores que integran la plantilla “EL PROVEEDOR” deberá exhibir el convenio finiquito o renuncia ratificada 
							      				    ante la autoridad laboral competente.</li>
													<li>Lista de asistencia del personal que realiza la prestación de los servicios.</li>
													<li>Las Constancias que acrediten el cumplimiento de las obligaciones laborales a cargo del proveedor a que se refiere el numeral 
												    XVI “Relaciones Laborales” de este documento, respecto de los recursos humanos que se empleen para la prestación de los servicios a favor de LA CONAFOR.</li>
													<li>Las constancias documentales de inscripción y pago a las instituciones de seguridad social, al Instituto Mexicano del 
												    Seguro Social (IMSS) e Instituto del Fondo Nacional para la Vivienda de los Trabajadores (INFONAVIT) de manera mensual.</li>
												</ol>
											</dd></dl>
							      			<p class="text-start">
							      				Se remite anexo al presente el soporte documental establecidos en la cláusula TERCERA.-FORMA Y LUGAR DE PAGO del contrato correspondiente, entre ellos 
							      				el soporte de cumplimiento de las obligaciones laborales del proveedor con sus empleados.
							      			</p>
							      			<br>
							      			<p class="text-start">
							      				Lo anterior a efecto de que se solicite la factura correspondiente a “EL PROVEEDOR”, para iniciar el trámite de pago correspondiente.
							      			</p>
							      		</div>
							      	</div>
								</div>
							</div>
							<div class="accordion-item" id="divAcordionActa" style="display: none;">
							    <h5 class="accordion-header" id="flush-headingTwo">
									<button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#flush-collapseTwo" aria-expanded="false" aria-controls="flush-collapseTwo">
							        	ACTA CIRCUNSTANCIADA DE HECHOS
							      	</button>
							    </h5>
							    <div id="flush-collapseTwo" class="accordion-collapse collapse" aria-labelledby="flush-headingTwo" data-bs-parent="#accordionFlushExample">
							      	<div class="accordion-body">
							      		<div class="form-group  row">
							      			<p>
							      				En el <input id="lblLucharHechos" name="lblLucharHechos" class="transpInput" value="Municipio de Zapopan, Jalisco"  style="width: 200px;background-color: #e5e7e9;"  readonly="readonly"/> 
							      				, siendo las <input id="lblFechaHechos" name="lblFechaHechos" class="transpInput" value="16:00 horas del día 01 uno del mes de julio del 2022 dos mil veintidós,en el lugar que ocupan las Oficinas Centrales de la Comisión Nacional forestal, ubicadas en Periférico Poniente Número 5360, Colonia, San Juan de Ocotán, C.P. 45019, en Zapopan, Jalisco"  
							      				style="width: 950px;background-color: #e5e7e9;"  readonly="readonly"/> 
							      				, nos constituimos los C.C. 
							      				<input id="lblNombreJefeDirecto" name="lblNombreJefeDirecto" class="transpInput" value="Nombre del jefe directo"  style="width: 200px;background-color: #e5e7e9;" readonly="readonly" />
							      				, en su carácter de <input id="lblPuestoFirmante" name="lblPuestoFirmante" class="transpInput" value="Puesto de jefe directo"  style="width: 150px;background-color: #e5e7e9;" readonly="readonly"  />, identificándose con credencial de elector, con número de folio 
							      				<input id="lblFolioJefe" name="lblFolioJefe" class="transpInput" value="0130069945397"  style="width: 120px;background-color: #e5e7e9;"  readonly="readonly"/>, expedida por el Instituto 
							      				Federal Electoral, misma que contiene una fotografía que coincide con los rasgos faciales; 
							      				<input id="lblTestigo1" name="lblTestigo1" class="transpInput" value="Nombre de testigo de asistencia"  style="width: 200px;background-color: #e5e7e9;" readonly="readonly"  />, 
							      				con el puesto de <input id="lblPuestoTestigo1" name="lblPuestoTestigo1" class="transpInput" value="puesto del testigo de asistencia"  style="width: 150px;background-color: #e5e7e9;" readonly="readonly"  />, identificándose con credencial de elector, con número de folio 
							      				<input id="lblFolioTestigo1" name="lblFolioTestigo1" class="transpInput" value="3716073428786"  style="width: 120px;background-color: #e5e7e9;"  readonly="readonly"/>, 
							      				expedida por el Instituto Nacional Electoral, misma que contiene una fotografía que coincide con los rasgos faciales; 
							      				<input id="lblTestigo2" name="lblTestigo2" class="transpInput" value="Nombre de testigo de asistencia"  style="width: 200px;background-color: #e5e7e9;" readonly="readonly"  />, 
							      				con el puesto de <input id="lblPuestoTestigo2" name="lblPuestoTestigo2" class="transpInput" value="puesto del testigo de asistencia"  style="width: 150px;background-color: #e5e7e9;" readonly="readonly"  />, identificándose con credencial de elector, 
							      				con número de folio <input id="lblFolioTestigo2" name="lblFolioTestigo2" class="transpInput" value="0052071498655"  style="width: 120px;background-color: #e5e7e9;" readonly="readonly" />, 
							      				expedida a su favor por el Instituto Nacional Electoral, misma que contiene una fotografía que 
							      				coincide con los rasgos faciales y que al final suscribe  Servidores Públicos todos adscritos 
							      				<input id="lblPromotoriaHechos" name="lblPromotoriaHechos" class="transpInput" value="a la Promotoría Desarrollo Forestal Baja California"  style="width: 600px;background-color: #e5e7e9;" readonly="readonly" />, 
							      				para dar por iniciada la presente acta circunstanciada, en la que se asientan y se hacen constar los siguientes.
							      			</p>
							      			<p>
							      			- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
							      			- - - - - - - - - - - - - - - - - - - - - H E C H O S - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
							      			- - - - - - - - - - - - - - - - - - - - 
							      			</p>
							      			
							      			<textarea rows="12" cols="200" id="textAreaHechos" style="border: 0;background-color: #e5e7e9" disabled="disabled"></textarea> 
							      			
							      			<p>
							      			- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - CIERRE DEL ACTA - - - - - - - - - - - - - - - - - - - - - - 
							      			- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
							      			</p>
							      			<p>
							      			No habiendo otro asunto que tratar, se cierra la presente acta Circunstanciada, siendo las 
							      			<input id="lblCierreActa" name="lblCierreActa" class="transpInput" value="17:00 horas del día 01 uno del mes de julio del año 2022 dos mil veintidós"  style="width: 450px;background-color: #e5e7e9;" readonly="readonly"  />, en que se actúa, misma que consta de 2 fojas útiles escritas sólo por el frente, firmando al margen y al calce, 
							      			los que en ella intervinieron, imprimiéndose en tres tantos 
							      			</p>
							      			
							      		</div>
							      		<div class="form-group">
											<div class="row">
												<div class="col">
													<label style="POSITION: relative; TOP:-2px; LEFT:5px;color: blue;">Tabla para "Servicio no prestado"</label><br/>
												</div>	
											</div>
											<div class="row">	
												<div class="col">
													<table id="tblDetalleInasistencia" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
														<thead >
															<tr>
																<th style="display: none;" >#</th>
																<th >Cantidad</th>
																<th >Monto <br/>Antes de IVA</th>
																<th >Cantidad <br/>de días</th>
																<th >Porcentaje  <br/>Penalidad</th>
																<th >Porcentaje <br/>Garantía Cumplimiento</th>
																<th >Concepto</th>
															</tr>									
														</thead>
													</table>
												</div>
											</div>
											<br />
											<div class="row">
												<div class="col">
													<label style="POSITION: relative; TOP:-2px; LEFT:5px;color: blue;">Tabla para "Servicio con deficiencia"</label><br/>
												</div>
											</div>
											<div class="row">	
												<div class="col">
													<table id="tblDetalleInasistenciaDeficiente" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
														<thead>
															<tr>
																<th style="display: none;" >#</th>
																<th >Cantidad</th>
																<th >Monto <br/>Antes de IVA</th>
																<th >Cantidad <br/>de días</th>
																<th >Porcentaje  <br/>Deducción</th>
																<th >Porcentaje <br/>Garantía Cumplimiento</th>
																<th >Concepto</th>
															</tr>									
														</thead>
													</table>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
		<input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="CV" />
		<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=ue%>" />
		<input type="hidden" name="nIdFirmante" id="nIdFirmante" value=""/>
		<input type="hidden" name="numFirmanteResponsable" id="numFirmanteResponsable" value=""/>
		<input type="hidden" name="numEmpTestigo1" id="numEmpTestigo1" value=""/>
		<input type="hidden" name="numEmpTestigo2" id="numEmpTestigo2" value=""/>
		<input type="hidden" name="cIdRFC" id="cIdRFC" value=""/>
		<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" />
		<input type="hidden" name="FECHA_DOCUMENTO" id="FECHA_DOCUMENTO" value="<%=today%>" />
		<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=today.substring(6)%>" />
		<input type="hidden" name="datoParaTextArea" id="datoParaTextArea" value=""/>
		<input type="hidden" name="processInDB" id="processInDB" value="0"/>
		<input type="hidden" name="idTbl" id="idTbl" value="0"/>
		<input type="hidden" name="nServicioEnteraSatisfaccion" id="nServicioEnteraSatisfaccion" value="0"/>
		<input type="hidden" name="idCaso" id="idCaso" value="<%=idCaso%>" />
		
    </form>
</body>
</html>