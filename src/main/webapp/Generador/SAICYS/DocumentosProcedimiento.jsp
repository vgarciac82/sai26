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
	String cIdTipoProcedimiento= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	
	if (session.getAttribute(GestionInterface.ATT_ProEjercicio) != null) {
		cEjercicio =(String)session.getAttribute(GestionInterface.ATT_ProEjercicio);
		cIdUnidadEjecutora= (String)session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora);
		cIdTipoProcedimiento=(String)session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento);
		nIdConsecutivo=(String)session.getAttribute(GestionInterface.ATT_ProConsecutivo);
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Procedimiento</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		
		<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
			$("#tbs").val(6);
			showAndHideTabs();
		<%
				
			NegativaPestana NegPestana=new NegativaPestana();
			//NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
			//botones
			
			NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
			///// botones/////
			int imgAdjudicarC=0;
			int imgDesiertoC=0;
			int imgDevolverC=0;
			Iterator it1 = rol.entrySet().iterator();
			Role role = new Role();
			while (it1.hasNext()) {
				Map.Entry r = (Map.Entry)it1.next();
				roles += r.getKey().toString()+",";
			}
			if(roles.length()>0){
				roles = roles.substring(0,roles.length()-1);
			}
			Map botones=nb.getBotones(roles,"Procedimiento","DocumentosProcedimiento");
			Iterator btn = botones.entrySet().iterator();
			while (btn.hasNext()) {
				Map.Entry b = (Map.Entry)btn.next();%>
				$("#<%=b.getValue()%>").attr("disabled", true);<%
				String img=(String) b.getValue();
				
				
			}
		%>	
		$("#usuarioLogin").val('<%=usuario.getLogin()%>');
		$("#cIdEntidadContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
		$("#cIdUsuario").val('<%=usuario.getLogin()%>');
		$("#usuarioRoleProcedimiento").val('<%=roles%>');
		
		
		queryFormPost("llenaCaratulaProcedimiento",{async:false});
		queryFormPost("mValidaPrecompromiso",{async:false});
		queryFormPost("apartadoConsolidado",{async:false});
	 	habilitarPestanas();
	 	
		$("#tblProveedoresDocumentos").dataTable({
				    bPaginate: true,
		 			bLengthChange: false,
		 			bFilter: true,
		 			bSort: false,
		 			bInfo: false,
		 			bAutoWidth: true,
		 			sScrollY : "100%",
						sScrollX: "200%",
						sScrollXInner: "100%",
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
						sInfoFiltered: "(filtrado de _MAX_ registros)",
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
	 	
	 	$("#tblDocumentos").dataTable({
				    bPaginate: true,
        			bLengthChange: false,
        			bFilter: true,
        			//bSort: false,
        			bInfo: false,
        			bAutoWidth: true,
        			sScrollY : "100%",
					sScrollX: "200%",
					sScrollXInner: "100%",
				  	bJQueryUI: true,
					bRetrive : true,
					bDestroy : true,
					sPaginationType: "full_numbers",
					oLanguage: {
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtrado de _MAX_ registros)",
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
       	mostrarTablaDocumentos();
       	$("#btnGuardarDocumentoProcedimiento").button().click(function(){
				if(validaModificarProcedimiento()){
					try{
						$("#nIdProcedimiento").val($("#cIdTipoProcedimiento").val()+"-"+$("#cIdUnidadEjecutora").val()+"-"+$("#nIdConsecutivo").val());
						queryFormPost("deleteDocumentosProveedor",{async:false});
						var vDocumentos=new Array();
						$('input[name=documentos]').each(function(){
	  						if (this.checked){
	  							$("#cDescripcionDocumento").val($("#cDocumento_"+($(this).val())).val());
	    						$("#cIdDocumento").val($(this).val());
	    						if($("#documentosPresentado"+$(this).val()).is(":checked"))
		    						$("#cPresentado").val("1");
		    					else
		    						$("#cPresentado").val("0");
	    						queryFormPost("mDocumentosProveedorInsert", {async : false});
	 					 	}
						});
						alert("Los datos se guardaron correctamente");
					}
					catch(e){
						alert("Error al guardar los datos");
					}
				}
		});
	 	
	 	$("#btnSeleccionarTodoDocumentos").button().click(function(){
			var inputTablaFechasProcedimiento = $('input','#divTblDocumentos');

			for(var i=0;i<inputTablaFechasProcedimiento.length;i++){
				if(inputTablaFechasProcedimiento[i].type.toUpperCase() == "CHECK" || inputTablaFechasProcedimiento[i].type.toUpperCase() == "CHECKBOX"){
					if(inputTablaFechasProcedimiento[i].id.indexOf("documento") >= 0){
						$("#"+inputTablaFechasProcedimiento[i].id).attr("checked",true);
					}
				}
			}
		});

		$("#btnSeleccionarDocumentosRequeridos").button().click(function(){
			var inputTablaFechasProcedimiento = $('input','#divTblDocumentos');

			for(var i=0;i<inputTablaFechasProcedimiento.length;i++){
				if(inputTablaFechasProcedimiento[i].type.toUpperCase() == "CHECK" || inputTablaFechasProcedimiento[i].type.toUpperCase() == "CHECKBOX"){
					if(inputTablaFechasProcedimiento[i].id.indexOf("documentosPresentado") < 0){
						$("#"+inputTablaFechasProcedimiento[i].id).attr("checked",true);
					}
				}
			}
		});

		$("#btnDeseleccionarTodoDocumentos").button().click(function(){
			var inputTablaFechasProcedimiento = $('input','#divTblDocumentos');

			for(var i=0;i<inputTablaFechasProcedimiento.length;i++){
				if(inputTablaFechasProcedimiento[i].type.toUpperCase() == "CHECK" || inputTablaFechasProcedimiento[i].type.toUpperCase() == "CHECKBOX"){
					if(inputTablaFechasProcedimiento[i].id.indexOf("documento") >= 0){
						$("#"+inputTablaFechasProcedimiento[i].id).attr("checked",false);
					}
				}
			}
		});
		
		$('#tblProveedoresDocumentos tr').live('click', function() { 
			var aTrs = $('#tblProveedoresDocumentos').dataTable().fnGetNodes();

			for ( var i=aTrs.length ; i>=0; i-- ){  
				if ( $(aTrs[i]).hasClass('row_selected') ){
					var nTr = $('#tblProveedoresDocumentos').dataTable().fnGetData(aTrs[i]);
					$("#cidRFCOculto").val(nTr[0]);
				}
			}

			$('#divTblDocumentos').css("display","");
			$('#tblDocumentos').dataTable({         
				bAutoWidth : true,
				bDestroy: true,		
				iDisplayLength: 25,
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
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mDocumentosProveedor('"+$("#cIdTipoProcedimiento").val()+"-"+$("#cIdUnidadEjecutora").val()+"-"+$("#nIdConsecutivo").val()+"','"+$("#cidRFCOculto").val()+"')",
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cReferencia" },
					{ sName: "checkbox" },
					{ sName: "checkbox2" },
					{ sName: "descripcion" }
				]
			});
			
		});
	});
	
	function validaModificarProcedimiento(){
		queryFormPost("mUsuarioMismaUE", {async: false   });
		if(($('#cIdUsuarioCreacion').val()== $("#usuarioLogin").val())||( $('#usuarioRoleProcedimiento').val().toString().indexOf('ADMIN_RECMAT') >= 0
			|| $('#usuarioRoleProcedimiento').val().toString().indexOf('ANALISTA') >= 0|| $('#usuarioRoleProcedimiento').val().toString().indexOf('JEFE') >= 0
			 ||parseInt($("#usuariosMismaUE").val(),10)==1) ){
			return true;
		}
		else{
			alert("El usuario no tiene permiso para realizar esta acci\xF3n.");
			return false;
		}
	}
	
	function muestraInformacionCaratula (){
		 //query para llenar la informacion de la caratula.
		   queryFormPost("llenaCaratulaProcedimiento",{async:false});
		   $("#caratulaAnterior").val($("#cboCategoriaCaratula").val());
		   if($("#tipoProcedimiento").val()=='PS'){
		   	 $("#esServicio").val(1);
		   }else{
		   	 $("#esServicio").val(0);
		   }
		   queryInnerDivPost("llenaFechasProcedimientoCaratula",{async:false});
	}
	
	function mostrarTablaDocumentos(){
        	//Mostramos la tabla de procedimientos
			$("#tblProveedoresDocumentos").css("display", "");
			//$("#divTblDocumentos").css("display", "none"); 
			$("#tblProveedoresDocumentos tbody").click(function(event) {
					$(oTableProveedoresDocumentos.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
				});
			
			//Parametros de busqueda opcionales
			var consulta="";
			var cIdProcedimiento =$("#cIdTipoProcedimiento").val()+"-"+$("#cIdUnidadEjecutora").val()+"-"+$("#nIdConsecutivo").val();
			$("#cIdProcedimiento").val(cIdProcedimiento);
			//Tabla Proveedores Documentos
			oTableProveedoresDocumentos=$('#tblProveedoresDocumentos').dataTable({
			"bProcessing": true,
			"bLengthChange" : true,//Habilitar el que dice mostrar 10, 25 etc
			"bFilter" : false,
			"bDestroy" : true,
			"bJQueryUI": true,
			"bAutoWidth" : false,
			"iDisplayLength": 5,
			"sPaginationType": "full_numbers",
			//"sScrollY": 250,
					"oLanguage": {
					sProcessing: "Procesando...",
					//sLengthMenu: "Mostrar _MENU_ registros",
					sLengthMenu: "<h2><b>PROVEEDORES ADJUDICADOS</b></h2><h5>Click para seleccionar proveedor</h5>",
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
				"bServerSide": true,
				"sAjaxSource": window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mProveedoresProcedimiento('"+cIdProcedimiento+"')",
				"aaSorting": [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "rfcDocumento" },
					{ sName: "razonSocialDocumento"  },
					{ sName: "cEstadoProveedor"  }
				]
			});
			
			$('#tblDocumentos').dataTable({         
				bAutoWidth : true,
				bDestroy: true,		
				iDisplayLength: 25,
				oLanguage: {
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
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[0, "asc" ]] ,
				aoColumns: [
					{ sName: "cReferencia" },
					{ sName: "checkbox" },
					{ sName: "checkbox2" },
					{ sName: "descripcion" }
				]
			});
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
						//$("#presupuestoProcedimiento" ).css("display", "block");
						//$("#precompromisoProcedimiento" ).css("display", "block");
						$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
						
						// si aun no hay partidas en adjudicacionPartidas, no se muestran las pestañas
						queryFormPost("validaPartidasAdjudicadas",{async:false});
						if($("#partidasAdjudicadas").val()=="0"){ 
							$("#presupuestoProcedimiento" ).css("display", "none");
							$("#precompromisoProcedimiento" ).css("display", "none");	
						}else{
							 $("#presupuestoProcedimiento" ).css("display", "block");
							$("#precompromisoProcedimiento" ).css("display", "block");
							//si el usuario logeado es administrador y no es dueño no se muestra
						    /* if ($("#usuarioRoleProcedimiento").val().toString().indexOf("ADMIN_RECMAT")>=0){
								if($("#cIdUsuarioCreacion").val()==$("#usuarioLogin").val()){
									$("#presupuestoProcedimiento").css("display", "block");
									$("#precompromisoProcedimiento").css("display", "block");
								}else{
									$("#presupuestoProcedimiento").css("display", "none");
									$("#precompromisoProcedimiento").css("display", "none");
								}
							}else{
								$("#presupuestoProcedimiento").css("display", "block");
								$("#precompromisoProcedimiento").css("display", "block");
							} */
						}
							
					}else{
						if(cubreMonto()){
							$("#presupuestoProcedimiento" ).css("display", "block");
							$("#precompromisoProcedimiento" ).css("display", "block");
						}else{
							$("#presupuestoProcedimiento" ).css("display", "none");
							$("#precompromisoProcedimiento" ).css("display", "none");
						}
					}
				}else{
					if($("#nIdEstado").val()==2){
						queryFormPost("validaFlujoProcedimiento", {async:false});
						if($("#cEventoFlujo").val()=="DISP_PRECOMMAT" || $("#cEventoFlujo").val()=="PRECOM_MAT"){
							$("#presupuestoProcedimiento" ).css("display", "block");
							$("#precompromisoProcedimiento" ).css("display", "block"); 
							$("#AmpliacionVigenciaProcedimiento" ).css("display", "block");
						}else{
							$("#presupuestoProcedimiento" ).css("display", "none");
							$("#precompromisoProcedimiento" ).css("display", "none");
							$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
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
						if($("#cEventoFlujo").val()=="DISP_PRECOMMAT" || $("#cEventoFlujo").val()=="PRECOM_MAT"){
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
	
	
	function cubreMonto(){
		queryFormPost("fn_mConsolidadoCalculaMontoConIVARead", {async:false});
		//if(parseInt(quitaFmt($("#lblApartado").val()))<quitaFmt($("#MontoConIVA").val())){
		if(parseInt(quitaFmt($("#lblApartado").val()),10)<parseInt(quitaFmt($("#MontoConIVA").val()),10)){			
			return true;
		}else{
			return false;
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
	
	function deshabilitaDocumentoPresentado(obj,cIdDocumento){
		if(!obj.checked){
			$("#documentosPresentado"+cIdDocumento).attr('checked',false);
		}
    }
    
    function validaCheckDocumentoRequerido(cIdDocumento){
		if(!$("#documentos"+cIdDocumento).is(":checked")){
			if($("#documentosPresentado"+cIdDocumento).is(":checked")){
				alert("Para seleccionar un documento presentado debe seleccionarlo primero como requerido.");
				$("#documentosPresentado"+cIdDocumento).attr('checked',false);
			}
		}
    }
//----
	</script>
	</head>
	<body id="dt_example" >
		<form>
		<fieldset>
						<table border="0" >
					    	<tr>
					    		<td  align="center">
					    			<div id="demo">
					    				<table id="tblProveedoresDocumentos" class="display" >
								        	<thead>
								        		<tr align="center">
								        			<th style="width:200px;">RFC</th>
								        			<th style="width:270px;">Raz&oacute;n Social</th>
								        			<th style="width:270px;">Habilitar/Deshabilitar</th>						        			
								        		</tr>
								        	</thead>
								        </table>
								        <br>
								        <div style="">
								        	<input type="button" name="btnSeleccionarDocumentosRequeridos" id="btnSeleccionarDocumentosRequeridos" value="Seleccionar Requeridos" class="btnInterfaceBG ui-button ui-corner-all"/>
								        	<input type="button" name="btnSeleccionarTodoDocumentos" id="btnSeleccionarTodoDocumentos" value="Seleccionar Todo" class="btnInterfaceBG ui-button ui-corner-all"/>
								        	<input type="button" name="btnDeseleccionarTodoDocumentos" id="btnDeseleccionarTodoDocumentos" value="Deseleccionar Todo" class="btnInterfaceBG ui-button ui-corner-all"/>
								        </div>
								         <br>
								        <div id="divTblDocumentos">
										    <table id="tblDocumentos" class="display" >
									        	<thead>
									        		<tr align="center"> 
									        			<th>Referencia</th>
									        			<th>Requerido</th>
									        			<th>Presentado</th>
									        			<th>Descripción</th>
									        		</tr>
									        	</thead>
									        </table>
									        <br>
									        <table>
										    	<tr>
										    		<td>
													    <table align="left">
												        	<tr>
												        		<td align="center">
												        			<input type="button" name="btnGuardarDocumentoProcedimiento" id="btnGuardarDocumentoProcedimiento" value="Guardar" class="btnInterfaceBG ui-button ui-corner-all"/>
												        		</td>
												        	</tr>	
												        </table>
										    		</td>
										    	</tr>
										    </table>
								        </div>
									</div>			        
					    		</td>
					    	</tr>
					   	</table>
					</fieldset>		
					
		<input name="lblApartado" id="lblApartado" type="hidden" size ="10" ></input>
		<input id="cEjercicio" name="cEjercicio" type="hidden" size="4" value="<%=cEjercicio %>">
		<input id="cIdTipoProcedimiento" name="cIdTipoProcedimiento" type="hidden" size="4" value="<%=cIdTipoProcedimiento %>">
		<input id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden" size="3" value="<%=cIdUnidadEjecutora %>">
		<input id="nIdConsecutivo" name="nIdConsecutivo" type="hidden" size="4" value="<%=nIdConsecutivo %>">
		<input id="cIdUsuario" name="cIdUsuario" value="" type="hidden" />	
			
		<input id="usuarioLogin" name="usuarioLogin" type="hidden" size="10">
		<input id="cIdEntidadContable" name="cIdEntidadContable" type="hidden" size="10">
		<input id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" type="hidden" size="10">
		<input id="usuarioRoleProcedimiento" name="usuarioRoleProcedimiento" value=""  type="hidden" size="10">
		
		<input id="tipoProceso" name="tipoProceso" type="hidden" size="4">
		<input id="cIdProcedimiento" name="cIdProcedimiento" type="hidden" size="4">
		<input id="nIdEstado" name="nIdEstado" type="hidden" size="4">	
		<input id="documentoAplicado" name="documentoAplicado" type="hidden" size="4">	
		<input id="existePrecompromiso" name="existePrecompromiso" type="hidden" size="10">
		<input id="partidasAdjudicadas" name="partidasAdjudicadas" type="hidden" size="10">
		<input type="hidden" name="cidRFCOculto" id="cidRFCOculto" />
		<input type="hidden" name="nIdProcedimiento" id="nIdProcedimiento" />
		<input type="hidden" name="cIdDocumento" id="cIdDocumento" />
		<input type="hidden" name="cPresentado" id="cPresentado" />
		<input type="hidden" name="cDescripcionDocumento" id="cDescripcionDocumento" />
		<input type="hidden" name="cIdConsolidado" id="cIdConsolidado" />
		<input id="MontoConIVA" name="MontoConIVA" type="hidden" size="10">
		<input id="cEventoFlujo" name="cEventoFlujo" type="hidden" size="10">
		<input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE" value=""/>
		</form>				
	</body>
</html>