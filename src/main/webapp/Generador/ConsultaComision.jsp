<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.syc.obrapublica.EjercicioFiscal"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>

<%
	String fatalError = "";
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	
	int estado = 0;
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	EjercicioFiscal aEjercicioFiscal = efbl.getEjercicioFiscalActivo();
	if (usuario == null || c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String centroContable = (usuario.getPropiedad("CCENTROCONTABLE") != null ? usuario.getPropiedad("CCENTROCONTABLE").getValor() : "");
	
	if (StringUtils.isBlank(centroContable))
		fatalError = "Error Fatal: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable. Consulte a su administrador.";
	
	String unidadEjecutora = usuario.getU_UR();
	if (StringUtils.isBlank(unidadEjecutora))
		fatalError = "Error Fatal: El Usuario no tiene Unidad Ejecutora asignada y no podra realizar aplicacion Contable. Consulte a su administrador.";
	
	String login = usuario.getLogin();
	String operador = usuario.getNombre();
	String ramo = usuario.getU_Ramo();
	String ejercicioFiscal = aEjercicioFiscal.getaEjercicioFiscal();
	String numeroEmpleado = usuario.getNumeroEmpleado(); 
	String urUsuario = usuario.getU_UR();
	int nFolioTramite =  Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
	
%>


<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Comisiones de Viáticos</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"	href="css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>

</head>

<body id="dt_example">
<form id="formViaticos" name="formViaticos" class="needs-validation" >
	<div class="form-group mx-auto">
		<input type="hidden" name="FOLIO" 	 	id="FOLIO" 		value="<%=nFolioTramite%>" />
		<input type="hidden" name="login" 	  	id="login"  	value="<%=login%>"/>
		<input type="hidden" name="urUsuario" 	id="urUsuario"	value="<%=urUsuario%>" />
		<input type="hidden" name="operador"  	id="operador" 	value=""/>
		<input type="hidden" name="fechaCaptura" 	id="fechaCaptura" 	value=""/>
		<input type="hidden" name="nFolioPagoFed" 	id="nFolioPagoFed" 	value=""/>
		<input type="hidden" name="ramo" 			id="ramo" 	value=""/>
		<input type="hidden" name="aejercicioFiscal" id="aejercicioFiscal" value="<%=ejercicioFiscal%>"/>
		<input type="hidden" name="folio"			id="folio" 	value=""/>
		<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value=""/>
		<input type="hidden" name="nIdEstatus" 		id="nIdEstatus" 	value=""/>
		<input type="hidden" name="operacion" 		id="operacion" 		value=""/>
		<input type="hidden" name="existeFirmante" 	id="existeFirmante" value=""/>
		<input type="hidden" name="boletoAsignado" 	id="boletoAsignado"/>
		<input type="hidden" name="importeBoleto" 	id="importeBoleto"/>
		<input type="hidden" name="partidaBoleto" 	id="partidaBoleto"/>
		<input type="hidden" name="rutaBoleto" 		id="rutaBoleto"/>
		<input type="hidden" name="nFolioPago" 		id="nFolioPago"		value="<%=nFolioTramite%>"/>
		<input type="hidden" name="nidTipo" 		id="nidTipo" />
		<input type="hidden" name="contrarecibo" 	id="contrarecibo" />
		<input type="hidden" name="cuentaBcoMod" 	id="cuentaBcoMod" />
		<input type="hidden" name="nivel1" 		id="nivel1" value ="A"/>
		<input type="hidden" name="cNotas" 			id="cNotas" />
		<input type="hidden" name="tipoPago" 		id="tipoPago" />
	<div id="container" class="container-fluid">
		<div class="row mx-2 pt-2">
			<div class="col-md-12">
	            		<p class="h5">Seguimiento Viáticos</p>
							<div id="divDatosEmpleado">
								<div class="row">	
									<div class="col-md-6">
										<div class= "card">
											<div class="card-body"> 
												<div class="row">
													<div class="col-md-4">
														RFC Empleado					    
												        <div class="input-group">
												        	<span class="input-group-text"><i class="bi bi-tag"></i></span>
												        	<input class="form-control"  type="text" maxlength="15" size="15" name="cIdRFC" id="cIdRFC" onchange="cargaCtaBancariaRFC();"  readonly/>
												        </div>
											    	</div>
													<div class="col-md-4">
														Cuenta Bancaria
														<input type="text" class="form-control" id="cuentaBancaria" name="cuentaBancaria" size="12" value="" readonly/>
													</div>
													<div class="col-md-2">
												      UR <input type="text" class="form-control" id="cUR" name="cUR" size="15"  readonly/>
												    </div>
													<div class="col-md-2">
													   Folio <input type="text" class="form-control" id="idComision" name="idComision" size="12" value="<%=nFolioTramite%>" readonly="readonly" class="notEditable"  />
													</div>
												</div>
												<div class="row">		
												    <div class="col-md-8">
												    	Nombre
												       <div class="input-group">
													       	<span class="input-group-text"><i class="bi bi-person"></i></span>
													      	<input type="text" class="form-control" id="cnombre" name="cnombre" size="15" readonly/>
													    </div>
												    </div>
												    
												    <div class="col-md-2">
												      No Emp. <input type="text" class="form-control" id="nEmpleado" name="nEmpleado" size="15"  readonly/>
												    </div>
													<div class="col-md-2">
													   Nivel <input type="text" class="form-control" id="cNivel" name="cNivel" size="12" value="0" readonly>
												  	</div> 
												</div>
												<div class="row">
												    <div class="col-md-12">
												      Plaza 
												      	<div class="input-group">
													       	<span class="input-group-text"><i class="bi bi-postcard"></i></span>
													      	<input type="text" class="form-control" id="cPlaza" name="cPlaza" size="15"  readonly/>
													   	</div>
												    </div>
												    
												</div>
											</div>
											<div class="row">
													<div class="col-md-5">
														<span id="btnModificaBco" ><a href="#" onclick="abrirDlgCuentaBancaria();" class="link-primary">Modifica la cuenta bancaria del beneficiario</a></span>
													</div>
											</div>
										</div>
									</div>
									<div class="col-md-6">
										<div class= "card">
											<div class="card-body"> 
												<div class="row">
													<div id="resumen1" class="col-6"> 
										             	<p class="h6">Dias acumulados</p>
										             	
										             	<div class="form-group row">
									              				<label class="col-5 col-form-label" for="diasNacional">Nacional</label>
									              				<div class="col-5">
									              					<div class=" input-group">
									              						<span class="input-group-text"><i class="bi bi-geo-fill"></i></span>
										              					<input type="text" class="form-control col-2" id="diasNacional" name="diasNacional" size="5" value = "0" readonly/>
										              				</div>
										              			</div>
														</div>
														<div class="form-group row">
															<label class="col-5 col-form-label" for="diasInternacional">Internacional</label>
															<div class="col-5">
																<div class=" input-group">
									              						<span class="input-group-text"><i class="bi bi-globe-americas"></i></span>
										              					<input type="text" class="form-control col-2" id="diasInternacional" name="diasInternacional" size="5" value = "0" readonly/>
										              			</div>
															</div>
														</div>
														<div class="form-group row">
															<label for="totalDias" class="col-5 col-form-label">Este formato</label>
															<div class="col-5">
																<div class=" input-group">
									              						<span class="input-group-text"><i class="bi bi-arrow-down-circle"></i></span>
										              					<input type="text" class="form-control col-2" id="totalDias" name="totalDias" size="5" value ="0" readonly/>
										              			</div>
															</div>
														</div>
														<div class="form-group row">
														    <label for="totalAcDias" class="col-5 col-form-label">Total Dias</label>
														    <div class="col-5">
														    	<div class=" input-group">
									              						<span class="input-group-text"><i class="bi bi-arrow-right-circle"></i> </span>
										              					<input type="number" class="form-control" id="totalAcDias" value ="0" readonly/>
										              			</div>
														    </div>
														</div>
									              	</div>
									              	<div id="resumen2" class="col-6">
									              		<p class="h6">Resumen Viáticos</p>
									              		
									              		<div class="form-group row">
									              				<label class="col-5 col-form-label" for="totalAgenda">Total Agenda </label>
									              				<div class="col-5">
									              					<div class="input-group">
														    			<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
										              					<input type="text" class="form-control col-2" id="totalAgenda" name="totalAgenda" size="10" readonly/>
										              				</div>
										              			</div>
														</div>
														<div class="form-group row">
															<label class="col-5 col-form-label" for="totalTransporte">Total Transporte </label>
															<div class="col-5">
																<div class="input-group">
															    	<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
																	<input type="text" class="form-control col-2" id="totalTransporte" name="totalTransporte" size="10" readonly/>
																</div>
															</div>
														</div>
														<div class="form-group row">
															<label class="col-5 col-form-label" id="tblTotalGral" for="totalGeneral">Total General </label>
															<div class="col-5">
																<div class="input-group">
														    		<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
																	<input type="text" class="form-control col-2" id="totalGeneral" name="totalGeneral" size="10" readonly/>
																</div>
															</div>
														</div>
														<div class="row">
															<div class="col-5"></div>
															<div class="col-6">
																<div class="input-group">
																	<span class="input-group-text"><i class="bi bi-printer" onclick="imprimir()"></i></span>
																	<input type="button" class="btn btn-secondary" id="btnImprimir" name="btnImprimir" onclick="imprimir()" value ="Imprimir"/>
																</div>
															</div>
														</div>
									              	</div>
									            </div>
											</div>
										</div> <!-- Termina Card empleado -->
								  	</div>
								</div><!-- Termina row -->
							</div><!-- Fin div datos empleados-->
							<br>
							<div class="row">
								<p class="h6">Agenda</p>
								<div class="table-responsive">
									<table id="tablaAgendaConsulta" class="table table-striped table-bordered">
										<thead>
										    <tr>
										      <th>#</th>
										      <th>F.Inicio</th>
										      <th>F.Fin</th>
										      <th>Destino</th>
										      <th>Motivo de la agenda / Comisión</th>
										      <th>Cuota x dia</th>
										      <th>Dias</th>
										      <th>Importe</th>
										      <th>%</th>
										    </tr>
										</thead>
									</table>
								</div>
							</div>
							<br>
							<div class="row">
								<p class="h6">Transporte</p>
								<div class="table-responsive text-nowrap">
									<table id="tablaTransporteConsulta" class="display table table-striped table-bordered">
										<thead>
										    <tr>
										      <th>#</th>
										      <th>Tipo</th>
										      <th>Origen</th>
										      <th>Monto</th>
										      <th>Km</th>
										      <th>Num Economico</th>
										      <th>Tiene Vales</th>
										    </tr>
										</thead>
									</table>
								</div>
							</div>
							<br>
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
											      <th>Observaciones</th>
											    </tr>
											</thead>
										</table>
									</div>
							</div>
							<br>
							<div class="row">
								<div class="col-9">
									<p class="h6">Autorizados</p>
									<div class="table-responsive text-nowrap">
										<table id="tablaAutoriza" class="display table table-striped table-bordered">
											<thead>
											    <tr>
											      <th>#</th>
											      <th>Nombre</th>
											      <th>Puesto</th>
											      <th>Fecha</th>
											    </tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
							<br>
							<div class="row">
								<div class="col-9">
									<p class="h6">Bitacora de Autorizadores</p>
									<div class="table-responsive text-nowrap">
										<table id="tablaAutorizaBitacora" class="display table table-striped table-bordered">
											<thead>
											    <tr>
											      <th>#</th>
											      <th>Nombre</th>
											      <th>Puesto</th>
											      <th>Fecha Autorizado</th>
											      <th>Fecha Modificado</th>
											    </tr>
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div><!-- Fin div consulta -->
			        </div>
							    						
		</div>
	</div>
</form>	
	
<div class="modal" tabindex="-1" role="dialog" id="dialog-modificaCuenta" data-mdb-keyboard="true" data-mdb-backdrop="static">
  	<div class="modal-dialog" role="document">
    	<div class="modal-content">
	      	<div class="modal-header">
	        	<h5 class="modal-title">Modificar cuenta Bancaria</h5>
	        	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	    	</div>
	    	<div class="modal-body">
		      	<div class="row">
					<div class="col-11">
						<label for="CTAB">Cuenta Bancaria</label>
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-bank"></i></span>
							<select  id="CTAB" name ="CTAB" class="form-select" ></select>
						</div>
					</div>
				</div>
	    	</div>
	      	<div class="modal-footer">
	        	<button type="button" id="btnModificaCuenta" onclick="actualizaCuentaBancaria();" class="btn btn-primary">Guardar</button>
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
	
	<script type="text/javascript" src="js/jquery-3.5.1.min.js"></script>
	<script src="https://kit.fontawesome.com/aad2c3aad1.js" ></script>
	<script type="text/javascript" src="js/bootstrap.min.js"></script>
	<script type="text/javascript" src="js/Moment.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js" charset="UTF-8"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js" charset="UTF-8"></script>
	<script src="../Generador/js/jquery.blockUI-2.70.0.js"></script>
	<script type="text/javascript" src="js/jquery-ui.min.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="js/validaciones.js"></script>
	<script type="text/javascript" src="js/ConsultaComision.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"></script>
	<script src="../SICOVE/js/Expedient.js"></script>
	
</body>
</html>
