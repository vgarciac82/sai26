<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.syc.obrapublica.EjercicioFiscal"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>

<%
String fatalError = "";
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

int estado = 0;
Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
EjercicioFiscal aEjercicioFiscal = efbl.getEjercicioFiscalActivo();
if (usuario == null) {
	response.sendRedirect("../index.jsp");
	return;
}

String centroContable = (usuario.getPropiedad("CCENTROCONTABLE") != null
? usuario.getPropiedad("CCENTROCONTABLE").getValor()
: "");
if (StringUtils.isBlank(centroContable))
fatalError = "Error Fatal: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable. Consulte a su administrador.";

String unidadEjecutora = usuario.getU_UR();
if (StringUtils.isBlank(unidadEjecutora))
fatalError = "Error Fatal: El Usuario no tiene Unidad Ejecutora asignada y no podra realizar aplicacion Contable. Consulte a su administrador.";

String login = usuario.getLogin();
String operador = usuario.getNombre();
String ramo = usuario.getU_Ramo();
String fechaAplicacion = Util.calculaFechaAplicacion(aEjercicioFiscal);
String ejercicioFiscal = aEjercicioFiscal.getaEjercicioFiscal();
String numeroEmpleado = usuario.getNumeroEmpleado();
String urUsuario = usuario.getU_UR();
int nFolioTramite;

if (c != null) {
	nFolioTramite = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
} else {
	nFolioTramite = Integer.parseInt(   request.getParameter("nFolioTramite") );
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	c = cbl.findByFolioLike( "VIATICOS", String.valueOf( nFolioTramite ) );
}
	
%>


<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Comisiones de Viáticos</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/css/bootstrap-datetimepicker.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"  href="../plantillasCasos/ComponentesPago/CSS/EgresoFirmantes.css"></link>
<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>

</head>

<body id="dt_example">
<form id="formCViaticos" name="formCViaticos" class="needs-validation" >
	<div class="form-group mx-auto">
		<input type="hidden" name="operador" 		id="operador" 		value=<%=operador%>/>
		<input type="hidden" name="login" 			id="login" 			value=<%=login%>/>
		<input type="hidden" name="fechaCaptura" 	id="fechaCaptura" 	value=""/>
		<input type="hidden" name="fechaAplicacion" id="fechaAplicacion" value="<%=fechaAplicacion%>"/>
		<input type="hidden" name="ramo" 			id="ramo" 			value=""/>
		<input type="hidden" name="ejercicioFiscal" id="ejercicioFiscal" value="<%=aEjercicioFiscal%>"/>
		<input type="hidden" name="folio" 			id="folio" 			value=""/>
		<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value=""/>
		<input type="hidden" name="nIdEstatus" 		id="nIdEstatus" 	value=""/>
		<input type="hidden" name="operacion" 		id="operacion" 		value=""/>
		<input type="hidden" name="tieneAnticipo" 	id="tieneAnticipo" />
		<input type="hidden" name="tipoEvento" 		id="tipoEvento" 	value="A"/>
		<input type="hidden" name="sumTransporteC" 	id="sumTransporteC" />
		<input type="hidden" name="sumAgendaC" 		id="sumAgendaC" />
		<input type="hidden" name="sumTransporteAnticipo" id="sumTransporteAnticipo" />
		<input type="hidden" name="sumAgendaAnticipo" id="sumAgendaAnticipo" />
		<input type="hidden" name="cuantosBoletos" 	id="cuantosBoletos" />
		<input type="hidden" name="importeAuxNeto" 	id="importeAuxNeto" />
		<input type="hidden" name="rgExiste" 		id="rgExiste" />
		<input type="hidden" name="cIdRFC_RelacionGasto" id="cIdRFC_RelacionGasto" />
		<input type="hidden" name="tieneFacturas" 	id="tieneFacturas" />
		<input type="hidden" name="idAgenda" 		id="idAgenda" />
		<input type="hidden" name="correo" 			id="correo" />
		<input type="hidden" name="nCorreo" 		id="nCorreo" />
		<input type="hidden" name="paternoCorreo" 	id="paternoCorreo" />
		<input type="hidden" name="maternoCorreo" 	id="maternoCorreo" />
		<input type="hidden" name="cCargo" 			id="cCargo" />
		<input type="hidden" name="tieneRetenXML" 	id="tieneRetenXML" />
		<input type="hidden" name="retencionesCapturadas" id="retencionesCapturadas" />
		<input type="hidden" name="tieneJustCapturada" id="tieneJustCapturada" />
		<input type="hidden" name="esResico" 		id="esResico" />
		<input type="hidden" name="listaSol" 		id="listaSol" />
		<input type="hidden" name="msgRF" 	 		id="msgRF" />
		<input type="hidden" name="dateTemp" 		id="dateTemp" />
		<input type="hidden" name="cTipoPago" 		id="cTipoPago" 	value="VIATICOS"/>
		<input type="hidden" name="cTipoFirmante" 	 id="cTipoFirmante" />
		<input type="hidden" name="cIdTipoRetencion" id="cIdTipoRetencion" />
		<input type="hidden" name="cIdRetencion" 	id="cIdRetencion" />
		<input type="hidden" name="permiteAnticipos" id="permiteAnticipos" />
		<input type="hidden" name="nPorcRetencionIva" id="nPorcRetencionIva" />
		<input type="hidden" name="nPorcRetencion" 	id="nPorcRetencion" />
		<input type="hidden" name="nPorcRetencionOtras" id="nPorcRetencionOtras" />
		<input type="hidden" name="nTipoRetencion" 	id="nTipoRetencion" />
		<input type="hidden" name="sumaRetenciones" id="sumaRetenciones" />
		<input type="hidden" name="tipoRetIVA" 		id="tipoRetIVA" />
		<input type="hidden" name="tipoRetISR" 		id="tipoRetISR" />
		<input type="hidden" name="tieneBoletos" 	id="tieneBoletos" />	
		<input type="hidden" name="nEmpleadoElabora" id="nEmpleadoElabora" value="<%=numeroEmpleado%>"/>
		<input type="hidden" name="totalViaticos" 	id="totalViaticos" />
		<input type="hidden" name="sumTransporte" 	id="sumTransporte" />
		<input type="hidden" name="sumPasajeLocal" 	id="sumPasajeLocal" />
		<input type="hidden" name="sumTaxiLocal" 	id="sumTaxiLocal" />
		<input type="hidden" name="sumGasolinaLocal" id="sumGasolinaLocal" />
		<input type="hidden" name="sumPeajeLocal" 	id="sumPeajeLocal" />
		<input type="hidden" name="sumMaritimoLocal" id="sumMaritimoLocal" />
		<input type="hidden" name="sumAereoLocal" 	id="sumAereoLocal" />
		<input type="hidden" name="tienePagos" 		id="tienePagos" />
		<input type="hidden" name="totFacturas" 	id="totFacturas" />
		<input type="hidden" name="boletosExistentes" id="boletosExistentes" />
		<input type="hidden" name="otroTipoCambio" 	id="otroTipoCambio" />
		<input type="hidden" name="mImporteTipoCambio" id="mImporteTipoCambio" />
		<input type="hidden" name="tieneVariosAnt" 	id="tieneVariosAnt" />
		<input type="hidden" name="nFolioCaja" 		id="nFolioCaja" />
		<input type="hidden" name="importeAnticipo" id="importeAnticipo" />
		<input type="hidden" name="nComision" 		id="nComision" />
		<input type="hidden" name="noEmpleado" 		id="noEmpleado" />
		<input type="hidden" name="cDocHaplicado" 	id="cDocHaplicado" />
		<input type="hidden" name="tieneComSinViaticos" 	id="tieneComSinViaticos" />
		<input type="hidden" name="cNotas" 			id="cNotas" />
		<input type="hidden" name="tipoPago" 		id="tipoPago" />
		<input type="hidden" name="mMontoComision" 	id="mMontoComision" />
		<input type="hidden" name="solNoAp" 		id="solNoAp" />
		<input type="hidden" name="solNoEnviadas" 	id="solNoEnviadas" />
		<input type="hidden" name="mDevolucion" 	id="mDevolucion" />
		<input type="hidden" name="cTieneReintegroContable" 	id="cTieneReintegroContable" />
		<input type="hidden" name="mTotalCaja" 		id="mTotalCaja" />
		<input type="hidden" name="mImporteAnticipoViat" 	id="mImporteAnticipoViat" />
		<input type="hidden" name="cantMeses" 		id="cantMeses" />
		<input type="hidden" name="necesitaJustificar" 	id="necesitaJustificar" />
		<input type="hidden" name="nombreComision" 	id="nombreComision"  />
		<input type="hidden" name="cJustGasolina" 	id="cJustGasolina"  />
		<input type="hidden" name="tieneEpAgregada" id="tieneEpAgregada" />
		<div id="container" class="container-fluid">
			<div id="divDatosEmpleado">
				<div class="row">	
					<p class="h5"> Comprueba Comisión </p>
					<div class="col-md-6">
						<div class= "card">
							<div class="card-body"> 
								<div class="row">
									<div class="col-md-auto">
										RFC Empleado					    
								        <div class="input-group">
								        	<span class="input-group-text"><i class="bi bi-person-lines-fill"></i></span>
								        	<input class="form-control"  type="text" maxlength="15" size="13" name="cRFC" id="cRFC" readonly />
								        </div>
							    	</div>
								    <div class="col-md-6">
								    	Nombre
								        <div class="input-group">
									       	<span class="input-group-text"><i class="bi bi-person-fill"></i></span>
									      	<input type="text" class="form-control" id="nombre" name="nombre" size="15" readonly/>
									    </div>
								    </div>
								    <div class="col-md-auto">
										Comisión
										<div class="input-group">
											<input type="text" class="form-control" id="nidComision" name="nidComision" size="5" value="" readonly/>
										</div>
									</div>
								</div>
							</div>
							<div class="row" id="modificacion">
									<div class="col-md-5 col-sm-12">
										<span id="btnModifica" ><a href="#" onclick="abrirDlgModifica();" class="link-primary">Modifica Nombre de la Comisión</a></span>
									</div>
							</div>
						</div>
					</div>
					
					<div class="col-md-6">
						<div class= "card">
							<div class="card-body"> 
								<div class="row">
				              				<div class="col-md-3">
				              					T. Agenda 
				              					<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-wallet2"></i></span>
					              					<input type="text" class="form-control col-2" id="mAgenda" name="mAgenda" size="10" readonly/>
					              				</div>
					              			</div>
											<div class="col-md-3">
												T. Transporte 
												<div class="input-group">
											    	<span class="input-group-text"><i class="bi bi-truck"></i></span>
													<input type="text" class="form-control col-2" id="mTransporte" name="mTransporte" size="10" readonly/>
												</div>
											</div>
											<div class="col-md-3">
												Total
												<div class="input-group">
										    		<span class="input-group-text"><i class="bi bi-plus-square"></i></span>
													<input type="text" class="form-control col-2" id="mTotal" name="mTotal" size="10" readonly/>
												</div>
											</div>
											<div class="col-md-3">
												Saldo 
												<div class="input-group">
										    		<span class="input-group-text"><i class="bi bi-piggy-bank"></i></span>
													<input type="text" class="form-control col-2" id="mSaldo" name="mSaldo" size="10" readonly/>
												</div>
											</div>
								</div>
								<div class="row" id="leyendaTipoCambio">
									<div class="col-md-5">
										<span id="btnTipoCambio" ><a href="#" onclick="abrirDlgTipoCambio();" class="link-primary">Actualizar el Tipo de Cambio</a></span>
									</div>
								</div>
				            </div>
				       	
						</div> <!-- Termina Card empleado -->
				  	</div>
				</div><!-- Termina row -->
			</div>
			
			<div class="row">
				<p class="h6">Solicitudes</p>
					<div class="table-responsive text-nowrap">
						<table id="tablaSolicitud" class="table table-striped table-bordered">
							<thead>
							    <tr>
							      <th>#</th>
							      <th>Folio</th>
							      <th>T.Pago</th>
							      <th>Evento</th>
							      <th>Viaticos</th>
							      <th>Local</th>
							      <th>Total</th>
							      <th>Anticipo</th>
							      <th>Comprobación</th>
							      <th>Estatus</th>
							      <th>Adj</th>
							      <th>Observaciones</th>
							    </tr>
							</thead>
						</table>
					</div>
			</div>
			
			<div class ="row">
					<div class="col-auto">
						<br>
						<input type="button" id="nuevoBtn" value="Sol. Anticipos - Devoluciones" class="btn btn-primary" />
					</div>
					<div class="col-auto"><br>
						<input type="button" id="nuevoRGBtn" value="Comprobacion - Devengados" class="btn btn-secondary" />
					</div>
					<div class="col-auto"><br>
						<input type="button" id="nuevoCVBtn" value="Comisión sin Viaticos" class="btn btn-dark" />
					</div>
					<div class="col-auto"><br>
						<input type="button" id="btnFinalizar" value="Finalizar trámite" class="btn btn-primary" />
					</div>							
					<div class="col-auto"><br>
						<input type="button" id="btnDesfinalizar" value="Activar trámite" class="btn btn-secondary" />
					</div>
					<div class="col-auto">
						Total Anticipo
						<div class="input-group">
				    		<span class="input-group-text"><i class="bi bi-coin"></i></span>
							<input type="text" class="form-control col-2" id="sumTotal" name="sumTotal" size="10" readonly/>
						</div>
					</div>
					<div class="col-auto">
            				Total Comprobacion
            				<div class="input-group">
			    				<span class="input-group-text"><i class="bi bi-card-checklist"></i></span>
             					<input type="text" class="form-control col-2" id="sumComprueba" name="sumComprueba" size="10" readonly/>
             				</div>
             		</div>
					<div class="col-auto">
							Total por Comprobar
							<div class="input-group">
						    	<span class="input-group-text"><i class="bi bi-check-circle"></i></span>
								<input type="text" class="form-control col-2" id="sumPorComprobar" name="sumPorComprobar" size="10" readonly/>
							</div>
					</div>
			</div>
			<br>
			<div id="detalleDiv" class="detalleSol col-md-12" >
					<div id="detalleReemplazo" class="row">
						<div class="col-4">
							<div class="input-group">
								<input type="checkbox" id="chk_reemplazo" name="chk_reemplazo" value="0" class="form-check" />
								<label for="chk_reemplazo">  Es reemplazo: </label>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-2">
							Tipo Documento
							<div class="input-group">
				    			<span class="input-group-text"><i class="bi bi-tag"></i></span>
              					<input type="text" id="cDocumento" name ="cDocumento" class="form-control"/>
              				</div>
						</div>
						<div class="col-md-4">
							<label id="lblEvento">Evento</label>
							<div id="divEvento" class=" input-group">
              						<span class="input-group-text"><i class="bi bi-journal-check"></i></span>
	              					<select  id="cEvento" name ="cEvento" class="form-select" onChange="consultaTipoEvento();"></select>
	              			</div>
						</div>	
						<div class="col-md-2" id="divSelAnticipo">
							<label id="lblCaja">Folio Anticipo</label>
							<div class=" input-group">
              						<span class="input-group-text"><i class="bi bi-receipt"></i></span>
	              					<select  id="folioCaja" name ="folioCaja" class="form-select" onChange="consultaFoliosCaja();"></select>
	              			</div>
						</div>
						<div class="col-md-2">
							Folio Pago
							<div class="input-group">
				    			<span class="input-group-text"><i class="bi bi-arrow-right-circle"></i></span>
              					<input readonly type="text" class="form-control" name="folioPago" id="folioPago" value=""/>
              				</div>
							
						</div>						
						<div class="col-md-2">
							Fecha Aplicación
							<div class="input-group">
				    			<span class="input-group-text"><i class="bi bi-calendar"></i></span>
              					<input readonly type="text" class="form-control" name="fAplicacion" id="fAplicacion" value ="<%=fechaAplicacion%>" />
              				</div>
							
						</div>	
					</div>
					<div id="detalleComision" class="mt-2" >
						<div class="row">
							<div class="col-md-6">
								<div class="card">
								  	<h5 class="card-header">Detalle de los Viáticos</h5>
									<div class="card-body" id="cardBodyDetalleRG">
									  	<div class="form-group row">
										  	<label class="col-6 col-form-label" for="mPasaje"><b>Pasajes</b> ( 37201 / 37204 )</label>
										  	<div class="col-5">
											   	<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-bus-front"></i></span>
					              					<input type="text" class="form-control col-2 inputDetalle" id="mPasaje" name="mPasaje" value="0.00" onchange="sumaDetalle();validaDetalle(this);"/>
								              	</div>
							              	</div>
							            </div>
							            <div class="form-group row">
							              	<label class="col-6 col-form-label" for="mTaxi"><b>Taxi</b>  ( 37201 / 37204 )</label>
						              		<div class="col-5">
											   	<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-taxi-front"></i></span>
					              					<input type="text" class="form-control col-2 inputDetalle" id="mTaxi" name="mTaxi" value="0.00" onchange="sumaDetalle();validaDetalle(this);"/>
								              	</div>
							             	</div>
							            </div>
							            <div class="form-group row">
							              	<label class="col-6 col-form-label" for="mPeaje"><b>Combustible y peaje</b> ( 37201 / 37204 / 33602 )</label>
						              		<div class="col-5">
											   	<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
					              					<input type="text" class="form-control col-2 inputDetalle" id="mPeaje" name="mPeaje" value="0.00" onchange="sumaDetalle();validaDetalle(this);"/>
								              	</div>
								            </div>
							            </div>
							            <div class="form-group row">
							              	<label class="col-6 col-form-label" for="mHotel"><b>Factura(s) de Hotel </b>( 37501 / 37504 )</label>
							              	<div class="col-5">
											   	<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-building"></i></span>
					              					<input type="text" class="form-control col-2 inputDetalle" id="mHotel" name="mHotel" value="0.00" onchange="sumaDetalle();validaDetalle(this);"/>
								              	</div>
								            </div>
							            </div>
							            <div class="form-group row">
							              	<label class="col-6 col-form-label" for="mConsumos"><b>Consumos</b> ( 37501 / 37504 )</label>
							              	<div class="col-5">
											   	<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-cash"></i></span>
					              					<input type="text" class="form-control col-2 inputDetalle" id="mConsumos" name="mConsumos" value="0.00" onchange="sumaDetalle();validaDetalle(this);"/>
								              	</div>
								              </div>
							            </div>
							            <div class="form-group row">
							              	<label class="col-6 col-form-label" for="mOtros"><b>Otros </b>( 37901 )</label>
										   	<div class="col-5">
											   	<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-coin"></i></span>
					              					<input type="text" class="form-control col-2 inputDetalle" id="mOtros" name="mOtros" value="0.00" onchange="sumaDetalle();validaDetalle(this);"/>
								              	</div>
								              </div>
							            </div>
							            <hr>
							            <div class="form-group row">
							              	<label class="col-6 col-form-label" for="mTotal1"><b>Total </b></label>
							              	<div class="col-5">
											   	<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-plus-square"></i></span>
					              					<input type="text" class="form-control col-2 inputDetalle" id="mTotal1" name="mTotal1" value="0.00"/>
								              	</div>
								            </div>
							            </div>
							            
									</div>
									<div class="card-body" id="cardBodyDetalleCaja">
										<div class="form-group row">
							              	<label class="col-6 col-form-label" for="mTotal1"><b>Total </b></label>
							              	<div class="col-5">
											   	<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-plus-square"></i></span>
					              					<input type="text" class="form-control col-2 inputDetalle" id="mTotal2" name="mTotal2" value="0.00" onchange="validaDetalle(this);formatoTotal();"/>
								              	</div>
								            </div>
							            </div>
									</div>
								</div>
							</div>
							<div class="col-md-6">
								<div class="card">
								  	<h5 class="card-header">Detalle de Transporte Local</h5>
								  	<div class="card-body">
								    	<div class="form-group row">
										  	<label class="col-6 col-form-label" for="mPasajeLocal"><b>Pasajes </b>	( 37201 / 37204 )</label>
										  	<div class="col-5">
											   	<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-bus-front"></i></span>
					              					<input type="text" class="form-control col-2 inputDetalle" id="mPasajeLocal" name="mPasajeLocal" value="0.00" onchange="sumaTransporte();validaDetalleTrans(this);"/>
								              	</div>
								             </div>
							            </div>
							            <div class="form-group row">
							              	<label class="col-6 col-form-label" for="mTaxiLocal"><b>Taxi </b>	( 37201 / 37204 )</label>
							              	<div class="col-5">
											   	<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-taxi-front"></i></span>
					              					<input type="text" class="form-control col-2 inputDetalle" id="mTaxiLocal" name="mTaxiLocal" value="0.00" onchange="sumaTransporte();validaDetalleTrans(this);"/>
								              	</div>
								            </div>
							            </div>
							            <div class="form-group row">
							              	<label class="col-6 col-form-label" for="mPeajeLocal"><b>Peaje y Estacionamiento </b> ( 37201 / 37204 / 33602 )</label>
										   	<div class="col-5">
											   	<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
					              					<input type="text" class="form-control col-2 inputDetalle" id="mPeajeLocal" name="mPeajeLocal" value="0.00" onchange="sumaTransporte();validaDetalleTrans(this);"/>
								              	</div>
								            </div>
							            </div>
							             <div class="form-group row">
							              	<label class="col-6 col-form-label" for="mGasolinaLocal"><b>Combustible</b> ( 37201 / 37204 )</label>
										   	<div class="col-5">
											   	<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
					              					<input type="text" class="form-control col-2 inputDetalle" id="mGasolinaLocal" name="mGasolinaLocal" value="0.00" onchange="sumaTransporte();validaDetalleTrans(this);"/>
								              	</div>
								            </div>
							            </div>
							             <div class="form-group row">
							              	<label class="col-6 col-form-label" for="mMaritimoLocal"><b>Maritimos</b> ( 37301 / 37303  )</label>
										   	<div class="col-5">
											   	<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-tsunami"></i></span>
					              					<input type="text" class="form-control col-2 inputDetalle" id="mMaritimoLocal" name="mMaritimoLocal" value="0.00" onchange="sumaTransporte();validaDetalleTrans(this);"/>
								              	</div>
								            </div>
							            </div>
							             <div class="form-group row">
							              	<label class="col-6 col-form-label" for="mAereoLocal"><b>Pasajes Aereos</b> ( 37101 / 37104 )</label>
										   	<div class="col-5">
											   	<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-airplane"></i></span>
					              					<input type="text" class="form-control col-2 inputDetalle" id="mAereoLocal" name="mAereoLocal" value="0.00" onchange="sumaTransporte();validaDetalleTrans(this);"/>
								              	</div>
								            </div>
							            </div>
										<hr>
							            <div class="form-group row">
							              	<label class="col-6 col-form-label" for="mTotalLocal"><b>Total</b> </label>
											<div class="col-5">
											   	<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-plus-square"></i></span>
					              					<input type="text" class="form-control inputDetalle" id="mTotalLocal" name="mTotalLocal" value="0.00"/>
								              	</div>
								            </div>
							            </div>
							            <div class="row" id="linkGasolina">
											<div class="col-md-5 col-sm-12">
												<span id="btnGasolina" ><a href="#" onclick="capturarJustificacionGasolina()" class="link-primary">Captura justificación Gasolina</a></span>
											</div>
										</div>
									</div>
								</div>
							</div>							
					</div>
					<div class= "card mt-1">
						<div class="card-body">
							<div class="row">
								<div class="col-12  d-flex justify-content-sm-center">
						              	<div class="col-auto">
										   	<div class="input-group">
								    			<span class="input-group-text btn-secondary">Total Viáticos + Transporte Local</span>
				              					<input type="text" class="form-control col-2" id="mTotalGral" name="mTotalGral" value="0.00" readonly/>
							              	</div>
							            </div>
						        </div>
						    </div>
						</div>
					</div>
				</div>
			</div> <!-- Fin detalleDiv -->
	
	<div class="row mt-2" id="divCardBoletos">
		<div class="col-md-12">
			<div class="card">
			        <div class="card-header">
				        <ul class="nav nav-tabs" id="rg-list" role="tablist">
				            <li class="nav-item">
				              <a class="nav-link active" data-toggle="tab" href="#boletosRG" role="tab" aria-controls="boletosRG" aria-selected="true">Boletos</a>
				            </li>
				            <li class="nav-item">
				              <a class="nav-link" data-toggle="tab" href="#detalleRG" role="tab" aria-controls="detalleRG" aria-selected="false" >Facturas</a>
				            </li>
				            <li class="nav-item">
				              <a class="nav-link" data-toggle="tab" href="#resumenRG" role="tab" aria-controls="resumenRG" aria-selected="false">Movimientos</a>
				            </li>
				        </ul>
			        </div>
			    <div class="card-body">
			        <div class="tab-content mt-3">
			            	<div class="tab-pane active" id="boletosRG" role="tabpanel">
			            		<div id="divboletosRGAgenda" class="col-12">
			            			<div class="detalleBoletos">	
										<div class="row">
											<p class="h6">Boletos de Avión Disponibles   (click en el botón + para agregar los boletos a la solicitud)</p>
											<div class="table-responsive text-nowrap">
												<table id="tablaBoletos" class="table table-striped table-bordered">
													<thead>
													    <tr>
													      <th>#</th>
													      <th>No.Boleto</th>
													      <th>Importe</th>
													      <th>Partida</th>
													      <th>Ruta</th>
													      <th>F.Salida</th>
													      <th>F.Regreso</th>
													      <th>RFC</th>
													      <th>Nombre Boleto</th>
													    </tr>
													</thead>
												</table>
											</div>
										</div>
									</div>	
										<br>
									<div class="row">
										<div class="col-12">
											<p class="h6">Boletos a comprobar en esta solicitud</p>
											<div class="table-responsive text-nowrap">
												<table id="tablaAsignados"  class="table table-striped table-bordered">
													<thead>
													    <tr>
													      <th>No.Boleto</th>
													      <th>Importe</th>
													      <th>Partida</th>
													      <th>Ruta</th>
													      <th>RFC</th>
													      <th>Nombre Boleto</th>
													      <th>-</th>
													    </tr>
													</thead>
												</table>
											</div>
										</div>
									</div>	
			            		</div>
			            		<br>
			            		<div id="divTaxis" class="col-12">
										<div class="row">
											<p class="h6">Listado de Taxis que se agregaran a esta solicitud</p>
											<div class="table-responsive text-nowrap">
												<table id="tablaTaxis" class="table table-striped table-bordered">
													<thead>
													    <tr>
													      <th>Folio Taxi</th>
													      <th>Destino</th>
													      <th>Fecha</th>
													      <th>Monto</th>
													    </tr>
													</thead>
												</table>
											</div>
										</div>
			            		</div>
			            		</br>
			            		<div id="divComisionSV">
				             		<div class="row">
				             			<div class="col-md-12">
				             				Captura Informe de Comision
				             				<textarea name="informeComisionCSV" id="informeComisionCSV" rows="3" onkeypress="valFmt(this,15)" class="form-control" ></textarea>
				             			</div>
				             		</div>
				             	</div>
			            		<div class="row">
									<div id="btnAvanzar" class="col-md-1">
										<input type="button" id="avanzaSolBtn" value="Avanzar" class="btn btn-primary" />
									</div>				
									<div id="btnBorrar" class="col-md-1">
										<input type="button" id="btnBorrar3" value="Borrar Todo" class="btn btn-secondary" />
									</div>		
								</div>
			            	</div><!-- Fin div boletosRG -->
			             
			            <div class="tab-pane" id="detalleRG" role="tabpanel" aria-labelledby="detalleRG-tab">  
			             	<div id="divdetalleRGComprob">
			             		<div class="row">
			             			<div class="col-md-6">
			             				Captura Concepto de la Solicitud
			             				<textarea name="concepto" id="concepto" rows="3" onkeypress="validarCaracteres(event)" class="form-control" ></textarea>
			             			</div>
			             			<div class="col-md-6">
			             				Captura Informe de Comision
			             				<textarea name="informeComision" id="informeComision" rows="3" onkeypress="validarCaracteres(event)" class="form-control" ></textarea>
			             			</div>
			             		</div>
			             		<div class="row mt-2">
									<div class="col-md-2">
										<input type="button" id="cargaFacturasBtn" value="Cargar Facturas" class="btn btn-secondary" onclick="creaDiagloFacturas('true'); return false;"/>
									</div>
									<div class="col-md-2">
										<input type="button" value="Capturar correo" id="capturaCorreo" class="btn btn-secondary" onclick="abrirDialogCorreo()"/>
									</div>
								</div>
								<div class="row mt-2">
									<div class="col-md-12">
			             				Capturar Justificacion para esta solicitud
			             				<textarea name="cOtraJustificacion" id="cOtraJustificacion" rows="3" onkeypress="validarCaracteres(event)" class="form-control" maxlength="900"></textarea>
			             			</div>
								</div>
								<div class="card mt-2">
									<div class="card-body">
										<div class="row gy-2 gx-3 align-items-center">
											<div class="col-auto">
												<label id="lbl_noComprobable" >No Comprobable: </label>
												<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-cart4"></i></span>
					              					<input type="text" id="importenoComprobable" name="importenoComprobable" value="0.00" readonly class="form-control" />
					              				</div>
											</div>
											<div class="col-auto">
												<label id="lbl_ImporteBruto" >Importe Bruto: </label>
												<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
					              					<input type="text" id="mImporteSinIVA" name="mImporteSinIVA" onchange="cambiafrmt(this)" value="0.00" readonly class="form-control" />
					              				</div>
												
											</div>
											<div class="col-auto">
												<label id="lbl_ImporteBruto" >Importe Mas IVA: </label>
												<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
					              					<input type="text" id="mImporteBruto" name="mImporteBruto" onchange="cambiafrmt(this)" value="0.00" readonly class="form-control" />
					              				</div>
											</div>
											<div class="col-auto">
												<label id="lbl_ImporteRet" >Retencion: </label>
												<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
					              					<input type="text" id="mImporteRetencion" name="mImporteRetencion" onchange="cambiafrmt(this)" value="0.00" readonly class="form-control" />
					              				</div>
											</div>
											<div class="col-auto">
												<label id="lbl_ImporteMaximo" >Importe en Fact: </label>
												<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
					              					<input type="text" id="importeEdicionMaximo" name="importeEdicionMaximo" value="0.00" readonly  class="form-control" />
					              				</div>
											</div>
											<div class="col-auto">
												<label id="lbl_importeNeto" >Importe a pagar: </label>
												<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
					              					<input type="text" id="importeNeto" name="importeNeto" value="0.00" readonly  class="form-control" />
					              				</div>
											</div>
											
											<div class="col-auto">
												<label id="lbl_nuevoImporte" >Captura Importe: </label>
												<div class="input-group">
									    			<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
					              					<input type="text" id="importeEdicion" name="importeEdicion" value="0.00" onkeypress="return validar2(event);" onblur="validaimportemaximo();" class="form-control" disabled/>
					              				</div>
											</div>
											<div class="col-auto">
												<label id="lbl_EditaImporte" >Editar Importe: </label>
					              				<input type="checkbox" id="chk_EditaImporte" name="chk_EditaImporte" value="0" class="form-check" />
					              				
											</div>
										</div>
									</div>
								</div>
								<div class="row mt-2">
									<div class="col-md-4">
										<div class="input-group">
											<input type="checkbox" id="chk_tieneRete" name="chk_tieneRete" value="0" class="form-check" />
											<label id="lbl_tieneRetenciones" for="chk_tieneRete">  Tiene Retenciones diferentes a RESICO?: </label>
										</div>
									</div>
								</div>
								<div class="row mt-2" id="divRetenciones">
										<div class="col-12">
											<div class="card mt-2">
												<div class="card-header"> Retenciones
												</div>
												<div class="card-body">
													<div class="row gy-2 gx-3 align-items-center">
														<div class="col-2">
															<label id="lblRete">Tipo Retención</label>
															<select  id="cTipoRetencion" name ="cTipoRetencion" class="form-select" >
															</select>
														</div>
														<div class="col-auto">
															<label id="lblIva">Calculo IVA</label>
															<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-envelope"></i></span>
								              					<input type="text" id="mImporteIva" name="mImporteIva" value="0.00" readonly  class="form-control" />
								              				</div>
														</div>
														<div class="col-auto">
															<label id="lblISR">Calculo ISR</label>
															<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-envelope-paper"></i></span>
								              					<input type="text" id="mImporteISR" name="mImporteISR" value="0.00" readonly  class="form-control" />
								              				</div>
														</div>
														<div class="col-auto">
															<label id="lblIva">IVA Factura</label>
															<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
								              					<input type="text" id="mIvaFactura" name="mIvaFactura" value="0.00" readonly  class="form-control" />
								              				</div>
														</div>
														<div class="col-auto">
															<label id="lblISR">ISR Factura</label>
															<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
								              					<input type="text" id="mISRFactura" name="mISRFactura" value="0.00" readonly  class="form-control" />
								              				</div>
														</div>
													</div>
						             			</div>
						             		</div>
						             	</div>
										<div class="col-12" id="divOtrasRete">
											<div class="card mt-2">
												<div class="card-header"> Otras Retenciones
												</div>
												<div class="card-body">
													<div class="row gy-2 gx-3 align-items-center">
														<div class="col-3">
															<label id="lblOtrasRete">Tipo Otras Retención</label>
															<select  id="cOtraRetencion" name ="cOtraRetencion" class="form-select" >
															</select>
														</div>
														<div class="col-auto">
															<label id="lblOtras">Calculo Otras</label>
															<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-envelope"></i></span>
								              					<input type="text" id="mOtras" name="mOtras" value="0.00" readonly  class="form-control" />
								              				</div>
														</div>
														<div class="col-auto">
															<label id="lblFOtras">Factura Otras Reten</label>
															<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
								              					<input type="text" id="mFacturaOtros" name="mFacturaOtros" value="0.00" readonly  class="form-control" />
								              				</div>
															
														</div>
														
													</div>
						             			</div>
						             		</div>
						             	</div>
				             	</div>
			             	</div>

			             	<div class="row mt-2">
									<div id="btnAvanzar" class="col-md-1">
										<input type="button" id="avanzaFact" value="Avanzar" class="btn btn-primary" />
									</div>						
									<div id="btnBorrar" class="col-md-1">
										<input type="button" id="btnBorrar4" value="Borrar Todo" class="btn btn-secondary" />
									</div>						
							</div>
			             </div> <!-- Fin div detalleRG -->
			             
			            <div class="tab-pane" id="resumenRG" role="tabpanel" aria-labelledby="resumenRG-tab">
			             	<div id="divresumenRGAgenda">
			             		
			             		<jsp:include page="../plantillasCasos/ComponentesPago/CapturaMovimientosRG.jsp"></jsp:include>
			             		<div class="row">
									<div id="btnAvanzar" class="col-md-1">
										<input type="button" id="avanzaMov" value="Avanzar" class="btn btn-primary" />
									</div>	
									<div id="btnBorrar" class="col-md-1">
										<input type="button" id="btnBorrar2" value="Borrar Todo" class="btn btn-secondary" />
									</div>						
								</div>
			             	</div>
			            </div>
			        </div>
			    </div>
			</div>
		</div>
	</div>	
				<div id="firmantesDiv" class="mt-2">
					<div class= "card">
						<div class="card-body">	
							<h5> Seleccione los Firmantes </h5>
							<hr class="mt-3">
							
							<jsp:include page="../plantillasCasos/ComponentesPago/EgresoFirmantesBs.jsp"></jsp:include>
						</div>
					</div>
				</div> 
				<br>
				<div id="btnDiv" class="col-6">
					<div class="row">
						<div id="btnGuardar" class="col-md-3">
							<input type="button" id="guardarBtn" value="Guardar y Enviar a Firmar" class="btn btn-secondary" />
						</div>							
					</div>
				</div>
			
		</div>
	</div>
</form>
<div class="modal hide fade in" tabindex="-1" role="dialog" id="dialog-validaFact" data-mdb-keyboard="false" data-mdb-backdrop="static">
  	<div class="modal-dialog modal-lg" role="document">
    	<div class="modal-content">
	      	<div class="modal-header">
		        <h5 class="modal-title">Validación facturas</h5>
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	      	</div>
    		<div class="modal-body">
      			<div id="uploadFacturasDiv" name="uploadFacturasDiv" >
					<iframe id="uploadFacturasFrm" src="UploadFacturasRGV.jsp?tipo_pago=RELACIONGASTOS" height="468" width="100%">
					</iframe>
				</div>
				<div id="facturasCapturadasDiv">
						<div id="uploadFacturasTR">
							<div class="row">
									<div class="col-9"></div>
									<div class="col-3" style="text-align: right;">
										<a href="#" onclick="togleDivFacts(1);return false;">Cargar Facturas</a>
									</div>
							</div>
							<div class="row">
								<div class="col-12">
										<fieldset>
											<legend>Facturas Capturadas</legend>
											<table id="grdValidaFacturas" name="grdValidaFacturas">
													<thead>
														<tr>
															<th>Serie</th>
															<th>Folio_Factura</th>
															<th>Importe_Bruto</th>
														</tr>
													</thead>
											</table>
											<div class="row">
												<div class="col-7"></div>
												<div class="col-5"> 
													<div class="input-group">
														Total de las facturas:
														<input type="text" class="form-control" style="text-align: right;" name="mTotalFacturaV"
														id="mTotalFacturaV" value="0.00" readonly />
													</div>
												</div>
											</div>
									</fieldset>
								</div>
							</div>	
						</div>
					</div>
       		</div>
	     	<div class="modal-footer">
	        	<button type="button" id="btnAceptarFacturas" onclick="aceptarFacturas();" class="btn btn-primary">Aceptar</button>
	        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
	    	</div>
    	</div>
  	</div>
</div>

<div class="modal" tabindex="-1" role="dialog" id="dialog-tipoCambio" data-mdb-keyboard="true" data-mdb-backdrop="static">
  	<div class="modal-dialog" role="document">
    	<div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title">Actualiza tipo de Cambio de la Comisión</h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	      </div>
	      <div class="modal-body">
	      	<h6>Esta comision tiene un tipo de cambio diferente a pesos por lo cual se debe poner el Importe del tipo de cambio del dia para poder realizar los pagos.</h6>
		      	<div class="row">
					<div class="col-6">
						<label for="importeTipoCambio">Importe</label>
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
							<input type="text"  id="importeTipoCambio" name="importeTipoCambio" placeholder="Tipo de cambio" class="form-control"/>
						</div>
					</div>
				</div>
	       </div>
	      <div class="modal-footer">
	        <button type="button" id="btnActualizarTC" onclick="actualizarTipoCambio();" class="btn btn-primary">Aceptar</button>
	        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
	      </div>
    	</div>
  	</div>
</div>

<div class="modal" tabindex="-1" role="dialog" id="dialog-modifica" data-mdb-keyboard="true" data-mdb-backdrop="static">
  	<div class="modal-dialog" role="document">
    	<div class="modal-content">
	      	<div class="modal-header">
	        	<h5 class="modal-title">Modificar nombre de la comisión</h5>
	        	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	    	</div>
	    	<div class="modal-body">
		      	<div class="row">
					<div class="col-11">
						<label for="cNombreComision">Nombre Comisión</label>
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-chat-right-text"></i></span>
							<select class="form-select form-select" name="idNombre" id="idNombre" onChange="leerNombreComision()" ></select>
							
						</div>
					</div>
				</div>
	    	</div>
	      	<div class="modal-footer">
	        	<button type="button" id="btnModificaAgenda" onclick="actualizaNombre();" class="btn btn-primary">Actualizar</button>
	        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
	      	</div>
    	</div>
  	</div>
</div>

<!-- DIV Para capturar correo -->			
<div class="modal" tabindex="-1" role="dialog" id="dialog-actualizaCorreo" data-mdb-keyboard="true" data-mdb-backdrop="static">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Actualiza correo del proveedor</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      		<div class="modal-body">
      			<h6>Favor de capturar los siguientes datos para seguimiento del PPD:</h6>
       				<div class="row">
						<div class="col-6">
							<label for="cRFCFactura">RFC</label>
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-check-circle-fill"></i></span>
								<input type="text"  id="cRFCFactura" name="cRFCFactura" placeholder="RFC" class="form-control" readonly/>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-12">
							<label for="nombreCorreo">Nombre</label>
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-person"></i></span>
								<input type="text"  id="nombreCorreo" name="nombreCorreo" placeholder="Nombre" class="form-control"/>
								<input type="text"  id="aPCorreo" name="aPCorreo" placeholder="Apellido Paterno" class="form-control"/>
								<input type="text"  id="aMCorreo" name="aMCorreo" placeholder="Apellido Materno" class="form-control"/>
								
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-12">
							<label for="cargo">Cargo</label>
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
								<input type="text" id="cargo" name="cargo" class="form-control" placeholder="Cargo"/>
							</div>	
						</div>
					</div>
					<div class="row">
						<div class="col-12">
						<label for="correoActual">Correo</label>
						<div class="input-group">
					        <div class="input-group-prepend">
					          <div class="input-group-text">@</div>
					        </div>
					        <input type="text" class="form-control" id="correoActual" name="correoActual" placeholder="Correo electrónico"/>
					      </div>
							
						</div>		
					</div>	
      		</div>
		    <div class="modal-footer">
		        <button type="button" id="btnAceptarCorreo" onclick="validarCorreo();" class="btn btn-primary">Aceptar</button>
		        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
		    </div>
    	</div>
  	</div>
</div>


<div class="modal" tabindex="-1" role="dialog" id="dialog-notas" data-mdb-keyboard="true" data-mdb-backdrop="static">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
	    <div class="modal-header">
	        <h5 class="modal-title">Agregar notas u observaciones</h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	    </div>
      	<div class="modal-body">
	      	<div class="row">
				<div class="col-11">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-chat-right-text"></i></span>
						<input type="text"  id="notaComision" name="notaComision" class="form-control" size=30 maxlength="30"/>
					</div>
				</div>
			</div>
       	</div>
	    <div class="modal-footer">
	        <button type="button" id="btnAgregaNota" onclick="guardaNotas();" class="btn btn-primary">Guardar</button>
	        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
	    </div>
    </div>
  </div>
</div>	

<div class="modal" tabindex="-1" role="dialog" id="dialog-comprobacion" data-mdb-keyboard="true" data-mdb-backdrop="static">
  <div class="modal-dialog modal-xl" role="document">
    <div class="modal-content">
	    <div class="modal-header">
	        <h5 class="modal-title">Detalle Agenda</h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	    </div>
    	<div class="modal-body">
	      	<div id="detalleAgenda" class="mt-2" >
				<div class="row">
					<div class="col-md-6">
						<div class="card">
						  	<h5 class="card-header">Detalle de Comisión</h5>
							<div class="card-body" id="cardBodyDetalleRG">
							  	<div class="form-group row inputRG">
								  	<label class="col-6 col-form-label" for="dmPasaje">Pasajes </label>
								  	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-bus-front"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle inputRG" id="dmPasaje" name="dmPasaje" value="0.00" onchange="sumaDetalleDM(this);"/>
						              	</div>
					              	</div>
					            </div>
					            <div class="form-group row inputRG">
					              	<label class="col-6 col-form-label" for="dmTaxi">Taxi </label>
				              		<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-taxi-front"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="dmTaxi" name="dmTaxi" value="0.00" onchange="sumaDetalleDM(this);"/>
						              	</div>
					             	</div>
					            </div>
					            <div class="form-group row inputRG">
					              	<label class="col-6 col-form-label" for="dmPeaje">Combustible y peaje</label>
				              		<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="dmPeaje" name="dmPeaje" value="0.00" onchange="sumaDetalleDM(this);"/>
						              	</div>
						            </div>
					            </div>
					            <div class="form-group row inputRG">
					              	<label class="col-6 col-form-label" for="dmHotel">Factura(s) de Hotel </label>
					              	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-building"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="dmHotel" name="dmHotel" value="0.00" onchange="sumaDetalleDM(this);"/>
						              	</div>
						            </div>
					            </div>
					            <div class="form-group row inputRG">
					              	<label class="col-6 col-form-label" for="dmConsumos">Consumos </label>
					              	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-cash"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="dmConsumos" name="dmConsumos" value="0.00" onchange="sumaDetalleDM(this);"/>
						              	</div>
						              </div>
					            </div>
					            <div class="form-group row inputRG">
					              	<label class="col-6 col-form-label" for="dmOtros">Otros </label>
								   	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-coin"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="dmOtros" name="dmOtros" value="0.00" onchange="sumaDetalleDM(this);"/>
						              	</div>
						              </div>
						        	      
					            </div>
					            <div class="form-group row inputRG">
					            	<hr>
					            </div>
					            <div class="form-group row">
					              	<label class="col-6 col-form-label" for="dmTotal">Total </label>
					              	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-plus-square"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="dmTotal" name="dmTotal" value="0.00" onchange=""/>
						              	</div>
						            </div>
					            </div>
					            
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="card">
						  	<h5 class="card-header">Detalle de Transporte Local</h5>
						  	<div class="card-body">
						    	<div class="form-group row">
								  	<label class="col-6 col-form-label" for="dmPasajeLocal">Pasajes </label>
								  	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-bus-front"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="dmPasajeLocal" name="dmPasajeLocal" value="0.00" onchange="sumaTransporteDM(this);"/>
						              	</div>
						             </div>
					            </div>
					            <div class="form-group row">
					              	<label class="col-6 col-form-label" for="dmTaxiLocal">Taxi </label>
					              	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-taxi-front"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="dmTaxiLocal" name="dmTaxiLocal" value="0.00" onchange="sumaTransporteDM(this);"/>
						              	</div>
						            </div>
					            </div>
					           <div class="form-group row">
					              	<label class="col-6 col-form-label" for="dmPeajeLocal">Peaje y estacionamiento</label>
								   	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="dmPeajeLocal" name="dmPeajeLocal" value="0.00" onchange="sumaTransporteDM(this);"/>
						              	</div>
						            </div>
					            </div>
					            <div class="form-group row">
					              	<label class="col-6 col-form-label" for="dmGasolinaLocal">Combustible</label>
								   	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="dmGasolinaLocal" name="dmGasolinaLocal" value="0.00" onchange="sumaTransporteDM(this);"/>
						              	</div>
						            </div>
					            </div>
					            <div class="form-group row">
					              	<label class="col-6 col-form-label" for="dmMaritimoLocal">Marítimo</label>
								   	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-tsunami"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="dmMaritimoLocal" name="dmMaritimoLocal" value="0.00" onchange="sumaTransporteDM(this);"/>
						              	</div>
						            </div>
					            </div>
					            <div class="form-group row">
					              	<label class="col-6 col-form-label" for="dmAereoLocal">Transporte Aéreo</label>
								   	<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-airplane"></i></span>
			              					<input type="text" class="form-control col-2 inputDetalle" id="dmAereoLocal" name="dmAereoLocal" value="0.00" onchange="sumaTransporteDM(this);"/>
						              	</div>
						            </div>
					            </div>
								<hr>
					            <div class="form-group row">
					              	<label class="col-6 col-form-label" for="dmTotalLocal">Total </label>
									<div class="col-5">
									   	<div class="input-group">
							    			<span class="input-group-text"><i class="bi bi-plus-square"></i></span>
			              					<input type="text" class="form-control inputDetalle" id="dmTotalLocal" name="dmTotalLocal" value="0.00"/>
						              	</div>
						            </div>
					            </div>
							</div>
						</div>
					</div>
				</div>
			</div>			
       	</div>
	    <div class="modal-footer">
	        <button type="button" id="btnModificarDetalle" onclick="guardaDetalle();" class="btn btn-primary">Guardar</button>
	        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
	    </div>
    </div>
  </div>
</div>	  
<div class="modal" tabindex="-1" role="dialog" id="dialog-adjuntarCB" data-mdb-keyboard="true" data-mdb-backdrop="static">
	<form id="fileUploadForm" enctype="multipart/form-data">
	  <input type="hidden" name="idProcess" id="idProcess" />
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
		    <div class="modal-header">
		        <h5 class="modal-title">Adjuntar Documento</h5>
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		    </div>
	      	<div class="modal-body">
		      	<div class="row mt-1">
						<div class="input-group mb-3">
							<label class="input-group-text" for="documentSelect">
								<i class="fa-solid fa-file-pdf"></i>
							</label> 
							<select class="form-select" id="documentSelect" name="documentSelect">
								<option selected value="">Seleccione un Documento</option>
							</select> 
							<button class="btn btn-outline-secondary documentTools" type="button" onclick="showFile()" style="display: none">Ver Archivo</button>
						</div>
					</div>
					
					<div class="row mt-1 uploadFile" style="display: none">
						<div class="input-group mb-3 col-12">
							<input type="file" class="form-control" id="file" name="file" >
							<button class="btn btn-outline-secondary" type="button"  onclick="uploadFile()">Enviar</button>
						</div>
						<div class="col-12">
							<div class="progress">
							  <div class="progress-bar" id="upload-progress" role="progressbar" aria-label="Progreso de Carga" style="width: 0%;" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">0%</div>
							</div>
						</div>
					</div>
	       	</div>
		    <div class="modal-footer">
		        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
		    </div>
	    </div>
	  </div>
  </form>
</div>	
<div class="modal" tabindex="-1" role="dialog" id="dialog-reemplazo" data-mdb-keyboard="true" data-mdb-backdrop="static">
	<div class="modal-dialog" role="document">
	    <div class="modal-content">
		    <div class="modal-header">
		        <h5 class="modal-title">Reemplazar solicitud</h5>
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		    </div>
	      	<div class="modal-body">
		      		<div class="row  mt-1">
		      			<label>Seleccione el número de la solicitud a reemplazar</label>
		      		</div>
		      		<div class="row">
						<div class="input-group mb-3">
							<label class="input-group-text" for="cTramiteSelect">
								<i class="bi bi-bookmark"></i>
							</label> 
							<select class="form-select" id="cTramiteSelect" name="cTramiteSelect">
								<option selected value="">Seleccione solicitud a reemplazar</option>
							</select> 
							
						</div>
					</div>
	       	</div>
		    <div class="modal-footer">
		        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
		        <button type="button" class="btn btn-primary" onclick="guardarReemplazo()">Guardar</button>
		    </div>
	    </div>
	</div>
</div>	
  

<div class="modal" tabindex="-1" role="dialog" id="dialog-justificacion" data-mdb-keyboard="true" data-mdb-backdrop="static">
	  <div class="modal-dialog modal-lg" role="document">
	    <div class="modal-content">
		    <div class="modal-header">
		        <h5 class="modal-title">Agregar justificación</h5>
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		    </div>
	      	<div class="modal-body">
	      		<iframe id="uploadJustificacionFrm" height="480" width="100%">
					</iframe>
	       	</div>
		    
	    </div>
	  </div>
</div>

<div class="modal" tabindex="-1" role="dialog" id="dialog-pases" data-mdb-keyboard="true" data-mdb-backdrop="static">
	  <div class="modal-dialog modal-dialog-centered" role="document">
	    <div class="modal-content">
		    <div class="modal-header">
		        <h5 class="modal-title">Agregar pases de abordar</h5>
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		    </div>
	      	<div class="modal-body">
	      		<iframe id="uploadPasesFrm" height="480" width="100%"  >
					</iframe>
	       	</div>
		    
	    </div>
	  </div>
</div>

<div class="modal" tabindex="-1" role="dialog" id="dialog-gasolina" data-mdb-keyboard="true" data-mdb-backdrop="static">
	  <div class="modal-dialog modal-lg" role="document">
	    <div class="modal-content">
		    <div class="modal-header">
		        <h5 class="modal-title">Agregar justificación de Gasolina</h5>
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		    </div>
	      	<div class="modal-body">
	      		<iframe id="uploadGasolinaFrm" height="480" width="100%"  >
					</iframe>
	       	</div>
		    
	    </div>
	  </div>
</div>

</body>
	
</html>
