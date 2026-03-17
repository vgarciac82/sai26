<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
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
String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>  
    <title>Capitulo Mil Dev Compromiso</title>
    
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
var mImporteTotalG = 0;
var tablaCierre;
var tablaSeleccionados;

$(document).ready(function (){
	
	getNextSequenceVal({seqName: "COMPROMISO" + "", async: false, callback: setSequenceValComp});
	
	$("#btnBuscar").button();
	$("#btnNuevo").button();
	$("#btnAplicar").button();
	$("#btnAgregar").button();
	$("#btnProcesar").button();
	$("#btnBuscar").click(function() { buscarCompromiso();});
	$("#btnAgregar").click(function() { agregar(); });
	$("#btnProcesar").click(function() { $( "#dialog-Motivo" ).dialog( "open" ); }); 
	$("#compromiso").focus();
	$("#btnProcesar").attr("disabled","disabled");
	$("#btnNuevo").click(function() {
		Swal.fire({
			text: "¿Desea Hacer Nueva Devolucion?",
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
	$("#btnAplicar").click(function() { $( "#dialog-MotivoExterno" ).dialog( "open" ); } )
	if($("#usuario").val() == "admin" ){ $("#tblAplicarCompromiso").hide(); /* Para aplicar cambiar por show*/  }else{ $("#tblAplicarCompromiso").hide(); }
	
	$( "#dialog-Motivo" ).dialog({
			autoOpen : false,
			height : 250,
			width : 500,
			modal : true,
			buttons : {
					"Aceptar": function()
					{
						$( "#dialog-Motivo" ).dialog( "close" ); 
						procesar();
					},
					"Cancelar": function() {
						$( this ).dialog( "close" );
					}
			}
	});
	
	$( "#dialog-Procesando" ).dialog({
			autoOpen : false,
			height : 250,
			width : 500,
			modal : true
	});
	
	$( "#dialog-Terminado" ).dialog({
			autoOpen : false,
			height : 250,
			width : 500,
			modal : true,
			buttons:{
					"Aceptar":function(){
						$( "#dialog-Terminado" ).dialog( "close" );
						location.reload();	
					}
			}
	});
	 
	$( "#dialog-MotivoExterno" ).dialog({
			autoOpen : false,
			height : 250,
			width : 500,
			modal : true,
			buttons : {
					"Aceptar": function()
					{
						$( "#dialog-MotivoExterno" ).dialog( "close" ); 
						procesarExterno();
					},
					"Cancelar": function() {
						$( this ).dialog( "close" );
					}
			}
	});
	
	tablaCierre = $('#tablaCompromiso').dataTable({        
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_CompromisoDevCLC&qw=" + " caNoCompromiso = '0' ",					
			});
	
	var tablaSeleccionados = $('#tablaSeleccionados').dataTable({         
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

	$('#pbDesmarcar')
		.button()
		.click( function() {
		if( $( this ).html() == "<SPAN class=ui-button-text>DesMarcar</SPAN>" ){
			$( this ).html("<SPAN class=ui-button-text>Marcar</SPAN>");
			$( ".marcar" ).attr('checked', false);
			//$("#importeTotal").html(0);
			$("#importeTotal").formatCurrency();
		}else{
			$( this ).html("<SPAN class=ui-button-text>DesMarcar</SPAN>");
			$( ".marcar" ).attr('checked', true);
			//$("#importeTotal").html( mImporteTotalG );
			$("#importeTotal").formatCurrency();
		}
	} );

});

function buscarCompromiso(){
	
	var compromiso = $("#compromiso").val();
	
	var szTabla = "TCOMPROMISONOMINAENCABEZADO_DEV";
	var camposWhere = " caNoCompromiso = '"+compromiso+"'";
	var elParametro = "";
	var order = "";
	var statusDoc= "";
	
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:camposWhere, Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j){
				
			for (var i = 0; i < j.length; i++){
						
				$("#fCarga").val(j[i].Col0);
				$("#cIdContrato").val(j[i].Col1);
				$("#cTipoContrato").val(j[i].Col2);
				$("#fAplicacion").val(j[i].Col3);
				$("#cCentroContable").val(j[i].Col4);
				$("#cRamo").val(j[i].Col5);
				$("#cUnidadResponsable").val(j[i].Col6);
				$("#cTipoPoliza").val(j[i].Col7);
				$("#nMes").val(j[i].Col8);
				$("#aEjercicioFiscal").val(j[i].Col9);
				$("#cEjercicio").val(j[i].Col9);
				$("#cUnidadResponsable").val(j[i].Col10);
				$("#cDescripcionPoliza").val(j[i].Col11);
				$("#nFolioSICOP").val(j[i].Col12);
				statusDoc =	j[i].Col13;
			}
			
			if(statusDoc == "C"){
				Swal.fire({ icon: "info",
							text: "El Compromiso Esta Cancelado"});				
				return;
				
			}if(statusDoc == "0"){
				Swal.fire({ icon: "info",
							text: "El Compromiso No Esta Aplicado"});				
				return;
			}
			
			var szTabla = "v_CompromisoDevCLC";
			var camposWhere = " caNoCompromiso = '"+compromiso+"' AND retencionTe > 0.00";
			var elParametro = "";
			var order = "";
			 
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:camposWhere, Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j){
						
							for (var i = 0; i < j.length; i++){
								
								var importeTotal = j[i].Col0;
								mImporteTotalG = importeTotal;
								$("#importeTotal").html(importeTotal);
								$("#importeTotal").formatCurrency();
							}		
			 });
	
	var oTable = $('#tablaCompromiso').dataTable();
	oTable.fnClearTable();
	
	var retencionTe = "retencionTe";
	
	$('#tablaCompromiso').dataTable({         
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
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_CompromisoDevCLC&qw=" + " caNoCompromiso = '"+compromiso+"' AND  retencionTe > 0.00",							
							aaSorting: [[ 0, "asc" ]] ,
							aoColumns: [
								{ sName: "renglon",			        bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"32px"},
							    { sName: "caNoCompromisoT", 		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"20px"},
								{ sName: "checkRenglon",	 		bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"25px"},
								{ sName: "nFolioCompromisoNominaAnt", 	bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignRight", sWidth:"80px"},
								//{ sName: "nFolioNomina",			bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"32px"},
								{ sName: "EP",						bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignLeft", sWidth:"402px" },
								{ sName: "cMes",					bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignLeft", sWidth:"30px" },
								//{ sName: "importeCompromiso",  		bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignCenter", sWidth:"22px"},
								//{ sName: "importeCXP",				bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignLeft", sWidth:"22px"},
								//{ sName: "clcImporte",				bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"20px"},
								//{ sName: "importeDevCXP",			bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight", sWidth:"50px"},
								//{ sName: "importeCompromisoDev",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"32px"},
								{ sName: "retencionFormato",		bSearchable: false,	bSortable: true, bVisible: true, sClass: "alignRight", sWidth:"32px"},
								{ sName: "retencion",				bSearchable: false,	bSortable: true, bVisible: true, sClass: "alignRight", sWidth:"32px"},
								{ sName: "cEventoDet",				bSearchable: false,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"32px"},
								{ sName: "nFolioCompromisoNominaDet",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"32px"},
								{ sName: "retencionFijo",				bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignRight", sWidth:"32px"}
							]		
		        });
	});

}

var nRowsCLC = 1;
function agregar(){
	
	var table = document.getElementById("tablaCompromiso");
	var data = $('#tablaCompromiso').dataTable().fnGetNodes();
	var total = $("#importeTotal").html();
	var mImporteNeto = $("#mImporteNeto").val();
	var mImporteAgregado = $("#mImporteAgregado").val();
	
	var oTable = $('#tablaCompromiso').dataTable();
	var aData = oTable.fnGetData();
	
	var nRows = $("#tablaCompromiso tr").length -1 ;
	var arrlist = new Array();
	var j = 0;
	
	var nFolioCompromisoDet = $("#nFolioCompromisoNomina").val(); 
		
	for(var i=0; i < nRows; i++){
		
		if ( $('input', data[i] )[1].checked ){
			
				var paso = aData[ i ];
				var monto = Number( $('input', data[i] )[6].value ) * -1;
				
				var montoFormato = $('input', data[i] )[5].value;
				paso[0] = "<input type=text name=\'nDocRenglon\' id=\'nDocRenglon\' value='"+nRowsCLC+"' size=9 style=\'background-color:#E0E0F8; border:1px solid #E0E0F8; text-align:center\' readonly=\'readonly\' >";
				paso[2] = "";
				paso[6] = "<input type=text name=\'retencionFormato\' id=\'retencionFormato\' value='"+montoFormato+"' size=8 style=\'background-color:#E0E0F8; border:1px solid #E0E0F8; text-align:right\' readonly=\'readonly\' >";
				paso[7] = "<input type=hidden name=\'retencion\' id=\'retencion\' value='"+monto+"' size=8 style=\'background-color:#E0E0F8; border:1px solid #E0E0F8; text-align:right\' readonly=\'readonly\' >";
				paso[9] = "<input type=hidden name=\'nFolioCompromisoNominaDet\' id=\'nFolioCompromisoNominaDet\' value='"+nFolioCompromisoDet+"' size=10 style=\'background-color:#E0E0F8; border:1px solid #E0E0F8; text-align:center\' readonly=\'readonly\' >";
				arrlist[ j++ ] = paso;
				nRowsCLC++;
				mImporteAgregado = Number(mImporteAgregado) + Number(monto);
		}
	}

	$("#btnProcesar").removeAttr("disabled","");
	$("#tablaSeleccionados").dataTable().fnAddData( arrlist );
				
	total = Number(quitaFmt(total)) + Number(mImporteNeto);
	 
	$("#mImporteAgregado").val(mImporteAgregado);
	$("#importeAgregado").html(mImporteAgregado);
	$("#importeAgregado").formatCurrency();
	$("#mImporteNeto").val(total);
	$("#importeTotal").html(0.00);
	
	$('#tablaCompromiso').dataTable({      
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_CompromisoDevCLC&qw=" + " caNoCompromiso = '0' ",					
			});	
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

function procesar(){
	
		$( "#dialog-Procesando" ).dialog( "open" );
		$("#divEsperaProcesando").attr("style","visibility=visible");
		
		// getNextSequenceVal({seqName: "COMPROMISO" + "", async: false, callback: setSequenceValComp});	
		getNextSequenceVal({seqName: "CO-" + "<%=cCentroContable%>", async: false, callback: setSequenceVal});
			
		queryFormPost("TCOMPROMISONOMINAENCABEZADO_DEV,TCOMPROMISONOMINAENCABEZADO_DEV_DET", {async: false });
		
			var tipo = "capituloMilDevCompromiso";
			var caNoCompromiso = $("#caNoCompromiso").val();
			
			$.ajax({
					url:'./cierrePresupuestal.jsp',
					type:'post',
					dataType: 'json',
					data:{tipo:tipo,caNoCompromiso:caNoCompromiso},
					success:function(data){
						
							if(data.sinSesion == 'sinSesion'){
									location.href = "../index.jsp";
							}
							if(data.estatus == "guardado"){
								
									$("#divEsperaProcesando").attr("style","visibility=hidden");
									$( "#dialog-Procesado" ).dialog( "close" ); 
									$( "#dialog-Terminado" ).dialog( "open" ); 
									divEsperaTerminado.innerHTML = "Aplicado Correctamente Devolucion Compromiso: "+$("#caNoCompromiso").val();
									
							}else{
									
									$("#divEsperaProcesando").attr("style","visibility=hidden");
									$( "#dialog-Procesado" ).dialog( "close" ); 
									$( "#dialog-Terminado" ).dialog( "open" ); 
									divEsperaTerminado.innerHTML = "No Se Aplico Correctamente: "+$("#caNoCompromiso").val();		
									
							}
					}
			});
}

function setSequenceVal(seqValue) {
	
		seqValue = 100000 + parseInt(seqValue, 10);
		seqValue = "<%=cCentroContable%>" + "CO" + $("#cEjercicio").val() + seqValue;
		$("#caNoCompromiso").val( seqValue );
}

function setSequenceValComp(seqValue) {
		$("#nFolioCompromisoNomina").val( seqValue );
		//$("#nFolioCompromisoNominaDet").val( seqValue );		
}

function importeRetencion(valor,numFijo){
	
		var valorFijo = $("#retencionFijo_"+numFijo).val();
		
		if(Number(quitaFmt(valor)) > Number(valorFijo)){
			
			Swal.fire({ icon: "warning",
						text: "Importe Ingresado Es Mayor a Retencion"});
			$("#retencionFormato_"+numFijo).val($("#retencionFijo_"+numFijo).val());
			return;
		}	
}

function importeTotal(valor, numFijo){
	
		var importeTotal = $("#importeTotal").html();
		var monto = $("#retencion_"+numFijo).val();
			
		if($("#"+valor).is(":checked")){
			importeTotal = Number(quitaFmt(importeTotal)) + Number(monto);	
			
		}else{
			importeTotal = Number(quitaFmt(importeTotal)) - Number(monto);
		}
		$("#importeTotal").html(importeTotal.toFixed(2));
		$("#importeTotal").formatCurrency();
}

function valor (valor){
	
	if(valor >= 0){
		
	}else{
		Swal.fire({ icon: "error",
					text: "El Importe Ingresado Es Incorrecto"});		
		return;
	}	
}

function Sinfrmt(fld){
		var valcol = $("#"+fld).val();
		valcol = valcol.replace("$", "");
		valcol = valcol.replace(",", "");
		$("#"+fld).val(valcol);
}

function cambiafrmt(fld){

	   	$("#"+fld).formatCurrency();
}

function actualizarCompromiso(valor){
	
	$("#retencion_"+valor).val(quitaFmt($("#retencionFormato_"+valor).val()));
}

function procesarExterno(){
	
	$("#dialog-Procesando" ).dialog( "open" );
	$("#divEsperaProcesando").attr("style","visibility=visible");
	
	var tipo = "capituloMilDevCompromiso";
	var compromisoExterno = $("#compromisoExterno").val();
	var szTabla = "TCOMPROMISONOMINAENCABEZADO_DEV";
	var camposWhere = " caNoCompromiso = '"+compromisoExterno+"'";
	var elParametro = "";
	var order = "";
	var statusDoc= "";
	var datos = "sinDatos";
	
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:camposWhere, Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j){
				
			for (var i = 0; i < j.length; i++){
					statusDoc =	j[i].Col13;
					datos = "datos";
			}
			
			if(statusDoc == "C"){
				$("#dialog-Procesando" ).dialog( "close" );
				$("#divEsperaProcesando").attr("style","visibility=hidden");
				Swal.fire({ icon: "info",
							text: "El Compromiso Esta Cancelado"});				
				
				return;
				
			}if(statusDoc == "S"){
				$("#dialog-Procesando" ).dialog( "close" );
				$("#divEsperaProcesando").attr("style","visibility=hidden");
				Swal.fire({ icon: "info",
							text: "El Compromiso Ya Esta Aplicado"});				
				return;
			}if(datos == "sinDatos"){
				$("#dialog-Procesando" ).dialog( "close" );
				$("#divEsperaProcesando").attr("style","visibility=hidden");
				Swal.fire({ icon: "info",
							text: "No Existe Compromiso"});				
				return;
			}
	
			$.ajax({
							url:'./cierrePresupuestal.jsp',
							type:'post',
							dataType: 'json',
							data:{tipo:tipo,caNoCompromiso:compromisoExterno},
							success:function(data){
								
									if(data.sinSesion == 'sinSesion'){
											location.href = "../index.jsp";
									}
									if(data.estatus == "guardado"){
										
											$("#divEsperaProcesando").attr("style","visibility=hidden");
											$( "#dialog-Procesado" ).dialog( "close" ); 
											$( "#dialog-Terminado" ).dialog( "open" ); 
											divEsperaTerminado.innerHTML = "Aplicado Correctamente Devolucion Compromiso: "+compromisoExterno;
											
									}else{
											
											$("#divEsperaProcesando").attr("style","visibility=hidden");
											$( "#dialog-Procesado" ).dialog( "close" ); 
											$( "#dialog-Terminado" ).dialog( "open" ); 
											divEsperaTerminado.innerHTML = "No Se Aplico Correctamente: "+compromisoExterno;		
											
									}
							}
				});
	});
}

function motor(){
	
	var tipo = "capituloMilDevCompromiso";
	var caNoCompromiso = $("#compromiso").val();
	
	$.ajax({
				url:'./cierrePresupuestal.jsp',
				type:'post',
				dataType: 'json',
				data:{tipo:tipo,caNoCompromiso:caNoCompromiso},
				success:function(data){
						
					if(data.sinSesion == 'sinSesion'){
							location.href = "../index.jsp";
					}
					if(data.estatus == "guardado"){
						Swal.fire({ icon: "success",
									text: "Aplicado Correctamente Devolucion Compromiso: "+$("#compromiso").val()});							
									
					}else{
						Swal.fire({ icon: "error",
									text: "No Se Aplico Correctamente: "+$("#compromiso").val()});								
						
					}
				}
	});	
}
</script>
</head>
<br/>
<body id="dt_example">  		
	<form id="capituloMilDevComp" name="capituloMilDevComp">
		<div id="container"class="ms-5" class="container" style="width: 90%">								
			<input type="hidden" name="nFolioCompromisoNomina" id="nFolioCompromisoNomina" />
			<input type="hidden" name="fCarga" id="fCarga"/>
			<input type="hidden" name="cIdContrato" id="cIdContrato"/>
			<input type="hidden" name="cTipoContrato" id="cTipoContrato" value=""/>
			<input type="hidden" name="fAplicacion" id="fAplicacion"/>
			<input type="hidden" name="cCentroContable" id="cCentroContable" value="<%=cCentroContable%>"/>
			<input type="hidden" name="cRamo" id="cRamo"/>
			<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable"/>
			<input type="hidden" name="caNoCompromiso" id="caNoCompromiso"/>
			<input type="hidden" name="nEnviadoSICOP" id="nEnviadoSICOP"/>
			<input type="hidden" name="cTipoPoliza" id="cTipoPoliza"/>
			<input type="hidden" name="nMes" id="nMes"/>
			<input type="hidden" name="aEjercicioFiscal" id="aEjercicioFiscal"/>
			<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable"/>
			<input type="hidden" name="cDescripcionPoliza" id="cDescripcionPoliza"/>
									
			<input type="hidden" name="mImporteAgregado" id="mImporteAgregado" value="0" />
			<input type="hidden" name="mImporteNeto" id="mImporteNeto" value="0" />
			<input type="hidden" name="cEjercicio" id="cEjercicio" value="0" />
			<input type="hidden" id="usuario" name="usuario" value="<%=usuario.getLogin()%>"/>
			
			<div class="card-header"> <h3> Devolucion de Compromiso Capitulo Mil </h3> </div>
			<hr class="mt-3"/>

			<div class="row d-flex">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="compromiso" class="form-label"> Compromiso: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<input type="text" id="compromiso" name="compromiso" class="form-control form-control-sm"/>
				</div>		
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
					<input type="button" id="btnBuscar" name="btnBuscar" value="Buscar" class="btn btn-secondary btn-sm"/>				
				</div>								
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
					<input type="button" id="btnNuevo" name="btnNuevo" value="Nuevo" class="btn btn-secondary btn-sm"/>
				</div>	
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
					<input type="hidden" id="btnAplicar" name="btnAplicar" value="Aplicar" onclick="motor()" class="btn btn-secondary btn-sm"/>
				</div>											
			</div>

			<div id ="tblAplicarCompromiso">
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="compromiso" class="form-label"> Compromiso: </label>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
						<input type="button" id="btnAplicar" name="btnAplicar" value="Aplicar" class="form-control form-control-sm"/>
					</div>		
				</div>
			</div>

			<br/>

			<h5> Datos Compromiso </h5>
			<hr class="mt-3">
			
			<div class="row d-flex">
				<div class="col-12 col-lg-12 col-md-1 col-sm-1 p-1" align="left">							
					<input type="button" id="pbDesmarcar" value="DesMarcar" class="btn btn-secondary btn-sm"/>
				</div>
				<div class="col-12 col-lg-12 col-md-11 col-sm-11 p-1" align="right">							
					<label for="importeTotal" class="form-label" > Total Compromiso </label>
					<div id="importeTotal" align="right" style="font-size: 12pt">0.00</div>
				</div>								
			</div>

			<div class="row d-flex">								
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
					<div class="table-responsive">	    				           
						<table id="tablaCompromiso" class="table table-striped table-bordered" >																		
							<tbody>
								<thead>
									<tr>
										<th>Numero</th>
										<th>Compromiso</th>
										<th>Seleccione</th>	
										<th>Folio Compromiso Nomina</th>										
										<th>Estructura Programatica</th>
										<th>Mes</th>
										<th>Compromiso</th>
										<th>-</th>
										<th>-</th>
										<th>-</th>	
										<th>-</th>																			
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
					<label for="importeAgregado" class="form-label" > Total Devolucion Compromiso </label>
					<div id="importeAgregado" align="right" style="font-size: 12pt">0.00</div>
				</div>								
			</div>

			<div class="row d-flex">								
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
					<div class="table-responsive">	    				           
						<table id="tablaSeleccionados" class="table table-striped table-bordered" >
							<tbody>
								<thead>
									<tr>
										<th>Numero</th>
										<th>Compromiso</th>
										<th>-</th>	
										<th>Folio Compromiso Nomina</th>
										<th>Estructura Programatica</th>
										<th>Mes</th>
										<th>Compromiso</th>
										<th>-</th>
										<th>-</th>
										<th>-</th>						
									</tr>	
								</thead>							
							</tbody>	
						</table>		
					</div>
				</div>
			</div>

			<div class="row d-flex">
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1" align="center">							
					<input type="button" id="btnProcesar" name="btnProcesar" value="Procesar" class="btn btn-secondary btn-sm"/>
				</div>								
			</div>
		
		<div id="dialog-Motivo" title="Devolucion De Compromiso">
			<h1>Desea Hacer La Devolucion De Compromiso ?</h1>
			<div id="divEspera" style="visibility: hidden" align="center">Espere por favor....
				<img border="0" src="../imagenes/espera.gif" height="30">
			</div>
		</div>
		<div id="dialog-Procesando" title="Procesando">
			<div id="divEsperaProcesando" style="visibility: hidden" align="center">Espere por favor....
				<img border="0" src="../imagenes/espera.gif" height="30">
			</div>
		</div>
		<div id="dialog-Terminado" title="Proceso Terminado">
			<div id="divEsperaTerminado" align="center"></div>
		</div>
		
		<div id="dialog-MotivoExterno" title="Devolucion De Compromiso">
			<h1>Desea Aplicar Compromiso ?</h1>
			<div id="divEspera" style="visibility: hidden" align="center">Espere por favor....
				<img border="0" src="../imagenes/espera.gif" height="30">
			</div>
		</div>
	</form>
</body>
</html>
