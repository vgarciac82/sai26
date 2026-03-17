<%@page import="com.syc.gestion.servlet.GestionServlet"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@page import="com.syc.obrapublica.ObraPublicaBusinessLogic"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.syc.gestion.core.Empleado"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.core.EmpleadoArea"%>
<%@page import="com.syc.obrapublica.ObraPublicaContractBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
//IRD 20131121	RO-0009 todo lo de pago de pasivo
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	//IRD 28/10/2013 RO-0002
	boolean OP_ACTUALIZACION_DATOS = "true".equals( (String) session.getAttribute("OP_ACTUALIZACION_DATOS") );
	boolean OP_CAPTURA_ESTIMACION = "true".equals( (String) session.getAttribute("OP_CAPTURA_ESTIMACION") );
	
	String tipoUsuario = "";
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("ADMIN_ADMIN_OBRAPUBLICA")) {
    	if ("SI".equals(usuario.getPropiedad("ADMIN_ADMIN_OBRAPUBLICA").getValor())) {
    		tipoUsuario = "ADMIN";
    	}
  	}
	int id_oper = c.getCasoOperacion(0).getIdOperacion();
	String titulo_aplicacion = c.getTipoCaso().getGavetaAsociada();
	int id_gabinete = c.getIdGabinete();

	String DATE_FORMAT = "dd/MM/yyyy";
	
	//mlr
	int idCaso = c.getIdCaso();
	
	String cCentroContable = "";
	String uUR = usuario.getU_UR();
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar calendarToday = Calendar.getInstance(); // today
	String today = sdf.format(calendarToday.getTime());

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(
			GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
			.getValor();
	String uLogin = usuario.getLogin();
	String Control[] = {"EjercicioFiscal", "RamoEP",
			"UnidadResponsableEP", "GrupoFuncional", "Funcion",
			"SubFuncion", "ProgramaGeneral", "ActividadInstitucional",
			"ProgramaPresupuestario", "Partida", "TipoGasto",
			"FuenteFinanciamiento", "EntidadFederativa", "Cartera",
			"UnidadNormativa", "cUnidadEjecutora"};

	String applyDocu = "No";
	String cancelContr = "No";
	String typeDocument;
	ObraPublicaContractBusinessLogic opbl = new ObraPublicaContractBusinessLogic(
			GestionInterface.ATT_CONEXION);

	// Pregunta por la variable de aplicar movimiento contable
	if (request.getParameter("applyDocument") != null
			&& request.getParameter("applyDocument").equals("Si")) {
		applyDocu = "Si";
	}

	// Pregunta por la variable de cancelar documento
	if (request.getParameter("cancelDocument") != null
			&& request.getParameter("cancelDocument").equals("Si")) {
		cancelContr = "Si";
	}
	String mensajeMotor = "";
	// Se verifica si hay que realizar una cancelación
	if (request.getParameter("cancelContract") != null
			&& request.getParameter("cancelContract").equals("Si")) {
		cancelContr = "Si";
	}

	if (cancelContr.equals("Si")) {
		typeDocument = (String) request.getParameter("typeDocument");
		if (typeDocument.equals("1") || typeDocument.equals("2")
				|| typeDocument.equals("3") || typeDocument.equals("4") || typeDocument.equals("5") || typeDocument.equals("6")) // Apartado
		{
			String[] r = opbl.CancelContract(c.getFolio(), typeDocument, false);
			
			
			opbl = null;
			
			out.println("<html>");
			out.println("<head>");
			out.println("<script type=\"text/javascript\">");
			out.println("function onLoad(){");
			out.println("parent.terminaAppContCancel(" + r[0] + ")");
			out.println("}");
			out.println("</script>");
			out.println("</head>");
			out.println("<body onload=\"onLoad()\">");
			out.println("</body>");
			out.println("</html>");
			out.flush();
			
			return;

		}
	}
	ObraPublicaBusinessLogic obpl = new ObraPublicaBusinessLogic();
	String statusContrato = obpl.getStatusContrato(c.getFolio());
	
	boolean esConvenioModificatorio = (4 == id_oper);
	boolean esPagoPasivo = (6 == id_oper);
	boolean esPlurianual = (8 == id_oper);
	boolean esCapturaEstimacion = "20".equals(statusContrato);
	boolean esConsulta = (3 == id_oper);
	
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionServlet.ATT_CONEXION);
	boolean esSAIFonden = "true".equalsIgnoreCase( cabl.getSystemSetting("SAI_FONDEN") );
	boolean esSAIAlterno = "true".equals(cabl.getSystemSetting("SAI_AMBIENTAL")) || esSAIFonden;
	String tituloOLI = "Se seleccionará del combo el Oficio de Liberación de Inversiones autorizado que le corresponda al Contrato y la Cartera de Proyecto seleccionada con anterioridad.";
	if( esSAIFonden ){
		tituloOLI = "Se seleccionará del combo el No. de Acuerdo autorizado que le corresponda al Contrato";
	}
	
%>

<!DOCTYPE html>
<html>
	<head>
		<title>Obra P&uacute;blica</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
		<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
		<link rel="stylesheet" type="text/css" href="../css/interfaz.css"/>
				
				<style type="text/css" title="currentStyle">
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		
		@import "css/demo_table_jui.css";
		
		@import "css/demo_page.css";
		</style>
		<script src="https://code.jquery.com/jquery-3.5.0.js"></script>
		
		<link rel="stylesheet" type="text/css" href="../Bootstrap/Bootstrap502/css/bootstrap.css"/>
		<link rel="stylesheet" type="text/css" href="../Bootstrap/Bootstrap-dataTables/datatables.css"/>
	
		<script type="text/javascript" src="../Bootstrap/Bootstrap502/js/bootstrap.js"></script>
		<script type="text/javascript" src="../Bootstrap/Bootstrap502/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="../Bootstrap/Bootstrap-dataTables/datatables.js"></script>
	
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
		<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>

		<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../js/sweetalert/sw/sweetalert.min.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	  	<link href="../css/reportesGRM.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="js/validaciones.js"></script>
		<script type="text/javascript" src="js/ObraPublica.js"></script>
		<script type="text/javascript" src="js/EstimacionObra.js"></script>
		<script type="text/javascript" src="js/MultiAnual.js"></script>
		
		<style>
			.validateTips {
				border: 1px solid transparent;
				padding: 0.3em;
			}
			
			.notFilled {
				border: 2px solid #f00;
				background: #f99;
			}
			
			.calendar_1 {
				color: #808080;
				text-align: right;
				background-color: #F5F5f5;
				padding-left: 6px;
			}
			
			.calendar_2 {
				color: #808080;
				text-align: right;
				background-color: #E2E4FF;
				padding-left: 6px;
				padding-right: 6px;
			}
			
			.estilo {
				background-color: #E0E0F8;
				border: 1px solid #E0E0F8;
			}
			
			.normal {
				background-color: white;
			}
			
			.inEdit {
				background-color: #FFFF99;
			}
			
			.notEditable {
				background-color: #CCCCCC;
			}
			
			.numerico {
				text-align: right;
			}
			
			h3 {
				font-size: 1em;
				margin: .6em 0;
			}
			
			.NombreMes {
				font-weight: bold;
			}
			
			.ep {
				text-align: right;
				background-color: #CCCCCC;
				border: 1px solid #aaaaaa;
				color: #222222;
			}
			
			.monto {
				text-align: right;
				background-color: #CCCCCC;
				border: 1px solid #aaaaaa;
				color: #222222;
			}
			
			.montoCaptura {
				text-align: right;
				background-color: white;
				border: 1px solid #aaaaaa;
				color: black;
			}
			
			.montoCapturaEdit {
				text-align: right;
				background-color: #FFFF99;
				border: 1px solid #aaaaaa;
			}
			
			.montoMA {
				text-align: right;
				background-color: #CCCCCC;
				border: 1px solid #aaaaaa;
				color: #222222;
			}
			
			.montoAnual {
				text-align: right;
				background-color: white;
				border: 1px solid #aaaaaa;
				color: black;
			}
			
			.montoAnualEdit {
				text-align: right;
				background-color: #FFFF99;
				border: 1px solid #aaaaaa;
			}
			
			.totalMontoMultiAnual{
				text-align: right;
				background-color: white;
				border: 1px solid #aaaaaa;
				color: black;
			}
		</style>
		<script type="text/javascript" charset="utf-8">
			var giTimer = 0;
			var gsOperacion = 'null';
			var esConvenioModificatorio = <%=esConvenioModificatorio%>;
			var esPagoPasivo = <%=esPagoPasivo%>;
			var esPlurianual = <%=esPlurianual%>;
			var esCapturaEstimacion = <%=esCapturaEstimacion%>;
			var OP_ACTUALIZACION_DATOS = <%=OP_ACTUALIZACION_DATOS%>;
			var OP_CAPTURA_ESTIMACION = <%=OP_CAPTURA_ESTIMACION%>;
			var esConsulta = <%=esConsulta%>;
			var esSAIAlterno = <%=esSAIAlterno%>;
			//09/May
			var rowCount = 0 ; 
			var hoy = '<%=today%>';
			var uUR = '<%=uUR%>';
			var tablaPluObra;
			var esPlurianualAniosAnteriores=false;
			var esSAIFonden = <%=esSAIFonden%>;
		//IRD 28/10/2013 RO-0002 Se modifican calendarios
			var myModalProv,myModalMA,myModalDocAutorizacion,myModalEP;
			var myModalDocAmortizacion,myModalApcon,myModalPluObr;
			var idOper=1;
			$(function() {
				$("#fConvocatoria_").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
				});
			});
			
			$(function() {
				$("#fAclaracion_").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
				});
			});
			
			$(function() {
				$("#fRecepProp_").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
				});
			});
			
			$(function() {
				$("#fRecepFallo_").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
					
					});
				});
			$(function() {
				$("#fAdjudicacion_").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
					
				});
			});
			$(function() {
				$("#fInicioCom_").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
				});
			});
			
			$(function() {
				$("#fFinCom_").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
				});
			});
					
			$(function() {
				$("#fFinConv_").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
				});
			});
			$(function() {
				$("#fSuspencion_").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
				});
			});
			
			$(function() {
				$("#fReactiva_").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
				});
			});
			
			$(function() {
				$("#fPrecom_").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
				});
			});
			
			$(function() {
				$("#fAdicionales_").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
				});
			});
			// MLR false para ocultar la imagen de calendario por ordenes de Tello
			$(function() {
				$("#fEntregaVentanilla_").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
				});
			});
				
			  	
			$(function() {
				$("#fPago").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
				});
			});
			
			$(function() {
				$("#fInicioFisico").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
				});
			});
			
			$(function() {
				$("#fFinFisico").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
				});
			});
				//MLR 1411/2013	R0-0007	Agregar campos a estimaciones
			$(function() {
				$("#pEstimacionInicial").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
				});
			});
			$(function() {
				$("#pEstimacionfinal").datetimepicker( {
					format: 'DD/MM/YYYY',
					altField: "#actualDate",
				 	currentText: "Now",
					changeYear: true
				});
			});
			function verSuspencion(){
					$("#Link04").show("slow");
			}
			function esActualizacionDatos(){
				$(".ConvMod").hide();
				$(".Estimacion").hide();
				$(".ActualizaDatos").show();
				$("#Link09").click();
				document.getElementById("cOficioHacienda").readOnly = false;
				document.getElementById("cOficioHacienda").className = "estimacion";
				document.getElementById("cSituacionJuridica").readOnly = false;
				document.getElementById("cSituacionJuridica").className = "estimacion";
				document.getElementById("cTipoObraPub").readOnly = false;
				document.getElementById("cTipoObraPub").className = "estimacion";
				document.getElementById("cOficioFiniquito").readOnly = false;
				document.getElementById("cOficioFiniquito").className = "estimacion";
				//document.getElementById("btnActualizaDatos").readOnly = true;
				//document.getElementById("btnActualizaDatos").className = "estimacion";
				$("#NoCntActualiza").val($("#cCveContrato").val());
				document.getElementById('btnActualizaDatos').style.display = 'block';
				//leerdatos
				queryFormPost("readnDatosActualizaOP", { async : false });
			}
			function actualizaDatosOP(){
				queryFormPost("pContratoObraActualizaDatosCedula", { async : false });
			}
			$(document).ready(function() {
				$('#obra-list a').on('click', function (e) {
					e.preventDefault();
				  	$(this).tab('show');
				});
				myModalMA = new bootstrap.Modal(document.getElementById('dialogMA'), {
				  keyboard: false
				});
				myModalApcon = new bootstrap.Modal(document.getElementById('dialog-form-apcon'), {
				  keyboard: false
				});
				myModalDocAmortizacion = new bootstrap.Modal(document.getElementById('dialog-DocAmortizacion'), {
					keyboard: false
				});
				$("input.AyudaSyC").subIniciaDlg();
				$("input.autoCompletaSyC").subIniciaAutoCompleta();
				
				$("#addBtn").button();
				$("#AgregarEP").button();
				$("#btnActualizaDatos").button();
				$("#ddt_clavepresup2").css("display", "none");
				$("#ddt_clavePasivo").css("display", "none");
	            $("#ddt_clavePlurianual").css("display", "none");
	            $("#ddt_claveConvenioModificatorio").css("display", "none");
				$("#noEstimacion2").css("display", "none");
				$("#cAprobacionPLU").val($("#DcAprobacionPLU").val());
				$("#chk_radicado").change(function(){
					if ($("#chk_radicado").prop("checked")){
						$("#cEsRadicado").val("S");
					}else{
						$("#cEsRadicado").val("N");
					}
				});
				
				$('#tblConsultaProvedores').on('dblclick', 'tr',function(){
					if ($(this).hasClass('row_selected'))             
						$(this).removeClass('row_selected');         
					else            
						$(this).addClass('row_selected');
				  	var anSelected=fnGetSelected(oTableConsultaProv);
				  	aData=oTableConsultaProv.fnGetData(anSelected[0]);
				  	
				  	$("#cIDRFC").val(aData[0]);
					$("#cnombre").val(aData[1]);
					$("#divModals").hide();
					myModalProv.hide();
				});
				
			});//Fin documentReady
			//SASV Cambiar a Mayusculas
			function conMayusculas(field) {
	            field.value = field.value.toUpperCase()
	            }
			
			function quitaFmt( val ) {
			   	val = val.replace("$", "");
			   	val = val.replace(/,/g, "");
			
			   	if ( val.indexOf( "(" ) >= 0 ) {
					val = val.replace("(", "");
					val = val.replace(")", "");
					val = "-" + val;
			   	}
			   	return val;
			}		
				
			function quitaFmtIVA( val ) {
			   	
				val = val.replace("$", "");
			   	val = val.replace(/,/g, "");
			
			   	if ( val.indexOf( "(" ) >= 0 ) {
					val = val.replace("(", "");
					val = val.replace(")", "");
					val = "-" + val;
			   	}
			   	
			   	return val;
			}
				
			function quitaFmtIVAMonto( val ) {
			   	val = val.replace("$", "");
			   	val = val.replace(/,/g, "");
		
			   	if ( val.indexOf( "(" ) >= 0 ) {
					val = val.replace("(", "");
					val = val.replace(")", "");
					val = "-" + val;
			   	}
			   	return val;
			}
//	 		$(function() {
// 			$("#dialog:ui-dialog").dialog("destroy");
// 		});

			function onlyNumberss(evt) {
				var keyPressed = (evt.which) ? evt.which : event.keyCode;
				var strCheck = '-0123456789.';
			
				var key = String.fromCharCode( keyPressed );
				if (strCheck.indexOf( key ) == -1)
					return false; // Valida que sea numero y punto decimal
			
				return true; 
			}
			
			function onlyNumbers2(evt) {
				var keyPressed = (evt.which) ? evt.which : event.keyCode;
				var strCheck = '0123456789';
			
				var key = String.fromCharCode( keyPressed );
				if (strCheck.indexOf( key ) == -1)
					return false; // Valida que sea numero y punto decimal
			
				return true; 
			}
		
			function LetrasNums(evt) {
				var keyPressed = (evt.which) ? evt.which : event.keyCode;
				if (keyPressed > 123 && keyPressed != 209 && keyPressed != 241){
					alert ("Solo se permiten Letras y Numeros");
				}
				if (keyPressed == 61 || keyPressed == 63 || keyPressed == 62
				|| keyPressed == 59 || keyPressed == 58 || keyPressed == 60
				|| keyPressed == 91 || keyPressed == 92 || keyPressed == 93
				|| keyPressed == 94 || keyPressed == 95 || keyPressed == 96) {
					return false;
				 }
				return !(keyPressed > 32 && (keyPressed < 48 || keyPressed > 122) && keyPressed != 209 && keyPressed != 241);
			}
			
			function SinEspacios(evt) {
				var keyPressed = (evt.which) ? evt.which : event.keyCode;
				if (keyPressed == 32){
					alert ("No se permiten espacios");
					return false;
				 }
				 else{
				 	return true;
				 }
			}
			function OperacionSiguiente(id_oper){
				var opSig = '';
				var cancelContract = parseInt($("#iCancelContract").val(),10);
		        if (cancelContract == 1) {
				   opSig='consulta_obra';
				   return opSig;
				}
				if(id_oper==1){
					opSig='autoriza_obra';
				}
				if(id_oper==2){
					opSig='consulta_obra'; 
				}			
				if(id_oper==4){
					opSig='autoriza_convenio';
				}		
				if(id_oper==6){
					opSig='autoriza_pagopasivo';
				}
				if(id_oper==8){
					opSig='AUTORIZA_PLURI';
				}			
				return opSig;
			}
		
			function ResponsableSiguiente(id_oper){
				var resSig = '';
				var cancelContract = parseInt($("#iCancelContract").val(),10);
			    if (cancelContract == 1) {
				  resSig='CONSULTA_OBRA'; //o CAPTURA_OBRAPUBLICA
				  return resSig;
				}
				// Se cambio de VENTANILLA_COMPROMISO por  VENTANILLA_COMPROMISOOBRA
				if(id_oper==1 ){
					resSig='VENTANILLA_COMPROMISOOBRA';
				}			
				if(id_oper==2 ){
					resSig='CONSULTA_OBRA'; //o CAPTURA_OBRAPUBLICA
				}
				if(id_oper==4){
					resSig='VENTANILLA_COMPROMISOOBRA'; //o CAPTURA_OBRAPUBLICA
				}
				if(id_oper==6){
					resSig='VENTANILLA_COMPROMISOOBRA'; //o CAPTURA_OBRAPUBLICA
				}

				if(id_oper==8){
					resSig='VENTANILLA_COMPROMISOOBRA'; //o CAPTURA_OBRAPUBLICA
				}
				return resSig;
			}
			
			function onSubmit(id_oper){
				var p = window.parent;
				var resultado = false;

				p.gestion.setFolio($("#FOLIO").val());
				p.gestion.setOperador("<%=usuario.getNombre()%>");
				p.gestion.setFechaDocumento($("#fRecepcion").val()); //en la variable de caso dice FECHA_DOCUMENTO se quita el underscore y se capitalizan las primeras letras
				p.gestion.setEjercicioFiscal("<%=today.substring(6)%>");//EJRECICIO_FISCAL lo mismo que arriba
				p.gestion.setMoneda("MXP");
				
				resultado=true;	
				if(resultado){
					parent.document.getElementById("pb_send").disabled=false;
				}		
				
				return resultado;
			}
			
			function onLoadPlantilla(id_oper){
				$("#trDescripcionFONDEN").hide();
				idOper=id_oper;
				$("#cCveContrato").css("backgroundColor","white");
				$(".convenioModificatorio").each(
						function(){
							$(this).val("");
						}
				);
				$(".pagoPasivo").each(
						function(){
							$(this).val("");
						}
				);
				$(".plurianual").each(
						function(){
							$(this).val("");
						}
				);
				init();
				if( parent.document.getElementById("pb_save") )
					parent.document.getElementById("pb_save").style.visibility="hidden";
				
				fnTabsClick("apartado");
				$("#nFolioOPConvHeader").val("");
				$("#nFolioOPPagPasivoHeader").val("");
				$("#nFolioOPPlurianualHeader").val("");
				
				//if(esConvenioModificatorio)
				//	fnTabsClick("convenio");
					
				
				if ($("#cCveContrato").css("backgroundColor") != "#f0f0f0")
					$("#cCveContrato").focus();
				else
					$("#cIdTObra").focus();	
				
				$("#btnSuspencion").css("visibility","hidden");
				if(esConsulta){
					deshabilitaCamposConsulta();
					//document.getElementById("iconoPDF").style.visibility = 'visible';
					$("#iconoPDF").show();
				}if(esCapturaEstimacion){
					enableEstimacion();
					fnTabsClick("convenio");//aqui
				}
				
				$("#iCancelContract").val("0"); 
	    		var claveContrato = $("#cCveContrato").val();
	    		$(function() {
					$("#fperiodoEstimacionIni_").datetimepicker( {
						format: 'DD/MM/YYYY',
						altField: "#actualDate",
					 	currentText: "Now",
						changeYear: true
					});
				});
		 
				$(function() {
					$("#fperiodoEstimacionFin_").datetimepicker( {
						format: 'DD/MM/YYYY',
						altField: "#actualDate",
					 	currentText: "Now",
						changeYear: true
					});
				});
	       		queryFormPost({
					queryName : "readUltimoDiaMesActual",
					async : false,
					callback : function() {
					queryFormPost({
						queryName : "readprimerDiaEjercicio",
						async : false,
						callback : function() {
						   
								$(function() {
									$("#fperiodoEstimacionIni_").datetimepicker( {
										format: 'DD/MM/YYYY',
										altField: "#actualDate",
									 	currentText: "Now",
										changeYear: true
									});
								});
						 
								$(function() {
									$("#fperiodoEstimacionFin_").datetimepicker( {
										format: 'DD/MM/YYYY',
										altField: "#actualDate",
									 	currentText: "Now",
										changeYear: true
									});
								});
						}
					});		
		
					}
				});			
	    		
	   			if ($("#tipoUsuario").val() == "ADMIN" ){
					$(function() {
						$("#fRecepcion").datetimepicker( {
							format: 'DD/MM/YYYY',
							altField: "#actualDate",
						 	currentText: "Now",
							changeYear: true
						});
					});
					$("#fRecepcion").removeAttr("disabled");
				}
				else {
					$("#fRecepcion").attr("disabled","disabled");
				}
				//alert("termina");
				if(id_oper==4)//SASV Ver en Inbox Captura de Convenio Modificatorio
				queryFormPost("verInboxCapturaCM", {async: false });
				if(OP_ACTUALIZACION_DATOS){
					esActualizacionDatos();
				}
				
				//Valida si debe mostrar el boton de Cargar Plurianuales
				queryFormPost("readExisteApartado", {async: false });
				queryFormPost("readEsPlurianual", {async: false });	
				if($("#esPlurianual").val()==1){
					esPlurianualAniosAnteriores=true;
					$("#vcIdContrato").val(claveContrato);
						
				}else{
					esPlurianualAniosAnteriores=false;
				}
				
				var existeApartado=$("#existeApartado").val();
				if(existeApartado==0){
					queryFormPost("readExistePlurianualidad", {async: false });			
					var plurianuales=$("#totalPlurianuales").val();
					if(plurianuales>0){
						//document.getElementById("btnCargaPlurianualidad").style.visibility = 'visible';
						$("#div_btnCargaPlurianualidad").show();
					}
				}else if(esPlurianualAniosAnteriores){
					llenarCamposPlurianualidad();
				}
				muestraDescripBienInmueble(id_oper);
			}
			function muestraDescripBienInmueble(id_oper){
				if(id_oper==3){
					$("#idRealEstate").hide();
					$("#cIdRealEstate").show();
				}
			}
			function onPostSubmit(id_oper){
				return true;		
			}
			function onPostDisplay(id_oper){
			}
			
			function buscarDetalleApar(){
				campos = "";
				elParametro = "";
				
			}
			
			function fnTabsClick(pTab){
				//alert("pTab "+pTab);
				$("#ddt_clavePasivo").css("display", "none");
				$("#ddt_claveConvenioModificatorio").css("display", "none");
				//$("#dt_clavePlurianual").css("display", "none");
				queryFormPost({
					queryName : "estatusTramiteOPRead",
					async : false,
					callback : function() {
						if( $("#hAplicaApartado").val() == 'S' )
							inhabilitaModifEP();
					}
				});
				$('#pasaValor').val(pTab);
				loadHeader(pTab);
				loadDetail(pTab); 
				disablePant();
				enableControls();
				if(esConsulta){
					deshabilitaCamposConsulta();
				}
				//Borrar este alert
				//alert("idOper : "+idOper+" OP_CAPTURA_ESTIMACION: "+OP_CAPTURA_ESTIMACION+" esPagoPasivo: "+esPagoPasivo+" esConvenioModificatorio: "+esConvenioModificatorio+" esPlurianual:"+esPlurianual+" OP_ACTUALIZACION_DATOS:"+OP_ACTUALIZACION_DATOS);
				if(OP_CAPTURA_ESTIMACION){
					enableEstimacion();
					if (pTab =='pagoPasivo') {
						cargaDetallePagoPasivo();
						$("#cOLI").css("visibility", "hidden");
						$("#cCarteraProyec").css("visibility", "hidden");
						$("#cOLI_2").show();
						$("#cCarteraProyec_2").show();
					}else{
						$("#ddt_clavepresup2").css("display", "");
					}
					if (pTab =='plurianual') {
						cargaDetallePlurianual();
						$("#cOLI").css("visibility", "");
						$("#cCarteraProyec").css("visibility", "hidden");
						$("#cOLI_2").css("visibility", "hidden");
						$("#cCarteraProyec_2").show();
					}else{
						$("#ddt_clavepresup2").css("display", "");
					}
					if (pTab =='convenio') {
						$("#ddt_clavepresup2").css("display", "none");
						$("#ddt_claveConvenioModificatorio").css("display", "");
						$("#dt_claveConvenioModificatorio").css("display", "");
						loadDetailCM();
						$("#cOLI").css("visibility", "");
						$("#cCarteraProyec").css("visibility", "hidden");
						$("#cOLI_2").css("visibility", "hidden");
						$("#cCarteraProyec_2").show();
					}else{
						$("#ddt_clavepresup2").css("display", "");
					}
				}else 
				if(esPagoPasivo){
					if (pTab =='pagoPasivo') {
						enablePagoPasivo();
						muestraTabla();
					} else {
						$("#ddt_clavepresup2").css("display", "");
						$("#ddt_clavePasivo").css("display", "none");
						$("#noEstimacion2").css("display", "none");
					}
				} else
				if(esPlurianual){
					if (pTab =='plurianual') {
						enablePlurianual();
						muestraTabla();
						$("#ddt_clavePlurianual").css("display", "");
						$("#dt_clavePlurianual").css("display", "");
					} else {
						$("#ddt_clavepresup2").css("display", "");
						$("#noEstimacion2").css("display", "none");
						
						$("#ddt_clavePlurianual").css("display", "none");
						$("#dt_clavePlurianual").css("display", "none");
					}
				} else
				if(esConvenioModificatorio){
					//document.getElementById("iconoPDF").style.visibility = "hidden";
					$("#iconoPDF").hide();
					if (pTab =='convenio') {
						enableConvenio();
						muestraTabla();
						document.getElementById("chk_radicado").disabled = false;
					} else {
						$("#ddt_clavepresup2").css("display", "");
						$("#noEstimacion2").css("display", "none");
						$("#ddt_claveConvenioModificatorio").css("display", "none");
						$("#dt_claveConvenioModificatorio").css("display", "none");
					}
				}
				else if(OP_ACTUALIZACION_DATOS){
					
				}
			}

			function loadHeader(pTab){
				if(pTab == "apartado"){
					queryFormPost( {
						queryName : "readApartadoObraPublicaEncabezado",
						async : false,
								callback : function() {
									readOLIs();
									$("#cOLI").val($("#cOLIp").val());
									$("#cU_UE").removeAttr("disabled");
									cargaAreas();
									$("#id_area").val( $("#idAreaSend").val() );
									$("#cU_UE").attr("disabled","disabled");
									
									llenaFundamentoLegal();
									$("#cIdFundamentoLegal").val( $("#cArticuloSend").val() );
								}
							});
			   	}else if( pTab == "precompromiso" ){
			   		
			   		$("#ExistePrecomEnc").val("0");
					queryFormPost( {
						queryName : "existePrecomRead",
						async : false,
					   				callback:function(){
					   					if( $("#ExistePrecomEnc").val() > 0 ){
					   						$("#cU_UE").removeAttr("disabled");
									         queryFormPost({queryName:"tCatalogoPrecomEncabezadoRead", async : false,
									        	            callback:function(){
									        	            	cargaAreas();
																$("#id_area").val( $("#idAreaSend").val() );
																llenaFundamentoLegal();
																$("#cIdFundamentoLegal").val( $("#cArticuloSend").val() );
									        	            }
									         });
					   					}
					   				} 
					   		});
			   		
			   	}else if( pTab == "compromiso" ){
			   		$("#ExisteComEnc").val("0");
			   		
					querySelectPost("catEntFedRead", "entidadobra", {async : false,
						callback: function(){
							$("#entidadobra").val(14);
							llamadaWS();
						}
					});
					
					queryFormPost( {
						queryName : "existeComRead",
						async : false,
				   				callback: function(){
				   					if( $("#ExisteComEnc").val() > 0 ){
										queryFormPost( {
											queryName : "readObraPublicaCompromisoEncabezado",
											async : false,
											callback : function() {
						   							if($("#iEsPluriAnual").val() == '0'){
						   								$("#cPluriaAnual").attr("checked",false);
						   								$("#filaMultianual").attr("style","display:none");
						   							}else{
						   								$("#cPluriaAnual").attr("checked",true);
						   								$("#filaMultianual").attr("style","display:inline");
						   								
						   							}if( parseFloat($("#nPorcAnticipo").val()) > 30 ){
						   								$("#lblFolAut").css("visibility","visible");
														$("#noOfAutAnt").css("visibility","visible");
						   							}
						   							
						   							$("#cU_UE").removeAttr("disabled");
													cargaAreas();
													$("#id_area").val( $("#idAreaSend").val() );
													
													llenaFundamentoLegal();
													$("#cIdFundamentoLegal").val( $("#cArticuloSend").val() );
													llamadaWS();
											}
										});
				   						cargaInformacionMA();
				   						
				   						if($("#esFonden").val() == 1){
				   							$("#trDescripcionFONDEN").show();			   							
				   							$("#chk_esFonden").attr("checked", true);
				   							llenaComboFONDEN(-2, "idRealEstate", "FONDEN");
				   						}else {
				   							$("#trDescripcionFONDEN").hide();
				   						}
				   						
				   					}else{
				   						$("#cU_UE").removeAttr("disabled");
								         queryFormPost({queryName:"tCatalogoPrecomEncabezadoRead", async : false,
								        	            callback:function(){
								        	            	cargaAreas();
															$("#id_area").val( $("#idAreaSend").val() );
															
															llenaFundamentoLegal();
															$("#cIdFundamentoLegal").val( $("#cArticuloSend").val() );
								        	            }
								         });
								         $("#cU_UE").attr("disabled","disabled");
				   					}
				   				}
					   		
				   			});
					   		queryFormPost({
					   				queryName:"readConcursoAdjudicacion_OP",
					   				async:false,
									callback : function() {
									}
							});
			   	}else if( pTab == "convenio" ){
			   		queryFormPost("readObraPublicaCompromisoEncabezado", { async : false });
			   		queryFormPost("obraPublicaMontoTotalConCMRead", { async : false });
			   		loadHeaderCM();
				}else if (pTab == "solicitudPago") {
					queryFormPost("readObraPublicaCompromisoEncabezado", {
						async : false
					});
					loadInfoSolicitudPagos();
			   	}
				
			}

	function disableApartado() {
				document.getElementById("cCveContrato").readOnly=true;			
				$("#cCveContrato").css("backgroundColor", "#CCCCCC");
				$("#cU_UE").attr("disabled","disabled");
				$("#id_area").attr("disabled","disabled");
				$("#cIdTObra").attr("disabled","disabled");
				$("#cIdTipRec").attr("disabled","disabled");
				$("#mObra").attr("disabled","disabled");
				$("#nPorcIVAAplicable").attr("disabled","disabled");						
				$("#cOLI").hide();
				$("#cCarteraProyec").hide();
				
				$("#cOLI_2").show();
				$("#cCarteraProyec_2").show();
				
				$("#cDescripcionContrato").attr("disabled","disabled");			
				
				$("#AgregarEP").css("visibility","hidden");
				$("#btnGuarda").css("visibility","hidden");
				$("#btnAplica").val("Aplicar");				
				$("#btnAplica").css("visibility","hidden");
				$("#btnCancela").css("visibility","hidden");
				$("#btnSuspencion").css("visibility","hidden");
			}
	function disablePreCompromiso() {
				document.getElementById("cCveContrato").readOnly=true;	
				$("#cCveContrato").css("backgroundColor", "#CCCCCC");
				$("#cU_UE").attr("disabled","disabled");
				$("#id_area").attr("disabled","disabled");
				
				$("#cIdTipoAdjudica").attr("disabled","disabled");
				$("#cIdFundamentoLegal").attr("disabled","disabled");
				
				$("#cIdTObra").attr("disabled","disabled");
				$("#cIdTipRec").attr("disabled","disabled");
				$("#mObra").attr("disabled","disabled");
				$("#nPorcIVAAplicable").attr("disabled","disabled");
				$("#cOLI").css("visibility","hidden");
				$("#cCarteraProyec").css("visibility","hidden");
				
				$("#cOLI_2").show();
				$("#cCarteraProyec_2").show();
				$("#cDescripcionContrato").attr("disabled","disabled");	
				
				$("#btnAplica").val("Enviar a Ventanilla");
				$("#cIdConvocatoria").attr("disabled","disabled");	
				$("#fConvocatoria").attr("disabled","disabled");	
				$("#fAclaracion").attr("disabled","disabled");	
				$("#fRecepProp").attr("disabled","disabled");	
				$("#fRecepFallo").attr("disabled","disabled");
				$("#fAdjudicacion").attr("disabled","disabled");	
				$("#AgregarEP").css("visibility","hidden");
				$("#btnGuarda").css("visibility","hidden");	
				$("#btnAplica").css("visibility","hidden");
				$("#btnCancela").css("visibility","hidden");
				$("#btnSuspencion").css("visibility","hidden");
			}
	function disableCompromiso() {
				document.getElementById("cCveContrato").readOnly=true;	
				$("#cCveContrato").css("backgroundColor", "#CCCCCC");
				$("#cU_UE").attr("disabled","disabled");
				$("#id_area").attr("disabled","disabled");
				
				$("#cIdTObra").attr("disabled","disabled");
				$("#cIdTipRec").attr("disabled","disabled");
				$("#mObra").attr("disabled","disabled");
				$("#nPorcIVAAplicable").attr("disabled","disabled");
				$("#cOLI").css("visibility","hidden");
				$("#cCarteraProyec").css("visibility","hidden");
				//MLR RO-0010 
				$("#mObraContrato").attr("disabled","disabled");
				
				
				$("#cOLI_2").show();
				$("#cCarteraProyec_2").show();
				$("#cDescripcionContrato").attr("disabled","disabled");				
				
				//$("#cIdConcurso").attr("disabled","disabled");
				$("#cIDRFC").attr("disabled","disabled");
				$("#cnombre").attr("disabled","disabled");
				$("#cIdTipoContratoObra").attr("disabled","disabled");
				$("#nPorcAnticipo").attr("disabled","disabled");
				$("#cPluriaAnual").attr("disabled","disabled");
				$("#cIdAdicionales").attr("disabled","disabled");
				$("#noOfAdicionales").attr("disabled","disabled");
				$("#fAdicionales").attr("disabled","disabled");			
				$("#fInicioCom").attr("disabled","disabled");			
				$("#fFinCom").attr("disabled","disabled");
				$("#totalMontoMultiAnual").attr("disabled","disabled");
				$("#totalMultiAnual").attr("disabled","disabled");
				$("#TOTALMultiAnualTotal").attr("disabled","disabled");
				$("#nofianza").attr("disabled","disabled");
				$("#lblFolAut").attr("disabled","disabled");
				$("#lblFolAut").attr('onclick','').unbind('click');
				$("#lblFolAut").click(function() {
					return false;
				});
				
				$("#addBtn").css("visibility","hidden");
				$("#AgregarEP").css("visibility","hidden");
				$("#btnGuarda").css("visibility","hidden");
				$("#btnAplica").val("Aplicar");
				$("#btnAplica").css("visibility","hidden");
				$("#btnCancela").css("visibility","hidden");
			}
			function enableComp(){			
				document.getElementById("cCveContrato").readOnly=false;	
				$("#cCveContrato").css("backgroundColor", "#FFFFFF");
				$("#cU_UE").removeAttr("disabled");
				$("#id_area").removeAttr("disabled","disabled");
				
				$("#cIdTObra").removeAttr("disabled");
				$("#cIdTipRec").removeAttr("disabled");
				$("#mObra").removeAttr("disabled");
				$("#nPorcIVAAplicable").removeAttr("disabled");			
				$("#cDescripcionContrato").removeAttr("disabled");
				$("#btnGuarda").css("visibility","visible");
				$("#AgregarEP").css("visibility","visible");
				$("#btnAplica").css("visibility","visible");
				$("#btnCancela").css("visibility","visible");
				//$("#btnSuspencion").css("visibility","visible");
			}
			
			function enablePant(){
				document.getElementById("cCveContrato").readOnly=false;
				$("#cCveContrato").css("backgroundColor", "#FFFFFF");
				
				$("#cU_UE").removeAttr("disabled");
				$("#id_area").removeAttr("disabled","disabled");
				
				$("#cIdTObra").removeAttr("disabled");
				$("#cIdTipRec").removeAttr("disabled");
				$("#mObra").removeAttr("disabled");
				$("#nPorcIVAAplicable").removeAttr("disabled");			
				$("#cDescripcionContrato").removeAttr("disabled");
				
				$("#cIdConvocatoria").removeAttr("disabled");	
				$("#fConvocatoria").removeAttr("disabled");	
				$("#fAclaracion").removeAttr("disabled");	
				$("#fRecepProp").removeAttr("disabled");			
				$("#fRecepFallo").removeAttr("disabled");	
				$("#fAdjudicacion").removeAttr("disabled");
				//$("#cIdConcurso").removeAttr("disabled");
				$("#cIdTipoAdjudica").removeAttr("disabled");
				$("#cIdFundamentoLegal").removeAttr("disabled");
				$("#cIDRFC").removeAttr("disabled");
				$("#cnombre").removeAttr("disabled");
				$("#cIdTipoContratoObra").removeAttr("disabled");
				$("#nPorcAnticipo").removeAttr("disabled");
				$("#cPluriaAnual").removeAttr("disabled");
				$("#cIdAdicionales").removeAttr("disabled");
				$("#noOfAdicionales").removeAttr("disabled");
				$("#fAdicionales").removeAttr("disabled");			
				$("#fInicioCom").removeAttr("disabled");			
				$("#fFinCom").removeAttr("disabled");			
				
				$("#AgregarEP").css("visibility","visible");
				$("#btnGuarda").css("visibility","visible");			
				$("#btnAplica").css("visibility","visible");
				$("#btnCancela").css("visibility","visible");
				//$("#btnSuspencion").css("visibility","visible");
				
			}		
			
			function validCampos(){			
				switch($("#pasaValor").val()){
					case 'apartado':			
			            validApartado();
						break;	
					case 'precompromiso':			
					      validPrecompromiso();
					      break;	
				  	case 'compromiso':
					     validCompromiso();
					     break;
				  	case 'convenio':
				  		validaConvenio();
				  		break;
				  	case 'pagoPasivo':
				  		validaPagoPasivo();
				  		break;
				  	case 'plurianual':
				  		validaPlurianual();
				  		break;
				  	
			    }
			}
			
			function validApartado(){	
			//alert ("#cCveContrato Apartado JSP" + $("#cCveContrato").val());	
				if($("#cCveContrato").val()!=''){
					var existeCveCnt = existeNoContrato();
					if( existeCveCnt ){
				        alert("El contrato " + $("#cCveContrato").val()
						+ " ya existe capturado previamente en el folio: "
						+ $("#folioExistente").val()
						+ "\n Por favor asigne otro n\u00FAmero de contrato.");
						return false;
					}
				}
				if($("#cU_UE").val() == ''){
					alert("Por favor ingrese la unidad ejecutora.");
					return false;
				}else if (quitaFrmt($("#id_area").val())==''){
					alert("Por favor ingrese el area");
					return false;
				}else if (quitaFrmt($("#mObra").val())<1){
					alert("Por favor ingrese el Monto de Obra");
					return false;
				}else if ($("#cCarteraProyec").val()==''){				
					alert("Seleccione una Cartera de Proyecto");				
					return false;			
				}else if ($("#cOLI").val()==''){				
					alert("Por favor Seleccione un OLI");				
					return false;			
				}else if ($("#cDescripcionContrato").val()==''){				
					alert("Por favor ingrese la descripcion de la obra");				
					return false;			
				}else if($("#dt_clavepresup").dataTable().fnGetData().length == 0){				
					alert('No ha calendarizado montos para el apartado');
					return false;
				}else if(!validaMontoTotal()){
					return false;
				}
				/*else if( requiereCuestionario() && !cuestionarioCapturado() ){
					alert("Se requiere que conteste el cuestionario de determinacion del articulo 15-D de la LFT");
					
				}*/
				else{
					applyDocument();
				}
			}

			function validPrecompromiso(){
				//alert("cCveContrato Precompromiso JSP: "+ $("#cCveContrato").val() );
				if($("#cCveContrato").val()!=''){
					var existeCveCnt = existeNoContrato();
					if( existeCveCnt ){
						alert("El contrato " + $("#cCveContrato").val() + " ya existe capturado previamente en el folio: "  + $("#folioExistente").val() + "\n Por favor asigne otro n\u00FAmero de contrato." );
						return false;
					}
				}
				
				if($("#cU_UE").val() == ''){
					alert("Por favor ingrese la unidad ejecutora.");
					return false;
				}else if (quitaFrmt($("#id_area").val())==''){
					alert("Por favor ingrese el area");
					return false;
				}else if ($("#cDescripcionContrato").val()==''){				
					alert("Por favor ingrese la descripcion de la obra");				
					return false;			
				}else if($("#cIdConvocatoria").val()=='' || $("#fConvocatoria").val()=='' ){
					alert("Por favor ingrese Convocatoria y Fecha de Convocatoria");
					return false;
				}else if($("#fAclaracion").val()== '' && "01" == $("#cIdTipoAdjudica").val()){
					alert("Por favor ingrese Fecha de Aclaracion");
					return false;
				}else if($("#fRecepProp").val()==''){
					alert("Por favor ingrese Fecha de Propuesta");
					return false;
				}else if($("#fRecepFallo").val()==''){
					alert("Por favor ingrese Fecha de Fallo");
					return false;			
				}else if($("#fAdjudicacion").val()==''){
					alert("Por favor ingrese Fecha de Formalización.");
					return false;			
				}else if(!validaMontoTotal()){
					return false;
				}else if (parseFloat($("#nPorcAnticipo").val()) > 30) {
					if ($("#noOfAutorizacion").val() == '') {
						alert("Por favor capture el No. de Oficio de Autorizacion de Porcentaje de Anticipo");
						return false;
					} else {
						queryFormPost("numerodePaginasOpRead", {
							async : false
						});
						if ($("#total_paginas").val() < 1){
							alert("Por favor Digitalice el documento de Autorizacion de Porcentaje");
							return false;
						}
					}

				}else{
					applyDocument();
				}
			}	
			
			function cmdImprimir(){
				queryFormPost("readContratoObraPublica", { async : false });
				window.open(
							"../admin/SeguridadCatalogos?"
								+ "catalogo=REPORTE"
								+ "&accion=run"
								+ "&rn=rptContratoOBRA.jasper"
								+ "&cidContrato=" + $("#cidContrato").val()
								+ "&cEjercicio=" + $("#cEjercicio").val(),
							"popacuse",
							"scrollbars=1, resizable=yes, width=1024, height=768");
			}
			
			function validCompromiso(){
				if( parseFloat($("#nPorcAnticipo").val())>0){
					if( $("#porcRetencion").val() == '' || parseFloat($("#porcRetencion").val())<=0 ){
						alert("La captura del porcentaje de retencion es obligatoria si existe anticipo.");
						return false;
					}
				}
				if( parseFloat($("#nPorcAnticipo").val())>30){
					if($("#noOfAutorizacion").val() == ''){
						alert("Por favor capture el No. de Oficio de Autorizacion de Porcentaje de Anticipo");
						return false;
					}else {
						queryFormPost("numerodePaginasOpRead", {async : false});
						if($("#total_paginas").val() < 1){
							alert("Por favor Digitalice el documento de Autorizacion de Porcentaje");
							return false;
						}
					}
				}
				if($("#cCveContrato").val()==''){
					alert("Por favor ingrese el No. de Contrato");
					return false;
				}else if("" != $("#cCveContrato").val() ){
					if( existeNoContrato() ){
						alert("El contrato " + $("#cCveContrato").val() + " ya existe capturado previamente en el folio: "  + $("#folioExistente").val() 
						+ "\n Por favor asigne otro n\u00FAmero de contrato." );
						alert("\u00A1ATENCI\u00D3N\u0021 No se aplicar\u00E1 el documento.");
						return false;
					}
				}
				
				if ($("#mObra").val()<1){
					alert("Por favor ingrese el Monto de Obra");
					return false;
				}else if($("#cU_UE").val() == ''){
					alert("Por favor ingrese la unidad ejecutora.");
					return false;
				}else if (quitaFrmt($("#id_area").val())==''){
					alert("Por favor ingrese el area");
					return false;
				}else if ($("#cDescripcionContrato").val()==''){				
					alert("Por favor ingrese la descripcion de la obra");				
					return false;			
				}/*else if($("#cIdConcurso").val()==''){
						alert("Por favor ingrese No. Concurso");			
						return false;
				}*/else if($("#cIDRFC").val()=='' || $("#cname").val()==''){
					alert("Por favor ingrese R.F.C y Nombre de Beneficiario");
					return false;
				}else if($("#noOfAdicionales").val()=='' || $("#fAdicionales").val()=='' || $("#fAdicionales").val()==''){
					alert("Por favor ingrese Tipo de Oficio, No. Oficio y Fecha de Oficio");
					return false;
				}else if($("#fInicioCom").val()=='' || $("#fFinCom").val()==''){
					alert("Por Favor Ingrese la Vigencia del Contrato");
					return false;
				}else if(!validaMontoTotal()){
					return false;
				}else if( $("#entidadobra").val()  == '' || $("#entidadobra").val() == '-1' ){
					alert("Por favor Indique la entidad federativa en que se desarrollara la obra");
					return false;
			    }else{
					applyDocument();
				}
			}
			
			function validaConvenio(){
				if(hayCambios())
					applyDocument();
			}

			function validaPagoPasivo(){
				if(verifPasivo())
					applyDocument();
			}
			function validaPlurianual(){
				if(verifPlurianual())
					applyDocument();
			}
			function setcAprobacionPLU(){
				$("#cAprobacionPLU").val($("#DcAprobacionPLU").val());				
			}
			
		
		</script>
	</head>
	<body >
		<form id="FormContrato" name="FormContrato">
			<div id="container" class="container-fluid">
				<h5 style="color: #1A69A9;" >Obra P&uacute;blica</h5>
				<fieldset class="form-group border p-3">
					<div class="form-group row">
						<div class="col-auto">
							<label for="FOLIO">Folio SAI:</label>
						</div>
				    	<div class="col-auto">
				    		<input type="text" disabled="disabled" class="form-control form-control-sm" title="Número de Folio, se asigna automáticamente" 
				      		value="<%=c.getFolio()%>" placeholder="Folio SAI" aria-label="cFolio" aria-describedby="basic-addon1"  id="FOLIO_SAI" name="FOLIO_SAI" />
				      		<input type="hidden" readonly="readonly" value="<%=c.getFolio()%>"  id="FOLIO" name="FOLIO" />
				    	</div>
				  	</div>
				  	<div class="form-group row" id="div_btnCargaPlurianualidad" style="display: none;">
				    	<div class="col-auto">
				    		<input type="button" class="btnInterface ui-button ui-widget ui-state-default ui-corner-all" 		
				    		id="btnCargaPlurianualidad" 	name="btnCargaPlurianualidad" 	value="Cargar Plurianualidad"		onclick="cargaPlurianuales();">
				    	</div>
				  	</div>
				  	<div class="form-group row" id="iconoPDF" style="display: none;">
				    	<div class="col-auto">
				    		<input type="button" class="btnInterfacePDF ui-button ui-widget ui-state-default ui-corner-all" 		
				    		id="btnPDF" 	name="btnPDF" 	value="PDF"		onClick="cmdImprimir()">
				    	</div>
				  	</div>
				  	<div class="form-group">
						<div class="row">
							<div class="input-group">
								<div class="col-2">
									<label for="cU_UE">U. Normativa: </label>
								</div>
								<div class="col-10">
									<select  class="custom-select" title="Seleccionar del Combo la Unidad Central Normativa que le corresponda" id="cU_UE" name="cU_UE" onchange="cargaAreas()" >
									</select>
								</div>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="row">
							<div class="input-group">
								<div class="col-2">
									<label for="id_area">&Aacute;rea: </label>
								</div>
								<div class="col-10">
									<select  class="custom-select" title="Seleccionar del combo el &Aacute;rea que le corresponda" id="id_area" name="id_area" ></select>
								</div>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="row">
							<div class="input-group">
								<div class="col-2">
									<label for="cIdTObra">Tipo de Obra: </label>
								</div>
								<div class="col-2">
									<select  class="custom-select" title="Seleccionar del combo el Tipo de Obra que aplica según sea el caso en cada Contrato." id="cIdTObra" name="cIdTObra" ></select>
								</div>
								<div class="col-2">
									<label for="cIdTipRec">Tipo de Recurso: </label>
								</div>
								<div class="col-2">
									<select  class="custom-select" title="Poner el Tipo de Recurso con que se ejecutaran los Recursos de Obra (En 2013 solo existe el Tipo de Recurso RF = Recursos Fiscales)."
									 id="cIdTipRec" name="cIdTipRec" ></select>
								</div>
								<div class="col-2">
									<label for="cIdTipRec">Fecha de Registro: </label>
								</div>
								<div class="col-2">
									<input class="form-control" title="Esta Fecha la proporciona el Sistema por Default" type="text" id="fRecepcion" name="fRecepcion"  readonly="readonly" />
								</div>
							</div>
						</div>
					</div>
					<!-- Solo para Plurianuales -->
					<div class="form-group" id="filaMultianual" style="display:none; ">
						<div class="row">
							<div class="input-group">
								<div class="col-2">
									<label for="nMontoContratoOP">Monto Plurianual del Contrato s/iva: </label>
								</div>
								<div class="col-2">
									<input class="form-control notEditable numerico" type="text" id="nMontoContratoOP" name="nMontoContratoOP" onblur="onBlurMoneyContrato()"  readonly="readonly"  title="Monto Total Plurianual sin IVA"/>
								</div>
								<div class="col-2">
									<label for="nMontoContratoOPiva">Monto Plurianual iva: </label>
								</div>
								<div class="col-2">
									<input class="form-control notEditable numerico" type="text" id="nMontoContratoOPiva" name="nMontoContratoOPiva" onblur="onBlurMoneyContrato()"  readonly="readonly" />
								</div>
								<div class="col-2">
									<label for="nMontoTContratoOPCiva">Monto total Plurianual c/iva: </label>
								</div>
								<div class="col-2">
									<input class="form-control notEditable numerico" type="text" id="nMontoTContratoOPCiva" name="nMontoTContratoOPCiva" onblur="onBlurMoneyContrato()"  readonly="readonly" />
								</div>	
							</div>
						</div>
					</div>
					<div class="form-group" >
						<div class="row">
							<div class="input-group">
								<div class="col-2">
									<label for="mObra">Monto del Ejercicio: </label>
								</div>
								<div class="col-2">
									<input title="Se capturara por única vez el Monto del Contrato en el formato de pesos y sin IVA." type="text" id="mObra" name="mObra" maxlength="12"
										size="12" value="" placeholder="0.00" onKeyPress="return onlyNumbers(event)" onfocus="onFocusMoney()" onblur="onBlurMoney()" class="form-control numerico" />
								</div>
								<div class="col-1">
									<label for="nPorcIVAAplicable">% IVA: </label>
								</div>
								<div class="col-1">
									<select title="Se selecciona del combo el IVA aplicable, según la zona Geogr&aacute;fica del Pa&iacute;s" name="nPorcIVAAplicable" id="nPorcIVAAplicable" onChange="recaulculaMontos();" class="custom-select">
										<option selected value="0">
											0%
										</option>
									</select>
								</div>
								<div class="col-1">
									<label for="mImporteIVA">Monto IVA: </label>
								</div>
								<div class="col-2">
									<input title="Este Monto lo da el Sistema por Default, al igual que el Monto Total del Contrato con el IVA incluído." type="text" maxlength="12" size="12" name="mImporteIVA"
									id="mImporteIVA" value="0" onKeyPress="return onlyNumbers(event)" class="form-control numerico" readonly="readonly"/>
								</div>
								<div class="col-1">
									<label for="mTotal">Monto Total: </label>
								</div>
								<div class="col-2">
									<input type="text" maxlength="12" size="12" id="mTotal" name="mTotal" readonly="readonly" id="mTotal" value="0"
										class="form-control obligatorio notEditable numerico" onKeyPress="return onlyNumbers(event)"  title="Monto Total del Ejercicio fiscal actual"/>	
								</div>
							</div>
						</div>
					</div>
					<div class="form-group" >
						<div class="row">
							<div class="input-group">
								<div class="col-2" >
									<label for="cCarteraProyec">Cartera Proyecto: </label>
								</div>
								<div class="col-4">
									<select class="custom-select" title="Se seleccionará del combo la cartera de proyecto autorizada que corresponda al Contrato." id="cCarteraProyec" name="cCarteraProyec"></select>
									<input type="text" id="cCarteraProyec_2" name="cCarteraProyec_2" readonly="readonly" class="form-control" style="background-color: #E8E8E8"/>
								</div>
								<div class="col-2" >
									<label for="cOLI"><%if( esSAIFonden ){ %>Acuerdo:<%}else{ %>O.L.I.:<%} %></label>
								</div>
								<div class="col-4">
									<select title="<%=tituloOLI%>"  id="cOLI" name="cOLI" class="custom-select">
										<option>
											Seleccionar
										</option>
									</select>
									<input type="text" id="cOLI_2" name="cOLI_2" readonly="readonly" class="form-control" style="background-color: #E8E8E8">
								</div>
							</div>
						</div>
					</div>
					<div class="form-group" >
						<div class="row">
							<div class="input-group">
								<div class="col-2">
									<label for="cDescripcionContrato">Descripci&oacute;n de la Obra:</label>
								</div>
								<div class="col-10">
									<textarea title="Se capturará la Descripción completa del Contrato correspondiente." id="cDescripcionContrato" name="cDescripcionContrato"
										class="form-control" rows="3" onKeyPress="return LetrasNums(event)" onChange="conMayusculas(this)"></textarea>
								</div>
							</div>
						</div>
					</div>
<!-- 				Sección de tabs -->
					<div class="form-group row">
						<div class="card">
							<div class="card-header">
					        	<ul class="nav nav-tabs card-header-tabs" id="obra-list" role="tablist">
					            	<li class="nav-item"><a id="Link01" class="nav-link active" href="#tabs-0" role="tab" aria-controls="apartado" aria-selected="true" 		onClick="fnTabsClick('apartado')">Apartado</a></li>
					            	<li class="nav-item"><a id="Link02" class="nav-link"  		href="#tabs-1" role="tab" aria-controls="precompromiso" aria-selected="false" onClick="fnTabsClick('precompromiso');">Precompromiso</a></li>
					            	<li class="nav-item"><a id="Link03" class="nav-link" 		href="#tabs-2" role="tab" aria-controls="compromiso" aria-selected="false"		onClick="fnTabsClick('compromiso');">Compromiso</a></li>
					            	<li class="nav-item"><a id="Link04" class="nav-link" 		href="#tabs-3" role="tab" aria-controls="suspension" aria-selected="false" 	onClick="">Suspensi&oacute;n</a></li>
					            	<li class="nav-item"><a id="Link05" class="nav-link"  		href="#tabs-4" role="tab" aria-controls="convenio" aria-selected="false" 	onClick="fnTabsClick('convenio');">Convenio</a></li>
					            	<li class="nav-item"><a id="Link06" class="nav-link" 		href="#tabs-5" role="tab" aria-controls="estimacion" aria-selected="false"	onClick="fnTabsClick('convenio');fnTabsClick('pagoPasivo');fnTabsClick('solicitudPago'); muestraTabla(); ">Estimaci&oacute;n</a></li>
					            	<li class="nav-item"><a id="Link07" class="nav-link"  		href="#tabs-7" role="tab" aria-controls="pagoPasivo" aria-selected="false" 	onClick="fnTabsClick('convenio');fnTabsClick('pagoPasivo'); ">Pago Pasivo</a></li>
					            	<li class="nav-item"><a id="Link08" class="nav-link" 		href="#tabs-8" role="tab" aria-controls="plurianual" aria-selected="false"		onClick="fnTabsClick('plurianual');">Plurianual</a></li>
					            	<li class="nav-item"><a id="Link09" class="nav-link" 		href="#tabs-9" role="tab" aria-controls="actualizaDatos" aria-selected="false" 		onClick="muestraTabla();">Actualizacion de Datos</a></li>
					          	</ul>
							</div>
							<div class="card-body">
			           			<div class="tab-content mt-3">
			            			<div class="tab-pane active" id="tabs-0" role="tabpanel">
			            			</div>
<!-- 								Tab precompromiso -->			            			
			            			<div class="tab-pane" id="tabs-1" role="tabpanel">
			            				<div class="form-group" >
											<div class="row">
										    	<div class="col-3">
										      		<label for="cIdTipoAdjudica">Art&iacute;culo Contrataci&oacute;n:</label>
										      	</div>
										      	<div class="col-9" >
													<select id="cIdTipoAdjudica" name="cIdTipoAdjudica" onchange="llenaFundamentoLegal()" class="custom-select">
													</select>
												</div>
										  	</div>
										</div>
										<div class="form-group" >
										  	<div class="row">
										    	<div class="col-3">
										      		<label for="cIdFundamentoLegal">Fundamento Legal: </label>
										      	</div>
										      	<div class="col-9" >
													<select id="cIdFundamentoLegal" name="cIdFundamentoLegal" class="custom-select">
														<option value="">Seleccione una opci&oacute;n</option>
													</select>
												</div>
										  	</div>
										</div>
										<div class="form-group" >
										  	<div class="row">
										    	<div class="col-3">
										      		<label for="cIdConvocatoria">No. de Convocatoria: </label>
										      	</div>
										      	<div class="col-9" >
													<input title="Se capturará el Número de Licitación como se haya licitado el Contrato (igual que en Compranet)" placeholder="Capturará el Número de Convocatoria" 
													type="text" id="cIdConvocatoria" class="form-control" name="cIdConvocatoria" size="40" maxlength="40" />
												</div>
										  	</div>
										</div>
	<!-- 									Fechas -->
										<div class="form-group" >
											<div class="row">
												<div class="form-group col-md-3">
													<label for="#fConvocatoria_">Fecha de Convocatoria:</label>
													<div class="input-group date" id="fConvocatoria_" data-target-input="nearest">
											          <input type="text" class="form-control datetimepicker-input" data-target="#fConvocatoria_" title="Fecha de Convocatoria" id="fConvocatoria" name="fConvocatoria"  />
											          <div class="input-group-append" data-target="#fConvocatoria_" data-toggle="datetimepicker" title="Fecha de Convocatoria">
											            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
											          </div>
											        </div>
										        </div>
										        <div class="form-group col-md-3">
													<label for="#fAclaracion_">Fecha Junta de Aclaraciones:</label>
													<div class="input-group date" id="fAclaracion_" data-target-input="nearest">
											          <input type="text" class="form-control datetimepicker-input" data-target="#fAclaracion_" title="Fecha de Aclaraciones" id="fAclaracion" name="fAclaracion"  
											          onchange="validaFechaMayor('fConvocatoria', 'fAclaracion', 'Debe capturar primero la fecha de convocatoria','La fecha de Junta de aclaracion debe ser mayor a la fecha de convocatoria' )"/>
											          <div class="input-group-append" data-target="#fAclaracion_" data-toggle="datetimepicker" title="Fecha de Aclaraciones">
											            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
											          </div>
											        </div>
										        </div>
										        <div class="form-group col-md-3">
													<label for="#fRecepProp_">Fecha Recepci&oacute;n de Propuesta:</label>
													<div class="input-group date" id="fRecepProp_" data-target-input="nearest">
											          <input type="text" class="form-control datetimepicker-input" data-target="#fRecepProp_" title="Fecha de Recepción y Apertura de propuesta Técnica y Económica" id="fRecepProp" name="fRecepProp" 
											          onchange="validaFechaMayor('fAclaracion', 'fRecepProp', 'Debe capturar primero la fecha de Junta de aclaracion','La fecha de Recepcion de Propuesta debe ser mayor a la fecha de convocatoria' )"/>
											          <div class="input-group-append" data-target="#fRecepProp_" data-toggle="datetimepicker" title="Fecha de Recepción y Apertura de propuesta Técnica y Económica">
											            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
											          </div>
											        </div>
										        </div>
										        <div class="form-group col-md-3">
													<label for="#fRecepFallo_">Fecha de Fallo:</label>
													<div class="input-group date" id="fRecepFallo_" data-target-input="nearest">
											          <input type="text" class="form-control datetimepicker-input" data-target="#fRecepFallo_" title="Fecha del Fallo según acta." id="fRecepFallo" name="fRecepFallo" 
											          onchange="validaFechaMayor('fRecepProp', 'fRecepFallo', 'Debe capturar primero la fecha de Recepcion de Propuesta','La fecha de Fallo debe ser mayor a la fecha de Recepcion de Propuesta' )"/>
											          <div class="input-group-append" data-target="#fRecepFallo_" data-toggle="datetimepicker" title="Fecha del Fallo según acta.">
											            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
											          </div>
											        </div>
										        </div>
											</div>
											<div class="row">
												<div class="form-group col-md-3">
													<label for="#fAdjudicacion_">Fecha de Formalizaci&oacute;n:</label>
													<div class="input-group date" id="fAdjudicacion_" data-target-input="nearest">
											          <input type="text" class="form-control datetimepicker-input" data-target="#fAdjudicacion_" title="Fecha de Adjudicación" id="fAdjudicacion" name="fAdjudicacion"  />
											          <div class="input-group-append" data-target="#fAdjudicacion_" data-toggle="datetimepicker" title="Fecha de Adjudicacion">
											            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
											          </div>
											        </div>
										        </div>
											</div>
										</div>
										<div class="form-group" >
											
										</div>
			            			</div>
<!-- 								Fin precompromiso -->
<!-- 								Tab compromiso -->			            			
			            			<div class="tab-pane" id="tabs-2" role="tabpanel">
			            				<div class="form-group" id="divCheckFonden">
											<div class="row">
												<div class="col-auto col-auto-inline">
													<div class="row"  >
														<div class="col-auto">		
														<label class="form-check-label" for="chk_esFonden">¿La contratación es de Fonden? </label>
														</div>
														<div class="col-auto">
											  				<input class="form-check-input" type="checkbox" id="chk_esFonden" name="chk_esFonden" value="0" onclick="habilitaFONDEN()">
											  			</div>
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
											<div class="row">
												<div class="col-2">
													<label for="cCveContrato">No. Contrato: </label>
												</div>
												<div class="col-4">
													<input type="text" class="form-control" placeholder="Ejemplo CNF-LO-016RHQ001-E193" 
													aria-label="Número de Contrato CNF-LO-016RHQ001-E193" aria-describedby="basic-addon1"  name="cCveContrato" id="cCveContrato"  value=""
													size="40" maxlength="40" onKeyPress="return SinEspacios(event)" onChange="conMayusculas(this)" />
												</div>
												<div class="col-auto col-auto-inline">
													<div class="row">
														<div class="col-auto">		
														<label class="form-check-label" for="cPluriaAnual" id="labelPluri">¿La contratación es Plurianual? </label>
														</div>
														<div class="col-auto">
											  				<input class="form-check-input" type="checkbox"  id="cPluriaAnual" name="cPluriaAnual" />
															&nbsp;&nbsp;<a id="verPlu" href="#" onclick="verPlurianual();return false;">Ver</a>
											  			</div>
													</div>
												</div>
												
											</div>
										</div>
										<div class="form-group">
											<div class="row">
												<div class="col-2">
														<label for="cIDRFC">R.F.C: </label>
												</div>
												<div class="col-4">
	 												<input type="text" id="cIDRFC" name="cIDRFC"  readonly="readonly" class="form-control"  /><!--Ayuda -->
												</div>
												<div class="col-1">
	 												<input type="button" id="btnShowProveedores" name="btnShowProveedores" onclick="showCatProveedores();" value="..." class="form-control" style="width: 30px"/><!--Ayuda -->
												</div>
												<div class="col-5">
													<input type="text" id="cnombre" name="cnombre" size="61" maxlength="100" readonly="readonly" class="form-control" />
												</div>
											</div>
										</div>
										<div class="form-group">
											<div class="row">
												<div class="col-2">
														<label for="cIdTipoContratoObra">Tipo de Contrato: </label>
												</div>
												<div class="col-4">
													<select name="cIdTipoContratoObra" id="cIdTipoContratoObra" class="custom-select">
													</select>
												</div>
												<div class="col-2">
													<label for="esquemaPrecios">Esquema de precios: </label>
												</div>
												<div class="col-4">
													<select name="esquemaPrecios" id="esquemaPrecios" class="custom-select">
													</select>
												</div>
											</div>
										</div>
										<div class="form-group">
											<div class="row">
												<div class="col-2">
														<label for="nofianza">N&uacute;mero de Fianza de Cumplimiento: </label>
												</div>
												<div class="col-2">
													<input type="text" id="nofianza" name="nofianza" class="form-control" />
												</div>
												<div class="col-2">
													<label for="nofianzaOcultos">N&uacute;mero de Fianza de Vicios Ocultos: </label>
												</div>
												<div class="col-2">
													<input type="text" id="nofianzaOcultos" name="nofianzaOcultos" class="form-control" />
												</div>
												<div class="col-2">
													<label for="nofianzaAnticipo">N&uacute;mero de Fianza de Anticipo: </label>
												</div>
												<div class="col-2">
													<input type="text" id="nofianzaAnticipo" name="nofianzaAnticipo" class="form-control" />
												</div>
											</div>
										</div>
										<div class="form-group">
											<div class="row">
												<div class="col-2">
														<label for="nPorcAnticipo">Porcentaje de Anticipo: </label>
												</div>
												<div class="col-2">
													<div class="input-group">
														<input type="text" id="nPorcAnticipo" name="nPorcAnticipo" class="form-control"  value="0" size="5" maxlength="5" onKeyPress="return onlyNumbers(event)"/>
														 <span class="input-group-text" id="inputGroupPorcentaje">%</span>
													</div>
												</div>
												<div class="col-2">
													<label for="mAnticipo">Monto c/IVA: </label>
												</div>
												<div class="col-2">
													<input type="text" id="mAnticipo" name="mAnticipo" class="form-control"  size="15" maxlength="12"  onKeyPress="return onlyNumbers(event)"/>
												</div>
												<div class="col-2">
													<label for="entidadobra">Entidad Federativa: </label>
												</div>
												<div class="col-2">
													<select id="entidadobra" name="entidadobra" onchange="llamadaWS()" class="custom-select"></select>
												</div>
											</div>
										</div>
										<div class="form-group" style="display: none;">
											<div class="row">
												<div class="col-2">
														<a id="lblFolAut" href="#"title="Click para editar No. de Autorizacion" onclick="showDialogNoAut();return false;" class="nav-link" style="visibility: hidden;">Folio:</a>
												</div>
												<div class="col-2">
													<input type="text" id="noOfAutAnt" name="noOfAutAnt" size="20" maxlength="40" value="" style="visibility: hidden;" class="form-control" alt="Click para editar" />
												</div>
											</div>
										</div>
										<div class="form-group">
											<div class="form-group row" id="trBienInmuebleWS">
												<div class="col-2">
														<label for="idRealEstate">Bien Inmueble: </label>
												</div>
												<div class="col-10">
													<select id="idRealEstate" name="idRealEstate" class="custom-select"></select>
													<input type="hidden" id="cIdRealEstate" name="cIdRealEstate" class="form-control"  value="" />
												</div>
											</div>
											<div class="form-group row" id="trDescripcionFONDEN">
												<div class="col-2">
														<label for="descripcionFONDEN">Descripci&oacute;n FONDEN: </label>
												</div>
												<div class="col-10">
													<textarea id="descripcionFONDEN" name="descripcionFONDEN"  rows="2" cols="80" class="form-control"></textarea>
												</div>
											</div>
										</div>
										<div class="form-group">
											<div class="form-group row" >
												<div class="col-2">
														<label for="cLugarRealizaObra">Lugar Donde se Realizar&aacute; la Obra </label>
												</div>
												<div class="col-10">
													<textarea rows="3" class="form-control" id="cLugarRealizaObra" name="cLugarRealizaObra"></textarea>
												</div>
											</div>	
										</div>
										<div class="form-group">
											<div class="row">
												<div class="col-2">
													<label for="cIdAdicionales">Doc. Asignaci&oacute;n: </label>
												</div>
												<div class="col-2">
													<select id="cIdAdicionales" name="cIdAdicionales" class="custom-select">
													</select>
												</div>
												<div class="col-1">
													<label for="noOfAdicionales">No.: </label>
												</div>
												<div class="col-3">
													<input type="text" id="noOfAdicionales" name="noOfAdicionales" size="30" maxlength="40" class="form-control">
												</div>
												<div class="col-2" align="right">
													<label for="fAdicionales">Fecha: </label>
												</div>
												<div class="col-2">
													<div class="input-group date" id="fAdicionales_" data-target-input="nearest">
											          <input type="text" class="form-control datetimepicker-input" data-target="#fAdicionales_" title="Fecha del documento de asignación" id="fAdicionales" name="fAdicionales" />
											          <div class="input-group-append" data-target="#fAdicionales_" data-toggle="datetimepicker" title="Fecha del documento de asignación">
											            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
											          </div>
											        </div>
												</div>
											</div>
										</div>
										<div class="form-group">
											<div class="row">
												<div class="col-2">
														<label for="cNoProcedimientoCNET">No. Proc. COMPRANET: </label>
												</div>
												<div class="col-2">
													<input type="text" id="cNoProcedimientoCNET" name="cNoProcedimientoCNET" size="30" maxlength="30" class="form-control">
												</div>
												<div class="col-2" align="right">
													<label for="nCodContratoCNET">Cod. Contrato COMPRANET: </label>
												</div>
												<div class="col-2">
													<input type="text" id="nCodContratoCNET" name="nCodContratoCNET"  class="form-control">
												</div>
												<div class="col-2" align="right">
													<label for="nCodExpedienteCNET">Cod. Expediente COMPRANET: </label>
												</div>
												<div class="col-2">
													<input type="text" id="nCodExpedienteCNET" name="nCodExpedienteCNET" class="form-control">
												</div>
											</div>
										</div>
										<div class="form-group">
											<div class="row">
												<div class="form-group col-md-auto">
													<span class="input-group-text" id="inputGroupVigencia">Vigencia del Contrato</span>
												</div>
												<div class="form-group col-md-1" align="right">
													<label for="fInicial">Inicio: </label>
												</div>
												<div class="form-group col-md-2">
													<div class="input-group date" id="fInicioCom_" data-target-input="nearest">
											          <input type="text" class="form-control datetimepicker-input" data-target="#fInicioCom_" title="Fecha Inicial del Contrato" id="fInicioCom" name="fInicioCom" onchange="validaPeriodoContrato('fInicioCom')"/>
											          <div class="input-group-append" data-target="#fInicioCom_" data-toggle="datetimepicker" title="Fecha Inicial del Contrato">
											            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
											          </div>
											        </div>
										        </div>
										        <div class="form-group col-md-1" align="right">
													<label for="fFinal">Fin: </label>
												</div>
												<div class="form-group col-md-2">
													<div class="input-group date" id="fFinCom_" data-target-input="nearest">
											          <input type="text" class="form-control datetimepicker-input" data-target="#fFinCom_" title="Fecha Inicial del Contrato" id="fFinCom" name="fFinCom" onchange="validaFechaFinCompromiso();"/>
											          <div class="input-group-append" data-target="#fFinCom_" data-toggle="datetimepicker" title="Fecha Inicial del Contrato">
											            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
											          </div>
											        </div>
										        </div>
										        <div class="form-group col-md-1">
												</div>
												<div class="form-group col-md-2">
													<input type="button" id="btnAmortiza" name="btnAmortiza" value="Amortizaci&oacute;n/Retenci&oacute;n" class="btn btn-outline-secondary"/>
												</div>
											</div>
										</div>
			            			</div>
<!-- 							Fin	Tab compromiso -->
<!-- 							Inicio	Tab Suspensión -->			            			
			            			<div class="tab-pane" id="tabs-3" role="tabpanel">
			            				<div class="form-group">
											<div class="row">
												<div class="form-group col-md-2" >
													<label for="fSuspencion_">Fecha de Suspenci&oacute;n: </label>
												</div>
												<div class="form-group col-md-4">
													<div class="input-group date" id="fSuspencion_" data-target-input="nearest">
											          <input type="text" class="form-control datetimepicker-input" data-target="#fSuspencion_" title="Fecha Suspensión del Contrato" id="fSuspencion" name="fSuspencion" />
											          <div class="input-group-append" data-target="#fSuspencion_" data-toggle="datetimepicker" title="Fecha Suspensión del Contrato">
											            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
											          </div>
											        </div>
										        </div>
											</div>
										    <div class="row">
										        <div class="form-group col-md-2" >
													<label for="#fSuspencion_">Motivo de la Suspenci&oacute;n: </label>
												</div>
												<div class="form-group col-md-4">
													<textarea id="cMotivoSuspencion" name="cMotivoSuspencion" onKeyPress="return LetrasNums(event)" rows="3" class="form-control"></textarea>
												</div>
											</div>
											<div class="row">
										        <div class="form-group col-md-3" align="center">
													<input type="button" value="Aceptar" class="btnInterfaceBG"/>
												</div>
												<div class="form-group col-md-3" align="center">
													<input type="button" value="Cancelar" class="btnInterfaceBG"/>
												</div>
											</div>
										</div>
			            			</div>
<!-- 							Fin	Tab Suspensión -->
<!-- 							Inicio	Tab Convenio -->			            			
			            			<div class="tab-pane" id="tabs-4" role="tabpanel">
			            				<div class="form-group">
											<div class="form-group row">
												 <div class="col-md-2" >
													<label for="cNoConvenio">No Convenio:</label>
												</div>
												<div class="col-md-4">
													<input type="text" id="cNoConvenio" name="cNoConvenio" maxlength="60" size="30" class="form-control convenioModificatorio" onChange="conMayusculas(this)"/>
												</div>
												<div class="col-md-2" >
													<span id="conveniosAnterioresLbl" style="visibility: hidden;">Convenios anteriores</span>
												</div>
												<div class="col-md-2" >
													<input type="text" id="conveniosAnteriores" name="conveniosAnteriores" class="form-control notEditable numerico convenioModificatorio" readonly="readonly" style="visibility: hidden;" />
												</div>
											</div>
											<div class="form-group row">
												<div class="col-md-10" style="font-weight: bold; text-align: center;background-color: #EBEBEB;">
													<label for="ModMonto">Modificaci&oacute;n de Monto</label>
												</div>
											</div>
											<div class="form-group row">
												<div class="col-md-2" >
													<label for="cNoConvenio">IVA:</label>
												</div>
												<div class="col-md-4">
													<input type="text" id="ivaConvF" name="ivaConvF" size="5" readonly="readonly" class="form-control notEditable convenioModificatorio" />
												</div>
												<div class="col-md-2" >
													<span id="mAnticipoAnosAntLbl" style="visibility: hidden;">Anticipo Año Anterior</span>
												</div>
												<div class="col-md-4">
													<input type="hidden" id="mAnticipoAnosAnt" name="mAnticipoAnosAnt" maxlength="20" size="20" class="form-control notEditable numerico convenioModificatorio" readonly="readonly"  />
												</div>
											</div>
											<div class="form-group row">
												<div class="col-md-2" >
													<label for="mIncremento">Incremento (Monto) *</label>
												</div>
												<div class="col-md-4">
													<input type="text" id="mIncremento" name="mIncremento" maxlength="20" size="20" onKeyPress="return onlyNumbers(event)" onblur="onBlurMoney()" class="form-control normal numerico convenioModificatorio" placeholder="$0.00"/>
												</div>
												<div class="col-md-2" >
													<label for="mIncrementoConIVA">Incremento C/IVA:</label>
												</div>
												<div class="col-md-4">
													<input type="text" id="mIncrementoConIVA" maxlength="20" size="20" class="form-control notEditable numerico convenioModificatorio" />
												</div>
											</div>
											<div class="form-group row">
												<div class="col-md-2" >
													<label for="mConv">Monto Modificado:</label>
												</div>
												<div class="col-md-4">
													<input type="text" id="mConv" name="mConv" maxlength="20" size="20"  value="$0.00" class="form-control notEditable numerico convenioModificatorio" readonly="readonly"/>
													<input type="hidden" id="mConvAnt" name="mConvAnt" maxlength="20" size="20"  value="$0.00" class="notEditable numerico convenioModificatorio" readonly="readonly" style="visibility: hidden;"/>
												</div>
												<div class="col-md-2" >
													<label for="mImporteIVAConv">Monto Modificado IVA:</label>
												</div>
												<div class="col-md-4">
													<input type="text" maxlength="20" size="20" name="mImporteIVAConv" id="mImporteIVAConv" value="$0.00" class="form-control notEditable numerico convenioModificatorio" />
													<input type="hidden" maxlength="20" size="20" name="mImporteIVAConvAnt" id="mImporteIVAConvAnt" value="$0.00" class="form-control notEditable numerico convenioModificatorio"  style="visibility: hidden;"/>
												</div>
											</div>
											<div class="form-group row">
												<div class="col-md-2" >
													<label for="mTotalConv">Monto Total:*</label>
												</div>
												<div class="col-md-4">
													<input type="text" maxlength="20" size="20" id="mTotalConv" name="mTotalConv" readonly="readonly" value="$0.00" class="form-control notEditable numerico convenioModificatorio" />
													<input type="text" maxlength="20" size="20" id="mTotalConvconAnt" name="mTotalConvconAnt" readonly="readonly" value="$0.00" class="form-control notEditable numerico convenioModificatorio" style="visibility: hidden;"/>
												</div>
											</div>
											<div class="form-group row">
												<div class="form-group col-md-auto">
													<span class="input-group-text" id="inputGroupVigenciaConv">Vigencia del Contrato</span>
												</div>
												<div class="form-group col-md-1" align="right">
													<label for="fInicioConv_">Inicio: </label>
												</div>
												<div class="form-group col-md-2">
													<div class="input-group date" id="fInicioCom_" data-target-input="nearest">
											          <input type="text" class="form-control datetimepicker-input" data-target="#fInicioConv_"  id="fInicioConv" name="fInicioConv"/>
											          <div class="input-group-append" data-target="#fInicioConv_" data-toggle="datetimepicker" >
											            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
											          </div>
											        </div>
										        </div>
										        <div class="form-group col-md-1" align="right">
													<label for="fFinConv">Fin: </label>
												</div>
												<div class="form-group col-md-2">
													<div class="input-group date" id="fFinConv_" data-target-input="nearest">
											          <input type="text" class="form-control datetimepicker-input" data-target="#fFinConv_"  id="fFinConv" name="fFinConv"/>
											          <div class="input-group-append" data-target="#fFinConv_" data-toggle="datetimepicker" >
											            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
											          </div>
											        </div>
										        </div>
											</div>
											<div class="form-group row">
												<div class="col-md-2" >
													<label for="cMotivoConv">Motivo del Convenio Modificatorio:</label>
												</div>
												<div class="col-md-4">
													<textarea id="cMotivoConv" name="cMotivoConv" onKeyPress="return LetrasNums(event)" rows="4" style="width: 600px;" class="form-control convenioModificatorio"></textarea>
												</div>
											</div>
											<div class="form-group row">
												<div class="col-md-4">
													<span style="font-size: 7pt; font-weight: bold;">* Monto sin IVA</span>
													&nbsp;
													<span style="font-size: 7pt; font-weight: bold;">** Monto total modificado</span>
												</div>
											</div>
										</div>
			            			</div>
<!-- 							Fin	Tab  Convenio-->
<!-- 							inicio	Tab  Estimación-->	
			            			<div class="tab-pane" id="tabs-5" role="tabpanel">
			            				<jsp:include page="ContratoObra/EstimacionObra.jsp"></jsp:include>
										<jsp:include page="firmaElectronica/datosNotaFIELObra.jsp"></jsp:include>
			            			</div>
<!-- 							Fin	Tab  Estimación-->								
<!-- 							inicio	Tab  pago de pasivo-->				            			
			            			<div class="tab-pane" id="tabs-7" role="tabpanel">
			            				<div class="form-group">
											<div class="form-group row">
												<div class="col-md-6" >
													<label for="pagoPasivo">Pago de Pasivos</label>
												</div>
											</div>
											<div class="form-group row">
												<div class="col-md-2" >
													<label for="mPagoPasivo">Monto Pasivo *</label>
												</div>
												<div class="col-md-4" >
													<input type="text" id="mPagoPasivo" name="mPagoPasivo" maxlength="20" size="20" onKeyPress="return onlyNumbers(event)" onblur="onBlurMoney()" class="form-control normal numerico pagoPasivo" value="$0.00"/>
												</div>
												<div class="col-md-2" >
													<label for="ivaPagPasF">IVA:</label>
												</div>
												<div class="col-md-4" >
													<input type="text" id="ivaPagPasF" name="ivaPagPasF" size="5" readonly="readonly" class="form-control notEditable pagoPasivo" />
												</div>
											</div>
											<div class="form-group row">
												<div class="col-md-2" >
													<label for="mTotalPagoPasivo">Monto Total: **</label>
												</div>
												<div class="col-md-4" >
													<input type="text" maxlength="20" size="20" id="mTotalPagoPasivo" name="mTotalPagoPasivo" readonly="readonly" value="$0.00" class="form-control notEditable numerico pagoPasivo" />
												</div>
											</div>
											<div class="form-group row">
												<div class="col-md-2" >
													<label for="mTotalPagoPasivo">Motivo del Pago de Pasivo:</label>
												</div>
												<div class="col-md-4" >
													<textarea id="cMotivoPagoPasivo" name="cMotivoPagoPasivo" onKeyPress="return LetrasNums(event)" rows="4" style="width: 600px;" class="form-control pagoPasivo"></textarea>
												</div>
											</div>
											<div class="form-group row">
												<div class="col-md-4">
													<span style="font-size: 7pt; font-weight: bold;">* Monto sin IVA</span>
													&nbsp;
													<span style="font-size: 7pt; font-weight: bold;">** Monto total modificado</span>
												</div>
											</div>
										</div>
			            			</div>
<!-- 							Fin	Tab Pasivos  -->
<!-- 							Inicio	Tab Plurianuales  -->			            			
			            			<div class="tab-pane" id="tabs-8" role="tabpanel">
			            				<div class="form-group">
											<div class="form-group row">
												<div class="col-md-2" >
													<label for="mPlurianual">Monto Plurianual: *</label>
												</div>
												<div class="col-md-3" >
													<input type="text" id="mPlurianual" name="mPlurianual" maxlength="20" size="20" onKeyPress="return onlyNumbers(event)" onblur="onBlurMoney()" class="form-control normal numerico plurianual" value="$0.00"/>
												</div>
												<div class="col-md-2" >
													<label for="ivaPlurianual">IVA:</label>
												</div>
												<div class="col-md-3" >
													<select  name="ivaPlurianual" id="ivaPlurianual" class="custom-select">
													</select>
												</div>
												<div class="col-md-2" >
													<label for="mImporteIVAPlurianual">Monto IVA Plurianual:</label>
												</div>
												<div class="col-md-3" >
													<input type="text" maxlength="12" size="12" name="mImporteIVAPlurianual" id="mImporteIVAPlurianual" value="0" onKeyPress="return onlyNumbers(event)" class="form-control notEditable numerico" />
												</div>
											</div>
											<div class="form-group row">
												<div class="col-md-2" >
													<label for="mTotalPlurianual">Monto Total: **</label>
												</div>
												<div class="col-md-4" >
													<input type="text" maxlength="20" size="20" id="mTotalPlurianual" name="mTotalPlurianual" readonly="readonly" value="$0.00" class="form-control notEditable numerico plurianual" />
												</div>
											</div>
											<div class="form-group row">
												<div class="col-md-4">
													<span style="font-size: 7pt; font-weight: bold;">* Monto sin IVA</span>
													&nbsp;
													<span style="font-size: 7pt; font-weight: bold;">** Monto total modificado</span>
												</div>
											</div>
										</div>
			            			</div>
<!-- 							Fin	Tab  Plurianuales-->			            			
			            			<div class="tab-pane" id="tabs-9" role="tabpanel">
			            				<div class="form-group">
											<div class="form-group row">
												<div class="col-md-2" >
													<label for="NoCntActualiza">No. Contrato:</label>
												</div>
												<div class="col-md-3" >
													<input id="NoCntActualiza" name="NoCntActualiza" type="text" size="40" maxlength="40" readonly="readonly" class="form-control"/>
												</div>
												<div class="col-md-2" >
													<label for="cOficioHacienda">Autorizacion de Plurianualidad (Oficio de Hacienda):</label>
												</div>
												<div class="col-md-3" >
													<input id="cOficioHacienda" name="cOficioHacienda" type="text" size="60" maxlength="60" class="form-control" />
												</div>
											</div>
											<div class="form-group row">
												<div class="col-md-2" >
													<label for="cSituacionJuridica">Situación Jurídica del Inmueble en el que se esta realizando la Obra:</label>
												</div>
												<div class="col-md-3" >
													<textarea  id="cSituacionJuridica" name="cSituacionJuridica" rows="3" cols="60" class="form-control"></textarea>
												</div>
												<div class="col-md-2" >
													<label for="cTipoObraPub">Tipo de Obra Publica:</label>
												</div>
												<div class="col-md-3" >
													<input id="cTipoObraPub" name="cTipoObraPub" type="text" size="60" maxlength="60" class="form-control" />
												</div>
											</div>
											<div class="form-group row">
												<div class="col-md-2" >
													<label for="cOficioFiniquito">Documento que Acredite la Terminación de la Obra (Oficio de Finiquito):</label>
												</div>
												<div class="col-md-3" >
													<input id="cOficioFiniquito" name="cOficioFiniquito" type="text" size="60" maxlength="60" class="form-control" />
												</div>
											</div>
											<div class="form-group row">
												<div class="col-md-10" align="center" >
													<input type="button" id="btnActualizaDatos"  onclick="actualizaDatosOP();" class="btnInterfaceBG" value= "Actualizar" />
												</div>
											</div>
										</div>
			            			</div>
			            		</div>
			            	</div>
						</div>
					</div>
<!-- 			Fin de la Sección de tabs -->
					<div class="form-group" id="dv">
			 			<div class="form-group row">
							<div class="col-md-2" >
								<label for="epSel">Estructura Program&aacute;tica:</label>
							</div>
							<div class="col-md-7" >
								<select id="epSel" name="epSel"  class="custom-select"></select>
							</div>
							<div class="col-md-1" >
								<input type="button" value="Agregar" name="AgregarEP" id="AgregarEP" onclick="fnClickAddRowC();" class="btn btn-outline-secondary btn-sm"/> 
							</div>
							<div class="col-md-2" style="display: none;">
								<label id="lbl_radicado"> Presupuesto Radicado:</label>
								<input type="checkbox" id="chk_radicado" name="chk_radicado" value="N" >
							</div>
						</div>
			 		</div>
			 		<div class="form-group" id="ddt_clavepresup">
			 			<div class="row">
							<div class="col">
								<table id="dt_clavepresup" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
									<thead >
										<tr> 
						        		    <th id="epGrid">No.</th>
						        			<th>Estructura Program&aacute;tica</th>
						        			<th></th>
											<th>Enero</th>
											<th></th>
											<th></th>
											<th>Febrero</th>
											<th></th>
											<th></th>
											<th>Marzo
											</th>
											<th></th>
											<th></th>
											<th>Abril</th>
											<th></th>
											<th></th>
											<th>Mayo</th>
											<th></th>
											<th></th>
											<th>Junio</th>
											<th></th>
											<th></th>
											<th>Julio</th>
											<th></th>
											<th></th>
											<th>Agosto</th>
											<th></th>
											<th></th>
											<th>Septiembre</th>
											<th></th>
											<th></th>
											<th>Octubre</th>
											<th></th>
											<th></th>
											<th>Noviembre</th>
											<th></th>
											<th></th>
											<th>Diciembre</th>
											<th></th>
											<th>Anual</th>
											<th>Modificar</th>
											<th>Borrar</th>
						        		</tr>										
									</thead>
								</table>
							</div>
						</div>
			 		</div>
			 		<div class="form-group" id="ddt_carteraOli">
			 			<div class="row">
							<div class="col">
								<table id="dt_carteraOli" style="display: none;" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
									<thead >
										<tr> 
						        		    <th>Cartera.</th>
						        			<th>Oli.</th>
						        		</tr>										
									</thead>
								</table>
							</div>
						</div>
			 		</div>
			 		<div class="form-group" id="ddt_clavePasivo">
			 			<div class="row">
							<div class="col">
								<table id="dt_clavePasivo" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
									<thead >
										<tr> 
						        		   <th id="epGrid">No.</th>
											<th>Estructura Program&aacute;tica</th>
											<th></th>
											<th>Enero</th>
											<th></th>
											<th></th>
											<th>Febrero</th>
											<th></th>
											<th></th>
											<th>Marzo</th>
											<th></th>
											<th></th>
											<th>Abril</th>
											<th></th>
											<th></th>
											<th>Mayo</th>
											<th></th>
											<th></th>
											<th>Junio</th>
											<th></th>
											<th></th>
											<th>Julio</th>
											<th></th>
											<th></th>
											<th>Agosto</th>
											<th></th>
											<th></th>
											<th>Septiembre</th>
											<th></th>
											<th></th>
											<th>Octubre</th>
											<th></th>
											<th></th>
											<th>Noviembre</th>
											<th></th>
											<th></th>
											<th>Diciembre</th>
											<th></th>
											<th>Anual</th>
											<th>Modificar</th>
						        		</tr>										
									</thead>
								</table>
							</div>
						</div>
			 		</div>
			 		<div class="form-group" id="ddt_clavePlurianual" style="display: none;">
			 			<div class="row">
							<div class="col">
								<table id="dt_clavePlurianual" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
									<thead >
										<tr> 
						        		   <th id="epGrid">No.</th>
											<th>Estructura Program&aacute;tica</th>
											<th></th>
											<th>Enero</th>
											<th></th>
											<th></th>
											<th>Febrero</th>
											<th></th>
											<th></th>
											<th>Marzo</th>
											<th></th>
											<th></th>
											<th>Abril</th>
											<th></th>
											<th></th>
											<th>Mayo</th>
											<th></th>
											<th></th>
											<th>Junio</th>
											<th></th>
											<th></th>
											<th>Julio</th>
											<th></th>
											<th></th>
											<th>Agosto</th>
											<th></th>
											<th></th>
											<th>Septiembre</th>
											<th></th>
											<th></th>
											<th>Octubre</th>
											<th></th>
											<th></th>
											<th>Noviembre</th>
											<th></th>
											<th></th>
											<th>Diciembre</th>
											<th></th>
											<th>Anual</th>
											<th>Modificar</th>
						        		</tr>										
									</thead>
								</table>
							</div>
						</div>
			 		</div>
			 		<div class="form-group" id="ddt_claveConvenioModificatorio" >
			 			<div class="row">
							<div class="col">
								<table id="dt_claveConvenioModificatorio" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
									<thead >
										<tr> 
						        		   <th id="epGrid">No.</th>
											<th>Estructura Program&aacute;tica</th>
											<th></th>
											<th>Enero</th>
											<th></th>
											<th></th>
											<th>Febrero</th>
											<th></th>
											<th></th>
											<th>Marzo</th>
											<th></th>
											<th></th>
											<th>Abril</th>
											<th></th>
											<th></th>
											<th>Mayo</th>
											<th></th>
											<th></th>
											<th>Junio</th>
											<th></th>
											<th></th>
											<th>Julio</th>
											<th></th>
											<th></th>
											<th>Agosto</th>
											<th></th>
											<th></th>
											<th>Septiembre</th>
											<th></th>
											<th></th>
											<th>Octubre</th>
											<th></th>
											<th></th>
											<th>Noviembre</th>
											<th></th>
											<th></th>
											<th>Diciembre</th>
											<th></th>
											<th>Anual</th>
											<th>Modificar</th>
											<th>Eliminar</th>
						        		</tr>										
									</thead>
								</table>
							</div>
						</div>
			 		</div>
			 		<div class="form-group row">
						<div class="col-md-12" align="right">
							<input title="Seleccionar el botón Guardar en el caso que se haya terminado la Captura del Contrato"id="btnGuarda" type="button" value="Guardar" onclick="saveByCRUD()" 
							class="btn btn-outline-secondary"/>
							<input title="Si ya tiene la seguridad de que la captura y la Estructura Programática (EP) son correctas, seleccionar el botón Aplicar, a partir de esto, el Presupuesto pasará al Apartado" id="btnAplica" type="button" value="Aplicar"
									onclick="validCampos()" class="btn btn-outline-secondary"/>
							<input id="btnCancela" type="button" value="Cancelar" onclick="cancelContract()" class="btn btn-outline-secondary"/>
						</div>
					</div>
				</fieldset>
			</div><!-- 			Fin del container -->
			<div id="divModals" style="display: none;">			
<!--           Modal Amortización-->
				<div class="modal fade" id="dialog-DocAmortizacion" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
				        		<h5 class="modal-title" id="modalLabelAmortizacion">Amortizaci&oacute;n</h5>
				        		<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				      		</div>
				      		<div class="modal-body">
						      	<fieldset class="form-group border p-3">
						      		<div class="form-group">
										<div class="row" >
											<div class="col-12">
										  		<label for="porcRetencion" >%Amortizaci&oacute;n:</label>
										  		<input type="text" class="form-control"  name="porcRetencion" id="porcRetencion" onkeypress="return onlyNumbers(event)"
													onblur="validaPorcRetencion()" readonly/>
										  	</div>
										</div>
									</div>
									<div class="form-group">
										<div class="row" >
											<div class="col-4">
										  		<label for="porcAnticipoShow" >%Anticipo:</label>
										  		<input type="text" class="form-control"  name="porcAnticipoShow" id="porcAnticipoShow" readonly/>
										  	</div>
										  	<div class="col-4">
										  		<label for="impBrutoShow" >Importe Bruto:</label>
										  		<input type="text" class="form-control"  name="impBrutoShow" id="impBrutoShow" readonly/>
										  	</div>
										  	<div class="col-4">
										  		<label for="ivaAnticipoShow" >IVA Ant&iacute;cipo:</label>
										  		<input type="text" class="form-control"  name="ivaAnticipoShow" id="ivaAnticipoShow" readonly/>
										  	</div>
										</div>
									</div>
									<div class="form-group">
										<div class="row" >
											<div class="col-12">
										  		<label for="totalAnticipoShow" >Total Ant&iacute;cipo:</label>
										  		<input type="text" class="form-control"  name="totalAnticipoShow" id="totalAnticipoShow"  readonly/>
										  	</div>
										</div>
									</div>
									<div class="form-group">
										<fieldset class="form-group border p-3">
											<legend class="w-auto px-2">Retenciones</legend>
											<div class="form-group">
												<div class="row" >
													<div class="col-8">
												  		<label for="txtNombreProveerdor" >Retencion:</label>
												  		<select id="cIdTipoRetencion" name="cIdTipoRetencion" class="custom-select" onchange="valida2PC()"></select>
												  	</div>
												  	<div class="col-4" align="left">
												  		<br />
												  		<input type="button" value="Agregar" id="addBtn" name="addBtn" class="btn btn-outline-secondary btn-sm"/>
												  	</div>
												</div>
											</div>
											<div class="form-group">
												<div class="row" >
													<div class="col-auto">
										  				<div class="form-check  form-check-inline">
														  <input class="form-check-input" type="radio" name="grpMilla2" id="IMDT"  value="0" checked="checked">
														  <label class="form-check-label" for="IMDT">
														    IMDT
														  </label>
														  
														</div>
														<div class="form-check  form-check-inline">
														  <input class="form-check-input" type="radio" name="grpMilla2" id="CNIC"  value="1">
														  <label class="form-check-label" for="CNIC">
														    CNIC
														  </label>
														</div>
													</div>
												</div>
											</div>
											<div class="form-group row" >
												<div class="col">
													<h1 style="font-size: 10px; font-style: italic; text-align: right;"> *Doble click en una fila para eliminar</h1>
												</div>
												<div class="col">
													<table id="dt_retencion" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
														<thead >
															<tr>
																<th>Clave de retenci&oacute;n</th>
																<th>Tipo de retenci&oacute;n</th>
															</tr>										
														</thead>
													</table>
													
												</div>
											</div>
										</fieldset>
									</div>
				      			</fieldset>
				      		</div>
				      		<div class="modal-footer">
				        		<button type="button" class="btn btn-primary" id="btnAcepDocAmortizacion" name="btnAcepDocAmortizacion" onclick="saveDocAmortizacion()">Guardar</button>
				        		<button type="button" class="btn btn-secondary"  id="btnCancelDocAmortizacion" name="btnCancelDocAmortizacion" onclick="cancelDocAmortizacion()">Cancelar</button>
				      		</div>
				    	</div>
					</div>
				</div>
				<!-- Fin del Modal Amortización-->
				<!-- Modal Autorización de porcentaje-->
				<div class="modal fade" id="dialog-form-DocAutorizacion" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
			  		<div class="modal-dialog">
			    		<div class="modal-content">
			      			<div class="modal-header">
			        			<h5 class="modal-title" id="modalDocAutorizacion">Autorizaci&oacute;n de Porcentaje</h5>
			        			<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			      			</div>
							<div class="modal-body">
					        	<div class="form-group" id="divAutorizacion">
									<div class="row" >
										<div class="col-auto">
									  		<label for="" >Ingrese el N&uacute;mero de Autorizaci&oacute;n:</label>
									  		<input type="text" id="noOfAutorizacion" name="noOfAutorizacion" class="form-contol apart" maxlength="31" size="31"/>
									  	</div>
									</div>
								</div>
					      	</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-primary" id="btnDocAutorizacion" name="btnDocAutorizacion" onclick="sendDocAutorizacion()">Enviar</button>
				        		<button type="button" class="btn btn-secondary" id="btnCancelDocAutorizacion" name="btnCancelDocAutorizacion" onclick="cancelaDocAutorizacion()">Cancelar</button>
							</div>
			    		</div>
			  		</div>
				</div>
				<!-- Fin del Modal Amortización-->
				<!-- Modal contratos pluris-->
				<div class="modal fade" id="dialogMA" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<h5 class="modal-title" id="modalContPluri">Informaci&oacute;n Contrato Plurianual</h5>
<!-- 								<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button> -->
							</div>
							<div class="modal-body">
								<div class="form-group" >
									<div class="row" >
										<div class="col-auto">
											<label for="totalMontoMultiAnual" >Monto Total del contrato S/IVA:</label>
								  			<input type="text" name="totalMontoMultiAnual" id="totalMontoMultiAnual" size="12" maxlength="20" class="form-control" placeholder="$0.00" aria-label="0" 
								  				aria-describedby="basic-addon1"  onblur="setMontosMultiAnual()"/>
										</div>
										<div class="col-auto">
											<label for="totalMultiAnual" >Total de a&ntilde;os del contrato:</label>
								  			<input type="text" name="totalMultiAnual" id="totalMultiAnual" class="form-control numerico" placeholder="0" aria-label="0" value=""
								  				aria-describedby="basic-addon1"  size="3" maxlength="2"/>
										</div>
									</div>
								</div>
								<div class="form-group row" >
									<div class="col">
										<table id="multiAnualTbl" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
											<thead >
												<tr>
													<th>A&ntilde;o</th>
													<th>Monto</th>
												</tr>										
											</thead>
										</table>
									</div>
								</div>
								<div class="form-group" >
									<div class="row" >
										<div class="col-auto">
											<h5 style="font-size: 10px; font-weight: bold;">NOTA: El a&ntilde;o actual ya esta considerado</h5>
										</div>
									</div>
								</div>
								<div class="form-group" >
									<div class="row" >
										<div class="form-group col-6">
											<label for="DcAprobacionPLU" >Oficio de Autorizaci&oacute;n Plurianual:</label>
								  			<input type="text" name="DcAprobacionPLU" id="DcAprobacionPLU" class="form-control" placeholder="Capturar Oficio de Autorizaci&oacute;n Plurianual" aria-describedby="basic-addon1"   onblur="setcAprobacionPLU()"/>
										</div>
									</div>
								</div>
							</div>
							<div class="modal-footer">
<!-- 								<button type="button" class="btn btn-primary" id="btnAcepModalMA" name="btnAcepModalMA" onclick="guardaMA();">Guardar</button> -->
								<button type="button" class="btn btn-secondary"  id="btnCancelModalMA" name="btnCancelModalMA" onclick="closedMA()">Cerrar</button>
							</div>
						</div>
					</div>
				</div>
				<!-- Fin Modal contratos pluris-->
				<!-- Modal Aplicación contable-->
				<div class="modal fade" id="dialog-form-apcon" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<h5 class="modal-title" id="modalApCont">Aplicaci&oacute;n Presupuestal/Contable</h5>
							</div>
							<div class="modal-body">
								<div class="form-group" id="divAplica">
									<iframe id="ifAplica" src="about:blank"></iframe>
								</div>
							</div>
							<div class="modal-footer">
								
							</div>
						</div>
					</div>
				</div>
				<!-- Fin Modal Aplicación contable-->
				<!-- Modal para captura de montos en las eps-->
				<div class="modal fade" id="dialog-form-ep" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<h5 class="modal-title" id="modalEP">Agregar Estructura Program&aacute;tica</h5>
								<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
							</div>
							<div class="modal-body">
								<fieldset class="form-group border p-3">
									<div class="form-group" >
										<div class="row" >
											<div class="col">
												<label for="epDisp" >EP:</label>
									  			<input type="text" name="epDisp" id="epDisp" class="form-control" aria-describedby="basic-addon1"  />
											</div>
										</div>
									</div>
									<div class="form-group row" >
										<div class="col">
											<table id="capturaMontosTbl" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
												<thead >
													<tr>
														<th>Mes</th>
														<th>Disponible</th>
														<th>Monto</th>
													</tr>										
												</thead>
												<tbody>
													<tr>
														<td><label for="eneroDisp">Enero</label></td>
														<td><input type="text" id="eneroDisp" name="eneroDisp" value="" class="form-control monto"size="15" /></td>
														<td>
															<input type="text" id="eneroApart" name="eneroApart" class="form-control montoCaptura" size="15" />
															<input type="hidden" id="eneroHide" name="eneroHide" value="" size="15" />
														</td>
													</tr>
													<tr>
														<td><label for="febreroDisp">Febrero</label></td>
														<td><input type="text" id="febreroDisp" name="febreroDisp" value="" class="form-control monto" size="15" /></td>
														<td>
															<input type="text" id="febreroApart" name="febreroApart" value="" class="form-control montoCaptura" size="15" />
															<input type="hidden" id="febreroHide" name="febreroHide" value="" size="15" />
														</td>
													</tr>
													<tr>
														<td><label for="marzoDisp">Marzo</label></td>
														<td><input type="text" id="marzoDisp" name="marzoDisp" value="" class="form-control monto" size="15" /></td>
														<td>
															<input type="text" id="marzoApart" name="marzoApart" value="" class="form-control montoCaptura" size="15" />
															<input type="hidden" id="marzoHide" name="marzoHide" value="" size="15" />
														</td>
													</tr>
													<tr>
														<td><label for="abrilDisp">Abril</label></td>
														<td><input type="text" id="abrilDisp" name="abrilDisp" value="" class="form-control monto" size="15" /></td>
														<td>
															<input type="text" id="abrilApart" name="abrilApart" value="" class="form-control montoCaptura" size="15" />
															<input type="hidden" id="abrilHide" name="abrilHide" value="" size="15" />
														</td>
													</tr>
													<tr>
														<td><label for="mayoDisp">Mayo</label></td>
														<td><input type="text" id="mayoDisp" name="mayoDisp" value="" class="form-control monto" size="15" /></td>
														<td>
															<input type="text" id="mayoApart" name="mayoApart" value="" class="form-control montoCaptura" size="15" />
															<input type="hidden" id="mayoHide" name="mayoHide" value="" size="15" />
														</td>
													</tr>
													<tr>
														<td><label for="junioDisp">Junio</label></td>
														<td><input type="text" id="junioDisp" name="junioDisp" value="" class="form-control monto" size="15" /></td>
														<td>
															<input type="text" id="junioApart" name="junioApart" value="" class="form-control montoCaptura" size="15" />
															<input type="hidden" id="junioHide" name="junioHide" value="" size="15" />
														</td>
													</tr>
													<tr>
														<td><label for="julioDisp">Julio</label></td>
														<td><input type="text" id="julioDisp" name="julioDisp" value="" class="form-control monto" size="15" /></td>
														<td>
															<input type="text" id="julioApart" name="julioApart" value="" class="form-control montoCaptura" size="15" />
															<input type="hidden" id="julioHide" name="julioHide" value="" size="15" />
														</td>
													</tr>
													<tr>
														<td><label for="agostoDisp">Agosto</label></td>
														<td><input type="text" id="agostoDisp" name="agostoDisp" value="" class="form-control monto" size="15" /></td>
														<td>
															<input type="text" id="agostoApart" name="agostoApart" value="" class="form-control montoCaptura" size="15" />
															<input type="hidden" id="agostoHide" name="agostoHide" value="" size="15" />
														</td>
													</tr>
													<tr>
														<td><label for="septiembreDisp">Septiembre</label></td>
														<td><input type="text" id="septiembreDisp" name="septiembreDisp" value="" class="form-control monto" size="15" /></td>
														<td>
															<input type="text" id="septiembreApart" name="septiembreApart" value="" class="form-control montoCaptura" size="15" />
															<input type="hidden" id="septiembreHide" name="septiembreHide" value="" size="15" />
														</td>
													</tr>
													<tr>
														<td><label for="octubreDisp">Octubre</label></td>
														<td><input type="text" id="octubreDisp" name="octubreDisp" value="" class="form-control monto" size="15" /></td>
														<td>
															<input type="text" id="octubreApart" name="octubreApart" value="" class="form-control montoCaptura" size="15"/>
															<input type="hidden" id="octubreHide" name="octubreHide" value="" size="15" />
														</td>
													</tr>
													<tr>
														<td><label for="noviembreDisp">Noviembre</label></td>
														<td><input type="text" id="noviembreDisp" name="noviembreDisp" value="" class="form-control monto" size="15" /></td>
														<td>
															<input type="text" id="noviembreApart" name="noviembreApart" value="" class="form-control montoCaptura" size="15" />
															<input type="hidden" id="noviembreHide" name="noviembreHide" value="" size="15" />
														</td>
													</tr>
													<tr>
														<td><label for="diciembreDisp">Diciembre</label></td>
														<td><input type="text" id="diciembreDisp" name="diciembreDisp" value="" class="form-control monto" size="15" /></td>
														<td>
															<input type="text" id="diciembreApart" name="diciembreApart" value="" class="form-control montoCaptura" size="15" />
															<input type="hidden" id="diciembreHide" name="diciembreHide" value="" size="15" />
														</td>
													</tr>
													<tr>
														<td>&nbsp;</td>
														<td align="right"><label for="">Total Calendarizado:</label></td>
														<td>
															<input type="text" id="totalCalendarizado" name="totalCalendarizado" readonly="readonly" value="" class="form-control numerico notEditable" size="15" />
														</td>
													</tr>
												</tbody>
											</table>
										</div>
									</div>
								</fieldset>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-primary" id="btnAcepModalConfirm" name="btnAcepModalConfirm" onclick="addEP()">Agregar</button>
								<button type="button" class="btn btn-secondary" id="btnCancelModalConfirm" name="btnModalConfirm" onclick="cancelEP()">Cancelar</button>
							</div>
						</div>
					</div>
				</div>
				<!-- Fin Modal para captura de montos en las eps-->
				<!-- Modal De contratos plurianuales-->
				<div class="modal fade bd-example-modal-lg" id="DialogPluObr" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
			  		<div class="modal-dialog modal-lg">
			    		<div class="modal-content">
			      			<div class="modal-header">
			        			<h5 class="modal-title" id="modalPluObr">Contratos Registrados en el Ejercicio Anterior como Plurianuales </h5>
			        			<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			      			</div>
							<div class="modal-body">
					        	<div class="form-group row" id="dtSolDivR">
									<div class="col">
										<label style="font-size: 12px; font-weight: bold;"> Dar doble clic sobre Contrato que desea agregar</label>
										<table id="tblPluObra" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
											<thead >
												<tr>
													<th>Contrato</th>
													<th>Descripcion</th>
													<th>RFC</th>
													<th>Proveedor</th>
													<th>Importe</th>
													<th>IVA</th>
													<th>MontoTotal</th>
												</tr>									
											</thead>
										</table>
									</div>
								</div>
					      	</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="btnCancelDialogPluObr" name="btnCancelDialogPluObr">Cancelar</button>
							</div>
			    		</div>
			  		</div>
				</div>
	<!-- Fin del Modal para contratos plurianuales-->
				<div class="modal fade" id="dlgQuestionnaire" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" title="Cuestionario de Contratacion">
			  		<div class="modal-dialog">
			    		<div class="modal-content">
			    			<div class="modal-header">
			        			<h5 class="modal-title" id="modalPluObr">Cuestionario de Contratacion </h5>
			        			<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			      			</div>
							<div class="modal-body">
								<iframe class="embed-responsive-item" id="questionnaire" src="about:blank" height="360" width="800"> </iframe>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="btnCancelQuestionnaire" name="btnCancelQuestionnaire">Cerrar</button>
							</div>
			    		</div>
			    	</div>
			    </div>
				
<!-- 			Modal de catalogo proveedores -->
				<div class="modal fade bd-example-modal-lg" id="modalProveedores" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
				  <div class="modal-dialog modal-lg">
				    <div class="modal-content">
				      <div class="modal-header">
				        <h5 class="modal-title" id="modalCatProveedores" style="color: #1A69A9;">Cat&aacute;logo de Proveedores</h5>
				        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				      </div>
				      <div class="modal-body">
				        	<div class="form-group row" >
			        			<div class="col-6 col-sm-6">
  									<label for="cIdDefinitivo" class="form-label">RFC</label>
									<input type="text" class="form-control" id="cIdRFCCat" name="cIdRFCCat" placeholder="AAAA-000000-A01 ó AAAA000000A01">
			        			</div>
								<div class="col-6 col-sm-6">
  									<label for="cIdDefinitivo" class="form-label">Raz&oacute;n Social</label>
									<input type="text" class="form-control" id="cRazonSocialCat" name="cRazonSocialCat" placeholder="Raz&oacute;n Social">
			        			</div>
				        	</div>
				        	<div class="form-group row" >
				        		<div class="col-3 col-sm-4">
  										<input type="button" id="btnSearchProv" value="Buscar" class="btn btn-primary" onclick="queryCatProveedores();"/>
			        			</div>
				        	</div>
				        	<div class="form-group">
				        		<div class="row">
				        			<div class="col">
				        				<h6 style="color: #1A69A9;" >** Dar doble click a un registro de la tabla para seleccionar el proveedor **</h6>
				        			</div>
				        		</div>
								<div class="row">
									<div class="col">
										<table id="tblConsultaProvedores" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
											<thead >
												<tr>
													<th align="center">RFC</th>
													<th align="center">Raz&oacute;n Social</th>
												</tr>										
											</thead>
										</table>
									</div>
								</div>
							</div>
				      </div>
				    </div>
				  </div>
				</div>
		  	</div>
<!-- 			Hiddens -->
			<input type="hidden" id="esCapitalizable" name="esCapitalizable" value="false" />
			<input type="hidden" id="ultimaEstimacion" name="ultimaEstimacion" value="false" />
			<input type="hidden" id="PartidasSinOLI" name="PartidasSinOLI" value="0" />
			<input type="hidden" id="tipoUsuario" value="<%=tipoUsuario%>" size="20" />
			<input type="hidden" id="cDocumento" value="CONTRATOOBRA" name="cDocumento" size="20" />
			<input type="hidden" id="idCaso" value="<%=idCaso %>" name="idCaso" size="20" />
			<input type="hidden" id="numPaginas" value="0" name="numPaginas" size="20" />
			<input type="hidden" id="fHoy" value="" name="fHoy" size="20" />
			<input type="hidden" id="ultDia" value="" name="ultDia" size="20" />
			<input type="hidden" id="primerDiaAnio" value="" name="primerDiaAnio" size="20" />
			<input type="hidden" id="cEsRadicado" name="cEsRadicado" value="N">
			<input type="hidden" id="totalPlurianuales" name="totalPlurianuales">
			<input type="hidden" id="reten" value="" size="20" />
			<input type="hidden" id="nPorcAmortizacion" value="" />
			<input type="hidden" id="acumuladoEstimado" value="" />
			<input type="hidden" id="totalAnticipo" value="" />		
			<input type="hidden" id="vcIdContrato" name="vcIdContrato">
			<input type="hidden" id="vmImporte" name="vmImporte">
			<input type="hidden" id="vmIVA" name="vmIVA">
			<input type="hidden" id="vmTotal" name="vmTotal">
			<input type="hidden" id="vcIdRFC" name="vcIdRFC">
			<input type="hidden" id="vcBeneficiario" name="vcBeneficiario">
			<input type="hidden" id="vcU_UE" name="vcU_UE">
			<input type="hidden" id="vID_AREA" name="vID_AREA">
			<input type="hidden" id="vcTipoObra" name="vcTipoObra">
			<input type="hidden" id="vcTipoRecurso" name="vcTipoRecurso">
			<input type="hidden" id="vnMonto" name="vnMonto">
			<input type="hidden" id="vnMontoConIVA" name="vnMontoConIVA">
			<input type="hidden" id="vcDescripcion" name="vcDescripcion">
			<input type="hidden" id="vcTipoAdjudica" name="vcTipoAdjudica">
			<input type="hidden" id="vcArticulo" name="vcArticulo">
			<input type="hidden" id="vcConvocatoria" name="vcConvocatoria">
			<input type="hidden" id="vfFechaConvoca" name="vfFechaConvoca">
			<input type="hidden" id="vfFechaJuntAcla" name="vfFechaJuntAcla">
			<input type="hidden" id="vfFechaReceProp" name="vfFechaReceProp">
			<input type="hidden" id="vfFechaFallo" name="vfFechaFallo">
			<input type="hidden" id="viTieneAnticipo" name="viTieneAnticipo">
			<input type="hidden" id="vnPorceAnticipo" name="vnPorceAnticipo">
			<input type="hidden" id="vcTipoContrato" name="vcTipoContrato">
			<input type="hidden" id="vcTipoAdicional" name="vcTipoAdicional">
			<input type="hidden" id="vcOficio" name="vcOficio">
			<input type="hidden" id="vfFechaOficio" name="vfFechaOficio">
			<input type="hidden" id="viEsPluriAnual" name="viEsPluriAnual">
			<input type="hidden" id="vmImporteAnticipo" name="vmImporteAnticipo">
			<input type="hidden" id="vTotalAnticipo" name="vTotalAnticipo">
			<input type="hidden" id="vnoFianza" name="vnoFianza">
			<input type="hidden" id="vnoFianzaVO" name="vnoFianzaVO">
			<input type="hidden" id="vnoFianzaAnt" name="vnoFianzaAnt">
			<input type="hidden" id="vPagadoEjerAnte" name="vPagadoEjerAnte">
			<input type="hidden" id="vPagadoIVA" name="vPagadoIVA">
			<input type="hidden" id="vPagadoEjerAntecIVA" name="vPagadoEjerAntecIVA">
			<input type="hidden" id="existeApartado" name="existeApartado">
			<input type="hidden" id="esPlurianual" name="esPlurianual">
			<input type="hidden" id="vfFechaIniContr" name="vfFechaIniContr">
			<input type="hidden" id="vfFechaFinContr" name="vfFechaFinContr">
			<input type="hidden" id="vcIdEntidadFederativa" name="vcIdEntidadFederativa">
			<input type="hidden" id="cAprobacionPLU" name="cAprobacionPLU" value="">
			<input type="hidden" id="vmEstimacion" name="vmEstimacion">
			<input type="hidden" id="vmEstimacionAmortizado" name="vmEstimacionAmortizado">
			<input type="hidden" id="cEjercicioTbl" name ="cEjercicioTbl"  />
			<input type="hidden" id="cContratoConvMod" name ="cContratoConvMod"  />
			<input type="hidden" id="esEditado" value="0" name="esEditado" />
			<input type="hidden" id="existeAmortizacion" name ="existeAmortizacion"  />
			<input type="hidden" id="esFonden" name ="esFonden" value="0" />
			
			<input type="hidden" id="abrImporte" value="" />
			<input type="hidden" id="agoImporte" value="" />
			<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>" />
			<input type="hidden" id="cCveConveModif" name="cCveConveModif" value="" />
			<input type="hidden" id="cCveContratoC" name="cCveContratoC" />
			<input type="hidden" id="cEjercicio" name="cEjercicio" value="" />
			<input type="hidden" id="cEvento" name="cEvento" value="" />
			<input type="hidden" id="cIdTipoRetencionVal" name="cIdTipoRetencionVal" value="" />
			<input type="hidden" id="cIdConcurso" name="cIdConcurso" value="" />
			<input type="hidden" id="cIdConvocatoriaSend" name="cIdConvocatoriaSend" value="" />
			<input type="hidden" id="cIdTipoAdjudicaSend" name="cIdTipoAdjudicaSend" value="" />
			<input type="hidden" id="cIdFundamentoLegalSend" name="cIdFundamentoLegalSend" value="" />
			<input type="hidden" id="cMes" name="cMes" value="0" />
			<input type="hidden" id="cmesFind" name="cmesFind" />
			<input type="hidden" id="cOLIc" name="cOLIc" value="" />
			<input type="hidden" id="cOLIp" name="cOLIp" value="" />
			<input type="hidden" id="cRamo" name="cRamo" value="16" />
			<input type="hidden" id="cU_UR" name="cU_UR" value="<%=uUR%>" />
			<input type="hidden" id="cU_CC" name="cU_CC" value="<%=cCentroContable%>" />
			<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=usuario.getU_UR()%>" />
			<input type="hidden" id="cUnidadResponsableContable" name="cUnidadResponsableContable" value="RHQ" />
			<input type="hidden" id="docAplicadoConv" name="docAplicadoConv" />
			<input type="hidden" id="docRenglon" name="docRenglon" />
			<input type="hidden" id="eneImporte" value="" />
			<input type="hidden" id="EP" name="EP" />
			<input type="hidden" id="epFind" name="epFind" />
			<input type="hidden" id="EPSend" name="EPSend" value="" />
			<input type="hidden" id="existeCom" name="existeCom" />
			<input type="hidden" id="ExisteComEnc" name="ExisteComEnc" value="" />
			<input type="hidden" id="existeConvenio" name="existeConvenio">
			<input type="hidden" id="existeDetConvModif" name="existeDetConvModif" value="" />
			<input type="hidden" id="existeDetPrecom" name="existeDetPrecom" />
			<input type="hidden" id="ExistePrecomEnc" name="ExistePrecomEnc" value="" />
			<input type="hidden" id="febImporte" value="" />
			<input type="hidden" id="fFechaAutoVent" name="fFechaAutoVent" value="" />
			<input type="hidden" id="folioExistente" name="folioExistente" value="">
			<input type="hidden" id="hAplicaApartado" value="" />
			<input type="hidden" id="hAplicaCom" value="" />
			<input type="hidden" id="hAplicaPrecom" value="" />
			<input type="hidden" id="hGuardadoApartado" value="" />
			<input type="hidden" id="hGuardadoCompromiso" value="" />
			<input type="hidden" id="hGuardadoPrecompromiso" value="" />
			<input type="hidden" id="dicImporte" value="" />
			<input type="hidden" id="iCancelContract"  value="0" />
			<input type="hidden" id="idAreaSend" name="idAreaSend" value="" />	
			<input type="hidden" id="id_gabinete" name="id_gabinete" value="<%=id_gabinete%>" />
			<input type="hidden" id="IdHeader" name="IdHeader" value="-1" />
			<input type="hidden" id="iEsPluriAnual" name="iEsPluriAnual" value="" />
			<input type="hidden" id="iStatus" name="iStatus" />
			<input type="hidden" id="iTieneAnticipo" name="iTieneAnticipo" value="0" />
			<input type="hidden" id="iTieneConveModif" name="iTieneConveModif" value="0" />
			<input type="hidden" id="ivaConv" name="ivaConv" value="" />
			<input type="hidden" id="julImporte" value="" />
			<input type="hidden" id="junImporte" value="" />
			<input type="hidden" id="marImporte" value="" />
			<input type="hidden" id="mayImporte" value="" />
			<input type="hidden" id="habilitaCalendarizacionMesSuperior" name="habilitaCalendarizacionMesSuperior" value="" />
			<input type="hidden" id="nMesSuperior" name="nMesSuperior" value="0" />
			<input type="hidden" id="mImporte" name="mImporte" value="0" />
			<input type="hidden" id="mImporteAnt" name="mImporteAnt" value="0" />
			<input type="hidden" id="mImporteBruto" name="mImporteBruto" value="0">
			<input type="hidden" id="mImporteCapEPs" name="mImporteCapEPs" value="0.0" />
			<input type="hidden" id="mImporteDif" name="mImporteDif" value="0" />
			<input type="hidden" id="mImporteIVANoFrmt" name="mImporteIVANoFrmt" value="" />
			<input type="hidden" id="mImporteIVAPlurianualNoFrmt" name="mImporteIVAPlurianualNoFrmt" value="" />
			<input type="hidden" id="mImporteNegativo" name="mImporteNegativo" value="0" />
			<input type="hidden" id="mMontoIncrementoIVA" name="mMontoIncrementoIVA" value="0" />	
			<input type="hidden" id="MontoTotal" name="MontoTotal" value="0" />	
			<input type="hidden" id="MontoTotalIVA" name="MontoTotalIVA" value="0" />
			<input type="hidden" id="mMA" name="mMA" value="" />
			<input type="hidden" id="nDocRenglon" name="nDocRenglon" value="0" />
			<input type="hidden" id="nFolioOPADetail" name="nFolioOPADetail" value="-1" />
			<input type="hidden" id="nFolioOPAHeader" name="nFolioOPAHeader" value="-1" />
			<input type="hidden" id="nFolioOPComDetail" name="nFolioOPComDetail" value="-1" />
			<input type="hidden" id="nFolioOPComHeader" name="nFolioOPComHeader" value="-1" />
			<input type="hidden" id="nFolioOPConvHeader" name="nFolioOPConvHeader" value="" />
			<input type="hidden" id="nFolioOPPagPasHeader" name="nFolioOPPagPasHeader" value="" />
			<input type="hidden" id="nFolioOPPlurianualHeader" name="nFolioOPPlurianualHeader" value="" />
			<input type="hidden" id="nFolioOPPreComDetail" name="nFolioOPPreComDetail" value="-1" />
			<input type="hidden" id="nFolioOPPagPasDetail" name="nFolioOPPagPasDetail" value="-1" />
			<input type="hidden" id="nFolioOPPreComHeader" name="nFolioOPPreComHeader" value="-1" />
			<input type="hidden" id="nFolioSAI" name="nFolioSAI" value="<%=c.getFolio().substring( c.getFolio().lastIndexOf('-') + 1)%>" />
			<input type="hidden" id="nMontoAnticipo" name="nMontoAnticipo" value="0.00"/>
			<input type="hidden" id="nMontoConIVANoFrmt" name="nMontoConIVANoFrmt" value="" />
			<input type="hidden" id="nMontoNoFrmt" name="nMontoNoFrmt" value="" />
			<input type="hidden" id="noOfAutorizacionSend" name="noOfAutorizacionSend" />
			<input type="hidden" id="novImporte" value="" />
			<input type="hidden" id="octImporte" value="" />
			<input type="hidden" id="opc2PC" name="opc2PC" value="" />
			<input type="hidden" id="pasaValor" />
			<input type="hidden" id="porcRetencionSend" name="porcRetencionSend" value="" />
			<input type="hidden" id="sepImporte" value="" />
			<input type="hidden" id="titulo_aplicacion" name="titulo_aplicacion" value="<%=titulo_aplicacion%>" />
			<input type="hidden" id="toConvert" value="" />
			<input type="hidden" id="total_paginas" name="total_paginas"/>
			<input type="hidden" id="U_Login" name="U_Login" value="<%=uLogin%>" />
			<input type="hidden" id="yMA" name="yMA" value="" />
			
			<!-- Validar -->
			<input type="hidden" id="cOficio" name="cOficio" value="" />
			<input type="hidden" id="fFinCntAnt" name="fFinCntAnt" value="" />
			<input type="hidden" id="nOrden" name="nOrden" value="" />
			<input type="hidden" id="Partida" name="Partida" value="6" />
			<input type="hidden" id="Partida2" name="Partida2" value="" />
			<input type="hidden" id="Partida3" name="Partida3" value="" />
			<input type="hidden" id="Partida4" name="Partida4" value="" />
			<input type="hidden" id="cDocumentoHaplicado" name="cDocumentoHaplicado" value="N" />
			<input type="hidden" id="fAplicacion" name="fAplicacion" value="" />
			<input type="hidden" id="nFolioApartado" name="nFolioApartado" value="1" />
			<input type="hidden" id="nFolioPrecompromiso" name="nFolioPrecompromiso" value="1" />
			<input type="hidden" id="nFolioCompromiso" name="nFolioCompromiso" value="1" />
			<input type="hidden" id="anualImporte" name="anualImporte" value="" />
			<input type="hidden" id="anualImporteMod" name="anualImporteMod" value="" />
			<input type="hidden" id="EPn" name="EPn" />
			<input type="hidden" id="nDocRenglonDetalle" name="nDocRenglonDetalle" value="0" />
			<input type="hidden" id="mImporteActu" name="mImporteActu" value="0" />
			<input type="hidden" id="nDocRenglonActu" name="nDocRenglonActu" value="0" />
			<input type="hidden" id="renglonReadonly" name="renglonReadonly" value="0" />
			<input type="hidden" id="statusDocumento" name="statusDocumento" value="1" />
			<input type="hidden" id="FolioSAI" name="FolioSAI" value="<%=c.getFolio()%>" />
			<input type="hidden" id="cCveCartera" name="cCveCartera" value="" />
			<input type="hidden" id="cCveOLI" name="cCveOLI" value="" />
			<input type="hidden" id="nPorceIVA" name="nPorceIVA" value="" />
			<input type="hidden" id="cTipoObra" name="cTipoObra" value="" />
			<input type="hidden" id="cTipoRecurso" name="cTipoRecurso" value="" />
			<input type="hidden" id="cDescripcion" name="cDescripcion" value="" />
			<input type="hidden" id="cRFC" name="cRFC" value="" />
			<input type="hidden" id="cBeneficiario" name="cBeneficiario" value="" />
			<input type="hidden" id="cConvocatoria" name="cConvocatoria" value="" />
			<input type="hidden" id="fFechaFallo" name="fFechaFallo" value="01/01/1900" />
			<input type="hidden" id="nPorceAnticipo" name="nPorceAnticipo" value="0.00" />
			<input type="hidden" id="fFechaIniContr" name="fFechaIniContr" value="" />
			<input type="hidden" id="fFechaFinContr" name="fFechaFinContr" value="" />
			<input type="hidden" id="cCveConcurso" name="cCveConcurso" value="" />
			<input type="hidden" id="cTipoContrato" name="cTipoContrato" value="" />
			<input type="hidden" id="id_precio" name="id_precio" value="" />
			<input type="hidden" id="cTipoAdjudica" name="cTipoAdjudica" value="" />
			<input type="hidden" id="iTipoAdicional" name="iTipoAdicional" value="" />
			<input type="hidden" id="fFechaOficio" name="fFechaOficio" value="" />
			<input type="hidden" id="fFechaConvoca" name="fFechaConvoca" value="" />
			<input type="hidden" id="fFechaJuntAcla" name="fFechaJuntAcla" value="" />
			<input type="hidden" id="fFechaReceProp" name="fFechaReceProp" value="" />
			<input type="hidden" id="existeEstimacion" name="existeEstimacion"/>
			<input type="hidden" id="noEstimacionDtTable" name="noEstimacionDtTable"/>
			<input type="hidden" id="apartadoAplicado" name="apartadoAplicado"/>
			<input type="hidden" id="precompromisoAplicado" name="precompromisoAplicado"/>
			<input type="hidden" id="compromisoAplicado" name="compromisoAplicado"/>
			<input type="hidden" id="sumaEstimacion" name="sumaEstimacion"/>
			<input type="hidden" id="cidContrato" name ="cidContrato"  />
			<input type="hidden" id="cidContrato" name ="cidContrato"  />
			<input type="hidden" id="existeConv" name ="existeConv"  />
			
			<!-- MLR RO 0010 -->
			<input type="hidden" id="nMontoContrato" name="nMontoContrato"/>
			<input type="hidden" id="cArticuloSend" name="cArticuloSend"/>
			<!-- VGC20181309 Se agrega para validar resto por amortizar -->
			<input type="hidden" id="restoAmortizar" name="restoAmortizar" value="0"/>
			<input type="hidden" id="idRealEstateAux" name="idRealEstateAux" value="-1"/>
			<!--HRFR20230504 hidens con los datos del contrato, anticipo y estimación-->
			<input type="hidden" id="brutoContrato" name="brutoContrato" value="0"/>
			<input type="hidden" id="porcentajeContrato" name="porcentajeContrato" value="0"/>
			<input type="hidden" id="montoAmortizacionContrato" name="montoAmortizacionContrato" value="0"/>
			<input type="hidden" id="montoIVAContrato" name="montoIVAContrato" value="0"/>
			<input type="hidden" id="montoTotalContrato" name="montoTotalContrato" value="0"/>
			<input type="hidden" id="retencionTotalContrato" name="retencionTotalContrato" value="0"/>
			<input type="hidden" id="netoContrato" name="netoContrato" value="0"/>
			<input type="hidden" id="brutoAnticipo" name="brutoAnticipo" value="0"/>
			<input type="hidden" id="porcentajeAnticipo" name="porcentajeAnticipo" value="0"/>
			<input type="hidden" id="montoIVAAnticipo" name="montoIVAAnticipo" value="0"/>
			<input type="hidden" id="montoAnticipoTotal" name="montoAnticipoTotal" value="0"/>
			<input type="hidden" id="brutoEstimaciones" name="brutoEstimaciones" value="0"/>
			<input type="hidden" id="porcentajeEstimaciones" name="porcentajeEstimaciones" value="0"/>
			<input type="hidden" id="amortizacionEstimaciones" name="amortizacionEstimaciones" value="0"/>
			<input type="hidden" id="montoIVAEstimaciones" name="montoIVAEstimaciones" value="0"/>
			<input type="hidden" id="montoEstimacionTotal" name="montoEstimacionTotal" value="0"/>
			<input type="hidden" id="montoEstimacionRetencion" name="montoEstimacionRetencion" value="0"/>
			<input type="hidden" id="montoEstimacionNeto" name="montoEstimacionNeto" value="0"/>
			<input type="hidden" id="porcentajeRetencion5" name="porcentajeRetencion5" value="0"/>
			<input type="hidden" id="montoConvSinIVA" name="montoConvSinIVA" value="0"/>
			
		</form>
	</body>
</html>