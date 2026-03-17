<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page	import="com.syc.gestion.core.*"%>
<%@page	import="com.syc.gestion.servlet.*"%>
<%@page	import="com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.reintcont.ReintegroContBussinesLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.reintcont.ReintegroContEncabezado"%>
<%@page import="com.syc.reintcont.ReintegroContDetalle"%>
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
	
	if( Integer.parseInt( aEjercicioFiscal ) != c1.get(Calendar.YEAR) )
		today = "31/12/" + aEjercicioFiscal;
		
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	int idCaso = c.getIdCaso();
	final int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
	String msVariable = c.getCasoDato("MENSAJE").getValor(); //Recuperamos el mensaje del caso
	String cUR = usuario.getU_UR();
	
	ReintegroContBussinesLogic reintegro = new ReintegroContBussinesLogic(GestionInterface.ATT_CONEXION);
	
	if (request.getParameter("pago") != null && request.getParameter("pago").equals("Si")) {
	    boolean a = reintegro.actualizaCtaBancaria(folio, request.getParameter("ctab"));
	}
	
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

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
	
	ReintegroContEncabezado re = reintegro.getReintegroEncabezadoNuevo(folio);
	ReintegroContDetalle rd = reintegro.getReintegroDetalleNuevo( folio );
	
	String rfc; 
	rfc = rd.getRfc();	
	
	CasoBusinessLogic casoTx = new CasoBusinessLogic("jdbc/gestion");
	ITree tree = casoTx.getArbolCaso(c);
	session.setAttribute("tree.model", tree);	
	
	String cNombreElabora = e.getNombre();
	String cApellidoPaternoElabora  = e.getApellidoPaterno();
	String cApellidoMaternoElabora  = e.getApellidoMaterno();
	String cPuestoElabora = e.getCargo();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Reintegro Contable</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>


<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

<script type="text/javascript">
	var rechazo=0;
	var rfc = "<%=rfc%>";
	
	$(document).ready(function() {		
		$("#rfcDiv").css("display","none");
		$("#aplicarContable").button();
		$("#aplicarContableAut").button();
		$("#btnImprimir").button();
		querySelectPost("catTipoSuplenciaRead", "tipoSuplencia",{async: false });
		querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaVoBo",{async: false });
		
		//Inicializa las ayudas. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.AyudaSyC").subIniciaDlg();
		//Inicializa el autocompletar. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		//$("input.autoCompletaSyC").subIniciaAutoCompleta();
		$("#btnImprimir").hide();
		
		$("#oficioDelegatorioCaptura").hide();		
 		$( "#dFechaOficio" ).datepicker({
 			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});	
		
		$("#oficioDelegatorioVoBo").hide();		
		$( "#dFechaOficioVoBo" ).datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});
		$( "#FechaExp" ).datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});
		
		$("#btnFirmas").hide();	
		//VGC20160126 segun el tipo de reintegro se muestran o se ocultan campos.
		queryFormPost("tipoReintegroRead", {async:false});
		 
		setFechas();		
		
		oTable = $('#dt_reintegro').dataTable({
				"bPaginate": true, 	"bLengthChange": true, "bFilter": true, "bSort": true, "bInfo": true, "bAutoWidth": false,
				"sScrollY": 270, "sScrollYInner": "100%", "bJQueryUI": true, "bRetrive" : true, "bDestroy" : true, "sPaginationType": "full_numbers",
				"sScrollX": "100%", "sScrollXInner": "110%", "bScrollCollapse": true, "bServerSide": true,   
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + 
				"/crud?rt=t&ql=vtReintegroDet&qw=nFolioReintegro="+<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>,
				aoColumns: [
					{ sName: "noCLC" },
					{ sName: "secCLC" },
					{ sName: "EP"},
					{ sName: "mImporte"},
					{ sName: "cxp"}
				],
				oLanguage: {
					sProcessing: "Procesando...", sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar", sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...", sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0", sInfoFiltered: "(filtered from _MAX_ total entries)",
					sInfoPostFix: "", sInfoThousands: ",", sSearch: "Filtro:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				},
				fnDrawCallback:function(oSettings){
					//VGC20160126 segun el tipo de reintegro se muestran o se ocultan campos.
  					obtenInfoTipoReintegro();
				}
				
			});
			
			$("#dt_reintegro tbody").click(function(event) {
 				$(event.target.parentNode).addClass('row_selected');
 			});
 			
 			creaDialogoMotivoRechazo();
			
			$('#dialogAut').dialog({
				autoOpen: false,
				width: 900,
				heigth: 2900
			});
			
			$('#dialogApl').dialog({
				autoOpen: false,
				width: 900,
				heigth: 2900
			});
		    
		    $('#dialogMotor').dialog({
		      	autoOpen: false,
	  			width: 900,
	 			heigth: 2900,
	 			close: function(){
	 				parent.document.getElementById("pb_leave").click();
	 				$("#esperar").dialog("open");
	 			}
		    });
		    $("#dialog-firmantesUpdate").dialog({
				autoOpen: false,
				height: 620,
				width: 500,
				modal: true,
				buttons: {
					"Aceptar": function() {
						if ($("#cNombreVoBo").val() == "" || $("#cboVoBo").val() == -1) {
								alert("Falta Ingresar Nombre en Datos Vº Bº"); return;
							} 
			
						if ($("#cNombreAut").val() == "" || $("#cboAutoriza").val() == -1) {
							alert("Falta Ingresar Nombre en Datos Autorizar"); return;
						} 
			
						$("#cNombreVo").val($("#cNombreVoBo").val());
						$("#cPaternoVo").val($("#cPaternoVoBo").val());
						$("#cMaternoVo").val($("#cMaternoVoBo").val());
						$("#cPuestoVo").val($("#cPuestoVoBo").val());
						$("#cEmpleadoVo").val($("#cboVoBo").val());
			
						$("#cNombreA").val($("#cNombreAut").val());
						$("#cPaternoA").val($("#cPaternoAut").val());
						$("#cMaternoA").val($("#cMaternoAut").val());
						$("#cPuestoA").val($("#cPuestoAut").val());
						$("#cEmpleadoA").val($("#cboAutoriza").val());
			
						$("#cNombreE").val($("#cNombreEla").val());
						$("#cPaternoE").val($("#cPaternoEla").val());
						$("#cMaternoE").val($("#cMaternoEla").val());
						$("#cPuestoE").val($("#cPuestoEla").val());
			
						$("#firmanteVoBo").val($("#cNombreVo").val() + " " + $("#cPaternoVo").val() + " " + $("#cMaternoVo").val());
						$("#firmanteAut").val($("#cNombreA").val() + " " + $("#cPaternoA").val() + " " + $("#cMaternoA").val());
						$("#firmanteEla").val($("#cNombreE").val() + " " + $("#cPaternoE").val() + " " + $("#cMaternoE").val());
			
						var msn = "No Se Guardo Correctamente Informacion de Firmantes";

						try{
							if (confirm("¿Desea actualizar los datos de los firmantes con la informacion capturada?")){
								queryFormPost("tReintegroAutEncabezadoFirmante_Update", {async: false });
														
								
								if ($("#oficioDelegatorio").prop("checked")){
								
									if($("#cFolioOficio").val() == ""){ alert("Falta Ingresar el folio de Oficio."); return; } 
									else if($("#dFechaOficio").val() == ""){ alert("Falta Ingresar la fecha del Oficio."); return; }
									else if($("#cNombreTitular").val() == ""){ alert("Falta Ingresar Nombre del Titular."); return; }
																	
									$("#cFolioOficioAux").val($("#cFolioOficio").val());
									$("#dFechaOficioAux").val($("#dFechaOficio").val());
									$("#cNombreTitularAux").val($("#cNombreTitular").val());
									$("#cApellidoPaternoTitularAux").val($("#cApellidoPaternoSup").val());
									$("#cApellidoMaternoTitularAux").val($("#cApellidoMaternoSup").val());
									$("#cPuestoTitularAux").val($("#cPuestoTitular").val());
									$("#tipoSuplenciaAux").val($("#tipoSuplencia").val());
									$("#numeroEmpleadoAutoriza").val($("#cboSuplenteAut").val());
									
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
									
									$("#cFolioOficioVoBoAux").val($("#cFolioOficioVoBo").val());
									$("#dFechaOficioVoBoAux").val($("#dFechaOficioVoBo").val());
									$("#cNombreTitularVoBoAux").val($("#cNombreTitularVoBo").val());
									$("#cApellidoPaternoTitularVoBoAux").val($("#cApellidoPaternoSupVoBo").val());
									$("#cApellidoMaternoTitularVoBoAux").val($("#cApellidoMaternoSupVoBo").val());
									$("#cPuestoTitularVoBoAux").val($("#cPuestoTitularVoBo").val());
									$("#tipoSuplenciaVoBoAux").val($("#tipoSuplenciaVoBo").val());
									$("#numeroEmpleadoVoBo").val($("#cboSuplenteVoBo").val());
									
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
								
								
								cmdImprimir("REINTEGROCONT");
								if(parent.document.getElementById("pb_send"))
									parent.document.getElementById("pb_send").disabled=false;
							}else{
								if(parent.document.getElementById("pb_save"))
									parent.document.getElementById("pb_save").disabled=false;
							}
						}catch(e){
							if(parent.document.getElementById("pb_save"))
								parent.document.getElementById("pb_save").disabled=false;
							alert("No se pudo actualizar los firmantes, intente mas tarde.");
						}
						
						$("#cboVoBo").empty();
						$("#cboAutoriza").empty();
						
						$(this).dialog("close");
					},
					"Cancelar": function() {
						if(parent.document.getElementById("pb_save"))
							parent.document.getElementById("pb_save").disabled=false;
						$(this).dialog("close");
					}
				},
				close: function(){}							
			});
		
			//creaDialogoFirmantes();			
  			
	});

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
		if(id_oper == 1){
			if(!validaCamposCapturables()){
				return;
			}else{
				revisaRechazo();
				if(!rechazo){
					queryFormPost("reintegroContableUpdate", {async:false});
				}				
				if(document.frmReintegroAut.rdoAutorizar[0].checked){
					mostrarDialog();
				}else{
					$("#esperar").dialog("open");
					return true;
				}
			}
		}
		if(id_oper == 2){
			if(document.frmReintegroAut.rdoAutorizar[0].checked){
				
				rechazo=false;
				//VGC20160126 segun el tipo de reintegro se validan campos.
				
				if( $("#ID_TIPOREINTEGRO").val() == "3" ){
					// TODO Agregar validaciones para el tipo de reintegro 3
					mostrarDialogAut();
				}else if( $("#ID_TIPOREINTEGRO").val() != "3"){ //  || $("#ID_TIPOREINTEGRO").val() == "1" ){ Cuando sea diferente de 3, tendran que capturar la cuenta bancaria.
					if($("#CTABAN").val() == "" || $("#CTABAN_FFM").val() == "" ){
						alert("Favor de capturar la Cuenta Bancaria.");
						return;
					}else{
						actualizaCtaBancaria();
						mostrarDialogAut();
					}	
				}
			}	
					
			if(document.frmReintegroAut.rdoAutorizar[1].checked){
				rechazo=true;
				if($("#motivoRechazo").val() == ""){
					alert("Favor de capturar el motivo del rechazo.");
					return;
				}
								
				cancelarApartadoContable();
			}
		}
	}
	
	// funcion para cargar la plantilla
	function onLoadPlantilla(id_oper) {
		queryFormPost("esViaticoRead", {async : false});
		queryFormPost("existeComisionRead", {async : false});
		cargaProgramaSubPrograma();
		
		if($("#esViatico").val()=="SI"){					
			queryFormPost("infoAgendaComisionPagoRead", {async:false});
			$("#divViaticos").show();
			
			if($("#existeComisionReintegro").val() == "SI"){
				queryFormPost("infoAgendaComisionReintRead", {async:false});
				$("#mPasajeR").attr('disabled','disabled');
				$("#mTaxiR").attr('disabled','disabled');
				$("#mPeajeR").attr('disabled','disabled');
				$("#mHotelR").attr('disabled','disabled');
				$("#mConsumosR").attr('disabled','disabled');
				$("#mOtrosR").attr('disabled','disabled');
				$("#mPasajeLocalR").attr('disabled','disabled');
				$("#mTaxiLocalR").attr('disabled','disabled');
				$("#mGasolinaLocalR").attr('disabled','disabled');
				$("#mPeajeLocalR").attr('disabled','disabled');
			}
		} else {
			$("#divViaticos").hide();
		}
		
		//alert("Se llamo a onLoadPlantilla");		
		$("#esperar").dialog({
			autoOpen : false,
			height : 150,
			width : 200,
			modal : true,
			close : function() {
			}
		});
		var p = window.parent;
		
		if(id_oper == 1){
			if(document.frmReintegroAut.rdoAutorizar[1].checked){
				$("#capturaRechazo_DIV").dialog("open");
  				parent.document.getElementById("pb_cancel").disabled=true;
  				soloLectura();
  				$("#FechaApl").val('<%=re!=null?re.getfAplicacion():"N/A"%>'); 
			}					
		}
		else if(id_oper == 2){
			soloLectura();
			document.getElementById("rdoCorrige").disabled = false;
			document.getElementById("rdoAutoriza").disabled = false;
  			parent.document.getElementById("pb_cancel").disabled=true;
  			$("#btnFirmas").show();
  			$("#btnImprimir").show();	
		}
		else if(id_oper == 3){
			soloLectura();
			$("#FechaApl").attr('readonly',true);
  			document.getElementById("FechaApl").disabled=true;
  			$("#FechaApl").datepicker("destroy");
  			if(document.frmReintegroAut.rdoAutorizar[0].checked){
  				$("#btnImprimir").show();
  			}
  			$("#btnFirmas").show();			 			
		}
		
			$("#mImporte").val('<%=re!=null?re.getImporteLC():""%>');
  			$("#cAvisoReintegro").val('<%=(re!=null && re.getAviso()!=null)?re.getAviso():"N/A"%>');
  			$("#cMovto").val('<%=re!=null?re.getMovimiento():"N/A"%>');
  			$("#nTipoAviso").val('<%=re!=null?re.getTipoAviso():"N/A"%>');
  			$("#nFormaPago").val('<%=re!=null?re.getFormaDePago():"N/A"%>');
  			$("#nCausaAviso").val('<%=re!=null?re.getCausaAviso():"N/A"%>');
  			//$("#FechaApl").val('<%=re!=null?re.getfAplicacion():"N/A"%>');
  			$("#nCtaBancaria").val('<%=re!=null?re.getCuentaBancaria():"N/A"%>');			  			
  			$("#cObservaciones").val('<%=re!=null?re.getObservaciones():"N/A"%>');
  			$("#cConcepto").val('<%=re!=null?re.getConcepto():"N/A"%>');
  			$("#nFolioDependencia").val('<%=re!=null?re.getFolioDependencia():"N/A"%>');  			
			
			if($("#cAvisoReintegro").val()=='null')
  				$("#cAvisoReintegro").val('N/A');
  			if($("#FechaApl").val()=='null')
  				$("#FechaApl").val('N/A');  			
  			if($("#nFolioDependencia").val()=='null')
  				$("#nFolioDependencia").val('N/A');
  			if($("#CTABAN").val()=='null')
  				$("#CTABAN").val('N/A');
  			
	}

	/**
	 * 1
	 * Funion llamada al momento de guardar.
	 * Realiza validaciones,si todo es correcto, regresar true para que continue con el flujo
	 */
	function onSubmit(id_oper) {		
		var p = window.parent;
	  	var valida_campos = true;
		try{
			//validaciones de la forma
			var msgAlert="";

			if(msgAlert!=""){
	  			alert(msgAlert);
	  			return false;
	  		}
	  			  		  		
	  		if (id_oper==1){
	  			if($("#esViatico").val()=="SI"){
					if($("#existeComisionReintegro").val() == "NO"){
						
						let total = 0;
						
						total = Number($("#mPasajeR").val()) + Number($("#mTaxiR").val()) + Number($("#mPeajeR").val()) 
							+ Number($("#mHotelR").val()) + Number($("#mConsumosR").val()) + Number($("#mOtrosR").val());
						
						$("#totalComision").val(total);
						if (total == 0){
							Swal.fire({ icon: 'info',
										text: "Debes capturar el importe a reintegrar en el rubro que le corresponda." });								
							return;
						} else {
							queryFormPost("insertaDetalleComisionReintegro", {async:false});
														
							$("#mPasajeR").attr('disabled','disabled');
							$("#mTaxiR").attr('disabled','disabled');
							$("#mPeajeR").attr('disabled','disabled');
							$("#mHotelR").attr('disabled','disabled');
							$("#mConsumosR").attr('disabled','disabled');
							$("#mOtrosR").attr('disabled','disabled');
							$("#mPasajeLocalR").attr('disabled','disabled');
							$("#mTaxiLocalR").attr('disabled','disabled');
							$("#mGasolinaLocalR").attr('disabled','disabled');
							$("#mPeajeLocalR").attr('disabled','disabled');
						
						}						
					}
				}
	  			
	  			if($("#esFIBBanorte").val()=="SI" ){
		  			if($("#nIDPrograma").val() == "1" || $("#cboSubPrograma").val() == "001"){
						alert("Favor de capturar el Programa y Subprograma de FFM para continuar.")
						continua = false;
					}
					else{
						revisaRechazo();
			  			if(!rechazo){
			  			
			  				queryFormPost("reintegroContableUpdate", {async:false});
			  				if(<%=re!=null%>){
								updateFirmantes();
							}
			  			}	  		
			  			
			  			if(id_oper==1 && $("#esFIBBanorte").val()=="SI" ){
			  				queryFormPost("reintegroContableUpdateFIDBanorte", {async:false});
			  			}		  											
			  			
						parent.document.getElementById("pb_save").disabled=true;
						parent.document.getElementById("pb_send").disabled=false;
						$("#btnFirmas").show();
						}			
					}
				else{
		  			revisaRechazo();
		  			if(!rechazo){
		  				queryFormPost("reintegroContableUpdate", {async:false});
		  				if(<%=re!=null%>){
							updateFirmantes();
						}
		  			}	  		
		  			
		  			if(id_oper==1 && $("#esFIBBanorte").val()=="SI" ){
		  				queryFormPost("reintegroContableUpdateFIDBanorte", {async:false});
		  			}		  											
		  			
					parent.document.getElementById("pb_save").disabled=true;
					parent.document.getElementById("pb_send").disabled=false;
					$("#btnFirmas").show();
					}																		
	  		}
	  		if (id_oper==2){	  			
	  			revisaRechazo();
				document.getElementById("rdoCorrige").disabled = true;
				document.getElementById("rdoAutoriza").disabled = true;
	  			parent.document.getElementById("pb_save").disabled=true;
				parent.document.getElementById("pb_send").disabled=false;
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
			if(rechazo)
				return "CONSULTA_REINTEGROCONT";
			else
				return "AUTORIZA_REINTEGROCONT";
			break;
		
		case 2 :
			if(rechazo)
				return "CAPTURA_REINTEGROCONT";
			else
				return "CONSULTA_REINTEGROCONT";
			break;
					
		case 3 :
			return "CONSULTA_REINTEGROCONT";
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
			if(rechazo)
				return "consulta_reintegrocont";
			else
				return "autoriza_reintegrocont";
			break;
		
		case 2 :			
			if(rechazo)
				return "captura_reintegrocont";
			else
				return "consulta_reintegrocont";						
			break;
					
		case 3 :
			return "consulta_reintegrocont";
			break;
		}
	}
	function soloLectura(){
		$("#fieldsetAut").show();
		$("#cAvisoReintegro").attr('readonly',true);
		document.getElementById("cAvisoReintegro").disabled=true;
  		$("#nFolioDependencia").attr('readonly',true);
  		document.getElementById("nFolioDependencia").disabled=true;
  		document.getElementById("rdoCorrige").disabled = true;
		document.getElementById("rdoAutoriza").disabled = true;
	}
	function revisaRechazo() {
		if(document.frmReintegroAut.rdoAutorizar[0].checked)
			rechazo=false;
		else 
			rechazo=true;
	}
	function setFechas(){
		$( "#fCaptura" ).datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});
				
		$( "#FechaApl" ).datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});
	}
	
	/**
	** Funcion para crear un dialog.
	*/
	function creaDialogoMotivoRechazo() { 
      	$("#capturaRechazo_DIV").dialog({
	        title:"Motivo de Rechazo",
	        autoOpen : false,
	        height : 360,
	        width : 520,
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
	function capturaRechazo(){
		$("#capturaRechazo_DIV").dialog("open");		
	}
	
	function insertMotivoRechazo(){	
		if($("#motivoRechazo").val()==""){
			alert("Favor de capturar el motivo del Rechazo.");
			return;			
		}
	
		$("#movRechazo").val( $("#motivoRechazo").val() );
		queryFormPost("motivoRechazoReintegroUpdate", {async:false});
		$("#capturaRechazo_DIV").dialog("close");		
		
	}
	function aplicaCont(){
			$("#dialogApl").dialog("close");
			if(!confirm("Enviara el Documento a Aplicar Contablemente.  \n \n  ¿desea continuar?")) {
				return false;
			}
			parent.document.getElementById("pb_save").disabled=true;
		   	var strAction="../servlet/ReintegrosContablesServlet";
		   	$.blockUI({message: "Procesando espere ......"});
		   		$.ajax({
		   			datatype:"html",
					type: "POST",
					url: strAction,
					data:{aplica:1},
					success: function (data,textStatus){
						$("#mensajeMotor").val(data);
						$('#dialogMotor').dialog('option', 'modal', false).dialog('open');
		   				$.unblockUI();
					},
					error: function (par) {alert('<%=mensaje%>');}
				});
		   		
   	}
   	function aplicaContAut(){
			$("#dialogAut").dialog("close");
			var fAcredit = $("#FechaApl").val();
			if(!confirm("Enviara el Documento a Autorizar Contablemente.  \n \n  ¿desea continuar?")) {
				return false;
			}
		   	var strAction="../servlet/ReintegrosContablesServlet";
		   	$.blockUI({message: "Procesando espere ......"});
		   		$.ajax({
		   			datatype:"html",
					type: "POST",
					url: strAction,
					data:{autoriza:1,fAcredit:fAcredit},
					success: function (data,textStatus){
						$("#mensajeMotor").val(data);
						validaEsPagoDiverso();
						$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
		   				$.unblockUI();
		   			},
					error: function (par) {alert('<%=mensaje%>');}
				});
   	}
   	function mostrarDialogAut(){
		$('#dialogAut').dialog('option', 'modal', true).dialog('open');	 
	}
	function mostrarDialog(){
		$('#dialogApl').dialog('option', 'modal', true).dialog('open');	 
	}
	
	function cancelarApartadoContable(){
		if(!confirm("Enviara el Documento a Cancelar Contablemente.  \n \n  ¿desea continuar?")) {
			return false;
		}
   		parent.document.getElementById("pb_save").disabled=false;
	 	parent.document.getElementById("pb_save").click();//es para que se calcule el responsable y operacion
   		parent.document.getElementById("pb_save").disabled=true;
   		parent.document.getElementById("pb_send").disabled=true;
   		var strAction="../servlet/ReintegrosContablesServlet";
		$.blockUI({message: "Procesando espere ......"});
   		$.ajax({
   			datatype:"html",
			type: "POST",
			url: strAction,
			data:{cancela:1},
			success: function (data,textStatus){
				$("#mensajeMotor").val(data);
				$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
		   		$.unblockUI();
			},
			error: function (par) {alert('<%=mensaje%>');}
		});		
   }
   
   function validaCamposCapturables(){
   		var continua = true;
   		
   		revisaRechazo();
   		
   		if(!rechazo){
	   		if($("#cAvisoReintegro").val() == 'N/A' || $("#cAvisoReintegro").val() == "" ){
	   			alert("Favor de capturar el aviso de Reintegro para continuar");
	   			continua = false;
	   		}
	   		
	   		if( continua && $("#nFolioDependencia").val() == 'N/A' || $("#nFolioDependencia").val() == "" ){
	   			alert("Favor de capturar el Folio Dependencia para continuar.");
	   			continua = false;
	   		}
	   		
	   		
			if( continua && $("#FechaApl").val() == 'N/A' || $("#FechaApl").val() == ""){
					alert("Favor de capturar la Fecha de Aplicación para continuar.");
					continua = false;
	   		}
	   			   		
   		}
   		
   		return continua;
   }
   
   function actualizaCtaBancaria(){
		var strAction="ReintegroAutomatico.jsp?pago=Si";
		$.blockUI({message: "Procesando espere ......"});
		var ctab;
		
		if (rfc == "BMN930209927")
			ctab = $("#CTABAN_FFM").val();
		else
			ctab = $("#CTABAN").val();
				
		$.ajax({
			datatype:"html",
			type: "POST",
			url: strAction,
			data:{ctab:ctab},
			success: function (data,textStatus){
   				$.unblockUI();   				
			},
			error: function (par) {alert('<%=mensaje%>');}
		});
	}
	
	// crea dialogo pára los firmantes de autorizacion
	function creaDialogoFirmantes() { 
      	$("#dialogFirmantes").dialog({
	        title:"Datos de Firmantes",
	        autoOpen : false,
	        height : 420,
	        width : 500,
	        modal : true,
	        buttons : {
            	"Aceptar" : function() {
                    if($.trim($("#cNombreVoBo").val()) == ""){ alert("Falta Ingresar Nombre en Datos Vº Bº"); return; } 
					else if($.trim($("#cApPaternoVoBo").val()) == ""){ alert("Falta Ingresar Apellido Paterno en Datos Vº Bº"); return; }
					else if($.trim($("#cApMaternoVoBo").val()) == ""){ alert("Falta Ingresar Apellido Materno en Datos Vº Bº"); return; }
					else if($.trim($("#cPuestoVoBo").val()) == ""){ alert("Falta Ingresar Puesto en Datos Vº Bº"); return; }					
					
					if($.trim($("#cNombreAut").val()) == ""){ alert("Falta Ingresar Nombre en Datos Autorizar"); return; } 
					else if($.trim($("#cApPaternoAut").val()) == ""){ alert("Falta Ingresar Apellido Paterno en Datos Autorizar"); return; }
					else if($.trim($("#cApMaternoAut").val()) == ""){ alert("Falta Ingresar Apellido Materno en Datos Autorizar"); return; }
					else if($.trim($("#cPuestoAut").val()) == ""){ alert("Falta Ingresar Puesto en Datos Autorizar"); return; }	
					
					$("#cNombreVo").val($("#cNombreVoBo").val());
					$("#cPaternoVo").val($("#cApPaternoVoBo").val());
					$("#cMaternoVo").val($("#cApMaternoVoBo").val());
					$("#cPuestoVo").val($("#cPuestoVoBo").val());
					$("#cNombreA").val($("#cNombreAut").val());
					$("#cPaternoA").val($("#cApPaternoAut").val());
					$("#cMaternoA").val($("#cApMaternoAut").val());
					$("#cPuestoA").val($("#cPuestoAut").val());
					
					$("#firmanteVoBo").val($("#cNombreVo").val()+" "+$("#cPaternoVo").val()+" "+$("#cMaternoVo").val());
					$("#firmanteAut").val($("#cNombreA").val()+" "+$("#cPaternoA").val()+" "+$("#cMaternoA").val());
					$("#firmanteEla").val($("#cNombreE").val() + " " + $("#cPaternoE").val() + " " + $("#cMaternoE").val());
					
					try{
						if (confirm("¿Desea actualizar los datos de los firmantes con la informacion capturada?")){
							queryFormPost("tReintegroAutEncabezadoFirmante_Update", {async: false });
							$("#cNombreVoBo").val("");
							$("#cApPaternoVoBo").val("");
							$("#cApMaternoVoBo").val("");
							$("#cPuestoVoBo").val("");
							$("#cNombreAut").val("");
							$("#cApPaternoAut").val("");
							$("#cApMaternoAut").val("");
							$("#cPuestoAut").val("");;
							
							
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
							
							cmdImprimir("REINTEGROCONT");
							if(parent.document.getElementById("pb_send"))
								parent.document.getElementById("pb_send").disabled=false;
						}else{
							if(parent.document.getElementById("pb_save"))
								parent.document.getElementById("pb_save").disabled=false;
						}
					}catch(e){
						if(parent.document.getElementById("pb_save"))
							parent.document.getElementById("pb_save").disabled=false;
						alert("No se pudo actualizar los firmantes, intente mas tarde.");
					}
					$(this).dialog("close");  
             	},
              	"Cancelar" : function() {
                	$(this).dialog("close");
                }
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
			+ "&whereTipo= '" + elFormato +"'", 			
			"popacuse",
			"scrollbars=1, resizable=yes, width=1024, height=768");
	}
	function updateFirmantes(){
		queryFormPost({
			queryName:"existeFirmanteReintegroNormal", 
			async : false, 
			callback:function(){
				if (<%=id_oper%>==1 && $("#existeFirmante").val()=="SIEXISTE" ){
					parent.document.getElementById("pb_save").disabled=true;
					parent.document.getElementById("pb_send").disabled=false;
				}else
					//$("#dialogFirmantes").dialog("open");
					modificarFirmantes();
			}
		});
	}
	
	/**
	 * segun el tipo de reintegro se muestran o se ocultan campos.
	 */
  	function obtenInfoTipoReintegro(){
  		var tipoReintegro = parseInt( $("#ID_TIPOREINTEGRO").val(), 10 );
  		/*Reintegro de ejercicios diferentes. No se requiere Cta Bancaria. Se lee el beneficiario del pago.*/
  		if( tipoReintegro == 3 ){
  			$("#lblSubCta").text("RFC Deudor:");
  			$("#ctaBanDiv").css("display","none");
  			$("#rfcDiv").css("display","block");
  			
  			var cxpBuscar = oTable.fnGetData()[0][4];
  			$("#CxPBuscar").val(cxpBuscar);
  			queryFormPost("deudorRFCRead",{async:false});
  		}
  		
  	}
  	
  	function showDivOficio(esUpdate){
		var cmpName = "oficioDelegatorio" + (esUpdate?"Update":"");
		var divName = "oficioDelegatorioCaptura" + (esUpdate?"Update":"");
		if( $("#" + cmpName ).is(":checked") )
			$("#"+divName).show();
		else
			$("#"+divName).hide();
	}
	
	function showDivOficioVoBo(esUpdate){
		var cmpName = "oficioDeleVoBo" + (esUpdate?"Update":"");
		var divName = "oficioDelegatorioVoBo" + (esUpdate?"Update":"");
		if( $("#" + cmpName ).is(":checked") )
			$("#"+divName).show();
		else
			$("#"+divName).hide();
	}
	
	function modificarFirmantes(){		
		tipoFirmantes();
		queryFormPost("firmantesModuloRead, firmantesModuloReadAut", {async : false}); 
		queryFormPost("tPagoFirmanteDelagatorioRead", {async: false }); // Para los firmantes de oficio delegatorio en caso de que existan.
		queryFormPost("tPagoFirmanteDelegatorioVoBoRead", {async: false }); // Para los firmantes de oficio delegatorio VoBo en caso de que existan.
		$("#dialog-firmantesUpdate").dialog("open");
	}
	
	function tipoFirmantes() {		
		llenaFirmanteVoBo();
		llenaFirmanteAut();
		llenaSuplenteVoBo();
		llenaSuplenteAut();
		$("#dialog-firmantesUpdate").dialog("open");
	}	
	
	function llenaFirmanteVoBo() {
		$("#cTipoFirmante").val("VOBO")
		querySelectPost("FirmantesPorTipo_Read", "cboVoBo", {
			async : false
		});
	}
	
	function llenaFirmanteAut() {
		$("#cTipoFirmante").val("AUT")
		querySelectPost("FirmantesPorTipo_Read", "cboAutoriza", {
			async : false
		});
	}
	
	function llenaSuplenteVoBo() {
		$("#cTipoFirmante").val("SUPVOBO")
		querySelectPost("FirmantesPorTipo_Read", "cboSuplenteVoBo", {
			async : false
		});
	
	}
	
	function llenaSuplenteAut() {
		$("#cTipoFirmante").val("SUPAUT")
		querySelectPost("FirmantesPorTipo_Read", "cboSuplenteAut", {
			async : false
		});
	}
	
	function limpiaFirmante(postFijo) {
		$("#cNombre" + postFijo).val("");
		$("#cPaterno" + postFijo).val("");
		$("#cMaterno" + postFijo).val("");
		$("#cPuesto" + postFijo).val("");
	}
	
	function infoEmpleado(tipoFirmante) {
		$("#cNombreEmpleado").val();
		$("#cPaternoEmpleado").val();
		$("#cMaternoEmpleado").val();
		$("#cPuestoEmpleado").val();
		$("#cTipoFirmante").val(tipoFirmante)
	
		var numeroEmpleado = -1;
		var postFijo = ""
	
		if( "VOBO" == tipoFirmante){
	
			numeroEmpleado = $("#cboVoBo").val();
			postFijo = "VoBo";
			$("#nNumEmpleadoVoBo").val( numeroEmpleado );
				
			}else if( "AUT" == tipoFirmante){
				numeroEmpleado = $("#cboAutoriza").val();
				postFijo = "Aut";
				$("#nNumEmpleadoAut").val( numeroEmpleado );
			}else if( "SUPAUT" == tipoFirmante){
				numeroEmpleado = $("#cboSuplenteAut").val();
				postFijo = "Titular";
				
			}else if( "SUPVOBO" == tipoFirmante){
				numeroEmpleado = $("#cboSuplenteVoBo").val();
				postFijo = "TitularVoBo";
				
			}

	
		limpiaFirmante(postFijo);
	
		if (parseInt(numeroEmpleado, 10) > 0) {
			$("#nNumEmpleadoBusqueda").val(numeroEmpleado);
			queryFormPost({
				queryName : "infoComplementariaFirmanteRead",
				async : false,
				callback : function() {
					$("#cNombre" + postFijo).val($("#cNombreEmpleado").val());
					$("#cPaterno" + postFijo).val($("#cPaternoEmpleado").val());
					$("#cMaterno" + postFijo).val($("#cMaternoEmpleado").val());
					$("#cPuesto" + postFijo).val($("#cPuestoEmpleado").val());
				}
			});
		}
	
	}
	
	function validaEsPagoDiverso(){
		$("#esCXPPagoDiverso").val("");
		$("#nFolioPAGODIVERSO").val("");
		$("#caNoContrarrecibo").val("");
		$("#cIdpedContDef").val("");
		$("#cIdRecepMat").val("");
		
		queryFormPost("esCXPPagoDiversoRead", {async : false});
		
		if($("#esCXPPagoDiverso").val() == "1"){
			queryFormPost("datosPagoDiversoReintegroCont_Read", {async : false});
			queryFormPost("activaRecepMaterialUpdate", {async : false});
			queryFormPost("pagoDiversoFacturaDelete", {async : false});
		}
	}
	
	function cargaProgramaSubPrograma(){		
		
		queryFormPost("esBeneficiarioFIBBanorteReintegroRead", {async : false});
		
		if (<%=id_oper%>==1 && $("#esFIBBanorte").val()=="SI" ){
			mostrarOcultarProgSubProg(true, true);
			querySelectPost( "tfideicomisoReintegrosRead", "cboPrograma", {async : false} );
			cambiaPrograma();
		}else{
			if ($("#esFIBBanorte").val()=="SI" ){
				mostrarOcultarProgSubProg(false, true);				
				querySelectPost( "tfideicomisoReintegrosRead", "cboPrograma", {async : false} );
				queryFormPost("tReintegroEncProgSubProgRead", {async : false});
				$("#cboPrograma").val($("#nIDPrograma").val());
				cambiaPrograma();
				$("#cboSubPrograma").val($("#cSubPrograma").val());								
			}else{
				mostrarOcultarProgSubProg(false, false);
			}
		}
	}
	
	function mostrarOcultarProgSubProg(sel, mostrar){
		if(mostrar){
			$("#lblPrograma").show();
			$("#cboPrograma").show();
			$("#lblSubPrograma").show();
			$("#cboSubPrograma").show();
		}else{
			$("#lblPrograma").hide();
			$("#cboPrograma").hide();
			$("#lblSubPrograma").hide();
			$("#cboSubPrograma").hide();
		}
		
		if(!sel){			
			$("#cboPrograma").attr('disabled','disabled');
			$("#cboSubPrograma").attr('disabled','disabled');
		}else{
			$("#cboPrograma").attr('disabled',false);
			$("#cboSubPrograma").attr('disabled',false);
		}
	}
	
	/**
	 * Actualiza los subprogramas segun la seleccion del programa
	 */
	function cambiaPrograma() {
		$("#nIDPrograma").val($("#cboPrograma").val());
		clearSelect("cboSubPrograma");
		querySelectPost("catalogoSubprgRead", "cboSubPrograma", {
			async : false
		});
	}
	
	/**
	 * Funcion general que limpia el contenido de un select agregando una opcion por
	 * default con valor -1
	 */
	function clearSelect(idSel) {
		for ( var i = 0; i < idSel.length; i++)
			$('#' + idSel[i]).find('option').remove().end().append(
					'<option value="-1"></option>');
	}
	
	function cierraAyuda() 
	{
		try
		{
			$("#divACnCuenta").hide();
		}	
		catch(ex)
		{
			alert("Error 0044js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema.");
		}
	}
	
</script>

</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form id="frmReintegroAut" name="frmReintegroAut">
		<div id="container" class="container" style="width: 80%">
			<div class="card-header"> <h3>Solicitud de Liberacion</h3> </div>					
			<hr class="mt-3">	
			
			<input type="hidden" name="CxPBuscar" id="CxPBuscar" value="" />
			<input type="hidden" name="ID_TIPOREINTEGRO" id="ID_TIPOREINTEGRO" value="" />
			<input type="hidden" name="movRechazo" id="movRechazo" value="" />
			<input type="hidden" name="idCaso" id="idCaso" value="<%=idCaso%>" />
			<input type="hidden" name="FOLIO" id="FOLIO" value="<%=folio%>" />
			<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0) == null ? " caso operacion nulo " : c.getCasoOperacion(0).getResponsable()%>" />
			<input type="hidden" name="FECHA_SOLICITUD" id="FECHA_SOLICITUD" value="<%=today%>" />
			<input type="hidden" name="EJERCICIO_FISCAL" id="EJERCICIO_FISCAL" value="<%=aEjercicioFiscal%>" />
			
			<!-- hidden para los firmantes de VoBo y Autoriza -->
			<input type="hidden" id="cNombreVoBo" name="cNombreVoBo" size=40 >
			<input type="hidden" id="cPaternoVoBo" name="cPaternoVoBo" size=40 >
			<input type="hidden" id="cMaternoVoBo" name="cMaternoVoBo" size=40>
			<input type="hidden" id="cPuestoVoBo" name="cPuestoVoBo" size=40>
			<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" size=40>
			<input type="hidden" id="cNombreAut" name="cNombreAut" size=40 >
			<input type="hidden" id="cPaternoAut" name="cPaternoAut" size=40 >
			<input type="hidden" id="cMaternoAut" name="cMaternoAut" size=40>
			<input type="hidden" id="cPuestoAut" name="cPuestoAut" size=40>
			<input type="hidden" id="firmanteAut" name="firmanteAut" size=40>
			
			<input type="hidden" id="cNombreVo" name="cNombreVo"  />
			<input type="hidden" id="cPaternoVo" name="cPaternoVo"  />
			<input type="hidden" id="cMaternoVo" name="cMaternoVo" />
			<input type="hidden" id="cPuestoVo" name="cPuestoVo" />
			<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" />
			<input type="hidden" id="cNombreA" name="cNombreA"  />
			<input type="hidden" id="cPaternoA" name="cPaternoA"  />
			<input type="hidden" id="cMaternoA" name="cMaternoA" />
			<input type="hidden" id="cPuestoA" name="cPuestoA" />
			<input type="hidden" id="firmanteAut" name="firmanteAut" />
			<input type="hidden" name="tipoSuplenciaAux" id="tipoSuplenciaAux" value="">
			<input type="hidden" name="tipoSuplenciaVoBoAux" id="tipoSuplenciaVoBoAux" value="">
			<input type="hidden" id="TipoAutorizacion" name="TipoAutorizacion" value="">
			<input type="hidden" id="cNombreEmpleado" name="cNombreEmpleado" value="">
			<input type="hidden" id="cPaternoEmpleado" name="cPaternoEmpleado" value="">
			<input type="hidden" id="cMaternoEmpleado" name="cMaternoEmpleado" value="">
			<input type="hidden" id="cPuestoEmpleado" name="cPuestoEmpleado" value="">
			<input type="hidden" id="cTipoFirmante" name="cTipoFirmante" value="">
			<input type="hidden" name="nNumEmpleado" id="nNumEmpleado" value=""/>
			<input type="hidden" name="nNumEmpleadoBusqueda" id="nNumEmpleadoBusqueda" value=""/>
			<input type="hidden" id="numeroEmpleadoVoBo" name="numeroEmpleadoVoBo" value="">
			<input type="hidden" id="numeroEmpleadoAutoriza" name="numeroEmpleadoAutoriza" value="">
			
			<input type="hidden" id="folioReint" name="folioReint"  value="<%=folio%>"/>
			<input type="hidden" id="existeFirmante" name="existeFirmante"  value="NOEXISTE"/>
			
			<!-- hidden para la captura de los datos de quien elaboro -->
			<input type="hidden" id="cNombreE" name="cNombreE"  value="<%=cNombreElabora%>" />
			<input type="hidden" id="cPaternoE" name="cPaternoE"  value="<%=cApellidoPaternoElabora%>" />
			<input type="hidden" id="cMaternoE" name="cMaternoE"  value="<%=cApellidoMaternoElabora%>" />
			<input type="hidden" id="cPuestoE" name="cPuestoE"  value="<%=cPuestoElabora%>" />
			<input type="hidden" id="firmanteEla" name="firmanteEla" >
			
			<!-- hidden para la captura de oficio delegatorio -->
			<input type="hidden" name="cFolioOficioAux" id="cFolioOficioAux" value=""/>
			<input type="hidden" name="dFechaOficioAux" id="dFechaOficioAux" value=""/>
			<input type="hidden" name="cNombreTitularAux" id="cNombreTitularAux" value=""/>
			<input type="hidden" name="cApellidoPaternoTitularAux" id="cApellidoPaternoTitularAux" value=""/>
			<input type="hidden" name="cApellidoMaternoTitularAux" id="cApellidoMaternoTitularAux" value=""/>
			<input type="hidden" name="cPuestoTitularAux" id="cPuestoTitularAux" value=""/>
			<input type="hidden" name="firmanteOficioExiste" id="firmanteOficioExiste" value=""/>
			
			<!-- hidden para la captura de oficio delegatorio VoBo-->
			<input type="hidden" name="cFolioOficioVoBoAux" id="cFolioOficioVoBoAux" value=""/>
			<input type="hidden" name="dFechaOficioVoBoAux" id="dFechaOficioVoBoAux" value=""/>
			<input type="hidden" name="cNombreTitularVoBoAux" id="cNombreTitularVoBoAux" value=""/>
			<input type="hidden" name="cApellidoPaternoTitularVoBoAux" id="cApellidoPaternoTitularVoBoAux" value=""/>
			<input type="hidden" name="cApellidoMaternoTitularVoBoAux" id="cApellidoMaternoTitularVoBoAux" value=""/>
			<input type="hidden" name="cPuestoTitularVoBoAux" id="cPuestoTitularVoBoAux" value=""/>
			<input type="hidden" name="firmanteOficioVoBoExiste" id="firmanteOficioVoBoExiste" value=""/>
			
			<input name="cDocumento" type="hidden" id="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>"/>
			<input name="nFolioPago" id="nFolioPago" type="hidden" value="<%=folio%>"/>
			
			<!-- para validar si el contrarrecibo es de un pago diverso -->
			<input type="hidden" id="esCXPPagoDiverso" name="esCXPPagoDiverso" value="" />
			<input type="hidden" id="nFolioPAGODIVERSO" name="nFolioPAGODIVERSO" value="" />
			<input type="hidden" id="caNoContrarrecibo" name="caNoContrarrecibo" value="" />
			<input type="hidden" id="cIdpedContDef" name="cIdpedContDef" value="" />
			<input type="hidden" id="cIdRecepMat" name="cIdRecepMat" value="" />
			
			<input type="hidden" id="esFIBBanorte" name="esFIBBanorte" value="" />
			<input type="hidden" id="RFCFIBBanorte" name="RFCFIBBanorte" value="BMN930209927" />
			<input type="hidden" id="cSubPrograma" name="cSubPrograma" value="" />
			<input type="hidden" id="nIDPrograma" name="nIDPrograma"  value="">
			<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value = "<%=cUR%>">
			
			<input type="hidden" id="esViatico" name="esViatico" value = "NO">
			<input type="hidden" id="existeComisionReintegro" name="existeComisionReintegro" value = "NO">						
			<input type="hidden" id="nIdComisionViatico" name="nIdComisionViatico" value = "">
			<input type="hidden" id="cEventoComision" name="cEventoComision" value = "">		
			<input type="hidden" id="totalComision" name="totalComision" value = "">
			
			<div class="container" id="fieldsetAut" style="display: none;">
				<div class="row d-flex justify-content">
					<div class="col-12 d-flex justify-content-center">
						<label class="form-label"> ¿Datos correctos?: </label>&nbsp;&nbsp;&nbsp;&nbsp;
						<label for="rdoAutoriza" class="form-check-label">Autorizar: </label> &nbsp;&nbsp;
						<input type="radio" name="rdoAutorizar" id="rdoAutoriza" class="form-check-input" value="Autorizar" checked />&nbsp;&nbsp;&nbsp;&nbsp;
						
						<label for="autorizaNo" class="form-check-label">Rechazar: </label>&nbsp;&nbsp;
						<input type="radio" name="rdoAutorizar" id="rdoCorrige" class="form-check-input" value="Corregir" onclick="capturaRechazo()"/>&nbsp;&nbsp;&nbsp;&nbsp;						
					</div>																		
				</div>			
			</div>
			
			<div id="esperar" align="center" title="Espera">
				<fieldset>
					<table>
						<tr>
							<td>Espere por favor.... <img border="0" src="../imagenes/espera.gif" height="30"/> </td>
						</tr>
					</table>
				</fieldset>
			</div>
			
			<div id="dialogApl" title="Detalle de Reintegros">
				<div class="row">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex">
						<p>Aplica Apartado Presupuestal </p>															
					</div>
				</div>	
				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">														
						<input type="button" class="btn btn-secondary btn-sm" id="aplicarContable" value="Aplicar apartado" onclick="aplicaCont();" />
					</div>
				</div>	
			</div>
			
			<div id="dialogAut" title="Detalle de Reintegros">
				<div class="row">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex">
						<p>Autorización de Reintegros del Presupuesto</p>								
					</div>
				</div>
				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">								
						<input type="button" class="btn btn-secondary btn-sm" id="aplicarContableAut" value="Autorizar contablemente" onclick="aplicaContAut();" />
					</div>
				</div>
			</div>
						
			<div id="dialogMotor" title="Mensajes del sistema Aviso de Reintegros">
				<p>Mensajes del sistema </p>
				<a rel=""></a>
				<textarea id="mensajeMotor" name="mensajeMotor" rows="8" cols="120"></textarea>
			</div>
		
			<br/>
			
			<h5> General </h5>
			<hr class="mt-3">
			
			<div class="row d-flex">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<label for="nFolioReintegro" class="form-label"> Folio </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="text" class="form-control form-control-sm" name="nFolioReintegro" id="nFolioReintegro" size="15" value="<%=c.getFolio() %>" readonly>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<label for="fCaptura" class="form-label"> Fecha </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="text" class="form-control form-control-sm" name="fCaptura" id="fCaptura" size="15" value="<%=today%>" readonly>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<label for="mImporte" class="form-label"> Total </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>					
						<input  type="text" id="mImporte" name="mImporte" class="form-control form-control-sm" readonly>
					</div>	
				</div>
			</div>	
			
			<br/>
			
			<div id="divViaticos">
				<div class="form-group row">
					<div class="col-4">
					</div>
					<label class="col-2 col-form-label" for="mPasaje">Detalle Comision </label>														
					<label class="col-2 col-form-label" for="mPasaje">Rubro a Reintegrar </label>
				</div>
				<div class="form-group row">
					<div class="col-2">
					</div>
				  	<label class="col-2 col-form-label" for="mPasaje">Pasajes </label>
				  	<div class="col-2">								  		
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-bus-front"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mPasaje" name="mPasaje" value="0.00" readonly/>			              					
		              	</div>
	              	</div>
	              	<div class="col-2">								  		
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-bus-front"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mPasajeR" name="mPasajeR" value="0.00"/>			              					
		              	</div>
	              	</div>
	            </div>
	            <div class="form-group row">
	            	<div class="col-2">
					</div>
	              	<label class="col-2 col-form-label" for="mTaxi">Taxi </label>
              		<div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-taxi-front"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mTaxi" name="mTaxi" value="0.00" readonly/>
		              	</div>
	             	</div>
	             	<div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-taxi-front"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mTaxiR" name="mTaxiR" value="0.00" />
		              	</div>
	             	</div>
	            </div>
	            <div class="form-group row">
	            	<div class="col-2">
					</div>
	              	<label class="col-2 col-form-label" for="mPeaje">Combustible y peaje</label>
              		<div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mPeaje" name="mPeaje" value="0.00" readonly/>
		              	</div>
		            </div>
		            <div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mPeajeR" name="mPeajeR" value="0.00" />
		              	</div>
		            </div>
	            </div>
	            <div class="form-group row">
	            	<div class="col-2">
					</div>
	              	<label class="col-2 col-form-label" for="mHotel">Factura(s) de Hotel </label>
	              	<div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-building"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mHotel" name="mHotel" value="0.00" readonly/>
		              	</div>
		            </div>
		            <div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-building"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mHotelR" name="mHotelR" value="0.00" />
		              	</div>
		            </div>
	            </div>
	            <div class="form-group row">
	            	<div class="col-2">
					</div>
	              	<label class="col-2 col-form-label" for="mConsumos">Consumos </label>
	              	<div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-cash"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mConsumos" name="mConsumos" value="0.00" readonly/>
		              	</div>
		              </div>
		              <div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-cash"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mConsumosR" name="mConsumosR" value="0.00" />
		              	</div>
		              </div>
	            </div>
	            <div class="form-group row">
	            	<div class="col-2">
					</div>
	              	<label class="col-2 col-form-label" for="mOtros">Otros </label>
				   	<div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-coin"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mOtros" name="mOtros" value="0.00" readonly/>
		              	</div>
		              </div>
		              <div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-coin"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mOtrosR" name="mOtrosR" value="0.00" />
		              	</div>
		              </div>
	            </div>	
	            
	            <div class="form-group row">
	            	<div class="col-2">
					</div>
				  	<label class="col-2 col-form-label" for="mPasajeLocal">Pasajes </label>
				  	<div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-bus-front"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mPasajeLocal" name="mPasajeLocal" value="0.00" readonly/>
		              	</div>
		             </div>
		             <div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-bus-front"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mPasajeLocalR" name="mPasajeLocalR" value="0.00" />
		              	</div>
		             </div>
	            </div>
	            <div class="form-group row">
	            	<div class="col-2">
					</div>
	              	<label class="col-2 col-form-label" for="mTaxiLocal">Taxi </label>
	              	<div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-taxi-front"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mTaxiLocal" name="mTaxiLocal" value="0.00" readonly/>
		              	</div>
		            </div>
		            <div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-taxi-front"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mTaxiLocalR" name="mTaxiLocalR" value="0.00" />
		              	</div>
		            </div>
	            </div>
	            <div class="form-group row">
	            	<div class="col-2">
					</div>
	              	<label class="col-2 col-form-label" for="mPeajeLocal">Peaje y Estacionamiento</label>
				   	<div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mPeajeLocal" name="mPeajeLocal" value="0.00" readonly/>
		              	</div>
		            </div>
		            <div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mPeajeLocalR" name="mPeajeLocalR" value="0.00" />
		              	</div>
		            </div>
	            </div>
	            <div class="form-group row">
	            	<div class="col-2">
					</div>
	              	<label class="col-2 col-form-label" for="mGasolinaLocal">Combustible</label>
				   	<div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mGasolinaLocal" name="mGasolinaLocal" value="0.00" readonly/>
		              	</div>
		            </div>
		            <div class="col-2">
					   	<div class="input-group">
			    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
             					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mGasolinaLocalR" name="mGasolinaLocalR" value="0.00" />
		              	</div>
		            </div>
	            </div>
	        </div>
			
			<br/>
			
			<h5> Datos de Encabezado </h5>
			<hr class="mt-3">			
			
			<div class="row d-flex">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="cAvisoReintegro" class="form-label"> Aviso Reintegro: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="text" name="cAvisoReintegro" id="cAvisoReintegro" class="form-control form-control-sm">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="nFolioDependencia" class="form-label"> Folio Dependencia: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="text" name="nFolioDependencia" id="nFolioDependencia" class="form-control form-control-sm">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="FechaApl" class="form-label"> Fecha Apl.: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-calendar"></i></span>	
						<input type="text" name="FechaApl" id="FechaApl" class="form-control form-control-sm" value="<%=today%>"/>
					</div>
				</div>
			</div>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="cMovto" class="form-label"> Movto: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-1 col-sm-12 p-1">
					<div class="input-group">
						<input type="text" name="cMovto" id="cMovto" size="5" class="form-control form-control-sm" readonly>
					</div>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="nTipoAviso" class="form-label"> Tipo de Aviso: </label>
				</div>				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="input-group">
						<input type="text" name="nTipoAviso" id="nTipoAviso" class="form-control form-control-sm" readonly>
					</div>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="CTAB" class="form-label"> Cta Bancaria: </label>
				</div>												
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">														
					<%if("BMN930209927".equals(rfc)){ %>
						<div class="input-group">														
							<input type="text" name="CTABAN_FFM" id="CTABAN_FFM" value="" class="form-control form-control-sm AyudaSyC" onkeydown="return onfocus="cierraAyuda()" readonly/>
						</div>																				
					<%} else {%>
						<div class="input-group">
							<input type="text" name="CTABAN" id="CTABAN" value="" class="form-control form-control-sm AyudaSyC" onkeydown="return onfocus="cierraAyuda()" readonly/>
						</div>
					<%}%>							
					<div id="rfcDiv">
						<input type="text" id="RFC" name="RFC" size="18"  class="form-control form-control-sm"/> 
					</div>	
				</div>				
			</div>
			
			<div class="row d-flex">					
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="nCausaAviso" class="form-label"> Causa del Aviso: </label>
					</div>												
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">						
						<div class="input-group">								
							<input type="text" name="nCausaAviso" id="nCausaAviso" class="form-control form-control-sm" readonly/>
						</div>														
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="nFormaPago" class="form-label"> Forma de Pago: </label>
					</div>												
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																												
						<div class="input-group">														
							<input type="text" name="nFormaPago" id="nFormaPago" value="" class="form-control form-control-sm" readonly/>
						</div>																																		
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="FechaExp" class="form-label"> Fecha Exp.: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-calendar"></i></span>	
							<input type="text" name="FechaExp" id="FechaExp" class="form-control form-control-sm" value="<%=" 31/12/ "+aEjercicioFiscal%>"/>
						</div>
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="cObservaciones" class="form-label"> Observaciones: </label>
					</div>												
					<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
						<textarea class="form-control form-control-sm" rows=3 cols="35" id="cObservaciones" name="cObservaciones"></textarea>
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="cConcepto" class="form-label"> Concepto: </label>
					</div>												
					<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
						<textarea class="form-control form-control-sm" rows=3 cols="35" id="cConcepto" name="cConcepto"></textarea>
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label id="lblPrograma" for="cboPrograma" class="form-label"> Programa: </label>
					</div>												
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<select class="form-select form-select-sm" id="cboPrograma" name="cboPrograma" onchange="cambiaPrograma()"></select>
					</div>
				</div>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label id="lblSubPrograma" for="cboSubPrograma" class="form-label"> SubPrograma: </label>
					</div>												
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						<select class="form-select form-select-sm" id="cboSubPrograma" name="cboSubPrograma" onchange="cambiaPrograma()"></select>
					</div>
				</div>
		
			<div>				
			
			<div class="row d-flex justify-content">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<span id="btnFirmas" ><a href="#" onclick="modificarFirmantes();" >*Firmas</a></span>
					<div class="input-group">																										
						<span class="input-group-text"><i class="bi bi-printer"></i></span>
						<input type="button" class="btn btn-secondary btn-sm" name="btnImprimir" id="btnImprimir" value="Reimprimir Poliza" onclick="cmdImprimir('REINTEGROCONT')" >						
					</div>
				</div>
			</div>		
				
				<br/>		
						
				<div class="table-responsive">	    
					<table id="dt_reintegro" class="table table-striped">						
						<thead>
							<tr>
								<th>noCLC</th>
								<th>sec</th>
								<th>EP</th>
								<th>mImporte</th>
								<th>Cuenta por Pagar</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
		</div>
		
		<div id="capturaRechazo_DIV" class="container">
			<div class="col-12 col-lg-12 col-md-12 col-sm-12">									
				<label for="motivoRechazo" class="form-label">Motivo:</label>				
				<textarea id="motivoRechazo" name="motivoRechazo" class="form-control form-control-sm" rows="4" cols="1000" onkeypress="return event.keyCode!=13"></textarea>								
			</div>			
		</div>
		
		<div id="dialog-firmantesUpdate" title="Actualizacion de Firmantes" class="container">
			<div class="row">
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
					<input type="checkbox" class="form-check-input" id="oficioDelegatorio" name="oficioDelegatorio" onclick="showDivOficio(false);"/>Oficio Delegatorio Autoriza
				</div>
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
					<input type="checkbox" class="form-check-input" id="oficioDeleVoBo" name="oficioDeleVoBo" onclick="showDivOficioVoBo(false);"/>Oficio Delegatorio VoBo
				</div>
			</div>								
		
			<br/>
			
			<h5> Datos VºBº </h5>
			<hr class="mt-3">		
				
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cboVoBo" class="form-label"> VoBo: </label>		
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<select class="form-select form-select-sm" id="cboVoBo" name="cboVoBo" onchange="infoEmpleado('VOBO');"> </select>
				</div>
			</div>
			
			<br/>
			
			<h5> Datos Autoriza </h5>
			<hr class="mt-3">		
				
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cboAutoriza" class="form-label"> Autoriza: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<select class="form-select form-select-sm" id="cboAutoriza" name="cboAutoriza" onchange="infoEmpleado('AUT');"> </select>
				</div>
			</div>	
			
			<br/>
			
			<h5> Datos Elabora </h5>
			<hr class="mt-3">		
				
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cboAutoriza" class="form-label"> Nombre: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cNombreEla" name="cNombreEla" size=40 maxlength="70" value="<%=cNombreElabora%>"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cboAutoriza" class="form-label"> Ap. Paterno: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cPaternoEla" name="cPaternoEla" size=40 maxlength="70" value="<%=cApellidoPaternoElabora%>"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cboAutoriza" class="form-label"> Ap. Materno: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cMaternoEla" name="cMaternoEla" size=40 maxlength="70" value="<%=cApellidoMaternoElabora%>"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cboAutoriza" class="form-label"> Puesto: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cPuestoEla" name="cPuestoEla" size=40 maxlength="70" value="<%=cPuestoElabora%>"/>
				</div>
			</div>				
			
			<br/>
			
			<div id="oficioDelegatorioCaptura">			
				<h5> Datos del Suplente </h5>
				<hr class="mt-3">		
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cFolioOficio" class="form-label"> No Oficio: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">						
						<input type="text" class="form-control form-control-sm" id="cFolioOficio" name="cFolioOficio" size=30 maxlength="70" />
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="dFechaOficio" class="form-label"> Fecha: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">			
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-calendar"></i></span>			
							<input type="text" class="form-control form-control-sm" id="dFechaOficio" name="dFechaOficio" readonly/>
						</div>
					</div>
				</div>		
				
				<div class="row">					
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="tipoSuplencia" class="form-label"> T Suplencia: </label>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">						
						<select class="form-select form-select-sm" id="tipoSuplencia" name="tipoSuplencia" > </select>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cboSuplenteAut" class="form-label"> Autoriza: </label>
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">						
						<select class="form-select form-select-sm" id="cboSuplenteAut" name="cboSuplenteAut" onchange="infoEmpleado('SUPAUT');"> </select>
					</div>
				</div>
				
			</div>
			
			<br/>
			
			<div id="oficioDelegatorioVoBo">
				<h5> Datos del Suplente VoBo</h5>
				<hr class="mt-3">	
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cFolioOficioVoBo" class="form-label"> No Oficio: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">						
						<input type="text" class="form-control form-control-sm" id="cFolioOficioVoBo" name="cFolioOficioVoBo" size=30 maxlength="70" />
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="dFechaOficioVoBo" class="form-label"> Fecha: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">			
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-calendar"></i></span>			
							<input type="text" class="form-control form-control-sm" id="dFechaOficioVoBo" name="dFechaOficioVoBo" readonly/>
						</div>
					</div>
				</div>		
				
				<div class="row">					
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="dFechaOficioVoBo" class="form-label"> T Suplencia: </label>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">						
						<select class="form-select form-select-sm" id="tipoSudFechaOficioVoBoplencia" name="dFechaOficioVoBo" > </select>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cboSuplenteVoBo" class="form-label"> VoBo: </label>
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">						
						<select class="form-select form-select-sm" id="cboSuplenteVoBo" name="cboSuplenteVoBo" onchange="infoEmpleado('SUPVOBO');"> </select>
					</div>
				</div>
				
			</div>
			
		</div>
	</form>
</body>

</html>