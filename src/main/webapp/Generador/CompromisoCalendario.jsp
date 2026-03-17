<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>

<%
		String cCentroContable = "";
		Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
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
			
		int id_oper = -1;
		if (request.getParameter("id_oper") != null)
			id_oper = new Integer(request.getParameter("id_oper")).intValue();
		else
			id_oper = c.getCasoOperacion(0).getIdOperacion();
		
		String select = c.getTipoCaso().getGavetaAsociada() + "_G" + c.getIdGabinete();
		
		if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
			cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
		}
		
		String cUR = usuario.getU_UR();
		String U_LOGIN = usuario.getLogin();
	
%>
<!DOCTYPE html>
<html>
	<head>
		<title>Compromiso Calendario</title>

  	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
  	<link rel="stylesheet" href="https://cdn.datatables.net/1.13.8/css/jquery.dataTables.min.css">
  	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
  	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css"/>
  	<link rel="stylesheet" href="../SISECOP/css/style.css">
  
    <style>
     
    .custom-card-header {
		  height: 40px; 	
		  display: flex;
		  align-items: center; 
		  justify-content: center;
		  background-color: #3571B6;
      	  color: white;
	}
   
  </style>
  
  	<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>	
  	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  	<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
  	<script src="https://cdn.datatables.net/1.13.6/js/dataTables.bootstrap5.min.js"></script>
  	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/CompromisoCalendario.js"></script>
		
	</head>
	<body id="dt_example">
		<form id="formCompromiso">
			<input type="hidden" name="nFolioPago" id="nFolioPago" value="<%=c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>"/>
			<input type="hidden" name="cNombreEmpleado" id="cNombreEmpleado" value=""/>
			<input type="hidden" name="cPaternoEmpleado" id="cPaternoEmpleado" value=""/>
			<input type="hidden" name="cMaternoEmpleado" id="cMaternoEmpleado" value=""/>
			<input type="hidden" name="cPuestoEmpleado" id="cPuestoEmpleado" value=""/>
			<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%= U_LOGIN%>"/>
	    	<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable"  value="<%= cUR%>"/>
	    	<input type="hidden" name="nNumEmpleadoBusqueda" id="nNumEmpleadoBusqueda" value=""/>
			<input type="hidden" name="cTipoFirmante" id="cTipoFirmante" value="AUT"/>
			<input type="hidden" id="cDocumento" name="cDocumento" value="COMPROMISO"/>
			<input type="hidden" id="idCompromiso" name="idCompromiso" value=""/>
			<input type="hidden" id="campo" name="campo" value=""/> 
			<input type="hidden" id="tablaEnc" name="tablaEnc" value=""/> 
			<input type="hidden" id="campoCondicion" name="campoCondicion" value=""/> 
			<input type="hidden" id="tablaDet" name="tablaDet" value=""/> 
			<input type="hidden" id="tipoAplicar" name="tipoAplicar" value=""/>
			<input type="hidden" id="cContrato" name="cContrato" value=""/>
			<input type="hidden" id="esIntegrado" name="esIntegrado" value=""/>
			<input type="hidden" id="tieneFirmante" name="tieneFirmante" value=""/>
			<input type="hidden" id="caNoCompromiso" name="caNoCompromiso"/>
			<input type="hidden" id="existeComp" name="existeComp"/>
			<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>"/>
			<input type="hidden" id="tieneClavesGuardadas" name="tieneClavesGuardadas"/>
			<input type="hidden" id="aEjercicioFiscal" value=""	name="aEjercicioFiscal" />
			<input type="hidden" id="cEjercicio" name="cEjercicio" />
		
		<div id="container" >
			<h1 class="mt-3 mb-3">Compromisos Calendario</h1>
			<div class="row mt-4">
				<div class="col-3">
					Clave de Contrato:
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-folder"></i></span>
						<input type="text" class="form-control" name="cIdContrato" id="cIdContrato" readonly/>
						<input type="button" class="btn btn-secondary" name="btnContrato" id="btnContrato" value="..." size="5" onclick="consultaContratos();" />
					</div>
				</div>
				<div class="col-1">
					Total Contrato :
					<input readonly type="text"	name="importeTotal" ID="importeTotal"	class="form-control desahabilitado" />
				</div>
				<div class="col-2">
					Folio compromiso:
					<input type="text" class="form-control" name="folio" id="folio" value = "<%=c.getFolio()%>" readonly/>
				</div>
				<div class="col-3" id ="divCheckClave">
					<br>Clave corta:
					<input type="checkbox" id="checkClave" name="checkClave" class="form-check-input" value="INTERNA" onclick="cambiarVista();"/>
				</div>
			</div>	
			<div class="row">
				
				<div class="col-2">
					R.F.C. :
					<div class="input-group">
						<span class="input-group-text"><i class="bi-file-person"></i></span>
						<input readonly type="text" name="cIDRFC" id="cIDRFC" class="form-control desahabilitado" />
					</div>
				</div>
				<div class="col-4">
					Nombre :
					<input readonly type="text"	name="cnombre" ID="cnombre"	class="form-control desahabilitado" />
				</div>
			</div>
			
			  <div class="card mt-3" align="center" id="tablaReduccionesCard">
			    <div class="custom-card-header"><b> Presupuesto Comprometido </b></div>
			    <div class="card-body">
			      <table id="tablaReducciones" class="table table-striped table-bordered table-hover" width="100%">
			        <thead>
			          <tr>
			            <th>EP</th>
			            <th>Comprometido</th>
			            <th>Enero</th>
			            <th>Feb</th>
			            <th>Mar</th>
			            <th>Abr</th>
			            <th>May</th>
			            <th>Jun</th>
			            <th>Jul</th>
			            <th>Ag</th>
			            <th>Sep</th>
			            <th>Oct</th>
			            <th>Nov</th>
			            <th>Dic</th>
			          </tr>
			        </thead>
			        <tbody></tbody>
			      </table>
			    </div>
			  </div>
			
			  <div class="card mt-2" align="center" id="tablaAmpliacionesCard">
			    <div class="custom-card-header"><b> Presupuesto Disponible</b></div>
			    <div class="card-body">
			      <table id="tablaAmpliaciones" class="table table-striped table-bordered table-hover" width="100%">
			        <thead>
			          <tr>
			            <th>EP</th>
			            <th>Disponible</th>
			            <th>Enero</th>
			            <th>Feb</th>
			            <th>Mar</th>
			            <th>Abr</th>
			            <th>May</th>
			            <th>Jun</th>
			            <th>Jul</th>
			            <th>Ag</th>
			            <th>Sep</th>
			            <th>Oct</th>
			            <th>Nov</th>
			            <th>Dic</th>
			          </tr>
			        </thead>
			        <tbody></tbody>
			      </table>
			    </div>
			  </div>
			</div>
			
			 <!-- Tabla Captura -->
		  <div class="row">
			 <div class="col-6">
			  <div class="card mt-3" align="center">
				  <div class="custom-card-header"><b> Movimientos Capturados </b></div>
				  <div class="card-body">
				   
				        <table id="tablaCalendario" class="table table-striped table-bordered table-hover" width="100%">
				          <thead>
				            <tr>
				              <th>Clave Presupuestal</th>
				              <th>Importe</th>
				              <th>Mes</th>
				              <th>X</th>
				            </tr>
				          </thead>
				          <tbody></tbody>
				        </table>
				      </div>
				  </div>
				</div>
			
			<div class="col-6">
			  <div class="card mt-3" align="center">
				  <div class="custom-card-header"><b> Validación clave corta </b></div>
				  <div class="card-body">
				   
				        <table id="tablaValidacion" class="table table-striped table-bordered table-hover" width="100%">
				          <thead>
				            <tr>
				              <th>Clave Corta</th>
				              <th>Mes</th>
				              <th>Importe</th>
				            </tr>
				          </thead>
				          <tbody></tbody>
				        </table>
				      </div>
				  </div>
				</div>
			</div>
			<div class="row mt-2">
			 	<div class="col-4"></div>
			 	
			 	<div class="col-2">
			 		<div class="input-group">
			 			<label><b>Total Movimientos</b></label>
			 			<input readonly type="text" name="sumaClaves" id="sumaClaves" class="form-control" />
			 			<span class="input-group-text"><i class="bi bi-plus-lg"></i></span>
			 		</div>	
			 	</div>
			</div>
			
		<!-- Modal Detalle Mensual -->
		<div class="modal fade" id="modalDetalle" tabindex="-1" aria-labelledby="modalDetalleLabel" aria-hidden="true">
		  <div class="modal-dialog modal-lg">
		    <div class="modal-content">
		      <div class="modal-header">
		        <h5 class="modal-title" id="modalDetalleLabel">Detalle Mensual: <span id="epSeleccionado"></span></h5>
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
		      </div>
		      <div class="modal-body">
		        <table id="tablaDetalleMensual" class="table table-striped">
		          <thead>
		            <tr>
		              <th>Mes</th>
		              <th id="columnaComp">Compromiso / Disponible</th>
		              <th id="columnaCaptura">Reducción / Ampliación</th>
		            </tr>
		          </thead>
		          <tbody>
		          </tbody>
		        </table>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-success" onclick="guardarDetalle()">Guardar Cambios</button>
		        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
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
	<div id="dialog-Procesando" title="Procesando">
		<div id="divEsperaProcesando" style="visibility: hidden"
			align="center">
			Espere por favor.... <img border="0" src="../imagenes/espera.gif"
				height="30">
		</div>
	</div>
		<script type="text/javascript" charset="utf-8">
			var msg = "<%=msg%>";
			var modalFirmantes;
			let mostrarBorrar = true;
			function onLoadPlantilla(){
				
				if(<%=id_oper == 1%>){
					parent.document.getElementById("pb_send").style.visibility='hidden';
					mostrarBorrar = true;
				} else {
					mostrarBorrar = false;
					queryFormPost("consultaCompromisoCalendario", {async: false});
					$('#tablaReduccionesCard').hide();
					$('#tablaAmpliacionesCard').hide();
				}
			
			}
			
			function onSubmit(id_oper){
				var p = window.parent;
				var valida_campos = true;
				if ($("#cDocumentoHaplicado").val() == "S") {
					alert("Documento ya fue aplicado y se avanzarï¿½ a modo de CONSULTA");
				}else {
				
					try{
						p.gestion.setFolio( $("#FOLIO").val() );
						p.gestion.setOperador( $("#OPERADOR").val() );
						p.gestion.setFechaDocumento( $("#FECHA_CARGA").val() );
						p.gestion.setEjercicioFiscal( $("#cEjercicio").val() );
						p.gestion.setConceptoMov("Aplicaciï¿½n de Compromisos");
						p.gestion.setMoneda("MXP");
						p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
						p.gestion.setAplicadoCont("false");
						
						if(<%=c.getIdGabinete()%>!=-1){
							p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
						}
						
						queryFormPost("existenClavesCalendario", {async: false});
						if ($("#tieneClavesGuardadas").val() == 0) {
							Swal.fire("No hay claves", "No se puede guardar ya que no se han capturado movimientos", 'error');
							return false;
						}
						
						if ($("#sumaClaves").val() != 0) {
							Swal.fire("Revise", "Los movimientos no estan compensandos, no se pueden guardar", 'error');
							return false;
						}
						
						var nretval = generaContarrecibo();
						if (nretval == -1) {
							return false;
						}
						
						if ( procesar() ){
							parent.document.getElementById("pb_save").disabled = false;												
						}
	
						parent.document.getElementById("pb_save").disabled=true;
						parent.document.getElementById("pb_cancel").disabled=true;			
					
					} catch (e) {
						window.alert("onSubmit: Error: " + e.message);
						return false;
					}
				}
			}
			
			function generaContarrecibo(){
				getNextSequenceVal({seqName: "CO-" + $("#cCentroContable").val(), async: false, callback: setSequenceVal});
				
				if ($("#caNoCompromiso").val() != "" )
					return 1;
				else
					return -1;
			}
			
			function setSequenceVal(seqValue) {
				seqValue = 100000 + parseInt(seqValue,10);
				
				seqValue = "<%=cCentroContable%>" + "CO" + $("#cEjercicio").val() + seqValue;
				$("#caNoCompromiso").val( seqValue );
			}
		</script>
	</form>	
	</body>
</html>