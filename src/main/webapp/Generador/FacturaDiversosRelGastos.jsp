<%@page import="com.syc.contable.PagosDiversosBussinessLogic"%>
<%@page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@page import="com.syc.contable.core.CatalogoURFIELBusinessLogic"%>

<%
	boolean bAplicadoCont = false;
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String algo = "";
	String U_LOGIN = "";

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	int idTipoCaso = c.getIdTC();

	if (c.getCasoDato("APLICADO_CONT").getValor() != null) {
		if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()))
			bAplicadoCont = true;
	}

	String mensaje = "";
	if (request.getParameter("msg") != null && !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(
			GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	//documentos del caso
	CasoBusinessLogic cbl = new CasoBusinessLogic(
			GestionInterface.ATT_CONEXION);
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
	boolean esSAIAlterno = "true".equals( cabl.getSystemSetting("SAI_AMBIENTAL") ) || "true".equals(cabl.getSystemSetting("SAI_FONDEN"));
	boolean esSAIFonden = "true".equals( cabl.getSystemSetting("SAI_FONDEN") );
	
	String select = c.getTipoCaso().getGavetaAsociada() + "_G"
			+ c.getIdGabinete();//se usa por separado abajo
	String cAplicaDocto = "No";
	if (request.getParameter("aplicaDocto") != null
			&& request.getParameter("aplicaDocto").equals("Si")) {
		cAplicaDocto = "Si";
	}
	
	//Valida Centro de Costos
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}

	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	//cRamo="16";
	algo = usuario.getLogin();
	U_LOGIN = usuario.getLogin();
	
/*VGC20151228 Se parametriza el habilitar radicado*/
ConfiguraAplicativoBusinessLogic configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
boolean muestraCBRadicado = "S".equalsIgnoreCase(   configApp.getSystemSetting("MUESTRA_RADICADO_RGOC")  );
boolean muestraCBIngreso = "S".equalsIgnoreCase( configApp.getSystemSetting("MUESTRA_INGRESOFISCAL_RGOC"));
boolean habilitaCBRadicado = "S".equalsIgnoreCase(   configApp.getSystemSetting("HABILITA_SEL_RADICADO_RG_OC")  );

	/*FAV20171019 Se guarda en base el prefijo de CxP*/
	String cxpPrefijo = configApp.getSystemSetting("CXP_PREFIJO") == null?"CP":configApp.getSystemSetting("CXP_PREFIJO");
	
	CatalogoURFIELBusinessLogic curbl = new CatalogoURFIELBusinessLogic( GestionInterface.ATT_CONEXION );
	boolean permitePagoSinFIEL = curbl.permitePagoSinFiel(cUR);
	String numeroEmpleado = usuario.getNumeroEmpleado();
	boolean esConsulta = c.getCasoOperacion(0).getOperacion().getNumero() == 3;
	
	String cNombreElabora = e.getNombre();
	String cApellidoPaternoElabora  = e.getApellidoPaterno();
	String cApellidoMaternoElabora  = e.getApellidoMaterno();
	String cPuestoElabora = e.getCargo();
	
	/*ARLA20231213 Permite habilitar o deshabilitar la validacion del saldo de Ingreso Propios*/
	boolean validaSaldoIP = "S".equalsIgnoreCase(   configApp.getSystemSetting("VALIDA_SALDO_IP")  );
	int nFolio = Util.folio( c );
	PagosDiversosBussinessLogic pdbl = new PagosDiversosBussinessLogic(GestionInterface.ATT_CONEXION);
	boolean autorizaPROFOEM ="S".equalsIgnoreCase(  cabl.getSystemSetting( "ACTIVA_VALIDACION_PROFOEM" ) );
	boolean pagoPROFOEM = pdbl.esPagoPROFOEM( nFolio);

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Documentos Diversos - Recepción de documentos</title>

<style type="text/css" title="currentStyle">	
	@import "themes/smoothness/jquery-ui-1.8.4.custom.css";	
</style>

<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker-es.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>

<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/ImpuestosRetenciones.js"></script>
<script type="text/javascript" src="js/ValidaMesContable.js"></script>
<script type="text/javascript" src="js/Firmantes.js"></script>
<script type="text/javascript" src="js/ActualizaFIEL.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>

<script type="text/javascript" charset="utf-8">
	
	
	const autorizaPROFOEM = <%=autorizaPROFOEM%>; 
	const pagoPROFOEM = <%=pagoPROFOEM%>;

	var muestraDivImprimePoliza = false;
	
	var bClicBtn = false;
	var breturnVal = false;
	var bCOMSOC = false;
	var oTablevFact;
	var oTableIng;
	var excluirValidacion = 'MET8908305M9';
	var oTableEps;
	var muestraCBRadicado = <%=muestraCBRadicado%>;
	var muestraCBIngreso = <%=muestraCBIngreso%>;
	var relaciones;
	var habilitaCBRadicado = <%=habilitaCBRadicado%>;
	var esSAIAlterno = <%=esSAIAlterno%>;
	var esSAIFonden = <%=esSAIFonden%>;
	let idOperacion = <%=id_oper%>;
	
	var cNombreElabora = "<%=cNombreElabora%>";
	var cApellidoPaternoElabora  = "<%=cApellidoPaternoElabora%>";
	var cApellidoMaternoElabora  = "<%=cApellidoMaternoElabora%>";
	var cPuestoElabora = "<%=cPuestoElabora%>";
	
	/*FAV20171019 Se guarda en base el prefijo de CxP*/
	var cxpPrefijo = "<%=cxpPrefijo%>";
	var habilitaCheckIP = false;
	
	var esConsulta = <%=esConsulta%>;
	/* Variable que indica si el pago es con firma (FIEL) */
	var permitePagoSinFIEL = <%=permitePagoSinFIEL%>;
	var numeroEmpleado = "<%=numeroEmpleado%>"
	var validaSaldoIP = <%=validaSaldoIP%>;
	let modalOli;
	let modalValidaFact;
	let modalCorreo;
		
	 $(document).ready( 			
		 function() {
			$("#verFondenSPAN").hide();
		 	
		 	$("#cNombreEla").val(cNombreElabora);
		 	$("#cPaternoEla").val(cApellidoPaternoElabora);
		 	$("#cMaternoEla").val(cApellidoMaternoElabora);
		 	$("#cPuestoEla").val(cPuestoElabora);
		 	
		 	if( permitePagoSinFIEL ) {
				$("#AutorizaConFielTD").css("display","block");
			}else{
				$("#autorizadoPorFiel ").val("true");
			}
			
			queryFormPost({
				queryName : "tipoAutorizacionRead",
				async:false,
				callback: function(){
					if( $("#TipoAutorizacion").val() == "N" ){
						muestraDivImprimePoliza = true;
					}
				}
			});
		 	
		 	$("#chk_RB").change(
			 	function(){
					referenciaBancaria();
				}
			);
		 	
		 	modalOli= new bootstrap.Modal(document.getElementById('dialog-validaOLI'), 'data-bs-backdrop');
		 	modalValidaFact = new bootstrap.Modal(document.getElementById('dialog-validaFact'), 'data-bs-backdrop');
		 	modalCorreo= new bootstrap.Modal(document.getElementById('dialog-actualizaCorreo'), 'data-bs-backdrop');
		 	modalOpinion = new bootstrap.Modal(document.getElementById('dialog-opinion'), 'data-bs-backdrop');
			
		 	document.getElementById("trReferenciaBancaria").style.display = "none";
		 	var relaciones  = [];
		 	if (parent.document.getElementById("pb_cancel")){
	   			parent.document.getElementById("pb_cancel").style.visibility='hidden';
				parent.document.getElementById("pb_cancel").disabled=true;
			}
			
			$("input.AyudaSyC").subIniciaDlg();
			$("input.autoCompletaSyC").subIniciaAutoCompleta();
			$("#Agregar1").button();
		 	$("#agrega2").button();
		 	$("#nIdClaveEgresos2").button();
		 	$("#btnLayoutBoletos").button();
		 	$("#btnCalculaCedular").button();
		 	$("#Borrar").button(); 
		 	$("#btnCalculaCedular1").button();
		 	$("#penas").css("display", "none");
		 	$("#penasI").css("display", "none");
		 	
			//VGC2016019 Parametriza si se muestra o no el checkbox de radicado
			if( !muestraCBRadicado ){
				$("#divRadicado").hide();
				$("#chk_radicado").attr("checked", false);
				$("#chk_radicado").change();
				$("#cEsRadicado").val("N");
			}
			
			//Parametriza si se muestra o no el checkbox de ingreso fiscal recaudado
			if( !muestraCBIngreso ){
				$("#divIngresoFiscal").hide();
				$("#chk_IF").attr("checked", false);
				$("#chk_IF").change();
			}
			
			//2017-01-13 Si esta activado el CB lo muestra habilitado.
			if( habilitaCBRadicado ){
				$("#chk_radicado").attr('disabled', false);
			}else{
				$("#chk_radicado").attr('disabled', true);
			}
			
				
		if( permitePagoSinFIEL ) {
			$("#AutorizaConFielTD").css("display","block");
		}else{
			$("#autorizadoPorFiel ").val("true");
		}
		
		$("#btnLayoutBoletos").hide();
		$("#lblNumAcompanantes").hide();
		$("#nAcompanantes").hide();
		$("#btnCalculaCedular").hide();
		
		if(!esSAIAlterno){
			$("#chk_IP").prop("checked",true);
			$("#chk_IP").attr('disabled', true);
			$("#chk_IF").attr('disabled', true);
			$("#fuenteFinanciamiento").val("4");
		}
						
    	var aaa = <%=request.getParameter("folio")%>;
		var porretencion=0;
			
			$( "#dlgSeleccionaFonden" ).dialog({
				autoOpen: false,
				height: 200,
				width: 400,
				modal: true,
				buttons: {
					"Aceptar": function() {
						if(idOperacion == 1  ){
							if( $("#proyectoFonden").val() == "" ){
								Swal.fire("Proyecto Fonden", "No ha seleccionado un proyecto de FONDEN", "warning");
							}else{
								$.blockUI();
								$("#cProyectoFonden").val( $("#proyectoFonden").val() );
								queryFormPost({
									queryName:"proyectoFondenPDUpdate",
									async:false,
									callback:function(){
										$( "#dlgSeleccionaFonden" ).dialog("close");
									}
								});
								$.unblockUI();
							}
						}
						else
							$( "#dlgSeleccionaFonden" ).dialog("close");
						
					},
					"Cerrar":function(){
						if(idOperacion != 1  ){
							$("#dlgSeleccionaFonden").dialog("close");
						}else{
							if( $("#proyectoFonden").val() == "" ){
								Swal.fire("No ha capturado el proyecto de FONDEN", "No podra continuar el pago hasta capturarlo.", "warning");
							}else{
								$("#dlgSeleccionaFonden").dialog("close");
							}
						}
					}
				}
			});
				
			$( "#dialog-form" ).dialog({
				autoOpen: false,
				height: 400,
				width: 800,
				modal: true,
				beforeClose: function( event, ui ) {
					return bClicBtn;			
				}
			});

			$( "#dialog-Procesando" ).dialog({
				autoOpen: false,
				height: 400,
				width: 400,
				modal: true,
				open: function() {

					var tipo = "aplicarMotor";
					var caNoContrarrecibo = $("#caNoContrarrecibo").val();
					var campo = $("#campo").val();
					var tablaEnc = $("#tablaEnc").val();
					var campoCondicion = $("#campoCondicion").val(); 
					var tablaDet = $("#tablaDet").val(); 
					var tipoAplicar = $("#tipoAplicar").val();

					var numEmpleadoCaptura = numeroEmpleado;
					var numEmpleadoVoBo = $("#cboVoBo").val();
					var numEmpleadoAut = $("#cboAutoriza").val();
					var autorizadoPorFiel = $("#autorizadoPorFiel").val();
					
					$("#divEsperaProcesando").attr("style","visibility=visible");
					
					var fAppActualizada = actualizaMesAplicacion();
					
					if( !fAppActualizada){
						$("#divEsperaProcesando").attr("style","visibility=hidden");
						$( "#dialog-Procesando" ).dialog( "close" );
						return false;
					}
					
					/*Validar si la retencion insertada en el detalle corresponde al regimen fiscal del proveedor */			
					queryFormPost("validaRegimenFiscal", {async: false});
					var msgRF = $("#msgRF").val();
					
					if ( msgRF != "" ){
						alert(msgRF);
						$("#dialog-Procesando").dialog("close");
						return; 			
					}
					
					/*Validar la suma de retenciones del detalle vs el encabezado*/			
					queryFormPost("validaRetencionENCvsDET", {async: false});
					var msgRF = $("#msgRF").val();
					
					if ( msgRF != "" ){
						alert(msgRF);
						$("#dialog-Procesando").dialog("close");
						return; 			
					}
					
					/*Validar si el regimen es 626 y el tipo persona es Moral no debe tener retencion RESICO*/			
					queryFormPost("validaRESICOPersonaMoral", {async: false});
					var msgRF = $("#msgRF").val();
					
					if ( msgRF != "" ){
						alert(msgRF);
						$("#dialog-Procesando").dialog("close");
						return; 			
					}
					
					/*ARLA SI LAS FACTURAS ESTAN BORRADAS SE HACE EL INSERT A tPagoFactura Y SE BORRAN DE tPagoFactura_Borrada*/
					queryFormPost("validaExisteFacturas", {async: false});
					
					$.ajax({
							url:'./cierrePresupuestal.jsp',
							type:'post',
							dataType: 'json',
							data:{tipo:tipo,
								  caNoContrarrecibo:caNoContrarrecibo,
								  campo:campo,
								  tablaEnc:tablaEnc,
								  campoCondicion:campoCondicion,
								  tablaDet:tablaDet,
								  tipoAplicar:tipoAplicar,
								  empleadoCaptura : numEmpleadoCaptura,
								  empleadoVoBo : numEmpleadoVoBo,
								  empleadoAutoriza : numEmpleadoAut,
								  autorizadoPorFiel : autorizadoPorFiel
								  },
									success:function(data){
									if(data.sinSesion == 'sinSesion'){
											location.href = "../index.jsp";
									}
									if(data.estatus == "guardado"){
										//$("#docAplicado").val( "S" ) ;
										/* JGDS Se agrega este crud para ver si tiene contrarrecibo y no crearlo*/
										var tieneContrarecibo = undefined;
										var contrareciboRegenerado = false;													
										queryFormPost({
											queryName : "tieneContrarrecibo",
											async:false,
											callback: function(){
												 tieneContrarecibo = ( $("#correcto").val() == "1" );
											}
									   	});
													
											if( tieneContrarecibo == undefined )
												throw "No se pudo determinar si el pago tiene contrarecibo";
													
											if(!tieneContrarecibo){
												queryFormPost({
														queryName : "insertaContrarrecibo",
														async:false,
														callback: function(){
															contrareciboRegenerado = true;
														}
												   });
												if(!contrareciboRegenerado)
													throw "El pago no genero contrarecibo. Descarte el tramite y reinicie";
											}
											
										if( $("#autorizadoPorFiel").val() != "true" ){
											cmdImprimir('PolizaPago');
										}
										
										alert( "Documento Aplicado Correctamente: "+ $("#caNoContrarrecibo").val() ) ;
										
										$("#cTipoPago").val("PAGODIVERSO");
										queryFormPost("cBuscaRadicado", {async: false });
										
										if ($("#cEsRadicado").val()=="S"){
											// TODO: Actualizamos a "D" FOLIO, cTipoPAgo, cEsRadicado
											queryFormPost("devengadoIngresoPago",{async: false });
										}
																				
													
										parent.document.getElementById("pb_send").disabled = false;
										parent.document.getElementById("pb_send").click();		
										breturnVal = true;
										actualizaFolioVueloPagado();
										
									}else{
										breturnVal = false;
										alert( "El Documento No Se Aplico: "+$("#caNoContrarrecibo").val() + " - " + data.estatus ) ;		
									}
									$("#divEsperaProcesando").attr("style","visibility=hidden");
									$( "#dialog-Procesando" ).dialog( "close" );
							}
					});
				},
				close: function() {										
				}				
			});

			
			$("#dialog-Anticipo").dialog({
				autoOpen : false,
				height : 500,
				width : 850,
				modal : true,
				buttons : {
					"Aceptar" : function() {
						var vacio = true;
						var impEjercer = $("#mImpEjercer").val();
						var impAcumulado = $("#totalAcumulado").val();
						var diferencia;
						var table = document.getElementById('tblSolicitudesIngreso');
		 				var aTrs = $('#tblSolicitudesIngreso').dataTable().fnGetNodes();		 				
		 				var fecha = $("#fRecepcion").val();
	 					
		 				for ( var i=1; i<=aTrs.length;  i++ ){ 								
	 						var row= table.rows[i];
	 						var chkbox = row.cells[0].childNodes[0];
	 						
	 						if(null != chkbox && true == chkbox.checked){
	 							var folioING = row.cells[1].innerHTML;
	 							var remanenteING = Number( quitaFmt( row.cells[3].innerHTML ) );		 							
	 							$('#sDataFoliosING').val($('#sDataFoliosING').val() + folioING + ",");
	 							$('#sDataRemanenteING').val($("#sDataRemanenteING").val() + remanenteING + ",");
	 							vacio = false;		 							
	 						}	
	 					}
		 				
		 				if(vacio){
		 					Swal.fire({ icon: "info",
								text: "Debes seleccionar al menos un ingreso que cubra el importe a ejercer." });
		 					return;
		 				}
		 				
						diferencia = impAcumulado - impEjercer;
						if(diferencia <= 0){
							Swal.fire({ icon: "warning",
										text: "El remanente del ingreso es menor al importe a ejercer, favor de seleccionar una solicitud de ingreso. \n Remanente: " + impAcumulado + "\n Ejercer: " + impEjercer });		
							return;
						} else {							
							$.ajax({
								url : '../GREENMEX/greenmex',
								dataType : 'json',
								type :"POST",
								data : {
									"folioRG" : aaa,
									"impEjercer": impEjercer,
									"folioING" : $("#sDataFoliosING").val(),
									"remanenteING" : $("#sDataRemanenteING").val(),
									"fecha" : fecha
								},
								async : false,
								success : function(json) {
									var exito = json.success;
									if( exito == "true"){
										Swal.fire("Se actualizo exitosamente el estado de cuenta de GreenMex","success");										
									}
								},
								error : function(xhr, textStatus, errorThrown) {
									Swal.fire("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown,"error");									
									return false;
								}
							});
						}
						$( this ).dialog( "close" );
						 						
					},
					"Cancelar" : function() {
						$(this).dialog("close");
					}
				},
				close: function() {										
				}
			});
			
		creaDTFacturas();
		creaDialogJustificacion();
		
		$("#chk_IP").change(function(){
			if ($("#chk_IP").prop("checked")){
				$("#fuenteFinanciamiento").val("4");
				
				if(muestraCBRadicado){
					$("#chk_radicado").prop("checked",false);
					$("#chk_radicado").attr('disabled', true);
					$("#cEsRadicado").val("N");
				} 
				
				if (muestraCBIngreso){
					$("#chk_IF").prop("checked",false);
					$("#chk_IF").attr('disabled', true);
					$("#cEsIngresoFiscal").val("N");
				}
				
			}else{
				$("#fuenteFinanciamiento").val("1");
				
				if(muestraCBRadicado){
					$("#chk_radicado").attr('disabled', false);
					$("#chk_radicado").prop("checked",	true);
					$("#cEsRadicado").val("S");
				}
				
				if(muestraCBIngreso){
					$("#chk_IF").attr('disabled', false);
					$("#chk_IF").prop("checked",  true);
					$("#cEsIngreso").val("S");
				}
			}
			
		});
				
		$("#chk_IF").change(function(){
			if ($("#chk_IF").prop("checked")){
					$("#cEsIngresoFiscal").val("S");
					
					$("#chk_IP").prop("checked",false);
					$("#chk_IP").attr('disabled', true);
					
					if (muestraCBRadicado){
						$("#chk_radicado").prop("checked",false);
						$("#chk_radicado").attr('disabled', true);
						$("#cEsRadicado").val("N");
					}
			}else{
					$("#cEsIngresoFiscal").val("N");
					
					$("#chk_IP").attr('disabled', false);
					$("#chk_IP").prop("checked",false);
					
					if (muestraCBRadicado){
						$("#chk_radicado").attr('disabled', false);
						$("#chk_radicado").prop("checked",true);
						$("#cEsRadicado").val("S");
					}
			}
		});
		
		$("#chk_radicado").change(function(){
			if ($("#chk_radicado").prop("checked")){
					$("#cEsRadicado").val("S");
					
					$("#chk_IP").prop("checked",false);
					$("#chk_IP").attr('disabled', true);
					
					if (muestraCBIngreso){
						$("#chk_IF").prop("checked",false);
						$("#chk_IF").attr('disabled', true);
						$("#cEsIngresoFiscal").val("N");
					}
			}else{
					$("#cEsRadicado").val("N");
					
					$("#chk_IP").attr('disabled', false);
					$("#chk_IP").prop("checked",false);
					
					if (muestraCBIngreso){
						$("#chk_IF").attr('disabled', false);
						$("#chk_IF").prop("checked",true);
						$("#cEsIngresoFiscal").val("S");
					}
			}
		});
		
			$("#pbAgregar")
				.button()
				.click(function() {
					if (Number($("#mImporteFact").val()).toFixed(2)==0){
						Swal.fire("Valide la Factura","No se puede agregar factura con importe 0","warning");
						return;
					}
					if ( $("#cFactura").val() == "" || Number( $("#cFactura").val() ) <= 0 ){
						Swal.fire("Capturar","Falta Capturar el Número de Factura", "warning");
						return;
					}

					if ( $("#mImporteFact").val() == "" ||  Number( $("#mImporteFact").val() ) <= 0 ){
						Swal.fire("Capturar","Falta Capturar el Importe de la Factura","warning");
						return;
					}

					var aData = oTablevFact.fnGetData();
					for(var i=0; i<aData.length; i++) {
						var cFactura = aData[ i ][ 0 ] + aData[ i ][ 1 ];
						if( $("#cSerie").val() + $("#cFactura").val() == cFactura){
							Swal.fire("Verifique","La Factura ya está capturada","warning");
							$("#cSerie").val( "" );
							$("#cFactura").val( "" );
							$("#mImporteFact").val( "" );
							return;
						}
					}

					var mImporte = quitaFmt( $("#mImporteFact").val() );
					$("#mImporteFact").formatCurrency();
					$('#grdValidaFacturas').dataTable().fnAddData( [ $("#cSerie").val(), $("#cFactura").val(), $("#mImporteFact").val()] );
					mImporte = Number( quitaFmt( $("#mTotalFacturaV").val() ) ) + Number( mImporte );
					$("#mTotalFacturaV").val( mImporte );
					$("#mTotalFacturaV").formatCurrency();
					$("#cSerie").val("");
					$("#cFactura").val("");
					$("#mImporteFact").val("");
			});	
			
			$("#grdCompromisos tbody").dblclick(function(event) {
				if(<%=id_oper == 1%>){
					 
  					$(oTableEps.fnSettings().aoData).each(function (){
   						$(this.nTr).removeClass('row_selected');
   						var aPos = oTableEps.fnGetPosition( this.nTr );
    					var aData = oTableEps.fnGetData( aPos[0] );
 					});
  					$(event.target.parentNode).addClass('row_selected');
     				var aPos = oTableEps.fnGetPosition( event.target.parentNode );
     				var aData = oTableEps.fnGetData( aPos );
					
     				$("#cEPborrar").val(aData[1]);
     				$("#mImporteBorrar").val(aData[2]);
     				$("#renglonBorrar").val(aPos+1);
     				
     				var montoRestar = 0.00;
     				montoRestar = Number(quitaFmt($("#mImporteBorrar").val()));
					
     				queryFormPost("tpagodiversodetalleDelete", {async: false });
     				queryFormPost("UpdateRecorridoRenglonPDiverso", {async: false });
					
					if ($("#cEsIngresoFiscal").val()=="S"){
					
						//TODO: Actualizamos mtotal
						queryFormPost("buscaIngresoPago", {async: false});
						queryFormPost("tIngresoPagoDetalleDelete", {async: false });
						
						$("#mTotalIngresoPago").val(parseFloat($("#mTotalIngresoPago").val()) - montoRestar);
						$("#saldoCompM").val(parseFloat($("#saldoCompM").val())-montoRestar);
						queryFormPost("updateIngresoPago",{async:false});	
					}
					
					$('#grdCompromisos').dataTable().fnClearTable();
					consMovimientos();
				}
 			});
			
 			$("#grdValidaFacturas tbody").dblclick(function(event) {
 			
					$(oTablevFact.fnSettings().aoData).each(function (){
   						$(this.nTr).removeClass('row_selected');
   						var aPos = oTablevFact.fnGetPosition( this.nTr );
    					// Get the data array for this row
    					var aData = oTablevFact.fnGetData( aPos[0] );

 					});
 					
  					$(event.target.parentNode).addClass('row_selected');
     				var aPos = oTablevFact.fnGetPosition( event.target.parentNode );
     				if (aPos != null){
	     				var aData = oTablevFact.fnGetData( aPos );
	     				var mImporte = Number( quitaFmt( $("#mTotalFacturaV").val() ) ) - Number( quitaFmt( aData[ 2 ] ) );
						$("#mTotalFacturaV").val( mImporte );
						$("#mTotalFacturaV").formatCurrency();
						oTablevFact.fnDeleteRow( aPos);
     				}
 			});

			$("#grdValidaFacturas tbody").click(function(event) {
					$(oTablevFact.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
					var aPos = oTablevFact.fnGetPosition( this.nTr );
					// Get the data array for this row
					var aData = oTablevFact.fnGetData( aPos[0] );
				});
				$(event.target.parentNode).addClass('row_selected');
			});
			
			
			$("#destGasto").change(function(){
				if ( $(this).val() == "AL" ){
					document.getElementById("Imp_Bruto").removeAttribute("readonly",false);
				}else if ( $.trim($(this).val()) != "" ){
					if( $("#cIdRFC").val() == "" ){
						$('#destGasto option[value=" "]').attr('selected','selected');
						Swal.fire("Beneficiario","Favor de seleccionar un Beneficiario","warning");
						return;		
					}else{
						//URVP.19112014 VALIDACIONES PARA SABER SI LLEVA O NO ANTICIPO, SI YA SE REGISTRO EL ANTICIPO EN CASO DE LLEVAR
						if ($(this).val()=="ANRO"){
							if ($("#llevaAnticipo").val()=="0"){
								$('#destGasto option[value=" "]').attr('selected','selected');
								Swal.fire("Este contrato no lleva anticipo", "Favor de seleccionar la opcion correcta.","warning");
								return;
							}
							if ($("#tieneAnticipo").val()!="0"){
								
								if($("#tieneRemanente").val() == "0"){
									$('#destGasto option[value=" "]').attr('selected','selected');
									Swal.fire("A este contrato ya se hizo el pago del anticipo", "Favor de seleccionar la opcion correcta.","warning");
									return;
								}else if($("#tieneRemanente").val() == "1"){
									$("#tieneAnticipo").val("0");
									$(".subtotall").change();									
								}
							}
							$("#nPorcAmortizacion").val(0);
						}else{
							if ($("#llevaAnticipo").val()=="1" && $("#tieneAnticipo").val()=="0"){
								$('#destGasto option[value=" "]').attr('selected','selected');
								Swal.fire("Este contrato lleva anticipo y no se le ha hecho", "Favor de seleccionar la opcion correcta.","warning");
								return;
							}
							if($("#tieneRemanente").val() != "0"){
								agregaAmortizacion(true);
							}
						}
					
						$("#mImporteBruto").attr("readonly", true);
						modalValidaFact.show();
						$("#uploadFacturasDiv").show();
						$("#facturasCapturadasDiv").hide();
						habilitaBtnCargaLayout();
						muestraCalculoCedular();	
					}
					if ( $.trim($(this).val())!="" && $("#cIdRFC").val() != ""){
						if($("#numPaso").val() == "1" )
							$("#EditaFacturas").css('visibility', 'visible');
					}else{
						$("#EditaFacturas").css('visibility', 'hidden');
					}
				}
			});
			
			$('#Descrip_Concepto').bind('copy paste', function (e) {       
				e.preventDefault();
	    	});

		  $("#OIRAUSU").val( "<%=algo%>" );
          $("#cCentroContable").val( "<%=cCentroContable%>");			
          $("#id_caso").val(aaa);
          $("#cNoFactura").val(aaa);
          $("#tabs").tabs( {
            	"show": function(event, ui) {            
            		var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
            		if ( oTable.length > 0 ) {                
            			oTable.fnAdjustColumnSizing();            
            		}
            	}    
            } );
	
          $('#tblDevengado1').dataTable({
				"bPaginate": false,
       			"bLengthChange": false,
       			"bFilter": false,
       			"bSort": false,
       			"bInfo": false,
       			"bAutoWidth": false,
				"sScrollY": "80%",
				"bJQueryUI": true,
				"bRetrive" : true,
				"bDestroy" : true,
				"sPaginationType": "full_numbers"

				} );
		oTableEps = $('#grdCompromisos').dataTable({
			"bPaginate": false,
   			"bLengthChange": false,
   			"bFilter": false,
   			"bSort": false,
   			"bInfo": false,
   			"bAutoWidth": false,
			"sScrollY": "80%",
			"bJQueryUI": true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers"
				} );
		$('#grdFacturas').dataTable(
				{
					sScrollY: "150px",
					sScrollX: "700px",
					sScrollXInner: "200%",
					"bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"

				} );
				
		$('#grdRetClave').dataTable({
				"bPaginate": false,
       			"bLengthChange": false,
       			"bFilter": false,
       			"bSort": false,
       			"bInfo": false,
       			"bAutoWidth": false,
				"sScrollY": "80%",
				"bJQueryUI": true,
				"bRetrive" : true,
				"bDestroy" : true,
				"sPaginationType": "full_numbers"

				} );
 oTable = $('#tretencion').dataTable( );
		$('#tretencion').dataTable(
				{
				"bPaginate": false,
       			"bLengthChange": false,
       			"bFilter": false,
       			"bSort": false,
       			"bInfo": false,
       			"bAutoWidth": false,
				"sScrollY": "80%",
				"bJQueryUI": true,
				"bRetrive" : true,
				"bDestroy" : true,
				"sPaginationType": "full_numbers"
				} );
		
		$('#tblSolicitudesIngreso').dataTable({
			"bPaginate": false,
   			"bLengthChange": false,
   			"bFilter": false,
   			"bSort": false,
   			"bInfo": false,
   			"bAutoWidth": false,
			"sScrollY": "80%",
			"bJQueryUI": true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers"

				} );
	$(function() {
		$( "#desde" ).datepicker({
				showOn: "button",
		//		dateFormat: "yy/mm/dd",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
				} );
	$(function() {
		$( "#hasta" ).datepicker({
				showOn: "button",
		//		dateFormat: "yy/mm/dd",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
				} );
	$(function() {
		$( "#fRecepcion" ).datepicker({
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true 
		});
	});
	
	$(function() {
		$( "#DCD_FECHA_FACTURA" ).datepicker({
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true
		});
	});
	
	var suma = 0;
	var sredondea=0;
	var res_iva=0;
	
	cssReadOnly();
	queryFormPost({
			queryName:"readImporteBrutoPago", 
			async:false, 
			callback:function(){
				if( $("#Imp_Bruto").val() == "" )
					$("#Imp_Bruto").val("$0.00");
			} 
		});
	

	$(".subtotall").change(function(){
		validaPenalizacion();
		calculaIVA();
	});
	
	habilitaCheckIP();
	if( habilitaCheckIP ){
		$("#chk_IP").attr('disabled', false);
	}else{
		$("#chk_IP").attr('disabled', true);
	}
});

	
	function cssReadOnly(){
		$( "[readOnly]" ).each(function(){	
			$(this).addClass("notEditable");	
		});
	}
		
	function muestraJson(){
		for( i = 0; i < relaciones.length; i++ )
			alert( relaciones[i].folioIngreso );
	}

	function validaEFO() {
		
		var esEFO = false;
		$("#esEFO").val("");
		$("#rfcValidar").val($("#cIdRFC_RelacionGasto").val());
		
		queryFormPost({
			queryName : "validaEFO",
			async : false,
			callback : function() {
				esEFO = $("#esEFO").val() == "S";
			}
		});
		
		return esEFO;
	}
		
		
	function onSubmit(id_oper){//validaciones del boton guardar
  		var p = window.parent;
  		var valida_campos = true;
  		if ($("#docAplicado").val() == "S") {
  			alert("Documento ya fue aplicado y se avanzará a modo de CONSULTA");
  		}else {
	  		
			try{
				//validaciones de la forma
				//poner aqui las validaciones a efectual en el boton guardar, en este caso no hay
	
				//Guardado de los campos correspondientes a cada variable de caso
				p.gestion.setFolio( $("#FOLIO").val() );
				p.gestion.setOperador( $("#OPERADOR").val() );
				p.gestion.setFechaDocumento( $("#FECHA_CARGA").val() );
				p.gestion.setEjercicioFiscal( $("#aEjercicioFiscal").val() );
				p.gestion.setConceptoMov("Aplicación Pago Obra");
				p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
				p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
				p.gestion.setAplicadoCont("false");
				
				var esEFO = validaEFO();
				if( esEFO ){
					alert("No se puede realizar el pago a un EFO. Solicite más informacion con el administrador");
					return false;
				}
				
				if(  $("#cIdRFC").val().replace(/ /g, '') != excluirValidacion ){
					$("#facturasDiferentes").val("0");
					queryFormPost("rfcDiferentesRead", {async : false});
					if( $("#facturasDiferentes").val() != "0" ){
						alert("El pago contiene facturas para otro proveedor diferente al del contrato: [" + $("#cIdRFC").val()  + "]");
						return false;
					}
				}
	
				var nretval = cmdGuardar();
				if (nretval == -1) {
					
					return false;
				}
	
	
				if(id_oper==2){
					$("#COMSOCAutoriza").val("");
					queryFormPost("BuscaCOMSOCAutorizaRead", {async : false});
					if ( $("#COMSOCAutoriza").val() == "0"){
						alert("Pago en Proceso de Autorización del Área Normativa");
						return false;
					}

					if ( $("#COMSOCAutoriza").val() == "-1"){
						alert("Pago fue Rechazado por el Área Normativa, <BORRAR>");
						return false;
					}

						$("#campo").val( "nFolio" + $("#cDocumento").val() );
						$("#tablaEnc").val( "t" + $("#cDocumento").val() + "Encabezado" );
						$("#campoCondicion").val( "caNoContrarrecibo" ); 
						$("#tablaDet").val( "t" + $("#cDocumento").val() + "Detalle" ); 
						$("#tipoAplicar").val( $("#cDocumento").val() );
						
						tipoFirmantes();
							
				}
			}
			catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}
		}
		return valida_campos;
  	}

	function procesar(){
		$( "#dialog-Procesando" ).dialog( "open" );
		return breturnVal;
	}
	
	function creaCasoComsoc(tipoDocumento, folioDocumentoPago){
		
		$.ajax({
			url : '../servlet/ComsocAutorizacionServlet',
			dataType : 'json',
			type :"POST",
			data : {
				"h_TipoPago" : tipoDocumento,
				"hFolioPago": folioDocumentoPago
			},
			async : false,
			success : function(json) {
				var exito = json.success;
				if( exito == "true"){
					alert("El Pago está en Proceso de Autorización por parte del Área Normativa");
				}
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("Advertencia: " + xhr.responseText + "\nEstatus: "
						+ textStatus + "\n" + errorThrown);
				return false;
			}
		});
		
	}


 	function fnAplicaMotor()
 	{
 		divAplica.innerHTML = "<iframe id='ifAplica' src='../gstnmngr/AppCont?elContra=" + $('#caNoContrarrecibo').val() + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
 	}

  	function onPostSubmit(id_oper){//validaciones del boton enviar
  		if(<%=id_oper == 1%>){ alert("Cuenta por Pagar: "+$("#caNoContrarrecibo").val()); } //URVP.22092014
	  		//valida que el documento de la Justificacion este agregado si se elimino la retencion
	  		queryFormPost("retencionEliminada", {async:false});
			queryFormPost("validaDoctoJustificacion", {async: false });
			
			if( $("#lEliminaRetencion6IVA").val() == 1 && $("#doctoCapturado").val() == 0){
	  			alert ("Debe adjuntar la documentacion que Justifique el pago sin retenciones");
	  			dlgJustificacion();	
	  			return false;	
	  		}	
  		return true;
  	}
  	
    function onLoadPlantilla(){
    	$("#verFondenSPAN").hide();
    	
    	queryFormPost("readBanderaDiversoRG", {async: false });
    	carga();
    	
    	if(<%=id_oper>1%>){
    		$("#EditaFacturas").css('visibility', 'hidden');
    		$("#ctaBancaria").attr('disabled', true);
    		$("#noRecepcion").attr('disabled', true);
    		$("#chk_radicado").attr('disabled', true);
    		$("#chk_IF").attr('disabled', true);
    	}
    	
		if(<%=id_oper == 1%>){
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=true;
		}
		if(<%=id_oper == 2%>){
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=true;
		}

		if(<%=id_oper == 3%>){
			if( muestraDivImprimePoliza ){
				$("#EditaFirmas").css('visibility', 'visible');
			}
			if( parent.document.getElementById("pb_cancel") ){
				parent.document.getElementById("pb_cancel").style.visibility='hidden';
				parent.document.getElementById("pb_cancel").disabled=true;
			}
						
		}
  	}
  	function ResponsableSiguiente(id_oper){

  		 if(id_oper==1){
  		 	return "AUTORIZA_" + $("#cDocumento").val();
		}
  		 if(id_oper==2){

  		 	return "CONSULTA_" + $("#cDocumento").val();
			}
  				
  	}
  	  	
  	function OperacionSiguiente(id_oper){

  		if(id_oper==1)
  		 	return "autoriza_factura";
  		if(id_oper==2){
		
  		 	return "consulta_factura";}
  	}

	function onPostDisplay(id_oper){
		if ($("#docAplicado").val() == "S") {
			parent.document.getElementById("pb_send").disabled = false;
			parent.document.getElementById("pb_send").click();
		}
			
	}
	
	function onCancel(id_oper){
		queryFormPost(	"TFactDiversoEncabezadoUpdate", {async: false });
		if ($("#docAplicado").val() != "S") {
			queryFormPost("borraFactRelacionPagoBorrado", {async: false });
			$("#estatusRecepcion").val("Emitida");
			$("#noRecepcion").attr('disabled', false);
			queryFormPost("updateEstatusRecepcion",{async: false });
			$("#noRecepcion").attr('disabled', true);
			$("#noPenalty").attr('disabled', true);
		}
		return true;
	}
	
	
	function fnClickAddRowComp() {
	
		dlgJustificacion();
		
		queryFormPost("leeImporteTotalDiversoEncabezado",{async: false });
		queryFormPost("leeImporteTotalDiversoDetalle",{async: false });		
		
		var importeDiferencia = 0.00;
		var importeDiferenciaOriginal = 0.00;
		importeDiferencia = Number(Number($("#importeTotalEncabezado").val())-Number($("#importeTotalDetalle").val())).toFixed(2);
		importeDiferenciaOriginal = Number(Number($("#importeTotalEncabezado").val())-Number($("#importeTotalDetalle").val())).toFixed(2);
		if (importeDiferencia<0)
			importeDiferencia*=-1;
		
		if (importeDiferencia <= 0.02){
			if (importeDiferencia > 0){
				$("#importeTotalDetalle").formatCurrency();
				if (!confirm("El Importe Neto se afectara contablemente por "+$("#importeTotalDetalle").val()+", ¿Desea continuar?")){
					alert("Favor de Borrar el tramite.");
					return;
				}
				var aux=0.00;
				aux = Number(Number($("#DCD_NETO").val())-Number(importeDiferenciaOriginal)).toFixed(2);
				$("#DCD_NETO").val(aux);
				aux = Number(Number($("#DCD_RETENCION").val())+Number(importeDiferenciaOriginal)).toFixed(2);
				$("#DCD_RETENCION").val(aux);
				$("#mImporteDiferenciaCentavos").val(importeDiferenciaOriginal);
				queryFormPost("updateEncabezadoNetoRetencionDiverso",{async: false });
			}
		}else{
			alert("No se puede seguir con el pago debido a que el detalle no corresponde al encabezado.");
			return;
		}
		
		$("#DCD_TIPO_OPE").attr('disabled', false);//URVP.17062014 se habilita para poder obtener su informacion y se deshabilita al finalizar de contrareibo()
	
		CalculaRetencionesG();		
		$('#grdFacturas').dataTable().fnAddData( [
		$("#DCD_FACTURA").val(),
		$("#DCD_FECHA_FACTURA").val(),
		$("#DCD_TBEN").val(),
		$("#DCD_CBEN").val(),
		$("#DCD_TIPO_OPE").val(),
		$("#DESCRIPCION20").val(),
		$("#DCD_IMP_BRUTO").val(),
		$("#DCD_IVADES").val(),
		$("#DCD_IVA").val(),
		$("#DCD_ISR").val(),
		$("#DCD_MIL5").val(),
		$("#DCD_MIL2").val(),
		$("#DCD_CONTRIBUCION").val(),
		$("#DCD_OTRAS_RET").val(),
		$("#DCD_PENALIZACION").val()
		] );
		
		$("#fRecepcion2").val($("#fRecepcion").val());
		getNextSequenceVal({seqName: "CR-" + $("#cCentroContable").val(), async: false, callback: setSequenceVal});
		setTimeout("contrareibo()",1000);
		
		parent.doSave();
		
		if (bCOMSOC){
			creaCasoComsoc($("#cDocumento").val(), $("#id_caso").val());	
		}
		$("#numPaso").val("5");
	}
	
function setSequenceVal(seqValue) {
	seqValue = "000000" + seqValue;
	seqValue = seqValue.substr(seqValue.length - 6);
	seqValue = "1" + seqValue.substr(seqValue.length - 5);
	seqValue = $("#cCentroContable").val() + cxpPrefijo + $("#aEjercicioFiscal").val() + seqValue;
	$("#caNoContrarrecibo").val( seqValue );
}

	function contrareibo(){
		$("#cIDContrato").attr('disabled', false);
		queryFormPost("obtieneIVAparaDocComp", {async: false });
		queryFormPost("SIG_FOLIO_CONTRAODIVERSOSUpdate", {async: false });
		queryFormPost("StatusAnticipoFACTDIVERSOUpdate", {async: false });		
		queryFormPost("tDocumentacionComprobatoriaDetCreate2",{async: false }); 
		$("#DCD_TIPO_OPE").attr('disabled', true);
		$("#divImprime").show();
		$("#Agregar1").attr('disabled', true);
		setTimeout("elRetardo()",1000);
		
	}					
	function fnClickAddRowB(A, B,C) {
		$('#tretencion').dataTable().fnAddData( [
				A,
				B,
				C] );
	}
	function fnClickAddRowH(A, B, C,D,E) {
		$('#grdCompromisos').dataTable().fnAddData( [A,B,C,D,E]);	
	}
				

	function Borrar1(){
		if ($("#docAplicado").val() != "S") {
			queryFormPost("borraFactRelacionPagoBorrado", {async: false }); //URVP.08092014 VALIDAR EN CASO DE YA APLICADOS
			queryFormPost("tPagoDiversoBoletajeAvionDelete", {async: false });
			queryFormPost("tTempVuelosEnCapturaDelete", {async: false });
			$("#estatusRecepcion").val("Emitida");
			$("#noRecepcion").attr('disabled', false);
			queryFormPost("updateEstatusRecepcion",{async: false });
			$("#noRecepcion").attr('disabled', true);
			queryFormPost("borraEstadoCuentaGreenMex",{async: false });
			
		}
		if (idOperacion == 2 ) {
			queryFormPost("readPenalty", {async: false });
		} else {
			$("#mPenalizacion2").val($("#noPenalty").val());
		}
		
		if ($("#mPenalizacion2").val() !="-1") {
			queryFormPost("actualizarEstatusPenasBorrar",{async: false });
		}
		
		$.blockUI({message : "Procesando espere..."});
		
		$("#elcontra").val($("#caNoContrarrecibo").val());

		$("#COMSOCAutoriza").val("");
		queryFormPost("BuscaCOMSOCAutorizaRead", {async : false});
		if ( $("#COMSOCAutoriza").val() == "0"){
			Swal.fire("Pago en Proceso de Autorización del Área Normativa", "No es posible Descarta el Documento","warning");
			return false;
		}

		if ( $("#COMSOCAutoriza").val() == "1"){
			Swal.fire("Pago Autorizado por el Área Normativa", "No es posible Descarta el Documento","warning");
			return false;
		}
		
		$.unblockUI();
		
		parent.document.getElementById("pb_send").disabled = true;
		parent.document.getElementById("pb_cancel").disabled = false;
		parent.document.getElementById("pb_cancel").click();
		parent.document.getElementById("pb_cancel").disabled = true;
		
		
	}
	
	
	function cmdRegresar(){
		self.location="../caso/inbox.jsp";
	}	
	
	function reten(){
	$("#cIDContrato").val($("#cFolioPAGODIVERSO").val());
		if ($("#cIDContrato").val()!=""){

			FiltroRetencionesD();
			$("#cIdRFC_RelacionGasto2").val($("#cIdRFC").val());			//quitar
			$("input[id='nIdConcepto']").val($('#TIPO_CONCEPTO option:selected').val());
			$("input[id='tConcepto2']").val($('#TIPO_CONCEPTO option:selected').text());
			$("input[id='noFactura2']").val($("input[id='cNoFactura']").val());
			$("input[id='cIdTipoOperacion2']").val($('#cIdTipoOperacion option:selected').val());
			$("input[id='tipoOper']").val($('#TIPO_OPERACION option:selected').val());
			$("input[id='impTotal']").val($("input[id='mImporteNeto']").val());
			
			$("#impTotal2").val($("#impTotal").val());
			$("#iva").val($("#nPorcIVAAplicable").val());
			$("#poriva").val($("#mImporteIVA").val());
			queryFormPost("tRelacionGastClaveBeneficiarioRead","cIdRFC_RelacionGasto2",{async: false });		 
			$(".pasoDos").show();
			$("#cIDContrato").attr('disabled', true);
			$(".paso01").attr('disabled', true);
			$(".subtotall").attr('disabled', true);
			$("#guardar").attr("disabled", true);
			$("#numPaso").val('2');
			//calculaIVA();
			DOCUMENTACION();
			claves();
			setTimeout("retraso3()",100);				
			setTimeout("retraso5()",100);	
			
			/* CONAFOR, No mostraba toda la informacion de Pestaña de Devengado*/
			$("#ID_DESTINO_GASTO").val( $("#destGasto").val() );
			querySelectPost("cat_TipoConceptoPagosDiversosRead", "TIPO_CONCEPTO",{async: false });
			concepto();
			$("#agrega2").attr('disabled', true);
			$("#TIPO_CONCEPTO").attr('disabled', true);
			$("#ALM").attr('disabled', true);
			
		}else{
			$(".pasoDos").hide();
			$(".pasoTres").hide();
		}
	}	
	
	function retraso5(){
		if ($("#cllave").val()!=""){
			$(".paso9").attr('disabled', true);
			//valida que no se vea la liga de captura de justificacion si ya ha sido agregada
			queryFormPost("retencionEliminada", {async:false});
			queryFormPost("validaDoctoJustificacion", {async: false });
		
			if( $("#lEliminaRetencion6IVA").val() == 1 && $("#doctoCapturado").val() == 0){
	  				Swal.fire ("Verifique","Debe adjuntar la documentacion que Justifique el pago sin retenciones","warning");
	  				dlgJustificacion();	
	  				return false;	
			}
			$(".pasoTres").show();	
		}
	}		
	function retraso3(){
		if ( $("#caNoContrarrecibo").val() !="0"  && (<%=id_oper%> == 2 )  ){
			queryFormPost("tRelacionGastClaveBeneficiarioRead","cIdRFC_RelacionGasto2",{async: false });	
			queryFormPost("tFacturaDiversoConsultaRead", {async: false });					  
			$("#Agregar1").attr('disabled', true);   
			$(".paso03").attr('disabled', true);   
			$("#numPaso").val('6');		

			Swal.fire("Finalizar","Para finalizar el tramite presiona el boton Guardar.","warning");
		}else{
			$(".paso03").attr('disabled', false); 
		}
	}	
	function DOCUMENTACION(){    	
		var szWhere = "";     
		szWhere = " caNoContrarrecibo ='"+  $("#caNoContrarrecibo").val()+"'";
		
		var elMonto = "";     
		var este=0;          
		var szTabla = "DOCUMENTACION_RELACION_GASTO";                                                                                         
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){                       
			var options = '';
			for (var i = 0; i < j.length; i++) {              			
				$('#grdFacturas').dataTable().fnAddData( [
				j[i].Col0, j[i].Col1, j[i].Col2,j[i].Col3,j[i].Col4,j[i].Col5,j[i].Col6,j[i].Col7,j[i].Col8,j[i].Col9,j[i].Col10,j[i].Col11,j[i].Col12,j[i].Col13,j[i].Col14	
				] );
				$("#DCD_FACTURA").val(j[i].Col0);
				$("#DCD_FECHA_FACTURA").val(j[i].Col1);
				$("#DCD_TBEN").val(j[i].Col2);
				$("#DCD_CBEN").val(j[i].Col3);
				$("#DCD_TIPO_OPE").val(j[i].Col4);
				$("#DESCRIPCION20").val(j[i].Col5);
				$("#DCD_IMP_BRUTO").val(j[i].Col6);
				$("#DCD_IVADES").val(j[i].Col7);
				$("#DCD_IVA").val(j[i].Col8);
				$("#DCD_ISR").val(j[i].Col9);
				$("#DCD_MIL5").val(j[i].Col10);
				$("#DCD_MIL2").val(j[i].Col11);
				$("#DCD_CONTRIBUCION").val(j[i].Col12);
				$("#DCD_OTRAS_RET").val(j[i].Col13);
				$("#DCD_PENALIZACION").val(j[i].Col14);		
				$("#DCD_NETO").val($("#mImporteNeto").val());				
				$("#DCD_SANCION").val($("#mImporteSancion").val());				
				$("#DCD_DEVOL").val($("#mImporteDevolucion").val());	
				$("#numPaso").val("4"); 
				
			}
			// Si Trae Informacion y es num Paso 4 
			if($("#numPaso").val() == "4" && <%=id_oper%> == 1){
			
				$("#numPaso").val("5");
				parent.document.getElementById("pb_send").disabled=false;//para CONAFOR
				parent.document.getElementById("pb_send").style.visibility='visible'; //para CONAFOR
				parent.document.getElementById("pb_save").disabled = true; //para CONAFOR
				parent.doSave();
				$("#divImprime").show(); //para CONAFOR*/
				$("#Agregar1").attr("disabled",true);
				
			}
		}) ;  
		
	}
	function  compDocumentacion(){
		$("#DCD_FACTURA").val( $("#cNoFactura").val());
		$("#DCD_FECHA_FACTURA").val($("#fRecepcion").val());
		$("#DESCRIPCION20").val($("#nPorcIVAAplicable").val());
		
		if(  $("#tipoAmortizacion").val() != "1"  )
			$("#DCD_IMP_BRUTO").val($("#mImporteBruto").val());
		else{
			var valImpBrutoDCD = ( parseFloat(  $("#mImporteBruto").val() - $("#mAmortizacionAnticipo").val() ) * 100 ) / 100;
			$("#DCD_IMP_BRUTO").val( valImpBrutoDCD.toFixed(2) );
		}
		
		$("#DCD_IVA").val($("#mImporteIVA").val());
		$("#DCD_NETO").val($("#mImporteNeto").val());				
		$("#DCD_SANCION").val($("#mImporteSancion").val());				
		$("#DCD_DEVOL").val($("#mImporteDevolucion").val());
		$("#DCD_CONCEPTO").val( $("#Descrip_Concepto").val());
		
		if(  $("#tipoAmortizacion").val() != "1"  )
			$("#DCD_AMORT").val( $("#mAmortizacionAnticipoMasIva").val() );
		else
			$("#DCD_AMORT").val( $("#mAmortizacionAnticipo").val() );
			
		$("#DCD_RETENCION").val($("#mImporteRetencion").val() );
		$("#DCD_CONTRIBUCION").val( $("#DCD_CONTRIBUCION_D").val() ); //urvp.06012014 se cambia por mImporteFlete4
		$("#DCD_PENALIZACION").val( $("#mImportePenalizacion").val() );
				
	}
	function claves(){     
	   
		var contador=0;                          
		var szWhere = "";     
		szWhere = " tr.nFolioPAGODIVERSO ='"+  $("#id_caso").val()+"'";
		var elMonto = "";     
		var este=0;          
		var szTabla = "CLAVES_DIVERSO";                                                                                         
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){                       
			var options = '';
			for (var i = 0; i < j.length; i++) {
				$("#montoDev").val(j[i].Col3);
				$("#montoDev").formatCurrency();
				var mImporteADev = $("#montoDev").val();
				$('#grdCompromisos').dataTable().fnAddData( [ j[i].Col0, j[i].Col2, mImporteADev ]);
				$("#montoDev").val("");
				contador = contador + 1;
				$("#cllave").val(contador);
				$("#numPaso").val("3"); // Para CONAFOR
				
			}
			
			if($("#numPaso").val() == "3" && <%=id_oper%> == 1 ){
				
				$("#nIdClaveEgresos2").attr('disabled', true);
				$("#agrega2").attr('disabled', true);
				$("#TIPO_CONCEPTO").attr('disabled', true);
				$("#ALM").attr('disabled', true);
				DOCUMENTACION();
				//valida que no se vea la liga de captura de justificacion si ya ha sido agregada
						queryFormPost("retencionEliminada", {async:false});
						queryFormPost("validaDoctoJustificacion", {async: false });	
							if( $("#lEliminaRetencion6IVA").val() == 1 && $("#doctoCapturado").val() > 0 || ("#lEliminaRetencion6IVA").val() == 0 ){
					  				$("#justificacion").hide();
								
							}
				$(".pasoTres").show();
			}
			
		});  	
	}			  

	function cambioss(){
		$('#grdRetClave').dataTable().fnClearTable();
		$("#EP").val( $("#ep").val() );
		var vEp = $("#EP").val();
		var vCartera = vEp.substring(44, 55);
		var vCapitulo = vEp.substring(31, 32);
		if(vCartera == "00000000000" || "1234".indexOf(vCapitulo) >= 0 || $("#IEsPasivo").val() == "1"){
			cambioMovmientos();
		}else{
			modalOli.show();
		}			
	}
	
	function cambioMovmientos(){
		$("#montoDev").val(quitaFmt($("#montoDev").val())); //URVP.20102014 Se quita formato para evitar error de conversion en caso de traer signos
        var vOGT = $("#ep").val();
	    vOGT = vOGT.substring(31, 36);
		$("#vOGT").val( vOGT );

		queryFormPost("tMsgPartidaRead", {async: false });
		if( $("#obs").val() != '' ){
			alert($("#obs").val());
		}

		var validM;
		var validacion=0;				
		validM = Number( $("#saldoCompM").val() ) + Number( $("#montoDev").val() );	
		validM = validM.toFixed(2);
		
		if ( Number( validM ) >  Number( $("#mImpEjercer").val()) ){
			Swal.fire("Error","Ha superado el Importe a Ejercer","error");
			$("#montoDev").val("");
			$("#ep").val("");
			$("#codSIAFF").val("");
			return;
		}
		
		validM = Number( $("#montoDev").val() );						                           
		
		var campos="";
		var szWhere="";
		var elMonto=12;
		
		if(validaEsIngresosPropios()){			
			$("#cMes").val(12);			
		}else{		
			$("#cMes").val($("#fRecepcion").val().split("/")[1]);
		}

		var orderBy = " order by 1 desc ";
		if( $("#chk_IP").prop("checked") ){
			orderBy = " order by 1 ASC";
		}
		 
		campos = $("#cMes").val() + ", '" + $("#cIdContrato").val() + "', '" + $("#cCentroContable").val() + "', 'DI', '" + $("#ep").val() + "'";
		szWhere = "";
        var szTabla = "SALDOS_DISPONIBLE_PAGOOBRA";
        
        if(validaEsIngresosPropios()){
        		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto,Campos:campos, Order: orderBy, ajax: 'false'}, function(j){
					var acumulado = 0.0;
					for (var i = 0; i < j.length; i++) {            
						acumulado = parseFloat(acumulado) + parseFloat(j[i].Col2);
						$("#TOTALSUBCUENTA").val(acumulado);
					}
										
				   	acumulado = acumulado.toFixed(2);
		
				   	if (acumulado < validM){
						alert('La cuenta no tiene suficiente saldo comprometido');
						return;
					}	
					var resto = parseFloat(validM);
					var aplicar = 0.0;
					for (var i = 0; i < j.length; i++){
						$("#nMes").val( j[i].Col0 );						
						var SuficMes = parseFloat(j[i].Col2); 
						SuficMes = SuficMes.toFixed(2);
						if (SuficMes > 0){
							if (resto > SuficMes ){
								resto -= SuficMes;
								aplicar = SuficMes;
								$('#grdRetClave').dataTable().fnAddData([j[i].Col1, j[i].Col0, aplicar]);
							}else{
								aplicar = resto;
								$('#grdRetClave').dataTable().fnAddData([j[i].Col1, j[i].Col0, aplicar]);
								i = j.length;
							}														
							
							var vOGT = $("#ep").val();
							vOGT = vOGT.substring(31, 36);
							$("#vOGT").val( vOGT ); 
							queryFormPost("BuscaPartidaExcepRead", {async : false});
							
							if ($("#TIPO_CONCEPTO").val() != "AL"){
								queryFormPost("BuscaPartidaExcepRead", {async : false});
								$("#altaAlmacen").val("");
								$("#cAnioFactEP").val("");
								$("#nFacturaEP").val("");
									
								if (vOGT == $("#partida").val()){
									querySelectPost("CatalogoAlmacenRead","ALM", {async: false });									
								} else{
									$("#altaAlmacen").hide();
									$("#cAnioFactEP").hide(); 
									$("#nFacturaEP").hide();			
									querySelectPost("CatalogoAlmacenVacioRead","ALM", {async: false });
								}
							}
						}
					}
				}) ;
        }else{
        
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto,Campos:campos, Order: orderBy, ajax: 'false'}, function(j){
				var acumulado = 0.0;
				for (var i = 0; i < j.length; i++) {            
					acumulado = parseFloat(acumulado) + parseFloat(j[i].Col2);
					$("#TOTALSUBCUENTA").val(acumulado);
				}	

				acumulado = acumulado.toFixed(2);
	
			   	if (acumulado < validM){
					alert('La cuenta no tiene suficiente saldo comprometido');
					return;
				}	
				var resto = parseFloat(validM);
				var aplicar = 0.0;
				for (var i = 0; i < j.length; i++){
					$("#nMes").val( j[i].Col0 );
					var SuficMes = parseFloat(j[i].Col2); //- parseFloat( $("#montoprevio").val() );
					SuficMes = SuficMes.toFixed(2);
					if (SuficMes > 0){
						if (resto - SuficMes > 0){
							aplicar = SuficMes;
						}else{
							aplicar = resto;
						}
						resto = resto-aplicar;
						if (parseFloat(aplicar) > 0.0) {
							$('#grdRetClave').dataTable().fnAddData([j[i].Col1, j[i].Col0, aplicar]);
						}

						var vOGT = $("#ep").val();
						vOGT = vOGT.substring(31, 36);
						$("#vOGT").val( vOGT ); 
						queryFormPost("BuscaPartidaExcepRead", {async : false});
						
						if ($("#TIPO_CONCEPTO").val() != "AL"){
							queryFormPost("BuscaPartidaExcepRead", {async : false});
							$("#altaAlmacen").val("");
							$("#cAnioFactEP").val("");
							$("#nFacturaEP").val("");
								
							if (vOGT == $("#partida").val()){
								querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
								
							} else{
								$("#altaAlmacen").hide();
								$("#cAnioFactEP").hide(); 
								$("#nFacturaEP").hide();			
								querySelectPost("CatalogoAlmacenVacioRead","ALM", {async: false });
							}
						}
					}
				}
			}) ;
		}  
	}
			
	function FiltroMovimientos(){
		$("#montoDev").val(quitaFmt($("#montoDev").val()));
		if ($("#ep").val()!=""){
			var monto = $("#montoDev").val();
			if (parseInt(monto.indexOf("."),10) != parseInt(monto.lastIndexOf("."),10) //URVP.16062014-Validacion de mas de un punto decimal
				|| parseInt(monto.indexOf("-"),10) != parseInt(monto.lastIndexOf("-"),10)
				|| parseInt(monto.indexOf("-"),10)>0 
				|| monto=="-" 
				|| monto=="-."
				|| monto ==""
				|| monto =="."
			){ 
				Swal.fire("Verifique","Revise que el importe sea correcto", "warning");
				return;
			}
			if (parseFloat($("#montoDev").val()).toFixed(2)<0){
				Swal.fire("Verifique!","No se puede agregar un movimiento con importe Negativo", "warning");
				return;
			}
			else if (parseFloat($("#montoDev").val()).toFixed(2)==0){
				Swal.fire("Verifique!","No se puede agregar un movimiento con importe 0", "warning");
				return;
			}
			
			var esEPCap5Mil = $("#ep").val();
			esEPCap5Mil = esEPCap5Mil.substring(31, 32);
			if(esEPCap5Mil != "5"){
				//se valida si la ep capturada pertene a CECFOR y el tipo de concepto es "ALMACEN".
				if(!validaEPsCECFOR()){
					$("#esEPCECFOR").val("");
					return;
				}
			}
		}	
		else{
			Swal.fire("Seleccione","Favor de seleccionar la EP","warning");
		 	return;
		}
		
var msg = "";
	try{
		msg = registraCalendarioEP($("#ep").val(), monto);
	}catch(err){
		msg = "ocurrio el siguiente error generando calendario: " + err.message;
	}
	
	if( msg != ""){
		Swal.fire("Verifique", msg ,"error");
		return false; 
	}
		//mostrar mensaje cuando la partida sea de gastos de representacion o viaticos.
		validaPartidaGtosRepresentacion();
		
		//Mostrar el control de captura de numero de acompañantes en caso de que la partida sea la 38501
		if(!mostrarAcompanantes()){
			return;
		}
		
		//Actualizar el numero de acompañantes en caso que hayan seleccionado la partida 38501
		if(!updateAcompañantes()){
			return;
		}
		
		$("#montoDev").val(parseFloat($("#montoDev").val()).toFixed(2));//URVP.17062017 se deja el campo a dos decimales debido a que en el grid se mostraba dos decimales en BD si registraba todos los decimales que estaban 

		var vOGT = $("#ep").val();
		vOGT = vOGT.substring(31, 36);
		$("#cOBGT").val(vOGT);
		
		if ($("#TIPO_CONCEPTO").val() == "AL" || vOGT == $("#partida").val()){
			if ($("#altaAlmacen").val() == ""){
				Swal.fire("Capturar","Falta Capturar el Alta Almacenaria","warning");
				return;
			}
			
			if ($("#cAnioFactEP").val() == ""){
				Swal.fire("Capturar","Falta Capturar el Año de la Factura","warning");
				return;
			}
			
			if ($("#nFacturaEP").val() == ""){
				Swal.fire("Capturar","Falta Capturar el Número de Factura","warning");
				return;
			}
			
		}

		$("#nombre").val($("#cobjetocontrato").val());
		$("#DCD_IMP_BRUTO").val($("#mImporteBruto").val());	
		
		document.getElementById("TIPO_CONCEPTO").removeAttribute("disabled",false);//SE HABILITA PARA TOMAR EL DATO
		$("#cEvento").val( "" );
		queryFormPost("eventoRGDiversos", {async: false }); //eventpEP3Read
		if ($("#cEvento").val() =="" ){
			Swal.fire("No hay evento",'La cuenta no corresponde al concepto',"error");
			return;
		}
		
		var mImporteADev = $("#montoDev").val();

		// validación de facturas para tipo destino almacen
		if(  $("#destGasto").val() == "AL" ){
			var cFactura = $("#nFacturaEP").val();
			$("#cValidaFactura").val( $("#nFacturaEP").val() );
			$("#cExiste").val( "0" );
			queryFormPost("ValidaFactPagosRead", { async: false});
			if( Number( $("#cExiste").val() ) > 0){
				Swal.fire("Cargue otra factura","La Factura " + cFactura + " ya existe","error");
				return -1;
			}else{
				var aData = oTablevFact.fnGetData();
				var bExiste = false;
				for(var i=0; i<aData.length; i++) {
					var cFactura = aData[ i ][ 1 ];
					if( cFactura == $("#nFacturaEP").val() ){
						bExiste = true;
					}
				}
				if ( !bExiste ){
					$('#grdValidaFacturas').dataTable().fnAddData( [ "", $("#nFacturaEP").val(), $("#montoDev").val()] );	
				}				
			}
		}
		
		if(validaSaldoIP){
			if(validaEsIngresosPropios()){ 
				if($("#destGasto").val() != "CPGM"){	
					if(!validaDispIngresoPropio()){
						Swal.fire("Verifique el importe","El Monto capturado para la EP supera el Importe de Ingresos Propios.","error");
						return;				
					}
				}
			}
		}

		$("#cEvento").val("D_"+$("#cEvento").val());
        var mTotalEjercer = parseFloat( $("#saldoCompM").val() );
		var table = document.getElementById('grdRetClave');
		var rowCount = table.rows.length;            		
		
		for(var i=1; i<rowCount; i++) {
			
			var row = table.rows[i];                		
			var elMesGrd = '';
			var elMontoGrd = '';
			
			try{
				var elMesGrd = row.cells[1].innerHTML;
				var elMontoGrd = row.cells[2].innerHTML;
				var mess;
			}catch(e){null;} 
			
			if(null != elMesGrd ) {
			
				if(elMesGrd.toString() != ""){
				
					if (parseInt(elMesGrd.toString(),10)<10)
						mess='0'+elMesGrd.toString();
					else
						mess=elMesGrd.toString();
					
					$("#cMes").val(mess);
					
					var vdcd_imp_bruto = Number( elMontoGrd.toString() );
					vdcd_imp_bruto = vdcd_imp_bruto.toFixed(2);
					
					$("#DCD_IMP_BRUTO").val( vdcd_imp_bruto );
					
					
					$("#montoDev").formatCurrency();
					$('#grdCompromisos').dataTable().fnAddData([$("#codSIAFF").val(),$("#ep").val(),$("#DCD_IMP_BRUTO").val()]);					
					$("#montoDev").val( mImporteADev );
					
					if ($("#lHaySaldoAnticipo").val() == 0  || $("#mAmortizado").val() != '0.0000'){			
						calculaRetencionesEP ( $("#DCD_IMP_BRUTO").val() );
						actualSaldosDet(1);
					}else{
				
						$("#mImporteNetoEP").val( $("#DCD_IMP_BRUTO").val());
					}
					
					var altaAlmacenresp = $("#altaAlmacen").val();

					mTotalEjercer = Number( mTotalEjercer ) + Number( elMontoGrd.toString() ) ;
					mTotalEjercer = mTotalEjercer.toFixed(2);
					if ( Number( mTotalEjercer ) == Number( $("#mImpEjercer").val() ) ){
						AjustaRetencionesD();
					}

					$("#altaAlmacen").val( $("#altaAlmacen").val() + "|" + $("#cAnioFactEP").val() + "|" +$("#nFacturaEP").val() );
					
					if (esIvaArrenda==1){
						$("#mImporteIvaArrenda").val($("#mImporteFlete23").val());
						$("#mImporteFlete23").val(0);
					}else{
						$("#mImporteIvaArrenda").val(0);
					}
					
					var aux=$("#mAmortizacionAnticipoMasIvaEP").val();
					var mAmortizacionEP = 0.00;
					mAmortizacionEP = $("#mAmortizacionAnticipo").val();
					var porcent = (vdcd_imp_bruto * 100 / Number($("#mImpEjercer").val()).toFixed(2));
					mAmortizacionEP =  Number(mAmortizacionEP * porcent / 100).toFixed(2);
					$("#mAmortizacionAnticipoMasIvaEP").val(mAmortizacionEP);
					
					/*AMORTIZACION POR FACTOR*/
					if ($("#tipoAmortizacion").val() == "1"){
						var dcdImpBruto = (  parseFloat( vdcd_imp_bruto ) +  parseFloat( mAmortizacionEP )  ) * 100 / 100;
						$("#DCD_IMP_BRUTO").val( parseFloat( vdcd_imp_bruto ) );
						mAmortizacionEP = Number (mAmortizacionEP * 1.16).toFixed(2);
						$("#mAmortizacionAnticipoMasIvaEP").val(mAmortizacionEP);
					}
					
					var otrosImpuestosEp = 0.00;
					otrosImpuestosEp = $("#mOtrosImpuestos").val();
					otrosImpuestosEp = Number(otrosImpuestosEp * porcent / 100).toFixed(2); 
					$("#mOtrosImpuestosEP").val(otrosImpuestosEp);
					
					var mPenalizacion = 0.00;
					mPenalizacion = $("#mImportePenalizacion").val();
					
					if(mPenalizacion > 0){
						$("#mPenalizacion").val("0");
					}
					
					queryFormPost("tFACTDIVERSOSDETALLECreate",{async: false });
					
					//se inserta otro renglo cuando es un pago con penalizacion				
					if(mPenalizacion > 0){
						
						mPenalizacion = Number(mPenalizacion * porcent / 100).toFixed(2);
						$("#mPenalizacion").val(mPenalizacion);
						
						queryFormPost("tPAGODIVERSODetallePenalizacionCreate", {async : false});
						
						$("#mPenalizacion").val("0");
					}
					
					$("#mAmortizacionAnticipoMasIvaEP").val(aux); //se regresa el valor que traia
					$("#altaAlmacen").val( altaAlmacenresp );
					table.deleteRow(i);
					rowCount--;
					i--;
					$("#numPaso").val("4");  // Para Conafor
					parent.document.getElementById("pb_save").disabled = true;
					
					if ($("#cEsIngresoFiscal").val()=="S"){
						guardaIngresoPago();
					}
					else if($("#cEsRadicado").val()=="S")
					{
						guardaIngresoPago();
					}
				}
				else
				{
					$("#montoDev").val('0');
					Swal.fire("Verifique","Favor de verificar que la EP tenga saldo comprometido para el Pago.","error");
					return;
				}
			}		
		}// FOR
		
		validacion=	parseFloat( $("#saldoCompM").val() ) + parseFloat( $("#montoDev").val());
		validacion = validacion.toFixed(2);
		if (validacion  > parseFloat( $("#mImpEjercer").val()) ){
			$("#montoDev").val("0");
			validacion=0;
		}else{
			var totalacumulado = parseFloat( $("#saldoCompM").val() ) + parseFloat( $("#montoDev").val());
			totalacumulado = totalacumulado.toFixed(2);
			$("#saldoCompM").val( totalacumulado );
			validacion=0;
			
			if ( parseFloat( $("#saldoCompM").val() ) == parseFloat( $("#mImpEjercer").val()) ){
				
				if ( $("#destGasto").val() == "AL" ){	
					var aData = oTablevFact.fnGetData();
					for(var i=0; i<aData.length; i++) {
						var cFactura = aData[ i ][ 1 ];
						var cImporte = quitaFmt( aData[ i ][ 2 ].toFixed(2) );
						$("#cValidaFactura").val( cFactura );
						$("#mImporteBrutoF").val( cImporte );
						queryFormPost("tPagoFacturaCreate", { async: false});
					}
				}

				var actualiacionCorrecta = actualizaRetenciones();
				if (actualiacionCorrecta) {				
					$("#montoDev").val("");
					$("#ep").val("");
					$("#codSIAFF").val("");
					$(".pasoTres").show();
					
					$("#nIdClaveEgresos2").attr('disabled', true);//URVP
					
					$("#agrega2").attr('disabled', true);//URVP
					$("#TIPO_CONCEPTO").attr('disabled', true);//URVP
					$("#ALM").attr('disabled', true);//URVP
					
					compDocumentacion();
					$("#DCD_TIPO_OPE").attr('disabled', true);//URVP
					$("#L04").click();									 
				}									 
			}
		}
		$("#montoDev").val("");
		$("#ep").val("");
		$("#codSIAFF").val("");
		
		if ($("#TIPO_CONCEPTO").val() != "AL"){
			if (vOGT == $("#partida").val()){
				$("#altaAlmacen").val("");
				$("#altaAlmacen").hide();
				$("#cAnioFactEP").val("");
				$("#nFacturaEP").val("");
				$("#cAnioFactEP").hide(); 
				$("#nFacturaEP").hide();
				querySelectPost("CatalogoAlmacenVacioRead","ALM", {async: false });
			}
		}

		$("#partCOMSOC").val("");
		queryFormPost("BuscaPartidaCOMSOCRead", {async : false});
		if (vOGT == $("#partCOMSOC").val()){
			bCOMSOC = true;
			alert( $("#cMsjCOMSOC").val() );
		}
		$("#TIPO_CONCEPTO").attr('disabled',true);//SE DESHABILITA NUEVAMENTE	
	}
	
	
	function guardaIngresoPago(){
		
		var table = document.getElementById("arrIngresoPago");
		var totalIngresoPago = 0;

		queryFormPost("buscaIngresoPago", {async:false});
		
		if ($("#nExisteIngresoPago").val() == "0"){
			queryFormPost("encabezadoIngresoPago", {async:false});
			queryFormPost("buscaIngresoPago", {async:false});
		}
		else{
			queryFormPost("getTotalIngresoPago",{async:false});
			totalIngresoPago = parseFloat($("#mTotalIngresoPago").val());
		}
		
		for( i = 0; i < relaciones.length; i++ ){
			
			if( relaciones[i].folioIngreso ){	
							
				$("#nExisteIngresoPago").val(); // nFolioIngresoPago
				$("#nFolioRegistroIngreso").val(relaciones[i].folioIngreso); //nFolioRegistroIngreso
				$("#cEPIngresoPago").val(relaciones[i].EP);//EP
				$("#nMesIngresoPago").val(relaciones[i].mes);//nMes
				$("#mImporteIngresoPago").val(relaciones[i].monto);//mImporte
				
				totalIngresoPago = totalIngresoPago + parseFloat(relaciones[i].monto);
		
				queryFormPost("detalleIngresoPago", {async:false});
			}
		}
		
		$("#mTotalIngresoPago").val(totalIngresoPago.toString());
		queryFormPost("updateIngresoPago",{async:false});		
		
		relaciones.length=0;
	}
		
	var TotalNeto=0;
    var	TotalRetenciones=0;
    
    

	function actualizaretencion(){
				
						elParametro = $("#campoRetencion").val()+ " = "+ $("#campoRetencion").val() +" + " + $("#TotalRetenciones").val()
						+" where  nFolioPAGODIVERSO = "+$("#id_caso").val()+ " AND cEvento !='ANTICIPO_DIV' AND nDocRenglon=1 " ;

		$.getJSON("../catalogos/InsertJson.jsp",{Tabla: "ACTUALIZARETENCIONDIVERSOS", Param: elParametro, MaxReg: "", ajax: 'false'}, function(j){});
	}
	
	
	function carga(){
		queryFormPost("tEjercicioRead",{async: false });
		$("#cEjercicio").val( $("#aEjercicioFiscal").val() );
		
		$("#cRamo").val( "<%=cRamo%>" );
		$("#cUnidadResponsable").val( "<%=cUR%>" );
		$(".divAutorizar").hide();
		$("#divImprime").hide();
		$("#divImprimePoliza").hide();
		$("#divImprimeAnexo").hide();				

		var con=document.getElementById("TO_TIPO_DOCTO").value;
		querySelectPost("CatalogoTipoRfcRead","tipoBenef", {async: false });
		querySelectPost("CatalogoDestinoGastoRelGastosDiversos","destGasto", {async: true }); //URVP.13012014 se cambia el crud por uno de RG con OC ("CatalogoObraDGastoRead")		
		$("#ID_DESTINO_GASTO").val( $("#destGasto").val() );
		querySelectPost("cat_TipoConceptoPagosDiversosRead","TIPO_CONCEPTO", {async: false });
		querySelectPost("CAT_TIPO_OPERACIONRead", "TIPO_OPERACION",{async: false });
		$('#TIPO_OPERACION option[value="1"]').attr('selected','selected'); //URVP.14102014 SE DEJA POR DEFAULT LA OPCION DE PAGO NORMAL VAL=1
		querySelectPost("ImporteTotalDiversoRead","cIDContrato", {async: false });
		queryFormPost("fRecepcion2Read", {async: false });
		queryFormPost("fRecepcionRead", {async: false });
		queryFormPost("TipoPolizaRead", {async: false });		

		if ($("#aEjercicioFiscal").val() >= '2013'){
            $("#fRecepcion").val( "<%=today%>" );
            $("#fecha_Pago").val( "<%=today%>" );

			if ( $("#fRecepcion").val().split("/")[2] != $("#aEjercicioFiscal").val() ) {
            	$("#fRecepcion").val( "31/12/" + $("#aEjercicioFiscal").val() );
            	$("#FECHA_CARGA").val( $("#fRecepcion").val() );
            }

			$("#fRecepcion").attr( "readonly","readonly" );
		}

		ponCerosD();
		 if (<%=id_oper%> == 2 || <%=id_oper%> == 3){
			
			 queryFormPost("tFacturaDiversoRead", {async: false });
			 retraso3();
			 querySelectPost("readCtaBancxPago", "ctaBancaria",{async: false });

		}
		
		//concepto();
		BuscaPoliza();
		 if (<%=id_oper%> ==3 ){
			   $(".divAutorizar").hide();
			   if( muestraDivImprimePoliza ){
				   $("#divImprime").show();
				   $("#divImprimePoliza").show();
				   $("#divImprimeAnexo").show();		 
			   }
			   
		  	   $("#Agregar").attr('disabled', true);
			   $("#Agregar1").attr('disabled', true);				   
			   $("#Borrar").attr('disabled', true);
		 }	
		
		 if (<%=id_oper%> == 1 ){
		 	setTimeout("informacionDocumento()",100);
		 }
	}
	
	function informacionDocumento(){	
 
		  /** Informacion que se Perdia al Cambiar de Pestaña Documento a Adjunto **/
		 	queryFormPost("tFacturaDiversoRead", {async: false });
		  
		 	if($("#numPaso").val() == "1" && $("#caNoContrarrecibo").val() != ""){
			 	$(".pasoDos").hide();
		   		$(".pasoTres").hide();
			 	$(".pasoCuatro").hide();
			 }
			 
			 if($("#numPaso").val()!="1"){
			 	querySelectPost("readCtaBancxPago", "ctaBancaria",{async: false });
			 	$("#ctaBancaria").attr('disabled', true);
			 }
			 
		 // Trae Las Ep Detalle Clave
		 	var contador=0;                          
			var szWhere = "";     
			szWhere = " tr.nFolioPAGODIVERSO ='"+  $("#id_caso").val()+"'";
			var elMonto = "";     
			var este=0;          
			var szTabla = "CLAVES_DIVERSO";                                                                                         
			
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){                       
				var options = '';
				
				for (var i = 0; i < j.length; i++) {
					$("#montoDev").val(j[i].Col3);
					$("#montoDev").formatCurrency();
					var mImporteADev = $("#montoDev").val();
					$('#grdCompromisos').dataTable().fnAddData( [ j[i].Col0, j[i].Col2, mImporteADev ]);
					$("#montoDev").val("");
					contador = contador + 1;
					$("#cllave").val(contador);
					$("#numPaso").val("3"); // Para CONAFOR
					$("#TIPO_CONCEPTO").val(j[i].Col4); 
					$("#TIPO_MOVIMIENTO").val(j[i].Col5); 
					 
				}
				
				// Si trae Informacion 
				if($("#numPaso").val() == "3"){  
					
					$("#nIdClaveEgresos2").attr('disabled', true);
					$("#agrega2").attr('disabled', true);
					concepto();
					$("#ID_DESTINO_GASTO").val( $("#destGasto").val() );
					querySelectPost("cat_TipoConceptoPagosDiversosRead", "TIPO_CONCEPTO",{async: false });
					$("#TIPO_CONCEPTO").attr('disabled', true);
					$("#ALM").attr('disabled', true);
					DOCUMENTACION();
					$(".pasoTres").show();
				}
				
				if($("#numPaso").val() == "2"){
					
					concepto();
					
		  		 	$("#ID_DESTINO_GASTO").val( $("#destGasto").val() );
		  		 	querySelectPost("cat_TipoConceptoPagosDiversosRead", "TIPO_CONCEPTO",{async: false });
					
					setTimeout("concepto()",1000);
				 	$(".pasoTres").hide();
				 	$("#DCD_TIPO_OPE").attr('disabled', true); // Desabilito Pestaña Detalle Oper
					$("#L03").click(); // Muestro Pestaña Devengado
					parent.document.getElementById("pb_save").disabled = true;
								 
				 }
				
				if($("#numPaso").val() == "3"){
					
					parent.document.getElementById("pb_save").disabled = true; //para CONAFOR
				}
				// Solo para la pestaña financiamiento
		  	 
				
				 if($("#numPaso").val() == "3" || $("#numPaso").val() == "6"){
				 
				 	$("#TFONDO").attr('disabled', true);
				 
				 }
				
					// numPaso a 5 Para que pueda seguir con el proceso
				 
				 if($("#numPaso").val() == "6" && $.trim($("#docAplicado").val()) == ""){
				 
				 	$("#numPaso").val("5");
				 }
					
			 
			$("#cIDContrato").val($("#cFolioPAGODIVERSO").val());
			
			if ($("#cIDContrato").val()!=""){
				
				$("#nPorcAmortizacion").attr('readOnly', true);//URVP
				$("#mAmortizacionAcumulado").attr('readOnly', true);//URVP
				$("#btncIDContrato").attr('disabled', true);//URVP
				$("#destGasto").attr('disabled', true);//URVP
				$("#TIPO_OPERACION").attr('disabled', true);//URVP
				$("#cNoFactura").attr('readOnly', true);//URVP
				$("#mImporteSancion").attr('readOnly', true);//URVP
				$("#mImporteDevolucion").attr('readOnly', true);//URVP
				$("#mAmortizacionAnticipo").attr('readOnly', true);//URVP
				$("#mImportePenalizacion").attr('readOnly', true);//URVP
				$("#saldoAnticipo").attr('readOnly', true);//URVP
				$("#Descrip_Concepto").attr('readOnly', true);//URVP
				$("#mImporteBruto").attr('readOnly', true);//URVP
				cssReadOnly();
				
				$("#nPorcAmortizacion").attr('disabled', true);
				$("#mAmortizacionAcumulado").attr('disabled', true);
				
			//leer la Entidad de la EP, Esto para el caso de los estados de Nayarit (poder identificar para calcular correctamente el impuesto cedular)
			$("#cEsNayarit").val("0");
			queryFormPost("buscaEntidad_Nayarit_Read", {async: false });
			/// Retenciones
					
			$("#cIdContrato").val( $("#cIDContrato").val() );
			szWhere = " cidcontrato ='"+ $("#cIDContrato").val()+"' AND cIdEntidadContable = '" +  $("#cCentroContable").val() + "'";
			if ($("#llevaAnticipo").val()=="1" && $("#tieneAnticipo").val()=="0") {szWhere = szWhere + " AND 1=2";}
			var elMonto = "100";
			var este=0;
			var retencion=0;
			elMonto = $("#mTotal").val();
			var szTabla = "CALC_RETENCIONES_FACT_CONT_DIVERSO";
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
					var options = '';
					var porretencion=0;
					for (var i = 0; i < j.length; i++) {
							//URVP.27112014 Se valida si es impuesto cedular y nayarit, se cambia el porcentaje de la retencion a 1.5%
							if (j[i].Col0=="9" && ($("#cCentroContable").val()=="28" || $("#cEsNayarit").val() == "1")){
								var porcentCedular = 0.00;
								porcentCedular = Number(Number(j[i].Col2) + 0.005).toFixed(6);
								fnClickAddRowB(j[i].Col0, j[i].Col1, porcentCedular);
							}
							else{
								fnClickAddRowB(j[i].Col0, j[i].Col1, j[i].Col2);
							}
							porretencion=este*elMonto;
							
							if (j[i].Col0 > 10 && j[i].Col0 < 16 ){ //URVP.04092014 SE CAMBIA LA CONDICION == 0 A >10 YA QUE SE DESGLOZARON EN MAS RETENCIONES Y AS POSTERIORES AL ID 10 SON LAS EQUIVALENTES A LA 0 DEL 2/3 DEL IVA
								$("#DCD_IVADES").val( 1);
								if ($("#lHaySaldoAnticipo").val() == 0 || $("#mAmortizado").val() != '0.0000' ){///1 espara cuando hay anticipo
									mImporteFlete23=1;//alo
											
								}

							}else if (j[i].Col0 == 2 ){
								$("#DCD_MIL2").val( parseFloat (j[i].Col2));
				
								if ($("#lHaySaldoAnticipo").val() == 0 || $("#mAmortizado").val() != '0.0000' ){
										CamInst=parseFloat (j[i].Col2);
										CamInst2=parseFloat (j[i].Col2);
										}
									DCD_MIL2res=parseFloat (j[i].Col2);
							
							}else if (j[i].Col0 == 3 ){
									$("#DCD_MIL5").val( parseFloat (j[i].Col2));
									if ($("#lHaySaldoAnticipo").val() == 0 || $("#mAmortizado").val() != '0.0000' ){	
										$("#mObra5").val( parseFloat (j[i].Col2) );
										$("#mObra52").val( parseFloat (j[i].Col2) );
									}
									
									DCD_MIL5res	= parseFloat (j[i].Col2	);	
					
							}else{
								if ($("#lHaySaldoAnticipo").val() == 0  || $("#mAmortizado").val() != '0.0000'){///1 espara cuando hay anticipo
								
									if (j[i].Col0 == 4 ){
										$("#mISRHonorarios").val( parseFloat (j[i].Col2));
										$("#mISRHonorarios2").val( parseFloat (j[i].Col2));						
									}else if (j[i].Col0 == 5 ){
										$("#mImporteFlete4").val( parseFloat (j[i].Col2));
										$("#mImporteFlete42").val( parseFloat (j[i].Col2));						
									}else if (j[i].Col0 == 6 ){						
											$("#mISRArrenda").val( parseFloat (j[i].Col2));		
											$("#mISRArrenda2").val( parseFloat (j[i].Col2));						
									}else if (j[i].Col0 == 7 ){						
										$("#mTesofe").val( parseFloat (j[i].Col2) );	
										$("#mTesofe2").val( parseFloat (j[i].Col2) );						
									}else if (j[i].Col0 == 9 ){	
										if ($("#cCentroContable").val()=="28" || $("#cEsNayarit").val() == "1"){				
											$("#mRetImpuestoCedular").val(porcentCedular);
											$("#mRetImpuestoCedular2").val(porcentCedular);
										}else{
											$("#mRetImpuestoCedular").val( parseFloat (j[i].Col2));
											$("#mRetImpuestoCedular2").val( parseFloat (j[i].Col2));
										}
									}	
								}
								if (j[i].Col0=="9" && ($("#cCentroContable").val()=="28" || $("#cEsNayarit").val() == "1")){ //dejar en igual a 28 para que en nayarit sea 1.5%
									
									$("#porcCedular").val("1");
									
								}else{
									retencion= parseFloat(retencion) + parseFloat(j[i].Col2);
								}
							}
							$("#sumareten").val(retencion);
							DCD_RETENCIONres=retencion;
						}
					});	
						

			}
					
		});  	
		 
			 compDocumentacion();
			 
			 if($("#numPaso").val() == "5"){
				 
				 $("#L04").click();
				 
			 }			 
}
	
	function clearSelect(idSel) {
		for ( var i = 0; i < idSel.length; i++)
			$('#' + idSel[i]).find('option').remove().end().append(
					'<option value="-1"></option>');
	}
	
	function concepto(){
		$("#ID_DESTINO_GASTO").val( $("#destGasto").val() );
		querySelectPost( "CatalogoObraTMovimendoRead", "TIPO_MOVIMIENTO",{async: false });
		concepto2();
	}
	
	function concepto2(){
	
 		querySelectPost("CatalogoObraTMovimendo2Read",{async: false });
		
		if( esSAIFonden && <%=id_oper%> ==3  ){
			$("#verProyectoFondenDiv").css('display', 'show');
			$("#selproyectoFondenDiv").css('display', 'none');
			
		}
		
		if( esSAIFonden  ){
			$("#verFondenSPAN").show();
			querySelectPost(
				{ 	     queryName: 'catFondenRead', 
					targetObjectId: 'proyectoFonden', 
					         async: false,
					      callback: function(){
					         	$("#dlgSeleccionaFonden").dialog("open");
					         }
				}
			);
			$("#verProyectoFondenDiv").css('display', 'none');
			$("#selproyectoFondenDiv").css('display', 'show');
		}
				
		if ($("#TIPO_CONCEPTO").val() == "AL"){
			querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
			$("#altaAlmacen").val("-1");
			$("#altaAlmacen").hide(); //show-->hide
			$("#cAnioFactEP").val("-1");
			$("#nFacturaEP").val("-1");
			$("#cAnioFactEP").hide(); //show-->hide
			$("#nFacturaEP").hide(); //show-->hide
		}
		else{
			$("#altaAlmacen").val("0");
			$("#altaAlmacen").hide();
			$("#cAnioFactEP").val("0");
			$("#nFacturaEP").val("0");
			$("#cAnioFactEP").hide(); 
			$("#nFacturaEP").hide();
			querySelectPost("CatalogoAlmacenVacioRead","ALM", {async: false });
		}
	}
	
	/*AMORTIZACION POR FACTOR*/
	var importeRecepcionOriginal = 0.0;
	var acumuladoAmortizacionOriginal = 0.0;
	var esIvaArrenda=0;
	var mImporteFlete23=0;
	var mImporteFlete232=0;
	var ayuda1=0;
	var ayuda2=0;
	var saldoo=0;
	var acumula=0;
	var comprometido=0;
	var DCD_IVADES=0;
	var DCD_MIL2=0;
	var DCD_MIL5=0;
	var DCD_RETENCION=0;
	var DCD_IVADESres=0;
	var DCD_MIL2res=0;
	var DCD_MIL5res=0;
	var DCD_RETENCIONres=0;
	var mAmortizacionAnticipo=0;
	var mAmortizacionAnticipoRes=0;
	var mAmortizacionAcumulado=0;
	var mAmortizacionAcumuladoRes=0;
	var mImporteSancionAcumulado=0;
	var mImporteDevolucionAcumulado=0;
	var contador=0;
	var mIMDT=0;
	var mCNIC=0;
	var CamInst=0;	
	var mObra5=0;
	var mISRHonorarios=0;
	var mISRArrenda=0;
	var mPenalizacion=0;
	var mRetImpuestoCedular=0;
	var mImporteNetoEP=0;
	var mImporteNetoEPRes=0;
	var mImporteIVAEP=0;
	
	/**
	 * Calculo de iva, retenciones y amortizaciones.
	 */

	 function calculaIVA() {
	 	
	 	var esSAIAlterno = $("#esSAIAlterno").val() === "true";
	 	
	 	queryFormPost("porcIVARead", { async: false });

	 	queryFormPost({
	 		queryName: "leeImpuestosContrato",
	 		async: false,
	 		callback: function() {
	 			ivaAplicable = $("#iva").val();
	 		}
	 	});


	 	let mAmortizacionAnticipo = (Number(quitaFmt($("#mAmortizacionAnticipo ").val())) * 100) / 100;

	 	suma = Number(quitaFmt($("input[id='mImporteBruto']").val())) + Number(quitaFmt($("input[id='mOtrosImpuestos']").val()));
	 	suma -= Number(quitaFmt($("input[id='mImporteSancion']").val()));
	 	suma += Number(quitaFmt($("input[id='mImporteDevolucion']").val()));
	 	
	 	/*Aqui guarda el ( Importe Bruto + Otros Impuestos) - Sanciones + Devoluciones*/
	 	$("#subTotal_1").val(suma.toFixed(2));
	 	suma -= mAmortizacionAnticipo ;
	 	validaPagoConPenalizacion();
	 	
	 	if (mAmortizacionAnticipo == 0){
	 		$("#subTotal_2").val($("#subTotal_1").val());
	 	}

	 	//JGDS si tiene sanciones,devoluciones, otros impuesto o amortizacion del anticipo, sino deja el importe de iva que ya traia
	 	if ($("#subTotal_1").val() != $("#subTotal_2").val()) {
	 		var res_iva = parseFloat($("input[id='nPorcIVAAplicable']").val() / 100) * (parseFloat($("input[id='subTotal_2']").val()));
	 		res_iva = res_iva.toFixed(2);
	 		$("#mImporteIVA").val(res_iva);
	 		$("#mImporteMasIva").val(Number($("#mImporteIVA").val()) + Number($("#subTotal_2").val()));
	 	}

	 	suma += Number($("input[id='mImporteIVA']").val());

	 	if (mImporteFlete23 == 1) {
	 		DCD_IVADES = (Number($("#subTotal_2").val()) * 0.106667);
	 		DCD_IVADES = Number(DCD_IVADES).toFixed(2);
	 		//		DCD_IVADES =  (Number($("#mImporteIVA").val()) * 2) / 3;
	 	}

	 	var mImpteCedular = 0.00;

	 	if (Number($("#lHaySaldoAnticipo").val()) == 1
	 		&& $("#mAmortizado").val() == '0.0000') {

	 		$("#DCD_MIL2").val(0);
	 		$("#DCD_MIL5").val(0);
	 		$("#DCD_RETENCION").val(0);
	 		$("#DCD_IVADES").val(0);
	 	} else {
	 		CalculaRetencionesG();
	 		$("#DCD_MIL2").val(DCD_MIL2res);
	 		$("#DCD_MIL5").val(DCD_MIL5res);
	 		$("#sumareten").val(DCD_RETENCIONres);
	 		DCD_MIL2 = parseFloat($("#DCD_MIL2").val() * parseFloat($("#subTotal_1").val()));
	 		DCD_MIL5 = parseFloat($("#DCD_MIL5").val() * $("#subTotal_1").val());

	 		if ($("#porcCedular").val() == "1") {
	 			mImpteCedular = parseFloat($("#mRetImpuestoCedular").val() * $("#subTotal_1").val());
	 			mImpteCedular = Number(mImpteCedular).toFixed(2);

	 		} else {
	 			if (Number($("#mRetImpuestoCedular").val()) > 0) {
	 				mImpteCedular = parseFloat($("#mRetImpuestoCedular").val() * $("#subTotal_1").val());
	 				mImpteCedular = Number(mImpteCedular).toFixed(2);
	 			}
	 		}
	 		DCD_RETENCION = parseFloat($("#sumareten").val() * $("#subTotal_1").val());
	 		DCD_RETENCION = Number(DCD_RETENCION).toFixed(2);
	 		//		DCD_RETENCION = parseFloat(DCD_RETENCION) + parseFloat(mImpteCedular);
	 		DCD_RETENCION = Number(DCD_RETENCION).toFixed(2);

	 		$("#DCD_MIL2").val(Math.round(DCD_MIL2 * 100) / 100);
	 		$("#DCD_MIL5").val(Math.round(DCD_MIL5 * 100) / 100);
	 		$("#DCD_RETENCION").val(Math.round(DCD_RETENCION * 100) / 100);
	 		$("#DCD_IVADES").val(Math.round(DCD_IVADES * 100) / 100);

	 	}

	 	$("#mImpEjercer").val(Math.round(Number(suma) * 100) / 100);

	 	ayuda1 = Number($("#sumareten").val()) * Number($("input[id='subTotal_1']").val());
	 	ayuda1 = Number(ayuda1).toFixed(2);

	 	if ($("#cCentroContable").val() == "28" || $("#cCentroContable").val() == "21") {
	 		ayuda1 = parseFloat(ayuda1) + parseFloat(mImpteCedular);
	 	}

	 	ayuda1 = Number(ayuda1).toFixed(2);

	 	ayuda2 = parseFloat(ayuda1) + parseFloat($("#DCD_MIL2").val())
	 		+ parseFloat($("#DCD_MIL5").val())
	 		+ parseFloat($("#DCD_IVADES").val());
	 	ayuda2 = Number(ayuda2).toFixed(2);

	 	$("#mImporteRetencion").val(Math.round(Number(ayuda2) * 100) / 100);

	 	if (Number(ayuda2) > 0 && Number($("#mImporteFacturas").val()) > 0) {
	 		$("#mImporteRetenPago").val("0");
	 		queryFormPost("importeRetencionPDIVERSO_Read", { async: false });

	 		var retencionPago = $("#mImporteRetenPago").val();
	 		retencionPago = Number(retencionPago).toFixed(2);
	 		var retenciones = $("#mImporteRetencion").val();
	 		retenciones = Number(retenciones).toFixed(2);

	 		var diferenciaRet = 0;
	 		diferenciaRet = Number(retenciones) - Number(retencionPago);
	 		diferenciaRet = Number(diferenciaRet).toFixed(2);

	 		if (diferenciaRet == -0.01 || diferenciaRet == 0.01) {
	 			$("#mImporteRetencion").val(Math.round(Number(retencionPago) * 100) / 100);
	 		}
	 	}

	 	suma -= Number($("input[id='mImporteRetencion']").val());

	 	var penalizacion = 0.00;
	 	penalizacion = (parseFloat($("#mImportePenalizacion").val())) * 100 / 100;
	 	suma -= Number(penalizacion);

	 	$("#mImporteNeto").val(Math.round(Number(suma) * 100) / 100);

	 	acumula = Number($("#acumulado").val()) + Number($("#mImpEjercer").val());
	 	acumula = acumula.toFixed(2);


	 	if ($("#PagoAMF").val() != "" && $("#mImporteNeto").val() != "0") {
	 		if (Number($("#mImporteNeto").val()) != Number($("#PagoAMF").val())) {
	 			$("#mImporteBruto").val(0);
	 			Swal.fire("Verifique", "Importe Neto es diferente al Importe AMF", "warning");
	 			calculaIVA();
	 		}
	 	}

	 	/*
	 	 * -----------------------------------------------------------------------------------------
	 	 * URVP.09092014 SE PONE A DOS DECIMALES LOS SIGUIENTES CAMPOS
	 	 */
	 	$("#nPorcIVAAplicable").val(Number($("#nPorcIVAAplicable").val()).toFixed(2));
	 	$("#saldoCed").val(Number($("#saldoCed").val()).toFixed(2));
	 	$("#mTotal").val(Number($("#mTotal").val()).toFixed(2));
	 	$("#saldoAnticipo").val(Number($("#saldoAnticipo").val()).toFixed(2));
	 	// ------------------------------------------------------------------------------------------

	 	/*
	 	 * EHR2015 Si es recepcion con factor de amortizacion se permite la captura.
	 	 */
	 	if ($("#tipoAmortizacion").val() == "1") {
	 		$("#mAmortizacionAnticipo")[0].readOnly = false;
	 		$("#mAmortizacionAnticipo").removeClass("notEditable");

	 	} else {
	 		$("#mAmortizacionAnticipo")[0].readOnly = true;
	 		$("#mAmortizacionAnticipo").addClass("notEditable");
	 	}



	 	if (penalizacion > 0.00) {
	 		var neto = parseFloat($("#mImporteNeto").val()).toFixed(2);
	 		var retenciones = $("#mImporteRetencion").val();
	 		retenciones = Number(retenciones).toFixed(2);

	 		var ejercer = parseFloat(Number(neto) + Number(penalizacion) + Number(retenciones)).toFixed(2);

	 		$("#mImpEjercer").val(ejercer);
	 		$("#mImporteMasIva").val(ejercer);
	 	}

	 }
	
	
	function AnticipoTotal(){
			if ( parseFloat ($("#saldoAnticipo").val()) == parseFloat($("#mAmortizacionAcumulado").val())){
				$("#nPorcAmortizacion").val(0);
				calculaIVA();
			}
	}
	function Amortizacion(){
		 if ( Number ($("#nPorcAmortizacion").val()) < Number( $("#nPorcAmortizacion2").val() )){
			Swal.fire("Revisar el procentaje","El porcentaje minimo es: " +$("#nPorcAmortizacion2").val(),"warning" );
			$("#nPorcAmortizacion").val($("#nPorcAmortizacion2").val());
	 	}else{
			calculaIVA();
		}
	}	
	var cantidadsiniva=0;
	var cantidadrespaldo=0;
	var cantidadsinIvaEP=0;
	var cantidadsinIvaEP23=0;
	
	function calculaRetencionesEP ( cantidad ){
		
		var porcParticipacion = 0.00;
		var porcentajeIVA = 0.00;
		porcentajeIVA = ( Number($("#mImporteIVA").val()) * 100 / (Number($("#mImpEjercer").val())) ) / 100;
		cantidadsinIvaEP = cantidad * (1-porcentajeIVA);		
		cantidadsinIvaEP = cantidadsinIvaEP.toFixed(2);
		
		porcParticipacion = cantidad / (Number($("#mImpEjercer").val()));
		
		mCNIC=0;
		mIMDT=0;
		mObra5=0;
		$("#mObra5").val($("#mObra52").val());
		$("#mISRHonorarios").val($("#mISRHonorarios2").val( ) );
		$("#mImporteFlete4").val($("#mImporteFlete42").val( ) );

		$("#mISRArrenda").val($("#mISRArrenda2").val());
		$("#mTesofe").val($("#mTesofe2").val());
		$("#mRetImpuestoCedular").val($("#mRetImpuestoCedular2").val( ) );
		cantidadrespaldo = cantidad;	
   		cantidad = cantidadsinIvaEP;
   		var d_miva = Number(cantidadrespaldo) - Number( cantidadsinIvaEP );
   		d_miva = d_miva.toFixed(2);
		$("#mIVA").val( d_miva );	
		
		queryFormPost("retenciones_ISR_IVA_RGOC_Read", {async:false});
		
		mImporteFlete232 = porcParticipacion * (Number($("#mImporteFlete23").val()));
		mImporteFlete232 = mImporteFlete232.toFixed(2);
		$("#mImporteFlete23").val( mImporteFlete232 );
		
		if ( $("#CamInst").val() == 0 ){
			mCNIC = parseFloat ( cantidad )* parseFloat( CamInst);
			mCNIC = Number( mCNIC.toFixed(2) ) ;
			
			if( ($("#nPorcAmortizacion").val()/100) == 1 )
				mCNIC = mCNIC / ( 1 ); 
			else
				mCNIC = mCNIC / ( 1 - ($("#nPorcAmortizacion").val()/100));
				
			mCNIC = mCNIC.toFixed(2);
			$("#mCNIC").val( mCNIC );
		}else if ( $("#CamInst").val() == 1 ){
			mIMDT = parseFloat ( cantidad )* parseFloat( CamInst);
			mIMDT = Number( mIMDT.toFixed(2) );
			if( ($("#nPorcAmortizacion").val()/100) == 1 )
				mIMDT = mIMDT / ( 1 );
			else
				mIMDT = mIMDT / ( 1 - ($("#nPorcAmortizacion").val()/100));
			mIMDT = mIMDT.toFixed(2);
			$("#mIMDT").val( mIMDT );
		}else{
			$("#mIMDT").val(0);
			$("#mCNIC").val(0);
		}
		
		mObra5 = parseFloat ( cantidad )* parseFloat( $("#mObra5").val());
		mObra5 = Number( mObra5.toFixed(2) ) ;
		
		if( ($("#nPorcAmortizacion").val()/100) == 1 )
			mObra5 = mObra5 / ( 1 );
		else
			mObra5 = mObra5 / ( 1 - ($("#nPorcAmortizacion").val()/100));
		
		mObra5 = mObra5.toFixed(2);
		$("#mObra5").val( mObra5 );
		
		queryFormPost("retenciones_ISR_RGOC_Read", {async:false});
		mISRHonorarios = porcParticipacion * (Number($("#mISRHonorarios").val()));
		mISRHonorarios = mISRHonorarios.toFixed(2);
		$("#mISRHonorarios").val( mISRHonorarios );
		
		mImporteFlete4 = parseFloat ( cantidad )* parseFloat( $("#mImporteFlete4").val());
		mImporteFlete4 = Number( mImporteFlete4.toFixed(2) ) ;
		if( ($("#nPorcAmortizacion").val()/100) == 1 )
			mImporteFlete4 = mImporteFlete4 / ( 1 );
		else
			mImporteFlete4 = mImporteFlete4 / ( 1 - ($("#nPorcAmortizacion").val()/100));
			
		mImporteFlete4 = mImporteFlete4.toFixed(2);
		$("#mImporteFlete4").val( mImporteFlete4 );
		
		mISRArrenda = parseFloat ( cantidad )* parseFloat( $("#mISRArrenda").val());
		mISRArrenda = Number( mISRArrenda.toFixed(2) ) ;
		
		if( ($("#nPorcAmortizacion").val()/100) == 1 )
			mISRArrenda = mISRArrenda / ( 1 );
		else
			mISRArrenda = mISRArrenda / ( 1 - ($("#nPorcAmortizacion").val()/100));
			
		mISRArrenda = mISRArrenda.toFixed(2);
		$("#mISRArrenda").val( mISRArrenda );
		
		var mAmortizaEP = ( Number( $("#mAmortizacionAnticipoMasIva").val() ) / Number( $("#mImpEjercer").val() ) ) * cantidadrespaldo;
		mAmortizaEP = mAmortizaEP.toFixed(2);
		$("#mAmortizacionAnticipoMasIvaEP").val( mAmortizaEP );
		
		var mPenaliza = ( Number( $("#mImportePenalizacion").val() ) / Number( $("#mImpEjercer").val() ) ) * cantidadrespaldo;
		mPenaliza = mPenaliza.toFixed(2);
		$("#mPenalizacion").val( mPenaliza );
		
		mRetImpuestoCedular = parseFloat ( cantidad )* parseFloat( $("#mRetImpuestoCedular").val());
		mRetImpuestoCedular = Number( mRetImpuestoCedular.toFixed(2) ) ;
		
		if( ($("#nPorcAmortizacion").val()/100) == 1 )
			mRetImpuestoCedular = mRetImpuestoCedular/ ( 1 );
		else
			mRetImpuestoCedular = mRetImpuestoCedular/ ( 1 - ($("#nPorcAmortizacion").val()/100));
			
		mRetImpuestoCedular = mRetImpuestoCedular.toFixed(2);
		$("#mRetImpuestoCedular").val( mRetImpuestoCedular );

		mImporteNetoEP = Number($("#m23IVA").val()) + Number($("#mCNIC").val())+Number($("#mIMDT").val())  + Number($("#mObra5").val()) + Number ($("#mISRHonorarios").val())+
		Number($("#mImporteFlete4").val())+ Number( $("#mISRArrenda").val() )+Number( $("#mRetImpuestoCedular").val())+
		Number($("#mImporteFlete23").val()) + Number($("#mTesofe").val());
		
		mImporteNetoEPRes = parseFloat(cantidadrespaldo) -  parseFloat(mImporteNetoEP);
		mImporteNetoEPRes = mImporteNetoEPRes.toFixed(2);
		$("#mImporteNetoEP").val( mImporteNetoEPRes );
	
	}

function ponCerosD(){
	
	$("#DCD_ISR_D").val( "0" );
	$("#DCD_IVA_D").val( "0" );
	$("#DCD_MIL5_D").val( "0" );
	$("#DCD_MIL2_D").val( "0" );
	$("#DCD_CONTRIBUCION_D").val( "0" );
	$("#DCD_OTRAS_RET_D").val( "0" );
	$("#DCD_PENALIZACION_D").val( "0" );
	$("#DCD_SANCION_D").val( "0" );
	$("#DCD_DEVOL_D").val( "0" );
	$("#DCD_AMORT_D").val( "0" );
	$("#DCD_RETENCION_D").val( "0" );
	$("#mIVA_D").val( "0" );
}
	
function actualSaldosDet( elTipo ){
    var laRetencion = 0;
	var iva = 0
    laRetencion = Number($("#m23IVA").val()) + Number($("#mCNIC").val())+Number($("#mIMDT").val())  + Number($("#mObra5").val()) + Number ($("#mISRHonorarios").val())+
		Number($("#mImporteFlete4").val())+ Number( $("#mISRArrenda").val() )+Number( $("#mRetImpuestoCedular").val())+
		Number($("#mImporteFlete23").val()) + Number($("#mTesofe").val());
    laRetencion = laRetencion.toFixed(2);
 
	$("#DCD_ISR_D").val(parseFloat($("#DCD_ISR_D").val()) + elTipo * parseFloat($("#mISRHonorarios").val()) + elTipo * Number( $("#mISRArrenda").val() ));
	$("#DCD_IVA_D").val(parseFloat($("#DCD_IVA_D").val()) + elTipo * parseFloat($("#mImporteFlete23").val()));
	$("#DCD_MIL5_D").val(parseFloat($("#DCD_MIL5_D").val()) + elTipo * parseFloat($("#mObra5").val()));
	$("#DCD_MIL2_D").val(parseFloat($("#DCD_MIL2_D").val()) + elTipo * ( Number( $("#mCNIC").val() ) + Number( $("#mIMDT").val() ) ) );
	$("#DCD_CONTRIBUCION_D").val(parseFloat($("#DCD_CONTRIBUCION_D").val()) + elTipo * parseFloat($("#mImporteFlete4").val()));
	$("#DCD_OTRAS_RET_D").val(parseFloat($("#DCD_OTRAS_RET_D").val()) + elTipo * parseFloat($("#mRetImpuestoCedular").val()));
	$("#DCD_PENALIZACION_D").val(parseFloat($("#DCD_PENALIZACION_D").val()) + elTipo * parseFloat($("#mTesofe").val()));
	$("#DCD_SANCION_D").val(parseFloat($("#DCD_SANCION_D").val()) + elTipo * parseFloat($("#mSancion").val()));
	$("#DCD_DEVOL_D").val(parseFloat($("#DCD_DEVOL_D").val()) + elTipo * parseFloat($("#mDevolucion").val()));
	$("#DCD_AMORT_D").val(parseFloat($("#DCD_AMORT_D").val()) + elTipo * parseFloat($("#mAmortizacionAnticipoMasIvaEP").val()));
	$("#DCD_RETENCION_D").val(parseFloat($("#DCD_RETENCION_D").val()) + elTipo * parseFloat(laRetencion));
	
	iva =  Number( $("#mIVA_D").val() ) + elTipo * Number( $("#mIVA").val() )  ;
	iva = iva.toFixed(2);
	$("#mIVA_D").val( iva);
}

function AjustaRetencionesD(){
		
    var laRetencion = 0;
	
	$("#mDevolucion").val( Number( $("#mDevolucion").val() ) + ( Number( $("#DCD_DEVOL").val() ) - Number( $("#DCD_DEVOL_D").val() ) ) );
	
	$("#mTesofe").val( Number( $("#mTesofe").val() ) + ( Number( $("#mImportePenalizacion").val() ) - Number($("#DCD_PENALIZACION_D").val()) ));
	
	if ( $("#CamInst").val() == 0 ){
		$("#mCNIC").val( Number( $("#mCNIC").val() ) + ( Number( $("#DCD_MIL2").val() ) - Number($("#DCD_MIL2_D").val()) ) );
	}else{
		$("#mIMDT").val( Number( $("#mIMDT").val() ) + ( Number( $("#DCD_MIL2").val() ) - Number($("#DCD_MIL2_D").val()) ) );
	}
	
	$("#mImporteFlete23").val( Number( $("#mImporteFlete23").val() ) + ( Number( $("#DCD_IVADES").val() ) - Number($("#DCD_IVA_D").val()) ) );
		
	$("#mObra5").val( Number( $("#mObra5").val() ) + ( Number( $("#DCD_MIL5").val() ) - Number($("#DCD_MIL5_D").val()) ) );
	$("#mImporteFlete4").val( Number( $("#mImporteFlete4").val() ) + ( Number( $("#DCD_CONTRIBUCION").val() ) - Number($("#DCD_CONTRIBUCION_D").val()) ) );
	
	if ( Number( $("#mISRHonorarios").val() ) > 0){
		$("#mISRHonorarios").val( Number( $("#mISRHonorarios").val() ) + ( Number( $("#DCD_ISR").val() ) - Number($("#DCD_ISR_D").val()) ) );
		
	}else{
		$("#mISRArrenda").val( Number( $("#mISRArrenda").val() ) + ( Number( $("#DCD_ISR").val() ) - Number($("#DCD_ISR_D").val()) ) );
	}
	
	$("#mRetImpuestoCedular").val( Number( $("#mRetImpuestoCedular").val() ) + ( Number( $("#DCD_OTRAS_RET").val() ) - Number($("#DCD_OTRAS_RET_D").val()) ) );
	$("#mSancion").val( Number( $("#mSancion").val() ) + ( Number( $("#DCD_SANCION").val() ) - Number($("#DCD_SANCION_D").val()) ) );

	$("#mAmortizacionAnticipoMasIvaEP").val( Number( $("#mAmortizacionAnticipoMasIvaEP").val() ) + ( Number( $("#DCD_AMORT").val() ) - Number($("#DCD_AMORT_D").val()) ) );
	//if (1==2){
		var mAmortizaEP = ( Number( $("#mAmortizacionAnticipoMasIva").val() ) / Number( $("#mImpEjercer").val() ) ) * cantidadrespaldo;
		mAmortizaEP = mAmortizaEP.toFixed(2);
		$("#mAmortizacionAnticipoMasIvaEP").val( mAmortizaEP );
	
    laRetencion = Number($("#m23IVA").val()) + Number($("#mCNIC").val())+Number($("#mIMDT").val())  + Number($("#mObra5").val()) + Number ($("#mISRHonorarios").val())+
		Number($("#mImporteFlete4").val())+ Number( $("#mISRArrenda").val() )+Number( $("#mRetImpuestoCedular").val())+
		Number($("#mImporteFlete23").val()) + Number($("#mTesofe").val());
    laRetencion = laRetencion.toFixed(2);
 	
    $("#mIVA").val( Number( $("#mIVA").val() ) + ( Number( $("#mImporteIVA").val() ) - Number( $("#mIVA_D").val() ) ) );
	$("#mImporteNetoEP").val( Number( $("#DCD_IMP_BRUTO").val() ) - Number( laRetencion ));
	
}


/*Busca la EP que se elimino del DT y la elimina de las relaciones*/
	function eliminaEPIngresos( epBuscar){
		
		var indice = -1;
		
		for( i = 0; i < relaciones.length; i++){
			if( relaciones[i] == epBuscar ){
				indice = i;
				break;
			}
		}
		
		if(indice >= 0 )
			relaciones[i].splice(indice,1);
		
		muestraJson();
	}
	
</script>
<script type="text/javascript">
	function CalculaRetencionesG(){
	
		//leer la Entidad de la EP, Esto para el caso de los estados de Nayarit (poder identificar para calcular correctamente el impuesto cedular)
		$("#cEsNayarit").val("0");
		queryFormPost("buscaEntidad_Nayarit_Read", {async: false });
	
		var szWhere = "";
		szWhere = " cidcontrato ='"+ $("#cIDContrato").val()+"' AND cIdEntidadContable = '" +  $("#cCentroContable").val() + "'";
		if ($("#llevaAnticipo").val()=="1" && $("#tieneAnticipo").val()=="0") {szWhere = szWhere + " AND 1=2";} //URVP.25112014 Si lleva anticipo y no se le ha hecho el anticipo se quitan las retenciones ya que el pago a realizar sera el anticipo el cual no lleva retenciones
		var este=0;
		var elMonto = "100";
		var retencion=0;
		var szTabla = "CALC_RETENCIONES_FACT_CONT_DIVERSO";
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
			var options = '';
			var porretencion=0;
			$("#DCD_ISR").val( 0 );
			for (var i = 0; i < j.length; i++) {
				if ($("#lHaySaldoAnticipo").val() == 0  || $("#mAmortizado").val() != '0.0000'){
					//URVP.27112014 Se valida si es impuesto cedular y nayarit, se cambia el porcentaje de la retencion a 1.5%
					if (j[i].Col0=="9" && ($("#cCentroContable").val()=="28" || $("#cEsNayarit").val() == "1" ) ){
						var porcentCedular = 0.00;
						porcentCedular = Number(Number(j[i].Col2) + 0.005).toFixed(6);
						retencion = parseFloat(porcentCedular)*Number($("#subTotal_1").val());	
					}else{
						retencion = parseFloat(j[i].Col2)*Number($("#subTotal_1").val());
					}
					//retencion.toFixed( 2 );
					
					if (j[i].Col0 == 4 ){
						retencion = Number($("#DCD_ISR").val()) + retencion ;
						retencion = Number(retencion).toFixed( 2 );
						$("#DCD_ISR").val( retencion ); 
						
						queryFormPost("retenciones_DCD_ISR_RGOC_Read", {async:false});
						var DCD_ISR = 0.00;
						DCD_ISR = Number($("#DCD_ISR_AUX").val());
						DCD_ISR = DCD_ISR.toFixed(2);
						$("#DCD_ISR").val( DCD_ISR );
												
					}else if (j[i].Col0 == 5 ){
						retencion = Number(retencion).toFixed( 2 );
						$("#DCD_CONTRIBUCION").val( retencion ); 
					}else if (j[i].Col0 == 6 ){						
						retencion = Number($("#DCD_ISR").val()) + retencion ;
						retencion = Number(retencion).toFixed( 2 );
						$("#DCD_ISR").val( retencion );
						
						queryFormPost("retenciones_DCD_ISR_RGOC_Read", {async:false});
						var DCD_ISR = 0.00;
						DCD_ISR = Number($("#DCD_ISR_AUX").val());
						DCD_ISR = DCD_ISR.toFixed(2);
						$("#DCD_ISR").val( DCD_ISR );
						 						
					}else if (j[i].Col0 == 9 ){
						retencion = Number(retencion).toFixed( 2 );
						$("#DCD_OTRAS_RET").val( retencion ); 						
					}	
				}
			}
		});	
	}
	

	
	function FiltroRetencionesD(){
		
		queryFormPost( {
			queryName:"esContratoValesRead",
			async:false,
			callback:function(){
					if( $("#vales_combustible").val()  === "" )
						$("#vales_combustible").val("false");
			}  
		});
		
		$("#tieneRetencion").val("FALSE");
		$('#tretencion').dataTable().fnClearTable(); //se limpia grid debido a que si se selecciona por error un contrato con retencion y se selecciona uno sin retencion, el grid deja las retenciones del que si tiene 
		cargaCtaBancariaRFC();
		querySelectPost("readRecepcionMateriales", "noRecepcion", {async:false});
		querySelectPost("readPenaltyDeduction", "noPenalty", {async:false});
		// reinicia valores
		$("#acumulado").val("0");
		comprometido=0;
		$("#mImporteFlete23").val("0");
		$("#mIMDT").val("0");
		$("#mCNIC").val("0");
		$("#mObra5").val("0");
		$("#mISRHonorarios").val("0");
		$("#mImporteFlete4").val("0");
		$("#mISRArrenda").val("0");
		$("#mAmortizacionAnticipoMasIvaEP").val("0");
		$("#mTesofe").val("0");
		$("#mRetImpuestoCedular").val("0");
		// termina reinicia valores
		
		//leer la Entidad de la EP, Esto para el caso de los estados de Nayarit (poder identificar para calcular correctamente el impuesto cedular)
		$("#cEsNayarit").val("0");
		queryFormPost("buscaEntidad_Nayarit_Read", {async: false });
		
		var szWhere = "";
		 
		$("#cIdContrato").val( $("#cIDContrato").val() );
		szWhere = " cidcontrato ='"+ $("#cIDContrato").val()+"' AND cIdEntidadContable = '" +  $("#cCentroContable").val() + "'";
		if ($("#llevaAnticipo").val()=="1" && $("#tieneAnticipo").val()=="0") {szWhere = szWhere + " AND 1=2";}
		var elMonto = "100";
		var este=0;
		var retencion=0;
		elMonto = $("#mTotal").val();
		var szTabla = "CALC_RETENCIONES_FACT_CONT_DIVERSO";
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
			var options = '';
			var porretencion=0;
			for (var i = 0; i < j.length; i++) {
					$("#tieneRetencion").val("TRUE");
					//URVP.27112014 Se valida si es impuesto cedular y nayarit, se cambia el porcentaje de la retencion a 1.5%
					if (j[i].Col0=="9" && ( $("#cCentroContable").val() == "28" || $("#cEsNayarit").val() == "1" )){
						var porcentCedular = 0.00;
						porcentCedular = Number(Number(j[i].Col2) + 0.005).toFixed(6);
						fnClickAddRowB(j[i].Col0, j[i].Col1, porcentCedular);
					}
					else{
						fnClickAddRowB(j[i].Col0, j[i].Col1, j[i].Col2);
					}
					porretencion=este*elMonto;
					
					if (j[i].Col0 > 10 && j[i].Col0 < 16 ){ //URVP.04092014 SE CAMBIA LA CONDICION == 0 A >10 YA QUE SE DESGLOZARON EN MAS RETENCIONES Y AS POSTERIORES AL ID 10 SON LAS EQUIVALENTES A LA 0 DEL 2/3 DEL IVA
						$("#DCD_IVADES").val(1);
						if ($("#lHaySaldoAnticipo").val() == 0 || $("#mAmortizado").val() != '0.0000' ){///1 espara cuando hay anticipo
							mImporteFlete23=1;//alo
							
						if (j[i].Col0==11)
							esIvaArrenda=1;
						}
					}else if (j[i].Col0 == 2 ){
						$("#DCD_MIL2").val( parseFloat (j[i].Col2));
		
						if ($("#lHaySaldoAnticipo").val() == 0 || $("#mAmortizado").val() != '0.0000' ){
								CamInst=parseFloat (j[i].Col2);
								CamInst2=parseFloat (j[i].Col2);
								}
							DCD_MIL2res=parseFloat (j[i].Col2);
					}else if (j[i].Col0 == 3 ){
							$("#DCD_MIL5").val( parseFloat (j[i].Col2));
							if ($("#lHaySaldoAnticipo").val() == 0 || $("#mAmortizado").val() != '0.0000' ){	
								$("#mObra5").val( parseFloat (j[i].Col2) );
								$("#mObra52").val( parseFloat (j[i].Col2) );
							}
							
							DCD_MIL5res	= parseFloat (j[i].Col2	);	
			
					}else{
						if ($("#lHaySaldoAnticipo").val() == 0  || $("#mAmortizado").val() != '0.0000'){///1 espara cuando hay anticipo
						
							if (j[i].Col0 == 4 ){
								$("#mISRHonorarios").val( parseFloat (j[i].Col2));
								$("#mISRHonorarios2").val( parseFloat (j[i].Col2));						
							}else if (j[i].Col0 == 5 ){
								$("#mImporteFlete4").val( parseFloat (j[i].Col2));
								$("#mImporteFlete42").val( parseFloat (j[i].Col2));						
							}else if (j[i].Col0 == 6 ){						
								$("#mISRArrenda").val( parseFloat (j[i].Col2));		
								$("#mISRArrenda2").val( parseFloat (j[i].Col2));						
							}else if (j[i].Col0 == 7 ){						
								$("#mTesofe").val( parseFloat (j[i].Col2) );	
								$("#mTesofe2").val( parseFloat (j[i].Col2) );						
							}else if (j[i].Col0 == 9 ){
								if ($("#cCentroContable").val()=="28" || $("#cEsNayarit").val() == "1"){				
									$("#mRetImpuestoCedular").val(porcentCedular);
									$("#mRetImpuestoCedular2").val(porcentCedular);
								}else{
									$("#mRetImpuestoCedular").val( parseFloat (j[i].Col2));
									$("#mRetImpuestoCedular2").val( parseFloat (j[i].Col2));
								}
							}	
						}
						if (j[i].Col0=="9" && ($("#cCentroContable").val()=="28" || $("#cEsNayarit").val() == "1" )){
							//retencion= parseFloat(retencion) + parseFloat(porcentCedular);
							$("#porcCedular").val("1");
							//$("#tieneCedular").val("1");
						}else if (j[i].Col0=="9" && $("#cCentroContable").val()=="21"){
							$("#porcCedular").val("1");
							//$("#tieneCedular").val("1");
						}else{
							retencion= parseFloat(retencion) + parseFloat(j[i].Col2);
						}
					}
					$("#sumareten").val(retencion);
					DCD_RETENCIONres=retencion;
			}
		});	
		if (<%=id_oper%> ==1 ){
			Anticipo();
			
			if ($("#lHaySaldoAnticipo").val() == 1 && $("#mAmortizado").val() != '0.0000'){
				$("#mAmortizacionAcumulado").val(  $("#mAmortizado").val() );
			}else {
				queryFormPost("tFACTDiversosSEGUNDOPAGORead", {async: false });
			}
			acumuladoAmortizacionOriginal = Number( quitaFmt( $("#mAmortizacionAcumulado").val() ) ) * 100 / 100;
			$("#nPorcAmortizacion2").val($("#nPorcAmortizacion").val());
			mAmortizacionAcumuladoRes= $("#mAmortizacionAcumulado").val();
			mImporteSancionAcumulado=$("#mImporteSancionAcumulado").val();
			mImporteDevolucionAcumulado=$("#mImporteDevolucionAcumulado").val();
			comprometido=$("#acumulado").val();
			setTimeout("AnticipoTotal()",1000);
		}
	}
	
	function validaAmortizacion(){
		var impAnticipo = 0.00;
		var impAmortizado = 0.00;
		var impPago = 0.00;		
		impAnticipo = Number($("#saldoAnticipo").val());
		impAmortizado = Number($("#mAmortizacionAcumulado").val());
		impPago = Number($("#mImpEjercer").val());
		
		if (impPago <= (impAnticipo-impAmortizado)){
			Swal.fire("El pago no se puede procesar debido a que es menor o igual que la amortizacion del anticipo", "favor de capturar el resto de las facturas.","error");
			return;
		}else{
			$("#mAmortizacionAnticipo").val((impAnticipo-impAmortizado).toFixed(2));
			$("#mImpEjercer").val(Number($("#mImpEjercer").val())+Number($("#mAmortizacionAnticipo").val()));
			$(".subtotall").change();
		}
	}
	
	var ivaanticipo=0;
	function Anticipo(){
		$("#cIDContratoObra").val( $("#cIDContrato").val() );
// 		queryFormPost("tFactDiversoAnticipoRead", {async: false });
		queryFormPost("tFACTOBRASaldocompromisoRead", {async: false });
		if ($("#lHaySaldoAnticipo").val() == "1" && $("#mAmortizado").val() == '0.0000'){
			$("#mImporteBruto").attr("readonly",true);
			 $("#nPorcAmortizacion").val(0);
			setTimeout("calculaIVA()",400);
		}else{
			$("#mImporteBruto").val(0);
			calculaIVA();			
		}
	}
	
	function elRetardo(){
		$("#nombre").val($("#cobjetocontrato").val());	
		$(".paso01").attr('disabled', false);
		
		if(  $("#tipoAmortizacion").val() != "1"  ){
			elParametro = "'"+$("#caNoContrarrecibo").val()+"', '"+$("#TIPO_OPERACION").val()+"', '0', '0', '"+$("#DCD_IMP_BRUTO").val()+"', '"+$("#DCD_SANCION").val()+"', '"+$("#DCD_DEVOL").val()+"', '"+$("#DCD_AMORT").val()+"', '"+$("#DCD_IVA").val()+"','"+$("#DCD_RETENCION").val()+"', '"+$("#DCD_PENALIZACION").val()+"', '"+$("#DCD_NETO").val()+"', '"+$("#DCD_FECHA_FACTURA").val()+"','" + $("#cCentroContable").val() + "'," + $("#aEjercicioFiscal").val() + ",'"+$("#nombre").val()+"', '2','0','"+$("#cIdRFC_RelacionGasto2").val()+"',"+$("#mOtrosImpuestos").val();
		}else{
			elParametro = "'"+$("#caNoContrarrecibo").val()+"', '"+$("#TIPO_OPERACION").val()+"', '0', '0', '" + $("#mImporteBruto").val() + "', '"+$("#DCD_SANCION").val()+"', '"+$("#DCD_DEVOL").val()+"', '"+$("#DCD_AMORT").val()+"', '"+$("#DCD_IVA").val()+"','"+$("#DCD_RETENCION").val()+"', '"+$("#DCD_PENALIZACION").val()+"', '"+$("#DCD_NETO").val()+"', '"+$("#DCD_FECHA_FACTURA").val()+"','" + $("#cCentroContable").val() + "'," + $("#aEjercicioFiscal").val() + ",'"+$("#nombre").val()+"', '2','0','"+$("#cIdRFC_RelacionGasto2").val()+"',"+$("#mOtrosImpuestos").val();
		}
		
		$.getJSON("../catalogos/InsertJson.jsp",{Tabla: "TCONTRARECIBODIVERSOSCREATE", Param: elParametro, MaxReg: "", ajax: 'false'}, function(j){});
		$("#cIDContrato").attr('disabled', true);
		$(".paso01").attr('disabled', true);
		$("#Agregar1").attr('disabled', true);
		
	parent.document.getElementById("pb_send").style.visibility='visible';
	parent.document.getElementById("pb_send").disabled = false;
	}	
	function validar(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla==8) return true;
			patron =/[A-Za-z.\d\s\\. ]/;
			te = String.fromCharCode(tecla);
			return patron.test(te);
	}
	function validar2(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla==8) 
			return true;
		patron =/[.\d]/;
		te = String.fromCharCode(tecla);
		return patron.test(te);
	}
	function cmdImprimir(elFormato){
		 if (elFormato =='PolizaPago'){
		 	elFormato = elFormato + 'N';
		 	tipo='CR.';
		 }else{
		 	tipo='';
		 }
					window.open(
						"../admin/SeguridadCatalogos?"
							+ "catalogo=CONTRARECIBO"
							+ "&accion=run"
							+ "&rn=" + elFormato + ".jasper"
							+ "&whereFolio= "+  tipo +"caNoContrarrecibo = '" + $("#caNoContrarrecibo").val()
							+"'", 			
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");
		
		setTimeout('anexo("' + elFormato + '")', 5000);	
										
}
function anexo( pfmt ){
	$("#totFacturas").val(0);
	queryFormPost("totFacturasPago", {async: false });
	
	if (($("#cllave").val()>7 || $("#totFacturas").val()>7) && pfmt == "PolizaPagoN" ){ //se cambia a EP's mayor a 7 o mas de 7 facturas
		window.open(
			"../admin/SeguridadCatalogos?"
				+ "catalogo=ANEXO"
				+ "&accion=run"
				+ "&rn=Anexo1.jasper"
				+ "&swhere=  and caNoContrarrecibo ='"+ $("#caNoContrarrecibo").val()+"'" ,  
				"Anexo",
				"scrollbars=1, resizable=yes, width=1024, height=768");
	}
}
	
	
	
	function quitaFmt( val ) {
		val = "" + val;
	   	val = val.replace("$", "");
	   	val = val.replace(",", "");
	   	val = val.replace(",", "");
	   	val = val.replace(",", "");
	   	val = val.replace(",", "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val;
	}
	function Sinfrmt( fld ){
		var valcol = fld.value ;
		valcol = valcol.replace("$", "");
		valcol = valcol.replace(",", "");
		$("#" + fld.id).val( valcol );
	}
	function cambiafrmt( fld ){
	    $("#" + fld.id).formatCurrency();
	}

	function validaSancion(){
		var montoPago = 0.00;
		var montoSancion = 0.00;
		montoSancion = Number($("#mImporteSancion").val());
		montoPago = Number($("#mImporteBruto").val());
		if (montoPago==0){
			Swal.fire("El importe del pago no puede ser 0", "Favor de verificar la recepcion de material o que los importes sean correctos en las Facturas.","error");
			return false;
		}
		if (montoSancion>montoPago){
			Swal.fire("Revise el importe","El monto de la Sanción no puede ser Mayor al Importe Bruto.","warning");
			$("#mImporteSancion").val(0);
			document.getElementById("mImporteSancion").focus();
			$(".subtotall").change();
			return false;
		}
		return true;
	}
	

	function agregaAmortizacion(calcularAmortizacion) {
		let importeIVA = 0;
		/*
		 * EHR2015 Si es recepcion con factor de amortizacion se permite la captura.
		 */
		if ($("#tipoAmortizacion").val() != "1") {

			if (calcularAmortizacion) {
				queryFormPost("buscaAnticipoPagoDiversoRead", { async: false });

				if (Number($("#saldoAnticipo").val()) - Number($("#mAmortizacionAcumulado").val()) != 0) {
					
						if ($("#tipoAmortizacion").val() != "1") {
					
							if ($("#lHaySaldoAnticipo").val() == 1
								&& $("#mAmortizado").val() == '0.0000') {
								$("#nPorcAmortizacion").val(0);
							} else {
								var mAmortizacionAnticipo = $("#mAmortizacionAnticipo").val();
								var importeBrutoNuevo = (Number(importeRecepcionOriginal) + Number(mAmortizacionAnticipo )) * 100 / 100;
								$("#mImporteBruto").val(importeBrutoNuevo.toFixed(2));
								
								var v_amortanticmasiva = Number(quitaFmt($("#mAmortizacionAnticipoMasIva").val()));
								v_amortanticmasiva = v_amortanticmasiva.toFixed(2);
					
								$("#mAmortizacionAnticipoMasIva").val(v_amortanticmasiva);
							}
					
							$("#subTotal_1").val(( importeBrutoNuevo ).toFixed(2));
							$("#subTotal_2").val(( importeBrutoNuevo - mAmortizacionAnticipo ).toFixed(2));
							importeIVA = parseFloat(Number($("#subTotal_2").val()) * 0.16).toFixed(2);
							$("#mImporteIVA").val(importeIVA);
							
						} else {
					
							$("#mImporteBruto").val(importeRecepcionOriginal);
							
							suma = Number(quitaFmt($("input[id='mImporteBruto']").val())) + Number(quitaFmt($("input[id='mOtrosImpuestos']").val()));
							
							if( !esSAIAlterno )
								suma = suma + Number(quitaFmt($("input[id='mAmortizacionAnticipo']").val()));
							 
							suma -= Number(quitaFmt($("input[id='mImporteSancion']").val()));
							suma += Number(quitaFmt($("input[id='mImporteDevolucion']").val()));
							
							$("#subTotal_1").val(suma.toFixed(2));
					
							var importeSubtotal = 0;
							if (mAmortizacionAnticipo > 0) {
					
								if( !esSAIAlterno )			
									$("#mImporteBruto").val( Number(importeRecepcionOriginal) + Number( mAmortizacionAnticipo) );
								else 
									$("#mImporteBruto").val(importeRecepcionOriginal)
									
								importeSubtotal = ( suma - mAmortizacionAnticipo ) * 100 / 100;
								$("#subTotal_2").val(importeSubtotal.toFixed(2));
					
								
								importeIVA = parseFloat(Number($("#subTotal_2").val()) * 0.16).toFixed(2);
								$("#mImporteIVA").val(importeIVA);
					
							} else {
								$("#subTotal_2").val(suma.toFixed(2));
							}
					
						}
				}
			} else {
				$("#importeEjercer").val($("#mImpEjercer").val());
			}
		}
		
		if ($("#mAmortizacionAnticipo").val() =="") {
			$("#mAmortizacionAnticipo").val("0")
		}
	}

	function cmdGuardar(){
		
		if ($("#numPaso").val() == '1'){
			queryFormPost("readBanderaDiversoRG", {async: false });
			if (($("#tieneRetencion").val()).toUpperCase()=="TRUE" && ($("#BanderaDiversoRG").val()).toUpperCase()=="FALSE"){
				Swal.fire("Revise","No se puede realizar este tramite debido a que se encuentra deshablitado el uso de retenciones.","warning");
				return;				
			}
			if (!validaSancion()) return;
			if ($("#noRecepcion").val()=="-1"){
				Swal.fire("Revise","Favor de seleccionar una recepcion.","warning");
				return;
			}
			
			// Verificar que exista la Opinión de Cumplimiento (SAT Formato 32D)
			if (!verificarOpinionCumplimiento()) {
				Swal.fire({
					icon: 'warning',
					title: 'Documento Requerido',
					text: 'Debe adjuntar el documento de Opinión de Cumplimiento (SAT Formato 32D) antes de guardar el pago. Por favor, cargue el documento y valídelo.',
					confirmButtonText: 'Entendido'
				});
				return;
			}
			
			queryFormPost("penaltyExist", {async: false});
			if ($("#tienePenas").val()!="0"){
				if ($("#noPenalty").val()=="-1"){
					if (!confirm("¿Este contrato tiene penas y/o deducciones capturadas en el modulo de materiales y no se agregaron al pago, deseas continuar?")){
						alert("Favor de agregar las Penas/Deducciones.");
			   			return;
			   		}
				}
			}
			
			var facRet = 0.00;
			facRet = (  parseFloat( $("#mImporteFacturas").val() ) + parseFloat( $("#mImporteRetencion").val() ) + parseFloat( $("#mImportePenalizacion").val()) + (parseFloat ($("#mAjustePenas").val()) *-1)) * 100 / 100;
			facRet = Number(facRet).toFixed(2);
			
			var imptePenalizacion = 0.00;
			imptePenalizacion = (  parseFloat( $("#mImportePenalizacion").val() )  ) * 100 / 100;
			
			/*AMORTIZACION POR FACTOR*/
			var importeEjercer = parseFloat(  $("#mImpEjercer").val() ) * 100 / 100;
			var techoImporteEjercer =  parseFloat( importeEjercer + 0.02 ) * 100 / 100;
			var pisoImporteEjercer  =  parseFloat( importeEjercer - 0.02 ) * 100 / 100;
			var amortizacionAnticipo = parseFloat( $("#mAmortizacionAnticipo").val() ) * 100 / 100;
			var techoEjercerMasAmortizaciones = 0.0;
			var pisoEjercerMasAmortizaciones = 0.0;
			
			if (amortizacionAnticipo > 0) {
				pisoImporteEjercer =  parseFloat( importeEjercer + Number(amortizacionAnticipo) - 0.02 ) * 100 / 100;
				techoImporteEjercer =  parseFloat( importeEjercer + Number(amortizacionAnticipo) ) * 100 / 100;  
			}
			
			if(  $("#tipoAmortizacion").val() != "1"  ){
				pisoEjercerMasAmortizaciones  =  parseFloat( importeEjercer + amortizacionAnticipo - 0.02 ) * 100 / 100;
				techoEjercerMasAmortizaciones =  parseFloat( importeEjercer + amortizacionAnticipo ) * 100 / 100;  
			}else{
				pisoEjercerMasAmortizaciones = pisoImporteEjercer;
				techoEjercerMasAmortizaciones = techoImporteEjercer;
			}
			
			if( ( facRet >= pisoImporteEjercer && facRet <= techoImporteEjercer ) ){
			
				var diferencia = 0.00;
				diferencia =  Math.abs(parseFloat($("#mImporteFacturas").val()) + (parseFloat($("#mAjustePenas").val()) * -1)  -parseFloat($("#mImporteNeto").val())).toFixed(2);
				if (diferencia>-0.02 && diferencia<0.02 && diferencia!=0.00){
					if (!confirm("Tu pago saldra con diferencia de $"+diferencia+" en el Importe Neto, ¿Deseas continuar?")){
			   			alert("Favor de Borrar el tramite.");
			   			return;
			   		}
			   	}
			   	
			}
			else{
			    Swal.fire("Revise importes","El importe en Facturas+Retencion no corresponde a la recepción.","warning");
				return;
			}
			if ($("#destGasto").val()=="ANRO"){
				
// 				if (Number($("#saldoAnticipo").val()).toFixed(2) != (Number($("#mImpEjercer").val()) + Number($("#totalPagosAnticipo").val())).toFixed(2)){
// 					alert("El importe del pago no coincide con el importe correcto del anticipo, favor de revisar las facturas.");
// 					return;
// 				}
				if ($("#destGasto").val()=="ANRO"){
					$('#tretencion').dataTable().fnClearTable();
					$("#mImporteRetencion").val(0);
					$(".subtotall").change();
					calculaIVA();
				}
			}
			
			if ($("#tipoAmortizacion").val() != "1"){
				if (Number($("#saldoAnticipo").val())!=Number($("#mAmortizacionAcumulado").val()) && $("#destGasto").val()!="ANRO" && $("#tieneRemanente").val() != "0"){ 
					Swal.fire("Importe Amortización","No se puede realizar el pago debido a que no cumple el total de la amortización del anticipo.","warning");
					return;
				}
			}
			if (Number($("#mImpEjercer").val())<0){
				Swal.fire("Valide importe","El importe no puede ser Negativo.","warning");
				return;
			}
			var aData = oTablevFact.fnGetData();
			if ( $("#destGasto").val() != "AL" && $("#destGasto").val() != ""){
				if ( aData.length == 0 ){
					Swal.fire("Capturar","Se deben Capturar las facturas","warning");
					return -1;
				}
			}
			
			if ($.trim($("#destGasto").val()) == ""){
				Swal.fire("Capturar","Favor de seleccionar un tipo destino.","warning");
				return;
			}
			
			if ($.trim($("#ctaBancaria option:selected").text())==""){											   
				Swal.fire("No tiene Cuenta bancaria","El beneficiario " + $("#cIdRFC").val() + " no tiene dada de alta alguna cuenta bancaria, por lo que no se puede proceder con el pago.","warning");
				return;
			} 
			
			if(!validaCapturaLayoutVuelos()){
				//alert("No ha capturado el layout de Vuelos. Verifique!!");
				return;
			}else{
				$("#btnLayoutBoletos").hide();
			}
		
			$('#cFolioPAGODIVERSO').val($('#id_caso').val());
			$("input[id='iva']").val($("input[id='nPorcIVAAplicable']").val());
			$("input[id='nIdConcepto']").val($('#TIPO_CONCEPTO option:selected').val());
			$("input[id='tConcepto2']").val($('#TIPO_CONCEPTO option:selected').text());
			$("input[id='noFactura2']").val($("input[id='cNoFactura']").val());
			$("input[id='tipoOper']").val($('#cIdTipoOperacion option:selected').val());
			$("input[id='impTotal']").val($("input[id='mImporteNeto']").val());
			$("#tipoBenef").val($('#cIdTipoRFC option:selected').text());
			$("#iva").val($("#nPorcIVAAplicable").val());
			$("#poriva").val($("#mImporteIVA").val());
			$("#cIdRFC_RelacionGasto2").val($("#cIdRFC").val());
			$("#cMes").val($("#fRecepcion").val().split("/")[1]);				
			queryFormPost("tRelacionGastClaveBeneficiarioRead","cIdRFC_RelacionGasto2",{async: false });
			var hayError='';
			$('.subtotall').each(function(){
				if ($(this).val()==''){
					hayError = hayError + this.name+', ';
				}
			});
			$('.paso01').each(function(){
				if ($(this).val()==''){
					hayError = hayError + this.name+', ';
				}
			});
			$('#cIdRFC').each(function(){
				if ($(this).val()==''){
					hayError = hayError + this.name+', ';
				}
			});
			
			var mensaje = validaCapturaCorreo();
			if (mensaje != "")
				hayError += "\n" + mensaje;
			
			if (hayError==''){

				$("#ID_DESTINO_GASTO").val( $("#destGasto").val() );
				querySelectPost("cat_TipoConceptoPagosDiversosRead", "TIPO_CONCEPTO",{async: false });
				concepto();
				
				var penalizacion = 0.00;
				penalizacion = (  parseFloat( $("#mImportePenalizacion").val() )  ) * 100 / 100;
				/*
				if(penalizacion > 0.00){
					$("#mImportePenalizacion").val(Math.round(Number(penalizacion * 1.16) * 100 ) / 100);		
				}
		
				*/
				var amortizacion = (  parseFloat( $("#mAmortizacionAnticipo").val() )  ) * 100 / 100;
				var auxIB = $("#mImporteBruto").val();
				
				queryFormPost("tContratoDiversosFactCreate",{async: false });
				queryFormPost("tPagoDiversoEncabezadoUpdate",{async: false });
				$("#mImporteBruto").val(auxIB);
				
				parent.document.getElementById("pb_save").disabled=true;
				
				if ($("#chk_IP").prop("checked"))
					queryFormPost("updateRGconOCdeIP",{async:false});
				
				if ( $("#noPenalty").val() !="-1")
					queryFormPost("actualizarEstatusPenas",{async: false });

				$("#nPorcAmortizacion").attr('readOnly', true);//URVP
				$("#mAmortizacionAcumulado").attr('readOnly', true);//URVP
				$("#btncIDContrato").attr('disabled', true);//URVP
				$("#destGasto").attr('disabled', true);//URVP
				$("#TIPO_OPERACION").attr('disabled', true);//URVP
				$("#cNoFactura").attr('readOnly', true);//URVP
				$("#mImporteSancion").attr('readOnly', true);//URVP
				$("#mImporteDevolucion").attr('readOnly', true);//URVP
				$("#mAmortizacionAnticipo").attr('readOnly', true);//URVP
				$("#mImportePenalizacion").attr('readOnly', true);//URVP
				$("#saldoAnticipo").attr('readOnly', true);//URVP
				$("#Descrip_Concepto").attr('readOnly', true);//URVP
				$("#cReferenciaBancaria").attr('readonly', true);
				$("#chk_RB").attr('disabled', true);
				cssReadOnly();
				
				$("#EditaFacturas").css('visibility', 'hidden');
				$("#ctaBancaria").attr('disabled', true);
				$("#estatusRecepcion").val("Pagada");
				
				$("#chk_IP").attr('disabled', true);
				queryFormPost("updateEstatusRecepcion,tPagoDiversoEncabezadoUpdateReferenciaBancaria",{async: false });
				
				$("#noRecepcion").attr('disabled', true);
				$("#noPenalty").attr('disabled', true);
	
				$("#chk_radicado").attr('disabled', true);
				if ($("#cEsRadicado").val()=="S")
              		queryFormPost("updateEsRadicadoDiv",{async: false });
				
				$("#chk_IF").attr('disabled', true);
				
				//Validar REPSE JGDS
				if( validaREPSE() ){
						if ( $("#cNumeroRepse").val() != ''){
							Swal.fire("Esta seguro de haber validado el numero de registro en el portal de la STPS?","Se le recuerda que es su responsabilidad de tener el expediente actualizado y confirmar que el registro del proveedor no haya sido removido/cancelado", "info");
							$("#repseDiv").show();
						} else { 
							Swal.fire("El pago indica que aplica el art. 15D al proveedor, sin embargo no se encontro su numero REPSE.", "Por favor actualice el proveedor con la informacion faltante para poder continuar. ", "warning");
							return false;
						}		
				}
				
				if ($("#cEsIngresoFiscal").val()=="S")
              		queryFormPost("updateEsIngresoDivRGOC",{async: false });
				
				if($("#cObservaciones").val() != ""){
					queryFormPost("tPAGODIVERSOEncObservacionesUpdate",{async: false });
				}
				$("#cObservaciones").attr('disabled', true);
				
				Swal.fire("OK!","Carátula guardada.", "success");
				$("#importeEjercer").val(Number($("#mImpEjercer").val()).toFixed(2));
				
				$(".pasoDos").show();
				$("#numPaso").val('2');
				$("#guardar").attr("disabled", true);
				$("#L03").click();
					
			}else{
				Swal.fire('Debe ingresar los siguientes datos: ', hayError, 'warning');
			}
		}
	
	}
	
	//TODO: Validar si se utiliza para algo
	function CAPITULO(){	
		var capitulo=$("#ep").val();
		$("#EPDES").val( capitulo.substring(31,32));
	}
	
	
	 function Grid(){
	 	var destinoGasto = $("#destGasto").val();
	 	
	 	if ($("#cEsIngresoFiscal").val()=="S"){
	 		//PRESUPESTO INGRESO FISCAL
	 		 //TODO: Agregar la opcion si es Pago con Ingreso Fiscal Recaudado
		 		window.open("AyudasEPIngresoFiscal.jsp", 'AyudaEPsPagos', 'status=1, width=900px, height=780px, left=100px, scrollbars=1');
		}else if ($("#cEsRadicado").val()=="N"){
	 		//PRESUPUESTO COMPROMETIDO NORMAL
	 		if ($("#fuenteFinanciamiento").val()=="4")
		 		window.open("AyudasEPOBRA.jsp?fuenteFinancimiento=4&destinoGasto=" + destinoGasto, 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
	 		else
		 		window.open("AyudasEPOBRA.jsp", 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
		}else if ($("#cEsRadicado").val()=="S"){
		 	//PRESUPUESTO COMPROMETIDO RADICADO
		 	if ($("#fuenteFinanciamiento").val()=="4")
		 		window.open("AyudasEPOBRA.jsp?fuenteFinancimiento=4&cEsRadicado=1", 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
	 		else
	 			if (relaciones)	relaciones.length=0;
		 		window.open("AyudasEPRadicado.jsp", 'AyudaEPsPagos', 'status=1, width=900px, height=780px, left=100px, scrollbars=1');
		}else{
				window.open("AyudasEPOBRA.jsp", 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
		}
		 
	 	return false;
	}
	
	function BuscaPoliza(){
		var szWhere = " nfolioPagoDiverso = " + $("#id_caso").val();
		var elMonto = "1";
		$("#laPoliza").val("");
		$("#docAplicado").val("");
		var szTabla = "TPAGODIVERSODOCAPLICADOREAD";
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
			for (var i = 0; i < j.length; i++) {
				$("#laPoliza").val(j[i].Col0);
				$("#docAplicado").val(j[i].Col1);
			}
		});
	}
	
	function validaPenalizacion(){
		var monto = "";
		monto = $("#mImportePenalizacion").val();
		if (parseInt(monto.indexOf("."),10) != parseInt(monto.lastIndexOf("."),10)){
			alert("No se puede captura mas de punto decimal, introduce el importe correcto.");
			$("#mImportePenalizacion").val("0");
			document.getElementById("mImportePenalizacion").focus();
		}
	}
  	
  	function cargaCtaBancariaRFC(){
		$("#campoRFC").val($.trim($("#cIdRFC").val()));
		querySelectPost("cargaCtaBancariasRFC", "ctaBancaria",{async: false });
		$("#mImporteBruto").val(0);
		queryFormPost("cargallevaAnticipoDiverso", {async: false });
		queryFormPost("cargatieneAticipoDiverso", {async: false });
		queryFormPost("remanentePagoDiversoAnticipoRead", {async: false });
		
		if($("#tieneAnticipo").val() == "0"){
			var tieneAntPlurianual = $("#cIDContrato").val().substr(0, 3);
			if(tieneAntPlurianual == "PLU"){
				queryFormPost("tieneAnticipoDiversoPlurianual", {async: false });
			}
		}
		queryFormPost("esProveedorExtranjero", {async:false});
	}
  	
  	function creaDialogFacturas(){
  		//queryFormPost("readMontoFacturasNeto", {async:false});
		//creaDTFacturas();
		togleDivFacts(0);
		modalValidaFact.show();
		
		$("#uploadFacturasDiv").hide();
		
		parent.document.getElementById("pb_save").disabled=true;
  	}
  	
  	function aceptarFacturas() {
  		bClicBtn = true;
		var aData = oTablevFact.fnGetData();
		$("#mImporteFacturas").val( quitaFmt( $("#mTotalFacturaV").val() ) );
		$(".subtotall").change();
		modalValidaFact.hide();
		calculaDatos();
		
		parent.document.getElementById("pb_save").disabled=false;
  	}
  	
  	function cerrarFacturas() {
  		parent.document.getElementById("pb_save").disabled=false;
  		calculaDatos();
  	}
  	
  	function calculaDatos(){
  		calculaIVA();
		revisaTipoFacturas();
		if($("#destGasto").val() == "CPGM"){						
			$("#mImporteComprobar").val($("#mImpEjercer").val());
			$("#mImporteComprobar").formatCurrency();
			creaDTAnticipos();
			$("#sDataFoliosING").val("");
			$("#sDataRemanenteING").val("");
			$("#totalAcumulado").val("");						
			$("#dialog-Anticipo").dialog("open");
		}
  	}
/*
    function creaDiagloFacturas(abrir){
			$( "#dialog-validaFact" ).dialog({
				autoOpen: false,
				height: 500,
				width: 850,
				modal: true,
				buttons: {
						"Aceptar": function(){
							bClicBtn = true;
							var aData = oTablevFact.fnGetData();
							$("#mImporteFacturas").val( quitaFmt( $("#mTotalFacturaV").val() ) );
							$(".subtotall").change();
							$( this ).dialog( "close" );							
						},
						"Cancelar": function(){
							bClicBtn = true;
							$( this ).dialog( "close" );
						}
					},
				beforeClose: function( event, ui ) {
					return bClicBtn;			
				},							
				close: function() {	
					parent.document.getElementById("pb_save").disabled=false;
					calculaIVA();
					revisaTipoFacturas();
					if($("#destGasto").val() == "CPGM"){						
						$("#mImporteComprobar").val($("#mImpEjercer").val());
						$("#mImporteComprobar").formatCurrency();
						creaDTAnticipos();
						$("#sDataFoliosING").val("");
						$("#sDataRemanenteING").val("");
						$("#totalAcumulado").val("");						
						$("#dialog-Anticipo").dialog("open");
					}
				},
				open: function(){
					queryFormPost("readMontoFacturasNeto", {async:false});
					creaDTFacturas();
					togleDivFacts(0);
					
					parent.document.getElementById("pb_save").disabled=true;
					
				}
			});
			
			if( abrir ){
				$( "#dialog-validaFact" ).dialog("open");	
			}
	}
	*/
	

	function creaDTFacturas(){
		oTablevFact = $('#grdValidaFacturas').dataTable(
		{
			"bProcessing": true,
			"bServerSide": true,
			"bDestroy": true,
			"bSort": true, 
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vfacturaspagos&qw=folioPago=<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%> AND tipoPago='PAGODIVERSO'" ,
			"bJQueryUI": true,
			"sScrollX": "100%",
			//"sScrollXInner": "100%",
			"sScrollY": "190px",
			"bPaginate": false,
			"bAutoWidth": true,
			"bInfo": true,
			aoColumns: [
						{ sName: "Serie" },
						{ sName: "Factura" },
						{ sName: "Importe" } ]	
		});
	}
	
	function creaDTAnticipos(){
		oTableIng = $('#tblSolicitudesIngreso').dataTable({
			"bProcessing": true,
			"bServerSide": true,
			"bDestroy": true,
			"bSort": true, 		
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_SaldoGreenMex",
			"bJQueryUI": true,
			"bPaginate": false,
			"bAutoWidth": true,
			"bInfo": true,
			fnInitComplete: function(settings, json) {
				$(".ING_SEL").each(function(){
					$(this).click(function(){
						actualizaTotales();
					});
				});
			  },
			aoColumns: [
						{ sName: "id" },
						{ sName: "folio" },
						{ sName: "cdescripcionpoliza" },
						{ sName: "mMontoRemanente" },
						{ sName: "RFC"}]	
		});
	}
	
	function togleDivFacts(nIdDiv){
		if( nIdDiv == 0){
			$("#uploadFacturasDiv").hide();
			$("#facturasCapturadasDiv").show();
			creaDTFacturas();
			queryFormPost("readMontoFacturasNeto", {async:false});
		}else{
			if ($("#esExtranjero").val()=="1")
				$('#uploadFacturasFrm').attr('src', "UploadFacturasExtranjeros.jsp");
			else
				$('#uploadFacturasFrm').attr('src', "UploadFacturas.jsp?tipo_pago=PAGODIVERSO&tipo_modulo=RGOC&contratoVales="+$("#vales_combustible").val() );
			$("#uploadFacturasDiv").show();
			$("#facturasCapturadasDiv").hide();
		}
			
	}
	
	function dlgJustificacion(){

		
		queryFormPost("retencionEliminada", {async:false});
		
		if( $("#lEliminaRetencion6IVA").val() == 1){
			creaDialogJustificacion(true);
			
		}	
	}	
	
	 function creaDialogJustificacion(abrir){
			$( "#dialog-Justificacion" ).dialog({
				autoOpen: false,
				height: 472,
				width: 558,
				modal: true,
				buttons: {
						"Aceptar": function(){
							togleJustificacion();											
						},
						"Cancelar": function(){
							bClicBtn = true;
							$( this ).dialog( "close" );
						}
					},
				close: function() {	
				},
				open: function(){
				}
			});
			
			if( abrir ){
				$( "#dialog-Justificacion" ).dialog("open");	
			}
	}
	
	function togleJustificacion(){
	
			queryFormPost("validaDoctoJustificacion", {async: false });
	
			if ($("#doctoCapturado").val() > 0) {
	  			parent.document.getElementById("pb_save").disabled = true;
	  			parent.document.getElementById("pb_send").click();
			}
			$( "#dialog-Justificacion" ).dialog("close");
		}	
	
	
	function changeIVA(){
		$("#nPorcIVAAplicable").val("0");
	}
	function referenciaBancaria(){
		var x = document.getElementById("ctaBancaria");
		var i=x.options.length;
		if ($("#chk_RB").prop("checked")){
			 document.getElementById("trctaBancaria").style.display = "";
			 document.getElementById("trReferenciaBancaria").style.display = "";
			 $("#cPagoReferenciado").val("S");
			(x.options[i] = new Option("Pago Referenciado","Pago_Referenciado")).setAttribute("Pago Referenciado","Pago_Referenciado");
		    x.selectedIndex = i;
		   
		}else{
			x.remove(i-1);
			x.selectedIndex = 0;
			document.getElementById("trctaBancaria").style.display = "block";
			document.getElementById("trReferenciaBancaria").style.display = "none";
			$("#cReferenciaBancaria").val("");		
		}
	}
	
	function readInfoRecepcionMat(){
		
		/*
		 * EHR2015 Al seleccionar el contrato valida el tipo de amortizacion que requiere:
		 * 1 Amortizacion por factor
		 * 0 Amortizacion normal.
		 */
		var tipoRecepcion = $("#noRecepcion").val().substr(0,2);	
		
		if( tipoRecepcion != "RA" )
			queryFormPost("tFactDiversoAnticipoRead", {async: false });
		else{
			$("#saldoAnticipo").val("0.00");
			$("#nPorcAsignacion").val("0.00");
			$("#mAmortizado").val("0.00");
		}
		
		$("#tipoAmortizacion").val("");
		queryFormPost("readTipoAmortiza", {async:false});

		queryFormPost(
			{
				queryName:"readInfoRecepcionMat",
				async:false,
				callback:function(){
					
					//EHR2015 Guarda el monto original para la recepcion
					importeRecepcionOriginal = Number(Number(quitaFmt( $("#mImporteBruto").val() ) ).toFixed(2)) ;
					
					//Llamar al recalculo de cosas
					var importee = 0.00;
					importee = Number(quitaFmt($("#mImporteBruto").val()));
					importee = importee.toFixed(2);
					
					$("#mImporteBruto").val(importee);
					$("#importeBrutoAux").val(importee);
					
					importee = Number(quitaFmt($("#mImporteIVA").val()));
					importee = importee.toFixed(2);
					$("#mImporteIVA").val(importee);
					
					importee = Number(quitaFmt($("#mOtrosImpuestos").val()));
					importee = importee.toFixed(2);
					$("#mOtrosImpuestos").val(importee); 
					
					importee = Number(quitaFmt($("#mImporteNeto").val()));
					importee = importee.toFixed(2);
					$("#mImporteMasIva").val(importee);
					
					calculaIVA();
					
					$("#mImportePenalizacion").val("0.00");
					$("#mImporteDevolucion").val("0.00");
					$("#destGasto").attr('disabled', false);
				}
			}
		);
		
		/*Habilitar combo para penas y deducciones*/
		queryFormPost("penaltyExist", {async:false});
		if($("#tienePenas").val()!="0"){
			$("#penas").css('display', 'block');
			$("#penasI").css("display","block");
		} 
		
	}
	
	function readInfoPenas(){
		if($("#noPenalty").val() != "-1") {
			queryFormPost({
				queryName:"readPenaltyPayment",
				async:false,
				callback:function(){
					
					var importe = Number(quitaFmt($("#mImportePenalizacion").val()));
					importe = importe.toFixed(2);
					$("#mImportePenalizacion").val(importe);
					calculaIVA();		
				}
			});
		} else {
			$("#mImportePenalizacion").val(0);
			calculaIVA();
		}
	}
	
	function consMovimientos(){
		var szTabla = "CLAVES_DIVERSO_CARGA";
		var elMonto = "";
	    var szWhere = " AND nfolioPagoDiverso = " + $("#id_caso").val();
	    var capturado = 0.00;
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: elMonto, MaxReg: szWhere, ajax: 'false'}, function(j){
			for (var i = 0; i < j.length; i++){
				$('#grdCompromisos').dataTable().fnAddData([ j[i].Col0, j[i].Col1, j[i].Col2 ]);
				capturado += Number(j[i].Col2);
			}
			$('#saldoCompM').val(capturado.toFixed(2));
		});
	}
	
	function showDivOficio(esUpdate){
		var cmpName = "oficioDelegatorio" + (esUpdate?"Update":"");
		var divName = "oficioDelegatorioCaptura" + (esUpdate?"Update":"");
		if( $("#" + cmpName ).is(":checked") )
			$("#"+divName).show();
		else
			$("#"+divName).hide();
	}
	
	function mostrarAcompanantes(){

		var cDestino_Gasto = $("#destGasto").val();
		var cTipoConcepto = $("#TIPO_CONCEPTO").val();
		var bRegresa = true;
		
		if (cDestino_Gasto == "CPRO" && cTipoConcepto == "PN"){
			var cPartida = $("#ep").val();
			cPartida = cPartida.substring(31, 36);		
					
			if(cPartida == "38501"){
				$("#lblNumAcompanantes").show();
				$("#nAcompanantes").show();	
							
				if($("#nAcompanantes").val() == "0" || $("#nAcompanantes").val() == ""){
					bRegresa = false;
					Swal.fire("Capturar","Favor de Capturar el Numero de Acompañantes para continuar...","warning");
				}		
			}
		}
		
		return bRegresa;
	}

	function updateAcompañantes(){
		var cDestino_Gasto = $("#destGasto").val();
		var cTipoConcepto = $("#TIPO_CONCEPTO").val();
		var bRegresa = true;
		if (cDestino_Gasto == "CPRO" && cTipoConcepto == "PN"){
			var cPartida = $("#ep").val();
			cPartida = cPartida.substring(31, 36);		
					
			if(cPartida == "38501"){
				if($("#nAcompanantes").val()=="" || $("#nAcompanantes").val()=="0"){
					Swal.fire("Capturar","Favor de Capturar el Numero de Acompañantes","warning");
					bRegresa = false;		
				}
				queryFormPost("tPAGODIVERSOEncabezadoAcompanantesUpdate", {async:false});		
			}
		}
		
		return bRegresa;		
	}
	
	/*vgc290916 Valida que la EP que se intenta agreagar no exista ya en el pago.*/
	function validaEPCapturada(epFind){
		var existe = false;
		var eps = $("#grdCompromisos").dataTable().fnGetData();
		for( i = 0; i < eps.length; i++ ){
			if( eps[i][1] == epFind ){
				existe = true;
				break;
			}
		}
		
		return existe;
	}
	
	function insertaObjeto( folioIngresoP, EPP, montoP, mesP){
																
		if(!relaciones)
			relaciones = [];	
		
		var obj = { folioIngreso : folioIngresoP ,
					EP:EPP,
				    monto:montoP,
					mes:mesP
				  };
		
		relaciones[relaciones.length] =  obj ;
	}
	
	function validaEPsCECFOR(){
		var bReturn = true;
		queryFormPost("esEPsCECFORPagoDIV", {async:false});
		
		if( $("#TIPO_CONCEPTO").val() == "AL" && $("#esEPCECFOR").val() == "1"){
			alert("EP capturada pertene a CECFOR, NO se puede realizar esta combinacion con el concepto ALMACEN.\nSeleccione otro tipo de concepto para esta EP.");
			bReturn = false;		
		}
		
		return bReturn;
	}
	
	function validaPartidaGtosRepresentacion(){
		var bRegresa = true;
		
		$("#esPartidaViaticos").val("0");
		var cPartida = $("#ep").val();
		cPartida = cPartida.substring(31, 36);		
		$("#cPartida").val(cPartida);
		queryFormPost("esPartidaViaticosRead", {async : false});
		
		if(cPartida == "38501" || $("#esPartidaViaticos").val() == "1"){			
			alert( "Queda bajo su responsabilidad la información capturada en los conceptos e informes, misma que séra pública en " + 
					"seguimiento al articulo 70 de la Ley General de Transparencia y Acceso a la Información Pública." );
		}
		
		return bRegresa;
	}
	

	function validaPagoConPenalizacion() {
		var penalizacion = 0.00;
		
		penalizacion = (  parseFloat($("#mImportePenalizacion").val())  ) * 100 / 100;
		
		if ( penalizacion > 0.00 ) {
			
			var iva = 0.00;
			iva = parseFloat($("input[id='nPorcIVAAplicable']").val() / 100) * (parseFloat($("input[id='subTotal_2']").val()));
			
			iva = Number(quitaFmt($("#mImporteIVA").val()));
			iva = iva.toFixed(2);
			$("#mImporteIVA").val(iva);

		}
	}
	
	function validaEsIngresosPropios(){
		var bRegresa = false;		
		
		$("#EP").val($("#ep").val());
		
		var cIngPropio = $("#EP").val();
		cIngPropio = cIngPropio.substring(39, 40);		
		
		if(cIngPropio == "4" ){			
			bRegresa = true;
		}
		
		return bRegresa;
	}
	
	function validaDispIngresoPropio(){
	
		var bRegresa = true;
		
		$("#mMovimiento").val("0");
		$("#nValidaSuficienciaIP").val("0");
				
		var mMovimiento = 0.00;
		
		mMovimiento = Number(quitaFmt($("#montoDev").val())).toFixed(2);
		
		$("#mMovimiento").val(mMovimiento);
				
		queryFormPost("validaSuficienciaRegIPRead", {async : false});
			
		if($("#nValidaSuficienciaIP").val() == "1"){
			bRegresa = false;
		}
		
		return bRegresa;
	}	
		
	function abrirDlgBoletaje(){		
		
		var folioPago = $("#nFolioPago").val();
		var centroContable = $("#cCentroContable").val();
		var importePago = Number($("#mImpEjercer").val());
		var nFolioVuelos = $("#nFolioVuelos").val();
		
		importePago = importePago.toFixed(2);	
		
		var login = $("#u_login").val();
		
		window.open(
						'SeleccionaVuelos.jsp?folioPago='+folioPago+'&centroContable='+centroContable+'&importePago='+importePago+'&ulogin='+login+'&nFolioVuelos='+nFolioVuelos+'&cTipoPago='+$("#cTipoPago").val(),
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=650"
					);
	}
	
	function habilitaBtnCargaLayout(){
		queryFormPost("esProveedorBoletajeRead", {async:false});
		
		if($("#esProvBoletaje").val() == "1"){
 			$("#btnLayoutBoletos").show();
		}else{
 			$("#btnLayoutBoletos").hide();
		}
	}
	
	function validaCapturaLayoutVuelos(){
		var bReturn = true;
		
		if($("#esProvBoletaje").val() == "1"){
			queryFormPost("validaCapturaLayoutVuelosRead", {async:false});
			if($("#capturaLayoutVuelos").val() != "1"){
				alert("No ha capturado Vuelos para el Pago. Verifique!!");
				bReturn = false;
			}else{
				bReturn = insertarFolioLayout();
			}
		}
		
		return bReturn;
	}
	
	function insertarFolioLayout(){	
		
		var bReturn = false; 
		
		queryFormPost("importeVuelosDetCapturadosRead", {async:false});
		
		var mImportePago = Number($("#mImpEjercer").val());
		var mImporteVuelos = Number($("#mImporteVuelos").val());
					
		mImportePago = mImportePago.toFixed(2);
		mImporteVuelos = mImporteVuelos.toFixed(2);
		
		if(mImportePago == mImporteVuelos){
			queryFormPost( { queryName:"tPagoDiversoBoletajeAvionCreate", async:false, callback:function(){ bReturn = true;} });					
		}else{
			alert("El importe Total de Vuelos es diferente al total del Pago. Verifique!!");
		}
		
		return bReturn;
	}
	
	function actualizaFolioVueloPagado(){
		queryFormPost("vuelosDetEsPagadoUpdate", {async : false});
	}
	function verReferencia(){
		
		$("#dlgSeleccionaFonden").dialog("open");
		
		if( <%=id_oper%> != 1 ){
			queryFormPost( "proyectoFondenRead", {async:false} );
			$("#verProyectoFondenDiv").css('display', 'block');
			$("#selproyectoFondenDiv").css('display', 'none');
		}
		
		
	}
	
	
	function validaREPSE() {
			var numRepse = false;
			queryFormPost("consultarAplica15DRead", {async: false});
			
			if ( $("#cAplica15D").val() == 'S') {
				queryFormPost("consultaProveedorRead", {async: false});
				numRepse = true;
			}
			return numRepse;
	}
	
	function borraOfiDelegatorio(){

		if($("#firmanteOficioExiste").val() == "Existe"){
			queryFormPost({	queryName : "borrarOficioDelegatorio", async : false, callback : function(){ 															
								msn = "Oficios borrados del pago " + $("#cTipoPago").val() + " no. " + $("#nFolioPago").val();
								} 
						});
			$("#cFolioOficioUpdate").val("");
			$("#dFechaOficioUpdate").val("");
			$("#cNombreTitularUpdate").val("");
			$("#cApellidoPaternoTitularUpdate").val("");
			$("#cApellidoMaternoTitularUpdate").val("");
			$("#cPuestoTitularUpdate").val("");
		}
		
		if($("#firmanteOficioVoBoExiste").val() == "Existe"){
			queryFormPost({	queryName : "borrarOficioDelegatorioVoBo", async : false, callback : function(){ 															
								msn = "Oficios borrados del pago " + $("#cTipoPago").val() + " no. " + $("#nFolioPago").val();
								} 
						});
			$("#cFolioOficioVoBoUpdate").val("");
			$("#dFechaOficioVoBoUpdate").val("");
			$("#cNombreTitularVoBoUpdate").val("");
			$("#cApellidoPaternoTitularVoBoUpdate").val("");
			$("#cApellidoMaternoTitularVoBoUpdate").val("");
			$("#cPuestoTitularVoBoUpdate").val("");
		}							
		
	}
	
	function habilitaCheckIP(){
	
		$("#habilitaCheckIP").val("0");
		queryFormPost( "validaHabilitaCheckIPRGOC_Read", {async:false} );
		
		if($("#habilitaCheckIP").val() == "1"){
			habilitaCheckIP = true;
		}else{
			habilitaCheckIP = false;
		}
		
	}
	
	function muestraCalculoCedular(){
		$("#tieneCedular").val("0");
		
		queryFormPost("tieneImpuestoCedularPDIV_Read", {async : false});
		
		if($("#tieneCedular").val() == "1"){
			$("#btnCalculaCedular").show();
		}else{
			$("#btnCalculaCedular").hide();
		}		
	}
	
	function calculaCedular(){
		queryFormPost("calculaImpuestoCedularPDIV_Read", {async : false});
		$("#mImporteFacturas").val( Number(quitaFmt( $("#mTotalFacturaV").val() )).toFixed(2) );
	}
	
	
function registraCalendarioEP(ep, monto) {
	var errMsg = "";
	
	$.ajax({
		url : '../egresos/GeneraCalendario',
		dataType : 'json',
		type : "post",
		data : {
			"importeEP" : monto,
			"ep" : ep
		},
		async : false,
		success : function(json) {
			var exito = json.success;
			if (exito != "true") {

				var msg = json.data_1.result;
				errMsg = "No fue posible guardar el calendario debido al error:  " + msg;
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
				+ textStatus + "\n" + errorThrown);
			errMsg = "Error guardando calendario";
		}
	});

	return errMsg;
}


function revisaTipoFacturas(){
	queryFormPost("readEsPPD", {async : false});
	
		if( $("#esPPD").val() != "0" ){
			Swal.fire ('IMPORTANTE', 'La factura adjunta tiene el metodo de pago PPD por lo que es indispensable posteriormente solicitar al proveedor el CFDI de complemento de pago.', 'warning');
			$("#cIDRFC").val($("#cIdRFC").val());
			$("#EditaCorreoE").css('visibility', 'visible');
			creaDialogCorreo(true);
			$("#capturaCorreo").show();
		} else {
			$("#EditaCorreoE").css('visibility', 'hidden');
			$("#capturaCorreo").hide();
		}
}

function validaCapturaCorreo(){
	let mensaje = "";
	if( $("#esPPD").val() != "0" &&  $("#correoActual").val() ==""){
		mensaje = "No se capturo el correo para seguimiento y la(s) factura(s) son PPD"
	}
	
	return mensaje;
}

function creaDialogCorreo() {
	
	queryFormPost("mostrarCorreoRead", { async: false });
	modalCorreo.show();
	
}

function creaDialogOpinion(){
	
	modalOpinion.show();
	 

}

/**
 * Valida el archivo PDF de Opinión de Cumplimiento (Formato 32D) contra el SAT.
 * Envía el archivo al backend para verificación y muestra el resultado al usuario.
 */
function validarOpinionCumplimiento() {
	
	// Validar que exista el RFC del contrato
	var rfc = $.trim($('#cIdRFC').val());
	if (!rfc || rfc === '') {
		Swal.fire({
			icon: 'warning',
			title: 'RFC requerido',
			text: 'Debe seleccionar un contrato con RFC válido antes de validar la opinión de cumplimiento'
		});
		return;
	}
	
	// Validar que se haya seleccionado un archivo
	var fileInput = $('#pdfOpinionCumplimiento32D')[0];
	if (!fileInput || fileInput.files.length === 0) {
		Swal.fire({
			icon: 'warning',
			title: 'Archivo requerido',
			text: 'Debe seleccionar un archivo PDF con la opinión de cumplimiento'
		});
		return;
	}
	
	var file = fileInput.files[0];
	
	// Validar que sea un archivo PDF
	if (file.type !== 'application/pdf') {
		Swal.fire({
			icon: 'warning',
			title: 'Tipo de archivo inválido',
			text: 'El archivo debe ser de tipo PDF'
		});
		return;
	}
	
	// Cerrar el modal
	modalOpinion.hide();
	
	// Bloquear la pantalla mientras se procesa
	$.blockUI({
		message: '<h4>Validando opinión de cumplimiento con el SAT...</h4><p>Por favor espere.</p>',
		css: { 
			border: 'none', 
			padding: '15px', 
			backgroundColor: '#000', 
			'-webkit-border-radius': '10px', 
			'-moz-border-radius': '10px', 
			opacity: .5, 
			color: '#fff' 
		}
	});
	
	// Crear FormData para enviar el archivo y el RFC
	var formData = new FormData();
	formData.append('pdfOpinionCumplimiento32D', file);
	formData.append('cIdRFC', rfc);
	
	// Enviar al backend
	$.ajax({
		url: '../validateOpinion32D',
		type: 'POST',
		data: formData,
		processData: false,  // No procesar los datos
		contentType: false,  // No establecer contentType (se establece automáticamente con boundary)
		success: function(response) {
			// Desbloquear pantalla
			$.unblockUI();
			
			console.log('Respuesta de validación:', response);
			
			// Verificar si la validación fue exitosa y positiva
			if (response.success && response.isPositive) {
				Swal.fire({
					icon: 'success',
					title: 'Validación Exitosa',
					text: 'El formato 32D se validó exitosamente con el SAT. Puede continuar el pago',
					confirmButtonText: 'Aceptar'
				});
				
				// Limpiar el input de archivo
				$('#pdfOpinionCumplimiento32D').val('');
				
			} else {
				// Opinión negativa o error en la validación
				var errorMessage = 'El archivo con el formato 32D no es procesable o no es positivo, envie al administrador.';
				
				if (response.message) {
					errorMessage += '\n\nDetalle: ' + response.message;
				}
				
				Swal.fire({
					icon: 'error',
					title: 'Error de Validación',
					text: errorMessage,
					confirmButtonText: 'Aceptar'
				});
			}
		},
		error: function(xhr, status, error) {
			// Desbloquear pantalla
			$.unblockUI();
			
			console.error('Error en validación:', status, error);
			console.error('Respuesta del servidor:', xhr.responseText);
			
			var errorMessage = 'El archivo con el formato 32D no es procesable o no es positivo, envie al administrador.';
			
			// Intentar extraer mensaje de error del servidor
			try {
				var errorResponse = JSON.parse(xhr.responseText);
				if (errorResponse.error) {
					errorMessage += '\n\nDetalle: ' + errorResponse.error;
				}
			} catch (e) {
				// Si no se puede parsear, usar mensaje genérico
				if (xhr.status === 401) {
					errorMessage = 'Su sesión ha expirado. Por favor, inicie sesión nuevamente.';
				} else if (xhr.status === 0) {
					errorMessage = 'No se pudo conectar con el servidor. Verifique su conexión a internet.';
				}
			}
			
			Swal.fire({
				icon: 'error',
				title: 'Error',
				text: errorMessage,
				confirmButtonText: 'Aceptar'
			});
		}
	});
}

/**
 * Verifica si ya existe un documento de Opinión de Cumplimiento (SAT Formato 32D)
 * capturado en el sistema para el trámite actual.
 * Esta función realiza una petición AJAX síncrona (bloqueante).
 * 
 * @return true si existe el documento, false en caso contrario
 */
function verificarOpinionCumplimiento() {
	var hayDocumento = false;
	
	$.ajax({
		url: '../validateOpinion32D',
		type: 'GET',
		async: false,  // Petición síncrona (bloqueante)
		dataType: 'json',
		success: function(response) {
			console.log('Verificación de opinión:', response);
			if (response.success) {
				hayDocumento = response.hasDocument;
			}
		},
		error: function(xhr, status, error) {
			console.error('Error verificando opinión:', status, error);
			// En caso de error, asumimos que no hay documento
			hayDocumento = false;
		}
	});
	
	return hayDocumento;
}

function mostrarCorreo(){
	queryFormPost("mostrarCorreoRead",{async: false});
} 
function validarCorreo(){
        
    emailRegex = /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
    
    if (emailRegex.test($("#correoActual").val())) {
		$("#correo").val($("#correoActual").val());
		//Actualizar datos y cerrar
		if($("#nombreCorreo").val()==""){
			Swal.fire("Capturar Nombre", "Debe capturar el nombre a la cual se dirigirá el correo", "warning")
		} else {
			$("#paternoCorreo").val($("#aPCorreo").val());
			$("#maternoCorreo").val($("#aMCorreo").val());
			$("#nCorreo").val($("#nombreCorreo").val());
			$("#cCargo").val($("#cargo").val());
			queryFormPost("updateCorreoActualizado",{async: false});
			modalCorreo.hide();
		}
	} else {
     	Swal.fire("Correo Invalido", "El correo no es valido, intente de nuevo", "error");
     	return;
    }
}

function actualizaTotales(){
	var suma = Number( quitaFmt( $("#totalAcumulado").val() ) );
	var comprobar = Number( quitaFmt( $("#mImporteComprobar").val() ) );
	var diferencia;
	$(".ING_SEL").each(
		function(){
			 if( $(this).attr("checked") ){
			 	  var sumando = buscaTotal( $(this).attr('id') );
			 	  suma += sumando;
			 }
	});
	$("#totalAcumulado").val( suma.toFixed(2) );	
	
	diferencia = suma.toFixed(2) - comprobar.toFixed(2);
	if(diferencia < 0){
		Swal.fire({ icon: "warning",
					text: "El remanente del ingreso es menor al importe a ejercer, favor de agregar otro ingreso. \n Remanente: " + suma.toFixed(2) + "\n Ejercer: " + comprobar.toFixed(2) });		
		return;
	}
	
}

function buscaTotal( idInput ){
	var matrizVal = $("#tblSolicitudesIngreso").dataTable().fnGetData();
	var val = 0.00;
	
	for( cnt = 0; cnt < matrizVal.length; cnt++){
		if( matrizVal[cnt][1] == idInput ){
			val = parseFloat( quitaFrmt( matrizVal[cnt][3] ) );
		}
	}
	return val;
}

function quitaFrmt(fld) {
	var valcol = fld.toString();
	valcol = valcol.replace(/[$]/g, "");
	valcol = valcol.replace(/,/g, "");
	return valcol;
}


function aceptarOli(){
	var vEP = $("#EP").val();
	var vCapitulo = "%" + vEP.substring(31, 36) + "%";
	var vCartera = vEP.substring(44, 55);
	var vUnidEj = vEP.substring(56, 59);
	var vUnidRe = vEP.substring(60, 64);
	var url = '../proimpro/readInfo';
	$.ajax({
		url : url,
		dataType : 'json',
		data : {
			"accion" : "READ_IMPORTEOLI",
			"cartera" : vCartera,
			"capitulo" : vCapitulo,
			"oli": $("#cOLI").val(),
			"ue":vUnidEj
		},
		async : false,
		success : function(RS) {
			var exito = RS.success;
			if(exito=="true"){
				var MontoOlis = RS.data_1.result;
				$("#cartera").val( vCartera );
				$("#capitulo").val( vEP.substring(31, 36) );
				$("#UE").val( vUnidEj );
				$("#mImporteCartera").val("0.00");
				queryFormPost("vAcumulaImporteCarteraEP", {async: false });
				var importeTotal = Number( $("#mImporteCartera").val() ) + Number( $("#montoDev").val() );
				var importeTotal = importeTotal.toFixed(2);
				
				if( Number( importeTotal ) <= Number( MontoOlis ) ){
					cambioMovmientos();	
				}else{
					alert("El Importe Capturado sobregira la Cartera ");
				}
			}else{
				var msg = RS.data_1.result;
				alert("Error al intentar validar OLI:\n"+msg);
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
			r = true;
		}
	});
	modalOli.hide();
}


function cerrarOli() {
	breturnVal = false;
	modalOli.hide();
}

</script>
</head>
<br/>
<body id="dt_example">
	<div id="container" style="width: 100%" class="container" >
		<form id="frmcontratoDeObra">
			
			<input type="hidden" id="vales_combustible"       value="false"/>
			<input type="hidden" id="iva"    name="iva"    value="1.0"/>
			<input type="hidden" id="correcto"    name="correcto"    value="N"/>
			<input type="hidden" id="esEFO"       name="esEFO"       value="N"/>
			<input type="hidden" id="rfcValidar"  name="rfcValidar"  value=""/>
			<input type="hidden" id="TipoAutorizacion" 	name="TipoAutorizacion" value=""/>
			<input type="hidden" id="mOtrosImpuestosEP" name="mOtrosImpuestosEP" value="0"/>
			<input type="hidden" id="lEliminaRetencion6IVA"  name="lEliminaRetencion6IVA"  value="0"/>
			<input type="hidden" id="doctoCapturado" name="doctoCapturado" value="0"/>
			
			<input name="cProyectoFonden" id="cProyectoFonden" value="" type="hidden" />
			<input name="nFondenCapturado" id="nFondenCapturado" value="0" type="hidden" />
			<input name="tipoAmortizacion" id="tipoAmortizacion" value="" type="hidden" />
			<input name="mesAbierto" id="mesAbierto" value="" type="hidden" />
			<input name="otrosImpuestos" id="otrosImpuestos" type="hidden" value="0"/>
			<input name="nFolioPago" id="nFolioPago" type="hidden" value="<%=c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>">
			<input name="cTipoPago" id="cTipoPago" type="hidden" value="PAGODIVERSO"/>
			<input name="tipoTramite" id="tipoTramite" type="hidden" value="<%=idTipoCaso %>"/>
			<input name="campoRFC" id="campoRFC" type="hidden"/>
			<input name="totFacturas" id="totFacturas" type="hidden" value="0"/>
			<input name="esRelacionGastos" id="esRelacionGastos" type="hidden" value="S"/>
			<input name="BanderaDiversoRG" id="BanderaDiversoRG" type="hidden" value=""/>
			<input name="tieneRetencion" id="tieneRetencion" type="hidden" value=""/>
			<input name="fuenteFinanciamiento" id="fuenteFinanciamiento" type="hidden" value="1"/>
			<input name="nPorcIVAAplicable2" id="nPorcIVAAplicable2" type="hidden" value="0"/>
			<input name="importeTotalEncabezado" id="importeTotalEncabezado" type="hidden" value="0"/>
			<input name="importeTotalDetalle" id="importeTotalDetalle" type="hidden" value="0"/>
			<input name="llevaAnticipo" id="llevaAnticipo" type="hidden"/>
			<input name="tieneAnticipo" id="tieneAnticipo" type="hidden"/>
			<input name="cEstatusPago" id="cEstatusPago" type="hidden" value=""/>
			<input name="estatusRecepcion" id="estatusRecepcion" type="hidden" value=""/>
			
			<!-- PARA LKS EP -->
			<input type="hidden" value="0" id="m23IVA" name="m23IVA"/>
			<input type="hidden" value="0" id="mIMDT" name="mIMDT"/>
			<input type="hidden" value="0" id="mCNIC" name="mCNIC"/>
			<input type="hidden" value="0" id="mObra5" name="mObra5"/>
			<input type="hidden" value="0" id="mAmortizado" name="mAmortizado"/>
			<input type="hidden" value="0" id="mISRHonorarios" name="mISRHonorarios"/>
			<input type="hidden" value="0" id="mImporteFlete4" name="mImporteFlete4"/>
			<input type="hidden" value="0" id="mISRArrenda" name="mISRArrenda"/>
			<input type="hidden" value="0" id="mPenalizacion" name="mPenalizacion"/>
			<input type="hidden" value="0" id="mRetImpuestoCedular" name="mRetImpuestoCedular"/> 
			<input type="hidden" value="0" id="mTesofe" name="mTesofe"/>
			<input type="hidden" value="0" id="DCD_ISR_D" name="DCD_ISR_D"/>
			<input type="hidden" value="0" id="DCD_IVA_D" name="DCD_IVA_D"/>
			<input type="hidden" value="0" id="DCD_MIL5_D" name="DCD_MIL5_D"/>
			<input type="hidden" value="0" id="DCD_MIL2_D" name="DCD_MIL2_D"/>
			<input type="hidden" value="0" id="DCD_CONTRIBUCION_D" name="DCD_CONTRIBUCION_D"/> 
			<input type="hidden" value="0" id="DCD_OTRAS_RET_D" name="DCD_OTRAS_RET_D"/>
			<input type="hidden" value="0" id="DCD_PENALIZACION_D" name="DCD_PENALIZACION_D"/> 
			<input type="hidden" value="0" id="DCD_SANCION_D" name="DCD_SANCION_D"/>
			<input type="hidden" value="0" id="DCD_DEVOL_D" name="DCD_DEVOL_D"/>
			<input type="hidden" value="0" id="DCD_AMORT_D" name="DCD_AMORT_D"/>
			<input type="hidden" value="0" id="DCD_RETENCION_D" name="DCD_RETENCION_D"/>
			<input type="hidden" value="0" id="mIVA_D" name="mIVA_D"/>
			<input type="hidden" value="0" id="CamInst" name="CamInst"/>
			
			<input type="hidden" value="0" id="mImporteFlete23" name="mImporteFlete23"/>
			<input type="hidden" value="0" id="mImporteIvaArrenda" name="mImporteIvaArrenda"/>
			<input type="hidden" value="" id="lHaySaldoAnticipo" name="lHaySaldoAnticipo"/>
			<input type="hidden" value="0" id="TotalNeto" name="TotalNeto"/>
			<input type="hidden" value="0" id="TotalNetoN" name="TotalNetoN"/>
			<input type="hidden" id="montoprevio" name="montoprevio"/>
			<input type="hidden" value="0" id="TotalRetenciones" name="TotalRetenciones"/> 
			<input type="hidden" value="0" id="campoRetencion" name="campoRetencion"/>
			<input type="hidden" value="0" id="mIVA" name="mIVA"/>
			<input type="hidden" value="DIVERSO" id="TO_TIPO_DOCTO" name="TO_TIPO_DOCTO"/> 
			<input type="hidden" value="0" id="m23IVA2" name="m23IVA2"/>
			<input type="hidden" value="0" id="mIMDT2" name="mIMDT2"/>
			<input type="hidden" value="0" id="mCNIC2" name="mCNIC2"/>
			<input type="hidden" value="0" id="mObra52" name="mObra52"/> 
			<input type="hidden" value="0" id="mISRHonorarios2" name="mISRHonorarios2"/> 
			<input type="hidden" value="0" id="mImporteFlete42" name="mImporteFlete42"/> 
			<input type="hidden" value="0" id="mISRArrenda2" name="mISRArrenda2"/>
			<input type="hidden" value="0" id="mPenalizacion2" name="mPenalizacion2"/> 
			<input type="hidden" value="0" id="mRetImpuestoCedular2" name="mRetImpuestoCedular2"/> 
			<input type="hidden" value="0" id="mTesofe2" name="mTesofe2"/> 
			<input type="hidden" value="0" id="CamInst2" name="CamInst2"/> 
			<input type="hidden" id="laPoliza" />
			<input type="hidden" id="docAplicado" value=""/> 
			<input type="hidden" value="0" id="mAmortizacionAnticipoMasIva" name="mAmortizacionAnticipoMasIva"/> 
			<input type="hidden" value="0" id="mAmortizacionAnticipoMasIvaEP" name="mAmortizacionAnticipoMasIvaEP"/> 
			<input type="hidden" value="" id="rowsAffected" name="rowsAffected"/>
			<input type="hidden" name="cFolioPAGODIVERSO" id="cFolioPAGODIVERSO"/> 
			<input type="hidden" name="tipoRet" id="tipoRet" value=""/> 
			<input type="hidden" name="resImpRet" id="resImpRet" value=""/> 
			<input type="hidden" name="nMes" id="nMes" value="2"/> 
			<input type="hidden" name="cIdCuentaContable" id="cIdCuentaContable" value="" /> 
			<input type="hidden" name="numPaso" id="numPaso" value="1"/>
			<input type="hidden" name="TOTALSUBCUENTA" id="TOTALSUBCUENTA"/> 
			<input type="hidden" value="2012" id="cEjercicio" name="cEjercicio"/> 
			<input type="hidden" value="" id="tiene" name="tiene"/>
			<input type="hidden" id="cRamo" name="cRamo"/>
			<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable"/> 
			<input type="hidden" id="OIRAUSU" name="OIRAUSU" />
			<input type="hidden" name="cIdRFC_RelacionGasto2" id="cIdRFC_RelacionGasto2"/> 
			<input type="hidden" name="sumareten" id="sumareten"/>
			<input type="hidden" name="doster" id="doster"/>
			<input type="hidden" value="0" name="cIdTipoDocumento" id="cIdTipoDocumento"/> 
			<input type="hidden" id="cllave" name="cllave" />
			<input type="hidden" id="nFolioCompromiso" name="nFolioCompromiso" value="0" /> 
			<input type="hidden" id="cCentroContable" name="cCentroContable"/> 
			<input type="hidden" id="cMes" name="cMes"/> 
			<input type="hidden" id="aEjercicioFiscal" value="2012" name="aEjercicioFiscal"/> 
			<input type="hidden" id="cEvento" name="cEvento" value=""/> 
			<input type="hidden" id="contra2" name="contra2" value=""/> 
			<input type="hidden" id="partida" name="partida" value=""/> 
			<input type="hidden" id="vOGT" name="vOGT" value=""/> 
			<input type="hidden" id="obs" name="obs"/> 
			<input type="hidden" id="cIdContrato" name="cIdContrato" value=""/> 
			<input type="hidden" id="tConcepto2" name="tConcepto2"/> 
			<input type="hidden" id="nIdConcepto" name="nIdConcepto"/> 
			<input type="hidden" id="codSIAFF" name="codSIAFF"/>
			<input type="hidden" id="saldoCompA" name="saldoCompA" style="background-color: #C0C0C0"/> 
			<input type="hidden" id="saldoPorEjercer" name="saldoPorEjercer"/> 
			<input type="hidden" id="ID_DESTINO_GASTO" name="ID_DESTINO_GASTO"/> 
			<input type="hidden" id="ID_TIPO_MOVIMIENTO" name="ID_TIPO_MOVIMIENTO"/>	
			 
			<!--<input name="ALM" type="hidden" id="ALM" value="1">-->
			<input type="hidden" id="campo" name="campo" value=""/> 
			<input type="hidden" id="tablaEnc" name="tablaEnc" value=""/> 
			<input type="hidden" id="campoCondicion" name="campoCondicion" value=""/> 
			<input type="hidden" id="tablaDet" name="tablaDet" value=""/> 
			<input type="hidden" id="tipoAplicar" name="tipoAplicar" value=""/> 
			<input type="hidden" name="cDocumento" id="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>"/> 
			<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value=""/> 
			<input type="hidden" name="nombre" id="nombre" value=""/>
			<input type="hidden" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" /> 
			<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" /> 
			<input type="hidden" name="FECHA_CARGA" id="FECHA_CARGA" value="<%=today%>" /> 
			<input type="hidden" name="cIDContratoObra" id="cIDContratoObra" /> 
			<input type="hidden" name="cartera" id="cartera" value=""/> 
			<input type="hidden" name="capitulo" id="capitulo" value=""/> 
			<input type="hidden" name="UE" id="UE" value=""/> 
			<input type="hidden" name="EP" id="EP" value=""/> 
			<input type="hidden" name="mImporteCartera" id="mImporteCartera" value=""/> 
			<input type="hidden" id="cValidaFactura" name="cValidaFactura" value="" /> 
			<input type="hidden" id="cExiste" name="cExiste" value="0" /> 
			<input type="hidden" id="mImporteBrutoF" name="mImporteBrutoF" value="0" /> 
			<input type="hidden" id="COMSOCAutoriza" name="COMSOCAutoriza" value="" /> 
			<input type="hidden" id="partCOMSOC" name="partCOMSOC" value="" /> 
			<input type="hidden" id="cMsjCOMSOC" name="cMsjCOMSOC" value=""/> 
			<input type="hidden" id="IEsPasivo" name="IEsPasivo" value=""/> 
			<input type="hidden" id="cOBGT" name="cOBGT" />
			<input type="hidden" id="firmanteExiste" name="firmanteExiste" />
			
			
			<input type="hidden" id="cNombreVo" name="cNombreVo" />
			<input type="hidden" id="cPaternoVo" name="cPaternoVo" />
			<input type="hidden" id="cMaternoVo" name="cMaternoVo" />
			<input type="hidden" id="cPuestoVo" name="cPuestoVo" />
			<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" />
			
			<input type="hidden" id="tipoFirmante" name="tipoFirmante" value="PAGO_VOBO"/> 
			<input type="hidden" id="cNombreA" name="cNombreA" /> 
			<input type="hidden" id="cPaternoA" name="cPaternoA" /> 
			<input type="hidden" id="cMaternoA" name="cMaternoA" /> 
			<input type="hidden" id="cPuestoA" name="cPuestoA" /> 
			<input type="hidden" id="firmanteAut" name="firmanteAut" /> 
			<input type="hidden" id="tipoFirmanteA" name="tipoFirmanteA" value="PAGO_AUT"/>
			<input type="hidden" id="facturasDiferentes" name="facturasDiferentes"  value="0"/>
			<input type="hidden" id="cEsRadicado" name="cEsRadicado" value="S"/>
			<input type="hidden" id="cEsIngresoFiscal" name="cEsIngresoFiscal" value="N"/>
			<input type="hidden" id="importeBrutoAux" name="importeBrutoAux" />
			<input type="hidden" id="cEPborrar" name="cEPborrar" />
			<input type="hidden" id="mImporteBorrar" name="mImporteBorrar" />
			<input type="hidden" id="renglonBorrar" name="renglonBorrar" />
			<input type="hidden" id="esExtranjero" name="esExtranjero" value=""/>
			<input type="hidden" id="mImporteDiferenciaCentavos" name="mImporteDiferenciaCentavos" value="0.00"/>
			<input type="hidden" id="cFolioGestion" name="cFolioGestion" value="<%=c.getFolio()%>"/>
			<input type="hidden" id="tienePenas" name="tienePenas" value="0"/>
			<input type="hidden" id="mAjustePenas" name="mAjustePenas" value="0"/>
			
			<!-- hidden para la captura de oficio delegatorio -->
			<input type="hidden" name="cFolioOficioAux" id="cFolioOficioAux" value=""/>
			<input type="hidden" name="dFechaOficioAux" id="dFechaOficioAux" value=""/>
			<input type="hidden" name="cNombreTitularAux" id="cNombreTitularAux" value=""/>
			<input type="hidden" name="cApellidoPaternoTitularAux" id="cApellidoPaternoTitularAux" value=""/>
			<input type="hidden" name="cApellidoMaternoTitularAux" id="cApellidoMaternoTitularAux" value=""/>
			<input type="hidden" name="cPuestoTitularAux" id="cPuestoTitularAux" value=""/>
			<input type="hidden" name="firmanteOficioExiste" id="firmanteOficioExiste" value=""/>
						
			<!-- hidden para la captura de los datos de quien elaboro -->
			<input type="hidden" id="cNombreE" name="cNombreE" />
			<input type="hidden" id="cPaternoE" name="cPaternoE" />
			<input type="hidden" id="cMaternoE" name="cMaternoE" />
			<input type="hidden" id="cPuestoE" name="cPuestoE" />
			<input type="hidden" id="firmanteEla" name="firmanteEla"/>
			
			<!-- Relacion Ingreso Fiscal con el pago -->
			<input type="hidden" id="nExisteIngresoPago" name="nExisteIngresoPago" value="0"/>
			<input type="hidden" id="mTotalIngresoPago" name="mTotalIngresoPago" value="0"/>
			<input type="hidden" id="nFolioRegistroIngreso" name="nFolioRegistroIngreso" value="0"/>
			<input type="hidden" id="cEPIngresoPago" name="cEPIngresoPago" value="0"/>
			<input type="hidden" id="nMesIngresoPago" name="nMesIngresoPago" value="0"/>
			<input type="hidden" id="mImporteIngresoPago" name="mImporteIngresoPago" value="0"/>
			
			<!-- hidden para la captura de oficio delegatorio VoBo-->
			<input type="hidden" name="cFolioOficioVoBoAux" id="cFolioOficioVoBoAux" value=""/>
			<input type="hidden" name="dFechaOficioVoBoAux" id="dFechaOficioVoBoAux" value=""/>
			<input type="hidden" name="cNombreTitularVoBoAux" id="cNombreTitularVoBoAux" value=""/>
			<input type="hidden" name="cApellidoPaternoTitularVoBoAux" id="cApellidoPaternoTitularVoBoAux" value=""/>
			<input type="hidden" name="cApellidoMaternoTitularVoBoAux" id="cApellidoMaternoTitularVoBoAux" value=""/>
			<input type="hidden" name="cPuestoTitularVoBoAux" id="cPuestoTitularVoBoAux" value=""/>
			<input type="hidden" name="firmanteOficioVoBoExiste" id="firmanteOficioVoBoExiste" value=""/>

			<!-- hidden para validar si el contrato tiene remanente para realizar el pago de anticipo -->
			<input type="hidden" id="tieneRemanente" name="tieneRemanente" value=""/>
			
			<!-- hidden para validar si las EPS pertenecen a CECFOR -->
			<input type="hidden" id="esEPCECFOR" name="esEPCECFOR" value=""/>
			
			<!-- hidden para validar si la partida es de viaticos -->
			<input type="hidden" name="cPartida" id="cPartida" value=""/>
			<input type="hidden" name="esPartidaViaticos" id="esPartidaViaticos" value="0"/>
			
			<!-- hidden para obtener los pagos de anticipo que se han hecho. -->
			<input type="hidden" id="totalPagosAnticipo" name="totalPagosAnticipo" value="0"/>
			
			<!-- hidden para el importe de la ep que se esta capturando. -->
			<input type="hidden" id="mMovimiento" name="mMovimiento" value="0"/>
			
			<!-- hidden para VALIDAR SI EXISTE SUFICIENCIA DE INGRESOS PROPIOS -->
			<input type="hidden" name="nValidaSuficienciaIP" id="nValidaSuficienciaIP" value="0"/>	
			
			<!-- hidden para validar si el rfc del proveedor es de boletaje de avion. -->
			<input type="hidden" id="esProvBoletaje" name="esProvBoletaje" value=""/>
			<input type="hidden" id="nFolioVuelos" name="nFolioVuelos" value=""/>
			<input type="hidden" id="u_login" name="u_login" value="<%=U_LOGIN%>"/>
			<input type="hidden" id="capturaLayoutVuelos" name="capturaLayoutVuelos" value=""/>
			<input type="hidden" id="mImporteVuelos" name="mImporteVuelos" value=""/>
			
			<input type="hidden" id="cPagoReferenciado" name="cPagoReferenciado" value="N"/>
			<input type="hidden" id="mImporteRetenPago" name="mImporteRetenPago" value="0"/>
			
			<!-- hidden para habilitar check de ingresos propios -->
			<input type="hidden" id="habilitaCheckIP" name="habilitaCheckIP" value="0"/>
			<input type="hidden" id="DCD_ISR_AUX" name="DCD_ISR_AUX" value="0"/>
			<input type="hidden" id="autorizadoPorFiel" name="autorizadoPorFiel" value="false"/>
			<input type="hidden" id="tieneCedular" name="tieneCedular" value="0"/>
			<input type="hidden" id="porcCedular" name="porcCedular"  value="0"/>
			<input type="hidden" id="cEsNayarit" name="cEsNayarit" value="0"/>
			<input type="hidden" id="cAplica15D" name="cAplica15D" value="false">
			<input type="hidden" id="cNumeroRepse" name="cNumeroRepse" value="false"/>			
			<input type="hidden" id="msgRF" name="msgRF" value=""/>
			
			<!-- hidden para captura de correo-->
			<input type="hidden" name="esPPD" id="esPPD" value=""/>
			<input type="hidden" name="cIDRFC" id="cIDRFC" value=""/>
			<input type="hidden" name="correo" id="correo" />
			<input type="hidden" name="nCorreo" id="nCorreo" />
			<input type="hidden" name="paternoCorreo" id="paternoCorreo" />
			<input type="hidden" name="maternoCorreo" id="maternoCorreo" />
			<input type="hidden" name="cCargo" id="cCargo" />
			<input type="hidden" name="sDataFoliosING" id="sDataFoliosING"/>
			<input type="hidden" name="sDataRemanenteING" id="sDataRemanenteING"/>	
			<input type="hidden" name="nIdPenaltyDeduction" id="nIdPenaltyDeduction" value = "-1"/>		
			
			<div class="alert alert-danger" role="alert" id="repseDiv" style="display: none">
			  <b>Informaci&oacute;n Importante</b><br> El proveedor cuenta con Numero de  REPSE. Por favor valide antes de autorizar el pago que el registro sea vigente en la ruta:
			  <br>
				<a href="https://repse.stps.gob.mx" target="_blank">https://repse.stps.gob.mx</a> 
			</div>
			
			<div class="dvGeneral">
				<div class="card-header"> <h3> Relaci&oacute;n Gastos con Orden de Compra </h3> </div>
				<div id="ContratoDeObra">
				
					<div class="row" >					
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1 divAutorizar">
							<label for="lbAutoriza"> Autorizar: </label>&nbsp;
							<input type="radio" name="grpAutorizar" id="grpAutorizar1" class="form-check-input" value = "Si" checked="checked"/> Si&nbsp;&nbsp;
							<input type="radio" name="grpAutorizar" id="grpAutorizar2" class="form-check-input" value = "No"/> No
						</div>										
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1" id="divImprimePoliza">
							<img src="imagenes/Imprimir.png" width="25" height="21" onClick="cmdImprimir('PolizaPago');"/> Poliza  
						</div>					
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1" id="divImprimeAnexo">
							<img src="imagenes/Imprimir.png" width="25" height="21" onClick="anexo('PolizaPagoN');"/> Anexo  
						</div>						
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							<span id="EditaFirmas" style="visibility:hidden"><a href="#" onclick="updateFirmantes();">Firmas*</a></span>  
						</div>
					</div>
					<div class="row">					
						<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							<label for="caNoContrarrecibo"> ContraRecibo: </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<input type="text" id="caNoContrarrecibo" name="caNoContrarrecibo" class="form-control form-control-sm" readonly value="0" />
						</div>											
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						</div>
					</div>
					
					<div class="row" >					
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
							<label for="cIDContrato"> No Contrato: </label>
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
							<div class="input-group">	 														
								<input type="text" name="cIDContrato" id="cIDContrato" onChange="FiltroRetencionesD();changeIVA();" class="form-control form-control-sm AyudaSyC" readonly title="Selecciona un contrato"/>
							</div>							
						</div>	
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
							<label for="cIDContrato"> Folio: </label>													
						</div>	
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<input name="id_caso" type="text" id="id_caso" class="form-control form-control-sm" readonly/>							
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							<input type="button" value="Borrar" onclick="Borrar1();" id="Borrar" name="Borrar" title="Elimina la solicitud temporal" class="btn btn-secondary btn-sm"/>
						</div>
					</div>
					
					<div class="row" >					
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
							<label for="cIdRFC"> RFC: </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																			
							<input type="text" name="cIdRFC" id="cIdRFC" class="form-control form-control-sm" size="15" maxlength="15" readonly/>
						</div>
						<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1"> 
							<input type="text" name="cobjetocontrato" id="cobjetocontrato" class="form-control form-control-sm" size="50" maxlength="50" readonly/>							
						</div>	
					</div>
					
					<div class="row" id="trctaBancaria">					
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
							<label for="ctaBancaria"> Cta Bancaria: </label>
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">																			
							<select id="ctaBancaria" name="ctaBancaria" class="form-select form-select-sm"></select>
						</div>						
					</div>
					
					<div class="row">					
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">							
							Pago con Referencia Bancaria&nbsp;<input type="checkbox" class="form-check-input" id="chk_RB" name="chk_RB"/>
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">																			
							<label for="noRecepcion"> Recepci&oacute;n: </label>
						</div>						
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<select id="noRecepcion" name="noRecepcion" onchange="readInfoRecepcionMat();" class="form-select form-select-sm"></select>
						</div>
					</div>
					
					<div class="row" id="trReferenciaBancaria">					
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
							<label for="cReferenciaBancaria"> Referencia: </label>
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">																			
							<input type="text" name="cReferenciaBancaria" id="cReferenciaBancaria" class="form-control form-control-sm"/>
						</div>						
					</div>
					
					<div class="row">					
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
							<label for="fRecepcion"> Fecha Captura: </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<div class="input-group">								
								<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
								<input type="text" class="form-control form-control-sm" id="fRecepcion" name="fRecepcion" readonly/>
								<input type="hidden" maxlength="10" size="10" id="fRecepcion2" name="fRecepcion2"/> 
								<input type="hidden" maxlength="10" size="10" class="paso01" id="fecha_Pago" name="fecha_Pago"/>
							</div> 							
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">																			
							<label for="TIPO_OPERACION"> Tipo de Operaci&oacute;n: </label>
						</div>						
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<select class="form-select form-select-sm" id="TIPO_OPERACION" name="TIPO_OPERACION"></select>
						</div>
					</div>
					
					<div class="row">					
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
							<label for="destGasto"> Tipo Destino: </label>
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
							<select id="destGasto" name="destGasto" class="form-select form-select-sm" disabled></select>						
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							<span id="EditaFacturas" style="visibility:hidden">
								<a href="#" onclick="creaDialogFacturas(); return false;"> Facturas... </a>
							</span>							
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							<button type="button" class="btn btn-secondary btn-sm" onclick="creaDialogOpinion(); return false;"> Opinión de Cumplimiento </button>
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							<span id="EditaCorreoE" style="visibility:hidden"><a href="#" onclick="creaDialogCorreo(true);"> Captura Correo </a></span>
							<input type="hidden" maxlength="10" size="10" id="ID_DESTINO_GASTO2" name="ID_DESTINO_GASTO2"/> 
							<input type="hidden" maxlength="10" size="10" id="ID_TIPO_FONDO" name="ID_TIPO_FONDO"/> 
							<input type="hidden" name="cNoEstimacion" id="cNoEstimacion" value="0" class="paso01" size="14" maxlength="14"/>
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1" id="penas">																			
							<label for="noPenalty"> Penas/ Deducciones: </label>
						</div>						
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1" id="penasI">
							<select id="noPenalty" name="noPenalty" onchange="readInfoPenas();" class="form-select form-select-sm"></select>
						</div>
					</div>
					
					<div class="row">					
						<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">						
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
							<input type="button" value="Cargar Layout Boletos" onclick="abrirDlgBoletaje();" id="btnLayoutBoletos" name="btnLayoutBoletos" title="Carga Layout Boletos de Vuelos" class="btn btn-secondary btn-sm"/>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<input type="button" value="Calcula Cedular Nay" onclick="calculaCedular(1.5);" id="btnCalculaCedular1" name="btnCalculaCedular" title="Calcula Cedular Nayarit" class="btn btn-secondary btn-sm"/>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<input type="button" value="Calcula Cedular Gto" onclick="calculaCedular(1.0);" id="btnCalculaCedular" name="btnCalculaCedular" title="Calcula Cedular Guanajuato" class="btn btn-secondary btn-sm"/>
						</div>
					</div>
					<div class="row">	
						<div class="col-3">
							<div class="row">											
								<div class="col-12">							
									Ingresos Propios: &nbsp;<input type="checkbox" id="chk_IP" name="chk_IP" class="form-check-input"/>
								</div>						
							</div>
							<div class="row" id="divIngresoFiscal">											
								<div class="col-12">							
									Ingreso Fiscal Recaudado: &nbsp;<input type="checkbox" id="chk_IF" name="chk_IF" class="form-check-input" />
								</div>						
							</div>
							<div class="row" id="divRadicado">											
								<div class="col-12">							
									Presupuesto Radicado: &nbsp;<input type="checkbox" id="chk_radicado" name="chk_radicado" value="S" class="form-check-input" disabled="disabled"/>
								</div>						
							</div>
						</div>		
    					<div class="col-9 col-md-9 col-sm-12">
							Observaciones <textarea cols="105" rows="2" name="cObservaciones" onkeypress="return validar(event)" class="form-control form-control-sm" id="cObservaciones"></textarea>								  							
						</div>
					</div>
				
			  </div>  				
			<div id="multitabs" style="width: 1300px">
				<div id="tabs" style="width: 100%">
				
		       		<ul>
			        	<li><a id="L01" href="#tabs-1">Detalle de operaci&oacute;n</a></li>
						<li><a id="L02" href="#tabs-2">Retenciones</a></li>
						<li><a id="L03" href="#tabs-3" class="pasoDos">Devengado</a></li>
						<li><a id="L04" href="#tabs-4" class="pasoTres">Documentaci&oacute;n</a></li>     				           
			    	</ul>
		    	
		    		<div id="tabs-1">
		    			<!--<a href="javascript:void(0);" onclick="fnClickAddRowA();">Agregar</a>-->
		    			<select id="NumPagoAMF" name="NumPagoAMF" style="visibility:hidden">
							<option value="***" selected>- Selecciona AMF -</option>
						</select>
						<input type="text" name="nClaveAMF" id="nClaveAMF" style="visibility:hidden"/>
						<input type="text" name="numFolioAMF" id="numFolioAMF" style="visibility:hidden"/>
						<input type="text" name="PagoAMF" id="PagoAMF" style="visibility:hidden"/>
						
						<div class="row">				
		    				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								&nbsp;
								<input type="text" name="cNoFactura" style="text-align: right" class="form-control form-control-sm paso01" id="cNoFactura" size="16" maxlength="10" style="visibility:hidden" title="Capture el Numero de Factura" />
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								 Amorizacion Anticipo
								 <div class="input-group">
								 	<input name="mAmortizacionAnticipo" style="text-align: right" type="text" class="form-control form-control-sm" id="mAmortizacionAnticipo" maxlength="10" size="10" value="0.0" readonly/>&nbsp;-
								 </div>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								% Amortizacion <input type="text" name="nPorcAmortizacion" style="text-align: right" onChange="Amortizacion()" class="form-control form-control-sm" id="nPorcAmortizacion" maxlength="10" size="10" value="0.0" readonly/>
								<input type="hidden" name="nPorcAmortizacion2" id="nPorcAmortizacion2"/> 
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Acumulado Devoluciones <input name="mImporteDevolucionAcumulado" type="text" value="0.0" onkeypress="return validar2(event)" class="form-control form-control-sm" maxlength="10" size="10" id="mImporteDevolucionAcumulado" style="text-align: right" readonly/>								  							
							</div>
						</div>
						
		    			<div class="row">				
		    				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Importe Facturas <input name="mImporteFacturas" type="text" style="text-align: right" class="form-control form-control-sm" id="mImporteFacturas" value="0.0" size="16" readonly>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								 Subtotal
								 <div class="input-group">
								 	<input name="subTotal_2" type="text" value="0.0" class="form-control form-control-sm" style="text-align: right" id="subTotal_2" size="10" readonly/>&nbsp;=
								 </div>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Acumulado Amortizacion <input type="text" name="mAmortizacionAcumulado" style="text-align: right" id="mAmortizacionAcumulado" class="form-control form-control-sm" maxlength="10" size="10" value="0.0" readonly/> 
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Anticipo <input type="text" name="saldoAnticipo" id="saldoAnticipo" maxlength="10" value="0.0" class="form-control form-control-sm" style="text-align: right" size="10" readonly/>								  							
							</div>
						</div>
						
						<div class="row">				
		    				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Importe Bruto 
								<div class="input-group">
									<input name="mImporteBruto" onkeypress="return validar2(event)" type="text" style="text-align: right" class="form-control form-control-sm" id="mImporteBruto" value="0.0" size="16" readonly/>&nbsp;-
								</div>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								 Monto IVA
								 <div class="input-group">
								 	<input name="mImporteIVA" type="text" class="form-control form-control-sm" id="mImporteIVA" style="text-align: right" maxlength="10" size="10" value="0.0" readonly/>&nbsp;+
								 </div>
								 <input name="mImporteMasIva" type="hidden" class="subtotall" id="mImporteMasIva" value="0"/>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								% IVA <input name="nPorcIVAAplicable" type="text" class="form-control form-control-sm" readonly id="nPorcIVAAplicable" maxlength="10" style="text-align: right" size="10" value="0.0"/>								
							</div>
						</div>
						
						<div class="row">				
		    				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Otros Impuestos <input type="text" name="mOtrosImpuestos" style="text-align: right" class="form-control form-control-sm paso01" id="mOtrosImpuestos"  size="16" readonly value="0">								
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								 Monto Retenciones
								 <div class="input-group">
								 	<input name="mImporteRetencion" type="text" class="form-control form-control-sm" readonly id="mImporteRetencion" maxlength="10" style="text-align: right" size="10" value="0.0"/>&nbsp;-
								 </div>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Saldo Total Compromiso <input type="text" name="saldoCed" id="saldoCed" maxlength="10" style="text-align: right" class="form-control form-control-sm" value="0.0" size="10" readonly /> 	
								<input type="hidden" name="saldoCed2" id="saldoCed2" maxlength="10" style="text-align: right" value="0" size="10" />								
							</div>
						</div>
						
						<div class="row">				
		    				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Monto Sanciones
								<div class="input-group"> 
									<input name="mImporteSancion" onkeypress="return validar2(event)" type="text" class="form-control form-control-sm subtotall" value="0.0" id="mImporteSancion" style="text-align: right" maxlength="10" size="16" title="Disponible a Devolución"/>&nbsp;+
								</div>								
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								 Monto Penalizaciones
								 <div class="input-group">
								 	<input name="mImportePenalizacion" type="text" class="form-control form-control-sm subtotall" id="mImportePenalizacion" value="0.0" onkeypress="return validar2(event);" maxlength="10" style="text-align: right" size="10" title="No disponible a Devolución"/>&nbsp;-
								 </div>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Saldo del Contrato <input name="mTotal" onkeypress="return validar2(event)" class="form-control form-control-sm" type="text" style="text-align: right" id="mTotal" value="0.0" maxlength="10" readonly size="10"> 								
							</div>
						</div>
						
						<div class="row">				
		    				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Monto Devoluciones
								<div class="input-group"> 
									<input name="mImporteDevolucion" type="text" value="0.0" onkeypress="return validar2(event)" maxlength="10" size="16" class="form-control form-control-sm subtotall" id="mImporteDevolucion" style="text-align: right" title="Devolución de lo Sancionado"/>&nbsp;=
								</div>								
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								 Importe Neto
								 <div class="input-group">
								 	<input name="mImporteNeto" value="0.0" style="text-align: right" type="text" class="form-control form-control-sm" readonly id="mImporteNeto" size="10"/>&nbsp;=
								 </div>
								 <input name="mImporteNetoEP" value="0" type="hidden" readonly id="mImporteNetoEP" size="10"/>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Comprometido <input type="text" name="acumulado" id="acumulado" class="form-control form-control-sm" maxlength="10" style="text-align: right" value="0.0" size="10" readonly/> 								
							</div>
						</div>
						
						<div class="row">				
		    				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Subtotal <input name="subTotal_1" value="0" type="text" style="text-align: right" onkeypress="return validar2(event)" class="form-control form-control-sm" id="subTotal_1" size="16" readonly/>								
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Importe a Ejercer <input name="mImpEjercer" onKeyPress="return validar2(event)" class="form-control form-control-sm" type="text" style="text-align: right" id="mImpEjercer" value="0.0" maxlength="10" size="10" readonly/> 
								<input type="hidden" name="mImporteAnticipoIVA" class="subtotall"	id="mImporteAnticipoIVA" value="0" />								 
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Acumulado Sanciones <input type="text" name="mImporteSancionAcumulado" id="mImporteSancionAcumulado" value="0.0" class="form-control form-control-sm" maxlength="10" style="text-align: right" size="10" readonly />
								<input name="mTotal2" type="hidden" style="text-align: right" id="mTotal2" value="0"/>  								
							</div>
						</div>
						<div class="row">				
		    				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>														
							<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">	
								Concepto <textarea cols="80" rows="8" name="Descrip_Concepto" onkeypress="return validar(event)" class="form-control form-control-sm paso01" id="Descrip_Concepto" title="Descripcion del pago" readonly></textarea>								  							
							</div>
						</div>

		    		</div> <!-- FIN tabs-1 -->
		    		
		    		<div id="tabs-2">
		    			<!--<a href="javascript:void(0);" onclick="fnClickAddRowC();">Agregar</a>-->
		    			<div class="rowr">							
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
								<table align="center" id="tretencion" class="display">								
									<thead>
										<tr>
											<th>Codigo</th>
											<th>Retencion</th>
											<th>Porcentaje</th>
										</tr>
									</thead>
									<tbody>
									</tbody>							
								</table>
							</div>
						</div>
												
		    		</div> <!-- FIN tabs-2 -->
		    		
		    		<div id="tabs-3">
		    			<div class="row">						    																	
							<div class="col-12 col-lg-3 col-md-3 col-sm-12">	
								Tipo De Concepto <select id="TIPO_CONCEPTO" name="TIPO_CONCEPTO" onchange="concepto();" class="form-select form-select-sm"></select>
								<span id="verFondenSPAN" >&nbsp; <a href="#" onclick="verReferencia();return false;">Ver Proyecto FONDEN</a> </span>								  							
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12">	
								Tipo De Movimiento <select id="TIPO_MOVIMIENTO" name="TIPO_MOVIMIENTO" class="form-select form-select-sm"></select>								  							
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								Importe a Ejercer <input type="text" id="importeEjercer" name="importeEjercer" value="0" style="text-align:right" size="16" class="form-control form-control-sm" readonly/>
							</div>
						</div>
						
						<div class="row">						    																	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								<label id="lblNumAcompanantes"># Acompañantes</label>
								<input type="text" style="text-align: right" id="nAcompanantes" name="nAcompanantes" class="form-control form-control-sm" size="12" value="0"/>								  							
							</div>
						</div>
						
						<div class="row">						    																	
							<div class="col-12 col-lg-3 col-md-3 col-sm-12">	
								Almacén <select id="ALM" name="ALM" class="form-select form-select-sm"></select>								  							
							</div>		
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<input name="altaAlmacen" type="text" class="form-control form-control-sm" id="altaAlmacen" style="text-align: right;" onKeyPress="valFmt(this,9)" value="0"/>
							</div>					
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								&nbsp;<input type="text" id="cAnioFactEP" name="cAnioFactEP" onKeyPress="valFmt(this,9)" size="4" maxlength="4" class="form-control form-control-sm paso04"/>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								&nbsp;<input type="text" id="nFacturaEP" name="nFacturaEP" style="text-transform:uppercase" class="form-control form-control-sm paso04" size="12" maxlength="12"/>
							</div>					
						</div>
						
						<div class="row">						    																	
							<div class="col-12 col-lg-5 col-md-5 col-sm-12">	
								Estructura Programática 
								<div class="input-group">
									<input type="text" maxlength="32" size="60" id="ep" name="ep" class="form-control form-control-sm" readonly/>
									<input name="button" type="button" 	id="nIdClaveEgresos2" class="btn-secondary btn-sm" onclick="Grid()" value="..." size="5"/>
								</div>								  							
							</div>									
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12">
								Monto Devengado 
								<div class="input-group">
									<input type="text" name="montoDev" value='0' id="montoDev" onblur="cambioss();" class="form-control form-control-sm" maxlength="16"  size="16" style="text-align:right" onkeypress="return validar2(event)"/>
									&nbsp;&nbsp;&nbsp;<input type="button" value="Agregar" onClick=" FiltroMovimientos();" class="btn-secondary btn-sm paso9" id="agrega2" name="agrega2" />
								</div>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								&nbsp;<input type="text" name="saldoCompM" id="saldoCompM" value="0" class="form-control form-control-sm" maxlength="16" size="16" style="text-align:right" readonly />
							</div>					
						</div>

						<br/>
						
						<div class="rowr">							
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">								
								<table align="center" id="grdCompromisos" class="display">						
									<thead>
										<tr>
											<th>Clave Presupuestaria SIAF</th>
											<th>Cod. SIF</th>
											<th>Importe</th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
						
						<div class="rowr">							
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">								
								<table id="grdRetClave" class="display" style="visibility:hidden">									
									<thead>
										<tr>
											<th>EP</th>
											<th>Mes</th>
											<th>Importe</th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
						
						<div class="rowr">							
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">								
								<table id="arrIngresoPago" class="display" style="visibility:hidden">							
									<thead>
										<tr>
											<th>Ingreso</th>
						                	<th>EP</th>
						                	<th>Codigo</th>
						                    <th>Importe</th>
						                    <th>Mes</th>
										</tr>
									</thead>								
								</table>
							</div>
						</div>
		    		</div> <!-- FIN tabs-3 -->
		    		
		    		<div id="tabs-4">
		    			<div class="row">						    				
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								No. Folio <input name="DCD_FACTURA" type="text" id="DCD_FACTURA" onkeypress="return validar(event)" class="form-control form-control-sm paso03" readonly size="15"/>								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Fecha de factura 
								<div class="input-group">								
									<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
									<input name="DCD_FECHA_FACTURA" type="text" id="DCD_FECHA_FACTURA" class="form-control form-control-sm paso03" maxlength="10" size="10" readonly/>
								</div>
							</div>							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Tipo de beneficiario <input name="DCD_TBEN" type="text" class="form-control form-control-sm paso03" size="15" id="DCD_TBEN" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								Clave de beneficiario <input name="DCD_CBEN" type="text" class="form-control form-control-sm paso03" size="15" id="DCD_CBEN" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
							  	Tipo de operaci&oacute;n
							  	<select name="DCD_TIPO_OPE" id="DCD_TIPO_OPE" class="form-select form-select-sm paso03">
									<option value="85" class="paso03">85 OTROS</option>
								</select>							
							</div>
						</div>
						
						<div class="row">						    				
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Porcentaje IVA <input name="DESCRIPCION20" type="text" class="form-control form-control-sm paso03" id="DESCRIPCION20" size="15" onKeyPress="return validar2(event)" value="0" readonly/>								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Importe Total <input type="text" id="DCD_IMP_BRUTO" class="form-control form-control-sm paso03" size="15" onkeypress="return validar2(event)" name="DCD_IMP_BRUTO" readonly/>								
							</div>							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								IVA Desglose <input name="DCD_IVA" type="text" id="DCD_IVA" size="15" value="0" class="form-control form-control-sm paso03" onKeyPress="return validar2(event)" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								IVA Retención <input name="DCD_IVADES" type="text" id="DCD_IVADES" size="15" class="form-control form-control-sm paso03" value="0" onKeyPress="return validar2(event)" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
							  	ISR <input name="DCD_ISR" type="text" id="DCD_ISR" size="15" value="0" class="form-control form-control-sm paso03" onkeypress="return validar2(event)" readonly/>							
							</div>
						</div>
						
						<div class="row">						    				
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Ret. 0.5% <input name="DCD_MIL5" type="text" class="form-control form-control-sm paso03" id="DCD_MIL5" size="15" onKeyPress="return validar2(event)" value="0" readonly/>								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Ret. 0.2% <input type="text" id="DCD_MIL2" class="form-control form-control-sm paso03" size="15" onkeypress="return validar2(event)" name="DCD_MIL2" readonly/>								
							</div>							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Beneficio Social <input name="DCD_CONTRIBUCION" type="text" id="DCD_CONTRIBUCION" size="15" value="0" class="form-control form-control-sm paso03" onKeyPress="return validar2(event)" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								Impuesto Cedular <input name="DCD_OTRAS_RET" type="text" id="DCD_OTRAS_RET" size="15" class="form-control form-control-sm paso03" value="0" onKeyPress="return validar2(event)" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
							  	Penalizaciones <input name="DCD_PENALIZACION" type="text" id="DCD_PENALIZACION" size="15" value="0" class="form-control form-control-sm paso03" onkeypress="return validar2(event)" readonly/>							
							</div>
						</div>
						
						<div class="row">						    				
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Sanción <input name="DCD_SANCION" type="text" class="form-control form-control-sm paso03" id="DCD_SANCION" size="15" onKeyPress="return validar2(event)" value="0" readonly/>								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Devolución <input type="text" id="DCD_DEVOL" class="form-control form-control-sm paso03" size="15" onkeypress="return validar2(event)" name="DCD_DEVOL" readonly/>								
							</div>							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Amort. Anticipo <input name="DCD_AMORT" type="text" id="DCD_AMORT" size="15" value="0" class="form-control form-control-sm paso03" onKeyPress="return validar2(event)" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								Total Retención <input name="DCD_RETENCION" type="text" id="DCD_RETENCION" size="15" class="form-control form-control-sm paso03" value="0" onKeyPress="return validar2(event)" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
							  	Neto <input name="DCD_NETO" type="text" id="DCD_NETO" size="15" value="0" class="form-control form-control-sm paso03" onkeypress="return validar2(event)" readonly/>							
							</div>
						</div>
						
						<br/>
						
						<div class="row">						    		
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>		
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								<input type="button" id="Agregar1" name="Agregar1" value="Agregar" onclick="fnClickAddRowComp();" class="btn-secondary btn-sm"/>								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<div class="input-group" id="divImprime" >
									<img src="imagenes/Imprimir.png" width="33" height="28" onClick="cmdImprimir('ContrareciboN');">Contrarecibo
								</div>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								<span id="justificacion"><a href="#" onclick="dlgJustificacion(); return false;"> Carga Justificacion </a></span>								
							</div>								
						</div>
						
						<div class="row">												
							<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">	
								Concepto <textarea cols="50" rows="3" name="DCD_CONCEPTO" onkeypress="return validar(event)" class="form-control form-control-sm paso03" id="DCD_CONCEPTO" ></textarea>								  							
							</div>
						</div>
						
						<div class="rowr">							
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">								
								<table id="grdFacturas" class="display">							
									<thead>
										<tr>
											<th nowrap>&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">Descuentos</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
										</tr>
										<tr>
											<th nowrap>No. Factura</th>
											<th align="left">Fecha Factura</th>
											<th align="left">Tipo beneficiario</th>
											<th align="left">Cve Benef</th>
											<th align="left">Tipo oper</th>
											<th align="left">%IVA</th>
											<th align="left">Importe Total</th>
											<th align="left">IVA desglose</th>
											<th align="left">IVA</th>
											<th align="left">ISR</th>
											<th align="left">Ret. 0.5</th>
											<th align="left">Ret 0.2</th>
											<th align="left">Beneficio social</th>
											<th align="left">Impuesto cedular</th>
											<th align="left">Penalizaciones</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
									<tfoot>
									</tfoot>
								</table>
							</div>
						</div>
		    		</div> <!-- FIN tabs-4 -->
		    			    			 
		    	</div>  	
		    </div> <!-- FIN class="row" -->											
		
			<div id="dialog-form" title="Aplicación Presupuestal/Contable">
				<div id="divEspera" align="center">
					Espere por favor.... <img border="0" src="../imagenes/espera.gif"
						height="30">
				</div>
				<div id="divAplica">
					<iframe id="ifAplica" src="about:blank"></iframe>
				</div>
			</div>
			<div id="dialog-Procesando" title="Procesando">
				<div id="divEsperaProcesando" style="visibility: hidden" align="center">
					Espere por favor.... <img border="0" src="../imagenes/espera.gif"
						height="30">
				</div>
			</div>
			
			<div class="modal" tabindex="-1" role="dialog" id="dialog-validaOLI" data-mdb-keyboard="true" data-mdb-backdrop="static">
			  <div class="modal-dialog" role="document">
			    <div class="modal-content">
				      <div class="modal-header">	
				      	<h6 class="modal-title">Digite el Número de OLI a Validar</h6>		
				      	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				      </div>
				      <div class="modal-body">
				      	<div class="row">										
							<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
								<input style="text-align: right;" name="cOLI" type="text" id="cOLI" class="form-control" value="" />
							</div>
						</div>		
				   	 </div>
				     <div class="modal-footer">
				        <button type="button" id="btnAceptarOli" onclick="aceptarOli();" class="btn btn-primary">Aceptar</button>
				        <button type="button" class="btn btn-secondary" onclick="cerrarOli();"  data-bs-dismiss="modal">Cerrar</button>
				     </div>
		    	 </div>
		  	   </div>
			</div>

<div class="modal" tabindex="-1" role="dialog" id="dialog-validaFact" data-mdb-keyboard="true" data-mdb-backdrop="static">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
	      <div class="modal-header">	
	      	<h6 class="modal-title">Validación de Facturas</h6>		
	      	<button type="button" class="btn-close" data-bs-dismiss="modal" onclick="cerrarFacturas();" aria-label="Close"></button>
	      </div>
	      <div class="modal-body">
				<div id="uploadFacturasDiv">
					<iframe id="uploadFacturasFrm" src="UploadFacturas.jsp?tipo_pago=PAGODIVERSO&tipo_modulo=RGOC" align="top" frameborder="0" height="360" width="600"> </iframe>
				</div>
				<div id="facturasCapturadasDiv">
					<div class="row">										
						<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
						</div>		
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
							<a href="#" onclick="togleDivFacts(1);return false;">Cargar Facturas</a>								  							
						</div>
					</div>
					
					<h5> Facturas Cargadas </h5>
					<hr class="mt-3">
					
					<div class="rowr">							
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">	
							<table id="grdValidaFacturas">
								<thead>
									<tr>
										<th>Serie</th>
										<th>Factura</th>
										<th>Importe Bruto</th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
					
					<div class="rowr">							
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
							Total de las facturas:&nbsp;&nbsp;&nbsp;
							<input type="text" style="text-align: right;" name="mTotalFacturaV" id="mTotalFacturaV" value="0.00" size="15" maxlength="15" class="form-control form-control-sm" readonly />
						</div
					</div>										
				</div>
		   </div>
	  </div>
      <div class="modal-footer">
        <button type="button" id="btnAceptarFacturas" onclick="aceptarFacturas();" class="btn btn-primary">Aceptar</button>
        <button type="button" class="btn btn-secondary" onclick="cerrarFacturas();"  data-bs-dismiss="modal">Cerrar</button>
      </div>
  </div>
</div>
			
			<div id="dialog-Justificacion" title="Ajuntar Justificacion">
				<div id="uploadJustificacion">
					<iframe id="UploadJustificacionIva6" src="UploadJustificacionIva6.jsp?tipo_pago=PAGODIVERSO" align="top" frameborder="0" height="360" width="510"> </iframe>
				</div>
			</div>
			
			
			<div id="dlgSeleccionaFonden">
				<fieldset>
					<legend>
						Seleccione el proyecto de FONDEN
					</legend>
					<table>
						<tr>
							<td align="right"> Proyecto:
							</td>
							<td>
								<div id="selproyectoFondenDiv" >
									<select id="proyectoFonden" name="proyectoFonden"></select>
								</div>
								<div id="verProyectoFondenDiv"  >
									<input type="text" id="proyectoFondenRead" name="proyectoFondenRead" size="24" readonly="readonly" class="form-control"/>
								</div>
								
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
			
			<div id="dialog-Anticipo" title="Selecciona el Ingreso">
				<h5> Solicitudes de Ingreso </h5>
				<hr class="mt-3">			
				
				<label style="font-size: 11px; font-weight: bold;"> Seleccione una solicitud dando clic en el cuadro de selección.</label>
				
				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">							
						<table id="tblSolicitudesIngreso" class="table table-striped table-bordered">							
							<thead>
								<tr>
									<th> id</th>
									<th> #Solicitud</th>
									<th> Concepto</th>
									<th style="text-align: left;"> Remanente</th>
									<th style="text-align: left;"> RFC</th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
					</div>
				</div>
				
				<br/>
				
				Importe a Comprobar:&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="text" style="text-align: right;" name="mImporteComprobar" id="mImporteComprobar" value="0.00" size="15" maxlength="15"  class="form-control form-control-sm" readonly />
				<input type="hidden" name="totalAcumulado" id="totalAcumulado" value = "0.00"/>										

			</div>
			
		</form>
	</div>	
	<!-- Dialogo Firmantes -->
		<jsp:include page="Firmantes.jsp"></jsp:include>
	<!-- Fin Dialogo Firmantes -->	
	<!-- DIV Para capturar correo -->
	<div class="modal" tabindex="-1" role="dialog" id="dialog-actualizaCorreo" data-mdb-keyboard="true" data-mdb-backdrop="static">
	  <div class="modal-dialog modal-lg" role="document">
	    <div class="modal-content">
		      <div class="modal-header">	
		      	<h6 class="modal-title">Actualiza Correo</h6>		
		      	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		      </div>
		      <div class="modal-body">
					<h6>Favor de capturar los siguientes datos para seguimiento del PPD:</h6>
						<div class="row">
							<div class="col-12">
								<label for="nombreCorreo">Nombre</label>
								<div class="input-group">
									<span class="input-group-text"><i class="bi bi-person"></i></span>
									<input type="text" id="nombreCorreo" name="nombreCorreo" placeholder="Nombre" class="form-control"/>
									<input type="text" id="aPCorreo" name="aPCorreo" placeholder="Apellido Paterno" class="form-control"/>
									<input type="text" id="aMCorreo" name="aMCorreo" placeholder="Apellido Materno" class="form-control"/>
									
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-12">
								<label for="cargo">Cargo</label>
								<div class="input-group">
									<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
									<input type="text" id="cargo" name="cargo" class="form-control" placeholder="Cargo"/>
								</div>	
							</div>
						</div>
						<div class="row">
							<div class="col-12">
								<label class="sr-only" for="correoActual">Correo</label>
								<div class="input-group">
						        	<div class="input-group-prepend">
						          		<div class="input-group-text">@</div>
						        	</div>
						        	<input type="text" id="correoActual" name="correoActual" placeholder="Correo electrónico" class="form-control"/>
						      	</div>
							</div>		
					  </div>	      	
			  </div>
			  <div class="modal-footer">
			        <button type="button" id="btnAceptarCorreo" onclick="validarCorreo();" class="btn btn-primary">Aceptar</button>
			        <button type="button" class="btn btn-secondary"  data-bs-dismiss="modal">Cerrar</button>
			  </div>
		</div>
	  </div>
	</div>

	<!-- DIV Para validar Opinión de Cumplimiento -->
	<div class="modal" tabindex="-1" role="dialog" id="dialog-opinion" data-mdb-keyboard="true" data-mdb-backdrop="static">
		<div class="modal-dialog modal-lg" role="document">
	    	<div class="modal-content">
				<div class="modal-header">	
		      		<h6 class="modal-title">Valida Opinion de Cumplimiento</h6>		
		      		<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		      	</div>
				
				<div class="modal-body">
					<div class="container-fluid">
						<div class="row mb-3">
							<div class="col-12">
								<label for="pdfOpinionCumplimiento32D" class="form-label">PDF con la opinion de cumplimiento (32D)</label>
								<input type="file" class="form-control" id="pdfOpinionCumplimiento32D" name="pdfOpinionCumplimiento32D" accept="application/pdf"/>
							</div>
						</div>
						<div class="row">
							<div class="col-12 text-end">
								<button type="button" class="btn btn-primary" id="btnValidarOpinion" name="btnValidarOpinion" onclick="validarOpinionCumplimiento(); return false;">Validar Archivo</button>
							</div>
						</div>
					</div>
				</div>
				
			</div>
		</div>
	</div>
			
</body>
</html>

