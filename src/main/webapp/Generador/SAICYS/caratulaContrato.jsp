<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%	
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
	String cCentroContable="";
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	if (usuarioTab.getPropiedades() != null && usuarioTab.getPropiedades().containsKey("CCENTROCONTABLE")){
		cCentroContable = usuarioTab.getPropiedad("CCENTROCONTABLE").getValor();
	}
	Map<String, Role> rol =usuarioTab.getRoles();
	String name_user=usuarioTab.getLogin();
	String cEjercicio = "";
	String cIdTipoContrato= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	if (session.getAttribute(GestionInterface.ATT_ContratoEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoEjercicio);
		cIdTipoContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoTipoContrato);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ContratoUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ContratoConsecutivo);		
	}else 
		response.sendRedirect("Contratos.jsp?tab=0");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Car&aacute;tula Contrato</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" charset="utf-8">
		var roles="";
		cIdTipoContrato="<%=cIdTipoContrato%>";
		$(document).ready(function() {
			$("#tbs").val(1);
			showAndHideTabs();
		<%
			   
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
			//	NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"Contratos","caratulaContrato");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}

		%>	
         //primero se tienen que validar que los datos de las fechas asi como la informacion de la caratula sea la correcta para poder habilitar las pestañas
		    queryFormPost("consultaContratoImpuestoAdicional1", {async : false});
			queryFormPost("consultaContratoImpuestoAdicional2", {async : false});
			queryFormPost("consultaContratoImpuestoAdicional3", {async : false});
			
			queryFormPost("consultaContratoOtroImpuesto1", {async : false});
			queryFormPost("consultaContratoOtroImpuesto2", {async : false});
			queryFormPost("consultaContratoOtroImpuesto3", {async : false});
			queryInnerDivPost("minimoMaximoTipoProcedimiento", {async : false});
			
			
		    deshabilitaTabs();
			setFieldsInit();
			validaCondicionesIniciales();
			roles="<%=roles%>";

			if($("#cDescripcionImpuestoAdicional1").val()!="" || $("#cDescripcionImpuestoAdicional2").val()!="" || $("#cDescripcionImpuestoAdicional3").val()!=""){
				//$("#divImpuestosAdicionales").css("visibility","visible");
				document.getElementById("divImpuestosAdicionales").style.display="block";
				document.getElementById("chkImpuestosAdicionales").checked=true;
			}
			
			if($("#cDescripcionOtroImpuesto1").val()!="" || $("#cDescripcionOtroImpuesto2").val()!="" || $("#cDescripcionOtroImpuesto3").val()!=""){
				document.getElementById("divOtrosImpuestos").style.display="block";
				document.getElementById("chkOtrosImpuestos").checked=true;
				if($("#nIdEstado").val()!=1){
					document.getElementById("MESSAGESPAN").style.display="block";
					$("#cDescripcionOtroImpuesto1").prop("readonly",true);
					$("#cDescripcionOtroImpuesto2").prop("readonly",true);					
					$("#cDescripcionOtroImpuesto3").prop("readonly",true);					
					$("#cMontoOtroImpuesto1").prop("readonly",true);					
					$("#cMontoOtroImpuesto2").prop("readonly",true);					
					$("#cMontoOtroImpuesto3").prop("readonly",true);
				}
			}
			
			
			//se checa si es un contrato de arrendamientos
			if($("#cIdTipoContrato").val()=="CA"){
				habilitaTabs();
			}else{
				if(!validaCampos()) {
				       swal("Existe un problema con las Fechas,favor de revisarlas",{icon:"info",button: "Cerrar"});
			     }else{
			    	 habilitaTabs(); 
			     }
			}
			
           //Desabilitar combo box categoria si el usuario es diferente del administrador
			if(roles.toString().indexOf("ADMIN_RECMAT") < 0 &&roles.indexOf("ANALISTA")<0 &&roles.indexOf("JEFE")<0 && parseInt($("#usuariosMismaUE").val(),10)==0){//document.getElementById("trErrorFechas").style.visibility='visible';
				$("#cboCategoriaCaratula").css("visibility","hidden");
				$("#Categoria").css("visibility","hidden");
			}
			else{
				$("#cboCategoriaCaratula").css("visibility","visible");
				$("#Categoria").css("visibility","visible");
			}
			
			if ($("#cIdTipoProcedimiento").val() == 'PF') {
				$( "#presupuestoContrato" ).attr("disabled", true);
				$( "#preCompromisoContrato" ).attr("disabled", true);
				$( "#pagosContrato" ).attr("disabled", true);
				$( "#plurianualidad" ).attr("disabled", true);
				$( "#pasivo" ).attr("disabled", true);
			}
			iniciaRadioBtn();
			var nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoContrato)%>;
			if(nIdEstado == 1)
				$("#preCompromisoContrato").css("display", "none");
			else{
				$("#preCompromisoContrato").css("display", "block");
				if(nIdEstado==4){
					queryFormPost("mValidaCompAutSicop", {async: false});
				}
			}
				
			queryFormPost("mUsuarioMismaUE", {async: false   });
			queryFormPost("cargaDoc", {async: false   });
			
			queryFormPost("ExisteHiperv", { async : false});
			if($("#existeRegistro").val()!=0){
				actualiza=true;
			}
			seExcentoGarantia();
			formato();
			consultaSelects();	
			showPrestacionServicio();
			showHideButtonAprobar();
		});
		function showHideButtonAprobar(){
			if((cIdTipoContrato=="CS" || cIdTipoContrato=="CR") && parseInt($("#nIdEstado").val(),10)==1){
				if($("#cnumCompranet").val()!=""){
					$("#imgAprobArt25").show();
					$("#textAutorizaContratoArt25").show();
				}
			}
			
		}
		function setFieldsInit(){
		
		
			//set de readonly a etiquetas
			document.getElementById("lblUnidadEjecutora").style.readonly=true;
			document.getElementById("lblProcedimiento").style.readonly=true;
			document.getElementById("lblDefinitivo").style.readonly=true;
			document.getElementById("lblContrato").style.readonly=true;
			document.getElementById("lblProveedor").style.readonly=true;
			document.getElementById("lblEstado").style.readonly=true;
			document.getElementById("lblEstadoSICOP").style.readonly=true;
			querySelectPost("CategoriaReadContrato", "cboCategoriaCaratula", {async : false});								
			//querySelectPost("CategoriaRead", "cboCategoriaCaratula", {async : false});

			//set de readonly a fechas
			document.getElementById("fechaFalloV").style.readonly=true;
			document.getElementById("fechaFormalizacionV").style.readonly=true;
			document.getElementById("fechaInicioV").style.readonly=true;
			document.getElementById("fechaFinV").style.readonly=true;	
			document.getElementById("fechaEntregaV").style.readonly=true;		
			//Carga de controles, caratula y hiddens
			queryFormPost("mContratoCaratulaRead", {async: false,
				callback:function(){
					muestraObserv();
					if($("#isPlurianual").val()==1){
						$("#trOficioDG").show();
						$("#trFolioMascp").show();
					}else{
						$("#trOficioDG").hide();
						$("#trFolioMascp").hide();
					}
					if($("#nCodContratoCNET").val()==0)	{
						$("#nCodContratoCNET").val('');
					}
					if($("#nCodExpedienteCNET").val()==0)	{
						$("#nCodExpedienteCNET").val('');
					}
					if($("#nllevaAnticipo").val()==1){
						document.getElementById("checkLlevaAnticipo").checked=true;
					}
				}
			
			});
			queryFormPost("mContratoHeaderRead", {async: false});
			
			queryFormPost("fnMontoImpuesto1ReadContrato", {async: false});
			queryFormPost("fnMontoImpuesto2ReadContrato", {async: false});
			queryFormPost("fnMontoImpuesto3ReadContrato", {async: false});
			
			//Leer la categoria del procedimiento vinculado al contrato
			//queryFormPost("mContratoCategoriaProcemientoRead", {async: false});
			//obtiene rol del usuario
			queryFormPost("cg_roleRead", {async: false});
			if(parseInt($("#cIdCategoriaProcedimiento").val(),10)==13){
				$("#nIdFechaInicio").val(15);
				$("#nIdFechaFin").val(16);
			}else{
				$("#nIdFechaInicio").val(17);
				$("#nIdFechaFin").val(18);
			} 
			
			if ($("#cIdCategoriaProcedimiento").val()==2 || $("#cIdCategoriaProcedimiento").val()==5 || $("#cIdCategoriaProcedimiento").val()==6 || $("#cIdCategoriaProcedimiento").val()==8 ){
				$("#trffal").show();
				queryFormPost("mContratoFechaFallo", {async: false});
			}else{
				$("#trffal").hide();
			}
			if(parseInt($("#cIdCategoriaProcedimiento").val(),10)!=11){
				$("#trCodContCNET").show();
				$("#trCodExpCNET").show();
			}else{
				$("#inpNumContCNET").val("No. Convenio de Colaboraci\u00f3n");
			}
				
			/* queryFormPost("mContratoFechaFallo", {async: false}); */
			if(parseInt($("#nIdEstado").val(),10)==1){
				queryFormPost("mContratoFechaFormalizacion", {async: false});	
			}
			
			if($("#cIdTipoProcedimiento").val()=="PS" || $("#cIdTipoProcedimiento").val()=="PA" || $("#cIdTipoProcedimiento").val()=="PL" 
			|| $("#cIdTipoProcedimiento").val()=="PN" || $("#cIdTipoProcedimiento").val()=="PO"){
				queryFormPost("mContratoFechaInicio", {async: false});
				queryFormPost("mContratoFechaFin", {async: false});
				$("#trffin").show();
				$("#trfentrega").hide();
			}else{//Compra
				if(parseInt($("#nIdEstado").val(),10)==1){
					queryFormPost("mPedidoFechaEntrega,mContratoFechaInicio", {async: false});
				}
				$("#trffin").hide();
				$("#trfentrega").show();
				//$("#fechaInicio").val($("#fechaEntrega").val());
				$("#fechaFin").val($("#fechaEntrega").val());
				
			}
			
			
			queryFormPost("mContratoFechaInicioFinConvenio", {async: false}); 
			if($("#fechaInicioTMP").val().length > 0){
				$("#fechaInicio").val($("#fechaInicioTMP").val());
			}
			if($("#fechaFinTMP").val().length > 0){
				$("#fechaFin").val($("#fechaFinTMP").val());
			}

			//Substring del tipo de cambio para que sea solo de dos digitos
			$("#mTipoCambioV").val($("#mTipoCambio").val());
			$("#cConceptoV").val($("#cConcepto").val());
			$("#cContratoDefinitivoV").val($("#cContratoDefinitivo").val());
			$("#fechaFalloV").val($("#fechaFallo").val());
			
			$("#cboCategoriaCaratula").val($("#cIdCategoriaProcedimiento").val());
			querySelectPost("FundamentoLegReadFiltrado", "cboFundamentoLeg", {async : false});
			$( "#cboCategoriaCaratula" ).attr("disabled", true);
			$("#cboFundamentoLeg").val($("#ValObtenidoFL").val());
			queryFormPost("mUpdateProcedimientoFundamentoLeg", {async: false});
			
			document.getElementById("lblTotal").style.readonly=true;
			$("#fechaFormalizacionV").val($("#fechaFormalizacion").val());
			
			if ($("#cCategoriaProcedimiento").val().toLowerCase().indexOf('colaboraci') >= 0){				
				$("#fechaSolicitudV").val($("#fechaSolicitud").val());
				$("#fechaPropuestasV").val($("#fechaPropuestas").val());
			}
			else {			
				$("#fechaFalloV").val($("#fechaFallo").val());
				$("#fechaInicioV").val($("#fechaInicio").val());
				$("#fechaFinV").val($("#fechaFin").val());
			}
			
			$("#fechaInicioV").val($("#fechaInicio").val());
			$("#fechaFinV").val($("#fechaFin").val());
			$("#fechaEntregaV").val($("#fechaEntrega").val());		
			if($("#lblImporteImpuesto1").val() == ""){
				$("#divImporteImpuesto1").css("display","none");
			}
			if($("#lblImporteImpuesto2").val() == ""){
				$("#divImporteImpuesto2").css("display","none");
			}
			if($("#lblImporteImpuesto3").val() == ""){
				$("#divImporteImpuesto3").css("display","none");
			}
			//queryFormPost("fnMontoTotalContrato", {async: false});
			queryFormPost("consultaEstadoSICOP", {async : false});			
		}
		function formateaMoneda(importe){
			var importeSeparado = importe.toString().split("\.");
			var importeParte1 = importeSeparado[0];
			var cont=0;
			var tem="";
			
			for(var i=importeParte1.length; i>0; i--){
				if(cont == 3){
					tem = ","+tem;
					cont=0;
				}
				tem = importeParte1.substring(i-1,i)+tem;
				cont++;
			}
			if(importe.toString().indexOf("\.")>0){
				for(var i=importeSeparado[1].length; i<2; i++){
					importeSeparado[1]+="0";
				}
				return tem+"."+importeSeparado[1];
			}
			else{
				return tem+".00";
			}
		}
		
		function validaCondicionesIniciales(){
			var dateChangeAllowed=true;
			var roles="<%=roles%>";
			if(parseInt($("#validacionMontoTipoAdj").val(),10)== 0 && $("#lJustificaTipoProced").val()==0){
				$("#legenTipoProcedimiento").show();
			}
			if(roles.toString().indexOf("ADMIN_RECMAT") < 0 && roles.toString().indexOf("JEFES") <0 &&roles.indexOf("ANALISTA")<0 && parseInt($("#validacionMontoTipoAdj").val(),10)== 0){
				$("#trJustificacionTipoProced").hide();
			}
			if($("#lJustificaTipoProced").val()==1 && parseInt($("#nIdEstado").val(),10)<4){
				document.getElementById("checkJustificacion").checked=true;
				enableDisabledDescrip();
			}
			//Condiciones para deshabilitar y ocultar campos
			if($("#nIdTipoCambio").val()=="01"){
				 $("#trTipoCambio").hide();
			}
			if($("#nIdEstado").val()!="1"){
		       	if($("#nIdEstado").val()!="6"){
		    		if($("#nIdEstado").val()=="2" || $("#nIdEstado").val()=="5" ){//en sif sin presupuesto
						//si es dos deshabilita el textbox de pedido definitivo y tipo cambio
						document.getElementById("cContratoDefinitivoV").disabled = true;			  	
		 	    		document.getElementById("mTipoCambioV").disabled = true;
						habilitaTabs();															
					}else {	 
						if($("#nIdEstado").val()=="3"){// en sif presupuestado						 	
		 	    			document.getElementById("mTipoCambioV").disabled = true;
		 	    			document.getElementById("cConceptoV").disabled=true;
							document.getElementById("cContratoDefinitivoV").disabled=true;
		 	    			habilitaTabs();
				        }
						else{ //es 4 aprobado
							document.getElementById("cConceptoV").disabled=true;
							document.getElementById("cContratoDefinitivoV").disabled=true;
							document.getElementById("mTipoCambioV").disabled = true;
							
							document.getElementById("cboCategoriaCaratula").disabled = true;
							//Deshabilitarcampos
							$("#cnumCompranet").prop("readonly",true);
							$("#nCodContratoCNET").prop("readonly",true);
							$("#nCodExpedienteCNET").prop("readonly",true);
							$("#oficioDG").prop("readonly",true);
							$("#folioMASCP").prop("readonly",true);	
							$("#actualizaDocContrato").val(1);	
														
							document.getElementById("btnGuardarCaratulaCont").disabled = true;
							document.getElementById("checkJustificacion").disabled = true;
							document.getElementById("cConceptoV").disabled = true;
							document.getElementById("mTipoCambioV").disabled = true;
							document.getElementById("cContratoDefinitivoV").disabled = true;
							document.getElementById("fechaFalloV").disabled = true;
							document.getElementById("fechaSolicitudV").disabled = true;
							document.getElementById("fechaPropuestasV").disabled = true;
							document.getElementById("fechaFormalizacionV").disabled = true;
							document.getElementById("fechaInicioV").disabled = true;
							document.getElementById("fechaFinV").disabled = true;
							document.getElementById("chkImpuestosAdicionales").disabled = true;
							document.getElementById("chkOtrosImpuestos").disabled = true;

							
							habilitaTabs();																			
			        	}
			   		}
		    	}
		    	if($("#nIdEstado").val()=="6"){
		    		document.getElementById("btnGuardarCaratulaCont").disabled = true;
		    	}
		    	document.getElementById("centralizado").disabled = true;
				document.getElementById("descentralizado").disabled = true;
			}else{
				$("input.AyudaSyC").subIniciaDlg();
		    	$("input.autoCompletaSyC").subIniciaAutoCompleta();
				queryFormPost("revisaCopiaContrato", {async: false});
				if(parseInt($("#estadoPartidaProcedimiento").val(),10)== 3 )
				document.getElementById("cContratoDefinitivoV").disabled=true;
			}
			  
		         
			document.getElementById("trffal").disabled = true;				
			document.getElementById("trfsol").disabled = true;
			document.getElementById("trfpro").disabled = true;
			document.getElementById("trffor").disabled = true;
			document.getElementById("trfini").disabled = true;
			document.getElementById("trffin").disabled = true;
			document.getElementById("fechaFalloV").disabled=true;
			document.getElementById("fechaSolicitudV").disabled=true;
			document.getElementById("fechaPropuestasV").disabled=true;
			document.getElementById("fechaFormalizacionV").disabled=true;
			document.getElementById("fechaInicioV").disabled=true;
			document.getElementById("fechaFinV").disabled=true;
			
			
			// convertimos a mayasculas usuario de creacion y u_login
			var login=$("#U_LOGIN").val();
			var u_creacion=$("#cIdUsuarioCreacion").val();
			login=login.toUpperCase();
			u_creacion=u_creacion.toUpperCase();
			$("#U_LOGIN").val(login);
			$("#cIdUsuarioCreacion").val(u_creacion);
			
		   	if($("#U_LOGIN").val()!=$("#cIdUsuarioCreacion").val()){
				if(roles.toString().indexOf("ADMIN_RECMAT") < 0 && roles.toString().indexOf("JEFES") <0 &&roles.indexOf("ANALISTA")<0 ){
			  		document.getElementById("cContratoDefinitivoV").disabled = true;	
			  		document.getElementById("mTipoCambioV").disabled = true;
					document.getElementById("btnGuardarCaratulaCont").disabled = true;
					dateChangeAllowed=false;
				}
			}
			if(dateChangeAllowed){
				if($("#nIdEstado").val() == "1" || $("#nIdEstado").val() == "2" || $("#nIdEstado").val() == "3" || $("#nIdEstado").val() == "5" ){
					document.getElementById("trffor").disabled = false;
					document.getElementById("trfini").disabled = false;
					document.getElementById("trffin").disabled = false;
					document.getElementById("fechaFormalizacionV").disabled=false;
					document.getElementById("fechaInicioV").disabled=false;
					document.getElementById("fechaFinV").disabled=false;
					agregaDatePickerFechas();
				}
				else if($("#nIdEstado").val() == "4"){
					document.getElementById("trfini").disabled = false;
					document.getElementById("trffin").disabled = false;
					document.getElementById("fechaInicioV").disabled=false;
					document.getElementById("fechaFinV").disabled=false;
					agregaDatePickerFechas();
				}
				else{
					document.getElementById("trffor").disabled=true;
					document.getElementById("trfini").disabled=true;
					document.getElementById("trffin").disabled=true;
					document.getElementById("fechaFormalizacionV").disabled=true;
					document.getElementById("fechaInicioV").disabled=true;
					document.getElementById("fechaFinV").disabled=true;
					document.getElementById("btnGuardarCaratulaCont").disabled=true;			
				}
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
		
		function foco(elemento) {
 			elemento.style.border = "1px solid #FF0000";
 		}
		function no_foco(elemento) {
			 elemento.style.border = "1px solid #CCCCCC";
		}
		function validaCampos(){
			var validado=true;
			var patternNumero=/^\d+([.]\d\d?)?$/
			var tipoCambioVar;
			$("#lblMsj").css('color','red');	
			//Valida Tipo de Cambio
			tipoCambioVar=  $("#mTipoCambioV").val();			
			if(!patternNumero.test(tipoCambioVar)){
				$("#lblMsj").val('El tipo de cambio debe ser un dato numérico');
				foco(document.getElementById('mTipoCambioV'));
				document.getElementById("trErrorFechas").style.visibility='visible';
				return false;
			}	
			if(tipoCambioVar <0){
				$("#lblMsj").val('El tipo de cambio debe ser mayor a 0');
				foco(document.getElementById('mTipoCambioV'));
				document.getElementById("trErrorFechas").style.visibility='visible';
				return false;
			}
			if(document.getElementById("checkJustificacion").checked && $("#descripJustTipoProced").val()==""){
				swal("Falta escribir la justificaci\u00f3n del procedimiento.",{icon:"info",button: "Cerrar"});
				return false;
			}
			var arr_fechaEnt = ($("#fechaEntregaV").val()).split("/");
			var dt_fechaEnt = new Date(arr_fechaEnt[2], arr_fechaEnt[1]-1, arr_fechaEnt[0]);
			
			var arr_fechaIni = ($("#fechaInicioV").val()).split("/");
			var dt_fechaIni = new Date(arr_fechaIni[2], arr_fechaIni[1]-1, arr_fechaIni[0]);
			
			var arr_fechaFin = ($("#fechaFinV").val()).split("/");
			var dt_fechaFin = new Date(arr_fechaFin[2], arr_fechaFin[1]-1, arr_fechaFin[0]);
			
			if(dt_fechaIni>=dt_fechaFin && $("#cIdTipoProcedimiento").val()!="PC" && $("#cIdTipoProcedimiento").val()!="PT" && $("#cIdTipoProcedimiento").val()!="PR"){
				swal("La fecha de inicio no puede ser mayor ni igual a la de fin",{icon:"info",button: "Cerrar"});
				return false;
			}
			var arr_fechaFor = ($("#fechaFormalizacionV").val()).split("/");
			var dt_fechaFor = new Date(arr_fechaFor[2], arr_fechaFor[1]-1, arr_fechaFor[0]);
				
			return true;
		}
		
		function guardaCaratula(){
			no_foco(document.getElementById('fechaInicioV'));
			no_foco(document.getElementById('fechaFinV'));
			no_foco(document.getElementById('fechaFormalizacionV'));
			no_foco(document.getElementById('mTipoCambioV'));
			no_foco(document.getElementById('fechaFalloV'));
			no_foco(document.getElementById('fechaSolicitudV'));
			no_foco(document.getElementById('fechaPropuestasV'));
			document.getElementById("trErrorFechas").style.visibility='hidden';
			manejaRadioBtn();
			if(roles.toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1){
				$("#esperar").dialog("open");
				$("#tipoOperacion").val(3);
				if(validaCapturaGarantia()){
					swal("Si el contrato no cuenta con garantías, favor de excentar el contrato.",{icon:"warning",button: "Cerrar"});
					return;
				}
				if( validaCampos()) {
					var object=llenaObjectCaratula();
					$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
						,data:object
						,dataType: 'json', success: 
							function(j){
								swal({
									title: "",
									text: j[0].MENSAJE,
									icon: "info",
									buttons: {
										confirm : "Cerrar"
									},
								}).then((continuar) => {
									$("#esperar").dialog("close");
									location.reload();
								});
								
						}, error: function( jqXHR, textStatus, errorThrown ) {
							$("#esperar").dialog("close");
						}
					});
				}
			}else{
				swal("No tiene Permisos de realizar está acción",{icon:"info",button: "Cerrar"});
			}
		}
		//Funcion para actualizar el estado del Contrato en la base de datos		
		function guardar() {
			
			var roles="<%=roles%>";	
			no_foco(document.getElementById('fechaInicioV'));
			no_foco(document.getElementById('fechaFinV'));
			no_foco(document.getElementById('fechaFormalizacionV'));
			no_foco(document.getElementById('mTipoCambioV'));
			no_foco(document.getElementById('fechaFalloV'));
			no_foco(document.getElementById('fechaSolicitudV'));
			no_foco(document.getElementById('fechaPropuestasV'));
			document.getElementById("trErrorFechas").style.visibility='hidden';
			manejaRadioBtn();

			
			//Actualiza el tipo de categoria si eres ADMIN_RECMAT
			if(roles.toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1){
				$("#nIdCategoria").val($("#cboCategoriaCaratula").val());
				queryFormPost("actualizaCaratulaCategoriaContrato", {async: false});
			}
			
			if( validaCampos()) {
				//utiliza variables auxiliares para ocupar el mismo CRUD independientemente de lo que se haya cambiado
				//o que se muestre en la pantalla (ej. Tipo de cambio)
				$("#mTipoCambio").val($("#mTipoCambioV").val());
				$("#cConcepto").val($("#cConceptoV").val());
				$("#cContratoDefinitivo").val($("#cContratoDefinitivoV").val());
				$("#fechaFallo").val($("#fechaFalloV").val());
				$("#fechaFormalizacion").val($("#fechaFormalizacionV").val());
				$("#fechaInicio").val($("#fechaInicioV").val());
				$("#fechaFin").val($("#fechaFinV").val());
				$("#fechaEntrega").val($("#fechaEntregaV").val());
				
				if ($("#cTipoContrato").val() == 'CV') {
					$("#fechaPropuestas").val($("#fechaPropuestasV").val());
					$("#fechaSolicitud").val($("#fechaSolicitudV").val());
				}
				queryFormPost("existePrecom",{async:false});
				if($("#existePrecom").val() == "" || $("#existePrecom").val() == "0"){
					//Actualiza Centralizado
					queryFormPost("descentralizadoContratoUpdate",{async:false});
					//Guarda impuestos adicionales
					queryFormPost("impuestosAdicionalesContratoDelete",{async: false});
					if(document.getElementById("chkImpuestosAdicionales").checked){
						validaDatosImpuestosAdicionales("cDescripcionImpuestoAdicional1","cPorcentajeImpuestoAdicional1","1");
						validaDatosImpuestosAdicionales("cDescripcionImpuestoAdicional2","cPorcentajeImpuestoAdicional2","2");
						validaDatosImpuestosAdicionales("cDescripcionImpuestoAdicional3","cPorcentajeImpuestoAdicional3","3");
					}
						
					$("#cMontoOtroImpuesto1").val($("#cMontoOtroImpuesto1").val().replace(/,/g, ''));
					$("#cMontoOtroImpuesto1").val($("#cMontoOtroImpuesto1").val().replace("$", ""));
					
					$("#cMontoOtroImpuesto2").val($("#cMontoOtroImpuesto2").val().replace(/,/g, ''));
					$("#cMontoOtroImpuesto2").val($("#cMontoOtroImpuesto2").val().replace("$", ""));
					
					$("#cMontoOtroImpuesto3").val($("#cMontoOtroImpuesto3").val().replace(/,/g, ''));
					$("#cMontoOtroImpuesto3").val($("#cMontoOtroImpuesto3").val().replace("$", ""));
				
					//Guarda otros impuestos
					queryFormPost("OtrosimpuestosContratoDelete",{async: false});
					if(document.getElementById("chkOtrosImpuestos").checked){
						validaDatosOtrosImpuestos("cDescripcionOtroImpuesto1","cMontoOtroImpuesto1","1");
						validaDatosOtrosImpuestos("cDescripcionOtroImpuesto2","cMontoOtroImpuesto2","2");
						validaDatosOtrosImpuestos("cDescripcionOtroImpuesto3","cMontoOtroImpuesto3","3");
					}
				}
				if ($("#nIdEstado").val() == "1" || $("#nIdEstado").val() == "2" ||$("#nIdEstado").val() == "3" ) {
				
				  //revisamos que el contrato no tenga un pasivo a plurianualiada asigando
				     queryFormPost("revisaContratosPasivosPlurianualesContrato", {async: false});
				    if(parseInt($("#tienePasivoPlurianual").val(),10) > 0 ){
						swal("El número de Contrato que desea actualizar ya tiene un Contrato Pasivo  o un Contrato Plurianual Asignado.",{icon:"warning",button: "Cerrar"});
						location.reload();
						return;
					}
				
				   //checamos que el pedido definitivo que se pretende actualizar no tenga un contrato diverso 
					queryFormPost("revisaContratosComprometidos", {async: false});
					queryFormPost("revisaContratosConContratoDiverso", {async: false});
					
										
					if((parseInt($("#numeroContratos").val(),10) || parseInt($("#numDiversos").val(),10)) > 0  ){
						swal("El número de Contrato que desea actualizar ya tiene un Contrato Diverso  o un Contrato de Materiales Asignado.",{icon:"warning",button: "Cerrar"});
						location.reload();
						return;
					}
				
					//primero checamos si el contrato ya esta siendo trabajado por el usuario de ventanilla
					queryFormPost("usuarioVentanillaReadContrato", {async: false});
					if(($("#responsable").val()!="VENTANILLA_PRECOMPROMISO" && $("#responsable").val()!="") && ($("#nIdEstado").val() == "3"))
					{
						swal("No se pueden actualizar los datos, el contrato lo esta revisando el usuario de ventanilla "+$("#responsable").val(),{icon:"warning",button: "Cerrar"});
						location.reload();
						return;
					}
					else{
						if($("#nCodContratoCNET").val()==0)	{
						$("#nCodContratoCNET").val('');
						}
						if($("#nCodExpedienteCNET").val()==0)	{
							$("#nCodExpedienteCNET").val('');
						}
						
						document.getElementById("descripJustTipoProced").disabled = false;
						queryFormPost("actualizaCaratulaContratoUpdate", {async: false});
						queryFormPost("mUpdateProcedimientoFundamentoLeg,mUpdateProcedimientoAdjFundamentoLeg", {async: false});
						if(!document.getElementById("checkJustificacion").checked){
							document.getElementById("descripJustTipoProced").disabled = true;
						}
						////eliminar en tabla de fechas de procedimiento
						queryFormPost("mDeleteFechasContrato", {async: false});
						//insertar en tabla fechas procedimiento
						queryFormPost("mInsertFechaFormalizacionContrato", {async: false});
						
						if($("#cIdTipoProcedimiento").val()=="PC" || $("#cIdTipoProcedimiento").val()=="PT"){
							queryFormPost("mInsertFechaEntregaPedido", {async: false});
						}else{
							queryFormPost("mInsertFechaInicioContrato", {async: false});
							queryFormPost("mInsertFechaFinContrato", {async: false});
						}
						queryFormPost("actualizaContratoFinancieroUpdateContratoDiverso", {async: false});
						if(guardaDatosPSP()){
							swal({
								title: "",
								text: "Los cambios se han realizado exitosamente.",
								icon: "info",
								buttons: {
									confirm : "Cerrar"
									},
							}).then((continuar) => {
								document.getElementById("trErrorFechas").style.visibility="visible";
								guardaBitacora("GUARDA_CARATULA", $("#cContratoDefinitivo").val());
				                habilitaTabs();
								location.reload();
							});
						}
					}
				}
				else if ($("#nIdEstado").val() == "4" && $("#actualizaDocContrato").val()==0) {
					if($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val() || roles.toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1){
						swal({
							title: "¿Desea continuar?",
							text: "Esta modificación afectará al sistema financiero!",
							icon: "info",
							buttons: {
								confirm : "Aceptar",
								cancel: "Cancelar"
								},
						}).then((continuar) => {
							if (!continuar) {
								return;
							}else{
								//agrega contrato diverso convenio con fecha nueva
								queryFormPost("mInsertFechaContratoDiversoConvenio", {async: false});
								swal("Los cambios se han realizado exitosamente",{icon:"info",button: "Cerrar"});
								document.getElementById("trErrorFechas").style.visibility='visible';
								location.reload();
							}
						});
					}
				}
			}
			
			//ExisteHiperv
			queryFormPost("ExisteHiperv", {async: false});
			if($("#existeRegistro").val()>0){
				queryFormPost("mUpdateGarantias", {async: false});
			}else{
				queryFormPost("insertamDocumentacionContrato", {async: false});
			}
			
			
			
		}
		function validaDatosImpuestosAdicionales(descripcion, porcentaje, impuesto){
			if($("#"+descripcion).val() != ""){
				if($("#"+porcentaje).val() != ""){
					if(parseFloat($("#"+porcentaje).val()) > 0){
						$("#cDescripcionImpuestoAdicional").val($("#"+descripcion).val());
						$("#cPorcentajeImpuestoAdicional").val(parseFloat($("#"+porcentaje).val()));
						$("#cImpuesto").val(impuesto);
						queryFormPost("insertImpuestoAdicionalContrato",{async: false});
					}
					else{
						swal("El valor del porcentaje del "+descripcion.replace("cDescripcion","").replace("Adicional"," ")+" debe ser mayor a 0",{icon:"info",button: "Cerrar"});
					}
				}
				else{
					swal("Debe insertar en valor del procentaje en el "+descripcion.replace("cDescripcion","").replace("Adicional"," "),{icon:"info",button: "Cerrar"});
				}
			}
			else{
				if($("#"+porcentaje).val() != ""){
					swal("Debe insertar la descripci\xF3n del "+descripcion.replace("cDescripcion","").replace("Adicional"," "),{icon:"info",button: "Cerrar"});
				}
			}
		}
		
		
		function validaDatosOtrosImpuestos(descripcion, monto, impuesto){
			if($("#"+descripcion).val() != ""){
				if($("#"+monto).val() != ""){
					if(parseFloat($("#"+monto).val()) > 0){
						$("#cDescripcionOtroImpuesto").val($("#"+descripcion).val());
						$("#cMontoOtroImpuesto").val(parseFloat($("#"+monto).val()));
						$("#cImpuestoOtros").val(impuesto);
						queryFormPost("insertOtrosImpuestosContrato",{async: false});
					}
					else{
						swal("El valor del monto del "+descripcion.replace("cDescripcionOtro","")+" debe ser mayor a 0",{icon:"info",button: "Cerrar"});
					}
				}
				else{
					swal("Debe insertar en valor del Monto en el "+descripcion.replace("cDescripcionOtro",""),{icon:"info",button: "Cerrar"});
				}
			}
			else{
				if($("#"+monto).val() != ""){
					swal("Debe insertar la descripci\xF3n del "+descripcion.replace("cDescripcionOtro",""),{icon:"info",button: "Cerrar"});
				}
			}
		}
		function Sinespacios(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			if (keyPressed > 91 && keyPressed != 209 && keyPressed != 95){
				swal("Solo se Permiten Mayusculas y Numeros",{icon:"info",button: "Cerrar"});
			 }
			 if (keyPressed == 61 || keyPressed == 63 ||
			 	 keyPressed == 46 || keyPressed == 59 ||
			 	 keyPressed == 58 || keyPressed == 60 ||
			 	 keyPressed == 62)
			{
			return false;
			 }
			return !(keyPressed > 31 && (keyPressed < 45 || keyPressed > 90) && keyPressed != 209 && keyPressed != 95);
		}


		function deshabilitaTabs(){
				$( "#aTab2" ).attr("disabled", true);
				$( "#aTab3" ).attr("disabled", true);
				$( "#aTab4" ).attr("disabled", true);
				$( "#aTab5" ).attr("disabled", true);
				$( "#aTab6" ).attr("disabled", true);
				$( "#aTab7" ).attr("disabled", true);
		}
		
		function habilitaTabs(){
				$( "#aTab2" ).attr("disabled", false);
				$( "#aTab3" ).attr("disabled", false);
				$( "#aTab4" ).attr("disabled", false);
				$( "#aTab5" ).attr("disabled", false);
				$( "#aTab6" ).attr("disabled", false);
		}
		function muestraImpuestosAdicionales(){
			if(document.getElementById("chkImpuestosAdicionales").checked){
				document.getElementById("divImpuestosAdicionales").style.display="block";
			}else{
				$("#cDescripcionImpuestoAdicional1").val("");
				$("#cDescripcionImpuestoAdicional2").val("");
				$("#cDescripcionImpuestoAdicional3").val("");
				$("#cPorcentajeImpuestoAdicional1").val("");
				$("#cPorcentajeImpuestoAdicional2").val("");
				$("#cPorcentajeImpuestoAdicional3").val("");
				document.getElementById("divImpuestosAdicionales").style.display="none";
			}
		}

		function copiarContrato(){
			if(parseInt($("#nIdEstado").val(),10) == 4 && ($("#R_NOMBRE").val()=="ADMIN_RECMAT" ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1 
				|| $("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
				if (!window.confirm("Esta seguro de  Realizar una copia del Contrato,tiene que estar seguro que el Contrato sufrio una Reduccion Presupuestal del Compromiso?"))
					return;
				//revisamos si existe una reduccion
				queryFormPost("checaReduccionContrato",{async : false});
				if(parseFloat($("#mImporteCompromiso").val())!= 0.0000){
					swal("No se puede Realizar la copia no han hecho la Reducción Presupuestal del Compromiso",{icon:"info",button: "Cerrar"});
					return;
				}
				//Obtener el siguiente consecutivo del procedimiento y asignarlo al hidden ConsecutivoProcedimiento
				queryFormPost("fn_ConsecutivoProcedimientoContratoCopia",{async : false});
		
				var res="";
				var link="";
				//aplicacion contable
				$.ajax({url: '../../servlet/ContratoServlet?cIdContratoDefinitivo='+$("#cContratoDefinitivo").val()+"&ConsecutivoProcedimientoCopia="+$("#ConsecutivoProcedimientoCopia").val(), type:'post' , async: false,data:'operacion=10', dataType: 'json', 
					success:function(j){
						res=j[0].Contable1;
						if(res!="-1"){
							swal("Se Realizo con Exito la Copia de Contrato",{icon:"info",button: "Cerrar"});
							//se constuye link donde va a ir la copia
							//Bitácora
							$("#cAccion").val("REALIZA_COPIA_CONTRATO");
							$("#cIdDocumento").val($("#cContratoDefinitivo").val());
							queryFormPost("sp_mBitacoraMovimientosCreate",{async : false});
							
							queryFormPost("fn_ConstruyeLinkContratoCopia",{async : false});
							link='Contratos.jsp?tab=1&cEjercicio='+$("#cEjercicio").val()+'&cIdTipoContrato='+$("#cIdTipoContrato").val()+ '&cIdUnidadEjecutora='+$("#cIdUnidadEjecutora").val()+'&nIdConsecutivo=' +$("#nIdConsecutivoContratoClon").val()+'&lPedidoAbierto='+$("#lContratoAbiertoClon").val()+'&nIdEstadoContrato='+$("#nIdEstadoContratoClon").val()
							window.location=link;
						}else{
							swal("No se realizo con exito la copia del Contrato intentelo nuevamente",{icon:"info",button: "Cerrar"});
							return;
						}
					}	
				});
			}else{
				swal("Para poder realizar una Copia, el Contrato debe estar aprobado,la Copia solo la podra realizar un Administrador o el Usuario Creador del Contrato, ademas tiene que haber sufrido una Reduccion Presupuestal del Compromiso. ",{icon:"info",button: "Cerrar"});
			}
		}

		function borrar(){
			var proce = $("#lblProcedimiento").val().replace('Procedimiento: [[ ','').replace(' ]]','');
			var tipoProce = proce.split('-')[0];
			if ((tipoProce == 'PI' || tipoProce == 'PF') && $.trim($("#lblEstado").val()) == 'CAPTURADO') {
				swal({
					title: "¿Esta seguro de Anular el Contrato?",
					text: "Se eliminara y no será posible regresar los cambios!",
					icon: "info",
					buttons: {
						confirm : "Aceptar",
						cancel: "Cancelar"
						},
					}).then((continuar) => {
						if (!continuar) {
								return;
						}else{
							queryFormPost("mContratoDelete",{async: false});
							window.location = 'Contratos.jsp?tab=0';
						}
					});
			}
			else {
				swal("No es posible anular el contrato",{icon:"info",button: "Cerrar"});
			}
		}

		function manejaRadioBtn(){
			if($("#centralizado").is(':checked')){
				//hidden 'esDescentralizado' = 0
				$("#esDescentralizado").val(0);
			}
			else{
				//hidden 'esDescentralizado' = 1
				$("#esDescentralizado").val(1);
			}
		}

		// muestra en pantalla si el registro de la bd es centralizado o descentralizado
		function iniciaRadioBtn(){
			queryFormPost("verSiContratoDescentralizado",{async: false});
			if($("#esDescentralizado").val()==1){
				$("#descentralizado").attr("checked",true);
			}
			else{
				$("#centralizado").attr("checked",true);
			}
		}
		
		function validaEdicionDatos(){
			var roles="<%=roles%>";
			queryFormPost("mValidaPagosPedidoRead",{async: false});
			queryFormPost("mTotalPagosContratoRead",{async: false});
			
			if($("#lblEstado").val().toString() == "APROBADO"){
				if(parseInt($("#cValidaPagosPedido").val(),10) == 1){
					if(roles.indexOf("ADMIN_RECMAT") >=0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1){
						if(parseInt($("#cTotalPagosContrato").val(),10) == 0)
							return true;
						else
							return false;
					}
					else
						return false;
				}
				else
					return false;
			}
			return;
		}

		function agregaDatePickerFechas(){
			if($("#nIdEstado").val() != "4"){
		
				$("#fechaFormalizacionV").datepicker({
					//beforeShowDay: nonWorkingDates,						
					dateFormat: "dd/mm/yy",
					altField: "#actualDate",
					currentText: "Now",
					showOn: 'button',
					buttonImageOnly: true,	
				    buttonImage: '../images/calendar.gif',			    					 
					changeYear: true
				});
							  
				$("#fechaInicioV").datepicker({	
					//beforeShowDay: nonWorkingDates,						
					dateFormat: "dd/mm/yy",
					currentText: "Now",
					showOn: 'button',
					altField: "#actualDate",
					buttonImageOnly: true,	
					buttonImage: '../images/calendar.gif',			    					 
					changeYear: true
				});			 
	
				$("#fechaFinV").datepicker({
					//beforeShowDay: nonWorkingDates,						
					dateFormat: "dd/mm/yy",
					currentText: "Now",
					showOn: 'button',
					altField: "#actualDate",
					buttonImageOnly: true,	
					buttonImage: '../images/calendar.gif',			    					 
					changeYear: true
				});
				$("#fechaEntregaV").datepicker({
					//beforeShowDay: nonWorkingDates,						
					dateFormat: "dd/mm/yy",
					currentText: "Now",
					showOn: 'button',
					altField: "#actualDate",
					buttonImageOnly: true,	
					buttonImage: '../images/calendar.gif',			    					 
					changeYear: true
				});
			}
		}
		
		function onlyNumbers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			var strCheck = '0123456789.';
		
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
		
			return true 
			//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
			
			
		}
		function onlyNumbers2(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			var strCheck = '0123456789';
		
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
		
			return true 
			//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
			
			
		}
		///Desabilita sabados y domingos del datepicker
			 function nonWorkingDates(date){
		        var day = date.getDay(), Sunday = 0, Monday = 1, Tuesday = 2, Wednesday = 3, Thursday = 4, Friday = 5, Saturday = 6;
		        //var closedDates = [[7, 29, 2009], [8, 25, 2010]];
		        var closedDays = [[Sunday], [Saturday]];
		        for (var i = 0; i < closedDays.length; i++) {
		            if (day == closedDays[i][0]) {
		                return [false];
		            }
		
		        }
		
		        return [true];
		    }
	
	
	function cambiafrmt(fld){
	
	   		$("#"+fld).formatCurrency();
		}	
	
		
	function muestraOtrosImpuestos(){
			if(document.getElementById("chkOtrosImpuestos").checked){
				document.getElementById("divOtrosImpuestos").style.display="block";
				if($("#nIdEstado").val()!=1){
					document.getElementById("MESSAGESPAN").style.display="block";
					$("#cDescripcionOtroImpuesto1").prop("readonly",true);
					$("#cDescripcionOtroImpuesto2").prop("readonly",true);					
					$("#cDescripcionOtroImpuesto3").prop("readonly",true);					
					$("#cMontoOtroImpuesto1").prop("readonly",true);					
					$("#cMontoOtroImpuesto2").prop("readonly",true);					
					$("#cMontoOtroImpuesto3").prop("readonly",true);
				}
			}else{
				$("#cDescripcionOtroImpuesto1").val("");
				$("#cDescripcionOtroImpuesto2").val("");
				$("#cDescripcionOtroImpuesto3").val("");
				$("#cMontoOtroImpuesto1").val("");
				$("#cMontoOtroImpuesto2").val("");
				$("#cMontoOtroImpuesto3").val("");
				document.getElementById("divOtrosImpuestos").style.display="none";
			}
		}	
		function guardaBitacora(accion,documento){
			//Bitácora
			$("#cAccion").val(accion);	
			$("#cIdDocumento").val(documento);
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
		}
		function formato(){
			$("#mTotalGarantias").formatCurrency();
			$("#mGarantiaCumplimiento").formatCurrency();
			$("#mGarantiaAnticipo").formatCurrency();
		}
		function sumaTotalGarantias(){
			var suma=0;
			suma=parseFloat(quitaFmt($("#mGarantiaCumplimiento").val()),10)+parseFloat(quitaFmt($("#mGarantiaAnticipo").val()),10);
			$("#mTotalGarantias").val(suma);
			formato();
		}
		function enableDisabledDescrip(){
			if(document.getElementById("checkJustificacion").checked){
				document.getElementById("descripJustTipoProced").disabled = false;
				$("#legenTipoProcedimiento").hide();
				$("#lJustificaTipoProced").val(1);
			}else{
				$("#descripJustTipoProced").val("");
				$("#lJustificaTipoProced").val(0);
				$("#legenTipoProcedimiento").show();
				document.getElementById("descripJustTipoProced").disabled = true;
			}
			
		}
		function guardaContratoArt25(){
			swal({
				title: "Autorizaci\u00f3n de contrataci\u00f3n Art. 25 LAASSP",
				text: "¿Est\u00e1 seguro de autorizar la contrataci\u00f3n?\n No se podr\u00e1 revertir est\u00e1 acci\u00f3n.",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
					},
			}).then((continuar) => {
				if (!continuar) {
					return;
				}else{
					if(validaCapturaGarantia()){
						swal("Si el contrato no cuenta con garantias, favor de excentar el contrato.",{icon:"warning",button: "Cerrar"});
						return;
					}
					if(roles.toString().indexOf("ADMIN_RECMAT") >= 0 ||roles.indexOf("JEFE")>=0 ){
						$("#esperar").dialog("open");
						$("#tipoOperacion").val(5);
						if( validaCampos()) {
							var object=llenaObjectCaratula();
							$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
								,data:object
								,dataType: 'json', success: 
									function(j){
										swal({
											title: "",
											text: j[0].MENSAJE,
											icon: "info",
											buttons: {
												confirm : "Cerrar"
											},
										}).then((continuar) => {
											$("#esperar").dialog("close");
											location.reload();
										});
										
								}, error: function( jqXHR, textStatus, errorThrown ) {
									$("#esperar").dialog("close");
								}
							});
						}
					}else{
						swal("No tiene permisos de realizar está acción",{icon:"info",button: "Cerrar"});
					}
				}
			});
			
				
		}
		
	</script>
 </head>  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form> 
		<div id="container" class="container" style="width: 95%">
		<fieldset>
			<legend>Car&aacute;tula del Contrato</legend>
				<table align="left" cellpadding="2" width="100%">
			    	<tr>
			    		<td align="right" colspan="2">
							<img id="imgBorrar" src="../imagenes/cancel_round.png" style="cursor: pointer;display: none;"  onclick="copiarContrato();"/>&nbsp;<span id="spamCopiaContrato" style="display: none;">Realizar Copia</span> 
			    			<img id="imgBorrar" src="../imagenes/cancel_round.png" style="cursor: pointer;display: none;"  onclick="borrar();"/>&nbsp;<span id="spamBorrarContrato" style="display: none;">Anular</span> 
			    			<input type="button" class="btnInterfaceAutoriza ui-button ui-widget ui-state-default ui-corner-all" 	id="imgAprobArt25" 	name="imgAprobArt25" 	value="Aprobar" onclick="guardaContratoArt25();"	style="display: none;"/>
							<input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 	id="imgSalir" 	name="imgSalir" 	value="Salir"	  onclick="window.location = 'Contratos.jsp?tab=0';" />
			    		</td>
			    	</tr>
			    	<tr>
						<td align="left" colspan="2"><input type="text" style="width: 200px;border-width:0; background-color:transparent; color:red" id="lblAutorizaSICOP" name="lblAutorizaSICOP" readonly />
						</td>
					</tr>
					<tr align="left" id="textAutorizaContratoArt25" style="display: none;">
						<td>
							<p style="color: red; ">Solicita a t&uacute; jefe la autorizaci&oacute;n
								de la contrataci&oacute;n por Art. 25 de la LAASSP.</p></td>
					</tr>
					<tr id="legenTipoProcedimiento" style="display: none;">
						<td align="left" colspan="2"><span style="color:red;">Esté contrato no se podra aprobar. El tipo de procedimiento seleccionado no corresponde con el rango de montos m&iacute;nimos y m&aacute;ximos. Tendras que devolver el procedimiento a captura y modificarlo en la car&aacute;tula.</span></td>
					</tr>
					<tr id="trDuplicidad" align="left">
						<td align="left" colspan="2">
							<textarea id="cadenaDuplicidad" name="cadenaDuplicidad" rows="5" cols="100" style="border: 0px solid black;color: red" readonly="readonly"></textarea>
						</td>
					</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" id="lblUnidadEjecutora" name="lblUnidadEjecutora"  readonly /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblProcedimiento" id="lblProcedimiento" readonly /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblDefinitivo" id="lblDefinitivo" readonly /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 700px;border-width:0; background-color:transparent" name="lblContrato" id="lblContrato"  readonly /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblProveedor" id="lblProveedor"   readonly /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 500px;border-width:0; background-color:transparent" name="lblEstado" id="lblEstado" readonly /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 500px;border-width:0; background-color:transparent" name="lblEstadoSICOP" id="lblEstadoSICOP" readonly /></td>
			    	</tr>
			    	<tr>
						<td colspan="2">Monto M&iacute;nimo y M&aacute;ximo por Tipo de Procedimiento<br/>
							<div id="tblMinimoMaximo" style="width: 100%"></div>
						</td>
					</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblSubtotal" id="lblSubtotal" readonly /></td>
			    	</tr>
			    	<tr id="divImporteIVA">
			    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteIVA" id="lblImporteIVA" readonly /></td>
			    	</tr>
			    	<tr id="divImporteImpuesto1">
			    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteImpuesto1" id="lblImporteImpuesto1" readonly /></td>
			    	</tr>
			    	<tr id="divImporteImpuesto2">
			    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteImpuesto2" id="lblImporteImpuesto2" readonly /></td>
			    	</tr>
			    	<tr id="divImporteImpuesto3">
			    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteImpuesto3" id="lblImporteImpuesto3" readonly /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblTotal" id="lblTotal" readonly /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent; color: blue;" name="lblTotalMax" id="lblTotalMax" readonly /></td>
			    	</tr>							    	
			    </table>
		</fieldset>
					
	    <fieldset>
	    	<legend>Edici&oacute;n del Contrato</legend>
	    		<table style="width: 100%" >
	    			<tr>
	    				<td style="width: 25%">
	    					<input name="Categoria" id="Categoria"  value="Tipo de Procedimiento:"size="20" style="border-width:0; background-color:transparent ">
	    				</td>
	    				<td style="width: 75%">
	    					<select id="cboCategoriaCaratula" name="cboCategoriaCaratula" style="width: 30em;">
							</select>
	    				</td>
	    			</tr>
	    			<tr>
	    				<td style="width: 25%">
	    					Fundamento Legal:
	    				</td>
	    				<td style="width: 75%">
	    					<select id="cboFundamentoLeg" name="cboFundamentoLeg" style="width: 30em;">
							</select>
	    				</td>
	    			</tr>
	    			<tr>
	    				<td style="width: 25%">
	    					Concepto:
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" id="cConceptoV" name="cConceptoV" style="width: 400px"/>
	    				</td>
	    			</tr>
	    			<tr id="trTipoCambio">
	    				<td style="width: 25%">
	    					Tipo de Cambio:
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" id="mTipoCambioV" name="mTipoCambioV" style="width: 400px"/>
	    				</td>
	    			</tr>
	    			<tr>
	    				<td style="width: 25%">
	    					No. Contrato SAI:
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" disabled="disabled" id="cContratoDefinitivoV" maxlength="25" name="cContratoDefinitivoV" style="width: 400px" onKeyPress="return Sinespacios(event)" />
	    				</td>
	    			</tr>
	    			<tr>
	    				<td style="width: 25%">
	    					<input type="text" style="width: 100%;border-width:0; background-color:transparent" name="inpNumContCNET" id="inpNumContCNET" value="No. de Contrato Compranet" readonly />
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" id="cnumCompranet" name="cnumCompranet" style="width: 400px" maxlength="100"/>
	    				</td>
	    			</tr>
	    			<tr  id="trCodExpCNET" style="display: none;">
	    				<td style="width: 25%">
	    					C&oacute;digo de Expediente Compranet:
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" id="nCodExpedienteCNET" name="nCodExpedienteCNET" style="width: 200px" maxlength="100" />
	    				</td>
	    			</tr>
	    			<tr id="trCodContCNET" style="display: none;">
	    				<td style="width: 25%">
	    					C&oacute;digo de Contrato Compranet:
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" id="nCodContratoCNET" name="nCodContratoCNET" style="width: 200px" maxlength="100" />
	    				</td>
	    			</tr>
	    			<tr id="trOficioDG" style="display: none;">
	    				<td style="width: 25%">
	    					Oficio DG:
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" id="oficioDG" name="oficioDG" style="width: 400px" maxlength="100"/>
	    				</td>
	    			</tr>
	    			<tr id="trFolioMascp" style="display: none;">
	    				<td style="width: 25%">
	    					Folio MASCP:
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" id="folioMASCP" name="folioMASCP" style="width: 400px" maxlength="100"/>
	    				</td>
	    			</tr>
	    			<tr  id="trffal">
	    				<td style="width: 25%">
	    					Fecha de Fallo:
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" id="fechaFalloV" readonly name="fechaFalloV" />
	    				</td>
	    			</tr>
	    			<tr id="trfsol" style="display: none;">
	    				<td style="width: 25%">
	    					Fecha de Solicitud:
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" id="fechaSolicitudV" readonly name="fechaSolicitudV" />
	    				</td>
	    			</tr>
	    			<tr id="trfpro" style="display: none;">
	    				<td style="width: 25%">
	    					Fecha de Presentaci&oacute;n de Propuestas:
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" id="fechaPropuestasV" readonly name="fechaPropuestasV" />
	    				</td>
	    			</tr>
	    			<tr id="trffor">
	    				<td style="width: 25%">
	    					Fecha de Formalizaci&oacute;n:
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" id="fechaFormalizacionV"  readonly name="fechaFormalizacionV" />
	    				</td>
	    			</tr>
	    			<tr id="trfini">
	    				<td style="width: 25%">
	    					Fecha de Inicio:
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" id="fechaInicioV" readonly name="fechaInicioV"  />
	    				</td>
	    			</tr>
	    			<tr id="trffin">
	    				<td style="width: 25%">
	    					Fecha de Fin:
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" id="fechaFinV" readonly name="fechaFinV" />
	    				</td>
	    			</tr>
	    			<tr id="trfentrega">
	    				<td style="width: 25%">
	    					Fecha de Entrega:
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" id="fechaEntregaV" readonly name="fechaEntregaV" />
	    				</td>
	    			</tr>
	    			<tr id="trErrorFechas" style="visibility: hidden;" >
				    	<td colspan="2"  align="left">
				    		<textarea rows="2" cols="20" style="width: 700px" id="lblMsj" name="lblMsj" readonly style="overflow: auto; border-width:0; background-color:transparent" ></textarea>  
				    	</td>	    									    		
			    	</tr>
	    			<tr style="display: none;">
	    				<td style="width: 25%">
	    					RFC Servidor P&uacute;blico Solicitante:
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" id="rfcServPubSolicitante" readonly="readonly"  name="rfcServPubSolicitante" class="AyudaSyC obligatorio desahabilitado"/>
	    				</td>
	    			</tr>
	    			<tr style="display: none;">
	    				<td style="width: 25%">
	    					RFC Servidor P&uacute;blico Revisor Jur&iacute;dico:
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" id="rfcServPubRevJuridico" readonly="readonly" name="rfcServPubRevJuridico" class="AyudaSyC obligatorio desahabilitado" />
	    				</td>
	    			</tr>
	    			<tr>
	    				<td colspan="2">
	    					<fieldset>
	    						<legend>Tipo de contrataci&oacute;n</legend>
	    						<table style="width: 100%">
	    							<tr>
					    				<td style="width: 25%">
					    					<input type="radio" name='grupoTipoPago' id='centralizado' value='0' onclick="manejaRadioBtn()" title="Solo el &aacute;rea del contrato podra generar los pagos de lo contratado"/>Centralizado
					    				</td>
					    				<td style="width: 75%">
					    					<input type="radio" name='grupoTipoPago' id='descentralizado' value='1' checked onclick="manejaRadioBtn()" title="Todas las &aacute;reas que participen en la contrataci&oacute; prodran ejecutar sus pagos que le s corresponde de la parte contratada"/>Descentralizado
					    				</td>
					    			</tr>
	    						</table>
	    					</fieldset>
	    				</td>
	    			</tr>
	    			<tr>
	    				<td colspan="2">
	    					<fieldset>
	    						<legend>Garant&iacute;as</legend>
	    						<table style="width: 100%">
	    							<tr>
	    								<td style="width: 30%">
	    									<input type="checkbox" name="checkExcentoGarantia" id="checkExcentoGarantia" onclick="excentaGarantia()"/>
	    									Contrato Exento de Garant&iacute;as
					    				</td>
					    			</tr>
					    			<tr>
					    				<td style="width: 30%">
					    					¿Se otorgar&aacute; anticipo?:
					    					<input type="checkbox" name="checkLlevaAnticipo" id="checkLlevaAnticipo" onclick="OtorgaAnticipo()"/>
					    				</td>
	    								<td style="width: 20%">
	    									Garant&iacute;a de Anticipo:<br>
					    					<input type="text" id="mGarantiaAnticipo"  name="mGarantiaAnticipo" onkeypress="return(onlyNumbers(event));" value="0" onblur="sumaTotalGarantias();" readonly="readonly" title="La captura del monto es con IVA"/>
					    				</td>
					    				<td style="width: 20%">
					    					Garant&iacute;a de Cumplimiento:<br>
					    					<input type="text" id="mGarantiaCumplimiento"  name="mGarantiaCumplimiento" onkeypress="return(onlyNumbers(event));" value="0" onblur="sumaTotalGarantias();" title="La captura del monto es sin IVA"/>
					    				</td>
					    				<td style="width: 30%">
					    					Monto Total de las Garant&iacute;as:<br>
					    					<input type="text" id="mTotalGarantias"  name="mTotalGarantias"  readonly="readonly" value="0"/>
					    				</td>
	    							</tr>
	    						</table>
	    					</fieldset>
	    				</td>
	    			</tr>
	    			<tr>
	    				<td style="width: 25%">
	    					Mecanismos de Vigilancia:
	    				</td>
	    				<td style="width: 75%">
	    					<input type="text" id="cMecanismosVigilancia"  name="cMecanismosVigilancia" style="width: 400px"/>
	    				</td>
	    			</tr>
	    			<tr style="display: none;">
	    				<td style="width: 25%">
	    					<input type="checkbox" name='chkImpuestosAdicionales' id='chkImpuestosAdicionales' value='0' onclick="muestraImpuestosAdicionales();"/>Impuestos Adicionales
	    				</td>
	    				<td style="width: 75%">
	    					<div id="divImpuestosAdicionales" style="display: none">
				    			<table style="width: 100%">
				    				<tr>
				    					<td>&nbsp;</td>
				    					<td>Descripci&oacute;n</td>
				    					<td>Porcentaje</td>
				    				</tr>
				    				<tr>
				    					<td>Impuesto 1:</td>
				    					<td><input type="text" name="cDescripcionImpuestoAdicional1" id="cDescripcionImpuestoAdicional1" size="15" maxlength="10"/></td>
				    					<td><input type="text" name="cPorcentajeImpuestoAdicional1" id="cPorcentajeImpuestoAdicional1" size="5" maxlength="5" onkeypress="return(onlyNumbers(event));"/></td>
				    				</tr>
				    				<tr>
				    					<td>Impuesto 2:</td>
				    					<td><input type="text" name="cDescripcionImpuestoAdicional2" id="cDescripcionImpuestoAdicional2" size="15" maxlength="10"/></td>
				    					<td><input type="text" name="cPorcentajeImpuestoAdicional2" id="cPorcentajeImpuestoAdicional2" size="5" maxlength="5" onkeypress="return(onlyNumbers(event));"/></td>
				    				</tr>
				    				<tr>
				    					<td>Impuesto 3:</td>
				    					<td><input type="text" name="cDescripcionImpuestoAdicional3" id="cDescripcionImpuestoAdicional3" size="15" maxlength="10"/></td>
				    					<td><input type="text" name="cPorcentajeImpuestoAdicional3" id="cPorcentajeImpuestoAdicional3" size="5" maxlength="5" onkeypress="return(onlyNumbers(event));"/></td>
				    				</tr>
				    			</table>
				    		</div>
	    				</td>
	    			</tr>
	    			<tr>
	    				<td style="width: 25%">
	    					Otros Impuestos <input type="checkbox" name='chkOtrosImpuestos' id='chkOtrosImpuestos' value='0' onclick="muestraOtrosImpuestos();"/>
	    				</td>
	    				<td style="width: 75%">
	    					<div id="divOtrosImpuestos" style="display: none">
				    			<span id="MESSAGESPAN" style="display: none; color: red;" >PARA MODIFICAR LOS IMPUESTOS EL ESTATUS DEL CONTRATO DEBE DE ESTAR EN CAPTURADO</span>
				    			<table style="width: 100%">
				    				<tr>
				    					<td>&nbsp;</td>
				    					<td>Descripci&oacute;n</td>
				    					<td>Monto</td>
				    				</tr>
				    				<tr>
				    					<td>Impuesto 1:</td>
				    					<td><input type="text" name="cDescripcionOtroImpuesto1" id="cDescripcionOtroImpuesto1" size="15" maxlength="10"/></td>
				    					<td><input type="text" name="cMontoOtroImpuesto1" id="cMontoOtroImpuesto1" size="10" maxlength="15"  onblur="cambiafrmt(this.name);"  onkeypress="return(onlyNumbers(event));"/></td>
				    				</tr>
				    				<tr>
				    					<td>Impuesto 2:</td>
				    					<td><input type="text" name="cDescripcionOtroImpuesto2" id="cDescripcionOtroImpuesto2" size="15" maxlength="10"/></td>
				    					<td><input type="text" name="cMontoOtroImpuesto2" id="cMontoOtroImpuesto2" size="10" maxlength="15"  onblur="cambiafrmt(this.name);"  onkeypress="return(onlyNumbers(event));"/></td>
				    				</tr>
				    				<tr>
				    					<td>Impuesto 3:</td>
				    					<td><input type="text" name="cDescripcionOtroImpuesto3" id="cDescripcionOtroImpuesto3" size="15" maxlength="10"/></td>
				    					<td><input type="text" name="cMontoOtroImpuesto3" id="cMontoOtroImpuesto3" size="10" maxlength="15" onblur="cambiafrmt(this.name);"  onkeypress="return(onlyNumbers(event));"/></td>
				    				</tr>
				    			</table>
				    		</div>
	    				</td>
	    			</tr>
	    			<tr>
	    				<td style="width: 25%">
	    					¿Es una Contrataci&oacute;n para un "PSP"? <input type="checkbox" name="checkLEsPSP" id="checkLEsPSP" checked="checked" onclick="showAndHideTablePSP()"/>
	    				</td>
	    				<td style="width: 75%">
	    					<div id="trDatosPSP">
	    					<fieldset>
	    					<legend>Datos para una contrataci&oacute;n de un PSP</legend>
	    						<div style="width: 100%">
				    				<table style="width: 100%">
				    					<tr>
				    						<td style="width: 15%">
					    						&Aacute;rea Requirente:
					    					</td>
					    					<td style="width: 85%">
					    						<select name='cAreaReq' id='cAreaReq' style='width: 90%' onchange="obtieneAreasResponsables()"></select>
					    					</td>
				    					</tr>
				    					<tr>
					    					<td style="width: 15%">
					    						&Aacute;rea Responsable:
					    					</td>
					    					<td style="width: 85%">
					    						<select name='cAreaResp' id='cAreaResp' style='width: 90%' ></select>
					    					</td>
					    				</tr>
					    				<tr>
					    					<td style="width: 15%">
					    						Centro de Trabajo:
					    					</td>
					    					<td style="width: 85%">
					    						<select name='cCentroTrabajo' id='cCentroTrabajo' style='width: 90%' ></select>
					    					</td>
					    				</tr>
					    				<tr>
					    					<td style="width: 15%">
					    						Monto Mensual con IVA:
					    					</td>
					    					<td style="width: 85%">
					    						<input type="text" id="mMontoMensual" name="mMontoMensual" value="0.00" style='width: 90%' onkeypress="return onlyDoubles(event)"/>
					    					</td>
					    				</tr>
					    				<tr>
					    					<td style="width: 15%">
					    						Denominaci&oacute;n del Proyecto:
					    					</td>
					    					<td style="width: 85%">
					    						<input type="text" id="cDenominacionProyecto" name="cDenominacionProyecto" value="" style='width: 90%' maxlength="500"/>
					    					</td>
					    				</tr>
					    				<tr>
								    		<td align="left">¿Es Maestro?</td>
								    		<td align="left"><input type="checkbox" name="checkLEsMaestro" id="checkLEsMaestro"  onclick="esMaestro()"/></td>
								    	</tr>
				    				</table>
				    			</div>
	    					</fieldset>
	    					</div>
	    				</td>
	    			</tr>
	    			<tr id="trPrestacionServicio" style="display: none;">
	    				<td colspan="2">
	    					<fieldset style="width: 95%;height: 20%">
	    						<legend>Lugar de prestaci&oacute;n del servicio o entrega de bienes</legend>
								<table style="width: 100%">
		    						<tr id="trAgregaServicio">
		    							<td style="width: 50%">
		    								Centro de Trabajo:<select name='cCentroTrabajoCont' id='cCentroTrabajoCont' style='width: 90%' ></select>
		    							</td>
		    							<td style="width: 50%">
		    								<input type="button"  name="btnAgregarCentroTrabajoCont" id="btnAgregarCentroTrabajoCont" value="Agregar" onclick="agregaCentroTrabajo()" class="btnInterfaceBG ui-button ui-corner-all" />
		    							</td>
		    						</tr>
		    						<tr>
		    							<td colspan="2">
		    								<div id="divServicioEntrega"  style="width: 50%;">
												<table id="tblervicioEntrega" class="display" style="width: 100%" >
													<thead >
														<tr>
															<th align="center" >Centro de trabajo</th>
															<th align="center">Descripci&oacute;n</th>
															<th align="center"></th>
														</tr>
													</thead>
												</table>
											</div>
		    							</td>
		    						</tr>
		    					</table>
	    					</fieldset>
	    				</td>
	    			</tr>
	    			<tr id="trJustificacionTipoProced">
	    				<td style="width: 25%">
	    					Desea justificar el tipo de procedimiento:<input type="checkbox" id="checkJustificacion" name="checkJustificacion" onclick="enableDisabledDescrip()"/>
	    				</td>
	    				<td style="width: 75%">
	    					Descripci&oacute;n de la justificaci&oacute;n:<br /><textarea rows="3" cols="60" id="descripJustTipoProced" name="descripJustTipoProced" disabled="disabled" ></textarea>
	    				</td>
	    			</tr>
	    			<tr>
			    		 <td colspan="2" align="center"><input type="button"  name="btnGuardarCaratulaCont" id="btnGuardarCaratulaCont" value="Guardar" onclick="guardaCaratula()" class="btnInterfaceBG ui-button ui-corner-all" /></td>
			    	</tr>
	    		</table>
	    		
	    </fieldset>
					
		<!-- Hidden's -->
		<!-- Sesion  -->
		<input type="hidden" id="lJustificaTipoProced" name="lJustificaTipoProced" value="0" />
		<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
		<input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
		<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
		<input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoContrato%>" />
		<input type="hidden" name="cIdContrato" id="cIdContrato" value="<%=cIdTipoContrato%>-<%=cIdUnidadEjecutora%>-<%=nIdConsecutivo%>" />
		<input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuarioTab.getU_UR() %>"/>
		<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuarioTab.getLogin() %>"/>
		<input type="hidden" name="R_NOMBRE" id="R_NOMBRE" >	    
		<!-- Resultado de consultas -->
		<input type="hidden" name="nIdTipoCambio" id="nIdTipoCambio" />
		<input type="hidden" name="nIdEstado" id="nIdEstado" />	     
		<input type="hidden" name="nIdConsecutivoProcedimiento" id="nIdConsecutivoProcedimiento" />
		<input type="hidden" name="cIdTipoProcedimiento" id="cIdTipoProcedimiento" />
		<input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />
		<input type="hidden" name="existePrecom" id="existePrecom" /> 
		<input type="hidden" name="cDescripcionOtroImpuesto" id="cDescripcionOtroImpuesto" value=""/>
		<input type="hidden" name="cMontoOtroImpuesto" id="cMontoOtroImpuesto" value=""/>
		<input type="hidden" name="cImpuestoOtros" id="cImpuestoOtros" value=""/> 
		<input type="hidden" name="ConsecutivoProcedimientoCopia" id="ConsecutivoProcedimientoCopia" value=""/>
		<input type="hidden" name="nIdConsecutivoContratoClon" id="nIdConsecutivoContratoClon" value=""/>
		<input type="hidden" name="lContratoAbiertoClon" id="lContratoAbiertoClon" value=""/>
		<input type="hidden" name="nIdEstadoContratoClon" id="nIdEstadoContratoClon" value=""/>
		<input type="hidden" name="estadoPartidaProcedimiento" id="estadoPartidaProcedimiento" value=""/>	
		<input name="mImporteCompromiso" id="mImporteCompromiso" type="hidden"> 	    
		<!-- Hidden's auxiliares -->
		<input type="hidden" name="mTipoCambio" id="mTipoCambio" />
		<input type="hidden" name="cConcepto" id="cConcepto" />
		<input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" />
		<input type="hidden" name="fechaFallo" id="fechaFallo" />
		<input type="hidden" name="fechaFormalizacion" id="fechaFormalizacion" />
		<input type="hidden" name="fechaInicio" id="fechaInicio" />
		<input type="hidden" name="fechaFin" id="fechaFin" />
		<input type="hidden" name="fechaEntrega" id="fechaEntrega" />
		<input type="hidden" name="fechaInicioTMP" id="fechaInicioTMP" />
		<input type="hidden" name="fechaFinTMP" id="fechaFinTMP" />
		<input type="hidden" name="fechaSolicitud" id="fechaSolicitud" />
		<input type="hidden" name="fechaPropuestas" id="fechaPropuestas" />	
		<input type="hidden" name="nIdCategoria" id="nIdCategoria" value="" />
		<input type="hidden" name="esDescentralizado" id="esDescentralizado"/>	
		<input type="hidden" name="cDescripcionImpuestoAdicional" id="cDescripcionImpuestoAdicional" value=""/>
		<input type="hidden" name="cPorcentajeImpuestoAdicional" id="cPorcentajeImpuestoAdicional" value=""/>
		<input type="hidden" name="cImpuesto" id="cImpuesto" value=""/>
		<input type="hidden" name="cTipoContrato" id="cTipoContrato" />
		<input type="hidden" name="cIdProcedimiento" id="cIdProcedimiento" />
		<input type="hidden" name="cIdCategoriaProcedimiento" id="cIdCategoriaProcedimiento" />
		<input type="hidden" name="cCategoriaProcedimiento" id="cCategoriaProcedimiento" />
		<input type="hidden" name="cTotalPagosContrato" id="cTotalPagosContrato"/>
		<input type="hidden" name="cValidaPagosPedido" id="cValidaPagosPedido"/>
		<input type="hidden" name="responsable" id="responsable" />	
		<input type="hidden" name="numeroContratos" id="numeroContratos" />	
		<input type="hidden" name="cContableRevisa" id="cContableRevisa" />	
		<input type="hidden" name="numDiversos" id="numDiversos" />
		<input type="hidden" name="tienePasivoPlurianual" id="tienePasivoPlurianual" />
		<input type="hidden" name="existeRegistro" id="existeRegistro" />
		<!-- Fechas -->
		<input type="hidden" name="nIdFechaInicio" id="nIdFechaInicio" />
		<input type="hidden" name="nIdFechaFin" id="nIdFechaFin" />
		<input name="cHipDocCont" id="cHipDocCont" type="hidden" value="">
		<input name="cHipInfAvance" id="cHipInfAvance" type="hidden" value="">
		<input name="cHipConvAut" id="cHipConvAut" type="hidden" value=""> 
		<input name="cAccion" id="cAccion" type="hidden">
		<input name="cIdDocumento" id="cIdDocumento" type="hidden">
		<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuarioTab.getLogin()%>"/>
		<input name="ValObtenidoFL" id="ValObtenidoFL" type="hidden">
		<input name="isPlurianual" id="isPlurianual" type="hidden">
		<input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE" value=""/>
		<input type="hidden" name="cIdRFC" id="cIdRFC" value=""/> 
		<input type="hidden" name="actualizaDocContrato" id="actualizaDocContrato" value="0"/> 
		<input type="hidden" name="validacionMontoTipoAdj" id="validacionMontoTipoAdj" value="0" />
		<input name="cCentroContable" type="hidden" id="cCentroContable"  value="<%=cCentroContable%>"/>
		<input type="hidden" name="cAplica15D" id="cAplica15D" value="N" />
		<input type="hidden" name="cIdConsolidado" id="cIdConsolidado" value="" />
		<input type="hidden" name="nIdConsecutivoAdj" id="nIdConsecutivoAdj"  />
		<input type="hidden" name="nEsContratacionPSP" id="nEsContratacionPSP" value=""  />
		<input type="hidden" id="tipoProceso" name="tipoProceso" value="1" />
		<input type="hidden" id="tipoOperacion" name="tipoOperacion" value="1" />
		<input type="hidden" id="nElPSPEsMaestro" name="nElPSPEsMaestro" value="0" />
		<input type="hidden" id="HAYINFO" name="HAYINFO" value="" />
  		<input type="hidden" id="nllevaAnticipo" name="nllevaAnticipo" value="0" />
  		<input type="hidden" id="cCentroTrabajoContDescrip" name="cCentroTrabajoContDescrip" value="" />
  		<input type="hidden" id="nIdCentroTrabajo" name="nIdCentroTrabajo" value="0" />
  		<input type="hidden" id="lExcentaGarantia" name="lExcentaGarantia" value="0" />
  		<input type="hidden" id="cPedidoDefinitivo" name="cPedidoDefinitivo" value="" />
  		
  		
	    </div>
	    
	</form>
  </body>
</html>
