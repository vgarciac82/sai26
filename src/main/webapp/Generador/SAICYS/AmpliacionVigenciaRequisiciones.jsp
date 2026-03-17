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
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ConEjercicio);
		cIdTipoConsolidado = (String)session.getAttribute(GestionInterface.ATT_ConTipoConsolidado);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ConUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ConConsecutivo);
	}
	String unidadUsuarioLogeado="";
	unidadUsuarioLogeado = usuario.getU_UR();
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
		<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />
		<style type="text/css" title="currentStyle">
				@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
				@import "../css/demo_table_jui.css";
				@import "../css/demo_page.css";
		</style>
		<link href="../../css/interfaz.css" rel="stylesheet" type="text/css" />

		<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.core"></script>
		<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
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
				Map botones=nb.getBotones(roles,"Consolidado","ampliacionVigenciasConsolidadas");
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
								
				$("#cIdConsolidado").val($("#cIdTipoConsolidado").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());
				queryFormPost("mConsolidadoCaratulaRead",{async : false});				
				queryFormPost("mConsolidadoRead", {async : false});
				queryFormPost("mConsolidado_LabelRead", {async : false});					
				queryFormPost("mSolicitudDescripcionUnidad", { async:false });
				
				document.getElementById("lblConsolidado").style.readonly=true;
				document.getElementById("lblUnidadEjecutora").style.readonly=true;
				document.getElementById("lblDescripcion").style.readonly=true;
				document.getElementById("lblEstado").style.readonly=true;
			
				querySelectPost("UnidadEjecutoraBuscaRead", "cUnidadEjecutoraRMC", {async : false});
				
				queryFormPost("buscaFolioCaso",{async:false});
			
				queryFormPost("cg_roleRead", { async:false });
				if (!($("#R_NOMBRE").val().toString().indexOf("ADMIN_RECMAT")>=0 || $("#cIdUnidadEjecutoraUsuario").val() == $("#cIdUnidadEjecutora").val())) {
					document.getElementById("imgAprobarCaratula").disabled = true;
					document.getElementById("imgDevolverCaratula").disabled = true;
					document.getElementById("imgAnularCaratula").disabled = true;
				}
				else if ($("#nIdEstadoCon").val() == "1") { //capturada
					document.getElementById("imgAprobarCaratula").disabled = false;
					document.getElementById("imgDevolverCaratula").disabled = true;
					document.getElementById("imgAnularCaratula").disabled = false;
				}
				///////////////////////////////////////////////
				
				queryFormPost("ValidaVentanilla", { async:false });
				if ($("#enVentanilla").val()=='1'){
					//muestra el label
					$("#lblAviso").val("Se encuentra una ampliación de vigencia pendiente de su aprobación");
					document.getElementById("trAviso").style.display="block";
					document.getElementById("nombre").style.display="none";
				}else{
					$("#lblAviso").val("");
					// si fue rechazada mostrar el mensaje
					queryFormPost("avisoConsolidado", {async:false});
					if($.trim($("#lblAviso").val()).length == 0 ){
						$("#trAviso").hide();
					}else{
						document.getElementById("trAviso").style.display="block";
						document.getElementById("nombre").style.display="block";
					}
					
					
				}
			
				
				muestraSolicitudes();
				habilitaPestanas();
			});
				
			function aprobarConsolidado() {
				var imgAprobar='<%=imgAprobar%>';
				if (imgAprobar==0){
					queryFormPost("numLinTotalRead",{async : false});
					if ($("#numLinTot").val() > 0) {
						$("#nIdEstado").val("2"); //aprobada
						queryFormPost("mConsolidadoUpdate", { async:false });
						
							//Bitácora
						$("#cAccion").val("APRUEBA_CONSOLIDADO");
						$("#cIdDocumento").val($("#cIdConsolidado").val());
						queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
						
						alert("El consolidado "+$("#cIdConsolidado").val() +" ha sido aprobado.");
						window.location = "Consolidado.jsp?tab=3";
					}
					else
						alert("Necesita agregar al menos una línea para aprobar el Consolidado.");
				}else{
					alert("No tiene permisos para realizar esta accion");
				}
			}
			
			function devolverConsolidado () {
				var imgDevolver='<%=imgDevolver%>';
				if (imgDevolver==0){
					queryFormPost("fn_mConsolidadoTieneProcedimientoRead", { async:false });
					if($("#tieneProcedimiento").val() == '' || $("#tieneProcedimiento").val() == 'N/A')
						$("#tieneProcedimiento").val('0');
					if ($("#tieneProcedimiento").val() == "0") {
						$("#nIdEstado").val("1"); //capturada
						queryFormPost("mConsolidadoUpdate", { async:false });
						//Bitácora
						$("#cAccion").val("DEVUELVE_CONSOLIDADO");
						$("#cIdDocumento").val($("#cIdConsolidado").val());
						queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
						window.location = "Consolidado.jsp?tab=3";
					}
					else
						alert("El Consolidado "+$("#cIdConsolidado").val()+" está asociado a un Procedimiento. Para devolverlo, primero es necesario eliminarlo del Procedimiento.");
				}else{
					alert("No tiene permisos para realizar esta accion");
				}
			}
			
			function anularConsolidado () {
				var imgAnular='<%=imgAnular%>';
				if (imgAnular==0){
					queryFormPost("fn_mConsolidadoTieneProcedimientoRead", { async:false });
					if($("#tieneProcedimiento").val() == '' || $("#tieneProcedimiento").val() == 'N/A')
						$("#tieneProcedimiento").val('0');
				
					if ($("#tieneProcedimiento").val() == "0"){ 
						
						if (confirm("¿Está seguro que desea anular el Consolidado "+ $("#cIdConsolidado").val() +" ? \n Esta acción no puede revertirse y todas sus lineas serán liberadas.")) {
							$("#nIdEstado").val("3"); //anulada
							queryFormPost("mConsolidadoUpdate", { async:false });
							queryFormPost("sp_mConsolidadoAnulaConsolidado", {async:false});
							
							//Bitácora
							$("#cAccion").val("ANULA_CONSOLIDADO");
							$("#cIdDocumento").val($("#cIdConsolidado").val());
							queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
								
							window.location = "Consolidado.jsp?tab=3";
						}				
					}else
						alert("El Consolidado "+$("#cIdConsolidado").val()+" está asociado a un Procedimiento. Para devolverlo, primero es necesario eliminarlo del Procedimiento.");
				}else{
					alert("No tiene permisos para realizar esta accion");
				}
			}
			
			function muestraSolicitudes(){
				var campos = "";  					
				campos = $("#cIdConsolidado").val();
				$('#tblResumenpartidas').dataTable().fnClearTable();
				$('#tblResumenpartidas').dataTable({         
			 				 sScrollX: "100%",
							 sScrollXInner: "97%",
							 bScrollCollapse: true,
							 bDestroy: true,
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
							bAutoWidth: false,
							bProcessing: true,
							sPaginationType: "full_numbers",
							bJQueryUI: true,
							bServerSide: true,
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mRequisicionesConsolidadas('"+campos+"')",
							aaSorting: [[ 0, "asc" ]] ,
							aoColumns: [						 
							{ sName: "cIdSolicitud" },
							{ sName: "cIdSubPartida"},
							{ sName: "cDescripcion"},
							{ sName: "fecha"}
							]      		
					});	
			}
			
	function habilitaPestanas(){
		queryFormPost("mValidaPrecompromiso",{async:false});
		var apartables = [ "CC", "CO", "CS", "CA" ];
		if(esRegularizacion()){
			$("#presupuestoConsolidado").css("display", "none");
			$("#preCompromisoConsolidado").css("display", "none");	
			$("#ampliacionPrecompromiso").css("display", "none");
			$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
		}else{
			if ($("#nIdEstado").val()==2){
				if($("#documentoAplicado").val()==0){ // precompromiso cancelado
					$("#presupuestoConsolidado").css("display", "block");
					$("#preCompromisoConsolidado").css("display", "block");
					$("#ampliacionPrecompromiso").css("display", "none");	
					$("#vigenciaRequisicionesConsolidadas").css("display", "none");					
				}else{ //aplicado contablemente
					$("#ampliacionPrecompromiso").css("display", "block");
					$("#presupuestoConsolidado").css("display", "none");
					$("#preCompromisoConsolidado").css("display", "none");
					$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
				}
			
			}else{
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
	
	function fileUpload(form, div_id) {
	    // Create the iframe...
	    var archivo = $("#uploadfile");
	    
	//    if($("#idCasoAmpliacion").val()!=""){ // hay caso
			 extensiones_permitidas = new Array(".pdf"); 
			 mierror = ""; 
			 if ( $("#uploadfile").val() == "") { 
			      //Si no tengo archivo, es que no se ha seleccionado un archivo en el formulario  
			     alert("Faltan datos por ingresar");
			 } else{ 
			      //recupero la extensión de este nombre de archivo 
			      extension = ($("#uploadfile").val().substring($("#uploadfile").val().lastIndexOf("."))).toLowerCase(); 
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
	
	      					}
	  						if (iframeId.addEventListener) iframeId.addEventListener("load", eventHandler, true);
	  						if (iframeId.attachEvent) iframeId.attachEvent("onload", eventHandler);
	  						// Set properties of form...
	  						form.setAttribute("target", "upload_iframe");
	  						form.setAttribute("action", "../../servlet/ConsolidadoServlet?cEjercicio="+$("#cEjercicio").val()+"&cIdTipoConsolidado="+$("#cIdTipoConsolidado").val()+"&cIdUnidadEjecutora="+$("#cIdUnidadEjecutora").val()+"&nIdConsecutivo="+$("#nIdConsecutivo").val()+"&nFolioAmpliacion="+$("#idCasoAmpliacion").val()+"&requisicion=1");
	  						form.setAttribute("method", "post");
	  						form.setAttribute("enctype", "multipart/form-data");
	  						form.setAttribute("encoding", "multipart/form-data");
	
	  						// Submit the form...
	  						form.submit();
			      		    alert("Enviado para extension de vigencia");
						
						//Bitácora
						$("#cAccion").val("AMPLIACION_VIGENCIA_REQUISICIONES_CONSOLIDADAS");
						$("#cIdDocumento").val($("#cIdTipoConsolidado").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
						queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
						
			         /***************************/
			      	} 
		   } 
		/*  }else{
		 	alert("El consolidado tiene una solicitud de ampliación pendiente");
		 	
		 }
	     */
	}
	
		</script>
	</head>
	<body id="dt_example" >
			<fieldset>
				<legend>Información del Consolidado</legend>
				<table align="left" cellpadding="2" width="100%">
			    	<tr>
				    	<td align="left" colspan="2">
				    		<input type="text" style="width: 25px" id="lblUnidadUsuario" name="lblUnidadUsuario" readonly style="border-width:0; background-color:transparent" value="<%=unidadUsuarioLogeado%>"/><input type="text" style="width: 690px" id="lblDescUsuario" name="lblDescUsuario" readonly style="border-width:0; background-color:transparent"/>
				    	</td>
			    	</tr>
			    	<tr>
				    	<td align="left" colspan="2">
				    		<input type="text" style="width: 80x" id="lblConsolidado" name="lblConsolidado" readonly style="border-width:0; background-color:transparent"/><input type="text" style="width: 600px" name="lblDescripcion" id="lblDescripcion" readonly style="border-width:0; background-color:transparent"/></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2">
			    			<input type="text" style="width: 700px" name="lblUnidadEjecutora" id="lblUnidadEjecutora" readonly style="border-width:0; background-color:transparent"/>
			    		</td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2">
			    			<input type="text" style="width: 700px" name="lblEstado" id="lblEstado" readonly style="border-width:0; background-color:transparent"/>
			    		</td>
			    	</tr>
			    	<tr id="trAviso" style="display: none">
			    		<td align="left" colspan="2"><input type="text" style="color:red; width: 500px;"" name="lblAviso" id="lblAviso" readonly style="border-width:0; background-color:transparent"/></td>
			    	</tr>
			    </table>
			</fieldset>
			<fieldset>
  				<legend>Ampliación de vigencias de requisiciones que cuentan con apartado</legend>
				<table border="0" width="100%">
					<tr>
						<td>
							<input id="cEjercicio" name="cEjercicio" type="hidden" size="4" value="<%= cEjercicio %>">
							<input id="cIdTipoConsolidado" name="cIdTipoConsolidado" type="hidden" size="4" value="<%= cIdTipoConsolidado %>">
							<input id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden" size="3" value="<%= cIdUnidadEjecutora %>">
							<input id="nIdConsecutivo" name="nIdConsecutivo" type="hidden" size="4" value="<%= nIdConsecutivo %>">
							<input id="cEstado" name="cEstado" type="hidden" />
							
							<input id="cDescripcion" name="cDescripcion" type="hidden" size="2000">
							<input id="nIdEstado" name="nIdEstado" type="hidden" size="2">
							<input id="nIdAlcance" name="nIdAlcance" type="hidden" size="2">
							
							<input type="hidden" id="imgEstado" name="imgEstado"/>
							
							<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%=usuario.getU_UR()%>" />
							<input type="hidden" name="cIdUnidadEjecutoraConsolidado" id="cIdUnidadEjecutoraConsolidado" value="<%=cIdUnidadEjecutora%>" />
							<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
							
							<input type="hidden" name="R_NOMBRE" id="R_NOMBRE" />
							<input name="cIdConsolidado" id="cIdConsolidado" type="hidden"/>
							<input id="nIdEstadoCon" name="nIdEstadoCon" type="hidden" size="2"/>
							<input name="numLinTot" id="numLinTot" type="hidden">
							<input name="cAccion" id="cAccion" type="hidden">
							<input name="cIdDocumento" id="cIdDocumento" type="hidden">
							<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuario.getLogin()%>"/>
							<input name="nFolioPrecompromiso" id="nFolioPrecompromiso" type="hidden">
							<input name="consecutivoPrecompromiso" id="consecutivoPrecompromiso" type="hidden"/>
							<input name="documentoAplicado" id="documentoAplicado" type="hidden"/>
							<input name="idCasoAmpliacion" id="idCasoAmpliacion" type="hidden"/>
							<input name="enVentanilla" id="enVentanilla" type="hidden"/>
							
							
						</td>
					</tr>
					<tr>
					<td>
						<table id="tblResumenpartidas" class="display">
				            <thead>
				                <tr>
				                	<th width="5%">Requisición</th>
				                	<th width="10%">Partida</th>
				                	<th>Descripcion</th>
				                	<th  width="10%">Fecha</th>
								</tr>
				            </thead>
				        </table>
				    </td>
					</tr>
				</table>
				<div id="adjuntoFieldset" >
					<form name="upform" id="upform" action="../../servlet/ConsolidadoServlet"  enctype="multipart/form-data" method="POST"> <%--onsubmit="return comprueba_extension2(this.form, this.form.uploadfile.value)"--%> 
						<table>
							<tr>
								<td align="right">Elegir archivo:</td>
								<td align="left">
									<input id="uploadfile" name="uploadfile" type="file" >
								</td>
							</tr>
							<tr>
								<td align="right">
									<input type="button" name="nombre" id="nombre" value="Solicitar extensión de vigencia"  onClick="fileUpload(this.form,'upload'); return false;" >
								</td>
							</tr>
						</table>
						<div id="upload"></div>
					</form>
				</div>			
			</fieldset>				
	</body>
</html>
