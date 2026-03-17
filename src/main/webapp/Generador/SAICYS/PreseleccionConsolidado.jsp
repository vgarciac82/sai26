﻿<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	//String path = request.getContextPath();
	//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
	Map<String, Role> rol =usuario.getRoles();
	
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
		<title>Preselección Consolidado</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Consolidado">
		<script type="text/javascript" charset="utf-8">
			var roles='';
			$(document).ready(function() {
				$("#tbs").val(4);
				showAndHideTabs();
				<%
				int tabla=0;
				int tabla2=0;
				int imgAnular=0;
				int imgAprobar=0;
				int imgDevolver=0;
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator<?> it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry <?,?> r = (Map.Entry <?,?>)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				
				Map<?,?> botonesConsolidado=nb.getBotones(roles,"Consolidado","PreseleccionConsolidado");
				Iterator <?> btnConsolidado = botonesConsolidado.entrySet().iterator();
				while (btnConsolidado.hasNext()) {
					Map.Entry <?,?> bConsolidado = (Map.Entry <?,?>)btnConsolidado.next();
					String img=(String) bConsolidado.getValue();
					if ("imgAnularPreseleccion".equals(img)){
						imgAnular=1;
					}
					if ("imgAprobarPreseleccion".equals(img)){
						imgAprobar=1;
					}
					if ("imgDevolverPreseleccion".equals(img)){
						imgDevolver=1;
					}
					
				}
				
				Map<?,?> botones=nb.getBotones(roles,"Consolidado","PreseleccionRequisiciones");
				Iterator<?> btn = botones.entrySet().iterator();
				System.out.println(botones);
				while (btn.hasNext()) {
					Map.Entry<?,?> b = (Map.Entry<?,?>)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					// validar evento de tabla
					String btnTabla= (String) b.getValue();
					if ("btnSolicitudesDisppreselTodas".equals(btnTabla)){
						 tabla=1;
					}
					if ("btnSolicitudespreselTodas".equals(btnTabla)){
						tabla2=1;
					}
					
				}
				
				Map<?,?> botonesLineas=nb.getBotones(roles,"Consolidado","PreseleccionLineas");
				Iterator<?> btnLineas = botonesLineas.entrySet().iterator();
				while (btnLineas.hasNext()) {
					Map.Entry<?,?> bLineas = (Map.Entry<?,?>)btnLineas.next();%>
					$("#<%=bLineas.getValue()%>").attr("disabled", true);<%
				}

				%>
				
				
				document.getElementById("lblConsolidado").style.readonly=true;
				document.getElementById("lblUnidadEjecutora").style.readonly=true;
				document.getElementById("lblDescripcion").style.readonly=true;
				document.getElementById("lblEstado").style.readonly=true;
				
				$("#cIdConsolidado").val($("#cIdTipoConsolidado").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());				
				$("#nIdConsecutivo").val();
				queryFormPost("mConsolidadoEstadoRead", {async : false});				
				$("#desConsolidadoPre").val($("#cDescripcion").val());
				$("#cEstadof").val($("#cEstado").val());
				$("#cAlcanceC").val($("#nIdAlcance").val());
				roles="<%=roles%>";
				//querySelectPost("UnidadEjecutoraBuscaRead", "cUnidadEjecutoraRMC", {async : false});
				if (roles.indexOf("ADMIN_RECMAT") >= 0){
					$("#isAdmin").val(0);
				} 
				querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cUnidadEjecutoraRMC", {async: false});
				
				querySelectPost("mCatalogoCapituloReadConsolidado", "cCapitulo", {async : false});
				$("#cCapitulo").val("2");
				querySelectPost("mCatalogoSubPartidaConsolRead", "partida", {async : true});														
				
				queryFormPost("mConsolidado_LabelRead", { async:false });					
				queryFormPost("mConsolidado_EstadoConsolidadoRead", {async:false});
				queryFormPost("cg_roleRead", { async:false });
				queryFormPost("mSolicitudDescripcionUnidad", { async:false });
				
				//es de regularización
				queryFormPost("mConsolidadoRegularizacion", { async:false });	
				queryFormPost("fn_mConsolidadoTieneProcedimientoRead", { async:false });
				habilitaPestanas();
				if(esRegularizacion()){
					document.getElementById('trBotones').style.display = 'mome';
					document.getElementById('trSalir').style.display = 'none';
					document.getElementById("esRegularizacion").style.display="block";
					document.getElementById("noEsRegularizacion").style.display="none";
				}else{
					document.getElementById('trBotones').style.display = 'none';
					document.getElementById('trSalir').style.display = 'block';
					document.getElementById("noEsRegularizacion").style.display="block";
					document.getElementById("esRegularizacion").style.display="none";
					cargaSolicitudesDisponibles();
				}				
				cargaSolicitudesDisppresel();
				cargaSolicitudesSel();	
				queryFormPost("mUsuarioMismaUE", {async: false   });
				if (!(roles.indexOf("ADMIN_RECMAT")>=0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1
					||$("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val())) {
					document.getElementById("imgAprobarPreseleccion").disabled = true;
					document.getElementById("imgDevolverPreseleccion").disabled = true;
					document.getElementById("imgAnularPreseleccion").disabled = true;
				}
				else if ($("#nIdEstadoCon").val() == "1") { //capturada
					document.getElementById("imgAprobarPreseleccion").disabled = false;
					document.getElementById("imgDevolverPreseleccion").disabled = true;
					document.getElementById("imgAnularPreseleccion").disabled = false;
				}
				else if ($("#nIdEstadoCon").val() == "2") { //Aprobada
					document.getElementById("imgAprobarPreseleccion").disabled = true;
					document.getElementById("imgDevolverPreseleccion").disabled = false;
					document.getElementById("imgAnularPreseleccion").disabled = false;
				} 
				else if ($("#nIdEstadoCon").val() == "3") { //anulada
					document.getElementById("imgAprobarPreseleccion").disabled = true;
					document.getElementById("imgDevolverPreseleccion").disabled = true;
					document.getElementById("imgAnularPreseleccion").disabled = true;
				}
				
				if (!(roles.indexOf("ADMIN_RECMAT")>=0||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1 || $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val()) )
					deshabilitarCampos();
				else if ($("#nIdEstadoCon").val() == "2" || $("#nIdEstadoCon").val() == "3") 
					deshabilitarCampos();
				
				$("#PreseleccionLineas").click(function() {
					cargaLineasPresel();
					cargaLineasDisppresel();					
				});
				$("#PreseleccionRequisiciones").click(function() {
					window.location="Consolidado.jsp?tab=4";
				});
				
						
				$('#tblLineasDispPreSel').dataTable().fnClearTable();
				$('#tblLineasPreSel').dataTable().fnClearTable();							
				cargaLineasPresel();
				cargaLineasDisppresel();	
				queryFormPost("numLinDispCreate", {async : false});
				queryFormPost("numLinAgregadasCreate", {async : false});				
				if ($("#numLinDisp").val() > 0 || $("#numLin").val() > 0)
					$( "#PreseleccionLineas" ).attr("disabled", false);
				else
					$( "#PreseleccionLineas" ).attr("disabled", true);
								
				$("#cCapitulo").change(function () {
					querySelectPost("mCatalogoCapituloReadConsolidado", "cCapitulo", {async : false});
					querySelectPost("mCatalogoSubPartidaConsolRead", "partida", {async : true});
				});				
				
				$("#btnSolicitudesDisppreselBuscar").button().click(function(){
						if(esRegularizacion()){
							$('#tblSolicitudDispPreSel').dataTable().fnClearTable();
							cargaSolicitudesDisppresel();
						}else{
							$('#tblSolicitudDisponible').dataTable().fnClearTable();
							cargaSolicitudesDisponibles();
						} 
						queryFormPost("numSolDispCreate", {async : false});
				});			
				
				$("#btnSolicitudesDisppreselTodas").button().click(function(){
						var aTrs = $('#tblSolicitudDispPreSel').dataTable().fnGetNodes();
						var aTrsPreSel=  $('#tblSolicitudPreSel').dataTable().fnGetNodes();
					
						var nTrPreSel;
						if((aTrsPreSel.length)>0 ){
							nTrPreSel = $('#tblSolicitudPreSel').dataTable().fnGetData(aTrsPreSel[0]);	
						}  								
            			for ( var i=aTrs.length-1 ; i>=0; i-- )     
						{         									
						    var nTr = $('#tblSolicitudDispPreSel').dataTable().fnGetData(aTrs[i]);
						    if((aTrsPreSel.length)>0 && (nTr[1].substr(0,1)!=nTrPreSel[1].substr(0,1))){
								swal("No se puede consolidar Req. de CAPITULOS DIFERENTES",{icon:"warning",button: "Cerrar"});
								return;
							}									
							$('#tblSolicitudPreSel').dataTable().fnAddData( nTr );
							$('#tblSolicitudDispPreSel').dataTable().fnDeleteRow( i );
							var valor = nTr[0];
							$("#cIdTipoSolicitud").val($.trim(valor).split("-")[0]);
							$("#cIdUnidadEjecutoraSolicitud").val($.trim(valor).split("-")[1]);
							$("#nIdConsecutivoSolicitud").val($.trim(valor).split("-")[2]);
							
							$("#cIdSolicitudPreselecionada").val(nTr[0]);
							queryFormPost("existeSolPresel", {async : false,
								callback : function() 
								{
									if(parseInt($("#existe").val(),10)==1){
										swal("La requisición "+ntr[0]+" ya se encuentra en otro consolidado.",{icon:"info",button: "Cerrar"});
										$("#existe").val(0);
									}else{
										queryFormPost("sp_mConsolidadoPreseleccionSolicitudes", {async : false});
									}
								}
							});
							
							
						}
						queryFormPost("numSolAgregadasCreate", {async : false});
						queryFormPost("numLinDispCreate", {async : false});
						queryFormPost("numLinAgregadasCreate", {async : false});
						if ($("#numLinDisp").val() > 0 || $("#numLin").val() > 0)
							$( "#PreseleccionLineas" ).attr("disabled", false);
						else
							$( "#PreseleccionLineas" ).attr("disabled", true);
							
							
						cargaSolicitudesSel();
						cargaSolicitudesDisppresel();
				});
				$("#btnSolicitudesTodas").button().click(function(){
						var aTrs = $('#tblSolicitudDisponible').dataTable().fnGetNodes();								
            			for ( var i=aTrs.length-1 ; i>=0; i-- )     
						{         									
						    var nTr = $('#tblSolicitudDisponible').dataTable().fnGetData(aTrs[i]);										
							
							var valor = nTr[0];
							$("#cIdTipoSolicitud").val($.trim(valor).split("-")[0]);
							$("#cIdUnidadEjecutoraSolicitud").val($.trim(valor).split("-")[1]);
							$("#nIdConsecutivoSolicitud").val($.trim(valor).split("-")[2]);
							
							$("#cIdSolicitudPreselecionada").val(nTr[0]);
							queryFormPost("existeSolPresel", {async : false,
								callback : function() 
								{
									if(parseInt($("#existe").val(),10)==1){
										swal("La requisición "+ntr[0]+" ya se encuentra en otro consolidado.",{icon:"info",button: "Cerrar"});
										$("#existe").val(0);
									}else{
										$('#tblSolicitudPreSel').dataTable().fnAddData( nTr );
										$('#tblSolicitudDisponible').dataTable().fnDeleteRow( i );
										queryFormPost("sp_mConsolidadoPreseleccionSolicitudes", {async : false});
									}
								}
							});
							
							
						}												
					//	queryFormPost("numSolDispCreate", {async : false});
						queryFormPost("numSolAgregadasCreate", {async : false});
						cargaSolicitudesSel();
						//cargaSolicitudesDisppresel();
						cargaSolicitudesDisponibles();
						if ($("#numLinDisp").val() > 0 || $("#numLin").val() > 0)
							$( "#PreseleccionLineas" ).attr("disabled", false);
						else
							$( "#PreseleccionLineas" ).attr("disabled", true);
						guardaBitacora("AGREGA_TODAS_REQUISICIONES",$("#cIdConsolidado").val());
				});
				
		
				$("#btnSolicitudespreselTodas").button().click(function(){
						var aTrs = $('#tblSolicitudPreSel').dataTable().fnGetNodes();           
						for ( var i=0 ; i<aTrs.length; i++ )     
						{         								            
							var nTr = $('#tblSolicitudPreSel').dataTable().fnGetData(aTrs[i]); 
							if(esRegularizacion()){
								$('#tblSolicitudDispPreSel').dataTable().fnAddData( nTr );
							}else{
								$('#tblSolicitudDisponible').dataTable().fnAddData( nTr );
							} 							
							
							$('#tblSolicitudPreSel').dataTable().fnDeleteRow( i );
							var valor = nTr[0];
							$("#cIdSolicitud").val($.trim(valor));
							$("#cIdTipoSolicitud").val($.trim(valor).split("-")[0]);
							$("#cIdUnidadEjecutoraSolicitud").val($.trim(valor).split("-")[1]);
							$("#nIdConsecutivoSolicitud").val($.trim(valor).split("-")[2]);									
							$("#nIdLineaSolicitud").val('0');
							queryFormPost("sp_mConsolidadoEliminaSolicitudesPreseleccion", {async : false});
						}												
						queryFormPost("numSolAgregadasCreate", {async : false});
						
						queryFormPost("numSolAgregadasCreate", {async : false});
						queryFormPost("numLinDispCreate", {async : false});
						queryFormPost("numLinAgregadasCreate", {async : false});
						if ($("#numLinDisp").val() > 0 || $("#numLin").val() > 0)
							$( "#PreseleccionLineas" ).attr("disabled", false);
						else
							$( "#PreseleccionLineas" ).attr("disabled", true);
							
							
						cargaSolicitudesSel();
						if(esRegularizacion()){
							cargaSolicitudesDisppresel();	
						}else{
							cargaSolicitudesDisponibles();	
						} 	
						guardaBitacora("ELIMINA_TODAS_REQUISICIONES",$("#cIdConsolidado").val());
				});				
					
				$("#btnLineasConsol").button().click(function(){							
						var aTrs =$('#tblSolicitudPreSel').dataTable().fnGetNodes(); 													
						$('#tblLineasDispPreSel').dataTable().fnClearTable();
						$('#tblLineasPreSel').dataTable().fnClearTable();													
						cargaLineasPresel();
						cargaLineasDisppresel();						
				});
			
				$("#btnLineasDisppreselTodas").button().click(function(){								
						var aTrs = $('#tblLineasDispPreSel').dataTable().fnGetNodes();								
            		    for ( var i=aTrs.length-1 ; i>=0; i-- )     
						{         									
						    var nTr = $('#tblLineasDispPreSel').dataTable().fnGetData(aTrs[i]);										
							
							var valor = nTr[3];							
							$("#cIdTipoConsolidadoL").val($.trim(valor).split("-")[0]);
							$("#cIdUnidadEjecutoraConsolidadoL").val($.trim(valor).split("-")[1]);
							$("#nIdConsecutivoConsolidadoL").val($.trim(valor).split("-")[2]);																	
							var valor2 = nTr[0];						
							$("#cIdTipoLinea").val($.trim(valor2).split("-")[0]);
							$("#cIdUnidadEjecutoraLinea").val($.trim(valor2).split("-")[1]);						
							$("#nIdConsecutivoLinea").val($.trim(valor2).split("-")[2]);
							$("#nIdLineaSolicitud").val($.trim(nTr[1]));							
							queryFormPost("mConsolidadoPreseleccionLineaSolicitudCreate", {async : false});	
							
						}
            		    cargaLineasDisppresel();
						cargaLineasPresel();
						guardaBitacora("AGREGA_TODAS_LINEAS_REQUISICIONES",$("#cIdConsolidado").val());
				});
				
				$("#btnLineaspreselTodas").button().click(function(){						
						var aTrs = $('#tblLineasPreSel').dataTable().fnGetNodes();           
						for ( var i=aTrs.length-1 ; i>=0; i-- )     
						{         								         								
							var nTr = $('#tblLineasPreSel').dataTable().fnGetData(aTrs[i]);									
							
							$("#nIdLineaSolicitud").val($.trim(nTr[1]));	
							$("#cIdSolicitud").val($.trim(nTr[0]));
							$("#cIdConsolidado").val(nTr[3]);								
							queryFormPost("sp_mConsolidadoEliminaSolicitudesPreseleccion", {async : false});
						}											
						
						
						cargaLineasDisppresel();
						cargaLineasPresel();
						guardaBitacora("ELIMINA_TODAS_LINEAS",$("#cIdConsolidado").val());
				});
				$("#btnLineasDisppreselSeleccionada").button().click(function(){										
					var aTrs = $('#tblLineasDispPreSel').dataTable().fnGetNodes();           
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							var nTr = $('#tblLineasDispPreSel').dataTable().fnGetData(aTrs[i]);   
													
							var valor = nTr[3];							
							$("#cIdTipoConsolidadoL").val($.trim(valor).split("-")[0]);
							$("#cIdUnidadEjecutoraConsolidadoL").val($.trim(valor).split("-")[1]);
							$("#nIdConsecutivoConsolidadoL").val($.trim(valor).split("-")[2]);							
							var valor2 = nTr[0];							
							$("#cIdTipoLinea").val($.trim(valor2).split("-")[0]);
							$("#cIdUnidadEjecutoraLinea").val($.trim(valor2).split("-")[1]);						
							$("#nIdConsecutivoLinea").val($.trim(valor2).split("-")[2]);
							$("#nIdLineaSolicitud").val($.trim(nTr[1]));							
							queryFormPost("mConsolidadoPreseleccionLineaSolicitudCreate", {async : false});												
						}     
					}

					
					cargaLineasDisppresel();
					cargaLineasPresel();
					guardaBitacora("AGREGA_LINEA_REQUISICION",$("#cIdConsolidado").val());
				});
				$("#btnLineaspreselSeleccionada").button().click(function(){
					var aTrs = $('#tblLineasPreSel').dataTable().fnGetNodes();           
						for ( var i=aTrs.length ; i>=0; i-- )     
						{
							if ( $(aTrs[i]).hasClass('row_selected') )         
							{							
								var nTr = $('#tblLineasPreSel').dataTable().fnGetData(aTrs[i]);  							
								
								$("#nIdLineaSolicitud").val($.trim(nTr[1]));	
								$("#cIdSolicitud").val($.trim(nTr[0]));
								$("#cIdConsolidado").val(nTr[3]);								
								queryFormPost("sp_mConsolidadoEliminaSolicitudesPreseleccion", {async : false});											
							}
						}
						
					cargaLineasDisppresel();
					cargaLineasPresel();
					guardaBitacora("ELIMINA_LINEA_REQUISICION",$("#cIdConsolidado").val());
				});				
				
				$("#cIdConsolidado").val($("#cIdTipoConsolidado").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());
				queryFormPost("mConsolidadoCaratulaRead",{async : false});									
			});

		$('#tblSolicitudDispPreSel tr').live('dblclick', function() { 
			var tabla='<%=tabla%>';
			if (tabla==0){
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
					var aTrs = $('#tblSolicitudDispPreSel').dataTable().fnGetNodes();  
					var aTrsPreSel=  $('#tblSolicitudPreSel').dataTable().fnGetNodes();
					
					var nTrPreSel;
					if((aTrsPreSel.length)>0 ){
						nTrPreSel = $('#tblSolicitudPreSel').dataTable().fnGetData(aTrsPreSel[0]);	
					}          
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							var nTr = $('#tblSolicitudDispPreSel').dataTable().fnGetData(aTrs[i]); 
							//Condición para que no consolide Req de diferentes capitulos
							if((aTrsPreSel.length)>0 && (nTr[1].substr(0,1)!=nTrPreSel[1].substr(0,1))){
								swal("No se puede consolidar Req. de CAPITULOS DIFERENTES",{icon:"warning",button: "Cerrar"});
								return;
							}
							$("#cIdSolicitudPreselecionada").val(nTr[0]);
							queryFormPost("existeSolPresel", {async : false});
							
							if(parseInt($("#existe").val(),10)==1){
								swal("La requisición ya se encuentra en otro consolidado.",{icon:"info",button: "Cerrar"});
								$(this).removeClass('row_selected');
								$("#existe").val(0); 
								return;
							}
							$('#tblSolicitudPreSel').dataTable().fnAddData( nTr );							
							$('#tblSolicitudDispPreSel').dataTable().fnDeleteRow( i );
							var valor = nTr[0];
							$("#cIdTipoSolicitud").val($.trim(valor).split("-")[0]);
							$("#cIdUnidadEjecutoraSolicitud").val($.trim(valor).split("-")[1]);
							$("#nIdConsecutivoSolicitud").val($.trim(valor).split("-")[2]);	
						}     
					}
					queryFormPost("sp_mConsolidadoPreseleccionSolicitudes", {async : false});					
				//	queryFormPost("numSolDispCreate", {async : false});
					queryFormPost("numSolAgregadasCreate", {async : false});
					queryFormPost("numLinDispCreate", {async : false});
					queryFormPost("numLinAgregadasCreate", {async : false});
					if ($("#numLinDisp").val() > 0 || $("#numLin").val() > 0)
						$( "#PreseleccionLineas" ).attr("disabled", false);
					else
						$( "#PreseleccionLineas" ).attr("disabled", true);
					cargaSolicitudesDisppresel();
					cargaSolicitudesSel();
				}
			});
			
			$('#tblSolicitudPreSel tr').live('dblclick', function() {    
				var tabla2='<%=tabla2%>'; 
				roles='<%=roles%>';
				if (tabla2==0){
					if ($("#nIdEstadoCon").val() != "2" && $("#nIdEstadoCon").val() != "3" &&
						(roles.indexOf("ADMIN_RECMAT")>=0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1|| $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val()) ) {
							if ( $(this).hasClass('row_selected') )             
								$(this).removeClass('row_selected');         
							else            
								$(this).addClass('row_selected');
								var aTrs = $('#tblSolicitudPreSel').dataTable().fnGetNodes();           
								for ( var i=aTrs.length ; i>=0; i-- )     
								{         
									if ( $(aTrs[i]).hasClass('row_selected') )         
									{    
										
										var nTr = $('#tblSolicitudPreSel').dataTable().fnGetData(aTrs[i]); 
										if(esRegularizacion()){
											$('#tblSolicitudDispPreSel').dataTable().fnAddData( nTr );
										}else{
											$('#tblSolicitudDisponible').dataTable().fnAddData( nTr );
										} 										
										$('#tblSolicitudPreSel').dataTable().fnDeleteRow( i );									
										var valor = nTr[0];
										$("#cIdSolicitud").val($.trim(valor));
										$("#cIdTipoSolicitud").val($.trim(valor).split("-")[0]);
										$("#cIdUnidadEjecutoraSolicitud").val($.trim(valor).split("-")[1]);
										$("#nIdConsecutivoSolicitud").val($.trim(valor).split("-")[2]);	
									}     
								}
								$("#nIdLineaSolicitud").val('0');
								queryFormPost("sp_mConsolidadoEliminaSolicitudesPreseleccion", {async : false});
								
								var szWhere = "";
								szWhere = "cSolicitud = '"+$("#cIdSolicitud").val()+"'";
								var campos = "";  					
								campos = $("#cIdConsolidado").val();
								
								var szTabla = "LINEASSOLICITUD";                                                                                         
								$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, Campos:campos, Order:" order by 1 desc ", ajax: 'false'}, function(j){
								for (var i = 0; i < j.length; i++) 
								{  						
									$("#nIdLineaSolicitud").val(j[i].Col0);	
									queryFormPost("sp_mConsolidadoEliminaSolicitudesPreseleccion", {async : false});																				 
								}
								});
			
							//	queryFormPost("numSolDispCreate", {async : false});
								queryFormPost("numSolAgregadasCreate", {async : false});
								queryFormPost("numLinDispCreate", {async : false});
								queryFormPost("numLinAgregadasCreate", {async : false});
								if ($("#numLinDisp").val() > 0 || $("#numLin").val() > 0)
									$( "#PreseleccionLineas" ).attr("disabled", false);
								else
									$( "#PreseleccionLineas" ).attr("disabled", true);
						}
					if(esRegularizacion()){
						cargaSolicitudesDisppresel();
					}else{
						cargaSolicitudesDisponibles();
					} 	
					
					cargaSolicitudesSel();
					guardaBitacora("ELIMINA_REQUISICION",$("#cIdConsolidado").val());
				}
			});
			
			$('#tblLineasDispPreSel tr').live('click', function() {         
				if ( $(this).hasClass('row_selected') )             
					 $(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
				
			});
			$('#tblLineasPreSel tr').live('click', function() { 
				roles='<%=roles%>';
				if ($("#nIdEstadoCon").val() != "2" && $("#nIdEstadoCon").val() != "3" &&
					(roles.toString().indexOf("ADMIN_RECMAT")>=0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1|| $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val()) ) {
						if ( $(this).hasClass('row_selected') )             
							$(this).removeClass('row_selected');         
						else            
							$(this).addClass('row_selected');
					
					}
					
				});					
			
			function cargaSolicitudesSel(){	
				$('#tblSolicitudPreSel').dataTable().fnClearTable();
				
				var campos = $("#cIdConsolidado").val();	
				oTableSolicitudPreSel=$('#tblSolicitudPreSel').dataTable({
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mConsolidadoSolicitudesPreseleccionadas('"+campos+"')",
					bJQueryUI: true,
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{ sName: "cSolicitud" },
						{ sName: "cIdSubPartida"   },
						{ sName: "cDescripcion"   }
					]
				});
				queryFormPost("numSolDispCreate", {async : false});
	          	queryFormPost("numSolAgregadasCreate", {async : false});
			}
			function cargaSolicitudesDisppresel(){				
				$('#tblSolicitudDispPreSel').dataTable().fnClearTable();
				var szWhere = " cIdSolicitud NOT IN( select replace (cSolicitud,' ','') as cIdSolicitud from fn_mConsolidadoSolicitudesPreseleccionadas ('" + $("#cIdConsolidado").val() + "'))";
				var area =$('#cUnidadEjecutoraRMC').val();	
				
				capitulo = '';
				partida = '';
				requisicion = '';
				var capitulo =$('#cCapitulo option:selected').val();						
				var partida = $('#partida option:selected').val();
				var requisicion = $('#cIdConsolidadoPresel').val();
				area = $.trim(area);
				partida = $.trim(partida);
				requisicion= $.trim(requisicion);      					
				if(area   	  != null && area    	 != '' && area    	  != '0'){szWhere += '  and substring(cIdSolicitud,4,3) = \''+  area +'\'';}      					
		    	if(capitulo   != null && capitulo    != '' && capitulo    != '0'){szWhere += '  and substring(cIdSubPartida,1,1) = \''+  capitulo +'\'';}
		    	if(partida    != null && partida     != '' && partida     != '0' && partida     != '2' && partida     != '3' && partida     != '5'){szWhere += '  and cIdSubPartida = \''+partida+'\'';}
		    	if(requisicion      != null && requisicion       != ''){szWhere += '  and cIdSolicitud = \''+requisicion+'\'';}				    	
				var campos = $("#cAlcanceC").val()+"','"+$("#cIdTipoConsolidado").val()+"','"+$("#cUnidadEjecutoraRMC").val()+"','"+$("#cIdConsolidado").val();
						
				oTableSolicitudDispPreSel=$("#tblSolicitudDispPreSel").dataTable({         																		 
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
					bJQueryUI: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=dbo.fn_mConsolidadoPreseleccionSolicitudes('"+campos+"')&qw="+szWhere,
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{ sName: "cIdSolicitud" },
						{ sName: "cIdSubPartida"   },
						{ sName: "cDescripcion"   }
					]
				});
              	
	         	if ($('#cCapitulo option:selected').val() == 0){
	         		
	         		if(requisicion != ''){	         			
	         			queryFormPost("numSolDispBuscarNumCreate", {async : false});	 	         				         			        		
	         		}else{	         				         			
	         	//		queryFormPost("numSolDispCreate", {async : false});
	         		}
	         		
	         	}else{
	         		if(requisicion == ''){
			         	if ($('#cCapitulo option:selected').val() == 0)
			         		$('#cCapitulo option:selected').val("%");
			         	
			         	$('#str_capitulo').val(""+$('#cCapitulo option:selected').val()+"");
			         	
			         	if (partida     == '' || partida     == 0 || partida     == '2' || partida     == '3' || partida     == '5' || partida     == 'undefined'){	         		
			         		$('#str_partida').val("%");
			         	}else{
			         		$('#str_partida').val(""+$('#partida option:selected').val()+"");
			         	}	         				         	
			         	queryFormPost("numSolDispBuscarCapParCreate", {async : false});
		         	}else{
		         		if ($('#cCapitulo option:selected').val() == 0)
			         		$('#cCapitulo option:selected').val("%");
		         		
			         	$('#str_capitulo').val(""+$('#cCapitulo option:selected').val()+"");
			         	
			         	if (partida    == null || partida     == '' || partida     == 0 || partida     == '2' || partida     == '3' || partida     == '5' || partida     == 'undefined'){			         		
			         		$('#str_partida').val("%");
			         	}else{
			         		$('#str_partida').val(""+$('#partida option:selected').val()+"");
			         	}
		         		if(requisicion == ''){
			         		$('#cIdConsolidadoPresel').val('');	         		
			         	}else{
			         		$('#cIdConsolidadoPresel').val();	         		
			         	}
		         		queryFormPost("numSolDispBuscarCapParNumCreate", {async : false});		         		
		         	}
		         	
		         	
	         	}
			 	queryFormPost("numSolAgregadasCreate", {async : false});
			}
			
			function cargaLineasDisppresel(){
				$('#tblLineasDispPreSel').dataTable().fnClearTable();
				//var szWhere = "";
				var campos =  $("#cIdConsolidado").val();
				$('#tblLineasDispPreSel').dataTable({         									
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=dbo.fn_mConsolidadoLineasDispPresel('"+campos+"')",
					aoColumns: [
						{ sName: "cSolicitud" },
						{ sName: "nIdLineaSolicitud"   },
						{ sName: "cDescripcion"   },
						{ sName: "cIdConsolidado"   }
					]
				});
			}
			function cargaLineasPresel(){
				$('#tblLineasPreSel').dataTable().fnClearTable();
				//var szWhere = "";
				var campos =  $("#cIdConsolidado").val();
				oTableLineasPreSel=$('#tblLineasPreSel').dataTable({         
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=dbo.fn_mConsolidadoLineasSolicitudPreseleccionadas('"+campos+"')",
					aoColumns: [
						{ sName: "cSolicitud" },
						{ sName: "nIdLineaSolicitud"   },
						{ sName: "cDescripcion"   },
						{ sName: "cIdConsolidado"   }
					]
				});
             	           
		         queryFormPost("numLinDispCreate", {async : false});
				 queryFormPost("numLinAgregadasCreate", {async : false});
				 if ($("#numLinDisp").val() > 0 || $("#numLin").val() > 0)
					$( "#PreseleccionLineas" ).attr("disabled", false);
				 else
					$( "#PreseleccionLineas" ).attr("disabled", true);
			}
			function BuscaSolicitudesDisppresel(){  					
            	var szWhere = " ";
				var campos = "";  					
				campos = $("#cAlcanceC").val()+"','"+$("#cIdTipoConsolidado").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#cIdConsolidado").val();			
                    var szTabla = "SOLICITUDESDISPPRESEL";                                                                                         
					$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, Campos:campos, Order:" order by 1 desc ", ajax: 'false'}, function(j){
						for (var i = 0; i < j.length; i++) 
    					{  
    					    $('#tblSolicitudDispPreSel').dataTable().fnAddData( [
								j[i].Col0,j[i].Col1,j[i].Col2
							]);    					 
						}
		         })  	
			}
				
			function deshabilitarCampos() {
				document.getElementById("btnLineaspreselTodas").setAttribute("disabled", "disabled");
				document.getElementById("btnSolicitudespreselTodas").setAttribute("disabled", "disabled");
				document.getElementById("divReq").style.display = "none";
				document.getElementById("divLin").style.display = "none";			
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
						
						swal("El consolidado ha sido aprobado.",{icon:"info",button: "Cerrar"});
						window.location = "Consolidado.jsp?tab=4";
					}
					else
						swal("Necesita agregar al menos una línea para aprobar el Consolidado.",{icon:"info",button: "Cerrar"});
				}else{
					swal("No tiene permiso para realizar esta accion",{icon:"warning",button: "Cerrar"});
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
						window.location = "Consolidado.jsp?tab=4";
					}
					else
						swal("El Consolidado "+$("#cIdConsolidado").val()+" está asociado a un Procedimiento. Para devolverlo, primero es necesario declarar desierto el Procedimiento.",{icon:"info",button: "Cerrar"});
				}else{
					swal("No tiene permiso para realizar esta acción",{icon:"warning",button: "Cerrar"});
				}
			}
		
		function anularConsolidado () {
				var imgAnular='<%=imgAnular%>';
				if (imgAnular==0){
					queryFormPost("fn_mConsolidadoTieneProcedimientoRead", { async:false });
					if($("#tieneProcedimiento").val() == '' || $("#tieneProcedimiento").val() == 'N/A')
						$("#tieneProcedimiento").val('0');
					
					if ($("#tieneProcedimiento").val() == "0") {				
						if (confirm("¿Está seguro que desea anular el Consolidado "+ $("#cIdConsolidado").val() +" ? \n Esta acción no puede revertirse y todas sus lineas serán liberadas.")) {
							$("#nIdEstado").val("3"); //anulada
							queryFormPost("mConsolidadoUpdate", { async:false });
							queryFormPost("sp_mConsolidadoAnulaConsolidado", {async:false});
							//Bitácora
							$("#cAccion").val("ANULA_CONSOLIDADO");
							$("#cIdDocumento").val($("#cIdConsolidado").val());
							queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
							window.location = "Consolidado.jsp?tab=4";
						}
					}else
						swal("El Consolidado "+$("#cIdConsolidado").val()+" está asociado a un Procedimiento. Para devolverlo, primero es necesario declarar desierto el Procedimiento.",{icon:"info",button: "Cerrar"});
				}else{
					swal("No tiene permiso para realizar esta acción",{icon:"warning",button: "Cerrar"});
				}
		}
		
		function cargaSolicitudesDisponibles(){
			$('#tblSolicitudDisponible').dataTable().fnClearTable();
			var szWhere = " cIdSolicitud NOT IN( select replace (cSolicitud,' ','') as cIdSolicitud from fn_mConsolidadoSolicitudesPreseleccionadas ('" + $("#cIdConsolidado").val() + "'))";
			//var szWhere = ' \'1\'=\'1\' ';						
			var area =$('#cUnidadEjecutoraRMC').val();	
			
			capitulo = '';
			partida = '';
			requisicion = '';
			var capitulo =$('#cCapitulo option:selected').val();						
			var partida = $('#partida option:selected').val();
			var requisicion = $('#cIdConsolidadoPresel').val();
			area = $.trim(area);
			partida = $.trim(partida);
			requisicion= $.trim(requisicion);      					
			if(area   	  != null && area    	 != '' && area    	  != '0'){szWhere += '  and substring(cIdSolicitud,4,3) = \''+  area +'\'';}      					
	    	if(capitulo   != null && capitulo    != '' && capitulo    != '0'){szWhere += '  and substring(cIdSubPartida,1,1) = \''+  capitulo +'\'';}
	    	if(partida    != null && partida     != '' && partida     != '0' && partida     != '2' && partida     != '3' && partida     != '5'){szWhere += '  and cIdSubPartida = \''+partida+'\'';}
	    	if(requisicion      != null && requisicion       != ''){szWhere += '  and cIdSolicitud = \''+requisicion+'\'';}				    	
			var campos = $("#cAlcanceC").val()+"','"+$("#cIdTipoConsolidado").val()+"','"+$("#cUnidadEjecutoraRMC").val()+"','"+$("#cIdConsolidado").val();
			
			oTableSolicitudDisponible=$("#tblSolicitudDisponible").dataTable({         																		 
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=dbo.fn_mConsolidadoPreseleccionSolicitudesApartado('"+campos+"')&qw="+szWhere,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdSolicitud" },
					{ sName: "cIdSubPartida"   },
					{ sName: "cDescripcion"   }
				]
			});
		}
		// click de las solicitudes que tienen apartado
		$("#tblSolicitudDisponible tr").live('click',function(){
				if ($(this).hasClass('row_selected'))
					$(this).removeClass('row_selected'); 
				else
					$(this).addClass('row_selected');

					var aTrs = $("#tblSolicitudDisponible").dataTable().fnGetNodes();
					for (var i=aTrs.length ; i>=0; i-- ){         
						if ($(aTrs[i]).hasClass('row_selected')){             
							var nTr = $('#tblSolicitudDisponible').dataTable().fnGetData(aTrs[i]);   
							$('#tblSolicitudPreSel').dataTable().fnAddData( nTr );							
							$('#tblSolicitudDisponible').dataTable().fnDeleteRow( i );
							var valor = nTr[0];
							
							$("#cIdTipoSolicitud").val($.trim(valor).split("-")[0]);
							$("#cIdUnidadEjecutoraSolicitud").val($.trim(valor).split("-")[1]);
							$("#nIdConsecutivoSolicitud").val($.trim(valor).split("-")[2]);							
						}    
					}
					
					queryFormPost("sp_mConsolidadoPreseleccionSolicitudes", {async : false});					
				//	queryFormPost("numSolDispCreate", {async : false});
					queryFormPost("numSolAgregadasCreate", {async : false});
					queryFormPost("numLinDispCreate", {async : false});
					queryFormPost("numLinAgregadasCreate", {async : false});
					if ($("#numLinDisp").val() > 0 || $("#numLin").val() > 0)
						$( "#PreseleccionLineas" ).attr("disabled", false);
					else
						$( "#PreseleccionLineas" ).attr("disabled", true);
					cargaSolicitudesDisponibles();
					cargaSolicitudesSel();
					guardaBitacora("AGREGA_REQUISICION",$("#cIdConsolidado").val());
		});
		
	function habilitaPestanas(){
		queryFormPost("mValidaPrecompromiso",{async:false});
		roles='<%=roles%>';
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
					/* if ($("#R_NOMBRE").val().toString().indexOf("ADMIN_RECMAT")>=0){
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
					} */			
					$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
					$("#ampliacionPrecompromiso").css("display", "none");
								
				}else{ //aplicado contablemente
					//validar que el documento sea de un flujo normal apartado/prmt
					queryFormPost("validaFlujo", {async:false});
					if($("#cEventoFlujo").val()=="DISP_PRECOMMAT"){
						if (roles.indexOf("ADMIN_RECMAT")>=0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1){
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
	
		function onlyIntegers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			return (keyPressed >= 48 && keyPressed <= 57);
		}
	
	
	function esRegularizacion(){
		var regularizacion=false;
		var apartables = [ "CC", "CO", "CS", "CA", "CT" ];
		
		if ($.inArray($("#cIdTipoConsolidado").val(), apartables) == -1){ // no traen apartado
			regularizacion=true;
		}else{
			regularizacion=false;
		}
		
		return regularizacion;
	}
	function guardaBitacora(accion,documento){
		//Bitácora
		$("#cAccion").val(accion);
		//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
		$("#cIdDocumento").val(documento);
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
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
		                        	<img id="imgAprobarPreseleccion" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="aprobarConsolidado();"/> Aprobar 
		                            <img id="imgDevolverPreseleccion" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer" onclick="devolverConsolidado();" /> Devolver
		                            <img id="imgAnularPreseleccion" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="anularConsolidado();" /> Anular
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
						    		<input type="text" value="<%=unidadUsuarioLogeado%>" style="width: 25px;border-width:0; background-color:transparent" id="lblUnidadUsuario" name="lblUnidadUsuario" readonly />
						    		<input type="text" style="width: 690px;border-width:0; background-color:transparent" id="lblDescUsuario" name="lblDescUsuario" readonly />
						    	</td>
					    	</tr>
					    	<tr>
						    	<td align="left" colspan="2"><input type="text" style="width: 90px;border-width:0; background-color:transparent" id="lblConsolidado" name="lblConsolidado" readonly />
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
					    </table>						
						<table border="0" align="left" width="100%">	
						<tr>
							<td>
								
								<input id="cDescripcion" name="cDescripcion" type="hidden" size="2000">
								<input id="nIdEstado" name="nIdEstado" type="hidden" size="2">
								<input id="nIdAlcance" name="nIdAlcance" type="hidden" size="2">
								<input id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" type="hidden" size="10">
								<input id="cIdEntidadContable" name="cIdEntidadContable" type="hidden" size="4">
								
								
								<input id="cIdSolicitud" name="cIdSolicitud" type="hidden" size="10">
								<input id="nIdLineaSolicitud" name="nIdLineaSolicitud" type="hidden" size="10">
								<input id="cIdTipoSolicitud" name="cIdTipoSolicitud" type="hidden" size="3">
								<input id="cIdUnidadEjecutoraSolicitud" name="cIdUnidadEjecutoraSolicitud" type="hidden" size="3">
								<input id="nIdConsecutivoSolicitud" name="nIdConsecutivoSolicitud" type="hidden" size="3">
																
								<input id="cIdTipoConsolidadoL" name="cIdTipoConsolidadoL" type="hidden" size="10">
								<input id="cIdUnidadEjecutoraConsolidadoL" name="cIdUnidadEjecutoraConsolidadoL" type="hidden" size="3">											
								<input id="nIdConsecutivoConsolidadoL" name="nIdConsecutivoConsolidadoL" type="hidden" size="10">											
								<input id="cIdTipoLinea" name="cIdTipoLinea" type="hidden" size="10">
								<input id="cIdUnidadEjecutoraLinea" name="cIdUnidadEjecutoraLinea" type="hidden" size="3">								
								<input id="nIdConsecutivoLinea" name="nIdConsecutivoLinea" type="hidden" size="10">																
								<input id="nIdLineaConsolidado" name="nIdLineaConsolidado" type="hidden" size="1">
								
								<input id="cIdCABM" name="cIdCABM" type="hidden" size="20">
								<input type="hidden" id="imgEstado" name="imgEstado"/>
								
								
								<input id="Accion" name="Accion" type="hidden" size="3">
								<input id="num_cambs" name="num_cambs" type="hidden" size="3">
								<input id="cAlcanceC" name="cAlcanceC" type="hidden" size="2">							
								<input name="cIdConsolidado" id="cIdConsolidado" type="hidden"/>								
								<input id="nIdEstadoCon" name="nIdEstadoCon" type="hidden" size="2">								
								<input type="hidden" value="<%=usuario.getU_UR()%>" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" />
								<input type="hidden" value="<%=cIdUnidadEjecutora%>" name="cIdUnidadEjecutoraConsolidado" id="cIdUnidadEjecutoraConsolidado" />
								<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuario.getLogin()%>" />
								<input type="hidden" name="R_NOMBRE" id="R_NOMBRE" />																						
								<input name="tieneProcedimiento" id="tieneProcedimiento" type="hidden">
								<input name="numLinTot" id="numLinTot" type="hidden">
								<input name="str_capitulo" id="str_capitulo" type="hidden">
								<input name="str_partida" id="str_partida" type="hidden">
								<input name="cAccion" id="cAccion" type="hidden"/>
								<input name="cIdDocumento" id="cIdDocumento" type="hidden"/>
								<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuario.getLogin()%>" />
								<input name="numLineasConsolidadas" id="numLineasConsolidadas" type="hidden"/>
								
								<!-- habilita pestanas -->
								
								<input type="hidden" id="cEventoFlujo" name="cEventoFlujo"/>
								<input name="documentoAplicado" id="documentoAplicado" type="hidden"/>
								<input name="cIdSolicitudPreselecionada" id="cIdSolicitudPreselecionada" type="hidden" />
								<input name="existe" id="existe" type="hidden" value="0"/>
								<input name="isAdmin" id="isAdmin" type="hidden" value="1"/>
								<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
								<input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE" value=""/>
							</td>
						</tr>
					</table>
				</fieldset>				
				<fieldset>
					<legend>Requisiciones y L&iacute;neas</legend>
					<div class="tabs" id="tabsA">
						<ul>
							<li><a id="PreseleccionRequisiciones" href="#tabsA-0">Requisiciones</a></li>
							<li><a id="PreseleccionLineas" href="#tabsA-1">L&iacute;neas</a></li>
						</ul>
						<div id="tabsA-0" align="center">
							<div id="divReq">
								<fieldset>
									<legend>Realiza Preselecci&oacute;n</legend>
									<table border="0" align="left" width="100%">	
										<tr>
											<td colspan="3" align="left">								
												<table id="tblcombosPreseleccion">							
														<tr>
															<td>C&aacute;pitulo:</td>										
															<td colspan="2" align="left">
																<select id="cCapitulo" name="cCapitulo"	style="width: 40em;"></select>
															</td>
														</tr>
														<tr>
															<td >Unidad Ejecutora:</td>										
															<td colspan="2" align="left">
																<select id="cUnidadEjecutoraRMC" name="cUnidadEjecutoraRMC" style="width: 40em;">
																<option value="<%=usuario.getU_UR()%>" selected="selected"></select>
															</td>
														</tr>
														
														<tr>
															<td >Partida:</td>										
															<td  colspan="2" align="left">
																<select id="partida" name="partida" style="width: 40em;"></select>
															</td>
														</tr>
														<tr>
															<td>N&uacute;mero:</td>										
															<td colspan="2" align="left">
																<input name="cIdConsolidadoPresel" id="cIdConsolidadoPresel" onkeypress="return onlyIntegers(event);" type="text" style="width: 40em;" >
															</td>
														</tr>
														<tr>																			
															<td colspan="3" align="left">
																<input type="button" name="btnSolicitudesDisppreselBuscar" id="btnSolicitudesDisppreselBuscar" value="Buscar"  class="btnInterfaceBG ui-button ui-corner-all"/>
															</td>
														</tr>
												</table>		
											</td>
										</tr>					
									</table>
							</fieldset>
								<fieldset>
								<legend>Requisiciones Disponibles para Pre-selecci&oacute;n <input name="numSolDisp" id="numSolDisp" type="text" size ="4" style="border: 0px solid black;"></legend>
								<div id="esRegularizacion" style="display:none" >
									es regularizacion
									<table id="tblSolicitudDispPreSel" class="display">
							            <thead>
							                <tr>
							                	<th>Requisición</th>
							                    <th>Partida</th>
							                    <th>Descripcion</th>
							                </tr>
							            </thead>
							        </table>
									<table border="0" align="left" >
										
										<tr>														
											<td colspan="2" align="left">					
												<input type="button" name="btnSolicitudesDisppreselTodas" id="btnSolicitudesDisppreselTodas" value="Agregar Todo"  class="btnInterfaceBG ui-button ui-corner-all"/>
											</td>						
										</tr>
									</table>
								</div>
								<div id="noEsRegularizacion" style="display:none" >
									<table id="tblSolicitudDisponible" class="display">
							            <thead>
							                <tr>
							                	<th>Requisición</th>
							                    <th>Partida</th>
							                    <th>Descripcion</th>
							                </tr>
							            </thead>
							        </table>
									<table border="0" align="left" >
										<tr>														
											<td colspan="2" align="left">
												<input type="button" name="btnSolicitudesTodas" id="btnSolicitudesTodas" value="Agregar Todo"  class="btnInterfaceBG ui-button ui-corner-all"/>		 
											</td>						
										</tr>
									</table>
								</div>
							</fieldset>
							</div>
							<fieldset>
								<legend>Requisiciones Pre-seleccionadas <input name="numSol" id="numSol" type="text" size ="4" style="border: 0px solid black;"></legend>
								<table id="tblSolicitudPreSel" class="display" >
						            <thead>
						                <tr>
						                	<th>Requisición</th>
						                    <th>Partida</th>
						                    <th>Descripcion</th>
						                </tr>
						            </thead>
						        </table>
								<table border="0" align="left" >
									<tr VALIGN=TOP>												
										<td colspan="3" align="left">
											<input type="button" name="btnSolicitudespreselTodas" id="btnSolicitudespreselTodas" value="Desagregar Todo"  class="btnInterfaceBG ui-button ui-corner-all"/>
										</td>						
									</tr>
								</table>
							</fieldset>
						</div>
<!-- 					Siguiente pestaña -->
						<div id="tabsA-1" align="center">
							<div id="divLin">
								<fieldset>
									<legend>Lineas Disponibles:<input name="numLinDisp" id="numLinDisp" type="text" size ="4" style="border: 0px solid black;"></legend>
									<table border="0" align="left">
										<tr>
											<td>
												<table id="tblLineasDispPreSel" class="display" >					
													<thead>
														<tr>
															<th style="background: #D4D0C8">Requisición</th>
															<th style="background: #D4D0C8">Línea</th>
															<th style="background: #D4D0C8">Descripcion</th>
															<th style="background: #D4D0C8">cIdConsolidado</th>
														</tr>
													</thead>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												&nbsp;
											</td>
										</tr>
										<tr>
											<td align="left">
												<input type="button" name="btnLineasDisppreselTodas" id="btnLineasDisppreselTodas" value="Agregar Todo"  class="btnInterfaceBG ui-button ui-corner-all"/>
												&nbsp;&nbsp;&nbsp;
												<input type="button" name="btnLineasDisppreselSeleccionada" id="btnLineasDisppreselSeleccionada" value="Agregar Seleccionados"  class="btnInterfaceBG ui-button ui-corner-all"/>
											</td>
										</tr>							
										<tr>
											<td colspan="3" align="center">											
											<h1><label id="lbOperacion" style="font-size: 8pt"></label></h1>												
										</tr>
									</table>
								</fieldset>
							</div>								
								<fieldset>
									<legend>Lineas Preseleccionadas:<input name="numLin" id="numLin" type="text" size ="4" style="border: 0px solid black;width: 40px;"></legend>					
									<table border="0" align="left">
									<tr>
										<td>
											<table id="tblLineasPreSel" class="display" >
									            <thead>
									                <tr>
									                	<th style="background: #D4D0C8">Requisici&oacute;n</th>
									                    <th style="background: #D4D0C8">L&iacute;nea</th>
									                    <th style="background: #D4D0C8">Descripci&oacute;n</th>
									                    <th style="background: #D4D0C8">Consolidado</th>
									                </tr>
									            </thead>
									        </table>
										</td>
									</tr>
									<tr>
										<td align="left">
											<input type="button" name="btnLineaspreselTodas" id="btnLineaspreselTodas" value="Desagregar Todo"  class="btnInterfaceBG ui-button ui-corner-all"/>
											&nbsp;&nbsp;&nbsp;
											<input type="button" name="btnLineaspreselSeleccionada" id="btnLineaspreselSeleccionada" value="Desagregar Seleccionados"  class="btnInterfaceBG ui-button ui-corner-all"/>
										</td>
									</tr>
									</table>
								</fieldset>
							</div>
						</div>
				</fieldset>
			</form>				
		</body>
</html>
