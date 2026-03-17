<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@page	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String cUR = "";
	String cIdTipo = "";
	String roles="";
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
	cUR = usuario.getU_UR();
%>

<!DOCTYPE html>
<html>
  <head>
    
    <title>My JSP 'AmpliacionContratoCap4.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" charset="utf-8">
		var oTableMP="";
		var roles="";
		$(document).ready(function() {
			<%
				int imgAprobar=1;
				int imgDevolver=0;
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				Map botones=nb.getBotones(roles,"ContratoCap4","AmpliacionContratoCap4");
					Iterator btn = botones.entrySet().iterator();
					while (btn.hasNext()) {
						Map.Entry b = (Map.Entry)btn.next();%>
						$("#<%=b.getValue()%>").attr("disabled", true);<%
						String img=(String) b.getValue();
						
					}
			%>
			roles="<%=roles%>";
			$("#dtblDetalleAmpliacionesMaster").css("display","none");
			$("#ddt_grabaprecompD").css("display","none");
			$("#ddt_grabaprecompE").css("display","none");
			$("#ddt_urcc").css("display","none");
			ocultarBotones();
			initQuerys();
			$('#tblAmpliaciones').on('dblclick','tr', function() {
				limpiaDatosAmpliacion();
				$("#dtblDetalleAmpliacionesMaster").css("display","");
				$(oTablaAmpliaciones.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
				
				var aTrs = $('#tblAmpliaciones').dataTable().fnGetNodes();           
				for ( var i=aTrs.length ; i>=0; i-- ){         
					if ($(aTrs[i]).hasClass('row_selected')){
						        
						var nTr = $('#tblAmpliaciones').dataTable().fnGetData(aTrs[i]);
						$("#nIdAmpliacion").val(nTr[0]);
						$("#cEstadoAmpliacion").val(nTr[2]);
						muestraBotones();
						$("#inpTotalCalendarizado").val(0);
						$("#cIdOficioPrecompromiso").val(nTr[1]);
						$("#cIdContrato").val($("#cIdContratoV").val()+"-AMP-"+nTr[0]);
						$("#nCantidadAmpliacion").val(nTr[3]);
						$("#mMontoAmpliacion").val(nTr[4]);
						$("#cIdUsuarioCreacionAmp").val(nTr[5]);
						$("#nFolioPrecomAmp").val(nTr[7]);
						$("#FolioPrecomAmp").val(nTr[8]);
						$("#cuentaDisponible").val(nTr[9]);
						$("#cDocumentoDefinitivo").val($("#cIdContrato").val());
						$("#cDocumentoDefinitivoAmpliacion").val($("#cContratoDefinitivo").val()+"-AMP-"+nTr[0]);
						if(parseInt(nTr[9],10)>2){
							$("#inpTotalFaltante").val(0);
						}else{
							$("#inpTotalFaltante").val(nTr[3]);
						}
						activacheck();
						vcontrato=$("#cIdContrato").val();
						if(nTr[2].toString().toUpperCase() != "CAPTURADO"){
							$("#btnAgregarAmpliacionDetalle").attr("disabled","true");
							$("#checkDispRadicado").attr("disabled","true");
							if(nTr[2].toString().toUpperCase() == "EN SAI SIN PRESUPUESTO" || nTr[2].toString().toUpperCase() == "RECHAZADO")
								//$("#btnEditarAmpliacionDetalle").removeAttr("disabled");
								document.getElementById("btnEditarAmpliacionDetalle").disabled = false;
							else
								$("#btnEditarAmpliacionDetalle").attr("disabled","true");
						}					
						else{
							document.getElementById("btnAgregarAmpliacionDetalle").disabled = false;
							$("#btnEditarAmpliacionDetalle").attr("disabled","true");
							//$("#checkDispRadicado").removeAttr("disabled");
							document.getElementById("checkDispRadicado").disabled = false;
						}					
					}
				}
			//queryFormPost("mFolioPrecompromisoReadAmpliacionContrato", {async: false});
			//	vfolio=$("#nFolioPreCompromiso").val();
				$("#tblDetalleAmpliaciones").css("display", "");
				if($("#cEstadoAmpliacion").val() == "EN SAI SIN PRESUPUESTO" || $("#cEstadoAmpliacion").val() == "RECHAZADO" ){
					recargaTablaAmpliacionDetalle();
					cargaSuficiencias();
					$("#dtblSuficienciaDetalleAmpliaciones").css("display","");
				}
				else if($("#cEstadoAmpliacion").val() == "EN SAI PRESUPUESTADO"){
					recargaTablaAmpliacionDetallePrecompromiso();
					//recargaTablaAmpliacionDetalle();
					$("#dtblSuficienciaDetalleAmpliaciones").css("display","none");
				}
				else if($("#cEstadoAmpliacion").val() == "APROBADO"){
				$("#dtblSuficienciaDetalleAmpliaciones").css("display","none");
				recargaTablaAmpliacionDetallePrecompromiso();
				//habilitaMeses($('#tblDetalleAmpliaciones').dataTable());
				}
				else{
					recargaTablaAmpliacionDetalle();
					$("#dtblSuficienciaDetalleAmpliaciones").css("display","none");
				}
				
				loadURCC();
				var nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoContrato)%>;
				if(nIdEstado == 1)
					$("#preCompromisoContrato").css("display", "none");
				else
					$("#preCompromisoContrato").css("display", "block");
			});
			
		});//Fin del document ready
		function initQuerys(){
			queryFormPost("obtieneDatosContCap4", {async : false,
				callback: function(){
					$("#totalNetoMax").formatCurrency();
					$("#mMontoActualContrato").formatCurrency();
					muestraLineas();
					$("#cIdContratoV").val($("#cIdTipoContrato").val()+'-'+$("#cIdUnidadEjecutora").val()+'-'+$("#nIdConsecutivo").val());
				}
			});
		}
		function muestraLineas(){
			if($("#nIdTipoActividadEconomica").val()==1){
				$("#divTblLineasBienes").css("display","none");
				$("#divTblLineasServ").css("display","block");
				muestraLineasServ();
			}else{
				$("#divTblLineasBienes").css("display","block");
				$("#divTblLineasServ").css("display","none");
				muestraLineasBienes();
			}
			initDataTable();
		}
		function muestraLineasBienes(){
			var qw="1=1 and cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'" ;
			oTableLineasBienes = $("#tblLineasBienes").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
        		sScrollY : "100%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mPartidasContCap4&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 2, "asc" ]],
				aoColumns: [
					{sName: "cIdContratoDefinitivo",bVisible: false,bSearchable: false},
					{sName: "cIdRFC",bVisible: false,bSearchable: false},
					{sName: "nIdLineaConsolidado"},
					{sName: "descripcion"},
					{sName: "ncantidadMinima"},
					{sName: "ncantidadLineaMax"},
					{sName: "nCantidadDisponible"},
					{sName: "nCantidadAmpliacion"}
					
					
				]
			});
		}
		function muestraLineasServ(){
			var qw="1=1 and cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'" ;
			oTableLineasServ = $("#tblLineasServ").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
        		sScrollY : "100%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mPartidasContCap4&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 2, "asc" ]],
				aoColumns: [
					{sName: "cIdContratoDefinitivo",bVisible: false,bSearchable: false},
					{sName: "cIdRFC",bVisible: false,bSearchable: false},
					{sName: "nIdLineaConsolidado"},
					{sName: "descripcion"},
					{sName: "mMontoNetoMinimoLinea"},
					{sName: "mMontoNetoMaximoLinea"},
					{sName: "mMontoNetoDisponible"},
					{sName: "mMontoNetoAmpliacion"}
					
					
				]
			});
		}
		function onlyNumbers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			var strCheck = '-0123456789.';
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
			return true; 
		}
		function onlyNumbers2(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			var strCheck = '0123456789';
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
			return true; 
		}
		function cambiafrmt( fld )	{
		   	var vcompr = $("#mComprometido").val();
		   	var vfld = $("#" + fld.id).val();
		   	if (vfld == "")
		   		vfld = '0';
			vcompr = quitaFmt( vcompr );
			vfld=quitaFmt(vfld);
		   	$("#mComprometido").val( parseFloat(vcompr) + parseFloat( vfld ) );
			$("#" + fld.id).formatCurrency();
			$("#mComprometido").formatCurrency();
		}
		function initDataTable(){
			$("#cEstadoAmpliacion").val("");
			$("#nIdAmpliacion").val("");
			oTablaAmpliaciones=$('#tblAmpliaciones').dataTable({         
				bAutoWidth : true,
				bScrollCollapse: true,
				bDestroy: true,		
				iDisplayLength: 25,
				sScrollX: "100%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mContratoCap4Ampliaciones('"+$("#cIdContratoDefinitivo").val()+"','"+$("#cIdUnidadEjecutoraSolicitud").val()+"')",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				//aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "nIdAmpliacion", bVisible:false },
					{ sName: "cIdOficioPrecompromiso" },
					{ sName: "cEstado" },
					{ sName: "cantidadAmpliacion" },
					{ sName: "montoAmpliacion" },
					{ sName: "cIdUsuarioCreacion" },
					{ sName: "cIdUsuarioCancelacion" },
					{ sName: "ConsecutivoPRECOMP" , bVisible:false},
					{ sName: "C_FOLIO_PRE" , bVisible:false},
					{ sName: "cNumCuentaDisp" , bVisible:false}
					//{ sName: "Guarda" }
				]
			});
			
		}
		function ocultarBotones(){
			$("#imgAprobarAmpliacion").hide();
			$("#imgDevolverAmpliacionPres").hide();
			$("#imgAprobarpreCompromisoPed").hide();
			$("#imgAprobarCompromisoContAmp").hide();
			$("#imgDevolverAmpliacion").hide();
			$("#imgEliminarAmpliacion").hide();
			
			$("#btnEditarAmpliacionDetalle").hide();
			$("#nIdClaveEP").hide();
			$("#btnAgregarAmpliacionDetalle").hide();
		}
		function muestraBotones(){
			ocultarBotones();
			$("#tblAyuda").show();
			if($("#cEstadoAmpliacion").val()=='CAPTURADO'){
				$("#imgAprobarAmpliacion").show();
				$("#imgEliminarAmpliacion").show();
				$("#nIdClaveEP").show();
				$("#btnAgregarAmpliacionDetalle").show();
			}
			if($("#cEstadoAmpliacion").val()=='EN SAI SIN PRESUPUESTO'){
				$("#imgDevolverAmpliacionPres").show();
				$("#imgAprobarpreCompromisoPed").show();
				$("#btnEditarAmpliacionDetalle").show();
			}
			if($("#cEstadoAmpliacion").val()=='EN SAI PRESUPUESTADO'){
				$("#imgAprobarCompromisoContAmp").show();
				$("#imgDevolverAmpliacion").show();
			}
			if($("#cEstadoAmpliacion").val()=='EN SAI PRESUPUESTADO' || $("#cEstadoAmpliacion").val()=='APROBADO'){
				$("#tblAyuda").hide();
			}
		}
		function nuevaAmpliacion(){
			var cadenaLineaConsCant="";
			var token="";
			
			if($("#nIdTipoActividadEconomica").val()==2  ){
				//Para bienes
				var aTrs = $('#tblLineasBienes').dataTable().fnGetNodes();
				var cantDisp=0;
				for ( var i=0 ; i<aTrs.length; i++ )     
				{
					var nTr = $('#tblLineasBienes').dataTable().fnGetData(aTrs[i]);
					cantDisp=nTr[6];
					var cantCapturado=$("#nCantidadIncremento_"+nTr[2]).val();
					if(parseInt(cantCapturado,10)<=parseInt(cantDisp,10) && parseInt(cantCapturado,10)>0){
						cadenaLineaConsCant=cadenaLineaConsCant+token+nTr[2]+'-'+cantCapturado;
						token=",";
					}else{
						if(parseFloat(cantCapturado)>0){
							swal("En la linea "+nTr[2]+" sobrepasaste el monto disponible.",{icon:"warning",button: "Cerrar"});
							return;
						}
					}
				}
				if(cadenaLineaConsCant==''){
					swal("No hay nada que agregar, favor de capturar el monto de la nueva ampliación.",{icon:"warning",button: "Cerrar"});
					return;
				}
				$("#cadenaLineaCantidad").val(cadenaLineaConsCant);
				queryFormPost("insertAmpContratoCap4", {async: false});
				initDataTable();
				ocultarBotones();
				muestraLineas();
				$("#dtblDetalleAmpliacionesMaster").css("display","none");
			}else{//Para Servicios
				var aTrs = $('#tblLineasServ').dataTable().fnGetNodes();
				var montoDisp=0;
				for ( var i=0 ; i<aTrs.length; i++ )     
				{  
					var nTr = $('#tblLineasServ').dataTable().fnGetData(aTrs[i]);
					montoDisp=nTr[6];
					montoDisp = montoDisp.replace("$", "");
					montoDisp = montoDisp.replace(/,/g, "");
					var montoCapturado=$("#mMontoNetoIncremento_"+nTr[2]).val();
					montoCapturado=montoCapturado.replace("$", "");
					montoCapturado = montoCapturado.replace(/,/g, "");
					montoCapturado=parseFloat(montoCapturado);
					montoCapturado = montoCapturado.toFixed(2);
					if(parseFloat(montoCapturado)<=parseFloat(montoDisp) && parseFloat(montoCapturado)>0.0){
						cadenaLineaConsCant=cadenaLineaConsCant+token+nTr[2]+'-'+montoCapturado;
						token=",";
					}else{
						if(parseFloat(montoCapturado)>0){
							swal("En la linea "+nTr[2]+" sobrepasaste el monto disponible.",{icon:"warning",button: "Cerrar"});
							return;
						}
					}
				}
				if(cadenaLineaConsCant==''){
					swal("No hay nada que agregar, favor de capturar la cantidad de bienes de la nueva ampliación .",{icon:"warning",button: "Cerrar"});
					return;
				}
				$("#cadenaLineaCantidad").val(cadenaLineaConsCant);
				queryFormPost("insertAmpContratoCap4", {async: false});
				initDataTable();
				ocultarBotones();
				muestraLineas();
				$("#dtblDetalleAmpliacionesMaster").css("display","none");
			}
						
			
		}
		
		function limpiaDatosAmpliacion(){
			$("#ep").val("");
		}
		function muestraDispRadicado(){
			swal({
				title: "",
				text: 'Si desea activar o desactivar el check se perderan las ep´s agregadas, ¿desea continuar?',
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
				},
			}).then((continuar) => {
				if (!continuar) {
					activacheck();
					return;
				}else{
					queryFormPost("deleteEPAmpliacionContrato",{async:false});
					$("#cuentaDisponible").val('82106');	
					if($('#checkDispRadicado').is(':checked')){
						$("#cuentaDisponible").val('82109');
					}
					recargaTablaAmpliacionDetalle();
					queryFormPost("updateCTAContratoCap4Ampliacion",{async:false});
				}
			});
		}
		function activacheck(){
			$("#esRadicado").val('N');
			$("#checkDispRadicado").attr("checked",false);
			if($("#cuentaDisponible").val()=='82109'){
				$("#checkDispRadicado").attr("checked",true);
				$("#esRadicado").val('S');
			}
		}
		function recargaTablaAmpliacionDetalle(){
			oTablaAmpliacionesDetalle=$('#tblDetalleAmpliaciones').dataTable({         
				bAutoWidth : true,
				bScrollCollapse: true,
				bDestroy: true,		
				iDisplayLength: 25,
				sScrollX: "100%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mContratoCap4AmpliacionesDetalle('"+$("#cIdContratoDefinitivo").val()+"',"+$("#nIdAmpliacion").val()+")",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ],[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "cClaveInterna" },
					{ sName: "cClaveEP" },
					{ sName: "compromiso01",bSortable: false },
					{ sName: "compromiso02",bSortable: false },
					{ sName: "compromiso03",bSortable: false },
					{ sName: "compromiso04",bSortable: false },
					{ sName: "compromiso05",bSortable: false },
					{ sName: "compromiso06",bSortable: false },
					{ sName: "compromiso07",bSortable: false },
					{ sName: "compromiso08",bSortable: false },
					{ sName: "compromiso09",bSortable: false },
					{ sName: "compromiso10",bSortable: false },
					{ sName: "compromiso11",bSortable: false },
					{ sName: "compromiso12",bSortable: false },
					{ sName: "cEliminar" },
					{ sName: "nIdConsecutivoAmpliacionDetalle", bVisible:false },
					{ sName: "centroContable", bVisible:false }
				]
			});
			deshabilitaMeses($('#tblDetalleAmpliaciones').dataTable());
		}
		function deshabilitaMeses(oTableLocal){
			var i=$("#mesDisponible").val();
			i=parseInt(i,10);
			for(i; i>1;i--){
				oTableLocal.fnSetColumnVis(i,false);
			}
		}
		function loadURCC(){
			var campos = "'" + $("#cIdContratoDefinitivo").val() + "'," + $("#nIdAmpliacion").val();
			
			oTableUrcc=$('#dt_urcc').dataTable( {
					bPaginate: false,
	       			bLengthChange: false,
	       			bFilter: false,
	       			bInfo: false,
					bAutoWidth: false,
					sScrollY: 100,
					bScrollCollapse: true,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,   
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mUrccContratoAmpliacion(" + campos + ")",
					aoColumns: [
						{ sName: "centroContable"},
						{ sName: "ur" }
					]
			}) ;
		}
		function apruebaAmpliacion(){
			if($("#nIdAmpliacion").val() != ""){
				if($("#cEstado").val().indexOf("APROBADO") >= 0){
					if($("#cEstadoAmpliacion").val().toUpperCase() == "CAPTURADO"){
						var oTable = $('#tblDetalleAmpliaciones').dataTable();
						var aTrs = oTable.fnGetNodes();
						if(aTrs.length==0){
							swal("Debe de Agregar al menos una Estructura Presupuestal.",{icon:"warning",button: "Cerrar"});
							return false;
						}
						swal({
							title: "",
							text: "\xBFEst\xE1s seguro de aprobar la ampliaci\xF3n?",
							icon: "info",
							buttons: {
								confirm : "Aceptar",
								cancel: "Cancelar"
							},
						}).then((continuar) => {
							if (!continuar) {
								return;
							}else{
								ocultarBotones();
								var proc=""+$("#cEjercicio").val()
									+","+$("#cIdContratoDefinitivo").val()
									+",contrato"
									+","+$("#nIdAmpliacion").val();
								//Llama a AmpliacionesServlet para llamar el stored procedure que aprueba el Contrato
								 $.ajax({url: '../../servlet/ContratoCap4Servlet'
									, type:'post' , async: false,data:'operacion=8&cEjercicio='+$("#cEjercicio").val()+'&cIdContratoDefinitivo='+$("#cIdContratoDefinitivo").val()
									+'&nIdAmpliacion='+$("#nIdAmpliacion").val()
									, dataType: 'json',
					        		mimeType: 'application/json', success: 
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
													window.location = "ContratoCap4.jsp?tab=7";
												}
										});
										
									}
								});
							}
						});
					}
				}else{
					swal("Para aprobar una ampliaci\xF3n debe estar aprobado el contrato.",{icon:"warning",button: "Cerrar"});
				}
			}else{
				swal("Debe seleccionar una ampliaci\xF3n.",{icon:"warning",button: "Cerrar"});
			}
		}
		function eliminaAmpliacion(){
			//alert($("#U_LOGIN").val()+"=="+$("#cIdUsuarioCreacionAmp").val());return;
			if($("#nIdAmpliacion").val() != ""){
				if((roles.indexOf("ADMIN_RECMAT") >= 0) ||(roles.indexOf("JEFES") >= 0) || ($("#U_LOGIN").val()==$("#cIdUsuarioCreacionAmp").val()) ){
					if($("#cEstadoAmpliacion").val().toUpperCase() == "CAPTURADO"){
						ocultarBotones();
						$("#nIdEstadoAmpliacion").val(6);
						queryFormPost("mAnulaAmpliacionContratoCap4", {async: false});
						initDataTable();
						muestraLineas();
						$("#dtblDetalleAmpliacionesMaster").css("display","none");
					}
					else{
						swal("La ampliaci\xF3n no se puede eliminar, su estado no lo permite.",{icon:"warning",button: "Cerrar"});
					}
				}
				else{
					swal("El usuario no puede realizar esta accion.",{icon:"warning",button: "Cerrar"});
				}
			}
			else{
				swal("Debe seleccionar una ampliaci\xF3n.",{icon:"warning",button: "Cerrar"});
			}
		}
		function devuelveAmpliacion(){
			if($("#nIdAmpliacion").val() != ""){
				if($("#cEstadoAmpliacion").val().toString() == "EN SAI SIN PRESUPUESTO" ){
					ocultarBotones();
												
					$.ajax({url: '../../servlet/ContratoCap4Servlet'
						, type:'post' , async: false,data:'operacion=9&cEjercicio='+$("#cEjercicio").val()
						+'&cIdContratoDefinitivo='+$("#cIdContratoDefinitivo").val()+"-AMP-"+$("#nIdAmpliacion").val()
						+'&nEsDescentralizado=0&esRadicado='+$("#esRadicado").val()+'&nIdAmpliacion='+$("#nIdAmpliacion").val()
						+'&descripPoliza=Devolución, Se devuelve la amplicaión del contrato', dataType: 'json',
		        		mimeType: 'application/json', success: 
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
										window.location = "ContratoCap4.jsp?tab=7";
									}
							});
						}
					});				
				}
				else{
					swal("No se puede devolver el presupuesto de la ampliaci\xF3n, su estatus no lo permite.",{icon:"warning",button: "Cerrar"});
				}
			}
			else{
				swal("Debe seleccionar una ampliaci\xF3n.",{icon:"warning",button: "Cerrar"});
			}
		}
		function buscaClaveEP(){
			window.open('../MultiReporteGridSacel.jsp?cIdUsuarioCreacion=' + $('#cIdUsuarioCreacion').val() 
					+ '&cIdDocumento=' + $('#cIdContratoDefinitivo').val() + '&cIdRFC=' + $('#cIdRFC').val()
					 +'&cuentaDisponible=' + $('#cuentaDisponible').val()+'&isContratoCap4=1'
					, 'MultiReporteGridSacel', 'status=1, width=900px, height=680px, left=100px');
			return false;
		}
		function validarGuardaAmpliacionDetalle(){
			if($("#nIdAmpliacion").val() == ""){
				swal("Debe seleccionar una ampliaci\xF3n.",{icon:"warning",button: "Cerrar"});
				return;
			}
			if($("#ep").val() == ""){
				swal("Debe seleccionar una Clave.",{icon:"warning",button: "Cerrar"});
				return;
			}
			var aTrs = $('#tblDetalleAmpliaciones').dataTable().fnGetNodes();
			if(aTrs.length > 0){        
				for ( var i=aTrs.length ; i>=0; i-- ){
					var nTr = $('#tblDetalleAmpliaciones').dataTable().fnGetData(aTrs[i]);
					if(nTr[1]+"."+nTr[0] == $("#ep").val()){
						swal("La EP ya esta agregada, favor de seleccionar otra.",{icon:"warning",button: "Cerrar"});
						return;
					}					
				}
			}
			queryFormPost("insertAmpliacionDetalleContratoCap4", {async: false, 
				callback: function(){
					recargaTablaAmpliacionDetalle();
					$("#ep").val("");
				}
			});
		}
		function eliminaAmpliacionDetalle(nIdAmpliacionDetalle){
			swal({
				title: "",
				text: "\xBFEst\xE1s seguro de eliminar la estructura presupuestal?",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
				},
			}).then((continuar) => {
				if (!continuar) {
					return;
				}else{
					$("#nIdAmpliacionDetalle").val(nIdAmpliacionDetalle);
					queryFormPost("eliminaEPContratoCap4Ampl", {async: false});
					//initDataTable();
					recargaTablaAmpliacionDetalle();
					swal("El detalle de ampliaci\xF3n se elimino correctamente.",{icon:"success",button: "Cerrar"});
				}
			});
		}
		function editCeldasDetalleAmpliaciones(){
	       	var nRow = editPrecompromiso( $('#tblDetalleAmpliaciones').dataTable() ) ; 
	   	}
	   	
		function editPrecompromiso( oTableLocal ) {
			//funcion para agregar los inputs a la tabla
			var i,j,descMes, nomMes,nColumna;
			var aData;
			var aTrs = oTableLocal.fnGetNodes();
			for (i=1 ; i<=aTrs.length ; i++ ){
				mes=parseInt($("#mesDisponible").val(),10);
				if(mes==0)
					mes=mes+1;
			    aData = oTableLocal.fnGetData(i-1);
			    nColumna=aData.length-mes-1;//dos columnas al final ocultas
			    for(j=2; j<nColumna-1; j++){
			    	nomMes='mes'+mes;
			    	descMes='mes'+mes+'-'+i;
			   		$("#tblDetalleAmpliaciones").children().children()[i].children[j].innerHTML = '<input class="form-control" type="text" id="' + descMes + '" name="'+nomMes+'" onchange="valSufic(this)"  onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[mes+1]+'" onKeyPress="return(onlyNumbers(event))">';
			    	mes++;	
			    }
		    }
		}
		function quitaFmt( val ) {
		   	val = val.replace("$", "");
		   	val = val.replace(/,/g, '');
		   	if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
		   	}
		   	return val;
		}
		function Sinfrmt( fld )	{
		   	var valcol = fld.value ;
		   	var vcompr = $("#mComprometido").val();
		   	vcompr = quitaFmt( vcompr );
		   	valcol = quitaFmt( valcol );
			$("#" + fld.id).val( valcol );
		   	fld.select();
			$("#mComprometido").val( parseFloat( vcompr ) - parseFloat( valcol ) );
			$("#mComprometido").formatCurrency();
		}
		function valSufic( fld ){
			var valor = $("#" + fld.id).val();
			//validacion para que solo se permitan números
			valor=quitaFmt(valor);
			if(isNaN(valor)){
				swal("Ingrese solo valores numéricos",{icon:"warning",button: "Cerrar"});
				$("#" + fld.id).val( "0" );
				return false;
			}
			if(valor<0){
				swal("No se pueden ingresar valores negativos",{icon:"warning",button: "Cerrar"});
		    	$("#" + fld.id).val( "0" );
		    	return false;
			}
			//obtiene la suficiencia de la EP y el valor del precompromiso ingresado para comparar
			var oTableLocal = $("#tblSuficienciaDetalleAmpliaciones").dataTable();
			var vimptot = $("#mImporteTotal").val();
			var vcompr  = $("#mComprometido").val();
			vimptot = quitaFmt( vimptot );
			vcompr = quitaFmt( vcompr );
			vcompT = parseFloat(vimptot)-parseFloat(vcompr);
			var nren = parseInt(fld.id.substring(fld.id.lastIndexOf("-") + 1),10) -1;
			var ncol = parseInt( fld.id.substring(5, 3),10 ) + 1 ;
			var aData = oTableLocal.fnGetData( nren );
			if (valor == "") {
				valor = '0';
				$("#" + fld.id).val( "0" );
			}
			var valsuf = parseFloat( aData[ ncol ] );
			if (parseFloat(valor) > valsuf) {
		    	swal("No hay Suficicencia Mensual en la Clave Presupuestal",{icon:"warning",button: "Cerrar"});
		    	$("#" + fld.id).val( "0" );
		    	return false;
		    }
			vcompr=parseFloat(vcompr);
			valor=parseFloat(valor);
			vimptot=parseFloat(vimptot);
			if ((vcompr.toFixed(2)+valor.toFixed(2))>  vimptot) {
		    	swal("El Compromiso Actual Excede al Saldo Compromiso",{icon:"warning",button: "Cerrar"});
		    	$("#" + fld.id).val( "0" );
		    	return false;
		    }
			vcompT = vimptot-valor-vcompr;
			//Redondea a 2 decimales para evitar conflictos con la notación científica para valores muy pequeños [xE-y]
			vcompT=vcompT.toFixed(2);
			$("#difPrecompromiso").val(vcompT);
			$("#difPrecompromiso").formatCurrency();
			totalCalendarizado();
		}
		function totalCalendarizado(){
			var montoTotal = 0;
			var nRows = $("#tblDetalleAmpliaciones tr").length -1;
			var oTable = $('#tblDetalleAmpliaciones').dataTable();
			var aTrs = oTable.fnGetNodes();
			
			//Obtine las columnas de las que va a precomprometer
			var j,nColums=12-parseInt($("#mesDisponible").val(),10);
			for ( var i=0 ; i<nRows ; i++ ) {
				var aData = oTable.fnGetData( i );
				var jqInputs = $('input', aTrs[i] );
				if (jqInputs.length > 0) {
					for ( j=0 ; j <= nColums ; j++ ) {
						var vep = aData[ 1 ] + "." + $.trim(aData[ 0 ]) ;
						var vimporteP = jqInputs[ j ].value ;
						vimporteP = quitaFmt(vimporteP);
						if (parseFloat(vimporteP) != 0 ) {
							var vimporteN = vimporteP * -1 ; 
							vMes = parseInt($("#mesDisponible").val(),10)+j;
							montoTotal = montoTotal+parseFloat(vimporteP);
						}
					}
				}
			}
			montoTotal = montoTotal.toFixed(2);
			var montoAmpli=quitaFmtExpresion($("#mMontoAmpliacion").val());
			//montoAmpli = montoAmpli.toFixed(2);
			$("#inpTotalFaltante").val(parseFloat(montoAmpli)-parseFloat(montoTotal));
			
			$("#inpTotalCalendarizado").val(montoTotal);
			$("#inpTotalCalendarizado").formatCurrency();
			
			$("#inpTotalFaltante").formatCurrency();
			
		}
		function quitaFmtExpresion( val ) {
		   	val = val.replace("$", "");
		   	val = val.replace(/,/g, "");
	
		   	if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
		   	}
		   	return val;
		}
		function cargaSuficiencias(){
			var qw=" nIdConsecutivoAmpliacion="+$("#nIdAmpliacion").val()
			+ " and cIdContrato='"+$("#cIdContratoDefinitivo").val()+"' and nCuentaP='"+$("#cuentaDisponible").val()+"'";
			var oTable=$('#tblSuficienciaDetalleAmpliaciones').dataTable( {
				bPaginate: false,
	   			bLengthChange: false,
	   			bFilter: false,
	   			bInfo: false,
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
				}},
	   			bAutoWidth: true,
				sScrollX: 100,
				sScrollY: 100,
		        bScrollCollapse: true,
				bJQueryUI: true,
				bDestroy : true,
				bServerSide: true,
				//Query de Financiero, también se utiliza para Compromiso
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vDisponibleEPAmpliacion&qw="+qw,
				aaSorting: [[ 0, "asc" ],[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "ClaveInterna",bSortable: false },
					{ sName: "ClaveSIAFF" ,bSortable: false},
					//{ sName: "ClaveInterna",bSortable: false },
					{ sName: "MontoEnero" ,bSortable: false},
					{ sName: "MontoFebrero" ,bSortable: false},
					{ sName: "MontoMarzo" ,bSortable: false},
					{ sName: "MontoAbril",bSortable: false },
					{ sName: "MontoMayo" ,bSortable: false},
					{ sName: "MontoJunio",bSortable: false },
					{ sName: "MontoJulio" ,bSortable: false},
					{ sName: "MontoAgosto" ,bSortable: false},
					{ sName: "MontoSeptiembre" ,bSortable: false},
					{ sName: "MontoOctubre",bSortable: false },
					{ sName: "MontoNoviembre" ,bSortable: false},
					{ sName: "MontoDiciembre" ,bSortable: false},
					{ sName: "MontoAnual",bSortable: false },
					{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false } ],
				fnInitComplete: function(oSettings, json) {
				}
	           } );
			deshabilitaMeses($('#tblSuficienciaDetalleAmpliaciones').dataTable());
		}
		function validaPrecompromisoDes(){
	    	var montoTotal = 0;
			//Genera en una tabla auxiliar el detalle del precompromiso
			var vdocren = 1 ; //Numero del renglon
			//obtiene el número de filas de la tabla de precompromiso
			var nRows = $("#tblDetalleAmpliaciones tr").length -1;
			var oTable = $('#tblDetalleAmpliaciones').dataTable();
			var aTrs = oTable.fnGetNodes();
			
			//Obtine las columnas de las que va a precomprometer
			var j,nColums=12-parseInt($("#mesDisponible").val(),10);
			for ( var i=0 ; i<nRows ; i++ ) {
				var aData = oTable.fnGetData( i );
				var jqInputs = $('input', aTrs[i] );
				if (jqInputs.length > 0) {
					for ( j=0 ; j <= nColums ; j++ ) {
						var vep = aData[ 1 ] + "." + $.trim(aData[ 0 ]) ;
						var vimporteP = jqInputs[ j ].value ;
						vimporteP = quitaFmt(vimporteP);
						if (parseFloat(vimporteP) != 0 ) {
							var vimporteN = vimporteP * -1 ; 
							vMes = parseInt($("#mesDisponible").val(),10)+j;
							montoTotal = montoTotal+parseFloat(vimporteP);
							vdocren++ ;
						}
					}
				}
			}
			montoTotal = montoTotal.toFixed(2);
			var montoAmpliacion=$("#mMontoAmpliacion").val().replace("$","").replace(",","");
			montoAmpliacion=quitaFmt( montoAmpliacion );
			montoAmpliacion=parseFloat(montoAmpliacion).toFixed(2);
			if(parseFloat(montoTotal)< parseFloat(montoAmpliacion) ){
				swal("El monto Calendarizado es menor al monto de la ampliación. Los Montos deben de ser iguales.",{icon:"warning",button: "Cerrar"});
				return false;
			}
			//alert("montoTotal: "+parseFloat(montoTotal)+" Monto ampli:"+parseFloat(montoAmpliacion));
			if(parseFloat(montoTotal)> parseFloat(montoAmpliacion) ){
				swal("El monto Calendarizado es mayor al monto de la ampliación. Los Montos deben de ser iguales.",{icon:"warning",button: "Cerrar"});
				return false;
			}
			var montoActual=quitaFmtExpresion($("#mMontoActualContrato").val());
			montoActual=parseFloat(montoActual).toFixed(2);
			var montoMaximo=quitaFmtExpresion($("#totalNetoMax").val());
			montoMaximo=parseFloat(montoMaximo).toFixed(2);
			montoTotalMontoActual=parseFloat(montoTotal)+parseFloat(montoActual);
			montoTotalMontoActual=parseFloat(montoTotalMontoActual).toFixed(2);
			//alert(montoTotalMontoActual+" ;  "+montoMaximo);
			if( parseFloat(montoTotalMontoActual) > parseFloat(montoMaximo) ){
				swal("El monto total del contrato mas la ampliaci\xF3n supera el m\xE1ximo.",{icon:"warning",button: "Cerrar"});
				return false;
			}
			//return;
			//En caso que no haya ningun valor en el calendario
			if (vdocren == 1) {
				swal("No se ha Calendarizado Recurso.",{icon:"warning",button: "Cerrar"});
				return false;
			}
			return true;
	    }
		function precomprometer(){
			var  imgAprobar='<%=imgAprobar%>';
			if ($("#nIdAmpliacion").val() != "") {
				if($("#cEstadoAmpliacion").val() == "EN SAI SIN PRESUPUESTO"){	
					if (!validaPrecompromisoDes()){
						return;
					}
					document.getElementById("imgAprobarpreCompromisoPed").disabled = true;
					//crea el array de la tabla de montos
					var arregloDatos=ArrayTabla();
					ocultarBotones();
					//ajax Precomprometer en java
					$.ajax({url: '../../servlet/ContratoCap4Servlet'
						, type:'post' , async: false,data:'operacion=10&tablaDatos='+arregloDatos+'&cEjercicio='+$("#cEjercicio").val()
						+'&cIdContratoDefinitivo='+$("#cIdContratoDefinitivo").val()+"-AMP-"+$("#nIdAmpliacion").val()
						+'&nEsDescentralizado=0'+'&esRadicado='+$("#esRadicado").val()
						+'&nIdAmpliacion='+$("#nIdAmpliacion").val()
						+'&descripPoliza=PRECOMPROMISO, AMPLIACIÓN DE CONTRATOS CAPITULO 4000', dataType: 'json',
		        		mimeType: 'application/json', success: 
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
										window.location = "ContratoCap4.jsp?tab=7";
									}
							});
						}
					});
				}
			}else{
				swal("No tienes permisos.",{icon:"warning",button: "Cerrar"});
			}
		}
		function ArrayTabla(){
			var arregloTmp=new Array();
			var arrayFila=new Object();
			
			var aTrs = $('#tblDetalleAmpliaciones').dataTable().fnGetNodes();
			var vimporteP;
			var nTr;
			var jqInputs;
			for ( var i=0 ; i<aTrs.length; i++ )     
			{
				nTr =  $('#tblDetalleAmpliaciones').dataTable().fnGetData(aTrs[i]);
				jqInputs = $('input',aTrs[i] );
				for ( j=0 ; j < jqInputs.length ; j++ ) {
					var k=j;
					vimporteP = jqInputs[j].value ;
					vimporteP = quitaFmt(vimporteP);
					arrayFila=[nTr[1]+'.'+nTr[0],k+1,vimporteP,"|"];
					arregloTmp.push(arrayFila);
				}
				//alert("EP="+arregloTmp[i][0]+"\nmes="+arregloTmp[i][1]+"\nMonto="+arregloTmp[i][2]);
			}
			
			return arregloTmp;
		}
		function recargaTablaAmpliacionDetallePrecompromiso(){			
			oTablaAmpliacionesDetalle=$('#tblDetalleAmpliaciones').dataTable({         
				bAutoWidth : true,
				bScrollCollapse: true,
				bDestroy: true,		
				iDisplayLength: 25,
				sScrollX: "100%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_ConsultaPreCompromisoAmpContCap4( '" + $("#cIdContratoDefinitivo").val() + "'," + $("#nIdAmpliacion").val() + " )",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "ClaveInterna" },
					{ sName: "ClaveSIAFF" },
					{ sName: "compromiso01",bSortable: false },
					{ sName: "compromiso02",bSortable: false },
					{ sName: "compromiso03",bSortable: false },
					{ sName: "compromiso04",bSortable: false },
					{ sName: "compromiso05",bSortable: false },
					{ sName: "compromiso06",bSortable: false },
					{ sName: "compromiso07",bSortable: false },
					{ sName: "compromiso08",bSortable: false },
					{ sName: "compromiso09",bSortable: false },
					{ sName: "compromiso10",bSortable: false },
					{ sName: "compromiso11",bSortable: false },
					{ sName: "compromiso12",bSortable: false },
					{ sName: "EP",bVisible:false },
					{ sName: "cIdContrato", bVisible:false },
					{ sName: "tmp", bVisible:false }
				]
			});
			deshabilitaMeses($('#tblDetalleAmpliaciones').dataTable());
		}
		function devuelvePrecompromiso(){
			if($("#nIdAmpliacion").val() != ""){
				if($("#cEstadoAmpliacion").val().toString() == "EN SAI PRESUPUESTADO"){
					//Llama a AmpliacionesServlet para llamar el stored procedure que aprueba el Contrato
					 $.ajax({url: '../../servlet/ContratoCap4Servlet'
						, type:'post' , async: false,data:'operacion=11&cEjercicio='+$("#cEjercicio").val()
						+'&cIdContratoDefinitivo='+$("#cIdContratoDefinitivo").val()+"-AMP-"+$("#nIdAmpliacion").val()
						+'&ntipoPago=0'+'&cCuentaDisponible='+$("#cuentaDisponible").val()
						+'&nIdAmpliacion='+$("#nIdAmpliacion").val()
						, dataType: 'json',
		        		mimeType: 'application/json', success: 
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
										window.location = "ContratoCap4.jsp?tab=7";
									}
							});
						}
					});
				}else{
					swal("No se puede devolver por el tipo de estatus",{icon:"warning",button: "Cerrar"});
				}
			}else{
				swal("Debe seleccionar una ampliaci\xF3n.",{icon:"warning",button: "Cerrar"});
			}
		}
		function guardarcomprimisoGeneral(){
			if($("#nIdAmpliacion").val() != ""){
				if($("#cEstadoAmpliacion").val().toString() == "EN SAI PRESUPUESTADO"){
					//Llama a AmpliacionesServlet para llamar el stored procedure que aprueba el Contrato
					 $.ajax({url: '../../servlet/ContratoCap4Servlet'
						, type:'post' , async: false,data:'operacion=12&cEjercicio='+$("#cEjercicio").val()
						+'&cIdContratoDefinitivo='+$("#cIdContratoDefinitivo").val()+"-AMP-"+$("#nIdAmpliacion").val()
						+'&ntipoPago=0'+'&cCuentaDisponible='+$("#cuentaDisponible").val()
						+'&nIdAmpliacion='+$("#nIdAmpliacion").val()
						, dataType: 'json',
		        		mimeType: 'application/json', success: 
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
										window.location = "ContratoCap4.jsp?tab=7";
									}
							});
						}
					});
				}else{
					swal("No se puede autorizar por el tipo de estatus",{icon:"warning",button: "Cerrar"});
				}
			}else{
				swal("Debe seleccionar una ampliaci\xF3n.",{icon:"warning",button: "Cerrar"});
			}
		}
	</script>
  </head>
  
  <body>
  <form id="formPresupContCap4">
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
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Ampliaciones</legend>
			<div class="form-group">
				<div class="row g-3 align-items-center">
					<div class="col-auto">
						<label for="totalNetoMax" class="col-form-label">Monto M&aacute;ximo</label>
					</div>
					<div class="col-auto">
					    <input type="text" class="form-control transpInput" id="totalNetoMax" name="totalNetoMax" readonly>
					</div>
				</div>
				<div class="row g-3 align-items-center">
					<div class="col-auto">
						<label for="mMontoActualContrato" class="col-form-label">Monto Actual del Contrato</label>
					</div>
					<div class="col-auto">
						<input type="text" class="form-control transpInput" id="mMontoActualContrato" name="mMontoActualContrato" readonly>
					</div>
				</div>
			</div>
			<div class="form-group" id="divTblLineasServ">
				<div class="row">
					<div class="col">
						<table id="tblLineasServ" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr align="left">
									<th style="display: none;"></th>
									<th style="display: none;"></th>
									<th align="center">Linea<br />Consolidado</th>
									<th align="center">Descripci&oacute;n</th>
									<th align="center">Monto <br /> NetoMinimo</th>
									<th align="center">Monto <br />NetoMaximo</th>
									<th align="center">Monto <br />Por ampliar</th>
									<th align="center">Monto <br /> Ampliación</th> 
							 	</tr> 
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="divTblLineasBienes">
				<div class="row">
					<div class="col">
						<table id="tblLineasBienes" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr align="left">
									<th style="display: none;"></th>
									<th style="display: none;"></th>
									<th align="center">Linea<br />Consolidado</th>
									<th align="center">Descripci&oacute;n</th>
									<th align="center">Cantidad <br /> Minima</th>
									<th align="center">Cantidad <br />Maxima</th>
									<th align="center">Cantidad <br />Por Ampliar</th>
									<th align="center">Cantidad <br /> Ampliación</th>
							 	</tr> 
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceCancelar ui-button ui-corner-all float-right" 	id="imgEliminarAmpliacion" 	name="imgEliminarAmpliacion" 
							value="Anular" onclick="eliminaAmpliacion();" title="Anula Ampliaci&oacute;n"/>
							
							<input type="button" class="btnInterfaceReturn ui-button ui-corner-all float-right" 	id="imgDevolverAmpliacion" name="imgDevolverAmpliacion" 	
							value="Devolver"	onclick="devuelvePrecompromiso();" title="Devuelve Pre-Compromiso de la Ampliaci&oacute;n"/>
							
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarCompromisoContAmp" name="imgAprobarCompromisoContAmp" 	value="Autorizar"	
							onclick="guardarcomprimisoGeneral();" title="Generar Compromiso"/>
							
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarpreCompromisoPed" name="imgAprobarpreCompromisoPed" 	value="Pre-Comprometer"	
							onclick="precomprometer();" title="Pre-Compromete el recurso de la Ampliaci&oacute;n"/>
							
							<input type="button" class="btnInterfaceReturn ui-button ui-corner-all float-right" 	id="imgDevolverAmpliacionPres" name="imgDevolverAmpliacionPres" 	
							value="Devolver"	onclick="devuelveAmpliacion();" title="Devolver la Ampliaci&oacute;n"/>
							
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarAmpliacion" name="imgAprobarAmpliacion" 	value="Aprobar"	
							onclick="apruebaAmpliacion();" title="Aprobar la Ampliaci&oacute;n"/>
							
							<input type="button" class="btnInterfaceAdd ui-button ui-corner-all float-right" 	id="imgNuevaAmpliacion" name="imgNuevaAmpliacion" 	
							value="Agregar"	onclick="nuevaAmpliacion();" title="Agregar Nueva Ampliaci&oacute;n"/>
							
						</div>
					</div>
				</div>
			</div>
			<div class="form-group" id="dtblAmpliaciones">
				<div class="row">
					<div class="col">
						<table id="tblAmpliaciones" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr align="left">
									<th>&nbsp;</th>
									<th>Oficio</th>
									<th>Estado</th>
									<th>Cantidad_Ampl</th>
									<th>Monto_Ampl</th>
									<th>Usuario_Creador</th>
									<th>Usuario_Cancela</th>
	
									<th style="display: none;"></th>
									<th style="display: none;"></th>
									<th style="display: none;"></th>
							 	</tr> 
							</thead>
						</table>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3" id="dtblDetalleAmpliacionesMaster">
			<legend class="w-auto px-2">Detalle Ampliaciones</legend>
			<div class="form-group">
				<div class="row" id="divRadicado" style="display: none;">
					<div class="col-auto">		
						<label class="form-check-label" for="checkDispRadicado">Disponible Radicado </label>
					</div>
					<div class="col-auto">
		  				<input class="form-check-input" type="checkbox" id="checkDispRadicado" name="checkDispRadicado" value="0" onclick="muestraDispRadicado()">
		  			</div>
				</div>
				<div class="row">
					<div class="col-md-6" id="divClave" >
						<label for="ep">Clave.</label>
						<input type="text" class="form-control" placeholder="Seleccione la estructura presupuestal que desea agregar." id="ep" name="ep" readonly>
					</div>
					<div class="col-auto">
						<br>
						<input type="button" id="nIdClaveEP" name="nIdClaveEP" value="..." onclick="buscaClaveEP()" 
						class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-start"  title="Dar clic para mostrar las estructuras presupuestales con recurso disponible."/>
					</div>
					<div class="col-auto">
						<br>
						<input type="button" id="btnAgregarAmpliacionDetalle" name="btnAgregarAmpliacionDetalle" value="Agregar" onclick="validarGuardaAmpliacionDetalle()" class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-start"/>
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input class="btnInterfaceBG ui-button ui-corner-all float-left" type="button" name="btnEditarAmpliacionDetalle" id="btnEditarAmpliacionDetalle" value="Editar" onClick="editCeldasDetalleAmpliaciones();">
					</div>
				</div>
				<div class="row " id="tblAyuda">
					<div class="d-flex flex-row-reverse bd-highlight" >
						<div class="p-2 bd-highlight">
							<div class="input-group">
								<label for="inpTotalFaltante" class="col-form-label">Recurso Faltante</label>
								<div class="col-auto ">
									<input type="text" class="form-control" id="inpTotalFaltante" name="inpTotalFaltante" readonly value="0">
								</div>
							</div>
						</div>
						<div class="p-2 bd-highlight">
							<div class="input-group">
								<label for="inpTotalCalendarizado" class="col-form-label">Total Calendarizado</label>
								<div class="col-auto" >
									<input type="text" class="form-control" id="inpTotalCalendarizado" name="inpTotalCalendarizado" readonly value="0">
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<table id="tblDetalleAmpliaciones" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr align="left">
									<th>#</th>
									<th>EP</th>
									<th>Enero</th>
									<th>Febrero</th>
									<th>Marzo</th>
									<th>Abril</th>
									<th>Mayo</th>
									<th>Junio</th>
									<th>Julio</th>
									<th>Agosto</th>
									<th>Septiembre</th>
									<th>Octubre</th>
									<th>Noviembre</th>
									<th>Diciembre</th>
									<th>&nbsp;</th>
									<th>&nbsp;</th>
									<th>&nbsp;</th>
							 	</tr> 
							</thead>
						</table>
					</div>
				</div>
				<div class="row" id="dtblSuficienciaDetalleAmpliaciones">
					<div class="col">
						<table id="tblSuficienciaDetalleAmpliaciones" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr align="left">
									<th>&nbsp;</th>
									<th>Estructura Program&aacute;tica</th>
									<!--<th>Clave Interna</th>-->
									<th>Enero</th>
									<th>Febrero</th>
									<th>Marzo</th>
									<th>Abril</th>
									<th>Mayo</th>
									<th>Junio</th>
									<th>Julio</th>
									<th>Agosto</th>
									<th>Septiembre</th>
									<th>Octubre</th>
									<th>Noviembre</th>
									<th>Diciembre</th>
									<th>Anual</th>
									<th>Contrato</th>
							 	</tr> 
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="ddt_grabaprecompE">
				<div class="row">
					<div class="col">
						<table id="dt_grabaprecompE" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr align="left">
									<th>nFolioPreCompromisoE</th>
									<th>aEjercicioFiscal</th>
									<th>fCarga</th>
									<th>fAplicacion</th>
									<th>cIdContrato</th>
									<th>cTipoContrato</th>
									<th>cCentroContable</th>
									<th>cRamo</th>
									<th>cUnidadResponsable</th>
									<th>caNoPreCompromiso</th>
									<th>nEnviadoSICOP</th>
									<th>nMes</th>
									<th>fVigencia</th>
							 	</tr> 
							</thead>
							<tbody>
								<tr>
									<td><input type="text" id="nFolioPreCompromisoE" name="nFolioPreCompromisoE"></td>
									<td><input type="text" id="aEjercicioFiscal" name="aEjercicioFiscal"></td>
									<td><input type="text" id="fCarga" name="fCarga"></td>
									<td><input type="text" id="fAplicacion" name="fAplicacion"></td>
									<td><input type="text" id="cIdContrato" name="cIdContrato"></td>
									<td><input type="text" id="cTipoContrato" name="cTipoContrato"></td>
									<td><input type="text" id="cCentroContable" name="cCentroContable"></td>
									<td><input type="text" id="cRamo" name="cRamo" value="16"></td>
									<td><input type="text" id="cUnidadResponsable" name="cUnidadResponsable"></td>
									<td><input type="text" id="caNoPreCompromiso" name="caNoPreCompromiso"></td>
									<td><input type="text" id="nEnviadoSICOP" name="nEnviadoSICOP"></td>
									<td><input type="text" id="nMes" name="nMes"></td>
									<td><input type="text" id="fVigencia" name="fVigencia"></td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="ddt_grabaprecompD">
				<div class="row">
					<div class="col">
						<table id="dt_grabaprecompD" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr align="left">
									<th>nDocRenglon</th>
									<th>EP</th>
									<th>cEvento</th>
									<th>mImporte</th>
									<th>mImporteNegativo</th>
									<th>nFolioPreCompromisoD</th>
									<th>cMes</th>
									<th>cCentroContable</th>
							 	</tr> 
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="ddt_urcc" style="display: none;">
				<div class="row">
					<div class="col">
						<table id="dt_urcc" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr align="left">
									<th>CentroContable</th>
									<th>UnidadEjecutora</th>
							 	</tr> 
							</thead>
						</table>
					</div>
				</div>
			</div>
		</fieldset>	
		<input type="hidden" id="cIdContratoDefinitivo" name="cIdContratoDefinitivo" value="<%=cIdContratoDefinitivo %>" />
		<input type="hidden" id="nIdTipoActividadEconomica" name="nIdTipoActividadEconomica" value="1" />
		<input type="hidden" id="cIdContratoV" name="cIdContratoV" value="" />
		<input type="hidden" name="nIdAmpliacion" id="nIdAmpliacion" />
		<input type="hidden" name="cEstadoAmpliacion" id="cEstadoAmpliacion" />
		<input type="hidden" name="cIdUnidadEjecutoraSolicitud"	id="cIdUnidadEjecutoraSolicitud" value="<%=cUR%>" />
		<input type="hidden" name="cadenaLineaCantidad" id="cadenaLineaCantidad" />
		<input type="hidden" name="cEjercicio" id="cEjercicio" />
		<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" />
		<input type="hidden" name="cIdRFC" id="cIdRFC" />
		<input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" />
		<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuario.getLogin() %>"/>
		<input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" />
		<input type="hidden" name="mesDisponible" id="mesDisponible" value="1" />
		<input type="hidden" name="cEstado" id="cEstado" value="1" />
		<input type="hidden" id="Partida" name="Partida" value="2"/>
		<input type="hidden" id="Partida2" name="Partida2" value="3"/>
		<input type="hidden" id="Partida3" name="Partida3" value="5"/>
		<input type="hidden" id="Partida1" name="Partida1" value="1"/>
		<input type="hidden" id="cuentaDisponible" name="cuentaDisponible" value="82106" />
		<input type="hidden" name="nIdAmpliacionDetalle" id="nIdAmpliacionDetalle" />
		<input type="hidden" name="nIdEstadoAmpliacion" id="nIdEstadoAmpliacion" /> 
		<input type="hidden" name="mImporteTotal" id="mImporteTotal" value="0"> 
		<input type="hidden" name="mComprometido" id="mComprometido" value="0" />
		<input type="hidden" name="difPrecompromiso" id="difPrecompromiso" value="0" />
		<input type="hidden" id="esRadicado" name="esRadicado" value="N" />
		<input type="hidden" id="mMontoAmpliacion" name="mMontoAmpliacion" value="" />
  	</form>
  </body>
</html>
