<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String cIdContratoDefPluri=(String)session.getAttribute(GestionInterface.ATT_ContratoPlurianualDefinitivo);
	String cidContratoOriginal=(String)session.getAttribute(GestionInterface.ATT_ContratoPlurianual);
		
	Map<String, Role> rol =usuario.getRoles();
	String roles="";
%>

<!doctype html>
<html>
  <head>
    
    
    <title>'CaratulaPlurianualidad.jsp' </title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" charset="utf-8">
		var oTableLineasServ;
		var oTableLineas;
		var oTableLineasBienes;
		var oTableLineasBienesContAbierto;
		var oTableLineasServContAbierto;
		var roles="";
		var tipo;
		$(document).ready(function() {
			tabb=2;
			showAndHideTabs();
			<%
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				Map botones=nb.getBotones(roles,"PlurianualidadContratos","CaratulaPlurianualidad");
					Iterator btn = botones.entrySet().iterator();
					while (btn.hasNext()) {
						Map.Entry b = (Map.Entry)btn.next();%>
						$("#<%=b.getValue()%>").attr("disabled", true);<%
						String img=(String) b.getValue();
						
					}
			%>
			roles="<%=roles%>";
			headerQuery();
			showLines();
			showTables();
			enabledDisabledButtons();
			showLabel();
			queryFormPost("obtenCNET", { async : false});
			if($("#nCodContratoCNET").val()==0){
				$("#nCodContratoCNET").val("");
			}
			if($("#nCodExpedienteCNET").val()==0){
				$("#nCodExpedienteCNET").val("");
			}
		});//TErmina el document ready
		function enabledDisabledButtons(){
			if(parseInt($("#partidasContrato").val(),10)>0){
				$("#gurdarLineas").hide();
			}
			if(parseInt($("#nIdEstado").val(),10)>1){
				$("#gurdar").hide();
			}
		}
		function showTables(){
			hideDivs();
			if($("#cIdTipoContrato").val()=='CV' || $("#esCucopGasolina").val()!=""){
				if(parseInt($("#lContratoAbierto").val(),10)==0){
					$("#tblLineas-serv").show();
					tipo=1;
					showLinesAdded();
				}else{
					$("#divLineasServ-ContAbierto").show();
					showLinesAddedServContAbierto();
					tipo=2;
				}
			}else{
				if(parseInt($("#lContratoAbierto").val(),10)==0){
					$("#divLineasBienes").show();
					tipo=3;
					showLinesAddedBienes();
				}else{
					$("#divLineasBienes-ContAbierto").show();
					tipo=4;
					showLinesAddedBienesContAbierto();
				}
			}
			
		}
		function hideDivs(){
			$("#divLineasServ-ContAbierto").css("display","none");
			$("#divLineasBienes-ContAbierto").css("display","none");
			$("#divLineasBienes").css("display","none");
			$("#tblLineas-serv").css("display","none");
		}
		function showLines(){
			var qw="1=1 and cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'";
		
			oTableLineas = $("#tblLineas").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
        		//sScrollY : "100%",
				sScrollX: "100%",
				bAutoWith: true,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mPartidasContratosPlurianuales&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					//{sName: "nIdConsecutivoRecepM",bVisible: false},
					{sName: "nIdLineaConsolidado"},
					{sName: "cIdSubPartida"},
					{sName: "cIdCABM"},
					{sName: "cDescripcion"},
					{sName: "cDescripcionAdicional"},
					{sName: "mMontoMinimo",bVisible: false},
					{sName: "mMontoMaximo",bVisible: false},
					{sName: "nPocentajeIVA"},
					{sName: "mMontoRemanente"},
					{sName: "mMontoNetoPluri"},
					{sName: "mMontoNetoLinea",bVisible: false},
					{sName: "mMontoNetoMinimo",bVisible: false},
					{sName: "mMontoNetoLineaMax",bVisible: false},
					{sName: "cIdContratoDefinitivo",bVisible: false}
					
				]
			});
		}
		function showLinesAdded(){
			var qw="1=1 and cIdContratoDefinitivo='"+$("#cIdContratoDefinitivoPlurianual").val()+"'";
		
			oTableLineasServ = $("#tblLineas-serv").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
        		//sScrollY : "100%",
				sScrollX: "100%",
				bAutoWith: true,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mEditaPArtidasContPluri&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{sName: "cIdContratoDefinitivo",bVisible: false},
					{sName: "cIdUnidadRequi"},
					{sName: "nIdLineaConsolidado"},
					{sName: "cIdSubPartida"},
					{sName: "cIdCABM"},
					{sName: "cDescripcionAdicional"},
					{sName: "nCantidadMinima"},
					{sName: "nCantidadMAx",bVisible: false},
					{sName: "mMontoMinimo"},
					{sName: "mMontoMaximo",bVisible: false},
					{sName: "nPorcentajeIVA"},
					{sName: "mMontoNetoLineaInput"},
					{sName: "mMontoNetoMinimo",bVisible: false},
					{sName: "mMontoNetoLineaMaximo	",bVisible: false},
					{sName: "mMontoNetoRemanenteAntPluri",bVisible: false}
				]
			});
		}
		function showLinesAddedBienes(){
			var qw="1=1 and cIdContratoDefinitivo='"+$("#cIdContratoDefinitivoPlurianual").val()+"'";
		
			oTableLineasBienes = $("#tblLineas-bienes").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
        		//sScrollY : "100%",
				sScrollX: "100%",
				bAutoWith: true,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mEditaPArtidasContPluri&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{sName: "cIdContratoDefinitivo",bVisible: false},
					{sName: "cIdUnidadRequi"},
					{sName: "nIdLineaConsolidado"},
					{sName: "cIdSubPartida"},
					{sName: "cIdCABM"},
					{sName: "cDescripcionAdicional"},
					{sName: "nCantidadMinimaInput"},
					{sName: "nCantidadMAx",bVisible: false},
					{sName: "mMontoMinimo"},
					{sName: "mMontoMaximo",bVisible: false},
					{sName: "nPorcentajeIVA"},
					{sName: "mMontoNetoLineaInput"},
					{sName: "mMontoNetoMinimo",bVisible: false},
					{sName: "mMontoNetoLineaMaximo	",bVisible: false},
					{sName: "mMontoNetoRemanenteAntPluri",bVisible: false}
				]
			});
		}
		function showLinesAddedBienesContAbierto(){
			var qw="1=1 and cIdContratoDefinitivo='"+$("#cIdContratoDefinitivoPlurianual").val()+"'";
		
			oTableLineasBienesContAbierto = $("#tblLineas-bienesContAbierto").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
        		//sScrollY : "100%",
				sScrollX: "100%",
				bAutoWith: true,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mEditaPArtidasContPluri&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{sName: "cIdContratoDefinitivo",bVisible: false},
					{sName: "cIdUnidadRequi"},
					{sName: "nIdLineaConsolidado"},
					{sName: "cIdSubPartida"},
					{sName: "cIdCABM"},
					{sName: "cDescripcionAdicional"},
					{sName: "nCantidadMinimaInput"},
					{sName: "nCantidadMAxInput"},
					{sName: "mMontoMinimo"},
					{sName: "mMontoMaximo",bVisible: false},
					{sName: "nPorcentajeIVA"},
					{sName: "mMontoNetoLineaInput"},
					{sName: "mMontoNetoMinimo",bVisible: false},
					{sName: "mMontoNetoLineaMaximoInput	"},
					{sName: "mMontoNetoRemanenteAntPluri",bVisible: false}
				]
			});
		}
		function showLinesAddedServContAbierto(){
			var qw="1=1 and cIdContratoDefinitivo='"+$("#cIdContratoDefinitivoPlurianual").val()+"'";
		
			oTableLineasServContAbierto = $("#tblLineas-servContAbierto").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
        		//sScrollY : "100%",
				sScrollX: "100%",
				bAutoWith: true,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mEditaPArtidasContPluri&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{sName: "cIdContratoDefinitivo",bVisible: false},
					{sName: "cIdUnidadRequi"},
					{sName: "nIdLineaConsolidado"},
					{sName: "cIdSubPartida"},
					{sName: "cIdCABM"},
					{sName: "cDescripcionAdicional"},
					{sName: "nCantidadMinima"},
					{sName: "nCantidadMAx",bVisible: false},
					{sName: "mMontoMinimo"},
					{sName: "mMontoMaximo"},
					{sName: "nPorcentajeIVA"},
					{sName: "mMontoNetoLineaInput"},
					{sName: "mMontoNetoMinimo",bVisible: false},
					{sName: "mMontoNetoLineaMaximoInput	"},
					{sName: "mMontoNetoRemanenteAntPluri",bVisible: false}
				]
			});
		}
		function headerQuery(){
			queryFormPost("datosContratoPlurianual", {async: false});
			queryFormPost("mUsuarioMismaUE", {async: false   });
			queryFormPost("mHayPartidasContPlurianual", {async: false   });
			queryFormPost("esCucopDeGasolinaContPlu", {async: false   });
			if(parseInt($("#nIdEstado").val(),10)==4){
				queryFormPost("mValidaCompAutSicop", {async: false});
			}
		}
		function addItems(){
			queryFormPost("agregaPArtidasContratoPlurianual", {async: false});
			$("#gurdarLineas").hide();
			showTables();
		}
		function updateCNET(){	
			var actualiza=false;
			queryFormPost("sp_RegistraCNETPluri", { async : false,
						callback : function() 
						{
							swal("Datos Actualizados.","info",{ button: "Cerrar"});
							actualiza =true;
							saveBinnacle("ActualizaDatosCompranet");
							queryFormPost("sp_RegistraCNET", {async: false   });
						}
					});
			if(!actualiza){
				swal("Error al Actualizar los Registros.","info",{ button: "Cerrar"});
			}
		}
		function deleteData(indice){
			var br=$("#mMontoMinimo_"+indice+"").val();
			br=br.replace("$","");
			br=br.replace(",","");
			$("#mMontoMinimo_"+indice+"").val(br);	
		}
		function deleteData1(indice){
			var br=$("#mMontoNetoLinea_"+indice+"").val();
			br=br.replace("$","");
			br=br.replace(",","");
			$("#mMontoNetoLinea_"+indice+"").val(br);	
		}
		function deleteData2(indice){
			var br=$("#mMontoNetoLineaMaximo_"+indice+"").val();
			br=br.replace("$","");
			br=br.replace(",","");
			$("#mMontoNetoLineaMaximo_"+indice+"").val(br);	
		}
		function onlyInt(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			var strCheck = '0123456789';
			
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
			
			return true;
			//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
		}
		function onlyNumbers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			var strCheck = '0123456789.';
		
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1){
				// Valida que sea numero y punto decimal
				return false; 
			}
			
			//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
			  return true;
		}
		function savesData(){
			var resp=false;
			if(( $("#cIdUsuario").val()==$("#cIdUsuarioCreacion").val() )||( roles.indexOf('ADMIN_RECMAT') >= 0)|| ( roles.indexOf('ANALISTA') >= 0)
					||( roles.indexOf('JEFE') >= 0) ||(parseInt($("#usuariosMismaUE").val(),10)==1) ){
				if($("#cIdTipoContrato").val()=='CV' || $("#esCucopGasolina").val()!="" ){
					if(parseInt($("#lContratoAbierto").val(),10)==0){
						resp=joinChain(oTableLineasServ,tipo);
					}else{
						resp=joinChain(oTableLineasServContAbierto,tipo);
					}
				}else{
					if(parseInt($("#lContratoAbierto").val(),10)==0){
						resp=joinChain(oTableLineasBienes,tipo);
					}else{
						resp=joinChain(oTableLineasBienesContAbierto,tipo);
					}
				}
				if(!resp){
					queryFormPost("actualizaPartidasContPlurianual", { async : false,
						callback : function() 
						{
							swal("Datos Actualizados.","info",{ button: "Cerrar"});
							saveBinnacle("GUARDA_MONTOS_CONTRATO_PLURIANUAL");
							showTables();
							headerQuery();
							showLabel();
						}
					});
				}
			}else{
				swal("No tienes permisos para modificar.","info",{ button: "Cerrar"});
			}
		}
		function joinChain(oTable,tipo){
			var aTrs;
			var token="";
			var nTr="";
			aTrs = oTable.fnGetNodes();
			var cadenaLinea="";
			$("#cadenaLineaCantidad").val(cadenaLinea);
			var montoNeto=0;
			var cantidad=1;
			var cantidadMax=1;
			var montoMaxAbiertoTotal=0;
			var mMontoRemanentePlu=0;
			var resp=false;
			var cadenaMaximo="";
			var montoNetoMax=0;
			for ( var i=0 ; i<aTrs.length; i++ )     
			{  
				nTr = oTable.fnGetData(aTrs[i]);
				cadenaMaximo="";
				montoNeto=unFormatCurrency($("#mMontoNetoLinea_"+nTr[2]).val());
				montoNeto=parseFloat(montoNeto).toFixed(2);
				mMontoRemanentePlu=unFormatCurrency(nTr[14]);
				mMontoRemanentePlu=parseFloat(mMontoRemanentePlu).toFixed(2);
				if(tipo==1){//Contratos de Servicios
					if(mMontoRemanentePlu>0){
						resp=validAmount(mMontoRemanentePlu, montoNeto,nTr[2],cadenaMaximo);	
					}
					cadenaLinea=cadenaLinea+nTr[2]+'-'+montoNeto+'|'+montoNeto+'!'+cantidad+'?'+cantidadMax+',';
				}
				if(tipo==2){//Contratos abiertos de Servicios
					montoNetoMax=unFormatCurrency($("#mMontoNetoLineaMaximo_"+nTr[2]).val());
					montoNetoMax=parseFloat(montoNetoMax).toFixed(2);
					resp=validAmount(mMontoRemanentePlu, montoNeto,nTr[2],cadenaMaximo);
					cadenaMaximo="Máximo";
					if(mMontoRemanentePlu>0){
						resp=validAmount(mMontoRemanentePlu, montoNetoMax,nTr[2],cadenaMaximo);
						resp=validAmountMinimoAndMaximo(montoNeto, nTr[2], montoNetoMax);
					}
					cadenaLinea=cadenaLinea+token+nTr[2]+'-'+montoNeto+'|'+montoNetoMax+'!'+cantidad+'?'+cantidadMax+',';
					montoMaxAbiertoTotal=montoMaxAbiertoTotal+montoNetoMax;
				}
				if(tipo==3){//Contratos de Bienes
					cantidad=$("#nCantidadMin_"+nTr[2]).val();
					cantidadMax=$("#nCantidadMin_"+nTr[2]).val();
					if(mMontoRemanentePlu>0){
						resp=validAmount(mMontoRemanentePlu, montoNeto,nTr[2],cadenaMaximo);
					}
					cadenaLinea=cadenaLinea+token+nTr[2]+'-'+montoNeto+'|'+montoNeto+'!'+cantidad+'?'+cantidadMax+',';
				}
				if(tipo==4){//Contratos Abiertos de Bienes
					montoNetoMax=unFormatCurrency($("#mMontoNetoLineaMaximo_"+nTr[2]).val());
					montoNetoMax=parseFloat(montoNetoMax).toFixed(2);
					cantidad=$("#nCantidadMin_"+nTr[2]).val();
					cantidadMax=$("#nCantidadMAx_"+nTr[2]).val();
					if(mMontoRemanentePlu>0){
						resp=validateQuantity(cantidad, cantidadMax, nTr[2]);
					}
					if(resp){
						return resp;
					}
					if(mMontoRemanentePlu>0){
						resp=validAmount(mMontoRemanentePlu, montoNeto,nTr[2],cadenaMaximo);
					}
					if(resp){
						return resp;
					}
					cadenaMaximo="Máximo";
					if(mMontoRemanentePlu>0){
						resp=validAmount(mMontoRemanentePlu, montoNetoMax,nTr[2],cadenaMaximo);
					}
					cadenaLinea=cadenaLinea+token+nTr[2]+'-'+montoNeto+'|'+montoNetoMax+'!'+cantidad+'?'+cantidadMax+',';
				}
			}
			$("#montoMaxAbiertoTotal").val(montoMaxAbiertoTotal);
			$("#cadenaLinea").val(cadenaLinea);
			return resp;
		}
		function validAmount(totalPluXlinea,amountCaptured,line,tokenMaximo){
			var resp=false;
			if(parseFloat(amountCaptured)>parseFloat(totalPluXlinea)){
				swal("En la linea "+line+" el monto neto capturado es mayor al monto remanente plurianual.\nMonto Remanente="+totalPluXlinea+"\n Monto Neto "+tokenMaximo+" Capturado="+amountCaptured,"info",{ button: "Cerrar"});
				resp=true;
			}
			return resp;
		}
		function validAmountMinimoAndMaximo(amountCapturedMin,line,amountCapturedMax){
			var resp=false;
			if(parseFloat(amountCapturedMin)>=parseFloat(amountCapturedMax)){
				swal("En la l\u00ednea "+line+" el monto neto m\u00ednimo capturado debe ser menor al monto neto m\u00e1ximo.","info",{ button: "Cerrar"});
				resp=true;
			}
			return resp;
		}
		function validateQuantity(quantityMinimum, quantityMaximum,line){
			var resp=false;
			if(parseInt(quantityMinimum,10)>=parseInt(quantityMaximum,10)){
				swal("En la l\u00ednea "+line+" la cantidad m\u00ednima capturada es mayor o igual a la cantidad m\u00e1xima.\nCantidad M\u00ednima="+quantityMinimum+"\n Cantidad M\u00e1xima "+quantityMaximum,"info",{ button: "Cerrar"});
				resp=true;
			}
			return resp;
		}
		function saveBinnacle(accion){
			$("#cAccion").val(accion);
			$("#cIdDocumento").val($("#cIdContratoDefinitivoPlurianual").val());
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
		}
		function unFormatCurrency(str){
			str=""+str;
			str = str.replace("$","");
			str = str.replace(/\,/g,'');
			return parseFloat(str);
		}
		function showLabel(){
			$("#trmTotalRemanente").css("display","none");
			$("#lblTotalRemanente").css("color","black");
			if(parseFloat($("#mTotalRemanente").val())<0){
				$("#trmTotalRemanente").css("display","");
				$("#lblTotalRemanente").css("color","red");
			}
			
		}
		function onlyNumbers2(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			var strCheck = '0123456789';
		
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
		
			return true; 
		}
	</script>
  </head>
  
  <body>
    <form id="frmCaratula">
    	<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Datos del Contrato Plurianual</legend>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblcIdContratoDefinitivoPluri" id="lblcIdContratoDefinitivoPluri"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblcNoContratoCNET" id="lblcNoContratoCNET"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblDescContrato" id="lblDescContrato"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblProveedor" id="lblProveedor"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblOficioDG" id="lblOficioDG"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblFolioMASCP" id="lblFolioMASCP"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lbcContratoAbierto" id="lbcContratoAbierto"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lbcContratoCentralizado" id="lbcContratoCentralizado"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput font-weight-bold" name="lblEstatus" id="lblEstatus"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblSubtotal" id="lblSubtotal"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblMontoIVA" id="lblMontoIVA"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotal" id="lblTotal"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblAutorizaSICOP" id="lblAutorizaSICOP"  readonly style="color:blue"/>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> DATOS COMPRANET</legend>
			<div class="form-group">
				<div class="row">
					<div class="col-md-2">
						<label for="nCodContratoCNET">C&oacute;digo de Contrato</label>
						<input type="text" class="form-control" placeholder="C&oacute;digo de Contrato" size=8  aria-label="C&oacute;digo de Contrato" aria-describedby="basic-addon1"  id="nCodContratoCNET" name="nCodContratoCNET" >
					</div>
					<div class="col-md-2">
						<label for="nCodExpedienteCNET">C&oacute;digo de Expediente</label>
						<input type="text" class="form-control" placeholder="C&oacute;digo de Expediente" size=8  aria-label="C&oacute;digo de Expediente" aria-describedby="basic-addon1"  id="nCodExpedienteCNET" name="nCodExpedienteCNET" >
					</div>
					<div class="col-md-2">
						<label for="cAprobacionPLU">Aprobaci&oacute;n Plurianual</label>
						<input type="text" class="form-control"  id="cAprobacionPLU" name="cAprobacionPLU" size=20 >
					</div>
					<div class="col-md-3">
						<label for="cNoProcedimientoCNET">N&uacute;mero de Procedimiento CNET</label>
						<input type="text" class="form-control"  id="cNoProcedimientoCNET" name="cNoProcedimientoCNET" size=35 maxlength="30" placeholder="N&uacute;mero de Procedimiento CNET" aria-label="N&uacute;mero de Procedimiento CNET" aria-describedby="basic-addon1">
					</div>
					<div class="col-md-2">
						<input type="text" class="transpInput" name="actualizaCNET2" id="actualizaCNET2"  readonly />
						<input type="button" id="actualizaCNET" name="actualizaCNET" value="Actualizar" onclick="updateCNET()" class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-start"/>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Partidas del contrato</legend>
			<div class="form-group" id="divtblLineas">
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalPlurianual" id="lblTotalPlurianual"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalRemanente" id="lblTotalRemanente"  readonly/>
					</div>
				</div>
				<div class="row" id="trmTotalRemanente" style="color: red; display: none;" align="left">
					<div class="col">
						<span>El monto de tu plurianual no puede ser mayor al remanente.</span>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<table id="tblLineas" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr> 
				        		    <th align="center">Partida <br/> Contrato</th>
									<th align="center">Partida</th>
									<th align="center">Cucop</th>
									<th align="center">Descripci&oacute;n</th>
									<th align="center">Descripci&oacute;n <br/> Adicional</th>
									<th align="center" style="display: none;">Monto M&iacute;nimo</th>
									<th align="center" style="display: none;">Monto M&aacute;ximo</th>
									<th align="center">IVA</th>
									<th align="center">Monto<br/> Remanente Ant</th>
									<th align="center">Monto <br/> Plurianual</th>
									<th align="center" style="display: none;">Monto Neto a Precomprometer</th>
									<th align="center" style="display: none;">Monto Neto Minimo</th>
									<th align="center" style="display: none;">Monto Neto Maximo</th>	
									<th style="display: none;"></th>
				        		</tr>										
							</thead>
						</table>
					</div>
				</div>
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="gurdarLineas" name="gurdarLineas" 	value="Agregar Partidas"	onclick="addItems();" />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group" id="divLineasServ">
				<div class="row">
					<div class="col">
						<table id="tblLineas-serv" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr> 
				        		    <th style="display: none;"></th>
									<th align="center">Unidad Requi</th>
									<th align="center">Partida <br/> Contrato</th>
									<th align="center">Partida</th>
									<th align="center">Cucop</th>
									
									<th align="center">Descripci&oacute;n <br/> Adicional</th>
									<th align="center">Cantidad</th>
									<th align="center" style="display: none;">Cantidad <br />M&aacute;xima </th>
									<th align="center" >Precio Unitrio</th>
									<th align="center" style="display: none;"></th>
									<th align="center">IVA</th>
									<th align="center" >Monto Neto</th>
									<th style="display: none;"></th>	
									<th style="display: none;"></th>
									<th style="display: none;">Remanente Plurianual</th>
				        		</tr>										
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="divLineasBienes">
				<div class="row">
					<div class="col">
						<table id="tblLineas-bienes" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr> 
				        		    <th style="display: none;"></th>
									<th align="center">Unidad Requi</th>
									<th align="center">Partida <br/> Contrato</th>
									<th align="center">Partida</th>
									<th align="center">Cucop</th>
									
									<th align="center">Descripci&oacute;n <br/> Adicional</th>
									<th align="center">Cantidad</th>
									<th align="center" style="display: none;">Cantidad <br />M&aacute;xima </th>
									<th align="center" >Precio Unitrio</th>
									<th align="center" style="display: none;"></th>
									<th align="center">IVA</th>
									<th align="center" >Monto Neto</th>
									<th style="display: none;"></th>	
									<th style="display: none;"></th>
									<th style="display: none;">Remanente Plurianual</th>
				        		</tr>										
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="divLineasBienes-ContAbierto">
				<div class="row">
					<div class="col">
						<table id="tblLineas-bienesContAbierto" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr> 
				        		    <th style="display: none;"></th>
									<th align="center">Unidad Requi</th>
									<th align="center">Partida <br/> Contrato</th>
									<th align="center">Partida</th>
									<th align="center">Cucop</th>
									
									<th align="center">Descripci&oacute;n <br/> Adicional</th>
									<th align="center">Cantidad</th>
									<th align="center" >Cantidad <br />M&aacute;xima </th>
									<th align="center" >Precio Unitrio</th>
									<th align="center" style="display: none;"></th>
									<th align="center">IVA</th>
									<th align="center" >Monto Neto</th>
									<th style="display: none;"></th>	
									<th>Monto Neto<br/> M&aacute;ximo</th>
									<th style="display: none;">Remanente Plurianual</th>
				        		</tr>										
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="divLineasServ-ContAbierto">
				<div class="row">
					<div class="col">
						<table id="tblLineas-servContAbierto" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr> 
				        		    <th style="display: none;"></th>
									<th align="center">Unidad Requi</th>
									<th align="center">Partida <br/> Contrato</th>
									<th align="center">Partida</th>
									<th align="center">Cucop</th>
									
									<th align="center">Descripci&oacute;n <br/> Adicional</th>
									<th align="center">Cantidad</th>
									<th align="center" style="display: none;" >Cantidad <br />M&aacute;xima </th>
									<th align="center" >Monto M&iacute;nimo</th>
									<th align="center" >Monto M&aacute;ximo</th>
									<th align="center">IVA</th>
									<th align="center" >Monto Neto <br/>M&iacute;nimo</th>
									<th style="display: none;"></th>	
									<th align="center">Monto Neto<br/>M&aacute;ximo</th>
									<th style="display: none;">Remanente Plurianual</th>
				        		</tr>										
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="gurdar" name="gurdar" 	value="Guardar"	onclick="savesData();" />
						</div>
					</div>
				</div>
			</div>
		</fieldset>
    	
    	<input type="hidden" name="cIdContratoDefinitivo" id="cIdContratoDefinitivo" value="<%=cidContratoOriginal %>"  />
    	<input type="hidden" name="cIdContratoDefinitivoPlurianual" id="cIdContratoDefinitivoPlurianual" value="<%=cIdContratoDefPluri %>"  />
    	<input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value="<%=cIdContratoDefPluri %>"  />
    	<input type="hidden" name="lContratoAbierto" id="lContratoAbierto"/>
    	<input type="hidden" name="esDescentralizado" id="esDescentralizado"/>
    	<input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato"/>
    	<input type="hidden" name="nIdEstado" id="nIdEstado"/>
    	<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuario.getLogin()%>"/>
    	<input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion"/>
    	<input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE"/>
    	<input type="hidden" name="cAccion" id="cAccion"/>
    	<input type="hidden" name="cIdDocumento" id="cIdDocumento"/>
    	<input type="hidden" name="cadenaLinea" id="cadenaLinea"/>
    	<input type="hidden" name="partidasContrato" id="partidasContrato" value="0"/>
    	<input type="hidden" name="mTotalRemanente" id="mTotalRemanente" value="0"/>
    	<input type="hidden" name="montoMaxAbiertoTotal" id="montoMaxAbiertoTotal" value="0" />
    	<input type="hidden" name="esCucopGasolina" id="esCucopGasolina" value="" />
    	
    </form>
  </body>
</html>
