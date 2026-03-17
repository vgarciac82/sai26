<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	Map rol =usuarioTab.getRoles();
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String name_user=usuarioTab.getLogin();
	String cIdContrato = "";
	String cIdContratoDefinitivo = "";
	String cIdConsecutivoMod = "";
	String cEjercicio = "";
	String cIdTipoContrato= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	String isConvEjercicioAnt="";
	if (session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo) != null) {
		cIdContratoDefinitivo = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo);
		cIdConsecutivoMod = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioConsecutivo);
		cIdContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioId);
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioEjercicio);
		isConvEjercicioAnt = (String)session.getAttribute(GestionInterface.ATT_ContratoIsModificatorioEjercicioAnt);
		
		cIdTipoContrato = cIdContrato.split("-")[0];
		cIdUnidadEjecutora = cIdContrato.split("-")[1];
		nIdConsecutivo = cIdContrato.split("-")[2];		
	}else 
		response.sendRedirect("ContratoModificatorio.jsp?tab=1");
	
%>
<!DOCTYPE html>
<html>
  <head>    
    <title>Presupuesto</title>    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">	
	<style>			// estilos 
			.input-group-append {
			  cursor: pointer;
			}
	</style>
	<script type="text/javascript" charset="utf-8">
		var oTableClaves;
		var roles="";
		$(document).ready(function() {
			tabb=3;
			showAndHideTabs();
				<%
			    String roles="";
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				int imgAnular=0;
				int imgAprobar=0;
				int imgDevolver=0;
				int imgPdf=0;
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"ContratoModificatorio","presupuestoContratoMod");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					if ("imgAprobarPresupuestoContMod".equals(img)){  
						%>
						$("#<%=img%>").attr("disabled", false);<%
						imgAprobar=1;
					}
					if ("imgDevolverPresupuestoContMod".equals(img)){
						%>
						$("#<%=img%>").attr("disabled", false);<%
						imgDevolver=1; 
					}	
				}

				%>
			roles="<%=roles%>";
			$("#isConvEjercicioAnt").val("<%=isConvEjercicioAnt %>");
			setReadOnly();
			headerQuery();
			showHidePestanas();
			var cIdContratoDef=$("#cContratoDefinitivo").val();
			if(1==$("#isConvEjercicioAnt").val()){
				validacionApartadoContEjercAnt();
			}else{
				if(cIdContratoDef.indexOf("PLU")>=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())<=0){
					validacionApartadoContPlu();
				}else{
					validacionApartado();
				}
			}
		});
		function validacionApartadoContEjercAnt(){
			//Para ver si tiene apartados en nApartadosUsados
			queryFormPost("fn_mApartadoContratoEjerAntModificadoCuenta", {async: false} );
			//Esconde el letrero de apartado encontrado
			$("#apartadoNotice").hide();
			//Si existe al menos un apartado, consulta el monto total - apartado disponible y lo pone en monto real
			if ($("#nApartadosUsados").val() > 0){
				queryFormPost("fn_mApartadoContratoEjerAntModificadoTotal", {async: false} );
				
				//Si el valor del apartado disponible es > 0
				if ($("#mApartadoReal").val() > 0){
					$("#mApartadoReal").formatCurrency();
					$("#aptdReal").html($("#mApartadoReal").val());
					$("#apartadoNotice").show();
				}
			}
			//Muestra las claves presupuestales
			loadClavesPresupuestalesContratoMod();
			if($("#cIdTipoProcedimiento").val()=="cIdTipoContrato"){
				$("#fechaEntrega").val($("#fechaInicio").val());
			}
			if(parseInt($("#tipoMod").val(),10)==2 || parseInt($("#tipoMod").val(),10)==3 || parseInt($("#tipoMod").val(),10)==4){
				$("#fieldsetClves").hide();
			}
			if(parseInt($("#tipoMod").val())!=1){
				$("#compromisoContratoMod").hide();
			}
		}
		function validacionApartadoContPlu(){
			//Para ver si tiene apartados en nApartadosUsados
			queryFormPost("fn_mApartadoContratoPluModificadoCuenta", {async: false} );
			//Esconde el letrero de apartado encontrado
			$("#apartadoNotice").hide();
			//Si existe al menos un apartado, consulta el monto total - apartado disponible y lo pone en monto real
			if ($("#nApartadosUsados").val() > 0){
				queryFormPost("fn_mApartadoContratoPluModificadoTotal", {async: false} );
				
				//Si el valor del apartado disponible es > 0
				if ($("#mApartadoReal").val() > 0){
					$("#mApartadoReal").formatCurrency();
					$("#aptdReal").html($("#mApartadoReal").val());
					$("#apartadoNotice").show();
				}
			}
			//Muestra las claves presupuestales
			loadClavesPresupuestalesContratoMod();
			if($("#cIdTipoProcedimiento").val()=="cIdTipoContrato"){
				$("#fechaEntrega").val($("#fechaInicio").val());
			}
			if(parseInt($("#tipoMod").val(),10)==2 || parseInt($("#tipoMod").val(),10)==3 || parseInt($("#tipoMod").val(),10)==4){
				$("#fieldsetClves").hide();
			}
		}
		function validacionApartado(){
			//Para ver si tiene apartados en nApartadosUsados
			queryFormPost("fn_mApartadoContratoModificadoCuenta", {async: false} );
			
			//Esconde el letrero de apartado encontrado
			$("#apartadoNotice").hide();
			//Si existe al menos un apartado, consulta el monto total - apartado disponible y lo pone en monto real
			if ($("#nApartadosUsados").val() > 0){
				queryFormPost("fn_mApartadoContratoModificadoTotal", {async: false} );
				
				//Si el valor del apartado disponible es > 0
				if ($("#mApartadoReal").val() > 0){
					$("#mApartadoReal").formatCurrency();
					$("#aptdReal").html($("#mApartadoReal").val());
					$("#apartadoNotice").show();
				}
			}
			
			//Muestra las claves presupuestales
			loadClavesPresupuestalesContratoMod();
			if($("#cIdTipoProcedimiento").val()=="PC"){
				$("#fechaEntrega").val($("#fechaInicio").val());
			}
			if(parseInt($("#tipoMod").val(),10)==2 || parseInt($("#tipoMod").val(),10)==3 || parseInt($("#tipoMod").val(),10)==4){
				$("#fieldsetClves").hide();
			}
			
		}
		function quitaFmt( val ) {
		   	val = val.replace("$", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
		   	}
		   	return val;
		}
		
		function loadClavesPresupuestalesContratoMod(){
			var campos = "'" + $("#cContratoDefinitivo").val() + "'," + $("#cConsecutivoMod").val();
			var funcion="fn_mApartadoContratoModificado(" + campos + ")";
			
			var cIdContratoDef=$("#cContratoDefinitivo").val();
			if(cIdContratoDef.indexOf("PLU")>=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())<=0){
				funcion="fn_mApartadoContratoPluModificado(" + campos + ")";
			}
			if(1==$("#isConvEjercicioAnt").val()){
				funcion="fn_mApartadoContratoEjercAntModificado(" + campos + ")";
				
			}
			oTableClaves=$('#dt_clavepresup').dataTable( {
					bPaginate: false,
        			bLengthChange: false,
        			bFilter: false,
        			bInfo: false,
					bAutoWidth: true,					
					bScrollCollapse: true,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,
					sScrollX: "100%",
					bRetrive : true,
					
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+ funcion,
					aoColumns: [
						{ sName: "ep"},
						{ sName: "claveInterna" }
						]
			}) ;
		}
			
		function setReadOnly(){
			document.getElementById("lblUnidadEjecutora").style.readonly = true;
			document.getElementById("lblProcedimiento").style.readonly = true;
			document.getElementById("lblDefinitivo").style.readonly = true;
			document.getElementById("lblContrato").style.readonly = true;
			document.getElementById("lblProveedor").style.readonly = true;
			document.getElementById("lblEstadoMod").style.readonly = true;
			document.getElementById("lblTotalAnterior").style.readonly = true;
			document.getElementById("lblTotalModificado").style.readonly = true;
			document.getElementById("lblTotal").style.readonly = true;
		}
		function headerQuery(){
			var cIdContratoDef=$("#cContratoDefinitivo").val();
			if(1==$("#isConvEjercicioAnt").val()){
				queryFormPost("mContratoHeaderReadConvAnt", {async: false});
				queryFormPost("mContratoModificadoAnteriorTotales", {async: false});
			}else{
				if(cIdContratoDef.indexOf("PLU")>=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())<=0){
					queryFormPost("mContratoHeaderReadPLU", {async: false});
					queryFormPost("mContratoPluModificadoTotales", {async: false});
				}else{
					queryFormPost("mContratoHeaderRead", {async: false});
					if(parseInt($("#lContratoAbierto").val())==1){
						queryFormPost("mContratoModificadoTotalesMax", {async: false});
					}else{
						queryFormPost("mContratoModificadoTotales", {async: false});
					}
				}	
			}
			queryFormPost("cg_roleRead", {async: false});
			
			if(parseInt($("#nIdEstado").val(),10) == 1 ){
				$("#precompromisoContratoMod").hide();
				$("#imgDevolverPresupuestoContMod").css("display", "none");
				
			}else{
				//Mostrar boton validaPrecomContMat
				$("#validaPrecomContMat").css("visibility","visible");
				$("#imgAprobarPresupuestoContMod").css("display", "none");
			}
			queryFormPost("mContratoModicadoChecaRolUsuario", {async: false});
			queryFormPost("mContratoModicadoUsuarioCreacionOriginalRead",{async: false });
			habilitaDeshabilitaFechas();
			queryFormPost("mContratoObtieneFechasOriginalModificacion",{async: false });
		}
		function fnGetSelected( oTableLocal ) {
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();			
			for ( var i=0 ; i<aTrs.length ; i++ )
			{
				if ( $(aTrs[i]).hasClass('row_selected') )
				{
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
		}
		function habilitaDeshabilitaFechas(){
			agregaDatePickerFechas();
			if($("#cIdTipoProcedimiento").val()=="PC"){
				document.getElementById("trfini").style.display="none";
				document.getElementById("trffin").style.display="none";
				document.getElementById("trfentrega").style.display="block";
			}else{
				document.getElementById("trfini").style.display="block";
				document.getElementById("trffin").style.display="block";
				document.getElementById("trfentrega").style.display="none";
			}
		
			if( $("#nIdEstado").val() == "2" || $("#nIdEstado").val() == "3" || $("#nIdEstado").val() == "5" ){
				document.getElementById("trffor").disabled = false;
				document.getElementById("trfini").disabled = false;
				document.getElementById("trffin").disabled = false;
				document.getElementById("trfentrega").disabled=false;
				document.getElementById("fechaFormalizacion").disabled=false;
				document.getElementById("fechaInicio").disabled=false;
				document.getElementById("fechaFin").disabled=false;
				document.getElementById("fechaEntrega").disabled=false;
				document.getElementById("trNumConvenio").disabled=false;
				document.getElementById("trObjetoConvenio").disabled=false;
				$("#btnGuardarCaratulaCont").css("display", "block");
			}
			else if($("#nIdEstado").val() == "4"  || $("#nIdEstado").val() == "1" ){
				document.getElementById("trffor").disabled=true;
				document.getElementById("trfini").disabled=true;
				document.getElementById("trffin").disabled=true;
				document.getElementById("trfentrega").disabled=true;
				document.getElementById("fechaFormalizacion").disabled=true;
				document.getElementById("fechaInicio").disabled=true; 
				document.getElementById("fechaFin").disabled=true;
				document.getElementById("fechaEntrega").disabled=true;
				document.getElementById("trNumConvenio").disabled=true;
				document.getElementById("trObjetoConvenio").disabled=true;
				document.getElementById("btnGuardarCaratulaCont").disabled=true;
				$("#btnGuardarCaratulaCont").css("display", "none");
			}
		
		}
		function guardaContratoMod(){
			var imgAprobar='<%=imgAprobar%>';
			if(imgAprobar==0){
				if($("#cContratoDefinitivo").val().indexOf("PLU")>=0&&$("#cContratoDefinitivo").val().indexOf("/"+$("#cEjercicio").val())<=0){
					$("#isPLUEjercicioAnt").val(1);
				}
			
				var proc=""+$("#cEjercicio").val()
							+","+$("#cContratoDefinitivo").val()
							+","+$("#cConsecutivoMod").val()
							+","+$("#U_LOGIN").val()
							+","+$("#nTipoPago").val()
							+","+$("#isConvEjercicioAnt").val()
							+","+$("#isPLUEjercicioAnt").val();
				
				if (roles.indexOf("ADMIN_RECMAT")< 0 && roles.indexOf("JEFES")< 0 && roles.indexOf("ANALISTA")< 0 && roles.indexOf("Estatales")< 0) { 
					if ($("#usuarioCreacionOriginal").val() != $("#usuarioLogin").val()){
						swal("No tiene permisos para realizar esta acción",{icon:"info",button: "Cerrar"});
						return;
					}
				}
				
				if ($("#nIdEstado").val() > 1) {
					swal("No es posible aprobar el presupuesto de este modificatorio",{icon:"info",button: "Cerrar"});
					return;
				}
				
				//Si existe un apartado, recarga las eps de apartado
				if (parseInt($("#tipoMod").val(),10)==0 && $("#nApartadosUsados").val() > 0 && quitaFmt($("#mApartadoReal").val()) > 0  ){
					if(1==$("#isConvEjercicioAnt").val()){
						queryFormPost("pa_mAptdPrccEPModEjeAnt", {async: false});
					}else{
						queryFormPost("pa_mAptdPrccEPMod", {async: false});	
					}
				}
				else {
					if(parseInt($("#tipoMod").val(),10)==0){
						swal("No es necesario aprobar este modificatorio porque no tiene EPs asociadas o estas no cuentan con presupuesto",{icon:"info",button: "Cerrar"});
						return;
					}
				}
				
				//Llama al ContratoServlet para llamar el stored procedure que aprueba el Contrato
				$.getJSON("../../servlet/ContratoModificadoServlet?operacion=0",{Tabla: "", Param: proc, MaxReg: "", ajax: 'false'}, function(j){
							for(var i = 0; i < j.length; i++)
								 var col=j[i].Col1
							switch(col){
							case "0": 
								swal({
									title: "",
									text: "El Contrato Modificatorio se ha aprobado tiene que Actualizar las Fechas del Contrato Modificatorio",
									icon: "info",
									buttons: {
										confirm : "Cerrar"
										},
									}).then((continuar) => {
										var cIdContratoDef=$("#cContratoDefinitivo").val();
										if(1==$("#isConvEjercicioAnt").val()){
											queryFormPost("mContratoHeaderReadConvAnt", {async: false});
											queryFormPost("mContratoModificadoAnteriorTotales", {async: false});
										}else{
											if(cIdContratoDef.indexOf("PLU")>=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())<=0){
												queryFormPost("mContratoHeaderReadPLU", {async: false});
												queryFormPost("mContratoPluModificadoTotales", {async: false});
											}else{
												queryFormPost("mContratoHeaderRead", {async: false});
												if(parseInt($("#lContratoAbierto").val())==1){
													queryFormPost("mContratoModificadoTotalesMax", {async: false});
												}else{
													queryFormPost("mContratoModificadoTotales", {async: false});
												}
											}
										}
											

										//deshabilita la imagen de aprobar una vez que se ha aprobado el Contrato
										$("#imgAprobarPresupuestoContMod").hide();
										$("#imgDevolverPresupuestoContMod").show();
										
										$("#nIdEstado").val("2");
										$("#precompromisoContratoMod").show();
										habilitaDeshabilitaFechas();
										$("#fechaFormalizacion").focus();
										//window.location = "ContratoModificatorio.jsp?tab=4";
										location.reload();
								});
							break;
							case "1":  
								swal("Este Modificatorio ya se encuentra aprobado",{icon:"info",button: "Cerrar"});
							break;
							case "2":  
								swal("Este proveedor no se encuentra registrado en el sistema Financiero",{icon:"info",button: "Cerrar"});
							break;
							case "3":  
								swal("No se han registrado EPs para este Contrato",{icon:"info",button: "Cerrar"});
							break;
							case "4":  
								swal("Ha ocurrido un error al registrar el Contrato, por favor contacte a su administrador",{icon:"info",button: "Cerrar"});
							break;
							case "5":  
								swal("Ha ocurrido un error al registrar las EPs, por favor contacte a su administrador",{icon:"info",button: "Cerrar"});
							break;
							case "6","7":  
								swal("Ha ocurrido un error al actualizar el Contrato, por favor contacte a su administrador",{icon:"info",button: "Cerrar"});
							break;
							case "-1":  
								swal("Ha ocurrido un error no se puede aprobar el presupuesto contacte a su administrador",{icon:"info",button: "Cerrar"});
							break;
							
							
							}
					});
			}else{
				swal("Usted no tiene permiso para realizar esta accion, contacte a su administrador",{icon:"info",button: "Cerrar"});
			}
		}
		
		//devuelve el Contrato
		function devuelveContratoMod(){
			var imgDevolver='<%=imgDevolver%>';
			if (imgDevolver == 0){
			if(parseInt($("#nIdEstado").val(),10) == 2 || parseInt($("#nIdEstado").val(),10) == 5 ){
						var proc=""+$("#cEjercicio").val()
						+","+$("#cContratoDefinitivo").val()
						+","+$("#cConsecutivoMod").val()
						+","+$("#nTipoPago").val();
					$.getJSON("../../servlet/ContratoModificadoServlet?operacion=1",{Tabla: "", Param: proc, MaxReg: "", ajax: 'true'}, function(j){
				 		for(var i = 0; i < j.length; i++)
		                	 var col=j[i].Col1;
		                switch(col){
						case "0":  
							swal({
								title: "",
								text: "Se ha devuelto el Contrato Modificatorio",
								icon: "info",
								buttons: {
									confirm : "Cerrar"
									},
								}).then((continuar) => {
									var cIdContratoDef=$("#cContratoDefinitivo").val();
									if(1==$("#isConvEjercicioAnt").val()){
										queryFormPost("mContratoHeaderReadConvAnt", {async: false});
										queryFormPost("mContratoModificadoAnteriorTotales", {async: false});
									}else{
										if(cIdContratoDef.indexOf("PLU")>=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())<=0){
											queryFormPost("mContratoHeaderReadPLU", {async: false});
											queryFormPost("mContratoPluModificadoTotales", {async: false});
										}else{
											queryFormPost("mContratoHeaderRead", {async: false});
											if(parseInt($("#lContratoAbierto").val())==1){
												queryFormPost("mContratoModificadoTotalesMax", {async: false});
											}else{
												queryFormPost("mContratoModificadoTotales", {async: false});
											}
										}	
									}
									
									$("#imgDevolverPresupuestoContMod").hide();
									$("#imgAprobarPresupuestoContMod").show();
									
									
									$("#nIdEstado").val("1");
									$("#precompromisoContratoMod").hide();
									habilitaDeshabilitaFechas();
									location.reload();
							});
							
						break;
						case "1":  
							swal("No se puede regresar el modificatorio porque su estado no lo permite",{icon:"info",button: "Cerrar"});
						break;
						case "2":  
							swal("Ha ocurrido un error al eliminar las EPs del modificatorio, por favor contacte a su administrador",{icon:"info",button: "Cerrar"});
						break;
						case "3":  
							swal("Ha ocurrido un error al devolver el modificatorio, por favor contacte a su administrador",{icon:"info",button: "Cerrar"});
						break;
						case "4":
							swal("Ha ocurrido un error al cambiar el estado del modificatorio, por favor contacte a su administrador",{icon:"info",button: "Cerrar"});
						break;
						case "-1":  
							swal("Ha ocurrido un error no se puede devolver el contrato",{icon:"info",button: "Cerrar"});
						break;
						
	                 	}
				});
			}
		}else{
			swal("No tiene permiso para realizar esta acción, contacte a su administrador",{icon:"info",button: "Cerrar"});
		}
	}
	
	
	function agregaDatePickerFechas(){
		$("#fechaFormaliza").datetimepicker({
			format: 'DD/MM/YYYY',
			altField: "#actualDate",
		 	currentText: "Now",
			changeYear: true
			
		});
		$("#fInicial").datetimepicker({
			format: 'DD/MM/YYYY',
			altField: "#actualDate",
		 	currentText: "Now",
			changeYear: true
			
		});
		$("#fFinal").datetimepicker({
			format: 'DD/MM/YYYY',
			altField: "#actualDate",
		 	currentText: "Now",
			changeYear: true
			
		});
		$("#fEntrega").datetimepicker({
			format: 'DD/MM/YYYY',
			altField: "#actualDate",
		 	currentText: "Now",
			changeYear: true
			
		});
			
	}
	function guardar(){
		if($("#cIdTipoProcedimiento").val()=="PC"){
			$("#fechaFin").val($("#fechaEntrega").val());
			$("#fechaInicio").val($("#fechaEntrega").val());
		}
		if($("#objConv").val()==""){
			swal("El objeto del convenio es un dato requerido.",{icon:"info",button: "Cerrar"});
			return
		}
		if($("#cNoConvenio").val()==""){
			swal("El número del convenio es un dato requerido.",{icon:"info",button: "Cerrar"});
			return
		}
		//llamamos al servlet que revisara las fechas
		$.ajax({url: '../../servlet/ContratoModificadoServlet' , type:'post' , async: false
			,data:'operacion=12&fechaFormalizacion='+$("#fechaFormalizacion").val()+"&fechaInicio="+$("#fechaInicio").val()
			+"&fechaFin="+$("#fechaFin").val()+"&cIdContratoDefinitivo="+$("#cContratoDefinitivo").val()
			+"&cConsecutivoMod="+$("#cConsecutivoMod").val()+"&cNoConvenio="+$("#cNoConvenio").val()
			+"&objConv="+$("#objConv").val()
			
			, dataType: 'json', success:
			function(j){
				var col=j[0].Col1;
				swal(j[0].MENSAJE  ,{icon:"info",button: "Cerrar"});
			}
		});
	}
	
	function POPUPpreVentanilla(){
	  $("#cIdContratoDefinitivoMod").val($("#cContratoDefinitivo").val()+"#M"+ $("#cConsecutivoMod").val());
	  
		var contrato=encodeURIComponent($("#cIdContratoDefinitivoMod").val())
	
		window.open("validaPrecomContratoMateriales.jsp?cIdContrato="+contrato, 'Notas', 'status=1, width=1000px, height=780px, left=150px ,scrollbars = 1');
	}
	
	
	</script>
</head>  
<body>
	<form id="formPresupuesto">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Datos del Contrato Modificado</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarPresupuestoContMod" name="imgAprobarPresupuestoContMod" 	value="Aprobar"	onclick="guardaContratoMod();" />
							<input type="button" class="btnInterfaceReturn ui-button ui-corner-all float-right" 	id="imgDevolverPresupuestoContMod" name="imgDevolverPresupuestoContMod" 	value="Devolver"	onclick="devuelveContratoMod();" />
							<input type="button" class="btnInterfaceBackToTop ui-button ui-corner-all float-right" 	id="imgSalir" 	name="imgSalir" value="Salir" onclick="window.location = 'ContratoModificatorio.jsp?tab=1';" />
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblProcedimiento" id="lblProcedimiento" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblDefinitivo" id="lblDefinitivo" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-12">
						<input type="text" class="form-control transpInput" name="lblContrato" id="lblContrato" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblProveedor" id="lblProveedor" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput font-weight-bold" name="lblEstadoMod" id="lblEstadoMod" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTipoMod" id="lblTipoMod" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalContratoOriginal" id="lblTotalContratoOriginal" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalAnterior" id="lblTotalAnterior" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalModificado" id="lblTotalModificado" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotal" id="lblTotal" readonly />
					</div>
				</div>
				<div class="row" id="apartadoNotice">
					<div class="col">
						<span style="color:#33CC00;">Monto cubierto por el apartado: <span id="aptdReal"></span>.</span>
					</div>
				</div>
				<div class="row" style="display: none;">
					<div class="col">
						<input type="button" name="validaPrecomContMat" id="validaPrecomContMat" onclick="POPUPpreVentanilla();" value="As&iacute; lo ver&iacute;a Ventanilla" style="visibility:hidden; color: red; border-bottom: 0;background-color: white;">
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3" id="fechas">
			<legend class="w-auto px-2">Edici&oacute;n del Contrato Modificado</legend>
			<div class="form-group">
				<div class="row" id="trObjetoConvenio">
					<div class="col">
						<label for="objConv">Objeto Convenio</label>
						<input type="text" class="form-control" maxlength="285" placeholder="Objeto del convenio modificatorio" aria-label="Objeto del convenio modificatorio" aria-describedby="basic-addon1"  id="objConv" name="objConv">
					</div>
				</div>
				<div class="row" id="trNumConvenio">
					<div class="col-md-4">
						<label for="cNoConvenio">N&uacute;mero. de Convenio</label>
						<input type="text" class="form-control" placeholder="Número de convenio modificatorio CNET" aria-label="Número de convenio modificatorio CNET" aria-describedby="basic-addon1"  id="cNoConvenio" name="cNoConvenio">
					</div>
				</div>
				<div class="row" id="trffor">
					<div class="col-md-4"  >
						<label for="fechaFormaliza">Fecha de Formalizaci&oacute;n</label>
						<div class="input-group date" id="fechaFormaliza" data-target-input="nearest">
				          <input type="text" class="form-control datetimepicker-input" data-target="#fechaFormaliza" title="Fecha de Formalización del convenio modificatorio" id="fechaFormalizacion" name="fechaFormalizacion" value=""/>
				          <div class="input-group-append" data-target="#fechaFormaliza" data-toggle="datetimepicker" title="Fecha de Formalización del convenio modificatorio">
				            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
				          </div>
				        </div>
			        </div>
				</div>
				<div class="row" id="trfini" style="display: none;">
			        <div class="col-md-4" >
						<label for="fInicial">Fecha Inicio</label>
						<div class="input-group date" id="fInicial" data-target-input="nearest">
				          <input type="text" class="form-control datetimepicker-input" data-target="#fInicial" title="Fecha inicio del convenio modificatorio" id="fechaInicio" name="fechaInicio" value=""/>
				          <div class="input-group-append" data-target="#fInicial" data-toggle="datetimepicker" title="Fecha inicio del convenio modificatorio">
				            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
				          </div>
				        </div>
			        </div>
				</div>
				<div class="row"  id="trffin" style="display: none;">
			        <div class="form-group col-md-4"  >
						<label for="fFinal">Fecha Fin</label>
						<div class="input-group date col-xs-2" id="fFinal" data-target-input="nearest" >
							<input type="text" class="form-control datetimepicker-input" data-target="#fFinal" id="fechaFin" name="fechaFin" title="Fecha final del convenio modificatorio" value=""  />
							<div class="input-group-append" data-target="#fFinal" data-toggle="datetimepicker" title="Fecha final del convenio modificatorio">
							  <div class="input-group-text"><i class="fa fa-calendar"></i></div>
							</div>
						</div>
					</div>
		        </div>
		        <div class="row"  id="trfentrega" style="display: none;">
			        <div class="form-group col-md-4"  >
						<label for="fEntrega">Fecha de Entrega</label>
						<div class="input-group date col-xs-2" id="fEntrega" data-target-input="nearest" >
							<input type="text" class="form-control datetimepicker-input" data-target="#fEntrega" id="fechaEntrega" name="fechaEntrega" title="Fecha entrega de los bienes" value=""  />
							<div class="input-group-append" data-target="#fEntrega" data-toggle="datetimepicker" title="Fecha entrega de los bienes">
							  <div class="input-group-text"><i class="fa fa-calendar"></i></div>
							</div>
						</div>
					</div>
		        </div>
		        <div class="row">
					<div class="input-group" >
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="btnGuardarCaratulaCont" name="btnGuardarCaratulaCont" 	value="Guardar"	onclick="guardar();" />
						</div>
					</div>
				</div>
			</div>
			
		</fieldset>
		<fieldset class="form-group border p-3" id="fieldsetClves">
			<legend class="w-auto px-2">Estructuras presupuestales "EP"</legend>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<table id="dt_clavepresup" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th>C&oacute;digo SAI "EP"</th> 
									<th>Clave SHCP</th> 
								</tr>										
							</thead>
						</table>
					</div>
				</div>
			</div>
		</fieldset>
		
        <!-- Hiddens de sesion -->
        <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
	    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
	    <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
	    <input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoContrato%>" />
	    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab.getLogin() %>"/>
	    <input type="hidden" name="cContrato" id="cContrato" value="<%=cIdContrato%>" />
	    <input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
	    <input type="hidden" name="contratoDefinitivo" id="contratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
	    <input type="hidden" name="cConsecutivoMod" id="cConsecutivoMod" value="<%=cIdConsecutivoMod%>" />
	    <input type="hidden" name="cIdContratoDefinitivoMod" id="cIdContratoDefinitivoMod" />
	    <input type="hidden" name="cIdTipoProcedimiento" id="cIdTipoProcedimiento"/>
	    
	    <input type="hidden" name="tipoMod" id="tipoMod" />
        <input type="hidden" name="totalAnterior" id="totalAnterior" />
	    <input type="hidden" name="totalMod" id="totalMod" />
	    <input type="hidden" name="totalNuevo" id="totalNuevo" />
	    <input type="hidden" name="nIdEstado" id="nIdEstado" />
	    <input type="hidden" name="nTipoPago" id="nTipoPago" />
		
		<!-- Para unir apartado con precompromiso -->
	    <input type="hidden" name="nApartadosUsados" id="nApartadosUsados" />
	    <input type="hidden" name="mApartadoReal" id="mApartadoReal" />
		<input type="hidden" name="cIdProcedimiento" id="cIdProcedimiento" />
		
	    <!--  Auxiliares para consulta -->		    
       	<input type="hidden" name="epAUX" id="epAUX" />
       	<input type="hidden" name="cIdUnidadEjecutoraEP" id="cIdUnidadEjecutoraEP" />
	    <input type="hidden" name="R_NOMBRE" id="R_NOMBRE" />
	    <input type="hidden" name="epsConsulta" id="epsConsulta" />
	    
	    <input type="hidden" name="usuarioCreacionOriginal" id="usuarioCreacionOriginal" value="" />
 		<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=name_user%>" />
 		<input type="hidden" name="usuarioLoginRole" id="usuarioLoginRole" value="" />
	    <input type="hidden" name="lContratoAbierto" id="lContratoAbierto" value="0" />
	    <input type="hidden" name="isConvEjercicioAnt" id="isConvEjercicioAnt" value="0" />
	    <input type="hidden" name="isPLUEjercicioAnt" id="isPLUEjercicioAnt" value="0" />
	</form>
  </body>
</html>
