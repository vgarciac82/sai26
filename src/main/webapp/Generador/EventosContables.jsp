<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.sai.contabilidad.caja.CajaBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>

<%	
	String cUR = "";
	String cRamo = "";
	String algo = "";

	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
		
	cUR = usuario.getU_UR();	
	cRamo = usuario.getU_Ramo();
	algo = usuario.getLogin();

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
	<title>Eventos Contables (Caja, Reintegros Caja, Poliza Manual)</title>
	
	<link rel="stylesheet" type="text/css" href="css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
	<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
	
	<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
	<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
	<script src="../Generador/js/bootstrap.bundle.min.js"></script>
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
	
	<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"> </script>
	<script type="text/javascript" src="../js/jsquery.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>
	<script type="text/javascript" src="../Generador/js/EventosContables.js"> </script>

</head>

<body id="dt_example">
<br/>
	<form id="TempForm" name="TempForm" action="../gstnmngr/guardaEventoContable">		
		<div id=container style="width: 100%" class="container">		
			<input type="hidden" id="modulo" name="modulo" />
			<input type="hidden" id="cUR" name="cUR" value="<%=cUR%>"/> 
			
			<div class="card-header"> <h3> Configuración de Eventos <label style="font-size: 12pt"> (Caja, Reintegros Caja y Póliza Manual) </label> </h3> </div>
			<hr class="mt-3"/>																												
			
			<div id="divTipoConfiguracion">
				<center>				
					<h6> <label style="color: #2471A3"> Selecciona el tipo de configuración a realizar </label> </h6>
					
					<div class="row d-flex">
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex justify-content-center">															
							<div class="form-check">
								<input type="radio" name="TIPO_CONFIGURACION" id="TIPO_CONFIGURACION1" class="form-check-input" value = "1" onclick="selecciona(this)" checked/>								
								<label for="op1" class="form-check-label">Evento Nuevo</label>
							</div>									
						</div>
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex justify-content-center">		
							<div class="form-check">								
								<input type="radio" name="TIPO_CONFIGURACION" id="TIPO_CONFIGURACION2" class="form-check-input" value = "2" onclick="selecciona(this)"/>
								<label for="op2" class="form-check-label">Sub-Grupo Nuevo</label>
							</div>
						</div>
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex justify-content-center">		
							<div class="form-check">								
								<input type="radio" name="TIPO_CONFIGURACION" id="TIPO_CONFIGURACION3" class="form-check-input" value = "3" onclick="selecciona(this)"/>
								<label for="op3" class="form-check-label">Consecutivo Nuevo</label>
							</div>
						</div>
					</div>					
					<hr class="mt-3"/>
				</center>		
			</div>
			
			<div class="card">
				<div class="card-header">
		       		<ul class="nav nav-pills  card-header-pills ">
			            <li class="nav-item">
			            	<button class="nav-link active" id="tabs-1" onClick="navEvento()" data-bs-toggle="tab" data-bs-target="#tabs-1-evento" type="button" role="tab" aria-controls="tabs-evento" aria-selected="true">Evento</button>
			            </li>
			            <li class="nav-item">
			            	<button class="nav-link" id="tabs-2" onClick="navGuia()" data-bs-toggle="tab" data-bs-target="#tabs-2-guia" type="button" role="tab" aria-controls="tabs-guia" aria-selected="true">Guía</button>
			            </li>
			            <li class="nav-item">
			            	<button class="nav-link" id="tabs-3" onClick="navCuentas()" data-bs-toggle="tab" data-bs-target="#tabs-3-cuentas" type="button" role="tab" aria-controls="tabs-cuentas" aria-selected="true">Cuentas</button>
			            </li>
			  		</ul>
	  			</div>
		  				
		  		<div class="card-body">	
					<div class="tab-pane fade show active" id="#tabs-1-evento" role="tabpanel" aria-labelledby="tabs-evento">
						<div id="divAltaEvento">
							<h6>Captura de Evento</h6>	
							<hr class="mt-3"/>
							
							<div class="row d-flex">																
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<label for="nIdGrupoEvento" class="form-label">ID Grupo</label>									
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-123"></i></span>
										<input type="number" name="nIdGrupoEvento" id="nIdGrupoEvento" class="form-control form-control-sm" value=""/>
									</div>	
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<label for="cNombreGrupo" class="form-label">Nombre Grupo</label>									
								</div>
								<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-pencil-square"></i></span>
										<input type="text" name="cNombreGrupo" id="cNombreGrupo" class="form-control form-control-sm" value="" maxlength="100"/>
									</div>
								</div>
							</div>		
								
					
							<div class="row d-flex">																
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<label for="nIdSubGrupoEvento" class="form-label">ID Sub-Grupo</label>									
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-123"></i></span>
										<input type="number" name="nIdSubGrupoEvento" id="nIdSubGrupoEvento" class="form-control form-control-sm" value=""/>
									</div>
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<label for="cNombreSubGrupo" class="form-label">Nombre Sub-Grupo</label>									
								</div>
								<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-pencil-square"></i></span>
										<input type="text" name="cNombreSubGrupo" id="cNombreSubGrupo" class="form-control form-control-sm" value="" maxlength="500"/>
									</div>
								</div>
							</div>
						
							<div class="row d-flex">																
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<label for="cEvento" class="form-label">ID Evento</label>									
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-123"></i></span>
										<input type="text" name="cEvento" id="cEvento" class="form-control form-control-sm" value=""/>
									</div>
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<label for="dEvento" class="form-label">Nombre Evento</label>									
								</div>
								<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-pencil-square"></i></span>
										<input type="text" name="dEvento" id="dEvento" class="form-control form-control-sm" value="" maxlength="200"/>
									</div>
								</div>
							</div>		
							
							<div class="row d-flex">																
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<label for="cIdUnidadEjecutora" class="form-label">Unidad Responsable</label>									
								</div>
								<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
									<select id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" class="form-select form-select-sm">
				            		<option value="<%=cUR%>"></option>
					            </select>										
								</div>
							</div>			
							
							<center>
								<div class="row d-flex">																
									<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
										<input type="button" class="btn btn-secondary btn-sm" id="btnGuardaEvento" name="btnGuardaEvento" onclick="GuardaEvento()" value="Guardar Evento">
									</div>
								</div>
							</center>						
						</div>
					</div>
				</div>		
				
				<div class="card-body">
					<div class="tab-pane fade" id="tabs-2-guia" role="tabpanel" aria-labelledby="tabs-guia">
						 <div id="divConfiguracionGuia">					
							<h6>Captura de Guía Contable</h6>
							<hr class="mt-3"/>
							
							<div class="row d-flex">
								<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex justify-content-center">															
									<div class="form-check">
										<label for="op1" class="form-check-label">¿El evento sera relacionado a una guía contable existente?</label>
										<input type="checkbox" name="ExisteGuia" id="ExisteGuia" class="form-check-input" onclick="guiaExistente()" value = "SI"/>														
										
									</div>									
								</div>
							</div>
							
							<div class="row d-flex">																								
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="nGrupo" class="form-label">Grupo Guía</label>									
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-123"></i></span>
										<input type="number" name="nGrupo" id="nGrupo" class="form-control form-control-sm" value=""/>
										<input type="button" id="btnCatGrupoGuia" value="..." class="btn btn-secondary btn-sm" data-toggle="modal" data-target="#dialog-formGrupo"/>																			
									</div>
								</div>
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="nSubGrupo" class="form-label">Sub-Grupo Guía</label>									
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-123"></i></span>
										<input type="text" name="nSubGrupo" id="nSubGrupo" class="form-control form-control-sm" value="" maxlength="5"/>
										<input type="button" id="btnCatSubGrupoGuia" value="..." class="btn btn-secondary btn-sm" data-toggle="modal" data-target="#dialog-formSubGrupo"/>
									</div>
								</div>							
							</div>
							
							<div class="row d-flex">								
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>																
								<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-pencil-square"></i></span>
										<input type="text" name="dGrupoGuia" id="dGrupoGuia" class="form-control form-control-sm" />
									</div>
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="nSubGrupo" class="form-label">Descripcion</label>
								</div>												
								<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-pencil-square"></i></span>
										<input type="text" name="cDescripcion" id="cDescripcion" class="form-control form-control-sm" value="" maxlength="500"/>
										</div>									
								</div>
							</div>
							
							<br/>
							
							<div class="row d-flex">
								<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex justify-content-center">															
									<div class="form-check">
										<label for="op1" class="form-check-label">¿El consecutivo de la guia ya existe?</label>
										<input type="checkbox" name="ExisteConsecutivo" id="ExisteConsecutivo" class="form-check-input" onclick="consecutivoExistente()" value = "SI"/>														
										
									</div>									
								</div>
							</div>
							
							<div class="row d-flex">																								
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="nConsecutivo" class="form-label">Consecutivo</label>									
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-123"></i></span>
										<input type="number" name="nConsecutivo" id="nConsecutivo" class="form-control form-control-sm" value=""/>
										<input type="button" id="btnCatConsecutivo" value="..." class="btn btn-secondary btn-sm" data-toggle="modal" data-target="#dialog-formConsecutivo"/>
									</div>
								</div>
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="dConcepto" class="form-label">Concepto</label>									
								</div>						
								<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-pencil-square"></i></span>
										<textarea id="dConcepto" name="dConcepto" rows="3" cols="80" maxlength="1000" class="form-control form-control-sm"></textarea>
									</div>
								</div>										
							</div>
							
							<div class="row d-flex">				
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="dDocumentoFuente" class="form-label">Doc Fuente</label>									
								</div>
								<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-pencil-square"></i></span>
										<textarea id="dDocumentoFuente" name="dDocumentoFuente" rows="2" cols="80" maxlength="1000" class="form-control form-control-sm"></textarea>
									</div>
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<label for="dPeriodicidad" class="form-label">Periodicidad</label>									
								</div>
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-pencil-square"></i></span>
										<input type="text" name="dPeriodicidad" id="dPeriodicidad" class="form-control form-control-sm" value="" maxlength="100"/>
									</div>
								</div>
							</div>
							
							<center>
								<div class="row d-flex">																
									<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
										<input type="button" class="btn btn-secondary btn-sm" id="btnGuardaGuia" name="btnGuardaGuia" onclick="GuardaGuia()" value="Gruardar Guia">
									</div>
								</div>	
							</center>	
						</div>
					</div>
				</div>
				
				<div class="card-body">
					<div class="tab-pane fade" id="tabs-3-cuentas" role="tabpanel" aria-labelledby="tabs-cuentas">
						<div id="divConfiguracionCuentas">												
												
							<center>					
								<h6> <label style="color: #2471A3"> Selecciona el tipo de configuración a realizar </label> </h6>
								
								<div class="row d-flex">
									<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex justify-content-center">															
										<div class="form-check">
											<input type="radio" name="MODULO" id="CAJA" class="form-check-input" value = "1" onclick="seleccionaModulo(this)" checked/>								
											<label for="op1" class="form-check-label">Caja</label>
										</div>									
									</div>
									<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex justify-content-center">		
										<div class="form-check">								
											<input type="radio" name="MODULO" id="REINTEGROCAJA" class="form-check-input" value = "2" onclick="seleccionaModulo(this)"/>
											<label for="op2" class="form-check-label">Reintegros Caja</label>
										</div>
									</div>
									<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex justify-content-center">		
										<div class="form-check">								
											<input type="radio" name="MODULO" id="POLIZAMANUAL" class="form-check-input" value = "3" onclick="seleccionaModulo(this)"/>
											<label for="op2" class="form-check-label">Póliza Manual</label>
										</div>
									</div>
								</div>
							</center>
							
							<h6>Confuguración de Cuentas 
								<label style="font-size: 10pt" id="modC"> (Caja) </label>
								<label style="font-size: 10pt" id="modRC"> </label>
								<label style="font-size: 10pt" id="modPM"> </label>
							</h6>
							<hr class="mt-3"/>
												
							<div class="row d-flex">								
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>								
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<label for="aEjercicioFiscal" class="form-label">Ejercicio Fiscal</label>									
								</div>
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
										<select name="nEjercicioFiscal" id="nEjercicioFiscal" class="form-select form-select-sm" style="width: 10em;">
											<option value=""></option>																		
										</select>
									</div>	
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<label for="cTipoPoliza" class="form-label">Tipo Póliza</label>									
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-file-earmark"></i></span>
										<select name="cTipoPoliza" id="cTipoPoliza" class="form-select form-select-sm" value="" onchange="cambioTipoPoliza()">
											<option value="-1">Selecciona...</option>
											<option value="DI">Diario</option>
											<option value="EG">Egreso</option>
											<option value="IN">Ingreso</option>
										</select>
									</div>
								</div>
							</div>
							
							<div class="row d-flex">								
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>								
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<label for="nCuenta" class="form-label">Cuenta</label>									
								</div>
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-123"></i></span>
										<input type="text" readonly="readonly" name="nCuenta" id="nCuenta" class="form-control form-control-sm" value=""/>
										<input type="button" id="btnCatCuentas" value="..." class="btn btn-secondary btn-sm" data-toggle="modal" data-target="#dialog-formCuentas"/>										
									</div>	
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<label for="cTipoMovimiento" class="form-label">Tipo Movimiento</label>									
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<div class="input-group">								
										<span class="input-group-text"><i class="bi bi-pencil-square"></i></span>
										<select name="cTipoMovimiento" id="cTipoMovimiento" class="form-select form-select-sm" value="">
											<option value="-1">Selecciona...</option>
											<option value="CARGO">Cargo</option>
											<option value="ABONO">Abono</option>
										</select>
									</div>
								</div>
							</div>
							
							<div class="row d-flex">								
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
								</div>																
								<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
									<textarea readonly="readonly" id="dCuenta" name="dCuenta" rows="2" cols="80" class="form-control form-control-sm"></textarea>
								</div>
							</div>
									
							<div id="divCajaRC">															
								<center>
									<div class="row d-flex">																
										<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
											<input type="button" class="btn btn-secondary btn-sm" id="btnAgregarCuentasCRC" name="btnAgregarCuentasCRC" onclick="AgregarCuentasCRC()" value="Agregar">
										</div>
									</div>
								</center>
								
								<br/>
								
								<div id="cuentasCRC" class="table-responsive">	    
									<table id="tblCuentasCRC" class="table table-striped text-nowrap">
							            <thead>
							                <tr>
							                	<th>nDocRenglon</th>
							                	<th>Cuenta</th>
							                	<th>Movimiento</th>							                	
							                	<th>Modulo</th>							                							                   
							                </tr>
							            </thead>
							        </table>
								</div>								
							</div>
							
							<div id="divPolManual">								
								<div class="row d-flex">								
									<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									</div>								
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
										<label for="cPartida" class="form-label">Partida</label>									
									</div>
									<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
										<div class="input-group">								
											<span class="input-group-text"><i class="bi bi-123"></i></span>											
											<input readonly="readonly" type="text" name="cPartida" id="cPartida" onchange="creaDTCucop()" class="form-control form-control-sm"/>											
											<input type="button" id="btnCatPartidas" value="..." class="btn btn-secondary btn-sm" data-toggle="modal" data-target="#dialog-formPartida"/>			
										</div>	
									</div>
								</div>
								
								<div class="row d-flex">								
									<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
									</div>																
									<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
										<textarea readonly="readonly" id="dPartida" name="dPartida" rows="2" cols="80" class="form-control form-control-sm"></textarea>
									</div>
								</div>
								
								<center>
									<div class="row d-flex">																
										<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
											<input type="button" class="btn btn-secondary btn-sm" id="btnAgregarCuentasPM" name="btnAgregarCuentasPM" onclick="AgregarCuentasPM()" value="Agregar">
										</div>
									</div>
								</center>
								
								<br/>
								 
								<div id="cuentasPM" class="table-responsive">	    
									<table id="tblCuentasPM" class="table table-striped text-nowrap">
							            <thead>
							                <tr>
							                	<th>nDocRenglon</th>
							                	<th>Cuenta</th>							                	
							                	<th>Movimiento</th>
							                	<th>Partida</th>							                							                   
							                </tr>
							            </thead>
							        </table>
								</div> 
							</div>
							
							<br/>
							
							<center>
								<div class="row d-flex">																
									<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
										<input type="button" class="btn btn-secondary btn-sm" id="btnGuardaCuentas" name="btnGuardaCuentas" onclick="GuardaCuentas()" value="Guardar Configuracion">
									</div>
								</div>
							</center>
						</div>
					</div>
				</div>				  
		  	</div>	
		  	
		  	<div class="modal fade" id="dialog-formGrupo" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
				<div class="modal-dialog modal-lg"> <!-- Caja de dialogo -->
					<div class="modal-content"> <!-- Contenido de la caja -->
						  <div class="modal-header"> <!-- Encabezado de la caja -->
							<h5 class="modal-title">Selección del Grupo</h5>									          	
						  </div>
						  
						  <h6> <b>Selecciona la opcion dando doble clic en el renglon</b> </h6>
							
						<div class="modal-body"> <!-- Cuerpo de la caja -->
							<div class="row d-flex">								
								<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
									<table id="tblGrupoGuia" class="table table-striped table-bordered">
										<thead>
											<tr>
												<th>ID Grupo</th>
												<th>Nombre Grupo</th>												
											</tr>
										</thead>
									</table>
								</div>
							</div>
						</div>
						<div class="modal-footer"> <!-- Pie de pagina de la caja -->							
							<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>										    
						</div>
					</div>
				  </div>		
			</div>
			
			<div class="modal fade" id="dialog-formSubGrupo" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
				<div class="modal-dialog modal-lg"> <!-- Caja de dialogo -->
					<div class="modal-content"> <!-- Contenido de la caja -->
						  <div class="modal-header"> <!-- Encabezado de la caja -->
							<h5 class="modal-title">Selección del Sub-Grupo</h5>										          	
						  </div>
						  
						  <h6> <b>Selecciona la opcion dando doble clic en el renglon</b> </h6>
							
						<div class="modal-body"> <!-- Cuerpo de la caja -->
							<div class="row d-flex">								
								<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
									<table id="tblSubGrupoGuia" class="table table-striped table-bordered">
										<thead>
											<tr>
												<th>ID Grupo</th>
												<th>ID Sub-Grupo</th>
												<th>Nombre Sub-Grupo</th>												
											</tr>
										</thead>
									</table>
								</div>
							</div>
						</div>
						<div class="modal-footer"> <!-- Pie de pagina de la caja -->							
							<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>										    
						</div>
					</div>
				</div>		
			</div>
			
			<div class="modal fade" id="dialog-formConsecutivo" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
				<div class="modal-dialog modal-xl"> <!-- Caja de dialogo -->
					<div class="modal-content"> <!-- Contenido de la caja -->
						  <div class="modal-header"> <!-- Encabezado de la caja -->
							<h5 class="modal-title">Selección del Consecutivo</h5>										          	
						  </div>
						  
						  <h6> <b>Selecciona la opcion dando doble clic en el renglon</b> </h6>
							
						<div class="modal-body"> <!-- Cuerpo de la caja -->
							<div class="row d-flex">								
								<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
									<table id="tblConsecutivo" class="table table-striped table-bordered">
										<thead>
											<tr>
												<th>ID Grupo</th>
												<th>ID Sub-Grupo</th>
												<th>ID Consecutivo</th>
												<th>Concepto</th>
												<th>Doc Fuente</th>
												<th>Periodicidad</th>																							
											</tr>
										</thead>
									</table>
								</div>
							</div>
						</div>
						<div class="modal-footer"> <!-- Pie de pagina de la caja -->							
							<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>										    
						</div>
					</div>
				</div>		
			</div>
			
			<div class="modal fade" id="dialog-formCuentas" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
				<div class="modal-dialog modal-xl"> <!-- Caja de dialogo -->
					<div class="modal-content"> <!-- Contenido de la caja -->
						  <div class="modal-header"> <!-- Encabezado de la caja -->
							<h5 class="modal-title">Selección de la Cuenta Contable</h5>										          	
						  </div>
						  
						  <h6> <b>Selecciona la opcion dando doble clic en el renglon</b> </h6>
							
						<div class="modal-body"> <!-- Cuerpo de la caja -->
							<div class="row d-flex">								
								<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
									<table id="tblCuentas" class="table table-striped table-bordered">
										<thead>
											<tr>
												<th>Cuenta</th>
												<th>Descripcion</th>
												<th>Sub-Cuenta</th>																																				
											</tr>
										</thead>
									</table>
								</div>
							</div>
						</div>
						<div class="modal-footer"> <!-- Pie de pagina de la caja -->							
							<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>										    
						</div>
					</div>
				</div>		
			</div>
			
			<div class="modal fade" id="dialog-formPartida" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
				<div class="modal-dialog modal-xl"> <!-- Caja de dialogo -->
					<div class="modal-content"> <!-- Contenido de la caja -->
						  <div class="modal-header"> <!-- Encabezado de la caja -->
							<h5 class="modal-title">Selección de la Partida</h5>										          	
						  </div>
						  
						  <h6> <b>Selecciona la opcion dando doble clic en el renglon</b> </h6>
							
						<div class="modal-body"> <!-- Cuerpo de la caja -->
							<div class="row d-flex">								
								<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
									<table id="tblPartida" class="table table-striped table-bordered">
										<thead>
											<tr>
												<th>Partida</th>
												<th>Descripcion</th>																																				
											</tr>
										</thead>
									</table>
								</div>
							</div>
						</div>
						<div class="modal-footer"> <!-- Pie de pagina de la caja -->							
							<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>										    
						</div>
					</div>
				</div>		
			</div>
							
		</div>																					
	</form>

</body>
</html>
