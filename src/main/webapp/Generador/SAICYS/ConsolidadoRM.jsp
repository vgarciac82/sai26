<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	//String path = request.getContextPath();
	//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
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
						
		var oTable;
		var rpTable;
		////////////////////////////////////INICIO DE FUNCION PRINCIPAL///////////////////////////////////////
		$(document).ready(function() {			
		$(".tabs").tabs();
		$("#aTab1").attr("disabled", true);
		$("#aTab2").attr("disabled", true);
		$("#aTab3").attr("disabled", true);
		////////////CATALOGOS/////////////////////		
		queryFormPost("ejercicioFiscalConsolidadoRead", {async : false});
		querySelectPost("TipoConsolidadoRead", "ctipoConsolidadoC", {async : false});
		querySelectPost("UnidadBusca", "cunidadRMC", {async : false});		
		if(     '<%=usuario.getU_UR().trim()%>'   == 'B01'
				||'<%=usuario.getU_UR().trim()%>' == 'B02' 
				||'<%=usuario.getU_UR().trim()%>' == 'A04'				
				||'<%=usuario.getU_UR().trim()%>' == 'B04'
				||'<%=usuario.getU_UR().trim()%>' == 'B05'
				||'<%=usuario.getU_UR().trim()%>' == 'B06'
				||'<%=usuario.getU_UR().trim()%>' == 'B07' 
				||'<%=usuario.getU_UR().trim()%>' == 'B08'
				||'<%=usuario.getU_UR().trim()%>' == 'B09'
				||'<%=usuario.getU_UR().trim()%>' == 'B010'
				||'<%=usuario.getU_UR().trim()%>' == 'B011'
				||'<%=usuario.getU_UR().trim()%>' == 'B012'
				||'<%=usuario.getU_UR().trim()%>' == 'B013'
				||'<%=usuario.getU_UR().trim()%>' == 'B014'){
			querySelectPost("AlcanceAdminRead", "cAlcanceC", {async : false});
		}else{			
			querySelectPost("AlcanceRead", "cAlcanceC", {async : false});
		}
		///////////DATOS DEL USUARIO///////////////////
		
		$("#cIdUnidadEjecutora").val('<%=usuario.getU_UR().trim()%>');		
		$("#cIdEntidadContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
		$("#cIdUsuarioCreacion").val('<%=usuario.getLogin()%>');
		////////////////////////////	
		
		
		querySelectPost("mCatalogoCapituloRead", "cCapitulo", {async : false});
		querySelectPost("mCatalogoSubPartidaConsolRead", "partida", {async : true});
		
		
		
			///////////////////////BUSCAR CONSOLIDADO////////////////////////////////////////
				$('#tblConsultaConsolidados tr').live('click', function() {
				$("#str_Consulta").val('1');					
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
					var aTrs = $('#tblConsultaConsolidados').dataTable().fnGetNodes();           
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							var nTr = $('#tblConsultaConsolidados').dataTable().fnGetData(aTrs[i]);							
								$("#cIdTipoConsolidado").val(nTr[2]);								
								$("#nIdEstado").val(nTr[0]);
								$("#nIdAlcance").val(nTr[1]);								
								$("#cIdConsolidado").val(nTr[3]);													
								$("#cIdUnidadEjecutora").val($.trim(nTr[3]).split("-")[1]);																
								$("#cAlcance").val(nTr[1]);	
								$("#nIdConsecutivo").val($.trim(nTr[3]).split("-")[2]);
								setTimeout("esperar()", 2000);												
						}     
					}					
				} );
		///////////////////////////////////BOTON GUARDAR CONSOLIDADO/////////////////////////////////////////////////				
		$("#btnGuardarConsolidado").button()
				.click(
						function() {
							
							
							$("#cIdTipoConsolidado").val($('#ctipoConsolidadoC option:selected').val());
							$("#cDescripcion").val($('#cDescripcionC').text());
							var des = $('#cDescripcionC');
							$("#nIdEstado").val('1');
							$("#nIdAlcance").val($('#cAlcanceC option:selected').val());
							
														
							//Validación de los campos							
							var bValid = true;
							allFields = $( []).add(des),
							tips = $(".validateTips");
							tips.text("");
							allFields.removeClass("ui-state-error");
							/*$("input").each(function() {
								var szStyle = "" + $(this).attr('style');
								if (szStyle.indexOf("uppercase") > 1) {
									$(this).val($(this).val().toUpperCase());
								}
							});
							$("input").each(function() {
								var szStyle = "" + $(this).attr('style');
								if (szStyle.indexOf("lowercase") > 1) {
									$(this).val($(this).val().toLowerCase());
								}
							});*/
							bValid = bValid&& checkRequerido(des, "Descripcion");
							if (bValid) {
								queryFormPost("ConsecutivoConsolidadoRead",{async : false});	
								queryFormPost("ConsolidadoCreate",{async : false});								
								$("#mensajePA").css("visibility", "visible");								
								setTimeout("esperar()", 2000);
							}	
						});
		///////////////////////////////////FIN BOTON GUARDAR CONSOLIDADO/////////////////////////////////////////////////
		///////////////////////////////////BOTON GUARDAR CARATULA CONSOLIDADO/////////////////////////////////////////////////
						$("#btnGuardarConsolCaratula").button().click(function(){
							queryFormPost("sp_mConsolidadoModificaConsolidadoCreate",{async : false});
							$("#mensajeConsol").css("visibility", "visible");							
							setTimeout("esperar2()",2000);
      					});
		///////////////////////////////////FIN BOTON GUARDAR CARATULA CONSOLIDADO/////////////////////////////////////////////////		
		///////////////////////////////////BOTON DE SOLICITUDES /////////////////////////////////////////////////						
						$("#btnSolicitudesConsol").button().click(function(){
							//$("#btnSolicitudesConsol").dataTable().fnClearTable();							
							document.getElementById('tblSolicitudes').style.display = '';
							document.getElementById('tblLineas').style.display = 'none';
							
							
							//queryFormPost("sp_mConsolidadoPreseleccionSolicitudes", {async : false});						
							
							
						});
		///////////////////////////////////BOTON BUSCAR SOLICITUD DISPONIBLE PRESELECCION /////////////////////////////////////////////////			
						$("#btnSolicitudesDisppreselBuscar").button().click(function(){						
						$('#tblSolicitudDispPreSel').dataTable().fnClearTable();
						var szWhere = ' \'1\'=\'1\' '; 
      					var capitulo =$('#cCapitulo option:selected').val();      	
      					var partida = $('#partida option:selected').val();
      					partida = $.trim(partida);
      					var idConsol = $('#cIdConsolidado').val();      					      					      					
				    	if(capitulo   != null && capitulo    != '' && capitulo    != '0'){szWhere += '  and substring(cIdSubPartida,1,1) = \''+  capitulo +'\'';}
				    	if(partida    != null && partida     != '' && partida     != '0' && partida     != '2' && partida     != '3' && partida     != '5'){szWhere += '  and cIdSubPartida = \''+partida+'\'';}
				    	//if(idConsol      != null && idConsol       != ''){where += '  and cIdConsolidado       = \''+idConsol+'\'';}				    	
						var campos = "";  					
						campos = $("#cAlcanceC").val()+"','"+$("#cIdTipoConsolidado").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#cIdConsolidado").val();						
                    	var szTabla = "SOLICITUDESDISPPRESEL";                                                                                         
						$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, Campos:campos, Order:" order by 1 desc ", ajax: 'false'}, function(j){
						for (var i = 0; i < j.length; i++) 
    					{  							
    					    $('#tblSolicitudDispPreSel').dataTable().fnAddData( [
								j[i].Col0,
								j[i].Col1,
								j[i].Col2
							]);    					 
						}
		         })  
			});
			///////////////////////////////////FIN BOTON BUSCAR SOLICITUD DISPONIBLE PRESELECCION ////////////////////////////////////////////////
		///////////////////////////////////BOTON DE SELECCIONAR TODAS LAS SOLICITUDES /////////////////////////////////////////////////
						$("#btnSolicitudesDisppreselTodas").button().click(function(){							
								var aTrs = $('#tblSolicitudDispPreSel').dataTable().fnGetNodes();								
            					 for ( var i=aTrs.length-1 ; i>=0; i-- )     
								{         									
									    var nTr = $('#tblSolicitudDispPreSel').dataTable().fnGetData(aTrs[i]);										
										$('#tblSolicitudPreSel').dataTable().fnAddData( nTr );
										$('#tblSolicitudDispPreSel').dataTable().fnDeleteRow( i );
										var valor = nTr[0];
										$("#cIdTipoSolicitud").val($.trim(valor).split("-")[0]);
										$("#cIdUnidadEjecutoraSolicitud").val($.trim(valor).split("-")[1]);
										$("#nIdConsecutivoSolicitud").val($.trim(valor).split("-")[2]);
										queryFormPost("sp_mConsolidadoPreseleccionSolicitudes", {async : false});
								}												
								queryFormPost("numSolDispCreate", {async : false});
								queryFormPost("numSolAgregadasCreate", {async : false});
						});
		///////////////////////////////////BOTON DE QUITAR TODAS LAS SOLICITUDES /////////////////////////////////////////////////
						$("#btnSolicitudespreselTodas").button().click(function(){
							var aTrs = $('#tblSolicitudPreSel').dataTable().fnGetNodes();           
							for ( var i=aTrs.length-1 ; i>=0; i-- )     
							{         								            
									var nTr = $('#tblSolicitudPreSel').dataTable().fnGetData(aTrs[i]);  
									//nTr[0] = nTr[0].replace("name","nombre");
									$('#tblSolicitudDispPreSel').dataTable().fnAddData( nTr );
									$('#tblSolicitudPreSel').dataTable().fnDeleteRow( i );
									var valor = nTr[0];
									$("#cIdSolicitud").val($.trim(valor));
									$("#cIdTipoSolicitud").val($.trim(valor).split("-")[0]);
									$("#cIdUnidadEjecutoraSolicitud").val($.trim(valor).split("-")[1]);
									$("#nIdConsecutivoSolicitud").val($.trim(valor).split("-")[2]);									
									$("#nIdLineaSolicitud").val('0');
									queryFormPost("sp_mConsolidadoEliminaSolicitudesPreseleccion", {async : false});
							}												
							queryFormPost("numSolDispCreate", {async : false});
							queryFormPost("numSolAgregadasCreate", {async : false});						
						});	
			///////////////////////////////////BOTON DE LINEAS /////////////////////////////////////////////////
						$("#btnLineasConsol").button().click(function(){							
							var aTrs =$('#tblSolicitudPreSel').dataTable().fnGetNodes(); 							
							//if(aTrs.length!= 0){
							document.getElementById('tblSolicitudes').style.display = 'none';
							document.getElementById('tblLineas').style.display = '';
							$('#tblLineasDispPreSel').dataTable().fnClearTable();
							$('#tblLineasPreSel').dataTable().fnClearTable();							
							if($("#str_Consulta").val() == 1){
								cargaLineasPresel();
								cargaLineasDisppresel();
							}else{
								cargaLineasPresel();
								cargaLineasDisppresel();
							}
						});
			///////////////////////////////////BOTON DE SELECCIONAR TODAS LAS LINEAS /////////////////////////////////////////////////
						$("#btnLineasDisppreselTodas").button().click(function(){								
								var aTrs = $('#tblLineasDispPreSel').dataTable().fnGetNodes();								
            					 for ( var i=aTrs.length-1 ; i>=0; i-- )     
								{         									
									    var nTr = $('#tblLineasDispPreSel').dataTable().fnGetData(aTrs[i]);										
										$('#tblLineasPreSel').dataTable().fnAddData( nTr );
										$('#tblLineasDispPreSel').dataTable().fnDeleteRow( i );
										//Consolidado
										var valor = nTr[3];							
										$("#cIdTipoConsolidadoL").val($.trim(valor).split("-")[0]);
										$("#cIdUnidadEjecutoraConsolidadoL").val($.trim(valor).split("-")[1]);
										$("#nIdConsecutivoConsolidadoL").val($.trim(valor).split("-")[2]);							
										//Lineas
										var valor2 = nTr[0]							
										$("#cIdTipoLinea").val($.trim(valor2).split("-")[0]);
										$("#cIdUnidadEjecutoraLinea").val($.trim(valor2).split("-")[1]);						
										$("#nIdConsecutivoLinea").val($.trim(valor2).split("-")[2]);
										$("#nIdLineaSolicitud").val($.trim(nTr[1]));							
										queryFormPost("numLinDispCreate", {async : false});
										queryFormPost("numLinAgregadasCreate", {async : false});
										queryFormPost("mConsolidadoPreseleccionLineaSolicitudCreate", {async : false});	
								}												
								
						});
			///////////////////////////////////BOTON DE QUITAR TODAS LAS LINEAS /////////////////////////////////////////////////
						$("#btnLineaspreselTodas").button().click(function(){						
							var aTrs = $('#tblLineasPreSel').dataTable().fnGetNodes();           
							for ( var i=aTrs.length-1 ; i>=0; i-- )     
							{         								         
									
									var nTr = $('#tblLineasPreSel').dataTable().fnGetData(aTrs[i]);									
									$('#tblLineasDispPreSel').dataTable().fnAddData( nTr );
									$('#tblLineasPreSel').dataTable().fnDeleteRow( i );
									$("#nIdLineaSolicitud").val($.trim(nTr[1]));	
									$("#cIdSolicitud").val($.trim(nTr[0]));
									$("#cIdConsolidado").val(nTr[3]);								
									queryFormPost("sp_mConsolidadoEliminaSolicitudesPreseleccion", {async : false});
							}
												
							queryFormPost("numLinDispCreate", {async : false});
							queryFormPost("numLinAgregadasCreate", {async : false});
						
						});
				///////////////////////////////////BOTON GUARDAR CONSOLIDADO /////////////////////////////////////////////////
						$("#btnGuardarConsolidadoAutomatico").button().click(function(){
							
							
							var cadena_campos=$('#cIdSolicitudCA option:selected').text().split('-');									
							$("#cIdTipoSolicitudCA").val(cadena_campos[0]);
							$("#cIdUnidadEjecutoraSolicitudCA").val(cadena_campos[1]);
							$("#nIdConsecutivoSolicitudCA").val(cadena_campos[2]);
							$("#cDescripcionCA").val(cadena_campos[3]);
							queryFormPost("pa_mConsolidadoAutomaticoCreate",{async : false});
							$("#mensajeCA").css("visibility", "visible");							
							setTimeout("esperar3()",2000);
							
							
      					});
				
				
				////////////////////////FUNCTION ONCHANGE/////////////////////////
				
				
			$("#cCapitulo").change(function () {
					querySelectPost("mCatalogoCapituloRead", "cCapitulo", {async : false});
					querySelectPost("mCatalogoSubPartidaConsolRead", "partida", {async : true});
			});
				
			$("#cunidadRMC").change(function () {
					//alert($("#cunidadRMC").val());
					$("#cIdUnidadEjecutora").val($("#cunidadRMC").val());
					//querySelectPost("mCatalogoCapituloRead", "cCapitulo", {async : false});
					//querySelectPost("mCatalogoSubPartidaConsolRead", "partida", {async : true});
			});	
			
			$("#AlcanceCA").change(function () {
				
				alert($("#cIdUnidadEjecutora").val());
				querySelectPost("fn_mConsolidadoAutomaticoSolicitudesDisponiblesRead","cIdSolicitudCA",{async : false});
				
			});
			
			
			$('#tblConsultaConsolidados tr').live('click', function() { 
				if ($(this).hasClass('row_selected'))             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
				var anSelected = fnGetSelected( oTable );
				var aData = oTable.fnGetData(anSelected[0]);
				//window.location = aData[0];
			});
									
			/////////////////////////////////////BOTON PARTIDAS/////////////////////////////////////////
			$("#btnPartidas").button().click(function(){
				document.getElementById('tblresumen').style.display = 'none';
				document.getElementById('tblconsolidado').style.display = 'block';
				document.getElementById('tbcambsSig').style.display = 'none';
				document.getElementById('tbcambs').style.display = 'block';
				$("#DescripcionPartida").val("");
				$('#tblCucops').dataTable().fnClearTable();
				queryFormPost("mConsolidadoRead", {async : false});								
				querySelectPost("ConsolidadoLineasCucopsDisponiblesRead", "cucopDisponible", {async : false});				
				queryFormPost("numLinTotalRead",{async : false});
				$("#mensajePartida").css("visibility", "hidden");							
			});
			/////////////////////////////////////BOTON RESUMEN/////////////////////////////////////////
			$("#btnPartidasResumen").button().click(function(){
				document.getElementById('tblresumen').style.display = 'block';
				document.getElementById('tblconsolidado').style.display = 'none';
				$("#mensajePartida").css("visibility", "hidden");
				$('#tblResumenpartidas').dataTable().fnClearTable();				
					var szWhere = "";
					var campos = "";  					
					campos = $("#cIdConsolidado").val();					
                    var szTabla = "CONSOLIDADORESUMENPARTIDAS";                     				
					$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, Campos:campos, Order:" order by 1 desc ", ajax: 'false'}, function(j){
						for (var i = 0; i < j.length; i++) 
    					{  
    					    $('#tblResumenpartidas').dataTable().fnAddData( [
								j[i].Col0,
								j[i].Col1,
								j[i].Col2,
								j[i].Col3,
								j[i].Col4,
								j[i].Col5,
								j[i].Col6,
								"<button id='btnLnElimina_" + i + 					"' name='btnLnElimina_" + i + 		"' onclick='eliminaPartida(" + j[i].Col2 + ","  + j[i].Col3 +");'>Elimina</button>"
							]);  
    					    
    					    
						}
		         })		   
				
				
				
				
				
				
				
			});
			/////////////////////////////////////BOTON MODIFICA PARTIDA/////////////////////////////////////////
			$("#btnPartidaModificar").button().click(function(){
				$("#DescripcionPartida").val($("#descripcion_partidaRes").val());
				$("#cIdCABM").val($("#num_cambs").val());
				querySelectPost("sp_mConsolidadoInsertaLineaConsolidadoCreate", "cCapitulo", {async : false});
				$("#btnPartidasResumen").click();
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
												bool= false;
										}
										bool2 = false;
									}								
								}
				if(bool2){
					alert("           Estimado usuario...\n\nPor lo menos debe seleccionar una Linea\n           Intente de nuevo");
				}            					 
            	
			});
			
			//////////////////////////GUARDAR LA PARTIDA//////////////////////////////			
			$("#btnPartidaGuardar").button().click(function(){
				var desPartida = $('#DescripcionPartida');
				var bValid = true;
				allFields = $( []).add(desPartida),
				tips = $(".validateTips");
				tips.text("");
				allFields.removeClass("ui-state-error");
				bValid = bValid&& checkRequerido(desPartida, "La Descripción de la Partida");
				if (bValid) {
					
					$("#cIdCABM").val($("#cucopDisponible").val());																					
					var aTrs = $('#tblCucops').dataTable().fnGetNodes();					
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{							    
								var nTr = $('#tblCucops').dataTable().fnGetData(aTrs[i]);
								queryFormPost("ConsecutivoConsolidadoLineaRead", {async : false});
								//alert("nIdLineaConsolidado"+$("#nIdLineaConsolidado").val());
								//alert("consecutivo"+$("#numPartida").val());
								
								if($("#nIdLineaConsolidado").val() != $("#numPartida").val()){
									$("#nIdLineaConsolidado").val($("#numPartida").val());
									$("#numLinTotPartida").val($("#numPartida").val());									
								}
								
								
									
			            		
			            		           		
			            		
			            		
								$("#cIdSolicitud").val(nTr[0]);								
								$("#nIdConsecutivoSolicitud").val(nTr[0].split("-")[2]);
								$("#nIdLineaSolicitud").val(nTr[1]);
								$("#Accion").val('0');
								queryFormPost("sp_mConsolidadoInsertaDetalleLineaConsolidadoCreate", {async : false});
								$('#tblCucops').dataTable().fnDeleteRow( i );																																			
								querySelectPost("ConsolidadoLineasCucopsDisponiblesRead", "cucopDisponible", {async : false});
						}     
					}																            					
					queryFormPost("sp_mConsolidadoInsertaLineaConsolidadoCreate", {async : false});				
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
					$("#mensajePartida").css("visibility", "visible");
				}
				//$("#btnPartidas").click();
			});
			$("#btnPartidaMenos10").button().click(function(){
				
			});
			$("#btnPartidaAnterior").button().click(function(){
			$('#tblCucops').dataTable().fnClearTable();
			$("#btnPartidaSiguiente").attr("disabled", false);
				var antPartida = document.getElementById("numPartida").value-1;				
				$("#nIdLinea").val(antPartida);	
				if(antPartida > 0){
				queryFormPost("fn_mConsolidadoConsultaUNALineaConsolidadoDetalladaRead", {async : false});
				var i=0;
				for(i=0;i<document.getElementById("cucopDisponible").options.length;i++){										
					if($.trim($("#cambs_desc").val().split(" ")[0]) == $.trim(document.getElementById("cucopDisponible").options[i].value)){
							document.getElementById('tbcambsSig').style.display = 'none';
							$("#DescripcionPartida").val('');
							document.getElementById('tbcambs').style.display = 'block';													
							//alert($("#nIdLineaConsolidado").val());
							return false;
					}
				}
				//document.getElementById("cucopDisponible").value
				
				
				//$.trim($("#cambs_desc").val().split(" ")[0]
				//alert($("#cambs_desc").val().split(" ")[0]);
				
				
				
				$("#numPartida").val($("#nIdLinea").val());				
				document.getElementById('tbcambsSig').style.display = 'block';
				document.getElementById('tbcambs').style.display = 'none';
				$("#mensajePartida").css("visibility", "hidden");
			}
				
			});
			$("#btnPartidaSiguiente").button().click(function(){

				$('#tblCucops').dataTable().fnClearTable();
				var sigPartida = document.getElementById("numPartida").value++;
				$("#nIdLinea").val(document.getElementById("numPartida").value);								
				if(document.getElementById("numLinTot").value >=  document.getElementById("nIdLinea").value){
				queryFormPost("fn_mConsolidadoConsultaUNALineaConsolidadoDetalladaSigRead", {async : false});
								var i=0;
				for(i=0;i<document.getElementById("cucopDisponible").options.length;i++){										
					if($.trim($("#cambs_desc").val().split(" ")[0]) == $.trim(document.getElementById("cucopDisponible").options[i].value)){
							document.getElementById('tbcambsSig').style.display = 'none';
							$("#DescripcionPartida").val('');
							document.getElementById('tbcambs').style.display = 'block';
							return false;
					}
				}
				
				$("#numPartida").val($("#nIdLinea").val());				
				document.getElementById('tbcambsSig').style.display = 'block';
				document.getElementById('tbcambs').style.display = 'none';
				$("#mensajePartida").css("visibility", "hidden");
				}else{
					
					$("#btnPartidaSiguiente").attr("disabled", true);
					document.getElementById('tbcambsSig').style.display = 'none';
					document.getElementById('tbcambs').style.display = 'block';
					$("#DescripcionPartida").val("");
					
				}
				
			});
			$("#btnPartidaMas10").button().click(function(){																			
			});
			$("#cucopDisponible").change(function () {
				$("#mensajePartida").css("visibility", "hidden");				
			$('#tblCucops').dataTable().fnClearTable();
								var szWhere = "";
								var campos = "";  					
								campos = $("#cIdConsolidado").val()+"','"+$("#cucopDisponible").val();					
			                    var szTabla = "CONSOLIDADOLINEASSOLICITUDDISPONIBLES";                                                                                         
								$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, Campos:campos, Order:" order by 1 desc ", ajax: 'false'}, function(j){
									for (var i = 0; i < j.length; i++) 
			    					{  
			    					    $('#tblCucops').dataTable().fnAddData( [
											j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3,j[i].Col4
										]); 
			    					    $("#cIdSolicitud").val(j[i].Col0);
			    					    $("#nIdLineaSolicitud").val(j[i].Col1);
			    					    
									}
					         	})
			})	
			
			////////////////////////////////////TABLA INICIAL RESUMEN PARTIDA///////////////////////////////////////
				$("#tblResumenLineasPartidas").dataTable(					
					{         
					 sScrollY:200,
									 sScrollX: "100%",
									 sScrollXInner: "90%",
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
									bAutoWidth: false       		
			});
			$("#tipoConsolidadoCA").change(function () {					
					querySelectPost("fn_mConsolidadoAutomaticoSolicitudesDisponiblesRead","cIdSolicitudCA",{async : false});
					
					var cadena_campos=$('#cIdSolicitudCA option:selected').text().split('-');									
					$("#cDescripcionCA").val(cadena_campos[3]);
					
				
			});
			$("#cIdSolicitudCA").change(function () {															  				
					var cadena_campos=$('#cIdSolicitudCA option:selected').text().split('-');									
					$("#cIdTipoSolicitudCA").val(cadena_campos[0]);
					$("#cIdUnidadEjecutoraSolicitudCA").val(cadena_campos[1]);
					$("#nIdConsecutivoSolicitudCA").val(cadena_campos[2]);
					$("#cDescripcionCA").val(cadena_campos[3]);
					
				
			});
			querySelectPost("mAlcanceRead", "nIdAlcanceCon", {async : false});
			querySelectPost("mTipoConsolidadoRead", "cIdTipoConsolidadoCon", {async : false});
			querySelectPost("UnidadEjecutoraConsolidadoRead", "cIdUnidadEjecutoraCon", {async: false });
			querySelectPost("mEstadoRead", "nIdEstadoCon", {async: false });
			mostrar();				
			$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			////////////////////////////////////TABLA INICIAL RESUMEN PARTIDA///////////////////////////////////////
			

			
				 rpTable =$('#tblResumenpartidas').dataTable(					
					{         
					 				 sScrollY:200,
									 sScrollX: "100%",
									 sScrollXInner: "90%",
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
									bAutoWidth: false       		
			});
				 $('#example tr').click( function() {    
				  $(this).toggleClass('row_selected');     } ); 
			  /* Init the table    
			   * 
			   */
			   var rpTable = $('#example').dataTable( );
		});
		////////////////////////////////////FIN DE FUNCION PRINCIPAL///////////////////////////////////////		
		function esperar() {
							queryFormPost("mConsolidadoRead", {async : false});
							$("#cIdConsolidadoPre").val($("#cIdConsolidado").val());
							$("#cIdConsolidadoPar").val($("#cIdConsolidado").val());
							$("#desConsolidadoPre").val($("#desConsolidado").val());
							$("#desConsolidadoPar").val($("#desConsolidado").val());
							if($("#str_Consulta").val() == 1){
								cargaSolicitudesSel();
								cargaSolicitudesDisppresel();
							}else{
								cargaSolicitudesDisppresel();
							}
							$("#mensajePA").css("visibility", "hidden");
							if($("#str_Consulta").val() != 1){							
							$("#cIdTipoConsolidado").val($('#ctipoConsolidadoC option:selected').val());	
							}
							if($("#str_Consulta").val() == 1){
								$("#cEstadof").val($("#nIdEstado").val());
								$("#cEstadoPre").val($("#cEstado").val());
								$("#cEstadoPar").val($("#cEstado").val());
								$("#cIdConsolidadof").val($("#cIdConsolidado").val());
							}else{
								$("#cEstadof").val($("#cEstado").val());
								$("#cEstadoPre").val($("#cEstado").val());
								$("#cEstadoPar").val($("#cEstado").val());
								$("#cIdConsolidadof").val($("#cIdConsolidado").val());
							}
							queryFormPost("fn_mConsolidadoCalculaMontoRead", {async : false});
							queryFormPost("fn_mConsolidadoCalculaMontoBrutoRead", {async : false});	
							queryFormPost("fn_mConsolidadoTieneProcedimientoRead", {async : false});
							queryFormPost("numPartidasConsolidadasRead",{async : false});
							if($("#tieneProcedimiento").val() == '')
								$("#tieneProcedimiento").val('N/A');
							if($("#tienePedidos").val() == '')
								$("#tienePedidos").val('0');							
							$("#montoTotal").formatCurrency();
							$("#MontoBruto").formatCurrency();							
							$("#aTab1").attr("disabled", false);
							$("#aTab2").attr("disabled", false);							
							$("#aTab3").attr("disabled", false);
							$("#aTab1").click()							
							$("#aTab0").attr("disabled", false);							
							querySelectPost("UnidadEjecutoraBuscaRead", "cUnidadEjecutoraRMC", {async : false});
							
		////////////////////////////////////TABLA INICIAL SOLICITUD DISPONIBLE///////////////////////////////////////					
							$("#tblSolicitudDispPreSel").dataTable(
								{         									
									 sScrollY:200,
									 sScrollX: "100%",
									 sScrollXInner: "90%",
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
									bAutoWidth: false					
								});
		////////////////////////////////////TABLA INICIAL SOLICITUD AGREGAR///////////////////////////////////////
								 $('#tblSolicitudPreSel').dataTable(
								{         
									 sScrollY:200,
									 sScrollX: "100%",
									 sScrollXInner: "90%",
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
									bAutoWidth: false   		
								});		
		////////////////////////////////////TABLA INICIAL LINEAS DISPONIBLE///////////////////////////////////////					
							$('#tblLineasDispPreSel').dataTable(
								{         									
									 sScrollY:200,
									 sScrollX: "100%",
									 sScrollXInner: "90%",
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
									bAutoWidth: false   				
								});
		////////////////////////////////////TABLA INICIAL LINEAS AGREGAR///////////////////////////////////////
								 $('#tblLineasPreSel').dataTable(
								{         
									 sScrollY:200,
									 sScrollX: "100%",
									 sScrollXInner: "90%",
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
									bAutoWidth: false 		
								});
				////////////////////////////////////TABLA INICIAL PARTIDAS///////////////////////////////////////				
				$('#tblCucops').dataTable(
					{         
					 				 sScrollY:200,
									 sScrollX: "100%",
									 sScrollXInner: "90%",
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
									bAutoWidth: false     		
				});	
				
					}
				function esperar2() {
							$("#mensajeConsol").css("visibility", "hidden");
							queryFormPost("mConsolidadoRead", {async : false});																					
							$("#aTab2").click();								
				}
				
				///////////////////////CLICK EN EL TR DE tblSolicitudDispPreSel////////////////////////////////////////
				$('#tblSolicitudDispPreSel tr').live('click', function() {         
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
					var aTrs = $('#tblSolicitudDispPreSel').dataTable().fnGetNodes();           
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							var nTr = $('#tblSolicitudDispPreSel').dataTable().fnGetData(aTrs[i]);   
							$('#tblSolicitudPreSel').dataTable().fnAddData( nTr );							
							$('#tblSolicitudDispPreSel').dataTable().fnDeleteRow( i );
							var valor = nTr[0];
							$("#cIdTipoSolicitud").val($.trim(valor).split("-")[0]);
							$("#cIdUnidadEjecutoraSolicitud").val($.trim(valor).split("-")[1]);
							$("#nIdConsecutivoSolicitud").val($.trim(valor).split("-")[2]);							
						}     
					}
					queryFormPost("sp_mConsolidadoPreseleccionSolicitudes", {async : false});					
					queryFormPost("numSolDispCreate", {async : false});
					queryFormPost("numSolAgregadasCreate", {async : false});
				} );
			
			$('#tblSolicitudPreSel tr').live('click', function() {         
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
							//nTr[0] = nTr[0].replace("name","nombre");
							$('#tblSolicitudDispPreSel').dataTable().fnAddData( nTr );
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
					queryFormPost("numSolDispCreate", {async : false});
					queryFormPost("numSolAgregadasCreate", {async : false});
				} );
			///////////////////////CLICK EN EL TR DE tblLineasDispPreSel////////////////////////////////////////
				$('#tblLineasDispPreSel tr').live('click', function() {         
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
					var aTrs = $('#tblLineasDispPreSel').dataTable().fnGetNodes();           
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{             
							var nTr = $('#tblLineasDispPreSel').dataTable().fnGetData(aTrs[i]);   
							$('#tblLineasPreSel').dataTable().fnAddData( nTr );							
							$('#tblLineasDispPreSel').dataTable().fnDeleteRow( i );							
							
							//Consolidado
							var valor = nTr[3];							
							$("#cIdTipoConsolidadoL").val($.trim(valor).split("-")[0]);
							$("#cIdUnidadEjecutoraConsolidadoL").val($.trim(valor).split("-")[1]);
							$("#nIdConsecutivoConsolidadoL").val($.trim(valor).split("-")[2]);							
							//Lineas
							var valor2 = nTr[0]							
							$("#cIdTipoLinea").val($.trim(valor2).split("-")[0]);
							$("#cIdUnidadEjecutoraLinea").val($.trim(valor2).split("-")[1]);						
							$("#nIdConsecutivoLinea").val($.trim(valor2).split("-")[2]);
							$("#nIdLineaSolicitud").val($.trim(nTr[1]));							
							queryFormPost("mConsolidadoPreseleccionLineaSolicitudCreate", {async : false});					
							
						}     
					}
					queryFormPost("numLinDispCreate", {async : false});
					queryFormPost("numLinAgregadasCreate", {async : false});
				} );
			
			
			
			
			
			$('#tblLineasPreSel tr').live('click', function() {         
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
					var aTrs = $('#tblLineasPreSel').dataTable().fnGetNodes();           
					for ( var i=aTrs.length ; i>=0; i-- )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{							
							var nTr = $('#tblLineasPreSel').dataTable().fnGetData(aTrs[i]);  							
							$('#tblLineasDispPreSel').dataTable().fnAddData( nTr );
							$('#tblLineasPreSel').dataTable().fnDeleteRow( i );	
							$("#nIdLineaSolicitud").val($.trim(nTr[1]));	
							$("#cIdSolicitud").val($.trim(nTr[0]));
							$("#cIdConsolidado").val(nTr[3]);								
							queryFormPost("sp_mConsolidadoEliminaSolicitudesPreseleccion", {async : false});											
						}
						
					}
					queryFormPost("numLinDispCreate", {async : false});
					queryFormPost("numLinAgregadasCreate", {async : false});
				} );
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
							
							 $("#nIdLineaConsolidado").val(nTr[2]);
							 $("#numPartidaRes").val(nTr[2]);
							 $("#descripcion_partidaRes").val(nTr[0]);							 
							var lineaConsol =nTr[2];
							//alert(nTr[1]);
							 $("#num_cambs").val(nTr[1]);
							cargaSolicitudLineaResumen(lineaConsol);							
						}
						
					}					
				} ); 
			$('#tblSolicitudPreSel tr').live('click', function() {         
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');     
				} );
			$('#tblLineasPreSel tr').live('click', function() {         
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');     
				} );
			///////////////////////CLICK EN EL TR DE tblCucops////////////////////////////////////////
				$('#tblCucops tr').live('click', function() {         
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');  					
				} );
			
			///////////////////////CLICK EN EL TR DE tblResumenpartidas////////////////////////////////////////
			/*	$('#tblResumenLineasPartidas tr').live('click', function() {					
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');  					
				} );
			*/
			
			//
			
			function cargaSolicitudesSel(){	
					$('#tblSolicitudPreSel').dataTable().fnClearTable();
               		var szWhere = "";
					var campos = "";  					
					campos = $("#cIdConsolidado").val();					
                    var szTabla = "CONSOLIDADOPRESOLICITUDESSELECCIONADAS";                                                                                         
					$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, Campos:campos, Order:" order by 1 desc ", ajax: 'false'}, function(j){
						for (var i = 0; i < j.length; i++) 
    					{  
    					    $('#tblSolicitudPreSel').dataTable().fnAddData( [
								j[i].Col0,j[i].Col1,j[i].Col2
							]);  
    					    
    					    
						}
		         })		           
		         queryFormPost("numSolDispCreate", {async : false});
				 queryFormPost("numSolAgregadasCreate", {async : false});
				}
						
				function cargaSolicitudesDisppresel(){
					$('#tblSolicitudDispPreSel').dataTable().fnClearTable();
               		var szWhere = "";
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
		         queryFormPost("numSolDispCreate", {async : false});
				 queryFormPost("numSolAgregadasCreate", {async : false});
				}
				function cargaLineasDisppresel(){						
               		var szWhere = "";
					var campos = "";  					
					campos = $("#cIdConsolidado").val();					
                    var szTabla = "LINEASDISPPRESEL";                                                                                         
					$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, Campos:campos, Order:" order by 1 desc ", ajax: 'false'}, function(j){
						for (var i = 0; i < j.length; i++) 
    					{  
    					    $('#tblLineasDispPreSel').dataTable().fnAddData( [
								j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3
							]);  
    					    
    					    
						}
		         })		           
		         queryFormPost("numLinDispCreate", {async : false});
				 queryFormPost("numLinAgregadasCreate", {async : false});
				}
				
				function cargaLineasPresel(){						
               		var szWhere = "";
					var campos = "";  					
					campos = $("#cIdConsolidado").val();					
                    var szTabla = "LINEASPRESEL";                                                                                         
					$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, Campos:campos, Order:" order by 1 desc ", ajax: 'false'}, function(j){
						for (var i = 0; i < j.length; i++) 
    					{  
    					    $('#tblLineasPreSel').dataTable().fnAddData( [
								j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3
							]);  
    					    
    					    
						}
		         })		           
		         queryFormPost("numLinDispCreate", {async : false});
				 queryFormPost("numLinAgregadasCreate", {async : false});
				}
				function BuscaSolicitudesDisppresel(){  					
               		var szWhere = " ";
					var campos = "";  					
					campos = $("#cAlcanceC").val()+"','"+$("#cIdTipoConsolidado").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#cIdConsolidado").val();
					//szWhere = " clavesiaff =substring('"+$("#EP").val()+"',1,55)  ";
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
				 /* Add a click handler for the delete row */  
	 				$('#delete').click( function() {       
	 var anSelected = fnGetSelected( oTable );   
	 oTable.fnDeleteRow( anSelected[0] );     } ); 
				$(this).ajaxForm({
					dataType:  "json",
					success: formSubmited
				});
						
				function checkRequerido(o, n) {
					var sTemp = $.trim(o.val());
					o.val(sTemp);
					if (sTemp.length == 0) {
						o.addClass("ui-state-error");
						updateTipsDlg(n + " es un dato requerido.");
						o.focus();
						return false;
					} else {
						return true;
					}
				}
				function updateTipsDlg(t) {
					tips.text(t);
					alert(t);
				}
												
				function fnGetSelected( oTableLocal )
			{
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
		function fnGetSelected( oTableLocal ) 
		{    
			return oTableLocal.$('tr.row_selected'); 
			} 
			
				function formSubmited() {
                	alert("Solicitudes enviadas!");
            	}
				function mostrar() {
			var qw = " 1 = 1";			
			var qw = " cEjercicioCons = '" + $("#cEjercicio").val() + 
				"' AND cIdUnidadEjecutoraCons = '" + $("#cIdUnidadEjecutoraCon").val() + 
				"' AND cIdTipoConsolidadoCons LIKE '" + $("#cIdTipoConsolidadoCon").val()+"'";		
			if ($("#nIdEstadoCon").val() != "0")
				qw += " AND nIdEstadoCons = " + $("#nIdEstadoCon").val();			
			if ($("#nIdAlcanceCon").val() != "0")
				qw += " AND nIdAlcanceCons = " + $("#nIdAlcanceCon").val();
			qw += " AND cDescripcionCons LIKE '%25" + $("#cDescripcionCon").val() + "%25'";
			qw+= " AND cIdConsolidadoCons LIKE '%25"+ $("#cIdConsolidadoCon").val()+"%25'";
			
			oTable = $("#tblConsultaConsolidados").dataTable({
				sScrollY:200,
				sScrollX: "850%",
				sScrollXInner: "110%",
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
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vconsultaConsolidado&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 4, "asc" ]] ,
				bAutoWidth: false,
				aoColumns: [		
					{ sName: "nIdEstadoCons", bVisible: false },
					{ sName: "nIdAlcanceCons", bVisible: false },
					{ sName: "cIdTipoConsolidadoCons", bVisible: false },
					{ sName: "cIdConsolidadoCons" },
					{ sName: "cAlcanceCons" },
					{ sName: "cEstadoCons" },
					{ sName: "cTipoConsolidadoCons"},
					{ sName: "mMontoConsolidadoCons" },
					{ sName: "mMontoConsolidadoIVACons" },
					{ sName: "cDescripcionCons" },
					{ sName: "nCantidadLineasCons" },
					{ sName: "cProcedimientoCons" },
					{ sName: "nTotalPedidosCons" }
				]
        	});
		}
		
		/* Get the rows which are currently selected */
		function fnGetSelected( oTableLocal )
		{
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
		
		
		
		function getUrlParameter(param) {
			param = param.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
			var r1 = "[\\?&]"+param+"=([^&#]*)";
			var r2 = new RegExp(r1);
			var r3 = r2.exec(window.location.href);
			if (r3 == null)
				return "";
			else
				return r3[1];
		}
		function  clicPreSeleccion(){			
			document.getElementById('tblSolicitudes').style.display = '';
			document.getElementById('tblLineas').style.display = 'none';				
			if($("#str_Consulta").val() == 1){
								
							cargaSolicitudesSel();
								
								cargaSolicitudesDisppresel();								
							}else{
								cargaSolicitudesDisppresel();
							}
		}
		function mConsolidadoTotalLineas(){			
				queryFormPost("numLinTotalRead",{async : false});
				queryFormPost("fn_mConsolidadoNumPartidasRead",{async : false});
			
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
			$("#btnPartidasResumen").button().click();
			
			
		}
		
		function cargaSolicitudLineaResumen(LinCon){
					$("#tblResumenLineasPartidas").dataTable().fnClearTable();
					var str_lineaConsol = LinCon;
					var szWhere = "";
					var campos = ""; 
					
					var a = '';
							var b = '';
					campos = $("#cIdConsolidado").val()+"','"+str_lineaConsol;					
                    var szTabla = "CONSOLIDADOSOLICITUDLINEARESUMEN";                                                                                         
					$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, Campos:campos, Order:" order by 1 desc ", ajax: 'false'}, function(j){
						for (var i = 0; i < j.length; i++) 
    					{
							a = j[i].Col0;
							b= j[i].Col1;							
    					    $('#tblResumenLineasPartidas').dataTable().fnAddData( [
								j[i].Col0,
								j[i].Col1,
								j[i].Col2,
								j[i].Col3,
								j[i].Col4,
								j[i].Col5,
								j[i].Col6,
								"<button id='btnLnEliminaLinea_" + i + 					"' name='btnLnEliminaLinea_" + i + 		"' onclick='eliminaLineaPartida(\"" + a + "\","  + b +","+str_lineaConsol+");'>Elimina</button>"								
							]);  
						}
		         })
		}
		
		function eliminaLineaPartida(a,b,str_lineaConsol){
			
			$("#cEjercicio").val();
			$("#cIdConsolidado").val();
			$("#cIdSolicitud").val(a);
			$("#nIdLineaSolicitud").val(b);
			$("#nIdLineaConsolidado").val(str_lineaConsol);
			$("#Accion").val('1');			
			
			queryFormPost("sp_mConsolidadoInsertaDetalleLineaConsolidadoCreate",{async : false});
			cargaSolicitudLineaResumen(str_lineaConsol);
			$("#tblResumenpartidas").dataTable().fnClearTable();
			$("#btnPartidasResumen").button().click();
			
			
		}
		function consolidadoAutomatico(){
			//querySelectPost("TipoConsolidadoAutomaticoRead","tipoConsolidadoCA",{async : true});			
			if(   '<%=usuario.getU_UR().trim()%>'   == 'B01'
				||'<%=usuario.getU_UR().trim()%>' == 'B02' 
				||'<%=usuario.getU_UR().trim()%>' == 'A04'				
				||'<%=usuario.getU_UR().trim()%>' == 'B04'
				||'<%=usuario.getU_UR().trim()%>' == 'B05'
				||'<%=usuario.getU_UR().trim()%>' == 'B06'
				||'<%=usuario.getU_UR().trim()%>' == 'B07' 
				||'<%=usuario.getU_UR().trim()%>' == 'B08'
				||'<%=usuario.getU_UR().trim()%>' == 'B09'
				||'<%=usuario.getU_UR().trim()%>' == 'B010'
				||'<%=usuario.getU_UR().trim()%>' == 'B011'
				||'<%=usuario.getU_UR().trim()%>' == 'B012'
				||'<%=usuario.getU_UR().trim()%>' == 'B013'
				||'<%=usuario.getU_UR().trim()%>' == 'B014'){
				
				querySelectPost("TipoConsolidadoAutomaticoRead","tipoConsolidadoCA",{async : true});
				querySelectPost("consolidadoAutomaticoUnidadEjecutoraRead","unidadEjecutoraCA",{async : true});
				querySelectPost("AlcanceconsolidadoAutomaticoAdminRead","AlcanceCA",{async : true});
			}else{
				querySelectPost("TipoConsolidadoAutomaticoRead","tipoConsolidadoCA",{async : true});
				querySelectPost("consolidadoAutomaticoUnidadEjecutoraRead","unidadEjecutoraCA",{async : true});
				
				("#unidadEjecutoraCA").readonly();
				
				querySelectPost("AlcanceconsolidadoAutomaticoRead","AlcanceCA",{async : true});
				
			}
			
		}
		
		function esperar3(){
			var pagina="../SAICYS/Procedimiento.jsp"
			location.href=pagina; 
		}
		function anular(){	
				$("#cIdConsolidado").val();
				queryFormPost("fn_mConsolidadoTieneProcedimientoRead", {async : false});
				alert($("#tieneProcedimiento").val());
				/*if($("#tieneProcedimiento").val()!= 'N/A'){
					queryFormPost("sp_mConsolidadoAnulaConsolidado", {async : false});				
					alert("El Consolidado "+$("#cIdConsolidado").val()+" será Anulado y todas sus lineas serán liberadas");
					$("#aTab0").click();	
				}else{
					alert("El Consolidado "+$("#cIdConsolidado").val()+" no puede ser Anulado esta en un procedimiento...");					
				}*/
		}
		
		function salir(){
			$("#aTab4").click()	
		}
		function aprobar(){						
			if($("#numLinTot").val() != ''){				
			if($("#numLinTot").val() != 0){				
				if($("#cEstadof").val() != "2"){				
			$("#nIdEstado").val('2');
			$("#cEjercicio").val();
			$("#cIdTipoConsolidado").val();
			$("#cIdUnidadEjecutora").val();
			$("#nIdConsecutivo").val();
			queryFormPost("mConsolidadoUpdate", {async : false});			
			setTimeout("msgAprobar()", 2000);
			}
		}else{
			
			alert("ESTIMADO USUARIO: \nEL CONSOLIDADO SOLICITA PARTIDAS CONSOLIDADAS");
		}
		}else{
			
			alert("ESTIMADO USUARIO: \nEL CONSOLIDADO PARTIDAS 0");
		}	
			
			
			
			
			
		}
		function msgAprobar(){
			alert("ESTIMADO USUARIO: \nEL CONSOLIDADO SE APROBÓ CORRECTAMENTE");			
		}
		
		
</script>
</head>
<body id="dt_example" >
		<form>
			<input id="cEjercicio" name="cEjercicio" type="hidden" size="4">
			<input id="cIdTipoConsolidado" name="cIdTipoConsolidado" type="hidden" size="4">
			<input id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden" size="3">
			<input id="cDescripcion" name="cDescripcion" type="hidden" size="2000">
			<input id="nIdEstado" name="nIdEstado" type="hidden" size="2">
			<input id="nIdAlcance" name="nIdAlcance" type="hidden" size="2">
			<input id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" type="hidden" size="10">
			<input id="cIdEntidadContable" name="cIdEntidadContable" type="hidden" size="4">
			<input id="nIdConsecutivo" name="nIdConsecutivo" type="hidden" size="4">
			
			
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
			<input id="cIdTipoSolicitudCA" name="cIdTipoSolicitudCA" type="hidden" size="3">
			<input id="cIdUnidadEjecutoraSolicitudCA" name="cIdUnidadEjecutoraSolicitudCA" type="hidden" size="3">
			<input id="nIdConsecutivoSolicitudCA" name="nIdConsecutivoSolicitudCA" type="hidden" size="3">
			<input id="Accion" name="Accion" type="hidden" size="3">
			<input id="num_cambs" name="num_cambs" type="hidden" size="3">
			
			
			<div id="container" class="container">
			
			
			<h1>CONSOLIDADO <label id="lbOperacion" style="font-size: 8pt"></label></h1>			
			<table border="0" align="center">
			<tr>
				<td>											
					<div class="tabs">
						<ul>
							<li><a id="aTab0" href="#tabs-0">Nuevo</a></li>
							<li><a id="aTab1" href="#tabs-1">Carátula</a></li>
							<li><a id="aTab2" href="#tabs-2" onclick="clicPreSeleccion()">PreSelección</a></li>
							<li><a id="aTab3" href="#tabs-3" onclick="mConsolidadoTotalLineas()">Partidas</a></li>
							<li><a id="aTab4" href="#tabs-4" onclick="mostrar()">Consulta</a></li>
							<li><a id="aTab5" href="#tabs-5" onclick="consolidadoAutomatico()">Nuevo Automatico</a></li>
						</ul>					
						<!-- NUEVO -->
							<div id="tabs-0" align="center">
										<table border="0" width="100%">
											<tr>
												<td align="right">
													Tipo Consolidado:
												</td>
												<td>
													<select id="ctipoConsolidadoC" name="ctipoConsolidadoC"
														style="width: 40em;">
													</select>
												</td>
											</tr>
											<tr>
												<td align="right">
													Unidad R.M.:
												</td>
												<td>
													<select id="cunidadRMC" name="cunidadRMC" style="width: 40em;">
														<option value="<%=usuario.getU_UR()%>" selected="selected">
													</select>
												</td>
											</tr>
											<tr>
												<td align="right">
													Alcance:
												</td>
												<td>
													<select id="cAlcanceC" name="cAlcanceC" style="width: 40em;">
													</select>
												</td>
											</tr>
											<tr>
												<td align="right">
													Descripción:
												</td>
												<td>
													<textarea name="cDescripcionC" rows="512" id="cDescripcionC" style='text-transform: uppercase; height: 111px; width: 545px'></textarea>
												</td>
											</tr>
											<tr>
												<td align="right">
													&nbsp;
												</td>
												<td align="center">
													<label class="validateTips ui-state-error"></label>
												</td>
											</tr>
											<tr>
												<td width="33%">
													&nbsp;
												</td>
												<td width="33%" align="center">
													<button id="btnGuardarConsolidado">
														Guardar
													</button>
													&nbsp;&nbsp;&nbsp;
												</td>
											</tr>
											<tr>
												<td>
													&nbsp;
												</td>
												<td id="mensajePA" name="mensajePA" style="FONT-SIZE: 12pt; Color: red" style="visibility: hidden" align="center">
													<img id="imgPlayStop" src="../imagenes/wait24trans.gif">&nbsp;Procesando...
												</td>
											</tr>
										</table>
						</div>
			
					<!-- CARÁTULA -->
						<div id="tabs-1" align="center">
							<table border="0" width="100%">
								<tr align="right">
									<td >&nbsp; 
									</td>								
									<td>&nbsp;&nbsp;&nbsp;&nbsp;<img id="imgPlayStop" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="aprobar()" />&nbsp;Aprobar 
									<img id="imgPlayStop" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer" />&nbsp;Devolver
									<img id="imgPlayStop" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="anular();"/>&nbsp;Anular
									<img id="imgPlayStop" src="../imagenes/cancel.png" style="cursor: pointer" onclick="salir();"/>&nbsp;Salir
									</td>
								</tr>
								<tr align="left">
									<td colspan="2">
										[[<input name="cIdConsolidado" id="cIdConsolidado" type="text" size ="10" style="border: 0px solid black;text-align: center"></input>]]&nbsp;<input name="desConsolidado" id="desConsolidado" type="text" size ="50" style="border: 0px solid black;"></input>  
									</td>								
								</tr>
								<tr>
									<td align="left" colspan="2">	
										&nbsp;<input name="cEstado" id="cEstado" type="text" size ="15" style="border: 0px solid black;">
									</td>								
								</tr>
								<tr>
									<td align="right">
										Tipo Consolidado: 
									</td>
									<td colspan="2" align="left">
										<input name="cTipoConsolidado" id="cTipoConsolidado" type="text" size ="50" disabled="disabled" >
									</td>
								</tr>
	
								<tr>
									<td align="right">
										Unidad R.M: 
									</td>
									<td colspan="2" align="left">
										<input name="unidadEjecutora" id="unidadEjecutora" type="text" size ="50" disabled="disabled">
	
									</td>
								</tr>
	
								<tr>
									<td align="right">
										Alcance: 
									</td>
									<td colspan="2" align="left">
										<input name="cAlcance" id="cAlcance" type="text" size ="50" disabled="disabled">
									</td>
								</tr>
								<tr>
									<td align="right">
										Número: 
									</td>
									<td colspan="2" align="left">
										<input name="cIdConsolidadof" id="cIdConsolidadof" type="text" size ="50" disabled="disabled">
									</td>
								</tr>
								<tr>
									<td align="right">
										Estado: 
									</td>
									<td colspan="2" align="left">
										<input name="cEstadof" id="cEstadof" type="text" size ="50" disabled="disabled">
									</td>
								</tr>
								<tr>
									<td align="right">
									Monto Bruto: 
									</td>
									<td colspan="2" align="left"> 
										<input name="MontoBruto" id="MontoBruto" type="text" size ="50" disabled="disabled">
									</td>
								</tr>
								<tr>
									<td align="right">
										Monto Total: 
									</td>
									<td colspan="2" align="left">
										<input name="montoTotal" id="montoTotal" type="text" size ="50" disabled="disabled">
									</td>
								</tr>
								<tr>
									<td align="right">
										Procedimiento 
									</td>
									<td colspan="2" align="left">
										<input name="tieneProcedimiento" id="tieneProcedimiento" type="text" size ="50" disabled="disabled">
									</td>
								</tr>
								<tr>
									<td align="right">
										Pedidos  
									</td>
									<td colspan="2" align="left">
										<input name="tienePedidos" id="tienePedidos" type="text" size ="50" disabled="disabled">
									</td>
								</tr>
								<tr>
									<td align="right">
										#PC
									</td>
									<td colspan="2" align="left">
										<input name="numLineasConsolidadas" id="numLineasConsolidadas" type="text" size ="50" disabled="disabled">
									</td>
								</tr>
								<tr>
									<td align="right">
										Descripción:  
									</td>
									<td colspan="2" align="left">
										<textarea name="desConsol" id="desConsol"  cols="50" rows="10" ></textarea>
									</td>
								</tr>
								<tr>
									<td align="center" colspan="3">
										<button id="btnGuardarConsolCaratula">
											Guardar
										</button>
										&nbsp;&nbsp;&nbsp;
									</td>
								</tr>
								<tr>	
									<td colspan="3" id="mensajeConsol" style="FONT-SIZE: 12pt; Color: red" style="visibility: hidden" align="center">
										<img id="imgPlayStop" src="../imagenes/wait24trans.gif">&nbsp;Guardando...
									</td>
									
								</tr>
							</table>					
						</div>
						<br />
					<!-- PRESELECCIÓN -->
						<div id="tabs-2" >
							<table border="0" width="100%">							
							<tr><td>
								<table border="0" align="left" width="100%">
								
								<tr align="right">																																	
									<td colspan="3">&nbsp;&nbsp;&nbsp;&nbsp;<img id="imgPlayStop" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="aprobar()" />&nbsp;Aprobar 
									<img id="imgPlayStop" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer" />&nbsp;Devolver
									<img id="imgPlayStop" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="anular();"/>&nbsp;Anular
									<img id="imgPlayStop" src="../imagenes/cancel.png" style="cursor: pointer" onclick="salir();"/>&nbsp;Salir</td>
								</tr>
								<tr align="left">
									<td colspan="3">[[<input name="cIdConsolidadoPre" id="cIdConsolidadoPre" type="text" size ="10" style="border: 0px solid black;text-align: center"></input>]]&nbsp;<input name="desConsolidadoPre" id="desConsolidadoPre" type="text" size ="50" style="border: 0px solid black;"></input></td>
								</tr>
								<tr>
									<td align="left" colspan="2">	
										&nbsp;<input name="cEstadoPre" id="cEstadoPre" type="text" size ="15" style="border: 0px solid black;">
									</td>								
								</tr>								
									<tr>
										<td >Realiza PreSelección</td>
										<td>&nbsp;</td>
										<td >&nbsp;</td>
									</tr>
									<tr>
										<td colspan="3">
										<button id="btnSolicitudesConsol">
												Solicitudes
											</button>&nbsp;&nbsp;&nbsp;
											<button id="btnLineasConsol">
												Lineas
											</button>				
										</td>										
									</tr>
								</table>
								</td></tr>
								<tr><td>
								<table>
								<tr>
									<td>
									<!-- SOLICITUDES -->
									<div  id="tblSolicitudes" style='display:block'>
									<table border="0" align="left" >								
										<tr>
											<td >Área:</td>										
											<td colspan="2">
												<select id="cUnidadEjecutoraRMC" name="cUnidadEjecutoraRMC" style="width: 40em;">
												<option value="<%=usuario.getU_UR()%>" selected="selected">
												</select>
											</td>
										</tr>
										<tr>
											<td >Cápitulo:</td>										
											<td   colspan="2">
												<select id="cCapitulo" name="cCapitulo"	style="width: 40em;">
												</select>
											</td>
										</tr>
										<tr>
											<td >Partida:</td>										
											<td  colspan="2">
												<select id="partida" name="partida" style="width: 40em;">
												</select>
											</td>
										</tr>
										<tr>
											<td >Número:</td>										
											<td   colspan="2">
												<input name="cIdConsolidadoPresel" id="cIdConsolidadoPresel" type="text" size ="40" >
											</td>
										</tr>
										<tr><td><h1>SOLICITUDES <label id="lbOperacion" style="font-size: 8pt"></label></h1></td></tr>
										<tr>
											<td colspan="3">
												Solicitudes Disponibles para preselección:
												<input name="numSolDisp" id="numSolDisp" type="text" size ="4" style="border: 0px solid black;"> 
											</td>								
										</tr>
									<tr>
											<td colspan="3">
											<table id="tblSolicitudDispPreSel" class="display">
									            <thead>
									                <tr>
									                	<th>Solicitud</th>
									                    <th>Partida</th>
									                    <th>Descripcion</th>
									                </tr>
									            </thead>
									        </table>
											</td>						
									</tr>
									<tr height="100 px" VALIGN=TOP>
									<td>
												<button id="btnSolicitudesDisppreselBuscar">
													Buscar
												</button>
												&nbsp;&nbsp;&nbsp;
									</td>						
									<td>					
												<button id="btnSolicitudesDisppreselTodas">
													Seleccionar Todo
												</button> 
									</td>						
									</tr>
									<tr>
									<td colspan="3" align="center">
												
												<h1><label id="lbOperacion" style="font-size: 8pt"></label></h1>
														
									</tr>
									<tr>
									<td colspan="3">
												Solicitudes Preseleccionadas:
												<input name="numSol" id="numSol" type="text" size ="4" style="border: 0px solid black;">
									</td>												
									</tr>
									<tr>
											<td colspan="3">
											<table id="tblSolicitudPreSel" class="display" >
									            <thead>
									                <tr>
									                	<th>Solicitud</th>
									                    <th>Partida</th>
									                    <th>Descripcion</th>
									                </tr>
									            </thead>
									        </table>
											</td>						
									</tr>
									<tr height="100 px" VALIGN=TOP>												
									<td colspan="3">					
												<button id="btnSolicitudespreselTodas">
													Seleccionar Todo
												</button> 
									</td>						
									</tr>
									</table>
									</div>	
									&nbsp;								
									</td>
								</tr>
								<tr><td>
								<!-- LINEAS -->
								<div  id="tblLineas" style='display:none'>
								<table border="0" align="left">		
									<tr><td>
										<h1>LINEAS <label id="lbOperacion" style="font-size: 8pt"></label></h1>																
									</td></tr>							
									<tr>
										<td width="330px" colspan="3">
											Lineas Disponibles:
											<input name="numLinDisp" id="numLinDisp" type="text" size ="4" style="border: 0px solid black;"> 
										</td>								
									</tr>
								<tr>
										<td colspan="3">
										<table id="tblLineasDispPreSel" class="display" >
								            <thead>
								                <tr>
								                	<th>Solicitud</th>
								                    <th>Línea</th>
								                    <th>Descripcion</th>
								                     <th>cIdConsolidado</th>
								                </tr>
								            </thead>
								        </table>
										</td>						
								</tr>
								<tr height="100 px" VALIGN=TOP>
								<td>											
											&nbsp;&nbsp;&nbsp;
								</td>						
								<td>					
											<button id="btnLineasDisppreselTodas">
												Seleccionar Todo
											</button> 
								</td>						
								</tr>
								<tr>
								<td colspan="3" align="center">											
											<h1><label id="lbOperacion" style="font-size: 8pt"></label></h1>												
								</tr>
								<tr>
								<td colspan="3">
											Lineas Preseleccionadas:
											<input name="numLin" id="numLin" type="text" size ="4" style="border: 0px solid black;">
								</td>												
								</tr>
								<tr>
										<td colspan="3">
										<table id="tblLineasPreSel" class="display" >
								            <thead>
								                <tr>
								                	<th>Solicitud</th>
								                    <th>Línea</th>
								                    <th>Descripcion</th>
								                    <th>cIdConsolidado</th>
								                </tr>
								            </thead>
								        </table>
										</td>						
								</tr>
								<tr height="100 px" VALIGN=TOP>												
								<td colspan="3">					
											<button id="btnLineaspreselTodas">
												Seleccionar Todo
											</button> 
								</td>						
								</tr>
								</table>									
								</div>	
							</td></tr></table>
							</td>
						</tr>
						</table>
					</div>
				<!--PARTIDAS  -->
			<div id="tabs-3">	
			<!--CONSOLIDACION  -->	
			<table border="0" width="100%">
				<tr align="right">																							
					<td colspan="3">&nbsp;&nbsp;&nbsp;&nbsp;<img id="imgPlayStop" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="aprobar()" />&nbsp;Aprobar 
									<img id="imgPlayStop" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer" />&nbsp;Devolver
									<img id="imgPlayStop" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="anular();"/>&nbsp;Anular
									<img id="imgPlayStop" src="../imagenes/cancel.png" style="cursor: pointer" onclick="salir();"/>&nbsp;Salir</td>
				</tr>
				<tr align="left">
					<td colspan="3">[[<input name="cIdConsolidadoPar" id="cIdConsolidadoPar" type="text" size ="10" style="border: 0px solid black;text-align: center"></input>]]&nbsp;<input name="desConsolidadoPar" id="desConsolidadoPar" type="text" size ="50" style="border: 0px solid black;"></input></td>
				</tr>
				
				<tr>
					<td align="left" colspan="3">	
							&nbsp;<input name="cEstadoPar" id="cEstadoPar" type="text" size ="15" style="border: 0px solid black;">
					</td>								
				</tr>
				<tr>
							<td align="left" colspan="3">
									<button id="btnPartidas">Partidas</button>
													&nbsp;&nbsp;&nbsp;&nbsp;											
									<button id="btnPartidasResumen">Resumen	</button>   									
							</td>
				 	</tr>
			</table>
			<div id="tblconsolidado" style="display: block">	
			<table border="0" width="100%">
								<tr align="right">
									<td colspan="3">&nbsp; 
									</td>																	
								</tr>
								
								
								<tr>
								<td colspan="3">&nbsp;
								</td>
								</tr>
								
								<tr>																
									<td align="left" colspan="3">
									<div id="tbcambs" style='display:block'>
									<table><tr><td>
									CABMS:&nbsp;&nbsp;	
										<select id="cucopDisponible" name="cucopDisponible" style="width: 40em;">										
											</select>
									</td></tr></table>		
										</div>	
										<div id="tbcambsSig" style='display:none'>
									<table><tr><td>
									
									
									
									CABMS:&nbsp;&nbsp;<br/>	
										<input name="cambs_desc" id="cambs_desc" type="text" size ="100" style="border: 0px solid black;">	
									</td></tr></table>		
										</div>
											
									</td>								
								</tr>
								<tr>
									<td align="left" colspan="3">
										&nbsp;
									</td>									
								</tr>
	
								<tr>
									<td colspan="3">
									<table id="tblCucops" class="display">
								            <thead>
								                <tr>
								                	<th>Solicitud</th>
								                    <th>Línea</th>
								                    <th>Descripcion</th>
								                    <th>U.M</th>
								                    <th>Cantidad</th>
								                </tr>
								            </thead>
								        </table>
									</td>									
								</tr>
	
								<tr>
									<td align="left" colspan="1">
										Lineas Totales  &nbsp;&nbsp; <input name="numLinTot" id="numLinTot" type="text" size ="4" style="border: 0px solid black;">																
									</td>
									<td id="mensajePartida" name="mensajePartida" style="FONT-SIZE: 12pt; Color: red" style="visibility: hidden" align="center" colspan="2">
													&nbsp;  La Partida <input name="numLinTotPartida" id="numLinTotPartida" type="text" size ="2" style="border: 0px solid black;color: red;text-align: center;">  se agregó correctamente 
									</td>		
									
								</tr>
								<tr>
									<td align="left">
										<button id="btnCucopspreselTodas">
												Seleccionar Todo
											</button> 
									</td>
									<td colspan="2">
									<button id="btnCucopsAgregar">
												Agregar
											</button> 
									
									</td>
									
								</tr>
								<tr>
									<td align="left">
										Descripción: 
									</td>
									<td colspan="2" align="left">
										<textarea name="DescripcionPartida"  id="DescripcionPartida" cols="60" rows="5" style="text-align: left"></textarea><br/><label class="validateTips ui-state-error"></label>
									</td>
								</tr>
								<tr>									
									<td colspan="3" align="left"> 
										<button id="btnPartidaGuardar">
												Guardar
											</button> 
									</td>
								</tr>
								<tr>
									<td align="left" colspan="3">
										Número de Partida:&nbsp;&nbsp;<input name="numPartida" id="numPartida" type="text" size ="2" style="border: 0px solid black;color: blue;text-align: center;font-size: 16pt; background-color: gray;">
										<input id="nIdLinea" name="nIdLinea" type="hidden" size="4">
									</td>
								</tr>
								<tr>
									<td align="left" colspan="3">&nbsp;&nbsp;&nbsp;<button id="btnPartidaAnterior"><<</button>
																 &nbsp;&nbsp;&nbsp;<button id="btnPartidaSiguiente">>></button>
									</td>
									
								</tr>															
							</table>
			</div>
			<!--RESUMEN  -->	
			<div id="tblresumen" style="display: none">					
			<table border="0" width="100%">
								<tr align="right">
									<td colspan="3">&nbsp; 
									</td>																	
								</tr>
								<tr>
									<td align="left" colspan="3">
										&nbsp;
									</td>									
								</tr>
	
								<tr>
									<td colspan="3">
									<table id="tblResumenpartidas" class="display">
								            <thead>
								                <tr>
								                	<th>Descripcion</th>
								                    <th>CABM</th>
								                    <th>Partida</th>
								                    <th>L.S</th>
								                    <th>Total Linea Bruto</th>
								                    <th>Unidades</th>
								                    <th>Total Linea</th>
								                    <th>Elimina</th>								                    
								                </tr>
								            </thead>
								        </table>
									</td>									
								</tr>
								<tr>
									<td align="left" colspan="3">
										&nbsp;
									</td>									
								</tr>
								<tr>
									<td align="left" >
										&nbsp;&nbsp;&nbsp;<button id="btnPartidaModificar">Modificar</button>
									</td>	
									<td align="left" >
										Partida:<input name="numPartidaRes" id="numPartidaRes" type="text" size ="4" style="border: 0px solid black;">
									</td>	
									<td align="left">
										<input name="descripcion_partidaRes" id="descripcion_partidaRes" type="text" size ="50">	
									</td>																	
								</tr>
								<tr>
									<td colspan="3">
									<table id="tblResumenLineasPartidas" class="display">
								            <thead>
								                <tr>
								                	<th>Solicitud</th>
								                    <th>Linea</th>
								                    <th>Partida</th>
								                    <th>Descripcion</th>
								                    <th>Cantidad</th>
								                    <th>Precio U.</th>
								                    <th>Sub Total</th>
								                    <th>Elimina</th>								                    
								                </tr>
								            </thead>
								        </table>
									</td>									
								</tr>
	
								
								
								
																						
							</table>
							</div>
			</div>
				<!-- Consulta -->
				<div id="tabs-4">
				
				<table border="0" width="100%">
				    	<tr>
				    		<td>
				    			<table align="left">
							   		<tr id="trArea" name="trArea" >
										<td>Área:</td>
										<td>
											<select id="cIdUnidadEjecutoraCon" name="cIdUnidadEjecutoraCon"style="width: 40em;">
											<option value="<%=usuario.getU_UR()%>" selected="selected">
											</select>	
													</td>
									</tr>
									<tr id="trTipoCons" name="trTipoCons" >
										<td>Tipo de Consolidado:</td>
										<td>
											<select id="cIdTipoConsolidadoCon" name="cIdTipoConsolidadoCon" style="width: 40em;"></select>	
										</td>
									</tr>
									<tr id="trAlcance" name="trAlcance" >
										<td>Alcance:</td>
										<td>
											<select id="nIdAlcanceCon" name="nIdAlcanceCon"style="width: 40em;"></select>	
										</td>
									</tr>
									<tr id="trNumero" name="trNumero" > 
										<td>Número:</td>
										<td>
										   <input type="text" name="cIdConsolidadoCon" id="cIdConsolidadoCon" style="width: 40em;" />	
										</td>
									</tr>
									<tr id="trEstado" name="trEstado" >
										<td>Estado:</td>
										<td>
											<select id="nIdEstadoCon" name="nIdEstadoCon" style="width: 40em;"></select>	
										</td>
									</tr>	
									<tr id="trDescripcionCon" name="trDescripcionCon" >
										<td>Descripción:</td>
										<td>
										   <input type="text" name="cDescripcionCon" id="cDescripcionCon" style="width: 40em;" />	
										</td>
									</tr>
							    	<tr>
							    		<td colspan="2" align="center"><input type="button"  name="btnBuscar" id="btnBuscar" value="Buscar" onclick="mostrar();" /></td>
							    	</tr>
							    </table>
				    		</td>
				    	</tr>
				    	<tr>
				    		<td width="700px" colspan="3">
				    		
				    		<div id="demo">
				    		
							    <table id="tblConsultaConsolidados" class="display">
						        	<thead>
						        		<tr> 
						        			<th></th>
						        			<th></th>
						        			<th></th>
						        			<th>CONSOLIDADO</th>
						        			<th>ALCANCE</th>
						        			<th>ESTADO</th>
						        			<th>TIPO</th> 
						        			<th>MONTO BRUTO</th>
						        			<th>MONTO C/IVA</th>
						        			<th>DESCRIPCION</th>
						        			<th>PARTIDAS</th>
						        			<th>PROC.</th>
						        			<th>P/C</th>						        			
						        		</tr>
						        	</thead>
						        </table>
						        
						        </div>
				    		</td>
				    	</tr>
				    	<tr>
				    		<td>
							    <table align="left" width="80%">
						        	<tr>
						        		<td></td>
						        	</tr>
						        	<tr>
						        		<td><input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuario.getU_UR() %>"/></td>
						        	</tr>
						        </table>
				    		</td>
				    	</tr>
				    </table>
			    </div>
			    <!-- Consolidado Automatico -->
							<div id="tabs-5" align="center">
										<table border="0" width="100%">
											<tr>
												<td align="left" colspan="3">
													*El consolidado automatico crea un consolidado a partir de una sola requisición y 
													se aprueba automaticamente.<br>
													*Crea una partida por cada l&iacute;nea de requisici&oacute;n. 
													es útil en la elaboración de consolidados de una sola l&iacute;nea 
													como en las requisiciones de servicios
												</td>												
											</tr>
											<tr>
												<td align="right">
													Unidad R.M.:
												</td>
												<td>
												
												
												
												
												
												<select id="unidadEjecutoraCA" name="unidadEjecutoraCA" style="width: 40em;">
													<option value="<%=usuario.getU_UR()%>" selected="selected"></option>
													 
													</select>
													
													
												</td>
											</tr>
											<tr>
												<td align="right">
													Tipo de Consolidado:
												</td>
												<td>
													<select id="tipoConsolidadoCA" name="tipoConsolidadoCA" style="width: 40em;">
													</select>
												</td>
											</tr>
											
											<tr>
												<td align="right">
													Alcance:
												</td>
												<td>
													<select id="AlcanceCA" name="AlcanceCA" style="width: 40em;">
													</select>
												</td>
											</tr>
											
											<tr>
												<td align="right">
													Requisición:
												</td>
												<td>
													<select id="cIdSolicitudCA" name="cIdSolicitudCA" style="width: 40em;">
													</select>
												</td>
											</tr>
											<tr>
												<td align="right">
													Descripción:
												</td>
												<td>
													<textarea name="cDescripcionCA" rows="512" id="cDescripcionCA" style='text-transform: uppercase; height: 111px; width: 545px'></textarea>
												</td>
											</tr>
											<tr>
												<td>
													&nbsp;
												</td>
												<td>
													<button id="btnGuardarConsolidadoAutomatico">
														Guardar
													</button>
													&nbsp;&nbsp;&nbsp;
												</td>
											</tr>
											<tr>
												<td>
													&nbsp;
												</td>
												<td id="mensajeCA" name="mensajeCA" style="FONT-SIZE: 12pt; Color: red" style="visibility: hidden" align="center">
													<img id="imgPlayStop" src="../imagenes/wait24trans.gif">&nbsp;Creando Consolidado Autom&aacute;tico...
												</td>
											</tr>
										</table>
						</div>
			
			</div>
			</td>
			</tr>
			</table>
		</div>
	</form>				
</body>
</html>