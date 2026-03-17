<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@ page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cUR2 = "";
	String cRamo = "";
	String algo = "";
	boolean algo2 = false;
	boolean bAplicadoCont = false;

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	int idTipoCaso = c.getIdTC();
	String mensaje = "";
	if (request.getParameter("msg") != null
			&& !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper"))
				.intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();
	System.out.println("CASOOOO: " + id_oper);
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
	algo = usuario.getLogin();

	if (c.getCasoDato("APLICADO_CONT").getValor() != null) {
		if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()))
			bAplicadoCont = true;
	}
	
	/*FAV20171019 Se guarda en base el prefijo de CxP*/
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
	String cxpPrefijo = cabl.getSystemSetting("CXP_PREFIJO") == null?"CP":cabl.getSystemSetting("CXP_PREFIJO");
%>
<%
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Pago Directo - Recepci&oacite;n de documentos</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">

<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

<style type="text/css" title="currentStyle">
@import "css/demo_page.css";

@import "css/demo_table_jui.css";

@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
</style>

<style>
.notEditable {
	background-color: #CCCCCC;
	color: #000000;
}
</style>
<script type="text/javascript" src="js/jquery-1.6.2.min.js">
</script>
<script type="text/javascript" src="js/jquery.dataTables.js">
</script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js">
</script>
<script type="text/javascript" src="js/jquery.ui.datepicker-es.js">
</script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js">
</script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js">
</script>

<script type="text/javascript" src="js/crud.js">
</script>
<script type="text/javascript" src="js/jquery.form-2.94.js">
</script>
<script type="text/javascript" src="js/validaciones.js">
</script>
<script type="text/javascript" src="js/jquery.formatCurrency.js">
</script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js">
</script>
<script type="text/javascript" src="js/FacturaDirecto.js">
</script>
<script type="text/javascript" src="js/ImpuestosRetenciones.js"></script>
<script type="text/javascript" src="js/ValidaMesContable.js"></script>
<script type="text/javascript" charset="utf-8">
var yaSeLleno = 0;
var suma = 0;
var mImporteIVA = 0;
var subTotal_1 = 0.0;
var subTotal_2 = 0.0;
var mImporteNeto = 0.0;
var mImporteBruto = 0.0;
var mImporteSancion = 0.0;
var mImporteDevolucion = 0.0;
var mAmortizacionAnticipo = 0.0;
var mImporteRetencion2 = 0.0;
var mImportePenalizacion = 0.0;
var m2Millar = 0.0;
var m23IVA = 0.0;
var mISRHonorarios = 0.0;
var m5Millar = 0.0;
var mFletes = 0.0;
var mISRArrenda = 0.0;
var mCedular = 0.0;
var bClicBtn = false;
var bAptdoCancel = false;
var breturnVal = false;
var bCOMSOC = false;
var nCOMSOC = 0;

var creditoExt23 = true;

/*FAV20171019 Se guarda en base el prefijo de CxP*/
var cxpPrefijo = "<%=cxpPrefijo%>";

function sinFormatoMonetario(strMoney ){
	return strMoney.replace(/$/g,'').replace(/,/g,'');
}

$(document).ready(
		function()
		{
		
		$("#oficioDelegatorioCaptura").hide();
		$("#oficioDelegatorioCapturaUpdate").hide();
		$( "#dFechaOficio" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
			
		$( "#dFechaOficioUpdate" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
		
		$("#oficioDelegatorioVoBo").hide();
		$("#oficioDelegatorioVoBoUpdate").hide();
		$( "#dFechaOficioVoBo" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
			
		$( "#dFechaOficioVoBoUpdate" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
		
			$( "#dialog-Cancela" ).dialog({
				autoOpen: false,
				height: 250,
				width: 350,
				modal: true,
				buttons: {
						"Aceptar": function() 
						{
							if ($("#cOrigen").val() == ""){
								$("#divEsperaCancelando").attr("style","visibility=visible");
								var nFolioPagado = $("#nFolioApartado").val();
								var cTipoDocto = "PagoApartado";
								$.ajax({
									url: './cierrePresupuestal.jsp',
									type: 'post',
									dataType: 'json',
									async: false,
									data: {tipo:'cancelaEjercidoPagado', nFolioAdefa:nFolioPagado, tipoDocumento:cTipoDocto},
									success: function(data){
											if(data.sinSesion == "sinSesion"){
												location.href = "../index.jsp";
											}else if(data.estatus == "guardado"){
												bAptdoCancel = true;
												getNextSequenceVal({seqName: "RCH" + "<%=cCentroContable%>", async: false, callback: setSequenceValRCH});
								     			$("#noFolio").val( "PDIR-" + "<%=cUR%>" + "-" + $("#id_caso").val() );
								     			$("#nfolio").val( $("#id_caso").val() );
								     			$("#cIdMotivoCancelacion").val( "1" );
								     			$("#motivo").val( $("#cMotivoRechazo").val() );
								     			$("#cnombreRFC").val( $("#cnombre").val() );
								     			$("#fAplicacion").val( "<%=today%>" );								     			
								     			$("#mImporteNeto").val( $("#Imp_Neto").val() );  
												queryFormPost("tVolanteRechazoCreate",{async: false });
												cmdImprimirRCH();
												queryFormPost("borraFactRelacionPagoBorrado", {async: false });
												alert("Apartado Cancelado Correctamente: " + $("#caNoContrarrecibo").val() );
												parent.document.getElementById("pb_send").disabled = false;
												parent.document.getElementById("pb_send").click();		
											}else{
												bAptdoCancel = false;
												alert( "Apartado NO Cancelado: " + $("#caNoContrarrecibo").val() + " - " + data.estatus ) ;
											}
										$("#divEsperaCancelando").attr("style","visibility=hidden");
										$( this ).dialog( "close" );
									}
								});
							}else{
								$("#divEsperaCancelando").attr("style","visibility=visible");
								$("#cIdDocumento").val( $("#No_Folio").val() );
								$("#cMotivoRechazoCRUD").val( $("#cMotivoRechazo").val() );
								$("#nIdEstado").val( "5" );
								
								getNextSequenceVal({seqName: "RCH" + "<%=cCentroContable%>", async: false, callback: setSequenceValRCH});
				     			$("#noFolio").val( "PDIR-" + "<%=cUR%>" + "-" + $("#id_caso").val() );
				     			$("#nfolio").val( $("#id_caso").val() );
				     			$("#cIdMotivoCancelacion").val( "1" );
				     			$("#motivo").val( $("#cMotivoRechazo").val() );
				     			$("#cnombreRFC").val( $("#cnombre").val() );
				     			$("#fAplicacion").val( "<%=today%>" );								     			
				     			$("#mImporteNeto").val( $("#Imp_Neto").val() );  
								queryFormPost("tVolanteRechazoCreate",{async: false });
								cmdImprimirRCH();
								alert("Documento Rechazado: " + $("#caNoContrarrecibo").val() );

								queryFormPost("mPagoDirectoUpdate,tPagoDirectoEncCancelaUpdate", {async: false });
								parent.document.getElementById("pb_send").disabled = false;
								parent.document.getElementById("pb_send").click();		
							} 
						},
						"Cancelar": function() {
							breturnVal = false;
							$("#divEsperaCancelando").hide();
							$("#divRechazoMat").attr("style","visibility=hidden");
							$( this ).dialog( "close" );							
						}
					},
				close: function() {										
				},
				open: function() {
						//if ($("#cOrigen").val() == "MATERIALES"){
						$('#uploadFacturasFrm').attr('src', "UploadFacturas.jsp?tipo_pago=PAGODIRECTO");
							$("#divRechazoMat").attr("style","visibility=visible");
						//}
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
					
					$("#divEsperaProcesando").attr("style","visibility=visible");
					var fAppActualizada = actualizaMesAplicacion();
					
					if( !fAppActualizada){
						$("#divEsperaProcesando").attr("style","visibility=hidden");
						$( "#dialog-Procesando" ).dialog( "close" );
						return false;
					}
					
					$.ajax({
							url:'./cierrePresupuestal.jsp',
							type:'post',
							dataType: 'json',
							data:{tipo:tipo,caNoContrarrecibo:caNoContrarrecibo,campo:campo,tablaEnc:tablaEnc,campoCondicion:campoCondicion,tablaDet:tablaDet,tipoAplicar:tipoAplicar},
							success:function(data){
									if(data.sinSesion == 'sinSesion'){
											location.href = "../index.jsp";
									}
									if(data.estatus == "guardado"){
										/*if(<%=id_oper == 2%> && $( "#cCentroContable" ).val() == "10"){
											cmdImprimir("Contrarecibo");
											alert( "Se va a Imprimir Contra-Recibo: "+ $("#caNoContrarrecibo").val() ) ;
										}*/
										if(<%=id_oper == 1%>){
											if (bCOMSOC){
												creaCasoComsoc($("#cDocumento").val(), $("#id_caso").val());	
											}
											
											/* if ($( "#cCentroContable" ).val() == "10"){
												cmdImprimir("ComprobanteRegistro");	
											}else{
												cmdImprimir("Contrarecibo");
											} */
											//cmdImprimir("NuevoContrarecibo"); //URVP.14092014 Se imprime la solicitud sin poliza
										}else{
											//$("#docAplicado").val( "S" ) ;
											cmdImprimir('PolizaPago');	
										}
										alert( "Documento Aplicado Correctamente: "+ $("#caNoContrarrecibo").val() ) ;
										$("#cIdDocumento").val( $("#No_Folio").val() );
										$("#cMotivoRechazoCRUD").val( "" );
										$("#nIdEstado").val( "4" );
		
										queryFormPost("mPagoDirectoUpdate", {async: false });
										parent.document.getElementById("pb_send").disabled = false;
										parent.document.getElementById("pb_send").click();		

										breturnVal = true;
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

			$( "#dialog-firmantes" ).dialog({
				
				autoOpen: false,
				height: 490,
				width: 480,
				modal: true,
				buttons: {
						"Aceptar": function() 
						{
				
							if($("#cNombreVoBo").val() == ""){ alert("Falta Ingresar Nombre en Datos Vo Bo"); return; } 
							else if($("#cPaternoVoBo").val() == ""){ alert("Falta Ingresar Apellido Paterno en Datos Vo Bo"); return; }
							else if($("#cMaternoVoBo").val() == ""){ alert("Falta Ingresar Apellido Materno en Datos Vo Bo"); return; }
							else if($("#cPuestoVoBo").val() == ""){ alert("Falta Ingresar Puesto en Datos Vo Bo"); return; }
							
							if($("#cNombreAut").val() == ""){ alert("Falta Ingresar Nombre en Datos Autorizar"); return; } 
							else if($("#cPaternoAut").val() == ""){ alert("Falta Ingresar Apellido Paterno en Datos Autorizar"); return; }
							else if($("#cMaternoAut").val() == ""){ alert("Falta Ingresar Apellido Materno en Datos Autorizar"); return; }
							else if($("#cPuestoAut").val() == ""){ alert("Falta Ingresar Puesto en Datos Autorizar"); return; }
							
							if($("#cNombreEla").val() == ""){ alert("Falta Ingresar Nombre en Datos Elabora"); return; } 
							else if($("#cPaternoEla").val() == ""){ alert("Falta Ingresar Apellido Paterno en Datos Elabora"); return; }
							else if($("#cMaternoEla").val() == ""){ alert("Falta Ingresar Apellido Materno en Datos Elabora"); return; }
							else if($("#cPuestoEla").val() == ""){ alert("Falta Ingresar Puesto en Datos Elabora"); return; }
							
							$("#cNombreVo").val($("#cNombreVoBo").val());
							$("#cPaternoVo").val($("#cPaternoVoBo").val());
							$("#cMaternoVo").val($("#cMaternoVoBo").val());
							$("#cPuestoVo").val($("#cPuestoVoBo").val());
							
							$("#cNombreA").val($("#cNombreAut").val());
							$("#cPaternoA").val($("#cPaternoAut").val());
							$("#cMaternoA").val($("#cMaternoAut").val());
							$("#cPuestoA").val($("#cPuestoAut").val());
							
							$("#cNombreE").val($("#cNombreEla").val());
							$("#cPaternoE").val($("#cPaternoEla").val());
							$("#cMaternoE").val($("#cMaternoEla").val());
							$("#cPuestoE").val($("#cPuestoEla").val());
							
							$("#firmanteVoBo").val($("#cNombreVoBo").val()+" "+$("#cPaternoVoBo").val()+" "+$("#cMaternoVoBo").val() );
							$("#firmanteAut").val($("#cNombreAut").val()+" "+$("#cPaternoAut").val()+" "+$("#cMaternoAut").val());
							$("#firmanteEla").val($("#cNombreEla").val()+" "+$("#cPaternoEla").val()+" "+$("#cMaternoEla").val());
							
							var msn = "No Se Guardo Correctamente Informacion de Firmantes"; 
								
							if($("#firmanteExiste").val() == "Existe"){
								queryFormPost({
									queryName : "firmanteModuloUpdate",
									async : false, 
									callback : function(){ 
										msn = "Actualizado Correctamente Firmantes";
									} 
				  				});
							}else{
								queryFormPost({
										queryName : "firmanteModuloCreate", 
										async : false, 
										callback : function(){
											msn = "Guardado Correctamente Firmantes";
										}
								});
							}
							
							queryFormPost("tPagoDirectoEncFirmante_Update", {async: false });
							//alert(msn);
							
							if ($("#oficioDelegatorio").prop("checked")){
							
								if($("#cFolioOficio").val() == ""){ alert("Falta Ingresar el folio de Oficio."); return; } 
								else if($("#dFechaOficio").val() == ""){ alert("Falta Ingresar la fecha del Oficio."); return; }
								else if($("#cNombreTitular").val() == ""){ alert("Falta Ingresar Nombre del Titular."); return; }
								else if($("#cApellidoPaternoTitular").val() == ""){ alert("Falta Ingresar Apellido Paterno del Titular."); return; }
								else if($("#cApellidoMaternoTitular").val() == ""){ alert("Falta Ingresar Apellido Materno del Titular."); return; }
								else if($("#cPuestoTitular").val() == ""){ alert("Falta Ingresar Puesto del Titular."); return; }
								
								$("#cFolioOficioAux").val($("#cFolioOficio").val());
								$("#dFechaOficioAux").val($("#dFechaOficio").val());
								$("#cNombreTitularAux").val($("#cNombreTitular").val());
								$("#cApellidoPaternoTitularAux").val($("#cApellidoPaternoTitular").val());
								$("#cApellidoMaternoTitularAux").val($("#cApellidoMaternoTitular").val());
								$("#cPuestoTitularAux").val($("#cPuestoTitular").val());
								
								if($("#firmanteOficioExiste").val() == "Existe"){
									queryFormPost({	queryName : "tPagoFirmanteDelagatorioUpdate", async : false, callback : function(){ 
															
															msn = "Firmantes Oficio Delegatorio actualizado correctamente.";
														} 
										  });									
								}else{
									queryFormPost({	queryName : "tPagoFirmanteDelagatorioCreate", async : false, callback : function(){ 
																
																msn = "Firmantes Oficio Delegatorio guardado correctamente.";
															} 
											  });
								}
								
								alert(msn);
								
							}
							
							if ($("#oficioDeleVoBo").prop("checked")){
							
								if($("#cFolioOficioVoBo").val() == ""){ alert("Falta Ingresar el folio de Oficio."); return; } 
								else if($("#dFechaOficioVoBo").val() == ""){ alert("Falta Ingresar la fecha del Oficio."); return; }
								else if($("#cNombreTitularVoBo").val() == ""){ alert("Falta Ingresar Nombre del Titular."); return; }
								else if($("#cApellidoPaternoTitularVoBo").val() == ""){ alert("Falta Ingresar Apellido Paterno del Titular."); return; }
								else if($("#cApellidoMaternoTitularVoBo").val() == ""){ alert("Falta Ingresar Apellido Materno del Titular."); return; }
								else if($("#cPuestoTitularVoBo").val() == ""){ alert("Falta Ingresar Puesto del Titular."); return; }
								
								$("#cFolioOficioVoBoAux").val($("#cFolioOficioVoBo").val());
								$("#dFechaOficioVoBoAux").val($("#dFechaOficioVoBo").val());
								$("#cNombreTitularVoBoAux").val($("#cNombreTitularVoBo").val());
								$("#cApellidoPaternoTitularVoBoAux").val($("#cApellidoPaternoTitularVoBo").val());
								$("#cApellidoMaternoTitularVoBoAux").val($("#cApellidoMaternoTitularVoBo").val());
								$("#cPuestoTitularVoBoAux").val($("#cPuestoTitularVoBo").val());
								
								if($("#firmanteOficioVoBoExiste").val() == "Existe"){
									queryFormPost({	queryName : "tPagoFirmanteDelegatorioVoBoUpdate", async : false, callback : function(){ 
															
															msn = "Firmantes Oficio Delegatorio VoBo actualizado correctamente.";
														} 
										  });									
								}else{
									queryFormPost({	queryName : "tPagoFirmanteDelegatorioVoBoCreate", async : false, callback : function(){ 
																
																msn = "Firmantes Oficio Delegatorio VoBo guardado correctamente.";
															} 
											  });	  
									
								}
								
								alert(msn);
							}
							
							if ( procesar() ){
								parent.document.getElementById("pb_save").disabled = false;												
							}
							//procesar();
							$( this ).dialog( "close" );
						},
						
						"Cancelar": function() {
							
							parent.document.getElementById("pb_save").disabled=false;
							$( this ).dialog( "close" );
							
						}
					},
				close: function() {
						
						parent.document.getElementById("pb_save").disabled=false;
						
				}							
	});

	$("#dialog-firmantesUpdate").dialog({
		autoOpen: false,
		height: 490,
		width: 480,
		modal: true,
		buttons: {
				"Aceptar": function() 
				{
					if($.trim($("#cNombreVoBoUpdate").val()) == ""){ alert("Falta Ingresar Nombre en Datos Vo Bo"); return; } 
					else if($.trim($("#cPaternoVoBoUpdate").val()) == ""){ alert("Falta Ingresar Apellido Paterno en Datos Vo Bo"); return; }
					else if($.trim($("#cMaternoVoBoUpdate").val()) == ""){ alert("Falta Ingresar Apellido Materno en Datos Vo Bo"); return; }
					else if($.trim($("#cPuestoVoBoUpdate").val()) == ""){ alert("Falta Ingresar Puesto en Datos Vo Bo"); return; }
					
					if($.trim($("#cNombreAutUpdate").val()) == ""){ alert("Falta Ingresar Nombre en Datos Autorizar"); return; } 
					else if($.trim($("#cPaternoAutUpdate").val()) == ""){ alert("Falta Ingresar Apellido Paterno en Datos Autorizar"); return; }
					else if($.trim($("#cMaternoAutUpdate").val()) == ""){ alert("Falta Ingresar Apellido Materno en Datos Autorizar"); return; }
					else if($.trim($("#cPuestoAutUpdate").val()) == ""){ alert("Falta Ingresar Puesto en Datos Autorizar"); return; }
					
					if($.trim($("#cNombreElaUpdate").val()) == ""){ alert("Falta Ingresar Nombre en Datos Elabora"); return; } 
					else if($.trim($("#cPaternoElaUpdate").val()) == ""){ alert("Falta Ingresar Apellido Paterno en Datos Elabora"); return; }
					else if($.trim($("#cMaternoElaUpdate").val()) == ""){ alert("Falta Ingresar Apellido Materno en Datos Elabora"); return; }
					else if($.trim($("#cPuestoElaUpdate").val()) == ""){ alert("Falta Ingresar Puesto en Datos Elabora"); return; }
					
					$("#cNombreVo").val($("#cNombreVoBoUpdate").val());
					$("#cPaternoVo").val($("#cPaternoVoBoUpdate").val());
					$("#cMaternoVo").val($("#cMaternoVoBoUpdate").val());
					$("#cPuestoVo").val($("#cPuestoVoBoUpdate").val());
					
					$("#cNombreA").val($("#cNombreAutUpdate").val());
					$("#cPaternoA").val($("#cPaternoAutUpdate").val());
					$("#cMaternoA").val($("#cMaternoAutUpdate").val());
					$("#cPuestoA").val($("#cPuestoAutUpdate").val());
					
					$("#cNombreE").val($("#cNombreElaUpdate").val());
					$("#cPaternoE").val($("#cPaternoElaUpdate").val());
					$("#cMaternoE").val($("#cMaternoElaUpdate").val());
					$("#cPuestoE").val($("#cPuestoElaUpdate").val());
					
					$("#firmanteVoBo").val($("#cNombreVo").val()+" "+$("#cPaternoVo").val()+" "+$("#cMaternoVo").val());
					$("#firmanteAut").val($("#cNombreA").val()+" "+$("#cPaternoA").val()+" "+$("#cMaternoA").val());
					$("#firmanteEla").val($("#cNombreE").val()+" "+$("#cPaternoE").val()+" "+$("#cMaternoE").val());
					
					try{
						if (confirm("\u00BFDesea actualizar los datos de los firmantes con la informacion capturada?")){
							queryFormPost("tPagoDirectoEncFirmante_Update", {async: false });
							alert("Firmantes Actualizados Correctamente.");
						
							$("#cNombreVoBoUpdate").val("");
							$("#cPaternoVoBoUpdate").val("");
							$("#cMaternoVoBoUpdate").val("");
							$("#cPuestoVoBoUpdate").val("");
							
							$("#cNombreAutUpdate").val("");
							$("#cPaternoAutUpdate").val("");
							$("#cMaternoAutUpdate").val("");
							$("#cPuestoAutUpdate").val("");
							
							$("#cNombreElaUpdate").val("");
							$("#cPaternoElaUpdate").val("");
							$("#cMaternoElaUpdate").val("");
							$("#cPuestoElaUpdate").val("");
							
							if ($("#oficioDelegatorioUpdate").prop("checked")){
								var msn = "";
								if($("#cFolioOficioUpdate").val() == ""){ alert("Falta Ingresar el folio de Oficio."); return; } 
								else if($("#dFechaOficioUpdate").val() == ""){ alert("Falta Ingresar la fecha del Oficio."); return; }
								else if($("#cNombreTitularUpdate").val() == ""){ alert("Falta Ingresar Nombre del Titular."); return; }
								else if($("#cApellidoPaternoTitularUpdate").val() == ""){ alert("Falta Ingresar Apellido Paterno del Titular."); return; }
								else if($("#cApellidoMaternoTitularUpdate").val() == ""){ alert("Falta Ingresar Apellido Materno del Titular."); return; }
								else if($("#cPuestoTitularUpdate").val() == ""){ alert("Falta Ingresar Puesto del Titular."); return; }
								
								$("#cFolioOficioAux").val($("#cFolioOficioUpdate").val());
								$("#dFechaOficioAux").val($("#dFechaOficioUpdate").val());
								$("#cNombreTitularAux").val($("#cNombreTitularUpdate").val());
								$("#cApellidoPaternoTitularAux").val($("#cApellidoPaternoTitularUpdate").val());
								$("#cApellidoMaternoTitularAux").val($("#cApellidoMaternoTitularUpdate").val());
								$("#cPuestoTitularAux").val($("#cPuestoTitularUpdate").val());
								
								if($("#firmanteOficioExiste").val() == "Existe"){
									queryFormPost({	queryName : "tPagoFirmanteDelagatorioUpdate", async : false, callback : function(){ 
															
															msn = "Firmantes Oficio Delegatorio actualizado correctamente.";
														} 
										  });									
								}else{
									queryFormPost({	queryName : "tPagoFirmanteDelagatorioCreate", async : false, callback : function(){ 
																
																msn = "Firmantes Oficio Delegatorio guardado correctamente.";
															} 
											  });
								}
								
								alert(msn);
										  
								$("#cFolioOficioUpdate").val("");
								$("#dFechaOficioUpdate").val("");
								$("#cNombreTitularUpdate").val("");
								$("#cApellidoPaternoTitularUpdate").val("");
								$("#cApellidoMaternoTitularUpdate").val("");
								$("#cPuestoTitularUpdate").val("");
								
							}
							
							if ($("#oficioDeleVoBoUpdate").prop("checked")){
								
								var msn = "";
								
								if($("#cFolioOficioVoBoUpdate").val() == ""){ alert("Falta Ingresar el folio de Oficio."); return; } 
								else if($("#dFechaOficioVoBoUpdate").val() == ""){ alert("Falta Ingresar la fecha del Oficio."); return; }
								else if($("#cNombreTitularVoBoUpdate").val() == ""){ alert("Falta Ingresar Nombre del Titular."); return; }
								else if($("#cApellidoPaternoTitularVoBoUpdate").val() == ""){ alert("Falta Ingresar Apellido Paterno del Titular."); return; }
								else if($("#cApellidoMaternoTitularVoBoUpdate").val() == ""){ alert("Falta Ingresar Apellido Materno del Titular."); return; }
								else if($("#cPuestoTitularVoBoUpdate").val() == ""){ alert("Falta Ingresar Puesto del Titular."); return; }
								
								$("#cFolioOficioVoBoAux").val($("#cFolioOficioVoBoUpdate").val());
								$("#dFechaOficioVoBoAux").val($("#dFechaOficioVoBoUpdate").val());
								$("#cNombreTitularVoBoAux").val($("#cNombreTitularVoBoUpdate").val());
								$("#cApellidoPaternoTitularVoBoAux").val($("#cApellidoPaternoTitularVoBoUpdate").val());
								$("#cApellidoMaternoTitularVoBoAux").val($("#cApellidoMaternoTitularVoBoUpdate").val());
								$("#cPuestoTitularVoBoAux").val($("#cPuestoTitularVoBoUpdate").val());
								
								if($("#firmanteOficioVoBoExiste").val() == "Existe"){
									queryFormPost({	queryName : "tPagoFirmanteDelegatorioVoBoUpdate", async : false, callback : function(){ 
															
															msn = "Firmantes Oficio Delegatorio VoBo actualizado correctamente.";
														} 
										  });									
								}else{
									queryFormPost({	queryName : "tPagoFirmanteDelegatorioVoBoCreate", async : false, callback : function(){ 
																
																msn = "Firmantes Oficio Delegatorio VoBo guardado correctamente.";
															} 
											  });
								}
									
								alert(msn);
									  
								$("#cFolioOficioVoBoUpdate").val("");
								$("#dFechaOficioVoBoUpdate").val("");
								$("#cNombreTitularVoBoUpdate").val("");
								$("#cApellidoPaternoTitularVoBoUpdate").val("");
								$("#cApellidoMaternoTitularVoBoUpdate").val("");
								$("#cPuestoTitularVoBoUpdate").val("");
								
							}
						}
					}catch(e){
						alert("No se pudo actualizar los firmantes, intente mas tarde.");
					}
					$(this).dialog("close");
				},

				"Cancelar": function() {
					$(this).dialog("close");
				}
			},
		close: function(){}							
	});
			
			
			$( "#dialog-Terminado" ).dialog({
				autoOpen: false,
				height: 400,
				width: 400,
				modal: false
			});
			
			$( "#dialog-validaOLI" ).dialog({
				autoOpen: false,
				height: 250,
				width: 250,
				modal: true,
				buttons: {
						"Aceptar": function() 
						{
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
										if (Number( MontoOlis ) > 0){
											$("#cartera").val( vCartera );
											$("#capitulo").val( vEP.substring(31, 36) );
											$("#UE").val( vUnidEj );
											$("#mImporteCartera").val("0.00");
											queryFormPost("vAcumulaImporteCarteraEP", {async: false });
											var importeTotal = Number( $("#mImporteCartera").val() ) + Number( $("#mMovimiento").val() );
											var importeTotal = importeTotal.toFixed(2);
											
											if( Number( importeTotal ) <= Number( MontoOlis ) ){
												cambioMovmientos();	
											}else{
												alert("El Importe Capturado sobregira la Cartera ");
											}
										}else{
											alert("El N\u00FAmero de OLI no es V\u00E1lido ");
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
							$( this ).dialog( "close" );
						},
						"Cancelar": function() {
							breturnVal = false;
							$( this ).dialog( "close" );							
						}
					},
				close: function() {	

				}
			});

			
			$( "#dialog-form" ).dialog({
				autoOpen: false,
				height: 400,
				width: 800,
				modal: false
			});

			$('#Concepto').bind('copy paste', function (e) {       
				e.preventDefault();
	    	});

		
		 	$('.currency').blur(function()	{
				$('.currency').formatCurrency();
				});

			creaDiagloFacturas();
			creaDTFacturas();

			$("#pbAgregar")
				.button()
				.click(function() {
					if ( $("#cFactura").val() == "" || Number( $("#cFactura").val() ) <= 0 ){
						alert("Falta Capturar el N\u00FAmero de Factura");
						return;
					}

					if ( $("#mImporteFact").val() == "" ||  Number( $("#mImporteFact").val() ) <= 0 ){
						alert("Falta Capturar el Importe de la Factura");
						return;
					}

					var aData = oTablevFact.fnGetData();
					for(var i=0; i<aData.length; i++) {
						var cFactura = aData[ i ][ 0 ] + aData[ i ][ 1 ];
						if( $("#cSerie").val() + $("#cFactura").val() == cFactura){
							alert("La Factura ya est\u00E1 capturada");
							$("#cSerie").val( "" );
							$("#cFactura").val( "" );
							$("#mImporteFact").val( "" );
							return;
						}
					}
					
					var mImporte = quitaFmt( $("#mImporteFact").val() );
					if (mImporte>0)
					{
						$("#mImporteFact").formatCurrency();
						$('#grdValidaFacturas').dataTable().fnAddData( [ $("#cSerie").val(), $("#cFactura").val(), $("#mImporteFact").val()] );
						mImporte = Number( quitaFmt( $("#mTotalFacturaV").val() ) ) + Number( mImporte );
						$("#mTotalFacturaV").val( mImporte );
						$("#mTotalFacturaV").formatCurrency();
						$("#cSerie").val("");
						$("#cFactura").val("");
						$("#mImporteFact").val("");
					}
					else if (mImporte<0)
					{
						alert("No se puede agregar una factura con importe Negativo");
					} 
					else if (mImporte==0)
					{
						alert("No se puede agregar una factura con importe 0");
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
	     				var cFolioFact = aData[1];
	     				
	     				if( cFolioFact != "" ){
	     					$("#cFacturaEliminar").val(cFolioFact);
	     					
	     					queryFormPost({
	     						queryName:"facturaPagoDelete",
	     						async:false,
	     						callback:function(){
									leeDatosFactura();
	     							$("#mTotalFacturaV").formatCurrency();
	     							oTablevFact.fnDeleteRow(aPos);
	     						}
	     					});
	     					
	     					$("#cFacturaEliminar").val("");
						}
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
			
			
			$("#DESTINO_GASTO").change(function(){
				if ( $(this).val() == "AL" ){
					document.getElementById("Imp_Bruto").removeAttribute("readonly",false);
				}else if ( $(this).val() != "" ){
					if( $("#cIDRFC").val() == "" ){
						$('#DESTINO_GASTO option[value=" "]').attr('selected','selected');
						alert("Favor de seleccionar un Beneficiario");
						return;
						//$(this).val('');
						//$(this).change();
					}else{
						$("#Imp_Bruto").attr("readonly", true);
						creaDiagloFacturas('true');
					}
					if ( $.trim($(this).val())!="" && $("#cIDRFC").val() != ""){
						if($("#numPaso").val() == "1" )
							$("#EditaFacturas").css('visibility', 'visible');
					}else{
						$("#EditaFacturas").css('visibility', 'hidden');
					}
				}
			});
			
			$('#grdMovimientos').dataTable(
				{
					"iDisplayLength": 20,
					sScrollY: "150px",
					sScrollX: "700px",
					"bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": true,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"

				} );
			
			$('#grdCompromisos').dataTable(
				{
					"iDisplayLength": 20,
					"bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"

				} );

			$('#grdRetencion').dataTable(
				{
					"bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"

				} );

				$('#dtRecalcRetencionEP').dataTable(
					{
						"iDisplayLength": 100,
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
			
			/* Add a click handler to the rows - this could be used as a callback */
 			$("#grdMovimientos tbody").dblclick(function(event) {
				//if ($("#btAgregaEP").attr('disabled') == null){
				
				if(<%=id_oper == 1%>){
					
  					$(oTableMov.fnSettings().aoData).each(function (){
   						$(this.nTr).removeClass('row_selected');
   						var aPos = oTableMov.fnGetPosition( this.nTr );
    					// Get the data array for this row
    					var aData = oTableMov.fnGetData( aPos[0] );

 					});
  					$(event.target.parentNode).addClass('row_selected');
     				var aPos = oTableMov.fnGetPosition( event.target.parentNode );
     				var aData = oTableMov.fnGetData( aPos );
					quitaFormato();
     				$("#nClaveCNA1").val(aData[2]);
     				$("#EP").val(aData[1]);
     				
     				//$("#mMovimiento").val(parseFloat(Sinfrmt(aData[5]).toFixed(2)));
     				$("#mMovimiento").val(parseFloat(aData[5]).toFixed(2));
     				
     				oTableMov.fnDeleteRow( aPos );
					oTablevFact.fnDeleteRow( aPos ); //ULISES, PORQUE BORRAR LA FACTURA DEL MISMO RENGLON??? 

					var vOGT = $("#EP").val();
					vOGT = vOGT.substring(31, 36);
					$("#vOGT").val( vOGT );

					$("#partCOMSOC").val("");
					queryFormPost("BuscaPartidaCOMSOCRead", {async : false});
					if (vOGT == $("#partCOMSOC").val()){
						nCOMSOC--;
						bCOMSOC = (nCOMSOC > 0);
					}
					var aux = $("#vOGT").val();//URVP.09062014 se anade variable para guardar temporalmente el valor del objeto y asignarle al objeto el renglon a eliminar
					$("#vOGT").val(aData[0]);
					queryFormPost("tpagodirectodetalleDelete,tPagoApartadoDetDelete", {async: false });
					queryFormPost("UpdateRecorridoRenglonPD,UpdateRecorridoRenglonAPDPD", {async: false });
					
					$("#vOGT").val(aux);//URVP.09062014 se regresa el valor que tenia el objeto
					calculaIVA(1, $("#mMovimiento").val(), 1);
					
					actVariablestmp();
					actualSaldosDet(-1);

					$('#grdMovimientos').dataTable().fnClearTable(); //URVP.09062014 LIMPIA EL GRID PARA CARGAR LOS DATOS UNA VES ELIMINADOS Y ACTUALIZADOS LOS RENGLONES EN BD
					consMovimientos();
					$("#Acumulado_Op").val(parseFloat($("#Acumulado_Op").val()) - parseFloat($("#mMovimiento").val()));
					$("#mMovimiento").val("");
					
					if ($("#TFONDO").val()=="CE"){
						queryFormPost("TPAGODIRECTOdetallexFolioDelete",{async:false});
						$("#EP").val("");
						$("#Acumulado_Op").val("0");
					}
					queryFormPost("exiteDetalleEPS",{async:false});
					if (Number($("#NumeroDetalle").val())==0){
						document.getElementById("tConcepto").removeAttribute("disabled",false);//SE HABILITA PARA TOMAR EL DATO
					}
					ponFormato();
				}
 			});

 				/* Add a click handler for the delete row */
 				$('#delete').click( function() {
  					var anSelected = fnGetSelected( oTableMov );
  					oTableMov.fnDeleteRow( anSelected[0] );
 				} );

 				/* Init the table */
 				oTableMov = $('#grdMovimientos').dataTable( );

				/* Add a click handler to the rows - this could be used as a callback */
 				$("#grdRetencion tbody").click(function(event) {
  					$(oTable.fnSettings().aoData).each(function (){
   						$(this.nTr).removeClass('row_selected');
   						var aPos = oTable.fnGetPosition( this.nTr );
    					// Get the data array for this row
    					var aData = oTable.fnGetData( aPos[0] );
 					});
  					$(event.target.parentNode).addClass('row_selected');
 				});
 				$("#grdRetencion tbody").dblclick(function(event) {
					quitaFormato();
					if ($("#btAgregaMov").attr('disabled') == null){

  						$(oTable.fnSettings().aoData).each(function (){
   							$(this.nTr).removeClass('row_selected');
   							var aPos = oTable.fnGetPosition( this.nTr );
						
    						// Get the data array for this row
    						var aData = oTable.fnGetData( aPos[0] );

 						});
	  					$(event.target.parentNode).addClass('row_selected');
	     				var aPos = oTable.fnGetPosition( event.target.parentNode );
						var temp=0;
	     				var aData = oTable.fnGetData( aPos );
						temp=aData[0];
	     				$("#cveRetencion").val( temp);
	     				
	     				//URVP.03102014 Se valida que la retencion a eliminar no sea obligatoria de acuerdo a las partidas del pago
	     				queryFormPost("RetencionObligatoria", {async: false });
	     				if (Number($("#obligatoria").val())!=0){
	     					alert("No se puede eliminar esa retencion debido a que es obligatoria de acuerdo a las partidas que se usan en el pago.");
	     					return;
	     				}
	     				
						if( aData[0] == 2 ){
							$("#aux").val(2);//akki
							$("#divMilla2").hide();
						}
	     				oTable.fnDeleteRow( aPos);
						queryFormPost("tpagodirectoretencionDelete", {async: false });
	
						calculaIVA(1,$("#Imp_Bruto").val());
					    actVariablestmp();
						actualSaldos(1);

						//$("#Imp_Iva").val(Math.round(mImporteIVA*100)/100);
						$("#Imp_Retenciones").val(Math.round(mImporteRetencion2*100)/100);
						//$("#Imp_Ejercer").val(Math.round(mImporteNeto*100)/100);
						$("#Imp_Ejercer").val(Math.round((mImporteNeto + mImporteRetencion2 + mImportePenalizacion)*100)/100);
	
						//$("#Imp_Neto").val(Math.round(mImporteNeto*100)/100);
	
						//Actulizo en base de datos las neuvas retenciones
						$("#elcontra").val($("#caNoContrarrecibo").val());
					   	$("#elrfc").val($("#cIDRFC").val());
						$(".paso01").attr('disabled', false);
						$(".subtotall").attr('disabled', false);
						quitaFormato();
						$("#ctaBancaria").attr('disabled', false); //URVP.12112014 Se habilita el combo de l cuenta bancaria para leer el dato y se deshabilita nuevamente dos lineas abajo
						queryFormPost("TPAGODIRECTOencabezadoxFolioDelete,tPagoDirectoFacturaCreate", {async: false });
						$("#ctaBancaria").attr('disabled', true);
						queryFormPost("tPagoDirectoEncabezadoUpdate",{async: false });
						queryFormPost("updateEncabezadoDirectoIvaNeto",{async: false });
						
						if (<%=id_oper == 2%> && $("#cOrigen").val() != "" ){
							RecalcRetencionEP();
						}
						$(".paso01").attr('disabled', true);
						$(".subtotall").attr('disabled', true);
					}
					ponFormato();
 				});

 				/* Add a click handler for the delete row */
 				$('#delete').click( function() {
  					var anSelected = fnGetSelected( oTable );
  					oTable.fnDeleteRow( anSelected[0] );
				});

 /* Init the table */
 oTable = $('#grdRetencion').dataTable( );
		var suma = 0;
	var res_iva=0;


$("#cIDRFC").change(function () {
	/* VGC Se comenta para liberacion. Si se requiere en conafor, descomentar e investigar funcionalidad de AMF
	$("#cIdRFC").val( $("#cIDRFC").val() );
	querySelectPost("PagosAMFRead", "NumPagoAMF", {async: false });
	*/	
});

$("#NumPagoAMF").change(function () {
	$("#nClaveAMF").val('');
	$("#numFolioAMF").val('');
	$("#PagoAMF").val('');
	queryFormPost("tPagoAMFRead", {async: false });
	var vConcepto = $("#Concepto").val();
	vConcepto = vConcepto + ' ' + $("#referenciaAMF").val(); 
	$("#Concepto").val( vConcepto );
});


$("#tConcepto").change(function(){
    $("#TIPO_CONCEPTO").val($("#tConcepto").val());
	//querySelectPost("CatalogoObraTMovimendoRead","tmovimiento", {async: false }); //se comentariza y se agrega el sig queryselectpost para dejar por defaulta seleccionado Pago Normal
	querySelectPost({
		queryName: 'CatalogoObraTMovimendoRead', 
		targetObjectId: 'tmovimiento', 
		async: false
		//callback:function(){
			//seleccionaPorValor("","");
			//$("#tmovimiento").attr("disabled","disabled");
		//}
	});
	//$("#tmovimiento").attr('disabled', true); //URVP.10092014 SE DESHABILITA EL COMBO DE TIPO DE MOVIMIENTO
	if ($("#tConcepto").val() == "AL"){
		querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
		$("#altaAlmacen").val("");
		$("#altaAlmacen").show();
		$("#cAnioFactEP").val("");
		$("#nFacturaEP").val("");
		$("#cAnioFactEP").show(); 
		$("#nFacturaEP").show();
	}
	else{
		$("#altaAlmacen").val("0");
		$("#altaAlmacen").hide();
		$("#cAnioFactEP").val("");
		$("#nFacturaEP").val("");
		$("#cAnioFactEP").hide(); 
		$("#nFacturaEP").hide();
		querySelectPost("CatalogoAlmacenVacioRead","ALM", {async: false });
	}

});

$("#cIdTipoRetencion").change(function(){
	if ( $( this ).val() == 2){
		$("#divMilla2").show();
	}else{
		$("#divMilla2").hide();
	}
});

$("input.AyudaSyC").subIniciaDlg();
$("input.autoCompletaSyC").subIniciaAutoCompleta();

init();

var aaa = <%=request.getParameter("folio")%>;

$("#cRamo").val( "<%=cRamo%>" );
$("#cUnidadResponsable").val( "<%=cUR%>" );
$("#cUnidadResponsable2").val( "<%=cUR%>" );

$("#OIRAUSU").val( "<%=algo%>" );
$("#miOper").val( "<%=id_oper%>" );
//        $("#docAplicado").val( "<%=algo2%>" );
$("#cCentroContable").val( "<%=cCentroContable%>" );
if ($("#cCentroContable").val() != "10"){
	$("#cUnidadEjecutora").val(  "<%=cUR%>" );
}

$("#id_caso").val(aaa);
      
$("#tabs").tabs( {
       "show": function(event, ui) {
           var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
           if ( oTable.length > 0 ) {
               oTable.fnAdjustColumnSizing();
           }
      	}
  	} );



			$('#grdFacturas').dataTable(
				{
					"iDisplayLength": 20,
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

	cssReadOnly();
	queryFormPost("firmantesModuloRead, firmantesModuloReadAut", {async: false }); // voBo - autoriza
	if ($("#miOper").val()==2)//URVP.16062014 si ya esta por autorizar la solicitud se deshabilitan grid y botones que ya no se requieren
			{
				$("#TFONDO").attr('disabled', true);
				$("#nIdClaveEgresos2").attr('disabled', true);
				$("#grdMovimientos").attr('disabled', true);
				$("#DCD_CONCEPTO").attr('disabled', true);
				$("#grdRetencion").attr('disabled', true);
			}
		
		queryFormPost({
			queryName:"readImporteBrutoPago", 
			async:false, 
			callback:function(){
				if( $("#Imp_Bruto").val() == "" )
					$("#Imp_Bruto").val("$0.00");
				$(".subtotall"). change();
			} 
		});
		$("#TFONDO option[value='FF']").attr('selected','selected'); 
	});  //fin del ready
	
function cssReadOnly(){
	$( "[readOnly]" ).each(function(){	
		$(this).addClass("notEditable");	
	});
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
				alert("El Pago est\u00E1 en Proceso de Autorizaci\u00F3n por parte del \u00E1rea Normativa");
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xh.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
			return false;
		}
	});
	
}

			
 function generar(){
 		try {
        	var table = document.getElementById('grdCompromisos');
        	//var table2 = document.getElementById('dt_paraEnvio')
            var rowCount = table.rows.length;
            for(var i=0; i<rowCount; i++) {
            	var row = table.rows[i];
                var chkbox = '';
				try{
				  chkbox = row.cells[0].childNodes[0].nodeValue;
				}catch(e){
         			null;
         		}
				if(null != chkbox ) {
					//quitando de la lista de compromisos los registros enviados a SICOP...
                    table.deleteRow(i);
                    rowCount--;
                    i--;
				}
            }
         }catch(e) {
         	alert(e);
         }
 	}

function borraRetxEP(){
	try {
  		var table = document.getElementById('grdRetClave');
    	var rowCount = table.rows.length;
    	var acumBorrar = 0.0;
		for(var i=1; i<rowCount; i++) {
    		var row = table.rows[i];
    		var chkbox = '';
    		var elMonto = '';
    		try
			{
	  			var chkbox = row.cells[4].childNodes[0];
	  			var elMonto = row.cells[3].childNodes[0];
	  		}
	 		catch(e) {
   				null;
   			}
			if(null != chkbox ) {
				if(chkbox.toString() == $("#EP").val()){
				//alert(elMonto.toString());
					acumBorrar = parseFloat(acumBorrar) + parseFloat(elMonto.toString());
					table.deleteRow(i);
    		  		rowCount--;
    		  		i--;
				}
   			}
    	}
		$("#mImporteRetencion").val(parseFloat($("#mImporteRetencion").val())-parseFloat(acumBorrar));
  	}
  	catch(e) {
   		alert(e);
	}
}

	function carga(){
			$("#cancelar").show();//URVP.30082014 SE MUESTRA EL BOTON DE BORRAR EL PD
           var con=document.getElementById("TO_TIPO_DOCTO").value;
			$("#fRecepcion").val( "<%=today%>" );
			$("#divImprime").hide();
			$(".pasoDos").hide();
			$(".pasoTres").hide();
			$(".pasoCuatro").hide();			
			$(".pasoCinco").hide();
			//descomentar$("#divImprimePoliza").hide();
			$("#divImprimePoliza").hide();
			$("#divImprimeAnexo").hide();
 		 	$("#btRegresar").hide();
			$("#divAutorizar").hide();
			$("#divMilla2").hide();
			querySelectPost("CAT_TIPO_IVARead", "DESCRIPCION20", {async: false });
			querySelectPost("CAT_TIPO_IVAporCienRead", "por_Iva", {async: false });
			//querySelectPost("CAT_TIPO_IVAporCienRead", "DESCRIPCION20", {async: false });
			
			$("#por_Iva").val("0.1600");
    		$("#TIPO_CONCEPTO").val($("#tConcepto").val());
		    querySelectPost("CAT_TIPO_OPERACIONRead", "cIdTipoOperacion", {async: false });
           // querySelectPost("catalogoTipoPersonaRFCRead","cIdTipoRFC", {async: false });
			querySelectPost("CatalogoObraDGastoRead","destGasto", {async: false });

			querySelectPost("CatalogoObraTConceptoRead","tConcepto", {async: false });
			$("#TIPO_CONCEPTO").val($("#tConcepto").val());
			//querySelectPost("CatalogoObraTMovimendoRead","tmovimiento", {async: false });

			querySelectPost("CatalogoObraDGastoRead", "DESTINO_GASTO", {async: false });
			querySelectPost("CatalogoTipoRetencionRead", "cIdTipoRetencion", {async: false });
			queryFormPost("tEjercicioRead",{async: false });
			queryFormPost("fRecepcionRead", {async: false });
			$("#cEjercicio").val( $("#aEjercicioFiscal").val() ) ;
			queryFormPost("montoMaximoPagoDirectoRead", {async: false });
			//queryFormPost("cCentroContableRead", {async: false });
			queryFormPost("TipoPolizaRead",{async: false });
			
			if ($("#TFONDO").val() == "CE"){
				cambio();
				cambio2();
			}else {//if($("#TFONDO").val() == "FF"){  /* Se agrega para mostrar informacion despues del la pestana adjuntos */
			}
 			//$("#TFONDO").val("FF"); //ULISES, REVISAR LA COMNTARIZACION DE ESTA LINEA
			tfondos();
			if ($("#aEjercicioFiscal").val() >= '2013'){
	            $("#FechaAplicacion").val( "<%=today%>" );
				$("#fRecepcion").val( "<%=today%>" );

				if ($("#fRecepcion").val().split("/")[2] != $("#aEjercicioFiscal").val() ) {
	            	$("#fRecepcion").val( "31/12/" + $("#aEjercicioFiscal").val() );
	            	$("#FechaAplicacion").val( $("#fRecepcion").val() );
            		$("#FECHA_CARGA").val( $("#fRecepcion").val() );
	            }

	            $("#FechaAplicacion").hide();
				$("#FechaAplicacion").attr("readonly","readonly");
				$("#fRecepcion").attr( "readonly","readonly" );
			}

		  	queryFormPost("tPagoDirectoEncabezadoRead", {async: false });
		  	$("#elcontra").val($("#caNoContrarrecibo").val());
		 	queryFormPost("tPagoApartadoEncabezadoRead",{async: false });
			if ($( "#Fecha_Pago" ).val() == "01/01/1900"){
		  		$( "#Fecha_Pago" ).val("");
		  	}
		  	
		  	//VGC Se comenta para liberacion. Si se requiere se tendra que investigar la funcionalidad de AMF
		  	//$("#cIDRFC").change();
		  	//$("#NumPagoAMF").val($("#NumPagoAMF_1").val());
		  	//alert( $("#NumPagoAMF_1").val() );
		  	//queryFormPost("tPagoAMFRead", {async: false });
		  	
			//$("#NumPagoAMF").change();
			setTimeout("pausa1()",50);
			ponFormato();
			BuscaPoliza();
//				pausa1();

		if ($("#numPaso").val()!="1"){ 
			$("#EditaFacturas").css('visibility', 'hidden');
			querySelectPost("readCtaBancxPago", "ctaBancaria",{async: false });
			$("#ctaBancaria").attr('disabled', true);
		}
		
		leeDatosFactura();
		
     }


	function ChecaSiexisteCodSif() {
		var regresa = false;
		try {
        	var table = document.getElementById('grdMovimientos');
          	var rowCount = table.rows.length;
          	var yaExiste = 0;
          	for(var i=0; i<rowCount; i++) {
	          	var row = table.rows[i];
	            var chkbox = '';
				try{
					var chkbox = row.cells[2].childNodes[0];
				}catch(e) {
        			null;
        		}
	        	if(null != chkbox ) {
					if(chkbox.toString() == $("#EP").val()){
	        			yaExiste = 1;
					}
	        	}
        	}
        }catch(e){
        	alert(e);
        }
		if (yaExiste == 1){
			alert("Ya se ingres\u00F3 un movimiento con esa clave");
			regresa = true;
		}
        return regresa;
	}

	

	function actVariablestmp(){
		$("#mBruto").val(parseFloat(mImporteBruto));
		$("#mSancion").val(parseFloat(mImporteSancion));
		$("#mDevolucion").val( parseFloat(mImporteDevolucion));
		$("#mAmortizacionAnticipo").val( mAmortizacionAnticipo);
		$("#mIVA").val(parseFloat(mImporteIVA));
		$("#mRetencion").val(mImporteRetencion2);
		$("#mPenalizacion").val( mImportePenalizacion);
		$("#mNeto").val( mImporteNeto);
		$("#m2Millar").val(m2Millar);
		$("#m23IVA").val(parseFloat(m23IVA));
		$("#mISRHonorarios").val(mISRHonorarios);
		$("#m5Millar").val(m5Millar);
		$("#mFletes").val( mFletes);
		$("#mISRArrenda").val(mISRArrenda);
		$("#mCedular").val(mCedular);
		$("#laamortizacion").val( mAmortizacionAnticipo);
		$("#mImporteMasIva").val(parseFloat(mImporteBruto) + parseFloat(mImporteIVA) - parseFloat(mImportePenalizacion)); //URVP.12092014 $("#mImporteMasIva").val(parseFloat(mImporteBruto));
	
	}

function actualSaldosDet( elTipo ){
    var laRetencion = 0;
	
    laRetencion = Number( m2Millar ) + Number( m23IVA ) + Number( mISRHonorarios ) + Number( m5Millar ) + Number( mFletes ) + Number( mISRArrenda ) + Number( mCedular );
    laRetencion = laRetencion.toFixed(2);
    
	$("#DCD_ISR_D").val(parseFloat($("#DCD_ISR_D").val()) + elTipo * parseFloat($("#mISRHonorarios").val()) + elTipo * parseFloat(mISRArrenda));
	$("#DCD_IVA_D").val(parseFloat($("#DCD_IVA_D").val()) + elTipo * parseFloat($("#m23IVA").val()));
	$("#DCD_MIL5_D").val(parseFloat($("#DCD_MIL5_D").val()) + elTipo * parseFloat($("#m5Millar").val()));
	$("#DCD_MIL2_D").val(parseFloat($("#DCD_MIL2_D").val()) + elTipo * parseFloat($("#m2Millar").val()));
	$("#DCD_CONTRIBUCION_D").val(parseFloat($("#DCD_CONTRIBUCION_D").val()) + elTipo * parseFloat($("#mFletes").val()));
	$("#DCD_OTRAS_RET_D").val(parseFloat($("#DCD_OTRAS_RET_D").val()) + elTipo * parseFloat($("#mCedular").val()));
	$("#DCD_PENALIZACION_D").val(parseFloat($("#DCD_PENALIZACION_D").val()) + elTipo * parseFloat($("#mPenalizacion").val()));
	$("#DCD_SANCION_D").val(parseFloat($("#DCD_SANCION_D").val()) + elTipo * parseFloat($("#mSancion").val()));
	$("#DCD_DEVOL_D").val(parseFloat($("#DCD_DEVOL_D").val()) + elTipo * parseFloat($("#mDevolucion").val()));
	$("#DCD_AMORT_D").val(parseFloat($("#DCD_AMORT_D").val()) + elTipo * parseFloat($("#laamortizacion").val()));
	$("#DCD_RETENCION_D").val(parseFloat($("#DCD_RETENCION_D").val()) + elTipo * parseFloat(laRetencion));

}

function AjustaRetencionesD(){
		
	//$("#mIVA").val( Number(mImporteIVA) );
	$("#mRetencion").val( Number(mImporteRetencion2) + ( Number( $("#DCD_RETENCION").val() ) - Number($("#DCD_RETENCION_D").val()) ) );
	
	$("#mDevolucion").val( Number( $("#mDevolucion").val() ) + ( Number( $("#DCD_DEVOL").val() ) - Number( $("#DCD_DEVOL_D").val() ) ) );
	
	$("#mPenalizacion").val( Number(mImportePenalizacion) + ( Number( $("#DCD_PENALIZACION").val() ) - Number($("#DCD_PENALIZACION_D").val()) ));
	$("#m2Millar").val( Number(m2Millar) + ( Number( $("#DCD_MIL2").val() ) - Number($("#DCD_MIL2_D").val()) ) );
	$("#m23IVA").val( Number( m23IVA ) + ( Number( $("#DCD_IVA").val() ) - Number($("#DCD_IVA_D").val()) ) );
	$("#m5Millar").val( Number(m5Millar) + ( Number( $("#DCD_MIL5").val() ) - Number($("#DCD_MIL5_D").val()) ) );
	$("#mFletes").val( Number(mFletes) + ( Number( $("#DCD_CONTRIBUCION").val() ) - Number($("#DCD_CONTRIBUCION_D").val()) ) );
	
	if (mISRHonorarios > 0)
		$("#mISRHonorarios").val( Number(mISRHonorarios) + ( Number( $("#DCD_ISR").val() ) - Number($("#DCD_ISR_D").val())));	
	else
		$("#mISRArrenda").val( Number(mISRArrenda) + ( Number( $("#DCD_ISR").val() ) - Number($("#DCD_ISR_D").val()) ) );
	
	$("#mCedular").val( Number(mCedular) + ( Number( $("#DCD_OTRAS_RET").val() ) - Number($("#DCD_OTRAS_RET_D").val()) ) );
	$("#mSancion").val( Number(mImporteSancion) + ( Number( $("#DCD_SANCION").val() ) - Number($("#DCD_SANCION_D").val()) ) );

	$("#mAmortizacionAnticipo").val( Number(mAmortizacionAnticipo) + ( Number( $("#DCD_AMORT").val() ) - Number($("#DCD_AMORT_D").val()) ) );
	$("#laamortizacion").val( Number(mAmortizacionAnticipo) + ( Number( $("#DCD_AMORT").val() ) - Number($("#DCD_AMORT_D").val()) ) );
	
	$("#mNeto").val( Number( $("#mImporteMasIva").val() ) - Number( $("#mRetencion").val())); //URVP.12092014 $("#mNeto").val( Number( $("#mImporteMasIva").val() ) - Number( $("#mRetencion").val() ) - Number( $("#mPenalizacion").val() ) );
	
}

function fnClickAddRowTable(A, B, C,D,E,F,G,H,I,J) {
	  $('#grdMovimientos').dataTable().fnAddData( [	A, B, C, D, E, F ] );
}

function fnClickAddRowComprobatoria(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) {
					  $('#grdFacturas').dataTable().fnAddData( [
					A,
					B,
					C,
					D, E, F, G, H, I, J, K, L, M, N, O ]);
			}



function pausa1(){
	ponCeros();
	ponCerosD();
	if ($("#tConcepto").val() == "AL"){
		querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
		$("#altaAlmacen").val("");
		$("#altaAlmacen").show();
		$("#cAnioFactEP").val("");
		$("#nFacturaEP").val("");
		$("#cAnioFactEP").show(); 
		$("#nFacturaEP").show();
	} else{
		$("#altaAlmacen").val("0");
		$("#altaAlmacen").hide();
		$("#cAnioFactEP").val("");
		$("#nFacturaEP").val("");
		$("#cAnioFactEP").hide(); 
		$("#nFacturaEP").hide();
	}
	$("#numPaso").val("1");
	if ($("#No_Folio").val()!=""){
		$("#numPaso").val("2");
		$(".paso01").attr('disabled', true);
	  	$(".subtotall").attr('disabled', true);
		$(".pasoDos").show();
		queryFormPost("FuentesdeFinanciamientoDirectoRead", {async: false });
	    if ($("#tieneFondo").val() != ""){
			$(".pasoTres").show();
	    	$("#numPaso").val("3");
			$(".paso02").attr('disabled', true);
					
			if ($("#TFONDO").val() == "CE"){
				$("#Prestamo").attr('disabled', false);
				$("#CInversion").attr('disabled', false);
				cambio();
				cambio2();
				queryFormPost("FuentesdeFinanciamientoDirectoRead", {async: false });
    		}
			tfondos();
			$("#TFONDO").attr('disabled', true);
			$("#Prestamo").attr('disabled', true);
			$("#CInversion").attr('disabled', true);
	  	    //queryFormPost("tRelacionGastFinanciamientoRead","id_caso",{async: false });
  	     	consMovimientos();
	  		FiltroRetenciones();
			copiaFactura();
			
			consComprobatoria();			
			if (<%=id_oper == 2%> && $("#cOrigen").val() == "" ){
				$(".paso03").attr('disabled', true);
			}else{
				//dtRecalcRetencionEP
				//$("#mImportePenalizacion").attr('disabled', false);
				$("#dtRecalcRetencionEP").dataTable().fnClearTable();
				$("#dtRecalcRetencionEP").dataTable({
					bAutoWidth : true,
					sScrollX: "100%",
					sScrollY: "300",
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay datos en la tabla",
						sLoadingRecords: "Cargando...",
						sInfo: "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty: "Registro 0 al 0 de 0",
						sInfoFiltered: "(filtado de _MAX_ registros)",
						sInfoPostFix: "",
						sInfoThousands: ",",
						sSearch: "Buscar:",
						oPaginate: {
							sFirst:    "Primero",
							sPrevious: "Ant.",
							sNext:     "Sigte.",
							sLast:     "&Uacute;ltimo"
						}
					},
					bRetrive: true,
					bDestroy: true,
					bLengthChange: false,
	   				bFilter: false,
					bInfo: false,
					bServerSide: true,
					bProcessing: true,
					sPaginate: false,
					bJQueryUI: true,						
		   			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vPagoDirectoDetalle&qw=nFolioPagodirecto=" + $("#id_caso").val() ,
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{ sName: "nDocRenglon" },
						{ sName: "mImporteBruto" }
					]
					});
			}
		}else{
  			$(".pasoDos").show();
    		$("#numPaso").val("2");
    		$("#LNK02").click();
   		}
	}else{
   		$(".pasoDos").hide();
      	$(".pasoTres").hide();
    	$(".pasoCuatro").hide();
       	$(".pasoCinco").hide();
	}
}


	$(function() {
		$( "#fElegibilidad" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
	});
	
	/*$(function() {
		$( "#Fecha_Pago" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
	});

	$(function() {
		$( "#FechaAplicacion" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
	});*/



	$(function() {
		$( "#fRecepcion" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
	});
	
function ponCeros(){
	$("#DCD_IVADES").val("0");
	$("#DCD_IMP_BRUTO").val("0");
	$("#DCD_IVA").val("0");
	$("#DCD_ISR").val("0");
	$("#DCD_MIL5").val("0");
	$("#DCD_MIL2").val("0");
	$("#DCD_CONTRIBUCION").val("0");
	$("#DCD_PENALIZACION").val("0");
	$("#DCD_OTRAS_RET").val("0");
	$("#DCD_SANCION").val("0");
	$("#DCD_DEVOL").val("0");
	$("#DCD_AMORT").val("0");
	$("#DCD_RETENCION").val("0");
	$("#DCD_NETO").val("0");
	$("#DCD_AMORT").val("0");
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
	
}

function cambioss(){
	var vEp = $("#EP").val();
	var vCartera = vEp.substring(44, 55);
	var vCapitulo = vEp.substring(31, 32);
	vTipoOperacion = $("#cIdTipoOperacion").val();
	
	if(vCartera == "00000000000" || "1234".indexOf(vCapitulo) >= 0 || ("6".indexOf(vTipoOperacion) >= 0 && "6".indexOf(vCapitulo) >= 0)){
		cambioMovmientos();
	}else{
		$( "#dialog-validaOLI" ).dialog( "open" );
	}
}
	


function cambioMovmientos(){
	
	$("#mMovimiento").val( sinFormatoMonetario($("#mMovimiento").val() ) );
	
	var mTotalEjercer = Number( $("#Acumulado_Op").val() ) + Number( $("#mMovimiento").val() ) ;
	mTotalEjercer = mTotalEjercer.toFixed(2);
	if ( Number( mTotalEjercer ) > Number( $("#Imp_Ejercer").val() ) ){
		alert("El Importe Excede el Total a Ejercer");
		return;
	}

	var vOGT = $("#EP").val();
	vOGT = vOGT.substring(31, 36);
	$("#vOGT").val( vOGT );

	queryFormPost("tMsgPartidaRead", {async: false });
		
	if( $("#obs").val() != '' ){
		alert($("#obs").val());
	}
	
    var szWhere = "";
    var elMonto = "";
	var campos = "";
	var oTablD = $('#grdCompromisos').dataTable();
	oTablD.fnClearTable();

	//campos = "2012, "+$("#fRecepcion").val().split("/")[1]+ ", '" + $("#EP").val()+"'";
	campos = $("#aEjercicioFiscal").val() + ", "+$("#FechaAplicacion").val().split("/")[1]+ ", '" + $("#EP").val()+"'";
	szWhere = " clavesiaff =substring('" + $("#EP").val() + "',1,55)  ";
    var szTabla = "SALDOS_DISPONIBLE_PAGO";
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto,Campos:campos, Order:" order by 1 desc ", ajax: 'false'}, function(j){
		var acumulado = 0.00;
		for (var i = 0; i < j.length; i++) {
		
		   acumulado = parseFloat(acumulado) + parseFloat(j[i].Col2);
		   acumulado = acumulado.toFixed(2);
		   $("#TOTALSUBCUENTA").val(acumulado);
        }
       
		queryFormPost("LeerMontoPDIR_EP_Read", {async : false});
	   	acumulado = parseFloat(acumulado) - parseFloat( $("#montoprevio").val() );
	   	acumulado = acumulado.toFixed(2);

		if (parseFloat(acumulado) < parseFloat($("#mMovimiento").val())){
			alert('La cuenta no tiene suficiente saldo disponible');
			return;
		}
		
		var resto = parseFloat($("#mMovimiento").val());
		var aplicar = 0.0;

		generar();
		for (var i = 0; i < j.length; i++) {
			$("#nMes").val( j[i].Col0 );
			queryFormPost("LeerMontoPDIR_EPMes_Read", {async : false});
			var SuficMes = parseFloat(j[i].Col2) - parseFloat( $("#montoprevio").val() );
			SuficMes = SuficMes.toFixed(2);
			if (SuficMes > 0){
				if (resto - SuficMes > 0){
					aplicar = SuficMes;
				}
				else{
			       aplicar = resto;
				}
			    //aplicar = parseFloat(aplicar.toFixed(2));
				resto = resto - aplicar;
				resto = parseFloat(resto.toFixed(2));
				if (aplicar > 0.0) {
	            	fnClickAddRowxMesComp(j[i].Col0, j[i].Col1,aplicar);
			    }
		    }
		}
		//$("#btAgregaEP").attr('disabled', false);//URVP 1306204
		var vOGT = $("#EP").val();
		vOGT = vOGT.substring(31, 36);
		$("#vOGT").val( vOGT );
		
		if ($("#tConcepto").val() != "AL"){
			$("#partida").val("");
			queryFormPost("BuscaPartidaExcepRead", {async : false});
		
			if ( vOGT == $("#partida").val() ){
				querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
				$("#altaAlmacen").val("");
				$("#altaAlmacen").show();
				$("#cAnioFactEP").val("");
				$("#nFacturaEP").val("");
				$("#cAnioFactEP").show(); 
				$("#nFacturaEP").show();
	
			} else{
				$("#altaAlmacen").val("0");
				$("#altaAlmacen").hide();
				$("#cAnioFactEP").val("");
				$("#nFacturaEP").val("");
				$("#cAnioFactEP").hide(); 
				$("#nFacturaEP").hide();
	
				querySelectPost("CatalogoAlmacenVacioRead","ALM", {async: false });
			}
		}

	});
}

function fnClickAddRowxMesComp(A, B, C) {
	$('#grdCompromisos').dataTable().fnAddData( [
		A, B, C] );
}


function consMovimientos(){
   // var szWhere = " and nfoliocontratodiversofactura = 10 ";
    var szWhere = "";
    var elMonto = " nfolioPagoDirecto = " + $("#id_caso").val();
	var contar=0;
	var acumOper = 0.0;
	var szTabla = "TPAGODIRECTOFACTURADETALLEREAD";
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
    			for (var i = 0; i < j.length; i++) {
					$(".pasoCuatro").show();
			    	$("#numPaso").val("4");
					//$(".paso03").attr('disabled', true);
				    acumOper = parseFloat(acumOper) + parseFloat(j[i].Col5);
				    var mImporteEP = Number(j[i].Col5);
				    $("#mMovimiento").val( mImporteEP );
				    //$("#mMovimiento").formatCurrency(); //URVP.09062014- se comentariza ya que al cargar los datos y eliminar o agregar otra EP existia problemas en la conversion de varchar a numerico para hacer la operacion(Al cargar no trae $)
					fnClickAddRowTable(j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, $("#mMovimiento").val());
					$("#mMovimiento").val('');
					if($("#miOper").val()=="3"){
						contar=contar+1;
						$("#cllave").val(contar);
					}
					calculaIVA(1, j[i].Col5, 1);
					actVariablestmp();
				}
				$("#Acumulado_Op").val(acumOper.toFixed(2));
				if (parseFloat($("#Acumulado_Op").val()) - parseFloat($("#Imp_Ejercer").val()) >= -0.02 && parseFloat($("#Acumulado_Op").val()) - parseFloat($("#Imp_Ejercer").val()) <= 0.02){			
					$(".paso4").show();
			    	$("#numPaso").val("4");
					$(".paso03").attr('disabled', true);
					$("#btAgregaEP").attr('disabled', true);//URVP 1306204
					$("#nIdClaveEgresos2").attr('disabled', true);
			    }
			    
			    //URVP.29082014 Se valida el importe pendiente de movimientos, si queda remanente por capturar se regresa al passo 3 y se oculta la pestana de las retenciones
			    if (parseFloat($("#Imp_Ejercer").val()) - parseFloat($("#Acumulado_Op").val()) !=0 && parseFloat($("#Imp_Ejercer").val())!=0){
			    	$(".pasoCuatro").hide();
			    	$(".paso3").show();
			    	$("#numPaso").val("3");
			    	$("#btAgregaEP").attr('disabled', false);
					$("#nIdClaveEgresos2").attr('disabled', false);
			    }else if (parseFloat($("#Imp_Ejercer").val()) - parseFloat($("#Acumulado_Op").val()) == 0 && parseFloat($("#Imp_Ejercer").val())!=0){
			    	$("#btAgregaEP").attr('disabled', true);
			    	$(".paso03").attr('disabled', true);
					$("#nIdClaveEgresos2").attr('disabled', true);
					$("#grdMovimientos").attr('disabled', true);//URVP
			    }
	});
}

function consComprobatoria(){
   // var szWhere = " and nfoliocontratodiversofactura = 10 ";
    var szWhere = " caNoContrarrecibo = '"+$("#caNoContrarrecibo").val()+"' ";
    var elMonto = "";
	var acumOper = 0.0;
	var szTabla = "TDOCUMENTACIONCOMPROBATORIADETREAD";
	
	$("#DCD_IMP_BRUTO").val($("#Imp_Bruto").val());		//j[i].Col6
	$("#DCD_IVADES").val( $("#Imp_Iva").val());			//j[i].Col7
	$("#DCD_NETO").val(  $("#Imp_Neto").val() );
	$("#DCD_ISR").val($("#mISRHonorarios").val());		//;j[i].Col9
	$("#DCD_MIL5").val($("#m5Millar").val());			//j[i].Col10
	$("#DCD_MIL2").val($("#m2Millar").val());			//j[i].Col11
	$("#DCD_IVA").val( $("#m23IVA").val() );			//j[i].Col8
	$("#DCD_OTRAS_RET").val($("#mCedular").val());		//j[i].Col13
	$("#DCD_PENALIZACION").val($("#mPenalizacion").val());	//j[i].Col14
	$("#DCD_CONTRIBUCION").val($("#mFletes").val());		//j[i].Col12
	
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
		for (var i = 0; i < j.length; i++) {
		    acumOper=parseFloat(acumOper)+ parseFloat(j[i].Col6);
			fnClickAddRowComprobatoria(j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3,j[i].Col4,j[i].Col5,j[i].Col6,j[i].Col7,j[i].Col8,j[i].Col9,j[i].Col10,j[i].Col11,j[i].Col12,j[i].Col13,j[i].Col14);		
		}
		if ((parseFloat(acumOper) - parseFloat($("#Imp_Bruto").val())) > -0.02 && (parseFloat(acumOper) - parseFloat($("#Imp_Bruto").val())) < 0.02 ){
			$(".pasoCinco").show();
	    	$("#numPaso").val("6");
			$(".paso04").attr('disabled', true); //URVP.05112014 En caso de ya tener la documentacion y no traer retencion, se deshabilita las opciones de retenciones ya que le pago ES sin retencion	    	
	    	$(".paso05").attr('disabled', true);
			$(".paso055").attr('disabled', true);
			
			$("#divImprime").hide();
	
	 		szWhere = " nfolioPagoDirecto = "+$("#id_caso").val()+" ";
	 		elMonto = "";
			$("#laPoliza").val("");
			$("#docAplicado").val("");
			var szTabla = "TPAGODIRECTODOCAPLICADOREAD";
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
				for (var i = 0; i < j.length; i++) {
					$("#laPoliza").val(j[i].Col0);
					$("#docAplicado").val(j[i].Col1);
				}
			});
		}
	});
	setTimeout("pausaAplic()",100);
}

	
function BuscaPoliza(){
	var szWhere = " nfolioPagoDirecto = " + $("#id_caso").val();
	var elMonto = "1";
	$("#laPoliza").val("");
	$("#docAplicado").val("");
	var szTabla = "TPAGODIRECTODOCAPLICADOREAD";
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
		for (var i = 0; i < j.length; i++) {
			$("#laPoliza").val(j[i].Col0);
			$("#docAplicado").val(j[i].Col1);
		}
	});
}	


function pausaAplic(){
	if($("#docAplicado").val() == "S"){
		
		if($("#miOper").val()=="1" && $("#caNoContrarrecibo").val() != ""){
				//aplicacion contable
				parent.document.getElementById("pb_save").disabled=false;
				parent.document.getElementById("pb_send").disabled=true;
				$("#divImprimePoliza").hide();
				$("#divImprimeAnexo").hide();
				$("#numPaso").val("5");
				
			}
									
			if($("#miOper").val()=="2"){
				//parent.document.getElementById("pb_save").disabled=true;
				//parent.document.getElementById("pb_send").disabled=false;
				$("#divImprimePoliza").hide();//****
				$("#divImprimeAnexo").hide();
				$("#divAutorizar").show();
				$("#numPaso").val("6");
						
			}
			if($("#miOper").val()=="3" ){
				$("#divImprimePoliza").show();
				$("#divImprimeAnexo").show();
				$("#divImprime").show();
				$("#cancelar").hide();				
			}
	}else{
		informacionDocumento();		
	}
					///}
}

function informacionDocumento(){
 
  /** Informacion que se Perdia al Cambiar de Pestana Documento a Adjunto **/
  
 // Combos concepto
 
 $("#TIPO_CONCEPTO").val($("#tConcepto").val());
	//querySelectPost("CatalogoObraTMovimendoRead","tmovimiento", {async: false }); //se comentariza y se agrega el sig queryselectpost para dejar por defaulta seleccionado Pago Normal
	querySelectPost({
		queryName: 'CatalogoObraTMovimendoRead', 
		targetObjectId: 'tmovimiento', 
		async: false
		//callback:function(){
			//seleccionaPorValor("","");
			//$("#tmovimiento").attr("disabled","disabled");
		//}
	});
	//$("#tmovimiento").attr('disabled', true); //URVP.10092014 SE DESHABILITA EL COMBO DE TIPO DE MOVIMIENTO
	if ($("#tConcepto").val() == "AL"){
		querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
		$("#altaAlmacen").val("");
		$("#altaAlmacen").show();
		$("#cAnioFactEP").val("");
		$("#nFacturaEP").val("");
		$("#cAnioFactEP").show(); 
		$("#nFacturaEP").show();
	}
	else{
		$("#altaAlmacen").val("0");
		$("#altaAlmacen").hide();
		$("#cAnioFactEP").val("");
		$("#nFacturaEP").val("");
		$("#cAnioFactEP").hide(); 
		$("#nFacturaEP").hide();
		querySelectPost("CatalogoAlmacenVacioRead","ALM", {async: false });
	}

	 // financiamiento
	 if($("#numPaso").val() > 3 ) { //== "3" || $("#numPaso").val() == "6"){ //URVP.29082014 SI EL PASO ES MAYOR A 3 SE DESHABILITA EL COMBO DE FUENTE DE FINANCIAMIENTO
	 	$("#TFONDO").attr('disabled', true);
	 }
	 
	 // numPaso a 5 Para que pueda seguir con el proceso
	 if($("#numPaso").val() == "6" && $.trim($("#docAplicado").val()) == ""){
	 	$("#numPaso").val("5");
	 }
	 	 
	 // Se perdia valor y se quedaba en 0
	 $("#cIdTipoDocumento").val("5");
 	 $("#DCD_CONCEPTO").attr('disabled', true); 	 
	
}

function cmdBorrar(){
	if ($("#docAplicado").val() == "S") {
		alert("Documento Aplicado, No es posible Borrar el Pago Directo");
		return;
	}

	$("#elcontra").val($("#caNoContrarrecibo").val());
	//queryFormPost("TDOCUMENTACIONCOMPROBATORIADET_OBRAFolioDelete,tPagoDirectoFinanciamientoxFolioDelete,tpagodirectodetalleRetencion,TPAGODIRECTOdetallexFolioDelete,tcontrarreciboPagoDirectoxFolioDelete,TPAGODIRECTOencabezadoxFolioDelete", {async: false });
	parent.document.getElementById("pb_save").disabled=true;
	parent.document.getElementById("pb_send").disabled=true;
	parent.document.getElementById("pb_cancel").disabled=false;
	parent.document.getElementById("pb_cancel").click();
	parent.document.getElementById("pb_cancel").disabled=true;
	
	if ($("#docAplicado").val() != "S") 
		queryFormPost("borraFactRelacionPagoBorrado", {async: false }); //URVP.10092014 ELIMINA LAS FACTURAS CAPTURADAS EN EL PAGO A BORRAR PARA PODER REGISTRARLAS NUEVAMENTE EN OTRO PAGO
	
//	self.location="../caso/principal.jsp";
}


function cmdRegresar(){
	self.location="../caso/inbox.jsp";
}

			
function setSequenceValCT(seqValue) {
	seqValue = "000000" + seqValue;
	seqValue = seqValue.substr(seqValue.length - 6);
	seqValue = $("#cCentroContable").val() + "CT" + $("#aEjercicioFiscal").val() + seqValue;
	$("#caNoContrarrecibo").val( seqValue );
	$("#elcontra").val( seqValue );
}
			
function setSequenceVal(seqValue) {
	seqValue = "000000" + seqValue;
	seqValue = seqValue.substr(seqValue.length - 6);
	seqValue = "1" + seqValue.substr(seqValue.length - 5);
	seqValue = $("#cCentroContable").val() + cxpPrefijo + $("#aEjercicioFiscal").val() + seqValue;
	$("#caNoContrarrecibo").val( seqValue );
	$("#elcontra").val( seqValue );
}
			
function cano(){
	
	if(<%=id_oper == 1%>){
		$("#cDescripcionPoliza").val( "Apartado del pago: " + $("#caNoContrarrecibo").val() );
	   	$("#FechaAplAptd").val( $("#FechaAplicacion").val() );
	   	queryFormPost("tPagoApartadoEncCreate", {async: false });
	
		queryFormPost("SIG_FOLIO_CONTRARRECIBOPAGODIRECTOUpdate", {async: false });
	}
	

	elParametro = "'"+$("#elcontra").val()+"', 1, '0', '0', '"+$("#DCD_IMP_BRUTO").val()+"', '"+$("#DCD_DEVOL").val()+"', '"+$("#DCD_SANCION").val()+"', '"+$("#DCD_AMORT").val()+"', '"+$("#DCD_IVA").val()+"','"+$("#DCD_RETENCION").val()+"', '"+$("#DCD_PENALIZACION").val()+"', '"+$("#DCD_NETO").val()+"', '"+$("#DCD_FECHA_FACTURA").val()+"','"+$("#cUnidadResponsable2").val()+"'," + $("#aEjercicioFiscal").val() + ",'"+$("#No_Folio").val()+"', '0','0','"+$("#cIDRFC").val()+"'";
	alert("Movimiento actualizado!");
	quitaFormato();
	elParametro = "'"+$("#caNoContrarrecibo").val()+"', 1, '0', '0', '"+$("#DCD_IMP_BRUTO").val()+"', '"+$("#DCD_DEVOL").val()+"', '"+$("#DCD_SANCION").val()+"', '"+$("#DCD_AMORT").val()+"', '"+$("#DCD_IVADES").val()+"','"+$("#DCD_RETENCION").val()+"', '"+$("#DCD_PENALIZACION").val()+"', '"+$("#DCD_NETO").val()+"', '"+$("#DCD_FECHA_FACTURA").val()+"','" + $("#cCentroContable").val() +  "'," + $("#aEjercicioFiscal").val() + ",'"+$("#No_Folio").val()+"', '"+$("#cIdTipoDocumento").val()+"','0','"+$("#cIDRFC").val()+"'," +  $("#otrosImpuestos").val() ;

	$.getJSON("../catalogos/InsertJson.jsp",{Tabla: "TCONTRARECIBODIRECTOCREATE", Param: elParametro, MaxReg: "", ajax: 'false'}, function(j){});

		var elParametro = "         '"+$("#fRecepcion").val()+"',16,1,'"+$("#caNoContrarrecibo").val()+"'," + $("#aEjercicioFiscal").val() + ",10,'"+$("#DCD_FACTURA").val()+"','"+$("#DCD_FECHA_FACTURA").val()+"'";
 		elParametro += ",'"+$("#DCD_TBEN").val()+"','"+$("#DCD_CBEN").val()+"','"+$("#DCD_TIPO_OPE").val()+"','"+$("#DESCRIPCION20").val()
 		+"','" + (  parseFloat( quitaFmt( $("#DCD_IMP_BRUTO").val() )  )  + parseFloat(  quitaFmt( $("#DCD_OTROS_IMP").val()  ) )   ) + "'";
 		elParametro += ",'"+$("#DCD_IVADES").val()+"','"+$("#DCD_IVA").val()+"','"+$("#DCD_ISR").val()+"','"+$("#DCD_MIL5").val()+"','"+$("#DCD_MIL2").val()+"','"+$("#DCD_CONTRIBUCION").val()+"','"+$("#DCD_OTRAS_RET").val()+"'";
 		elParametro += ",'"+$("#DCD_PENALIZACION").val()+"','"+$("#DCD_CONCEPTO").val()+"'";
 		
 		$.getJSON("../catalogos/InsertJson.jsp",{Tabla: "TDOCUMENTACIONCOMPROBATORIADETCREATE", Param: elParametro, MaxReg: "", ajax: 'false'}, function(j){
 	});
 	
 	//URVP.30092014 Se actualiza importe neto y total de retencion en contrarecibo
 	queryFormPost("updateRetencionesPagoDirectoContrarrecibo", {async: false });

}			
			
			
			
function copiaFactura(){
	$("#DCD_FECHA_FACTURA").val($("#Fecha_Pago").val());
	$("#DCD_CONCEPTO").val($("#Concepto").val());
	$("#DCD_FACTURA").val($("#No_Folio").val());
	$("#DCD_TIPO_OPE").val($("#cIdTipoOperacion").val());
	$("#DCD_TIPO_OPE").attr('disabled', true);
	$("#DESCRIPCION20").find("option:contains('" + $('#por_Iva option:selected').text() + "')").attr("selected","selected");
	$("#DESCRIPCION20").attr('disabled', true);
}



function habilitaGuardar(elPar){
	//parent.document.getElementById("pb_save").disabled=false;
	if (elPar == 0){
		$("#grpAutorizar").val("No");
	}
	else{
		$("#grpAutorizar").val("Si");
		$("#numPaso").val("6");
	}
}


function habilitaMillar2(elPar){
	if (elPar == 0){
		$("#aux").val(elPar);
	}else{
		$("#aux").val(elPar);
	}
}


function cmdImprimir(elFormato){
	var swhere = "&folio=" + $("#caNoContrarrecibo").val();
	if ($("#docAplicado").val() == "C" && $("#caNoContrarrecibo").val().substring(0,4) == $("#cCentroContable").val() + "CT" ) {
		var vUnidad = $("#cUnidadResponsable").val();
		vUnidad = vUnidad.substring(0,3);
		$("#noFolio").val( "PDIR-" + vUnidad + "-" + $("#id_caso").val() );
								     			
		cmdImprimirRCH();
		return;
	}

	if(elFormato == "ComprobanteRegistro" || elFormato == "NuevoContrarecibo"){
		swhere = "&whereFolio= and CR.caNocontrarrecibo = '" + $("#caNoContrarrecibo").val() + "'";
	} 
			window.open(
					"../admin/SeguridadCatalogos?"
						+ "catalogo=CONTRARECIBO"
						+ "&accion=run"
						+ "&rn=" + elFormato + ".jasper"
						+ swhere,
						//+ "&nombre="   + ""
						//+ "&cargo="    + ""
						//+ "&area="     + "",
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768");
					
		//$("#anex").val(elFormato);		
		setTimeout('anexo("' + elFormato + '")', 3000);	
					
					
}
function anexo( pfmt ){
	$("#totFacturas").val(0);
	queryFormPost("totFacturasPago", {async: false });
	
	if (($("#cllave").val()>7 || $("#totFacturas").val()>7) && pfmt == "PolizaPago"){  //se cambia a EP's mayor a 7 o mas de 7 facturas
		window.open("../admin/SeguridadCatalogos?"
			+ "catalogo=ANEXO"
			+ "&accion=run"
			+ "&rn=Anexo1.jasper"
			+ "&swhere=  and caNoContrarrecibo ='" + $("#caNoContrarrecibo").val()+"'" ,
			"Anexo",
			"scrollbars=1, resizable=yes, width=1024, height=768");
	}
}


var cont=0;
function fnAgregarRet() {
	if ($("#cIdTipoRetencion").val()=="9" && $("#cCentroContable").val()!="21" && $("#cCentroContable").val()!="28"){
		alert("El Impuesto Cedular no aplica para tu Centro Contable.");
		return;
	}
	quitaFormato();
	var table = document.getElementById('grdRetencion');
    var rowCount = table.rows.length;
    var yaExiste = 0;
	var nu2=0;
    try{
		for(var i=0; i<rowCount; i++) {
	        var row = table.rows[i];
	        var chkbox = '';
			try
			{
			  var chkbox = row.cells[0].childNodes[0];
			}
			catch(e) {
	        	null;
	        }
			if(null != chkbox ) {
				if(chkbox.toString() == $("#cIdTipoRetencion").val()){
	        		yaExiste=1;
				}	
	        }	
	    }
    }
    catch(e) {
       alert(e);
    }
    
	if (yaExiste == 1){
		alert("Ya se ingres\u00F3 una retenci\u00F3n con esa clave");
		return;
	}

	queryFormPost("CatalogoTipoRetencionObtenDetRead", {async: false });
	queryFormPost("tPagoDirectoRetencion", {async: false });

	//URVP.25112014 SI ES CC DE GUANAJUATO Y RETENCION DE IMPUESTO CEDULAR SE CAMBIA A 0.015%
	if ($("#cIdTipoRetencion").val()=="9" && $("#cCentroContable").val()=="28"){
		var porcenCedular = 0.00;
		porcenCedular = Number($("#nPorcRetencion").val());
		porcenCedular = porcenCedular + 0.005;
		$("#nPorcRetencion").val(Number(porcenCedular).toFixed(18));
	}

	fnClickAddRowB($("#cIdTipoRetencion").val(), $("#cTipoRetencion").val(), $("#nPorcRetencion").val() );

	calculaIVA(1,$("#Imp_Bruto").val());
    actVariablestmp();
	actualSaldos(1);
	
	//$("#Imp_Iva").val(Math.round(mImporteIVA*100)/100);
	$("#Imp_Retenciones").val(Math.round(mImporteRetencion2*100)/100);
	$("#Imp_Ejercer").val(Math.round((mImporteNeto + mImporteRetencion2 + mImportePenalizacion)*100)/100);
	//$("#Imp_Neto").val(Math.round(mImporteNeto*100)/100);

	//Actulizo en base de datos las neuvas retenciones
	$("#elcontra").val($("#caNoContrarrecibo").val());
   	$("#elrfc").val($("#cIDRFC").val());
	$(".paso01").attr('disabled', false);
	$(".subtotall").attr('disabled', false);
	quitaFormato();
	$("#ctaBancaria").attr('disabled', false); //URVP.12112014 Se habilita el combo de l cuenta bancaria para leer el dato y se deshabilita nuevamente dos lineas abajo
	queryFormPost("TPAGODIRECTOencabezadoxFolioDelete,tPagoDirectoFacturaCreate", {async: false });
	$("#ctaBancaria").attr('disabled', true);
	queryFormPost("tPagoDirectoEncabezadoUpdate",{async: false });
	queryFormPost("updateEncabezadoDirectoIvaNeto",{async: false });
	$(".paso01").attr('disabled', true);
	$(".subtotall").attr('disabled', true);
	ponFormato();
						
	if ( $("#cIdTipoRetencion").val() == 2){
		$("#divMilla2").show();
	}else{
		$("#divMilla2").hide();
		//$("#aux").val(2);
	}
	if (<%=id_oper == 2%> && $("#cOrigen").val() != "" ){
		RecalcRetencionEP();
	}
}

function RecalcRetencionEP(){
	var oTblDetEP = $("#dtRecalcRetencionEP").dataTable();
	var aData = oTblDetEP.fnGetData();
	
	$("#elcontra").val( $("#caNoContrarrecibo").val() );
	
	queryFormPost("tDocumentacionComprobatoriaDetDelete,tContrarreciboDelete", {async: false });
	for (var i = 0; i < aData.length; i++) {
		calculaIVA(1, aData[i][1], 1);								
	  	actVariablestmp();
		actualSaldos(1);
		actualSaldosDet(1);
		if(i == aData.length)
			AjustaRetencionesD();
		$("#nDocRenglon").val( aData[i][0] );
		$("#mIMDT").val("0");
		$("#mCNIC").val("0");

		if (  $("#aux").val() != 2    ){
			if (  $("#aux").val() == 0) {
				$("#mCNIC").val($("#m2Millar").val());
				$("#mIMDT").val("0");		
			}else if ( $("#aux").val() == 1){
				$("#mCNIC").val("0");
				$("#mIMDT").val($("#m2Millar").val());		
			}
		}

		queryFormPost("tPagoDirectoDetalleUpdate", {async: false });
	}
	cano();
}

function FiltroRetenciones(){
	var table = document.getElementById('grdRetencion');
	var rowCount = table.rows.length;

	if (yaSeLleno == 1){
    	return;
	}
	if ($("#cIDContrato").val()!=""){
		var szWhere = "";
		szWhere = " nFolioPagoDirecto ='"+ $("#id_caso").val()+"'";
        var szTabla = "CALC_RETENCIONES_FACT_PAGO_DIRECTO";
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: "", ajax: 'false'}, function(j){
      		var options = '';
			for (var i = 0; i < j.length; i++) {
				$(".pasoCuatro").show();
   				$("#numPaso").val("4");
				//$(".paso03").attr('disabled', true);
				//URVP.27112014 Se valida si es impuesto cedular y nayarit, se cambia el porcentaje de la retencion a 1.5%
				if (j[i].Col0=="9" && $("#cCentroContable").val()=="28"){
					var porcentCedular = 0.00;
					porcentCedular = Number(Number(j[i].Col2) + 0.005).toFixed(6);
					fnClickAddRowB(j[i].Col0, j[i].Col1, porcentCedular);
				}
				else{
					fnClickAddRowB(j[i].Col0, j[i].Col1, j[i].Col2);
				}
            }
            if (j.length>0){
				$("#cIdTipoRetencion").attr('disabled', true);
     			$("#btAgregaMov").attr('disabled', true);
            }
     		yaSeLleno = 1;
		});
	}
}

function fnClickAddRowB(A, B, C) {
				$('#grdRetencion').dataTable().fnAddData( [
					A,
					B,
					C
					] );
			}
			
function cambio(){
	if ($("#TFONDO").val() == "CE"){
		if ($("#Prestamo").val()!=null){
 			cambio2();
 		}
	}
}

function cambio2(){
	if ($("#Prestamo").val()!=null){
	 queryFormPost("PrestamoCategoriaInversion2Read",{async: false });
	}

	$("#vcInversion").val($('#nPorcentajeFinanciamiento').val());
	if(Number($("#nPorcentajeFinanciamiento").val())==100){
		$("#contraparte").val(0);
	}else{
		$("#contraparte").val( 100-Number($("#nPorcentajeFinanciamiento").val()));
	}
}


function tfondos(){
	if ($('#TFONDO option:selected').val()!="CE"){
		$(".paso02").hide();
	}else{
		$(".paso02").show();
		cambio();
	}
}

function seleccionaPorValor(idSel, valor){
	var valor = $("#tmovimiento option[text='Pago Normal']").attr("selected","selected");
}

function cmdGuardar(){
	quitaFormato();
	var hayError='';
	var msgAut = "Operaci\u00F3n realizada.";
		
	// 6 Consulta
	if ($("#numPaso").val() == '6'){
		aplicaContableExitosa();
	}
	
	// 5 Aplicacion Exitosa para el apartado  / Devengado al autorizar
	if ($("#numPaso").val() == '5'){
		//$("#btncIDRFC").attr('disabled', true);//URVP
		$("#campo").val( "nFolioPagoApartado" );
		$("#tablaEnc").val( "tPagoApartadoEncabezado" );
		$("#campoCondicion").val( "caNoContrarrecibo" ); 
		$("#tablaDet").val( "tPagoApartadoDetalle" ); 
		$("#tipoAplicar").val( "PAGOAPARTADO" );
		
		if(!validarRetencionesPago()){
			parent.document.getElementById("pb_save").disabled=true;
			return;			
		}else{		
			if ( procesar() ){
				$("#divImprime").show();
				$("#cancelar").hide();
				parent.document.getElementById("pb_save").disabled=true;
			}
		}
	}
	
	// 4 Ingresa movimientos numPaso = 5, muestra pasoCinco $("#LNK05").click();
	
	if ($("#numPaso").val() == '4'){
		$("TFONDO").attr('disable', true);
		if ( $("#PagoAMF").val() != "" && $("#Imp_Neto").val() != "0"){
			if ( Number( $("#Imp_Neto").val() ) != Number( $("#PagoAMF").val() ) ){
				$("#Imp_Bruto").val(0);
				alert("Importe Neto es diferente al Importe AMF");
				calculaIVA(1,$("#Imp_Bruto").val());
				$("#Imp_Bruto").attr('disabled', false);
				return -1;
			}
		}		
		
		$("#cIdTipoRetencion").attr('disabled', true);//URVP
		$("#btAgregaMov").attr('disabled', true);//URVP
		$("#grdRetencion").attr('disabled', true);//URVP
		
		queryFormPost("updateRetencionesPagoDirecto", {async: false }); //URVP Se actualizan las retenciones en el detalle, debido al cambio de orden de pestanas
		queryFormPost("updateRetencionesPagoDirectoEncabezado", {async: false }); //URVP Se actualizan las retenciones en los encabezados
		queryFormPost("readDocumentacionPagoDirecto", {async: false }); //URVP Se actualizan los campos de la documentacion de acuerdo a los update anteriores
		
		if(!validarImporteFactura()){
			return;
		}
		
		alert("Retenciones guardadas!");		
		$("#ivadesgloceinicial").val($("#DCD_IVADES").val());

		$("#numPaso").val("5");
		$(".pasoCinco").show();
		$("#LNK05").click();
		parent.document.getElementById("pb_save").disabled=true;
		//$(".paso05").attr('disabled', true); urvp28082014
		
		copiaFactura();
		$("#DESCRIPCION20").attr('disabled', true);
	 	$("#DCD_TIPO_OPE").attr('disabled', true);
		$("#DCD_CONCEPTO").attr('disabled', true);
	}
	
	// 3 Guardar Retenciones numPaso = '3' muestra pasoTres $("#LNK03").click();
	
	if ($("#numPaso").val() == '3'){
		if (Number($("#Acumulado_Op").val()) != Number($("#Imp_Ejercer").val()))
		{
			alert ("El Total Acumulado no coincide con el Total a Ejercer");
			return;
		}
		//$("#btncIDRFC").attr('disabled', true);//URVP
		if ( $("#DESTINO_GASTO").val() == "AL" ){	
			var aData = oTablevFact.fnGetData();
			for(var i=0; i<aData.length; i++) {
				var cFactura = aData[ i ][ 1 ];
				var cImporte = quitaFmt( aData[ i ][ 2 ] );
				$("#cValidaFactura").val( cFactura );
				$("#mImporteBrutoF").val( cImporte );
				queryFormPost("tPagoFacturaCreate", { async: false});
			}
		}
		
		$("#TFONDO").attr('disabled', true);//URVP
		
		$("#nIdClaveEgresos2").attr('disabled', true);//URVP
		$("#grdMovimientos").attr('disabled', true);//URVP
		$("#mMovimiento").attr('readOnly', true);//URVP
		alert("Movimientos Guardados!");
		$(".paso03").attr('disabled', true);
		$("#numPaso").val("4");
		$(".pasoCuatro").show();
		
		var actualizados = actualizaRetenciones();
		if( actualizados ){
			$("#LNK04").click();
			$("#nTotalRetenciones").val("0");
			
			queryFormPost({
				queryName: 'facturasConRetencionesRead', 
				async: false,
				callback:function(){
					if( parseInt( $("#nTotalRetenciones").val() , 10 ) > 0 ){
						$("#listaRetenciones").val("");
						queryFormPost("listaFacturaRead", {async:false});
						alert( "Se encontraron las siguientes retenciones en las facturas cargadas: " + $("#listaRetenciones").val() + " Por favor revisar que la informacion es congruente." );
					}
				}
			});
		}else
			return false;
		
		retencionesObligatorias();
		
	}
	
	// 2 Fuente de Financiamiento numPaso = '2' muestra .pasoCuatro, $("#LNK04").click();
	
	if ($("#numPaso").val() == '2'){
		if($("#TFONDO").val() != "CE")
			$(".paso02").hide();
		else{
			if ($.trim($("#oficioElegibilidad").val())==""){
				alert("Favor de capturar el Numero de Oficio de elegibilidad.");
				return;
			}
			if ($.trim($("#fElegibilidad").val())==""){
				alert("Favor de capturar la fecha del Oficio.");
				return;
			}
		}
	     if ($("#TFONDO").val() == "CE"){
	     	$('.paso02').each(function(){
		  		if ($(this).val()==''){
					hayError = hayError + this.name+', ';
				}
	     	});
		}
		
		if (hayError != ""){
			alert('Debe ingresar los siguientes datos: '+ hayError);
			ponFormato();
			return -1;
		} else{
			//   queryFormPost("SIG_FOLIO_CONTRARRECIBODirectoCreate", {async: false });
			$("#elfolio").val($("#No_Folio").val());
			$(".paso02").show();
			
			
			quitaFormato();
			queryFormPost("tPagodirectoFinanciamientoCreate",{async: false });
			if ($('#TFONDO option:selected').val()!="CE"){
			    $(".paso02").hide();
			}
			$("#TFONDO").attr('disabled', true);//URVP.09062014
			alert("Fuente de financiamiento guardada!");
			$("#numPaso").val("3");
			$(".pasoTres").show();
			$("#LNK03").click();
			
			//URVP.02092014 SE CARGAN LOS COMBOS DE TIPO CONCEPTO MOVIMIENTO Y ALMACEN, YA QUE EN OCACIONES LOS DEJABA VACIOS
			querySelectPost("CatalogoObraTConceptoRead","tConcepto", {async: false });
			querySelectPost({
				queryName: 'CatalogoObraTMovimendoRead', 
				targetObjectId: 'tmovimiento', 
				async: false
				//callback:function(){
					//seleccionaPorValor("","");
					//$("#tmovimiento").attr("disabled","disabled");
				//}
			});
						
			//$("#tmovimiento").attr('disabled', true); //URVP.10092014 SE DESHABILITA EL COMBO DE TIPO DE MOVIMIENTO
			if ($("#tConcepto").val() == "AL"){
				$("#altaAlmacen").val("");
				$("#altaAlmacen").show();
				$("#cAnioFactEP").val("");
				$("#nFacturaEP").val("");
				$("#cAnioFactEP").show(); 
				$("#nFacturaEP").show();
				querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
			}
			else{
				$("#altaAlmacen").val("0");
				$("#altaAlmacen").hide();
				$("#cAnioFactEP").val("");
				$("#nFacturaEP").val("");
				$("#cAnioFactEP").hide(); 
				$("#nFacturaEP").hide();
				querySelectPost("CatalogoAlmacenVacioRead","ALM", {async: false });
			}
			
			if($("#TFONDO").val() == "CE"){
				$("#Prestamo").attr('disabled',true);
				$("#CInversion").attr('disabled',true);
				$("#oficioElegibilidad").attr('disabled',true);
				$("#fElegibilidad").attr('disabled',true);
				$("#TContrato").attr('disabled',true);
				//$("#contraparte").attr('disabled',true);
			}			
		}
	}
	
	// 1 Guardar Datos Generales e Importes Bruto, muestra .pasoDos, numPaso = '2' $("#LNK02").click();
	
	if ($("#numPaso").val() == '1'){
		
		$("#No_Folio").val($("#id_caso").val());//URVP.22092014 SE AGREGA EL NUMERO DEL FOLIO DADO POR EL SISTEMA AL CAMPO DE NUMERO DE FACTURA, EL CAMPO SE OCULTA PARA NO CAUSAR RUIDO AL USUARIO
	
		if ($("#DESTINO_GASTO option:selected").text()=="- Seleccionar -"){
			alert("Favor de seleccionar un Tipo Destino");
			return;
		}
// 		if (parseInt($("#Imp_Ejercer").val(),10)>22647.00){
// 			alert("No se puede Generar este tipo de pago con importe mayor a $22,647.00");
// 			return;
// 		}
		
		var imp_penalizacion = parseFloat($("#mImportePenalizacion").val());
		var imp_ejercer = parseFloat($("#Imp_Ejercer").val());
		if (imp_penalizacion>imp_ejercer)
		{
			alert("El importe de las penalizaciones debe ser menor o igual al Total por Ejercer.");
			return;
		}
		else if (imp_penalizacion<0)
		{
			alert("El importe de las penalizaciones no debe ser Negativo.");
			return;
		}
		
		
			var aData = oTablevFact.fnGetData();
			if ( $("#DESTINO_GASTO").val() != "AL" && $("#DESTINO_GASTO").val() != ""){	
				if ( aData.length == 0 ){
					alert("Se deben Capturar las facturas");
					return -1;
				}
			}
			
		if ($.trim($("#ctaBancaria option:selected").text())==""){
			alert("El beneficiario " + $("#cIDRFC").val() + " no tiene dada de alta alguna cuenta bancaria, por lo que no se puede proceder con el pago.");
			return;
		}		
		
			
			$("input[id='ID_DESTINO_GASTO']").val($('#DESTINO_GASTO option:selected').val());
			querySelectPost("CatalogoObraTConceptoRead","tConcepto", {async: false });
			$("#TIPO_CONCEPTO").val($("#tConcepto").val());
			//querySelectPost("CatalogoObraTMovimendoRead","tmovimiento", {async: false });
			$("#tConcepto").change();
			$('.subtotall').each(function(){
					  	if ($(this).val()==''){
					  		hayError = hayError + this.name+', ';
				  		}
		     });
		     $('.paso01').each(function(){
		    	 var cnameCol = '';
			  	if ($(this).val()==''){
			  		cnameCol = this.name + ', ';
					if("<%=cUR%>" != "A02" && $( "#cCentroContable" ).val() == "10" && this.name == "Fecha_Pago"){
						cnameCol = "";
					}
					hayError = hayError + cnameCol ;
				}//nuevo
		     });
		     
			  $('.AyudaSyC').each(function(){
			  	if ($(this).val()==''){
					hayError = hayError + this.name+', ';
				}
		     });	 
		     
			 //$("#cIdTipoOperacion").val() == "4" || "67".indexOf(vTipoOperacion) >= 0 
			 vTipoOperacion = $("#cIdTipoOperacion").val();
			 if ("67".indexOf(vTipoOperacion) == -1){
			 	if (parseFloat($("#Imp_Ejercer").val()) > parseFloat($("#montoMaximo").val())){
					alert('El monto m\u00E1ximo para pago directo es:'+ $("#montoMaximo").val());
					ponFormato();
					return -1;
				}
			}
			 if (hayError==''){
				/* Se comentariza para que ya no haga el insert de las facturas debido a que ya se valida y se hace insert de ellas en la carga del xml
				for(var i=0; i<aData.length; i++) {
					var cFactura = aData[ i ][ 0 ] + aData[ i ][ 1 ];
					var cImporte = quitaFmt( aData[ i ][ 2 ] );
					$("#cValidaFactura").val( cFactura );
					$("#mImporteBrutoF").val( cImporte );
					queryFormPost("tPagoFacturaCreate", { async: false});
				}*/
	
				getNextSequenceVal({seqName: "APARTADO", async: false, callback: setSequenceAptd});
	
				$("#elcontra").val($("#caNoContrarrecibo").val());
			   	$("#elrfc").val($("#cIDRFC").val());
				quitaFormato();
			   	queryFormPost("tPagoDirectoFacturaCreate", {async: false });
			   	queryFormPost("tPagoDirectoEncabezadoUpdate",{async: false });
				//$("#btncIDRFC").attr('disabled', true);//URVP
				$("#EditaFacturas").css('visibility', 'hidden');
				$("#ctaBancaria").attr('disabled', true);
	            alert("Car\u00E1tula guardada!");
	            $("#DCD_FACTURA").val($("#No_Folio").val()); //se asigna el campo del folio al input de la factura de documentacion
			  	$(".paso01").attr('disabled', true);
			  	//$(".subtotall").attr('disabled', true); //-----comentarizado para pruebas de cssReadOnly
		    	$(".paso01").attr('disabled', true);
			  	$(".pasoDos").show();
				//FiltroRetenciones();
				//$("#No_Folio").attr('disabled', true); -----comentarizado para pruebas de cssReadOnly
				//---------------------------------------
				$("#No_Folio").attr('disabled', false);
				$("#fRecepcion").attr('disabled', false);
				$("#Fecha_Pago").attr('disabled', false);
				$("#cnombre").attr('disabled', false);
				//$("#mImportePenalizacion").attr('disabled', false);
				$("#por_Iva").attr('disabled', true);
				
				$("#Concepto").attr('readonly', true);
				$("#No_Folio").attr('readonly', true);
				$("#fRecepcion").attr('readonly', true);
				$("#Fecha_Pago").attr('readonly', true);
				$("#cnombre").attr('readonly', true);
				$("#mImportePenalizacion").attr('readonly', true);
				
				cssReadOnly();
				//---------------------------------------
				$("#numPaso").val('2');
				$("#LNK02").click();
			}
			else
			{
				alert('Debe ingresar los siguientes datos: '+ hayError);
				return -1;
			}
   	}
	ponFormato();
	return 0;
}

function retencionesObligatorias(){ //URVP.02092014 
	var partidas = "";
	
	var separador = "";
	var oTableM = $("#grdMovimientos").dataTable();
	var aData = oTableM.fnGetData();
	for(var i=0; i<aData.length; i++) {
		var ep = "";
		ep = aData[i][1];
		ep = ep.substring(31,36);
		partidas += separador + ep;
		separador = ",";
	}
	$("#partidasSeleccionadas").val(partidas); //URVP.03102014 Se anade para tener las partidas en el pago, y hacer validacion al querer eliminar una retencion
	var partidasSeleccionadas = "";
	var cIDRFC = "";
	var cContable = "";
	var folio = "";
	partidasSeleccionadas = partidas;
	cIDRFC = $("#cIDRFC").val();
	cContable = $("#cCentroContable").val();
	folio = $("#id_caso").val(); 
	$.ajax({
		url:'../gstnmngr/RetencionTipoPersonaBusinessLogic',
		type:'post',
		dataType: 'json',
		data:{cIDRFC: cIDRFC, partidasSeleccionadas: partidasSeleccionadas, cContable:cContable, tipo:"Directo", folio:folio},
		success: function(j){
			var retenciones = j.data_1;
			
			for (var i = 0; i < retenciones.length; i++) {
				var retencion = retenciones[i];
				var idRetencion = retencion.idRetencion;
				var nombreRetencion = retencion.descRetencion;
				var pcRetencion = retencion.porcentaje;			
				
				if ( idRetencion == "9" && $("#cCentroContable").val()=="28"){
					//URVP.27112014 Se valida si es impuesto cedular y nayarit, se cambia el porcentaje de la retencion a 1.5%
					var porcentCedular = 0.00;
					porcentCedular = (parseFloat(pcRetencion) + 0.005).toFixed(18);
					fnClickAddRowB(idRetencion, nombreRetencion, porcentCedular);
				}
				else{
					fnClickAddRowB(idRetencion, nombreRetencion, parseFloat(pcRetencion).toFixed(18));
				}
				//Se inserta desde el servlet que obtiene que retenciones le corresponden al tipo de persona y partidas
				/*$("#idRetencionDefault").val(idRetencion);
				queryFormPost("tPagoDirectoRetencionDefault", {async: false });*/
			}
		}
	});
}

function setSequenceAptd(seqValue) {
	$("#nFolioApartado").val( seqValue );
}

function procesar(){
	
	$( "#dialog-Procesando" ).dialog( "open" );
	return breturnVal;
}

	function tipoFirmantes(){
		
		$( "#dialog-firmantes" ).dialog( "open" );
		queryFormPost("firmantesModuloRead, firmantesModuloReadAut", {async: false }); // voBo - autoriza
		queryFormPost("tPagoFirmanteDelagatorioRead", {async: false }); // Para los firmantes de oficio delegatorio en caso de que existan.
		queryFormPost("tPagoFirmanteDelegatorioVoBoRead", {async: false }); // Para los firmantes de oficio delegatorio VoBo en caso de que existan.
	}


 	function onSubmit(id_oper){//validaciones del boton guardar
  		var p = window.parent;
  		var valida_campos = true;
  		
  		if ($("#docAplicado").val() == "S") {
  			alert("Documento ya fue aplicado y se avanzar\u00E1 a modo de CONSULTA");
  		}else {
			try{
				//validaciones de la forma
				//poner aqui las validaciones a efectual en el boton guardar, en este caso no hay
	
				//Guardado de los campos correspondientes a cada variable de caso
				p.gestion.setFolio( $("#FOLIO").val() );
				p.gestion.setOperador( $("#OPERADOR").val() );
				p.gestion.setFechaDocumento( $("#FECHA_CARGA").val() );
				p.gestion.setEjercicioFiscal( $("#cEjercicio").val() );
				p.gestion.setConceptoMov("Aplicaci\u00F3n pago directo");
				p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
				p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
	
			
				if(id_oper==1){
					var nretval = cmdGuardar();
					if (nretval == -1) {
						return false;
					}
				}

				ponFormato();
				if(id_oper==2){
					$("#COMSOCAutoriza").val("");
					queryFormPost("BuscaCOMSOCAutorizaRead", {async : false});
					if ( $("#COMSOCAutoriza").val() == "0"){
						alert("Pago en Proceso de Autorizaci\u00F3n del \u00E1rea Normativa");
						return false;
					}

					if ( $("#COMSOCAutoriza").val() == "-1"){
						alert("Pago fue Rechazado por el \u00E1rea Normativa, <BORRAR>");
						return false;
					}

					if ( $("#grpAutorizar").val() == "Si" ) {
					  	if ($( "#Fecha_Pago" ).val() == ""){
					  		alert("Falta Capturar la Fecha de Pago");
					  		return false;
					  	}
					  	if ( $( "#cUnidadResponsable" ).val() != "A02" && $( "#cCentroContable" ).val() == "10" ){
						  	$("#Fecha_Pago").attr('disabled', false);
						  	$("#elcontraTemp").val($("#caNoContrarrecibo").val());
							if($("#elcontraTemp").val().substring(0,4) != $("#cCentroContable").val() + cxpPrefijo ){
						  		getNextSequenceVal({seqName: "CR-" + $("#cCentroContable").val(), async: false, callback: setSequenceVal});
						  	}
						  	queryFormPost("tPagoDirectoEncFPagoUpdate,tContraReciboFPagoUpdate,tDocCompFPagoUpdate",{async: false });
					  	}
					   	$("#cancelar").attr('disabled', true);
					   	$("#cancelar").hide();
						//aplicacion contable
						$("#divImprimePoliza").show();
						$("#divImprimeAnexo").show();
		 		 		parent.document.getElementById("pb_save").disabled=true;
						$("#campo").val( "nFolio" + $("#cDocumento").val() );
						$("#tablaEnc").val( "t" + $("#cDocumento").val() + "Encabezado" );
						$("#campoCondicion").val( "caNoContrarrecibo" ); 
						$("#tablaDet").val( "t" + $("#cDocumento").val() + "Detalle" ); 
						$("#tipoAplicar").val( $("#cDocumento").val() );
						
						tipoFirmantes();
						
						/*if ( procesar() ){
							parent.document.getElementById("pb_save").disabled = false;												
						}*/

						//$( "#dialog-form" ).dialog( "open" );
						//fnAplicaMotor();
					}else {
						CancelaApartado();
					}
				}
	
				//Control de estado de botones
			}
			catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}

  		}
  		
		return valida_campos;
  	}
 	
 	function fnAplicaMotor()
 	{
 		divAplica.innerHTML = "<iframe id='ifAplica' src='../gstnmngr/AppCont?elContra=" + $('#caNoContrarrecibo').val() + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
 	}

 	
 	function CancelaApartado(){
		$( "#dialog-Cancela" ).dialog( "open" );
		return bAptdoCancel;
 	}
 	
  	function onPostSubmit(id_oper){//validaciones del boton enviar
  		return true;
  	}
function aplicaContableExitosa()
{
		$("#divImprimePoliza").show();
		$("#divImprimeAnexo").show();
 		$("#divImprime").show();
		//cmdImprimir("PolizaPago");
		$(".paso05").attr('disabled', true);
		$(".paso055").attr('disabled', true);
		parent.document.getElementById("pb_send").disabled=false;
		//parent.document.getElementById("pb_save").disabled=true;
}
  	function onLoadPlantilla(){
		carga();
		if(<%=id_oper > 1%>){
			$("#EditaFacturas").css('visibility', 'hidden');
			$("#ctaBancaria").attr('disabled', true);
		}
		if(<%=id_oper == 1%>){
			
			if("<%=cUR%>" != "A02" && $( "#cCentroContable" ).val() == "10"){
				$( "#Fecha_Pago" ).hide();
				$("#Fecha_Pago").attr("readonly","readonly");
			}
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=false;
		}
		if(<%=id_oper == 2%>){
			$("#grpAutorizar").val("Si");
			$("#divAutorizar").show();
			$("#cancelar").hide();
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=true;
		}

		if(<%=id_oper == 3%>){
			$("#divImprimePoliza").show();
			$("#divImprimeAnexo").show();
			$("#EditaFirmas").css('visibility', 'visible');
			//parent.document.getElementById("pb_cancel").style.visibility='hidden';
			//parent.document.getElementById("pb_cancel").disabled=true;
		}
  	}
  	function ResponsableSiguiente(id_oper){

  		 if(id_oper==1)
  		 	return "AUTORIZA_" + $("#cDocumento").val();
  		 if(id_oper==2)
  		 	return "CONSULTA_" + $("#cDocumento").val();

  	}

  	function OperacionSiguiente(id_oper){

  		if(id_oper==1)
  		 	return "autoriza_factura";
  		if(id_oper==2){
  		 	return "consulta_factura";}
  	}
	
  	
  	
	function onPostDisplay(){
		if ($("#docAplicado").val() == "S") {
			parent.document.getElementById("pb_send").disabled = false;
			parent.document.getElementById("pb_send").click();
		}

	}

	
	function Sinfrmt( fld )	{
	   var valcol = fld.value ;
	   valcol = valcol.replace("$", "");
	   valcol = valcol.replace(",", "");
	   $("#" + fld.id).val( valcol );
}

	function cambiafrmt( fld )
	{
	    $("#" + fld.id).formatCurrency();
	}

function ponFormato(){
		$('.subtotall').each(function(){
			cambiafrmt( this ) ;
	     });
		$('.paso055').each(function(){
			cambiafrmt( this ) ;
	     });
}

	function quitaFmtObj( elObjeto ) {
		var val =0;
		val = elObjeto.value;
		//alert(val);
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
	   	elObjeto.value=val;
	}
	
	 function no_foco(elemento) {
		elemento.style.border = "1px solid #CCCCCC";
	 }
	 
	 function Grid(){
	 	if( $("#TFONDO").val() == "CE" )
			window.open('AyudaEPsPagos.jsp?tipoGasto=2&fuenteFinancimiento=2', 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
		else
			window.open('AyudaEPsPagos.jsp', 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
	 	return false;
	}

	function setSequenceValRCH(seqValue){
		seqValue = "000000" + seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = "<%=cCentroContable%>" + "RCH" + $("#aEjercicioFiscal").val() + seqValue;
		$("#folioDevolucion").val( seqValue );
	}
	  	
	  	
	function cmdImprimirRCH(){
		window.open(
			"../admin/SeguridadCatalogos?"
				+ "catalogo=CONTRARECIBO"
				+ "&accion=run"
				+ "&rn=VOLANTERECHAZO.jasper"
				+ "&NumeroFolio=" + $("#noFolio").val(),
			"popacuse",
			"scrollbars=1, resizable=yes, width=1024, height=768");
	}
	
	function masmenoscentavo(){
		var centavo=0.05;
		var	nuevoIVA = 0.00;
		if (quitaFmt($("#DCD_IVADES").val())==Number($("#ivadesgloceinicial").val())-centavo){
			nuevoIVA=Number(quitaFmt($("#DCD_IVADES").val()));
			nuevoIVA+=Number(centavo);
			$("#DCD_IVADES").val("$"+nuevoIVA.toFixed(2));
		}
		else if (quitaFmt($("#DCD_IVADES").val())==Number($("#ivadesgloceinicial").val())){
			nuevoIVA=Number(quitaFmt($("#DCD_IVADES").val()));
			nuevoIVA+=Number(centavo);
			$("#DCD_IVADES").val("$"+nuevoIVA.toFixed(2));
			
		}
		else if (quitaFmt($("#DCD_IVADES").val())==Number($("#ivadesgloceinicial").val())+centavo){
			nuevoIVA=Number(quitaFmt($("#DCD_IVADES").val()));
			nuevoIVA-=Number(centavo);
			nuevoIVA-=Number(centavo);
			$("#DCD_IVADES").val("$"+nuevoIVA.toFixed(2));
			
		}
	}
	
	function cargaCtaBancariaRFC(){
		$("#campoRFC").val($("#cIDRFC").val());
		queryFormPost("esProveedorExtranjero", {async:false});
		querySelectPost("cargaCtaBancariasRFC", "ctaBancaria",{async: false });
	}

 	function creaDiagloFacturas(abrir){
			$( "#dialog-validaFact" ).dialog({
				autoOpen: false,
				height: 490,
				width: 600,
				modal: true,
				buttons: {
						"Aceptar": function(){
							bClicBtn = true;
							var aData = oTablevFact.fnGetData();
							$("#Imp_Bruto").val( quitaFmt( $("#mTotalFacturaV").val() ) );
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
				},
				open: function(){
					leeDatosFactura();
					creaDTFacturas();
					togleDivFacts(0);
					
					parent.document.getElementById("pb_save").disabled=true;
					
				}
			});
			
			if( abrir ){
				$( "#dialog-validaFact" ).dialog("open");	
			}
	}
	
	function creaDTFacturas(){
		oTablevFact = $('#grdValidaFacturas').dataTable(
		{
			"bProcessing": true,
			"bServerSide": true,
			"bDestroy": true,
			"bSort": true, 
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vfacturaspagos&qw=folioPago=<%=c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%> AND tipoPago='PAGODIRECTO'" ,
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
	
	function togleDivFacts(nIdDiv){
	
		if( nIdDiv == 0){
			$("#uploadFacturasDiv").hide();
			$("#facturasCapturadasDiv").show();
			leeDatosFactura();
			creaDTFacturas();
		}else{
			if ($("#esExtranjero").val()=="1")
				$('#uploadFacturasFrm').attr('src', "UploadFacturasExtranjeros.jsp");
			else
				$('#uploadFacturasFrm').attr('src', "UploadFacturas.jsp?tipo_pago=PAGODIRECTO&RFC="+$("#cIdRFC").val());
			$("#uploadFacturasDiv").show();
			$("#facturasCapturadasDiv").hide();
		}
			
	}
	
	function updateFirmantes(){
	
		queryFormPost("readcDocumentoHaplicadoPago",{async: false });
		
		if ($("#cEstatusPago").val()!="C" ){
			$("#dialog-firmantesUpdate").dialog("open");
			queryFormPost("tPagoFirmanteDelagatorioActualizaRead", {async:false});
			queryFormPost("tPagoFirmanteDelegatorioActualizaVoBoRead", {async:false});
		}else{
			alert("El pago ya est\u00E1 en estatus de Cancelado, por lo que ya no se puede cambia la firma.");
		}
	}
	
	function validarImporteFactura(){	
		var bRegresa = true;
		
		queryFormPost("readMontoFacturasNeto", {async:false});
		
		var montopenalizaciones = quitaFmt($("#DCD_PENALIZACION").val());
		var otrosImptos = parseFloat(  $("#otrosImpuestos").val() );		
		var impteBruto = parseFloat(  $("#Imp_Bruto").val() );
		var impteIva = parseFloat(  $("#Imp_Iva").val() );
		var retencionPago =  quitaFmt($("#DCD_RETENCION").val());
		var importeTotalFact = parseFloat(  quitaFmt( $("#mTotalFacturaV").val() ) ) * 100 / 100; 
		var importeNetoaPagar = (( impteBruto + impteIva + otrosImptos ) - (parseFloat(retencionPago) + parseFloat(montopenalizaciones) )) * 100 / 100;
		var techoImporteEjercer =  parseFloat( importeNetoaPagar + 0.02 ) * 100 / 100;
		var pisoImporteEjercer  =  parseFloat( importeNetoaPagar - 0.02 ) * 100 / 100;
		
		if( ( importeTotalFact >= pisoImporteEjercer && importeTotalFact <= techoImporteEjercer ) ){
			
			var diferencia = 0.00;
			diferencia =  Math.abs(parseFloat(quitaFmt( $("#mTotalFacturaV").val() )) - importeNetoaPagar).toFixed(2);
			if (diferencia>0 && diferencia<0.02){
				if (!confirm("¿Tu pago saldra con diferencia de $"+diferencia+" en el Importe Neto, deseas continuar?")){
		   			alert("Favor de Borrar el tramite.");
		   			bRegresa = false;
		   		}
		   	}
		}else{
		   alert("El importe total en Facturas no corresponde al Importe Neto a Pagar.");
			bRegresa = false;
		}
		
		return bRegresa;
	}
	
	
	function validarRetencionesPago(){	
		var bRegresa = true;
		
		queryFormPost("validaRetencionesDocCompPagoDirectoRead", {async:false});
		
		var retencionPago = $("#retencionPago").val();
				
		if( retencionPago == "0" ){
			alert("No se registraron correctamente los importes de las retenciones. Favor de cancelar el pago.");
			bRegresa = false;
		}
		
		return bRegresa;
	}
	
	function showDivOficioVoBo(esUpdate){
		var cmpName = "oficioDeleVoBo" + (esUpdate?"Update":"");
		var divName = "oficioDelegatorioVoBo" + (esUpdate?"Update":"");
		if( $("#" + cmpName ).is(":checked") )
			$("#"+divName).show();
		else
			$("#"+divName).hide();
	}
</script>

</head>

<body id="dt_example">
	<div id="container" class="container SyCData">
		<h1>Pago Directo - Recepci&oacute;n de documentos</h1>

		<form id="formPagos">
			<!-- VGC validacion de las retenciones en factura. -->
			<input type="hidden" id="listaRetenciones" name="listaRetenciones" value="">
			<input type="hidden" id="nTotalRetenciones" name="nTotalRetenciones" value="0">
			<input type="hidden" id="esExtranjero" name="esExtranjero" value="">
			<input type="hidden" id="cFolioGestion" name="cFolioGestion" value="<%=c.getFolio()%>"> 
			<input name="cFacturaEliminar" id="cFacturaEliminar" value="" type="hidden"> 
			<input name="nFolioPago" id="nFolioPago" type="hidden" value="<%=c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>">
			<input name="cTipoPago" id="cTipoPago" type="hidden" value="PAGODIRECTO"> 
			<input name="totFacturas"  id="totFacturas" type="hidden" value="0"> 
			<input name="cEstatusPago" id="cEstatusPago" type="hidden" value="">
			<input name="mImporteDisponibleEP" id="mImporteDisponibleEP" type="hidden" value=""> 
			<input name="disponibleEP" id="disponibleEP" type="hidden" value=""> 
			<input name="tipoTramite" id="tipoTramite" type="hidden" value="<%=idTipoCaso%>"> 
			<input name="campoRFC" id="campoRFC" type="hidden">
			<input name="nTotalRetenciones" id="nTotalRetenciones" type="hidden"> 
			<input name="partidasSeleccionadas" type="hidden" id="partidasSeleccionadas" value=""> 
			<input
name="obligatoria" type="hidden" id="obligatoria" value="">
			<input name="ivadesgloceinicial" type="hidden"
				id="ivadesgloceinicial" value="0.00"> <input
				name="idRetencionDefault" type="hidden" id="idRetencionDefault">
			<input type="hidden" id="cTipoRfc" name="cTipoRfc" value="" /> <input
				name="mBruto" class="tmpTotall" type="hidden" id="mBruto" value="0"
				size="10"> <input name="mSancion" class="tmpTotall"
				type="hidden" id="mSancion" value="0" size="10"> <input
				name="mDevolucion" class="tmpTotall" type="hidden" id="mDevolucion"
				value="0" size="10"> <input name="mIVA" class="tmpTotall"
				type="hidden" id="mIVA" value="0" size="10"> <input
				name="mRetencion" class="tmpTotall" type="hidden" id="mRetencion"
				value="0" size="10"> <input name="mPenalizacion"
				class="tmpTotall" type="hidden" id="mPenalizacion" value="0"
				size="10"> <input name="mNeto" class="tmpTotall"
				type="hidden" id="mNeto" value="0" size="10"> <input
				name="m2Millar" class="tmpTotall" type="hidden" id="m2Millar"
				value="0" size="10"> <input name="m23IVA" class="tmpTotall"
				type="hidden" id="m23IVA" value="0" size="10"> <input
				name="mISRHonorarios" class="tmpTotall" type="hidden"
				id="mISRHonorarios" value="0" size="10"> <input
				name="m5Millar" class="tmpTotall" type="hidden" id="m5Millar"
				value="0" size="10"> <input name="mFletes" class="tmpTotall"
				type="hidden" id="mFletes" value="0" size="10"> <input
				name="mISRArrenda" class="tmpTotall" type="hidden" id="mISRArrenda"
				value="0" size="10"> <input name="mCedular"
				class="tmpTotall" type="hidden" id="mCedular" value="0" size="10">
			<input name="cInterna" type="hidden" id="cInterna" size="10">
			<input name="OIRAUSU" type="hidden" id="OIRAUSU" size="10"> <input
				name="elfolio" type="hidden" id="elfolio" size="10"> <input
				type="hidden" value="0" id="DCD_ISR_D" name="DCD_ISR_D"> <input
				type="hidden" value="0" id="DCD_IVA_D" name="DCD_IVA_D"> <input
				type="hidden" value="0" id="DCD_MIL5_D" name="DCD_MIL5_D"> <input
				type="hidden" value="0" id="DCD_MIL2_D" name="DCD_MIL2_D"> <input
				type="hidden" value="0" id="DCD_CONTRIBUCION_D"
				name="DCD_CONTRIBUCION_D"> <input type="hidden" value="0"
				id="DCD_OTRAS_RET_D" name="DCD_OTRAS_RET_D"> <input
				type="hidden" value="0" id="DCD_PENALIZACION_D"
				name="DCD_PENALIZACION_D"> <input type="hidden" value="0"
				id="DCD_SANCION_D" name="DCD_SANCION_D"> <input
				type="hidden" value="0" id="DCD_DEVOL_D" name="DCD_DEVOL_D">
			<input type="hidden" value="0" id="DCD_AMORT_D" name="DCD_AMORT_D">
			<input type="hidden" value="0" id="DCD_RETENCION_D"
				name="DCD_RETENCION_D"> <input type="hidden" value="DIRECTO"
				id="TO_TIPO_DOCTO" name="TO_TIPO_DOCTO"> <input
				type="hidden" value="0" class="tmpTotall" id="mImporteMasIva"
				name="mImporteMasIva"> <input type="hidden" value=""
				id="rowsAffected" name="rowsAffected"> <input type="hidden"
				id="cRamo" name="cRamo" value="16"> <input type="hidden"
				id="txtFolioFact" name="txtFolioFact"> <input type="hidden"
				id="cUnidadResponsable2" name="cUnidadResponsable2"> <input
				type="hidden" id="partida" name="partida" value="" /> <input
				type="hidden" id="vOGT" name="vOGT" value="" /> <input
				type="hidden" id="obs" name="obs"> 
				<input type="hidden" id="montoprevio" name="montoprevio"> 
				
				<input type="hidden" id="cDocumento" name="cDocumento"  value="<%=c.getTipoCaso().getGavetaAsociada()%>"> 
				
				<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="">
			<!-- <input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable"> -->
			<input type="hidden" id="cCentroContable" name="cCentroContable">
			<!-- <input type="text" id="cUnidadResponsable" name="cUnidadResponsable">-->
			<input name="numPaso" type="hidden" id="numPaso" value="1" size="5"
				readonly> <input name="tieneFondo" type="hidden"
				id="tieneFondo" size="5" readonly> <input name="cEvento"
				type="hidden" id="cEvento" value="D_AL01"> <input
				type="hidden" id="miOper" value="">
			<!-- urvp tempo -->
			<input name="montoMaximo" type="hidden" id="montoMaximo" value="0">
			<input name="cUnidadEjecutora" type="hidden" id="cUnidadEjecutora"
				value="A02"> <input name="cveRetencion" type="hidden"
				id="cveRetencion" value="0"> <input type="hidden"
				name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" /> <input
				type="hidden" name="numcaso" id="numcaso" value="<%=c.getIdCaso()%>" />
			<input type="hidden" name="OPERADOR" id="OPERADOR"
				value="<%=c.getCasoOperacion(0).getResponsable()%>" /> <input
				type="hidden" name="FECHA_CARGA" id="FECHA_CARGA" value="<%=today%>" />
			<input type="hidden" id="laPoliza" /> <input type="hidden"
				id="aEjercicioFiscal" name="aEjercicioFiscal" value="" /> <input
				type="hidden" id="referenciaAMF" name="referenciaAMF" value="" /> <input
				type="hidden" id="docAplicado" name="docAplicado" value="">
			<input type="hidden" id="campo" name="campo" value=""> <input
				type="hidden" id="tablaEnc" name="tablaEnc" value=""> <input
				type="hidden" id="campoCondicion" name="campoCondicion" value="">
			<input type="hidden" id="tablaDet" name="tablaDet" value="">
			<input type="hidden" id="tipoAplicar" name="tipoAplicar" value="">
			<input type="hidden" id="nFolioApartado" name="nFolioApartado"
				value=""> <input type="hidden" id="cDescripcionPoliza"
				name="cDescripcionPoliza" value=""> <input type="hidden"
				id="FechaAplAptd" name="FechaAplAptd" value=""> <input
				name="elcontra" type="hidden" id="elcontra"> <input
				name="elcontraTemp" type="hidden" id="elcontraTemp" value="">
			<input name="cartera" type="hidden" id="cartera" value=""> <input
				name="capitulo" type="hidden" id="capitulo" value=""> <input
				name="UE" type="hidden" id="UE" value=""> <input
				name="mImporteCartera" type="hidden" id="mImporteCartera" value="">
			<input name="fVigenciaIVA" type="hidden" id="fVigenciaIVA"
				value="05/12/2012" class="paso01" size="10"> <input
				name="mImporteSancion" type="hidden" id="mImporteSancion" value="0"
				class="tmpTotall" size="10"> <input
				name="mImporteDevolucion" type="hidden" id="mImporteDevolucion"
				value="0" class="tmpTotall" size="10"> <input
				name="mAmortizacionAnticipo" type="hidden"
				id="mAmortizacionAnticipo" value="0" class="tmpTotall" size="10">
			<input name="tipoRet" type="hidden" id="tipoRet" value="0"
				class="paso01" size="10"> <input name="mImporteRetencion2"
				type="hidden" id="mImporteRetencion2" value="0" class="tmpTotall"
				size="10"> <input type="hidden" value="2012" id="cEjercicio"
				name="cEjercicio"> <input type="hidden" value="2012"
				id="elrfc" name="elrfc"> <input type="hidden" value="0"
				id="laamortizacion" name="laamortizacion"> 
				<input type="hidden" name="cIdRFC" id="cIdRFC" />
				<input type="hidden" name="RFC" id="RFC" />
			 <input type="hidden"
				name="NumPagoAMF_1" id="NumPagoAMF_1" />
			<!--  input type="hidden" name="NumPagoAMF" id="NumPagoAMF"/> -->
			<input name="aux" type="hidden" id="aux" value="2"> <input
				type="hidden" id="anex" name="anex" value="1" /> <input
				name="mIMDT" type="hidden" id="mIMDT" value=""> <input
				name="mCNIC" type="hidden" id="mCNIC" value=""> <input
				name="cllave" type="hidden" id="cllave" /> <input type="hidden"
				id="nOrden" name="nOrden" value="" /> <input type="hidden"
				id="cValidaFactura" name="cValidaFactura" value="" /> <input
				type="hidden" id="cExiste" name="cExiste" value="0" /> <input
				type="hidden" id="mImporteBrutoF" name="mImporteBrutoF" value="0" />
			<input type="hidden" id="cOrigen" name="cOrigen" value="" /> <input
				type="hidden" id="nDocRenglon" name="nDocRenglon" value="" /> <input
				type="hidden" id="cIdDocumento" name="cIdDocumento" value="" /> <input
				type="hidden" id="cMotivoRechazoCRUD" name="cMotivoRechazoCRUD" />
			<input type="hidden" id="nIdEstado" name="nIdEstado" /> <input
				type="hidden" id="folioDevolucion" name="folioDevolucion" /> <input
				type="hidden" id="noFolio" name="noFolio" /> <input type="hidden"
				name="DPC_FECHA" id="DPC_FECHA" value="<%=today%>" /> <input
				type="hidden" id="nfolio" name="nfolio" value=""> <input
				type="hidden" id="motivo" name="motivo" value=""> <input
				type="hidden" id="mImporteNeto" name="mImporteNeto" value="">
			<input type="hidden" id="fAplicacion" name="fAplicacion" value="">
			<input type="hidden" id="cnombreRFC" name="cnombreRFC" value="">
			<input type="hidden" id="cIdMotivoCancelacion"
				name="cIdMotivoCancelacion" value=""> <input type="hidden"
				id="COMSOCAutoriza" name="COMSOCAutoriza" value="" /> <input
				type="hidden" id="partCOMSOC" name="partCOMSOC" value="" /> <input
				type="hidden" id="cMsjCOMSOC" name="cMsjCOMSOC" value="" /> <input
				type="hidden" id="cOBGT" name="cOBGT" size="14"> <input
				type="hidden" id="firmanteExiste" name="firmanteExiste" size="14">

			<input type="hidden" id="cNombreVo" name="cNombreVo" size=40>
			<input type="hidden" id="cPaternoVo" name="cPaternoVo" size=40>
			<input type="hidden" id="cMaternoVo" name="cMaternoVo" size=40>
			<input type="hidden" id="cPuestoVo" name="cPuestoVo" size=40>
			<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" size=40>
			<input type="hidden" id="tipoFirmante" name="tipoFirmante" size=15
				value="PAGO_VOBO"> <input type="hidden" id="cNombreA"
				name="cNombreA" size=40> <input type="hidden" id="cPaternoA"
				name="cPaternoA" size=40> <input type="hidden"
				id="cMaternoA" name="cMaternoA" size=40> <input
				type="hidden" id="cPuestoA" name="cPuestoA" size=40> <input
				type="hidden" id="firmanteAut" name="firmanteAut" size=40> <input
				type="hidden" id="tipoFirmanteA" name="tipoFirmanteA" size=15
				value="PAGO_AUT">
			<input type="hidden" id="NumeroDetalle" name="NumeroDetalle" value="0">
			<input type="hidden" id="retencionPago" name="retencionPago" value="0">
			
			<!-- hidden para la captura de oficio delegatorio -->
			<input type="hidden" name="cFolioOficioAux" id="cFolioOficioAux" value="">
			<input type="hidden" name="dFechaOficioAux" id="dFechaOficioAux" value="">
			<input type="hidden" name="cNombreTitularAux" id="cNombreTitularAux" value="">
			<input type="hidden" name="cApellidoPaternoTitularAux" id="cApellidoPaternoTitularAux" value="">
			<input type="hidden" name="cApellidoMaternoTitularAux" id="cApellidoMaternoTitularAux" value="">
			<input type="hidden" name="cPuestoTitularAux" id="cPuestoTitularAux" value="">
			<input type="hidden" name="firmanteOficioExiste" id="firmanteOficioExiste" value="">
			
			
			<!-- hidden para la captura de los datos de quien elaboro -->
			<input type="hidden" id="cNombreE" name="cNombreE" size=40>
			<input type="hidden" id="cPaternoE" name="cPaternoE" size=40>
			<input type="hidden" id="cMaternoE" name="cMaternoE" size=40>
			<input type="hidden" id="cPuestoE" name="cPuestoE" size=40>
			<input type="hidden" id="firmanteEla" name="firmanteEla" size=40>
			
			<!-- hidden para validar si la partida es de viaticos -->
			<input type="hidden" name="cPartida" id="cPartida" value="">
			<input type="hidden" name="esPartidaViaticos" id="esPartidaViaticos" value="0">
			
			<!-- hidden para la captura de oficio delegatorio VoBo-->
			<input type="hidden" name="cFolioOficioVoBoAux" id="cFolioOficioVoBoAux" value="">
			<input type="hidden" name="dFechaOficioVoBoAux" id="dFechaOficioVoBoAux" value="">
			<input type="hidden" name="cNombreTitularVoBoAux" id="cNombreTitularVoBoAux" value="">
			<input type="hidden" name="cApellidoPaternoTitularVoBoAux" id="cApellidoPaternoTitularVoBoAux" value="">
			<input type="hidden" name="cApellidoMaternoTitularVoBoAux" id="cApellidoMaternoTitularVoBoAux" value="">
			<input type="hidden" name="cPuestoTitularVoBoAux" id="cPuestoTitularVoBoAux" value="">
			<input type="hidden" name="firmanteOficioVoBoExiste" id="firmanteOficioVoBoExiste" value="">
			
			<!--   -->
			
			<div class="dvGeneral">
				<table height="66" width="800" border="0">
					<tr align="left">
						<td>
							<!-- No. Folio:  --> <input name="No_Folio" type="hidden"
							style="text-transform:uppercase" id="No_Folio" class="paso01"
							onkeypress="valFmt(this,11)" size="20" maxlength="40"
							title="Capture el numero de Factura">
						</td>
						<td nowrap><input type="hidden" id="TIPO_CONCEPTO"
							name="TIPO_CONCEPTO">
							<div id="divAutorizar">
								<label id="lbAutorizar"> Autorizar </label> <input
									name="grpAutorizar" type="radio" id="grpAutorizar"
									onclick="habilitaGuardar(1)" value="Si" checked> Si <label>
									<input type="radio" id="grpAutorizar" name="grpAutorizar"
									onclick="habilitaGuardar(0)" value="No"> No </label>
								&nbsp;&nbsp;&nbsp;&nbsp;
							</div> No. contrarrecibo:&nbsp;&nbsp; <input readonly type="text"
							name="caNoContrarrecibo" id="caNoContrarrecibo" size="17" />
						</td>
						<td colspan="2">Folio <input readonly type="text"
							id="id_caso" name="id_caso">
						</td>
						<td><input type="hidden" id="cUnidadResponsable"
							name="cUnidadResponsable">
							<div id="divImprimePoliza">
								<img src="imagenes/Imprimir.png" width="25" height="21"
									onClick="cmdImprimir('PolizaPago');"> P&oacute;liza
							</div>
							<div id="divImprimeAnexo">
								<img src="imagenes/Imprimir.png" width="25" height="21"
									onClick="anexo('PolizaPago');"> Anexo
							</div> <span id="EditaFirmas" style="visibility:hidden"><a
								href="#" onclick="updateFirmantes();">Firmas*</a>
						</span></td>
					</tr>
					<tr align="left">
						<td nowrap>Tipo de pago Directo:</td>
						<td>
							<!--<input type="hidden" id="cIdTipoOperacion"
									name="cIdTipoOperacion">--> <select id="cIdTipoOperacion"
							class="paso01" name="cIdTipoOperacion" style="width: 20em;">
						</select>
						</td>
						<td colspan="2" nowrap><input type="button" value="Borrar"
							name="cancelar" id="cancelar" onClick="cmdBorrar()"
							title="Elimina la solicitud temporal" /> &nbsp; <input
							type="button" value="Regresar" name="btRegresar"
							onClick="cmdRegresar()" title="Regresar al Inbox" />
						</td>
					</tr>
					<tr align="left">
						<td>R.F.C.: <input readOnly type="text" maxlength="15"
							size="15" name="cIDRFC" id="cIDRFC"
							onchange="cargaCtaBancariaRFC();" /> <input type="button"
							id="btnBeneficiario" onclick="cat_beneficiario()" value="...">
						</td>
						<td colspan="4"><input readonly type="text" maxlength="100"
							size="100" name="cnombre" class="paso01" ID="cnombre" />
						</td>
					</tr>
					<tr>
						<td colspan="4">Cuenta Bancaria: <select id="ctaBancaria"
							name="ctaBancaria"></select>
						</td>
					</tr>
					<tr align="left">
						<td nowrap>Fecha Recepci&oacute;n: <input name="fRecepcion"
							type="text" id="fRecepcion" value="" class="paso01" size="10"
							readonly>
						</td>
						<td valign="top" nowrap>
							<!--Fecha Estimada de Pago:--> <input name="Fecha_Pago"
							type="hidden" class="paso01" id="Fecha_Pago"
							onchange="valFecha(this)" size="10" maxlength="10"
							value="<%=today%>">
						</td>
						<td valign="top" nowrap>
							<!--Fecha Aplicacion:--> <input name="FechaAplicacion"
							type="text" class="paso01" id="FechaAplicacion"
							onchange="valFecha(this)" value="31/01/2012" size="10"
							maxlength="10">
						</td>
					</tr>
					<tr>
						<td colspan="2" width="40%">Tipo Destino: <select
							class="paso01" id="DESTINO_GASTO" name="DESTINO_GASTO">
						</select> <span id="EditaFacturas" style="visibility:hidden"><a
								href="#" onclick="creaDiagloFacturas('true'); return false;">
									Facturas </a> </span> <input type="hidden" name="ID_TIPO_MOVIMIENTO"
							id="ID_TIPO_MOVIMIENTO" /> <input type="hidden"
							id="ID_TIPO_CONCEPTO" name="ID_TIPO_CONCEPTO">
						</td>
						<td style="visibility: hidden">#Pago AMF <select
							id="NumPagoAMF" name="NumPagoAMF">
								<option value="***" selected>- Selecciona AMF -</option>
						</select>
						</td>
						<td>
							<input type="hidden" name="ID_DESTINO_GASTO" id="ID_DESTINO_GASTO" /> 
							<input name="cIdTipoDocumento" type="hidden" id="cIdTipoDocumento" value="5">
						</td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td width="40%" style="visibility: hidden;">Clave AMF <input
							type="text" name="nClaveAMF" id="nClaveAMF" readonly="readonly"
							size="15" maxlength="15" />
						</td>
						<td width="55%" style="visibility: hidden;">Folio AMF <input
							type="text" name="numFolioAMF" id="numFolioAMF"
							readonly="readonly" size="15" maxlength="15" />
						</td>
						<td width="55%" style="visibility: hidden;">Importe AMF <input
							type="text" name="PagoAMF" id="PagoAMF" readonly="readonly"
							size="15" maxlength="15" />
						</td>

					</tr>






				</table>


			</div>
			<div id="multitabs" style="width: 110%">
				<div id="tabs" style="width: 100%">
					<ul>
						<li><a id="LNK01" href="#tabs-5" class="pasoUno">Concepto</a>
						</li>
						<li><a id="LNK02" href="#tabs-2" class="pasoDos">Fuente
								de financiamiento</a>
						</li>
						<li><a id="LNK03" href="#tabs-1" class="pasoTres">Movimientos</a>
						</li>
						<li><a id="LNK04" href="#tabs-3" class="pasoCuatro">Retenciones</a>
						</li>
						<li><a id="LNK05" href="#tabs-4" class="pasoCinco">Documentaci&oacute;n</a>
						</li>
					</ul>
					<div id="tabs-5">
						<table border="0" cellspacing="0" cellpadding="0"
							style="width: 750px">
							<tr>
								<td>
									<table border="0" cellspacing="0" cellpadding="0"
										style="width: 550px">
										
										<tr>
											<td rowspan="5" align="left">Concepto: <textarea
													name="Concepto" rows="512" class="paso01" ID="Concepto"
													style="height: 111px; width: 300px"
													onkeypress="valFmt(this,15)" title="Descripcion del pago"></textarea>
											</td>
										</tr>
										
										<tr>

											<td colspan="2" align="right" nowrap>
												Importe Bruto:
											</td>
											<td nowrap>
												<input readonly style="text-align: right;" name="Imp_Bruto" type="text" class="subtotall" id="Imp_Bruto" onkeypress="valFmt(this,9)" value="0" size="15" maxlength="15" />
											</td>
											<td nowrap>
												&nbsp;&nbsp;Importe por Ejercer:
											</td>
											<td width="177">
												<input readonly style="text-align: right;" name="Imp_Ejercer" type="text" class="subtotall" id="Imp_Ejercer" onkeypress="valFmt(this,9)" value="0" size="15" maxlength="15" />
											</td>
										</tr>

										<tr>
											<td colspan="2" align="right" nowrap>
												Importe IVA:
											</td>
											<td nowrap>
												<input readonly style="text-align: right;" name="Imp_Iva" type="text" class="subtotall" id="Imp_Iva" onkeypress="valFmt(this,9)" value="0" size="15"  maxlength="15" />
											</td>
											<td align="right" nowrap>
												% IVA:
											</td>
											<td nowrap>
												<input style="text-align: right;" name="por_IvaOld" type="hidden" class="subtotall" id="por_IvaOld" onKeyPress="valFmt(this,9)" value="0.16" size="15" maxlength="15" /> 
												<select class="subtotall" id="por_Iva" name="por_Iva" style="width: 10em;">
												</select>
											</td>
										</tr>
										
										<tr>
											<td colspan="2" align="right" nowrap>
												Importe Retenciones:
											</td>
											<td nowrap>
												<input readonly style="text-align: right;" name="Imp_Retenciones" type="text" class="subtotall" id="Imp_Retenciones" onkeypress="valFmt(this,9)" value="0" size="15" maxlength="15" />
											</td>
											<td nowrap>&nbsp;</td>
											<td nowrap>&nbsp;</td>
										</tr>

										<tr>
											<td colspan="2" align="right" nowrap>
												Otros Impuestos:
											</td>
											<td nowrap>
												<input readonly style="text-align: right;" name="otrosImpuestos" type="text" class="subtotall" id="otrosImpuestos" onkeypress="valFmt(this,9)" value="0" size="15" maxlength="15" />
											</td>
											<td nowrap>&nbsp;</td>
											<td nowrap>&nbsp;</td>
										</tr>

										<tr>

											<td align="right" nowrap colspan="3">
												Monto penalizaciones:
											</td>
											<td>
												<input style="text-align: right;" onKeyPress="valFmt(this,9)" onfocus="Sinfrmt(this)" name="mImportePenalizacion" type="text" class="subtotall" id="mImportePenalizacion" value="0" size="15" maxlength="15">
											</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
										</tr>
										
										<tr>
											<td colspan="2" align="right"></td>
											<td align="right">
												Importe Neto:
											</td>
											<td>
												<input readonly style="text-align: right;" name="Imp_Neto" type="text" class="subtotall" id="Imp_Neto" onKeyPress="valFmt(this,9)" value="0" size="15" maxlength="15" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td height="50px" colspan="3" align="left">&nbsp;</td>
							</tr>
						</table>
					</div>
					<div id="tabs-1">
						<!--	    <h2>Claves Presupuestales</h2>-->
						<table>
							<tr>
								<td>Tipo de Concepto</td>
								<td colspan="2">Tipo De Movimiento</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td><select id="tConcepto" class="paso03" name="tConcepto"
									style="width: 10em;"></select>
								</td>
								<td colspan="2"><select id="tmovimiento" class="paso03"
									name="tmovimiento" style="width: 20em;"></select>
								</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td>Almac&eacute;n</td>
								<td></td>
								<!-- <td>Alta almacenaria</td> -->
								<td></td>
								<!-- <td>A&ntilde;o</td>  -->
								<td></td>
								<!-- <td>Factura</td>  -->
							</tr>
							<tr>
								<td><select class="paso03" id="ALM" name="ALM"></select>
								</td>
								<td><input name="altaAlmacen" type="text" class="paso03"
									id="altaAlmacen" style="text-align: right;"
									onKeyPress="valFmt(this,9)" value="0" size="12" maxlength="12">
								</td>
								<td><input type="text" id="cAnioFactEP" name="cAnioFactEP"
									onKeyPress="valFmt(this,9)" class="paso03" size="4"
									maxlength="4"></td>
								<td><input type="text" id="nFacturaEP" name="nFacturaEP"
									style="text-transform:uppercase" class="paso03" size="12"
									maxlength="12"></td>
							</tr>
							<tr>
								<td>Estructura Program&aacute;tica</td>
								<td align="right"><input type="button" id="btAgregaEP"
									class="paso03" name="btAgregaEP" onClick="agregaMovimientos();"
									value="Agregar">
								</td>

								<td nowrap>Monto del movimiento<br>(Con IVA)</td>

								<td nowrap>Acumulado Operaci&oacute;n</td>
							<tr>

								<td colspan=2 nowrap><input type="text" id="EP" name="EP"
									size="64" readonly> <input type="button"
									id="nIdClaveEgresos2" size="5" value="..." onclick="Grid()">
									<input name="nClaveCNA1" type="hidden" id="nClaveCNA1" size="7"
									maxlength="7" readonly>
								</td>
								<td><input name="mMovimiento" type="text" class="paso03"
									id="mMovimiento" style="text-align: right;"
									onKeyPress="valFmt(this,9)" value="0" size="15" maxlength="15"
									onchange="cambioss()"> <input name="mMovimiento2"
									class="tmpTotall" type="hidden" id="mMovimiento2" size="10">
									<a href="javascript:void(0);" onClick="fnClickAddRowA();">
										<input name="cIdCuentaContable" type="hidden"
										id="cIdCuentaContable" size="20"> </a><a
									href="javascript:void(0);" onClick="fnClickAddRowA();"> <input
										name="nMes" type="hidden" id="nMes" size="20"> </a>
								</td>
								<td>&nbsp; <input readonly style="text-align: right;"
									name="Acumulado_Op" type="text" class="subtTotall"
									id="Acumulado_Op" onKeyPress="valFmt(this,9)" value="0"
									size="15" maxlength="15" />
								</td>



							</tr>
						</table>

						<table id="grdMovimientos" class="display">
							<thead>
								<tr align="center">
									<th>#Movto</th>
									<th>Estructura Program&aacute;tica SIAF</th>
									<th>Tipo Concepto</th>
									<th>Almac&eacute;n</th>
									<th>Alta</th>
									<th>Importe</th>
								</tr>
							</thead>
						</table>
					</div>

					<div id="tabs-2">
						<table width="788" border="0">
							<tr>
								<td width="234">
									<table width="248" height="83" border="0">
										<tr>
											<td width="242"><strong>Fuente de
													Financiamiento </strong>
											</td>
										</tr>
										<tr>
											<td><select id="TFONDO" name="TFONDO"
												onChange="tfondos()">
													<option value="FF">Fondos Fiscales</option>
													<option value="CE" selected>Credito Externo</option>
													<!-- <option value="IP"> Ingresos Propios</option>  -->
											</select>
											</td>
										</tr>

									</table>
								</td>
								<td width="236">
									<table width="232" height="78" border="0">
										<tr>
											<td colspan="2"><strong>Porcentaje de
													Financiamiento</strong>
											</td>
										</tr>
										<tr>
											<td width="103">Cr&eacute;dito Externo</td>
											<td width="100"><input style="text-align: right;"
												name="vcInversion" type="text" class="paso02"
												id="vcInversion" onkeypress="valFmt(this,9)" value="0"
												size="7" maxlength="7" readonly /> %</td>
										</tr>
										<tr>
											<td>Contraparte</td>
											<td><input style="text-align: right;" name="contraparte"
												type="text" class="paso02" id="contraparte"
												onkeypress="valFmt(this,9)" size="7" maxlength="7" readonly />
												%</td>
										</tr>
									</table>
								</td>
								<td width="278">
									<table width="278" border="0">
										<tr>
											<td colspan="2"><strong>El contrato es
													considerado como: </strong>
											</td>
										</tr>
										<tr>
											<td width="35"><select id="TContrato" name="TContrato"
												class="paso02">
													<option value="AL">Aporte Local (AL)</option>
													<option value="FE">Financiamiento Externo</option>
											</select>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td colspan="3">
									<table width="778" border="0">
										<tr>
											<td colspan="2"><strong>Datos del
													Pr&eacute;stamo </strong>
											</td>
										</tr>
										<tr>
											<td width="197">Prestamo</td>
											<td width="549"><select id="Prestamo" name="Prestamo"
												onChange="cambio();" class="paso02"><option
														value=" ">seleccione</option>
											</select> <!-- <input type="hidden" id="vpres2" name="vpres"  /> --> <input
												type="hidden" id="nPorcentajeFinanciamiento"
												name="nPorcentajeFinanciamiento" />
											</td>
										</tr>
										<tr>
											<td>Categor&iacute;a Inversi&oacute;n</td>
											<td width="549"><select id="CInversion"
												name="CInversion" onChange="cambio2();" class="paso02"></select>
												<input type="hidden" id="pcInversion" name="pcInversion" />

											</td>
										</tr>
										<tr>
											<td>Oficio de Elegibilidad</td>
											<td width="549"><input name="oficioElegibilidad"
												type="text" class="paso02" id="oficioElegibilidad"
												onkeypress="valFmt(this,11)" size="15" maxlength="15" />
											</td>
										</tr>
										<tr>
											<td>Fecha de Oficio de Elegibilidad</td>
											<td><input name="fElegibilidad" class="paso02"
												id="fElegibilidad" onChange="valFecha(this)" value=""
												size="10" maxlength="10" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</div>
					<div id="tabs-3">
						<!--	 <h2>Retenciones</h2>-->

						<table border="0" cellspacing="0" cellpadding="0"
							style="width: 550px">
							<tr>
								<td><select name="cIdTipoRetencion" id="cIdTipoRetencion"
									class="paso04">
										<option selected value="">SIN RETENCION</option>
								</select> <input type="hidden" id="cTipoRetencion" name="cTipoRetencion" />
									<input type="hidden" id="nPorcRetencion" name="nPorcRetencion" />
								</td>
								<td><input type="button" id="btAgregaMov" class="paso04"
									name="btAgregaMov" onClick="fnAgregarRet();" value="Agregar">
								</td>
								<td>&nbsp;
									<div id="divMilla2">
										<label> Camara : <input name="grpMilla2" type="radio"
											id="grpMilla2" onclick="habilitaMillar2(0)" value="Camara">
										</label> <label> Instituto : <input type="radio"
											id="grpMilla2" name="grpMilla2" onclick="habilitaMillar2(1)"
											value="Instituto"> </label> &nbsp;&nbsp;&nbsp;&nbsp;
									</div>
								</td>
							</tr>
						</table>
						<table   class="display"
							id="grdRetencion">
							<thead>
								<tr>
									<th nowrap>C&oacute;digo</th>
									<th>Retenci&oacute;n</th>

									<th>Porcentaje</th>
								</tr>
							</thead>
							<tbody>
							</tbody>
							<tfoot>
							</tfoot>
						</table>
					</div>


					<div id="tabs-4">
						<table border="0" cellspacing="2" cellpadding="0">
							<tr>
								<td>No. Folio</td>
								<td>Fecha de factura</td>
								<td>Tipo de beneficiario</td>
								<td>Clave de beneficiario</td>
								<td>Tipo de operaci&oacute;n</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td><input class="paso05" name="DCD_FACTURA" type="text"
									maxlength="20" id="DCD_FACTURA" readonly>
								</td>
								<td><input class="paso05" name="DCD_FECHA_FACTURA"
									type="text" id="DCD_FECHA_FACTURA" onchange="valFecha(this)"
									size="10" maxlength="10" readonly>
								</td>
								<td><input class="paso05" name="DCD_TBEN" type="text"
									id="DCD_TBEN" size="15" maxlength="15" readonly>
								</td>
								<td><input class="paso05" name="DCD_CBEN" type="text"
									id="DCD_CBEN" size="15" maxlength="15" readonly>
								</td>
								<td><select class="paso05" name="DCD_TIPO_OPE"
									id="DCD_TIPO_OPE">
										<option value="85">85 OTROS</option>
								</select>
									<div id="divImprime">
										<img src="imagenes/Imprimir.png" width="25" height="21"
											onClick="cmdImprimir('Contrarecibo');"> <input
											type="checkbox" name="checkbox" value="checkbox"> Con
										formato
									</div>
								</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td>Porcentaje IVA</td>
								<td>Importe Total</td>
								<td>IVA Desglose</td>
								<td>IVA Retenci&oacute;n</td>
								<td>Otros Impuestos</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td><select class="paso05" id="DESCRIPCION20"
									name="DESCRIPCION20" style="width: 10em;">
								</select>
								</td>
								<td><input class="paso055" style="text-align: right;"
									onkeypress="valFmt(this,9)" name="DCD_IMP_BRUTO" type="text"
									id="DCD_IMP_BRUTO" value="0" size="15" maxlength="15" readonly>
								</td>
								<td><input class="paso055" style="text-align: right;"
									onkeypress="valFmt(this,9)" name="DCD_IVADES" type="text"
									id="DCD_IVADES" value="0" size="15" maxlength="15" readonly>
									<!-- onclick="masmenoscentavo();" -->
								</td>

								<td><input class="paso055" style="text-align: right;"
									onkeypress="valFmt(this,9)" name="DCD_IVA" type="text"
									id="DCD_IVA" value="0" size="15" maxlength="15" readonly>
								</td>

								<td><input class="paso055" style="text-align: right;"
									onkeypress="valFmt(this,9)" name="DCD_OTROS_IMP" type="text"
									id="DCD_OTROS_IMP" value="0" size="15" maxlength="15" readonly>
								</td>

								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td>ISR</td>
								<td>Ret. 0.5%</td>
								<td>Ret. 0.2%</td>
								<td>Beneficio social</td>
								<td>Impuesto cedular</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td><input class="paso055" style="text-align: right;"
									onkeypress="valFmt(this,9)" name="DCD_MIL5" type="text"
									id="DCD_MIL5" value="0" size="15" maxlength="15" readonly>
								</td>
								<td><input class="paso055" style="text-align: right;"
									onkeypress="valFmt(this,9)" name="DCD_MIL2" type="text"
									id="DCD_MIL2" value="0" size="15" maxlength="15" readonly>
								</td>
								<td><input class="paso055" style="text-align: right;"
									onkeypress="valFmt(this,9)" name="DCD_CONTRIBUCION" type="text"
									id="DCD_CONTRIBUCION" value="0" size="15" maxlength="15"
									readonly>
								</td>
								<td><input class="paso055" style="text-align: right;"
									onkeypress="valFmt(this,9)" name="DCD_OTRAS_RET" type="text"
									id="DCD_OTRAS_RET" value="0" size="15" maxlength="15" readonly>
								</td>
								<td><input class="paso055" style="text-align: right;"
									onkeypress="valFmt(this,9)" name="DCD_ISR" type="text"
									id="DCD_ISR" value="0" size="15" maxlength="15" readonly>
								</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td>Penalizaciones</td>
								<td>Sanci&oacute;n</td>
								<td>Devolucion</td>
								<td>Amort. Anticipo</td>
								<td>Retencion</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td><input class="paso055" style="text-align: right;"
									onkeypress="valFmt(this,9)" name="DCD_SANCION" type="text"
									id="DCD_SANCION" value="0" size="15" maxlength="15" readonly>
								</td>
								<td><input class="paso055" style="text-align: right;"
									onkeypress="valFmt(this,9)" name="DCD_DEVOL" type="text"
									id="DCD_DEVOL" value="0" size="15" maxlength="15" readonly>
								</td>
								<td><input class="paso055" style="text-align: right;"
									onkeypress="valFmt(this,9)" name="DCD_AMORT" type="text"
									id="DCD_AMORT" value="0" size="15" maxlength="15" readonly>
								</td>
								<td><input class="paso055" style="text-align: right;"
									onkeypress="valFmt(this,9)" name="DCD_RETENCION" type="text"
									id="DCD_RETENCION" value="0" size="15" maxlength="15" readonly>
								</td>
								<td><input class="paso055" style="text-align: right;"
									onkeypress="valFmt(this,9)" name="DCD_PENALIZACION" type="text"
									id="DCD_PENALIZACION" value="0" size="15" maxlength="15"
									readonly></td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td>Neto</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td><input class="paso055" style="text-align: right;"
									onkeypress="valFmt(this,9)" name="DCD_NETO" type="text"
									id="DCD_NETO" value="0" size="15" maxlength="15" readonly>
								</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td>Concepto:</td>
								<td></td>
								<td><input class="paso05" type="button" value="Agregar"
									id="btAgregarFact" name="btAgregarFact"
									onClick="fnClickAddRowComp()" />
								</td>

								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td colspan="3"><textarea name="DCD_CONCEPTO"
										id="DCD_CONCEPTO" rows="500" id="DCD_CONCEPTO"
										style="height: 91px; width: 448px"
										onkeypress="valFmt(this,15)"></textarea>
								</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
						</table>
						<table   class="display"
							id="grdFacturas">
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
						<table   class="display"
							id="grdCompromisos">
							<thead>
								<tr>

									<th nowrap>Mes</th>
									<th>Cuenta</th>
									<th>Saldo</th>
								</tr>
							</thead>
							<tbody>
							</tbody>
							<tfoot>
							</tfoot>
						</table>

						<table border="0" cellspacing="0" cellpadding="0"
							style="width: 450px">
							<tr>

								<td height="50px" colspan="3" align="left">&nbsp;</td>
							</tr>
						</table>
					</div>
				</div>
			</div>
			<div id="dialog-form" title="Aplicacion Presupuestal/Contable">
				<div id="divEspera" align="center">
					Espere por favor.... <img border="0" src="../imagenes/espera.gif"
						height="30">
				</div>
				<div id="divAplica">
					<iframe id="ifAplica" src="about:blank"></iframe>
				</div>
			</div>
			<div id="dialog-Procesando" title="Procesando">
				<div id="divEsperaProcesando" style="visibility: hidden"
					align="center">
					Espere por favor.... <img border="0" src="../imagenes/espera.gif"
						height="30">
				</div>
			</div>
			<div id="dialog-Terminado" title="Proceso Terminado">
				<div id="divEsperaTerminado" align="center"></div>
			</div>
			<div id="dialog-Cancela" title="Cancela Documento">
				<h1>Esta Seguro Que Desea Cancelar el Documento?</h1>
				<div id="divRechazoMat" style="visibility: hidden">
					Motivo de Rechazo
					<textarea id="cMotivoRechazo" name="cMotivoRechazo" cols="40"
						rows="5"></textarea>
				</div>
				<div id="divEsperaCancelando" style="visibility: hidden"
					align="center">
					Espere por favor.... <img border="0" src="../imagenes/espera.gif"
						height="30">
				</div>
			</div>
			<div id="dialog-validaOLI" title="Validacion de OLI">
				<h1>Digite el N&uacute;mero de OLI a Validar</h1>
				<input style="text-align: right;" name="cOLI" type="text" id="cOLI"
					value="" size="6" maxlength="6" />
			</div>
			<div id="dialog-validaFact" title="Validacion de Facturas">
				<div id="uploadFacturasDiv">
					<iframe id="uploadFacturasFrm"
						src="UploadFacturas.jsp?tipo_pago=PAGODIRECTO" align="top"
						frameborder="0" height="360" width="600"> </iframe>
				</div>
				<div id="facturasCapturadasDiv">
					<table width="95%">
						<tr>
							<td align="right"><a href="#"
								onclick="togleDivFacts(1);return false;">Cargar Facturas</a></td>
						</tr>
						<tr>
							<td>
								<fieldset>
									<legend>Facturas Capturadas.</legend>
									<table id="grdValidaFacturas">
										<thead>
											<tr>
												<th>Serie</th>
												<th>Factura</th>
												<th>Importe Bruto</th>
											</tr>
										</thead>
									</table>
									Total de las facturas:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input
										type="text" style="text-align: right;" name="mTotalFacturaV"
										id="mTotalFacturaV" value="0.00" size="15" maxlength="15"
										readonly />
								</fieldset></td>
						</tr>
					</table>
				</div>
			</div>
			<div style="visibility: hidden">
				<table id="dtRecalcRetencionEP">
					<thead>
						<tr>
							<th>nDocRenglon</th>
							<th>mImporteBruto</th>
						</tr>
					</thead>
				</table>
			</div>
			<div id="dialog-firmantes" title="Firmantes">
				<table>
					<tr>
						<td align="left">
							<input type="checkbox" id="oficioDelegatorio" name="oficioDelegatorio" onclick="showDivOficio(false);">Oficio Delegatorio Autoriza
						</td>
						<td align="left">
							<input type="checkbox" id="oficioDeleVoBo" name="oficioDeleVoBo" onclick="showDivOficioVoBo(false);">Oficio Delegatorio VoBo
						</td>	
					</tr>
				</table>
				<fieldset>
					<legend>Datos Vo Bo</legend>					
					<table>
						<tr>
							<td>Nombre</td>
							<td><input type="text" id="cNombreVoBo" name="cNombreVoBo"
								size=40></td>
						</tr>
						<tr>
							<td>Apellido Paterno</td>
							<td><input type="text" id="cPaternoVoBo" name="cPaternoVoBo"
								size=40></td>
						</tr>
						<tr>
							<td>Apellido Materno</td>
							<td><input type="text" id="cMaternoVoBo" name="cMaternoVoBo"
								size=40></td>
						</tr>
						<tr>
							<td>Puesto</td>
							<td><input type="text" id="cPuestoVoBo" name="cPuestoVoBo"
								size=40></td>
						</tr>
					</table>
				</fieldset>
				<fieldset>
					<legend>Datos Autoriza</legend>
					<table>
						<tr>
							<td>Nombre</td>
							<td><input type="text" id="cNombreAut" name="cNombreAut"
								size=40></td>
						</tr>
						<tr>
							<td>Apellido Paterno</td>
							<td><input type="text" id="cPaternoAut" name="cPaternoAut"
								size=40></td>
						</tr>
						<tr>
							<td>Apellido Materno</td>
							<td><input type="text" id="cMaternoAut" name="cMaternoAut"
								size=40></td>
						</tr>
						<tr>
							<td>Puesto</td>
							<td><input type="text" id="cPuestoAut" name="cPuestoAut"
								size=40></td>
						</tr>


					</table>
				</fieldset>
				<fieldset>
					<legend>Datos Elabora</legend>
					<table>
						<tr>
							<td>Nombre</td>
							<td><input type="text" id="cNombreEla" name="cNombreEla"
								size=40 maxlength="70" />
							</td>
						</tr>
						<tr>
							<td>Apellido Paterno</td>
							<td><input type="text" id="cPaternoEla" name="cPaternoEla"
								size=40 maxlength="70" />
							</td>
						</tr>
						<tr>
							<td>Apellido Materno</td>
							<td><input type="text" id="cMaternoEla" name="cMaternoEla"
								size=40 maxlength="70" />
							</td>
						</tr>
						<tr>
							<td>Puesto</td>
							<td><input type="text" id="cPuestoEla" name="cPuestoEla"
								size=40 maxlength="70" />
							</td>
						</tr>

					</table>
				</fieldset>
				<div id="oficioDelegatorioCaptura">
					<fieldset>
						<legend>Datos del Suplente</legend>
						<table>
							<tr>
								<td>No. de Oficio</td>
								<td><input type="text" id="cFolioOficio"
									name="cFolioOficio" size=30 maxlength="70" />
								</td>
								<td>Fecha de Oficio</td>
								<td><input type="text" id="dFechaOficio"
									name="dFechaOficio" size=12 maxlength="12" />
								</td>

							</tr>
							<tr>
								<td>Nombre Suplente:</td>
								<td><input type="text" id="cNombreTitular"
									name="cNombreTitular" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Apellido Paterno</td>
								<td><input type="text" id="cApellidoPaternoTitular"
									name="cApellidoPaternoTitular" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Apellido Materno</td>
								<td><input type="text" id="cApellidoMaternoTitular"
									name="cApellidoMaternoTitular" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Puesto</td>
								<td><input type="text" id="cPuestoTitular"
									name="cPuestoTitular" size=40 maxlength="70" /></td>
						</table>
					</fieldset>
				</div>
				
				<div id="oficioDelegatorioVoBo">
					<fieldset>
						<legend>Datos del Suplente VoBo</legend>
						<table>
							<tr>
								<td>No. de Oficio</td>
								<td><input type="text" id="cFolioOficioVoBo"
									name="cFolioOficioVoBo" size=30 maxlength="70" />
								</td>
								<td>Fecha de Oficio</td>
								<td><input type="text" id="dFechaOficioVoBo"
									name="dFechaOficioVoBo" size=12 maxlength="12" />
								</td>

							</tr>
							<tr>
								<td>Nombre Suplente:</td>
								<td><input type="text" id="cNombreTitularVoBo"
									name="cNombreTitularVoBo" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Apellido Paterno</td>
								<td><input type="text" id="cApellidoPaternoTitularVoBo"
									name="cApellidoPaternoTitularVoBo" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Apellido Materno</td>
								<td><input type="text" id="cApellidoMaternoTitularVoBo"
									name="cApellidoMaternoTitularVoBo" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Puesto</td>
								<td><input type="text" id="cPuestoTitularVoBo"
									name="cPuestoTitularVoBo" size=40 maxlength="70" /></td>
						</table>
					</fieldset>
				</div>
				
			</div>
			
			<!-- Dialogo Firmantes -->
			<div id="dialog-firmantesUpdate" title="Actualizacion de Firmantes">
				<table>
					<tr>
						<td align="left">
							<input type="checkbox" id="oficioDelegatorioUpdate" name="oficioDelegatorioUpdate" onclick="showDivOficio(true);">Oficio Delegatorio Autoriza
						</td>
						<td align="left">
							<input type="checkbox" id="oficioDeleVoBoUpdate" name="oficioDeleVoBoUpdate" onclick="showDivOficioVoBo(true);">Oficio Delegatorio VoBo
						</td>	
					</tr>
				</table>
				<fieldset>
					<legend>Datos VoBo</legend>
					<table>
						<tr>
							<td>Nombre</td>
							<td><input type="text" id="cNombreVoBoUpdate"
								name="cNombreVoBoUpdate" size=40></td>
						</tr>
						<tr>
							<td>Apellido Paterno</td>
							<td><input type="text" id="cPaternoVoBoUpdate"
								name="cPaternoVoBoUpdate" size=40></td>
						</tr>
						<tr>
							<td>Apellido Materno</td>
							<td><input type="text" id="cMaternoVoBoUpdate"
								name="cMaternoVoBoUpdate" size=40></td>
						</tr>
						<tr>
							<td>Puesto</td>
							<td><input type="text" id="cPuestoVoBoUpdate"
								name="cPuestoVoBoUpdate" size=40></td>
						</tr>
					</table>
				</fieldset>
				<fieldset>
					<legend>Datos Autoriza</legend>
					<table>
						<tr>
							<td>Nombre</td>
							<td><input type="text" id="cNombreAutUpdate"
								name="cNombreAutUpdate" size=40></td>
						</tr>
						<tr>
							<td>Apellido Paterno</td>
							<td><input type="text" id="cPaternoAutUpdate"
								name="cPaternoAutUpdate" size=40></td>
						</tr>
						<tr>
							<td>Apellido Materno</td>
							<td><input type="text" id="cMaternoAutUpdate"
								name="cMaternoAutUpdate" size=40></td>
						</tr>
						<tr>
							<td>Puesto</td>
							<td><input type="text" id="cPuestoAutUpdate"
								name="cPuestoAutUpdate" size=40></td>
						</tr>
					</table>
				</fieldset>
				<fieldset>
					<legend>Datos Elabora</legend>
					<table>
						<tr>
							<td>Nombre</td>
							<td><input type="text" id="cNombreElaUpdate" name="cNombreElaUpdate"
								size=40>
							</td>
						</tr>
						<tr>
							<td>Apellido Paterno</td>
							<td><input type="text" id="cPaternoElaUpdate" name="cPaternoElaUpdate"
								size=40>
							</td>
						</tr>
						<tr>
							<td>Apellido Materno</td>
							<td><input type="text" id="cMaternoElaUpdate" name="cMaternoElaUpdate"
								size=40>
							</td>
						</tr>
						<tr>
							<td>Puesto</td>
							<td><input type="text" id="cPuestoElaUpdate" name="cPuestoElaUpdate"
								size=40>
							</td>
						</tr>
					</table>
				</fieldset>
				<div id="oficioDelegatorioCapturaUpdate" style="display: none">
					<fieldset>
						<legend>Datos del Suplente</legend>
						<table>
							<tr>
								<td>No. de Oficio</td>
								<td><input type="text" id="cFolioOficioUpdate"
									name="cFolioOficioUpdate" size=30 maxlength="70" />
								</td>
							</tr>
							<tr>
								<td>Fecha de Oficio</td>
								<td><input type="text" id="dFechaOficioUpdate"
									name="dFechaOficioUpdate" size=10 maxlength="10" />
								</td>

							</tr>
							<tr>
								<td>Nombre Suplente:</td>
								<td><input type="text" id="cNombreTitularUpdate"
									name="cNombreTitularUpdate" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Apellido Paterno</td>
								<td><input type="text" id="cApellidoPaternoTitularUpdate"
									name="cApellidoPaternoTitularUpdate" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Apellido Materno</td>
								<td><input type="text" id="cApellidoMaternoTitularUpdate"
									name="cApellidoMaternoTitularUpdate" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Puesto</td>
								<td><input type="text" id="cPuestoTitularUpdate"
									name="cPuestoTitularUpdate" size=40 maxlength="70" /></td>
						</table>
					</fieldset>
				</div>
				
				<div id="oficioDelegatorioVoBoUpdate" style="display: none">
					<fieldset>
						<legend>Datos del Suplente</legend>
						<table>
							<tr>
								<td>No. de Oficio</td>
								<td><input type="text" id="cFolioOficioVoBoUpdate"
									name="cFolioOficioVoBoUpdate" size=30 maxlength="70" />
								</td>
							</tr>
							<tr>
								<td>Fecha de Oficio</td>
								<td><input type="text" id="dFechaOficioVoBoUpdate"
									name="dFechaOficioVoBoUpdate" size=10 maxlength="10" />
								</td>

							</tr>
							<tr>
								<td>Nombre Suplente:</td>
								<td><input type="text" id="cNombreTitularVoBoUpdate"
									name="cNombreTitularVoBoUpdate" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Apellido Paterno</td>
								<td><input type="text" id="cApellidoPaternoTitularVoBoUpdate"
									name="cApellidoPaternoTitularVoBoUpdate" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Apellido Materno</td>
								<td><input type="text" id="cApellidoMaternoTitularVoBoUpdate"
									name="cApellidoMaternoTitularVoBoUpdate" size=40 maxlength="70" /></td>
							</tr>
							<tr>
								<td>Puesto</td>
								<td><input type="text" id="cPuestoTitularVoBoUpdate"
									name="cPuestoTitularVoBoUpdate" size=40 maxlength="70" /></td>
						</table>
					</fieldset>
				</div>
				
			</div>
			<!-- Fin Dialogo Firmantes -->
		</form>
	</div>
</body>
</html>