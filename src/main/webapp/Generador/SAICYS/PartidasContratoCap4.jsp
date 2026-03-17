<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page import="java.util.*" %>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	Map rol =usuario.getRoles();
	String cIdContratoDefinitivo="";
	if(request.getParameter("cIdContratoDefinitivo")!=null){
		cIdContratoDefinitivo=request.getParameter("cIdContratoDefinitivo");
		session.setAttribute(GestionInterface.ATT_ContratCap4Definitivo, cIdContratoDefinitivo);
	}else{
		cIdContratoDefinitivo=(String)session.getAttribute(GestionInterface.ATT_ContratCap4Definitivo);
	}
	//System.out.print(cIdContratoDefinitivo);
%>

<!DOCTYPE html>
<html>
  <head>
   
    
    <title>PartidasContratoCap4.jsp</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="Consolidado">
	
	<script type="text/javascript">
		var oTablePartidas="";
		var oTablePartidas2="";
		$(document).ready(function() {
			<%
			    String role="";
			    String roles="";
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
			//	NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				Map botones=nb.getBotones(roles,"ContratoCap4","PartidasContratoCap4");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			
			%>
			
			ocultarTablas();
			initQuerys();
			showButtons();
			showAndHideDivTotalPluri();
		});//Fin del document ready
		function showAndHideDivTotalPluri(){
			if( $("#isPlurianual").val()==0  ){
				$("#divTotalPluri").hide();
			}else{
				$("#divTotalPluri").show();
			}
		}
		function initQuerys(){
			queryFormPost("obtieneDatosContCap4", {async : false,
				callback : function() {
					muestraTabla();
					activaCheck();
				}
			});
			querySelectPost("cambiaCombomCatalogoSubPartidaRead", "cboPartida", {async : false,
				callback : function() {
					$("#cboPartida").val(0);
					//querySelectPost("cucposContratoCap4", "cboCucop", {async : false});
				}
			});
		}
		function consultaCucop(){
			$("#cIdUnidadMedida").val('PZA');
			if($("#nIdTipoActividadEconomica").val()==1){
				$("#cIdUnidadMedida").val('SRV');
			}
			querySelectPost("cucposContratoCap4", "cboCucop", {async : false});
		}
		function initTablaBienes(){
			var qw="1=1 AND cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'"; 
			$('#tblPartidasAbiBienes').empty();
			oTablePartidas = $("#tblPartidasBienes").dataTable({
				bScrollCollapse: true,
	        		bInfo: false,
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mContratoCap4Partidas&qw="+qw,
					bJQueryUI: true,
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{sName: "nIdContratoCap4Partida",bVisible: false},
						{sName: "nIdPartida"},
						{sName: "cIdSubPartida"},
						{sName: "cIdCABM"},
						{sName: "descripcion"},
						{sName: "cDescripAdi"},
						{sName: "nCantidadMin"},
						{sName: "cIdUnidadMedida"},
						{sName: "mPrecioUnitario"},
						{sName: "nIdIVA"},
						{sName: "mMontoNetoLinea"},
						{sName: "boton1"},
						{sName: "cIdContratoDefinitivo",bVisible: false},
						{sName: "mMontoNetoLineaOrig",bVisible: false}
					]
				});
		}
		function initTablaServ(){
			var qw="1=1 AND cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'"; 
			$('#tblPartidasAbiServ').empty();
			oTablePartidas = $("#tblPartidasServ").dataTable({
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mContratoCap4Partidas&qw="+qw,
					bJQueryUI: true,
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{sName: "nIdContratoCap4Partida",bVisible: false},
						{sName: "nIdPartida"},
						{sName: "cIdSubPartida"},
						{sName: "cIdCABM"},
						{sName: "descripcion"},
						{sName: "cDescripAdi"},
						{sName: "nCantidadMinReadOnly"},
						{sName: "cIdUnidadMedida"},
						{sName: "mPrecioUnitario"},
						{sName: "nIdIVA"},
						{sName: "mMontoNetoLinea"},
						{sName: "boton1"},
						{sName: "cIdContratoDefinitivo",bVisible: false},
						{sName: "mMontoNetoLineaOrig",bVisible: false}
					]
				});
		}
		function initTablaAbiBienes(){
			var qw="1=1 AND cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'"; 
			oTablePartidas2 = $("#tblPartidasAbiBienes").dataTable({
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mContratoCap4Partidas&qw="+qw,
					bJQueryUI: true,
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{sName: "nIdContratoCap4Partida",bVisible: false},
						{sName: "nIdPartida"},
						{sName: "cIdSubPartida"},
						{sName: "cIdCABM"},
						{sName: "descripcion"},
						{sName: "cDescripAdi"},
						{sName: "nCantidadMin"},
						{sName: "nCantidadMax"},
						{sName: "cIdUnidadMedida"},
						{sName: "mPrecioUnitarioAbi"},
						{sName: "nIdIVA"},
						{sName: "mMontoNetoLinea"},
						{sName: "mMontoNetoMaximo"},
						{sName: "boton1"},
						{sName: "cIdContratoDefinitivo",bVisible: false},
						{sName: "mMontoNetoLineaOrig",bVisible: false},
						{sName: "mMontoNetoMaximoOrig",bVisible: false}
					]
				});
		}
		function initTablaAbiServ(){
			var qw="1=1 AND cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'"; 
			oTablePartidas2 = $("#tblPartidasAbiServ").dataTable({
				bAutoWidth : true,
				bPaginate:true,
				bDestroy:true,
				bRetrive : true,
				bServerSide:false,
				sScrollX: "100%",
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
				bProcessing: true,
				bJQueryUI: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mContratoCap4Partidas&qw="+qw,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{sName: "nIdContratoCap4Partida",bVisible: false},
					{sName: "nIdPartida"},
					{sName: "cIdSubPartida"},
					{sName: "cIdCABM"},
					{sName: "descripcion"},
					{sName: "cDescripAdi"},
					{sName: "nCantidadMinReadOnly"},
					{sName: "cIdUnidadMedida"},
					{sName: "mPrecioUnitarioAbi"},
					{sName: "mPrecioUnitarioMax"},
					{sName: "nIdIVA"},
					{sName: "mMontoNetoLinea"},
					{sName: "mMontoNetoMaximo"},
					{sName: "boton1"},
					{sName: "cIdContratoDefinitivo",bVisible: false},
					{sName: "mMontoNetoLineaOrig",bVisible: false},
					{sName: "mMontoNetoMaximoOrig",bVisible: false}
				]
			});
		}
		function initTablaPluBienes(){
			var qw="1=1 AND cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'"; 
			oTablePartidas = $("#tblPartidasPluBienes").dataTable({
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mContratoCap4Partidas&qw="+qw,
					bJQueryUI: true,
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{sName: "nIdContratoCap4Partida",bVisible: false},
						{sName: "nIdPartida"},
						{sName: "cIdSubPartida"},
						{sName: "cIdCABM"},
						{sName: "descripcion"},
						{sName: "cDescripAdi"},
						{sName: "nCantidadMin"},
						{sName: "cIdUnidadMedida"},
						{sName: "mPrecioUnitario"},
						{sName: "nIdIVA"},
						{sName: "mMontoNetoLinea"},
						{sName: "mMontoNetoPluri"},
						{sName: "boton1"},
						{sName: "cIdContratoDefinitivo",bVisible: false},
						{sName: "mMontoNetoLineaOrig",bVisible: false}
					]
				});
		}
		function initTablaPluServ(){
			var qw="1=1 AND cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'";  
			oTablePartidas = $("#tblPartidasPluServ").dataTable({
				bAutoWidth : true,
				bPaginate:true,
				bDestroy:true,
				bRetrive : true,
				bServerSide:false,
				sScrollX: "100%",
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
				bProcessing: true,
				bJQueryUI: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mContratoCap4Partidas&qw="+qw,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{sName: "nIdContratoCap4Partida",bVisible: false},
					{sName: "nIdPartida"},
					{sName: "cIdSubPartida"},
					{sName: "cIdCABM"},
					{sName: "descripcion"},
					{sName: "cDescripAdi"},
					{sName: "nCantidadMinReadOnly"},
					{sName: "cIdUnidadMedida"},
					{sName: "mPrecioUnitario"},
					{sName: "nIdIVA"},
					{sName: "mMontoNetoLinea"},
					{sName: "mMontoNetoPluri"},
					{sName: "boton1"},
					{sName: "cIdContratoDefinitivo",bVisible: false},
					{sName: "mMontoNetoLineaOrig",bVisible: false}
				]
			});
		}
		function initTablaPluAbiBienes(){
			var qw="1=1 AND cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'"; 
			oTablePartidas2 = $("#tblPartidasPluAbiBienes").dataTable({
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mContratoCap4Partidas&qw="+qw,
					bJQueryUI: true,
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{sName: "nIdContratoCap4Partida",bVisible: false},
						{sName: "nIdPartida"},
						{sName: "cIdSubPartida"},
						{sName: "cIdCABM"},
						{sName: "descripcion"},
						{sName: "cDescripAdi"},
						{sName: "nCantidadMin"},
						{sName: "nCantidadMax"},
						{sName: "cIdUnidadMedida"},
						{sName: "mPrecioUnitarioAbi"},
						{sName: "nIdIVA"},
						{sName: "mMontoNetoLinea"},
						{sName: "mMontoNetoMaximo"},
						{sName: "mMontoNetoPluri"},
						{sName: "boton1"},
						{sName: "cIdContratoDefinitivo",bVisible: false},
						{sName: "mMontoNetoLineaOrig",bVisible: false},
						{sName: "mMontoNetoMaximoOrig",bVisible: false}
					]
				});
		}
		function initTablaPluAbiServ(){
			var qw="1=1 AND cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'"; 
			oTablePartidas2 = $("#tblPartidasPluAbiServ").dataTable({
				bAutoWidth : true,
				bPaginate:true,
				bDestroy:true,
				bRetrive : true,
				bServerSide:false,
				sScrollX: "100%",
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
				bProcessing: true,
				bJQueryUI: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mContratoCap4Partidas&qw="+qw,
				aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{sName: "nIdContratoCap4Partida",bVisible: false},
						{sName: "nIdPartida"},
						{sName: "cIdSubPartida"},
						{sName: "cIdCABM"},
						{sName: "descripcion"},
						{sName: "cDescripAdi"},
						{sName: "nCantidadMinReadOnly"},
						{sName: "cIdUnidadMedida"},
						{sName: "mPrecioUnitarioAbi"},
						{sName: "mPrecioUnitarioMax"},
						{sName: "nIdIVA"},
						{sName: "mMontoNetoLinea"},
						{sName: "mMontoNetoMaximo"},
						{sName: "mMontoNetoPluri"},
						{sName: "boton1"},
						{sName: "cIdContratoDefinitivo",bVisible: false},
						{sName: "mMontoNetoLineaOrig",bVisible: false},
						{sName: "mMontoNetoMaximoOrig",bVisible: false}
					]
				});
		}
		function agregaPartida(){
			if($("#cboPartida").val()==0){
				swal("Seleccionar una partida.",{icon:"warning",button: "Cerrar"});
				return;
			}
			if($("#cboCucop").val()==null || $("#cboCucop").val()==0){
				swal("Seleccionar un CUCOP.",{icon:"warning",button: "Cerrar"});
				return;
			}
			$("#cIdUniMed").val('SRV');
			if($("#nIdTipoActividadEconomica").val()==2){
				$("#cIdUniMed").val('PZA');
				
			}
			queryFormPost("mAgregaPartidasContCap4", {async : false,
				callback : function() {
					swal("Partida Agregada.",{icon:"success",button: "Cerrar"});
					muestraTabla();
				}
			});
		}
		function onlyIntInmuebles(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			var strCheck = '0123456789';
			
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
			
			return true;
			//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
		}
		function onlyMoney(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			var strCheck = '0123456789.';
		
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
		
			return true;
			//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
		  }
		  function eliminaPartida(id){
		  	$("#nIdContratoCap4Partida").val(id);
		  	if(parseInt($("#nIdEstado").val(),10)>1){
		  		swal("No se puede eliminar la partida por que su estatus es diferente de capturado.",{icon:"warning",button: "Cerrar"});
		  		return;
		  	}
		  	swal({
		  		title: "¿Está seguro que desea eliminar la partida?",
		  		text: "Una vez confirmado, no podrá deshacer los cambios.!",
		  		icon: "info",
		  		buttons: {
		  			confirm : "Aceptar",
		  			cancel: "Cancelar"
		  		},
		  	}).then((continuar) => {
		  		if (!continuar) {
		  			$("#nIdContratoCap4Partida").val(-1);
		  			return;
		  		}else{
		  			queryFormPost("mDeletePartidasContCAp4", {async : false,
						callback : function() {
							$("#nIdContratoCap4Partida").val(-1);
							swal("Partida Eliminada",{icon:"success",button: "Cerrar"});
							muestraTabla();
						}
					});
		  		}
		  	});
			
		  }
		  function ocultarTablas(){
		  	$("#divTblPartidasBienes").css("display","none");
		  	$("#divTblPartidasServ").css("display","none");
		  	$("#divTblPartidasAbiBienes").css("display","none");
		  	$("#divTblPartidasAbiServ").css("display","none");
		  	$("#divTblPartidasPluBienes").css("display","none");
		  	$("#divTblPartidasPluServ").css("display","none");
		  	$("#divTblPartidasPluAbiBienes").css("display","none");
		  	$("#divTblPartidasPluAbiServ").css("display","none");
		  }
		  function emptyDatatable(){
				$('#tblPartidasBienes').dataTable().fnClearTable();
				$('#tblPartidasServ').dataTable().fnClearTable();
				$('#tblPartidasAbiBienes').dataTable().fnClearTable();
				$('#tblPartidasAbiServ').dataTable().fnClearTable();
				$('#tblPartidasPluBienes').dataTable().fnClearTable();
				$('#tblPartidasPluServ').dataTable().fnClearTable();
				$('#tblPartidasPluAbiBienes').dataTable().fnClearTable();
				$('#tblPartidasPluAbiServ').dataTable().fnClearTable();
		}
		function muestraTabla(){
		  //$("#nIdTipoActividadEconomica").val()==2 && $("#isPlurianual").val()==0 &&
		  	emptyDatatable();
		  	if( $("#isPlurianual").val()==0  ){
		  		if($("#isAbierto").val()==0){
		  			if($("#nIdTipoActividadEconomica").val()==2){//Bienes
		  				$("#divTblPartidasBienes").css("display","block");
		  				initTablaBienes();
			  		}else{
			  			$("#divTblPartidasServ").css("display","block");
			  			initTablaServ();
			  		}
		  		}else{
		  			if($("#nIdTipoActividadEconomica").val()==2){//Bienes
		  				$("#divTblPartidasAbiBienes").css("display","block");
		  				initTablaAbiBienes();
			  		}else{
			  			$("#divTblPartidasAbiServ").css("display","block");
			  			initTablaAbiServ();
			  		}
		  		}
		  	}else{//Plurianuales
		  		
		  		if($("#isAbierto").val()==0){
		  			if($("#nIdTipoActividadEconomica").val()==2){//Bienes
		  				$("#divTblPartidasPluBienes").css("display","block");
		  				initTablaPluBienes();
			  		}else{
			  			$("#divTblPartidasPluServ").css("display","block");
			  			initTablaPluServ();
			  		}
		  		}else{
		  			if($("#nIdTipoActividadEconomica").val()==2){//Bienes
		  				$("#divTblPartidasPluAbiBienes").css("display","block");
		  				initTablaPluAbiBienes();
			  		}else{
			  			$("#divTblPartidasPluAbiServ").css("display","block");
			  			initTablaPluAbiServ();
			  		}
		  		}
		  	}
		}
		function validaContratoAbierto(check){
			if(check.checked){
				$("#isAbierto").val(1);
				$("#divTotalMax").show();
			}
			else{
				$("#isAbierto").val(0);
				$("#divTotalMax").hide();
			}
			ocultarTablas();
			muestraTabla();
		}
		function activaCheck(){
			if( $("#isAbierto").val()==1 ){
				document.getElementById("chk_ContAbierto").checked=true;
				$("#divTotalMax").show();
			}
			else{
				document.getElementById("chk_ContAbierto").checked=false;
				$("#divTotalMax").hide();
			}
		}
		function guardarPartidas(){
			var cadenaPart=cadenaPartidas();
			if(cadenaPart==null){
				return;
			}
			$.ajax({url: "../../servlet/ContratoCap4Servlet" , type:'post' , async: false
			,data:'operacion=2&cIdContratoDefinitivo='+$("#cIdContratoDefinitivo").val()
			+'&isAbierto='+$("#isAbierto").val()+'&isPlurianual='+$("#isPlurianual").val()
			+'&actEconomContratoCap4='+$("#nIdTipoActividadEconomica").val()
			+'&cadenaPartidas='+cadenaPart
			, dataType: 'json', success: 
				function(j){
					var mensaje=j[0].MENSAJE;
					var resp=j[0].RESPUESTA;
					swal({
						title: "",
						text: mensaje,
						icon: "info",
						buttons: {
							confirm : "Cerrar"
							},
						}).then((continuar) => {
							if(resp){
								window.location = "ContratoCap4.jsp?tab=3";
							}
					});
					
				}
			});
		}
		function getDataTable(){
			if($("#isAbierto").val()==0){
				return oTablePartidas.dataTable();
			}else{
				//alert("Abi");
				 
				return oTablePartidas2.dataTable();
			}
		}
		function getaDataTable(){
			var aTrs;
			if( $("#isPlurianual").val()==0  ){
		  		if($("#isAbierto").val()==0){
		  			if($("#nIdTipoActividadEconomica").val()==2){//Bienes
		  				aTrs = $('#tblPartidasBienes').dataTable().fnGetNodes();
			  		}else{
			  			aTrs = $('#tblPartidasServ').dataTable().fnGetNodes();
			  		}
		  		}else{
		  			if($("#nIdTipoActividadEconomica").val()==2){//Bienes
		  				aTrs = $('#tblPartidasAbiBienes').dataTable().fnGetNodes();
			  		}else{
			  			aTrs = $('#tblPartidasAbiServ').dataTable().fnGetNodes();
			  		}
		  		}
		  	}else{//Plurianuales
		  		if($("#isAbierto").val()==0){
		  			if($("#nIdTipoActividadEconomica").val()==2){//Bienes
		  				aTrs = $('#tblPartidasPluBienes').dataTable().fnGetNodes();
			  		}else{
			  			aTrs = $('#tblPartidasPluServ').dataTable().fnGetNodes();
			  		}
		  		}else{
		  			if($("#nIdTipoActividadEconomica").val()==2){//Bienes
		  				aTrs = $('#tblPartidasPluAbiBienes').dataTable().fnGetNodes();
			  		}else{
			  			$('#tblPartidasPluServ').empty();
			  			aTrs = $('#tblPartidasPluAbiServ').dataTable().fnGetNodes();
			  		}
		  		}
		  	}
			return aTrs;
		}
		function cadenaPartidas(){
			var cantMin=1;
			var cantMax=1;
			var precioU=0;
			var nIdIVA=1;
			var precioUMax=0;
			var mMontoNetoLine=0;
			var mMontoNetoMin=0;
			var mMontoNetoMax=0;
			var mMontoNetoPluri=0;
			var sumaMontoNetoPluri=0;
			var mMontoNetoLineOrig=0;
			var mMontoNetoMaxOrig=0;
			var cidUniMed='SRV';
			var cadenaPartidas='';
			var otable=getDataTable();
			var aTrs = getaDataTable();
			var token=",";
			for(var i=0;i<aTrs.length;i++){
				if(i>0){
					cadenaPartidas+=token;
				}
				aData = otable.fnGetData(aTrs[i]);
				nIdIVA=$("#nporcIVA"+aData[0]).val();
 				mMontoNetoLine=$("#mMontoNetoLinea_"+aData[0]).val();
 				mMontoNetoMin=mMontoNetoLine;
 				cidUniMed=$("#cIdUnidadMedida_"+aData[0]).val();
				if( $("#isPlurianual").val()==0  ){
			  		if($("#isAbierto").val()==0){
			  			if($("#nIdTipoActividadEconomica").val()==2){// contrato de Bienes
			  				cantMin=$("#nCantidadMin_"+aData[0]).val();
				  		}else{//contrato de servicios
				  			cantMin=$("#nCantidadMinReadOnly_"+aData[0]).val();
				  		}
				  		precioU=$("#mPrecioUnit_"+aData[0]).val();
				  		mMontoNetoLineOrig=aData[13];
			  		}else{
			  			if($("#nIdTipoActividadEconomica").val()==2){//Contrato Abierto de Bienes
			  				cantMin=$("#nCantidadMin_"+aData[0]).val();
			  				cantMax=$("#nCantidadMax_"+aData[0]).val();
				  		}else{//Contrato Abierto de Servicios
				  			cantMin=$("#nCantidadMinReadOnly_"+aData[0]).val();
			  				precioUMax=$("#mPrecioUnitMax_"+aData[0]).val();
			  				//Validar el minimo y maximo que no sean iguales
				  		}
				  		precioU=$("#mPrecioUnitAbi_"+aData[0]).val();
				  		mMontoNetoMax=$("#mMontoNetoMax_"+aData[0]).val();
 						mMontoNetoLineOrig=aData[15];
 						mMontoNetoMaxOrig=aData[16];
			  		}
			  	}else{//Plurianuales
			  		if($("#isAbierto").val()==0){
			  			if($("#nIdTipoActividadEconomica").val()==2){//Contrato plurianual de Bienes
			  				cantMin=$("#nCantidadMin_"+aData[0]).val();
				  		}else{//Contrato plurianual de Servicios
				  			cantMin=$("#nCantidadMinReadOnly_"+aData[0]).val();
				  		}
				  		precioU=$("#mPrecioUnit_"+aData[0]).val();
				  		mMontoNetoLineOrig=aData[14];
			  		}else{
			  			if($("#nIdTipoActividadEconomica").val()==2){//Contrato Plurianual Abierto de Bienes
			  				cantMin=$("#nCantidadMin_"+aData[0]).val();
			  				cantMax=$("#nCantidadMax_"+aData[0]).val();
				  		}else{//Contrato Plurianual Abierto de Servicios
				  			cantMin=$("#nCantidadMinReadOnly_"+aData[0]).val();
			  				precioUMax=$("#mPrecioUnitMax_"+aData[0]).val();
				  		}
				  		precioU=$("#mPrecioUnitAbi_"+aData[0]).val();
				  		mMontoNetoMax=$("#mMontoNetoMax_"+aData[0]).val();
				  		mMontoNetoLineOrig=aData[16];
 						mMontoNetoMaxOrig=aData[17];
 						
			  		}
			  		mMontoNetoPluri=$("#mMontoNetoTotalPluri_"+aData[0]).val();
			  		sumaMontoNetoPluri+=parseFloat(mMontoNetoPluri);
			  	}
				//Validación
				if(!validacionMontos(precioU, precioUMax, cantMin, cantMax, aData[1])){
					return null;
				}
				cadenaPartidas+=aData[0]+"-"+($("#cDescripAdi_"+aData[0]).val()).replace("-","")+"-"+cantMin+"-"+cantMax+"-"+precioU+"-"+precioUMax
						+"-"+mMontoNetoLine+"-"+mMontoNetoMin+"-"+mMontoNetoMax+"-"+mMontoNetoPluri+"-"+nIdIVA+"-"+mMontoNetoLineOrig+"-"+mMontoNetoMaxOrig+"-"+cidUniMed;
			}
			if($("#isPlurianual").val()==1 && sumaMontoNetoPluri!=$("#montoTotalPluri").val()){
				swal("La suma plurianual por partida "+sumaMontoNetoPluri+" no es igual al total plurianual "+$("#montoTotalPluri").val() ,{icon:"warning",button: "Cerrar"});
				return null;
			}
			return cadenaPartidas;
		}
		function validacionMontos(precioU,precioUMax,cantMin,cantMax,numLinea){
			if($("#isAbierto").val()==1 ){
				if($("#nIdTipoActividadEconomica").val()==1){//Servicio
					if(parseFloat(precioU)<=0.00){
						swal("El Monto M\u00e1nimo no puede ser cero en la l\u00ednea n\u00famero "+numLinea,{icon:"warning",button: "Cerrar"});
						return false;
					}
					if(parseInt(precioUMax)==0){
						swal("La Monto M\u00e1ximo debe de ser mayor a 0 en la l\u00ednea "+numLinea,{icon:"warning",button: "Cerrar"});
						return false;
					}
					if(parseInt(precioUMax)<=parseFloat(precioU)){
						swal("La Monto M\u00e1ximo debe de ser mayor al m\u00ednimo en la l\u00ednea "+numLinea,{icon:"warning",button: "Cerrar"});
						return false;
					}
				}else{//Bienes
					if(parseFloat(precioU)<=0.00){
						swal("El Precio unitario no puede ser cero en la l\u00ednea n\u00famero "+numLinea,{icon:"warning",button: "Cerrar"});
						return false;
					}
					if(parseInt(cantMin)==0){
						swal("La Cantidad Mínima debe de ser mayor a 0 en la l\u00ednea "+numLinea,{icon:"warning",button: "Cerrar"});
						return false;
					}
					if(parseInt(cantMax)==0){
						swal("La Cantidad M\u00e1xima debe de ser mayor a 0 en la l\u00ednea "+numLinea,{icon:"warning",button: "Cerrar"});
						return false;
					}
					if(parseInt(cantMax)<=parseInt(cantMin)){
						swal("La Cantidad M\u00e1xima debe de ser mayor a la Cantidad M\u00ednima en la l\u00ednea "+numLinea,{icon:"warning",button: "Cerrar"});
						return false;
					}
				}
			}else{
				if(parseFloat(precioU)<=0.00){
					swal("El Precio unitario no puede ser cero en la l\u00ednea n\u00famero "+numLinea,{icon:"warning",button: "Cerrar"});
					return false;
				}
				if($("#nIdTipoActividadEconomica").val()==2 && parseInt(cantMin)==0){//Servicio
					swal("La Cantidad M\u00e1xima debe de ser mayor a 0 en la l\u00ednea "+numLinea,{icon:"warning",button: "Cerrar"});
					return false;
				}
			}
			return true;
		}
		function showButtons(){
			if(parseInt($("#nIdEstado").val(),10)>=2){
				$("#guardaPartidas").hide();
				$("#btnAgregar").hide(); 
			}else{
				$("#guardaPartidas").show();
				$("#btnAgregar").show();
			}
			if($("#isAbierto").val()==0){
				$("#trTotalNetoMax").hide();
			}else{
				$("#trTotalNetoMax").show();
			}
		}
	</script>
  </head>
  
  <body>
  	<form id="formPartidasContCap4">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Datos del Contrato</legend>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblDefinitivo" id="lblDefinitivo"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="cConceptoContrato" id="cConceptoContrato"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblProveedor" id="lblProveedor"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTipoContrato" id="lblTipoContrato"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="cEstado" id="cEstado"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblSubtotal" id="lblSubtotal"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalIVA" id="lblTotalIVA"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalNeto" id="lblTotalNeto"  readonly/>
					</div>
				</div>
				<div class="row" id="divTotalMax">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalNetoMax" id="lblTotalNetoMax"  readonly/>
					</div>
				</div>
				<div class="row" id="divTotalPluri">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblMontoTotalPluri" id="lblMontoTotalPluri"  readonly/>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Crear partidas</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-3">
							<label for="cboPartida">Partida </label>
						</div>
						<div class="col-4">
							<select class="custom-select" id="cboPartida" name="cboPartida" 	onchange="consultaCucop()">								
							</select>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-3">
							<label for="cboCucop">Cucop </label>
						</div>
						<div class="col-4">
							<select class="custom-select" id="cboCucop" name="cboCucop" >
								<option value="" selected="selected"></option>
							</select>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="btnAgregar" name="btnAgregar" 	value="Agregar"	onclick="agregaPartida();" />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group form-check  ">
				<div class="row" >
					<div class="form-check input-group">
						<div class="col-3">		
							<label class="form-check-label" for="chk_ContAbierto">¿Es Contrato Abierto? </label>
						</div>
						<div class="form-check  col-auto">
			  				<input class="form-check-input" type="checkbox" id="chk_ContAbierto" name="chk_ContAbierto" onClick="validaContratoAbierto(this);">
			  			</div>
		  			</div>
				</div>
			</div>
			<div class="form-group" id="divTblPartidasBienes" style="display: none;">
				<div class="row">
					<div class="col">
						<table id="tblPartidasBienes" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th style="display: none;">nIdContratoCap4Partida</th>
									<th >Num</th>
									<th >Partida </th>
									<th >Cucop</th>
									<th >Descripci&oacute;n</th>
									<th >Descripci&oacute;n  <br /> Adicional </th>
									<th >Cantidad</th>
									<th >Unidad Medida</th>
									<th >Precio<br/>Unitario</th>
									<th >IVA</th>
									<th >Monto Neto<br/>Linea</th>
									<th ></th>
									<th  style="display: none;">Contrato Definitivo</th>
									<th  style="display: none;">Monto Neto Linea original</th>
								</tr>									
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="divTblPartidasServ" >
				<div class="row">
					<div class="col">
						<table id="tblPartidasServ" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th style="display: none;">nIdContratoCap4Partida</th>
									<th >Num</th>
									<th >Partida </th>
									<th >Cucop</th>
									<th >Descripci&oacute;n</th>
									<th >Descripci&oacute;n  <br /> Adicional </th>
									<th >Cantidad</th>
									<th >Unidad Medida</th>
									<th >Precio<br/>Unitario</th>
									<th >IVA</th>
									<th >Monto Neto<br/>Linea</th>
									<th ></th>
									<th  style="display: none;">Contrato Definitivo</th>
									<th  style="display: none;">Monto Neto Linea original</th>
								</tr>									
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="divTblPartidasAbiBienes" >
				<div class="row">
					<div class="col">
						<table id="tblPartidasAbiBienes" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th style="display: none;">nIdContratoCap4Partida</th>
									<th  >Num</th>
									<th  >Partida </th>
									<th  >Cucop</th>
									<th  >Descripci&oacute;n</th>
									<th  >Descripci&oacute;n  <br /> Adicional </th>
									<th  >Cantidad<br/>Minima</th>
									<th  >Cantidad<br/>Maxima</th>
									<th  >Unidad Medida</th>
									<th  >Precio<br/>Unitario</th>
									<th  >IVA</th>
									<th  >Monto Neto<br/>Minimo</th>
									<th  >Monto Neto<br/>Maximo</th>
									<th  ></th>
									<th  style="display: none;">Contrato Definitivo</th>
									<th  style="display: none;">Monto Neto minimo original</th>
									<th  style="display: none;">Monto Neto maximo original</th>	
								</tr>									
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="divTblPartidasAbiServ" >
				<div class="row">
					<div class="col">
						<table id="tblPartidasAbiServ" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th style="display: none;">nIdContratoCap4Partida</th>
									<th >Num</th>
									<th >Partida </th>
									<th >Cucop</th>
									<th >Descripci&oacute;n</th>
									<th >Descripci&oacute;n  <br /> Adicional </th>
									<th >Cantidad</th>
									<th >Unidad Medida</th>
									<th >Monto<br/>Minimo</th>
									<th >Monto<br/>Maximo</th>
									<th >IVA</th>
									<th >Monto Neto<br/>Minimo</th>
									<th >Monto Neto<br/>Maximo</th>
									<th ></th>
									<th style="display: none;">Contrato Definitivo</th>	
									<th style="display: none;">Monto Neto minimo original</th>
									<th style="display: none;">Monto Neto maximo original</th>
								</tr>									
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="divTblPartidasPluBienes" >
				<div class="row">
					<div class="col">
						<table id="tblPartidasPluBienes" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th style="display: none;">nIdContratoCap4Partida</th>
									<th >Num</th>
									<th >Partida </th>
									<th >Cucop</th>
									<th >Descripci&oacute;n</th>
									<th >Descripci&oacute;n  <br /> Adicional </th>
									<th >Cantidad</th>
									<th >Unidad Medida</th>
									<th >Precio<br/>Unitario</th>
									<th >IVA</th>
									<th >Monto Neto</th>
									<th >Monto Neto<br/> Total  Pluri</th>
									<th ></th>
									<th  style="display: none;">Contrato Definitivo</th>
									<th  style="display: none;">Monto Neto  original</th>
								</tr>									
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="divTblPartidasPluServ" >
				<div class="row">
					<div class="col">
						<table id="tblPartidasPluServ" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th style="display: none;">nIdContratoCap4Partida</th>
									<th >Num</th>
									<th >Partida </th>
									<th >Cucop</th>
									<th >Descripci&oacute;n</th>
									<th >Descripci&oacute;n  <br /> Adicional </th>
									<th >Cantidad</th>	
									<th >Unidad Medida</th>						
									<th >Precio<br/>Unitario</th>
									<th >IVA</th>
									<th >Monto Neto</th>
									<th >Monto Neto<br/> Total  Pluri</th>
									<th ></th>
									<th  style="display: none;">Contrato Definitivo</th>
									<th  style="display: none;">Monto Neto original</th>
								</tr>									
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="divTblPartidasPluAbiBienes" >
				<div class="row">
					<div class="col">
						<table id="tblPartidasPluAbiBienes" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th style="display: none;">nIdContratoCap4Partida</th>
									<th >Num</th>
									<th >Partida </th>
									<th >Cucop</th>
									<th >Descripci&oacute;n</th>
									<th >Descripci&oacute;n  <br /> Adicional </th>
									<th >Cantidad<br/>Minima</th>
									<th >Cantidad<br/>Maxima</th>
									<th >Unidad Medida</th>
									<th >Precio<br/>Unitario</th>
									<th >IVA</th>
									<th >Monto Neto<br/>Minimo</th>
									<th >Monto Neto<br/>Maximo</th>
									<th >Monto Neto<br/> Total  Pluri</th>
									<th ></th>
									<th  style="display: none;">Contrato Definitivo</th>
									<th  style="display: none;">Monto Neto minimo original</th>
									<th  style="display: none;">Monto Neto maximo original</th>
								</tr>									
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="divTblPartidasPluAbiServ" >
				<div class="row">
					<div class="col">
						<table id="tblPartidasPluAbiServ" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th style="display: none;">nIdContratoCap4Partida</th>
									<th >Num</th>
									<th >Partida </th>
									<th >Cucop</th>
									<th >Descripci&oacute;n</th>
									<th >Descripci&oacute;n  <br /> Adicional </th>
									<th >Cantidad</th>
									<th >Unidad Medida</th>
									<th >Monto<br/>Minimo</th>
									<th >Monto<br/>Maximo</th>
									<th >IVA</th>
									<th >Monto Neto<br/>Minimo</th>
									<th >Monto Neto<br/>Maximo</th>
									<th >Monto Neto<br/> Total  Pluri</th>
									<th ></th>
									<th  style="display: none;">Contrato Definitivo</th>
									<th  style="display: none;">Monto Neto minimo original</th>
									<th  style="display: none;">Monto Neto maximo original</th>
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
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="guardaPartidas" name="guardaPartidas" 	value="Guardar"	onclick="guardarPartidas();" />
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<input type="hidden" id="mCatalogoCapitulo" name="mCatalogoCapitulo" value="4" />
		<input type="hidden" id="nIdTipoActividadEconomica" name="nIdTipoActividadEconomica" value="1" />
		<input type="hidden" id="cIdUnidadMedida" name="cIdUnidadMedida" value="SRV" />
		<input type="hidden" id="nIdContratoCap4Partida" name="nIdContratoCap4Partida" value="-1" />
		<input type="hidden" id="isPlurianual" name="isPlurianual" value="0" />
		<input type="hidden" id="isAbierto" name="isAbierto" value="0" />
		<input type="hidden" id="montoTotalPluri" name="montoTotalPluri" value="0" />
		<input type="hidden" id="nIdEstado" name="nIdEstado" value="1" />
		<input type="hidden" id="cIdUniMed" name="cIdUniMed" value="SRV" />
		<input type="hidden" id="cIdContratoDefinitivo" name="cIdContratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
  	</form>
  </body>
</html>
