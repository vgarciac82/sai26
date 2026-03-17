<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	//String path = request.getContextPath();
	//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	
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
				querySelectPost("UnidadEjecutoraBuscaRead", "cUnidadEjecutoraRMC", {async : false});
				querySelectPost("mCatalogoCapituloRead", "cCapitulo", {async : false});
				querySelectPost("mCatalogoSubPartidaConsolRead", "partida", {async : true});														
				cargaSolicitudesSel();								
				cargaSolicitudesDisppresel();	
				
				queryFormPost("mConsolidado_LabelRead", { async:false });					
				queryFormPost("mConsolidado_EstadoConsolidadoRead", {async:false});
				queryFormPost("cg_roleRead", { async:false });
				if (!($("#R_NOMBRE").val() == "ADMIN_RECMAT" || $("#cIdUnidadEjecutoraUsuario").val() == $("#cIdUnidadEjecutora").val())) {
					document.getElementById("imgAprobar").disabled = true;
					document.getElementById("imgDevolver").disabled = true;
					document.getElementById("imgAnular").disabled = true;
				}
				else if ($("#nIdEstadoCon").val() == "1") { //capturada
					document.getElementById("imgAprobar").disabled = false;
					document.getElementById("imgDevolver").disabled = true;
					document.getElementById("imgAnular").disabled = false;
				}
				else if ($("#nIdEstadoCon").val() == "2") { //Aprobada
					document.getElementById("imgAprobar").disabled = true;
					document.getElementById("imgDevolver").disabled = false;
					document.getElementById("imgAnular").disabled = false;
				} 
				else if ($("#nIdEstadoCon").val() == "3") { //anulada
					document.getElementById("imgAprobar").disabled = true;
					document.getElementById("imgDevolver").disabled = true;
					document.getElementById("imgAnular").disabled = true;
				}
				
				if (!($("#R_NOMBRE").val() == "ADMIN_RECMAT" || $("#cIdUnidadEjecutoraUsuario").val() == $("#cIdUnidadEjecutora").val()))
					deshabilitarCampos();
				else if ($("#nIdEstadoCon").val() == "2" || $("#nIdEstadoCon").val() == "3") 
					deshabilitarCampos();
				
				$("#aTabA1").click(function() {
					$('#tblLineasDispPreSel').dataTable().fnClearTable();
					$('#tblLineasPreSel').dataTable().fnClearTable();
					cargaLineasPresel();
					cargaLineasDisppresel();					
				});
				$("#aTabA0").click(function() {
					window.location="Consolidado.jsp?tab=4"	
				});
				
						
				$('#tblLineasDispPreSel').dataTable().fnClearTable();
				$('#tblLineasPreSel').dataTable().fnClearTable();							
				cargaLineasPresel();
				cargaLineasDisppresel();				
				queryFormPost("numLinDispCreate", {async : false});
				queryFormPost("numLinAgregadasCreate", {async : false});				
				if ($("#numLinDisp").val() > 0 || $("#numLin").val() > 0)
					$( "#aTabA1" ).attr("disabled", false);
				else
					$( "#aTabA1" ).attr("disabled", true);
								
				$("#cCapitulo").change(function () {
					querySelectPost("mCatalogoCapituloRead", "cCapitulo", {async : false});
					querySelectPost("mCatalogoSubPartidaConsolRead", "partida", {async : true});
				});				
				
				$("#btnSolicitudesDisppreselBuscar").button().click(function(){
						$('#tblSolicitudDispPreSel').dataTable().fnClearTable();
						cargaSolicitudesDisppresel();
						/*var szWhere = ' \'1\'=\'1\' ';
						var area =$('#cUnidadEjecutoraRMC').val();						
      					var capitulo =$('#cCapitulo option:selected').val();      	
      					var partida = $('#partida option:selected').val();
      					area = $.trim(area);
      					partida = $.trim(partida);
      					
      					var idConsol = $('#cIdConsolidado').val();
						if(area   != null && area    != '' && area    != '0'){szWhere += '  and substring(cIdSubPartida,1,1) = \''+  area +'\'';}      					
				    	if(capitulo   != null && capitulo    != '' && capitulo    != '0'){szWhere += '  and substring(cIdSubPartida,1,1) = \''+  capitulo +'\'';}
				    	if(partida    != null && partida     != '' && partida     != '0' && partida     != '2' && partida     != '3' && partida     != '5'){szWhere += '  and cIdSubPartida = \''+partida+'\'';}
				    	//if(idConsol      != null && idConsol       != ''){where += '  and cIdConsolidado       = \''+idConsol+'\'';}				    	
						var campos = "";  					
						campos = $("#cAlcanceC").val()+"','"+$("#cIdTipoConsolidado").val()+"','"+$("#cUnidadEjecutoraRMC").val()+"','"+$("#cIdConsolidado").val();						
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
		        	 })*/  
				});			
				
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
						cargaSolicitudesSel()
						cargaSolicitudesDisppresel()
				});
		
				$("#btnSolicitudespreselTodas").button().click(function(){
						var aTrs = $('#tblSolicitudPreSel').dataTable().fnGetNodes();           
						for ( var i=aTrs.length-1 ; i>=0; i-- )     
						{         								            
							var nTr = $('#tblSolicitudPreSel').dataTable().fnGetData(aTrs[i]);  							
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
						
						cargaSolicitudesSel()
						cargaSolicitudesDisppresel()
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
							//$('#tblLineasPreSel').dataTable().fnAddData( nTr );
							//$('#tblLineasDispPreSel').dataTable().fnDeleteRow( i );
							var valor = nTr[3];							
							$("#cIdTipoConsolidadoL").val($.trim(valor).split("-")[0]);
							$("#cIdUnidadEjecutoraConsolidadoL").val($.trim(valor).split("-")[1]);
							$("#nIdConsecutivoConsolidadoL").val($.trim(valor).split("-")[2]);																	
							var valor2 = nTr[0]							
							$("#cIdTipoLinea").val($.trim(valor2).split("-")[0]);
							$("#cIdUnidadEjecutoraLinea").val($.trim(valor2).split("-")[1]);						
							$("#nIdConsecutivoLinea").val($.trim(valor2).split("-")[2]);
							$("#nIdLineaSolicitud").val($.trim(nTr[1]));							
							//queryFormPost("numLinDispCreate", {async : false});
							//queryFormPost("numLinAgregadasCreate", {async : false});
							queryFormPost("mConsolidadoPreseleccionLineaSolicitudCreate", {async : false});	
							if ($("#numLinDisp").val() > 0 || $("#numLin").val() > 0)
								$( "#aTabA1" ).attr("disabled", false);
							else
								$( "#aTabA1" ).attr("disabled", true);										
						}
            		    			cargaLineasDisppresel();
						cargaLineasPresel();
				});
				
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
						cucopsDisponibles();
						if ($("#numLinDisp").val() > 0 || $("#numLin").val() > 0)
							$( "#aTabA1" ).attr("disabled", false);
						else
							$( "#aTabA1" ).attr("disabled", true);
						
						cargaLineasDisppresel()
						cargaLineasPresel()
						});
							
				
				$("#cIdConsolidado").val($("#cIdTipoConsolidado").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());
				queryFormPost("mConsolidadoCaratulaRead",{async : false});									
			});
			
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
				queryFormPost("numLinDispCreate", {async : false});
				queryFormPost("numLinAgregadasCreate", {async : false});
				if ($("#numLinDisp").val() > 0 || $("#numLin").val() > 0)
					$( "#aTabA1" ).attr("disabled", false);
				else
					$( "#aTabA1" ).attr("disabled", true);
				cargaSolicitudesDisppresel();
				cargaSolicitudesSel();
			});
			
			$('#tblSolicitudPreSel tr').live('click', function() {    
				if ($("#nIdEstadoCon").val() != "2" && $("#nIdEstadoCon").val() != "3" &&
					($("#R_NOMBRE").val() == "ADMIN_RECMAT" || $("#cIdUnidadEjecutoraUsuario").val() == $("#cIdUnidadEjecutora").val())) {
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
	         				})

	         				queryFormPost("numSolDispCreate", {async : false});
							queryFormPost("numSolAgregadasCreate", {async : false});
							queryFormPost("numLinDispCreate", {async : false});
							queryFormPost("numLinAgregadasCreate", {async : false});
							if ($("#numLinDisp").val() > 0 || $("#numLin").val() > 0)
								$( "#aTabA1" ).attr("disabled", false);
							else
								$( "#aTabA1" ).attr("disabled", true);
					}
				cargaSolicitudesDisppresel();
				cargaSolicitudesSel();
				});
			
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
							var valor = nTr[3];							
							$("#cIdTipoConsolidadoL").val($.trim(valor).split("-")[0]);
							$("#cIdUnidadEjecutoraConsolidadoL").val($.trim(valor).split("-")[1]);
							$("#nIdConsecutivoConsolidadoL").val($.trim(valor).split("-")[2]);							
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
					if ($("#numLinDisp").val() > 0 || $("#numLin").val() > 0)
						$( "#aTabA1" ).attr("disabled", false);
					else
						$( "#aTabA1" ).attr("disabled", true);
					cargaLineasDisppresel()
					cargaLineasPresel()
			});
			$('#tblLineasPreSel tr').live('click', function() {    
				if ($("#nIdEstadoCon").val() != "2" && $("#nIdEstadoCon").val() != "3" &&
					($("#R_NOMBRE").val() == "ADMIN_RECMAT" || $("#cIdUnidadEjecutoraUsuario").val() == $("#cIdUnidadEjecutora").val())) {
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
						if ($("#numLinDisp").val() > 0 || $("#numLin").val() > 0)
							$( "#aTabA1" ).attr("disabled", false);
						else
							$( "#aTabA1" ).attr("disabled", true);
					}
					cargaLineasDisppresel()
					cargaLineasPresel()
				});					
			
			function cargaSolicitudesSel(){	
				$('#tblSolicitudPreSel').dataTable().fnClearTable();
				//var szWhere = "";
				var campos = $("#cIdConsolidado").val();	
				$('#tblSolicitudPreSel').dataTable(
		 		 {         
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
					 bProcessing: true,
					 sPaginationType: "full_numbers",
					 bJQueryUI: true,
					//aaSorting: [[ 1, "asc" ]] ,
					 bAutoWidth: false,
					 bServerSide: true,
					 sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mConsolidadoSolicitudesPreseleccionadas('"+campos+"')",
					 aaSorting: [[ 1, "asc" ]] ,
					 aoColumns: [
					{ sName: "cSolicitud" },
					{ sName: "cIdSubPartida"   },
					{ sName: "cDescripcion"   }
					]
				});
	            	/*var szWhere = "";
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
		         })	*/	           
		         queryFormPost("numSolDispCreate", {async : false});
				 queryFormPost("numSolAgregadasCreate", {async : false});
			}
						
			function cargaSolicitudesDisppresel(){				
				$('#tblSolicitudDispPreSel').dataTable().fnClearTable();
						var szWhere = " cIdSolicitud NOT IN( select replace (cSolicitud,' ','') as cIdSolicitud from fn_mConsolidadoSolicitudesPreseleccionadas ('" + $("#cIdConsolidado").val() + "'))";
						//var szWhere = ' \'1\'=\'1\' ';						
						var area =$('#cUnidadEjecutoraRMC').val();	
						//$('#cIdUnidadEjecutora').val($('#cUnidadEjecutoraRMC').val());   
						capitulo = '';
						partida = '';
						requisicion = '';
						var capitulo =$('#cCapitulo option:selected').val();						
      					var partida = $('#partida option:selected').val();
      					var requisicion = $('#cIdConsolidadoPresel').val();
      					//alert($('#cCapitulo option:selected').val());
      					area = $.trim(area);
      					partida = $.trim(partida);
      					requisicion= $.trim(requisicion);
      					//var idConsol = $('#cIdConsolidado').val();
						if(area   	  != null && area    	 != '' && area    	  != '0'){szWhere += '  and substring(cIdSolicitud,4,3) = \''+  area +'\'';}      					
				    	if(capitulo   != null && capitulo    != '' && capitulo    != '0'){szWhere += '  and substring(cIdSubPartida,1,1) = \''+  capitulo +'\'';}
				    	if(partida    != null && partida     != '' && partida     != '0' && partida     != '2' && partida     != '3' && partida     != '5'){szWhere += '  and cIdSubPartida = \''+partida+'\'';}
				    	if(requisicion      != null && requisicion       != ''){szWhere += '  and cIdSolicitud = \''+requisicion+'\'';}				    	
						//var campos = "";
						//var campos = $("#cAlcanceC").val()+"','"+$("#cIdTipoConsolidado").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#cIdConsolidado").val();
						var campos = $("#cAlcanceC").val()+"','"+$("#cIdTipoConsolidado").val()+"','"+$("#cUnidadEjecutoraRMC").val()+"','"+$("#cIdConsolidado").val();
						//alert(szWhere);
						//alert(campos);
						
				$("#tblSolicitudDispPreSel").dataTable(
				{         																		 
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
					 		
					 bProcessing: true,
					 sPaginationType: "full_numbers",
					 bJQueryUI: true,
					 //aaSorting: [[ 1, "asc" ]] ,
					 bAutoWidth: false,
					 bServerSide: true,
					 sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=dbo.fn_mConsolidadoPreseleccionSolicitudes('"+campos+"')&qw="+szWhere,
					 aaSorting: [[ 1, "asc" ]] ,
					 aoColumns: [
					{ sName: "cIdSolicitud" },
					{ sName: "cIdSubPartida"   },
					{ sName: "cDescripcion"   }
				]
				});
              	/*var szWhere = "";
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
	         	})*/
	         	
	         	//$('#cIdUnidadEjecutora').val($('#cUnidadEjecutoraRMC').val());
	         	if ($('#cCapitulo option:selected').val() == 0){
	         		
	         		if(requisicion != ''){	         			
	         			queryFormPost("numSolDispBuscarNumCreate", {async : false});	 	         				         			        		
	         		}else{	         				         			
	         			queryFormPost("numSolDispCreate", {async : false});
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
				$('#tblLineasDispPreSel').dataTable(
				{         									
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
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					//aaSorting: [[ 1, "asc" ]] ,
					bAutoWidth: false,
					bServerSide: true,
					 sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=dbo.fn_mConsolidadoLineasDispPresel('"+campos+"')",
					 aaSorting: [[ 1, "asc" ]] ,
					 aoColumns: [
					{ sName: "cSolicitud" },
					{ sName: "nIdLineaSolicitud"   },
					{ sName: "cDescripcion"   },
					{ sName: "cIdConsolidado"   }
				]
				});
              	/*var szWhere = "";
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
		         })*/		           
		         queryFormPost("numLinDispCreate", {async : false});
				 queryFormPost("numLinAgregadasCreate", {async : false});
				 if ($("#numLinDisp").val() > 0 || $("#numLin").val() > 0)
					$( "#aTabA1" ).attr("disabled", false);
				 else
					$( "#aTabA1" ).attr("disabled", true);
			}
				
			function cargaLineasPresel(){
				$('#tblLineasPreSel').dataTable().fnClearTable();
				//var szWhere = "";
				var campos =  $("#cIdConsolidado").val();
				 $('#tblLineasPreSel').dataTable(
				{         
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
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					//aaSorting: [[ 1, "asc" ]] ,
					bAutoWidth: false,
					 bServerSide: true,
					 sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=dbo.fn_mConsolidadoLineasSolicitudPreseleccionadas('"+campos+"')",
					 aaSorting: [[ 1, "asc" ]] ,
					 aoColumns: [
					{ sName: "cSolicitud" },
					{ sName: "nIdLineaSolicitud"   },
					{ sName: "cDescripcion"   },
					{ sName: "cIdConsolidado"   }
				]
				});
              /*	var szWhere = "";
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
	         	})*/		           
		         queryFormPost("numLinDispCreate", {async : false});
				 queryFormPost("numLinAgregadasCreate", {async : false});
				 if ($("#numLinDisp").val() > 0 || $("#numLin").val() > 0)
					$( "#aTabA1" ).attr("disabled", false);
				 else
					$( "#aTabA1" ).attr("disabled", true);
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
				queryFormPost("numLinTotalRead",{async : false});
				if ($("#numLinTot").val() > 0) {
					$("#nIdEstado").val("2"); //aprobada
					queryFormPost("mConsolidadoUpdate", { async:false });
					alert("El consolidado ha sido aprobado.");
					window.location = "Consolidado.jsp?tab=4";
				}
				else
					alert("Necesita agregar al menos una línea para aprobar el Consolidado.")
			}
		
			function devolverConsolidado () {
				queryFormPost("fn_mConsolidadoTieneProcedimientoRead", { async:false });
				if($("#tieneProcedimiento").val() == '' || $("#tieneProcedimiento").val() == 'N/A')
					$("#tieneProcedimiento").val('0');
				if ($("#tieneProcedimiento").val() == "0") {
					$("#nIdEstado").val("1"); //capturada
					queryFormPost("mConsolidadoUpdate", { async:false });
					window.location = "Consolidado.jsp?tab=4";
				}
				else
					alert("El Consolidado "+$("#cIdConsolidado").val()+" está asociado a un Procedimiento. Para devolverlo, primero es necesario eliminarlo del Procedimiento.");
			}
		
			function anularConsolidado () {
				queryFormPost("fn_mConsolidadoTieneProcedimientoRead", { async:false });
				if($("#tieneProcedimiento").val() == '' || $("#tieneProcedimiento").val() == 'N/A')
					$("#tieneProcedimiento").val('0');
				
				if ($("#tieneProcedimiento").val() == "0") {				
					if (confirm("¿Está seguro que desea anular el Consolidado "+ $("#cIdConsolidado").val() +" ? \n Esta acción no puede revertirse y todas sus lineas serán liberadas.")) {
						$("#nIdEstado").val("3"); //anulada
						queryFormPost("mConsolidadoUpdate", { async:false });
						queryFormPost("sp_mConsolidadoAnulaConsolidado", {async:false});
						window.location = "Consolidado.jsp?tab=4";
					}
				}else
					alert("El Consolidado "+$("#cIdConsolidado").val()+" está asociado a un Procedimiento. Para devolverlo, primero es necesario eliminarlo del Procedimiento.");
				
			}
		</script>
		</head>
		<body id="dt_example" >
			<form>
				<fieldset>
					<legend>Información del Consolidado</legend>
						<table align="left" cellpadding="2" width="100%">
					    	<tr>
					    		<td align="right" colspan="2">
		                                 	<img id="imgAprobar" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="aprobarConsolidado();"/> Aprobar 
		                                     <img id="imgDevolver" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer" onclick="devolverConsolidado();" /> Devolver
		                                     <img id="imgAnular" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="anularConsolidado();" /> Anular
		                                     <img id="imgSalir" src="../imagenes/cancel.png" style="cursor: pointer"  onclick="window.location = 'Consolidado.jsp?tab=2&ses=0';"/> Salir
		                        </td>
					    	</tr>
					    	<tr>
						    	<td align="left" colspan="2"><input type="text" style="width: 700px" id="lblConsolidado" name="lblConsolidado" readonly style="border-width:0; background-color:transparent"/></td>
					    	</tr>
					    	<tr>
					    		<td align="left" colspan="2"><input type="text" style="width: 700px" name="lblDescripcion" id="lblDescripcion" readonly style="border-width:0; background-color:transparent"/></td>
					    	</tr>
					    	<tr>
					    		<td align="left" colspan="2"><input type="text" style="width: 700px" name="lblUnidadEjecutora" id="lblUnidadEjecutora" readonly style="border-width:0; background-color:transparent"/></td>
					    	</tr>
					    	<tr>
					    		<td align="left" colspan="2"><input type="text" style="width: 700px" name="lblEstado" id="lblEstado" readonly style="border-width:0; background-color:transparent"/></td>
					    	</tr>
					    </table>						
						<table border="0" align="left" width="100%">	
						<tr>
							<td>
								<input id="cEjercicio" name="cEjercicio" type="hidden" size="4" value="<%= cEjercicio %>">
								<input id="cIdTipoConsolidado" name="cIdTipoConsolidado" type="hidden" size="4" value="<%= cIdTipoConsolidado %>">
								<input id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden" size="3" value="<%= cIdUnidadEjecutora %>">
								<input id="nIdConsecutivo" name="nIdConsecutivo" type="hidden" size="4" value="<%= nIdConsecutivo %>">
								
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
								<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%=usuario.getU_UR()%>" />
								<input type="hidden" name="cIdUnidadEjecutoraConsolidado" id="cIdUnidadEjecutoraConsolidado" value="<%=cIdUnidadEjecutora%>" />
								<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
								<input type="hidden" name="R_NOMBRE" id="R_NOMBRE" />																						
								<input name="tieneProcedimiento" id="tieneProcedimiento" type="hidden">
								<input name="numLinTot" id="numLinTot" type="hidden">
								<input name="str_capitulo" id="str_capitulo" type="hidden">
								<input name="str_partida" id="str_partida" type="hidden">
								
								
							</td>
						</tr>
					</table>
				</fieldset>				
				<fieldset>
					<legend>Requisiciones y L&iacute;neas</legend>
					<div class="tabs" id="tabsA" name="tabsA" >
						<ul>
							<li><a id="aTabA0" href="#tabsA-0">Requisiciones</a></li>
							<li><a id="aTabA1" href="#tabsA-1">L&iacute;neas</a></li>
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
															<td >&Aacute;rea:</td>										
															<td colspan="2" align="left">
																<select id="cUnidadEjecutoraRMC" name="cUnidadEjecutoraRMC" style="width: 40em;">
																<option value="<%=usuario.getU_UR()%>" selected="selected"></select>
															</td>
														</tr>
														<tr>
															<td>C&aacute;pitulo:</td>										
															<td colspan="2" align="left">
																<select id="cCapitulo" name="cCapitulo"	style="width: 40em;"></select>
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
																<input name="cIdConsolidadoPresel" id="cIdConsolidadoPresel" type="text" size ="70" >
															</td>
														</tr>
														<tr>																			
															<td colspan="3" align="left">
																<button id="btnSolicitudesDisppreselBuscar">Buscar</button>
															</td>
														</tr>
												</table>		
											</td>
										</tr>					
									</table>
							</fieldset>
							<fieldset>
								<legend>Requisiciones Disponibles para Pre-selecci&oacute;n <input name="numSolDisp" id="numSolDisp" type="text" size ="4" style="border: 0px solid black;"></legend>
								<table border="0" align="left" >
									<tr>
										<td colspan="3">
											<table id="tblSolicitudDispPreSel" class="display">
									            <thead>
									                <tr>
									                	<th>Requisición</th>
									                    <th>Partida</th>
									                    <th>Descripcion</th>
									                </tr>
									            </thead>
									        </table>
										</td>						
									</tr>
									<tr>														
										<td colspan="2" align="left">					
													<button id="btnSolicitudesDisppreselTodas">Seleccionar Todo</button> 
										</td>						
									</tr>
								</table>
							</fieldset>
							</div>
							<fieldset>
								<legend>Requisiciones Pre-seleccionadas <input name="numSol" id="numSol" type="text" size ="4" style="border: 0px solid black;"></legend>
								<table border="0" align="left" >
									<tr>
										<td colspan="3">
											<table id="tblSolicitudPreSel" class="display" >
									            <thead>
									                <tr>
									                	<th>Requisición</th>
									                    <th>Partida</th>
									                    <th>Descripcion</th>
									                </tr>
									            </thead>
									        </table>
										</td>						
									</tr>
									<tr VALIGN=TOP>												
										<td colspan="3" align="left">					
											<button id="btnSolicitudespreselTodas">Seleccionar Todo</button> 
										</td>						
									</tr>
								</table>
							</fieldset>
						</div>
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
															<th>Requisición</th>
															<th>Línea</th>
															<th>Descripcion</th>
															<th>cIdConsolidado</th>
														</tr>
													</thead>
												</table>
											</td>
										</tr>
										<tr>
											<td align="left">
												<button id="btnLineasDisppreselTodas">Seleccionar Todo</button> 
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
									                	<th>Requisici&oacute;n</th>
									                    <th>L&iacute;nea</th>
									                    <th>Descripci&oacute;n</th>
									                    <th>Consolidado</th>
									                </tr>
									            </thead>
									        </table>
										</td>
									</tr>
									<tr>
										<td align="left">														
											<button id="btnLineaspreselTodas">Seleccionar Todo</button> 
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
