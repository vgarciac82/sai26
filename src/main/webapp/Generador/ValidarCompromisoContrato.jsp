<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	//IRD 20131121	RO-0009 todo lo de pago de pasivo
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	int id_oper = c.getCasoOperacion(0).getIdOperacion();
	String folio = c.getFolio();
	boolean esConvenioModificatorio = (5 == id_oper);
	boolean esPagoPasivo = (7 == id_oper);
	boolean esPlurianual = (9 == id_oper);

	String cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
	boolean esSAIAlterno = "true".equals(cabl.getSystemSetting("SAI_AMBIENTAL")) || "true".equals(cabl.getSystemSetting("SAI_FONDEN"));
%>
<!DOCTYPE html>

<html>
<head>
<title>Ventanilla PreCompromiso</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
	<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
	<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
	<link rel="stylesheet" type="text/css" href="../css/interfaz.css"/>
			
			<style type="text/css" title="currentStyle">
	@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
	
	@import "css/demo_table_jui.css";
	
	@import "css/demo_page.css";
	</style>
	<style>
		.monto {
			text-align: right;
			background-color: #CCCCCC;
			border: 1px solid #aaaaaa;
			color: #222222;
		}
	</style>
	<script src="https://code.jquery.com/jquery-3.5.0.js"></script>
	
	<link rel="stylesheet" type="text/css" href="../Bootstrap/Bootstrap502/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="../Bootstrap/Bootstrap-dataTables/datatables.css"/>

	<script type="text/javascript" src="../Bootstrap/Bootstrap502/js/bootstrap.js"></script>
	<script type="text/javascript" src="../Bootstrap/Bootstrap502/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../Bootstrap/Bootstrap-dataTables/datatables.js"></script>

	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
	<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>

	<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="js/ValidarCompromisoContrato.js"></script>
	
	<script type="text/javascript" charset="utf-8">
		var esConvenioModificatorio = <%=esConvenioModificatorio%> ;
		var esPagoPasivo = <%=esPagoPasivo%>;
		var esPlurianual = <%=esPlurianual%>;
		var esSAIAlterno = <%=esSAIAlterno%>;
		var myModalValidaFact,myModalApcon;
		$(document).ready(function() {
			$('#obraPub-list a').on('click', function (e) {
				e.preventDefault();
			  	$(this).tab('show');
			});
			myModalApcon = new bootstrap.Modal(document.getElementById('dialog-form'), {
			  keyboard: false
			});
		});
	</script>
</head>
<body >
	<form>		
		<div id="container" class="container-fluid">
			<h5 style="color: #1A69A9;" >Autorizaci&oacute;n de Precompromiso</h5>
			<fieldset class="form-group border p-3">
				<div class="form-group row">
					<div class="col-4 col-sm-4">
						<fieldset >
						<legend  style="color: #1A69A9;">Seleccione una acci&oacute;n</legend>
						<div class="form-check form-check-inline">
							<input type="radio" id="autorizado" value="S" name="operacionFinal" checked="checked" class="form-check-input">
						  	<label class="form-check-label" for="autorizado">Autorizar</label>
						</div>
						<div class="form-check form-check-inline">
							<input type="radio" id="rechazado" value="N" name="operacionFinal" class="form-check-input">
						  	<label class="form-check-label" for="rechazado">Rechazar</label>
						</div>
						</fieldset>
					</div>
				</div>
<!-- 				Sección de tabs -->
				<div class="form-group row">
					<div class="card">
						<div class="card-header">
				        	<ul class="nav nav-tabs card-header-tabs" id="obraPub-list" role="tablist">
				            	<li class="nav-item"><a id="aTab0" href="#tabs-0" class="nav-link active" role="tab" aria-controls="informacionGeneral" aria-selected="true">Informaci&oacute;n General</a></li>
								<li class="nav-item"><a id="aTab1" href="#tabs-1" class="nav-link" role="tab" aria-controls="detalle" aria-selected="false" >Detalle</a></li>
								<li class="nav-item"><a id="aTab2" href="#tabs-2" class="nav-link" role="tab" aria-controls="anticipos" aria-selected="false" >Anticipos</a></li>
								<li class="nav-item"><a id="aTab3" href="#tabs-3" class="nav-link" role="tab" aria-controls="convenioModificatorio" aria-selected="false" >Convenio Modificatorio</a></li>
								<li class="nav-item"><a id="aTab4" href="#tabs-4" class="nav-link" role="tab" aria-controls="detalleConvenio" aria-selected="false" >Detalle Convenio</a></li>
								<li class="nav-item"><a id="aTab5" href="#tabs-5" class="nav-link" role="tab" aria-controls="retencionAmort" aria-selected="false" >Retencion/Amortizaci&oacute;n</a></li>
								<li class="nav-item"><a id="aTab6" href="#tabs-6" class="nav-link" role="tab" aria-controls="detallePagoPasivo" aria-selected="false" >Detalle Pago de Pasivo</a></li>
								<li class="nav-item"><a id="aTab7" href="#tabs-7" class="nav-link" role="tab" aria-controls="detallePlurianual" aria-selected="false" >Detalle Plurianual</a></li>
								
				          	</ul>
						</div>
						<div class="card-body">
		           			<div class="tab-content mt-3">
		            			<div class="tab-pane active" id="tabs-0" role="tabpanel">
		            				<div class="form-group row" id="trCargaFacturas">
		            					<div class="col">
		            						<a href="#" onclick="creaDiagloFacturas(true);return false;" class="nav-link">Cargar Facturas</a>
		            					</div>
		            				</div>
		            				<div class="form-group row">
		            					<div class="col-3">
		            						<label for="ccvecontrato">No. Contrato:</label>
		            						<input type="text" class="form-control" name="ccvecontrato" id="ccvecontrato"  style="text-transform: uppercase; background-color: transparent" />
		            					</div>
		            					<div class="col-3">
		            						<label for="ctipocontratoobra">Tipo de Contrato:</label>
		            						<input type="text" class="form-control" name="ctipocontratoobra" id="ctipocontratoobra"  style="background-color: transparent" />
		            					</div>
		            					<div class="col-3">
		            						<label for="ccveconcurso">No. Concurso:</label>
		            						<input type="text" class="form-control" id="ccveconcurso" name="ccveconcurso"  style="background-color: transparent" />
		            					</div>
		            					<div class="col-3">
		            						<label for="ccvecartera">Cartera Proyecto:</label>
		            						<input type="text" class="form-control" id="ccvecartera" name="ccvecartera" style="background-color: transparent" />
		            					</div>
		            				</div>
		            				<div class="form-group row">
		            					<div class="col-3">
		            						<label for="ccveoli">O.L.I.:</label>
		            						<input type="text" class="form-control" id="ccveoli" name="ccveoli" style=" background-color: transparent">
		            					</div>
		            					<div class="col-3">
		            						<label for="cDescAdicionales">Doc. Asignaci&oacute;n:</label>
		            						<input type="hidden"  id="cIdAdicionales" name="cIdAdicionales"/>
		            						<input type="text" class="form-control" id="cDescAdicionales" name="cDescAdicionales" size="15" style="background-color: transparent" />  
		            					</div>
		            					<div class="col-3">
		            						<label for="noOfAdicionales">No. Oficio:</label>
		            						<input type="text" id="noOfAdicionales" name="noOfAdicionales"  style="background-color: transparent" class="form-control">
		            					</div>
		            					<div class="col-3">
		            						<label for="fAdicionales">Fecha:</label>
		            						<input type="text" id="fAdicionales" name="fAdicionales"  style="background-color: transparent" class="form-control"/>
		            					</div>
		            				</div>
		            				<div class="form-group row" style="display: none;">
		            					<div class="col-3">
		            						<div class="row"  >
												<div class="col-auto">		
													<label class="form-check-label" id="lbl_radicado" for="chk_esFonden">Presupuesto Radicado: </label><br>
												</div>
												<div class="col-auto">
									  				<input class="form-check-input" type="checkbox" id="chk_radicado" name="chk_radicado" value="N" >
									  			</div>
											</div>
		            					</div>
		            				</div>
		            				<div class="form-group row">
		            					<div class="col-3">
		            						<label for="crfc">RFC:</label>
		            						<input type="text" name="crfc" id="crfc"  style="background-color: transparent" class="form-control"/>
		            					</div>
		            					<div class="col-3">
		            						<label for="cbeneficiario">Raz&oacute;n Social:</label>
		            						<input type="text"  name="cbeneficiario" id="cbeneficiario"  style="background-color:transparent" class="form-control" />
		            					</div>
		            					<div class="col-3">
		            						<label for="descTipoRec">Tipo de Recurso:</label>
		            						<input type="hidden" name="ctiporecurso" id="ctiporecurso" /> 
		            						<input type="text"  name="descTipoRec" id="descTipoRec"  style="background-color:transparent" class="form-control"/> 
		            					</div>
		            					<div class="col-3">
		            						<label for="noFianza">No. de Fianza:</label>
		            						<input type="text"  name="noFianza" id="noFianza"  style="background-color:transparent" class="form-control"/>
		            					</div>
		            				</div>
		            				<div class="form-group row">
		            					<div class="col-3">
		            						<label for="entidadobra">Entidad Federativa:</label>
		            						<input type="text"  name="entidadobra" id="entidadobra"  style="background-color:transparent" class="form-control"/>
		            					</div>
		            					<div class="col-3">
		            						<label for="cIdRealEstate">Bien Inmueble:</label>
		            						<input type="text" name="cIdRealEstate" id="cIdRealEstate"  style="background-color:transparent" class="form-control"/>
		            					</div>
		            					<div class="col-3">
		            						<label for="cIdRealEstate">Tipo Adjudicaci&oacute;n:</label>
		            						<input type="hidden" name="ctipoadjudica" id="ctipoadjudica" /> 
		            						<input type="text" name="cDescAdjudica" id="cDescAdjudica"  style="background-color:transparent" class="form-control"/>
		            					</div>
		            				</div>
		            				<div class="form-group row">
		            					<div class="col-3">
		            						<label for="nmonto"> Procedimiento CNET:</label>
		            						<input type="text"  class="form-control montos" name="cNoProcedimientoCNET" id="cNoProcedimientoCNET"  style=" background-color:transparent" />
		            					</div>
		            					<div class="col-3">
		            						<label for="nmontoconiva"> Codigo de Expediente:</label>
		            						<input type="text"  name="nCodExpedienteCNET" id="nCodExpedienteCNET"  style="background-color:transparent" class="form-control" />
		            					</div>
		            					<div class="col-3">
		            						<label for="ffechainicontr"> Codigo de Contrato:</label>
		            						<input type="text"  class="form-control" name="nCodContratoCNET" id="nCodContratoCNET"  style="background-color:transparent" />
		            					</div>
		            				</div>
		            				<div class="form-group row">
		            					<div class="col-12">
		            						<label for="cLugarRealizaObra"> Lugar Donde se Realizar&aacute; la Obra:</label>
		            						<textarea id="cLugarRealizaObra" name="cLugarRealizaObra" rows="3"  cols="50" class="form-control"></textarea>
		            					</div>
		            				</div>
		            				<div class="form-group row">
		            					<div class="col-12">
		            						<label for="cdescripcion"> Descripci&oacute;n de la Obra:</label>
		            						<textarea id="cdescripcion" name="cdescripcion" rows="3"  cols="50" class="form-control"></textarea>
		            					</div>
		            				</div>
		            				<div class="form-group row">
		            					<div class="col-2">
		            						<label for="fAdjudicacion"> Fecha de Captura:</label>
		            						<input type="text" name="fAdjudicacion" id="fAdjudicacion"  style="background-color: transparent" class="form-control"/>
		            					</div>
		            					<div class="col-2">
		            						<label for="fFirmaContrato"> Fecha de Aplicaci&oacute;n:</label>
		            						<input type="text" name="fFirmaContrato" id="fFirmaContrato"  style="background-color: transparent" class="form-control"/>
		            					</div>
		            					<div class="col-2">
		            						<label for="ffechafincontr"> Fecha de Formalizaci&oacute;n:</label>
		            						<input type="text" name="ffechaAdjudicacion" id="ffechaAdjudicacion"  style="background-color: transparent" class="form-control"/>
		            					</div>
		            					<div class="col-2">
		            						<label for="ffechainicontr"> Fecha Inicio del Servicio:</label>
		            						<input type="text" name="ffechainicontr" id="ffechainicontr"  style="background-color: transparent" class="form-control"/>
		            					</div>
		            					<div class="col-2">
		            						<label for="ffechafincontr"> Fecha Fin del Servicio:</label>
		            						<input type="text" name="ffechafincontr" id="ffechafincontr"  style="background-color: transparent" class="form-control"/>
		            					</div>
		            					
		            				</div>
		            				<div class="form-group row">
		            					<div class="col-3">
		            						<label for="nmonto"> Importe Bruto:</label>
		            						<input type="text"  class="form-control montos" name="nmonto" id="nmonto"  style=" background-color:transparent" />
		            					</div>
		            					<div class="col-3">
		            						<label for="nmontoconiva"> %IVA:</label>
		            						<input type="text"  name="nporceiva" id="nporceiva"  style="background-color:transparent" class="form-control" />
		            					</div>
		            					<div class="col-3">
		            						<label for="ffechainicontr"> Monto IVA:</label>
		            						<input type="text"  class="form-control montos" name="nmontoconiva" id="nmontoconiva"  style="background-color:transparent" />
		            					</div>
		            					<div class="col-3">
		            						<label for="ffechafincontr"> Monto Total:</label>
		            						<input type="text"  class="form-control montos" name="montoTotal" id="montoTotal"  style=" background-color:transparent" />
		            					</div>
		            				</div>
		            			</div>
<!-- 		            			Fin del tab información general -->
		            			<div class="tab-pane" id="tabs-1" role="tabpanel">
		            				<div class="form-group row">
		            					<div class="col">
		            						<h5 style="color: #1A69A9;">Detalle de Claves Presupuestarias</h5>
		            					</div>
		            				</div>
									<div class="form-group row">
										<div class="col">
											<table id="dt_total" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
												<thead >
													<tr> 
									        		    <th>Mes</th>
														<th>EP</th>
														<th>Total</th>
									        		</tr>										
												</thead>
											</table>
										</div>
									</div>
		            			</div>
<!-- 		            			Fin del tab Detealle -->
								<div class="tab-pane" id="tabs-2" role="tabpanel">
		            				<div class="form-group row">
		            					<div class="col">
		            						<h5 style="color: #1A69A9;">Anticipos</h5>
		            					</div>
		            				</div>
		            				<div class="form-group row">
		            					<div class="col">
		            						<label id="lblNoFolioAutAnticpoMayor" style="visibility: hidden"> Oficio de Autorizacion: </label> 
		            						<input type="text" id="noFolioAutAnticpoMayor" name="noFolioAutAnticpoMayor" value="" style="visibility: hidden;" />
		            					</div>
		            				</div>
		            				<div class="form-group row">
										<div class="col">
											<table id="dt_anticipos" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
												<thead >
													<tr> 
									        		   <th>% Anticipo</th>
														<th>Monto</th>
									        		</tr>										
												</thead>
												<tbody>
													<tr align="left">
														<td align="center">
															<input type="text"  name="nporceanticipo" id="nporceanticipo" value="0" style="text-align: right;" class="form-control">
														</td>
														<td align="center">
															<input type="text"  name="nmontoanticipo" id="nmontoanticipo" value="0"  style="text-align: right;" class="form-control montos">
														</td>
													</tr>
												</tbody>
											</table>
										</div>
									</div>
		            			</div>
<!-- 		            			Fin del tab Anticipos -->
								<div class="tab-pane" id="tabs-3" role="tabpanel">
		            				<div class="form-group" id="convModifInfoTbl">
		            					<div class="row">
			            					<div class="col">
			            						<label for="cNoConvenio">No Convenio:</label>
		            							<input type="text" class="form-control" id="cNoConvenio" name="cNoConvenio" style="background-color: transparent" readonly="readonly" />
			            					</div>
			            				</div>
		            					<div class="row">
			            					<div class="col-3">
			            						<label for="mConv">Monto Nuevo:</label>
		            							<input type="text" class="form-control" id="mConv" name="mConv" style="background-color: transparent" readonly="readonly" />
			            					</div>
			            					<div class="col-3">
			            						<label for="ivaConvF">IVA:</label>
		            							<input type="text" class="form-control" id="ivaConvF" name="ivaConvF" style="background-color: transparent" readonly="readonly" />
			            					</div>
			            					<div class="col-3">
			            						<label for="mImporteIVAConv">Monto IVA:</label>
		            							<input type="text" class="form-control" name="mImporteIVAConv" id="mImporteIVAConv" style="background-color: transparent" readonly="readonly" />
			            					</div>
			            					<div class="col-3">
			            						<label for="mTotalConv">Monto Total:</label>
		            							<input type="text" class="form-control"  id="mTotalConv" name="mTotalConv" style="background-color: transparent" readonly="readonly" />
			            					</div>
			            				</div>
			            				<div class="row">
			            					<div class="col-3">
			            						<label for="mTotalConv">Inicio:</label>
		            							<input type="text" class="form-control" id="fInicioConv" name="fInicioConv" style="background-color: transparent" readonly="readonly" >
			            					</div>
			            					<div class="col-3">
			            						<label for="mTotalConv">Fin:</label>
		            							<input type="text" class="form-control" id="fFinConv" name="fFinConv" style="background-color: transparent" readonly="readonly" >
			            					</div>
			            				</div>
			            				<div class="row">
			            					<div class="col">
			            						<label for="cNoConvenio">Motivo del Convenio Modificatorio:</label>
		            							<textarea class="form-control" id="cMotivoConv" name="cMotivoConv" style="background-color: transparent" readonly="readonly" rows="4" ></textarea>
			            					</div>
			            				</div>
		            				</div>
		            				<div class="form-group row">
		            					<div class="col">
		            						
		            					</div>
		            				</div>
		            			</div>
<!-- 		            			Fin del tab Convenio Modificatorio -->
								<div class="tab-pane" id="tabs-4" role="tabpanel">
		            				<div class="form-group">
		            					<div class="row">
		            						<div class="col">
		            							<h5 style="color: #1A69A9;">Detalle de Claves Presupuestarias Modificadas</h5>
		            						</div>
		            					</div>
		            					<div class="row">
											<div class="col">
												<table id="dt_total_modif" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
													<thead >
														<tr> 
										        		    <th>Mes</th>
															<th>EP</th>
															<th>Total</th>
										        		</tr>										
													</thead>
												</table>
											</div>
										</div>
		            				</div>
		            			</div>
<!-- 		            			Fin del tab Detalle Convenio -->
								<div class="tab-pane" id="tabs-5" role="tabpanel">
		            				<div class="form-group">
		            					<div class="row">
		            						<div class="col">
		            							<h5 style="color: #1A69A9;">Amortizaciones y Retenciones</h5>
		            						</div>
		            					</div>
		            				</div>
		            				<div class="form-group" id="divAmortizacion">
		            					<div class="row">
		            						<div class="col" style="font-weight: bold; text-align: center;background-color: #EBEBEB;"> 
		            							<label>Amortizaci&oacute;n y Anticipo</label>
		            						</div>
		            					</div>
		            					<div class="row">
		            						<div class="col-2">
		            							<label>%Amortizaci&oacute;n:</label>
		            						</div>
		            						<div class="col-2">
		            							<input type="text" class="form-control monto" id="porcRetencion" name="porcRetencion">
		            						</div>
		            						<div class="col-2">
		            							<label>%Anticipo:</label>
		            						</div>
		            						<div class="col-2">
		            							<input type="text" id="porcAnticipoShow" name="porcAnticipoShow" class="form-control monto"/>
		            						</div>
		            					</div>
		            					<div class="row">
		            						<div class="col-2">
		            							<label>Importe Bruto Anticipo:</label>
		            						</div>
		            						<div class="col-2">
		            							<input type="text" id="impBrutoShow" name="impBrutoShow" class="form-control monto" />
		            						</div>
		            						<div class="col-2">
		            							<label>IVA Anticipo:</label>
		            						</div>
		            						<div class="col-2">
		            							<input type="text" id="ivaAnticipoShow" name="ivaAnticipoShow" class="form-control monto"/>
		            						</div>
		            						<div class="col-2">
		            							<label>Total Anticipo:</label>
		            						</div>
		            						<div class="col-2">
		            							<input type="text" id="totalAnticipoShow" name="totalAnticipoShow" class="form-control monto" />
		            						</div>
		            					</div>
		            				</div>
		            				<div class="form-group">
		            					<div class="row">
		            						<div class="col" style="font-weight: bold; text-align: center;background-color: #EBEBEB;">
		            							<label>Retenciones</label>
		            						</div>
		            					</div>
		            					<div class="row">
		            						<div class="col-1">
		            							<label>Retenci&oacute;n:</label>
		            						</div>
		            						<div class="col-4">
		            							<select id="cIdTipoRetencion" name="cIdTipoRetencion" class="custom-select"></select>
		            						</div>
		            					</div>
		            					<div class="row" id="divMilla2">
		            						<div class="col-auto" id="opciones2PC">
								  				<div class="form-check  form-check-inline">
												  <input class="form-check-input" type="radio" name="grpMilla2" id="IMDT"  value="0" checked="checked">
												  <label class="form-check-label" for="IMDT">
												    IMDT
												  </label>
												  
												</div>
												<div class="form-check  form-check-inline">
												  <input class="form-check-input" type="radio" name="grpMilla2" id="CNIC"  value="1">
												  <label class="form-check-label" for="CNIC">
												    CNIC
												  </label>
												</div>
											</div>
		            					</div>
		            					<div class="row">
											<div class="col">
												<table id="dt_retencion" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
													<thead >
														<tr> 
										        		   	<th>Clave de retenci&oacute;n</th>
															<th>Tipo de retenci&oacute;n</th>
										        		</tr>										
													</thead>
												</table>
											</div>
										</div>
		            				</div>
		            			</div>
<!-- 		            			Fin del tab Retencion/amortización -->
								<div class="tab-pane" id="tabs-6" role="tabpanel">
									<div class="form-group row">
										<div class="col">
											<h5 style="color: #1A69A9;">Detalle de Claves Presupuestarias Modificadas</h5>
										</div>
									</div>
		            				<div class="form-group row">
		            					<div class="col">
											<table id="dt_pago_pasivo" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
												<thead >
													<tr> 
									        		   	<th>Mes</th>
														<th>EP</th>
														<th>Total</th>
									        		</tr>										
												</thead>
											</table>
										</div>
		            				</div>
		            			</div>
<!-- 		            			Fin del tab Detallle pago pasivo -->
								<div class="tab-pane" id="tabs-7" role="tabpanel">
		            				<div class="form-group row">
										<div class="col">
											<h5 style="color: #1A69A9;">Plurianualidad</h5>
										</div>
									</div>
		            				<div class="form-group row">
		            					<div class="col">
											<table id="dt_plurianual" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
												<thead >
													<tr> 
									        		   	<th>Contrato</th>
														<th>A&ntilde;os Contratados</th>
														<th>Total</th>
									        		</tr>										
												</thead>
											</table>
										</div>
		            				</div>
		            			</div>
<!-- 		            			Fin del tab Detallle plurianual -->
		            		</div>
		            	</div>
					</div>
				</div>
				
<!-- 			Fin	Sección de tabs -->
			</fieldset>
		</div>
<!-- 		Fin del div container -->		
<!-- Modal Aplicación contable-->
		<div class="modal fade" id="dialog-form" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="modalApCont">Aplicaci&oacute;n Presupuestal/Contable</h5>
					</div>
					<div class="modal-body">
						<div class="form-group" id="divAplica">
							<iframe id="ifAplica" src="about:blank"></iframe>
						</div>
					</div>
					<div class="modal-footer">
						
					</div>
				</div>
			</div>
		</div>
<!-- Modal valida factura-->
		<div class="modal fade" id="dialog-validaFact" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" title="Carga de factura global de obra">
	  		<div class="modal-dialog">
	    		<div class="modal-content">
	    			<div class="modal-header">
	        			<h5 class="modal-title" id="modalValidaFact">Carga de Factura Global </h5>
	        			<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	      			</div>
					<div class="modal-body">
						<div id="uploadFacturasDiv" class="form-group row">
							<iframe id="uploadFacturasFrm" src="UploadCFDIContrato.jsp?TipoContrato=OB&IDContrato=&RFC=" height="360" width="510">
							</iframe>
						</div>
						<div id="facturasCapturadasDiv" class="form-group">
							<div class="row" id="uploadFacturasTR">
								<div class="col">
									<a href="#" onclick="togleDivFacts(1);return false;" class="nav-link">Cargar Facturas</a>
								</div>
							</div>
							<div class="row">
								<div class="col">
									<fieldset>
										<legend>Facturas Capturadas.</legend>
										<div class="form-group row">
											<div class="col-auto">
												<table id="grdValidaFacturas" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
													<thead >
														<tr> 
										        		    <th>Factura</th>
															<th>Importe Bruto</th>
															<th>Impuestos</th>
															<th>Total</th>
										        		</tr>										
													</thead>
												</table>
											</div>
										</div>
										<div class="form-group row">
											<div class="col-3">
												<label for="mTotalFacturaV" class="form-label">Total de las facturas:</label>
											</div>
											<div class="col-3">
												<input type="text" name="mTotalFacturaV" id="mTotalFacturaV" value="0.00" readonly class="form-control montos"/>
											</div>
										</div>
									</fieldset>
								</div>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="btnCancelValidaFact" name="btnCancelValidaFact">Cerrar</button>
					</div>
	    		</div>
	    	</div>
	    </div>
		<input type="hidden" id="TipoContrato" name="TipoContrato" value="OB" />
		<input type="hidden" id="nFolioOPConvHeader" name="nFolioOPConvHeader" value="" />
		<input type="hidden" id="nfoliooppagpasheader" name="nfoliooppagpasheader" value="" />
		<input type="hidden" id="nfolioopplurianualheader" name="nfolioopplurianualheader" value="" />
		<input type="hidden" id="cIdTipoRetencionVal" name="cIdTipoRetencionVal" value="" />
		<input type="hidden" id="opc2PC" name="opc2PC" value="" />
		<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>" />
		<input type="hidden" id="cEjercicio" name="cEjercicio">
		<input type="hidden" id="nFolioSAI" name="nFolioSAI" value="<%=c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 )%>">
		<input type="hidden" id="docAplicadoConv" name="docAplicadoConv" />
		<!-- SASV Convenio Modificatorio -->
		<input type="hidden" id="cEjercicioTbl" name="cEjercicioTbl" />
		<input type="hidden" id="cIdContratoTbl" name="cIdContratoTbl" />
		<input type="hidden" id="cTContratoTbl" name="cTContratoTbl" value="OB" />
		<input type="hidden" id="nIdClaveEgresos" name="nIdClaveEgresos" />
		<input type="hidden" id="cIdEntidadContableTbl" name="cIdEntidadContableTbl" />
		<input type="hidden" id="mtotal2" name="mtotal2" />
		<input type="hidden" id="cIdEntidadContable" name="cIdEntidadContable" />
		<input type="hidden" id="cEsRadicado" name="cEsRadicado" value="">

		<input type="hidden" name="FOLIO" type="hidden" id="FOLIO" value="<%=folio%>" />
		<input type="hidden" name="FolioSAI" type="hidden" id="FolioSAI" value="<%=folio%>" />
		<input type="hidden" name="iStatus" type="hidden" id="iStatus" value="3" />
		<input type="hidden" name="ctipocontrato" id="ctipocontrato" value="OB" />
		<input type="hidden" name="today" id="today" value="<%=today%>" />
		<input type="hidden" name="nFolioOPComHeader" id="nFolioOPComHeader" />
		<input type="hidden" name="iStatus" id="iStatus" />
		<input type="hidden" name="fechaAut" id="fechaAut" />
		<input type="hidden" name="mMontoPlurianual" id="mMontoPlurianual" value="0" />
		<input type="hidden" name="iEsPluriAnual" id="iEsPluriAnual" />
	</form>
</body>
</html>