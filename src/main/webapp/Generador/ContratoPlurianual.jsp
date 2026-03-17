
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page	import="com.syc.gestion.core.*"%>
<%@page	import="com.syc.gestion.servlet.*"%>
<%@page	import="com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.jenkov.prizetags.tree.itf.ITree"%>
<%
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());	
	
	String cCentroContable = "";
	String aEjercicioFiscal = "";
	String cUR = "";
	String cRamo = "";
	String U_LOGIN = "";
	boolean bErrorAdec=false;
	
	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
	
	if( Integer.parseInt( aEjercicioFiscal ) != c1.get(Calendar.YEAR) )
		today = "31/12/" + aEjercicioFiscal;
		
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	int idCaso = c.getIdCaso();
	final int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
	
	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	U_LOGIN = usuario.getLogin();
	
	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();
	
	//Valida Centro de Costos
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	
	FortimaxFile[] archivoANEXOS = null; 	
	
	int nIdDocumento = 0;
			
	nIdDocumento = cbl.buscaIdDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, "ANEXOS");
	archivoANEXOS = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, nIdDocumento);
	
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Solicitud de Contratos Plurianuales y Especiales</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/css/bootstrap-datetimepicker.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>

<style type="text/css">
.centerCls {
	text-align: center;
}

.rightCls {
	text-align: right;
}

.leftCls {
	text-align: left;
}

</style>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/ContratoPlurianual.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script> 
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/validaciones.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript">

	var rechazo = 0;
	var valida = 0;
	var idoper = "<%=id_oper%>";
	var totalContrato = 0;
	let modalPartidas;
	let modalFirmantes;
	
	$(document).ready(function() {
		//Inicializa las ayudas. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.AyudaSyC").subIniciaDlg();
		//Inicializa el autocompletar. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.autoCompletaSyC").subIniciaAutoCompleta();		
		
		$("#tabs").tabs( {
			"show": function(event, ui) {
	    		var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
	    		if ( oTable.length > 0 ) {
	    			oTable.fnAdjustColumnSizing();
	    		}
			}
		} );
		
		document.getElementById("presupuesto").disabled = true;
		queryFormPost("consultaJustificacion", {asyn:false});
		queryFormPost("consultaFundamento", {asyn:false});
		
		modalPartidas = new bootstrap.Modal(document.getElementById('dlg-capturaImportes'), 'data-bs-backdrop');
		modalFirmantes = new bootstrap.Modal(document.getElementById('dlgFirmanteSol'), 'data-bs-backdrop');
		
		$("#btnMontoEjercicio").button(); 
		
		$(".cardBtn").hide();
		
		setFechas();
		creaDlgMotivoRechazo();
		creaDataTableClaves();
		cargaEjerciciosMontosTabla();
		cargarClaves();	
		
		$("#rdoAbierto").change(function() {
			if ($("#rdoAbierto").prop("checked")) {
				$("#rdoCerrado").prop("checked", false);
				$(".minmax").show();
				
			}
		});
		
		$("#rdoCerrado").change(function() {
			if ($("#rdoCerrado").prop("checked")) {
				$("#rdoAbierto").prop("checked", false);
				$(".minmax").hide();
				
			}

		});
		
		$("#mTotalContrato").change(function(){
			validaMontoTotalContrato();
		});
		
		$("#cboEjerFiscal").change(function(){
			$("#importeCapturado").val("0");
			
		});
		
		$("#edit").hide();
				
		$(".pasoDos").hide();
		parent.document.getElementById("pb_save").disabled=true;		
	});
	
	function onPostDisplay(id_oper) {
		if (id_oper == 2|| id_oper == 3)
			parent.document.getElementById("pb_send").click();
		
	}
	//Boton Enviar
	function onPostSubmit(id_oper) {
		
		return true;
	}
	
	// funcion para cargar la plantilla
	function onLoadPlantilla(id_oper) {
		//Captura		
		if(id_oper == 1){
			$("#existeFolio").val("");
			queryFormPost("existeFolioContratoPlurianual_Read", {async:false});
			
			if($("#existeFolio").val() == "1"){
				//Cargar Informacion del Folio.
				cargaInfoFolio();
				$(".cardBtn").show();
				
			}	
			//Validación		
		} else if(id_oper == 2){
			$("#fieldsetVal").show();
			$("#fieldImporte").hide();
			$("#fieldsClave").hide();
			cargaInfoFolio();
			habilitarCamposAlValidar(true);
			
			//Autorización
		} else if(id_oper == 3){
			$("#fieldsetAut").show();
			$("#fieldFolios").show();
			$("#fieldImporte").hide();
			$("#fieldsClave").hide();
			cargaInfoFolio();
		
			//Consulta
		} else if(id_oper == 4){
			habilitarCampos(false);
			document.getElementById("folioMASCP").disabled = true;
			document.getElementById("oficioDG").disabled = true;
			$("#fieldFolios").show();
			$("#fieldImporte").hide();
			$("#fieldsClave").hide();	
			cargaInfoFolio();
		}		
	}

	/**
	 * 1
	 * Funion llamada al momento de guardar.
	 * Realiza validaciones,si todo es correcto, regresar true para que continue con el flujo
	 */
	function onSubmit(id_oper) {		
		var p = window.parent;
	  	var valida_campos = true;
	  	
	  	//Guardado de los campos correspondientes a cada variable de caso
		p.gestion.setFolio( $("#FOLIO").val() );
		p.gestion.setOperador( $("#OPERADOR").val() );
		p.gestion.setFechaDocumento( $("#FECHA_DOCUMENTO").val() );
		p.gestion.setEjercicioFiscal( "<%=aEjercicioFiscal%>" );
		p.gestion.setConceptoMov("Documentaci&oacuten Contrato Plurianual");
		p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
		p.gestion.setFechaApCont( $("#FECHA_DOCUMENTO").val() );
		p.gestion.setAplicadoCont("false");
	  	
		try{
			//validaciones de la forma
			var msgAlert="";
			var bHabilitaBotones = false;
			if(id_oper == 1){
			
				if($("#existeFolio").val() == "1"){
					let correcto = updateFolioContratoPLU();					
					if( correcto) {
						updateFirmantes();
						$(".cardBtn").show();
						
					}
					else {
						throw "No se puedo actualizar la información. Consulte al administrador"
					}
						
				}else {						
					valida_campos = cmdGuardar();
					
					if(valida_campos){
						updateFirmantes();	
						$("#btnExportarSol").show();
					} else {
						throw "No se guardo la solicitud, ocurrio un error.";
					}
				}
				
			}else if(id_oper == 2){
				if(!$("#rdoValida").prop("checked") && !$("#rdoRechazar").prop("checked")){
					Swal.fire("Seleccione","Debe marcar Validar o Rechazar para continuar.","warning");
					valida_campos = false;
					return;
				}else{
					revisaValidacion();
					if(valida){	
						Swal.fire("Ok","La Validación de la Solicitud es correcta.","success");					
						parent.document.getElementById("pb_save").disabled=true;
						parent.document.getElementById("pb_send").disabled=false;						
					}else{
						Swal.fire("OK","La Solicitud se regresara a captura.","success");
						parent.document.getElementById("pb_send").disabled=false;
						parent.document.getElementById("pb_save").disabled=true;
						
					}					
				}
			}else if(id_oper == 3){
				if(!$("#rdoAutoriza").prop("checked") && !$("#rdoCorrige").prop("checked")){
					Swal.fire("Capturar","Debe marcar Autorizar o Rechazar para continuar.","warning");
					valida_campos = false;
					return;
				}else{
					revisaRechazo();
					if(!rechazo){
						if(isEmpty("folioMASCP")){
							Swal.fire("Capturar","Debe capturar el Folio MASCP para continuar.","info");
							valida_campos = false;
							return;
						}else if(isEmpty("oficioDG")){
							Swal.fire("Capturar","Debe capturar el Oficio DG para continuar.","info");
							valida_campos = false;
							return;
						}else if(<%=archivoANEXOS.length==0%>){
							Swal.fire("Capturar","Debe Adjuntar los ANEXOS para continuar.","info");
							valida_campos = false;
							return;
						}
						aplicarContratoPlurianual("S");
						Swal.fire("Solicitud Aplicada.","La Solicitud de Contrato Plurianual ha sido Autorizada.","success");
					}else{
						aplicarContratoPlurianual("C");
						Swal.fire("Solicitud Cancelada.","La Solicitud de Contrato Plurianual ha sido Rechazada.","success");
					}
					parent.document.getElementById("pb_save").disabled=true;
					parent.document.getElementById("pb_send").disabled=false;
				}
			}
						
	  	}		
		catch (e) {
			window.alert("onSubmit: Error: " + e.message);
			return false;
		}
		return valida_campos;
	}

	/**
	 * 2
	 * Retorna el nombre del responsable siguiente.
	 */
	function ResponsableSiguiente(id_oper) {
		switch(parseInt(id_oper)){
		case 1:			
			return "VALIDA_CONPLURIANUAL";
			break;		
		case 2 :
			if(!valida)
				return "CAPTURA_CONPLURIANUAL";
			else
				return "AUTORIZA_CONPLURIANUAL";
			break;
					
		case 3 :
			return "CONSULTA_CONPLURIANUAL";
			break;
		}
	}

	/**
	 * 3
	 * Retorna el nombre de la operacion siguiente.
	 */
	function OperacionSiguiente(id_oper) {
		switch(parseInt(id_oper)){
		case 1:
			return "valida_conplurianual";
			break;		
		case 2 :			
			if(!valida)
				return "captura_conplurianual";
			else
				return "autoriza_conplurianual";						
			break;
					
		case 3 :
			return "consulta_conplurianual";
			break;
		}
	}
		
		
</script>

</head>

<body id="dt_example" bgColor="red" >
	<form id="rptExcelForm" method="post" target="_blank" action="../plurianuales/SolicitudPlurianual">
	</form>
	<form id="frmContratosPlurianuales" name="frmContratosPlurianuales">
		<div id="container" class="container" style="width: 85%;" >
			<input type="hidden" name="FOLIO" id="FOLIO" value="<%=folio%>" />
			<input type="hidden" name="nFolioPago" id="nFolioPago" value="<%=folio%>" />
			<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0) == null ? " caso operacion nulo " : c.getCasoOperacion(0).getResponsable()%>" />
			<input type="hidden" name="FECHA_DOCUMENTO" id="FECHA_DOCUMENTO" value="<%=today%>" />
			<input type="hidden" name="EJERCICIO_FISCAL" id="EJERCICIO_FISCAL" value="<%=aEjercicioFiscal%>" />
			<input name="cDocumento" type="hidden" id="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>"/>
			<input type="hidden" name="aEjercicioFiscal" id="aEjercicioFiscal" value="<%=aEjercicioFiscal%>" />
			<input type="hidden" name="id_caso" id="id_caso" value="<%=idCaso%>" />
			<input type="hidden" name="cRamo" id="cRamo" value="<%=cRamo%>" />
			<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value="<%=cUR%>" />
			<input type="hidden" name="cCentroContable" id="cCentroContable" value="<%=cCentroContable%>" />
			<input type="hidden" name="fCaptura" id="fCaptura" value="<%=today%>" />
			<input type="hidden" name="u_login" id="u_login" value="<%=U_LOGIN%>" />
			<input type="hidden" name="importeAcumulado" id="importeAcumulado" value="" />
			<input type="hidden" name="existeFolio" id="existeFolio" value="" />
			<input type="hidden" name="aEjercicioMonto" id="aEjercicioMonto" value="" />
			<input type="hidden" name="nTipoContrato" id="nTipoContrato" value="" />
			<input type="hidden" name="nSolicitud" id="nSolicitud" value="" />
			<input type="hidden" name="nEspecificacion" id="nEspecificacion" value="" />
			<input type="hidden" name="nTipoMoneda" id="nTipoMoneda" value="1" />
			<input type="hidden" name="cEstatus" id="cEstatus" value="A" />
			<input type="hidden" name="bExisteEjer" id="bExisteEjer" value="0" />
			<input type="hidden" name="bExiste" id="bExiste" value="0" />
			<input type="hidden" name="mTotal" id="mTotal" value="0" />
			<input type="hidden" name="renglon" id="renglon" value="0" />
			<input type="hidden" name="importeEjercicio" id="importeEjercicio" value="0" />
			<input type="hidden" name="mMontoMinUpdate" id="mMontoMinUpdate" value="0" />
			<input type="hidden" name="mMontoMaxUpdate" id="mMontoMaxUpdate" value="0" />
			<input type="hidden" name="mTotalUpdate" id="mTotalUpdate" value="0" />
			<input type="hidden" name="fIniUpdate" id="fIniUpdate" value="0" />
			<input type="hidden" name="fFinUpdate" id="fFinUpdate" value="0" />
			<input type="hidden" name="cFundamentoMotivacion" id="cFundamentoMotivacion" value="" />
			<input type="hidden" name="cEspecificacion" id="cEspecificacion" value="" />
			<input type="hidden" name="cJustificacionEconomica" id="cJustificacionEconomica" value="" />
			<input type="hidden" name="cJustificacionPlazo" id="cJustificacionPlazo" value="" />
			<input type="hidden" name="cMotivoRechazo" id="cMotivoRechazo" value="" />
			<input type="hidden" name="cDocumentoHaplicado" id="cDocumentoHaplicado" value="" />
			<input type="hidden" id="firmanteExiste" name="firmanteExiste" value=""/>
			<input type="hidden" name="EP" id="EP" value="" />			
			<input type="hidden" name="mImporteClaves" id="mImporteClaves" value="0" />			
			<input type="hidden" name="renglon" id="renglon" value="1" />
			<input type="hidden" name="nDocRenglon" id="nDocRenglon" value="" />
			<input type="hidden" name="EPs" id="EPs" value="" />
			<input type="hidden" name="EPCorta" id="EPCorta" value="" />
			<input type="hidden" name="ejerFiscal" id="ejerFiscal" value="" />
			<input type="hidden" name="mMes1" id="mMes1" value="" />
			<input type="hidden" name="mMes2" id="mMes2" value="" />
			<input type="hidden" name="mMes3" id="mMes3" value="" />
			<input type="hidden" name="mMes4" id="mMes4" value="" />
			<input type="hidden" name="mMes5" id="mMes5" value="" />
			<input type="hidden" name="mMes6" id="mMes6" value="" />
			<input type="hidden" name="mMes7" id="mMes7" value="" />
			<input type="hidden" name="mMes8" id="mMes8" value="" />
			<input type="hidden" name="mMes9" id="mMes9" value="" />
			<input type="hidden" name="mMes10" id="mMes10" value="" />
			<input type="hidden" name="mMes11" id="mMes11" value="" />
			<input type="hidden" name="mMes12" id="mMes12" value="" />
			<input type="hidden" name="mImporteTotal" id="mImporteTotal" value="0" />
			<input type="hidden" name="cAniosCapturados" id="cAniosCapturados" value="" />
			<input type="hidden" name="cNumEjercicio" id="cNumEjercicio" value="" />
			<input type="hidden" name="anioInicial" id="anioInicial" value="<%=aEjercicioFiscal%>" />
			<input name="numPaso" type="hidden" id="numPaso" value="1" size="5"	readonly />
			<input type="hidden" name="mImporteEjercicio" id="mImporteEjercicio" value="" />
			<input type="hidden" name="cDescripcionProyecto" id="cDescripcionProyecto" value="" />
			<input type="hidden" name="nNumEjercicios" id="nNumEjercicios" value="" />	
			<input type="hidden" name="cJustifSolicitud" id="cJustifSolicitud" value="" />
			<input type="hidden" name="mTotalAnio" id="mTotalAnio" value="0" />	
			<input type="hidden" name="mTotalEP" id="mTotalEP" value="0" />
			<input type="hidden" name="aEjercicioBorrar" id="aEjercicioBorrar" value="" />		
			<input type="hidden" id="cNombreS" name="cNombreS"  />
			<input type="hidden" id="cPaternoS" name="cPaternoS"  />
			<input type="hidden" id="cMaternoS" name="cMaternoS"  />
			<input type="hidden" id="cPuestoS" name="cPuestoS"  />
			<input type="hidden" id="firmanteSol" name="firmanteSol"  />
			<input type="hidden" id="existeFirmanteSol" name="existeFirmanteSol" value="NOEXISTE" />		
			
			<h5>Solicitud de Contratos Plurianuales</h5>
			<div class="row">	
				<div class="col-6">			
					<div class= "card" id="fieldsetVal" style="display: none;">
						<div class="card-header">
						    Validaci&oacuten
						</div>
						<div class="card-body">
							<div class="row">
								<div class="col-6">
									Validar
									<input type="radio" id="rdoValida" name="rdoValidar" value="Validar" /> 
								</div>
								<div class="col-6">
									Rechazar
									<input type="radio" id="rdoRechazar" name="rdoValidar" value="Rechazar" onclick="capturaRechazo()"/>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row">	
				<div class="col-6">	
					<div class= "card" id="fieldsetAut" style="display: none;">
						<div class="card-header">
						    Autorizaci&oacuten
						</div>
						<div class="card-body">
							<div class="row">
								<div class="col-6">
									Autorizar
									<input type="radio" id="rdoAutoriza" name="rdoAutorizar" value="Autorizar" checked> 
								</div>
								<div class="col-6">
									Rechazar
									<input type="radio" id="rdoCorrige" name="rdoAutorizar" value="Corregir" onclick="capturaRechazo()"/>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-6">
					<div class= "card" id="fieldFolios" style="display: none;">
						<div class="card-header">
						    Folios Autorización
						</div>
						<div class="card-body">
							<div class="row">
								<div class="col-6">
									<div class="input-group">
										<div class="col-6">
										*Folio MASCP:
										</div>
										<div class="col-6">
										<input type="text" id="folioMASCP" name="folioMASCP" class="form-control"/>
										</div>
									</div>
								</div>
								<div class="col-6">
									<div class="input-group">
										<div class="col-6">
										Oficio DG:
										</div>
										<div class="col-6">
											<input type="text" id="oficioDG" name="oficioDG" class="form-control"/>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>	
				</div>
			</div>
			<div class="row">
				<div class="col-2">
					Folio SAI:
					<input type="text" class="form-control"  id="folioSAI" name="folioSAI" size=20 class="notEditable" value="<%=folio%>" />
				</div>
				<div class="col-7">
				</div>
				<div class="col-3">
					Presupuesto:
					<input type="text" class="form-control" id="presupuesto" name="presupuesto" size=20 class="notEditable"/>
				</div>
			</div>
			<div class="row">
				<div class="col-12">
					<label class="form-check-label" for="cDescProyecto">*Nombre del Proyecto:</label>
					<textarea cols=188 rows=2 id="cDescProyecto" name="cDescProyecto" class="form-control"></textarea>
				</div>
			</div>
			<div class="row">
				<div class="col-12">
					*Justificaci&oacuten de la Solicitud:
					<textarea cols=187 rows=5 id="cJustificaSolicitud" name="cJustificaSolicitud" class="form-control"></textarea>
				</div>
			</div>		
			<div class="row">
				<div class="col-12">
					<textarea cols=187 rows=7 id="cJustificacion" name="cJustificacion" class="form-control" readonly></textarea>
				</div>
			</div>	
			
			<div class= "card mt-2">
				<div class="card-body">
					<div class="row">
					<div class="col-2">
					*Fecha Inicio:
					<input type="text" id="fInicio" name="fInicio" class="form-control"  size=10 readonly />
				</div>
				<div class="col-2">
					*Fecha Fin:
					<input type="text" id="fFin" name="fFin" class="form-control" size=10 readonly/>
				</div>
				<div class="col-6">
					<div class="d-flex justify-content-center">
						<div class="col-3">
							Tipo de Contrato:
							<br>
						</div>
						<div class="col-2">
							<input type="radio" class="form-check-input"  id="rdoAbierto" name="tipoContrato" value = "Abierto" checked="checked"/>
							<label class="form-check-label" for="rdoAbierto">Abierto</label>												
						</div>
						<div class="col-2">
							<input type="radio" class="form-check-input"  id="rdoCerrado" name="tipoContrato" value = "Cerrado" />
							<label class="form-check-label" for="rdoCerrado">Cerrado</label>							
						</div>
					</div>
					</div>
				</div>
				</div>
			</div>	
			<div class= "card">
				<div class="card-body">
					<div class="row">
						<div class="col-3">
							*Solicitud
							<select class="form-select form-select-sm" name="cboSolicitud" id="cboSolicitud" " >								
								<option value = "1" >1 .- PLURIANUAL</option>								
							</select>
						</div>
						<div class="col-3">
							*Especificaci&oacuten
							<select class="form-select form-select-sm" name="cboEspecificacion" id="cboEspecificacion"  >
								<option value = "0" >--Seleccione--</option>
								<option value = "1" >1 .- ADQUISICION</option>
								<option value = "2" >2 .- SERVICIOS</option>
								<option value = "3" >3 .- OBRAS</option>
								<option value = "4" >4 .- ARRENDAMIENTOS</option>
							</select>
						</div>
						<div class="col-3">
							*Tipo de Moneda:
							<select class="form-select form-select-sm" name="cboTipoMoneda" id="cboTipoMoneda" >
								<option value = "1" >1 .- Pesos Mexicanos</option>
							</select>
						</div>
					</div>
				</div>
			</div>
			<div class= "card cardBtn" id="cardButton">
				<div class="card-body">
					<div class="row">
						<div class="col-3">
							<input type="button" id="btnImprimirRpt" name="btnImprimirRpt" value="Imprimir Solicitud" onclick="cmdImprimir();" class="btn btn-secondary"/>
						</div>
						<div class="col-3">
							<input type="button" id="btnExportarSol" name="btnExportarSol" value="Exportar Solicitud" onclick="exportarSolicitudExcel()" class="btn btn-primary"/>
						</div>
						<div class="col-2">
						</div>
						<div class="col-3">
							<input type="button" id="btnFirmantes" name="btnFirmantes" value="Actualizar Firmantes" onclick="updateFirmantes();" class="btn btn-dark"/>
						</div>
					</div>
				</div>
			</div>
			<div class= "card">
				<div class="card-body">
					<div class="row">
						<div class="col-3">
							<label id="lblmMinimo" class="minmax">*Monto M&iacutenimo del Contrato:</label>
							<div class="input-group minmax">
								<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
								<input type="text" class="form-control" id="mMontoMinimo" name="mMontoMinimo" style="text-align:right" value="0" onfocus="Sinfrmt(this)"
														onblur="cambiafrmt(this);"/>
							</div>
						</div>
						<div class="col-3">
							<label id="lblmMaximo" class="minmax">*Monto M&aacuteximo del Contrato:</label>
							<div class="input-group minmax">
								<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
								<input type="text" class="form-control" id="mMontoMaximo" name="mMontoMaximo" style="text-align:right"  value="0" onfocus="Sinfrmt(this)"
													onblur="cambiafrmt(this);" />
							</div>
						</div>
						<div class="col-3">
							* Monto total del Contrato:
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
								<input type="text" class="form-control" id="mTotalContrato" name="mTotalContrato" value="0"  style="text-align:right" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" />
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div id="tabs"> 
				<ul>
					<li><a href="#tabs-1" >Fundamento y Motivaci&oacuten</a></li> 
					<li><a href="#tabs-2"> a) Especificaci&oacuten</a></li>
					<li><a href="#tabs-3"> b) Justificaci&oacuten Ventajas Econ&oacutemicas</a></li>
					<li><a href="#tabs-4"> c) Justificaci&oacuten del plazo</a></li>
					<li><a id = "tabImporte" href="#tabs-Importe" class ="pasoDos"> Importes por ejercicio</a></li>
					<li><a id = "tabDesglose" href="#tabs-Desglose" class = "pasoDos"> Desglose del gasto</a></li>
					
				</ul>
				<div id="tabs-1">
					<div class="row">
						<div class="col-12">
							<textarea class="form-control" cols=150 rows=10 id="cFundamentoMotiv" name="cFundamentoMotiv" readonly></textarea>
						</div>
					</div>
				</div>
				<div id="tabs-2">
					<div class="row">
						<div class="col-12">
							<b>a) La especificaci&oacuten de las obras, adquisiciones, arrendamientos o servicios, señalando si corresponden a inversi&oacuten o gasto corriente.</b>
							<textarea class="form-control" cols=150 rows=10 id="cEspecif" name="cEspecif"></textarea>
						</div>
					</div>
				</div>
				<div id="tabs-3">
					<div class="row">
						<div class="col-12">
							<b>b) La justificaci&oacuten de que la celebraci&oacuten de dichos compromisos representa ventajas econ&oacutemicas o que sus t&eacuterminos y condiciones son m&aacutes favorables respecto a la celebraci&oacuten de dichos contratos por un solo ejercicio fiscal.</b>
							<textarea class="form-control" cols=150 rows=10 id="cJustifEconomica" name="cJustifEconomica"></textarea>
						</div>
					</div>
				</div>
				<div id="tabs-4">
					<div class="row">
						<div class="col-12">
							<b>c)	La justificaci&oacuten del plazo de la contrataci&oacuten y de que el mismo no afectar&aacute negativamente la competencia econ&oacutemica del sector de que se trate.</b>
							<textarea class="form-control" cols=150 rows=10 id="cJustifPlazo" name="cJustifPlazo"></textarea>
						</div>
					</div>
					<div class="row mt-2">
						<div class="col-12">
							<input type="button" id="btnGuardar" name="btnGuardar" value="Guardar" onclick="cmdAvanzar()" class="btn btn-secondary" style="FONT-SIZE: 11pt;"/>
						</div>
					</div>
				</div>
				
				<div id = "tabs-Importe">
					<div class="row">
						<div class="col-3">
							Año:
							<select class="form-select form-select-sm" name="cboEjercicioMontos" id="cboEjercicioMontos" style="width: 125px"> 
								<option value = "0" >--Seleccione--</option>								
							</select>
						</div>
						<div class="col-3">
							<label id="lblmMin" class="minmax">Monto M&iacutenimo del Ejercicio:</label>
							<div class="input-group minmax">
								<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
								<input type="text" class="form-control" id="mMontoMin" name="mMontoMin" size="20" style="text-align:right" value="0" onfocus="Sinfrmt(this)"
																	onblur="cambiafrmt(this);"/>
							</div>
						</div>
						<div class="col-3">
							<label id="lblmMax" class="minmax">Monto M&aacuteximo del Ejercicio:</label>
							<div class="input-group minmax">
								<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
								<input type="text" class="form-control" id="mMontoMax" name="mMontoMax" size="20" style="text-align:right" value="0" onfocus="Sinfrmt(this)"
												onblur="cambiafrmt(this);"/>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-3">
							Importe Ejercicio:
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
								<input type="text" class="form-control" maxlength="30" id="mMontoEjercicio" name="mMontoEjercicio" style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" />
							</div>
						</div>
						<div class="col-3">
							<br>
							<input type="button" class="form-control" name="btnMontoEjercicio" id="btnMontoEjercicio" onclick="agregarImporteEjercicio()"  value="Agrega Importe Ejercicio" class="btn btn-secondary"/>
						</div>
					</div>
					<br>
					<table id="dt_MontosEjercicios" class="display">
							<thead>
								<tr align="center">								
									<th>Año</th>
									<th>Importe Ejercicio</th>
									<th>Renglon</th>
									<th>Monto Min</th>
									<th>Monto Max</th>
									<th class="eliminar">Eliminar</th>
								</tr>
							</thead>
							<tbody>
							</tbody>
					</table>
					<div class="row">
						<div class="col-3">
							 Importe total del contrato :
							 <div class="input-group">
								<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
							 	<input type = "text" class="form-control" name = "montoTotal" id = "montoTotal" style="text-align:right" value="0" />
							 </div>  
						</div>
					</div>
				</div>
				<div id="tabs-Desglose" >
					<h5>Clave Presupuestal</h5>
					<div class="row">
						<div class="col-12">
							d)	El desglose del gasto que debe consignarse a precios del año, tanto para el ejercicio fiscal como para los subsecuentes, a&iacute como, en el caso de obra p&uacuteblica, los avances f&iacutesicos esperados. Los montos deber&aacuten presentarse en moneda nacional y, en su caso, en la moneda prevista para su contrataci&oacuten. Las dependencias y entidades deber&aacuten presupuestar el gasto para los ejercicios subsecuentes conforme al inciso d) anterior.
						</div>
					</div>
					<div class="row mt-2" id="fieldsClave">
						<div class="col-2">
							<div class="input-group mb-3">
								Año:
								<select class="form-select form-select-sm" name="cboEjerFiscal" id="cboEjerFiscal" style="width: 125px" > 
									<option value = "0" >--Seleccione--</option>								
								</select>
							</div>
						</div>
						<div class="col-6">
							<div class="input-group mb-3">
								Clave:
								<input type="text" maxlength="60" size="45" class="form-control" id="clavePresupuestal" name="clavePresupuestal" />
								<input name="btnBuscarClave" type="button" id="btnBuscarClave" onclick="BuscarClavePresupuestal(); "  value="..." size="5" class="btn btn-secondary"/>
							</div>
						</div>
						<div class="col-2">
							<input name="btnAgregar" type="button" id="btnAgregar" onclick="agregarImportes()"  value="Capturar Importes" class="btn btn-secondary"/>
						</div>
					</div>
					<div class="table-responsive text-nowrap">
						<table id="dt_Claves" class="display">
							<thead>
								<tr align="center">								
									<th>Clave Presupuestal</th>
									<th>Año</th>
									<th>Total</th>
									<th>Enero</th>
									<th>Febrero</th>
									<th>Marzo</th>
									<th>Abril</th>
									<th>Mayo</th>
									<th>Junio</th>
									<th>Julio</th>
									<th>Agosto</th>
									<th>Septiembre</th>
									<th>Octubre</th>
									<th>Noviembre</th>
									<th>Diciembre</th>								
									<th>EP</th>
									<th>Renglon</th>
									<th class="eliminar">Eliminar</th>
								</tr>
							</thead>
						</table>
					</div>
					
				</div>
				
			</div>	
		
			<div id="capturaRechazo_DIV">
				<fieldset>
					<legend>Capture Motivo Rechazo</legend>
					<table>
						<tr>
							<td align="left"> Motivo: </td>
						</tr>
						<tr>
							<td align="right">
								<textarea cols=50 rows=6 class="form-control" id="motivoRechazo" name="motivoRechazo"></textarea>
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
			
			<div class="modal" tabindex="-1" role="dialog" id="dlg-capturaImportes" data-mdb-keyboard="true" data-mdb-backdrop="static">
			  	<div class="modal-dialog" role="document">
			    	<div class="modal-content">
				      	<div class="modal-header">
				        	<h5 class="modal-title">Capture Importes Mes</h5>
				        	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				    	</div>
				    	<div class="modal-body">
					      	<div class="row">
						      	<div class="col-12">
						      		Clave Presupuestal:
									<input type="text" class="form-control" id="clavePresup" name="clavePresup" size=45 class="notEditable"/>
						      	</div>
					      	</div>
					      	<div class="row">
					      		<div class="col-4">
						      		Ejercicio Fiscal:
									<input type="text" class="form-control" id="ejercicioFiscal" name="ejercicioFiscal" value=""/>
						      	</div>
						      	<div class="col-4">
						      		Importe Ejercicio:
									<input type="text" class="form-control" id="importeEjerEP" name="importeEjerEP" size=15 style="text-align:right" value="0" onblur="cambiafrmt(this);" readonly/>
						      	</div>
						      	<div class="col-4">
						      		Importe Capturado:
									<input type="text" class="form-control" id="importeCapturado" name="importeCapturado" size=15 style="text-align:right" value="0" onblur="cambiafrmt(this);" readonly/>
						      	</div>
					      	</div>
					      	<div class="row">
						      	<div class="col-4">
						      		Enero:
									<input type="text" class="form-control" id="enero" name="enero" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"/>
						      	</div>
						      	<div class="col-4">
						      		Febrero:
									<input type="text" class="form-control" id="febrero" name="febrero" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"/>
						      	</div>
						      	<div class="col-4">
						      		Marzo:
									<input type="text" class="form-control" id="marzo" name="marzo" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"/>
						      	</div>
						      	
					      	</div>
					      	<div class="row">
						      	<div class="col-4">
						      		Abril:
									<input type="text" class="form-control" id="abril" name="abril" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);"  onchange="sumaMeses(this);"/>
						      	</div>
						      	<div class="col-4">
						      		Mayo:
									<input type="text" class="form-control" id="mayo" name="mayo" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"/>
						      	</div>
						      	<div class="col-4">
						      		Junio:
									<input type="text" class="form-control" id="junio" name="junio" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"/>
						      	</div>
					      	</div>
					      	<div class="row">
						      <div class="col-4">
						      		Julio:
									<input type="text" class="form-control" id="julio" name="julio" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"/>
						      	</div>
						      	<div class="col-4">
						      		Agosto:
									<input type="text" class="form-control" id="agosto" name="agosto" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"/>
						      	</div>
						      	<div class="col-4">
						      		Septiembre:
									<input type="text" class="form-control" id="septiembre" name="septiembre" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"/>
						      	</div>
					      	</div>
					      	<div class="row">
						      	<div class="col-4">
						      		Octubre:
									<input type="text" class="form-control" id="octubre" name="octubre" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"/>
						      	</div>
						      	<div class="col-4">
						      		Noviembre:
									<input type="text" class="form-control" id="noviembre" name="noviembre" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"/>
						      	</div>
						      	<div class="col-4">
						      		Diciembre:
									<input type="text" class="form-control" id="diciembre" name="diciembre" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"/>
						      	</div>
					      	</div>
				    	</div>
				      	<div class="modal-footer">
				        	<button type="button" id="btnAceptar" onclick="aceptarImporte();" class="btn btn-primary">Guardar</button>
				        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
				      	</div>
			    	</div>
			  	</div>
			</div>
		
			
	<div class="modal" tabindex="-1" role="dialog" id="dlgFirmanteSol" data-mdb-keyboard="true" data-mdb-backdrop="static">
		  	<div class="modal-dialog" role="document">
		    	<div class="modal-content">
			      	<div class="modal-header">
			        	<h5 class="modal-title">Datos Solicita</h5>
			        	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			    	</div>
			    	<div class="modal-body">
				      	<div class="row">		
							<div class="col-12">
								Nombre:
								<input type="text" class="form-control" id="cNombreSol" name="cNombreSol" size=40/>
							</div>
							<div class="col-12">
								Apellido Paterno:
								<input type="text" class="form-control" id="cApPaternoSol" name="cApPaternoSol" size=40/>
							</div>
							<div class="col-12">
								Apellido Materno:
								<input type="text" class="form-control" id="cApMaternoSol" name="cApMaternoSol" size=40/>
							</div>
							<div class="col-12">
								Puesto:
								<input type="text" class="form-control" id="cPuestoSol" name="cPuestoSol" size=40/>
							</div>
						</div>
					</div>
					<div class="modal-footer">
			        	<button type="button" id="btnAceptarFirmante" onclick="aceptarFirmante();" class="btn btn-primary">Guardar</button>
			        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
			      	</div>
				</div>
			</div>
	</div>
						
			</div>
		
			
		</div>
	</form>
</body>

</html>
