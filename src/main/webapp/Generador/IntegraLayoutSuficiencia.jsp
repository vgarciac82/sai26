<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%
	String cCentroContable = "";
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String msg = "";
	if( session.getAttribute("RESULT") != null ){
		msg = (String)session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
		
	
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	
	String cUE_Usuario = "";
	cUE_Usuario = usuario.getU_UR();
	String U_LOGIN = "";
	U_LOGIN = usuario.getLogin();
%>
<!DOCTYPE html>
<html>
	<head>
		<title>Layout Suficiencia</title>


  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.datatables.net/1.13.8/css/jquery.dataTables.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css"/>

  	<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>	
  	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  	<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
  	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
		
	</head>
		  	<body id="dt_example"  >
		  	<br/>  	
				<div class="mx-4">
					<form id="envioSICOP" action="../gstnmngr/generaLayoutSuficiencia" method="post">
						<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
				    	<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= cUE_Usuario%>" />
				    	<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
				    	<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%= U_LOGIN%>"/>
				    	<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable"  value="<%= cUE_Usuario%>"/>
				    	<input type="hidden" name="cUR" id="cUR"  value=""/>
				    	<input type="hidden" id="foliosCompromisos" name="foliosCompromisos" />
						<input type="hidden" id="foliosSuficiencia" name="foliosSuficiencia" />
						<input type="hidden" id="foliosCalendario" name="foliosCalendario" />
						<input type="hidden" id="tipoLayout" name="tipoLayout"/>
						<input type="hidden" id="reimprimir" name="reimprimir" value="0"/> 
						<input type="hidden" id="sAuxiliarComodin" name="sAuxiliarComodin"/>
						<input type="hidden" id="folioCompromisoSnd"name="folioCompromisoSnd" value="">
						<input type="hidden" id="NoFolioSICOPSnd"name="NoFolioSICOPSnd" value="">
						<input type="hidden" id="cxp" name="cxp" />
						<input type="hidden" name="cNombreEmpleado" id="cNombreEmpleado" value=""/>
						<input type="hidden" name="cPaternoEmpleado" id="cPaternoEmpleado" value=""/>
						<input type="hidden" name="cMaternoEmpleado" id="cMaternoEmpleado" value=""/>
						<input type="hidden" name="cPuestoEmpleado" id="cPuestoEmpleado" value=""/>
						<input type="hidden" name="cContrato" id="cContrato" value=""/>
						<input type="hidden" name="coIntegrada" id="coIntegrada" value=""/>
						<input type="hidden" name="tieneFirmante" id="tieneFirmante" value=""/>
						<input type="hidden" name="idCompromiso" id="idCompromiso" value=""/>
						<input type="hidden" name="foliosIntegradosD" id="foliosIntegradosD" value=""/>
						<input type="hidden" name="nNumEmpleadoBusqueda" id="nNumEmpleadoBusqueda" value=""/>
						<input type="hidden" name="cTipoFirmante" id="cTipoFirmante" value="AUT"/>
						<input type="hidden" id="cDocumento" name="cDocumento" value="COMPROMISO"/> 
						<input type="hidden" name="esIntegrado" id="esIntegrado" value=""/>
						<input type="hidden" name="tab_calendario" id="tab_calendario" value=""/>
						<input type="hidden" name="esCalendario" id="esCalendario" value=""/>
						<input type="hidden" name="caNoCompromiso" id="caNoCompromiso" value=""/>
						<input type="hidden" name="integradaRef" id="integradaRef" value=""/>
						
					</form>
			<div class="card-header"> <h3> Generar Layout Suficiencia / Compromisos </h3> </div>
			<br>
		    <ul class="nav nav-tabs" id="TabSuf" role="tablist">
		        <li class="nav-item" role="presentation">
		            <button class="nav-link active" id="pendientes-suf-tab" data-bs-toggle="tab" data-bs-target="#pendientes-suf" type="button" role="tab">Generar suficiencia</button>
		        </li>
		        <li class="nav-item" role="presentation">
		            <button class="nav-link" id="generado-suf-tab" data-bs-toggle="tab" data-bs-target="#generado-suf" type="button" role="tab">Suficiencias No autorizadas</button>
		        </li>
		        <li class="nav-item" role="presentation">
		            <button class="nav-link" id="pendientes-comp-tab" data-bs-toggle="tab" data-bs-target="#pendientes-comp" type="button" role="tab">Generar compromiso</button>
		        </li>
		        <li class="nav-item" role="presentation">
		            <button class="nav-link" id="comp-calendario-tab" data-bs-toggle="tab" data-bs-target="#comp-calendario" type="button" role="tab">Compromisos Calendario</button>
		        </li>
		        <li class="nav-item" role="presentation">
		            <button class="nav-link" id="generado-comp-tab" data-bs-toggle="tab" data-bs-target="#generado-comp" type="button" role="tab">Compromisos No autorizados</button>
		        </li>
		        <li class="nav-item" role="presentation">
		            <button class="nav-link" id="generado-pago-tab" data-bs-toggle="tab" data-bs-target="#generado-pago" type="button" role="tab">Regenerar layout pagos</button>
		        </li>
		    </ul>
		
		    <div class="tab-content mt-3" id="tabsLayouts">
		        <!-- TAB 1 -->
		        <div class="tab-pane fade show active" id="pendientes-suf" role="tabpanel">
		        	<div class="row">
			        	<div class="col-sm-2 col-md-4  col-lg-6" align="left">	
							<label for = "cboUnidadEjecutora">Unidad Ejecutora: </label>
							<select class="form-select" name="cboUnidadEjecutora" id="cboUnidadEjecutora" onchange="buscaSuficienciasPendientes();">
				    				<option value = "*" >TODAS LAS UNIDADES</option>
				    		</select>
						</div>
					</div>
					<h5 class="mt-3"> Tipo de Compromiso </h5>
							<hr class="mt-3"/>
					        
					        <div class="row d-flex">								
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<input type="radio" id="checkCompromiso" name="rTipoIntegracion" class="form-check-input" value="COMP" onclick="llenaSuficiencia();"> Suficiencia
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<input type="radio" id="checkDesc" name=rTipoIntegracion class="form-check-input" value="INTEGRA" onclick="llenaSuficiencia();"> Descentralizado
								</div>
							</div>
		            <div class="form-check mb-2 mt-2">
		                <input class="form-check-input" type="checkbox" id="selectAllSuf">
		                <label class="form-check-label" for="selectAllSuf">Seleccionar todos</label>
		            </div>
		            <table id="tablaPendientesSuf" class="table table-bordered table-striped">
		            	<colgroup>
					        <col style="width: 10px;">
					        <col style="width: 10px;">
					        <col>
					        <col>
					        <col>
					        <col>
					        <col>
					        <col>
					        <col style="width: 400px;">
					        <col style="width: 10px;">
					        <col>
					        <col>
					        <col style="width: 10px;">
					    </colgroup>
		                <thead>
		                <tr>
		                    <th></th>
		                    <th>Folio</th>
		                    <th>Fecha</th>
		                    <th>Contrato</th>
		                    <th>Origen</th>
		                    <th>UR</th>
		                    <th>Importe</th>
		                    <th>Contrarrecibo</th>
		                    <th style="width:150px;">Descripción</th>
		                    <th>Tipo</th>
		                    <th>Des</th>
		                    <th>Partida</th>
		                    <th>cal</th>
		                </tr>
		                </thead>
		                <tbody></tbody>
		            </table>
		            <br>
		            <div class="row">
		            	<div class="col-sm-2 col-md-4  col-lg-6" align="left">	
							<button class="btn btn-secondary" id="btnGenerarSuf">Generar Layout Suficiencia</button>
						</div>
					</div>
		        </div>
		
		        <!-- TAB 2 -->
		        <div class="tab-pane fade" id="generado-suf" role="tabpanel">
		        	
		        	<h5 class="mt-3"> Tipo de Compromiso </h5>
							<hr class="mt-3"/>
					        
					        <div class="row d-flex">								
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<input type="radio" id="checkCompromiso2" name="rTipoIntegracionS" class="form-check-input" value="COMP" onclick="noAutorizadosSuficiencia();"> Suficiencia
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<input type="radio" id="checkDesc2" name="rTipoIntegracionS" class="form-check-input" value="INTEGRA" onclick="noAutorizadosSuficiencia();"> Descentralizado
								</div>
							</div>
		        	 <div class="form-check mt-2">
		                <input class="form-check-input" type="checkbox" id="selectAllGenSuf">
		                <label class="form-check-label" for="selectAllGenSuf">Seleccionar todos</label>
		            </div>
		            <br>
		            <table class="table table-bordered table-striped" id="tablaSufGenerado">
		            	<colgroup>
					        <col style="width: 10px;">
					        <col style="width: 10px;">
					        <col>
					        <col>
					        <col>
					        <col>
					        <col>
					        <col>
					        <col style="width: 400px;">
					        <col>
					        <col style="width: 10px;">
					        <col>
					        <col style="width: 10px;">
					        <col>
					        <col style="width: 10px;">
					    </colgroup>
		                <thead>
		                <tr>
		                	<th>ID</th>
		                    <th>Folio</th>
		                    <th>Fecha</th>
		                    <th>Contrato</th>
		                    <th>Folio Interno</th>
		                    <th>UR</th>
		                    <th>Importe</th>
		                    <th>Contrarrecibo</th>
		                    <th>Descripción</th>
		                    <th>Folio Suf</th>
		                    <th>Tipo</th>
		                    <th>Acciones</th>
		                    <th>Des</th>
		                    <th>Partida</th>
		                    <th>cal</th>
		                </tr>
		                </thead>
		                <tbody >
		                </tbody>
		            </table>
		            <br>
		            <button class="btn btn-secondary" id="btnRegenerarSuf">Regenerar Layout Suficiencia</button>
		        </div>
		
		        <!-- TAB 3 -->
		        <div class="tab-pane fade" id="pendientes-comp" role="tabpanel">
		       		 <div class="row">
			        	<div class="col-sm-2 col-md-4  col-lg-6" align="left">	
							<label for = "cboUnidadEjecutora2">Unidad Ejecutora: </label>
							<select class="form-select" name='cboUnidadEjecutora2' id='cboUnidadEjecutora2' onchange="buscaCompromisosPendientes();">
				    				<option value = "*" >TODAS LAS UNIDADES</option>
				    		</select>
						</div>
					</div>
					<h5 class="mt-3"> Tipo de Compromiso </h5>
							<hr class="mt-3"/>
					        
					        <div class="row d-flex">								
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<input type="radio" id="checkCompromiso3" name="rTipoIntegracionC" class="form-check-input" value="COMP" onclick="llenaCompromisos();"> Centralizados
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<input type="radio" id="checkDesc3" name="rTipoIntegracionC" class="form-check-input" value="INTEGRA" onclick="llenaCompromisos();"> Descentralizado
								</div>
							</div>
		            <div class="form-check mb-2 mt-2">
		                <input class="form-check-input" type="checkbox" id="selectAllComp">
		                <label class="form-check-label" for="selectAllComp">Seleccionar todos</label>
		            </div>
		            <table class="table table-bordered table-striped mt-2" id="tablaPendientesComp">
		                <colgroup>
					        <col style="width: 10px;">
					        <col style="width: 10px;">
					        <col style="width: 300px;">
					        <col>
					        <col>
					        <col>
					        <col>
					        <col>
					        <col>
					        <col style="width: 8px;">
					        <col>
					        <col>
					    </colgroup>
		                <thead>
			                <tr>
							    <th>ID</th>
							    <th>Contrarrecibo</th>
							    <th>Contrato</th>
							    <th>Origen</th>
							    <th>Estado</th>
							    <th>Fecha</th>
							    <th>Ramo</th>
							    <th>UR</th>
							    <th>Importe</th>
							    <th>RFC</th>
							    <th>Folio Suf</th>
							    <th>Tipo Doc</th>
							    <th>Tipo Pago</th>
							    <th>D</th>
							</tr>
		                </thead>
		                <tbody>
		                </tbody>
		            </table>
		            <br>
		            <button class="btn btn-primary mt-3" id="btnGeneraLayout">Generar Layout Compromiso</button>
		            <button class="btn btn-secondary mt-3" id="btnExtraerExpediente">Extraer expediente</button>
		            
		        </div>
		
		        <!-- TAB 4 -->
		        <div class="tab-pane fade" id="generado-comp" role="tabpanel">
		        	<h5 class="mt-3"> Tipo de Compromiso </h5>
							<hr class="mt-3"/>
					        
					        <div class="row d-flex">								
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<input type="radio" id="checkCompromiso4" name="rTipoIntegracionCG" class="form-check-input" value="COMP" onclick="noAutorizadosCompromiso();"> Centralizados
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<input type="radio" id="checkDesc4" name="rTipoIntegracionCG" class="form-check-input" value="INTEGRA" onclick="noAutorizadosCompromiso();"> Descentralizado
								</div>
							</div>
		        	<div class="form-check mb-2 mt-2">
		                <input class="form-check-input" type="checkbox" id="selectAllGenCom">
		                <label class="form-check-label" for="selectAllGenCom">Seleccionar todos</label>
		            </div>
		        	<br>
		            <table class="table table-bordered table-striped" id="tablaCompGenerados">
		                <colgroup>
					        <col style="width: 10px;">
					        <col style="width: 10px;">
					        <col style="width: 300px;">
					        <col>
					        <col>
					        <col>
					        <col>
					        <col>
					        <col>
					        <col style="width: 8px;">
					        <col>
					        <col>
					    </colgroup>
		                <thead>
		                <tr>
		                	<th>Id</th>
		                    <th>Contrarrecibo</th>
		                    <th>Contrato</th>
		                    <th>Tipo Doc</th>
		                    <th>Estado</th>
		                    <th>Fecha</th>
		                    <th>Ramo</th>
		                    <th>UR</th>
		                    <th>Importe</th>
		                    <th>RFC</th>
		                    <th>Folio Suf</th>
		                    <th>Tipo</th>
		                    <th>Acción</th>
		                    <th>D</th>
		                    <th>Cal</th>
		                </tr>
		                </thead>
		                <tbody>
		                </tbody>
		            </table>
		            <button class="btn btn-primary mt-3" id="btnRegenerarComp">Regenerar Layout Compromiso</button>
		            <button class="btn btn-secondary mt-3" id="btnExtraerExpediente2">Extraer expediente</button>
		            
		        </div> 
		         <!-- TAB 5 -->
		        <div class="tab-pane fade" id="comp-calendario" role="tabpanel">
		       		 
		            <div class="form-check mb-2 mt-2">
		                <input class="form-check-input" type="checkbox" id="selectAllCalendario">
		                <label class="form-check-label" for="selectAllCalendario">Seleccionar todos</label>
		            </div>
		            <table class="table table-bordered table-striped mt-2" id="tablaCompCalendario">
		                <thead>
			                <tr>
							    <th>ID</th>
							    <th>Contrarrecibo</th>
							    <th>Contrato</th>
							    <th>Origen</th>
							    <th>Estado</th>
							    <th>Fecha</th>
							    <th>Ramo</th>
							    <th>UR</th>
							    <th>Importe</th>
							    <th>RFC</th>
							    <th>Folio Suf</th>
							    <th>Tipo Doc</th>
							    <th>Imp</th>
							</tr>
		                </thead>
		                <tbody>
		                </tbody>
		            </table>
		            <br>
		            <button class="btn btn-primary mt-3" id="btnGeneraLayoutCalendario">Generar Compromiso Calendario</button>
		        </div>
		
		        <!-- TAB 6 -->
		        <div class="tab-pane fade" id="generado-pago" role="tabpanel">
		        	<h5> Regenerar layout de pagos integrados</h5>
		        	<div class="form-check mb-2 mt-2">
		                <input class="form-check-input" type="checkbox" id="selectAllGenPago">
		                <label class="form-check-label" for="selectAllGenPago">Seleccionar todos</label>
		            </div>
		        	<br>
		            <table class="table table-bordered table-striped" id="tablaPagosGenerados">
		                <thead>
		                <tr>
		                	<th>Id</th>
		                    <th>Contrarrecibo</th>
		                    <th>Contrato</th>
		                    <th>Tipo Doc</th>
		                    <th>Estado</th>
		                    <th>Fecha</th>
		                    <th>Ramo</th>
		                    <th>UR</th>
		                    <th>RFC</th>
		                    <th>Folio Suf</th>
		                </tr>
		                </thead>
		                <tbody>
		                </tbody>
		            </table>
		            <button class="btn btn-primary mt-3" id="btnRegenerarPago">Regenerar Layout Pagos</button>		            
		        </div> 
		    </div>
		</div>
		
<div class="modal fade" id="dialog-CapturaFolio" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
	<div class="modal-dialog"> <!-- Caja de dialogo -->
		<div class="modal-content"> <!-- Contenido de la caja -->
			  <div class="modal-header"> <!-- Encabezado de la caja -->
				<h5 class="modal-title">Ingrese informacion Suficiencia</h5>
				<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
			  </div>
			<div class="modal-body"> <!-- Cuerpo de la caja -->
				<div class="row d-flex">								
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">					
						<label for="integracion" class="form-label"> Contrato / Integración: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="integracion" name="integracion" value="" readonly/>
			        </div>						
				</div>		
				<div class="row d-flex">								
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">					
						<label for="folioCompromisoTxt" class="form-label"> Id Compromiso: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="folioCompromisoTxt" name="folioCompromisoTxt" value="" readonly/>
			        </div>						
				</div>	
				<div class="row d-flex">								
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">					
						<label for="NoFolioSICOP" class="form-label"> #Folio Suficiencia SICOP: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="NoFolioSICOP" name="NoFolioSICOP" value=""/>
			        </div>						
				</div>				
			</div>
			<div class="modal-footer"> <!-- Pie de pagina de la caja -->
				<button type="button" id="aceptarFolioSICOP" class="btn btn-primary" onclick="actualizaFolioSICOP();" >Aceptar</button>
				<button type="button" class="btn btn-secondary" onclick="limpiaValores();" data-bs-dismiss="modal">Cancelar</button>										    
			</div>
		</div>
	</div>		
</div>


<div class="modal fade" id="firmantesModal" tabindex="-1" aria-labelledby="firmantesModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-scrollable">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="firmantesModalLabel">Firmantes</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body">
        <div class="container-fluid">
          <div class="row mb-2">
            <div class="col-12" id="AutorizaConFielTD" style="display: none">
              <input type="checkbox" id="autorizadoPorFielChk" name="autorizadoPorFielChk" onclick="autorizadoPorFielAction()">
              <label for="autorizadoPorFielChk">Autorizar con Firma Electrónica</label>
            </div>
          </div>
          <p><b>Datos de los firmantes</b></p>
          <div class="row mb-2">
            <div class="col-12">
              Autoriza:
              <select id="cboAutoriza" name="cboAutoriza" onchange="infoEmpleado('AUT');" class="form-select"></select>
            </div>
          </div>
        </div> 

      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-bs-dismiss="modal" onclick="imprimirNotaNuevo();">Aceptar</button>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="conceptoModal" tabindex="-1" aria-labelledby="conceptoModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-scrollable">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="conceptoModalLabel">Nota Informativa</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body">
        <div class="container-fluid">
          <p><b>Concepto por:</b></p>
          <div class="row mb-2">
            <div class="col-12">
              <input type="text" class="form-control form-control-sm" id="conceptoContrato" name="conceptoContrato" value=""/>
            </div>
          </div>
        </div> 
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-bs-dismiss="modal" onclick="imprimirCompromiso();">Aceptar</button>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
      </div>
    </div>
  </div>
</div>
	
		
<div class="modal fade" id="dialog-AutorizaComp" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
	<div class="modal-dialog"> <!-- Caja de dialogo -->
		<div class="modal-content"> <!-- Contenido de la caja -->
			  <div class="modal-header"> <!-- Encabezado de la caja -->
				<h5 class="modal-title">Ingrese informacion para autorizar compromiso</h5>
				<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
			  </div>
			<div class="modal-body"> <!-- Cuerpo de la caja -->
				<div class="row d-flex">								
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">					
						<label for="integracionC" class="form-label"> Contrato: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="integracionC" name="integracionC" value="" readonly/>
			        </div>						
				</div>		
				<div class="row d-flex">								
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">					
						<label for="folioCompromisoC" class="form-label"> Id Compromiso: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="folioCompromisoC" name="folioCompromisoC" value="" readonly/>
			        </div>						
				</div>	
				<div class="row d-flex">								
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">					
						<label for="nFolioAutSICOP" class="form-label"> #Folio Autorización SICOP: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<input type="text" class="form-control form-control-sm" id="nFolioAutSICOP" name="nFolioAutSICOP" value=""/>
			        </div>						
				</div>				
			</div>
			<div class="modal-footer"> <!-- Pie de pagina de la caja -->
				<button type="button" id="capturaFolioSICOP" class="btn btn-primary" onclick="autorizaCompromiso();" >Aceptar</button>
				<button type="button" class="btn btn-secondary" onclick="limpiaValoresComp();" data-bs-dismiss="modal">Cancelar</button>										    
			</div>
		</div>
	</div>		
</div>
		<script type="text/javascript" charset="utf-8">
			var msg = "<%=msg%>";
			var modalFolioSICOP;
			var modalFirmantes;
			var modalConcepto;
			var modalAutoriza;
			var calendario;
			$(document).ready(function() {
				 
					modalFolioSICOP = new bootstrap.Modal(document.getElementById('dialog-CapturaFolio'), 'data-bs-backdrop');
					modalFirmantes = new bootstrap.Modal(document.getElementById('firmantesModal'), 'data-bs-backdrop');
					modalConcepto = new bootstrap.Modal(document.getElementById('conceptoModal'), 'data-bs-backdrop');
					modalAutoriza = new bootstrap.Modal(document.getElementById('dialog-AutorizaComp'), 'data-bs-backdrop');
					
					const options = {
							bRetrieve: true,
					        language: es_mx,
					        paging: true,
					        searching: true,
					        autoWidth: false
					    };
					const optsSinPaginacion = Object.assign({}, options, { paging: false , searching: false, "bInfo" : false });

				    if (msg!="") {
						   Swal.fire({
							  title: 'Revise',
							  text: msg,
							  icon: 'error'
							})
				    }
					
				    $("#comp-calendario-tab").closest("li").hide();
			        $("#comp-calendario").hide();
				 
				    queryFormPost("visualizaTabCompromisoCalendario", {async: false});				    
				    if ( $("#tab_calendario").val() > 0 ) {
				    	 $("#comp-calendario-tab").closest("li").show();
				         $("#comp-calendario").show();
				       
					     $("#pendientes-comp-tab").closest("li").hide();
					     $("#pendientes-comp").hide();
					     
					     calendario ="'S'";
				    } 	else {
				    	 calendario ="'N'";
				    }
				    
					querySelectPost("FirmantesPorTipo_Read", "cboAutoriza", {
						async : false
					});
				    
				    datosUnidadEjecutora();	
				    var unidad = document.getElementById("cIdUnidadEjecutoraUsuario").value;
				    document.querySelector('input[name="rTipoIntegracion"][value="COMP"]').checked = true;
				    document.querySelector('input[name="rTipoIntegracionS"][value="COMP"]').checked = true;
				    document.querySelector('input[name="rTipoIntegracionC"][value="COMP"]').checked = true;
				    document.querySelector('input[name="rTipoIntegracionCG"][value="COMP"]').checked = true;

				    //if (unidad !== "A02") {
				         $("#generado-pago-tab").closest("li").hide();
				         $("#generado-pago").hide();
				    //}
						// Inicializa tabla visible por defecto
					    $('#tablaPendientesSuf').DataTable(options);
	
						buscaSuficienciasPendientes();
						
					    
					 	// Inicializar al cambiar de tab (solo una vez)
				    	let initialized = {};


					    $('button[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
					        const target = $(e.target).data('bsTarget');

					        switch (target) {
					            case '#generado-suf':
					                if (!initialized['tablaSufGenerado']) {
					                	$('#tablaSufGenerado').DataTable(optsSinPaginacion);
					                    initialized['tablaSufGenerado'] = true;
					                } else {
					                    $('#tablaSufGenerado').DataTable().columns.adjust().draw();
					                }
					                
					                noAutorizadosSuficiencia();
					                
					                if ($("#selectAllGenSuf").is(":checked")) { 
					            	    $("#selectAllGenSuf").prop("checked", false);
					            	}
					                break;

					            case '#pendientes-comp':
					                if (!initialized['tablaPendientesComp']) {
					                    $('#tablaPendientesComp').DataTable(options);
					                    initialized['tablaPendientesComp'] = true;
					                }
					                buscaCompromisosPendientes()
					                if ($("#selectAllComp").is(":checked")) { 
					            	    $("#selectAllComp").prop("checked", false);
					            	}
					                break;
					            case '#generado-comp':
					                if (!initialized['tablaCompGenerados']) {
					                    $('#tablaCompGenerados').DataTable(optsSinPaginacion);
					                    initialized['tablaCompGenerados'] = true;
					                } else {
					                    $('#tablaCompGenerados').DataTable().columns.adjust().draw();
					                }
					                
					                noAutorizadosCompromiso(); 
					                
					                if ($("#selectAllGenCom").is(":checked")) { 
					            	    $("#selectAllGenCom").prop("checked", false);
					            	}
					                break;
					            case '#pendientes-suf':
					            	if ($("#selectAllSuf").is(":checked")) { 
					            	    $("#selectAllSuf").prop("checked", false);
					            	}
					            	buscaSuficienciasPendientes();
					            	break;
					            	
					            case '#comp-calendario':
					            	if ($("#selectAllCalendario").is(":checked")) { 
					            	    $("#selectAllCalendario").prop("checked", false);
					            	}
					            	generaTblCalendario();
					            	break;
					        }
					    });
					    
					
					    $("#btnGenerarSuf").on("click", function () {
					    	$("#tipoLayout").val(1);
					    	$("#reimprimir").val(0);
					    	enviarLayoutSuf();
					    });
					    
					    $("#btnRegenerarSuf").on("click", function () {
					    	$("#tipoLayout").val(1);
					    	$("#reimprimir").val(1);
					    	reenviarLayoutSuf();
					    });
					    
					    $("#btnGeneraLayout").on("click", function () {
					    	$("#tipoLayout").val(2);
					    	$("#reimprimir").val(0);
					    	enviarLayoutCompromiso();
					    });
					    
					    $("#btnRegenerarComp").on("click", function () {
					    	$("#tipoLayout").val(2);
					    	$("#reimprimir").val(1);
					    	reenviarLayoutCompromiso();
					    });
					    
					    $("#btnGeneraLayoutCalendario").on("click", function () {
					    	$("#tipoLayout").val(4);
					    	$("#reimprimir").val(0);
					    	enviarLayoutCalendario();
					    });
					
			});
			 
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
			 
		    // Seleccionar todos en el tab 1
		    $("#selectAllSuf").on("change", function () {
		        const checked = $(this).is(":checked");
		        $("#tablaPendientesSuf input[type=checkbox]").prop("checked", checked);
		        
		    });
		    
		    $("#selectAllComp").on("change", function () {
		        const checked = $(this).is(":checked");
		        $("#tablaPendientesComp input[type=checkbox]").prop("checked", checked);
		    });
		
		    $("#selectAllGenSuf").on("change", function () {
		        const checked = $(this).is(":checked");
		        $("#tablaSufGenerado input[type=checkbox]").prop("checked", checked);
		    });
		    
		    $("#selectAllGenCom").on("change", function () {
		        const checked = $(this).is(":checked");
		        $("#tablaCompGenerados input[type=checkbox]").prop("checked", checked);
		    });
		    
		    $("#selectAllCalendario").on("change", function () {
		        const checked = $(this).is(":checked");
		        $("#tablaCompCalendario input[type=checkbox]").prop("checked", checked);
		    });
		    
		    
		    function buscaSuficienciasPendientes(){
				$("#cUR").val($("#cboUnidadEjecutora").val());
				llenaSuficiencia();

			}
		    
		    function infoEmpleado( tipoFirmante ){
		    	var postFijo;
				
				if( "AUT" == tipoFirmante){
					numeroEmpleado = $("#cboAutoriza").val();
					postFijo = "Aut";
				}else if( "SUPAUT" == tipoFirmante){
					numeroEmpleado = $("#cboSuplenteAut").val();
					postFijo = "Titular";
				}
				
				limpiaFirmante(postFijo);
				
				if( parseInt( numeroEmpleado, 10 ) > 0 ){
					$("#nNumEmpleadoBusqueda").val( numeroEmpleado );		
					queryFormPost({ queryName:"infoComplementariaFirmanteRead", 
					                    async:false,
					              });
				} 

			}
		    
		    function limpiaFirmante(postFijo){
				$("#cNombreEmpleado").val( "" );
		        $("#cPaternoEmpleado").val( "" );
		        $("#cMaternoEmpleado").val( "" );
		        $("#cPuestoEmpleado").val( "" );
			}
			
		    
		    function llenaSuficiencia(){
		    	var tipoIntegra=$("input[name='rTipoIntegracion']:checked").val();
				var cUR = $("#cUR").val();
				var cWhereUR = "";
				var esAdmin = $("#isAdmin").val();
				var cUE_Usuario = $("#cIdUnidadEjecutoraUsuario").val();
				
				if (cUR != "*" && cUR != "**"){
					cWhereUR = " AND cUnidadResponsable = '" + cUR + "'";
				} else if (cUR == "**") {
					cWhereUR += " AND cUnidadResponsable in ('G01', 'G02', 'G03', 'G04', 'G05', 'G06', 'G07', 'G08', 'G09', 'G10', 'G11', 'G12', 'G13', 'G14', 'G15', 'G16', 'G17', 'G18', 'G19', 'G20', 'G21', 'G22', 'G23', 'G24', 'G25', 'G26', 'G27', 'G28', 'G29', 'G30', 'G31', 'G32')";
				}
				
				if(cUE_Usuario == "A02"){
					cWhereUR += " AND (cTipoContrato IN ('FE', 'RE', 'RI', 'PD', 'AV', 'CL') )";
				}else if(cUE_Usuario == "A04"){
					cWhereUR += " AND cTipoContrato NOT IN ('FE', 'RE', 'RI', 'PD', 'AV', 'CL')";
				}	
				
				if (tipoIntegra=="COMP") {
					cWhereUR += " AND esIntegrado = 0 ";
				} else {
					cWhereUR += " AND esIntegrado = 1 ";
				}
				
				
				$('#tablaPendientesSuf').DataTable({
					bRetrive: true,
					bDestroy: true,
					oLanguage: es_mx,
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" 
								+ window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_SuficienciaPendiente&qw=nEnviadoSicopSuficiencia=0 "+ cWhereUR + " AND ( (Origen = 'DECREMENTO' AND nFolioAutSICOP IS NOT NULL)  OR (Origen != 'DECREMENTO')) AND cEsCalendario = " + calendario ,
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					order: [[1, 'asc']],
					aoColumns: [
						{ sName: "id",					bSearchable: false, bSortable: false, bVisible: true},
						{ sName: "nFolioCompromiso",	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "fCarga",				bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cIdContrato",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "Origen",				bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cUnidadResponsable",	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "Importe"			,	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "folioInterno"		,	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "Descripcion",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cTipoContrato",		bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "esIntegrado",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "partida",				bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cEsCalendario",		bSearchable: true, bSortable: false, bVisible: true}
					]
				});
				
			}
		    
		    var oTableSufLayout;
		    function noAutorizadosSuficiencia(){ 
		    	var tipoIntegra=$("input[name='rTipoIntegracionS']:checked").val();
		    	var cWhereUR ="";
		    	var cUE_Usuario = $("#cIdUnidadEjecutoraUsuario").val();
		    	if(cUE_Usuario == "A02"){
		    						cWhereUR += " AND (cTipoContrato IN ('FE', 'RE', 'RI', 'PD', 'AV', 'CL') )";
		    					}else if(cUE_Usuario == "A04"){
		    						cWhereUR += " AND cTipoContrato NOT IN ('FE', 'RE', 'RI', 'PD', 'AV', 'CL')";
		    					}	
		    	if (tipoIntegra=="COMP") {
					cWhereUR += " AND esIntegrado = 0 ";
				} else {
					cWhereUR += " AND esIntegrado = 1 ";
				}
				
		    	oTableSufLayout = $('#tablaSufGenerado').DataTable({
					bRetrive: true,
					bDestroy: true,
					oLanguage: es_mx,
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" 
								+ window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_SuficienciaPendiente&qw=nEnviadoSicopSuficiencia=1 "+ cWhereUR + " AND ( (Origen = 'DECREMENTO' AND nFolioAutSICOP IS NOT NULL)  OR (Origen != 'DECREMENTO')) AND cEsCalendario = " + calendario  ,
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					order: [[1, 'asc']],
					aoColumns: [
						{ sName: "id",					bSearchable: false, bSortable: false, bVisible: true},
						{ sName: "nFolioCompromiso",	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "fCarga",				bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cIdContrato",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "Origen",				bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cUnidadResponsable",	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "Importe"			,	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "folioInterno"		,	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "Descripcion",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "nFolioSuficiencia",	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cTipoContrato",		bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "bImprimir",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "esIntegrado",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "partida",				bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cEsCalendario",		bSearchable: true, bSortable: false, bVisible: true}
					]
				});
				
		    	$("#tablaSufGenerado tbody").on("dblclick", "tr", function(event) {
		    		limpiaValores();
		    		
		    		var row = oTableSufLayout.row($(this));
		    		var data = row.data();
		    		
		    		var compromiso = data[1];
		    		var contrato = data[3];
		    		var folioSicop = data[9];
		    		
		    		$("#integracion").val(contrato);
		    		$("#folioCompromisoTxt").val(compromiso);
		    		$("#folioCompromisoSnd").val(compromiso);
		    		$("#NoFolioSICOP").val(folioSicop);
		    		
		    		modalFolioSICOP.show();
		    	});
			}
			
		    function datosUnidadEjecutora(){
				
				queryFormPost("validaUsuarioCentralesMAT_Read", {async: false});
				querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cboUnidadEjecutora", {async: false});
				querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cboUnidadEjecutora2", {async: false});
				querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cboUnidadEjecutora3", {async: false});
				$("#cboUnidadEjecutora").val($("#cIdUnidadEjecutoraUsuario").val());
				$("#cboUnidadEjecutora2").val($("#cIdUnidadEjecutoraUsuario").val());
				$("#cboUnidadEjecutora3").val($("#cIdUnidadEjecutoraUsuario").val());
				
				if($("#isAdmin").val() == "0"){
					$("#cboUnidadEjecutora").prepend("<option value='*'> * - OFICINAS CENTRALES</option>");
					$("#cboUnidadEjecutora").prepend("<option value='**'> ** - GERENCIAS ESTATALES</option>");
					$("#cboUnidadEjecutora2").prepend("<option value='*'> * - OFICINAS CENTRALES</option>");
					$("#cboUnidadEjecutora2").prepend("<option value='**'> ** - GERENCIAS ESTATALES</option>");
					$("#cboUnidadEjecutora3").prepend("<option value='*'> * - OFICINAS CENTRALES</option>");
					$("#cboUnidadEjecutora3").prepend("<option value='**'> ** - GERENCIAS ESTATALES</option>");
				}
				
			}
		    
		    function buscaCompromisosPendientes(){
				$("#cUR").val($("#cboUnidadEjecutora2").val());
				llenaCompromisos();
				
			}
		    
		    function llenaCompromisos(){
		    	var tipoIntegra=$("input[name='rTipoIntegracionC']:checked").val();
				var cUR = $("#cUR").val();
				var cWhereUR = "";
				var esAdmin = $("#isAdmin").val();
				var cUE_Usuario = $("#cIdUnidadEjecutoraUsuario").val();
				
				if (cUR != "*" && cUR != "**"){
					cWhereUR = " AND cUnidadResponsable = '" + cUR + "'";
				} else if (cUR == "**") {
					cWhereUR += " AND cUnidadResponsable in ('G01', 'G02', 'G03', 'G04', 'G05', 'G06', 'G07', 'G08', 'G09', 'G10', 'G11', 'G12', 'G13', 'G14', 'G15', 'G16', 'G17', 'G18', 'G19', 'G20', 'G21', 'G22', 'G23', 'G24', 'G25', 'G26', 'G27', 'G28', 'G29', 'G30', 'G31', 'G32') ";
				}
				
				if(cUE_Usuario == "A02"){
					cWhereUR += " AND (cTipoContrato IN ( 'FE', 'RE', 'RI', 'PD' ) OR TipoDocumento in ( 'Convenio de Colaboracion', 'CONVENIO DE PAGO BENEFICIARIOS'))";
				}else if(cUE_Usuario == "A04"){
					cWhereUR += " AND cTipoContrato NOT IN ( 'FE', 'RE', 'RI', 'PD' ) AND TipoDocumento not in ( 'Convenio de Colaboracion', 'CONVENIO DE PAGO BENEFICIARIOS')";
				}	
				
				if (tipoIntegra=="COMP") {
					cWhereUR += " AND esIntegrado = 0 ";
				} else {
					cWhereUR += " AND esIntegrado = 1 ";
				}
				
				$('#tablaPendientesComp').DataTable({
					bRetrive: true,
					bDestroy: true,
					oLanguage: es_mx,
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" 
								+ window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaCompromisos&qw=estadoCompromiso = 'CREADO'  AND ( (cTipoDocumento = 'REDUCCION')  OR (cTipoDocumento != 'REDUCCION' AND nfolioSuficiencia IS NOT NULL)) "+ cWhereUR,
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					order: [[1, 'asc']],
					aoColumns: [
						{ sName: "idCompromiso",		bSearchable: false, bSortable: true, bVisible: true},
						{ sName: "caNoCompromiso",		bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cIdContrato",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cTipoDocumento",		bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "estadoCompromiso",	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "fAplicacion",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cRamo",				bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cUnidadResponsable",	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "Importe",				bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cIdRFC",				bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "nFolioSuficiencia",	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "TipoDocumento",		bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "TipoPago",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "esIntegrado",			bSearchable: true, bSortable: false, bVisible: true}
						
					]
				});
				
			}
		    
		    function noAutorizadosCompromiso() {
		    	var tipoIntegra=$("input[name='rTipoIntegracionCG']:checked").val();
		    	var cWhereUR ="";
		    	var cUE_Usuario = $("#cIdUnidadEjecutoraUsuario").val();
		    	if(cUE_Usuario == "A02"){
		    			if (calendario == "'S'") {
		    				cWhereUR = " AND cEsCalendario = " + calendario;
		    			}else {
		    				cWhereUR += " AND (cTipoContrato IN ('FE', 'RE', 'RI', 'PD'))" ;
		    			}
		    			
				}else if(cUE_Usuario == "A04"){
					cWhereUR += " AND cTipoContrato NOT IN ('FE', 'RE', 'RI', 'PD')";
				}	
		    	
		    	if (tipoIntegra=="COMP") {
					cWhereUR += " AND esIntegrado = 0 ";
				} else {
					cWhereUR += " AND esIntegrado = 1 ";
				}
				
		    	$('#tablaCompGenerados').DataTable({
					bRetrive: true,
					bDestroy: true,
					oLanguage: es_mx,
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" 
								+ window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaCompromisos&qw=estadoCompromiso='ENVIADO A SICOP' AND ( (cTipoDocumento = 'REDUCCION')  OR (cTipoDocumento != 'REDUCCION' AND nfolioSuficiencia IS NOT NULL))" + cWhereUR ,
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					order: [[1, 'asc']],
					aoColumns: [
						{ sName: "idCompromiso",		bSearchable: false, bSortable: true, bVisible: true},
						{ sName: "caNoCompromiso",		bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cIdContrato",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cTipoDocumento",		bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "estadoCompromiso",	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "fAplicacion",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cRamo",				bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cUnidadResponsable",	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "Importe",				bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cIdRFC",				bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "nfolioSuficiencia",	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cTipoContrato",		bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "bImprimir",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "esIntegrado",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cesCalendario",		bSearchable: true, bSortable: false, bVisible: true}
					]
				});
		    }
		    
		    function generaTblCalendario() {
		    	
		    	$('#tablaCompCalendario').DataTable({
					bRetrive: true,
					bDestroy: true,
					oLanguage: es_mx,
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" 
								+ window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vListaCompromisos&qw=estadoCompromiso='CREADO' AND  cEsCalendario = 'S'",
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					order: [[1, 'asc']],
					aoColumns: [
						{ sName: "idCompromiso",		bSearchable: false, bSortable: true, bVisible: true},
						{ sName: "caNoCompromiso",		bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cIdContrato",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cTipoDocumento",		bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "estadoCompromiso",	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "fAplicacion",			bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cRamo",				bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cUnidadResponsable",	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "Importe",				bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cIdRFC",				bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "nfolioSuficiencia",	bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "cTipoContrato",		bSearchable: true, bSortable: false, bVisible: true},
						{ sName: "bImprimirCalendario",	bSearchable: true, bSortable: false, bVisible: true}
					]
				});
		    }
		    function enviarLayoutSuf(){
				try {
					$('#tablaPendientesSuf tbody tr input:checked').each(function(idx, elm){
						var caNoCompromiso = $(this).parent('td').parent('tr').find('td:eq(1)').html();
						var esDescentralizado = $(this).parent('td').parent('tr').find('td:eq(10)').html();
						var integrada = $(this).parent('td').parent('tr').find('td:eq(7)').html();
						var esCalendario = $(this).parent('td').parent('tr').find('td:eq(12)').html();
						$('#esIntegrado').val(esDescentralizado);
						$('#esCalendario').val(esCalendario);
						
						if (esDescentralizado == 1){
							let resultado = integrada.substring(1);
							$('#coIntegrada').val($('#coIntegrada').val() +"'" + resultado + "',");
							$('#foliosSuficiencia').val("1");
							
						} else {
							$('#foliosSuficiencia').val($('#foliosSuficiencia').val()  + caNoCompromiso + ",");	
						}
						 
						
					});

					if($('#foliosSuficiencia').val() == "" && $('#esIntegrado').val() == 0    || $('#coIntegrada').val() == "" && $('#esIntegrado').val() == 1 ){
						Swal.fire("Revise","No se ha seleccionado ningún registro...", "info");
						
					} else {
						$('#envioSICOP').submit();
						setTimeout(function() {
							noAutorizadosSuficiencia();
							$('#foliosSuficiencia').val("");
							$('button[data-bs-target="#generado-suf"]').trigger('click');
							}, 5000);
					}

				}catch(e) {
					alert(e);
					location.reload(true);
				
				}
	 		}
		    
		    function reenviarLayoutSuf(){
				try {
					$('#tablaSufGenerado tbody tr input:checked').each(function(idx, elm){
						var caNoCompromiso = $(this).parent('td').parent('tr').find('td:eq(1)').html();
						var esDescentralizado = $(this).parent('td').parent('tr').find('td:eq(12)').html();
						var integrada = $(this).parent('td').parent('tr').find('td:eq(7)').html();
						var esCalendario = $(this).parent('td').parent('tr').find('td:eq(14)').html();
						$('#esIntegrado').val(esDescentralizado);
						$('#esCalendario').val(esCalendario);
						
						if (esDescentralizado == 1){
							let resultado = integrada.substring(1);
							$('#coIntegrada').val($('#coIntegrada').val() +"'" + resultado + "',");
							$('#foliosSuficiencia').val("1");
							
						} else {
							$('#foliosSuficiencia').val($('#foliosSuficiencia').val()  + caNoCompromiso + ",");	
						}
						
					});

					if($('#foliosSuficiencia').val() == "" && $('#esIntegrado').val() == 0    || $('#coIntegrada').val() == "" && $('#esIntegrado').val() == 1  ){
						Swal.fire("Revise","No se ha seleccionado ningún registro...", "info");
						
					} else {
						$('#envioSICOP').submit();
						$('#foliosSuficiencia').val('');
					}

				}catch(e) {
					alert(e);
					location.reload(true);
				}
	 		}
		    
		    function enviarLayoutCompromiso(){
				try {
					$('#tablaPendientesComp tbody tr input:checked').each(function(idx, elm){
						var caNoCompromiso = $(this).parent('td').parent('tr').find('td:eq(1)').html();
						var esDescentralizado = $(this).parent('td').parent('tr').find('td:eq(13)').html();
						
						$('#esIntegrado').val(esDescentralizado);
						
						if (esDescentralizado == 1){
							$('#coIntegrada').val($('#coIntegrada').val() +"'" + caNoCompromiso + "',");
							$('#foliosCompromisos').val("");
							
						} else {
							$('#foliosCompromisos').val($('#foliosCompromisos').val() + "'" + caNoCompromiso + "',");
						}
					});
					
					if($('#foliosCompromisos').val() == "" && $('#esIntegrado').val() == 0    || $('#coIntegrada').val() == "" && $('#esIntegrado').val() == 1){
						Swal.fire("Revise","No se ha seleccionado ningún registro...", "info");
					
					} else {
						$('#envioSICOP').submit();
						setTimeout(function() {
							noAutorizadosCompromiso();
							$('#foliosCompromisos').val("");
							$('button[data-bs-target="#generado-comp"]').trigger('click');
							}, 5000);
					}

				}catch(e) {
					alert(e);
					location.reload(true);
				}
	 		}
		    
		    function reenviarLayoutCompromiso(){
				try {
					$('#tablaCompGenerados tbody tr input:checked').each(function(idx, elm){
						var caNoCompromiso = $(this).parent('td').parent('tr').find('td:eq(1)').html();
						var esDescentralizado = $(this).parent('td').parent('tr').find('td:eq(13)').html();
						var esCalendario = $(this).parent('td').parent('tr').find('td:eq(14)').html();
						$('#esIntegrado').val(esDescentralizado);
						
						if (esCalendario == "S") {
							$("#tipoLayout").val(4);
							$('#foliosCalendario').val($('#foliosCalendario').val() + "'" + caNoCompromiso + "',");
							
						} 
						
						if (esDescentralizado == 1){
							$('#coIntegrada').val($('#coIntegrada').val() +"'" + caNoCompromiso + "',");
							$('#foliosCompromisos').val("");
							
						} else {
							$('#foliosCompromisos').val($('#foliosCompromisos').val() + "'" + caNoCompromiso + "',");	
						}		
					});
					
					if($('#foliosCompromisos').val() == "" && $('#esIntegrado').val() == 0    || $('#coIntegrada').val() == "" && $('#esIntegrado').val() == 1 ){
						Swal.fire("Revise","No se ha seleccionado ningún registro...", "info");
						
					} else {
						$('#envioSICOP').submit(); 
						$('#foliosCompromisos').val("");
						$('#foliosCalendario').val("");
					}

				}catch(e) {
					alert(e);
					location.reload(true);
				}
	 		}

		    function enviarLayoutCalendario(){
				try {
					$('#tablaCompCalendario tbody tr input:checked').each(function(idx, elm){
						var caNoCompromiso = $(this).parent('td').parent('tr').find('td:eq(1)').html();
						$('#foliosCalendario').val($('#foliosCalendario').val() + "'" + caNoCompromiso + "',");
						
					});

					if($('#foliosCalendario').val() == "" ){
						Swal.fire("Revise","No se ha seleccionado ningún registro...", "info");
						
					} else {
						$('#envioSICOP').submit();
						setTimeout(function() {
							generaTblCalendario();
							$('#foliosCalendario').val("");
							$('button[data-bs-target="#comp-calendario"]').trigger('click');
							}, 5000);
					}

				}catch(e) {
					alert(e);
					location.reload(true);
				}
	 		}
			function limpiaValores(){
				$("#integracion").val("");
				$("#folioCompromisoTxt").val("");
				$("#folioCompromisoSnd").val("");
				
				$("#NoFolioSICOP").val("");
				$("#NoFolioSICOPSnd").val("");
			}

			function limpiaValoresComp(){
				$("#integracionC").val("");
				$("#folioCompromisoC").val("");
				$("#folioCompromisoSnd").val("");
				
				$("#nFolioAutSICOP").val("");
				$("#NoFolioSICOPSnd").val("");
			}
			
			function actualizaFolioSICOP(){
					if( validaEnvio() && confirm("Esta seguro que desea actualizar el id " + $("#folioCompromisoSnd").val() + " con el folio de Suficiencia " + $("#NoFolioSICOP").val() + "?") ){
						$("#NoFolioSICOPSnd").val($("#NoFolioSICOP").val() );
						try{
							queryFormPost({
								queryName:"numFolioSuficienciaUpdate",
								async:false,
								callback:function(){
									modalFolioSICOP.hide();
								    Swal.fire({ icon: "success",
												text: "Suficiencia actualizada exitosamente. Ahora puede generar el Layout de Compromiso."});
									limpiaValores();	
											
								}
							});
							noAutorizadosSuficiencia();
							
						}catch(e){
							Swal.fire({ icon: "error",
										text: e});
							
						}
						
						modalFolioSICOP.hide();
					}
				
			
			}
		    
			function validaEnvio(){
				$("#sAuxiliarComodin").val("");
				$("#sAuxiliarComodin").val($("#integracion").val());
				
				if( $.trim( $("#NoFolioSICOP").val() ) == ""){			
					Swal.fire({ icon: "warning",
								text: "El Num. de Folio de Suficiencia es requerido"});
					return false;
				}else if( $("#folioCompromisoSnd").val() == "" ){			
					Swal.fire({ icon: "warning",
								text: "No se encontro folio seleccionado. Intente nuevamente"});
					return false;
				}
				
				return true;
			}
			
			function imprimir(contrato, folio){
				$("#cContrato").val(contrato)
				$("#foliosCompromisos").val(folio)
				
				queryFormPost("leerConceptoCompromiso",{async:false});
				
				modalConcepto.show();
			}
			
			function imprimirCompromiso(){
				queryFormPost("leerTipoIntegracion",{async:false});
				//Imprimir Oficio
				/*
				if($("#integradaRef").val() == "1"){
					window.open("../admin/SeguridadCatalogos?" 
						    + "catalogo=CONTRARECIBO"
							+ "&accion=run" 
							+ "&rn=ReporteCompromisosRef.jasper" 
							+ "&where2=" + $("#cContrato").val()
							+ "&concepto=" + $("#conceptoContrato").val(),
							"popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
				} else if($("#integradaRef").val() == "0"){
					*/
					window.open("../admin/SeguridadCatalogos?" 
						    + "catalogo=CONTRARECIBO"
							+ "&accion=run" 
							+ "&rn=ReporteCompromisos.jasper" 
							+ "&where2=" + $("#cContrato").val()
							+ "&concepto=" + $("#conceptoContrato").val(),
							"popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
				//}
			}
			
			function extraerFormato(contrato) {
				$("#tipoLayout").val(3);
				$('#cxp').val(contrato);
				$('#envioSICOP').submit();
			}
			
			
			function abrirModalFirmantes(contrato, id, esIntegrada){
				$("#cContrato").val(contrato)
				$("#idCompromiso").val(id)
				$('#esIntegrado').val(esIntegrada);
				modalFirmantes.show();
				
			}
			
			function imprimirNotaNuevo(){
				queryFormPost("existeFirmanteCompromisoRead", {async: false});
				
				if ($("#tieneFirmante").val() == 0) {
					queryFormPost("guardarFirmanteCompromiso", {async: false});	
				} else {
					queryFormPost("actualizarFirmanteCompromiso", {async: false});	
				}
				
				window.open("../admin/SeguridadCatalogos?" 
					    + "catalogo=CONTRARECIBO"
						+ "&accion=run" 
						+ "&rn=NotaInformativaContrato.jasper" 
						+ "&where2=" + $("#cContrato").val()
						+ "&esIntegrada=" + $("#esIntegrado").val()
						+ "&nombre=" + $("#cNombreEmpleado").val() + ' ' +  $("#cPaternoEmpleado").val() + ' ' +  $("#cMaternoEmpleado").val()
						+ "&puesto=" +  $("#cPuestoEmpleado").val(),
						"popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
			}
			
			
			function generaLayoutSuficiencia(folio) {
				$("#tipoLayout").val(5);
				$('#foliosSuficiencia').val(folio);
				$('#envioSICOP').submit();
			}
			

			function generaContarrecibo(){
				getNextSequenceVal({seqName: "CO-" + $("#cCentroContable").val(), async: false, callback: setSequenceVal});
				
				if ($("#caNoCompromiso").val() != "" )
					return 1;
				else
					return -1;
			}
			
			
			function cancelarPrecompromiso(folio){
				
				Swal.fire({
					  title: "Se cancelará la suficiencia: " + folio,
					  text: "Desea continuar?",
					  icon: 'warning',
					  showCancelButton: true,
					  confirmButtonColor: '#288BA8',
					  cancelButtonColor: '#e6e6e6',
					  confirmButtonText: 'Aceptar',
					  cancelButtonText: 'Cancelar'
					}).then((result) => {
					  if (result.isConfirmed) {
						  $.ajax({
							    url: '../compromiso/cancelaSuficiencia',
							    method: 'get',
							    dataType : 'json',
							    data: { folio: folio ,
							    		tipo: "cancelacion"
							    	  },
							    success : function() {
							    		noAutorizadosSuficiencia();
							    		noAutorizadosCompromiso(); 
								},
								error : function(err) {
									noAutorizadosSuficiencia();
							    	noAutorizadosCompromiso(); 
							    	
							    	if (err.responseText !="") {
							    		let mensaje = err.responseText || "Ocurrió un error inesperado.";
								        Swal.fire('Verifique!', mensaje, 'error');
								    } else {
								    	Swal.fire('OK!', 'Suficiencia Cancelada.', 'success');
								    }
								}
						  });
				    	 
					  } 
					})
				}
			
			function capturaFolioSicop(contrato, id){
				$("#integracionC").val(contrato)
				$("#folioCompromisoC").val(id)
				modalAutoriza.show();
				
			}
			
			function autorizaCompromiso(){
				
				if (!$("#nFolioAutSICOP").val().trim()) {
					Swal.fire("Debe capturar el folio del compromiso SICOP", "Capture", "warning" );
					return false; 
				} 
					
					Swal.fire({
						  title: "Se autorizara el compromiso: " + $("#folioCompromisoC").val(),
						  text: "Desea continuar?",
						  icon: 'warning',
						  showCancelButton: true,
						  confirmButtonColor: '#288BA8',
						  cancelButtonColor: '#e6e6e6',
						  confirmButtonText: 'Aceptar',
						  cancelButtonText: 'Cancelar'
						}).then((result) => {
						  if (result.isConfirmed) {
							  $.ajax({
								    url: '../compromiso/cancelaSuficiencia',
								    method: 'get',
								    dataType : 'json',
								    data: { folio: $("#folioCompromisoC").val() ,
								    		tipo: "aplicacion",
								    		folioSICOP:	$("#nFolioAutSICOP").val(),
								    	  },
								    success : function() {
								    		noAutorizadosSuficiencia();
								    		noAutorizadosCompromiso(); 
									},
									error : function(err) {
										noAutorizadosSuficiencia();
								    	noAutorizadosCompromiso(); 
								    	
								    	if (err.responseText !="") {
								    		let mensaje = err.responseText || "Ocurrió un error inesperado.";
									        Swal.fire('Verifique!', mensaje, 'error');
									    } else {
									    	Swal.fire('OK!', 'Compromiso autorizado.', 'success');
									    }
									}
							  });
					    	 
						  } 
						})
					
			}
			
            function consultaFormato(ruta) {
                window.open("../muestraDocumento?fortimax=" + ruta, "_blank");
            }
            
            function consultaCFDI(ruta) {
                window.open("../descargaFacturas?fortimax=" + ruta, "_blank");
            }
		</script>

	</body>
			
</html>
