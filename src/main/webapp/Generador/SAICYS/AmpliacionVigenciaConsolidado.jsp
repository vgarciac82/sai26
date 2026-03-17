<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
	Map rol =usuario.getRoles();
	String cEjercicio = "";
	String cIdTipoConsolidado = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	
	if (session.getAttribute(GestionInterface.ATT_ConEjercicio) != null) {
		cEjercicio=(String)session.getAttribute(GestionInterface.ATT_ConEjercicio);
		System.out.println("*******************cEjercicio********************"+cEjercicio);
		cIdTipoConsolidado=(String)session.getAttribute(GestionInterface.ATT_ConTipoConsolidado);
		cIdUnidadEjecutora=(String)session.getAttribute(GestionInterface.ATT_ConUnidadEjec);
		nIdConsecutivo=(String)session.getAttribute(GestionInterface.ATT_ConConsecutivo);
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
			$("#tbs").val(8);
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
			Map botones=nb.getBotones(roles,"Consolidado","ampliacionVigenciaConsolidado");
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
		$("#cEjercicio2").val('<%=cEjercicio%>');
		
		$("#cIdConsolidado").val($("#cIdTipoConsolidado").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());
		queryFormPost("mConsolidadoCaratulaRead",{async : false});

		//alert("cEjercicio :"+$("#cEjercicio").val()+" cIdTipoConsolidado: "+$("#cIdTipoConsolidado").val() +"\n cIdUnidadEjecutora: "+$("#cIdUnidadEjecutora").val()+" nIdConsecutivo :"+$("#nIdConsecutivo").val()+" nIdAlcance: "+$("#nIdAlcance").val());//cEjercicio,cIdTipoConsolidado,cIdUnidadEjecutora,nIdConsecutivo,nIdAlcance
		queryFormPost("mConsolidadoRead2", {async : false});
		//alert(0);				
		queryFormPost("vigenciaConsolidado2",{async:false});
		agregaFechas();
		habilitaPestanas();
		//alert(1);
		queryFormPost("obtieneIdCaso2",{async:false});
		//alert(2);
		document.getElementById("lblConsolidado").style.readonly=true;
		document.getElementById("lblUnidadEjecutora").style.readonly=true;
		document.getElementById("lblDescripcion").style.readonly=true;
		document.getElementById("lblEstado").style.readonly=true;
		
		$("#extensionVigencia").click(solicitarExtension );
				
		$("#cIdConsolidadof").val($("#cIdConsolidado").val());
		$("#cEstadof").val($("#cEstado").val());
				
		querySelectPost("UnidadEjecutoraBuscaRead", "cUnidadEjecutoraRMC", {async : false});
			
		queryFormPost("mConsolidado_LabelRead2", { async:false });
								
		queryFormPost("mConsolidado_EstadoConsolidadoRead2", {async:false});
		//alert(3);
		queryFormPost("cg_roleRead", { async:false });
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
			
		function validarCampoDescp(a){
			var v = document.getElementById('desConsol').value.replace("ñ", "n").replace("Ñ", "N");					
				document.getElementById('desConsol').value = v;				
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
			alert("Datos");
			var miforma = document.getElementById("upform");
			miforma.action = "../../servlet/ConsolidadoServlet?nFolioPrecompromiso="+$("#nFolioPreCompromiso").val()+"&nuevaFecha="+$("#nuevaFecha").val();
			var datos = "";
     				datos.append('messageData', $('upform').serialize());
      			var filesList = document.getElementById('uploadfile');
      			
      			for (var i = 0; i < filesList.files.length; i ++) {
          			datos.append('file', filesList.files[i]);
			}
			
			$.ajax({
				url: '../../servlet/ConsolidadoServlet?nFolioPrecompromiso='+$("#nFolioPreCompromiso").val()+"&nuevaFecha="+$("#nuevaFecha").val(), 					
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
					
					//Bitácora
					$("#cAccion").val("AMPLIACION_VIGENCIA_PRECOMPROMISO");
					$("#cIdDocumento").val($("#cIdTipoConsolidado").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#n").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
						
					
					//document.getElementById("upform").action = 	"../../servlet/ConsolidadoServlet?nFolioPrecompromiso="+$("#nFolioPreCompromiso").val()+"&nuevaFecha="+$("#nuevaFecha").val();	
					//$("#upform").submit();
					//alert("veamos");
					
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
    // Create the iframe...
    var archivo = $("#uploadfile");
     queryFormPost("vigencias",{async:false});
    // alert($("#enEspera").val());
    if($("#enEspera").val()==0){
		 extensiones_permitidas = new Array(".pdf"); 
		 mierror = ""; 
		 /*
		 alert("arch1: "+!archivo);
		 alert("arch2: "+ archivo == true);
		 alert("fecha: "+ $("#nuevaFecha").val()=='');
		 */
		 // && $("#nuevaFecha").val()==''
		 if ( $("#uploadfile").val() == "" || $("#nuevaFecha").val()=='') { 
		      //Si no tengo archivo, es que no se ha seleccionado un archivo en el formulario  
		     alert("Faltan datos por ingresar");
		 } else{ 
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
  						form.setAttribute("action", "../../servlet/ConsolidadoServlet?nFolioPrecompromiso="+$("#nFolioPreCompromiso").val()+"&nuevaFecha="+$("#nuevaFecha").val()+"&idCaso="+$("#idCaso").val());
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
					$("#cIdDocumento").val($("#cIdTipoConsolidado").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
					
		         /***************************/
		      	} 
	   } 
	 }else{
	 	alert("El consolidado tiene una solicitud de ampliación pendiente");
	 	
	 }
    
}

	function habilitaPestanas(){
		queryFormPost("mValidaPrecompromiso",{async:false});
		if(esRegularizacion()){
			$("#presupuestoConsolidado").css("display", "none");
			$("#preCompromisoConsolidado").css("display", "none");	
			$("#ampliacionPrecompromiso").css("display", "none");
			$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
		}else{
			queryFormPost("fn_mConsolidadoTieneProcedimientoRead", { async:false });
			if($("#tieneProcedimiento").val() == '' || $("#tieneProcedimiento").val() == 'N/A')
				$("#tieneProcedimiento").val('0');
			if ($("#nIdEstado").val()==2 && $("#tieneProcedimiento").val()==0 ){ // validar que el consolidado este aprobado pero que no tenga un procedimiento
				if($("#documentoAplicado").val()==0){ // precompromiso cancelado
					$("#presupuestoConsolidado").css("display", "block");
					$("#preCompromisoConsolidado").css("display", "block");
					$("#ampliacionPrecompromiso").css("display", "none");	
					$("#vigenciaRequisicionesConsolidadas").css("display", "none");					
				}else{ //aplicado contablemente
					//validar que el documento sea de un flujo normal apartado/prmt
					queryFormPost("validaFlujo", {async:false});
					if($("#cEventoFlujo").val()=="DISP_PRECOMMAT"){
						$("#ampliacionPrecompromiso").css("display", "block");
						$("#presupuestoConsolidado").css("display", "block");
						$("#preCompromisoConsolidado").css("display", "block");
						$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
					}else{
						$("#ampliacionPrecompromiso").css("display", "block");
						$("#presupuestoConsolidado").css("display", "none");
						$("#preCompromisoConsolidado").css("display", "none");
						$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
					}	
				}
			
			}else{ // esta aprobado y tiene procedimiento 
				if($("#documentoAplicado").val()==0){ // precompromiso cancelado
					$("#presupuestoConsolidado").css("display", "none");
					$("#preCompromisoConsolidado").css("display", "none");
					$("#ampliacionPrecompromiso").css("display", "none");	
					$("#vigenciaRequisicionesConsolidadas").css("display", "none");					
				}else{ //aplicado contablemente
					/* $("#ampliacionPrecompromiso").css("display", "block");
					$("#presupuestoConsolidado").css("display", "none");
					$("#preCompromisoConsolidado").css("display", "none");
					$("#vigenciaRequisicionesConsolidadas").css("display", "none");	 */
					queryFormPost("validaFlujo", {async:false});
					if($("#cEventoFlujo").val()=="DISP_PRECOMMAT"){
						$("#ampliacionPrecompromiso").css("display", "block");
						$("#presupuestoConsolidado").css("display", "block");
						$("#preCompromisoConsolidado").css("display", "block");
						$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
					}else{
						$("#ampliacionPrecompromiso").css("display", "block");
						$("#presupuestoConsolidado").css("display", "none");
						$("#preCompromisoConsolidado").css("display", "none");
						$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
					}
				}

			}
			if($("#nIdEstado").val()==1 ){
				$("#presupuestoConsolidado").css("display", "none");
				$("#preCompromisoConsolidado").css("display", "none");	
				$("#ampliacionPrecompromiso").css("display", "none");
				queryFormPost("numPartidasConsolidadasRead",{async : false});
				if($("#numLineasConsolidadas").val()!=0){
					$("#vigenciaRequisicionesConsolidadas").css("display", "block");	
				}else{
					$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
				}
			}
		}
	}
	function esRegularizacion(){
		var regularizacion=false;
		var apartables = [ "CC", "CO", "CS", "CA" ];
		if ($.inArray($("#cIdTipoConsolidado").val(), apartables) == -1){ // no traen apartado
			regularizacion=true;
		}else
			regularizacion=false;
		return regularizacion;
	}
	</script>
	</head>
	<body id="dt_example" >
			<fieldset>
				<legend>Información del Consolidado</legend>
				<table align="left" cellpadding="2" width="100%">
			    	<tr>
			    		<td align="right" colspan="2">
                            <img id="imgSalir" src="../imagenes/cancel.png" style="cursor: pointer"  onclick="window.location = 'Consolidado.jsp?tab=2&ses=0';"/> Salir
                       	</td>
			    	</tr>
			    	<tr>
				    	<td align="left" colspan="2"><input type="text" style="width: 700px;border-width:0; background-color:transparent" id="lblConsolidado" name="lblConsolidado" readonly /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 700px;border-width:0; background-color:transparent" name="lblDescripcion" id="lblDescripcion" readonly /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 700px;border-width:0; background-color:transparent" name="lblUnidadEjecutora" id="lblUnidadEjecutora" readonly/></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 700px;border-width:0; background-color:transparent" name="lblEstado" id="lblEstado" readonly /></td>
			    	</tr>
			    	<tr id="lblRechazo" style="display: none">
			    		<td align="left" colspan="2"><input type="text" style="color:red; width: 500px;border-width:0; background-color:transparent" name="lblMotivoRechazo" id="lblMotivoRechazo" readonly /></td>
			    	</tr>
			    </table>
			</fieldset>
			<fieldset>
  				<legend>Ampliación de vigencia</legend>
				<table border="0" width="100%">
					<tr >
						<td colspan="2">
						
							<input id="cIdDocumento" name="cIdDocumento" type="hidden" size="4" >
							<input id="cAccion" name="cAccion" type="hidden" size="4" ">
							<input id="cEjercicio" name="cEjercicio" type="hidden" size="4" value="<%= cEjercicio %>">
							<input id="cEjercicio2" name="cEjercicio2" type="hidden">
							<input id="cIdTipoConsolidado" name="cIdTipoConsolidado" type="hidden" size="4" value="<%= cIdTipoConsolidado %>">
							<input id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden" size="3" value="<%= cIdUnidadEjecutora %>">
							<input id="nIdConsecutivo" name="nIdConsecutivo" type="hidden" size="4" value="<%= nIdConsecutivo %>">
							<input id="cEstado" name="cEstado" type="hidden" />
							<input id="nIdAlcance" name="nIdAlcance" type="hidden" size="2">
							<input type="hidden" id="imgEstado" name="imgEstado"/>
							
							<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%=usuario.getU_UR()%>" />
							<input type="hidden" name="cIdUnidadEjecutoraConsolidado" id="cIdUnidadEjecutoraConsolidado" value="<%=cIdUnidadEjecutora%>" />
							<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
							
							<input type="hidden" name="R_NOMBRE" id="R_NOMBRE" />
							<input name="cIdConsolidado" id="cIdConsolidado" type="hidden"/>
							<input name="nFolioPreCompromiso" id="nFolioPreCompromiso" type="hidden"/>
							<input name="consecutivoPrecompromiso" id="consecutivoPrecompromiso" type="hidden"/>
							<input name="enEspera" id="enEspera" type="hidden"/>
							<input name="cAccion" id="cAccion" type="hidden"/>
							<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuario.getLogin()%>"/>	
							<input name="nIdEstado" id="nIdEstado" type="hidden"/>
							<input name="idCaso" id="idCaso" type="hidden"/>
							<input name="motivo" id="motivo" type="hidden"/>
							<input name="solicitudEspera" id="solicitudEspera" type="hidden"/>
							
							<!-- habilitar pestanas -->
							<input type="hidden" id="numLineasConsolidadas" name="numLineasConsolidadas">
							<input type="hidden" id="tieneProcedimiento" name="tieneProcedimiento">
							<input type="hidden" id="nIdEstado" name="nIdEstado">
							<input type="hidden" id="documentoAplicado" name="documentoAplicado">
							<input type="hidden" id="cEventoFlujo" name="cEventoFlujo">
							
						</td>
					</tr>
				</table>
				<div align="left" >
				<table align="left" >
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
				</div>
				<br /><br /><br />
				<div id="adjuntoFieldset" align="left">
					<form name="upform" id="upform" action="../../servlet/ConsolidadoServlet"  enctype="multipart/form-data" method="POST"> <%--onsubmit="return comprueba_extension2(this.form, this.form.uploadfile.value)"--%> 
						<table align="left">
							<tr>
								<td align="right">Elegir archivo:</td>
								<td align="left">
									<input id="uploadfile" name="uploadfile" type="file" >
								</td>
							</tr>
							<tr>
								<td id="btnEnviar" align="right">
									<input type="button" name="nombre" id="nombre" value="Solicitar extensión de vigencia"  onClick="fileUpload(this.form,'upload'); return false;" class="btnInterfaceBG ui-button ui-corner-all" />
								</td>
							</tr>
						</table>
						<div id="upload"></div>
					</form>
				</div>					
			</fieldset>
	</body>
</html>