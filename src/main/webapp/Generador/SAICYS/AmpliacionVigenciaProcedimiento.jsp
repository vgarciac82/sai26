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
	String cIdTipoProcedimiento = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	
	if (session.getAttribute(GestionInterface.ATT_ProEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ProEjercicio);
		cIdTipoProcedimiento = (String)session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ProConsecutivo);
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Consolidado</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Consolidado">
		
		<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
			$("#tbs").val(12);
			showAndHideTabs();
		<%
				
			NegativaPestana NegPestana=new NegativaPestana();
			NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
			//botones
			
			NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
			int imgAnular=0;
			int imgAprobar=0;
			int imgDevolver=0;
			Iterator it1 = rol.entrySet().iterator();
			Role role = new Role();
			while (it1.hasNext()) {
				Map.Entry r = (Map.Entry)it1.next();
				roles += r.getKey().toString()+",";
			}
			if(roles.length()>0){
				roles = roles.substring(0,roles.length()-1);
			}
			Map botones=nb.getBotones(roles,"Procedimiento","ampliacionVigenciaProcedimiento");
			Iterator btn = botones.entrySet().iterator();
			while (btn.hasNext()) {
				Map.Entry b = (Map.Entry)btn.next();%>
				$("#<%=b.getValue()%>").attr("disabled", true);<%
				String img=(String) b.getValue();
				if ("imgAnularCaratula".equals(img)){
					imgAnular=1;
				}
				if ("imgAprobarCaratula".equals(img)){
					imgAprobar=1;
				}
				if ("imgDevolverCaratula".equals(img)){
					imgDevolver=1;
				}
			}
		%>	

		$("#cIdProcedimiento").val($("#cIdTipoProcedimiento").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());
		$("#U_LOGIN").val('<%=usuario.getLogin()%>');
		$("#cIdEntidadContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
		
		$("#usuarioRoleProcedimiento").val('<%=roles%>');		
		querySelectPost("CategoriaRead", "cboCategoria", {async : false});
		
		muestraInformacionCaratula();	
		habilitarPestanas();	
		agregaFechas();
		queryFormPost("obtieneIdCasoProcedimiento",{async:false});
		
		
		queryFormPost("motivoRechazo", { async:false });
		if($("#motivo").val()!=""){
			$("#lblMotivoRechazo").val($("#motivo").val());
			document.getElementById("lblRechazo").style.display="block";
			document.getElementById("btnEnviar").style.display="block";
		}else{
			queryFormPost("vigenciasEspera", { async:false });
			if($("#solicitudEspera").val()==1){
				$("#lblMotivoRechazo").val("Solicitud de ampliación pendiente de aprobación");
				document.getElementById("lblRechazo").style.display="block";
				document.getElementById("btnEnviar").style.display="none";
			}else{
				document.getElementById("lblRechazo").style.display="none";
				document.getElementById("btnEnviar").style.display="block";
			}
			
		}
	});
		
		function textCounter( field, maxlimit ) {
			if ( field.value.length > maxlimit )
				field.value = field.value.substring( 0, maxlimit );
		}
			
		function validar(e) {
			tecla = (document.all) ? e.keyCode : e.which;
			if (tecla==8) return true;
				patron =/[\w\d\s\\.\/\_\ñ\Ñ]/;					
				te = String.fromCharCode(tecla);																				
				var v = document.getElementById('desConsol').value.replace("ñ", "n").replace("Ñ", "N");					
				document.getElementById('desConsol').value = v;
				return patron.test(te);
		}
			
	
		function agregaFechas(){
			 $("#nuevaFecha").datepicker({
			 	beforeShowDay: nonWorkingDates,						
				dateFormat: "dd/mm/yy",
				currentText: "Now",
				showOn: 'button',
				altField: "#actualDate",
				buttonImageOnly: true,	
			    buttonImage: '../images/calendar.gif',			    					 
				changeYear: true
			});
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
		
	// 	        for (i = 0; i < closedDates.length; i++) {
	// 	            if (date.getMonth() == closedDates[i][0] - 1 &&
	// 	            date.getDate() == closedDates[i][1] &&
	// 	            date.getFullYear() == closedDates[i][2]) {
	// 	                return [false];
	// 	            }
	// 	        }
		
		        return [true];
		    }	
	 	function solicitarExtension(){
			if ($("#uploadfile").val() != "" || $("#nPaginas").val() > 0) {
				enviarSolicitud();
			} else {
				alert("Debe adjuntar un documento");
			}
		} 
			
		function enviarSolicitud(){
			var miforma = document.getElementById("upform");
			miforma.action = "../../servlet/ProcedimientoServlet?nFolioPrecompromiso="+$("#nFolioPreCompromiso").val()+"&nuevaFecha="+$("#nuevaFecha").val();
			var datos = "";
     				datos.append('messageData', $('upform').serialize());
      			var filesList = document.getElementById('uploadfile');
      			
      			for (var i = 0; i < filesList.files.length; i ++) {
          			datos.append('file', filesList.files[i]);
			}
			
			$.ajax({
				url: '../../servlet/ProcedimientoServlet?nFolioPrecompromiso='+$("#nFolioPreCompromiso").val()+"&nuevaFecha="+$("#nuevaFecha").val(), 					
				async: false, 
				processData: false,
          			type:        'POST',
          			contentType: false,
				dataType: 'json', 
				data : datos,
				//Si el ajax fue success
				success: function(json){
				
					mensajeAp=j[0].Devuelve;
					alert(mensajeAp);
							
					//Si no hay folio en la respuesta json
					if (result == "true") {
						alert("Enviado para extension de vigencia");
						init(true);
					}
				},
				error:function(json){
					alert("error");
				}
			});
					
				
			}
		function comprueba_extension(formulario, archivo) { 
			queryFormPost("vigencias",{async:false});
			 if($("#enEspera").val()==0){
				 extensiones_permitidas = new Array(".pdf"); 
				 mierror = ""; 
				 if (!archivo || $("#nuevaFecha").val()=='') { 
				      //Si no tengo archivo, es que no se ha seleccionado un archivo en el formulario 
				      	mierror = "Faltan datos por ingresar"; 
				 }else{ 
				      //recupero la extensión de este nombre de archivo 
				      extension = (archivo.substring(archivo.lastIndexOf("."))).toLowerCase(); 
				      //alert (extension); 
				      //compruebo si la extensión está entre las permitidas 
				      permitida = false; 
				      for (var i = 0; i <= extensiones_permitidas.length; i++) { 
				         if (extensiones_permitidas[i] == extension) { 
					         permitida = true; 
					         break; 
				         } 
				      } 
				      if (!permitida) { 
				         mierror = "Comprueba la extensión de los archivos a subir. \nSólo se pueden subir archivos con extensiones: " + extensiones_permitidas.join();
				         alert(mierror); 
				        
				      }else{ 
				      	fileUpload(documento.getElementById("upform"),'upload');
				      } 
			   } 
			 }else{
			 	alert("El consolidado tiene una solicitud de ampliación pendiente")
			 	
			 }
			   
		   }
		   
		   
		   
function fileUpload(form, div_id) {
	if (validaModificarProcedimiento()){
		 // Create the iframe...
     var archivo = $("#uploadfile");
     queryFormPost("vigencias",{async:false});
     if($("#enEspera").val()==0){
		 extensiones_permitidas = new Array(".pdf"); 
		 mierror = ""; 
		 if ( $("#uploadfile").val() == "" || $("#nuevaFecha").val()=='') { 
		      //Si no tengo archivo, es que no se ha seleccionado un archivo en el formulario  
		     alert("Faltan datos por ingresar");
		 }else{ 
		      //recupero la extensión de este nombre de archivo 
		      extension = ($("#uploadfile").val().substring($("#uploadfile").val().lastIndexOf("."))).toLowerCase(); 
		      //alert (extension); 
		      //compruebo si la extensión está entre las permitidas 
		      permitida = false; 
		      for (var i = 0; i <= extensiones_permitidas.length; i++) { 
		         if (extensiones_permitidas[i] == extension) { 
		         permitida = true; 
		         break; 
		         } 
		      } 
		      
		      if (!permitida) { 
		         mierror = "Comprueba la extensión de los archivos a subir. \nSólo se pueden subir archivos PDF";
		         alert(mierror); 
		        
		      	}else{ 
		         	 /****************/ 
					var iframe = document.createElement("iframe");
					iframe.setAttribute("id", "upload_iframe");
					iframe.setAttribute("name", "upload_iframe");
					iframe.setAttribute("width", "0");
					iframe.setAttribute("height", "0");
					iframe.setAttribute("border", "0");
					iframe.setAttribute("style", "width: 0; height: 0; border: none;");

	   
					form.parentNode.appendChild(iframe);
					window.frames['upload_iframe'].name = "upload_iframe";

				    iframeId = document.getElementById("upload_iframe");

  						// Add event...
  						var eventHandler = function () {

          					if (iframeId.detachEvent) iframeId.detachEvent("onload", eventHandler);
          					else iframeId.removeEventListener("load", eventHandler, false);

          					// Message from server...
          					if (iframeId.contentDocument) {
               				content = iframeId.contentDocument.body.innerHTML;
  	        				} else if (iframeId.contentWindow) {
      	        				content = iframeId.contentWindow.document.body.innerHTML;
          					} else if (iframeId.document) {
              					content = iframeId.document.body.innerHTML;
          					}

          					document.getElementById(div_id).innerHTML = content;

  	        				// Del the iframe...
      	   					// setTimeout('iframeId.parentNode.removeChild(iframeId)', 250000);
      					}

  						if (iframeId.addEventListener) iframeId.addEventListener("load", eventHandler, true);
  						if (iframeId.attachEvent) iframeId.attachEvent("onload", eventHandler);
						
  						// Set properties of form...
  						form.setAttribute("target", "upload_iframe");
  						form.setAttribute("action", "../../servlet/ProcedimientoServlet?nFolioPrecompromiso="+$("#nFolioPreCompromiso").val()+"&nuevaFecha="+$("#nuevaFecha").val()+"&idCaso="+$("#idCaso").val());
  						form.setAttribute("method", "post");
  						form.setAttribute("enctype", "multipart/form-data");
  						form.setAttribute("encoding", "multipart/form-data");

  						// Submit the form...
  						form.submit();
		      		//fileUpload(documento.getElementById("upform"),'upload');
		      		
		      			alert("Enviado para extension de vigencia");
						//init(true);
					
					//Bitácora
					$("#cAccion").val("AMPLIACION_VIGENCIA_PRECOMPROMISO");
					$("#cIdDocumento").val($("#cIdTipoProcedimiento").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
					$("#cIdUsuario").val($("#U_LOGIN").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
					
		         /***************************/
		      	} 
	   		} 
		 }else{
		 	alert("El consolidado tiene una solicitud de ampliación pendiente");
		 	
		 }
	}
}

	function muestraInformacionCaratula (){
		   queryFormPost("llenaCaratulaProcedimiento",{async:false});
		   queryFormPost("llenaUnidadEjecutoraProcedimiento",{async:false});
		   queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
		   queryFormPost("mProcedimientoCumpleRequisitosRead",{async:false});
		   queryFormPost("mValidaPrecompromisoProcedimiento",{async:false});
		//   queryFormPost("apartadoConsolidado",{async:false});
		   queryFormPost("fn_mConsolidadoCalculaMontoConIVARead",{async:false});
		   queryFormPost("vigenciaProcedimiento",{async:false});
		  
		   queryFormPost("motivoRechazo", { async:false });
			if($("#motivo").val()!=""){
				$("#lblMotivoRechazo").val($("#motivo").val());
				document.getElementById("lblRechazo").style.display="block";
				document.getElementById("btnEnviar").style.display="block";
			}else{
				queryFormPost("vigenciasEspera", { async:false });
				if($("#solicitudEspera").val()==1){
					$("#lblMotivoRechazo").val("Solicitud de ampliación pendiente de aprobación");
					document.getElementById("lblRechazo").style.display="block";
					document.getElementById("btnEnviar").style.display="none";
				}else{
					document.getElementById("lblRechazo").style.display="none";
					document.getElementById("btnEnviar").style.display="block";
				}
				
			}
		   
		   
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
			
			if($("#nIdEstado").val()==1){
				 queryFormPost("apartadoConsolidado",{async:false});
			}else{
				 queryFormPost("sumPrecomProcedimiento",{async:false});
			}
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
			}else{ // trae precompromiso
				if ($("#nIdEstado").val()==1){ 
					if($("#documentoAplicado").val()==0){ // precompromiso cancelado
						$("#presupuestoProcedimiento" ).css("display", "block");
						$("#precompromisoProcedimiento" ).css("display", "block");	
					}else{
						$("#presupuestoProcedimiento" ).css("display", "none");
						$("#precompromisoProcedimiento" ).css("display", "none");
					}
				}else{
					if($("#nIdEstado").val()==2){
						queryFormPost("validaFlujoProcedimiento", {async:false});
						if($("#cEventoFlujo").val()=="DISP_PRECOMMAT" || $("#cEventoFlujo").val()=="PRECOM_MAT" ){
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
					/* $("#presupuestoProcedimiento" ).css("display", "none");
					$("#precompromisoProcedimiento" ).css("display", "none"); */
					
						if($("#nIdEstado").val()==2){
						queryFormPost("validaFlujoProcedimiento", {async:false});
						if($("#cEventoFlujo").val()=="DISP_PRECOMMAT"){
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
	
	function esRegularizacion(){
		var regularizacion=false;
		var apartables = [ "PC", "PO", "PS", "PA", "PT" ];
		if ($.inArray($("#cIdTipoProcedimiento").val(), apartables) == -1){ // no traen apartado
			regularizacion=true;
		}else
			regularizacion=false;
		return regularizacion;
	}
	
	function validaModificarProcedimiento(){ 
		if(($('#cIdUsuarioCreacion').val()== $("#U_LOGIN").val())||( $('#usuarioRoleProcedimiento').val().toString().indexOf('ADMIN_RECMAT') >= 0)){
			return true;
		}
		else{
			alert("El usuario no tiene permiso para realizar esta acci\xF3n.");
			return false;
		}
	}
	
	
	</script>
	</head>
	<body id="dt_example" >
			<fieldset>
				<legend>Información del Procedimiento</legend>
				<table align="left" cellpadding="2" width="100%">
			    	<tr>
			    		<td align="right" colspan="2">
                            <input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	 onclick="window.location = 'Procedimiento-copia.jsp?tab=1';" />&nbsp;&nbsp;
                       	</td>
			    	</tr>
			    	<tr align="left">
						<td colspan="2">
							[[<input name="cIdProcedimiento" id="cIdProcedimiento" type="text" size ="10" style="border-width:0; background-color:transparent;" readonly="readonly"/>]]&nbsp;
							<input name="desProcedimientoCaratula" id="desProcedimientoCaratula" type="text" maxlength="150" style="border-width:0; background-color:transparent; width: 40em;" readonly="readonly"></input>  
						</td>								
					</tr>
				   <tr align="left">
						<td colspan="2">
							<input name="lblcIdUnidadEjecutora" id="lblcIdUnidadEjecutora" type="text" readonly="readonly" style="border-width:0; background-color:transparent; width: 30em;"></input>  
						</td>								
					</tr>
					<tr align="left">
						<td colspan="2">
						Consolidado: [[<input name="cIdConsolidado" id="cIdConsolidado" type="text" size ="10" style="border-width:0; background-color:transparent;" readonly="readonly"></input>]]  
						</td>								
					</tr>
					<tr align="left">
						<td colspan="2">
						Tipo de Proceso: [[<input name="lbltipoProceso" id="lbltipoProceso" type="text" size ="18" style="border-width:0; background-color:transparent;" readonly="readonly"></input>]]  
						</td>								
					</tr>
					<tr align="left">
						<td colspan="2">
							<input name="nIdEstadoProcedimiento" id="nIdEstadoProcedimiento" type="text"  style="border-width:0; background-color:transparent; width: 45em;"></input>  
						</td>								
					</tr>
					<tr align="left">
						<td colspan="2">
								Monto Precomprometido: [[<input name="lblApartado" id="lblApartado" type="text" size ="10" style="border-width:0; background-color:transparent;" readonly="readonly"></input>]]
						</td>								
					</tr>
					<tr>
						<td align="left" style="color: black " colspan="2">																						  
							Total Consolidado con IVA: $<input name="MontoConIVA" id="MontoConIVA"  readonly style="border-width:0; background-color:transparent;">										
						</td>
					</tr>
					<tr id="lblRechazo" style="display: none">
			    		<td align="left" colspan="2"><input type="text" style="color:red; width: 500px;"" name="lblMotivoRechazo" id="lblMotivoRechazo" readonly style="border-width:0; background-color:transparent"/></td>
			    	</tr>
			    </table>
			</fieldset>
			<fieldset>
  				<legend>Ampliación de vigencia</legend>
				<table border="0" width="100%">
					<tr >
						<td colspan="2">
							<input id="cEjercicio" name="cEjercicio" type="hidden" size="4" value="<%= cEjercicio %>">
							<input id="cIdTipoProcedimiento" name="cIdTipoProcedimiento" type="hidden" size="4" value="<%= cIdTipoProcedimiento %>">
							<input id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden" size="3" value="<%= cIdUnidadEjecutora %>">
							<input id="nIdConsecutivo" name="nIdConsecutivo" type="hidden" size="4" value="<%= nIdConsecutivo %>">
							<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
							<input name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" type="hidden"/>
							<input name="usuarioRoleProcedimiento" id="usuarioRoleProcedimiento" type="hidden"/>
							  
							  
							<input name="motivo" id="motivo" type="hidden"/>
							<input name="solicitudEspera" id="solicitudEspera" type="hidden"/>
							<input name="idCaso" id="idCaso" type="hidden"/>
							<input name="enEspera" id="enEspera" type="hidden"/>
							<input name="nFolioPreCompromiso" id="nFolioPreCompromiso" type="hidden"/>
							<input name="consecutivoPrecompromiso" id="consecutivoPrecompromiso" type="hidden"/>
							
							
							<!-- habilitar pestanas -->
							<input name="nIdEstado" id="nIdEstado" type="hidden"/>
							<input name="cEventoFlujo" id="cEventoFlujo" type="hidden"/>
							<input name="documentoAplicado" id="documentoAplicado" type="hidden"/>
							<input name="existePrecompromiso" id="existePrecompromiso" type="hidden"/>
							<input id="tipoProceso" name="tipoProceso" type="hidden" size="10">
							<!-- bitacora -->
							<input id="cIdDocumento" name="cIdDocumento" type="hidden" size="4" >
							<input id="cAccion" name="cAccion" type="hidden" size="4">
							<input id="cIdUsuario" name="cIdUsuario" type="hidden" size="4" ">
							
							
							
						</td>
					</tr>
				</table>
				<table>
					<tr>
						<td align="left">Fecha de vencimiento del PreCompromiso:</td>
						<td>
							<input type="text" id="lblFechaVencimiento" name="lblFechaVencimiento" readonly style="border-width:0; background-color:transparent"/>
						</td>
					</tr>
					<tr>
						<td align="right">Nueva fecha:</td>
						<td align="left">
							<input type="text" id="nuevaFecha" name="nuevaFecha"/>
						</td>
					</tr>
				</table>	
				<div id="adjuntoFieldset" >
					<form name="upform" id="upform" action="../../servlet/ProcedimientoServlet"  enctype="multipart/form-data" method="POST"> 
						<table>
							<tr>
								<td align="right">Elegir archivo:</td>
								<td align="left">
									<input id="uploadfile" name="uploadfile" type="file" >
								</td>
							</tr>
							<tr>
								<td id="btnEnviar" align="right">
									<input type="button" name="nombre" id="nombre" value="Solicitar extensión de vigencia"  onClick="fileUpload(this.form,'upload'); return false;" class="btnInterfaceBG ui-button ui-corner-all">
									 
								</td>
							</tr>
						</table>
						<div id="upload"></div>
					</form>
				</div>					
			</fieldset>
	</body>
</html>
