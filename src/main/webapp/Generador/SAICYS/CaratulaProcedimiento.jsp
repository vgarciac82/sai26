<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
	   return;
	}
	String roles="";
	Map rol =usuario.getRoles();
	String cEjercicio = "";
	String cIdTipoProcedimiento= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	
	if (session.getAttribute(GestionInterface.ATT_ProEjercicio) != null) {
		cEjercicio =(String)session.getAttribute(GestionInterface.ATT_ProEjercicio);
		cIdUnidadEjecutora= (String)session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora);
		cIdTipoProcedimiento=(String)session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento);
		nIdConsecutivo=(String)session.getAttribute(GestionInterface.ATT_ProConsecutivo);
	}
	String unidadUsuarioLogeado="";
	unidadUsuarioLogeado = usuario.getU_UR();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Procedimiento</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Consolidado">
		
		<script type="text/javascript" charset="utf-8">
		var roles='';
		var daTable;
		$(document).ready(function() {
			$("#tbs").val(2);
			showAndHideTabs();
		<%
				
			NegativaPestana NegPestana=new NegativaPestana();
			//NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
			//botones
			
			NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
			///// botones/////
			int imgAdjudicarC=0;
			int imgDesiertoC=0;
			int imgDevolverC=0;
			Iterator it1 = rol.entrySet().iterator();
			Role role = new Role();
			while (it1.hasNext()) {
				Map.Entry r = (Map.Entry)it1.next();
				roles += r.getKey().toString()+",";
			}
			if(roles.length()>0){
				roles = roles.substring(0,roles.length()-1);
			}
			Map botones=nb.getBotones(roles,"Procedimiento","CaratulaProcedimiento");
			Iterator btn = botones.entrySet().iterator();
			while (btn.hasNext()) {
				Map.Entry b = (Map.Entry)btn.next();%>
				$("#<%=b.getValue()%>").attr("disabled", true);<%
				String img=(String) b.getValue();
				if ("imgAdjudicarCaratulaProc".equals(img)){
					imgAdjudicarC=1; 
				}
				if ("imgDesiertoCaratulaProc".equals(img)){
					imgDesiertoC=1;
				}
				if ("imgDevolverCaratulaProc".equals(img)){
					imgDevolverC=1;
				}
				
			}
		%>	
	roles="<%=roles%>";	
	$("#usuarioLogin").val('<%=usuario.getLogin()%>');
	$("#cIdEntidadContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
	$("#cIdUsuario").val('<%=usuario.getLogin()%>');
	$("#usuarioRoleProcedimiento").val('<%=roles%>');		
	querySelectPost("CategoriaRead", "cboCategoria", {async : false});
	$("#usuarioLogin").val('<%=usuario.getLogin()%>');
	muestraInformacionCaratula();
	hideAndShowbuttons();
	if($("#cIdTipoProcedimiento").val().toString()!="PA" && $("#cIdTipoProcedimiento").val().toString()!="PL"){
		$(".arrendamientos_div").attr("disabled", true);
		document.getElementById("fieldsetArrendamiento").style.display = "none";
	}else{
	    querySelectPost("UnidadEjecutoraRead", "cboUnidadEjecutoraInmueble", {async: false });
		querySelectPost("TipoDenominacionInmuebleRead", "cboDenominacionInmueble", {async: false });
		document.getElementById("fieldsetArrendamiento").style.display = "block";
		queryFormPost("llenaCaratulaInmuebles", {async:false});
		if($("#cboDenominacionInmueble").val()=="8"){
		 $("#descripcionOtros").css("visibility","visible");
		}
	}
	
	habilitarPestanas();
	var cadena_campos=$("#cIdConsolidado").val().split('-');
	$("#TipoConsolidado").val(cadena_campos[0]);
	$("#ConsecutivoConsolidado").val(cadena_campos[2]);
	
	$( "#btnGuardarCaratulaProcedimiento" ).button().click(function() {
			if(validaModificarProcedimiento()){
				//se checa si es un procedimiento de arrendamiento y se salta las validaciones de fechas
				var inputTablaFechasProcedimiento = $('input','#tablaFechasProcedimientoCaratula');
				//Procedimiento de arrendamiento
				var numFecha="";
				var numFechaAnt="0";
				if ($("#cIdTipoProcedimiento").val()!="PA" &&  $("#cIdTipoProcedimiento").val()!="PL"){
					var banFechasProcedimiento = true;
					var fechaTem="";
				for(var i=0;i<inputTablaFechasProcedimiento.length;i++){
					if(inputTablaFechasProcedimiento[i].type == "text" || inputTablaFechasProcedimiento[i].type == "TEXT"){
						if(inputTablaFechasProcedimiento[i].id.indexOf("fechaProcedimientoC") >= 0){
							if(inputTablaFechasProcedimiento[i].value == ""){
								swal("Falta insertar algunas fechas.",{icon:"info",button: "Cerrar"});
								banFechasProcedimiento = false;
								return;
							}
							else{
								if(!validaFormatoFecha(inputTablaFechasProcedimiento[i].value)){
									swal("Alguna de las fechas no cumple con el formato requerido (dd/mm/yyyy o yyyy/mm/dd).",{icon:"info",button: "Cerrar"});
									banFechasProcedimiento = false;
									return;
								}
							}
						}
					}
				}
					for(var i=0;i<inputTablaFechasProcedimiento.length;i++){
						if(inputTablaFechasProcedimiento[i].type == "text" || inputTablaFechasProcedimiento[i].type == "TEXT"){
								if(inputTablaFechasProcedimiento[i].id.indexOf("fechaProcedimiento") >= 0){
									numFecha=parseInt(inputTablaFechasProcedimiento[i].id.replace("fechaProcedimientoC",""),10);
									if(i>0){
										if(inputTablaFechasProcedimiento[i-1].value != "" && inputTablaFechasProcedimiento[i].value != ""){
											if(numFechaAnt!=12){
												fechaTem=inputTablaFechasProcedimiento[i-1].value;
											}else{
												fechaTem=inputTablaFechasProcedimiento[i-2].value;
											}
											if(!compare_dates(inputTablaFechasProcedimiento[i].value, fechaTem)){
												swal("Las fechas se deben introducir en orden cronologico.",{icon:"info",button: "Cerrar"});
												banFechasProcedimiento = false;
												return;
											}
										}else{
											swal("Falta insertar algunas fechas.",{icon:"info",button: "Cerrar"});
											banFechasProcedimiento = false;
											return;
										}
									}
									numFechaAnt=numFecha;
								}
							}
						}//fin del if que checa los tipos de procedimiento de arrendamiento
					}//fin del for 
				
				
				
				var ivaaux=$("#ivaProcedimiento").val();
				$("#EstadoCaptura").val(1);
				if($("#cIdTipoProcedimiento").val().toString()!="PA" && $("#cIdTipoProcedimiento").val().toString()!="PL"){
					queryFormPost("sp_ProcedimientoUpdate",{async : false});
				}
				else{
					queryFormPost("sp_ProcedimientoUpdateArrendamientos",{async : false});
				}
				ponValorCheckBox();
			
				if ($("#existeProv").val()==0){// no hay proveedores asignados
					$("#ivaProcedimiento").val(ivaaux);
				}else{//existen proveedores
					queryFormPost("actualizaIvaProcedimiento",{async : false});
				}
				 muestraInformacionCaratula();
	 		
				  //Delete Fechas del procedimiento
				 queryFormPost("deleteFechasProcedimiento",{async : false});
				//Insertar Fechas del procedimiento
					for(var i=0;i<inputTablaFechasProcedimiento.length;i++){
						if(inputTablaFechasProcedimiento[i].type == "text" || inputTablaFechasProcedimiento[i].type == "TEXT"){
							if(inputTablaFechasProcedimiento[i].id.indexOf("fechaProcedimiento") >= 0){
								if(inputTablaFechasProcedimiento[i].value != ""){
									$("#nIdFechaProcedimiento").val(inputTablaFechasProcedimiento[i].id.replace("fechaProcedimientoC",""));
									$("#nFechaProcedimiento").val(inputTablaFechasProcedimiento[i].value);
									queryFormPost("agregaFechasProcedimiento",{async : false});
									inputTablaFechasProcedimiento[i].value = "";
								}
							}
						}
					}

					queryInnerDivPost("llenaFechasProcedimientoCaratula",{async:false});
					$("#btnBuscarConsultaProcedimiento").click();
					if ($("#existeProv").val()==0){// no hay proveedores asignados
						$("#ivaProcedimiento").val(ivaaux);
					}
					guardaBitacora("CARATULA_GUARDADA",$("#cIdProcedimiento").val());
					swal("Datos Actualizados.",{icon:"info",button: "Cerrar"});
				}else{
					swal("No tiene Permisos.",{icon:"info",button: "Cerrar"});
				}
				
		});
	cargaDatatable();
	cargaProveedoresCotizaciones();
	$( "#dialog-form" ).dialog({
				autoOpen: false,
				height: 300,
				width: 600,
				modal: true,
				buttons: {
					"Aceptar": function() {
						$("#cIdRFC").val($("#cIdRFC1").val()+"-"+$("#cIdRFC2").val()+"-"+$("#cIdRFC3").val());
						$("#cRazonSocial2").val($("#cRazonSocial").val());
						$("#cNombre2").val($("#cNombre").val());
						$("#cApellidoPaterno2").val($("#cApellidoPaterno").val());
						$("#cApellidoMaterno2").val($("#cApellidoMaterno").val());
						$("#cMonto2").val($("#cMonto").val());
						$("#nIdSexo").val($("#nSexo").val());
						queryFormPost("insertmCotizacionesProcedimiento",{async : false});
						$( this ).dialog( "close" );
						cargaProveedoresCotizaciones();
					},
					Cancelar: function() {
						$( this ).dialog( "close" );
					}
				},
				close: function() {
					
				}
			});
	});
	function cargaDatatable(){
		daTable= $("#tblProvedoresCotizaciones").dataTable({
				    bPaginate: false,
        			bLengthChange: false,
        			bFilter: true,
        			bSort: false,
        			bInfo: false,
        			bAutoWidth: true,
					sScrollX: "100%",
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
					sInfoFiltered: "(filtrado de _MAX_ registros)",
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
				
		       });
		$("#tblProvedoresCotizaciones tbody").dblclick(
			function(event) {$($("#tblProvedoresCotizaciones").dataTable().fnSettings().aoData).each(function() {$(this.nTr).removeClass('row_selected');});
	
				$(event.target.parentNode).addClass('row_selected');
	
				aPos = daTable.dataTable().fnGetPosition(event.target.parentNode);
				
				var aData = daTable.fnGetData(aPos);
	
				var rfc=aData[0];
				var razon_Social=aData[1];
				var monto=aData[2];
				
				swal({
					title: "Se Eliminara la cotización "+rfc+" "+razon_Social+" $"+monto+",estas seguro?",
					text: "",
					icon: "info",
					buttons: {
						confirm : "Aceptar",
						cancel: "Cancelar"
						},
					}).then((continuar) => {
						if (!continuar) {
							return;
					}else{
						$("#cMonto2").val(monto);
						$("#cIdRFC").val(rfc);
						queryFormPost("mCotizacionesProcedimientoDelete",{async : false});
						cargaProveedoresCotizaciones();	
					}
				});
				
		});
		       
	}
	function cargaProveedoresCotizaciones(){
		
		var query= " cIdProcedimiento = '" +$("#cIdProcedimiento").val()+"'" ;
		$('#tblProvedoresCotizaciones').dataTable().fnClearTable();
		
		szTabla = "PROVEEDORESCOTIZACIONES";                                                                                         
				$.getJSON("../../catalogos/SelectJson.jsp?"+new Date(),{Tabla: szTabla, Param: query, MaxReg:"" , ajax: 'false'}, 
					function(j)
					{     
					nEditaRegistrosProveedor=j.length;
						arrayCompleto=new Array();
						var l=0;
						for (var i = 0; i < j.length; i++) 
    					{
    					   if(j[i].Col0!=$("#nIdRFCProveedor_"+i+"").val()){
    				       
	    					arrayCompleto [l]=[
							  j[i].Col0 , 
							  j[i].Col1,
							  j[i].Col2,
							  j[i].Col3
							]; 
							l++;
							}	     
    					
						}
						$('#tblProvedoresCotizaciones').dataTable().fnAddData(arrayCompleto);	
		         });   
	}
	function validaLicitacion2(){
		 if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO"){
		 	swal("Para poder modificar el tipo de procedimiento de de estar en captura.",{icon:"info",button: "Cerrar"});
		   	return;
		 }else{
			swal({
				title: "Al cambiar de categoria se borrarán las fechas, \n¿Desea continuar?",
				text: "",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
					},
				}).then((continuar) => {
					if (!continuar) {
						$("#cboCategoria").val( $("#caratulaAnterior").val());
						return;
				}else{
					if(parseInt($("#cboCategoria").val(),10) <= 4){
					//es licitación
						$("#doctosComite1").hide();
						$("#doctosComite2").hide();
						$("#etiquetaChkActivo").hide();
						document.getElementById("ProveedoresProcedimiento").disabled = false;
						document.getElementById("CotizacionProcedimiento").disabled = false;
						document.getElementById("DocumentosProcedimiento").disabled = false;
						document.getElementById("EvaluacionProcedimiento").disabled = false;
						document.getElementById("ArchivosProcedimiento").disabled = false;
						document.getElementById("PreguntasProcedimiento").disabled = false; 
						$("#Activo").val(0);	
					}else{
						//validación del valor del checkbox en BD para la Categoría				
						$("#doctosComite1").show();
					 	$("#doctosComite2").show();
						$("#doctosComite1").attr("checked", false);
						$("#doctosComite2").attr("checked", true);
						$("#Activo").val(0);			
						$("#etiquetaChkActivo").show();
						obtenCategoriaProcedimiento();
					}
				}
			});
			queryInnerDivPost("llenaFechasProcedimientoModificacion", {async : false});
		 }
	}
	function hideAndShowbuttons(){
		if(parseInt($("#nIdEstado").val(),10)==3){
			$("#btnAgregaCNET").hide();
			$("#btnGuardarCaratulaProcedimiento").hide();
			$("#btnAgregaCotizacion").hide();
		}
		if(parseInt($("#nIdEstado").val(),10)==2){
			$("#btnGuardarCaratulaProcedimiento").hide();
		}
	}
	function muestraInformacionCaratula(){
		
		   ponValorCheckBox();
		   querySelectPost("mCatalogoTipoIVA", "ivaProcedimiento", {async : false});
		   queryFormPost("llenaCaratulaProcedimiento",{async:false});
		   queryFormPost("llenaUnidadEjecutoraProcedimiento",{async:false});
		   queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
		   queryFormPost("mProcedimientoCumpleRequisitosRead",{async:false});
		   queryFormPost("mValidaPrecompromiso",{async:false});
			//queryFormPost("mValidaPrecompromisoProcedimiento",{async:false});
			if($("#nIdEstado").val()==1){
				 queryFormPost("apartadoConsolidado",{async:false});
			}else{
				 if ($("#documentoAplicado").val()==0){
				 	 queryFormPost("sumPrecomProcedimiento",{async:false});
				 }else{
				 	 queryFormPost("apartadoConsolidado",{async:false});
				 }
			}
		  
		   queryFormPost("mUsuarioMismaUE", {async: false   });
		   queryFormPost("mValidaPrecompromiso",{async:false});
		   queryInnerDivPost("minimoMaximoTipoProcedimiento", {async : false}); 
		   if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO" || !($('#cIdUsuarioCreacion').val()== $("#usuarioLogin").val() ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1)){
		   		$("#btnGuardarCaratulaProcedimiento" ).css("visibility","hidden");
		   		document.getElementById("btnAgregaCNET").style.display = "block";
		   }
		   if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO"){
		   		document.getElementById("btnAgregaCNET").style.display = "block";
		   }
		   $("#caratulaAnterior").val($("#cboCategoria").val());
		   if($("#cIdTipoProcedimiento").val().toString()=="PS" || $("#cIdTipoProcedimiento").val().toString()=="PA" || $("#cIdTipoProcedimiento").val().toString()=="PL" || $("#cIdTipoProcedimiento").val().toString()=="PN"){
		 
		   	 $("#esServicio").val(1);
		   }else{
		   	 $("#esServicio").val(0);
		   }
		   queryInnerDivPost("llenaFechasProcedimientoCaratula",{async:false});
		   var cadena_campos=$("#cIdConsolidado").val().split('-');
			$("#TipoConsolidado").val(cadena_campos[0]); 
			$("#ConsecutivoConsolidado").val(cadena_campos[2]);
			
			if($("#cIdTipoProcedimiento").val()=='PN' || $("#cIdTipoProcedimiento").val()=='PS'){
				if($("#cProcedimientoCumple").val()==1){ // cumple requisitos
				
					if($("#tipoProceso").val()==1){ // proceso corto
						$("#CotizacionProcedimiento").css("display", "block");
						$("#CotizacionProcedimiento").attr("disabled", false);
						$("#EvaluacionProcedimiento").css("display", "none");
					} 
					if($("#tipoProceso").val()==2){ // proceso largo
						$("#EvaluacionProcedimiento").css("display", "block");
						$("#CotizacionProcedimiento").css("display", "none");
						$("#EvaluacionProcedimiento").attr("disabled", false);
					}
				}else{
					if($("#tipoProceso").val()==1){
						$("#CotizacionProcedimiento").css("display", "block");
						$("#CotizacionProcedimiento").attr("disabled", true);
						$("#EvaluacionProcedimiento").css("display", "none");
					}
					if($("#tipoProceso").val()==2){
						$("#EvaluacionProcedimiento").css("display", "block");
						$("#CotizacionProcedimiento").css("display", "none");
						$("#EvaluacionProcedimiento").attr("disabled", true);
					}
				}		
			}
			if(esRegularizacion()){
				document.getElementById('trBotones').style.display = 'none';
				document.getElementById('trSalir').style.display = 'none';
				
			}else{
				document.getElementById('trBotones').style.display = 'none';
				document.getElementById('trSalir').style.display = 'block';
			}
			
			queryFormPost("mSolicitudDescripcionUnidad", { async:false });	
			
	}
	///Desabilita sabados y domingos del datepicker
		 function nonWorkingDates(date){
	        var day = date.getDay(), Sunday = 0, Monday = 1, Tuesday = 2, Wednesday = 3, Thursday = 4, Friday = 5, Saturday = 6;
	        //var closedDates = [[7, 29, 2009], [8, 25, 2010]];
	        var closedDays = [[Sunday], [Saturday]];
	        /*for (var i = 0; i < closedDays.length; i++) {
	            if (day == closedDays[i][0]) {
	                return [false];
	            }
	
	        }*/
	
// 	        for (i = 0; i < closedDates.length; i++) {
// 	            if (date.getMonth() == closedDates[i][0] - 1 &&
// 	            date.getDate() == closedDates[i][1] &&
// 	            date.getFullYear() == closedDates[i][2]) {
// 	                return [false];
// 	            }
// 	        }
	
	        return [true];
	    }
	function ponValorCheckBox(){
		queryFormPost("esActivoProcedimiento",{async:false});
		
        if($("#Activo").val() == 1){
    		document.getElementById("doctosComite1").checked = true;
    		document.getElementById("doctosComite2").checked = false;
    	} else{
    		document.getElementById("doctosComite1").checked = false;
    		document.getElementById("doctosComite2").checked = true;
	    }
	}
	
	function obtenCategoriaProcedimiento(){
		queryFormPost("ObtenCategoriaProcedimiento", {async:false});
		if(parseInt($("#cboCategoria").val(),10) == $("#Categoria").val()){
			ponValorCheckBox();
		}
	}
	
	function validaModificarProcedimiento(){
		queryFormPost("mUsuarioMismaUE", {async: false   });
		if(($('#cIdUsuarioCreacion').val()== $("#usuarioLogin").val())||( roles.indexOf('ADMIN_RECMAT') >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1)){
			return true;
		}
		else{
			swal("El usuario no tiene permiso para realizar esta acci\xF3n.",{icon:"info",button: "Cerrar"});
			return false;
		}
	}
	
	function adjudicarProcedimiento(){
		if(validaModificarProcedimiento()){
			if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO")
				return;	
		queryFormPost("mProcedimientoCumpleRequisitosRead",{async:false});
		if($("#cProcedimientoCumple").val() == false){
			swal("El procedimiento no cumple los requisitos.",{icon:"info",button: "Cerrar"});
			return;
		}
		queryFormPost("checaProveedoresAsignados",{async:false});
		if($("#tieneProveedor").val()=="0"){
			swal("El procedimiento no tiene ningun proveedor asociado.",{icon:"info",button: "Cerrar"});
			return;
		}
		//valida el procedimiento de invitacion a tres
		queryFormPost("fn_mVerificaAplicaPartidaDesiertaRead",{async:false});
		if($("#cCategoriaDescripcion").val().toLowerCase().indexOf("tres personas") >= 0 && ($("#tipoProceso").val().toUpperCase().indexOf("COMPLETO") >= 0 || parseInt($("#tipoProceso").val(),10) == 0)){			
				if($("#tieneProveedor").val() < 3){
					swal({
						title: "El procedimiento solo tiene "+$("#tieneProveedor").val()+" proveedor(es) si contin\xFAa se cambiar\xE1 el estatus a desierto autom\xE1ticamente.\n\xBFDesea continuar?",
						text: "",
						icon: "info",
						buttons: {
							confirm : "Aceptar",
							cancel: "Cancelar"
							},
						}).then((continuar) => {
							if (!continuar) {
								return;
							}else{
								$("#EstadoCaptura").val(3);
								queryFormPost("sp_ProcedimientoDesierto",{async:false});
								actualizaDatosProcedimientoDesierto();
								return;
							}
						});
				}else{
					//valida que se hayan cargado mas de 3 cotizaciones				
					if($("#cPartidaDesierta").val() != ""){
						if($("#cPartidaDesierta").val().toUpperCase() == "SI"){
							swal({
								title: "Existen partidas que tienen menos de 3 cotizaciones si contin\xFAa se cambiaran a desiertas autom\xE1ticamente.\n\xBFDesea continuar?",
								text: "",
								icon: "info",
								buttons: {
									confirm : "Aceptar",
									cancel: "Cancelar"
									},
								}).then((continuar) => {
									if (!continuar) {
										return;
									}else{
										queryFormPost("sp_mAplicaPartidaDesierta",{async:false});
										return;
									}
								});
						}
					}
				}
			}
	
			if($("#cCategoriaDescripcion").val().toLowerCase().indexOf("adjudicaci") >= 0 && $("#cCategoriaDescripcion").val().toLowerCase().indexOf("n directa") >= 0 && ($("#tipoProceso").val().toUpperCase().indexOf("COMPLETO") >= 0 || parseInt($("#tipoProceso").val(),10) == 0)){
				if($("#tieneProveedor").val() < 3){
					swal({
						title: "El procedimiento solo tiene "+$("#tieneProveedor").val()+" provedor(s) si contin\xFAa se cambiar\xE1 el estatus a desierto autom\xE1ticamente.\n\xBFDesea continuar?",
						text: "",
						icon: "info",
						buttons: {
							confirm : "Aceptar",
							cancel	: "Cancelar"
							},
						}).then((continuar) => {
							if (!continuar) {
								return;
							}else{
								$("#EstadoCaptura").val(3);
								queryFormPost("sp_ProcedimientoDesierto",{async:false});
								actualizaDatosProcedimientoDesierto();
								return;
							}
						});
				}
			}
			$.ajax({url: '../../servlet/ProcedimientoServlet?cEjercicio='+$("#cEjercicio").val()+"&cIdTipoProcedimiento="+$("#cIdTipoProcedimiento").val()+"&cIdUnidadEjecutora="+$("#cIdUnidadEjecutora").val()+"&nIdConsecutivo="+$("#nIdConsecutivo").val()+"&tipoProceso="+$("#tipoProceso").val(),
			 type:'post' , async: false,data:'operacion=2', dataType: 'json', success: 
				function(j){
					mensajeAp=j[0].Contable1;
					swal(mensajeAp,{icon:"info",button: "Cerrar"});
					window.location = "Procedimiento-copia.jsp?tab=2";
					$("#cAccion").val("ADJUDICA_PROCEDIMIENTO");
					$("#cIdDocumento").val($("#cIdProcedimiento").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});	
				}
			});
		}
  	}
  	
  	function actualizaDatosProcedimientoDesierto(){
		$("#nIdEstadoProcedimiento").val("DESIERTO");
		
		queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
	    var imagen=$("#imagenEstadoProcedimiento").val();
	    imagen=imagen.substring(11,56);
	    var ima=document.getElementById("imgEstado");
		$("#btnGuardarCaratulaProcedimiento").attr("visibility", "hidden");	
		document.getElementById("btnAgregaCNET").style.display = "block";	
		//bitacora
		$("#cAccion").val("DESIERTO_PROCEDIMIENTO");
		$("#cIdDocumento").val($("#cIdProcedimiento").val());
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
		
		
		
    }
  
  	function actualizaDatosProcedimientoAdjudicado(){
		$("#nIdEstadoProcedimiento").val("ADJUDICADO");
		queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
		var imagen=$("#imagenEstadoProcedimiento").val();
	   //bitacora
		$("#cAccion").val("ADJUDICA_PROCEDIMIENTO");
		$("#cIdDocumento").val($("#cIdProcedimiento").val());
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
		
   }
   function desiertoProcedimiento(){
		if(validaModificarProcedimiento()){
			if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO" ||$("#nIdEstadoProcedimiento").val()=="DESIERTO  " )
			return;	
			swal({
				title: "¿Desea declarar desierto el Procedimiento " + $("#cIdProcedimiento").val()+" ?",
				text: "",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
					},
				}).then((continuar) => {
					if (!continuar) {
						return;
					}else{
						$("#EstadoCaptura").val('3');
						$("#descripcionCaratula").val(encodeURIComponent($("#descripcionCaratula").val()));
						
						queryFormPost("sp_ProcedimientoDesierto",{async:false});
						actualizaDatosProcedimientoDesierto();
					}
				});
		}
	}
	function devolverProcedimiento(){
		if(validaModificarProcedimiento()){
			if($("#nIdEstadoProcedimiento").val()=="CAPTURADO" || $("#nIdEstadoProcedimiento").val()=="DESIERTO"){
				swal("No se puede devolver el procedimiento, porque su estado no lo permite.",{icon:"info",button: "Cerrar"});
				return;
			}
		   $.ajax({url: '../../servlet/ProcedimientoServlet?', type:'post' , async: false, 
           data:"operacion=3&cEjercicio="+$("#cEjercicio").val()+"&cIdTipoProcedimiento="+$("#cIdTipoProcedimiento").val()+"&cIdUnidadEjecutora="+$("#cIdUnidadEjecutora").val()+"&nIdConsecutivo="+$("#nIdConsecutivo").val()+"&tipoProceso="+$("#tipoProceso").val(),
            dataType: 'json', success: 
            function(j){
            		mensajeAp=j[0].Contable1;
					swal(mensajeAp,{icon:"info",button: "Cerrar"});
					window.location = "Procedimiento-copia.jsp?tab=2";	
					$("#cAccion").val("DEVOLVER_PROCEDIMIENTO");
					$("#cIdDocumento").val($("#cIdProcedimiento").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
			}
	   		});
		}
	}
	
	
	  function actualizaDatosProcedimientoDevuelto(){
		$("#nIdEstadoProcedimiento").val("CAPTURADO");
	    queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
	    var imagen=$("#imagenEstadoProcedimiento").val();
   		//bitacora
		$("#cAccion").val("DEVUELVE_PROCEDIMIENTO");
		$("#cIdDocumento").val($("#cIdProcedimiento").val());
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
		
   }
 	
 	function validaFormatoFecha(fecha){
		var fechaS=fecha.split("/");
		if(fechaS[0].length == 4){//Verifica el formato del anio
			
			if(fechaS[1].length == 2 && fechaS[2].length == 2){
				if(isInteger(fechaS[0]) && isInteger(fechaS[1]) && isInteger(fechaS[2]))
					return true;
				else
					return false;
			}
			else
				return false;
		}
		if(fechaS[2].length == 4){//verifica el formato del anio
			if(fechaS[0].length == 2 && fechaS[1].length == 2){
				if(isInteger(fechaS[0]) && isInteger(fechaS[1]) && isInteger(fechaS[2]))
					return true;
				else
					return false;
			}
			else
				return false;
		}
	}
	
		function isInteger(s){
        var i;
        var c;
        for (i = 0; i < s.length; i++){
            c = parseInt(s.charAt(i),10);
            if((c != 0) && (c != 1) && (c != 2) && (c != 3) && (c != 4) && (c != 5) && (c != 6) && (c != 7) && (c != 8) && (c != 9))
           		return false;
        }
        return true;
    }
    
    function compare_dates(fecha, fecha2){
	var xFecha = fecha.split("/");
	var yFecha = fecha2.split("/");
	var xMonth;
	var xDay;
	var xYear;
	var yMonth;
	var yDay;
	var yYear;

	xMonth = xFecha[1];
	yMonth = yFecha[1];
	//verifica en que posision biene en anio en fecha1
	if(xFecha[0].toString>2){
		xDay = xFecha[2];
		xYear = xFecha[0];
	}
	else{
		xDay = xFecha[0];
		xYear = xFecha[2];
	}
	
	//verifica en que posision biene en anio en fecha2
	if(yFecha[0].toString>2){
		yDay = yFecha[2];
		yYear = yFecha[0];
	}
	else{
		yDay = yFecha[0];
		yYear = yFecha[2];
	}
	
  	if (xYear> yYear){
      return(true);
  	}
  	else{
    	if (xYear == yYear){ 
      		if (xMonth> yMonth){
          		return(true);
     		}
      		else{ 
        		if (xMonth == yMonth){
          			if (xDay >= yDay)
            			return(true);
          			else
            			return(false);
        		}
        		else
          			return(false);
      		}
    	}
    	else
      		return(false);
  	}
}
	function days_between(date1, date2) {
	    // The number of milliseconds in one day
	    var ONE_DAY = 1000 * 60 * 60 * 24;
	
	    // Convert both dates to milliseconds
	    var date1_ms = date1.getTime();
	    var date2_ms = date2.getTime();
	
	    // Calculate the difference in milliseconds
	    var difference_ms = date2_ms - date1_ms;
	    
	    // Convert back to days and return
	    return Math.round(difference_ms/ONE_DAY);
	}
	
	function habilitarPestanas(){
		if($("#tipoProceso").val()==1){ //proceso corto
			$("#EvaluacionProcedimiento" ).css("display", "none");
			$("#PreguntasProcedimiento" ).css("display", "none");
			$("#ArchivosProcedimiento" ).css("display", "none");
			$("#CotizacionProcedimiento" ).css("display", "block");
			if(esRegularizacion()){// no trae precompromiso
				$("#presupuestoProcedimiento" ).css("display", "none");
				$("#precompromisoProcedimiento" ).css("display", "none");	
				$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
			}else{ // trae precompromiso
				queryFormPost("validaPartidasAdjudicadas",{async:false});
				if ($("#nIdEstado").val()==1){ 				
				//verificar si el precompromiso se hizo en consolidado
					if($("#documentoAplicado").val()==0){
							$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
							// si aun no hay partidas en adjudicacionPartidas, no se muestran las pestañas
							
							if($("#partidasAdjudicadas").val()=="0"){ 
								$("#presupuestoProcedimiento" ).css("display", "none");
								$("#precompromisoProcedimiento" ).css("display", "none"); 	
							}else{
								/* //si el usuario logeado es administrador y no es dueño no se muestra
							    if ($("#usuarioRoleProcedimiento").val().toString().indexOf("ADMIN_RECMAT")>=0){
									if($("#cIdUsuarioCreacion").val()==$("#usuarioLogin").val()){
										$("#presupuestoProcedimiento").css("display", "block");
										$("#precompromisoProcedimiento").css("display", "block");
									}else{
										$("#presupuestoProcedimiento").css("display", "none");
										$("#precompromisoProcedimiento").css("display", "none");
									}
								}else{
									$("#presupuestoProcedimiento").css("display", "block");
									$("#precompromisoProcedimiento").css("display", "block");
								} */				
								$("#presupuestoProcedimiento").css("display", "block");
								$("#precompromisoProcedimiento").css("display", "block");				
							}
					}else{
						//ya trae precompromiso, si no cubre el monto del consolidado mostrar las pestañas de precom y presupuesto, para que sea solvetada
						if($("#partidasAdjudicadas").val()=="0"){
							$("#presupuestoProcedimiento" ).css("display", "none");
							$("#precompromisoProcedimiento" ).css("display", "none");
						}else{
							if(cubreMonto()){
								$("#presupuestoProcedimiento" ).css("display", "block");
								$("#precompromisoProcedimiento" ).css("display", "block");
							
							}else{
								$("#presupuestoProcedimiento" ).css("display", "none");
								$("#precompromisoProcedimiento" ).css("display", "none");
							} 
						
						} 	 						
						
					}					
				}else{
					if($("#nIdEstado").val()==2){
						queryFormPost("validaFlujoProcedimiento", {async:false});
						if($("#cEventoFlujo").val()=="DISP_PRECOMMAT" || $("#cEventoFlujo").val()=="PRECOM_MAT"){
							/* if ($("#usuarioRoleProcedimiento").val().toString().indexOf("ADMIN_RECMAT")>=0){
								if($("#cIdUsuarioCreacion").val()==$("#usuarioLogin").val()){
									$("#presupuestoProcedimiento").css("display", "block");
									$("#precompromisoProcedimiento").css("display", "block");
								}else{
									$("#presupuestoProcedimiento").css("display", "none");
									$("#precompromisoProcedimiento").css("display", "none");
								}
							}else{
								$("#presupuestoProcedimiento").css("display", "block");
								$("#precompromisoProcedimiento").css("display", "block");
							} */	
							$("#presupuestoProcedimiento" ).css("display", "block");
							$("#precompromisoProcedimiento" ).css("display", "block"); 
							$("#AmpliacionVigenciaProcedimiento" ).css("display", "block");
						}else{
							$("#presupuestoProcedimiento" ).css("display", "none");
							$("#precompromisoProcedimiento" ).css("display", "none");
							$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
						}
					}else{
						$("#presupuestoProcedimiento" ).css("display", "none");
						$("#precompromisoProcedimiento" ).css("display", "none");
					}
				}
			}			
		}else{// proceso completo
			$("#EvaluacionProcedimiento" ).css("display", "block");
			$("#PreguntasProcedimiento" ).css("display", "block");
			$("#ArchivosProcedimiento" ).css("display", "block");
			$("#CotizacionProcedimiento" ).css("display", "none");
			
			if(esRegularizacion()){// no trae precompromiso
				$("#presupuestoProcedimiento" ).css("display", "none");
				$("#precompromisoProcedimiento" ).css("display", "none");	
			}else{
				if ($("#nIdEstado").val()==1){
					if($("#documentoAplicado").val()==0){ // precompromiso cancelado 
						$("#presupuestoProcedimiento" ).css("display", "block");
						$("#precompromisoProcedimiento" ).css("display", "block");	
					}else{
						$("#presupuestoProcedimiento" ).css("display", "none");
						$("#precompromisoProcedimiento" ).css("display", "none");
					}
				}else{
					$("#presupuestoProcedimiento" ).css("display", "none");
					$("#precompromisoProcedimiento" ).css("display", "none");
				}
			}
		}
		if($("#cIdTipoProcedimiento").val()=="PN" || $("#cIdTipoProcedimiento").val()=="PS" ){
			$("#RequisitosProcedimiento" ).css("display", "block");
		}else{
			$("#RequisitosProcedimiento" ).css("display", "none");
		}
		//valida que exista un precompromiso en procedimiento,muestra la pestaña de ampliacion de vigencias
		if($("#nIdEstado").val()==2){
			queryFormPost("existePrecompromiso",{async:false});
			if($("#existePrecompromiso").val()==1){
				$("#AmpliacionVigenciaProcedimiento" ).css("display", "block");
			}else{
				$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
			}
		}else{
			$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
		}
	}
	
	  function validaCaratula(){
		if($("#doctosComite1").is(':checked')){
			$("#Activo").val(1);
		}
		else{
			$("#Activo").val(0);				
		}				
	}
	
	function cubreMonto(){
		queryFormPost("fn_mConsolidadoCalculaMontoConIVARead", {async:false});	
		if(parseInt(quitaFmt($("#lblApartado").val()),10)<parseInt(quitaFmt($("#MontoConIVA").val()),10 )){			
			return true;
		}else{
			return false;
		}
		
	}
	
	function quitaFmt( val ) {
	  	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");
	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val;
	}
	 function muestraDenominacionOtros(select){
		if(select.value.toString()=="8"){
		$("#descripcionOtros").css("visibility","visible");
		  
		}else
		$("#descripcionOtros").css("visibility","hidden");
		
	}
	
	function esRegularizacion(){
		var regularizacion=false;
		var apartables = [ "PC", "PO", "PS", "PA", "PT" ];
		if ($.inArray($("#cIdTipoProcedimiento").val(), apartables) == -1){ // no traen apartado
			regularizacion=true;
		}else
			regularizacion=false;
		return regularizacion;
	}
	function onlyPercentage(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode
		if (keyPressed == 46 || keyPressed == 37)
			return true;
		return (keyPressed >= 48 && keyPressed <= 57);
	}
	function guardaBitacora(accion,documento){
		//Bitácora
		$("#cAccion").val(accion);
		//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
		$("#cIdDocumento").val(documento);
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
	}
	function muestraVentanaCapturaCotizacion(){
		$( "#dialog-form" ).dialog( "open" );
		querySelectPost("tCatalogoSexo", "nSexo", {async: false});
		tipoPersonaChange();
	}
	function tipoPersonaChange() {
		var tipo = $("#cIdTipoPersonaRFC").val();
		$("#cIdTipoPersona").val(tipo);
		if (tipo == 2) {//FISICA
			document.getElementById('cIdRFC1').maxLength = 4;
			$("#cRazonSocial").val("");
			$("#cNombre").val("");
			$("#cApellidoPaterno").val("");
			$("#cApellidoMaterno").val("");
			
			$("#trRazonSocial").hide();
			$("#trNombre").show();
			$("#trApellidoPaterno").show();
			$("#trApellidoMaterno").show();
			
		}else{//MORAL
			$("#cIdRFC1").val($("#cIdRFC1").val().substring(0, 3));
			document.getElementById('cIdRFC1').maxLength = 3;
			$("#cRazonSocial").val("");
			$("#cNombre").val("");
			$("#cApellidoPaterno").val("");
			$("#cApellidoMaterno").val("");
			
			$("#trRazonSocial").show();
			$("#trNombre").hide();
			$("#trApellidoPaterno").hide();
			$("#trApellidoMaterno").hide();
				
		}
			
	}
	function ChangeCase(elem) {//Cambia a Mayusculas
		elem.value = elem.value.toUpperCase();
	}
	function cambiafrmt( elem ){
	    elem.value = elem.value.formatCurrency();
	}
	function Change(elem,evt) {//Pasa al siguiente campo al escribir
		var rfc = elem.name;
		if (elem.value.length == elem.maxLength) {
			if (rfc == "cIdRFC1") {
				if(onlyNumbers(evt))
				$("#cIdRFC2").select();
			} else
				$("#cIdRFC3").select();
		}
	}
	function onlyNumbersAndLetters(evt, elem) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode;
		var strCheck = "!@#$%^&*()´+=-[]\\';,./{}|\":<>?";
		var key = String.fromCharCode(keyPressed);
		if(elem.name == "cMonto"){
				strCheck = '0123456789.';
				if (strCheck.indexOf(key) == -1)
					return false;
				else
					return true;
			}
			else
		if (strCheck.indexOf(key) == -1) {
			//fue ingresado un caracter valido
			if(elem.name == "cMonto"){
				strCheck = '0123456789.';
				if (strCheck.indexOf(key) == -1)
					return false;
				else
					return true;
			}
			else if (elem.name == "cIdRFC2" || elem.name == "NEmp") {//Solo numeros para esta parte del RFC
				strCheck = '0123456789';
				if (strCheck.indexOf(key) == -1)
					return false;
				else
					return true;
			} else
				return true;
		}
		return false;
	}
	function agregaCNET (){
		if($("#numero_externoCaratula").val()==''){
			swal("No se puede actualizar un procedimiento vacío",{icon:"info",button: "Cerrar"});
		}else{
			swal({
				title: "Desea actualizar el Procedimiento de CNET: "+$("#numero_externoCaratula").val()+"?",
				text: "",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
					},
				}).then((continuar) => {
					if (!continuar) {
						return;
					}else{
						queryFormPost({
							queryName: "actualizaProcedimientoCNET",
							async: false,
							callback: function() {
							queryFormPost({
								queryName: "obtenContrato",
								async: false,
								callback: function() {
									queryFormPost({
									queryName: "sp_RegistraCNET",
									async: false,
									callback: function() {
										swal("Procedimiento Actualizado.",{icon:"info",button: "Cerrar"});
										$("#cAccion").val("ACTUALIZA PROC-CNET");
										$("#cIdDocumento").val($("#cIdProcedimiento").val());
										queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
								}});
							}});
						}});
					}
				});
		}
		
	}
	</script>
	</head>
	<body id="dt_example" >
		<form>
		<div id="dialog-form" title="Captura Cotizacion">
			<fieldset>
				<table align="left">
					<tr>
						<td style="width: 120px; ">Tipo Persona: </td>
						<td style="width: 500px; ">
							<select id="cIdTipoPersonaRFC" name="cIdTipoPersonaRFC" onChange="tipoPersonaChange()" >
								<option id="1" value="1" selected>MORAL</option>
								<option id="2" value="2">FISICA</option>
							</select>
						</td>
					</tr>
					<tr>
						<td style="width: 120px; ">Sexo: </td>
						<td style="width: 500px; ">
							<select id="nSexo" name="nSexo" >
							</select>
						</td>
					</tr>
					<tr>
						<td style="width: 120px; ">RFC: </td>
						<td style="width: 500px; ">
							<input type="text" name="cIdRFC1" id="cIdRFC1" maxLength="3" style="width: 4em;" onKeyPress="Change(this,event);return(onlyNumbersAndLetters(event,this));" onblur="ChangeCase(this);">-
							<input type="text" name="cIdRFC2" id="cIdRFC2" style="width: 4em;" maxlength="6" onKeyPress="Change(this,event);return(onlyNumbersAndLetters(event,this));" onblur="ChangeCase(this);" />-
							<input type="text" name="cIdRFC3" id="cIdRFC3" style="width: 4em;" maxlength="3" onKeyPress="return(onlyNumbersAndLetters(event,this));" onblur="ChangeCase(this);" />	
						</td>
					</tr>
					<tr id="trRazonSocial">
						<td style="width: 120px; ">Raz&oacute;n Social: </td>
						<td style="width: 500px; ">
							<input type="text" name="cRazonSocial" id="cRazonSocial" style="width: 30em;" maxlength="300" onblur="ChangeCase(this);" />
					</tr>
					<tr id="trNombre">
						<td style="width: 120px; ">Nombre: </td>
						<td style="width: 500px; ">
							<input type="text" name="cNombre" id="cNombre" style="width: 30em;" maxlength="300" onblur="ChangeCase(this);" />
					</tr>
					<tr id="trApellidoPaterno">
						<td style="width: 120px; ">Apellido Paterno: </td>
						<td style="width: 500px; ">
							<input type="text" name="cApellidoPaterno" id="cApellidoPaterno" style="width: 30em;" maxlength="300" onblur="ChangeCase(this);" />
					</tr>
					<tr id="trApellidoMaterno">
						<td style="width: 120px; ">Apellido Materno: </td>
						<td style="width: 500px; ">
							<input type="text" name="cApellidoMaterno" id="cApellidoMaterno" style="width: 30em;" maxlength="300" onblur="ChangeCase(this);" />
					</tr>
					<tr id="trMonto">
						<td style="width: 120px; ">Monto Total: </td>
						<td style="width: 500px; ">
							<input type="text" name="cMonto" id="cMonto" style="width: 10em;" maxlength="30em" onblur="cambiafrmt(this);" onKeyPress="return(onlyNumbersAndLetters(event,this));" />
					</tr>
					
				</table>
			</fieldset>
	</div>		
			<fieldset>
				<legend>Informaci&oacute;n del Procedimiento</legend>
				<table border="0" align="center" width="100%">
					<tr id="trBotones" style='display:none'>						
						<td align="right" colspan="2"  >
						<img id="imgPlayStop" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="adjudicarProcedimiento();"/>Adjudicar
						<img id="imgPlayStop" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="desiertoProcedimiento();" />Desierto
						<img id="imgPlayStop" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer;" onclick="devolverProcedimiento();"/>Devolver
						<img id="imgSalir" src="../imagenes/cancel.png" style="cursor: pointer"  onclick="salir();"/>Salir
						</td>
					</tr>
					<tr id="trSalir" style='display:none' align="right">
						<td align="right" colspan="2"  >
							<input type="button" class="btnInterfaceCancelar ui-button ui-widget ui-state-default ui-corner-all" 	id="imgPlayStop" 	name="imgPlayStop" 	value="Desierto"	onclick="desiertoProcedimiento();"/>&nbsp;&nbsp;
                            <input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	 onclick="salir();" />&nbsp;&nbsp;
						</td>
					</tr>
					<tr>
				    	<td align="left" colspan="2">
				    		<input type="text" style="width: 25px;border-width:0; background-color:transparent" id="lblUnidadUsuario" name="lblUnidadUsuario" readonly value="<%=unidadUsuarioLogeado%>"/>
				    		<input type="text" style="width: 690px;border-width:0; background-color:transparent" id="lblDescUsuario" name="lblDescUsuario" readonly />
				    	</td>
			    	</tr>
					<tr align="left">
						<td colspan="2">
							[[<input name="cIdProcedimiento" id="cIdProcedimiento" type="text" size ="10" style="border-width:0; background-color:transparent;" readonly="readonly"/>]]&nbsp;
							<input name="desProcedimiento" id="desProcedimiento" type="text" maxlength="150" style="border-width:0; background-color:transparent; width: 40em;" readonly="readonly"></input>  
						</td>								
					</tr>
				   <tr align="left">
						<td colspan="2">
							<input name="lblcIdUnidadEjecutora" id="lblcIdUnidadEjecutora" type="text" readonly="readonly" style="border-width:0; background-color:transparent; width: 30em;"></input>  
						</td>								
					</tr>
					<tr align="left">
						<td colspan="2">
							<input name="nIdEstadoProcedimiento" id="nIdEstadoProcedimiento" type="text"  style="border-width:0; background-color:transparent; width: 45em;"></input>  
						</td>								
					</tr>
					<tr align="left">
						<td colspan="2">
						Consolidado: <input name="cIdConsolidado" id="cIdConsolidado" type="text" size ="10" style="border-width:0; background-color:transparent;" readonly="readonly"></input> 
						</td>								
					</tr>
					<tr align="left">
						<td colspan="2">
						Tipo de Proceso: <input name="lbltipoProceso" id="lbltipoProceso" type="text" size ="18" style="border-width:0; background-color:transparent;" readonly="readonly"></input>  
						</td>								
					</tr>
					<tr align="left">
						<td colspan="2">
								Monto Precomprometido: <input name="lblApartado" id="lblApartado" type="text" size="18" style="border-width:0; background-color:transparent;" readonly="readonly"></input>
						</td>								
					</tr>
					<tr align="left">
						<td  colspan="2">
							<div id="tblMinimoMaximo" style="width:100%;"></div>
							
						</td>
					</tr>
					<tr>
						<td>
					 	<br/>
						</td>
					</tr>
 			</table>
		</fieldset>
		<br/>
		<fieldset>
		<table>
			<tr>
				<td align="right">	Tipo de Procedimiento:</td>
				<td align="left">
<!-- 					<input type="text" name="cCategoriaDescripcion" id="cCategoriaDescripcion" style="width: 30em;" readonly="readonly"> -->
<!-- 					<input type="hidden" name="cboCategoria" id="cboCategoria"> -->
					
					<select id="cboCategoria" name="cboCategoria" onchange="validaLicitacion2();" style="width: 30em;" >
							<option value="" selected="selected">
							</option>
					</select>
				</td>
				<td id="etiquetaChkActivo">
					
					¿Cuenta con documentos o autorización del comité?
				<BR>
					Si&nbsp;<input type="radio" id="doctosComite1" name="doctosComite" value="1" onclick="document.getElementById('Activo').value=this.value;validaCaratula();"/>
					No&nbsp;<input type="radio" id="doctosComite2" name="doctosComite" value="0" onclick="document.getElementById('Activo').value=this.value;validaCaratula();"/>
					</td>
			</tr>
		   	<tr>
				<td align="right">IVA:</td>
                <td align="left">
<!--                 <input type="text" id="ivaProcedimiento" name="ivaProcedimiento" onkeypress="return onlyPercentage(event);"style="width: 30em;" value=""/> -->
					<select id="ivaProcedimiento" name="ivaProcedimiento" style="width: 30em;"> 
					</select> 
                </td>
			</tr>
			<tr>
				<td align="right">Descripci&oacute;n:</td>
				<td align="left"><textarea id="descripcionCaratula" name="descripcionCaratula"  style="height: 91px; width: 400px" ></textarea></td>
			</tr>
			<tr>
				<td  align="right">N&uacute;mero de Procedimiento (COMPRANET)::</td>
				<td align="left"><input type="text" id="numero_externoCaratula" name="numero_externoCaratula"style="width: 30em;" maxlength="30" /></td>
				<td width="33%" align="center">
					<input type="button" name="btnAgregaCNET" id="btnAgregaCNET" value="Actualizar" onClick="agregaCNET()" class="btnInterfaceBG ui-button ui-corner-all" style="display: none"/>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="right">
			 		<div id="tablaFechasProcedimientoCaratula" style="width:100%;">
						<!-- Carga las fechas -->
					</div>
				</td>
			</tr>
			<tr>
					<td width="33%">&nbsp;</td>
					<td width="33%" align="center">
						<input type="button" name="btnGuardarCaratulaProcedimiento" id="btnGuardarCaratulaProcedimiento" value="GUARDAR"  class="btnInterfaceBG ui-button ui-corner-all" />
					</td>
					<td width="33%" align="right">&nbsp;</td>
			</tr>
			</table>
			</fieldset>
			<fieldset id="fieldsetCotizaciones">
				<legend>Cotizaciones</legend>
				<table>
					<tr>
					<td width="33%" align="center">
						<input type="button" name="btnAgregaCotizacion" id="btnAgregaCotizacion" value="Agregar" onClick="muestraVentanaCapturaCotizacion()" class="btnInterfaceBG ui-button ui-corner-all" />
					</td>
					</tr>
					<tr><td>* Doble clic para eliminar registro</td></tr>
				</table>
				<table id="tblProvedoresCotizaciones" width="750px">
					<thead>
						<tr>
							<th >RFC</th>
							<th >Raz&oacute;n Social</th>
							<th >Monto Cotizacion</th>
							<th >Sexo</th>
						</tr>
					</thead>
				</table>
			</fieldset>	
			 <fieldset id="fieldsetArrendamiento">
			 		<table>
			 			<tr >
							<td align="right">Uso y/o Denominacion Inmueble:</td>
							<td align="left"  ><select id="cboDenominacionInmueble" name="cboDenominacionInmueble" style="width: 25em;" onchange="muestraDenominacionOtros(this);" class="arrendamientos_div">
							<option value="" selected="selected"></option>
								</select><font color="red">*</font>
							</td>
							<td align="left">
							<textarea  id="descripcionOtros" name="descripcionOtros"  style="visibility: hidden;" style="height: 91px; width: 200px" class="arrendamientos_div" ></textarea>
							</td>
						</tr>
							
					</table>
							
					<table border="0"  width="750px" height="20" align="center">
							<tr>
							<td align="right">Area Construida:</td>
							<td align="left"><input type="text" id="area_construida" name="area_construida" style="width: 30em;"  class="arrendamientos_div" /></td><td align="left">m2 <font color="red">*</font></td>
						   </tr>
						
						   <tr>
							<td align="right">Area Rentable:</td>
							<td align="right"><input type="text" id="area_rentable" name="area_rentable" style="width: 30em;"  class="arrendamientos_div"/></td><td align="left">m2 <font color="red">*</font></td>
						   </tr>
						
						    <tr>
							<td align="right">No.Empleados que laboran en el Inmueble:</td>
							<td align="left"><input type="text" id="numero_empleados" name="numero_empleados" style="width: 30em;" class="arrendamientos_div" /></td><td align="left">Empleados <font color="red">*</font></td>
						   </tr>
							
							<tr>
							<td align="right">Direccion Inmueble:</td>
							<td align="left"><textarea  id="direccion_inmueble" name="direccion_inmueble"  style="height: 91px; width: 250px" class="arrendamientos_div" ></textarea><font color="red">*</font></td>
						    </tr>						   
							
							<tr>
							<td align="right">Unidad Administrativa que Ocupa el Inmueble:</td>
							<td align="left"><select id="cboUnidadEjecutoraInmueble" name="cboUnidadEjecutoraInmueble" style="width: 30em;" class="arrendamientos_div">
							<option value="" selected="selected">
									</option>
								</select><font color="red">*</font>
							</td>
							</tr>
			
				</table>
		
		</fieldset>
				
		<input id="Categoria" name="Categoria" type="hidden" size="4" >
		<input id="Activo" name="Activo" type="hidden" size="4" >
		<input id="esServicio" name="esServicio" type="hidden" size="4" >
		<input id="usuarioLogin" name="usuarioLogin"  type="hidden" size="10">
		<input id="EstadoCaptura" name="EstadoCaptura" type="hidden" size="10">
		<input id="cProcedimientoCumple" name="cProcedimientoCumple" value=""  type="hidden" size="10">
		<input id="tieneProveedor" name="tieneProveedor" type="hidden" size="10">
		<input id="imagenEstadoProcedimiento" name="imagenEstadoProcedimiento" value=""  type="hidden" size="10">
		<input id="cPartidaDesierta" name="cPartidaDesierta"  type="hidden" size="10">
		<input id="TipoConsolidado" name="TipoConsolidado" type="hidden" size="10">
		<input id="ConsecutivoConsolidado" name="ConsecutivoConsolidado" type="hidden" size="10">
		<input id="tipoProceso" name="tipoProceso" type="hidden" size="10">
		
		<input id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" type="hidden" size="10">
		<input id="usuarioLogin" name="usuarioLogin" type="hidden" size="10">
		<input id="usuarioRoleProcedimiento" name="usuarioRoleProcedimiento" type="hidden" size="10">
		<input id="cIdEntidadContable" name="cIdEntidadContable" type="hidden" size="10">
		<input id="nIdFechaProcedimiento" name="nIdFechaProcedimiento" type="hidden" size="10">
		<input id="nFechaProcedimiento" name="nFechaProcedimiento" type="hidden" size="10">
		<input id="documentoAplicado" name="documentoAplicado" type="hidden" size="10">
		<input id="MontoConIVA" name="MontoConIVA" type="hidden" size="10">
		<input id="cEventoFlujo" name="cEventoFlujo" type="hidden" size="10">
		<input id="partidasAdjudicadas" name="partidasAdjudicadas" type="hidden" size="10">
		
		<input type="hidden" name="cAccion" id="cAccion"/>
		<input type="hidden" name="cIdDocumento" id="cIdDocumento" />
		<input type="hidden" name="cIdUsuario" id="cIdUsuario" />
		<input type="hidden" name="isPlurianual" id="isPlurianual" />
		<input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE" value=""/>
		<input type="hidden" name="cIdTipoPersona" id="cIdTipoPersona" value=" " />
		<input type="hidden" name="cIdRFC" id="cIdRFC" value=" " />
		
		<input type="hidden" name="cRazonSocial2" id="cRazonSocial2" value=" " />
		<input type="hidden" name="cNombre2" id="cNombre2" value=" " />
		<input type="hidden" name="cApellidoPaterno2" id="cApellidoPaterno2" value=" " />
		<input type="hidden" name="cApellidoMaterno2" id="cApellidoMaterno2" value=" " />
		<input type="hidden" name="cMonto2" id="cMonto2" value=" " />
		<input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value=" " />
		<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuario.getLogin()%>"/>
		<input type="hidden" name="nIdSexo" id="nIdSexo" value="0" />
		
		</form>				
	</body>
</html>