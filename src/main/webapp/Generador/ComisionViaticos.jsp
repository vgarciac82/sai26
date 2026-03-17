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
	if (c == null || usuario == null) {
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
	
	String urUsuario = usuario.getU_UR();
	String urUsuario_orig = usuario.getU_UR_Orig();
	String login = usuario.getLogin();
	String operador = usuario.getNombre();
	String fechaCaptura = Util.getTodayESMX();
	String fechaAplicacion = Util.calculaFechaAplicacion(aEjercicioFiscal);
	String ramo = usuario.getU_Ramo();
	String ejercicioFiscal = aEjercicioFiscal.getaEjercicioFiscal();
	String folio = c.getFolio();
	String numeroEmpleado = usuario.getNumeroEmpleado();
	int idCaso = c.getIdCaso(); 
	int nFolioTramite = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
	int id_oper = c.getCasoOperacion(0).getIdOperacion();
	
	if ( !StringUtils.isEmpty( urUsuario_orig ))
		urUsuario = urUsuario_orig;
%>


<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content ="IE-edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Comisiones de Viáticos</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/css/bootstrap-datetimepicker.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
<link rel="stylesheet" href="https://cdn.datatables.net/1.13.8/css/jquery.dataTables.min.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>


</head>

<body id="dt_example">
<form id="formViaticos" name="formViaticos" class="needs-validation" >
	<div id="container" class="container-fluid">
		<input type="hidden" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" />
		<input type="hidden" name="OPERADOR" id="OPERADOR"	value="<%=c.getCasoOperacion(0).getResponsable()%>" />
		<input type="hidden" name="FECHA_CARGA" id="FECHA_CARGA"	value="<%=fechaCaptura%>" />
		<input type="hidden" name="urUsuario" id="urUsuario"	value="<%=urUsuario%>" />
		<input type="hidden" id="login" name="login" value="<%=login%>"/>
		<input type="hidden" id="pais" name="pais"/>
		<input type="hidden" id="estado" name="estado"/>
		<input type="hidden" id="municipio" name="municipio"/>
		<input type="hidden" id="nidtipo" name="nidtipo"/>
		<input type="hidden" id="tipoTransp" name="tipoTransp"/>
		<input type="hidden" id="niveles" name="niveles"/>
		<input type="hidden" id="paquetes" name="paquetes"/>
		<input type="hidden" id="campoRFC" name="campoRFC"/>
		<input type="hidden" id="ctaBancariaRFC" name="ctaBancariaRFC"/>
		<input type="hidden" id="cTipoRfc" name="cTipoRfc" value = 3/>
		<input type="hidden" id="registros" name="registros" value = 0/>
		<input type="hidden" id="folioA" name="folioA" value = 0/>
		<input type="hidden" id="hasPackage" name="hasPackage" value = 0/>
		<input type="hidden" id="hasSameRate" name="hasSameRate" value = 0/>
		<input type="hidden" id="hasTicket" name="hasTicket" value = 0/>
		<input type="hidden" id="tieneVales" name="tieneVales" value = 0/>
		<input type="hidden" id="actividades" name="actividades"/>
		<input type="hidden" id="concepto" name="concepto"/>
		<input type="hidden" id="dateExist" name="dateExist"/>
		<input type="hidden" id="dateTemp" name="dateTemp"/>
		<input type="hidden" id="nOrigen" name="nOrigen" value ="ORIGINAL"/>
		<input type="hidden" id="existeComision" name="existeComision"/>
		<input type="hidden" id="existeTransporte" name="existeTransporte"/>
		<input type="hidden" id="idTransporte" name="idTransporte"/>
		<input type="hidden" id="nComisionAnterior" name="nComisionAnterior"/>
		<input type="hidden" id="nComisionAnteriorF" name="nComisionAnteriorF"/>
		<input type="hidden" id="nComisionAnterior2" name="nComisionAnterior2"/>
		<input type="hidden" id="fechaInicio" name="fechaInicio"/>
		<input type="hidden" id="fechaFin" name="fechaFin"/>
		<input type="hidden" id="tieneAgenda" name="tieneAgenda"/>
		<input type="hidden" id="fFinAnterior" name="fFinAnterior"/>
		<input type="hidden" id="nivel1" name="nivel1" value ="A"/>
		<input type="hidden" id="tieneInicidencia" name="tieneInicidencia"/>
		<input type="hidden" id="tieneInicidenciaF" name="tieneInicidenciaF"/>
		<input type="hidden" id="tieneExcepcionMA" name="tieneExepcionMA" value= 0/>
		<input type="hidden" id="agendaDifAnio" name="agendaDifAnio"/>
		<input type="hidden" id="jBoleto" name="jBoleto"/>
		<input type="hidden" id="nombreComision" name="nombreComision"/>
		<input type="hidden" id="cUejecutora" name="cUejecutora"/>

			<div id="radiosEmpleado" class="col-12" style="visibility:hidden">
				<div class="form-check form-check-inline">
				  <input type="radio" class="form-check-input" id="radio1" name="optradio" value="unica" onclick="validaBotones()" checked>Unica
				  <label class="form-check-label" for="radio1"></label>
				</div>
				<div class="form-check form-check-inline">
				  <input type="radio" class="form-check-input" id="radio2" name="optradio" value="multiple" onclick="validaBotones()">Multiempleados
				  <label class="form-check-label" for="radio2"></label>
				</div>
			</div>
			<!-- Datos multi empleado -->
								
		<div align="center"><h1 class="h4 text-white bg-secondary">MÓDULO DE VIÁTICOS</h1></div>
			
		<div id="divDatosEmpleado">
			<div class="row mx-2">	
				<div class="col-lg-5 col-md-12 col-sm-12">
					<div class= "card">
						<div class="card-header">
						    <h5>Busqueda de datos del empleado</h5>
						</div>
						<div class="card-body"> 
							<div class="row">	
								
							</div>
							<div class="row">
								<div class="col-md-5">
									RFC Empleado					    
							        <div class="input-group">
							        	<span class="input-group-text"><i class="bi bi-1-circle"></i></span>
							        	<input class="form-control"  type="text" maxlength="15" size="15" name="cIdRFC" id="cIdRFC" onchange="consultaDatos(); cargaCtaBancariaRFC(); cargaCuota(); "/>
							        	<input type="button" class="btn btn-secondary" name="btnBeneficiario" id="btnBeneficiario" value="..." size="5" onclick="cat_beneficiario();" />
							        </div>
						    	</div>
								<div class="col-md-5">
										Cuenta Bancaria
										<div class="input-group">
							        		<span class="input-group-text"><i class="bi bi-2-circle"></i></span>
											<select class="form-select" name="ctaBancaria" id="ctaBancaria" onclick="cargaCtaBancariaRFC(); ">
											<option selected>Seleccione cuenta bancaria</option>
											</select>
											<input type="text" class="form-control" id="cuentaBancaria" name="cuentaBancaria" size="12" value="" readonly/>
										</div>
								</div>
								<div class="col-md-2">
								   		Folio <input type="text" class="form-control" id="idComision" name="idComision" size="12" value="<%=nFolioTramite%>" readonly="readonly" class="notEditable"  />
								</div>
							</div>
							<div id="divTablaEmpleados" class="table-responsive">
									<table id="tablaEmpleados" class="table table-striped table-bordered">
										<thead>
										    <tr>
										      <th>#</th>
										      <th>Nombre</th>
										      <th>Cuenta</th>
										      <th>Eliminar</th>
										    </tr>
										</thead>
										<tbody></tbody>
									</table>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-7 col-sm-12">
					<div class= "card">
						
						<div class="card-body"> 
							<div class="row">		
								    <div class="col-md-8 col-sm-12">
								    	Nombre
								    	 <div class="input-group">
									       	<span class="input-group-text"><i class="bi bi-person"></i></span>
									      	<input type="text" class="form-control" id="cnombre" name="cnombre" size="15" readonly/>
									     </div>
								    </div>
								    <div class="col-md-2 col-sm-6">
								      	No Emp. <input type="text" class="form-control" id="nEmpleado" name="nEmpleado" size="15" readonly/>
								    </div>
									<div class="col-md-2 col-sm-6">
									   	Nivel <input type="text" class="form-control" id="cNivel" name="cNivel" size="12" value="0" readonly>
								  	</div> 
							</div>
							<div class="row">
								    <div class="col-md-6 col-sm-12">
								      	Plaza 
								      	<div class="input-group">
									       	<span class="input-group-text"><i class="bi bi-postcard"></i></span>
									      	<input type="text" class="form-control" id="cPlaza" name="cPlaza" size="15"  readonly/>
									    </div>
								    </div>
								    <div class="col-md-6 col-sm-12">
								      	UR <input type="text" class="form-control" id="cUR" name="cUR" size="15"  readonly/>
								    </div>
							</div>
						</div>
					</div> <!-- Termina Card empleado -->
				  </div>
			   </div><!-- Termina row -->
			</div> 
			<div class="row mx-2 pt-2">
				<div class="col-lg-12 col-md-12">
			      <div class="card">
			        <div class="card-header">
			          <ul class="nav nav-tabs card-header-tabs" id="viaticos-list" role="tablist">
			            <li class="nav-item">
			              <a class="nav-link active" href="#agenda" role="tab" aria-controls="agenda" aria-selected="true">Agenda</a>
			            </li>
			            <li class="nav-item">
			              <a class="nav-link"  href="#transporte" role="tab" aria-controls="transporte" aria-selected="false" >Transporte</a>
			            </li>
			            <li class="nav-item">
			              <a class="nav-link" href="#resumen" role="tab" aria-controls="resumen" aria-selected="false">Resumen</a>
			            </li>
			          </ul>
			        </div>
			        <div class="card-body">
			           <div class="tab-content">
			            	<div class="tab-pane active" id="agenda" role="tabpanel">
			            		<div id="encabezadoAgenda">
			             		 	<div class="row ">
											<div class="col-lg-3 col-md-3 col-sm-6">
											    Fecha Inicio
											    <div class="input-group">
											      	<span class="input-group-text"><i class="bi bi-calendar3"></i></span> 
											      	<input type="text" class="form-control" id="fInicio" name="fInicio" size="15" onchange="validaFechaAnterior(this);" readonly/>
											    </div>
											</div>
									    <div class="col-lg-3 col-md-3 col-sm-6">
									      	Fecha Fin 
									      	 <div class="input-group">
											      <span class="input-group-text"><i class="bi bi-calendar3"></i></span> 
											      <input type="text" class="form-control" id="fFin" name="fFin" size="15" onchange="validaFechaAnterior(this);verificaFechas()"  readonly/>
											 </div>
									    </div>
										 	<div class="col-md-6 col-sm-12">
										      	Nombre de la comisión
										      	<div class="input-group">
											      <span class="input-group-text"><i class="bi bi-airplane"></i></span> 
												      <select class="form-select form-select" name="idNombre" id="idNombre" onchange="leerNombreComision()" ></select>
												</div>
										    </div>
									</div>
									<div class="row">
									    <div class="col-md-3 col-sm-6">
									    	País:
									      	<div class="input-group">
											      <span class="input-group-text"><i class="bi bi-globe"></i></span> 
											      <select class="form-select form-select" name="cPais" id="cPais" onchange="cargaEstados(), cargaMunicipios()" ></select>
											</div>
									    </div>
									    <div class="col-md-3 col-sm-6">
									      	Estado:
									      	<div class="input-group">
											      <span class="input-group-text"><i class="bi bi-buildings"></i></span> 
											      <select class="form-select form-select" name="estadocombo" id="estadocombo" onchange="cargaMunicipios()" ></select>
											</div>
									      	<div id="dlgAgregar" title="Agregar nuevo Estado" style="display: none;">
									      		<div class="form-row">
						        							Estado/Provincia: 
						        							<div class="input-group">	
						        								<span class="input-group-text"><i class="bi bi-hospital"></i></span>
							        							<input type="text" class="form-control" id="nombre_estado" name="nombre_estado" size="25" />
							        							<input type="button" class="btn btn-secondary" id="btnAgregar" name="btnAgregar" onclick="agregarEstado()" value ="Agregar"/>
							        						</div>
						        				</div>
						        			</div>	
									    </div>
										<div class="col-md-3 col-sm-6">
										    Municipio:
										    <div class="input-group">
											      <span class="input-group-text"><i class="bi bi-building"></i></span> 
											      <select class="form-select form-select" name="municipiocombo" id="municipiocombo" onchange="validaMunicipio()"></select>
											</div>
										    
											    <div id="dlgAgregarMpio" title="Agregar nueva Ciudad" style="display: none;">
						        					<div class="form-row">
						        								Ciudad:	
						        								<div class="input-group">	
						        								 	<span class="input-group-text"><i class="bi bi-building-add"></i></span>						
							        								<input type="text" class="form-control" id="nombre_municipio" name="nombre_municipio" size="25" />
							        								<input type="button" class="btn btn-secondary" id="btnAgregarMpio" name="btnAgregarMpio" onclick="agregarMunicipio()" value="Agregar"/>
						        								</div>
						        					</div>
						        				</div>
									  	</div>
									  	<div class="col-md-3 col-sm-6">
										    Localidad:
										    <div class="input-group">
											      <span class="input-group-text"><i class="bi bi-houses"></i></span> 
											      <input type="text" class="form-control" id="cLocalidad" name="cLocalidad" size="20" aria-describedby="inputGroup-sizing-sm"/>
											</div>
										    
									  	</div>
									</div>
									<div class="row">
										    <div class="col-md-12">
										      	Motivo de la comisión
												<textarea class="form-control" id="cConcepto" rows="3" onkeypress="validarCaracteres(event)"></textarea>
										    </div>
									</div>
									<div class="row">
											<div class="col-md-2 col-sm-6">
												<label for = "cCuota">Cuota por día</label>
										    	<div class="input-group">
										    		<span class="input-group-text">$</span>
													<input type="number" class="form-control" id="cCuota" name="cCuota" value="" readonly/>
												</div>
										    </div>
										    <div class="col-md-1 col-sm-6">										    	
													<label for="cMoneda">Moneda: </label>
													<select class="form-select form-select" name="cMoneda" id="cMoneda" onchange="leerTipoMoneda()"></select>
											</div>
											<div class="col-md-2 col-sm-6" >
													<label for="tipoCambio">Tipo Cambio: </label>
													<input type="text" class="form-control" id="tipoCambio" name="tipoCambio" value="1" />
											</div>
									</div>
									<div class="form-check mt-2">
										<div class="input-group">
											<div class="col-md-2 col-sm-6">
												<input class="form-check-input" type="checkbox" id="chk_homologa" onclick="activarBotones()" value="0">
												<label class="form-check-label" for="chk_homologa">Homologar</label>
											</div>
										    <div class="col-md-3 col-sm-6 inputHomologa">
										    	<label id="lblNivel">Nivel Homologa</label>
										    	<div class="input-group">
														<span class="input-group-text"><i class="bi bi-arrow-up-right-circle"></i></span>
										    			<select class="form-select" id="nivelHomologa" name="nivelHomologa" onchange="cargaNiveles()"></select>
										    	</div>
										    </div>
										    <div class="col-md-3 col-sm-12 inputHomologa">
										    	<label id="lblPlaza">Plaza</label>
										    	<input type="text" class="form-control" id="plazaHomologa" name ="plazaHomologa" />
										    </div>
										    <div class="col-md-3 col-sm-12 inputHomologa">
										    	<label id="lblJustifica">Justificación</label>
										    	<input type="text" class="form-control" id="plazaJustifica" name ="plazaJustifica" />
										    </div>
										 </div>
									</div>	
									<div class="form-check">
										<div class="input-group"> 
												<div class="col-md-2 col-sm-6">
													<input class="form-check-input" type="checkbox" id="chk_paquete" onclick="activarPaquete()" value="0">
													<label class="form-check-label" for="chk_paquete">Paquetes</label>
												</div>
												<div class="col-md-3 col-sm-6 inputPaquete">
													<div class="input-group">
														<span class="input-group-text"><i class="bi bi-box-seam"></i></span>
														<select class="form-select" id="paqueteCombo" name="paqueteCombo" onchange="cargaPaquetes()"></select>
													</div>
												</div>
												<div class="col-md-1 col-sm-6 inputPaquete">
													<div class="input-group">
														<input class="form-control" type="text"  id="nPorcentaje" name ="nPorcentaje" readonly/>
														<span class="input-group-text">%</span>
													</div>
												</div>
												<div class="col-md-4 d-sm-none"></div>
												<div class="col-md-2 col-sm-6">
													<br>
													<input type="button" class="btn btn-secondary"  id="btnAgregaAgenda" name="btnAgregaAgenda" onclick="save()" value ="Agregar"/>
													<input type="button" class="btn btn-primary"  id="btnGuardar" name="btnGuardar" onclick="avanzar()" value ="Avanzar"/>
												</div>
											</div>
									</div>
								</div>
								<div id="divTablaAgenda" class="table-responsive text-nowrap">
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
										      <th>%</th>
										      <th>Eliminar</th>
										    </tr>
										  </thead>
									</table>
								</div>
								<div id="divTablaAgendaConsulta" class="table-responsive text-nowrap">
									<table id="tablaAgendaConsulta" class="table table-striped table-bordered">
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
											      <th>%</th>
											    </tr>
											 </thead>
									</table>
								</div>	
			            </div><!-- Fin div Agenda -->
			             
			            <div class="tab-pane" id="transporte" role="tabpanel" aria-labelledby="transporte-tab">  
			             	<div id="transporteEncabezado">	
			             		 <div class="row">
										<div class="col-md-2 col-sm-12">
									      Tipo Transporte 
									      <div class="input-group">								
												<span class="input-group-text"><i class="bi bi-taxi-front"></i></span>
												<select class="form-select" name="transporteCombo" id="transporteCombo" onchange="cargaTransporte(); validaTransporte();"></select>
										  </div>
									      	
									    </div>
									    <div class="col-md-3 col-sm-12">
									      Descripción <input type="text" class="form-control" id="descripcion" name="descripcion" size="15"/>
									    </div>
									  
									    <div class="col-md-2 col-sm-12">
									    	<label for= "importeT">Importe</label>
									    	<div class="input-group">
									    		<span class="input-group-text">$</span>
									      	 	<input type="number" class="form-control" id="importeT" name="importeT" size="15"/>
									      	 	
									      	 </div>
									    </div>
									    <div class="col-md-2 col-sm-12">
									    	<br>
										      <input type="button" class="btn btn-secondary" onclick="agregarTransporte()"  id="btnAgregarTrans" name="btnAgregarTrans" value="Agregar"/>
	  							      	 	  <input type="button" class="btn btn-primary" onclick="avanzaTransporte()"  id="btnAvanzaTrans" name="btnAvanzaTrans" value="Avanzar"/>
									    </div>
									 
								</div>
								<div id="transporteDet">
									<div class="row">
										<div class="col-md-1 col-sm-6">
									      Kilometraje <input type="number" class="form-control" id="km" name="km" size="15" value="0"/>
									    </div>
									    <div class="col-md-1 d-sm-none"> </div>
									    <div class="col-md-2 col-sm-6">
									    	<label id="lblnEconomico">Num Economico </label>
									      	<input type="text" class="form-control" id="nEconomico" name="nEconomico" size="15"/>
									    </div>
									    <div class="col-md-1 d-sm-none"></div>
									    <div class="col-md-3 col-sm-12">
									       	<div class="form-check"> 	
				  								<input class="form-check-input" type="checkbox" id="chk_tieneVales" value="1">
				  								<label class="form-check-label" for="chk_tieneVales">Tiene Vales</label>
			  								</div>									      
									    </div>
									</div>		
								</div>
							</div>
							
							<div class="col-md-10 col-sm-12">
								<div class="table-responsive text-nowrap mt-2" id="divTransporte">
									<table id="tablaTransporte" class="table table-striped table-bordered">
										<thead>
										    <tr>
										      <th>#</th>
										      <th>Tipo</th>
										      <th>Origen</th>
										      <th>Monto</th>
										      <th>Km</th>
										      <th>Num Economico</th>
										      <th>Tiene Vales</th>
										      <th>Eliminar</th>
										    </tr>
										</thead>
									</table>
								</div>
								<div class="table-responsive text-nowrap" id="divTransporteConsulta">
									<table id="tablaTransporteConsulta" class="table table-striped table-bordered">
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
			            </div> <!-- Fin div transporte -->
			             
			            <div class="tab-pane" id="resumen" role="tabpanel" aria-labelledby="resumen-tab">
			             	
			             	<div class="row">
				             	<div id="resumen1" class="col-lg-4 col-sm-12" align="center"> 
					             	<h6 class="h5">Días acumulados</h6>
					             	<hr style="color: #0056b2;" />
					             	<div class="form-group row">
				              				<label class="col-md-5 col-form-label" for="diasNacional">Nacional</label>
				              				<div class="col-md-5">
				              					<div class=" input-group">
				              						<span class="input-group-text"><i class="bi bi-geo-fill"></i></span>
					              					<input type="text" class="form-control col-2" id="diasNacional" name="diasNacional" size="5" value = "0" readonly/>
					              				</div>
					              			</div>
									</div>
									<div class="form-group row">
										<label class="col-md-5 col-form-label" for="diasInternacional">Internacional</label>
										<div class="col-md-5">
											<div class=" input-group">
				              						<span class="input-group-text"><i class="bi bi-globe-americas"></i></span>
					              					<input type="text" class="form-control col-2" id="diasInternacional" name="diasInternacional" size="5" value = "0" readonly/>
					              			</div>
										</div>
									</div>
									<div class="form-group row">
										<label for="totalDias" class="col-md-5 col-form-label">Este formato</label>
										<div class="col-md-5">
											<div class=" input-group">
				              						<span class="input-group-text"><i class="bi bi-arrow-down-circle"></i></span>
					              					<input type="text" class="form-control col-2" id="totalDias" name="totalDias" size="5" value ="0" readonly/>
					              			</div>
										</div>
									</div>
									<div class="form-group row">
									    <label for="totalAcDias" class="col-md-5 col-form-label">Total Dias</label>
									    <div class="col-md-5">
									    	<div class=" input-group">
				              						<span class="input-group-text"><i class="bi bi-arrow-right-circle"></i></span>
					              					<input type="number" class="form-control" id="totalAcDias" value ="0" readonly/>
					              			</div>
									    </div>
									  </div>
				              	</div>
				              	<div id="resumen2" class="col-lg-4 col-sm-12" align="center">
				              		<h6 class="h5">Resumen Viáticos</h6>
				              		<hr style="color: #0056b2;" />
				              		<div class="row">
				              				<label class="col-md-5 col-form-label" for="totalAgenda">Total Agenda </label>
				              				<div class="col-md-5">
				              					<div class=" input-group">
				              						<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
					              					<input type="text" class="form-control col-2" id="totalAgenda" name="totalAgenda" size="10" readonly/>
					              				</div>
					              			</div>
									</div>
									<div class="form-group row">
										<label class="col-md-5 col-form-label" for="totalTransporte">Total Transporte </label>
										<div class="col-md-5">
											<div class=" input-group">
				              					<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
					              				<input type="text" class="form-control col-2" id="totalTransporte" name="totalTransporte" size="10" readonly/>
					              			</div>
										
										</div>
									</div>
									<div class="form-group row">
										<label class="col-md-5 col-form-label" id="tblTotalGral" for="totalGeneral">Total General </label>
										<div class="col-md-5">
											<div class=" input-group">
				              					<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
					              				<input type="text" class="form-control col-2" id="totalGeneral" name="totalGeneral" size="10" readonly/>
					              			</div>
										
										</div>
									</div>
				              	</div>
				              	<div id="resumen3" class="col-lg-4 col-sm-12">
									<div class="row">
										<div class="col-md-12">
											<div class="form-check"> 	
				  								<input class="form-check-input" type="checkbox" id="chkBoletosAvion" value="1">
				  								<label class="form-check-label" for="chkBoletosAvion">Solicitar Boleto de Avión</label>
			  								</div>
										 </div>
									</div>
									<div class="row">
										 <div class="col-md-12" id="divJustBoleto">
										 	Notas para enviar a la Agencia por correo electrónico
										 	<textarea class="form-control" id="justificaBoleto" rows="6" ></textarea>
										 </div>
									 </div>
								</div>
			              	
			              	</div>
			            </div>
			          </div>
			        </div>
			      </div>
			    </div>
			  </div>
			</div>	  
	</form>
	<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>	
  	<script type="text/javascript" src="../Generador/js/jquery-ui.min.js"></script>
  	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  	<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="../Generador/js/Moment.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/js/bootstrap-datetimepicker.min.js"></script>
	<script type="text/javascript" src="../Generador/js/ComisionViaticos.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../Generador/js/validaciones.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js" charset="UTF-8"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js" charset="UTF-8"></script>
</body>
<script type="text/javascript" charset="utf-8">
	var id_oper = <%=id_oper%>;
	
	function onSubmit(id_oper) {
	
		let ejecucionCorrecta = true;
		var p = window.parent;
	  	//Guardado de los campos correspondientes a cada variable de caso
		p.gestion.setFolio( $("#FOLIO").val() );
		p.gestion.setOperador( $("#OPERADOR").val() );
		p.gestion.setFechaDocumento( $("#FECHA_CARGA").val() );
		p.gestion.setEjercicioFiscal( "<%=ejercicioFiscal%>" );
		p.gestion.setConceptoMov("Documentaci&oacuten Comisión Viaticos");
		p.gestion.setMoneda("MXP");
		p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
		p.gestion.setAplicadoCont("false");
		
		if(id_oper==1){
			try {
				if(guardarTramite()) {
					cmdImprimir();
					parent.document.getElementById("pb_save").disabled=true;
					parent.document.getElementById("pb_send").disabled=false;
					Swal.fire("OK","Comisión guardada con exito!!!", "success");
					
				}
			} catch (e) {
				alert(e);
				ejecucionCorrecta = false;
			}
		}
		
		return ejecucionCorrecta;
	}
	
	function ResponsableSiguiente(id_oper) {
		switch(parseInt(id_oper)){
		case 1:			
			return "VALIDA_VIATICOS";
			break;		
		case 2 :
			if(!valida)
				return "VALIDA_VIATICOS";
			else
				return "CONSULTA_VIATICOS";
			break;
					
		case 3 :
			return "CONSULTA_VIATICOS";
			break;
		}
	}

	function OperacionSiguiente(id_oper) {
		switch(parseInt(id_oper)){
		case 1:
			return "valida_viaticos";
			break;		
		case 2 :			
			if(!valida)
				return "captura_viaticos";
			else
				return "consultar_viaticos";						
			break;
					
		case 3 :
			return "consultar_viaticos";
			break;
		}
	}
	
	function onPostSubmit(id_oper){
		return true;
		
  	}
  	
	function onPostDisplay(id_oper){
		return true;
		
	}
	
	$(document).ready(function() {
		
		parent.document.getElementById("pb_save").disabled = true;
		$("#cuentaBancaria").hide();
		revisaValores();	
			
		setFechas();		 
		cargaPaises();
		cargaEstados();
		cargaMunicipios();
		activarBotones();
		cargaNiveles();
		activarPaquete();
		cargaPaquetes();
		cargaTransporte();
		creaTableAgenda();
		creaTableTransporte();
		//createTableAgendaConsulta();
		//creaTableTransporteConsulta();
		
		$("#cCuota").val(cuota);
		
		$("#btnAgregarMpio").button();
		$("#btnAgregarEmp").attr("style", "visibility: hidden");
		$(".divBanco").css('display', 'none');
		
		$('#viaticos-list a').on('click', function (e) {
			  e.preventDefault()
			  $(this).tab('show')
			})
		
		querySelectPost("cat_tipoMoneda", "cMoneda",{async: false });
		
		if($("#radio1").is(':checked'))	
			$("#divTablaEmpleados").hide();
		
		$("#chk_tieneVales").change(function(){
			if ($("#chk_tieneVales").prop("checked")){
				$("#importeT").val(0);
				$("#importeT").attr('readonly', true);
			} else {
				$("#importeT").attr('readonly', false);
			}
		});
		
		querySelectPost("cargaNombreComision", "idNombre",{async: false });
		
		$("#divJustBoleto").hide();
		$("#chkBoletosAvion").change(function(){
			if ($("#chkBoletosAvion").prop("checked")){
				$("#hasTicket").val(1);
				$("#divJustBoleto").show();
			} else {
				$("#hasTicket").val(0);
				$("#divJustBoleto").hide();
			}
		});
	});
</script>
</html>
