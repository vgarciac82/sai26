<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
	String ue=folio.substring( 5, 8 );
	Calendar c1 = Calendar.getInstance();
	String today = sdf.format(c1.getTime());
	String msg;
	if (session.getAttribute("RESULT") != null) {
		msg = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
	int idCaso = c.getIdCaso();
	String []fecha=today.split( "/" );
	//System.out.println(fecha[2] ); 
 %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF8">
<title>Captura  proceso a entera satisfacción de contratos de servicio</title>
<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<style type="text/css" title="currentStyle"> 
 		@import "../../css/interfaz.css";
	</style>
	<script type="text/javascript" src="../../jq/js/jquery-3.7.1.js"></script>
	<script type="text/javascript" src="../../jq/jquery-ui-1.14.1/jquery-ui.js"></script>
	<script type="text/javascript" src="../../jq/jquery-ui-1.14.1/jquery-ui.min.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI.js"></script>
	
	<script type="text/javascript" src="../../Bootstrap/Bootstrap502/js/bootstrap.js"></script>
	<script type="text/javascript" src="../../Bootstrap/Bootstrap502/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../../Bootstrap/Bootstrap-dataTables/datatables.js"></script>
	<script type="text/javascript" src="../../Bootstrap/Bootstrap-dataTables/datatables.min.js"></script>
	
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/funciones.js"></script>
	
	<script type="text/javascript" src="../js/ProcesoEnteraSatisfaccion.js"></script>
<!-- 	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script> -->
	<link rel="stylesheet" type="text/css" href="../../css/fontawesome/fontawesome.min.css" />
	<link rel="stylesheet" type="text/css" href="../../Bootstrap/Bootstrap502/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="../../Bootstrap/Bootstrap502/css/bootstrap.min.css"/>
	<link rel="stylesheet" type="text/css" href="../../Bootstrap/Bootstrap-dataTables/datatables.css"/>
	
<!-- 	quitar el siguiente link -->
	<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
	
	
	<link rel="stylesheet" type="text/css" href="../../jq/jquery-ui-1.14.1/jquery-ui.css"/>
<!-- 	<link rel="stylesheet" type="text/css" href="../../css/reportesGRM.css"  /> -->
	
	
	<script type="text/javascript" charset="utf-8">
		var oTabletblDetalleInasistencia="";
		var oTabletblDetalleInasistenciaDeficiente="";
		var oTableConsultaFirmante;
		var oTableConsulta;
		var myModal;
		var myModalFirmante;
		var myModalItems;
		var devolucion=false;
		$(document).ready(function() {
			$('#tblConsulta').on('dblclick', 'tr',function(){
				if ($(this).hasClass('row_selected'))             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
			  	var anSelected=fnGetSelected(oTableConsulta);
			  	aData=oTableConsulta.fnGetData(anSelected[0]);
			  	addParams(aData);
			  	fillSelects();
			  	$("#lblEjercioPago").val(<%=fecha[2]%>);
			  	myModal.hide();
			});
			$( "#divAcordionActa" ).on( "click", function() {
				if($('#tblDetalleInasistencia >tbody >tr').length>0){
					oTabletblDetalleInasistencia.fnAdjustColumnSizing();
				}
				
				if($('#tblDetalleInasistenciaDeficiente >tbody >tr').length>0){
					oTabletblDetalleInasistenciaDeficiente.fnAdjustColumnSizing();
				}
			} );
			$('#tblConsultaFirmante').on('dblclick', 'tr',function(){
				if ($(this).hasClass('row_selected'))             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
			  	var anSelected=fnGetSelected(oTableConsultaFirmante);
			  	aData=oTableConsultaFirmante.fnGetData(anSelected[0]);
			  	addParamsFirmantes(aData);
			  	myModalFirmante.hide();
			});
			
		});//Fin del document Ready
	</script>
</head>
<body>
	<form id="formPrestacionEnteraSatisfaccion">
		<div class="container-fluid fondoWhite margin">
			<div class="row">
				<h4 ><span style="color: #1A69A9;" id="spanEncabezado">Proceso a entera satisfacci&oacute;n de contratos de servicios "vigilancia, limpieza, jardinería y fumigaci&oacute;n"</span> </h4>
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
								<input type="text" aria-label="Contrato" class="form-control input-group-sm " placeholder="" 
								id="pedidoContratoCompromiso" name="pedidoContratoCompromiso" value="" readonly="readonly">
								<input type="button" id="btnShowModal" value="Seleccionar Contrato" class="btn btn-primary" onclick="searchContract();" />
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
					      		<select class="form-select" id="catMesPago" name="catMesPago" onchange="addDato();">
								</select>
				      		</div>
				    	</div>
				    	<div class="form-group  col-4">
							<div class="input-group">
					      		<span class="input-group-text input-group-sm">Prestación del servicio:</span>
					      		<select class="form-select" id="prestacionServicio" name="prestacionServicio" >
								</select>
				      		</div>
				    	</div>
					</div>
					<div class="form-group  row">
						<div class="form-group  col-7">
							<div class="input-group">
					      		<span class="input-group-text input-group-sm">Partida de Contrato:</span>
					      		<select class="form-select" id="nPartidaCont" name="nPartidaCont" >
								</select>
				      		</div>
				    	</div>
				    </div>
				    <div class="form-group  row">
				    	<div class="form-group col-7">
							<div class="input-group">
							  	<span class="input-group-text">Firmante:</span>
								<input type="text" aria-label="Contrato SAI" class="form-control input-group-sm " placeholder="Favor de seleccionar el firmante"   
								id="firmanteResponsable" name="firmanteResponsable" value="" readonly="readonly">
								<input type="button" id="btnShowModalFirmResp" value="Seleccionar Firmante" class="btn btn-primary" onclick="searchFirmante(0);"/>
							</div>
						</div>
					</div>
					<div class="form-group  row">
						<div class="form-group  col-7">
							<div class="input-group">
					      		<span class="input-group-text input-group-sm">¿El servicio se prest&oacute; a entera satisfacci&oacute;n?</span>
					      		<select class="form-select" id="nServicioPrestado" name="nServicioPrestado" onchange="mostrarDivTestigos();" >
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
									<input type="text" aria-label="Testigo 1" class="form-control input-group-sm " placeholder="Favor de seleccionar el testigo 1"   
									id="testigo1" name="testigo1" readonly="readonly">
									<input type="button" id="btnShowModalTestigo1" value="Seleccionar Firmante" class="btn btn-primary" onclick="searchFirmante(1);"/>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="form-group col-7">
								<div class="input-group">
								  	<span class="input-group-text">Testigo 2:</span>
									<input type="text" aria-label="Testigo 2" class="form-control input-group-sm " placeholder="Favor de seleccionar el testigo 2"   
									id="testigo2" name="testigo2" readonly="readonly">
									<input type="button" id="btnShowModalTestigo2" value="Seleccionar Firmante" class="btn btn-primary" onclick="searchFirmante(2);"/>
								</div>
							</div>
						</div>
						<div class="row">
					    	<div class="form-group col-7">
								<div class="input-group">
								  	<h6 style="color: red">Favor de adjuntar evidencia de los hechos en el apartado de "Adjuntos" despues de haber guardado los datos.</h6>
								</div>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="row">
							<div  class="form-group col-7">
								<div class="form-floating" id="divObservaciones">
								  <textarea class="form-control" placeholder="Comentarios" id="cObservations" name="cObservations" readonly="readonly"></textarea>
								  <label for="cObservations" id="floatingTextarea"> </label>
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
								      			<input id="lblContratoCNET" name="lblContratoCNET" class="transpInput" value=""  
								      			readonly="readonly" style="width: 320px;background-color: #e5e7e9;"/> ratificado y firmado electrónicamente 
								      			por las “PARTES” el día <input id="lblDia" name="lblDia" class="transpInput" value=""  style="width: 25px;background-color: yellow;" />  
								      			de <input id="lblMesFormalizado" name="lblMesFormalizado" class="transpInput" value=""  style="width: 80px;background-color: yellow;" /> de  
								      			<input id="lblEjercicio" name="lblEjercicio" class="transpInput" value=""  style="width: 35px;background-color: yellow;"  />
								      			, formalizado con el proveedor <input id="lblProveedor" name="lblProveedor" class="transpInput" value=""  
								      			style="width: 255px;background-color: #e5e7e9;" readonly="readonly" />
								      			, informándole que en seguimiento 
								      			a la declaración <input id="lblDeclaracion" name="lblDeclaracion" class="transpInput" value=""  style="width: 35px;background-color: yellow;"  />   
								      			de “LA CONAFOR, como área responsable de administrar y verificar la prestación de los servicios en sitio, 
								      			informándole que el servicio contratado 
								      			<input id="lblSatisfaccion" name="lblSatisfaccion" class="transpInput" value="se prestó a satisfacción o no satisfacción (solicitando el cálculo y aplicación de penas convencionales o deducciones al pago)"  
								      			style="width: 750px;background-color: yellow;" /> 
								      			 en el inmueble de 
												<input id="lblInmueble" name="lblInmueble" class="transpInput" value=""  style="width: 150px;background-color: yellow;"  /> 
								      			de la CONAFOR, en estricto apego a la propuesta técnica presentada por el proveedor, en el Anexo 1 “Términos de Referencia”, 
								      			de la convocatoria y de acuerdo a las fechas pactadas para ello, durante el mes de 
								      			<input id="lblMes" name="lblMes" class="transpInput" value=""  style="width: 65px;background-color: #e5e7e9;" readonly="readonly" /> 
								      			de <input id="lblEjercioPago" name="lblEjercioPago" class="transpInput" value="2025"  style="width: 38px;background-color: yellow;"  />,  
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
							      		<div class="form-group  row">
										  	<div class="d-grid gap-2 d-md-flex justify-content-md-end">
												<button type="button" class="btn btn-primary" id="btnAnexo" name="btnAnexo"  onclick="generateAnexo(3)">Crear Anexo 1 A</button>
											</div>
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
							      				En el <input id="lblLucharHechos" name="lblLucharHechos" class="transpInput" value="Municipio de Zapopan, Jalisco"  style="width: 200px;background-color: yellow;"  /> 
							      				, siendo las <input id="lblFechaHechos" name="lblFechaHechos" class="transpInput" value="16:00 horas del día 01 uno del mes de julio del 2022 dos mil veintidós,en el lugar que ocupan las Oficinas Centrales de la Comisión Nacional forestal, ubicadas en Periférico Poniente Número 5360, Colonia, San Juan de Ocotán, C.P. 45019, en Zapopan, Jalisco"  style="width: 950px;background-color: yellow;"  /> 
							      				, nos constituimos los C.C. 
							      				<input id="lblNombreJefeDirecto" name="lblNombreJefeDirecto" class="transpInput" value="Nombre del jefe directo"  style="width: 200px;background-color: #e5e7e9;" readonly="readonly" />
							      				, en su carácter de <input id="lblPuestoFirmante" name="lblPuestoFirmante" class="transpInput" value="Puesto de jefe directo"  style="width: 150px;background-color: #e5e7e9;" readonly="readonly"  />, identificándose con credencial de elector, con número de folio 
							      				<input id="lblFolioJefe" name="lblFolioJefe" class="transpInput" value="0130069945397"  style="width: 120px;background-color: yellow;"  />, expedida por el Instituto 
							      				Federal Electoral, misma que contiene una fotografía que coincide con los rasgos faciales; 
							      				<input id="lblTestigo1" name="lblTestigo1" class="transpInput" value="Nombre de testigo de asistencia"  style="width: 200px;background-color: #e5e7e9;" readonly="readonly"  />, 
							      				con el puesto de <input id="lblPuestoTestigo1" name="lblPuestoTestigo1" class="transpInput" value="puesto del testigo de asistencia"  style="width: 150px;background-color: #e5e7e9;" readonly="readonly"  />, identificándose con credencial de elector, con número de folio 
							      				<input id="lblFolioTestigo1" name="lblFolioTestigo1" class="transpInput" value="3716073428786"  style="width: 120px;background-color: yellow;"  />, 
							      				expedida por el Instituto Nacional Electoral, misma que contiene una fotografía que coincide con los rasgos faciales; 
							      				<input id="lblTestigo2" name="lblTestigo2" class="transpInput" value="Nombre de testigo de asistencia"  style="width: 200px;background-color: #e5e7e9;" readonly="readonly"  />, 
							      				con el puesto de <input id="lblPuestoTestigo2" name="lblPuestoTestigo2" class="transpInput" value="puesto del testigo de asistencia"  style="width: 150px;background-color: #e5e7e9;" readonly="readonly"  />, identificándose con credencial de elector, 
							      				con número de folio <input id="lblFolioTestigo2" name="lblFolioTestigo2" class="transpInput" value="0052071498655"  style="width: 120px;background-color: yellow;"  />, 
							      				expedida a su favor por el Instituto Nacional Electoral, misma que contiene una fotografía que 
							      				coincide con los rasgos faciales y que al final suscribe  Servidores Públicos todos adscritos 
							      				<input id="lblPromotoriaHechos" name="lblPromotoriaHechos" class="transpInput" value="a la Promotoría Desarrollo Forestal Baja California"  style="width: 600px;background-color: yellow;" />, 
							      				para dar por iniciada la presente acta circunstanciada, en la que se asientan y se hacen constar los siguientes.
							      			</p>
							      			<p>
							      			- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
							      			- - - - - - - - - - - - - - - - - - - - - H E C H O S - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
							      			- - - - - - - - - - - - - - - - - - - - 
							      			</p>
							      			
							      			<textarea rows="12" cols="200" id="textAreaHechos" style="border: 0;background-color: yellow;"></textarea> 
							      			
							      			<p>
							      			- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - CIERRE DEL ACTA - - - - - - - - - - - - - - - - - - - - - - 
							      			- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
							      			</p>
							      			<p>
							      			No habiendo otro asunto que tratar, se cierra la presente acta Circunstanciada, siendo las 
							      			<input id="lblCierreActa" name="lblCierreActa" class="transpInput" value="17:00 horas del día 01 uno del mes de julio del año 2022 dos mil veintidós"  style="width: 450px;background-color: yellow;"  />, en que se actúa, misma que consta de 2 fojas útiles escritas sólo por el frente, firmando al margen y al calce, 
							      			los que en ella intervinieron, imprimiéndose en tres tantos 
							      			</p>
							      			
							      		</div>
							      		<div class="form-group  row">
										  	<div class="d-grid gap-2 d-md-flex justify-content-md-end">
												<button type="button" class="btn btn-primary" id="btnActaHechos" name="btnActaHechos"  onclick="generateAnexo(4)">Crear Acta de Hechos</button>
											</div>
							      		</div>
							      		<div class="form-group">
											<div class="row">
												<div class="col">
													<label style="POSITION: relative; TOP:-2px; LEFT:5px;color: blue;">Tabla para "Servicio no prestado"</label><br/>
												</div>	
												<div class="col">
													<button type="button" class="btn btn-primary" id="btnAddItemServNoPrestado" name="btnAddItemServNoPrestado"  onclick="showFormItem(1)">Agregar Registro</button>
												</div>
												<div class="col">
													<button type="button" class="btn btn-danger" id="btnDeleteServNoPrestado" name="btnDeleteServNoPrestado"  onclick="deleteItems(1)">Borrar Registros</button>
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
												<div class="col">
													<button type="button" class="btn btn-primary" id="btnAddItemServDeficiencia" name="btnAddItemServDeficiencia"  onclick="showFormItem(2)">Agregar Registro</button>
												</div>
												<div class="col">
													<button type="button" class="btn btn-danger" id="btnDeleteItemServDeficiencia" name="btnDeleteItemServDeficiencia"  onclick="deleteItems(2)">Borrar Registros</button>
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
					<!-- Modal -->
					<div class="modal fade bd-example-modal-lg" id="modalContractsServices" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
					  <div class="modal-dialog modal-lg">
					    <div class="modal-content">
					      <div class="modal-header">
					        <h5 class="modal-title" id="exampleModalLabel">Contratos de Servicios </h5>
					        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
					      </div>
					      <div class="modal-body">
					        	<div class="form-group row" >
				        			<div class="col-6 col-sm-4">
   										<label for="cIdDefinitivo" class="form-label">Contrato SAI</label>
										<input type="text" class="form-control" id="cIdDefinitivo" name="cIdDefinitivo" placeholder="Ejemplo CV-A04-1/2022">
				        			</div>
									<div class="col-3 col-sm-4">
   										<label for="cIdDefinitivo" class="form-label">Proveedor</label>
										<input type="text" class="form-control" id="cRazonSocial" name="cRazonSocial" placeholder="Raz&oacute;n Social">
				        			</div>
				        			<div class="col-3 col-sm-4">
   										<label for="cIdDefinitivo" class="form-label">Contrato CNET</label>
										<input type="text" class="form-control" id="cNumCNET_Search" name="cNumCNET_Search" placeholder="Ejemplo CNF-016RHQ001-E00">
				        			</div>
					        	</div>
					        	<div class="form-group row" >
					        		<div class="col-3 col-sm-4">
   										<input type="button" id="btnSearchContract" value="Buscar" class="btn btn-primary" onclick="queryTableContracts();"/>
				        			</div>
					        	</div>
					        	<div class="form-group">
									<div class="row">
										<div class="col">
											<table id="tblConsulta" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
												<thead >
													<tr>
														<th align="center">Contrato SAI</th>
														<th align="center">RFC</th>
														<th align="center">Contrato CNET</th>
														<th align="center">Proveedor </th>
														<th align="center">Concepto </th>
														<th align="center">Tipo </th>
														<th align="center" style="display: none;">Day </th>
														<th align="center" style="display: none;">Mes </th>
														<th align="center" style="display: none;">Anio </th>
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
					<!-- Modal Firmante-->
					<div class="modal fade bd-example-modal-lg" id="modalFirmante" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
					  <div class="modal-dialog modal-lg">
					    <div class="modal-content">
					      <div class="modal-header">
					        <h5 class="modal-title" id="exampleModalFirmante">Catálogo de Empleados con Usuario en SAI </h5>
					        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
					      </div>
					      <div class="modal-body">
					        	<div class="form-group row" >
				        			<div class="col-6 col-sm-2">
   										<label for="nNumEmpl_serch" class="form-label">Núm. Empleado</label>
										<input type="text" class="form-control" id="nNumEmpl_serch" name="nNumEmpl_serch" placeholder="Número de Empleado">
				        			</div>
									<div class="col-3 col-sm-3">
   										<label for="cNombreEmpl_serch" class="form-label">Nombre</label>
										<input type="text" class="form-control" id="cNombreEmpl_serch" name="cNombreEmpl_serch" placeholder="Nombre de Empleado">
				        			</div>
				        			<div class="col-3 col-sm-3">
   										<label for="cPrimerAp_serch" class="form-label">Primer Apellido</label>
										<input type="text" class="form-control" id="cPrimerAp_serch" name="cPrimerAp_serch" placeholder="Primer Apellido">
				        			</div>
				        			<div class="col-3 col-sm-3">
   										<label for="cSegundoAp_serch" class="form-label">Segundo Apellido</label>
										<input type="text" class="form-control" id="cSegundoAp_serch" name="cSegundoAp_serch" placeholder="Segundo Apellido">
				        			</div>
					        	</div>
					        	<div class="form-group row" >
					        		<div class="col-3 col-sm-4">
   										<input type="button" id="btnSearchEmpleado" value="Buscar" class="btn btn-primary" onclick="queryTableEmpleados();"/>
				        			</div>
					        	</div>
					        	<div class="form-group">
									<div class="row">
										<div class="col">
											<table id="tblConsultaFirmante" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
												<thead >
													<tr>
														<th align="center">Número<br> Empleado</th>
														<th align="center">Nombre</th>
														<th align="center">Primer Apellido</th>
														<th align="center">Segundo Apellido </th>
														<th align="center">Puesto </th>
														<th align="center" style="display: none;">Nombre Completo </th>
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
					<div class="modal fade bd-example-modal-lg" id="modalItems" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
					  <div class="modal-dialog modal-lg">
					    <div class="modal-content">
					      <div class="modal-header">
					        <h5 class="modal-title" id="exampleModalItems">Agregar registro</h5>
					        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
					      </div>
					      <div class="modal-body">
					        	<div class="form-group row" >
				        			<div class="col-3 col-sm-3">
   										<label for="ncantidadElementos_add" class="form-label">Cantidad</label>
										<input type="text" class="form-control" id="ncantidadElementos_add" name="ncantidadElementos_add" placeholder="Cantidad">
				        			</div>
									<div class="col-3 col-sm-3">
   										<label for="mMontoTotal_add" class="form-label">Total</label>
										<input type="text" class="form-control" id="mMontoTotal_add" name="mMontoTotal_add" placeholder="Total antes de IVA">
				        			</div>
				        			<div class="col-3 col-sm-3">
   										<label for="nCantidadDias_add" class="form-label">Cantidad de Días</label>
										<input type="text" class="form-control" id="nCantidadDias_add" name="nCantidadDias_add" placeholder="Cantidad de días">
				        			</div>
				        			<div class="col-3 col-sm-3">
   										<label for="nPorcentajePenalidad_add" class="form-label">Porcentaje Penalidad</label>
										<input type="text" class="form-control" id="nPorcentajePenalidad_add" name="nPorcentajePenalidad_add" placeholder="Porcentaje de penalidad" value="10">
				        			</div>
					        	</div>
					        	<div class="form-group row" >
				        			<div class="col-3 col-sm-3">
   										<label for="nPorcentajeGarantiaCumplimiento_add" class="form-label">Porcentaje Garantía</label>
										<input type="text" class="form-control" id="nPorcentajeGarantiaCumplimiento_add" name="nPorcentajeGarantiaCumplimiento_add" placeholder="Garantía de cumplimiento" value="10">
				        			</div>
									<div class="col-3 col-sm-6">
   										<label for="cDescripcion_add" class="form-label">Concepto</label>
										<input type="text" class="form-control" id="cDescripcion_add" name="cDescripcion_add" placeholder="Concepto de la deficiencia del servicio">
				        			</div>
				        			
					        	</div>
					        	<div class="form-group row" >
					        		<div class="col-3 col-sm-4">
					        			<input type="hidden" id="cFolio_search" name="cFolio_search" value="" />
   										<input type="button" id="btnAddItem" value="Agregar" class="btn btn-primary" onclick="addItem();"/>
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