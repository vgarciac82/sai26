<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String login = usuario.getLogin();
	String cRamo = usuario.getU_Ramo();
	String ur = usuario.getU_UR();
	String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat fe = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = fe.format(c1.getTime());
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Pago Acuerdo Ministracion De Fondos</title>
    
	<meta http-equiv="description" content="Capitulo Mil">
    
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
		
	<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>	
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="../Generador/js/Moment.js"></script>  
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>	
 
 <script type="text/javascript">
	var tablaCierre;
	var tablaCLCSeleccionados;

	$(document).ready(function (){
		
		$("input.AyudaSyC").subIniciaDlg();	//Para mostrar Ayudas
		$("#btnGenerar").attr("disabled","disabled");
		$("#btnProcesar").attr("disabled","disabled");
		$("#btnCancelarCLC").attr("disabled","disabled");
		$("#btnAnexo").attr("disabled","disabled");
		$("#btnBuscar").button();
		$("#btnAgregar").button();
		$("#btnAnexo").button();
		$("#btnNuevo").button();
		$("#btnBuscar").click(function() { buscarCxp(); });
		$("#btnAgregar").click(function() { agregar(); });
		$("#btnAnexo").click(function() { anexo(); });
		$("#btnNuevo").click(function() { 
				
				Swal.fire({
						text: "¿Desea Hacer Nuevo Layout CLC?",
						icon: "info",
						showCancelButton: true,
						confirmButtonColor: '#288BA8',
						cancelButtonColor: '#e6e6e6',
						confirmButtonText: 'Aceptar',
						cancelButtonText: 'Cancelar'
					}).then((result) => {
						if(result.isConfirmed){	
							location.reload(); 
						}
					})
		});

		$("#btnBuscarLayout").click(function(){	buscarLayout();	});
		$("#buscarLayout").change(function(){ $("#btnGenerar").removeAttr("disabled",""); });
		$("#btnGenerar").click(function() { generarLayout(); limpiar(); } );
		$("#btnProcesar").click(function() { $( "#dialog-Motivo" ).dialog( "open" ); });  
		$("#btnCancelarCLC").click(function() {

			var clc = $("#buscarLayout").val();
			
			Swal.fire({
				text: "¿Esta Seguro De Cancelar CLC " + clc + "?",
				icon: "info",
				showCancelButton: true,
				confirmButtonColor: '#288BA8',
				cancelButtonColor: '#e6e6e6',
				confirmButtonText: 'Aceptar',
				cancelButtonText: 'Cancelar'
			}).then((result) => {
				if(result.isConfirmed){
					queryFormPost("tNOMINACLCEncabezadoUpdate", {async: false }); 			
					Swal.fire({ icon: "success",
								text: "Cancelado Correctamente"});
					$("#btnProcesar").attr("disabled","disabled"); 
					$("#btnCancelarCLC").attr("disabled","disabled");
					location.reload();
				}
			})
			
		}); 

		$("#cCuentaBancaria").change(function() { $("#cuentaBancaria").val($("#cCuentaBancaria").val()); });
		$("#cIDRFC1000").change(function(){
		$("#cuentaBancaria").val($("#cCuentaBancaria").val());
		$("#rfcAMF").val($("#cIDRFC1000").val()); 
		//querySelectPost("cuentasBancarias", "cCuentaBancaria", {async: false });
		mostrarRefInfonacot(); 
		});

		//$("#cIDRFC").change(function(){  $("#rfcAMF").val($("#cIDRFC").val()); querySelectPost("cuentasBancarias", "cCuentaBancaria", {async: false }); });

		querySelectPost("tEjercicioRead", "cEjercicio", {async: false });
		$("#aEjercicioFiscal").val($("#cEjercicio").val());
		
		$("#lblRefInfonacot").hide();
		$("#cRefInfonacot").hide();
		// prueba vista
				
		$( "#dialog-Motivo" ).dialog({
			
			autoOpen: false,
			height: 500,
			width: 750,
			modal: true,
			buttons: {
					"Aceptar": function(){
						queryFormPost("leeRFCDevengado",{async: false });
						if($("#cIDRFC1000").val() == ""){
							Swal.fire({ icon: "warning",
										text: "Ingrese Un Beneficiario Por Favor"});							
							return;
						}
						if( $.trim($("#cIDRFC1000").val()) != $.trim($("#cRfcCxp").val()) ){
							Swal.fire({ icon: "warning",
										text: "El RFC no coincide con el archivo cargado de Nomina. [" + $.trim($("#cRfcCxp").val()) + "]"});
							return; 
						}
						
						if($("#mostrarRefInfonacot").val() == "1"){
							if( $.trim($("#cRefInfonacot").val()) == "" ){
								Swal.fire({ icon: "warning",
											text: "Es necesario capturar la Referencia Infonacot para continuar. Verifique!!"});										
								return; 
							}
						}
						
						procesar();
						$( this ).dialog( "close" );
					},
					
					"Cancelar": function() {						
						$("#rfcAMF").val("");
						$("#cuentaBancaria").val("");
						$( this ).dialog( "close" );
						
					}
				},
			close: function() {
					
			}							
		});

		tablaCierre = $('#tablaCLC').dataTable({      
			"bLengthChange" : true,
	          "bFilter" : true,
	          "bSort" : true,
	          "bInfo" : true,
	          "bPaginate" : false,
	          "bAutoWidth" : false,
	          "bScrollCollapse" : true,   		            
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
					sInfoFiltered: "(filtered from _MAX_ total entries)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:"
				},
			
				bServerSide: false,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true
		});	

		tablaCLCSeleccionados = $('#tablaCLCSeleccionados').dataTable({         				
			"bLengthChange" : true,
	          "bFilter" : true,
	          "bSort" : true,
	          "bInfo" : true,
	          "bPaginate" : false,
	          "bAutoWidth" : false,
	          "bScrollCollapse" : true,   		            
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
					sInfoFiltered: "(filtered from _MAX_ total entries)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:"
				},
			
				bServerSide: false,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true
		});
		
	});

function buscarCxp(){
		
	var valorConcepto = "";
	var concepto = "";
	var datos = "sinDatos";
	var idCXP = $("#caNoRecibo").val();
	var elParametro = "caNoContrarrecibo = '"+idCXP+"'";
	var caNoCompromiso = "";
	var tmovimiento = $("#tmovimiento").val();
	var movi = "";
	
	if($("#checkAP").is(":checked")){
			concepto += "'AP',";
	}if($("#checkPT").is(":checked")){
			concepto += "'PT',";
	}if($("#checkPP").is(":checked")){
			concepto += "'PP',";
	}if($("#checkPN").is(":checked")){
			concepto += "'PN',";
	}if($("#checkPI").is(":checked")){
			concepto += "'PI',";
	}if($("#checkFR").is(":checked")){
			concepto += "'FR',";
	}
	
	if(concepto != ""){	
		var max = concepto.length;
		concepto = concepto.substring(0,max - 1);
		valorConcepto = "AND ID_TIPO_CONCEPTO IN ("+concepto+")";			
	}

	if(tmovimiento != ""){		
		var tmov = tmovimiento.split(",");
		var count = tmov.length;
					
		for(var i = 0; i < count; i++ )
		{
			movi += "'"+tmov[i]+"'";
			movi += ",";
		}	
			
		tiposMovi = movi.substring(0, movi.length - 1);
		tmovimiento = " AND ID_TIPO_MOVIMIENTO IN ("+tiposMovi+")";		
	}
	
	//Buscar el Folio Para Mostrar Sus Detalles
	
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "TNOMINAENCABEZADO", Campos:"nFolioNOMINA, isnull(solicitudPago,'vacio') solicitudPago, isnull(cDocumentoHaplicado, 'sinAplicar') cDocumentoHaplicado, caNoCompromiso ", Param:elParametro, Order:"", MaxReg: "10", ajax: 'true'}, function(j){
				
				for (var i = 0; i < j.length; i++) {
						
						var nFolio = j[i].Col0;
						var solicitudPago = j[i].Col1;
						var cDocumentoHaplicado = j[i].Col2;
						caNoCompromiso = j[i].Col3;
						datos = "datos";
				}		
				
				if(solicitudPago == "vacio"){
					Swal.fire({ icon: "warning",
								text: "La Cuenta Por Pagar No Tiene Solicitud Pago"});					
					return;
					
				}else if(cDocumentoHaplicado == 'C' ){
					Swal.fire({ icon: "warning",
								text: "El Documento Esta Cancelado"});					
					return;
					
				}else if(cDocumentoHaplicado == 'sinAplicar' || cDocumentoHaplicado == ""){
					Swal.fire({ icon: "warning",
								text: "El Documento No Esta Aplicado"});					
					return;
					
				}
				
				// Si Encuentra La Cuenta Por Pagar Buscar Sus Detalles
				
				if(datos == "datos"){
					
					valorConcepto;
					//var camposDet = "nFolioNOMINA = "+nFolio+ " "+valorConcepto+ " "+tablaCLC ;
					//var elParametro = "";
					
					$('#tablaCLC').dataTable().fnClearTable();
					
					buscarCXPVista(nFolio, valorConcepto, tmovimiento);
							
					
					
					$("#nFolioNOMINAn").val(nFolio);
					//$("#caNoRecibo").val("");
					
				}else{
					alert("No se Encontro Informacion Con La Solicitud de Pago Ingresada");
				}
	
	});
}
var nRowsCLC = 1;
function agregar(){
	
	var table = document.getElementById("tablaCLC");
	var data = $('#tablaCLC').dataTable().fnGetNodes();
	var total = $("#importeTotal").html();
	var mImporteNeto = $("#mImporteNeto").val();
	
	
	var oTable = $('#tablaCLC').dataTable();
	var aData = oTable.fnGetData();
	var nRows = $("#tablaCLC tr").length -1 ;
	var arrlist = new Array();
	var j = 0;
	
	for(var i=0; i < nRows; i++){
		if ( $('input', data[i] )[2].checked ){
			
				var paso = aData[ i ];
				paso[11] = "<input type=hidden name=\'nDocRenglonCLC\' id=\'nDocRenglonCLC\' value='"+nRowsCLC+"' size=10 style=\'background-color:#E0E0F8; border:1px solid #E0E0F8; text-align:center\' readonly=\'readonly\' >";
				arrlist[ j++ ] = paso;
				nRowsCLC++;
		}
	}

	$("#btnProcesar").removeAttr("disabled","");
	
	$("#tablaCLCSeleccionados").dataTable().fnAddData( arrlist );
				
	total = Number(quitaFmt(total)) + Number(mImporteNeto);
	$("#importeAgregado").html(total);
	$("#importeAgregado").formatCurrency();
	$("#mImporteNeto").val(total);
	
	$("#importeTotal").html(0.00);
	//$("#mImporteBruto").val(total.toFixed(2));
	//$("#mImporteMasIva").val(total.toFixed(2));
	
	
	$('#tablaCLC').dataTable({ 
					"bLengthChange" : true,
					"bFilter" : true,
					"bSort" : true,
					"bInfo" : true,
					"bPaginate" : false,
					"bAutoWidth" : false,
					"bScrollCollapse" : true,   		            
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_NominaCapituloMil&qw=" + " caNoContrarrecibo = '0' ",	
			});	
	
		

}

function procesar()
{
	$("#cConcepto").val($("#cConceptoTxt").val());
	getNextSequenceVal({seqName: "NOMINA-CLC" , async: false, callback: setSequenceVal});
	
	queryFormPost("tNOMINACLCEncabezado,tNOMINACLCDetalle", {async: false });
	
	var mostrar = $("#mostrarRefInfonacot").val();
	
	if(mostrar == "1"){
		$("#cRef1SICOP").val($("#cRefInfonacot").val());
		queryFormPost("tNOMINACLCEncabezadoRefSICOP_Update", {async: false });
	}
	
	alert("Guardado Correctamente con el Numero "+$("#caNoContrarreciboCLC").val());
	$("#buscarLayout").val($("#caNoContrarreciboCLC").val());
	$("#btnProcesar").attr("disabled","disabled");
	$("#btnGenerar").removeAttr("disabled","");
	$("#btnAnexo").removeAttr("disabled","");
	$("#caNoRecibo").val("");
	$("#mImporteNeto").val(0);
	//$('#tablaCLCSeleccionados').dataTable().fnClearTable();

}

function setSequenceVal(seqValue) 
{
	
		$("#nFolioNOMINACLC").val( seqValue );
	
		seqValue = "000000" + seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = "1" + seqValue.substr(seqValue.length - 5);
		seqValue = "<%=cCentroContable%>" + "NC" + $("#aEjercicioFiscal").val() + seqValue;
		//seqValue = "CLC-"+seqValue;
		$("#caNoContrarreciboCLC").val( seqValue );
	
}
function quitaFmt( val ) 
{
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val;
}

function buscarLayout(){
	
	$("#btnGenerar").attr("disabled","disabled");
	$("#btnProcesar").attr("disabled","disabled");
	$("#btnAnexo").attr("disabled","disabled");
	
	if($("#buscarLayout").val() == ""){
		Swal.fire({ icon: "warning",
					text: "Ingrese Numero CLC Para Buscar Layout"});			
		return;
	}
	var clc = $("#buscarLayout").val();
	var datos = "sinDatos";
	var camposWhere = " '"+clc+"'";
	var elParametro = "";
		
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "TNOMINACLCENCABEZADOMIL", Campos:camposWhere, Param:elParametro, Order:"", MaxReg: "10", ajax: 'true'}, function(j){
				
				for (var i = 0; i < j.length; i++) {
						
						var nFolioCLC = j[i].Col0;
						$("#nFolioNOMINACLCLayout").val(nFolioCLC);
						var activo = j[i].Col1;
						var ejercido = j[i].Col2;
						datos = "datos";
				}
				
				if( ejercido == "S"){
					
					alert("La CLC "+clc+" Esta Ejercida No se Puede Cancelar");
					$("#btnCancelarCLC").attr("disabled","disabled");
					$("#btnGenerar").removeAttr("disabled","");
					$("#btnAnexo").removeAttr("disabled","");
					return;
					
				}
				if(datos == "datos" && activo == 1){
					
					var camposDet = "nFolioNOMINACLC = "+nFolioCLC ;
					var elParametro = "";
					$("#btnCancelarCLC").removeAttr("disabled","");
					$("#btnGenerar").removeAttr("disabled","");
					$("#btnAnexo").removeAttr("disabled","");				
				
				}else{
					if(activo == 0){
						alert("El Numero "+clc+" Esta Cancelado");
						return;
					}
					alert("No se Encontro Informacion Con La Solicitud de Pago Ingresada");
					$("#btnCancelarCLC").attr("disabled","disabled");
					$("#btnGenerar").attr("disabled","disabled");
					$("#btnAnexo").attr("disabled","disabled");
				}
		});
	
}
function limpiar(){
	$("#btnGenerar").attr("disabled","disabled");
	$("#btnProcesar").attr("disabled","disabled");
	$("#btnAnexo").attr("disabled","disabled");
	$("#caNoRecibo").val("");
	$("#buscarLayout").val("");
	$("#cIDRFC1000").val("");
	$("#cCuentaBancaria").val("");
	$('#tablaCLC').dataTable().fnClearTable();
	$('#tablaCLCSeleccionados').dataTable().fnClearTable();
}

function buscarCXPVista(nFolioNOMINA, concepto, tMovimiento){
	
	var szTabla = "v_NominaCapituloMil";
	var camposWhere = " folio = '"+nFolioNOMINA+"'" +concepto+ " " +tMovimiento;
	var elParametro = "";
	var order = "";
	 
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:camposWhere, Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j){
				
					for (var i = 0; i < j.length; i++){
						
						var importeTotal = j[i].Col0;
						$("#importeTotal").html(importeTotal);
						$("#importeTotal").formatCurrency();
							
					}		
			 });
	
	var oTable = $('#tablaCLC').dataTable();
	oTable.fnClearTable();
	$('#tablaCLC').dataTable({         
							"bLengthChange" : true,
							"bFilter" : true,
							"bSort" : true,
							"bInfo" : true,
							"bPaginate" : false,
							"bAutoWidth" : false,
							"bScrollCollapse" : true,   		            
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
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_NominaCapituloMil&qw=" + " folio = "+nFolioNOMINA+" "+concepto+" "+tMovimiento ,						
							aaSorting: [[ 3, "asc" ]] ,
							aoColumns: [
							    { sName: "nFolioNomina", 		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"25px"},
								{ sName: "nDocRenglon",	 		bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"25px"},
								{ sName: "checkRenglon", 		bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignRight", sWidth:"27px"},
								{ sName: "caNoContrarrecibo",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"32px"},
								{ sName: "EP",					bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignLeft", sWidth:"102px" },
								{ sName: "cMes",  				bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignCenter", sWidth:"22px"},
								{ sName: "movimiento",			bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignLeft", sWidth:"22px"},
								{ sName: "concepto",			bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"20px"},
								{ sName: "mImporteNetoDet",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight", sWidth:"50px"},
								{ sName: "caNoCompromiso",		bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"32px"},
								{ sName: "cEventoDet",			bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"10px"},
								{ sName: "nDocRenglonCLC",		bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"10px"}
							]
							
		        });	
			
	
	
} 
function importeTotal(valor, monto){
	
	var importeTotal = $("#importeTotal").html();
		
	if($("#"+valor).is(":checked")){
		importeTotal = Number(quitaFmt(importeTotal)) + Number(monto);	
		
	}else{
		importeTotal = Number(quitaFmt(importeTotal)) - Number(monto);
	}
	$("#importeTotal").html(importeTotal.toFixed(2));
	$("#importeTotal").formatCurrency();
}

function valida_concepto(e) {
	
		var nChars = $("#cConceptoTxt").val();
		nChars = nChars.length;
		tecla = (document.all) ? e.keyCode : e.which;
		
		if (tecla==8) {
			$("#nChars").val( --nChars );
			if (nChars < 0)
				$("#nChars").val( 0 );
			return true;
		}
		
		if (nChars >= 70) {
			return false;
		}
		$("#nChars").val( ++nChars );
		patron =/[A-Za-z.\d\s\\. `]/;
		te = String.fromCharCode(tecla);
		return patron.test(te);
}

function anexo(){
		
		window.open(
					"../admin/SeguridadCatalogos?"
						+ "catalogo=ANEXO"
						+ "&accion=run"
						+ "&rn=AnexoCapMil.jasper"
						+ "&nFolioOperAjenas=" + $("#nFolioNOMINACLCLayout").val(),
					"Anexo",
					"scrollbars=1, resizable=yes, width=1024, height=768");
}

function generarLayout(){
	$("#capituloMilCLC").submit();
}

function mostrarRefInfonacot(){

	$("#mostrarRefInfonacot").val("0");
	queryFormPost("mostrarRefInfonacot_Read", {async: false });
	
	var mostrar = $("#mostrarRefInfonacot").val();
	
	if(mostrar == "1"){
		$("#lblRefInfonacot").show();
		$("#cRefInfonacot").show();
	}else if(mostrar == "0"){
		$("#lblRefInfonacot").hide();
		$("#cRefInfonacot").hide();
	}
	
}
</script>
</head>
<br/>
<body id="dt_example">
	<form method="post" id="capituloMilCLC" name="capituloMilCLC" action="../gstnmngr/LayoutPagosCLCServlet" >					  		
		<div id="container"class="ms-5" class="container" style="width: 90%">										
			<input type="hidden" id="nFolioNOMINACLC" name="nFolioNOMINACLC" />
			<input type="hidden" id="nFolioNOMINAn" name="nFolioNOMINAn" />
			<input type="hidden" id="nFolioNOMINACLCLayout" name="nFolioNOMINACLCLayout" />
			<input type="hidden" name="fAplicacion" id="fAplicacion" value="<%= today%>" />
			<input type="hidden" name="cRamo" id="cRamo" value="<%= cRamo%>" />
			<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value="<%= ur%>" />
			<select style="visibility: hidden" name="cEjercicio" id="cEjercicio"></select>
			<input type="hidden" id="rfcAMF" name="rfcAMF" />
			<input type="hidden" id="cuentaBancaria" name="cuentaBancaria" />
			<input type="hidden" name="mImporteNeto" id="mImporteNeto" value="0" />
			<input type="hidden" id="caNoContrarreciboCLC" name="caNoContrarreciboCLC" />
			<input type="hidden" id="cIdUsuarioCaptura" name="cIdUsuarioCaptura" value="<%= login%>" />
			<input type="hidden" id="aEjercicioFiscal" name="aEjercicioFiscal" />
			<input type="hidden" name="cCentroContable" id="cCentroContable" value="<%= cCentroContable%>" />
			<!-- input type="text" name="mImporteNeto" id="mImporteNeto" / -->
			<input type="hidden" name="cUnidadResponsableContable" id="cUnidadResponsableContable" value="RHQ">
			<input type="hidden" name="cIdRelacion" id="cIdRelacion" value="NominaCLC"/>
			<input type="hidden" id="cConcepto" name="cConcepto"/>
			<input type="hidden" id="cRfcCxp" name="cRfcCxp"/>
			<input type="hidden" id="cRef1SICOP" name="cRef1SICOP" value=""/>
			<input type="hidden" id="mostrarRefInfonacot" name="mostrarRefInfonacot" value="0"/>
		
			<div class="card-header"> <h3> Generar Layout CLC </h3> </div>
			<hr class="mt-3"/>
				
			<div class="row d-flex">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="caNoRecibo" class="form-label"> Cuenta Por Pagar: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<input type="text" id="caNoRecibo" name="caNoRecibo" class="form-control form-control-sm"/>
				</div>								
			</div>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="caNoRecibo" class="form-label"> Concepto: </label>
				</div>
				<div class="col-12 col-lg-1 col-md-2 col-sm-12 p-1">	
					<input type="checkbox" id="checkAP" name="checkAP" value="AP" class="form-check-input">
					<label for="checkAP" class="form-check-label">AP</label>
				</div>								
				<div class="col-12 col-lg-1 col-md-2 col-sm-12 p-1">	
					<input type="checkbox" id="checkPT" name="checkPT" value="PT" class="form-check-input">
					<label for="checkPT" class="form-check-label">PT</label>
				</div>								
				<div class="col-12 col-lg-1 col-md-2 col-sm-12 p-1">	
					<input type="checkbox" id="checkPN" name="checkPN" value="PN" class="form-check-input">
					<label for="checkPN" class="form-check-label">PN</label>
				</div>								
				<div class="col-12 col-lg-1 col-md-2 col-sm-12 p-1">	
					<input type="checkbox" id="checkPP" name="checkPP" value="PP" class="form-check-input">
					<label for="checkPP" class="form-check-label">PP</label>
				</div>								
				<div class="col-12 col-lg-1 col-md-2 col-sm-12 p-1">	
					<input type="checkbox" id="checkPI" name="checkPI" value="PI" class="form-check-input">
					<label for="checkPI" class="form-check-label">PI</label>
				</div>								
				<div class="col-12 col-lg-1 col-md-2 col-sm-12 p-1">	
					<input type="checkbox" id="checkFR" name="checkFR" value="FR" class="form-check-input">
					<label for="checkFR" class="form-check-label">FR</label>
				</div>								
			</div>

			<div class="row d-flex">							
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="tmovimiento" class="form-label"> Tipo Movimiento: </label>
				</div>
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
					<input type="text" id="tmovimiento" name="tmovimiento" class="form-control form-control-sm"/>
					<span class="label">Dividido por comas (,). </span>
					<br/>
					<span class="label">Ejemplo 102,198,113</span>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
					<input type="button" id="btnBuscar" name="btnBuscar" value="Buscar" class="btn btn-secondary btn-sm"/>				
				</div>								
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
					<input type="button" id="btnNuevo" name="btnNuevo" value="Nuevo" class="btn btn-secondary btn-sm"/>
				</div>								
			</div>
			
			<br/>

			<h5> Datos Cuentas por Pagar </h5>
			<hr class="mt-3">
			
			<div class="row d-flex">
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1" align="right">							
					<label for="importeTotal" class="form-label" > Total Cuenta Por Pagar </label>
					<div id="importeTotal" align="right" style="font-size: 12pt">0.00</div>
				</div>								
			</div>

			<div class="row d-flex">								
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
					<div class="table-responsive">	    				           
						<table id="tablaCLC" class="table table-striped table-bordered" >
							<tbody>
								<thead>
									<tr>
										<th>Folio</th>
										<th>Num</th>	
										<th>Seleccione</th>	
										<th>Cuenta Por Pagar</th>
										<th>Estructura Programatica</th>
										<th>Mes</th>
										<th>Movimiento</th>
										<th>Concepto</th>
										<th>Importe</th>
										<th>Compromiso</th>
										<th>E</th>
										<th>N</th>																				
									</tr>	
								</thead>							
							</tbody>	
						</table>
					</div>						
				</div>
			</div>
						
			<div class="row d-flex">
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1" align="center">							
					<input type="button" id="btnAgregar" name="btnAgregar" value="Agregar" class="btn btn-secondary btn-sm"/>
				</div>								
			</div>
			
			<h5> Datos Cuentas por Pagar Seleccionados </h5>
			<hr class="mt-3">
			
			<div class="row d-flex">
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1" align="right">							
					<label for="importeAgregado" class="form-label" > Total Cuenta Por Pagar Agregados </label>
					<div id="importeAgregado" align="right" style="font-size: 12pt">0.00</div>
				</div>								
			</div>

			<div class="row d-flex">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1" align="right">							
					<label for="buscarLayout" class="form-label" > Buscar Layout</label>						
				</div>								
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="text" id="buscarLayout" name="buscarLayout" class="form-control form-control-sm" />
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<input type="button" id="btnBuscarLayout" name="btnBuscarLayout" class="btn btn-secondary btn-sm" value="Buscar">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<input type="button" id="btnCancelarCLC" name="btnCancelarCLC" class="btn btn-secondary btn-sm" value="Cancelar CLC">
				</div>
			</div>
		
			<div class="row d-flex">								
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
					<div class="table-responsive">	    				           
						<table id="tablaCLCSeleccionados" class="table table-striped table-bordered" >																		
							<tbody>
								<thead>
									<tr align="center">
										<th>Folio</th>
										<th>Num</th>
										<th>Seleccionado</th>
										<th>Cuenta Por Pagar</th>
										<th>Estructura Programatica</th>
										<th>Mes</th>
										<th>Movimiento</th>
										<th>Concepto</th>
										<th>Importe</th>
										<th>Compromiso</th>
										<th>E</th>
										<th>N</th>
										
									</tr>	
								</thead>							
							</tbody>	
						</table>
					</div>
				</div>
			</div>						

			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
					<input type="button" id="btnProcesar" name="btnProcesar" class="btn btn-secondary btn-sm" value="Procesar">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<input type="button" id="btnGenerar" name="btnGenerar" class="btn btn-secondary btn-sm" value="Generar">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<input type="button" id="btnAnexo" name="btnAnexo" class="btn btn-secondary btn-sm" value="Imprimir Anexo">
				</div>
			</div>			   	
 			
 		</div>

		<div id="dialog-Motivo" title="Beneficiario">
			<div class="row d-flex">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
					<label for="importeAgregado" class="form-label" > RFC </label>
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					<div class="input-group">					
						<input type="text" id="cIDRFC1000" name="cIDRFC1000" class="form-control form-control-sm AyudaSyC" readonly/>
					</div>
				</div>
			</div>

			<div class="row d-flex">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
					<label for="cnombre" class="form-label" > Razón Social </label>
				</div>
				<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">					
						<input type="text" id="cnombre" name="cnombre" class="form-control form-control-sm" readonly/>					
				</div>
			</div>

			<div class="row d-flex">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
					<label for="cCuentaBancaria" class="form-label" > Cuenta Bancaria </label>
				</div>
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">					
						<input type="text" id="cCuentaBancaria" name="cCuentaBancaria" class="form-control form-control-sm" readonly/>					
				</div>
			</div>

			<div class="row d-flex">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
					<label for="cConceptoTxt" class="form-label" > Concepto </label>
				</div>
				<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">					
						<input type="text" id="cConceptoTxt" name="cConceptoTxt" class="form-control form-control-sm" onkeydown="return valida_concepto(event)"/>					
				</div>
			</div>

			<div class="row d-flex">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
					<label for="lblRefInfonacot" class="form-label" > Ref Infonacot </label>
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">					
						<input type="text" id="cRefInfonacot" name="cRefInfonacot" class="form-control form-control-sm" />					
				</div>
			</div>
		</div>
	</form>
</body>
</html>
