<%@page import="com.syc.contable.ReintegrosFiscalesBusinessLogic"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="java.util.*"%>
<%@page	import="com.syc.gestion.core.*"%>
<%@page	import="com.syc.gestion.servlet.*"%>
<%@page	import="com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.ReintegrosFiscalesBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.ReintegroEncabezado"%>
<%@page import="com.syc.contable.core.ReintegroDetalle"%>
<%@page import="com.syc.contable.core.ReintegroIngresoEncabezado"%>
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
	
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	
	final int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
	String msVariable = c.getCasoDato("MENSAJE").getValor(); //Recuperamos el mensaje del caso
	
	ReintegrosFiscalesBusinessLogic reintegro = new ReintegrosFiscalesBusinessLogic(GestionInterface.ATT_CONEXION);
	
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
	
	archivoExcel = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1); 
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
	
	ReintegroIngresoEncabezado re = reintegro.getReintegroEncabezadoNuevo(folio);
	
	CasoBusinessLogic casoTx = new CasoBusinessLogic("jdbc/gestion");
	ITree tree = casoTx.getArbolCaso(c);
	session.setAttribute("tree.model", tree);
	
	%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Reintegro de Ingreso Fiscal</title>
<link rel="stylesheet" type="text/css"  href="../css/datepickercontrol_bluegray.css" />
		<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>	
		<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>	
		
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="../Generador/js/Moment.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
		
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

		<script type="text/javascript" charset="utf-8">
		var muestraAplicar = true;
		var clickEnviar = false;
		
		function onPostSubmit(id_oper){//validaciones del boton enviar
			//var valida_doctos_requeridos = true;
		  	//validaciones de documentos requeridos
		  	//return valida_doctos_requeridos;
		  	clickEnviar=true;
			if (id_oper==1 && <%=archivoPDFCXP.length==0%>){ 
				Swal.fire({ icon: 'warning',
							text: "Para poder continuar debe de adjuntar la Solicitud Firmada(PDF CXP)" });				
				return false;
			}
			if(id_oper==1){
				
				if(<%="1".equals(re!=null?re.getTipoAviso():"")%>){
					if($("#CTABAN").val()==""){
						Swal.fire({ icon: 'warning',
									text: "Favor de actualizar la cuenta bancaria" });						
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
				
			}else if(id_oper==3){
				if (document.datosReintegro.autorizaRein[1].checked)
					queryFormPost("tReintegroIngresoUpdateCancelado", {async:false});

				actualizaPagoInfo();
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
			if(id_oper==1){
				if($("#cTipoReintegro").val() == "IP"){
	  				return "AUTORIZA_REINTEGROINGRESO";
	  			}else{
	  				return "CAPTURA_REINTEGROINGRESO";
	  			}	  		 	
	  		}
	  		if(id_oper==2){
	  		 	return "AUTORIZA_REINTEGROINGRESO";
	  		}
	  		if(id_oper==3){
	  		 	return "CONSULTA_REINTEGROINGRESO";
	  		}
		 }
		
		function OperacionSiguiente(id_oper){
			if(id_oper==1){
				if($("#cTipoReintegro").val() == "IP"){
	  				return "autoriza_reintegroingreso";
	  			}else{
	  				return "genera_layout";
	  			}				
			}
			if(id_oper==2){
				return "autoriza_reintegroingreso";
			}
			if(id_oper==3){
				return "consulta_reintegroingreso";
			}
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
					p.gestion.setConceptoMov("Reintegro Ingreso Fiscal");
					p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
					p.gestion.setFechaApCont($("#FECHA_APLICACION_CONTABLE").val());
					if(<%=re!=null%>)
						updateFirmantes();
				}
					
				if(id_oper == 2){
					parent.document.getElementById("pb_send").disabled=false;
		  			parent.document.getElementById("pb_save").disabled=true;
				}
					
		  		if (id_oper==3){
		  			if(document.datosReintegro.autorizaRein[1].checked && $("#motivoRechazo").val()==""){			  			
			  			Swal.fire({ icon: 'warning',
									text: "Motivo de rechazo es requerido" });
			  			return false;
		  			}else if(document.datosReintegro.autorizaRein[1].checked){
		  				parent.document.getElementById("pb_send").disabled=false;
		  				parent.document.getElementById("pb_save").disabled=true;
		  				$("#motivoR").val($("#motivoRechazo").val());
		  				$("#mensajeError").val($("#motivoRechazo").val());
		  				$("#nFolioR").val(<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>);
		  				queryFormPost("tReintegroIngresoEncabezadoUpdate", {async:false});
		  			}else if(!(document.datosReintegro.autorizaRein[0].checked || document.datosReintegro.autorizaRein[1].checked)){		  				
		  				Swal.fire({ icon: 'warning',
									text: "Favor de marcar si los datos son correctos o no" });
		  				return false;
		  			}else{
		  				if(<%=("true".equals(c.getCasoDato("APLICADO_CONT").getValor())) && c.getCasoDato("APLICADO_CONT").getValor()!=null%>){
							parent.document.getElementById("pb_send").disabled=true;
						}
		  			}
	  			}
			
		  		/*if(id_oper == 3){
		  			parent.document.getElementById("pb_send").disabled=false;
		  		}*/
		  		
	  		}catch (e) {
				window.alert("onSubmit: Error: " + e.message);				
				return false;
			}
			return valida_campos;
	  	}
	
		function onLoadPlantilla(id_oper){
			
			if (<%=id_oper%> == 4){
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
					Swal.fire({ icon: 'error',
								text: $("#mensajeError").val() });
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
	  		
	  		$("#cTipoReintegro").val('<%=re!=null?re.getcTipoReintegro():""%>');
			
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
	  				
	  			if($("#cTipoReintegro").val() == "IP"){
	  				$("#Exportar").hide();
	  			}else{
	  				$("#Exportar").show();
	  			}
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
			
			if(<%=id_oper%>  == 3){ //AUTORIZA
				
				$("#fechaAcredit").attr('disabled','disabled');
				$("#aviso").attr('disabled','disabled');
				$("#folioDep").attr('disabled','disabled');
				$("#fechaAp").attr('disabled','disabled');
				$("#clvRastreo").attr('disabled','disabled');
				$("#deposito").attr('disabled','disabled');
				$("#cveBanco").attr('disabled','disabled');
				$("#cuenta").attr('disabled','disabled');
				$("#lineaCap").attr('disabled','disabled');
				
				parent.document.getElementById("pb_save").disabled=false;
				parent.document.getElementById("pb_send").disabled=true;
			}
			
			if(<%=id_oper%>  == 4){ //consulta
				
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
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});
		$( "#fechaEx" ).datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});
		$( "#fechaAp" ).datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});
		$( "#fechaAcredit" ).datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
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
		$("#aplicarContable").button();
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
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vtreintegroIngresoDet&qw=nFolioReintegroIngreso="+<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>,
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

		if(<%=id_oper%>==3){
			if(<%=("true".equals(c.getCasoDato("APLICADO_CONT").getValor())) && c.getCasoDato("APLICADO_CONT").getValor()!=null%>){
				document.datosReintegro.autorizaRein[1].disabled=true;
			}
			$('#autorizaRein').click(function(){
				if(<%=!("true".equals(c.getCasoDato("APLICADO_CONT").getValor())) && c.getCasoDato("APLICADO_CONT").getValor()!=null%>){
					/*VALIDACION DE ARCHIVOS ADJUNTOS ANTES DE APLICAR
					if (<%=archivoPDFComprobante.length==0%> || <%=archivoLineaCaptura.length==0%> || <%=archivoPDFCLC.length==0%> || <%=archivoReporteSiaff.length==0%> || <%=archivoPDFCXP.length==0%> ){ 						
						Swal.fire({ icon: 'warning',
									text: "Para poder continuar debe de adjuntar los archivos siguientes:\n\nComprobande de Pago\nLinea de Captura\nOficios\nReporte SIAFF\nPDF CXP"});
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
					Swal.fire({ icon: 'info',
								text: "El documento ya se encuentra aplicado contablemente"});
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
					if($.trim($("#cNombreVoBoUpdate").val()) == ""){ 						
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Nombre en Datos Vº Bº"});
						return; 
					} else if($.trim($("#cPaternoVoBoUpdate").val()) == ""){ 
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Apellido Paterno en Datos Vº Bº"});
						return;  
					} else if($.trim($("#cMaternoVoBoUpdate").val()) == ""){ 
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Apellido Materno en Datos Vº Bº"});
						return;
					} else if($.trim($("#cPuestoVoBoUpdate").val()) == ""){ 
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Puesto en Datos Vº Bº"});
						return;
					}					
					
					if($.trim($("#cNombreAutUpdate").val()) == ""){ 
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Nombre en Datos Autoriza"});
						return;
					} else if($.trim($("#cPaternoAutUpdate").val()) == ""){ 
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Apellido Paterno en Datos Autoriza"});
						return;
					} else if($.trim($("#cMaternoAutUpdate").val()) == ""){ 
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Apellido Materno en Datos Autoriza"});
						return;
					} else if($.trim($("#cPuestoAutUpdate").val()) == ""){ 
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Puesto en Datos Autoriza"});
						return;
					}	
					
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
								queryName:"tReintegroIngresoEncabezadoFirmante_Update", 
								async : false, 
								callback:function(){
									cmdImprimir("REINTEGROINGRESO");
									
									if (<%=id_oper%>==1){
										if(parent.document.getElementById("pb_save"))
											parent.document.getElementById("pb_save").disabled=true;
										if(parent.document.getElementById("pb_send"))
											parent.document.getElementById("pb_send").disabled=false;
									}
								}
							});
					}catch(e){
						Swal.fire({ icon: 'warning',
									text: "No se pudo actualizar los firmantes, intente mas tarde."});				
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
					if($("#cTipoReintegro").val() == "IF"){
						if (!confirm("El folio de la Integracion a la que desea hacer el reintegro \n no se encuentra en los archivos de SICOP, favor de avisar al Administrador. ¿Desea continuar?")){
							$(this).dialog("close");
							return;
						}
					}
				}
				if($("#CTABAN").val()==""){
					Swal.fire({ icon: 'warning',
								text: "Favor de actualizar Cuenta Bancaria"});
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
		/*$.blockUI( {
			message : "Procesando espere ..."
		});
		document.ExportaSicop.submit();
		$.unblockUI();*/
		return true;
	}	 

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
	   	var strAction="../servlet/ReintegrosFiscalesServlet";
	   	$.blockUI({message: "Procesando espere ......"});
   		$.ajax({
   			datatype:"html",
			type: "POST",
			url: strAction,
			data:{aplica:1},
			success: function (data,textStatus){
   				/*$.unblockUI();
   				var mensaje = data;
   				if(mensaje.lastIndexOf("DOCUMENTO DE REINTEGRO INGRESO APLICADO CONTABLEMENTE") != -1){
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
   				}*/
   				
   				$("#mensajeMotor").val(data);
				$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
				actualizaPagoInfo();
				document.datosReintegro.autorizaRein[0].disabled=true;
				document.datosReintegro.autorizaRein[1].disabled=true;			
 				parent.document.getElementById("pb_send").disabled=false;
 				parent.execOperacion();
				parent.execResponsable();

				parent.document.getElementById("pb_send").click();
   				//document.liberardocumento.submit();
			},
			error: function (par) {
				Swal.fire({ icon: 'error',
							text: '<%=mensaje%>'});
				}
		});
   	}
	
	function actualizaPagoInfo(){
		var strAction="reintegroIngreso.jsp?pago=Si";
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
			error: function (par) {
				Swal.fire({ icon: 'error',
							text: '<%=mensaje%>'});
				}
		});
	}
	
	function limpiarDatos(){
		var folioReintegro = $("#folioReintegro").val();
		$.blockUI({message : "Procesando espere ......"});
		$.ajax({
			datatype:"html",
			type: "POST",
			url: "../servlet/ReintegrosFiscalesServlet",
			data:{folioReintegro:folioReintegro,borraTodo:1},
			success: function (data,textStatus){
   				$.unblockUI();
   				document.location.reload();
			},
			error: function (par) {
				Swal.fire({ icon: 'error',
							text: '<%=mensaje%>'});
				}
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
			queryName:"existeFirmanteReintegroIngreso", 
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
<br/>
	<body id="dt_example">
		<div id="container" class="container" style="width: 80%">
			<div class="card-header"> <h3>Reintegro de Solicitud de Ingresos</h3> </div>					
			<hr class="mt-3">	
			
			<div id="dialogMensaje" title="Mensajes">
				<% if(mensaje!=null && !"".equals(mensaje)){%>
					<div class="row d-flex justify-content-center">
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1" style="border-style: double;">
							<%=mensaje%>
						</div>
					</div>			
				<%} %>
				<%if (id_oper == 3 ) {%>
					<% if(msVariable!=null && !"".equals(msVariable)){%>
						<div class="row d-flex justify-content-center">
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1" style="border-style: double;">
								<%=msVariable%>
							</div>
						</div>	
					<%} %>
				<%}%>
				
			</div>
			
			<%if (id_oper == 2) {%>
				<form id="ExportaSicop" name="ExportaSicop" action="../gstnmngr/ReintegrosLayoutSicop" method="POST">
					<input type="hidden" id="tipoLayout" name="tipoLayout" value="3"/>
					<div class="row d-flex">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
							<input type="button" id="Exportar" name="Exportar" value="Exportar SICOP" class="btn btn-secondary" onclick="fnExportaSicop();"/>
						</div>
					</div>					
				</form>
			<%}%>
			
			<form id="datosReintegro" name="datosReintegro" action="">
			
				<% if(mensaje==null || "".equals(mensaje.trim())){%>
				<%if (id_oper == 3) {%>
					<div class="row d-flex justify-content">
						<div class="col-12 d-flex justify-content-center">
							<label class="form-label"> ¿Datos correctos?: </label>&nbsp;&nbsp;&nbsp;&nbsp;
							<label for="autorizaSi" class="form-check-label">Si: </label> &nbsp;&nbsp;
							<input type="radio" name="autorizaRein" id="autorizaRein" class="form-check-input" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;
							
							<label for="autorizaNo" class="form-check-label">No: </label>&nbsp;&nbsp;
							<input type="radio" name="autorizaRein" id="autorizaRein" class="form-check-input" value="0" />&nbsp;&nbsp;&nbsp;&nbsp;
							
						</div>																		
					</div>
					
					<div class="row d-flex justify-content-center">
						<div class="col-2 d-flex">
							<label class="form-label"> Motivo: </label>
						</div>
						<div class="col-6 d-flex">
							<input type="text" id="motivoRechazo" name="motivoRechazo" size="60" maxlength="200"  class="form-control form-control-sm"/>
						</div>
					</div>
					
					<input type="hidden" id="motivoR" name="motivoR" />
				<%}%>
				
				<br/>
				
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
				
				<input type="hidden" id="cTipoReintegro" name="cTipoReintegro" value="">
				
				<div class="row d-flex justify-content">
					<div class="col">
						<div class="accordion accordion-flush" id="temario">
							<div class="accordion-item">
								<h2 class="accordion-header" id="encabezado-1">							
									<button	class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#principal" aria-expanded="true" aria-controles="principal">
										PRINCIPAL
									</button>
								</h2>	
								<div id="principal" class="accordion-collapse collapse show" aria-labelledby="encabezado-1">
									<div class="accordion-body">
									
										<div class="row d-flex">
											<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
												<label for="FOLIO" class="form-label"> Folio </label>
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<input type="text" class="form-control form-control-sm" name="FOLIO" id="FOLIO" size="15" value="<%=c.getFolio() %>" readonly>
											</div>
											<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
											</div>
											<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
												<label for="fechaSol" class="form-label"> Fecha Sol. </label>
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<input type="text" class="form-control form-control-sm" name="fechaSol" id="fechaSol" size="15" value="<%=today%>" readonly>
											</div>
											<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
											</div>
											<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
												<label for="importe" class="form-label"> Total </label>
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<div class="input-group">
													<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>					
													<input  type="text" id="importe" name="importe" class="form-control form-control-sm" readonly>
												</div>	
											</div>
										</div>
									
										<div id="divImprimePoliza">
											<div class="row d-flex justify-content">
												<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																										
													<input type="button" class="btn btn-secondary btn-sm" name="btnImprimir" id="btnImprimir" value="Reimprimir Poliza" onclick="cmdImprimir('REINTEGROINGRESO')" >													
													<span id="EditaFirmas" ><a href="#" onclick="updateFirmantes();" >*Firmas</a></span>
												</div>
											</div>	
										</div>
				
									</div>
								</div>
							</div>
							<!-- PARA HACER EL UPDATE DE MONTOS -->
								<input type="hidden" id="mImportePos" name="mImportePos" />
								<input type="hidden" name="mImporteNeg" id="mImporteNeg" />
								<input type="hidden" name="mImporteLCR" id="mImporteLCR" />
								<input type="hidden" name="nFolioRein" id="nFolioRein" />
								<input type="hidden" name="nDocRenglonR" id="nDocRenglonR" />
								<input type="hidden" id="nFolioR" name="nFolioR" />
								<input type="hidden" name="volante" id="volante" value="0">
							<!-- ****************************** -->	
							
							<div class="accordion-item">
								<h2 class="accordion-header" id="encabezado-2">							
									<button	class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#restante" aria-expanded="true" aria-controles="restante">
										CARACTERISTICAS
									</button>
								</h2>
								
								<div id="restante" class="accordion-collapse collapse" aria-labelledby="encabezado-2">					
									<div class="accordion-body">
										
										<div class="row d-flex">
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<label for="aviso" class="form-label"> Aviso Reintegro: </label>
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<input type="text" name="aviso" id="aviso" class="form-control form-control-sm">
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<label for="folioDep" class="form-label"> Folio Dependencia: </label>
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<input type="text" name="folioDep" id="folioDep" class="form-control form-control-sm">
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<label for="fechaEx" class="form-label"> Fecha Exp.: </label>
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<div class="input-group">
													<span class="input-group-text"><i class="bi bi-calendar"></i></span>	
													<input type="text" name="fechaEx" id="fechaEx" class="form-control form-control-sm">
												</div>
											</div>
										</div>
										
										<div class="row d-flex">
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<label for="CatMovimientoReintegro" class="form-label"> Movto: </label>
											</div>												
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<div class="input-group">
													<input type="text" name="CatMovimientoReintegro" id="CatMovimientoReintegro" size="5" class="form-control form-control-sm AyudaSyC">
												</div>
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<label for="fechaAp" class="form-label"> Fecha Apl: </label>
											</div>												
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">		
												<div class="input-group">
													<span class="input-group-text"><i class="bi bi-calendar"></i></span>												
													<input type="text" name="fechaAp" id="fechaAp" size="5" class="form-control form-control-sm" value="<%=(today.contains(aEjercicioFiscal))?today:"31/12/"+aEjercicioFiscal%>" readonly>
												</div>														
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<label for="fechaAp" class="form-label"> Cta Bancaria: </label>
											</div>												
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																								
												<div class="input-group">
													<input type="text" name="CTABAN" id="CTABAN" value="" class="form-control form-control-sm AyudaSyC" onkeydown="return ctaKeyDwn(event, this.id)" onfocus="cierraAyuda()" readonly/>
												</div>													
											</div>
										</div>
										
										<div class="row d-flex">
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<label for="CatTipoCausaAvisoReintegro" class="form-label"> Tipo de Aviso: </label>
											</div>												
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<div class="input-group">
													<input type="text" name="CatTipoCausaAvisoReintegro" id="CatTipoCausaAvisoReintegro" class="form-control form-control-sm AyudaSyC">
												</div>
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<label for="CatCausaAvisoReintegro" class="form-label"> Causa del Aviso: </label>
											</div>												
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">						
												<div class="input-group">								
													<input type="text" name="CatCausaAvisoReintegro" id="CatCausaAvisoReintegro" class="form-control form-control-sm AyudaSyC"/>
												</div>														
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<label for="CatFormaPagoAvisoReintegro" class="form-label"> Forma de Pago: </label>
											</div>												
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																												
												<div class="input-group">														
													<input type="text" name="CatFormaPagoAvisoReintegro" id="CatFormaPagoAvisoReintegro" value="" class="form-control form-control-sm AyudaSyC"/>
												</div>																																		
											</div>
										</div>
										
										<div class="row d-flex">
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<label for="observaciones" class="form-label"> Observaciones: </label>
											</div>												
											<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
												<textarea class="form-control form-control-sm" rows=3 cols="35" id="observaciones" name="observaciones"></textarea>
											</div>
										</div>
										
										<div class="row d-flex">
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<label for="concepto" class="form-label"> Concepto: </label>
											</div>												
											<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
												<textarea class="form-control form-control-sm" rows=3 cols="35" id="concepto" name="concepto"></textarea>
											</div>
										</div>
								
									</div>
								</div>
							</div>
								
							<div class="accordion-item">
								<h2 class="accordion-header" id="encabezado-3">							
									<button	class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#pago" aria-expanded="true" aria-controles="pago">
										PAGO
									</button>
								</h2>	
								
								<div id="pago" class="accordion-collapse collapse" aria-labelledby="encabezado-3">					
									<div class="accordion-body">
									
										<div class="row d-flex">
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<label for="clvRastreo" class="form-label"> Clave de Rastreo: </label>
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<input type="text" name="clvRastreo" id="clvRastreo" class="form-control form-control-sm">
											</div>
										</div>
										
										<div class="row d-flex">
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<label for="deposito" class="form-label"> Ficha de Dep&oacute;sito: </label>
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<input type="text" name="deposito" id="deposito" class="form-control form-control-sm">
											</div>
										</div>
										
										<div class="row d-flex">
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<label for="cveBanco" class="form-label"> Cve. Banco: </label>
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<input type="text" name="cveBanco" id="cveBanco" class="form-control form-control-sm">
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<label for="cuenta" class="form-label"> Cta Bancaria: </label>
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<input type="text" name="cuenta" id="cuenta" class="form-control form-control-sm">
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<label for="fechaAcredit" class="form-label"> Fecha Aut.: </label>
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<div class="input-group">
													<span class="input-group-text"><i class="bi bi-calendar"></i></span>								
													<input type="text" name="fechaAcredit" id="fechaAcredit" class="form-control form-control-sm" readonly>
												</div>
											</div>
										</div>
										
										<div class="row d-flex">
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<label for="lineaCap" class="form-label"> Linea Cap.: </label>
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<input type="text" name="lineaCap" id="lineaCap" class="form-control form-control-sm">
											</div>
										</div>
					
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>				
								
				<div id="multitabs" style="width: 100%" class="container">									
					<div class="table-responsive">	    
						<table id="tblReintegros" class="table table-striped">						
							<thead>
								<tr>
									<th>noCLC</th>
									<th>sec</th>
									<th style="width: 60px">EP</th>
									<th>mImporte</th>
									<th>Cuenta Por Pagar</th>									
								</tr>
							</thead>
							<tbody>
								<tr>
									<td style="text-align: center;"></td><td style="text-align: center;"></td>
									<td style="text-align: center;"></td><td style="text-align: center; width:100px;"></td>
									<td style="text-align: center;"></td><td style="text-align: center;"></td>
								</tr>
							</tbody>							
						</table>
					</div>					
				</div>
				
				<br/>
				
				<%if(id_oper==1 && re!=null){%>
					<form id="limpiaDatos" name="limpiaDatos" method="POST" action="../servlet/ReintegrosFiscalesServlet" >
						<input type="hidden" name="folioReintegro" id="folioReintegro" value="<%=folio%>"/>
						<input type="hidden" name="borraTodo" id="borraTodo" value="1"/>
						<input type="button" name="borrarTodo" id="borrarTodo" class="btn btn-outline-secondary btn-sm" value="Limpiar Datos" onclick="limpiarDatos();"/>
					</form>
				<%}%>
										
				<div id="dialog" title="Detalle de Reintegros">
					<div class="row">
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex">
							<p>Aplica Apartado Presupuestal </p>															
						</div>
					</div>	
					<div class="row">
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">														
							<input type="button" class="btn btn-secondary btn-sm" id="aplicarContable" value="Aplicar contablemente" onclick="aplicaCont();" />
						</div>
					</div>	
				</div>
		
			</form>
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
	
		<div id="dialog-firmantesUpdate" title="Actualizacion de Firmantes" class="container">
			<h5> Datos VºBº </h5>
			<hr class="mt-3">		
				
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cNombreVoBoUpdate" class="form-label"> Nombre: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cNombreVoBoUpdate" name="cNombreVoBoUpdate" size=40 maxlength="70"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cPaternoVoBoUpdate" class="form-label"> Ap. Paterno: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cPaternoVoBoUpdate" name="cPaternoVoBoUpdate" size=40 maxlength="70"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cMaternoVoBoUpdate" class="form-label"> Ap. Materno: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cMaternoVoBoUpdate" name="cMaternoVoBoUpdate" size=40 maxlength="70"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cPuestoVoBoUpdate" class="form-label"> Puesto: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cPuestoVoBoUpdate" name="cPuestoVoBoUpdate" size=40 maxlength="70"/>
				</div>
			</div>	
			
			<br/>
			
			<h5> Datos Autoriza </h5>
			<hr class="mt-3">		
				
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cNombreAutUpdate" class="form-label"> Nombre: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cNombreAutUpdate" name="cNombreAutUpdate" size=40 maxlength="70"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cPaternoAutUpdate" class="form-label"> Ap. Paterno: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cPaternoAutUpdate" name="cPaternoAutUpdate" size=40 maxlength="70"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cMaternoAutUpdate" class="form-label"> Ap. Materno: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cMaternoAutUpdate" name="cMaternoAutUpdate" size=40 maxlength="70"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cPuestoAutUpdate" class="form-label"> Puesto: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cPuestoAutUpdate" name="cPuestoAutUpdate" size=40 maxlength="70"/>
				</div>
			</div>	

		</div>
	</body>
</html>
