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
		
		<script type="text/javascript" charset="utf-8">
			
			
			var roles='';
			$(document).ready(function() {
				$("#tbs").val(3);
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
				Map botones=nb.getBotones(roles,"Consolidado","CaratulaConsolidado");
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
				roles="<%=roles%>";
				$("#btnGuardarConsolCaratula").button().click(function(){
					queryFormPost("sp_mConsolidadoModificaConsolidadoCreate",{async : false});
					window.location = "Consolidado.jsp?tab=4";
  				});
				$("#cIdConsolidado").val($("#cIdTipoConsolidado").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());
				queryFormPost("mConsolidadoCaratulaRead",{async : false});				
				queryFormPost("mConsolidadoRead", {async : false});				
				queryFormPost("fn_mConsolidadoCalculaMontoRead", {async : false});
				queryFormPost("fn_mConsolidadoCalculaMontoBrutoRead", {async : false});	
				queryFormPost("fn_mConsolidadoTieneProcedimientoRead", {async : false});
				queryFormPost("numPartidasConsolidadasRead",{async : false});
				queryFormPost("mValidaPrecompromiso",{async:false});
				if(esRegularizacion()){
					document.getElementById('trBotones').style.display = 'none';
					document.getElementById('trSalir').style.display = 'none';
					
				}else{
					document.getElementById('trBotones').style.display = 'none';
					document.getElementById('trSalir').style.display = 'block';
				}
				habilitaPestanas();

				document.getElementById("lblConsolidado").style.readonly=true;
				document.getElementById("lblUnidadEjecutora").style.readonly=true;
				document.getElementById("lblDescripcion").style.readonly=true;
				document.getElementById("lblEstado").style.readonly=true;
				
				if($("#tieneProcedimiento").val() == '' ||$("#tieneProcedimiento").val() == 0)
					$("#tieneProcedimiento").val('N/A');
				if($("#tienePedidos").val() == '')
					$("#tienePedidos").val('0');							
				$("#montoTotal").formatCurrency();
				$("#MontoBruto").formatCurrency();		
				
				$("#cIdConsolidadof").val($("#cIdConsolidado").val());
				$("#cEstadof").val($("#cEstado").val());
				
				querySelectPost("UnidadEjecutoraBuscaRead", "cUnidadEjecutoraRMC", {async : false});
				
				queryFormPost("mConsolidado_LabelRead", { async:false });					
				queryFormPost("mConsolidado_EstadoConsolidadoRead", {async:false});
				queryFormPost("cg_roleRead", { async:false });
				queryFormPost("mSolicitudDescripcionUnidad", { async:false });
				queryFormPost("mUsuarioMismaUE", {async: false   });
				if (!(roles.indexOf("ADMIN_RECMAT")>=0 || $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val())) {
					document.getElementById("imgAprobarCaratula").disabled = true;
					document.getElementById("imgDevolverCaratula").disabled = true;
					document.getElementById("imgAnularCaratula").disabled = true;
				}
				else if ($("#nIdEstadoCon").val() == "1") { //capturada
					document.getElementById("imgAprobarCaratula").disabled = false;
					document.getElementById("imgDevolverCaratula").disabled = true;
					document.getElementById("imgAnularCaratula").disabled = false;
				}
				else if ($("#nIdEstadoCon").val() == "2") { //Aprobada
					document.getElementById("imgAprobarCaratula").disabled = true;
					document.getElementById("imgDevolverCaratula").disabled = false;
					document.getElementById("imgAnularCaratula").disabled = false;
				} 
				else if ($("#nIdEstadoCon").val() == "3") { //anulada
					document.getElementById("imgAprobarCaratula").disabled = true;
					document.getElementById("imgDevolverCaratula").disabled = true;
					document.getElementById("imgAnularCaratula").disabled = true;
				}
				
				if (!(roles.indexOf("ADMIN_RECMAT")>=0||roles.indexOf("ANALISTA")>=0||roles.indexOf("JEFE")>=0||parseInt($("#usuariosMismaUE").val(),10)==1 || $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val()))
					deshabilitarCampos();
				else if ($("#nIdEstadoCon").val() == "2" || $("#nIdEstadoCon").val() == "3")  //aprobada
					deshabilitarCampos();
			});
			
			function deshabilitarCampos() {
				document.getElementById("desConsol").setAttribute("disabled", "disabled");
				document.getElementById("notasConsol").setAttribute("disabled", "disabled");
				//document.getElementById("btnGuardarConsolCaratula").setAttribute("disabled", "disabled");
				$("#btnGuardarConsolCaratula").hide();
			}
			
			function cargaSolicitudesSel(){	
				$('#tblSolicitudPreSel').dataTable().fnClearTable();
              	var szWhere = "";
				var campos = "";  					
				campos = $("#cIdConsolidado").val();					
                var szTabla = "CONSOLIDADOPRESOLICITUDESSELECCIONADAS";                                                                                         
				arrayCompleto=new Array();
				$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, Campos:campos, Order:" order by 1 desc ", ajax: 'false'}, function(j){
					for (var i = 0; i < j.length; i++) 
   					{  
   					    arrayCompleto [i]=[j[i].Col0,j[i].Col1,j[i].Col2];   
					}
					$('#tblSolicitudPreSel').dataTable().fnAddData(arrayCompleto);
		        });
		        queryFormPost("numSolDispCreate", {async : false});
				queryFormPost("numSolAgregadasCreate", {async : false});
			}
						
			function cargaSolicitudesDisppresel(){
				$('#tblSolicitudDispPreSel').dataTable().fnClearTable();
              	var szWhere = "";
				var campos = "";  					
				campos = $("#cAlcanceC").val()+"','"+$("#cIdTipoConsolidado").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#cIdConsolidado").val();					
                var szTabla = "SOLICITUDESDISPPRESEL";                                                                                         
				arrayCompleto=new Array();
				$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, Campos:campos, Order:" order by 1 desc ", ajax: 'false'}, function(j){
					for (var i = 0; i < j.length; i++) 
	  				{  
	  				    arrayCompleto [i]=[j[i].Col0,j[i].Col1,j[i].Col2];  
					}
					$('#tblSolicitudDispPreSel').dataTable().fnAddData(arrayCompleto);
	         	});
		     	queryFormPost("numSolDispCreate", {async : false});
				queryFormPost("numSolAgregadasCreate", {async : false});
			}
				
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
						swal("El consolidado "+$("#cIdConsolidado").val() +" ha sido aprobado.",{icon:"info",button: "Cerrar"});
						window.location = "Consolidado.jsp?tab=3";
					}
					else
						swal("Necesita agregar al menos una línea para aprobar el Consolidado.",{icon:"info",button: "Cerrar"});
				}else{
					swal("No tiene permisos para realizar esta acción.",{icon:"info",button: "Cerrar"});
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
						swal("El Consolidado "+$("#cIdConsolidado").val()+" está asociado a un Procedimiento. Para devolverlo, primero es necesario declarar desierto el Procedimiento.",{icon:"info",button: "Cerrar"});						
				}else{
					swal("No tiene permisos para realizar esta acción.",{icon:"info",button: "Cerrar"});
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
						swal("El Consolidado "+$("#cIdConsolidado").val()+" está asociado a un Procedimiento. Para devolverlo, primero es necesario declarar desierto el Procedimiento.",{icon:"info",button: "Cerrar"});
				}else{
					swal("No tiene permisos para realizar esta acción.",{icon:"info",button: "Cerrar"});
				}
			}
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
			
	function habilitaPestanas(){
		queryFormPost("mValidaPrecompromiso",{async:false});
		queryFormPost("cg_roleRead", { async:false });
		if(esRegularizacion()){
			$("#presupuestoConsolidado").css("display", "none");
			$("#preCompromisoConsolidado").css("display", "none");	
			$("#ampliacionPrecompromiso").css("display", "none");
			$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
		}else{
			if($("#tieneProcedimiento").val() == '' || $("#tieneProcedimiento").val() == 'N/A')
				$("#tieneProcedimiento").val('0');
			if ($("#nIdEstado").val()==2 && $("#tieneProcedimiento").val()==0 ){ // validar que el consolidado este aprobado pero que no tenga un procedimiento
				if($("#documentoAplicado").val()==0){ // precompromiso cancelado
					$("#presupuestoConsolidado").css("display", "block");
							$("#preCompromisoConsolidado").css("display", "block");
					$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
					$("#ampliacionPrecompromiso").css("display", "none");
			
				}else{ //aplicado contablemente
					//validar que el documento sea de un flujo normal apartado/prmt
					queryFormPost("validaFlujo", {async:false});
					if($("#cEventoFlujo").val()=="DISP_PRECOMMAT"){
						if (roles.indexOf("ADMIN_RECMAT")>=0){
							if($("#cIdUsuarioCreacion").val()==$("#U_LOGIN").val()){
								$("#presupuestoConsolidado").css("display", "block");
								$("#preCompromisoConsolidado").css("display", "block");
							}else{
								$("#presupuestoConsolidado").css("display", "none");
								$("#preCompromisoConsolidado").css("display", "none");
							}
						}else{
								$("#presupuestoConsolidado").css("display", "block");
								$("#preCompromisoConsolidado").css("display", "block");
						}	
						$("#ampliacionPrecompromiso").css("display", "block");
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
					$("#ampliacionPrecompromiso").css("display", "block");
					$("#presupuestoConsolidado").css("display", "none");
					$("#preCompromisoConsolidado").css("display", "none");
					$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
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
		var apartables = [ "CC", "CO", "CS", "CA", "CT" ];
		if ($.inArray($("#cIdTipoConsolidado").val(), apartables) == -1){ // no traen apartado
			regularizacion=true;
		}else
			regularizacion=false;
		return regularizacion;
	}
		</script>
	</head>
	<body id="dt_example" >
		<form>
			<fieldset>
				<legend>Información del Consolidado</legend>
				<table align="left" cellpadding="2" width="100%">
					<tr id="trBotones" style='display:none'>						
						<td align="right" colspan="2">
                        	<img id="imgAprobarCaratula" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="aprobarConsolidado();"/> Aprobar 
                            <img id="imgDevolverCaratula" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer" onclick="devolverConsolidado();" /> Devolver
                            <img id="imgAnularCaratula" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="anularConsolidado();" /> Anular
                            <img id="imgSalir" src="../imagenes/cancel.png" style="cursor: pointer"  onclick="window.location = 'Consolidado.jsp?tab=2&ses=0';"/> Salir
                       	</td>
					</tr>
					<tr id="trSalir" style='display:none' align="right">
						<td align="right" colspan="2"  >
							 <input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	onclick="window.location = 'Consolidado.jsp?tab=2&ses=0';" />&nbsp;&nbsp;
						</td>
					</tr>
					<tr>
				    	<td align="left" colspan="2">
				    		<input type="text" style="width: 25px;border-width:0; background-color:transparent" id="lblUnidadUsuario" name="lblUnidadUsuario" readonly value="<%=unidadUsuarioLogeado%>"/>
				    		<input type="text" style="width: 690px;border-width:0; background-color:transparent" id="lblDescUsuario" name="lblDescUsuario" readonly />
				    	</td>
			    	</tr>
			    	<tr>
				    	<td align="left" colspan="2"><input type="text" style="width: 80px;border-width:0; background-color:transparent" id="lblConsolidado" name="lblConsolidado" readonly />
				    	<input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblDescripcion" id="lblDescripcion" readonly /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2">
			    			<input type="text" style="width: 700px;border-width:0; background-color:transparent" name="lblUnidadEjecutora" id="lblUnidadEjecutora" readonly />
			    		</td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2">
			    			<input type="text" style="width: 700px;border-width:0; background-color:transparent" name="lblEstado" id="lblEstado" readonly />
			    		</td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2">
			    		
			    		</td>
			    	</tr>
			    </table>
			</fieldset>
			<fieldset>
  				<legend>Edici&oacute;n de car&aacute;tula</legend>
				<table border="0" width="100%">
					<tr>
						<td>
							<input id="cEjercicio" name="cEjercicio" type="hidden" size="4" value="<%= cEjercicio %>">
							<input id="cIdTipoConsolidado" name="cIdTipoConsolidado" type="hidden" size="4" value="<%= cIdTipoConsolidado %>">
							<input id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden" size="3" value="<%= cIdUnidadEjecutora %>">
							<input id="nIdConsecutivo" name="nIdConsecutivo" type="hidden" size="4" value="<%= nIdConsecutivo %>">
							<input id="cEstado" name="cEstado" type="hidden" />
							
							<input id="cDescripcion" name="cDescripcion" type="hidden" size="2000">
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
							
							<!-- habilitar pestanas -->
							<input name="cEventoFlujo" id="cEventoFlujo" type="hidden"/>
							<input name="documentoAplicado" id="documentoAplicado" type="hidden"/>
							<input name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" type="hidden"/>
							<input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE" value=""/>
							
						</td>
					</tr>
					<tr>
						<td>Tipo Consolidado:</td>
						<td colspan="2" align="left"><input name="cTipoConsolidado" id="cTipoConsolidado" type="text" style="width: 550px;" disabled="disabled" ></td>
					</tr>
					<tr>
						<td>Unidad Ejecutora:</td>
						<td colspan="2" align="left"><input name="unidadEjecutora" id="unidadEjecutora" type="text" style="width: 550px;" disabled="disabled"></td>
					</tr>
					<tr>
						<td>Alcance:</td>
						<td colspan="2" align="left"><input name="cAlcance" id="cAlcance" type="text" style="width: 550px;" disabled="disabled"></td>
					</tr>
					<tr>
						<td>N&uacute;mero:</td>
						<td colspan="2" align="left"><input name="cIdConsolidadof" id="cIdConsolidadof" type="text" style="width: 550px;" disabled="disabled"></td>
					</tr>
					<tr>
						<td>Estado:</td>
						<td colspan="2" align="left"><input name="cEstadof" id="cEstadof" type="text" style="width: 550px;" disabled="disabled"></td>
					</tr>
					<tr>
						<td align="right">Monto Bruto:</td>
						<td colspan="2" align="left"><input name="MontoBruto" id="MontoBruto" type="text" style="width: 550px;" disabled="disabled"></td>
					</tr>
					<tr>
						<td align="right">Monto Total:</td>
						<td colspan="2" align="left"><input name="montoTotal" id="montoTotal" type="text" style="width: 550px;" disabled="disabled"></td>
					</tr>
					<tr>
						<td align="right">Procedimiento</td>
						<td colspan="2" align="left"><input name="tieneProcedimiento" id="tieneProcedimiento" type="text" style="width: 550px;" disabled="disabled"></td>
					</tr>
					<tr>
						<td align="right">Pedidos  </td>
						<td colspan="2" align="left"><input name="tienePedidos" id="tienePedidos" type="text" style="width: 550px;" disabled="disabled"></td>
					</tr>
					<tr>
						<td align="right">#PC</td>
						<td colspan="2" align="left">
							<input name="numLineasConsolidadas" id="numLineasConsolidadas" type="text" style="width: 550px;" disabled="disabled">
						</td>
					</tr>
					<tr>
						<td>Descripci&oacute;n:</td>
						<td colspan="2" align="left"><textarea name="desConsol" id="desConsol" cols="68" rows="6" onkeypress="textCounter(this,2000);" ></textarea></td>
					</tr>
					<tr>
						<td>Notas:</td>
						<td colspan="2" align="left"><textarea name="notasConsol" id="notasConsol" cols="68" rows="2" onkeypress="textCounter(this,255);return validar(event)" ></textarea></td>
					</tr>
					<tr>
						<td align="center" colspan="3">
							 <input type="button" name="btnGuardarConsolCaratula" id="btnGuardarConsolCaratula" value="Guardar"  class="btnInterfaceBG ui-button ui-corner-all"/>
						</td>
					</tr>
				</table>	
			</fieldset>
		</form>				
	</body>
</html>
