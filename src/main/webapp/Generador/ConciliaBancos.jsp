<%@page language="java" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.reportes.servlet.*"%>
<%@page import="java.text.DecimalFormat"%>

<%
	String cCentroContable ="";
	String algo ="";
 	String cConciliacion ="";
 	String fConciliacion ="";
 	String importado ="";
 	
 	boolean insertSaldo = false;
 	
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

	boolean error = "SI".equals( request.getParameter("error") );
	
	String msg = "";
	
	if(error){
		msg = (String) session.getAttribute(GestionInterface.ATT_MSG);	
	}
	
	String mensaje = "";
	String mensajeRetorno = "";
	
	if (request.getParameter( "importado" ) != null && !"".equals(request.getParameter( "importado" )) ){
		importado = request.getParameter("importado") ;
		}
	
	if (request.getParameter( "mensajeRetorno" ) != null && !"".equals(request.getParameter( "mensajeRetorno" )) ){
		mensajeRetorno = request.getParameter("mensajeRetorno") ;
		} 
	
	if (request.getParameter( "cconciliacion" ) != null && !"".equals(request.getParameter( "cconciliacion" )) ){
		cConciliacion = request.getParameter( "cconciliacion" );
		}
	
	if (request.getParameter( "fconciliacion" ) != null && !"".equals(request.getParameter( "fconciliacion" )) ){
		fConciliacion = request.getParameter( "fconciliacion" );
		}	

	if (request.getParameter("msg") != null	&& !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	//Valida Centro de Costos
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	} 
	String cUR = usuario.getU_UR();
	
	if ("10".equals(cCentroContable) || "00".equals(cCentroContable) ) {
		insertSaldo = true;				
	} 
	
	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar la Conciliacion, Consulte a su administrador.";
	}

	algo = usuario.getLogin();
	//de aki para arriba es de cajon
%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Conciliaci&oacute;n Bancos</title>

<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css"></link>
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>

<script src="../Generador/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script src="../Generador/js/jquery.blockUI-2.70.0.js"></script>
<script src="../Generador/js/conciliaBancos.js"></script>

<script type="text/javascript" charset="utf-8">

var oTable1;
var oTable2;
var oTable3;
var modalFirmantes;
var modalInsertar;

$(document).ready(function() {	
	$("#insertaSaldos").button();
	$("#nuevaConciliacion").button();
	$("#importa").button();
	$("#Procesa").button();
	$("#ver").button();
	$("#guarda").button();
	$("#final").button();
	$("#iniciar").button();
	$("#exporta").button();
	$("#firmantes").button();
	
	modalFirmantes = new bootstrap.Modal(document.getElementById('dialog-Firmantes'), 'data-bs-backdrop');
	modalInsertar = new bootstrap.Modal(document.getElementById('dialog-form'), 'data-bs-backdrop');
			
	if( <%=insertSaldo%> ){
		document.getElementById("insertaSaldos").disabled = false;
	}
	
	$("input.AyudaSyC").subIniciaDlg();     
    $("input.autoCompletaSyC").subIniciaAutoCompleta();
	
	$("#cCentroContable").val( "<%=cCentroContable%>");
	
	$("#cUsuarioLogin").val( "<%=algo%>");
		
	creaTablaConciliaciones();
	
});
	

	
</script>

</head>
<body id="dt_example">
<br/>	
	<form id="frmConciliacionBancaria" name="frmConciliacionBancaria">
		<div id="principal" class="container" style="width: 90%">
				
			<div class="card-header"> <h3> Conciliaciones Bancarias </h3> </div>
			<hr class="mt-3"/>
			
			<div id="conciliacion"  style=" visibility: visible;  width: 100% "  class="container" >
			
				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<label for="mesMostrado" class="form-label"> Selecciona el Mes:&nbsp; </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">										
						<select id="mesMostrado" name="mesMostrado" class="form-select" onchange="cambia_Mes()">
							<option value="0">Todos los Meses...</option>
							<option value="1">Enero</option>
							<option value="2">Febrero</option>
							<option value="3">Marzo</option>
							<option value="4">Abril</option>
							<option value="5">Mayo</option>
							<option value="6">Junio</option>
							<option value="7">Julio</option>
							<option value="8">Agosto</option>
							<option value="9">Septiembre</option>
							<option value="10">Octubre</option>
							<option value="11">Noviembre</option>
							<option value="12">Diciembre</option>
							<option value="13">Anual CP</option>
						</select>																													
					</div>	
					
					<div class="col-12 col-lg-4 col-md-4 col-sm-12">															
						<input type="button" id="nuevaConciliacion" name="nuevaConciliacion" value="Nueva Conciliacion" class="btn btn-primary" onclick="nuevaConcilia()"/>				
					</div>											
					<div class="col-12 col-lg-4 col-md-4 col-sm-12">															
						<input type="button" id="insertaSaldos" name="insertaSaldos" value="Inserta Saldos" class="btn btn-secondary" onclick="insertaSaldosConciliacion()"/>				
					</div>																																									
				</div>		

				<br/>

				<div id="conciliacion" class="table-responsive">	   
					<table id="dt_Conciliaciones" class="table table-striped" >
						<thead>
							<tr>
								<th>ID</th>
								<th>Cuenta</th>
								<th>Mes</th>
								<th>Descripción</th>
								<th>Final</th>
								<th>CC</th>
								<th>SaldoTXT</th>
								<th>SaldoPDF</th>
								<th>Intereses</th>
								<th>Contabilidad</th>
								<th>PDF</th>
								<th>Firmas</th>
								<th>Op 1</th>
								<th>Op 2</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
			
		</div>
	</form>		
	
	<br/>
	
	<form id="ExportarForm" name="ExportarForm" action="../servlet/ConciliaBancosServlet" enctype = "multipart/form-data" method = "POST">
		<div id="contConciliacion" style=" visibility: hidden;  width: 90% "  class="container">			
			<div id="newConciliacion"  class="container" style= "visibility: hidden; width: 100%">	
				<div class="card-header"> <h5><strong> Conciliación </strong></h5> </div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12  p-1">	
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12  p-1" id="divImportar">	
						<div class="row d-flex">
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1" >														
								<label for="TIPO" class="form-check-label">Importa Estado de Cuenta:</label>
								<input type="checkbox" name="TIPO" id="TIPO" class="form-check-input" value = "0" disabled="disabled" onclick="habilita()"/>
							</div>
						</div>
						<div class="row d-flex">
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">	
								<label for="cargaArchivo"></label>		
								<input class="form-control" type="file" id="cargaArchivo" name="cargaArchivo" style="width: 25em;" />
							</div>
						</div>										
					</div>											
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">															
						<div class="row d-flex">
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">															
								Cuenta Bancaria:																
							</div>											
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
								<div class="input-group">								
									<span class="input-group-text"><i class="bi bi-bank"></i></span>														
									<input type="text" id="cuenta" name="cuenta" class="form-control" value="<%=cConciliacion%>" readonly/>
								</div>				
							</div>																																															
						</div>
						<div class="row d-flex">
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">															
								Mes a Conciliar:																
							</div>											
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">	
								<div class="input-group">								
									<span class="input-group-text"><i class="bi bi-calendar"></i></span>												
									<input type="text" id="fecha" name="fecha" class="form-control" value="<%=fConciliacion%>" readonly/>
								</div>				
							</div>																																															
						</div>						
					</div>																																									
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">	
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">	
						<input type="button" id="importa" name="importa" value="Procesa Archivo" class="btn btn-secondary" onclick="importaSubmit()"/>
					</div>
					<div class="col-12 col-lg-6 col-md-4 col-sm-12 d-flex p-1">	
						<label for="esFinal" class="form-check-label">Final:</label>
						<input type="checkbox" name="esFinal" id="esFinal" class="form-check-input" value = "0" disabled="disabled"/>
					</div>
				</div>
					
			</div>		
		</div>
	</form>
	
	<form id="ProcesaForm" name="ProcesaForm" action="../servlet/ConciliaAutomaticaServlet" enctype = "multipart/form-data" method = "post" >
		<div id="ConciliacionAut" style=" visibility: hidden; width: 80% "  class="container">
			<fieldset>
				<table align="center" cellpadding="3">									
					<tr>
						<td>  </td>
						<td> <input type="button" id="Procesa" name="Procesa" value="Procesar Conciliacion" onclick="conciliaAutomatica()" class="btn btn-secondary btn-sm" /> </td>
						<td>  </td>
					</tr>
				</table>
			</fieldset>		
		</div>
	</form>
	
	<form id="AuxiliarBanco" name="AuxiliarBanco" action="../reportes/RepConciliaBancosServlet" method="get" target="_blanck" >
		<input  type="hidden" id="cEsFirmaElectronica" name="cEsFirmaElectronica" value="" />
		<div id="contDatos"  style=" visibility: hidden;  width: 80%" class="container" >
			<div id= "opciones" class="container" >
				<div class="card-header"> <h5><strong> Opciones </strong></h5> </div>			
				
				<div class="row d-flex justify-content-center mt-2">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1" >
						<input type="button" id="ver" name="ver" value="Ver Movimientos" class="btn btn-secondary" onclick="conciliacion()"/>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1" >
						<input type="button" id="guarda" name="guarda" value="Guardar Conciliacion" class="btn btn-secondary" onclick="guarda_info()"/>
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1" >
						<input type="button" id="final" name="final" value="Finaliza Conciliacion" class="btn btn-primary" onclick="finaliza()"/>
					</div>
				</div>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1" >
						<input type="button" id="iniciar" name="iniciar" value="Valida Seleccion" class="btn btn-secondary" onclick="sumas()"/>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1" >
						Suma Cargos&nbsp;
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1" >
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>		
							<input type="text" id="Cargos" name="Cargos" class="form-control" value="" readonly/>
						</div>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1" >
						Suma Abonos&nbsp;
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1" >
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>		
							<input type="text" id="Abonos" name="Abonos" class="form-control" value="" readonly/>
						</div>
					</div>
				</div>										
				
			</div>
			
			<div id= "reportes" style=" visibility: hidden " class="container" >				
				<div class="card-header"> <h5><strong> Reportes </strong></h5> </div>
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">						
						<strong> Elige tu reporte: </strong>											
						<select id="report" name="report" class="form-select">
							<option value="00">Seleciona un reporte...</option>
						<option value="01">Movimientos en Conciliacion</option>
						<option value="03">Estado de Cuenta</option>
						<option value="04">Auxiliar</option>
						</select>																																	
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<br>					
						<input type="button" id="exporta" name="exporta" value="Genera Archivo" class="btn btn-secondary" onclick="exporta_archivo()"/>									
					</div>	
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">					
						<input type="button" id="firmantes" name="firmantes" value="Genera PDF" class="btn btn-primary" onclick="muestraFirmantes()"/>									
					</div>																																										
				</div>
				
			</div>
			
			<div style="display: none" >
				<input  type="text" id="cBan" value="<%=cConciliacion%>" name="cBan" size="35" />
				<input  type="text" id="nMes" value="<%=fConciliacion%>" name="nMes" size="35" />
				<input  type="text" id="cCC" value="<%=cCentroContable%>" name="cCC" size="35" />
				<input  type="hidden" id="sCargos" name="sCargos" value="" />
				<input  type="hidden" id="sAbonos" name="sCargos" value="" />  
				<input  type="hidden" id="nConciliacion" name="nConciliacion" value="" />  
				<input  type="hidden" id="numConciliacion" name="numConciliacion" value="" />
				<input  type="hidden" id="cadenaAuxiliar" name="cadenaAuxiliar" value="" />  
				<input  type="hidden" id="cadenaEdoCta" name="cadenaEdoCta" value="" />  
				<input  type="hidden" id="cadenaNoCon" name="cadenaNoCon" value="" />  
				<input  type="hidden" id="numConciliacionf" name="numConciliacionf" value="" />
				<input  type="hidden" id="cadenaAuxiliarf" name="cadenaAuxiliarf" value="" />  
				<input  type="hidden" id="cadenaEdoCtaf" name="cadenaEdoCtaf" value="" />  
				<input  type="hidden" id="cadenaNoConf" name="cadenaNoConf" value="" />
				<input  type="hidden" id="tipoReporte" name="tipoReporte" value="" />
				<input  type="hidden" id="cDescripcion" name="cDescripcion" value="" />
				<input  type="hidden" id="nFinal" name="nFinal" value="" />
				<input  type="hidden" id="nFolioCuenta" name="nFolioCuenta" value="" />
				<input  type="hidden" id="cCCSaldo" name="cCCSaldo" value="" />
				<input  type="hidden" id="cUsuarioLogin" name="cUsuarioLogin" value="" />
				<input  type="hidden" id="numeroConciliacionElimina" name="numeroConciliacionElimina" value="" />
				<input  type="hidden" id="validaFinal" name="validaFinal" value="" />
				<input  type="hidden" id="esVigente" name="esVigente" value="" />
				<input  type="hidden" id="estaDup" name="estaDup" value="" />		
				<input  type="hidden" id="nOrden" name="nOrden" value="" />
				<input  type="hidden" id="nIdtipoFirmante" name="nIdtipoFirmante" value="" />
				<input  type="hidden" id="seImporto" name="seImporto" value="<%=importado%>" />
				<input  type="hidden" id="mensajeRetorno" name="mensajeRetorno" value="<%=mensajeRetorno%>" />
				<input  type="hidden" id="nIdReporte" name="nIdReporte" value="" />
				<input  type="hidden" id="esFiel" name="esFiel" value=""/>
				<input  type="hidden" id="nNumEmpleadoVoBo" name="nNumEmpleadoVoBo" value="" />
				<input  type="hidden" id="nNumEmpleadoAut" name="nNumEmpleadoAut" value="" />		
				<input  type="hidden" id="nNumEmpleadoElab" name="nNumEmpleadoElab" value="" />
				<input  type="hidden" id="nNumEmpleadoBusqueda" name="nNumEmpleadoBusqueda" value="" />
				<input  type="hidden" id="cPuestoEmpleado" name="cPuestoEmpleado" value="" />
				<input  type="hidden" id="firmanteExiste" name="firmanteExiste" value="" />
				<input  type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" />
				
									
			</div>
			<div  id= "ntabla1" style=" visibility: hidden;  width: 90%" class="container mt-2" >
				<div class="card-header"> <h5><strong> Cargos </strong></h5> </div>
				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1" >						
						<input type="checkbox" name="sTCargos" id="sTCargos" class="form-check-input" value="" onclick="seleccionaCARGOS()" disabled/>
						<label for="sTCargos" class="form-check-label">Todos</label>																																			
					</div>													
				</div>
				
				<div id="consulta" class="table-responsive">	   
					<table id="dt_vNoConciliadosC" class="table table-striped" >
						<thead>
							<tr>
								<th>Concilia</th>
								<th>ID</th>
								<th>Folio</th>
								<th>Origen</th>
								<th>Fecha</th>
								<th>Referencia</th>
								<th>Cheque</th>
								<th>Descripcion</th>
								<th>Tipo</th>
								<th>Monto</th>
								<th style="display: none;"></th>
								<th style="display: none;"></th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
			
			<br/>
			
			<div id= "ntabla2" style=" visibility: hidden;  width: 90%" class="container" >
				<div class="card-header"> <h5><strong> Abonos </strong></h5> </div>
				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1" >						
						<input type="checkbox" name="sTAbonos" id="sTAbonos" class="form-check-input" value="" onclick="seleccionaABONOS()" disabled/>
						<label for="sTAbonos" class="form-check-label">Todos</label>																																			
					</div>													
				</div>
				 
				<div id="consulta" class="table-responsive">
					<table id="dt_vNoConciliadosA" class="table table-striped" >
						<thead>
							<tr>
								<th>Concilia</th>
								<th>ID</th>
								<th>Folio</th>
								<th>Origen</th>
								<th>Fecha</th>
								<th>Referencia</th>
								<th>Cheque</th>
								<th>Descripcion</th>
								<th>Tipo</th>
								<th>Monto</th>
								<th style="display: none;"></th>
								<th style="display: none;"></th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
		</div>
	</form>

<div class="modal fade" tabindex="-1" role="dialog" id="dialog-form" data-mdb-keyboard="true" data-mdb-backdrop="static">
			<div class="modal-dialog modal-lg" role="document">
				    <div class="modal-content">
					    <div class="modal-header">
					        <h5 class="modal-title">Insertar Conciliacion Manual</h5>
					        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
					    </div>
					    <div class="modal-body">
					    	<form id="insertaConciliacion" name="insertaConciliacion" action="">
							<div class="row">
								<div class="input-group">
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																
										<label for="mesSaldo" class="form-label">Selecciona Mes:&nbsp; </label>																												
									</div>	
									<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">																															
										<select name = "mesSaldo" id = "mesSaldo" class="form-select">
											<option value="1">Enero</option>
											<option value="2">Febrero</option>
											<option value="3">Marzo</option>
											<option value="4">Abril</option>
											<option value="5">Mayo</option>
											<option value="6">Junio</option>
											<option value="7">Julio</option>
											<option value="8">Agosto</option>
											<option value="9">Septiembre</option>
											<option value="10">Octubre</option>
											<option value="11">Noviembre</option>
											<option value="12">Diciembre</option>
										</select>						
									</div>																											
								</div>
							</div>
							<div class="row">
								<div class="input-group">
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																
										<label for="hbuscaConciliacion" class="form-label">Cuenta bancaria:&nbsp; </label>																												
									</div>	
									<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">			
										<div class="form-group">							
											<div class="input-group">
												<span class="input-group-text"><i class="bi bi-bank"></i></span>															
												<input type="text" id="hbuscaConciliacion" name="hbuscaConciliacion" value="" class="form-control AyudaSyC" />							
												<input type="hidden" id="buscaConciliacion" name="buscaConciliacion" value="" class="form-control" />
											</div>
										</div>
									</div>																											
								</div>
							</div>
							<div class="row">
								<div class="input-group">
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																
										<label for="cInst" class="form-label">Instrumento:&nbsp; </label>																												
									</div>	
									<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">																									
										<input type="text" id="cInst" name="cInst" value="" class="form-control" readonly/>													
									</div>																											
								</div>
							</div>
							<div class="row">
								<div class="input-group">
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																
										<label for="mSaldo" class="form-label">Saldo Banco:&nbsp; </label>																												
									</div>	
									<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
										<div class="input-group">
											<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>																							
											<input type="text" id="mSaldo" name="mSaldo" value="" class="form-control" placeholder="0.00"/>
										</div>													
									</div>																											
								</div>
							</div>
							<div class="row">
								<div class="input-group">
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																
										<label for="mSaldoContable" class="form-label">Saldo Contable:&nbsp; </label>																												
									</div>	
									<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">		
										<div class="input-group">
											<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>																										
											<input type="text" id="mSaldoContable" name="mSaldoContable" value="" class="form-control" placeholder="0.00"/>
										</div>													
									</div>																											
								</div>
							</div>
							<div class="row">
								<div class="input-group">
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																
										<label for="mIntereses" class="form-label">Intereses:&nbsp; </label>																												
									</div>	
									<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
										<div class="input-group">
											<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>																												
											<input type="text" id="mIntereses" name="mIntereses" value="" class="form-control" placeholder="0.00"/>
										</div>													
									</div>																											
								</div>
							</div>		
					    </form>
					    </div>
						<div class="modal-footer">
				       		<button type="button" id="btnAceptarInsertar" onclick="insertarConciliacion();" class="btn btn-primary">Aceptar</button>
				        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
				       </div>
			    </div>
		  </div>
	
</div>

	
	<div class="modal fade" tabindex="-1" role="dialog" id="dialog-Firmantes" data-mdb-keyboard="true" data-mdb-backdrop="static">
			<div class="modal-dialog modal-lg" role="document">
				    <div class="modal-content">
					    <div class="modal-header">
					        <h5 class="modal-title">Insertar Firmantes</h5>
					        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
					    </div>
					    <div class="modal-body">
					      	<div class="row" id="divFiel">
								<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
									<strong>Firma Electrónica:&nbsp;</strong><input type="checkbox" id="chk_esFIEL" name="chk_esFIEL" class="form-check-input"/>
								</div>
							</div>
							<div class="row">
								<div class="input-group">
									<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">																
										<label for="cboElabora" class="form-label">Elaboró:&nbsp; </label>
										<div class="input-group">
											<span class="input-group-text"><i class="bi bi-person"></i></span>																												
											<select id="cboElabora" name="cboElabora" onchange="infoEmpleado('ELAB');" class="form-select"> </select>
										</div>											
									</div>
									<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">																
										<label for="cboVoBo" class="form-label">Revisó:&nbsp; </label>			
										<div class="input-group">
											<span class="input-group-text"><i class="bi bi-person"></i></span>																									
											<select id="cboVoBo" name="cboVoBo" onchange="infoEmpleado('VOBO');" class="form-select"> </select>
										</div>		
									</div>
									<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">																
										<label for="cboAutoriza" class="form-label">Autorizá:&nbsp; </label>
										<div class="input-group">
											<span class="input-group-text"><i class="bi bi-person"></i></span>																												
											<select id="cboAutoriza" name="cboAutoriza" onchange="infoEmpleado('AUT');" class="form-select"> </select>
										</div>		
									</div>
								</div>
							</div>
							<div class="form-group">
								<div class="row">
									<div class="input-group">
										<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
											<div class="input-group">
												<span class="input-group-text"><i class="bi bi-briefcase"></i></span>																															
												<input type="text" name="cPuestoELAB" id="cPuestoELAB" class="form-control"/>
											</div>											
										</div>
										<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
											<div class="input-group">
												<span class="input-group-text"><i class="bi bi-briefcase"></i></span>																																
												<input type="text" name="cPuestoVOBO" id="cPuestoVOBO" class="form-control"/>
											</div>		
										</div>
										<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
											<div class="input-group">
												<span class="input-group-text"><i class="bi bi-briefcase"></i></span>																																	
												<input type="text" name="cPuestoAUT" id="cPuestoAUT" class="form-control"/>
											</div>		
										</div>
									</div>
								</div>
							</div>
					    </div>
						<div class="modal-footer">
				       		<button type="button" id="btnAceptarEdicion" onclick="aceptarFirmantes();" class="btn btn-primary">Aceptar</button>
				        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
				       </div>
			    </div>
		  </div>
	</div>
	
	<div id="dialog-importaCB" title="Adjunta Archivo PDF">
		<div id="uploadCB">
			<iframe id="uploadConciliacionFrm" src="CargaCB.jsp?ctaBan=1223&mes=1" align="top" frameborder="0" height="550" width="500"> 
			</iframe>
		</div>
	</div>

</body>
</html>


