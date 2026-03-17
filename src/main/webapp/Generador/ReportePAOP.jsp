<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
	String uUR = usuario.getU_UR();
	
	java.util.Date utilDate = new java.util.Date();
	long lnMilisegundos = utilDate.getTime();
	java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
	String [] fp = String.valueOf(sqlDate).split("-");
	String fecha = fp[2]+"-"+fp[1]+"-"+fp[0];
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Captura PAOP</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
			@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "css/demo_table_jui.css";
			@import "css/demo_page.css";
			.normal{
				background-color: white;
			}
			.inEdit{
				background-color: #FFFF99;
			}
			.notEditable {
				background-color: #CCCCCC;
			}
			.numerico {
				text-align: right;
			}
			
			.montoCapturaEdit{
				text-align: right;
				background-color: #FFFF99;
				border: 1px solid #aaaaaa;
			}			
		</style>
		
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../jq/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
		
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		
		<script type="text/javascript" src="../admin/js/ReporteOPFormato10.js"></script>
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>


		<script type="text/javascript" charset="utf-8">

		
		$(function(){
			$("#fInicialContrato").datepicker({
			changeMonth: true, changeYear: true, showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
				buttonImageOnly : true
			});
		});		
		
		$(function(){
			$("#fFinalContrato").datepicker({
			changeMonth: true, changeYear: true, showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
				buttonImageOnly : true
			});
		});
		
				 
		 
		$(document).ready(function() {
		
		/*MLR
		*/
		queryFormPost({
				queryName : "readUltimoDiaMesActual",
				async : false,
				callback : function() {
				queryFormPost({
					queryName : "readUltimoDiaPAOP",
					async : false,
					callback : function() {
				
			/*	if ($("#tipoUsuario").val() == "ADMIN" ){
				$(function() {
					$("#fRecepcion").datepicker( {
						minDate: '01/01/2012', maxDate: "+0M +1D", changeMonth: true, changeYear: true, showOn : "button",
						dateFormat : "dd/mm/yy",
						buttonImage : "images/calendar.gif",
						buttonImageOnly : true
					});
				});
				$("#fRecepcion").removeAttr("disabled");
			}
			else {
				$("#fRecepcion").attr("disabled","disabled");
			}*/
				if($("#nPlurianual").val()== 1){
					$(function(){
							$("#fInicialContrato").datepicker({
							//changeMonth: true, changeYear: true, showOn : "button",
							changeMonth: true, changeYear: true, showOn : "button",
							dateFormat : "dd/mm/yy",
							buttonImage : "images/calendar.gif",
								buttonImageOnly : true
							});
						});		
						
						$(function(){
							//alert("Entra ffinal ..." +  $("#ultDia").val());
							$("#fFinalContrato").datepicker({
							//changeMonth: true, changeYear: true, showOn : "button",
							changeMonth: true, changeYear: true, showOn : "button",
							dateFormat : "dd/mm/yy",
							buttonImage : "images/calendar.gif",
								buttonImageOnly : true
							});
						});
				}
				else{
							$(function(){
								$("#fInicialContrato").datepicker({
								//changeMonth: true, changeYear: true, showOn : "button",
								minDate: $("#primerDiaAnio").val(), maxDate: $("#ultimoDiaAnio").val(), changeMonth: true, changeYear: true, showOn : "button",
								dateFormat : "dd/mm/yy",
								buttonImage : "images/calendar.gif",
									buttonImageOnly : true
								});
							});		
							
							$(function(){
								//alert("Entra ffinal ..." +  $("#ultDia").val());
								$("#fFinalContrato").datepicker({
								//changeMonth: true, changeYear: true, showOn : "button",
								minDate:$("#primerDiaAnio").val(), maxDate: $("#ultimoDiaAnio").val(), changeMonth: true, changeYear: true, showOn : "button",
								dateFormat : "dd/mm/yy",
								buttonImage : "images/calendar.gif",
									buttonImageOnly : true
								});
							});
						}
				   	}
				});		
	
				}
			});	
			
		
		/*MLR*/
			$("input.AyudaSyC").subIniciaDlg();
			$("input.autoCompletaSyC").subIniciaAutoCompleta();
			querySelectPost("catUnidadEjecutoraRead", "uUR", {
					async : false
				});
			querySelectPost("tCatalogoTipoArticuloRead", "cTipoProcContratacion", {
				async : false
			});
					
			$("#btnActualizarRegistro").button();
			$("#btnGuardar").button();
			$("#btnRegresar").button();
			$("#btnNuevo").button();
			$("#btnCrear").button();
			$("#btnBuscar").button();
			$("#btnAutorizar").button();
			$("#tblCaptura").hide();
			$("#modificaRegistro").hide();
			$("#GenerarReporte2").button();
			querySelectPost("tEjercicioRead", "aEjercicioFiscal", {async: false });		
			//Cambio mcf 4-dic-2013
			fn_Nuevo();
			
				
			$("#btnCrear").click(function(){ 				
				//guardarNuevoMes();
				
				
				if(confirm("Esta acción copiará y reemplazará los datos del mes "+$("#").val()+" con los del mes " + $("#mes").val() + ".\n"+"¿Está seguro de realizar la copia?")){
					existeCucopMes();
				}
				});
			$("#btnGuardar").click(function(){ 
				if(validCaptura()){
					validacCVE_CUPOP();			
				}				
			});
			$("#btnActualizarRegistro").hide();
//IRD 20140811 Se habilita botón de autorizar sólo si hay datos capturados
			$("#btnAutorizar").hide();
			$("#btnActualizarRegistro").click(function(){				
				if(validCaptura()){
					actualizaRegistro();
				}				
			});
			$("#btnBuscar").click(function(){ muestraTabla("");});
			$("#btnAutorizar").click(function(){
				queryFormPost("tPROGRAMAANUALOBRAPUBLICASaldosNegativosRead", {async:false});
				if ($("#txtcantidadnegativos").val() > 0 ){
					muestraTabla("EP in (select EP from tPROGRAMAANUALOBRAPUBLICASaldosNegativos) and ");
					var oTableM = $('#tblPAOP').dataTable();
					var aData = oTableM.fnGetData();
					var nRows = aData.length + 1;
					alert("Existen Registros con saldo negativo. Se muestran en la tabla inferior");
				}
				else{
					leeSaldo();
					
/*					queryFormPost("tPROGRAMAANUALOBRAPUBLICASaldosAurotizadoRead", {async:false});
					if ($("#txtnumautorizados").val() > 0){
						alert("El mes ya se fué autorizado con anterioridad.");
						return;
					}*/
					//queryFormPost("tPROGRAMAANUALOBRAPUBLICASaldosRead", {async:false});
					var mensaje ="Unidad Ejecutora: "+$("#uUR").val()+"\n"
					mensaje +="Mes: "+$("#mesNuevo").val()+"\n"
	    			if ($("#Cartera").val() != ""){
	    				mensaje = mensaje + "Cartera = '" + $("#Cartera").val() + "'\n";
				    }
				    if ($("#Partida").val() != ""){
				    	mensaje = mensaje + " Partida = '" + $("#Partida").val() + "'\n";
				    }
				    if ($("#ProgramaPresupuestario").val() != ""){
				    	mensaje = mensaje + "ProgramaPresupuestario = '" + $("#ProgramaPresupuestario").val() + "'\n";
				    }
				   
				    if($("#txtsaldo").val() != 0 ) {
						alert("Existen Registros con saldo.");
					}
				    else{ 
					    var creaFallo = 1;
						if(confirm("¿Está seguro de guardar los cambios de\n" + mensaje + "?\nYa no podrá realizar cambios.")){
							queryFormPost("tPROGRAMAANUALOBRAPUBLICACreate",{async: false,
								callback : function() {
									creaFallo = 0;
									alert("Autorización exitosa");
								}
					 		});
						}
						if (creaFallo){
							alert("No se pudo autorizar");
						}
				   } // else
				}
				
			});
			$("#btnRegresar").click(function(){ 
			    mostrarCaptura(); 
				$("#tblCaptura").hide(); 
				oTable.fnClearTable();			
			});

var oTable;
/*			var oTable = $("#tblPAOP").dataTable({
					//iDisplayLength: 20,
					bSortClasses: false,
					ScrollY: "800px",
					sScrollX: "1300px",
					bPaginate: false,
		        	bLengthChange: false,
		        	bFilter: false,
		        	bSort: true,
		        	bInfo: false,
		        	bAutoWidth: false,
					bJQueryUI: true,
					bRetrive : true,
					bDestroy : true,
					bRetrieve: true,
					sPaginationType: "full_numbers",
					bScrollCollapse: true,
					//sScrollXInner: "100%",
					aaSorting: [[ 1, "asc" ]] ,
					bRetrive: true,
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
						}
						
				});*/
			
			//muestraTabla('1= 2 and ');
			leeSaldo();
			//queryFormPost("tPROGRAMAANUALOBRAPUBLICASaldosRead", {async:false});
				
			$("#tblPAOP tbody").click(function(event) {
				$("#tblCaptura").show();
				$("#btnGuardar").hide();
				$("#btnActualizarRegistro").show();
			    oTable = $('#tblPAOP').dataTable();
				var aPos = oTable.fnGetPosition( event.target.parentNode );
	     		var aData = oTable.fnGetData( aPos );
	     		$("#nRenglon").val(aData[2]);
				$("#cCVE_CUCOP").val(aData[3]);
				$("#Cucop").val(aData[3]);
				$("#cCONCEPTO").val(aData[4]);
				$("#mMultianualEstimado").val(aData[5]);
				$("#mEstimadoMipymes").val(aData[6]);
				$("#mEstimadoNoCubiertasTLC").val(aData[7]);
				$("#nCantidad").val(aData[8]);
				$("#nUnidadMedida").val(aData[9]);
				$("#txtGridcIdClave").val(aData[9]);
				$("#cTipoProcContratacion").val(aData[10]);
				$("#nEntidadFederativa").val(aData[11]);
				$("#nTrimestre1").val(aData[12]);
				$("#nTrimestre2").val(aData[13]);
				$("#nTrimestre3").val(aData[14]);
				$("#nTrimestre4").val(aData[15]);
				$("#fInicialContrato").val(aData[16]);
				$("#nPlurianual").val(aData[17]);
				$("#nEjerciciosFicales").val(aData[18]);
				$("#mAnualEjercer").val(aData[19]);
				$("#cComentario1").val(aData[20]);
				$("#fFinalContrato").val(aData[21]);
				$("#cComentario3").val(aData[22]);
				$("#cTipoProcedimiento").val(aData[23]);
				$("#btnActualizarRegistro").show();
				querySelectPost("EstadosRead", "nEntidadFederativa", {async: false});	
				if($("#nPlurianual").val()== 1)
				$("#checkPlurianual").attr("checked", true)

				$("#ep").val(aData[24]);
				$("#Cartera").val(aData[25]);
				$("#Partida").val(aData[26]);
				$("#ProgramaPresupuestario").val(aData[27]);

				$("#hCarteraOP").val(aData[25]);
				$("#hPartidaOP").val(aData[26]);
				$("#hProgramaPresupuestarioOP").val(aData[27]);
				
				$("#noContrato").val(aData[28]);
				$("#OLIAutorizado").val(aData[29]);
				$("#valorTotal").val(aData[30]);
                $("#mMultianualEstimado").focus();
                
                $("#mMultianualEstimado").formatCurrency();
                $("#mEstimadoMipymes").formatCurrency();
                $("#mEstimadoNoCubiertasTLC").formatCurrency();
                $("#mAnualEjercer").formatCurrency();
                
                $("#valorTotal").formatCurrency();
                
                 $("#nEntidadFederativa").val(aData[24].substr(29,2));
                 //alert ( $("#nEntidadFederativa").val());
			});
						
			$('#btnNuevo').click( function fn_Nuevo() { 
				
					mostrarCaptura();
					$("#tblCaptura").show();
				    $("#divtblPAOP").hide();
					querySelectPost("EstadosRead", "nEntidadFederativa", {async: false});
					queryFormPost("EjercicioFiscalRead", {async : false});
					$("#btnActualizarRegistro").hide();
					
					$("#btnGuardar").show();
					$("#btnActualizarRegistro").hide();
			});
			querySelectPost("MesesPAOP1", "mes", {async: false});	
			querySelectPost("MesesPAOP2", "mesNuevo", {async: false});	
			if ($("#uUR_ori").val() == "A02") {
				$("#btnCrear").css("visibility", "visible");
				//$("#btnCrear").style.visibility="hidden";
			} else{
				$("#btnCrear").hide();
			}
			
				$("#txtmodificado").formatCurrency();
				$("#manualejercer").formatCurrency();
				$("#txtsaldo").formatCurrency();
				
				$("#valorTotal").formatCurrency();
				
				$('#dlgJustificaciones').dialog({height:500, width:1200}).dialog('close');

			
	});
	
	function leeSaldo() {
		queryFormPost("tPROGRAMAANUALOBRAPUBLICASaldosRead", {async:false});
		if ($("#manualejercer").val() > 0 && $("#txtsaldo").val() >= 0){
			$("#btnAutorizar").show();
		}
	}
		
		
	function guardarNuevoMes(){
		
		var mesAnterior = $("#mes").val();
		var mesNuevo = $("#mesNuevo").val();
		var year = $("#aEjercicioFiscal").val();
			
		//alert(mesAnterior);
		//alert(mesNuevo);
		
		$.ajax({
					url: './reportePAOPValidar.jsp',
					type: 'post',
					data:{tipo:'guardarNuevoMesPAOP',mesAnterior:mesAnterior, mesNuevo:mesNuevo,year:year},
					success:function(data){
						
							if(data.status == "correcto"){
								muestraTabla();	
							}else{
								alert("No Se Guardo Correctamente");
							}
					}	
			 });	
	}

	function muestraTabla(esInicio){
	    var strWhere = "&qw=" + esInicio + "cStatus= 1 and nMes = " + $("#mesNuevo").val() 
	    if ($("#uUR").val() != "" && $("#uUR").val() != "000"){
            strWhere +=  " and cUnidadEjecutora='" + $("#uUR").val() +"'";
           }
	    if ($("#Cartera").val() != ""){
	    	strWhere = strWhere + " and cCartera = '" + $("#Cartera").val() + "'";
	    }
	    if ($("#Partida").val() != ""){
	    	strWhere = strWhere + " and cPartida = '" + $("#Partida").val() + "'";
	    }
	    if ($("#ProgramaPresupuestario").val() != ""){
	    	strWhere = strWhere + " and cProgramaPresupuestario = '" + $("#ProgramaPresupuestario").val() + "'";
	    }
	   // var strWhere = "&qw=cStatus= 1 and nMes = 3 and cUnidadEjecutora='" + $("#uUR").val() +"'";
	   // $("#tblPAOP").dataTable().fnClearTable();
		$("#divtblPAOP").show();
	var oTable = $('#tblPAOP').dataTable();
	oTable.fnClearTable();
	$('#tblPAOP').dataTable({         
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vPROGRAMAANUALOBRAPUBLICA"+strWhere,
							sServerMethod: "POST",
							bProcessing: true,
							bJQueryUI: true,
							bAutoWidth : true,
							bRetrive: true,
							bDestroy: true,
        					bPaginate: false,
							iDisplayLength: 10,
		        			sScrollY: "450px", 
		        			sScrollX: "1200px",
		        			Height: "450px", 
		        			Width: "1050px", 
				aoColumns: [
					{ sName: "cEjercicioFiscal" },
					{ sName: "nMes" },
					{ sName: "nRenglon" },
					{ sName: "cCVE_CUCOP" },
					{ sName: "cCONCEPTO" },
					{ sName: "mMultianualEstimado" },
					{ sName: "mEstimadoMipymes" },
					{ sName: "mEstimadoNoCubiertasTLC" },
					{ sName: "nCantidad" },
					{ sName: "nUnidadMedida" },
					{ sName: "cTipoProcContratacion" },
					{ sName: "nEntidadFederativa" },
					{ sName: "nTrimestre1" },
					{ sName: "nTrimestre2" },
					{ sName: "nTrimestre3" },
					{ sName: "nTrimestre4" },
					{ sName: "fInicioContrato" },
					{ sName: "nPlurianual" },
					{ sName: "nEjerciciosFicales" },
					{ sName: "mAnualEjercer" },
					{ sName: "cComentario1" },
					{ sName: "fFinalContrato" },
					{ sName: "cComentario3" },
					{ sName: "cTipoProcedimiento" },
					{ sName: "EP" },
					{ sName: "cCartera" },
					{ sName: "cPartida" },
					{ sName: "cProgramaPresupuestario" },
					{ sName: "noContrato" },
					{ sName: "oliAutorizado" },
					{ sName: "valorTotalObra" },
					{ sName: "borrar" }
				]
        	});
/*
		//var vvvv = $('#tblPAOP').dataTable(0);
		if (typeof oTable != 'undefined') {
			$('#tblPAOP').dataTable().fnDraw();
			oTable = $('#tblPAOP').dataTable();
    		oTable.fnClearTable();
			oTable.fnReloadAjax(window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vPROGRAMAANUALOBRAPUBLICA"+strWhere);
     
		} else {
		//$('#tblPAOP').dataTable().fnClearTable();
				oTable = $("#tblPAOP").dataTable({
				bAutoWidth : false,
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
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vPROGRAMAANUALOBRAPUBLICA"+strWhere,
					bProcessing: true,
					bJQueryUI: true,
					bAutoWidth : true,
					bRetrive: true,
					bDestroy: true,
		    		bPaginate: false,
					iDisplayLength: 10,
		      			sScrollY: "450px", 
		      			sScrollX: "1200px",
		      			Height: "450px",
				aoColumns: [
					{ sName: "cEjercicioFiscal" },
					{ sName: "nMes" },
					{ sName: "nRenglon" },
					{ sName: "cCVE_CUCOP" },
					{ sName: "cCONCEPTO" },
					{ sName: "mMultianualEstimado" },
					{ sName: "mEstimadoMipymes" },
					{ sName: "mEstimadoNoCubiertasTLC" },
					{ sName: "nCantidad" },
					{ sName: "nUnidadMedida" },
					{ sName: "cTipoProcContratacion" },
					{ sName: "nEntidadFederativa" },
					{ sName: "nTrimestre1" },
					{ sName: "nTrimestre2" },
					{ sName: "nTrimestre3" },
					{ sName: "nTrimestre4" },
					{ sName: "fInicioContrato" },
					{ sName: "nPlurianual" },
					{ sName: "nEjerciciosFicales" },
					{ sName: "mAnualEjercer" },
					{ sName: "cComentario1" },
					{ sName: "fFinalContrato" },
					{ sName: "cComentario3" },
					{ sName: "cTipoProcedimiento" },
					{ sName: "EP" },
					{ sName: "cCartera" },
					{ sName: "cPartida" },
					{ sName: "cProgramaPresupuestario" },
					{ sName: "noContrato" },
					{ sName: "oliAutorizado" },
					{ sName: "valorTotalObra" }
				]
        	});
        	oTable.fnClearTable();
			}*/
		leeSaldo();
		//queryFormPost("tPROGRAMAANUALOBRAPUBLICASaldosRead", {async:false});
		
		$("#txtmodificado").formatCurrency();
				$("#manualejercer").formatCurrency();
				$("#txtsaldo").formatCurrency();
				
				$("#valorTotal").formatCurrency();
	}
	function muestraTabla2(){
		$("#divtblPAOP").show();
		$('#tblPAOP').dataTable().fnClearTable();
		var datos = "sinDatos";
		var szTabla = "tProgramaAnualObraPublica";
		var elParametro = "";
		var mes = $("#mesNuevo").val();
		var year = $("#aEjercicioFiscal").val();
		var camposCondicion = " WHERE cEjercicioFiscal = "+year+" AND nMes = "+mes;
		
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:camposCondicion, Param: elParametro, MaxReg: "5", ajax: 'true'}, function(j){
				
					for (var i = 0; i < j.length; i++) {
												
							datos = "datos";
							aoColumns: [
								   { sName: j[i].Col0,	bSearchable: true,	bSortable: false, bVisible: false},
								   { sName: j[i].Col1,	bSearchable: false,	bSortable: false, bVisible: true},
								   { sName: j[i].Col2,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col3,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col4,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col5,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col6,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col7,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col8,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col9,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col10,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col11,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col12,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col13,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col14,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col15,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col16,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col17,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col18,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col19,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col20,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col21,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col22,	bSearchable: false,	bSortable: false, bVisible: false},
								   { sName: j[i].Col23,	bSearchable: false,	bSortable: false, bVisible: false}
							]
								
								
																
								$("#tblPAOP").dataTable().fnAddData( [j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, j[i].Col5, j[i].Col6, j[i].Col7, j[i].Col8 , j[i].Col9, j[i].Col10, j[i].Col11, j[i].Col12, j[i].Col13, j[i].Col14 , j[i].Col15, j[i].Col16, j[i].Col17, j[i].Col18, j[i].Col19, j[i].Col20, j[i].Col21, j[i].Col22, j[i].Col23 ] );
								var cveCucop = j[i].Col3;
								$("#cCVE_CUCOPMes").val(cveCucop);
								//alert(cveCucop);
					}							
					if(datos == "sinDatos"){
						alert(" No se encontraron Registros.");
						$('#tblPAOP').dataTable().fnClearTable();
						$("#divtblPAOP").hide();
					}
			});

	}
	function abreGridEP()
	{
		window.open('MultiReporteGrid.jsp?id=<%=request.getParameter("id")%>&cCartera='+$("#Cartera").val()+'&cPartida='+$("#Partida").val()+'&cPrograma='+$("#ProgramaPresupuestario").val()+'&esPAOP=1&nEntidadFederativa=1', 'MultiReporteGrid', 'status=1, width=900px, height=600px');
/*	if ($.trim(document.getElementById("ep").value)!=""){
			//window.alert($.trim(document.getElementById("ep").value));
			rellenaCampos();
		}
	*/
		//window.location.href="MultiReporteGrid.jsp?id=<%=request.getParameter("id")%>";
	 	return false;
	}
		function guardarRegistro(){
			//Guarda el valor del checkbox en la caja de texto
			var plurianualStr = ($("#checkPlurianual").attr("checked")?1:0);				
			$("#nPlurianual").val(plurianualStr);		
			
			var datos = "sinDatos";
			var szTabla = "tProgramaAnualObraPublicaMaximo";
			var mes = $("#mesNuevo").val();
			var year = $("#aEjercicioFiscal").val();
			var camposCondicion = " WHERE cEjercicioFiscal = "+year+" AND nMes = "+mes;
			var elParametro = "";
			var max = 0;
			
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:camposCondicion, Param: elParametro, MaxReg: "5", ajax: 'true'}, function(j){
				
					for (var i = 0; i < j.length; i++) {
												
							datos = "datos";
							max = j[i].Col0;
							if(max > 1){
								$("#nRenglon").val(j[i].Col0);
							}else{
								$("#nRenglon").val(1);
							}
					}				
			});
			
			$("#cEjercicioFiscal").val($("#aEjercicioFiscal").val());
			$("#nMes").val($("#mesNuevo").val());
			
			setTimeout('queryFormPost("tCreateReportePAOP",{async: false })', 4000);
			alert("Guardado Correctamente");
			$('#tblPAOP').dataTable().fnClearTable();
			$("#tblCaptura").hide();	
		}
		
		function actualizaRegistro(){
			//Guarda el valor del checkbox en la caja de texto
			var plurianualStr = ($("#checkPlurianual").attr("checked")?1:0);				
			$("#nPlurianual").val(plurianualStr);
			
			queryFormPost("tProgramaAnualObraPublicaUpdate", {async:false});
			mostrarCaptura();
			$("#tblCaptura").hide();
			$('#tblPAOP').dataTable().fnClearTable();
			muestraTabla('');
			
		}
		
		function mostrarCaptura(){
		   // alert("mostrarCatura");
			$("#nRenglon").val("");
			$("#cCVE_CUCOP").val("");
			$("#Cucop").val("");
			$("#cCONCEPTO").val("");
			$("#mMultianualEstimado").val("");
			$("#mEstimadoMipymes").val("");
			$("#mEstimadoNoCubiertasTLC").val("");
			$("#nCantidad").val("");
			$("#nUnidadMedida").val("");
			$("#txtGridcIdClave").val("");			
			$("#cTipoProcContratacion").val("");
			$("#nEntidadFederativa").val("");
			$("#nTrimestre1").val("");
			$("#nTrimestre2").val("");
			$("#nTrimestre3").val("");
			$("#nTrimestre4").val("");
			$("#fInicialContrato").val("");
			$("#nPlurianual").val(0);
			$("#checkPlurianual").attr("checked",false)
			$("#nEjerciciosFicales").val("");
			$("#mAnualEjercer").val("");
			$("#cComentario1").val("");
			$("#fFinalContrato").val("");
			$("#cComentario3").val("");
			$("#cTipoProcedimiento").val("");

			$("#hCarteraOP").val("");
			$("#Cartera").val("");
			$("#hPartidaOP").val("");
			$("#Partida").val("");
			$("#hProgramaPresupuestarioOP").val("");
			$("#ProgramaPresupuestario").val("");
			$("#ep").val("");
			
		}
		
		function validCaptura(){
			$("#cCVE_CUCOP").val($("#Cucop").val());
			$("#nUnidadMedida").val($("#txtGridcIdClave").val());
			
			if($("#EP").val() == ''){
				alert("Por favor capture la EP");
				return false;
			}
			if($("#cCVE_CUCOP").val() == ''){
				alert("Por favor capture la Clave Cucop");
				return false;
			}
			else if($("#mMultianualEstimado").val() == ''){
				alert("Por favor capture el Valor Total Multianual");
				return false;
			} 
			else if($("#mEstimadoMipymes").val() == ''){
				alert("Por favor capture el Valor Estimado Compras");
				return false;
			}
			else if($("#mEstimadoNoCubiertasTLC").val() == ''){
				alert("Por favor capture el Valor Estimado Compras TLC");
				return false;
			}
			else if($("#nCantidad").val() == ''){
				alert("Por favor capture la Cantidad");
				return false;
			}
			else if($("#nUnidadMedida").val() == ''){
				alert("Por favor capture la Unidad de Medida");
				return false;
			}
			else if($("#nTrimestre1").val == ''){
				alert("Por favor capture el Trimestre 1");
				return false;
			}
			else if($("#nTrimestre2").val() == ''){
				alert("Por favor capture el Trimestre 2");
				return false;
			}
			else if($("#nTrimestre3").val() == ''){
				alert("Por favor capture el Trimestre 3");
				return false;
			}
			else if($("#nTrimestre4").val() == ''){
				alert("Por favor capture el Trimestre 4");			
				return false;
			}
			else if($("#mAnualEjercer").val() == ''){
				alert("Por favor capture el Monto Anual a Ejercer");
				return false;
			}
			
			else if($("#fInicialContrato").val() == ''){
				alert("Por favor capture la Fecha Inicial del Contrato");
				return false;
			}
			else if($("#fFinalContrato").val() == ''){
				alert("Por favor capture la Fecha Final del Contrato");
				return false;
			}
			
			else if($("#cTipoProcedimiento").val() == ''){
				alert("Por favor capture el Tipo de Procedimiento");
				return false;
			}
			else if($("#cTipoProcContratacion").val() == ''){
				alert("Por favor capture el Caracter del Procedimiento");
				return false;
			}
			else if($("#cCONCEPTO").val() == ''){
				alert("Por favor capture el Concepto");
				return false;
			}
			else if(parseFloat($("#nTrimestre1").val()) + parseFloat($("#nTrimestre2").val())+  parseFloat($("#nTrimestre3").val())+ parseFloat($("#nTrimestre4").val()) != 100 ){
				alert("La suma de porcentajes trimestrales debe ser 100");
				return false;
			}
			return true;
		}
		
		function borrarRegistro(){
			$('#tblPAOP tr td ').click(function () {
			var oTable = $('#tblPAOP').dataTable();
				var aPos = oTable.fnGetPosition(this);
				var aData = oTable.fnGetData( aPos[0] );
				$("#nRenglon").val(aData[2]);
				$("#aEjercicioFiscal").val(aData[0]);
				//$("#aEjercicioFiscal").val(aData[0]);
				
				queryFormPost("tProgramaAnualObraPublicaDelete", {async: false,
					callback : function() {
						oTable.fnDeleteRow(aPos);
						muestraTabla('');
					}
				 });								
     		});
		}
		
		//Valida si la clave CUCOP ya fue agregada
		function validaAgregaRegistro(clave){
				return false;
			}
		
		function validacCVE_CUPOP(){			
			var optSel = $("#cCVE_CUCOP").val();
			var optTxt = $("#cCVE_CUCOP").val();			
			
			var existe = validaAgregaRegistro(optSel);
				if(existe){					
					alert("La Clave Cucop " + optTxt + " ya fue agregada");	
					return true;
					}
				else{
					//queryFormPost("tPROGRAMAANUALOBRAPUBLICAExisteEPCUCOPRead", {async:false});
					guardarRegistro();
					setTimeout("muestraTabla('')", 3000);
				}
			}
		
		///var cveCucop = j[i].Col3;
			
		//Valida si la clave CUCOP ya existe en el mes
		function existeCucopMes(){			
			var oTable = $('#tblPAOP').dataTable();
			var sData = oTable.fnGetData();	
			
			var optSel = $("#mesNuevo").val();
			var optTxt = $("#mesNuevo option[value='" + optSel + "']").text();
			var mesNuevo = $("#mesNuevo option[value='" + optSel + "']").val();
			if ($("#mes").val() == $("#mesNuevo").val() ) {
				alert("El mes Nuevo debe ser diferente al mes Anterior");
				return;			
			}
			
			/*if(sData == "")
				alert("Busque los registros a crear");
				else{ */
					$("#nMes").val(mesNuevo);
					queryFormPost("existeCucopRead", {async: false });								
					var folioCucop = $("#existeRegistro").val();				
					if (folioCucop > 0)	{				
						alert("La clave cucop ya existe en el mes de " + optTxt);			
						$("#existeRegistro").val(0);
						}
					else{
						guardarNuevoMes();						
					}
					
				//}			
				
			} 
		//$("#txtmodificado").addClass("notEditable");
		
		
		/*
			else{				
				queryFormPost("existeCucopRead", {async : false});
				var existeReg = $("#existeRegistro").val();
				if( existeReg > 0 )
					alert("El registro ya existe");
					else{
						guardarNuevoMes();
					}
				
			}		
			*/
		function muestraJ(){
		    $("#cCONCEPTOdlg").val($("#cCONCEPTO").val());
			$("#cComentario1dlg").val($("#cComentario1").val());
			$("#cComentario3dlg").val($("#cComentario3").val());
			$('#dlgJustificaciones').dialog({height:500, width:1200,
									buttons : {
							"Aceptar" : function() {
							    $("#cCONCEPTO").val($("#cCONCEPTOdlg").val());
								$("#cComentario1").val($("#cComentario1dlg").val());
								$("#cComentario3").val($("#cComentario3dlg").val());
								$(this).dialog("close");
							},
							Cancel : function() {
								$(this).dialog("close");
							}
						},
						close : function() {
								$(this).dialog("close");
						}}).dialog('open');
			parent.document.getElementById("pb_send").disabled=true;
			parent.document.getElementById("pb_save").disabled=true;
			
			
		}
		
		function fnPrintTituloReporte2() {
			//$("#FormContrato").action = "../ObraPublica/reportes?rt=EXPORTA_PAOP&uUR=B03";
			//$("#FormContrato").submit();
			var url = "../ObraPublica/reportes?rt=EXPORTA_PAOP&uUR=" + $("#uUR_ori").val()+"&mesNuevo=" +$("#mesNuevo").val()+ "";
			var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
			
/*					$.ajax({
					url: '../ObraPublica/reportes',
					type: 'post',
					data:{rt:'EXPORTA_PAOP',uUR:'B03'},
					success:function(data){
							if(data.status == "correcto"){
								alert('correctisisimo');	
							}else{
								alert("No Se Guardo Correctamente");
							}
					}	
			 });	*/
			
		}
		 
		</script>
</head>
<body id="dt_example" >
	<form id="FormContrato" name="FormContrato" method="post" action="../ObraPublica/reportes?rt=EXPORTA_PAOP&uUR=<%=uUR%>" target="_blank"> 
		<div id="container" class="container">	
		<input type="hidden" name="nRenglon" id="nRenglon" value="1" />
		<input type="hidden" name="cEjercicioFiscal" id="cEjercicioFiscal" />
		<input type="hidden" name="nMes" id="nMes" />
		<input type="hidden" name="cStatus" id="cStatus" value="1" />
		<input type="hidden" id="nPlurianual" name="nPlurianual"/>
		<input type="hidden"   id="existeRegistro" name="existeRegistro" value="" />
		<input type="hidden"  id="cCVE_CUCOPMes" value="">
		
		<input type="hidden"  id="cCVE_CUCOP" name="cCVE_CUCOP" value="">
		<input type="hidden"  id="nUnidadMedida" name="nUnidadMedida" value="">
		<input type="hidden"  id="uUR_ori" name="uUR_ori" value="<%=uUR%>">
		<input type="hidden" id="cDocumento" value="CONTRATOOBRA" name="cDocumento" size="20" />
		<input type="hidden"  id="txtcantidadnegativos" name="txtcantidadnegativos" value="">
		
		<input type="hidden" id="ultDia" value="" name="ultDia" size="20" />
		<input type="hidden" id="primerDiaAnio" value="" name="primerDiaAnio" size="20" />
		<input type="hidden" id="ultimoDiaAnio" value="" name="ultimoDiaAnio" size="20" />

		<input type="hidden" id="txtnumautorizados" value="" name="ultimoDiaAnio" size="20" />
		<input type="hidden" id="rt" value="" name="rt" size="20" value="EXPORTA_PAOP"/>

		
		<select id="uUR" name="uUR"></select>
		 
			<h1>Captura PAOP</h1>					
			<table align="center" width="800">
				<tr>
					<td>
						<fieldset>
							<legend>Seleccione</legend>
						<table width="100%">
							<tr>
								<td align="right">Seleccionar Mes Anterior:</td>
								<td>
									<select name="mes" id="mes">						
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
								</td>
								<td align="right">Seleccionar Mes Nuevo:</td>
								<td>
									<select name="mesNuevo" id="mesNuevo">
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
								</td>			
								<td align="right" style="visibility:hidden">Año:</td><td style="visibility:hidden"><select name="aEjercicioFiscal" id="aEjercicioFiscal"></select></td>								
							</tr>
							<tr>
								<td colspan="6" align="center">
									<input type="button" name="btnNuevo" id="btnNuevo" value="Nuevo" title="Borra los datos y se prepara para la captura de un nuevo registro"/>				
									<input type="button" name="btnCrear" id="btnCrear" value="Crear"  title="Copia todos los registros existentes en 'Mes Anterior' a 'Mes Nuevo' con la finalidad de facilitar la captura en el nuevo mes."/>
									<input type="button" name="btnBuscar" id="btnBuscar" value="Buscar" title="Busca los registros de la Unidad Ejecutora del usuario y el dato del 'mes nuevo' y si se tienen datos capturados también utiliza partida, cartera, programa y ep como filtro."/>
									
								
									<input type="button" name="btnActualizarRegistro" id="btnActualizarRegistro" value="Actualizar" title="Guarda la informacion del registro modificado."/>
									<input type="button" name="btnGuardar" id="btnGuardar" value="Guardar" title="Permite guardar el registro en cuestión."/>								
									<input type="button" name="btnRegresar" id="btnRegresar" value="Regresar" title="Devolver a la accion anterior."/>
									
									
								</td>
							</tr>
							<tr>
								<td colspan="6" align="center"> 
									<input type="button" name="btnAutorizar" id="btnAutorizar" value="Autorizar" title="Envía los datos de la Unidad Ejecutora y del dato de 'mes nuevo' y si se tienen datos capturados también utiliza partida, cartera, programa y ep como filtro al estatus 2 para que se visualicen en el reporte PAOP"/>
									<input type="button" name="GenerarReporte2" id="GenerarReporte2" value="Generar Reporte" onClick="return fnPrintTituloReporte2();" title="Genera la integración de registros autorizados en archivo Excel."/>
								</td>
							</tr>
						</table>
						</fieldset>
					</td>
				</tr>	
			</table>						
			<table id="tblCaptura" width="800">
									
				<tr>
					<td>									
						<fieldset> 
							<legend>Captura</legend>
							<table width="100%">
				<tr>
					<td align="left">Modificado:</td>
					<td align="left" >Monto PAOP:</td>
					<td align="left">Saldo:</td>
				</tr>
				<tr>
					<td><input type="text"  id="txtmodificado" name="txtmodificado" readonly="readonly" class="notEditable numerico" ></td>
					<td><input type="text"  id="manualejercer" name="manualejercer" readonly="readonly" class="notEditable numerico"></td>
					<td><input type="text"  id="txtsaldo" name="txtsaldo" readonly="readonly" class="notEditable numerico"></td>
				</tr>
				<tr >
					<td align="left ">Programa Presupuestario:</td>
					<td>&nbsp;</td>
					<td  align="left">&nbsp;&nbsp;Partida:</td>
					<td  align="left">&nbsp;&nbsp;Cartera:</td>
				</tr>
				<tr >
					<td colspan="2"><input id="hProgramaPresupuestarioOP" name="hProgramaPresupuestarioOP" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC" onblur ="leeSaldo();"/>
					<input id="ProgramaPresupuestario" name="ProgramaPresupuestario" type="hidden"   value="" size="5" maxlength="5" class=""/></td>
					
					<td><input id="hPartidaOP" name="hPartidaOP" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC" onblur ="leeSaldo();"/>
					<input id="Partida" name="Partida" type="hidden"   value="" size="5" maxlength="5" class=""/></td>
					
					<td><input id="hCarteraOP" name="hCarteraOP" type="text"   value="" size="40" maxlength="50" class="AyudaSyC  autoCompletaSyC" onblur ="leeSaldo();"/>
					<input id="Cartera" name="Cartera" type="hidden"   value="" size="5" maxlength="5" class=""/></td>
<!--				<td><input id="Partida"  name="Partida"  value="" size="5"  maxlength="7"/></td>-->
				</tr>
				<tr >
					<td colspan="2" nowrap>
					<input id="ep" name="ep" type="text"   value="" size="64" maxlength="64" class="" onblur ="leeSaldo();" onchange ="leeSaldo();"/>
					<input type="button" value="..." onclick="abreGridEP()"  />
					</td>
				</tr>
				<tr >


									<td>Clave Cucop:</td>									
									<td>Valor Total Multianual:</td>									
									<td nowrap="nowrap">Valor Estimado Compras:</td>
									<td nowrap="nowrap">Valor Estimado Compras TLC:</td>									
								</tr>
								<tr>
									<td>
										<!-- cCVE_CUCOP -->
										<input type="text" name="Cucop" id="Cucop" size="10" maxlength="10" 
										 onkeypress="Validaciones(this,15)" class="AyudaSyC autoCompletaSyC">
										
									</td>
									<td>
										<input type="text" name="mMultianualEstimado" id="mMultianualEstimado" maxlength="20" onkeypress="Validaciones(this,9)">
									</td>
									<td>
										<input type="text" name="mEstimadoMipymes" id="mEstimadoMipymes" maxlength="20" onkeypress="Validaciones(this,9)">
									</td>
									<td>
										<input type="text" name="mEstimadoNoCubiertasTLC" id="mEstimadoNoCubiertasTLC" maxlength="20" onkeypress="Validaciones(this,9)">
									</td>
								</tr>								
								<tr>																	
									<td>Cantidad:</td>									
									<td>Unidad de Medida:</td>										
									<td colspan="2">Entidad Federativa:</td>							
								</tr>
								<tr>									
									<td>
										<input type="text" name="nCantidad" id="nCantidad" maxlength="20" onkeypress="Validaciones(this,2)">
									</td>
									<td>
										<!-- nUnidadMedida -->
										<input type="text" name="txtGridcIdClave" id="txtGridcIdClave" maxlength="20" onkeypress="Validaciones(this,2)" 
										class="AyudaSyC autoCompletaSyC">
									</td>									
									<td colspan="2">							
										<select id="nEntidadFederativa" name="nEntidadFederativa"></select>
									</td>
								</tr>															
								<tr>	
									<td>Trimestre 1:</td>									
									<td>Trimestre 2:</td>
									<td>Trimestre 3:</td>
									<td>Trimestre 4:</td>									
								</tr>
								<tr>
									<td>
										<input type="text" name="nTrimestre1" id="nTrimestre1" maxlength="20" onkeypress="Validaciones(this,9)">
									</td>
									<td>
										<input type="text" name="nTrimestre2" id="nTrimestre2" maxlength="20" onkeypress="Validaciones(this,9)">
									</td>
									<td>
										<input type="text" name="nTrimestre3" id="nTrimestre3" maxlength="20" onkeypress="Validaciones(this,9)">
									</td>
									<td>
										<input type="text" name="nTrimestre4" id="nTrimestre4" onkeypress="Validaciones(this,9)">
									</td>
								</tr>
								<tr>									
									<td>Monto Anual a Ejercer:</td>																		
									<td>Fecha Inicial Contrato:</td>									
									<td>Fecha Final Contrato:</td>
									<td>Ejercicio Fiscal:</td>																		
								</tr>
								<tr>									
									<td>
										<input type="text" name="mAnualEjercer" id="mAnualEjercer" onkeypress="Validaciones(this,9)" maxlength="20">
									</td>								
									<td>
										<input type="text" name="fInicialContrato" id="fInicialContrato" readonly="readonly" maxlength="10" size="10">
									</td>
									<td>
										<input type="text" name="fFinalContrato" id="fFinalContrato" readonly="readonly" maxlength="10" size="10">
									</td>
									<td>
										<input type="text" name="nEjerciciosFicales" id="nEjerciciosFicales" readonly="readonly" size="4">
									</td>
								</tr>
								<tr>						
									<td>Caracter del Procedimiento:</td>
									<td>Tipo de Procedimiento:</td>
									<td colspan="2">&nbsp;&nbsp;Plurianual									
										<input type="checkbox" id="checkPlurianual" name="checkPlurianual" >
									</td>
								</tr>
								<tr>									
									<td>
										<!--  <input type="text" name="cTipoProcedimiento" id="cTipoProcedimiento" size="5" maxlength="5" onkeypress="Validaciones(this,2)">-->
										<select name="cTipoProcedimiento" id="cTipoProcedimiento">						
										<option value="N">Nacional</option>
										<option value="I">Internacional</option>
									</select>
									</td>									  
									<td colspan ="3">
										<input type="hidden" name="cTipoProcContratacion_ori" id="cTipoProcContratacion_ori" maxlength="1" size="1" onkeypress="Validaciones(this,2)">
																						<select id="cTipoProcContratacion" name="cTipoProcContratacion"
													style="width: 275px;">
													<option>A</option>
													<option>B</option>
													<option>C</option>
												</select>
									</td>								
									
								</tr>																
								<tr>
									<td>
										Contratos:
									</td>									
									<td>
										OLI Autorizado:
									</td>									
									<td colspan="2">											
										Valor Total de la obra:
									</td>								
								</tr>
								<tr>
									<td>
										<textarea id="noContrato" name="noContrato" rows="" cols="" onkeypress="Validaciones(this,17)"></textarea>
									</td>
									<td>
										<input type="text" name="OLIAutorizado" id="OLIAutorizado" onkeypress="Validaciones(this,17)"  maxlength="40">
									</td>
									<td>
										<input type="text" name="valorTotal" id="valorTotal" onkeypress="Validaciones(this,9)" maxlength="20">
									</td>								
								</tr>
								<tr>
								</tr>
								<tr>
									<td  colspan="2">
										<input type="button" id="muestraConcepto" name="muestraConcepto" value="Capturar Concepto y Comentarios" onclick="muestraJ()" />
									</td>
								</tr>
								<tr>
									<td>
										Concepto:
									</td>									
									<td>
										Comentario 1:
									</td>									
									<td colspan="2"  style="display:none;">											
										Comentario 2:
									</td>
									<td>
									</td>								
								</tr>
								<tr>
									<td>
										<textarea id="cCONCEPTO" name="cCONCEPTO" rows="" cols="" onkeypress="Validaciones(this,17)" readonly="readonly"></textarea>
									</td>
									<td>
										<textarea name="cComentario1" id="cComentario1" rows="" cols="" onkeypress="Validaciones(this,17)" readonly="readonly"></textarea>
									</td>
									<td colspan="2" >
										<textarea id="cComentario3" name="cComentario3"  style="display:none;" rows="" cols="" onkeypress="Validaciones(this,17)" readonly="readonly"></textarea>
									</td>
								</tr>
								<!--  <tr align="center">
									<td colspan="4">
										<input type="button" name="btnActualizarRegistro" id="btnActualizarRegistro" value="Actualizar"/>
										<input type="button" name="btnGuardar" id="btnGuardar" value="Guardar" >								
										<input type="button" name="btnRegresar" id="btnRegresar" value="Regresar"/>
									</td>									
								</tr>-->
							</table>
						</fieldset>
					</td>					
				</tr>				
			</table>			
			<br/><br/>
			<table id="modificaRegistro">
					<tr>
					<td>
						<label style="font-size: 10px; font-style: italic; text-align: right;">
							*Doble click en una fila para modificar los registros
						</label>						
					</td>
				</tr>
			</table>
			<div id="divtblPAOP" >		
			<table id="tblPAOP" class="display">				
	            <thead>
	                <tr>
	                	<th>Ejercicio</th>
	                	<th>Mes</th>
	                	<th>Numero</th>
	                	<th>Clave Cucop</th>
	                	<th>Concepto</th>
	                    <th>Valor Total Multianual</th>
	                    <th>Valor Estimado de Compras</th>
	                    <th>Valor Estimado de Compras TLC</th>
	                    <th>Cantidad</th>
	                    <th>Unidad de Medida</th>
	                    <th>Caracter Del Procedimiento</th>
	                    <th>Entidad Federativa</th>
	                    <th>Trimestre 1</th>
	                    <th>Trimestre 2</th>
	                    <th>Trimestre 3</th>
	                    <th>Trimestre 4</th>
	                    <th>Fecha Inicial Contrato</th>
	                    <th>Plurianual</th>
	                    <th>Ejercicio Fiscal</th>
	                    <th>Monto Anual a Ejercer </th>
	                    <th>Comentario 1</th>
	                    <th>Fecha Final Contrato</th>
	                    <th>Comentario 3</th>
	                    <th>Tipo de Procedimiento</th>

	                    <th>EP</th>
	                    <th>Cartera</th>
	                    <th>Partida</th>
	                    <th>Programa</th>

	                    <th>Contratos</th>
	                    <th>OLI</th>
	                    <th>Valor Total</th>
	                    <th>Eliminar</th>
	                   
	                </tr>
	            </thead>
	        </table>
          </div>		        
			<br/>
		</div>
		
		<div id="dlgJustificaciones" title="Concepto y Comentarios">
					<table>
						<tr>
							<td>
								Concepto:
							</td>
							<td>
								<textarea id="cCONCEPTOdlg" name="cCONCEPTOdlg" rows="7" cols="110" onkeypress="Validaciones(this,17)"></textarea>
							</td>
							</tr>
						<tr>
							<td>
								Comentario 1:
							</td>
							<td>
								<textarea id="cComentario1dlg" name="cComentario1dlg" rows="7" cols="110" onkeypress="Validaciones(this,17)"></textarea>
							</td>
						</tr>
						<tr style="display:none;">
							<td>
								Comentario 2:
							</td>
							<td>
								<textarea id="cComentario3dlg" name="cComentario3dlg" rows="7" cols="110" onkeypress="Validaciones(this,17)"></textarea>
							</td>
						</tr>
						<tr>
						
						</tr>
					</table>
				</div>
		
	</form>
	</body>
</html>