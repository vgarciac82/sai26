<%@page import="com.syc.contable.servlet.RectificacionIngresoFiscalBusinessLogic"%>
<%@ page import="com.syc.contable.core.RectificacionIngresoFiscal"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.syc.gestion.core.*"%>
<%@ page import="com.syc.gestion.servlet.*"%>
<%@ page import="com.syc.gestion.util.*"%>
<%@ page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@ page import="com.syc.gestion.CasoBusinessLogic"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page import="java.io.File"%>
<%@ page import="org.slf4j.Logger"%>
<%@ page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@ page import="com.syc.contable.core.RectificacionIngresoFiscalEncabezado"%>
<%@ page import="com.syc.contable.core.RectificacionIngresoFiscalDetalle"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%!private static Logger log = LoggerFactory.getLogger("com.syc.plantillas.casos.RectificacionIngreso.jsp");%>
<%
	boolean cGrupoUSR = false;
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String cUsrLog = "";
	String mensaje = "";
	String msg = "";
	String aEjercicioFiscal = "";
	
	//SACA LA FECHA DE HOY
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	
	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
	
	Caso caso = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	if (caso == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	Empleado empleado = new Empleado();
	EmpleadoBusinessLogic empleadobl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	RectificacionIngresoFiscalBusinessLogic rectificacion = new RectificacionIngresoFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	EmpleadoArea empleadoarea = new EmpleadoArea();
	CasoBusinessLogic casobl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	String select = caso.getTipoCaso().getGavetaAsociada() + "_G" + caso.getIdGabinete();//se usa por separado abajo
	
	boolean esConsulta = ( caso == null? false : ( caso.getCasoOperacion(0) == null? false : (  caso.getCasoOperacion(0).getOperacion() == null ? false : ( "consulta_rectificaingreso".equalsIgnoreCase( caso.getCasoOperacion(0).getOperacion().getNombre() ) )  ) )  );
	String operacionActual = ( caso != null? (  caso.getCasoOperacion(0) != null? (  caso.getCasoOperacion(0).getOperacion() != null? caso.getCasoOperacion(0).getOperacion().getNombre() : ""   ) : "" ) : "" ) ;
	
	String cAplicaDocto = "No";
	Map<?,?> m = CasoDatoManager.readValuesCasoDato(request, caso.getCasoDato(), true);
	String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
	int id_oper = -1;
	boolean reload = true;
	double valor = 0.00;
	String strValor = "0.00";
	boolean porEXCEL = false; //VARIABLE QUE SIRVE PARA SABER SI SE VA A SUBIR A TRAVES DE EXCEL FMC 30/oct

	empleado.setClaveUsuario(usuario.getLogin());
	empleado = empleadobl.getEmpleado(empleado);
	empleadoarea.setId(empleado.getClaveArea());
	empleadoarea = empleadobl.getEmpleadoArea(empleadoarea);

	if (session.getAttribute("mensaje") != null)
		msg = (String) session.getAttribute("mensaje");

	session.removeAttribute("mensaje");

	RectificacionIngresoFiscal rectificacionCapturada = null;
	
	if (session.getAttribute("RECTIFICAINGRESO") != null) {
		rectificacionCapturada = (RectificacionIngresoFiscal) session.getAttribute("RECTIFICAINGRESO");
		reload = false;
		session.removeAttribute("RECTIFICAINGRESO");
	}

	final int folio = new Integer(caso.getFolio().substring(caso.getFolio().lastIndexOf('-') + 1)).intValue();
	
	RectificacionIngresoFiscalEncabezado rec = rectificacion.getRectificacionEncabezado(folio);
	ArrayList<RectificacionIngresoFiscalDetalle> rds = rectificacion.getRectificacionDetalle(folio);
	
	boolean existeDetalle = false; //checamos si ya existe un detalle cargado: si hay lo agregamos al grid en paso 1; sino es paso 1, usamos la vista; si no hay nada, agregamos.

	if(!rds.isEmpty())
	    existeDetalle=true;
	
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = caso.getCasoOperacion(0).getIdOperacion();

	
	if (request.getParameter("aplicaDocto") != null && request.getParameter("aplicaDocto").equals("Si"))
		cAplicaDocto = "Si";
	
	String cAutorizaDocto = "No";
	if (request.getParameter("aut") != null && request.getParameter("aut").equals("Si"))
		cAutorizaDocto = "Si";
	
	boolean captura=true;
	if (request.getParameter("mensajerem") != null && request.getParameter("mensajerem").equals("si"))
		captura=false;
	
	//Valida Centro de Costos
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE"))
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();

	if (cCentroContable.isEmpty() || cCentroContable.equals(""))
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";

	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	cUsrLog = usuario.getLogin();
	cGrupoUSR = (usuario.getGrupos() != null && usuario.getGrupos().containsKey("AUTORIZADOR_RECTIFICACION")) ? true : false;
	String sResultadoAut =null;
	
	boolean busqueda = false;
	if(request.getParameter("busqueda")!=null){
		if("SI".equals(request.getParameter("busqueda").toUpperCase()))
		    busqueda = true;
	}

	mensaje = mensaje.replace("[", "");
	mensaje = mensaje.replace("]", "");
	mensaje = mensaje.replace("'", "");
	mensaje = mensaje.replace(",", "<br>");
	mensaje = mensaje.replace("\n", "<br>");
	mensaje = Util.encodeJS(mensaje);
	
	FortimaxFile[] archivoPDFCXP = null; //id_oper 3 (COMPLEMENTARIO)
	archivoPDFCXP = casobl.getArchivosDeDocumento(caso.getTipoCaso().getGavetaAsociada(), caso.getIdGabinete(), 0, 1);
	
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Rectificación de Ingresos</title>
<link rel="stylesheet" type="text/css"  href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<script type="text/javascript" src="../Generador/js/jquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../js/RectificacionIngreso.js"></script>
<script type="text/javascript" charset="utf-8">

var oTable;
var aTrs;
var generaRenglon = false;
var epTemporal="";
var esConsulta = <%=esConsulta%>;
var operacionActual = "<%=operacionActual%>";

$(document).ready(function(){
	
	$('#concepto').bind('copy paste', function (e) {       
		e.preventDefault();
	});

	var p = window.parent;
	$("input.AyudaSyC").subIniciaDlg();
	setDatePickers();
	
	$(function() {
		$('#dialog').dialog({
			autoOpen: false,
		    width: 900,
		    heigth: 2900
		});
	});
	
	$(function() {		
     	$('#dialog_remanentes').dialog({
     		autoOpen: false,
     		width: 800,
     		height:500
   		});
   	});
					    
	$(function(){
		$('#dialogAut').dialog({
			autoOpen: false,
			width: 900,
			heigth: 2900
		});
	});
				
	$(function(){
   		$('#dialogMensaje').dialog({
   			autoOpen: false,
   			width: 900,
   			heigth: 2900
   		});
   	});
	
	$('#dialogMotor').dialog({
      	autoOpen: false,
		width: 900,
		heigth: 2900
   	});
   	
	$("#avanza").click(function (){
		$('input:radio[name=esquemaDocumento]:nth(0)').attr('checked',true);
	});

	$("#regresa").click(function(){
		$('input:radio[name=esquemaDocumento]:nth(1)').attr('checked',true);
	});

	$(function(){
		$('#filtro').dialog({
			autoOpen: <%if (reload && !porEXCEL) {%>true<%} else {%>false<%}%>,
			width: 900,
			heigth: 2900
		});
	});
				
	querySelectPost("TipoPresupuestoRead","origPresupuesto");

	$('#CatMovimientoRectificacion').change(function(){
		querySelectPost("TipoPresupuestoRead","origPresupuesto");
	});

	$("#cDescripcionPoliza").val($("#cDescripcionPolizaH").val());
	if(<%=rec!=null%>){
		$("#totalDice").val($("#totalDICEH").val());
		$("#totalDiceValor").val($("#totalDICEV").val());
	}

	if(<%=caso.getIdGabinete()!=-1 || existeDetalle || esConsulta%>){
		cargaDataTable();		
		obtieneTotales();
	}else{
		oTable = $("#tblPagadoFiltrado").dataTable({
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bPaginate" : false,
			"bAutoWidth" : true,
			"bScrollCollapse" : true,
			"sScrollXInner": "100%", 
			"sScrollX": "100%",
			"sPaginationType" : "full_numbers",
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"bServerSide": true,	
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
				oPaginate: { sFirst: "Primero", sPrevious: "Ant.", sNext: "Sigte.", sLast: "&Uacute;ltimo" }
			},
			"bServerSide": false,
			bProcessing: true,
			bSort : false,
			sPaginationType: "full_numbers",
			bJQueryUI: true,
			bSortClasses: false,
			"aoColumnDefs": [
				{ "bVisible": false, "aTargets": [8] },
				{ "sClass": "centro", "aTargets": [0,1,2,3,4,7] },
				{ "sClass": "money", "aTargets": [5,6] }
			]						
		});
	}
				
	$("#tblPagadoFiltrado tbody").click(function(event){
		$(oTable.fnSettings().aoData).each(function(){
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');
	});
				
	$("#tblPagadoFiltrado tbody").dblclick(function(e){
		$(oTable.fnSettings().aoData).each(function(){
			$(this.nTr).removeClass('row_selected');
		});
		$(e.target.parentNode).addClass('row_selected');
		tblPagoDblClick(e);
	});
				
	if(<%=id_oper%>==2){
		$('#autorizaRein').click(function(){
			if(<%=!("true".equals(caso.getCasoDato("APLICADO_CONT").getValor())) && caso.getCasoDato("APLICADO_CONT").getValor()!=null%>){
				mostrarDialog();
				$("#FECHA_APLICACION_CONTABLE").val($("#fApl").val());
				p.gestion.setFechaApCont($("#FECHA_APLICACION_CONTABLE").val());
			}else				
				Swal.fire({ icon: 'warning',
							text: "El documento ya se encuentra aplicado contablemente" });
		});
	}
	
	$('input').each(function() {
	    var readonly = $(this).attr("readonly");
	    if(readonly && readonly.toLowerCase()!=='false'){
	        $(this).addClass("notEditable");
	    }
	});
	
	creaDialogoEditar();
				
	$("#dialog-firmantesUpdate").dialog({
		autoOpen: false,
		height: 420,
		width: 500,
		modal: true,
		buttons: {
			"Aceptar": function() {
				if($.trim($("#cNombreVoBoUpdate").val()) == ""){ 
					Swal.fire({ icon: 'warning',
								text: "Falta Ingresar Nombre en Datos Vº Bº" });
					return;
				} else if($.trim($("#cPaternoVoBoUpdate").val()) == ""){ 
					Swal.fire({ icon: 'warning',
								text: "Falta Ingresar Apellido Paterno en Datos Vº Bº" });
					return;
				} else if($.trim($("#cMaternoVoBoUpdate").val()) == ""){ 
					Swal.fire({ icon: 'warning',
								text: "Falta Ingresar Apellido Materno en Datos Vº Bº" });
					return;					
				} else if($.trim($("#cPuestoVoBoUpdate").val()) == ""){ 
					Swal.fire({ icon: 'warning',
								text: "Falta Ingresar Puesto en Datos Vº Bº" });
					return;					 
				}					
				
				if($.trim($("#cNombreAutUpdate").val()) == ""){ 				
					Swal.fire({ icon: 'warning',
								text: "Falta Ingresar Nombre en Datos Autorizar" });
					return;
				} else if($.trim($("#cPaternoAutUpdate").val()) == ""){ 
					Swal.fire({ icon: 'warning',
								text: "Falta Ingresar Apellido Paterno en Datos Autorizar" });
					return; 
				} else if($.trim($("#cMaternoAutUpdate").val()) == ""){
					Swal.fire({ icon: 'warning',
								text: "Falta Ingresar Apellido Materno en Datos Autorizar" });
					return;					
				} else if($.trim($("#cPuestoAutUpdate").val()) == ""){ 
					Swal.fire({ icon: 'warning',
								text: "Falta Ingresar Puesto en Datos Autorizar" });
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
						queryName:"tRectificacionIngresosFirmante_Update", 
						async : false, 
						callback:function(){							
							cmdImprimir("RECTIFICAINGRESO");
							
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
								text: "No se pudo actualizar los firmantes, intente mas tarde." });
				}
				$(this).dialog("close");
			},
			"Cancelar": function() {
				$(this).dialog("close");
			}
		},
		close: function(){}							
	});
	
	var tablaCierre = $('#tablaCLC').dataTable({      
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
				sSearch: "Buscar:",
				oPaginate: {
					sFirst:    "Primero",			
					sPrevious: "Ant.",		
					sNext:     "Sigte.",			
					sLast:     "&Uacute;ltimo"
				}
			},
			bServerSide: false,
			bProcessing: true,
			bJQueryUI: true,
			bAutoWidth : false,
			bRetrive: true,
			bDestroy: true,
			bPaginate: false,
			iDisplayLength: 10,
			sScrollY: "250px", 
			sScrollX: "800px",
			Height: "250px",
			Width: "800px",
			aoColumns: [
				{ sName: "checknDocRenglon",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
				{ sName: "clc",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px" },
				{ sName: "caNoContrarrecibo",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px" },
				{ sName: "EP",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"150px"},
				{ sName: "cMes",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"25px"},
				{ sName: "remanente",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px"},
				{ sName: "cCentroContable",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px"},
				{ sName: "Solicitud",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" }
		    ]
		});
		
});//FIN DEL READY
			
			
function setDatePickers(){  	
	$( "#fApl" ).datepicker({
		dateFormat: "dd/mm/yy",
		autoclose: true,
		changeYear: true,
		changeMonth: true
	});
}
function inicio(recargar){
	
	if(<%=id_oper!=1%>)
		cargaDataTable();
		
	$('#padreRectificacion').val("");
	$("#eventoDICE").val("");
	if(recargar){
		var msg = $('#mensaje').val();
		if( msg != "" && msg != null ){			
			Swal.fire({ icon: 'warning',
						text: msg });
			parent.document.getElementById("pb_send").disabled=true;
		}else
			$('#mensaje').val("");

		$('#CatMovimientoRectificacion').val("");
		$('#concepto').val("");
		//$('#oficioRectif').val("");
		$('#ctr_int').val("");
		$('#folioSICOP').focus();
		if(<%=!porEXCEL%>)
			$('#filtro').dialog('option', 'modal', true).dialog('open');
		$("#tblPagadoFiltrado tbody tr td:eq(0)").click();
	}else{
		var msg = $('#mensaje').val();
		if( msg != "" && msg != null ){
			parent.document.getElementById("pb_send").disabled=true;			
			Swal.fire({ icon: 'warning',
						text: msg });
			$('#tblPagadoFiltrado thead tr td:eq(0)').click();
		}else{
			$('#mensaje').val("");
		}
	}
}

function permite(elEvento, permitidos) {
	var numeros = "0123456789";
	var caracteres = " abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ.";
	var numeros_caracteres = numeros + caracteres;
	var decimal = numeros + ".";
	var teclas_especiales = [8];
	switch(permitidos) {
		case 'num':
			permitidos = numeros;
			break;
		case 'car':
			permitidos = caracteres;
			break;
		case 'num_car':
			permitidos = numeros_caracteres;
			break;
		case 'dec':
			permitidos = decimal;
			break;
	}
	var evento = elEvento || window.event;
	var codigoCaracter = evento.charCode || evento.keyCode;
	var caracter = String.fromCharCode(codigoCaracter);
	var tecla_especial = false;
	for(var i in teclas_especiales) {
		if(codigoCaracter == teclas_especiales[i]) {
			tecla_especial = true;
			break;
		}
	}
	return permitidos.indexOf(caracter) != -1 || tecla_especial;
}

function buscarCLC(){
	$('#tblPagadoFiltrado').dataTable().fnClearTable();
	$('#tablaCLC').dataTable().fnClearTable();
	
	$('#dialog_remanentes').dialog('option', 'modal', true).dialog('open');
	var caNoContrarrecibo = $("#CXP").val(); 
	var folioCLC = $("#folioSICOP").val();
	
	var ur='<%=usuario.getU_UR()%>';
	var cc = '<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>';
	var locationAjaxSource = "";
	if(ur=='A02')
		locationAjaxSource = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vRegistroIngresoFiscalRemanente&qw=caNoContrarrecibo = '"+caNoContrarrecibo+"' AND folioDependenciaCLC = '"+ folioCLC+"'";
	else
		locationAjaxSource = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vRegistroIngresoFiscalRemanente&qw=caNoContrarrecibo = '"+caNoContrarrecibo+"' AND folioDependenciaCLC = '"+ folioCLC+"' AND cCentroContable = '"+cc+"'";
		
	$('#tablaCLC').dataTable({         
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
			sSearch: "Buscar:",		
			oPaginate: {		
				sFirst:    "Primero",		
				sPrevious: "Ant.",		
				sNext:     "Sigte.",		
				sLast:     "&Uacute;ltimo"		
			}	
		},	
		bServerSide: true,	
		sAjaxSource: locationAjaxSource,
		bProcessing: true,
		"fnDrawCallback": function(oSettings) {
			if (oSettings.aiDisplay.length == 0){
				$("#dialog_remanentes").dialog('close');
				Swal.fire({ icon: 'warning',
							text: "No hay información con los criterios solicitados." });
			}
		},
		bJQueryUI: true,
		bAutoWidth : false,		
		bRetrive: true,		
		bDestroy: true,        
		bPaginate: false,		
		iDisplayLength: 30,		
		sScrollY: "300px", 	
		sScrollX: "1200px",		
		Height: "400px",
		aoColumns: [
			{ sName: "checknDocRenglon",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "clc",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px" },
			{ sName: "caNoContrarrecibo",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"60px" },
			{ sName: "EP",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"150px"},
			{ sName: "cMes",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"25px"},
			{ sName: "remanente",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRi", sWidth:"40px"},
			{ sName: "cCentroContable",bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px"},
			{ sName: "Solicitud",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"40px" }
		]
	});
}

function fjsRoundNumber(dato,decimales){
	if (dato.toFixed) 
		return dato.toFixed(decimales);
	var aux = 0;
	dato = dato + "";
	if(dato.indexOf(".") > -1){
		if(decimales != null &&  decimales > 0)
			aux = 10;
		var i;
		for( i = 0; i<decimales; i++)
			aux = aux * 10;
		return Math.round(dato*aux)/aux;
	}
	return dato;
}

function editarMonto(monto){
	var montoEditable = $(monto).val();
	montoEditable = montoEditable * 1;							
	
	var total = $("#totalDice").val() * 1;				
	total = total - montoEditable;
	total = fjsRoundNumber(total,2);
	$(monto).val("");
	$("#totalDebeDecir").val(total);
}
			
function editarMontoDD(monto){
	var tListado = $("#tblPagadoFiltrado").dataTable().fnGetData();
	for (i=0; i<tListado.length;i++){
		if(tListado[i][0] == monto){
			monto = tListado[i][5];
			i = tListado.length;
		}
	}				
	var total = $("#totalDice").val() * 1;
	
	total = total - monto;
	total = fjsRoundNumber(total,2);
	$(monto).val("");
	
	$("#totalDice").val(total);
	$("#totalDebeDecir").val("0.00");		
	$("#totalDiceValor").val(total);		
	sumaImporteDiceDebeDecirReal();
}

function actualizaTotalDD(valorIngresado){
	var rw = $('#tblPagadoFiltrado').dataTable().fnGetData();
	var suma = 0.0;
	for ( var i = 0; i < rw.length; i++){
		suma += parseFloat( rw[i][5]);
	}
	if(<%=rec==null%>){
		$("#totalDice").val(suma);
		$("#totalDiceValor").val(suma);
	}
	
	var montoDD = $("#totalDebeDecir").val();
	var valorIng = $(valorIngresado).val();
	montoDD = (montoDD * 1) + valorIng;
	montoDD = fjsRoundNumber(montoDD,2);
	$("#totalDebeDecir").val(montoDD);
}

function ResponsableSiguiente(id_oper){
	if(id_oper==1)
		return "AUTORIZA_RECTIFICAINGRESO";

	if(id_oper==2)
		return "CONSULTA_RECTIFICAINGRESO";
}

function OperacionSiguiente(id_oper){
	if(id_oper==1)
		return "autoriza_rectificaingreso";

	if(id_oper==2)
		return "consulta_rectificaingreso";
}

function onPostDisplay(idoper){}
				
function onSubmit(id_oper){//validaciones del boton guardar
	var p = window.parent;
	var valida_campos = true;
	try{
		//validaciones de la forma
		var msgAlert="";
		if(msgAlert!=""){
			Swal.fire({ icon: 'warning',
						text: msgAlert });
			return false;
		}
		if (id_oper==1){
			var dblTotalDice = $("#totalDice").val() * 1;
			var dblTotalDebeDecir = $("#totalDebeDecir").val() * 1;

			p.gestion.setFolio($("#folio").val());
			p.gestion.setFechaDocumento($("#fApl").val());
			p.gestion.setEjercicioFiscal($("#cEjercicio").val());
			p.gestion.setOperador($("#operador").val());
			p.gestion.setConceptoMov("Rectificacion Presupuestal con FOLIO " + $("#folio").val() + ".");
			p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
			
			dblTotalDice=Math.round(dblTotalDice*100)/100 ;
			dblTotalDebeDecir=Math.round(dblTotalDebeDecir*100)/100 ;

			if( dblTotalDice == dblTotalDebeDecir ){
				var aTrs = oTable.fnGetNodes();
				if(<%=!porEXCEL%>){
					if($("#concepto").val()=="" || $("#ctr_int").val()=="" || $("#CatMovimientoRectificacion").val()==""){						
						Swal.fire({ icon: 'warning',
									text: "Favor de llenar el concepto, control interno y tipo de movimiento" });
						parent.document.getElementById("pb_save").disabled=false;
					}else if($("#totalDice").val()!=$("#totalDebeDecir").val()){
						Swal.fire({ icon: 'warning',
									text: "Los importes del Dice y Debe Decir, no coinciden." });
						return false;
					}else if(Number($("#totalDice").val())+Number($("#totalDebeDecir").val())==0){						
						Swal.fire({ icon: 'warning',
									text: "Los importes no pueden ser 0." });
						return false;
					}else{
						$.trim($("#concepto").val());
						$("#info").val(obtenValoresGrid());
						$.ajax({
							url: '../gstnmngr/CapturaRectificacionIngresoFiscalServlet',
							type: 'post',
							async:false,
							dataType: 'json',
							data : $("#salvarRectificacion").serialize(),
							error : function(data) {								
								Swal.fire({ icon: 'error',
											text: "Ocurrio un error en el Insert. ["+data.data_1.result+"]" });
							},
							success: function(data){
								var exito = data.success;
								if( "true" == exito)			
									if(<%=captura%>)
										dialogFirmantes();
							}
						});
					}
				}else
					parent.document.getElementById("pb_save").disabled=true;
								
			} else{				
				Swal.fire({ icon: 'warning',
							text: "favor de revisar la(s) lineas rectificadas(marcadas con la leyenda \"DEBE DECIR\"). Los montos del dice no coinciden con los montos del debe decir" });
			}
		}
					
		if (id_oper==2 ){
  			if(document.rechazo.autorizaRein[1].checked && $("#motivoRechazo").val()==""){	  			
	  			Swal.fire({ icon: 'warning',
							text: "Motivo de rechazo es requerido" });
	  			return false;
  			}else if(document.rechazo.autorizaRein[1].checked){
 				parent.document.getElementById("pb_send").disabled=false;
 				parent.document.getElementById("pb_save").disabled=true;
 				$("#motivoR").val($("#motivoRechazo").val());
 				queryFormPost("tRectificacionIngresosEncabezadoUpdate", {async:false});
 			}else if(!(document.rechazo.autorizaRein[0].checked || document.rechazo.autorizaRein[1].checked)){  				
  				Swal.fire({ icon: 'warning',
							text: "Favor de marcar si los datos son correctos o no" });
  				return false;
  			}else{
  				if(<%=("true".equals(caso.getCasoDato("APLICADO_CONT").getValor())) && caso.getCasoDato("APLICADO_CONT").getValor()!=null%>){
  					parent.document.getElementById("pb_save").disabled=true;
  					parent.document.getElementById("pb_send").disabled=false;  				
  					parent.document.getElementById("pb_cancel").disabled=true;
  				}
  			}
 		}
	} catch (e) {
		window.alert("onSubmit: Error: " + e.message);
		return false;
	}
	return valida_campos;
}
			
function onLoadPlantilla(id_oper){
	
	$("#totalDice").val("0.00");
	$("#totalDebeDecir").val("0.00");

	if(id_oper==3){
		$("#divImprimePoliza").show();
		$("#EditaFirmas").css('visibility', 'visible');
	}else{
		$("#divImprimePoliza").hide();
		$("#EditaFirmas").css('visibility', 'hidden');
	}
				
	$("#folioSICOP").val('');
	
	$("#nFolioSICOP").val('<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)?rectificacionCapturada.getEncabezado().getnFolioSicop():""%>');
	$("#caNoContrarrecibo").val('<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)?rectificacionCapturada.getEncabezado().getCaNoContrarrecibo():""%>');
	$("#nFolioSIAFF").val('<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)?rectificacionCapturada.getEncabezado().getnFolioSIAFF():""%>');
	$("#cDescripcionPoliza").val('<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)?rectificacionCapturada.getEncabezado().getcDescripcionPoliza():""%>');
	$("#cEjercicio_C").val('<%=adecProy.obtenEjercicioFiscal()%>');
		
	queryFormPost("fExpRead", {async: false });
	queryFormPost("fAplRead", {async: false });
		
	
	if(<%=rec != null%>){
		$("#CatMovimientoRectificacion").val('<%=rec!=null?rec.getcTipoMovto():""%>');
		$("#concepto").val('<%=rec!=null && rec.getcConceptoRectificacion()!=null?rec.getcConceptoRectificacion():""%>');
		$("#oficioRectif").val('<%=rec!=null &&rec.getOficioRectif()!=null?rec.getOficioRectif():""%>');
		$("#ctr_int").val('<%=rec!=null &&rec.getCtr_int()!=null?rec.getCtr_int():""%>');
		$("#totalDice").val('<%=rec!=null &&rec.getTotalDice()!=null?rec.getTotalDice():""%>');
		$("#totalDebeDecir").val('<%=rec!=null &&rec.getTotalDice()!=null?rec.getTotalDice():""%>');
		$("#nFolioSICOP").val('<%=rec!=null &&rec.getnFolioSicop()!=null?rec.getnFolioSicop():""%>');
		$("#nFolioSIAFF").val('<%=rec!=null &&rec.getnFolioSIAFF()!=null?rec.getnFolioSIAFF():""%>');
		$("#caNoContrarrecibo").val('<%=rec!=null &&rec.getCaNoContrarrecibo()!=null?rec.getCaNoContrarrecibo():""%>');
		$("#cDescripcionPoliza").val('<%=rec!=null &&rec.getcDescripcionPoliza()!=null?rec.getcDescripcionPoliza().replaceAll("\r\n", "\\\\n"):""%>');
		$("#filtroRec").hide();
		
		$("#totalDice").val(Number($("#totalDice").val()).toFixed(2));
		$("#totalDebeDecir").val(Number($("#totalDebeDecir").val()).toFixed(2));
				
	}else{
		$("#CatMovimientoRectificacion").val("");
		$("#concepto").val("");
		$("#ctr_int").val("");
		$("#totalDebeDecir").val("0");
	}
		
	var p = window.parent;
								
	if(id_oper==3 && parent.document.getElementById("pb_cancel")){
	   	parent.document.getElementById("pb_cancel").style.visibility='hidden';
		parent.document.getElementById("pb_cancel").disabled=true;
	}
				
	var rw = $('#tblPagadoFiltrado').dataTable().fnGetData();
	var suma = 0.0;
	for ( var i = 0; i < rw.length; i++){
		suma += parseFloat(rw[i][5]);
	}
	suma = (suma * 100)/100;
	
	if(<%=rec==null%>){
		$("#totalDice").val(suma);
		$("#totalDiceValor").val(suma);
	}
	
	if(<%=id_oper!=1%>)
		queryFormPost("fechasRectificaIngresoFiscalRead", {async: false });
}
			
function onPostSubmit(id_oper){
	if (id_oper==1 && <%=archivoPDFCXP.length==0%>){ 
		Swal.fire({ icon: 'warning',
					text: "Para poder continuar debe de adjuntar la Solicitud Firmada" });
		return false;		
	}
	return true;
}
			
function obtenValoresGrid(){
	var info="";
	$('#tblPagadoFiltrado tbody tr').each(function(idx, elm){
		info += $(this).find('td:eq(0)').html();
		
		if($(this).find('td:eq(3)').children().val()==undefined)
			info += "!" + $(this).find('td:eq(1)').html();	
		else
			info += "!" + $(this).find('td:eq(1)').children().val();
		
		if($(this).find('td:eq(3)').children().val()==undefined)
			info += "!" + $(this).find('td:eq(3)').html();	
		else
			info += "!" + $(this).find('td:eq(3)').children().val();
		
		info += "!" + $(this).find('td:eq(4)').html();
		
			if($(this).find('td:eq(5)').children().val()==undefined){
				var sincomas = $(this).find('td:eq(5)').html();				
				info += "!" + sincomas.replace(",","");
			}
			else{
				var sincomas = $(this).find('td:eq(5)').children().val();
				info += "!" + sincomas.replace(",","");
			}
		
		if($(this).find('td:eq(3)').children().val()==undefined)
			info += "!" + $(this).find('td:eq(2)').html() + "!";	
		else
			info += "!" + $(this).find('td:eq(2)').children().val() + "!";
		
	});
	return info;
}
			
function guardaExp(){
	parent.document.getElementById("pb_save").disabled=false;
	parent.document.getElementById("pb_save").click();
	parent.document.getElementById("pb_save").disabled=true;
}

function mostrarDialog(){
	$('#dialog').dialog('option', 'modal', true).dialog('open');	 
 }
 
 function mostrarDialogAut(){
	$('#dialogAut').dialog('option', 'modal', true).dialog('open');	 
 }
			    
function aplicaCont(){
	$("#dialog").dialog("close");				
	if(!confirm("Enviara el Documento a Aplicar Contablemente.  \n \n  ¿desea continuar?")) {
		return false;
	}
	parent.document.getElementById("pb_save").disabled=true;
   	var strAction="../gstnmngr/RectificacionIngresoFiscalServlet";
  	$.blockUI({message: "Procesando espere..."});
  	$.ajax({
  		datatype:"html",
		type: "POST",
		url: strAction,
		data:{accion:1, CXP:$("#caNoContrarrecibo").val(), folioSICOP:$("#nFolioSICOP").val()},
		success: function (data,textStatus){
			$.unblockUI();
			var mensaje = data;
			if(mensaje.lastIndexOf("DOCUMENTO DE RECTIFICAINGRESO APLICADO CONTABLEMENTE") != -1){
  				document.rechazo.autorizaRein[0].disabled=true;
 				document.rechazo.autorizaRein[1].disabled=true; 				
 				Swal.fire({ icon: 'success',
							text: "Aplicado correctamente" });
  				
		   		parent.document.getElementById("pb_send").disabled=false;
		   		parent.execOperacion();
				parent.execResponsable();
	   			
	   			parent.document.getElementById("pb_send").click();
	   			
			}else{
				Swal.fire({ icon: 'error',
							text: "No se aplico correctamente.\n\n" + data });   				
   			}

		},
		error: function (par) {
			Swal.fire({ icon: 'error',
						text: '<%=mensaje%>' });
		}
	});
}

function fnExportaSicop() {
	$("#info").val(obtenValoresGrid());
	$.blockUI( {
		message : "Procesando espere ......"
	});
	document.ExportaSicop.submit();
	$.unblockUI();
	return true;
}
			
function cmdImprimir(elFormato){
	window.open(
		"../admin/SeguridadCatalogos?"
		+ "catalogo=CONTRARECIBO"
		+ "&accion=run"
		+ "&rn=PolizaRectificacion.jasper"
		+ "&whereFolio= '" + <%=folio %> +"'"
		+ "&whereTipo= '" + elFormato+"'", 			
		"popacuse",
		"scrollbars=1, resizable=yes, width=1024, height=768");
}

function dialogFirmantes(){
	queryFormPost({
		queryName:"existeFirmanteRectificacionIngresosRead", 
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

function agregarClave(){
	var oTable = $('#tablaCLC').dataTable();
	var aData = $("#tablaCLC").dataTable().fnGetData();
	var aDataA = $("#tblPagadoFiltrado").dataTable().fnGetData();
	var arrlist = new Array();

	var i=0;	
	$('#tablaCLC input:checked').each(function(idx, elm){
		var paso = aData[oTable.fnGetPosition($(this).closest('tr')[0])];
		var agregaD = true;
		var j=0;
		while(j<aDataA.length){		
			if(aDataA[j][2]==paso[3]){
				agregaD = false;
			}
			j++;
		}
		if(agregaD){
			var cmdBorrar = "<img src=\"../imagenes/cancelar.gif\" width=\"25\" height=\"21\" alt=\"Descartar Renglon\" onClick=\"descartar('"
				+ paso[3] 
				+ "','" 
				+ paso[4] 
				+ "'," 
				+ "'DICE'" 
				+ "," 
				+ (Number(aDataA.length+1) + i) + ");\">";
				
			arrlist[i] = new Array(
							aDataA.length+i+1, 	//renglon
							paso[4], 			//mes
							paso[7],			//Solictud (caNoContrarrecibo)
							paso[3], 			//ep
							"DICE", 			//evento
							paso[5], 			//importe
							paso[5], 			//remanente
							cmdBorrar, 			//descartar
							"" 					//eppadre
						);
			i++;
		}
	});
	if (i>0)
		queryFormPost("infoIntegracionIngresoFiscalRead", {async: false });
	 
	$("#tblPagadoFiltrado").dataTable().fnAddData( arrlist );
	$('#tablaCLC').dataTable().fnClearTable();
	$("#dialog_remanentes").dialog('close');
	obtieneTotales();
}

function obtieneTotales(){
	var dice = 0.00;
	var debedecir = 0.00;
	var tablaRectificacion = $("#tblPagadoFiltrado").dataTable().fnGetData();
	
	var j=0;
	while(j<tablaRectificacion.length){		
		if(tablaRectificacion[j][4]=="DICE")
			dice += Number(tablaRectificacion[j][5]);
		else
			debedecir += Number(tablaRectificacion[j][5]);
		j++;
	}
	
	$("#totalDice").val(dice.toFixed(2));
	$("#totalDebeDecir").val(debedecir.toFixed(2));		
}

function cargaDataTable(){
	oTable = $("#tblPagadoFiltrado").dataTable({
		"bPaginate": false,
    		"bLengthChange": true,
    		"bFilter": true,
    		"bSort": true,
    		"bInfo": true,
    		"bAutoWidth": false,
		"sScrollY": 100,
		"sScrollYInner": "100%",
		"bJQueryUI": true,
		"bRetrive" : true,
		"bDestroy" : true,
		"sPaginationType": "full_numbers",
		"sScrollX": "800",
		"bScrollCollapse": true,	
		"bServerSide": true,   
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vw_RectificacionIngresosDetalle&qw=nFolioRectificaIngreso="+<%=caso.getFolio().substring(caso.getFolio().lastIndexOf('-') + 1)%>,
			aoColumns: [
				{ sName: "nDocRenglon", "bVisible": true},
				{ sName: "cMes", "bVisible": true},
				{ sName: "caNoContrarrecibo", "bVisible": true},
				{ sName: "EP", "bVisible": true},
				{ sName: "cEvento", "bVisible": true},
				{ sName: "mImporte", "bVisible": true},
				{ sName: "remanente", "bVisible": false},
				{ sName: "descartar", "bVisible": false},
				{ sName: "epPadre", "bVisible": false}
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
}

</script>
</head>
<br/>
<body id="dt_example" class="ex_highlight" onLoad="inicio(<%=reload%>);">
	<div id="container" class="container" style="with: 80%">
		<div class="card-header"> <h3>Rectificación de Ingresos Fiscales</h3> </div>					
		<hr class="mt-3">
		
		<div id="dialogMensaje" title="Mensajes">
			<%if (mensaje != null && !"".equals(mensaje)) {%>
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1" style="border-style: double;">
						<%=mensaje%>
					</div>
				</div>		
			<%}%>
		</div>
		
		<div id="filtroRec" title="Filtro por criterios de la CLC">
			<form id="filtroRectificacion" name="filtroRectificacion" method="POST" action="../gstnmngr/RectificacionIngresoFiscalServlet">
				<input type="hidden" id="cEjercicio" name="cEjercicio" value="<%=aEjercicioFiscal%>"> 
				<input type="hidden" id="cRamo" name="cRamo" value="16"> 
				<input type="hidden" id="cUnidad" name="cUnidad" value="RHQ"> 
				<input type="hidden" id="operador" name="operador" value="<%=caso.getCasoOperacion(0) == null ? "caso operacion nulo" : caso.getCasoOperacion(0).getResponsable()%>" />
				<input type="hidden" id="fDocumento" name="fDocumento" value="<%=today%>" />
				<%if (id_oper == 1) {%>
					<div class="row d-flex">
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<label for="Folio de Dependencia CLC" class="form-label"> Folio de Dependencia CLC </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<input type="text" class="form-control form-control-sm" name="folioSICOP" id="folioSICOP" onkeypress="return permite(event, 'num');" size="15">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<label for="CXP" class="form-label"> Folio de Integracion </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<input type="text" class="form-control form-control-sm" name="CXP" id="CXP" size="60">
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							<input type="button" id="validar" name="buscar" value="Buscar" onClick="buscarCLC();" class="btn btn-secondary btn-sm">
						</div>						
					</div>	
				<%}%>
			</form>
		</div>
		<%if (id_oper == 2 ) {%>
			<form id="rechazo" name="rechazo">
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
			</form>
		<%}%>
		<%if (id_oper == 2 || id_oper == 3) {%>
			<form id="ExportaSicop" name="ExportaSicop" action="../gstnmngr/LayoutRectificaIngresoFiscalServlet" method="POST">
				<input type="hidden" name="info" id="info" />
				<input type="hidden" name="cxpIntegrada" id="cxpIntegrada" />
				<div class="row d-flex">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<input type="button" id="Exportar" name="Exportar" value="Exportar SICOP" class="btn btn-secondary" onclick="fnExportaSicop();"/>
					</div>
				</div>		
			</form>
		<%}	%>
		<div id="container2">
			<form id="salvarRectificacion" name="salvarRectificacion" method="POST" action="../gstnmngr/CapturaRectificacionIngresoFiscalServlet">
				<input type="hidden" id="mensaje" name="mensaje" value="<%=msg%>" />
				<input type="hidden" id="cEjercicio_C" name="cEjercicio_C" value="<%=adecProy.obtenEjercicioFiscal()%>" /> 
				<input type="hidden" id="cRamo_C" name="cRamo_C" value="<%=usuario.getU_Ramo() == null ? "" : usuario.getU_Ramo()%>" />
				<input type="hidden" id="cUnidad_C" name="cUnidad_C" value="<%=usuario.getU_UR() == null ? "" : usuario.getU_UR()%>" />
				<input type="hidden" id="operador_C" name="operador_C"  value="<%=caso.getCasoOperacion(0) == null ? "caso operacion nulo" : caso.getCasoOperacion(0).getResponsable()%>" />
				<input type="hidden" id="u_login_C" name="u_login_C" value="<%=usuario.getLogin() == null ? "usuario nulo" : usuario .getLogin()%>" />
				<input type="hidden" id="cCentroContable_C" name="cCentroContable_C" value="<%=cCentroContable == null ? "centro contable nulo" : cCentroContable%>" />
				<input type="hidden" id="ID_Caso_C" name="ID_Caso_C" value="<%=caso.getIdCaso()%>" /> 
				<input type="hidden" id="cMes_C" name="cMes_C" value="10" /> 
				<input type="hidden" id="cDescripcionPolizaH" name="cDescripcionPolizaH" value="<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null) ? rectificacionCapturada.getEncabezado().getcDescripcionPoliza() : ""%>" />
				<input type="hidden" id="padreRectificacion" name="padreRectificacion" /> 
				<input type="hidden" id="epGrid" name="epGrid" /> 
				<input type="hidden" id="epConResto" name="epConResto" /> 
				<input type="hidden" id="eventoDICE" name="eventoDICE" /> 
				<input type="hidden" id="eventoDEBE_DECIR" name="eventoDEBE_DECIR" /> 
				<input type="hidden" name="info" id="info" /> 
				<input type="hidden" id="mesTemporal" name="mesTemporal" />
				<!-- Este sirve cuando se calcula la EP para ponerle su mes original y no el calculado -->

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
				<input type="hidden" id="folioRectif" name="folioRectif" size=40 value="<%=folio%>">
				<input type="hidden" id="existeFirmante" name="existeFirmante" size=40 value="NOEXISTE">
				
				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">											
						<label for="folio" class="form-label">*Folio</label>
						<input type="text" name="folio" id="folio" class="form-control form-control-sm" readOnly value="<%=caso.getFolio()%>" />							
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
						<label for="oficioRectif" class="form-label">Oficio rectif.</label>	
						<input type="text" id="oficioRectif" name="oficioRectif" class="form-control form-control-sm" readOnly value="<%=caso.getFolio()%>"/>							
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
						<label for="ctr_int" class="form-label">CTR_INT</label>	
						<input type="text" id="ctr_int" name="ctr_int" class="form-control form-control-sm"/>							
					</div>										
				</div>	

				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">							
						<label for="fExp" class="form-label">*F. Expedici&oacute;n</label>
						<input type="text" id="fExp" name="fExp" class="form-control form-control-sm" readOnly />							
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
						<label for="fApl" class="form-label">*Fecha Aplicaci&oacute;n</label>							
						<input type="text" id="fApl" name="fApl" class="form-control form-control-sm"/> 
					</div>					
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">							
						<label for="CatMovimientoRectificacion" class="form-label">Movimiento</label>
						<div class="input-group">
							<input type="text" class="form-control AyudaSyC form-control-sm" name="CatMovimientoRectificacion" id="CatMovimientoRectificacion" readOnly onKeyDown="return false;" value="<%=(rec != null) ? rec.getcTipoMovto() : ""%>"/>
						</div>							
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					</div>
					<div class="col-12 col-lg-7 col-md-7 col-sm-12">									
						<label for="origPresupuesto" class="form-label">*Origen ppto.</label>							
						<select id="origPresupuesto" name="origPresupuesto" class="form-select form-select-sm">
							<option value="Z:">A</option>
							<option value="Y:">B</option>
							<option value="X:">C</option>
							<option value="xx" selected>--</option>
						</select> 
					</div>
				</div>	
				
				<div class="row">						
					<div class="col-12 col-lg-12 col-md-12 col-sm-12">								
						<label for="concepto" class="form-label">*Concepto</label>							
						<textarea id="concepto" name="concepto" class="form-control form-control-sm" rows="3" cols="50" onkeypress="return event.keyCode!=13"></textarea>
					</div>
				</div>	
				
				<div class="row">						
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">													
						<label for="totalDice" class="form-label">Total DICE</label>	
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>						
							<input type="text" id="totalDice" name="totalDice" class="form-control form-control-sm" onKeyDown="return false;" readOnly/> 
							<input id="totalDiceValor" name="totalDiceValor" type="hidden"/>
						</div>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12">					
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">													
						<label for="totalDebeDecir" class="form-label">Total DEBE DECIR</label>
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>									
							<input type="text" id="totalDebeDecir" name="totalDebeDecir" class="form-control form-control-sm" onKeyDown="return false;" readOnly/>
						</div> 							
					</div>
				</div>	
				
				<br/>
					
				<div id="divImprimePoliza" class="conainer">
					<div class="row">							
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-printer"></i></span>		
								<input type="button" value="Poliza" onClick="cmdImprimir('RECTIFICAINGRESO');" class="btn btn-secondary">
							</div>
						</div>						
						<div class="col-12 col-lg-3 col-md-3 col-sm-12">										
							<span id="EditaFirmas" style="visibility:hidden"><a href="#" class="text-decoration-none" onclick="dialogFirmantes();">Firmas*</a></span>								
						</div>							
					</div>
				</div>
				
				<div class="container" style="width: 100%">
					<h6> CLC a Rectificar </h6>
					<hr class="mt-3">
					
					<div class="row">												
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">						
							<label for="dTipoPago" class="form-label">Tipo CLC:</label>		
														
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12">					
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
							<label for="nFolioSICOP" class="form-label">Folio CLC:</label>		
							<input type="text" id="nFolioSICOP" name="nFolioSICOP" class="form-control form-control-sm" value="<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)?rectificacionCapturada.getEncabezado().getnFolioSicop() : ""%>" readOnly/>												
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
							<label for="caNoContrarrecibo" class="form-label">No. CxP</label>	
							<input type="text" id="caNoContrarrecibo" name="caNoContrarrecibo" class="form-control form-control-sm" value="<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)?rectificacionCapturada.getEncabezado().getCaNoContrarrecibo() : ""%>" readOnly/>							 					
						</div>
					</div>
					
					<div class="row">						
						<div class="col-12 col-lg-10 col-md-10 col-sm-12">									
							<label for="cDescripcionPoliza" class="form-label">Descripcion</label>							
							<textarea id="cDescripcionPoliza" name="cDescripcionPoliza" class="form-control form-control-sm" rows="3" cols="50" readOnly onKeyDown="return false;"></textarea>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
							<label for="nFolioSIAFF" class="form-label">Folio SIAFF:</label>	
							<input type="text" id="nFolioSIAFF" name="nFolioSIAFF" class="form-control form-control-sm" value="<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)?rectificacionCapturada.getEncabezado().getnFolioSIAFF() : ""%>" readOnly/>							
						</div>						
					</div>	
					
					<div id="demo_jui">
						<h6>*Doble click en el renglon DICE para editar</h6>						
						<table id="tblPagadoFiltrado" class="table table-striped table-sm">
							<thead>
								<tr>
									<th>Renglon</th>
									<th>Mes</th>
									<th>Solicitud</th>
									<th>EP</th>
									<th>Evento</th>
									<th>Importe Neto</th>
									<th>Remanente</th>
									<th>Descartar Secuencia</th>
									<th>EP_PADRE</th>
								</tr>
							</thead>
						</table>
						
						<input  id="totalDICEH" name="totalDICEH" value="<%=strValor%>" type="hidden"/> 
						<input  id="totalDICEV" name="totalDICEV" value="<%=valor%>" type="hidden" />
					</div>
					
				</div>
			</form>
		</div>
		<!-- DIALOGOS (POPUPS) QUE SALEN PARA HACER LAS APLICACIONES CONTABLES AL ESTILO DE REINTEGROS FMC 2/NOV -->

		<%
		if (mensaje == null || "".equals(mensaje)) {
		%>
			<div id="dialog" title="Detalle de Rectificaciones">
				<p>Consulta de Rectificaciones de Ingreso</p>
				<input type="button" id="aplicarContable" value="Aplicar contablemente" onclick="aplicaCont();" />
			</div>

			<div id="dialogAut" title="Detalle de Rectificaciones">
				<p>Autorización de Rectificaciones de Ingreso</p>
				<input type="button" id="aplicarContableAut" value="Autorizar contablemente" onclick="aplicaContAut()" />
			</div>
		<%}%>
		<div id="totales">
			<label id="totalDiceInfo" type="hidden"></label>
			<br/>
			<label id="totalDebeInfo" type="hidden"></label>
			<br/>
		</div>
	</div>
	<div id="dialogMotor"
		title="Rectificacion de Ingreso">
		<p>Mensajes del sistema</p>
		<a rel=""></a>
		<textarea id="mensajeMotor" name="mensajeMotor" rows="8" cols="120"></textarea>
	</div>
	
	<div id="EdtaRenglon" class="container">
		<form id="editaEP">
			<h5> DICE </h5>
			<hr class="mt-3"/>	
			
			<div class="row">												
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">						
					<label for="solicitud" class="form-label">Solicitud:</label>									
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-2">
					<div class= "input-group">
						<span class="input-group-text"><i class="bi bi-tag"></i></span>						
						<input type="text" id="solicitud" name="solicitud" class="form-control form-control-sm" readOnly/>
					</div>							
				</div>
			</div>
			
			<div class="row">												
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">						
					<label for="EPDice" class="form-label">EP:</label>									
				</div>
				<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">	
					<div class= "input-group">
						<span class="input-group-text"><i class="bi bi-tag"></i></span>					
						<input type="text" id="EPDice" name="EPDice" class="form-control form-control-sm" readOnly/>
					</div>							
				</div>
			</div>
			
			<div class="row">												
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">						
					<label for="nMesDice" class="form-label">Mes:</label>									
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">		
					<div class= "input-group">
						<span class="input-group-text"><i class="bi bi-calendar"></i></span>				
						<input type="text" id="nMesDice" name="nMesDice" class="form-control form-control-sm" readOnly/>
					</div>							
				</div>
			</div>
			
			<div class="row">												
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">						
					<label for="mImporteDice" class="form-label">Importe:</label>									
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">		
					<div class= "input-group">
						<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>														
						<input type="text" id="mImporteDice" name="mImporteDice" class="form-control form-control-sm" value="" placeholder="0.00"/>
					</div>							
				</div>
			</div>	
			
			<h5> DEBE DECIR </h5>
			<hr class="mt-3"/>
			
			<div class="row">												
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">						
					<label for="EPDebeDecir" class="form-label">EP:</label>									
				</div>
				<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-2">
					<div class= "input-group">
						<span class="input-group-text"><i class="bi bi-tag"></i></span>						
						<input type="text" size="65" id="EPDebeDecir" name="EPDebeDecir" class="form-control form-control-sm" onchange="limpiarSelect()" readonly/>
						<input type="button" id="nIdClaveEgresos2" size="5" value="..."	onclick="Grid()" class="btn btn-secondary"/>
						
					</div>							
				</div>
			</div>
			
			<div class="row">												
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">						
					<label for="MesDebeDecir" class="form-label">Mes:</label>									
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-2">
					<div class= "input-group">
						<span class="input-group-text"><i class="bi bi-calendar"></i></span>						
						<select id="MesDebeDecir" name="MesDebeDecir" class="form-select form-select-sm">
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
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">						
					<label for="ImporteDebeDecir" class="form-label">Importe:</label>									
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">			
					<div class= "input-group">
						<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>										
						<input type="text" id="ImporteDebeDecir" name="ImporteDebeDecir" class="form-control form-control-sm" placeholder="0.00"/>
					</div>											
				</div>
			</div>				
		</form>
	</div>
	
	<div id="dialog-firmantesUpdate" title="Actualizacion de Firmantes" class="container">
		<h5> Datos  VºBº</h5>
		<hr class="mt-3">
		
		<div class="row">
			<div class="col-12 col-lg-10 col-md-10 col-sm-12">
				<label for="cNombreVoBoUpdate" class="form-label">Nombre:</label>
				<div class="input-group">								
					<span class="input-group-text"><i class="bi bi-person"></i></span>
					<input type="text" id="cNombreVoBoUpdate" name="cNombreVoBoUpdate" size=40 maxlength="70" class="form-control form-control-sm"/>
				</div>
			</div>
		</div>
		
		<div class="row">
			<div class="col-12 col-lg-10 col-md-10 col-sm-12">
				<label for="cPaternoVoBoUpdate" class="form-label">Apellido Paterno:</label>
				<div class="input-group">								
					<span class="input-group-text"><i class="bi bi-person"></i></span>
					<input type="text" id="cPaternoVoBoUpdate" name="cPaternoVoBoUpdate" size=40 maxlength="70" class="form-control form-control-sm"/>
				</div>
			</div>
		</div>
		
		<div class="row">
			<div class="col-12 col-lg-10 col-md-10 col-sm-12">
				<label for="cMaternoVoBoUpdate" class="form-label">Apellido Materno:</label>
				<div class="input-group">								
					<span class="input-group-text"><i class="bi bi-person"></i></span>
					<input type="text" id="cMaternoVoBoUpdate" name="cMaternoVoBoUpdate" size=40 maxlength="70" class="form-control form-control-sm"/>
				</div>
			</div>
		</div>
		
		<div class="row">
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">
					<label for="cPuestoVoBoUpdate" class="form-label">Puesto:</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
						<input type="text" id="cPuestoVoBoUpdate" name="cPuestoVoBoUpdate" size=40 maxlength="70" class="form-control form-control-sm"/>
					</div>
				</div>
			</div>

		<h5> Datos  Autoriza</h5>
		<hr class="mt-3">
			
		<div class="row">			
			<div class="col-12 col-lg-10 col-md-10 col-sm-12">
				<label for="cNombreAutUpdate" class="form-label">Nombre:</label>
				<div class="input-group">								
					<span class="input-group-text"><i class="bi bi-person"></i></span>
					<input type="text" id="cNombreAutUpdate" name="cNombreAutUpdate" size=40 maxlength="70" class="form-control form-control-sm"/>
				</div>
			</div>
		</div>	
		
		<div class="row">
			<div class="col-12 col-lg-10 col-md-10 col-sm-12">
				<label for="cPaternoAutUpdate" class="form-label">Apellido Paterno:</label>
				<div class="input-group">								
					<span class="input-group-text"><i class="bi bi-person"></i></span>
					<input type="text" id="cPaternoAutUpdate" name="cPaternoAutUpdate" size=40 maxlength="70" class="form-control form-control-sm"/>
				</div>
			</div>
		</div>		
		
		<div class="row">
			<div class="col-12 col-lg-10 col-md-10 col-sm-12">
				<label for="cMaternoAutUpdate" class="form-label">Apellido Materno:</label>
				<div class="input-group">								
					<span class="input-group-text"><i class="bi bi-person"></i></span>
					<input type="text" id="cMaternoAutUpdate" name="cMaternoAutUpdate" size=40 maxlength="70" class="form-control form-control-sm"/>
				</div>
			</div>
		</div>
		
		<div class="row">
			<div class="col-12 col-lg-10 col-md-10 col-sm-12">
				<label for="cPuestoAutUpdate" class="form-label">Puesto:</label>
				<div class="input-group">								
					<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
					<input type="text" id="cPuestoAutUpdate" name="cPuestoAutUpdate" size=40 maxlength="70" class="form-control form-control-sm"/>
				</div>
			</div>
		</div>	

	</div>
	<div id="dialog_remanentes" title="Disponible Ingreso Fiscal">
		<table id="tablaCLC" class="table table-striped table-sm">
			<thead>
					<tr>
						<th>Sel</th>
						<th>CLC</th>
						<th>Cuenta Por Pagar</th>
						<th>Estructura Programatica</th>
						<th>Mes</th>
						<th>Remanente</th>
						<th>Centro Contable</th>
						<th>Solicitud</th>
					</tr>
			</thead>
		</table>
		<input type="button" id="btnAgregaClaves" name="btnAgregaClaves" value="Agregar" onclick="agregarClave();"/>
	</div>
</body>
</html>
