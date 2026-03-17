<%@page import="com.syc.contable.ReintegrosAnexo1BusinessLogic"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="java.util.*"%>
<%@page	import="com.syc.gestion.core.*"%>
<%@page	import="com.syc.gestion.servlet.*"%>
<%@page	import="com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.ReintegrosBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.ReintegroEncabezado"%>
<%@page import="com.syc.contable.core.ReintegroDetalle"%>
<%@page import="com.syc.contable.core.ReintegroEncabezadoMil"%>
<%@page import="com.jenkov.prizetags.tree.itf.ITree"%>
<%
	
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	
	String cCentroContable = "";
	String aEjercicioFiscal = "";
	boolean bErrorAdec=false;
	
	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
	
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

	final int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
	String msVariable = c.getCasoDato("MENSAJE").getValor(); //Recuperamos el mensaje del caso
	
	ReintegrosAnexo1BusinessLogic reintegro = new ReintegrosAnexo1BusinessLogic(GestionInterface.ATT_CONEXION);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	if (request.getParameter("pago") != null && request.getParameter("pago").equals("Si")) {
	    boolean a = reintegro.actualizaInfoPagos(folio,request.getParameter("rastreo"),request.getParameter("lcaptura"),request.getParameter("fichaDep"),request.getParameter("clvBanco"),request.getParameter("cuenta"),request.getParameter("fAcredit"),request.getParameter("ctab"),request.getParameter("aviso"),request.getParameter("folioDep"));
	}
	
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	FortimaxFile[] archivoExcel = null; //id_oper 1 (EXCEL)
	FortimaxFile[] archivoLineaCaptura = null; //id_oper 5 (LINEA DE CAPTURA)
	FortimaxFile[] archivoPDFComprobante = null; //id_oper 5 (COMPROBANTE)
	FortimaxFile[] archivoPDFCLC = null; //id_oper 3 (COMPLEMENTARIO)
	FortimaxFile[] archivoPDFCXP = null; //id_oper 3 (COMPLEMENTARIO)
	FortimaxFile[] archivoReporteSicop = null; //id_oper 6 (REPORTES)
	FortimaxFile[] archivoReporteSiaff = null; //id_oper 6 (REPORTES)
	
	archivoExcel = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1); //el numero que está primero indica el num de carpeta
	//la raiz es la 0, se puede ver con el numero que viene después de la c al pasar el mouse en el arcbol. El segundo numero es el numero de archivo dentro de esa carpeta, empieza en el 1.
	archivoPDFCLC = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 3, 1);
	archivoPDFCXP = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 3, 2);
	archivoLineaCaptura = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 4, 1);
	archivoPDFComprobante = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 5, 1);
	archivoReporteSicop = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 6, 1);
	archivoReporteSiaff = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 6, 2);

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
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	//Valida Centro de Costos
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	
	String mensajeError="";
	try{
	    if (request.getParameter("leeExcel") != null && request.getParameter("leeExcel").equals("1")) {
			if(cbl.getArchivosDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1).length>0)
			    reintegro.leeArchivoExcel(cbl.getArchivosDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1)[0].getAbsolutePath(),c,usuario,folio);
		}
	}catch(Exception ex){
	 	mensajeError = ex.getMessage();   
	}
	
	ReintegroEncabezadoMil re = reintegro.getReintegroEncabezadoNuevo(folio);
	
	CasoBusinessLogic casoTx = new CasoBusinessLogic("jdbc/gestion");
	ITree tree = casoTx.getArbolCaso(c);
	session.setAttribute("tree.model", tree);
	
	%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Reintegro de Solicitud de Recursos x Pagar</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
		<link type="text/css" rel="stylesheet" href="../css/themes/base/jquery.ui.all.css" />
		<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />

		<style type="text/css" title="currentStyle">
			@import "../Generador/css/demo_page.css";
			@import "../Generador/css/demo_table_jui.css";
			@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
		</style>
		
		<style>
			.notEditable {
				background-color: #CCCCCC;
				color: #000000;
			}
		</style>
		
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.core"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/ui/jquery.ui.accordion.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>

		<script type="text/javascript" charset="utf-8">
		var muestraAplicar = true;
		var clickEnviar = false;
		
		function onPostSubmit(id_oper){//validaciones del boton enviar
			//var valida_doctos_requeridos = true;
		  	//validaciones de documentos requeridos
		  	//return valida_doctos_requeridos;
		  	clickEnviar=true;
			if (id_oper==1 && <%=archivoPDFCXP.length==0%>){ 
				alert("Para poder continuar debe de adjuntar la Solicitud Firmada(PDF CXP)");
				return false;
			}
			if(id_oper==1){
				if(<%="1".equals(re!=null?re.getTipoAviso():"")%>){
					if($("#CTABAN").val()==""){
						alert("Favor de actualizar la cuenta bancaria");
						return;
					}else
				  		actualizaPagoInfo();
			  	}
				if(!clickEnviar){
					parent.document.getElementById("pb_send").disabled=false;
					clickEnviar = true;
				}else{
					parent.document.getElementById("pb_send").disabled=true;
				}
			}else if(id_oper==2){
				if (document.datosReintegro.autorizaRein[1].checked)
					queryFormPost("tReintegroAnexo1UpdateCancelado", {async:false});
				/*
				if(<%="1".equals(re!=null?re.getTipoAviso():"")%>){
					if (!document.datosReintegro.autorizaRein[1].checked){					
						if($("#clvRastreo").val()=='N/A' || $("#deposito").val()=='N/A'){
							alert("Favor de actualizar la clave de rastreo y la ficha de deposito");
							return;
						}else if($("#lineaCap").val()=='N/A' ){
							alert("Favor de actualizar la linea de captura.");
							return;
						}else
				*/
					  		actualizaPagoInfo();
				/*	}
			  	}*/
			}
				
			if(!clickEnviar){
				parent.document.getElementById("pb_send").disabled=false;
				clickEnviar = true;
			}else{
				parent.document.getElementById("pb_send").disabled=true;
				parent.document.getElementById("pb_save").disabled=true;
				parent.document.getElementById("pb_leave").disabled=true;
			}
		  	return true;
		}
		
		function ResponsableSiguiente(id_oper){
			if(id_oper==1)
	  		 	return "AUTORIZA_REINTEGROANEXO1";
	  		if(id_oper==2)
	  		 	return "CONSULTA_REINTEGROANEXO1";
		 }
		
		function OperacionSiguiente(id_oper){
			if(id_oper==1)
				return "autoriza_reintegoranexo1";
			if(id_oper==2)
				return "consulta_reintegroanexo1";
  		 }
		
		function onPostDisplay(id_oper){}

		function onSubmit(id_oper){//validaciones del boton guardar
		  	var p = window.parent;
		  	var valida_campos = true;
			try{
		  		if (id_oper==1){
		  			p.gestion.setFolio($("#FOLIO").val());
					p.gestion.setOperador($("#OPERADOR").val());
					p.gestion.setFechaDocumento($("#FECHA_SOLICITUD").val());
					p.gestion.setEjercicioFiscal($("#EJERCICIO_FISCAL").val());
					p.gestion.setConceptoMov("Reintegro Anexo 1");
					p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
					p.gestion.setFechaApCont($("#FECHA_APLICACION_CONTABLE").val());
					if(<%=re!=null%>)
						updateFirmantes();
				}
					
		  		if (id_oper==2){
		  			if(document.datosReintegro.autorizaRein[1].checked && $("#motivoRechazo").val()==""){
			  			alert("Motivo de rechazo es requerido");
			  			return false;
		  			}else if(document.datosReintegro.autorizaRein[1].checked){
		  				parent.document.getElementById("pb_send").disabled=false;
		  				parent.document.getElementById("pb_save").disabled=true;
		  				$("#motivoR").val($("#motivoRechazo").val());
		  				$("#mensajeError").val($("#motivoRechazo").val());
		  				$("#nFolioR").val(<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>);
		  				queryFormPost("tReintegroAnexo1EncabezadoUpdate", {async:false});
		  			}else if(!(document.datosReintegro.autorizaRein[0].checked || document.datosReintegro.autorizaRein[1].checked)){
		  				alert("Favor de marcar si los datos son correctos o no");
		  				return false;
		  			}else{
		  				if(<%=("true".equals(c.getCasoDato("APLICADO_CONT").getValor())) && c.getCasoDato("APLICADO_CONT").getValor()!=null%>){
							parent.document.getElementById("pb_send").disabled=true;
						}
		  			}
	  			}
			
		  		if(id_oper == 3){
		  			parent.document.getElementById("pb_send").disabled=false;
		  		}
		  		
	  		}catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}
			return valida_campos;
	  	}
	
		function onLoadPlantilla(id_oper){
			if (<%=id_oper%> == 3){
				$("#divImprimePoliza").show();
				$("#EditaFirmas").css('visibility', 'visible');
			}else if (<%=id_oper%> == 1)
				$("#divImprimePoliza").show(); 
			else
				$("#divImprimePoliza").hide();
			  		
			if(<%=mensajeError!=null && !"".equals(mensajeError)%>){
				$("#textmensajeError").val('<%=mensajeError.replace("'","")%>');
				$('#dlgError').dialog('option', 'modal', true).dialog('open');
			}
			  
			<% if(mensaje==null || "".equals(mensaje.trim()) && (id_oper>1)){%>
				if(parent.document.getElementById("pb_cancel"))
			  		parent.document.getElementById("pb_cancel").disabled=true;
			<%}%>
			  
			if(<%=id_oper%>  == 1){
				if(<%="1".equals(request.getParameter("avanzai"))%>){
					if(!clickEnviar){
						parent.document.getElementById("pb_send").disabled=false;
						clickEnviar = true;
					}else{
						parent.document.getElementById("pb_send").disabled=true;
					}
					parent.document.getElementById("pb_save").disabled=true;
				}else{
					parent.document.getElementById("pb_send").disabled=true;
					parent.document.getElementById("pb_save").disabled=false;
				}

				if($("#mensajeError").val()!=""){
					alert($("#mensajeError").val());
				}
		  		
				if(<%=c.getIdGabinete()%>==-1)
	  				parent.document.getElementById("pb_save").click();//esto es para asegurar que se crea el expediente antes de subir el archivo
			}
			 	
	 		$("#importe").val('<%=re!=null?re.getImporteLC():""%>');
	  		$("#aviso").val('<%=(re!=null && re.getAviso()!=null)?re.getAviso():"N/A"%>');
	  		$("#CatMovimientoReintegro").val('<%=re!=null?re.getMovimiento():"N/A"%>');
	  		$("#CatTipoCausaAvisoReintegro").val('<%=re!=null?re.getTipoAviso():"N/A"%>');
	  		$("#CatFormaPagoAvisoReintegro").val('<%=re!=null?re.getFormaDePago():"N/A"%>');
	  		$("#CatCausaAvisoReintegro").val('<%=re!=null?re.getCausaAviso():"N/A"%>');
	  		$("#fechaAp").val('<%=re!=null?re.getfAplicacion():"N/A"%>');
	  		$("#fechaAcredit").val('<%=re!=null?re.getfAcreditacion():"N/A"%>');
	  		$("#clvRastreo").val('<%=re!=null?re.getClvRastreo():"N/A"%>');			  			
	  		$("#deposito").val('<%=re!=null?re.getFichaDeposito():"N/A"%>');
	  		$("#lineaCap").val('<%=re!=null?re.getLc():"N/A"%>');
	  		$("#cveBanco").val('<%=re!=null?re.getClvBanco():"N/A"%>');
	  		$("#cuenta").val('<%=re!=null?re.getCuentaBancaria():"N/A"%>');			  			
	  		$("#observaciones").val('<%=re!=null?re.getObservaciones():"N/A"%>');
	  		$("#concepto").val('<%=re!=null?re.getConcepto():"N/A"%>');
	  		$("#cxp").val('<%=re!=null?re.getCuentaPorPagar():"N/A"%>');
	  		$("#folioDep").val('<%=re!=null?re.getFolioDependencia():"N/A"%>');
			
			if($("#fechaAp").val()=='null')
	  			$("#fechaAp").val('N/A');
	  		if($("#folioDep").val()=='null')
	  			$("#folioDep").val('N/A');
	  		if($("#clvRastreo").val()=='null')
	  			$("#clvRastreo").val('N/A');
	  		if($("#deposito").val()=='null')
	  			$("#deposito").val('N/A');
	  		if($("#lineaCap").val()=='null')
	  			$("#lineaCap").val('N/A');
	  		if($("#cuenta").val()=='null')
	  			$("#cuenta").val('N/A');
	  		if($("#cveBanco").val()=='null')
	  			$("#cveBanco").val('N/A');
	  		if($("#fechaAcredit").val()=='null')
	  			$("#fechaAcredit").val('N/A');
			 			
			if(<%=id_oper%> == 2){
		  		$("#fechaEx").attr('disabled',true);
		  		$("#fechaAcredit").attr('disabled',false);				  		
		  		$("#CTABAN").attr('disabled',true);
		  		if(parent.document.getElementById("pb_cancel"))
	  				parent.document.getElementById("pb_cancel").disabled=true;
	  		}
			if(<%=id_oper%>  > 1){ //se deshabilitan campos que sólo puede modificar el capturista
				$("#clc").attr('disabled','disabled');
	  			$("#importe").attr('disabled','disabled');
	  			$("#aviso").attr('disabled','disabled');
	  			$("#CatMovimientoReintegro").attr('disabled','disabled');
	  			$("#CatTipoCausaAvisoReintegro").attr('disabled','disabled');
	  			$("#CatFormaPagoAvisoReintegro").attr('disabled','disabled');
	  			$("#CatCausaAvisoReintegro").attr('disabled','disabled');
	  			$("#txtGridTipoCLC").attr('disabled','disabled');
	  			$("#fechaAp").attr('disabled','disabled');
	  			//$("#fechaAcredit").attr('disabled','disabled');
	  			//$("#cveBanco").attr('disabled','disabled');
	  			//$("#cuenta").attr('disabled','disabled');
	  			$("#folioDep").attr('disabled','disabled');
	  			destroyDatePickers();
	  			
	  			$("#CTABAN").attr('disabled','disabled');
	  			$("#observaciones").attr('disabled','disabled');
	  			$("#concepto").attr('disabled','disabled');
			}
			if(<%=id_oper%>  == 3){ //consulta
				
				if(parent.document.getElementById("pb_cancel"))
					parent.document.getElementById("pb_cancel").disabled=true;
				$("#fechaAcredit").attr('disabled','disabled');
				
				$("#aviso").attr('disabled','disabled');
				$("#folioDep").attr('disabled','disabled');
				$("#fechaAp").attr('disabled','disabled');
				$("#clvRastreo").attr('disabled','disabled');
				$("#deposito").attr('disabled','disabled');
				$("#cveBanco").attr('disabled','disabled');
				$("#cuenta").attr('disabled','disabled');
				$("#lineaCap").attr('disabled','disabled');
				
				parent.document.getElementById("pb_save").disabled=true;
				parent.document.getElementById("pb_send").disabled=true;
			}
			if(<%=id_oper%> > 2){ //a partir de la autorizacion ya no se puede descartar
				
			}
	  	}
  		
  	function setDatePickers(){
	  	$( "#fechaSol" ).datepicker({
			dateFormat: "dd/mm/yy",
			showOn: "button",
			buttonImage: "../Generador/images/calendar.gif",
			buttonImageOnly: true
		});
		$( "#fechaEx" ).datepicker({
			dateFormat: "dd/mm/yy",
			showOn: "button",
			buttonImage: "../Generador/images/calendar.gif",
			buttonImageOnly: true
		});
		$( "#fechaAp" ).datepicker({
			dateFormat: "dd/mm/yy",
			showOn: "button",
			buttonImage: "../Generador/images/calendar.gif",
			buttonImageOnly: true
		});
		$( "#fechaAcredit" ).datepicker({
			dateFormat: "dd/mm/yy",
			showOn: "button",
			buttonImage: "../Generador/images/calendar.gif",
			buttonImageOnly: true
		});
	}

  	function destroyDatePickers(){
	  	$( "#fechaSol" ).datepicker("destroy");
		$( "#fechaEx" ).datepicker("destroy");
	  	$( "#fechaAp" ).datepicker("destroy");
	}
  	
	$(document).ready(function(){
		setDatePickers();
		$("#ImportarExcel").button();
		$("#borrarTodo").button();
		var $dialog;
		var p = window.parent;
      			//PageInit();
      	$(function() {		
      		$('#dialog').dialog({
      			autoOpen: false,
      			width: 900,
      			heigth: 2900
    		});
    	});
			    
		var $dlgError;
		$(function(){
			$('#dlgError').dialog({
				autoOpen: <%=bErrorAdec%>,
					width: 1200,
					heigth: 1800,
					close: function() {
		      			document.location.reload();
					}
				});
			});
		$(function() {		
		    $('#dialogMotor').dialog({
		      	autoOpen: false,
	  			width: 900,
	 			heigth: 2900
		    });
		});
				
		$("input.AyudaSyC").subIniciaDlg();
 		$('.currency').blur(function(){
			$('.currency').formatCurrency();
		});
 		//obtiene datos del reintegro detalle
		oTable = $('#tblReintegros').dataTable({
			"bPaginate": true,
   			"bLengthChange": true,
   			"bFilter": false,
   			"bSort": true,
   			"bInfo": true,
   			"bAutoWidth": false,
			"sScrollY": 270,
			"sScrollYInner": "100%",
			"bJQueryUI": true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers",
			"sScrollX": "100%",
			"sScrollXInner": "110%",
			"bScrollCollapse": true,	
			"bServerSide": true,   
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vtreintegroAnexo1Det&qw=nFolioReintegroAnexo1="+<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>,
			aoColumns: [
				{ sName: "noCLC" },
				{ sName: "secCLC" },
				{ sName: "EP"},
				{ sName: "mImporte"},
				{ sName: "cxp"}
			],
			oLanguage: {
				sProcessing: "Procesando...",
				sLengthMenu: "Mostrar _MENU_ registros",
				sZeroRecords: "No hay registros a mostrar",
				sEmptyTable: "No hay datos en la tabla",
				sLoadingRecords: "Cargando...",
				sInfo: "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty: "Registro 0 al 0 de 0",
				sInfoFiltered: "(filtered from _MAX_ total entries)",
				sInfoPostFix: "",
				sInfoThousands: ",",
				sSearch: "Filtro:",
				oPaginate: {
					sFirst:    "Primero",
					sPrevious: "Ant.",
					sNext:     "Sigte.",
					sLast:     "&Uacute;ltimo"
				}
			}
			
		});

		$("#tblReintegros tbody").click(function(event) {
			$(event.target.parentNode).addClass('row_selected');
		});

		if(<%=id_oper%>==2){
			if(<%=("true".equals(c.getCasoDato("APLICADO_CONT").getValor())) && c.getCasoDato("APLICADO_CONT").getValor()!=null%>){
				document.datosReintegro.autorizaRein[1].disabled=true;
			}
			$('#autorizaRein').click(function(){
				if(<%=!("true".equals(c.getCasoDato("APLICADO_CONT").getValor())) && c.getCasoDato("APLICADO_CONT").getValor()!=null%>){
					/*VALIDACION DE ARCHIVOS ADJUNTOS ANTES DE APLICAR
					if (<%=archivoPDFComprobante.length==0%> || <%=archivoLineaCaptura.length==0%> || <%=archivoPDFCLC.length==0%> || <%=archivoReporteSiaff.length==0%> || <%=archivoPDFCXP.length==0%> ){ 
						alert("Para poder continuar debe de adjuntar los archivos siguientes:\n\nComprobande de Pago\nLinea de Captura\nOficios\nReporte SIAFF\nPDF CXP");
						return false;		
					}
					*/
					mostrarDialog();
					$("#FECHA_APLICACION_CONTABLE").val($("#fechaAp").val());
					p.gestion.setFechaApCont($("#FECHA_APLICACION_CONTABLE").val());
					if(!document.datosReintegro.autorizaRein[1].checked){
						parent.document.getElementById("pb_send").disabled=true;
					}
				}else{
					alert("El documento ya se encuentra aplicado contablemente");
					if(!clickEnviar){
						parent.document.getElementById("pb_save").click();
						parent.document.getElementById("pb_save").disabled=true;
						parent.document.getElementById("pb_send").disabled=false;
						clickEnviar = true;
					}else{
						parent.document.getElementById("pb_send").disabled=true;
					}
					document.datosReintegro.autorizaRein[0].disabled=true;
					document.datosReintegro.autorizaRein[1].disabled=true;
				}
				parent.document.getElementById("pb_save").disabled=false;
			});
		}
				
		var d = new Date();
		var day = d.getDate();
		var month = d.getMonth() + 1;
		var year = d.getFullYear();
		var dString = ( day < 10 ? "0" + day: day  ) + "/" + (month < 10 ? "0" + month: month ) + "/" + year;
				
		if( $("#fechaEx").val() == "" || "N/A" == $("#fechaEx").val()){
			$("#fechaEx").val(dString);
		}
		
		if( $("#fechaAp").val() == "" || "N/A" == $("#fechaAp").val()){
			$("#fechaAp").val(dString);
		}
 				
 		$("#dialog-firmantesUpdate").dialog({
			autoOpen: false,
			height: 420,
			width: 500,
			modal: true,
			buttons: {
				"Aceptar": function() {
					if($.trim($("#cNombreVoBoUpdate").val()) == ""){ alert("Falta Ingresar Nombre en Datos Vº Bº"); return; } 
					else if($.trim($("#cPaternoVoBoUpdate").val()) == ""){ alert("Falta Ingresar Apellido Paterno en Datos Vº Bº"); return; }
					else if($.trim($("#cMaternoVoBoUpdate").val()) == ""){ alert("Falta Ingresar Apellido Materno en Datos Vº Bº"); return; }
					else if($.trim($("#cPuestoVoBoUpdate").val()) == ""){ alert("Falta Ingresar Puesto en Datos Vº Bº"); return; }					
					
					if($.trim($("#cNombreAutUpdate").val()) == ""){ alert("Falta Ingresar Nombre en Datos Autorizar"); return; } 
					else if($.trim($("#cPaternoAutUpdate").val()) == ""){ alert("Falta Ingresar Apellido Paterno en Datos Autorizar"); return; }
					else if($.trim($("#cMaternoAutUpdate").val()) == ""){ alert("Falta Ingresar Apellido Materno en Datos Autorizar"); return; }
					else if($.trim($("#cPuestoAutUpdate").val()) == ""){ alert("Falta Ingresar Puesto en Datos Autorizar"); return; }	
					
					$("#cNombreVo").val($("#cNombreVoBoUpdate").val());
					$("#cPaternoVo").val($("#cPaternoVoBoUpdate").val());
					$("#cMaternoVo").val($("#cMaternoVoBoUpdate").val());
					$("#cPuestoVo").val($("#cPuestoVoBoUpdate").val());
					$("#cNombreA").val($("#cNombreAutUpdate").val());
					$("#cPaternoA").val($("#cPaternoAutUpdate").val());
					$("#cMaternoA").val($("#cMaternoAutUpdate").val());
					$("#cPuestoA").val($("#cPuestoAutUpdate").val());
					
					$("#firmanteVoBo").val($("#cNombreVo").val()+" "+$("#cPaternoVo").val()+" "+$("#cMaternoVo").val());
					$("#firmanteAut").val($("#cNombreA").val()+" "+$("#cPaternoA").val()+" "+$("#cMaternoA").val());
					try{
							queryFormPost({
								queryName:"tReintegroAnexo1EncabezadoFirmante_Update", 
								async : false, 
								callback:function(){
									cmdImprimir("REINTEGROANEXO1");
									
									if (<%=id_oper%>==1){
										if(parent.document.getElementById("pb_save"))
											parent.document.getElementById("pb_save").disabled=true;
										if(parent.document.getElementById("pb_send"))
											parent.document.getElementById("pb_send").disabled=false;
									}
								}
							});
					}catch(e){
						alert("No se pudo actualizar los firmantes, intente mas tarde.");
					}
					
					$(this).dialog("close");
				},
				"Cancelar": function() {
					if(parent.document.getElementById("pb_save"))
						parent.document.getElementById("pb_save").disabled=false;
					$(this).dialog("close");
				}
			},
			close: function(){},
			open: function(){
				var table = document.getElementById('tblReintegros');
 				var row = table.rows[1];
				var folioCLC = row.cells[0].childNodes[0].toString();
				if (folioCLC =="-1"){
					if (!confirm("El folio de la Integracion a la que desea hacer el reintegro \n no se encuentra en los archivos de SICOP, favor de avisar al Administrador. ¿Desea continuar?")){
						$(this).dialog("close");
						return;
					}
				}
				if($("#CTABAN").val()==""){
					alert("Favor de actualizar Cuenta Bancaria");
					$(this).dialog("close");
					return;
				}else
					actualizaPagoInfo();
			}					
		});
		cssReadOnly();
 	});  //fin del ready

		
	function cmdRegresar(){
		self.location="../caso/principal.jsp";
	}
		 
	function guardaExp(){
	  		parent.document.getElementById("pb_save").disabled=false;
	  		parent.document.getElementById("pb_save").click();
	  		parent.document.getElementById("pb_save").disabled=true;
	}
		
	function fnExportaSicop() {
		$.blockUI( {
			message : "Procesando espere ..."
		});
		document.ExportaSicop.submit();
		$.unblockUI();
		return true;
	}
		 
	 $(function(){
		$( '#principal' ).accordion({
			collapsible: true,
			autoHeight: false
		});
		$( '#pago' ).accordion({
			collapsible: true,
			autoHeight: false,
			active: false
		});
		$( '#lineaCaptura' ).accordion({
			collapsible: true,
			autoHeight: false,
			active: false
		});
		$( '#restante' ).accordion({
			collapsible: true,
			autoHeight: false,
			active: false
		});
	});		 
		 
	 function mostrarDialog(){
		$('#dialog').dialog('option', 'modal', true).dialog('open');	 
	 }
		 
	var editable = true;
		
	function aplicaCont(){
		$("#dialog").dialog("close");
		if(!confirm("Enviara el Documento a Aplicar Contablemente.  \n \n  ¿desea continuar?")) {
			return false;
		}
		parent.document.getElementById("pb_save").disabled=true;
	   	var strAction="../servlet/ReintegrosAnexo1Servlet";
	   	$.blockUI({message: "Procesando espere ......"});
   		$.ajax({
   			datatype:"html",
			type: "POST",
			url: strAction,
			data:{aplica:1},
			success: function (data,textStatus){
   				$.unblockUI();
   				var mensaje = data;
   				if(mensaje.lastIndexOf("DOCUMENTO DE REINTEGROANEXO1 APLICADO CONTABLEMENTE") != -1){
   					actualizaPagoInfo();
   					document.datosReintegro.autorizaRein[0].disabled=true;
   					document.datosReintegro.autorizaRein[1].disabled=true;
   					alert("Aplicado correctamente");
   					
			   		parent.document.getElementById("pb_send").disabled=false;
			   		parent.execOperacion();
					parent.execResponsable();
					
		   			parent.document.getElementById("pb_send").click();
   					
   				}else{
   					alert("No se aplico correctamente.\n\n"+data);
   				}
			},
			error: function (par) {alert('<%=mensaje%>');}
		});
   	}
	
	function actualizaPagoInfo(){
		var strAction="reintegroAnexo1.jsp?pago=Si";
		var lc = $("#lineaCap").val();
		var clvRastreo = $("#clvRastreo").val();
		var fichaDep = $("#deposito").val();
		var clvBanco = $("#cveBanco").val();
		var cuenta = $("#cuenta").val();
		var fAcredit = $("#fechaAcredit").val();
		var ctab = $("#CTABAN").val();
		var aviso = $("#aviso").val();
		var folioDep = $("#folioDep").val();
		
		$.ajax({
			datatype:"html",
			type: "POST",
			url: strAction,
			data:{rastreo:clvRastreo, lcaptura:lc, fichaDep:fichaDep, clvBanco:clvBanco, cuenta:cuenta, fAcredit:fAcredit, ctab:ctab, aviso:aviso, folioDep:folioDep},
			success: function (data,textStatus){},
			error: function (par) {alert('<%=mensaje%>');}
		});
	}
	
	function limpiarDatos(){
		var folioReintegro = $("#folioReintegro").val();
		$.blockUI({message : "Procesando espere ......"});
		$.ajax({
			datatype:"html",
			type: "POST",
			url: "../servlet/ReintegrosAnexo1Servlet",
			data:{folioReintegro:folioReintegro,borraTodo:1},
			success: function (data,textStatus){
   				$.unblockUI();
   				document.location.reload();
			},
			error: function (par) {alert('<%=mensaje%>');}
		});
	}
	function cmdImprimir(elFormato){
		window.open(
			"../admin/SeguridadCatalogos?"
			+ "catalogo=CONTRARECIBO"
			+ "&accion=run"
			+ "&rn=PolizaReintegro.jasper"
			+ "&whereFolio= " + <%=folio %>
			+ "&whereTipo= '" + elFormato+"'", 			
			"popacuse",
			"scrollbars=1, resizable=yes, width=1024, height=768");
	}
	function updateFirmantes(){
		queryFormPost({
			queryName:"existeFirmanteReintegroAnexo1", 
			async : false, 
			callback:function(){
				if (<%=id_oper%>==1 && $("#existeFirmante").val()=="SIEXISTE" ){
					parent.document.getElementById("pb_save").disabled=true;
					parent.document.getElementById("pb_send").disabled=false;
				}else
					$("#dialog-firmantesUpdate").dialog("open");
			}
		});
	}
	
	function cssReadOnly(){
		$( "[readOnly]" ).each(function(){	
			$(this).addClass("notEditable");	
		});
	}
	</script>

	</head>

	<body id="dt_example">
		<div id="container" class="container SyCData">
			<h1>
				Reintegro de Solicitud de Recursos x Pagar
			</h1>
			<div id="dialogMensaje" title="Mensajes">
				<% if(mensaje!=null && !"".equals(mensaje)){%>
						<div style="border-style: double;">
							<%=mensaje%>
						</div>
	
				<%} %>
				<%if (id_oper == 2 ) {%>
					<% if(msVariable!=null && !"".equals(msVariable)){%>
							<div style="border-style: double;"><%=msVariable%></div>
					<%} %>
				<%}%>
				
			</div>
			
			<%if (id_oper > 1) {%>
				<form id="ExportaSicop" name="ExportaSicop" action="../gstnmngr/ReintegrosLayoutSicop" method="POST">
					<input type="hidden" id="tipoLayout" name="tipoLayout" value="3"/>
					<input type="button" id="Exportar" value="Exportar SICOP" onclick="fnExportaSicop()"></input>
				</form>
			<%}%>
			
			<form id="datosReintegro" name="datosReintegro" action="">
			
			<% if(mensaje==null || "".equals(mensaje.trim())){%>
			<%if (id_oper == 2) {%>
				Datos Correctos: &nbsp;&nbsp;&nbsp;
				Si <input type="radio" id="autorizaRein" name="autorizaRein"value="1" />&nbsp;
				No <input type="radio" id="autorizaRein" name="autorizaRein" value="0" /> &nbsp;&nbsp;&nbsp; 
				Motivo:&nbsp; <input type="text" id="motivoRechazo" name="motivoRechazo" size="60" maxlength="200" />
				<input type="hidden" id="motivoR" name="motivoR" />
			<%}%>
				<input type="hidden" id="mensajeError" name="mensajeError" />
				<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0) == null ? "caso operacion nulo" : c.getCasoOperacion(0).getResponsable()%>" />
				<input type="hidden" name="FECHA_SOLICITUD" id="FECHA_SOLICITUD" value="<%=today%>" />
				<input type="hidden" name="EJERCICIO_FISCAL" id="EJERCICIO_FISCAL" value="<%=aEjercicioFiscal%>" />	
				<input type="hidden" name="FECHA_APLICACION_CONTABLE" id="FECHA_APLICACION_CONTABLE" value="<%=today%>" readonly="readonly" maxlength="10" size="10"/>
				
				<input type="hidden" id="cNombreVo" name="cNombreVo" size=40 >
				<input type="hidden" id="cPaternoVo" name="cPaternoVo" size=40 >
				<input type="hidden" id="cMaternoVo" name="cMaternoVo" size=40>
				<input type="hidden" id="cPuestoVo" name="cPuestoVo" size=40>
				<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" size=40>
				<input type="hidden" id="cNombreA" name="cNombreA" size=40 >
				<input type="hidden" id="cPaternoA" name="cPaternoA" size=40 >
				<input type="hidden" id="cMaternoA" name="cMaternoA" size=40>
				<input type="hidden" id="cPuestoA" name="cPuestoA" size=40>
				<input type="hidden" id="firmanteAut" name="firmanteAut" size=40>
				<input type="hidden" id="folioReint" name="folioReint" size=40 value="<%=folio%>">
				<input type="hidden" id="existeFirmante" name="existeFirmante" size=40 value="NOEXISTE">
				
			<div id="principal" class="margenA">
				<h3>
					<a href="#">PRINCIPAL</a>
				</h3>
				<table>
					<tr>
						<td width="50">
							Folio
						</td>
						<td width="240">
							<input type="text" name="FOLIO" id="FOLIO" size="15" value="<%=c.getFolio() %>" readonly="readonly" style="text-align:center;">
						</td>
						<td width="100">
							 Fecha Sol.
						</td>
						<td width="200">
							<input type="text" name="fechaSol" id="fechaSol" value="<%=today%>" size="20" readonly="readonly" style="text-align:center;"/>
						</td>

						<td width="50">
							Total $
						</td>
						<td  width="150">
							<input type="text" name="importe" id="importe" size="20" readonly style="text-align: right;">
						</td>
					</tr>
					<tr>
						<td>
							<div id="divImprimePoliza">
								<img src="../Generador/imagenes/Imprimir.png" width="25" height="21" onClick="cmdImprimir('REINTEGROANEXO1');" > Poliza
							</div>
						</td>
						<td>
							<span id="EditaFirmas" style="visibility:hidden"><a href="#" onclick="updateFirmantes();">Firmas*</a></span>
						</td>
					</tr>
				</table>
			</div>
			<!-- PARA HACER EL UPDATE DE MONTOS -->
				<input type="hidden" id="mImportePos" name="mImportePos" />
				<input type="hidden" name="mImporteNeg" id="mImporteNeg" />
				<input type="hidden" name="mImporteLCR" id="mImporteLCR" />
				<input type="hidden" name="nFolioRein" id="nFolioRein" />
				<input type="hidden" name="nDocRenglonR" id="nDocRenglonR" />
				<input type="hidden" id="nFolioR" name="nFolioR" />
			<!-- ****************************** -->	
			</form>
			
			<div id="restante" class="margenA">
				<h3>
					<a href="#">CARACTERISTICAS</a>
				</h3>
				<table>
					<tr>
						<td>
							Aviso reintegro
						</td>
						<td width="200">
							<input type="text" name="aviso" id="aviso" maxlength="20">
						</td>
						<td>
							Folio Dependencia
						</td>
						<td>
							<input type="text" name="folioDep" id="folioDep" size="25" maxlength="30" />
						</td>
						<td>
							 Fecha Exp.
						</td>
						<td>
							<input type="text" name="fechaEx" id="fechaEx" readonly="readonly"/>
						</td>
					</tr>
					<tr>
						<td>
							 Movto.
						</td>
						<td>
							<input type="text" class="AyudaSyC autoCompletaSyC" name="CatMovimientoReintegro" id="CatMovimientoReintegro" size="5" readonly="readonly"/>
						</td>
						<td>
							 Fecha Apl.
						</td>
						<td>
							<input type="text" name="fechaAp" id="fechaAp" value="<%=(today.contains(aEjercicioFiscal))?today:"31/12/"+aEjercicioFiscal%>"/>
						</td>
					</tr>
					<tr>
						<td>
							 Tipo de Aviso
						</td>
						<td>
							<input type="text" name="CatTipoCausaAvisoReintegro" id="CatTipoCausaAvisoReintegro" class="AyudaSyC autoCompletaSyC" size="5" readonly="readonly">
						</td>
						<td>
							 Causa del Aviso.
						</td>
						<td>
							<input type="text" name="CatCausaAvisoReintegro" id="CatCausaAvisoReintegro" class="AyudaSyC autoCompletaSyC" size="5" readonly="readonly">
						</td>
						<td width="200">
							 Forma de Pago
						</td>
						<td>
							<input type="text" class="AyudaSyC autoCompletaSyC" name="CatFormaPagoAvisoReintegro" id="CatFormaPagoAvisoReintegro" size="5" readonly="readonly"/>
						</td>
						
					</tr>
					<tr>
					</tr>
					<tr>
						<td>
							Observaciones
						</td>
						<td colspan="3">
							<input type="text" name="observaciones" id="observaciones" size="39" maxlength="60" readonly="readonly"/>
						</td>
						<td>
							Cuenta Bancaria
						</td>
						<td colspan="1">
							<input type="text" name="CTABAN" id="CTABAN" value="" size="18" onkeydown="return ctaKeyDwn(event, this.id)" onfocus="cierraAyuda()" class="AyudaSyC" readonly="readonly"/>
						</td>
					</tr>
					<tr>
						<td>
							Concepto
						</td>
						<td colspan="3">
							<input type="text" name="concepto" id="concepto" size="39" readonly="readonly"/>
						</td>
					</tr>
				</table>
			</div>
			<div id="pago" class="margenA">
				<h3>
					<a href="#">PAGO</a>
				</h3>
				<table>
					<tr>
						<td>
							Clave de Rastreo
						</td>
						<td colspan="3" width="500">
							<input type="text" name="clvRastreo" id="clvRastreo" size="39" />
						</td>
					</tr>
					<tr>
						<td>
							Ficha de Dep&oacute;sito
						</td>
						<td colspan="3">
							<input type="text" name="deposito" id="deposito" size="39" />
						</td>
					</tr>
					<tr>
						<td>
							Cve. Banco
						</td>
						<td>
							<input type="text" name="cveBanco" id="cveBanco" size="5" >
						</td>
						<td>
							Cta Bancaria
							<input type="text" name="cuenta" id="cuenta" size="10" >
						</td>
						<td>
							Fecha de Autorización.
						</td>
						<td>
							<input type="text" name="fechaAcredit" id="fechaAcredit" readonly="readonly"/>
						</td>
					</tr>
					<tr>
					<td>
							<!-- VOLANTE SIEMPRE VA A SER 0 -->						
							<input type="hidden" name="volante" id="volante" value="0">
						</td>
						<td>
							Linea Cap.
						</td>
						<td width=250>
							<input type="text" name="lineaCap" id="lineaCap" size="30">
						</td>
						</tr>
				</table>
			</div>
			<div id="multitabs" style="width: 100%">
				<div id="tabs-3">
				
						<table   class="display" id="tblReintegros" style="text-align: center;">
							<thead>
								<tr>
									<th>
										noCLC
									</th>
									<th>
										sec
									</th>
									<th style="width: 80px">
										EP
									</th>
									<th>
										mImporte
									</th>
									<th>
										Cuenta Por Pagar
									</th>
									</tr>
									</thead>
									<tbody>
										<tr>
											<td style="text-align: center;"></td><td style="text-align: center;"></td>
											<td style="text-align: center;"></td><td style="text-align: center; width:100px;"></td>
											<td style="text-align: center;"></td><td style="text-align: center;"></td>
											<!--<td></td>-->
										</tr>
									</tbody>
									</table>
												
				</div>
			</div>
			<%if(id_oper==1 && re!=null){%>
				<form id="limpiaDatos" name="limpiaDatos" method="POST" action="../servlet/ReintegrosAnexo1Servlet" >
					<input type="hidden" name="folioReintegro" id="folioReintegro" value="<%=folio%>"/>
					<input type="hidden" name="borraTodo" id="borraTodo" value="1"/>
					<input type="button" name="borrarTodo" id="borrarTodo" value="Limpiar Datos" onclick="limpiarDatos();"/>
				</form>
			<%}%>
									
		<div id="dialog" title="Detalle de Reintegros">
			<p>Consulta de Reintegros del Presupuesto </p>
			<input type="button" id="aplicarContable" value="Aplicar contablemente" onclick="aplicaCont();" />
		</div>
		
		</div>
	<div id="dlgError" title="Mensajes del sistema Aviso de Reintegros">
		<p>Mensajes del sistema </p> <a rel=""></a>
		<table   class="display" id="grdAnteProyecto">
			<tr> <td>
				<textarea id="textmensajeError" name="textmensajeError" rows="16" cols="140"></textarea>
			</td></tr>
		</table>
	</div>
	<% }%>
	<div id="dialogMotor" title="Mensajes del sistema Aviso de Reintegros">
		<p>Mensajes del sistema </p> <a rel=""></a>
		<textarea id="mensajeMotor" name="mensajeMotor" rows="8" cols="120"></textarea>
	</div>
	
	<form id="liberardocumento" name="liberardocumento"
			action="../gstnmngr/gestion?cmd=1" target="content-iframe"
			method="post">
	</form>
		<div id="dialog-firmantesUpdate" title="Actualizacion de Firmantes">
			<fieldset>
				<legend>Datos VºBº</legend>
				<table>
					<tr>
						<td>Nombre</td>
						<td><input type="text" id="cNombreVoBoUpdate" name="cNombreVoBoUpdate" size=40></td>
					</tr>
					<tr>
						<td>Apellido Paterno</td>
						<td><input type="text" id="cPaternoVoBoUpdate" name="cPaternoVoBoUpdate" size=40></td>
					</tr>
					<tr>
						<td>Apellido Materno</td>
						<td><input type="text" id="cMaternoVoBoUpdate" name="cMaternoVoBoUpdate" size=40></td>
					</tr>
					<tr>
						<td>Puesto</td>
						<td><input type="text" id="cPuestoVoBoUpdate" name="cPuestoVoBoUpdate" size=40></td>
					</tr>
				</table>
			</fieldset>
			<fieldset>
				<legend>Datos Autoriza</legend>
				<table>
					<tr>
						<td>Nombre</td>
						<td><input type="text" id="cNombreAutUpdate" name="cNombreAutUpdate" size=40></td>
					</tr>
						<tr>
							<td>Apellido Paterno</td>
							<td><input type="text" id="cPaternoAutUpdate" name="cPaternoAutUpdate" size=40> </td>
						</tr>
						<tr>
							<td>Apellido Materno</td>
							<td><input type="text" id="cMaternoAutUpdate" name="cMaternoAutUpdate" size=40></td>
						</tr>
						<tr>
							<td>Puesto</td>
							<td><input type="text" id="cPuestoAutUpdate" name="cPuestoAutUpdate" size=40></td>
						</tr>
					</table>
				</fieldset>
			</div>
	</body>
</html>
