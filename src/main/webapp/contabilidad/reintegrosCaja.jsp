
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="utf-8"%>
	
<%@ page import="com.axtel.contabilidad.reintegrosCaja.core.ReintegrosCaja"%>
<%@ page import="com.axtel.contabilidad.reintegrosCaja.core.ReintegrosCajaEncabezado"%>
<%@ page import="com.axtel.contabilidad.reintegrosCaja.core.ReintegrosCajaDetalle"%>
<%@ page import="com.axtel.contabilidad.reintegrosCaja.ReintegrosCajaBusinessLogic"%>

<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="java.util.*"%>
<%@ page import="com.syc.gestion.core.*"%>
<%@ page import="com.syc.gestion.servlet.*"%>
<%@ page import="com.syc.gestion.util.*"%>
<%@ page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@ page import="com.syc.gestion.CasoBusinessLogic"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page import="com.syc.gestion.servlet.GestionServlet"%>
<%@ page import="java.io.File"%>
<%@ page import="org.apache.log4j.Logger"%>
<%@ page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@ page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@ page import="com.syc.sai.contabilidad.caja.CajaBusinessLogic"%>

<%!private static Logger log = Logger.getLogger("ReintegrosCaja.jsp");%>

<%
	Caso caso = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	if (caso == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String cUsrLog = "";
	String mensaje = "";
	String aEjercicioFiscal = EjercicioFiscalBusinessLogic.getEjercicioFiscal();
	String today = Util.getTodayESMX();
		
	Empleado empleado = new Empleado();
	EmpleadoBusinessLogic empleadobl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	ReintegrosCajaBusinessLogic reintegroCajaBL = new ReintegrosCajaBusinessLogic(GestionInterface.ATT_CONEXION);
	EmpleadoArea empleadoarea = new EmpleadoArea();
	CasoBusinessLogic casobl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	ReintegrosCaja  reintegroCajaCapturado = null;
	
	if( session.getAttribute("exito") != null ){
		if( (Boolean) session.getAttribute("exito") ){
			reintegroCajaCapturado = (ReintegrosCaja) session.getAttribute( "REINTEGROCAJA" );
			
			session.removeAttribute("REINTEGROCAJA");
		}else{
			String err = (String) session.getAttribute( "ERR_MSG" );
			if( StringUtils.isBlank(  err ) ){
				mensaje = "No se encontro informacion de la CxP";
			}else{
				mensaje = err;
				session.removeAttribute("ERR_MSG");
			}
		}
	}
	
	boolean esConsulta = ( caso == null? false : ( caso.getCasoOperacion(0) == null? false : (  caso.getCasoOperacion(0).getOperacion() == null ? false : ( "CONSULTA_REINTEGROCAJA".equalsIgnoreCase( caso.getCasoOperacion(0).getOperacion().getNombre() ) )  ) )  );
	String operacionActual = ( caso != null? (  caso.getCasoOperacion(0) != null? (  caso.getCasoOperacion(0).getOperacion() != null? caso.getCasoOperacion(0).getOperacion().getNombre() : ""   ) : "" ) : "" ) ;
	
	String cDocumento = (caso.getTipoCaso().getGavetaAsociada());	
	int id_oper = -1;
	boolean reload = true;
	int id_caso = -1;
	
	empleado.setClaveUsuario(usuario.getLogin());
	empleado = empleadobl.getEmpleado(empleado);
	empleadoarea.setId(empleado.getClaveArea());
	empleadoarea = empleadobl.getEmpleadoArea(empleadoarea);
	
	int nFolioReintegrocaja = new Integer(caso.getFolio().substring(caso.getFolio().lastIndexOf('-') + 1)).intValue();
	String c_folio = caso.getFolio();
	String fCreacion = Util.getTodayESMX(  );	
	
	id_caso = caso.getCasoOperacion(0).getIdCaso();
	
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = caso.getCasoOperacion(0).getIdOperacion();
	
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	
	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}
	
	cUR = usuario.getU_UR();
	
	//FIRMANTES
	String nNumEmpleado = usuario.getNumeroEmpleado();	
	
	String fAplicacion[] = CajaBusinessLogic.readfAplicacion(cCentroContable,cUR);
	System.out.println("0: Fecha de Aplicacion "+fAplicacion[0]);
	System.out.println("1: Fecha Minima "+fAplicacion[1]);
	System.out.println("2: Fecha Maxima "+fAplicacion[2]);
	
	ReintegrosCajaEncabezado rce = reintegroCajaBL.getReintegrosCajaEncabezado(nFolioReintegrocaja);
	ReintegrosCajaDetalle rcd = reintegroCajaBL.getReintegrosCajaDetalle(nFolioReintegrocaja);
	
	FortimaxFile[] solicitudFirmada = null; //id_oper 1 (EXCEL)
	int nIdDocumento = 0;
	int longitud = 0;
	
	nIdDocumento = casobl.buscaIdDocumento(caso.getTipoCaso().getGavetaAsociada(), caso.getIdGabinete(), 2, "Solicitud Firmada");	
	solicitudFirmada = casobl.getArchivosDeDocumento(caso.getTipoCaso().getGavetaAsociada(), caso.getIdGabinete(), 2, nIdDocumento);
	longitud = solicitudFirmada.length;
	System.out.println(longitud);
		
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Reintegros Caja</title>

<!-- Estilos estandar para los controles JQuery -->

<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<!-- FIRMANTES -->
<link rel="stylesheet" type="text/css" href="../Generador/css/resumenPago.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/ResumenFIEL.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../plantillasCasos/ComponentesPago/CSS/EgresoFirmantes.css"></link>
 
<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
 
<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../contabilidad/js/reintegrosCaja.js"></script>

<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<!-- FIRMANTES -->
<script type="text/javascript" src="../plantillasCasos/ComponentesPago/js/EgresoFirmantes.js"></script>
<!-- ADJUNTOS -->
<script type="text/javascript" src="../plantillasCasos/ComponentesPago/js/AdjuntaExpediente.js"></script>

<script type="text/javascript">

<!-- FIRMANTES -->
	var esConsulta = <%=esConsulta%>;
	var nNumEmpleado = "<%=nNumEmpleado%>";
	var cIdUsuarioCaptura = "<%=usuario.getLogin(  )%>";
	var nombreElaboro = "<%=empleado.getNombre()%>";
	var aPaternoElaboro = "<%=empleado.getApellidoPaterno()%>";
	var aMaternoElaboro = "<%=empleado.getApellidoMaterno()%>";
	var puestoElaboro = "<%=empleado.getCargo()%>";
	var idOperacionActual = <%=id_oper%>;
		
	$(document).ready(function() {			
		init();
		setFechas();
		
		//Si existen los datos guardados se muestran como solo lectura
		if(<%=rce != null%>){
			$("#cUnidad").val('<%=rcd!=null && rcd.getcUnidadResponsable()!=null ? rcd.getcUnidadResponsable() : ""%>');
			$("#ue").val('<%=rce!=null && rce.getcUnidadEjecutora()!=null ? rce.getcUnidadEjecutora() : ""%>');
			$("#cEventoOrigen").val('<%=rcd!=null && rcd.getcEvento()!=null ? rcd.getcEvento() : ""%>');
			$("#cEventoDestino").val('<%=rcd!=null && rcd.getcEventoDestino()!=null ? rcd.getcEventoDestino() : ""%>');
			$("#cConcepto").val('<%=rce!=null && rce.getcDescripcionPoliza()!=null ? rce.getcDescripcionPoliza() : ""%>').disabled;
			$("#mMonto").val('<%=rce!= null && rce.getmMontoSolicitud() != 0 ? rce.getmMontoSolicitud() : ""%>');
			$("#hbuscabeneficiario").val('<%=rcd!=null && rcd.getRFC()!=null ? rcd.getRFC() : ""%>');
			$("#cNombre").val('<%=rcd!=null && rcd.getdNombre()!=null ? rcd.getdNombre() : ""%>');
			$("#FFM").val('<%=rcd!=null && rcd.getFFM()!=null ? rcd.getFFM() : ""%>');
			$("#FFM").val('<%=rcd!=null && rcd.getdFFM()!=null ? rcd.getdFFM() : ""%>');
			$("#CTA_TODAS").val('<%=rcd!=null && rcd.getCTAB()!=null ? rcd.getCTAB() : ""%>');
			$("#nomCTAB").val('<%=rcd!=null && rcd.getnomCTAB()!=null ? rcd.getnomCTAB() : ""%>');
			$("#ctaBeneficiario").val('<%=rcd!=null && rcd.getnCuentaBeneficiario()!=null ? rcd.getnCuentaBeneficiario() : ""%>');
			$("#nCtaBeneficiario").val('<%=rcd!=null && rcd.getctaBeneficiario()!=null ? rcd.getctaBeneficiario() : ""%>');
			$("#txtmotivoRechazo").val('<%=rce!=null && rce.getcMotivoRechazo()!=null ? rce.getcMotivoRechazo() : ""%>');
			$("#fechaAplicacion").val('<%=rce!=null && rce.getfAplicacion()!=null ? rce.getfAplicacion() : ""%>');
					
		} else {
			$("#ue").val('<%=reintegroCajaCapturado!=null && reintegroCajaCapturado.getEncabezado()!=null ? reintegroCajaCapturado.getEncabezado().getcUnidadEjecutora() : ""%>');			
			$("#cConcepto").val('<%=reintegroCajaCapturado!=null && reintegroCajaCapturado.getEncabezado()!=null ? reintegroCajaCapturado.getEncabezado().getcDescripcionPoliza() : ""%>');			
			$("#mMonto").val('<%=reintegroCajaCapturado!=null && reintegroCajaCapturado.getEncabezado()!=null ? reintegroCajaCapturado.getEncabezado().getmMontoSolicitud() : ""%>');			
			
			$("#cUnidad").hide();
			$("#ctaBancaria").hide();
			$("#nombreCuenta").hide();		
			$("#nomCTAB").hide();
			$("#nCtaBeneficiario").hide();
						
			parent.document.getElementById("pb_save").disabled=false;
			parent.document.getElementById("pb_send").disabled=true;
			parent.document.getElementById("pb_cancel").disabled=false;
		}
		
		$("#CausaRegreso").dialog({
			autoOpen : false,
			height : 250,
			width : 450,
			modal : true,
			buttons : 
			{
				"Aceptar" : function() {	
					
					if(idOperacionActual == 1)
						$(this).dialog("close");
					 else
						{
						    $("#cMotivoRechazo").val( $("#txtmotivoRechazo").val());
						    queryFormPost("updateMotivoRechazoReintegroCaja",{async : false,callback : function() {}});
							
							$("#co_responsable").val("CAPTURA_REINTEGROCAJA");
							$("#id_oper").val("1");
							queryFormPost("UpdateCoperReintegroCaja", 
							{
							   async : false, 
					           callback : function() 
					            {update=true;}
				            });
        				   if(update)	
					       	document.liberardocumento.submit();	
        				   //$("#pb_cancel", parent.window.document).click();
				           else
				           {
				        	   Swal.fire({ icon: 'warning',
											text: "Reintente en un momento." });
					         return false;
				           }
						}
					
					$(this).dialog("close");
					return false;
								
				},
				
				"Cancelar":function(){
					$(this).dialog("close");
				}
			}
		
		});				
		
	});	
	
	function setFechas(){
		$("#fechaAplicacion").datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true,
			minDate:new Date(<%=fAplicacion[1]%>),
			maxDate:new Date(<%=fAplicacion[2]%>)
		});		
	}
		
	function onLoadPlantilla(id_oper){//CARGA PLANTILLA	
		$("#id_oper").val(<%=id_oper%>);
		$("#longitudDocumento").val( <%=longitud%>);
	
		if(id_oper == 1){//CAPTURA					
			//BUSCA SI EXISTE EL FOLIO EN LA TABLA SI NO 
			queryFormPost("tReintegroCajaExiste", {async:false});
		
			if(!$("#txtmotivoRechazo").val() == ""){//SI EXITE RECHAZO SE MUESTRA EL DIALOG DEL MOTIVO DE RECHAZO
				$("#btnRechazo").show();
				
				//SE DEJAN LOS CAMPOS DE SOLO LECTURA
				var concepto = document.getElementById("cConcepto");
				concepto.disabled = true;
				
				var monto = document.getElementById("mMonto");
				monto.disabled = true;
				
				showButtonsDetail();
				cat_CuentaBeneficiario();
				
				$("#btnBeneficiario").hide();
				$("#ctabBeneficiario").hide();
				$("#Limpiar").hide();	
								
				$("#ue").hide();
				$("#btnFFM").hide();
				$(".btnAyuda").each(
						function (){
							$(this).hide();
						}		
					);
				
				if($("#ctaBeneficiario").val() == ' '){				
					$("#trctabBene").hide();
				}
				
				$("#operaciones").hide();
				$("#tdAutorizar").hide();
				$("#ReImprimir").hide();
				$("#operacionesAutoriza").hide();				
			
				parent.document.getElementById("pb_save").disabled=true;
				parent.document.getElementById("pb_send").disabled=true;
				parent.document.getElementById("pb_cancel").disabled=false;
				
			} else { 				
				if($("#existe").val() == "SI"){
					//SE DEJAN LOS CAMPOS DE SOLO LECTURA
					var concepto = document.getElementById("cConcepto");
					concepto.disabled = true;
					
					var monto = document.getElementById("mMonto");
					monto.disabled = true;
					
					showButtonsDetail();
					cat_CuentaBeneficiario();
					
					$("#btnBeneficiario").hide();
					$("#ctabBeneficiario").hide();
					$("#Limpiar").hide();	
					
					$("#ue").hide();
					$("#btnFFM").hide();
					$(".btnAyuda").each(
							function (){
								$(this).hide();
							}		
						);
					
					if($("#ctaBeneficiario").val() == ' '){				
						$("#trctabBene").hide();
					}
					
					$("#operaciones").hide();
					$("#tdAutorizar").hide();
					$("#ReImprimir").hide();
											
					Swal.fire({ icon: 'info',
								text: "Los datos ya han sido guardados correctamente, avanza al siguiente estatus." })					
					//yA NO HABILITAR EL BOTON DE GUARDAR CUANDO YA SE ENCUENTRE APLICADO EL TRAMITE
					$("#pb_save", parent.window.document).click();
					
					parent.document.getElementById("pb_save").disabled=true;
					parent.document.getElementById("pb_send").disabled=false;
					
				} else {
					parent.document.getElementById("pb_save").disabled=false;
					parent.document.getElementById("pb_send").disabled=true;
				}
			}
			//MOSTRAR FIRMANTES
			$("#firmantesDiv").hide();
									
		}
		
		if(id_oper == 2){//REVISOR
			//APLICA PRIMER POLIZA
						
			if(!$("#txtmotivoRechazo").val() == "")
				$("#btnRechazo").show();
		
			//BUSCA SI YA ESTA APLICADO CON LA PRIMER POLIZA
			queryFormPost("tReintegroCajaAplicado", {async:false});
			if($("#cDocumentohAplicado").val() == "S"){
				$("#operaciones").show();
				$("#tdAutorizar").hide();
				$("#ReImprimir").show();				
				$("#operacionesAutoriza").hide();
				
				//SOLO SE HABILITA EL BOTON DE ENVIAR
				parent.document.getElementById("pb_save").disabled=true;
				parent.document.getElementById("pb_send").disabled=false;
								
				//alert("El tramite ya ha sido aplicado, avanza al siguiente estatus.")
				
				$("#pb_save", parent.window.document).click();
			}else{
				$("#operaciones").show();
				$("#tdAutorizar").show();
				$("#ReImprimir").hide();
				$("#operacionesAutoriza").hide();
				
				parent.document.getElementById("pb_save").disabled=true;
				parent.document.getElementById("pb_send").disabled=true;
			}												
			
			parent.document.getElementById("pb_cancel").disabled=true;
			
			//SE DEJAN LOS CAMPOS DE SOLO LECTURA
			var concepto = document.getElementById("cConcepto");
			concepto.disabled = true;
			
			var monto = document.getElementById("mMonto");
			monto.disabled = true;
			
			showButtonsDetail();
			cat_CuentaBeneficiario();
			
			$("#btnBeneficiario").hide();
			$("#ctabBeneficiario").hide();
			$("#Limpiar").hide();	
			
			$("#ue").hide();
			$("#btnFFM").hide();
			$(".btnAyuda").each(
					function (){
						$(this).hide();
					}		
				);
			
			if($("#ctaBeneficiario").val() == ' '){				
				$("#trctabBene").hide();
			}
			
			//MOSTRAR FIRMANTES
			$("#firmantesDiv").show();
			
			muestraEditaFirmantes();
			if( $("#nombreElabora").val() == "" ){
				$("#nombreElabora").val( $("#cNombreEla").val() + ' ' 
				                      +  $("#cPaternoEla").val() + ' ' 
				                      +  $("#cMaternoEla").val() ); 
				$("#puestoElabora").val($("#cPuestoEla").val());
			}
			$(".firmaElectronica").each(function(){
				$(this).show();
			});
				
		}
		
		if(id_oper == 3 ){//autorizador
			
			if(!$("#txtmotivoRechazo").val() == "")
				$("#btnRechazo").show();
			//BUSCA SI YA ESTA APLICADA LA SEGUNDA POLIZA 
			queryFormPost("tReintegroAutCajaAplicado", {async:false});
			if($("#cDocumentohAplicado").val() == "S"){			
				Swal.fire({ icon: 'info',
							text: "El tramite ya ha sido autorizado, se avanzara al siguiente estatus, consulta." })							
				
				parent.document.getElementById("pb_save").disabled=true;
				parent.document.getElementById("pb_send").disabled=false;
				
				$("#co_responsable").val(ResponsableSiguiente(id_oper));
				$("#id_oper").val(Id_OperacionSiguiente(id_oper));
				
				$("#pb_save", parent.window.document).click();//yA NO HABILITAR EL BOTON DE GUARDAR CUANDO YA SE ENCUENTRE APLICADO EL TRAMITE
			} else {			
				parent.document.getElementById("pb_save").disabled=false;
				parent.document.getElementById("pb_send").disabled=true;
			}
			
			$("#operaciones").hide();
			$("#operacionesAutoriza").show();
			
			parent.document.getElementById("pb_cancel").disabled=true;
			
			var concepto = document.getElementById("cConcepto");
			concepto.disabled = true;
			
			var monto = document.getElementById("mMonto");
			monto.disabled = true;
			
			showButtonsDetail();
			cat_CuentaBeneficiario();
			
			$("#btnBeneficiario").hide();
			$("#ctabBeneficiario").hide();
			$("#Limpiar").hide();	
			
			$("#ue").hide();
			$("#btnFFM").hide();
			$(".btnAyuda").each(
					function (){
						$(this).hide();
					}		
				);
			
			if($("#ctaBeneficiario").val() == ' '){				
				$("#trctabBene").hide();
			}
			
			$("#firmantesDiv").show();
			
			var esFIEL = esFirmaElectronica();
			if (esFIEL) {
				muestraResumenFirmas();
			} else {
				$("#ReIimprimirBtn").button().click(function(){
					cmdImprimir('PolizaPago');
				});
								
				muestraEditaFirmantes();
				$("#ReImprimir").show();
			}
						
		}
		
		if(id_oper == 4){//consulta
			
			if(!$("#txtmotivoRechazo").val() == "")
				$("#btnRechazo").show();
				
			var concepto = document.getElementById("cConcepto");
			concepto.disabled = true;
			
			var monto = document.getElementById("mMonto");
			monto.disabled = true;
			
			showButtonsDetail();
			cat_CuentaBeneficiario();
			
			$("#btnBeneficiario").hide();
			$("#ctabBeneficiario").hide();
			$("#Limpiar").hide();	
			
			$("#ue").hide();
			$("#btnFFM").hide();
			$(".btnAyuda").each(
					function (){
						$(this).hide();
					}		
				);
			
			if($("#ctaBeneficiario").val() == ' '){				
				$("#trctabBene").hide();
			}
			
			$("#firmantesDiv").show();
			var esFIEL = esFirmaElectronica();
			if (esFIEL) {
				muestraResumenFirmas();
			} else {
				$("#imprimirBtn").button().click(function(){
					cmdImprimir('PolizaPago');
				});
								
				muestraEditaFirmantes();
				$("#operaciones").show();
				$("#tdAutorizar").hide();
				$("#ReImprimir").show();
				$("#operacionesAutoriza").hide();				
			}
						
			parent.document.getElementById("pb_save").disabled=false;
			parent.document.getElementById("pb_send").disabled=true;
			parent.document.getElementById("pb_cancel").disabled=true;
		
		}
	}
		
</script>
</head>

<body id="dt_example" >
	<form action="" method="post" id="FormReintegroCaja" name="FormReintegroCaja">		
		<input type="hidden" id="id_tc" name="id_tc" value="<%=caso.getIdTC(  )%>"/>		
		<input type="hidden" id="operador" name="operador" value="<%=usuario.getNombre(  )%>"/>
		<input type="hidden" id="u_login" name="u_login" value="<%=usuario.getLogin(  )%>"/>		
		<input type="hidden" id="nFolioReintegrocaja" name="nFolioReintegrocaja" value="<%=nFolioReintegrocaja%>"/>		
		<input type="hidden" id="ejercicioFiscal" name="ejercicioFiscal" value="<%=aEjercicioFiscal%>"/>		
		<input type="hidden" id="cUR" name="cUR" value="<%=cUR%>"/>
		<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>"/>		
		<input type="hidden" id="cTipoRfc" name="cTipoRfc" value="0,1,2,3,4,5,7">
		<input type="hidden" id="id" name="id" value=" " />	
		<input type="hidden" id="dEvento" name="dEvento" value="">
		<input type="hidden" id="nidgrupoevento" name="nidgrupoevento" value="">
		<input type="hidden" id="dEventoDestino" name="dEventoDestino" value="">
		<input type="hidden" id="nidgrupoeventoDestino" name="nidgrupoeventoDestino" value="">
		<input type="hidden" id="cidsubgrupoevento" name="cidsubgrupoevento" value="">
		<input type="hidden" id="id_oper" name="id_oper" value="<%=id_oper%>">
		<input type="hidden" id="FOLIO" name="FOLIO" value="<%=c_folio%>">		
		<input type="hidden" id="id_caso" name="id_caso" value="<%=id_caso%>">
		<input type="hidden" id="cDocumentohAplicado" name="cDocumentohAplicado" value="">
		<input type="hidden" id="cMotivoRechazo" name="cMotivoRechazo" value="" />
		<input type="hidden" id="co_responsable" name="co_responsable" value="" />		
		<input type="hidden" id="existe" name="existe" value="" />
		<input type="hidden" id="longitudDocumento" name="longitudDocumento" value="<%=longitud %>" />
				
	<!-- FIRMANTES -->
		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>">		
		<input type="hidden" id="cDocumento" name="cDocumento" value="<%=cDocumento%>"/>
		<input type="hidden" id="cNombreEla" name="cNombreEla"/>
		<input type="hidden" id="cPaternoEla" name="cPaternoEla"/>
		<input type="hidden" id="cMaternoEla" name="cMaternoEla"/>
		<input type="hidden" id="cPuestoEla" name="cPuestoEla"/>
		<input type="hidden" id="nEnviadoSICOP" name="nEnviadoSICOP">
		<input type="hidden" id="cEsFirmaElectronica" name="cEsFirmaElectronica"/>
		<input type="hidden" id="nNumEmpleadoElab" name="nNumEmpleadoElab" value="-1"/>
		<input type="hidden" id="cTipoFirmante" name="cTipoFirmante" value=""/>
		<input type="hidden" id="cTipoPago" name="cTipoPago" value="REINTEGROCAJA"/>
		<input type="hidden" id="TITULO_APLICACION" name="TITULO_APLICACION" value="REINTEGROCAJA"/>		
		<input type="hidden" id="cIdUsuarioCaptura" name="cIdUsuarioCaptura" value=""/>
		<input type="hidden" id="nFolioPago" name="nFolioPago" value="<%=nFolioReintegrocaja%>"/>
		<input type="hidden" id="rechazaAutorizador" name="rechazaAutorizador" value="NO"/>
		
		<div id="container" class="container" style="width: 80%">
			<div class="card-header"> <h3>Reintegros de Caja</h3> </div>					
			<hr class="mt-3">
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>				
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">											
					<label for="cFolio" class="form-label"> Folio: </label>						
					<input type="text" id="cFolio" name="cFolio" class="form-control form-control-sm" style="width: 12em;" value="<%=c_folio%>" readonly/>																
					<span id="btnRechazo" ><a href="#" onclick="mostrarMotivoRechazo();" >Ver Motivo del rechazo</a></span>
				</div>
			
				<div class="col-12 col-lg-4 col-md-4 col-sm-12">
					<label for="fechaCaptura" class="form-label"> Fecha de captura: </label>	
					<input type="text"	id="fechaCaptura" name="fechaCaptura" class="form-control form-control-sm" style="width: 10em;" value="<%=fCreacion%>" readonly>						
				</div>

				<div class="col-12 col-lg-4 col-md-4 col-sm-12">
					<label for="fechaAplicacion" class="form-label"> Fecha de aplicaci&oacute;n:</label>						
					<input type="text"	id="fechaAplicacion" name="fechaAplicacion" class="form-control form-control-sm" style="width: 10em;" value="<%=fAplicacion[0]%>" readonly>							
				</div>																
			</div>
			
			<br/>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">	
					<label for="ue" class="form-label"> Unidad Ejecutora: </label>						
					<select id="ue" name="ue" class="form-select form-select-sm">
							<option value="Z:">A</option>
					</select>
					<input type="text" class="form-control form-control-sm" size="50" id="cUnidad" name="cUnidad" readonly>						
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12">					
					<label for="EventoOrigen" class="form-label"> Evento Origen: </label>		
					<div class="input-group">
						<input type="text" id="cEventoOrigen" name="cEventoOrigen" class="form-control AyudaSyC form-control-sm" style="width: 10em !important;flex: none;" onchange="cambioEvento()" readonly>
					</div>
				</div>	
									
				<div class="col-12 col-lg-4 col-md-4 col-sm-12">
					<label for="cEventoDestino" class="form-label"> Evento Destino: </label>
					<div class="input-group">						
						<input type="text" id="cEventoDestino" name="cEventoDestino" class="form-control AyudaSyC form-control-sm" style="width: 10em !important;flex: none;" onchange="showButtonsDetail()" onclick="validaEventoOrigen()" readonly>
					</div>						
				</div>								
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">	
					<label for="cConcepto" class="form-label"> Concepto: </label>				
					<textarea id="cConcepto" name="cConcepto" class="form-control form-control-sm"></textarea>
				</div>
			</div>
		
			<br/>
		
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-6 col-md-6 col-sm-12" id="trCTABAN">										
					<label for="CTA_TODAS" class="form-label"> Cuenta Bancaria: </label>	
					<div class="input-group">				
						<span class="input-group-text"><i class="bi bi-bank"></i></span>		
						<input type="text" id="CTA_TODAS" name="CTA_TODAS" class="form-control AyudaSyC form-control-sm" style="width: 15em !important;flex: none;" readonly>																			 						
						<input type="text" class="form-control form-control-sm" size="35" id="nomCTAB" name="nomCTAB" readonly>
					</div>						
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-6 col-md-6 col-sm-12" id="trhbuscabeneficiario">																	
					<label for="hbuscabeneficiario" class="form-label"> Beneficiario: </label>	
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-person"></i></span>					
						<input type="text" id="hbuscabeneficiario" name="hbuscabeneficiario" class="form-control form-control-sm" style="width: 18em !important;flex: none;" onchange="cat_CuentaBeneficiario()" readonly/>						
						<input type="button" class="btn btn-secondary btn-sm" id="btnBeneficiario" value="..." onclick="ayudaBeneficiarios()">&nbsp;&nbsp;		
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-person"></i></span>																																																		
							<input type="text" id="cNombre" name="cNombre" class="form-control form-control-sm" style="width: 20em" readonly/>
						</div>
					</div>										
				</div>
		
				<div class="col-12 col-lg-4 col-md-4 col-sm-12" id="trctabBene">							
					<label for="ctabBeneficiario" class="form-label"> Cuenta Bancaria del Beneficiario: </label>	
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-bank"></i></span>						
						<select name="ctabBeneficiario" id="ctabBeneficiario" class="form-select form-select-sm">
			  		   	<option value="NA">-N/A-</option>
			  	    	</select>
			  	    </div>
				  	<input type="text" id="ctaBeneficiario" name="ctaBeneficiario" class="form-control form-control-sm" style="width: 20em" readonly>
					  	
				  	<div class="col-12 col-lg-6 col-md-6 col-sm-12" id="trctabBene" id="nombreCuenta">	
				  		<div class="input-group">
							<span class="input-group-text"><i class="bi bi-bank"></i></span>			  		
				  			<input type="text" id="nCtaBeneficiario" name="nCtaBeneficiario" class="form-control form-control-sm" readonly>
				  		</div>
				  	</div>				  							
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-6 col-md-6 col-sm-12" id="trFFM">							
					<label for="FFM" class="form-label"> FFM: </label>
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-bookmark"></i></span>		
						<input type="text" id="FFM" name="FFM" class="form-control form-control-sm" style="width: 18em !important;flex: none;" readonly>
						<input type="button" class="btn btn-secondary btn-sm" id="btnFFM" value="..." onclick="cat_FFM()">
					</div>										
				</div>
			</div>																									
				
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">							
					<label for="mMonto" class="form-label"> Monto: </label>		
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>					
						<input  type="text" id="mMonto" name="mMonto" class="form-control form-control-sm" style="width: 15em;" placeholder="0.00">
					</div>														
				</div>
			</div>
			
			<br/>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-6 col-md-6 col-sm-12">						
					<input type="button" class="btn btn-outline-primary btn-sm" value="Limpiar Datos" id="Limpiar" onClick="limpiarDatos()" />																									
				</div>
			</div>				
	
			<div id="CausaRegreso" title="Motivo de rechazo" class="container">				
				<textarea rows="5" cols="45" id="txtmotivoRechazo" class="form-control form-control-sm"></textarea>											
			</div>
			
			<div id="operaciones" class="container">			
				<h5> Operaciones </h5>
				<hr class="mt-3">
				
				<div class="row" id="tdAutorizar">
					<div class="col-12 d-flex justify-content-center">
						<label class="form-label"> ¿Datos correctos?: </label>&nbsp;&nbsp;
						<label for="autorizaSi" class="form-check-label">Si: </label> 
						<input type="radio" name="grpAutorizar" id="grpAutorizar" class="form-check-input" checked value="Si" />&nbsp;&nbsp;
						
						<label for="autorizaNo" class="form-check-label">No: </label>&nbsp;&nbsp;
						<input type="radio" name="grpAutorizar" id="grpAutorizar" class="form-check-input" value="No" />&nbsp;&nbsp;
						<input type="button" class="btn btn-secondary btn-sm" value="Aplicar Tramite" id="enviar" onClick="EnviarFirma()"/>
					</div>																		
				</div>
				
				<br/>
				
				<div class="row" id="ReImprimir">
					<div class="col-12 d-flex justify-content-center">
						<input type="button" class="btn btn-secondary btn-sm" value="Imprimir Solicitud" id="ReImprimirBtn" onClick="cmdReImprime()" />											
					</div>
				</div>
				
				<br/>
			</div>
					
			 <div id="operacionesAutoriza" class="container">			
				<h5> Operaciones Autoriza </h5>
				<hr class="mt-3">
				
				<div class="row" id="tdAutorizarAut">
					<div class="col-12 d-flex justify-content-center" id="tdAutorizarAut">
						<label class="form-label"> ¿Datos correctos?: </label>&nbsp;&nbsp;
						<label for="autorizaAutSi" class="form-check-label">Si: </label> 
						<input type="radio" name="grpAutorizarAut" id="grpAutorizarAut" class="form-check-input" checked value="Si" />&nbsp;&nbsp;
						
						<label for="autorizaAutNo" class="form-check-label">No: </label> &nbsp;&nbsp;
						<input type="radio" name="grpAutorizarAut" id="grpAutorizarAut" class="form-check-input" value="No" />																						
					</div>					
				</div>			
			</div>
			 		
		</div>					
		
		<br/>
		<!-- JSP para firmantes -->			
		<div id="firmantesDiv" class="container" style="width: 80%" >	
			<div class= "card">
				<div class="card-body">	
					<h5> Seleccione los Firmantes </h5>
					<hr class="mt-3">
								
					<jsp:include page="../plantillasCasos/ComponentesPago/EgresoFirmantesBs.jsp"></jsp:include>
				</div>
			</div>
		</div>
						
	</form>
	
	<form id="liberardocumento" name="liberardocumento"
			action="../gstnmngr/gestion?cmd=1" target="content-iframe"
			method="post">
	</form>

</body>

</html>