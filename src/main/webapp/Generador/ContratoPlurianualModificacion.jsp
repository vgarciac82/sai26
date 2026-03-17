
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page	import="com.syc.gestion.core.*"%>
<%@page	import="com.syc.gestion.servlet.*"%>
<%@page	import="com.syc.gestion.util.*"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
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
<link rel="stylesheet" type="text/css" href="../css/interfaz.css"/>
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
<script type="text/javascript" src="js/Firmantes.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript">

	var rechazo = 0;
	var valida = 0;
	var idoper = "<%=id_oper%>";
	
	$(document).ready(function() {
		
		$("#tabs").tabs( {
			"show": function(event, ui) {
	    		var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
	    		if ( oTable.length > 0 ) {
	    			oTable.fnAdjustColumnSizing();
	    		}
			}
		} );
		
		
		//Inicializa las ayudas. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.AyudaSyC").subIniciaDlg();
		//Inicializa el autocompletar. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.autoCompletaSyC").subIniciaAutoCompleta();		
		
		document.getElementById("presupuesto").disabled = true;
		queryFormPost("consultaJustificacion", {asyn:false});
		queryFormPost("consultaFundamento", {asyn:false});
		
		$("#btnImprimirRpt").button();
		$("#btnExportarSol").button();
		$("#btnFirmantes").button();
		$("#btnAgregar").button();
		$("#btnGuardar").button();
		$("#btnMontoEjercicio").button();
		
		$("#btnImprimirRpt").hide();
		$("#btnExportarSol").hide();
		$("#btnFirmantes").hide();			
					
		setFechas();
		creaDlgMotivoRechazo();
		creaDlgImportes();
		creaDataTableClaves();
		creaDlgFirmanteSolicita();	
		
		$("#rdoAbierto").change(function() {
			if ($("#rdoAbierto").prop("checked")) {
				$("#rdoCerrado").prop("checked", false);
				$("#lblmMinimo").show();
				$("#mMontoMinimo").show();
				$("#lblmMaximo").show();
				$("#mMontoMaximo").show();
				$("#lblmMin").show();
				$("#mMontoMin").show();
				$("#lblmMax").show();
				$("#mMontoMax").show();
			}
		});
		
		$("#rdoCerrado").change(function() {
			if ($("#rdoCerrado").prop("checked")) {
				$("#rdoAbierto").prop("checked", false);
				$("#lblmMinimo").hide();
				$("#mMontoMinimo").hide();
				$("#lblmMaximo").hide();
				$("#mMontoMaximo").hide();
				$("#lblmMin").hide();
				$("#mMontoMin").hide();
				$("#lblmMax").hide();
				$("#mMontoMax").hide();
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
	
	function setFechas(){
		$( "#fInicio" ).datepicker({
			dateFormat: "dd/mm/yy",
			showOn: "button",
			buttonImage: "../Generador/images/calendar.gif",
			buttonImageOnly: true,
			changeYear: true, 
			changeMonth: true,
			onSelect: function(dateText, inst) {
		 		llenaFechaFin();
		 	}	
		});		
				
		$( "#fFin" ).datepicker({
			dateFormat: "dd/mm/yy",
			showOn: "button",
			buttonImage: "../Generador/images/calendar.gif",
			buttonImageOnly: true,
			changeYear: true, 
			changeMonth: true,
			onSelect: function(dateText, inst) {
		 		validarFechas();
		 	}     
		});
	}
	
	/**
	 * 4
	 * Se ejecuta despues de que termina la funcion onSubmit 
	 */
	function onPostDisplay(id_oper) {
		//alert("Se llamo a onPostDisplay");		
	}
	//Boton Enviar
	function onPostSubmit(id_oper) {
		//alert("Se llamo a onPostSubmit");		
		return true;
	}
	
	// funcion para cargar la plantilla
	function onLoadPlantilla(id_oper) {
		//Captura		
		if(id_oper == 1){
			$("#existeFolio").val('');
			queryFormPost("existeFolioContratoPlurianualMod_Read", {async:false});
			
			if($("#existeFolio").val() == "1"){
				//Cargar Informacion del Folio.
				cargaInfoFolio();				
				$("#nFolioUltimo").val($("#FOLIO").val());
				$("#btnExportarSol").show();
				$("#btnFirmantes").show();
			}	
			//Validación		
		} else if(id_oper == 2){
			$("#fieldsetVal").show();
			$("#fieldImporte").hide();
			$("#fieldsClave").hide();
			
			cargaInfoFolio();
			$("#nFolioUltimo").val($("#FOLIO").val());
			//Autorización
		} else if(id_oper == 3){
			$("#fieldsetAut").show();
			$("#fieldFolios").show();
			$("#fieldImporte").hide();
			$("#fieldsClave").hide();
			
			cargaInfoFolio();
			$("#nFolioUltimo").val($("#FOLIO").val());
			queryFormPost("consultaFolioAnterior", {async:false});
			//Consulta
		} else if(id_oper == 4){
			habilitarCampos(false);
			document.getElementById("folioMASCP").disabled = true;
			document.getElementById("oficioDG").disabled = true;
			$("#fieldFolios").show();
			$("#fieldImporte").hide();
			$("#fieldsClave").hide();
					
			cargaInfoFolio();
			$("#nFolioUltimo").val($("#FOLIO").val());
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
		p.gestion.setConceptoMov("Modificaci&oacuten Contrato Plurianual");
		p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
		p.gestion.setFechaApCont( $("#FECHA_DOCUMENTO").val() );
		p.gestion.setAplicadoCont("false");
	  	
		try{
			//validaciones de la forma
			var msgAlert="";
			var bHabilitaBotones = false;
			if(id_oper == 1){
			
				if($("#existeFolio").val() == "1"){
					updateFolioContratoPLU();					
				}else {						
					valida_campos = cmdGuardar();
					
					if(valida_campos){
						updateFirmantes();	
						$("#btnImprimirRpt").show();
						$("#btnExportarSol").show();
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
						parent.document.getElementById("pb_save").disabled=true;
						parent.document.getElementById("pb_send").disabled=false;
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
						alert("La Solicitud de Contrato Plurianual ha sido Autorizada.\nSolicitud Aplicada.");
					}else{
						aplicarContratoPlurianual("C");
						alert("La Solicitud de Contrato Plurianual ha sido Rechazada.\nSolicitud Cancelada.");
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
			return "VALIDA_MCONPLURIANUAL";
			break;		
		case 2 :
			if(!valida)
				return "CAPTURA_MCONPLURIANUAL";
			else
				return "AUTORIZA_MCONPLURIANUAL";
			break;
					
		case 3 :
			return "CONSULTA_MCONPLURIANUAL";
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
			return "valida_mconPlurianual";
			break;		
		case 2 :			
			if(!valida)
				return "captura_mconPlurianual";
			else
				return "autoriza_mconPlurianual";						
			break;
					
		case 3 :
			return "consulta_mconPlurianual";
			break;
		}
	}
	
	function aplicarContratoPlurianual(valor){
		
		$("#cDocumentoHaplicado").val(valor);
		if(valor == "S"){
			queryFormPost("folioMASCPContratoPlurianualModUpdate", {async:false});
		}		
		queryFormPost("aplicarRechazarContratoPlurianualModUpdate", {async:false});
		
		//Poner el folio anterior en estatus de modificado
		if ($("#cEsModificado").val() != 0)
			queryFormPost("modificarContratoPlurianualUpdate", {async: false});
		else 
			queryFormPost("modificarContratoPlurianualOriginalUpdate", {async: false});
		
	}
	
	function cmdAvanzar(){
		var bRegresa = true;
		var mMinimo = Number(quitaFmt($("#mMontoMinimo").val()));
		var mMaximo = Number(quitaFmt($("#mMontoMaximo").val()));		
		var montoContrato = Number( quitaFmt($("#mTotalContrato").val()) );
					
		mMinimo = mMinimo.toFixed(2);
		mMaximo = mMaximo.toFixed(2);		
		montoContrato = montoContrato.toFixed(2);
		
		$("#cDescripcionProyecto").val($("#cDescProyecto").val());
		$("#cJustifSolicitud").val($("#cJustificaSolicitud").val());
		$("#cFundamentoMotivacion").val($("#cFundamentoMotiv").val());
		$("#cEspecificacion").val($("#cEspecif").val());
		$("#cJustificacionEconomica").val($("#cJustifEconomica").val());
		$("#cJustificacionPlazo").val($("#cJustifPlazo").val());
		
		if(isEmpty("cDescripcionProyecto")){
			Swal.fire("Verifique!", "La Descripcion del Proyecto no debe quedar vacia. ","info");
			bRegresa = false;
			return;
		}else if(isEmpty("cJustifSolicitud")){
			Swal.fire("Verifique!","La Justificacion de la Solicitud no debe quedar vacia.", "info");
			bRegresa = false;
			return;
		}else if($("#fInicio").val() == ""){
			Swal.fire("Capture","Favor de capturar la Fecha Inicio.","info");
			bRegresa = false;
			return;
		}else if($("#fFin").val() == ""){
			Swal.fire("Capture","Favor de capturar la Fecha Fin.","info");
			bRegresa = false;
			return;
		}else if(!$("#rdoAbierto").prop("checked") && !$("#rdoCerrado").prop("checked")){
			Swal.fire("Capture","Favor de seleccionar el Tipo de Contrato.","info");
			bRegresa = false;
			return;
		}else if($("#cboSolicitud").val() == "0"){
			Swal.fire("Capture","Favor de seleccionar una Solicitud.","info");
			bRegresa = false;
			return;
		}else if($("#cboEspecificacion").val() == "0"){
			Swal.fire("Capture","Favor de seleccionar una Especificación.","info");
			bRegresa = false;
			return;
		}else if($("#cboTipoMoneda").val() == "0"){
			Swal.fire("Capture","Favor de seleccionar un Tipo de Moneda.","info");
			bRegresa = false;
			return;		
		}else if(Number(montoContrato) == 0){
			Swal.fire("Capture","El Monto Total por Contrato no puede ser Cero.", "warning");
			bRegresa = false;
			return;
		}else if(!validaMontoTotalContrato()){
			bRegresa = false;
			return;
		
		}else if(isEmpty("cEspecificacion")){
			Swal.fire("Verifique!","Especificación no debe quedar vacio. ","info");
			bRegresa = false;
			return;
		}else if(isEmpty("cJustificacionEconomica")){
			Swal.fire("Verifique!","Justificación Ventajas Economicas no debe quedar vacio.","info");
			bRegresa = false;
			return;
		}else if(isEmpty("cJustificacionPlazo")){
			Swal.fire("Verifique!","Justificación del Plazo no debe quedar vacio.","info");
			bRegresa = false;
			return;
		}
		
		if($("#rdoAbierto").prop("checked")){
			if(Number(mMinimo) == 0){
				Swal.fire("Verifique!","El Monto Minimo no puede ser Cero.","info");
				bRegresa = false;
				return;
			}else if(Number(mMaximo) == 0){
				Swal.fire("Verifique!","El Monto Maximo no puede ser Cero.","info");
				bRegresa = false;
				return;
			}else if (Number(mMinimo) > Number(mMaximo)){
				Swal.fire("Verifique!","El Monto Minimo no puede ser mayor al Monto Maximo. Verifique!!","info");
				bRegresa = false;
				return;
			}else if (Number(mMinimo) > Number(montoContrato)){
				Swal.fire("Verifique!","El Monto Minimo no puede ser mayor al Monto Total del Contrato. Verifique!!", "info");
				bRegresa = false;
				return;
			}
		}
		
		if($("#rdoCerrado").prop("checked")){
			$("#mMontoMinimo").val( quitaFmt($("#mTotalContrato").val()));
			$("#mMontoMaximo").val(quitaFmt($("#mTotalContrato").val()));
		}
		
		if(bRegresa){
			llenaDatos();
		}
		
		guardaEncabezado();
		$("#montoTotal").val(quitaFmt($("#mTotalContrato").val()));
		
		//Valida los totales por si es un rechazo y ya se hubieran capturado los importes de las ep y claves
		if ($("#cEsModificado").val() != 0)
			queryFormPost("consultaTotalesContratoAnt_read", {async: false});
		else 
			queryFormPost("consultaTotalesContratoOriginal_read", {async: false});
			
		var mTotalA = Number($("#mTotalAnio").val());	
		var mTotalEP = Number($("#mTotalEP").val());
		
		if(montoContrato == mTotalEP && montoContrato == mTotalA) {
			parent.document.getElementById("pb_save").disabled=false;
			parent.document.getElementById("pb_send").disabled=true;
		}
		
		validaTotales();
	}
	
	function validaTotales() {
		
		//Valida los totales por si es un rechazo y ya se hubieran capturado los importes de las ep y claves
		queryFormPost("consultaTotalesContratoMod_read", {async: false});
		var mTotalA = Number($("#mTotalAnio").val());	
		var mTotalEP = Number($("#mTotalEP").val());
		
		var montoContrato = Number( quitaFmt($("#mTotalContrato").val()) );
		montoContrato = montoContrato.toFixed(2);
		
		if(montoContrato == mTotalEP && montoContrato == mTotalA) {
			parent.document.getElementById("pb_save").disabled=false;
			parent.document.getElementById("pb_send").disabled=true;
		}
	}
	
	function guardaEncabezado() {
			
			queryFormPost ("existeFolioContratoPlurianualMod_Read", {async: false});
			
			if ($("#existeFolio").val() == 0 ) {
					//Inserta en la tabla del encabezado
					queryFormPost({ queryName : "tContratoPlurianualEncabezadoModCreate", async : false})
					//Se guarda el detalle de ejercicios y de claves
					queryFormPost({ queryName : "ContratoPlurianual_EjerciciosAnteriorCreate", async : false})
					queryFormPost({ queryName : "ClavesContrato_AnteriorCreate", async : false})				
			} else {
					updateFolioContratoPLU();
					$("#btnImprimirRpt").show();
					$("#btnExportarSol").show();
					$("#btnFirmantes").show();
			} 			
					var fInicio = $("#fInicio").val();  
					var fFin = $("#fFin").val();
					var iYearInicio = parseInt(fInicio.substr(6,4), 10);
					var iYearFin = parseInt(fFin.substr(6,4), 10);
					cargarEjercicioFiscal(iYearInicio, iYearFin);
					cargarEjercicioMontos(iYearInicio, iYearFin);
					
			
			$(".pasoDos").show();
			bloqueaBotones();
						
			Swal.fire("Ok","Se guardo el encabezado del Contrato Plurianual","success");
			$("#tabImporte").click();
		
	}
	function bloqueaBotones() {
			document.getElementById("cDescProyecto").readOnly = true;
			document.getElementById("cJustificaSolicitud").readOnly = true;
			document.getElementById("folioMASCP").readOnly = true;
			document.getElementById("oficioDG").readOnly = true;
			document.getElementById("fInicio").readOnly = true;
			document.getElementById("fFin").readOnly = true;
			document.getElementById("cboSolicitud").readOnly = true;
			document.getElementById("cboEspecificacion").readOnly = true;
			document.getElementById("cboTipoMoneda").readOnly = true;
			document.getElementById("mMontoMinimo").readOnly = true;
			document.getElementById("mMontoMaximo").readOnly = true;
			document.getElementById("cFundamentoMotiv").readOnly = true;
			document.getElementById("cEspecif").readOnly = true;
			document.getElementById("cJustifEconomica").readOnly = true;
			document.getElementById("cJustifPlazo").readOnly = true;
	}
	
	function cmdGuardar(){
		var bRegresa = true;	
		
		 if(!validarCapturaImportesEjer()){
			bRegresa = false;
			return;
		}
				
		var dtClaves = $("#dt_Claves").dataTable();
	    var iRow = $(dtClaves.fnGetNodes()).length;	
	    
	    if(iRow == 0){
	    	Swal.fire("Verifique!","Debe Capturar al menos una Clave Presupuestal.","warning");
			bRegresa = false;
			return;
	    }	
		
		if(bRegresa){
			llenaDatos();
		}
		
		return bRegresa;
		
	}
	
	function updateFolioContratoPLU(){
		
		$("#mMontoMinimo").val(quitaFmt($("#mMontoMinimo").val()));
		$("#mMontoMaximo").val(quitaFmt($("#mMontoMaximo").val()));	
		$("#mTotalContrato").val(quitaFmt($("#mTotalContrato").val()));			
		$("#cDescripcionProyecto").val($("#cDescProyecto").val());
		$("#cJustifSolicitud").val($("#cJustificaSolicitud").val());
		$("#cFundamentoMotivacion").val($("#cFundamentoMotiv").val());
		$("#cEspecificacion").val($("#cEspecif").val());
		$("#cJustificacionEconomica").val($("#cJustifEconomica").val());
		$("#cJustificacionPlazo").val($("#cJustifPlazo").val());
		
		queryFormPost({
						queryName : "folioEncContratoPluMod_Update",
						async : false,
						callback : function() {
							updateFirmantes();
						}
					});
	}
	
	function llenaDatos(){	
		$("#nSolicitud").val($("#cboSolicitud").val());
		$("#nEspecificacion").val($("#cboEspecificacion").val());
		
		if($("#rdoAbierto").prop("checked")){
			$("#nTipoContrato").val("1");	
		}else if($("#rdoCerrado").prop("checked")){
			$("#nTipoContrato").val("2");
		}
	}	
	
	function isEmpty(idCampo){
        var val = $("#"+idCampo).val();
        val = val.replace(/\s/g, "" );
        
        if( val == "" )
               return true;
        else 
               return false;
    }
    
    function revisaValidacion() {
		if($("#rdoValida").prop("checked"))
			valida=true;
		else 
			valida=false;
	}
    
    function revisaRechazo() {
		if($("#rdoAutoriza").prop("checked")){
			rechazo=false;
			//marcar como aplicado el folio.
		}else{ 
			rechazo=true;
			capturaRechazo();
		}
	}
	
	function sumaMeses( fld ){
		var importeCap = "0";
		var total =  quitaFmt($("#importeEjerEP").val());
		importeCap = parseFloat( quitaFmt($("#enero").val())) + parseFloat( quitaFmt($("#febrero").val())) +parseFloat( quitaFmt($("#marzo").val()))+parseFloat( quitaFmt($("#abril").val()))
		+parseFloat( quitaFmt($("#mayo").val())) +parseFloat(quitaFmt( $("#junio").val()))+parseFloat( quitaFmt($("#julio").val()))+parseFloat(quitaFmt( $("#agosto").val()))
		+parseFloat( quitaFmt($("#septiembre").val()))+parseFloat(quitaFmt( $("#octubre").val()))+ parseFloat( quitaFmt($("#noviembre").val()))+parseFloat( quitaFmt($("#diciembre").val()));
	   //importeCap = parseFloat(importeCap) + parseFloat( $("#" + fld.id).val());
	   $("#importeCapturado").val(addCommas(importeCap));
	   var diferencia = Math.round((total - importeCap) *100) / 100 ; 
	    $("#faltante").val(addCommas(diferencia));
	}
	
	function creaDlgMotivoRechazo() { 
      	$("#capturaRechazo_DIV").dialog({
	        title:"Motivo de Rechazo",
	        autoOpen : false,
	        height : 260,
	        width : 420,
	        modal : true,
	        buttons : {
            	"Aceptar" : function() {
                       insertMotivoRechazo();   
             	},
              	"Cancelar" : function() {
                	$(this).dialog("close");
                }
         	}
       });
 
	}
	
	function creaDlgImportes() { 
		
      	$("#capturaImportes").dialog({
	        title:"Importes Mes Clave Presupuestal ",
	        autoOpen : false,
	        height : 450,
	        width : 540,
	        modal : true,
	        buttons : {
            	"Aceptar" : function() {
            		if(validaMontoCapturado()){
            			agregarClave();
            			cargarClaves();
                    	$(this).dialog("close");
            		}                     
             	},
              	"Cancelar" : function() {
                	$(this).dialog("close");
                }
         	},
         	open : function() {
				iniciaDivImportes();
			}
       });
 
	}
	
	function iniciaDivImportes(){
		$("#clavePresup").val($("#clavePresupuestal").val());
		document.getElementById("ejercicioFiscal").disabled = true;
		$("#importeEjerEP").focus();
		$("#ejercicioFiscal").val("0");
		$("#ejercicioFiscal").val($("#cboEjerFiscal").val());
		$("#importeCapturado").val("0");
		$("#enero").val("0");
		$("#febrero").val("0");
		$("#marzo").val("0");
		$("#abril").val("0");
		$("#mayo").val("0");
		$("#junio").val("0");
		$("#julio").val("0");
		$("#agosto").val("0");
		$("#septiembre").val("0");
		$("#octubre").val("0");
		$("#noviembre").val("0");
		$("#diciembre").val("0");

	}
	

	function agregarImportes(){
		importeCap = 0;
		$("#aEjercicioMonto").val($("#cboEjerFiscal").val());
		$("#ejercicioFiscal").val($("#cboEjerFiscal").val());
		
		queryFormPost("consultaMontoEjercicioPlurianualMod", {asyn:false});
		queryFormPost("consultaMontoAcumuladoMod", {async:false})
				
		$("#capturaImportes").dialog("open");
	}
	
	function validaMontoCapturado(){		
		var importeAcum = 0;
		var bRegresa = true;
		//folio = $("#FOLIO").val();
		queryFormPost("consultaMontoAcumuladoMod", {async:false});
		$("#aEjercicioMonto").val($("#cboEjerFiscal").val());				

		if ($("#importeAcumulado").val() > 0) {
			importeAcum = $("#importeAcumulado").val();
		}
		
		var mEjercicio = Number($("#importeEjerEP").val()).toFixed(2);
		
		var mes1 = $("#enero").val(); mes1 = Number(quitaFmt(mes1));
		var mes2 = $("#febrero").val(); mes2 = Number(quitaFmt(mes2)); 
		var mes3 = $("#marzo").val(); mes3 = Number(quitaFmt(mes3)); 
		var mes4 = $("#abril").val(); mes4 = Number(quitaFmt(mes4)); 
		var	mes5 = $("#mayo").val(); mes5 = Number(quitaFmt(mes5)); 
		var mes6 = $("#junio").val(); mes6 = Number(quitaFmt(mes6)); 
		var mes7 = $("#julio").val(); mes7 = Number(quitaFmt(mes7)); 
		var mes8 = $("#agosto").val(); mes8 = Number(quitaFmt(mes8)); 
		var	mes9 = $("#septiembre").val(); mes9 = Number(quitaFmt(mes9)); 
		var mes10 = $("#octubre").val(); mes10 = Number(quitaFmt(mes10)); 
		var mes11 = $("#noviembre").val(); mes11 = Number(quitaFmt(mes11));
		var mes12 = $("#diciembre").val(); mes12 = Number(quitaFmt(mes12));
		
		
		var impteCapturado = 0.00;		
		impteCapturado = (mes1 + mes2 + mes3 + mes4 + mes5 + mes6 + mes7 + mes8 +mes9 + mes10 + mes11 + mes12  ).toFixed(2);
		
		$("#importeCapturado").val(impteCapturado);	
		
		importeAcum = Number(importeAcum) + Number(impteCapturado);
		importeAcum = Number(importeAcum).toFixed(2);
		
		if( impteCapturado > Number (mEjercicio)){
			Swal.fire("Verifique!!","La suma del importe capturado no puede revasar el importe del Ejercicio: " + $("#ejercicioFiscal").val(), "info" );
			bRegresa = false;			
		}			
		//Valida que no se pase del importe total del contrato
		if(importeAcum > totalContrato){
			Swal.fire("Verifique!!","La suma de los importes que se han capturado no pueden ser mayor al importe total del Contrato. ", "info");
			bRegresa = false;			
		}	else if (importeAcum == totalContrato) {
				parent.document.getElementById("pb_save").disabled=false;
		}						
		
		return bRegresa;
	}
	var renglonClave = 0;
	
	function agregarClave(){
		
		var impteClaves = $("#mImporteClaves").val();
		impteClaves = quitaFmt( impteClaves );
		var clave = $("#clavePresupuestal").val();		
		var EP = $("#EP").val();
		var impteTotal =  $("#importeCapturado").val();
		var ejerFiscalClave = $("#ejercicioFiscal").val();
		var mes1 = $("#enero").val(); mes1 = quitaFmt(mes1);
		var mes2 = $("#febrero").val(); mes2 = quitaFmt(mes2); 
		var mes3 = $("#marzo").val(); mes3 = quitaFmt(mes3); 
		var mes4 = $("#abril").val(); mes4 = quitaFmt(mes4); 
		var	mes5 = $("#mayo").val(); mes5 = quitaFmt(mes5); 
		var mes6 = $("#junio").val(); mes6 = quitaFmt(mes6); 
		var mes7 = $("#julio").val(); mes7 = quitaFmt(mes7); 
		var mes8 = $("#agosto").val(); mes8 = quitaFmt(mes8); 
		var	mes9 = $("#septiembre").val(); mes9 = quitaFmt(mes9); 
		var mes10 = $("#octubre").val(); mes10 = quitaFmt(mes10); 
		var mes11 = $("#noviembre").val(); mes11 = quitaFmt(mes11);
		var mes12 = $("#diciembre").val(); mes12 = quitaFmt(mes12);
	
		++renglonClave;
		$("#renglon").val(renglonClave);		
		
		cargaDatosDetalle( mes1, mes2, mes3, mes4, mes5, mes6, mes7, mes8, mes9, mes10, mes11, mes12, impteTotal);
	    
	    queryFormPost("consultaIDMod", {async:false});
	    queryFormPost("creaClavesContratoPluriMod", {async:false});

		$("#clavePresupuestal").val("");
	}
		
	function capturaRechazo(){
		$("#capturaRechazo_DIV").dialog("open");		
	}
	
	function insertMotivoRechazo(){	
		if($("#motivoRechazo").val()==""){
			Swal.fire("Capture","Favor de capturar el motivo del Rechazo.","info");
			return;			
		}
	
		$("#cMotivoRechazo").val( $("#motivoRechazo").val() );
		queryFormPost("motivoRechazoContratoPlurianualModUpdate", {async:false});
		$("#capturaRechazo_DIV").dialog("close");		
		
	}
	
	function cargaInfoAnterior() {
		queryFormPost("esContratoModificado", {async:false});
	
		var esModificado = $("#cEsModificado").val();
		if (esModificado == 0) {
			//Carga los datos del original
			queryFormPost("tContratoPlurianualOriginalFolio_Read", {async:false});
			cargarClavesAnterior();	
			cargaDetEjerciciosMontosAnt();
		} else { 
			queryFormPost("consultaFolioUlitmo", {async:false});
			queryFormPost("tContratoPlurianualModFolio_Read", {async:false});
			cargarClaves();
			cargaDetEjerciciosMontos();
		}
		var nTipoCopntrato = $("#nTipoContrato").val();
		var nSolicitud =  $("#nSolicitud").val();
		var nEspecificacion = $("#nEspecificacion").val();
		
		if(nTipoCopntrato == 1){
			$("#rdoAbierto").prop("checked", true);
			$("#rdoCerrado").prop("checked", false);
			$("#lblmMinimo").show();
			$("#mMontoMinimo").show();
			$("#lblmMaximo").show();
			$("#mMontoMaximo").show();
			$("#lblmMin").show();
			$("#mMontoMin").show();
			$("#lblmMax").show();
			$("#mMontoMax").show();
		}else if(nTipoCopntrato == 2){
			$("#rdoCerrado").prop("checked", true);
			$("#rdoAbierto").prop("checked", false);
		}
		
		$("#cboSolicitud").val(nSolicitud);
		$("#cboEspecificacion").val(nEspecificacion);	
		$("#cDescripcionProyecto").val($("#cDescProyecto").val());
		$("#cJustifSolicitud").val($("#cJustificaSolicitud").val());
		$("#cFundamentoMotiv").val($("#cFundamentoMotivacion").val());
		$("#cEspecif").val($("#cEspecificacion").val());
		$("#cJustifEconomica").val($("#cJustificacionEconomica").val());
		$("#cJustifPlazo").val($("#cJustificacionPlazo").val());
		
		cambiafrmt($("#mMontoMinimo").val());
		cambiafrmt($("#mMontoMaximo").val());
		cambiafrmt($("#mTotalContrato").val());
		
	}
	function cargaInfoFolio(){
		
		queryFormPost("tContratosPlurianualFolioMod_Read", {async:false});
		
		var nTipoCopntrato = $("#nTipoContrato").val();
		var nSolicitud =  $("#nSolicitud").val();
		var nEspecificacion = $("#nEspecificacion").val();
		
		if(nTipoCopntrato == 1){
			$("#rdoAbierto").prop("checked", true);
			$("#rdoCerrado").prop("checked", false);
			$("#lblmMinimo").show();
			$("#mMontoMinimo").show();
			$("#lblmMaximo").show();
			$("#mMontoMaximo").show();
			$("#lblmMin").show();
			$("#mMontoMin").show();
			$("#lblmMax").show();
			$("#mMontoMax").show();
		}else if(nTipoCopntrato == 2){
			$("#rdoCerrado").prop("checked", true);
			$("#rdoAbierto").prop("checked", false);
		}
		
		$("#cboSolicitud").val(nSolicitud);
		$("#cboEspecificacion").val(nEspecificacion);	
		$("#cDescripcionProyecto").val($("#cDescProyecto").val());
		$("#cJustifSolicitud").val($("#cJustificaSolicitud").val());
		$("#cFundamentoMotiv").val($("#cFundamentoMotivacion").val());
		$("#cEspecif").val($("#cEspecificacion").val());
		$("#cJustifEconomica").val($("#cJustificacionEconomica").val());
		$("#cJustifPlazo").val($("#cJustificacionPlazo").val());
		
		queryFormPost("existeEjercicioPluriM", {async:false});		
		
		if (idoper > 1 ){
			habilitarCampos(true);
			$("#btnImprimirRpt").show();
			$("#btnExportarSol").show();
			$("#btnFirmantes").show();
			$(".pasoDos").show();
			
		} else{
			
			if($("#bExiste").val()> 0) {
				totalContrato = Number(quitaFmt($("#mTotalContrato").val()));
				llenaFechaFin();
				$(".pasoDos").show();
			}
			
			document.getElementById("fInicio").readOnly = true;
			document.getElementById("fFin").readOnly = true;
		}
				
		cargarClaves();	
		cargaDetEjerciciosMontos();
	}
	
	function habilitarCampos(valor){
		
		document.getElementById("folioSAI").disabled = valor;
		document.getElementById("cDescProyecto").disabled = valor;
		document.getElementById("cJustificaSolicitud").disabled = valor;
		document.getElementById("presupuesto").disabled = valor;
		document.getElementById("fInicio").disabled = valor;
		document.getElementById("fFin").disabled = valor;
		
		$("#rdoAbierto").attr('disabled', valor);
		$("#rdoCerrado").attr('disabled', valor);
		
		document.getElementById("cboSolicitud").disabled = valor;
		document.getElementById("cboEspecificacion").disabled = valor;
		document.getElementById("cboTipoMoneda").disabled = valor;
		document.getElementById("mMontoMinimo").disabled = valor;
		document.getElementById("mMontoMaximo").disabled = valor;
		document.getElementById("mTotalContrato").disabled = valor;
		document.getElementById("cFundamentoMotiv").disabled = valor;
		document.getElementById("cEspecif").disabled = valor;
		document.getElementById("cJustifEconomica").disabled = valor;
		document.getElementById("cJustifPlazo").disabled = valor;
		
		if (idoper == 3 || idoper == 2 )
			parent.document.getElementById("pb_save").disabled=false;
	}
	
	function creaDataTableClaves() {
		
		$('#dt_Claves').dataTable(
		{
			"bPaginate" : false,
			"bLengthChange" : false,
			"bFilter" : false,
			"bSort" : true,
			"bInfo" : false,
			"bAutoWidth" : false,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"bServerSide" : true,
			aoColumns : 
			[ 
				{ bVisible : true },
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true },
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true },
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true },
				{ bVisible : false }, 
				{ bVisible : false },
				{ bVisible : true }
			]		
		});
	}
	
	var oTableClaves;
	function cargarClaves() {
		
		var esModificado = $("#cEsModificado").val();
		if (idoper == 1 && esModificado == 1) {
			var conds = " Folio = " + $("#nFolioUltimo").val();
		}else 
			var conds = " Folio = " + $("#FOLIO").val();
		
		if (idoper == 1) {
			oTableClaves = $('#dt_Claves').dataTable(
			{
				"bPaginate" : false,
				"bLengthChange" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=vw_ContratosPlurianualDetalleMod&qw="
						+ conds,
				aoColumns : [ 
								{ sName : "EPCorta" },
								{ sName : "iEjercicioClave", sClass : "centerCls" }, 
								{ sName : "montoTotalClave", sClass : "rightCls" },
								{ sName : "montoEnero", sClass : "rightCls" }, 
								{ sName : "montoFebrero", sClass : "rightCls" }, 
								{ sName : "montoMarzo", sClass : "rightCls" }, 
								{ sName : "montoAbril", sClass : "rightCls" }, 
								{ sName : "montoMayo", sClass : "rightCls" }, 
								{ sName : "montoJunio", sClass : "rightCls" },
								{ sName : "montoJulio", sClass : "rightCls" }, 
								{ sName : "montoAgosto", sClass : "rightCls" }, 
								{ sName : "montoSeptiembre", sClass : "rightCls" },
								{ sName : "montoOctubre", sClass : "rightCls" }, 
								{ sName : "montoNoviembre", sClass : "rightCls" }, 
								{ sName : "montoDiciembre", sClass : "rightCls" },
								{ sName : "EP", bVisible : false }, 
								{ sName : "nDocRenglon", bVisible : false },
								{ sName : "eliminar", bVisible : true }
							],
				oLanguage : es_mx
			});
			} else {
			 
			oTableClaves = $('#dt_Claves').dataTable(
			{
				"bPaginate" : true,
				"bLengthChange" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=vw_ContratosPlurianualDetalleMod&qw="
						+ conds,
				aoColumns : [ 
								{ sName : "EPCorta" },
								{ sName : "iEjercicioClave", sClass : "centerCls" }, 
								{ sName : "montoTotalClave", sClass : "rightCls" },
								{ sName : "montoEnero", sClass : "rightCls" }, 
								{ sName : "montoFebrero", sClass : "rightCls" }, 
								{ sName : "montoMarzo", sClass : "rightCls" }, 
								{ sName : "montoAbril", sClass : "rightCls" }, 
								{ sName : "montoMayo", sClass : "rightCls" }, 
								{ sName : "montoJunio", sClass : "rightCls" },
								{ sName : "montoJulio", sClass : "rightCls" }, 
								{ sName : "montoAgosto", sClass : "rightCls" }, 
								{ sName : "montoSeptiembre", sClass : "rightCls" },
								{ sName : "montoOctubre", sClass : "rightCls" }, 
								{ sName : "montoNoviembre", sClass : "rightCls" }, 
								{ sName : "montoDiciembre", sClass : "rightCls" },
								{ sName : "EP", bVisible : false }, 
								{ sName : "nDocRenglon", bVisible : false },
								{ sName : "eliminar", bVisible : false }
							],
				oLanguage : es_mx
			});
			 
		 }
	}
	
	function cargarClavesAnterior() {
		var conds = " Folio = " + $("#nFolioContratoPlurianual").val();

		if (idoper == 1) {
			oTableClaves = $('#dt_Claves').dataTable(
			{
				"bPaginate" : false,
				"bLengthChange" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=vw_ContratosPlurianualDetalle&qw="
						+ conds,
				aoColumns : [ 
								{ sName : "EPCorta" },
								{ sName : "iEjercicioClave", sClass : "centerCls" }, 
								{ sName : "montoTotalClave", sClass : "rightCls" },
								{ sName : "montoEnero", sClass : "rightCls" }, 
								{ sName : "montoFebrero", sClass : "rightCls" }, 
								{ sName : "montoMarzo", sClass : "rightCls" }, 
								{ sName : "montoAbril", sClass : "rightCls" }, 
								{ sName : "montoMayo", sClass : "rightCls" }, 
								{ sName : "montoJunio", sClass : "rightCls" },
								{ sName : "montoJulio", sClass : "rightCls" }, 
								{ sName : "montoAgosto", sClass : "rightCls" }, 
								{ sName : "montoSeptiembre", sClass : "rightCls" },
								{ sName : "montoOctubre", sClass : "rightCls" }, 
								{ sName : "montoNoviembre", sClass : "rightCls" }, 
								{ sName : "montoDiciembre", sClass : "rightCls" },
								{ sName : "EP", bVisible : false }, 
								{ sName : "nDocRenglon", bVisible : false },
								{ sName : "eliminar", bVisible : true }
							],
				oLanguage : es_mx
			});
			} else {
			 
			oTableClaves = $('#dt_Claves').dataTable(
			{
				"bPaginate" : false,
				"bLengthChange" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=vw_ContratosPlurianualDetalle&qw="
						+ conds,
				aoColumns : [ 
								{ sName : "EPCorta" },
								{ sName : "iEjercicioClave", sClass : "centerCls" }, 
								{ sName : "montoTotalClave", sClass : "rightCls" },
								{ sName : "montoEnero", sClass : "rightCls" }, 
								{ sName : "montoFebrero", sClass : "rightCls" }, 
								{ sName : "montoMarzo", sClass : "rightCls" }, 
								{ sName : "montoAbril", sClass : "rightCls" }, 
								{ sName : "montoMayo", sClass : "rightCls" }, 
								{ sName : "montoJunio", sClass : "rightCls" },
								{ sName : "montoJulio", sClass : "rightCls" }, 
								{ sName : "montoAgosto", sClass : "rightCls" }, 
								{ sName : "montoSeptiembre", sClass : "rightCls" },
								{ sName : "montoOctubre", sClass : "rightCls" }, 
								{ sName : "montoNoviembre", sClass : "rightCls" }, 
								{ sName : "montoDiciembre", sClass : "rightCls" },
								{ sName : "EP", bVisible : false }, 
								{ sName : "nDocRenglon", bVisible : false },
								{ sName : "eliminar", bVisible : false }
							],
				oLanguage : es_mx
			});
			 
		 }
	}
	
	function BuscarClavePresupuestal(){
		$("#EP").val("");
		$("#clavePresupuestal").val("");
		
		var ejerFiscal = "";
		
		ejerFiscal = $("#cboEjerFiscal").val();
		
		if(ejerFiscal == "0"){
			Swal.fire("Capture","Debe seleccionar un Año para continuar.","info");
		}else{
			window.open("AyudaClavesPresupuestal.jsp?ejercicioFiscal="+ejerFiscal, 'AyudaClavesPresupuestal', 'status=1, width=900px, height=530px, left=100px');
		}		
						
	}		
	
	function cargaDatosDetalle( mes1, mes2, mes3, mes4, mes5, mes6, mes7, mes8, mes9, mes10, mes11, mes12, importeTotal){
		$("#mImporteTotal").val("");
		$("#mMes1").val(""); $("#mMes2").val(""); $("#mMes3").val(""); $("#mMes4").val("");
		$("#mMes5").val(""); $("#mMes6").val(""); $("#mMes7").val(""); $("#mMes8").val("");
		$("#mMes9").val(""); $("#mMes10").val(""); $("#mMes11").val(""); $("#mMes12").val("");
		$("#mImporteTotal").val(importeTotal);
		$("#mMes1").val(mes1); $("#mMes2").val(mes2); $("#mMes3").val(mes3); $("#mMes4").val(mes4);
		$("#mMes5").val(mes5); $("#mMes6").val(mes6); $("#mMes7").val(mes7); $("#mMes8").val(mes8);
		$("#mMes9").val(mes9); $("#mMes10").val(mes10); $("#mMes11").val(mes11); $("#mMes12").val(mes12);
		
	}
	
	function validarFechas(){
		var bRegresa = true;	
		var fInicio = $("#fInicio").val();  
		var fFin = $("#fFin").val();
		
		var iAnioInicio = parseInt(fInicio.substr(6,4), 10);
		var iAnioFin = parseInt(fFin.substr(6,4), 10);
		
		if(iAnioFin <= iAnioInicio){
			Swal.fire("Verifique!!","El Año de la Fecha Fin no puede ser menor o igual la Año de la Fecha Inicio. ","info");
			var sFechaFin = "01/01/" + (iAnioInicio+1);
			$("#fFin").val(sFechaFin);
			iAnioFin = parseInt(sFechaFin.substr(6,4), 10);
			cargarEjercicioFiscal(iAnioInicio, iAnioFin);
			cargarEjercicioMontos(iAnioInicio, iAnioFin);			
			bRegresa = false;
		}else{
			cargarEjercicioFiscal(iAnioInicio, iAnioFin);
			cargarEjercicioMontos(iAnioInicio, iAnioFin);
		}
		
		return bRegresa;		
	}
	
	function cargarEjercicioFiscal(inicio, fin){
		var iCont = fin - inicio;
	   	var ejerFiscal = 0;
	   	
	   	removeSelectBox(document.getElementById("cboEjerFiscal"));
	   	
	   	$("#cboEjerFiscal").append("<option value=" + ejerFiscal + ">--Seleccione--</option>");
	   	
	   	ejerFiscal = inicio;
	   	
		for(var i=0; i<=iCont; i++){ 
    		$("#cboEjerFiscal").append("<option value=" + ejerFiscal + ">" + ejerFiscal + "</option>");
    		ejerFiscal++;
 		}
	}
	
	function cargarEjercicioMontos(inicio, fin){
		var iCont = fin - inicio;
	   	var ejerFiscal = 0;
	   	
	   	removeSelectBox(document.getElementById("cboEjercicioMontos"));
	   	
	   	$("#cboEjercicioMontos").append("<option value=" + ejerFiscal + ">--Seleccione--</option>");
	   	
	   	ejerFiscal = inicio;
	   	
		for(var i=0; i<=iCont; i++){ 
    		$("#cboEjercicioMontos").append("<option value=" + ejerFiscal + ">" + ejerFiscal + "</option>");
    		$("#aEjercicios").val(ejerFiscal);
    		$("#renglon").val(i + 1);
    		
    		queryFormPost("modificarNdocRenglon_update", {async:false});
    		ejerFiscal++;
 		}
	}
	
	function removeSelectBox(selectbox)
	{
	    var i;
	    for(i = selectbox.options.length - 1 ; i >= 0 ; i--)
	    {
	        selectbox.remove(i);
	    }
	}
	
	function llenaFechaFin(){		
				
		var fInicio = $("#fInicio").val();  
		var fFin = $("#fFin").val();	
		var iYearInicio = 0;
		var iYearFin = 0;
		
		if(isEmpty("fFin")){
			var iYearFecha = parseInt(fInicio.substr(6,4), 10) + 1;
			var sFechaFin = "01/01/" + iYearFecha;
			$("#fFin").val(sFechaFin);
			fFin = $("#fFin").val();
			
			iYearInicio = parseInt(fInicio.substr(6,4), 10);
			iYearFin = parseInt(fFin.substr(6,4), 10);
			
			$("#anioInicial").val(iYearInicio);
			queryFormPost("consultaJustificacion", {async:false});
			
			cargarEjercicioFiscal(iYearInicio, iYearFin);
			cargarEjercicioMontos(iYearInicio, iYearFin);
	
		}else{
			
			iYearInicio = parseInt(fInicio.substr(6,4), 10);
			iYearFin = parseInt(fFin.substr(6,4), 10);
			
			$("#anioInicial").val(iYearInicio);
			queryFormPost("consultaJustificacion", {async:false});
			
			if(iYearInicio >= iYearFin){
				Swal.fire("Verifique!!","El Año de la Fecha Inicio no puede ser Mayor o Igual al Año de la Fecha Fin.","info");
				var sFechaInicio = "01/01/" + (iYearFin-1);
				$("#fInicio").val(sFechaInicio);
				iYearInicio = parseInt(sFechaInicio.substr(6,4), 10);
				cargarEjercicioFiscal(iYearInicio, iYearFin);
				cargarEjercicioMontos(iYearInicio, iYearFin);
			
			}else{
				cargarEjercicioFiscal(iYearInicio, iYearFin);
				cargarEjercicioMontos(iYearInicio, iYearFin);
			}
		}	
						
	}
	
	var totalContrato = 0;
	
	function validaMontoTotalContrato(){	
		var bRegresa = true;
		
		totalContrato = parseFloat( quitaFmt( $("#mTotalContrato").val()) );
		totalContrato = totalContrato.toFixed(2);
		var mMontoMinimoContrato = Number( quitaFmt($("#mMontoMinimo").val()) );
		mMontoMinimoContrato =  mMontoMinimoContrato.toFixed(2);		
		var porcentaje = 0.00;
		
		porcentaje = (mMontoMinimoContrato / totalContrato );
		porcentaje = porcentaje.toFixed(2);
		
		if($("#rdoAbierto").prop("checked")){
			if(parseFloat(porcentaje) < 0.40){
				Swal.fire("Verifique!!","El Monto minimo del Contrato no puede ser menor al 40% del total del Contrato. ", "info");
				bRegresa = false;			
			}
		}	
		return bRegresa;
	}	
	
	var renglon = 1;
	var montoTotal = 0;
	
	function agregarImporteEjercicio(){
		
		totalContrato = Number( quitaFmt( $("#mTotalContrato").val() ));
		totalContrato = parseFloat(Math.round(totalContrato *100) / 100);
		var montoEjercicio = Number(quitaFmt($("#mMontoEjercicio").val()));
		var montoMax =Number( quitaFmt( $("#mMontoMax").val()));
		var montoMin = Number( quitaFmt($("#mMontoMin").val()));
		
		montoEjercicio = parseFloat(Math.round(montoEjercicio *100) / 100);
		montoMax = parseFloat(Math.round( montoMax  *100) / 100);
		montoMin = parseFloat(Math.round( montoMin  *100) / 100);
		
		if(parseFloat(montoEjercicio) <= 0){
			Swal.fire("Capturar","Es necesario capturar el Importe para continuar.","info");
			return;
		}
				
		$("#aEjercicioMonto").val($("#cboEjercicioMontos").val());
		queryFormPost("consultaEjercicioPluriM", {async:false});
		queryFormPost("consultaMontoTotalPluriMod", {async:false});
		
		if ($("#montoTotal").val() > 0) {
			montoTotal = Number( quitaFmt( $("#montoTotal").val() ));
			montoTotal = parseFloat(Math.round( montoEjercicio  *100) / 100) + parseFloat(Math.round( montoTotal  *100) / 100);
			montoTotal = Number (montoTotal.toFixed(2))
		} 
		
		if ((montoTotal  > totalContrato) || (montoEjercicio > totalContrato)){
			Swal.fire("Verifique!!","El monto del Ejercicio sobregira el monto Total del Contrato.", "info");
			return;
		}
		
		if($("#bExisteEjer").val() > 0){
			Swal.fire("Verifique!!","El Ejercicio seleccionado ya esta capturado.","info");
			return;
		}else{	
				renglon = ++ renglon;
				$("#renglon").val(renglon);
				queryFormPost("tContratosPlurianual_MontosEjercModifCreate", {async:false});
				$("#mMontoEjercicio").val("0");
				$("#mMontoMax").val("0");
				$("#mMontoMin").val("0");
				cargaDetEjerciciosMontos();
				queryFormPost("consultaMontoTotalPluriMod", {async:false});
		}
		
		if(montoTotal.toFixed(2) == parseFloat(totalContrato).toFixed(2)) {
			$("#tabDesglose").click();
		} 		
	}
	
	var oTableEjerMontos;
	function cargaEjerciciosMontosTabla() {
	 	$('#dt_MontosEjercicios').dataTable(
		{
			"bPaginate": false,
   			"bLengthChange": false,
   			"bFilter": false,
   			"bSort": false,
   			"bInfo": false,
   			"bAutoWidth": true,
   			"sScrollX": 100,
			"sScrollY": 100,
			"bJQueryUI": true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers",
			aoColumns : 
			[ 
				{ bVisible : true },
				{ bVisible : true }, 
				{ bVisible : false }, 
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true }
			]
		});		
	}
	
	function cargaDetEjerciciosMontos() {
		var esModificado = $("#cEsModificado").val();
		if (idoper == 1 && esModificado != 0)
			var conds = " Folio = " + $("#nFolioUltimo").val();
		else 
			var conds = " Folio = " + $("#FOLIO").val();
			
	if (idoper == 1 ) {
		oTableEjerMontos = $('#dt_MontosEjercicios').dataTable(
		{
			"bPaginate" : false,
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth": true,
   			"sScrollX": "100%",
			"sScrollY": "100%",
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"bScrollCollapse" : true,
			"bServerSide" : true,
			"processing": true,
			sAjaxSource : window.location.protocol + "//"
					+ window.location.host + "/"
					+ window.location.pathname.split("/")[1]
					+ "/crud?rt=t&ql=vw_ContratosPlurianual_MontosEjerMod&qw="
					+ conds,
			aoColumns : [ 
							{ sName : "aEjercicio", sClass : "centerCls" },
							{ sName : "montoEjercicio", sClass : "leftCls" }, 
							{ sName : "nDocRenglon", bVisible : false },
							{ sName : "mMontoMin", sClass : "leftCls" }, 
							{ sName : "mMontoMax", sClass : "leftCls" },
							{ sName : "eliminar",sClass : "leftCls" }  
						],
			oLanguage : es_mx
		})		
	
	} else if (idoper > 1 ) {
			oTableEjerMontos = $('#dt_MontosEjercicios').dataTable(
		{
			"bPaginate" : false,
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth": true,
   			"sScrollX": "100%",
			"sScrollY": "100%",
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"bScrollCollapse" : true,
			"bServerSide" : true,
			"processing": true,
			sAjaxSource : window.location.protocol + "//"
					+ window.location.host + "/"
					+ window.location.pathname.split("/")[1]
					+ "/crud?rt=t&ql=vw_ContratosPlurianual_MontosEjerMod&qw="
					+ conds,
			aoColumns : [ 
							{ sName : "aEjercicio", sClass : "centerCls" },
							{ sName : "montoEjercicio", sClass : "leftCls" }, 
							{ sName : "nDocRenglon", bVisible : false },
							{ sName : "mMontoMin", sClass : "leftCls" }, 
							{ sName : "mMontoMax", sClass : "leftCls" },
							{ sName : "eliminar",sClass : "leftCls", bVisible : false  }  
						],
			oLanguage : es_mx
		})		
			
		}
			
	}
	
	
	function cargaDetEjerciciosMontosAnt() {

		var conds = " Folio = " + $("#nFolioContratoPlurianual").val();
		oTableEjerMontos = $('#dt_MontosEjercicios').dataTable(
		{
			"bPaginate" : false,
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth": true,
   			"sScrollX": "100%",
			"sScrollY": "100%",
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"bScrollCollapse" : true,
			"bServerSide" : true,
			"processing": true,
			sAjaxSource : window.location.protocol + "//"
					+ window.location.host + "/"
					+ window.location.pathname.split("/")[1]
					+ "/crud?rt=t&ql=vw_ContratosPlurianual_MontosEjercicios&qw="
					+ conds,
			aoColumns : [ 
							{ sName : "aEjercicio", sClass : "centerCls" },
							{ sName : "montoEjercicio", sClass : "leftCls" }, 
							{ sName : "nDocRenglon", bVisible : false },
							{ sName : "mMontoMin", sClass : "leftCls" }, 
							{ sName : "mMontoMax", sClass : "leftCls" },
							{ sName : "eliminar",sClass : "leftCls" }  
						],
			oLanguage : es_mx
		})		
	}
	
	function updateFirmantes(){
		queryFormPost({
			queryName:"existeFirmanteSolContratoPluMod_Read", 
			async : false, 
			callback:function(){
				if (idoper == 1 && $("#existeFirmanteSol").val()=="SIEXISTE" ){
					parent.document.getElementById("pb_save").disabled=true;
					parent.document.getElementById("pb_send").disabled=false;
				}else
					$("#dlgFirmanteSol").dialog("open");
			}
		});
	}
	
	function creaDlgFirmanteSolicita() { 
      	$("#dlgFirmanteSol").dialog({
	        title:"Datos de Firmante Solicita.",
	        autoOpen : false,
	        height : 420,
	        width : 500,
	        modal : true,
	        buttons : {
            	"Aceptar" : function() {
                    					
					if($.trim($("#cNombreSol").val()) == ""){ Swal.fire("Capture","Falta Ingresar Nombre en Datos Solicita","warning"); return; } 
					else if($.trim($("#cApPaternoSol").val()) == ""){ Swal.fire("Capture","Falta Ingresar Apellido Paterno en Datos Solicita","warning"); return; }
					else if($.trim($("#cApMaternoSol").val()) == ""){ Swal.fire("Capture","Falta Ingresar Apellido Materno en Datos Solicita","warning"); return; }
					else if($.trim($("#cPuestoSol").val()) == ""){ Swal.fire("Capture","Falta Ingresar Puesto en Datos Solicita","warning"); return; }	
										
					$("#cNombreS").val($("#cNombreSol").val());
					$("#cPaternoS").val($("#cApPaternoSol").val());
					$("#cMaternoS").val($("#cApMaternoSol").val());
					$("#cPuestoS").val($("#cPuestoSol").val());					
					$("#firmanteSol").val($("#cNombreS").val()+" "+$("#cPaternoS").val()+" "+$("#cMaternoS").val());
					
					try{						
						queryFormPost("firmanteSolicitaContratoPlurMod_Update", {async: false });
						
						$("#cNombreSol").val("");
						$("#cApPaternoSol").val("");
						$("#cApMaternoSol").val("");
						$("#cPuestoSol").val("");													
						
						validaImpresion();
						exportarSolicitudExcel();
						
						Swal.fire("Ok!","Solicitud de Contrato Plurianual generada correctamente.","success");
						parent.document.getElementById("pb_send").disabled=false;
						
					}catch(e){
						if(parent.document.getElementById("pb_save"))
							parent.document.getElementById("pb_save").disabled=false;
						Swal.fire("Verifique!","No se pudo actualizar los firmantes, intente mas tarde.","info");
					}
					$(this).dialog("close");  
             	},
              	"Cancelar" : function() {
                	$(this).dialog("close");
                }
         	}
       });   
 
	}
	
	function exportarSolicitudExcel() {
			//Se imprime el formato en Excel consultaFolioAnterior
			var cFolio = $("#FOLIO").val();
			queryFormPost("esContratoModificado", {async:false});
			queryFormPost("consultaFolioAnterior", {async:false});
			
			$.blockUI();
			try {
				//2 es modificado
				createInput( 'rptExcelForm', 'generaExcel', "2" );
				createInput( 'rptExcelForm', 'FOLIO', cFolio );
				createInput( 'rptExcelForm', 'cEsModificado', $("#cEsModificado").val()  );
				createInput( 'rptExcelForm', 'nFolioContratoPlurianual', $("#nFolioContratoPlurianual").val()  );
				createInput( 'rptExcelForm', 'folioAnterior', $("#folioSAI").val()  );
				
				$( "#rptExcelForm" ).submit();
				$.unblockUI();
				
			} catch( e ) {
				$.unblockUI();
				alert( e );
			}
		}
		
		function borraElementos() {
			$( '.remove' ).remove();
		}
		
		function createInput( form, name, value ) {
			$( '<input>' ).attr( {
			type : 'hidden',
			name : name,
			value : value
			} ).addClass( 'remove' ).appendTo( '#' + form );
		}
		
		function fnGetSelected( oTableLocal ) {
		
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();
			
			for ( var i=0 ; i<aTrs.length ; i++ )
			{				
				aReturn.push( aTrs[i] );
				editRow ( oTableLocal, aReturn );
				aReturn.shift();				
			}
			return aReturn;
		}
	
		
		function validarCapturaImportesEjer(){
			var bRegresa = true;
						
			var aniosClave = $("#cAniosCapturados").val();  
			var aniosEjercicio = $("#cNumEjercicio").val();
			
			if(aniosClave < aniosEjercicio){
				Swal.fire("Verifique!!","No se han capturado todos los Importes por Ejercicio.", "info");
				bRegresa = false;
			}			
			return bRegresa;		
		}
		
		function eliminaRegistro(row) {
			Swal.fire({
				  title: 'Desea continuar?',
				  text: "Se eliminará el importe del Ejercicio.",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					  $("#renglon").val(row);
			    		//Consulta año de los ejercicios a borrar
			    		queryFormPost("consultaAnioPluriMod_read", {async:false});
			    		
			    		//Borra el ejercicio
			    		queryFormPost("delete_contatoPluri_ejercicioMod", {async:false});
			    	    
			    	    //Elimina las claves de ese año que se borro
			    	    queryFormPost("delete_contatoPluri_detAnioMod", {async:false});
			    	    
			    	     //Consulta el importe que quedo
			    	    queryFormPost("consultaMontoTotalPluriMod", {async:false});
			    	    
			    	    //Carga la tabla de nuevo
			    	    cargaDetEjerciciosMontos();
			    	    cargarClaves();
			    	    
			    	     //Deshabilita el guardar
			    	    parent.document.getElementById("pb_save").disabled=true;
			    	    parent.document.getElementById("pb_send").disabled=true;  
			    	} 
				})
			    
		}
			
    
	var folio = 0;
	function eliminaRegistroCve(row) {
		Swal.fire({
			  title: 'Desea continuar?',
			  text: "Se eliminará el registro seleccionado",
			  icon: 'warning',
			  showCancelButton: true,
			  confirmButtonColor: '#288BA8',
			  cancelButtonColor: '#e6e6e6',
			  confirmButtonText: 'Aceptar',
			  cancelButtonText: 'Cancelar'
			}).then((result) => {
			  if (result.isConfirmed) {
					//Borra el registro
					$("#renglon").val(row);
					 folio = $("#FOLIO").val();
		    		queryFormPost("delete_contatoPluri_detalleMod", {async:false});
		    		
		    		//vuelve a recargar las tablas
		    	    cargarClaves();
		    	    
		    	    //modifica los acumulados
					$("#FOLIO").val(folio);
					queryFormPost("consultaMontoAcumuladoMod", {asyn:false});
		    		
		    		parent.document.getElementById("pb_save").disabled=true;	
			  } 
			})
		}
		
	
	function validaImpresion() {
		queryFormPost("esContratoModificado", {async:false});
	
		window.open("../plurianuales/SolicitudPlurianual?folio=" + $("#FOLIO").val() + "&cEsModificado=" +  $("#cEsModificado").val() + "&nFolioContratoPlurianual=" +  $("#nFolioContratoPlurianual").val() + "&esOriginal=0", "_blank", "toolbar=no,scrollbars=no,resizable=yes,top=800,left=800,width=250,height=250");
		
	}	
	
</script>

</head>

<body id="dt_example" bgColor="red" >
	<form id="rptExcelForm" method="post" target="_blank" action="../plurianuales/SolicitudPlurianual">
	</form>
	<form id="frmContratosPlurianuales" name="frmContratosPlurianuales">
		<div id="container" class="container" style="width: 90%;" >
			
			<input type="hidden" name="FOLIO" id="FOLIO" value="<%=folio%>" />
			<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0) == null ? " caso operacion nulo " : c.getCasoOperacion(0).getResponsable()%>" />
			<input type="hidden" name="FECHA_DOCUMENTO" id="FECHA_DOCUMENTO" value="<%=today%>" />
			<input type="hidden" name="EJERCICIO_FISCAL" id="EJERCICIO_FISCAL" value="<%=aEjercicioFiscal%>" />
			<input name="cDocumento" type="hidden" id="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>">
			
			<input type="hidden" name="aEjercicioFiscal" id="aEjercicioFiscal" value="<%=aEjercicioFiscal%>" />
			<input type="hidden" name="cRamo" id="cRamo" value="<%=cRamo%>" />
			<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value="<%=cUR%>" />
			<input type="hidden" name="cCentroContable" id="cCentroContable" value="<%=cCentroContable%>" />
			<input type="hidden" name="fCaptura" id="fCaptura" value="<%=today%>" />
			<input type="hidden" name="u_login" id="u_login" value="<%=U_LOGIN%>" />
			<input type="hidden" name="importeAcumulado" id="importeAcumulado" value="" />
			<input type="hidden" name="existeFolio" id="existeFolio" value="" />
			<input type="hidden" name="aEjercicioMonto" id="aEjercicioMonto" value="" />
			<input type="hidden" name="aEjercicios" id="aEjercicios" value="" />
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
			
			<input type="hidden" name="cFundamentoMotivacion" id="cFundamentoMotivacion" value="" />
			<input type="hidden" name="cEspecificacion" id="cEspecificacion" value="" />
			<input type="hidden" name="cJustificacionEconomica" id="cJustificacionEconomica" value="" />
			<input type="hidden" name="cJustificacionPlazo" id="cJustificacionPlazo" value="" />
			<input type="hidden" name="cMotivoRechazo" id="cMotivoRechazo" value="" />
			<input type="hidden" name="cDocumentoHaplicado" id="cDocumentoHaplicado" value="" />
			<input type="hidden" id="firmanteExiste" name="firmanteExiste" value="">
			
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
			
			<input type="hidden" name="mTotalAnio" id="mTotalAnio" value="0" />	
			<input type="hidden" name="mTotalEP" id="mTotalEP" value="0" />
			<input type="hidden" name="aEjercicioBorrar" id="aEjercicioBorrar" value="" />	
			<input type="hidden" name="nFolioUltimo" id="nFolioUltimo" value="0" />
			
			<input type="hidden" name="mImporteEjercicio" id="mImporteEjercicio" value="" />
			<input type="hidden" name="cDescripcionProyecto" id="cDescripcionProyecto" value="" />
			<input type="hidden" name="nNumEjercicios" id="nNumEjercicios" value="" />	
			<input type="hidden" name="cJustifSolicitud" id="cJustifSolicitud" value="" />
			
			<input type="hidden" name="cEsModificado" id="cEsModificado" value="" />
			<input type="hidden" name="folioUlitmo" id="folioUlitmo" value="" />
			<input type="hidden" name="folioAnterior" id="folioAnterior" value="" />			
			
			<input type="hidden" id="cNombreS" name="cNombreS"  />
			<input type="hidden" id="cPaternoS" name="cPaternoS"  />
			<input type="hidden" id="cMaternoS" name="cMaternoS"  />
			<input type="hidden" id="cPuestoS" name="cPuestoS"  />
			<input type="hidden" id="firmanteSol" name="firmanteSol"  />
			<input type="hidden" id="existeFirmanteSol" name="existeFirmanteSol" value="NOEXISTE" />		
			
			<h1>Modificación de Contratos Plurianuales</h1>
			
			<fieldset id="fieldsetVal" style="display: none;">
				<legend id="validacion">Validaci&oacuten</legend>
				<table align="left" width="35%">
					<tr>
						<td align="right"> Validar </td>
						<td align="left">
							<input type="radio" id="rdoValida" name="rdoValidar" value="Validar" /> </td>
						<td align="right"> Rechazar </td>
						<td align="left">
							<input type="radio" id="rdoRechazar" name="rdoValidar" value="Rechazar" onclick="capturaRechazo()"/> </td>
					</tr>
				</table>
			</fieldset>
			
			<fieldset id="fieldsetAut" style="display: none;">
				<legend id="validacion">Autorizaci&aacuten</legend>
				<table align="left" width="35%">
					<tr>
						<td align="right"> Autorizar </td>
						<td align="left">
							<input type="radio" id="rdoAutoriza" name="rdoAutorizar" value="Autorizar" checked> </td>
						<td align="right"> Rechazar </td>
						<td align="left">
							<input type="radio" id="rdoCorrige" name="rdoAutorizar" value="Corregir" onclick="capturaRechazo()"/> </td>
					</tr>
				</table>
			</fieldset>
			<span id="EditaFirmas" style="visibility:hidden"><a href="#" >Firmas*</a></span>
			
			<fieldset>
				<legend>Datos del Proyecto</legend>
				<table>
					<tr>
						<td align="right">Folio SAI:</td>
						<td align="left">
							<input type="text" class="form-control"  id="folioSAI" name="folioSAI" size=20 class="notEditable" value="<%=folio%>" />
						</td>
						<td nowrap> Solicitud Contrato Anterior
							<input type="text" name="nFolioContratoPlurianual" id="nFolioContratoPlurianual"  class="AyudaSyC" size="15" maxlength="15" onChange ="cargaInfoAnterior();" readonly title="Selecciona un contrato" />
							
						</td>												
					</tr>
				</table>
				<table>
					<tr>
						<td align="right">*Nombre del Proyecto:</td>																	
					</tr>
				</table>
				<table>
					<tr>
						<td align="right">
							<textarea class="form-control"  cols=110 rows=5 id="cDescProyecto" name="cDescProyecto" style="width: 1024px; "></textarea>
						</td>
					</tr>					
				</table>
				<table>
					<tr>
						<td align="right">*Justificaci&oacuten de la Solicitud:</td>																	
					</tr>
				</table>
				<table>
					<tr>
						<td align="right">
							<textarea class="form-control" cols=110 rows=10 id="cJustificaSolicitud" name="cJustificaSolicitud" style="height: 146px; width: 1024px"></textarea>
						</td>
					</tr>	
					<tr>
						<td align="right">
							<textarea class="form-control" cols=110 rows=5 id="cJustificacion" name="cJustificacion" readonly style="height: 136px; width: 1024px"></textarea>
						</td>
					</tr>					
				</table>
			</fieldset>
			<fieldset id="fieldFolios" style="display: none;">
			<table>
					<tr>						
						<td align="right" >*Folio MASCP:</td>
						<td align="left">
							<input type="text" class="form-control" id="folioMASCP" name="folioMASCP" size=20 />
						</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td align="right"id="nOficio">Oficio DG:</td>
						<td align="left">
							<input type="text" class="form-control" id="oficioDG" name="oficioDG" size=20 />
						</td>
						
					</tr>
					
				</table>
			</fieldset>
			<fieldset>
				<legend>Captura</legend>
				<table>
					
					<tr>
						<td align="right">*Fecha Inicio:</td>
						<td align="left">
							<input type="text" id="fInicio" name="fInicio" size=20 />
						</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td align="right">*Fecha Fin:</td>
						<td align="left">
							<input type="text" id="fFin" name="fFin" size=20 />
						</td>
						<td align="right">Presupuesto:</td>
						<td align="left">
							<input type="text" class="form-control" id="presupuesto" name="presupuesto" size=20 class="notEditable"/>
						</td>
					</tr>
					<tr>
						<td align="right">*Tipo Contrato:</td>
						<td align="left">
							<input type="radio" class="form-check-input"  id="rdoAbierto" name="tipoContrato" value = "Abierto" checked="checked"/>
							<label class="form-check-label" for="rdoAbierto">Abierto</label>												
							<input type="radio" class="form-check-input"  id="rdoCerrado" name="tipoContrato" value = "Cerrado" />
							<label class="form-check-label" for="rdoCerrado">Cerrado</label>								
						</td>						
					</tr>
					<tr>
						<td align="right">*Solicitud:</td>
						<td align="left">
							<select class="form-select form-select-sm" name="cboSolicitud" id="cboSolicitud" style="width: 200px" >								
								<option value = "1" >1 .- PLURIANUAL</option>								
							</select>
						</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td align="right">*Especificaci&oacuten:</td>
						<td align="left">
							<select class="form-select form-select-sm" name="cboEspecificacion" id="cboEspecificacion" style="width: 200px" >
								<option value = "0" >--Seleccione--</option>
								<option value = "1" >1 .- ADQUISICION</option>
								<option value = "2" >2 .- SERVICIOS</option>
								<option value = "3" >3 .- OBRAS</option>
								<option value = "4" >4 .- ARRENDAMIENTOS</option>
							</select>
						</td>
					</tr>
					<tr>
						<td align="right">*Tipo de Moneda:</td>
						<td align="left">
							<select class="form-select form-select-sm" name="cboTipoMoneda" id="cboTipoMoneda" style="width: 200px" >
								<option value = "1" >1 .- Pesos Mexicanos</option>
							</select>
						</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						
					</tr>
					<tr>
						<td align="right"></td>
						<td align="left">
							<input type="button" id="btnImprimirRpt" name="btnImprimirRpt" value="Imprimir Solicitud" onclick="validaImpresion();" class="btnInterfaceBG"/>
						</td>
						
						<td align="left">
							<input type="button" id="btnExportarSol" name="btnExportarSol" value="Exportar Solicitud" onclick="exportarSolicitudExcel();" class="btnInterfaceBG"/>
						</td>
						<td></td><td></td><td></td>
						<td align="left">
							<input type="button" id="btnFirmantes" name="btnFirmantes" value="Actualizar Firmantes" onclick="updateFirmantes();" class="btnInterfaceBG"/>
						</td>
					</tr>					
				</table>
			</fieldset>
					
			<fieldset>
				<legend>Importes</legend>
				<table>
					<tr>
						<td align="right"><label id="lblmMinimo">*Monto M&iacutenimo del Contrato:</label></td>
						<td align="left">
							<input type="text" class="form-control" id="mMontoMinimo" name="mMontoMinimo" size="20"  style="text-align:right" value="0" onfocus="Sinfrmt(this)"
														onblur="cambiafrmt(this);"/>
						</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td align="right"><label id="lblmMaximo">*Monto M&aacuteximo del Contrato:</label></td>
						<td align="left">
							<input type="text" class="form-control" id="mMontoMaximo" name="mMontoMaximo" size="20" style="text-align:right"  value="0" onfocus="Sinfrmt(this)"
														onblur="cambiafrmt(this);" />
						</td>
					</tr>					
					<tr>
						<td align="right">*Monto total por Contrato:</td>
						<td align="left">
							<input type="text" class="form-control" id="mTotalContrato" name="mTotalContrato" size="20" value="0"  style="text-align:right" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" />
						</td>						
					</tr>	
				</table>
			</fieldset>		
			
			
		
			
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
					<fieldset>
						<legend>Capture Fundamento y Motivaci&oacuten</legend>
						<table>
							<tr>
								<td align="right">
									<textarea class="form-control" cols=150 rows=10 id="cFundamentoMotiv" name="cFundamentoMotiv" readonly></textarea>
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
				<div id="tabs-2">
					<fieldset>
						<legend>Capture Especificaci&oacuten</legend>
						<table>
							<tr>
								<td align="left"><b>a) La especificaci&oacuten de las obras, adquisiciones, arrendamientos o servicios, señalando si corresponden a inversi&oacuten o gasto corriente.</b></td>
							</tr>
						</table>
						<table>
							<tr>
								<td align="right">
									<textarea class="form-control" cols=150 rows=10 id="cEspecif" name="cEspecif"></textarea>
								</td>
							</tr>
						</table>
					</fieldset>					
				</div>
				<div id="tabs-3">
					<fieldset>
						<legend>Capture Justificaci&oacuten Ventajas Economicas</legend>
						<table>
							<tr>
								<td align="left"><b>b) La justificaci&oacuten de que la celebraci&oacuten de dichos compromisos representa ventajas econ&oacutemicas o que sus t&eacuterminos y condiciones son m&aacutes favorables respecto a la celebraci&oacuten de dichos contratos por un solo ejercicio fiscal.</b></td>
							</tr>
						</table>
						<table>
							<tr>
								<td align="right">
									<textarea class="form-control" cols=150 rows=10 id="cJustifEconomica" name="cJustifEconomica"></textarea>
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
				<div id="tabs-4">
					<fieldset>
						<legend>Capture Justificaci&oacuten del Plazo</legend>
						<table>
							<tr>
								<td align="left"><b>c)	La justificaci&oacuten del plazo de la contrataci&oacuten y de que el mismo no afectar&aacute negativamente la competencia econ&oacutemica del sector de que se trate.</b></td>
							</tr>
						</table>
						<table>
							<tr>
								<td align="right">
									<textarea class="form-control" cols=150 rows=10 id="cJustifPlazo" name="cJustifPlazo"></textarea>
								</td>
							</tr>
							<tr>
								<td colspan = 2 align="left">
									<input type="button" id="btnGuardar" name="btnGuardar" value="Guardar" onclick="cmdAvanzar()" class="btnInterfaceBG"
								 	style="FONT-SIZE: 11pt;"/>
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
				
				<div id = "tabs-Importe">
					<fieldset>
						<legend>Importes por Ejercicio</legend>
					</fieldset>
					<fieldset id="fieldImporte">
						<table>
							<tr>
								<td align="right">Año:</td>
								<td align="left"> 
									<select class="form-select form-select-sm" name="cboEjercicioMontos" id="cboEjercicioMontos" style="width: 125px"> 
										<option value = "0" >--Seleccione--</option>								
									</select>
								</td>
							</tr>
							<tr>
									<td align="right">Importe Ejercicio:</td>
									<td align="left">
										<input type="text" class="form-control" maxlength="30" id="mMontoEjercicio" name="mMontoEjercicio" style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" />
									</td>						
									<td align="left" colspan = 2>
										<input type="button" name="btnMontoEjercicio" id="btnMontoEjercicio" onclick="agregarImporteEjercicio()"  value="Agrega Importe Ejercicio" class="btnInterfaceBG">
									</td>
							</tr>
							<tr>
									<td align="right"><label id="lblmMin" >Monto M&iacutenimo del Ejercicio:</label></td>
									<td align="left">
										<input type="text" class="form-control" id="mMontoMin" name="mMontoMin" size="20" style="text-align:right" value="0" onfocus="Sinfrmt(this)"
																	onblur="cambiafrmt(this);"/>
									</td>
									<td align="right"><label id="lblmMax">Monto M&aacuteximo del Ejercicio:</label></td>
									<td align="left">
										<input type="text" class="form-control" id="mMontoMax" name="mMontoMax" size="20" style="text-align:right" value="0" onfocus="Sinfrmt(this)"
																	onblur="cambiafrmt(this);"/>
									</td>
							</tr>							
						</table>
						</fieldset>
						<fieldset>
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
					</fieldset>
					<table>
						<tr>
							<td>  Importe total del contrato :   </td>
							<td><input type = "text" class="form-control" name = "montoTotal" id = "montoTotal" style="text-align:right" value="0" /></td>
						</tr>
					</table>
				</div>
				<div id="tabs-Desglose" >
					<fieldset>
						<legend>Clave Presupuestal</legend>
						<table>
							<tr>
								<td align="left"><b>d)	El desglose del gasto que debe consignarse a precios del año, tanto para el ejercicio fiscal como para los subsecuentes, a&iacute como, en el caso de obra p&uacuteblica, los avances f&iacutesicos esperados. Los montos deber&aacuten presentarse en moneda nacional y, en su caso, en la moneda prevista para su contrataci&oacuten. Las dependencias y entidades deber&aacuten presupuestar el gasto para los ejercicios subsecuentes conforme al inciso d) anterior.</b></td>
							</tr>
						</table>
						</fieldset>
						<fieldset id="fieldsClave">
						<table>
							<tr>
								<td align="right">Año:</td>
								<td align="left"> 
									<select class="form-select form-select-sm" name="cboEjerFiscal" id="cboEjerFiscal" style="width: 125px" > 
										<option value = "0" >--Seleccione--</option>								
									</select>
								</td>
							</tr>
							<tr>
								<td align="right">Clave:</td>
								<td align="left">
									<input type="text" class="form-control" maxlength="60" size="45" id="clavePresupuestal" name="clavePresupuestal" >
								</td>
								<td align="left">
									<input name="btnBuscarClave" type="button" id="btnBuscarClave" onclick="BuscarClavePresupuestal(); "  value="..." size="5" class="btnInterfaceBG">
								</td>
								<td align="left">
									<input name="btnAgregar" type="button" id="btnAgregar" onclick="agregarImportes()"  value="Capturar Importes" class="btnInterfaceBG">
								</td>
							</tr>							
						</table>
					</fieldset>
					<fieldset>
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
					</fieldset>
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
								<textarea class="form-control" cols=50 rows=6 id="motivoRechazo" name="motivoRechazo"></textarea>
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
			
			<div id="capturaImportes">
				<fieldset>
					<legend>Capture Importes Mes</legend>
					<table>
						<tr>
							<td  align="right">Clave Presupuestal:</td>
							<td  colspan = 3> 
								<input type="text" class="form-control" id="clavePresup" name="clavePresup" size=45 class="notEditable"/>
							</td>							
						</tr>
						<tr>
							<td align="right">Ejercicio Fiscal:</td>
							<td> 
								<input type="text" class="form-control" id="ejercicioFiscal" name="ejercicioFiscal" size=15 value=""/>
							</td>							
						</tr>
						<tr>
							<td align="right">Importe Ejercicio:</td>
							<td> 
								<input type="text" class="form-control" id="importeEjerEP" name="importeEjerEP" size=15 style="text-align:right" value="0" onblur="cambiafrmt(this);"/>
							</td>	
							<td align="right">Importe Capturado:</td>
							<td> 
								<input type="text" class="form-control" id="importeCapturado" name="importeCapturado" size=15 style="text-align:right" value="0" onblur="cambiafrmt(this);" />
							</td>						
						</tr>						
						<tr>
							<td align="right">Enero:</td>
							<td> 
								<input type="text" class="form-control" id="enero" name="enero" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"  />
							</td>	
							<td align="right">Julio:</td>
							<td> 
								<input type="text" class="form-control" id="julio" name="julio" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"/>
							</td>						
						</tr>
						<tr>
							<td align="right">Febrero:</td>
							<td> 
								<input type="text" class="form-control" id="febrero" name="febrero" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);" />
							</td>
							<td align="right">Agosto:</td>
							<td> 
								<input type="text" class="form-control" id="agosto" name="agosto" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"/>
							</td>								
						</tr>
						<tr>
							<td align="right">Marzo:</td>
							<td> 
								<input type="text" class="form-control" id="marzo" name="marzo" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);" />
							</td>	
							<td align="right">Septiembre:</td>
							<td> 
								<input type="text" class="form-control" id="septiembre" name="septiembre" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"/>
							</td>							
						</tr>
						<tr>
							<td align="right">Abril:</td>
							<td> 
								<input type="text" class="form-control" id="abril" name="abril" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);"  onchange="sumaMeses(this);"/>
							</td>	
							<td align="right">Octubre:</td>
							<td> 
								<input type="text" class="form-control" id="octubre" name="octubre" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"/>
							</td>						
						</tr>
						<tr>
							<td align="right">Mayo:</td>
							<td> 
								<input type="text" class="form-control" id="mayo" name="mayo" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);" />
							</td>
							<td align="right">Noviembre:</td>
							<td> 
								<input type="text" class="form-control" id="noviembre" name="noviembre" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"/>
							</td>							
						</tr>
						<tr>
							<td align="right">Junio:</td>
							<td> 
								<input type="text" class="form-control" id="junio" name="junio" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);" />
							</td>
							<td align="right">Diciembre:</td>
							<td> 
								<input type="text" class="form-control" id="diciembre" name="diciembre" size=15 style="text-align:right" value="0" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);" onchange="sumaMeses(this);"/>
							</td>							
						</tr>
					</table>
				</fieldset>
			</div>
			
			<!-- Dialogo Firmantes -->
			<jsp:include page="Firmantes.jsp"></jsp:include>
			<!-- Fin Dialogo Firmantes -->
			
			
			<div id="dlgFirmanteSol" title="Captura de Firmante Solicita.">			 	
				<fieldset>
					<legend>Datos Solicita</legend>
					<table>
						<tr>
							<td>Nombre: </td>
							<td><input type="text" class="form-control" id="cNombreSol" name="cNombreSol" size=40></td>
						</tr>
						<tr>
							<td>Apellido Paterno: </td>
							<td><input type="text" class="form-control" id="cApPaternoSol" name="cApPaternoSol" size=40></td>
						</tr>
						<tr>
							<td>Apellido Materno: </td>
							<td><input type="text" class="form-control" id="cApMaternoSol" name="cApMaternoSol" size=40></td>
						</tr>
						<tr>
							<td>Puesto: </td>
							<td><input type="text" class="form-control" id="cPuestoSol" name="cPuestoSol" size=40></td>
						</tr>
					</table>
				</fieldset>			
			</div>
		
			
		</div>
	</form>
</body>

</html>
