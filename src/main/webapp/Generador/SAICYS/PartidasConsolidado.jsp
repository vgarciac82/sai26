﻿<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	//String path = request.getContextPath();
	//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
	Map<String, Role> rol =usuario.getRoles();   
	
	String cEjercicio = "";
	String cIdTipoConsolidado = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	int mesActual = c1.getTime().getMonth() + 1;//Porque empieza en 0: Enero
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	
	if (session.getAttribute(GestionInterface.ATT_ConEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ConEjercicio);
		cIdTipoConsolidado = (String)session.getAttribute(GestionInterface.ATT_ConTipoConsolidado);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ConUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ConConsecutivo);
	}
	
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE"))
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
		cUR = usuario.getU_UR();
		cRamo = usuario.getU_Ramo();
		String today = sdf.format(c1.getTime());
		String unidadUsuarioLogeado="";
		unidadUsuarioLogeado = usuario.getU_UR();
		
	String grupoMat;
	boolean bGrupo=false;
	
	Map grupo=usuario.getGrupos();
	Iterator it2 = grupo.entrySet().iterator();
	while(it2.hasNext()){
		Map.Entry r = (Map.Entry)it2.next();
		grupoMat=(String)r.getKey();
		System.out.print(grupoMat);
		if("RECURSOS_MATERIALES".equalsIgnoreCase(grupoMat)){
			bGrupo=true;
			break;
		}
	
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>    
    <title>Consolidado Partidas</title>    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">	
	
	<script type="text/javascript" charset="utf-8">
		var nFolioPreCompromiso;
		var cIdConsolidado;
		var vcaNoCompromiso;
		var roles='';
		$(document).ready(function() {
			$("#tbs").val(5);
			showAndHideTabs();
			<%
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
	
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				int imgAnular=0;
				int imgAprobar=0;
				int imgDevolver=0;
				String EliminaPartida="";
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botonesConsolidado=nb.getBotones(roles,"Consolidado","PartidasConsolidado");
				Iterator btnConsolidado = botonesConsolidado.entrySet().iterator();
				while (btnConsolidado.hasNext()) {
					Map.Entry bConsolidado = (Map.Entry)btnConsolidado.next();
					String img=(String) bConsolidado.getValue();
					if ("imgAnularPartidas".equals(img)){
						imgAnular=1;
					}
					if ("imgAprobarPartidas".equals(img)){
						imgAprobar=1;
					}
					if ("imgDevolverPartidas".equals(img)){
						imgDevolver=1;
					}
					%>				
					document.getElementById("<%=bConsolidado.getValue()%>").disabled = true;<%
				}
				Map botones=nb.getBotones(roles,"Consolidado","PartidasPartidas");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
				Map botonesResumen=nb.getBotones(roles,"Consolidado","PartidasResumen");
				Iterator btnResumen = botonesResumen.entrySet().iterator();
				while (btnResumen.hasNext()) {
					Map.Entry bResumen = (Map.Entry)btnResumen.next();
					String Elimina=(String) bResumen.getValue();
					if ("btnLnEliminaLinea".equals(Elimina)){
						EliminaPartida="false";
					}%>
					$("#<%=bResumen.getValue()%>").attr("disabled", true);<%
				}
			%>	
			roles='<%=roles%>';
			$("#usuarioLogin").val('<%=usuario.getLogin()%>');
			$("#cIdEntidadContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
			$("#cIdUsuarioCreacion").val('<%=usuario.getLogin()%>');
			$("#usuarioRoleConsolidado").val('<%=roles%>');
			
			if(esRegularizacion()){
				document.getElementById("lblApartado").style.display="none";
			}else{
				document.getElementById("lblApartado").style.display="block";
			}
			document.getElementById("lblConsolidado").style.readonly=true;
			document.getElementById("lblUnidadEjecutora").style.readonly=true;
			document.getElementById("lblDescripcion").style.readonly=true;
			document.getElementById("lblEstado").style.readonly=true;
			document.getElementById("lblApartado").style.readonly=true;
			
			$("#cIdConsolidado").val($("#cIdTipoConsolidado").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());
			cIdConsolidado=$("#cIdConsolidado").val();	
			queryFormPost("mConsolidadoEstadoRead", {async : false});
			queryFormPost("apartadoConsolidado", {async : false});
			queryFormPost("fn_mConsolidadoNumPartidasRead",{async : false});
			queryFormPost("numPartidasConsolidadasRead",{async : false});
			queryFormPost("fn_mConsolidadoCalculaMontoConIVARead", {async : false});
			queryFormPost("fn_mConsolidadoCalculaMontoRead", {async : false});
			queryFormPost("fn_mConsolidadoCalculaMontoBrutoRead", {async : false});
			querySelectPost("partidasConsolidadoDescripcionRead","partidaConsolidado",{async : false});
			queryFormPost("fn_mConsolidadoTieneProcedimientoRead", { async:false });	
			queryFormPost("vigenciaPrecompromisoMateriales", { async:false });	
			queryFormPost("mSolicitudDescripcionUnidad", { async:false });
			queryFormPost("mTipoConsolidadoMismoTipoSol", { async:false });
			$("#desConsolidadoPar").val($("#cDescripcion").val());
			$("#cEstadof").val($("#cEstado").val());
			$("#cAlcanceC").val($("#nIdAlcance").val());
			$("#cIdConsolidadoPar").val($("#cIdConsolidado").val());
			$("#cEstadoPar").val();		
			habilitaPestanas();	
			document.getElementById('tbcambsSig').style.display = 'none';
			document.getElementById('tbcambs').style.display = 'block';
			
			$("#DescripcionPartida").val("");
			$('#tblCucops').dataTable().fnClearTable();			
			queryFormPost("mConsolidadoRead", {async : false});								
			querySelectPost("ConsolidadoLineasCucopsDisponiblesRead", "cucopDisponible", {async : false});				
			queryFormPost("numLinTotalRead",{async : false});
			$("#mensajePartida").css("visibility", "hidden");
			cucopsDisponibles();
			queryFormPost("mConsolidado_LabelRead", { async:false });					
			queryFormPost("mConsolidado_EstadoConsolidadoRead", {async:false});
			queryFormPost("mUsuarioMismaUE", {async: false   });
			queryFormPost("cg_roleRead", { async:false });
			if (!(roles.indexOf("ADMIN_RECMAT")>=0||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0  || $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val() 
				||parseInt($("#usuariosMismaUE").val(),10)==1)) {
				document.getElementById("imgAprobarPartidas").disabled = true;
				document.getElementById("imgDevolverPartidas").disabled = true;
				document.getElementById("imgAnularPartidas").disabled = true;
			}
			else if ($("#nIdEstadoCon").val() == "1") { //capturada
				document.getElementById("imgAprobarPartidas").disabled = false;
				document.getElementById("imgDevolverPartidas").disabled = true;
				document.getElementById("imgAnularPartidas").disabled = false;
			}
			else if ($("#nIdEstadoCon").val() == "2") { //Aprobada
				document.getElementById("imgAprobarPartidas").disabled = true;
				document.getElementById("imgDevolverPartidas").disabled = false;
				document.getElementById("imgAnularPartidas").disabled = false;
			} 
			else if ($("#nIdEstadoCon").val() == "3") { //anulada
				document.getElementById("imgAprobarPartidas").disabled = true;
				document.getElementById("imgDevolverPartidas").disabled = true;
				document.getElementById("imgAnularPartidas").disabled = true;
			}
			//busca requisiciones con apartado vencido
			if(esRegularizacion()){
				document.getElementById("requisicionesVencidas").style.display = 'none';
			}else{
				if ($("#nIdEstado").val()==2 || $("#nIdEstado").val()==3 ){
					document.getElementById("requisicionesVencidas").style.display = 'none';
				}else{
					document.getElementById("requisicionesVencidas").style.display = 'block';
				querySelectPost("apartadoVencido", "apartadoVencido", {async : false});	
				}
				
			}
			
			
			//// tipo de poliza
			queryFormPost("TipoPolizaRead", {async: false});
			
			if (!(roles.indexOf("ADMIN_RECMAT")>=0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1 || $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val()))
				deshabilitarCampos();
			else if ($("#nIdEstadoCon").val() == "2" || $("#nIdEstadoCon").val() == "3")  //aprobada
				deshabilitarCampos();
				
			/////////////////////////////////////BOTON PARTIDAS/////////////////////////////////////////
			$("#PartidasPartidas").button().click(function(){	
				window.location="Consolidado.jsp?tab=5";			
				document.getElementById('tbcambsSig').style.display = 'none';
				document.getElementById('tbcambs').style.display = 'block';
				$("#cIdCABM").val("");
				$("#DescripcionPartida").val("");
				$("#DescripcionPartidaAdicional").val("");
				queryFormPost("mConsolidadoRead", {async : false});								
				querySelectPost("ConsolidadoLineasCucopsDisponiblesRead", "cucopDisponible", {async : false});				
				queryFormPost("numLinTotalRead",{async : false});
				$("#mensajePartida").css("visibility", "hidden");				
				cucopsDisponibles();
				
			});
			/////////////////////////////////////BOTON RESUMEN/////////////////////////////////////////
			$("#PartidasResumen").click(function(){								
					cargaResumenpartidas();
					initTableResumenLineasPartidas();
					//llenaVigenciaApartado();	 
					  
			});
			/////////////////////////////////////BOTON MODIFICA PARTIDA/////////////////////////////////////////
			$("#btnPartidaModificar").button().click(function(){				
				$("#DescripcionPartida").val($("#descripcion_partidaRes").val());
				$("#cIdCABM").val($("#num_cambs").val());
				querySelectPost("sp_mConsolidadoInsertaLineaConsolidadoCreate", "cCapitulo", {async : false});				
				var $tabs = $(".tabsA").tabs();
				$tabs.tabs('select', 0);
				cargaResumenpartidas();
				$("#descripcion_partidaRes").val('');
				$("#numPartidaRes").val('');
			});
			//////////////////////////////////SELECCIONAR TODOS LOS CUCOPS DE LAS LINEAS/////////////////////////////////////////////
			$("#btnCucopspreselTodas").button().click(function(){
				//$("#btnCucopsAgregar").click();
				var aTrs = $('#tblCucops').dataTable().fnGetNodes();								
	        		 for ( var i=aTrs.length-1 ; i>=0; i-- )     
					{
						$(aTrs[i]).addClass('row_selected');									    
					}            					 
			});
			//////////////////////////////AGREGAR CUCOPS A LA DESCRIPCIÓN//////////////////////////////////////
			$("#btnCucopsAgregar").button().click(function(){
				var bool = true;
				var bool2 = true;
				var aTrs = $('#tblCucops').dataTable().fnGetNodes();								
				for ( var i=aTrs.length-1 ; i>=0; i-- )     
				{
    				var nTr = $('#tblCucops').dataTable().fnGetData(aTrs[i]);
					if ( $(aTrs[i]).hasClass('row_selected') ){             
						if(bool == true){												    
							var valor = nTr[2];//.split(",")[2];
							$("#DescripcionPartida").val(valor);
							$("#DescripcionPartidaAdicional").val(nTr[3]);
							bool= false;
						}
						bool2 = false;
					}								
				}
				if(bool2){
					swal("Estimado usuario...\n\nPor lo menos debe seleccionar una Línea.\nIntente de nuevo.",{icon:"info",button: "Cerrar"});
				}            					 
            	
			});
			/////////////Se agregan todas las partidas uno a uno//////////77
			$("#btnPartidaGuardarUnoAUno").button().click(function(){
				if($("#cucopDisponible").val()==null){
					swal("No hay nada para agregar.",{icon:"info",button: "Cerrar"});
					return;
				}
				if(parseInt($("#numLinTot").val(),10)!=0){
					swal("Ya hay partidas creadas, debe de borrar todas para poder usar est\u00e9 bot\u00f3n.",{icon:"info",button: "Cerrar"});
					return;
				}
				$.ajax({url: '../../servlet/ConsolidadoServlet?cIdConsolidado='+$("#cIdConsolidado").val() , type:'post' , async: false,data:'operacion=6', dataType: 'json', success: 
					function(j){
						if(j[0].respuesta=='0'){
							guardaBitacora("PARTIDAS_CREADAS_UNOAUNO",$("#cIdConsolidado").val());
							swal("Partidas creadas.",{icon:"info",button: "Cerrar"});
							window.location = "Consolidado.jsp?tab=5";
						}else{
							swal("No inserto partidas.\nYa est\u00e1n creadas o hubo un error.",{icon:"info",button: "Cerrar"});
						}
					}
				});
			});
			//////////////////////////GUARDAR LA PARTIDA//////////////////////////////			
			$("#btnPartidaGuardar").button().click(function(){	
				var desPartida = $('#DescripcionPartida');
				var bValid = true;
				allFields = $( []).add(desPartida),
				tips = $(".validateTips");
				tips.text("");
				allFields.removeClass("ui-state-error");
				bValid = bValid && checkRequerido(desPartida, "La Descripción de la Partida");
				var flag=false;
				if (bValid) {					
					$("#cIdCABM").val($("#cucopDisponible").val());																					
			
					var aTrs = $('#tblCucops').dataTable().fnGetNodes();
					var encabezado=0;				
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )    
						{	
						flag=true;
									    
								var nTr = $('#tblCucops').dataTable().fnGetData(aTrs[i]);
								queryFormPost("ConsecutivoConsolidadoLineaRead", {async : false});
																								
								if($("#nIdLineaConsolidado").val() != $("#numPartida").val()){
									$("#nIdLineaConsolidado").val($("#numPartida").val());
									$("#numLinTotPartida").val($("#numPartida").val());									
								}
								$("#cIdSolicitud").val(nTr[0]);	
								$("#nIdConsecutivoSolicitud").val(nTr[0].split("-")[2]);
						        $("#nIdLineaSolicitud").val(nTr[1]);
								$("#Accion").val('0');//cEjercicio,cIdConsolidado,cIdSolicitud,nIdLineaSolicitud,nIdLineaConsolidado,Accion
								if(encabezado==0){
									queryFormPost("sp_mConsolidadoInsertaLineaConsolidadoCreate", {async : false});
									encabezado++;
								}
								queryFormPost("sp_mConsolidadoInsertaDetalleLineaConsolidadoCreate", {async : false});
								$('#tblCucops').dataTable().fnDeleteRow( i );																																			
								querySelectPost("ConsolidadoLineasCucopsDisponiblesRead", "cucopDisponible", {async : false});
						}     
					}	
					
					if(flag){		
						queryFormPost("numLinTotalRead",{async : false});
						if($("#nIdLineaConsolidado").val() == $("#numPartida").val()){
							$("#numLinTotPartida").val($("#nIdLineaConsolidado").val());	
						}else{
							
							$("#numLinTotPartida").val($("#numLinTot").val());	
						}
						queryFormPost("fn_mConsolidadoNumPartidasRead",{async : false});
						queryFormPost("fn_mConsolidadoCalculaMontoRead", {async : false});
						queryFormPost("fn_mConsolidadoCalculaMontoBrutoRead", {async : false});				
						$("#DescripcionPartida").val("");
						$("#DescripcionPartidaAdicional").val("");
						//$("#cIdCABM").val("");					
						cucopsDisponibles();
						$("#mensajePartida").css("visibility", "visible");
						guardaBitacora("CREA_PARTIDAS_MANUAL",$("#cIdConsolidado").val());
						window.location="Consolidado.jsp?tab=5";
					
					}else{
						flag=false;
						swal("Debe seleccionar al menos una línea.",{icon:"info",button: "Cerrar"});
						return;
					}
										
				}				
			});
			
			////////////////////ANTERIOR/////////////////////////////////
			$("#btnPartidaAnterior").button().click(function(){			
			$("#btnPartidaSiguiente").attr("disabled", false);
			querySelectPost("ConsolidadoLineasCucopsDisponiblesRead","cucopDisponible",{async : false});
			$('#tblCucops').dataTable().fnClearTable();			
			var antPartida = document.getElementById("numPartida").value-1;				
			$("#nIdLinea").val(antPartida);	
			if(antPartida >= 1){
				if(antPartida ==1){
					$("#btnPartidaAnterior").attr("disabled", true);	
				}else{
					$("#btnPartidaAnterior").attr("disabled", false);
				}
				queryFormPost("fn_mConsolidadoConsultaUNALineaConsolidadoDetalladaRead", {async : false});
				var i=0;
				for(i=0;i<document.getElementById("cucopDisponible").options.length;i++){										
					if($.trim($("#cambs_desc").val().split(" ")[0]) == $.trim(document.getElementById("cucopDisponible").options[i].value)){
							document.getElementById('tbcambsSig').style.display = 'block';							
							$("#nIdLineaConsolidado").val($("#numPartida").val());
							querySelectPost("ConsolidadoLineasCucopsDisponiblesEnLineasRead","cucopDisponible",{async : false});							
							cucopsDisponibles();
							document.getElementById('tbcambs').style.display = 'none';																				
							return false;
					}
				}				
				cucopsDisponibles();
				$("#numPartida").val($("#nIdLinea").val());				
				document.getElementById('tbcambsSig').style.display = 'block';				
				document.getElementById('tbcambs').style.display = 'none';
				$("#mensajePartida").css("visibility", "hidden");
			}			
			});
			
			////////////////////////////////SIGUIENTE////////////////////////////////////////////////////////	
			$("#btnPartidaSiguiente").button().click(function(){
				$("#btnPartidaAnterior").attr("disabled", false);
				querySelectPost("ConsolidadoLineasCucopsDisponiblesRead","cucopDisponible",{async : false});
				$('#tblCucops').dataTable().fnClearTable();
				var sigPartida = document.getElementById("numPartida").value++;
				$("#nIdLinea").val(document.getElementById("numPartida").value);										
				if(parseInt(document.getElementById("numLinTot").value) >=  parseInt(document.getElementById("nIdLinea").value,10)){
					queryFormPost("fn_mConsolidadoConsultaUNALineaConsolidadoDetalladaSigRead", {async : false});
					var i=0;
					for(i=0;i<document.getElementById("cucopDisponible").options.length;i++){										
						if($.trim($("#cambs_desc").val().split(" ")[0]) == $.trim(document.getElementById("cucopDisponible").options[i].value)){
								document.getElementById('tbcambsSig').style.display = 'block';	
								$("#cucopDisponible").val(null);
								$("#nIdLineaConsolidado").val($("#numPartida").val());
								querySelectPost("ConsolidadoLineasCucopsDisponiblesEnLineasRead","cucopDisponible",{async : false});								
								cucopsDisponibles();
								document.getElementById('tbcambs').style.display = 'none';							
								return false;
						}
					}					
					cucopsDisponibles();
					$("#numPartida").val($("#nIdLinea").val());				
					document.getElementById('tbcambsSig').style.display = 'block';
					$("#cucopDisponible").val(null);
					document.getElementById('tbcambs').style.display = 'none';
					$("#mensajePartida").css("visibility", "hidden");
				}else{					
					$("#btnPartidaSiguiente").attr("disabled", true);
					document.getElementById('tbcambsSig').style.display = 'none';
					document.getElementById('tbcambs').style.display = 'block';					
					$("#cIdCABM").val("");
					$("#DescripcionPartida").val("");					
					cucopsDisponibles();
				}
			});
			$("#btnPartidaMenos10").button().click(function(){});
			$("#btnPartidaMas10").button().click(function(){
			});						
			
			$("#cucopDisponible").change(function () {				
				$("#cIdCABM").val("");
				cucopsDisponibles();				
			});
			
			$("#btnPartidaSiguiente").attr("disabled", true);	
			
			//elimina del consolidado
			$("#eliminaConsolidado").button().click(function(){
				queryFormPost("eliminaSolicitudConsolidado", {async:false});
				$("#cAccion").val("ELIMINA_CONSOLIDADO");
				$("#cIdDocumento").val($("#cIdConsolidado").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				window.location="Consolidado.jsp?tab=5";	 
			});
			
			$("#activaApartado").button().click(function(){
				 queryFormPost("habilitaApartado",{async:false});
				 swal("Se han habilitado las pestañas de presupuesto.",{icon:"info",button: "Cerrar"});
			});
		});//Fin del document ready
		
			///////////////////////CLICK EN EL TR DE tblCucops////////////////////////////////////////
		$('#tblCucops tr').live('click', function() {   
			if ($("#nIdEstadoCon").val() != "2" && $("#nIdEstadoCon").val() != "3" && (roles.indexOf("ADMIN_RECMAT")>=0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1 
				|| $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val() )) {
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');  
			}
		});
			///////////////////////CLICK EN EL TR DE tblResumenpartidas////////////////////////////////////////						
		$('#tblResumenpartidas tr').live('click', function(event) {			
				$($("#tblResumenpartidas").dataTable().fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});
				$(event.target.parentNode).addClass('row_selected');
				var aTrs = $('#tblResumenpartidas').dataTable().fnGetNodes();           
				for ( var i=aTrs.length ; i>=0; i-- )     
				{         
					if ( $(aTrs[i]).hasClass('row_selected') )         
					{							
						var nTr = $('#tblResumenpartidas').dataTable().fnGetData(aTrs[i]);
						
						// $("#nIdLineaConsolidado").val(nTr[2]);
						 $("#nIdLineaConsolidado").val(nTr[0]);
						 
						// $("#numPartidaRes").val(nTr[2]);
						 
						  $("#numPartidaRes").val(nTr[0]);
						  
						// $("#descripcion_partidaRes").val(nTr[0]);
						 $("#descripcion_partidaRes").val(nTr[1]);								 
						//var lineaConsol =nTr[2];
						var lineaConsol =nTr[0];
													
						// $("#num_cambs").val(nTr[1]);
						  $("#num_cambs").val(nTr[2]);
						cargaSolicitudLineaResumen(lineaConsol);							
					}						
				}		
		});
			
		function cargaResumenpartidas(){
			var campos = ""; 
			$("#mensajePartida").css("visibility", "hidden");
			var visible='<%=EliminaPartida%>';
			if (visible=="false"){
				var deshabilitacion = "disabled='disabled'";
			}else{
			var deshabilitacion ="";
			if (!(roles.indexOf("ADMIN_RECMAT")>=0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1 || $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val()) ||
				($("#nIdEstadoCon").val() == "2" || $("#nIdEstadoCon").val() == "3")) {
					deshabilitacion = " disabled='disabled'";
				}
			}
				 					
			if((roles.indexOf("ADMIN_RECMAT")>=0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1 || $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val())){
				campos = $("#cIdConsolidado").val();
			}
			var optionsTem = "", options = "";
			$.getJSON("../../catalogos/SelectJson.jsp", {Tabla: "UNIDADMEDIDA", ajax: false, async: false},
				function(data){
					for(var i=0; i < data.length; i++)
						options+="<option value='"+data[i].Col0+"' #"+data[i].Col0+"##>"+data[i].Col1+"</opcion>";
				});
				$('#tblResumenpartidas').dataTable().fnClearTable();	
				$('#tblResumenLineasPartidas').dataTable().fnClearTable();
				rpTable =$('#tblResumenpartidas').dataTable({  
				    	sScrollX: "100%",
						//sScrollXInner: "440%",
						bJQueryUI: true,
						bScrollCollapse: true,
						bDestroy: true,
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
						bJQueryUI: true,
						bServerSide: true,
						sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mConsolidadoConsultaLineasConsolidado('"+campos+"')",						
						aaSorting: [[ 0, "asc" ]] ,
						aoColumns: [
							{ sName: "nIdLineaConsolidado" },
							{ sName: "cDescripcion"},
							{ sName: "cIdCABM"},
							{ sName: "unidadMedida"},						
							{ sName: "nCantidadLineas"},
							{ sName: "mCantidadLinea"},
							{ sName: "mMontoLinea"},
							{ sName: "mMontoLineaIVA"},
							{ sName: "apartado"},
							{ sName: "eliminar"}
													
						],
						
        
							fnRowCallback: function( nRow, aData, iDisplayIndex ){
								optionsTem = options;
								optionsTem = optionsTem.replace("#"+aData[3]+"##","selected");
								optionsTem = optionsTem.replace(optionsTem.substring(optionsTem.indexOf("#"),optionsTem.indexOf("##")+2),"");
								$("td:eq(3)", nRow).html("<select id='unidadMedida"+iDisplayIndex+"'>"+optionsTem+"</select>");
								$("#unidadMedida"+iDisplayIndex).val(aData[3]);
								
								return nRow;
							}    		
				});	
		
		}
		function initTableResumenLineasPartidas(){
			oTableResumenLineasPartidas=$("#tblResumenLineasPartidas").dataTable({         					 				
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
				}
			});	
		}
		function cargaSolicitudLineaResumen(LinCon){
				var str_lineaConsol = LinCon;
				var szWhere = "";
				var campos = ""; 					
				var deshabilitacion = "";
				if (!(roles.indexOf("ADMIN_RECMAT")>=0||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1 || $("#cIdUsuarioCreacion").val() == $("#cIdUsuario").val()) ||
					($("#nIdEstadoCon").val() == "2" || $("#nIdEstadoCon").val() == "3")) {
					deshabilitacion = " disabled='disabled'";
				}
				var a = '';
				var b = '';
				campos = $("#cIdConsolidado").val()+"','"+str_lineaConsol;	
				$("#tblResumenLineasPartidas").dataTable().fnClearTable();
				oTableResumenLineasPartidas=$("#tblResumenLineasPartidas").dataTable({         					 				
					bScrollCollapse: true,
	        		bInfo: false,
					sScrollX: "100%",
					bAutoWith: true,
					bJQueryUI: true,
					bRetrive : true,
					bDestroy : true,
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
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=dbo.fn_mConsolidadoConsultaLineasConsolidadoDetalle('"+campos+"')",
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{ sName: "cIdSolicitud" },
						{ sName: "nIdLineaSolicitud"},
						{ sName: "cIdCABM"},
						{ sName: "cDescripcion"},
						{ sName: "nCantidad"},
						{ sName: "mPrecioUnitario"},
						{ sName: "mSubTotal"},
						{ sName: "apartadoLinea"},
						{ sName: "eliminarLinea"}
						]
				});	
		}
		function eliminaLineaPartida(a,b,str_lineaConsol){			
			$("#cEjercicio").val();
			$("#cIdConsolidado").val();
			$("#cIdSolicitud").val(a);
			$("#nIdLineaSolicitud").val(b);
			$("#nIdLineaConsolidado").val(str_lineaConsol);
			$("#Accion").val('1');			
			
			queryFormPost("sp_mConsolidadoInsertaDetalleLineaConsolidadoCreate",{async : false});
			cargaResumenpartidas();
			cargaSolicitudLineaResumen(str_lineaConsol);
			$("#tblResumenpartidas").dataTable().fnClearTable();
			//$("#btnPartidasResumen").button().click();			
		}
		function eliminaPartida(nIdLineaConsolidado,nIdLineaSolicitud){			
			var str_nIdLineaConsolidado = nIdLineaConsolidado;
			var str_nIdLineaSolicitud = nIdLineaSolicitud ;			
			$("#cIdConsolidado").val();
            $("#cIdSolicitud").val('');
            $("#nIdLineaConsolidado").val(str_nIdLineaConsolidado);
            $("#nIdLineaSolicitud").val(str_nIdLineaSolicitud);            
			queryFormPost("sp_mConsolidadoEliminaLineasConsolidadoCreate",{async : false});
			$("#tblResumenpartidas").dataTable().fnClearTable();
			cargaResumenpartidas();
			guardaBitacora("ELIMINA_PARTIDA",$("#cIdConsolidado").val());
			//$("#btnPartidasResumen").button().click();
		}
		function checkRequerido(o, n) {
			var sTemp = $.trim(o.val());
			o.val(sTemp);
			if (sTemp.length == 0) {
				o.addClass("ui-state-error");
				updateTipsDlg(n + " es un dato requerido.");
				o.focus();
				return false;
			}else{
				return true;
			}
		}
		function updateTipsDlg(t) {
			tips.text(t);
			swal(t,{icon:"info",button: "Cerrar"});
		}
		function exportarConsolidado(){
			var cIdCons = $("#cIdConsolidado").val();
			servletPath = "../../servlet/CatalogosCSV?" + "rn=rptmConsolidadoCompranet.csv" + "&cIdConsolidado=" + cIdCons;
			window.open(servletPath, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
		}
		function openARCH(ext){
			window.open("../../servlet/SeguridadCatalogosMateriales?"
			        + "catalogo=REPORTE"
					+ "&accion=run"
					+ "&rn=ReporteConsolidado.jasper"
					+"&formato="+ext
					+ "&cIdCons=" + $("#cIdConsolidado").val(), 'Procesando', 'status=1, width=500px, height=100px, left=150px');
		}
		function openCSV(){
			window.open(
					"../../servlet/CatalogosCSV?"
					+ "rn=ReporteConsolidado.jasper"
					+ "&cIdCons=" + $("#cIdConsolidado").val(),
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768");
		}	
		function aprobarConsolidado() {
			var imgAprobar='<%=imgAprobar%>';
			if (imgAprobar==0){
				if($("#tipoSolIgualTipoConsol").val()==0){
					swal("Este consolidado no lo puedes aprobar por que el tipo de consolidado no corresponde con el tipo de solicitud o no has agregado las lineas del consolidado.",{icon:"info",button: "Cerrar"});
					return;
				}
				queryFormPost("numLinTotalRead",{async : false});
				if ($("#numLinTot").val() > 0) {
					///////////////////////////////////////////////PRECOMPROMISO////////////////////////////////////////////////////////////
					if(esRegularizacion()){
						$("#nIdEstado").val("2"); //aprobada
						queryFormPost("mConsolidadoUpdate", { async:false });
						
						swal({
							title: "",
							text: "El consolidado ha sido aprobado.",
							icon: "info",
							buttons: {
								confirm : "Cerrar"
								},
							}).then((continuar) => {
								//Bitácora
								$("#cAccion").val("APRUEBA_CONSOLIDADO");
								$("#cIdDocumento").val($("#cIdConsolidado").val());
								queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
								window.location = "Consolidado.jsp?tab=5";
						});
						
					}else{
					//	if(validaVigencia()){
						//valida que haya al menos una requisicion con apartado para generar precompromiso
						queryFormPost("countApartados", { async : false});
						if($("#documentosApartado").val()==0){
							$("#nIdEstado").val("2"); //aprobada
							queryFormPost("mConsolidadoUpdate", { async:false });
							swal("El consolidado ha sido aprobado.",{icon:"info",button: "Cerrar"});
							//Bitácora
							$("#cAccion").val("APRUEBA_CONSOLIDADO");
							$("#cIdDocumento").val($("#cIdConsolidado").val());
							queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
							window.location = "Consolidado.jsp?tab=5";
						}else{
							var grupo=<%=bGrupo%>;
							if(grupo){
								guardarPrecompromiso();	
								queryFormPost("mConsolidadoRead", {async : false});	
								queryFormPost("mConsolidado_LabelRead", {async : false});
							}else{
								swal("El módulo de presupuesto se encuentra inhabilitado",{icon:"warning",button: "Cerrar"});
							}
						}
					}
				}else{
					swal("Necesita agregar al menos una línea para aprobar el Consolidado.",{icon:"info",button: "Cerrar"});
				}
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
					if($("#nIdEstado").val()==("2")){
						if(esRegularizacion()){
							$("#nIdEstado").val("1"); //capturada
							queryFormPost("mConsolidadoUpdate", { async:false });
							//Bitácora
							$("#cAccion").val("DEVUELVE_CONSOLIDADO");
							$("#cIdDocumento").val($("#cIdConsolidado").val());
							queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
							window.location = "Consolidado.jsp?tab=5";
						}else{
							if ($("#documentoAplicado").val()==0){ //cancelado
								$("#nIdEstado").val("1"); //capturada
								queryFormPost("mConsolidadoUpdate", { async:false });
								//Bitácora
								$("#cAccion").val("DEVUELVE_CONSOLIDADO");
								$("#cIdDocumento").val($("#cIdConsolidado").val());
								queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
								habilitaPestanas();
								window.location = "Consolidado.jsp?tab=5";
							
							}else{
								var grupo=<%=bGrupo%>;
								if(grupo){
									queryFormPost("vigencias",{async:false});
								    if($("#enEspera").val()==0){ // no existen vigencias en espera
								    	swal({
								    		title: "Está seguro que desea devolver el Consolidado, el preCompromiso sera devuelto al apartado, esta acción no se puede deshacer?",
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
								    			//Bitácora
												$("#cAccion").val("DEVUELVE_CONSOLIDADO");
												$("#cIdDocumento").val($("#cIdConsolidado").val());
												queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
												//hacer la devolucion del precompromiso al apartado
												$.ajax({url: '../../servlet/ConsolidadoServlet?nFolioPrecompromiso='+$("#consecutivoPrecompromiso").val()+"&cEjercicio="+$("#cEjercicio").val()+"&partidas=1" , type:'post' , async: false,data:'operacion=3', dataType: 'json', success: 
													function(j){
														mensajeAp=j[0].Contable1;
														swal(mensajeAp,{icon:"info",button: "Cerrar"});
														queryFormPost("mConsolidado_LabelRead", {async : false});
														habilitaPestanas();	
													}
												});
												window.location = "Consolidado.jsp?tab=5";
								    		}
								    	});
								    }else{
								    	swal("No es posible devolver el consolidado, se encuentra una solicitud de ampliación de vigencia pendiente.Contacte a su administrador",{icon:"info",button: "Cerrar"});
								    }
								}else{
									swal("El módulo de presupuesto se encuentra inhabilitado",{icon:"info",button: "Cerrar"});
								}
							}
						}
					}else{
						swal("El estado del consolidado no permite realizar la operacion solicitada.",{icon:"info",button: "Cerrar"});
					}
				}else
					swal("El Consolidado "+$("#cIdConsolidado").val()+" está asociado a un Procedimiento. Para devolverlo, primero es necesario eliminarlo del Procedimiento.",{icon:"info",button: "Cerrar"});
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
				
				if ($("#nIdEstado").val()==2){
					swal("No es posible anular el consolidado, su estatus no lo permite.",{icon:"info",button: "Cerrar"});
					return;
				}
				if ($("#tieneProcedimiento").val() == "0") {
					if (confirm("¿Está seguro que desea anular el Consolidado "+ $("#cIdConsolidado").val() +" ? \n Esta acción no puede revertirse y todas sus lineas serán liberadas.")) {
						$("#nIdEstado").val("3"); //anulada
						queryFormPost("mConsolidadoUpdate", { async:false });
						queryFormPost("sp_mConsolidadoAnulaConsolidado", {async:false});
						//Bitácora
						$("#cAccion").val("ANULA_CONSOLIDADO");
						$("#cIdDocumento").val($("#cIdConsolidado").val());
						queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
						window.location = "Consolidado.jsp?tab=5";
					}
				}else
					swal("El Consolidado "+$("#cIdConsolidado").val()+" está asociado a un Procedimiento. Para devolverlo, primero es necesario eliminarlo del Procedimiento.",{icon:"info",button: "Cerrar"});
			
			}else{
				swal("No tiene permisos para realizar esta acción.",{icon:"info",button: "Cerrar"});
			}
		}
			
		function deshabilitarCampos() {
			document.getElementById("btnCucopspreselTodas").setAttribute("disabled", "disabled");
			document.getElementById("btnCucopsAgregar").setAttribute("disabled", "disabled");
			document.getElementById("btnPartidaGuardar").setAttribute("disabled", "disabled");				
			document.getElementById("btnPartidaModificar").setAttribute("disabled", "disabled");
			document.getElementById("btnPartidaGuardarUnoAUno").setAttribute("disabled", "disabled");
			document.getElementById("btnGuardarLineasConsolidado").setAttribute("disabled", "disabled");
		}
			
		function cucopsDisponibles(){				
			querySelectPost("ConsolidadoLineasCucopsDisponiblesRead", "cucopDisponible", {async : false});
			$("#mensajePartida").css("visibility", "hidden");				
			$('#tblCucops').dataTable().fnClearTable();
			oTable1=$('#tblCucops').dataTable({         
				bScrollCollapse: true,
				bInfo: false,
				sScrollX: "100%",
				bAutoWith: true,
				bJQueryUI: true,
				bRetrive : true,
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
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				bAutoWidth: true     		
			});
			var szWhere = "";
			var campos = "";
			var camb ="";								
			if($("#cucopDisponible").val()== null){
				camb = $("#cIdCABM").val();
			}else{
				if($("#cIdCABM").val()!= null&& $("#cIdCABM").val()!= ""){										
					camb = $("#cIdCABM").val();
				}else{											
					camb = $("#cucopDisponible").val();
				}									
			}
			campos = $("#cIdConsolidado").val()+"','"+camb;					
                  var szTabla = "CONSOLIDADOLINEASSOLICITUDDISPONIBLES";                                                                                         
			$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, Campos:campos, Order:" order by 1 desc ", ajax: 'false'}, function(j){
				for (var i = 0; i < j.length; i++) 
  					{  
  					    $('#tblCucops').dataTable().fnAddData( [
						j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3,j[i].Col4,j[i].Col5
					]); 
  					    $("#cIdSolicitud").val(j[i].Col0);
  					    $("#nIdLineaSolicitud").val(j[i].Col1);
				}
         	});
		}
		function irPartidaConsolidada(){									
				$("#btnPartidaSiguiente").attr("disabled", false);									
				var partida_var = $("#partidaConsolidado").val();
				if(partida_var ==1){
					$("#btnPartidaAnterior").attr("disabled", true);	
				}else{
					$("#btnPartidaAnterior").attr("disabled", false);
				}
				if($("#partidaConsolidado").val() != 0){											
					$("#nIdLinea").val(partida_var);
				}else{										
					$("#nIdLinea").val($("#numLinTot").val());
				}
				querySelectPost("ConsolidadoLineasCucopsDisponiblesRead","cucopDisponible",{async : false});				
				queryFormPost("fn_mConsolidadoConsultaUNALineaConsolidadoDetalladaSigRead", {async : false});
				var i=0;
				for(i=0;i<document.getElementById("cucopDisponible").options.length;i++){										
					if($.trim($("#cambs_desc").val().split(" ")[0]) == $.trim(document.getElementById("cucopDisponible").options[i].value)){
							document.getElementById('tbcambsSig').style.display = 'block';	
							$("#cucopDisponible").val(null);
							$("#nIdLineaConsolidado").val($("#numPartida").val());
							querySelectPost("ConsolidadoLineasCucopsDisponiblesEnLineasRead","cucopDisponible",{async : false});								
							cucopsDisponibles();
							document.getElementById('tbcambs').style.display = 'none';
							setTimeout("$('#partidaConsolidado').val('0')",500);							
							return false;
					}
				}					
				cucopsDisponibles();
				$("#numPartida").val($("#nIdLinea").val());				
				document.getElementById('tbcambsSig').style.display = 'block';
				$("#cucopDisponible").val(null);
				document.getElementById('tbcambs').style.display = 'none';					
				setTimeout("$('#partidaConsolidado').val('0')",500);
		}
			
		function validarDesPartida(e,id) {
			tecla = (document.all) ? e.keyCode : e.which;
			if (tecla==8) return true;
			patron =/[\w\d\s\\.\/\_\ñ\Ñ]/;					
			te = String.fromCharCode(tecla);																				
			var v = document.getElementById(id).value.replace("ñ", "n").replace("Ñ", "N");					
			document.getElementById(id).value = v;					
			return patron.test(te);
		}
			
		function validarCampoDescp(a){
			var v = document.getElementById(a).value.replace("ñ", "n").replace("Ñ", "N");					
				document.getElementById(a).value = v;				
		}
		function fnGetSelected( oTableLocal ) {     
			var aReturn = new Array();     
			var aTrs = oTableLocal.fnGetNodes(); 
			var flag=false;          
			for ( var i=0 ; i<aTrs.length ; i++ )     
			{         
				if ( $(aTrs[i]).hasClass('row_selected') )
				{        
					flag=true;     
					//aReturn.push( aTrs[i] );         
				}
			}     
			return flag; 
		}
		////////////////////////////////////////Guarda precompromiso//////////////////////////////////77777		
		function guardarPrecompromiso(){	
		     var mensajeAp="";
			//Genera un nuevo folio en caso de que no lo tenga
			queryFormPost("validaAplicacionContable",{async:false});
			if ($("#aplica").val()!=1){
				$("#nFolioPreCompromiso").val(0);
			}
			if($("#nFolioPreCompromiso").val()==0){
				$.ajax({url: '../../servlet/ConsolidadoServlet' , type:'post' , async: false, data:'operacion=1', dataType: 'json', success: guardaFolio});
			}
			if($("#nFolioPreCompromiso").val()==0)
				return -1;
			$("#cCentroContable").val( "<%=cCentroContable%>");
			//Guarda en una tabla auxiliar el encabezado del compromiso
			getNextSequenceVal({seqName: "PR-" + "<%=cCentroContable%>", async: false, callback: setSequenceVal});
			var nMes = "<%=today%>";
			var nMes = nMes.substring(5, 7 ) ;
			$("#fCarga").val( "<%=today%>" ); 
			$("#fAplicacion").val("<%=today%>" ); 
			$("#cIdContrato").val($("#cIdConsolidado").val()); // id del consolidado
			$("#cTipoContrato").val( "DI" ); 
			$("#cRamo").val( "<%=cRamo%>" );
			$("#cUnidadResponsable").val( "<%=cUR%>" );
			$("#caNoPreCompromiso").val( vcaNoCompromiso );
			$("#nEnviadoSICOP").val("0");
			$("#nMes").val( nMes ); 
			$("#fVigencia").val( $("#vigenciaPrecomMateriales").val());

			 //Genera y actualiza Encabezado
			queryFormPost('tPreCompromisoEncabezadoCreate', {async: false });
			queryFormPost('tPreCompromisoDetalleCreate', {async: false });
			aplicaContable();
			//llenaPreCompromisoDetalle();
    }
    //////////////////// termina funcion de guardar precompromiso//////////////////////////////////////
    function guardaFolio(j){
		var folioPre=-1;
		var folioCaso=-1;
    	folioPre=j[0].Folio1;
    	folioCaso=j[0].Folio2;
        if(folioPre==-1){
      		swal("Ha ocurrido un error al crear el caso, contacte a su soporte.",{icon:"info",button: "Cerrar"});
      		return -1;
        }else{
     	   $("#nFolioPreCompromiso").val(folioPre);
     	   $("#folioCasoPreCompromiso").val(folioCaso);
     	   nFolioPreCompromiso= $("#nFolioPreCompromiso").val();
     	   
     	}
	}
	
	function setSequenceVal(seqValue) {
		seqValue = "000000" + seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		vcaNoCompromiso = $("#cCentroContable").val() + "PR" + $("#cEjercicio").val() + seqValue;
	}
	
	function llenaPreCompromisoDetalle(){
		var campos=$("#cIdConsolidado").val()+"','"+$("#nFolioPreCompromiso").val()+"','"+$("#cCentroContable").val();		
			$("#apartadoConsolidado").dataTable({         					 				
				sScrollX: "100%",
				 sScrollXInner: "97%",
				 bScrollCollapse: true,
				 bDestroy: true,
				 iDisplayLength: 70,
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
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=dbo.fn_mPrecompromisoConsolidado('"+campos+"')",
				aaSorting: [[ 1, "asc" ]] ,
				bAutoWidth: false,
				aoColumns: [
					{ sName: "nFolio" },
					{ sName: "nDocRenglon"},
					{ sName: "ep"},
					{ sName: "cEvento"},
					{ sName: "mImporte"},
					{ sName: "mImporteNegativo"},
					{ sName: "mes"},
					{ sName: "centroContable"}
					],	
					fnInitComplete: function(oSettings, json) {
						aplicaContable();
					}
			});	
			$("#apartadoConsolidado").attr('visible', false);
	}

			
	function generaPreCompromisoDetalle(){
		$("#nFolioPreCompromiso").val(nFolioPreCompromiso);
		var aTrs = $('#apartadoConsolidado').dataTable().fnGetNodes();	
	        for ( var i=aTrs.length-1 ; i>=0; i-- ){
				var aData = $('#apartadoConsolidado').dataTable().fnGetData( aTrs[i]);
				$("#nDocRenglon").val(aData[1]);
				$("#EP").val(aData[2]);
				$("#cEvento").val(aData[3]);
				$("#mImporte").val(aData[4]);
				$("#mImporteNegativo").val(aData[5]);
				$("#nMesD").val(aData[6]);
				
				queryFormPost("tPreCompromisoDetalleCreate", {async: false });
				//queryFormPost("tPreCompromisoDCreatetmpMateriales", {async: false });	
			}
	}

	function habilitaPestanas(){
		queryFormPost("mValidaPrecompromiso",{async:false});
		if(esRegularizacion()){
			$("#presupuestoConsolidado").css("display", "none");
			$("#preCompromisoConsolidado").css("display", "none");	
			$("#ampliacionPrecompromiso").css("display", "none");
			$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
			document.getElementById('trBotones').style.display = 'block';
			document.getElementById('trSalir').style.display = 'none';
			if ($("#nIdEstado").val()==2  ){ 
				$("#imgAprobarPartidas").css("display", "none");
				$("#imgAnularPartidas").css("display", "none");
			}
			if ($("#nIdEstado").val()==1  ){ 
				$("#imgDevolverPartidas").css("display", "none");
				
			}
		}else{
			if($("#tieneProcedimiento").val() == '' || $("#tieneProcedimiento").val() == 'N/A')
				$("#tieneProcedimiento").val('0');
			if ($("#nIdEstado").val()==2 && $("#tieneProcedimiento").val()==0 ){ // validar que el consolidado este aprobado pero que no tenga un procedimiento
				if($("#documentoAplicado").val()==0){ // precompromiso cancelado
					$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
					$("#ampliacionPrecompromiso").css("display", "none");
					document.getElementById('trBotones').style.display = 'block';
					document.getElementById('trSalir').style.display = 'none';		
					$("#presupuestoConsolidado").css("display", "block");
					$("#preCompromisoConsolidado").css("display", "block");		
				}else{ //aplicado contablemente
					// si el precompromiso aplicado cubre el monto del consolidado ya no se muestran las pestañas de presupuesto
					
					if (cubreMonto()){ // no lo cubre, se muestran las pestañas
						$("#ampliacionPrecompromiso").css("display", "block");
						$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
						$("#presupuestoConsolidado").css("display", "block");
						$("#preCompromisoConsolidado").css("display", "block");	
						document.getElementById('trBotones').style.display = 'block';
						document.getElementById('trSalir').style.display = 'none';
					}else{ // se cubre el monto del consolidado con el del precompromiso
						// si el precompromiso se aprobo del disponible, mostrar las pestañas a manera de consulta
						$("#cEventoFlujo").val("");
						queryFormPost("validaFlujo", {async:false});
						if($("#cEventoFlujo").val()=="DISP_PRECOMMAT"){
							$("#ampliacionPrecompromiso").css("display", "block");
							$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
							document.getElementById('trBotones').style.display = 'block';
							document.getElementById('trSalir').style.display = 'none';
							$("#presupuestoConsolidado").css("display", "block");
							$("#preCompromisoConsolidado").css("display", "block");
						}else{
							$("#ampliacionPrecompromiso").css("display", "block");
							$("#presupuestoConsolidado").css("display", "none");
							$("#preCompromisoConsolidado").css("display", "none");
							$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
							document.getElementById('trBotones').style.display = 'block';
							document.getElementById('trSalir').style.display = 'none';
						}	
						
					}					
				}
			
			}else{ // esta aprobado y tiene procedimiento 
				if($("#documentoAplicado").val()==0){ // precompromiso cancelado
					$("#presupuestoConsolidado").css("display", "none");
					$("#preCompromisoConsolidado").css("display", "none");
					$("#ampliacionPrecompromiso").css("display", "none");	
					$("#vigenciaRequisicionesConsolidadas").css("display", "none");					
				}else{ //aplicado contablemente
					queryFormPost("validaFlujo", {async:false});
					if($("#cEventoFlujo").val()=="DISP_PRECOMMAT"){
						$("#ampliacionPrecompromiso").css("display", "block");
						$("#presupuestoConsolidado").css("display", "block");
						$("#preCompromisoConsolidado").css("display", "block");
						$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
						document.getElementById('trBotones').style.display = 'none';
						document.getElementById('trBotonesReportes').style.display = 'none';
						document.getElementById('trSalir').style.display = 'block';
					}else{
						$("#ampliacionPrecompromiso").css("display", "block");
						$("#presupuestoConsolidado").css("display", "none");
						$("#preCompromisoConsolidado").css("display", "none");
						$("#vigenciaRequisicionesConsolidadas").css("display", "none");	
						document.getElementById('trBotones').style.display = 'block';
						document.getElementById('trSalir').style.display = 'none';
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
				document.getElementById('trBotones').style.display = 'block';
				document.getElementById('trSalir').style.display = 'none';
			}
			if($("#nIdEstado").val()==2){
				document.getElementById('imgAprobarPartidas').style.display = 'none';
				document.getElementById('imgAnularPartidas').style.display = 'none';
			}
			if($("#nIdEstado").val()==3){
				document.getElementById('trBotonesReportes').style.display = 'none';
			}
		}
	}
		
	function validaModificarConsolidado(){
		if(($('#cIdUsuarioCreacion').val()== $("#usuarioLogin").val())||( roles.indexOf('ADMIN_RECMAT') >= 0 ||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1)){
			return true;
		}
		else{
			swal("El usuario no tiene permiso para realizar esta acci\xF3n.",{icon:"info",button: "Cerrar"});
			return false;
		}
	}
	
	function esRegularizacion(){
		var regularizacion=false;
		var apartables = [ "CC", "CS","CT" ];
		if ($.inArray($("#cIdTipoConsolidado").val(), apartables) == -1){ // no traen apartado
			regularizacion=true;
		}else
			regularizacion=false;
		return regularizacion;
	}
	function aplicaContable(){
		//generaPreCompromisoDetalle();	
		
		//aplicacion contable
		$.ajax({url: '../../servlet/ConsolidadoServlet?nFolioPrecompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val()+"&folioCasoPreCompromiso="+$("#folioCasoPreCompromiso").val()+"&partidas=1", type:'post' , async: false,data:'operacion=2', dataType: 'json', success: 
			function(j){
				mensajeAp=j[0].Contable1;
				swal(mensajeAp,{icon:"info",button: "Cerrar"});
				//Bitácora
				$("#cAccion").val("APRUEBA_CONSOLIDADO");
				$("#cIdDocumento").val($("#cIdConsolidado").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				window.location = "Consolidado.jsp?tab=5";		
				
			}
		});
		return mensajeAp;
	}
	function guardarLineasConsolidado(){
		var aTrs = $("#tblResumenpartidas").dataTable().fnGetNodes();
		var aData;
		var descripcion = "";
		var actualizaciones = 0;
		for(var i=0; i<aTrs.length; i++){
			aData = $("#tblResumenpartidas").dataTable().fnGetData(aTrs[i]);
			// GUARDA UNIDAD DE MEDIDA idUnidadMedida
			$("#nIdLineaConsolidado").val(aData[0]);
			if($("#unidadMedida"+i).val() != aData[3]){			
				$("#cIdUnidadMedida").val($("#unidadMedida"+i+" option:selected").val());
				queryFormPost("updateUnidadMedida", {async: false});
				//$("#tblResumenpartidas").dataTable().fnUpdate($("#unidadMedida"+i).val(), i, 3);
				actualizaciones++;
			}
		}
		if(actualizaciones > 0)
			swal("La actualización se realizo correctamente.",{icon:"info",button: "Cerrar"});
		else
			swal("No se realizo ningun cambio.",{icon:"info",button: "Cerrar"});
	}
	function cubreMonto(){
		queryFormPost("fn_mConsolidadoCalculaMontoConIVARead", {async:false});
		if(parseInt(quitaFmt($("#lblApartado").val()),10)<parseInt(quitaFmt($("#MontoConIVA").val()),10)){			
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
			
			<input id="cDescripcion" name="cDescripcion" type="hidden" size="2000">
			<input id="nIdAlcance" name="nIdAlcance" type="hidden" size="2">
			<input id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" type="hidden" size="10">
			<input id="cIdEntidadContable" name="cIdEntidadContable" type="hidden" size="4">
			
			
			<input id="cIdTipoSolicitud" name="cIdTipoSolicitud" type="hidden" size="10">
			<input id="cIdUnidadEjecutoraSolicitud" name="cIdUnidadEjecutoraSolicitud" type="hidden" size="3">
			<input id="nIdConsecutivoSolicitud" name="nIdConsecutivoSolicitud" type="hidden" size="10">
			<input id="cIdSolicitud" name="cIdSolicitud" type="hidden" size="10">
			
			<input id="cIdTipoConsolidadoL" name="cIdTipoConsolidadoL" type="hidden" size="10">
			<input id="cIdUnidadEjecutoraConsolidadoL" name="cIdUnidadEjecutoraConsolidadoL" type="hidden" size="3">			
			
			<input id="nIdConsecutivoConsolidadoL" name="nIdConsecutivoConsolidadoL" type="hidden" size="10">			
			
			<input id="cIdTipoLinea" name="cIdTipoLinea" type="hidden" size="10">
			<input id="cIdUnidadEjecutoraLinea" name="cIdUnidadEjecutoraLinea" type="hidden" size="3">
			<input id="nIdLineaSolicitud" name="nIdLineaSolicitud" type="hidden" size="10">
			<input id="nIdConsecutivoLinea" name="nIdConsecutivoLinea" type="hidden" size="10">
			<input id="str_Consulta" name="str_Consulta" type="hidden" size="1">
			<input id="nIdLineaConsolidado" name="nIdLineaConsolidado" type="hidden" size="1">
			<input id="cIdCABM" name="cIdCABM" type="hidden" size="20">
			<input type="hidden" id="imgEstado" name="imgEstado"/>			
			<input id="Accion" name="Accion" type="hidden" size="3">
			<input id="num_cambs" name="num_cambs" type="hidden" size="3">
			
			<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%=usuario.getU_UR()%>" />
			<input type="hidden" name="cIdUnidadEjecutoraConsolidado" id="cIdUnidadEjecutoraConsolidado" value="<%=cIdUnidadEjecutora%>" />
			<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
			<input type="hidden" name="R_NOMBRE" id="R_NOMBRE" />
			<input id="nIdEstadoCon" name="nIdEstadoCon" type="hidden" size="2">			
			<input name="cIdConsolidado" id="cIdConsolidado" type="hidden"/>			
			<input name="tieneProcedimiento" id="tieneProcedimiento" type="hidden">
			<input name="cAccion" id="cAccion" type="hidden">
			<input name="cIdDocumento" id="cIdDocumento" type="hidden">
			<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuario.getLogin()%>"/>
			<input name="cIdUnidadMedida" id="cIdUnidadMedida" type="hidden">

			
			
			
			<!-- Campos para precompromiso -->
			<input type="hidden" id="documentosApartado" name="documentosApartado">
			<input type="hidden" id="nFolioPreCompromiso" name="nFolioPreCompromiso" value="0">
			<input type="hidden" name="vigenciaPrecomMateriales" id="vigenciaPrecomMateriales" />
			<!-- 
			<input type="text" id="aEjercicioFiscal" name="aEjercicioFiscal"> -->
			<input type="hidden" id="fCarga" name="fCarga">
			<input type="hidden" id="fAplicacion" name="fAplicacion">
			<input type="hidden" id="cIdContrato" name="cIdContrato">
			<input type="hidden" id="cTipoContrato" name="cTipoContrato">
			<input type="hidden" id="cCentroContable" name="cCentroContable">
			<input type="hidden" id="cRamo" name="cRamo" value="16">
			<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable">
			<input type="hidden" id="caNoPreCompromiso" name="caNoPreCompromiso">
			<input type="hidden" id="nEnviadoSICOP" name="nEnviadoSICOP">
			<input type="hidden" id="nMes" name="nMes">
			<input type="hidden" id="fVigencia" name="fVigencia">
			
			
			<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable">
			<input type="hidden" id="cDocumento" name="cDocumento" value="PRECOMMATERIALES">
			<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="">
			<input type="hidden" id="folioCasoPreCompromiso" name="folioCasoPreCompromiso" value="">
			<input type="hidden" name="mesDisponible" id="mesDisponible" value="<%=mesActual%>" />
			<input type="hidden" id="aplicado" name="aplicado">
			<input type="hidden" id="tipoSolIgualTipoConsol" name="tipoSolIgualTipoConsol" value="0">
			
			<!-- DETALLE DEL PRECOMPROMISO -->
			<input type="hidden" id="nDocRenglon" name="nDocRenglon">
			<input type="hidden" id="EP" name="EP">
			<input type="hidden" id="cEvento" name="cEvento">
			<input type="hidden" id="mImporte" name="mImporte">
			<input type="hidden" id="mImporteNegativo" name="mImporteNegativo">
			<input type="hidden" id="nMesD" name="nMesD">
			<input type="hidden" id="consecutivoPrecompromiso" name="consecutivoPrecompromiso">
			<input type="hidden" id="enEspera" name="enEspera">
			
			<!-- habilitar pestanas -->
			<input type="hidden" id="numLineasConsolidadas" name="numLineasConsolidadas">
			<input type="hidden" id="usuarioLogin" name="usuarioLogin">
			<input type="hidden" id="usuarioRoleConsolidado" name="usuarioRoleConsolidado">
			<input type="hidden" id="documentoAplicado" name="documentoAplicado">
			<input type="hidden" id="cEventoFlujo" name="cEventoFlujo">
			<input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE" value=""/>
			<!-- VIGENCIA APARTADOS -->
			<input type="hidden" id="vencido" name="vencido">
			
			<fieldset>
				<legend>Informaci&oacute;n del Consolidado</legend>
				<div id="apartadoConsolidadoTabla" style="display: none">
				<table id="apartadoConsolidado"  >
					<thead>
						<tr>
							<th>folio</th>
							<th>renglon</th>
							<th>ep</th>
							<th>cEvento</th>
							<th>importe</th>
							<th>importeNegativo</th>
							<th>Mes</th>
							<th>centro contable</th>
						</tr>
					</thead>
					</table>
					
					</div>
				<table align="left" cellpadding="2" width="100%">
			    	<tr id="trBotones" style='display:none' align="right">						
						<td align="right" colspan="2">
							<input type="button" class="btnInterfaceCSV ui-button ui-widget ui-state-default ui-corner-all" 	id="imgExportCVS" 		name="imgExportCVS" 	value="Imprimir Compranet"	onclick="exportarConsolidado();" />&nbsp;&nbsp;
                        	<input type="button" class="btnInterfaceAutoriza ui-button ui-widget ui-state-default ui-corner-all" 	id="imgAprobarPartidas" 	name="imgAprobarPartidas" 	value="Aprobar" onclick="aprobarConsolidado();"	/>&nbsp;&nbsp;
                            <input type="button" class="btnInterfaceReturn ui-button ui-widget ui-state-default ui-corner-all" 		id="imgDevolverPartidas" 	name="imgDevolverPartidas" 	value="Devolver" onclick="devolverConsolidado();"	/>&nbsp;&nbsp;
                            <input type="button" class="btnInterfaceCancelar ui-button ui-widget ui-state-default ui-corner-all" 		id="imgAnularPartidas" 	name="imgAnularPartidas" 	value="Anular"	onclick="anularConsolidado();" />&nbsp;&nbsp;
                            <input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	onclick="window.location = 'Consolidado.jsp?tab=2&ses=0';" />&nbsp;&nbsp;
                       	</td>
					</tr>
					<tr id="trSalir" style='display:none'>
						<td align="right" colspan="2"  >
							 <input type="button" class="btnInterfaceCSV ui-button ui-widget ui-state-default ui-corner-all" 	id="imgExportCVS" 		name="imgExportCVS" 	value="Imprimir Compranet"	onclick="exportarConsolidado();" />&nbsp;&nbsp;
							 <input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	onclick="window.location = 'Consolidado.jsp?tab=2&ses=0';" />&nbsp;&nbsp;
						</td>
					</tr>
			    	<tr>
				    	<td align="left" colspan="2">
				    		<input type="text" style="width: 25px;border-width:0; background-color:transparent" id="lblUnidadUsuario" name="lblUnidadUsuario" readonly  value="<%=unidadUsuarioLogeado%>"/>
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
			    		<td align="left" colspan="2">Monto Precomprometido<input type="text" style="width: 700px;border-width:0; background-color:transparent" name="lblApartado" id="lblApartado" readonly /></td>
			    	</tr>
			    	<tr id="trBotonesReportes">
			    		<td align="right" colspan="2">							
							<input type="button" class="btnInterfacePDF ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdPdfReporteProg" 		name="cmdPdfReporteProg" 	value="PDF"		onclick="openARCH('pdf');" />&nbsp;&nbsp;
			    			<input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdxlsReporteProg" 		name="cmdxlsReporteProg" 	value="Excel"	onclick="openARCH('xls');" />&nbsp;&nbsp;
			    			<input type="button" class="btnInterfaceCSV ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdcsvReporteProg" 		name="cmdcsvReporteProg" 	value="CSV"		onclick="openCSV();" />&nbsp;&nbsp;
							<input type="button" class="btnInterfaceDOC ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdwordReporteProg" 	name="cmdwordReporteProg" 	value="Word"	onclick="openARCH('doc');" />&nbsp;&nbsp;
                        </td>
			    	</tr>
			    </table>
			</fieldset>
			<fieldset>
			<div id="requisicionesVencidas" style="display: none">
				<table>
					<tr>
						<td>Requisiciones con apartado vencido</td>
						<td>
							<select id="apartadoVencido" name="apartadoVencido" style="width: 10em;"></select>
						</td>
					</tr>
					<tr>
					<td>
						<input type="button" name="eliminaConsolidado" id="eliminaConsolidado" value="Eliminar del Consolidado"  class="btnInterfaceBG ui-button ui-corner-all"/>
					</td>
					<td>
						<input type="button" name="activaApartado" id="activaApartado" value="Activar Apartado"  class="btnInterfaceBG ui-button ui-corner-all"/>
					</td>
					
					</tr>
				</table>
			</div>
			</fieldset>
			<fieldset>
				<legend>Requisiciones y L&iacute;neas</legend>
				<div class="tabs" id="tabsA"  >
					<ul>
						<li><a id="PartidasPartidas" href="#tabsA-0">Partidas</a></li>
						<li><a id="PartidasResumen" href="#tabsA-1">Resumen</a></li>
						
					</ul>
					<div id="tabsA-0" align="center">
						<div id="tblconsolidado">
						<fieldset>
						<legend>Partidas</legend>				
						<table border="0" width="100%">				
								<tr>				
									<td align="left" colspan="3">
									<div id="tbcambs" style='display:block'>
										<table>
											<tr>
												<td>CUCOP:&nbsp;
													<select id="cucopDisponible" name="cucopDisponible" style="width: 40em;"></select>
												</td>
											</tr>
										</table>		
									</div>	
									<div id="tbcambsSig" style='display:none'>
										<table>
											<tr>
												<td>CABMS:&nbsp;<input name="cCABM" id="cCABM" type="text" size ="90" style="border: 0px solid black;">													
													<input name="cambs_desc" id="cambs_desc" type="hidden" size ="90" style="border: 0px solid black;">	
													
												</td>
											</tr>
										</table>		
									</div>										
									</td>								
								</tr>
							</table>
							<table id="tblCucops" class="display">
					           <thead>
					                <tr>
					                	<th>Requisicion</th>
					                    <th>Línea</th>
					                    <th>Descripcion</th>
					                    <th>Descripcion Adicional</th>
					                    <th>U.M</th>
					                    <th>Cantidad</th>
					                </tr>
					            </thead>
					        </table>
							<table width="100%">
								<tr>
									<td align="left" colspan="1">Partidas Totales&nbsp;&nbsp; <input name="numLinTot" id="numLinTot" type="text" size ="4" style="border: 0px solid black;"></td>
									<td id="mensajePartida" style="FONT-SIZE: 12pt; Color: red" style="visibility: hidden" align="center" colspan="2">&nbsp;  La Partida <input name="numLinTotPartida" id="numLinTotPartida" type="text" size ="2" style="border: 0px solid black;color: red;text-align: center;">  se agregó correctamente </td>
								</tr>
								<tr>
									<td align="left" >
										<input type="button" name="btnCucopspreselTodas" id="btnCucopspreselTodas" value="Seleccionar Todo"  class="btnInterfaceBG ui-button ui-corner-all"/> 
									</td>
									<td  align="left">
										<input type="button" name="btnCucopsAgregar" id="btnCucopsAgregar" value="Copiar Descripción"  class="btnInterfaceBG ui-button ui-corner-all"/>
									</td>
									<td>
										<input type="button" name="btnPartidaGuardarUnoAUno" id="btnPartidaGuardarUnoAUno" title="Genera una partida por cada linea de solicitud." value="Uno A Uno"  class="btnInterfaceBG ui-button ui-corner-all"/>
									</td>
									
								</tr>
								<tr>
									<td align="left">Descripci&oacute;n:</td>
									<td colspan="2" align="left"><textarea name="DescripcionPartida" readonly="readonly" id="DescripcionPartida" cols="60" rows="5" style="text-align: left" onkeypress="return validarDesPartida(event, this.id)" onblur="validarCampoDescp(this.id);"></textarea><br/><label class="validateTips ui-state-error"></label>
									<input type="hidden" id="DescripcionPartidaAdicional" name="DescripcionPartidaAdicional" value="" />
									</td>
								</tr>
								<tr>									
									<td  align="left" >
										<input type="button" name="btnPartidaGuardar" id="btnPartidaGuardar" value="Agregar"  class="btnInterfaceBG ui-button ui-corner-all"/>
									</td>
								</tr>
								<tr>
									<td align="left" colspan="3">N&uacute;mero de Partida:&nbsp;&nbsp;<input name="numPartida" id="numPartida" type="text" size ="2" style="border: 0px solid black;color: white;text-align: center;font-size: 16pt; background-color: gray;"><input id="nIdLinea" name="nIdLinea" type="hidden" size="4"></td>
								</tr>
								<tr>
									<td align="left" colspan="3">&nbsp;&nbsp;&nbsp;
									<input type="button" name="btnPartidaAnterior" id="btnPartidaAnterior" value="&lt;&lt;"  class="btnInterfaceBG ui-button ui-corner-all"/>&nbsp;&nbsp;&nbsp;
									<input type="button" name="btnPartidaSiguiente" id="btnPartidaSiguiente" value="&gt;&gt;"  class="btnInterfaceBG ui-button ui-corner-all"/>
									Seleccione su Partida: &nbsp;&nbsp;&nbsp;<select id="partidaConsolidado" name="partidaConsolidado" style="width: 30em;" onchange="javascript:irPartidaConsolidada()"></select></td>
								</tr>
							</table>
						</fieldset>
						</div>
					</div>
					<div id="tabsA-1" align="center">
						<div id="tblresumen" >
							<fieldset>
								<legend>Partidas de Consolidado</legend>
									
								<div id="prueba" class="tableConsolidadoResumen">
								<table id="tblResumenpartidas"  class="display" >
						            <thead>
						                <tr>
						                <th>Partida</th>
						                <th>Descripcion</th>
						                <th>CUCOP</th>
						                <th>U.M.</th>
						                <th>L.S</th>
						                <th>Cantidad</th>
						                <th>Total Linea Bruto</th>
						                <th>Total Linea</th>
						                <th>Total Apartado</th>
						                <th>Elimina</th>
						                </tr>
						            </thead>
						        </table>
						        </div>
											
									<table align="left">
									<tr>									
						    			<td align="left">
						    				<input type="button" name="btnGuardarLineasConsolidado" id="btnGuardarLineasConsolidado" value="Guardar" onClick="guardarLineasConsolidado();" class="btnInterfaceBG ui-button ui-corner-all"/>
						    			</td>						    	
									</tr>
									</table>
							</fieldset>
							<fieldset>
								<legend>L&iacute;neas del Consolidado</legend>
								<table border="0" width="100%">
									<tr>
										<td align="left">&nbsp;&nbsp;&nbsp;
											<input type="button" name="btnPartidaModificar" id="btnPartidaModificar" value="Modificar"  class="btnInterfaceBG ui-button ui-corner-all"/>
										</td>
											
										<td align="left">Partida:<input name="numPartidaRes" id="numPartidaRes" type="text" size ="3" style="border: 0px solid black;"></td>	
										<td align="left"><input name="descripcion_partidaRes" id="descripcion_partidaRes" type="text" size ="60" onkeypress="return validarDesPartida(event, this.id)" onblur="validarCampoDescp(this.id);">	</td>
									</tr>
								</table>
								<div id="prueba2" class="tableConsolidadoResumen">
								<table id="tblResumenLineasPartidas" class="display"> 
								 <thead>
						                <tr>
						                	<th>Requisicion</th>
						                    <th>Linea</th>
						                    <th>CUCOP</th>
						                    <th width="10%">Descripcion</th>
						                    <th>Cantidad</th>
						                    <th>Precio U.</th>
						                    <th>Sub Total</th>
						                    <th>Monto Apartado / Linea</th>
						                    <th>Elimina</th>								                    
						                </tr>
						            </thead>
							    </table>
							    </div>
							    <table align="left">	
									<tr>
										<td align="left" style="color: blue" colspan="3">
											Total Consolidado Bruto: $<input name="MontoBruto" id="MontoBruto"  readonly style="border-width:0; background-color:transparent;color: blue">
										</td>										
									</tr>
									<tr>
										<td align="left" style="color: blue" colspan="3">																						  
											Total Consolidado con IVA: $<input name="MontoConIVA" id="MontoConIVA"  readonly style="border-width:0; background-color:transparent;color: blue">										
										</td>
									</tr>
								</table>	
							</fieldset>			
						</div>
					</div>
					
				</div>
			</fieldset>

		</form>	
				
	</body>
</html>
