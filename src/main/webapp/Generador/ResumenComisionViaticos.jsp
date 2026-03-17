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
	//Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	EjercicioFiscal aEjercicioFiscal = efbl.getEjercicioFiscalActivo();
	
	
	String action = request.getParameter("a");
	String cTipoPago = request.getParameter("d");

	int nFolio = Integer.parseInt(StringUtils.isEmpty(request.getParameter("f")) ? "0" : request.getParameter("f"));
	
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
	String noEmpUsuario = usuario.getNumeroEmpleado();
	String fechaCaptura = Util.getTodayESMX();
	String fechaAplicacion = Util.calculaFechaAplicacion(aEjercicioFiscal);
	//int idCaso = c.getIdCaso(); 
	//int id_oper = c.getCasoOperacion(0).getIdOperacion();
	String unidadResponsable = usuario.getU_UR( );
%>


<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Comisiones de Viáticos</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"	href="css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"  href="../plantillasCasos/ComponentesPago/CSS/EgresoFirmantes.css"></link>
<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>

</head>

<body id="dt_example">
<form id="formViaticos" name="formViaticos" class="needs-validation" >
	<div class="form-group mx-auto">
		<input type="hidden" name="FOLIO" 		id="FOLIO" 			value="<%=nFolio%>" />
		<input type="hidden" name="FECHA_CARGA" id="FECHA_CARGA"	value="<%=fechaCaptura%>" />
		<input type="hidden" name="login" 		id="login"  		value="<%=login%>"/>
		<input type="hidden" name="noEmpUsuario" id="noEmpUsuario"  value="<%=noEmpUsuario%>"/>
		<input type="hidden" name="operador" 	id="operador" 		value=""/>
		<input type="hidden" name="cAutorizado" id="cAutorizado" 	value="N"/>
		<input type="hidden" name="cDocHaplicado" id="cDocHaplicado" 	value=""/>
		<input type="hidden" name="fechaCaptura" id="fechaCaptura" 	value=""/>
		<input type="hidden" name="fechaAplicacion" id="fechaAplicacion" value=""/>
		<input type="hidden" name="nFolioPagoFed" id="nFolioPagoFed" value=""/>
		<input type="hidden" name="ramo" 		id="ramo" 			value=""/>
		<input type="hidden" name="ejercicioFiscal" id="ejercicioFiscal" value=""/>
		<input type="hidden" name="folio" 		id="folio" 			value=""/>
		<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value=""/>
		<input type="hidden" name="nIdEstatus" 	id="nIdEstatus" 	value=""/>
		<input type="hidden" name="operacion" 	id="operacion" 		value=""/>
		<input type="hidden" name="cMotivoRechazo" id="cMotivoRechazo" value=""/>
		<input type="hidden" name="tienePagos" id="tienePagos" value=""/>
		<input type="hidden" name="saldoPagos" id="saldoPagos" value="0"/>
		<input type="hidden" name="cTieneBoleto" id="cTieneBoleto" value="0"/>
		<input type="hidden" id="idNombre" name="idNombre" value = "0"/>
		<input type="hidden" id="tienePagos" name="tienePagos"/>
		
		<div class="alert alert-danger" role="alert">
		  <b>Esta comisión solicita boleto de avión.</b> Al terminar el proceso de Autorización se enviará correo electrónico a la Agencia para la cotización del boleto.
		</div>
		<div id="container" class="container-fluid">
		<br>						
		<p class="h5"> Seguimiento Viáticos</p>
		<div id="divDatosEmpleado">
			<div class="row">	
				<div class="col-md-6">
					<div class= "card">
						<div class="card-body"> 
							<div class="row">
								<div class="col-md-4">
									RFC Empleado					    
							        <div class="input-group">
							        	<span class="input-group-text"><i class="bi bi-person"></i></span>
							        	<input class="form-control"  type="text" maxlength="15" size="15" name="cIdRFC" id="cIdRFC" onchange="cargaCtaBancariaRFC();"  readonly/>
							        	<input type="button" class="btn btn-secondary" name="btnBeneficiario" id="btnBeneficiario" value="..." size="5" onclick="cat_beneficiario();" />
							        </div>
						    	</div>
								<div class="col-md-4">
									Cuenta Bancaria
									<div class="input-group">
						        		<span class="input-group-text"><i class="bi bi-bank"></i></span>
										<input type="text" class="form-control" id="cuentaBancaria" name="cuentaBancaria" size="12" value="" readonly/>
									</div>
								</div>
								<div class="col-md-2">
								   Folio <input type="text" class="form-control" id="idComision" name="idComision" size="12" value="<%=nFolio%>" readonly="readonly" class="notEditable"  />
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
							    <div class="col-md-8">
							      Plaza 
							      <div class="input-group">
								       	<span class="input-group-text"><i class="bi bi-postcard"></i></span>
								      	<input type="text" class="form-control" id="cPlaza" name="cPlaza" size="15"  readonly/>
								   </div>
							    </div>
							    <div class="col-md-4">
							      UR <input type="text" class="form-control" id="cUR" name="cUR" size="15"  readonly/>
							    </div>
							</div>
						</div>
					</div>
			</div>
			<div class="col-md-6">
				<div class= "card">
					<div class="card-body"> 
						<div class="row">
							<div id="resumen1" class="col-6" align="center"> 
				             	<h6 class="h5">Dias acumulados</h6>
				             	
				             	<div class="form-group row">
			              				<label class="col-5 col-form-label" for="diasNacional">Nacional</label>
			              				<div class="col-5">
			              					<div class=" input-group">
				              						<span class="input-group-text"><i class="bi bi-sun"></i></span>
				              						<input type="text" class="form-control col-2" id="diasNacional" name="diasNacional" size="5" value = "0" readonly/>
				              				</div>
				              			</div>
								</div>
								<div class="form-group row">
									<label class="col-5 col-form-label" for="diasInternacional">Internacional</label>
									<div class="col-5">
										<div class=" input-group">
				              				<span class="input-group-text"><i class="bi bi-sun"></i></span>
											<input type="text" class="form-control col-2" id="diasInternacional" name="diasInternacional" size="5" value = "0" readonly/>
										</div>
									</div>
								</div>
								<div class="form-group row">
									<label for="totalDias" class="col-5 col-form-label">Pendientes</label>
									<div class="col-5">
										<div class=" input-group">
				              				<span class="input-group-text"><i class="bi bi-sun"></i></span>
											<input type="text" class="form-control col-2" id="totalDias" name="totalDias" size="5" value ="0" readonly/>
										</div>
									</div>
								</div>
								<div class="form-group row">
								    <label for="totalAcDias" class="col-5 col-form-label">Total Dias</label>
								    <div class="col-5">
								    	<div class=" input-group">
				              				<span class="input-group-text"><i class="bi bi-sun"></i></span>
								      		<input type="number" class="form-control" id="totalAcDias" value ="0" readonly/>
								      	</div>
								    </div>
								  </div>
			              	</div>
			              	<div id="resumen2" class="col-6" align="center">
			              		<h6 class="h5">Resumen Viáticos</h6>
			              		
			              		<div class="form-group row">
			              				<label class="col-6 col-form-label" for="totalAgenda">Total Agenda </label>
			              				<div class="col-6">
			              					<div class="input-group">
								    			<span class="input-group-text">$</span>
				              					<input type="text" class="form-control col-2" id="totalAgenda" name="totalAgenda" size="10" readonly/>
				              				</div>
				              			</div>
								</div>
								<div class="form-group row">
									<label class="col-6 col-form-label" for="totalTransporte">Total Transporte </label>
									<div class="col-6">
										<div class="input-group">
									    	<span class="input-group-text">$</span>
											<input type="text" class="form-control col-2" id="totalTransporte" name="totalTransporte" size="10" readonly/>
										</div>
									</div>
								</div>
								<div class="form-group row">
									<label class="col-6 col-form-label" id="tblTotalGral" for="totalGeneral">Total General </label>
									<div class="col-6">
										<div class="input-group">
								    		<span class="input-group-text">$</span>
											<input type="text" class="form-control col-2" id="totalGeneral" name="totalGeneral" size="10" readonly/>
										</div>
									</div>
								</div>
			              	</div>
			             </div>
					</div>
				</div> <!-- Termina Card empleado -->
			  </div>
			</div><!-- Termina row -->
		</div>
		<br>
			<div class="row">
				<p class="h6">Agenda</p>
				<div class="table-responsive text-nowrap">
						<table id="tablaAgenda" class="table table-striped table-bordered">
							<thead>
							    <tr>
							      <th>#</th>
							      <th>F.Inicio</th>
							      <th>F.Fin</th>
							      <th>Destino</th>
							      <th>Motivo</th>
							      <th>Cuota x dia</th>
							      <th>Dias</th>
							      <th>Importe</th>
							    </tr>
							  </thead>
						</table>
				</div>
			</div>
			<div class="mt-2">
				<p class="h6">Transporte</p>
				<div class="table-responsive text-nowrap">
						<table id="tablaTransporte" class="display table table-striped table-bordered">
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
				<div class="col-9">
					<p class="h6">Autorizadores</p>
					<div class="table-responsive text-nowrap">
							<table id="tablaAutoriza" class="display table table-striped table-bordered">
								<thead>
								    <tr>
								      <th>#</th>
								      <th>Nombre</th>
								      <th>Puesto</th>
								      <th>Fecha</th>
								      <th>Aut.</th>
								      <th>Notas</th>
								    </tr>
								</thead>
							</table>
					</div>
				</div>
				<div id="operacionesDiv" class="col-3">
					<p class="h6">Operaciones disponibles</p>
						<div class="row">
							<div class="col-md-12">
								<input type="button" id="aceptarBtn" value="Autorizar" class="btn btn-primary" />
							
								<input type="button" id="rechazarBtn" value="Rechazar" class="btn btn-secondary" />
							</div>							
						</div>
	
				</div>
			</div>
		</div>
	</div>
	</form>	
	<div class="modal" tabindex="-1" role="dialog" id="dialogMotivo" data-mdb-keyboard="true" data-mdb-backdrop="static">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title">Rechazo de Comisión de Viáticos</h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	      </div>
	      <div class="modal-body">
		      	<div class="row">
					<div class="col-11">
							<textarea rows="5" cols="35" id="motivoRechazo" class="form-control"></textarea>
					</div>
				</div>
	       </div>
	      <div class="modal-footer">
	        <button type="button" id="btnAceptar" onclick="aceptarDlg();" class="btn btn-primary">Guardar</button>
	        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
	      </div>
	    </div>
	  </div>
	</div>	
			

</body>
<script type="text/javascript" src="js/jquery-3.5.1.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/Moment.js"></script>
<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/ResumenComisionViaticos.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js" charset="UTF-8"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js" charset="UTF-8"></script>
<script type="text/javascript" charset="utf-8">
	
	function onSubmit(id_oper) {
		var p = window.parent;
	  	
		parent.document.getElementById("pb_save").disabled=true;
	}
	
</script>

</html>
