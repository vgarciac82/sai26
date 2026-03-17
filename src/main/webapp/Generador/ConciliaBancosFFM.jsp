<%@page language="java" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.reportes.servlet.*"%>
<%@page import="java.text.DecimalFormat"%>

<%
	String cCentroContable ="";
	String algo ="";
 	String cConciliacion ="";
 	String fConciliacion ="";
 	String importado ="";
 	
 	boolean insertSaldo = false;
 	
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

	boolean error = "SI".equals( request.getParameter("error") );
	
	String msg = "";
	
	if(error){
		msg = (String) session.getAttribute(GestionInterface.ATT_MSG);	
	}
	
	String mensaje = "";
	
	if (request.getParameter( "importado" ) != null && !"".equals(request.getParameter( "importado" )) ){
		importado = request.getParameter("importado") ;
		}
	
	if (request.getParameter( "cconciliacion" ) != null && !"".equals(request.getParameter( "cconciliacion" )) ){
		cConciliacion = request.getParameter( "cconciliacion" );
		}
	
	if (request.getParameter( "fconciliacion" ) != null && !"".equals(request.getParameter( "fconciliacion" )) ){
		fConciliacion = request.getParameter( "fconciliacion" );
		}	

	if (request.getParameter("msg") != null
			&& !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	//Valida Centro de Costos
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	} 
	
	if ("10".equals(cCentroContable) || "00".equals(cCentroContable) ) {
		insertSaldo = true;				
	} 
	
	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar la Conciliacion, Consulte a su administrador.";
	}

	algo = usuario.getLogin();
	//de aki para arriba es de cajon
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Conciliaci&oacute;n Bancos</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3" />
<meta http-equiv="description" content="This is my page" />
<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css">
<style type="text/css" title="currentStyle">
	@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
	@import "css/demo_table_jui.css";
	@import "css/demo_page.css";
</style>
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" charset="utf-8">

var oTable1;
var oTable2;
var oTable3;

$(document).ready(function() {	
	
	if( <%=error%> ){
		alert("Ocurrio el siguiente error: <%=msg%>"  );
	}
	
	if ( $("#seImporto").val() == "S" ){
		alert ("Se importo el estado de cuenta, abre la conciliacion para continuar.");
	}
			
	if( <%=insertSaldo%> ){
		document.getElementById("insertaSaldos").disabled = false;
	}
	
	$("input.AyudaSyC").subIniciaDlg();     
    $("input.autoCompletaSyC").subIniciaAutoCompleta();
	
	$("#cCentroContable").val( "<%=cCentroContable%>");
	
	$("#cUsuarioLogin").val( "<%=algo%>");
		
	creaTablaConciliaciones();
		
	});
	
	
	$(function(){
		$( "#dialog-importaCB" ).dialog({
			autoOpen: false,
			height: 610,
			width: 760,
			modal: true
		});
	});
	
	
	$(function() {
	$( "#dialog-form" ).dialog({
			autoOpen: false,
			height: 250,
			width: 800,
			modal: true,
			buttons: {
						"Aceptar": function() {
												if ($("#hbuscaConciliacion").val()!= ""){
													if ($("#mSaldo").val()!= ""){
														if ($("#mIntereses").val()!= ""){
															//queryFormPost("esCtaVigente", {async: false});
															queryFormPost("esCtaDuplicada", {async: false});
															//if( $("#esVigente").val() == 1){
																if($("#estaDup").val() == 0){
																		alert("Insertamos los valores...");
																	 	queryFormPost("insertaSaldosConciliacionFFM",{async:false, callback:function()
																	        {
																	   			alert("Proceso terminado ");
																	   		}
																	  		});
																		
																		creaTablaConciliaciones();
																		$( this ).dialog( "close" );
																		}
																	else{
																		alert ("Esta Cuenta ya se registro para el mes seleccionado...");
																	}
															//}else{
															//	alert("La cuenta no es VIGENTE para el mes seleccionado...");
															// }
														}else{
															alert("Falta insertar los intereses...");
															$("#mIntereses").focus();
														}
													}else{
														alert("Falta insertar el saldo...");
														$("#mSaldo").focus();
													}
												}
												else{
													alert("Falta selecionar la Cuenta...");
													$("#hbuscaConciliacion").focus();
												}
												
											  },
						"Cancelar": function() {$( this ).dialog( "close" );}
					},
			close: function() {
				alert('Mes:'+ $("#mesSaldo").val()+', Cuenta:'+ $("#hbuscaConciliacion").val()+', CC:'+ $("#cCCSaldo").val()+', Saldo:'+ $("#mSaldo").val()+', Inte:'+ $("#mIntereses").val()+', nFolioSW:'+$("#nFolioCuenta").val()+', Usuario:'+ $("#cUsuarioLogin").val() );
			}
		});
	});
		
	function importaSubmit(){
			document.getElementById("ver").disabled = false;
			$("#ExportarForm").submit();
		}
	
	
	function conciliaAutomatica(){
			document.getElementById("Procesa").disabled = false;
			
			$.ajax({
					async: false,
					cache: false,
					type: 'GET',
					dataType: 'json',
					url: "../servlet/ConciliaAutomaticaFFMServlet",
					data: "Param="+ $("#cBan").val()+","+$("#nMes").val()+"," + $("#nConciliacion").val(), 
					success: function(resp){
						if ( !resp.ERROR ){
							
							var arrData = resp.DATA;
							
							$("#cBan").val(arrData[0]); //cuenta 
							$("#nMes").val(arrData[1]); //mes
							$("#nConciliacion").val(arrData[2]); //conciliacion
							$("#cuenta").val(arrData[0]);
							$("#fecha").val(arrData[1]);
														
							document.getElementById("contConciliacion").style.visibility= "visible"; // Div - Importación estado de cuenta
							document.getElementById("ConciliacionAut").style.visibility= "hidden";   // Div - Procesar conciliación automática	
							document.getElementById("contDatos").style.visibility= "visible";        // Div - Ver, guardar y finalizar conciliación
							$("#seImporto").val("N");
							alert ("Se proceso la conciliacion automatica.");
						}
						else{
							alert(resp.ERROR);
						}
					},
					 error: function(errorThrown) 
				      {
				         alert("error: " + errorThrown.ERROR);
				      }
				});
		}
	
	
	function abreConciliacion(numero, nMes, cve, nFinal){
	
		 document.getElementById("contConciliacion").style.visibility= "visible"; // Div - Importación estado de cuenta
		 document.getElementById("newConciliacion").style.visibility= "visible";  // SubDiv - Importación estado de cuenta
		 document.getElementById("contDatos").style.visibility= "visible";        // Div - Ver, guardar y finalizar conciliación
		 
		 nconciliacion = numero;		 		 
		 $("#nConciliacion").val(numero);
		 
		 cConciliacion = cve;
		 $("#cuenta").val(cve);
		 $("#cBan").val(cve);
		 
		 fConciliacion = nMes;
		 $("#fecha").val(nMes);
		 $("#nMes").val(nMes);
		 
		if ( nFinal == "1"){
			document.getElementById("esFinal").checked= true;
			document.getElementById("final").disabled = true;
			document.getElementById("iniciar").disabled = true;
		}
		else
		{
			document.getElementById("esFinal").checked= false;
			document.getElementById("iniciar").disabled = false;
		}
		
		document.getElementById("ntabla1").style.visibility= "hidden";
		document.getElementById("ntabla2").style.visibility= "hidden";
		document.getElementById("reportes").style.visibility= "hidden"; 
		
		document.getElementById("cargaArchivo").disabled = true;
		document.getElementById("importa").disabled = true;
		document.getElementById("TIPO").checked = false;
		document.getElementById("TIPO").disabled = true;
		document.getElementById("ver").disabled = false;
		
		queryFormPost("readEstatusConciliacionFFM", {async: false});
		
		if ($("#seImporto").val() == "S"){
			document.getElementById("ConciliacionAut").style.visibility= "visible"; // Div - Procesar conciliación automática
			document.getElementById("contDatos").style.visibility= "hidden";        // Div - Ver, guardar y finalizar conciliación
			document.getElementById("Procesa").disabled = false;
		}else{
			document.getElementById("ConciliacionAut").style.visibility= "hidden";  // Div - Procesar conciliación automática
			document.getElementById("Procesa").disabled = true;
		}
				
	}
		
	
	function nuevaConciliaicon(){
		document.getElementById("TIPO").disabled = false;
		document.getElementById("contConciliacion").style.visibility= "visible"; // Div - Importación estado de cuenta
		document.getElementById("newConciliacion").style.visibility= "visible";  // SubDiv - Importación estado de cuenta
		document.getElementById("contDatos").style.visibility= "hidden";         // Div - Ver, guardar y finalizar conciliación 
		document.getElementById("ntabla1").style.visibility= "hidden";  		 // Div - Cargos
		document.getElementById("ntabla2").style.visibility= "hidden";  	 	 // Div - Abonos
		document.getElementById("reportes").style.visibility= "hidden"; 		 // Div - Repoertes
		document.getElementById("ver").disabled = true;
		
		 nconciliacion = "";		 		 
		 $("#nConciliacion").val("");
		 
		 cConciliacion = "";
		 $("#cuenta").val("");
		 $("#cBan").val("");
		 
		 fConciliacion = "";
		 $("#fecha").val("");
		 $("#nMes").val("");
	}
		
	
	function insertaSaldosConciliacion(){
		$("#mesSaldo").val(1);
		$("#hbuscaConciliacion").val("");
		$("#mSaldo").val("");
		//$("#mIntereses").val("");
		$( "#dialog-form" ).dialog( "open" );
		
	}
	
	
	function eliminaConciliacion(numeroConciliacion){
		var r = "";
		r = confirm("Se elmiminará la conciliación número "+numeroConciliacion+", ¿Desea continuar?");
				
		$("#numeroConciliacionElimina").val(numeroConciliacion);		
		
		if (r){
			queryFormPost("eliminaConciliacionFFM",
				{async:false, callback:function()
			        {
			   			alert("Proceso terminado ");
			   			$("#seImporto").val("N");
			   			window.location = "ConciliaBancosFFM.jsp?importado=N";
			   			return;
			   		}
				});
			}
		//creaTablaConciliaciones();
	}
		
	function habilita(){
		if ( $("#TIPO").prop("checked")) {
		 document.getElementById("cargaArchivo").disabled = false;
		 document.getElementById("importa").disabled = false;
		 }
	}	
	
	
	function OpenDialogAdjunta(cuenta, mes, tipo){
		
		 $("#nMes").val(mes);
		 $("#cBan").val(cuenta);
		
		$('#uploadConciliacionFrm').attr('src', "CargaCB.jsp?ctaBan="+cuenta+"&mes="+mes+"&cTipoDoc="+tipo);
		
		$("#dialog-importaCB").dialog( "open" );
		
	}
		 
	function seleccionaCARGOS(){
		if ( $("#sTCargos").prop("checked")) {
		 	$('#dt_vNoConciliadosC input').each(function(idx, elm) {
				$(this).prop('checked', true);
			});
		 }
		 else {
		 	$('#dt_vNoConciliadosC input').each(function(idx, elm) {
				$(this).prop('checked', false);
			});
		 }
	}
	
	function seleccionaABONOS(){
		if ( $("#sTAbonos").prop("checked")) {
		 	$('#dt_vNoConciliadosA input').each(function(idx, elm) {
				$(this).prop('checked', true);
			});
		 }
		 else{
		 	$('#dt_vNoConciliadosA input').each(function(idx, elm) {
				$(this).prop('checked', false);
			});
		 }
	}
	
	function habiliat_boton(){
			if ( $("#report").val()!= "00") {
		 		document.getElementById("exporta").disabled = false;
		 	}
		 	else{
		 		document.getElementById("exporta").disabled = true;
		 	}
		}
	
	function ActualizaDatos(){
		$("#cuenta").val($("#cBan").val());
		$("#fecha").val($("#nMes").val());
		
		if ( $("#nFinal").val() == "1"){
			document.getElementById("esFinal").checked= true;
		}
		else
		{
			document.getElementById("esFinal").checked= false;
		}
		
	}
	
	function aceptarCarga(){
		$( "#dialog-importaCB" ).dialog("close");
		
		
		//queryFormPost("updateSaldoPDF", {async: false});
		
		creaTablaConciliaciones();
	}
		
	function cambia_Mes(){
		creaTablaConciliaciones();
		 document.getElementById("contConciliacion").style.visibility= "hidden";
		 document.getElementById("newConciliacion").style.visibility= "hidden";
		 document.getElementById("contDatos").style.visibility= "hidden";
		
		document.getElementById("ntabla1").style.visibility= "hidden";
		document.getElementById("ntabla2").style.visibility= "hidden";
		document.getElementById("reportes").style.visibility= "hidden"; 
		
		document.getElementById("cargaArchivo").disabled = true;
		document.getElementById("importa").disabled = true;
		document.getElementById("TIPO").checked = false;
		document.getElementById("TIPO").disabled = true;
		document.getElementById("ver").disabled = false;
		
	}
		
	function conciliacion(){
				
				document.getElementById("ntabla1").style.visibility= "visible";  // Div - Cargos
				document.getElementById("ntabla2").style.visibility= "visible";  // Div - Abonos
				document.getElementById("reportes").style.visibility= "visible"; // Div - Repoertes
				
				$("#Cargos").val("0");
				$("#Abonos").val("0");
				$("#sAbonos").val("0");
				$("#sCargos").val("0");
				creaTablaVistaC();
				creaTablaVistaA();
			
		 }
		  
	function guarda_info()
		{	
			sumas();
			
			if($("#guarda").prop("disabled")){
				alert("Seleccion no cuadra");
			}
			else {
				alert("Se guarda y actuliza la conciliacion");
				guarda_valores();
			}
		}
		
	function exporta_archivo(){
	
		switch ($("#report").val()) {
		case "01":
				alert("Genera Movimientos en Conciliacion");
				$("#AuxiliarBanco").submit();
			break;
		case "02":
				alert("Genera Movimientos Conciliados");
				//$("#AuxiliarBanco").submit();
			break;
		case "03":
				alert("Exporta Estado de Cuenta");
				$("#AuxiliarBanco").submit();
			break;
		case "04":
				alert("Exporta Auxiliar 11121");
				$("#AuxiliarBanco").submit();
			break;
		default:
			break;
		}
	}	
		
	
	function finaliza(){
		var fin = 0;
		alert("Se finalizara la conciliacion");
				
		var cAux="";
		var cEdo = "";
		var cNoC = "";
			
		var cSuma = $("#dt_vNoConciliadosC").dataTable().fnGetNodes();
		var aSuma = $("#dt_vNoConciliadosA").dataTable().fnGetNodes();
		var ren;
		
		for (i = 0; i < cSuma.length; i ++)
			{
			ren = oTable1.fnGetData(cSuma[i]);
				if ( ren[3] == "Contabilidad"){
					if ( ren[1] == $("#nConciliacion").val()){
						cAux += ren[11]+',';					
						}
					else {
						cNoC += ren[11]+',';
					}	
				}else{
				
					if ( ren[1] == $("#nConciliacion").val()){
							cEdo += ren[11]+',';
						}
					else{
							cNoC += ren[11]+',';
					}
				}
			}
		
		for (j = 0; j < aSuma.length; j ++)
			{
			ren = oTable2.fnGetData(aSuma[j]);
				if ( ren[3] == "Contabilidad"){
					if ( ren[1] == $("#nConciliacion").val()){
						cAux += ren[11]+',';					
						}
					else {
						cNoC += ren[11]+',';
					}	
				}else{
					if ( ren[1] == $("#nConciliacion").val()){
						cEdo += ren[11]+',';
					}
					else
					{
						cNoC += ren[11]+',';
					}
				}
			}
		
		cAux   = "'" + cAux.substring(0, cAux.length - 1) + "'";
		cNoC   = "'" + cNoC.substring(0, cNoC.length - 1) + "'";
		cEdo   = "'" + cEdo.substring(0, cEdo.length - 1) + "'";
				
		$("#numConciliacionf").val($("#nConciliacion").val());
		$("#cadenaAuxiliarf").val(cAux);
		$("#cadenaEdoCtaf").val(cEdo);
		$("#cadenaNoConf").val(cNoC);
		
		
		queryFormPost("validaFinal", {async: false});	
		
		fin = $("#validaFinal").val();
			
		if ( fin == 0 ){
							
		queryFormPost("finConciliacionFFM",{async:false, callback:function()
	        {
				alert("Proceso terminado ");
			}
			});
		
		document.getElementById("guarda").disabled = true;
		document.getElementById("final").disabled = true;
		document.getElementById("esFinal").checked= true;
		
		document.getElementById("ntabla1").style.visibility= "hidden";
		document.getElementById("ntabla2").style.visibility= "hidden";
		
		document.getElementById("reportes").style.visibility= "visible";
		}
		else
		{
			alert("Ya existe una conciliación finalizada para este mes...");
		}
		
	}
	
	
	function guarda_valores(){
		
		var cAuxiliar="";
		var cEdoCta = "";
		var cNoCon = "";
			
		var cargosSuma = $("#dt_vNoConciliadosC").dataTable().fnGetData();
		var abonosSuma = $("#dt_vNoConciliadosA").dataTable().fnGetData();
		var renglon;
		
		$('#dt_vNoConciliadosC input:checked').each(function(idx, elm) {
			renglon = cargosSuma[oTable1.fnGetPosition($(this).closest('tr')[0])];
				if ( renglon[3] == "Contabilidad"){
					if ( renglon[1] == $("#nConciliacion").val()){
						cAuxiliar += renglon[11]+',';					
						}
					else {
						cNoCon += renglon[11]+',';
					}	
				}else{
					if ( renglon[1] == $("#nConciliacion").val()){
						cEdoCta += renglon[11]+',';
						}
					else {
						cNoCon += renglon[11]+',';
					}
				}
			});
			
		$('#dt_vNoConciliadosA input:checked').each(function(idx, elm) {
			renglon = abonosSuma[oTable2.fnGetPosition($(this).closest('tr')[0])];
				if ( renglon[3] == "Contabilidad"){
					if ( renglon[1] == $("#nConciliacion").val()){
						cAuxiliar += renglon[11]+',';
						}
					else {
						cNoCon += renglon[11]+',';
					}	
				}else{
					if ( renglon[1] == $("#nConciliacion").val()){
						cEdoCta += renglon[11]+',';
						}
					else {
						cNoCon += renglon[11]+',';
					}
				}
			});
		
			cAuxiliar = cAuxiliar.substring(0, cAuxiliar.length - 1 );
			cNoCon	  = cNoCon.substring(0, cNoCon.length - 1);
			cEdoCta   = cEdoCta.substring(0, cEdoCta.length - 1) ;		
					
			$("#numConciliacion").val($("#nConciliacion").val());
			$("#cadenaAuxiliar").val(cAuxiliar);
			$("#cadenaEdoCta").val(cEdoCta);
			$("#cadenaNoCon").val(cNoCon);
							
		queryFormPost("guardaConciliacionManualFFM",{async : false, callback : function() 
				{
					alert("Proceso terminado.");
				}
				});
				
				creaTablaVistaC();
				creaTablaVistaA();		
	}
	
	
	function sumas(){
		var cargos = 0;
		var abonos = 0;
		
		$("#sCargos").val(0);
		$("#sAbonos").val(0);
		sumaCargos();
		sumaAbonos();
		cargos = parseFloat($("#sCargos").val()).toFixed(2) ;
		abonos = parseFloat($("#sAbonos").val()).toFixed(2) ;
		
		$("#Cargos").val(cargos);
		$("#Abonos").val(abonos);
		
		if (cargos == abonos){
			document.getElementById("guarda").disabled = false;
			document.getElementById("final").disabled = false;
		}
		else{
			document.getElementById("guarda").disabled = true;
			document.getElementById("final").disabled = true;
			document.getElementById("final").checked = false;
		}		
	}
	
	function sumaCargos(){
			var cargosSuma = $("#dt_vNoConciliadosC").dataTable().fnGetData();
			var renglon;
			var banco = 0;
			var conta = 0;

			$('#dt_vNoConciliadosC input:checked').each(function(idx, elm) {
			renglon = cargosSuma[oTable1.fnGetPosition($(this).closest('tr')[0])];
				if ( renglon[3] == "Contabilidad"){
					conta += Math.round(parseFloat(renglon[9])*100)/100;
				}else{
					banco += Math.round(parseFloat(renglon[9])*100)/100;
				}
			});
			
			$("#sCargos").val((conta - banco).toFixed(2));		
	}
	
	function sumaAbonos(){	
			var abonosSuma = $("#dt_vNoConciliadosA").dataTable().fnGetData();
			var renglon;
			var banco = 0;
			var conta = 0;
			
			$('#dt_vNoConciliadosA input:checked').each(function(idx, elm) {
			renglon = abonosSuma[oTable2.fnGetPosition($(this).closest('tr')[0])];
				if ( renglon[3] == "Contabilidad"){
					conta += Math.round(parseFloat(renglon[9])*100)/100;
				}else{
					banco += Math.round(parseFloat(renglon[9])*100)/100;
				}
			});
			
			$("#sAbonos").val( (conta - banco).toFixed(2));	
		}
	
	function fixDecimals(oObj)
        {
			var valor = parseFloat(oObj.aData[oObj.iDataColumn]);
			return valor.toFixed(2);
		}
		
		
	
	function creaTablaVistaC() {

		oTable1 = $("#dt_vNoConciliadosC").dataTable(
						{	
							bAutoWidth : false,
							oLanguage: {
										sProcessing: "Procesando...",
										sZeroRecords: "No hay registros a mostrar",
										sEmptyTable: "No hay datos en la tabla",
										sLoadingRecords: "Cargando...",
										sSearch: "Buscar:",
										sInfo: "Número de Registros --> _TOTAL_ ",
										sInfoEmpty: "Sin Registros"
									   },
							bPaginate : false,
							bServerSide : true,
							sScrollY : 450,
							sScrollYInner : "100%",
							bScrollCollapse: true,
							bFilter : true,
							bSort : true,
							bRetrive : true,
							bDestroy : true,
							bJQueryUI : true,
							aaSorting: [[ 2, "asc" ]] ,
							sAjaxSource : window.location.protocol + "//"+ window.location.host + "/"+ window.location.pathname.split("/")[1]
									+ "/crud?rt=t&ql=fn_j_noConciliadosFFM("+$("#nConciliacion").val()+","+$("#nMes").val()+",'"+$("#cBan").val()+"')&qw="
									+ " cTipo = 'C' ",
							aoColumns : [ { sName : "Id"},
							              { sName : "nConciliacion"},
							              { sName : "nFolioOrigen"},
							              { sName : "cOrigen"},
							              { sName : "fFecha"},
							              { sName : "cReferencia"},
							              { sName : "cCheque"},
							              { sName : "cDescripcion"},
							              { sName : "cTipo"},
							              { 
							              	sName : "mMonto",
							              	fnRender: fixDecimals
							              },
							              { sName : "Conciliado", bVisible : false},
							              { sName : "idDetalle", bVisible : false}]
						});		
						
						document.getElementById("sTCargos").disabled = false;
						
		}
		
		function creaTablaVistaA(){				

		oTable2 = $("#dt_vNoConciliadosA").dataTable(
						{
							bAutoWidth : false,
							oLanguage: {
										sProcessing: "Procesando...",
										sZeroRecords: "No hay registros a mostrar",
										sEmptyTable: "No hay datos en la tabla",
										sLoadingRecords: "Cargando...",
										sSearch: "Buscar:",
										sInfo: "Número de Registros --> _TOTAL_ ",
										sInfoEmpty: "Sin Registros"
									   },
							bPaginate : false,
							bServerSide : true,
							sScrollY : 450,
							sScrollYInner : "100%",
							bScrollCollapse: true,
							bFilter : true,
							bSort : true,
							bRetrive : true,
							bDestroy : true,
							bJQueryUI : true,
							aaSorting: [[ 2, "asc" ]] ,
							sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1]
									+ "/crud?rt=t&ql=fn_j_noConciliadosFFM("+$("#nConciliacion").val()+","+$("#nMes").val()+",'"+$("#cBan").val()+"')&qw="
									+ " cTipo = 'A' ", 
							aoColumns : [ {	sName : "Id" },
										  {	sName : "nConciliacion"},
										  {	sName : "nFolioOrigen"},
										  { sName : "cOrigen"},
										  { sName : "fFecha"},
										  { sName : "cReferencia"},
										  { sName : "cCheque"},
										  { sName : "cDescripcion"},
										  { sName : "cTipo"},
										  { 
							              	sName : "mMonto",
							              	fnRender: fixDecimals
							              },
										  { sName : "Conciliado", bVisible : false},
										  { sName : "idDetalle", bVisible : false}]
							}
						);
						
						document.getElementById("sTAbonos").disabled = false;

}


function creaTablaConciliaciones() {

		oTable3 = $("#dt_Conciliaciones").dataTable(
						{
							bAutoWidth : false,
							oLanguage: {
										sProcessing: "Procesando...",
										sZeroRecords: "No hay registros a mostrar",
										sEmptyTable: "No hay datos en la tabla",
										sLoadingRecords: "Cargando...",
										sSearch: "Buscar:",
										sInfo: "Número de Registros --> _TOTAL_ ",
										sInfoEmpty: "Sin Registros"
									   },
							bPaginate : false,
							bFilter : true,
							bSort : true,    
							sScrollX: "100%",
    						bScrollCollapse: true,
							bRetrive : true,
							bDestroy : true,
							bJQueryUI : true,
							bServerSide : true,
							aaSorting: [[ 1, "asc" ]] ,
							sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1]	+ "/crud?rt=t&ql=fn_j_ConciliacionesFFM("+$("#mesMostrado").val()+",'"+$("#cCC").val()+"')",
								aoColumns: [
											{ sName: "nConciliacion" },
											{ sName: "nCban"   },
											{ sName: "nMes" },
											{ sName: "cDescripcion"	},
											{ sName: "Final"},
											{ sName: "cCentroContable"	},
											{ sName: "SaldoTXT"	},
											//{ sName: "SaldoPDF"	},
											//{ sName: "Intereses"},
											{ sName: "Contabilidad"	},
											{ sName: "PDF"	},
											{ sName: "Firmas"},
											{ sName: "abreConciliacion"},
											{ sName: "eliminaConciliacion"} ]
						}
					);
		}
	
	
	function DescargaConciliacion(idDocumento){
		url = "../SAIFilestore?select="+idDocumento;
	    window.open(url, "popacuse","scrollbars=1, resizable=yes, width=500, height=700");
	}
	
</script>

</head>
<body id="dt_example">
		<div id="principal" class="container" style="width: 90%">
			<h1> Conciliaciones Bancarias </h1>
			<div id="conciliacion"  style=" visibility: visible;  width: 90% "  class="container" >
	        	Selecciona el Mes: 
					<select name = "mesMostrado" id = "mesMostrado" onchange="cambia_Mes()">
								<option value=0>Todos los Meses...</option>
								<option value=1>Enero</option>
								<option value=2>Febrero</option>
								<option value=3>Marzo</option>
								<option value=4>Abril</option>
								<option value=5>Mayo</option>
								<option value=6>Junio</option>
								<option value=7>Julio</option>
								<option value=8>Agosto</option>
								<option value=9>Septiembre</option>
								<option value=10>Octubre</option>
								<option value=11>Noviembre</option>
								<option value=12>Diciembre</option>
					</select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;	
				<button id="nuevaConciliacion" onclick="nuevaConciliaicon()">Nueva Conciliacion</button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<button id="insertaSaldos" disabled="disabled" onclick="insertaSaldosConciliacion()">Inserta Saldos</button>	
				<br/><br/>
				<table id="dt_Conciliaciones" class="display" >
					<thead>
						<tr>
							<th>ID</th>
							<th>Cuenta</th>
							<th>Mes</th>
							<th>Descripción</th>
							<th>Final</th>
							<th>CC</th>
							<th>SaldoTXT</th>
							<th>Contabilidad</th>
							<th>PDF</th>
							<th>Firmas</th>
							<th>Op 1</th>
							<th>Op 2</th>
						</tr>
					</thead>
				</table>
			</div>	
		</div>
	<form id="ExportarForm" name="ExportarForm" action="../servlet/ConciliaBancosFFMServlet" enctype = "multipart/form-data" method = "post" >
		<div id="contConciliacion" style=" visibility: hidden;  width: 90% "  class="container">
			<h2 class="container" > Conciliación </h2>
			<div id="newConciliacion"  class="container" style= "visibility: hidden">
			<fieldset>
				<table align="center" cellpadding="3">									
					<tr>
						<td>  </td>
						<td> Importa Estado de Cuenta: <input type="checkbox" id="TIPO" name="TIPO" value="0" disabled="disabled" onclick="habilita()" /> </td>
						<td>  </td>
						<td> Cuenta Bancaria: </td>
						<td><input type="text" id="cuenta" value="<%=cConciliacion%>" name="cuenta" size="35" readonly="readonly" /></td>
					</tr>
					<tr id="trLayout">
						<td> Archivo: </td>
						<td>  <input id="cargaArchivo" name="cargaArchivo" disabled="disabled" type="file" size="22" /></td>
						<td>  </td>
						<td> Mes a Conciliar: </td>
						<td><input type="text" id="fecha" value="<%=fConciliacion%>" name="fecha" size="35" readonly="readonly"/></td>
					</tr>
					<tr>
						<td></td>
						<td><input type="button" id="importa"   name="importa"   value="Procesa Archivo" disabled="disabled" onclick="importaSubmit()" /></td>
						<td>  </td>
						<td>  </td>
						<td>  Final <input type="checkbox" id="esFinal" name="esFinal" value="" disabled="disabled" /> </td>
					</tr>
				</table>
			</fieldset>	
			</div>		
		</div>
	</form>
	<form id="ProcesaForm" name="ProcesaForm" action="../servlet/ConciliaAutomaticaFFMServlet" enctype = "multipart/form-data" method = "post" >
		<div id="ConciliacionAut" style=" visibility: hidden; width: 90% "  class="container">
			<fieldset>
				<table align="center" cellpadding="3">									
					<tr>
						<td>  </td>
						<td> <input type="button" id="Procesa" name="Procesa" value="Procesar Conciliacion" disabled="disabled" onclick="conciliaAutomatica()" /> </td>
						<td>  </td>
					</tr>
				</table>
			</fieldset>		
		</div>
	</form>
	<form id="AuxiliarBanco" name="AuxiliarBanco" action="../reportes/RepConciliaBancosFFMServlet" method="get" target="_blanck" >
		<div id="contDatos"  style=" visibility: hidden;  width: 90%" class="container" >
		<div id= "opciones" class="container" >
			<fieldset>
				<table align="center" cellpadding="3">
				<tr>
					<td> <input type="button" id="ver" name="ver" value="Ver Movimientos" onclick="conciliacion()" /></td>
					<td> <input type="button" id="guarda" name="guarda" value="Guardar Conciliacion" disabled="disabled" onclick="guarda_info()" /></td>
					<td> <input type="button" id="final" name="final" value="Finaliza Conciliacion" disabled="disabled" onclick="finaliza()" /></td>
				</tr>
				<tr>
					<td><input type="button" id="iniciar" name="iniciar" value="Valida Seleccion" disabled="disabled" onclick="sumas()" /></td>
					<td> Suma Cargos <input type="text"   id="Cargos" name="Cargos" value="" readonly="readonly" /></td>
					<td> Suma Abonos <input type="text"   id="Abonos" name="Abonos" value="" readonly="readonly" /></td>
				</tr>	
				</table>
			</fieldset>
		</div>
		<div id= "reportes" style=" visibility: hidden " class="container" >
			<div align="center">
			<fieldset>
				Elige tu reporte: 
					<select name = "report" id = "report" onchange="habiliat_boton()">
						<option value="00">Seleciona un reporte...</option>
						<option value="01">Movimientos en Conciliacion</option>
						<option value="03">Estado de Cuenta</option>
						<option value="04">Auxiliar</option>
					</select>	
					<input type="button" id="exporta" name="exporta" value="Genera Archivo" disabled="disabled" onclick="exporta_archivo()" />
			</fieldset>
			</div>
		</div>
		<div style="display: none" >
			<input  type="text" id="cBan" value="<%=cConciliacion%>" name="cBan" size="35" />
			<input  type="text" id="nMes" value="<%=fConciliacion%>" name="nMes" size="35" />
			<input  type="text" id="cCC" value="<%=cCentroContable%>" name="cCC" size="35" />
			<input  type="hidden" id="sCargos" name="sCargos" value="" />
			<input  type="hidden" id="sAbonos" name="sCargos" value="" />  
			<input  type="hidden" id="nConciliacion" name="nConciliacion" value="" />  
			<input  type="hidden" id="numConciliacion" name="numConciliacion" value="" />
			<input  type="hidden" id="cadenaAuxiliar" name="cadenaAuxiliar" value="" />  
			<input  type="hidden" id="cadenaEdoCta" name="cadenaEdoCta" value="" />  
			<input  type="hidden" id="cadenaNoCon" name="cadenaNoCon" value="" />  
			<input  type="hidden" id="numConciliacionf" name="numConciliacionf" value="" />
			<input  type="hidden" id="cadenaAuxiliarf" name="cadenaAuxiliarf" value="" />  
			<input  type="hidden" id="cadenaEdoCtaf" name="cadenaEdoCtaf" value="" />  
			<input  type="hidden" id="cadenaNoConf" name="cadenaNoConf" value="" />
			<input  type="hidden" id="tipoReporte" name="tipoReporte" value="" />
			<input  type="hidden" id="cDescripcion" name="cDescripcion" value="" />
			<input  type="hidden" id="nFinal" name="nFinal" value="" />
			<input  type="hidden" id="nFolioCuenta" name="nFolioCuenta" value="" />
			<input  type="hidden" id="cCCSaldo" name="cCCSaldo" value="" />
			<input  type="hidden" id="cUsuarioLogin" name="cUsuarioLogin" value="" />
			<input  type="hidden" id="numeroConciliacionElimina" name="numeroConciliacionElimina" value="" />
			<input  type="hidden" id="validaFinal" name="validaFinal" value="" />
			<input  type="hidden" id="esVigente" name="esVigente" value="" />
			<input  type="hidden" id="estaDup" name="estaDup" value="" />
			
			<input  type="hidden" id="seImporto" name="seImporto" value="<%=importado%>" />
								
		</div>
		<div  id= "ntabla1" style=" visibility: hidden;  width: 90%" class="container" >
			<h2> Cargos </h2> 
			<input type="checkbox" id="sTCargos" name="sTCargos" value="" disabled="disabled" onclick="seleccionaCARGOS()" /> Todos 
			<table id="dt_vNoConciliadosC" class="display" >
				<thead>
					<tr>
						<th>Concilia</th>
						<th>ID</th>
						<th>Folio</th>
						<th>Origen</th>
						<th>Fecha</th>
						<th>Referencia</th>
						<th>Cheque</th>
						<th>Descripcion</th>
						<th>Tipo</th>
						<th>Monto</th>
						<th style="display: none;"></th>
						<th style="display: none;"></th>
					</tr>
				</thead>
			</table>
		</div>
		<div id= "ntabla2" style=" visibility: hidden;  width: 90%" class="container" >
			<h2> Abonos </h2> 
			<input type="checkbox" id="sTAbonos" name="sTAbonos" value="" disabled="disabled" onclick="seleccionaABONOS()" /> Todos 
			<table id="dt_vNoConciliadosA" class="display" >
				<thead>
					<tr>
						<th>Concilia</th>
						<th>ID</th>
						<th>Folio</th>
						<th>Origen</th>
						<th>Fecha</th>
						<th>Referencia</th>
						<th>Cheque</th>
						<th>Descripcion</th>
						<th>Tipo</th>
						<th>Monto</th>
						<th style="display: none;"></th>
						<th style="display: none;"></th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
	</form>
	<div id="dialog-form" title="Insertar Conciliacion Manual">
		<form id="insertaConciliacion" name="insertaConciliacion" action="">
			<fieldset>
				<table cellpadding="2" cellspacing="0" border="0" width="100%">
					<tr>
						<td align="right">Selecciona el Mes:</td>
						<td colspan=3>
							<select name = "mesSaldo" id = "mesSaldo">
								<option value=1>Enero</option>
								<option value=2>Febrero</option>
								<option value=3>Marzo</option>
								<option value=4>Abril</option>
								<option value=5>Mayo</option>
								<option value=6>Junio</option>
								<option value=7>Julio</option>
								<option value=8>Agosto</option>
								<option value=9>Septiembre</option>
								<option value=10>Octubre</option>
								<option value=11>Noviembre</option>
								<option value=12>Diciembre</option>
							</select>
					   </td>
					</tr>
					<tr>
						<td align="right">Cuenta bancaria:</td>
						<td colspan="3">
							<input id="hbuscaConciliacion" name="hbuscaConciliacion" value="" size="30" maxlength="30" class="AyudaSyC autoCompletaSyC" type="text">
							<input id="buscaConciliacion" name="buscaConciliacion" value=""  size="5" maxlength="5" class="" type="hidden">
						</td>
					</tr>
					<tr>
						<td align="right">Saldo Banco:</td>
						<td><input type="text" name="mSaldo" id="mSaldo" align="right" onkeypress="" /> </td>
					</tr>
					<tr>
						<td align="right">Intereses:</td>
						<td><input type="text" name="mIntereses" id="mIntereses" align="right" onkeypress="" /> </td>
					</tr>
				</table>
			</fieldset>
		</form>
	</div>
	<div id="dialog-importaCB" title="Adjunta Archivo PDF">
		<div id="uploadCB">
			<iframe id="uploadConciliacionFrm"
				src="CargaCB.jsp?ctaBan=1223&mes=1" align="top"
				frameborder="0" height="500" width="750"> 
			</iframe>
		</div>
	</div>
</body>
</html>


